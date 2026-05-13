/**
 * @(#)YdCoilL3RcvPIFaEJBBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2022/02/28
 *
 * @description      2열연 COIL 야드 물류진행 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자     요청자  수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2022/02/28   정종균  송정현      최초 등록
 * 
 */
package com.inisteel.cim.ydPI.session;

import com.inisteel.cim.common.exception.DAOException;

import com.inisteel.cim.yd.ccommon.util.CCommUtils;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
 
/**
 *      [A] 클래스명 : PI관련 2열연COIL야드 출하수신 처리
 *
 * @ejb.bean name="YdCoilL3RcvPIFaEJB" jndi-name="YdCoilL3RcvPIFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required" 
*/

public class YdCoilL3RcvPIFaEJBBean extends BaseSessionBean {
	
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
	 * IFTest 전송 (전문 test용)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData sndIfTestPI(GridData gdReq) throws DAOException {
		String mthdNm = "IFTest 전송[CoilL3RcvPIFaEJB.sndIfTestPI]";
		String logId  = commUtils.getLogId();
		
		try {

			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");		
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "YdCoilL3RcvPISeEJB", this);			
			ejbConn.trx("sndIfTestPI", new Class[] { GridData.class }, new Object[] { gdReq });			
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			gdRet.setMessage("처리 완료");	
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	/**
	 * IFTest 전송 (진양 특수강 백업용 multi 전송)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData sndIfTestPIMUL(GridData gdReq) throws DAOException {
		String mthdNm = "IFTest 전송MULTY[CoilL3RcvPIFaEJB.sndIfTestPIMUL]";
		String logId  = commUtils.getLogId();
		
		try {

			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");		
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "YdCoilL3RcvPISeEJB", this);			
			ejbConn.trx("sndIfTestPIMUL", new Class[] { GridData.class }, new Object[] { gdReq });			
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			gdRet.setMessage("처리 완료");	
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * IFTest Layout 변경 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updIfTestDataPI(GridData gdReq) throws DAOException {
		String mthdNm = "IFTest Layout 변경[CoilL3RcvPIFaEJB.updIfTestDataPI]";
		String logId  = commUtils.getLogId();
		
		try {

			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "YdCoilL3RcvPISeEJB", this);			
			ejbConn.trx("updIfTestDataPI", new Class[] { GridData.class }, new Object[] { gdReq });
			
			
			EJBConnector ejbConn1 = new EJBConnector("default", "CCoilJspFaEJB", this);
			GridData gdRet = (GridData)ejbConn1.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}		
}
