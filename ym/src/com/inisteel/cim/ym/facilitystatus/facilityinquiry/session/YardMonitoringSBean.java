package com.inisteel.cim.ym.facilitystatus.facilityinquiry.session;

import java.util.List;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTORecord;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YardFicilityDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackerDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="YardMonitoringEJB" jndi-name="JNDIYardMonitoring" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class YardMonitoringSBean extends BaseSessionBean {
	
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
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
	public List getListFacilityTracking(String queryId, String sulbigubun) {
		YardFicilityDAO yardficilitydao = null;	    
	    List facilityTrackList = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	yardficilitydao = new YardFicilityDAO();
	    	facilityTrackList = yardficilitydao.getListFacilityTracking(queryId ,new Object[]{sulbigubun});
	    	return facilityTrackList;
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
	public List getListFacilityTracking(String queryID, List listData) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			YardFicilityDAO yardficilityDAO = new YardFicilityDAO();
			return yardficilityDAO.getListData(queryID, listData);
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
	public List getAYardMonitoringInfo(String queryID, String yard_gp){
		YdStackerDAO ydstackerDAO = null;	    
	    List stackerList = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydstackerDAO = new YdStackerDAO();
	        stackerList = ydstackerDAO.getListData(queryID, new Object[]{yard_gp});
	    	return stackerList;
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
	public List getFacilityTracking(String queryID, List listData) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			YardFicilityDAO yardficilityDAO = new YardFicilityDAO();
			return yardficilityDAO.getListData(queryID, listData);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * 007-04-23 (MCH)A열연 SLAB 야드현황 조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public List getFacilityTracking(String queryID) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			return new YardFicilityDAO().getListFacilityTracking(queryID);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * 2007-04-23 (MCH)A열연 SLAB 야드현황 조회 추가 Pallet 조회	
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public JDTORecord getAYardMonitoringPallet(String queryID, String where){
	    try{ 
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	return new YardFicilityDAO().getfindData(queryID, where);
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}	
}

