package com.inisteel.cim.ym.scheduling.crane.dao;

import java.util.List;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.exception.DAOException;

import jspeed.base.record.JDTORecord;


/**
 * 
 */
public class StockingBlncBasDAO extends CommonDAO{
	
	/**
     * 야드 적치열 등록현황을 조회한다.
     * 
     * @param 야드구분, 동구분, 스판구분, 열구분
     * @return List
     * @throws 
     */
	public List getListData(String queryCode,Object[] objs) throws DAOException{
		return super.findList(queryCode, objs);	
    }
	
	public List getListData(String query, List whereData) throws DAOException{	
		return super.findList(query, whereData.toArray());
	}
	
	/**
     * List에 입력된 순서대로 Query값에 Matching하여 데이타를 Update한다. 
     * @param listData 입력할값
     * @return int 
     * @throws DAOException
     */		
   	public int updateData(String queryId, List listData) throws DAOException{	   	 	  	
   		return super.updateData(queryId,listData.toArray());
   	}
   	
   	
   	
   	/**
     * List에 입력된 순서대로 Query값에 Matching하여 데이타를 Delete한다.  
     * @param listData 입력할값
     * @return int 
     * @throws DAOException
     */		
   	public int deleteData(String queryId, List listData) throws DAOException{	
   		return super.deleteData(queryId, listData.toArray());
   	}
   	
   	public JDTORecord getData(String query, List whereData) throws DAOException{
   		return super.findByPrimaryKey(query, whereData.toArray());          
   	}
   	
   	
   	
   	
   	
   	/**
	  * List에 입력된 순서대로 Query값에 Matching하여 데이타를 Insert한다.  
	  * @param listData 입력할값
	  * @return int 
	  * @throws DAOException
	  */		
		public int insertData(String qeuryId, List listData) throws DAOException{		 			
			return super.insertData(qeuryId,listData.toArray());
		}		

}

