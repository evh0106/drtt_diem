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
import com.inisteel.cim.common.parser.Level2Parser;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="SRTExtWrkOrdRegEJB" jndi-name="JNDISRTExtWrkOrdReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class SRTExtWrkOrdRegSBean extends BaseSessionBean{
	private YmComm ymComm = new YmComm();	
	
	public void ejbCreate() {
	}

 	/**
	 * 오퍼레이션명 : 
	 *
	 * Mill LEVEL2로부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
     * 전문내용을 JDTORecord로 파싱한다.
     * 업무 로직
     *	1.TC_CD - CF1PB11 (I/F ID : YM-BIF-025)
     *	2.Mill 조업시스템으로부터 SLAB Line Off Request 정보를 수신
     *	3.Slab 재료정보를 야드 저장품 Table에 존재하는지 Read한다.
     *	4.작업예약 Table에 해당하는 Slab가 존재하면 Update하고 존재하지 않으면 Insert한다. 
     * 
     *  CF1PB112005-09-2213:13:13I0062HD00320     1AW000111
     * 
     * param TC_CD(전문메세지)
     * return true(성공),false(실패)
     * throws
     * 
     * logger.println(LogLevel.DEBUG, "전문코드	 ==" +jDTORecord.getFieldString("전문코드"));
     * logger.println(LogLevel.DEBUG, "발생일자	 ==" +jDTORecord.getFieldString("발생일자"));
     * logger.println(LogLevel.DEBUG, "발생시간       ==" +jDTORecord.getFieldString("발생시간"));
     * logger.println(LogLevel.DEBUG, "전문구분	 ==" +jDTORecord.getFieldString("전문구분")); // I : Initialize, U : Update, D : Delete, R : Re-request
     * logger.println(LogLevel.DEBUG, "전문길이	 ==" +jDTORecord.getFieldString("전문길이"));
     * logger.println(LogLevel.DEBUG, "SlabNo    ==" +jDTORecord.getFieldString("SlabNo"));
     * logger.println(LogLevel.DEBUG, "SPARE	 ==" +jDTORecord.getFieldString("SPARE"));
     * logger.println(LogLevel.DEBUG, "근	     ==" +jDTORecord.getFieldString("근"));
     * logger.println(LogLevel.DEBUG, "조	     ==" +jDTORecord.getFieldString("조"));
     * logger.println(LogLevel.DEBUG, "SLAB구분      ==" +jDTORecord.getFieldString("SLAB구분")); // H:HCR, C:CCR, W:WCR
     * logger.println(LogLevel.DEBUG, "DestinationChange ==" +jDTORecord.getFieldString("DestinationChange"));
     * logger.println(LogLevel.DEBUG, "Location	 ==" +jDTORecord.getFieldString("Location")); // 1:SHB1, 2:SHB2,	3:SHB3, 4:#2CTC
     * logger.println(LogLevel.DEBUG, "Position	 ==" +jDTORecord.getFieldString("Position")); // 0:#2CTC, 
	 * 1:SHB1  : 2AHB01
	 * 2:SHB2  : 2BHB02
	 * 3:SHB3  : 2CHB03
	 * 4:#2CTC : 2ACT02
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveOutCCExtWrkOrd(String sMessage) throws java.rmi.RemoteException{ 
        
        LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger           = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveOutCCExtWrkOrd()");
		
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
				jDTORecord.setField("MSG_ID"	, "CF1PB11");  
				EJBConnector ydEjbCon = new EJBConnector("default", this);
			    ydEjbCon.trx("YmCommEJB", "rcvInterface", jDTORecord);
				return true;
			}				
			//--------------------------------------------------------------------
			
			String SlabNo     = StringHelper.evl(jDTORecord.getFieldString("SLABNo"), "");
			String Shift      = StringHelper.evl(jDTORecord.getFieldString("Shift"), ""); // 근
			String Group      = StringHelper.evl(jDTORecord.getFieldString("Group"), ""); // 조
			String SlabID     = StringHelper.evl(jDTORecord.getFieldString("SLAB구분"), "");
			String DestChange = StringHelper.evl(jDTORecord.getFieldString("DestinationChange"), "");
			String Location   = StringHelper.evl(jDTORecord.getFieldString("Location"), "");
			String Position   = StringHelper.evl(jDTORecord.getFieldString("Position"), "");
			
			/*
			 * 수신한 Slab를 해당위치와, STACK_LAYER_STAT 상태를  'L' Update 한다. 
			 * 1:SHB1  : 2AHB01
			 * 2:SHB2  : 2BHB02
			 * 3:SHB3  : 2CHB03
			 * 4:#2CTC : 2ACT02
		     * update TB_YM_STACKLAYER SET STOCK_ID = SlabNo, STACK_LAYER_STAT='L' 
		     *  WHERE STACK_COL_GP   = '2AHB01'
		     *    AND STACK_BED_GP   =
		     *    AND STACK_LAYER_GP = '01' 
			 */
			
			String HZ_COL_GP   = "";
			String HZ_BED_GP   = "";
			String HZ_LAYER_GP = YmCommonConst.STACK_LAYER_GP_01;
			
			if (Location.trim().equals(YmCommonConst.LOCATION_1)){
				HZ_COL_GP = YmCommonConst.STACK_COL_GP_2AHB01;
			}else if (Location.trim().equals(YmCommonConst.LOCATION_2)){
				HZ_COL_GP = YmCommonConst.STACK_COL_GP_2BHB02;
			}else if (Location.trim().equals(YmCommonConst.LOCATION_3)){
				HZ_COL_GP = YmCommonConst.STACK_COL_GP_2CHB03;
			}else if (Location.trim().equals(YmCommonConst.LOCATION_4)){
				HZ_COL_GP = YmCommonConst.STACK_COL_GP_2ACT02;
			}	
			
			if (Position.trim().equals("0") ){
				HZ_BED_GP = YmCommonConst.STACK_BED_GP_01;
			}else{
				HZ_BED_GP = "0" + Position.trim();
			}
					
			String updateHZStackLayer = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateHZStackLayer";
			int updatehzstacklayer    = ydStackLayerDAO.requestupdateData(updateHZStackLayer, new Object[]{ 
					                    SlabNo.trim(), YmCommonConst.STACK_LAYER_STAT_L, 
										HZ_COL_GP.trim(), HZ_BED_GP.trim(), HZ_LAYER_GP.trim() });			
			
			
			
			/* 수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재 하는지 점검 
             * 저장품 Table에 작업예약_ID가 존재한다면 Error
             * Select STOCK_ID, WBOOK_ID from TB_YM_STOCK Where STOCK_ID = ?
             */ 
			String sStockQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
			JDTORecord StockSlabNo = ydStockDAO.getData(sStockQueryId, new Object[]{ SlabNo.trim() });

			if (StockSlabNo == null){
				throw new EJBServiceException("수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재하지 않습니다. Error");
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
						if (stackStat.trim().equals(YmCommonConst.STACK_LAYER_STAT_L)){
							String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
							int stkColGp            = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ 
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
						
						// Schedule Code
						if (Location.trim().equals(YmCommonConst.LOCATION_4)){
							int wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Slab H/B Line Off
							           wBookid.trim(), stackCol1.trim(), stackCol2.trim(), YmCommonConst.NEW_SCH_WORK_KIND_SHLO, 
									   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });	
						}else{
							int wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Slab H/B Line Off
							           wBookid.trim(), stackCol1.trim(), stackCol2.trim(), YmCommonConst.NEW_SCH_WORK_KIND_SHLO, 
									   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });	
						}

						String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(stockId.trim(),"");
						
						// 저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
						// UPDATE TB_YM_STOCK SET WBOOK_ID= ?, STOCK_MOVE_TERM = ? WHERE STOCK_ID = ?
						String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
						if (Location.trim().equals(YmCommonConst.LOCATION_4)){
							int stkId = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ 
									wBookid.trim(), sStockInfo[1], stockId.trim() });	
						}else{
							int stkId = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ 
									wBookid.trim(), sStockInfo[1], stockId.trim() });
						}
						
						logger.println(LogLevel.DEBUG,this, "End-receiveOutCCExtWrkOrd()");
						
						//Slab Schedule EJB Call
						EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
						Boolean isTrue = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",new Class[]{String.class},new Object[]{ wBookid.trim() });							
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

 	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 L2로부터 Slab Line Off 요구를 받아 Slab Line Off 완료시 호출
	 * CF1BP03
	 *       1	전문코드	C	7	전문코드	
	 *       2	발생일자	C	10	발생일자	
	 *       3	발생시간	C	8	발생시간	
	 *       4	전문구분	C	1	전문구분	
	 *       5	전문길이	C	4	전문길이	
	 *       6	SLABNo		C	11	SLABNo	
	 *       7	SPARE		C	1	SPARE	 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveSlabLineOffResult(String sSlabNo) throws java.rmi.RemoteException{
		
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveSlabLineOffResult()");
		
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
			
			String TC_CD                = ""; //CF1BP03
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
			int	iSlab_No				= 11;
			int	iSpace   				= 1;
			
			TC_CD 						= YmCommonConst.TC_CF1BP03; 
			TC_Date 					= YmCommonUtil.getCurDate("yyyy-MM-dd");
			TC_Time						= YmCommonUtil.getCurDate("hh-mm-ss");
			TC_ID						= "I";
			TC_Length					= "0012";
			
			sMsg.append(YmCommonUtil.FillToString(TC_CD			,iTC_CD));
			sMsg.append(YmCommonUtil.FillToString(TC_Date		,iTC_Date));
			sMsg.append(YmCommonUtil.FillToString(TC_Time		,iTC_Time));
			sMsg.append(YmCommonUtil.FillToString(TC_ID			,iTC_ID));
			sMsg.append(YmCommonUtil.FillToString(TC_Length		,iTC_Length));
			sMsg.append(YmCommonUtil.FillToString(sSlabNo	    ,iSlab_No));
			sMsg.append(YmCommonUtil.FillToString(TC_Space	    ,iSpace));
			
			SendMsgInfo  =  sMsg;

			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			Boolean isTrue = (Boolean)ejbConn.trx("CF1BP03send",new Class[]{String.class},new Object[]{ SendMsgInfo.toString()  });					
			
			logger.println(LogLevel.DEBUG,this,"End-receiveSlabLineOffResult()");
			
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
	 * B열연 L2로부터 #3 CTC, #4 CTC Slab Loading  완료시 호출
	 * CF1BP12
	 * 		1	전문코드	C	7	전문코드	
	 * 		2	발생일자	C	10	발생일자	
	 * 		3	발생시간	C	8	발생시간	
	 * 		4	전문구분	C	1	전문구분	
	 * 		5	전문길이	C	4	전문길이	
	 * 		6	SLABNo		C	11	SLABNo	
	 * 		7	SPARE		C	1	SPARE	
	 * 		8	Position	C	1	Position	
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveSlabLoadingResult(String sSlabNo,
											String sPosition) throws java.rmi.RemoteException{
		
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveSlabLoadingResult()");
		
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
			
			String TC_CD                = ""; //CF1BP12
			String TC_Date              = "";
			String TC_Time				= "";
			String TC_ID 				= ""; //I
			String TC_Length 			= ""; //0254
			String TC_Space 			= "";
			String TC_Position 			= "";
			
			int	iTC_CD					= 7;
			int	iTC_Date				= 10;
			int	iTC_Time				= 8;
			int	iTC_ID					= 1;
			int	iTC_Length				= 4;			
			int	iSlab_No				= 11;
			int	iSpace   				= 1;
			int	iPosition  				= 1;
			
			TC_CD 						= YmCommonConst.TC_CF1BP12; 
			TC_Date 					= YmCommonUtil.getCurDate("yyyy-MM-dd");
			TC_Time						= YmCommonUtil.getCurDate("hh-mm-ss");
			TC_ID						= "I";
			TC_Length					= "0013";
			TC_Position					= "";
			
			String sBayGp  = sPosition.substring(0, 6); // 동구분
			
			if(YmCommonConst.STACK_COL_GP_2ACT01.equals(sBayGp)){
				TC_Position = "1";
			}else if(YmCommonConst.STACK_COL_GP_2ACT02.equals(sBayGp)){
				TC_Position = "2";
			}else if(YmCommonConst.STACK_COL_GP_2BCT03.equals(sBayGp)){
				TC_Position = "3";
			}else if(YmCommonConst.STACK_COL_GP_2CCT04.equals(sBayGp)){
				TC_Position = "4";
			}
			
			sMsg.append(YmCommonUtil.FillToString(TC_CD			,iTC_CD));
			sMsg.append(YmCommonUtil.FillToString(TC_Date		,iTC_Date));
			sMsg.append(YmCommonUtil.FillToString(TC_Time		,iTC_Time));
			sMsg.append(YmCommonUtil.FillToString(TC_ID			,iTC_ID));
			sMsg.append(YmCommonUtil.FillToString(TC_Length		,iTC_Length));
			sMsg.append(YmCommonUtil.FillToString(sSlabNo	    ,iSlab_No));
			sMsg.append(YmCommonUtil.FillToString(TC_Space	    ,iSpace));
			sMsg.append(YmCommonUtil.FillToString(TC_Position	,iPosition));
			
			SendMsgInfo  =  sMsg;

			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			Boolean isTrue = (Boolean)ejbConn.trx("CF1BP12send",new  Class[]{String.class},
																new Object[]{SendMsgInfo.toString()});					
			
			logger.println(LogLevel.DEBUG,this,"End-receiveSlabLoadingResult()");
			
			return true;
			
		}catch(DAOException daoe){
		    throw daoe;
		}catch(Exception e){
		    throw new EJBServiceException(e);
		}  		
	}	

	
}


