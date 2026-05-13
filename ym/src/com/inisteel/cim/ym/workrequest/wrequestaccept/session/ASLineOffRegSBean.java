package com.inisteel.cim.ym.workrequest.wrequestaccept.session;

import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.YmCommonConst;
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
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="ASLineOffRegEJB" jndi-name="JNDIASLineOffReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class ASLineOffRegSBean extends BaseSessionBean{
	private Logger logger 			= null;
	private CraneSchDAO dao 		= null;
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger 		= new Logger(config);
		dao 		= new CraneSchDAO();
	}
	
	/**
	 * 오퍼레이션명 : BED 금지/해제
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public boolean ASLineOffresult(String sMessage) { 
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		logger.println(LogLevel.DEBUG,this,"Start-ASLineOffresult()");
		Level2Parser level2Parser 	= new Level2Parser();
		JDTORecord jDTORecord 		= level2Parser.parse(sMessage);
		boolean isTrue = ASLineOffresult(jDTORecord);
		logger.println(LogLevel.DEBUG,this, "End-ASLineOffresult()");
		return isTrue;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *  야드 LEVEL2로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
        *  전문내용을 JDTORecord로 파싱한다.
        *  업무 로직
 	 *  2007-04-04 A열연 SLAB야드 추가 (MCH)
	 *  TC_CD : HC3PB51
	 *	SLABNo		C	11	SLAB_No	
	 *	SPARE		C	9	SPARE	
	 *	Dong		C	1	Dong		A : A동 , B : B동
	 *	Position	C	2	Position	추후 L2에서 정의함
	 *	Mode		C	1	Mode		1:Auto    2:Manual
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public boolean ASLineOffresult(JDTORecord jDTORecord) { 
		
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

			String sQueryId 			= "";
			String SlabNo               = StringHelper.evl(jDTORecord.getFieldString("SLABNo"), "").trim();
			String TC_CD                = StringHelper.evl(jDTORecord.getFieldString("전문코드"), "").trim();
			String sYdGp 				= YmCommonConst.YD_GP_0;
			String sBayGp			    = StringHelper.evl(jDTORecord.getFieldString("Dong"), "").trim();
			String Position			    = StringHelper.evl(jDTORecord.getFieldString("Position"), "").trim();
			String sWbookId				= "";
			String RtName = "";
			
			//level2에서 동구분이 빠졌을 경우를 위해서
			if("".equals(sBayGp)){
				if("11".equals(Position)){
					sBayGp = "A";
				}else{
					sBayGp = "B";					
				}
			}
			if(YmCommonConst.BAY_GP_B.equals(sBayGp)){
				RtName = YmCommonConst.EQUIP_KIND_0_B_RT;
			}else{
				RtName = YmCommonConst.EQUIP_KIND_0_A_RT;
			}
			
			//저장품 테이블에 해당 저장품이 있는지 체크
			JDTORecord stockJr = dao.getStockInfoWcrGp(SlabNo);
			if(stockJr == null){
				logger.println(LogLevel.DEBUG,this, "A열연 Slab Line Off 요구 저장품정보 존재않함="+SlabNo);
				throw new EJBServiceException("A열연 Slab Line Off 요구 저장품정보 존재않함"+SlabNo);
			}
			
			String sORD_YEOJAE_GP 	= StringHelper.evl(stockJr.getFieldString("ORD_YEOJAE_GP"), "");
			String sORD_HCR_GP 		= StringHelper.evl(stockJr.getFieldString("ORD_HCR_GP"), "");
			String tWbookId 		= StringHelper.evl(stockJr.getFieldString("WBOOK_ID"), "");
			
			logger.println(LogLevel.DEBUG,this, "A열연 Slab Line Off ="+SlabNo);
			logger.println(LogLevel.DEBUG,this, "A열연 Slab Line Off WCR_GP= ["+sORD_HCR_GP+"]");
			logger.println(LogLevel.DEBUG,this, "A열연 Slab Line Off BayGp = ["+sBayGp+"]");
			
	    	if ("".equals(tWbookId)){
				
				/*****************************************************************************************
				SELECT  TO_CHAR(SYSDATE, 'YYYYMMDDHH24MI') || 
				             LPAD(YM_WBOOK_SEQ.NEXTVAL,6,'0') AS WBOOK_SELECT
				FROM     DUAL
				******************************************************************************************/
				sQueryId 	= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectYdWBook";
				JDTORecord wBookSel	= ydStackLayerDAO.requestFind(sQueryId);
				
				if (wBookSel == null){
					throw new EJBServiceException("작업예약 ID 생성 Error");
				}
													
				sWbookId = StringHelper.evl(wBookSel.getFieldString("WBOOK_SELECT"), "");
				
				/*****************************************************************************************
				 *	작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 작업예약조, 등록자, 등록일시) 한다.
				INSERT INTO TB_YM_WBOOK (
				            WBOOK_ID,  			YD_GP, 	    BAY_GP,     SCH_WORK_KIND, SCH_WORK_LOC_DECISION_METHOD,
				            CRANE_WORD_PUT_LOC, WBOOK_DDTT, WBOOK_DUTY, WBOOK_PARTY,   WBOOK_SCH_TERM, 
				            WBOOK_SCH_ACT_DDTT, REGISTER,   REG_DDTT,   MODIFIER,      MOD_DDTT, 
				            DEL_YN)
				VALUES (?,    							  ?,							     ?,       ?,    'S', 
					    null,                             to_char(sysdate,'YYYYMMDDHH24MI'), ?,       ?,    'T', 
					    to_char(sysdate,'YYYYMMDDHH24MI'),'SYSTEM',                          sysdate, null, null,
					    'N')

				******************************************************************************************/
	 		    sQueryId 	= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
				iSeq 		= ydWBookDAO.requestinsertData(sQueryId, 
															new Object[]{ 
													           sWbookId, 
													           sYdGp, 
													           sBayGp, 
													           YmCommonConst.NEW_SCH_WORK_KIND_SRLO, 
															   YmCommonUtil.getWorkDuty(), 
															   YmCommonUtil.getWorkParty()});	
				
				/******************************************************************************************
				 *  저장품 Table(TB_YM_STOCK)에 WBOOK_ID,STOCK_MOVE_TERM를 Update 한다.
					UPDATE TB_YM_STOCK 
					   SET WBOOK_ID		   = ?, 
					       STOCK_MOVE_TERM = ? 
					 WHERE STOCK_ID = ?
				 ******************************************************************************************/
				String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(SlabNo,"");
				
				sQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
				iSeq 	 = ydStockDAO.requestupdateData(sQueryId, 
													 new Object[]{ 
						    							sWbookId, 
						    							sStockInfo[1], 
						    							SlabNo});
				
				
				/*
				 *	적치단  Table Update(작업요구상태='S'로 변경), STOCK_ID 등록
				 *	UPDATE TB_YM_STACKLAYER 
				 *     SET STACK_LAYER_STAT = ?,
				 *         STOCK_ID 		= ?
				 *   WHERE STACK_COL_GP = ?
				 */
				sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatStock_Id";
				iSeq 	 = ydStackLayerDAO.requestupdateData(sQueryId, 
															 new Object[]{ 
						       									YmCommonConst.STACK_LAYER_STAT_S, 
						       									SlabNo,
						       									sYdGp+sBayGp+RtName});
					
				//Slab Schedule EJB Call syCraneScheduleInfoInsert
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
				Boolean isTrue = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",
													  new  Class[]{String.class},
													  new Object[]{sWbookId });							
				
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
	 * 오퍼레이션명 : A열연 SLAB야드 Line On (MCH)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */ 
	public boolean ASLineInresult(JDTORecord jDTORecord) { 
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		ymCommonDAO dao1 				= ymCommonDAO.getInstance();
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iSeq = 0;

			String sQueryId 			= "";
			String SlabNo               = StringHelper.evl(jDTORecord.getFieldString("SLABNo"), "").trim();
			String sYdGp 				= "0";
			String sBayGp			    = StringHelper.evl(jDTORecord.getFieldString("Dong"), "").trim();
			String register			    = StringHelper.evl(jDTORecord.getFieldString("register"), "").trim();
			
			String sWbookId				= "";
			String RtName = "";
			
			if(YmCommonConst.BAY_GP_B.equals(sBayGp)){
				RtName = YmCommonConst.EQUIP_KIND_0_B_RT;
			}else{
				RtName = YmCommonConst.EQUIP_KIND_0_A_RT;
			}
			
			//저장품 테이블에 해당 저장품이 있는지 체크
			JDTORecord stockJr = dao.getStockInfoWcrGp(SlabNo);
			if(stockJr == null){
				logger.println(LogLevel.DEBUG,this, "A열연 Slab Line On 요구 저장품정보 존재않함="+SlabNo);
				throw new EJBServiceException("A열연 Slab Line On 요구 저장품정보 존재않함"+SlabNo);
			}
			String tWbookId = StringHelper.evl(stockJr.getFieldString("WBOOK_ID"), "");
			
			logger.println(LogLevel.DEBUG,this, "A열연 Slab Line On ="+SlabNo);
			logger.println(LogLevel.DEBUG,this, "A열연 Slab Line On BayGp = ["+sBayGp+"]");
			
	    	if ("".equals(tWbookId)){
					
				sWbookId 		 = dao1.createWBook(sYdGp+sBayGp, 
													YmCommonConst.NEW_SCH_WORK_KIND_SRLI, 
													YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_O,
													sYdGp+sBayGp+RtName);
													//sYdGp+sBayGp+RtName+YmCommonConst.STACK_BED_GP_01+YmCommonConst.STACK_LAYER_GP_01);
				
				/******************************************************************************************
				 *  저장품 Table(TB_YM_STOCK)에 WBOOK_ID,STOCK_MOVE_TERM를 Update 한다.
					UPDATE TB_YM_STOCK 
					   SET WBOOK_ID		   = ?, 
					       STOCK_MOVE_TERM = ? 
					 WHERE STOCK_ID = ?
				 ******************************************************************************************/
				String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(SlabNo,"");
				

				sQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
				iSeq 	 = ydStockDAO.requestupdateData(sQueryId, 
													 new Object[]{ 
						    							sWbookId, 
						    							sStockInfo[1], 
						    							SlabNo});
				
				
				/*
				--적치상태변경
				update TB_YM_STACKLAYER 
				   set STACK_LAYER_STAT = 'S'
				      , MODIFIER = ?
				      , MOD_DDTT = sysdate   
				where  STOCK_ID = ?     --저장품ID
				 */
				sQueryId = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateStockStat";
				iSeq 	 = ydStackLayerDAO.requestupdateData(sQueryId, 
															 new Object[]{ 
						       									register,
						       									SlabNo});
					
				//Slab Schedule EJB Call syCraneScheduleInfoInsert
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
				Boolean isTrue = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",
													  new  Class[]{String.class},
													  new Object[]{sWbookId });							
				
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



	

