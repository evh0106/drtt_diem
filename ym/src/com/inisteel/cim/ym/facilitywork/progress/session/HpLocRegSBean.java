package com.inisteel.cim.ym.facilitywork.progress.session;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTORecord;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.scheduling.crane.dao.YdLocSearchDAO;
import com.inisteel.cim.ym.scheduling.crane.dao.YdSchRuleDAO;
import com.inisteel.cim.ym.scheduling.crane.dao.YdStockMoveRouteDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="HpLocRegEJB" jndi-name="JNDIHpLocReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class HpLocRegSBean extends BaseSessionBean {
	
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 적치대 등록현황 정보를 가져와 List로 데이터를 리턴한다.
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
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
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
	public List getListMoveRoute(String queryID, String yd_gp, String bay_gp, String stockId, String schcode){
		YdStockMoveRouteDAO ydstockmoverouteDAO = null;	    
	    List dataList = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	        ydstockmoverouteDAO = new YdStockMoveRouteDAO();
	        dataList = ydstockmoverouteDAO.getListData(queryID, new Object[]{yd_gp, bay_gp, stockId, schcode});	    	
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
	public List getListSchRule(String queryID, String yd_gp, String bay_gp){
		YdSchRuleDAO ydschruleDAO = null;	    
	    List dataList = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydschruleDAO = new YdSchRuleDAO();
	        dataList = ydschruleDAO.getListData(queryID, new Object[]{yd_gp, bay_gp});	    	
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
	public List getListPairMoveRoute(String queryID, String stock_move_route_id){
		YdStockMoveRouteDAO ydstockmoverouteDAO = null;	    
	    List dataList = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	        ydstockmoverouteDAO = new YdStockMoveRouteDAO();
	        dataList = ydstockmoverouteDAO.getListData(queryID, new Object[]{stock_move_route_id});	    	
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
	public int deleteMoveRoute(String qeuryId1, String qeuryId2, String stock_move_route_id) throws RemoteException ,EJBServiceException ,DAOException {	
		int resultNum = 0;   
		List listPK = new ArrayList();
		listPK.add(stock_move_route_id);
    	
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
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
	public JDTORecord getMoveRoute(String queryID, String stock_move_route_id){
		YdStockMoveRouteDAO ydstockmoverouteDAO = null;	    
	    JDTORecord returnRecord = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydstockmoverouteDAO = new YdStockMoveRouteDAO();
	    	returnRecord = ydstockmoverouteDAO.getData(queryID,new Object[]{stock_move_route_id});
	    	return returnRecord;
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
    public int updateMoveRoute(String queryId1, String queryId2, String stock_move_route_id, String stock_move_route_prior, String stock_move_route_stat, String stack_usage_cd_to, String modifier) throws RemoteException ,EJBServiceException ,DAOException {    
		int resNum = 0;
		List listPk = new ArrayList();
    	try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
    		YdStockMoveRouteDAO ydstockmoverouteDAO = new YdStockMoveRouteDAO();   
			YdLocSearchDAO ydlocsearchDAO = new YdLocSearchDAO();   	
			
			//listPk.add(stock_move_route_id);
    		//resNum =  ydlocsearchDAO.deleteData(queryId1, listPk);
			//listPk.clear();

			listPk.add(stock_move_route_prior);
			listPk.add(stock_move_route_stat);
			listPk.add(stack_usage_cd_to);
			listPk.add(modifier);
			listPk.add(stock_move_route_id);
			resNum =  ydstockmoverouteDAO.updateData(queryId2, listPk);

			return resNum;
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
    public int updateMoveRoute(String queryId1, String queryId2, String stock_move_route_id[], String stock_move_route_prior[], String stock_move_route_stat[], String stack_usage_cd_to[], String modifier) throws RemoteException ,EJBServiceException ,DAOException {    
		int resNum = 0;
		List listPk = new ArrayList();
    	try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
    		YdStockMoveRouteDAO ydstockmoverouteDAO = new YdStockMoveRouteDAO();   
			YdLocSearchDAO ydlocsearchDAO = new YdLocSearchDAO();   
			

			for(int i = 0 ; i < stock_move_route_id.length ; i++){			
				//listPk.add(stock_move_route_id[i]);
				//resNum =  ydlocsearchDAO.deleteData(queryId1, listPk);
				//listPk.clear();

				listPk.add(stock_move_route_prior[i]);
				listPk.add(stock_move_route_stat[i]);
				listPk.add(stack_usage_cd_to[i]);
				listPk.add(modifier);
				listPk.add(stock_move_route_id[i]);
				resNum =  ydstockmoverouteDAO.updateData(queryId2, listPk);
				listPk.clear();
			}

			return resNum;
    	}catch(DAOException daoe){
            throw daoe;
        }catch(Exception e){
            throw new EJBServiceException(e);
        }
    }
    
	/**
	 * 오퍼레이션명 : 
	 *
	 * 적치대  데이타를 insert한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
    public int insertMoveRoute(String queryId, String yd_gp, String bay_gp, String stock_item, String sch_work_kind, String stack_col_usage_cd, String stack_usage_cd_to, String stock_move_term, String stock_move_route_stat, String register) throws EJBServiceException{   	

    	try {   	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			List insertData = new ArrayList();

			insertData.add(yd_gp);
			insertData.add(bay_gp);
			insertData.add(stock_item);
			insertData.add(stack_col_usage_cd);
			insertData.add(sch_work_kind);
			insertData.add(stock_move_term);
			insertData.add(yd_gp);
			insertData.add(bay_gp);
			insertData.add(stock_item);
			insertData.add(stack_col_usage_cd);
			insertData.add(sch_work_kind);
			insertData.add(stock_move_term);
			insertData.add(stock_move_route_stat);
			insertData.add(stack_usage_cd_to);
			insertData.add(register);

    		YdStockMoveRouteDAO ydstockmoverouteDAO = new YdStockMoveRouteDAO();   
   			return ydstockmoverouteDAO.insertData(queryId, insertData);
    	}catch(DAOException daoe){
            throw daoe;
        }catch(Exception e){
            String msg = "적치열 등록 중에 오류가 발생하였습니다";
            throw new EJBServiceException(msg,e);
        }
    }
	/**
	 * 오퍼레이션명 : 
	 *
	 * 적치대  데이타를 insert한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */    
    public List getListTotalLocCol(String queryID, String yd_gp, String bay_gp, String stack_cd, String stock_move_route_id){
    	YdLocSearchDAO ydlocsearchDAO = null;	    
	    List dataList = null;
	    try{
	    	/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
	    	ydlocsearchDAO = new YdLocSearchDAO();
	        dataList = ydlocsearchDAO.getListLocCol(queryID, new Object[]{yd_gp, bay_gp, stack_cd, stock_move_route_id});	    	
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
	 * 적치대  데이타를 insert한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */    
    public List getListChoiceLocCol(String queryID, String stock_move_route_id){
    	YdLocSearchDAO ydlocsearchDAO = null;	    
	    List dataList = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydlocsearchDAO = new YdLocSearchDAO();
	        dataList = ydlocsearchDAO.getListLocCol(queryID, new Object[]{stock_move_route_id});	    	
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
	 * 적치대  데이타를 insert한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */    
    public int insertLocCol(String deleteQueryId, String insertQueryId, String stock_move_route_id, String register, String modifier, String colSelected) throws EJBServiceException{   	
		int resultNum = 0;
		int seq = 1; 
		YdLocSearchDAO ydlocsearchDAO = new YdLocSearchDAO();   
		String loc_search_id = "";

    	try {  
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			deleteLocCol(deleteQueryId, stock_move_route_id);//먼저 지우고 시작한다.

			List insertData = new ArrayList();

			StringTokenizer st = new StringTokenizer(colSelected, "-");
			while (st.hasMoreTokens()) {
				insertData.add(stock_move_route_id);

				if(seq < 10){
					loc_search_id = "0" + seq;
				}else{
					loc_search_id = seq + "";
				}

				insertData.add(loc_search_id);
				insertData.add(st.nextToken());
				insertData.add(loc_search_id);
				insertData.add(register);
				insertData.add(modifier);	
				resultNum = ydlocsearchDAO.insertData(insertQueryId, insertData);
				seq++;
				insertData.clear();
			}  		
			return resultNum;
    	}catch(DAOException daoe){
            throw daoe;
        }catch(Exception e){
            String msg = "적치열 등록 중에 오류가 발생하였습니다";
            throw new EJBServiceException(msg,e);
        }
    }
    
	/**
	 * 오퍼레이션명 : 
	 *
	 * 적치대  데이타를 insert한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public int deleteLocCol(String deleteQueryId, String stock_move_route_id) throws RemoteException ,EJBServiceException ,DAOException {	
		List listPK = new ArrayList();
		listPK.add(stock_move_route_id);    	
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			YdLocSearchDAO ydlocsearchDAO = new YdLocSearchDAO();	   	  		
			return ydlocsearchDAO.deleteData(deleteQueryId, listPK);				
		}catch(DAOException daoe){
            throw daoe;
        }catch(Exception e){
            throw new EJBServiceException(e);
        }
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * 적치대  데이타를 insert한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public int deleteSchRule(String deleteQueryId, String p_sch_rule_id) throws RemoteException ,EJBServiceException ,DAOException {	
		List listPK = new ArrayList();
		listPK.add(p_sch_rule_id);    	
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			YdSchRuleDAO ydschruleDAO = new YdSchRuleDAO();	   	  		
			return ydschruleDAO.deleteData(deleteQueryId, listPK);				
		}catch(DAOException daoe){
            throw daoe;
        }catch(Exception e){
            throw new EJBServiceException(e);
        }
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * 적치대  데이타를 insert한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public int insertSchRule(String queryId, List listData) throws EJBServiceException{   	
    	try {   	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			YdSchRuleDAO ydschruleDAO = new YdSchRuleDAO();   
   			return ydschruleDAO.insertData(queryId, listData);
    	}catch(DAOException daoe){
            throw daoe;
        }catch(Exception e){
            String msg = "적치열 등록 중에 오류가 발생하였습니다";
            throw new EJBServiceException(msg,e);
        }
    }
	/**
	 * 오퍼레이션명 : 
	 *
	 * 적치대  데이타를 insert한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */    
	public int updateSchRule(String queryId, List listData) throws RemoteException ,EJBServiceException ,DAOException {    
		
	    	try {
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return 0;
				}
				
	    		YdSchRuleDAO ydschruleDAO = new YdSchRuleDAO();   
				return  ydschruleDAO.updateData(queryId, listData);
			}catch(DAOException daoe){
	            throw daoe;
	        }catch(Exception e){
	            throw new EJBServiceException(e);
	        }
	    }
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 적치대  데이타를 insert한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public JDTORecord getSchRule(String queryID, String sch_rule_id){
		YdSchRuleDAO ydschruleDAO = new YdSchRuleDAO();   
	    try{
	    	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	return ydschruleDAO.getData(queryID,new Object[]{sch_rule_id});

	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * 적치대  데이타를 insert한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public int updateSchRuleRanking(String queryid, String craneRK,  String modifier) throws RemoteException ,EJBServiceException ,DAOException {   	 
		try {		
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int resNum = 0;
			List listData = new ArrayList();
			YdSchRuleDAO ydschruleDAO = new YdSchRuleDAO(); 
			String craneRanking[] = craneRK.split("-");
			for(int i = 0 ; i < craneRanking.length ; i++){
				listData.add((i+1)+"");
				listData.add(modifier);
				listData.add(craneRanking[i]);
				resNum = ydschruleDAO.updateData(queryid, listData);
				listData.clear();
			}
			return resNum;			
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
    

}

