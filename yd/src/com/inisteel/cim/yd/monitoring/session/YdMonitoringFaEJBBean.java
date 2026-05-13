/**
 * @(#)MonitoringFaEJBBean
 *
 * @version          V1.00
 * @author           신지은
 * @date             2018/08/14
 *
 * @description      야드 모니터링 데이터  처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2018/08/14   신지은        신지은      최초 등록
 */
package com.inisteel.cim.yd.monitoring.session;
   
import java.util.List;
import java.util.Vector;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.or.common.util.CmnUtil;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.yd.monitoring.dao.YdMonitoringCommDAO;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;


/**
 *      [A] 클래스명 : 야드 모니터링 데이터  처리
 *
 * @ejb.bean name="YdMonitoringFaEJB" jndi-name="YdMonitoringFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/


public class YdMonitoringFaEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YdSlabUtils slabUtils = new YdSlabUtils();
	private YdMonitoringCommDAO commDao = new YdMonitoringCommDAO();
	
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	
	/**
	 * GridData - 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getSelectData(GridData gdReq) throws DAOException {
		String methodNm =  "조회[YdMonitoringFaEJB.getSelectData]";
		String logId = slabUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
			                    + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
			                    + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "YdMonitoringSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of getSelectData	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : SMS 사용자 관리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrSmsUserMgt(GridData gdReq) throws DAOException {
		String methodNm = "SMS 사용자 관리[YdMonitoringFaEJB.scrSmsUserMgt]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);

			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "YdMonitoringSeEJB", this);
			//등록처리      
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("scrSmsUserMgt", new Class[] { GridData.class }, new Object[] { gdReq });

			//사용자조회
			gdRtn = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 에러 기준 관리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrErrorControl(GridData gdReq) throws DAOException {
		String methodNm = "에러 기준 관리[YdMonitoringFaEJB.scrErrorControl]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);

			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "YdMonitoringSeEJB", this);
			//등록처리      
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("scrErrorControl", new Class[] { GridData.class }, new Object[] { gdReq });

			//사용자조회
			gdRtn = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 문자전송
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrTransDelaySMSByManual(GridData gdReq) throws DAOException {
		String methodNm = "이송지연 알림 SMS 수동 전송을 위한 Log 처리[YdMonitoringFaEJB.scrTransDelaySMSByManual]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);

			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "YdMonitoringSeEJB", this);
			//등록처리      
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtTransDelaySMSByManual", new Class[] { GridData.class }, new Object[] { gdReq });

			//사용자조회
			gdRtn = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

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

