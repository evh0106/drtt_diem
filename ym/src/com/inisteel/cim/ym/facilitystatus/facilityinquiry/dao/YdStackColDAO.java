package com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao;

import java.util.List;

import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.exception.DAOException;
/**
 * 
 */
public class YdStackColDAO extends CommonDAO {
	private Logger log = null;
	
	public int insertData(List listData) {	
	 	if(listData == null) {
	 		log.println("Input(listData) is NULL");
			throw new DAOException("Input Data is NULL");
			}	 	
		String  queryCode = "ym.facilitystatus.facilityinquiry.dao.YdStackColDAO.insertStackCol";			
		return super.insertData(queryCode,listData.toArray());
	}

	
	
	
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
	
	
	


	
	/**
     * 해당 적치열에 해당하는 상세내역을 가져와 JDTORecord로 데이터를 리턴한다.
     * 
     * @param 적치열번호
     * @return
     * @throws 
     */
	public JDTORecord getData(String queryCode,Object[] objs) throws DAOException{
		return super.findByPrimaryKey(queryCode, objs);          
    }
	
	
	
	
	

    
    /**
     * List에 입력된 순서대로 Query값에 Matching하여 데이타를 Update한다. 
     * @param listData 입력할값
     * @return int 
     * @throws DAOException
     */		
   	public int updateData(List listData) throws DAOException{	   	 	
   		String  queryCode = "ym.facilitystatus.facilityinquiry.dao.YdStackColDAO.updateStackCol";   	
   		return super.updateData(queryCode,listData.toArray());
   	}
   	
   	
   	/**
     * List에 입력된 순서대로 Query값에 Matching하여 데이타를 Delete한다.  
     * @param listData 입력할값
     * @return int 
     * @throws DAOException
     */		
   	public int deleteData(String queryId, List listData) throws DAOException{
   		/*
   		DELETE EMP_TEMP
   		WHERE emp_no=? 
   		*/
   	
   	 	if(listData == null) {
   	 		log.println("Input(listData) is NULL");
   			throw new DAOException("Input Data is NULL");
   	 	}
   	 	
   		return super.deleteData(queryId, listData.toArray());
   	}

	/**
     * List에 입력된 순서대로 Query값에 Matching하여 데이타를 Update한다. 
     * @param listData 입력할값
     * @return int 
     * @throws DAOException
     */
	public JDTORecord requestFind(String queryCode) throws DAOException{
		return super.find(queryCode);          
    }
	
	public JDTORecord requestgetData(String queryCode,Object[] objs) throws DAOException{
		return super.findByPrimaryKey(queryCode, objs);          
    }

	public int requestinsertData(String queryCode, Object[] objs) throws DAOException{	
		return super.insertData(queryCode,objs);
	}

   	public int requestupdateData(String queryCode, Object[] objs) throws DAOException{	   	 	
   		return super.updateData(queryCode,objs);
   	}

   
	
}

