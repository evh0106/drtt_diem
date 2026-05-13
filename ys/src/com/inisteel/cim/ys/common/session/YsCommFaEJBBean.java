/**
 * @(#)YsCommFaEJBBean
 *
 * @version          V1.00
 * @author           조병기
 * @date             2014/12/22
 *
 * @description      야드 공통관리 Facade EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2014/12/22   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.common.session;

import xlib.cmc.GridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ys.common.util.YsCommUtils;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
/**
 *      [A] 클래스명 : 화면공통관리 Facade EJB
 *
 * @ejb.bean name="YsCommFaEJB" jndi-name="YsCommFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class YsCommFaEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YsCommUtils commUtils = new YsCommUtils();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	/**
	 *      [A] 오퍼레이션명 : 특수강야드 코드 조회(WiseGrid)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getYsCode(GridData gdReq) throws DAOException {
		String methodNm = "특수강야드코드조회[YsCommFaEJB.getYsCode]";
		String logId = commUtils.getLogId();

		try {
			gdReq.setNavigateValue(methodNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID

			//코드조회
			EJBConnector ejbConn = new EJBConnector("default", "YsCommSeEJB", this);
			return (GridData)ejbConn.trx("getYsCode", new Class[] { GridData.class }, new Object[] { gdReq });
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 수요가 코드 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getDemanderCdList(GridData gdReq) throws DAOException {
		String methodNm = "특수강야드코드조회[YsCommFaEJB.getDemanderCdList]";
		String logId = commUtils.getLogId();
		
		try{
			gdReq.setNavigateValue(methodNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			//코드조회
			EJBConnector ejbConn = new EJBConnector("default", "YsCommSeEJB", this);
			return (GridData)ejbConn.trx("getDemanderCdList", new Class[] { GridData.class }, new Object[] { gdReq });
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 목적지 코드 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getDestCdList(GridData gdReq) throws JDTOException {
		String methodNm = "특수강야드코드조회[YsCommFaEJB.getDestCdList]";
		String logId = commUtils.getLogId();
		
		try{
			gdReq.setNavigateValue(methodNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			//코드조회
			EJBConnector ejbConn = new EJBConnector("default", "YsCommSeEJB", this);
			return (GridData)ejbConn.trx("getDestCdList", new Class[] { GridData.class }, new Object[] { gdReq });
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 고객사 코드 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getCustCdList(GridData gdReq) throws JDTOException {
		String methodNm = "특수강야드코드조회[YsCommFaEJB.getCustCdList]";
		String logId = commUtils.getLogId();
		
		try{
			gdReq.setNavigateValue(methodNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			//코드조회
			EJBConnector ejbConn = new EJBConnector("default", "YsCommSeEJB", this);
			return (GridData)ejbConn.trx("getCustCdList", new Class[] { GridData.class }, new Object[] { gdReq });
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
}	