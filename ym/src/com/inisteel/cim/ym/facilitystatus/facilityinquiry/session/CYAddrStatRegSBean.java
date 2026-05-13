package com.inisteel.cim.ym.facilitystatus.facilityinquiry.session;
import java.rmi.RemoteException;

import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdEquipDAO;
import java.util.ArrayList;
import java.util.List;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTORecord;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.util.MessageHelper;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackColDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YardFicilityDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="CYAddrStatRegEJB" jndi-name="JNDICYAddrStatReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CYAddrStatRegSBean extends BaseSessionBean {
	
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
	public List getListStacker(String queryID, String stack_yd_gp, String stack_bay_gp , String stack_sect_gp, String stack_col_gp){
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
	        stackerList = ydstackerDAO.getListData(queryID, new Object[]{stack_yd_gp, stack_bay_gp, stack_sect_gp, stack_col_gp});
	    	//stackerList = ydstackerDAO.getListData("ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.getListStacker", new Object[]{stack_yd_gp,stack_bay_gp,stack_sect_gp,stack_col_gp});
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
	 * 야드 적치대 등록현황 정보를 가져와 List로 데이터를 리턴한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        	 	           
	public List getExistStockID(String query, String stack_col_gp, String stack_bed_gp){
		YdStackLayerDAO ydstacklayerDAO = null;	    
	    List stackerList = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
	        ydstacklayerDAO = new YdStackLayerDAO();
	        stackerList = ydstacklayerDAO.getExistStockID(query, new Object[]{stack_col_gp, stack_bed_gp});
	    	//stackerList = ydstackerDAO.getListData("ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.getListStacker", new Object[]{stack_yd_gp,stack_bay_gp,stack_sect_gp,stack_col_gp});
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
	 * 적치대  데이타를 insert한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
    public int insertStackerData(List listData, String loopNum, String stack_col_gp, String stack_bed_gp, String stack_layer_x_axis, String stack_layer_y_axis, String stack_layer_z_axis,String register,String modifier) throws EJBServiceException{   	
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
   			resNum =  ydstackerDAO.insertData(listData);

   			resNum = insertStackLayerData(loopNum, stack_col_gp, stack_bed_gp, stack_layer_x_axis, stack_layer_y_axis, stack_layer_z_axis, register, modifier);
   			
   			return resNum;


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
	 * 적치대  데이타를 insert한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */            
    public int insertStackLayerData(String loopNum, String stack_col_gp, String stack_bed_gp, String stack_layer_x_axis, String stack_layer_y_axis, String stack_layer_z_axis,String register,String modifier) throws EJBServiceException{   	
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return 0;
		}
		
    	int resultNum = 0;
    	YdStackLayerDAO ydstacklayerDAO = new YdStackLayerDAO();   
    	int loopingNum = Integer.parseInt(loopNum);
    	List listData 	= null;
    	for(int i = 0 ; i < loopingNum ;  i++){
    		listData = new ArrayList();
    		listData.add(stack_col_gp);
    		listData.add(stack_bed_gp);
    		listData.add(("0"+(i+1)));
    		if(i == 0){
    			listData.add("O");
    			listData.add("E");
    		}else{
    			listData.add("C");
    			listData.add("V");
    		}
    		listData.add(stack_layer_x_axis);
    		listData.add(stack_layer_y_axis);
    		listData.add(stack_layer_z_axis);
    		listData.add(register);
    		listData.add(modifier);  		
	   		resultNum =  ydstacklayerDAO.insertData(listData);	
    	}    	
    	return resultNum;        	
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
	public int deleteData(List queryList,  String stack_col_gp, String STACK_BED_GP) throws RemoteException ,EJBServiceException ,DAOException {	
		
    	List listPK = new ArrayList();
    	listPK.add(stack_col_gp);

    	int resultNum = 0;    	
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0 ;
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
	public int deleteStacker(String queryId2,  String stack_col_gp, String stack_bed_gp) throws RemoteException ,EJBServiceException ,DAOException {	
		
    	List listPK = new ArrayList();
    	listPK.add(stack_col_gp);
		listPK.add(stack_bed_gp);

    	int resultNum = 0;    	
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			YdStackerDAO ydstackerDAO = new YdStackerDAO();		
			resultNum = ydstackerDAO.deleteData(queryId2, listPK);
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
	public int deleteStackLayer(String qeuryID1,String equeryId2, String stack_col_gp, String stack_bed_gp) throws RemoteException ,EJBServiceException ,DAOException {	
		
    	List listPK = new ArrayList();
    	listPK.add(stack_col_gp);
		listPK.add(stack_bed_gp);

    	int resultNum = 0;    	
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			YdStackLayerDAO ydstacklayerDAO = new YdStackLayerDAO();			
			resultNum = ydstacklayerDAO.deleteData(qeuryID1, listPK);
			resultNum = deleteStacker(equeryId2, stack_col_gp, stack_bed_gp);
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
	public JDTORecord getStacker(String queryID, String stack_col_gp, String stack_bed_gp){
		YdStackerDAO ydstackerDAO = null;	    
	    JDTORecord returnRecord = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydstackerDAO = new YdStackerDAO();
	    	returnRecord = ydstackerDAO.getData(queryID,new Object[]{stack_col_gp, stack_bed_gp});
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
	 * 적치대 데이타를 update한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        
    public int updateStackerData(List listData, String stack_bed_active_stat, String stack_bed_x_axis, String stack_bed_y_axis, String stack_bed_z_axis, String modifier, String stack_col_gp, String stack_bed_gp) throws RemoteException ,EJBServiceException ,DAOException {    
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
    		resNum =  ydstackerDAO.updateData("ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.updateStacker", listData);
			resNum =  updateStackLayerData(stack_bed_active_stat, stack_bed_x_axis, stack_bed_y_axis, stack_bed_z_axis, modifier, stack_col_gp, stack_bed_gp);
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
    public int updateStackLayerData(String stack_bed_active_stat, String stack_bed_x_axis, String stack_bed_y_axis, String stack_bed_z_axis, String modifier, String stack_col_gp, String stack_bed_gp) throws RemoteException ,EJBServiceException ,DAOException {    	
    	try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			List listData = new ArrayList();

			listData.add(stack_bed_active_stat);
			listData.add(stack_bed_x_axis);
			listData.add(stack_bed_y_axis);
			listData.add(stack_bed_z_axis);
			listData.add(modifier);
			listData.add(stack_col_gp);
			listData.add(stack_bed_gp);

    		YdStackLayerDAO ydstacklayerDAO = new YdStackLayerDAO();    	
    		return ydstacklayerDAO.updateData("ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayer", listData);
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
    public int updateSStackLayer2(String queryid, List listData) throws RemoteException ,EJBServiceException ,DAOException {    
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
    		return  ydstackerDAO.updateData(queryid, listData);			
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
    public int deleteSStackLayer(String queryid, String dp_STACK_COL_GP, String dp_STACK_BED_GP, String dp_STACK_LAYER_GP) throws RemoteException ,EJBServiceException ,DAOException {	
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return 0 ;
		}
		
    	List listPK = new ArrayList();
    	listPK.add(dp_STACK_COL_GP);
    	listPK.add(dp_STACK_BED_GP);
    	listPK.add(dp_STACK_LAYER_GP);
		try {
			YdStackColDAO ydstackcolDAO = new YdStackColDAO();			
			return ydstackcolDAO.deleteData(queryid, listPK);		
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
    public List getListStackLayer(String queryID, String stack_yd_gp, String stack_bay_gp , String stack_sect_gp, String stack_col_gp){
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
	        stackerList = ydstackerDAO.getListData(queryID, new Object[]{stack_yd_gp + stack_bay_gp + stack_sect_gp + stack_col_gp});
	    	//stackerList = ydstackerDAO.getListData("ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.getListStacker", new Object[]{stack_yd_gp,stack_bay_gp,stack_sect_gp,stack_col_gp});
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
    public JDTORecord getStackLayerCheck3(String queryID, String STACK_COL_GP, String STACK_BED_GP, String STACK_LAYER_GP){ 
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	YdStockDAO ydstockDAO = new YdStockDAO();
	    	return ydstockDAO.getData(queryID,new Object[]{STACK_COL_GP, STACK_BED_GP, STACK_LAYER_GP});
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
    public JDTORecord getStackLayerCheck1(String queryID, String STOCK_ID){ 
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
	    	YdStockDAO ydstockDAO = new YdStockDAO();
	    	return ydstockDAO.getData(queryID,new Object[]{STOCK_ID});
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
    public JDTORecord getStackLayerCheck2(String queryID, String STOCK_ID){ 
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
	    	YdStockDAO ydstockDAO = new YdStockDAO();
	    	return ydstockDAO.getData(queryID,new Object[]{STOCK_ID});
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
    public int insertStackLayer2(String query1, List listData) throws EJBServiceException{ 
    	try {   				
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
    		YdStockDAO ydstockDAO = new YdStockDAO();  			
   			return ydstockDAO.insertMoveProduct(query1, listData);
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
    public List getListCarUnloadBay(String queryID, List listData) throws EJBServiceException ,DAOException{
    	try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
    		YdEquipDAO ydequipDAO = new YdEquipDAO();
    		return ydequipDAO.getListData(queryID, listData);
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
    public int updateCarUnloadBay(String queryid, List listData) throws EJBServiceException ,DAOException {   	 
    	try {	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
    		YdEquipDAO ydequipDAO = new YdEquipDAO(); 
    		return ydequipDAO.updateData(queryid, listData);
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
    public List getListEquipMax(String queryID, List listData) throws EJBServiceException ,DAOException{
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
    public int updateEquipMax(String queryid, List listData) throws EJBServiceException ,DAOException {   	 
    	try {	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			} 
    		YardFicilityDAO yardficilityDAO = new YardFicilityDAO();
    		return yardficilityDAO.updateData(queryid, listData);
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
    public int updateStackLayer(String queryid, List listData) throws EJBServiceException ,DAOException {   	 
    	try {	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
    		YdStackLayerDAO ydstacklayerDAO = new YdStackLayerDAO(); 
    		return ydstacklayerDAO.updateData(queryid, listData);
    	}catch(DAOException daoe){
    		throw daoe;
    	}catch(Exception e){
    		throw new EJBServiceException(e);
    	}
    }
   

	
	
	

}

