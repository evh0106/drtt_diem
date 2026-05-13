package com.inisteel.cim.ym.scheduling.crane.dao;

import java.util.List;

import jspeed.base.log.Logger;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.exception.DAOException;

/**
 * 
 */
public class YdLocSearchDAO  extends CommonDAO{
	
	
	private Logger log = null;
	
	public int deleteData(String queryId, List listData) throws DAOException{
   	 	if(listData == null) {
   	 		log.println("Input(listData) is NULL");
   			throw new DAOException("Input Data is NULL");
   	 	}   	 	
   		return super.deleteData(queryId, listData.toArray());
   	}
	
	
	public List getListLocCol(String queryCode,Object[] objs) throws DAOException{
		return super.findList(queryCode, objs);	
    }
	
	
	
	
	
	public int insertData(String queryId, List listData) throws DAOException{	
	 	if(listData == null) {
	 		log.println("Input(listData) is NULL");
			throw new DAOException("Input Data is NULL");
	 	}		 			
		return super.insertData(queryId,listData.toArray());
	}		
	
}

