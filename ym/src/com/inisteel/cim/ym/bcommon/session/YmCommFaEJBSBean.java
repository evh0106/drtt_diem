/**
 * @(#)YmCommFaEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      YM야드 공통관리 Facade EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.ym.bcommon.session;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
/**
 *      [A] 클래스명 : 화면공통관리 Facade EJB
 *
 * @ejb.bean name="YmCommFaEJB" jndi-name="YmCommFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class YmCommFaEJBSBean extends BaseSessionBean {

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
	 *      [A] 오퍼레이션명 : YM야드 코드 조회(WiseGrid)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getYmCode(GridData gdReq) throws DAOException {
		String methodNm = "YM야드코드조회[YmCommFaEJB.getYmCode]";
		String logId = commUtils.getLogId();

		try {
			gdReq.setNavigateValue(methodNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID

			//코드조회
			EJBConnector ejbConn = new EJBConnector("default", "YmCommSeEJB", this);
			return (GridData)ejbConn.trx("getYmCode", new Class[] { GridData.class }, new Object[] { gdReq });
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 화면 도움말 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData setPageHelpInfo(GridData gdReq) throws DAOException {
		String methodNm =  "화면 도움말 등록[YmCommFaEJB.setPageHelpInfo]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "YmCommSeEJB", this);
			ejbConn.trx("setPageHelpInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of insEqpTrblReg
	
	/**
	 * 화면 도움말 - 버튼등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData setPageHelpBtnInfo(GridData gdReq) throws DAOException {
		String methodNm =  "화면 도움말 - 버튼등록[YmCommFaEJB.setPageHelpBtnInfo]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "YmCommSeEJB", this);
			ejbConn.trx("setPageHelpBtnInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of setPageHelpBtnInfo
	
	
	/**
	 * 화면 도움말 - 작업방법(버튼상세) 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData setPageHelpBtnDtlInfo(GridData gdReq) throws DAOException {
		String methodNm =  "화면 도움말 - 작업방법(버튼상세) 등록[YmCommFaEJB.setPageHelpBtnDtlInfo]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "YmCommSeEJB", this);
			ejbConn.trx("setPageHelpBtnDtlInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of setPageHelpBtnDtlInfo
	
	/**
	 * 화면 도움말 - 신규 문서번호 채번
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public JDTORecord getPageHelpDocMaxDocSeq(JDTORecord inRecord) throws DAOException {
		String methodNm =  "화면 도움말 - 신규 문서번호 채번[YmCommFaEJB.getPageHelpDocMaxDocSeq]";
		String logId = commUtils.getLogId();
		JDTORecord outRecord = null;
		try {

			EJBConnector ejbConn = new EJBConnector("default", "YmCommSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("getPageHelpDocMaxDocSeq", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			
			return outRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of getPageHelpDocMaxDocSeq
	
	
	/**
	 * 화면 도움말 - 첨부문서 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public JDTORecord setPageHelpDoc(JDTORecord inRecord) throws DAOException {
		String methodNm =  "화면 도움말 - 첨부문서 등록[YmCommFaEJB.setPageHelpDoc]";
		String logId = commUtils.getLogId();
		JDTORecord outRecord = null;
		try {
			EJBConnector ejbConn = new EJBConnector("default", "YmCommSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("setPageHelpDoc", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			
			return outRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of setPageHelpDoc
	
	/**
	 * 화면 도움말 - 첨부문서 삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public JDTORecord delPageHelpDoc(JDTORecord inRecord) throws DAOException {
		String methodNm =  "화면 도움말 - 첨부문서 등록[YmCommFaEJB.setPageHelpDoc]";
		String logId = commUtils.getLogId();
		JDTORecord outRecord = null;
		try {
			EJBConnector ejbConn = new EJBConnector("default", "YmCommSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("delPageHelpDoc", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			
			return outRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of setPageHelpDoc

}