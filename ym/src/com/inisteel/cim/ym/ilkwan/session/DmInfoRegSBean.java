package com.inisteel.cim.ym.ilkwan.session;

import java.util.List;
import java.util.ArrayList;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.ym.ilkwan.dao.ilkwanDAO;
import com.inisteel.cim.common.level2.util.*;
import com.inisteel.cim.common.parser.Level2Parser;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.YmCommonDB;

import javax.naming.*;
import jspeed.base.record.*;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.*;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTORecord;
import jspeed.base.util.StringHelper;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="DmInfoRegEJB" jndi-name="JNDIDmInfoReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class DmInfoRegSBean extends BaseSessionBean { 

	private Logger logger 	= null;
	private ilkwanDAO dao 	= null;
	
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger 				= new Logger(config);
		dao 					= new ilkwanDAO();
	}
	
	/**
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                    
	public boolean callCraneUpRtInfo(String sMessage){
		
		boolean isSuccess = false;
			
		try{
		
			return isSuccess;
		
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
}

