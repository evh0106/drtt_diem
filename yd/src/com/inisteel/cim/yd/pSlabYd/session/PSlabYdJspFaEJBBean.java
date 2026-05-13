/**
 * @(#)PSlabJspFaEJBBean
 *
 * @version          V1.00
 * @author           염용선
 * @date             2020/05/06
 *
 * @description      Slab야드 화면 처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2020/05/06   염용선      염용선      최초 등록
 */
package com.inisteel.cim.yd.pSlabYd.session;


import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.common.util.MessageHelper;
import com.inisteel.cim.yd.pSlabCommon.util.PSlabYdUtils;
/**
 *      [A] 클래스명 : Slab야드 화면관리
 *
 * @ejb.bean name="PSlabYdJspFaEJB" jndi-name="PSlabYdJspFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class PSlabYdJspFaEJBBean extends BaseSessionBean {
	// Session Name
	private String szSessionName=getClass().getName();
	private static final long serialVersionUID = 1L;
	private PSlabYdUtils slabUtils = new PSlabYdUtils();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	/***************************************************************************
	 * Slab야드 화면
	 **************************************************************************/

	
	/**
	 * GridData - 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getSelectData(GridData gdReq) throws DAOException {
		String mthdNm = "조회[PSlabYdJspFaEJB.getSelectData]";
		String logId  = slabUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
			                    + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
			                    + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			
			slabUtils.printLog(logId, mthdNm, "F+");

			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, mthdNm, "F-");
			gdRet.setMessage("SUCCESS");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}		

	/**
	 * 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSelectData(JDTORecord recPara) throws DAOException {
		String mthdNm = "조회[PSlabYdJspFaEJB.getSelectData]";
		String logId  = slabUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + slabUtils.trim(recPara.getFieldString("jsp_page_nm")) 
			                    + "("   + slabUtils.trim(recPara.getFieldString("jsp_page_id")) 
			                    + ")_"  + slabUtils.trim(recPara.getFieldString("jsp_page_func_nm"));
			
			slabUtils.printLog(logId, mthdNm, "F+");

			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecordSet jsRecordSet = (JDTORecordSet)ejbConn.trx("getSelectData", new Class[] { JDTORecord.class }, new Object[] { recPara });

			slabUtils.printLog(logId, mthdNm, "F-");
			
			return jsRecordSet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}	

	
	
	/**
	 *      [A] 오퍼레이션명 : Slab야드 코드 조회(WiseGrid)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getSlabYdCode(GridData gdReq) throws DAOException {
		String methodNm = "Slab야드코드조회[PSlabYdJspFaEJB.getSlabYdCode]";
		String logId = slabUtils.getLogId();

		try {
			gdReq.setNavigateValue(methodNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID

			//코드조회
			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			return (GridData)ejbConn.trx("getSlabYdCode", new Class[] { GridData.class }, new Object[] { gdReq });
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	

	/**
	 * 스케줄점검 실행
	 * 후판슬라브야드 > 작업계획 > 크레인작업예약관리
	 * 염용선 2020.06.09
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData chkCrnWrkBookMgtSC(GridData gdReq) throws DAOException {
		String methodNm = "작업예약정보조회[PSlabYdJspFaEJB.chkCrnWrkBookMgtSC]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("chkCrnWrkBookMgtSC", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			gdRet.setMessage(rtnMsg);
			slabUtils.printLog(logId, methodNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 * 스케줄기동
	 * 후판슬라브야드 > 작업계획 > 크레인작업예약관리
	 * 염용선 2020.06.09
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtCrnWrkBookMgtSS(GridData gdReq) throws DAOException {
		String methodNm = "작업예약정보조회-스케줄기동[PSlabYdJspFaEJB.trtCrnWrkBookMgtSS]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trtCrnWrkBookMgtSS", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 우선순위변경
	 * 후판슬라브야드 > 작업계획 > 크레인작업예약관리
	 * 염용선 2020.06.09
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updWrkBookPrior(GridData gdReq) throws DAOException {
		String methodNm = "작업예약정보조회-우선순위변경[PSlabYdJspFaEJB.updWrkBookPrior]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updWrkBookPrior", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 크레인지정
	 * 후판슬라브야드 > 작업계획 > 크레인작업예약관리
	 * 염용선 2020.06.09
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updWrkBookCrn(GridData gdReq) throws DAOException {
		String methodNm = "작업예약정보조회-크레인지정[PSlabYdJspFaEJB.updWrkBookCrn]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updWrkBookCrn", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 작업예약삭제
	 * 후판슬라브야드 > 작업계획 > 크레인작업예약관리
	 * 염용선 2020.06.17
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "작업예약정보조회-작업예약삭제[PSlabYdJspFaEJB.delWrkBook]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("delWrkBook", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
            GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * To위치변경
	 * 후판슬라브야드 > 작업계획 > 크레인작업예약관리
	 * 염용선 2020.06.17
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updWrkBookToGuide(GridData gdReq) throws DAOException {
		String methodNm = "작업예약정보조회-To위치변경[PSlabYdJspFaEJB.updWrkBookToGuide]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrWrkBookToGuide = (JDTORecord)ejbConn.trx("updWrkBookToGuide", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrWrkBookToGuide.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrWrkBookToGuide.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");

            GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 크레인변경
	 * 후판슬라브야드 > 작업계획 > 크레인작업관리
	 * 염용선 2020.06.09
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtCrnWrkMgtCM(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리-크레인변경[PSlabYdJspFaEJB.trtCrnWrkMgtCM]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrCrnWrkMgtCM = (JDTORecord)ejbConn.trx("trtCrnWrkMgtCM_BF", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrCrnWrkMgtCM.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrCrnWrkMgtCM.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrCrnWrkMgtCM.setResultCode(logId);
				jrCrnWrkMgtCM.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrCrnWrkMgtCM });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 순위변경
	 * 후판슬라브야드 > 작업계획 > 크레인작업관리
	 * 염용선 2020.06.09
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtCrnWrkMgtPM(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리-순위변경[PSlabYdJspFaEJB.trtCrnWrkMgtPM]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trtCrnWrkMgtPM", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq); 
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 권하위치변경
	 * 후판슬라브야드 > 작업계획 > 크레인작업관리
	 * 염용선 2020.06.09
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtCrnWrkMgtDM(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리-권하위치변경[PSlabYdJspFaEJB.trtCrnWrkMgtDM]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trtCrnWrkMgtDM", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 보류/해제
	 * 후판슬라브야드 > 작업계획 > 크레인작업관리
	 * 염용선 2020.06.09
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtCrnWrkMgtHR(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리-보류/해제[PSlabYdJspFaEJB.trtCrnWrkMgtHR]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trtCrnWrkMgtHR", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 작업취소
	 * 후판슬라브야드 > 작업계획 > 크레인작업관리
	 * 염용선 2020.06.09
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtCrnWrkMgtWC(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리-작업취소[PSlabYdJspFaEJB.trtCrnWrkMgtWC]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trtCrnWrkMgtWC", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 스케줄 취소
	 * 후판슬라브야드 > 작업계획 > 크레인작업관리
	 * 염용선 2020.06.09
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtCrnWrkMgtSC(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리-스케줄취소[PSlabYdJspFaEJB.trtCrnWrkMgtSC]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trtCrnWrkMgtSC", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 * 크레인상태설정 >권하실적처리
	 * 후판슬라브야드 > 작업계획 > 크레인작업관리
	 * 염용선 2020.06.09
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtCrnStatSet(GridData gdReq) throws DAOException {
		String methodNm = "크레인상태설정[PSlabYdJspFaEJB.trtCrnStatSet]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trtCrnStatSet", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 크레인작업
	 * 후판슬라브야드 > Monitoring> 저장위치별정보조회
	 * 염용선 2020.08.11
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtMvStkWrkBookReg(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업 예약생성[PSlabYdJspFaEJB.trtMvStkWrkBookReg]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trtMvStkWrkBookReg", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 크레인작업
	 * 후판슬라브야드 > Monitoring> 저장위치별정보조회
	 * 염용선 2020.08.11
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updateStlMessagePa(GridData gdReq) throws DAOException {
		String methodNm = "크레인상태설정[PSlabYdJspFaEJB.updateStlMessagePa]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updateStlMessagePa", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			/*if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}*/
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 벤딩처리  : 2020.08.18
	 * 염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData updStockBendReg(GridData gdReq) throws JDTOException {
		String methodNm = "벤딩처리(Fa)[PSlabYdJspFaEJB.updStockBendReg]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updStockBendReg", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			/*if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}*/
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
			
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of updStockBendReg		
	
	
	/**
	 * 압연지시조회 Dummy이적지시 : 2020.08.18
	 * 염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData trtMillWoInqDM(GridData gdReq) throws JDTOException {
		String methodNm = "압연지시조회 Dummy이적지시 [PSlabYdJspFaEJB.trtMillWoInqDM]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trtMillWoInqDM", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
			
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 * 위치검색베드    (등록)		
	 * 야드관리 > 후판슬라브야드 [신] > 설비관리 > 위치검색Bed (TM1) 		
	 * PYS : 2020-08-20
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData insLocSrchRng2(GridData gdReq) throws DAOException {
		String methodNm = "위치검색베드(Fa)[PSlabYdJspFaEJB.insLocSrchRng2]";
		String logId    = "";
		
		try {
			logId   = slabUtils.getLogId();
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("insLocSrchRng2", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 		
	/**
	 * 위치검색베드    (등록)		
	 * 야드관리 > 후판슬라브야드 [신] > 설비관리 > 위치검색Bed (TM1) 		
	 * yys : 2020-08-20
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData insLocSrchRngNew(GridData gdReq) throws DAOException {
		String methodNm = "위치검색베드(Fa)[PSlabYdJspFaEJB.insLocSrchRng2]";
		String logId    = "";
		
		try {
			logId   = slabUtils.getLogId();
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("insLocSrchRngNew", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 		
	

	/**
	 * 위치검색베드    					수정(상단부) 	 
	 * 야드관리 > 후판슬라브야드 [신] > 설비관리 > 위치검색Bed (TM1) 
	 * PYS : 2020-08-20
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData updYdLocSrchRng(GridData gdReq) throws JDTOException {
		String methodNm = "위치검색베드(Fa)[PSlabYdJspFaEJB.updYdLocSrchRng]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updYdLocSrchRng", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 		
	
	/**
	 * 위치검색베드    					수정(상단부) 	 
	 * 야드관리 > 후판슬라브야드 [신] > 설비관리 > 위치검색Bed (TM1) 
	 * YYS : 2020-08-20
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData updYdLocSrchRngNew(GridData gdReq) throws JDTOException {
		String methodNm = "위치검색베드(Fa)[PSlabYdJspFaEJB.updYdLocSrchRng]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updYdLocSrchRngNew", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 		
	


	/**
	 * 위치검색베드    					 수정(하단부 ) 		
	 * 야드관리 > 후판슬라브야드 [신] > 설비관리 > 위치검색Bed (TM1) 			 
	 * PYS : 2020-08-20
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData updYdLocSrchBed(GridData gdReq) throws JDTOException {
		String methodNm = "위치검색베드(Fa)[PSlabYdJspFaEJB.updYdLocSrchRng]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updYdLocSrchBed", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 		
	

	/**
	 * 위치검색베드    					(삭제)		
	 * 야드관리 > 후판슬라브야드 [신] > 설비관리 > 위치검색Bed (TM1) 		 
	 * PYS : 2020-08-20
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData delYdLocSrchBed(GridData gdReq) throws JDTOException {
		String methodNm = "위치검색베드(Fa)[PSlabYdJspFaEJB.delYdLocSrchBed]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("delYdLocSrchBed", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			//GridData gdRet   = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 		
	

	/**
	 * 위치검색베드    (삭제) 			-상단부 삭제 (추가사항) 		
	 * 야드관리 > 후판슬라브야드 [신] > 설비관리 > 위치검색Bed (TM1) 	 
	 * PYS : 2020-08-20
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData delYdLocSrchBedii(GridData gdReq) throws JDTOException {
		String methodNm = "위치검색베드(Fa)[PSlabYdJspFaEJB.delYdLocSrchBedii]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("delYdLocSrchBedii", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 		


	/**
	 * 스케쥴기준관리     수정 	
	 * 야드관리 > 후판슬라브야드 [신] > 설비관리 > 스케줄기준관리 (TM1)				 
	 * PYS : 2020-08-20
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData updslabYdSchStdMgt(GridData gdReq) throws JDTOException {
		String methodNm = "스케쥴기준관리(Fa)[PSlabYdJspFaEJB.updslabYdSchStdMgt]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updslabYdSchStdMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 		
	


	/**
	 * 스케쥴기준관리    -스케쥴 기동  		
	 * 야드관리 > 후판슬라브야드 [신] > 설비관리 > 스케줄기준관리 (TM1)			 
	 * PYS : 2020-08-25
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData schStart(GridData gdReq) throws JDTOException {
		String methodNm = "스케쥴기동관리(Fa)[PSlabYdJspFaEJB.schStart]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID
 
			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("schStart", new Class[] { GridData.class     }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if ("1".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 		


	/**
	 * 설비정비이력  등록     										(단건처리)
	 * 야드관리 > 후판슬라브야드 [신] > 설비관리 > 설비정비이력조회 (TM1) 
	 * PYS : 2020-08-21
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param rcvMsg
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData InsEqpPauseHist(GridData gdReq) throws JDTOException {
		String methodNm = "설비정비이력조회(등록)(Fa)[PSlabYdJspFaEJB.InsEqpPauseHist]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID   gdReq

			
			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("InsEqpPauseHist", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd:" + rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			//GridData gdRet   = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdReq.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdReq.setMessage("설비이력 등록 처리가 완료 됐습니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 		
	

	/**           
	 * 저장위치 수정 -1.테스트슬라브재 등록  			                (※주요기능 :1.테스트재, 2.수정)   
	 * 야드관리 > 후판슬라브야드 [신] > 설비관리 > 저장위치수정     		 
	 * PYS : 2020-08-25
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData makePlateYdTestSlab(GridData gdReq) throws DAOException {
		String methodNm = "후판슬라브 테스트 슬라브 등록(Fa)[PSlabYdJspFaEJB.makePlateYdTestSlab]";
		String logId = slabUtils.getLogId();
		
		//String szMethodName = "makePlateYdTestSlab";
		//String szLogMsg = "";
		//String szOperationName = "후판슬라브 테스트 슬라브 등록";
		/* ------------------------------------------------------------------------------------------------------
		*  CASE 1. 붉은 바탕 슬라브- STKLYR엔 존재하지만, STOCK에는 존재하지 않는 슬라브. SLABCOMM 및 MSLABCOMM 에는 존재
		*  CASE 2. 빈칸 슬라브(나타내지 않음) - STKLYR상은 존재하지 않지만, STOCK 상에는 존재하는 슬라브. SLABCOMM 및 MSLABCOMM 에는 존재
		*  따라서, CASE 1의 경우 SLABCOMM 의 재료를 바탕으로 STOCK 에 삽입, CASE2 의 경우 존재하는 STOCK 정보를 STKLYR에 작업자가 INSERT 할 수 있게끔 기능
		* ------------------------------------------------------------------------------------------------------*/
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress    (logId);    // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("makePlateYdTestSlab", new Class[] { GridData.class }, new Object[] { gdReq });

			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 		
	
	/**
	 * 저장위치 수정 - 현재 저장위치 체크
	 * 민종근 : 2021.01.12
	 * 야드관리 > 후판슬라브야드 [신] > 설비관리 > *저장위치수정     		 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws JDTOException
	 * @throws DAOException
	 */	
	public GridData chkSlabYdStkPosSlab(GridData gdReq) throws JDTOException {
		String methodNm = "저장위치수정-현재 저장위치 체크 (Fa)[PSlabYdJspFaEJB.chkSlabYdStkPosSlab]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("chkSlabYdStkPosSlab", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd:" + rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
//			//화면 메시지
//			if (!"1".equals(rtnCd)) {
//				m_ctx.setRollbackOnly();
//			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdReq.setMessage(rtnMsg);
			return gdReq;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 저장위치 수정 -2.수정      			                		(※주요기능 :1.테스트재, 2.수정)                 
	 * 야드관리 > 후판슬라브야드 [신] > 설비관리 > 저장위치수정     		 
	 * PYS : 2020-08-21
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws JDTOException
	 * @throws DAOException
	 */	
	public GridData updSlabYdStkPosFix(GridData gdReq) throws JDTOException {
		String methodNm = "저장위치수정-수정처리(Fa)[PSlabYdJspFaEJB.updSlabYdStkPosFix]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updSlabYdStkPosFix", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd:" + rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if ("1".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 	 
	
	/**
	 * 슬라브야드 야드저장위치좌표설정 -열 수정(1)
	 * 야드관리 > 후판슬라브야드 [신] > 설비관리 > 저장위치좌표설정  
	 * PYS  2020-09-01
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  gdReq
	 * @return GridData
  	 * @throws JDTOException
	 * @throws DAOException
	 */
	public GridData updSlabYdStkPosSet(GridData gdReq) throws JDTOException {
		String methodNm = "슬라브야드 야드저장위치좌표설정-수정(열정보)[PSlabYdJspFaEJB.updSlabYdStkPosSet]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updSlabYdStkPosSet", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 * 슬라브야드 야드저장위치좌표설정 -Bed수정2
	 * 야드관리 > 후판슬라브야드 [신] > 설비관리 > 저장위치좌표설정  
	 * PYS  2020-09-01
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSlabYdStkPosSetBed(GridData gdReq) throws DAOException {
		String methodNm = "슬라브야드 야드저장위치좌표설정-수정(Bed정보)[PSlabYdJspFaEJB.updSlabYdStkPosSetBed]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updSlabYdStkPosSetBed", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			// ROLLBACK 시 전문 발생
			if ("1".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
  
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
 			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	

	/**
	 * 야드설비(크레인)사양설정 -등록 
	 * 야드관리 > 후판슬라브야드 [신] > 설비관리 > 야드설비(크레인)사양설정  
	 * PYS  2020-09-01
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData insslabYdEqpSetSpec(GridData gdReq) throws DAOException {
		String methodNm = "야드설비(크레인)사양설정-등록 [PSlabYdJspFaEJB.insslabYdEqpSetSpec]";
		String logId = slabUtils.getLogId();

		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("insslabYdEqpSetSpec", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 야드설비(크레인)사양설정 -수정
	 * 야드관리 > 후판슬라브야드 [신] > 설비관리 > 야드설비(크레인)사양설정   
	 * PYS  2020-09-01
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updslabYdEqpSetSpec(GridData gdReq) throws DAOException {
		String methodNm = "야드설비(크레인)사양설정 -수정  [PSlabYdJspFaEJB.updslabYdEqpSetSpec]";
		String logId = slabUtils.getLogId();

		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updslabYdEqpSetSpec", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 * 야드설비(크레인)사양설정 -삭제
	 * 야드관리 > 후판슬라브야드 [신] > 설비관리 > 야드설비(크레인)사양설정
	 * PYS  2020-09-01
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delslabYdEqpSetSpec(GridData gdReq) throws DAOException {
		String methodNm = "야드설비(크레인)사양설정 -삭제 [PSlabYdJspFaEJB.delslabYdEqpSetSpec]";
		String logId = slabUtils.getLogId();

		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("delslabYdEqpSetSpec", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	//+++++++++++++++++++-+-++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	
	/**
	 *      [A] 오퍼레이션명 : 대차스케줄관리 대차초기화
	 *      염용선 2020-08-24
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public GridData trtTcarSchMgtTI(GridData gdReq) throws JDTOException {
		String methodNm = "대차스케줄관리 대차초기화[PSlabYdJspFaEJB.trtTcarSchMgtTI]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trtTcarSchMgtTI", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd:" + rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 		
	
	

	/**
	 * 오퍼레이션명 : 대차상태설정 등록처리
	 * 염용선 2020-08-25
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtTcarStatSet(GridData gdReq) throws DAOException {
		String methodNm = "작업예약정보조회-스케줄기동[PSlabYdJspFaEJB.trtTcarStatSet]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			//JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trtTcarStatSet", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 * 오퍼레이션명 : 대차스케줄기준 등록
	 * 염용선 2020-08-25
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtTcarSchRule(GridData gdReq) throws DAOException {
		String methodNm = "대차스케줄기준 등록[PSlabYdJspFaEJB.trtTcarSchRule]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trtTcarSchRule", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 설비인출보급 순번 변경
	 * 염용선 2020-09-09
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStockSeqNo(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 순번 변경[PSlabYdJspFaEJB.updStockSeqNo]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updStockSeqNo", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 * 설비인출보급 보급편성기준변경
	 * 염용선 2020.09.09
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updEqpPulloutSupRule(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 보급편성기준변경[PSlabYdJspFaEJB.updEqpPulloutSupRule]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updEqpPulloutSupRule", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 더미이적 기준변경
	 * 염용선 2021.11.09
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData upDummySupRule(GridData gdReq) throws DAOException {
		String methodNm = "더미이적 기준변경[PSlabYdJspFaEJB.upDummySupRule]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("upDummySupRule", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			//GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 * 크레인 보급편성기준변경
	 * 염용선 2020.09.09
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCrnPulloutSupRule(GridData gdReq) throws DAOException {
		String methodNm = "크레인 보급편성기준변경[PSlabYdJspFaEJB.updCrnPulloutSupRule]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCrnPulloutSupRule", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 오퍼레이션명 : 설비인출보급 보급요구(Carry-In)
	 * 염용선 2020-08-25
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtEqpPulloutSupCI(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 보급요구(Carry-In)[PSlabYdJspFaEJB.trtEqpPulloutSupCI]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			//JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trtEqpPulloutSupCI", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 오퍼레이션명 : 설비인출보급 Take-In완료
	 * 염용선 2020-08-25
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtEqpPulloutSupTI(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 Take-In완료[PSlabYdJspFaEJB.trtEqpPulloutSupTI]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			//JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trtEqpPulloutSupTI", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 오퍼레이션명 : 후판압연지시메세지확인
	 * 염용선 2020-09-07
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updMillWoInqMsg(GridData gdReq) throws DAOException {
		String methodNm = "후판압연지시메세지확인[PSlabYdJspFaEJB.updMillWoInqMsg]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			//JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updMillWoInqMsg", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 오퍼레이션명 : 설비인출보급 Bed재료삭제
	 * 염용선 2020-09-08
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delEqpPulloutSupMtl(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 Bed재료삭제[PSlabYdJspFaEJB.delEqpPulloutSupMtl]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			//JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("delEqpPulloutSupMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 오퍼레이션명 : 설비인출보급 행선변경
	 * 염용선 2020-09-08
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updEqpPulloutSupRt(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 행선변경[PSlabYdJspFaEJB.updEqpPulloutSupRt]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			//JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updEqpPulloutSupRt", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 오퍼레이션명 : 설비인출보급 인출요구(Carry-Out)
	 * 염용선 2020-09-08
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtEqpPulloutSupCO(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 인출요구(Carry-Out)[PSlabYdJspFaEJB.trtEqpPulloutSupCO]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			//JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trtEqpPulloutSupCO", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 오퍼레이션명 : 설비인출보급 Take-Out완료
	 * 염용선 2020-09-08
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtEqpPulloutSupTO(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 Take-Out완료[PSlabYdJspFaEJB.trtEqpPulloutSupTO]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			//JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trtEqpPulloutSupTO", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 * 오퍼레이션명 : 작업예약등록(이적)
	 * 염용선 2020-09-08
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */	
	public GridData insMvWBookId(GridData gdReq) throws DAOException {
		String methodNm = "작업예약등록(이적)[PSlabYdJspFaEJB.insMvWBookId]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			//JDTORecord [] inRecord = ydComUtil.genGridToJDTORecordAll(gdReq);
			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("insMvWBookId", new Class[] { GridData.class }, new Object[] { gdReq});
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setStatus("true");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 오퍼레이션명 : 슬라브야드 메뉴얼 작업지시 편성
	 * 염용선 2020-09-17
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData ydManualReq(GridData gdReq) throws DAOException {
		String methodNm = "메뉴얼 작업지시 편성[PSlabYdJspFaEJB.ydManualReq]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			//JDTORecord [] inRecord = ydComUtil.genGridToJDTORecordAll(gdReq);
			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("ydManualReq", new Class[] { GridData.class }, new Object[] { gdReq});
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 오퍼레이션명 : 준비스케줄 LOT편성 ---> 이적 작업요구
	 * 염용선 2020-09-22
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvFtmvOrdLotReq(JDTORecord inRecord) throws JDTOException {
		String methodNm = "준비스케줄 LOT편성 ---> 이적 작업요구[PSlabYdJspFaEJB.rcvFtmvOrdLotReq]";
		String logId = slabUtils.getLogId();
		String szMsg="";
		String szMethodName="rcvFtmvOrdLotReq";
		

		if( !slabUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			slabUtils.printLog(szSessionName, szMethodName, "F-");
			return;
			
		}
	
 
		
		
        try {
        	//EJBConnector ydEjbCon = new EJBConnector("default", this);
            //이적 작업요구  - 구 버전 : MvStkWrkDmdSeEJB    
            //ydEjbCon.trx("PSlabYdJspSeEJB", "procFtmvOrdLotReq", inRecord);
            EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procFtmvOrdLotReq", new Class[] { JDTORecord.class }, new Object[] { inRecord});
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
        } catch (Exception e) {         
            szMsg =szMethodName + "() " +e.getMessage(); 
            slabUtils.printLog(szSessionName, szMsg, "F-");
            throw new JDTOException(szMsg);

        } 

		
		szMsg="이적 작업요구 처리("+szMethodName+") Session 호출 성공";
		slabUtils.printLog(szSessionName, szMsg, "F-");
	} 
	

	/**
	 * 공통저장품관리    1-1 		 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 공통저장품관리
	 * PYS : 2020-09-23
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData updPtSlabCommFix(GridData gdReq) throws JDTOException {
		String methodNm = "공통저장품관리[PSlabYdJspFaEJB.updPtSlabCommFix]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updPtSlabCommFix", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 * 공통저장품관리    2-1 		 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 공통저장품관리
	 * PYS : 2020-09-23
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData updPtMSlabCommFix(GridData gdReq) throws JDTOException {
		String methodNm = "공통저장품관리[PSlabYdJspFaEJB.updPtMSlabCommFix]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updPtMSlabCommFix", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 * 공통저장품관리    3-1 		 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 공통저장품관리
	 * PYS : 2020-09-23
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData updPtCoilComm(GridData gdReq) throws JDTOException {
		String methodNm = "공통저장품관리[PSlabYdJspFaEJB.updPtCoilComm]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updPtCoilComm", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 * 공통저장품관리    4-1 		 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 공통저장품관리
	 * PYS : 2020-09-23
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData updPtPlateComm(GridData gdReq) throws JDTOException {
		String methodNm = "공통저장품관리[PSlabYdJspFaEJB.updPtPlateComm]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updPtPlateComm", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 * 공통저장품관리    1-2 		 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 공통저장품관리
	 * PYS : 2020-09-23
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData updYdStock(GridData gdReq) throws JDTOException {
		String methodNm = "공통저장품관리[PSlabYdJspFaEJB.updYdStock]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updYdStock", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 공통저장품관리    3-2 		 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 공통저장품관리
	 * PYS : 2020-09-23
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData updStockCoilComm(GridData gdReq) throws JDTOException {
		String methodNm = "공통저장품관리[PSlabYdJspFaEJB.updStockCoilComm]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updStockCoilComm", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 * 공통저장품관리    4-2 		 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 공통저장품관리
	 * PYS : 2020-09-23
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData updStockPlateComm(GridData gdReq) throws JDTOException {
		String methodNm = "공통저장품관리[PSlabYdJspFaEJB.updStockPlateComm]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updStockPlateComm", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 차량도착    			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 출하차량상차Lot List   
	 * PYS : 2020-11-23
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData procDmCarArr(GridData gdReq) throws JDTOException {
		String methodNm = "차량도착[PSlabYdJspFaEJB.procDmCarArr]";
		String logId = slabUtils.getLogId();
		String sYdGp = "";
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			String sModifier     = gdReq.getParam("userid" ); 					//수정자(MODIFIER)
			JDTORecord jrRtn     = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
			JDTORecord jrParam   = slabUtils.getParam(logId, methodNm, sModifier);		//DAO Parameter - Log ID, Method, 수정자 Set

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			slabUtils.printLog(logId, gdReq.getHeader("CARD_NO")+ "" , "FL");
			sYdGp = gdReq.getParam("YD_GP");
			
			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);
			jrRtn = (JDTORecord)ejbConn.trx("procDmCarArr", new Class[] { GridData.class }, new Object[] { gdReq });

			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			if ("1".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 
	
	
	/**
	 * 오퍼레이션명 : 후판슬라브 이상재 보류해제등록
	 * 염용선 2020-09-17
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updateStlHoldstatPa(GridData gdReq) throws DAOException {
		String methodNm = "메뉴얼 작업지시 편성[PSlabYdJspFaEJB.updateStlHoldstatPa]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updateStlHoldstatPa", new Class[] { GridData.class }, new Object[] { gdReq});
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 * 이송LOT편성LIST   _삭제1 			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송LOT편성LIST   
	 * PYS : 2020-10-12
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData delYdPrepSch(GridData gdReq) throws JDTOException {
		String methodNm = "이송LOT편성LIST[PSlabYdJspFaEJB.delYdPrepSch]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);  
			JDTORecord   jrRtn   = (JDTORecord)ejbConn.trx("delYdPrepSch", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 
	

	/**
	 * 이송LOT편성LIST _수정   			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송LOT편성LIST   
	 * PYS : 2020-10-12
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData uptYdPrepSch(GridData gdReq) throws JDTOException {
		String methodNm = "이송LOT편성LIST[PSlabYdJspFaEJB.uptYdPrepSch]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);  
			JDTORecord   jrRtn   = (JDTORecord)ejbConn.trx("uptYdPrepSch", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 
	
	/**
	 * 이송LOT편성LIST   _삭제2 			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송LOT편성LIST   
	 * PYS : 2020-10-12
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData delYdPrepMtl(GridData gdReq) throws JDTOException {
		String methodNm = "이송LOT편성LIST[PSlabYdJspFaEJB.procYdPrepSch]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);  
			JDTORecord   jrRtn   = (JDTORecord)ejbConn.trx("delYdPrepMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 
	

	/**
	 * 이송재료 LIST   _수정  			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송재료List  
	 * PYS : 2020-10-15
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData updSlabTotYdTransMtlList(GridData gdReq) throws JDTOException {
		String methodNm = "이송재료 LIST[PSlabYdJspFaEJB.updSlabTotYdTransMtlList]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);	 
			JDTORecord   jrRtn   = (JDTORecord)ejbConn.trx("updSlabTotYdTransMtlList", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	


	/**
	 * 이송재료 LIST _ 이송LOT등록   _(자동)			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송재료List  
	 * PYS : 2020-10-15 (2020-12-03)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData insYdPrepSch(GridData gdReq) throws JDTOException {
		String methodNm = "이송재료 LIST[PSlabYdJspFaEJB.insYdPrepSch]";
		String logId = slabUtils.getLogId();
		
		try {
			slabUtils.printLog(logId, methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
					                           + "(" + gdReq.getParam("jsp_page_id") + ")", "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);	 
			JDTORecord   jrRtn   = (JDTORecord)ejbConn.trx("insYdPrepSch", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 * 이송재료 LIST _ 이송LOT등록   		_수동	 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송재료List  
	 * PYS : 2020-10-15
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData insYdPrepSchByManual(GridData gdReq) throws JDTOException {
		String methodNm = "이송재료 LIST[PSlabYdJspFaEJB.insYdPrepSchByManual]";
		String logId = slabUtils.getLogId();
		GridData      gdRes     = null;
		try {
			slabUtils.printLog(logId, methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
					                           + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")", "F+");
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);	 
			JDTORecord   jrRtn   = (JDTORecord)ejbConn.trx("insYdPrepSchByManual", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);

			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 이송재료 LIST _ 이송LOT등록   			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송재료List  
	 * PYS : 2020-10-15
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData SlabTotYdTransMtlList(GridData gdReq) throws JDTOException {
		String methodNm = "이송재료 LIST[PSlabYdJspFaEJB.SlabTotYdTransMtlList]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			slabUtils.printLog(logId, gdReq.getParam("CHECK"), "SL");
			
			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);	 
			JDTORecord   jrRtn   = (JDTORecord)ejbConn.trx("SlabTotYdTransMtlList", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 * 차량상차정보조회_수정    			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량상차정보조회  
	 * PYS : 2020-10-19
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData updpectionComplete(GridData gdReq) throws JDTOException {
		String methodNm = "차량상차정보조회_수정[PSlabYdJspFaEJB.updpectionComplete]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);	  
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updpectionComplete", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 * 검수완료처리 (차량상차정보조회)    			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량상차정보조회  
	 * PYS : 2020-10-19
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData inspectionComplete(GridData gdReq) throws JDTOException {
		String methodNm = "검수완료처리_차량상차정보조회_[PSlabYdJspFaEJB.inspectionComplete]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);	  
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("inspectionComplete", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * Point개폐(1-1) ( *1.차량Point작업현황, 2.배차내역, 3.차량작업상세내역 ) _차량작업관리   			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량상차정보조회  
	 * PYS : 2020-10-22
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData procSlabYdPntUnitCL(GridData gdReq) throws JDTOException {
		String methodNm = "차량작업관리_포인트개폐 처리[PSlabYdJspFaEJB.procSlabYdPntUnitCL]";
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("procSlabYdPntUnitCL", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			//
			if ("1".equals(rtnCd)) {					//JMS_TC_CD:YDTSJ012 + MSG_ID:YDY3L001
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 * 입동순서변경(1-2) ( *1.차량Point작업현황, 2.배차내역, 3.차량작업상세내역 )    			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량상차정보조회  
	 * PYS : 2020-10-22
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData procSlabYdBayInWoSeqChang(GridData gdReq) throws JDTOException {
		String methodNm = "차량작업관리_입동순서변경[PSlabYdJspFaEJB.procSlabYdBayInWoSeqChang]";
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this); 		
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("procSlabYdGdsBayInWoSeqChang", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 * 배차등록(2-1) ( 1.차량Point작업현황, *2.배차내역, 3.차량작업상세내역 )    			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량상차정보조회  
	 * PYS : 2020-10-22
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData uptCarSch(GridData gdReq) throws JDTOException {
		String methodNm = "차량작업관리_배차등록[PSlabYdJspFaEJB.uptCarSch]";
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("uptCarSch", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 * 하차완료(2-2) ( 1.차량Point작업현황, *2.배차내역, 3.차량작업상세내역 )    			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량상차정보조회  
	 * PYS : 2020-10-22
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData complCarUd(GridData gdReq) throws JDTOException {
		String methodNm = "차량작업관리_[PSlabYdJspFaEJB.complCarUd]";
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("complCarUd", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	


	/**
	 * 배차등록(2-3) ( 1.차량Point작업현황, *2.배차내역, 3.차량작업상세내역 )    			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량상차정보조회  
	 * PYS : 2020-12-24
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData canCarSch(GridData gdReq) throws JDTOException {
		String methodNm = "차량작업취소_[PSlabYdJspFaEJB.canCarSch]";
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("canCarSch", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			// ROLLBACK 시 전문 발생
			if ("1".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 * 예약재처리(2-5) ( 1.차량Point작업현황, *2.배차내역, 3.차량작업상세내역 )    			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량작업관리  
	 * PYS : 2021.03.02
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData uptReWbook(GridData gdReq) throws JDTOException {
		String methodNm = "예약재처리(차량작업관리)_[PSlabYdJspFaEJB.uptReWbook]";
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) ;
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("uptReWbook", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			// ROLLBACK 시 전문 발생
			if ("1".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	


	/**
	 * 상차Lot편성(3-1) ( 1.차량Point작업현황, 2.배차내역, *3.차량작업상세내역 )   
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량상차정보조회  
	 * PYS : 2020-12-22
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData insCarLdLot(GridData inDto) throws JDTOException {
		String methodNm = "차량작업관리_[PSlabYdJspFaEJB.insCarLdLot]";
		String logId = slabUtils.getLogId();
		String szYdCarLotType = "";
		JDTORecord [] inRecord =  null;
		String szMsg = "";
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(inDto.getParam("jsp_page_nm")) + "(" + slabUtils.trim(inDto.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			inDto.setNavigateValue(methodNm); // 상위 Method 명
			inDto.setIPAddress(logId);        // Logging 을 위한 ID

			szYdCarLotType = inDto.getParam("YD_CAR_LOT_TYPE");
			inDto.addParam("logId", logId);
			if( szYdCarLotType.equals("M") ) {  
				inRecord =  slabUtils.genGridToJDTORecordAll(inDto);
				szMsg = "차량작업관리 화면 상차LOT편성 - 작업자 지정";
				slabUtils.printLog(logId, szMsg , "SL");
			}
			/*else{
				inRecord = new JDTORecord[1];
				inRecord[0] = slabUtils.genParamToJDTORecord(inDto);
				szMsg = "차량작업관리 화면 상차LOT편성   - 기준 적용";
				slabUtils.printLog(logId, szMsg , "SL");
			}*/

			
			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("insCarLdLot", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
	 		
			GridData gdRet = OperateGridData.cloneResponseGridData(inDto);
			
			if ("1".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			} else {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 * 상차Lot취소(3-2) ( 1.차량Point작업현황, 2.배차내역, *3.차량작업상세내역 )    			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량상차정보조회  
	 * PYS : 2020-10-22
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData cancelCarLdLot(GridData gdReq) throws JDTOException {
		String methodNm = "차량작업관리_[PSlabYdJspFaEJB.cancelCarLdLot]";
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("cancelCarLdLot", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 * 상차완료처리(3-3) ( 1.차량Point작업현황, 2.배차내역, *3.차량작업상세내역 )    			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량상차정보조회  
	 * PYS : 2020-10-22
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData complCarLdLot(GridData gdReq) throws JDTOException {
		String methodNm = "차량작업관리_[PSlabYdJspFaEJB.complCarLdLot]";
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("complCarLdLot", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			slabUtils.printParam(logId, jrRtn);
			
			// ROLLBACK 시 전문 발생
			if ("1".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 * 차량위치수정(4-1)    <차량상차정보조회 			 	 										 -4-4.상차완료처리SlabJspFaEJB:complCarLdLot
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량작업관리  
	 * PYS : 2020-11-01
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData carLiftPosSet(GridData gdReq) throws JDTOException {
		String methodNm = "차량위치수정(POP)_[PSlabYdJspFaEJB.carLiftPosSet]";	
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID		(차량위치수정, 재료등록,삭제  )

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);	
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("carLiftPosSet", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			if ("1".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 * 재료등록(4-2)    <차량상차정보조회 			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량작업관리  
	 * PYS : 2020-11-01
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData carLiftStlInsert(GridData gdReq) throws JDTOException {
		String methodNm = "재료등록(POP)_[PSlabYdJspFaEJB.carLiftStlInsert]";	
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID		(차량위치수정, *재료등록,삭제,상차완료처리 )

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);	
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("carLiftStlInsert", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			if ("1".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 * 재료삭제(4-3)    <차량상차정보조회 			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량작업관리  
	 * PYS : 2020-11-01
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData carLiftStlDelete(GridData gdReq) throws JDTOException {
		String methodNm = "재료삭제(POP)_[PSlabYdJspFaEJB.carLiftStlDelete]";	
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID		(차량위치수정, 재료등록,삭제 상차완료처리 )

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);			
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("carLiftStlDelete", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			if ("1".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 건별작업에약 생성 처리(4-4)    <x차작업재료관리 (팝업) 			 	 										
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량작업관리(팝업)  
	 * PYS : 2021-04-30
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  gdReq
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData makeReWbook(GridData gdReq) throws JDTOException {
		String methodNm = "건별하차작업_[PSlabYdJspFaEJB.makeReWbook]";	
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID		(차량위치수정, 재료등록,삭제  )

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);	
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("makeReWbook", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			if ("1".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 이송대상재 수정(5-1)    <이송대상재 조회 			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량작업관리  
	 * PYS : 2020-11-01
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData updFrtoMoveMtlToStock(GridData gdReq) throws JDTOException {
		String methodNm = "이송대상재 조회(POP)_[PSlabYdJspFaEJB.updFrtoMoveMtlToStock]";
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);		
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("updFrtoMoveMtlToStock", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 * 차량작업관리 작업재료 조회   (TAB3-조회)
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량작업관리  
	 * PYS : 2020-11-17
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getYdGdsCarWork(GridData gdReq) throws DAOException {
		String mthdNm = "조회[PSlabYdJspFaEJB.getYdGdsCarWork]";
		String logId  = slabUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
			                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id"));
			slabUtils.printLog(logId, mthdNm, "F+");

			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getYdGdsCarWork", new Class[] { GridData.class }, new Object[] { gdReq });

			//GridData gdRet = OperateGridData.cloneResponseGridData(gdRes);
			
			slabUtils.printLog(logId, mthdNm, "F-");
			gdRet.setMessage("SUCCESS");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, mthdNm, e));
		}
	}		
	
	/**
	 * 후판창고 차량정지상태 등록 수정 
	 * 염용선  2020-10-09
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPlateYdCarStopLocStsReg(GridData gdReq) throws DAOException {
		String methodNm = "차량정지상태 등록 수정[PSlabYdJspFaEJB.updPlateYdCarStopLocStsReg]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updPlateYdCarStopLocStsReg", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 오퍼레이션명 : Y3크레인비상조업실적 (Y3YDL010)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws JDTOException
	 */ 
	public void rcvY3CrnEmgPtopWr(JDTORecord inRecord) throws JDTOException  {
	// 
	// YD-UC-5206 Y3크레인비상조업실적
	// TC : Y3YDL010
	// A후판슬라브야드L2시스템으로부터 크레인비상조업실적 수신
	//
	//┏━┓
	//┃ A후판슬라브야드 L2에서 비상조업한 결과를 수신하여 야드의 저장위치를 정리
	//┗━┛
		String methodNm = "Y3크레인비상조업실적[PSlabYdJspFaEJB.rcvY3CrnEmgPtopWr]";
		String logId = slabUtils.getLogId();
		String szMsg="";
		String szMethodName="rcvY3CrnEmgPtopWr";
		slabUtils.printLog(logId, methodNm, "F+");
		if( !slabUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			slabUtils.printLog(logId, szMsg, "FL");
			return;
			
		}
		
		
		try {
			
			EJBConnector sndConn = new EJBConnector("default", "CraneUdHdSeEJB", this);
			sndConn.trx("procY3CrnEmgPtopWr", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			
			// 비상조업실적등록 요청 
			//ydEjbCon.trx("CraneUdHdSeEJB", "procY3CrnEmgPtopWr", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			slabUtils.printLog(logId, szMsg, "FL");
			throw new JDTOException(szMsg);

		} // end of try catch
		
		
		szMsg="Y3크레인비상조업실적 처리("+szMethodName+") 완료";
		slabUtils.printLog(logId, szMsg, "F-");
		slabUtils.printLog(logId, methodNm, "F-");
		
	 } 
	
	
	/**
	 * IFTest 전송 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData sndIfTest(GridData gdReq) throws DAOException {
		String methodNm =  "IFTest 전송[PSlabYdJspFaEJB.sndIfTest]";
		String logId = slabUtils.getLogId(); //(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			//IFTest 전송
			ejbConn.trx("sndIfTest", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}		
	
	

	/**
	 * IFTest 전송   (추가)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData sndIfTest6(GridData gdReq) throws DAOException {
		String methodNm =  "IFTest 전송[PSlabYdJspFaEJB.sndIfTest]";
		String logId = slabUtils.getLogId(); //(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			
			//IFTest 전송
			ejbConn.trx("sndIfTest6", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	 
	
	/**
	 * IFTest EAI전송
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData sndIfTestEAI(GridData gdReq) throws DAOException {
		String methodNm =  "IFTest EAI전송[PSlabYdJspFaEJB.sndIfTestEAI]";
		String logId = slabUtils.getLogId();//(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this); //BSlabJspSeEJB
			
			//IFTest EAI전송
			GridData gdRet = (GridData) ejbConn.trx("sndIfTestEAI", new Class[] { GridData.class }, new Object[] { gdReq });
			
			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
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
	public GridData updIfTestData(GridData gdReq) throws DAOException {
		String methodNm =  "IFTest Layout 변경[BSlabJspFaEJB.updIfTestData]";
		String logId = slabUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			//IFTest Layout 변경
			ejbConn.trx("updIfTestData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	
	

	/**
	 * IFTest Layout 변경 				-추가-
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updIfTestData6(GridData gdReq) throws DAOException {
		String methodNm =  "IFTest Layout 변경[pSlabYdJspFaEJB.updIfTestData6]";
		String logId = slabUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			
			//IFTest Layout 변경
			ejbConn.trx("updIfTestData6", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updIfTestData
	
	
	/**
	 * 오퍼레이션명 : 후판 슬라브 이상재 등록/해제 -공정관리 호출 (YDYDJ298)> YDYDJ429 로 전문코드 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvAbmtlOccurSend(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 후판 슬라브 이상재 등록/해제 -공정관리 호출
		// TC : YDYDJ298 > YDYDJ429 로 전문코드 변경
		//  
		//PSlabYdCommEJB  rcvInterface
		//┏━┓
		//┃ 전문 호출하지 않고 직접 처리해도 무방한 프로세스임
		//┗━┛
		String methodNm = "후판 슬라브 이상재 등록/해제[PSlabYdJspFaEJB.rcvAbmtlOccurSend]";
		String logId = slabUtils.getLogId();
		String szMsg="";
		
        try {
        	inRecord.setField("LOG_ID"       , logId); 
        	inRecord.setField("METHOD_NM"    , methodNm  ); 
        	
        	//ydEjbCon.trx("PSlabYdJspSeEJB", "rcvAbmtlOccurSend", inRecord);
        	EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			ejbConn.trx("rcvAbmtlOccurSend", new Class[] { JDTORecord.class }, new Object[] { inRecord });
        } catch (Exception e) {         
            szMsg =methodNm + "() " +e.getMessage();             
            throw new JDTOException(szMsg);
        } 
        
        
        
	} 
	


	/**
	 * 상하차백업처리(2-7) ( 1.차량Point작업현황, *2.배차내역, 3.차량작업상세내역 )    			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량상차정보조회  
	 * PYS : 2021.01.21
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData mkUdCarSch(GridData gdReq) throws JDTOException {
		String methodNm = "차량작업취소_[PSlabYdJspFaEJB.mkUdCarSch]";
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("mkUdCarSch", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	


	/**
	 * 구내배송] 이송차량 실적처리 팝업 - 등록	
	 * 야드관리 > 후판슬라브야드 [신] > 인터페이스 > 구내배송 백업			 
	 * PYS : 2021.01.23
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData trtMvCarStatSet2(GridData gdReq) throws JDTOException {
		String methodNm = "이송차량 실적처리 팝업[PSlabYdJspFaEJB.trtMvCarStatSet2]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID
 
			EJBConnector ejbConn    = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trtMvCarStatSet2", new Class[] { GridData.class     }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			// ROLLBACK 시 전문 발생
			if ("1".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 		
	

	/**
	 * 이송작업재료등록
	 * 야드관리 > 후판슬라브야드 [신] > 인터페이스 > 구내배송 백업			 
	 * PYS : 2021.01.23
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws JDTOException

	 */
	public GridData updCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm =  "이송작업재료등록[PSlabYdJspFaEJB.updCarFtMvMtl]";
		String logId = slabUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCarFtMvMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");

			if ("1".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}

//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			
			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCarFtMvMtl


	/**
	 * 이송재료 삭제 :    			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송차량 백업 처리 
	 * PYS : 2021.01.29
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData delCarFtMvMtl(GridData gdReq) throws JDTOException {
		String methodNm = "차량작업취소_[PSlabYdJspFaEJB.delCarFtMvMtl]";
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("delCarFtMvMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			
			slabUtils.printLog(logId, ")>" , "SL"); 
			slabUtils.printLog(logId, ">>"+ slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0") , "SL"); 
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			

			if ("1".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 이송재료 이동처리     			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송차량 백업 처리 
	 * PYS : 2021.01.29
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData chgCarFtMvMtl(GridData gdReq) throws JDTOException {
		String methodNm = "차량작업취소_[PSlabYdJspFaEJB.chgCarFtMvMtl]";
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("chgCarFtMvMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 * 차량 회송 처리    	(2-6)		 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 차량작업관리:배차내역  
	 * PYS : 2021.01.29
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData runTsRetHt(GridData gdReq) throws JDTOException {
		String methodNm = "차량회송처리_[PSlabYdJspFaEJB.runTsRetHt]";
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("runTsRetHt", new Class[] { GridData.class }, new Object[] { gdReq });
			
			slabUtils.printLog(logId, ")>" , "SL"); 
			slabUtils.printLog(logId, ">>"+ slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0") , "SL"); 
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			

			if ("1".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	


	/**
	 * 이송LOT편성LIST-동일상차처리
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송LOT편성LIST   
	 * PYS : 2021.03.31
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData uptSameYdPrepSch(GridData gdReq) throws JDTOException {
		String methodNm = "동일(차량)상차(배정)처리[PSlabYdJspFaEJB.uptSameYdPrepSch]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);  
			JDTORecord   jrRtn   = (JDTORecord)ejbConn.trx("uptSameYdPrepSch", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " ======*rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " ======*rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 이송LOT편성LIST-동일상차취소
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송LOT편성LIST   
	 * PYS : 2021.03.31
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData uptCnclYdPrepSch(GridData gdReq) throws JDTOException {
		String methodNm = "동일(차량)상차(배정)처리[PSlabYdJspFaEJB.uptSameYdPrepSch]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);  
			JDTORecord   jrRtn   = (JDTORecord)ejbConn.trx("uptCnclYdPrepSch", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " ======*rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " ======*rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 수불구용도변경 등록
	 *      YYS 2021-04-06
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public GridData trtBedUsgGpSet(GridData gdReq) throws JDTOException {
		String methodNm = "수불구용도변경 등록[PSlabYdJspFaEJB.trtBedUsgGpSet]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);  
			JDTORecord   jrRtn   = (JDTORecord)ejbConn.trx("trtBedUsgGpSet", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " ======*rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " ======*rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} 
	

	/**
	 * 차량스케줄삭제   			 	 
	 * 야드관리 > 후판슬라브야드 [신] > Monitoring > 이송차량 백업 처리 
	 * PYS : 2021.05.11
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData initBookCarSch(GridData gdReq) throws JDTOException {
		String methodNm = "차량스케줄삭제_[PSlabYdJspFaEJB.initBookCarSch]";
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("initBookCarSch", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 재열재 정보 수정 및 이적(rcvPPYDJ016)   			 	 
	 * 야드관리 > 후판슬라브야드 [신] > 인터페이스 > Interface Test 
	 * 김현규 : 2021.06.04
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
//	public GridData rcvPPYDJ016(GridData gdReq) throws JDTOException {
//		String methodNm = "재열재 정보 수정 및 이적[PSlabYdJspFaEJB.rcvPPYDJ016]";
//		String logId = slabUtils.getLogId();
// 
//		try {
//			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
//			slabUtils.printLog(logId, methodNm, "F+");
//
//			gdReq.setNavigateValue(methodNm); // 상위 Method 명
//			gdReq.setIPAddress(logId);        // Logging 을 위한 ID
//			
//			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdJspSeEJB", this);
//			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("rcvPPYDJ016", new Class[] { GridData.class }, new Object[] { gdReq });
//
//			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
//			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
//			
//			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
//			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
//			
//			if ("1".equals(rtnCd)) {
//				jrRtn.setResultCode(logId);
//				jrRtn.setResultMsg(methodNm);
//				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
//				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
//			}
//			
//			//화면 메시지
//			if (!"1".equals(rtnCd)) {
//				m_ctx.setRollbackOnly();
//			}
//			
//			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
//			slabUtils.printLog(logId, methodNm, "F-");
//			gdRet.setMessage(rtnMsg);
//			return gdRet;		
//		
//		} catch(DAOException e) {
//			throw e;
//		} catch(Exception e) {
//			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
//		}
//	}
	
	
	

	/**
	 * 차량작업관리 상차LOT편성 - 운송장비(차량)에 이송LOT로 작업예약을 생성한다.
	 * 민종근 : 2021-07-27
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData insCarLdLotNew(GridData inDto) throws JDTOException {
		String methodNm = "후판슬라브야드  상차LOT편성[PSlabYdJspFaEJB.insCarLdLotNew]";
		String logId = slabUtils.getLogId();
		
		JDTORecord		paramRecord	=  null;	// 와이즈그리드에 SetParam으로 담긴 값 저장용
		JDTORecord []	listRecord	=  null;	// 와이즈그리드의 row 저장용(선택된 row만 넘어온다.)
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(inDto.getParam("jsp_page_nm")) + "(" + slabUtils.trim(inDto.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			inDto.setNavigateValue(methodNm); // 상위 Method 명
			inDto.setIPAddress(logId);        // Logging 을 위한 ID
			inDto.addParam("logId", logId);
			
			paramRecord	=  slabUtils.genParamToJDTORecord(inDto);	// 파라미터 저장
			listRecord	=  slabUtils.genGridToJDTORecord(inDto);	// 그리드 row 저장
			
			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdMvCarSeEJB", this);
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("insCarLdLotNew", new Class[] { JDTORecord.class, JDTORecord[].class }, new Object[] { paramRecord, listRecord });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
	 		
			GridData gdRet = OperateGridData.cloneResponseGridData(inDto);
			
			if ("1".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			} else {
				m_ctx.setRollbackOnly();
			}
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	

	/**  ----------------------------------------------------------------도움말----  
	 * 화면 도움말 등록			 	 
	 * 야드관리 > 후판슬라브야드 [신] > 공통 > 도움말 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData setPageHelpInfo(GridData gdReq) throws JDTOException {
		String methodNm = "화면 도움말 등록[PSlabYdJspFaEJB.setPageHelpInfo]";
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("setPageHelpInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd+ " =======rtnMsg:"+ rtnMsg, "FL");
			
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**    
	 * 화면 도움말 - 버튼 등록			 	 
	 * 야드관리 > 후판슬라브야드 [신] > 공통 > 도움말 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData setPageHelpBtnInfo(GridData gdReq) throws JDTOException {
		String methodNm = "화면 도움말 -버튼등록[PSlabYdJspFaEJB.setPageHelpBtnInfo]";
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("setPageHelpBtnInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd+ " =======rtnMsg:"+ rtnMsg, "FL");
			
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**    
	 * 화면 도움말 - 작업방법(버튼상세) 등록			 	 
	 * 야드관리 > 후판슬라브야드 [신] > 공통 > 도움말 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData setPageHelpBtnDtlInfo(GridData gdReq) throws JDTOException {
		String methodNm = "화면 도움말 -작업방법(버튼상세)등록[PSlabYdJspFaEJB.setPageHelpBtnDtlInfo]";
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("setPageHelpBtnDtlInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd+ " =======rtnMsg:"+ rtnMsg, "FL");
			
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	/**    
	 * 화면 도움말 - 첨부문서 등록	 	 
	 * 야드관리 > 후판슬라브야드 [신] > 공통 > 도움말 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws 
	 * @throws JDTOException
	 */	
	public JDTORecord getPageHelpDocMaxDocSeq(JDTORecord inRecord) throws JDTOException {
		String methodNm = "화면 도움말 -신규 문서번호 채번[PSlabYdJspFaEJB.getPageHelpDocMaxDocSeq]";
		String logId = slabUtils.getLogId();
		JDTORecord outRecord = null;
		try {
			slabUtils.printLog(logId, methodNm, "F+");

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("getPageHelpDocMaxDocSeq", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			
			return outRecord;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	/**    
	 * 화면 도움말 - 첨부문서 등록	 	 
	 * 야드관리 > 후판슬라브야드 [신] > 공통 > 도움말 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws 
	 * @throws JDTOException
	 */	
	public JDTORecord setPageHelpDoc(JDTORecord inRecord) throws JDTOException {
		String methodNm = "화면 도움말 -신규 문서번호 채번[PSlabYdJspFaEJB.setPageHelpDoc]";
		String logId = slabUtils.getLogId();
		JDTORecord outRecord = null;
		try {
			slabUtils.printLog(logId, methodNm, "F+");

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("setPageHelpDoc", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			
			return outRecord;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**    
	 * 화면 도움말 - 첨부문서 삭제	 	 
	 * 야드관리 > 후판슬라브야드 [신] > 공통 > 도움말 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws 
	 * @throws JDTOException
	 */	
	public JDTORecord delPageHelpDoc(JDTORecord inRecord) throws JDTOException {
		String methodNm = "화면 도움말 -첨부문서 삭제[PSlabYdJspFaEJB.delPageHelpDoc]";
		String logId = slabUtils.getLogId();
		JDTORecord outRecord = null;
		try {
			slabUtils.printLog(logId, methodNm, "F+");

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("delPageHelpDoc", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			
			return outRecord;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 녹슨재 지정
	 * 후판슬라브야드 > Monitoring> 저장위치별정보조회
	 * 민종근 2021.08.17
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updateRustStl(GridData gdReq) throws DAOException {
		String methodNm = "후판슬라브야드 녹슨재 지정[PSlabYdJspFaEJB.updateRustStl]";
		String logId = slabUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");
			
			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updateRustStl", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			if ("1".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			} else {
				m_ctx.setRollbackOnly();
			}
			
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 휴지실적관리 휴지분류,내용입력    			 	 
	 * 야드관리 > 후판슬라브야드 [신] > 작업실적관리 > 휴지실적관리(2차절단)
	 * HJW : 2022.05.12
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return GridData
	 * @throws 
	 * @throws JDTOException
	 */	
	public GridData updateEqpPauseHist(GridData gdReq) throws JDTOException {
		String methodNm = "[휴지실적관리 휴지분류,내용입력 PSlabYdJspFaEJB.updateEqpPauseHist]";
		String logId = slabUtils.getLogId();
 
		try {
			methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, methodNm, "F+");

			gdReq.setNavigateValue(methodNm); // 상위 Method 명
			gdReq.setIPAddress(logId);        // Logging 을 위한 ID

			EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdJspSeEJB", this);
			JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("updateEqpPauseHist", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd,  "FL");
			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
			
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			slabUtils.printLog(logId, methodNm, "F-");
			gdRet.setMessage(rtnMsg);
			return gdRet;		
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**    
     * SLAB정보 수신처리 - 버튼 등록
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  GridData gdReq
     * @return GridData
     * @throws DAOException
     */ 
    public GridData setInterfaceSLABInfoTreatQuality(GridData gdReq) throws DAOException {
        String methodNm = "SLAB정보 수신처리 - 버튼등록[PSlabYdJspFaEJB.setInterfaceSLABInfoTreatQuality]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
            slabUtils.printLog(logId, methodNm, "F+");

            gdReq.setNavigateValue(methodNm); // 상위 Method 명
            gdReq.setIPAddress(logId);        // Logging 을 위한 ID

            EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("setInterfaceSLABInfoTreatQuality", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnCd  = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
            String rtnMsg = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
            slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd+ " =======rtnMsg:"+ rtnMsg, "FL");
            
            if (!"1".equals(rtnCd)) {
                m_ctx.setRollbackOnly();
            }
            
            GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
            slabUtils.printLog(logId, methodNm, "F-");
            gdRet.setMessage(rtnMsg);
            return gdRet;       
        
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }

    /**    
     * SLAB정보 수신처리 - 버튼 삭제
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  GridData gdReq
     * @return GridData
     * @throws DAOException
     */ 
    public GridData delInterfaceSLABInfoTreatQuality(GridData gdReq) throws DAOException {
        String methodNm = "SLAB정보 수신처리 - 버튼삭제[PSlabYdJspFaEJB.setInterfaceSLABInfoTreatQuality]";
        String logId = slabUtils.getLogId();
 
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
            slabUtils.printLog(logId, methodNm, "F+");

            gdReq.setNavigateValue(methodNm); // 상위 Method 명
            gdReq.setIPAddress(logId);        // Logging 을 위한 ID

            EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("delInterfaceSLABInfoTreatQuality", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnCd  = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
            String rtnMsg = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
            slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd+ " =======rtnMsg:"+ rtnMsg, "FL");
            
            if (!"1".equals(rtnCd)) {
                m_ctx.setRollbackOnly();
            }
            
            GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
            slabUtils.printLog(logId, methodNm, "F-");
            gdRet.setMessage(rtnMsg);
            return gdRet;
        
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }
    
    /**
     * SLAB정보 수신처리 - interface 신규처리 가능성 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getInterfaceCheck(GridData gdReq) throws DAOException {
        String methodNm = "SLAB정보 수신처리 - interface 신규처리 가능성 조회[PSlabYdJspFaEJB.getInterfaceCheck]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            JDTORecord   jrRtn   = (JDTORecord)ejbConn.trx("getInterfaceCheck", new Class[] { GridData.class }, new Object[] { gdReq });

            String rtnCd  = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
            String rtnMsg = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
            slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd+ " =======rtnMsg:"+ rtnMsg, "FL");
            
            GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
            slabUtils.printLog(logId, methodNm, "F-");
            gdRet.setMessage(rtnMsg);
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }       
    
    /**
     * SLAB정보 수신처리 - Interface New
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getInterfaceNew(GridData gdReq) throws DAOException {
        String methodNm = "SLAB정보 수신처리 - Interface New[PSlabYdJspFaEJB.getInterfaceNew]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getInterfaceNew", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }       
    
    /**
     * SLAB정보 수신처리 - Interface Add
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getInterfaceAdd(GridData gdReq) throws DAOException {
        String methodNm = "SLAB정보 수신처리 - Interface Add[PSlabYdJspFaEJB.getInterfaceAdd]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getInterfaceAdd", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }

    /**
     * SLAB정보 수신처리 - Interface SLAB Check
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getInterfaceSLABCheck(GridData gdReq) throws DAOException {
        String methodNm = "SLAB정보 수신처리 - Interface SLAB Check[PSlabYdJspFaEJB.getInterfaceSLABCheck]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getInterfaceSLABCheck", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }

    /**
     * B/L번호 정보조회 - 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getBLNOSearch(GridData gdReq) throws DAOException {
        String methodNm = "B/L번호 정보조회 - 조회[PSlabYdJspFaEJB.getBLNOSearch]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getBLNOSearch", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }

    /**
     * B/L번호 정보조회 - 총중량
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getBLNOTotal(GridData gdReq) throws DAOException {
        String methodNm = "B/L번호 정보조회 - 총중량[PSlabYdJspFaEJB.getBLNOTotal]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getBLNOTotal", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }

    /**
     * 구입슬라브 입고등록 - 조회1
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListSlabStackReg(GridData gdReq) throws DAOException {
        String methodNm = "구입슬라브 입고등록 - 조회1[PSlabYdJspFaEJB.getListSlabStackReg]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListSlabStackReg", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }

    /**
     * 구입슬라브 입고등록 - 조회2
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListSlabStackReg2(GridData gdReq) throws DAOException {
        String methodNm = "구입슬라브 입고등록 - 조회2[PSlabYdJspFaEJB.getListSlabStackReg2]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListSlabStackReg2", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }

    /**
     * 구입슬라브 입고등록 - SLAB 정보 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListSlabStackReg4(GridData gdReq) throws DAOException {
        String methodNm = "구입슬라브 입고등록 - SLAB 정보 조회[PSlabYdJspFaEJB.getListSlabStackReg4]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListSlabStackReg4", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }

    /**
     * 구입슬라브 입고등록 - 기존의 슬라브원장에 정보가 있는지 체크
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListStackCheck(GridData gdReq) throws DAOException {
        String methodNm = "구입슬라브 입고등록 - 기존의 슬라브원장에 정보가 있는지 체크[PSlabYdJspFaEJB.getListStackCheck]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListStackCheck", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }

    /**
     * 구입슬라브 입고등록 - 입고수신이 되어있는지 확인
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListSlabStackReg5(GridData gdReq) throws DAOException {
        String methodNm = "구입슬라브 입고등록 - 입고수신이 되어있는지 확인[PSlabYdJspFaEJB.getListSlabStackReg5]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListSlabStackReg5", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }

    /**
     * 구입슬라브 입고등록 - 등록할 슬라브 정보 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListSlabStackReg3(GridData gdReq) throws DAOException {
        String methodNm = "구입슬라브 입고등록 - 등록할 슬라브 정보 조회[PSlabYdJspFaEJB.getListSlabStackReg3]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListSlabStackReg3", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }

    /**
     * 구입슬라브 입고등록 - 취소작업 존재유무 확인
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListCancleCheckSlabStackReg(GridData gdReq) throws DAOException {
        String methodNm = "구입슬라브 입고등록 - 취소작업 존재유무 확인[PSlabYdJspFaEJB.getListCancleCheckSlabStackReg]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListCancleCheckSlabStackReg", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }

    /**    
     * 구입슬라브 입고등록 - 저장(완료)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  GridData gdReq
     * @return GridData
     * @throws DAOException
     */ 
    public GridData insertNUpdateSlabStackReg(GridData gdReq) throws DAOException {
        String methodNm = "구입슬라브 입고등록 - 저장(완료)[PSlabYdJspFaEJB.insertNUpdateSlabStackReg]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
            slabUtils.printLog(logId, methodNm, "F+");

            gdReq.setNavigateValue(methodNm); // 상위 Method 명
            gdReq.setIPAddress(logId);        // Logging 을 위한 ID

            EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("insertNUpdateSlabStackReg", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnCd  = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
            String rtnMsg = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
            slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd+ " =======rtnMsg:"+ rtnMsg, "FL");
            
            if (!"1".equals(rtnCd)) {
                m_ctx.setRollbackOnly();
                
                if(rtnMsg == null || rtnMsg.equals("")) {
                    rtnMsg = "1"+MessageHelper.getUserMessage("MSG0040", new String[] {"적치등록"}, "적치등록이 실패하였습니다.");
                }
            }
            
            GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
            slabUtils.printLog(logId, methodNm, "F-");
            gdRet.setMessage(rtnMsg);
            return gdRet;       
        
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }

    /**
     * 구입슬라브 입고등록 - 현재 진도코드, 입고일자, 입고시간 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListSendStackInfo(GridData gdReq) throws DAOException {
        String methodNm = "구입슬라브 입고등록 - 현재 진도코드, 입고일자, 입고시간 조회[PSlabYdJspFaEJB.getListSendStackInfo]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListSendStackInfo", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }

    /**    
     * 구입슬라브 입고등록 - 취소(삭제)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  GridData gdReq
     * @return GridData
     * @throws DAOException
     */ 
    public GridData deleteSlabStackReg(GridData gdReq) throws DAOException {
        String methodNm = "구입슬라브 입고등록 - 취소(삭제)[PSlabYdJspFaEJB.deleteSlabStackReg]";
        String logId = slabUtils.getLogId();
 
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
            slabUtils.printLog(logId, methodNm, "F+");

            gdReq.setNavigateValue(methodNm); // 상위 Method 명
            gdReq.setIPAddress(logId);        // Logging 을 위한 ID

            EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("deleteSlabStackReg", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnCd  = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
            String rtnMsg = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
            slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd+ " =======rtnMsg:"+ rtnMsg, "FL");
            
            if (!"1".equals(rtnCd)) {
                m_ctx.setRollbackOnly();

                if(rtnMsg == null || rtnMsg.equals("")) {
                    rtnMsg = "1"+MessageHelper.getUserMessage("MSG0040", new String[] {"취소"}, "취소작업이 실패하였습니다.");
                }
            }
            
            GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
            slabUtils.printLog(logId, methodNm, "F-");
            gdRet.setMessage(rtnMsg);
            return gdRet;
        
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }

    /**
     * 적치리스트 조회 - 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListStackList(GridData gdReq) throws DAOException {
        String methodNm = "적치리스트 조회 - 조회[PSlabYdJspFaEJB.getListStackList]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListStackList", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }

    /**
     * 적치리스트 조회 - 총중량, 취소중량, 적치중량
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListSumWt(GridData gdReq) throws DAOException {
        String methodNm = "적치리스트 조회 - 총중량, 취소중량, 적치중량[PSlabYdJspFaEJB.getListSumWt]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListSumWt", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }

    /**    
     * 수출슬라브 출고처리 - 출고등록
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  GridData gdReq
     * @return GridData
     * @throws DAOException
     */ 
    public GridData setMSSlabIssueReg(GridData gdReq) throws DAOException {
        String methodNm = "수출슬라브 출고처리 - 출고등록[PSlabYdJspFaEJB.setMSSlabIssueReg]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
            slabUtils.printLog(logId, methodNm, "F+");

            gdReq.setNavigateValue(methodNm); // 상위 Method 명
            gdReq.setIPAddress(logId);        // Logging 을 위한 ID

            EJBConnector ejbConn  = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            JDTORecord   jrRtn    = (JDTORecord)ejbConn.trx("setMSSlabIssueReg", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnCd  = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
            String rtnMsg = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
            slabUtils.printLog(logId, methodNm + " =======rtnCd :"+ rtnCd+ " =======rtnMsg:"+ rtnMsg, "FL");
            
            if (!"1".equals(rtnCd)) {
                m_ctx.setRollbackOnly();
                
                if(rtnMsg.equals("MSG0034")) {
                    rtnMsg = "1"+MessageHelper.getUserMessage("MSG0040", new String[] {"출고등록"}, "출고등록이 실패하였습니다.");
                }
            }
            
            GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
            slabUtils.printLog(logId, methodNm, "F-");
            gdRet.setMessage(rtnMsg);
            return gdRet;       
        
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }

    /**
     * 출고LIST - 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListIssueList(GridData gdReq) throws DAOException {
        String methodNm = "출고LIST - 조회[PSlabYdJspFaEJB.getListIssueList]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListIssueList", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }
    
    /**
     * B/L정보조회 - 규격 팝업 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListSpecSearchPop(GridData gdReq) throws DAOException {
        String methodNm = "B/L정보조회 - 규격 팝업 정보 조회[PSlabYdJspFaEJB.getListSpecSearchPop]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListSpecSearchPop", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }
    
    /**
     * SLAB정보 수신처리 - 모선관리번호 팝업 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListvesselMgrNumSearchPop(GridData gdReq) throws DAOException {
        String methodNm = "SLAB정보 수신처리 - 모선관리번호 팝업 정보 조회[PSlabYdJspFaEJB.getListvesselMgrNumSearchPop]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListvesselMgrNumSearchPop", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }
    
    /**
     * SLAB정보 수신처리 - 모선관리번호 팝업 후판 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListvesselMgrNumSearchPop2(GridData gdReq) throws DAOException {
        String methodNm = "SLAB정보 수신처리 - 모선관리번호 팝업 후판 정보 조회[PSlabYdJspFaEJB.getListvesselMgrNumSearchPop2]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListvesselMgrNumSearchPop2", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }
    
    /**
     * SLAB정보 수신처리 - 모선관리번호 팝업 슬라브 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListvesselMgrNumSearchPop3(GridData gdReq) throws DAOException {
        String methodNm = "SLAB정보 수신처리 - 모선관리번호 팝업 슬라브 정보 조회[PSlabYdJspFaEJB.getListvesselMgrNumSearchPop3]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListvesselMgrNumSearchPop3", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }
    
    /**
     * SLAB정보 수신처리 - 모선코드 팝업 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListvesselCDSearchPop(GridData gdReq) throws DAOException {
        String methodNm = "SLAB정보 수신처리  - 모선코드 팝업 정보 조회[PSlabYdJspFaEJB.getListvesselCDSearchPop]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListvesselCDSearchPop", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }
    
    /**
     * 구입슬라브 입고등록 - 당사SLAB 번호 팝업 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListSearchSlabNoPop(GridData gdReq) throws DAOException {
        String methodNm = "구입슬라브 입고등록 - 당사SLAB 번호 팝업 정보 조회[PSlabYdJspFaEJB.getListSearchSlabNoPop]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListSearchSlabNoPop", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }
    
    /**
     * 구입슬라브 입고등록 - 평면조회 팝업 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListPlaneInqPop(GridData gdReq) throws DAOException {
        String methodNm = "구입슬라브 입고등록 - 평면조회 팝업 정보 조회[PSlabYdJspFaEJB.getListPlaneInqPop]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListPlaneInqPop", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }
    
    /**
     * 구입슬라브 입고등록 - 평면조회 팝업2 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListPlaneInqPop2(GridData gdReq) throws DAOException {
        String methodNm = "구입슬라브 입고등록 - 평면조회 팝업2 정보 조회[PSlabYdJspFaEJB.getListPlaneInqPop2]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListPlaneInqPop2", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }
    
    /**
     * 구입슬라브 입고등록 - 번지단면 팝업 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListCellnoRSectInqPop(GridData gdReq) throws DAOException {
        String methodNm = "구입슬라브 입고등록 - 번지단면 팝업 정보 조회[PSlabYdJspFaEJB.getListCellnoRSectInqPop]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListCellnoRSectInqPop", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }
    
    /**
     * 구입슬라브 입고등록 - 번지단면 팝업2
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListCellnoRSectInqPop2(GridData gdReq) throws DAOException {
        String methodNm = "구입슬라브 입고등록 - 번지단면 팝업2 정보 조회[PSlabYdJspFaEJB.getListCellnoRSectInqPop2]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListCellnoRSectInqPop2", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }
    
    /**
     * 적치리스트 조회 - 제조사 팝업 조회
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param GridData gdReq
     * @return GridData
     * @throws DAOException
     */
    public GridData getListMakerSearchPop(GridData gdReq) throws DAOException {
        String methodNm = "적치리스트 조회 - 제조사 팝업 정보 조회[PSlabYdJspFaEJB.getListMakerSearchPop]";
        String logId = slabUtils.getLogId();
        
        try {
            methodNm = methodNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) 
                                + "("   + slabUtils.trim(gdReq.getParam("jsp_page_id")) 
                                + ")_"  + slabUtils.trim(gdReq.getParam("jsp_page_func_nm"));
            
            slabUtils.printLog(logId, methodNm, "F+");

            EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspSeEJB", this);
            GridData     gdRet   = (GridData)ejbConn.trx("getListMakerSearchPop", new Class[] { GridData.class }, new Object[] { gdReq });
            
            String rtnMsg = gdRet.getMessage();
            slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "FL");
            return gdRet;       
            
        } catch(DAOException e) {
            throw e;
        } catch(Exception e) {
            throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
        }
    }
}