/**
 * @(#)AutowaySpecialistJspFaEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2020/04/13
 *
 * @description      AutowaySpecialist (B열연 SLAB 야드 화면관리 Facade EJB)
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2020/04/13   박비오      박비오      최초 등록
 * 
 */
package com.inisteel.cim.ym.autoway.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bcommon.util.YmConstant;

/**
 *      [A] AutowaySpecialist(클래스명 : B열연 SLAB 야드 화면관리 Facade EJB)
 *
 * @ejb.bean name="AutowaySpecialistJspFaEJB" jndi-name="AutowaySpecialistJspFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @ejb.transaction type="Required"
*/
public class AutowaySpecialistJspFaEJBSBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YmCommUtils commUtils = new YmCommUtils();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */ 
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	/**
	 * 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * 
	 * @throws DAOException
	 */
	public JDTORecordSet getAutowaySpecialist(JDTORecord gdReq) throws DAOException {
		EJBConnector ejbConn = null;
		System.out.println("getAutowaySpecialist");
		String methodNm =  "오토웨이 시스템담당자[AutowaySpecialistJspFaEJB.getAutowaySpecialist]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_0);
		try {
			commUtils.printLog(logId, methodNm, "F+", gdReq);		
			ejbConn = new EJBConnector("default", "AutowaySpecialistJspSeEJB", this);
			return (JDTORecordSet) ejbConn.trx("getAutowaySpecialist" , new Class[] { JDTORecord.class }, new Object[] { gdReq });
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}	
	
}
