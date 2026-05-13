package com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao;

import java.util.List;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.exception.DAOException;

import jspeed.base.log.LogService;
import jspeed.base.log.Logger;

/**
 * 
 */
public class YdSchDAO extends CommonDAO {
	private Logger log = null;
	
	public YdSchDAO() {
		log = LogService.getInstance().getLogServiceContext().getLogger( "ym" );
	}

    /**
     * List에 입력된 순서대로 Query값에 Matching하여 데이타를 Insert한다.  
     * @param listData 입력할값
     * @return int 
     * @throws DAOException
     */		
	public int insertData(List listData) throws DAOException {	
		String  queryCode = "ym.scheduling.crane.dao.YdSchDAO.insertData";			
		return super.insertData(queryCode,listData.toArray());
	}
}

