/**
 * @(#)ASlabRcvL2SeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      박판열연 Slab 야드 L3 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.yf.aslab.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;


import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;


/**
 *      [A] 클래스명 : 박판열연 Slab 야드 L3수신 처리
 *
 * @ejb.bean name="ASlabRcvL2SeEJB" jndi-name="ASlabRcvL2SeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class ASlabRcvL2SeEJBSBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	
}	
	