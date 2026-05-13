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
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="CTankExtWrkOrdRegEJB" jndi-name="JNDICTankExtWrkOrdReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CTankExtWrkOrdRegSBean extends BaseSessionBean {
	public void ejbCreate() {
	}
	private YmComm ymComm = new YmComm();
	/**
	 * 오퍼레이션명 : 
	 *
	 * 작업예약취소 버튼 기능
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          	      
	public void receiveCTankExtWrkOrd(String stackColGp)  throws java.rmi.RemoteException{ 
        
        LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveCTankExtWrkOrd()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
			int iResult = 0;
			
			/*
			 * 수조 Tank 실적 처리 후 추출 요구를 위하여  수신한 수조 Tank 
			 * ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateLayerActiveStat 
			 * Select STOCK_ID From TB_YM_STACKLAYER  
			 *  where STACK_COL_GP = stackColGp
			 *    And STACK_BED_GP = 01~03
			 *    And STACK_LAYER_GP = '01'
			 */
			String selectCoolTankStockID = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectCoolTankStockID";
			List ListStockCoilNo         = ydStockDAO.getListData(selectCoolTankStockID, new Object[]{ stackColGp.trim() });	    		    	
			
			int MaxCoil                  = ListStockCoilNo.size();
			JDTORecord TmpSelStock       = null;
			String SelStock              = null;
			String CreatewBookid         = "0";
			String CoilNo                = ""; 
			
			if ( MaxCoil != 0 ){
				// 작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
				String wBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectYdWBook";
				JDTORecord wBookSel = ydStackLayerDAO.requestFind(wBookQueryId);
				
				if (wBookSel == null){
					throw new EJBServiceException("작업예약 ID 창성 Error");
				}
				
				CreatewBookid  = wBookSel.getFieldString("WBOOK_SELECT");
			}
			boolean isSucess = false;
			
			if (MaxCoil > 0 ){
				for (int ii=0; ii<MaxCoil; ii++){
					TmpSelStock    = (JDTORecord) ListStockCoilNo.get(ii);
					CoilNo         = StringHelper.evl(TmpSelStock.getFieldString("STOCK_ID"), "");
					
					/* 수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재 하는지 점검 
	                 * 저장품 Table에 작업예약_ID가 존재한다면 Error
	                 * Select STOCK_ID, WBOOK_ID from TB_YM_STOCK Where STOCK_ID = ?
	                 */ 
					String sStockQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
					JDTORecord StockCoilNo = ydStockDAO.getData(sStockQueryId, new Object[]{ CoilNo.trim() });
					  
					if (StockCoilNo != null){
						String stockId = StringHelper.evl(StockCoilNo.getFieldString("STOCK_ID"), "");
						String wbookId = StringHelper.evl(StockCoilNo.getFieldString("WBOOK_ID"), "");
		                
						logger.print(LogLevel.DEBUG,this, "stockId="+ stockId);
						
						List list = new ArrayList();
						list.add(CoilNo.trim());
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
								JDTORecord StackColGp = ydStackLayerDAO.requestgetData(stackLayQueryId, new Object[]{ stockId });

								if (StackColGp == null){
									throw new EJBServiceException("적치단(TB_YM_STACKLAYER) Table에 존재하지 않습니다. Error");
								}
								
								String stackCol  = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP"), "");
								String stackCol1 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP1"), "");
								String stackCol2 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP2"), "");
								String stackStat = StringHelper.evl(StackColGp.getFieldString("STACK_LAYER_STAT"), "");
								
								if (stackCol != null && stackCol != ""){
									
		                            // 적치단  Table Update(작업요구상태='S'로 변경)
									// UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'S' WHERE STOCK_ID = ?
									if (stackStat.trim().equals("L")){
										String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
										int stkColGp = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ 
												       YmCommonConst.STACK_LAYER_STAT_S, stockId.trim() });
									}
									
									/* 저장품이동조건은 압연실적 시점에 발생한것을 그대로 살린다.
									 * select STOCK_MOVE_TERM From TB_YM_STOCK where stock_id = ?
									 */
									String selectStockMoveTerm = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockMoveTerm";
									JDTORecord selectstockmoveterm  = ydStockDAO.requestgetData(selectStockMoveTerm, new Object[]{ stockId.trim() });
									
									String TmpSTOCKMOVETERM   = StringHelper.evl(selectstockmoveterm.getFieldString("STOCK_MOVE_TERM"), "");									

									// 저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
									// UPDATE TB_YM_STOCK SET wbook_id = (WBookID) WHERE stock_id = ?
									String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
									int stkId = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ 
											    CreatewBookid, TmpSTOCKMOVETERM, stockId.trim() });
									
									if (!isSucess){
										// Coil 수냉탱크추출 
										String sWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
		  							    int wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ 
		  							    		           CreatewBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CTFL, 
														   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
										isSucess = true;														   
									}
									
									logger.println(LogLevel.DEBUG,this, "End-receiveCTankExtWrkOrd()");
								}	
					    	}
						}						
					}
				}
			}
			
			{
				boolean isTc = false;
				
				CraneSchDAO dao = new CraneSchDAO();
				
				String sEquipGp			= "";
				String sEquipStat 		= "";
				String sWorkMode		= "";
				String sStackStat		= "";
				String sWprogStat		= "";
				String sCurrStopLoc 	= "";
				
				JDTORecord equipJr = dao.getEquipInfoWithEquipGp(YmCommonConst.EQUIP_GP_3XTC02);
				
				if(equipJr != null){
					sEquipGp 		= StringHelper.evl(equipJr.getFieldString("EQUIP_GP"), "");
					sEquipStat 		= StringHelper.evl(equipJr.getFieldString("EQUIP_STAT"), "");
					sWorkMode  		= StringHelper.evl(equipJr.getFieldString("WORK_MODE"), "");
					sStackStat 		= StringHelper.evl(equipJr.getFieldString("STACK_STAT"), "");
					sWprogStat 		= StringHelper.evl(equipJr.getFieldString("WPROG_STAT"), "");
					sCurrStopLoc  	= StringHelper.evl(equipJr.getFieldString("CURR_STOP_LOC"), "");
				}
				
				if(YmCommonConst.WORK_MODE_C.equals(sEquipStat)||
				   YmCommonConst.WORK_MODE_C.equals(sWorkMode)){
					logger.println(LogLevel.DEBUG,this,"=대차로직=> 2번 대차가 고장상태");
				}else{
					
			    	if("L".equals(sStackStat)&&
			    	   "W".equals(sWprogStat)){
			    			
			    		isTc = true;	
			    		logger.println(LogLevel.DEBUG,this,"=대차로직=> 2번 대차가 상차대기중");
				    	
			    	}
			    }
				
				if(isTc){
//2010.04.13 대차위치변경 자동해제 정종균						
//					int iSeq = dao.updateEquipSchInfo_01(YmCommonConst.EQUIP_GP_3XTC02,
//														 sCurrStopLoc,
//														 YmCommonConst.YD_GP_3 + 
//										  			     stackColGp.substring(1, 2) + 
//										  			     sEquipGp.substring(2),
//														 "Y",
//														 YmCommonConst.NEW_SCH_WORK_KIND_CTFL,
//														 YmCommonConst.YD_GP_3 + 
//										  			     YmCommonConst.BAY_GP_H + 
//										  			     sEquipGp.substring(2),
//														 "Y",
//														 YmCommonConst.NEW_SCH_WORK_KIND_CTFU);
					
					String sBayGp = stackColGp.substring(1, 2); 
					String sCurGp = sCurrStopLoc.substring(1, 2); 
					 
					if(!sBayGp.equals(sCurGp)){
						
						
						EJBConnector ejbConn = new EJBConnector("default","JNDICTSStatusReg",this);
						/* 2008.03. 27 이정훈
						 * 일단 막음
						 *  - 수냉실적 발생시 대차 자동 출발
						 */
						/*Boolean isTrue = (Boolean)ejbConn.trx("sendStartOrder",new Class[]{String.class,
																						   String.class,
																						   String.class},
																		  	  new Object[]{YmCommonConst.EQUIP_GP_3XTC02,
																		  	  			   sCurrStopLoc,
																		  	  			   YmCommonConst.YD_GP_3 + 
																			  			   stackColGp.substring(1, 2) + 
																			  			   sEquipGp.substring(2)});
						*/
					}else{
						//	Coil Schedule EJB Call
						EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
						Boolean isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class},new Object[]{ CreatewBookid });
					}
				}else{
					//	Coil Schedule EJB Call
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					Boolean isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class},new Object[]{ CreatewBookid });
				}
			}	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }    
	} 


}
