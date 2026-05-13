package com.inisteel.cim.ym.wrecord.wrecordif.dao;

import java.util.List;

import jspeed.base.log.Logger;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.exception.DAOException;
import jspeed.base.record.JDTORecord;

/**
 * 
 */
public class YdWRsltDAO extends CommonDAO{
	private Logger log = null;
	public List getListData(String queryCode,Object[] objs) throws DAOException{
		return super.findList(queryCode, objs);	
    }
	
	public List getListData(String query, List whereData) throws DAOException{	
		return super.findList(query, whereData.toArray());
	}
	
	public JDTORecord getData(String query, List whereData) throws DAOException{
		return super.findByPrimaryKey(query, whereData.toArray());          
	}
	
	public int deleteData(String qeuryId, List editData) throws DAOException {
		return super.deleteData(qeuryId, editData.toArray());
	}
}

