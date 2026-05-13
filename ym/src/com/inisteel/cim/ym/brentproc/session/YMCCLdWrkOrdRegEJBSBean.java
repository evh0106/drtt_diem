package com.inisteel.cim.ym.brentproc.session;

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
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackColDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.jms.model.dm.*;
import com.inisteel.cim.common.jms.model.po.*;
import com.inisteel.cim.common.jms.model.ps.*;
import com.inisteel.cim.common.jms.model.pc.*;
import com.inisteel.cim.common.util.*;
//import com.inisteel.cim.common.util.CodeUtil;

/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="YMCCLdWrkOrdRegEJB" jndi-name="JNDIYMCCLdWrkOrdReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class YMCCLdWrkOrdRegEJBSBean extends BaseSessionBean {
	private YmComm ymComm = new YmComm();
	
	private YmCommDAO commDao = new YmCommDAO();
	
	private Logger logger 			= null;
	JDTORecord tcRecord = JDTORecordFactory.getInstance().create();
	
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger 		= new Logger(config);
	}

     
	
	

   /**
     * 대차 도착시 From 위치와 To 위치 정보를 실적 처리한다.
     *
     * @param String	: 대차 No
     * @param String	: From LOC
     * @param String	: To LOC
     * @param String	: 스케쥴 코드
     * @param String	: 야드구분
     *
     */ 
	private int insertUpPutWrslRtData(String sStockId, 
									  String sUpLoc, 
									  String sPutLoc, 
									  String sSchCode,
									  String sYdGp,
									  String sTCNo)
	{	
		int iSeq = -1;
		
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		
		try {
			
			String scrane_sch_id		= "";    
            String scrane_stock_id		= "";
            String scrane_equip_gp		= "";    
            String scrane_sch_code		= "";   
            String scrane_up_loc		= ""; 
            String scrane_put_loc		= "";
            String scrane_up_func		= ""; 
            String scrane_put_func		= ""; 
            String scrane_register		= ""; 
            String scrane_modifier		= ""; 
            String scrane_yd_gp			= "";
            String scrane_work_duty		= "";
			String scrane_work_party	= "";	        
			String sch_wdemand_duty		= "";
		    String sch_wdemand_party	= "";
		    
			scrane_sch_id		= "000000000000000000";    
            scrane_stock_id		= sStockId.trim();
            scrane_equip_gp		= "";    
            scrane_sch_code		= "";   
            scrane_up_loc		= sUpLoc; 
            scrane_put_loc		= sPutLoc;
            scrane_up_func		= YmCommonConst.CRANE_FUNC_N; 
            scrane_put_func		= YmCommonConst.CRANE_FUNC_N;
            scrane_register		= "SYSTEM"; 
            scrane_modifier		= "SYSTEM"; 
            scrane_yd_gp		= sYdGp;
            
            String sUpBay	= sUpLoc.length() > 2 ? sUpLoc.substring(1,2)  : "";
            String sPutBay	= sPutLoc.length()> 2 ? sPutLoc.substring(1,2) : "";
            
            if("".equals(sSchCode)){
            	if(sUpBay.equals(sPutBay)){
            		if(YmCommonConst.YD_GP_1.equals(sYdGp) ||
            		   YmCommonConst.YD_GP_3.equals(sYdGp) ){
            			scrane_sch_code = YmCommonConst.NEW_SCH_WORK_KIND_CYMM;
            		}else if(YmCommonConst.YD_GP_2.equals(sYdGp) ||
            		   		 YmCommonConst.YD_GP_4.equals(sYdGp) ){
            		   	scrane_sch_code = YmCommonConst.NEW_SCH_WORK_KIND_SYMM;	 	
            		}   		 	
            	}else{
            		if(YmCommonConst.YD_GP_1.equals(sYdGp) ||
            		   YmCommonConst.YD_GP_3.equals(sYdGp) ){
            			scrane_sch_code = YmCommonConst.NEW_SCH_WORK_KIND_CTML;
            		}else if(YmCommonConst.YD_GP_2.equals(sYdGp) ||
            		   		 YmCommonConst.YD_GP_4.equals(sYdGp) ){
            		   	scrane_sch_code = YmCommonConst.NEW_SCH_WORK_KIND_STML;
            		   	//scrane_sch_code = YmCommonConst.NEW_SCH_WORK_KIND_STM2;
            		}   		
            	}
            }else{
            	scrane_sch_code = sSchCode;
        	}            
        	
            scrane_equip_gp = sTCNo.trim();
	        
	        scrane_work_duty		= YmCommonUtil.getWorkDuty();
			scrane_work_party		= YmCommonUtil.getWorkParty();				        
			sch_wdemand_duty		= YmCommonUtil.getWorkDuty();
		    sch_wdemand_party		= YmCommonUtil.getWorkParty();		
		     
			/*
			INSERT INTO TB_YM_WRSLT (
					CRANE_WRSLT_ID, 
					SCH_ID,   
					STOCK_ID,  
					EQUIP_GP,  
					SCH_WORK_KIND,     
	                CRANE_WORK_DDTT,      
	                CRANE_WORK_DUTY,
	                CRANE_WORK_PARTY,
	                CRANE_WORD_DDTT,   
	                CRANE_WRSLT_CD,  
	                SCH_WPREFER,
	                SCH_WDEMAND_DDTT,
				    SCH_WDEMAND_DUTY,
				    SCH_WDEMAND_PARTY,
	                CRANE_WORD_UP_LOC,   
	                CRANE_WORD_PUT_LOC,
	                CRANE_WRSLT_UP_LOC,   
	                CRANE_WRSLT_UP_FUNC,   
	                CRANE_WRSLT_UP_DDTT, 
	                CRANE_WRSLT_PUT_LOC, 
	                CRANE_WRSLT_PUT_FUNC,	
	                CRANE_WRSLT_PUT_DDTT, 
	                REGISTER,    
	                REG_DDTT,    
	                MODIFIER,     
	                MOD_DDTT,    
	                DEL_YN,
	                YD_GP)              
			VALUES (to_char(sysdate,'YYYYMMDDHH24MI')||YM_WRSLT_SEQ.nextval, 
	                :sch_id,    
	                :stock_id,     
	                :equip_gp,    
	                :sch_work_kind,   
	                to_char(sysdate,'YYYYMMDDHH24MISS'),   
	                :scrane_work_duty,
	                :scrane_work_party,
	                to_char(sysdate,'YYYYMMDDHH24MISS'), 
	                'N', 
	                '1',
	                to_char(sysdate,'YYYYMMDDHH24MISS'), 
				    :sch_wdemand_duty,
				    :sch_wdemand_party,
	                :up_loc,  
	                :put_loc,
	                :up_loc, 
	                :up_func, 
	                to_char(sysdate,'YYYYMMDDHH24MISS'), 
	                :put_loc, 
	                :put_func, 
	                to_char(sysdate,'YYYYMMDDHH24MISS'), 
	                :register, 
	                sysdate,  
	                :modifier, 
	                sysdate, 
	                'N',
	                :yd_gp)
			*/
			
		 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.insertCraneWrsltSanJuk";			
		 	iSeq  			  = ydWBookDAO.requestinsertData(queryCode, new Object[]{ 
																		scrane_sch_id,
															            scrane_stock_id,
															            scrane_equip_gp,
															            scrane_sch_code,
															            scrane_work_duty,
																		scrane_work_party,
																		sch_wdemand_duty,
																	    sch_wdemand_party,
															            scrane_up_loc,
															            scrane_put_loc,
															            scrane_up_loc,
															            scrane_up_func,
															            scrane_put_loc,
															            scrane_put_func,
															            scrane_register,
															            scrane_modifier,
															            scrane_yd_gp});	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	    return iSeq; 
	}	
	
	
	
	
	/**
	 * 오퍼레이션명 : 임가공 입고 실적 처리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public void callCoilHrPlateResult(String sStockId,
								  	  String sYdGp,
							      	  String sLoc,
							      	  String modifier)
	{		
		ymCommonDAO dao2 = ymCommonDAO.getInstance();
		CraneSchDAO dao					= new CraneSchDAO();
		Boolean isTrue = Boolean.FALSE;
		
		int iReq =0;
		List productList2 = null;
		JDTORecord stlRecord2 = null;
		stlRecord2 = JDTORecordFactory.getInstance().create();
		
		try{
		    logger.println(LogLevel.DEBUG, this, "=======임가공 고객사 입고 실적 처리 시작========");
			
		    String queryID2 = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
			productList2 	= dao2.getCommonList(queryID2, new Object[]{sStockId});
			stlRecord2 = (JDTORecord)productList2.get(0);
			
			String sYD_GP =StringHelper.evl(stlRecord2.getFieldString("YD_GP"), "");
			
			if(sYD_GP.equals("H") || sYD_GP.equals("J")|| sYD_GP.equals("X")
			  || sYdGp.equals("7") //아세아스틸
			   ){ 
				logger.println(LogLevel.DEBUG, this, "=======C열연  임가공 처리 ========");
				
				//출하 하차개시 완료 전송 및 저장위치 설정
				EJBConnector ejbConn 	= new EJBConnector("default","JNDIYMCCLdWrkOrdReg",this);
				ejbConn.trx("callCoilFromToResult2",new Class[]{String.class,
															String.class,
															String.class,
															String.class},
												 new Object[]{sStockId,
												 	   		sYdGp,
												 	   		sLoc,
												 	   		modifier});
				
				
			}else{
				logger.println(LogLevel.DEBUG, this, "=======AB열연 임가공 처리 ========");
				
				//출하 하차개시 완료 전송 및 저장위치 설정
				EJBConnector ejbConn 	= new EJBConnector("default","JNDIYMCCLdWrkOrdReg",this);
				ejbConn.trx("callCoilFromToResult",new Class[]{String.class,
															String.class,
															String.class},
												 new Object[]{sStockId,
												 	   		sYdGp,
												 	   		sLoc});
				
				//산적위치 수정 
				EJBConnector ejbConn1 = new EJBConnector("default","JNDIYMCraneSchReg",this);
				isTrue = (Boolean)ejbConn1.trx("changeCoilLocationInfo",new Class[]{String.class, 
																	   String.class, 
																	   String.class,
																	   String.class},
																   new Object[]{sStockId, 
																    			"", 
																    			sLoc,
																    			modifier});
				
			}
			
			
			//삼우스틸 TB_CMS_FRTOMOVEWHIF 입고테이블에 전송
			if(sYdGp.equals("5")){
				
				iReq = dao.insertFrtoMoveWhif(sStockId, sLoc);
				logger.println(LogLevel.DEBUG, this, "TB_CMS_FRTOMOVEWHIF insert: = " + iReq);
				
			}
			
			logger.println(LogLevel.DEBUG, this, "=======임가공 고객사 입고 실적 처리 종료========");
						
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }
	}
	
	
	
	/**
	 * 오퍼레이션명 : 임가공 입고 실적 처리 PIDEV
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public void callCoilHrPlateResultPI(String sStockId,
								  	  String sYdGp,
							      	  String sLoc,
							      	  String modifier,
							      	  String sPI_YD)
	{		
		ymCommonDAO dao2 = ymCommonDAO.getInstance();
		CraneSchDAO dao					= new CraneSchDAO();
		Boolean isTrue = Boolean.FALSE;
		
		int iReq =0;
		List productList2 = null;
		JDTORecord stlRecord2 = null;
		stlRecord2 = JDTORecordFactory.getInstance().create();
		
		try{
		    logger.println(LogLevel.DEBUG, this, "=======임가공 고객사 입고 실적 처리 시작========");
			
		    String queryID2 = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
			productList2 	= dao2.getCommonList(queryID2, new Object[]{sStockId});
			stlRecord2 = (JDTORecord)productList2.get(0);
			
			String sYD_GP =StringHelper.evl(stlRecord2.getFieldString("YD_GP"), "");
			
			if(sYD_GP.equals("H") || sYD_GP.equals("J")|| sYD_GP.equals("X")
			  || sYdGp.equals("7") //아세아스틸
			   ){ 
				logger.println(LogLevel.DEBUG, this, "=======C열연  임가공 처리 ========");
				
				//출하 하차개시 완료 전송 및 저장위치 설정
				EJBConnector ejbConn 	= new EJBConnector("default","JNDIYMCCLdWrkOrdReg",this);
				ejbConn.trx("callCoilFromToResult2PI",new Class[]{String.class,
															String.class,
															String.class,
															String.class,
															String.class},
												 new Object[]{sStockId,
												 	   		sYdGp,
												 	   		sLoc,
												 	   		modifier,
													      	sPI_YD});
				
				
			}else{
				logger.println(LogLevel.DEBUG, this, "=======AB열연 임가공 처리 ========");
				
				//출하 하차개시 완료 전송 및 저장위치 설정
				EJBConnector ejbConn 	= new EJBConnector("default","JNDIYMCCLdWrkOrdReg",this);
				ejbConn.trx("callCoilFromToResultPI",new Class[]{String.class,
															String.class,
															String.class},
												 new Object[]{sStockId,
												 	   		sYdGp,
												 	   		sLoc});
				
				//산적위치 수정 
				EJBConnector ejbConn1 = new EJBConnector("default","JNDIYMCraneSchReg",this);
				isTrue = (Boolean)ejbConn1.trx("changeCoilLocationInfoPI",new Class[]{String.class, 
																	   String.class, 
																	   String.class,
																	   String.class,
																	   String.class},
																   new Object[]{sStockId, 
																    			"", 
																    			sLoc,
																    			modifier,
																    			sYdGp});
				
			}
			
			
			//삼우스틸 TB_CMS_FRTOMOVEWHIF 입고테이블에 전송
			if(sYdGp.equals("5")){
				
				iReq = dao.insertFrtoMoveWhifPI(sStockId, sLoc);
				logger.println(LogLevel.DEBUG, this, "TB_CMS_FRTOMOVEWHIF PI insert: = " + iReq);
				
			}
			
			logger.println(LogLevel.DEBUG, this, "=======임가공 고객사 입고 실적 처리 종료========");
						
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }
	}
		
	
	/**
	 * 오퍼레이션명 : 임가공 입고 실적 처리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public void callCoilHrPlateResultNEW(String sStockId,
								  	  String sYdGp,
							      	  String sLoc,
							      	  String modifier)
	{
		
		ymCommonDAO dao2 = ymCommonDAO.getInstance();
		CraneSchDAO dao					= new CraneSchDAO();
		Boolean isTrue = Boolean.FALSE;
		
		int iReq =0;
		List productList2 = null;
		JDTORecord stlRecord2 = null;
		stlRecord2 = JDTORecordFactory.getInstance().create();
		
		try{
		    logger.println(LogLevel.DEBUG, this, "=======임가공 고객사 입고 실적 처리 시작========");
			
				
			//출하 하차개시 완료 전송 및 저장위치 설정
			EJBConnector ejbConn 	= new EJBConnector("default","JNDIYMCCLdWrkOrdReg",this);
			ejbConn.trx("callCoilFromToResultNEW",new Class[]{String.class,
															String.class,
															String.class,
															String.class,
															String.class},
												new Object[]{sStockId,
												 	   		sYdGp,      //임가공사야드
												 	   		sLoc,
												 	   		modifier});			
		
			//TB_CMS_FRTOMOVEWHIF 입고테이블에 전송				
			iReq = dao.insertFrtoMoveWhif(sStockId, sLoc);
			logger.println(LogLevel.DEBUG, this, "TB_CMS_FRTOMOVEWHIF insert: = " + iReq);
				
			
			logger.println(LogLevel.DEBUG, this, "=======임가공 고객사 입고 실적 처리 종료========");
						
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }
	}
	
	
	/**
	 * 오퍼레이션명 : 임가공 입고 실적 처리 
	 * param String : 저장품 List
        * param String : 야드구분
        * param String : 동구분
        * param String : 수조탱크보급(P)/대차출하상차(U) 구분
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */ 
	public void callCoilFromToResult(String sStockId,
								  	  String sYdGp,
							      	  String sLoc)
	{
	
		Boolean isSuccess = new Boolean(false);
		try{
			
			int iSeq = 0;
			
			YdStockDAO ydStockDAO 	        = new YdStockDAO();
			YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
			YdWBookDAO ydWBookDAO           = new YdWBookDAO();
			CraneSchDAO dao					= new CraneSchDAO();
			
			
			List 	   dmList  = null; // Card No,운송지시일자,순번이 같은 저장품정보
			JDTORecord dmRc    = null; // 저장품정보
			JDTORecord pmRc    = null; // 저장품정보
			List 	   pmList  = null;
			String 	   sSmt    = "";   // 저장품이동조건
			String 	   sUpDown = "";   // 이송상차/하차구분
			int		   iDmSize = 0;    // Card No,운송지시일자,순번이 같은 저장품 갯수
			int		   iPmSize = 0;
			int		   iDmFns  = 0;    // 상차완료인 저장품 갯수	
			String 	   sDmItem = "";
			
			String sSchCode 		= "";
			String sStockMoveTerm 	= "";
			
			String sStkLayerQueryId = "";
			String wBookQueryId 	= "";
			String sWBookQueryId 	= "";	
			String stkQueryId 		= "";
			String wBookid      	= "";
	
			
			JDTORecord wBookSel		= null;
			
			logger.println(LogLevel.DEBUG, this, "=======임가공  생성 시작========");
			
			EJBConnector ejbConn = new EJBConnector("default","JNDIYMYardWrkResReg",this);
			
				
			logger.println(LogLevel.DEBUG,this, "Stock_id===" + sStockId);
			logger.println(LogLevel.DEBUG,this, "sLoc===" + sLoc);
			
			
			/* 이송 완료 setting */
			iSeq = dao.updateFrToDoneInfo(sStockId);
				
			/*
			 * 이송 상하차 실적 조업 전송 
			*/
//			YMPO159 model = new YMPO159();
//			model.setTcCode(YmCommonConst.MODEL_YMPO159);
//			model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
//			model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
//				
//			/* 상하차처리일자  CHAR(8)	 yyyymmdd */
//			model.setupDownDate(YmCommonUtil.getCurDate("yyyyMMdd"));
//				
//			/* COIL번호	 CHAR(11)	*/
//			model.setcoilNo(sStockId);
//				
//			/* 상하차구분	 CHAR(1)		U:상차, D:하차		*/
//			model.setupDownGbn("D");
//				
//			/* 상하차위치 	 CHAR(10) 상차,하차 위치 */
//			model.setupDownLoc(sLoc);
//				
//				
//			isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
//															  	  	 new Object[]{model});
//			logger.println(LogLevel.DEBUG,this, "내부IF호출===Coil 이송 상/하차 완료 시.===");
			
			
			
			List productList = null;
			List productList2 = null;
			JDTORecord stlRecord = null;
			JDTORecord stlRecord2 = null;
			stlRecord = JDTORecordFactory.getInstance().create();
			stlRecord2 = JDTORecordFactory.getInstance().create();
			
			ymCommonDAO dao2 = ymCommonDAO.getInstance();
			
			String queryID2 = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
			productList2 	= dao2.getCommonList(queryID2, new Object[]{sStockId});
			stlRecord2 = (JDTORecord)productList2.get(0);
			
			String sCURR_PROG_CD =StringHelper.evl(stlRecord2.getFieldString("CURR_PROG_CD"), "");

			if(sCURR_PROG_CD.equals("C")){// 이미진행 된 경우 
				logger.println(LogLevel.DEBUG,this, "==>>코일 재료공통 변경이 완료 된 경우 skip처리  =============");
			} else {
			/*	
		    String queryID2 = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
			productList2 	= dao2.getCommonList(queryID2, new Object[]{sStockId});
			stlRecord2 = (JDTORecord)productList2.get(0);
			
			String sCURR_PROG_CD =StringHelper.evl(stlRecord2.getFieldString("CURR_PROG_CD"), "");
			
			if(sCURR_PROG_CD.equals("C")){// 이미진행 된 경우 
				logger.println(LogLevel.DEBUG,this, "==>>코일 재료공통 변경이 완료 된 경우 skip처리  =============");
			} else {
			*/	
			//재료공통 테이블 업데이트
//			int intRtnVal = dao.requestupdateData2(sStockId);
//			if(intRtnVal <= 0){
//				logger.println(LogLevel.DEBUG,this, "==>>코일 재료공통 변경 작업 에러 =============");
//			}
			
			
	 		//실적처리 
    	 	EJBConnector ejbConn2 = new EJBConnector("default","JNDIYMCCLdWrkOrdReg",this);
    	 	Boolean isYd = (Boolean)ejbConn2.trx("CarinfoFrtoMoveBackupSub2",new  Class[]{String.class},
														new Object[]{sStockId});	
    	 	
    	 	
			
		    String queryID 			= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
			productList 	= dao2.getCommonList(queryID, new Object[]{sStockId});
			stlRecord = (JDTORecord)productList.get(0);
			
            //코일소재임가공이송완료실적(YDPTJ003)
			tcRecord =JDTORecordFactory.getInstance().create();
			tcRecord.setField("JMS_TC_CD", "YDPTJ003");
			tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			tcRecord.setField("STL_NO",  StringHelper.evl(stlRecord.getFieldString("COIL_NO"), ""));
			// 주문번호
			tcRecord.setField("ORD_NO",  StringHelper.evl(stlRecord.getFieldString("ORD_NO"), ""));
			// 주문행번
			tcRecord.setField("ORD_DTL", StringHelper.evl(stlRecord.getFieldString("ORD_DTL"), ""));
			// 공장공정코드
			tcRecord.setField("PLNT_PROC_CD",  StringHelper.evl(stlRecord.getFieldString("PLNT_PROC_CD"), ""));
			// 재료외형구분
			tcRecord.setField("STL_APPEAR_GP",  StringHelper.evl(stlRecord.getFieldString("STL_APPEAR_GP"), ""));
			// 현재진도코드
			tcRecord.setField("CURR_PROG_CD",  StringHelper.evl(stlRecord.getFieldString("CURR_PROG_CD"), ""));
			// 주문여재구분
			tcRecord.setField("ORD_YEOJAE_GP", StringHelper.evl(stlRecord.getFieldString("ORD_YEOJAE_GP"), ""));
			// 재료중량 (SLAB중량) 
			tcRecord.setField("STL_WT",  StringHelper.evl(stlRecord.getFieldString("COIL_WT"), ""));
			// 설계재료중량(항목명?)
			tcRecord.setField("DS_MTL_WT", "");
			// 재료상태구분(항목명?)
			tcRecord.setField("MTL_STAT_GP",  StringHelper.evl(stlRecord.getFieldString("RECORD_PROG_STAT"), ""));
			// Record 종료구분
			tcRecord.setField("RECORD_END_GP",  StringHelper.evl(stlRecord.getFieldString("RECORD_END_GP"), ""));
			// Record 종료구분 1(항목명?)
			tcRecord.setField("RECORD_END_GP1", "");
			// 전진도 코드
			tcRecord.setField("BEFO_PROG_CD",  StringHelper.evl(stlRecord.getFieldString("BEFO_PROG_CD"), ""));
			// 전주문 번호
			tcRecord.setField("BEF_ORD_NO",  StringHelper.evl(stlRecord.getFieldString("BEF_ORD_NO"), ""));
			// 전주문 행번
			tcRecord.setField("BEF_ORD_DTL",  StringHelper.evl(stlRecord.getFieldString("BEF_ORD_DTL"), ""));
			// 모재료번호   
			tcRecord.setField("MMATL_FEE_NO",  StringHelper.evl(stlRecord.getFieldString("MMATL_FEE_NO"), ""));
			// 목전충당구분
			tcRecord.setField("ORDERTRANS_MATCH_GP", StringHelper.evl(stlRecord.getFieldString("MATCH_ORDERTRANS_GP"), ""));	
			isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
			  	  	 new Object[]{tcRecord});
			logger.println(LogLevel.DEBUG,this, "내부IF호출===B열연 SLAB 이송 하차 완료 시.===");
                   	
	
				
			/*
			 * 이송 상하차 실적 출하 전송
			 */
			
			/* 이송 완료 */
			pmList  = dao.getYmDmFrToInfo(sStockId);
			iDmFns	= pmList.size();
			
			/* 이송 지시 전체 */
			dmList  = dao.getYmDmCommonInfo(sStockId);
			iDmSize = dmList.size();			

				
			logger.println(LogLevel.DEBUG,this, "이송 지시 전체==" + iDmSize);
			logger.println(LogLevel.DEBUG,this, "이송 완료.=====" + iDmFns);
			if(iDmSize != 0 && iDmSize == iDmFns){ // 상,하차완료인 저장품이 ALL일때 처리
		    		
				dmRc   = (JDTORecord)dmList.get(0);
					
		    	String sCarCardNo     = StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), "");
		    	String sTransWordNo   = StringHelper.evl(dmRc.getFieldString("TRANS_WORD_NO"), "");
		    		
			    //AB열연 ##############################################################################################	
		   	
//				YMDM006 model1 = new YMDM006();
//				model1.setTcCode(YmCommonConst.MODEL_YMDM006);
//				model1.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
//				model1.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
//					
//				/** 상차 완료 일자 */
//				model1.setCARLOAD_DONE_DATE(YmCommonUtil.getCurDate("yyyyMMdd"));
//					
//				/** 상차 완료 시각 */
//				model1.setCARLOAD_DONE_TIME(YmCommonUtil.getCurDate("HHmmss"));
//					
//				/** 상하차 구분 U:상차, D:하차*/
//				model1.setUPCARUNLOAD_GP("D");
//					
//				/** 이송지시일자 */
//				model1.setTRANS_WORD_DATE(sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
//					
//				/** 이송지시순번 */
//				model1.setTRANS_WORD_SEQNO(sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
//												
//				/** 카드번호 */
//				model1.setCARD_NO(sCarCardNo);
		    	
				JDTORecord carR = dao.getDmCarInfo(sStockId);
				String sCarNo = "";
		    	if(carR != null){
					sCarNo = StringHelper.evl(carR.getFieldString("CAR_NO"), "");
		    	}					
//				JDTORecord carR = dao.getDmCarInfo(sStockId);
//				String sCarNo = "";
//			    if(carR != null){
//					sCarNo = StringHelper.evl(carR.getFieldString("CAR_NO"), "");
//			    }
//				/** 차량번호 */
//				model1.setCAR_NO(sCarNo);
//					
//				/** 소재/제품 구분 1:소재, 2:제품, 3: 임가공 */
//				model1.setMATERIAL_GOODS("3");
//					
//				isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
//																  	  	 new Object[]{model1});
//				logger.println(LogLevel.DEBUG,this, "내부IF호출===Coil 제품창고 이송 상차 작업 시 마지막 Coil 상/하차 권하 시.===");		
			    //일관제철 ##############################################################################################	
		    	
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                //임가공이송하차개시
     			JDTORecord tcRecordDM = null;
     			tcRecordDM = JDTORecordFactory.getInstance().create(); 
     			tcRecordDM.setField("UPCARUNLOAD_GP", "D"); 
                tcRecordDM.setField("CARD_NO",sCarCardNo);
                tcRecordDM.setField("CAR_NO", sCarNo);
                tcRecordDM.setField("YD_GP", sYdGp);		
                tcRecordDM.setField("TRANS_WORD_DATE",sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
                tcRecordDM.setField("TRANS_WORD_SEQNO", sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
     			
     			//인터페이스 전문 호출
     			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYMYardWrkResReg",this);
     			isSuccess = (Boolean)ejbConn1.trx("getYDDMR020",new Class[]{JDTORecord.class},
     			  	  	 new Object[]{tcRecordDM}); 
                logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 임가공이송상하차개시.===");
                //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                //임가공이송하차완료
     			tcRecordDM = null;
     			tcRecordDM = JDTORecordFactory.getInstance().create(); 
     			tcRecordDM.setField("UPCARUNLOAD_GP", "D");
                tcRecordDM.setField("CARD_NO",sCarCardNo);
                tcRecordDM.setField("CAR_NO", sCarNo);
                tcRecordDM.setField("YD_GP", sYdGp);		
                tcRecordDM.setField("TRANS_WORD_DATE",sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
                tcRecordDM.setField("TRANS_WORD_SEQNO",  sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
     			
     			//인터페이스 전문 호출
     			EJBConnector ejbConn3 = new EJBConnector("default","JNDIYMYardWrkResReg",this);
     			isSuccess = (Boolean)ejbConn3.trx("getYDDMR022",new Class[]{JDTORecord.class},
     			  	  	 new Object[]{tcRecordDM}); 
                logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 임가공이송상하차완료.===");
                //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			}
			} //if end skip
			
		logger.println(LogLevel.DEBUG, this, "=======작업예약 생성 끝========");
						
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }
	}
	
	
	/**
	 * 오퍼레이션명 : 임가공 입고 실적 처리 PIDEV
	 * param String : 저장품 List
        * param String : 야드구분
        * param String : 동구분
        * param String : 수조탱크보급(P)/대차출하상차(U) 구분
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */ 
	public void callCoilFromToResultPI(String sStockId,
								  	  String sYdGp,
							      	  String sLoc)
	{
	
		Boolean isSuccess = new Boolean(false);
		try{
			
			int iSeq = 0;
			
			YdStockDAO ydStockDAO 	        = new YdStockDAO();
			YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
			YdWBookDAO ydWBookDAO           = new YdWBookDAO();
			CraneSchDAO dao					= new CraneSchDAO();
			
			
			List 	   dmList  = null; // Card No,운송지시일자,순번이 같은 저장품정보
			JDTORecord dmRc    = null; // 저장품정보
			JDTORecord pmRc    = null; // 저장품정보
			List 	   pmList  = null;
			String 	   sSmt    = "";   // 저장품이동조건
			String 	   sUpDown = "";   // 이송상차/하차구분
			int		   iDmSize = 0;    // Card No,운송지시일자,순번이 같은 저장품 갯수
			int		   iPmSize = 0;
			int		   iDmFns  = 0;    // 상차완료인 저장품 갯수	
			String 	   sDmItem = "";
			
			String sSchCode 		= "";
			String sStockMoveTerm 	= "";
			
			String sStkLayerQueryId = "";
			String wBookQueryId 	= "";
			String sWBookQueryId 	= "";	
			String stkQueryId 		= "";
			String wBookid      	= "";
	
			
			JDTORecord wBookSel		= null;
			
			logger.println(LogLevel.DEBUG, this, "=======임가공  생성 시작========");
			
			EJBConnector ejbConn = new EJBConnector("default","JNDIYMYardWrkResReg",this);
			
				
			logger.println(LogLevel.DEBUG,this, "Stock_id===" + sStockId);
			logger.println(LogLevel.DEBUG,this, "sLoc===" + sLoc);
			
			
			/* 이송 완료 setting */
			iSeq = dao.updateFrToDoneInfo(sStockId);
				
			/*
			 * 이송 상하차 실적 조업 전송 
			*/
//			YMPO159 model = new YMPO159();
//			model.setTcCode(YmCommonConst.MODEL_YMPO159);
//			model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
//			model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
//				
//			/* 상하차처리일자  CHAR(8)	 yyyymmdd */
//			model.setupDownDate(YmCommonUtil.getCurDate("yyyyMMdd"));
//				
//			/* COIL번호	 CHAR(11)	*/
//			model.setcoilNo(sStockId);
//				
//			/* 상하차구분	 CHAR(1)		U:상차, D:하차		*/
//			model.setupDownGbn("D");
//				
//			/* 상하차위치 	 CHAR(10) 상차,하차 위치 */
//			model.setupDownLoc(sLoc);
//				
//				
//			isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
//															  	  	 new Object[]{model});
//			logger.println(LogLevel.DEBUG,this, "내부IF호출===Coil 이송 상/하차 완료 시.===");
			
			
			
			List productList = null;
			List productList2 = null;
			JDTORecord stlRecord = null;
			JDTORecord stlRecord2 = null;
			stlRecord = JDTORecordFactory.getInstance().create();
			stlRecord2 = JDTORecordFactory.getInstance().create();
			
			ymCommonDAO dao2 = ymCommonDAO.getInstance();
			
			String queryID2 = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
			productList2 	= dao2.getCommonList(queryID2, new Object[]{sStockId});
			stlRecord2 = (JDTORecord)productList2.get(0);
			
			String sCURR_PROG_CD =StringHelper.evl(stlRecord2.getFieldString("CURR_PROG_CD"), "");

			if(sCURR_PROG_CD.equals("C")){// 이미진행 된 경우 
				logger.println(LogLevel.DEBUG,this, "==>>코일 재료공통 변경이 완료 된 경우 skip처리  =============");
			} else {
			/*	
		    String queryID2 = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
			productList2 	= dao2.getCommonList(queryID2, new Object[]{sStockId});
			stlRecord2 = (JDTORecord)productList2.get(0);
			
			String sCURR_PROG_CD =StringHelper.evl(stlRecord2.getFieldString("CURR_PROG_CD"), "");
			
			if(sCURR_PROG_CD.equals("C")){// 이미진행 된 경우 
				logger.println(LogLevel.DEBUG,this, "==>>코일 재료공통 변경이 완료 된 경우 skip처리  =============");
			} else {
			*/	
			//재료공통 테이블 업데이트
//			int intRtnVal = dao.requestupdateData2(sStockId);
//			if(intRtnVal <= 0){
//				logger.println(LogLevel.DEBUG,this, "==>>코일 재료공통 변경 작업 에러 =============");
//			}
			
			
	 		//실적처리 
    	 	EJBConnector ejbConn2 = new EJBConnector("default","JNDIYMCCLdWrkOrdReg",this);
    	 	Boolean isYd = (Boolean)ejbConn2.trx("CarinfoFrtoMoveBackupSub2",new  Class[]{String.class},
														new Object[]{sStockId});	
    	 	
    	 	
			
		    String queryID 			= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
			productList 	= dao2.getCommonList(queryID, new Object[]{sStockId});
			stlRecord = (JDTORecord)productList.get(0);
			
            //코일소재임가공이송완료실적(YDPTJ003)
			tcRecord =JDTORecordFactory.getInstance().create();
			tcRecord.setField("JMS_TC_CD", "YDPTJ003");
			tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			tcRecord.setField("STL_NO",  StringHelper.evl(stlRecord.getFieldString("COIL_NO"), ""));
			// 주문번호
			tcRecord.setField("ORD_NO",  StringHelper.evl(stlRecord.getFieldString("ORD_NO"), ""));
			// 주문행번
			tcRecord.setField("ORD_DTL", StringHelper.evl(stlRecord.getFieldString("ORD_DTL"), ""));
			// 공장공정코드
			tcRecord.setField("PLNT_PROC_CD",  StringHelper.evl(stlRecord.getFieldString("PLNT_PROC_CD"), ""));
			// 재료외형구분
			tcRecord.setField("STL_APPEAR_GP",  StringHelper.evl(stlRecord.getFieldString("STL_APPEAR_GP"), ""));
			// 현재진도코드
			tcRecord.setField("CURR_PROG_CD",  StringHelper.evl(stlRecord.getFieldString("CURR_PROG_CD"), ""));
			// 주문여재구분
			tcRecord.setField("ORD_YEOJAE_GP", StringHelper.evl(stlRecord.getFieldString("ORD_YEOJAE_GP"), ""));
			// 재료중량 (SLAB중량) 
			tcRecord.setField("STL_WT",  StringHelper.evl(stlRecord.getFieldString("COIL_WT"), ""));
			// 설계재료중량(항목명?)
			tcRecord.setField("DS_MTL_WT", "");
			// 재료상태구분(항목명?)
			tcRecord.setField("MTL_STAT_GP",  StringHelper.evl(stlRecord.getFieldString("RECORD_PROG_STAT"), ""));
			// Record 종료구분
			tcRecord.setField("RECORD_END_GP",  StringHelper.evl(stlRecord.getFieldString("RECORD_END_GP"), ""));
			// Record 종료구분 1(항목명?)
			tcRecord.setField("RECORD_END_GP1", "");
			// 전진도 코드
			tcRecord.setField("BEFO_PROG_CD",  StringHelper.evl(stlRecord.getFieldString("BEFO_PROG_CD"), ""));
			// 전주문 번호
			tcRecord.setField("BEF_ORD_NO",  StringHelper.evl(stlRecord.getFieldString("BEF_ORD_NO"), ""));
			// 전주문 행번
			tcRecord.setField("BEF_ORD_DTL",  StringHelper.evl(stlRecord.getFieldString("BEF_ORD_DTL"), ""));
			// 모재료번호   
			tcRecord.setField("MMATL_FEE_NO",  StringHelper.evl(stlRecord.getFieldString("MMATL_FEE_NO"), ""));
			// 목전충당구분
			tcRecord.setField("ORDERTRANS_MATCH_GP", StringHelper.evl(stlRecord.getFieldString("MATCH_ORDERTRANS_GP"), ""));	
			isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
			  	  	 new Object[]{tcRecord});
			logger.println(LogLevel.DEBUG,this, "내부IF호출===B열연 SLAB 이송 하차 완료 시.===");
                   	
	
				
			/*
			 * 이송 상하차 실적 출하 전송
			 */
			
			/* 이송 완료 */
			pmList  = dao.getYmDmFrToInfoPI(sStockId);
			/* 이송 지시 전체 */
			dmList  = dao.getYmDmCommonInfoPI(sStockId);	
			
			iDmFns	= pmList.size();
			iDmSize = dmList.size();			

				
			logger.println(LogLevel.DEBUG,this, "이송 지시 전체==" + iDmSize);
			logger.println(LogLevel.DEBUG,this, "이송 완료.=====" + iDmFns);
			if(iDmSize != 0 && iDmSize == iDmFns){ // 상,하차완료인 저장품이 ALL일때 처리
		    		
				dmRc   = (JDTORecord)dmList.get(0);
					
		    	String sCarCardNo     = StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), "");
		    	String sTransWordNo   = StringHelper.evl(dmRc.getFieldString("TRANS_WORD_NO"), "");
		    		
			    //AB열연 ##############################################################################################	
		   	
//				YMDM006 model1 = new YMDM006();
//				model1.setTcCode(YmCommonConst.MODEL_YMDM006);
//				model1.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
//				model1.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
//					
//				/** 상차 완료 일자 */
//				model1.setCARLOAD_DONE_DATE(YmCommonUtil.getCurDate("yyyyMMdd"));
//					
//				/** 상차 완료 시각 */
//				model1.setCARLOAD_DONE_TIME(YmCommonUtil.getCurDate("HHmmss"));
//					
//				/** 상하차 구분 U:상차, D:하차*/
//				model1.setUPCARUNLOAD_GP("D");
//					
//				/** 이송지시일자 */
//				model1.setTRANS_WORD_DATE(sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
//					
//				/** 이송지시순번 */
//				model1.setTRANS_WORD_SEQNO(sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
//												
//				/** 카드번호 */
//				model1.setCARD_NO(sCarCardNo);
		    	
				JDTORecord carR = dao.getDmCarInfoPI(sStockId);
				String sCarNo = "";
		    	if(carR != null){
					sCarNo = StringHelper.evl(carR.getFieldString("CAR_NO"), "");
		    	}					
//				JDTORecord carR = dao.getDmCarInfo(sStockId);
//				String sCarNo = "";
//			    if(carR != null){
//					sCarNo = StringHelper.evl(carR.getFieldString("CAR_NO"), "");
//			    }
//				/** 차량번호 */
//				model1.setCAR_NO(sCarNo);
//					
//				/** 소재/제품 구분 1:소재, 2:제품, 3: 임가공 */
//				model1.setMATERIAL_GOODS("3");
//					
//				isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
//																  	  	 new Object[]{model1});
//				logger.println(LogLevel.DEBUG,this, "내부IF호출===Coil 제품창고 이송 상차 작업 시 마지막 Coil 상/하차 권하 시.===");		
			    //일관제철 ##############################################################################################	
		    	
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                //임가공이송하차개시
     			JDTORecord tcRecordDM = null;
     			tcRecordDM = JDTORecordFactory.getInstance().create(); 
     			tcRecordDM.setField("UPCARUNLOAD_GP", "D"); 
                tcRecordDM.setField("CARD_NO",sCarCardNo);
                tcRecordDM.setField("CAR_NO", sCarNo);
                tcRecordDM.setField("YD_GP", sYdGp);		
                tcRecordDM.setField("TRANS_WORD_DATE",sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
                tcRecordDM.setField("TRANS_WORD_SEQNO", sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");

     			//인터페이스 전문 호출
     			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYMYardWrkResReg",this);
     			isSuccess = (Boolean)ejbConn1.trx("getM10YDLMJ1115",new Class[]{JDTORecord.class},
     			  	  	 new Object[]{tcRecordDM});      
     			
                logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 임가공이송상하차개시.===");
                //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                //임가공이송하차완료
     			tcRecordDM = null;
     			tcRecordDM = JDTORecordFactory.getInstance().create(); 
     			tcRecordDM.setField("UPCARUNLOAD_GP", "D");
                tcRecordDM.setField("CARD_NO",sCarCardNo);
                tcRecordDM.setField("CAR_NO", sCarNo);
                tcRecordDM.setField("YD_GP", sYdGp);		
                tcRecordDM.setField("TRANS_WORD_DATE",sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
                tcRecordDM.setField("TRANS_WORD_SEQNO",  sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");

     			//인터페이스 전문 호출
     			isSuccess = (Boolean)ejbConn1.trx("getM10YDLMJ1125",new Class[]{JDTORecord.class},
     			  	  	 new Object[]{tcRecordDM});    
                
                logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 임가공이송상하차완료.===");
                //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			}
			} //if end skip
			
		logger.println(LogLevel.DEBUG, this, "=======작업예약 생성 끝========");
						
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }
	}
	
	
	/**
	 * 오퍼레이션명 : 
	 *	1.	COIL INFO
        *		실적정보 처리 
	 *
	 * param jDTORecord : 전문항목
        * param String	   : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */     	 
	public boolean CarinfoFrtoMoveBackupSub2(String s_STOCK_ID){
		
		boolean isSuccess = false;
		String stkQueryId ="";
		YdStockDAO ydStockDAO = new YdStockDAO();
		try{
			
			//코일공통 업데이트				
			stkQueryId = "ym.facilitywork.putwrecord.session.updateMatlFtmvWlrstCoil2";
			int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID,s_STOCK_ID});
				
			isSuccess = true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *	1.	COIL INFO
        *		실적정보 처리 
	 *
	 * param jDTORecord : 전문항목
        * param String	   : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */     	 
	public boolean CarinfoFrtoMoveBackupSub(String s_STOCK_ID){
		
		boolean isSuccess = false;
		String stkQueryId ="";
		YdStockDAO ydStockDAO = new YdStockDAO();
		try{
			//코일공통 업데이트				
			stkQueryId = "ym.facilitywork.putwrecord.session.updateMatlFtmvWlrstCoil2";
			int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID,s_STOCK_ID});
				
			isSuccess = true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}
	
	/**
	 * 오퍼레이션명 : 임가공 입고 실적 처리(C열연용) 
	 * param String : 저장품 List
        * param String : 야드구분
        * param String : 동구분
        * param String : 수조탱크보급(P)/대차출하상차(U) 구분
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public void callCoilFromToResult2(String sStockId,
								  	  String sYdGp,
							      	  String sLoc,
							      	  String modifier)
	{
	
		Boolean isSuccess = new Boolean(false);
		try{
			
			int iSeq = 0;
			
			CraneSchDAO dao					= new CraneSchDAO();
			List productList = null;
			List productList2 = null;
			JDTORecord stlRecord = null;
			JDTORecord stlRecord2 = null;
			stlRecord = JDTORecordFactory.getInstance().create();
			stlRecord2 = JDTORecordFactory.getInstance().create();			
			ymCommonDAO dao2 = ymCommonDAO.getInstance();
			
			List 	   dmList  = null; // Card No,운송지시일자,순번이 같은 저장품정보
			JDTORecord dmRc    = null; // 저장품정보
			JDTORecord pmRc    = null; // 저장품정보
			List 	   pmList  = null;
			int		   iDmSize = 0;    // Card No,운송지시일자,순번이 같은 저장품 갯수
			int		   iPmSize = 0;
			int		   iDmFns  = 0;    // 상차완료인 저장품 갯수	
			int 		iReq	= 0;

			String sPutStackColGp 	= "";
			String sPutStackBedGp 	= "";
			String sPutStackLayerGp = "";
			String sPutYardGp		= "";
			
			JDTORecord wBookSel		= null;
			
			logger.println(LogLevel.DEBUG, this, "=======임가공  생성 시작========");
			
			EJBConnector ejbConn = new EJBConnector("default","JNDIYMYardWrkResReg",this);
			
				
			logger.println(LogLevel.DEBUG,this, "Stock_id===" + sStockId);
			logger.println(LogLevel.DEBUG,this, "sLoc===" + sLoc);
			
			
			/* 이송 완료 setting */
			iSeq = dao.updateFrToDoneInfo2(sStockId);
			
			String queryID2 = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
			productList2 	= dao2.getCommonList(queryID2, new Object[]{sStockId});
			stlRecord2 = (JDTORecord)productList2.get(0);
			
			String sCURR_PROG_CD =StringHelper.evl(stlRecord2.getFieldString("CURR_PROG_CD"), "");

			if(sCURR_PROG_CD.equals("C")){// 이미진행 된 경우 
				logger.println(LogLevel.DEBUG,this, "==>>코일 재료공통 변경이 완료 된 경우 skip처리  =============");
			} else {
//				//재료공통 테이블 업데이트
//				int intRtnVal = dao.requestupdateData2(sStockId);
//				if(intRtnVal <= 0){
//					logger.println(LogLevel.DEBUG,this, "==>>코일 재료공통 변경 작업 에러 =============");
//				}
				
	 		//실적처리 
    	 	EJBConnector ejbConn2 = new EJBConnector("default","JNDIYMCCLdWrkOrdReg",this);
    	 	Boolean isYd = (Boolean)ejbConn2.trx("CarinfoFrtoMoveBackupSub2",new  Class[]{String.class},
														new Object[]{sStockId});
				
			
		    String queryID 			= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
			productList 	= dao2.getCommonList(queryID, new Object[]{sStockId});
			stlRecord = (JDTORecord)productList.get(0);
			
            //코일소재임가공이송완료실적(YDPTJ003)
			tcRecord =JDTORecordFactory.getInstance().create();
			tcRecord.setField("JMS_TC_CD", "YDPTJ003");
			tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			tcRecord.setField("STL_NO",  StringHelper.evl(stlRecord.getFieldString("COIL_NO"), ""));
			// 주문번호
			tcRecord.setField("ORD_NO",  StringHelper.evl(stlRecord.getFieldString("ORD_NO"), ""));
			// 주문행번
			tcRecord.setField("ORD_DTL", StringHelper.evl(stlRecord.getFieldString("ORD_DTL"), ""));
			// 공장공정코드
			tcRecord.setField("PLNT_PROC_CD",  StringHelper.evl(stlRecord.getFieldString("PLNT_PROC_CD"), ""));
			// 재료외형구분
			tcRecord.setField("STL_APPEAR_GP",  StringHelper.evl(stlRecord.getFieldString("STL_APPEAR_GP"), ""));
			// 현재진도코드
			tcRecord.setField("CURR_PROG_CD",  StringHelper.evl(stlRecord.getFieldString("CURR_PROG_CD"), ""));
			// 주문여재구분
			tcRecord.setField("ORD_YEOJAE_GP", StringHelper.evl(stlRecord.getFieldString("ORD_YEOJAE_GP"), ""));
			// 재료중량 (SLAB중량) 
			tcRecord.setField("STL_WT",  StringHelper.evl(stlRecord.getFieldString("COIL_WT"), ""));
			// 설계재료중량(항목명?)
			tcRecord.setField("DS_MTL_WT", "");
			// 재료상태구분(항목명?)
			tcRecord.setField("MTL_STAT_GP",  StringHelper.evl(stlRecord.getFieldString("RECORD_PROG_STAT"), ""));
			// Record 종료구분
			tcRecord.setField("RECORD_END_GP",  StringHelper.evl(stlRecord.getFieldString("RECORD_END_GP"), ""));
			// Record 종료구분 1(항목명?)
			tcRecord.setField("RECORD_END_GP1", "");
			// 전진도 코드
			tcRecord.setField("BEFO_PROG_CD",  StringHelper.evl(stlRecord.getFieldString("BEFO_PROG_CD"), ""));
			// 전주문 번호
			tcRecord.setField("BEF_ORD_NO",  StringHelper.evl(stlRecord.getFieldString("BEF_ORD_NO"), ""));
			// 전주문 행번
			tcRecord.setField("BEF_ORD_DTL",  StringHelper.evl(stlRecord.getFieldString("BEF_ORD_DTL"), ""));
			// 모재료번호   
			tcRecord.setField("MMATL_FEE_NO",  StringHelper.evl(stlRecord.getFieldString("MMATL_FEE_NO"), ""));
			// 목전충당구분
			tcRecord.setField("ORDERTRANS_MATCH_GP", StringHelper.evl(stlRecord.getFieldString("MATCH_ORDERTRANS_GP"), ""));	
			isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
			  	  	 new Object[]{tcRecord});
			logger.println(LogLevel.DEBUG,this, "내부IF호출===B열연 SLAB 이송 하차 완료 시.===");
                   	
	
				
			/*
			 * 이송 상하차 실적 출하 전송
			 */
			
			/* 이송 완료 대상 조회 */
			pmList  = dao.getYmDmFrToInfo2(sStockId);
			iDmFns	= pmList.size();
			
			/* 이송 지시 대상 조회 */
			dmList  = dao.getYdDmCommonInfo(sStockId);
			iDmSize = dmList.size();			

				
			logger.println(LogLevel.DEBUG,this, "이송 지시 전체==" + iDmSize);
			logger.println(LogLevel.DEBUG,this, "이송 완료.=====" + iDmFns);
			if(iDmSize != 0 && iDmSize == iDmFns){ // 상,하차완료인 저장품이 ALL일때 처리
		    		
				dmRc   = (JDTORecord)dmList.get(0);
					
		    	String sCarCardNo     = StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), "");
		    	String sTransWordNo   = StringHelper.evl(dmRc.getFieldString("TRANS_WORD_NO"), "");
		    	String sCarNo   	  = StringHelper.evl(dmRc.getFieldString("CAR_NO"), "");
					
			    //일관제철 ##############################################################################################	
		    	
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                //임가공이송하차개시
     			JDTORecord tcRecordDM = null;
     			tcRecordDM = JDTORecordFactory.getInstance().create(); 
     			tcRecordDM.setField("UPCARUNLOAD_GP", "D"); 
                tcRecordDM.setField("CARD_NO",sCarCardNo);
                tcRecordDM.setField("CAR_NO", sCarNo);
                tcRecordDM.setField("YD_GP", sYdGp);		
                tcRecordDM.setField("TRANS_WORD_DATE",sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
                tcRecordDM.setField("TRANS_WORD_SEQNO", sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
     			
     			//인터페이스 전문 호출
     			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYMYardWrkResReg",this);
     			isSuccess = (Boolean)ejbConn1.trx("getYDDMR020",new Class[]{JDTORecord.class},
     			  	  	 new Object[]{tcRecordDM}); 
                logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 임가공이송상하차개시.===");
                //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                //임가공이송하차완료
     			tcRecordDM = null;
     			tcRecordDM = JDTORecordFactory.getInstance().create(); 
     			tcRecordDM.setField("UPCARUNLOAD_GP", "D");
                tcRecordDM.setField("CARD_NO",sCarCardNo);
                tcRecordDM.setField("CAR_NO", sCarNo);
                tcRecordDM.setField("YD_GP", sYdGp);		
                tcRecordDM.setField("TRANS_WORD_DATE",sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
                tcRecordDM.setField("TRANS_WORD_SEQNO",  sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
     			
     			//인터페이스 전문 호출
     			EJBConnector ejbConn3 = new EJBConnector("default","JNDIYMYardWrkResReg",this);
     			isSuccess = (Boolean)ejbConn3.trx("getYDDMR022",new Class[]{JDTORecord.class},
     			  	  	 new Object[]{tcRecordDM}); 
                logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 임가공이송상하차완료.===");
                //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			}
			} //if end skip
			
			
			
			/**
			 * 0. 입력한 To 위치 정합성 점검
			 */
			if (sLoc.length() == 10) {
				 sPutStackColGp = sLoc.substring(0, 6);
				 sPutStackBedGp = sLoc.substring(6, 8);
				 sPutStackLayerGp = sLoc.substring(8, 10);
			}else {
//				substr(:pos,1,1),-- 야드구분
//		        substr(:pos,2,1),-- 동
//		        substr(:pos,3,2),-- SPAN
//		        substr(:pos,5,2),-- 적치열번지
//		        substr(:pos,7,2),-- 적치번지
//		        substr(:pos,9,2),-- 적치단
				sLoc =	sYdGp + 
						sLoc.substring(0, 1) + 
						"00" + 
						"0" + sLoc.substring(1, 2) + 
						sLoc.substring(2, 4) + 
						"01";
			}
			
			
			/*
			 * 적치단 Put위치를 적치상태로 변경 tb_ym_stacklayer Table : stock_id = Coil
			 * No tb_ym_stacklayer Table : stack_layer_stat = 'L'(적치중)
			 */
			String sLayerStat = YmCommonConst.STACK_LAYER_STAT_L;
			iReq = dao.updateCraneStackLayerStat(sPutStackColGp, sPutStackBedGp, sPutStackLayerGp, sStockId, sLayerStat);
			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 저장위치  UPDATE = " + iReq);

			/*
			 * Coil 공통 Table 저장위치 Update
			 */
			iReq = dao.updateCoilCommonLocInfo(sStockId, sLoc);
			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 코일공통 UPDATE = " + iReq);
			
			/*
			 * 6. Crane 작업 실적 등록
			 */
			
			if (sLoc.length() > 1) {
				sPutYardGp = sLoc.substring(0, 1);
			}
			
			
			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYMCraneSchReg",this);
			ejbConn1.trx("insertUpPutWrslRtData",new Class[]{String.class, 
														   String.class, 
														   String.class,
														   String.class,
														   String.class,
														   String.class},
													   new Object[]{sStockId, 
													    			"", 
													    			sLoc.trim(),
													    			"",
													    			sPutYardGp,
													    			modifier});
			
		logger.println(LogLevel.DEBUG, this, "=======작업예약 생성 끝========");
						
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }
	}

	
	/**
	 * 오퍼레이션명 : 임가공 입고 실적 처리(C열연용) PIDEV
	 * param String : 저장품 List
        * param String : 야드구분
        * param String : 동구분
        * param String : 수조탱크보급(P)/대차출하상차(U) 구분
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public void callCoilFromToResult2PI(String sStockId,
								  	  String sYdGp,
							      	  String sLoc,
							      	  String modifier,
							      	  String sPI_YD)
	{
	
		Boolean isSuccess = new Boolean(false);
		try{
			
			int iSeq = 0;
			
			CraneSchDAO dao					= new CraneSchDAO();
			List productList = null;
			List productList2 = null;
			JDTORecord stlRecord = null;
			JDTORecord stlRecord2 = null;
			stlRecord = JDTORecordFactory.getInstance().create();
			stlRecord2 = JDTORecordFactory.getInstance().create();			
			ymCommonDAO dao2 = ymCommonDAO.getInstance();
			
			List 	   dmList  = null; // Card No,운송지시일자,순번이 같은 저장품정보
			JDTORecord dmRc    = null; // 저장품정보
			JDTORecord pmRc    = null; // 저장품정보
			List 	   pmList  = null;
			int		   iDmSize = 0;    // Card No,운송지시일자,순번이 같은 저장품 갯수
			int		   iPmSize = 0;
			int		   iDmFns  = 0;    // 상차완료인 저장품 갯수	
			int 		iReq	= 0;

			String sPutStackColGp 	= "";
			String sPutStackBedGp 	= "";
			String sPutStackLayerGp = "";
			String sPutYardGp		= "";
			
			JDTORecord wBookSel		= null;
			
			logger.println(LogLevel.DEBUG, this, "=======임가공  생성 시작========");
			
			EJBConnector ejbConn = new EJBConnector("default","JNDIYMYardWrkResReg",this);
			
				
			logger.println(LogLevel.DEBUG,this, "Stock_id===" + sStockId);
			logger.println(LogLevel.DEBUG,this, "sLoc===" + sLoc);
			
			
			/* 이송 완료 setting */
			iSeq = dao.updateFrToDoneInfo2(sStockId);
			
			String queryID2 = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
			productList2 	= dao2.getCommonList(queryID2, new Object[]{sStockId});
			stlRecord2 = (JDTORecord)productList2.get(0);
			
			String sCURR_PROG_CD =StringHelper.evl(stlRecord2.getFieldString("CURR_PROG_CD"), "");

			if(sCURR_PROG_CD.equals("C")){// 이미진행 된 경우 
				logger.println(LogLevel.DEBUG,this, "==>>코일 재료공통 변경이 완료 된 경우 skip처리  =============");
			} else {
//				//재료공통 테이블 업데이트
//				int intRtnVal = dao.requestupdateData2(sStockId);
//				if(intRtnVal <= 0){
//					logger.println(LogLevel.DEBUG,this, "==>>코일 재료공통 변경 작업 에러 =============");
//				}
				
	 		//실적처리 
    	 	EJBConnector ejbConn2 = new EJBConnector("default","JNDIYMCCLdWrkOrdReg",this);
    	 	Boolean isYd = (Boolean)ejbConn2.trx("CarinfoFrtoMoveBackupSub2",new  Class[]{String.class},
														new Object[]{sStockId});
				
			
		    String queryID 			= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
			productList 	= dao2.getCommonList(queryID, new Object[]{sStockId});
			stlRecord = (JDTORecord)productList.get(0);
			
            //코일소재임가공이송완료실적(YDPTJ003)
			tcRecord =JDTORecordFactory.getInstance().create();
			tcRecord.setField("JMS_TC_CD", "YDPTJ003");
			tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			tcRecord.setField("STL_NO",  StringHelper.evl(stlRecord.getFieldString("COIL_NO"), ""));
			// 주문번호
			tcRecord.setField("ORD_NO",  StringHelper.evl(stlRecord.getFieldString("ORD_NO"), ""));
			// 주문행번
			tcRecord.setField("ORD_DTL", StringHelper.evl(stlRecord.getFieldString("ORD_DTL"), ""));
			// 공장공정코드
			tcRecord.setField("PLNT_PROC_CD",  StringHelper.evl(stlRecord.getFieldString("PLNT_PROC_CD"), ""));
			// 재료외형구분
			tcRecord.setField("STL_APPEAR_GP",  StringHelper.evl(stlRecord.getFieldString("STL_APPEAR_GP"), ""));
			// 현재진도코드
			tcRecord.setField("CURR_PROG_CD",  StringHelper.evl(stlRecord.getFieldString("CURR_PROG_CD"), ""));
			// 주문여재구분
			tcRecord.setField("ORD_YEOJAE_GP", StringHelper.evl(stlRecord.getFieldString("ORD_YEOJAE_GP"), ""));
			// 재료중량 (SLAB중량) 
			tcRecord.setField("STL_WT",  StringHelper.evl(stlRecord.getFieldString("COIL_WT"), ""));
			// 설계재료중량(항목명?)
			tcRecord.setField("DS_MTL_WT", "");
			// 재료상태구분(항목명?)
			tcRecord.setField("MTL_STAT_GP",  StringHelper.evl(stlRecord.getFieldString("RECORD_PROG_STAT"), ""));
			// Record 종료구분
			tcRecord.setField("RECORD_END_GP",  StringHelper.evl(stlRecord.getFieldString("RECORD_END_GP"), ""));
			// Record 종료구분 1(항목명?)
			tcRecord.setField("RECORD_END_GP1", "");
			// 전진도 코드
			tcRecord.setField("BEFO_PROG_CD",  StringHelper.evl(stlRecord.getFieldString("BEFO_PROG_CD"), ""));
			// 전주문 번호
			tcRecord.setField("BEF_ORD_NO",  StringHelper.evl(stlRecord.getFieldString("BEF_ORD_NO"), ""));
			// 전주문 행번
			tcRecord.setField("BEF_ORD_DTL",  StringHelper.evl(stlRecord.getFieldString("BEF_ORD_DTL"), ""));
			// 모재료번호   
			tcRecord.setField("MMATL_FEE_NO",  StringHelper.evl(stlRecord.getFieldString("MMATL_FEE_NO"), ""));
			// 목전충당구분
			tcRecord.setField("ORDERTRANS_MATCH_GP", StringHelper.evl(stlRecord.getFieldString("MATCH_ORDERTRANS_GP"), ""));	
			isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
			  	  	 new Object[]{tcRecord});
			logger.println(LogLevel.DEBUG,this, "내부IF호출===B열연 SLAB 이송 하차 완료 시.===");
                   	
	
				
			/*
			 * 이송 상하차 실적 출하 전송
			 */
			
			/* 이송 완료 대상 조회 */
			pmList  = dao.getYmDmFrToInfo2PI(sStockId);
			iDmFns	= pmList.size();
			
			/* 이송 지시 대상 조회 */
			dmList  = dao.getYdDmCommonInfoPI(sStockId);
			iDmSize = dmList.size();			

				
			logger.println(LogLevel.DEBUG,this, "이송 지시 전체==" + iDmSize);
			logger.println(LogLevel.DEBUG,this, "이송 완료.=====" + iDmFns);
			if(iDmSize != 0 && iDmSize == iDmFns){ // 상,하차완료인 저장품이 ALL일때 처리
		    		
				dmRc   = (JDTORecord)dmList.get(0);
					
		    	String sCarCardNo     = StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), "");
		    	String sTransWordNo   = StringHelper.evl(dmRc.getFieldString("TRANS_WORD_NO"), "");
		    	String sCarNo   	  = StringHelper.evl(dmRc.getFieldString("CAR_NO"), "");
					
			    //일관제철 ##############################################################################################	
		    	
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                //임가공이송하차개시
     			JDTORecord tcRecordDM = null;
     			tcRecordDM = JDTORecordFactory.getInstance().create(); 
     			tcRecordDM.setField("UPCARUNLOAD_GP", "D"); 
                tcRecordDM.setField("CARD_NO",sCarCardNo);
                tcRecordDM.setField("CAR_NO", sCarNo);
                tcRecordDM.setField("YD_GP", sYdGp);		
                tcRecordDM.setField("TRANS_WORD_DATE",sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
                tcRecordDM.setField("TRANS_WORD_SEQNO", sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
     			
     			//인터페이스 전문 호출
                // getYDDMR020
     			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYMYardWrkResReg",this);
     			isSuccess = (Boolean)ejbConn1.trx("getM10YDLMJ1115",new Class[]{JDTORecord.class},
     			  	  	 new Object[]{tcRecordDM}); 
                logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 임가공이송상하차개시.===");
                //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                //임가공이송하차완료
     			tcRecordDM = null;
     			tcRecordDM = JDTORecordFactory.getInstance().create(); 
     			tcRecordDM.setField("UPCARUNLOAD_GP", "D");
                tcRecordDM.setField("CARD_NO",sCarCardNo);
                tcRecordDM.setField("CAR_NO", sCarNo);
                tcRecordDM.setField("YD_GP", sYdGp);		
                tcRecordDM.setField("TRANS_WORD_DATE",sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
                tcRecordDM.setField("TRANS_WORD_SEQNO",  sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
     			
     			//인터페이스 전문 호출
                // getYDDMR022
     			EJBConnector ejbConn3 = new EJBConnector("default","JNDIYMYardWrkResReg",this);
     			isSuccess = (Boolean)ejbConn3.trx("getM10YDLMJ1125",new Class[]{JDTORecord.class},
     			  	  	 new Object[]{tcRecordDM}); 
                logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 임가공이송상하차완료.===");
                //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			}
			} //if end skip
			
			
			
			/**
			 * 0. 입력한 To 위치 정합성 점검
			 */
			if (sLoc.length() == 10) {
				 sPutStackColGp = sLoc.substring(0, 6);
				 sPutStackBedGp = sLoc.substring(6, 8);
				 sPutStackLayerGp = sLoc.substring(8, 10);
			}else {
//				substr(:pos,1,1),-- 야드구분
//		        substr(:pos,2,1),-- 동
//		        substr(:pos,3,2),-- SPAN
//		        substr(:pos,5,2),-- 적치열번지
//		        substr(:pos,7,2),-- 적치번지
//		        substr(:pos,9,2),-- 적치단
				sLoc =	sYdGp + 
						sLoc.substring(0, 1) + 
						"00" + 
						"0" + sLoc.substring(1, 2) + 
						sLoc.substring(2, 4) + 
						"01";
			}
			
			
			/*
			 * 적치단 Put위치를 적치상태로 변경 tb_ym_stacklayer Table : stock_id = Coil
			 * No tb_ym_stacklayer Table : stack_layer_stat = 'L'(적치중)
			 */
			String sLayerStat = YmCommonConst.STACK_LAYER_STAT_L;
			iReq = dao.updateCraneStackLayerStat(sPutStackColGp, sPutStackBedGp, sPutStackLayerGp, sStockId, sLayerStat);
			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 저장위치  UPDATE = " + iReq);

			/*
			 * Coil 공통 Table 저장위치 Update
			 */
			iReq = dao.updateCoilCommonLocInfo(sStockId, sLoc);
			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 코일공통 UPDATE = " + iReq);
			
			/*
			 * 6. Crane 작업 실적 등록
			 */
			
			if (sLoc.length() > 1) {
				sPutYardGp = sLoc.substring(0, 1);
			}
			
			
			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYMCraneSchReg",this);
			ejbConn1.trx("insertUpPutWrslRtData",new Class[]{String.class, 
														   String.class, 
														   String.class,
														   String.class,
														   String.class,
														   String.class},
													   new Object[]{sStockId, 
													    			"", 
													    			sLoc.trim(),
													    			"",
													    			sPutYardGp,
													    			modifier});
			
		logger.println(LogLevel.DEBUG, this, "=======작업예약 생성 끝========");
						
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }
	}

	
	/**
	 * 오퍼레이션명 : 임가공 입고 실적 처리 
	 * param String : 저장품 List
        * param String : 야드구분
        * param String : 동구분
        * param String : 수조탱크보급(P)/대차출하상차(U) 구분
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public void callCoilFromToResultNEW(String sStockId,
								  	  String sYdGp,
							      	  String sLoc,
							      	  String modifier)
	{
		
		int iSeq = 0;
		
		CraneSchDAO dao					= new CraneSchDAO();
		List productList = null;
		List productList2 = null;
		JDTORecord stlRecord = null;
		JDTORecord stlRecord2 = null;
		stlRecord = JDTORecordFactory.getInstance().create();
		stlRecord2 = JDTORecordFactory.getInstance().create();			
		ymCommonDAO dao2 = ymCommonDAO.getInstance();
		
		List 	   dmList  = null; // Card No,운송지시일자,순번이 같은 저장품정보
		JDTORecord dmRc    = null; // 저장품정보
		JDTORecord pmRc    = null; // 저장품정보
		List 	   pmList  = null;
		int		   iDmSize = 0;    // Card No,운송지시일자,순번이 같은 저장품 갯수
		int		   iPmSize = 0;
		int		   iDmFns  = 0;    // 상차완료인 저장품 갯수	
		int 		iReq	= 0;

		Boolean isSuccess = new Boolean(false);
		try{
			logger.println(LogLevel.DEBUG, this, "=======임가공  생성 시작========");
			logger.println(LogLevel.DEBUG,this, "Stock_id===" + sStockId);
			
			/**
			 * 0. 입력한 To 위치 정합성 점검
			 */
			if (sLoc.length() == 10) {

			}else {
//				substr(:pos,1,1),-- 야드구분
//		        substr(:pos,2,1),-- 동
//		        substr(:pos,3,2),-- SPAN
//		        substr(:pos,5,2),-- 적치열번지
//		        substr(:pos,7,2),-- 적치번지
//		        substr(:pos,9,2),-- 적치단
				sLoc =	sYdGp + 
						sLoc.substring(0, 1) + 
						"00" + 
						"0" + sLoc.substring(1, 2) + 
						sLoc.substring(2, 4) + 
						"01";
			}
			
			
			logger.println(LogLevel.DEBUG,this, "sLoc===" + sLoc);
			
			
			String queryID2 = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
			productList2 	= dao2.getCommonList(queryID2, new Object[]{sStockId});
			stlRecord2 = (JDTORecord)productList2.get(0);
			
			String sCURR_PROG_CD =StringHelper.evl(stlRecord2.getFieldString("CURR_PROG_CD"), "");
			String sYD_GP =StringHelper.evl(stlRecord2.getFieldString("YD_GP"), "");
			
			
			/*
			 * STOCK TABLE 이송 완료 setting///////////////////////////////////
			 */
			if(sYD_GP.equals("H")||sYD_GP.equals("J")||sYD_GP.equals("X")){		
				//C열연
				iSeq = dao.updateFrToDoneInfo2(sStockId);
			}else{
				//AB열연
				iSeq = dao.updateFrToDoneInfo(sStockId);
			}

			
			/*
			 * COIL공통 TABLE 저장위치 Update////////////////////////////////////
			 */
			iReq = dao.updateCoilCommonLocInfo(sStockId, sLoc);
			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 코일공통 UPDATE = " + iReq);
			
			
			/*
			 * 임가공 이송하차개시완료 출하전송 및 실적처리 ////////////////////////////
			 */
			if(sCURR_PROG_CD.equals("C")){// 이미진행 된 경우 
				logger.println(LogLevel.DEBUG,this, "==>>코일 재료공통 변경이 완료 된 경우 skip처리  =============");
			} else {
				
		 		//실적처리 
	    	 	EJBConnector ejbConn2 = new EJBConnector("default","JNDIYMCCLdWrkOrdReg",this);
	    	 	Boolean isYd = (Boolean)ejbConn2.trx("CarinfoFrtoMoveBackupSub2",new  Class[]{String.class},
															new Object[]{sStockId});
					
				
			    String queryID 			= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
				productList 	= dao2.getCommonList(queryID, new Object[]{sStockId});
				stlRecord = (JDTORecord)productList.get(0);
				
	            //코일소재임가공이송완료실적(YDPTJ003)
				tcRecord =JDTORecordFactory.getInstance().create();
				tcRecord.setField("JMS_TC_CD", "YDPTJ003");
				tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
				tcRecord.setField("STL_NO",  StringHelper.evl(stlRecord.getFieldString("COIL_NO"), ""));
				// 주문번호
				tcRecord.setField("ORD_NO",  StringHelper.evl(stlRecord.getFieldString("ORD_NO"), ""));
				// 주문행번
				tcRecord.setField("ORD_DTL", StringHelper.evl(stlRecord.getFieldString("ORD_DTL"), ""));
				// 공장공정코드
				tcRecord.setField("PLNT_PROC_CD",  StringHelper.evl(stlRecord.getFieldString("PLNT_PROC_CD"), ""));
				// 재료외형구분
				tcRecord.setField("STL_APPEAR_GP",  StringHelper.evl(stlRecord.getFieldString("STL_APPEAR_GP"), ""));
				// 현재진도코드
				tcRecord.setField("CURR_PROG_CD",  StringHelper.evl(stlRecord.getFieldString("CURR_PROG_CD"), ""));
				// 주문여재구분
				tcRecord.setField("ORD_YEOJAE_GP", StringHelper.evl(stlRecord.getFieldString("ORD_YEOJAE_GP"), ""));
				// 재료중량 (SLAB중량) 
				tcRecord.setField("STL_WT",  StringHelper.evl(stlRecord.getFieldString("COIL_WT"), ""));
				// 설계재료중량(항목명?)
				tcRecord.setField("DS_MTL_WT", "");
				// 재료상태구분(항목명?)
				tcRecord.setField("MTL_STAT_GP",  StringHelper.evl(stlRecord.getFieldString("RECORD_PROG_STAT"), ""));
				// Record 종료구분
				tcRecord.setField("RECORD_END_GP",  StringHelper.evl(stlRecord.getFieldString("RECORD_END_GP"), ""));
				// Record 종료구분 1(항목명?)
				tcRecord.setField("RECORD_END_GP1", "");
				// 전진도 코드
				tcRecord.setField("BEFO_PROG_CD",  StringHelper.evl(stlRecord.getFieldString("BEFO_PROG_CD"), ""));
				// 전주문 번호
				tcRecord.setField("BEF_ORD_NO",  StringHelper.evl(stlRecord.getFieldString("BEF_ORD_NO"), ""));
				// 전주문 행번
				tcRecord.setField("BEF_ORD_DTL",  StringHelper.evl(stlRecord.getFieldString("BEF_ORD_DTL"), ""));
				// 모재료번호   
				tcRecord.setField("MMATL_FEE_NO",  StringHelper.evl(stlRecord.getFieldString("MMATL_FEE_NO"), ""));
				// 목전충당구분
				tcRecord.setField("ORDERTRANS_MATCH_GP", StringHelper.evl(stlRecord.getFieldString("MATCH_ORDERTRANS_GP"), ""));	
				
				
				EJBConnector ejbConn = new EJBConnector("default","JNDIYMYardWrkResReg",this);			
				isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
				  	  	 new Object[]{tcRecord});
				logger.println(LogLevel.DEBUG,this, "코일소재임가공이송완료실적(YDPTJ003)===");                   	
				
				/*
				 * 이송하차 상하차 실적 출하 전송///////////////////////////////////////////
				 */				
				/* 이송 완료 대상 조회 */
				pmList  = dao.getYmDmFrToInfo2(sStockId);
				iDmFns	= pmList.size();
				
				/* 이송 지시 대상 조회 */
				dmList  = dao.getYdDmCommonInfo(sStockId);
				iDmSize = dmList.size();			
				logger.println(LogLevel.DEBUG,this, "이송 지시 전체==" + iDmSize);
				logger.println(LogLevel.DEBUG,this, "이송 완료.=====" + iDmFns);
				if(iDmSize != 0 && iDmSize == iDmFns){ // 상,하차완료인 저장품이 ALL일때 처리
			    		
					dmRc   = (JDTORecord)dmList.get(0);
						
			    	String sCarCardNo     = StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), "");
			    	String sTransWordNo   = StringHelper.evl(dmRc.getFieldString("TRANS_WORD_NO"), "");
			    	String sCarNo   	  = StringHelper.evl(dmRc.getFieldString("CAR_NO"), "");
						
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	                //임가공이송하차개시
	     			JDTORecord tcRecordDM = null;
	     			tcRecordDM = JDTORecordFactory.getInstance().create(); 
	     			tcRecordDM.setField("UPCARUNLOAD_GP", "D"); 
	                tcRecordDM.setField("CARD_NO",sCarCardNo);
	                tcRecordDM.setField("CAR_NO", sCarNo);
	                tcRecordDM.setField("YD_GP", sYdGp);		
	                tcRecordDM.setField("TRANS_WORD_DATE",sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
	                tcRecordDM.setField("TRANS_WORD_SEQNO", sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
	     			
	     			//인터페이스 전문 호출
	     			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYMYardWrkResReg",this);
	     			isSuccess = (Boolean)ejbConn1.trx("getYDDMR020",new Class[]{JDTORecord.class},
	     			  	  	 new Object[]{tcRecordDM}); 
	                logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 임가공이송상하차개시.===");
	                //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	                
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	                //임가공이송하차완료
	     			tcRecordDM = null;
	     			tcRecordDM = JDTORecordFactory.getInstance().create(); 
	     			tcRecordDM.setField("UPCARUNLOAD_GP", "D");
	                tcRecordDM.setField("CARD_NO",sCarCardNo);
	                tcRecordDM.setField("CAR_NO", sCarNo);
	                tcRecordDM.setField("YD_GP", sYdGp);		
	                tcRecordDM.setField("TRANS_WORD_DATE",sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
	                tcRecordDM.setField("TRANS_WORD_SEQNO",  sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
	     			
	     			//인터페이스 전문 호출
	     			EJBConnector ejbConn3 = new EJBConnector("default","JNDIYMYardWrkResReg",this);
	     			isSuccess = (Boolean)ejbConn3.trx("getYDDMR022",new Class[]{JDTORecord.class},
	     			  	  	 new Object[]{tcRecordDM}); 
	                logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 임가공이송상하차완료.===");
	                //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				} //if end skip
			} //if end skip
			
			
			/*
			 * Crane 작업 실적 등록//////////////////////////////////////////////////////
			 */
			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYMCraneSchReg",this);
			ejbConn1.trx("insertUpPutWrslRtData",new Class[]{String.class, 
														   String.class, 
														   String.class,
														   String.class,
														   String.class,
														   String.class},
													   new Object[]{sStockId, 
													    			"", 
													    			sLoc.trim(),
													    			"",
													    			sYdGp,
													    			modifier});
			
		logger.println(LogLevel.DEBUG, this, "=======임가공  생성 종료========");
						
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }
	}
	
//	/**
//	 * 오퍼레이션명 : 임가공이송상하차완료.
//	 *
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param 
//	 * @return
//	 * @throws 
//	 */	 
//	public JDTORecord getYDDMR022(JDTORecord model) {
//		    JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
//		    try{
//		    tcRecord1.setField("JMS_TC_CD", "YDDMR022");
//		    tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//		    tcRecord1.setField("UPCARUNLOAD_GP", StringHelper.evl(model.getFieldString("UPCARUNLOAD_GP"), ""));
//		    tcRecord1.setField("CARD_NO",StringHelper.evl(model.getFieldString("CARD_NO"), ""));
//		    tcRecord1.setField("CAR_NO", StringHelper.evl(model.getFieldString("CAR_NO"), ""));		
//		    tcRecord1.setField("TRANS_WORD_DATE", StringHelper.evl(model.getFieldString("TRANS_WORD_DATE"), ""));
//		    tcRecord1.setField("TRANS_WORD_SEQNO", StringHelper.evl(model.getFieldString("TRANS_WORD_SEQNO"), ""));
//		    tcRecord1.setField("YD_GP", "");
//		    tcRecord1.setField("CARLOAD_END_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
//		    tcRecord1.setField("CARLOAD_END_TIME", YmCommonUtil.getCurDate("HHmmss"));			
//		    }
//			catch(Exception e){
//		    }
//	        return tcRecord1;
//	    }
	
	// 차량이적용 차량이  사용중인지 확인한다. 최규성 
	// true : 사용중 .
	// false: 미사용중.
	public boolean getStatMoveCar(String sQueryId, String sCarName) throws java.rmi.RemoteException
	{
		logger.println(LogLevel.DEBUG,this, "차량이적용 차량이 사용중인지 확인:getStatMoveCar()");
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		
		try {
			
			List listResult = new ArrayList();
			listResult = ydStockDAO.getListData(sQueryId, new Object[] {sCarName});
			
			if (listResult.size() > 0)
			{
				logger.println(LogLevel.DEBUG,this, "차량이적용 차량 결과:false = 사용중" );
				return false;
			}else {
				logger.println(LogLevel.DEBUG,this, "차량이적용 차량 결과:true = 미사용");
				return true;
			}
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
		

	}
	
	
	/**
	 * 오퍼레이션명 : 임가공 입고 실적 처리(아세아스틸) 
	 * param String : 저장품 List
        * param String : 야드구분
        * param String : 동구분
        * param String : 수조탱크보급(P)/대차출하상차(U) 구분
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean callCoilFromToResultASA(String sStockId,
								  	  String sYdGp,
							      	  String sLoc,
							      	  String modifier)
	{
		boolean isSuccess = true;
		try{
			CraneSchDAO dao					= new CraneSchDAO();
			int 		iReq	= 0; 
			
			logger.println(LogLevel.DEBUG, this, "=======임가공  생성 시작(asa)========");
			
				
			logger.println(LogLevel.DEBUG,this, "Stock_id===" + sStockId);
			logger.println(LogLevel.DEBUG,this, "sLoc===" + sLoc);
			
 
			/*
			 * Coil 공통 Table 저장위치 Update
			 */
			iReq = dao.updateCoilCommonLocInfo(sStockId, sLoc);
			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 코일공통 UPDATE = " + iReq);
			
		 
			/*
			 * 야드실적 Table 등록 
			 */
			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYMCraneSchReg",this);
			ejbConn1.trx("insertUpPutWrslRtData",new Class[]{String.class, 
														   String.class, 
														   String.class,
														   String.class,
														   String.class,
														   String.class},
													   new Object[]{sStockId, 
													    			"", 
													    			sLoc.trim(),
													    			"",
													    			sYdGp,
													    			modifier});
			
		logger.println(LogLevel.DEBUG, this, "=======임가공  생성 종료(asa)========");
						
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		
		return isSuccess;
	}


}

