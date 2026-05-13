package com.inisteel.cim.ym.ilkwan.session;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import jspeed.base.record.JDTORecord;

import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.util.CommonUtil;
import com.inisteel.cim.common.util.MessageHelper;
//import com.inisteel.cim.ps.pup.suloading.dao.InoutportDAO;
//import com.inisteel.cim.ps.settledown.ar.dao.PayininfoDAO;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ym.ilkwan.dao.YdMtlStatModHistDao;
import com.inisteel.cim.ym.ilkwan.dao.YdStockDao;
import com.inisteel.cim.ym.ilkwan.dao.ilkwanDAO;
import com.inisteel.cim.ym.scheduling.crane.dao.StockingBlncBasDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.ydStock.StockSpecReg.CoilSpecRegSeEJBBean;
import com.inisteel.cim.ym.common.YmCommonDB;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.session.CTSStatusRegSBean;

import javax.naming.*;
import jspeed.base.record.*;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.*;
import jspeed.base.ejb.BaseSessionBean;

import jspeed.base.util.StringHelper;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.common.exception.DAOException;

/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="TsInfoRegEJB" jndi-name="JNDITsInfoReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class TsInfoRegSBean extends BaseSessionBean { 

	private Logger logger 	= null;
	private ilkwanDAO dao 	= null;
	private ymCommonDAO ymCommonDAO = null;
	private CraneSchDAO ydDao 		= null;
	private String szSessionName = "JNDIWorkOrderInfoReg";
	String[] rVal = new String[2];
	String sStocMv   = "";
	private boolean bDebugFlag=true; 
	private YdUtils ydUtils =new YdUtils();
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger 				= new Logger(config);
		dao 				= new ilkwanDAO();
	}
	 
	
	/**
	 *      [A] 오퍼레이션명 : 소재차량 출발 실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public boolean procMatlCarLev(JDTORecord msgRecord)throws DAOException  {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
//		TC_CODE : TSYDJ004
		 String szMsg           = "";
		 String szMethodName    = "procMatlCarLev";
		 String szmethod_nm 	= "";
		 String sWorkGp 		= "";
		 ymCommonDAO dao = ymCommonDAO.getInstance();
		 String szRcvTcCode=YmCommonUtil.getTcCode(msgRecord);
			if(szRcvTcCode==null){
				szMsg=szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				
				return false;
			}
			if(bDebugFlag){
				szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
			}
			
		try{
			String szTRN_EQP_CD             = YmCommonUtil.paraRecChkNull(msgRecord, "TRN_EQP_CD"); //운송장비코드
			String szSPOS_WLOC_CD           = YmCommonUtil.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");//발지개소코드
			String szARR_WLOC_CD            = YmCommonUtil.paraRecChkNull(msgRecord, "ARR_WLOC_CD");//착지개소코드

			
			szMsg="소재차량 출발 처리("+szMethodName+":"+szTRN_EQP_CD+") 시작";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);
			
			
			/**
    		 *  AB열연 야드개소코드 체크 
    		 */    		
    		logger.println(LogLevel.DEBUG,this,"=발지 체크개소코드 => "+ szSPOS_WLOC_CD);
    		logger.println(LogLevel.DEBUG,this,"=착지 체크개소코드 => "+ szARR_WLOC_CD);   
    		
    		sWorkGp = this.getABLocationInfo_01(szSPOS_WLOC_CD,szARR_WLOC_CD);
	    	
	    	if("CC".equals(sWorkGp)){
	    		szMsg = "착지 개소코드 오류";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
	    	
	    	//장애 발생시 이전 소스로 원복 하기 위한 조치
			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklistTS";
		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});

		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
	    	if(CHK.equals("Y")){
			
				if(szARR_WLOC_CD.equals("D2Y44")||
				   szARR_WLOC_CD.equals("D2Y45")||
				   szARR_WLOC_CD.equals("D3Y41")||
				   szARR_WLOC_CD.equals("D3Y42")){
					szmethod_nm ="procMatlCarLevCoil" ;
				}else {
					szmethod_nm ="procMatlCarLevSlab" ;
				}
			
	    	}else{
				szmethod_nm ="procMatlCarLevSlab" ;				
			}
			
			//차량 출발처리
			//msgOutRecord = JDTORecordFactory.getInstance().create(); 
			EJBConnector ejbConn = new EJBConnector("default","JNDITsInfoReg",this);
			msgRecord.setField("sWorkGp", sWorkGp);        
 			ejbConn.trx( szmethod_nm,
													new  Class[]{JDTORecord.class},
													new Object[]{msgRecord });
			
 			
 			szMsg="소재차량 출발 처리("+szMethodName+":"+szTRN_EQP_CD+") 완료";
 			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);

 			return true;
 			
	}catch(Exception e){
		
		szMsg="소재차량 출발 처리 Error:" +e.getMessage();
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
		m_ctx.setRollbackOnly();
		throw new DAOException(szMsg);
	}
  
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 소재차량 출발 실적처리(코일)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public boolean procMatlCarLevCoil(JDTORecord msgRecord)throws DAOException {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		boolean isSuccess = false;

	  	ymCommonDAO dao = ymCommonDAO.getInstance();
		JDTORecord Paramrecord = null;
	    
	    String szMsg             = "";
	    String szMethodName      = "procMatlCarLevCoil";
	    
	    String szTRN_WRK_FULLVOID_GP= "";
	    String szTRN_EQP_CD      	= "";
	    String szTRN_EQP_GP      	= "";
	    String szSPOS_WLOC_CD    	= "";
	    String szSPOS_YD_PNT_CD  	= "";
	    String szARR_YD_PNT_CD   	= "";
	    String szARR_WLOC_CD     	= "";
	    String szYD_CAR_USE_GP   	= "";
	    String szYD_EQP_WRK_STATE 	= "";
	    String szTRN_EQP_STK_CAPA 	= "";
	    String s_STACK_YD_GP 		= "";
	    String s_STACK_BAY_GP 		= "";
		String trnEqpQueryId 		= "";
	    String trnQueryId 			= "";
		String wBookid      		= "";
		String sloadStoppoint 		= "";
		String sloadStopTsCd 		= "";
		String sunloadStoppoint 	= "";
	    String queryID1   			= "";
	    String layerState 			= "";
	    String s_CAR_SCH_ID 		= "";
	    String s_STL_APPEAR_GP 		= "";
	    String unloadStopwloc		= "";
	    String unloadStoppoint		= "";
	    String sYD_MSG_CD			= "";
	    List FrtoProductList = null;
	    List loadPointList = null;
	    List FrtostlList = null;
	    List unloadPointList = null;
	    List AimSpaceList = null;

	    int count  = 0;
		int iSeq = 0;
	
	    try{
	    	//운송장비코드, 발지개소코드, 발지야드포인트코드, 착지개소코드, 착지야드포인트코드
	    	szTRN_EQP_CD             = YmCommonUtil.paraRecChkNull(msgRecord, "TRN_EQP_CD"); //운송장비코드
	    	szSPOS_WLOC_CD           = YmCommonUtil.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");//발지개소코드
	    	szSPOS_YD_PNT_CD         = YmCommonUtil.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");//발지야드포인트코드
	    	szARR_WLOC_CD            = YmCommonUtil.paraRecChkNull(msgRecord, "ARR_WLOC_CD");//착지개소코드
	    	szARR_YD_PNT_CD          = YmCommonUtil.paraRecChkNull(msgRecord, "ARR_YD_PNT_CD");//착지야드포인트코드
	    	szTRN_WRK_FULLVOID_GP    = YmCommonUtil.paraRecChkNull(msgRecord, "TRN_WRK_FULLVOID_GP");//공차/영차 구분
	    	szTRN_EQP_STK_CAPA    	 = YmCommonUtil.paraRecChkNull(msgRecord, "TRN_EQP_STK_CAPA");//공차/영차 구분
	    	String sWorkGp			 =YmCommonUtil.paraRecChkNull(msgRecord, "sWorkGp");
	
	    	szTRN_EQP_GP = szTRN_EQP_CD.substring(1, 3);//PT/TR 구분
	    	
	    	{	    		
	    		 /**
	    		 *  기존 출발정보 여부 확인 
	    		 */		
				   logger.println(LogLevel.DEBUG,this,"=기존 출발정보 여부 확인 ");
	    			/*
	    			  select YD_CAR_SCH_ID,YD_CARLD_WRK_BOOK_ID
	                   from TB_YD_CARSCH
	                   where TRN_EQP_CD = ?
	                   and DEL_YN = 'N'
	    			 */
	    				    			
	    		    String QueryId 	= "ym.tsinfo.getListSposYNchk2";
	    		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{szTRN_EQP_CD});
	    		    if(sposYNChklist.size() > 0)
	    		    { 	
	    		    	
	    		    	if(szTRN_WRK_FULLVOID_GP.equals("E")){
	    		    	logger.println(LogLevel.DEBUG,this,"=이미 차량정보가 있음 차량 초기화 작업 ");
	    		    	
	    		         JDTORecord paramRecord = JDTORecordFactory.getInstance().create();
	    		         paramRecord.setField("TRN_EQP_CD", szTRN_EQP_CD);        //사업장코드
	    		         
	    		         EJBConnector ejbConn = new EJBConnector("default", "JNDITsInfoReg", this);          
	    		         ejbConn.trx("CarinfoReset", 
	    		             new Class[]{ JDTORecord.class }, new Object[]{ paramRecord } );
	    		    	}
	    		    }   		
	    	}
	    	
	    	
	    	{
	    		/**
	    		 *  중장비수리고로 빠지면 예약정보 삭제 
	    		 */	    
		    	
		    	if("DMY1P".equals(szARR_WLOC_CD)){
		    		szMsg = "중장비수리고로 출발";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					queryID1 = "ym.tsinfo.updateColstatinit";
				    count = dao.updateData(queryID1, new Object[]{szTRN_EQP_CD});
				    
				    //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
					
				}
	    	}
  	
	    	
////================================================================================================================================================
//AB열연/C열연에서 AB열연으로 이송의 경우
	    	if("AA".equals(sWorkGp)||"CA".equals(sWorkGp))
	    	{		 
		    	//##########################################################################################################################################
				//영차인 경우
		    	if(szTRN_WRK_FULLVOID_GP.equals("F"))
				{	    		
		    		logger.println(LogLevel.DEBUG,this,"=코일 출발실적처리[AB,C<>AB]=>영차처리 시작◑◑◑◑◑◑◑◑◑◑◑◑◑");


		    		//소재이면 재료공통에 계획공정 및 차공정(우선)으로 야드 동 정보가져오고 
		    		//제품이면 MTP테이블에서 하차개소코드,하차포인트코드 가지고 와서 동정보 알아옴)
		    		trnQueryId 	= "ym.tsinfo.getListFrtostlList_CoilAppearGp";
		    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{szTRN_EQP_CD});
		    		
		    		if(FrtostlList.size()>0){
		    		JDTORecord TolocRec 	= (JDTORecord)FrtostlList.get(0);	
		    		s_STL_APPEAR_GP 		= StringHelper.evl(TolocRec.getFieldString("STL_APPEAR_GP"),"");
		    		unloadStopwloc 			= StringHelper.evl(TolocRec.getFieldString("ARR_WLOC_CD"),"");
	    			unloadStoppoint 		= StringHelper.evl(TolocRec.getFieldString("ARR_YD_PNT_CD"),"");
		    		}
		    		
		    		
					if ("AA".equals(sWorkGp)) {
						// 발지 차량 적치단 Clear 하기
						layerState = "C";
						queryID1 = "ym.tsinfo.updateLayerstat_02_new";
						count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
						
						// 발지 차량정보 삭제 하기
						queryID1 = "ym.tsinfo.updateLayerstat_03";
						count = dao.updateData(queryID1 , new Object[]{szSPOS_WLOC_CD, szTRN_EQP_CD});

						// 차량포인트통합관리(구분, 장비번호,저장위치,상태)
						CarPointinforeg("1" , "" , szTRN_EQP_CD , "" , "" , "" , "C");

						//if (count > 0) {
						//	layerState = "C";
						//	queryID1 = "ym.tsinfo.updateLayerstat_02";
						//	count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szSPOS_YD_PNT_CD, szTRN_EQP_GP});
						//}
					}
		    		
		    		
		    		if(s_STL_APPEAR_GP.equals("Y"))
		    		{// 제품

		    			trnQueryId 		= "ym.tsinfo.getListloadStoppointGD";
			    		AimSpaceList 	= dao.getCommonList(trnQueryId, new Object[]{unloadStopwloc,unloadStoppoint});
						
						if(AimSpaceList.size()>0){
							JDTORecord AimSpaceRec = (JDTORecord)AimSpaceList.get(0);
				    		sunloadStoppoint 	= StringHelper.evl(AimSpaceRec.getFieldString("STACK_COL_GP"),"");
				    		sloadStopTsCd 		= StringHelper.evl(AimSpaceRec.getFieldString("YD_PNT_CD"),"");
				    		
				    		//포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
				  	        trnEqpQueryId 	  = "ym.tsinfo.updateEquipcolStat";
				  	        iSeq = dao.updateData(trnEqpQueryId, new Object[]{ 	"L",	  	            		                                            
								                                            szTRN_EQP_CD,
								                                            sunloadStoppoint}); 
				  	        
				  	        //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
						    CarPointinforeg("3","",szTRN_EQP_CD,sunloadStoppoint,"","","R");
						    
			    		}else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
			    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>하차완료 포인트 없음 => 포인트없음 처리");
			    			sunloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
			    			sloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;	
			    			
			    			//대기장 포인트 사유 가져오기
			    			sYD_MSG_CD = this.getCarMsg(s_STL_APPEAR_GP ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , unloadStopwloc,unloadStoppoint);
			    		}
						
			    		logger.println(LogLevel.DEBUG,this,"=코일 출발실적처리[AB,C<>AB]=>제품 하차정지위치검색:"+sunloadStoppoint+" 포인트:"+sloadStopTsCd);
		                														    			
		    		}
		    		else
		    		{//소재
			    		trnQueryId 	= "ym.tsinfo.getListFrtostlList_1";
			    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{szTRN_EQP_CD});
	
			    		JDTORecord TolocRecCoil = (JDTORecord)FrtostlList.get(0);
			    		s_CAR_SCH_ID = StringHelper.evl(TolocRecCoil.getFieldString("YD_CAR_SCH_ID"),"");
			    		
			    		if(szARR_WLOC_CD.equals("D3Y41")||szARR_WLOC_CD.equals("D3Y42"))
				    	{
			    		     s_STACK_YD_GP 	= "3";	
					    	 s_STACK_BAY_GP = getBCoilAimCd(s_CAR_SCH_ID);   
				    	}
			    		else if(szARR_WLOC_CD.equals("D2Y44")||szARR_WLOC_CD.equals("D2Y45"))
			    		{
			    			s_STACK_YD_GP 	= "1";
			    			s_STACK_BAY_GP = getACoilAimCd(s_CAR_SCH_ID);
			    		}			    						    			
		    			
			    		logger.println(LogLevel.DEBUG,this,"=코일 출발실적처리[AB,C<>AB]=>소재 하차정지위치검색");
		  
		    				
		    		
			    		//하차정지위치 검색
				    	//추가부분 : 검색대상재 동에 있는 차량위치정보  
			    		trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
			    		unloadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szARR_WLOC_CD,
																	    				s_STACK_YD_GP,
																	    				s_STACK_BAY_GP,
																	    				szTRN_EQP_GP});
			    			
			    		if(unloadPointList.size() > 0){
					    	JDTORecord unloadPointrec = (JDTORecord)unloadPointList.get(0);
					    	sunloadStoppoint  = StringHelper.evl(unloadPointrec.getFieldString("STACK_COL_GP"), "");
					    	sloadStopTsCd   = StringHelper.evl(unloadPointrec.getFieldString("YD_PNT_CD"), "");
					    	
				  	        //포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
				  	        trnEqpQueryId 	  = "ym.tsinfo.updateEquipcolStat";
				  	        iSeq = dao.updateData(trnEqpQueryId, new Object[]{ 	"L",	  	            		                                            
									                                            szTRN_EQP_CD,
									                                            sunloadStoppoint}); 
				  	        
				  	        //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
						    CarPointinforeg("3","",szTRN_EQP_CD,sunloadStoppoint,"","","R");
	
			    		}else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
			    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>하차완료 포인트 없음 => 포인트없음 처리");
			    			sunloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
			    			sloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;	
			    			
			    			//대기장 포인트 사유 가져오기
			    			sYD_MSG_CD = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD,unloadStoppoint);
			    		}
 
		    		}
			    	
		    		
		    		
	               //차량스케쥴 업데이트(등록할것) 
	                szYD_CAR_USE_GP 	= "L";
		    		szYD_EQP_WRK_STATE 	= "L";//영차		    		
	  	            trnEqpQueryId 		= "ym.tsinfo.updatetrnEqpsch";
	  	            iSeq = dao.updateData(trnEqpQueryId, new Object[]{ 	szYD_CAR_USE_GP,
	  	            		                                            szYD_EQP_WRK_STATE,                                                                 
	  	            		                                            szARR_WLOC_CD,                                      
	  	            		                                            wBookid,
							                                            sunloadStoppoint,
							                                            szTRN_EQP_CD}); 
	             
					isSuccess = true;
			
					logger.println(LogLevel.DEBUG,this,"=코일 출발실적처리[AB,C<>AB]=>영차처리 종료◑◑◑◑◑◑◑◑◑◑◑◑◑"); 
				}
		    	
		    	//##########################################################################################################################################
				//공차인 경우
				else if(szTRN_WRK_FULLVOID_GP.equals("E"))
				{					
					logger.println(LogLevel.DEBUG,this,"=코일 출발실적처리[AB,C<>AB]=>공차처리 시작◑◑◑◑◑◑◑◑◑◑◑◑◑");   
					
					String queryID ="";			
					int iSeqCount 	= 0 ;
					if(szARR_WLOC_CD.equals("D3Y41")||szARR_WLOC_CD.equals("D3Y42"))
					{															         						
						//대상재 조회
				    	queryID 	= "ym.ilkwan.dao.YdStockDAO.getListFrtoStl_CoilNewB";				    	
					}else if(szARR_WLOC_CD.equals("D2Y44")||szARR_WLOC_CD.equals("D2Y45"))
					{															         						
						//대상재 조회
				    	queryID 	= "ym.ilkwan.dao.YdStockDAO.getListFrtoStl_CoilNewA";
					}
					FrtoProductList = dao.getCommonList(queryID, new Object[]{szARR_WLOC_CD,szTRN_EQP_CD,szTRN_EQP_CD,szARR_WLOC_CD});
					iSeqCount = FrtoProductList.size();
					
					logger.println(LogLevel.DEBUG,this,"=코일 출발실적처리[AB,C<>AB]=>상차정지위치검색 건수="+iSeqCount);
					
					
					if(iSeqCount > 0){	
						JDTORecord AvaPointList = (JDTORecord)FrtoProductList.get(0);
				    	s_STACK_YD_GP 	= StringHelper.evl(AvaPointList.getFieldString("STACK_COL_GP"),"").substring(0,1);
						s_STACK_BAY_GP 	= StringHelper.evl(AvaPointList.getFieldString("STACK_COL_GP"),"").substring(1,2);	
			
					}
					
					//상차정지위치 검색
				    //추가부분 : 검색대상재 동에 있는 차량위치정보  
			    	trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
			    	loadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{	szARR_WLOC_CD,
																	    				s_STACK_YD_GP,
																	    				s_STACK_BAY_GP,
																	    				szTRN_EQP_GP});

		    		if(loadPointList.size() > 0){
				    	JDTORecord loadPointrec = (JDTORecord)loadPointList.get(0);
				    	sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
				    	sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
				    	
					    //포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
		  	            trnEqpQueryId 		= "ym.tsinfo.updateEquipcolStat";
		  	            iSeq = dao.updateData(trnEqpQueryId, new Object[]{ 	"L",	  	            		                                            
								                                            szTRN_EQP_CD,
								                                            sloadStoppoint}); 
		  	            
		  	            //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					    CarPointinforeg("3","",szTRN_EQP_CD,sloadStoppoint,"","","R");
		  	          
		    		}else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
		    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>상차완료 포인트 없음 => 포인트없음 처리");
		    			sloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
		    			sloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;
		    			
		    			//대기장 포인트 사유 가져오기
		    			sYD_MSG_CD = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD,unloadStoppoint);
		    			
		    		}
		    			

		    		if("AA".equals(sWorkGp)){
						// 발지 차량 적치단 Clear 하기
						layerState = "C";
						queryID1 = "ym.tsinfo.updateLayerstat_02_new";
						count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
		    			
			    		//발지 차량정보 삭제 하기 
				    	queryID1 = "ym.tsinfo.updateLayerstat_03";
				    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
				    	
				    	//차량포인트통합관리(구분, 장비번호,저장위치,상태)
					    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
				    				
				    	//if(count >0 ){
						//layerState 	= "C";
				    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
				    	//count = dao.updateData(queryID1, new Object[]{	layerState,
				    	//												szSPOS_WLOC_CD,
				    	//												szSPOS_YD_PNT_CD,
				    	//												szTRN_EQP_GP}); 
				    	//}
		    		}
		    		
			    	//차량스케쥴테이블에 insert		    		
		    		szYD_CAR_USE_GP 	= "L";
		    		szYD_EQP_WRK_STATE 	= "U";//공차경우
	  	            trnEqpQueryId 		= "ym.tsinfo.inserttrnEqpsch";
			    	iSeq = dao.insertData(trnEqpQueryId, new Object[]{ 	szTRN_EQP_CD,
			    			                                            szYD_CAR_USE_GP,
			    			                                            szTRN_EQP_CD,
			    			                                            szYD_EQP_WRK_STATE,
			    			                                            szARR_WLOC_CD,
			    														"", //착지개소코드
			    														wBookid,
			    														sloadStoppoint}); 
	            
					isSuccess = true;
					
					logger.println(LogLevel.DEBUG,this,"=코일 출발실적처리[AB,C<>AB]=>공차처리 종료◑◑◑◑◑◑◑◑◑◑◑◑◑");   

			  }
	    	
	    }
//AB열연에서 AB열연으로 이송의 경우 end
////================================================================================================================================================

	   
	    	
	    	
	    	
////================================================================================================================================================
//AB열연에서 일관제철로 이송의 경우
	    else if("AC".equals(sWorkGp))
	    {
	    	logger.println(LogLevel.DEBUG,this,"=코일 출발실적처리[AB<>C]=>AB열연에서 일관제철로 이송 시작◑◑◑◑◑◑◑◑◑◑◑◑◑");
	    	
			// 발지 차량 적치단 Clear 하기
			layerState = "C";
			queryID1 = "ym.tsinfo.updateLayerstat_02_new";
			count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
			
	    	//발지 차량정보 삭제 하기 
	    	queryID1 = "ym.tsinfo.updateLayerstat_03";
	    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
	    	
	    	//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
		    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
	    	
	    	//if(count >0 ){
			//layerState 	= "C";
	    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
	    	//count = dao.updateData(queryID1, new Object[]{	layerState,
	    	//												szSPOS_WLOC_CD,
	    	//												szSPOS_YD_PNT_CD,
	    	//												szTRN_EQP_GP}); 
	    	//}

			logger.println(LogLevel.DEBUG,this,"=코일 출발실적처리[AB<>C]=>AB열연에서 일관제철로 이송 종료◑◑◑◑◑◑◑◑◑◑◑◑◑"); 
	    			
			isSuccess = true;
		  
	   }	    	
//AB열연에서 일관제철으로 이송의 경우 end
////================================================================================================================================================


   	
    	//포인트지시 모듈 호출 ##################################################################
    	//착지개소 및 포인트코드 추후 셋팅요 
		Paramrecord = JDTORecordFactory.getInstance().create(); 
		Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
		Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
		Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
		Paramrecord.setField("YD_MSG_CD", 		sYD_MSG_CD);
		
		this.procMatlCarArrPntReq(Paramrecord);
		//###################################################################################
			
		//포인트가 있는 경우에만 처리 한다.
		if(!sloadStopTsCd.equals(YmCommonConst.YM_DEFAULT_PNT_CD)){
			//포인트점유사항 출하송신 ################################################################
			Paramrecord = JDTORecordFactory.getInstance().create(); 
			Paramrecord.setField("WLOC_CD", 			szSPOS_WLOC_CD);//
			Paramrecord.setField("YD_PNT_CD", 			szSPOS_YD_PNT_CD);//
			Paramrecord.setField("YD_PNT_OCPY_GP", 		"A");
			Paramrecord.setField("YD_PNT_UNIT_CL_GP", 	"O");
			Paramrecord.setField("LOAN_PULLOUT_ABLE_YN","Y");
			Paramrecord.setField("OCPY_TRN_EQP_CD", 	szTRN_EQP_CD);
			this.procMatlCarArrPntSenddm(Paramrecord);
			//###################################################################################
	
	    	//L-2 차량 출발 정보 전송 #################################################################
	    	Paramrecord = JDTORecordFactory.getInstance().create(); 
			Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
			Paramrecord.setField("ARR_WLOC_CD", 	szSPOS_WLOC_CD);//착지개소코드
			Paramrecord.setField("ARR_YD_PNT_CD", 		szSPOS_YD_PNT_CD);
			Paramrecord.setField("TRN_EQP_GP", 		szTRN_EQP_GP);
			procMatlCarArrPntRequestL2(Paramrecord , "S") ; 	
			//####################################################################################
		}  	
	  }catch(Exception e){
			logger.println(LogLevel.ERROR,"작업중에러가 발생되었습니다.    ###################",e.toString(),e);
			szMsg="소재차량 출발 실적 처리 Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
	   }
		
		szMsg="소재차량 출발 실적 처리("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);
		
		return isSuccess;
	}// end of procMatlCarLevCoil()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 소재차량 출발 실적처리
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public boolean procMatlCarLevSlab(JDTORecord msgRecord)throws DAOException {
		
		boolean isSuccess = false;
		
		YdWBookDAO ydWBookDAO = new YdWBookDAO();
		YdStockDAO ydStockDAO = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
	  	ymCommonDAO dao = ymCommonDAO.getInstance();
		JDTORecord Paramrecord = null;
		JDTORecord Paramrecord2 = null;
	    
	    String szMsg             = "";
	    String szMethodName      = "procMatlCarLevSlab";
	    
	    String szTRN_WRK_FULLVOID_GP = "";
	    
	    String szQuery           = "";

	    String szYD_STK_COL_GP   = "";
	    String szTRN_EQP_CD      = "";
	    String szTRN_EQP_GP      = "";
	    String szSPOS_WLOC_CD    = "";
	    String szSPOS_YD_PNT_CD  = "";
	    String szARR_YD_PNT_CD   = "";
	    String szARR_WLOC_CD     = "";
	    String szYD_CAR_USE_GP   = "";
	    String szYD_EQP_WRK_STATE = "";
	    String szTRN_EQP_STK_CAPA = "";
	    String s_STOCK_ID  = "";
	    String s_STACK_YD_GP = "";
	    String s_STACK_BAY_GP = "";
	    String s_BED_GP = "";
	    String s_LAYER_GP = "";
	    String sYD_MSG_CD ="";
	    String[] rVal = new String[2];
	    String rAimVal = "";
	    
	    List FrtoProductList = null;
	    List carSchList = null;
	    List loadPointList = null;
	    List loadEndPointList = null;
	    List unloadEndPointList = null;
	    List unloadEndPointListEbayList = null;
	    List FrtostlList = null;
	    List unloadPointList = null;
	    List FrtostlList1 = null;
	    List TolocList = null;
	    List AimSpaceList = null;
	    List FrtoProductReqList = null;
	    List uploadMethodList = null;
	     
		String sWBookQueryId 	= "";	
		String wBookQueryId = "";
		String stkQueryId 		= "";
		String trnEqpQueryId 		= "";
	    String trnQueryId = "";
		String wBookid      	= "";
		String sSchCode = "";
		String sCarSchCode = "";
		String sMoveterm = "";
		String sloadStoppoint = "";
		String sloadStopTsCd = "";
		String sunloadStoppoint = "";
		String sunloadStopTsCd = "";
	    String queryID1   = "";
	    String Tolocquery   = "";
	    String layerState = "";
	    String s_HCR_GP = ""; 
	    String s_SLAB_WO_RT_CD = "";
	    String s_SCARFING_YN = "";
	    String s_CAR_SCH_ID = "";
	    String item = "";
	    String pos = "";
	    String wBookid2 ="";
	    int count  = 0;
		int iSeq = 0;
		
		JDTORecord wBookSel		= null;

		String szRcvTcCode=YmCommonUtil.getTcCode(msgRecord);
//		if(szRcvTcCode==null){
//			szMsg=szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
//			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
//		}
//		if(bDebugFlag){
//			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
//			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
//		}
				
	    try{
	    	//운송장비코드, 발지개소코드, 발지야드포인트코드, 착지개소코드, 착지야드포인트코드
	    	szTRN_EQP_CD             = YmCommonUtil.paraRecChkNull(msgRecord, "TRN_EQP_CD"); //운송장비코드
	    	szSPOS_WLOC_CD           = YmCommonUtil.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");//발지개소코드
	    	szSPOS_YD_PNT_CD         = YmCommonUtil.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");//발지야드포인트코드
	    	szARR_WLOC_CD            = YmCommonUtil.paraRecChkNull(msgRecord, "ARR_WLOC_CD");//착지개소코드
	    	szARR_YD_PNT_CD          = YmCommonUtil.paraRecChkNull(msgRecord, "ARR_YD_PNT_CD");//착지야드포인트코드
	    	szTRN_WRK_FULLVOID_GP    = YmCommonUtil.paraRecChkNull(msgRecord, "TRN_WRK_FULLVOID_GP");//공차/영차 구분
	    	szTRN_EQP_STK_CAPA    = YmCommonUtil.paraRecChkNull(msgRecord, "TRN_EQP_STK_CAPA");//공차/영차 구분
	    	int STK_CAPA = 0;     
	    	int STK_WT = 0;
	    	szTRN_EQP_GP = szTRN_EQP_CD.substring(1, 3);//PT/TR 구분
	    	int EndPointCount = 0;
	    	
	    	String sWorkGp	= "";
	    	
	    	//B-CAST 트레일러 이면서 상차지도착 인경우 PT처럼 처림 함.
	    	if(szTRN_EQP_GP.equals("TR") && szARR_WLOC_CD.equals("D2Y43") && szTRN_WRK_FULLVOID_GP.equals("E")){
	    		szTRN_EQP_GP ="PT";
	    	}
	    	
	    	{	    		
	    		 /**
	    		 *  기존 출발정보 여부 확인 
	    		 */		
				   logger.println(LogLevel.DEBUG,this,"=기존 출발정보 여부 확인 ");
	    			/*
	    			  select YD_CAR_SCH_ID,YD_CARLD_WRK_BOOK_ID
	                   from TB_YD_CARSCH
	                   where TRN_EQP_CD = ?
	                   and DEL_YN = 'N'
	    			 */
	    				    			
	    		    String QueryId 	= "ym.tsinfo.getListSposYNchk2";
	    		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{szTRN_EQP_CD});
	    		    if(sposYNChklist.size() > 0)
	    		    { 	
	    		    	
	    		    	if(szTRN_WRK_FULLVOID_GP.equals("E")){
	    		    	logger.println(LogLevel.DEBUG,this,"=이미 차량정보가 있음 차량 초기화 작업 ");
//	    		    	return false;
	    		    	
	    				 List paramList = new ArrayList();
	    				 JDTORecord dtoRecord = null; 
	    		         JDTORecord paramRecord = JDTORecordFactory.getInstance().create();
	    		         paramRecord.setField("TRN_EQP_CD", szTRN_EQP_CD);        //사업장코드
	    		         
	    		         EJBConnector ejbConn = new EJBConnector("default", "JNDITsInfoReg", this);          
	    		         dtoRecord = (JDTORecord)ejbConn.trx("CarinfoReset", 
	    		             new Class[]{ JDTORecord.class }, new Object[]{ paramRecord } );
	    		    	}
	    		    }   		
	    	}
	    	
	    	
	    	{
	    		/**
	    		 *  중장비수리고로 빠지면 예약정보 삭제 
	    		 */
	    
		    	
		    	if("DMY1P".equals(szARR_WLOC_CD)){
		    		szMsg = "중장비수리고로 출발";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					queryID1 = "ym.tsinfo.updateColstatinit";
				    count = dao.updateData(queryID1, new Object[]{szTRN_EQP_CD});
				    
				  //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
					
				}
	    	}
  	
	    	
	    	{
	    		/**
	    		 *  AB열연 야드개소코드 체크 
	    		 */
	    		
	    		
	    		logger.println(LogLevel.DEBUG,this,"=발지 체크개소코드 => "+ szSPOS_WLOC_CD);
	    		logger.println(LogLevel.DEBUG,this,"=착지 체크개소코드 => "+ szARR_WLOC_CD);   
	    		
	    		sWorkGp = this.getABLocationInfo_01(szSPOS_WLOC_CD,szARR_WLOC_CD);
		    	
		    	if("CC".equals(sWorkGp)){
		    		szMsg = "착지 개소코드 오류";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					return false;
				}
	    	}
	    	
         	////================================================================================================================================================
	    	//AB열연에서 AB열연으로 이송의 경우
	    	if("AA".equals(sWorkGp))
	    	{
		    	
		    	if(szTRN_WRK_FULLVOID_GP.equals("F"))
				{	    		
		    		logger.println(LogLevel.DEBUG,this,"=출발실적처리=>영차처리 시작");

			    	
			    	//슬라브이송
                    if(szSPOS_WLOC_CD.equals("D2Y43")||szSPOS_WLOC_CD.equals("D3Y43"))
			    	{
                    	

                    	if(szTRN_EQP_GP.equals("PT"))
                    	{
                    		
                    		sSchCode 	= YmCommonConst.NEW_SCH_WORK_KIND_SVMU;
    			    		//차량스케쥴ID로 이송재료 조회
    			    		trnQueryId 	= "ym.tsinfo.getListFrtostlList_1";
    			    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{szTRN_EQP_CD});
    			    		/*
    			    		 * SELECT YD_CAR_SCH_ID
                               FROM TB_YD_CARSCH
                               WHERE TRN_EQP_CD = ?
                               AND DEL_YN = 'N'
    			    		 */ 
    			    		JDTORecord TolocRec = (JDTORecord)FrtostlList.get(0);
    			    		s_CAR_SCH_ID 			= StringHelper.evl(TolocRec.getFieldString("YD_CAR_SCH_ID"),"");
    			    		
    			    		if(szARR_WLOC_CD.equals("D3Y43")){
    			    			s_STACK_YD_GP 	= "2";	//b열연		   
    			    		}else {
    			    			s_STACK_YD_GP 	= "0";  //b-cast
    			    		}
    			    		
    			    		s_STACK_BAY_GP = getSlabAimCd(s_CAR_SCH_ID,szSPOS_WLOC_CD);
    			    		logger.println(LogLevel.DEBUG,this,"=B열연 이송재 목적동 조회" + s_STACK_BAY_GP);
    												
    						logger.println(LogLevel.DEBUG,this,"=출발실적처리=>하차정지위치검색");
    						  //하차정지위치 검색
    				    	//추가부분 : 검색대상재 동에 있는 차량위치정보  
    						
    						if(szARR_WLOC_CD.equals("D3Y43")){
	    			    		trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
	    			    		unloadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szARR_WLOC_CD,
	    																	    				s_STACK_YD_GP,
	    																	    				s_STACK_BAY_GP,
	    																	    				szTRN_EQP_GP});
	    			    		
	//    			    		포인트 모두 점유상태일때 하차완료된 포인트찾음					    	  
	    			    		trnEqpQueryId = "ym.tsinfo.getListUnloadEndpoint_slab";
	    			    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STACK_YD_GP,
	    			    				                                                        s_STACK_BAY_GP,
	    																	    				szTRN_EQP_GP,
	    																	    				szARR_WLOC_CD});
    			    		
    						}else{
	    			    		trnEqpQueryId = "ym.tsinfo.getListloadStoppointBCAST";
	    			    		unloadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szARR_WLOC_CD,
	    																	    				s_STACK_YD_GP,
	    																	    				szTRN_EQP_GP});
	    			    		
	//    			    		포인트 모두 점유상태일때 하차완료된 포인트찾음					    	  
	    			    		trnEqpQueryId = "ym.tsinfo.getListUnloadEndpoint_slab";
	    			    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STACK_YD_GP,
	    			    				                                                        "%",
	    																	    				szTRN_EQP_GP,
	    																	    				szARR_WLOC_CD});
    						}
    			    		

    			    		
    			    		
    			    		
//    			    		trnEqpQueryId = "ym.tsinfo.getListunloadPointBSlabDiffpoint_slab";
//    			    		  /*	
//    	    				Select 
//    	    			    STACK_COL_GP,
//    	    			    WLOC_CD,
//    	    			    YD_PNT_CD
//    	    			   From Tb_Ym_StackCol
//    	    			   Where Stack_Col_Gp like '2EPT%'
//    	    			   And   CAR_CARD_NO Is Null
//    	    			   And   TRN_EQP_CD IS Null
//    	    			   And   RowNum = 1*/ 	
//    			    		unloadEndPointListEbayList = dao.getCommonList(trnEqpQueryId);
    			    	
    		    			
    			    		if(unloadPointList.size() > 0){
    			    			logger.println(LogLevel.DEBUG,this,"=빈포인트 검색");
    					    	JDTORecord unloadPointrec = (JDTORecord)unloadPointList.get(0);
    					    	sunloadStoppoint  = StringHelper.evl(unloadPointrec.getFieldString("STACK_COL_GP"), "");
    					    	sunloadStopTsCd   = StringHelper.evl(unloadPointrec.getFieldString("YD_PNT_CD"), "");
    			    		}
    			    	/*	else if(unloadEndPointListEbayList.size()>0)
    			    		{
    			    			logger.println(LogLevel.DEBUG,this,"= E동포인트 검색");
    			    		     JDTORecord unloadPointBSlabDiffrec = (JDTORecord)unloadEndPointListEbayList.get(0);
    			    			 sunloadStoppoint  = StringHelper.evl(unloadPointBSlabDiffrec.getFieldString("STACK_COL_GP"), "");
    						     sunloadStopTsCd   = StringHelper.evl(unloadPointBSlabDiffrec.getFieldString("YD_PNT_CD"), "");
    						     s_STACK_BAY_GP = "E";			    							    				            						    	    			
    			    		 }	*/		    		
    			    		 else if(unloadEndPointList.size() > 0){
      			    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>하차완료 포인트 검색");
      			    			
      			    			int unloadEndPointListSize = unloadEndPointList.size();
      			    		
      			    			
      			    			for(int i=0; i < unloadEndPointListSize ; i++){
      			    			
      					    	    JDTORecord unloadPointrec = (JDTORecord)unloadEndPointList.get(i);
      					    	
      					    	    String QueryId 	= "ym.tsinfo.getListCarSpec";
      				    		    List CarSpecList = dao.getCommonList(QueryId, new Object[]{szTRN_EQP_CD});
      				    		    
      				    		    logger.println(LogLevel.DEBUG,this,"=인출차입 가능여부 조회");
        				    		JDTORecord CarSpecRec = (JDTORecord)CarSpecList.get(0);
        				    		String inCarspec= StringHelper.evl(CarSpecRec.getFieldString("CAR_NO"),""); 				    		
        					    	String outCarspec   = StringHelper.evl(unloadPointrec.getFieldString("CAR_NO"), "");
        					    	logger.println(LogLevel.DEBUG,this,"=인출할 pallet의 그룹"+outCarspec);
        					    	logger.println(LogLevel.DEBUG,this,"=차입할 pallet의 그룹"+inCarspec);
        					    	
        					    	if (inCarspec.equals(outCarspec)){
          					    		logger.println(LogLevel.DEBUG,this,"=인출차입 가능한 pallet 그룹임");
          						    	sunloadStoppoint  = StringHelper.evl(unloadPointrec.getFieldString("STACK_COL_GP"), "");
          						    	sunloadStopTsCd   = StringHelper.evl(unloadPointrec.getFieldString("YD_PNT_CD"), "");	
          						    	EndPointCount++;
          						    	break;
          					    	}
      			    			}
      			    			
      			    		 if(EndPointCount == 0)
      			    		 {
//       					   포인트없으면 포인트 없음으로 지시주고 하위처리 안함
  					    		logger.println(LogLevel.DEBUG,this,"=인출차입 불가능한 pallet 그룹임");
  				    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>하차완료 포인트 없음 => 포인트없음 처리");
  				    			sunloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
  				    			sunloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;
  				    			
  				    			//대기장 포인트 사유 가져오기
  				    			sYD_MSG_CD = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD,"");
  				    			
  				    			
  				    			Paramrecord = JDTORecordFactory.getInstance().create(); 
  								Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
  								Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
  								Paramrecord.setField("YD_PNT_CD", 		sunloadStopTsCd);
  								
  								// 발지 차량 적치단 Clear 하기
  								layerState = "C";
  								queryID1 = "ym.tsinfo.updateLayerstat_02_new";
  								count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
  								
//  								발지 차량정보 삭제 하기 
  						    	queryID1 = "ym.tsinfo.updateLayerstat_03";
  						    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
  						    	
  						    	//차량포인트통합관리(구분, 장비번호,저장위치,상태)
  							    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
  						    	
  						    	//if(count >0 ){
  						    	//layerState 	= "C";
  						    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
  						    	//count = dao.updateData(queryID1, new Object[]{	layerState,
  						    	//												szSPOS_WLOC_CD,
  						    	//												szSPOS_YD_PNT_CD,
  						    	//												szTRN_EQP_GP}); 
  						    	//}
      			    		 }
  			    	
      					   }else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
    			    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>하차완료 포인트 없음 => 포인트없음 처리");
    			    			sunloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
    			    			sunloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;
    			    			
    			    			//대기장 포인트 사유 가져오기
  				    			sYD_MSG_CD = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD,"");
  				    			
    			    			Paramrecord = JDTORecordFactory.getInstance().create(); 
    							Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
    							Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
    							Paramrecord.setField("YD_PNT_CD", 		sunloadStopTsCd);
    							
    							// 발지 차량 적치단 Clear 하기
    							layerState = "C";
    							queryID1 = "ym.tsinfo.updateLayerstat_02_new";
    							count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
    							
//    							발지 차량정보 삭제 하기 
    					    	queryID1 = "ym.tsinfo.updateLayerstat_03";
    					    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
    					    	
    					    	//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
    						    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
    					    	
    					    	//if(count >0 ){
    					    	//layerState 	= "C";
    					    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
    					    	//count = dao.updateData(queryID1, new Object[]{	layerState,
    					    	//												szSPOS_WLOC_CD,
    					    	//												szSPOS_YD_PNT_CD,
    					    	//												szTRN_EQP_GP}); 
    					    	//}
    					    	    					    		   									    			
    			    		}	
                    	
                    	}                   	
                    	else if (szTRN_EQP_GP.equals("TR"))
                    	{
     		
                    		sSchCode 	= YmCommonConst.NEW_SCH_WORK_KIND_SVMU;
    			    		//차량스케쥴ID로 이송재료 조회
    			    		trnQueryId 	= "ym.tsinfo.getListFrtostlList_1";
    			    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{szTRN_EQP_CD});
    			    		/*
    			    		 * SELECT YD_CAR_SCH_ID
                               FROM TB_YD_CARSCH
                               WHERE TRN_EQP_CD = ?
                               AND DEL_YN = 'N'
    			    		 */ 
    			    		JDTORecord TolocRec = (JDTORecord)FrtostlList.get(0);
    			    		s_CAR_SCH_ID 			= StringHelper.evl(TolocRec.getFieldString("YD_CAR_SCH_ID"),"");
    			    		
	
    			    		if(szARR_WLOC_CD.equals("D3Y43")){
    			    			s_STACK_YD_GP 	= "2";	//b열연		   
    			    		}else {
    			    			s_STACK_YD_GP 	= "0";  //b-cast
    			    		}
    			    		
    			    		s_STACK_BAY_GP = getSlabAimCd(s_CAR_SCH_ID,szSPOS_WLOC_CD);
    			    		logger.println(LogLevel.DEBUG,this,"=B열연 이송재 목적동 조회" + s_STACK_BAY_GP);
    												
    						logger.println(LogLevel.DEBUG,this,"=출발실적처리=>하차정지위치검색");
    						  //하차정지위치 검색
    				    	//추가부분 : 검색대상재 동에 있는 차량위치정보  
    						
    						if(szARR_WLOC_CD.equals("D3Y43")){
	    			    		trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
	    			    		unloadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szARR_WLOC_CD,
	    																	    				s_STACK_YD_GP,
	    																	    				s_STACK_BAY_GP,
	    																	    				szTRN_EQP_GP});
	    			    		
	//    			    		포인트 모두 점유상태일때 하차완료된 포인트찾음					    	  
	    			    		trnEqpQueryId = "ym.tsinfo.getListUnloadEndpoint_slab";
	    			    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STACK_YD_GP,
	    			    				                                                        s_STACK_BAY_GP,
	    																	    				szTRN_EQP_GP,
	    																	    				szARR_WLOC_CD});
    			    		
    			    		
    						}else{
	    			    		trnEqpQueryId = "ym.tsinfo.getListloadStoppointBCAST";
	    			    		unloadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szARR_WLOC_CD,
	    																	    				s_STACK_YD_GP,
	    																	    				szTRN_EQP_GP});
	    			    		
	//    			    		포인트 모두 점유상태일때 하차완료된 포인트찾음					    	  
	    			    		trnEqpQueryId = "ym.tsinfo.getListUnloadEndpoint_slab";
	    			    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STACK_YD_GP,
	    			    				                                                        "%",
	    																	    				szTRN_EQP_GP,
	    																	    				szARR_WLOC_CD});
    							
    						}
//    			    		trnEqpQueryId = "ym.tsinfo.getListunloadPointBSlabDiffpoint_slab";
//    			    		  /*	
//    	    				Select 
//    	    			    STACK_COL_GP,
//    	    			    WLOC_CD,
//    	    			    YD_PNT_CD
//    	    			   From Tb_Ym_StackCol
//    	    			   Where Stack_Col_Gp like '2EPT%'
//    	    			   And   CAR_CARD_NO Is Null
//    	    			   And   TRN_EQP_CD IS Null
//    	    			   And   RowNum = 1*/ 	
//    			    		unloadEndPointListEbayList = dao.getCommonList(trnEqpQueryId);
    			    	
    		    			
    			    		if(unloadPointList.size() > 0){
    			    			logger.println(LogLevel.DEBUG,this,"=빈포인트 검색");
    					    	JDTORecord unloadPointrec = (JDTORecord)unloadPointList.get(0);
    					    	sunloadStoppoint  = StringHelper.evl(unloadPointrec.getFieldString("STACK_COL_GP"), "");
    					    	sunloadStopTsCd   = StringHelper.evl(unloadPointrec.getFieldString("YD_PNT_CD"), "");
    			    		}
    			    	/*	else if(unloadEndPointListEbayList.size()>0)
    			    		{
    			    			logger.println(LogLevel.DEBUG,this,"= E동포인트 검색");
    			    		     JDTORecord unloadPointBSlabDiffrec = (JDTORecord)unloadEndPointListEbayList.get(0);
    			    			 sunloadStoppoint  = StringHelper.evl(unloadPointBSlabDiffrec.getFieldString("STACK_COL_GP"), "");
    						     sunloadStopTsCd   = StringHelper.evl(unloadPointBSlabDiffrec.getFieldString("YD_PNT_CD"), "");
    						     s_STACK_BAY_GP = "E";			    							    				            						    	    			
    			    		 }	*/		    		
    			    	    else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
    			    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>하차완료 포인트 없음 => 포인트없음 처리");
    			    			sunloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
    			    			sunloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;
    			    			
    			    			//대기장 포인트 사유 가져오기
  				    			sYD_MSG_CD = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD,"");
  				    			
    			    			Paramrecord = JDTORecordFactory.getInstance().create(); 
    							Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
    							Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
    							Paramrecord.setField("YD_PNT_CD", 		sunloadStopTsCd);
    							
    							// 발지 차량 적치단 Clear 하기
    							layerState = "C";
    							queryID1 = "ym.tsinfo.updateLayerstat_02_new";
    							count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
    							
//    							발지 차량정보 삭제 하기 
    					    	queryID1 = "ym.tsinfo.updateLayerstat_03";
    					    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
    					    	
    					    	//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
    						    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
    					    	
    					    	//if(count >0 ){
    					    	//layerState 	= "C";
    					    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
    					    	//count = dao.updateData(queryID1, new Object[]{	layerState,
    					    	//												szSPOS_WLOC_CD,
    					    	//												szSPOS_YD_PNT_CD,
    					    	//												szTRN_EQP_GP}); 
    					    	//}
    					    				    			
    			    		}
             		
                    	}    		
			    	}
                    
                    //코일이송
			    	else if(szSPOS_WLOC_CD.equals("D2Y44")||
			    			szSPOS_WLOC_CD.equals("D2Y45")||
			    			szSPOS_WLOC_CD.equals("D3Y41")||
			    			szSPOS_WLOC_CD.equals("D3Y42"))
			    	{
			    		//소재이면 재료공통에 계획공정 및 차공정(우선)으로 야드 동 정보가져오고 
			    		//제품이면 MTP테이블에서 하차개소코드,하차포인트코드 가지고 와서 동정보 알아옴)
			    		trnQueryId 	= "ym.tsinfo.getListFrtostlList_Coil";
			    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{szTRN_EQP_CD});
			    		
			    		JDTORecord TolocRec 	= (JDTORecord)FrtostlList.get(0);	
			    		String s_STL_APPEAR_GP 	= StringHelper.evl(TolocRec.getFieldString("STL_APPEAR_GP"),"");
			    		
			    		if(s_STL_APPEAR_GP.equals("Y"))// 제품
			    		{
			    			
			    			sSchCode 				= YmCommonConst.NEW_SCH_WORK_KIND_GVMU;
			    			String unloadStopwloc 	= StringHelper.evl(TolocRec.getFieldString("ARR_WLOC_CD"),"");
			    			String unloadStoppoint 	= StringHelper.evl(TolocRec.getFieldString("ARR_YD_PNT_CD"),"");
			    			
			    			trnQueryId 		= "ym.tsinfo.getListAimspace";
				    		AimSpaceList 	= dao.getCommonList(trnQueryId, new Object[]{unloadStopwloc,unloadStoppoint});
				    		JDTORecord AimSpaceRec = (JDTORecord)AimSpaceList.get(0);
				    		
				    		s_STACK_YD_GP 	= StringHelper.evl(AimSpaceRec.getFieldString("YD_GP"),"");
							s_STACK_BAY_GP 	= StringHelper.evl(AimSpaceRec.getFieldString("BAY_GP"),"");
							
				    		logger.println(LogLevel.DEBUG,this,"=출발실적처리=>하차정지위치검색");
			                //하차정지위치 검색
					    	//추가부분 : 검색대상재 동에 있는 차량위치정보  
				    		trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
				    		/*
					    	 	SELECT  STACK_COL_GP,
										YD_PNT_CD
								FROM TB_YM_STACKCOL 
								WHERE WLOC_CD     = :WLOC_CD
								AND YD_GP         = :YD_GP
								AND BAY_GP        = :BAY_GP
								AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
								AND TRN_EQP_CD  IS NULL	-- 현재차량정지위치
								AND CAR_CARD_NO IS NULL	-- 예약차량정지위치
					    	 */
				    		unloadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szARR_WLOC_CD,
																		    				s_STACK_YD_GP,
																		    				s_STACK_BAY_GP,
																		    				szTRN_EQP_GP});
				    		
//				    		포인트 모두 점유상태일때 하차완료된 포인트찾음					    	  
				    		trnEqpQueryId = "ym.tsinfo.getListUnloadEndpoint_coil";
				    		/*
								SELECT  B.STACK_COL_GP,
			                            B.YD_PNT_CD
		                        FROM  TB_YM_STACKLAYER A,TB_YM_STACKCOL B
		                        WHERE SUBSTR(A.STACK_COL_GP,1,1) = ?       
		                        AND SUBSTR(A.STACK_COL_GP,2,1) = ?
		                        AND SUBSTR(A.STACK_COL_GP,3,2) = ?
		                        AND A.STACK_BED_GP = '01'
		                        AND A.STOCK_ID IS NULL
		                        AND A.STACK_LAYER_STAT = 'E'
		                        AND A.STACK_COL_GP = B.STACK_COL_GP
		                        AND B.WLOC_CD = ?
					    	 */
				    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STACK_YD_GP,
				    				                                                        s_STACK_BAY_GP,
																		    				szTRN_EQP_GP,
																		    				szARR_WLOC_CD});
				    			
				    		if(unloadPointList.size() > 0){
						    	JDTORecord unloadPointrec = (JDTORecord)unloadPointList.get(0);
						    	sunloadStoppoint  = StringHelper.evl(unloadPointrec.getFieldString("STACK_COL_GP"), "");
						    	sunloadStopTsCd   = StringHelper.evl(unloadPointrec.getFieldString("YD_PNT_CD"), "");
						    	item = YmCommonConst.ITEM_CG;
				    			pos  = sunloadStoppoint;				    			
				    			sSchCode = getUnloadSchKind(item,pos);
				    		}else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
				    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>하차완료 포인트 없음 => 포인트없음 처리");
				    			sunloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
				    			sunloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;	
				    			sSchCode = "0000";
				    			
				    			//대기장 포인트 사유 가져오기
  				    			sYD_MSG_CD = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD,"");
  				    			
  								// 발지 차량 적치단 Clear 하기
  								layerState = "C";
  								queryID1 = "ym.tsinfo.updateLayerstat_02_new";
  								count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
  								
				    			//발지 차량정보 삭제 하기 
						    	queryID1 = "ym.tsinfo.updateLayerstat_03";
						    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
						    	
						    	//차량포인트통합관리(구분, 장비번호,저장위치,상태)
							    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
						    	
						    	//if(count >0 ){
						    	//layerState 	= "C";
						    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
						    	//count = dao.updateData(queryID1, new Object[]{	layerState,
						    	//												szSPOS_WLOC_CD,
						    	//												szSPOS_YD_PNT_CD,
						    	//												szTRN_EQP_GP}); 
						    	//}
				    			
				    			
				    		}
															    			
			    		}
			    		else//소재
			    		{
			    			
				    		trnQueryId 	= "ym.tsinfo.getListFrtostlList_1";
				    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{szTRN_EQP_CD});
				    		/*
				    		 * SELECT YD_CAR_SCH_ID
	                           FROM TB_YD_CARSCH
	                           WHERE TRN_EQP_CD = ?
	                           AND DEL_YN = 'N'
				    		 */ 
				    		JDTORecord TolocRecCoil = (JDTORecord)FrtostlList.get(0);
				    		s_CAR_SCH_ID = StringHelper.evl(TolocRecCoil.getFieldString("YD_CAR_SCH_ID"),"");
				    		
				    		if(szSPOS_WLOC_CD.equals("D3Y41")||
					    			szSPOS_WLOC_CD.equals("D3Y42"))
					    	{
				    		     s_STACK_YD_GP 	= "1";	
						    	 s_STACK_BAY_GP = getACoilAimCd(s_CAR_SCH_ID);   
					    	}
				    		else if(szSPOS_WLOC_CD.equals("D2Y44")||szSPOS_WLOC_CD.equals("D2Y45"))
				    		{
				    			s_STACK_YD_GP 	= "3";
				    			s_STACK_BAY_GP = getBCoilAimCd(s_CAR_SCH_ID);
				    		}
				    						    			
			    			
				    		logger.println(LogLevel.DEBUG,this,"=출발실적처리=>하차정지위치검색");
			                //하차정지위치 검색
					    	//추가부분 : 검색대상재 동에 있는 차량위치정보  
				    		trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
				    		/*
					    	 	SELECT  STACK_COL_GP,
										YD_PNT_CD
								FROM TB_YM_STACKCOL 
								WHERE WLOC_CD     = :WLOC_CD
								AND YD_GP         = :YD_GP
								AND BAY_GP        = :BAY_GP
								AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
								AND TRN_EQP_CD  IS NULL	-- 현재차량정지위치
								AND CAR_CARD_NO IS NULL	-- 예약차량정지위치
					    	 */
				    		unloadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szARR_WLOC_CD,
																		    				s_STACK_YD_GP,
																		    				s_STACK_BAY_GP,
																		    				szTRN_EQP_GP});
				    			
				    		if(unloadPointList.size() > 0){
						    	JDTORecord unloadPointrec = (JDTORecord)unloadPointList.get(0);
						    	sunloadStoppoint  = StringHelper.evl(unloadPointrec.getFieldString("STACK_COL_GP"), "");
						    	sunloadStopTsCd   = StringHelper.evl(unloadPointrec.getFieldString("YD_PNT_CD"), "");
						    	item = YmCommonConst.ITEM_CM;
				    			pos  = sunloadStoppoint;				    			
				    			sSchCode = getUnloadSchKind(item,pos);
				    		}else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
				    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>하차완료 포인트 없음 => 포인트없음 처리");
				    			sunloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
				    			sunloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;					    					    			
				    			sSchCode = "0000";
				    			
				    			//대기장 포인트 사유 가져오기
  				    			sYD_MSG_CD = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD,"");
			 
  								// 발지 차량 적치단 Clear 하기
  								layerState = "C";
  								queryID1 = "ym.tsinfo.updateLayerstat_02_new";
  								count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
  								
				    			//발지 차량정보 삭제 하기 
						    	queryID1 = "ym.tsinfo.updateLayerstat_03";
						    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
						    	
						    	//차량포인트통합관리(구분, 장비번호,저장위치,상태)
							    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
						    	
						    	//if(count >0 ){
						    	//layerState 	= "C";
						    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
						    	//count = dao.updateData(queryID1, new Object[]{	layerState,
						    	//												szSPOS_WLOC_CD,
						    	//												szSPOS_YD_PNT_CD,
						    	//												szTRN_EQP_GP}); 
						    	//}
				    		}
				    		
			    		
			    		}			    		
			    	}
                    
//		    		 야드맵 Close
//			    	queryID1 = "ym.tsinfo.updateLayerstat_01";
//			    	count = dao.updateData(queryID1, new Object[]{	"","","","",
//			    													szSPOS_WLOC_CD,
//			    													szSPOS_YD_PNT_CD,
//			    													szTRN_EQP_GP}); 
//			    	/*
//				    	UPDATE  TB_YM_STACKCOL
//						SET     YD_CAR_USE_GP   = :YD_CAR_USE_GP,
//						        TRN_EQP_CD      = :TRN_EQP_CD,
//						        CAR_NO          = :CAR_NO,
//						        CARD_NO         = :CARD_NO
//						WHERE WLOC_CD   = :WLOC_CD
//						AND   YD_PNT_CD = :YD_PNT_CD
//						AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
//					*/
			    	
			    	
					// 발지 차량 적치단 Clear 하기
					layerState = "C";
					queryID1 = "ym.tsinfo.updateLayerstat_02_new";
					count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
					
			    	//발지 차량정보 삭제 하기 
			    	queryID1 = "ym.tsinfo.updateLayerstat_03";
			    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
			    	
			    	//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
			    	
					sMoveterm   = YmCommonConst.NEW_STOCK_MOVE_TERM_CS;
					
					//if(count >0 ){
					//layerState 	= "C";
			    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
			    	//count = dao.updateData(queryID1, new Object[]{	layerState,
			    	//												szSPOS_WLOC_CD,
			    	//												szSPOS_YD_PNT_CD,
			    	//												szTRN_EQP_GP}); 
					//}
			     	/*
			    	 * UPDATE TB_YM_STACKLAYER
		               SET  STOCK_ID = '',
		                    STACK_LAYER_ACTIVE_STAT = ?,
		                    STACK_LAYER_STAT = 'E' 
		               WHERE STACK_COL_GP = (SELECT A.STACK_COL_GP	    	 		
							                 FROM TB_YM_STACKCOL A
						                     WHERE  A.WLOC_CD 	= ?
						                       AND 	A.YD_PNT_CD = ?
	                                           AND SUBSTR(A.STACK_COL_GP,3,2) = :TRN_EQP_GP
							                 )
			    	 */
			    	                   
		    		//차량스케쥴ID로 이송재료 조회(등록할것)
		    		trnQueryId 		= "ym.tsinfo.getListFrtostlList";
		    		FrtostlList1 	= dao.getCommonList(trnQueryId, new Object[]{szTRN_EQP_CD});
		    		/*
		    		 * SELECT STL_NO,SUBSTR(YD_STK_LYR_NO,2,2)STK_LYR
	                   FROM TB_YD_CARFTMVMTL A, TB_YD_CARSCH B
	                   WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
	                   AND A.DEL_YN = 'N'
	                   AND B.TRN_EQP_CD = ?
	                   ORDER BY A.YD_STK_LYR_NO 
		    		 */
		    		
					//작업예약생성	
			    	wBookQueryId 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";								
					wBookSel 		= ydStackLayerDAO.requestFind(wBookQueryId);
					wBookid      	= wBookSel.getFieldString("WBOOK_ID");
			    		
					//작업예약테이블에 insert
					sWBookQueryId 	= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
					
					iSeq = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ wBookid, 
																					 s_STACK_YD_GP, 
																					 s_STACK_BAY_GP,
																					 sSchCode, 
																					 YmCommonUtil.getWorkDuty(),
																					 YmCommonUtil.getWorkParty()});
					
					
					int iSeqCount 	= FrtostlList1.size();

	                for(int i=0; i < iSeqCount ; i++){
						
			    		JDTORecord FrtoSltrec = (JDTORecord)FrtostlList1.get(i);
						s_STOCK_ID = StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"),"");
												
						
						//이전 작업예약 존재 유무 체크  
						trnQueryId 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID3";
			    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{s_STOCK_ID});
			 
			    		JDTORecord TolocRecCoil = (JDTORecord)FrtostlList.get(0);
			    		wBookid2 = StringHelper.evl(TolocRecCoil.getFieldString("WBOOK_ID"),"");
			    		
			    		if(!wBookid2.equals("")){
			    			stkQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteWbookInfo";
								iSeq = ydStockDAO.deleteData(stkQueryId, new Object[]{wBookid2});
			    		}
						
						//저장품테이블에 update				
						stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
						iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ wBookid, 
								                                                      sMoveterm,
																					  s_STOCK_ID});
						/*
		                 *	적치단  Table Update(작업요구상태='S'로 변경)
		                 */
		            	String sQuery4 = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
		            	iSeq = ydStockDAO.requestupdateData(sQuery4, 
														    new Object[]{YmCommonConst.STACK_LAYER_STAT_S, 
		            										s_STOCK_ID });
			    	}
					     

	                
	               //차량스케쥴 업데이트(등록할것) 
	                szYD_CAR_USE_GP 	= "L";
		    		szYD_EQP_WRK_STATE 	= "L";//공차경우
		    		
	  	            trnEqpQueryId 		= "ym.tsinfo.updatetrnEqpsch";
	  	            iSeq = dao.updateData(trnEqpQueryId, new Object[]{ 	szYD_CAR_USE_GP,
	  	            		                                            szYD_EQP_WRK_STATE,                                                                 
	  	            		                                            szARR_WLOC_CD,                                      
	  	            		                                            wBookid,
							                                            sunloadStoppoint,
							                                            szTRN_EQP_CD}); 
	  	            /*
	  	             * UPDATE TB_YD_CARSCH
	                           SET MODIFIER = 'SYSTEM'
	                              ,MOD_DDTT = SYSDATE
	                              ,YD_CAR_USE_GP = ?
	                              ,YD_EQP_WRK_STAT = ?
	                              ,ARR_WLOC_CD = ?
	                              ,YD_CARUD_LEV_DT= SYSDATE
				      ,YD_CARUD_PNT_WO_DT = SYSDATE
				      ,YD_CARUD_WRK_BOOK_ID = ?
				      ,YD_CARUD_STOP_LOC = ?
				      ,YD_CAR_PROG_STAT ='A'
	                          WHERE TRN_EQP_CD = ?
	  	             */
	  	          
	  	            
	  	          /*
	  	             * 포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
	  	          */
	  	          trnEqpQueryId 		= "ym.tsinfo.updateEquipcolStat";
	  	          iSeq = dao.updateData(trnEqpQueryId, new Object[]{ 	szYD_CAR_USE_GP,	  	            		                                            
							                                            szTRN_EQP_CD,
							                                            sunloadStoppoint}); 	  	           	  	            
	  	            
	  	          //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				    CarPointinforeg("3","",szTRN_EQP_CD,sunloadStoppoint,"","","R");
	  	            /*
	  	             * UPDATE TB_YM_STACKCOL
                       SET STACK_STAT = :STACK_STAT,
                           CAR_CARD_NO = :CAR_CARD_NO,
                           MODIFIER = 'TSYDJ004',
                           MOD_DDTT = sysdate
                       WHERE STACK_COL_GP = :STACK_COL_GP
	  	             */
	  	            
	  	                 
	  	            /*
	  	             * 20090801_YJK
	  	             * 향후 포인트 예약실적 I/F 추가
	  	             */
	  	            
	                // 포인트지시 모듈  호출
			    	//착지개소 및 포인트코드 추후 셋팅요 
					Paramrecord = JDTORecordFactory.getInstance().create(); 
					Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
					Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
					Paramrecord.setField("YD_PNT_CD", 		sunloadStopTsCd);
					Paramrecord.setField("YD_MSG_CD", 		sYD_MSG_CD);
					this.procMatlCarArrPntReq(Paramrecord);
	             
					isSuccess = true;
			
					logger.println(LogLevel.DEBUG,this,"=출발실적처리=>영차처리 종료"); 
				}
		    	
		    	//##########################################################################################################################################
				//공차인경우
				else if(szTRN_WRK_FULLVOID_GP.equals("E"))
				{

		    		/**
		    		 *  기존 출발정보 여부 확인 
		    		 */		
				//	   logger.println(LogLevel.DEBUG,this,"=기존 출발정보 여부 확인 ");
		    			/*
		    			 * select YD_CAR_SCH_ID,YD_CARLD_WRK_BOOK_ID,SPOS_WLOC_CD
	                       from TB_YD_CARSCH
	                       where TRN_EQP_CD = ?
	                       and DEL_YN = 'N'
	                       and YD_CAR_PROG_STAT  = '1'
		    			 */
		    				    			
		    	/*	    String QueryId 	= "ym.tsinfo.getListSposYNchk_E";
		    		    List sposChklist = dao.getCommonList(QueryId, new Object[]{szTRN_EQP_CD});
		    		    if(sposChklist.size() > 0)
		    		    { 
		    		    		    		    	
		    		    	logger.println(LogLevel.DEBUG,this,"=이미 공차출발정보가 있음 ");
		    		    	
		    		    	JDTORecord sposChkReq = (JDTORecord)sposChklist.get(0);
					    	String s_CARLD_WRK_BOOK_ID	= StringHelper.evl(sposChkReq.getFieldString("YD_CARLD_WRK_BOOK_ID"), "");
					    	String s_YD_CAR_SCH_ID	= StringHelper.evl(sposChkReq.getFieldString("YD_CAR_SCH_ID"), "");
					    	String s_SPOS_WLOC_CD	= StringHelper.evl(sposChkReq.getFieldString("SPOS_WLOC_CD"), "");
					    	
					    	
					    	if(s_SPOS_WLOC_CD.equals(szARR_WLOC_CD))
					    	{
					    		logger.println(LogLevel.DEBUG,this,"=신규수신된 전문의 착지가 기존전문된 수신과 동일하므로 SKIP====== ");
					    		return false;					    		
					    	}
					    	else
					    	{
					    		logger.println(LogLevel.DEBUG,this,"=신규수신된 전문의 착지가 기존전문된 수신과 다름. 출발취소처리와 동일한 로직 수행 ");
					    		Paramrecord2 = JDTORecordFactory.getInstance().create(); 	
					    		Paramrecord2.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
					    		Paramrecord2.setField("TRN_WRK_FULLVOID_GP", 	"E");//착지개소코드
					    		this.procStartdeleteRev(Paramrecord2);	
					    	}
					    				
		    		    }	    		    

		        */
										
					logger.println(LogLevel.DEBUG,this,"=출발실적처리=>공차처리 시작");   
					
					/*
					 * 현재는 권하완료 시점에 VL로 셋팅한다.
					 * 여기서 추가셋팅을 할것인지는 추후 검토한다(B-Cast 이송재 이동조건 여기서 셋팅해야 함.)
					 */
					String aimBay_All = ""; 
					String aimYD_All = ""; 
					
					logger.println(LogLevel.DEBUG,this,"=공차출발전문수신 ===== BCast");
					//A연주-B Cast Slab Yard(슬라브 이송의 경우)
					if(szARR_WLOC_CD.equals("D2Y43")||szARR_WLOC_CD.equals("D3Y43")) 
					{
																	
						
						logger.println(LogLevel.DEBUG,this,"=파레트 도착 목적동검색");
						
						if(szARR_WLOC_CD.equals("D2Y43"))
						{
							String queryID 	= "ym.ilkwan.dao.YdStockDAO.getListAimbayBCast";
					    	List AimbayBCast = dao.getCommonList(queryID);
					    			    	
					    	int iSeqCount 	= AimbayBCast.size();				    	
					    	String aimBay_BCast = "";
					    	if(iSeqCount >0){
					    	JDTORecord AimbayBCastReq = (JDTORecord)AimbayBCast.get(0);
					    	aimBay_BCast	= StringHelper.evl(AimbayBCastReq.getFieldString("AIM_BAY"), "");	
					    	logger.println(LogLevel.DEBUG,this,"=BCast 목적동 검색결과 : " + aimBay_BCast);	
					    	aimYD_All = "0";
					    	aimBay_All = aimBay_BCast;
					    	}
						}else if(szARR_WLOC_CD.equals("D3Y43")) ///////////////////////////////////////////////////////////////////
						{							
							String queryID 	= "ym.ilkwan.dao.YdStockDAO.getListAimbayBSlab";
					    	List AimbayBSlab = dao.getCommonList(queryID);
					    			    	
					    	int iSeqCount 	= AimbayBSlab.size();				    	
					    	String aimBay_BSlab = "";
					    	if(iSeqCount >0){
					    	JDTORecord AimbayBSlabReq = (JDTORecord)AimbayBSlab.get(0);
					    	aimBay_BSlab	= StringHelper.evl(AimbayBSlabReq.getFieldString("AIM_BAY"), "");	
					    	logger.println(LogLevel.DEBUG,this,"=BCast 목적동 검색결과 : " + aimBay_BSlab);	
					    	aimYD_All = "2";
					    	aimBay_All = aimBay_BSlab;
					    	}																					
						}
						
						
				
						logger.println(LogLevel.DEBUG,this,"=BCast 이송지시대상재 조회 시작");
						/* 착지별로 그룹핑
						 *   SELECT
								       (select SCH_RULE_VAL
								       from TB_YM_STACKPRIORITY
								       where RULE_ID = 'YM05'
								       and B.ARR_WLOC_CD = SCH_CD ) AS 개소순위,
								
								       (select SCH_RULE_VAL
								       from TB_YM_STACKPRIORITY
								       where RULE_ID = 'YM04'
								       and SUBSTR(A.STACK_COL_GP,2,1) = SCH_CD ) AS 동순위,
								       
								       (select SCH_RULE_VAL
								       from TB_YM_STACKPRIORITY
								       where RULE_ID = 'YM03'
								       and C.SCARFING_YN = SCH_CD ) AS 스카핑순위,
			
									   SUBSTR(A.STACK_COL_GP,2,1) AIM_BAY,							   
								       A.STOCK_ID,
								       A.STACK_COL_GP,
								       A.STACK_BED_GP,
								       A.STACK_LAYER_GP,
								       B.SPOS_WLOC_CD,      -- 발지개소
								       B.ARR_WLOC_CD      -- 착지개소
								FROM    TB_YM_STACKLAYER A,
								        TB_TS_MATL_FTMV_WO B,
								        VW_YD_SLABCOMM C
								WHERE   A.STOCK_ID = B.STL_NO
								AND     A.STOCK_ID = C.SLAB_NO
								AND     A.STACK_LAYER_STAT = 'L'
								AND     B.SPOS_WLOC_CD = ?
								AND     B.TS_MATL_FTMV_STAT_GP = '1'
								AND     B.MATL_FTMV_WO_NML_HD_YN = 'Y'
								ORDER BY 개소순위,
								         동순위,
								         스카핑순위,
								         A.STACK_COL_GP,
								         A.STACK_BED_GP,
								         A.STACK_LAYER_GP DESC
	
						*/
						//대상재 조회
						
 					
				    	String queryID 	= "ym.ilkwan.dao.YdStockDAO.getListFrtoStl_ASlab";
				    	FrtoProductList = dao.getCommonList(queryID, new Object[]{szARR_WLOC_CD});
				    	
				    	int iSeqCount 	= FrtoProductList.size();				    	
				    	String s_aimBay = "";
				    	if(iSeqCount >0){
				    	JDTORecord FrtoProductReq = (JDTORecord)FrtoProductList.get(0);
				    	s_aimBay	= StringHelper.evl(FrtoProductReq.getFieldString("AIM_BAY"), "");
				    		
				    	}

				    	logger.println(LogLevel.DEBUG,this,"=BCast 이송지시대상재 조회 종료");
				    	logger.println(LogLevel.DEBUG,this,"=BCast 이송지시대상재 조회 건 수"+iSeqCount);
				    	
				    	
				   // 	=========================================================================================================================================
				   // 	=========================================================================================================================================
				    	if(iSeqCount==0)//야드에 적치된 이송지시대상재가 없을경우
				    	{
				    		logger.println(LogLevel.DEBUG,this,"=BCast 공차출발실적 수신 =======야드에 적치된 이송지시대상재가 없을경우");
				    		sSchCode 		= YmCommonConst.NEW_SCH_WORK_KIND_SVML;
				    		
				    		logger.println(LogLevel.DEBUG,this,"=출발실적처리=>상차정지위치검색");
//				    		상차정지위치 검색
					    	//추가부분 : 검색대상재 동에 있는 차량위치정보  
				    		trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
				    		/*
								SELECT  STACK_COL_GP,
										YD_PNT_CD
								FROM TB_YM_STACKCOL 
								WHERE WLOC_CD     = :WLOC_CD
								AND YD_GP         = :YD_GP
								AND BAY_GP        = :BAY_GP
								AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
								AND TRN_EQP_CD  IS NULL	-- 현재차량정지위치
								AND CAR_CARD_NO IS NULL	-- 예약차량정지위치
					    	 */
				    		loadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{	szARR_WLOC_CD,
				    				                                                        aimYD_All ,
				    				                                                        aimBay_All,
																		    				szTRN_EQP_GP});
//				    		포인트 모두 점유상태일때 상차완료된 포인트찾음					    	  
				    		trnEqpQueryId = "ym.tsinfo.getListloadEndpoint";
				    		/*
								SELECT B.STACK_COL_GP,
                                       B.YD_PNT_CD
                               FROM TB_YD_CARSCH A,TB_YM_STACKCOL B
                               WHERE A.DEL_YN = 'N'
                               AND A.YD_CAR_PROG_STAT = '5'
                               AND SUBSTR(YD_CARLD_STOP_LOC,1,1) = ?
                               AND SUBSTR(YD_CARLD_STOP_LOC,2,1) = ?
                               AND SUBSTR(YD_CARLD_STOP_LOC, 3, 2) = ?
                               AND A.YD_CARLD_STOP_LOC = B.STACK_COL_GP
					    	 */
				    		loadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{aimYD_All,
				    				                                                         aimBay_All,
																		    				 szTRN_EQP_GP});
				    		
				    		
					    	
				    		if(loadPointList.size() > 0){
						    	JDTORecord loadPointrec = (JDTORecord)loadPointList.get(0);
						    	sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
						    	sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
				    		}else if(loadEndPointList.size() > 0){
				    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>상차완료 포인트 검색");
						    	JDTORecord loadPointrec = (JDTORecord)loadEndPointList.get(0);
						    	sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
						    	sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
				    		}else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
				    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>상차완료 포인트 없음 => 포인트없음 처리");
				    			sloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
				    			sloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;
				    			
				    			//대기장 포인트 사유 가져오기
  				    			sYD_MSG_CD = this.getCarMsg("N" ,aimYD_All , aimBay_All , szTRN_EQP_GP , szARR_WLOC_CD,"");
  				    			
				    			Paramrecord = JDTORecordFactory.getInstance().create(); 
								Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
								Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
								Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
								
								// 발지 차량 적치단 Clear 하기
								layerState = "C";
								queryID1 = "ym.tsinfo.updateLayerstat_02_new";
								count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
								
//								발지 차량정보 삭제 하기 
						    	queryID1 = "ym.tsinfo.updateLayerstat_03";
						    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
						    	
						    	//차량포인트통합관리(구분, 장비번호,저장위치,상태)
							    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
						    	
						    							    	
						    	//if(count >0 ){
						    	//layerState 	= "C";
						    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
						    	//count = dao.updateData(queryID1, new Object[]{	layerState,
						    	//												szSPOS_WLOC_CD,
						    	//												szSPOS_YD_PNT_CD,
						    	//												szTRN_EQP_GP}); 		
						    	//}
				    		}
				    		
//				    		 야드맵 Close
//					    	queryID1 = "ym.tsinfo.updateLayerstat_01";
//					    	count = dao.updateData(queryID1, new Object[]{	"","","","",
//					    													szSPOS_WLOC_CD,
//					    													szSPOS_YD_PNT_CD,
//					    													szTRN_EQP_GP}); 
//					    	/*
//						    	UPDATE  TB_YM_STACKCOL
//								SET     YD_CAR_USE_GP   = :YD_CAR_USE_GP,
//								        TRN_EQP_CD      = :TRN_EQP_CD,
//								        CAR_NO          = :CAR_NO,
//								        CARD_NO         = :CARD_NO
//								WHERE WLOC_CD   = :WLOC_CD
//								AND   YD_PNT_CD = :YD_PNT_CD
//								AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
//							*/
//					    	
							// 발지 차량 적치단 Clear 하기
							layerState = "C";
							queryID1 = "ym.tsinfo.updateLayerstat_02_new";
							count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
							
//				    		발지 차량정보 삭제 하기 
					    	queryID1 = "ym.tsinfo.updateLayerstat_03";
					    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
					    	
					    	//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
						    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
					    	
							sMoveterm   = YmCommonConst.NEW_STOCK_MOVE_TERM_CS;
							
							//if(count >0 ){
							//layerState 	= "C";
					    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
					    	//count = dao.updateData(queryID1, new Object[]{	layerState,
					    	//												szSPOS_WLOC_CD,
					    	//												szSPOS_YD_PNT_CD,
					    	//												szTRN_EQP_GP}); 
							//}
					     	/*
					    	 * UPDATE TB_YM_STACKLAYER
				               SET  STOCK_ID = '',
				                    STACK_LAYER_ACTIVE_STAT = ?,
				                    STACK_LAYER_STAT = 'E' 
				               WHERE STACK_COL_GP = (SELECT A.STACK_COL_GP	    	 		
									                 FROM TB_YM_STACKCOL A
								                     WHERE  A.WLOC_CD 	= ?
								                       AND 	A.YD_PNT_CD = ?
			                                           AND SUBSTR(A.STACK_COL_GP,3,2) = :TRN_EQP_GP
									                 )
					    	 */
					    	
							
					    	//차량스케쥴테이블에 insert
				    		szYD_CAR_USE_GP 	= "L";
				    		szYD_EQP_WRK_STATE 	= "U";//공차경우
			  	            trnEqpQueryId 		= "ym.tsinfo.inserttrnEqpsch";
					    	iSeq = dao.insertData(trnEqpQueryId, new Object[]{ 	szTRN_EQP_CD,
					    			                                            szYD_CAR_USE_GP,
					    			                                            szTRN_EQP_CD,
					    			                                            szYD_EQP_WRK_STATE,
					    			                                            szARR_WLOC_CD,
					    														"", //착지개소코드
					    														"",
					    														sloadStoppoint}); 
					    	/*
					    	 * INSERT INTO TB_YD_CARSCH
								(
								    YD_CAR_SCH_ID
								    ,DEL_YN
								    ,YD_EQP_ID
								    ,YD_CAR_USE_GP
								    ,TRN_EQP_CD
								    ,YD_EQP_WRK_STAT
								    ,SPOS_WLOC_CD
								    ,ARR_WLOC_CD
								    ,YD_CARLD_LEV_DT
								    ,YD_CARLD_PNT_WO_DT
								    ,YD_CARLD_WRK_BOOK_ID
								    ,YD_CARLD_STOP_LOC
								    ,YD_CAR_PROG_STAT 
								)
								SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') ||  LPAD(YD_CARSCH_SEQ.nextval,6,'0') AS YD_CAR_SCH_ID
								,'N'
								,(SELECT YD_EQP_ID
								  FROM TB_YD_CARSPEC
								  WHERE TRN_EQP_CD = :TRN_EQP_CD
								  AND DEL_YN = 'N')YD_EQP_ID
								,:YD_CAR_USE_GP
								,:TRN_EQP_CD
								,:YD_EQP_WRK_STAT
								,:SPOS_WLOC_CD
								,:ARR_WLOC_CD
								,SYSDATE
								,SYSDATE
								,:YD_CARLD_WRK_BOOK_ID
								,:YD_CARLD_STOP_LOC
								,'1'
								FROM DUAL
					    	 */
							
					      /*
			  	             * 포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
			  	          */
			  	          trnEqpQueryId 		= "ym.tsinfo.updateEquipcolStat";
			  	          iSeq = dao.updateData(trnEqpQueryId, new Object[]{ 	szYD_CAR_USE_GP,	  	            		                                            
									                                            szTRN_EQP_CD,
									                                            sloadStoppoint}); 	  
			  	          
			  	      //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
						    CarPointinforeg("3","",szTRN_EQP_CD,sloadStoppoint,"","","R");
			  	            
			  	            /*
			  	             * UPDATE TB_YM_STACKCOL
		                       SET STACK_STAT = :STACK_STAT,
		                           CAR_CARD_NO = :CAR_CARD_NO,
		                           MODIFIER = 'TSYDJ004',
		                           MOD_DDTT = sysdate
		                       WHERE STACK_COL_GP = :STACK_COL_GP
			  	             */
			  	          
					    	
					    	/*
			  	             * 20090801_YJK
			  	             * 향후 포인트 예약실적 I/F 추가
			  	             */
					    	
					    	//포인트지시 모듈 독립적으로 만들어서 호출
					    	//착지개소 및 포인트코드 추후 셋팅요 
							Paramrecord = JDTORecordFactory.getInstance().create(); 
							Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
							Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
							Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
							Paramrecord.setField("YD_MSG_CD", 		sYD_MSG_CD);
							
							this.procMatlCarArrPntReq(Paramrecord);
			            
							isSuccess = true;
		    		
				    	}				    		
				    		
				    	// 	=========================================================================================================================================
						// 	=========================================================================================================================================		    		
							    	
				    	//야드에 적치된 이송대상재가 존재할 경우
				    	else if(iSeqCount>0)
				    	{
				    		logger.println(LogLevel.DEBUG,this,"=BCast 공차출발실적 수신 =======야드에 적치된 이송지시대상재가 존재 할 경우");
					    	
				    	      //목적동의 상차대상편성 방법이 자동인지 수동인지 조회	
				    		String s_upLoadMhthod = "";
				    		 if(szTRN_EQP_GP.equals("PT"))
				    		 {
				    			 queryID 	= "ym.tsinfo.getUploadMethod";
					    	      /*
					    	       * select DOWN_CD
	                               from TB_YM_EQUIP
	                               WHERE EQUIP_GP LIKE ?||?||'PT%'
					    	       */
					    	      uploadMethodList = dao.getCommonList(queryID, new Object[]{aimYD_All,s_aimBay});
					    	      JDTORecord uploadMethodReq = (JDTORecord)uploadMethodList.get(0);
					    	      s_upLoadMhthod	= StringHelper.evl(uploadMethodReq.getFieldString("DOWN_CD"), "");				    			 
				    			 
				    		 }
				    		 else if(szTRN_EQP_GP.equals("TR"))
				    		 {
				    			 queryID 	= "ym.tsinfo.getUploadMethod_TR";
					    	      /*
					    	       * select DOWN_CD
	                               from TB_YM_EQUIP
	                               WHERE EQUIP_GP LIKE ?||?||'PT%'
					    	       */
					    	      uploadMethodList = dao.getCommonList(queryID, new Object[]{aimYD_All,s_aimBay});
					    	      JDTORecord uploadMethodReq = (JDTORecord)uploadMethodList.get(0);
					    	      s_upLoadMhthod	= StringHelper.evl(uploadMethodReq.getFieldString("DOWN_CD"), "");				    			 
				    			 
				    		 }
				    		
				    	      
				    		
				    	    if(s_upLoadMhthod.equals("A"))
				    	    {
				    		    JDTORecord FrtoProduct3 = (JDTORecord)FrtoProductList.get(0);
					    	    s_STACK_YD_GP 	= StringHelper.evl(FrtoProduct3.getFieldString("STACK_COL_GP"),"").substring(0,1);
							    s_STACK_BAY_GP 	= StringHelper.evl(FrtoProduct3.getFieldString("STACK_COL_GP"),"").substring(1,2);
							
							    logger.println(LogLevel.DEBUG,this,"=출발실적처리=>상차정지위치검색");
							    //상차정지위치 검색
					    	    //추가부분 : 검색대상재 동에 있는 차량위치정보  
				    		    trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
				    		    /*
					    	 	SELECT  STACK_COL_GP,
										YD_PNT_CD
								FROM TB_YM_STACKCOL 
								WHERE WLOC_CD     = :WLOC_CD
								AND YD_GP         = :YD_GP
								AND BAY_GP        = :BAY_GP
								AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
								AND TRN_EQP_CD  IS NULL	-- 현재차량정지위치
								AND CAR_CARD_NO IS NULL	-- 예약차량정지위치
					    	 */
				    		    loadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{	szARR_WLOC_CD,
																		    				s_STACK_YD_GP,
																		    				s_STACK_BAY_GP,
																		    				szTRN_EQP_GP});
				    		
				    		
				    		
				    		    //포인트 모두 점유상태일때 상차완료된 포인트찾음					    	  
				    		    trnEqpQueryId = "ym.tsinfo.getListloadEndpoint";
				    		    /*
								SELECT B.STACK_COL_GP,
                                       B.YD_PNT_CD
                               FROM TB_YD_CARSCH A,TB_YM_STACKCOL B
                               WHERE A.DEL_YN = 'N'
                               AND A.YD_CAR_PROG_STAT = '5'
                               AND SUBSTR(YD_CARLD_STOP_LOC,1,1) = ?
                               AND SUBSTR(YD_CARLD_STOP_LOC,2,1) = ?
                               AND SUBSTR(YD_CARLD_STOP_LOC, 3, 2) = ?
                               AND A.YD_CARLD_STOP_LOC = B.STACK_COL_GP
					    	 */
				    		    loadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STACK_YD_GP,
				    				                                                        s_STACK_BAY_GP,
																		    				szTRN_EQP_GP});
				    		
					    	
				    		    if(loadPointList.size() > 0){
						    	    JDTORecord loadPointrec = (JDTORecord)loadPointList.get(0);
						    	    sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
						    	    sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
				    		    }else if(loadEndPointList.size() > 0){
				    		    	logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>상차완료 포인트 검색");
						    	    JDTORecord loadPointrec = (JDTORecord)loadEndPointList.get(0);
						    	    sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
						    	    sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
				    		    }else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
				    		    	logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>상차완료 포인트 없음 => 포인트없음 처리");
				    		    	sloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
				    		    	sloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;
				    		    	
				    		    	//대기장 포인트 사유 가져오기
	  				    			sYD_MSG_CD = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD,"");
	  				    			
				    			    Paramrecord = JDTORecordFactory.getInstance().create(); 
								    Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
								    Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
								    Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
								    
									// 발지 차량 적치단 Clear 하기
									layerState = "C";
									queryID1 = "ym.tsinfo.updateLayerstat_02_new";
									count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
									
//								  발지 차량정보 삭제 하기 
							    	queryID1 = "ym.tsinfo.updateLayerstat_03";
							    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
							    	
							    	//차량포인트통합관리(구분, 장비번호,저장위치,상태)
								    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
								    
							    	//if(count >0 ){
							    	//layerState 	= "C";
							    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
							    	//count = dao.updateData(queryID1, new Object[]{	layerState,
							    	//												szSPOS_WLOC_CD,
							    	//												szSPOS_YD_PNT_CD,
							    	//												szTRN_EQP_GP}); 	
							    	//}
				    		    }
				    	    }
				    	    else if(s_upLoadMhthod.equals("M"))
				    	    {
				    	    	logger.println(LogLevel.DEBUG,this,"=출발실적처리=>상차정지위치검색");
																				    //상차정지위치 검색
					    	    //추가부분 : 검색대상재 동에 있는 차량위치정보  
				    		    trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
				    		    /*
					    	 	SELECT  STACK_COL_GP,
										YD_PNT_CD
								FROM TB_YM_STACKCOL 
								WHERE WLOC_CD     = :WLOC_CD
								AND YD_GP         = :YD_GP
								AND BAY_GP        = :BAY_GP
								AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
								AND TRN_EQP_CD  IS NULL	-- 현재차량정지위치
								AND CAR_CARD_NO IS NULL	-- 예약차량정지위치
					    	 */
				    		    loadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{	szARR_WLOC_CD,
				    		    		                                                    aimYD_All ,
				    		    		                                                    aimBay_All,
																		    				szTRN_EQP_GP});
				    		
				    		
				    		
				    		    //포인트 모두 점유상태일때 상차완료된 포인트찾음					    	  
				    		    trnEqpQueryId = "ym.tsinfo.getListloadEndpoint";
				    		    /*
								SELECT B.STACK_COL_GP,
                                       B.YD_PNT_CD
                               FROM TB_YD_CARSCH A,TB_YM_STACKCOL B
                               WHERE A.DEL_YN = 'N'
                               AND A.YD_CAR_PROG_STAT = '5'
                               AND SUBSTR(YD_CARLD_STOP_LOC,1,1) = ?
                               AND SUBSTR(YD_CARLD_STOP_LOC,2,1) = ?
                               AND SUBSTR(YD_CARLD_STOP_LOC, 3, 2) = ?
                               AND A.YD_CARLD_STOP_LOC = B.STACK_COL_GP
					    	 */
				    		    loadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{aimYD_All,
				    		    		                                                         aimBay_All,
																		    				szTRN_EQP_GP});
				    		
					    	
				    		    if(loadPointList.size() > 0){
						    	    JDTORecord loadPointrec = (JDTORecord)loadPointList.get(0);
						    	    sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
						    	    sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
				    		    }else if(loadEndPointList.size() > 0){
				    		    	logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>상차완료 포인트 검색");
						    	    JDTORecord loadPointrec = (JDTORecord)loadEndPointList.get(0);
						    	    sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
						    	    sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
				    		    }else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
				    		    	logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>상차완료 포인트 없음 => 포인트없음 처리");
				    		    	sloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
				    		    	sloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;
				    		    	
				    		    	//대기장 포인트 사유 가져오기
	  				    			sYD_MSG_CD = this.getCarMsg("N" ,aimYD_All , aimBay_All , szTRN_EQP_GP , szARR_WLOC_CD,"");
	  				    			
				    			    Paramrecord = JDTORecordFactory.getInstance().create(); 
								    Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
								    Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
								    Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
								    
									// 발지 차량 적치단 Clear 하기
									layerState = "C";
									queryID1 = "ym.tsinfo.updateLayerstat_02_new";
									count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
									
//								  발지 차량정보 삭제 하기 
							    	queryID1 = "ym.tsinfo.updateLayerstat_03";
							    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
					
							    	//차량포인트통합관리(구분, 장비번호,저장위치,상태)
								    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
								    
							    	//if(count >0 ){
							    	//layerState 	= "C";
							    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
							    	//count = dao.updateData(queryID1, new Object[]{	layerState,
							    	//												szSPOS_WLOC_CD,
							    	//												szSPOS_YD_PNT_CD,
							    	//												szTRN_EQP_GP}); 	
							    	//}
				    		    }
				    	    }				    						    						    		
				    		
//				    		 야드맵 Close
//					    	queryID1 = "ym.tsinfo.updateLayerstat_01";
//					    	count = dao.updateData(queryID1, new Object[]{	"","","","",
//					    													szSPOS_WLOC_CD,
//					    													szSPOS_YD_PNT_CD,
//					    													szTRN_EQP_GP}); 
//					    	/*
//						    	UPDATE  TB_YM_STACKCOL
//								SET     YD_CAR_USE_GP   = :YD_CAR_USE_GP,
//								        TRN_EQP_CD      = :TRN_EQP_CD,
//								        CAR_NO          = :CAR_NO,
//								        CARD_NO         = :CARD_NO
//								WHERE WLOC_CD   = :WLOC_CD
//								AND   YD_PNT_CD = :YD_PNT_CD
//								AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
//							*/
					    	
					    	
							// 발지 차량 적치단 Clear 하기
							layerState = "C";
							queryID1 = "ym.tsinfo.updateLayerstat_02_new";
							count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
							
//				    	  발지 차량정보 삭제 하기 
					    	queryID1 = "ym.tsinfo.updateLayerstat_03";
					    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
					    	
					    	//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
						    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
						    
							sMoveterm   = YmCommonConst.NEW_STOCK_MOVE_TERM_CS;
							
							//if(count >0 ){
							//layerState 	= "C";
					    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
					    	//count = dao.updateData(queryID1, new Object[]{	layerState,
					    	//												szSPOS_WLOC_CD,
					    	//												szSPOS_YD_PNT_CD,
					    	//												szTRN_EQP_GP}); 
							//}
					     	/*
					    	 * UPDATE TB_YM_STACKLAYER
				               SET  STOCK_ID = '',
				                    STACK_LAYER_ACTIVE_STAT = ?,
				                    STACK_LAYER_STAT = 'E' 
				               WHERE STACK_COL_GP = (SELECT A.STACK_COL_GP	    	 		
									                 FROM TB_YM_STACKCOL A
								                     WHERE  A.WLOC_CD 	= ?
								                       AND 	A.YD_PNT_CD = ?
			                                           AND SUBSTR(A.STACK_COL_GP,3,2) = :TRN_EQP_GP
									                 )
					    	 */
					    	

				    	
				    	      if(s_upLoadMhthod.equals("A")) // 자동편성인 경우 
				    	      {  
				    	    	  logger.println(LogLevel.DEBUG,this,"=BCast 공차출발실적 수신 =======해당 목적동의 대상재 편성 방법이 자동");
				    	                sSchCode 		= YmCommonConst.NEW_SCH_WORK_KIND_SVML;
				    	    	  
//				    	    	            상차 LOT편성

								       //작업예약생성	
								    	wBookQueryId 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";								
										wBookSel 		= ydStackLayerDAO.requestFind(wBookQueryId);
										wBookid      	= wBookSel.getFieldString("WBOOK_ID");
								    	
								    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
								    	s_STACK_YD_GP 	= StringHelper.evl(FrtoProduct.getFieldString("STACK_COL_GP"),"").substring(0,1);
										s_STACK_BAY_GP 	= StringHelper.evl(FrtoProduct.getFieldString("STACK_COL_GP"),"").substring(1,2);
										
										//작업예약테이블에 insert
										sWBookQueryId 	= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
										 
										iSeq = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ wBookid, 
												 s_STACK_YD_GP, 
												 s_STACK_BAY_GP,
												 sSchCode, 
												 YmCommonUtil.getWorkDuty(),
												 YmCommonUtil.getWorkParty()});


							    	/**
							    	 *  차량스펙 및 그룹핑재료정보에 따른 상차물량 결정처리
							    	 */
							    	int iLoopIndex 		= 0;
							    	int iLoadMax = 0;
							    	String sLoopIndex1 	= "";
							    	String sLoopIndex2 	= "";
							    	{ 
							    		//int iLoadMax = 0;
							    		if(szTRN_EQP_GP.equals("TR"))
							    		{
							    			/*iLoadMax = 4;
							    			STK_CAPA = Integer.parseInt(szTRN_EQP_STK_CAPA);
							    			for(int ii=0 ; ii < iLoadMax ; ii++){
							    				
							    				JDTORecord stkwtRwq = (JDTORecord)FrtoProductList.get(ii);
							    				
							    				STK_WT += stkwtRwq.getFieldInt("STK_WT");
							    				
							    				if(STK_WT > STK_CAPA)
							    				{
							    					iLoadMax = ii;
							    					break;
							    				}
							    			}*/
							    		
							    		 iLoadMax = 4;	
							    		
							    		}
							    		else 
							    		{
								    		iLoadMax = 4;	
							    		}

							    		if(iLoadMax > iSeqCount) iLoopIndex = iSeqCount;
							    		else iLoopIndex = iLoadMax;
							    		
							    		for(int index=0; index < iLoopIndex ; index++){
							    			
											JDTORecord FrtoProduct2 = (JDTORecord)FrtoProductList.get(index);
											sLoopIndex2	 = "";
											sLoopIndex2	+= StringHelper.evl(FrtoProduct2.getFieldString("개소순위"),"");
								    		sLoopIndex2	+= StringHelper.evl(FrtoProduct2.getFieldString("동순위"),"");
								    		sLoopIndex2	+= StringHelper.evl(FrtoProduct2.getFieldString("스카핑순위"),"");
								    		
								    		logger.println(LogLevel.DEBUG,this,"=재료그룹속성1 => "+ sLoopIndex1);   
								    		logger.println(LogLevel.DEBUG,this,"=재료그룹속성2 => "+ sLoopIndex2);   
								    		if(index == 0 ){
								    			sLoopIndex1 = sLoopIndex2;
								    		}else{
								    			if(sLoopIndex1.equals(sLoopIndex2)){
								    				
								    			}else{
								    				iLoopIndex = index;
								    				break;
								    			}
								    		}
							    		}
							    	}

							    	
							    	for(int index=0; index < iLoopIndex ; index++){
										
							    		JDTORecord FrtoProduct2 = (JDTORecord)FrtoProductList.get(index);
										s_STOCK_ID = StringHelper.evl(FrtoProduct2.getFieldString("STOCK_ID"),"");
										
										//이전 작업예약 존재 유무 체크  
										trnQueryId 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID3";
							    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{s_STOCK_ID});
							 
							    		JDTORecord TolocRecCoil = (JDTORecord)FrtostlList.get(0);
							    		wBookid2 = StringHelper.evl(TolocRecCoil.getFieldString("WBOOK_ID"),"");
							    		
							    		if(!wBookid2.equals("")){
							    			stkQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteWbookInfo";
												iSeq = ydStockDAO.deleteData(stkQueryId, new Object[]{wBookid2});
							    		}
										
										//저장품테이블에 update				
										stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
										iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ wBookid, 
												                                                      sMoveterm,
																									  s_STOCK_ID});
										/*
						                 *	적치단  Table Update(작업요구상태='S'로 변경)
						                 */
						            	String sQuery4 = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
						            	iSeq = ydStockDAO.requestupdateData(sQuery4, 
																		    new Object[]{YmCommonConst.STACK_LAYER_STAT_S, 
						            										s_STOCK_ID });
							    	}
									

									
							    	//차량스케쥴테이블에 insert
						    		
						    		szYD_CAR_USE_GP 	= "L";
						    		szYD_EQP_WRK_STATE 	= "U";//공차경우
					  	            trnEqpQueryId 		= "ym.tsinfo.inserttrnEqpsch";
							    	iSeq = dao.insertData(trnEqpQueryId, new Object[]{ 	szTRN_EQP_CD,
							    			                                            szYD_CAR_USE_GP,
							    			                                            szTRN_EQP_CD,
							    			                                            szYD_EQP_WRK_STATE,
							    			                                            szARR_WLOC_CD,
							    														"", //착지개소코드
							    														wBookid,
							    														sloadStoppoint}); 
							    	/*
							    	 * INSERT INTO TB_YD_CARSCH
										(
										    YD_CAR_SCH_ID
										    ,DEL_YN
										    ,YD_EQP_ID
										    ,YD_CAR_USE_GP
										    ,TRN_EQP_CD
										    ,YD_EQP_WRK_STAT
										    ,SPOS_WLOC_CD
										    ,ARR_WLOC_CD
										    ,YD_CARLD_LEV_DT
										    ,YD_CARLD_PNT_WO_DT
										    ,YD_CARLD_WRK_BOOK_ID
										    ,YD_CARLD_STOP_LOC
										    ,YD_CAR_PROG_STAT 
										)
										SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') ||  LPAD(YD_CARSCH_SEQ.nextval,6,'0') AS YD_CAR_SCH_ID
										,'N'
										,(SELECT YD_EQP_ID
										  FROM TB_YD_CARSPEC
										  WHERE TRN_EQP_CD = :TRN_EQP_CD
										  AND DEL_YN = 'N')YD_EQP_ID
										,:YD_CAR_USE_GP
										,:TRN_EQP_CD
										,:YD_EQP_WRK_STAT
										,:SPOS_WLOC_CD
										,:ARR_WLOC_CD
										,SYSDATE
										,SYSDATE
										,:YD_CARLD_WRK_BOOK_ID
										,:YD_CARLD_STOP_LOC
										,'1'
										FROM DUAL
							    	 */

							    	//차량스케쥴ID조회
						    		trnEqpQueryId = "ym.tsinfo.getListtrnEqpschL";
						    		carSchList = dao.getCommonList(trnEqpQueryId, new Object[]{szTRN_EQP_CD});
							    	
						    		if(carSchList.size() > 0){
								    	JDTORecord CarSchrec = (JDTORecord)carSchList.get(0);
								    	sCarSchCode  = StringHelper.evl(CarSchrec.getFieldString("YD_CAR_SCH_ID"), "");
						    		}
							    	/*
							    	 * SELECT YD_CARLD_WRK_BOOK_ID WBOOK_ID
							    	 *        ,YD_CAR_SCH_ID
						                FROM TB_YD_CARSCH
						               WHERE TRN_EQP_CD = ?
						               AND YD_CAR_PROG_STAT = '1'
						               AND (DEL_YN IS NULL OR DEL_YN <> 'Y')
							    	 */	
										
							    	//차량대상재 테이블에 insert	
							    	for(int index=0; index < iLoopIndex ; index++){
							    		
							    		logger.println(LogLevel.DEBUG,this,"=출발실적처리=>차량스케쥴 대상재 등록 ");   
							    		
							    		JDTORecord FrtoProduct2 = (JDTORecord)FrtoProductList.get(index);
										s_STOCK_ID = StringHelper.evl(FrtoProduct2.getFieldString("STOCK_ID"),"");
										s_BED_GP = StringHelper.evl(FrtoProduct2.getFieldString("STACK_BED_GP"),"");
										s_LAYER_GP = StringHelper.evl(FrtoProduct2.getFieldString("STACK_LAYER_GP"),"");
										
										trnEqpQueryId = "ym.tsinfo.insertCarftmtl";
							    		iSeq = dao.insertData(trnEqpQueryId, new Object[]{ sCarSchCode,s_STOCK_ID}); 
								    	/*
								    	 *  INSERT INTO TB_YD_CARFTMVMTL
											(
											    YD_CAR_SCH_ID
											   ,STL_NO
											   ,DEL_YN						   
											)
											VALUES (
											    :YD_CAR_SCH_ID,
											    :STL_NO,
											    'N'					    
											)
								    	 */
							    	}
							    	
							     /*
					  	             * 포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
					  	          */
					  	          trnEqpQueryId 		= "ym.tsinfo.updateEquipcolStat";
					  	          iSeq = dao.updateData(trnEqpQueryId, new Object[]{ 	szYD_CAR_USE_GP,	  	            		                                            
											                                            szTRN_EQP_CD,
											                                            sloadStoppoint}); 	  	           	  	            
					  	            
					  	          //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
								  CarPointinforeg("3","",szTRN_EQP_CD,sloadStoppoint,"","","R");
					  	            /*
					  	             * UPDATE TB_YM_STACKCOL
				                       SET STACK_STAT = :STACK_STAT,
				                           CAR_CARD_NO = :CAR_CARD_NO,
				                           MODIFIER = 'TSYDJ004',
				                           MOD_DDTT = sysdate
				                       WHERE STACK_COL_GP = :STACK_COL_GP
					  	             */
							    	
					  	          
							    	/*
					  	             * 20090801_YJK
					  	             * 향후 포인트 예약실적 I/F 추가
					  	             */
							    	
							    	//포인트지시 모듈 독립적으로 만들어서 호출
							    	//착지개소 및 포인트코드 추후 셋팅요 
									Paramrecord = JDTORecordFactory.getInstance().create(); 
									Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
									Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
									Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
									Paramrecord.setField("YD_MSG_CD", 		sYD_MSG_CD);
									
									this.procMatlCarArrPntReq(Paramrecord);
					            
									isSuccess = true;				    	    	  	
				    	      }
  	      
				    	      else // 수동편성인 경우
				    	      {
				    	    	   logger.println(LogLevel.DEBUG,this,"=BCast 공차출발실적 수신 =======해당 목적동의 대상재 편성 방법이 자동");
				    	    		sSchCode 		= YmCommonConst.NEW_SCH_WORK_KIND_SVML;
				    	    		
									
							    	//차량스케쥴테이블에 insert
						    		szYD_CAR_USE_GP 	= "L";
						    		szYD_EQP_WRK_STATE 	= "U";//공차경우
					  	            trnEqpQueryId 		= "ym.tsinfo.inserttrnEqpsch";
							    	iSeq = dao.insertData(trnEqpQueryId, new Object[]{ 	szTRN_EQP_CD,
							    			                                            szYD_CAR_USE_GP,
							    			                                            szTRN_EQP_CD,
							    			                                            szYD_EQP_WRK_STATE,
							    			                                            szARR_WLOC_CD,
							    														"", //착지개소코드
							    														"",
							    														sloadStoppoint}); 
							    	/*
							    	 * INSERT INTO TB_YD_CARSCH
										(
										    YD_CAR_SCH_ID
										    ,DEL_YN
										    ,YD_EQP_ID
										    ,YD_CAR_USE_GP
										    ,TRN_EQP_CD
										    ,YD_EQP_WRK_STAT
										    ,SPOS_WLOC_CD
										    ,ARR_WLOC_CD
										    ,YD_CARLD_LEV_DT
										    ,YD_CARLD_PNT_WO_DT
										    ,YD_CARLD_WRK_BOOK_ID
										    ,YD_CARLD_STOP_LOC
										    ,YD_CAR_PROG_STAT 
										)
										SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') ||  LPAD(YD_CARSCH_SEQ.nextval,6,'0') AS YD_CAR_SCH_ID
										,'N'
										,(SELECT YD_EQP_ID
										  FROM TB_YD_CARSPEC
										  WHERE TRN_EQP_CD = :TRN_EQP_CD
										  AND DEL_YN = 'N')YD_EQP_ID
										,:YD_CAR_USE_GP
										,:TRN_EQP_CD
										,:YD_EQP_WRK_STAT
										,:SPOS_WLOC_CD
										,:ARR_WLOC_CD
										,SYSDATE
										,SYSDATE
										,:YD_CARLD_WRK_BOOK_ID
										,:YD_CARLD_STOP_LOC
										,'1'
										FROM DUAL
							    	 */
									
							      /*
					  	             * 포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
					  	          */
					  	          trnEqpQueryId 		= "ym.tsinfo.updateEquipcolStat";
					  	          iSeq = dao.updateData(trnEqpQueryId, new Object[]{ 	szYD_CAR_USE_GP,	  	            		                                            
											                                            szTRN_EQP_CD,
											                                            sloadStoppoint}); 	  	           	  	            
					  	            
					  	          //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
								  CarPointinforeg("3","",szTRN_EQP_CD,sloadStoppoint,"","","R");
								  
					  	            /*
					  	             * UPDATE TB_YM_STACKCOL
				                       SET STACK_STAT = :STACK_STAT,
				                           CAR_CARD_NO = :CAR_CARD_NO,
				                           MODIFIER = 'TSYDJ004',
				                           MOD_DDTT = sysdate
				                       WHERE STACK_COL_GP = :STACK_COL_GP
					  	             */
					  	          
							    	
							    	/*
					  	             * 20090801_YJK
					  	             * 향후 포인트 예약실적 I/F 추가
					  	             */
							    	
							    	//포인트지시 모듈 독립적으로 만들어서 호출
							    	//착지개소 및 포인트코드 추후 셋팅요 
									Paramrecord = JDTORecordFactory.getInstance().create(); 
									Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
									Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
									Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
									Paramrecord.setField("YD_MSG_CD", 		sYD_MSG_CD);
									
									this.procMatlCarArrPntReq(Paramrecord);
					            
									isSuccess = true;
				    	      }
				    	}
					}
//#######################################################AB열연 코일 이송 #####################################################################					
					else if(szARR_WLOC_CD.equals("D2Y44")||
							szARR_WLOC_CD.equals("D2Y45")||
							szARR_WLOC_CD.equals("D3Y41")||
							szARR_WLOC_CD.equals("D3Y42")){
										
						if(szARR_WLOC_CD.equals("D3Y41")||
							szARR_WLOC_CD.equals("D3Y42"))
						{															         						
							//대상재 조회
					    	String queryID 	= "ym.ilkwan.dao.YdStockDAO.getListFrtoStl_CoilNewB";
					    	FrtoProductList = dao.getCommonList(queryID, new Object[]{szARR_WLOC_CD,szTRN_EQP_CD,szTRN_EQP_CD,szARR_WLOC_CD});
						}else if(szARR_WLOC_CD.equals("D2Y44")||
							szARR_WLOC_CD.equals("D2Y45"))
						{															         						
							//대상재 조회
					    	String queryID 	= "ym.ilkwan.dao.YdStockDAO.getListFrtoStl_CoilNewA";
					    	FrtoProductList = dao.getCommonList(queryID, new Object[]{szARR_WLOC_CD,szTRN_EQP_CD,szTRN_EQP_CD,szARR_WLOC_CD});
						}
					
					if(FrtoProductList.size() > 0){	
						JDTORecord AvaPointList = (JDTORecord)FrtoProductList.get(0);
				    	s_STACK_YD_GP 	= StringHelper.evl(AvaPointList.getFieldString("STACK_COL_GP"),"").substring(0,1);
						s_STACK_BAY_GP 	= StringHelper.evl(AvaPointList.getFieldString("STACK_COL_GP"),"").substring(1,2);	
					}
					
					logger.println(LogLevel.DEBUG,this,"=출발실적처리=>상차정지위치검색|"+FrtoProductList.size());
					//상차정지위치 검색
				    //추가부분 : 검색대상재 동에 있는 차량위치정보  
			    	trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
			    		/*
								select AA.STACK_COL_GP,
								       AA.YD_PNT_CD
								from(
									SELECT max(A.STACK_COL_GP)STACK_COL_GP,
									       A.YD_PNT_CD,
									       sum(A.count)count
									from(
										SELECT STACK_COL_GP, 
										       YD_PNT_CD, 
										       1 count  
										FROM TB_YM_STACKCOL 
										WHERE WLOC_CD     = ?
										AND YD_GP      =  ?
										AND BAY_GP    = ?
										AND SUBSTR(STACK_COL_GP,3,2) in (?,'PT','TR')
										AND TRN_EQP_CD  IS NULL	-- 현재차량정지위치
										AND CAR_CARD_NO IS NULL	-- 예약차량정지위치
										AND CARD_NO IS NULL
										ORDER BY YD_PNT_CD DESC
										)A
									group by YD_PNT_CD)AA
								where count = '2'
				    	 */
			    	loadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{	szARR_WLOC_CD,
																	    				s_STACK_YD_GP,
																	    				s_STACK_BAY_GP,
																	    				szTRN_EQP_GP});
			    	
			    	
//		    		포인트 모두 점유상태일때 상차완료된 포인트찾음					    	  
		    		trnEqpQueryId = "ym.tsinfo.getListloadEndpoint";
		    		/*
						SELECT B.STACK_COL_GP,
                               B.YD_PNT_CD
                       FROM TB_YD_CARSCH A,TB_YM_STACKCOL B
                       WHERE A.DEL_YN = 'N'
                       AND A.YD_CAR_PROG_STAT = '5'
                       AND SUBSTR(YD_CARLD_STOP_LOC,1,1) = ?
                       AND SUBSTR(YD_CARLD_STOP_LOC,2,1) = ?
                       AND SUBSTR(YD_CARLD_STOP_LOC, 3, 2) = ?
                       AND A.YD_CARLD_STOP_LOC = B.STACK_COL_GP
                       AND A.SPOS_WLOC_CD = B.WLOC_CD
			    	 */
		    		loadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STACK_YD_GP,
		    				                                                         s_STACK_BAY_GP,
																    				szTRN_EQP_GP});
		    		
		    				    		
			    		if(loadPointList.size() > 0){
					    	JDTORecord loadPointrec = (JDTORecord)loadPointList.get(0);
					    	sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
					    	sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
			    		}else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
			    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>상차완료 포인트 없음 => 포인트없음 처리");
			    			sloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
			    			sloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;
			    			
			    			//대기장 포인트 사유 가져오기
				    		sYD_MSG_CD = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD,"");
				    			
							// 발지 차량 적치단 Clear 하기
							layerState = "C";
							queryID1 = "ym.tsinfo.updateLayerstat_02_new";
							count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
							
//			    			발지 차량정보 삭제 하기 
					    	queryID1 = "ym.tsinfo.updateLayerstat_03";
					    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
					    	
					    	//차량포인트통합관리(구분, 장비번호,저장위치,상태)
						    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
						    
					    	//if(count >0 ){
					    	//layerState 	= "C";
					    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
					    	//count = dao.updateData(queryID1, new Object[]{	layerState,
					    	//												szSPOS_WLOC_CD,
					    	//												szSPOS_YD_PNT_CD,
					    	//												szTRN_EQP_GP}); 
					    	//}
			    			
			    		}
		    			
		    		
				
//		    		 야드맵 Close
//			    	queryID1 = "ym.tsinfo.updateLayerstat_01";
//			    	count = dao.updateData(queryID1, new Object[]{	"","","","",
//			    													szSPOS_WLOC_CD,
//			    													szSPOS_YD_PNT_CD,
//			    													szTRN_EQP_GP}); 
//			    	/*
//				    	UPDATE  TB_YM_STACKCOL
//						SET     YD_CAR_USE_GP   = :YD_CAR_USE_GP,
//						        TRN_EQP_CD      = :TRN_EQP_CD,
//						        CAR_NO          = :CAR_NO,
//						        CARD_NO         = :CARD_NO
//						WHERE WLOC_CD   = :WLOC_CD
//						AND   YD_PNT_CD = :YD_PNT_CD
//						AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
//					*/
			    	
					// 발지 차량 적치단 Clear 하기
					layerState = "C";
					queryID1 = "ym.tsinfo.updateLayerstat_02_new";
					count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
						
//		    		발지 차량정보 삭제 하기 
			    	queryID1 = "ym.tsinfo.updateLayerstat_03";
			    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
			    	
			    	//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
				    
					sMoveterm   = YmCommonConst.NEW_STOCK_MOVE_TERM_CS;
					
					//if(count >0 ){
					//layerState 	= "C";
			    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
			    	//count = dao.updateData(queryID1, new Object[]{	layerState,
			    	//												szSPOS_WLOC_CD,
			    	//												szSPOS_YD_PNT_CD,
			    	//												szTRN_EQP_GP}); 
					//}
			     	/*
			    	 * UPDATE TB_YM_STACKLAYER
		               SET  STOCK_ID = '',
		                    STACK_LAYER_ACTIVE_STAT = ?,
		                    STACK_LAYER_STAT = 'E' 
		               WHERE STACK_COL_GP = (SELECT A.STACK_COL_GP	    	 		
							                 FROM TB_YM_STACKCOL A
						                     WHERE  A.WLOC_CD 	= ?
						                       AND 	A.YD_PNT_CD = ?
	                                           AND SUBSTR(A.STACK_COL_GP,3,2) = :TRN_EQP_GP
							                 )
			    	 */
			    		
		    	
			    	int iSeqCount 	= FrtoProductList.size();
			    	String s_STL_APPEAR_GP = "";
			    			    		
			    	    if(iSeqCount > 0){
			    	    	
				    	     JDTORecord FrtoProductReq = (JDTORecord)FrtoProductList.get(0);
				    	     String s_STOCK  	= StringHelper.evl(FrtoProductReq.getFieldString("STOCK_ID"), "");
				    	     trnEqpQueryId 		= "ym.tsinfo.getListCoilSchGP";
				    	     FrtoProductReqList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STOCK});
						    
				    	     
				    	     
					    	 if(FrtoProductReqList.size() > 0){
							    	JDTORecord FrtoProductCoil = (JDTORecord)FrtoProductReqList.get(0);
							    	s_STL_APPEAR_GP  = StringHelper.evl(FrtoProductCoil.getFieldString("STL_APPEAR_GP"), "");
					    	 }
					    	 
					    	 if(s_STL_APPEAR_GP.equals("Y"))// 제품
					    	 {
					    		 sSchCode	= YmCommonConst.NEW_SCH_WORK_KIND_GVML ; 
					    		 sSchCode = getSchWorkKind(sSchCode, sloadStoppoint);
					    		 
					    	 }
					    	 else
					    	 {
					    		 sSchCode	= YmCommonConst.NEW_SCH_WORK_KIND_CVML ;
					    		 sSchCode = getSchWorkKind(sSchCode, sloadStoppoint);
					    	 }		    	     
			    	    }
		    	
			    	
			    			    	
			    	//상차 LOT편성
			    	if(iSeqCount > 0){
			    		
				    	//작업예약생성	
				    	wBookQueryId 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";								
						wBookSel 		= ydStackLayerDAO.requestFind(wBookQueryId);
						wBookid      	= wBookSel.getFieldString("WBOOK_ID");
				    	
				    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
				    	s_STACK_YD_GP 	= StringHelper.evl(FrtoProduct.getFieldString("STACK_COL_GP"),"").substring(0,1);
						s_STACK_BAY_GP 	= StringHelper.evl(FrtoProduct.getFieldString("STACK_COL_GP"),"").substring(1,2);
						
						//작업예약테이블에 insert
						sWBookQueryId 	= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
						 
						iSeq = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ wBookid, 
								 s_STACK_YD_GP, 
								 s_STACK_BAY_GP,
								 sSchCode, 
								 YmCommonUtil.getWorkDuty(),
								 YmCommonUtil.getWorkParty()});
			    	}

			    	/**
			    	 *  차량스펙 및 그룹핑재료정보에 따른 상차물량 결정처리
			    	 */
			    	List lCarStockList	= new ArrayList();	
			    	int iLoopIndex 		= 0;
			    	String sLoopIndex1 	= "";
			    	String sLoopIndex2 	= "";
			    	
			    	long lTotalWt	= 0;
			    	long lPerWt		= 0;
			    	long lCarMaxWt	= 0;
			    	
			    	int iLoadMax 	= 0;
			    	int iLoadCur 	= 0;
			    	{
			    		/*
			    		 * 1. 차량장비 MAX 중량정보를 가져온다.
			    		 */
			    		if(szTRN_EQP_GP.equals("TR"))
			    		{
			    			try{
			    				lCarMaxWt = Long.parseLong(szTRN_EQP_STK_CAPA);
			    			}catch(Exception e){
			    				lCarMaxWt = 70000;
			    			}
			    			iLoadMax = 3;	
			    		}
			    		else 
			    		{
			    			try{
			    				lCarMaxWt = Long.parseLong(szTRN_EQP_STK_CAPA);
			    			}catch(Exception e){
			    				lCarMaxWt = 180000;
			    			}
				    		iLoadMax = 6;	
			    		}
			    		/*
			    		 * 2. 상차Lot편성 대상재정보를 가지고 Lot를 편성한다.
			    		 */
			    		for(int index=0; index < iSeqCount ; index++){
			    			
							JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(index);
							
							sLoopIndex2	 = "";
							sLoopIndex2	+= StringHelper.evl(FrtoProduct.getFieldString("소재제품우선순위"),"");
							sLoopIndex2	+= StringHelper.evl(FrtoProduct.getFieldString("현재동우선순위"),"");
				    		sLoopIndex2	+= StringHelper.evl(FrtoProduct.getFieldString("착지개소우선순위"),"");
				    		sLoopIndex2	+= StringHelper.evl(FrtoProduct.getFieldString("목표동우선순위"),"");
				    		
				    		logger.println(LogLevel.DEBUG,this,"=재료그룹속성1 => "+ sLoopIndex1);   
				    		logger.println(LogLevel.DEBUG,this,"=재료그룹속성2 => "+ sLoopIndex2);   
				    		
				    		if(index == 0 ){
				    			sLoopIndex1 = sLoopIndex2;
				    		}else{
				    			if(sLoopIndex1.equals(sLoopIndex2)){
				    				
				    			}else{

				    				break;
				    			}
				    		}
				    		/*
				    		 * 3. 같이 편성할 수 있는 Lot편성 대상재이면 차량Max중량과 비교해서 Over하는지 체크
				    		 * 	  lCarStockList	: 상차Lot편성 대상재 
				    		 *    lTotalWt		: 차량에 실린 재료 총중량
				    		 *    lCarMaxWt		: 차량에 실을 수 있는 Max 중량
				    		 *    lPerWt		: 재료단위 중량
				    		 *    iLoadCur		: 현재 차량에 실린 재료갯수
				    		 *    iLoadMax		: 차량에 실을 수 있는 Max 갯수 
				    		 */
				    		lPerWt	= Long.parseLong(StringHelper.evl(FrtoProduct.getFieldString("STK_WT"),"0"));
				    		
				    		if(lCarMaxWt > lTotalWt + lPerWt){
				    			
				    			logger.println(LogLevel.DEBUG,this,"=상차LOT대상재 편성 => OK");   
				    			
				    			lCarStockList.add(FrtoProduct);
				    			lTotalWt += lPerWt;
				    			iLoadCur++;				
				    			if(iLoadMax <= iLoadCur){
				    				break;
				    			}
				    		}
				    	}
			    	}
			    	
			    	logger.println(LogLevel.DEBUG,this,"=iLoadCur AS-IS => "+ iLoadCur);   
			    	
			    	//2010.07.14  보안관리팀에 따른 트레일러 4매이상 상차 불가 처리
			    	if(iLoadCur >= 4 && szTRN_EQP_GP.equals("TR")){
			    		iLoadCur =3 ;
			    	}
			    	//제품 1매인 경우 2매로 상차 가능 하도록 정정
			    	if(iLoadCur == 1 && s_STL_APPEAR_GP.equals("Y")){
			    		iLoadCur =2 ;
			    	}
			    	
			    	logger.println(LogLevel.DEBUG,this,"=iLoadCur TO-BE => "+ iLoadCur);  
			    	
			    	logger.println(LogLevel.DEBUG,this,"=lCarMaxWt => "+ lCarMaxWt);   
			    	logger.println(LogLevel.DEBUG,this,"=lTotalWt => "+ lTotalWt);   
			    	
			    	for(int index=0; index < iLoadCur ; index++){
						
			    		JDTORecord FrtoProduct = (JDTORecord)lCarStockList.get(index);
						s_STOCK_ID = StringHelper.evl(FrtoProduct.getFieldString("STOCK_ID"),"");
						
						//이전 작업예약 존재 유무 체크  
						trnQueryId 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID3";
			    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{s_STOCK_ID});
			 
			    		JDTORecord TolocRecCoil = (JDTORecord)FrtostlList.get(0);
			    		 wBookid2 = StringHelper.evl(TolocRecCoil.getFieldString("WBOOK_ID"),"");
			    		
			    		if(!wBookid2.equals("")){
			    			stkQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteWbookInfo";
								iSeq = ydStockDAO.deleteData(stkQueryId, new Object[]{wBookid2});
			    		}
						
						//저장품테이블에 update				
						stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
						iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ wBookid, 
								                                                      sMoveterm,
																					  s_STOCK_ID});
						/*
		                 *	적치단  Table Update(작업요구상태='S'로 변경)
		                 */
		            	String sQuery4 = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
		            	iSeq = ydStockDAO.requestupdateData(sQuery4, 
														    new Object[]{YmCommonConst.STACK_LAYER_STAT_S, 
		            										s_STOCK_ID });
			    	}

					
			    	//차량스케쥴테이블에 insert
		    		
		    		szYD_CAR_USE_GP 	= "L";
		    		szYD_EQP_WRK_STATE 	= "U";//공차경우
	  	            trnEqpQueryId 		= "ym.tsinfo.inserttrnEqpsch";
			    	iSeq = dao.insertData(trnEqpQueryId, new Object[]{ 	szTRN_EQP_CD,
			    			                                            szYD_CAR_USE_GP,
			    			                                            szTRN_EQP_CD,
			    			                                            szYD_EQP_WRK_STATE,
			    			                                            szARR_WLOC_CD,
			    														"", //착지개소코드
			    														wBookid,
			    														sloadStoppoint}); 
			    	/*
			    	 * INSERT INTO TB_YD_CARSCH
						(
						    YD_CAR_SCH_ID
						    ,DEL_YN
						    ,YD_EQP_ID
						    ,YD_CAR_USE_GP
						    ,TRN_EQP_CD
						    ,YD_EQP_WRK_STAT
						    ,SPOS_WLOC_CD
						    ,ARR_WLOC_CD
						    ,YD_CARLD_LEV_DT
						    ,YD_CARLD_PNT_WO_DT
						    ,YD_CARLD_WRK_BOOK_ID
						    ,YD_CARLD_STOP_LOC
						    ,YD_CAR_PROG_STAT 
						)
						SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') ||  LPAD(YD_CARSCH_SEQ.nextval,6,'0') AS YD_CAR_SCH_ID
						,'N'
						,(SELECT YD_EQP_ID
						  FROM TB_YD_CARSPEC
						  WHERE TRN_EQP_CD = :TRN_EQP_CD
						  AND DEL_YN = 'N')YD_EQP_ID
						,:YD_CAR_USE_GP
						,:TRN_EQP_CD
						,:YD_EQP_WRK_STAT
						,:SPOS_WLOC_CD
						,:ARR_WLOC_CD
						,SYSDATE
						,SYSDATE
						,:YD_CARLD_WRK_BOOK_ID
						,:YD_CARLD_STOP_LOC
						,'1'
						FROM DUAL
			    	 */

			    	//차량스케쥴ID조회
		    		trnEqpQueryId = "ym.tsinfo.getListtrnEqpschL";
		    		carSchList = dao.getCommonList(trnEqpQueryId, new Object[]{szTRN_EQP_CD});
			    	
		    		if(carSchList.size() > 0){
				    	JDTORecord CarSchrec = (JDTORecord)carSchList.get(0);
				    	sCarSchCode  = StringHelper.evl(CarSchrec.getFieldString("YD_CAR_SCH_ID"), "");
		    		}
			    	/*
			    	 * SELECT YD_CARLD_WRK_BOOK_ID WBOOK_ID
			    	 *        ,YD_CAR_SCH_ID
		                FROM TB_YD_CARSCH
		               WHERE TRN_EQP_CD = ?
		               AND YD_CAR_PROG_STAT = '1'
		               AND (DEL_YN IS NULL OR DEL_YN <> 'Y')
			    	 */	
						
			    	//차량대상재 테이블에 insert	
			    	for(int index=0; index < iLoadCur ; index++){
			    		
			    		logger.println(LogLevel.DEBUG,this,"=출발실적처리=>차량스케쥴 대상재 등록 ");   
			    		
			    		JDTORecord FrtoProduct = (JDTORecord)lCarStockList.get(index);
						s_STOCK_ID = StringHelper.evl(FrtoProduct.getFieldString("STOCK_ID"),"");
						s_BED_GP = StringHelper.evl(FrtoProduct.getFieldString("STACK_BED_GP"),"");
						s_LAYER_GP = StringHelper.evl(FrtoProduct.getFieldString("STACK_LAYER_GP"),"");
						
						trnEqpQueryId = "ym.tsinfo.insertCarftmtl";
			    		iSeq = dao.insertData(trnEqpQueryId, new Object[]{ sCarSchCode,s_STOCK_ID}); 
				    	/*
				    	 *  INSERT INTO TB_YD_CARFTMVMTL
							(
							    YD_CAR_SCH_ID
							   ,STL_NO
							   ,DEL_YN						   
							)
							VALUES (
							    :YD_CAR_SCH_ID,
							    :STL_NO,
							    'N'					    
							)
				    	 */
			    	}
			    	
			     /*
	  	             * 포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
	  	          */
	  	          trnEqpQueryId 		= "ym.tsinfo.updateEquipcolStat";
	  	          iSeq = dao.updateData(trnEqpQueryId, new Object[]{ 	szYD_CAR_USE_GP,	  	            		                                            
							                                            szTRN_EQP_CD,
							                                            sloadStoppoint}); 	  	           	  	            
	  	            
	  	          //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				    CarPointinforeg("3","",szTRN_EQP_CD,sloadStoppoint,"","","R");
	  	            /*
	  	             * UPDATE TB_YM_STACKCOL
                       SET STACK_STAT = :STACK_STAT,
                           CAR_CARD_NO = :CAR_CARD_NO,
                           MODIFIER = 'TSYDJ004',
                           MOD_DDTT = sysdate
                       WHERE STACK_COL_GP = :STACK_COL_GP
	  	             */
			    	
	  	          
			    	/*
	  	             * 20090801_YJK
	  	             * 향후 포인트 예약실적 I/F 추가
	  	             */
			    	
			    	//포인트지시 모듈 독립적으로 만들어서 호출
			    	//착지개소 및 포인트코드 추후 셋팅요 
					Paramrecord = JDTORecordFactory.getInstance().create(); 
					Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
					Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
					Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
					Paramrecord.setField("YD_MSG_CD", 		sYD_MSG_CD);
					
					this.procMatlCarArrPntReq(Paramrecord);
	            
					isSuccess = true;
					
//#######################################################AB열연 코일 이송 #####################################################################						
			    }else
				{
					isSuccess = false;	
				}
		    	
			  }
//		    	포인트점유사항 출하송신
				Paramrecord = JDTORecordFactory.getInstance().create(); 
				Paramrecord.setField("WLOC_CD", 		szSPOS_WLOC_CD);//
				Paramrecord.setField("YD_PNT_CD", 	szSPOS_YD_PNT_CD);//
				Paramrecord.setField("YD_PNT_OCPY_GP", 		"A");
				Paramrecord.setField("YD_PNT_UNIT_CL_GP", 		"O");
				Paramrecord.setField("LOAN_PULLOUT_ABLE_YN", 		"Y");
				Paramrecord.setField("OCPY_TRN_EQP_CD", szTRN_EQP_CD);
				this.procMatlCarArrPntSenddm(Paramrecord);	
		    	
	    }
	    //AB열연에서 AB열연으로 이송의 경우 end
        ////================================================================================================================================================

	   
	    	
	    	
	    	
       ////================================================================================================================================================
	   //AB열연에서 일관제철로 이송의 경우
	    else if("AC".equals(sWorkGp))
	    {
	    	// 야드맵 Close
//	    	queryID1 = "ym.tsinfo.updateLayerstat_01";
//	    	count = dao.updateData(queryID1, new Object[]{	"","","","",
//	    													szSPOS_WLOC_CD,
//	    													szSPOS_YD_PNT_CD,
//	    													szTRN_EQP_GP}); 
//	    	/*
//		    	UPDATE  TB_YM_STACKCOL
//				SET     YD_CAR_USE_GP   = :YD_CAR_USE_GP,
//				        TRN_EQP_CD      = :TRN_EQP_CD,
//				        CAR_NO          = :CAR_NO,
//				        CARD_NO         = :CARD_NO
//				WHERE WLOC_CD   = :WLOC_CD
//				AND   YD_PNT_CD = :YD_PNT_CD
//				AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
//			*/
	    	
			// 발지 차량 적치단 Clear 하기
			layerState = "C";
			queryID1 = "ym.tsinfo.updateLayerstat_02_new";
			count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
			
//	    	발지 차량정보 삭제 하기 
	    	queryID1 = "ym.tsinfo.updateLayerstat_03";
	    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
	    	
	    	logger.println(LogLevel.DEBUG,this,"=출발실적처리=>영차처리 시작:"+count);
	    	
	    	//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
		    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
		
	    	//if(count > 0 ){
			//layerState 	= "C";
	    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
	    	//count = dao.updateData(queryID1, new Object[]{	layerState,
	    	//												szSPOS_WLOC_CD,
	    	//												szSPOS_YD_PNT_CD,
	    	//												szTRN_EQP_GP}); 
	    	/*
	    	 * UPDATE TB_YM_STACKLAYER
               SET  STOCK_ID = '',
                    STACK_LAYER_ACTIVE_STAT = ?,
                    STACK_LAYER_STAT = 'E' 
               WHERE STACK_COL_GP = (SELECT A.STACK_COL_GP	    	 		
					                 FROM TB_YM_STACKCOL A
				                     WHERE  A.WLOC_CD 	= ?
				                       AND 	A.YD_PNT_CD = ?
                                       AND SUBSTR(A.STACK_COL_GP,3,2) = :TRN_EQP_GP
					                     )
	    	 */
	    	//}
	
			logger.println(LogLevel.DEBUG,this,"=출발실적처리=>영차처리 종료"); 
	    			
		  isSuccess = true;
		  
//	    	포인트점유사항 출하송신
			Paramrecord = JDTORecordFactory.getInstance().create(); 
			Paramrecord.setField("WLOC_CD", 		szSPOS_WLOC_CD);//
			Paramrecord.setField("YD_PNT_CD", 	szSPOS_YD_PNT_CD);//
			Paramrecord.setField("YD_PNT_OCPY_GP", 		"A");
			Paramrecord.setField("YD_PNT_UNIT_CL_GP", 		"O");
			Paramrecord.setField("LOAN_PULLOUT_ABLE_YN", 		"Y");
			Paramrecord.setField("OCPY_TRN_EQP_CD", szTRN_EQP_CD);
			this.procMatlCarArrPntSenddm(Paramrecord);
		  
	   }	    	
		//AB열연에서 일관제철으로 이송의 경우 end
	    ////================================================================================================================================================

		    
	    	
		    	
	    	
	    ////================================================================================================================================================
       //일관제철에서 AB열연으로 이송의 경우	    	    	
	    else if("CA".equals(sWorkGp))
	    {
	    	
	    	if(szTRN_WRK_FULLVOID_GP.equals("F"))
			{	    		
	    		logger.println(LogLevel.DEBUG,this,"=출발실적처리=>영차처리 시작CA");
	    			    		
	    		
				/*
				 * 현재는 권하완료 시점에 VL로 셋팅한다.
				 * 여기서 추가셋팅을 할것인지는 추후 검토한다(C연주 이송재는 이동조건 여기서 셋팅해야 함.)
				 */
				sMoveterm   = YmCommonConst.NEW_STOCK_MOVE_TERM_CS;	    	
				 
				
		    	//슬라브이송
                if(szARR_WLOC_CD.equals("D2Y43")||szARR_WLOC_CD.equals("D3Y43")||szARR_WLOC_CD.equals("D3Y44"))
		    	{
                	if(szTRN_EQP_GP.equals("PT"))
                	{
                	
                		
              		
                		sSchCode 	= YmCommonConst.NEW_SCH_WORK_KIND_SVMU;
     	    		   //차량스케쥴ID로 이송재료 조회
     	    		   trnQueryId 	= "ym.tsinfo.getListFrtostlList_1";
     	    		   FrtostlList = dao.getCommonList(trnQueryId, new Object[]{szTRN_EQP_CD});
     	    		   /*
     	    		    * SELECT YD_CAR_SCH_ID
                           FROM TB_YD_CARSCH B
                           WHERE A.TRN_EQP_CD = ?
                           AND A.DEL_YN = 'N'
     	    		    */ 
     	    		   JDTORecord TolocRec = (JDTORecord)FrtostlList.get(0);
     	    		   s_CAR_SCH_ID 			= StringHelper.evl(TolocRec.getFieldString("YD_CAR_SCH_ID"),"");
     	    		
			    		if(szARR_WLOC_CD.equals("D3Y43")){
			    			s_STACK_YD_GP 	= "2";	//b열연		   
			    		}else {
			    			s_STACK_YD_GP 	= "0";  //b-cast
			    		}
			    		
     	    		   s_STACK_BAY_GP = getSlabAimCd(s_CAR_SCH_ID,szSPOS_WLOC_CD);
     	    		   logger.println(LogLevel.DEBUG,this,"=B열연 이송재 목적동 조회" + s_STACK_BAY_GP);
                
     					logger.println(LogLevel.DEBUG,this,"=출발실적처리=>하차정지위치검색");
     	                //하차정지위치 검색(등록할것)
     			    	//추가부분 : 검색대상재 동에 있는 차량위치정보  
      		    		
     		    		if(szARR_WLOC_CD.equals("D3Y43")){
    			    		trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
    			    		unloadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szARR_WLOC_CD,
    																	    				s_STACK_YD_GP,
    																	    				s_STACK_BAY_GP,
    																	    				szTRN_EQP_GP});
    			    		
//    			    		포인트 모두 점유상태일때 하차완료된 포인트찾음					    	  
    			    		trnEqpQueryId = "ym.tsinfo.getListUnloadEndpoint_slab";
    			    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STACK_YD_GP,
    			    				                                                        s_STACK_BAY_GP,
    																	    				szTRN_EQP_GP,
    																	    				szARR_WLOC_CD});
			    		
			    		
						}else{
    			    		trnEqpQueryId = "ym.tsinfo.getListloadStoppointBCAST";
    			    		unloadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szARR_WLOC_CD,
    																	    				s_STACK_YD_GP,
    																	    				szTRN_EQP_GP});
    			    		
//    			    		포인트 모두 점유상태일때 하차완료된 포인트찾음					    	  
    			    		trnEqpQueryId = "ym.tsinfo.getListUnloadEndpoint_slab";
    			    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STACK_YD_GP,
    			    				                                                        "%",
    																	    				szTRN_EQP_GP,
    																	    				szARR_WLOC_CD});
							
						}
     		    		
     		    		
     		    		
     		    			
     		    		if(unloadPointList.size() > 0){
     		    			logger.println(LogLevel.DEBUG,this,"=빈 포인트 조회");
     				    	JDTORecord unloadPointrec = (JDTORecord)unloadPointList.get(0);
     				    	sunloadStoppoint  = StringHelper.evl(unloadPointrec.getFieldString("STACK_COL_GP"), "");
     				    	sunloadStopTsCd   = StringHelper.evl(unloadPointrec.getFieldString("YD_PNT_CD"), "");
     		    		}
     		    	/*	else if(unloadEndPointListEbayList.size()>0)
     		    		{
     		    			logger.println(LogLevel.DEBUG,this,"= E동포인트 검색");
     		    		     JDTORecord unloadPointBSlabDiffrec = (JDTORecord)unloadEndPointListEbayList.get(0);
     		    			 sunloadStoppoint  = StringHelper.evl(unloadPointBSlabDiffrec.getFieldString("STACK_COL_GP"), "");
     					     sunloadStopTsCd   = StringHelper.evl(unloadPointBSlabDiffrec.getFieldString("YD_PNT_CD"), "");
     					     s_STACK_BAY_GP = "E";			    							    				            						    	    			
     		    		}	*/	    		
     		    		 else if(unloadEndPointList.size() > 0){
     			    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>하차완료 포인트 검색");
     			    			
     			    			int unloadEndPointListSize = unloadEndPointList.size();    					    	

      			    			for(int i=0; i < unloadEndPointListSize ; i++){
      			    			
      					    	    JDTORecord unloadPointrec = (JDTORecord)unloadEndPointList.get(i);
      					    	
      					    	    String QueryId 	= "ym.tsinfo.getListCarSpec";
      				    		    List CarSpecList = dao.getCommonList(QueryId, new Object[]{szTRN_EQP_CD});
      				    		    
      				    		    logger.println(LogLevel.DEBUG,this,"=인출차입 가능여부 조회");
        				    		JDTORecord CarSpecRec = (JDTORecord)CarSpecList.get(0);
        				    		String inCarspec= StringHelper.evl(CarSpecRec.getFieldString("CAR_NO"),""); 				    		
        					    	String outCarspec   = StringHelper.evl(unloadPointrec.getFieldString("CAR_NO"), "");
        					    	logger.println(LogLevel.DEBUG,this,"=인출할 pallet의 그룹"+outCarspec);
        					    	logger.println(LogLevel.DEBUG,this,"=차입할 pallet의 그룹"+inCarspec);
        					    	
        					    	if (inCarspec.equals(outCarspec)){
          					    		logger.println(LogLevel.DEBUG,this,"=인출차입 가능한 pallet 그룹임");
          						    	sunloadStoppoint  = StringHelper.evl(unloadPointrec.getFieldString("STACK_COL_GP"), "");
          						    	sunloadStopTsCd   = StringHelper.evl(unloadPointrec.getFieldString("YD_PNT_CD"), "");	
          						    	EndPointCount++;
          						    	break;
          					    	}
      			    			}
      			    			if(EndPointCount == 0)
         			    		 {
//          					   포인트없으면 포인트 없음으로 지시주고 하위처리 안함
     					    		logger.println(LogLevel.DEBUG,this,"=인출차입 불가능한 pallet 그룹임");
     				    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>하차완료 포인트 없음 => 포인트없음 처리");
     				    			sunloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
     				    			sunloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;
     				    			
     				    			//대기장 포인트 사유 가져오기
      				    			sYD_MSG_CD = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD,"");
      				    			
     				    			Paramrecord = JDTORecordFactory.getInstance().create(); 
     								Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
     								Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
     								Paramrecord.setField("YD_PNT_CD", 		sunloadStopTsCd);
     								
     								// 발지 차량 적치단 Clear 하기
     								layerState = "C";
     								queryID1 = "ym.tsinfo.updateLayerstat_02_new";
     								count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
     								
//     								발지 차량정보 삭제 하기 
     						    	queryID1 = "ym.tsinfo.updateLayerstat_03";
     						    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
     						    	
     						    	//차량포인트통합관리(구분, 장비번호,저장위치,상태)
     							    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
     							    
     						    	//if(count >0 ){
     						    	//layerState 	= "C";
     						    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
     						    	//count = dao.updateData(queryID1, new Object[]{	layerState,
     						    	//												szSPOS_WLOC_CD,
     						    	//												szSPOS_YD_PNT_CD,
     						    	//												szTRN_EQP_GP}); 
     						    	//}
         			    		 }
     					   }else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
     		    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>하차완료 포인트 없음 => 포인트없음 처리");
     		    			sunloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
     		    			sunloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;
     		    			
     		    			//대기장 포인트 사유 가져오기
				    		sYD_MSG_CD = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD,"");
				    			
     		    			Paramrecord = JDTORecordFactory.getInstance().create(); 
     						Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
     						Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
     						Paramrecord.setField("YD_PNT_CD", 		sunloadStopTsCd);
     						
    						// 발지 차량 적치단 Clear 하기
    						layerState = "C";
    						queryID1 = "ym.tsinfo.updateLayerstat_02_new";
    						count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
    						
//     						발지 차량정보 삭제 하기 
     				    	queryID1 = "ym.tsinfo.updateLayerstat_03";
     				    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
     				    	
     				    	//차량포인트통합관리(구분, 장비번호,저장위치,상태)
     					    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
     					    
     				    	//if(count >0 ){
     				    	//layerState 	= "C";
     				    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
     				    	//count = dao.updateData(queryID1, new Object[]{	layerState,
     				    	//												szSPOS_WLOC_CD,
     				    	//												szSPOS_YD_PNT_CD,
     				    	//												szTRN_EQP_GP});     
     				    	//}
     		    			
     		    		}
                 }else
                 {
                	 sSchCode 	= YmCommonConst.NEW_SCH_WORK_KIND_SVMU;
   	    		   //차량스케쥴ID로 이송재료 조회
   	    		   trnQueryId 	= "ym.tsinfo.getListFrtostlList_1";
   	    		   FrtostlList = dao.getCommonList(trnQueryId, new Object[]{szTRN_EQP_CD});
   	    		   /*
   	    		    * SELECT YD_CAR_SCH_ID
                         FROM TB_YD_CARSCH B
                         WHERE A.TRN_EQP_CD = ?
                         AND A.DEL_YN = 'N'
   	    		    */ 
   	    		   JDTORecord TolocRec = (JDTORecord)FrtostlList.get(0);
   	    		   s_CAR_SCH_ID 			= StringHelper.evl(TolocRec.getFieldString("YD_CAR_SCH_ID"),"");
   	    		
		    		if(szARR_WLOC_CD.equals("D3Y43")){
		    			s_STACK_YD_GP 	= "2";	//b열연		   
		    		}else {
		    			s_STACK_YD_GP 	= "0";  //b-cast
		    		}
		    		
   	    		   s_STACK_BAY_GP = getSlabAimCd(s_CAR_SCH_ID,szSPOS_WLOC_CD);
   	    		   logger.println(LogLevel.DEBUG,this,"=B열연 이송재 목적동 조회" + s_STACK_BAY_GP);
              
   					logger.println(LogLevel.DEBUG,this,"=출발실적처리=>하차정지위치검색");
   	                //하차정지위치 검색(등록할것)
   			    	//추가부분 : 검색대상재 동에 있는 차량위치정보  
   		    		
 		    		if(szARR_WLOC_CD.equals("D3Y43")){
			    		trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
			    		unloadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szARR_WLOC_CD,
																	    				s_STACK_YD_GP,
																	    				s_STACK_BAY_GP,
																	    				szTRN_EQP_GP});
			    		
//			    		포인트 모두 점유상태일때 하차완료된 포인트찾음					    	  
			    		trnEqpQueryId = "ym.tsinfo.getListUnloadEndpoint_slab";
			    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STACK_YD_GP,
			    				                                                        s_STACK_BAY_GP,
																	    				szTRN_EQP_GP,
																	    				szARR_WLOC_CD});
		    		
		    		
					}else{
			    		trnEqpQueryId = "ym.tsinfo.getListloadStoppointBCAST";
			    		unloadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szARR_WLOC_CD,
																	    				s_STACK_YD_GP,
																	    				szTRN_EQP_GP});
			    		
//			    		포인트 모두 점유상태일때 하차완료된 포인트찾음					    	  
			    		trnEqpQueryId = "ym.tsinfo.getListUnloadEndpoint_slab";
			    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STACK_YD_GP,
			    				                                                        "%",
																	    				szTRN_EQP_GP,
																	    				szARR_WLOC_CD});
						
					}
   		    		
//   		    		trnEqpQueryId = "ym.tsinfo.getListunloadPointBSlabDiffpoint_slab";
//   		    		  /*	
//     				    Select 
//     			        STACK_COL_GP,
//     			        WLOC_CD,
//     			        YD_PNT_CD
//     			        From Tb_Ym_StackCol
//     			        Where Stack_Col_Gp like '2EPT%'
//     			        And   CAR_CARD_NO Is Null
//     			        And   TRN_EQP_CD IS Null
//     			        And   RowNum = 1*/ 	
//   		    		unloadEndPointListEbayList = dao.getCommonList(trnEqpQueryId);
   		    		
   		    		
   		    		
   		    			
   		    		if(unloadPointList.size() > 0){
   		    			logger.println(LogLevel.DEBUG,this,"=빈 포인트 조회");
   				    	JDTORecord unloadPointrec = (JDTORecord)unloadPointList.get(0);
   				    	sunloadStoppoint  = StringHelper.evl(unloadPointrec.getFieldString("STACK_COL_GP"), "");
   				    	sunloadStopTsCd   = StringHelper.evl(unloadPointrec.getFieldString("YD_PNT_CD"), "");
   		    		}
   		    	/*	else if(unloadEndPointListEbayList.size()>0)
   		    		{
   		    			logger.println(LogLevel.DEBUG,this,"= E동포인트 검색");
   		    		     JDTORecord unloadPointBSlabDiffrec = (JDTORecord)unloadEndPointListEbayList.get(0);
   		    			 sunloadStoppoint  = StringHelper.evl(unloadPointBSlabDiffrec.getFieldString("STACK_COL_GP"), "");
   					     sunloadStopTsCd   = StringHelper.evl(unloadPointBSlabDiffrec.getFieldString("YD_PNT_CD"), "");
   					     s_STACK_BAY_GP = "E";			    							    				            						    	    			
   		    		}	*/	    		
   		    		 else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
   		    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>하차완료 포인트 없음 => 포인트없음 처리");
   		    			sunloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
   		    			sunloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;
   		    			
   		    			//대기장 포인트 사유 가져오기
			    		sYD_MSG_CD = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD,"");
			    			
   		    			Paramrecord = JDTORecordFactory.getInstance().create(); 
   						Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
   						Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
   						Paramrecord.setField("YD_PNT_CD", 		sunloadStopTsCd);
   						
						// 발지 차량 적치단 Clear 하기
						layerState = "C";
						queryID1 = "ym.tsinfo.updateLayerstat_02_new";
						count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
						
//   						발지 차량정보 삭제 하기 
   				    	queryID1 = "ym.tsinfo.updateLayerstat_03";
   				    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
   				    	
   				    	//차량포인트통합관리(구분, 장비번호,저장위치,상태)
   					    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
   					    
   				    	//if(count >0 ){
   				    	//layerState 	= "C";
   				    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
   				    	//count = dao.updateData(queryID1, new Object[]{	layerState,
   				    	//												szSPOS_WLOC_CD,
   				    	//												szSPOS_YD_PNT_CD,
   				    	//												szTRN_EQP_GP});   
   				    	//}
   		    		}        	 
                 }
 									
		    	}
                
               //코일이송
		    	else if(szARR_WLOC_CD.equals("D2Y44")||
		    			szARR_WLOC_CD.equals("D2Y45")||
		    			szARR_WLOC_CD.equals("D3Y41")||
		    			szARR_WLOC_CD.equals("D3Y42"))
		    	{
		    		//소재이면 재료공통에 계획공정 및 차공정(우선)으로 야드 동 정보가져오고 
		    		//제품이면 MTP테이블에서 하차개소코드,하차포인트코드 가지고 와서 동정보 알아옴)
		    		trnQueryId 	= "ym.tsinfo.getListFrtostlList_Coil";
		    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{szTRN_EQP_CD});
		    		
		    		JDTORecord TolocRec 	= (JDTORecord)FrtostlList.get(0);	
		    		String s_STL_APPEAR_GP 	= StringHelper.evl(TolocRec.getFieldString("STL_APPEAR_GP"),"");
		    		
		    		if(s_STL_APPEAR_GP.equals("Y"))// 제품
		    		{
		    									
		    			//sSchCode 				= YmCommonConst.NEW_SCH_WORK_KIND_GVMU;
		    			String unloadStopwloc 	= StringHelper.evl(TolocRec.getFieldString("ARR_WLOC_CD"),"");
		    			String unloadStoppoint 	= StringHelper.evl(TolocRec.getFieldString("ARR_YD_PNT_CD"),"");
		    			
		    			trnQueryId 		= "ym.tsinfo.getListAimspace";
			    		AimSpaceList 	= dao.getCommonList(trnQueryId, new Object[]{unloadStopwloc,unloadStoppoint});
			    		JDTORecord AimSpaceRec = (JDTORecord)AimSpaceList.get(0);
			    		
			    		s_STACK_YD_GP 	= StringHelper.evl(AimSpaceRec.getFieldString("YD_GP"),"");
						s_STACK_BAY_GP 	= StringHelper.evl(AimSpaceRec.getFieldString("BAY_GP"),"");
						
			    		logger.println(LogLevel.DEBUG,this,"=출발실적처리=>하차정지위치검색");
		                //하차정지위치 검색
				    	//추가부분 : 검색대상재 동에 있는 차량위치정보  
			    		trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
			    		/*
				    	 	SELECT  STACK_COL_GP,
									YD_PNT_CD
							FROM TB_YM_STACKCOL 
							WHERE WLOC_CD     = :WLOC_CD
							AND YD_GP         = :YD_GP
							AND BAY_GP        = :BAY_GP
							AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
							AND TRN_EQP_CD  IS NULL	-- 현재차량정지위치
							AND CAR_CARD_NO IS NULL	-- 예약차량정지위치
				    	 */
			    		unloadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szARR_WLOC_CD,
																	    				s_STACK_YD_GP,
																	    				s_STACK_BAY_GP,
																	    				szTRN_EQP_GP});
			    		
//			    		포인트 모두 점유상태일때 하차완료된 포인트찾음					    	  
			    		trnEqpQueryId = "ym.tsinfo.getListUnloadEndpoint_coil";
			    		/*
							SELECT  B.STACK_COL_GP,
		                            B.YD_PNT_CD
	                        FROM  TB_YM_STACKLAYER A,TB_YM_STACKCOL B
	                        WHERE SUBSTR(A.STACK_COL_GP,1,1) = ?       
	                        AND SUBSTR(A.STACK_COL_GP,2,1) = ?
	                        AND SUBSTR(A.STACK_COL_GP,3,2) = ?
	                        AND A.STACK_BED_GP = '01'
	                        AND A.STOCK_ID IS NULL
	                        AND A.STACK_LAYER_STAT = 'E'
	                        AND A.STACK_COL_GP = B.STACK_COL_GP
	                        AND B.WLOC_CD = ?
				    	 */
			    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STACK_YD_GP,
			    				                                                        s_STACK_BAY_GP,
																	    				szTRN_EQP_GP,
																	    				szARR_WLOC_CD});
			    			
			    		if(unloadPointList.size() > 0){
					    	JDTORecord unloadPointrec = (JDTORecord)unloadPointList.get(0);
					    	sunloadStoppoint  = StringHelper.evl(unloadPointrec.getFieldString("STACK_COL_GP"), "");
					    	sunloadStopTsCd   = StringHelper.evl(unloadPointrec.getFieldString("YD_PNT_CD"), "");
			    			item = YmCommonConst.ITEM_CG;
			    			pos  = sunloadStoppoint;			    			
			    			sSchCode = getUnloadSchKind(item,pos);
			    		}else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
			    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>하차완료 포인트 없음 => 포인트없음 처리");
			    			sunloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
			    			sunloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;	
			    			sSchCode = "0000";
			    			
			    			//대기장 포인트 사유 가져오기
				    		sYD_MSG_CD = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD,"");
				    			
			    			
							// 발지 차량 적치단 Clear 하기
							layerState = "C";
							queryID1 = "ym.tsinfo.updateLayerstat_02_new";
							count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
							
//			    			발지 차량정보 삭제 하기 
					    	queryID1 = "ym.tsinfo.updateLayerstat_03";
					    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
					    	
					    	//차량포인트통합관리(구분, 장비번호,저장위치,상태)
						    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
						    
					    	//if(count >0 ){
					    	//layerState 	= "C";
					    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
					    	//count = dao.updateData(queryID1, new Object[]{	layerState,
					    	//												szSPOS_WLOC_CD,
					    	//												szSPOS_YD_PNT_CD,
					    	//												szTRN_EQP_GP}); 
					    	//}
			    			
			    		}
						

											
		    			
		    		}
		    		else//소재
		    		{
		    			sSchCode 			= YmCommonConst.NEW_SCH_WORK_KIND_CVMU;

		    			
			    		trnQueryId 	= "ym.tsinfo.getListFrtostlList_1";
			    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{szTRN_EQP_CD});
			    		/*
			    		 * SELECT YD_CAR_SCH_ID
                           FROM TB_YD_CARSCH
                           WHERE TRN_EQP_CD = ?
                           AND DEL_YN = 'N'
			    		 */ 
			    		JDTORecord TolocRecCoil = (JDTORecord)FrtostlList.get(0);
			    		s_CAR_SCH_ID = StringHelper.evl(TolocRecCoil.getFieldString("YD_CAR_SCH_ID"),"");
			    		
			    		if(szARR_WLOC_CD.equals("D3Y41")||szARR_WLOC_CD.equals("D3Y42")) 
				    	{
			    			s_STACK_YD_GP 	= "3";
			    			s_STACK_BAY_GP = getBCoilAimCd(s_CAR_SCH_ID);
				    	}
			    		else if(szARR_WLOC_CD.equals("D2Y44")||szARR_WLOC_CD.equals("D2Y45"))
			    		{
			    			s_STACK_YD_GP 	= "1";	
					    	s_STACK_BAY_GP = getACoilAimCd(s_CAR_SCH_ID);
			    		}
		    			
			    		logger.println(LogLevel.DEBUG,this,"=출발실적처리=>하차정지위치검색");
		                //하차정지위치 검색(등록할것)
				    	//추가부분 : 검색대상재 동에 있는 차량위치정보  
			    		trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
			    		/*
				    	 	SELECT  STACK_COL_GP,
									YD_PNT_CD
							FROM TB_YM_STACKCOL 
							WHERE WLOC_CD     = :WLOC_CD
							AND YD_GP         = :YD_GP
							AND BAY_GP        = :BAY_GP
							AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
							AND TRN_EQP_CD  IS NULL	-- 현재차량정지위치
							AND CAR_CARD_NO IS NULL	-- 예약차량정지위치
				    	 */
			    		unloadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{	szARR_WLOC_CD,
																	    				s_STACK_YD_GP,
																	    				s_STACK_BAY_GP,
																	    				szTRN_EQP_GP});
			    		
//			    		포인트 모두 점유상태일때 하차완료된 포인트찾음					    	  
			    		trnEqpQueryId = "ym.tsinfo.getListUnloadEndpoint_coil";
			    		/*
							SELECT  B.STACK_COL_GP,
		                            B.YD_PNT_CD
	                        FROM  TB_YM_STACKLAYER A,TB_YM_STACKCOL B
	                        WHERE SUBSTR(A.STACK_COL_GP,1,1) = ?       
	                        AND SUBSTR(A.STACK_COL_GP,2,1) = ?
	                        AND SUBSTR(A.STACK_COL_GP,3,2) = ?
	                        AND A.STACK_BED_GP = '01'
	                        AND A.STOCK_ID IS NULL
	                        AND A.STACK_LAYER_STAT = 'E'
	                        AND A.STACK_COL_GP = B.STACK_COL_GP
	                        AND B.WLOC_CD = ?
				    	 */
			    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STACK_YD_GP,
			    				                                                        s_STACK_BAY_GP,
																	    				szTRN_EQP_GP,
																	    				szARR_WLOC_CD});
			    			
			    		if(unloadPointList.size() > 0){
					    	JDTORecord unloadPointrec = (JDTORecord)unloadPointList.get(0);
					    	sunloadStoppoint  = StringHelper.evl(unloadPointrec.getFieldString("STACK_COL_GP"), "");
					    	sunloadStopTsCd   = StringHelper.evl(unloadPointrec.getFieldString("YD_PNT_CD"), "");					    	
			    			item = YmCommonConst.ITEM_CM;
			    			pos  = sunloadStoppoint;			    			
			    			sSchCode = getUnloadSchKind(item,pos);
			    		}else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
			    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>하차완료 포인트 없음 => 포인트없음 처리");
			    			sunloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
			    			sunloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;
			    			sSchCode = "0000";
			    			
			    			//대기장 포인트 사유 가져오기
				    		sYD_MSG_CD = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD,"");
				    			
							// 발지 차량 적치단 Clear 하기
							layerState = "C";
							queryID1 = "ym.tsinfo.updateLayerstat_02_new";
							count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
							
			    			queryID1 = "ym.tsinfo.updateLayerstat_03";
					    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
					    	
					    	//차량포인트통합관리(구분, 장비번호,저장위치,상태)
						    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
						    
					    	//if(count >0 ){
			    			//layerState 	= "C";
					    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
					    	//count = dao.updateData(queryID1, new Object[]{	layerState,
					    	//												szSPOS_WLOC_CD,
					    	//												szSPOS_YD_PNT_CD,
					    	//												szTRN_EQP_GP}); 
					    	//}
			    			  	    			
			    		}
			    	
		    			
		    		}

		    	}
                
    	
	    		//차량스케쥴ID로 이송재료 조회(등록할것)
	    		trnQueryId 		= "ym.tsinfo.getListFrtostlList";
	    		FrtostlList1 	= dao.getCommonList(trnQueryId, new Object[]{szTRN_EQP_CD});
	    		/*
	    		 * SELECT STL_NO,SUBSTR(YD_STK_LYR_NO,2,2)STK_LYR
                   FROM TB_YD_CARFTMVMTL A, TB_YD_CARSCH B
                   WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
                   AND A.DEL_YN = 'N'
                   AND B.TRN_EQP_CD = ?
                   ORDER BY A.YD_STK_LYR_NO 
	    		 */
	    		
				//작업예약생성	
		    	wBookQueryId 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";								
				wBookSel 		= ydStackLayerDAO.requestFind(wBookQueryId);
				wBookid      	= wBookSel.getFieldString("WBOOK_ID");
		    		
				//작업예약테이블에 insert
				sWBookQueryId 	= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
				
				iSeq = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ wBookid, 
																				 s_STACK_YD_GP, 
																				 s_STACK_BAY_GP,
																				 sSchCode, 
																				 YmCommonUtil.getWorkDuty(),
																				 YmCommonUtil.getWorkParty()});
				
				
				int iSeqCount 	= FrtostlList1.size();
				String s_C3_SCARF_TRF_YN ="";
                for(int i=0; i < iSeqCount ; i++){
					
		    		JDTORecord FrtoSltrec = (JDTORecord)FrtostlList1.get(i);
					s_STOCK_ID = StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"),"");
					s_C3_SCARF_TRF_YN= StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"),"");
					
					//항만스카핑 핸드구분 지정 시
					if("Y".equals(s_C3_SCARF_TRF_YN)){
						sMoveterm= YmCommonConst.NEW_STOCK_MOVE_TERM_D3;	   //chito 2016.08.30 
					}

					//이전 작업예약 존재 유무 체크  
					trnQueryId 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID3";
		    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{s_STOCK_ID});
		 
		    		if(FrtostlList.size() > 0){
			    		JDTORecord TolocRecCoil = (JDTORecord)FrtostlList.get(0);
			    		wBookid2 = StringHelper.evl(TolocRecCoil.getFieldString("WBOOK_ID"),"");
		    		}else{
		    			//저장품 등록작업 2014.04.10
		    			sWBookQueryId 	= "ym.tsinfo.insertstock";						
						iSeq = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ s_STOCK_ID});
						
						logger.println(LogLevel.DEBUG,this,"=저장품 신규 등록 작업:"+s_STOCK_ID);
		    		}
		    		
		    		if(!wBookid2.equals("")){
		    			stkQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteWbookInfo";
							iSeq = ydStockDAO.deleteData(stkQueryId, new Object[]{wBookid2});
		    		}
		    		
		    		
		    		//--------------------------------------------------------------------------//		     
					/*
		        	 * 3. 저장품 테이블에 실주편번호로 저장품이 있는지 체크
		        	 */		    	
		    		trnQueryId 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStockInfoWcrGp";
		    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{s_STOCK_ID});
	 
					if(FrtostlList.size() <= 0){
 
						createSlabInfo2( "",  sMoveterm, s_STOCK_ID);
						//L2전문송신 
						sendSlabInfo2(dao.readSlabInfo(s_STOCK_ID), YmCommonConst.FORM_I);
					}
					//--------------------------------------------------------------------------//
		    		 
					
					//저장품테이블에 update				
					stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
					iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ wBookid, 
							                                                      sMoveterm,
																				  s_STOCK_ID});
					/*
	                 *	적치단  Table Update(작업요구상태='S'로 변경)
	                 */
	            	String sQuery4 = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
	            	iSeq = ydStockDAO.requestupdateData(sQuery4, 
													    new Object[]{YmCommonConst.STACK_LAYER_STAT_S, 
	            										s_STOCK_ID });
		    	}
				

                
               //차량스케쥴 업데이트(등록할것) 
                szYD_CAR_USE_GP 	= "L";
	    		szYD_EQP_WRK_STATE 	= "L";//공차경우
  	           // trnEqpQueryId 		= "ym.tsinfo.updatetrnEqpsch";
  	           
  	          
//  	            iSeq = dao.updateData(trnEqpQueryId, new Object[]{ 	szYD_CAR_USE_GP,
//  	            		                                            szYD_EQP_WRK_STATE,                                                                 
//  	            		                                            szARR_WLOC_CD,                                      
//  	            		                                            wBookid,
//						                                            sunloadStoppoint,
//						                                            szTRN_EQP_CD}); 
  	            
  	          	trnEqpQueryId 		= "ym.tsinfo.updatetrnEqpschNEW";
	  	        iSeq = dao.updateData(trnEqpQueryId, new Object[]{ 	szYD_CAR_USE_GP,
											                      szYD_EQP_WRK_STATE,                                                                 
											                      szARR_WLOC_CD,                                      
											                      wBookid,
											                      sunloadStoppoint,
											                      s_STACK_BAY_GP,    //차량스케쥴에 야드 목적동을 등록 한다.
											                      szTRN_EQP_CD}); 
  	          
  
  	            
  	          /*
  	             * 포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
  	          */
  	          trnEqpQueryId 		= "ym.tsinfo.updateEquipcolStat";
  	          iSeq = dao.updateData(trnEqpQueryId, new Object[]{ 	szYD_CAR_USE_GP,	  	            		                                            
						                                            szTRN_EQP_CD,
						                                            sunloadStoppoint}); 	  	           	  	            
  	          
  	          //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			  CarPointinforeg("3","",szTRN_EQP_CD,sunloadStoppoint,"","","R");
			  
  	            /*
  	             * UPDATE TB_YM_STACKCOL
                   SET STACK_STAT = :STACK_STAT,
                       CAR_CARD_NO = :CAR_CARD_NO,
                       MODIFIER = 'TSYDJ004',
                       MOD_DDTT = sysdate
                   WHERE STACK_COL_GP = :STACK_COL_GP
  	             */
  	          
  	            
  	            /*
  	             * 20090801_YJK
  	             * 향후 포인트 예약실적 I/F 추가
  	             */
  	            
                // 포인트지시 모듈  호출
		    	//착지개소 및 포인트코드 추후 셋팅요 
				Paramrecord = JDTORecordFactory.getInstance().create(); 
				Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
				Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
				Paramrecord.setField("YD_PNT_CD", 		sunloadStopTsCd);
				Paramrecord.setField("YD_MSG_CD", 		sYD_MSG_CD);
				
				this.procMatlCarArrPntReq(Paramrecord);
             
				isSuccess = true;
		
				logger.println(LogLevel.DEBUG,this,"=출발실적처리=>영차처리 종료"); 
			}
	    	 
	    	
	    	//##############################################################################################################################################
			//공차인경우
			else if(szTRN_WRK_FULLVOID_GP.equals("E"))
			{
				
					
				/**
	    		 *  기존 출발정보 여부 확인 
	    		 */		
				//   logger.println(LogLevel.DEBUG,this,"=기존 출발정보 여부 확인 ");
	    			/*
	    			 * select YD_CAR_SCH_ID,YD_CARLD_WRK_BOOK_ID
                       from TB_YD_CARSCH
                       where TRN_EQP_CD = ?
                       and DEL_YN = 'N'
                       and YD_CAR_PROG_STAT  = '1'
	    			 */
	    				    			
	    		//    String QueryId 	= "ym.tsinfo.getListSposYNchk_E";
	    		//    List sposChklist = dao.getCommonList(QueryId, new Object[]{szTRN_EQP_CD});
	    		//    if(sposChklist.size() > 0)
	    		//    { 	
	    		///    	logger.println(LogLevel.DEBUG,this,"=이미 공차출발정보가 있음 ");
	    		    	
	    		 //   	JDTORecord sposChkReq = (JDTORecord)sposChklist.get(0);
				 //   	String s_CARLD_WRK_BOOK_ID	= StringHelper.evl(sposChkReq.getFieldString("YD_CARLD_WRK_BOOK_ID"), "");
				 //   	String s_YD_CAR_SCH_ID	= StringHelper.evl(sposChkReq.getFieldString("YD_CAR_SCH_ID"), "");
				    	
				 //   	logger.println(LogLevel.DEBUG,this,"=작업예약 ID삭제");
				 //   	dao.removeWBook(s_CARLD_WRK_BOOK_ID);
				    	
				    	//예약정보 삭제
				 //   	QueryId = "ym.steelinfo.steelinforecv.YdStockDAO.delStkBookDtl";
				//		iSeq = ydStockDAO.requestupdateData(QueryId, new Object[]{szTRN_EQP_CD});
			    		
   		    	
//	    		    	 이송재료 조회(등록할것)
			    //		trnQueryId 		= "ym.tsinfo.getListFrtostlList";
			    //		FrtostlList1 	= dao.getCommonList(trnQueryId, new Object[]{szTRN_EQP_CD});
			    		/*
			    		 * SELECT STL_NO,SUBSTR(YD_STK_LYR_NO,2,2)STK_LYR
		                   FROM TB_YD_CARFTMVMTL A, TB_YD_CARSCH B
		                   WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
		                   AND A.DEL_YN = 'N'
		                   AND B.TRN_EQP_CD = ?
		                   ORDER BY A.YD_STK_LYR_NO 
			    		 */
	    				
				//		int iSeqCount 	= FrtostlList1.size();

		        //        for(int i=0; i < iSeqCount ; i++){
							
				//    		JDTORecord FrtoSltrec = (JDTORecord)FrtostlList1.get(i);
				//			s_STOCK_ID = StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"),"");
							
							//저장품테이블에 update				
				//			stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.delWbookID";
				//			iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{s_STOCK_ID});
							
//							차량재료정보테이블테이블 delete			
				//			stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.delstlList";
				//			iSeq = ydStockDAO.deleteData(stkQueryId, new Object[]{s_STOCK_ID});
							
							/*
							DELETE
                            FROM TB_YD_CARFTMVMTL
                            WHERE  STL_NO = ?   
							*/
							
							/*
			                 *	적치단  Table Update(작업요구상태='L'로 변경)
			                 */
			     //       	String sQuery4 = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
			     //       	iSeq = ydStockDAO.requestupdateData(sQuery4, 
				//											    new Object[]{YmCommonConst.STACK_LAYER_STAT_L, 
			      //      										s_STOCK_ID });
			            
				  ///  	}
		                
		                //차량스케쥴정보 삭제
		           //     stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.delCarschID";
					//	iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{s_YD_CAR_SCH_ID}); 
						
	    		   // }	    		    
				
				
				
				
				logger.println(LogLevel.DEBUG,this,"=출발실적처리=>공차처리 시작");   
				
				/*
				 * 현재는 권하완료 시점에 VL로 셋팅한다.
				 * 여기서 추가셋팅을 할것인지는 추후 검토한다(B-Cast 이송재 이동조건 여기서 셋팅해야 함.)
				 */
				sMoveterm = YmCommonConst.NEW_STOCK_MOVE_TERM_CS;
				
				String aimBay_All = ""; 
				String aimYD_All = ""; 
				//A연주-B Cast Slab Yard
				if(szARR_WLOC_CD.equals("D2Y43")||szARR_WLOC_CD.equals("D3Y43")) 
				{				
					
					logger.println(LogLevel.DEBUG,this,"=파레트 도착 목적동검색");
					
					if(szARR_WLOC_CD.equals("D2Y43"))
					{
						String queryID 	= "ym.ilkwan.dao.YdStockDAO.getListAimbayBCast";
				    	List AimbayBCast = dao.getCommonList(queryID);
				    			    	
				    	int iSeqCount 	= AimbayBCast.size();				    	
				    	String aimBay_BCast = "";
				    	if(iSeqCount >0){
				    	JDTORecord AimbayBCastReq = (JDTORecord)AimbayBCast.get(0);
				    	aimBay_BCast	= StringHelper.evl(AimbayBCastReq.getFieldString("AIM_BAY"), "");	
				    	logger.println(LogLevel.DEBUG,this,"=BCast 목적동 검색결과 : " + aimBay_BCast);	
				    	aimYD_All = "0";
				    	aimBay_All = aimBay_BCast;
				    	}
					}else if(szARR_WLOC_CD.equals("D3Y43")) ///////////////////////////////////////////////////////////////////
					{							
						String queryID 	= "ym.ilkwan.dao.YdStockDAO.getListAimbayBSlab";
				    	List AimbayBSlab = dao.getCommonList(queryID);
				    			    	
				    	int iSeqCount 	= AimbayBSlab.size();				    	
				    	String aimBay_BSlab = "";
				    	if(iSeqCount >0){
				    	JDTORecord AimbayBSlabReq = (JDTORecord)AimbayBSlab.get(0);
				    	aimBay_BSlab	= StringHelper.evl(AimbayBSlabReq.getFieldString("AIM_BAY"), "");	
				    	logger.println(LogLevel.DEBUG,this,"=BCast 목적동 검색결과 : " + aimBay_BSlab);	
				    	aimYD_All = "2";
				    	aimBay_All = aimBay_BSlab;
				    	}																					
					}
					
				logger.println(LogLevel.DEBUG,this,"=BCast 이송지시대상재 조회 시작");
					/* 착지별로 그룹핑
					 *   SELECT
							       (select SCH_RULE_VAL
							       from TB_YM_STACKPRIORITY
							       where RULE_ID = 'YM05'
							       and B.ARR_WLOC_CD = SCH_CD ) AS 개소순위,
							
							       (select SCH_RULE_VAL
							       from TB_YM_STACKPRIORITY
							       where RULE_ID = 'YM04'
							       and SUBSTR(A.STACK_COL_GP,2,1) = SCH_CD ) AS 동순위,
							       
							       (select SCH_RULE_VAL
							       from TB_YM_STACKPRIORITY
							       where RULE_ID = 'YM03'
							       and C.SCARFING_YN = SCH_CD ) AS 스카핑순위,
		
								   SUBSTR(A.STACK_COL_GP,2,1) AIM_BAY,							   
							       A.STOCK_ID,
							       A.STACK_COL_GP,
							       A.STACK_BED_GP,
							       A.STACK_LAYER_GP,
							       B.SPOS_WLOC_CD,      -- 발지개소
							       B.ARR_WLOC_CD      -- 착지개소
							FROM    TB_YM_STACKLAYER A,
							        TB_TS_MATL_FTMV_WO B,
							        VW_YD_SLABCOMM C
							WHERE   A.STOCK_ID = B.STL_NO
							AND     A.STOCK_ID = C.SLAB_NO
							AND     A.STACK_LAYER_STAT = 'L'
							AND     B.SPOS_WLOC_CD = ?
							AND     B.TS_MATL_FTMV_STAT_GP = '1'
							AND     B.MATL_FTMV_WO_NML_HD_YN = 'Y'
							ORDER BY 개소순위,
							         동순위,
							         스카핑순위,
							         A.STACK_COL_GP,
							         A.STACK_BED_GP,
							         A.STACK_LAYER_GP DESC

					*/
					//대상재 조회
										
			    	String queryID 	= "ym.ilkwan.dao.YdStockDAO.getListFrtoStl_ASlab";
			    	FrtoProductList = dao.getCommonList(queryID, new Object[]{szARR_WLOC_CD});
			    	
			    	int iSeqCount 	= FrtoProductList.size();
			    	String s_aimBay = "";
			    	if(iSeqCount >0){
			    	JDTORecord FrtoProductReq = (JDTORecord)FrtoProductList.get(0);
			    	s_aimBay	= StringHelper.evl(FrtoProductReq.getFieldString("AIM_BAY"), "");
			    		
			    	}

			    	
			    	logger.println(LogLevel.DEBUG,this,"=BCast 이송지시대상재 조회 종료");
			    	logger.println(LogLevel.DEBUG,this,"=BCast 이송지시대상재 조회 건 수"+iSeqCount);
			    	
			    	
			   // 	=========================================================================================================================================
			   // 	=========================================================================================================================================
			    	if(iSeqCount==0)//야드에 적치된 이송지시대상재가 없을경우
			    	{
			    		logger.println(LogLevel.DEBUG,this,"=BCast 공차출발실적 수신 =======야드에 적치된 이송지시대상재가 없을경우");
			    		sSchCode 		= YmCommonConst.NEW_SCH_WORK_KIND_SVML;
			    		
			    		logger.println(LogLevel.DEBUG,this,"=출발실적처리=>상차정지위치검색");
//			    		상차정지위치 검색
				    	//추가부분 : 검색대상재 동에 있는 차량위치정보  
			    		trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
			    		/*
							SELECT  STACK_COL_GP,
									YD_PNT_CD
							FROM TB_YM_STACKCOL 
							WHERE WLOC_CD     = :WLOC_CD
							AND YD_GP         = :YD_GP
							AND BAY_GP        = :BAY_GP
							AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
							AND TRN_EQP_CD  IS NULL	-- 현재차량정지위치
							AND CAR_CARD_NO IS NULL	-- 예약차량정지위치
				    	 */
			    		loadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{	szARR_WLOC_CD,
                                                                                       aimYD_All ,
                                                                                       aimBay_All,
			    				                                                        szTRN_EQP_GP});
			    		 
//			    		포인트 모두 점유상태일때 상차완료된 포인트찾음					    	  
			    		trnEqpQueryId = "ym.tsinfo.getListloadEndpoint";
			    		/*
							SELECT B.STACK_COL_GP,
                                   B.YD_PNT_CD
                           FROM TB_YD_CARSCH A,TB_YM_STACKCOL B
                           WHERE A.DEL_YN = 'N'
                           AND A.YD_CAR_PROG_STAT = '5'
                           AND SUBSTR(YD_CARLD_STOP_LOC,1,1) = ?
                           AND SUBSTR(YD_CARLD_STOP_LOC,2,1) = ?
                           AND SUBSTR(YD_CARLD_STOP_LOC, 3, 2) = ?
                           AND A.YD_CARLD_STOP_LOC = B.STACK_COL_GP
                           AND A.SPOS_WLOC_CD = B.WLOC_CD
				    	 */
			    		loadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{aimYD_All,
			    				                                                        aimBay_All,
																	    				szTRN_EQP_GP});			    		
			    		if(loadPointList.size() > 0){
					    	JDTORecord loadPointrec = (JDTORecord)loadPointList.get(0);
					    	sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
					    	sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
			    		}else if(loadEndPointList.size() > 0){
			    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>상차완료 포인트 검색");
					    	JDTORecord loadPointrec = (JDTORecord)loadEndPointList.get(0);
					    	sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
					    	sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
			    		}else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
			    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>상차완료 포인트 없음 => 포인트없음 처리");
			    			sloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
			    			sloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;
			    			
			    			//대기장 포인트 사유 가져오기
				    		sYD_MSG_CD = this.getCarMsg("N" ,aimYD_All , aimBay_All , szTRN_EQP_GP , szARR_WLOC_CD,"");
				    			
			    			Paramrecord = JDTORecordFactory.getInstance().create(); 
							Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
							Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
							Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
							
							// 발지 차량 적치단 Clear 하기
							layerState = "C";
							queryID1 = "ym.tsinfo.updateLayerstat_02_new";
							count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
							
//							발지 차량정보 삭제 하기 
					    	queryID1 = "ym.tsinfo.updateLayerstat_03";
					    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
					    	
					    	//차량포인트통합관리(구분, 장비번호,저장위치,상태)
						    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
						    
					    	//if(count >0 ){
					    	//layerState 	= "C";
					    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
					    	//count = dao.updateData(queryID1, new Object[]{	layerState,
					    	//												szSPOS_WLOC_CD,
					    	//												szSPOS_YD_PNT_CD,
					    	//												szTRN_EQP_GP}); 
					    	//}
			    			
			    		}
						
				    	//차량스케쥴테이블에 insert
			    		szYD_CAR_USE_GP 	= "L";
			    		szYD_EQP_WRK_STATE 	= "U";//공차경우
		  	            trnEqpQueryId 		= "ym.tsinfo.inserttrnEqpsch";
				    	iSeq = dao.insertData(trnEqpQueryId, new Object[]{ 	szTRN_EQP_CD,
				    			                                            szYD_CAR_USE_GP,
				    			                                            szTRN_EQP_CD,
				    			                                            szYD_EQP_WRK_STATE,
				    			                                            szARR_WLOC_CD,
				    														"", //착지개소코드
				    														"",
				    														sloadStoppoint}); 
				    	/*
				    	 * INSERT INTO TB_YD_CARSCH
							(
							    YD_CAR_SCH_ID
							    ,DEL_YN
							    ,YD_EQP_ID
							    ,YD_CAR_USE_GP
							    ,TRN_EQP_CD
							    ,YD_EQP_WRK_STAT
							    ,SPOS_WLOC_CD
							    ,ARR_WLOC_CD
							    ,YD_CARLD_LEV_DT
							    ,YD_CARLD_PNT_WO_DT
							    ,YD_CARLD_WRK_BOOK_ID
							    ,YD_CARLD_STOP_LOC
							    ,YD_CAR_PROG_STAT 
							)
							SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') ||  LPAD(YD_CARSCH_SEQ.nextval,6,'0') AS YD_CAR_SCH_ID
							,'N'
							,(SELECT YD_EQP_ID
							  FROM TB_YD_CARSPEC
							  WHERE TRN_EQP_CD = :TRN_EQP_CD
							  AND DEL_YN = 'N')YD_EQP_ID
							,:YD_CAR_USE_GP
							,:TRN_EQP_CD
							,:YD_EQP_WRK_STAT
							,:SPOS_WLOC_CD
							,:ARR_WLOC_CD
							,SYSDATE
							,SYSDATE
							,:YD_CARLD_WRK_BOOK_ID
							,:YD_CARLD_STOP_LOC
							,'1'
							FROM DUAL
				    	 */
						
				      /*
		  	             * 포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
		  	          */
		  	          trnEqpQueryId 		= "ym.tsinfo.updateEquipcolStat";
		  	          iSeq = dao.updateData(trnEqpQueryId, new Object[]{ 	szYD_CAR_USE_GP,	  	            		                                            
								                                            szTRN_EQP_CD,
								                                            sloadStoppoint}); 	  	           	  	            
		  	            
		  	          //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					  CarPointinforeg("3","",szTRN_EQP_CD,sloadStoppoint,"","","R");
					  
		  	          /*
		  	             * UPDATE TB_YM_STACKCOL
	                       SET STACK_STAT = :STACK_STAT,
	                           CAR_CARD_NO = :CAR_CARD_NO,
	                           MODIFIER = 'TSYDJ004',
	                           MOD_DDTT = sysdate
	                       WHERE STACK_COL_GP = :STACK_COL_GP
		  	             */
		  	          
				    	
				    	/*
		  	             * 20090801_YJK
		  	             * 향후 포인트 예약실적 I/F 추가
		  	             */
				    	
				    	//포인트지시 모듈 독립적으로 만들어서 호출
				    	//착지개소 및 포인트코드 추후 셋팅요 
						Paramrecord = JDTORecordFactory.getInstance().create(); 
						Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
						Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
						Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
						Paramrecord.setField("YD_MSG_CD", 		sYD_MSG_CD);
						
						this.procMatlCarArrPntReq(Paramrecord);
		            
						isSuccess = true;
	    		
			    	}
			    		
			    		
			    	// 	=========================================================================================================================================
					// 	=========================================================================================================================================		    		
						    	
			    	//야드에 적치된 이송대상재가 존재할 경우
			    	else if(iSeqCount>0)
			    	{ 			    				
			    		logger.println(LogLevel.DEBUG,this,"=BCast 공차출발실적 수신 =======야드에 적치된 이송지시대상재가 존재 할 경우");
			    		
			    		
				    	
			    	      //목적동의 상차대상편성 방법이 자동인지 수동인지 조회			
			    		String s_upLoadMhthod = "";
			    		 if(szTRN_EQP_GP.equals("PT"))
			    		 {
			    			 queryID 	= "ym.tsinfo.getUploadMethod";
				    	      /*
				    	       * select DOWN_CD
                              from TB_YM_EQUIP
                              WHERE EQUIP_GP LIKE '0'||?||'PT%'
				    	       */
				    	      uploadMethodList = dao.getCommonList(queryID, new Object[]{aimYD_All,s_aimBay});
				    	      JDTORecord uploadMethodReq = (JDTORecord)uploadMethodList.get(0);
				    	      s_upLoadMhthod	= StringHelper.evl(uploadMethodReq.getFieldString("DOWN_CD"), "");				    			 
			    			 
			    		 }
			    		 else if(szTRN_EQP_GP.equals("TR"))
			    		 {
			    			 queryID 	= "ym.tsinfo.getUploadMethod_TR";
				    	      /*
				    	       * select DOWN_CD
                              from TB_YM_EQUIP
                              WHERE EQUIP_GP LIKE '0'||?||'PT%'
				    	       */
				    	      uploadMethodList = dao.getCommonList(queryID, new Object[]{aimYD_All,s_aimBay});
				    	      JDTORecord uploadMethodReq = (JDTORecord)uploadMethodList.get(0);
				    	      s_upLoadMhthod	= StringHelper.evl(uploadMethodReq.getFieldString("DOWN_CD"), "");				    			 
			    			 
			    		 }
			    	      
			    	    if(s_upLoadMhthod.equals("A")) // 자동편성인 경우   
			    	    {
			    		    JDTORecord AvaPointList = (JDTORecord)FrtoProductList.get(0);
				    	    s_STACK_YD_GP 	= StringHelper.evl(AvaPointList.getFieldString("STACK_COL_GP"),"").substring(0,1);
						    s_STACK_BAY_GP 	= StringHelper.evl(AvaPointList.getFieldString("STACK_COL_GP"),"").substring(1,2);
						
						    logger.println(LogLevel.DEBUG,this,"=출발실적처리=>상차정지위치검색");
						    //상차정지위치 검색
				    	    //추가부분 : 검색대상재 동에 있는 차량위치정보  
			    		    trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
			    		    /*
				    	 	SELECT  STACK_COL_GP,
									YD_PNT_CD
							FROM TB_YM_STACKCOL 
							WHERE WLOC_CD     = :WLOC_CD
							AND YD_GP         = :YD_GP
							AND BAY_GP        = :BAY_GP
							AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
							AND TRN_EQP_CD  IS NULL	-- 현재차량정지위치
							AND CAR_CARD_NO IS NULL	-- 예약차량정지위치
				    	 */
			    		    loadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{	szARR_WLOC_CD,
																	    				s_STACK_YD_GP,
																	    				s_STACK_BAY_GP,
																	    				szTRN_EQP_GP});
			    		
			    		 
//			    		    포인트 모두 점유상태일때 상차완료된 포인트찾음					    	  
			    		    trnEqpQueryId = "ym.tsinfo.getListloadEndpoint";
			    		   /*
							SELECT B.STACK_COL_GP,
                                   B.YD_PNT_CD
                           FROM TB_YD_CARSCH A,TB_YM_STACKCOL B
                           WHERE A.DEL_YN = 'N'
                           AND A.YD_CAR_PROG_STAT = '5'
                           AND SUBSTR(YD_CARLD_STOP_LOC,1,1) = ?
                           AND SUBSTR(YD_CARLD_STOP_LOC,2,1) = ?
                           AND SUBSTR(YD_CARLD_STOP_LOC, 3, 2) = ?
                           AND A.YD_CARLD_STOP_LOC = B.STACK_COL_GP
                           AND A.SPOS_WLOC_CD = B.WLOC_CD
				    	 */
			    		    loadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STACK_YD_GP,
			    			                                                          	s_STACK_BAY_GP,
																	    				szTRN_EQP_GP});
			    		
			    		    if(loadPointList.size() > 0){
					        	JDTORecord loadPointrec = (JDTORecord)loadPointList.get(0);
					        	sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
					        	sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
			    		    }else if(loadEndPointList.size() > 0){
			    		    	logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>상차완료 포인트 검색");
					        	JDTORecord loadPointrec = (JDTORecord)loadEndPointList.get(0);
					        	sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
					        	sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
			    	    	}else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
			    	    		logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>상차완료 포인트 없음 => 포인트없음 처리");
			    		    	sloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
			    		    	sloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;
			    		    	
			    		    	//대기장 포인트 사유 가져오기
  				    			sYD_MSG_CD = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD,"");
  				    			
			    		    	Paramrecord = JDTORecordFactory.getInstance().create(); 
					    		Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
					    		Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
						    	Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
						    	
								// 발지 차량 적치단 Clear 하기
								layerState = "C";
								queryID1 = "ym.tsinfo.updateLayerstat_02_new";
								count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
								
//						    	발지 차량정보 삭제 하기 
						    	queryID1 = "ym.tsinfo.updateLayerstat_03";
						    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
						    	
						    	//차량포인트통합관리(구분, 장비번호,저장위치,상태)
							    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
							    
						    	//if(count >0 ){
						    	//layerState 	= "C";
						    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
						    	//count = dao.updateData(queryID1, new Object[]{	layerState,
						    	//												szSPOS_WLOC_CD,
						    	//												szSPOS_YD_PNT_CD,
						    	//												szTRN_EQP_GP}); 		
						    	//}
			    			
			    	    	}
			    	    }
			    	    
			    	    else if(s_upLoadMhthod.equals("M")) // 수동편성인 경우   
			    	    {
			    	    	logger.println(LogLevel.DEBUG,this,"=출발실적처리=>상차정지위치검색");
						    //상차정지위치 검색
				    	    //추가부분 : 검색대상재 동에 있는 차량위치정보  
			    		    trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
			    		    /*
				    	 	SELECT  STACK_COL_GP,
									YD_PNT_CD
							FROM TB_YM_STACKCOL 
							WHERE WLOC_CD     = :WLOC_CD
							AND YD_GP         = :YD_GP
							AND BAY_GP        = :BAY_GP
							AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
							AND TRN_EQP_CD  IS NULL	-- 현재차량정지위치
							AND CAR_CARD_NO IS NULL	-- 예약차량정지위치
				    	 */
			    		    loadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{	szARR_WLOC_CD,
			    		    		                                                    aimYD_All,
			    		    		                                                    aimBay_All,
																	    				szTRN_EQP_GP});
			    		
			    		 
//			    		    포인트 모두 점유상태일때 상차완료된 포인트찾음					    	  
			    		    trnEqpQueryId = "ym.tsinfo.getListloadEndpoint";
			    		   /*
							SELECT B.STACK_COL_GP,
                                   B.YD_PNT_CD
                           FROM TB_YD_CARSCH A,TB_YM_STACKCOL B
                           WHERE A.DEL_YN = 'N'
                           AND A.YD_CAR_PROG_STAT = '5'
                           AND SUBSTR(YD_CARLD_STOP_LOC,1,1) = ?
                           AND SUBSTR(YD_CARLD_STOP_LOC,2,1) = ?
                           AND SUBSTR(YD_CARLD_STOP_LOC, 3, 2) = ?
                           AND A.YD_CARLD_STOP_LOC = B.STACK_COL_GP
                           AND A.SPOS_WLOC_CD = B.WLOC_CD
				    	 */
			    		    loadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{aimYD_All,
			    		    		                                                    aimBay_All,
																	    				szTRN_EQP_GP});
			    		
			    		    if(loadPointList.size() > 0){
					        	JDTORecord loadPointrec = (JDTORecord)loadPointList.get(0);
					        	sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
					        	sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
			    		    }else if(loadEndPointList.size() > 0){
			    		    	logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>상차완료 포인트 검색");
					        	JDTORecord loadPointrec = (JDTORecord)loadEndPointList.get(0);
					        	sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
					        	sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
			    	    	}else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
			    	    		logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>상차완료 포인트 없음 => 포인트없음 처리");
			    		    	sloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
			    		    	sloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;
			    		    	
			    		    	//대기장 포인트 사유 가져오기
  				    			sYD_MSG_CD = this.getCarMsg("N" ,aimYD_All , aimBay_All , szTRN_EQP_GP , szARR_WLOC_CD,"");
  				    			
			    		    	Paramrecord = JDTORecordFactory.getInstance().create(); 
					    		Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
					    		Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
						    	Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
						    	
								// 발지 차량 적치단 Clear 하기
								layerState = "C";
								queryID1 = "ym.tsinfo.updateLayerstat_02_new";
								count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
								
//						    	발지 차량정보 삭제 하기 
						    	queryID1 = "ym.tsinfo.updateLayerstat_03";
						    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
						    	
						    	//차량포인트통합관리(구분, 장비번호,저장위치,상태)
							    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
							    
						    	//if(count >0 ){
						    	//layerState 	= "C";
						    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
						    	//count = dao.updateData(queryID1, new Object[]{	layerState,
						    	//												szSPOS_WLOC_CD,
						    	//												szSPOS_YD_PNT_CD,
						    	//												szTRN_EQP_GP}); 
						    	//}
			    			
			    	    	}
			    	    }
						
						
						

			    	
			    	      if(s_upLoadMhthod.equals("A")) // 자동편성인 경우 
			    	      {  
			    	    	  logger.println(LogLevel.DEBUG,this,"=BCast 공차출발실적 수신 =======해당 목적동의 대상재 편성 방법이 자동");
			    	                sSchCode 		= YmCommonConst.NEW_SCH_WORK_KIND_SVML;
			    	    	  
//			    	    	            상차 LOT편성

							       //작업예약생성	
							    	wBookQueryId 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";								
									wBookSel 		= ydStackLayerDAO.requestFind(wBookQueryId);
									wBookid      	= wBookSel.getFieldString("WBOOK_ID");
							    	
							    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
							    	s_STACK_YD_GP 	= StringHelper.evl(FrtoProduct.getFieldString("STACK_COL_GP"),"").substring(0,1);
									s_STACK_BAY_GP 	= StringHelper.evl(FrtoProduct.getFieldString("STACK_COL_GP"),"").substring(1,2);
									
									//작업예약테이블에 insert
									sWBookQueryId 	= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
									 
									iSeq = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ wBookid, 
											 s_STACK_YD_GP, 
											 s_STACK_BAY_GP,
											 sSchCode, 
											 YmCommonUtil.getWorkDuty(),
											 YmCommonUtil.getWorkParty()});


						    	/**
						    	 *  차량스펙 및 그룹핑재료정보에 따른 상차물량 결정처리
						    	 */
						    	int iLoopIndex 		= 0;
						    	String sLoopIndex1 	= "";
						    	String sLoopIndex2 	= "";
						    	{
						    		int iLoadMax = 0;
						    		if(szTRN_EQP_GP.equals("TR"))
						    		{
						    			/*iLoadMax = 4;
						    			STK_CAPA = Integer.parseInt(szTRN_EQP_STK_CAPA);
						    			for(int ii=0 ; ii < iLoadMax ; ii++){
						    				
						    				JDTORecord stkwtRwq = (JDTORecord)FrtoProductList.get(ii);
						    				
						    				STK_WT += stkwtRwq.getFieldInt("STK_WT");
						    				
						    				if(STK_WT > STK_CAPA)
						    				{
						    					iLoadMax = ii;
						    					break;
						    				}
						    			}*/
						    		
						    		 iLoadMax = 3;	
			
						    		}
						    		else 
						    		{
							    		iLoadMax = 6;	
						    		}
						    		if(iLoadMax > iSeqCount) iLoopIndex = iSeqCount;
						    		else iLoopIndex = iLoadMax;
						    		
						    		for(int index=0; index < iLoopIndex ; index++){
						    			
										JDTORecord FrtoProduct2 = (JDTORecord)FrtoProductList.get(index);
										sLoopIndex2	 = "";
										sLoopIndex2	+= StringHelper.evl(FrtoProduct2.getFieldString("개소순위"),"");
							    		sLoopIndex2	+= StringHelper.evl(FrtoProduct2.getFieldString("동순위"),"");
							    		sLoopIndex2	+= StringHelper.evl(FrtoProduct2.getFieldString("스카핑순위"),"");
							    		
							    		logger.println(LogLevel.DEBUG,this,"=재료그룹속성1 => "+ sLoopIndex1);   
							    		logger.println(LogLevel.DEBUG,this,"=재료그룹속성2 => "+ sLoopIndex2);   
							    		if(index == 0 ){
							    			sLoopIndex1 = sLoopIndex2;
							    		}else{
							    			if(sLoopIndex1.equals(sLoopIndex2)){
							    				
							    			}else{
							    				iLoopIndex = index;
							    				break;
							    			}
							    		}
						    		}
						    	}

						    	
						    	for(int index=0; index < iLoopIndex ; index++){
									
						    		JDTORecord FrtoProduct2 = (JDTORecord)FrtoProductList.get(index);
									s_STOCK_ID = StringHelper.evl(FrtoProduct2.getFieldString("STOCK_ID"),"");
									
									//이전 작업예약 존재 유무 체크  
									trnQueryId 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID3";
						    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{s_STOCK_ID});
						 
						    		JDTORecord TolocRecCoil = (JDTORecord)FrtostlList.get(0);
						    		wBookid2 = StringHelper.evl(TolocRecCoil.getFieldString("WBOOK_ID"),"");
						    		
						    		if(!wBookid2.equals("")){
						    			stkQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteWbookInfo";
											iSeq = ydStockDAO.deleteData(stkQueryId, new Object[]{wBookid2});
						    		}
									
									//저장품테이블에 update				
									stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
									iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ wBookid, 
											                                                      sMoveterm,
																								  s_STOCK_ID});
									/*
					                 *	적치단  Table Update(작업요구상태='S'로 변경)
					                 */
					            	String sQuery4 = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
					            	iSeq = ydStockDAO.requestupdateData(sQuery4, 
																	    new Object[]{YmCommonConst.STACK_LAYER_STAT_S, 
					            										s_STOCK_ID });
						    	}

								
						    	//차량스케쥴테이블에 insert
					    		
					    		szYD_CAR_USE_GP 	= "L";
					    		szYD_EQP_WRK_STATE 	= "U";//공차경우
				  	            trnEqpQueryId 		= "ym.tsinfo.inserttrnEqpsch";
						    	iSeq = dao.insertData(trnEqpQueryId, new Object[]{ 	szTRN_EQP_CD,
						    			                                            szYD_CAR_USE_GP,
						    			                                            szTRN_EQP_CD,
						    			                                            szYD_EQP_WRK_STATE,
						    			                                            szARR_WLOC_CD,
						    														"", //착지개소코드
						    														wBookid,
						    														sloadStoppoint}); 
						    	/*
						    	 * INSERT INTO TB_YD_CARSCH
									(
									    YD_CAR_SCH_ID
									    ,DEL_YN
									    ,YD_EQP_ID
									    ,YD_CAR_USE_GP
									    ,TRN_EQP_CD
									    ,YD_EQP_WRK_STAT
									    ,SPOS_WLOC_CD
									    ,ARR_WLOC_CD
									    ,YD_CARLD_LEV_DT
									    ,YD_CARLD_PNT_WO_DT
									    ,YD_CARLD_WRK_BOOK_ID
									    ,YD_CARLD_STOP_LOC
									    ,YD_CAR_PROG_STAT 
									)
									SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') ||  LPAD(YD_CARSCH_SEQ.nextval,6,'0') AS YD_CAR_SCH_ID
									,'N'
									,(SELECT YD_EQP_ID
									  FROM TB_YD_CARSPEC
									  WHERE TRN_EQP_CD = :TRN_EQP_CD
									  AND DEL_YN = 'N')YD_EQP_ID
									,:YD_CAR_USE_GP
									,:TRN_EQP_CD
									,:YD_EQP_WRK_STAT
									,:SPOS_WLOC_CD
									,:ARR_WLOC_CD
									,SYSDATE
									,SYSDATE
									,:YD_CARLD_WRK_BOOK_ID
									,:YD_CARLD_STOP_LOC
									,'1'
									FROM DUAL
						    	 */

						    	//차량스케쥴ID조회
					    		trnEqpQueryId = "ym.tsinfo.getListtrnEqpschL";
					    		carSchList = dao.getCommonList(trnEqpQueryId, new Object[]{szTRN_EQP_CD});
						    	
					    		if(carSchList.size() > 0){
							    	JDTORecord CarSchrec = (JDTORecord)carSchList.get(0);
							    	sCarSchCode  = StringHelper.evl(CarSchrec.getFieldString("YD_CAR_SCH_ID"), "");
					    		}
						    	/*
						    	 * SELECT YD_CARLD_WRK_BOOK_ID WBOOK_ID
						    	 *        ,YD_CAR_SCH_ID
					                FROM TB_YD_CARSCH
					               WHERE TRN_EQP_CD = ?
					               AND YD_CAR_PROG_STAT = '1'
					               AND (DEL_YN IS NULL OR DEL_YN <> 'Y')
						    	 */	
									
						    	//차량대상재 테이블에 insert	
						    	for(int index=0; index < iLoopIndex ; index++){
						    		
						    		logger.println(LogLevel.DEBUG,this,"=출발실적처리=>차량스케쥴 대상재 등록 ");   
						    		
						    		JDTORecord FrtoProduct2 = (JDTORecord)FrtoProductList.get(index);
									s_STOCK_ID = StringHelper.evl(FrtoProduct2.getFieldString("STOCK_ID"),"");
									s_BED_GP = StringHelper.evl(FrtoProduct2.getFieldString("STACK_BED_GP"),"");
									s_LAYER_GP = StringHelper.evl(FrtoProduct2.getFieldString("STACK_LAYER_GP"),"");
									
									trnEqpQueryId = "ym.tsinfo.insertCarftmtl";
						    		iSeq = dao.insertData(trnEqpQueryId, new Object[]{ sCarSchCode,s_STOCK_ID}); 
							    	/*
							    	 *  INSERT INTO TB_YD_CARFTMVMTL
										(
										    YD_CAR_SCH_ID
										   ,STL_NO
										   ,DEL_YN						   
										)
										VALUES (
										    :YD_CAR_SCH_ID,
										    :STL_NO,
										    'N'					    
										)
							    	 */
						    	}
						    	
						     /*
				  	             * 포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
				  	          */
				  	          trnEqpQueryId 		= "ym.tsinfo.updateEquipcolStat";
				  	          iSeq = dao.updateData(trnEqpQueryId, new Object[]{ 	szYD_CAR_USE_GP,	  	            		                                            
										                                            szTRN_EQP_CD,
										                                            sloadStoppoint}); 	  	           	  	            
				  	            
				  	          //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
							  CarPointinforeg("3","",szTRN_EQP_CD,sloadStoppoint,"","","R");
							  
				  	            /*
				  	             * UPDATE TB_YM_STACKCOL
			                       SET STACK_STAT = :STACK_STAT,
			                           CAR_CARD_NO = :CAR_CARD_NO,
			                           MODIFIER = 'TSYDJ004',
			                           MOD_DDTT = sysdate
			                       WHERE STACK_COL_GP = :STACK_COL_GP
				  	             */
						    	
				  	          
						    	/*
				  	             * 20090801_YJK
				  	             * 향후 포인트 예약실적 I/F 추가
				  	             */
						    	
						    	//포인트지시 모듈 독립적으로 만들어서 호출
						    	//착지개소 및 포인트코드 추후 셋팅요 
								Paramrecord = JDTORecordFactory.getInstance().create(); 
								Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
								Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
								Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
								Paramrecord.setField("YD_MSG_CD", 		sYD_MSG_CD);
								
								this.procMatlCarArrPntReq(Paramrecord);
				            
								isSuccess = true;				    	    	  	
			    	      }
	      
			    	      else // 수동편성인 경우
			    	      {
			    	    	   logger.println(LogLevel.DEBUG,this,"=BCast 공차출발실적 수신 =======해당 목적동의 대상재 편성 방법이 자동");
			    	    		sSchCode 		= YmCommonConst.NEW_SCH_WORK_KIND_SVML;
			    	    		
								
						    	//차량스케쥴테이블에 insert
					    		szYD_CAR_USE_GP 	= "L";
					    		szYD_EQP_WRK_STATE 	= "U";//공차경우
				  	            trnEqpQueryId 		= "ym.tsinfo.inserttrnEqpsch";
						    	iSeq = dao.insertData(trnEqpQueryId, new Object[]{ 	szTRN_EQP_CD,
						    			                                            szYD_CAR_USE_GP,
						    			                                            szTRN_EQP_CD,
						    			                                            szYD_EQP_WRK_STATE,
						    			                                            szARR_WLOC_CD,
						    														"", //착지개소코드
						    														"",
						    														sloadStoppoint}); 
						    	/*
						    	 * INSERT INTO TB_YD_CARSCH
									(
									    YD_CAR_SCH_ID
									    ,DEL_YN
									    ,YD_EQP_ID
									    ,YD_CAR_USE_GP
									    ,TRN_EQP_CD
									    ,YD_EQP_WRK_STAT
									    ,SPOS_WLOC_CD
									    ,ARR_WLOC_CD
									    ,YD_CARLD_LEV_DT
									    ,YD_CARLD_PNT_WO_DT
									    ,YD_CARLD_WRK_BOOK_ID
									    ,YD_CARLD_STOP_LOC
									    ,YD_CAR_PROG_STAT 
									)
									SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') ||  LPAD(YD_CARSCH_SEQ.nextval,6,'0') AS YD_CAR_SCH_ID
									,'N'
									,(SELECT YD_EQP_ID
									  FROM TB_YD_CARSPEC
									  WHERE TRN_EQP_CD = :TRN_EQP_CD
									  AND DEL_YN = 'N')YD_EQP_ID
									,:YD_CAR_USE_GP
									,:TRN_EQP_CD
									,:YD_EQP_WRK_STAT
									,:SPOS_WLOC_CD
									,:ARR_WLOC_CD
									,SYSDATE
									,SYSDATE
									,:YD_CARLD_WRK_BOOK_ID
									,:YD_CARLD_STOP_LOC
									,'1'
									FROM DUAL
						    	 */
								
						      /*
				  	             * 포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
				  	          */
				  	          trnEqpQueryId 		= "ym.tsinfo.updateEquipcolStat";
				  	          iSeq = dao.updateData(trnEqpQueryId, new Object[]{ 	szYD_CAR_USE_GP,	  	            		                                            
										                                            szTRN_EQP_CD,
										                                            sloadStoppoint}); 	  	           	  	            
				  	            
				  	          //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
							  CarPointinforeg("3","",szTRN_EQP_CD,sloadStoppoint,"","","R");
							  
				  	          /*
				  	             * UPDATE TB_YM_STACKCOL
			                       SET STACK_STAT = :STACK_STAT,
			                           CAR_CARD_NO = :CAR_CARD_NO,
			                           MODIFIER = 'TSYDJ004',
			                           MOD_DDTT = sysdate
			                       WHERE STACK_COL_GP = :STACK_COL_GP
				  	             */
				  	          
						    	
						    	/*
				  	             * 20090801_YJK
				  	             * 향후 포인트 예약실적 I/F 추가
				  	             */
						    	
						    	//포인트지시 모듈 독립적으로 만들어서 호출
						    	//착지개소 및 포인트코드 추후 셋팅요 
								Paramrecord = JDTORecordFactory.getInstance().create(); 
								Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
								Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
								Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
								Paramrecord.setField("YD_MSG_CD", 		sYD_MSG_CD);
								
								this.procMatlCarArrPntReq(Paramrecord);
				            
								isSuccess = true;
			    	      }
			    	}
				}
				
				else if(szARR_WLOC_CD.equals("D2Y44")||
						szARR_WLOC_CD.equals("D2Y45")||
						szARR_WLOC_CD.equals("D3Y41")||
						szARR_WLOC_CD.equals("D3Y42")){
						    		
					if(szARR_WLOC_CD.equals("D3Y41")||
							szARR_WLOC_CD.equals("D3Y42"))
					{															         						
						//대상재 조회
				    	String queryID 	= "ym.ilkwan.dao.YdStockDAO.getListFrtoStl_CoilNewB";
				    	FrtoProductList = dao.getCommonList(queryID, new Object[]{szARR_WLOC_CD,szTRN_EQP_CD,szTRN_EQP_CD,szARR_WLOC_CD});
					}else if(szARR_WLOC_CD.equals("D2Y44")||
						szARR_WLOC_CD.equals("D2Y45"))
					{															         						
						//대상재 조회
				    	String queryID 	= "ym.ilkwan.dao.YdStockDAO.getListFrtoStl_CoilNewA";
				    	FrtoProductList = dao.getCommonList(queryID, new Object[]{szARR_WLOC_CD,szTRN_EQP_CD,szTRN_EQP_CD,szARR_WLOC_CD});
					}
			    	
					if(FrtoProductList.size() > 0){		
				    JDTORecord AvaPointList = (JDTORecord)FrtoProductList.get(0);
				    s_STACK_YD_GP 	= StringHelper.evl(AvaPointList.getFieldString("STACK_COL_GP"),"").substring(0,1);
					s_STACK_BAY_GP 	= StringHelper.evl(AvaPointList.getFieldString("STACK_COL_GP"),"").substring(1,2);	
					}
				logger.println(LogLevel.DEBUG,this,"=출발실적처리=>상차정지위치검색:"+FrtoProductList.size());
				//상차정지위치 검색
		    	//추가부분 : 검색대상재 동에 있는 차량위치정보  
	    		trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
	    		/*
		    	 	SELECT  STACK_COL_GP,
							YD_PNT_CD
					FROM TB_YM_STACKCOL 
					WHERE WLOC_CD     = :WLOC_CD
					AND YD_GP         = :YD_GP
					AND BAY_GP        = :BAY_GP
					AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
					AND TRN_EQP_CD  IS NULL	-- 현재차량정지위치
					AND CAR_CARD_NO IS NULL	-- 예약차량정지위치
		    	 */
	    		loadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{	szARR_WLOC_CD,
															    				s_STACK_YD_GP,
															    				s_STACK_BAY_GP,
															    				szTRN_EQP_GP});
//	    		포인트 모두 점유상태일때 상차완료된 포인트찾음					    	  
	    		trnEqpQueryId = "ym.tsinfo.getListloadEndpoint";
	    		/*
					SELECT B.STACK_COL_GP,
                           B.YD_PNT_CD
                   FROM TB_YD_CARSCH A,TB_YM_STACKCOL B
                   WHERE A.DEL_YN = 'N'
                   AND A.YD_CAR_PROG_STAT = '5'
                   AND SUBSTR(YD_CARLD_STOP_LOC,1,1) = ?
                   AND SUBSTR(YD_CARLD_STOP_LOC,2,1) = ?
                   AND SUBSTR(YD_CARLD_STOP_LOC, 3, 2) = ?
                   AND A.YD_CARLD_STOP_LOC = B.STACK_COL_GP
                   AND A.SPOS_WLOC_CD = B.WLOC_CD
		    	 */
	    		loadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STACK_YD_GP,
	    				                                                         s_STACK_BAY_GP,
															    				szTRN_EQP_GP});
	    		
	    		
		    		if(loadPointList.size() > 0){
				    	JDTORecord loadPointrec = (JDTORecord)loadPointList.get(0);
				    	sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
				    	sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
		    		}else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
		    			logger.println(LogLevel.DEBUG,this,"=모든 포인트 점유됨 =>상차완료 포인트 없음 => 포인트없음 처리");
		    			sloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
		    			sloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;
		    			
		    			//대기장 포인트 사유 가져오기
			    		sYD_MSG_CD = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD,"");
			    		
			    			
						// 발지 차량 적치단 Clear 하기
						layerState = "C";
						queryID1 = "ym.tsinfo.updateLayerstat_02_new";
						count = dao.updateData(queryID1 , new Object[]{layerState, szSPOS_WLOC_CD, szTRN_EQP_CD});
						
//		    			발지 차량정보 삭제 하기 
				    	queryID1 = "ym.tsinfo.updateLayerstat_03";
				    	count = dao.updateData(queryID1, new Object[]{szSPOS_WLOC_CD,szTRN_EQP_CD});
				    	
				    	//차량포인트통합관리(구분, 장비번호,저장위치,상태)
					    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
					    
				    	//if(count >0 ){
				    	//layerState 	= "C";
				    	//queryID1 	= "ym.tsinfo.updateLayerstat_02";
				    	//count = dao.updateData(queryID1, new Object[]{	layerState,
				    	//												szSPOS_WLOC_CD,
				    	//												szSPOS_YD_PNT_CD,
				    	//												szTRN_EQP_GP}); 
				    	//}
		    			
		    		}

	    	
		    	int iSeqCount 	= FrtoProductList.size();
		    			    	
		    		
		    	    if(iSeqCount > 0){
		    	    	
			    	     JDTORecord FrtoProductReq = (JDTORecord)FrtoProductList.get(0);
			    	     String s_STOCK  	= StringHelper.evl(FrtoProductReq.getFieldString("STOCK_ID"), "");
			    	     trnEqpQueryId 		= "ym.tsinfo.getListCoilSchGP";
			    	     FrtoProductReqList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STOCK});
					    
			    	     String s_STL_APPEAR_GP = "";
			    	     
				    	 if(FrtoProductReqList.size() > 0){
						    	JDTORecord FrtoProductCoil = (JDTORecord)FrtoProductReqList.get(0);
						    	s_STL_APPEAR_GP  = StringHelper.evl(FrtoProductCoil.getFieldString("STL_APPEAR_GP"), "");
				    	 }
				    	 
				    	 if(s_STL_APPEAR_GP.equals("Y"))// 제품
				    	 {
				    		 sSchCode	= YmCommonConst.NEW_SCH_WORK_KIND_GVML ; 
				    		 sSchCode = getSchWorkKind(sSchCode, sloadStoppoint);
				    	 }
				    	 else
				    	 {
				    		 sSchCode	= YmCommonConst.NEW_SCH_WORK_KIND_CVML ;
				    		 sSchCode = getSchWorkKind(sSchCode, sloadStoppoint);
				    	 }		    	     
		    	    }
		    	
		    			    	
		    	//상차 LOT편성
		    	if(iSeqCount > 0){
		    		
			    	//작업예약생성	
			    	wBookQueryId 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";								
					wBookSel 		= ydStackLayerDAO.requestFind(wBookQueryId);
					wBookid      	= wBookSel.getFieldString("WBOOK_ID");
			    	
			    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
			    	s_STACK_YD_GP 	= StringHelper.evl(FrtoProduct.getFieldString("STACK_COL_GP"),"").substring(0,1);
					s_STACK_BAY_GP 	= StringHelper.evl(FrtoProduct.getFieldString("STACK_COL_GP"),"").substring(1,2);
					
					//작업예약테이블에 insert
					sWBookQueryId 	= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
					 
					iSeq = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ wBookid, 
							 s_STACK_YD_GP, 
							 s_STACK_BAY_GP,
							 sSchCode, 
							 YmCommonUtil.getWorkDuty(),
							 YmCommonUtil.getWorkParty()});
		    	}
		    	

		    	/**
		    	 *  차량스펙 및 그룹핑재료정보에 따른 상차물량 결정처리
		    	 */
		    	List lCarStockList	= new ArrayList();	
		    	int iLoopIndex 		= 0;
		    	String sLoopIndex1 	= "";
		    	String sLoopIndex2 	= "";
		    	
		    	long lTotalWt	= 0;
		    	long lPerWt		= 0;
		    	long lCarMaxWt	= 0;
		    	
		    	int iLoadMax 	= 0;
		    	int iLoadCur 	= 0;
		    	{
		    		
		    		
		    		if(szTRN_EQP_GP.equals("TR"))
		    		{
		    			try{
		    				lCarMaxWt = Long.parseLong(szTRN_EQP_STK_CAPA);
		    			}catch(Exception e){
		    				lCarMaxWt = 70000;
		    			}
		    			iLoadMax = 3;	
		    		}
		    		else 
		    		{
		    			try{
		    				lCarMaxWt = Long.parseLong(szTRN_EQP_STK_CAPA);
		    			}catch(Exception e){
		    				lCarMaxWt = 180000;
		    			}
			    		iLoadMax = 6;	
		    		}
		    		
		    		for(int index=0; index < iSeqCount ; index++){
		    			
						JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(index);
						
						sLoopIndex2	 = "";
						sLoopIndex2	+= StringHelper.evl(FrtoProduct.getFieldString("소재제품우선순위"),"");
						sLoopIndex2	+= StringHelper.evl(FrtoProduct.getFieldString("현재동우선순위"),"");
			    		sLoopIndex2	+= StringHelper.evl(FrtoProduct.getFieldString("착지개소우선순위"),"");
			    		sLoopIndex2	+= StringHelper.evl(FrtoProduct.getFieldString("목표동우선순위"),"");
			    		
			    		logger.println(LogLevel.DEBUG,this,"=재료그룹속성1 => "+ sLoopIndex1);   
			    		logger.println(LogLevel.DEBUG,this,"=재료그룹속성2 => "+ sLoopIndex2);   
			    		
			    		if(index == 0 ){
			    			sLoopIndex1 = sLoopIndex2;
			    		}else{
			    			if(sLoopIndex1.equals(sLoopIndex2)){
			    				
			    			}else{

			    				break;
			    			}
			    		}
			    		
			    		lPerWt	= Long.parseLong(StringHelper.evl(FrtoProduct.getFieldString("STK_WT"),"0"));
			    		
			    		if(lCarMaxWt > lTotalWt + lPerWt){
			    			
			    			logger.println(LogLevel.DEBUG,this,"=상차LOT대상재 편성 => OK");   
			    			
			    			lCarStockList.add(FrtoProduct);
			    			lTotalWt += lPerWt;
			    			iLoadCur++;
			    			if(iLoadMax <= iLoadCur){
			    				break;
			    			}
			    		}
			    	}
		    	}
		    	
		    	logger.println(LogLevel.DEBUG,this,"=lCarMaxWt => "+ lCarMaxWt);   
		    	logger.println(LogLevel.DEBUG,this,"=lTotalWt => "+ lTotalWt);   
		    	
		    	for(int index=0; index < iLoadCur ; index++){
					
		    		JDTORecord FrtoProduct = (JDTORecord)lCarStockList.get(index);
					s_STOCK_ID = StringHelper.evl(FrtoProduct.getFieldString("STOCK_ID"),"");
					
					//이전 작업예약 존재 유무 체크  
					trnQueryId 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID3";
		    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{s_STOCK_ID});
		 
		    		JDTORecord TolocRecCoil = (JDTORecord)FrtostlList.get(0);
		    		wBookid2 = StringHelper.evl(TolocRecCoil.getFieldString("WBOOK_ID"),"");
		    		
		    		if(!wBookid2.equals("")){
		    			stkQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteWbookInfo";
							iSeq = ydStockDAO.deleteData(stkQueryId, new Object[]{wBookid2});
		    		}
					
					//저장품테이블에 update				
					stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
					iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ wBookid, 
							                                                      sMoveterm,
																				  s_STOCK_ID});
					/*
	                 *	적치단  Table Update(작업요구상태='S'로 변경)
	                 */
	            	String sQuery4 = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
	            	iSeq = ydStockDAO.requestupdateData(sQuery4, 
													    new Object[]{YmCommonConst.STACK_LAYER_STAT_S, 
	            										s_STOCK_ID });
		    	}
				
		    	//차량스케쥴테이블에 insert
	    		
	    		szYD_CAR_USE_GP 	= "L";
	    		szYD_EQP_WRK_STATE 	= "U";//공차경우
  	            trnEqpQueryId 		= "ym.tsinfo.inserttrnEqpsch";
		    	iSeq = dao.insertData(trnEqpQueryId, new Object[]{ 	szTRN_EQP_CD,
		    			                                            szYD_CAR_USE_GP,
		    			                                            szTRN_EQP_CD,
		    			                                            szYD_EQP_WRK_STATE,
		    			                                            szARR_WLOC_CD,
		    														"", //착지개소코드
		    														wBookid,
		    														sloadStoppoint}); 
		    	/*
		    	 * INSERT INTO TB_YD_CARSCH
					(
					    YD_CAR_SCH_ID
					    ,DEL_YN
					    ,YD_EQP_ID
					    ,YD_CAR_USE_GP
					    ,TRN_EQP_CD
					    ,YD_EQP_WRK_STAT
					    ,SPOS_WLOC_CD
					    ,ARR_WLOC_CD
					    ,YD_CARLD_LEV_DT
					    ,YD_CARLD_PNT_WO_DT
					    ,YD_CARLD_WRK_BOOK_ID
					    ,YD_CARLD_STOP_LOC
					    ,YD_CAR_PROG_STAT 
					)
					SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') ||  LPAD(YD_CARSCH_SEQ.nextval,6,'0') AS YD_CAR_SCH_ID
					,'N'
					,(SELECT YD_EQP_ID
					  FROM TB_YD_CARSPEC
					  WHERE TRN_EQP_CD = :TRN_EQP_CD
					  AND DEL_YN = 'N')YD_EQP_ID
					,:YD_CAR_USE_GP
					,:TRN_EQP_CD
					,:YD_EQP_WRK_STAT
					,:SPOS_WLOC_CD
					,:ARR_WLOC_CD
					,SYSDATE
					,SYSDATE
					,:YD_CARLD_WRK_BOOK_ID
					,:YD_CARLD_STOP_LOC
					,'1'
					FROM DUAL
		    	 */

		    	//차량스케쥴ID조회
	    		trnEqpQueryId = "ym.tsinfo.getListtrnEqpschL";
	    		carSchList = dao.getCommonList(trnEqpQueryId, new Object[]{szTRN_EQP_CD});
		    	
	    		if(carSchList.size() > 0){
			    	JDTORecord CarSchrec = (JDTORecord)carSchList.get(0);
			    	sCarSchCode  = StringHelper.evl(CarSchrec.getFieldString("YD_CAR_SCH_ID"), "");
	    		}
		    	/*
		    	 * SELECT YD_CARLD_WRK_BOOK_ID WBOOK_ID
		    	 *        ,YD_CAR_SCH_ID
	                FROM TB_YD_CARSCH
	               WHERE TRN_EQP_CD = ?
	               AND YD_CAR_PROG_STAT = '1'
	               AND (DEL_YN IS NULL OR DEL_YN <> 'Y')
		    	 */	
					
		    	//차량대상재 테이블에 insert	
		    	for(int index=0; index < iLoadCur ; index++){
		    		
		    		logger.println(LogLevel.DEBUG,this,"=출발실적처리=>차량스케쥴 대상재 등록 ");   
		    		
		    		JDTORecord FrtoProduct = (JDTORecord)lCarStockList.get(index);
					s_STOCK_ID = StringHelper.evl(FrtoProduct.getFieldString("STOCK_ID"),"");
					s_BED_GP = StringHelper.evl(FrtoProduct.getFieldString("STACK_BED_GP"),"");
					s_LAYER_GP = StringHelper.evl(FrtoProduct.getFieldString("STACK_LAYER_GP"),"");
					
					trnEqpQueryId = "ym.tsinfo.insertCarftmtl";
		    		iSeq = dao.insertData(trnEqpQueryId, new Object[]{ sCarSchCode,s_STOCK_ID}); 
			    	/*
			    	 *  INSERT INTO TB_YD_CARFTMVMTL
						(
						    YD_CAR_SCH_ID
						   ,STL_NO
						   ,DEL_YN						   
						)
						VALUES (
						    :YD_CAR_SCH_ID,
						    :STL_NO,
						    'N'					    
						)
			    	 */
		    	}
		    	
		      /*
  	             * 포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
  	          */
  	          trnEqpQueryId 		= "ym.tsinfo.updateEquipcolStat";
  	          iSeq = dao.updateData(trnEqpQueryId, new Object[]{ 	szYD_CAR_USE_GP,	  	            		                                            
						                                            szTRN_EQP_CD,
						                                            sloadStoppoint}); 	  	           	  	            
  	            
  	          //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			    CarPointinforeg("3","",szTRN_EQP_CD,sloadStoppoint,"","","R");
  	            /*
  	             * UPDATE TB_YM_STACKCOL
                   SET STACK_STAT = :STACK_STAT,
                       CAR_CARD_NO = :CAR_CARD_NO,
                       MODIFIER = 'TSYDJ004',
                       MOD_DDTT = sysdate
                   WHERE STACK_COL_GP = :STACK_COL_GP
  	             */
		    	
		    	/*
  	             * 20090801_YJK
  	             * 향후 포인트 예약실적 I/F 추가
  	             */
		    	
		    	//포인트지시 모듈 독립적으로 만들어서 호출
		    	//착지개소 및 포인트코드 추후 셋팅요 
				Paramrecord = JDTORecordFactory.getInstance().create(); 
				Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
				Paramrecord.setField("SPOS_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
				Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
				Paramrecord.setField("YD_MSG_CD", 		sYD_MSG_CD);
				
				this.procMatlCarArrPntReq(Paramrecord);
            
				isSuccess = true;
				
		    }	    	 				
		  }//영차종료
	    		    		    		    		    	    		
	    }// if 일관제철->AB 종료
	    	

    	//L-2 차량 출발 정보 전송 #################################################################
    	Paramrecord = JDTORecordFactory.getInstance().create(); 
		Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
		Paramrecord.setField("ARR_WLOC_CD", 	szSPOS_WLOC_CD);//착지개소코드
		Paramrecord.setField("ARR_YD_PNT_CD", 		szSPOS_YD_PNT_CD);
		Paramrecord.setField("TRN_EQP_GP", 		szTRN_EQP_GP);
		procMatlCarArrPntRequestL2(Paramrecord , "S") ; 	
		//####################################################################################3
		    	
	  }catch(Exception e){
			logger.println(LogLevel.ERROR,"작업중에러가 발생되었습니다.    ###################",e.toString(),e);
			szMsg="소재차량 출발 실적 처리 Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
	   }
		
		szMsg="소재차량 출발 실적 처리("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);
		
		return isSuccess;
	}// end of procMatlCarLev()
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 소재차량 도착 실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public boolean procMatlCarArr(JDTORecord msgRecord)throws DAOException  {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
//		TC_CODE : TSYDJ003
		 String szMsg           = "";
		 String szMethodName    = "procMatlCarArr";
		 JDTORecord Paramrecord = null;
		 JDTORecord msgOutRecord= null;
		 String szTRN_EQP_CD    = "";
		 String szARR_WLOC_CD 	= "";
		 String szARR_YD_PNT_CD = "";
		 String szmethod_nm 	= "";
		 String szGlo_Sch_Call 	= "";
		 ymCommonDAO dao = ymCommonDAO.getInstance();
			String szRcvTcCode=YmCommonUtil.getTcCode(msgRecord);
			if(szRcvTcCode==null){
				szMsg=szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				
				return false;
			}
			if(bDebugFlag){
				szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
			}
			
		try{
			
			szTRN_EQP_CD            = YmCommonUtil.paraRecChkNull(msgRecord, "TRN_EQP_CD");
			szARR_WLOC_CD 			= YmCommonUtil.paraRecChkNull(msgRecord, "ARR_WLOC_CD");
			szARR_YD_PNT_CD 		= YmCommonUtil.paraRecChkNull(msgRecord, "ARR_YD_PNT_CD");
			
			msgOutRecord = JDTORecordFactory.getInstance().create(); 
			szMsg="소재차량 도착 처리("+szMethodName+":"+szTRN_EQP_CD+") 시작";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);
			
			
	    	//장애 발생시 이전 소스로 원복 하기 위한 조치
			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklistTS";
		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});

		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
	    	if(CHK.equals("Y")){
	    		
	    		if(szARR_WLOC_CD.equals("D2Y44")||
	 				   szARR_WLOC_CD.equals("D2Y45")||
	 				   szARR_WLOC_CD.equals("D3Y41")||
	 				   szARR_WLOC_CD.equals("D3Y42")){
	    			szmethod_nm ="procMatlCarArrCoil";
	    		}else{
	    			szmethod_nm ="procMatlCarArrNew";
	    		}
				 		
	    	}else{
	    		szmethod_nm ="procMatlCarArrNew";
	    	}
	    	
			//차량 도착처리
 			EJBConnector ejbConn = new EJBConnector("default","JNDITsInfoReg",this);
 			msgOutRecord		 = (JDTORecord)ejbConn.trx( szmethod_nm,
 															new  Class[]{JDTORecord.class},
 															new Object[]{msgRecord });
	    	
 			szMsg="소재차량 도착 처리 결과값:"+YmCommonUtil.paraRecChkNull(msgOutRecord, "chk");
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);
			
			
			
 				
 			if(YmCommonUtil.paraRecChkNull(msgOutRecord, "chk").equals("1")){
	 			szmethod_nm         = YmCommonUtil.paraRecChkNull(msgOutRecord, "method_nm");
		    	szGlo_Sch_Call   	= YmCommonUtil.paraRecChkNull(msgOutRecord, "Glo_Sch_Call");
		    	
		    	szMsg="소재차량 도착 처리 크레인스케줄 생성 ("+szmethod_nm+":"+szGlo_Sch_Call+":"+szTRN_EQP_CD+") 시작 ";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);
				
		    	if(!"".equals(szmethod_nm)){
			       	//스케쥴 생성 
		 			EJBConnector ejbConn2 = new EJBConnector("default","JNDICraneSchReg",this);
		 			Boolean isTrue2		  = (Boolean)ejbConn2.trx( szmethod_nm,
		 															new  Class[]{String.class},
		 															new Object[]{szGlo_Sch_Call.trim() });
		    	}
		    	
	 			szMsg="소재차량 도착 처리 크레인스케줄 생성 ("+szmethod_nm+":"+szTRN_EQP_CD+") 종료 ";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);
 			}
 			
 			//B-CAST는 생략
 			if(!"D2Y43".equals(szARR_WLOC_CD)){
 					
	 			//L-2 차량 출발 정보 전송 #################################################################
	 			Paramrecord = JDTORecordFactory.getInstance().create(); 
	 			Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
	 			Paramrecord.setField("ARR_WLOC_CD", 	szARR_WLOC_CD);//착지개소코드
	 			Paramrecord.setField("ARR_YD_PNT_CD", 		szARR_YD_PNT_CD);
	 			procMatlCarArrPntRequestL2(Paramrecord , "D") ; 	
	 			//####################################################################################3

 			}
 			
 			szMsg="소재차량 도착 처리("+szMethodName+":"+szTRN_EQP_CD+") 완료";
 			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);

 			return true;
 			
	}catch(Exception e){
		
		szMsg="소재차량 도착 처리 Error:" +e.getMessage();
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
		m_ctx.setRollbackOnly();
		throw new DAOException(szMsg);
	}
	

	

}// end of procMatlCarArr()
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 소재차량 도착 실적(코일)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord procMatlCarArrCoil(JDTORecord msgRecord)throws DAOException {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		YdStockDAO ydStockDAO = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO = new YdWBookDAO();
		ymCommonDAO dao = ymCommonDAO.getInstance();
	    
	    String szMsg           		= "";
	    String szMethodName    		= "procMatlCarArrCoil";	    
	    String queryID         		= "";
	    String szTRN_EQP_CD    		= "";
	    String szTRN_WRK_FULLVOID_GP= "";
	    String szARR_WLOC_CD 		= "";
	    String szARR_YD_PNT_CD 		= "";
	    String szTRN_EQP_GP 		= "";
	    String trnQueryId  			= "";
	    String stkQueryId 			= "";
	    String s_STOCK_ID  			= "";
	    String s_LAYER 				= ""; 
	    String s_BED 				= "";
	    String sStkClo 				= ""; 
	    String s_STACK_YD_GP 		= ""; 
	    String s_STACK_BAY_GP 		= "";
	    String wbook_ID 			= "";
	    String trnEqpQueryId 		= "";
	    String s_ARR_WLOC_CD 		= "";	    
	    String sStkQty 				= "";
	    String sSchCode 			= "";
	    String szTRN_EQP_STK_CAPA 	= "";
	    String sLoopIndex1 			= "";
    	String sLoopIndex2 			= "";
    	String layerState 			= "";
		String s_STL_APPEAR_GP		= "";
		String s_YD_CAR_SCH_ID		= "";
		String wBookid2				= "";
    	
	    List FrtoProductList = null;
	    List FrtostlList 	= null;
	    List StkColList 	= null;
	    List StkStlList 	= null;
	    List lCarStockList	= new ArrayList();
	    
		int count 		= 0;
		int iSeq 		= 0;
		int iLoadMax 	= 0;
    	int iLoadCur 	= 0;	    	
    	
    	long lTotalWt	= 0;
    	long lPerWt		= 0;
    	long lCarMaxWt	= 0;

		JDTORecord wBookSel		= null;
		JDTORecord Paramrecord = null;
		JDTORecord msgOutRecord = null;

	    try{
	    	szTRN_EQP_CD            = YmCommonUtil.paraRecChkNull(msgRecord, "TRN_EQP_CD");
	    	szTRN_WRK_FULLVOID_GP   = YmCommonUtil.paraRecChkNull(msgRecord, "TRN_WRK_FULLVOID_GP");
	    	szARR_WLOC_CD 			= YmCommonUtil.paraRecChkNull(msgRecord, "ARR_WLOC_CD");
	    	szARR_YD_PNT_CD 		= YmCommonUtil.paraRecChkNull(msgRecord, "ARR_YD_PNT_CD");	    	
	    	szTRN_EQP_STK_CAPA		= YmCommonUtil.paraRecChkNull(msgRecord, "TRN_EQP_STK_CAPA");
	    	szTRN_EQP_GP 			= szTRN_EQP_CD.substring(1, 3);//PT/TR 구분
	    
    	
	    	msgOutRecord = JDTORecordFactory.getInstance().create(); 
 			msgOutRecord.setField("chk", 		"1");		//정상
	    	{ 
	    		/**
	    		 *  AB열연 야드개소코드 체크 
	    		 *  공차 : 착지개소
	    		 *  영차 : 발지개
	    		 */
	    		boolean IsABLoc = false; 
	    		
	    		logger.println(LogLevel.DEBUG,this,"=체크개소코드 => "+ szARR_WLOC_CD);   
	    		
	    		IsABLoc = this.getABLocationInfo_02(szARR_WLOC_CD);
		    	
		    	if(!IsABLoc){
		    		szMsg = "착지 개소코드 오류";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					msgOutRecord.setField("chk", 		"0");		//오류
					return msgOutRecord;
				}
	    	}	
	   
 	
	    	
	    	{ 
	    		/**
	    		 *  도착 포인트코드가 대기장(1Z99)인 경우 도착처리 안함
	    		 */	
	    		if(szARR_YD_PNT_CD.equals("1Z99"))
	    		{
	    		logger.println(LogLevel.DEBUG,this,"=도착포인트코드 => "+ szARR_YD_PNT_CD); 
	    		logger.println(LogLevel.DEBUG,this,"<==============도착포인트코드가 대기장임 ===============> ");   
	    		
	    		msgOutRecord.setField("chk", 		"0");		//오류
	    		return msgOutRecord;
	    		}
		   
	    	}
	    	
	    	
	    	/*
	    	 * 예약으로 잡혀있는정보 Clear
	    	 */
	          trnEqpQueryId 		= "ym.tsinfo.updateEquipcolStatArr";
	          iSeq = dao.updateData(trnEqpQueryId, new Object[]{szTRN_EQP_CD}); 	  	           	  	            
    
	          
	        //차량저장위치 포인트에 입동유무 체크 
	        queryID = "ym.tsinfo.stackcolpointchk";
	        StkStlList = dao.getCommonList(queryID, new Object[]{szARR_WLOC_CD,szARR_YD_PNT_CD,szTRN_EQP_GP });
	        
	        if(StkStlList.size() <= 0){
	        	logger.println(LogLevel.DEBUG,this,"=확인대상[☎☎☎☎CHITO☎☎☎☎]=>1.차량저장위치 포인트에 입동유무 체크 ");
	        	msgOutRecord.setField("chk", 		"0");		//오류
	    		return msgOutRecord;
	        }

	    	//차량저장위치 점유	  
	    	queryID = "ym.tsinfo.updateLayerstat_01";
	    	count = dao.updateData(queryID, new Object[]{	"L",szTRN_EQP_CD,"","",
	    													szARR_WLOC_CD,
	    													szARR_YD_PNT_CD,
	    													szTRN_EQP_GP}); 
	    	
	    	//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
		    CarPointinforeg("4","",szTRN_EQP_CD,"",szARR_WLOC_CD,szARR_YD_PNT_CD,"L");
	    	
	    	queryID = "ym.tsinfo.getListstlQty";
	    	StkStlList = dao.getCommonList(queryID, new Object[]{szTRN_EQP_CD});	    	
	    	JDTORecord StkStlRec = (JDTORecord)StkStlList.get(0);
	    	sStkQty  = StringHelper.evl(StkStlRec.getFieldString("QTY"), "");
	    	logger.println(LogLevel.DEBUG,this,"<==============예약된 재료 갯수 ===============> " + sStkQty); 
	    	
	    	if(sStkQty.equals("0")){
	    		
	    		logger.println(LogLevel.DEBUG,this,"<==============예약된 정보없음 =============== 모든 단정보 오픈> "); 
 	
	    	    layerState = "O";
	    	    queryID = "ym.tsinfo.updateLayerstat";
	    	    count = dao.updateData(queryID, new Object[]{layerState,
	    													szARR_WLOC_CD,
	    													szARR_YD_PNT_CD,
	    													szTRN_EQP_GP}); 
    	   
	    	}
	    	else
	    	{
	    		logger.println(LogLevel.DEBUG,this,"<==============예약된 정보있음 =============== 예약된 재료 갯수만큼  단정보 오픈> "); 
	    		layerState = "O";
		    	queryID = "ym.tsinfo.updateLayerstat_Qty";
		    	count = dao.updateData(queryID, new Object[]{layerState,
	    													szARR_WLOC_CD,
	    													szARR_YD_PNT_CD,
	    													szTRN_EQP_GP,
	    													sStkQty}); 
	    	}
	    	
	    	//실제 도착위치 업데이트 및 영차도착시 맵정보 생성을 위해
    		queryID = "ym.tsinfo.getListStkColgp";
    		StkColList = dao.getCommonList(queryID, new Object[]{szARR_WLOC_CD,szARR_YD_PNT_CD,szTRN_EQP_GP});
    		if(StkColList.size() > 0){
		    	JDTORecord StkColRec = (JDTORecord)StkColList.get(0);
		    	sStkClo  = StringHelper.evl(StkColRec.getFieldString("STACK_COL_GP"), "");
		    	s_STACK_YD_GP =sStkClo.substring(0, 1);
		    	s_STACK_BAY_GP = sStkClo.substring(1, 2);//PT/TR 구분
    		}
    		
//####################################################################################################################################
//영차인경우
	    	if(szTRN_WRK_FULLVOID_GP.equals("F"))
			{    		
	    		logger.println(LogLevel.DEBUG,this,"=도착실적처리=>영차처리 시작◑◑◑◑◑◑◑◑◑◑◑◑◑◑");	
		    	

	    		//차량스케쥴ID로 이송재료 조회
	    		trnQueryId = "ym.tsinfo.getListFrtostlListsangcha";
	    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{szTRN_EQP_CD});
	    		int iSeqCount2 	= FrtostlList.size();
	    		
	    		if(iSeqCount2 <= 0){
		    		logger.println(LogLevel.DEBUG,this,"=도착실적처리=>영차처리 대상재가 존재 안함 (ym.tsinfo.getListFrtostlList)");	   
		    		logger.println(LogLevel.DEBUG,this,"=확인대상[☎☎☎☎CHITO☎☎☎☎]=>1.작업예약 존재 유무 ,2.차량취소상태유무,3:차량재료상태 존재 유무");	 
		        	msgOutRecord.setField("chk", 		"0");		//오류
		    		return msgOutRecord;
		        }else{
	        		JDTORecord TolocRec 	= (JDTORecord)FrtostlList.get(0);	
	        		s_STL_APPEAR_GP 	= StringHelper.evl(TolocRec.getFieldString("STL_APPEAR_GP"),"");    		
	        		s_YD_CAR_SCH_ID		= StringHelper.evl(TolocRec.getFieldString("YD_CAR_SCH_ID"),"");
	        	}	
	    		
	    		//스케줄코드 생성   
	    		if(s_STL_APPEAR_GP.equals("Y")){
	    			// 제품
	    			sSchCode = getUnloadSchKind(YmCommonConst.ITEM_CG,sStkClo);	    			
	    		}else{
	    			//소재
	    			sSchCode = getUnloadSchKind(YmCommonConst.ITEM_CM,sStkClo);    			
	    		}
	    		
	    		
	    		//작업예약 생성 작업***********************************************************

    			//작업예약id 생성	
    			queryID 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";								
				wBookSel 		= ydStackLayerDAO.requestFind(queryID);
				wbook_ID      	= wBookSel.getFieldString("WBOOK_ID");
		    		
				//작업예약테이블에 insert
				queryID 	= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";				
				iSeq = ydWBookDAO.requestinsertData(queryID, new Object[]{ wbook_ID, 
																				 s_STACK_YD_GP, 
																				 s_STACK_BAY_GP,
																				 sSchCode, 
																				 YmCommonUtil.getWorkDuty(),
																				 YmCommonUtil.getWorkParty()});
	    		
    			//작업예약 생성 작업***********************************************************

		    	logger.println(LogLevel.DEBUG,this,"=실제작업할 동"+ s_STACK_BAY_GP);
		    	logger.println(LogLevel.DEBUG,this,"=실제작업할 작업예약ID"+ wbook_ID);
		    	
	    		//야드맵 생성
                for(int i=0; i < iSeqCount2 ; i++){
					
		    		JDTORecord FrtoSltrec = (JDTORecord)FrtostlList.get(i);
					s_STOCK_ID = StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"),"");
					s_BED = StringHelper.evl(FrtoSltrec.getFieldString("YD_STK_BED_NO"),"");
					s_LAYER = StringHelper.evl(FrtoSltrec.getFieldString("STK_LYR"),"");
					
					//저장품테이블에 update				
					stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
					iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ wbook_ID, 
																				  YmCommonConst.NEW_STOCK_MOVE_TERM_CS,
																				  s_STOCK_ID});
					//적치단  Table Update(작업요구상태='S'로 변경)					
					stkQueryId = "ym.tsinfo.YdStockDAO.updateYdStkLayerGp_Coil";
					iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID, 
							                                                      sStkClo,
							                                                      s_BED,
							                                                      s_LAYER});					
					
		    	} 
                
		    	//차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)
	    		stkQueryId = "ym.tsinfo.YdStockDAO.updateArrDt5";
				iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{sStkClo ,szARR_YD_PNT_CD ,wbook_ID ,szTRN_EQP_CD});
		    	
	 			msgOutRecord.setField("method_nm", 		"callCraneSchInfo");	//method 명
	 			msgOutRecord.setField("Glo_Sch_Call", 	wbook_ID);				//작업예약번호
	 			
	 			logger.println(LogLevel.DEBUG,this,"=도착실적처리=>영차처리 종료◑◑◑◑◑◑◑◑◑◑◑◑◑◑");	    	
			}
	    	
//###########################################################################################################################################
//공차인경우
	    	else if(szTRN_WRK_FULLVOID_GP.equals("E"))
			{
	    		logger.println(LogLevel.DEBUG,this,"=도착실적처리=>공차처리 시작◑◑◑◑◑◑◑◑◑◑◑◑◑◑");	
	    		
	    		//대상재 조회
		    	queryID 	= "ym.ilkwan.dao.YdStockDAO.getListFrtoStl_CoilNewsangcha";
		    	FrtoProductList = dao.getCommonList(queryID, new Object[]{	szTRN_EQP_CD,
		    																s_STACK_YD_GP+s_STACK_BAY_GP ,
		    																szARR_WLOC_CD,
		    																szTRN_EQP_CD,
		    																szTRN_EQP_CD,
		    																szARR_WLOC_CD});
		    	int iSeqCount=FrtoProductList.size();
		    	
		    	if(iSeqCount <= 0){
		    		logger.println(LogLevel.DEBUG,this,"=도착실적처리=>공차처리 대상재가 존재 안함 (ym.ilkwan.dao.YdStockDAO.getListFrtoStl_CoilNewsangcha)");	    
		    		logger.println(LogLevel.DEBUG,this,"=확인대상[☎☎☎☎CHITO☎☎☎☎]=>1.작업예약 존재 유무 ,2.차량취소상태유무,3:이송대상재 존재 유무 ,4:구내운송 장비상태");	 
		        	msgOutRecord.setField("chk", 		"0");		//오류
		    		return msgOutRecord;
		        }else{
	        		JDTORecord TolocRec 	= (JDTORecord)FrtoProductList.get(0);	
	        		s_STL_APPEAR_GP 	= StringHelper.evl(TolocRec.getFieldString("STL_APPEAR_GP"),"");    		
	        		s_YD_CAR_SCH_ID		= StringHelper.evl(TolocRec.getFieldString("YD_CAR_SCH_ID"),"");
	        	}		
		    	
		    	//스케줄코드 생성   
		    	if(s_STL_APPEAR_GP.equals("Y")){
		    		// 제품
		    		 sSchCode	= YmCommonConst.NEW_SCH_WORK_KIND_GVML ; 
		    		 sSchCode = getSchWorkKind(sSchCode, sStkClo);		    		 
		    	 }else{
		    		 //소재
		    		 sSchCode	= YmCommonConst.NEW_SCH_WORK_KIND_CVML ;
		    		 sSchCode = getSchWorkKind(sSchCode, sStkClo);
		    	 }
	    		
	    		
	    		//작업예약 생성 작업***********************************************************
	   			//작업예약id 생성	
	   			queryID 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";								
				wBookSel 		= ydStackLayerDAO.requestFind(queryID);
				wbook_ID      	= wBookSel.getFieldString("WBOOK_ID");
		    		
				//작업예약테이블에 insert
				queryID 	= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";				
				iSeq = ydWBookDAO.requestinsertData(queryID, new Object[]{ wbook_ID, 
																		 s_STACK_YD_GP, 
																		 s_STACK_BAY_GP,
																		 sSchCode, 
																		 YmCommonUtil.getWorkDuty(),
																		 YmCommonUtil.getWorkParty()});
	    		
				//작업예약 생성 작업***********************************************************
				
	    		/*
	    		 * 1. 차량장비 MAX 중량정보를 가져온다.
	    		 */
	    		if(szTRN_EQP_GP.equals("TR"))
	    		{
	    			try{
	    				lCarMaxWt = Long.parseLong(szTRN_EQP_STK_CAPA);
	    			}catch(Exception e){
	    				lCarMaxWt = 60000;
	    			}
	    			iLoadMax = 4;	
	    		}
	    		else 
	    		{
	    			try{
	    				lCarMaxWt = Long.parseLong(szTRN_EQP_STK_CAPA);
	    			}catch(Exception e){
	    				lCarMaxWt = 180000;
	    			}
		    		iLoadMax = 6;	
	    		}
	    		
	    		//상차Lot편성 대상재정보를 가지고 Lot를 편성한다.
	    		for(int index=0; index < iSeqCount ; index++){
	    			
					JDTORecord FrtoProduct2 = (JDTORecord)FrtoProductList.get(index);
					
					sLoopIndex2	 = "";
					sLoopIndex2	+= StringHelper.evl(FrtoProduct2.getFieldString("소재제품우선순위"),"");
					sLoopIndex2	+= StringHelper.evl(FrtoProduct2.getFieldString("현재동우선순위"),"");
		    		sLoopIndex2	+= StringHelper.evl(FrtoProduct2.getFieldString("착지개소우선순위"),"");
		    		sLoopIndex2	+= StringHelper.evl(FrtoProduct2.getFieldString("목표동우선순위"),"");
		    		
		    		logger.println(LogLevel.DEBUG,this,"=재료그룹속성1 => "+ sLoopIndex1);   
		    		logger.println(LogLevel.DEBUG,this,"=재료그룹속성2 => "+ sLoopIndex2);   
		    		
		    		if(index == 0 ){
		    			sLoopIndex1 = sLoopIndex2;
		    		}else{
		    			if(sLoopIndex1.equals(sLoopIndex2)){		    				
		    			}else{
		    				break;
		    			}
		    		}
		    		
		    		//같이 편성할 수 있는 Lot편성 대상재이면 차량Max중량과 비교해서 Over하는지 체크
		    		lPerWt	= Long.parseLong(StringHelper.evl(FrtoProduct2.getFieldString("STK_WT"),"0"));
		    		
		    		if(s_STL_APPEAR_GP.equals("Y")){
		    			logger.println(LogLevel.DEBUG,this,"=제품 상차LOT대상재 편성(차량 중량체크 안함) => OK"); 		    			
		    			lCarStockList.add(FrtoProduct2);
		    			lTotalWt += lPerWt;
		    			iLoadCur++;				
		    			if(iLoadMax <= iLoadCur){
		    				break;
		    			}
		    		}else{
			    		if(lCarMaxWt >= lTotalWt + lPerWt){		    			
			    			logger.println(LogLevel.DEBUG,this,"=소재 상차LOT대상재 편성 => OK"); 		    			
			    			lCarStockList.add(FrtoProduct2);
			    			lTotalWt += lPerWt;
			    			iLoadCur++;				
			    			if(iLoadMax <= iLoadCur){
			    				break;
			    			}
			    		}
		    		}
		    	}
		    	
		    	logger.println(LogLevel.DEBUG,this,"=iLoadCur AS-IS => "+ iLoadCur);   
		    	
		    	//2010.07.14  보안관리팀에 따른 트레일러 4매이상 상차 불가 처리
		    	if(iLoadCur >= 4 && szTRN_EQP_GP.equals("TR")){
		    		iLoadCur =4 ;
		    	}
		    	logger.println(LogLevel.DEBUG,this,"=iLoadCur TO-BE => "+ iLoadCur);  
		    	
		    	logger.println(LogLevel.DEBUG,this,"=lCarMaxWt => "+ lCarMaxWt);   
		    	logger.println(LogLevel.DEBUG,this,"=lTotalWt => "+ lTotalWt);   
		    	
		    	for(int index=0; index < iLoadCur ; index++){
					
		    		JDTORecord FrtoProduct3 = (JDTORecord)lCarStockList.get(index);
					s_STOCK_ID = StringHelper.evl(FrtoProduct3.getFieldString("STOCK_ID"),"");
					s_ARR_WLOC_CD = StringHelper.evl(FrtoProduct3.getFieldString("ARR_WLOC_CD"),"");
					
					//이전 작업예약 존재 유무 체크  
					trnQueryId 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID3";
		    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{s_STOCK_ID});
		 
		    		JDTORecord TolocRecCoil = (JDTORecord)FrtostlList.get(0);
		    		wBookid2 = StringHelper.evl(TolocRecCoil.getFieldString("WBOOK_ID"),"");
		    		
		    		if(!wBookid2.equals("")){
		    			stkQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteWbookInfo";
							iSeq = ydStockDAO.deleteData(stkQueryId, new Object[]{wBookid2});
		    		}
					
					//저장품테이블에 update				
					stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
					iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ wbook_ID, 
																				  YmCommonConst.NEW_STOCK_MOVE_TERM_CS,s_STOCK_ID});
																				  
					//적치단  Table Update(작업요구상태='S'로 변경)
	            	String sQuery4 = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
	            	iSeq = ydStockDAO.requestupdateData(sQuery4, new Object[]{	YmCommonConst.STACK_LAYER_STAT_S, s_STOCK_ID });
	            	
	            	//차량대상재 테이블에 insert				    		
		    		logger.println(LogLevel.DEBUG,this,"=출발실적처리=>차량스케쥴 대상재 등록 ");   
					trnEqpQueryId = "ym.tsinfo.insertCarftmtl";
		    		iSeq = dao.insertData(trnEqpQueryId, new Object[]{ s_YD_CAR_SCH_ID,s_STOCK_ID}); 
		    	}		
		    	
		    	//차량스케쥴 update(도착시간, 실제도착위치 업데이트처리)
	    		stkQueryId = "ym.tsinfo.YdStockDAO.updateArrDt4";
				iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{wbook_ID ,
																			s_ARR_WLOC_CD ,
																			szARR_YD_PNT_CD , 
																			sStkClo,
																			szTRN_EQP_CD});
				
		    	
		    	queryID = "ym.tsinfo.updateWbookInfo";
		    	count = dao.updateData(queryID, new Object[]{	szARR_WLOC_CD,
																szARR_YD_PNT_CD,
																szTRN_EQP_GP,
																wbook_ID}); 
			    	
	 			msgOutRecord.setField("method_nm", 		"callCraneSchInfo");		//method 명
	 			msgOutRecord.setField("Glo_Sch_Call", 	wbook_ID);	//작업예약번호
	 			
	 			logger.println(LogLevel.DEBUG,this,"=도착실적처리=>공차처리 종료◑◑◑◑◑◑◑◑◑◑◑◑◑◑◑◑");
	
			}	
	    				
            //포인트점유사항 출하송신
			Paramrecord = JDTORecordFactory.getInstance().create(); 
			Paramrecord.setField("WLOC_CD", 		szARR_WLOC_CD);//
			Paramrecord.setField("YD_PNT_CD", 	szARR_YD_PNT_CD );//
			Paramrecord.setField("YD_PNT_OCPY_GP", 		"A");
			Paramrecord.setField("YD_PNT_UNIT_CL_GP", 		"C");
			Paramrecord.setField("LOAN_PULLOUT_ABLE_YN", 		"N");
			Paramrecord.setField("OCPY_TRN_EQP_CD", szTRN_EQP_CD);
			this.procMatlCarArrPntSenddm(Paramrecord);
			

		}catch(Exception e){
			
			szMsg="소재차량 도착 처리 Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}


		return msgOutRecord;
	}// end of procMatlCarArrCoil()
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 소재차량 도착 실적(sub)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord procMatlCarArrNew(JDTORecord msgRecord)throws DAOException {

		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		YdStockDAO ydStockDAO = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO = new YdWBookDAO();
		ymCommonDAO dao = ymCommonDAO.getInstance();
	    int intRtnVal 		   = 0 ;
	    
	    String szMsg           = "";
	    String szMethodName    = "procMatlCarArrNew";
	    
	    String szQuery         = "";
	    String queryID         = "";

	    String szTRN_EQP_CD    = "";
	    String szTRN_WRK_FULLVOID_GP = "";
	    String szARR_WLOC_CD = "";
	    String szARR_YD_PNT_CD = "";
	    String szTRN_EQP_GP = "";
	    
	    String sCarSchCode = "";
	    String trnQueryId  = "";
	    String stkQueryId 		= "";
	    String s_STOCK_ID  = "";
	    String s_LAYER = ""; 
	    String s_BED = "";
	    String sStkClo = ""; 
	    String sStkBay = "";
	    String wbook_ID = "";
	    String method_nm = "";
	    String trnEqpQueryId = "";
	    String sStkQty = "";
	    String pos = "";
	    
	    List FrtoProductList = null;
	    List FrtostlList = null;
	    List StkColList = null;
	    List StkStlList = null;
	    List TolocList = null;
		String wBookid      	= "";
		String Glo_Sch_Call   = "";
		int count = 0;
		int iSeq = 0;
		String layerState = "";
		
		JDTORecord wBookSel		= null;
		JDTORecord Paramrecord = null;
		JDTORecord msgOutRecord = null;

	    try{
	    	szTRN_EQP_CD            = YmCommonUtil.paraRecChkNull(msgRecord, "TRN_EQP_CD");
	    	szTRN_WRK_FULLVOID_GP   = YmCommonUtil.paraRecChkNull(msgRecord, "TRN_WRK_FULLVOID_GP");
	    	szARR_WLOC_CD 			= YmCommonUtil.paraRecChkNull(msgRecord, "ARR_WLOC_CD");
	    	szARR_YD_PNT_CD 		= YmCommonUtil.paraRecChkNull(msgRecord, "ARR_YD_PNT_CD");
	    	szTRN_EQP_GP 			= szTRN_EQP_CD.substring(1, 3);//PT/TR 구분
	    
//	    	B-CAST 트레일러 이면서 상차지도착 인경우 PT처럼 처림 함.
	    	if(szTRN_EQP_GP.equals("TR") && szARR_WLOC_CD.equals("D2Y43") && szTRN_WRK_FULLVOID_GP.equals("E")){
	    		szTRN_EQP_GP ="PT";
	    	}
	    	
	    	msgOutRecord = JDTORecordFactory.getInstance().create(); 
 			msgOutRecord.setField("chk", 		"1");		//정상
	    	{ 
	    		/**
	    		 *  AB열연 야드개소코드 체크 
	    		 *  공차 : 착지개소
	    		 *  영차 : 발지개
	    		 */
	    		boolean IsABLoc = false; 
	    		
	    		logger.println(LogLevel.DEBUG,this,"=체크개소코드 => "+ szARR_WLOC_CD);   
	    		
	    		IsABLoc = this.getABLocationInfo_02(szARR_WLOC_CD);
		    	
		    	if(!IsABLoc){
		    		szMsg = "착지 개소코드 오류";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					msgOutRecord.setField("chk", 		"0");		//오류
					return msgOutRecord;
				}
	    	}	
	   
 	
	    	
	    	{ 
	    		/**
	    		 *  도착 포인트코드가 대기장(1Z99)인 경우 도착처리 안함
	    		 */	
	    		if(szARR_YD_PNT_CD.equals("1Z99"))
	    		{
	    		logger.println(LogLevel.DEBUG,this,"=도착포인트코드 => "+ szARR_YD_PNT_CD); 
	    		logger.println(LogLevel.DEBUG,this,"<==============도착포인트코드가 대기장임 ===============> ");   
	    		logger.println(LogLevel.DEBUG,this,"<==============도착포인트코드가 대기장임 ===============> ");   
	    		
	    		msgOutRecord.setField("chk", 		"0");		//오류
	    		return msgOutRecord;
	    		}
		   
	    	}
	    	
	    	
	    	/*
	    	 * 예약으로 잡혀있는정보 Clear
	    	 */
	          trnEqpQueryId 		= "ym.tsinfo.updateEquipcolStatArr";
	          iSeq = dao.updateData(trnEqpQueryId, new Object[]{szTRN_EQP_CD}); 	  	           	  	            
	            
	            /*
	             * UPDATE TB_YM_STACKCOL
                   SET STACK_STAT = '',
                   CAR_CARD_NO = '',
                   MODIFIER = 'TSYDJ003',
                   MOD_DDTT = sysdate
                   WHERE CAR_CARD_NO = :CAR_CARD_NO
	           */
	          
	          //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			  CarPointinforeg("1","",szTRN_EQP_CD,"","","","C"); 
	    
	  
    		//도착시 차량 포인트에 차량 존재 유무 체크
    		trnQueryId = "ym.tsinfo.getListStackcolpointList";
    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{szARR_WLOC_CD,szARR_YD_PNT_CD});
    		/*
			    SELECT *
			 FROM USRYMA.TB_YM_STACKCOL
			 WHERE WLOC_CD =:V_WLOC_CD
			  AND YD_PNT_CD =:V_YD_PNT_CD
			  AND TRN_EQP_CD IS NOT NULL
    		 */
           
    		if( FrtostlList.size() > 0 ){
	    		logger.println(LogLevel.DEBUG,this,"=도착시 차량 포인트에 차량 존재 유무 체크 => "+ szARR_WLOC_CD+" 포인트:"+szARR_YD_PNT_CD+"에 장비가 존재 합니다." ); 
   		
	    		msgOutRecord.setField("chk", 		"0");		//오류
	    		return msgOutRecord;
    		}
	    		
	          
	    	/*
             * 20090801_YJK
             * 향후 포인트 점유실적 I/F 추가
             */
	    	
	    	//야드맵 오픈	  
	    	queryID = "ym.tsinfo.updateLayerstat_01";
	    	count = dao.updateData(queryID, new Object[]{	"L",szTRN_EQP_CD,"","",
	    													szARR_WLOC_CD,
	    													szARR_YD_PNT_CD,
	    													szTRN_EQP_GP}); 
	    	/*
	    	UPDATE  TB_YM_STACKCOL
            SET     YD_CAR_USE_GP   = :YD_CAR_USE_GP,
                    TRN_EQP_CD      = :TRN_EQP_CD,
                    CAR_NO          = :CAR_NO,
                    CARD_NO         = :CARD_NO
            WHERE WLOC_CD   = :WLOC_CD
            AND   YD_PNT_CD = :YD_PNT_CD
            AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
			*/
	    	
	    	//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
		    CarPointinforeg("4","",szTRN_EQP_CD,"",szARR_WLOC_CD,szARR_YD_PNT_CD,"L");
	    	
	    	
	    	queryID = "ym.tsinfo.getListstlQty";
	    	StkStlList = dao.getCommonList(queryID, new Object[]{szTRN_EQP_CD});	    	
	    	JDTORecord StkStlRec = (JDTORecord)StkStlList.get(0);
	    	sStkQty  = StringHelper.evl(StkStlRec.getFieldString("QTY"), "");
	    	logger.println(LogLevel.DEBUG,this,"<==============예약된 재료 갯수 ===============> " + sStkQty); 
	    	/*
	    	select count(*) Qty
	    	from(
	    	select B.STL_NO
	    	from TB_YD_CARSCH A, TB_YD_CARFTMVMTL B
	    	where A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
	    	and A.TRN_EQP_CD = ?
	    	AND A.YD_CAR_PROG_STAT = 'A'
	    	AND (A.DEL_YN IS NULL OR A.DEL_YN <> 'Y'))
	    	*/
	    	
	    	if(sStkQty.equals("0")){
	    		
	    		logger.println(LogLevel.DEBUG,this,"<==============예약된 정보없음 =============== 모든 단정보 오픈> "); 
 	
	    	   layerState = "O";
	    	   queryID = "ym.tsinfo.updateLayerstat";
	    	   count = dao.updateData(queryID, new Object[]{layerState,
	    													szARR_WLOC_CD,
	    													szARR_YD_PNT_CD,
	    													szTRN_EQP_GP}); 
	    	/*
	    	 * UPDATE TB_YM_STACKLAYER
               SET STACK_LAYER_ACTIVE_STAT = ?   
               WHERE STACK_COL_GP = (SELECT  A.STACK_COL_GP	    	 		
					                 FROM TB_YM_STACKCOL A
				                    	WHERE  A.WLOC_CD = ?
				                    	          AND A.YD_PNT_CD = ?
                                                  AND SUBSTR(A.STACK_COL_GP,3,2) = :TRN_EQP_GP
					                     )
	    	 */	    	   
	    	}
	    	else
	    	{
	    		logger.println(LogLevel.DEBUG,this,"<==============예약된 정보있음 =============== 예약된 재료 갯수만큼  단정보 오픈> "); 
	    		layerState = "O";
		    	   queryID = "ym.tsinfo.updateLayerstat_Qty";
		    	   count = dao.updateData(queryID, new Object[]{layerState,
		    													szARR_WLOC_CD,
		    													szARR_YD_PNT_CD,
		    													szTRN_EQP_GP,
		    													sStkQty}); 
		    	/*
		    	 * UPDATE TB_YM_STACKLAYER
               SET STACK_LAYER_ACTIVE_STAT = 'O'   
               WHERE STACK_COL_GP = (SELECT  A.STACK_COL_GP	    	 		
					                 FROM TB_YM_STACKCOL A
				                    	WHERE  A.WLOC_CD = ?
				                    	AND A.YD_PNT_CD = ?
                                        AND SUBSTR(A.STACK_COL_GP,3,2) = ?
					                     )
			    AND TO_NUMBER(STACK_LAYER_GP) <= ?
						                     )
		    	 */
	    	}
	    	
	    	//실제 도착위치 업데이트 및 영차도착시 맵정보 생성을 위해
    		queryID = "ym.tsinfo.getListStkColgp";
    		StkColList = dao.getCommonList(queryID, new Object[]{szARR_WLOC_CD,szARR_YD_PNT_CD,szTRN_EQP_GP});
    		if(StkColList.size() > 0){
		    	JDTORecord StkColRec = (JDTORecord)StkColList.get(0);
		    	sStkClo  = StringHelper.evl(StkColRec.getFieldString("STACK_COL_GP"), "");
		    	sStkBay = sStkClo.substring(1, 2);//PT/TR 구분
    		}
    		/*
    		 * SELECT STACK_COL_GP
               FROM TB_YM_STACKCOL
               WHERE WLOC_CD = ?
               AND YD_PNT_CD = ? 
               AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
    		 */	 
    			    	
	    	
	    	//####################################################################################################################################
	    	//영차인경우
	    	if(szTRN_WRK_FULLVOID_GP.equals("F"))
			{    		
	    		logger.println(LogLevel.DEBUG,this,"=도착실적처리=>영차처리 시작");
	    	
	    			    					    		    				    	
	    		queryID = "ym.tsinfo.getListtrnEqpschU";
	    		/*
		    	 * SELECT YD_CARUD_WRK_BOOK_ID WBOOK_ID
		    	 *       ,YD_CAR_SCH_ID
	               FROM TB_YD_CARSCH
	               WHERE TRN_EQP_CD = ?
	               AND YD_CAR_PROG_STAT = 'A'
	               AND (DEL_YN IS NULL OR DEL_YN <> 'Y')
		    	 */
	    		
	    		FrtoProductList = dao.getCommonList(queryID, new Object[]{szTRN_EQP_CD});
	    		int iSeqCount 			= FrtoProductList.size();
		    	String[] tmpWbookID  	= new String[iSeqCount];
		    	
		    	if(iSeqCount == 0){
		    		logger.println(LogLevel.DEBUG,this,"=도착실적처리=>출발 상태 차량스케줄이 존재 안함");
		    		msgOutRecord.setField("chk", 		"0");		//오류
		    		return msgOutRecord;
		    	}
		    	
		    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
	    		wbook_ID = StringHelper.evl(FrtoProduct.getFieldString("WBOOK_ID"), "");
		    	tmpWbookID[0]  	= StringHelper.evl(FrtoProduct.getFieldString("WBOOK_ID"), "");
		    	Glo_Sch_Call  	= tmpWbookID[0].trim() +"-" ;	
		    	
		    	//실제 도착한 동으로 작업예약 업데이트
		    	logger.println(LogLevel.DEBUG,this,"=실제작업할 동"+ sStkBay);
		    	logger.println(LogLevel.DEBUG,this,"=실제작업할 작업예약ID"+ wbook_ID);
		    	
		    	/*
		    	update TB_YM_WBOOK
		    	set YD_GP = ?
		    	where WBOOK_ID = ?
		    	*/
		    	queryID = "ym.tsinfo.updateWbookBay";
		    	count = ydStockDAO.requestupdateData(queryID, new Object[]{sStkBay, wbook_ID}); 
		    	
		    	
		    	if(szARR_WLOC_CD.equals("D3Y43")||szARR_WLOC_CD.equals("D2Y43")){
		    	    Glo_Sch_Call  	= tmpWbookID[0].trim() +"-" ;	
		    	    method_nm = "syCraneScheduleInfoInsert";
		    	}
		    	else 
		    	{
		    		Glo_Sch_Call  	= tmpWbookID[0].trim();
		    		method_nm = "callCraneSchInfo";
		    	}
	    		
				
//		    	차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)
	    		stkQueryId = "ym.tsinfo.YdStockDAO.updateArrDt";
				iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{sStkClo,szTRN_EQP_CD});
				
		    	
	    		//차량스케쥴ID로 이송재료 조회
	    		trnQueryId = "ym.tsinfo.getListFrtostlList";
	    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{szTRN_EQP_CD});
	    		/*
	    		 * SELECT STL_NO,SUBSTR(YD_STK_LYR_NO,2,2)STK_LYR
                   FROM TB_YD_CARFTMVMTL A, TB_YD_CARSCH B
                   WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
                   AND A.DEL_YN = 'N'
                   AND B.TRN_EQP_CD = ?
                   ORDER BY A.YD_STK_LYR_NO
	    		 */

	    		int iSeqCount2 	= FrtostlList.size();

	    		//야드맵 생성
                for(int i=0; i < iSeqCount2 ; i++){
					
		    		JDTORecord FrtoSltrec = (JDTORecord)FrtostlList.get(i);
					s_STOCK_ID = StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"),"");
					s_BED = StringHelper.evl(FrtoSltrec.getFieldString("YD_STK_BED_NO"),"");
					s_LAYER = StringHelper.evl(FrtoSltrec.getFieldString("STK_LYR"),"");
					
					
					//B열연 도착의 경우 재료공통에 위치정보 업데이트(조업 정정마감처리)
				/*	if(szARR_WLOC_CD.equals("D3Y43"))
					{
						한매작업 - 주편공통에서 현재 진도코드 확인													
						queryID	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getMSLABCOMM";
						List FrtStockList2 = dao.getCommonList(queryID, new Object[]{s_STOCK_ID});
				    	JDTORecord FrtStockreq2 = (JDTORecord)FrtStockList2.get(0);
				    	
				    	String CurrProg_CD = StringHelper.evl(FrtStockreq2.getFieldString("RECORD_PROG_STAT"),"");
				    	
				    	if(CurrProg_CD.equals("3"))
				    	{
				    		재료공통 테이블 저장위치 업데이트(슬라브공통)
				    		String QueryId = "ym.facilitywork.putwrecord.session.updateStkarriveLoc_Slab";
							iSeq = ydStockDAO.requestupdateData(QueryId, new Object[]{ sStkClo,sStkClo,sStkClo,sStkClo,s_BED
									                                                   ,s_LAYER,sStkClo,s_BED,s_LAYER,s_STOCK_ID,s_STOCK_ID});			    		
				    	}
				    	else
				    	{
				    	   재료공통 테이블 저장위치 업데이트(주편공통)
				    		String QueryId = "ym.facilitywork.putwrecord.session.updateStkarriveLoc_MSlab";
				    		iSeq = ydStockDAO.requestupdateData(QueryId, new Object[]{ sStkClo,sStkClo,sStkClo,sStkClo,s_BED
                                                                                        ,s_LAYER,sStkClo,s_BED,s_LAYER,s_STOCK_ID,s_STOCK_ID});	
				    		
				    	}
						
					}*/
					
													
					stkQueryId = "ym.tsinfo.YdStockDAO.updateYdStkLayerGp_Coil";
					iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID, 
							                                                      sStkClo,
							                                                      s_BED,
							                                                      s_LAYER});					
					
		    	} 
                /*
                 * UPDATE TB_YM_STACKLAYER
                   SET STOCK_ID = ?
                  ,STACK_LAYER_ACTIVE_STAT = 'O'
                  ,STACK_LAYER_STAT = 'L'
                  WHERE STACK_COL_GP = ?
                  AND STACK_BED_GP = ?
                  AND STACK_LAYER_GP = ?
                 */
        
                  							           

		       	//스케쥴 생성 
//	 			EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
//	 			Boolean isTrue 		 = (Boolean)ejbConn.trx( method_nm,
//	 															new  Class[]{String.class},
//	 															new Object[]{Glo_Sch_Call.trim() });	
	 			
	 			msgOutRecord.setField("method_nm", 		method_nm);		//method 명
	 			msgOutRecord.setField("Glo_Sch_Call", 	Glo_Sch_Call);	//작업예약번호
	 			
	 			
			}
	    	
	    	//###########################################################################################################################################
	    	//공차인경우
	    	else if(szTRN_WRK_FULLVOID_GP.equals("E"))
			{
	    		   			    		
//		    	차량스케쥴 update(도착시간, 실제도착위치 업데이트처리)
	    		stkQueryId = "ym.tsinfo.YdStockDAO.updateArrDt_1";
				iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{sStkClo,szTRN_EQP_CD});
				
			
				//B-cast 슬라브야드 공차도착일 경우
				if(szARR_WLOC_CD.equals("D2Y43")||szARR_WLOC_CD.equals("D3Y43"))
				{
		    		
	    			queryID = "ym.tsinfo.getListtrnEqpschL_1";
	    		/*
		    	 * SELECT YD_CARLD_WRK_BOOK_ID WBOOK_ID
		    	 *        ,YD_CAR_SCH_ID
	                FROM TB_YD_CARSCH
	               WHERE TRN_EQP_CD = ?
	               AND YD_CAR_PROG_STAT = '2'
	               AND (DEL_YN IS NULL OR DEL_YN <> 'Y')
		    	 */	 
	    		FrtoProductList = dao.getCommonList(queryID, new Object[]{szTRN_EQP_CD});
	    		
	    		int iSeqCount 			= FrtoProductList.size();
		    	String[] tmpWbookID  	= new String[iSeqCount];
		    	
		    	if(iSeqCount == 0){
		    		logger.println(LogLevel.DEBUG,this,"=도착실적처리=>출발 상태 차량스케줄이 존재 안함");
		    		msgOutRecord.setField("chk", 		"0");		//오류
		    		return msgOutRecord;
		    	}
		    	
		    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
		    	tmpWbookID[0]  	= StringHelper.evl(FrtoProduct.getFieldString("WBOOK_ID"), "");
		    	String work_Booked_YN = StringHelper.evl(FrtoProduct.getFieldString("WBOOK_ID"), "");
		    	
    			logger.println(LogLevel.DEBUG,this,"=BCast상차작업예약여부 조회: "+ work_Booked_YN);
		    	
		    	      //작업예약이 있는경우(자동상차인 경우)
		    	     if(!"".equals(work_Booked_YN))
		    	     {
		    	    	 logger.println(LogLevel.DEBUG,this,"=BCast상차 ===> 자동상차편성, 공차도착처리 시 스케쥴 생성");
			    	      Glo_Sch_Call  	= tmpWbookID[0].trim() +"-" ;	
			    	      method_nm = "syCraneScheduleInfoInsert";
			    	  			   			    	
			    	      queryID = "ym.tsinfo.updateWbookInfo";
			    	      count = dao.updateData(queryID, new Object[]{	szARR_WLOC_CD,
																	szARR_YD_PNT_CD,
																	szTRN_EQP_GP,
																	tmpWbookID[0]}); 
			    	
			         	/*
					   --작업예약을 오퍼레이터 지정으로 UPDATE
					   UPDATE  TB_YM_WBOOK        
					   SET     SCH_WORK_LOC_DECISION_METHOD = 'O',
					           CRANE_WORD_PUT_LOC           = (SELECT STACK_COL_GP	    	 		
											                FROM TB_YM_STACKCOL 
										                    WHERE  WLOC_CD 		= :WLOC_CD
										                    AND    YD_PNT_CD 	= :YD_PNT_CD
										                    AND    SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
											                     )
					   WHERE   WBOOK_ID                     = :WBOOK_ID
					   */
			          	//스케쥴 생성 
//		 			     EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
//		 			     Boolean isTrue 		 = (Boolean)ejbConn.trx(method_nm,
//		 															new  Class[]{String.class},
//		 															new Object[]{Glo_Sch_Call.trim() });
		 			     
			 			msgOutRecord.setField("method_nm", 		method_nm);		//method 명
			 			msgOutRecord.setField("Glo_Sch_Call", 	Glo_Sch_Call);	//작업예약번호
		    		 
		    	     }	
			  }		
				
		    	//B-cast 슬라브야드 경우는 제외
				else if(szARR_WLOC_CD.equals("D2Y44")||szARR_WLOC_CD.equals("D2Y45")||szARR_WLOC_CD.equals("D3Y41")||szARR_WLOC_CD.equals("D3Y42")){
	    		
	    			queryID = "ym.tsinfo.getListtrnEqpschL_1";
	    		/*
		    	 * SELECT YD_CARLD_WRK_BOOK_ID WBOOK_ID
		    	 *        ,YD_CAR_SCH_ID
	                FROM TB_YD_CARSCH
	               WHERE TRN_EQP_CD = ?
	               AND YD_CAR_PROG_STAT = '2'
	               AND (DEL_YN IS NULL OR DEL_YN <> 'Y')
		    	 */	
	    		FrtoProductList = dao.getCommonList(queryID, new Object[]{szTRN_EQP_CD});
	    		
	    		int iSeqCount 			= FrtoProductList.size();
		    	String[] tmpWbookID  	= new String[iSeqCount];
		    	
		    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
		    	tmpWbookID[0]  	= StringHelper.evl(FrtoProduct.getFieldString("WBOOK_ID"), "");
		    	if(szARR_WLOC_CD.equals("D3Y43")){
		    	    Glo_Sch_Call  	= tmpWbookID[0].trim() +"-" ;	
		    	    method_nm = "syCraneScheduleInfoInsert";
		    	}
		    	else 
		    	{
		    		Glo_Sch_Call  	= tmpWbookID[0].trim();
		    		method_nm = "callCraneSchInfo";
		    	}
		    	

		    	
		    	queryID = "ym.tsinfo.updateWbookInfo";
		    	count = dao.updateData(queryID, new Object[]{	szARR_WLOC_CD,
																szARR_YD_PNT_CD,
																szTRN_EQP_GP,
																tmpWbookID[0]}); 
		    	
		       	/*
				--작업예약을 오퍼레이터 지정으로 UPDATE
				UPDATE  TB_YM_WBOOK        
				SET     SCH_WORK_LOC_DECISION_METHOD = 'O',
				        CRANE_WORD_PUT_LOC           = (SELECT STACK_COL_GP	    	 		
										                FROM TB_YM_STACKCOL 
									                    WHERE  WLOC_CD 		= :WLOC_CD
									                    AND    YD_PNT_CD 	= :YD_PNT_CD
									                    AND    SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
										                     )
				WHERE   WBOOK_ID                     = :WBOOK_ID
				*/
		       	//스케쥴 생성 
//	 			EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
//	 			Boolean isTrue 		 = (Boolean)ejbConn.trx(method_nm,
//	 															new  Class[]{String.class},
//	 															new Object[]{Glo_Sch_Call.trim() });
	 			
	 			msgOutRecord.setField("method_nm", 		method_nm);		//method 명
	 			msgOutRecord.setField("Glo_Sch_Call", 	Glo_Sch_Call);	//작업예약번호
	 			
			   }		
			}	
	    				
            //				포인트점유사항 출하송신
			Paramrecord = JDTORecordFactory.getInstance().create(); 
			Paramrecord.setField("WLOC_CD", 		szARR_WLOC_CD);//
			Paramrecord.setField("YD_PNT_CD", 	szARR_YD_PNT_CD );//
			Paramrecord.setField("YD_PNT_OCPY_GP", 		"A");
			Paramrecord.setField("YD_PNT_UNIT_CL_GP", 		"C");
			Paramrecord.setField("LOAN_PULLOUT_ABLE_YN", 		"N");
			Paramrecord.setField("OCPY_TRN_EQP_CD", szTRN_EQP_CD);
			this.procMatlCarArrPntSenddm(Paramrecord);
			
			
			

	    	
		    	
		}catch(Exception e){
			
			szMsg="소재차량 도착 처리 Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}


		return msgOutRecord;
	}// end of procMatlCarArrNew()
	
	
	/**
	 *      [A] 오퍼레이션명 : 소재차량 도착 Point요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public boolean procMatlCarArrPntRequest(JDTORecord msgRecord)throws DAOException  {
		//TC_CODE : TSYDJ002
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		boolean isSuccess = false;
		
		YdStockDAO ydStockDAO = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO = new YdWBookDAO();
		ymCommonDAO dao = ymCommonDAO.getInstance();
	    int intRtnVal 		   				= 0 ;
	    
		JDTORecord Paramrecord = null;
	    	    
	    String s_STACK_YD_GP = "";
	    String s_STACK_BAY_GP = "";
        String wBookid      	= "";
        String sWBookQueryId 	= "";
        String sloadStoppoint = "";	
        String sunloadStoppoint = "";
	    
	    String szMsg           				= "";
	    String szMethodName    				= "procMatlCarArrPntRequest";
	    
	    String szQuery         				= "";
	    String queryID         				= "";

	    String szTRN_EQP_CD    				= "";
	    String szTRN_WRK_FULLVOID_GP 		= "";
	    String szWLOC_CD 					= "";
	    String szSP_TRUCK_LOADING_LOC_TP 	= "";
	    String szTRN_EQP_GP 				= "";
	    String trnEqpQueryId                = "";
	    String szYD_CAR_USE_GP 	= "";
	    String	sYD_MSG_CD					= "";
	    String sloadStopTsCd = "";
	    String sunloadStopTsCd = "";
	    String item  = "";
	    String s_WBOOK_ID = "";
	    String sSchCode = "";
	    
	    
	    List StkColList = null;
	    List loadPointList = null;
	    List unloadPointList = null;
	    List unloadEndPointList = null;
	    List loadEndPointList = null;
	    List FrtoProductList = null;
		String szRcvTcCode=YmCommonUtil.getTcCode(msgRecord);
		if(szRcvTcCode==null){
			szMsg=szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			
			return false;
		}
		if(bDebugFlag){
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
		}
		
	    try{
	    	szTRN_EQP_CD			  = YmCommonUtil.paraRecChkNull(msgRecord, "TRN_EQP_CD");
	    	szWLOC_CD 				  = YmCommonUtil.paraRecChkNull(msgRecord, "WLOC_CD");
	    	szTRN_WRK_FULLVOID_GP     = YmCommonUtil.paraRecChkNull(msgRecord, "TRN_WRK_FULLVOID_GP");
	    	szTRN_EQP_GP 			  = szTRN_EQP_CD.substring(1, 3);//PT/TR 구분
	    
	    	{ 
	    		/**
	    		 *  AB열연 야드개소코드 체크 
	    		 *  공차 : 착지개소
	    		 *  영차 : 발지개
	    		 */
	    		boolean IsABLoc = false; 
	    		
	    		logger.println(LogLevel.DEBUG,this,"=체크개소코드 => "+ szWLOC_CD);   
	    		
	    		IsABLoc = this.getABLocationInfo_02(szWLOC_CD);
		    	
		    	if(!IsABLoc){
		    		szMsg = "착지 개소코드 오류";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					return false;
				}
	    	}	
	    	
	      //**********************************************************************************	
          // 장비코드로 포인트 재 요구 시 상차예약정지위치 검색 (20090828 정종균 추가 )
  		  trnEqpQueryId = "ym.tsinfo.getListloadStoppoint2";
  		  /*
			 SELECT  STACK_COL_GP,
             YD_PNT_CD
             FROM TB_YM_STACKCOL 
             WHERE WLOC_CD     = :WLOC_CD
             AND CAR_CARD_NO = :TRN_EQP_CD

				*/
	  		loadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{	szWLOC_CD,															    				
															    				szTRN_EQP_CD});
			if(loadPointList.size() > 0){
		    	JDTORecord loadPointrec = (JDTORecord)loadPointList.get(0);
		    	sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
		    	sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
		    	
		    	
		    	//포인트지시 모듈 독립적으로 만들어서 호출
		    	//착지개소 및 포인트코드 추후 셋팅요 
				Paramrecord = JDTORecordFactory.getInstance().create(); 
				Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
				Paramrecord.setField("SPOS_WLOC_CD", 	szWLOC_CD);//착지개소코드
				Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
				Paramrecord.setField("YD_MSG_CD", 		sYD_MSG_CD);
				
				this.procMatlCarArrPntReq(Paramrecord);
            
				isSuccess = true; 
			//**********************************************************************************
			}else{		    		    	
	    	
	       if(szTRN_WRK_FULLVOID_GP.equals("E"))
	       {   //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
	    	   logger.println(LogLevel.DEBUG,this,"공차 포인트요구 수신 장비번호 "+ szTRN_EQP_CD);
	    	   
	    	   if(szWLOC_CD.equals("D2Y43")) //BCast의 경우
			   {
	    		   logger.println(LogLevel.DEBUG,this,"=파레트 도착 목적동검색");
					
					queryID 	= "ym.ilkwan.dao.YdStockDAO.getListAimbayBCast";
			    	List AimbayBCast = dao.getCommonList(queryID);
			    			    	
			    	int iSeqCount 	= AimbayBCast.size();				    	
			    	String aimBay_BCast = "";
			    	if(iSeqCount >0){
			    	JDTORecord AimbayBCastReq = (JDTORecord)AimbayBCast.get(0);
			    	aimBay_BCast	= StringHelper.evl(AimbayBCastReq.getFieldString("AIM_BAY"), "");	
			    	logger.println(LogLevel.DEBUG,this,"=BCast 목적동 검색결과 : " + aimBay_BCast);				    	
			    	}
	    		   
                      // 상차정지위치 검색
		    		  trnEqpQueryId = "ym.tsinfo.getListloadStoppoint_1";
		    		  /*
			    	 	SELECT  STACK_COL_GP,
								YD_PNT_CD
						FROM TB_YM_STACKCOL 
						WHERE WLOC_CD     = :WLOC_CD
						AND YD_GP         = :YD_GP
						AND BAY_GP        = :BAY_GP
						AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
						AND TRN_EQP_CD  IS NULL	-- 현재차량정지위치
						AND CAR_CARD_NO IS NULL	-- 예약차량정지위치
						*/
		    		loadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{	szWLOC_CD,
																    				"0",
																    				aimBay_BCast,
																    				szTRN_EQP_GP});
		    		
//		    		포인트 모두 점유상태일때 상차완료된 포인트찾음					    	  
		    		trnEqpQueryId = "ym.tsinfo.getListloadEndpoint";
		    		/*
						SELECT B.STACK_COL_GP,
                               B.YD_PNT_CD
                       FROM TB_YD_CARSCH A,TB_YM_STACKCOL B
                       WHERE A.DEL_YN = 'N'
                       AND A.YD_CAR_PROG_STAT = '5'
                       AND SUBSTR(YD_CARLD_STOP_LOC,1,1) = ?
                       AND SUBSTR(YD_CARLD_STOP_LOC,2,1) = ?
                       AND SUBSTR(YD_CARLD_STOP_LOC, 3, 2) = ?
                       AND A.YD_CARLD_STOP_LOC = B.STACK_COL_GP
                       AND A.SPOS_WLOC_CD = B.WLOC_CD
			    	 */
		    		loadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{"0",
		    				                                                        aimBay_BCast,
																    				szTRN_EQP_GP});
		    		

		    		if(loadPointList.size() > 0){
				    	JDTORecord loadPointrec = (JDTORecord)loadPointList.get(0);
				    	sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
				    	sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
				    	logger.println(LogLevel.DEBUG,this,"지시포인트 "+ sloadStopTsCd);
		    		}else if(loadEndPointList.size() > 0){
				    	JDTORecord loadPointrec = (JDTORecord)loadEndPointList.get(0);
				    	sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
				    	sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
				    	logger.println(LogLevel.DEBUG,this," 지시포인트없음 상/하차 완료포인트 "+ sloadStopTsCd);
		    		}else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
		    			sloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
		    			sloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;
		    			
		    			//대기장 포인트 사유 가져오기
			    		sYD_MSG_CD = this.getCarMsg("N" ,"0" , aimBay_BCast , szTRN_EQP_GP , szWLOC_CD,"");
			    			
		    			Paramrecord = JDTORecordFactory.getInstance().create(); 
						Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
						Paramrecord.setField("SPOS_WLOC_CD", 	szWLOC_CD);//착지개소코드
						Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
						Paramrecord.setField("YD_MSG_CD", 		sYD_MSG_CD);
						
						logger.println(LogLevel.DEBUG,this," 포인트 없음 ");
						this.procMatlCarArrPntReq(Paramrecord);	
						return false;		    			
		    		}
		    	  	
	    		    /*
	  	             * 포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
	  	          */
	  	          trnEqpQueryId 		= "ym.tsinfo.updateEquipcolStat";
	  	          int iSeq = dao.updateData(trnEqpQueryId, new Object[]{"L",	  	            		                                            
							                                            szTRN_EQP_CD,
							                                            sloadStoppoint}); 	
	  	          
	  	          //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				  CarPointinforeg("3","",szTRN_EQP_CD,sloadStoppoint,"","","R");
	  	            
	  	            /*
	  	             * UPDATE TB_YM_STACKCOL
	                   SET STACK_STAT = :STACK_STAT,
	                       CAR_CARD_NO = :CAR_CARD_NO,
	                       MODIFIER = 'TSYDJ004',
	                       MOD_DDTT = sysdate
	                   WHERE STACK_COL_GP = :STACK_COL_GP
	  	             */
		    		
	    		
	          }
	    	  else if(szWLOC_CD.equals("D2Y44")||
	    			  szWLOC_CD.equals("D2Y45")||
	    			  szWLOC_CD.equals("D3Y41")||
	    			  szWLOC_CD.equals("D3Y42")){
	    		  
	    		  
		    	   String 	trnQueryId 	= "ym.tsinfo.getListFrtostlList_Coil_E";
		           List FrtostlList = dao.getCommonList(trnQueryId, new Object[]{szTRN_EQP_CD});
		           
		           String s_STL_APPEAR_GP ="N";
		           
		           if(FrtostlList.size() > 0){
		        	   JDTORecord TolocRec 	= (JDTORecord)FrtostlList.get(0);	
		        	   s_STL_APPEAR_GP 	= StringHelper.evl(TolocRec.getFieldString("STL_APPEAR_GP"),"");
		           }
		           
	               // 공차의 경우 상차작업예약 ID를 이용하여 목적동을 알아온다
    	           sWBookQueryId  = "ym.tsinfo.getListStoppointE";
					  /*
					  * select B.YD_GP, B.BAY_GP, B.WBOOK_ID
					  from TB_YD_CARSCH A, TB_YM_WBOOK B
					  where A.YD_CARLD_WRK_BOOK_ID = B.WBOOK_ID
					  and A.TRN_EQP_CD = :TRN_EQP_CD
					  and YD_EQP_WRK_STAT = 'U'
					  and A.DEL_YN = 'N'
					  and B.DEL_YN = 'N'  
					  */
	    	   	    	   
	    		      StkColList = dao.getCommonList(sWBookQueryId, new Object[]{szTRN_EQP_CD});
	    		      if(StkColList.size() > 0){
			    	      JDTORecord StkColRec = (JDTORecord)StkColList.get(0);
			    	      s_STACK_YD_GP   = StringHelper.evl(StkColRec.getFieldString("YD_GP"), "");
			    	      s_STACK_BAY_GP    = StringHelper.evl(StkColRec.getFieldString("BAY_GP"), "");
			    	      s_WBOOK_ID    = StringHelper.evl(StkColRec.getFieldString("WBOOK_ID"), "");
	    		      }else{
	    		    	   	
		  					int iSeqCount 	= 0 ;
		  					if(szWLOC_CD.equals("D3Y41")||szWLOC_CD.equals("D3Y42"))
		  					{															         						
		  						//대상재 조회
		  				    	queryID 	= "ym.ilkwan.dao.YdStockDAO.getListFrtoStl_CoilNewB";				    	
		  					}else if(szWLOC_CD.equals("D2Y44")||szWLOC_CD.equals("D2Y45"))
		  					{															         						
		  						//대상재 조회
		  				    	queryID 	= "ym.ilkwan.dao.YdStockDAO.getListFrtoStl_CoilNewA";
		  					}
		  					FrtoProductList = dao.getCommonList(queryID, new Object[]{szWLOC_CD,szTRN_EQP_CD,szTRN_EQP_CD,szWLOC_CD});
		  					iSeqCount = FrtoProductList.size();
		  					
		  					logger.println(LogLevel.DEBUG,this,"=코일 출발실적처리[AB,C<>AB]=>상차정지위치검색 건수="+iSeqCount);
		  					
		  					
		  					if(iSeqCount > 0){	
		  						JDTORecord AvaPointList = (JDTORecord)FrtoProductList.get(0);
		  						s_STACK_YD_GP 	= StringHelper.evl(AvaPointList.getFieldString("STACK_COL_GP"),"").substring(0,1);
		  						s_STACK_BAY_GP 	= StringHelper.evl(AvaPointList.getFieldString("STACK_COL_GP"),"").substring(1,2);	
		  			
		  					}
	    		      }
	    		
				   //상차정지위치 검색
		    	  //추가부분 : 검색대상재 동에 있는 차량위치정보  
	    		       trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
	    		  /*
		    	 	SELECT  STACK_COL_GP,
							YD_PNT_CD
					FROM TB_YM_STACKCOL 
					WHERE WLOC_CD     = :WLOC_CD
					AND YD_GP         = :YD_GP
					AND BAY_GP        = :BAY_GP
					AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
					AND TRN_EQP_CD  IS NULL	-- 현재차량정지위치
					AND CAR_CARD_NO IS NULL	-- 예약차량정지위치
		    	 */
	    		      loadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{	szWLOC_CD,
															    				s_STACK_YD_GP,
															    				s_STACK_BAY_GP,
															    				szTRN_EQP_GP});
 
 
		    		    if(loadPointList.size() > 0){
				        	JDTORecord loadPointrec = (JDTORecord)loadPointList.get(0);
				        	sloadStoppoint  = StringHelper.evl(loadPointrec.getFieldString("STACK_COL_GP"), "");
				        	sloadStopTsCd   = StringHelper.evl(loadPointrec.getFieldString("YD_PNT_CD"), "");
				        	logger.println(LogLevel.DEBUG,this,"지시포인트 "+ sloadStopTsCd);
		    		    }else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
		    		    	sloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
		    			    sloadStopTsCd	= YmCommonConst.YM_DEFAULT_PNT_CD;
		    			    
		    			    //대기장 포인트 사유 가져오기
				    		sYD_MSG_CD = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szWLOC_CD,"");
				    		
		    			    Paramrecord = JDTORecordFactory.getInstance().create(); 
						    Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
						    Paramrecord.setField("SPOS_WLOC_CD", 	szWLOC_CD);//착지개소코드
						    Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
						    Paramrecord.setField("YD_MSG_CD", 		sYD_MSG_CD);
						    
							logger.println(LogLevel.DEBUG,this," 포인트 없음 ");
						    this.procMatlCarArrPntReq(Paramrecord);	
						    return false;		    			
		    		    }
		    		    
		    		   	if(s_STL_APPEAR_GP.equals("Y"))// 제품
				    	 {
				    		 sSchCode	= YmCommonConst.NEW_SCH_WORK_KIND_GVML ; 
				    		 sSchCode = getSchWorkKind(sSchCode, sloadStoppoint);
				    		 
				    	 }
				    	 else
				    	 {
				    		 sSchCode	= YmCommonConst.NEW_SCH_WORK_KIND_CVML ;
				    		 sSchCode = getSchWorkKind(sSchCode, sloadStoppoint);
				    	 }
		    		    
		    		    /*
		  	             * 포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
		  	          */
		  	          trnEqpQueryId 		= "ym.tsinfo.updateEquipcolStat";
		  	          int iSeq = dao.updateData(trnEqpQueryId, new Object[]{"L",	  	            		                                            
								                                            szTRN_EQP_CD,
								                                            sloadStoppoint}); 
		  	          
		  	          //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					  CarPointinforeg("3","",szTRN_EQP_CD,sloadStoppoint,"","","R");
		  	            
		  	            /*
		  	             * UPDATE TB_YM_STACKCOL
		                   SET STACK_STAT = :STACK_STAT,
		                       CAR_CARD_NO = :CAR_CARD_NO,
		                       MODIFIER = 'TSYDJ004',
		                       MOD_DDTT = sysdate
		                   WHERE STACK_COL_GP = :STACK_COL_GP
		  	             */
		    		   	
		    		   	
		    		   	//작업예약 테이블에 실제 스케쥴코드  update
		    		   	queryID = "ym.tsinfo.updateWbookSchInfo";
				    	int count = dao.updateData(queryID, new Object[]{sSchCode,s_WBOOK_ID}); 
				    	logger.println(LogLevel.DEBUG,this," 작업예약에 스케쥴코드 업데이트 성공================ :" + count);

	    	        }
	        	
		    	
		    	//포인트지시 모듈 독립적으로 만들어서 호출
		    	//착지개소 및 포인트코드 추후 셋팅요 
				Paramrecord = JDTORecordFactory.getInstance().create(); 
				Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
				Paramrecord.setField("SPOS_WLOC_CD", 	szWLOC_CD);//착지개소코드
				Paramrecord.setField("YD_PNT_CD", 		sloadStopTsCd);
				Paramrecord.setField("YD_MSG_CD", 		sYD_MSG_CD);
				
				this.procMatlCarArrPntReq(Paramrecord);
            
				isSuccess = true;  
				//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
	       }    
	  
	       else if(szTRN_WRK_FULLVOID_GP.equals("F")) 	    	   
	       {   //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
	    	   logger.println(LogLevel.DEBUG,this,"영차 포인트요구 수신 장비번호 "+ szTRN_EQP_CD);
	    	   if(szWLOC_CD.equals("D2Y44")||
    			  szWLOC_CD.equals("D2Y45")||
    			  szWLOC_CD.equals("D3Y43")||
    			  szWLOC_CD.equals("D3Y41")||
    			  szWLOC_CD.equals("D3Y42")){
	    		   
	    		   if(szWLOC_CD.equals("D3Y43"))
	    		   {
	    			   
	    		   }
	    		   else
	    		   {
		    	   String 	trnQueryId 	= "ym.tsinfo.getListFrtostlList_Coil";
		           List FrtostlList = dao.getCommonList(trnQueryId, new Object[]{szTRN_EQP_CD});
		    		
		          JDTORecord TolocRec 	= (JDTORecord)FrtostlList.get(0);	
		    	  String s_STL_APPEAR_GP 	= StringHelper.evl(TolocRec.getFieldString("STL_APPEAR_GP"),"");
		    		
		            if(s_STL_APPEAR_GP.equals("Y"))// 제품
		    	    {  
		            	item = YmCommonConst.ITEM_CG;		    	   
		    	    }
		            else
		            {
		            	item = YmCommonConst.ITEM_CM;
		            }   
	    		   }
   	   
		    	   // 영차의 경우 하차작업예약 ID를 이용하여 목적동을 알아온다
		    	   sWBookQueryId  = "ym.tsinfo.getListStoppointF";
		    	   StkColList = dao.getCommonList(sWBookQueryId, new Object[]{szTRN_EQP_CD});
		    		if(StkColList.size() > 0){
				    	JDTORecord StkColRec = (JDTORecord)StkColList.get(0);
				    	s_STACK_YD_GP   = StringHelper.evl(StkColRec.getFieldString("YD_GP"), "");
				    	s_STACK_BAY_GP    = StringHelper.evl(StkColRec.getFieldString("BAY_GP"), "");
				    	s_WBOOK_ID    = StringHelper.evl(StkColRec.getFieldString("WBOOK_ID"), "");
		    		}
	    		
	                //하차정지위치 검색(등록할것)
			    	//추가부분 : 검색대상재 동에 있는 차량위치정보  
		    		trnEqpQueryId = "ym.tsinfo.getListloadStoppoint";
		    		/*
			    	 	SELECT  STACK_COL_GP,
								YD_PNT_CD
						FROM TB_YM_STACKCOL 
						WHERE WLOC_CD     = :WLOC_CD
						AND YD_GP         = :YD_GP
						AND BAY_GP        = :BAY_GP
						AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
						AND TRN_EQP_CD  IS NULL	-- 현재차량정지위치
						AND CAR_CARD_NO IS NULL	-- 예약차량정지위치
			    	 */
		    		unloadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szWLOC_CD,
																    				s_STACK_YD_GP,
																    				s_STACK_BAY_GP,
																    				szTRN_EQP_GP});

	    		
		    		if(unloadPointList.size() > 0){
				    	JDTORecord unloadPointrec = (JDTORecord)unloadPointList.get(0);
				    	sunloadStoppoint  = StringHelper.evl(unloadPointrec.getFieldString("STACK_COL_GP"), "");
				    	sunloadStopTsCd   = StringHelper.evl(unloadPointrec.getFieldString("YD_PNT_CD"), "");
				    	 logger.println(LogLevel.DEBUG,this," 지시포인트  "+ sunloadStopTsCd);
		    		}else{//포인트없으면 포인트 없음으로 지시주고 하위처리 안함
		    			sunloadStoppoint	= YmCommonConst.YM_DEFAULT_WLOC_CD;
		    			sunloadStopTsCd		= YmCommonConst.YM_DEFAULT_PNT_CD;
		    			
		    			//대기장 포인트 사유 가져오기
			    		sYD_MSG_CD = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szWLOC_CD,"");
			    			
		    			Paramrecord = JDTORecordFactory.getInstance().create(); 
						Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
						Paramrecord.setField("SPOS_WLOC_CD", 	szWLOC_CD);//착지개소코드
						Paramrecord.setField("YD_PNT_CD", 		sunloadStopTsCd);
						Paramrecord.setField("YD_MSG_CD", 		sYD_MSG_CD);
						
						logger.println(LogLevel.DEBUG,this," 포인트 없음 ");
						this.procMatlCarArrPntReq(Paramrecord);	
						return false;
		    			
		    		}
	    		
	    		
		    		if(szWLOC_CD.equals("D3Y43"))
		    		{			    			
		    			sSchCode =  YmCommonConst.NEW_SCH_WORK_KIND_SVMU;
		    			   /*
		  	             * 포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
		  	          */
		  	          trnEqpQueryId 		= "ym.tsinfo.updateEquipcolStat";
		  	          int iSeq = dao.updateData(trnEqpQueryId, new Object[]{"L",	  	            		                                            
								                                            szTRN_EQP_CD,
								                                            sunloadStoppoint}); 
		  	          
		  	          //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					  CarPointinforeg("3","",szTRN_EQP_CD,sunloadStoppoint,"","","R");
		  	            
		  	            /*
		  	             * UPDATE TB_YM_STACKCOL
		                   SET STACK_STAT = :STACK_STAT,
		                       CAR_CARD_NO = :CAR_CARD_NO,
		                       MODIFIER = 'TSYDJ004',
		                       MOD_DDTT = sysdate
		                   WHERE STACK_COL_GP = :STACK_COL_GP
		  	             */
	//	  	      작업예약 테이블에 실제 작업예약 ID update
				    	queryID = "ym.tsinfo.updateWbookSchInfo";
				    	int count = dao.updateData(queryID, new Object[]{sSchCode,s_WBOOK_ID}); 
				    	logger.println(LogLevel.DEBUG,this," 작업예약에 스케쥴코드 업데이트 성공================ " + sSchCode);
		    			   
		    		}	    		
		    		else
		    		{
		    		String pos  = sunloadStoppoint;				    			
	    			sSchCode = getUnloadSchKind(item,pos);
	    			
	    			   /*
	  	             * 포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
	  	          */
	  	          trnEqpQueryId 		= "ym.tsinfo.updateEquipcolStat";
	  	          int iSeq = dao.updateData(trnEqpQueryId, new Object[]{"L",	  	            		                                            
							                                            szTRN_EQP_CD,
							                                            sunloadStoppoint}); 	  	           	  	            
	  	            
	  	          //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				  CarPointinforeg("3","",szTRN_EQP_CD,sunloadStoppoint,"","","R");  
	  	          /*
	  	             * UPDATE TB_YM_STACKCOL
	                   SET STACK_STAT = :STACK_STAT,
	                       CAR_CARD_NO = :CAR_CARD_NO,
	                       MODIFIER = 'TSYDJ004',
	                       MOD_DDTT = sysdate
	                   WHERE STACK_COL_GP = :STACK_COL_GP
	  	             */
	    			
	   		   	
	    		   	//작업예약 테이블에 실제 작업예약 ID update
			    	queryID = "ym.tsinfo.updateWbookSchInfo";
			    	int count = dao.updateData(queryID, new Object[]{sSchCode,s_WBOOK_ID}); 
			    	logger.println(LogLevel.DEBUG,this," 작업예약에 스케쥴코드 업데이트 성공================ " + sSchCode);
		    		}
	    		
	    	   }
	 	          		    	
		    	//포인트지시 모듈 독립적으로 만들어서 호출
		    	//착지개소 및 포인트코드 추후 셋팅요 
				Paramrecord = JDTORecordFactory.getInstance().create(); 
				Paramrecord.setField("TRN_EQP_CD", 		szTRN_EQP_CD);//운송장비코드
				Paramrecord.setField("SPOS_WLOC_CD", 	szWLOC_CD);//착지개소코드
				Paramrecord.setField("YD_PNT_CD", 		sunloadStopTsCd);
				Paramrecord.setField("YD_MSG_CD", 		sYD_MSG_CD);
				
				this.procMatlCarArrPntReq(Paramrecord);
            
				isSuccess = true; 
				//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&	   
	       }
			//**********************************************************************************
			}
	          	
		}catch(Exception e){
			
			szMsg="소재차량 도착 Point요구 Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}

		
		szMsg="소재차량 도착 Point요구("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);

		return true;
	}// end of procMatlCarArrPntRequest()
	
	/**
	 *	야드 개소코드 체크 
	 *
	 *  SLAB
	 *  D2Y43 A연주-B Cast Slab Yard 
	 *  D3Y43 B열연-Slab Yard
	 *  D3Y44 B열연-가열로 Slab Yard
	 *  
	 *  COIL
	 *  
	 * 	D2Y44 A열연-#1 제품/소재 Coil Yard
	 * 	D2Y45 A열연-#2 제품/소재 Coil Yard
	 * 	D3Y41 B열연-#1 제품/소재 Coil Yard
	 *	D3Y42 B열연-#2 제품/소재 Coil Yard
	 *
	 *	Case 1 : AB 	=> AB    	Value : AA
	 *  Case 2 : AB 	=> 일관   	Value : AC
	 *  Case 3 : 일관 	=> AB    	Value : CA
	 *  Case 4 : 일관 	=> 일관 	 	Value : CC
	 */
	private String getABLocationInfo_01(String sSlocCd,String sWlocCd){
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		String sWorkGp	= "CC";
		/**
		 * 발/착지 개소코드 체크
		 */
		if("D2Y43".equals(sSlocCd)|| //A연주-B Cast Slab Yard
		   "D3Y43".equals(sSlocCd)|| //B열연-Slab Yard
		   "D3Y44".equals(sSlocCd)|| //B열연-가열로 Slab Yard
		   "D2Y44".equals(sSlocCd)|| //A열연-#1 제품/소재 Coil Yard
		   "D2Y45".equals(sSlocCd)|| //A열연-#2 제품/소재 Coil Yard
		   "D3Y41".equals(sSlocCd)|| //B열연-#1 제품/소재 Coil Yard
		   "D3Y42".equals(sSlocCd)){ //B열연-#2 제품/소재 Coil Yard
			
			if("D2Y43".equals(sWlocCd)|| //A연주-B Cast Slab Yard
			   "D3Y43".equals(sWlocCd)|| //B열연-Slab Yard
			   "D3Y44".equals(sWlocCd)|| //B열연-가열로 Slab Yard
			   "D2Y44".equals(sWlocCd)|| //A열연-#1 제품/소재 Coil Yard
			   "D2Y45".equals(sWlocCd)|| //A열연-#2 제품/소재 Coil Yard
			   "D3Y41".equals(sWlocCd)|| //B열연-#1 제품/소재 Coil Yard
			   "D3Y42".equals(sWlocCd)){ //B열연-#2 제품/소재 Coil Yard
				
				sWorkGp	= "AA";
			}
			else{
				sWorkGp	= "AC";
			}
		}else{
			
			if("D2Y43".equals(sWlocCd)|| //A연주-B Cast Slab Yard
			   "D3Y43".equals(sWlocCd)|| //B열연-Slab Yard
			   "D3Y44".equals(sWlocCd)|| //B열연-가열로 Slab Yard
			   "D2Y44".equals(sWlocCd)|| //A열연-#1 제품/소재 Coil Yard
			   "D2Y45".equals(sWlocCd)|| //A열연-#2 제품/소재 Coil Yard
			   "D3Y41".equals(sWlocCd)|| //B열연-#1 제품/소재 Coil Yard
			   "D3Y42".equals(sWlocCd)){ //B열연-#2 제품/소재 Coil Yard
				
				sWorkGp	= "CA";
			}
			else{
				sWorkGp	= "CC";
			}
		}
		return sWorkGp;
	}
	
	private boolean getABLocationInfo_02(String sWlocCd){
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		boolean isReturn = false;
		
		      if("D2Y43".equals(sWlocCd)){//A연주-B Cast Slab Yard 
			isReturn = true;
		}else if("D2Y44".equals(sWlocCd)){//A열연-#1 제품/소재 Coil Yard
			isReturn = true;
		}else if("D2Y45".equals(sWlocCd)){//A열연-#2 제품/소재 Coil Yard
			isReturn = true;
		}else if("D3Y41".equals(sWlocCd)){//B열연-#1 제품/소재 Coil Yard
			isReturn = true;
		}else if("D3Y42".equals(sWlocCd)){//B열연-#2 제품/소재 Coil Yard
			isReturn = true;
		}else if("D3Y43".equals(sWlocCd)){//B열연-Slab Yard
			isReturn = true;
		}else if("D3Y44".equals(sWlocCd)){//B열연-가열로 Slab Yard 
			isReturn = true;
		}
		      
		return isReturn;
	}
	
	/*
	
	*/
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 포인트점유사항 출하송신
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procMatlCarArrPntSenddm(JDTORecord msgRecord)throws JDTOException  {
		
		
        logger.println(LogLevel.DEBUG,this, "포인트점유사항 출하송신.===");

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		//TC_CODE : TSYDJ011
		Boolean isSuccess = new Boolean(false);
		
	    String szMsg           = "";
	    String szMethodName    = "procMatlCarArrPntReq";
		 
		JDTORecord tcRecord = JDTORecordFactory.getInstance().create();
		try{
			tcRecord.setField("JMS_TC_CD", "YDDMR026");
			tcRecord.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			tcRecord.setField("WLOC_CD", StringHelper.evl(msgRecord.getFieldString("WLOC_CD"), ""));
			tcRecord.setField("YD_PNT_CD", StringHelper.evl(msgRecord.getFieldString("YD_PNT_CD"), ""));
			tcRecord.setField("YD_PNT_OCPY_GP", StringHelper.evl(msgRecord.getFieldString("YD_PNT_OCPY_GP"), ""));
			tcRecord.setField("YD_PNT_UNIT_CL_GP", StringHelper.evl(msgRecord.getFieldString("YD_PNT_UNIT_CL_GP"), ""));
			tcRecord.setField("LOAN_PULLOUT_ABLE_YN", StringHelper.evl(msgRecord.getFieldString("LOAN_PULLOUT_ABLE_YN"), ""));
			tcRecord.setField("OCPY_TRN_EQP_CD", StringHelper.evl(msgRecord.getFieldString("OCPY_TRN_EQP_CD"), ""));
			tcRecord.setField("TC_CODE", "YDDMR026");
			
			//EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			//isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
			//  	  	 new Object[]{tcRecord});
			logger.println(LogLevel.DEBUG,this, "현재 지시된 포인트정보==========================================================================");
		    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 출하 포인트점유상태 송신.==="+StringHelper.evl(msgRecord.getFieldString("WLOC_CD"), ""));
		    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 출하 포인트점유상태 송신.==="+StringHelper.evl(msgRecord.getFieldString("YD_PNT_CD"), ""));
		    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 출하 포인트점유상태 송신.==="+StringHelper.evl(msgRecord.getFieldString("YD_PNT_OCPY_GP"), ""));
		    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 출하 포인트점유상태 송신.==="+StringHelper.evl(msgRecord.getFieldString("YD_PNT_UNIT_CL_GP"), ""));
		    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 출하 포인트점유상태 송신.==="+StringHelper.evl(msgRecord.getFieldString("LOAN_PULLOUT_ABLE_YN"), ""));
		    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 출하 포인트점유상태 송신.==="+StringHelper.evl(msgRecord.getFieldString("OCPY_TRN_EQP_CD"), ""));
			
			
			
           logger.println(LogLevel.DEBUG,this, "포인트점유사항 출하송신 완료.===");

		}catch(Exception e){
			
			szMsg="포인트점유실적 송신  Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);
		}

		
		szMsg="포인트점유실적송신("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);

	
	}// end of procMatlCarArrPntSenddm()
	
	
	

	/**
	 *      [A] 오퍼레이션명 : 소재차량Point지시	
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public void procMatlCarArrPntReq(JDTORecord msgRecord)throws DAOException  {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		//TC_CODE : TSYDJ011
		Boolean isSuccess = new Boolean(false);
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		List loadPointList 	   = null;
		
		String szWLOC_CD       = "";
		String szTRN_EQP_CD    = "";
	    String szMsg           = "";
	    String szYD_MSG_CD	   = "";
	    String trnEqpQueryId2  = "";
	    String szMethodName    = "procMatlCarArrPntReq";
		 
		JDTORecord tcRecord = JDTORecordFactory.getInstance().create();
		JDTORecord loadPointrec =JDTORecordFactory.getInstance().create();
		try{
			szTRN_EQP_CD = StringHelper.evl(msgRecord.getFieldString("TRN_EQP_CD"), "");
			szYD_MSG_CD = StringHelper.evl(msgRecord.getFieldString("YD_MSG_CD"), "");
			
			//-------------------------------------------------------
			//******************메시지 이력 관리*************************
			//-------------------------------------------------------
			trnEqpQueryId2	= "ym.tsinfo.updateEquipMsgRecode";
	        int iSeq = dao.updateData(trnEqpQueryId2, new Object[]{szYD_MSG_CD,szTRN_EQP_CD});	
			
			String	trnEqpQueryId = "ym.tsinfo.getListloadStoppoint4";
	  		loadPointList = dao.getCommonList(trnEqpQueryId, new Object[]{	szTRN_EQP_CD});
		 
			tcRecord.setField("JMS_TC_CD", "YDTSJ011");
			tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			tcRecord.setField("TRN_EQP_CD",StringHelper.evl(msgRecord.getFieldString("TRN_EQP_CD"), ""));
			tcRecord.setField("WLOC_CD", StringHelper.evl(msgRecord.getFieldString("SPOS_WLOC_CD"), ""));
			tcRecord.setField("PNT_WO_DT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			tcRecord.setField("YD_PNT_CD", StringHelper.evl(msgRecord.getFieldString("YD_PNT_CD"), "0000")); //포인트 값 null 일때 '0000'으로 지시
			tcRecord.setField("YD_MSG_NM",szYD_MSG_CD);	
			tcRecord.setField("PNT_WO_GP","A");	
			
			if(loadPointList.size() > 0){
		    	loadPointrec = (JDTORecord)loadPointList.get(0); 
		    	tcRecord.setField("TRN_WRK_MTL_GP", StringHelper.evl(loadPointrec.getFieldString("TRN_WRK_MTL_GP"), ""));
	  		}else{
	  			tcRecord.setField("TRN_WRK_MTL_GP", "");
	  		}
			
			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
			  	  	 new Object[]{tcRecord});
			
			logger.println(LogLevel.DEBUG,this, "현재 지시된 포인트정보==========================================================================");
		    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 소재차량Point지시.==="+StringHelper.evl(msgRecord.getFieldString("TRN_EQP_CD"), ""));
		    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 소재차량Point지시.==="+StringHelper.evl(msgRecord.getFieldString("SPOS_WLOC_CD"), ""));
		    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 소재차량Point지시.==="+StringHelper.evl(msgRecord.getFieldString("YD_PNT_CD"), ""));
			
           logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 소재차량Point지시.===");

		}catch(Exception e){
			
			szMsg="소재차량Point지시 Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}

		
		szMsg="소재차량Point지시("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);

	
	}// end of procMatlCarArrPntReq()
	
	/**
     * hyuksang
	 * 코일 공통 테이블의 다음공정을 참조해서 
	 * 목적동을 가져온다
     *
     * @param  String	:	다음공정 코드
     *
     * @return String
     * @throws  
     */	
	
/*	
	public static String[] getCoilAimCd(String next_Prog)
	{	
		String[] rVal = new String[2];
		
		String sProgCd   = "";
		String sStocMv   = "";
		String sReturnGp = "";
		
		String s_STACK_YD_GP 	= "";
		String s_STACK_BAY_GP 	= "";
			   		
    	// 일관제철 다음공정 별 목적동
    	if(next_Prog.equals("1H")){
    		s_STACK_YD_GP 	= "1";
    		s_STACK_BAY_GP 	= "C";
    	}else if(next_Prog.equals("1K")){
    		s_STACK_YD_GP 	= "1";
    		s_STACK_BAY_GP 	= "E";
    	}else if(next_Prog.equals("5H")){
    		s_STACK_YD_GP 	= "3";
    		s_STACK_BAY_GP 	= "A";
    	}else if(next_Prog.equals("5K")){
    		s_STACK_YD_GP 	= "3";
    		s_STACK_BAY_GP 	= "C";
    	}else if(next_Prog.equals("6K")){
    		s_STACK_YD_GP 	= "3";
    		s_STACK_BAY_GP 	= "D";
    	}else if(next_Prog.equals("6R")){
    		s_STACK_YD_GP 	= "3";
    		s_STACK_BAY_GP 	= "D";
    	}else if(next_Prog.equals("DH")){
    		s_STACK_YD_GP 	= "H";
    		s_STACK_BAY_GP 	= "D";
    	}else if(next_Prog.equals("EH")){
    		s_STACK_YD_GP 	= "H";
    		s_STACK_BAY_GP 	= "E";
    	}else if(next_Prog.equals("EK")){
    		s_STACK_YD_GP 	= "H";
    		s_STACK_BAY_GP 	= "E";
    	}else if(next_Prog.equals("ER")){
    		s_STACK_YD_GP 	= "H";
    		s_STACK_BAY_GP 	= "E";
    	}else if(next_Prog.equals("FH")){
    		s_STACK_YD_GP 	= "H";
    		s_STACK_BAY_GP 	= "F";
    	}else if(next_Prog.equals("GH")){
    		s_STACK_YD_GP 	= "H";
    		s_STACK_BAY_GP 	= "G";
    	}else if(next_Prog.equals("HH")){
    		s_STACK_YD_GP 	= "H";
    		s_STACK_BAY_GP 	= "H";
    	}else if(next_Prog.equals("HK")){
    		s_STACK_YD_GP 	= "H";
    		s_STACK_BAY_GP 	= "H";
    	}else if(next_Prog.equals("HR")){
    		s_STACK_YD_GP 	= "H";
    		s_STACK_BAY_GP 	= "H";
    	}
    	
    	rVal[0] = s_STACK_YD_GP;
    	rVal[1] = s_STACK_BAY_GP;
		    	
	    return rVal;
	}
	
*/	
	
	/**
     * hyuksang
	 * B열연 슬라브의 목적동을 가져온다
	 * 
     *
     * @param  String	:	차량스케쥴 코드
     *
     * @return String
     * @throws  
     */	
	private String getSlabAimCd(String s_CAR_SCH_ID, String szSPOS_WLOC_CD)
	{	
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		ymCommonDAO dao = ymCommonDAO.getInstance();
		String rVal = "";
		
		String sProgCd   = "";
		String sStocMv   = "";
		String sReturnGp = ""; 
 
		String s_STACK_BAY_GP 	= "";
		
		List aimBayList = null;
		List SposwlocList = null;
		
		logger.println(LogLevel.DEBUG,this, "슬라브 발지개소는== "+szSPOS_WLOC_CD);
		
		
		if(szSPOS_WLOC_CD.equals("D3Y43"))
		{
			logger.println(LogLevel.DEBUG,this, "슬라브 발지개소가 B열연이므로 발지개소코드 조회");
			
			String QueryId = "ym.tsinfo.getListSposwlocCD";
			
			SposwlocList = dao.getCommonList(QueryId, new Object[]{s_CAR_SCH_ID});
			
			if(SposwlocList.size() > 0){
		    	JDTORecord Sposwlocrec = (JDTORecord)SposwlocList.get(0);
		    	szSPOS_WLOC_CD  = StringHelper.evl(Sposwlocrec.getFieldString("SPOS_WLOC_CD"), "");
			}			
		}
		
		
		
		
		
		if(szSPOS_WLOC_CD.equals("D2Y43"))
		{
			
			logger.println(LogLevel.DEBUG,this, "슬라브 발지개소가 BCast이므로 BCast기준 조회");
			String QueryId = "ym.tsinfo.getListAimBay_BCast";
			
			aimBayList = dao.getCommonList(QueryId, new Object[]{s_CAR_SCH_ID});
			
			if(aimBayList.size() > 0){
		    	JDTORecord aimBayrec = (JDTORecord)aimBayList.get(0);
		    	s_STACK_BAY_GP  = StringHelper.evl(aimBayrec.getFieldString("DEST_BAY"), "D");
			}
			
			
		}		
		else if("DJY25".equals(szSPOS_WLOC_CD)||"DYY15".equals(szSPOS_WLOC_CD)||"BSY01".equals(szSPOS_WLOC_CD)||"BSY02".equals(szSPOS_WLOC_CD)||"BSY03".equals(szSPOS_WLOC_CD)) { //(비상야드추가)
			logger.println(LogLevel.DEBUG,this, "슬라브 발지개소가 통합야드이므로 통합야드기준 조회");
			
			String QueryId = "ym.tsinfo.getListAimBay_Port";
		
			aimBayList = dao.getCommonList(QueryId, new Object[]{s_CAR_SCH_ID});
			
			if(aimBayList.size() > 0){
		    	JDTORecord aimBayrec = (JDTORecord)aimBayList.get(0);
		    	s_STACK_BAY_GP  = StringHelper.evl(aimBayrec.getFieldString("DEST_BAY"), "D");
			}
			
			
			
		}else if(szSPOS_WLOC_CD.equals("C3S01"))
		{
			logger.println(LogLevel.DEBUG,this, "슬라브 발지개소가 C3스카핑야드이므로 C3스카핑기준 조회");
			
			String QueryId = "ym.tsinfo.getListAimBay_C3Cast";
		
			aimBayList = dao.getCommonList(QueryId, new Object[]{s_CAR_SCH_ID});
			
			if(aimBayList.size() > 0){
		    	JDTORecord aimBayrec = (JDTORecord)aimBayList.get(0);
		    	s_STACK_BAY_GP  = StringHelper.evl(aimBayrec.getFieldString("DEST_BAY"), "D");
			}
			
			
			
		}else if(szSPOS_WLOC_CD.equals("DHY21"))
		{
			logger.println(LogLevel.DEBUG,this, "슬라브 발지개소가 C연주이므로 C연주기준 조회");
			
			String QueryId2 = "ym.tsinfo.getListAimBay_CCast2";
			
			aimBayList = dao.getCommonList(QueryId2, new Object[]{s_CAR_SCH_ID});
			//pallet조회에서 지정한 목적동으로 배차.
			if(aimBayList.size() > 0){
		    	JDTORecord aimBayrec = (JDTORecord)aimBayList.get(0);
		    	s_STACK_BAY_GP  = StringHelper.evl(aimBayrec.getFieldString("DEST_BAY"), "D");
			}else{
						
				String QueryId = "ym.tsinfo.getListAimBay_CCast";
				
				aimBayList = dao.getCommonList(QueryId, new Object[]{s_CAR_SCH_ID});
				
				if(aimBayList.size() > 0){
			    	JDTORecord aimBayrec = (JDTORecord)aimBayList.get(0);
			    	s_STACK_BAY_GP  = StringHelper.evl(aimBayrec.getFieldString("DEST_BAY"), "D");
				}
			}
			
			
			
		}

				    	
	    return s_STACK_BAY_GP;
	}
	
	
	
	
	/**
     * hyuksang
	 * A열연 코일의 목적동을 가져온다
	 * 
     *
     * @param  String	:	차량스케쥴 코드
     *
     * @return String
     * @throws  
     */	
	private String getACoilAimCd(String s_CAR_SCH_ID)
	{	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		ymCommonDAO dao = ymCommonDAO.getInstance();
		String rVal = "";
		
		String sProgCd   = "";
		String sStocMv   = "";
		String sReturnGp = "";

		String s_STACK_BAY_GP 	= "";
		
		List aimBayList = null;
		
		String QueryId = "ym.tsinfo.getListAimBay_ACoil";
		/*
		SELECT DEST_BAY
		FROM TB_YM_SLABMOVEBAYRANKING
		WHERE RANKING = (
		    SELECT MIN(RANKING)
		    FROM TB_YM_SLABMOVEBAYRANKING A,
		      (
		        SELECT DECODE(B.NEXT_PROC, '1H', '1', '1K', '2') AS T1,
		        FROM TB_YD_CARFTMVMTL A,
		          TB_PT_COILCOMM B
		        WHERE A.STL_NO = B.COIL_NO
		          AND A.YD_CAR_SCH_ID = :YD_CAR_SCH_ID )B
		    WHERE A.SLAB_GP IN (B.T1)
		      )
		AND ORD_YEOJAE_GP = '3' --A열연  
		*/
		aimBayList = dao.getCommonList(QueryId, new Object[]{s_CAR_SCH_ID});
		
		if(aimBayList.size() > 0){
	    	JDTORecord aimBayrec = (JDTORecord)aimBayList.get(0);
	    	s_STACK_BAY_GP  = StringHelper.evl(aimBayrec.getFieldString("DEST_BAY"), "");
		}
	    	
	    return s_STACK_BAY_GP;
	}
	
	
	
	
	/**
     * hyuksang
	 * B열연 코일의 목적동을 가져온다
	 * 
     *
     * @param  String	:	차량스케쥴 코드
     *
     * @return String
     * @throws  
     */	
	private String getBCoilAimCd(String s_CAR_SCH_ID)
	{	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		ymCommonDAO dao = ymCommonDAO.getInstance();
		String rVal = "";
		
		String sProgCd   = "";
		String sStocMv   = "";
		String sReturnGp = "";

		String s_STACK_BAY_GP 	= "";
		
		List aimBayList = null;
		
		String QueryId = "ym.tsinfo.getListAimBay_BCoil";
		/*
		SELECT DEST_BAY
		FROM TB_YM_SLABMOVEBAYRANKING
		WHERE RANKING = (
		    SELECT MIN(A.RANKING)
		    FROM TB_YM_SLABMOVEBAYRANKING A,
		      (
		        SELECT DECODE(B.NEXT_PROC, '5H', '1', '5K', '2', '6K', '3', '6R' ,'4') AS T1
		        FROM TB_YD_CARFTMVMTL A,
		          TB_PT_COILCOMM B
		        WHERE A.STL_NO = B.COIL_NO
		          AND A.YD_CAR_SCH_ID = :YD_CAR_SCH_ID )B
		    WHERE A.SLAB_GP IN (B.T1)
		      )
		AND ORD_YEOJAE_GP = '4' --B열연
		*/
		aimBayList = dao.getCommonList(QueryId, new Object[]{s_CAR_SCH_ID});
		
		if(aimBayList.size() > 0){
	    	JDTORecord aimBayrec = (JDTORecord)aimBayList.get(0);
	    	s_STACK_BAY_GP  = StringHelper.evl(aimBayrec.getFieldString("DEST_BAY"), "");
		}
	    	
	    return s_STACK_BAY_GP;
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 :출발취소정보 수신시(TSYDJ014)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */	
	public boolean procStartdeleteRev(JDTORecord msgRecord)throws DAOException 
	{
		
		logger.println(LogLevel.DEBUG,this, "출발취소처리 호출===");
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		boolean isSuccess = false;
		
		YdStockDAO ydStockDAO = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO = new YdWBookDAO();
		ymCommonDAO dao = ymCommonDAO.getInstance();
	    
	    
		int iSeq = 0;
	    
	    String szMsg           				= "";
	    String szMethodName    				= "procStartdeleteRev";
	    
	    String szQuery         				= "";
	    String queryID         				= "";

	    String szTRN_EQP_CD    				= "";
	    String szTRN_WRK_FULLVOID_GP 		= "";
	    
	    String s_STOCK_ID = "";
	    
	    List FrtostlList = null;


		String szRcvTcCode=YmCommonUtil.getTcCode(msgRecord);
		if(szRcvTcCode==null){
			szMsg=szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			
			return false;
		}
		if(bDebugFlag){
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
		}
		
	    try{
	    	szTRN_EQP_CD			  = YmCommonUtil.paraRecChkNull(msgRecord, "TRN_EQP_CD");
		    szTRN_WRK_FULLVOID_GP     = YmCommonUtil.paraRecChkNull(msgRecord, "TRN_WRK_FULLVOID_GP");
		    
		    
		    /**
    		 *  기존 출발정보 여부 확인 
    		 */		
			   logger.println(LogLevel.DEBUG,this,"=기존 출발정보 여부 확인 ");
    			/*
    			 * select YD_CAR_SCH_ID,YD_CARLD_WRK_BOOK_ID
                   from TB_YD_CARSCH
                   where TRN_EQP_CD = ?
                   and DEL_YN = 'N'
                   and YD_CAR_PROG_STAT  in ('1','2')
    			 */
    				    			
    		    String QueryId 	= "ym.tsinfo.getListSposYNchk_E";
    		    List sposChklist = dao.getCommonList(QueryId, new Object[]{szTRN_EQP_CD});
    		    if(sposChklist.size() > 0)
    		    { 	
    		    	logger.println(LogLevel.DEBUG,this,"=이미 공차출발정보가 있음 ");
    		    	
    		    	JDTORecord sposChkReq = (JDTORecord)sposChklist.get(0);
    		    	String s_SPOS_WLOC_CD	= StringHelper.evl(sposChkReq.getFieldString("SPOS_WLOC_CD"), "");
			    	String s_CARLD_WRK_BOOK_ID	= StringHelper.evl(sposChkReq.getFieldString("YD_CARLD_WRK_BOOK_ID"), "");
		    	String s_YD_CAR_SCH_ID	= StringHelper.evl(sposChkReq.getFieldString("YD_CAR_SCH_ID"), "");
			    	String s_YD_CAR_PROG_STAT	= StringHelper.evl(sposChkReq.getFieldString("YD_CAR_PROG_STAT"), "");
			    	
			    	logger.println(LogLevel.DEBUG,this,"=상차개소 = "+ s_SPOS_WLOC_CD);
			    	logger.println(LogLevel.DEBUG,this,"=취소할 작업예약 ID = "+ s_CARLD_WRK_BOOK_ID);
			    	logger.println(LogLevel.DEBUG,this,"=취소할 차량작업 ID = "+ s_YD_CAR_SCH_ID);
			    	logger.println(LogLevel.DEBUG,this,"=취소할 차량상태 = "+ s_YD_CAR_PROG_STAT);
			    	
			    	
			    	
			    	logger.println(LogLevel.DEBUG,this,"=AB열연스케쥴 존재여부 확인 = ");
			    	QueryId 	= "ym.tsinfo.getListSchchkYN";
	    		    List Schchklist = dao.getCommonList(QueryId, new Object[]{s_CARLD_WRK_BOOK_ID});
	    		    logger.println(LogLevel.DEBUG,this,"=AB열연 크레인스케쥴 존재 ================= " + Schchklist.size());
	    		    
	    		    logger.println(LogLevel.DEBUG,this,"=신규야드스케쥴 존재여부 확인 = ");
			    	QueryId 	= "ym.tsinfo.getListYDSchchkYN";
	    		    List Schchklist1 = dao.getCommonList(QueryId, new Object[]{s_CARLD_WRK_BOOK_ID});
	    		    logger.println(LogLevel.DEBUG,this,"=신규야드 크레인스케쥴 존재================= " + Schchklist1.size());
	    		    
	    		     
	    		    if(Schchklist.size()>0)
	    		    {
	    		    	logger.println(LogLevel.DEBUG,this,"=AB열연크레인스케쥴 존재함 ");
	    		    	return false;	    		    		    		    	
	    		    }
	    		    if(Schchklist1.size()>0)
	    		    {
	    		    	logger.println(LogLevel.DEBUG,this,"=신규야드크레인스케쥴 존재함 ");
	    		    	return false;	    		    		    		    	
	    		    }
	    		    
	    		    
	    		    else
	    		    {
	    		    	
	    		    	
	    		    	 //AB열연으로 출발한 정보 취소
	    		    	  if("D2Y43".equals(s_SPOS_WLOC_CD)|| //A연주-B Cast Slab Yard
		    		 		 "D3Y43".equals(s_SPOS_WLOC_CD)|| //B열연-Slab Yard
		    				"D3Y44".equals(s_SPOS_WLOC_CD)|| //B열연-가열로 Slab Yard
		    				"D2Y44".equals(s_SPOS_WLOC_CD)|| //A열연-#1 제품/소재 Coil Yard
		    				"D2Y45".equals(s_SPOS_WLOC_CD)|| //A열연-#2 제품/소재 Coil Yard
		    				"D3Y41".equals(s_SPOS_WLOC_CD)|| //B열연-#1 제품/소재 Coil Yard
		    				"D3Y42".equals(s_SPOS_WLOC_CD)){ //B열연-#2 제품/소재 Coil Yard
	    		    		  
	    		    		logger.println(LogLevel.DEBUG,this,"=상차개소가  "+s_SPOS_WLOC_CD+" AB열연 임"); 
	    		    		  

			    		    	logger.println(LogLevel.DEBUG,this,"=작업예약삭제");
						    	dao.removeWBook(s_CARLD_WRK_BOOK_ID);
					    	
					    	
					    	if(s_YD_CAR_PROG_STAT.equals("1"))
					    	{
//					    		예약정보 삭제
						    	logger.println(LogLevel.DEBUG,this,"=예약위치정보삭제");
						    	QueryId = "ym.steelinfo.steelinforecv.YdStockDAO.delStkBookDtl";
								iSeq = ydStockDAO.requestupdateData(QueryId, new Object[]{szTRN_EQP_CD});
								/*
								update TB_YM_STACKCOL
								set STACK_STAT = ''
								    , CAR_CARD_NO = ''
								where  CAR_CARD_NO = ?  
					    		*/
								logger.println(LogLevel.DEBUG,this,"=예약 위치정보삭제 완료건수" + iSeq);
					    	}
					    	else if(s_YD_CAR_PROG_STAT.equals("2"))
					    	{
					    		
					    		logger.println(LogLevel.DEBUG,this,"=야드맵정보 close");
								/*
				                 *	적치단  Table Update(close 로 변경)
				                 */
				            	String sQuery4 = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark_empty";
				            	iSeq = ydStockDAO.requestupdateData(sQuery4, 
																    new Object[]{"C", 
				            			                               szTRN_EQP_CD });	
					    		
					    		logger.println(LogLevel.DEBUG,this,"=현재위치정보삭제");
						    	QueryId = "ym.steelinfo.steelinforecv.YdStockDAO.delStkBookDtlArr";
								iSeq = ydStockDAO.requestupdateData(QueryId, new Object[]{szTRN_EQP_CD});
								/*
								update TB_YM_STACKCOL
								set YD_CAR_USE_GP = ''
								    , TRN_EQP_CD = ''
								where  TRN_EQP_CD = ?  
								*/
								
								logger.println(LogLevel.DEBUG,this,"=현재위치정보삭제 완료건수" + iSeq);
			    		
					    	}
							
			    	
		    		    	// 이송재료 조회(등록할것)
							queryID 		= "ym.tsinfo.getListFrtostlList";
							FrtostlList 	= dao.getCommonList(queryID, new Object[]{szTRN_EQP_CD});
							/*
							SELECT STL_NO,YD_STK_BED_NO,SUBSTR(YD_STK_LYR_NO,2,2)STK_LYR
							FROM TB_YD_CARFTMVMTL A
							WHERE A.YD_CAR_SCH_ID = (select max(A.YD_CAR_SCH_ID)
							                      from TB_YD_CARSCH A
							                      where A.TRN_EQP_CD = ?
							                      AND A.DEL_YN = 'N')
							AND A.DEL_YN = 'N'
							ORDER BY A.YD_STK_LYR_NO
		    				*/
							int iSeqCount 	= FrtostlList.size();
															
								for(int i=0; i < iSeqCount ; i++){
				                	
						    		JDTORecord FrtoSltrec = (JDTORecord)FrtostlList.get(i);
									s_STOCK_ID = StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"),"");
									 
									queryID = "ym.steelinfo.steelinforecv.YdStockDAO.delWbookID";
									iSeq = ydStockDAO.requestupdateData(queryID, new Object[]{s_STOCK_ID});

									logger.println(LogLevel.DEBUG,this,"=저장품테이블 업데이트 재료번호" + s_STOCK_ID);
									//저장품테이블에 update		
									/*update TB_YM_STOCK
									set WBOOK_ID = ''
									where  STOCK_ID = ?*/
									
									logger.println(LogLevel.DEBUG,this,"=차량 이송대상제테이블 업데이트 재료번호" + s_STOCK_ID);
//									차량재료정보테이블테이블 update			
									queryID = "ym.steelinfo.steelinforecv.YdStockDAO.delstlList";
									iSeq = ydStockDAO.deleteData(queryID, new Object[]{s_STOCK_ID});
									
									/*
									update TB_YD_CARFTMVMTL
			                        set DEL_YN = 'Y'
			                        WHERE  STL_NO = ?   
									*/
									
									logger.println(LogLevel.DEBUG,this,"=차량 적치단정보  업데이트 재료번호" + s_STOCK_ID);
									/*
					                 *	적치단  Table Update(작업요구상태='L'로 변경)
					                 */
					            	String sQuery4 = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
					            	iSeq = ydStockDAO.requestupdateData(sQuery4, 
																	    new Object[]{YmCommonConst.STACK_LAYER_STAT_L, 
					            										s_STOCK_ID });
					            
						    	}

						
			                logger.println(LogLevel.DEBUG,this,"=차량스케쥴정보 업데이트" + s_YD_CAR_SCH_ID);
			                //차량스케쥴정보 삭제
			                queryID = "ym.steelinfo.steelinforecv.YdStockDAO.delCarschID";
							iSeq = ydStockDAO.requestupdateData(queryID, new Object[]{s_YD_CAR_SCH_ID}); 
							/*
							update TB_YD_CARSCH
							set DEL_YN = 'Y'
							where  YD_CAR_SCH_ID = ? 
							*/
							logger.println(LogLevel.DEBUG,this,"=업데이트 차량스케쥴 건 수" + iSeq);
							
							//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
						    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
			
	    		        } //AB야드 end
	    		    	  		    	  
	    		    	  //신규야드로 출발한 정보 취소
	    		    	  else { 
	    		    		    logger.println(LogLevel.DEBUG,this,"=상차개소가  "+s_SPOS_WLOC_CD+" 신규야드 임"); 
	    		    		  
			    		    	logger.println(LogLevel.DEBUG,this,"=작업예약삭제");
						    	QueryId = "ym.steelinfo.steelinforecv.YdStockDAO.delYDStkBook";
								iSeq = ydStockDAO.requestupdateData(QueryId, new Object[]{s_CARLD_WRK_BOOK_ID});
						    	
						    	logger.println(LogLevel.DEBUG,this,"=작업예약재료삭제");
						    	QueryId = "ym.steelinfo.steelinforecv.YdStockDAO.delYDStkBookDtl";
								iSeq = ydStockDAO.requestupdateData(QueryId, new Object[]{s_CARLD_WRK_BOOK_ID});
					    	
					    	
					    	if(s_YD_CAR_PROG_STAT.equals("1")||s_YD_CAR_PROG_STAT.equals("2"))
					    	{
//					    		예약정보 삭제
						    	logger.println(LogLevel.DEBUG,this,"=예약위치정보삭제");
						    	QueryId = "ym.steelinfo.steelinforecv.YdStockDAO.delYDStkBookLoc";
								iSeq = ydStockDAO.requestupdateData(QueryId, new Object[]{szTRN_EQP_CD});
								/*
								update TB_YD_STKCOL
                                set YD_STK_COL_ACT_STAT = 'C'
                                and YD_CAR_USE_GP = ''
                                and TRN_EQP_CD = ''
                                where TRN_EQP_CD = ? 
					    		*/
								logger.println(LogLevel.DEBUG,this,"=예약 위치정보삭제 완료건수" + iSeq);
					    	}					    			    	

			                
			                logger.println(LogLevel.DEBUG,this,"=차량스케쥴정보 업데이트" + s_YD_CAR_SCH_ID);
			                //차량스케쥴정보 삭제
			                queryID = "ym.steelinfo.steelinforecv.YdStockDAO.delCarschID";
							iSeq = ydStockDAO.requestupdateData(queryID, new Object[]{s_YD_CAR_SCH_ID}); 
							/*
							update TB_YD_CARSCH
							set DEL_YN = 'Y'
							where  YD_CAR_SCH_ID = ? 
							*/
							logger.println(LogLevel.DEBUG,this,"=업데이트 차량스케쥴 건 수" + iSeq);	
							
							//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
						    CarPointinforeg("1","",szTRN_EQP_CD,"","","","C");
						    
	    		      }	//신규야드 end							
	               }		
   		    
    		    }	
    		    
    		    else 
    		    {
    		    	logger.println(LogLevel.DEBUG,this,"=이전 출발정보가 없음 ");
    		    	return false;
    		    }
		    
          	
		}catch(Exception e){
			
			szMsg="출발취소처리 Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}

		
		szMsg="출발취소처리("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);

		return true;
		
	}

	
	
	
	
	
/**
	 *      [A] 오퍼레이션명 :상차완료처리
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public boolean effectcarLoadEnd(String pos)throws DAOException  {
		
		logger.println(LogLevel.DEBUG,this, "START effectcarLoadEnd : 상차완료처리 호출===");

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		Boolean isSuccess = new Boolean(false);
		
		YdStockDAO ydStockDAO = new YdStockDAO();

		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		CoilSpecRegSeEJBBean CoilSpecRegSeEJBBean=new CoilSpecRegSeEJBBean();
	    
		int count = 0;
		int iSeq = 0;
	    int intRtnVal= 0 ;

        String slab_no = "";
	    String sTrnEqpCd ="";
	    String szMsg           				= "";
	    String szMethodName    				= "effectcarLoadEnd";

	    String queryID         				= "";
	    String recordProgStat               = "";
	    String  szPT_TB_COMM = "";
	    
	    List FrtoProductList = null;
	    List loadList = null;
	    List FrtostlList = null;
	    List FrtoProductList2 = null;


		
	    try{
	    	 logger.println(LogLevel.DEBUG,this, "상차완료 할 정지위치==="+pos);
	    	 
	    	int nIdx = 0;
 			
 			//장비번호 가져오기 					
			queryID	= "ym.tsinfo.getLoadendLayer";
			loadList = dao.getCommonList(queryID, new Object[]{pos});
			
			if(loadList.size()>0){
				JDTORecord FrtoProduct = (JDTORecord)loadList.get(0);	    	
		    	sTrnEqpCd = StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"),"");
		    	
		    	if(sTrnEqpCd.equals("")){
		    		return false;
		    	}
			}else {
				return false;
			}
 			
 			//차량에 이송대상재 삭제	
			queryID	= "ym.tsinfo.getListLoadEndStldel";
			count = dao.deleteData(queryID, new Object[]{sTrnEqpCd});
			logger.println(LogLevel.DEBUG,this, "차량에 이송대상재 삭제 건수==="+count);
			
			//차량 제품정보 등록
			queryID = "ym.tsinfo.updateLoadendLayer";
 			count = dao.updateData(queryID, new Object[]{pos});		
 			logger.println(LogLevel.DEBUG,this, "차량 제품정보 등록 건수==="+count);
 			
	    	
	    	//차량스케쥴ID로 이송재료 조회
    		String trnQueryId = "ym.tsinfo.getListFrtostlList_loadEnd_PIDEV";
    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{sTrnEqpCd});
 			
    		JDTORecord tcRecord = null;
    		int iSeqCount 	= FrtostlList.size();
    		for(int i=0; i < iSeqCount ; i++){
				
	    		JDTORecord FrtoSltrec = (JDTORecord)FrtostlList.get(i);
				String s_STOCK_ID = StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"),"");
				
				//TB_PT_STLFRTOMOVE update				
				String stkQueryId = "ym.facilitywork.putwrecord.session.updateLoadTimeToPT";
				iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID, 
						                                                          s_STOCK_ID});
				
				//주편공통에서 현재 종료상태 확인						
				queryID	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getMSLABCOMM";
		    	FrtoProductList2 = dao.getCommonList(queryID, new Object[]{s_STOCK_ID});
		    	JDTORecord FrtoProduct3 = (JDTORecord)FrtoProductList2.get(0);
		    	
		    	recordProgStat = StringHelper.evl(FrtoProduct3.getFieldString("RECORD_PROG_STAT"),"");
		    		    	
		    	
		    	//주편or슬라브 공통 소재이송일시 등록(MATL_FTMV_DT)
		    	if(recordProgStat.equals("3")){
		    		stkQueryId = "ym.facilitywork.putwrecord.session.updateMatlFtmvTimeSlab";
					iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID});
					szPT_TB_COMM = "S";
		    	}else{
		    		stkQueryId = "ym.facilitywork.putwrecord.session.updateMatlFtmvTimeMSlab"; 
				    iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID});
				    szPT_TB_COMM = "B";
		    	}
		    	
		    	if(i == iSeqCount-1){
	        	//상위 단정보 업데이트				    		
    	    	     String stkBed  = "01";
    	    	     String stkLayer  = StringHelper.evl(FrtoSltrec.getFieldString("STK_LYR"),"");
    	    	     String stk_col = pos;
		    	     stkQueryId = "ym.facilitywork.putwrecord.session.updateUpperlayerStat";
		    	     iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ stk_col,stkBed,stkLayer});
		    	  /*
		    	 * update TB_YM_STACKLAYER
                   set STACK_LAYER_STAT = 'V'
                   where STACK_COL_GP = :STACK_COL_GP
                   and STACK_BED_GP = :STACK_BED_GP
                   and STACK_LAYER_GP > :STACK_LAYER_GP
		    	 */
	        	    logger.println(LogLevel.DEBUG,this, "===상차완료단정보=== COL : "+stk_col+" BED :"+stkBed+" LAYER : "+stkLayer);
    		    }
		    	
			    			    					    	
				if(i == 0){
    			   //상차완료실적 송신
                   //소재차량상차완료
    			   tcRecord = JDTORecordFactory.getInstance().create(); 
				   tcRecord.setField("JMS_TC_CD", "YDTSJ008");
				   tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));					
				   tcRecord.setField("TRN_EQP_CD", StringHelper.evl(FrtoSltrec.getFieldString("TRN_EQP_CD"), ""));
				   tcRecord.setField("SPOS_WLOC_CD", StringHelper.evl(FrtoSltrec.getFieldString("SPOS_WLOC_CD"), ""));
				   tcRecord.setField("SPOS_YD_PNT_CD", StringHelper.evl(FrtoSltrec.getFieldString("YD_PNT_CD"), ""));
				   tcRecord.setField("ARR_WLOC_CD", StringHelper.evl(FrtoSltrec.getFieldString("ARR_WLOC_CD"), ""));
				   tcRecord.setField("TRN_WRK_MTL_GP", StringHelper.evl(FrtoSltrec.getFieldString("TRN_WRK_MTL_GP"), ""));
				   tcRecord.setField("MTL_UGNT_GP", StringHelper.evl(FrtoSltrec.getFieldString("MTL_UGNT_GP"), ""));
				   tcRecord.setField("HCR_GP", StringHelper.evl(FrtoSltrec.getFieldString("HCR_GP"), ""));
				   tcRecord.setField("CARLD_SH", Integer.toString(iSeqCount));
				}
				tcRecord.setField("STL_NO" + (1+nIdx), StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), ""));
				tcRecord.setField("STL_WT" + (1+nIdx), StringHelper.evl(FrtoSltrec.getFieldString("STL_WT"), ""));
				nIdx ++;
				
				
				logger.println(LogLevel.DEBUG,this, "상차완료 후 저장품 정보 YD송신.===");
				
				String s_YD_GP = getYdFromWlocCd(StringHelper.evl(FrtoSltrec.getFieldString("ARR_WLOC_CD"), ""));	
				
				//c열연과 후판,slab간에 분리처리
				if(s_YD_GP.equals("H")){

					CoilSpecRegSeEJBBean.stockProcCom(StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), ""),1);

				}else {
			        JDTORecord ydStlRecord = null;
			        ydStlRecord = JDTORecordFactory.getInstance().create();
			        ydStlRecord.setField("PT_TB_COMM",szPT_TB_COMM);
			        ydStlRecord.setField("STL_NO", StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), ""));							        
			        ydStlRecord.setField("SLAB_WO_RT_CD", StringHelper.evl(FrtoSltrec.getFieldString("SLAB_WO_RT_CD"), ""));
			        ydStlRecord.setField("ORD_YEOJAE_GP", StringHelper.evl(FrtoSltrec.getFieldString("ORD_YEOJAE_GP"), ""));
			        ydStlRecord.setField("SCARFING_YN", StringHelper.evl(FrtoSltrec.getFieldString("SCARFING_YN"), ""));
			        ydStlRecord.setField("SCARFING_DONE_YN", StringHelper.evl(FrtoSltrec.getFieldString("SCARFING_DONE_YN"), ""));
			        ydStlRecord.setField("MILL_WO_EXN", StringHelper.evl(FrtoSltrec.getFieldString("MILL_WO_EXN"), ""));
			        ydStlRecord.setField("YD_GP", s_YD_GP);					        
			        ydStlRecord.setField("STL_APPEAR_GP", StringHelper.evl(FrtoSltrec.getFieldString("STL_APPEAR_GP"), ""));
			        ydStlRecord.setField("HCR_GP", StringHelper.evl(FrtoSltrec.getFieldString("HCR_GP"), ""));
			        
			        String szRetunMsg = YdCommonUtils.uptStockCodeMapping(ydStlRecord);
				}
    		}   		
    		tcRecord.setField("YD_CARLD_END_DT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
    		EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
		    isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
		  	  	             new Object[]{tcRecord});
		    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 상차완료실적.zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz===");

		    
	    	
// 			차량스케쥴테이블 - 상차완료시간 업데이트
 			queryID = "ym.tsinfo.updatecarLoadend";
 			count = dao.updateData(queryID, new Object[]{sTrnEqpCd}); 
 			
		    return isSuccess.booleanValue();
	    	      	
		}catch(Exception e){
			m_ctx.setRollbackOnly();
			throw new DAOException(e);
		}
	}// end of effectcarLoadEnd()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 :상차완료처리(B열연 상차완료 처리)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public boolean effectcarLoadEnd2(String trneqp)throws JDTOException  {
		
		logger.println(LogLevel.DEBUG,this, "START effectcarLoadEnd : 상차완료처리 호출===");
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		
		Boolean isSuccess = new Boolean(false);
		
		YdStockDAO ydStockDAO = new YdStockDAO();

		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		CoilSpecRegSeEJBBean CoilSpecRegSeEJBBean=new CoilSpecRegSeEJBBean();
	    
		JDTORecord Paramrecord = null;
		
		int count = 0;
		int iSeq = 0;
	    int intRtnVal= 0 ;

        String slab_no = "";
	    
	    String szMsg           				= "";
	    String szMethodName    				= "effectcarLoadEnd";

	    String queryID         				= "";
	    String CurrProg_CD                  = "";
	    String  szPT_TB_COMM = "";
	    
	    List FrtoProductList = null;
	    List loadList = null;
	    List FrtostlList = null;
	    List FrtoProductList2 = null;


		
	    try{
	    	 logger.println(LogLevel.DEBUG,this, "상차완료 할 장비번호==="+trneqp);
	    	 
	    	int nIdx = 0;
	    	
// 			차량스케쥴테이블 - 상차완료시간 업데이트
 			queryID = "ym.facilitywork.putwrecord.session.updateLoadendTime2";
 			count = dao.updateData(queryID, new Object[]{trneqp}); 
 			/*
			update TB_YD_CARSCH A
			set A.MODIFIER = 'BYD',
			    A.MOD_DDTT=SYSDATE,
			    A.YD_EQP_WRK_STAT = 'L',
			    A.YD_CARLD_CMPL_DT  =SYSDATE,
			    A.YD_CAR_PROG_STAT = '5'
			where A.YD_CAR_SCH_ID in (select MAX(YD_CAR_SCH_ID)
			                         from TB_YD_CARSCH B
			                         where A.TRN_EQP_CD = B.TRN_EQP_CD
			                         and DEL_YN = 'N') 
			AND  A.TRN_EQP_CD = ?                        
			and A.DEL_YN = 'N'
 			 */
 			if(count == 0){
 				
 				return true;
 			}
 			
 			//이송대상재료 조회					
			queryID	= "ym.tsinfo.getListFrtostlList_loadEnd_PIDEV";
			loadList = dao.getCommonList(queryID, new Object[]{trneqp});
	    	JDTORecord FrtoProduct = (JDTORecord)loadList.get(0);	    	
	    	slab_no = StringHelper.evl(FrtoProduct.getFieldString("STL_NO"),"");
	    	
	    	
	    	queryID	= "ym.facilitywork.putwrecord.session.getListTcconst";
	    	FrtoProductList = dao.getCommonList(queryID, new Object[]{slab_no});
	    	JDTORecord FrtoProduct2 = (JDTORecord)FrtoProductList.get(0);
	    	
	    	//차량스케쥴ID로 이송재료 조회(
    		String trnQueryId = "ym.tsinfo.getListFrtostlList_loadEnd_PIDEV";
    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{StringHelper.evl(FrtoProduct2.getFieldString("TRN_EQP_CD"), "")});
 			
    		JDTORecord tcRecord = null;
    		int iSeqCount 	= FrtostlList.size();
    		for(int i=0; i < iSeqCount ; i++){
				
	    		JDTORecord FrtoSltrec = (JDTORecord)FrtostlList.get(i);
				String s_STOCK_ID = StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"),"");
				
				//TB_PT_STLFRTOMOVE update				
				String stkQueryId = "ym.facilitywork.putwrecord.session.updateLoadTimeToPT";
				iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID, 
						                                                          s_STOCK_ID});
				
				//주편공통에서 현재 진도코드 확인						
				queryID	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getMSLABCOMM";
		    	FrtoProductList2 = dao.getCommonList(queryID, new Object[]{s_STOCK_ID});
		    	JDTORecord FrtoProduct3 = (JDTORecord)FrtoProductList2.get(0);
		    	
		    	CurrProg_CD = StringHelper.evl(FrtoProduct2.getFieldString("RECORD_PROG_STAT"),"");
		    		    	
		    	
		    	if(CurrProg_CD.equals("3"))
		    	{
		    		stkQueryId = "ym.facilitywork.putwrecord.session.updateMatlFtmvTimeSlab";
					iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID});
					szPT_TB_COMM = "S";
		    	}
		    	
		    	else
		    	{
		    		stkQueryId = "ym.facilitywork.putwrecord.session.updateMatlFtmvTimeMSlab"; 
				    iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID});
				    szPT_TB_COMM = "B";
		    	}
		    	
		    	if(i == iSeqCount-1){
	        	//상위 단정보 업데이트				    		
    	    	     String stkBed  = "01";
    	    	     String stkLayer  = StringHelper.evl(FrtoSltrec.getFieldString("STK_LYR"),"");
    	    	     String stk_col = StringHelper.evl(FrtoSltrec.getFieldString("STORE_LOC_CD"),"");
    	    	     stk_col = stk_col.substring(0, 6);
		    	     stkQueryId = "ym.facilitywork.putwrecord.session.updateUpperlayerStat";
		    	     iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ stk_col,stkBed,stkLayer});
		    	  /*
		    	 * update TB_YM_STACKLAYER
                   set STACK_LAYER_STAT = 'V'
                   where STACK_COL_GP = :STACK_COL_GP
                   and STACK_BED_GP = :STACK_BED_GP
                   and STACK_LAYER_GP > :STACK_LAYER_GP
		    	 */
	        	    logger.println(LogLevel.DEBUG,this, "===상차완료단정보=== COL : "+stk_col+" BED :"+stkBed+" LAYER : "+stkLayer);
    		    }
		    	
			    			    					    	
				if(i == 0){
    			   //상차완료실적 송신
                   //소재차량상차완료
    			   tcRecord = JDTORecordFactory.getInstance().create(); 
				   tcRecord.setField("JMS_TC_CD", "YDTSJ008");
				   tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));					
				   tcRecord.setField("TRN_EQP_CD", StringHelper.evl(FrtoSltrec.getFieldString("TRN_EQP_CD"), ""));
				   tcRecord.setField("SPOS_WLOC_CD", StringHelper.evl(FrtoSltrec.getFieldString("SPOS_WLOC_CD"), ""));
				   tcRecord.setField("SPOS_YD_PNT_CD", StringHelper.evl(FrtoSltrec.getFieldString("YD_PNT_CD"), ""));
				   tcRecord.setField("ARR_WLOC_CD", StringHelper.evl(FrtoSltrec.getFieldString("ARR_WLOC_CD"), ""));
				   tcRecord.setField("TRN_WRK_MTL_GP", StringHelper.evl(FrtoSltrec.getFieldString("TRN_WRK_MTL_GP"), ""));
				   tcRecord.setField("MTL_UGNT_GP", StringHelper.evl(FrtoSltrec.getFieldString("MTL_UGNT_GP"), ""));
				   tcRecord.setField("HCR_GP", StringHelper.evl(FrtoSltrec.getFieldString("HCR_GP"), ""));
				   tcRecord.setField("CARLD_SH", Integer.toString(iSeqCount));
				}
				tcRecord.setField("STL_NO" + (1+nIdx), StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), ""));
				tcRecord.setField("STL_WT" + (1+nIdx), StringHelper.evl(FrtoSltrec.getFieldString("STL_WT"), ""));
				nIdx ++;
				
				
				logger.println(LogLevel.DEBUG,this, "상차완료 후 저장품 정보 YD송신.===");
				
				String s_YD_GP = getYdFromWlocCd(StringHelper.evl(FrtoSltrec.getFieldString("ARR_WLOC_CD"), ""));	
				
				//c열연과 후판,slab간에 분리처리
				if(s_YD_GP.equals("H")){

					CoilSpecRegSeEJBBean.stockProcCom(StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), ""),1);

				}else {
			        JDTORecord ydStlRecord = null;
			        ydStlRecord = JDTORecordFactory.getInstance().create();
			        ydStlRecord.setField("PT_TB_COMM",szPT_TB_COMM);
			        ydStlRecord.setField("STL_NO", StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), ""));							        
			        ydStlRecord.setField("SLAB_WO_RT_CD", StringHelper.evl(FrtoSltrec.getFieldString("SLAB_WO_RT_CD"), ""));
			        ydStlRecord.setField("ORD_YEOJAE_GP", StringHelper.evl(FrtoSltrec.getFieldString("ORD_YEOJAE_GP"), ""));
			        ydStlRecord.setField("SCARFING_YN", StringHelper.evl(FrtoSltrec.getFieldString("SCARFING_YN"), ""));
			        ydStlRecord.setField("SCARFING_DONE_YN", StringHelper.evl(FrtoSltrec.getFieldString("SCARFING_DONE_YN"), ""));
			        ydStlRecord.setField("MILL_WO_EXN", StringHelper.evl(FrtoSltrec.getFieldString("MILL_WO_EXN"), ""));
			        ydStlRecord.setField("YD_GP", s_YD_GP);					        
			        ydStlRecord.setField("STL_APPEAR_GP", StringHelper.evl(FrtoSltrec.getFieldString("STL_APPEAR_GP"), ""));
			        ydStlRecord.setField("HCR_GP", StringHelper.evl(FrtoSltrec.getFieldString("HCR_GP"), ""));
			        
			        String szRetunMsg = YdCommonUtils.uptStockCodeMapping(ydStlRecord);
				}
		        
		        
		        
				
    		}   		
    		tcRecord.setField("YD_CARLD_END_DT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
    		EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
		    isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
		  	  	             new Object[]{tcRecord});
		    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 상차완료실적.zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz===");

		    return isSuccess.booleanValue();
	    	      	
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}// end of effectcarLoadEnd2()
	
	/**
	 * 관리되는 개소코드를 야드구분으로 변환하는 메소드
	 * @param szWLOC_CD
	 * @return
	 */
	public static String getYdFromWlocCd(String szWLOC_CD) {
		
		String szYD_GP = "";
		if(szWLOC_CD.equals("DHY21") || szWLOC_CD.equals("DHY22")) {				//C연주슬라브
			szYD_GP = YdConstant.YD_GP_C_SLAB_YARD;
		}else if(szWLOC_CD.equals("DJY21") || szWLOC_CD.equals("DJY22")) {			//C열연소재
			szYD_GP = YdConstant.YD_GP_C_HR_COIL_MATL_YARD;
		}else if(szWLOC_CD.equals("DJY15") 
				|| szWLOC_CD.equals("DJY16") 
				|| szWLOC_CD.equals("DJY17") 
    	    	|| szWLOC_CD.equals("DJY18") 
    	    	|| szWLOC_CD.equals("DJY19") 
    	    	|| szWLOC_CD.equals("DJY30")) {										//C열연 코일제품창고
			szYD_GP = YdConstant.YD_GP_C_HR_COIL_GDS_YARD;
    	}else if(szWLOC_CD.equals("DKY21")||szWLOC_CD.equals("DWY22")){	 									//A후판 소재
    		szYD_GP = YdConstant.YD_GP_A_PLATE_SLAB_YARD;
    	}else if(szWLOC_CD.equals("DKY30")) {										//A후판 제품창고
    		szYD_GP = YdConstant.YD_GP_PLATE_GDS_YARD;
    	}else if("DJY25".equals(szWLOC_CD)||"DYY15".equals(szWLOC_CD)||"BSY01".equals(szWLOC_CD)||"BSY02".equals(szWLOC_CD)||"BSY03".equals(szWLOC_CD)) { //(비상야드추가)
    		szYD_GP = YdConstant.YD_GP_INTGR_YARD;
    	}else if( YdConstant.WLOC_CD_A_PLATE_PLANT.equals(szWLOC_CD)||
    			  YdConstant.WLOC_CD_B_PLATE_PLANT.equals(szWLOC_CD)){
    		szYD_GP = YdConstant.YD_GP_A_PLATE_PLANT;								//A후판조업
    	}else if( YdConstant.WLOC_CD_C_HR_PLANT.equals(szWLOC_CD) ) {
    		szYD_GP = YdConstant.YD_GP_C_HR_PLANT;									//C열연조업
		}else if( YdConstant.WLOC_CD_B_HR_PLANT.equals(szWLOC_CD) ) {
			szYD_GP = YdConstant.YD_GP_B_HR_SLAB_YARD;								//B열연
		}
		return szYD_GP;
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 :상차취소처리(BCast)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public boolean effectcarLoadEndDel(String pos)throws JDTOException  {
		
		logger.println(LogLevel.DEBUG,this, "START effectcarLoadEndDel : 상차완료처리 호출===");
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		Boolean isSuccess = new Boolean(false);
		
		YdStockDAO ydStockDAO = new YdStockDAO();

		ymCommonDAO dao = ymCommonDAO.getInstance();

	    
		JDTORecord Paramrecord = null;
		
		int count = 0;
		int iSeq = 0;
	    int intRtnVal= 0 ;

        String slab_no = "";
	    
	    String szMsg           				= "";
	    String szMethodName    				= "effectcarLoadEndDel";

	    String queryID         				= "";
	    String CurrProg_CD                  = "";
	    

		
	    try{
	    	 logger.println(LogLevel.DEBUG,this, "상차취소처리 할 정지위치==="+pos);
	    	 
	    	int nIdx = 0;
	    	
// 			차량스케쥴테이블 - 차량스케쥴  업데이트
 			queryID = "ym.tsinfo.updateLoadenddelete";
 			count = dao.updateData(queryID, new Object[]{pos}); 
 					    	
	
		    return isSuccess.booleanValue();
	    	      	
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}// end of effectcarLoadEnd()
	
	
	
	
	
	
    /**
     * 스케쥴코드를 리턴한다.
     * @param col	차량정지위치
     * @return
     */
    private String getUnloadSchKind(String item, String pos) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        if(YmCommonConst.ITEM_SM.equals(item)) {
            return YmCommonConst.NEW_SCH_WORK_KIND_SVMU;
        }else {
            if(YmCommonConst.YD_GP_1.equals(pos.substring(0, 1))) {
                if(YmCommonConst.ITEM_CG.equals(item)) {
                	

                		//H동 제품이송 하차 GATE 4,5번은 49번 크레인 작업 진행
                		 if(YmCommonConst.BAY_GP_H.equals(pos.subSequence(1, 2))) {
                			 if("3".equals(pos.substring(5, 6))	||
        	                    "4".equals(pos.substring(5, 6))	||
        	                    "5".equals(pos.substring(5, 6))	||
        	                    "6".equals(pos.substring(5, 6))	||
        	                    "7".equals(pos.substring(5, 6))	) {
        	                    return YmCommonConst.NEW_SCH_WORK_KIND_GVM4;         	          
                			 }else{
                				 return YmCommonConst.NEW_SCH_WORK_KIND_GVMU;
                			 }
                			 
                		 }else{
                			 return YmCommonConst.NEW_SCH_WORK_KIND_GVMU;
                		 }
             
                }else {
                    return YmCommonConst.NEW_SCH_WORK_KIND_CVMU;
                }                
            }else if(YmCommonConst.YD_GP_3.equals(pos.substring(0, 1))) {
                if("3".equals(pos.substring(5, 6)) ||
                   "4".equals(pos.substring(5, 6))) {
                    if(YmCommonConst.ITEM_CG.equals(item)) {
                        return YmCommonConst.NEW_SCH_WORK_KIND_GVM4;
                    }else {
                        return YmCommonConst.NEW_SCH_WORK_KIND_CVM4;
                    }
                }else {
                    if(YmCommonConst.ITEM_CG.equals(item)) {
                        return YmCommonConst.NEW_SCH_WORK_KIND_GVMU;
                    }else {
                        return YmCommonConst.NEW_SCH_WORK_KIND_CVMU;
                    }                    
                }
            }
        }
        return "";
    }
    
    
    

    /**
     * @param schKind
     * @param pos
     * @return
     */
    private String getSchWorkKind(String schKind, String pos) {        
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        if(YmCommonConst.YD_GP_1.equals(pos.substring(0, 1))) {
        	/**
        	 */
        	if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(schKind)) {
        		return schKind;
        	}
        	
        	if(YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(schKind)) {
        		//H동 제품이송 상차 GATE 4,5번은 49번 크레인 작업 진행
        		 if(YmCommonConst.BAY_GP_H.equals(pos.subSequence(1, 2))) {
        			 if("3".equals(pos.substring(5, 6))	||
	                    "4".equals(pos.substring(5, 6))	||
	                    "5".equals(pos.substring(5, 6))	||
	                    "6".equals(pos.substring(5, 6))	||
	                    "7".equals(pos.substring(5, 6))	) {
	                    return YmCommonConst.NEW_SCH_WORK_KIND_GVM2;         	          
        			 }else{
        				return schKind;
        			 }
        			 
        		 }else{
        			 return schKind;
        		 }
        	}
        	
            if(YmCommonConst.BAY_GP_F.equals(pos.subSequence(1, 2))	||
               YmCommonConst.BAY_GP_G.equals(pos.subSequence(1, 2))	||
               YmCommonConst.BAY_GP_H.equals(pos.subSequence(1, 2))) {
                if("3".equals(pos.substring(5, 6))	||
                   "4".equals(pos.substring(5, 6))) {
//                    return YmCommonConst.NEW_SCH_WORK_KIND_GVF1;
                	return schKind.substring(0, 3)+"1";
                }
            }
        }else if(YmCommonConst.YD_GP_3.equals(pos.substring(0, 1))) {
            if("3".equals(pos.substring(5, 6)) ||
               "4".equals(pos.substring(5, 6))) {
                if(YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(schKind)||
                		YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(schKind)||
                		YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(schKind)) {
//                	return YmCommonConst.NEW_SCH_WORK_KIND_GVF1;  
                	return schKind.substring(0, 3)+"1";
                }else if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(schKind)) {
                	return YmCommonConst.NEW_SCH_WORK_KIND_CVM2;
                }else if(YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(schKind)) {
                	return YmCommonConst.NEW_SCH_WORK_KIND_GVM2;
                }
            } else if (YmCommonConst.BAY_GP_E.equals(pos.subSequence(1, 2))) {
            	if("5".equals(pos.substring(5, 6)) ||
                        "6".equals(pos.substring(5, 6))) {
            		if(YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(schKind)||
                    		YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(schKind)||
                    		YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(schKind)) {
            			// Coil 제품출하상차  => 1,2
//            			return YmCommonConst.NEW_SCH_WORK_KIND_GVF2;     
            			return schKind.substring(0, 3)+"2";
                    }else if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(schKind)) {
                    	// Coil 소재이송상차 => 2,3
                    	return YmCommonConst.NEW_SCH_WORK_KIND_CVM3;
                    }else if(YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(schKind)) {
                    	// Coil 제품이송상차 => 2,3
                    	return YmCommonConst.NEW_SCH_WORK_KIND_GVM3;
                    }
            	}
            }
        }
        return schKind;
    }
    
    
    /**
     * JDTORecord 가 가지는 name parameter에 대한 값을 리턴한다.
     * @param data
     * @param name
     * @return
     */
    private String getField(JDTORecord data, String name) {
    	if(data == null){
    		return "";	
    	}else{
       	return StringHelper.evl(data.getFieldString(name), "").trim();
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
     * 지시구분을 리턴한다.
     * @param schKind
     * @param yd
     * @return 
     */
    private String getOrderGp(String schKind) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        if(YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(schKind)|| // COIL 제품출하상차
           YmCommonConst.NEW_SCH_WORK_KIND_GVF1.equals(schKind)|| // Coil 제품출하상차
    	   YmCommonConst.NEW_SCH_WORK_KIND_GVF2.equals(schKind)||
    	   YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(schKind)|| // COIL 제품출하상차
    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF1.equals(schKind)|| // Coil 제품출하상차
    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF2.equals(schKind)||    		    
    	   YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(schKind)|| // COIL 제품출하상차
    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF1.equals(schKind)|| // Coil 제품출하상차
    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF2.equals(schKind) 
           ){ // Coil 제품출하상차	
            return YmCommonConst.COIL_ORDER_GP_3;
        }else if(YmCommonConst.NEW_SCH_WORK_KIND_SVFL.equals(schKind)) {
            return YmCommonConst.SLAB_ORDER_GP_1;
        }else if(YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(schKind)) {
            return YmCommonConst.SLAB_ORDER_GP_1;    
        }else if(YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(schKind)) {
            return YmCommonConst.SLAB_ORDER_GP_2;    
        }else if(YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(schKind)|| // COIL 제품이송상차
			     YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(schKind)|| // COIL 제품이송상차
	   		     YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(schKind)|| // COIL 제품이송상차		
                 	     YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(schKind)|| // COIL 제품이송하차
			     YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(schKind)|| // COIL 제품이송하차	 	
   			     YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(schKind)|| // COIL 제품이송하차			
   			     YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(schKind)|| // COIL 소재이송상차
			     YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(schKind)|| // COIL 소재이송상차
	   		     YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(schKind)|| // COIL 소재이송상차
			     YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(schKind)|| // COIL 소재이송하차
			     YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(schKind)|| // COIL 소재이송하차	 
  		   	     YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(schKind)){ // COIL 소재이송하차	 
            return YmCommonConst.COIL_ORDER_GP_1;
        }
        return "";
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
	 *      [A] 오퍼레이션명 : 소재차량 도착 Point요구(야드 L-2 송신)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public boolean procMatlCarArrPntRequestL2(JDTORecord msgRecord, String sCAR_GP)throws DAOException  {
		//TC_CODE : TSYDJ002
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		boolean isSuccess = false;
		String pos ="";
		String s_STACK_YD_GP ="";
		String s_STACK_BAY_GP ="";
		YdStockDAO ydStockDAO = new YdStockDAO();

		ymCommonDAO dao = ymCommonDAO.getInstance();

		try{ 
			//########################################################################################################
			 JDTORecord stock = JDTORecordFactory.getInstance().create();
			 JDTORecord stock2 =JDTORecordFactory.getInstance().create();
			 
			 String szTRN_EQP_CD        = YmCommonUtil.paraRecChkNull(msgRecord, "TRN_EQP_CD"); //운송장비코드
			 String szARR_WLOC_CD       = YmCommonUtil.paraRecChkNull(msgRecord, "ARR_WLOC_CD");//착지개소코드
			 String sARR_PNT_CD         = YmCommonUtil.paraRecChkNull(msgRecord, "ARR_YD_PNT_CD");//마지막 저장품
		     String sEQP_GP   			= YmCommonUtil.paraRecChkNull(msgRecord, "TRN_EQP_GP");
		     
		     if(sEQP_GP.equals("")){
		    	 sEQP_GP =szTRN_EQP_CD.substring(1, 3); 
		     }
		     
			 String trnEqpQueryId = "ym.tsinfo.getListloadStackColGp";
			 List stocklist = dao.getCommonList(trnEqpQueryId, new Object[]{szARR_WLOC_CD , sARR_PNT_CD,sEQP_GP});   
			 if(stocklist.size()<=0){
				 logger.println(LogLevel.DEBUG,this, "L2차량도착지 저장위치가  존재 안함==="+szARR_WLOC_CD+","+sARR_PNT_CD);
				 return false ;
			 }
			 stock2 	= (JDTORecord)stocklist.get(0);    		
			 pos 			= StringHelper.evl(stock2.getFieldString("STACK_COL_GP"),"");
			 s_STACK_YD_GP	=pos.substring(0,1);
			 s_STACK_BAY_GP	=pos.substring(1,2);
			 
			 String queryCode = "ym.common.dao.selectCarArrival2";    	
			 List stocks 		= dao.getCommonList(queryCode, new Object[]{szTRN_EQP_CD, s_STACK_YD_GP, s_STACK_BAY_GP});   
			 if(stocks.size()<=0){
				 logger.println(LogLevel.DEBUG,this, "L2차량스케줄이  존재 안함==="+szTRN_EQP_CD+","+s_STACK_YD_GP+","+s_STACK_BAY_GP);
				 return false ;
			 }
				 stock 		= (JDTORecord)stocks.get(0);
				 
				 
	
			 CTSStatusRegSBean TSStatusRegSBean=new CTSStatusRegSBean();
			 TSStatusRegSBean.ArrivalOrder(stocks ,stock ,  szTRN_EQP_CD , pos ,sCAR_GP);
				 logger.println(LogLevel.DEBUG,this, "L2차량도착전송 ===");
//			 EJBConnector ejbConn = new EJBConnector("default", "JNDICTSStatusReg", this);
//			   Boolean isSucf = (Boolean) ejbConn.trx("ArrivalOrder", new Class[] { List.class,JDTORecord.class,String.class , String.class, String.class }, 
//					   								new Object[] { stocks ,stock ,  szTRN_EQP_CD , pos ,sCAR_GP });
			 
			 
			 logger.println(LogLevel.DEBUG,this, "L2차량도착전송 성공  ===");
		  	//########################################################################################################
		   }catch(Exception e){}
		   return true ; 
    }
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 상차완료 BACKUP처리 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public boolean procMatlCarArrPntEnd(JDTORecord msgRecord )throws JDTOException  {
		//TC_CODE : TSYDJ002
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		
		Boolean isSuccess = new Boolean(false);
		
		String sStockId ="";
		String trnEqpQueryId ="";
		String trnEqpQueryId2 ="";
		String sCarSchId ="";
		String sStackColGp ="";
		String yd_gp ="";	
		List carSchList = null;
		List FrtostlList  = null;
		int iSeq =0;
		YdStockDAO ydStockDAO = new YdStockDAO();		
		ymCommonDAO dao = ymCommonDAO.getInstance();
		CoilSpecRegSeEJBBean CoilSpecRegSeEJBBean=new CoilSpecRegSeEJBBean();

		try{ 
			//########################################################################################################
				 JDTORecord tcRecord = JDTORecordFactory.getInstance().create();
				 JDTORecord stock2 =JDTORecordFactory.getInstance().create();			 
				 
				 String szTRN_EQP_CD           = YmCommonUtil.paraRecChkNull(msgRecord, "TRN_EQP_CD"); //운송장비코드
				 String szSPOS_WLOC_CD         = YmCommonUtil.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");//발지개소코드
				 String szSPOS_YD_PNT_CD       = YmCommonUtil.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");//발지야드포인트코드
				 String szARR_WLOC_CD          = YmCommonUtil.paraRecChkNull(msgRecord, "ARR_WLOC_CD");//착지개소코드
				 String sStockList             = YmCommonUtil.paraRecChkNull(msgRecord, "sStockList"); //상차대상 제료 번호
	
				 ydUtils.disyRec(msgRecord);
				 
				 
				 String[] bStockId = sStockList.split("-");
				 String sEQP_GP = szTRN_EQP_CD.substring(1,3);  //장비 구분 TR ,PT
				 
			 
		    	//차량스케쥴ID조회
	    		trnEqpQueryId = "ym.tsinfo.getListtrnEqpschL2";
	    		carSchList = dao.getCommonList(trnEqpQueryId, new Object[]{szTRN_EQP_CD});
		    	
	    		if(carSchList.size() > 0){
	    			logger.println(LogLevel.DEBUG,this, "차량스케줄이 이미 존재 합니다. 확인 요망 "+szTRN_EQP_CD);
					 return false ;
	    		}
	    		
			 
		    	///////////////////////////////////////////////////////////////////////////////////////////
			 	//*********************************차량스케줄  등록하기 *****************************************
			 	///////////////////////////////////////////////////////////////////////////////////////////
				 trnEqpQueryId 		= "ym.tsinfo.inserttrnEqpsch2";
			     iSeq = dao.insertData(trnEqpQueryId, new Object[]{ szTRN_EQP_CD,
		    			                                            "L",
		    			                                            szTRN_EQP_CD,
		    			                                            "L",
		    			                                            szSPOS_WLOC_CD,
		    			                                            szARR_WLOC_CD, //착지개소코드
		    			                                            szSPOS_YD_PNT_CD}); 
		    	/*
					INSERT INTO TB_YD_CARSCH
					(
					YD_CAR_SCH_ID
					,DEL_YN
					,YD_EQP_ID
					,YD_CAR_USE_GP
					,TRN_EQP_CD
					,YD_EQP_WRK_STAT
					,SPOS_WLOC_CD
					,ARR_WLOC_CD
					,YD_PNT_CD1
					,YD_CARLD_LEV_DT
					,YD_CARLD_PNT_WO_DT
					,YD_CAR_PROG_STAT 
					)
					SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') ||  LPAD(YD_CARSCH_SEQ.nextval,6,'0') AS YD_CAR_SCH_ID
					,'N'
					,(SELECT YD_EQP_ID
					  FROM TB_YD_CARSPEC
					  WHERE TRN_EQP_CD = :TRN_EQP_CD
					  AND DEL_YN = 'N')YD_EQP_ID
					,:YD_CAR_USE_GP
					,:TRN_EQP_CD
					,:YD_EQP_WRK_STAT
					,:SPOS_WLOC_CD
					,:ARR_WLOC_CD
					,:YD_PNT_CD1
					,SYSDATE
					,SYSDATE
					,'5'
					FROM DUAL
		    	 */
		     
		     
	    	//차량스케쥴ID조회
    		trnEqpQueryId = "ym.tsinfo.getListtrnEqpschL2";
    		carSchList = dao.getCommonList(trnEqpQueryId, new Object[]{szTRN_EQP_CD});
	    	
    		if(carSchList.size() > 0){
		    	JDTORecord CarSchrec = (JDTORecord)carSchList.get(0);
		    	sCarSchId  = StringHelper.evl(CarSchrec.getFieldString("YD_CAR_SCH_ID"), "");
    		}
	    	/*
	    	 * SELECT YD_CARLD_WRK_BOOK_ID WBOOK_ID
	    	 *        ,YD_CAR_SCH_ID
                FROM TB_YD_CARSCH
               WHERE TRN_EQP_CD = ?
               AND YD_CAR_PROG_STAT = '5'
               AND (DEL_YN IS NULL OR DEL_YN <> 'Y')
	    	 */	
		 	///////////////////////////////////////////////////////////////////////////////////////////
			
    		
    		
    		//장비 포인트 점유
    		trnEqpQueryId = "ym.tsinfo.updateLayerstat_01";
	    	int count = dao.updateData(trnEqpQueryId, new Object[]{	"L",szTRN_EQP_CD,"","",
			    													szSPOS_WLOC_CD,
			    													szSPOS_YD_PNT_CD,
			    													sEQP_GP}); 
	    	/* 
		    	UPDATE  TB_YM_STACKCOL
				SET     YD_CAR_USE_GP   = :YD_CAR_USE_GP,
				        TRN_EQP_CD      = :TRN_EQP_CD,
				        CAR_NO          = :CAR_NO,
				        CARD_NO         = :CARD_NO
				WHERE WLOC_CD   = :WLOC_CD
				AND   YD_PNT_CD = :YD_PNT_CD
				AND SUBSTR(STACK_COL_GP,3,2) = :TRN_EQP_GP
			*/
	    	
	    	//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
		    CarPointinforeg("4","",szTRN_EQP_CD,"",szSPOS_WLOC_CD,szSPOS_YD_PNT_CD,"L");
    		
    		 //포인트 점유 열 정보 가져오기
			 trnEqpQueryId = "ym.tsinfo.getListloadStackColGp";
			 List stocklist = dao.getCommonList(trnEqpQueryId, new Object[]{szSPOS_WLOC_CD , szSPOS_YD_PNT_CD,sEQP_GP});   
			 if(stocklist.size()<=0){
				 logger.println(LogLevel.DEBUG,this, "포인트 점유 열 정보 가져오기 ==="+szSPOS_WLOC_CD+","+szSPOS_YD_PNT_CD+","+szTRN_EQP_CD);
				 return false ;
			 }
			 stock2 		= (JDTORecord)stocklist.get(0);    		
			 sStackColGp	= StringHelper.evl(stock2.getFieldString("STACK_COL_GP"),"");
			 yd_gp 			= sStackColGp.substring(0,1);
			 
	    	///////////////////////////////////////////////////////////////////////////////////////////
		 	//*********************************차량이송재료 등록하기 *****************************************
		 	///////////////////////////////////////////////////////////////////////////////////////////
		 	for(int index = 0; index < bStockId.length; index++)
		 	{   
		 		sStockId = bStockId[index].toUpperCase();
		 		
		 		logger.println(LogLevel.DEBUG,this,"=상차완료 BACKUP처리=>차량스케쥴 대상재 등록 ");   
	    		

	    		if(yd_gp.equals("0")||	//A열연 슬라브 
    				yd_gp.equals("1")|| //A열연 코일 
    				yd_gp.equals("2")|| //B열연 슬라브 
    				yd_gp.equals("3") ){ //B열연 코일 
	    		//--------------------------------- AB열연 -----------------------------------------------
	    		//########################################################################################	
	    		
		 		//저장위치 초기화(차량대상제로 옮김)
				trnEqpQueryId = "ym.tsinfo.updatestacklayer";
	    		iSeq = dao.insertData(trnEqpQueryId, new Object[]{sStockId});
	    		/*
	    		UPDATE TB_YM_STACKLAYER
				SET STOCK_ID =NULL
				  , STACK_LAYER_STAT='E'
				WHERE STOCK_ID =?
	    		 */		
	    			
	    		trnEqpQueryId = "ym.tsinfo.YdStockDAO.updateYdStkLayerGp_Coil";
	    		trnEqpQueryId2 = "ym.tsinfo.insertCarftmtl2";
	            /*
	             * UPDATE TB_YM_STACKLAYER
	               SET STOCK_ID = ?
	              ,STACK_LAYER_ACTIVE_STAT = 'O'
	              ,STACK_LAYER_STAT = 'L'
	              WHERE STACK_COL_GP = ?
	              AND STACK_BED_GP = ?
	              AND STACK_LAYER_GP = ?
	             */
	    		
	    		
	    		if(sEQP_GP.equals("TR")){
	    		//트레일러 인 경우(단일 단으로 적치)
	    			
					//차량위치 맵에 등록
//					iSeq = ydStockDAO.requestupdateData(trnEqpQueryId, new Object[]{ sStockId, 
//																					 sStackColGp,
//							                                                         "0"+Integer.toString(index+1),
//							                                                         "01"});	
					//차량대상재료 등록					
		    		iSeq = dao.insertData(trnEqpQueryId2, new Object[]{ sCarSchId,sStockId,"0"+Integer.toString(index+1),"001"});
	    		
	    		}else {
	    		//빠레트 인 경우(여러 단으로 적치 )	
	    			
					//차량위치 맵에 등록
//					iSeq = ydStockDAO.requestupdateData(trnEqpQueryId, new Object[]{ sStockId, 
//																					 sStackColGp,
//							                                                         "01",
//							                                                         "0"+Integer.toString(index+1)});	      
		
					//차량대상재료 등록	
		    		iSeq = dao.insertData(trnEqpQueryId2, new Object[]{ sCarSchId,sStockId,"01","00"+Integer.toString(index+1)});
	    		
	    		}
	    		//########################################################################################	
	    		//--------------------------------- AB열연 -----------------------------------------------
	    		} else {
	    		//--------------------------------- 일관제철 -----------------------------------------------
		    	//########################################################################################	
	    			
		 		//저장위치 초기화(차량대상제로 옮김)
				trnEqpQueryId = "ym.tsinfo.updatestacklayer2";
	    		iSeq = dao.insertData(trnEqpQueryId, new Object[]{sStockId});
	    		/*
				UPDATE TB_YD_STKLYR
				SET STL_NO =NULL
				  , STACK_LAYER_STAT='E'
				WHERE STL_NO =?
	    		 */		
	    			
	    		trnEqpQueryId = "ym.tsinfo.YdStockDAO.updateYdStkLayerGp_Coil2";
	    		trnEqpQueryId2 = "ym.tsinfo.insertCarftmtl2";
	            /*
		      	UPDATE TB_YD_STKLYR
				SET STL_NO = ?
				    ,YD_STK_LYR_ACT_STAT = 'E'
				    ,YD_STK_LYR_MTL_STAT = 'C'
				WHERE YD_STK_COL_GP = ?
				AND YD_STK_BED_NO = ?
				AND YD_STK_LYR_NO = ?
	             */
	    		
	    		if(sEQP_GP.equals("TR")){
	    		//트레일러 인 경우(단일 단으로 적치)		
				
		    		//차량위치 맵에 등록
//					iSeq = ydStockDAO.requestupdateData(trnEqpQueryId, new Object[]{ sStockId, 
//																					 sStackColGp,
//							                                                         "0"+Integer.toString(index+1),
//							                                                         "001"});
					
					//차량대상재료 등록					
		    		iSeq = dao.insertData(trnEqpQueryId2, new Object[]{ sCarSchId,sStockId,"0"+Integer.toString(index+1),"001"});
				}else {
	    		//빠레트 인 경우(여러 단으로 적치 )	
	    		
					//차량위치 맵에 등록	
//					iSeq = ydStockDAO.requestupdateData(trnEqpQueryId, new Object[]{ sStockId, 
//																					 sStackColGp,
//							                                                         "01",
//							                                                         "00"+Integer.toString(index+1)});	  
					
					//차량대상재료 등록	
		    		iSeq = dao.insertData(trnEqpQueryId2, new Object[]{ sCarSchId,sStockId,"01","00"+Integer.toString(index+1)});
	    		}
	    		//########################################################################################	
	    		//--------------------------------- 일관제철 -----------------------------------------------
	    			
	    		}
		 		
		 	} // FOR END
		 	///////////////////////////////////////////////////////////////////////////////////////////		 	
		 	
		 	
		 	
		 	
	    	///////////////////////////////////////////////////////////////////////////////////////////
		 	//*********************************상차완료 전문 전송*****************************************
		 	///////////////////////////////////////////////////////////////////////////////////////////
    		String trnQueryId = "ym.tsinfo.getListFrtostlList_loadEnd2";
    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{StringHelper.evl(szTRN_EQP_CD, "")});
    	
    		int iSeqCount 	= FrtostlList.size();
    		for(int i=0; i < iSeqCount ; i++){
				
	    		JDTORecord FrtoSltrec = (JDTORecord)FrtostlList.get(i);

		       if(i == 0){
			         tcRecord = JDTORecordFactory.getInstance().create(); 
			         tcRecord.setField("JMS_TC_CD", "YDTSJ008");
			         tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));					
			         tcRecord.setField("TRN_EQP_CD", StringHelper.evl(FrtoSltrec.getFieldString("TRN_EQP_CD"), ""));
			         tcRecord.setField("SPOS_WLOC_CD", StringHelper.evl(FrtoSltrec.getFieldString("SPOS_WLOC_CD"), ""));
			         tcRecord.setField("SPOS_YD_PNT_CD", StringHelper.evl(szSPOS_YD_PNT_CD, ""));
			         tcRecord.setField("ARR_WLOC_CD", StringHelper.evl(FrtoSltrec.getFieldString("ARR_WLOC_CD"), szARR_WLOC_CD));
			         tcRecord.setField("TRN_WRK_MTL_GP", StringHelper.evl(FrtoSltrec.getFieldString("TRN_WRK_MTL_GP"), ""));
			         tcRecord.setField("MTL_UGNT_GP", StringHelper.evl(FrtoSltrec.getFieldString("MTL_UGNT_GP"), ""));
			         tcRecord.setField("HCR_GP", StringHelper.evl(FrtoSltrec.getFieldString("HCR_GP"), ""));
			         tcRecord.setField("CARLD_SH", Integer.toString(iSeqCount));
		         }
			     tcRecord.setField("STL_NO" + (1+i), StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), ""));
			     tcRecord.setField("STL_WT" + (1+i), StringHelper.evl(FrtoSltrec.getFieldString("STL_WT"), ""));	
			     
			     logger.println(LogLevel.DEBUG,this, "상차완료 후 저장품 정보 YD송신.==="+StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), ""));
			     
			     
			     
			    //C열연 이송인 경우에만 적용 
			    String sARRWLOCCD = StringHelper.evl(FrtoSltrec.getFieldString("ARR_WLOC_CD"), szARR_WLOC_CD);
			    
			    if(sARRWLOCCD.equals("DJY21")||sARRWLOCCD.equals("DJY22")||sARRWLOCCD.equals("DJY1E")) {

				    //C열연 코일 저장품 등록
				    CoilSpecRegSeEJBBean.stockProcCom(StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), ""),1);				     
				     
					//TB_PT_COILCOMM update 시험생산제품구분='C'로 UPDATE 적용(2010.12.31까지)				
					String stkQueryId = "ym.facilitywork.putwrecord.session.updateCoilcomm";
					iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), "")});
					
			    }
					
    		} //FOR END
    		tcRecord.setField("CARLD_CMPL_DT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
			  	  	             new Object[]{tcRecord});
			logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 상차완료실적(BACKUP처리).===");
		 	///////////////////////////////////////////////////////////////////////////////////////////
			
		 	
		  	//########################################################################################################
		   }catch(Exception e){}
		   return true ; 
    }
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 차량초기화처리 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
 
    public JDTORecord CarinfoReset(JDTORecord paramRecord) {
        logger.println(LogLevel.INFO,this,"Called CarinfoReset() Start");

        List rtnList = null;
        JDTORecord dtoRecordR = null;
        List paramList = new ArrayList();        
        int count =0;
 
        YdStockDAO ydStockDAO = new YdStockDAO();

        try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return null;
    		}
    		
            dtoRecordR = JDTORecordFactory.getInstance().create();
            
            if(paramRecord != null) {   

	            String TRN_EQP_CD = StringHelper.evl(paramRecord.getFieldString("TRN_EQP_CD"), "");   


	            //***************************************************************************************
	            // 차량초기화 프로시져 Call
	            //***************************************************************************************
	            /* 쿼리
	            {CALL YM_SP_TRANSMOVEBACKUPPRO(?,?,?)}
	            */      			
				paramList.clear(); 
				paramList.add(TRN_EQP_CD);    
				paramList.add("5");
				
				logger.println("bean에  조건값 :"+paramList);
				JDTORecord jRecord4 = ydStockDAO.spCall_YM_SP_TRANSMOVEBACKUPPRO(paramList);
				
				logger.println(LogLevel.DEBUG, "차량 초기화 - SP CALL JDTORecord => " + jRecord4);
				
				count = jRecord4.getFieldInt("3");        	
				
				if(count == 0) {
					dtoRecordR.setResultCode("MSG0042");
					dtoRecordR.setResultMsg(MessageHelper.getUserMessage("MSG0042",
							new String[] {"생성"}, "생성 작업이 정상적으로 완료되었습니다."));
				}           
				
				//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			    CarPointinforeg("1","",TRN_EQP_CD,"","","","C");
            }
            return dtoRecordR;
        } catch (DAOException daoe) {
            throw daoe;
        } catch (Exception e) {
            throw new EJBServiceException(e);
        }
    }
    
    /**
	 *      [A] 오퍼레이션명 : 차량초기화처리 (차량 ID)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
 
    public JDTORecord CarinfoReset2(JDTORecord paramRecord) {
        logger.println(LogLevel.INFO,this,"Called CarinfoReset() Start");

        List rtnList = null;
        JDTORecord dtoRecordR = null;
        List paramList = new ArrayList();        
        int count =0;
 
        YdStockDAO ydStockDAO = new YdStockDAO();

        try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return null;
    		}
    		
            dtoRecordR = JDTORecordFactory.getInstance().create();
            
            if(paramRecord != null) {   

	            String CAR_SCH_ID = StringHelper.evl(paramRecord.getFieldString("CAR_SCH_ID"), "");   


	            //***************************************************************************************
	            // 차량초기화 프로시져 Call
	            //***************************************************************************************
	            /* 쿼리
	            {CALL YM_SP_TRANSMOVEBACKUPPRO(?,?,?)}
	            */      			
				paramList.clear(); 
				paramList.add(CAR_SCH_ID);    
				paramList.add("5");
				
				logger.println("bean에  조건값 :"+paramList);
				JDTORecord jRecord4 = ydStockDAO.spCall_YM_SP_TRANSMOVEBACKUPSLAB(paramList);
				
				logger.println(LogLevel.DEBUG, "2차량 초기화 - SP CALL JDTORecord => " + jRecord4);
				
				count = jRecord4.getFieldInt("3");        	
				
				if(count == 0) {
					dtoRecordR.setResultCode("MSG0042");
					dtoRecordR.setResultMsg(MessageHelper.getUserMessage("MSG0042",
							new String[] {"생성"}, "2생성 작업이 정상적으로 완료되었습니다."));
				}           
 
            }
            return dtoRecordR;
        } catch (DAOException daoe) {
            throw daoe;
        } catch (Exception e) {
            throw new EJBServiceException(e);
        }
    }
    
    
	/**
	 *      [A] 오퍼레이션명 : 포인트초기화처리 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
 
	public boolean CarpointinfoReset(String pos)throws JDTOException  {
		
		logger.println(LogLevel.DEBUG,this, "START effectcarLoadEndDel : 상차완료처리 호출===");
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		Boolean isSuccess = new Boolean(false);
		
		YdStockDAO ydStockDAO = new YdStockDAO();

		ymCommonDAO dao = ymCommonDAO.getInstance();

	    
		JDTORecord Paramrecord = null;
		
		int count = 0;
		int iSeq = 0;
	    int intRtnVal= 0 ;

        String slab_no = "";
	    
	    String szMsg           				= "";
	    String szMethodName    				= "CarpointinfoReset";

	    String queryID         				= "";
	    String CurrProg_CD                  = "";
	    

		
	    try{
	    	 logger.println(LogLevel.DEBUG,this, "초기화처리 할 정지위치==="+pos);
	    	 
	    	int nIdx = 0;
	    	
// 			TB_YM_STACKCOL테이블 업데이트 
 			queryID = "ym.tsinfo.CarpoiontReset_Col";
 			count = dao.updateData(queryID, new Object[]{pos}); 
 			
 			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
		    CarPointinforeg("2","","",pos,"","","C");
		    
 			//TB_YM_STACKLAYER테이블 업데이트 close
 			queryID = "ym.tsinfo.CarpoiontReset_Layer";
 			count = dao.updateData(queryID, new Object[]{pos}); 
 			
 					    	
	
		    return isSuccess.booleanValue();
	    	      	
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}// end of effectcarLoadEnd()
	
    
    
	/**
	 *      [A] 오퍼레이션명 : B열연 이송대상조회 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
    
    public JDTORecord getListFrtomoveEndSearchPUP(JDTORecord paramRecord) {
        logger.println(LogLevel.INFO,this,"Called SuLoadingSBean getListFrtomoveEndSearchPUP() Start");

        JDTORecord dtoRecordR = null;
        List rtnList = null;
        List paramList = new ArrayList();
        String dQuery = "";

        YdStockDAO ydStockDAO = new YdStockDAO();

        try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return null;
    		}
    		
            dtoRecordR = JDTORecordFactory.getInstance().create();
            
            if(paramRecord != null) {
                String sARR_WLOC_CD = StringHelper.evl(paramRecord.getFieldString("ARR_WLOC_CD"),"");
                         
                                
                paramList.add(sARR_WLOC_CD);
                paramList.add(sARR_WLOC_CD);
               
                rtnList = ydStockDAO.getListFrtomoveEndSearchPUP(paramList);

                dtoRecordR.setField("LIST", rtnList);
            }
            
            return dtoRecordR;
        } catch (DAOException daoe) {
            throw daoe;
        } catch (Exception e) {
            throw new EJBServiceException(e);
        }
    }
    
    
	/**
	 *      [A] 오퍼레이션명 : B열연 이송차량별 재료정보 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
    
    public JDTORecord getListFrtomoveEndSearchPUPIfr(JDTORecord paramRecord) {
        logger.println(LogLevel.INFO,this,"Called SuLoadingSBean getListFrtomoveEndSearchPUPIfr() Start");

        JDTORecord dtoRecordR = null;
        List rtnList = null;
        List paramList = new ArrayList();

        YdStockDAO ydStockDAO = new YdStockDAO();

        try{
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return null;
    		}
    		
            dtoRecordR = JDTORecordFactory.getInstance().create();
            
            if(paramRecord != null) {
                String sYD_CAR_SCH_ID = StringHelper.evl(paramRecord.getFieldString("YD_CAR_SCH_ID"),"");
                                                           
                paramList.add(sYD_CAR_SCH_ID);
               
                rtnList = ydStockDAO.getListFrtomoveEndSearchPUPIfr(paramList);

                dtoRecordR.setField("LIST", rtnList);
            }
            
            return dtoRecordR;
        } catch (DAOException daoe) {
            throw daoe;
        } catch (Exception e) {
            throw new EJBServiceException(e);
        }
    }
    
    
    /**
	 *      [A] 오퍼레이션명 : 하차완료처리백업(슬라브)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public boolean CarinfoFrtoMoveEndBackup(JDTORecord msgRecord ) throws JDTOException  {
        logger.println(LogLevel.INFO,this,"슬라브하차완료실적BACKUP처리 Start");
	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		Boolean isSuccess = new Boolean(false);
		ymCommonDAO dao = ymCommonDAO.getInstance();
		YdStockDAO ydStockDAO = new YdStockDAO();
		List FrtStockList = null;
		List FrtStockList2 = null;
		List FrtoProductList = null;
		List FrtoProductList2 = null;
		List FrtoProductList3 = null;
		List FrtoProductList4 = null;
		List FrtoProductBackUPList = null;
		
		String stkBed = "";
		String stkLayer = "";
		String grobal_stkBed = "";
		String grobal_stkLayer = "";
		 String  szPT_TB_COMM = "";
	    List FrtostlList = null;
	    String CurrProg_CD = "";
	    String queryID = "";
			
		int tot_Qty = 0;
		int work_Qty = 0;
		int count = 0;
		
    	logger.println(LogLevel.DEBUG,this, "하차완료 백업처리 개시");

        try{
        	
        	logger.println(LogLevel.DEBUG,this, "하차완료 백업처리 개시2");
        	String s_YD_CAR_SCH_ID  = YmCommonUtil.paraRecChkNull(msgRecord, "YD_CAR_SCH_ID"); //운송장비코드
        	
        	logger.println(LogLevel.DEBUG,this, "하차완료백업처리 할 차량스케쥴ID = "+s_YD_CAR_SCH_ID);
        	
        	queryID	= "ym.tsinfo.dao.ydStockDAO.getListFrtomoveEndSearchPUPIfr";
	    	FrtoProductBackUPList = dao.getCommonList(queryID, new Object[]{s_YD_CAR_SCH_ID});
	    	JDTORecord FrtoProductBackUP = (JDTORecord)FrtoProductBackUPList.get(0);
	    	
	    	String slab_no         = StringHelper.evl(FrtoProductBackUP.getFieldString("STL_NO"),"");
        	
        	logger.println(LogLevel.DEBUG,this, "하차완료 백업처리 slab_no" + slab_no );
	
        	logger.println(LogLevel.DEBUG,this, "============== 권하처리 슬라브의 단정보가 001이므로 하차완료처리.===");
	    	queryID	= "ym.facilitywork.putwrecord.session.getListTcconst_1";
	    	FrtoProductList = dao.getCommonList(queryID, new Object[]{slab_no});
	    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
	    	
	    	//차량스케쥴ID로 이송재료 조회(
    		String trnQueryId = "ym.tsinfo.getListFrtostlList";
    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), "")});
    		/*
    		 * SELECT STL_NO,SUBSTR(YD_STK_LYR_NO,2,2)STK_LYR
               FROM TB_YD_CARFTMVMTL A, TB_YD_CARSCH B
               WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
               AND A.DEL_YN = 'N'
               AND B.TRN_EQP_CD = ?
               ORDER BY A.YD_STK_LYR_NO 
    		 */
			
    		//차량스케쥴테이블 - 하차완료시간 업데이트
			queryID = "ym.facilitywork.putwrecord.session.updateUnloadendTime";
	    	count = dao.updateData(queryID, new Object[]{slab_no});
	    	
	    	//차량재료테이블 - DEL_YN 업데이트
			queryID = "ym.facilitywork.putwrecord.session.deletefrmtList";
	    	count = dao.updateData(queryID, new Object[]{slab_no});
	    	
    		int iSeqCount 	= FrtostlList.size();
    		for(int i=0; i < iSeqCount ; i++){
				
	    		JDTORecord FrtoSltrec = (JDTORecord)FrtostlList.get(i);
				String s_STOCK_ID = StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"),"").trim();
				
				//TB_PT_STLFRTOMOVE update 				
				String stkQueryId = "ym.facilitywork.putwrecord.session.updateunLoadTimeToPT";
				int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID, 
						                                                          s_STOCK_ID});
				
				//주편공통에서 현재 진도코드 확인						
				queryID	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getMSLABCOMM";
		    	FrtoProductList2 = dao.getCommonList(queryID, new Object[]{s_STOCK_ID});
		    	JDTORecord FrtoProduct2 = (JDTORecord)FrtoProductList2.get(0);
		    	
		    	CurrProg_CD = StringHelper.evl(FrtoProduct2.getFieldString("RECORD_PROG_STAT"),"");
		    	
		    	if(CurrProg_CD.equals("3"))
		    	{
					
					//YDPTJ001 송신 추가(슬라브소재이송완료실적)- 슬라브인경우 슬라브 공통에서 항목 조회에화서 전문편집
                   //슬라브 공통에서 조회	
		    		
		    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶슬라브공통 업데이트처리 메소드 분리◀◀◀◀◀◀◀◀◀◀"); 
		    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
		    	 	Boolean isYd = (Boolean)ejbConn.trx("updateMatlFtmvWlrstSlab",new  Class[]{String.class},
																new Object[]{s_STOCK_ID});
					
					JDTORecord FrtoProduct3 =JDTORecordFactory.getInstance().create(); 
					
					queryID	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getSLABCOMM";
			    	FrtoProductList3 = dao.getCommonList(queryID, new Object[]{s_STOCK_ID});
			    	 FrtoProduct3 = (JDTORecord)FrtoProductList3.get(0);
			    	
			    	
			    	JDTORecord FrtoendRecord = null;
			    	FrtoendRecord = JDTORecordFactory.getInstance().create(); 
			    	FrtoendRecord.setField("JMS_TC_CD", "YDPTJ001");
			    	FrtoendRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));												    					    					    	
			    	FrtoendRecord.setField("STL_NO", StringHelper.evl(FrtoProduct3.getFieldString("SLAB_NO"), "").trim());// 재료번호												
			    	FrtoendRecord.setField("ORD_NO", StringHelper.evl(FrtoProduct3.getFieldString("ORD_NO"),""));	// 주문번호													
			    	FrtoendRecord.setField("ORD_DTL", StringHelper.evl(FrtoProduct3.getFieldString("ORD_DTL"),""));	// 주문행번														
			    	FrtoendRecord.setField("PLNT_PROC_CD", StringHelper.evl(FrtoProduct3.getFieldString("PLNT_PROC_CD"),""));// 공장공정코드														
			    	FrtoendRecord.setField("STL_APPEAR_GP", StringHelper.evl(FrtoProduct3.getFieldString("STL_APPEAR_GP"),""));// 재료외형구분							
					FrtoendRecord.setField("CURR_PROG_CD", StringHelper.evl(FrtoProduct3.getFieldString("CURR_PROG_CD"),""));// 현재진도코드													
			    	FrtoendRecord.setField("ORD_YEOJAE_GP", StringHelper.evl(FrtoProduct3.getFieldString("ORD_YEOJAE_GP"),""));	// 주문여재구분														
			    	FrtoendRecord.setField("STL_WT", StringHelper.evl(FrtoProduct3.getFieldString("SLAB_WT"),""));// 재료중량 (SLAB중량) 																				
			    	FrtoendRecord.setField("DS_MTL_WT", "");	// 설계재료중량																							
			    	FrtoendRecord.setField("MTL_STAT_GP", StringHelper.evl(FrtoProduct3.getFieldString("RECORD_PROG_STAT"),""));	// 재료상태구분														
			    	FrtoendRecord.setField("RECORD_END_GP", StringHelper.evl(FrtoProduct3.getFieldString("RECORD_END_GP"),""));// Record 종료구분
			    	FrtoendRecord.setField("RECORD_END_GP1", "");	
			    	FrtoendRecord.setField("BEFO_PROG_CD", StringHelper.evl(FrtoProduct3.getFieldString("BEFO_PROG_CD"),""));// 전진도 코드		
			    	FrtoendRecord.setField("BEF_ORD_NO", StringHelper.evl(FrtoProduct3.getFieldString("BEF_ORD_NO"),""));// 전주문 번호		
			    	FrtoendRecord.setField("BEF_ORD_DTL", StringHelper.evl(FrtoProduct3.getFieldString("BEF_ORD_DTL"),""));// 전주문 행번 
			    	FrtoendRecord.setField("MMATL_FEE_NO", "");// 모재료번호  
			    	FrtoendRecord.setField("ORDERTRANS_MATCH_GP", StringHelper.evl(FrtoProduct3.getFieldString("MATCH_ORDERTRANS_GP"),""));// 목전충당구분
			    	
			    	ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
					  	  	 new Object[]{FrtoendRecord});
					logger.println(LogLevel.DEBUG,this, "내부IF호출=== 슬라브소재이송완료실적.===");
					
					//이송완료 후 YDCTJ032전문 송신
					JDTORecord FrtoendRecord2 = null;
			    	FrtoendRecord2 = JDTORecordFactory.getInstance().create(); 
			    	FrtoendRecord2.setField("JMS_TC_CD", "YDCTJ032");
			    	FrtoendRecord2.setField("JMS_TC_CREATE_DDTT",new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));	
			    	FrtoendRecord2.setField("PTOP_PLNT_GP", 		"HB");
			    	FrtoendRecord2.setField("STL_APPEAR_GP", 	"C");
			    	FrtoendRecord2.setField("CHG_SUP_PROG_STAT", "09");
			    	FrtoendRecord2.setField("WR_OCCR_DT", 		new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			    	FrtoendRecord2.setField("YD_EQP_WR_CNT", 	"1");
			    	FrtoendRecord2.setField("STL_NO1", 	  StringHelper.evl(FrtoProduct3.getFieldString("SLAB_NO"), "").trim());

			    	
			    	ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
					  	  	 new Object[]{FrtoendRecord2});
					logger.println(LogLevel.DEBUG,this, "내부IF호출=== YDCTJ032.===");
				}
		    	
		    	else
		    	{
		    		
		    		logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶주편공통 업데이트처리 메소드 분리◀◀◀◀◀◀◀◀◀◀"); 
		    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
		    	 	Boolean isYd = (Boolean)ejbConn.trx("updateMatlFtmvWlrstMSlab",new  Class[]{String.class},
																new Object[]{s_STOCK_ID});
				    
				    stkQueryId	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getMSLABCOMM";
			    	FrtoProductList4 = dao.getCommonList(stkQueryId, new Object[]{s_STOCK_ID});
			    	JDTORecord FrtoProduct4 = (JDTORecord)FrtoProductList4.get(0);
				    
                   //YDPTJ001 송신 추가(슬라브소재이송완료실적)- 주편인경우 주편공통에서 항목 조회에화서 전문편집
					JDTORecord FrtoendRecord = null;
			    	FrtoendRecord = JDTORecordFactory.getInstance().create(); 
			    	FrtoendRecord.setField("JMS_TC_CD", "YDPTJ001");
			    	FrtoendRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));												    					    					    	
			    	FrtoendRecord.setField("STL_NO", StringHelper.evl(FrtoProduct4.getFieldString("MSLAB_NO"), "").trim());// 재료번호												
			    	FrtoendRecord.setField("ORD_NO", StringHelper.evl(FrtoProduct4.getFieldString("ORD_NO"),""));	// 주문번호													
			    	FrtoendRecord.setField("ORD_DTL", StringHelper.evl(FrtoProduct4.getFieldString("ORD_DTL"),""));	// 주문행번														
			    	FrtoendRecord.setField("PLNT_PROC_CD", StringHelper.evl(FrtoProduct4.getFieldString("PLNT_PROC_CD"),""));// 공장공정코드														
			    	FrtoendRecord.setField("STL_APPEAR_GP", StringHelper.evl(FrtoProduct4.getFieldString("STL_APPEAR_GP"),""));// 재료외형구분							
					FrtoendRecord.setField("CURR_PROG_CD", StringHelper.evl(FrtoProduct4.getFieldString("CURR_PROG_CD"),""));// 현재진도코드													
			    	FrtoendRecord.setField("ORD_YEOJAE_GP", StringHelper.evl(FrtoProduct4.getFieldString("ORD_YEOJAE_GP"),""));	// 주문여재구분														
			    	FrtoendRecord.setField("STL_WT", StringHelper.evl(FrtoProduct4.getFieldString("MSLAB_WT"),""));// 재료중량 (SLAB중량) 																				
			    	FrtoendRecord.setField("DS_MTL_WT", "");	// 설계재료중량																							
			    	FrtoendRecord.setField("MTL_STAT_GP", StringHelper.evl(FrtoProduct4.getFieldString("RECORD_PROG_STAT"),""));	// 재료상태구분														
			    	FrtoendRecord.setField("RECORD_END_GP", StringHelper.evl(FrtoProduct4.getFieldString("RECORD_END_GP"),""));// Record 종료구분
			    	FrtoendRecord.setField("RECORD_END_GP1", "");	
			    	FrtoendRecord.setField("BEFO_PROG_CD", StringHelper.evl(FrtoProduct4.getFieldString("BEFO_PROG_CD"),""));// 전진도 코드		
			    	FrtoendRecord.setField("BEF_ORD_NO", "");// 전주문 번호		
			    	FrtoendRecord.setField("BEF_ORD_DTL","");// 전주문 행번 
			    	FrtoendRecord.setField("MMATL_FEE_NO", "");// 모재료번호  
			    	FrtoendRecord.setField("ORDERTRANS_MATCH_GP","");// 목전충당구분
			    	
			    	ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
					  	  	 new Object[]{FrtoendRecord});
					logger.println(LogLevel.DEBUG,this, "내부IF호출=== 슬라브소재이송완료실적.==="); 
					
					//이송완료 후 YDCTJ032전문 송신
					JDTORecord FrtoendRecord2 = null;
			    	FrtoendRecord2 = JDTORecordFactory.getInstance().create(); 
			    	FrtoendRecord2.setField("JMS_TC_CD", "YDCTJ032");
			    	FrtoendRecord2.setField("JMS_TC_CREATE_DDTT",new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));	
			    	FrtoendRecord2.setField("PTOP_PLNT_GP", 		"HB");
			    	FrtoendRecord2.setField("STL_APPEAR_GP", 	"C");
			    	FrtoendRecord2.setField("CHG_SUP_PROG_STAT", "09");
			    	FrtoendRecord2.setField("WR_OCCR_DT", 		new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			    	FrtoendRecord2.setField("YD_EQP_WR_CNT", 	"1");
			    	FrtoendRecord2.setField("STL_NO1", 	  StringHelper.evl(FrtoProduct4.getFieldString("MSLAB_NO"), "").trim());

			    	
			    	ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
					  	  	 new Object[]{FrtoendRecord2});
					logger.println(LogLevel.DEBUG,this, "내부IF호출=== YDCTJ032.===");
					
    	        }
			
	    	}
	    	   			
			//하차완료실적 송신
			    //소재차량하차완료
			JDTORecord tcRecord = null;
			tcRecord = JDTORecordFactory.getInstance().create(); 
			tcRecord.setField("JMS_TC_CD", "YDTSJ010");
			tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			tcRecord.setField("TRN_EQP_CD", StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), ""));
			tcRecord.setField("ARR_WLOC_CD", StringHelper.evl(FrtoProduct.getFieldString("ARR_WLOC_CD"), ""));
			tcRecord.setField("ARR_YD_PNT_CD", StringHelper.evl(FrtoProduct.getFieldString("YD_PNT_CD"), ""));
			tcRecord.setField("CARUD_CMPL_DT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			
			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
			  	  	 new Object[]{tcRecord});
			logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철하차완료실적.===");
 
            return true;
        } catch (DAOException daoe) {
            throw daoe;
        } catch (Exception e) {
            throw new EJBServiceException(e);
        }
    }
		
	
    /**
	 *      [A] 오퍼레이션명 : 하차완료실적BACKUP처리 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
 
	public boolean CarinfoFrtoMoveBackup(JDTORecord msgRecord ) throws JDTOException  {
        logger.println(LogLevel.INFO,this,"코일하차완료실적BACKUP처리 Start");
		Boolean isSuccess2 = new Boolean(false);
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
        List rtnList = null;
        JDTORecord tcRecord2 = null;
        List paramList = new ArrayList();        
        int count =0;
        int intRtnVal =0;
        
        String queryID="";
        String s_STOCK_ID="";
        Boolean isSuccess =null;
        YdStockDAO ydStockDAO = new YdStockDAO();

        try{
        	
        	String sStockList             = YmCommonUtil.paraRecChkNull(msgRecord, "sStockList"); //상차대상 제료 번호
        	
			ydUtils.disyRec(msgRecord);
			 
			 
			 String[] bStockId = sStockList.split("-");
        	
			 	for(int index = 0; index < bStockId.length; index++)
			 	{   
			 		s_STOCK_ID = bStockId[index].toUpperCase();
			 		
			 		//실적처리 
		    	 	EJBConnector ejbConn = new EJBConnector("default","JNDITsInfoReg",this);
		    	 	Boolean isYd = (Boolean)ejbConn.trx("CarinfoFrtoMoveBackupSub",new  Class[]{String.class},
																new Object[]{s_STOCK_ID});					
					
					ymCommonDAO dao2 = ymCommonDAO.getInstance();
				    queryID 			= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
					List productList 	= dao2.getCommonList(queryID, new Object[]{s_STOCK_ID});
					JDTORecord stlRecord = (JDTORecord)productList.get(0);
					
					String stl_appear_gp =StringHelper.evl(stlRecord.getFieldString("STL_APPEAR_GP"), "");
		            
					if(!stl_appear_gp.equals("Y"))
					{
						
			        	//TB_PT_STLFRTOMOVE update				
						String stkQueryId = "ym.facilitywork.putwrecord.session.updateunLoadTimeToPT";
						int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID, 
								                                                          s_STOCK_ID}); 
						
					    //코일소재 이송완료실적(YDPTJ002)
					    tcRecord2 =JDTORecordFactory.getInstance().create();
					    tcRecord2.setField("JMS_TC_CD", "YDPTJ002");
					    tcRecord2.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
					    tcRecord2.setField("STL_NO",  StringHelper.evl(stlRecord.getFieldString("COIL_NO"), ""));
					    // 주문번호
					    tcRecord2.setField("ORD_NO",  StringHelper.evl(stlRecord.getFieldString("ORD_NO"), ""));
					    // 주문행번
					    tcRecord2.setField("ORD_DTL", StringHelper.evl(stlRecord.getFieldString("ORD_DTL"), ""));
					    // 공장공정코드
					    tcRecord2.setField("PLNT_PROC_CD",  StringHelper.evl(stlRecord.getFieldString("PLNT_PROC_CD"), ""));
					    // 재료외형구분
					    tcRecord2.setField("STL_APPEAR_GP",  StringHelper.evl(stlRecord.getFieldString("STL_APPEAR_GP"), ""));
					    // 현재진도코드
					    tcRecord2.setField("CURR_PROG_CD",  StringHelper.evl(stlRecord.getFieldString("CURR_PROG_CD"), ""));
					    // 주문여재구분
					    tcRecord2.setField("ORD_YEOJAE_GP", StringHelper.evl(stlRecord.getFieldString("ORD_YEOJAE_GP"), ""));
					    // 재료중량 (SLAB중량) 
					    tcRecord2.setField("STL_WT",  StringHelper.evl(stlRecord.getFieldString("COIL_WT"), ""));
				    	// 설계재료중량(항목명?)
				    	tcRecord2.setField("DS_MTL_WT", "");
					    // 재료상태구분(항목명?)
					    tcRecord2.setField("MTL_STAT_GP",  StringHelper.evl(stlRecord.getFieldString("RECORD_PROG_STAT"), ""));
					    // Record 종료구분
					    tcRecord2.setField("RECORD_END_GP",  StringHelper.evl(stlRecord.getFieldString("RECORD_END_GP"), ""));
					    // Record 종료구분 1(항목명?)
					    tcRecord2.setField("RECORD_END_GP1", "");
					    // 전진도 코드
					    tcRecord2.setField("BEFO_PROG_CD",  StringHelper.evl(stlRecord.getFieldString("BEFO_PROG_CD"), ""));
					    // 전주문 번호
					    tcRecord2.setField("BEF_ORD_NO",  StringHelper.evl(stlRecord.getFieldString("BEF_ORD_NO"), ""));
					    // 전주문 행번
					    tcRecord2.setField("BEF_ORD_DTL",  StringHelper.evl(stlRecord.getFieldString("BEF_ORD_DTL"), ""));
					    // 모재료번호   
					    tcRecord2.setField("MMATL_FEE_NO",  StringHelper.evl(stlRecord.getFieldString("MMATL_FEE_NO"), ""));
					    // 목전충당구분
					    tcRecord2.setField("ORDERTRANS_MATCH_GP", StringHelper.evl(stlRecord.getFieldString("MATCH_ORDERTRANS_GP"), ""));	
					
					    EJBConnector ejbConn2 = new EJBConnector("default","JNDIYardWrkResReg",this);
					    isSuccess = (Boolean)ejbConn2.trx("sendInternalModel",new Class[]{JDTORecord.class},
					  	  	 new Object[]{tcRecord2});
					    logger.println(LogLevel.DEBUG,this, "내부IF호출===코일소재 이송완료실적BACKUP처리.===");
					}
			 	}

            return true;
        } catch (DAOException daoe) {
            throw daoe;
        } catch (Exception e) {
            throw new EJBServiceException(e);
        }
    }
	
	
	/**
	 *      [A] 오퍼레이션명 : 하차완료실적BACKUP처리(SLAB) 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
 
	public boolean CarinfoSlabFrtoMoveBackup(JDTORecord msgRecord ) throws JDTOException  {
        logger.println(LogLevel.INFO,this,"SLAB하차완료실적BACKUP처리 Start");
		Boolean isSuccess2 = new Boolean(false);
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
        List rtnList = null;
        JDTORecord tcRecord2 = null;
        List paramList = new ArrayList();        
        int count =0;
        int intRtnVal =0;
        
        String queryID="";
        String s_STOCK_ID="";
        String CurrProg_CD="";
        
        Boolean isSuccess =null;
        YdStockDAO ydStockDAO = new YdStockDAO();
        List FrtoProductList2 = null;
        List FrtoProductList3 = null;
        List FrtoProductList4 = null;
        
        ymCommonDAO dao = ymCommonDAO.getInstance();
        try{
        	
        	String sStockList             = YmCommonUtil.paraRecChkNull(msgRecord, "sStockList"); //상차대상 제료 번호
        	
			ydUtils.disyRec(msgRecord);
			 
			 
			 String[] bStockId = sStockList.split("-");
        	
			 	for(int index = 0; index < bStockId.length; index++)
			 	{   
			 		s_STOCK_ID = bStockId[index].toUpperCase();
			 		
			 		//TB_PT_STLFRTOMOVE update 				
					String stkQueryId = "ym.facilitywork.putwrecord.session.updateunLoadTimeToPT";
					int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID, 
							                                                          s_STOCK_ID});
					
					//주편공통에서 현재 진도코드 확인						
					queryID	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getMSLABCOMM";
			    	FrtoProductList2 = dao.getCommonList(queryID, new Object[]{s_STOCK_ID});
			    	JDTORecord FrtoProduct2 = (JDTORecord)FrtoProductList2.get(0);
			    	
			    	CurrProg_CD = StringHelper.evl(FrtoProduct2.getFieldString("RECORD_PROG_STAT"),"");
			    	
			    	if(CurrProg_CD.equals("3"))
			    	{
						
						//YDPTJ001 송신 추가(슬라브소재이송완료실적)- 슬라브인경우 슬라브 공통에서 항목 조회에화서 전문편집
	                   //슬라브 공통에서 조회	
			    		
			    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶슬라브공통 업데이트처리 메소드 분리(BACKUP처리)◀◀◀◀◀◀◀◀◀◀"); 
			    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
			    	 	Boolean isYd = (Boolean)ejbConn.trx("updateMatlFtmvWlrstSlab",new  Class[]{String.class},
																	new Object[]{s_STOCK_ID});
						
						JDTORecord FrtoProduct3 =JDTORecordFactory.getInstance().create(); 
						
						queryID	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getSLABCOMM";
				    	FrtoProductList3 = dao.getCommonList(queryID, new Object[]{s_STOCK_ID});
				    	 FrtoProduct3 = (JDTORecord)FrtoProductList3.get(0);
				    	
				    	
				    	JDTORecord FrtoendRecord = null;
				    	FrtoendRecord = JDTORecordFactory.getInstance().create(); 
				    	FrtoendRecord.setField("JMS_TC_CD", "YDPTJ001");
				    	FrtoendRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));												    					    					    	
				    	FrtoendRecord.setField("STL_NO", StringHelper.evl(FrtoProduct3.getFieldString("SLAB_NO"), "").trim());// 재료번호												
				    	FrtoendRecord.setField("ORD_NO", StringHelper.evl(FrtoProduct3.getFieldString("ORD_NO"),""));	// 주문번호													
				    	FrtoendRecord.setField("ORD_DTL", StringHelper.evl(FrtoProduct3.getFieldString("ORD_DTL"),""));	// 주문행번														
				    	FrtoendRecord.setField("PLNT_PROC_CD", StringHelper.evl(FrtoProduct3.getFieldString("PLNT_PROC_CD"),""));// 공장공정코드														
				    	FrtoendRecord.setField("STL_APPEAR_GP", StringHelper.evl(FrtoProduct3.getFieldString("STL_APPEAR_GP"),""));// 재료외형구분							
						FrtoendRecord.setField("CURR_PROG_CD", StringHelper.evl(FrtoProduct3.getFieldString("CURR_PROG_CD"),""));// 현재진도코드													
				    	FrtoendRecord.setField("ORD_YEOJAE_GP", StringHelper.evl(FrtoProduct3.getFieldString("ORD_YEOJAE_GP"),""));	// 주문여재구분														
				    	FrtoendRecord.setField("STL_WT", StringHelper.evl(FrtoProduct3.getFieldString("SLAB_WT"),""));// 재료중량 (SLAB중량) 																				
				    	FrtoendRecord.setField("DS_MTL_WT", "");	// 설계재료중량																							
				    	FrtoendRecord.setField("MTL_STAT_GP", StringHelper.evl(FrtoProduct3.getFieldString("RECORD_PROG_STAT"),""));	// 재료상태구분														
				    	FrtoendRecord.setField("RECORD_END_GP", StringHelper.evl(FrtoProduct3.getFieldString("RECORD_END_GP"),""));// Record 종료구분
				    	FrtoendRecord.setField("RECORD_END_GP1", "");	
				    	FrtoendRecord.setField("BEFO_PROG_CD", StringHelper.evl(FrtoProduct3.getFieldString("BEFO_PROG_CD"),""));// 전진도 코드		
				    	FrtoendRecord.setField("BEF_ORD_NO", StringHelper.evl(FrtoProduct3.getFieldString("BEF_ORD_NO"),""));// 전주문 번호		
				    	FrtoendRecord.setField("BEF_ORD_DTL", StringHelper.evl(FrtoProduct3.getFieldString("BEF_ORD_DTL"),""));// 전주문 행번 
				    	FrtoendRecord.setField("MMATL_FEE_NO", "");// 모재료번호  
				    	FrtoendRecord.setField("ORDERTRANS_MATCH_GP", StringHelper.evl(FrtoProduct3.getFieldString("MATCH_ORDERTRANS_GP"),""));// 목전충당구분
				    	
				    	ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
						isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
						  	  	 new Object[]{FrtoendRecord});
						logger.println(LogLevel.DEBUG,this, "내부IF호출=== 슬라브소재이송완료실적.===");
						
						//이송완료 후 YDCTJ032전문 송신
						JDTORecord FrtoendRecord2 = null;
				    	FrtoendRecord2 = JDTORecordFactory.getInstance().create(); 
				    	FrtoendRecord2.setField("JMS_TC_CD", "YDCTJ032");
				    	FrtoendRecord2.setField("JMS_TC_CREATE_DDTT",new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));	
				    	FrtoendRecord2.setField("PTOP_PLNT_GP", 		"HB");
				    	FrtoendRecord2.setField("STL_APPEAR_GP", 	"C");
				    	FrtoendRecord2.setField("CHG_SUP_PROG_STAT", "09");
				    	FrtoendRecord2.setField("WR_OCCR_DT", 		new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
				    	FrtoendRecord2.setField("YD_EQP_WR_CNT", 	"1");
				    	FrtoendRecord2.setField("STL_NO1", 	  StringHelper.evl(FrtoProduct3.getFieldString("SLAB_NO"), "").trim());

				    	
				    	ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
						isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
						  	  	 new Object[]{FrtoendRecord2});
						logger.println(LogLevel.DEBUG,this, "내부IF호출=== YDCTJ032.===");
					}
			    	
			    	else
			    	{
			    		
			    		logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶주편공통 업데이트처리 메소드 분리(BACKUP처리)◀◀◀◀◀◀◀◀◀◀"); 
			    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
			    	 	Boolean isYd = (Boolean)ejbConn.trx("updateMatlFtmvWlrstMSlab",new  Class[]{String.class},
																	new Object[]{s_STOCK_ID});
					    
					    stkQueryId	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getMSLABCOMM";
				    	FrtoProductList4 = dao.getCommonList(stkQueryId, new Object[]{s_STOCK_ID});
				    	JDTORecord FrtoProduct4 = (JDTORecord)FrtoProductList4.get(0);
					    
	                   //YDPTJ001 송신 추가(슬라브소재이송완료실적)- 주편인경우 주편공통에서 항목 조회에화서 전문편집
						JDTORecord FrtoendRecord = null;
				    	FrtoendRecord = JDTORecordFactory.getInstance().create(); 
				    	FrtoendRecord.setField("JMS_TC_CD", "YDPTJ001");
				    	FrtoendRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));												    					    					    	
				    	FrtoendRecord.setField("STL_NO", StringHelper.evl(FrtoProduct4.getFieldString("MSLAB_NO"), "").trim());// 재료번호												
				    	FrtoendRecord.setField("ORD_NO", StringHelper.evl(FrtoProduct4.getFieldString("ORD_NO"),""));	// 주문번호													
				    	FrtoendRecord.setField("ORD_DTL", StringHelper.evl(FrtoProduct4.getFieldString("ORD_DTL"),""));	// 주문행번														
				    	FrtoendRecord.setField("PLNT_PROC_CD", StringHelper.evl(FrtoProduct4.getFieldString("PLNT_PROC_CD"),""));// 공장공정코드														
				    	FrtoendRecord.setField("STL_APPEAR_GP", StringHelper.evl(FrtoProduct4.getFieldString("STL_APPEAR_GP"),""));// 재료외형구분							
						FrtoendRecord.setField("CURR_PROG_CD", StringHelper.evl(FrtoProduct4.getFieldString("CURR_PROG_CD"),""));// 현재진도코드													
				    	FrtoendRecord.setField("ORD_YEOJAE_GP", StringHelper.evl(FrtoProduct4.getFieldString("ORD_YEOJAE_GP"),""));	// 주문여재구분														
				    	FrtoendRecord.setField("STL_WT", StringHelper.evl(FrtoProduct4.getFieldString("MSLAB_WT"),""));// 재료중량 (SLAB중량) 																				
				    	FrtoendRecord.setField("DS_MTL_WT", "");	// 설계재료중량																							
				    	FrtoendRecord.setField("MTL_STAT_GP", StringHelper.evl(FrtoProduct4.getFieldString("RECORD_PROG_STAT"),""));	// 재료상태구분														
				    	FrtoendRecord.setField("RECORD_END_GP", StringHelper.evl(FrtoProduct4.getFieldString("RECORD_END_GP"),""));// Record 종료구분
				    	FrtoendRecord.setField("RECORD_END_GP1", "");	
				    	FrtoendRecord.setField("BEFO_PROG_CD", StringHelper.evl(FrtoProduct4.getFieldString("BEFO_PROG_CD"),""));// 전진도 코드		
				    	FrtoendRecord.setField("BEF_ORD_NO", "");// 전주문 번호		
				    	FrtoendRecord.setField("BEF_ORD_DTL","");// 전주문 행번 
				    	FrtoendRecord.setField("MMATL_FEE_NO", "");// 모재료번호  
				    	FrtoendRecord.setField("ORDERTRANS_MATCH_GP","");// 목전충당구분
				    	
				    	ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
						isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
						  	  	 new Object[]{FrtoendRecord});
						logger.println(LogLevel.DEBUG,this, "내부IF호출=== 슬라브소재이송완료실적.==="); 
						
						//이송완료 후 YDCTJ032전문 송신
						JDTORecord FrtoendRecord2 = null;
				    	FrtoendRecord2 = JDTORecordFactory.getInstance().create(); 
				    	FrtoendRecord2.setField("JMS_TC_CD", "YDCTJ032");
				    	FrtoendRecord2.setField("JMS_TC_CREATE_DDTT",new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));	
				    	FrtoendRecord2.setField("PTOP_PLNT_GP", 		"HB");
				    	FrtoendRecord2.setField("STL_APPEAR_GP", 	"C");
				    	FrtoendRecord2.setField("CHG_SUP_PROG_STAT", "09");
				    	FrtoendRecord2.setField("WR_OCCR_DT", 		new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
				    	FrtoendRecord2.setField("YD_EQP_WR_CNT", 	"1");
				    	FrtoendRecord2.setField("STL_NO1", 	  StringHelper.evl(FrtoProduct4.getFieldString("MSLAB_NO"), "").trim());

				    	
				    	ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
						isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
						  	  	 new Object[]{FrtoendRecord2});
						logger.println(LogLevel.DEBUG,this, "내부IF호출=== YDCTJ032.===");
						
	    	        }
			 	}

            return true;
        } catch (DAOException daoe) {
            throw daoe;
        } catch (Exception e) {
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
	public boolean CarinfoFrtoMoveBackupSub(String s_STOCK_ID){
		
		boolean isSuccess = false;
		int iReq = -1;
		int intRtnVal =0;
		String stkQueryId ="";
		YdStockDAO ydStockDAO = new YdStockDAO();
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			//코일공통 업데이트				
			stkQueryId = "ym.facilitywork.putwrecord.session.updateMatlFtmvWlrstCoil";
			int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID,s_STOCK_ID});
			
			//저장품 이동조건 업데이트 
			rVal = YmCommonUtil.getCoilCurrProgCd(s_STOCK_ID,"");
			String sStocMv = rVal[1];
			stkQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStock";						
			intRtnVal =  ydStockDAO.requestupdateData(stkQueryId,new Object[]{ sStocMv,"YDTSJ010",s_STOCK_ID });
				
			isSuccess = true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}
	
	
	/**
	 * 오퍼레이션명 : 차량포인트 통합관리
 	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */     	
 
	public boolean CarPointinforeg(String chk 
									, String s_CAR_NO
									, String s_TRN_EQP_CD
									,String s_STACK_COL_GP
									,String szARR_WLOC_CD 
									,String szARR_YD_PNT_CD
									,String s_STAT)throws DAOException {
		
		boolean isSuccess = false;
		int iReq = -1;
		int intRtnVal =0;
		int iSeq=0;
		String stkQueryId ="";
		YdStockDAO ydStockDAO = new YdStockDAO();
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			logger.println(LogLevel.DEBUG,this, "▣▣▣▣차량포인트 통합관리(START):"+chk+","+s_CAR_NO+","+s_TRN_EQP_CD+","+s_STACK_COL_GP+","+szARR_WLOC_CD+","+szARR_YD_PNT_CD+","+s_STAT+"▣▣▣▣▣");
			
			//장애 발생시 이전 소스로 원복 하기 위한 조치
			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.CarPointinforegchklist";
		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});

		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
	    	if(CHK.equals("Y")){	    		
	    		
	    		//육송출하고도화
				 List chkList = null;
				QueryId 	= "com.inisteel.cim.yd.dao.chklist";
				chkList = dao.getCommonList(QueryId, new Object[]{});

			    JDTORecord unloadPointrec2 = (JDTORecord)chkList.get(0);
		    	String CHK2   = StringHelper.evl(unloadPointrec2.getFieldString("CHK"), "");	    	
		    	logger.println(LogLevel.DEBUG,this, "◑◑◑◑◑  CHK:"+CHK2);
		    	
		    	
	    		logger.println(LogLevel.DEBUG,this, "▣▣▣▣차량포인트 통합관리(START):통합작업 시작 ▣▣▣▣▣");
				if(chk.equals("1")){
					//설비코드로 초기화 하는 경우(구내운송)			 			
					stkQueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.carpointtrneqpcdupdate";
					 iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_TRN_EQP_CD});
				}else if(chk.equals("2")){
					//저장위치로 초기화 하는 경우(구내운송)
					if(CHK2.equals("Y") ){
						stkQueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.carpointstackcolgpupdateNEW";
			    	}else{
			    		stkQueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.carpointstackcolgpupdate";
			    	}
					
					 iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_STACK_COL_GP});
				}else if(chk.equals("3")){
					//저장위치로 차량 포인트 예약 하는 경우(구내운송)
					if(CHK2.equals("Y") ){
						stkQueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.carpointtrneqpcdupdate2NEW";
			    	}else{
			    		stkQueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.carpointtrneqpcdupdate2";
			    	}
					
					 iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_TRN_EQP_CD,s_STACK_COL_GP});
				} else if(chk.equals("4")){
					//개소코드,포인트로 차량 포인트 예약 하는 경우(구내운송)
					stkQueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.carpointWlocpntupdate";
					 iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_TRN_EQP_CD,szARR_WLOC_CD,szARR_YD_PNT_CD});
				}else if(chk.equals("A")){
					//설비코드로 초기화 하는 경우(출하)			 			
					stkQueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.carpointtrneqpcdupdatePT";
					 iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_TRN_EQP_CD});
				}else if(chk.equals("B")){
					//저장위치로 초기화 하는 경우(출하)
					if(CHK2.equals("Y") ){
						stkQueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.carpointstackcolgpupdatePTNEW";
			    	}else{
			    		stkQueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.carpointstackcolgpupdatePT";
			    	}
					 iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_STACK_COL_GP});
				}else if(chk.equals("C")){
					//저장위치로 차량 포인트 예약 하는 경우(출하)
					if(CHK2.equals("Y") ){
						stkQueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.carpointtrneqpcdupdatePT2NEW";
			    	}else{
			    		stkQueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.carpointtrneqpcdupdatePT2";
			    	}					
					
					 iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_CAR_NO ,s_TRN_EQP_CD,s_STACK_COL_GP});
				} else if(chk.equals("D")){
					//개소코드,포인트로 차량 포인트 예약 하는 경우(출하)
					stkQueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.carpointWlocpntupdatePT";
					 iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_CAR_NO, s_TRN_EQP_CD,szARR_WLOC_CD,szARR_YD_PNT_CD});
				}  
		 
	    	}
	    	logger.println(LogLevel.DEBUG,this, "▣▣▣▣차량포인트 통합관리(END)COUNT:"+iSeq+"▣▣▣▣▣");
			isSuccess = true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}
	

	/**
	 * 오퍼레이션명 : 재료단위 차량상차스케줄 생성 
 	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */     	 
	public boolean CarStlNoSchCreate( String szSTL_NO
									, String szTRN_EQP_CD
									, String szSTL_APPEAR_GP)throws DAOException {
		
		boolean isSuccess = false;
		int iReq = -1;
		int intRtnVal =0;
		int iSeq=0;
		String stkQueryId ="";
		String szYD_CAR_PROG_STAT  		="";
		String szwbook_ID			   	="";
		String szSchCode			   	="";
		String szYD_CARLD_STOP_LOC		="";
		String szYD_GP					="";
		String szSTACK_BAY_GP			="";
		String szWbook_CHK			   	="";
		String szYD_CAR_SCH_ID			="";
		
		JDTORecord 		wBookSel		= null;
		YdStockDAO 		ydStockDAO 		= new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO 		ydWBookDAO 		= new YdWBookDAO();
 
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			logger.println(LogLevel.DEBUG,this, "▣▣▣▣재료단위 차량상차스케줄 생성 (START): "+szSTL_NO+","+szTRN_EQP_CD+" ▣▣▣▣▣");
			
			//이전 작업예약 존재 유무 체크 *************************************************************** 
			String trnQueryId 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID3";
			List FrtostlList = dao.getCommonList(trnQueryId, new Object[]{szSTL_NO});
 
			if(FrtostlList.size() > 0)
 		    { 
	    		JDTORecord TolocRecCoil = (JDTORecord)FrtostlList.get(0);
	    		szWbook_CHK = StringHelper.evl(TolocRecCoil.getFieldString("WBOOK_ID"),"");
	    		
	    		if(!szWbook_CHK.equals("")){
	    			logger.println(LogLevel.DEBUG,this, "다른 작업예약이 존재 합니다. szWbook_CHK:"+szWbook_CHK);
	    			return isSuccess;
	    		}
 		    }
			
			
			//차량정보 검색****************************************************************************
			String QueryId 	= "ym.tsinfo.getListSposYNchk3";
 		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{szTRN_EQP_CD,szSTL_NO});
 		    if(sposYNChklist.size() > 0)
 		    { 
 		    	JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
 		    	szYD_CAR_SCH_ID		 = StringHelper.evl(unloadPointrec.getFieldString("YD_CAR_SCH_ID"), "");
 		    	szYD_CAR_PROG_STAT   = StringHelper.evl(unloadPointrec.getFieldString("YD_CAR_PROG_STAT"), "");
 		    	szYD_CARLD_STOP_LOC  = StringHelper.evl(unloadPointrec.getFieldString("YD_CARLD_STOP_LOC"), "");
 		    	szYD_GP				 =szYD_CARLD_STOP_LOC.substring(0, 1);
 		    	szSTACK_BAY_GP		 =szYD_CARLD_STOP_LOC.substring(1, 2);
 		    }else{
 		    	 
 				YmCommonUtil.putLog(szSessionName, "CarStlNoSchCreate", "착지코드가 틀린 대상입니다.", 1);
 				m_ctx.setRollbackOnly();
 				throw new DAOException("착지코드가 틀린 대상입니다.");
 		    }
 		    
 		    if(szYD_CAR_PROG_STAT.equals("2")||szYD_CAR_PROG_STAT.equals("3")||szYD_CAR_PROG_STAT.equals("4")){
 		    	logger.println(LogLevel.DEBUG,this, "상차중인 상태에서 작업을 진행 합니다.szYD_CAR_PROG_STAT:"+szYD_CAR_PROG_STAT);
 		    	
 		    	//스케줄코드 생성 ***********************************************************  
		    	if(szSTL_APPEAR_GP.equals("GM")){
		    		// 제품
		    		szSchCode	= YmCommonConst.NEW_SCH_WORK_KIND_GVML ; 
		    		szSchCode = getSchWorkKind(szSchCode, szYD_CARLD_STOP_LOC);		    		 
		    	 }else{
		    		 //소재
		    		 szSchCode	= YmCommonConst.NEW_SCH_WORK_KIND_CVML ;
		    		 szSchCode = getSchWorkKind(szSchCode, szYD_CARLD_STOP_LOC);
		    	 }
		    	
 		    	
 		    	//작업예약 생성 작업***********************************************************
	   			//작업예약id 생성	
		    	szwbook_ID = dao.createWBook(szYD_CARLD_STOP_LOC, szSchCode, "O" ,szYD_CARLD_STOP_LOC);	// SCHEDULE 에서 검색
	 
 		    	
				//저장품테이블에 update	*******************************************************			
				dao.modifyTermAndWBookIdOfStock(szwbook_ID, "CS", szSTL_NO);
				
																			  
				//적치단  Table Update(작업요구상태='S'로 변경)***********************************
				QueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
            	iSeq = ydStockDAO.requestupdateData(QueryId, new Object[]{	YmCommonConst.STACK_LAYER_STAT_S, szSTL_NO });
            	
            	
            	//차량대상재 테이블에 insert****************************************************				    		
	    		logger.println(LogLevel.DEBUG,this,"=출발실적처리=>차량스케쥴 대상재 등록 ");   
	    		QueryId = "ym.tsinfo.insertCarftmtl";
	    		iSeq = dao.insertData(QueryId, new Object[]{ szYD_CAR_SCH_ID,szSTL_NO}); 
            	
	    		
            	//크레인 스케쥴 호출************************************************************ 
	 			EJBConnector ejbConn2 = new EJBConnector("default","JNDICraneSchReg",this);
	 			Boolean isTrue2		  = (Boolean)ejbConn2.trx( "callCraneSchInfo",new  Class[]{String.class},new Object[]{szwbook_ID});
            	
         
 		    }else{
 		    	logger.println(LogLevel.DEBUG,this, "상차중인 상태가 아닌경우 실행을 할 수가 없습니다.szYD_CAR_PROG_STAT:"+szYD_CAR_PROG_STAT);
 		    	return isSuccess;
 		    }
			 
	    	logger.println(LogLevel.DEBUG,this, "▣▣▣▣재료단위 차량상차스케줄 생성 (END)COUNT:"+iSeq+"▣▣▣▣▣");
			isSuccess = true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}
	
	
	/**
	 * 오퍼레이션명 : 대기장 도착 MSG 생성
 	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */ 
	public String getCarMsg(String s_STL_APPEAR_GP , String s_STACK_YD_GP, String  s_STACK_BAY_GP, String  szTRN_EQP_GP,String szARR_WLOC_CD,String szYD_POINT_CD) {
    	String trnEqpQueryId ="";
    	
    	ymCommonDAO dao = ymCommonDAO.getInstance();
    	
    	List unloadEndPointList = null;
    	try{
    		//-------------------------------------------------------------------------
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return null;
    		}
    		//개소코드 체크
			if(szARR_WLOC_CD.equals("")){
				return "입력된 개소코드가 존재 안함.";
			}
			
			//코일야드 만 해당됨
			if(	szARR_WLOC_CD.equals("D2Y44")||szARR_WLOC_CD.equals("D2Y45")||
				szARR_WLOC_CD.equals("D3Y41")||szARR_WLOC_CD.equals("D3Y42")||
				szARR_WLOC_CD.equals("DJY21")||szARR_WLOC_CD.equals("DJY22")||
				szARR_WLOC_CD.equals("DJY1E")){
				 //-------------------------------------코일야드-------------------------------------------
			 
			
			
	    		if(s_STL_APPEAR_GP.equals("Y")){
	    			//******************************제품 ***************************************
	    			//포인트코드 체크
	    			if(szYD_POINT_CD.equals("")){
	    				return "입력된 포인트코드가 존재 안함.";
	    			}
	    			
	    			//개소코드 체크
					trnEqpQueryId = "ym.tsinfo.getListUnloadEndpointGoods_chk";
		    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szARR_WLOC_CD,szYD_POINT_CD});
		    		if(unloadEndPointList.size() <= 0){
		    			return s_STACK_BAY_GP+"동  지시개소코드가 야드와 틀림.";
		    		}
		    		
		    		//포인트 체크
					trnEqpQueryId = "ym.tsinfo.getListUnloadEndpointGoods_chk2";
		    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szARR_WLOC_CD,szYD_POINT_CD});
		    		if(unloadEndPointList.size() > 0){
		    			return s_STACK_BAY_GP+"동 개소지의 야드포인트가 사용불가.";
		    		}
				 
		    		//다른차량 체크
					trnEqpQueryId = "ym.tsinfo.getListUnloadEndpointGoods_chk4";
		    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szARR_WLOC_CD,szYD_POINT_CD});
		    		if(unloadEndPointList.size() > 0){
		    			return s_STACK_BAY_GP+"동 해당개소에 다른 차량 점유.";
		    		}
	    			
	    		}else{
	    			//******************************소재 ***************************************
		    		//목적동 체크
					if(s_STACK_BAY_GP.equals("")){
						return "목적동OR이송대상이 존재 안함.";
					}
					 
					//야드 체크
					if(s_STACK_YD_GP.equals("")){
						return "입력된 야드코드가 존재 안함.";
					}
					
					//TR/PT 체크
					if(szTRN_EQP_GP.equals("")){
						return "입력된 장비구분(TR/PT)가 존재 안함.";
					}
					
					//개소코드 체크
					trnEqpQueryId = "ym.tsinfo.getListUnloadEndpoint_chk";
		    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STACK_YD_GP,s_STACK_BAY_GP,szTRN_EQP_GP,szARR_WLOC_CD});
		    		if(unloadEndPointList.size() <= 0){
		    			return s_STACK_BAY_GP+"동 지시개소코드가 야드와 틀림.";
		    		}
		    		
		    		//포인트 체크
					trnEqpQueryId = "ym.tsinfo.getListUnloadEndpoint_chk2";
		    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STACK_YD_GP,s_STACK_BAY_GP,szTRN_EQP_GP,szARR_WLOC_CD});
		    		if(unloadEndPointList.size() > 0){
		    			return s_STACK_BAY_GP+"동 개소지의 야드포인트가 사용불가.";
		    		}
				 
		    		//다른차량 체크
					trnEqpQueryId = "ym.tsinfo.getListUnloadEndpoint_chk3";
		    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{s_STACK_YD_GP,s_STACK_BAY_GP,szTRN_EQP_GP,szARR_WLOC_CD});
		    		if(unloadEndPointList.size() > 0){
		    			return s_STACK_BAY_GP+"동 해당개소에 다른 차량 점유.";
		    		}
 
	    		}

	    		//-------------------------------------코일야드-------------------------------------------
			}else{
				//-------------------------------------슬라브야드-------------------------------------------
				return "";
				//-------------------------------------슬라브야드-------------------------------------------
			}
			//-------------------------------------------------------------------------
    		 
			return "시스템 담당자 확인 요망.";
    	}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
 
    }
	
	
	/**
	 * 오퍼레이션명 : 재료단위 이송지시 취소 작업 TC전송(YDPTJ007)
 	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */     	 
	public boolean CarStlMTPCancel( String szSTL_NO , String szSPOS_WLOC_CD , String szARR_WLOC_CD, String szORD_YEOJAE_GP, String szRE_WO_LMT_RSN_CD,String szSCH_ID )throws DAOException {
		String szRE_WO_LMT_YN="Y";
		
		boolean isSuccess = false; 
		Boolean isFalse = new Boolean(false);
		Boolean resultRes = new Boolean(false); 
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			logger.println(LogLevel.DEBUG,this, "▣▣▣▣재료단위 이송지시 취소 작업 TC전송 (START): "+szSTL_NO+","+szRE_WO_LMT_RSN_CD+","+szSCH_ID+" ▣▣▣▣▣");
			
			//크레인 스케줄 취소  *************************************************************** 
			if(!"".equals(szSCH_ID)){
				EJBConnector ejbCon = new EJBConnector("default", "JNDICraneSchReg", this);
				isFalse =  (Boolean)ejbCon.trx("cancelCoilSchInfo",new Class[]{ String.class}, new Object[]{ szSCH_ID});
			}
			
			//작업예약 취소 *******************************************************************
			EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneStatusReg",this);
			resultRes =  (Boolean) ejbConn1.trx("setWorkIdCancle", new Class[] {String.class}, new Object[] {szSTL_NO});	
 		    
			//이송지시취소 TC전송**************************************************************
			 
			//지시보류
			if("X".equals(StringHelper.evl(szRE_WO_LMT_RSN_CD,""))){
				szRE_WO_LMT_RSN_CD="";
				szRE_WO_LMT_YN ="N";
			}
			
			JDTORecord FrtoendRecord = null;
	    	FrtoendRecord = JDTORecordFactory.getInstance().create(); 
	    	FrtoendRecord.setField("JMS_TC_CD", "YDPTJ007");
	    	FrtoendRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));												    					    					    	
	    	FrtoendRecord.setField("STL_NO", StringHelper.evl(szSTL_NO, "").trim());// 재료번호	
	    	FrtoendRecord.setField("SPOS_WLOC_CD", StringHelper.evl(szSPOS_WLOC_CD, "").trim());// 발지개소코드
	    	FrtoendRecord.setField("ARR_WLOC_CD", StringHelper.evl(szARR_WLOC_CD, "").trim());// 착지개소코드
	    	FrtoendRecord.setField("ORD_YEOJAE_GP", StringHelper.evl(szORD_YEOJAE_GP, "").trim());// 주문여재구분
	    	FrtoendRecord.setField("RE_WO_LMT_RSN_CD", StringHelper.evl(szRE_WO_LMT_RSN_CD,""));	// 재지시제한사유		
	    	FrtoendRecord.setField("RE_WO_LMT_YN", szRE_WO_LMT_YN);	// 재지시제한여부			    	
	    	FrtoendRecord.setField("CANCEL_DATE", new String(YmCommonUtil.getTcDate("yyyyMMdd")));	// 취소일자														

	    	
	    	EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
	    	resultRes = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
			  	  	 													new Object[]{FrtoendRecord});
			logger.println(LogLevel.DEBUG,this, "내부IF호출=== 슬라브소재이송완료실적.==="); 
			
			
 		   logger.println(LogLevel.DEBUG,this, "▣▣▣▣재료단위 이송지시 취소 작업 TC전송 (END): "+szSTL_NO+","+szRE_WO_LMT_RSN_CD+","+szSCH_ID+" ▣▣▣▣▣");
			isSuccess = true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}
	
	
	 /**
     * 저장품 테이블에 INSERT 한다.
     * @param 	ydStockDAO		: DAO
     * @param 	slabNo			: 슬라브번호
     * @param 	stockStat		: 저장품상태
     * @return 	1(성공),0(실패)
     */
    private int createSlabInfo2(	String sChargeLotNo, 
    							String sStocMv,
    							String slabNo) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return 0;
		}
		
        List insertData = new ArrayList();
        insertData.add(slabNo);					//저장품 ID
        insertData.add("");						//작업예약 ID
        insertData.add(YmCommonConst.ITEM_SM); 	//저장품 품목
        insertData.add("");						//저장품 상태        
        insertData.add("");						//저장품 냉각 상태
        insertData.add("");						//저장품 냉각 시작 일시
        insertData.add("");						//저장품 냉각 시작 온도
        insertData.add("");						//산적 LOT 번호
        insertData.add(sStocMv);				//저장품 이동 조건
        insertData.add("");						//이송 설비 구분
        insertData.add("");						//이송 설비 BED 구분
        insertData.add("");						//이송 설비 단 구분
        insertData.add(sChargeLotNo);			//장입 LOT 번호
        insertData.add("");						//이송 지시 번호
        insertData.add("");						//운송 작업지시 번호
        insertData.add(""); 					//SCARFING 보급 유무
        insertData.add("");						//차량 CARD 번호
        insertData.add("");						//정정 보급 순서
        insertData.add("");						//CTS 중계 구분
        insertData.add("");						//CTS 중계 동
        insertData.add("");						//CTS 중계 SADDLE
        insertData.add("");						//하차 YARD
        insertData.add("");						//하차 동
        insertData.add("TSYDJ004");				//등록자
        insertData.add("");						//수정자
        insertData.add("");						//수정 일시
        return new YdStockDAO().createData(insertData);		        
    }  

    
    /**
     * 장입확정시 L-2로 슬라브정보를 송신한다.
     * @param dto	슬라브정보
     */
    private void sendSlabInfo2(JDTORecord slabInfo,String sFormGp) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
    	sendQueue(YmCommonConst.TC_CM1BP02, YmCommonUtil.getSlabMsgInfo(slabInfo,sFormGp));
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
}

