package com.inisteel.cim.ym.brentproc.session;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;

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
 * @ejb.bean name="YMTsInfoRegEJB" jndi-name="JNDIYMTsInfoReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class YMTsInfoRegSBean extends BaseSessionBean { 

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
		    	 	EJBConnector ejbConn = new EJBConnector("default","JNDIYMTsInfoReg",this);
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
					
					    EJBConnector ejbConn2 = new EJBConnector("default","JNDIYMYardWrkResReg",this);
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
}

