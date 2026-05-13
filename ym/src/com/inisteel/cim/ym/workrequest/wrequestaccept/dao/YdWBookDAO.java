package com.inisteel.cim.ym.workrequest.wrequestaccept.dao;

import java.util.List;

import jspeed.base.record.JDTORecord;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.exception.DAOException;
/**
 * 
 */
public class YdWBookDAO  extends CommonDAO {

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

	public int deleteWbookInfo(String sWbookId) throws DAOException
	{
		/*
		DELETE tb_ym_wbook
		WHERE wbook_id = :wbook_id 
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteWbookInfo";	
		Object[] params = {sWbookId};		
		return super.deleteData(queryCode, params);
	}
   	public List requestgetListData(String queryCode, Object[] objs) throws DAOException{	   	 	
   		return super.findList(queryCode,objs);
   	}	
}

