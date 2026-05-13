/**
 * @(#)SlabYdScrSeEJBBean
 *
 * @version          V1.00
 * @author           허철호
 * @date             2012/11/22
 *
 * @description      Slab야드 화면 처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2012/11/22   허철호      허철호      최초 등록
 */
package com.inisteel.cim.yd.jsp.session;
   
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.jsp.dao.SlabYdScrDAO;

/**
 *      [A] 클래스명 : Slab야드 화면 처리
 *
 * @ejb.bean name="SlabYdScrSeEJB" jndi-name="SlabYdScrSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class SlabYdScrSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YdSlabUtils slabUtils = new YdSlabUtils();
	private SlabYdScrDAO scrDao = new SlabYdScrDAO();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	
	/***************************************************************************
	 * 스카핑 픽업 모니터링 정보 조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 스카핑 픽업 모니터링 정보 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getPickUpScarfMonitor(GridData gdReq) throws DAOException {
		String methodNm = "저장위치별정보조회[SlabYdScrSeEJB.getPickUpScarfMonitor] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		try {
			JDTORecordSet jrRst = scrDao.getPickUpScarfMonitor(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			 
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
}
