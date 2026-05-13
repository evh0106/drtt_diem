/**
 * @(#)BlYsJspFaEJBBean
 *
 * @version          V1.00
 * @author           조병기
 * @date             2014/12/22
 *
 * @description      BLOOM 야드 화면관리 Facade EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2014/12/22   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.bl.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsConstant;
import com.inisteel.cim.ys.jsp.common.YsComUtil;
/**
 *      [A] 클래스명 : BLOOM 야드 화면관리 Facade EJB
 *
 * @ejb.bean name="BlYsJspFaEJB" jndi-name="BlYsJspFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class BlYsJspFaEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();
	 
	YsComUtil   ysComUtil = new YsComUtil();
	
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
		String methodNm =  "조회[BlYsJspFaEJB.getSelectData]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
			                    + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of getSelectData	

	/**
	 * 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSelectData(JDTORecord recPara) throws DAOException {
		String methodNm =  "조회[BlYsJspFaEJB.getSelectData]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) 
			                    + "("   + commUtils.trim(recPara.getFieldString("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(recPara.getFieldString("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", recPara);

			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			JDTORecordSet recordSet = (JDTORecordSet)ejbConn.trx("getSelectData", new Class[] { JDTORecord.class }, new Object[] { recPara });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return recordSet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of getSelectData	
	
	/**
	 * 대차스케줄관리 - 대차초기화
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData initTcarSchMgt(GridData gdReq) throws DAOException {
		String methodNm =  "대차스케줄관리 대차초기화[BlYsJspFaEJB.initTcarSchMgt]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			//대차초기화
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("initTcarSchMgt", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of initTcarSchMgt	
	
	/**
	 * 대차스케줄관리 - 작업예약 우선순위변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updWrkBookPrior(GridData gdReq) throws DAOException {
		String methodNm =  "작업예약 우선순위변경[BlYsJspFaEJB.updWrkBookPrior]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			//우선순위변경
			ejbConn.trx("updWrkBookPrior", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updWrkBookPrior		
	
	/**
	 * 대차스케줄관리, 작업예약관리 - 작업예약삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delWrkBook(GridData gdReq) throws DAOException {
		String methodNm =  "작업예약삭제[BlYsJspFaEJB.delWrkBook]";
		String logId = commUtils.getLogId();
		JDTORecord sndRecord = null;
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			sndRecord = (JDTORecord)ejbConn.trx("delWrkBook", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (sndRecord != null) {
				sndRecord.setResultCode(logId);
				sndRecord.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { sndRecord });
			}
		
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of delWrkBook	
	
	/**
	 * 대차상태설정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	*/
	public GridData trtTcarStatSet(GridData gdReq) throws DAOException {
		String methodNm = "대차상태설정[BlYsJspFaEJB.trtTcarStatSet]";
		String logId = commUtils.getLogId();

		try {
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";

			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);

			//대차상태설정
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtTcarStatSet", new Class[] { GridData.class }, new Object[] { gdReq });
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}


			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of trtTcarStatSet
	
	/**
	 * 저장위치 좌표설정 - 열정보 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStrLocPosSetCol(GridData gdReq) throws DAOException {
		String methodNm =  "저장위치 좌표설정 - 열정보 변경[BlYsJspFaEJB.updStrLocPosSetCol]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updStrLocPosSetCol", new Class[] { GridData.class }, new Object[] { gdReq });
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updStrLocPosSetCol	
	
	/**
	 * 저장위치 좌표설정 - BED정보 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStrLocPosSetBed(GridData gdReq) throws DAOException {
		String methodNm =  "저장위치 좌표설정 - Bed정보 변경[BlYsJspFaEJB.updStrLocPosSetBed]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updStrLocPosSetBed", new Class[] { GridData.class }, new Object[] { gdReq });
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updStrLocPosSetBed	
	
	/**
	 * 스케줄기준관리 - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSchRule(GridData gdReq) throws DAOException {
		String methodNm =  "스케줄기준관리 - 수정[BlYsJspFaEJB.updSchRule]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			//기준관리수정
			ejbConn.trx("updSchRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updSchRule	
	

	/**
	 * [A] 오퍼레이션명 : 크레인작업관리-스케줄취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCraneSchCancel(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업관리-스케줄취소[BlYsJspFaEJB.updCraneSchCancel]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCraneSchCancel", new Class[] { GridData.class }, new Object[] { gdReq });
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCraneSchCancel	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리-작업취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCraneWrkCancel(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업관리-작업취소[BlYsJspFaEJB.updCraneWrkCancel]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCraneWrkCancel", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCraneWrkCancel
	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리-권하위치변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updDownLocChange(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업관리-권하위치변경[BlYsJspFaEJB.updDownLocChange]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updDownLocChange", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updDownLocChange	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리-순위변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPriorChange(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업관리-순위변경[BlYsJspFaEJB.updPriorChange]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updPriorChange", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updPriorChange	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리-긴급작업
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPriorWrkChange(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업관리-긴급작업[BlYsJspFaEJB.updPriorWrkChange]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updPriorWrkChange", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updPriorWrkChange	
				
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리-크레인변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCraneChange(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업관리-크레인변경[BlYsJspFaEJB.updCraneChange]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCraneChange", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCraneChange
	
	/**
	 * 야드 저장위치 등록 (PDA)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord regStrLocPda(JDTORecord recPara) throws DAOException {
		String methodNm =  "야드 저장위치 등록 (PDA) - 등록[BlYsJspFaEJB.regStrLocPda]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) + "(" + commUtils.trim(recPara.getFieldString("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", recPara);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("regStrLocPda", new Class[] { JDTORecord.class }, new Object[] { recPara });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				
				String sRETN_CD = StringHelper.evl(jrRst.getFieldString("RETN_CD"),"");
				
				if(sRETN_CD.equals(YsConstant.RETN_CD_SUCCESS)) {
				
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);
	
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//결과
			return jrRst;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of regStrLocPda	

	/**
	 * 재료저장위치 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStrLocMod(GridData gdReq) throws DAOException {
		String methodNm =  "재료저장위치 수정 [BlYsJspFaEJB.updStrLocMod]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			//변경전 저장품등록 및 정합성 체크
			ejbConn.trx("insBlYsStock", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//재료저장위치 수정
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updStrLocMod", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			

			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updStrLocMod	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인상태설정 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updblCrnStsSetPp(GridData gdReq) throws DAOException {
		String methodNm = "크레인상태설정[BlYsJspFaEJB.updblCrnStsSetPp]";
		String logId = commUtils.getLogId();

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updblCrnStsSetPp", new Class[] { GridData.class }, new Object[] { gdReq });
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}


			
			commUtils.printLog(logId, methodNm, "F-", gdReq);

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인SCH 기동 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData procCrnWrkBookStart(GridData gdReq) throws DAOException {
		String methodNm = "크레인SCH 기동 [BlYsJspFaEJB.procCrnWrkBookStart]";
		String logId = commUtils.getLogId();

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			

			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procCrnWrkBookStart", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			
			commUtils.printLog(logId, methodNm, "F-", gdReq);

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	/**
	 *      [A] 오퍼레이션명 : 이적작업 예약 등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updblMvStkWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "이적작업 예약 등록[BlYsJspFaEJB.updblMvStkWrkBook]";
		String logId = commUtils.getLogId();

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updblMvStkWrkBook", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
		String methodNm =  "IFTest Layout 변경[BlYsJspFaEJB.updIfTestData]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			//IFTest Layout 변경
			ejbConn.trx("updIfTestData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updIfTestData
	
	/**
	 * IFTest 전송 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData sndIfTest(GridData gdReq) throws DAOException {
		String methodNm =  "IFTest 전송[BlYsJspFaEJB.sndIfTest]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			//IFTest 전송
			ejbConn.trx("sndIfTest", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of sndIfTest	
	
	/**
	 * IFTest EAI전송
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData sndIfTestEAI(GridData gdReq) throws DAOException {
		String methodNm =  "IFTest EAI전송[BlYsJspFaEJB.sndIfTestEAI]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			//IFTest EAI전송
			GridData gdRet = (GridData) ejbConn.trx("sndIfTestEAI", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of sndIfTestEAI
	
	/**
	 * 스케줄기준관리 - 선택복구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData resetSchRule(GridData gdReq) throws DAOException {
		String methodNm =  "스케줄기준관리 - 선택복구[BlYsJspFaEJB.resetSchRule]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			//스케줄기준관리 - 선택복구
			ejbConn.trx("resetSchRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of resetSchRule	
	
	/**
	 * 스케줄기준관리 - 전체복구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData resetAllSchRule(GridData gdReq) throws DAOException {
		String methodNm =  "스케줄기준관리 - 전체복구[BlYsJspFaEJB.resetAllSchRule]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			//스케줄기준관리 - 전체복구
			ejbConn.trx("resetAllSchRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of resetAllSchRule	
	/**
	 * 블름차량작업 관리- 포인트 개폐
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData procPntUnit(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "차량작업 포인트 현황 - 포인트 개폐[BlYsJspFaEJB.procPntUnit]";
		String logId = commUtils.getLogId();
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procPntUnit", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
	}  
	/**
	 * 블름차량작업 관리- 입동차량순서 변경처리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData procGdsBayInWoSeqChang(GridData gdReq) throws JDTOException {
		String methodNm =  "차량작업 관리- 입동순서 변경처리[BlYsJspFaEJB.procGdsBayInWoSeqChang]";
		String logId = commUtils.getLogId();
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procGdsBayInWoSeqChang", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
	}  
	/**
	 * 블름차량작업 관리-입동지시
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData procBayInWo(GridData gdReq) throws JDTOException {
		String methodNm =  "블름차량작업 관리 - 입동지시[BlYsJspFaEJB.procBayInWo]";
		String logId = commUtils.getLogId();
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procBayInWo", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
	} 
	/**
	 * 빌름차량작업 관리-차량출발처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData procLeaveCar(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "블름차량작업 관리-차량출발처리[BlYsJspFaEJB.procLeaveCar]";
		String logId = commUtils.getLogId();
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procLeaveCar", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
	}  
	/**
	 * 차량상차정보 조회 - 차량상차정보
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getblCarldInfoInqjl(GridData gdReq) throws DAOException {
		String methodNm =  "차량상차정보 조회 - 차량상차정보[BlYsJspFaEJB.getblCarldInfoInqjl]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";

			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);

			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", this);				
			JDTORecordSet recordSet = (JDTORecordSet) ejbConn.trx("BlYsJspSeEJB", "getblCarldInfoInqjl", inRecord);	
			
			if(recordSet== null && recordSet.size() == 0 ){
			} else {
			
				gdRtn = commUtils.jdtoRecordToGridData(gdRtn, recordSet.toList(), gdReq);
			}	
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}
	
	/**
	 * 차량상차정보 조회 -차상위치 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updblCarldInfoInqjl(GridData gdReq) throws DAOException {
		String methodNm =  "차량상차정보 조회 - 차상위치 수정[BlYsJspFaEJB.updblCarldInfoInqjl]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updblCarldInfoInqjl", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}

	/**
	 * 준비스케줄 - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPrepSchLot(GridData gdReq) throws DAOException {
		String methodNm =  "준비스케줄 - 수정[BlYsJspFaEJB.updPrepSchLot]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			ejbConn.trx("updPrepSchLot", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updPrepSchLot	
	
	/**
	 * 보급Lot등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regSupLot(GridData gdReq) throws DAOException {
		String methodNm =  "보급Lot등록[BlYsJspFaEJB.regSupLot]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			ejbConn.trx("regSupLot", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of regSupLot
	
	/**
	 * 이송Lot등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regFtmvLot(GridData gdReq) throws DAOException {
		String methodNm =  "이송Lot등록[BlYsJspFaEJB.regFtmvLot]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			ejbConn.trx("regFtmvLot", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of regFtmvLot		

	
	/**
	 * 사외이송상차등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regBlStlFrtmove(GridData gdReq) throws DAOException {
		String methodNm =  "사외이송상차등록[BlYsJspFaEJB.regBlStlFrtmove]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("regBlStlFrtmove", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of regBlStlFrtmove
	
	
	/**
	 * 차량입고LOT등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regCarFtmvLot(GridData gdReq) throws DAOException {
		String methodNm =  "차량입고LOT등록[BlYsJspFaEJB.regCarFtmvLot]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("regCarFtmvLot", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of regFtmvLot		
		
	
	/**
	 * 준비스케줄 - 재료삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delPrepMtl(GridData gdReq) throws DAOException {
		String methodNm =  "준비스케줄 - 재료삭제[BlYsJspFaEJB.delPrepMtl]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			ejbConn.trx("delPrepMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of delPrepMtl	
	
	/**
	 * 준비스케줄 - 삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delPrepSchLot(GridData gdReq) throws DAOException {
		String methodNm =  "준비스케줄  - 삭제[BlYsJspFaEJB.delPrepSchLot]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			ejbConn.trx("delPrepSchLot", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of delPrepSchLot	

	/**
	 * 설비인출보급 - 재료등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPulloutSupMtl(GridData gdReq) throws DAOException {
		String methodNm =  "설비인출보급 - 재료등록[BlYsJspFaEJB.updPulloutSupMtl]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			ejbConn.trx("updPulloutSupMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			//GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updPulloutSupMtl		
	
	/**
	 * 설비인출보급 - 재료삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delPulloutSupMtl(GridData gdReq) throws DAOException {
		String methodNm =  "설비인출보급 - 재료삭제[BlYsJspFaEJB.delPulloutSupMtl]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			JDTORecord jrRst =  (JDTORecord)ejbConn.trx("delPulloutSupMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			//GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of delPulloutSupMtl	
	
	/**
	 * 설비인출보급 - CARRY-OUT
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqCarryOut(GridData gdReq) throws DAOException {
		String methodNm =  "설비인출보급 - CARRY-OUT[BlYsJspFaEJB.reqCarryOut]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);
			jrParam.setResultMsg(methodNm);
			JDTORecord jrRst = null;
			if(gdReq.getParam("YS_STK_COL_GP").equals("BAWB01")) {
				jrParam.setField("JMS_TC_CD"			, "M2YSL101" );
				jrParam.setField("YD_EQP_ID"			, gdReq.getParam("YS_STK_COL_GP") );
				jrParam.setField("YS_STK_BED_NO"		, gdReq.getParam("YS_STK_BED_NO") );
				jrParam.setField("YS_STK_LYR_NO"		, "01" );
				jrParam.setField("YD_SCH_ST_GP"			, "A" );
				jrParam.setField("YD_STK_BED_STL_SH"	, gdReq.getParam("YD_STK_BED_STL_SH") );
				jrParam.setField("SSTL_NO1"				, gdReq.getParam("SSTL_NO1") );
				jrParam.setField("SSTL_NO2"				, gdReq.getParam("SSTL_NO2") );
				jrParam.setField("SSTL_NO3"				, gdReq.getParam("SSTL_NO3") );
				jrParam.setField("SSTL_NO4"				, gdReq.getParam("SSTL_NO4") );
				jrParam.setField("SSTL_NO5"				, gdReq.getParam("SSTL_NO5") );
				jrParam.setField("L3_HMI"				, "Y" );   //백업화면 기동 여부
				
				EJBConnector ejbConn = new EJBConnector("default", "BlYsL2RcvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvM2YSL101", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			} else {
				jrParam.setField("JMS_TC_CD"			, "M3YSL102" );
				jrParam.setField("YD_EQP_ID"			, gdReq.getParam("YS_STK_COL_GP") );
				jrParam.setField("YS_STK_BED_NO"		, "01" );
				jrParam.setField("YS_STK_LYR_NO"		, "01" );
				jrParam.setField("YD_SCH_ST_GP"			, "A" );
				jrParam.setField("YD_STK_BED_STL_SH"	, gdReq.getParam("YD_STK_BED_STL_SH") );
				jrParam.setField("SSTL_NO1"				, gdReq.getParam("SSTL_NO1") );
				jrParam.setField("SSTL_NO2"				, gdReq.getParam("SSTL_NO2") );
				jrParam.setField("SSTL_NO3"				, gdReq.getParam("SSTL_NO3") );
				jrParam.setField("SSTL_NO4"				, gdReq.getParam("SSTL_NO4") );
				jrParam.setField("SSTL_NO5"				, gdReq.getParam("SSTL_NO5") );
				jrParam.setField("L3_HMI"				, "Y" );   //백업화면 기동 여부
				
				EJBConnector ejbConn = new EJBConnector("default", "BlYsL2RcvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvM3YSL102", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			}
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of reqCarryOut	

	/**
	 * 설비인출보급 - CARRY-IN
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqCarryIn(GridData gdReq) throws DAOException {
		String methodNm =  "설비인출보급 - CARRY-IN[BlYsJspFaEJB.reqCarryIn]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("JMS_TC_CD"			, gdReq.getParam("JMS_TC_CD") );
			jrParam.setField("YD_EQP_ID"			, gdReq.getParam("YS_STK_COL_GP") );
			jrParam.setField("YS_STK_BED_NO"		, gdReq.getParam("YS_STK_BED_NO") );
			jrParam.setField("L3_HMI"				, "Y" );   //백업화면 기동 여부
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsL2RcvSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("rcv"+gdReq.getParam("JMS_TC_CD"), new Class[] { JDTORecord.class }, new Object[] { jrParam });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of reqCarryIn	
	
	/**
	 * 기준관리 - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updYsRule(GridData gdReq) throws DAOException {
		String methodNm =  "기준관리 - 수정[BlYsJspFaEJB.updYsRule]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			ejbConn.trx("updYsRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updYsRule		
	
	/**
	 * 차량도착요구 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqCarArrive(GridData gdReq) throws DAOException {
		String methodNm =  "차량도착요구 - [BlYsJspFaEJB.reqCarArrive]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("YS_STK_COL_GP", gdReq.getParam("YD_CAR_STOP_LOC"));
			JDTORecordSet rsStkCol = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkCol1", logId, methodNm, "적치열 조회"); 
	    	
	    	if (rsStkCol == null || rsStkCol.size() <= 0) {
				throw new Exception("차량정지Poitn : "+gdReq.getParam("YD_CAR_STOP_LOC")+" 가 적치열에 존재하지 않습니다!");
			}
			
	    	rsStkCol.first(); 
	    	JDTORecord recStkCol	= rsStkCol.getRecord();
	    	
			String szWLOC_CD 		= StringHelper.evl(recStkCol.getFieldString("WLOC_CD"), ""); 
			String szYD_PNT_CD 		= StringHelper.evl(recStkCol.getFieldString("YD_PNT_CD"), ""); 
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name	    	
			jrParam.setField("JMS_TC_CD"			,"YSYSJ906");
			jrParam.setField("JMS_TC_CREATE_DDTT" 	,commUtils.getDateTime14());
			jrParam.setField("TRANS_ORD_DT"			,gdReq.getParam("TRANS_ORD_DATE"));
			jrParam.setField("TRANS_ORD_SEQNO" 		,gdReq.getParam("TRANS_ORD_SEQNO"));
			jrParam.setField("CAR_NO"				,gdReq.getParam("CAR_NO"));
			jrParam.setField("SPOS_WLOC_CD"			,szWLOC_CD);	
			jrParam.setField("SPOS_YD_PNT_CD"		,szYD_PNT_CD);
			jrParam.setField("L3_HMI"   			,"Y");
			
			EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("rcvYSYSJ906", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of reqCarArrive	
	
	/**
	 * 설비보급 - 장입보급기준 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updAutoSupRule(GridData gdReq) throws DAOException {
		String methodNm =  "설비보급 - 장입보급기준 변경[BlYsJspFaEJB.updAutoSupRule]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			ejbConn.trx("updAutoSupRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updAutoSupRule	
	
	/**
	 * 장입실적 BACKUP
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData chgWrBackUp(GridData gdReq) throws DAOException {
		String methodNm =  "장입실적 BACKUP[BlYsJspFaEJB.chgWrBackUp]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			JDTORecord jrRst =  (JDTORecord)ejbConn.trx("chgWrBackUp", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of chgWrBackUp		
	
	/**
	 * Carry-In완료전송
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData sndCarryInDone(GridData gdReq) throws DAOException {
		String methodNm =  "Carry-In완료전송[BlYsJspFaEJB.sndCarryInDone]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			JDTORecord jrRst =  (JDTORecord)ejbConn.trx("sndCarryInDone", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of sndCarryInDone		

	/**
	 * 서냉피트 관리 - BED활성상태 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updBedActStat(GridData gdReq) throws DAOException {
		String methodNm =  "서냉피트 관리 - BED활성상태 변경[BlYsJspFaEJB.updBedActStat]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			ejbConn.trx("updBedActStat", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updBedActStat		
	
	/**
	 * Carry-Out완료전송
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData sndCarryOutDone(GridData gdReq) throws DAOException {
		String methodNm =  "Carry-Out완료전송[BlYsJspFaEJB.sndCarryOutDone]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			JDTORecord jrRst =  (JDTORecord)ejbConn.trx("sndCarryOutDone", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of sndCarryOutDone		

	/**
	 * 장입대인출 - CARRY-OUT
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqTzCarryOut(GridData gdReq) throws DAOException {
		String methodNm =  "장입대인출 - CARRY-OUT[BlYsJspFaEJB.reqTzCarryOut]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);
			jrParam.setResultMsg(methodNm);
			JDTORecord jrRst = null;
			
			//BBTZ01 --> BBLB01 재료 이동 처리 ------------------------------------------------------ 
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			ejbConn.trx("movTZtoLB", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//이상재 Carry-Out 요청 호출 ------------------------------------------------------------
			jrParam.setField("JMS_TC_CD"			, "M3YSL102" );
			//jrParam.setField("YD_EQP_ID"			, gdReq.getParam("YS_STK_COL_GP") );
			jrParam.setField("YD_EQP_ID"			, "BBLB01" );
			jrParam.setField("YS_STK_BED_NO"		, "01" );
			jrParam.setField("YS_STK_LYR_NO"		, "01" );
			jrParam.setField("YD_SCH_ST_GP"			, "A" );
			jrParam.setField("YD_STK_BED_STL_SH"	, gdReq.getParam("YD_STK_BED_STL_SH") );
			jrParam.setField("SSTL_NO1"				, gdReq.getParam("SSTL_NO1") );
			jrParam.setField("SSTL_NO2"				, gdReq.getParam("SSTL_NO2") );
			jrParam.setField("SSTL_NO3"				, gdReq.getParam("SSTL_NO3") );
			jrParam.setField("SSTL_NO4"				, gdReq.getParam("SSTL_NO4") );
			jrParam.setField("SSTL_NO5"				, gdReq.getParam("SSTL_NO5") );
			jrParam.setField("L3_HMI"				, "Y" );   //백업화면 기동 여부
			
			EJBConnector ejbConn2 = new EJBConnector("default", "BlYsL2RcvSeEJB", this);
			jrRst = (JDTORecord)ejbConn2.trx("rcvM3YSL102", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of reqTzCarryOut	

	/**
	 * 준비스케줄 - 적치위치수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPrepMtl(GridData gdReq) throws DAOException {
		String methodNm =  "준비스케줄 - 적치위치수정[BlYsJspFaEJB.updPrepMtl]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			ejbConn.trx("updPrepMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updPrepMtl	

	/**
	 * 준비스케줄 - 하차도착처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regCarUdWrk(GridData gdReq) throws DAOException {
		String methodNm =  "준비스케줄 - 하차도착처리[BlYsJspFaEJB.regCarUdWrk]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("regCarUdWrk", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of regCarUdWrk	
	
	/**
	 * 기준관리  - 검색가이드 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updYsRuleSrchGd(GridData gdReq) throws DAOException {
		String methodNm =  "기준관리 - 검색가이드 수정[BlYsJspFaEJB.updYsRuleSrchGd]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			ejbConn.trx("updYsRuleSrchGd", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updYsRuleSrchGd		

	/**
	 * 저장위치별 현황 - 적치열 활성상태 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updColActStat(GridData gdReq) throws DAOException {
		String methodNm =  "저장위치별 현황 - 적치열 활성상태 변경[BlYsJspFaEJB.updColActStat]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			ejbConn.trx("updColActStat", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updColActStat		
	
	/**
	 * 이송차량스케줄 관리 - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updMvCarSch(GridData gdReq) throws DAOException {
		String methodNm =  "이송차량스케줄 관리 - 수정[BlYsJspFaEJB.updMvCarSch]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			ejbConn.trx("updMvCarSch", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updMvCarSch	
	
	/**
	 * 구내운송차량출발
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqTsStart(GridData gdReq) throws DAOException {
		String methodNm =  "구내운송차량출발 [BlYsJspFaEJB.reqTsStart]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("JMS_TC_CD"			, "TSYSJ004" );
			jrParam.setField("TRN_EQP_CD"			, gdReq.getParam("TRN_EQP_CD") );
			jrParam.setField("SPOS_WLOC_CD"			, "DMY1P" );
			jrParam.setField("ARR_WLOC_CD"			, YsConstant.BL_WLOC_CD_A );
			jrParam.setField("ARR_YD_PNT_CD"		, "1A01" );
			jrParam.setField("TRN_WRK_FULLVOID_GP"	, "E" );
			jrParam.setField("YD_WO_CNCL_YN"		, "N" );
			jrParam.setField("CARLD_SH"				, "0" );
			jrParam.setField("L3_HMI"				, "Y" );   //백업화면 기동 여부
			
			
			EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("rcvTSYSJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of reqTsStart	
	
	/**
	 * 이송차량스케줄 초기화
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData initMvCarSchMgt(GridData gdReq) throws DAOException {
		String methodNm =  "이송차량스케줄 초기화[BlYsJspFaEJB.initTcarSchMgt]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			//대차초기화
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("initMvCarSchMgt", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of initMvCarSchMgt	

	/**
	 * 이송차량상태설절팝업 - 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtMvCarStatSet(GridData gdReq) throws DAOException {
		String methodNm =  "이송차량상태설절팝업 - 등록[BlYsJspFaEJB.trtMvCarStatSet]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtMvCarStatSet", new Class[] { GridData.class }, new Object[] { gdReq });

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of trtMvCarStatSet	

	/**
	 * 이송차량 실적처리 팝업 - 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtMvCarStatSet2(GridData gdReq) throws DAOException {
		String methodNm =  "이송차량 실적처리 팝업 - 등록[BlYsJspFaEJB.trtMvCarStatSet2]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtMvCarStatSet2", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of trtMvCarStatSet2	
	
	/**
	 * 이송작업재료등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm =  "이송작업재료등록[BlYsJspFaEJB.updCarFtMvMtl]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			ejbConn.trx("updCarFtMvMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			//GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCarFtMvMtl
	
	/**
	 * 이송작업재료삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm =  "이송작업재료삭제[BlYsJspFaEJB.delCarFtMvMtl]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			JDTORecord jrRst =  (JDTORecord)ejbConn.trx("delCarFtMvMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			//GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of delCarFtMvMtl	

	/**
	 * 이송작업재료위치변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData chgCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm =  "이송작업재료위치변경[BlYsJspFaEJB.chgCarFtMvMtl]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			
			JDTORecord jrRst =  (JDTORecord)ejbConn.trx("chgCarFtMvMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			//GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of chgCarFtMvMtl	
	
	/**
	 * [A] 오퍼레이션명: BLOOM재료 메시지등록
	 * 
	 * @ejb.interface-method
	 * @param inParam
	 * @return
	 * @throws JDTOException
	 */
	public GridData updateStlMessage(GridData gdReq) throws JDTOException {
  
		String methodNm =  "BLOOM재료 메시지등록[BlYsJspFaEJB.updateStlMessage]";
		String logId = commUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			ejbConn.trx("updateStlMessage", new Class[] { GridData.class }, new Object[] { gdReq });
			
			return gdReq ;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * [A] 오퍼레이션명: BILLET 메모 저장
	 * 
	 * @ejb.interface-method
	 * @param inParam
	 * @return
	 * @throws JDTOException
	 */
	public GridData updMemoData(GridData gdReq) throws JDTOException {
  
		String methodNm =  "BILLET 메모 저장[BlYsJspFaEJB.updMemoData]";
		String logId = commUtils.getLogId();
		
		try {
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			ejbConn.trx("updMemoData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			return gdReq ;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 설비보급 - 장입대사용여부 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updAutoLayerRule(GridData gdReq) throws DAOException {
		String methodNm =  "설비보급 - 장입대사용여부 변경[BlYsJspFaEJB.updAutoLayerRule]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
			ejbConn.trx("updAutoLayerRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updAutoLayerRule	
	
	 
	
}	