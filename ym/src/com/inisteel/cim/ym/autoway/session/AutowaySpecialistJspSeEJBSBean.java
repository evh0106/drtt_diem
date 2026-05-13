/**
 * @(#)AutowaySpecialistJspSeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2020/04/13
 *
 * @description      AutowaySpecialist (B열연 SLAB 야드 화면 관리 Session EJB)
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2020/04/13   박비오      박비오      최초 등록
 *   
 */
package com.inisteel.cim.ym.autoway.session; 

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;


/**
 *      [A] 클래스명 : AutowaySpecialist (B열연 SLAB 야드 화면관리 Session EJB) 
 *
 * @ejb.bean name="AutowaySpecialistJspSeEJB" jndi-name="AutowaySpecialistJspSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @ejb.transaction type="Required"
*/
public class AutowaySpecialistJspSeEJBSBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YmCommDAO commDao = new YmCommDAO();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	/** 
	 *      [A] 오퍼레이션명 : 제철소 조업 현황 조회
	 *      
	 *      @ejb.interface-method
	*/
	public JDTORecordSet getAutowaySpecialist(JDTORecord gdReq) throws DAOException {
		
		String methodNm = "조회[AutowaySpecialistJspSeEJB.getSelectData] < " + gdReq.getResultMsg();
		String logId = gdReq.getResultCode();
		
		//리턴 할 값들을 가지고 있는 JDTORecordSet
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");			//return JDTORecordSet
		String jspeed_query_id = ""; 
		try {
			jspeed_query_id = "com.inisteel.cim.ym.autoway.session.AutowaySpecialistJspSe.getAutowaySpecialist";
			
			commDao.jspSelect(gdReq, outRecSet, jspeed_query_id, logId, methodNm); /* 조회 결과물*/	
			
			// retRecordSet 	= new PrdRptPlnqtyDAO().getMillNmlInvAimIdxRegJm_3rd(jspeed_query_id, sObj);		/* 조회 결과물*/
			
			return outRecSet;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}
	}	
}	
