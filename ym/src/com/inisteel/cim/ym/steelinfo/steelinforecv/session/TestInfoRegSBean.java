package com.inisteel.cim.ym.steelinfo.steelinforecv.session;

import java.util.List;
import java.util.ArrayList;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.util.StringHelper;
import com.inisteel.cim.common.util.CommonUtil;

import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonDB;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdEquipDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;

import com.inisteel.cim.ym.scheduling.crane.dao.YdLocSearchDAO;
import com.inisteel.cim.ym.scheduling.crane.dao.YdStockMoveRouteDAO;

/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="TestInfoRegXEJB" jndi-name="JNDITestInfoReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class TestInfoRegSBean extends BaseSessionBean {
	Logger logger = null;
	 
	private YmComm ymComm = new YmComm();
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger = new Logger(config);
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
   	public List ejbTest(String sQueryId1, String State, String col_gp, String Bedgp, String Layer_gp){
   		/*	
   		YdStockDAO ydstockDAO = null;	
   		
   		String sQueryId1 = "ym.common.YmCommonDB.setStackLayerStateInfo";
   		int bedList = dao.updateData(sQueryId1,new Object[]{State,ColGp,BedGp,LayerGp});
   		*/
   		return null;
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
	public JDTORecord getInfo(String sQueryID, String stockid){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		CommonDAO dao = new CommonDAO();
		return dao.findByPrimaryKey(sQueryID, new Object[] { stockid });
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
	public int deleteWork(String stockid, String wbookid, String schid) {
	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return 0;
		}
		
		CommonDAO dao = new CommonDAO();		
		
		int iSeq3 = -1;
		
		if(!"".equals(schid)){
			
			String sQueryId1 = "ym.sch_delete";
			int i1 = dao.deleteData(sQueryId1, new Object[] {schid});
					
			String sQueryId2 = "ym.layer_update1";
			int i2 = dao.updateData(sQueryId2, new Object[] {stockid});
			
			String sQueryId3 = "ym.layer_update2";
			int i3 = dao.updateData(sQueryId3, new Object[] {stockid});						
		}
		
		if(!"".equals(wbookid)){
			
			String sQuery1 = "ym.delete_wbook";
			int iSeq1 = dao.deleteData(sQuery1, new Object[] {wbookid});
					
			String sQuery2 = "ym.update_layer";
			int iSeq2 = dao.updateData(sQuery2, new Object[] {stockid});
			
			String sQuery3 = "ym.update_stock";
			iSeq3 = dao.updateData(sQuery3, new Object[] {stockid});
							
		}
		return iSeq3;
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
	public int deleteMoveRoute(String qeuryId1, String qeuryId2, String stock_move_route_id) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return 0;
		}
		
		int resultNum = 0;   
		List listPK = new ArrayList();
		listPK.add(stock_move_route_id);
    	
		try {
			YdStockMoveRouteDAO ydstockmoverouteDAO = new YdStockMoveRouteDAO();	   
			YdLocSearchDAO ydlocsearchDAO = new YdLocSearchDAO();	  		
				resultNum = ydlocsearchDAO.deleteData(qeuryId1, listPK);
				resultNum = ydstockmoverouteDAO.deleteData(qeuryId2, listPK);
			return resultNum;		
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
	public List getListMoveRoute(String queryID, String yd_gp, String bay_gp, String stockId){
		YdStockMoveRouteDAO ydstockmoverouteDAO = null;	    
	    List dataList = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	        ydstockmoverouteDAO = new YdStockMoveRouteDAO();
	        dataList = ydstockmoverouteDAO.getListData(queryID, new Object[]{yd_gp, bay_gp, stockId});	    	
	    	return dataList;
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
	public String sayHello(String name){
	    return "Hello..." + name;
	}
}	
