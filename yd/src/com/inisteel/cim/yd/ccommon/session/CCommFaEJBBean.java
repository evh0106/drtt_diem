/**
 * @(#)CCommFaEJBBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2019/05/02
 *
 * @description      C열연 야드 공통관리 Facade EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자     요청자  수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2019/05/02   정종균  이현진      최초 등록
 * 
 */
package com.inisteel.cim.yd.ccommon.session;

import xlib.cmc.GridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.ccommon.util.CConstant;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;

/**
 *      [A] 클래스명 : 화면공통관리 Facade EJB
 *
 * @ejb.bean name="CCommFaEJB" jndi-name="CCommFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class CCommFaEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private CCommUtils commUtils = new CCommUtils();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	/**
	 * GridData - 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getTblData(GridData gdReq) throws DAOException {
		String mthdNm = "조회[CCommFaEJB.getTblData]";
		String logId  = commUtils.getLogId();
		try {
			
			commUtils.printLog(logId, mthdNm, "F+");

			EJBConnector ejbConn = new EJBConnector("default", "CCommSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getTblData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, mthdNm, "F-");
			gdRet.setMessage(CConstant.RETN_CD_SUCCESS);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}
	
	/**
	 * GridData - 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData setTblData(GridData gdReq) throws DAOException {
		String mthdNm = "조회[CCommFaEJB.setTblData]";
		String logId  = commUtils.getLogId();
		try {
			
			commUtils.printLog(logId, mthdNm, "F+");

			EJBConnector ejbConn = new EJBConnector("default", "CCommSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("setTblData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, mthdNm, "F-");
			gdRet.setMessage(CConstant.RETN_CD_SUCCESS);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}
	
}