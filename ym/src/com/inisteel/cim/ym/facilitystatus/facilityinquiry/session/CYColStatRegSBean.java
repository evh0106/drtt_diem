package com.inisteel.cim.ym.facilitystatus.facilityinquiry.session;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTORecord;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.util.MessageHelper;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackColDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackerDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="CYColStatRegEJB" jndi-name="JNDICYColStatReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CYColStatRegSBean extends BaseSessionBean {

	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
	}
	
      /**
	 * 오퍼레이션명 : 
	 *
	 * 야드 적치열 등록현황 정보를 가져와 List로 데이터를 리턴한다.
        * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public List getListStackCol(String queryID, String stack_yd_gp, String stack_bay_gp , String stack_sect_gp){
		YdStackColDAO ydstackcolDAO = null;	    
	    List stackcolList = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
	        ydstackcolDAO = new YdStackColDAO();
	        stackcolList = ydstackcolDAO.getListData(queryID, new Object[]{stack_yd_gp, stack_bay_gp, stack_sect_gp});
	    	//stackcolList = ydstackcolDAO.getListData("ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.getListStackCol", new Object[]{stack_yd_gp,stack_bay_gp,stack_sect_gp});
	    	return stackcolList;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	
        /**
	 * 오퍼레이션명 : 
	 *
	 * 해당 적치열번호에 해당하는 상제정보를 가져와 JDTORecord로 데이터를 리턴한다.
        * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public JDTORecord getStackCol(String queryID, String stackId){
		YdStackColDAO ydstackcolDAO = null;	    
	    JDTORecord returnRecord = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydstackcolDAO = new YdStackColDAO();
	    	returnRecord = ydstackcolDAO.getData(queryID,new Object[]{stackId});
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
	 * 적치열  데이타를 insert한다.
        * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
    public int insertStackColData(List listData) throws EJBServiceException{   	
    	try {  
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
    	    YdStackColDAO ydstackcoldao = new YdStackColDAO();    		
   			return ydstackcoldao.insertData(listData);
    	}catch(DAOException daoe){
            throw daoe;
        }catch(Exception e){
            String msg = MessageHelper.getUserMessage("MSG0055", new String[]{"PK오류"}, "적치열 등록 중에 오류가 발생하였습니다");
            throw new EJBServiceException(msg,e);
        }
    }
    
    
        /**
	 * 오퍼레이션명 : 
	 *
	 * 한건 데이타를 update한다.
        * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */           
    public int updateStackColData(List listData, String stack_col_active_stat, String modifier, String stack_col_gp) throws RemoteException ,EJBServiceException ,DAOException {    	
    	int resNum = 0;
    	try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
    		YdStackColDAO ydstackcolDAO = new YdStackColDAO();    	
    		resNum = ydstackcolDAO.updateData(listData);
    		resNum = updateStackerData(stack_col_active_stat, modifier, stack_col_gp);
    		resNum = updateStackLayerData(stack_col_active_stat, modifier, stack_col_gp);
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
	 * 적치대 데이타를 update한다.
        * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
    public int updateStackerData(String stack_col_active_stat, String modifier, String stack_col_gp) throws RemoteException ,EJBServiceException ,DAOException {    
    	List listData = new ArrayList();
    	listData.add(stack_col_active_stat);
    	listData.add(modifier);
    	listData.add(stack_col_gp);
    	try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
    		YdStackerDAO ydstackerDAO = new YdStackerDAO();    	
    		return ydstackerDAO.updateData("ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.updateStackerStat", listData);

    	}catch(DAOException daoe){
            throw daoe;
        }catch(Exception e){
            throw new EJBServiceException(e);
        }
    }
    
      /**
	 * 오퍼레이션명 : 
	 *
	 * 적치단 데이타를 update한다.
        * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
    public int updateStackLayerData(String stack_col_active_stat, String modifier, String stack_col_gp) throws RemoteException ,EJBServiceException ,DAOException {    
    	List listData = new ArrayList();
    	listData.add(stack_col_active_stat);
    	listData.add(modifier);
    	listData.add(stack_col_gp);
    	try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
    		YdStackLayerDAO ydstacklayerDAO = new YdStackLayerDAO();  	
    		return ydstacklayerDAO.updateData("ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStat", listData);

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
	public int deleteStackColData(List queryList,  String stack_col_gp) throws RemoteException ,EJBServiceException ,DAOException {	
		
    	List listPK = new ArrayList();
    	listPK.add(stack_col_gp);

    	int resultNum = 0;    	
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			YdStackColDAO ydstackcolDAO = new YdStackColDAO();			
			for( int i = 0 ; i < queryList.size() ; i++){
				resultNum = ydstackcolDAO.deleteData(queryList.get(i).toString(), listPK);
			}
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
	 public int updateStackColAct(String stack_col_active_stat, String modifier, String stack_col_gp) throws RemoteException ,EJBServiceException ,DAOException {    
    	List listData = new ArrayList();
    	listData.add(stack_col_active_stat);
    	listData.add(modifier);
    	listData.add(stack_col_gp);

		int resNum = 0;

    	try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
    		YdStackerDAO ydstackerDAO = new YdStackerDAO();    	
    		resNum = ydstackerDAO.updateData("ym.facilitystatus.facilityinquiry.dao.YdStackColDAO.updateStackColStat", listData);
			resNum = updateStackerData(stack_col_active_stat, modifier, stack_col_gp);
    		resNum = updateStackLayerData(stack_col_active_stat, modifier, stack_col_gp);
    		return resNum;

    	}catch(DAOException daoe){
            throw daoe;
        }catch(Exception e){
            throw new EJBServiceException(e);
        }
    }

}

