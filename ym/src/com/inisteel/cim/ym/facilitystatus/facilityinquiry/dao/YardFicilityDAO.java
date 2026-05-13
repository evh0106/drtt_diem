package com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao;

import java.util.List;

import jspeed.base.record.JDTORecord;
import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.exception.DAOException;


/**
 * 
 */
public class YardFicilityDAO extends CommonDAO {
	
	            
	public List getListFacilityTracking(String queryId, Object[] objs) throws DAOException{
		return super.findList(queryId, objs);	
	}
	
	public List getListFacilityTracking(String queryId) throws DAOException{
		return super.findList(queryId);	
	}
	
	public List getListData(String query, List whereData) throws DAOException{	
		return super.findList(query, whereData.toArray());
	}
	
	public int updateData(String qeuryId, List updateData) {
		return super.updateData(qeuryId, updateData.toArray());
	}
	
	//2007-04-23 (MCH) JDTORecord 타입 추가 야드현황조회 Pallet 상태 가져오기(MCH)
	public JDTORecord getfindData(String qeuryId, String where) {
		return super.findByPrimaryKey(qeuryId, where);
	}

}

