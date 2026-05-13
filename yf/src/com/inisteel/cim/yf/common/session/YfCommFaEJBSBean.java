/**
 * @(#)YfCommFaEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      박판열연 공통 로직 처리 Facade EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.yf.common.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.sb.common.util.CmnUtil;
import com.inisteel.cim.yf.common.YfCommUtils;
import com.inisteel.cim.yf.common.dao.YfCommDAO;


/**
 *      [A] 클래스명 : 박판열연 공통 로직 처리
 *
 * @ejb.bean name="YfCommFaEJB" jndi-name="YfCommFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True 
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class YfCommFaEJBSBean extends BaseSessionBean 
{
	
	private static final long serialVersionUID = 1L;
	
	private Logger logger = new Logger("yf");
	private YfCommUtils commUtils = new YfCommUtils();
	private YfCommDAO commDao = new YfCommDAO();	
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException 
	{
		
	}	
	
	
	/**
	 *      [A] 오퍼레이션명 : Yf야드 코드 조회(WiseGrid)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getYfCode(GridData gdReq) throws DAOException {
		String methodNm = "Yf야드코드조회[YfCommFaEJB.getYfCode]";
		String logId = commUtils.getLogId();

		try {
			gdReq.setNavigateValue(methodNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID

			//코드조회
			EJBConnector ejbConn = new EJBConnector("default", "YfCommSeEJB", this);
			return (GridData)ejbConn.trx("getYfCode", new Class[] { GridData.class }, new Object[] { gdReq });
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
//	2019-12-10 사용안함 삭제검토
//	/**
//	 *      [A] 오퍼레이션명 : Yf야드 코드 조회(WiseGrid)
//	 *
//	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 *      @param GridData gdReq
//	 *      @return GridData
//	 *      @throws DAOException
//	*/
//	public GridData getYfGridCode(GridData gdReq) throws DAOException {
//		String methodNm = "Yf야드그리드코드조회[YfCommFaEJB.getYfGridCode]";
//		String logId = commUtils.getLogId();
//
//		try {
//			gdReq.setNavigateValue(methodNm); //상위 Method 명
//			gdReq.setIPAddress(logId); //Logging 을 위한 ID
//
//			//코드조회
//			EJBConnector ejbConn = new EJBConnector("default", "YfCommSeEJB", this);
//			return (GridData)ejbConn.trx("getYfGridCode", new Class[] { GridData.class }, new Object[] { gdReq });
//		} catch(DAOException e) {
//			throw e;
//		} catch(Exception e) {
//			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
//		}
//	}
	
	/**
	 * 화면 도움말 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData setPageHelpInfo(GridData gdReq) throws DAOException {
		String methodNm =  "화면 도움말 등록[YfCommFaEJB.setPageHelpInfo]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "YfCommSeEJB", this);
			ejbConn.trx("setPageHelpInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
			ejbConn = new EJBConnector("default", "ACoilJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
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
		String methodNm =  "화면 도움말 - 버튼등록[YfCommFaEJB.setPageHelpBtnInfo]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "YfCommSeEJB", this);
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
	 * 화면 도움말 - 버튼삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delPageHelpInfo(GridData gdReq) throws DAOException {
		String methodNm =  "화면 도움말 - 버튼삭제[YfCommFaEJB.delPageHelpInfo]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "YfCommSeEJB", this);
			ejbConn.trx("delPageHelpInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
			ejbConn = new EJBConnector("default", "ACoilJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of delPageHelpInfo
	
	/**
	 * 화면 도움말 - 작업방법(버튼상세) 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData setPageHelpBtnDtlInfo(GridData gdReq) throws DAOException {
		String methodNm =  "화면 도움말 - 작업방법(버튼상세) 등록[YfCommFaEJB.setPageHelpBtnDtlInfo]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "YfCommSeEJB", this);
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
		String methodNm =  "화면 도움말 - 신규 문서번호 채번[YfCommFaEJB.getPageHelpDocMaxDocSeq]";
		String logId = commUtils.getLogId();
		JDTORecord outRecord = null;
		try {

			EJBConnector ejbConn = new EJBConnector("default", "YfCommSeEJB", this);
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
		String methodNm =  "화면 도움말 - 첨부문서 등록[YfCommFaEJB.setPageHelpDoc]";
		String logId = commUtils.getLogId();
		JDTORecord outRecord = null;
		try {
			EJBConnector ejbConn = new EJBConnector("default", "YfCommSeEJB", this);
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
	public GridData delPageHelpDoc(GridData gdReq) throws DAOException {
		String methodNm =  "화면 도움말 - 첨부문서 삭제[YfCommFaEJB.setPageHelpDoc]";
		String logId = commUtils.getLogId();
		
		try {
			EJBConnector ejbConn = new EJBConnector("default", "YfCommSeEJB", this);
			ejbConn.trx("delPageHelpDoc", new Class[] { GridData.class }, new Object[] { gdReq });
			
			ejbConn = new EJBConnector("default", "ACoilJspSeEJB", this);
			GridData gdRtn = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			return gdRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of setPageHelpDoc
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스Test
	 *      - 특수강
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData mgtIFTest(GridData gdReq) throws DAOException 
	{
		try 
		{
			EJBConnector ejbConn = new EJBConnector("default", "YfCommSeEJB", this);
			String trtGp = CmnUtil.nvl(gdReq.getParam("V_TRT_GP"), "");	//처리구분
			String methodName = "";
			
			if("I".equals(trtGp)) 
			{
				methodName = "updIFTest"; //등록
			} 
			else if("N".equals(trtGp)) 
			{
				methodName = "sndIFTest"; //전송
			} 
			else 
			{
				methodName = "getIFTest"; //조회
			}

			return (GridData)ejbConn.trx(methodName, new Class[] { GridData.class }, new Object[] { gdReq });
		} 
		catch(DAOException ex) 
		{
			throw ex;
		}
		catch(Exception e) 
		{
			CmnUtil.printErrorLog("인터페이스Test", this, e);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 인터페이스Test(Multi전송)
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData mgtIFTestMulti(GridData gdReq) throws DAOException 
	{
		try 
		{
			EJBConnector ejbConn = new EJBConnector("default", "YfCommSeEJB", this);
			return (GridData)ejbConn.trx("sndIFTestMulti", new Class[] { GridData.class }, new Object[] { gdReq });
		} 
		catch(Exception e) 
		{
			CmnUtil.printErrorLog("인터페이스Test(Multi전송)", this, e);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 인터페이스Layout등록
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData mgtIFLayoutReg(GridData gdReq) throws DAOException 
	{
		try 
		{
			EJBConnector ejbConn = new EJBConnector("default", "YfCommSeEJB", this);
			String trtGp = CmnUtil.nvl(gdReq.getParam("V_TRT_GP"), "");	//처리구분
			String methodName = "getIFLayoutRegB";
			
			if("SL".equals(trtGp)) 
			{
				methodName = "getIFLayoutRegL"; //Layout조회
			} 
			else if("IB".equals(trtGp)) 
			{
				methodName = "insIFLayoutRegB"; //기본정보등록
			} 
			else if("IL".equals(trtGp)) 
			{
				methodName = "insIFLayoutRegL"; //Layout등록
			} 
			else if("DB".equals(trtGp)) 
			{
				methodName = "delIFLayoutRegB"; //삭제
			}

			return (GridData)ejbConn.trx(methodName, new Class[] { GridData.class }, new Object[] { gdReq });
		} 
		catch(Exception e) 
		{
			CmnUtil.printErrorLog("인터페이스Layout등록", this, e);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
    
}	
	
