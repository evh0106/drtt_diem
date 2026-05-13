package com.inisteel.cim.ym.workrequest.wrequestaccept.session;

import java.util.ArrayList;
import java.util.List;

import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
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
import com.inisteel.cim.common.parser.Level2Parser;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="SSExExtWrkOrdRegEJB" jndi-name="JNDISSExExtWrkOrdReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class SSExExtWrkOrdRegSBean extends BaseSessionBean{
	
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 *  조업 LEVEL3로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
        *  전문내용을 JDTORecord로 파싱한다.
        *  업무 로직
        *	1.TC_CD - POYM003 (I/F ID : YM-LIF-023 )
        *	2.조업 LEVEL3로부터 Scarfing 출측 Line Off 요구 정보를 수신
        *  3.이미 해당 Coil에 작업예약이 되어 있다면 Error(Skip)
        *  4.수신항목의 Coil No로 현재 적치위치를 검색하여 
        *  5.적치단 Table에 적치단상태(STACK_LAYER_STAT CHAR(1)) 작업요구상태='S'로 변경처리 한다.
        *	6.작업예약 Table에 “BSY_SM 출측 Line_Off 요구등록“  Schedule Code로 Insert 한다.
        *  7.작업예약 Table의 ID를 저장품 Table에 Set 하고 Update 한다.
        *  8.Slab Schedule EJB를 Call 한다.  
        *  POYM0032005-09-0510:15:31100421HB00016   2
        * 
	 *  logger.println("전문코드	  ==" +jDTORecord.getFieldString("전문코드"));
	 *  logger.println("발생일자	  ==" +jDTORecord.getFieldString("발생일자"));
	 *  logger.println("발생시간       ==" +jDTORecord.getFieldString("발생시간"));
	 *  logger.println("전문구분	  ==" +jDTORecord.getFieldString("전문구분")); // I : Initialize, U : Update, D : Delete, R : Re-request
	 *  logger.println("전문길이	  ==" +jDTORecord.getFieldString("전문길이"));
	 *  logger.println("처리구분	  ==" +jDTORecord.getFieldString("처리구분")); // 1:보급, 2:추출, 3:Take-Out
	 *  logger.println("Slab No   ==" +jDTORecord.getFieldString("Slab No"));
	 *  logger.println("위치             ==" +jDTORecord.getFieldString("위치"));
 	 *
	 *  EJBConnector ejbConn = new EJBConnector("default",sJndiName,this);
	 *  Boolean isTrue = (Boolean)ejbConn.trx(sMethod,new Class[]{String.class},new Object[]{sMessage});
	 *
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean receiveSSExExtWrkOrd(String sMessage) { 
        
        	LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
			Logger logger = new Logger(config);
			logger.println(LogLevel.DEBUG,this,"Start-receiveSSExExtWrkOrd()");
			
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
				
				String ProcessID            = StringHelper.evl(jDTORecord.getFieldString("작업처리구분"), "");
				String SlabNo               = StringHelper.evl(jDTORecord.getFieldString("SLABNO"), "");
				String TakeOutPosition      = StringHelper.evl(jDTORecord.getFieldString("TakeOutPosition"), "");
				
				if (ProcessID.equals(YmCommonConst.PROCESS_ID_1) || 
					ProcessID.equals(YmCommonConst.PROCESS_ID_2) || 
					ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
					if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){
						/*
						 * 저장품 Table에서 저장품 이동 조건이 STOCK_MOVE_TERM(압연지시대기) && SCARFING_SUPPLY_YN(Scarfing 보급 유무='Y')를 조회
						 * Order by SHEAR_SUPPLY_SEQ(산적 Lot 보급순서), STOCK_ID(Slab No)
						 * 
						 * <지정>
						 * Select STOCK_ID 
						 *   From TB_YM_STOCK
						 *  Where STOCK_MOVE_TERM    = ?
						 *    And SCARFING_SUPPLY_YN = 'Y'
						 *    And STOCK_ID Is Not Null
						 *  Order by SHEAR_SUPPLY_SEQ, STOCK_ID
						 *
						 * <미지정> 변경할것
						 * SELECT  SCARFING.STOCK_ID
						 *   FROM  (
						 *          SELECT  STOCK.STOCK_ID
						 *          FROM    TB_YM_STOCK         STOCK,
						 *                  TB_YM_STACKLAYER    LAYER
						 *          WHERE   STOCK.STOCK_MOVE_TERM    = 'ES'
						 *            AND   STOCK.SCARFING_SUPPLY_YN = 'Y'
						 *            AND   STOCK.STOCK_ID = LAYER.STOCK_ID
						 *            AND   LAYER.STACK_LAYER_STAT = 'L'
						 *          ORDER   BY STOCK.SHEAR_SUPPLY_SEQ, LAYER.STACK_COL_GP, LAYER.STACK_BED_GP, LAYER.STACK_LAYER_GP DESC
						 *         ) SCARFING
						 *  WHERE   ROWNUM = 1 
						 * 
						 */	
						String selectScarfingStockID = "ym.steelinfo.steelinforecv.YdStockDAO.selectScarfingStockID";
						List ListscarfingstockID     = ydStockDAO.getListData(selectScarfingStockID, new Object[]{ //압연지시대기
								                       YmCommonConst.NEW_STOCK_MOVE_TERM_ES });
						
						int MaxSlab                  = ListscarfingstockID.size();
						JDTORecord TmpSelStock       = null;
						String SelStock              = null;
						
						if (MaxSlab > 0 ){
							for (int ii=0; ii<MaxSlab; ii++){
								if (ii==0){
									TmpSelStock      = (JDTORecord) ListscarfingstockID.get(ii);
//									SlabNo           = StringHelper.evl(TmpSelStock.getFieldString("STOCK_ID"), "");															
								}
							}
						}
					}

//					String InsertAddress = YmCommonDB.insertConveyorInfo("2DSE01", stockId.trim(), "MIN");
//					String DeleteAddress = YmCommonDB.deleteConveyorInfo("2DSD01", stockId.trim(), "MAX");
					
					/* 수신한  Slab No가 저장품(TB_YM_STOCK) Table 에 존재 하는지 점검 
	                 * 저장품 Table에 작업예약_ID가 존재한다면 Error
	                 * Select STOCK_ID, WBOOK_ID from TB_YM_STOCK Where STOCK_ID = ?
	                 */ 
					String sStockQueryId   = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
					JDTORecord StockSlabNo = ydStockDAO.getData(sStockQueryId, new Object[]{ SlabNo.trim() });
					  
					if (StockSlabNo == null){
						logger.println(LogLevel.DEBUG,this, "StockSlabNo Error");
						return false; 					
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
		                     *        STACK_BED_GP, STACK_LAYER_STAT, STACK_LAYER_GP
				    		 *   From TB_YM_STACKLAYER 
				    		 *  Where STOCK_ID = ? And (STACK_LAYER_STAT = "L" OR STACK_LAYER_STAT = "U") (적치단 상태 L:적치중, U:권상전)
				    		 */     	
							String stackLayQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectStackColGp";
							JDTORecord StackColGp  = ydStackLayerDAO.requestgetData(stackLayQueryId, new Object[]{ stockId.trim() });

							if (StackColGp == null){
								logger.println(LogLevel.DEBUG,this, "적치단(TB_YM_STACKLAYER) Table Read Error");
								return false; 												
							}
							
							String stackCol     = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP"), "");
							String stackCol1    = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP1"), "");     //야드구분
							String stackCol2    = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP2"), "");     //동구분
							String stackStat    = StringHelper.evl(StackColGp.getFieldString("STACK_LAYER_STAT"), "");
							String stackbedgp   = StringHelper.evl(StackColGp.getFieldString("STACK_BED_GP"), "");
							String stacklayergp = StringHelper.evl(StackColGp.getFieldString("STACK_LAYER_GP"), "");
							
							if (stackCol != null && !stackCol.equals("")){
								
	                            /* 적치단  Table Update(작업요구상태='S'로 변경)
								 UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'S' 
								  WHERE STOCK_ID = ?
							        And STACK_COL_GP = ?
							     	And STACK_BED_GP = ?
							     	And STACK_LAYER_GP = ?
							    */ 	     
								if (stackStat.trim().equals(YmCommonConst.STACK_LAYER_STAT_L) ||
									stackStat.trim().equals(YmCommonConst.STACK_LAYER_STAT_U)){
									String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMarkSlab";
									int stkColGp            = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ 
											                  YmCommonConst.STACK_LAYER_STAT_S, stockId.trim(),
												              stackCol.trim(), stackbedgp.trim(), stacklayergp.trim() });
								}
								
								// 작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
								String wBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectYdWBook";
								JDTORecord wBookSel = ydStackLayerDAO.requestFind(wBookQueryId);

								if (wBookSel == null){
									logger.println(LogLevel.DEBUG,this, "작업예약 ID 생성 Error");
									return false; 																				
								}
								
								String wBookid  = wBookSel.getFieldString("WBOOK_SELECT");
								
								// 작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 작업예약조, 등록자, 등록일시) 한다.
								String sWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
								
								// 1:보급,2:Take-Out,3:추출
								if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){
									int wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Slab Scarfing 보급
											           wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_SSLI, 
													   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });	
								}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_2)){
									int wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Slab Scarfing Take-Out
											           wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_SSTO, 
													   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });								
								}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
									int wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Slab Scarfing 추출
											           wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_SSLO, 
													   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
								}
								
								// Select STOCK_STAT from TB_YM_STOCK WHERE stock_id = ?							
								String selectYdStockStockstat = "ym.steelinfo.steelinforecv.YdStockDAO.selectYdStockStockstat";
								JDTORecord selectStockstat    = ydStackLayerDAO.requestgetData(selectYdStockStockstat, new Object[]{ 
										                        stockId.trim() });
								String tmpSTOCKSTAT           = StringHelper.evl(StackColGp.getFieldString("STOCK_STAT"), "");
								String tmpStockMoveTerm       = "";
								if (tmpSTOCKSTAT.equals(YmCommonConst.STOCK_STAT_D)){       //정정작업대기
									tmpStockMoveTerm          = YmCommonConst.NEW_STOCK_MOVE_TERM_DS;
								}else if (tmpSTOCKSTAT.equals(YmCommonConst.STOCK_STAT_E)){ //압연지시대기
									tmpStockMoveTerm          = YmCommonConst.NEW_STOCK_MOVE_TERM_ES;
								}
								
								// 저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
								// UPDATE TB_YM_STOCK SET WBOOK_ID= ?, STOCK_MOVE_TERM = ? WHERE STOCK_ID = ?
								String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
								int stkId = 0;
								if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){       // 정정작업대기
									  stkId = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ 
									  		  wBookid, YmCommonConst.NEW_STOCK_MOVE_TERM_DS, stockId.trim() });	
								}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_2)){ // 정정작업대기
									  stkId = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ 
									  		  wBookid, YmCommonConst.NEW_STOCK_MOVE_TERM_DS, stockId.trim() });								  
								}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){ // 그대로
									  stkId = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ 
									  		  wBookid, tmpStockMoveTerm, stockId.trim() });
								}

								logger.println(LogLevel.DEBUG,this, "End-receiveSSSpplyWrkOrd()");
								
								//Slab Schedule EJB Call syCraneScheduleInfoInsert
								EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
								Boolean isTrue       = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",new Class[]{String.class},new Object[]{ 
										               wBookid });							
							}
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
}
