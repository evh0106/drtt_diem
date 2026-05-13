/*
 * @(#) 2후판정정야드 야드저장품등록 Session EJB
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/21
 *
 * @description		야드저장품등록 Session EJB
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/21   김현우      김현우       최초작성
 */

package com.inisteel.cim.yd.jplateyd.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;

import com.inisteel.cim.common.exception.DAOException;

import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO;

/**
 *
 * 야드저장품등록 Session EJB
 *
 * @ejb.bean name="JPlateYdStockSeEJB" jndi-name="JPlateYdStockSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class JPlateYdStockSeEJBBean extends BaseSessionBean  {

	private static final long serialVersionUID = 1L;

	// Session Name
//	private final String SZ_SESSION_NAME = getClass().getName();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */
	public int updYdStockReTX(JDTORecord inRec) throws DAOException, JDTOException {

		JPlateYdStockDAO ydStockDao = new JPlateYdStockDAO();
		int intRtnVal = 0;

		try {

    		intRtnVal = ydStockDao.updYdStockTX(inRec);
    		if (intRtnVal == 0) {
    			return intRtnVal = -1;
    		}

			intRtnVal = 1;

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockReTX

}
