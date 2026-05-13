/*****************************************************************************
** 프로그램 ID	: /po/jungjung/result/dao/ABReceiveRollingInfoDAO.java
** 작 성 일 자	: 2005/09/28
** 작  성   자	: 최웅주
** 설       명	: 
** --------------------------------------------------------------------------
** 수정이력
** 수정일자		: 
** 수 정 자		: 
** 설    명		: 
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

/**
 * @author bestpro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class ABReceiveRollingInfoDAO extends CommonDAO {
	
	private Logger log = null;
	
	public ABReceiveRollingInfoDAO()
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
	    String queryCode="ym.workrequest.wrequestaccept.ABReceiveRollingInfoDAO.getCoilCommDtl";
	    return super.findList(queryCode, ParamList.toArray());
    }
	
	/* 주문진행자료조회(TB_PM_ORDPROG)*/     		
	public List getOrdProgDtl(List ParamList) throws DAOException {
		/*  ----------------------------------------------------------
			SELECT * FROM TB_PM_ORDPROG
			WHERE ORD_NO =? 
			     and   ORD_DTL =?	
		  -----------------------------------------------------------*/
	    //String queryCode="po.jungjung.result.dao.ABReceiveRollingInfoDAO.getOrdProgDtl";
	    String queryCode="ym.workrequest.wrequestaccept.ABReceiveRollingInfoDAO.getOrdProgDtl";
	    return super.findList(queryCode, ParamList.toArray());
    }
	
	/* 주문공통(TB_SM_ORDCOMM)*/     		
	public List getOrdComm(List ParamList) throws DAOException {
		/*  ----------------------------------------------------------
		 	SELECT * FROM TB_SM_ORDCOMM
 			WHERE   ORD_NO  = ? 
		  -----------------------------------------------------------*/
	    //String queryCode="po.jungjung.result.dao.ABReceiveRollingInfoDAO.getOrdComm";
	    String queryCode="ym.workrequest.wrequestaccept.ABReceiveRollingInfoDAO.getOrdComm";
	    return super.findList(queryCode, ParamList.toArray());
    }

}

