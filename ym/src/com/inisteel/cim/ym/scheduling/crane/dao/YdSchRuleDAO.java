package com.inisteel.cim.ym.scheduling.crane.dao;

import java.util.List;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.exception.DAOException;

import jspeed.base.log.LogService;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;

/**
 * 
 */
public class YdSchRuleDAO extends CommonDAO {
	private Logger log = null;
	
	public YdSchRuleDAO(){
		log = LogService.getInstance().getLogServiceContext().getLogger( "ym" );
	}
	
	
	
	
	public List getListData(String queryCode,Object[] objs) throws DAOException{
		return super.findList(queryCode, objs);	
    }
	
	
	public int insertData(String queryId, List listData) throws DAOException{	
	 	if(listData == null) {
	 		log.println("Input(listData) is NULL");
			throw new DAOException("Input Data is NULL");
	 	}		 			
		return super.insertData(queryId,listData.toArray());
	}		
	
	
	public int deleteData(String queryId, List listData) throws DAOException{
   	 	if(listData == null) {
   	 		log.println("Input(listData) is NULL");
   			throw new DAOException("Input Data is NULL");
   	 	}   	 	
   		return super.deleteData(queryId, listData.toArray());
   	}
	
	public JDTORecord getData(String queryCode,Object[] objs) throws DAOException{
		return super.findByPrimaryKey(queryCode, objs);          
    }
	
	
	public int updateData(String queryId, List listData) throws DAOException{	   	 	  	
   		return super.updateData(queryId,listData.toArray());
   	}
	

   
	
}

