package com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao;
/**
 * 
 */

import java.util.List;

import jspeed.base.log.LogService;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.exception.DAOException;


public class YdEquipDownDAO extends CommonDAO{
	public void tcParsing() {
	}

	public void readFicilityInfo() {
	}

	public void changeFicilityWrkStat() {
	}

	public void UpdateFicilityWorking() {
	}

	public void isDataProc() {
	}

	public void insertInfoQueue() {
	}
	
	
	public int insertData(String qeuryId, List editData) throws DAOException {
		return super.insertData(qeuryId, editData.toArray());
	}

}

