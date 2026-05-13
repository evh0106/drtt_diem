/*****************************************************************************
** 프로그램 ID	: /po/jungjung/result/dao/ReservCoilProcessReadDAO.java
** 작 성 일 자	    : 2005/10/28
** 작  성   자	    : 박종민
** 설       명	    : 
** --------------------------------------------------------------------------
** 수정이력             :
** 수정일자		: 
** 수 정 자		: 
** 설    명		: 
getCoilCommDtl
*****************************************************************************/
/*
 * Created on 2005. 7. 28.
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inisteel.cim.ym.workrequest.wrequestaccept.dao;

import java.util.ArrayList;
import java.util.List;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.log.*;
import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CommonUtil;
import com.inisteel.cim.common.util.DBUtil;


public class ReservCoilProcessReadDAO extends CommonDAO {
	
	private Logger log = null;
	public ReservCoilProcessReadDAO()
	{
		log = LogService.getInstance().getLogServiceContext().getLogger( "template" );
	}
	
	/* COIL공통 조회(TB_PM_COILCOMM)*/     		
	public List getCoilCommDtl(String coilNo) throws DAOException {
		/*  ----------------------------------------------------------
			SELECT * FROM TB_PM_COILCOMM  WHERE COIL_NO = ?
		  -----------------------------------------------------------*/
		List ParamList = new ArrayList();
		ParamList.add(coilNo);                
	    //String queryCode="po.jungjung.result.dao.ABReceiveRollingInfoDAO.getCoilCommDtl";
	    String queryCode="ym.workrequest.wrequestaccept.ReservCoilProcessReadDAO.getCoilCommDtl";
	    return super.findList(queryCode, ParamList.toArray());
    }

}

