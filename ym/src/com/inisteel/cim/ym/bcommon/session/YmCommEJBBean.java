/**
 * @(#)YmCommEJBBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 * 
 * @description      야드관리 공통 처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 *        2017/02/22           조병기      삭제처리 (YmCommEJBSBean.java 로 대체)
 * 
 */
package com.inisteel.cim.ym.bcommon.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.jms.JmsQueueSender;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;

/**
 *      [A] 클래스명 : 야드관리 공통 처리
 *
 * @ejb.bean name="YmCommEJB" jndi-name="YmCommEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/

public class YmCommEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YmCommUtils commUtils = new YmCommUtils();
	private YmCommDAO commDao = new YmCommDAO();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/***************************************************************************
	 * 
	 * 
	 * 삭제처리
	 * 
	 * 
	 * 
	 **************************************************************************/

}
