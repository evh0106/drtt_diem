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
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.util.StringHelper;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
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
 * @ejb.bean name="CCLdWrkOrdRegEJB" jndi-name="JNDICCLdWrkOrdReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CCLdWrkOrdRegSBean extends BaseSessionBean {
	private YmComm ymComm = new YmComm();
	
	private Logger logger 			= null;
	JDTORecord tcRecord = JDTORecordFactory.getInstance().create();
	
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger 		= new Logger(config);
	}

      /**
	 * 오퍼레이션명 :  
	 * 야드 LEVEL2로부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
        * 전문내용을 JDTORecord로 파싱한다.
        * 업무 로직
        *	1.TC_CD - CN1PB12 (I/F ID : YM-BIF-012 ) 
        *	2.야드 Level-2 시스템으로부터 대차도착 정보를 수신
        *  3.영대차인지 공대차인지 점검
        *  4.대차 현위치 변경
        *  5.적재상태가 'L'일때
        *  6.적재상태가 'U'일때 
        *  7.5/6번에 해당하는 상하차 작업 Schedule Code가 지정 또는 검색 되었다면 Coil Schedule Call
        *  8.대차 작업 진행 상태 변경 
        *    - Schedule을 수행했다면 'S', Schedule을 미수행했다면 'W'
        *   대차 설비 번호 
	 *	2XTC01 : SLAB대차01호
	 *	2XTC02 : SLAB대차02호
	 *	2XTC03 : SLAB대차03호
	 *	
	 *	3XTC01 : 하이스코대차
	 *	3XTC02 : HFL대차
	 *	3XTC03 : 동간이적대차
        *
	 *	상차 지정 유무 CARLOAD_ASSIGN_YN    VARCHAR2(1)  NULL
	 *	하차 지정 유무 CARUNLOAD_ASSIGN_YN  VARCHAR2(1)  NULL
        * B열연 대차 도착  CN1PB12
        * logger.println("전문코드		==" +jDTORecord.getFieldString("전문코드"));
	 * logger.println("발생일자		==" +jDTORecord.getFieldString("발생일자"));
	 * logger.println("발생시간 	==" +jDTORecord.getFieldString("발생시간"));
	 * logger.println("전문구분		==" +jDTORecord.getFieldString("전문구분"));    // I : Initialize, U : Update, D : Delete, R : Re-request
	 * logger.println("전문길이		==" +jDTORecord.getFieldString("전문길이"));
	 * logger.println("대차번호		==" +jDTORecord.getFieldString("대차번호"));    // 설비구분 : TC(2) + TC NO(2)
	 * logger.println("대차도착동  	==" +jDTORecord.getFieldString("대차도착동"));  // YARD구분(1) + 동구분(1)+ SPAN(2) + 열 NO(2)  
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return 
	 * @throws 
	 */     	   
	public boolean receiveOutCCLdWrkOrd(String sMessage) { 
		
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
		
			return receiveOutCCLdWrkOrd(jDTORecord);
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	
	/**
	 * 오퍼레이션명 : 대차 도착 처리
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean receiveOutCCLdWrkOrd(JDTORecord jDTORecord)  throws java.rmi.RemoteException{ 
        
	        logger.println(LogLevel.DEBUG,this,"Start-receiveOutCCLdWrkOrd()");
			
			YdStockDAO ydStockDAO 	        = new YdStockDAO();
			YdStackColDAO ydStackColDAO     = new YdStackColDAO();
			YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
			YdWBookDAO ydWBookDAO           = new YdWBookDAO();
			CraneSchDAO CraneSchDAO         = new CraneSchDAO();
			
			// 최규성 추가
			List list = new ArrayList();
			
			try{
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return false;
				}
				
				int iResult 			= 0;
				int iReq 				= 0;
				String wBookidSchCall 	= "";
				
				// 야드 Level-2에서 대차 도착 정보를  수신			
				String tcTcNo        = StringHelper.evl(jDTORecord.getFieldString("대차번호"), "");   // 설비구분 : TC(2) + TC NO(2)
					   tcTcNo 		 = YmCommonConst.TC_3X + tcTcNo;          				  		  // B열연 COIL 대차		
				String tcCurrStopLoc = StringHelper.evl(jDTORecord.getFieldString("대차도착동"), ""); // YARD구분(1) + 동구분(1)+ SPAN(2) + 열 NO(2)

				String sBACKUP_YN 	 = StringHelper.evl(jDTORecord.getFieldString("BACKUP_YN"), ""); //화면에서 백업처리 시
				// B열연 Coil 대차도착동 야드구분
				String tmpYd_Gp  	 = tcCurrStopLoc.substring(0, 1); // 야드구분
				// B열연 Coil 대차도착동 동 구분
				String tmpBay_Gp 	 = tcCurrStopLoc.substring(1, 2); // 동구분
				
				logger.println(LogLevel.DEBUG,this,"B열연Coil대차:"+tcTcNo);
				logger.println(LogLevel.DEBUG,this,"대차도착동:"+tcCurrStopLoc);
				logger.println(LogLevel.DEBUG,this,"야드구분:"+tmpYd_Gp);
				logger.println(LogLevel.DEBUG,this,"동구분"+tmpBay_Gp);
				
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
				
				// 설비 정보를 가져온다. CGS
				// 대차정보 고장 및 OFFLINE 검사
				// 대차최대적치수량
				/*
				SELECT *
				FROM tb_ym_equip
				WHERE equip_gp = :equip_gp
				*/  
				JDTORecord equipJr = CraneSchDAO.getEquipInfoWithEquipGp(tcTcNo);
				
				if(equipJr != null){
					sEquipStat 		= StringHelper.evl(equipJr.getFieldString("EQUIP_STAT"), "");				// 설비 상태
					sWorkMode  		= StringHelper.evl(equipJr.getFieldString("WORK_MODE"), "");				// 작업 모드
					sStackStat 		= StringHelper.evl(equipJr.getFieldString("STACK_STAT"), "");				// 적재 상태
					sStackMaxQnty	= StringHelper.evl(equipJr.getFieldString("STACK_MAX_QNTY"), "0");			// 적재 최대 수량
					sCurrStopLoc  	= StringHelper.evl(equipJr.getFieldString("CURR_STOP_LOC"), "");			// 현재 정지 위치
					
					tmpLoadGp        = StringHelper.evl(equipJr.getFieldString("CARLOAD_ASSIGN_YN"), "");        //상차지정유무
					tmpLoadSch       = StringHelper.evl(equipJr.getFieldString("CARLOAD_SCH_WORK_KIND"), "");    //상차 Schedule 작업종류
					tmpUnLoadGp      = StringHelper.evl(equipJr.getFieldString("CARUNLOAD_ASSIGN_YN"), "");      //하차지정유무
					tmpUnLoadSch     = StringHelper.evl(equipJr.getFieldString("CARUNLOAD_SCH_WORK_KIND"), "");  //하차 Schedule 작업종류
					tmpLoadStopLoc   = StringHelper.evl(equipJr.getFieldString("CARLOAD_STOP_LOC"), "");         //상차위치
					tmpUnLoadStopLoc = StringHelper.evl(equipJr.getFieldString("CARUNLOAD_STOP_LOC"), "");       //하차위치
				}
				 
				if(YmCommonConst.WORK_MODE_C.equals(sEquipStat)||
				   YmCommonConst.WORK_MODE_C.equals(sWorkMode)){		// Off Line
					
					logger.println(LogLevel.DEBUG,this,"대차도착=>대차정보 고장 및 OFFLINE 처리="+sEquipStat+sWorkMode);
					return false;
				}
				
				if(sCurrStopLoc.length() > 2){
					sCurrStopBay  = sCurrStopLoc.substring(1,2); // ex)1BTC03 -> B
				}
				if (tmpBay_Gp.trim().equals(sCurrStopBay)){
					
					
					if(sBACKUP_YN.equals("Y")){
						//skip
						logger.println(LogLevel.DEBUG,this,"대차도착=>대차 도착동이 현재 도착동과 같습니다.(BACKUP 기능에 따른 SKIP처리)");
					}else {
						logger.println(LogLevel.DEBUG,this,"대차도착=>대차 도착동이 현재 도착동과 같습니다.");
						return false;
					}
				}
								
				/**************************************************************
				 *	대차도착처리 예외사항1 시작
				 *	=>	도착처리시	=>	하차상태('U')이고	=>	공대차이면(저장품정보 존재안함)
				 *	=>	상차상태('L')로 수정한다.
				 **************************************************************/
				if (sStackStat.equals(YmCommonConst.STACK_STAT_U)){  
					/*
						 SELECT STOCK_ID,
	            				FRTOMOVE_EQUIP_BED_GP,     --이송설비번지
	            				FRTOMOVE_EQUIP_LAYER_GP,  --이송설비단
	            				STOCK_MOVE_TERM
	   					   FROM TB_YM_STOCK
	 					  WHERE FRTOMOVE_EQUIP_GP = :tcTcNo  --확장대차(1XTC03)
	     					AND STOCK_ID IS NOT NULL
	 				   ORDER BY FRTOMOVE_EQUIP_BED_GP,FRTOMOVE_EQUIP_LAYER_GP DESC
					*/
					String sYQeury1  = "ym.steelinfo.steelinforecv.dao.YdStockDAO.selectStock";
					List stockL      = ydStockDAO.getListData(sYQeury1, new Object[]{ tcTcNo });					
					
					int iCount = 0;
					/*
					Ver4.5--
					SELECT 
					    count(stock_id) as cnt
					FROM  TB_YM_STACKLAYER
					WHERE stack_col_gp = :stack_col_gp
					*/  
					JDTORecord cTcJr = CraneSchDAO.getTCLoadCount(tcCurrStopLoc); 
					if(cTcJr != null){
						iCount = cTcJr.getFieldInt("CNT");
					}
						
					if(stockL.size() == 0 && 	
					   iCount		 == 0 ){
						/*
						Ver4.0--
						UPDATE tb_ym_equip
						SET	
							STACK_STAT = :STACK_STAT,
							WPROG_STAT = :WPROG_STAT,
							modifier   = 'SYSTEM',
						 	mod_ddtt   = sysdate     
						WHERE equip_gp = :equip_gp
						*/
					   	int iSeq = CraneSchDAO.updateEquipTcInfo(YmCommonConst.STACK_STAT_L,	// 상차상태
																 YmCommonConst.WPROG_STAT_W,	// IDLE, Schedule 대기
																 tcTcNo); 
						logger.println(LogLevel.DEBUG,this,"대차도착=>대차도착처리 예외사항.");   	
						logger.println(LogLevel.DEBUG,this,"대차도착=>상차대기상태로 수정.STACK_STAT_L : WPROG_STAT_W");   	
					}
				}
				/**************************************************************
				 *	대차도착처리 예외사항1 종료
				 **************************************************************/
				
				/**
				 *	도착처리시 다른동 적치단 활성상태를 모두 비활성화 한다.
				 */
				/* 
				Ver4.0--
				UPDATE  USRYMA.TB_YM_STACKLAYER
				SET     STACK_LAYER_ACTIVE_STAT = 'C'
				WHERE   STACK_COL_GP LIKE :STACK_COL_GP||'%' -- 3_TC%
				*/
				String tmpTcNo = "3_"+StringHelper.evl(jDTORecord.getFieldString("대차번호"), ""); 
				// STACK_LAYER_ACTIVE_STAT = 'C' : 비활성화 코드로 수정.
				iReq = CraneSchDAO.updateStackLayerStatInfo(tmpTcNo);
				
				logger.println(LogLevel.DEBUG,this,"대차위치정보 초기화(적치 단 활성상태 : 비활성화)= ["+tmpTcNo+"] = ["+String.valueOf(iReq) );
										
				/*
				 *	1.1	적재상태가 'L' (상차) 일때
				 */
				if (sStackStat.equals(YmCommonConst.STACK_STAT_L)){         
					//SKIP
					logger.println(LogLevel.DEBUG,this,"적재상태가 'L' (상차) 일때 : "+sStackStat );
				/*
				 *	1.2	적재상태가 'U' (하차) 일때
				 */
				}else if (sStackStat.equals(YmCommonConst.STACK_STAT_U)){   
					logger.println(LogLevel.DEBUG,this,"적재상태가 'U' (하차) 일때 : "+sStackStat );
					if (!tmpBay_Gp.trim().equals(tmpUnLoadStopLoc.trim().substring(1, 2))){
						logger.println(LogLevel.DEBUG,this,"에러 데이터 Bay_GP :"+tmpBay_Gp );
						logger.println(LogLevel.DEBUG,this,"에러 데이터 tmpUnLoadStopLoc.trim().substring(1, 2):"+tmpUnLoadStopLoc.trim().substring(1, 2) );
						//throw new EJBServiceException("대차도착 에러 : 대차하차 도착동이 목적동이 아닙니다.");
						logger.println(LogLevel.DEBUG,this,"대차도착 에러 : 대차하차 도착동이 목적동이 아닙니다.");
						logger.println(LogLevel.DEBUG,this,"대차도착 에러 : return false;");
						return false;
					}											
				}
				
			   /*
				*	2	COIL 대차의 현재위치를 변경한다.
				
					 UPDATE TB_YM_EQUIP 
					    SET CURR_STOP_LOC = ?
	 				  WHERE EQUIP_GP = ?   --확장대차(1XTC03)
				*/
				String updateCurrQueryId = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateLoadCurrLoc";
				int CurrStop             = ydStackLayerDAO.requestupdateData(updateCurrQueryId, 
																			 new Object[]{tcCurrStopLoc, 
																			 			  tcTcNo });										

				/**************************************************************
				 *	대차도착처리 예외사항2 시작
				 *	=>	도착처리시	=>	상차상태('L')이고	=>	공대차가 아니면(저장품정보 존재함)
				 **************************************************************/
				if (sStackStat.equals(YmCommonConst.STACK_STAT_L)){  
					logger.println(LogLevel.DEBUG,this,"대차도착처리 예외사항2 시작");
					int iCount = 0;
					/*
						SELECT count(stock_id) as CNT
						FROM  TB_YM_STACKLAYER
						WHERE stack_col_gp = :stack_col_gp
					*/ 
					JDTORecord cTcJr = CraneSchDAO.getTCLoadCount(tcCurrStopLoc); 
					if(cTcJr != null){
						iCount = cTcJr.getFieldInt("CNT");
					}                   
					logger.println(LogLevel.DEBUG,this,"대차도착처리예외2 현 적치된 저장품정보=>CNT: "+iCount);
					logger.println(LogLevel.DEBUG,this,"CNT > 0 일 경우만 예외2 처리됨. ");
					
					if(iCount > 0){
						
						ymCommonDAO dao1 = ymCommonDAO.getInstance();
					
						int iMaxCount = Integer.parseInt(sStackMaxQnty); 	// 적재 최대 수량 형변환 
						/*
						 * 2007.07.10 이정훈 
						 * Hysco 대차 고장
						 * 대차 도착 시 
						 */
						logger.println(LogLevel.DEBUG,this, "동          ==  "+tmpBay_Gp);
						logger.println(LogLevel.DEBUG,this, "대차번호 ==  "+tcTcNo);
						logger.println(LogLevel.DEBUG,this, "대차번호1== "+tcTcNo.substring(2));
						
						/*if ( "TC02".equals(tcTcNo.substring(2))) {
							for(int inx = 3; inx > 3-iMaxCount; inx--) {     
			                	
			                	dao1.modifyActiveStatOfLayer(YmCommonConst.STACK_LAYER_ACTIVE_STAT_O, 
			                								 tcCurrStopLoc, 
			                								 "0"+ (inx));
								}
						}
						else {*/
							for(int inx = 0; inx < iMaxCount; inx++) {     
		                	/* 적치 단 상태 변경
		                	 	UPDATE  TB_YM_STACKLAYER
								   SET  STACK_LAYER_ACTIVE_STAT = ?
								 WHERE  STACK_COL_GP = ?
								   AND  STACK_BED_GP = ? 
		                	*/
								dao1.modifyActiveStatOfLayer(YmCommonConst.STACK_LAYER_ACTIVE_STAT_O, 	/*적치 단 활성상태 : 활성*/
		                								 	 tcCurrStopLoc, 
		                								 	 "0"+ (inx + 1));
							}
						/*}*/
						/*
						 *	대차 작업 진행 상태 변경
						 *  S :  스케줄 수행 상태
						 */
						/*
							UPDATE TB_YM_EQUIP 
     						   SET WPROG_STAT = :WPROG_STAT  --작업진행 상태코드(W,S)
 							 WHERE EQUIP_GP   = :tcTcNo      --무인대차01호(2XTC01) 
						*/
						String uQeury = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateWprogStat";
						int uSeq      = ydStockDAO.requestupdateData(uQeury, new Object[]{YmCommonConst.WPROG_STAT_S,/*Schedule 수행*/
																						  tcTcNo });		
						
						logger.println(LogLevel.DEBUG,this,"대차도착처리 예외사항2=> 이미 도착처리모듈 처리");
						logger.println(LogLevel.DEBUG,this,"대차도착처리 예외사항2=> 적치단만 OPEN 및 대차상태 S로 셋팅");
						
						return false;
					}
				}		
				/**************************************************************
				*	대차도착처리 예외사항2 종료
				**************************************************************/
				
				//3.적재상태가 'L'일때 (상차)
				if (sStackStat.equals(YmCommonConst.STACK_STAT_L)){         //상차
					if (tmpLoadGp.equals(YmCommonConst.CARLOAD_ASSIGN_Y)) {   //작업지정
						logger.println(LogLevel.DEBUG,this,"3.적재상태가 'L'일때 (상차)");
						/* 
						 * 2007.04.10 이정훈
			    		 * B열연 Coil 야드 대차출하상차 작업 시 
			    		 *  1. 대차 도착 후 대차 출하하기 위해 이적한 물량 편성
			    		 *  2. 대차 도착 전 이적 편성 
			    		 *  
						 */
						String sLoadSchQueryId = "";
						List wBookid1 		= null; 
						
						if (YmCommonConst.NEW_SCH_WORK_KIND_CTFL.equals(tmpLoadSch)) //Coil 대차출하상차
						{
							// 상차 동 Crane Sch 편성 유무 확인 ( SCH_WORK_STAT IN ('1','S') )
							/*
							 	SELECT * 
								  FROM TB_YM_SCH
								 WHERE YD_GP = :yd_gp
								   AND BAY_GP = :bay_gp 
								   AND SCH_WORK_KIND = :sch_work_kind 
								   AND SCH_WORK_AID_YN = 'M' 
								   AND SCH_WORK_STAT IN ('1','S')
							 */
							JDTORecord wbookRc = CraneSchDAO.getCranInfoSchCTFL(tmpYd_Gp.trim(),
																				tmpBay_Gp.trim(),
																				tmpLoadSch.trim());
							if (wbookRc == null) {
								logger.println(LogLevel.DEBUG,this, "====대차 상차 동 출발 : 대차 출하하기 위해 이적한 물량 편성====");
								/*
								SELECT X.WBOOK_ID 
								FROM (  
							    		SELECT A.WBOOK_ID, 																		-- 작업 ID
							       		       A.SCH_WORK_KIND AS 작업종류,														-- 작업종류
							       		       NVL(B.STACK_COL_GP||B.STACK_BED_GP||B.STACK_LAYER_GP, 'UNKNOW') AS FROM_ADDR,	-- 출발주소
							       		       A.STOCK_ID AS 재료번호,															-- 재료번호
							       		       B.STACK_LAYER_STAT AS 적치상태 													-- 적치상태
									      FROM ( 
							       				SELECT A.WBOOK_ID, 				-- 작업 ID
							              			   A.SCH_WORK_KIND, 		-- 작업종류
							              			   C.STOCK_ID 				-- 저장품 ID
							            		FROM ( 
							            				SELECT 	WBOOK_ID, 		-- 작업 ID
							                     			   	YD_GP, 			-- 야드구분(1)
							                     				BAY_GP, 		-- 동구분('A', 'B', 'C' ...)
							                     				SCH_WORK_KIND 	-- 작업종류('CTCL', 'CSTU' ...)
							              				  FROM TB_YM_WBOOK 
											             WHERE YD_GP            = :tmpYd_Gp			-- 야드구분
											               AND BAY_GP LIKE        :tmpBay_Gp		-- 동구분
											               AND SCH_WORK_KIND LIKE :tmpLoadSch		-- 작업종류('CTCL', 'CSTU' ...)
							            	   		 ) A, TB_YM_SCHRULE B, TB_YM_STOCK C
							       				WHERE A.YD_GP = B.YD_GP 
									         	AND A.BAY_GP = B.BAY_GP 
									         	AND A.SCH_WORK_KIND = B.SCH_WORK_KIND
									         	AND A.WBOOK_ID = C.WBOOK_ID
							     			   ) A, TB_YM_STACKLAYER B, USRPMA.TB_PM_COILCOMM C
										  WHERE A.STOCK_ID = B.STOCK_ID(+) 
									  		AND B.STACK_LAYER_STAT = 'S'
									  		AND A.STOCK_ID = C.COIL_NO(+)
									  		AND B.STACK_COL_GP IN (
																	SELECT STACK_COL_GP 
																	  FROM USRYMA.TB_YM_LOCSEARCH
																	 WHERE STOCK_MOVE_ROUTE_ID  IN (
																						    			SELECT STOCK_MOVE_ROUTE_ID    
																						    			  FROM TB_YM_STOCKMOVEROUTE 
																						    		     WHERE YD_GP            = :tmpYd_Gp			-- 야드구분
																						                   AND BAY_GP LIKE        :tmpBay_Gp		-- 동구분
																						                   AND SCH_WORK_KIND LIKE :tmpLoadSch		-- 작업종류('CTCL', 'CSTU' ...)
																						    			   AND STACK_USAGE_CD_TO != 'CX'   			-- 대차정지위치(CX)
																						    	   )
																  )
										ORDER BY B.STACK_LAYER_GP DESC,A.WBOOK_ID
									) X
								WHERE ROWNUM = 1
								*/
								sLoadSchQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneWbookInfoCTFL_01";											
								wBookid1 = ydStockDAO.getListData(sLoadSchQueryId, 
										 						new Object[]{tmpYd_Gp, 
										 			  						tmpBay_Gp, 
										 			  						tmpLoadSch,
										 			  						tmpYd_Gp, 
										 			  						tmpBay_Gp, 
										 			  						tmpLoadSch});
								// wBookid1 :  작업ID | 작업종류 | 출발주소 | 재료번호 | 적치상태
								// 대차 도착 전 이적 편성
								if(wBookid1 == null || wBookid1.size() == 0 )
								{   
									logger.println(LogLevel.DEBUG,this, "====대차 상차 동 출발 ====");
									/*
									SELECT X.WBOOK_ID 
									FROM ( 
											SELECT 	A.WBOOK_ID, -- 작업 ID
									       			A.SCH_WORK_KIND AS 작업종류,
									       			NVL(B.STACK_COL_GP||B.STACK_BED_GP||B.STACK_LAYER_GP, 'UNKNOW') AS FROM_ADDR,
									       			A.STOCK_ID AS 재료번호,
									       			B.STACK_LAYER_STAT AS 적치상태 								-- 적치상태    
											  FROM ( 
									       			SELECT 	A.WBOOK_ID, 												-- 작업 ID
									              			A.SCH_WORK_KIND, 										-- 작업종류
									              			C.STOCK_ID 													-- 저장품 ID
									            	  FROM ( 
									            	  		SELECT WBOOK_ID, 										-- 작업 ID
									                     			YD_GP, 												-- 야드구분(1)
									                     			BAY_GP, 											-- 동구분('A', 'B', 'C' ...)
									                     			SCH_WORK_KIND 								-- 작업종류('CTCL', 'CSTU' ...)
									              			  FROM TB_YM_WBOOK 
									              			 WHERE YD_GP = ?										-- 야드구분
									                		   AND BAY_GP LIKE ?								-- 동구분
									                		   AND SCH_WORK_KIND LIKE ?					-- 작업종류('CTCL', 'CSTU' ...)
									            		   ) A, TB_YM_SCHRULE B, TB_YM_STOCK C
									       			 WHERE A.YD_GP = B.YD_GP 
									         		   AND A.BAY_GP = B.BAY_GP 
									         		   AND A.SCH_WORK_KIND = B.SCH_WORK_KIND
									         		   AND A.WBOOK_ID = C.WBOOK_ID
									     			) A, TB_YM_STACKLAYER B, USRPMA.TB_PM_COILCOMM C
											 WHERE A.STOCK_ID = B.STOCK_ID(+) 
									  		   AND B.STACK_LAYER_STAT = 'S'
									  		   AND A.STOCK_ID = C.COIL_NO(+)
									           AND B.STACK_COL_GP NOT IN (
																			SELECT STACK_COL_GP FROM USRYMA.TB_YM_LOCSEARCH
																			WHERE STOCK_MOVE_ROUTE_ID  IN (
																    										SELECT STOCK_MOVE_ROUTE_ID    
																							    			  FROM TB_YM_STOCKMOVEROUTE 
																							    			 WHERE YD_GP = ?-- 야드구분
																							    			   AND BAY_GP LIKE ?-- 동구분
																							    			   AND SCH_WORK_KIND LIKE ?-- 작업종류('CTCL', 'CSTU' ...)
																							    			   AND STACK_USAGE_CD_TO != 'CX'
																							    		  )
																		 )
											ORDER BY A.WBOOK_ID
									) X
									WHERE ROWNUM = 1
									*/									
									sLoadSchQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneWbookInfoCTFL_02";											
									wBookid1 = ydStockDAO.getListData(sLoadSchQueryId, 
											 						new Object[]{tmpYd_Gp, 
											 			  						tmpBay_Gp, 
											 			  						tmpLoadSch,
											 			  						tmpYd_Gp, 
											 			  						tmpBay_Gp, 
											 			  						tmpLoadSch});
								}
							}
						} else {	 
						// CGS 로그 추가.						
						logger.println(LogLevel.DEBUG,this, "==== 시작 : NOT CTFL: 대차출하상차가 아님 ====");	
						logger.println(LogLevel.DEBUG,this, "YD_GP        : "+tmpYd_Gp);
						logger.println(LogLevel.DEBUG,this, "BAY_GP[도착동] : "+tmpBay_Gp);	
						logger.println(LogLevel.DEBUG,this, "LoadSch      : "+tmpLoadSch);	
						sLoadSchQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectLoadSchNEW"; 
						wBookid1 		= ydStockDAO.getListData(sLoadSchQueryId, 
																 new Object[]{tmpYd_Gp, 
																 			  tmpBay_Gp, 
																 			  tmpLoadSch,
																 			  tcTcNo});
						}
						
						if (wBookid1 != null && wBookid1.size() > 0 ){
							logger.println(LogLevel.DEBUG,this, "wBookid1 확인(Not Null && > 0)");
							
							wBookidSchCall  = StringHelper.evl(((JDTORecord) wBookid1.get(0)).getFieldString("WBOOK_ID"), "");
							logger.println(LogLevel.DEBUG,this, "wBookidSchCall="+ wBookidSchCall);
						}
						logger.println(LogLevel.DEBUG,this, "==== 끝 : NOT CTFL : 대차출하상차가 아님 ====");	
					
					}else if (tmpLoadGp.equals(YmCommonConst.CARLOAD_ASSIGN_N)) { //작업 미지정 : 상차 미지정
						logger.println(LogLevel.DEBUG,this, "====시작 : 작업 미지정 : 상차 미지정 ====");	
						/* - 설비작업미지정이고 상차 Schedule Code가 없다면 작업예약 Table에서 상차 동간이적에 해당하는 Schedule 검색
					        ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectSchWorkKind
						     SELECT MIN(WBOOK_ID) AS WBOOK_ID
						       FROM TB_YM_WBOOK
						      WHERE SCH_WORK_KIND = 'CTML'      --(Schedule 작업종류)상차는 CTML
						        AND WBOOK_SCH_ACT_DDTT IS NULL
						        AND YD_GP = ? 
						        AND BAY_GP = ?
						 */
						if (tmpLoadSch.trim() == null && tmpLoadSch.trim().equals("")){
							// Coil 동간이적상차 : NEW_SCH_WORK_KIND_CTML
							//SELECT A.WBOOK_ID  AS WBOOK_ID,
		        	//			C.STACK_COL_GP||C.STACK_BED_GP AS BED,
		       		//			C.STACK_LAYER_GP AS LAYER
							//FROM TB_YM_WBOOK A,
							//     TB_YM_STOCK B,
							//     TB_YM_STACKLAYER C
							//WHERE A.YD_GP  			= :YD_GP
							//AND   A.BAY_GP 			= :BAY_GP 
							//AND   A.SCH_WORK_KIND 	= :SCH_WORK_KIND 
							//AND   NOT EXISTS (SELECT WBOOK_ID FROM TB_YM_SCH WHERE WBOOK_ID = A.WBOOK_ID)
							//AND   A.WBOOK_ID        = B.WBOOK_ID
							//AND   B.STOCK_ID        = C.STOCK_ID
							//AND   C.STACK_LAYER_STAT  IN ('L','S','U')
							//ORDER BY A.WBOOK_ID
							
							logger.println(LogLevel.DEBUG,this, "==지정된 대차 상차 작업이 없음. LoadSch가 없음.==");
							logger.println(LogLevel.DEBUG,this, "YD:"+tmpYd_Gp+" | BAY:"+tmpBay_Gp+" | SCHCD:"+YmCommonConst.NEW_SCH_WORK_KIND_CTML);

							String selectLoadSchQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectLoadSchNEW";
							JDTORecord wBookid2         = ydStackLayerDAO.requestgetData(selectLoadSchQueryId, new Object[]{ 
									                      tmpYd_Gp.trim(), tmpBay_Gp.trim(), YmCommonConst.NEW_SCH_WORK_KIND_CTML ,tcTcNo });	
							
							if (wBookid2 != null){
								wBookidSchCall  = StringHelper.evl(wBookid2.getFieldString("WBOOK_ID"), "");
							}
							logger.println(LogLevel.DEBUG,this, "RecData : " + wBookid2);
						}
					}
					
 			    // 4.적재상태가 'U'일때 (하차)
				}else if (sStackStat.equals(YmCommonConst.STACK_STAT_U)){		// 하차 CGS
					logger.println(LogLevel.DEBUG,this, "==대차 도착 작업 - 하차작업 시작 ===================");
					/* 현재 대차에 실려있는 작업 대상재 점검
					ym.steelinfo.steelinforecv.dao.YdStockDAO.selectStock
					SELECT STOCK_ID ,                 --저장품ID
				           FRTOMOVE_EQUIP_BED_GP ,    --이송설비번지
				           FRTOMOVE_EQUIP_LAYER_GP,   --이송설비단
				           STOCK_MOVE_TERM
				      FROM TB_YM_STOCK
				     WHERE FRTOMOVE_EQUIP_GP = ?      --확장대차(1XTC03)
				     ORDER BY FRTOMOVE_EQUIP_BED_GP,FRTOMOVE_EQUIP_LAYER_GP
				  	*/
					logger.println(LogLevel.DEBUG,this, "==현재 대차에 실려있는 작업 대상재 점검 ===================");
					String selectStockQuery  = "ym.steelinfo.steelinforecv.dao.YdStockDAO.selectStock";
					List StockId             = ydStockDAO.getListData(selectStockQuery, new Object[]{ tcTcNo });					
					
					JDTORecord TmpSelBedAddr = null;
					int MaxRec               = StockId.size();	
					String[] tmpStockID      = new String[MaxRec];
					String[] tmpFromEquipBedGp    	= new String[MaxRec];
					String[] tmpFromEquipLayerGp  	= new String[MaxRec];
					String[] tmpFromStockMoveTerm  	= new String[MaxRec];

					for (int ii=0; ii<MaxRec; ii++){
						TmpSelBedAddr            = (JDTORecord) StockId.get(ii);
						tmpStockID[ii]           = StringHelper.evl(TmpSelBedAddr.getFieldString("STOCK_ID"), "");
						tmpFromEquipBedGp[ii]    = StringHelper.evl(TmpSelBedAddr.getFieldString("FRTOMOVE_EQUIP_BED_GP"), "");
						tmpFromEquipLayerGp[ii]  = StringHelper.evl(TmpSelBedAddr.getFieldString("FRTOMOVE_EQUIP_LAYER_GP"), "");
						tmpFromStockMoveTerm[ii] = StringHelper.evl(TmpSelBedAddr.getFieldString("STOCK_MOVE_TERM"), "");
						//=============================== CGS 로그 추가
						logger.println(LogLevel.DEBUG,this, "StockID:                  "+tmpStockID[ii]);
						logger.println(LogLevel.DEBUG,this, "FRTOMOVE_EQUIP_BED_GP:    "+tmpFromEquipBedGp[ii]);
						logger.println(LogLevel.DEBUG,this, "FRTOMOVE_EQUIP_LAYER_GP:  "+tmpFromEquipLayerGp[ii]);
						logger.println(LogLevel.DEBUG,this, "STOCK_MOVE_TERM:          "+tmpFromStockMoveTerm[ii]);
						//===============================
					}
					
					// 공통에서 진도코드를 검색해서 저장품 이동조건을 셋팅한다.
					/*
					SELECT
						PLANT_GP 					AS 공장구분,
						ORD_NO 						AS 제작번호,
						ORD_DTL 					AS 제작행번,
						COIL_T 						AS 코일두께,
						COIL_W 						AS 코일폭,
						CURR_COIL_LEN				AS 코일길이,
						COIL_INDIA 					AS 코일내경,
						COIL_OUTDIA 				AS 코일외경,
						COIL_WT 					AS 코일중량,
						NEXT_PROC 					AS 차공정,
		                PLAN_PROC1      			AS 계획공정,
				    	BRANCH_CD 					AS 분기위치코드,
				    	EXTEND_CONVEYOR_BRANCH_CD 	AS 확장분기위치코드,
				    	HYSCO_TRANS_GP 				AS HYSCO이송수단,
				    	COOL_METHOD 				AS 냉각방법,
						CURR_PROG_CD,
						RETURN_GP
					FROM  USRPMA.TB_PM_COILCOMM 
					WHERE COIL_NO = :COIL_NO   -- 재료번호(HE00001)
					*/
					//===============================
					// CGS 추가
					logger.println(LogLevel.DEBUG,this, "공통에서 진도코드를 검색해서 저장품 이동조건을 셋팅 "+String.valueOf(MaxRec));
					String sProgCd = "";
					String sStocMv = "";
					//===============================
					for (int ii=0; ii<MaxRec; ii++){
						String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(tmpStockID[ii],"");
					  	sProgCd   	  = sStockInfo[0];	// CURR_PROG_CD
					  	sStocMv   	  = sStockInfo[1];	// RETURN_GP
						tmpFromStockMoveTerm[ii] = sStocMv;
						//=============================== CGS 로그 추가
						logger.println(LogLevel.DEBUG,this, "저장품정보["+String.valueOf(ii)+"]="+sStockInfo[0] +" / "+sStockInfo[1]);
						logger.println(LogLevel.DEBUG,this, "CURR_PROG_CD + RETURN_GP : "+sProgCd +" / "+ sStocMv);
						logger.println(LogLevel.DEBUG,this, "STOCK_MOVE_TERM(저장품이동조건) : "+tmpFromStockMoveTerm[ii]);
						//===============================
						
					}
					
					logger.println(LogLevel.DEBUG,this, "Coil 공통 Table 저장위치 Update "+String.valueOf(MaxRec));
					for (int ii=0; ii<MaxRec; ii++){
						/*
						 * Coil 공통 Table 저장위치 Update 
						 */
						/*
						Ver4.0--
						UPDATE tb_pm_coilcomm
						SET(  
						    yd_gp,              -- 야드구분
						    bay,                -- 동
						    span,               -- SPAN
						    col,                -- 적치열번지
						    cellno,             -- 적치번지
						    stack_layer,        -- 적치단
						    store_loc_cd,       -- 현 저장위치코드
						    befo_store_loc_cd   -- 전 저장위치코드
						   )=
						   (
						    SELECT 
						        substr(:pos,1,1),-- 야드구분
						        substr(:pos,2,1),-- 동
						        substr(:pos,3,2),-- SPAN
						        substr(:pos,5,2),-- 적치열번지
						        substr(:pos,7,2),-- 적치번지
						        substr(:pos,9,2),-- 적치단
						        :pos,            -- 현 저장위치코드   
						        store_loc_cd     -- 전 저장위치코드
						    FROM tb_pm_coilcomm
						    WHERE coil_no = :coil_no
						   )
						WHERE coil_no = :coil_no
						*/
						//=============================== CGS 로그 추가
						logger.println(LogLevel.DEBUG,this, "==대차 도착 작업 Coil공통Table 등록 ===================");
						logger.println(LogLevel.DEBUG,this, "저장품ID : "+tmpStockID[ii]);
						logger.println(LogLevel.DEBUG,this, "현 저장위치정보: "+tcCurrStopLoc + tmpFromEquipBedGp[ii] + tmpFromEquipLayerGp[ii]);
						
						iReq = CraneSchDAO.updateCoilCommonLocInfo(tmpStockID[ii],
								                                   tcCurrStopLoc + tmpFromEquipBedGp[ii] + tmpFromEquipLayerGp[ii]);
						/*
						 * 대차 도착 작업 실적 등록
						 */
						//=============================== CGS 로그 추가
						logger.println(LogLevel.DEBUG,this, "==대차 도착 작업 실적 등록 ===================");
						logger.println(LogLevel.DEBUG,this, "상차위치=   "+ tmpLoadStopLoc + tmpFromEquipBedGp[ii] + tmpFromEquipLayerGp[ii]);
						logger.println(LogLevel.DEBUG,this, "현재정지위치="+ tcCurrStopLoc + tmpFromEquipBedGp[ii] + tmpFromEquipLayerGp[ii]);
						logger.println(LogLevel.DEBUG,this, "하차위치=   "+ tmpUnLoadSch);
						logger.println(LogLevel.DEBUG,this, "야드구분=   "+ tmpYd_Gp);
						logger.println(LogLevel.DEBUG,this, "대차코드=   "+ tcTcNo);
						
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

						iReq = insertUpPutWrslRtData(tmpStockID[ii].trim(), 
						 		                      tmpLoadStopLoc + tmpFromEquipBedGp[ii] + tmpFromEquipLayerGp[ii], 
						 		                      tcCurrStopLoc + tmpFromEquipBedGp[ii] + tmpFromEquipLayerGp[ii], 
						 		                      tmpUnLoadSch.trim(), 
													  tmpYd_Gp.trim(),
													  tcTcNo);
					}
					
					/* -(공통)하차작업이면 도착위치 Map에 등록
					 * 대차출발위치차량번호변경	
						ym.facilitystatus.facilityinquiry.dao.YdStackColDAO.updateStockColArrive
						UPDATE TB_YM_STACKCOL 
						   SET STACK_COL_ARRIVE_CAR_NO = null,
						       STACK_COL_ARRIVE_CAR_STAT = null        
						 where STACK_COL_GP = (select CARLOAD_STOP_LOC 
						                         from TB_YM_EQUIP
						                        where EQUIP_GP = ? )  --확장대차(1XTC03)
					*/
					String updateArriveQuery = "ym.facilitystatus.facilityinquiry.dao.YdStackColDAO.updateStockColArrive";
					int updateColArrive = ydStackColDAO.requestupdateData(updateArriveQuery, new Object[]{ tcTcNo });

					logger.println(LogLevel.DEBUG,this, "1=====================");
					logger.println(LogLevel.DEBUG,this, "tcTcNo="+ tcTcNo);
					logger.println(LogLevel.DEBUG,this, "1=====================");
					
					/* -(공통)대차도착위치정지포인트변경	                   
						ym.facilitystatus.facilityinquiry.dao.YdStackColDAO.updateStockColArriveCarNo
						UPDATE TB_YM_STACKCOL 
						   SET STACK_COL_ARRIVE_CAR_NO = (select CURR_STOP_LOC 
						                                    from TB_YM_EQUIP
						                                   where EQUIP_GP = :tcTcNo ),  --확장대차(1XTC03)
						       STACK_COL_ARRIVE_CAR_STAT = 'T'                                      
						 where STACK_COL_GP = (select CARUNLOAD_STOP_LOC 
						                         from TB_YM_EQUIP
						                        where EQUIP_GP = :tcTcNo )  --확장대차(1XTC03)
					 */
					logger.println(LogLevel.DEBUG,this, "== (공통)대차도착위치정지포인트변경	");
					String updateArriveCarNoQuery = "ym.facilitystatus.facilityinquiry.dao.YdStackColDAO.updateStockColArriveCarNo";
					int updateColArriveCarNo = ydStackColDAO.requestupdateData(updateArriveCarNoQuery, new Object[]{ tcTcNo, tcTcNo });								
					
					/* -(공통)저장품이송설비업데이트(저장품갯수만큼LOOP)	
						ym.steelinfo.steelinforecv.dao.YdStockDAO.updateFromEquipGp
						UPDATE TB_YM_STOCK 
						   SET FRTOMOVE_EQUIP_GP = (select CARUNLOAD_STOP_LOC 
						                              from TB_YM_EQUIP
						                             where EQUIP_GP = :tcTcNo )  --확장대차(1XTC03)
						 WHERE STOCK_ID = :stockId   --저장품ID                     
					 */
					for (int ii=0; ii<MaxRec; ii++){
						logger.println(LogLevel.DEBUG,this, "== (공통)저장품이송설비업데이트(저장품갯수만큼LOOP)	");
						if (tmpStockID[ii].trim() != null && !tmpStockID[ii].trim().equals("")){
							logger.println(LogLevel.DEBUG,this, "설비코드:  "+tcTcNo);
							logger.println(LogLevel.DEBUG,this, "저장품 :  "+tmpStockID[ii]);
							String updateFromEquipGpQuery = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateFromEquipGp";
							int updateFromEquipGp = ydStockDAO.requestupdateData(updateFromEquipGpQuery, new Object[]{ 
									                tcTcNo, tmpStockID[ii].trim() });
						}
					}
					
					/* -(공통)대차출발위치 CLEAR
						ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateClearStockId
						update TB_YM_STACKLAYER 
						   set STOCK_ID = null,
						       STACK_LAYER_STAT = 'E'
						 where STACK_COL_GP = (select CARLOAD_STOP_LOC 
						                         from TB_YM_EQUIP
						                        where EQUIP_GP = ? )   --확장대차(1XTC03)
					 */
					logger.println(LogLevel.DEBUG,this, "== (공통)대차출발위치 CLEAR ");
					String updateClearStockIdQuery = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateClearStockId_01";
					int updateClearStockId = ydStackLayerDAO.requestupdateData(updateClearStockIdQuery, new Object[]{ tcTcNo });
					
					/* -(공통)대차도착위치 저장품업데이트(저장품갯수만큼반복)		
						ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateUnLoadStopLocStock
						update TB_YM_STACKLAYER 
						   set STOCK_ID                = ?,   --STOCK_ID
						       STACK_LAYER_STAT        = 'L'  -- 적치중 'L'
						 where STACK_COL_GP = (select CARUNLOAD_STOP_LOC 
						                         from TB_YM_EQUIP
						                        where EQUIP_GP = ? )   --확장대차(1XTC03)
						   and STACK_BED_GP   = ?   --FRTOMOVE_EQUIP_BED_GP
						   and STACK_LAYER_GP = ?   --FRTOMOVE_EQUIP_LAYER_GP  					 
					 */
					for (int ii=0; ii<MaxRec; ii++){
						if (tmpStockID[ii].trim() != null && !tmpStockID[ii].trim().equals("")){
							String updateUnLoadStopLocQuery = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateUnLoadStopLocStock";
							int updateUnLoadStopLoc = ydStockDAO.requestupdateData(updateUnLoadStopLocQuery, new Object[]{ 
									      tmpStockID[ii].trim(), tcTcNo, tmpFromEquipBedGp[ii].trim(), tmpFromEquipLayerGp[ii].trim() });

							logger.println(LogLevel.DEBUG,this, "(공통)대차도착위치 저장품업데이트(저장품갯수만큼반복)=======================");
							logger.println(LogLevel.DEBUG,this, "Loop Index  ="+ String.valueOf(ii) );
							logger.println(LogLevel.DEBUG,this, "StockID     ="+ tmpStockID[ii].trim());
							logger.println(LogLevel.DEBUG,this, "BedGp       ="+ tmpFromEquipBedGp[ii].trim());
							logger.println(LogLevel.DEBUG,this, "EquipLayerGp="+ tmpFromEquipLayerGp[ii].trim());
							logger.println(LogLevel.DEBUG,this, "=============================================");
						}
					}
					
					/* 목적동에 도착시 현재 수량과 가능수량을 조절한다.
				    Select STACK_BED_QNTY_MAX from TB_YM_STACKER 
					 where STACK_COL_GP = (select CARUNLOAD_STOP_LOC 
					                         from TB_YM_EQUIP
					                        where EQUIP_GP = tcTcNo )   --확장대차(1XTC03)
					   and STACK_BED_GP = tmpFromEquipBedGp[ii]
					     
					update TB_YM_STACKER 
					   set STACK_BED_QNTY_CURR = STACK_BED_QNTY_MAX
					       STACK_BED_ABLE_QNTY = '0'
					 where STACK_COL_GP = (select CARUNLOAD_STOP_LOC 
					                         from TB_YM_EQUIP
					                        where EQUIP_GP = tcTcNo )   --확장대차(1XTC03)
					   and STACK_BED_GP = tmpFromEquipBedGp[ii]
				    */
					logger.println(LogLevel.DEBUG,this, "목적동에 도착시 현재 수량과 가능수량을 조절");
					for (int ii=0; ii<MaxRec; ii++){
						String selectBedMax = "ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.selectBedMax";
						JDTORecord bedMax   = ydStackLayerDAO.requestgetData(selectBedMax, new Object[]{ 
								              tcTcNo, tmpFromEquipBedGp[ii].trim()  });	
						
						String TmpStackerBedMax  = StringHelper.evl(bedMax.getFieldString("STACK_BED_QNTY_MAX"), "");
						
						logger.println(LogLevel.DEBUG,this, "적치 대 Max : "+TmpStackerBedMax);
						
						if (TmpStackerBedMax != null){
							/*
							 update TB_YM_STACKER 
					   		set STACK_BED_QNTY_CURR = :TmpStackerBedMax				 --
					       		STACK_BED_ABLE_QNTY = :'0'
					 		where STACK_COL_GP = (select CARUNLOAD_STOP_LOC 
					                         		from TB_YM_EQUIP
					                        		where EQUIP_GP = :tcTcNo )   --확장대차(1XTC03)
					   		  and STACK_BED_GP = :tmpFromEquipBedGp[ii]
							 */
							String updatestackctSet = "ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.updateStackCountSet";
							int updateStackerCTSet = ydStackLayerDAO.requestupdateData(updatestackctSet, new Object[]{ 
									                 TmpStackerBedMax.trim(), "0", tcTcNo, tmpFromEquipBedGp[ii].trim() });										
						}
					}

					/*
					 * HYSCO 대차 하차일경우는 작업예약을 해선는 않됨 tcCurrStopLoc = 3HTC02
					 */
					if (!tcCurrStopLoc.trim().equals(YmCommonConst.HYSCO_3HTC02)){
						
						String wStockId  = "";
						String wSchCode  = "";
						String wStockMv  = "";
						
						if (tmpUnLoadGp.equals(YmCommonConst.CARLOAD_ASSIGN_Y)) {	    //작업 지정
							
							wSchCode = tmpUnLoadSch.trim();
							
						}else if (tmpUnLoadGp.equals(YmCommonConst.CARLOAD_ASSIGN_N)) {	//작업 미지정
							
							wSchCode = YmCommonConst.NEW_SCH_WORK_KIND_CTMU;
						}
						
						
						logger.println(LogLevel.DEBUG,this, "==스케줄 코드= "+wSchCode);
						JDTORecord wBookSel 	= null;
						
						String sQueryId 		= "";					// Query ID
						String sWbookId			= "";					// 작업예약ID
						String sCarunloadBay 	= "";					// 하차PUT위치 변수
						
						int iSeq = 0;
						
						CraneSchDAO dao2 = new CraneSchDAO();
						ymCommonDAO dao1 = ymCommonDAO.getInstance();
						// 적재된 코일의 수만큼 처리한다.
						for (int index = 0; index < MaxRec; index++){
							// 저장품 ID 존재 검사.
							
							list.clear(); // list 초기화.
							if ( tmpStockID[index].trim() != null && !tmpStockID[index].trim().equals(""))
							{
								wStockId  = tmpStockID[index].trim();
								wStockMv  = tmpFromStockMoveTerm[index].trim();
						
								/*
								 *	YJK.20060613.	
								 *	작업예약 생성시 저장품TABLE에 CARUNLOAD_PUT_LOC 항목에 
								 *	특정위치값이 존재하면 OPERATION 지정으로 작업예약을 생성한다.
								 */
								
								//Ver4.2--
								//SELECT *
								//FROM tb_ym_stock
								//WHERE stock_id = :stock_id

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
									/*
									INSERT INTO TB_YM_WBOOK (
									            WBOOK_ID, 
									            YD_GP, 
									            BAY_GP,
									            SCH_WORK_KIND, 
									            SCH_WORK_LOC_DECISION_METHOD,
									            CRANE_WORD_PUT_LOC, 
									            WBOOK_DDTT, 
									            WBOOK_DUTY,
									            WBOOK_PARTY, 
									            WBOOK_SCH_TERM, 
									            WBOOK_SCH_ACT_DDTT,
									            REGISTER, 
									            REG_DDTT, 
									            MODIFIER, 
									            MOD_DDTT, 
									            DEL_YN)
									VALUES (?, ?, ?, ?, 'S', null, to_char(sysdate,'YYYYMMDDHH24MI'), ?, ?, 'T', to_char(sysdate,'YYYYMMDDHH24MI'), 'SYSTEM', sysdate, null, null, 'N')
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
								if(index == 0){
									wBookidSchCall  = sWbookId;		
								}			
								 
								/*
								 *	적치단  Table Update(작업요구상태='S'로 변경)
								 *	UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'S' WHERE STOCK_ID = ?
								 */
								sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
								iSeq 	 = ydStackLayerDAO.requestupdateData(sQueryId, 
																			 new Object[]{YmCommonConst.STACK_LAYER_STAT_S, wStockId});							
								
								/* 
								 *  저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
								 *  UPDATE TB_YM_STOCK 
								 *  SET WBOOK_ID= ?, 
								 *      STOCK_MOVE_TERM = ? 
								 *  WHERE STOCK_ID = ?
								 *  저장품이동조건	SD 대차상차대기,  SE 대차하차대기						  
								 */
								sQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
								iSeq 	 = ydStockDAO.requestupdateData(sQueryId, 
																	 new Object[]{ 
										    							sWbookId, 
										    							wStockMv, 
										    							wStockId});		
							}						
						}
												
					}
				}
				
				
				/*
				 *	4.12	대차도착했을때 상차 하차 구분에 따라 
				 *			STACK_LAYER_ACTIVE_STAT, 
				 *			STACK_LAYER_STAT 업데이트
				 */
				// CGS 추가
				// 스케줄코드가 하차일 때만 상하차에 대한 STAT를 변경한다.
				
				{
					logger.println(LogLevel.DEBUG,this, "상,하차 구분="+ sStackStat.trim());
					
					ymCommonDAO dao1 = ymCommonDAO.getInstance();
					
					int iMaxCount = Integer.parseInt(sStackMaxQnty);
					/*
					 * 2007.07.10 이정훈 
					 * Hysco 대차 고장
					 */
					logger.println(LogLevel.DEBUG,this, "동"+tmpBay_Gp);
					logger.println(LogLevel.DEBUG,this, "대차번호"+tcTcNo);
					/*
					 * 2007.07.10 이정훈 
					 * Hysco 대차 고장
					 * 대차 도착 시 
					 */
					logger.println(LogLevel.DEBUG,this, "동     ==  "+tmpBay_Gp);
					logger.println(LogLevel.DEBUG,this, "대차번호==  "+tcTcNo);
					logger.println(LogLevel.DEBUG,this, "대차번호1== "+tcTcNo.substring(2));
					
					/*if ( "TC02".equals(tcTcNo.substring(2))) {
						for(int inx = 3; inx > 3-iMaxCount; inx--) {     
		                	
		                	dao1.modifyActiveStatOfLayer(YmCommonConst.STACK_LAYER_ACTIVE_STAT_O, 
		                								 tcCurrStopLoc, 
		                								 "0"+ (inx ));
							}
					}
					else {*/
						logger.println(LogLevel.DEBUG,this, "==== 적치 단 활성 상태를 UPDATE==== ");
						for(int inx = 0; inx < iMaxCount; inx++) {     
	                	/*
    						--ym.common.dao.updateActiveStatOfLayer2
							--적치 단 활성 상태를 UPDATE
							UPDATE  TB_YM_STACKLAYER
							   SET  STACK_LAYER_ACTIVE_STAT = ?
							 WHERE  STACK_COL_GP = ?
							   AND  STACK_BED_GP = ?
				        */
	                	dao1.modifyActiveStatOfLayer(YmCommonConst.STACK_LAYER_ACTIVE_STAT_O, 
	                								 tcCurrStopLoc, 
	                								 "0"+ (inx + 1));
						}
					/*}*/         
					/*
					 *	4.12.1	하차시 하차동MAP을 활성화.
					 */
					if (sStackStat.equals(YmCommonConst.STACK_STAT_U)){         
						logger.println(LogLevel.DEBUG,this, "==== 하차시 하차동MAP을 활성화.==== STACK_STAT: "+YmCommonConst.STACK_STAT_U);
					/*
					 *	4.12.1	상차및 IDLE시 상차동MAP을 활성화 및 적치가능 상태로 셋팅.
					 */
					}else{
						logger.println(LogLevel.DEBUG,this, "==== 상차및 IDLE시 상차동MAP을 활성화 및 적치가능 상태로 셋팅.==== ");                                 
				        /*
					        --ym.common.dao.updateStockStatOfLayer1
							--'저장품ID', '적치단 상태' 항목을 UPDATE
							UPDATE  TB_YM_STACKLAYER
							   SET  STOCK_ID = ?,
						            STACK_LAYER_STAT = ?
							 WHERE  STACK_COL_GP = ?
				        */
						dao1.modifyStockStatOfLayer("", 
													YmCommonConst.STACK_LAYER_STAT_E, 
													tcCurrStopLoc);	   															 			  
					}
				}
				
				/*
				 *	5.	Coil Schedule Call
				 */ 
				if (wBookidSchCall != null && !wBookidSchCall.trim().equals("")){
					/*
					6.대차 작업 진행 상태 변경
					  - 설비 Table의 차량작업상태 Code를 조건에 맞게 변경
					    ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateWprogStat 
						update TB_YM_EQUIP 
						   set WPROG_STAT = ?  --작업진행 상태코드(W,S)
						 where EQUIP_GP   = ?  --무인대차01호(3XTC01)
					*/
					logger.println(LogLevel.DEBUG,this, "==대차 작업 진행 상태 변경.==== ");
					String updateWprogStatQuery = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateWprogStat";
					int WprogStat               = ydStockDAO.requestupdateData(updateWprogStatQuery, new Object[]{ 
							                      YmCommonConst.WPROG_STAT_S, tcTcNo });				
					
					
					
					logger.println(LogLevel.DEBUG,this, "5.	Coil Schedule Call :"+wBookidSchCall);
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					Boolean isTrue       = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class},new Object[]{ 
							               wBookidSchCall.trim() });					
				}else{
					/*
					update TB_YM_EQUIP 
					     set WPROG_STAT = ?  --작업진행 상태코드(W,S)
					 where EQUIP_GP      = ?  --무인대차01호(2XTC01)
					 */
					//  YmCommonConst.WPROG_STAT_W  //IDEL, Schedule  대기
					logger.println(LogLevel.DEBUG,this, "==대차 작업 진행 상태 변경.==== ");
					String updateWprogStatQuery = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateWprogStat";
					int WprogStat               = ydStockDAO.requestupdateData(updateWprogStatQuery, new Object[]{ 
							                      YmCommonConst.WPROG_STAT_W, tcTcNo });								
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
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean bookDummyWorkInfo_02(JDTORecord jDTORecord){ 
        
        logger.println(LogLevel.DEBUG,this,"Start-bookDummyWorkInfo_02()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		CraneSchDAO CraneSchDAO         = new CraneSchDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iResult 			= 0;
			int iReq 				= 0;
			
			// 야드 Level-2에서 대차 도착 정보를  수신			
			String tcTcNo        = StringHelper.evl(jDTORecord.getFieldString("대차번호"), "");   // 설비구분 : TC(2) + TC NO(2)
				   tcTcNo 		 = YmCommonConst.TC_3X + tcTcNo;          				  		  // B열연 COIL 대차		
			String tcCurrStopLoc = StringHelper.evl(jDTORecord.getFieldString("대차도착동"), ""); // YARD구분(1) + 동구분(1)+ SPAN(2) + 열 NO(2)

			String tmpYd_Gp  	 = tcCurrStopLoc.substring(0, 1); // 야드구분
			String tmpBay_Gp 	 = tcCurrStopLoc.substring(1, 2); // 동구분
			 
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
				
				logger.println(LogLevel.DEBUG,this,"대차도착=>대차정보 고장및 OFFLINE 처리="+sEquipStat+sWorkMode);
				return false;
			}
			
			if(sCurrStopLoc.length() > 2){
				sCurrStopBay  = sCurrStopLoc.substring(1,2); // 1BTC03 -> B
			}
			if (tmpBay_Gp.trim().equals(sCurrStopBay)){
				logger.println(LogLevel.DEBUG,this,"대차도착=>대차 도착동이 현재 도착동과 같습니다.");
				//return false;
			}
							
			/**************************************************************
			 *	대차도착처리 예외사항1 시작
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
			 *	대차도착처리 예외사항1 종료
			 **************************************************************/
			
			/**
			 *	도착처리시 다른동 적치단 활성상태를 모두 비활성화 한다.
			 */
		   /*	
			* String tmpTcNo = "3_"+StringHelper.evl(jDTORecord.getFieldString("대차번호"), ""); 
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
			}else if (sStackStat.equals(YmCommonConst.STACK_STAT_U)){   
			    /*	
				 * if (!tmpBay_Gp.trim().equals(tmpUnLoadStopLoc.trim().substring(1, 2))){
				 *  	throw new EJBServiceException("대차도착 에러 : 대차하차 도착동이 목적동이 아닙니다.");
				 * }											
				 */
			}
			
		   /*
			*	2	COIL 대차의 현재위치를 변경한다.
			*/
		   /*
			* String updateCurrQueryId = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateLoadCurrLoc";
			* int CurrStop             = ydStackLayerDAO.requestupdateData(updateCurrQueryId, 
			*															 new Object[]{tcCurrStopLoc, 
			*															 			  tcTcNo });										
			*/
			
			
			int iMaxCount 		= Integer.parseInt(sStackMaxQnty);
			String[] tmpWbookID	= null;
			
			/*
			 *	3	대차도착했을때 상차 하차 구분에 따라 
			 *		STACK_LAYER_ACTIVE_STAT, 
			 *		STACK_LAYER_STAT 업데이트
		     */
			{
				logger.println(LogLevel.DEBUG,this, "상,하차 구분="+ sStackStat.trim());
				
				ymCommonDAO dao1 = ymCommonDAO.getInstance();
			
				/*
				 * 2007.07.10 이정훈 
				 * Hysco 대차 고장
				 * 대차 도착 시 
				 */
				logger.println(LogLevel.DEBUG,this, "동         ==  "+tmpBay_Gp);
				logger.println(LogLevel.DEBUG,this, "대차번호==  "+tcTcNo);
				logger.println(LogLevel.DEBUG,this, "대차번호1== "+tcTcNo.substring(2));
				
				/*if ( "TC02".equals(tcTcNo.substring(2))) {
					for(int inx = 3; inx > 3-iMaxCount; inx--) {     
	                	
	                	dao1.modifyActiveStatOfLayer(YmCommonConst.STACK_LAYER_ACTIVE_STAT_O, 
	                								 tcCurrStopLoc, 
	                								 "0"+ (inx));
						}
				}
				else {*/
				
					for(int inx = 0; inx < iMaxCount; inx++) {     
                	
						dao1.modifyActiveStatOfLayer(YmCommonConst.STACK_LAYER_ACTIVE_STAT_O, 
                								 tcCurrStopLoc, 
                								 "0"+ (inx + 1));
					}
				/*}*/           
				/*
				 *	4.12.1	상차및 IDLE시 상차동MAP을 활성화 및 적치가능 상태로 셋팅.
				 */
				{                                 
					logger.println(LogLevel.DEBUG,this, "상차및 IDLE시 상차동MAP을 활성화 및 적치가능 상태로 셋팅.");
					dao1.modifyStockStatOfLayer("", 
												YmCommonConst.STACK_LAYER_STAT_E, 
												tcCurrStopLoc);	   															 			  
				}
			}	
		    /*
		     *	4	적재상태가 'U'(하차) 일때 
		     */
			{
				String sLoadSchQueryId 	= "";
				List wBookid1 			= null;
				JDTORecord wbookRc    = null;
				logger.println(LogLevel.DEBUG,this, "상차지정 Sch"+tmpLoadSch);
				if (tmpLoadGp.equals(YmCommonConst.CARLOAD_ASSIGN_Y)) {   //작업지정
					 
					/* 
					 * 2007.04.10 이정훈
		    		 * B열연 Coil 야드 대차출하상차 작업 시 
		    		 *  1. 대차 도착 후 대차 출하하기 위해 이적한 물량 편성
		    		 *  2. 대차 도착 전 이적 편성 
		    		 *  
					 */
					if (YmCommonConst.NEW_SCH_WORK_KIND_CTFL.equals(tmpLoadSch)) 
					{
						// 상차 동 Crane Sch 편성 유무 확인 (SCH_WORK_STAT IN ('1','S')
						wbookRc = CraneSchDAO.getCranInfoSchCTFL(tmpYd_Gp,
								  								 tmpBay_Gp,
								  								 tmpLoadSch);
						
						
						
						if (wbookRc == null) {
							
							logger.println(LogLevel.DEBUG,this, "====대차 상차 동 출발 : 대차 출하하기 위해 이적한 물량 편성====");
							sLoadSchQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneWbookInfoCTFL_01";											
							wBookid1 = ydStockDAO.getListData(sLoadSchQueryId, 
									 						new Object[]{tmpYd_Gp, 
									 			  						tmpBay_Gp, 
									 			  						tmpLoadSch,
									 			  						tmpYd_Gp, 
									 			  						tmpBay_Gp, 
									 			  						tmpLoadSch});
							// 대차 도착 전 이적 편성
							if(wBookid1 == null || wBookid1.size() == 0 )
							{   
								logger.println(LogLevel.DEBUG,this, "====대차 상차 동 출발 ====");
								sLoadSchQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneWbookInfoCTFL_02";											
								wBookid1 = ydStockDAO.getListData(sLoadSchQueryId, 
										 						new Object[]{tmpYd_Gp, 
										 			  						tmpBay_Gp, 
										 			  						tmpLoadSch,
										 			  						tmpYd_Gp, 
										 			  						tmpBay_Gp, 
										 			  						tmpLoadSch});
							}
						}
					} else {
						 //확장대차 상차지 상차작업예약을 호출하지 않는다.
						if(!tmpLoadSch.equals("CTM5")&&!tmpLoadSch.equals("CTM6")&&!tmpLoadSch.equals("CTM7")){
							sLoadSchQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectLoadSchNEW";						 
							wBookid1 = ydStockDAO.getListData(sLoadSchQueryId , new Object[]{tmpYd_Gp, tmpBay_Gp, tmpLoadSch,tcTcNo});
						}
						logger.println(LogLevel.DEBUG , this , "작업지정,데이터 검사: " + wBookid1);
					}
				}else if (tmpLoadGp.equals(YmCommonConst.CARLOAD_ASSIGN_N)) { //작업 미지정

					if (tmpLoadSch.trim() == null || tmpLoadSch.trim().equals("")){
						
						sLoadSchQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectLoadSchNEW"; 
						wBookid1 = ydStockDAO.getListData(sLoadSchQueryId , new Object[]{tmpYd_Gp.trim(), tmpBay_Gp.trim(), YmCommonConst.NEW_SCH_WORK_KIND_CTML,tcTcNo });	
					}
					logger.println(LogLevel.DEBUG,this, "작업미지정,데이터 검사: " + wBookid1);
				}
				
				JDTORecord TmpSelBedAddr    = null;
				int MaxRec = 0;
				
				if (wBookid1 != null && wBookid1.size() > 0) {
				
					MaxRec                  = iMaxCount > wBookid1.size() ?  wBookid1.size() : iMaxCount;	
					tmpWbookID            		= new String[MaxRec];
				
				
					logger.println(LogLevel.DEBUG,this, "wBookid1.size() "+wBookid1.size());
				/*
				 *	3.1.1	COIL 작업예약 리스트를 작성한다.
				 */
				
					for (int inx = 0; inx < MaxRec; inx++){
						TmpSelBedAddr	 = (JDTORecord) wBookid1.get(inx);
						tmpWbookID[inx]  = StringHelper.evl(TmpSelBedAddr.getFieldString("WBOOK_ID"), "");
					}
				}
			}
						
			/*
			 *	5.	Coil Schedule Call
			 */ 
			
			if (tmpWbookID != null && tmpWbookID.length > 0){
		       /*
				* 6.대차 작업 진행 상태 변경
				*/
				String updateWprogStatQuery = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateWprogStat";
				int WprogStat               = ydStockDAO.requestupdateData(updateWprogStatQuery, new Object[]{ 
						                      YmCommonConst.WPROG_STAT_S, tcTcNo });				
				
				
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
				
				for (int inx = 0; inx < tmpWbookID.length; inx++){
				    
				logger.println(LogLevel.DEBUG,this,"Hysco 작업예약"+inx+" "+tmpWbookID[inx]);
				Boolean isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{String.class},
																			 new Object[]{tmpWbookID[inx]});							
					
				}
			}else{
				logger.println(LogLevel.DEBUG,this,"작업진행상태 코드 변경 >> "+YmCommonConst.WPROG_STAT_W);
				//update TB_YM_EQUIP
			    // set WPROG_STAT = ?  --작업진행 상태코드(W,S)
				//where EQUIP_GP      = ?  --무인대차01호(2XTC01)
				String updateWprogStatQuery = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateWprogStat";
				int WprogStat               = ydStockDAO.requestupdateData(updateWprogStatQuery, new Object[]{ 
						                      YmCommonConst.WPROG_STAT_W, tcTcNo });								
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
	 * 오퍼레이션명 : A열연 대차 도착 THCH630
	 * logger.println("대차번호	 ==" +jDTORecord.getFieldString("대차번호"));    // 설비구분 : TC(2) + TC NO(2) LCAR
	 * logger.println("대차도착동  ==" +jDTORecord.getFieldString("대차도착동"));  // 동구분(1)
	 * JNDICTSStatusReg / vicCarMoveResult(출발동)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveACarRev(String sMessage)  throws java.rmi.RemoteException{
        
       	logger.println(LogLevel.DEBUG,this,"Start-receiveACarRev()");
		
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
			int iReq = 0;
			String wBookidSchCall = "";
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);

			// 야드 Level-2에서 대차 도착 정보를  수신			
			String tcTcNo         = YmCommonConst.CTS_GP_1XTC03;                                   // A열연 대차
			String tcCurrStopBay  = StringHelper.evl(jDTORecord.getFieldString("대차도착동"), ""); // 동구분(1)

			String tmpYd_Gp  	  = YmCommonConst.YD_GP_1;           // 야드구분
			String tmpBay_Gp 	  = tcCurrStopBay;                   // 동구분
			String tmpStartBay_Gp = "";
			String tmpArriveBAYGP = "";
			
			// 대차 강제 출발 처리
			if (tmpBay_Gp.equals(YmCommonConst.BAY_GP_A)){
				tmpStartBay_Gp = YmCommonConst.BAY_GP_B; // 1ATC03
			}else{
				tmpStartBay_Gp = YmCommonConst.BAY_GP_A; // 1BTC03
			}
				
			/*	
			 *	현재동정보를 가져온다.
			 *	Select CURR_STOP_LOC From TB_YM_EQUIP WHERE EQUIP_GP = ?   --확장대차(1XTC03)
			 */
			String sQuery1      = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectLoadCurrLoc";
			JDTORecord curLocJr = ydStackLayerDAO.requestgetData(sQuery1,new Object[]{ tcTcNo });
			
			String sCurrStopLoc = StringHelper.evl(curLocJr.getFieldString("CURR_STOP_LOC"), "");
			String sCurrStopBay = sCurrStopLoc.trim().substring(1,2); // 1BTC03 -> B
			
			if (tmpBay_Gp.trim().equals(sCurrStopBay)){
				logger.println(LogLevel.DEBUG,this,"대차도착=>대차 도착동이 현재 도착동과 같습니다.");
				return false;
			}
			
           //------------------------------------------------------------
		   /*
		    * 대차 출발 강제 기동 ( A열연은 대차 출발 정보가 없음으로  대차 도착 시점에 강제 출발 모듈 기동 ) 	
		    */
			EJBConnector ejbConn = new EJBConnector("default","JNDICTSStatusReg",this);
			Boolean isTrue = (Boolean)ejbConn.trx("vicCarMoveResult",new Class[]{String.class},
																	new Object[]{ tmpStartBay_Gp.trim() });					
			//------------------------------------------------------------
			
			if(tcTcNo == null || tcTcNo.equals("")){
				throw new EJBServiceException("EXTEND CAR PROCESS ERROR");
			}
			
			if (tmpBay_Gp.equals(YmCommonConst.BAY_GP_A)){
				tmpStartBay_Gp = YmCommonConst.BAY_GP_A; // 1ATC03
				tmpArriveBAYGP = YmCommonConst.TC_1ATC03;				
			}else{
				tmpStartBay_Gp = YmCommonConst.BAY_GP_B; // 1BTC03
				tmpArriveBAYGP = YmCommonConst.TC_1BTC03;			
			}
			
           /*
	        *	1.영차인지 공차인지 점검. 적재상태, 진행상태에 따라 대차 작업 선택 
	        *	  STACK_STAT(적재상태), WPROG_STAT(진행상태)를 점검
            */ 		
			String selectStackQueryId = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.selectStackstat";
			JDTORecord Stackstat = ydStackLayerDAO.requestgetData(selectStackQueryId, 
																  new Object[]{ tcTcNo });

			if (Stackstat == null){
				throw new EJBServiceException("Stackstat Error");
			}
			
			String tmpStackstat  = StringHelper.evl(Stackstat.getFieldString("STACK_STAT"), "");               //적재상태
			String tmpWprogstat  = StringHelper.evl(Stackstat.getFieldString("WPROG_STAT"), "");               //작업진행상태
			String tmpLoadGp     = StringHelper.evl(Stackstat.getFieldString("CARLOAD_ASSIGN_YN"), "");        //상차지정유무
			String tmpLoadSch    = StringHelper.evl(Stackstat.getFieldString("CARLOAD_SCH_WORK_KIND"), "");    //상차 Schedule 작업종류
			String tmpUnLoadGp   = StringHelper.evl(Stackstat.getFieldString("CARUNLOAD_ASSIGN_YN"), "");      //하차지정유무
			String tmpUnLoadSch  = StringHelper.evl(Stackstat.getFieldString("CARUNLOAD_SCH_WORK_KIND"), "");  //하차 Schedule 작업종류
			
		   /*
			*	2.대차의 MAP정보를 현재위치로 변경한다.
			*/
			String updateCurrQueryId = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateLoadCurrLoc";
			int CurrStop             = ydStackLayerDAO.requestupdateData(updateCurrQueryId, 
																		 new Object[]{tmpArriveBAYGP.trim(), 
																		 			  tcTcNo});										

		    /*
		     *	3.적재상태가 'L'일때(상차)
		     */
			if (tmpStackstat.equals(YmCommonConst.STACK_STAT_L)){             
				/*
				 *	3.1	상차작업이 스케쥴 지정일때.
				 */
				if (tmpLoadGp.equals(YmCommonConst.CARLOAD_ASSIGN_Y)) {       
					/*
					 *	3.1.1	상차 스케쥴 작업예약 검색.
					 */
					String selectLoadSchQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectLoadSchNEW";
					JDTORecord wBookid1 = ydStackLayerDAO.requestgetData(selectLoadSchQueryId, 
																		 new Object[]{tmpYd_Gp.trim(), 
																		 			  tmpBay_Gp.trim(), 
																		 			  tmpLoadSch.trim(),
																		 			  tcTcNo});
					if (wBookid1 != null){
						wBookidSchCall  = StringHelper.evl(wBookid1.getFieldString("WBOOK_ID"), "");
					}
				/*
				 *	3.2	상차작업이 스케쥴 미지정일때.
				 */	
				}else if (tmpLoadGp.equals(YmCommonConst.CARLOAD_ASSIGN_N)) { 
					/* 
					 *	3.1.2	동간이적상차(CTML) 작업예약을 검색.
					 */
					if ("".equals(tmpLoadSch.trim())){
						
						String selectLoadSchQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectLoadSchNEW";
						JDTORecord wBookid2         = ydStackLayerDAO.requestgetData(selectLoadSchQueryId, 
																					 new Object[]{tmpYd_Gp.trim(), 
																					 			  tmpBay_Gp.trim(),
																					 			  YmCommonConst.NEW_SCH_WORK_KIND_CTML,
																					 			  tcTcNo});	
						if (wBookid2 != null){
							wBookidSchCall  = StringHelper.evl(wBookid2.getFieldString("WBOOK_ID"), "");
						}	
					}
				}
				
			/*
			 *	4.적재상태가 'U'일때(하차)
			 */    
			}else if (tmpStackstat.equals(YmCommonConst.STACK_STAT_U)){
				/* 
				 *	4.1	현재 대차에 실려있는 작업 대상재 검검
			     */
				String selectStockQuery = "ym.steelinfo.steelinforecv.dao.YdStockDAO.selectStock";
				List StockId            = ydStockDAO.getListData(selectStockQuery, new Object[]{ tcTcNo });					
				
				JDTORecord TmpSelBedAddr       = null;
				int MaxRec                     = StockId.size();	
				String[] tmpStockID            = new String[MaxRec];
				String[] tmpFromEquipBedGp     = new String[MaxRec];
				String[] tmpFromEquipLayerGp   = new String[MaxRec];
				String[] tmpFromStockMoveTerm  = new String[MaxRec];
				
				for (int ii=0; ii<MaxRec; ii++){
					TmpSelBedAddr            = (JDTORecord) StockId.get(ii);
					tmpStockID[ii]           = StringHelper.evl(TmpSelBedAddr.getFieldString("STOCK_ID"), "");
					tmpFromEquipBedGp[ii]    = StringHelper.evl(TmpSelBedAddr.getFieldString("FRTOMOVE_EQUIP_BED_GP"), "");
					tmpFromEquipLayerGp[ii]  = StringHelper.evl(TmpSelBedAddr.getFieldString("FRTOMOVE_EQUIP_LAYER_GP"), "");
					tmpFromStockMoveTerm[ii] = StringHelper.evl(TmpSelBedAddr.getFieldString("STOCK_MOVE_TERM"), "");
				}
				
				/*
				 *	4.2	공통에서 진도코드를 검색해서 저장품 이동조건을 셋팅한다.
				 */
				for (int ii=0; ii<MaxRec; ii++){
					String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(tmpStockID[ii],"");
				    String sProgCd   	= sStockInfo[0];
					String sStocMv   	= sStockInfo[1];
					tmpFromStockMoveTerm[ii] = sStocMv;
				}
				
				for (int ii=0; ii<MaxRec; ii++){
					/*
					 *	4.3	Coil 공통 Table 저장위치 Update 
					 */						
					iReq = CraneSchDAO.updateCoilCommonLocInfo(tmpStockID[ii],
							                                   tmpArriveBAYGP + tmpFromEquipBedGp[ii] + tmpFromEquipLayerGp[ii]);
					/*
					 *	4.4	대차 도착 작업 실적 등록
					 */
					iReq = insertUpPutWrslRtData(tmpStockID[ii], 
												 tmpArriveBAYGP + tmpFromEquipBedGp[ii] + tmpFromEquipLayerGp[ii], 
							                     tmpArriveBAYGP + tmpFromEquipBedGp[ii] + tmpFromEquipLayerGp[ii], 
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
				 *	4.7	저장품이송설비항목 하차동으로 업데이트(저장품갯수만큼LOOP)	
				 */
				for (int ii=0; ii<MaxRec; ii++){
					if (tmpStockID[ii].trim() != null && !tmpStockID[ii].trim().equals("")){
						String updateFromEquipGpQuery = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateFromEquipGp";
						int updateFromEquipGp         = ydStockDAO.requestupdateData(updateFromEquipGpQuery, 
																					 new Object[]{ tcTcNo, 
																					 			   tmpStockID[ii].trim() });
					}
				}
				
				/* 
				 *	4.8	대차설비 상차동 CLEAR
				 */
				String updateClearStockIdQuery = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateClearStockId";
				int updateClearStockId         = ydStackLayerDAO.requestupdateData(updateClearStockIdQuery, 
																				   new Object[]{ tcTcNo });
				
				/* 
				 *	4.9	대차설비 하차동 저장품정보 셋팅(저장품갯수만큼반복)		
				 */
				for (int ii=0; ii<MaxRec; ii++){
					if (tmpStockID[ii].trim() != null && !tmpStockID[ii].trim().equals("")){
						String updateUnLoadStopLocQuery = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateUnLoadStopLocStock";
						int updateUnLoadStopLoc         = ydStockDAO.requestupdateData(updateUnLoadStopLocQuery, 
																					   new Object[]{ tmpStockID[ii].trim(), 
																					   				 tcTcNo, 
														  											 tmpFromEquipBedGp[ii].trim(), 
														  											 tmpFromEquipLayerGp[ii].trim() });

						logger.println(LogLevel.DEBUG,this, "대차도착위치 저장품업데이트=======================");
						logger.println(LogLevel.DEBUG,this, "StockID		="+ tmpStockID[ii].trim());
						logger.println(LogLevel.DEBUG,this, "ColGp			="+ tcTcNo);
						logger.println(LogLevel.DEBUG,this, "BedGp			="+ tmpFromEquipBedGp[ii].trim());
						logger.println(LogLevel.DEBUG,this, "EquipLayerGp	="+ tmpFromEquipLayerGp[ii].trim());
						logger.println(LogLevel.DEBUG,this, "=============================================");
					}
				}
				
				/* 
				 *	4.10	목적동에 도착시 현재 수량과 가능수량을 조절한다.
			     */

				for (int ii=0; ii<MaxRec; ii++){
					String selectBedMax = "ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.selectBedMax";
					JDTORecord bedMax   = ydStackLayerDAO.requestgetData(selectBedMax, 
																		 new Object[]{ tcTcNo, 
																		 			   tmpFromEquipBedGp[ii].trim()  });	
					
					String TmpStackerBedMax  = StringHelper.evl(bedMax.getFieldString("STACK_BED_QNTY_MAX"), "");
					
					if (TmpStackerBedMax != null){
						String updatestackctSet = "ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.updateStackCountSet";
						int updateStackerCTSet  = ydStackLayerDAO.requestupdateData(updatestackctSet, 
																					new Object[]{ TmpStackerBedMax.trim(), 
																								  YmCommonConst.STACK_BED_ABLE_QNTY_0, 
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
					
					wSchCode	= YmCommonConst.NEW_SCH_WORK_KIND_CTMU;
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
			    			
			    			sWbookId 		 = dao1.createWBook(tmpYd_Gp+
			    												tcCurrStopBay, 
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
						if(index == 0){
							wBookidSchCall  = sWbookId;		
						}			
						 
						/*
						 *	적치단  Table Update(작업요구상태='S'로 변경)
						 *	UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'S' WHERE STOCK_ID = ?
						 */
						sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
						iSeq 	 = ydStackLayerDAO.requestupdateData(sQueryId, 
																	 new Object[]{YmCommonConst.STACK_LAYER_STAT_S, wStockId});							
						
						/* 
						 *  저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
						 *  UPDATE TB_YM_STOCK 
						 *  SET WBOOK_ID= ?, 
						 *      STOCK_MOVE_TERM = ? 
						 *  WHERE STOCK_ID = ?
						 *  저장품이동조건	SD 대차상차대기,  SE 대차하차대기						  
						 */
//						sQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
//						iSeq 	 = ydStockDAO.requestupdateData(sQueryId, 
//															 new Object[]{ 
//								    							sWbookId, 
//								    							wStockMv, 
//								    							wStockId});		
						
						sQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId2";
						iSeq 	 = ydStockDAO.requestupdateData(sQueryId, 
															 new Object[]{ 
								    							sWbookId, 
								    							wStockId});		
					}						
				}
			}

			/*
			 *	4.12	대차도착했을때 상차 하차 구분에 따라 
			 *			STACK_LAYER_ACTIVE_STAT, 
			 *			STACK_LAYER_STAT 업데이트
		     */
			logger.println(LogLevel.DEBUG,this, "상,하차 구분="+ tmpStackstat.trim());
			
			/*
			 *	4.12.1	하차시 하차동MAP을 활성화.
			 */
			if (tmpStackstat.equals(YmCommonConst.STACK_STAT_U)){         
				String updateActiveStat  = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateActiveStat_01";
				int updateactivestat     = ydStackLayerDAO.requestupdateData(updateActiveStat, 
																			 new Object[]{tmpArriveBAYGP.trim() });		
			
			/*
			 *	4.12.1	상차및 IDLE시 상차동MAP을 활성화 및 적치가능 상태로 셋팅.
			 */
			}else{                                 
				String updateActiveStat  = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateActiveStat";
				int updateactivestat     = ydStackLayerDAO.requestupdateData(updateActiveStat, 
																			 new Object[]{YmCommonConst.WT_E_PROCESS, 
																			 			  tmpArriveBAYGP.trim() });					
			}
			
			/*
			 *	5.	Coil Schedule Call
			 */
			if (wBookidSchCall != null && !wBookidSchCall.trim().equals("")){
		       /*
				6.대차 작업 진행 상태 변경
				*/
				String updateWprogStatQuery = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateWprogStat";
				int WprogStat               = ydStockDAO.requestupdateData(updateWprogStatQuery, new Object[]{ 
						                      YmCommonConst.WPROG_STAT_S, tcTcNo });				
				
				ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
				isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class},new Object[]{ wBookidSchCall.trim() });					
			}else{
				String updateWprogStatQuery = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateWprogStat";
				int WprogStat               = ydStockDAO.requestupdateData(updateWprogStatQuery, new Object[]{ 
						                      YmCommonConst.WPROG_STAT_W, tcTcNo });								
			}
			
			logger.println(LogLevel.DEBUG,this,"End-receiveACarRev()");
					  
			return true;
			    
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
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
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
				EJBConnector ejbConn 	= new EJBConnector("default","JNDICCLdWrkOrdReg",this);
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
				EJBConnector ejbConn 	= new EJBConnector("default","JNDICCLdWrkOrdReg",this);
				ejbConn.trx("callCoilFromToResult",new Class[]{String.class,
															String.class,
															String.class},
												 new Object[]{sStockId,
												 	   		sYdGp,
												 	   		sLoc});
				
				//산적위치 수정 
				EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneSchReg",this);
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
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
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
			EJBConnector ejbConn 	= new EJBConnector("default","JNDICCLdWrkOrdReg",this);
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
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
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
			
			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			
				
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
    	 	EJBConnector ejbConn2 = new EJBConnector("default","JNDICCLdWrkOrdReg",this);
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
     			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYardWrkResReg",this);
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
     			EJBConnector ejbConn3 = new EJBConnector("default","JNDIYardWrkResReg",this);
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
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			

			
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

			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
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
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return ;
			}
			
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
			
			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			
				
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
    	 	EJBConnector ejbConn2 = new EJBConnector("default","JNDICCLdWrkOrdReg",this);
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
     			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYardWrkResReg",this);
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
     			EJBConnector ejbConn3 = new EJBConnector("default","JNDIYardWrkResReg",this);
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
			
			
			EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneSchReg",this);
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
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
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
	    	 	EJBConnector ejbConn2 = new EJBConnector("default","JNDICCLdWrkOrdReg",this);
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
				
				
				EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);			
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
	     			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYardWrkResReg",this);
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
	     			EJBConnector ejbConn3 = new EJBConnector("default","JNDIYardWrkResReg",this);
	     			isSuccess = (Boolean)ejbConn3.trx("getYDDMR022",new Class[]{JDTORecord.class},
	     			  	  	 new Object[]{tcRecordDM}); 
	                logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 임가공이송상하차완료.===");
	                //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				} //if end skip
			} //if end skip
			
			
			/*
			 * Crane 작업 실적 등록//////////////////////////////////////////////////////
			 */
			EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneSchReg",this);
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
		YdStackColDAO ydStackColDAO     = new YdStackColDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO CraneSchDAO         = new CraneSchDAO();
		
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
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
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
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
			EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneSchReg",this);
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

