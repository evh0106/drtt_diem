package com.inisteel.cim.ym.steelinfo.worderrecv.session;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordImplMap;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.util.CommonUtil;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.jms.model.pc.ZZPC001;
import com.inisteel.cim.common.jms.model.ym.DMYM001;
import com.inisteel.cim.common.jms.model.ym.DMYM002;
import com.inisteel.cim.common.jms.model.ym.DMYM003;
import com.inisteel.cim.common.jms.model.ym.DMYM004;
import com.inisteel.cim.common.jms.model.ym.DMYM008;
import com.inisteel.cim.common.jms.model.ym.PCYM001;
import com.inisteel.cim.common.jms.model.ym.PCYM002;
import com.inisteel.cim.common.jms.model.ym.PMYM001;
import com.inisteel.cim.common.jms.model.ym.PMYM002;
import com.inisteel.cim.common.jms.model.ym.PMYM004;
import com.inisteel.cim.common.jms.model.ym.PMYM006;
import com.inisteel.cim.common.jms.model.ym.PMYM007;
import com.inisteel.cim.common.jms.model.ym.PMYM008;
import com.inisteel.cim.common.jms.model.ym.POYM003;
import com.inisteel.cim.common.jms.model.ym.PSYM001;
import com.inisteel.cim.common.jms.model.ym.PSYM002;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.ym.ilkwan.dao.YdMtlStatModHistDao;
import com.inisteel.cim.ym.ilkwan.dao.YdStockDao;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.ModelWarning;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="WorkOrderInfoRegEJB" jndi-name="JNDIWorkOrderInfoReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class WorkOrderInfoRegSBean extends BaseSessionBean {
    private Logger logger 			= null;
    private ymCommonDAO ymCommonDAO = null;
    private String szSessionName = "JNDIWorkOrderInfoReg";
	private YmComm ymComm = new YmComm();
	public void ejbCreate() {
        LogServiceConfig config = 
            LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
        logger = new Logger(config);
        ymCommonDAO = new ymCommonDAO();
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 화면에서 요청한 동간보급 작업예약 한다.
	 * param stockId	저장품LIST
	 * param storeLoc	적치LIST
	 * param schKind	스케쥴코드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public void zoinWork(String stockId, String storeLoc, String schKind) {
        logger.println(LogLevel.DEBUG, this, "수신MSG: "+ schKind);
        logger.println(LogLevel.DEBUG, this, "수신MSG: "+ stockId);
        try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
            //슬라브번호 SPLIT
            List stocks 	= getZoinList(stockId.split("-"));
            List fromLocs 	= getZoinList(storeLoc.split("-"));
            //작업예약
            List wbookIds = createWBook(stocks, fromLocs, schKind);
            //SCH CALL
            callSchedule(wbookIds);                
            //관제송신
            if(getPCStat(schKind) != null) {
                sendPCStat(stocks, getPCStat(schKind));
            }
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	}


	/**
	 * 오퍼레이션명 : 
	 *
	 * 화면에서 요청한 시편재/핸드스카핑 보급 작업예약 한다.
	 * param stockId	저장품LIST
	 * param storeLoc	적치LIST
	 * param gp		등록/취소구분
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public void scarfingWork(String stockId, String storeLoc, String pattern) {
        logger.println(LogLevel.DEBUG, this, "수신MSG: "+ stockId);
        try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
            //슬라브번호 SPLIT
            List stocks 	= getZoinList(stockId.split("-"));
            List fromLocs 	= getZoinList(storeLoc.split("-"));
            List patterns	= getZoinList(pattern.split("-"));
            
            List wbookIds = createScarfing(stocks, fromLocs, patterns);
            
            callSchedule(wbookIds);     
            
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 화면에서 요청한 시편재/핸드스카핑 보급 작업예약 한다.
	 * param stockId	저장품
	 * param gp		시편(S)/핸드(H)구분
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	 
	public String scarfingOut(String sStockId, 
							  String gbn) {
        
        String sMessage = "추출 작업예약을 생성했습니다";
        try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
        	CraneSchDAO dao	= new CraneSchDAO();
        	
        	String sSchCode 	= "";
        	String sLayerStat 	= "";
        	String sColGp	 	= YmCommonConst.STACK_COL_GP_2E0113;
        	
	        /*
	         *	1.	야드 MAP정보를 체크
	         */
	        {
	        	JDTORecord slabJr = dao.getStackLayerInfoWithStockId(sColGp,
	        													 	 sStockId);
				
				if(slabJr == null){
					sMessage = "=시편재/핸드스카핑 추출=>저장품정보 존재안함.";
					return sMessage;
				}	
				
				sLayerStat = StringHelper.evl(slabJr.getFieldString("STACK_LAYER_STAT"),"");
				
				if(YmCommonConst.STACK_LAYER_STAT_S.equals(sLayerStat)){
					sMessage = "=시편재/핸드스카핑 추출=>저장품이 작업예약이 존재함.";
					return sMessage;
				}else if(YmCommonConst.STACK_LAYER_STAT_U.equals(sLayerStat)){
					sMessage = "=시편재/핸드스카핑 추출=>저장품이 스케쥴 등록 상태임.";
					return sMessage;
				}else if(YmCommonConst.STACK_LAYER_STAT_P.equals(sLayerStat)){
					sMessage = "=시편재/핸드스카핑 추출=>저장품이 스케쥴 등록 상태임.";
					return sMessage;
				}							 	
            }
            
            /*
	         *	2.	SLAB INFO 체크
	         */
	        {
	        	
	        	String sCurrProgCd			= "";
				String sReagentPickYn   	= "";
				String sReagentPickDoneYn  	= "";
				String sScarfingYn   		= "";
				String sScarfingDoneYn   	= "";
				String sScarfingPattern   	= "";
					
		        JDTORecord slabJr = dao.getSlabCommonInfo(sStockId);
		        	
	        	if(slabJr != null){
		    		sCurrProgCd	   		= StringHelper.evl(slabJr.getFieldString("CURR_PROG_CD"), "");
		    		sReagentPickYn   	= StringHelper.evl(slabJr.getFieldString("REAGENTPICK_TARGET_YN"), "");
		    		sReagentPickDoneYn  = StringHelper.evl(slabJr.getFieldString("REAGENTPICK_DONE_YN"), "");
		    		sScarfingYn   		= StringHelper.evl(slabJr.getFieldString("SCARFING_YN"), "");
		    		sScarfingDoneYn   	= StringHelper.evl(slabJr.getFieldString("SCARFING_DONE_YN"), "");
		    		sScarfingPattern   	= StringHelper.evl(slabJr.getFieldString("SCARFING_PATTERN"), "");
		    	}
		    	
		    	logger.println(LogLevel.DEBUG, this, "시편재/핸드스카핑재 체크 sCurrProgCd			="+sCurrProgCd);
		    	logger.println(LogLevel.DEBUG, this, "시편재/핸드스카핑재 체크 sReagentPickYn		="+sReagentPickYn);
		    	logger.println(LogLevel.DEBUG, this, "시편재/핸드스카핑재 체크 sReagentPickDoneYn	="+sReagentPickDoneYn);
		    	logger.println(LogLevel.DEBUG, this, "시편재/핸드스카핑재 체크 sScarfingYn			="+sScarfingYn);
		    	logger.println(LogLevel.DEBUG, this, "시편재/핸드스카핑재 체크 sScarfingDoneYn		="+sScarfingDoneYn);
		    	logger.println(LogLevel.DEBUG, this, "시편재/핸드스카핑재 체크 sScarfingPattern		="+sScarfingPattern);
	        	
	        	/*
	        	 *	2.1	시편재인 경우 체크
	        	 */
	        	if("S".equals(gbn)){
	            	// 추후 처리 로직 반영
	            /*
	        	 *	2.2	핸드 스카핑재인 경우 체크
	        	 */	
	            }else if("H".equals(gbn)){
	            	// 추후 처리 로직 반영
	            }
	        }
            
            /*
	         *	3.	스케쥴 코드 셋팅
	         */
            if("S".equals(gbn)){
            	sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_SRPO;	//시편재 추출	
            }else if("H".equals(gbn)){
            	sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_SHSO;	//핸드스카핑 추출
            }
            
            /*
	         *	4.	작업예약 생성.
	         */
            List wbookIds = extractScarfing(sStockId, 
				            				sSchCode, 
				            				sColGp);
            
            callSchedule(wbookIds);     
            
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return sMessage;
	}
	
	/**
     * @param stocks
     */
    private void cancelScarfing(List stocks, List from) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        JDTORecord dto = null;
        String[] fromLoc = null;
        int stocksCnt = stocks != null ? stocks.size() : 0;
        for(int i = 0; i < stocksCnt; i++) {
            fromLoc = ((String)from.get(i)).split(" ");
            dto = ymCommonDAO.readSlabInfo((String)stocks.get(i));
            ymCommonDAO.removeWBook(getField(dto, "WBOOK_ID"));
            ymCommonDAO.modifyTermAndWBookIdOfStock(
                    "", 
                    YmCommonUtil.getSlabCurrProgCd((String)stocks.get(i),"")[1],
                    YmCommonConst.SCARFING_N,
                    (String)stocks.get(i));
            ymCommonDAO.modifyLayerStateOfLayer(
                    YmCommonConst.STACK_LAYER_STAT_L, 
                    fromLoc[0], 
                    fromLoc[1],
                    fromLoc[2]);
        }        
    }

    /**
     * @param schKind
     * @return
     */
    private String getPCStat(String schKind) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        if(YmCommonConst.NEW_SCH_WORK_KIND_STSL.equals(schKind) ||
           YmCommonConst.NEW_SCH_WORK_KIND_STML.equals(schKind)||
           YmCommonConst.NEW_SCH_WORK_KIND_STM2.equals(schKind)) {
            return "10";
        }else if(YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(schKind)) {
            return "40";
        }else if(YmCommonConst.NEW_SCH_WORK_KIND_SCLI.equals(schKind)) {
            return "50";
        }
        return null;
    }

    /**
	 * 관제에 장입을 위한 대차 작업임을 알린다.
     * @param stocks
     */
    private void sendPCStat(List stocks) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        sendPCStat(stocks, "10");
    }

	/**
	 * 관제에 장입을 위한 대차 작업임을 알린다.
     * @param stocks
     */
    private void sendPCStat(List stocks, String stat) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
    	/*
    	 * 	2009.07 YJK 생산통제 장입진행정보 기존모듈 삭제.
    	 * 
	        int stocksCnt = stocks != null ? stocks.size() : 0;
	        String ymd = YmCommonUtil.getStringYMD("-");
	        String hms = YmCommonUtil.getStringHMS("-");
		    ZZPC001 model 			= null; 
	    	EJBConnector ejbConn 	= null;
	    	try {
	    	    ejbConn = new EJBConnector("default", "JNDIYardWrkResReg", this);
		        for(int i = 0; i < stocksCnt; i++) {
		            model = new ZZPC001();
		            model.setTcCode(YmCommonConst.MODEL_YMPC100);
		            model.setTcDate(ymd);
		            model.setTcTime(hms);
		            model.setrealStlNo((String)stocks.get(i));
		            model.setplanStlNo("");
		            model.seteventStat(stat);
		            model.seteventOccurDDTT("");
		            ejbConn.trx("sendInternalModel", 
		                    new Class[]{ CommonModel.class }, new Object[]{ model });
		        }
	        }catch (Exception e) {
	            e.printStackTrace();
	        }
	      *   
	      */
    }

    /**
     * @param wbookIds
	 * @throws Exception
	 * @throws RemoteException
     */
    private void callSchedule(List wbookIds) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        StringBuffer wbids 	= new StringBuffer();
        int wbookIdsCnt		= wbookIds != null ? wbookIds.size() : 0;
        for(int i = 0; i < wbookIdsCnt; i++) {
            wbids.append((String)wbookIds.get(i)).append("-");
        }
        EJBConnector conn = new EJBConnector("default", "JNDICraneSchReg", this);
        conn.trx(
                "syCraneScheduleInfoInsert",
                new Class[]{ String.class },
                new Object[]{ wbids.toString() });
        wbids.setLength(0);
    }
    
    /**
     * @param stocks
     * @param fromLocs
     * @return
     */
    private ArrayList createWBook(List stocks, List fromLocs) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        ArrayList arr = new ArrayList();
        int stocksCnt = stocks != null ? stocks.size() : 0; 
        for(int i = 0; i < stocksCnt; i++) { 
            String nextWBookId = ymCommonDAO.createWBook(
                    ((String)fromLocs.get(i)).substring(0, 6), 
                    YmCommonConst.NEW_SCH_WORK_KIND_STSL,
                    YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_S, 
                    "");
            ymCommonDAO.modifyTermAndWBookIdOfStock(
                    nextWBookId, 
                    YmCommonUtil.getSlabCurrProgCd((String)stocks.get(i),"")[1], 
                    (String)stocks.get(i));
            ymCommonDAO.modifyLayerStatOfLayer(
                    YmCommonConst.STACK_LAYER_STAT_S, 
                    ((String)fromLocs.get(i)).substring(0, 6), 
                    (String)stocks.get(i));
            arr.add(nextWBookId);
        }

        return arr;
    }
    
    /**
     * @param stocks
     * @param fromLocs
     * @return
     */
    private ArrayList createWBook(List stocks, List fromLocs, String schKind) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        ArrayList arr = new ArrayList();
        int stocksCnt = stocks != null ? stocks.size() : 0; 
        
        for(int i = 0; i < stocksCnt; i++) { 
            String nextWBookId = ymCommonDAO.createWBook(
                    ((String)fromLocs.get(i)).substring(0, 6), 
                    schKind,
                    YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_S, 
                    "");
                    
            ymCommonDAO.modifyTermAndWBookIdOfStock(
                    nextWBookId, 
                    YmCommonUtil.getSlabCurrProgCd((String)stocks.get(i),"")[1], 
                    (String)stocks.get(i));                
            
            ymCommonDAO.modifyLayerStatOfLayer(
                    YmCommonConst.STACK_LAYER_STAT_S, 
                    ((String)fromLocs.get(i)).substring(0, 6), 
                    (String)stocks.get(i));
            arr.add(nextWBookId);
        }

        return arr;
    }
    
    /**
     *	화면을 통해서 시편재 및 핸드스카핑 보급요구
     *	작업예약을 생성한다.
     *	
     * @param stocks
     * @param fromLocs
     * @param pattern  - 스카핑패턴
     *		SP = 시편재.
     * @return
     */
    private ArrayList createScarfing(List stocks, List fromLocs, List pattern) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        ArrayList arr = new ArrayList();
        int stocksCnt = stocks != null ? stocks.size() : 0; 
        
        String sPattern 	= "";
        String nextWBookId 	= "";
        
        for(int i = 0; i < stocksCnt; i++) { 
        	
        	sPattern = (String)pattern.get(i);
        	if("G".equals(sPattern)){
        		
        		nextWBookId = ymCommonDAO.createWBook(
			                    ((String)fromLocs.get(i)).substring(0, 6), 
			                    YmCommonConst.NEW_SCH_WORK_KIND_SHSI,
			                    YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_O, 
			                    YmCommonConst.STACK_COL_GP_2E0113);
	        
	        }else if("S".equals(sPattern)){
        		
	        	nextWBookId = ymCommonDAO.createWBook(
	                    ((String)fromLocs.get(i)).substring(0, 6), 
	                    YmCommonConst.NEW_SCH_WORK_KIND_SHSI,
	                    YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_S, 
	                    "");
	        } 

        	else if("SP".equals(sPattern)){
        		
        		nextWBookId = ymCommonDAO.createWBook(
			                    ((String)fromLocs.get(i)).substring(0, 6), 
			                    YmCommonConst.NEW_SCH_WORK_KIND_SRPI,
			                    YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_O, 
			                    YmCommonConst.STACK_COL_GP_2E0113);
	        }else{
	        	
	        	nextWBookId = ymCommonDAO.createWBook(
			                    ((String)fromLocs.get(i)).substring(0, 6), 
			                    YmCommonConst.NEW_SCH_WORK_KIND_SSLI,
			                    YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_S, 
			                    "");
	    	}
	    	
            ymCommonDAO.modifyTermAndWBookIdOfStock(
                    nextWBookId, 
                    YmCommonUtil.getSlabCurrProgCd((String)stocks.get(i),"")[1], 
                    (String)stocks.get(i));                
        
            ymCommonDAO.modifyLayerStatOfLayer(
                    YmCommonConst.STACK_LAYER_STAT_S, 
                    ((String)fromLocs.get(i)).substring(0, 6), 
                    (String)stocks.get(i));
            arr.add(nextWBookId);
        }

        return arr;
    }
    
    private ArrayList extractScarfing(String sStockId,
	    							  String sSchCode,
	    							  String sFromLoc) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        
        ArrayList arr = new ArrayList();
        
    	String nextWBookId = ymCommonDAO.createWBook(
		                    	sFromLoc, 
		                    	sSchCode,
		                    	YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_S, 
		                    	"");

        ymCommonDAO.modifyTermAndWBookIdOfStock(
                nextWBookId, 
                YmCommonUtil.getSlabCurrProgCd(sStockId,"")[1], 
                sStockId);                
    
        ymCommonDAO.modifyLayerStatOfLayer(
                YmCommonConst.STACK_LAYER_STAT_S, 
                sFromLoc, 
                sStockId);
        
        arr.add(nextWBookId);   
        
        return arr;     
    }
    
    /**
     * @param strings
     * @return
     */
    private ArrayList getZoinList(String[] list) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        int listCnt		= list != null ? list.length : 0;
        ArrayList arr 	= new ArrayList();
        for(int i = 0; i < listCnt; i++) {
            arr.add(list[i]);
        }
        return arr;
    }
    

	/**
	 * 오퍼레이션명 : 
	 *
	 * SLAB 차량 항만 출발 정보 (PSYM001)
	 * 
        * 항만시스템으로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: PSYM001
        * 2.I/F ID	: YM-LIF-044
	 * 1	전문코드	TC			CHAR	07		
	 * 2	발생일자	Date		CHAR	10		YYYY-MM-DD
	 * 3	발생시간	Time		CHAR	08		HH-MM-SS
	 * 4	YARD 구분	YD_GP	CHAR	1		1:A열연Coil, 2:B열연Slab, 3:B열연Coil
	 * 5	이송상차지시 일자	TransLoadOrderDate	CHAR	8		
	 * 6	이송상차지시 순번	TransLoadOrderSeq	CHAR	4
	 * 7	카드 번호 	cardNo		CHAR	4
	 * 8    pallet 번호 	palletNo	CHAR	2
	 * 9	구분 		dataGp		CHAR	1	1: 정상, 2: 취소               	
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	 
    public boolean startLoadOrderOrCancel(PSYM001 model) {
        logger.println(LogLevel.DEBUG, this, "SLAB 차량 항만 출발 정보 처리");
        try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
            /**
             * valid check
             */
            if(model.getTransLoadOrderDate().length() > 8) {
                ModelWarning.getInstance().setWarning(
                        model, "이송상차지시/취소 일자 ERROR: "+ model.getTransLoadOrderDate());
                return false;
            }else if(model.getTransLoadOrderSeq().length() > 4) {
                ModelWarning.getInstance().setWarning(
                        model, "이송상차지시/취소 순번 ERROR: "+ model.getTransLoadOrderSeq());
                return false;
            }
            
            /**
             * 차량 상차지시/취소 정보를 가져온다.
             */
            List ordInfo = ymCommonDAO.readStartOrderInfo(
                    model.getTransLoadOrderDate(),
                    model.getTransLoadOrderSeq(),
                    model.getCardNo(),
                    model.getDataGp());
            int ordInfoCnt = ordInfo != null && ordInfo.size() > 0 ? ordInfo.size() : 0;
            if(ordInfoCnt == 0) {
                ModelWarning.getInstance().setWarning(model, "전문에 대한 차량 상차지시/취소 정보가 없습니다.");
                return false;
            }
            
            /**
             *  차량 출발 지시 또는 취소를 처리
             */       
            if(YmCommonConst.ORDER_GP_1.equals(model.getDataGp())) {
                loadStart(ordInfo, ordInfoCnt);
            }else if(YmCommonConst.ORDER_GP_2.equals(model.getDataGp())) {
                cancelStart(ordInfo, ordInfoCnt);                    
            }      
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return true;
    }
  
  	/**
	 * 오퍼레이션명 : 
	 *
	 * SLAB 차량 항만 도착 정보 (PSYM002)
	 * 
     	 * 항만시스템으로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
     	 * 1.TC_CD	: PSYM002
     	 * 2.I/F ID	: YM-LIF-044
	 * 1	전문코드	TC			CHAR	07		
	 * 2	발생일자	Date		CHAR	10		YYYY-MM-DD
	 * 3	발생시간	Time		CHAR	08		HH-MM-SS
	 * 4	YARD 구분	YD_GP	CHAR	1		1:A열연Coil, 2:B열연Slab, 3:B열연Coil
	 * 5	이송상차지시 일자	TransLoadOrderDate	CHAR	8		
	 * 6	이송상차지시 순번	TransLoadOrderSeq	CHAR	4
	 * 7	카드 번호 	cardNo		CHAR	4
	 * 8    pallet 번호 	palletNo	CHAR	2
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	 
    public boolean startLoadOrderOrCancel_02(PSYM002 model) {
        logger.println(LogLevel.DEBUG, this, "SLAB 차량 항만 도착 정보 처리. CARD_NO="+model.getCardNo());
        try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
        	ymCommonDAO.modifyStockCardNo(model.getCardNo());
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return true;
    }
    
    /**
     * 차량 출발 지시를 처리한다.
     * --작업예약, 저장품/적치단 UPDATE, 
     * @param ordInfo		이송품정보
     * @param workGp		작업근조
     * @param ordInfoCnt	이송품 수	
     */
    private void loadStart(List ordInfo, int ordInfoCnt) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        JDTORecord dto 		= null;
        
        dto = (JDTORecord)ordInfo.get(0);
        
        /** 차량 출발 상차 지시 수신 
         *  -STOCK, Stockcol 삭제
         */
        dto = (JDTORecord)ordInfo.get(0);
        ymCommonDAO.modifyStockCardNo(getField(dto, "CARD_NO"));
        ymCommonDAO.modifyStackcolCardNo(getField(dto, "CARD_NO"));
        
        for(int i = 0; i < ordInfoCnt; i++) {
            dto = (JDTORecord)ordInfo.get(i);
            ymCommonDAO.modifyStartOfStock(
                    YmCommonConst.NEW_STOCK_MOVE_TERM_VL,
                    getField(dto, "FRTOMOVE_EQUIP_LAYER_GP"),
                    getField(dto, "FRTOMOVE_WORD_DATE_NO"),
                    getField(dto, "CARD_NO"),
                    getField(dto, "PALETTE_NO"),
                    getField(dto, "STOCK_ID"));
            
        }
    }

    /**
     * 차량 출발 지시 취소를 처리한다.
     * --작업예약 DELETE, 저장품/적치단 UPDATE, 
     * @param ordInfo		이송품정보
     * @param ordInfoCnt	이송품 수	
     */
    private void cancelStart(List ordInfo, int ordInfoCnt) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
         //스케쥴 편성이 되어 있으면 취소 불가
        if(ordInfoCnt > 0) {
            String wbookId = getField((JDTORecord)ordInfo.get(0), "WBOOK_ID");
            boolean hasSch = ymCommonDAO.readCancelOrdOfSch(wbookId);
            if(hasSch) {
                return;
            }
        }

        JDTORecord dto = null;
        for(int i = 0; i < ordInfoCnt; i++) {
            dto = (JDTORecord)ordInfo.get(i);
            ymCommonDAO.modifyMoveLoadOrderOfStock(
                    "", 
                    YmCommonConst.NEW_STOCK_MOVE_TERM_BS, 
                    "", 
                    "",
                    getField(dto, "STOCK_ID"));
        }
    }
        
    
    
	/**
	 * 오퍼레이션명 : 
	 *
	 * SLAB 이송상차지시/취소
	 * --Slab를 타 창고로 이송지시, 취소하는 시점
        * 출하시스템으로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: DMYM008
        * 2.I/F ID	: YM-LIF-044
	 * 1	전문코드				TC					CHAR	07		
	 * 2	발생일자				Date				CHAR	10		YYYY-MM-DD
	 * 3	발생시간				Time				CHAR	08		HH-MM-SS
	 * 4	YARD 구분			YD_GP				CHAR	1		1:A열연Coil, 2:B열연Slab, 3:B열연Coil
	 * 5	이송상차지시/취소 구분	TransLoadOrder		CHAR	1		‘1’:지시, ‘2’:취소, '3': 완료
	 * 6	이송상차지시 일자		TransLoadOrderDate	CHAR	8		
	 * 7	이송상차지시 순번		TransLoadOrderSeq	CHAR	4		
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	 
    public boolean moveLoadOrderOrCancel(DMYM008 model) {
        logger.println(LogLevel.DEBUG, this, "SLAB 이송상차지시/취소 처리");
        try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
    		
            /**
             * valid check
             */
            if(model.getTransLoadOrderDate().length() > 8) {
                ModelWarning.getInstance().setWarning(model, "이송상차지시/취소 일자 ERROR: "+ model.getTransLoadOrderDate());
                return false;
            }else if(model.getTransLoadOrderSeq().length() > 4) {
                ModelWarning.getInstance().setWarning(model, "이송상차지시/취소 순번 ERROR: "+ model.getTransLoadOrderSeq());
                return false;
            }
            
            /**
             * 차량 상차지시/취소 정보를 가져온다.
             */
            List ordInfo = ymCommonDAO.readLoadOrderInfo(model.getTransLoadOrderDate(),model.getTransLoadOrderSeq());
            int ordInfoCnt = ordInfo != null && ordInfo.size() > 0 ? ordInfo.size() : 0;
            if(ordInfoCnt == 0) {
                ModelWarning.getInstance().setWarning(model, "전문에 대한 이송상차지시 정보가 없습니다.");
                return false;
            }
            
            /**
             *  이송상차 지시 또는 취소를 처리
             *  2007-03-08 차량 상차 작업 완료 추가(MCH)
             */            
            if(YmCommonConst.ORDER_GP_1.equals(model.getTransLoadOrder())) {	
            	loadOrd(ordInfo, ordInfoCnt);
            }else if(YmCommonConst.ORDER_GP_2.equals(model.getTransLoadOrder())) {
                cancelOrd(ordInfo, ordInfoCnt);                    
            }else if(YmCommonConst.ORDER_GP_3.equals(model.getTransLoadOrder())) {
            	completion(ordInfo, ordInfoCnt);
            }
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return true;
    }
    
    /**
     * 이송상차 지시를 처리한다.
     * --작업예약, 저장품/적치단 UPDATE, 
     * @param ordInfo		이송품정보
     * @param workGp		작업근조
     * @param ordInfoCnt	이송품 수	
     */
    private void loadOrd(List ordInfo, int ordInfoCnt) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        JDTORecord dto 		= null;
        JDTORecord dtoDel   = null;
        String nextWBookId 	= null;
        
        /** 이송 상차 지시 수신 
         *  -STOCK, Stockcol 삭제
         */
        dtoDel = (JDTORecord)ordInfo.get(0);
        ymCommonDAO.modifyStockCardNo(getField(dtoDel, "CARD_NO"));
        ymCommonDAO.modifyStackcolCardNo(getField(dtoDel, "CARD_NO"));
        
        CraneSchDAO dao	= new CraneSchDAO();
        	
        for(int i = 0; i < ordInfoCnt; i++) {
            dto = (JDTORecord)ordInfo.get(i);
            nextWBookId = ymCommonDAO.createWBook( getField(dto, "STACK_COL_GP"), 
								                   YmCommonConst.NEW_SCH_WORK_KIND_SVML, 
								                   YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_S, 
								                   "");
            ymCommonDAO.modifyMoveLoadOrderOfStock( nextWBookId,
								                    YmCommonConst.NEW_STOCK_MOVE_TERM_CS,
								                    getField(dto, "FRTOMOVE_WORD_DATE_NO"),
								                    getField(dto, "CARD_NO"),
								                    getField(dto, "STOCK_ID"));
            ymCommonDAO.modifyLayerStatOfLayer( YmCommonConst.STACK_LAYER_STAT_S, 
							                    getField(dto, "STACK_COL_GP"),
							                    getField(dto, "STOCK_ID"));
							                    
	     /*
	      * 2008.01.15 YJK
	      * Slab 동간 이송작업실행시 기존의 복수동상차로직를 재사용하기위해
	      * 적용이유 : 부두야드나 B-Cast에서 이송이력정보를 가지고 있기 때문에
	      *                 복수동상차로 인식할 수 있다,
	      *		    따라서 복수동 체크항목인 저장품 Map정보를 Clear한다.
	      */						                    
	     int intResult = dao.updateSlabMoveEquipInfo_01(getField(dto, "STOCK_ID"),
									   	  	   "", //sFrtomoveEquipGp
									   	          "", //sFrtomoveEquipBedGp,
									   	          ""  //sFrtomoveEquipLayerGp
									   	          );
        }
    }

    /**
     * 이송상차 지시 취소를 처리한다.
     * --작업예약 DELETE, 저장품/적치단 UPDATE, 
     * @param ordInfo		이송품정보
     * @param ordInfoCnt	이송품 수	
     */
    private void cancelOrd(List ordInfo, int ordInfoCnt) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
         //스케쥴 편성이 되어 있으면 취소 불가
        if(ordInfoCnt > 0) {
            String wbookId = getField((JDTORecord)ordInfo.get(0), "WBOOK_ID");
            boolean hasSch = ymCommonDAO.readCancelOrdOfSch(wbookId);
            if(hasSch) {
                return;
            }
        }

        JDTORecord dto = null;
        for(int i = 0; i < ordInfoCnt; i++) {
            dto = (JDTORecord)ordInfo.get(i);
            ymCommonDAO.removeWBook(getField(dto, "WBOOK_ID"));
            ymCommonDAO.modifyMoveLoadOrderOfStock( "", 
								                    YmCommonConst.NEW_STOCK_MOVE_TERM_BS, 
								                    "", 
								                    "",
								                    getField(dto, "STOCK_ID"));
            ymCommonDAO.modifyLayerStatOfLayer( YmCommonConst.STACK_LAYER_STAT_L, 
							                    getField(dto, "STACK_COL_GP"), 
							                    getField(dto, "STOCK_ID"));
        }
    }

    /**
     * 차량 상차 작업 완료  지시를 처리한다.
     * 2007-03-08 차량 상차 작업 완료 추가(MCH)
     * --작업예약, 저장품/적치단 UPDATE, 
     * @param ordInfo		이송품정보
     * @param workGp		작업근조
     * @param ordInfoCnt	이송품 수	
     */
    private void completion(List ordInfo, int ordInfoCnt) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        JDTORecord dto 		= null;

        for(int i = 0; i < ordInfoCnt; i++) {
            dto = (JDTORecord)ordInfo.get(i);
            ymCommonDAO.modifyMoveCompletionOfStock(YmCommonConst.NEW_STOCK_MOVE_TERM_VL,
								                    getField(dto, "FRTOMOVE_WORD_DATE_NO"),
								                    getField(dto, "CARD_NO"),
								                    getField(dto, "STOCK_ID"));

        }
        dto = (JDTORecord)ordInfo.get(0);
        ymCommonDAO.modifycolCompletion( getField(dto, "CARD_NO"),
										getField(dto, "STACK_COL_GP"));         
        /*
		UPDATE TB_YM_STACKLAYER A
		   SET A.STACK_LAYER_STAT = DECODE(A.STOCK_ID, NULL, 'V', A.STACK_LAYER_STAT)
		 WHERE A.STACK_COL_GP = ?
		 */
        String query = "ym.common.dao.updateStackLayerStat";
        ymCommonDAO.updateData(query,new Object[]{getField(dto, "STACK_COL_GP")});
    }
    
    /**
     * 작업예약 테이블 INSERT, 저장품 테이블에 '작업예약ID' UPDATE, 적치단 테이블에 '적치상태'를 UPDATE
     * @param colGp		적치열
     * @param sch		스케쥴작업종류
     * @param operGp	오퍼레이터 지정 구분
     * @param loc		PUT위치
     * @param stockId	저장품ID
     */
    private void createWBookOfLoadOrd(String colGp, 
						    		  String sch, 
						    		  String operGp, 
						    		  String loc, 
						    		  String stockId) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String nextWBookId = ymCommonDAO.createWBook(colGp, sch, operGp, loc);
        ymCommonDAO.modifyWbookIdOfStock(nextWBookId, stockId);
        ymCommonDAO.modifyLayerStatOfLayer(YmCommonConst.STACK_LAYER_STAT_S, colGp, stockId);
    }
    

	/**
	 * 오퍼레이션명 : 
	 *
	 * 관제시스템으로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: PCYM001
        * 2.I/F ID	: YM-LIF-041
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	 
	public boolean syZoneInReservationCancel(PCYM001 model) {
        logger.println(LogLevel.DEBUG, this, "Slab 장입예정번호취소 처리");
        try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
    		
            /**
             * Slab No 체크
             */
            if(! model.validSlabNo()) {
                ModelWarning.getInstance().setWarning(
                        model,"수신항목 'Slab No' ERROR: "+ model.getSlabNo());
                return false;
            }
            /**
             * 관제 ReSchedul 취소에 따른 '장입LOT번호'를 UPDATE
             */
            ymCommonDAO.modifyZoneInOfStock(
                    "", 
                    YmCommonUtil.getSlabCurrProgCd(model.getSlabNo(),"")[1],
                    model.getSlabNo());
            /**
             * 야드 L-2에 압연취소 정보를 송신한다.
             */
            sendSlabInfo(ymCommonDAO.readCancelZoneInOfStock(model.getSlabNo()));
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return true;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * 관제시스템으로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: PCYM002
        * 2.I/F ID	: YM-LIF-042
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	      
	public boolean syZoneInReservationInsert(PCYM002 model) {
        logger.println(LogLevel.DEBUG, this, "Slab 장입예정번호등록 처리");
        try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
        	
        	//L2 삭제 장입정보 송신
        	{
			List del_list 	= ymCommonDAO.readZoneInStocks_Del(model.getSlabNo());
			int del_listCnt= del_list != null ? del_list.size() : 0;
			
			JDTORecord dtoDel = null;
			for(int ix = 0; ix < del_listCnt; ix++) 
			{
				dtoDel = (JDTORecord)del_list.get(ix);
				sendSlabInfo_first(dtoDel);                     
			}
        	}	
        	
            /**
             * 저장품의 장입순번을 CLEAR 한다.
             */
            ymCommonDAO.modifyZoneInNo();
            /**
             * 장입예정번호를 READ 한다.
             */
            List zoneInLst 	= ymCommonDAO.YJK_DEL_readZoneInStocks(model.getSlabNo());
            int zoneInLstCnt= zoneInLst != null ? zoneInLst.size() : 0;
            if(zoneInLstCnt == 0 ) {
                ModelWarning.getInstance().setWarning(model, "장입예정번호가 존재하지 않습니다.");
                return false;
            }            
            /**
             * #3CTC,#4CTC,W/B에 올려진 SLAB는 장입순번을 변경하지 않는다.
             */
            List list = ymCommonDAO.readLoadWBCTC();
            int listCnt = list != null ? list.size() : 0;
            /**
             * 저장품 테이블에 READ한 Slab No를 Update 한다.
             * -저장품 상태	: "F".
             * -장입 LOT 번호	: READ한 예정번호. 
             */
            JDTORecord dto = null;
            for(int i = 0; i < zoneInLstCnt; i++) {
                dto = (JDTORecord)zoneInLst.get(i);
                if(notWBLoading(list, getField(dto, "STOCK_ID"), listCnt)) {
                    ymCommonDAO.modifyZoneInOfStock(
                            getField(dto, "LOT_PRIOR"),
                            YmCommonUtil.getSlabCurrProgCd(getField(dto, "STOCK_ID"),"")[1],
                            getField(dto, "STOCK_ID"));
                    sendSlabInfo(dto);                    
                }
            }
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return true;
	}

    /**
     * @param list
     * @param field
     * @param listCnt
     * @return
     */
    private boolean notWBLoading(List list, String stockId, int listCnt) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
        boolean notStock = true;
        for(int i = 0; i < listCnt; i++) {
            if(stockId.equals(getField((JDTORecord)list.get(i), "STOCK_ID"))) {
                notStock = false;
                break;
            }
        }
        return notStock;
    }
    
   /**
     * 최초 변경된 슬라브 정보에 대한 시점을 체크
     * 
     * @param dto	슬라브정보
     */
    private void sendSlabInfo_first(JDTORecord slabInfo) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
	    Map tc = ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_CM1BP02);
        StringBuffer sendMsg = new StringBuffer();
        sendMsg.append(YmCommonConst.TC_CM1BP02);
        sendMsg.append(YmCommonUtil.getStringYMD("/"));
        sendMsg.append(YmCommonUtil.getStringHMS(":"));
        sendMsg.append("R");
        appendMsgNum(sendMsg, ""+ (YmCommonUtil.getTotalLenOfTc(tc) - 30), getFieldLen(tc, "전문길이"));
        appendMsg(sendMsg, getField(slabInfo, "STOCK_ID"),		getFieldLen(tc, "SLAB번호"));
        appendMsg(sendMsg, "1",									getFieldLen(tc, "처리구분"));
        appendMsg(sendMsg, getField(slabInfo, "ORD_YEOJAE_GP"), getFieldLen(tc, "주문여재구분"));
        appendMsg(sendMsg, getField(slabInfo, "PRODUC_NO"),		getFieldLen(tc, "제작번호행번"));
        String t 	= YmCommonUtil.format(getField(slabInfo, "SLAB_T"), 3, 3).replace('.', ' ');
        String w 	= YmCommonUtil.format(getField(slabInfo, "SLAB_W"), 4, 1).replace('.', ' ');
        appendMsgNum(sendMsg, t.replaceAll(" ", ""),			getFieldLen(tc, "두께"));
        appendMsgNum(sendMsg, w.replaceAll(" ", ""),			getFieldLen(tc, "폭"));
        appendMsgNum(sendMsg, getField(slabInfo, "SLAB_LEN"), 	getFieldLen(tc, "길이"));
        appendMsgNum(sendMsg, getField(slabInfo, "SLAB_WT"),	getFieldLen(tc, "중량"));
        appendMsg(sendMsg, getField(slabInfo, "COIL_NO"), 		getFieldLen(tc, "예정COILNO"));
        appendMsg(sendMsg, getField(slabInfo, "MILL_PLAN_DDTT"),getFieldLen(tc, "압연예정일시"));
        appendMsg(sendMsg, getField(slabInfo, "LOT_NO"),		getFieldLen(tc, "장입LOT번호"));
        appendMsgNum(sendMsg, getField(slabInfo, "LOT_IN_SLAB_PRIOR"),getFieldLen(tc, "LOT내작업순위"));
        appendMsg(sendMsg, getField(slabInfo, "BUY_SLAB_NO"), 	getFieldLen(tc, "구입SLABNO"));
        appendMsg(sendMsg, "", 	getFieldLen(tc, "장입순번"));
        
        sendQueue(YmCommonConst.TC_CM1BP02, sendMsg.toString());
    }
    	
    /**
     * 장입확정시 L-2로 슬라브정보를 송신한다.
     * @param dto	슬라브정보
     */
    private void sendSlabInfo(JDTORecord slabInfo) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
	    Map tc = ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_CM1BP02);
        StringBuffer sendMsg = new StringBuffer();
        sendMsg.append(YmCommonConst.TC_CM1BP02);
        sendMsg.append(YmCommonUtil.getStringYMD("/"));
        sendMsg.append(YmCommonUtil.getStringHMS(":"));
        sendMsg.append(YmCommonConst.FORM_I);
        appendMsgNum(sendMsg, ""+ (YmCommonUtil.getTotalLenOfTc(tc) - 30), getFieldLen(tc, "전문길이"));
        appendMsg(sendMsg, getField(slabInfo, "STOCK_ID"),		getFieldLen(tc, "SLAB번호"));
        appendMsg(sendMsg, "1",									getFieldLen(tc, "처리구분"));
        appendMsg(sendMsg, getField(slabInfo, "ORD_YEOJAE_GP"), getFieldLen(tc, "주문여재구분"));
        appendMsg(sendMsg, getField(slabInfo, "PRODUC_NO"),		getFieldLen(tc, "제작번호행번"));
        String t 	= YmCommonUtil.format(getField(slabInfo, "SLAB_T"), 3, 3).replace('.', ' ');
        String w 	= YmCommonUtil.format(getField(slabInfo, "SLAB_W"), 4, 1).replace('.', ' ');
        appendMsgNum(sendMsg, t.replaceAll(" ", ""),			getFieldLen(tc, "두께"));
        appendMsgNum(sendMsg, w.replaceAll(" ", ""),			getFieldLen(tc, "폭"));
        appendMsgNum(sendMsg, getField(slabInfo, "SLAB_LEN"), 	getFieldLen(tc, "길이"));
        appendMsgNum(sendMsg, getField(slabInfo, "SLAB_WT"),	getFieldLen(tc, "중량"));
        appendMsg(sendMsg, getField(slabInfo, "COIL_NO"), 		getFieldLen(tc, "예정COILNO"));
        appendMsg(sendMsg, getField(slabInfo, "MILL_PLAN_DDTT"),getFieldLen(tc, "압연예정일시"));
        appendMsg(sendMsg, getField(slabInfo, "LOT_NO"),		getFieldLen(tc, "장입LOT번호"));
        appendMsgNum(sendMsg, getField(slabInfo, "LOT_IN_SLAB_PRIOR"),getFieldLen(tc, "LOT내작업순위"));
        appendMsg(sendMsg, getField(slabInfo, "BUY_SLAB_NO"), 	getFieldLen(tc, "구입SLABNO"));
        appendMsg(sendMsg, getField(slabInfo, "LOT_PRIOR"), 	getFieldLen(tc, "장입순번"));
        
        sendQueue(YmCommonConst.TC_CM1BP02, sendMsg.toString());
    }

	/**
	 * 오퍼레이션명 : 
	 *
	 * 공정시스템으로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: PMYM002 
        * 2.I/F ID	: YM-LIF-034
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	      	     
	public boolean syMoveInstructionCancel(PMYM002 model) {
        logger.println(LogLevel.DEBUG, this, "Slab 이송 지시 취소 처리");
        try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
            /**
             * Slab No 체크
             */
            if(! model.validSlabNo()) {
                ModelWarning.getInstance().setWarning(
                        model, "수신항목 'Slab No' ERROR: "+ model.getSlabNo());
                return false;
            }

            /**
             * 저장품 테이블을 UPDATE 한다.
             * 1. 저장품 상태		: 충당실적
             * 2. 저장품 이동 조건	: 이송지시대기
             * 3. 이송 지시 번호	: "" 
             */
            ymCommonDAO.modifyMoveOrderOfStock(
                    YmCommonConst.NEW_STOCK_MOVE_TERM_BS,
                    "", 
                    model.getSlabNo());
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return true;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * 공정시스템으로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: PMYM001
        * 2.I/F ID	: YM-LIF-033
        * 1	전문코드			TC		CHAR	07		
        * 2	발생일자			Date		CHAR	10		YYYY-MM-DD
        * 3	발생시간			Time		CHAR	08		HH-MM-SS
        * 4	YARD 구분		YD_GP	CHAR	1		0:A열연Slab,1:A열연Coil,2:B열연Slab,3:B열연Coil
        * 5	이송지시일정				CHAR	18		
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	     
	public boolean syMoveInstructionInsert(PMYM001 model) {
		
		logger.println(LogLevel.DEBUG, this, "Slab 이송 지시 등록 처리");
		try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
			/**
			* valid check
			*/
			if(model.getTransOrderDate().length() > 8) {
				ModelWarning.getInstance().setWarning(model,"이송지시일정 ERROR: "+ model.getTransOrderDate());
				return false;
			}else if(model.gettransLoadOrderSeq().length() > 4) {
				ModelWarning.getInstance().setWarning(model,"이송지시 순서 ERROR: "+ model.gettransLoadOrderSeq());
				return false;
			}
			
			/**
			* ‘이송지시일정‘으로 PO_SLAB이송 Table을 READ 한다.
			*/
		
			List moveStocks = ymCommonDAO.readMoveOrderStocks(	model.getTransOrderDate(), 
															model.gettransLoadOrderSeq().trim());
			int moveStocksCnt = moveStocks != null ? moveStocks.size() : 0;
			if(moveStocksCnt == 0) {
				ModelWarning.getInstance().setWarning(model, "'이송지시일정'에 대한 Slab 정보가  존재하지 않습니다.");
				return false;
			}
				
			/**
			* 저장품 테이블에 READ한 Slab No를 Update 한다.
			* 1. 저장품 이동 조건	: "S8"-Slab 이송대기
			* 2. 이송 지시 번호	: READ한 이송지시번호
			*/
			logger.println(LogLevel.DEBUG, this, "A열연 SLAB야드 공정의 이송지시 등록 -> 이송지시 대상 건수 :"+moveStocksCnt);
			
			for(int i = 0; i < moveStocksCnt; i++) {
				logger.println(LogLevel.DEBUG, this, "A열연 SLAB야드 공정의 이송지시 등록 -> 이송지시 대상  :"+getField((JDTORecord)moveStocks.get(i), "SLAB_NO"));
				
				ymCommonDAO.modifyMoveOrderOfStock( 	YmCommonConst.NEW_STOCK_MOVE_TERM_CS,
													getField((JDTORecord)moveStocks.get(i), "FRTOMOVE_WREQ_DATE_NO"),
													getField((JDTORecord)moveStocks.get(i), "SLAB_NO"));
			}
			
			/*
			  *	2007-05-23(MCH)
			  *	A열연 ->B열연이송 지시 일경우만 상차지시 등록 -> 크레인스케줄 CALL 
			  */
			if(StringHelper.evl(model.getyardID(),"").equals(YmCommonConst.YD_GP_0)){
				
				List stockAay = ymCommonDAO.readMoveOrderStocksbay(	model.getTransOrderDate(), 
																model.gettransLoadOrderSeq().trim(),
																YmCommonConst.BAY_GP_A);
				List stockBay = ymCommonDAO.readMoveOrderStocksbay(	model.getTransOrderDate(), 
																model.gettransLoadOrderSeq().trim(),
																YmCommonConst.BAY_GP_B);
																
				logger.println(LogLevel.DEBUG, this, "A열연 SLAB야드 공정의 이송지시 등록 -> 작업예약 생성 -> 크레인스케줄 CALL");
				
				String s_spangubun 	= "01";	//스판
				String s_colsgubun 	= "01";	//열
				String Abay_stock_id 	= "";		//A동 저장품 ID
				String Bbay_stock_id 	= "";		//B동 저장품 ID
				 
				/*
				  *	위치지정(6자리)으로 작업예약을 등록하기 위해 아래의 메소드 호출
				  */
				
				//동구분 A,B
				EJBConnector ejbConn = new EJBConnector("default", "JNDICoilInfoReg", this);
				//저장품이 A동에 있을경우
				if(stockAay.size() > 0){
					logger.println(LogLevel.DEBUG, this, "A열연 SLAB야드 A동 이송상차 작업 예약 시작 건수 	:"+stockAay.size());
					logger.println(LogLevel.DEBUG, this, "A열연 SLAB야드 A동 이송상차 작업 예약 시작 건 		:"+stockAay);
					for(int i = 0; i < stockAay.size(); i++) {
						Abay_stock_id = Abay_stock_id + getField((JDTORecord)stockAay.get(i), "SLAB_NO") + "-";
					}
					ejbConn.trx("produtPerSpanSvml", new  Class[] { 	String.class, String.class, 
																String.class, String.class, 
																String.class, String.class }, 
												new Object[] {	YmCommonConst.YD_GP_0, 
																YmCommonConst.BAY_GP_A, 
																s_spangubun, 
																s_colsgubun, 
																Abay_stock_id, 
																model.getTcCode()});
				}
				//저장품이 B동에 있을경우
				if(stockBay.size() > 0){
					logger.println(LogLevel.DEBUG, this, "A열연 SLAB야드 B동 이송상차 작업 예약 시작 건수 	:"+stockBay.size());
					logger.println(LogLevel.DEBUG, this, "A열연 SLAB야드 B동 이송상차 작업 예약 시작 건 		:"+stockBay);
					for(int i = 0; i < stockBay.size(); i++) {
						Bbay_stock_id = Bbay_stock_id + getField((JDTORecord)stockBay.get(i), "SLAB_NO") + "-";
					}
					ejbConn.trx("produtPerSpanSvml", new  Class[] { 	String.class, String.class, 
																String.class, String.class, 
																String.class, String.class }, 
												new Object[] {	YmCommonConst.YD_GP_0, 
																YmCommonConst.BAY_GP_B, 
																s_spangubun, 
																s_colsgubun, 
																Bbay_stock_id, 
																model.getTcCode()});
				}
			}
			
		}catch(DAOException daoe) {
			throw daoe;
		}catch(Exception e) {
			throw new EJBServiceException(e);
		}
		return true;
	}

 	/**
	 * 오퍼레이션명 : 
	 *
	 * 공정시스템으로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: PMYM004
        * 2.I/F ID	: YM-LIF-036
        * 
        * 전문코드	TC		CHAR	07		
        * 발생일자	Date	CHAR	10		YYYY-MM-DD
        * 발생시간	Time	CHAR	08		HH-MM-SS
        * 압연지시일자		CHAR	18		
        * 진도코드			CHAR	02		
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	     
    public boolean syRollingWorkOrderInsert(PMYM004 model) {
        logger.println(LogLevel.DEBUG, this, "Slab 압연 지시 등록 처리");
        try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
    		
            /**
             * vlaid check
             */
            if(! model.validRollingOrderDate()) {
                ModelWarning.getInstance().setWarning(
                        model, "압연지시일자 ERROR: "+ model.getRollingOrderDate());
                return false;
            }else if(! model.validProgressCode()) {
                ModelWarning.getInstance().setWarning(
                        model, "진도코드 ERROR: "+ model.getProgressCode());
                return false;
            }
            /**
             * 수신항목의 '압연지시일자'로 관제L-3의 TB_PC_MILLSPEC Table을 READ한다.
             */
            List rollings = ymCommonDAO.YJK_DEL_readRollingSlabInfo( 
                    model.getProgressCode().trim(),
                    model.getRollingOrderDate().trim());
            int rollingsCnt = rollings != null ? rollings.size() : 0;
            if(rollingsCnt == 0) {
                ModelWarning.getInstance().setWarning(
                        model, "TB_PC_MILLSPEC 에 '압연지시일자'에 해당하는 저장품이 존재하지 않습니다.");
                return false;
            }
            /**
             * 저장품 테이블에 READ한 데이터의 저장품상태를 'E'로 UPDATE
             */
            editRollingOrder(rollings, rollingsCnt);
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return true;
    }
    
    /**
     * 송신 EJB를 이용하여 송신데이터를 송신한다.
     * @param methodName	TC명
     * @param sendMsg		송신데이터
     * @throws Exception
     */
    private void sendQueue(String methodName, String sendMsg) {
        EJBConnector ejbConn = null;
        try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return;
    		}
    		
            ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
            ejbConn.trx("send"+ methodName, new Class[]{ String.class }, new Object[]{ sendMsg });
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
    }

    /**
     * 공백을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private void fillSpace(StringBuffer buffer, int cnt) {
        for(int i = 0; i < cnt; i++) {
            buffer.append(" ");
        }
    }

    /**
     * name parameter에 대한 값을 반환한다.
     * @param data	
     * @param name  
     * @return
     */
    private int getFieldLen(Map data, String name) {
        return StringHelper.parseInt((String)data.get(name), 0);
    }

    /**
     * 공백을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private void appendMsg(StringBuffer buffer, String field, int cnt) {
	    try{ 	
	    	if("".equals(field)) {
	            fillSpace(buffer, cnt);
	        }else if(CommonUtil.getLength(field) > cnt) {
	        	buffer.append(CommonUtil.substr(field, 0, cnt));
	        }else if(CommonUtil.getLength(field) < cnt) {
	            buffer.append(field);
	            fillSpace(buffer, cnt - CommonUtil.getLength(field));
	        }else {
	            buffer.append(field);
	        }
	    }catch(Exception e){}
    }
    
    /**
     * 0을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private void appendMsgNum(StringBuffer buffer, String field, int cnt) {
	    try{    
	        if("".equals(field)) {
	            fillZeroSpace(buffer, cnt);
	        }else if(CommonUtil.getLength(field) > cnt) {
	            buffer.append(CommonUtil.substr(field, 0, cnt));
	        }else if(CommonUtil.getLength(field) < cnt) {
	            fillZeroSpace(buffer, cnt - CommonUtil.getLength(field));
	            buffer.append(field);
	        }else {
	            buffer.append(field);
	        }
	    }catch(Exception e){}
    }
    
    /**
     * 0을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private void fillZeroSpace(StringBuffer buffer, int cnt) {
        for(int i = 0; i < cnt; i++) {
            buffer.append("0");
        }
    }

    /**
     * 저장품이동조건을 압연대기로 UPDATE
     * @param rollings	압연지시 정보
     */
    private void editRollingOrder(List rollings, int rollingsCnt) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
        for(int i = 0; i < rollingsCnt; i++) {
            ymCommonDAO.modifyStockTermOfStock(
                    YmCommonConst.NEW_STOCK_MOVE_TERM_FS, 
                    getField((JDTORecord)rollings.get(i), "STOCK_ID"));
        }
    }

    /**
     * JDTORecord 가 가지는 name parameter에 대한 값을 리턴한다.
     * @param data
     * @param name
     * @return
     */
    private String getField(JDTORecord data, String name) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        return StringHelper.evl(data.getFieldString(name), "").trim();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    ////////////////////////YJK START//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
 	/**
	 * 오퍼레이션명 : 
	 *
	 *  정정지시 등록(조업 -> 야드)
	 *	1	전문코드		TC				CHAR	07		
	 *	2	발생일자		Date			CHAR	10		YYYY-MM-DD
	 *	3	발생시간		Time			CHAR	08		HH-MM-SS
	 *	4	공장구분		PlantGp			CHAR	01 		1,2,3,4
	 *	5	공정구분		ProcGp			CHAR	01		K(SPM),H(HFL)
	 *	6	작업지시단위명	WordUnitName	CHAR	06	
	 *
	 *  D2 // COIL 정정보급대기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	  
	public boolean cyJungJungGisiRegistInfo(POYM003 dModel){
		boolean isSucess = false;
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
    		
			String sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_CC; // COIL 정정작업대기
			
			String sPlantGp			= StringHelper.evl(dModel.getPlantGp(),"");
			String sProcGp   		= StringHelper.evl(dModel.getProcGp(),"");
			String sWordUnitName   	= StringHelper.evl(dModel.getWordUnitName(),"");
			
			isSucess = setPoPmCoilGoodsList(sStockMoveTerm,
    								 	    sPlantGp,
    								 	    sProcGp,
    								 	    sWordUnitName);
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) { 
            throw new EJBServiceException(e);
        }
        return isSucess;
    }

	/**
	 * 오퍼레이션명 : 
	 *
	 *  정정지시 등록(공정 -> 야드)
	 *	Event를 수신하여 정정지시 Table을 읽어서 처리
	 *	1	전문코드		TC				CHAR	07		
	 *	2	발생일자		Date			CHAR	10		YYYY-MM-DD
	 *	3	발생시간		Time			CHAR	08		HH-MM-SS
	 *	4	공장구분		PlantGp			CHAR	01 		1,2,3,4
	 *	5	공정구분		ProcGp			CHAR	01		K(SPM),H(HFL)
	 *	6	작업지시단위명	WordUnitName	CHAR	06	
	 *
	 *  D2 // COIL 정정보급대기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	  	 
	public boolean cyJungJungGisiRegistInfo(PMYM008 dModel){
		boolean isSucess = false;
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
			String sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_CC; // COIL 정정작업대기
			
			String sPlantGp			= StringHelper.evl(dModel.getPlantGp(),"");
			String sProcGp   		= StringHelper.evl(dModel.getProcGp(),"");
			String sWordUnitName   	= StringHelper.evl(dModel.getWordUnitName(),"");
			
			isSucess = setPoPmCoilGoodsList(sStockMoveTerm,
    								 	    sPlantGp,
    								 	    sProcGp,
    								 	    sWordUnitName);
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
    }
    
  
	/**
	 * 오퍼레이션명 : 
	 *
	 *  조업 정정Table에서 저장품정보를 읽어서 해당 저장품의
	 *  저장품이동경로 항목을 UPDATE한다.
	 *
	 * param  String	: 저장품이동경로
	 * param  String	: 공장구분
	 * param  String	: 공정구분
	 * param  String	: 작업지시 단위명
	 */     	  	 
    private boolean setPoPmCoilGoodsList(String sStockMoveTerm,
    								 	 String sPlantGp,
									     String sProcGp,
									     String sWordUnitName){
    								 	
    	boolean isSucess = false;
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
			String sStockId					= "";
			String sCarCardNo				= ""; 
			String sTransWordDate			= "";
			String sTransWordSeqno			= "";
			
			YdStockDAO dao = new YdStockDAO();
			
			if(YmCommonConst.YD_GP_1.equals(sPlantGp)){
				sPlantGp = YmCommonConst.YD_GP_A;
			}else{
				sPlantGp = YmCommonConst.YD_GP_B;
			}
			
			//조업정보 검색
			List poList 		= null;
            {
            	poList = dao.getPoPmStockInfo(sPlantGp,sProcGp,sWordUnitName);

            	if(poList.size() == 0){
	            	logger.println(LogLevel.DEBUG, this, "조업TABLE 에서 저장품정보를 가져오지 못했습니다.");
	            	throw new EJBServiceException("=인터페이스 작업요구=>조업TABLE 저장품정보 존재안함.");
	            }
	        }
	        
            //STOCK TABLE UPDATE
            int iSeq = 0;
            JDTORecord stockV	= null;
            {
            	for(int inx = 0; inx < poList.size() ; inx++){
				 	stockV 		= (JDTORecord)poList.get(inx);
				 	sStockId   	= StringHelper.evl(stockV.getFieldString("GOODS_NO"),"");
				 	
					logger.println(LogLevel.DEBUG, this, "sStockId			="+sStockId);
					
		            iSeq = dao.updateStockTransInfo(sStockId,
													sStockMoveTerm); 
													
					logger.println(LogLevel.DEBUG, this, "iSeq				="+iSeq);
				}								
	        }    
	        
			isSucess = true;
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
    }
    
	/**
	 * 오퍼레이션명 : 
	 *
	 *  이송지시 등록(공정 -> 야드)
	 *	Event를 수신하여 Coil 이송지시 Table을 읽어서 처리
	 *	1	전문코드			TC					CHAR	07		
	 *	2	발생일자			Date				CHAR	10		YYYY-MM-DD
	 *	3	발생시간			Time				CHAR	08		HH-MM-SS
	 *	4	이송작업지시일자	FrtomoveWordDate	CHAR	8		
	 *	5	이송작업지시순번	FrtomoveWordSeqno	CHAR	4	
	 *
	 *  CB	Coil 이송상차 대기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	  	 	 
	public boolean cyISongGisiRegistInfo(PMYM006 dModel){
		boolean isSucess = false;
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
			String sStockMoveTerm  = YmCommonConst.NEW_STOCK_MOVE_TERM_DC; // Coil 이송작업지시대기
			
			String sFrtomoveWordDate  = StringHelper.evl(dModel.getFrtomoveWordDate(),"");
			String sFrtomoveWordSeqno = StringHelper.evl(dModel.getFrtomoveWordSeqno(),"");
			
			YdStockDAO dao = new YdStockDAO();
			
			//공정정보 검색
			List pmList 		= null;
            {
            	pmList = dao.getPmStockInfo_02(sFrtomoveWordDate,sFrtomoveWordSeqno);

            	if(pmList.size() == 0){
	            	logger.println(LogLevel.DEBUG, this, "공정TABLE 에서 저장품정보를 가져오지 못했습니다..");
	            	throw new EJBServiceException("=인터페이스 작업요구=>공정TABLE 저장품정보 존재안함.");
	            }
	        }
	        
            //STOCK TABLE UPDATE
            int iSeq = 0;
            JDTORecord stockV	= null;
            String sStockId		= "";
            {
            	for(int inx = 0; inx < pmList.size() ; inx++){
				 	stockV 		= (JDTORecord)pmList.get(inx);
				 	sStockId   	= StringHelper.evl(stockV.getFieldString("GOODS_NO"),"");
				 	
					logger.println(LogLevel.DEBUG, this, "sStockId			="+sStockId);
					
		            iSeq = dao.updateStockTransInfo_05(sStockId,
		            								   sFrtomoveWordDate,
		            								   sFrtomoveWordSeqno,
													   sStockMoveTerm); 								
													
					logger.println(LogLevel.DEBUG, this, "iSeq				="+iSeq);
				}								
	        }    
	        
			isSucess = true;
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
    }

	/**
	 * 오퍼레이션명 : 
	 *
	 *  이송지시 취소(공정 -> 야드)
	 *	공정에서 이송지시 취소 등록 후 공정 -> 야드로(Coil 단위)
	 *	1	전문코드			TC		CHAR	07		
	 *	2	발생일자			Date	CHAR	10		YYYY-MM-DD
	 *	3	발생시간			Time	CHAR	08		HH-MM-SS
	 *	4	Coil No				CoilNo	CHAR	10
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	  	 	 	 
	public boolean cyISongGisiCancelInfo(PMYM007 dModel){
		boolean isSucess = false;
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
			String sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_DC; // Coil 이송작업지시대기
			
			String sStockId = StringHelper.evl(dModel.getCoilNo(),"");
			
			YdStockDAO dao = new YdStockDAO();
			
			//STOCK TABLE UPDATE
            int iSeq = 0;
            {
	            iSeq = dao.updateStockTransInfo_05(sStockId,
	            								   "",
	            								   "",
												   sStockMoveTerm); 		
												   								
				logger.println(LogLevel.DEBUG, this, "iSeq				="+iSeq);
			}    
	        
			isSucess = true;
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
    }
    
    /*
     	===== 출하 업무 코드 =====
    			
		DMYM001	-	1.	제품운송지시(출하)
					2.	소재이송지시(공정)
					3.	제품운송지시취소(출하)
					4.	소재이송지시취소(공정)
					
		DMYM002	-	1.	제품출하상차지시(출하)		- 작업예약생성
					2.	제품출하상차지시취소(출하)	- 작업예약취소		
					
		DMYM003	-	1.	제품이송지시(출하)
					2.	제품이송지시취소(출하)	
					
		DMYM004 -	1.	제품이송상차지시(출하)		- 작업예약생성
					2.	소재이송상차지시(출하)		- 작업예약생성
					3.	제품이송상차지시취소(출하)	- 작업예약취소	
					4.	소재이송상차지시취소(출하)	- 작업예약취소												
	*/

	/**
	 * 오퍼레이션명 : 
	 *
	 *  제품.소재 운송지시/취소 등록(일품Match)(출하 -> 야드)
	 *	출고대상 제품을 배차, 취소하는 시점
	 *	1	전문코드			TC				CHAR	07		
	 *	2	발생일자			Date			CHAR	10		YYYY-MM-DD
	 *	3	발생시간			Time			CHAR	08		HH-MM-SS
	 *	4	운송지시/취소 구분	TransOrderId	CHAR	1		‘1’:지시, ‘2’:취소
	 *	5	운송지시 일자		TranOrderDate	CHAR	8		
	 *	6	운송지시 순번		TranOrderSeq	CHAR	4	
	 *  7   소재/제품구분       materialgoods   CHAR    1 		‘1’:소재, ‘2’:제품
	 *
	 *  GB	Coil 출하 대기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	  	 	 	 	 
	public boolean cyGoodsUnSongGisiRegistInfo(DMYM001 dModel){
		
		boolean isSucess = false;
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
			
			String sStockMoveTerm			= "";
			
			String sYdGp	 	= StringHelper.evl(dModel.getyardID(),"");
			String sOrderId 	= StringHelper.evl(dModel.getTransOrderId(),"");
			String sOrderDate 	= StringHelper.evl(dModel.getTranOrderDate(),"");
			String sOrderSeq 	= StringHelper.evl(dModel.getTranOrderSeq(),"");
			String sIsGoods 	= StringHelper.evl(dModel.getMaterialgoods(),"");
			
			if(	YmCommonConst.YD_GP_0.equals(sYdGp)||
			   	YmCommonConst.YD_GP_2.equals(sYdGp)){
				/*
				 *	SLAB외판 운송지시 추가
				 */
				
				if("1".equals(sOrderId)){    
					sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_LS; // Slabl 출하작업대기
				}else if("2".equals(sOrderId)){	
					sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_KS; // Slab  출하작업지시대기
				}
				
				if("1".equals(sOrderId)){      	
					isSucess = setDmSlabGoodsList(	sStockMoveTerm,
	    								 	  	  	sOrderDate,
	    								 	  	  	sOrderSeq,
	    								 	  	  	"N");
				}else if("2".equals(sOrderId)){	
					
					isSucess = removeWbookSlabGoodsList(	sStockMoveTerm,
														sOrderDate,
	    								 	 	    			sOrderSeq);	
				}
				
			}else{
				
				
				if("1".equals(sOrderId)){    
					if("1".equals(sIsGoods)){       	
						sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_CS; // Coil 이송대기
					}else if("2".equals(sIsGoods)){	
						sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_LG;	// Coil 출하작업대기
					}
				}else if("2".equals(sOrderId)){	
					if("1".equals(sIsGoods)){       	
						sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_DC; // Coil 이송작업지시대기
					}else if("2".equals(sIsGoods)){	
						sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_KG; // Coil 출하작업지시대기
					}
				}
		        
	    			if("1".equals(sOrderId)){      	
					isSucess = setDmCoilGoodsList(	sStockMoveTerm,
	    								 	  	  	sOrderDate,
	    								 	  	  	sOrderSeq,
	    								 	  	  	sIsGoods,
	    								 	  	  	"N");
				}else if("2".equals(sOrderId)){	
					
					isSucess = removeWbookGoodsList(	sStockMoveTerm,
													sOrderDate,
	    								 	 	    		sOrderSeq,
	    								 	 	    		sIsGoods,
	    								 	 	    		"N");	
				}
    			}					 	  
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
    }
   

	/**
	 * 오퍼레이션명 : 
	 *
	 *  제품출하상차지시/취소 등록(상차Lot)(출하 -> 야드)
	 *	차량이 공차로 계근대를 통과, 취소하는 시점
	 *	1	전문코드				TC				CHAR	07		
	 *	2	발생일자				Date				CHAR	10		YYYY-MM-DD
	 *	3	발생시간				Time				CHAR	08		HH-MM-SS
	 *	4	출하상차지시/취소 구분	DistLoadOrder	CHAR	1		‘1’:지시, ‘2’:취소
	 * 	5	출하상차지시 일자		DistLoadOrderDate	CHAR	8		
	 *	6	출하상차지시 순번		DistLoadOrderSeq	CHAR	4		
	 *
	 * 	GD // Coil 출하 상차대기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	  	 	 	 		 
	public boolean cyGoodsChulHaSangChaGisiRegistInfo(DMYM002 dModel){
		boolean isSucess = false;
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
			
			String sStockMoveTerm			= "";
			
			String sYdGp	 	= StringHelper.evl(dModel.getyardID(),"");
			String sOrderId 	= StringHelper.evl(dModel.getDistLoadOrder(),"");
			String sOrderDate 	= StringHelper.evl(dModel.getDistLoadOrderDate(),"");
			String sOrderSeq 	= StringHelper.evl(dModel.getDistLoadOrderSeq(),"");
			
			if("1".equals(sOrderId)){      	
				sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_LG;	// Coil 출하작업대기
		       }else if("2".equals(sOrderId)){	
		        	sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_KG; // Coil 출하작업지시대기
		       }
		       
		       if(	YmCommonConst.YD_GP_0.equals(sYdGp)||
			   	YmCommonConst.YD_GP_2.equals(sYdGp)){
				/*
				 *	SLAB외판 운송지시 추가
				 */
				
				if("1".equals(sOrderId)){    
					sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_LS; // Slabl 출하작업대기
				}else if("2".equals(sOrderId)){	
					sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_KS; // Slab  출하작업지시대기
				}
				
				if("1".equals(sOrderId)){      	
					isSucess = setDmSlabGoodsList(	sStockMoveTerm,
	    								 	  	  	sOrderDate,
	    								 	  	  	sOrderSeq,
	    								 	  	  	"Y");
					
					if(isSucess){
						isSucess = setWbookSlabGoodsList(	sOrderDate,
		    								 	 	 		sOrderSeq);			    								 	  
					}    								 	 	 
					
				}else if("2".equals(sOrderId)){	
					
					isSucess = removeWbookSlabGoodsList(	sStockMoveTerm,
														sOrderDate,
	    								 	 	    			sOrderSeq);	
				}
				
			}else{
			 
			       if("1".equals(sOrderId)){    
					
					isSucess = setDmCoilGoodsList(	sStockMoveTerm,
		    								 	  	sOrderDate,
		    								 	  	sOrderSeq,
		    								 	  	"2",
		    								 	  	"Y");
					if(isSucess){
						isSucess = setWbookGoodsList(	"DMYM002",
													sOrderDate,
		    								 	 	 	sOrderSeq);			    								 	  
					}    								 	 	 
			   	}else if("2".equals(sOrderId)){	
			    	
					isSucess = removeWbookGoodsList(	sStockMoveTerm,
													sOrderDate,
	    								 	 	    		sOrderSeq,
		    								 	  		"2",
		    								 	 		"Y");
				}
			}	
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
    }
	
	
	/**
	 * 오퍼레이션명 : 
	 *  일관제철용
	 *  제품출하상차지시/취소 등록(상차Lot)(출하 -> 야드)
	 *	차량이 공차로 계근대를 통과, 취소하는 시점
	 *	1	전문코드				TC				CHAR	07		
	 *	2	발생일자				Date				CHAR	10		YYYY-MM-DD
	 *	3	발생시간				Time				CHAR	08		HH-MM-SS
	 *	4	출하상차지시/취소 구분	DistLoadOrder	CHAR	1		‘1’:지시, ‘2’:취소
	 * 	5	출하상차지시 일자		DistLoadOrderDate	CHAR	8		
	 *	6	출하상차지시 순번		DistLoadOrderSeq	CHAR	4		
	 *
	 * 	GD // Coil 출하 상차대기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	  	 	 	 		 
	public boolean hsCyGoodsChulHaSangChaGisiRegistInfo(JDTORecord dModel){
		boolean isSucess = false;
 
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
			
			String sYdGp	 	= StringHelper.evl(dModel.getFieldString("YD_GP"),"");
 
			String sOrderDate 	= StringHelper.evl(dModel.getFieldString("TRANS_ORD_DT"),"");
			String sOrderSeq 	= StringHelper.evl(dModel.getFieldString("TRANS_ORD_SEQNO"),"");
			String szCARUD_GP	= StringHelper.evl(dModel.getFieldString("CARUD_GP"),"");
			String szYD_STK_COL_GP	= StringHelper.evl(dModel.getFieldString("YD_STK_COL_GP"),"");
 
		       
		       if(	YmCommonConst.YD_GP_0.equals(sYdGp)||
			   	YmCommonConst.YD_GP_2.equals(sYdGp)){
				/*
				 *	SLAB외판 운송지시 
				 */
  
				// 작업예약 등록
				isSucess = setWbookSlabGoodsList(sOrderDate , sOrderSeq);	 
	
				
			}else{
				/*
				 *	코일  운송지시 
				 */ 

				if(szCARUD_GP.equals("")){				 
					//작업예약 등록
					isSucess = setWbookGoodsList("DMYM002" , sOrderDate , sOrderSeq);
				}else if(szCARUD_GP.equals("U")){
					 //하차 작업예약 등록 ->스케줄 호출 
					isSucess = setWbookGoodsListNEWU( sOrderDate , sOrderSeq,szYD_STK_COL_GP);
				}else if(szCARUD_GP.equals("L")){
					 //상차 작업예약 등록  
					isSucess = setWbookGoodsListNEWL( sOrderDate , sOrderSeq,szYD_STK_COL_GP);
				}
				
							    								 	  
   		
			}	
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
    }

	/**
	 * 오퍼레이션명 : 
	 *
	 *  제품이송지시/취소 등록(일품Match)(출하 -> 야드)
	 *	제품을 타창고로 이송지시, 취소하는 시점
	 *	1	전문코드				TC					CHAR	07		
	 *	2	발생일자				Date				CHAR	10		YYYY-MM-DD
	 *	3	발생시간				Time				CHAR	08		HH-MM-SS
	 *	4	이송지시/취소 구분		TransLoadOrder		CHAR	1		‘1’:지시, ‘2’:취소
	 *	5	이송지시 일자			TransLoadOrderDate	CHAR	8		
	 *	6	이송지시 순번			TransLoadOrderSeq	CHAR	4		
	 *
	 *  GB	Coil 출하 대기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	  	 	 	 
	public boolean cyGoodsISongGisiRegistInfo(DMYM003 dModel){
		boolean isSucess = false;
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
			String sStockMoveTerm = "";
			
			String sOrderId 	= StringHelper.evl(dModel.getTransLoadOrder(),"");
			String sOrderDate 	= StringHelper.evl(dModel.getTransLoadOrderDate(),"");
			String sOrderSeq 	= StringHelper.evl(dModel.getTransLoadOrderSeq(),"");
			
			sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_KG; // Coil 출하작업지시대기
	         
	        if("1".equals(sOrderId)){      	
	        	
	        	isSucess = setDmCoilGoodsList(sStockMoveTerm,
	    								 	  sOrderDate,
	    								 	  sOrderSeq,
	    								 	  "2",
	    								 	  "N");
			}else if("2".equals(sOrderId)){	
				
				isSucess = removeWbookGoodsList(sStockMoveTerm,
												sOrderDate,
    								 	 	    sOrderSeq,
	    								 	 	"2",
	    								 	 	"N");	
			}
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
    }
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *  제품.소재 이송상차지시/취소 등록(상차Lot)(출하 -> 야드)
	 *	제품을 타창고로 이송하는 차량의 상차 Loc 편성, 취소하는 시점
	 *	1	전문코드				TC					CHAR	07		
	 *	2	발생일자				Date				CHAR	10		YYYY-MM-DD
	 *	3	발생시간				Time				CHAR	08		HH-MM-SS
	 *	4	이송상차지시/취소 구분	TransLoadOrder		CHAR	1		‘1’:지시, ‘2’:취소
	 *	5	이송상차지시 일자		TransLoadOrderDate	CHAR	8		
	 *	6	이송상차지시 순번		TransLoadOrderSeq	CHAR	4	
	 *  7   소재/제품구분            materialgoods      CHAR    1 		‘1’:소재, ‘2’:제품
	 *
	 *  GD // Coil 출하 상차대기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	  	 	 	 	 
	public boolean cyGoodsISongSangChaGisiRegistInfo(JDTORecord dModel){
		boolean isSucess = false;
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
			String sStockMoveTerm			= "";
			
			String sIsGoods 	= StringHelper.evl("1","");			
			
			String sYdGp	 	= StringHelper.evl(dModel.getFieldString("YD_GP"),"");
			String sOrderId 	= StringHelper.evl("1","");
			String sOrderDate 	= StringHelper.evl(dModel.getFieldString("TRANS_ORD_DT"),"");
			String sOrderSeq 	= StringHelper.evl(dModel.getFieldString("TRANS_ORD_SEQNO"),"");
			
			
			if("1".equals(sOrderId)){    
				if("1".equals(sIsGoods)){       	
					sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_CS; // Coil 이송대기
				}else if("2".equals(sIsGoods)){	
					sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_KG; // Coil 출하작업지시대기
				}
			}else if("2".equals(sOrderId)){	
				if("1".equals(sIsGoods)){       	
					sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_DC; // Coil 이송작업지시대기
				}else if("2".equals(sIsGoods)){	
					sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_KG; // Coil 출하작업지시대기
				}
			}
						
	        if("1".equals(sOrderId)){      	
	        	

			//작업예약작업
			isSucess = setWbookGoodsList("1".equals(sIsGoods) ? "DMYM004_1" : "DMYM004_2",
										 sOrderDate,
								 	 	 sOrderSeq);		    								 	  

	    	}else if("2".equals(sOrderId)){	
	    		
				isSucess = removeWbookGoodsList(sStockMoveTerm,
												sOrderDate,
    								 	 	    sOrderSeq,
	    								 	  	sIsGoods,
	    								 	  	"Y");
			}
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
    }
    		
	/**
	 * 오퍼레이션명 : 
	 *
	 *  제품.소재 이송상차지시/취소 등록(상차Lot)(출하 -> 야드)
	 *	제품을 타창고로 이송하는 차량의 상차 Loc 편성, 취소하는 시점
	 *	1	전문코드				TC					CHAR	07		
	 *	2	발생일자				Date				CHAR	10		YYYY-MM-DD
	 *	3	발생시간				Time				CHAR	08		HH-MM-SS
	 *	4	이송상차지시/취소 구분	TransLoadOrder		CHAR	1		‘1’:지시, ‘2’:취소
	 *	5	이송상차지시 일자		TransLoadOrderDate	CHAR	8		
	 *	6	이송상차지시 순번		TransLoadOrderSeq	CHAR	4	
	 *  7   소재/제품구분            materialgoods      CHAR    1 		‘1’:소재, ‘2’:제품
	 *
	 *  GD // Coil 출하 상차대기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	  	 	 	 	 
	public boolean cyGoodsISongSangChaGisiRegistInfo(DMYM004 dModel){
		boolean isSucess = false;
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
			String sStockMoveTerm			= "";
			
			String sOrderId 	= StringHelper.evl(dModel.getTransLoadOrder(),"");
			String sOrderDate 	= StringHelper.evl(dModel.getTransLoadOrderDate(),"");
			String sOrderSeq 	= StringHelper.evl(dModel.getTransLoadOrderSeq(),"");
			String sIsGoods 	= StringHelper.evl(dModel.getMaterialgoods(),"");
			
			if("1".equals(sOrderId)){    
				if("1".equals(sIsGoods)){       	
					sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_CS; // Coil 이송대기
				}else if("2".equals(sIsGoods)){	
					sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_KG; // Coil 출하작업지시대기
				}
			}else if("2".equals(sOrderId)){	
				if("1".equals(sIsGoods)){       	
					sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_DC; // Coil 이송작업지시대기
				}else if("2".equals(sIsGoods)){	
					sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_KG; // Coil 출하작업지시대기
				}
			}
						
	        if("1".equals(sOrderId)){      	
	        	
				isSucess = setDmCoilGoodsList(sStockMoveTerm,
	    								 	  sOrderDate,
	    								 	  sOrderSeq,
	    								 	  sIsGoods,
	    								 	  "Y");
				if(isSucess){
					//작업예약작업
					isSucess = setWbookGoodsList("1".equals(sIsGoods) ? "DMYM004_1" : "DMYM004_2",
												 sOrderDate,
	    								 	 	 sOrderSeq);		    								 	  
		        }
	    	}else if("2".equals(sOrderId)){	
	    		
				isSucess = removeWbookGoodsList(sStockMoveTerm,
												sOrderDate,
    								 	 	    sOrderSeq,
	    								 	  	sIsGoods,
	    								 	  	"Y");
			}
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
    }
    
    /**
	 * YJK	
	 *  출하TABLE에서 저장품정보를 읽어서 해당 저장품의
	 *	저장품이동경로 및 이송관련항목을 UPDATE한다.
	 *
	 * @param  String	: 저장품이동경로
	 * @param  String	: 운송지시번호
	 * @param  String	: 운송지시일련번호
	 * @param  String	: 소재/제품구분 - ‘1’:소재, ‘2’:제품
	 * @param  String	: 차량카드 체크여부
     *
     * @return boolean
     * @throws 
	 */		
    private boolean setDmCoilGoodsList(String sStockMoveTerm,
    								   String sOrderDate,
    								   String sOrderSeq,
    								   String sIsGoods,
    								   String sIsCardCheck){
    								 	
    	boolean isSucess = false;
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
			String sItem                    = "";			
			String sStockId					= "";
			String sCarCardNo				= ""; 
			String sTransWordDate			= "";
			String sTransWordSeqno			= "";
			
			YdStockDAO dao = new YdStockDAO();
			JDTORecord dto = null;
			
			//출하정보 검색
			List dmList 		= null;
            {
            	if("2".equals(sIsGoods)){ //제품
	            	dmList = dao.getDmStockInfo(sOrderDate,sOrderSeq);
	            }else if("1".equals(sIsGoods)){ //소재
	            	if( "Y".equals(sIsCardCheck)){ //상차지시 출하 TABLE 조인
	            		dmList = dao.getPmStockInfo_01(sOrderDate,sOrderSeq);
		            }else{ 						   //이송지시 공정TABLE 단독
		            	dmList = dao.getPmStockInfo_02(sOrderDate,sOrderSeq);
		        	}
	        	}
	        	if(dmList == null || dmList.size() == 0){
	            	//출하TABLE 에서 저장품정보를 가져오지 못했습니다.
	            	logger.println(LogLevel.DEBUG, this, "출하TABLE 에서 저장품정보를 가져오지 못했습니다.");
	            	//throw new EJBServiceException("=인터페이스 작업요구=>출하TABLE 저장품정보 존재안함.");
	            	return false;
	            }
	        }
            
           
            
            /** 
             *  2007.04.30 이정훈
             *  이송 상차 지시 수신 
             *  -STOCK, Stockcol 삭제
             */
            dto = (JDTORecord)dmList.get(0);
            logger.println(LogLevel.DEBUG, this, "CarCardNo		="+getField(dto, "CARD_NO"));
            
            ymCommonDAO.modifyStockCardNo(getField(dto, "CARD_NO"));
            ymCommonDAO.modifyStackcolCardNo(getField(dto, "CARD_NO"));
          
            
            //STOCK TABLE UPDATE
            int iSeq = 0;
            JDTORecord stockV	= null;
            {
            	for(int inx = 0; inx < dmList.size() ; inx++){
				 	stockV 			= (JDTORecord)dmList.get(inx);
				 	sStockId   		= StringHelper.evl(stockV.getFieldString("GOODS_NO"),"");
			 	 	sCarCardNo   	= StringHelper.evl(stockV.getFieldString("CARD_NO"),"");
				 	sTransWordDate  = StringHelper.evl(stockV.getFieldString("TRANS"),"");
				 	sTransWordSeqno = StringHelper.evl(stockV.getFieldString("SEQ"),"");
				 	
				 	if( "Y".equals(sIsCardCheck)&& // 상차지시시점에 체크한다.
				 		"".equals(sCarCardNo)){
						logger.println(LogLevel.DEBUG, this, "CARD_NO 가 존재하지 않습니다.");
		            	//throw new EJBServiceException("=인터페이스 작업요구=>CARD_NO 저장품정보 존재안함.");
						return false;
					}
			
				 	logger.println(LogLevel.DEBUG, this, "sStockId			="+sStockId);
				 	logger.println(LogLevel.DEBUG, this, "sCarCardNo		="+sCarCardNo);
				 	logger.println(LogLevel.DEBUG, this, "sTransWordDate	="+sTransWordDate);
				 	logger.println(LogLevel.DEBUG, this, "sTransWordSeqno	="+sTransWordSeqno);
					
					if("1".equals(sIsGoods)&&
					   "N".equals(sIsCardCheck)){ //소재 and 공정이송지시
					   	iSeq = dao.updateStockTransInfo_05(sStockId,
			            								   sTransWordDate,
			            								   sTransWordSeqno,
														   sStockMoveTerm); 
					}else{
						/*
						 * 2007.03.19 이정훈 
						 * 이송 상차 지시 편성 시  소재/제품 확인
						 */
						if("1".equals(sIsGoods)) {
							sItem = YmCommonConst.ITEM_CM;
						}else{
							sItem = YmCommonConst.ITEM_CG;
						}
						/*iSeq = dao.updateStockTransInfo_01(sStockId,
			            								   sCarCardNo,
			            								   sTransWordDate,
			            								   sTransWordSeqno,
														   sStockMoveTerm); */	
			            iSeq = dao.updateStockTransInfo_10(sStockId,
			            		                           sItem,
			            								   sCarCardNo,
			            								   sTransWordDate,
			            								   sTransWordSeqno,
														   sStockMoveTerm); 
					}								
					logger.println(LogLevel.DEBUG, this, "iSeq				="+iSeq);
				}								
	        }    
	        
			isSucess = true;
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
    }
    
    /**
	 * YJK	
	 *	저장품TABLE에 등록된 운송지시번호,일련번호,카드번호
	 *  항목으로 작업예약을 편성한다.
	 *
	 * @param  String	: 운송지시/취소구분
	 * @param  String	: 운송지시번호
	 * @param  String	: 운송지시일련번호
     *
     * @return boolean
     * @throws 
	 */		
    private boolean setWbookGoodsList(String sGbn,
    								  String sOrderDate,
    								  String sOrderSeq){
    								 	
    	boolean isSucess = false;
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
    		
			YdStockDAO dao = new YdStockDAO();
			CraneSchDAO cao = new CraneSchDAO();
			
			//작업예약 대상 저장품정보
			List stockList = null;
            {
               	stockList = dao.getStockList_02(sOrderDate,sOrderSeq);
	        
	        	if(stockList == null || stockList.size() == 0){
	            	//저장품정보를 가져오지 못했습니다.
	            	logger.println(LogLevel.DEBUG, this, "작업예약 대상 저장품정보를 가져오지 못했습니다.");
	            	//throw new EJBServiceException("=인터페이스 작업요구=>작업예약 대상 저장품정보 존재안함.");
	            	return false;
	            }
	        }
	        
            int iSeq 			=  0;
            String sStockId		= "";
            String sYdGp		= ""; 
			String sBayGp		= "";
			String sFromLoc		= "";
			String sDelYn		= "";
			JDTORecord stockV	= null;
			
			String sCoilList 	= "";
			String TransCom 	= "";
	    	String CarNo 		= "";
	    	JDTORecord carR 	= null;
	    	{
            	for(int inx = 0; inx < stockList.size() ; inx++){
				 	stockV 			= (JDTORecord)stockList.get(inx);
				 	sStockId   		= StringHelper.evl(stockV.getFieldString("STOCK_ID"),"");
				 	sYdGp   		= StringHelper.evl(stockV.getFieldString("YD_GP"),"");
				 	sBayGp   		= StringHelper.evl(stockV.getFieldString("BAY_GP"),"");
				 	sFromLoc   		= StringHelper.evl(stockV.getFieldString("FROM_LOC"),"");
				 	sDelYn   		= StringHelper.evl(stockV.getFieldString("DEL_YN"),"");
				 	
				 	logger.println(LogLevel.DEBUG, this, "sStockId	="+sStockId);
				 	logger.println(LogLevel.DEBUG, this, "sYdGp		="+sYdGp);
				 	logger.println(LogLevel.DEBUG, this, "sBayGp	="+sBayGp);
				 	logger.println(LogLevel.DEBUG, this, "sFromLoc	="+sFromLoc);
				 	logger.println(LogLevel.DEBUG, this, "sDelYn	="+sDelYn);
				 	
				 	/**
				 	 *  이미 출하 완료된 코일이다.
				 	 *	따라서, 다시 상차지시 편성이 와도 
				 	 *	작업예약을 편성하지 않는다.
				 	 */
				 	if("Y".equals(sDelYn)){
				 		continue;	
				 	}
				 	
				 	iSeq = callCoilWbookInfo(sGbn,
      						 sStockId,
							     sYdGp,
							     sBayGp); 
													
					logger.println(LogLevel.DEBUG, this, "iSeq ="+iSeq);
					
					/**
					 *	대기차량 전문 메세지를 구성한다.
					 *	A열연일 경우
					 */
					if(YmCommonConst.YD_GP_1.equals(sYdGp)){
						 
							sCoilList += YmCommonUtil.FillToString(sStockId,10);
						
						if(sFromLoc.indexOf("TR") != -1){
							sCoilList += YmCommonUtil.FillToString(
										 YmCommonUtil.setLegacyPositionWithCurTr(sFromLoc,
																				 sStockId),
										  8);										 
						}else{
							sCoilList += YmCommonUtil.FillToString(
										 YmCommonUtil.setLegacyPositionWithCur(sFromLoc),
										  8);										 
						}
					}
				}	
				{
					/**
					 *	대기차량 전문 메세지를 구성한다.
					 *	A열연일 경우
					 */
					if(YmCommonConst.YD_GP_1.equals(sYdGp)){
						 
						carR = cao.getDmCarInfo(sStockId);
				    	
				    	if(carR != null){
				    		TransCom	= StringHelper.evl(carR.getFieldString("TRANS_COM_CD"), "");
							CarNo		= StringHelper.evl(carR.getFieldString("CAR_NO_ADDR"), "");
				    	}
				    	
						isSucess = callCarMsgInfo(sBayGp,
											 	  TransCom,
											 	  CarNo,
											 	  "1",
											 	  stockList.size()+"",
											 	  sCoilList);	
					}
				}		
				
	        }    
	        
			isSucess = true;
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
    }
    
    
    /**
	 * YJK	
	 *	저장품TABLE에 등록된 운송지시번호,일련번호,카드번호
	 *  항목으로 작업예약을 편성한다.
	 *
	 * @param  String	: 운송지시/취소구분
	 * @param  String	: 운송지시번호
	 * @param  String	: 운송지시일련번호
     *
     * @return boolean
     * @throws 
	 */		
    private boolean setWbookGoodsListNEWU( String sOrderDate,
	    								  String sOrderSeq,
	    								  String sStackcolGp){
    								 	
    	boolean isSucess = false;
        int iSeq 			=  0;
        String sStockId		= "";
        String sYdGp		= ""; 
		String sBayGp		= "";
 
		String sDelYn		= "";
		JDTORecord stockV	= null;
		 
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
    		
			YdStockDAO dao = new YdStockDAO();
 
			
			//작업예약 대상 저장품정보
			List stockList = null;
            {
               	stockList = dao.getStockList_02(sOrderDate,sOrderSeq);
	        
	        	if(stockList == null || stockList.size() == 0){
	            	//저장품정보를 가져오지 못했습니다.
	            	logger.println(LogLevel.DEBUG, this, "작업예약 대상 저장품정보를 가져오지 못했습니다.");
	            	//throw new EJBServiceException("=인터페이스 작업요구=>작업예약 대상 저장품정보 존재안함.");
	            	return false;
	            }
	        }
	        

	    	{
            	for(int inx = 0; inx < stockList.size() ; inx++){
				 	stockV 			= (JDTORecord)stockList.get(inx);
				 	sStockId   		= StringHelper.evl(stockV.getFieldString("STOCK_ID"),"");
				 	sYdGp   		= StringHelper.evl(stockV.getFieldString("YD_GP"),"");
				 	sBayGp   		= StringHelper.evl(stockV.getFieldString("BAY_GP"),"");
 				 	sDelYn   		= StringHelper.evl(stockV.getFieldString("DEL_YN"),"");
				 	
				 	logger.println(LogLevel.DEBUG, this, "sStockId	="+sStockId);
				 	logger.println(LogLevel.DEBUG, this, "sYdGp		="+sYdGp);
				 	logger.println(LogLevel.DEBUG, this, "sBayGp	="+sBayGp);
				 	logger.println(LogLevel.DEBUG, this, "sStackcolGp	="+sStackcolGp);
				 	logger.println(LogLevel.DEBUG, this, "sDelYn	="+sDelYn);
				 	
				 	/**
				 	 *  이미 출하 완료된 코일이다.
				 	 *	따라서, 다시 상차지시 편성이 와도 
				 	 *	작업예약을 편성하지 않는다.
				 	 */
				 	if("Y".equals(sDelYn)){
				 		continue;	
				 	}
				 	
					iSeq = callCoilWbookInfoNEWU(sStockId , sYdGp , sBayGp , sStackcolGp);
													
					logger.println(LogLevel.DEBUG, this, "iSeq ="+iSeq);
					
	 
				}	
 		
				
	        }    
	        
			isSucess = true;
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
    }
    
    
    /**
	 * YJK	
	 *	저장품TABLE에 등록된 운송지시번호,일련번호,카드번호
	 *  항목으로 작업예약을 편성한다.
	 *
	 * @param  String	: 운송지시/취소구분
	 * @param  String	: 운송지시번호
	 * @param  String	: 운송지시일련번호
     *
     * @return boolean
     * @throws 
	 */		
    private boolean setWbookGoodsListNEWL( String sOrderDate,
	    								  String sOrderSeq,
	    								  String sStackcolGp ){
    								 	
    	boolean isSucess = false;
        int iSeq 			=  0;
        String sStockId		= "";
        String sYdGp		= ""; 
		String sBayGp		= ""; 
		String sDelYn		= "";
		JDTORecord stockV	= null;
		 
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
    		
			YdStockDAO dao = new YdStockDAO();
 
			
			//작업예약 대상 저장품정보
			List stockList = null;
            {
               	stockList = dao.getStockList_02(sOrderDate,sOrderSeq);
	        
	        	if(stockList == null || stockList.size() == 0){
	            	//저장품정보를 가져오지 못했습니다.
	            	logger.println(LogLevel.DEBUG, this, "작업예약 대상 저장품정보를 가져오지 못했습니다.");
	            	//throw new EJBServiceException("=인터페이스 작업요구=>작업예약 대상 저장품정보 존재안함.");
	            	return false;
	            }
	        }
	        

	    	{
            	for(int inx = 0; inx < stockList.size() ; inx++){
				 	stockV 			= (JDTORecord)stockList.get(inx);
				 	sStockId   		= StringHelper.evl(stockV.getFieldString("STOCK_ID"),"");
				 	sYdGp   		= StringHelper.evl(stockV.getFieldString("YD_GP"),"");
				 	sBayGp   		= StringHelper.evl(stockV.getFieldString("BAY_GP"),"");
 				 	sDelYn   		= StringHelper.evl(stockV.getFieldString("DEL_YN"),""); 
				 	
				 	logger.println(LogLevel.DEBUG, this, "sStockId	="+sStockId);
				 	logger.println(LogLevel.DEBUG, this, "sYdGp		="+sYdGp);
				 	logger.println(LogLevel.DEBUG, this, "sBayGp	="+sBayGp);
				 	logger.println(LogLevel.DEBUG, this, "sStackcolGp	="+sStackcolGp); 
				 	logger.println(LogLevel.DEBUG, this, "sDelYn	="+sDelYn);
				 	
				 	/**
				 	 *  이미 출하 완료된 코일이다.
				 	 *	따라서, 다시 상차지시 편성이 와도 
				 	 *	작업예약을 편성하지 않는다.
				 	 */
				 	if("Y".equals(sDelYn)){
				 		continue;	
				 	}
				 	
					iSeq = callCoilWbookInfoNEWL(sStockId , sYdGp , sBayGp , sStackcolGp);
													
					logger.println(LogLevel.DEBUG, this, "iSeq ="+iSeq);
					
	 
				}	
 		
				
	        }    
	        
			isSucess = true;
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
    }
	/**
	 * 오퍼레이션명 : 
	 *
	 *   대기차량 전문MESSAGE를 구성한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	  	 	 	     
	public boolean callCarMsgInfo(String sBay,
							 	  String sCarFm,
							 	  String sCarNo,
							 	  String sGbn,
							 	  String sCount,
							 	  String sCoilList){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
			logger.println(LogLevel.DEBUG,this,"=============대기차량정보 MESSAGE 처리 시작========"); 
								
			String sMessage = setCarMsgInfo(sBay,
											sCarFm,
											sCarNo,
											sGbn,
											sCount,
											sCoilList);
			
			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			isSuccess = (Boolean)ejbConn.trx("THHC180send",new Class[]{String.class},new Object[]{ sMessage });	
		
			logger.println(LogLevel.DEBUG,this,"=============대기차량정보 MESSAGE 처리 종료========"); 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue(); 
	}
	
    /**
	 *   전문코드		CHAR	7   0       THHC180	
	 *	 작업동			char	1	7		
	 *	 운송회사 코드	char	5	8		
	 *	 차량번호		char	5	13		
	 *	 투입구분		char	1	18		"1:상차지시	2:반입지시"
	 *	 작업대상 수량	char	1	19		
	 *	 coilno			char	10	20		10회 반복
	 *	 작업대상번지	char	8	30		
	 *							   200	
	 *
	 * @param String : COIL LIST
     *
     * @return
     * @throws 
     */	
	private String setCarMsgInfo(String sBay,
								 String sCarFm,
								 String sCarNo,
								 String sGbn,
								 String sCount,
								 String sCoilList){
		
		StringBuffer sMsg = new StringBuffer();

		String TC				= "";
		String BAY				= "";
		String CARFM			= "";
		String CARNO			= "";
		String GBN				= "";
		String COUNT			= "";
		String COILLIST			= "";
				
		int iTC					=  7;
		int iBAY				=  1;
		int iCARFM				=  5;
		int iCARNO				=  5;
		int iGBN				=  1;
		int iCOUNT				=  1;
		int iCOILLIST			=180;
		
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return null;
    		}
			TC			= YmCommonConst.TC_THHC180;
			BAY			= sBay;
			CARFM		= sCarFm;
			CARNO		= sCarNo;
			GBN			= sGbn;
			COUNT		= sCount;
			COILLIST	= sCoilList;
					    
			sMsg.append(YmCommonUtil.FillToString(TC		,iTC));
			sMsg.append(YmCommonUtil.FillToString(BAY		,iBAY));
			sMsg.append(YmCommonUtil.FillToString(CARFM		,iCARFM));
			sMsg.append(YmCommonUtil.FillToString(CARNO		,iCARNO));
			sMsg.append(YmCommonUtil.FillToString(GBN		,iGBN));
			sMsg.append(YmCommonUtil.FillToString(COUNT		,iCOUNT));
			sMsg.append(YmCommonUtil.FillToString(COILLIST	,iCOILLIST));
			
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sMsg.toString();
	}
	
    /**
     * 저장품에 대해 
     * 작업예약을 호출한다.
     *
     * @param String : 저장품ID
     * 
     * @return
     * @throws  
     */	
	private int callCoilWbookInfo(String sGbn,
								  String sStockId,
								  String sYdGp,
							      String sBayGp){
		
		int iSeq = 1;
		
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return 0;
    		}
			YdStockDAO ydStockDAO 	        = new YdStockDAO();
			YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
			YdWBookDAO ydWBookDAO           = new YdWBookDAO();
			
			logger.println(LogLevel.DEBUG, this, "=======작업예약 생성 시작========");
			
            // 적치단  Table Update(작업요구상태='S'로 변경)
			// UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'S' WHERE STOCK_ID = ?
			/*
			 * 2007.03.19
			 * 이정훈 이송 상차 지시 작업 예약 생성시 'L','S','U' 만  
			 */
			String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark_01";
			int stkColGp = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ YmCommonConst.STACK_LAYER_STAT_S, 
																							 sStockId});
					
			// 작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
			String wBookQueryId = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";
			JDTORecord wBookSel = ydStackLayerDAO.requestFind(wBookQueryId);
			String wBookid      = wBookSel.getFieldString("WBOOK_ID");
					
			String sSchCode 		= "";
			String sStockMoveTerm 	= "";
						
			if("DMYM004_1".equals(sGbn)){
				//소재 이송상차지시(이송지시)
				sSchCode 		= YmCommonConst.NEW_SCH_WORK_KIND_CVML; //COIL 소재이송상차 
				sStockMoveTerm 	= YmCommonConst.NEW_STOCK_MOVE_TERM_CS; //Coil 이송대기
			}else if("DMYM004_2".equals(sGbn)){
				//제품 이송상차지시(이송지시)
				sSchCode 		= YmCommonConst.NEW_SCH_WORK_KIND_GVML; //COIL 제품이송상차
				sStockMoveTerm 	= YmCommonConst.NEW_STOCK_MOVE_TERM_KG; //Coil 출하작업지시대기
			}else if("DMYM002".equals(sGbn)){
				//제품 출하상차지시(출하지시)
//				sSchCode 		= YmCommonConst.NEW_SCH_WORK_KIND_GVFL; //COIL 제품출하상차
				sStockMoveTerm 	= YmCommonConst.NEW_STOCK_MOVE_TERM_LG; //Coil 출하작업대기	
				
				List chkList = null;
				ymCommonDAO dao2 = ymCommonDAO.getInstance();
				String QueryId 	= "com.inisteel.cim.yd.dao.chklist";
				chkList = dao2.getCommonList(QueryId, new Object[]{});

			    JDTORecord unloadPointrec = (JDTORecord)chkList.get(0);
		    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");	    	
 		    	logger.println(LogLevel.DEBUG, this, "◑◑◑◑◑ TC_CODE:callCoilWbookInfo , CHK:"+CHK);
		    	if(CHK.equals("Y")){
		    		//----------------------------------------------------------------------------
					CraneSchDAO dao  	= new CraneSchDAO();
					String szCAR_KIND 	= "";
					JDTORecord stockJr 	= dao.getStockInfo(sStockId);
					if(stockJr != null){
						szCAR_KIND = StringHelper.evl(stockJr.getFieldString("SHEAR_SUPPLY_GP"), "");
						if("".equals(szCAR_KIND)){
							szCAR_KIND = "T";
						}
					}
					
					// 차량구분
					if ("TT".equals(szCAR_KIND)) {
						sSchCode 		= YmCommonConst.NEW_SCH_WORK_KIND_GTFL; //COIL TT제품출하상차
					} else if ("PT".equals(szCAR_KIND)) {
						sSchCode 		= YmCommonConst.NEW_SCH_WORK_KIND_GPFL; //COIL PT제품출하상차
					} else if ("T".equals(szCAR_KIND)||"TR".equals(szCAR_KIND)) {
						sSchCode 		= YmCommonConst.NEW_SCH_WORK_KIND_GVFL; //COIL TR제품출하상차
					} else {
						sSchCode 		= YmCommonConst.NEW_SCH_WORK_KIND_GVFL; //COIL TR제품출하상차
					}
					//-----------------------------------------------------------------------------
		    	}else{
		    		sSchCode 		= YmCommonConst.NEW_SCH_WORK_KIND_GVFL; //COIL 제품출하상차
		    	}
			} 
						
			String sWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
			iSeq = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ wBookid, 
																			 sYdGp, 
																			 sBayGp,
																			 sSchCode, 
																			 YmCommonUtil.getWorkDuty(),
																			 YmCommonUtil.getWorkParty()});	
			
			// 저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
			// UPDATE TB_YM_STOCK SET WBOOK_ID= ?, STOCK_MOVE_TERM(저장품이동조건) = ?	 WHERE STOCK_ID = ?
			String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
			iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ wBookid, 
																		  sStockMoveTerm, 
																		  sStockId});	
			
			logger.println(LogLevel.DEBUG, this, "=======작업예약 생성 끝========");
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return iSeq; 
	}


/**
 * 저장품에 대해 
 * 작업예약을 호출한다.
 *
 * @param String : 저장품ID
 * 
 * @return
 * @throws  
 */	

	private int callCoilWbookInfoNEWU(  String sStockId, String sYdGp, String sBayGp, String sStackColGp) {

		int iSeq = 1;

		try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return 0;
    		}
			YdStockDAO ydStockDAO = new YdStockDAO();
			YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
			YdWBookDAO ydWBookDAO = new YdWBookDAO();

			logger.println(LogLevel.DEBUG , this , "=======callCoilWbookInfoNEWU 작업예약 생성 시작========");

			// 적치단 Table Update(작업요구상태='S'로 변경)
			// UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'S' WHERE STOCK_ID = ?

			String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark_01";
			int stkColGp = ydStackLayerDAO.requestupdateData(sStkLayerQueryId , new Object[]{YmCommonConst.STACK_LAYER_STAT_S, sStockId});

			// 작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
			String wBookQueryId = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";
			JDTORecord wBookSel = ydStackLayerDAO.requestFind(wBookQueryId);
			String wBookid = wBookSel.getFieldString("WBOOK_ID");

			String sSchCode = "";
			String sStockMoveTerm = "";

			//=======================================================================
			//**********************	스케줄 코드 생성 	******************************
			//=======================================================================
			if (YmCommonConst.YD_GP_1.equals(sYdGp)) {

				// H동 제품이송 하차 GATE 4,5번은 49번 크레인 작업 진행(G,F동 추가 2018.05.28)
				if (YmCommonConst.BAY_GP_H.equals(sBayGp) || YmCommonConst.BAY_GP_G.equals(sBayGp)|| YmCommonConst.BAY_GP_F.equals(sBayGp)) {
					if ("3".equals(sStackColGp.substring(5 , 6)) || "4".equals(sStackColGp.substring(5 , 6)) || "5".equals(sStackColGp.substring(5 , 6))
							|| "6".equals(sStackColGp.substring(5 , 6)) || "7".equals(sStackColGp.substring(5 , 6))) {
						sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_GVM4;
					} else {
						sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_GVMU;
					}

				} else {
					sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_GVMU;
				}

			} else if (YmCommonConst.YD_GP_3.equals(sYdGp)) {
				if ("3".equals(sStackColGp.substring(5 , 6)) || "4".equals(sStackColGp.substring(5 , 6))) {
					sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_GVM4;

				} else {
					sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_GVMU;
				}
			}
			//=======================================================================
		
			sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_EC; // Coil 이송작업대기

			String sWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
			iSeq = ydWBookDAO.requestinsertData(sWBookQueryId , new Object[]{wBookid, sYdGp, sBayGp, sSchCode, YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty()});

			// 저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
			// UPDATE TB_YM_STOCK SET WBOOK_ID= ?, STOCK_MOVE_TERM(저장품이동조건) = ? WHERE STOCK_ID = ?
			String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
			iSeq = ydStockDAO.requestupdateData(stkQueryId , new Object[]{wBookid, sStockMoveTerm, sStockId});

			logger.println(LogLevel.DEBUG , this , "=======callCoilWbookInfoNEWU 작업예약 생성 끝========");
			
			
			
			logger.println(LogLevel.DEBUG , this , "=======callCoilWbookInfoNEWU 크레인 스케쥴 호출========");
			
			//크레인 스케쥴 호출************************************************************ 
 			EJBConnector ejbConn2 = new EJBConnector("default","JNDICraneSchReg",this);
 			Boolean isTrue2		  = (Boolean)ejbConn2.trx( "callCraneSchInfo",new  Class[]{String.class},new Object[]{wBookid});
			
		 
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iSeq;
	}
	
	/**
	 * 저장품에 대해 
	 * 작업예약을 호출한다.
	 *
	 * @param String : 저장품ID
	 * 
	 * @return
	 * @throws  
	 */	

		private int callCoilWbookInfoNEWL(  String sStockId, String sYdGp, String sBayGp, String sStackColGp) {

			int iSeq = 1;

			try {
	    		/*
	    		 * 구자원 단계별 삭제 로직  
	    		 */
	    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
	    		if(sAPP060_OLDSRC_YN.equals("Y")){
	    			return 0;
	    		}
				YdStockDAO ydStockDAO = new YdStockDAO();
				YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
				YdWBookDAO ydWBookDAO = new YdWBookDAO();

				logger.println(LogLevel.DEBUG , this , "=======callCoilWbookInfoNEWL 작업예약 생성 시작========");

				// 적치단 Table Update(작업요구상태='S'로 변경)
				// UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'S' WHERE STOCK_ID = ?

				String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark_01";
				int stkColGp = ydStackLayerDAO.requestupdateData(sStkLayerQueryId , new Object[]{YmCommonConst.STACK_LAYER_STAT_S, sStockId});

				// 작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
				String wBookQueryId = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";
				JDTORecord wBookSel = ydStackLayerDAO.requestFind(wBookQueryId);
				String wBookid = wBookSel.getFieldString("WBOOK_ID");

				String sSchCode = "";
				String sStockMoveTerm = "";

				//=======================================================================
				//**********************	스케줄 코드 생성 	******************************
				//=======================================================================
				if (YmCommonConst.YD_GP_1.equals(sYdGp)) {
	
					// H동 제품이송 상차 GATE 4,5번은 49번 크레인 작업 진행
					if (YmCommonConst.BAY_GP_H.equals(sBayGp)|| YmCommonConst.BAY_GP_G.equals(sBayGp) ||YmCommonConst.BAY_GP_F.equals(sBayGp)) {
						if ("3".equals(sStackColGp.substring(5 , 6)) || "4".equals(sStackColGp.substring(5 , 6)) || "5".equals(sStackColGp.substring(5 , 6)) || "6".equals(sStackColGp.substring(5 , 6))
								|| "7".equals(sStackColGp.substring(5 , 6))) {
							sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_GVM2;
						} else {
							sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_GVML;
						}
	
					} else {
						sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_GVML;
					}
					
				} else if (YmCommonConst.YD_GP_3.equals(sYdGp)) {
					if ("3".equals(sStackColGp.substring(5 , 6)) || "4".equals(sStackColGp.substring(5 , 6))) {
						sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_GVM2;
					} else if (YmCommonConst.BAY_GP_E.equals(sStackColGp.subSequence(1 , 2))) {
						if ("5".equals(sStackColGp.substring(5 , 6)) || "6".equals(sStackColGp.substring(5 , 6))) {
							sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_GVM3;
						}else {
							sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_GVML;
						}
					} else {
						sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_GVML;	
					}
				}
			        
				//=======================================================================
			
				sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_KG; // Coil 출하작업지시대기

				String sWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
				iSeq = ydWBookDAO.requestinsertData(sWBookQueryId , new Object[]{wBookid, sYdGp, sBayGp, sSchCode, YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty()});

				// 저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
				// UPDATE TB_YM_STOCK SET WBOOK_ID= ?, STOCK_MOVE_TERM(저장품이동조건) = ? WHERE STOCK_ID = ?
				String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
				iSeq = ydStockDAO.requestupdateData(stkQueryId , new Object[]{wBookid, sStockMoveTerm, sStockId});

				logger.println(LogLevel.DEBUG , this , "=======callCoilWbookInfoNEWL 작업예약 생성 끝========");
				
	   
			} catch (DAOException daoe) {
				throw daoe;
			} catch (Exception e) {
				throw new EJBServiceException(e);
			}
			return iSeq;
		}
		
		
	/**
	 * 오퍼레이션명 : 
	 *
	 *	저장품TABLE에 등록된 운송지시번호,일련번호,카드번호
	 *  항목으로 작업예약을 삭제한다.
	 *
	 * param  String	: 저장품이동조건
	 * param  String	: 운송지시번호
	 * param  String	: 운송지시일련번호
        * param  String	: 소재/제품구분 - ‘1’:소재, ‘2’:제품
	 * param  String	: 차량카드 체크여부
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	  	 	 	     
    public boolean removeWbookGoodsList(String sStockMoveTerm,
    								    String sOrderDate,
    								    String sOrderSeq,
    								    String sIsGoods,
    								    String sIsCardCheck){
    								 	
    	boolean isSucess = false;
		try{
			
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
			YdStockDAO  dao   = new YdStockDAO();
			CraneSchDAO cDao  = new CraneSchDAO();
			
			//작업예약 대상 저장품정보
			List stockList = null;
            {
            	int iWb = cDao.deleteAllWbookId();
            	/**
            	 *	출하완료 시점에 해당 정보에 대한
            	 *  작업예약 및 스케쥴 정보가 있으면 
            	 *	모두 삭제처리한다.
            	 */
               	if("YJK".equals(sIsGoods)){
               		
               		stockList =	cDao.getYmDmCommonInfo(sIsCardCheck);
               	
               	/**
            	 *	상차지시 취소시 
            	 */	
               	}else{
               	
		        	if("1".equals(sIsGoods)&&
					   "N".equals(sIsCardCheck)){ //소재 and 공정이송지시취소
					   	
					   	stockList = dao.getStockList_01(sOrderDate,sOrderSeq);
					}else{
			            
			            stockList = dao.getStockList_02(sOrderDate,sOrderSeq);
					}	
					
					if(stockList == null || stockList.size() == 0){
		            	//저장품정보를 가져오지 못했습니다.
		            	logger.println(LogLevel.DEBUG, this, "작업취소 대상 저장품정보를 가져오지 못했습니다.");
		            	throw new EJBServiceException("=인터페이스 작업요구=>작업취소 대상 저장품정보 존재안함.");
		            }		
				}
			}
	        
            //작업예정 생성
            int iSeq = 0;
            String sStockId					= "";
            String sWbookId					= "";
			
            JDTORecord stockV	= null;
            
            if(stockList != null)
            {
            	for(int inx = 0; inx < stockList.size() ; inx++){
				 	stockV 			= (JDTORecord)stockList.get(inx);
				 	sStockId   		= StringHelper.evl(stockV.getFieldString("STOCK_ID"),"");
				 	sWbookId   		= StringHelper.evl(stockV.getFieldString("WBOOK_ID"),"");
				 	
				 	logger.println(LogLevel.DEBUG, this, "sStockId	="+sStockId);
				 	logger.println(LogLevel.DEBUG, this, "sWbookId	="+sWbookId);
				 	
				 	/*
				 	 * 1.	작업예약 생성여부 체크
				 	 */ 
				 		if("".equals(sWbookId)){
				 			logger.println(LogLevel.DEBUG, this, "출하 취소 작업예약 존재안함.");
				 		} 
				 	/*
				 	 * 2.	스케쥴 생성여부 체크
				 	 *		스케쥴이 있으면 스케쥴을 삭제한다.
				 	 */  
				 	 	if(!"".equals(sWbookId)){
				 	 		
				 	 		JDTORecord schV	= cDao.getSchInfoWithWbookId(sWbookId,sStockId);
				 	 		
				 	 		if(schV != null){
				 	 			logger.println(LogLevel.DEBUG, this, "출하 취소 스케쥴정보 존재함.");
				 	 			/**
				 	 			 *	2.1	스케쥴 취소 모듈 CALL
				 	 			 */
				 	 			EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
			    				Boolean isTemp  = (Boolean)ejbConn.trx("cancelCoilSchInfo",
															new  Class[]{String.class},
															new Object[]{StringHelper.evl(schV.getFieldString("SCH_ID"),"")});
				 	 		}
				 	 	}
				 	/*
				 	 * 3.	저장품 정보 초기화
				 	 */ 
				 		iSeq = dao.updateStockTransInfo_02(sStockId,
			            								   "", //wbook_id
			            								   "", //car_card_no 
			            								   "", //frtomove_word_no
			            								   "", //trans_word_no
														   sStockMoveTerm);  
		            /*
				 	 * 4.	적치단  Table Update(STACK_LAYER_STAT = 적치중('L')로 변경)
				 	 */ 
				 	 	if(!"".equals(sWbookId)){
							iSeq = cDao.updateStackLayerStatWithStockId(YmCommonConst.STACK_LAYER_STAT_L,
																		sStockId);
						}
				 	/*
				 	 * 5.	작업예약 Table Delete
				 	 */ 
				 	 	if(!"".equals(sWbookId)){
							iSeq = cDao.deleteWbookInfo(sWbookId);
						}
				}								
	        }    
	        logger.println(LogLevel.DEBUG, this, "=======작업예약 취소 끝========");
			isSucess = true;
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
    }
	
	/**
	* YJK	
	*  	출하TABLE에서 저장품정보를 읽어서 해당 저장품의
	*	저장품이동경로 및 외판출하관련항목을 UPDATE한다.
	*
	* @param  String	: 저장품이동경로
	* @param  String	: 운송지시번호
	* @param  String	: 운송지시일련번호
	* @param  String	: 차량카드 체크여부
	*
	* @return boolean
	* @throws 
	*/		
	private boolean setDmSlabGoodsList(	String sStockMoveTerm,
    								   	String sOrderDate,
    								   	String sOrderSeq,
    								   	String sIsCardCheck){
    								 	
		boolean isSucess = false;
		
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
			String sStockId			= "";
			String sCarCardNo			= ""; 
			String sTransWordDate		= "";
			String sTransWordSeqno		= "";
			
			YdStockDAO dao = new YdStockDAO();
			JDTORecord dto = null;
			
			//출하정보 검색
			List dmList 		= null;
            		{
            	
	            		dmList = dao.getDmStockInfo(sOrderDate,sOrderSeq);
	       
		        	if(dmList == null || dmList.size() == 0)
		        	{
			            	logger.println(LogLevel.DEBUG, this, "SLAB외판 => 출하TABLE 에서 저장품정보를 가져오지 못했습니다.="+sOrderDate+"/"+sOrderSeq);
			            	return false;
		            	}
		        }
            
	           	 //STOCK TABLE UPDATE
	            	int iSeq = 0;
	            	JDTORecord stockV	= null;
	            	{
	            		for(int inx = 0; inx < dmList.size() ; inx++){
					 	
				 	stockV 			= (JDTORecord)dmList.get(inx);
				 	sStockId   		= StringHelper.evl(stockV.getFieldString("GOODS_NO"),"");
			 	 	sCarCardNo   		= StringHelper.evl(stockV.getFieldString("CARD_NO"),"");
				 	sTransWordDate  	= StringHelper.evl(stockV.getFieldString("TRANS"),"");
				 	sTransWordSeqno 	= StringHelper.evl(stockV.getFieldString("SEQ"),"");
				 	
					if( "Y".equals(sIsCardCheck)&& // 상차지시시점에 체크한다.
					   "".equals(sCarCardNo)){
						logger.println(LogLevel.DEBUG, this, "SLAB외판 => CARD_NO 가 존재하지 않습니다.");
			            		throw new EJBServiceException("SLAB외판 => 인터페이스 작업요구=>CARD_NO 저장품정보 존재안함.");
					}
				
				 	logger.println(LogLevel.DEBUG, this, "sStockId			="+sStockId);
				 	logger.println(LogLevel.DEBUG, this, "sCarCardNo		="+sCarCardNo);
				 	logger.println(LogLevel.DEBUG, this, "sTransWordDate		="+sTransWordDate);
				 	logger.println(LogLevel.DEBUG, this, "sTransWordSeqno	="+sTransWordSeqno);
					
					iSeq = dao.updateStockTransInfo_01(	sStockId,
				            								sCarCardNo,
				            								sTransWordDate,
				            								sTransWordSeqno,
													sStockMoveTerm); 
					logger.println(LogLevel.DEBUG, this, "iSeq	="+iSeq);
				}								
		        }    
	        
			isSucess = true;
		}catch(DAOException daoe) {
	            throw daoe;
	        }catch(Exception e) {
	            throw new EJBServiceException(e);
	        }
        	
        	return isSucess;
	}	
	

	/**
	 * 오퍼레이션명 : 
	 *
	 *	저장품TABLE에 등록된 운송지시번호,일련번호,카드번호
	 *  	항목으로 작업예약을 삭제한다.
	 *
	 * param  String	: 저장품이동조건
	 * param  String	: 운송지시번호
	 * param  String	: 운송지시일련번호
	 * param  String	: 차량카드 체크여부
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	  	 	 	    	
	public boolean removeWbookSlabGoodsList(	String sStockMoveTerm,
    								    		String sOrderDate,
    								    		String sOrderSeq){
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
    		return removeWbookSlabGoodsList(	sStockMoveTerm,
    								    		sOrderDate,
    								    		sOrderSeq,
    								    		"");
    	}
	/**
	 * 오퍼레이션명 : 
	 *
	 *	저장품TABLE에 등록된 운송지시번호,일련번호,카드번호
	 *  	항목으로 작업예약을 삭제한다.
	 *
	 * param  String	: 저장품이동조건
	 * param  String	: 운송지시번호
	 * param  String	: 운송지시일련번호
	 * param  String	: 차량카드 체크여부
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	  
    	public boolean removeWbookSlabGoodsList(	String sStockMoveTerm,
    								    		String sOrderDate,
    								    		String sOrderSeq,
    								    		String sCardStockId){
    								    										 	
		boolean isSucess = false;
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
			YdStockDAO  	dao   	= new YdStockDAO();
			CraneSchDAO cDao  	= new CraneSchDAO();
			
			//작업예약 대상 저장품정보
			List stockList = null;
            		
	            	/**
	            	 *	출하완료 시점에 해당 정보에 대한
	            	 *  	작업예약 및 스케쥴 정보가 있으면 
	            	 *	모두 삭제처리한다.
	            	 */
               	if(!"".equals(sCardStockId)){
               		
               		stockList =	cDao.getYmDmCommonInfo(sCardStockId);
               	
               	}else{
               	
			/**
			*	SLAB외판 운송/상차지시 취소시 
			*/	
				
				stockList = dao.getStockList_02(sOrderDate,sOrderSeq);
					
				if(stockList == null || stockList.size() == 0){
			            	logger.println(LogLevel.DEBUG, this, "SLAB외판 => 작업취소 대상 저장품정보를 가져오지 못했습니다.");
			            	throw new EJBServiceException("SLAB외판 => 작업취소 대상 저장품정보 존재안함.");
		            	}
			}
	        	
			//작업예정 취소
			int iSeq 				= 0;
			String sStockId		= "";
			String sWbookId		= "";
			
			JDTORecord stockV	= null;
	            
			if(stockList != null)
			{
				for(int inx = 0; inx < stockList.size() ; inx++){
				 	
					stockV 			= (JDTORecord)stockList.get(inx);
					sStockId   		= StringHelper.evl(stockV.getFieldString("STOCK_ID"),"");
					sWbookId   		= StringHelper.evl(stockV.getFieldString("WBOOK_ID"),"");
					
					logger.println(LogLevel.DEBUG, this, "sStockId	="+sStockId);
					logger.println(LogLevel.DEBUG, this, "sWbookId	="+sWbookId);

				 	/*
				 	 * 1.	작업예약 생성여부 체크
				 	 */ 
				 		if("".equals(sWbookId)){
				 			logger.println(LogLevel.DEBUG, this, "SLAB외판 => 출하 취소 작업예약 존재안함.");
				 		} 
				 	/*
				 	 * 2.	스케쥴 생성여부 체크
				 	 *	스케쥴이 있으면 스케쥴을 삭제한다.
				 	 */  
				 	 	if(!"".equals(sWbookId)){
				 	 		
				 	 		JDTORecord schV	= cDao.getSchInfoWithWbookId(sWbookId,sStockId);
				 	 		
				 	 		if(schV != null){
				 	 			logger.println(LogLevel.DEBUG, this, "SLAB외판 => 출하 취소 스케쥴정보 존재함.");
				 	 			/**
				 	 			 *	2.1	스케쥴 취소 모듈 CALL
				 	 			 */
				 	 			EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
			    					Boolean isTemp  = (Boolean)ejbConn.trx("cancelSlabSchInfo",
															new  Class[]{String.class},
															new Object[]{StringHelper.evl(schV.getFieldString("SCH_ID"),"")});
				 	 		}
				 	 	}
				 	/*
				 	 * 3.	저장품 정보 초기화
				 	 */ 
				 		iSeq = dao.updateStockTransInfo_02(sStockId,
				            								   "", //wbook_id
				            								   "", //car_card_no 
				            								   "", //frtomove_word_no
				            								   "", //trans_word_no
													   sStockMoveTerm);  
		            		/*
				 	 * 4.	적치단  Table Update(STACK_LAYER_STAT = 적치중('L')로 변경)
				 	 */ 
				 	 	if(!"".equals(sWbookId)){
							iSeq = cDao.updateStackLayerStatWithStockId(YmCommonConst.STACK_LAYER_STAT_L,
																   sStockId);
						}
				 	/*
				 	 * 5.	작업예약 Table Delete
				 	 */ 
				 	 	if(!"".equals(sWbookId)){
							iSeq = cDao.deleteWbookInfo(sWbookId);
						}
				}								
			}    
	        	logger.println(LogLevel.DEBUG, this, "SLAB외판 => =======작업예약 취소 끝========");
			isSucess = true;
		}catch(DAOException daoe) {
	            throw daoe;
	        }catch(Exception e) {
	            throw new EJBServiceException(e);
	        }
	        return isSucess;
	}
	
	/**
	* YJK	
	*	저장품TABLE에 등록된 운송지시번호,일련번호,카드번호
	*  	항목으로 작업예약을 편성한다.
	*
	* @param  String	: 운송지시번호
	* @param  String	: 운송지시일련번호
	*
	* @return boolean
	* @throws 
	*/		
	private boolean setWbookSlabGoodsList(	String sOrderDate,
    								  		String sOrderSeq){
    								 	
		boolean isSucess = false;
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
    		
			YdStockDAO dao = new YdStockDAO();
			CraneSchDAO cao = new CraneSchDAO();
			
			//작업예약 대상 저장품정보
			List stockList = null;
	            	{
	              	stockList = dao.getStockList_02(sOrderDate,sOrderSeq);
		        
		        	if(stockList == null || stockList.size() == 0){
			            	logger.println(LogLevel.DEBUG, this, "SLAB외판 => 작업예약 대상 저장품정보를 가져오지 못했습니다.");
			            	throw new EJBServiceException("SLAB외판 => 인터페이스 작업요구=>작업예약 대상 저장품정보 존재안함.");
		            	}
		        }
		        
			int iSeq 				=  0;
			String sStockId		= "";
			String sYdGp			= ""; 
			String sBayGp			= "";
			String sFromLoc		= "";
			String sDelYn			= "";
			JDTORecord stockV	= null;
			
			String sCoilList 		= "";
			String TransCom 		= "";
			String CarNo 			= "";
			JDTORecord carR 		= null;
		    	{
	            		for(int inx = 0; inx < stockList.size() ; inx++){
				 	stockV 			= (JDTORecord)stockList.get(inx);
				 	sStockId   		= StringHelper.evl(stockV.getFieldString("STOCK_ID"),"");
				 	sYdGp   			= StringHelper.evl(stockV.getFieldString("YD_GP"),"");
				 	sBayGp   		= StringHelper.evl(stockV.getFieldString("BAY_GP"),"");
				 	sFromLoc   		= StringHelper.evl(stockV.getFieldString("FROM_LOC"),"");
				 	sDelYn   			= StringHelper.evl(stockV.getFieldString("DEL_YN"),"");
				 	
				 	logger.println(LogLevel.DEBUG, this, "sStockId	="+sStockId);
				 	logger.println(LogLevel.DEBUG, this, "sYdGp		="+sYdGp);
				 	logger.println(LogLevel.DEBUG, this, "sBayGp	="+sBayGp);
				 	logger.println(LogLevel.DEBUG, this, "sFromLoc	="+sFromLoc);
				 	logger.println(LogLevel.DEBUG, this, "sDelYn	="+sDelYn);
				 	
				 	/**
				 	 *	이미 출하 완료된 SLAB이다.
				 	 *	따라서, 다시 상차지시 편성이 와도 
				 	 *	작업예약을 편성하지 않는다.
				 	 */
				 	if("Y".equals(sDelYn)){
				 		continue;	
				 	}
				 	
		            		iSeq = callSlabWbookInfo(	sStockId,
        								     		sYdGp,
        								     		sBayGp); 
													
					logger.println(LogLevel.DEBUG, this, "iSeq ="+iSeq);
					
				}	
				
	        	}    
	        
			isSucess = true;
		}catch(DAOException daoe) {
	            throw daoe;
	        }catch(Exception e) {
	            throw new EJBServiceException(e);
	        }
	        return isSucess;
    	}
    	
	/**
	* 저장품에 대해 
	* 작업예약을 호출한다.
	*
	* @param String : 저장품ID
	* 
	* @return
	* @throws  
	*/	
	private int callSlabWbookInfo(	String sStockId,
							String sYdGp,
							String sBayGp){
		
		int iSeq = 1;
		
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return 0;
    		}
			YdStockDAO ydStockDAO 	        	= new YdStockDAO();
			YdStackLayerDAO ydStackLayerDAO 	= new YdStackLayerDAO();
			YdWBookDAO ydWBookDAO           	= new YdWBookDAO();
			
			logger.println(LogLevel.DEBUG, this, "SLAB외판 => ======작업예약 생성 시작========");
			
            		// 적치단  Table Update(작업요구상태='S'로 변경)
			// UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'S' WHERE STOCK_ID = ?
			String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark_01";
			int stkColGp = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ YmCommonConst.STACK_LAYER_STAT_S, 
																				   sStockId});
					
			// 작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
			String wBookQueryId 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";
			JDTORecord wBookSel 	= ydStackLayerDAO.requestFind(wBookQueryId);
			String wBookid      		= wBookSel.getFieldString("WBOOK_ID");
					
			String sSchCode 		= "";
			String sStockMoveTerm 	= "";
						
			//SLAB외판 출하상차지시
			sSchCode 		= YmCommonConst.NEW_SCH_WORK_KIND_SVFL; 	//SLAB외판 출하상차
			sStockMoveTerm 	= YmCommonConst.NEW_STOCK_MOVE_TERM_LS; 	//SLAB외판 출하작업대기	
						
			String sWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
			iSeq = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ 	wBookid, 
																		 sYdGp, 
																		 sBayGp,
																		 sSchCode, 
																		 YmCommonUtil.getWorkDuty(),
																		 YmCommonUtil.getWorkParty()});	
			
			// 저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
			// UPDATE TB_YM_STOCK SET WBOOK_ID= ?, STOCK_MOVE_TERM(저장품이동조건) = ?	 WHERE STOCK_ID = ?
			String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
			iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ wBookid, 
																  sStockMoveTerm, 
																  sStockId});	
			
			logger.println(LogLevel.DEBUG, this, "SLAB외판 => ======작업예약 생성 끝========");
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return iSeq; 
	}
	

	   ///////////////////////////////////////////////////////////////////////////
    ////////////////////////YJK END////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/////////////////////B열연수정시작///////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
 	/**
	 * 오퍼레이션명 : 
	 *
	 *  정정지시 등록(조업 -> 야드)
	 *	1	전문코드		TC				CHAR	07		
	 *	2	발생일자		Date			CHAR	10		YYYY-MM-DD
	 *	3	발생시간		Time			CHAR	08		HH-MM-SS
	 *	4	공장구분		PlantGp			CHAR	01 		1,2,3,4
	 *	5	공정구분		ProcGp			CHAR	01		K(SPM),H(HFL)
	 *	6	작업지시단위명	WordUnitName	CHAR	06	
	 *
	 *  D2 // COIL 정정보급대기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	  
	public boolean receivePOYM003(JDTORecord rcvMsg){
//	public boolean cyJungJungGisiRegistInfo(POYM003 dModel){	
		boolean isSucess = false;
		 logger.println(LogLevel.DEBUG, this, "WorkOrderInfoRegEJB:receivePOYM003(POYM003)");
		try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
    		
			String sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_CC; // COIL 정정작업대기
			
//S			String sPlantGp			= StringHelper.evl(dModel.getPlantGp(),"");
//S			String sProcGp   		= StringHelper.evl(dModel.getProcGp(),"");
//S			String sWordUnitName   	= StringHelper.evl(dModel.getWordUnitName(),"");
			
//			String sPlantGp			= StringHelper.evl(rcvMsg.getFieldString("plantGp"),"").trim();
//			String sProcGp   		= StringHelper.evl(rcvMsg.getFieldString("procGp"),"").trim();
//			String sWordUnitName   	= StringHelper.evl(rcvMsg.getFieldString("wordUnitName"),"").trim();

			String sPlantGp			= StringHelper.evl(rcvMsg.getFieldString("PlantGp"),"").trim();
			String sProcGp   		= StringHelper.evl(rcvMsg.getFieldString("ProcGp"),"").trim();
			String sWordUnitName   	= StringHelper.evl(rcvMsg.getFieldString("WordUnitName"),"").trim();

			logger.println(LogLevel.DEBUG,this," PlantGp 		=" + sPlantGp);
			logger.println(LogLevel.DEBUG,this," ProcGp 		=" + sProcGp);
			logger.println(LogLevel.DEBUG,this," WordUnitName 	=" + sWordUnitName);
			
			isSucess = setPoPmCoilGoodsList(sStockMoveTerm,
    								 	    sPlantGp,
    								 	    sProcGp,
    								 	    sWordUnitName);
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) { 
            throw new EJBServiceException(e);
        }
        return isSucess;
    }	
}	

