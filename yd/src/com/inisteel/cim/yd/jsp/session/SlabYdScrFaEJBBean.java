/**
 * @(#)SlabYdScrFaEJBBean
 *
 * @version          V1.00
 * @author           허철호
 * @date             2012/11/22
 *
 * @description      Slab야드 화면 관리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2012/11/22   허철호      허철호      최초 등록
 */
package com.inisteel.cim.yd.jsp.session;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdSlabUtils;

/**
 *      [A] 클래스명 : Slab야드 화면관리
 *
 * @ejb.bean name="SlabYdScrFaEJB" jndi-name="SlabYdScrFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class SlabYdScrFaEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	
	private YdSlabUtils slabUtils = new YdSlabUtils();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	/**
	 *      [A] 오퍼레이션명 : 픽업스카핑 모니터링 조회[C연주Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrPickUpScarfMonitor(GridData gdReq) throws DAOException {
		String methodNm = "픽업스카핑 조회 [SlabYdScrFaEJB.scrPickUpScarfMonitor]";
		String logId = slabUtils.getLogId();
		try {   
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String ejbMethod = "getPickUpScarfMonitor";	   	
				
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdScrSeEJB", this);			
			
			//조회
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });
			slabUtils.printLog(logId, methodNm, "F-");
			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
}
