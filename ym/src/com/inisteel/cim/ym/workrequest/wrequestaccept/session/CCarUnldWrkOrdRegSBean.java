package com.inisteel.cim.ym.workrequest.wrequestaccept.session;

import java.util.List;

import jspeed.base.ejb.BaseSessionBean;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackColDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="CCarUnldWrkOrdRegEJB" jndi-name="JNDICCarUnldWrkOrdReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CCarUnldWrkOrdRegSBean extends BaseSessionBean {
	private javax.ejb.SessionContext sessionContext;

	private YmComm ymComm = new YmComm();
	public void ejbCreate() {
	}
	/**
	 * 오퍼레이션명 : 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public List getListCarArriveStart(String queryID, String yd_gp){
		YdStackColDAO ydstackcolDAO = new YdStackColDAO();;	    
	    List dataList = null;
	    try{
	    	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	        dataList = ydstackcolDAO.getListData(queryID, new Object[]{yd_gp});	    	
	    	return dataList;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}

}

