package com.inisteel.cim.ym.workrequest.wrequestaccept.session;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonDB;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.YmCommonConst;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.util.StringHelper;
import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.parser.Level2Parser;
import com.inisteel.cim.common.util.ExceptionMessageUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
import com.inisteel.cim.common.jms.model.pm.ZZPM001;
import java.util.Hashtable;
import java.util.Vector;
import com.metis.rapi4j.ResultData;
import com.metis.rapi4j.RuleException;
import com.metis.rapi4j.RAPI4J;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="SSSpplyWrkOrdRegEJB" jndi-name="JNDISSSpplyWrkOrdReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class SSSpplyWrkOrdRegSBean extends BaseSessionBean{
	private Logger logger 			= null;
	private ymCommonDAO ymCommonDAO = null;
	private CraneSchDAO dao 		= null;
	private Hashtable retTable = new Hashtable();
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger 		= new Logger(config);
		ymCommonDAO = new ymCommonDAO();
		dao 		= new CraneSchDAO();
	}

 	/**
	 * 오퍼레이션명 : 
	 *
	 *  야드 LEVEL2로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
        *  전문내용을 JDTORecord로 파싱한다.
        *  업무 로직
        *	1.TC_CD - CS1PB02/CS1PB03/CS1PB08
        *	2.야드 LEVEL2로부터 Scarfing 보급/Take Out/추출  요구 정보를 수신
        *  	3.저장품 Table에서 저장품 이동 조건이 Slab Scarfing 지시대기 && Scarfing 보급 유무 'Y' 를 조회  Order by 산적 Lot 보급순서, Slab No
        *    	입측 : 2DSE01, 출측 : 2DSD01 
        *  	4.검색한 Slab가  작업예약이 되어 있다면 Error(Skip)
        *  	5.검색한 Slab로 현재 적치위치를 검색하여 
        *  	6.적치단 Table에 적치단상태(STACK_LAYER_STAT CHAR(1)) 작업요구상태='S'로 변경처리 한다.
        *	7.작업예약 Table에 “BSY_SM 입측 Line_In / Take Out 요구등록“  Schedule Code로 Insert 한다.
        *  	8.작업예약 Table의 ID를 저장품 Table에 Set하고 Update 한다.
        *  	9.Slab Schedule EJB를 Call 한다.
        *   
        *  SCARFING 보급	                  5S	                    Slab Scarfing Entry Line In	              SSLI
        *  SCARFING 추출	      저장품상태가 D이면 ES,   저장품상태가 E이면 FS 	Slab Scarfing Delivary Line Off	          SSLO
        *  SCARFING TAKEOUT	              9S	                    Slab Scarfing Delivary Take Out	          SSTU
	 *
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveSSSpplyWrkOrd(String sMessage) { 
       	 /*
			작업처리구분		        CHAR	01		'1’ : Line In (보급), ‘2’ : Take Out, ‘3’ : Line Off (추출)
			SLABNO		            CHAR	11		
			TakeOutPosition		    CHAR	3
			SLABNO 가 없으면 야드시스템에서 대상재를 선정해서 보급하고 
			SLABNO 가 있으면 Scarfing Level-2에서 특정 SLAB를 보급 해달라고 요청하는것임		 
         	*/
        
		logger.println(LogLevel.DEBUG,this,"Start-receiveSSSpplyWrkOrd()");
		
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
			
			int iSeq = 0;
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 	= level2Parser.parse(sMessage);
			String sQueryId 			= "";
			String ProcessID            	= StringHelper.evl(jDTORecord.getFieldString("작업처리구분"), "");
			String SlabNo               		= StringHelper.evl(jDTORecord.getFieldString("SLABNO"), "").trim();
			
			if ("".equals(SlabNo)){
				if (YmCommonConst.PROCESS_ID_1.equals(ProcessID)){
					/*
					 *	SLAB 정보 없이 보급요구가 오면 
					 *	스카핑 작업예약을 검색해서 스케쥴을 호출한다.
					 */
					sQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.selectScarfingWBookID_01";
					JDTORecord dataJr = ydStockDAO.requestgetData(sQueryId, 
															 new Object[]{YmCommonConst.NEW_SCH_WORK_KIND_SSLI });
					
					if(dataJr != null){
						String wbookId = StringHelper.evl(dataJr.getFieldString("WBOOK_ID"), "");
						
						//Slab Schedule EJB Call syCraneScheduleInfoInsert
						EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
						Boolean isTrue = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",
															  new  Class[]{String.class},
															  new Object[]{wbookId });														
						
					}
				}
			}else {
				
				JDTORecord stockJr = dao.getStockInfo(SlabNo);
				
				if(stockJr == null){
					logger.println(LogLevel.DEBUG,this, "스카핑=>저장품정보 존재안함="+SlabNo);
					throw new EJBServiceException("스카핑=>저장품정보 존재안함="+SlabNo);
				}
				
				if (ProcessID.equals(YmCommonConst.PROCESS_ID_2)){
						
					JDTORecord tmpJr = dao.getStackLayerInfoWithStockId(YmCommonConst.STACK_COL_GP_2ESE01,
												 						SlabNo);
					
					if(stockJr == null){
						logger.println(LogLevel.DEBUG,this, "스카핑=>입측에 저장품 정보 존재안함="+SlabNo);
						throw new EJBServiceException("스카핑=>입측에 저장품 정보 존재안함="+SlabNo);
					}
				}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
					
					JDTORecord tmpJr = dao.getStackLayerInfoWithStockId(YmCommonConst.STACK_COL_GP_2ESD01,
												 						SlabNo);
					
					if(stockJr == null){
						logger.println(LogLevel.DEBUG,this, "스카핑=>출측에 저장품 정보 존재안함="+SlabNo);
						throw new EJBServiceException("스카핑=>출측에 저장품 정보 존재안함="+SlabNo);
					}								 						
				}
				
				String tWbookId = StringHelper.evl(stockJr.getFieldString("WBOOK_ID"), "");

		    		if ("".equals(tWbookId)){
					
		    			sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectStackColGp";
					JDTORecord colJr  = ydStackLayerDAO.requestgetData(sQueryId, 
																 new Object[]{ SlabNo });

					if (colJr == null){
						logger.println(LogLevel.DEBUG,this, "스카핑=>MAP정보 존재안함="+SlabNo);
						throw new EJBServiceException("스카핑=>MAP정보 존재안함="+SlabNo);										
					}
					
					String sYdGp    = StringHelper.evl(colJr.getFieldString("STACK_COL_GP1"), "");     //야드구분
					String sBayGp   = StringHelper.evl(colJr.getFieldString("STACK_COL_GP2"), "");     //동구분
					
					String sSchCode     = "";
					
					if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){
						
						sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_SSLI;
					
					}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_2)){
						
						sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_SSTO;
					
					}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
						
						sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_SSLO;
					}
					
					sQueryId 	= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectYdWBook";
					JDTORecord wBookSel	= ydStackLayerDAO.requestFind(sQueryId);
					if (wBookSel == null){
						throw new EJBServiceException("작업예약 ID 생성 Error");
					}
													
					String sWbookId = StringHelper.evl(wBookSel.getFieldString("WBOOK_SELECT"), "");
					/*
					 *	작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 
					 *	작업예약조, 등록자, 등록일시) 한다.
					 */
		 		    	sQueryId 	= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
					iSeq 	= ydWBookDAO.requestinsertData(sQueryId, 
														new Object[]{ 
															           sWbookId, 
															           sYdGp, 
															           sBayGp, 
															           sSchCode, 
																    YmCommonUtil.getWorkDuty(), 
																    YmCommonUtil.getWorkParty()});
					
					/*
					 *	적치단  Table Update(작업요구상태='S'로 변경)
					 *	UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'S' WHERE STOCK_ID = ?
					 */
					sQueryId 	= "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
					iSeq 	= ydStackLayerDAO.requestupdateData(sQueryId, 
															new Object[]{ 
							       									YmCommonConst.STACK_LAYER_STAT_S, 
							       									SlabNo});							
					
					/* 
					 *  저장품 Table(TB_YM_STOCK)에 WBOOK_ID,STOCK_MOVE_TERM를 Update 한다.
					 */
					String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(SlabNo,"");
							     
					sQueryId 	= "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
					iSeq 	= ydStockDAO.requestupdateData(sQueryId, 
														new Object[]{ 
								    							sWbookId, 
								    							sStockInfo[1], 
								    							SlabNo});		
					
					if (ProcessID.equals(YmCommonConst.PROCESS_ID_2)||
					    ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
						
						//Slab Schedule EJB Call syCraneScheduleInfoInsert
						EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
						Boolean isTrue = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",
															  new  Class[]{String.class},
															  new Object[]{sWbookId });							
					}
					
				}	
		    					
			}
			
			logger.println(LogLevel.DEBUG,this, "End-receiveSSSpplyWrkOrd()");
			
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
	 * Scarfing 작업지시 재요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
    public boolean ScarfingReOrder(String sMessage) {
        
		logger.println(LogLevel.DEBUG,this,"Start-ScarfingReOrder()");
		
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
			
			String SlabNo               = StringHelper.evl(jDTORecord.getFieldString("SLABNO"), "");
			
			// 작업지시 Call
			callScarfingMsgInfo(SlabNo);
		
			logger.println(LogLevel.DEBUG,this,"End-ScarfingReOrder()");
			
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
	 * SCARFING 실적을 처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
    public boolean callScarfingWrsltInfo(String sMessage){
    	
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
			JDTORecord jtR = level2Parser.parse(sMessage);
			
			isSuccess = setScarfingWrsltInfo(jtR,"A");
			
			//if(isSuccess){
			/** 
			 *	YJK.20060615. 
			 *	공정 요구사항 주석처리		
			 */
			if(false){ 
				isSuccess = callInnerWorkInfo_Slab(jtR);
			}
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
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
    public boolean callScarfingWrsltInfo(JDTORecord jtR,String sGbn){
    	
    	boolean isSuccess = false;
    	try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			isSuccess = setScarfingWrsltInfo(jtR,sGbn);
			
			//if(isSuccess){
			/** 
			 *	YJK.20060615. 
			 *	공정 요구사항 주석처리		
			 */
			if(false){ 
				isSuccess = callInnerWorkInfo_Slab(jtR);
			}
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
	 * SCARFING 실적을 처리
        * 
	 * 전문코드		전문코드		CHAR	 7	CS1PB04
	 * 발생일자		발생일자		CHAR	10	YYYY-MM-DD
	 * 발생시간		발생시간		CHAR	 8	HH-MM-SS
	 * 전문구분		전문구분		CHAR	 1	I  : Initialize, U : Update D : Delete  R : Re-request
	 * 전문길이		전문길이		CHAR	 4		
	 * SLAB NO		SLABNO			CHAR	11		
	 * 두께			두께			CHAR	 7	㎜	소수점3자리 
	 * 폭			폭				CHAR	 6	㎜	소수점1자리
	 * 길이			길이			CHAR	 6	㎜	
	 * 중량			중량			CHAR	 5	kg	이론중량
	 * SLAB 실온도	SLAB실온도		CHAR	 4	Slab 실온도값 (현재는 온도계가 없어 지시와동일)
	 * SCARFING 유무(top)			CHAR	 1	무:0, 유:1
	 * SCARFING 유무(bottom)		CHAR	 1		
	 * SCARFING 유무(left)			CHAR	 1		
	 * SCARFING 유무(right)			CHAR	 1		
	 * SCARFING 유무(top corner)	CHAR	 1		
	 * SCARFING 유무(bottom corner)	CHAR	 1		
	 * SCARFING 깊이				CHAR	 2	㎜	예)  1:1mm , 2:2mm  ------  4:4mm
	 * 작업시작일시	작업시작일시	CHAR	14		
	 * 작업종료일시	작업종료일시	CHAR	14		
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
    public boolean setScarfingWrsltInfo(JDTORecord jtR,
    									String sGbn){
		
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iSeq = 0;
			
			String sTc 				= StringHelper.evl(jtR.getFieldString("전문코드"), "");
			String sSlabNo 			= StringHelper.evl(jtR.getFieldString("SLABNO"), "").trim();
			String sThick 			= StringHelper.evl(jtR.getFieldString("두께"), "");
			String sWidth 			= StringHelper.evl(jtR.getFieldString("폭"), "");
			String sLength 			= StringHelper.evl(jtR.getFieldString("길이"), "");
			String sWeight 			= StringHelper.evl(jtR.getFieldString("중량"), "");
			String SlabTemp			= StringHelper.evl(jtR.getFieldString("SLAB실온도"), "");
			String TopPattern		= StringHelper.evl(jtR.getFieldString("SCARFINGTop"), "");
			String BottomPattern	= StringHelper.evl(jtR.getFieldString("SCARFINGBottom"), "");
			String LeftPattern		= StringHelper.evl(jtR.getFieldString("SCARFINGLeft"), "");
			String RightPattern		= StringHelper.evl(jtR.getFieldString("SCARFINGRight"), "");
			String TCornerPattern	= StringHelper.evl(jtR.getFieldString("SCARFINGTopCorner"), "");
			String BCornerPattern	= StringHelper.evl(jtR.getFieldString("SCARFINGBottomCorner"), "");
			String sDepth 			= StringHelper.evl(jtR.getFieldString("SCARFING깊이"), "");
			String sSdate 			= StringHelper.evl(jtR.getFieldString("작업시작일시"), "");
			String sEdate 			= StringHelper.evl(jtR.getFieldString("작업종료일시"), "");
			String sScarfing		= StringHelper.evl(jtR.getFieldString("Scarfing유무"), "Y");
			String sSpeed 			= StringHelper.evl(jtR.getFieldString("속도"), "");
			
			
			logger.println(LogLevel.DEBUG,this, "sTc="				+ sTc);
			logger.println(LogLevel.DEBUG,this, "sSlabNo="			+ sSlabNo);
			logger.println(LogLevel.DEBUG,this, "sThick="			+ sThick);
			logger.println(LogLevel.DEBUG,this, "sWidth="			+ sWidth);
			logger.println(LogLevel.DEBUG,this, "sLength="			+ sLength);
			logger.println(LogLevel.DEBUG,this, "sWeight="			+ sWeight);
			logger.println(LogLevel.DEBUG,this, "SlabTemp="			+ SlabTemp);
			logger.println(LogLevel.DEBUG,this, "TopPattern="		+ TopPattern);
			logger.println(LogLevel.DEBUG,this, "BottomPattern="	+ BottomPattern);
			logger.println(LogLevel.DEBUG,this, "LeftPattern="		+ LeftPattern);
			logger.println(LogLevel.DEBUG,this, "RightPattern="		+ RightPattern);
			logger.println(LogLevel.DEBUG,this, "TCornerPattern="	+ TCornerPattern);
			logger.println(LogLevel.DEBUG,this, "BCornerPattern="	+ BCornerPattern);
			logger.println(LogLevel.DEBUG,this, "sDepth="			+ sDepth);
			logger.println(LogLevel.DEBUG,this, "sSdate="			+ sSdate);
			logger.println(LogLevel.DEBUG,this, "sEdate="			+ sEdate);
			logger.println(LogLevel.DEBUG,this, "sSpeed="			+ sSpeed);
			
			if(sSdate.compareTo(sEdate) > 0){
	    		throw new EJBServiceException("=SCARFING 실적=>작업일시에러.");
    		}
			
			if("".equals(sThick.trim())) sThick = "0000000";
			java.text.DecimalFormat df1 =  new java.text.DecimalFormat("####.###");
			String s1 = df1.format(Double.parseDouble(sThick.substring(0,4)+"."+sThick.substring(4)))+"";
			
			if("".equals(sWidth.trim())) sWidth = "000000";
			java.text.DecimalFormat df2 =  new java.text.DecimalFormat("#####.#");
			String s2 = df2.format(Double.parseDouble(sWidth.substring(0,5)+"."+sWidth.substring(5)))+"";
			
			if("".equals(sLength.trim())) sLength = "0000000";
			String s3 = Long.parseLong(sLength)+"";
			
			if("".equals(sWeight.trim())) sWeight = "00000";
			String s4 = Long.parseLong(sWeight)+"";
			
			logger.println(LogLevel.DEBUG,this,"=============SCARFING 실적 처리 시작========"); 
			
			YdStockDAO dao = new YdStockDAO();
			/**
		     *	1.	SCARFING 실적 UPDATE
		     *		Scarfing 실적을 수신하면 조업의 Scarfing 실적 Table에 Insert 한다.
		     */
				if("Y".equals(sScarfing)) 
				{
				    iSeq = dao.insertScarfingWrsltInfoNEW(sGbn,
			    									   sSlabNo,
			   										   s1,
			   										   s2,
			   										   s3,
			   										   s4,
			   										   SlabTemp,			
													   TopPattern,		
													   BottomPattern,	
													   LeftPattern,		
													   RightPattern,		
													   TCornerPattern,	
													   BCornerPattern,	
			   										   sDepth, 
			   										   sSpeed,
			   										   sSdate.length() > 8  ? sSdate.substring(0,8) : sSdate,
			   										   sSdate.length() > 8  ? sSdate.substring(8)   : "",
			   										   sEdate.length() > 8  ? sEdate.substring(0,8) : sEdate,
			   										   sEdate.length() > 8  ? sEdate.substring(8)   : "");
			    	
			    	logger.println(LogLevel.DEBUG,this,"==조업의 Scarfing 실적 Table에 Update Result="+iSeq);
			    	
			    	/*
			    	 * 2014.03.06 윤재광
			    	 * 스카핑 머신구분항목을 공통테이블에 UPDATE한다.
			    	 */
			    	iSeq = dao.updatePtMComScrfMcnoGpInfo(sSlabNo);
			    	iSeq = dao.updatePtSComScrfMcnoGpInfo(sSlabNo);
				}
				
		    	String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(sSlabNo,"");
			    String sProgCd   	= sStockInfo[0];
				String sStocMv   	= sStockInfo[1];
				
		    	iSeq = dao.updateStockTransInfo(sSlabNo,				      	  
												sStocMv);
				
				logger.println(LogLevel.DEBUG,this,"==SCARFING 실적 Table에 Update Result="+iSeq); 
												
		    	if(iSeq == 0){
		    		throw new EJBServiceException("=SCARFING 실적=>저장품정보 에러.");
	    		}
		    /**
		     *	0.	SLAB 공통 TABLE UPDATE
		     */
		     	/** 
				 *	YJK.20060615. 
				 *	공정 요구사항 주석처리		
				 */
		     	//iSeq = dao.updateSlabCommonScarfingInfo(sSlabNo);
		     	
		     	logger.println(LogLevel.DEBUG,this,"==SLAB 공통 Table에 Update Result="+iSeq); 
		     
		    /**
		     *	2.	SCARFING 야드 MAP 정보 셋팅
		     */
		     	 	/**
					 *	2.1.	입측삭제
					 */
			     	iSeq = YmCommonDB.deleteConveyorInfo(YmCommonConst.SCARFING_COL_2ESE+YmCommonConst.STACK_BED_GP_01,
				   						  				 sSlabNo);
			   	   	logger.println(LogLevel.DEBUG,this,"==입측삭제="+iSeq); 
		   	   	
		   	   //	if(iSeq > 0 ){ 	
			   	   	/**
					 *	2.2.	출측추가
					 */
			   	   	iSeq = YmCommonDB.insertConveyorInfo(YmCommonConst.SCARFING_COL_2ESD+YmCommonConst.STACK_BED_GP_01, 
				   						  				 sSlabNo,
				   						  				 YmCommonConst.GBN_MIN);
					logger.println(LogLevel.DEBUG,this,"==출측추가="+iSeq); 
				//}
				logger.println(LogLevel.DEBUG,this,"=============SCARFING 실적 처리 종료========");   
							            
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
	 *
	 *		SLAB INFO
        *		야드작업실적송신
        *		타업무에 내부인터페이스를 통해 전달한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */         
	public boolean callInnerWorkInfo_Slab(JDTORecord jtR){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			/*
			 *	SCARFING 실적 수신 후 공정에 실적 송신
			 * 
			 */
			{
				String sPlantGp = "";
				String sOrdNo	= "";
				String sOrdDtl	= "";
				String sSlabWt	= "";
				String sSlabNo 		= StringHelper.evl(jtR.getFieldString("SLABNO"), "").trim();
				
				JDTORecord stockV = dao.getSlabCommonInfo(sSlabNo);
				
				if(stockV != null){
					sPlantGp	= StringHelper.evl(stockV.getFieldString("PLANT_GP"), "B");
					sOrdNo		= StringHelper.evl(stockV.getFieldString("ORD_NO"), "");
					sOrdDtl		= StringHelper.evl(stockV.getFieldString("ORD_DTL"), "");
					sSlabWt		= StringHelper.evl(stockV.getFieldString("SLAB_WT"), "0");
				}
				
				ZZPM001 model = new ZZPM001();
				
				model.setTcCode("YMPM001");
				model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
				model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
					
				/* TC 발생 프로그램	varchar2(10)	*/	
				model.setTc_occur_pgm("YMPM001");
				
				/* TC 발생 일시	*/
				model.setTc_occur_ddtt(YmCommonUtil.getCurDate("yyyyMMddHHmmss"));
				
				/* 재료번호	varchar2(10)	*/
				model.setStl_no(sSlabNo);
				
				/* 공장구분	varchar2(1) */
				model.setPlant_gp(sPlantGp);
				
				/* 주문번호	varchar2(10)*/
				model.setOrd_no(sOrdNo);
				
				/* 주문행번	varchar2(3)	*/
				model.setOrd_dtl(sOrdDtl);
				
				/* 주문 여재 구분	varchar2(1) */
				model.setOrd_yeojae_gp("1");
				
				/* 재료 진도 CODE varchar2(1) */
				model.setStl_prog_cd("E");
				
				/* 재료 중량 number(8,1)  */
				model.setStl_wt(Double.parseDouble(sSlabWt));
				
				/* SCARFING 유무 varchar2(1)  */
				model.setScarfing_yn("Y");
				
				EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
															  	  	 new Object[]{model});
				
				logger.println(LogLevel.DEBUG,this, "내부IF호출===SCARFING 실적 수신 후에 발생.===");
			}
			
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
 	/**
	 * 오퍼레이션명 : 
	 *
	 * SCARFING 지시할 전문MESSAGE를 구성한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */            
	public boolean callScarfingMsgInfo(String sSlabNo){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
 
			logger.println(LogLevel.DEBUG,this,"=============SCARFING MESSAGE 처리 시작========"); 
		 
			JDTORecord cJr = dao.getSlabCommonInfo(sSlabNo);
			
			JDTORecord mJr = dao.getSlabHeatWrsltInfo(StringHelper.evl(cJr.getFieldString("HEAT_NO"), ""));
			
			//AB열연
			//JDTORecord sJr = dao.getScarfingPatternInfo(StringHelper.evl(cJr.getFieldString("SCARFING_PATTERN"), ""));
			
			//일관제철 (SCARFING_PATTERN -> WO_MSLAB_RPR_MTD)
			JDTORecord sJr = getRuleQMB518(StringHelper.evl(cJr.getFieldString("WO_MSLAB_RPR_MTD"), ""));
			
			String sMessage = setScarfingMsgInfo(sSlabNo,cJr,mJr,sJr);
			
			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			isSuccess = (Boolean)ejbConn.trx("CS1BP01send",new Class[]{String.class},new Object[]{ sMessage });	
		
			logger.println(LogLevel.DEBUG,this,"=============SCARFING MESSAGE 처리 종료========"); 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue(); 
	}
	
	/**
	 *	전문코드		전문코드	CHAR	7		
	 *	발생일자		발생일자	CHAR	10		YYYY-MM-DD
	 *	발생시간		발생시간	CHAR	8		HH-MM-SS
	 *	전문구분		전문구분	CHAR	1		I  : Initialize, U : Update D : Delete  R : Re-request
	 *	전문길이		전문길이	CHAR	4		
	 *	SLAB NO			SLABNO		CHAR	11		
	 *	구입 SLAB NO	구입SLABNO	CHAR	25		
	 *	강종			강종		CHAR	7		
	 *	Carbon함유량				CHAR	5	ppm	
	 *	두께			두께		CHAR	7	㎜	소수점3자리 
	 *	폭				폭			CHAR	6	㎜	소수점1자리
	 *	길이			길이		CHAR	6	㎜	
	 *	중량			중량		CHAR	5	kg	
	 *	SLAB 실온도		SLAB실온도	CHAR	4		slab 실온도값 (현재는 고정20도)
	 *	SCARFING 유무(top)			CHAR	1		무:0, 유:1         아래 스카핑패턴(품질설계) 참조
	 *	SCARFING 유무(bottom)		CHAR	1		
	 *	SCARFING 유무(left)			CHAR	1		
	 *	SCARFING 유무(right)		CHAR	1		
	 *	SCARFING 유무(top corner)	CHAR	1		
	 *	SCARFING 유무(bottom corner)CHAR	1		
	 *	SCARFING 깊이				CHAR	2	㎜	예)  1:1mm , 2:2mm  ------  4:4mm
	 * @param String 	 : SLAB NO
     * @param JDTORecord : SLAB COMMON INFO
     * @param JDTORecord : SLAB FRTOMOVE INFO
     *
     * @return
     * @throws 
     */	
	private String setScarfingMsgInfo(String sSlabNo,
									  JDTORecord cJr,
									  JDTORecord mJr,
									  JDTORecord sJr){
		
		StringBuffer sMsg = new StringBuffer();

		String TC					= "";//07	
		String sDate				= "";//10	
		String sTime				= "";//08	
		String Form					= "";//01	
		String Message_Length		= "";//04	
		String SlabNo				= "";//11	
		String BuySlabNo			= "";//25	
		String GangJong				= "";//10	
		String DataCarbon			= "";//05	
		String Thick				= "";//07	
		String Width				= "";//06	
		String Length				= "";//06	
		String Weight				= "";//05	
		String SlabTemp				= "";//04	
		String TopPattern			="0";//01	
		String BottomPattern		="0";//01	
		String LeftPattern			="0";//01	
		String RightPattern			="0";//01	
		String TCornerPattern		="0";//01	
		String BCornerPattern		="0";//01	
		String ScarfingDepth		= "";//02	
		
		String ScarfingPattern		= "";
		
		int iTC						=  7;	
		int iDate					= 10;	
		int iTime					=  8;	
		int iForm					=  1;	
		int iMessage_Length			=  4;	
		int iSlabNo					= 11;	
		int iBuySlabNo				= 25;	
		int iGangJong				= 10;	
		int iDataCarbon				=  5;
		int iThick					=  7;	
		int iWidth					=  6;	
		int iLength					=  6;	
		int iWeight					=  5;	
		int iSlabTemp				=  4;	
		int iTopPattern				=  1;	
		int iBottomPattern			=  1;	
		int iLeftPattern			=  1;	
		int iRightPattern			=  1;	
		int iTCornerPattern			=  1;	
		int iBCornerPattern			=  1;	
		int iScarfingDepth			=  2;	
		int iTotalLength			= 87;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			/*
			 *	1.	HEADER INFO
			 */
			TC					= YmCommonConst.TC_CS1BP01;
			sDate				= YmCommonUtil.getCurDate("yyyy-MM-dd");
			sTime				= YmCommonUtil.getCurDate("HH-mm-ss");
			Form				= "I";
			Message_Length		= iTotalLength+"";
			/*
			 *	2.	SLAB NO
			 */
			SlabNo	= sSlabNo;
			
			/*
			 *	3.	BUY SLAB NO
			 */
			BuySlabNo	= StringHelper.evl(cJr.getFieldString("BUY_SLAB_NO"), "");
			/*
			 *	4.	강종(규격약호)
			 */
			GangJong	= StringHelper.evl(cJr.getFieldString("SPEC_ABBSYM"), "");
			/*
			 *	12.	성분 DATA 카본
			 */
			if(mJr != null){ 
				DataCarbon	= StringHelper.replaceStr(
						  	  YmCommonUtil.format(StringHelper.evl(mJr.getFieldString("C_WRSLT"), ""),4,1),".","");	
			}
			/*
			 *	5.	두께
			 */
			Thick	= StringHelper.replaceStr(
					  YmCommonUtil.format(StringHelper.evl(cJr.getFieldString("SLAB_T"), ""),3,3),".","");
			/*
			 *	6.	폭
			 */
			Width	= StringHelper.replaceStr(
					  YmCommonUtil.format(StringHelper.evl(cJr.getFieldString("SLAB_W"), ""),4,1),".","");
			/*
			 *	7.	길이
			 */
			Length	= StringHelper.evl(cJr.getFieldString("SLAB_LEN"), "");
			
			/*
			 *	8.	중량
			 */
			Weight	= StringHelper.evl(cJr.getFieldString("SLAB_WT"), "");
			
			/*
			 *	9.	SCARFING PATTERN
			 */
			SlabTemp = "20";
			
			/*
			 *	10.	SCARFING PATTERN
			 */
			if(sJr != null){ 
				TopPattern			= StringHelper.evl(sJr.getFieldString("SCARFING_ORD_UP")			, "0");
				BottomPattern		= StringHelper.evl(sJr.getFieldString("SCARFING_ORD_DOWN")			, "0");
				LeftPattern			= StringHelper.evl(sJr.getFieldString("SCARFING_ORD_LEFT")			, "0");
				RightPattern		= StringHelper.evl(sJr.getFieldString("SCARFING_ORD_RIGHT")			, "0");
				TCornerPattern		= StringHelper.evl(sJr.getFieldString("SCARFING_ORD_LEFT_CORNER")	, "0");
				BCornerPattern		= StringHelper.evl(sJr.getFieldString("SCARFING_ORD_RIGHT_CORNER")	, "0");
			}
			/*
			 *	11.	SCARFING 깊이
			 */
			ScarfingDepth	= StringHelper.evl(cJr.getFieldString("SCARFING_DEPTH"), "");
	
			sMsg.append(YmCommonUtil.FillToString(TC				,iTC));
			sMsg.append(YmCommonUtil.FillToString(sDate				,iDate));
			sMsg.append(YmCommonUtil.FillToString(sTime				,iTime));
			sMsg.append(YmCommonUtil.FillToString(Form				,iForm));
			sMsg.append(YmCommonUtil.FillToNumber(Message_Length	,iMessage_Length));
			sMsg.append(YmCommonUtil.FillToString(SlabNo			,iSlabNo));
			sMsg.append(YmCommonUtil.FillToString(BuySlabNo			,iBuySlabNo));
			sMsg.append(YmCommonUtil.FillToString(GangJong			,iGangJong));
			sMsg.append(YmCommonUtil.FillToNumber(DataCarbon		,iDataCarbon));
			sMsg.append(YmCommonUtil.FillToNumber(Thick				,iThick));
			sMsg.append(YmCommonUtil.FillToNumber(Width				,iWidth));
			sMsg.append(YmCommonUtil.FillToNumber(Length			,iLength));
			sMsg.append(YmCommonUtil.FillToNumber(Weight			,iWeight));
			sMsg.append(YmCommonUtil.FillToNumber(SlabTemp			,iSlabTemp));
			sMsg.append(YmCommonUtil.FillToString(TopPattern		,iTopPattern));
			sMsg.append(YmCommonUtil.FillToString(BottomPattern		,iBottomPattern));
			sMsg.append(YmCommonUtil.FillToString(LeftPattern		,iLeftPattern));
			sMsg.append(YmCommonUtil.FillToString(RightPattern		,iRightPattern));
			sMsg.append(YmCommonUtil.FillToString(TCornerPattern	,iTCornerPattern));
			sMsg.append(YmCommonUtil.FillToString(BCornerPattern	,iBCornerPattern));
			sMsg.append(YmCommonUtil.FillToString(ScarfingDepth		,iScarfingDepth));
			
			
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sMsg.toString();
	}
	
	
	 /**
	 *
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2009-07-10 11:46:19)
	 * @param	scarfing_pattern	주편손질방법
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 * @return 정상처리 여부
	 */
	public JDTORecord getRuleQMB518(String scarfing_pattern) throws DAOException {
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();

		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			String sRtnCol [] = new String[] {
				
					 "SCARFING_ORD_DEEF"	
					,"SCARFING_ORD_TEMP"
					,"SCARFING_ORD_UP"
					,"SCARFING_ORD_DOWN"		
					,"SCARFING_ORD_LEFT"	
					,"SCARFING_ORD_RIGHT"	
					,"SCARFING_ORD_LEFT_CORNER"	
					,"SCARFING_ORD_RIGHT_CORNER"	
	
//					 *			<li>QMB518[0] :Scarfing깊이
//					 *			<li>QMB518[1] :Scarfing온도
//					 *			<li>QMB518[2] :Scarfing지시상
//					 *			<li>QMB518[3] :Scarfing지시하
//					 *			<li>QMB518[4] :Scarfing지시좌
//					 *			<li>QMB518[5] :Scarfing지시우
//					 *			<li>QMB518[6] :Scarfing지시좌Corner
//					 *			<li>QMB518[7] :Scarfing지시우Corner
			};
			
			this.QMB518(retTable,scarfing_pattern);
			
			jrReturn= this.convToJDTORecord("QMB518", sRtnCol, retTable);
			
		
		} catch(Exception e) {
			logger.println(LogLevel.ERROR, this, ExceptionMessageUtil.getStackTrace(e)) ;
			throw new DAOException(e); 
		}
		
		return jrReturn;
		
	}
	
	
	/**
	 *
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2009-07-10 11:46:19)
	 * @param	item1	주편손질방법
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>QMB518[0] :Scarfing깊이
	 *			<li>QMB518[1] :Scarfing온도
	 *			<li>QMB518[2] :Scarfing지시상
	 *			<li>QMB518[3] :Scarfing지시하
	 *			<li>QMB518[4] :Scarfing지시좌
	 *			<li>QMB518[5] :Scarfing지시우
	 *			<li>QMB518[6] :Scarfing지시좌Corner
	 *			<li>QMB518[7] :Scarfing지시우Corner
	 *			<li>QMB518_ColCnt :8
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean QMB518(Hashtable table,
	                        String item1 // 주편손질방법
	                      ) throws RuleException {     
	        Vector vt = new Vector();
	        int rc = 0;

	        RAPI4J  RCaller=null;
	        try {
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return false;
				}
				
	           RCaller = new RAPI4J( false,  "" );
	            RCaller.Initialize("QMB518");
	            /* 사용자 입력값 설정 시작 */ 
	            RCaller.AddItemCount(1); 
	            RCaller.AddItemString( item1);
	            /* 사용자 입력값 설정  */ 
	            if (!RCaller.MBRS_Call(2)){                                                    						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	            }																		 									
	            byte resColTypes[] = new byte[RCaller.getColCount()];                     
	            for (int j = 0; j < RCaller.getColCount(); j++) {                            				
	                resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
	            }                     																						
	            ResultData    result=new ResultData();	
	            result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
	            for (int i = 0; i < RCaller.getRowCount(); i++) { 
	                //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//Scarfing깊이
	                result.add( i ,RCaller.ReadString() );	//Scarfing깊이
	                //System.out.println("  ROW[" + i + "] COL[2]:"+ RCaller.ReadInt() );	//Scarfing온도
	                result.add(  i ,new Integer(RCaller.ReadInt()) );	//Scarfing온도
	                //System.out.println("  ROW[" + i + "] COL[3]:"+ RCaller.ReadString());	//Scarfing지시상
	                result.add( i ,RCaller.ReadString() );	//Scarfing지시상
	                //System.out.println("  ROW[" + i + "] COL[4]:"+ RCaller.ReadString());	//Scarfing지시하
	                result.add( i ,RCaller.ReadString() );	//Scarfing지시하
	                //System.out.println("  ROW[" + i + "] COL[5]:"+ RCaller.ReadString());	//Scarfing지시좌
	                result.add( i ,RCaller.ReadString() );	//Scarfing지시좌
	                //System.out.println("  ROW[" + i + "] COL[6]:"+ RCaller.ReadString());	//Scarfing지시우
	                result.add( i ,RCaller.ReadString() );	//Scarfing지시우
	                //System.out.println("  ROW[" + i + "] COL[7]:"+ RCaller.ReadString());	//Scarfing지시좌Corner
	                result.add( i ,RCaller.ReadString() );	//Scarfing지시좌Corner
	                //System.out.println("  ROW[" + i + "] COL[8]:"+ RCaller.ReadString());	//Scarfing지시우Corner
	                result.add( i ,RCaller.ReadString() );	//Scarfing지시우Corner
	            } 
	            table.put("QMB518_ColCnt", new Integer(resColTypes.length));					
	            table.put("QMB518", result);																					
	            if (result.size() == 0) {																								
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	         																																
	    }

	   
	    
		public JDTORecord convToJDTORecord(String sRullID, String sRtnCol[],  Hashtable srcTable ) throws DAOException {
			JDTORecord jrReturn = JDTORecordFactory.getInstance().create();

			try{
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return null;
				}
				
				ResultData rData = (ResultData)srcTable.get(sRullID);
				
				for (int jj = 0 ; jj < rData.getColumnCount(); jj++) {
					
					if (jj > sRtnCol.length ) {
						jrReturn.setField(jj + "" , rData.get(0, jj));
					} else {
						jrReturn.setField(sRtnCol[jj], rData.get(0, jj));
					}
				}
				
				if ( rData.size() > 0) {
					jrReturn.setResultCode("SUCCESS") ; 
				} else {
					jrReturn.setResultCode("FAILURE") ;
					jrReturn.setResultMsg((String)srcTable.get("CHECK_VAL")) ; 
				}
				
				return jrReturn;
			
			} catch(Exception e) {
				logger.println(LogLevel.ERROR, this, ExceptionMessageUtil.getStackTrace(e)) ;
				throw new DAOException(e); 
				
			}
			
			
		}

	
}


	

