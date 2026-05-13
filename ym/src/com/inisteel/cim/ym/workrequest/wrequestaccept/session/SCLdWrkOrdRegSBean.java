package com.inisteel.cim.ym.workrequest.wrequestaccept.session;

import java.util.List;
import java.util.Map;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.util.CommonUtil;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.jms.model.pc.ZZPC001;
import com.inisteel.cim.common.parser.Level2Parser;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackColDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="SCLdWrkOrdRegEJB" jndi-name="JNDISCLdWrkOrdReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class SCLdWrkOrdRegSBean extends BaseSessionBean {
	private Logger logger 			= null;
	 // SJH    
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger 		= new Logger(config);
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 넘어온 정보를 가지고 해당 업무 로직을 처리한다.
        * 전문내용을 JDTORecord로 파싱한다.
        * 업무 로직
        * 1.TC_CD - CM1PB11 (I/F ID : YM-BIF-028)
        *	2.야드 Level-2 시스템으로부터 대차도착 정보를 수신
        *  3.영대차인지 공대차인지 점검
        *  4.대차 현위치 변경
        *  5.적재상태가 'L'일때
        *  6.적재상태가 'U'일때 
        *  7.5/6번에 해당하는 상하차 작업 Schedule Code가 지정 또는 검색 되었다면 Coil Schedule Call
        *  8.대차 작업 진행 상태 변경 
        *    - Schedule을 수행했다면 'S', Schedule을 미수행했다면 'W'
        *  대차 설비 번호 
        *	2XTC01 : SLAB대차01호
        *	2XTC02 : SLAB대차02호
        *	2XTC03 : SLAB대차03호
	 *	
	 *	3XTC01 : 하이스코대차
	 *	3XTC02 : HFL대차
	 *	3XTC03 : 동간이적대차
	 * 	상차 지정 유무 CARLOAD_ASSIGN_YN    VARCHAR2(1)  NULL
	 *	하차 지정 유무 CARUNLOAD_ASSIGN_YN  VARCHAR2(1)  NULL
	 *
        *	1.영차인지 공차인지 점검. 적재상태, 진행상태에 따라 대차 작업 선택 STACK_STAT(적재상태), WPROG_STAT(진행상태)를 점검
	 *	2.대차 현위치 변경
	 *	3.적재상태가 'L'일때
        *	 - 설비상차작업 지정이면 Schedule 호출
        *	 - 설비상차작업 미지정이고 상차 Schedule Code가 없다면 작업예약 Table에서 상차 동간이적에 해당하는 Schedule 검색  
	 *	4.적재상태가 'U'일때
	 *	  -(공통)현재 대차에 실려있는 작업 대상재 검검
        *  	  -(공통)하차작업이면 도착위치 Map에 등록  
	 *      - 하차작업이고 Schedule Code가 미지정되어 있으면 대차 하차작업 Schedule Code로 작업예약 Table에 등록
	 *	5.Schedule 기동이 지정되어 있으면 Slab Schedule Call
	 *	6.대차 작업 진행 상태 변경
	 *	  - 설비 Table의 차량작업상태 Code를 조건에 맞게 변경
        * --------------------------------------------
        * CM1PB11
        * logger.println("전문코드		==" +jDTORecord.getFieldString("전문코드"));
	 * logger.println("발생일자		==" +jDTORecord.getFieldString("발생일자"));
	 * logger.println("발생시간 	==" +jDTORecord.getFieldString("발생시간"));
	 * logger.println("전문구분		==" +jDTORecord.getFieldString("전문구분"));    // I : Initialize, U : Update, D : Delete, R : Re-request
	 * logger.println("전문길이		==" +jDTORecord.getFieldString("전문길이"));
	 * logger.println("대차번호		==" +jDTORecord.getFieldString("대차번호"));    // 설비구분 : TC(2) + TC NO(2)
	 * logger.println("대차도착동 	==" +jDTORecord.getFieldString("대차도착동"));  // YARD구분(1) + 동구분(1)+ SPAN(2) + 열 NO(2)
	 * 
	 * --------대차출발 지시-------CM1PB04 / YM-BIF-032
        * logger.println("전문코드	  	==" +jDTORecord.getFieldString("전문코드"));
	 * logger.println("발생일자	  	==" +jDTORecord.getFieldString("발생일자"));
	 * logger.println("발생시간     ==" +jDTORecord.getFieldString("발생시간"));
	 * logger.println("전문구분	  	==" +jDTORecord.getFieldString("전문구분"));     // I : Initialize, U : Update, D : Delete, R : Re-request
	 * logger.println("전문길이	  	==" +jDTORecord.getFieldString("전문길이"));
	 * logger.println("대차번호	  	==" +jDTORecord.getFieldString("대차번호"));     // 설비구분 : TC(2)+TC NO(2)
	 * logger.println("대차도착동   ==" +jDTORecord.getFieldString("대차현재동"));   // YARD구분(1) + 동구분(1)+ SPAN(2) + 열 NO(2)
	 * logger.println("대차TO동    	==" +jDTORecord.getFieldString("대차TO동"));     // YARD구분(1) + 동구분(1)+ SPAN(2) + 열 NO(2)
	 * 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveOutSCLdWrkOrd(String sMessage) { 
		
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
			
			String tcTcGbn = StringHelper.evl(jDTORecord.getFieldString("전문구분"), "");   
			
			if("U".equals(tcTcGbn)){
				
				String tcTcNo = StringHelper.evl(jDTORecord.getFieldString("대차번호"), "");   
				         tcTcNo = YmCommonConst.TC_2X + tcTcNo;   
				String tcCurrStopLoc = StringHelper.evl(jDTORecord.getFieldString("대차도착동"), ""); 
				
				logger.println(LogLevel.DEBUG,this,"=Level 2 비상조업 대차 백업실적="); 
				logger.println(LogLevel.DEBUG,this,"=Level 2 비상조업 대차 백업실적 대차번호	="+tcTcNo); 
				logger.println(LogLevel.DEBUG,this,"=Level 2 비상조업 대차 백업실적 대차도착동	="+tcCurrStopLoc); 
			
				CraneSchDAO dao = new CraneSchDAO();
				dao.updateEquipTcInfo("L","W",tcTcNo);
				dao.updateEquipCurLocWithEquipGp(tcTcNo, tcCurrStopLoc);
			    							  		    
				return true;
			}else{
				return receiveOutSCLdWrkOrd(jDTORecord);
			}
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
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
	public boolean receiveOutSCLdWrkOrd(JDTORecord jDTORecord) { 
        
		logger.println(LogLevel.DEBUG,this,"Start-receiveOutCCLdWrkOrd()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackColDAO ydStackColDAO     = new YdStackColDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO CraneSchDAO         = new CraneSchDAO();
		
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iResult = 0;
			int iReq 	= 0;
			
			String wBookidSchCall = "";
			String Glo_Sch_Call   = "";
			
			// 야드 Level-2에서 대차 도착 정보를  수신			
			/*
			 *	B열연 SLAB 대차번호(EX:TC01) : TC(2) + TC NO(2)
			 */
			String tcTcNo = StringHelper.evl(jDTORecord.getFieldString("대차번호"), "");   
				   tcTcNo = YmCommonConst.TC_2X + tcTcNo;   
			/*
			 *	B열연 SLAB 대차도착동(EX:2ATC11) : YARD구분(1) + 동구분(1)+ SPAN(2) + 열 NO(2)
			 */
			String tcCurrStopLoc = StringHelper.evl(jDTORecord.getFieldString("대차도착동"), ""); 
			
			/*
			 *	B열연 SLAB 대차도착동 야드구분
			 */
			String tmpYd_Gp  = tcCurrStopLoc.trim().substring(0, 1);
			
			/*
			 *	B열연 SLAB 대차도착동 동구분
			 */
			String tmpBay_Gp = tcCurrStopLoc.trim().substring(1, 2); 
			
			/*
			 *	B열연 SLAB 대차의 EQUIP 설정 정보를 가져온다.	
			 *
			 *	대차정보 고장 및 OFFLINE 체크
			 *	대차최대적치수량
			 */
			String sEquipStat 		= "";
			String sWorkMode		= "";
			String sStackStat		= "";
			String sStackMaxQnty	= "";
			String sCurrStopLoc  	= "";
			String sCurrStopBay  	= "";
			
			String tmpLoadGp        = "";
			String tmpLoadSch       = "";
			String tmpUnLoadGp      = "";
			String tmpUnLoadSch     = "";
			String tmpLoadStopLoc   = "";
			String tmpUnLoadStopLoc = "";

			JDTORecord equipJr = CraneSchDAO.getEquipInfoWithEquipGp(tcTcNo);
			
			if(equipJr != null){
				sEquipStat 		= StringHelper.evl(equipJr.getFieldString("EQUIP_STAT"), "");
				sWorkMode  		= StringHelper.evl(equipJr.getFieldString("WORK_MODE"), "");
				sStackStat 		= StringHelper.evl(equipJr.getFieldString("STACK_STAT"), "");
				sStackMaxQnty	= StringHelper.evl(equipJr.getFieldString("STACK_MAX_QNTY"), "0");
				sCurrStopLoc  	= StringHelper.evl(equipJr.getFieldString("CURR_STOP_LOC"), "");
				
				tmpLoadGp        = StringHelper.evl(equipJr.getFieldString("CARLOAD_ASSIGN_YN"), "");        //상차지정유무
				tmpLoadSch       = StringHelper.evl(equipJr.getFieldString("CARLOAD_SCH_WORK_KIND"), "");    //상차 Schedule 작업종류
				tmpUnLoadGp      = StringHelper.evl(equipJr.getFieldString("CARUNLOAD_ASSIGN_YN"), "");      //하차지정유무
				tmpUnLoadSch     = StringHelper.evl(equipJr.getFieldString("CARUNLOAD_SCH_WORK_KIND"), "");  //하차 Schedule 작업종류
				tmpLoadStopLoc   = StringHelper.evl(equipJr.getFieldString("CARLOAD_STOP_LOC"), "");         //상차위치
				tmpUnLoadStopLoc = StringHelper.evl(equipJr.getFieldString("CARUNLOAD_STOP_LOC"), "");       //하차위치

			}
			 
			if(YmCommonConst.WORK_MODE_C.equals(sEquipStat)||
			   YmCommonConst.WORK_MODE_C.equals(sWorkMode)){
				
				logger.println(LogLevel.DEBUG,this,"=대차정보 고장및 OFFLINE 처리="+sEquipStat+sWorkMode);
				return false;
			}
			
			/*
			 *	B열연 SLAB 대차의 EQUIP 설정 현재동.
			 */
			if(sCurrStopLoc.length() > 2){
				sCurrStopBay  = sCurrStopLoc.substring(1,2); // 1BTC03 -> B
			}
			if (tmpBay_Gp.trim().equals(sCurrStopBay)){
				logger.println(LogLevel.DEBUG,this,"대차도착=>대차 도착동이 현재 도착동과 같습니다.");
				return false;
			}
			
			/**************************************************************
			 *	대차도착처리 예외사항1 시작
			 *	=>	도착처리시	=>	하차상태('U')이고	=>	공대차이면(저장품정보 존재안함)
			 *	=>	상차상태('L')로 수정한다.
			 **************************************************************/
			if (sStackStat.equals(YmCommonConst.STACK_STAT_U)){  
				
				String sYQeury1  = "ym.steelinfo.steelinforecv.dao.YdStockDAO.selectStock";
				List stockL      = ydStockDAO.getListData(sYQeury1, new Object[]{ tcTcNo });					
				
				int iCount = 0;
				JDTORecord cTcJr = CraneSchDAO.getTCLoadCount(tcCurrStopLoc); 
				if(cTcJr != null){
					iCount = cTcJr.getFieldInt("CNT");
				}
					
				if(stockL.size() == 0 && 	
				   iCount		 == 0 ){
				   	
				   	int iSeq = CraneSchDAO.updateEquipTcInfo(YmCommonConst.STACK_STAT_L,
															 YmCommonConst.WPROG_STAT_W,
															 tcTcNo); 
					logger.println(LogLevel.DEBUG,this,"대차도착=>대차도착처리 예외사항.");   	
					logger.println(LogLevel.DEBUG,this,"대차도착=>상차대기상태로 수정.");   	
				}
			}
			/**************************************************************
			 *	대차도착처리 예외사항1 종료
			 **************************************************************/
			 
			/**
			 *	도착처리시 다른동 적치단 활성상태를 모두 비활성화 한다.
			 */
			String tmpTcNo = "2_TC"+tcCurrStopLoc.substring(4,5)+"_";
														  	
			iReq = CraneSchDAO.updateStackLayerStatInfo(tmpTcNo);
			
			logger.println(LogLevel.DEBUG,this,"대차위치정보 초기화="+tmpTcNo+"="+iReq);
		
			/*
			 *	1.1	적재상태가 'L' (상차) 일때
			 */
			if (sStackStat.equals(YmCommonConst.STACK_STAT_L)){         
				//SKIP
			/*
			 *	1.2	적재상태가 'U' (하차) 일때
			 */	 
			}else if (sStackStat.equals(YmCommonConst.STACK_STAT_U)){   //하차
				
				if (!tmpBay_Gp.trim().equals(tmpUnLoadStopLoc.trim().substring(1, 2))){
					throw new EJBServiceException("대차도착 에러 : 대차하차 도착동이 목적동이 아닙니다.");
				}											
			}
			
		   /*
			*	2	SLAB 대차의 현재위치를 변경한다.
			*/
			String updateCurrQueryId = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateLoadCurrLoc";
			int CurrStop = ydStackLayerDAO.requestupdateData(updateCurrQueryId, 
															 new Object[]{ tcCurrStopLoc, 
															 			   tcTcNo });										
			
		    /**************************************************************
			 *	대차도착처리 예외사항2 시작
			 *	=>	도착처리시	=>	상차상태('L')이고	=>	공대차가 아니면(저장품정보 존재함)
			 **************************************************************/
			if (sStackStat.equals(YmCommonConst.STACK_STAT_L)){  
				
				int iCount = 0;
				JDTORecord cTcJr = CraneSchDAO.getTCLoadCount(tcCurrStopLoc); 
				if(cTcJr != null){
					iCount = cTcJr.getFieldInt("CNT");
				}
				logger.println(LogLevel.DEBUG,this,"현 적치된 저장품정보=>"+iCount);
				
				if(iCount > 0){
					
					ymCommonDAO dao1 = ymCommonDAO.getInstance();
				
					int iMaxCount = Integer.parseInt(sStackMaxQnty);
					
	                for(int inx = 0; inx < iMaxCount; inx++) {     
	                	
	                	dao1.modifyActiveStatOfLayer(YmCommonConst.STACK_LAYER_ACTIVE_STAT_O, 
	                								 tcCurrStopLoc, 
	                								 YmCommonConst.STACK_BED_GP_01, 
	                								 "0"+ (inx + 1));
	                }
	                
					/*
					 *	대차 작업 진행 상태 변경
					 */
					String uQeury = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateWprogStat";
					int uSeq      = ydStockDAO.requestupdateData(uQeury, new Object[]{YmCommonConst.WPROG_STAT_S, 
																					  tcTcNo });		
					
					logger.println(LogLevel.DEBUG,this,"대차도착처리 예외사항2=> 이미 도착처리모듈 처리");
					logger.println(LogLevel.DEBUG,this,"대차도착처리 예외사항2=> 적치단만 OPEN 및 대차상태 S로 셋팅");
					
					boolean isWork = bookDummyWorkInfo_03(jDTORecord);  
						
					return false;
				}
			}		
		    /**************************************************************
			 *	대차도착처리 예외사항2 종료
			 **************************************************************/
			 
		    /*
		     *	3	적재상태가 'L'(상차) 일때 
		     */
			if (sStackStat.equals(YmCommonConst.STACK_STAT_L)){         
				
				/*
				 *	3.1	SLAB 대차의 상차작업이 지정일 경우.
				 */
				if (tmpLoadGp.equals(YmCommonConst.CARLOAD_ASSIGN_Y)) {       
					
					/*
					 *	3.1.0	SLAB 대차 작업인 경우 EQUIP TABLE STACK_MAX_QNTY 까지 처리한다.
					 */
						int iMaxCount = Integer.parseInt(sStackMaxQnty);
					
					/**
					 *	3.1.0	대차도착시에 상차작업예약 자동검색(W/B 보급)
					 */
						boolean isTrue = selectWBSearchLineInSlab(tmpLoadSch,
																  tmpUnLoadSch,
																  tmpBay_Gp,
																  tmpLoadStopLoc.substring(1,2),
																  iMaxCount,
																  tcCurrStopLoc);
						
					/* 
					 *	3.1.1	SLAB 대차의 상차 스케쥴 지정 작업예약을 검색
					 *			SLAB 인 경우는 여러건의 작업예약을 동시에 스케쥴 호출
					 *			동간이적인 경우는 상차위치가 현 대차인지 체크한다.
					 */ 		
					String sLoadSchQueryId 	= "";
					List wBookid1 			= null;
					 
					if(YmCommonConst.NEW_SCH_WORK_KIND_STML.equals(tmpLoadSch)||
					   YmCommonConst.NEW_SCH_WORK_KIND_STM2.equals(tmpLoadSch)){ 	
					
						sLoadSchQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectLoadSch_01";
						wBookid1 = ydStockDAO.getListData(sLoadSchQueryId, new Object[]{tmpYd_Gp.trim(), 
															   			    		 tmpBay_Gp.trim(), 
															   			    		 tmpLoadSch.trim(),
															   			    		 "2_TC"+tcTcNo.substring(5, 6)+"_"}); //TO위치
															   			    			
					}else{
					
						sLoadSchQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectLoadSchNEW";
						wBookid1 = ydStockDAO.getListData(sLoadSchQueryId, new Object[]{tmpYd_Gp.trim(), 
															   			    		    tmpBay_Gp.trim(), 
															   			    			tmpLoadSch.trim(),
															   			    			tcTcNo});
						
					}
					
					JDTORecord TmpSelBedAddr       = null;
					int MaxRec                     = wBookid1.size();	
					String[] tmpWbookID            = new String[MaxRec];
					
					/*
					 *	3.1.2	SLAB 작업예약 리스트를 작성한다.
					 */
					for (int ii=0; ii<MaxRec; ii++){
						if (ii < iMaxCount){
							TmpSelBedAddr	= (JDTORecord) wBookid1.get(ii);
							tmpWbookID[ii]  = StringHelper.evl(TmpSelBedAddr.getFieldString("WBOOK_ID"), "");
							Glo_Sch_Call   += tmpWbookID[ii].trim() +"-" ;							
						}
					}
					
				/*
				 *	3.2	SLAB 대차의 상차작업이 미지정일 경우.
				 */	
				}else if (tmpLoadGp.equals(YmCommonConst.CARLOAD_ASSIGN_N)) { 
					/* 
					 *	3.2.1	동간이적상차(STML,STM2) 작업예약을 검색.
					 */
					if ("".equals(tmpLoadSch.trim())){

						String sLoadSchQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectLoadSch_02";
						List wBookid1 = ydStockDAO.getListData(sLoadSchQueryId, 
															 new Object[]{tmpYd_Gp.trim(), 
													   		    	              tmpBay_Gp.trim(), 
													   			    	  	YmCommonConst.NEW_SCH_WORK_KIND_STML,
													   			    	  	YmCommonConst.NEW_SCH_WORK_KIND_STM2,
													   			    	  	"2_TC"+tcTcNo.substring(5, 6)+"_"}); //TO위치
														   			    			
						
						JDTORecord TmpSelBedAddr       = null;
						int MaxRec                     = wBookid1.size();	
						String[] tmpWbookID            = new String[MaxRec];
						
						/*
						 *	3.2.2	SLAB 대차 작업인 경우 EQUIP TABLE STACK_MAX_QNTY 까지 처리한다.
						 */
							int iMaxCount = Integer.parseInt(sStackMaxQnty);
						/*
						 *	3.2.3	SLAB 작업예약 리스트를 작성한다.
						 */
						for (int ii=0; ii<MaxRec; ii++){
							if (ii < iMaxCount){
								TmpSelBedAddr	= (JDTORecord) wBookid1.get(ii);
								tmpWbookID[ii]  = StringHelper.evl(TmpSelBedAddr.getFieldString("WBOOK_ID"), "");
								Glo_Sch_Call   += tmpWbookID[ii].trim() +"-" ;							
							}
						}			
					}
				}
				
			/*
			 *	4	적재상태가 'U'(하차) 일때
			 */
			}else if (sStackStat.equals(YmCommonConst.STACK_STAT_U)){
				
				/*
				 *	4.1	현재 대차에 실려있는 작업 대상재 검검
				 */
				String selectStockQuery = "ym.steelinfo.steelinforecv.dao.YdStockDAO.selectStock";
				List StockId = ydStockDAO.getListData(selectStockQuery, new Object[]{ tcTcNo });					
				
				JDTORecord TmpSelBedAddr       = null;
				int MaxRec                     = StockId.size();	
				String[] tmpStockID            = new String[MaxRec];
				String[] tmpFromEquipBedGp     = new String[MaxRec];
				String[] tmpFromEquipLayerGp   = new String[MaxRec];
				String[] tmpFromStockMoveTerm  = new String[MaxRec];
				
				for (int ii=0; ii<MaxRec; ii++){
					TmpSelBedAddr              = (JDTORecord) StockId.get(ii);
					tmpStockID[ii]             = StringHelper.evl(TmpSelBedAddr.getFieldString("STOCK_ID"), "");
					tmpFromEquipBedGp[ii]      = StringHelper.evl(TmpSelBedAddr.getFieldString("FRTOMOVE_EQUIP_BED_GP"), "");
					tmpFromEquipLayerGp[ii]    = StringHelper.evl(TmpSelBedAddr.getFieldString("FRTOMOVE_EQUIP_LAYER_GP"), "");
					tmpFromStockMoveTerm[ii]   = StringHelper.evl(TmpSelBedAddr.getFieldString("STOCK_MOVE_TERM"), "");
				}
				
				/*
				 *	4.2	공통에서 진도코드를 검색해서 저장품 이동조건을 셋팅한다.
				 */
				for (int ii=0; ii<MaxRec; ii++){
					String[] sStockInfo        = YmCommonUtil.getSlabCurrProgCd(tmpStockID[ii],"");
				    String sProgCd   	       = sStockInfo[0];
					String sStocMv   	       = sStockInfo[1];
					tmpFromStockMoveTerm[ii]   = sStocMv;
				}

				for (int ii=0; ii<MaxRec; ii++){
					/*
					 *	4.3	SLAB 공통 Table 저장위치 Update 
					 */						
					iReq = CraneSchDAO.updateSlabCommonLocInfo(tmpStockID[ii],
							                                   tcCurrStopLoc + 
							                                   tmpFromEquipBedGp[ii] + 
							                                   tmpFromEquipLayerGp[ii]);
					/*
					 *	4.4	대차 도착 작업 실적 등록 
					 */
					 iReq = insertUpPutWrslRtData(tmpStockID[ii].trim(), 
							                      tmpLoadStopLoc 		+ 
							                      tmpFromEquipBedGp[ii] + 
							                      tmpFromEquipLayerGp[ii], 
							                      tcCurrStopLoc 		+ 
							                      tmpFromEquipBedGp[ii] + 
							                      tmpFromEquipLayerGp[ii], 
							                      tmpUnLoadSch.trim(), 
												  tmpYd_Gp.trim(),
												  tcTcNo);						
				}				
				
				/* 
				 *	4.5	STACKCOL INFO ?
				 *	(공통)하차작업이면 도착위치 Map에 등록
				 * 	대차출발위치차량번호변경	
				 */
				String updateArriveQuery = "ym.facilitystatus.facilityinquiry.dao.YdStackColDAO.updateStockColArrive";
				int updateColArrive      = ydStackColDAO.requestupdateData(updateArriveQuery, 
																		   new Object[]{ tcTcNo });								

				/*
				 *	4.6	STACKCOL INFO ?
				 *	(공통)대차도착위치정지포인트변경	                   
				 */
				String updateArriveCarNoQuery = "ym.facilitystatus.facilityinquiry.dao.YdStackColDAO.updateStockColArriveCarNo";
				int updateColArriveCarNo      = ydStackColDAO.requestupdateData(updateArriveCarNoQuery, 
																				new Object[]{ tcTcNo, 
																							  tcTcNo });		
				
				/*
				 *	4.7	저장품이송설비항목 하차동으로 업데이트(저장품갯수만큼)	
				 */
				for (int ii=0; ii<MaxRec; ii++){
					if (tmpStockID[ii].trim() != null && !tmpStockID[ii].trim().equals("")){
						String updateFromEquipGpQuery = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateFromEquipGp";
						int updateFromEquipGp = ydStockDAO.requestupdateData(updateFromEquipGpQuery, 
																			 new Object[]{tcTcNo, 
																			 			  tmpStockID[ii].trim() });
					}
				}
				
				/*
				 *	4.8	SLAB 대차출발위치 CLEAR
				 */
				String updateClearStockIdQuery = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateClearStockId_01";
				int updateClearStockId         = ydStackLayerDAO.requestupdateData(updateClearStockIdQuery, 
																				   new Object[]{ tcTcNo });
				
				/*
				 *	4.9	SLAB 대차도착위치 저장품정보 업데이트(저장품갯수만큼)		
				 *		STOCK_ID,STACK_LAYER_STAT('L')
				 */
				for (int ii=0; ii<MaxRec; ii++){
					if (tmpStockID[ii].trim() != null && !tmpStockID[ii].trim().equals("")){
						String updateUnLoadStopLocQuery = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateUnLoadStopLocStock";
						int updateUnLoadStopLoc         = ydStockDAO.requestupdateData(updateUnLoadStopLocQuery, 
																					   new Object[]{tmpStockID[ii].trim(), 
																					   				tcTcNo, 
																					   				tmpFromEquipBedGp[ii].trim(), 
														  											tmpFromEquipLayerGp[ii].trim() });
					}
				}
				
				/*
				 *	4.10	목적동에 도착시 현재 수량과 가능수량을 조절한다.
				 */
				for (int ii=0; ii<MaxRec; ii++){
					String selectBedMax = "ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.selectSlabBedMax";
					JDTORecord bedMax   = ydStackLayerDAO.requestgetData(selectBedMax, 
																		 new Object[]{tcTcNo, 
																		 			  tmpFromEquipBedGp[ii].trim()});	
					
					String TmpStackerBedMax      = StringHelper.evl(bedMax.getFieldString("STACK_BED_QNTY_MAX"), "");
					String TmpStackerBedWTMax    = StringHelper.evl(bedMax.getFieldString("STACK_BED_WT_MAX"), "");
					String TmpStackerBedHighMax  = StringHelper.evl(bedMax.getFieldString("STACK_BED_HIGH_MAX"), "");
					
					if (TmpStackerBedMax != null){
						String updatestackctSet  = "ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.updateSlabStackCountSet";
						int updateStackerCTSet   = ydStackLayerDAO.requestupdateData(updatestackctSet, 
																					 new Object[]{TmpStackerBedMax.trim(), 
																					 			  YmCommonConst.STACK_BED_ABLE_QNTY_0, 
												   												  TmpStackerBedWTMax.trim(), 
												   												  TmpStackerBedHighMax.trim(), 
												   												  tcTcNo, 
												   												  tmpFromEquipBedGp[ii].trim() });										
					}
				}					
							

				/*
				 *	4.11	작업예약 생성.
				 */
				String wStockId  = "";
				String wSchCode  = "";
				String wStockMv  = "";
				
				if (tmpUnLoadGp.equals(YmCommonConst.CARLOAD_ASSIGN_Y)) {	    //작업 지정
					
					wSchCode	= tmpUnLoadSch.trim();
										
				}else if (tmpUnLoadGp.equals(YmCommonConst.CARLOAD_ASSIGN_N)) {	//작업 미지정
					
					wSchCode	= YmCommonConst.NEW_SCH_WORK_KIND_STMU;
				}
								
				JDTORecord wBookSel 	= null;
						
				String sQueryId 		= "";
				String sWbookId			= "";
				String sCarunloadBay 	= "";
				
				int iSeq = 0;
				
				CraneSchDAO dao2 = new CraneSchDAO();
				ymCommonDAO dao1 = ymCommonDAO.getInstance();
				
				for (int index = 0; index < MaxRec; index++){
					
					if ( tmpStockID[index].trim() != null && 
					    !tmpStockID[index].trim().equals("")){
						
						wStockId  = tmpStockID[index].trim();
						wStockMv  = tmpFromStockMoveTerm[index].trim();
				
						/*
						 *	YJK.20060613.	
						 *	작업예약 생성시 저장품TABLE에 CARUNLOAD_PUT_LOC 항목에 
						 *	특정위치값이 존재하면 OPERATION 지정으로 작업예약을 생성한다.
						 */
						
						JDTORecord stockRc = dao2.getStockInfo(wStockId);
						
			    		if(stockRc != null){
			    			sCarunloadBay	= StringHelper.evl(stockRc.getFieldString("CARUNLOAD_PUT_LOC"), "");//하차PUT위치
			    		}
			    		logger.println(LogLevel.DEBUG,this, "sCarunloadBay="	 + sCarunloadBay);
			    		
			    		if(sCarunloadBay.length() == 10){
			    			
			    			sWbookId 		 = dao1.createWBook(tcCurrStopLoc, 
								    						    wSchCode, 
								    						    "O", 
								    						    sCarunloadBay);
			    			
			    		}else{
			    		
							sQueryId 	= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectYdWBook";
							wBookSel 	= ydStackLayerDAO.requestFind(sQueryId);
							if (wBookSel == null){
								throw new EJBServiceException("작업예약 ID 생성 Error");
							}
															
							sWbookId = StringHelper.evl(wBookSel.getFieldString("WBOOK_SELECT"), "");
							/*
							 *	작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 
							 *	작업예약조, 등록자, 등록일시) 한다.
							 */
				 		    sQueryId 	= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
							iSeq 		= ydWBookDAO.requestinsertData(sQueryId, 
																		new Object[]{ 
																           sWbookId, 
																           tmpYd_Gp.trim(), 
																           tmpBay_Gp.trim(), 
																           wSchCode, 
																		   YmCommonUtil.getWorkDuty(), 
																		   YmCommonUtil.getWorkParty()});
						}
						
						wBookidSchCall  = sWbookId;		
												 
						/*
						 *	적치단  Table Update(작업요구상태='S'로 변경)
						 *	UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'S' WHERE STOCK_ID = ?
						 */
						sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
						iSeq 	 = ydStackLayerDAO.requestupdateData(sQueryId, 
																	 new Object[]{ 
								       									YmCommonConst.STACK_LAYER_STAT_S, 
								       									wStockId});							
						
						/* 
						 *  저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
						 *  UPDATE TB_YM_STOCK 
						 *  SET WBOOK_ID= ?, 
						 *      STOCK_MOVE_TERM = ? 
						 *  WHERE STOCK_ID = ?
						 *  저장품이동조건	SD 대차상차대기,  SE 대차하차대기						  
						 */
						String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(wStockId,"");
								     
						sQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
						iSeq 	 = ydStockDAO.requestupdateData(sQueryId, 
															 new Object[]{ 
								    							sWbookId, 
								    							sStockInfo[1], 
								    							wStockId});		
						
						/*
				    	 * 	2009.07 YJK 생산통제 장입진행정보 기존모듈 삭제.
				    	 * 
							// 관제 압연 사양  Table 물류진행상태 Update (SWLI : W/B 보급, SCLI : CTC 보급)
							String selectWBookSchCode = "ym.steelinfo.steelinforecv.YdStockDAO.selectWBookSchCode";
							JDTORecord selectwbookschcode = ydStackLayerDAO.requestgetData(selectWBookSchCode, 
																						   new Object[]{ wBookidSchCall.trim() });
							
							if(selectwbookschcode != null){															   
								
								String sSchCode = StringHelper.evl(selectwbookschcode.getFieldString("SCH_WORK_KIND"), "");
								
								if (sSchCode.trim().equals(YmCommonConst.NEW_SCH_WORK_KIND_SWLI) ||
									sSchCode.trim().equals(YmCommonConst.NEW_SCH_WORK_KIND_SCLI)){
									
									ZZPC001 model = new ZZPC001();
									model.setTcCode("YMPC100");
									model.setrealStlNo(wStockId);
									model.seteventStat("30");
									model.seteventOccurDDTT(YmCommonUtil.getStringYMDHMS());
									EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
									Boolean isTrue = (Boolean)ejbConn.trx("sendInternalModel",new  Class[]{CommonModel.class},
																							  new Object[]{ model });							
								}
			                }
						 *
						 */
						/*
						 *	SLAB 작업예약 리스트를 작성한다.
						 */
						if (wBookidSchCall != null && !wBookidSchCall.trim().equals("")){
					    
							Glo_Sch_Call  += wBookidSchCall.trim() +"-" ;
							wBookidSchCall = "";
						}
					}						
				}
			}
			
			/*
			 *	4.12	대차도착했을때 상차 하차 구분에 따라 
			 *			STACK_LAYER_ACTIVE_STAT, 
			 *			STACK_LAYER_STAT 업데이트
		     */
			{
				logger.println(LogLevel.DEBUG,this, "상,하차 구분="+ sStackStat.trim());
				
				ymCommonDAO dao1 = ymCommonDAO.getInstance();
				
				int iMaxCount = Integer.parseInt(sStackMaxQnty);
				
                for(int inx = 0; inx < iMaxCount; inx++) {     
                	
                	dao1.modifyActiveStatOfLayer(YmCommonConst.STACK_LAYER_ACTIVE_STAT_O, 
                								 tcCurrStopLoc, 
                								 YmCommonConst.STACK_BED_GP_01, 
                								 "0"+ (inx + 1));
                }
                            
				/*
				 *	4.12.1	하차시 하차동MAP을 활성화.
				 */
				if (sStackStat.equals(YmCommonConst.STACK_STAT_U)){         
					
				/*
				 *	4.12.1	상차및 IDLE시 상차동MAP을 활성화 및 적치가능 상태로 셋팅.
				 */
				}else{                                 
					
					/*
					 * 상차도착시에 대차의 연속작업 횟수를 초기화한다.
					 */
					int iCnt = CraneSchDAO.updateEquipSaddleInfo(tcTcNo,"0");
					
					dao1.modifyStockStatOfLayer("", 
												YmCommonConst.STACK_LAYER_STAT_E, 
												tcCurrStopLoc);	   															 			  
												
				}
			}	
			/*
			 *	5.	SLAB 작업예약 리스트가 존재하면 스케쥴 호출
			 */
			if (Glo_Sch_Call != null && !Glo_Sch_Call.trim().equals("")){
		       /*
				*	5.1	대차 작업 진행 상태 변경
				*/
				String updateWprogStatQuery = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateWprogStat";
				int WprogStat               = ydStockDAO.requestupdateData(updateWprogStatQuery, new Object[]{ 
						                      YmCommonConst.WPROG_STAT_S, tcTcNo });				
				
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
				Boolean isTrue 		 = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",
															new  Class[]{String.class},
															new Object[]{Glo_Sch_Call.trim() });	
			
			}else{
			   /*
				*	5.2	대차 대기상태로 변경
				*/
				String updateWprogStatQuery = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateWprogStat";
				int WprogStat               = ydStockDAO.requestupdateData(updateWprogStatQuery, 
																		   new Object[]{YmCommonConst.WPROG_STAT_W, 
																		   				tcTcNo });								
			}
		
			logger.println(LogLevel.DEBUG,this,"End-receiveOutCCLdWrkOrd()");
					  
			return true;
			    
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 상차 후 하차출발시 해당동에 다음작업의 보조작업여부를 체크한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */               
	public boolean bookDummyWorkInfo_01(JDTORecord jDTORecord) { 
        
		logger.println(LogLevel.DEBUG,this,"Start-bookDummyWorkInfo_01()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		CraneSchDAO CraneSchDAO         = new CraneSchDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iResult = 0;
			int iReq 	= 0;
			
			String Glo_Sch_Call			= "";
						
			// 야드 Level-2에서 대차 도착 정보를  수신			
			/*
			 *	B열연 SLAB 대차번호(EX:TC01) : TC(2) + TC NO(2)
			 */
			String tcTcNo = StringHelper.evl(jDTORecord.getFieldString("대차번호"), "");   
				   tcTcNo = YmCommonConst.TC_2X + tcTcNo;   
			/*
			 *	B열연 SLAB 대차도착동(EX:2ATC11) : YARD구분(1) + 동구분(1)+ SPAN(2) + 열 NO(2)
			 */
			String tcCurrStopLoc = StringHelper.evl(jDTORecord.getFieldString("대차도착동"), ""); 
			
			/*
			 *	B열연 SLAB 대차도착동 야드구분
			 */
			String tmpYd_Gp  = tcCurrStopLoc.trim().substring(0, 1);
			
			/*
			 *	B열연 SLAB 대차도착동 동구분
			 */
			String tmpBay_Gp = tcCurrStopLoc.trim().substring(1, 2); 
			
			/*
			 *	B열연 SLAB 대차의 EQUIP 설정 정보를 가져온다.	
			 *
			 *	대차정보 고장 및 OFFLINE 체크
			 *	대차최대적치수량
			 */
			String sStackMaxQnty	= "";
			
			String tmpLoadGp        = "";
			String tmpLoadSch       = "";
			String tmpUnLoadSch     = "";
			String tmpLoadStopLoc   = "";
			
			JDTORecord equipJr = CraneSchDAO.getEquipInfoWithEquipGp(tcTcNo);
			
			if(equipJr != null){
			
				sStackMaxQnty	 = StringHelper.evl(equipJr.getFieldString("STACK_MAX_QNTY"), "0");
				tmpLoadGp        = StringHelper.evl(equipJr.getFieldString("CARLOAD_ASSIGN_YN"), "");        //상차지정유무
				tmpLoadSch       = StringHelper.evl(equipJr.getFieldString("CARLOAD_SCH_WORK_KIND"), "");    //상차 Schedule 작업종류
				tmpUnLoadSch     = StringHelper.evl(equipJr.getFieldString("CARUNLOAD_SCH_WORK_KIND"), "");  //하차 Schedule 작업종류
				tmpLoadStopLoc   = StringHelper.evl(equipJr.getFieldString("CARLOAD_STOP_LOC"), "");         //상차위치
			}
				
			/*
			 *	3.1	SLAB 대차의 상차작업이 지정일 경우.
			 */
			if (tmpLoadGp.equals(YmCommonConst.CARLOAD_ASSIGN_Y)) {       
				
				/*
				 *	3.1.0	SLAB 대차 작업인 경우 EQUIP TABLE STACK_MAX_QNTY 까지 처리한다.
				 */
					int iMaxCount = Integer.parseInt(sStackMaxQnty);
				
				/**
				 *	3.1.0	대차도착시에 상차작업예약 자동검색(W/B 보급)
				 */
					boolean isTrue = selectWBSearchLineInSlab(tmpLoadSch,
															  tmpUnLoadSch,
															  tmpBay_Gp,
															  tmpLoadStopLoc.substring(1,2),
															  iMaxCount,
															  tcCurrStopLoc);
				/* 
				 *	3.1.1	SLAB 대차의 상차 스케쥴 지정 작업예약을 검색
				 *			SLAB 인 경우는 여러건의 작업예약을 동시에 스케쥴 호출
				 */ 		
				String sLoadSchQueryId 	= "";
				List wBookid1 			= null;
				 
				if(YmCommonConst.NEW_SCH_WORK_KIND_STML.equals(tmpLoadSch)||
				   YmCommonConst.NEW_SCH_WORK_KIND_STM2.equals(tmpLoadSch)){ 	
				
					sLoadSchQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectLoadSch_01";
					wBookid1 = ydStockDAO.getListData(sLoadSchQueryId, new Object[]{tmpYd_Gp.trim(), 
														   			    		    tmpBay_Gp.trim(), 
														   			    			tmpLoadSch.trim(),
														   			    			"2_TC"+tcTcNo.substring(5, 6)+"_"}); //TO위치
														   			    			
				}else{
				
					sLoadSchQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectLoadSchNEW";
					wBookid1 = ydStockDAO.getListData(sLoadSchQueryId, new Object[]{tmpYd_Gp.trim(), 
														   			    		    tmpBay_Gp.trim(), 
														   			    			tmpLoadSch.trim(),
														   			    			tcTcNo});
					
				}
				
				JDTORecord TmpSelBedAddr       = null;
				int MaxRec                     = iMaxCount > wBookid1.size() ?  wBookid1.size() : iMaxCount;	
				String[] tmpWbookID            = new String[MaxRec];
				String[] tmpStackBed           = new String[MaxRec];
				
				/*
				 *	3.1.2	SLAB 작업예약 리스트를 작성한다.
				 */
				for (int ii=0; ii<MaxRec; ii++){
					TmpSelBedAddr		= (JDTORecord) wBookid1.get(ii);
					tmpWbookID[ii]  	= StringHelper.evl(TmpSelBedAddr.getFieldString("WBOOK_ID"), "");
					tmpStackBed[ii]  	= StringHelper.evl(TmpSelBedAddr.getFieldString("BED"), "");
					
					
					if(ii > 0){
						if(tmpStackBed[ii - 1].equals(tmpStackBed[ii])){
							/**
							 *	작업대상재정보를 가지고 보조작업대상재를 체크한다.
							 *  작업대상재가 같은 번지에 있을 경우 상단에 있는 정보를 가지고
							 *	보조작업대상재 처리를 한다.
							 *  즉,위,아래로 주작업대상재가 있을 경우 위의 대상재가 스케쥴실패시
							 *  아래의 스케쥴생성때 보조작업으로 잡히지 않도록 처리.
							 */
							logger.println(LogLevel.DEBUG,this,"아래,위 적치정보 U="+tmpStackBed[ii-1]+"="+tmpWbookID[ii-1]);
							logger.println(LogLevel.DEBUG,this,"아래,위 적치정보 P="+tmpStackBed[ii]+"="+tmpWbookID[ii]);
							continue;
						}
					}
				
					Glo_Sch_Call   += tmpWbookID[ii].trim() +"-" ;						
				}
				
				//SCH CALL
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
				Boolean boolTrue	 = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",
															new  Class[]{String.class},
															new Object[]{Glo_Sch_Call.trim() });	
			}
			
			logger.println(LogLevel.DEBUG,this,"WBOOK_ID="+Glo_Sch_Call);
			logger.println(LogLevel.DEBUG,this,"End-bookDummyWorkInfo_01()");
					  
			return true;
			    
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
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
	public boolean bookDummyWorkInfo_02(JDTORecord jDTORecord) { 
        
		logger.println(LogLevel.DEBUG,this,"Start-bookDummyWorkInfo_02()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		CraneSchDAO CraneSchDAO         = new CraneSchDAO();
		
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iResult = 0;
			int iReq 	= 0;
			
			String wBookidSchCall = "";
			String Glo_Sch_Call   = "";
			
			// 야드 Level-2에서 대차 도착 정보를  수신			
			/*
			 *	B열연 SLAB 대차번호(EX:TC01) : TC(2) + TC NO(2)
			 */
			String tcTcNo = StringHelper.evl(jDTORecord.getFieldString("대차번호"), "");   
				   tcTcNo = YmCommonConst.TC_2X + tcTcNo;   
			/*
			 *	B열연 SLAB 대차도착동(EX:2ATC11) : YARD구분(1) + 동구분(1)+ SPAN(2) + 열 NO(2)
			 */
			String tcCurrStopLoc = StringHelper.evl(jDTORecord.getFieldString("대차도착동"), ""); 
			
			/*
			 *	B열연 SLAB 대차도착동 야드구분
			 */
			String tmpYd_Gp  = tcCurrStopLoc.trim().substring(0, 1);
			
			/*
			 *	B열연 SLAB 대차도착동 동구분
			 */
			String tmpBay_Gp = tcCurrStopLoc.trim().substring(1, 2); 
			
			/*
			 *	B열연 SLAB 대차의 EQUIP 설정 정보를 가져온다.	
			 *
			 *	대차정보 고장 및 OFFLINE 체크
			 *	대차최대적치수량
			 */
			String sEquipStat 		= "";
			String sWorkMode		= "";
			String sStackStat		= "";
			String sStackMaxQnty	= "";
			String sCurrStopLoc  	= "";
			String sCurrStopBay  	= "";
			String sCtsRelayYn		= ""; // 크레인 작업횟수
			
			String tmpLoadGp        		= "";
			String tmpLoadSch       		= "";
			String tmpUnLoadGp      		= "";
			String tmpUnLoadSch     	= "";
			String tmpLoadStopLoc   	= "";
			String tmpUnLoadStopLoc 	= "";

			JDTORecord equipJr = CraneSchDAO.getEquipInfoWithEquipGp(tcTcNo);
			
			if(equipJr != null){
				sEquipStat 		= StringHelper.evl(equipJr.getFieldString("EQUIP_STAT"), "");
				sWorkMode  		= StringHelper.evl(equipJr.getFieldString("WORK_MODE"), "");
				sStackStat 		= StringHelper.evl(equipJr.getFieldString("STACK_STAT"), "");
				sStackMaxQnty	= StringHelper.evl(equipJr.getFieldString("STACK_MAX_QNTY"), "0");
				sCurrStopLoc  	= StringHelper.evl(equipJr.getFieldString("CURR_STOP_LOC"), "");
				sCtsRelayYn  		= StringHelper.evl(equipJr.getFieldString("CTS_RELAY_YN"), "");
				
				tmpLoadGp        = StringHelper.evl(equipJr.getFieldString("CARLOAD_ASSIGN_YN"), "");        //상차지정유무
				tmpLoadSch       = StringHelper.evl(equipJr.getFieldString("CARLOAD_SCH_WORK_KIND"), "");    //상차 Schedule 작업종류
				tmpUnLoadGp      = StringHelper.evl(equipJr.getFieldString("CARUNLOAD_ASSIGN_YN"), "");      //하차지정유무
				tmpUnLoadSch     = StringHelper.evl(equipJr.getFieldString("CARUNLOAD_SCH_WORK_KIND"), "");  //하차 Schedule 작업종류
				tmpLoadStopLoc   = StringHelper.evl(equipJr.getFieldString("CARLOAD_STOP_LOC"), "");         //상차위치
				tmpUnLoadStopLoc = StringHelper.evl(equipJr.getFieldString("CARUNLOAD_STOP_LOC"), "");       //하차위치

			}
			 
			if(YmCommonConst.WORK_MODE_C.equals(sEquipStat)||
			   YmCommonConst.WORK_MODE_C.equals(sWorkMode)){
				
				logger.println(LogLevel.DEBUG,this,"=대차정보 고장및 OFFLINE 처리="+sEquipStat+sWorkMode);
				return false;
			}
			
			/*
			 *	B열연 SLAB 대차의 EQUIP 설정 현재동.
			 */
			if(sCurrStopLoc.length() > 2){
				sCurrStopBay  = sCurrStopLoc.substring(1,2); // 1BTC03 -> B
			}
			if (tmpBay_Gp.trim().equals(sCurrStopBay)){
				logger.println(LogLevel.DEBUG,this,"대차도착=>대차 도착동이 현재 도착동과 같습니다.");
				//return false;
			}
			
			/**************************************************************
			 *	대차도착처리 예외사항 시작
			 *	=>	도착처리시	=>	하차상태('U')이고	=>	공대차이면(저장품정보 존재안함)
			 *	=>	상차상태('L')로 수정한다.
			 **************************************************************/
		   /*	
			* if (sStackStat.equals(YmCommonConst.STACK_STAT_U)){  
			* 	
			* 	String sYQeury1  = "ym.steelinfo.steelinforecv.dao.YdStockDAO.selectStock";
			* 	List stockL      = ydStockDAO.getListData(sYQeury1, new Object[]{ tcTcNo });					
			* 	
			* 	int iCount = 0;
			* 	JDTORecord cTcJr = CraneSchDAO.getTCLoadCount(tcCurrStopLoc); 
			* 	if(cTcJr != null){
			* 		iCount = cTcJr.getFieldInt("CNT");
			* 	}
			* 		
			* 	if(stockL.size() == 0 && 	
			* 	   iCount		 == 0 ){
			* 	   	
			* 	   	int iSeq = CraneSchDAO.updateEquipTcInfo(YmCommonConst.STACK_STAT_L,
			* 												 YmCommonConst.WPROG_STAT_W,
			* 												 tcTcNo); 
			* 		logger.println(LogLevel.DEBUG,this,"대차도착=>대차도착처리 예외사항.");   	
			* 		logger.println(LogLevel.DEBUG,this,"대차도착=>상차대기상태로 수정.");   	
			* 	}
			* }
			*/
			/**************************************************************
			 *	대차도착처리 예외사항 종료
			 **************************************************************/
			 
			/**
			 *	도착처리시 다른동 적치단 활성상태를 모두 비활성화 한다.
			 */
		   /*	
			* String tmpTcNo = "2_TC"+tcCurrStopLoc.substring(4,5)+"_";
			* 											  	
			* iReq = CraneSchDAO.updateStackLayerStatInfo(tmpTcNo);
			* 
			* logger.println(LogLevel.DEBUG,this,"대차위치정보 초기화="+tmpTcNo+"="+iReq);
		    */
			/*
			 *	1.1	적재상태가 'L' (상차) 일때
			 */
			if (sStackStat.equals(YmCommonConst.STACK_STAT_L)){         
				//SKIP
			/*
			 *	1.2	적재상태가 'U' (하차) 일때
			 */	 
			}else if (sStackStat.equals(YmCommonConst.STACK_STAT_U)){   //하차
			   /*	
				* if (!tmpBay_Gp.trim().equals(tmpUnLoadStopLoc.trim().substring(1, 2))){
				* 	throw new EJBServiceException("대차도착 에러 : 대차하차 도착동이 목적동이 아닙니다.");
				* }											
				*/
			}
			
		   /*
			*	2	SLAB 대차의 현재위치를 변경한다.
			*/
		   /*	
			* String updateCurrQueryId = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateLoadCurrLoc";
			* int CurrStop = ydStackLayerDAO.requestupdateData(updateCurrQueryId, 
			* 												 new Object[]{ tcCurrStopLoc, 
			* 												 			   tcTcNo });										
			*/
		    
		    /*
		     *	3	적재상태가 'U'(하차) 일때 
		     */
			{         
				
				/*
				 *	3.1	SLAB 대차의 상차작업이 지정일 경우.
				 */
				if (tmpLoadGp.equals(YmCommonConst.CARLOAD_ASSIGN_Y)) {       
					
					/*
					 *	3.1.0	SLAB 대차 작업인 경우 EQUIP TABLE STACK_MAX_QNTY 까지 처리한다.
					 */
						int iMaxCount = Integer.parseInt(sStackMaxQnty);
					
					/**
					 *	3.1.0	대차도착시에 상차작업예약 자동검색(W/B 보급)
					 */
						boolean isTrue = selectWBSearchLineInSlab(tmpLoadSch,
																  tmpUnLoadSch,
																  tmpBay_Gp,
																  tmpLoadStopLoc.substring(1,2),
																  iMaxCount,
																  tcCurrStopLoc);
						
					/* 
					 *	3.1.1	SLAB 대차의 상차 스케쥴 지정 작업예약을 검색
					 *			SLAB 인 경우는 여러건의 작업예약을 동시에 스케쥴 호출
					 *			동간이적인 경우는 상차위치가 현 대차인지 체크한다.
					 */ 		
					String sLoadSchQueryId 	= "";
					List wBookid1 			= null;
					 
					if(YmCommonConst.NEW_SCH_WORK_KIND_STML.equals(tmpLoadSch)||
					   YmCommonConst.NEW_SCH_WORK_KIND_STM2.equals(tmpLoadSch)){ 	 
					
						sLoadSchQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectLoadSch_01";
						wBookid1 = ydStockDAO.getListData(sLoadSchQueryId, new Object[]{tmpYd_Gp.trim(), 
															   			    		 tmpBay_Gp.trim(), 
															   			    		 tmpLoadSch.trim(),
															   			    		 "2_TC"+tcTcNo.substring(5, 6)+"_"}); //TO위치
					}else{
					
						sLoadSchQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectLoadSchNEW";
						wBookid1 = ydStockDAO.getListData(sLoadSchQueryId, new Object[]{tmpYd_Gp.trim(), 
															   			    		    tmpBay_Gp.trim(), 
															   			    			tmpLoadSch.trim(),
															   			    			tcTcNo});
						
					}
					
					JDTORecord TmpSelBedAddr       = null;
					int MaxRec                     = wBookid1.size();	
					String[] tmpWbookID            = new String[MaxRec];
					
					
					/*
					 *	3.1.2	SLAB 작업예약 리스트를 작성한다.
					 */
					
					{
						/*
						 *	대차 제약조건 1
						 *	크레인 작업횟수 1회, 적치가능매수 2매인 경우만 적용
						 *	2매의 번지정보가 틀릴경우 적치가능매수를 1로 변경
						 *    최소 2매이상이 되어야 비교가 가능하기 때문에 'MaxRec>1' 추가
						 */
						if("1".equals(sCtsRelayYn)&&"2".equals(sStackMaxQnty)&&MaxRec>1){
							
							String[] jkWbookID            = new String[MaxRec];
							String[] jkLayer            	   = new String[MaxRec];
							
							for (int ii=0; ii<MaxRec; ii++){
								if (ii < iMaxCount){
									TmpSelBedAddr = (JDTORecord) wBookid1.get(ii);
									jkLayer[ii]  	= StringHelper.evl(TmpSelBedAddr.getFieldString("BED"), "");
								}
							}
							if(jkLayer[0] != null &&
							   jkLayer[1] != null &&  	
							   !jkLayer[0].equals(jkLayer[1]))
							{
								iMaxCount 		= 1;
								sStackMaxQnty 	= "1";
								logger.println(LogLevel.DEBUG,this,"============YJK===isCraneWork===>"+ jkLayer[0]+"/"+ jkLayer[1]+"/");
							}   
						}
					}
					
					for (int ii=0; ii<MaxRec; ii++){
						if (ii < iMaxCount){
							TmpSelBedAddr = (JDTORecord) wBookid1.get(ii);
							tmpWbookID[ii]  = StringHelper.evl(TmpSelBedAddr.getFieldString("WBOOK_ID"), "");
							Glo_Sch_Call   += tmpWbookID[ii].trim() +"-" ;							
						}
					}
					
				/*
				 *	3.2	SLAB 대차의 상차작업이 미지정일 경우.
				 */	
				}else if (tmpLoadGp.equals(YmCommonConst.CARLOAD_ASSIGN_N)) { 
					/* 
					 *	3.2.1	동간이적상차(STML,STM2) 작업예약을 검색.
					 */
					if ("".equals(tmpLoadSch.trim())){

						String sLoadSchQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectLoadSch_02";
						List wBookid1 = ydStockDAO.getListData(sLoadSchQueryId, 
															 new Object[]{tmpYd_Gp.trim(), 
													   		    	      		tmpBay_Gp.trim(), 
													   			    	  	YmCommonConst.NEW_SCH_WORK_KIND_STML,
													   			    	  	YmCommonConst.NEW_SCH_WORK_KIND_STM2,
													   			    	  	"2_TC"+tcTcNo.substring(5, 6)+"_"}); //TO위치
														   			    			
						
						JDTORecord TmpSelBedAddr       = null;
						int MaxRec                     = wBookid1.size();	
						String[] tmpWbookID            = new String[MaxRec];
						
						/*
						 *	3.2.2	SLAB 대차 작업인 경우 EQUIP TABLE STACK_MAX_QNTY 까지 처리한다.
						 */
							int iMaxCount = Integer.parseInt(sStackMaxQnty);
						/*
						 *	3.2.3	SLAB 작업예약 리스트를 작성한다.
						 */
						
						{
							/*
							 *	대차 제약조건 1
							 *	크레인 작업횟수 1회, 적치가능매수 2매인 경우만 적용
							 *	2매의 번지정보가 틀릴경우 적치가능매수를 1로 변경
							 *    최소 2매이상이 되어야 비교가 가능하기 때문에 'MaxRec>1' 추가
							 */
							if("1".equals(sCtsRelayYn)&&"2".equals(sStackMaxQnty)&&MaxRec>1){
								
								String[] jkWbookID            = new String[MaxRec];
								String[] jkLayer            	   = new String[MaxRec];
								
								for (int ii=0; ii<MaxRec; ii++){
									if (ii < iMaxCount){
										TmpSelBedAddr = (JDTORecord) wBookid1.get(ii);
										jkLayer[ii]  	= StringHelper.evl(TmpSelBedAddr.getFieldString("BED"), "");
									}
								}
								if(jkLayer[0] != null &&
								   jkLayer[1] != null &&  	
								   !jkLayer[0].equals(jkLayer[1]))
								{
									iMaxCount 		= 1;
									sStackMaxQnty 	= "1";
									logger.println(LogLevel.DEBUG,this,"============YJK===isCraneWork===>"+ jkLayer[0]+"/"+ jkLayer[1]+"/");
								}   
							}
						}
						 
						for (int ii=0; ii<MaxRec; ii++){
							if (ii < iMaxCount){
								TmpSelBedAddr	= (JDTORecord) wBookid1.get(ii);
								tmpWbookID[ii]  = StringHelper.evl(TmpSelBedAddr.getFieldString("WBOOK_ID"), "");
								Glo_Sch_Call   += tmpWbookID[ii].trim() +"-" ;							
							}
						}			
					}
				}
				
			}
			
			
			/*
			 *	4.12	대차도착했을때 상차 하차 구분에 따라 
			 *			STACK_LAYER_ACTIVE_STAT, 
			 *			STACK_LAYER_STAT 업데이트
		     */
			{
				logger.println(LogLevel.DEBUG,this, "상,하차 구분="+ sStackStat.trim());
				
				ymCommonDAO dao1 = ymCommonDAO.getInstance();
				
				int iMaxCount = Integer.parseInt(sStackMaxQnty);
				
		              for(int inx = 0; inx < iMaxCount; inx++) {     
		                	
		                	dao1.modifyActiveStatOfLayer(YmCommonConst.STACK_LAYER_ACTIVE_STAT_O, 
		                								 tcCurrStopLoc, 
		                								 YmCommonConst.STACK_BED_GP_01, 
		                								 "0"+ (inx + 1));
		              }
                            
				/*
				 *	4.12.1	상차및 IDLE시 상차동MAP을 활성화 및 적치가능 상태로 셋팅.
				 */
				{
					/*
					 * 상차도착시에 대차의 연속작업 횟수를 초기화한다.
					 */
					int iCnt = CraneSchDAO.updateEquipSaddleInfo(tcTcNo,"0");
					
					dao1.modifyStockStatOfLayer("", 
											    YmCommonConst.STACK_LAYER_STAT_E, 
											    tcCurrStopLoc);	   															 			  
				}
			}	
			/*
			 *	5.	SLAB 작업예약 리스트가 존재하면 스케쥴 호출
			 */
			if (Glo_Sch_Call != null && !Glo_Sch_Call.trim().equals("")){
		       /*
				*	5.1	대차 작업 진행 상태 변경
				*/
				String updateWprogStatQuery = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateWprogStat";
				int WprogStat               = ydStockDAO.requestupdateData(updateWprogStatQuery, new Object[]{ 
						                      YmCommonConst.WPROG_STAT_S, tcTcNo });				
				
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
				Boolean isTrue 		 = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",
															new  Class[]{String.class},
															new Object[]{Glo_Sch_Call.trim() });	
			}else{
			   /*
				*	5.2	대차 대기상태로 변경
				*/
				String updateWprogStatQuery = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateWprogStat";
				int WprogStat               = ydStockDAO.requestupdateData(updateWprogStatQuery, 
																		   new Object[]{YmCommonConst.WPROG_STAT_W, 
																		   				tcTcNo });								
			}
		
			logger.println(LogLevel.DEBUG,this,"End-bookDummyWorkInfo_02()");
					  
			return true;
			    
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * SLAB D동 2대 대차도착처리 모듈
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public boolean bookDummyWorkInfo_03(JDTORecord jDTORecord) { 
        
		logger.println(LogLevel.DEBUG,this,"Start-bookDummyWorkInfo_03()");
		
		YdStockDAO ydStockDAO 	= new YdStockDAO();
		CraneSchDAO dao         = new CraneSchDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iResult = 0;
			int iReq 	= 0;
			
			String wBookidSchCall = "";
			String Glo_Sch_Call   = "";
			
			// 야드 Level-2에서 대차 도착 정보를  수신			
			/*
			 *	B열연 SLAB 대차번호(EX:TC01) : TC(2) + TC NO(2)
			 */
			String tcTcNo = StringHelper.evl(jDTORecord.getFieldString("대차번호"), "");   
				   tcTcNo = YmCommonConst.TC_2X + tcTcNo;   
			/*
			 *	B열연 SLAB 대차도착동(EX:2ATC11) : YARD구분(1) + 동구분(1)+ SPAN(2) + 열 NO(2)
			 */
			String tcCurrStopLoc = StringHelper.evl(jDTORecord.getFieldString("대차도착동"), ""); 
			
			/*
			 *	B열연 SLAB 대차도착동 야드구분
			 */
			String tmpYd_Gp  = tcCurrStopLoc.trim().substring(0, 1);
			
			/*
			 *	B열연 SLAB 대차도착동 동구분
			 */
			String tmpBay_Gp = tcCurrStopLoc.trim().substring(1, 2); 
			
			/*
			 *	B열연 SLAB 2번 대차의 EQUIP 설정 정보를 가져온다.	
			 */
			String s2EquipStat 		= "";
			String s2WorkMode		= "";
			String s2StackStat		= "";
			String s2StackMaxQnty	= "";
			String s2CurrStopLoc  	= "";
			
			String s2LoadGp        = "";
			String s2LoadSch       = "";
			String s2UnLoadGp      = "";
			String s2UnLoadSch     = "";
			String s2LoadStopLoc   = "";
			String s2UnLoadStopLoc = "";

			JDTORecord equipJr2 = dao.getEquipInfoWithEquipGp(YmCommonConst.EQUIP_GP_2XTC02);
			
			if(equipJr2 != null){
				s2EquipStat 	= StringHelper.evl(equipJr2.getFieldString("EQUIP_STAT"), "");
				s2WorkMode  	= StringHelper.evl(equipJr2.getFieldString("WORK_MODE"), "");
				s2StackStat 	= StringHelper.evl(equipJr2.getFieldString("STACK_STAT"), "");
				s2StackMaxQnty	= StringHelper.evl(equipJr2.getFieldString("STACK_MAX_QNTY"), "0");
				s2CurrStopLoc  	= StringHelper.evl(equipJr2.getFieldString("CURR_STOP_LOC"), "");
				s2LoadGp        = StringHelper.evl(equipJr2.getFieldString("CARLOAD_ASSIGN_YN"), "");        //상차지정유무
				s2LoadSch       = StringHelper.evl(equipJr2.getFieldString("CARLOAD_SCH_WORK_KIND"), "");    //상차 Schedule 작업종류
				s2UnLoadGp      = StringHelper.evl(equipJr2.getFieldString("CARUNLOAD_ASSIGN_YN"), "");      //하차지정유무
				s2UnLoadSch     = StringHelper.evl(equipJr2.getFieldString("CARUNLOAD_SCH_WORK_KIND"), "");  //하차 Schedule 작업종류
				s2LoadStopLoc   = StringHelper.evl(equipJr2.getFieldString("CARLOAD_STOP_LOC"), "");         //상차위치
				s2UnLoadStopLoc = StringHelper.evl(equipJr2.getFieldString("CARUNLOAD_STOP_LOC"), "");       //하차위치
			}
			
			/*
			 *	B열연 SLAB 3번 대차의 EQUIP 설정 정보를 가져온다.	
			 */
			String s3EquipStat 		= "";
			String s3WorkMode		= "";
			String s3StackStat		= "";
			String s3StackMaxQnty	= "";
			String s3CurrStopLoc  	= "";
			
			String s3LoadGp        = "";
			String s3LoadSch       = "";
			String s3UnLoadGp      = "";
			String s3UnLoadSch     = "";
			String s3LoadStopLoc   = "";
			String s3UnLoadStopLoc = "";

			JDTORecord equipJr3 = dao.getEquipInfoWithEquipGp(YmCommonConst.EQUIP_GP_2XTC03);
			
			if(equipJr2 != null){
				s3EquipStat 	= StringHelper.evl(equipJr3.getFieldString("EQUIP_STAT"), "");
				s3WorkMode  	= StringHelper.evl(equipJr3.getFieldString("WORK_MODE"), "");
				s3StackStat 	= StringHelper.evl(equipJr3.getFieldString("STACK_STAT"), "");
				s3StackMaxQnty	= StringHelper.evl(equipJr3.getFieldString("STACK_MAX_QNTY"), "0");
				s3CurrStopLoc  	= StringHelper.evl(equipJr3.getFieldString("CURR_STOP_LOC"), "");
				s3LoadGp        = StringHelper.evl(equipJr3.getFieldString("CARLOAD_ASSIGN_YN"), "");        //상차지정유무
				s3LoadSch       = StringHelper.evl(equipJr3.getFieldString("CARLOAD_SCH_WORK_KIND"), "");    //상차 Schedule 작업종류
				s3UnLoadGp      = StringHelper.evl(equipJr3.getFieldString("CARUNLOAD_ASSIGN_YN"), "");      //하차지정유무
				s3UnLoadSch     = StringHelper.evl(equipJr3.getFieldString("CARUNLOAD_SCH_WORK_KIND"), "");  //하차 Schedule 작업종류
				s3LoadStopLoc   = StringHelper.evl(equipJr3.getFieldString("CARLOAD_STOP_LOC"), "");         //상차위치
				s3UnLoadStopLoc = StringHelper.evl(equipJr3.getFieldString("CARUNLOAD_STOP_LOC"), "");       //하차위치
			}
			
			/*
			 *	1.	실물대차가 D동에 진짜 도착할 경우 처리.
			 */
				if(!YmCommonConst.BAY_GP_D.equals(tmpBay_Gp)){
					logger.println(LogLevel.DEBUG,this,"=SLAB D동 2대차도착처리=>D동이 아님="+tmpBay_Gp);
					return false;
				}
			/*
			 *	2.	2대의 대차설정이 모두 동일할 경우 처리.
			 *
			 *		대차 상태 			:
			 *		대차 상차지정유무	:	Y
			 *		대차 상차스케쥴 	:	STSL 	
			 *		대차 상차동 		:	D동
			 *		대차 하차지정유무	:	Y
			 *		대차 하차스케쥴 	:	SWLI 	
			 *		대차 하차동			:	C
			 */	    
				
				if(YmCommonConst.WORK_MODE_C.equals(s2EquipStat)||
				   YmCommonConst.WORK_MODE_C.equals(s2WorkMode)){
					logger.println(LogLevel.DEBUG,this,"=SLAB D동 2대차도착처리=> 2번 대차가 고장상태");
					return false;
				}
				
				if(YmCommonConst.WORK_MODE_C.equals(s3EquipStat)||
				   YmCommonConst.WORK_MODE_C.equals(s3WorkMode)){
					logger.println(LogLevel.DEBUG,this,"=SLAB D동 2대차도착처리=> 3번 대차가 고장상태");
					return false;
				}
				
				if(!YmCommonConst.CARLOAD_ASSIGN_Y.equals(s2LoadGp)){
					logger.println(LogLevel.DEBUG,this,"=SLAB D동 2대차도착처리=> 2번 대차 상차지정유무 ="+s2LoadGp);
					return false;
				}
				
				if(!YmCommonConst.CARLOAD_ASSIGN_Y.equals(s2UnLoadGp)){
					logger.println(LogLevel.DEBUG,this,"=SLAB D동 2대차도착처리=> 2번 대차 상차지정유무 ="+s2LoadGp);
					return false;
				}
				
				if(!YmCommonConst.CARLOAD_ASSIGN_Y.equals(s3LoadGp)){
					logger.println(LogLevel.DEBUG,this,"=SLAB D동 2대차도착처리=> 3번 대차 상차지정유무 ="+s3LoadGp);
					return false;
				}
				
				if(!YmCommonConst.CARLOAD_ASSIGN_Y.equals(s3UnLoadGp)){
					logger.println(LogLevel.DEBUG,this,"=SLAB D동 2대차도착처리=> 3번 대차 상차지정유무 ="+s3LoadGp);
					return false;
				}
				
				if(YmCommonConst.NEW_SCH_WORK_KIND_STSL.equals(s2LoadSch)&&
	    	   	   YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(s2UnLoadSch)&&
	    	   	   s2LoadSch.equals(s3LoadSch)&&
		    	   s2UnLoadSch.equals(s3UnLoadSch)){
		    	}else{
		    		logger.println(LogLevel.DEBUG,this,"=SLAB D동 2대차도착처리=> 2,3번 대차 상하차 스케쥴 다름="+s2LoadSch+"/"+s2UnLoadSch+"/"+s3LoadSch+"/"+s3UnLoadSch);
		    		return false;    
		    	}
		    	
		    	if(s2LoadStopLoc.substring(1, 2).equals(s3LoadStopLoc.substring(1, 2))&&
		    	   s2UnLoadStopLoc.substring(1, 2).equals(s3UnLoadStopLoc.substring(1, 2))){
		    	}else{
		    		logger.println(LogLevel.DEBUG,this,"=SLAB D동 2대차도착처리=> 2,3번 대차 상하차동 다름="+s2LoadStopLoc+"/"+s2UnLoadStopLoc+"/"+s3LoadStopLoc+"/"+s3UnLoadStopLoc);
		    		return false;    
		    	}
		    
		    /*
			 *	3.	대차도착시	현재 크레인 작업지시 스케쥴 정보를 가져온다.
			 */		
				List schL = dao.getCraneSchList(YmCommonConst.YD_GP_2,
												YmCommonConst.BAY_GP_D,
												YmCommonConst.NEW_SCH_WORK_KIND_STSL);
			/*
			 *	4.	현재 크레인 작업지시 스케쥴 정보중 
			 *		TO위치가 현 도착대차가 아니고,
			 *		다른대차가 아직 실제 도착처리가 되어있지 않을 경우.
			 */		
			 	boolean isWork		= false;
			 	JDTORecord schJr 	= null;	  	 	
			 	String sSchPutLoc 	= "";
				if(schL.size() > 0){
					schJr = (JDTORecord) schL.get(0);
					sSchPutLoc = StringHelper.evl(schJr.getFieldString("CRANE_WORD_PUT_LOC"), "").substring(0, 6);
				}else{
		    		logger.println(LogLevel.DEBUG,this,"=SLAB D동 2대차도착처리=> 스케쥴 정보 존재안함.");
		    		return false;    
		    	}
				
				logger.println(LogLevel.DEBUG,this,"=SLAB D동 2대차도착처리=>"+tcCurrStopLoc+"=");
				logger.println(LogLevel.DEBUG,this,"=SLAB D동 2대차도착처리=>"+sSchPutLoc+"=");
				/*
				 *	4.1	현재도착동과 가장빠른 스케쥴 PUT LOC을 비교한다.
				 */
				if(!tcCurrStopLoc.equals(sSchPutLoc)){
					isWork = true;
				}else{
					logger.println(LogLevel.DEBUG,this,"=SLAB D동 2대차도착처리=>다음작업 스케쥴 PUT위치는 현재 대차위치");
		    		return false;    
				}
				
				/*
				 *	4.2	다른 대차정보를 확인한다.
				 *		대차의 STACK_STAT = 'L' 상차
				 *		대차의 현재동이 D동 
				 */
				if(YmCommonConst.EQUIP_GP_2XTC02.equals(tcTcNo)){
					if(YmCommonConst.STACK_STAT_L.equals(s3StackStat)&&
					   YmCommonConst.BAY_GP_D.equals(s3CurrStopLoc.substring(1, 2))){
					   	logger.println(LogLevel.DEBUG,this,"=SLAB D동 2대차도착처리=>03대차 이미 도착처리되어 있슴.");
		    			return false;    
					}else{
						isWork = true;
					}
				}
				
				if(YmCommonConst.EQUIP_GP_2XTC03.equals(tcTcNo)){
					if(YmCommonConst.STACK_STAT_L.equals(s2StackStat)&&
					   YmCommonConst.BAY_GP_D.equals(s2CurrStopLoc.substring(1, 2))){
					   	logger.println(LogLevel.DEBUG,this,"=SLAB D동 2대차도착처리=>02대차 이미 도착처리되어 있슴.");
		    			return false;    
					}else{
						isWork = true;
					}
				}
			/*
			 *	5.	스케쥴 정보가 스케쥴등록,UP지시 상태이면 스케쥴 취소.
			 *		스케쥴 정보가 UP실적,PUT지시 상태이면 수정작업지시.
			 *					  수정작업지시처리시 GRIP체크한다.
			 */							 
				
				String sSchId 		= "";
				String sSchWorkStat = "";
				String sLotYn		= "";
				String sPutLoc      = "";
				
				boolean isPut		= false;
				boolean isGrip		= false;
				
				EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneSchReg",this);
				EJBConnector ejbConn2 = new EJBConnector("default","JNDICWrkOrdReg",this);
				
				for(int index = 0; index < schL.size(); index++)
				{
					schJr  		= (JDTORecord)schL.get(index);
					sSchId 		= StringHelper.evl(schJr.getFieldString("SCH_ID"),"");
					sSchWorkStat= StringHelper.evl(schJr.getFieldString("SCH_WORK_STAT"),"");
					sLotYn		= StringHelper.evl(schJr.getFieldString("SCH_WORK_GRIP_LOT_YN"),"");
					
					
					if(YmCommonConst.SCH_WORK_STAT_S.equals(sSchWorkStat)||
    			       YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)){
	    			    
	    			    Boolean isTemp  = (Boolean)ejbConn1.trx("cancelSlabSchInfo",
													new  Class[]{String.class},
													new Object[]{sSchId});
					}else{
						/**
						 *	PUT지시상태인지를 체크한다.
						 *	PUT지시상태이면 수정작업지시에서 처리하기 때문에
						 *	대차도착처리를 하지 않는다.
						 */
						isPut = true;
						
						if(YmCommonConst.GRIP_LOT_YN_G.equals(sLotYn)){
							isGrip = true;
							continue;
						}
						
						if(isGrip){
							sPutLoc = tcCurrStopLoc 				+
									  YmCommonConst.STACK_BED_GP_01 +	
							          YmCommonConst.STACK_LAYER_GP_02;	
						}else{
							sPutLoc = tcCurrStopLoc 				+
									  YmCommonConst.STACK_BED_GP_01 +	
							          YmCommonConst.STACK_LAYER_GP_01;	
						}
						
			    		Boolean isTemp  = (Boolean)ejbConn2.trx("callBSlabCraneMsgInfo",
			    												new  Class[]{String.class,
			    												             String.class,
																 	  		 String.class},
															    new Object[]{sSchId,
															                 "",
																	  		 sPutLoc});
					}											  		 
				}		
																			  		
			/*
			 *	6.	2대의 대차도착처리를 다시한다.
			 */ 
				if(YmCommonConst.EQUIP_GP_2XTC02.equals(tcTcNo)){
					
					if(!isPut){ //PUT지시상태가 아닐경우에
						
						/*
						 *	6.1.1	02번대차 도착처리를 다시한다.
						 */ 
						JDTORecord dataJr1 = JDTORecordFactory.getInstance().create();
						dataJr1.setField("대차번호"	 	,"TC02");
						dataJr1.setField("대차도착동" 	,s2LoadStopLoc);
						boolean isBool1 = bookDummyWorkInfo_02(dataJr1);
					}
					{
						/*
						 *	6.1.2	03번대차 도착처리를 다시한다.
						 */ 
						JDTORecord dataJr2 = JDTORecordFactory.getInstance().create();
						dataJr2.setField("대차번호"	 	,"TC03");
						dataJr2.setField("대차도착동" 	,s3LoadStopLoc);
						boolean isBool2 = bookDummyWorkInfo_02(dataJr2);
						
						/*
						 *	6.1.3	03번대차 도착위치를 CLOSE한다(현재 대차위치가 아니기 때문에)
						 */ 
						int iReq2 		= dao.updateStackLayerStatInfo(s3LoadStopLoc);
					}
				}else{
					
					if(!isPut){ //PUT지시상태가 아닐경우에
						/*
						 *	6.2.1	03번대차 도착처리를 다시한다.
						 */ 
						JDTORecord dataJr1 = JDTORecordFactory.getInstance().create();
						dataJr1.setField("대차번호"	 	,"TC03");
						dataJr1.setField("대차도착동" 	,s3LoadStopLoc);
						boolean isBool1 = bookDummyWorkInfo_02(dataJr1);
					}
					{
						/*
						 *	6.1.2	02번대차 도착처리를 다시한다.
						 */ 
						JDTORecord dataJr2 = JDTORecordFactory.getInstance().create();
						dataJr2.setField("대차번호"	 	,"TC02");
						dataJr2.setField("대차도착동" 	,s2LoadStopLoc);
						boolean isBool2 = bookDummyWorkInfo_02(dataJr2);
						
						/*
						 *	6.1.3	02번대차 도착위치를 CLOSE한다(현재 대차위치가 아니기 때문에)
						 */ 
						int iReq2 		= dao.updateStackLayerStatInfo(s2LoadStopLoc);
					}	
				}
			/*
			 *	7.	PUT지시 상태일 경우 PUT지시 SLAB정보를 도착한 대차에 TO위치로 셋팅한 후 
			 *		나머지 스케쥴 정보는 5번,6번 처리를 한다.
			 */ 
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }   
	    logger.println(LogLevel.DEBUG,this,"End-bookDummyWorkInfo_03()");
		return true;
	}
	
	/**
	 *	상차스케쥴(동간보급상차),하차스케쥴(W/B 보급),현재동(D,E)이면
	 *		=>	해당동에 MIN(장입예정) 저장품 정보를 검색한다.
	 *		=>	저장품작업예약 유무를 검색한다.
	 *			=>	작업예약존재하면 SKIP
	 *			=>	작업예약없으면 상차스케쥴로 작업예약 생성
	 *
	 *	@return
	 */
	private boolean selectWBSearchLineInSlab(String sUSchCode,
											 String sDSchCode,
											 String sCurBay,
											 String sUpBay,
											 int iMaxCount,
											 String sCurrStopLoc){
		
		boolean isSuccess = true;
		
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
			
	    	logger.println(LogLevel.DEBUG,this, "상차스케쥴	="+sUSchCode);	
	    	logger.println(LogLevel.DEBUG,this, "하차스케쥴	="+sDSchCode);	
	    	logger.println(LogLevel.DEBUG,this, "현재동	="+sCurBay);	
	    	logger.println(LogLevel.DEBUG,this, "상차동	="+sUpBay);	
	    	
	    	if(YmCommonConst.NEW_SCH_WORK_KIND_STSL.equals(sUSchCode)&&
	    	   YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(sDSchCode)&&
	    	   sUpBay.equals(sCurBay)){
	    	}else{
	    		logger.println(LogLevel.DEBUG,this, "자동 작업예약 생성 처리 하지 않음.");	
				return false;    
	    	}
	    	
	    	/* 
			 * 동간보급 작업예약 COUNT를 체크한다.	
			    SELECT  WBOOK_ID
				FROM    TB_YM_WBOOK A
				WHERE   YD_GP 			= :YD_GP
				AND     BAY_GP 			= :BAY_GP
				AND     SCH_WORK_KIND 	= :SCH_CODE
				AND     NOT EXISTS (SELECT WBOOK_ID FROM TB_YM_SCH WHERE WBOOK_ID = A.WBOOK_ID)
			 */
			String sQuery0 	= "ym.steelinfo.steelinforecv.YdStockDAO.selectWBSlabSearch_05";
			List wbL0  		= ydStockDAO.getListData(sQuery0, new Object[]{"2",
																		   sCurBay, 
																		   sUSchCode});
			
			/* 
			 * 동간보급 작업예약이 존재하면 현재동으로 작업예약 CRANE_WORD_PUT_LOC 항목을 셋팅.	
			    UPDATE  TB_YM_WBOOK A 
			    SET 	CRANE_WORD_PUT_LOC  = :PUT_LOC
				WHERE   YD_GP 				= :YD_GP
				AND     BAY_GP 				= :BAY_GP
				AND     SCH_WORK_KIND 		= :SCH_CODE
				AND     NOT EXISTS (SELECT WBOOK_ID FROM TB_YM_SCH WHERE WBOOK_ID = A.WBOOK_ID)
			 */
			if (wbL0.size()	>= 0){
				
				String tQuery = "ym.steelinfo.steelinforecv.YdStockDAO.updateWBSlabSearch_05";
				int    tSeq   = ydStockDAO.requestupdateData(tQuery, 
															  new Object[]{sCurrStopLoc, 
															  			   "2",
																		   sCurBay, 
																		   sUSchCode});		
			}	
												  			   
			if (wbL0.size()	>= iMaxCount){
				logger.println(LogLevel.DEBUG,this, "SLAB 장입 대상재 작업예약 존재함.");	
				return false;    	
			}						
													   
	    	/* 
			 *	조건 => 현재동에서 가장빠른 장입대상순번이고,
			 			야드에 적치중인 SLAB ,
			 			작업예약이 존재하지 않는 SLAB
				SELECT  STOCK.STOCK_ID,
				        STOCK.CHARGE_LOT_NO,
				        LAYER.STACK_COL_GP,
				        LAYER.STACK_BED_GP,
				        LAYER.STACK_LAYER_GP
				FROM    TB_YM_STOCK         STOCK,
				        TB_YM_STACKLAYER    LAYER
				WHERE   STOCK.CHARGE_LOT_NO = 
				        (
				        SELECT  MIN(CHARGE_LOT_NO)
				        FROM    TB_YM_STOCK A,
				                TB_YM_STACKLAYER B
				        WHERE   A.CHARGE_LOT_NO IS NOT NULL
				        AND     B.STACK_COL_GP LIKE '2'||:BAY||'0%'
				        AND     A.STOCK_ID = B.STOCK_ID
				        AND     NOT EXISTS (SELECT WBOOK_ID FROM TB_YM_SCH WHERE WBOOK_ID = A.WBOOK_ID)
				        )
				AND     STOCK.WBOOK_ID 		    IS NULL
				AND     STOCK.STOCK_ID 			= LAYER.STOCK_ID
				AND     LAYER.STACK_COL_GP 		LIKE '2'||:BAY||'0%'
				AND     LAYER.STACK_LAYER_STAT  IN ('L','S','U')
				ORDER BY STACK_COL_GP, 
						 STACK_BED_GP, 
						 STACK_LAYER_GP DESC
			*/
			String sQuery1 	= "ym.steelinfo.steelinforecv.YdStockDAO.selectWBSlabSearch_04";
			List wbL   		= ydStockDAO.getListData(sQuery1, new Object[]{sCurBay,
																		   sCurBay });
	    	
	    	if (wbL 		== null	||
	    		wbL.size()	==0){
				logger.println(LogLevel.DEBUG,this, "SLAB 장입 대상재 존재 안함.");	
				return false;    	
			}	
			
			int MaxRec    = wbL.size();	
			
			//최대 대차설비 MAX COUNT 매만 생성한다.
			if(MaxRec > iMaxCount) MaxRec = iMaxCount;
			
			JDTORecord stockJr  = null;
			JDTORecord wbookJr 	= null;
			
			String sQuery 	 = ""; 
			String sStockId  = ""; 
			String sWbookId  = ""; 
			
			int iSeq  		 = 0;
			
			for (int inx = 0; inx < MaxRec; inx++){
				
				stockJr	  = (JDTORecord) wbL.get(inx);
				sStockId  = StringHelper.evl(stockJr.getFieldString("STOCK_ID"), "");
																								   
                /*
                 *	적치단  Table Update(작업요구상태='S'로 변경)
                 */
            	sQuery = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
				iSeq   = ydStackLayerDAO.requestupdateData(sQuery, 
														   new Object[]{YmCommonConst.STACK_LAYER_STAT_S, 
														   				sStockId });
				
				
				sWbookId = new ymCommonDAO().createWBook(sCurrStopLoc, 
						    						     sUSchCode, 
						    						     "O", 
						    						     sCurrStopLoc);
					    						    
			   /*	
				* //	작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
				* sQuery  = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectYdWBook";
				* wbookJr = ydStackLayerDAO.requestFind(sQuery);
                * 
				* if (wbookJr == null){
				* 	logger.println(LogLevel.DEBUG,this, "작업예약ID 생성 Error");
				* 	return false; 																				
				* }
				* 
				* sWbookId  = StringHelper.evl(wbookJr.getFieldString("WBOOK_SELECT"), "");
				* 
				* //	작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 
				* //	작업예약조, 등록자, 등록일시) 한다.
				* sQuery = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
				* iSeq   = ydWBookDAO.requestinsertData(sQuery, 
				* 									  new Object[]{sWbookId, 
				* 									  			   YmCommonConst.YD_GP_2, 
				* 									  			   sCurBay, 
				* 									  			   sUSchCode, //대차 상차스케쥴 코드
				*                									   YmCommonUtil.getWorkDuty(), 
				*                									   YmCommonUtil.getWorkParty() });	
				*/
				
				/*
				 *	저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
				 */
				sQuery = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
				iSeq   = ydStockDAO.requestupdateData(sQuery, 
													  new Object[]{sWbookId, 
													  			   YmCommonUtil.getSlabCurrProgCd(sStockId,"")[1],
													  			   sStockId });		
				/*
		    	 * 	2009.07 YJK 생산통제 장입진행정보 기존모듈 삭제.
		    	 * 
					// 관제 압연 사양  Table 물류진행상태 Update 
					ZZPC001 model = new ZZPC001();
					model.setTcCode("YMPC100");
					model.setrealStlNo(sStockId);
					model.seteventStat("10");
					model.seteventOccurDDTT(YmCommonUtil.getStringYMDHMS());
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					Boolean isTrue = (Boolean)ejbConn.trx("sendInternalModel",new  Class[]{CommonModel.class},
				 *													  		  new Object[]{model});						
				 */												  
				logger.println(LogLevel.DEBUG,this, "자동 작업예약 생성 ="+sStockId+"/"+sWbookId);														  			   				
			}				
		
			return isSuccess;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
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
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
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
            
			scrane_sch_id		= "000000000000000000";    
	            scrane_stock_id		= sStockId.trim();
	            scrane_equip_gp	= "";    
	            scrane_sch_code	= "";   
	            scrane_up_loc		= sUpLoc; 
	            scrane_put_loc		= sPutLoc;
	            scrane_up_func		= YmCommonConst.CRANE_FUNC_N; 
	            scrane_put_func	= YmCommonConst.CRANE_FUNC_N;
	            scrane_register		= "SYSTEM"; 
	            scrane_modifier		= "SYSTEM"; 
	            scrane_yd_gp		= sYdGp;
	            
	            String sUpBay	= sUpLoc.length() > 2 ? sUpLoc.substring(1,2)  : "";
	            String sPutBay	= sPutLoc.length()> 2 ? sPutLoc.substring(1,2) : "";
	            
	            if("".equals(sSchCode)){
	            	if(sUpBay.equals(sPutBay)){
	            		scrane_sch_code = YmCommonConst.NEW_SCH_WORK_KIND_CYMM;
	            	}else{
	            		scrane_sch_code = YmCommonConst.NEW_SCH_WORK_KIND_CTML;
	            	}
	            }else{
	            	scrane_sch_code = sSchCode;
	        	}            
	        	
	            scrane_equip_gp = sTCNo.trim();
	        	        
			/*
			INSERT INTO TB_YM_WRSLT (
					CRANE_WRSLT_ID, 
					SCH_ID,   
					STOCK_ID,  
					EQUIP_GP,  
					SCH_WORK_KIND,     
	                CRANE_WORK_DDTT,      
	                CRANE_WORD_DDTT,   
	                CRANE_WRSLT_CD,  
	                SCH_WPREFER,
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
	                to_char(sysdate,'YYYYMMDDHH24MISS'), 
	                'N', 
	                '1',
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
			
		 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.insertCraneWrsltSanJuk_01";			
		 	iSeq  			  = ydWBookDAO.requestinsertData(queryCode, new Object[]{ 
																		scrane_sch_id,
															            scrane_stock_id,
															            scrane_equip_gp,
															            scrane_sch_code,
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
	 * 오퍼레이션명 : 
	 *
	 * 대차 도착시 From 위치와 To 위치 정보를 실적 처리한다.
        *
        * param String	: 대차 No
        * param String	: From LOC
        * param String	: To LOC
        * param String	: 스케쥴 코드
        * param String	: 야드구분
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */               
	public boolean receiveMHMI110(String sMessage) { 

        logger.println(LogLevel.DEBUG,this,"Start-receiveMHMI110()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackColDAO ydStackColDAO     = new YdStackColDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO CraneSchDAO         = new CraneSchDAO();
		
		try{
			int iResult = 0;
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);

	        //설비코드	설비코드	CHAR	4	설비 code( SSX1 )
			String TC_No                = StringHelper.evl(jDTORecord.getFieldString("설비코드"), "");   // SSX1
		
			/*
			 * 상차 실적 Call
			 */
			if (!TC_No.trim().equals("SSX1")){
				logger.println(LogLevel.DEBUG,this,"수신한 설비코드가 SSX1 이 아님");
			}else {

				String sBCOIL_EFF_YN = "N";
				JDTORecord jrChk = ymComm.getNewModuleEffYn_YM();
				sBCOIL_EFF_YN = jrChk.getFieldString("BCOIL_EFF_YN");
				if(sBCOIL_EFF_YN.equals("Y")) {
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setField("MSG_ID"	, "MHMI110");  
					jrYdMsg.setField("EQUIP_CD"	, "SSX1");  //설비 
					
					//B열연 신규모듈 호출
					EJBConnector ydEjbCon = new EJBConnector("default", this);
				    ydEjbCon.trx("YmCommEJB", "rcvInterface", jrYdMsg);

				    logger.println(LogLevel.DEBUG,this,"End-receiveMHMI110()");
					
				    return true;
					
				} 
				EJBConnector ejbConn = new EJBConnector("default","JNDICUpResReg",this);
				Boolean isTrue       = (Boolean)ejbConn.trx("callHyscoTcRtInfo",new Class[]{String.class},new Object[]{ 
						               YmCommonConst.HYSCO_3XTC02 });								
			}
			
			logger.println(LogLevel.DEBUG,this,"End-receiveMHMI110()");
			  
			return true;
			    
		}catch(DAOException daoe){
		  throw daoe;
		}catch(Exception e){
		  throw new EJBServiceException(e);
		}    
	}			
		
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * HYSCO 대차이동요구 (HYSCO MHMI220 → 현대제철 CN1BP08 → 야드 L-2)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public boolean receiveMHMI220(String sMessage) {
		/*
		 * MHMI220
		 * 1	전문코드		전문코드		CHAR	7		Inter-face T/C Code
		 * 2	발생일자		발생일자		CHAR	14		전문발생시간(yyyymmddhhmmss)
		 * 3	작업근조		작업근조		CHAR	2		작업근조
		 * 4	작업공장		작업공장		CHAR	1		작업공장( 현대제철 : 'I', HYSCO : 'B')
		 * 5	작업공정		작업공정		CHAR	1		작업공정( 현대제철 : 'I', HYSCO : 'H')
		 * 6	전문COUNT	전문COUNT	CHAR	1		전문 발생 count(전문이 3개 생성시 첫번째 1, 두번째 2, 마지막 전문일경우 *)
		 * 7	전문요구구분	전문요구구분	CHAR	1		작업공정( 현대제철 : 'I', HYSCO : 'H')
		 * 8	여분항목		여분항목		CHAR	13		여분항목(space)
		 * 9	설비코드		설비코드		CHAR	4		이동요구 설비 Code( SSX1 )
		 * 
		 * CN1BP08
		 * 전문코드			전문코드		CHAR	7
		 * 발생일자			발생일자		CHAR	10		YYYY-MM-DD
		 * 발생시간			발생시간		CHAR	8		HH-MM-SS
		 * 전문구분			전문구분		CHAR	1		I : Initialize, U : Update D : Delete  R : Re-request
		 * 전문길이			전문길이		CHAR	4
		 * 대차설비코드		대차설비코드	CHAR	4		이동요구 설비 Code( SSX1 ) 
		 */
        
		logger.println(LogLevel.DEBUG,this,"Start-receiveMHMI220()");		
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackColDAO ydStackColDAO     = new YdStackColDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO CraneSchDAO         = new CraneSchDAO();
		StringBuffer sMsg               = new StringBuffer();
		
		try{
			int iResult = 0;
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);
			
	        //설비코드	설비코드	CHAR	4	설비 code( SSX1 )
			String TC_No                = StringHelper.evl(jDTORecord.getFieldString("설비코드"), "");   // SSX1			
			
			if (!TC_No.trim().equals("SSX1")){
				logger.println(LogLevel.DEBUG,this,"수신한 설비코드가 SSX1 이 아님");
			}else {
				
//sjh				
				String sBCOIL_EFF_YN = "N";
				JDTORecord jrChk = ymComm.getNewModuleEffYn_YM();
				sBCOIL_EFF_YN = jrChk.getFieldString("BCOIL_EFF_YN");
				
				if(sBCOIL_EFF_YN.equals("Y")) {
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setField("MSG_ID", "MHMI220");  
					jrYdMsg.setField("EQUIP_CD", "SSX1");  //작업근조 
					
					//B열연 신규모듈 호출
					EJBConnector ydEjbCon = new EJBConnector("default", this);
				    ydEjbCon.trx("YmCommEJB", "rcvInterface", jrYdMsg);
				    logger.println(LogLevel.DEBUG,this,"End-receiveMHMI220()");
					  
					return true;
				} 	
				
				StringBuffer SendMsgInfo    = new StringBuffer();
				
				String TC				= ""; 
				String sDate			= ""; 
				String sTime			= ""; 
				String Form				= ""; 
				String Message_Length	= ""; 				
				String TC_Code	        = "";
				
				TC 						= "CN1BP08"; 
				sDate 					= YmCommonUtil.getCurDate("yyyy-MM-dd");
				sTime					= YmCommonUtil.getCurDate("hh-mm-ss");
				Form					= "I";
				Message_Length			= "0004";
				TC_Code                 = "SSX1";
				
				sMsg.append(YmCommonUtil.FillToString(TC		    ,7));
				sMsg.append(YmCommonUtil.FillToString(sDate			,10));
				sMsg.append(YmCommonUtil.FillToString(sTime	    	,8));
				sMsg.append(YmCommonUtil.FillToString(Form		    ,1));
				sMsg.append(YmCommonUtil.FillToNumber(Message_Length,4));
				sMsg.append(YmCommonUtil.FillToNumber(TC_Code       ,4));
				
				SendMsgInfo  =  sMsg;

				EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				Boolean isTrue = (Boolean)ejbConn.trx("CN1BP08send",new Class[]{String.class},new Object[]{ SendMsgInfo.toString() });					
			}
			
			logger.println(LogLevel.DEBUG,this,"End-receiveMHMI220()");
			  
			return true;
			    
		}catch(DAOException daoe){
		  throw daoe;
		}catch(Exception e){
		  throw new EJBServiceException(e);
		}    
	}			
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * HYSCO 설비상태정보	MHMI310(HYSCO → 현대제철)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public boolean receiveMHMI310(String sMessage) {  
		/*
		 * 1	전문코드		전문코드		CHAR	7	7	Inter-face T/C Code		
		 * 2	발생일자		발생일자		CHAR	14	21	전문발생시간(yyyymmddhhmmss)
		 * 3	작업근조		작업근조		CHAR	2	23	작업근조
		 * 4	작업공장		작업공장		CHAR	1	24	작업공장( 현대제철 : 'I', HYSCO : 'B')
		 * 5	작업공정		작업공정		CHAR	1	25	작업공정( 현대제철 : 'I', HYSCO : 'H')
		 * 6	전문COUNT	전문COUNT	CHAR	1	26	전문 발생 count(전문이 3개 생성시 첫번째 1, 두번째 2, 마지막 전문일경우 *)
		 * 7	전문요구구분	전문요구구분	CHAR	1	27	작업공정( 현대제철 : 'I', HYSCO : 'H')
		 * 8	여분항목		여분항목		CHAR	13	40	여분항목(space)
		 * 9	설비코드		설비코드		CHAR	4	44	HYSCO 설비코드( 'JCX1':JIB Crane, 'CVX1': Chainconveyor )
		 * 10	운전상태		운전상태		CHAR	1	45	''A':AUTO, 'M':MANUAL( AUTO Mode를 제외한 운전 Mode )
		 * 11	설비상태		설비상태		CHAR	1	46	''0' : 고장, '1' : 정상 
		 */
        logger.println(LogLevel.DEBUG,this,"Start-receiveMHMI310()");
		try{
	        /**
	         * Message Parsing
	         */
	        JDTORecord parseData = new Level2Parser().parse(sMessage);
	        logger.println(LogLevel.DEBUG, this, parseData);	        
	        /**
	         * valid check
	         */
	        String stat	= getField(parseData, "설비상태");
	        if(stat.length() == 0) {
	            logger.println(LogLevel.DEBUG,this,"##### 설비상태 길이가 0");
	            return false;
	        }
	        
			String sBCOIL_EFF_YN = "N";
			JDTORecord jrChk = ymComm.getNewModuleEffYn_YM();
			sBCOIL_EFF_YN = jrChk.getFieldString("BCOIL_EFF_YN");
			
			if(sBCOIL_EFF_YN.equals("Y")) {
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("MSG_ID", "MHMI310");  
				jrYdMsg.setField("EQUIP_STAT", stat);  //작업근조 
				
				//B열연 신규모듈 호출
				EJBConnector ydEjbCon = new EJBConnector("default", this);
			    ydEjbCon.trx("YmCommEJB", "rcvInterface", jrYdMsg);
		        logger.println(LogLevel.DEBUG,this,"End-receiveMHMI310()");
				return true;
				
			} 
		        
	        /**
	         * 설비 SETTING
	         */
	        ymCommonDAO ymCommonDAO = new ymCommonDAO();
	        if(YmCommonConst.TRO_REC_0.equals(stat)) { 			//고장	            
	            ymCommonDAO.modifyLoadSchOfVicCar("C", "CTFL", "3XTC02");
	        }else if(YmCommonConst.TRO_REC_1.equals(stat)) { 	//복구	            
	            ymCommonDAO.modifyHMIOfEquip("O", "3XTC02");
	        }	        
            
	        //sendMIMH210(parseData);
	        logger.println(LogLevel.DEBUG,this,"End-receiveMHMI310()");
		}catch(DAOException daoe){
		  throw daoe;
		}catch(Exception e){
		  throw new EJBServiceException(e);
		}    
		return true;
	}		
	
    /**
     * @param parseData
     * 
     */
    private void sendMIMH210(JDTORecord parseData) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        /**
         * 1	전문코드		전문코드		CHAR	7	7	Inter-face T/C Code
         * 2	발생일자		발생일자		CHAR	14	21	전문발생시간(yyyymmddhhmmss)
         * 3	작업근조		작업근조		CHAR	2	23	작업근조
         * 4	작업공장		작업공장		CHAR	1	24	작업공장( 현대제철 : 'I', HYSCO : 'B')
         * 5	작업공정		작업공정		CHAR	1	25	작업공정( 현대제철 : 'I', HYSCO : 'H')
         * 6	전문count	전문count	CHAR	1	26	전문 발생 count(전문이 3개 생성시 첫번째 1, 두번째 2, 마지막 전문일경우 *)
         * 7	전문요구구분	전문요구구분	CHAR	1	27	작업공정( 현대제철 : 'I', HYSCO : 'H')
         * 8	여분항목		여분항목		CHAR	13	40	여분항목(space)
         * 9	설비코드		설비코드		CHAR	4	44	현대 대차 설비코드( 'SSX1' : slade skid )
         * 10	운전상태		운전상태		CHAR	1	45	''A':AUTO, 'M':MANUAL( AUTO Mode를 제외한 운전 Mode )
         * 11	설비상태		설비상태		CHAR	1	46	''0' : 고장, '1' : 정상
         */
        String tcName = "MIMH210";
        Map tc = new ymCommonDAO().readColumnLenOfTc(tcName);
        StringBuffer sendMsg = new StringBuffer();
        sendMsg.append(tcName);
        sendMsg.append(YmCommonUtil.getStringYMDHMS());
        appendMsg(sendMsg, getField(parseData, "작업근조"), 	getFieldLen(tc, "작업근조"));
        appendMsg(sendMsg, "I", 							getFieldLen(tc, "작업공장"));
        appendMsg(sendMsg, "I", 							getFieldLen(tc, "작업공정"));
        appendMsg(sendMsg, getField(parseData, "전문COUNT"), getFieldLen(tc, "전문COUNT"));
        appendMsg(sendMsg, "I", 							getFieldLen(tc, "전문요구구분"));
        appendMsg(sendMsg, getField(parseData, "여분항목"), 	getFieldLen(tc, "여분항목"));
        appendMsg(sendMsg, getField(parseData, "여분항목"), 	getFieldLen(tc, "여분항목"));
        appendMsg(sendMsg, getField(parseData, "설비코드"), 	getFieldLen(tc, "설비코드"));
        appendMsg(sendMsg, getField(parseData, "운전상태"), 	getFieldLen(tc, "운전상태"));
        appendMsg(sendMsg, getField(parseData, "설비상태"), 	getFieldLen(tc, "설비상태"));
        sendQueue(tcName, sendMsg.toString());
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
     * JDTORecord 가 가지는 name parameter에 대한 값을 리턴한다.
     * @param data
     * @param name
     * @return
     */
    private String getField(JDTORecord data, String name) {
        return StringHelper.evl(data.getFieldString(name), "").trim();
    }
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * HYSCO Coil 상세정보요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public boolean receiveMHMI510(String sMessage) { 
        /*
		 * MHMI510
		 * 1	전문코드		전문코드		CHAR	7		Inter-face T/C Code
		 * 2	발생일자		발생일자		CHAR	14		전문발생시간(yyyymmddhhmmss)
		 * 3	작업근조		작업근조		CHAR	2		작업근조
		 * 4	작업공장		작업공장		CHAR	1		작업공장( 현대제철 : 'I', HYSCO : 'B')
		 * 5	작업공정		작업공정		CHAR	1		작업공정( 현대제철 : 'I', HYSCO : 'H')
		 * 6	전문COUNT	전문COUNT	CHAR	1		전문 발생 count(전문이 3개 생성시 첫번째 1, 두번째 2, 마지막 전문일경우 *)
		 * 7	전문요구구분	전문요구구분	CHAR	1		작업공정( 현대제철 : 'I', HYSCO : 'H')
		 * 8	여분항목		여분항목		CHAR	13		여분항목(space)
		 * 9	COILNO		COILNO		CHAR	15		공급사 coil 번호
		 * 
		 * MIMH510
		 * 전문코드		전문코드		CHAR	7	7	Inter-face T/C Code
		 * 발생일자		발생일자		CHAR	14	21	전문발생시간(yyyymmddhhmmss)
		 * 작업근조		작업근조		CHAR	2	23	작업근조
		 * 작업공장		작업공장		CHAR	1	24	작업공장( 현대제철 : 'I', HYSCO : 'B')
		 * 작업공정		작업공정		CHAR	1	25	작업공정( 현대제철 : 'I', HYSCO : 'H')
		 * 전문COUNT		전문COUNT	CHAR	1	26	전문 발생 count(전문이 3개 생성시 첫번째 1, 두번째 2, 마지막 전문일경우 *)
		 * 전문요구구분	전문요구구분	CHAR	1	27	작업공정( 현대제철 : 'I', HYSCO : 'H')
		 * 여분항목		여분항목		CHAR	13	40	여분항목(space)
		 * 계약번호		계약번호		CHAR	10	50
		 * 계약번호행번	계약번호행번	CHAR	5	55
		 * COILNO		COILNO		CHAR	15	70
		 * 적치위치		적치위치		CHAR	10	80
		 * 주문두께		주문두께		CHAR	4	84	두께(mm)
		 * 주문폭			주문폭		CHAR	5	89	폭(mm)
		 * NET중량		NET중량		CHAR	5	94	중량(kg)
		 * 내경			내경			CHAR	4	98	내경(mm)
		 * 외경			외경			CHAR	5	103	외경(mm)
		 * 길이			길이			CHAR	4	107	길이(cm)
		 * 공급사규격		공급사규격	CHAR	20	127
		 * HEATNO		HEATNO		CHAR	15	142
		 * 공급사출고일	공급사출고일	CHAR	8	150
		 * MC여부		MC여부		CHAR	1	151
		 * COIL권취온도	COIL권취온도	CHAR	4	155	T_HSM_COILER(권취온도*Exit temperature from HSM)
		 * 냉각방법		냉각방법		CHAR	1	156	COOLING_MA(냉각 방법*Cooling type HSM: 1 = air, 2 = water)
		 * 어널링1		어널링1		CHAR	4	160	T_ANNEALING(온도*Annealing temperature)
		 * 어널링2		어널링2		CHAR	5	165	ANNEALING_TIME (초시간*Annealing temperature)
		 * 형상정보1		형상정보1		CHAR	3	168	PROFILE_1 (Strip profile coefficient A1)
		 * 형상정보2		형상정보2		CHAR	3	171	PROFILE_2 (Strip profile coefficient A1)
		 * 형상정보3		형상정보3		CHAR	3	174	PROFILE_3 (Strip profile coefficient A1)
		 * 형상정보4		형상정보4		CHAR	3	177	MATERIAL_HARDNESS(*Material Hardness ID) 
         */
        logger.println(LogLevel.DEBUG,this,"Start-receiveMHMI510()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackColDAO ydStackColDAO     = new YdStackColDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO CraneSchDAO         = new CraneSchDAO();
		StringBuffer sMsg               = new StringBuffer();
		
		try{
			int iResult = 0;
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);
			
			String COIL_No              = StringHelper.evl(jDTORecord.getFieldString("COILNO"), "");   // COIL NO			
			
			String sBCOIL_EFF_YN = "N";
			JDTORecord jrChk = ymComm.getNewModuleEffYn_YM();
			sBCOIL_EFF_YN = jrChk.getFieldString("BCOIL_EFF_YN");
			
			if(sBCOIL_EFF_YN.equals("Y")) {
				
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("MSG_ID", "MHMI510");  
				jrYdMsg.setField("COILNO", COIL_No);  //코일번호 
				
				//B열연 신규모듈 호출
				EJBConnector ydEjbCon = new EJBConnector("default", this);
			    ydEjbCon.trx("YmCommEJB", "rcvInterface", jrYdMsg);
				
			    logger.println(LogLevel.DEBUG,this,"End-receiveMHMI510()");
				  
				return true;
			} 
				
			if (COIL_No.trim().equals("")){ logger.println(LogLevel.DEBUG,this,"수신 COIL No SPACE");	}
			
			String sStockQueryId   = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
			JDTORecord StockCoilNo = ydStockDAO.getData(sStockQueryId, new Object[]{ COIL_No.trim() });

			if (StockCoilNo == null){ 
				logger.println(LogLevel.DEBUG,this, "StockCoilNo Error");
				return false; 																																									
			}			
			
			String stockId = StringHelper.evl(StockCoilNo.getFieldString("STOCK_ID"), "");
			
			if (stockId == null || stockId.equals("")){
				logger.println(LogLevel.DEBUG,this, "상세정보 요청한 Coil이 존재하지 않음  Error");
				return false; 
			}
			
			/*
			 * 계약번호 	USRSMA.TB_SM_ORDDTL.CUST_PO_NO
			 * 주문두께	USRPMA.TB_PM_COILCOMM.COIL_T
			 * 주문폭   	USRPMA.TB_PM_COILCOMM.COIL_W
			 * NET중량  	USRPMA.TB_PM_COILCOMM.NET_WEIGHT_WT
			 * 내경     	USRPMA.TB_PM_COILCOMM.COIL_INDIA
			 * 외경    	USRPMA.TB_PM_COILCOMM.COIL_OUTDIA
			 * 길이     	USRPMA.TB_PM_COILCOMM.COIL_LEN
			 * HEATNO   USRPMA.TB_PM_SLABCOMM.BUY_HEAT_NO
			 * 냉각방법 	USRPMA.TB_PM_COILCOMM.COOL_METHOD (W수냉 A공냉)
			 * 어널링1 	USRPOA.TB_PO_SLABREHEATFURWRSLT.EXT_TEMP
			 * 어널링2 	USRPOA.TB_PO_SLABREHEATFURWRSLT.TOT_INFUR_HR
			 */
			StringBuffer SendMsgInfo    = new StringBuffer();
			
			String TC				= ""; 
			String sDate			= ""; 
			String Group			= ""; 
			String Plant			= ""; 
			String Line         	= ""; 				
			String Count	        = "";
			String ReqGp	        = "";
			String Filler	        = "";
			String CustPoNo  	    = ""; //계약번호 10
			String CustPoSubNo      = ""; //계약번호행번 5
            // COIL_NO
			String Position	        = ""; //적치위치 10
			String CoilT	        = ""; //주문두께 4
			String CoilW	        = ""; //주문폭 5
			String CoilWT	        = ""; //NET중량 5
			String CoilInDia        = ""; //내경 4
			String CoilOutDia       = ""; //외경 5 
			String CoilLen	        = ""; //길이 4
			String SupplySpec       = ""; //공급사규격 20
			String HeatNo	        = ""; //HEATNO 15
			String SupplyDate       = ""; //공급사출고일 8
			String MCGp 	        = ""; //MC여부 1
			String HSMCooler        = ""; //COIL권취온도 4
			String CoolingType      = ""; //냉각방법 1
			String Annealing1       = ""; //어널링1 4
			String Annealing2       = ""; //어널링2 5
			String Profile1         = ""; //형상정보1 3
			String Profile2	        = ""; //형상정보2 3
			String Profile3	        = ""; //형상정보3 3
			String Profile4	        = ""; //형상정보4 3
			
			TC 						= "MIMH510"; 
			sDate 					= YmCommonUtil.getCurDate("yyyyMMddhhmmss");
			Group					= YmCommonUtil.getWorkParty();
			Plant					= "I";
			Line			        = "I";
			Count                   = "*";
			ReqGp                   = "I";
			
			/*
				SELECT  
				    SUBSTR(C.CUST_PO_NO,1,10)		AS 주문번호,
					SUBSTR(C.CUST_PO_NO,11,5) 		AS 주문행번,
				    B.ORD_T           as 주문두께,   
				    B.ORD_W           as 주문폭, 
				    A.COIL_WT         as NET중량,    
				    A.COIL_INDIA      as 내경,     
				    A.COIL_OUTDIA     as 외경,
				    A.CURR_COIL_LEN   as 길이, 
				    A.COOL_METHOD     as 냉각방법,
				    A.HYSCO_TRANS_GP  as HYSCO이송수단
				    
				FROM  USRPMA.TB_PM_COILCOMM A,
				      USRPMA.TB_PM_ORDPROG B,
				      USRSMA.TB_SM_ORDDTL C
				WHERE A.COIL_NO = :COIL_NO
				AND   A.ORD_NO  = B.ORD_NO(+)
				AND   A.ORD_DTL = B.ORD_DTL(+)
				AND   A.ORD_NO  = C.ORD_NO(+) 
				AND   A.ORD_DTL = C.ORD_DTL(+)
		
			 */
			String selectCoilComm   = "ym.steelinfo.steelinforecv.YdStockDAO.selectCoilComm";
			JDTORecord selectcoilcomm     = ydStockDAO.getData(selectCoilComm, new Object[]{ COIL_No.trim() });

			if (selectcoilcomm == null){ 
				logger.println(LogLevel.DEBUG,this, "selectcoilcomm 에러 ");
				return false; 																																									
			}			
			
			String Ord_No    = StringHelper.evl(selectcoilcomm.getFieldString("주문번호"), "");
			String Ord_SubNo = StringHelper.evl(selectcoilcomm.getFieldString("주문행번"), "");
			
			CoilT       	 = Double.valueOf((Double.parseDouble(StringHelper.evl(selectcoilcomm.getFieldString("주문두께"), "0")) * 1000) + "").longValue()+"";
			CoilW       	 = Double.valueOf((Double.parseDouble(StringHelper.evl(selectcoilcomm.getFieldString("주문폭"), "0")) * 10) + "").longValue()+"";
			CoilWT      	 = StringHelper.evl(selectcoilcomm.getFieldString("NET중량"), "");
			CoilInDia   	 = Double.valueOf((Double.parseDouble(StringHelper.evl(selectcoilcomm.getFieldString("내경"), "0")) * 25.4) + "").longValue()+"";
			CoilOutDia  	 = StringHelper.evl(selectcoilcomm.getFieldString("외경"), "");
			CoilLen     	 = StringHelper.evl(selectcoilcomm.getFieldString("길이"), "");
			CoolingType 	 = StringHelper.evl(selectcoilcomm.getFieldString("냉각방법"), "");
			
			if (CoolingType.trim().equals("A")){         //공냉
				CoolingType  = "1";
			}else if (CoolingType.trim().equals("W")){   //수냉 
				CoolingType  = "2";
			}
				
			/*
			 * select CUST_PO_NO as 계약번호  From USRSMA.TB_SM_ORDDTL  where ORD_NO = '주문번호' and ORD_DTL = '주문행번'
			 */
			/*
			String selectOrdDTL     = "ym.steelinfo.steelinforecv.YdStockDAO.selectOrdDTL";
			JDTORecord selectorddtl = ydStockDAO.getData(selectOrdDTL, new Object[]{ Ord_No.trim(), Ord_SubNo.trim() });

			if (selectorddtl == null){ 
				CustPoNo         = "";
			}else {
				CustPoNo         = StringHelper.evl(selectorddtl.getFieldString("계약번호"), "");	
			}
			*/
			/*
			 * select BUY_HEAT_NO as HEATNO  From USRPMA.TB_PM_SLABCOMM  where COIL_NO = 'COILNO'
			 */
			String selectSlabComm     = "ym.steelinfo.steelinforecv.YdStockDAO.selectSlabComm";
			JDTORecord selectslabcomm = ydStockDAO.getData(selectSlabComm, new Object[]{ COIL_No.trim() });

			if (selectslabcomm == null){ 
				HeatNo          = "";
			}else {
				HeatNo          = StringHelper.evl(selectslabcomm.getFieldString("HEATNO"), "");	
			}
			
//			String[] ymd =	YmCommonUtil.getStringYMD("-").split("-");
//			ymd[0] +"년"+ ymd[1] +"월"+ ymd[2]+"일"
				
			SupplyDate      = YmCommonUtil.getStringYMD(); // 공급사 출고일 "20060501"
			
			/*
			 * select EXT_TEMP as 어널링1, TOT_INFUR_HR as 어널링2
			 *   From USRPOA.TB_PO_SLABREHEATFURWRSLT
			 *    and ROWNUM = 1
			 *  where COIL_NO = 'COILNO'			
			 */
			String selectSlabReHeat     = "ym.steelinfo.steelinforecv.YdStockDAO.selectSlabReHeat";
			JDTORecord selectslabreheat = ydStockDAO.getData(selectSlabReHeat, new Object[]{ COIL_No.trim() });

			if (selectslabreheat == null){ 
				Annealing1       = "";
				Annealing2       = "";
			}else {
				Annealing1       = StringHelper.evl(selectslabreheat.getFieldString("어널링1"), "");
				Annealing2       = StringHelper.evl(selectslabreheat.getFieldString("어널링2"), "");				
			}
			
			/*
			 * 대차 출하 상차한 정보 (적치위치) - 현재 대차에 실려있는 작업 대상재 검검
			 * SELECT FRTOMOVE_EQUIP_BED_GP,     --이송설비번지
			 *   FROM TB_YM_STOCK
			 *  WHERE STOCK_ID = ?
			 */
			String selectTCStock     = "ym.steelinfo.steelinforecv.dao.YdStockDAO.selectTCStock";
			JDTORecord selecttcstock = ydStockDAO.getData(selectTCStock, new Object[]{ COIL_No.trim() });
			
			if (selecttcstock == null){ 
				logger.println(LogLevel.DEBUG,this, "selecttcstock 에러 ");
				return false; 																																									
			}			
			
			Position         = StringHelper.evl(selecttcstock.getFieldString("FRTOMOVE_EQUIP_BED_GP"), "");
			
			if (Position.trim().equals("01")){
				Position = "B1XSS-X101";
			}else if (Position.trim().equals("02")){
				Position = "B1XSS-X102";
			}else if (Position.trim().equals("03")){
				Position = "B1XSS-X103";
			}
			
			sMsg.append(YmCommonUtil.FillToString(TC		    ,7));
			sMsg.append(YmCommonUtil.FillToString(sDate			,14));
			sMsg.append(YmCommonUtil.FillToString(Group	    	,2));
			sMsg.append(YmCommonUtil.FillToString(Plant		    ,1));
			sMsg.append(YmCommonUtil.FillToNumber(Line          ,1));
			sMsg.append(YmCommonUtil.FillToNumber(Count         ,1));
			sMsg.append(YmCommonUtil.FillToNumber(ReqGp         ,1));
			sMsg.append(YmCommonUtil.FillToNumber(Filler        ,13));			
			sMsg.append(YmCommonUtil.FillToString(Ord_No        ,10));
			sMsg.append(YmCommonUtil.FillToNumber(Ord_SubNo     ,5));
			sMsg.append(YmCommonUtil.FillToString(COIL_No       ,15));
			sMsg.append(YmCommonUtil.FillToString(Position      ,10));
			sMsg.append(YmCommonUtil.FillToString(CoilT         ,4));
			sMsg.append(YmCommonUtil.FillToString(CoilW         ,5));
			sMsg.append(YmCommonUtil.FillToString(CoilWT        ,5));
			sMsg.append(YmCommonUtil.FillToString(CoilInDia     ,4));
			sMsg.append(YmCommonUtil.FillToString(CoilOutDia    ,5));
			sMsg.append(YmCommonUtil.FillToString(CoilLen       ,4));
			sMsg.append(YmCommonUtil.FillToString(SupplySpec    ,20));
			sMsg.append(YmCommonUtil.FillToString(HeatNo        ,15));
			sMsg.append(YmCommonUtil.FillToString(SupplyDate    ,8));
			sMsg.append(YmCommonUtil.FillToString(MCGp          ,1));
			sMsg.append(YmCommonUtil.FillToString(HSMCooler     ,4));
			sMsg.append(YmCommonUtil.FillToString(CoolingType   ,1));
			sMsg.append(YmCommonUtil.FillToString(Annealing1    ,4));
			sMsg.append(YmCommonUtil.FillToString(Annealing2    ,5));
			sMsg.append(YmCommonUtil.FillToString(Profile1      ,3));
			sMsg.append(YmCommonUtil.FillToString(Profile2      ,3));
			sMsg.append(YmCommonUtil.FillToString(Profile3      ,3));
			sMsg.append(YmCommonUtil.FillToString(Profile4      ,3));
			
			SendMsgInfo  =  sMsg;

			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			Boolean isTrue = (Boolean)ejbConn.trx("MIMH510send",new Class[]{String.class},new Object[]{ SendMsgInfo.toString() });
			
			
			logger.println(LogLevel.DEBUG,this,"End-receiveMHMI510()");
			  
			return true;
			    
		}catch(DAOException daoe){
		  throw daoe;
		}catch(Exception e){
		  throw new EJBServiceException(e);
		}    
	}		
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * HYSCO 권상권하실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public boolean receiveMHMI710(String sMessage) { 
        /*
		 * 1	전문코드		전문코드		CHAR	7		Inter-face T/C Code
		 * 2	발생일자		발생일자		CHAR	14		전문발생시간(yyyymmddhhmmss)
		 * 3	작업근조		작업근조		CHAR	2		작업근조
		 * 4	작업공장		작업공장		CHAR	1		작업공장( 현대제철 : 'I', HYSCO : 'B')
		 * 5	작업공정		작업공정		CHAR	1		작업공정( 현대제철 : 'I', HYSCO : 'H')
		 * 6	전문COUNT	전문COUNT	CHAR	1		전문 발생 count(전문이 3개 생성시 첫번째 1, 두번째 2, 마지막 전문일경우 *)
		 * 7	전문요구구분	전문요구구분	CHAR	1		작업공정( 현대제철 : 'I', HYSCO : 'H')
		 * 8	여분항목		여분항목		CHAR	13		여분항목(space)
		 * 9	설비코드		설비코드		CHAR	4		작업설비 Code
		 * 10	작업구분		작업구분		CHAR	1		작업구분( '1':권상, '2':권하 )
		 * 11	COILNO		COILNO		CHAR	15		공급사 coil 번호
		 * 12	작업위치		작업위치		CHAR	10		작업 위치( 권상시 권상위치, 권하시 권하위치) 
         */
        logger.println(LogLevel.DEBUG,this,"Start-receiveMHMI710()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackColDAO ydStackColDAO     = new YdStackColDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO CraneSchDAO         = new CraneSchDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iResult = 0;
			
			logger.println(LogLevel.DEBUG,this,"End-receiveMHMI710()");
			  
			return true;
			    
		}catch(DAOException daoe){
		  throw daoe;
		}catch(Exception e){
		  throw new EJBServiceException(e);
		}    
	}				
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * HYSCO 대차 이동 정보(야드 L2 CN1PB13 에서 수신받아 HYSCO로 MIMH220 넘겨줄 TC)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public boolean receiveCN1PB13(String sMessage) { 
        /*
		 * CN1PB13
		 * 전문코드	전문코드	CHAR	7
		 * 발생일자	발생일자	CHAR	10		YYYY-MM-DD
		 * 발생시간	발생시간	CHAR	8		HH-MM-SS
		 * 전문구분	전문구분	CHAR	1		I  : Initialize, U : Update D : Delete  R : Re-request
		 * 전문길이	전문길이	CHAR	4
		 * 이동구분	이동구분	CHAR	1		''1':출발, ‘2':도착
		 * 출발번지	출발번지	CHAR	10		출발번지(B1XSS-X101, B1XSS-X102, B1XSS-X103, B1XSS-X104)
		 * 도착번지	도착번지	CHAR	10		도착번지(B1XSS-X101, B1XSS-X102, B1XSS-X103, B1XSS-X104)
		 * 
		 * MIMH220
		 * 전문코드		전문코드		CHAR	7		Inter-face T/C Code
		 * 발생일자		발생일자		CHAR	14		전문발생시간(yyyymmddhhmmss)
		 * 작업근조		작업근조		CHAR	2		작업근조
		 * 작업공장		작업공장		CHAR	1		작업공장( 현대제철 : 'I', HYSCO : 'B')
		 * 작업공정		작업공정		CHAR	1		작업공정( 현대제철 : 'I', HYSCO : 'H')
		 * 전문COUNT		전문COUNT	CHAR	1		전문 발생 count(전문이 3개 생성시 첫번째 1, 두번째 2, 마지막 전문일경우 *)
		 * 전문요구구분	전문요구구분	CHAR	1		작업공정( 현대제철 : 'I', HYSCO : 'H')
		 * 여분항목		여분항목		CHAR	13		여분항목(space)
		 * 이동구분		이동구분		CHAR	1		''1':출발, ‘2':도착
		 * 출발번지		출발번지		CHAR	10		출발번지(B1XSS-X101, B1XSS-X102, B1XSS-X103, B1XSS-X104)
		 * 도착번지		도착번지		CHAR	10		도착번지(B1XSS-X101, B1XSS-X102, B1XSS-X103, B1XSS-X104) 
         */
        logger.println(LogLevel.DEBUG,this,"Start-receiveCN1PB13()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackColDAO ydStackColDAO     = new YdStackColDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO CraneSchDAO         = new CraneSchDAO();
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
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);
			
			String Move_Gp              = StringHelper.evl(jDTORecord.getFieldString("이동구분"), "");   // '1':출발, ‘2':도착			
			String StartAddr            = StringHelper.evl(jDTORecord.getFieldString("출발번지"), "");   // 출발번지(B1XSS-X101, B1XSS-X102, B1XSS-X103, B1XSS-X104)
			String ArriAddr             = StringHelper.evl(jDTORecord.getFieldString("도착번지"), "");   // 도착번지(B1XSS-X101, B1XSS-X102, B1XSS-X103, B1XSS-X104)
			
			StringBuffer SendMsgInfo    = new StringBuffer();
			
			String TC				= ""; 
			String sDate			= ""; 
			String Group			= ""; 
			String Plant			= ""; 
			String Line         	= ""; 				
			String Count	        = "";
			String ReqGp	        = "";
			String Filler	        = "";
			
			TC 						= "MIMH220"; 
			sDate 					= YmCommonUtil.getCurDate("yyyyMMddhhmmss");
			Group					= YmCommonUtil.getWorkParty();
			Plant					= "I";
			Line			        = "I";
			Count                   = "*";
			ReqGp                   = "I";
			
			sMsg.append(YmCommonUtil.FillToString(TC		    ,7));
			sMsg.append(YmCommonUtil.FillToString(sDate			,14));
			sMsg.append(YmCommonUtil.FillToString(Group	    	,2));
			sMsg.append(YmCommonUtil.FillToString(Plant		    ,1));
			sMsg.append(YmCommonUtil.FillToNumber(Line          ,1));
			sMsg.append(YmCommonUtil.FillToNumber(Count         ,1));
			sMsg.append(YmCommonUtil.FillToNumber(ReqGp         ,1));
			sMsg.append(YmCommonUtil.FillToNumber(Filler        ,13));
			sMsg.append(YmCommonUtil.FillToNumber(Move_Gp       ,1));
			sMsg.append(YmCommonUtil.FillToNumber(StartAddr     ,10));
			sMsg.append(YmCommonUtil.FillToNumber(ArriAddr      ,10));
			
			SendMsgInfo  =  sMsg;

			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			Boolean isTrue = (Boolean)ejbConn.trx("MIMH220send",new Class[]{String.class},new Object[]{ SendMsgInfo.toString() });
			
			logger.println(LogLevel.DEBUG,this,"End-receiveCN1PB13()");
			  
			return true;
			    
		}catch(DAOException daoe){
		  throw daoe;
		}catch(Exception e){
		  throw new EJBServiceException(e);
		}    
	}					
	
}

