/**
 * @(#)GdsYsJspFaEJBBean
 *
 * @version          V1.00
 * @author           조병기
 * @date             2014/12/22
 *
 * @description      제품(봉강,선재) 야드 화면관리 Facade EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2014/12/22   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.gds.session;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsConstant;
import com.inisteel.cim.ys.jsp.common.YsComUtil;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
/**
 *      [A] 클래스명 : 제품(봉강,선재) 야드 화면관리 Facade EJB
 *
 * @ejb.bean name="GdsYsJspFaEJB" jndi-name="GdsYsJspFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/ 
public class GdsYsJspFaEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YsCommUtils commUtils = new YsCommUtils();
	private YsComUtil ysComUtil = new YsComUtil();
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
		String methodNm =  "조회[GdsYsJspFaEJB.getSelectData]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
			                    + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
		String methodNm =  "조회[GdsYsJspFaEJB.getSelectData]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) 
			                    + "("   + commUtils.trim(recPara.getFieldString("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(recPara.getFieldString("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", recPara);

			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
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
	 * [A] 오퍼레이션명 : 입고예정동 수정 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updgdsWhsPlnInfojl(GridData gdReq) throws DAOException {
		String methodNm =  "제품입고예정정보-입고예정동 수정[GdsYsJspFaEJB.updgdsWhsPlnInfojl]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updgdsWhsPlnInfojl", new Class[] { GridData.class }, new Object[] { gdReq });
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
		
	}	// end of updgdsWhsPlnInfojl	
	
	/**	
	 * [A] 오퍼레이션명 : 입고예정동 수정 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updgdsWhsPlnInfojl_01(GridData gdReq) throws DAOException {
		String methodNm =  "제품입고예정정보-입고예정동 수정[GdsYsJspFaEJB.updgdsWhsPlnInfojl_01]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updgdsWhsPlnInfojl_01", new Class[] { GridData.class }, new Object[] { gdReq });
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
		
	}	// end of updgdsWhsPlnInfojl_01
	
	/**
	 * IFTest Layout 변경 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updIfTestData(GridData gdReq) throws DAOException {
		String methodNm =  "IFTest Layout 변경[GdsYsJspFaEJB.updIfTestData]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
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
		String methodNm =  "IFTest 전송[GdsYsJspFaEJB.sndIfTest]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
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
		String methodNm =  "IFTest EAI전송[GdsYsJspFaEJB.sndIfTestEAI]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
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
	 * [A] 오퍼레이션명 : 제품 재료상세정보 조회-출하위치송신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updgdsMtlDtlInfoInqjl(GridData gdReq) throws DAOException {
		String methodNm =  "제품 재료상세정보 조회-출하위치송신[GdsYsJspFaEJB.updgdsMtlDtlInfoInqjl]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updgdsMtlDtlInfoInqjl", new Class[] { GridData.class }, new Object[] { gdReq });
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
		
	}	// end of updgdsMtlDtlInfoInqjl		

	/**
	 * [A] 오퍼레이션명 : 봉강 자동창고 저장계획 -저장계획등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData insgdsRbAutoWhuStrPlnjm(GridData gdReq) throws DAOException {
		String methodNm =  "봉강 자동창고 저장계획-저장계획등록[GdsYsJspFaEJB.insgdsRbAutoWhuStrPlnjm]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
//			JDTORecord jrRst = (JDTORecord)ejbConn.trx("insgdsRbAutoWhuStrPlnjm", new Class[] { GridData.class }, new Object[] { gdReq });
			ejbConn.trx("insgdsRbAutoWhuStrPlnjm", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of insgdsRbAutoWhuStrPlnjm		
	
	/**
	 * [A] 오퍼레이션명 : 봉강 자동창고 저장계획 -저장계획수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updgdsRbAutoWhuStrPlnjm(GridData gdReq) throws DAOException {
		String methodNm =  "봉강 자동창고 저장계획-저장계획수정[GdsYsJspFaEJB.updgdsRbAutoWhuStrPlnjm]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
//			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updgdsRbAutoWhuStrPlnjm", new Class[] { GridData.class }, new Object[] { gdReq });
			ejbConn.trx("updgdsRbAutoWhuStrPlnjm", new Class[] { GridData.class }, new Object[] { gdReq });

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
		
	}	// end of updgdsRbAutoWhuStrPlnjm		
	
	/**
	 * [A] 오퍼레이션명 : 봉강 자동창고 저장계획 -저장계획삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delgdsRbAutoWhuStrPlnjm(GridData gdReq) throws DAOException {
		String methodNm =  "봉강 자동창고 저장계획-저장계획삭제[GdsYsJspFaEJB.delgdsRbAutoWhuStrPlnjm]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
//			JDTORecord jrRst = (JDTORecord)ejbConn.trx("delgdsRbAutoWhuStrPlnjm", new Class[] { GridData.class }, new Object[] { gdReq });
			ejbConn.trx("delgdsRbAutoWhuStrPlnjm", new Class[] { GridData.class }, new Object[] { gdReq });

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
		
	}	// end of delgdsRbAutoWhuStrPlnjm		

	
	/**
	 * 봉강제품 기준관리 - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updYsRule(GridData gdReq) throws DAOException {
		String methodNm =  "봉강제품 기준관리 - 수정[GdsYsJspFaEJB.updYsRule]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
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
	 * [A] 오퍼레이션명 : BED별 이적지시 -크레인작업SCH등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updgdsRbBedMoveWojm(GridData gdReq) throws DAOException {
		String methodNm =  "BED별 이적지시 -크레인작업SCH등록[GdsYsJspFaEJB.updgdsRbBedMoveWojm]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updgdsRbBedMoveWojm", new Class[] { GridData.class }, new Object[] { gdReq });

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
		
	}	// end of updgdsRbBedMoveWojm		

	/**
	 * 출고검수 이상제품 - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCarIssueChk(GridData gdReq) throws DAOException {
		String methodNm =  "출고검수 이상제품 - 수정[GdsYsJspFaEJB.updCarIssueChk]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			ejbConn.trx("updCarIssueChk", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updCarIssueChk	
	
	/**
	 * 출고검수 PDA - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public JDTORecord updCarIssueChkPda(JDTORecord recPara) throws DAOException {
		
		String methodNm =  "출고검수 PDA - 수정[GdsYsJspFaEJB.updCarIssueChkPda]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) + "(" + commUtils.trim(recPara.getFieldString("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", recPara);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCarIssueChkPda", new Class[] { JDTORecord.class }, new Object[] { recPara });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return jrRst;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCarIssueChkPda	
	
	/**
	 * 출고검수 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData distCheckProc(GridData gdReq) throws DAOException {
		String methodNm =  "출고검수 [GdsYsJspFaEJB.updCarIssueChk]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("distCheckProc", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of distCheckProc	

	/**
	 * 출고검수 PDA - 출고검수
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public JDTORecord distCheckProcPda(JDTORecord recPara) throws DAOException {
		
		String methodNm =  "출고검수 PDA - 출고검수[GdsYsJspFaEJB.distCheckProcPda]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) + "(" + commUtils.trim(recPara.getFieldString("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", recPara);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("distCheckProcPda", new Class[] { JDTORecord.class }, new Object[] { recPara });
			
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

			//조회결과
			return jrRst;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of distCheckProcPda	

	/**
	 * 입고검수 PDA - 입고검수
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public JDTORecord whsChkProcPda(JDTORecord recPara) throws DAOException {
		
		String methodNm =  "입고검수 PDA - 입고검수[GdsYsJspFaEJB.whsChkProcPda]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) + "(" + commUtils.trim(recPara.getFieldString("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", recPara);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("whsChkProcPda", new Class[] { JDTORecord.class }, new Object[] { recPara });
			
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

			//조회결과
			return jrRst;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of whsChkProcPda	
	
	/**
	 * 입고검수 PDA - 입고검수(각강)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public JDTORecord sbChkProcPda(JDTORecord recPara) throws DAOException {
		
		String methodNm =  "입고검수 PDA - 입고검수(각강)[GdsYsJspFaEJB.sbChkProcPda]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) + "(" + commUtils.trim(recPara.getFieldString("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", recPara);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("sbChkProcPda", new Class[] { JDTORecord.class }, new Object[] { recPara });
			
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

			//조회결과
			return jrRst;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of whsChkProcPda	
	
	/**
	 * 반입검수 PDA - 반입검수
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public JDTORecord ReturnChkProcPda(JDTORecord recPara) throws DAOException {
		
		String methodNm =  "반입검수 PDA - 반입검수[GdsYsJspFaEJB.ReturnChkProcPda]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) + "(" + commUtils.trim(recPara.getFieldString("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", recPara);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("ReturnChkProcPda", new Class[] { JDTORecord.class }, new Object[] { recPara });
			
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

			//조회결과
			return jrRst;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of whsChkProcPda	
	
	/**
	 * 반입검수 PDA - 차량출발 및 입동 지시 송신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public JDTORecord ReturnCarBayInOrdReq(JDTORecord recPara) throws DAOException {
		
		String methodNm =  "반입검수 PDA - 차량출발 및 입동 지시 송신[GdsYsJspFaEJB.ReturnCarBayInOrdReq]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) + "(" + commUtils.trim(recPara.getFieldString("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", recPara);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("ReturnCarBayInOrdReq", new Class[] { JDTORecord.class }, new Object[] { recPara });
			
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

			//조회결과
			return jrRst;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of whsChkProcPda	
		
	
	/**
	 * [A] 오퍼레이션명 : BED별 이적지시 -크레인작업SCH등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData insgdsRbABayMoveWkAutojm(GridData gdReq) throws DAOException {
		String methodNm =  "봉강A동 이적작업 자동지시 -크레인작업SCH등록[GdsYsJspFaEJB.insgdsRbABayMoveWkAutojm]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("insgdsRbABayMoveWkAutojm", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회
//			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of insgdsRbABayMoveWkAutojm		

	/**
	 * [A] 오퍼레이션명 : BED별 이적지시 -크레인작업SCH등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData insgdsRbBBaySelWkAutojm(GridData gdReq) throws DAOException {
		String methodNm =  "봉강B동 선별작업 자동지시 -크레인작업SCH등록[GdsYsJspFaEJB.insgdsRbBBaySelWkAutojm]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
//			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("insgdsRbBBaySelWkAutojm", new Class[] { GridData.class }, new Object[] { gdReq });
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
		
	}	// end of insgdsRbABayMoveWkAutojm			
	
	/**
	 * 저장위치 기준관리 - 열정보 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStrLocPosSetCol(GridData gdReq) throws DAOException {
		String methodNm =  "저장위치 기준관리 - 열정보 변경[GdsYsJspFaEJB.updStrLocPosSetCol]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			
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
	 * 저장위치 기준관리 - BED정보 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStrLocPosSetBed(GridData gdReq) throws DAOException {
		String methodNm =  "저장위치 기준관리 - Bed정보 변경[GdsYsJspFaEJB.updStrLocPosSetBed]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			
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
	 * 특수강 봉강 사외임가공사 이송완료실적 처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updInlnOutStlInfo(GridData gdReq) throws DAOException {
		String methodNm =  "특수강 봉강 사외임가공사 이송완료실적 처리[GdsYsJspFaEJB.updInlnOutStlInfo]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updInlnOutStlInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updInlnOutStlInfo	
	
	/**
	 * 특수강 봉강 사외임가공사 입고실적 처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData sndInlnOutStlInfo(GridData gdReq) throws DAOException {
		String methodNm =  "특수강 봉강 사외임가공사 입고실적 처리[GdsYsJspFaEJB.sndInlnOutStlInfo]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("sndInlnOutStlInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of sndInlnOutStlInfo	
	
	/**
	 * 특수강 봉강 정정 이송실적전송
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData upbundleOutStlInfo(GridData gdReq) throws DAOException {
		String methodNm =  "특수강 봉강 정정 이송실적 전송처리[GdsYsJspFaEJB.upbundleOutStlInfo]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("upbundleOutStlInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of upbundleOutStlInfo	

	/**
	 * 저장위치 기준관리 - Lyr정보 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStrLocPosSetLyr(GridData gdReq) throws DAOException {
		String methodNm =  "저장위치 기준관리 - Bed정보 변경[GdsYsJspFaEJB.updStrLocPosSetLyr]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updStrLocPosSetLyr", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updStrLocPosSetLyr
	
	/**
	 * 차량상차정보 조회 - 차량상차정보
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getgdsCarldInfoInqjl(GridData gdReq) throws DAOException {
		String methodNm =  "차량상차정보 조회 - 차량상차정보[GdsYsJspFaEJB.getgdsCarldInfoInqjl]";  
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";

			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);

			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", this);				
			JDTORecordSet recordSet = (JDTORecordSet) ejbConn.trx("GdsYsJspSeEJB", "getgdsCarldInfoInqjl", inRecord);	
			
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
		
	}	// end of getgdsCarldInfoInqjl	
	
	/**
	 * 저장위치 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStrLocMod(GridData gdReq) throws DAOException {
		String methodNm =  "저장위치 수정 [GdsYsJspFaEJB.updStrLocMod]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			//변경전 저장품등록 및 정합성 체크
			ejbConn.trx("insGdsYsStock", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//재료저장위치 수정
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updStrLocMod", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회 - 저장위치 수정 화면은 Grid가 2개 임으로 화면에서 다시 doSearch()를 호출 하는 방식으로 함
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updStrLocMod	
	
	/**
	 * 저장위치 수정(재공야드)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updInlnLocMod(GridData gdReq) throws DAOException {
		String methodNm =  "재공야드 저장위치 수정 [GdsYsJspFaEJB.updInlnLocMod]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			//재료저장위치 수정
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updInlnLocMod", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회 - 저장위치 수정 화면은 Grid가 2개 임으로 화면에서 다시 doSearch()를 호출 하는 방식으로 함
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updInlnLocMod	
	
	/**
	 * 차상위치 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updgdsCarldInfoInqjl(GridData gdReq) throws DAOException {
		String methodNm =  "차량상차정보 조회 - 차상위치 수정[GdsYsJspFaEJB.updgdsCarldInfoInqjl]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updgdsCarldInfoInqjl", new Class[] { GridData.class }, new Object[] { gdReq });

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
		
	}	// end of updStrLocMod	

	/**
	 * [A] 오퍼레이션명 : 선재 제품단위 이적지시 -크레인작업SCH등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updgdsWrMoveWojm(GridData gdReq) throws DAOException {
		String methodNm =  "선재 제품단위 이적지시 -크레인작업SCH등록[GdsYsJspFaEJB.updgdsWrMoveWojm]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updgdsWrMoveWojm", new Class[] { GridData.class }, new Object[] { gdReq });

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
		
	}	// end of updgdsWrMoveWojm		
	
	/**
	 * [A] 오퍼레이션명 : 반납/반송  관리 -크레인작업SCH등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updgdsRetnjm(GridData gdReq) throws DAOException {
		String methodNm =  "반납/반송  관리 -크레인작업SCH등록[GdsYsJspFaEJB.updgdsRetnjm]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updgdsRetnjm", new Class[] { GridData.class }, new Object[] { gdReq });

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
		
	}	// end of updgdsRetnjm		
		
	/**
	 * 스케줄기준관리 - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSchRule(GridData gdReq) throws DAOException {
		String methodNm =  "스케줄기준관리 - 수정[GdsYsJspFaEJB.updSchRule]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			//기준관리수정
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updSchRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updSchRule	
	
	/**
	 * 스케줄기준관리 - 선택복구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData resetSchRule(GridData gdReq) throws DAOException {
		String methodNm =  "스케줄기준관리 - 선택복구[GdsYsJspFaEJB.resetSchRule]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			//스케줄기준관리 - 선택복구
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("resetSchRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		String methodNm =  "스케줄기준관리 - 전체복구[GdsYsJspFaEJB.resetAllSchRule]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			//스케줄기준관리 - 전체복구
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("resetAllSchRule", new Class[] { GridData.class }, new Object[] { gdReq });
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
		
	}	// end of resetAllSchRule	
	
	/**
	 * 스케줄기준관리 - 전송
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData sndSchRule(GridData gdReq) throws DAOException {
		String methodNm =  "스케줄기준관리 - 전송[GdsYsJspFaEJB.sndSchRule]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
//			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			//기준관리수정
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("sndSchRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of sndSchRule	
	
	
	/**
	 * 장비가동상황 - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updEqpOprnStat(GridData gdReq) throws DAOException {
		String methodNm =  "장비가동상황 - 수정[GdsYsJspFaEJB.updEqpOprnStat]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updEqpOprnStat", new Class[] { GridData.class }, new Object[] { gdReq });

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
		
	}	// end of updEqpOprnStat	

	

	/**
	 * [A] 오퍼레이션명 : 크레인작업관리-크레인변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCraneChange(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업관리-크레인변경[GdsYsJspFaEJB.updCraneChange]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
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
			//gdRes.setMessage(szRtnValue);

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
	 * [A] 오퍼레이션명 : 크레인작업관리-권하위치변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updDownLocChange(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업관리-권하위치변경[GdsYsJspFaEJB.updDownLocChange]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
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
	 * [A] 오퍼레이션명 : 크레인작업관리-작업취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCraneWrkCancel(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업관리-작업취소[GdsYsJspFaEJB.updCraneWrkCancel]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
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
	 * [A] 오퍼레이션명 : 크레인작업관리-스케줄취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCraneSchCancel(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업관리-스케줄취소[GdsYsJspFaEJB.updCraneSchCancel]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

		//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCraneSchCancel", new Class[] { GridData.class }, new Object[] { gdReq });

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
	 * [A] 오퍼레이션명 : 크레인작업관리PP-설비 고장/정상 설정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCrnStsSetCrnStat(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업관리PP-설비 고장/정상 설정[GdsYsJspFaEJB.updCrnStsSetCrnStat]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCrnStsSetCrnStat", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			//gdRes.setMessage(szRtnValue);

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCrnStsSetCrnStat
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리PP-모드변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCrnStsSetCrnMode(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업관리PP-모드변경[GdsYsJspFaEJB.updCrnStsSetCrnMode]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCrnStsSetCrnMode", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			//gdRes.setMessage(szRtnValue);
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCrnStsSetCrnMode
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리PP- 명령선택기동
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCmdSelStart(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리PP- 명령선택기동[GdsYsJspFaEJB.updCmdSelStart] < " + gdReq.getNavigateValue();
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCmdSelStart", new Class[] { GridData.class }, new Object[] { gdReq });

			//gdRes.setMessage(szRtnValue);
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
		
	}	// end of updCmdSelStart	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리PP- 작업구분 지정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData crnWrkGPartSet(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리PP- 작업구분 지정[GdsYsJspFaEJB.crnWrkGPartSet] < " + gdReq.getNavigateValue();
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			//크레인변경
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			ejbConn.trx("crnWrkGPartSet", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of crnWrkGPartSet		
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리PP- 권상실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCrnUpPrsBackUp(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리PP- 권상실적[GdsYsJspFaEJB.updCrnUpPrsBackUp] < " + gdReq.getNavigateValue();
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCrnUpPrsBackUp", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			//gdRes.setMessage(szRtnValue);
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCrnUpPrsBackUp
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리PP- 권하실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCrnDnPrsBackUp(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리PP- 권하실적[GdsYsJspFaEJB.updCrnDnPrsBackUp] < " + gdReq.getNavigateValue();
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			//권하처리
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCrnDnPrsBackUp", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			//gdRes.setMessage(szRtnValue);

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCrnDnPrsBackUp	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리PP- 권상권하실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCrnUpDnPrsBackUp(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리PP- 권상권하실적[GdsYsJspFaEJB.updCrnUpDnPrsBackUp] < " + gdReq.getNavigateValue();
		String logId = commUtils.getLogId();
		JDTORecord jrRst = null;
		JDTORecord sndRecord = null;
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			//크레인권상
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			sndRecord = (JDTORecord)ejbConn.trx("updCrnUpPrsBackUp", new Class[] { GridData.class }, new Object[] { gdReq });

			//권하처리
			EJBConnector ejbConn1 = new EJBConnector("default", "GdsYsJspSeEJB", this);
			jrRst = (JDTORecord)ejbConn1.trx("updCrnDnPrsBackUp", new Class[] { GridData.class }, new Object[] { gdReq });
			
			sndRecord = commUtils.addSndData(sndRecord, jrRst);			
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (sndRecord != null) {
				sndRecord.setResultCode(logId);
				sndRecord.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { sndRecord });
			}
			//gdRes.setMessage(szRtnValue);

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCrnUpDnPrsBackUp	
	
	/**
	 * [A] 오퍼레이션명 : 작업예약조회-스케즐 점검
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procCrnSchRunnable(GridData gdReq) throws DAOException {
		String methodNm = "작업예약조회-스케쥴점검[GdsYsJspFaEJB.procCrnSchRunnable] < " + gdReq.getNavigateValue();
		String logId = commUtils.getLogId();
		JDTORecord jrRst = null;
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			//크레인권상
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			jrRst = (JDTORecord)ejbConn.trx("procCrnSchRunnable", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			gdRet.setMessage( jrRst.getResultMsg() );	

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of procCrnSchRunnable	
	

	/**
	 * [A] 오퍼레이션명 : 작업예약조회-스케즐 기동
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtCrnWrkBookMgt(GridData gdReq) throws DAOException {
		String methodNm = "작업예약조회-스케쥴기동[GdsYsJspFaEJB.trtCrnWrkBookMgt] < " + gdReq.getNavigateValue();
		String logId = commUtils.getLogId();
		JDTORecord sndRecord = null;
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			//크레인권상
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			sndRecord = (JDTORecord)ejbConn.trx("trtCrnWrkBookMgt", new Class[] { GridData.class }, new Object[] { gdReq });

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
		
	}	// end of trtCrnWrkBookMgt	
	
		
	/**
	 * [A] 오퍼레이션명 : 작업예약관리-스케쥴보류
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData WaitSchedule(GridData gdReq) throws DAOException {
		String methodNm = "작업예약관리-스케쥴보류[GdsYsJspFaEJB.WaitSchedule] < " + gdReq.getNavigateValue();
		String logId = commUtils.getLogId();
		JDTORecord sndRecord = null;		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			//크레인권상
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			sndRecord = (JDTORecord)ejbConn.trx("WaitSchedule", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of WaitSchedule		
	
	
	/**
	 * [A] 오퍼레이션명 : 작업예약관리-스케쥴보류해제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData unWaitSchedule(GridData gdReq) throws DAOException {
		String methodNm = "작업예약관리-스케쥴보류[GdsYsJspFaEJB.unWaitSchedule] < " + gdReq.getNavigateValue();
		String logId = commUtils.getLogId();
		JDTORecord sndRecord = null;
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			//크레인권상
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			sndRecord = (JDTORecord)ejbConn.trx("unWaitSchedule", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of unWaitSchedule		
	
	/**
	 * [A] 오퍼레이션명 : 작업예약관리-취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procWrkBookCncl(GridData gdReq) throws DAOException {
		String methodNm = "작업예약관리-취소[GdsYsJspFaEJB.procWrkBookCncl] < " + gdReq.getNavigateValue();
		String logId = commUtils.getLogId();
		JDTORecord sndRecord = null;
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			//크레인권상
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			sndRecord = (JDTORecord)ejbConn.trx("procWrkBookCncl", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of procWrkBookCncl		
	
	/**
	 * 제품차량작업 관리- 포인트 개폐
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData procPntUnit(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "차량작업 포인트 현황 - 포인트 개폐[GdsYsJspFaEJB.procPntUnit]";
		String logId = commUtils.getLogId();
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
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
	 * 제품차량작업 관리- 봉강출하대기차량 포인트 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData doModCarPnt(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "차량작업 포인트 현황 - 봉강출하대기차량 포인트 변경[GdsYsJspFaEJB.doModCarPnt]";
		String logId = commUtils.getLogId();
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("doModCarPnt", new Class[] { GridData.class }, new Object[] { gdReq });
			
			
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
	 * 제품차량작업 관리- 입동차량순서 변경처리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData procGdsBayInWoSeqChang(GridData gdReq) throws JDTOException {
		String methodNm =  "차량작업 관리- 입동순서 변경처리[GdsYsJspFaEJB.procGdsBayInWoSeqChang]";
		String logId = commUtils.getLogId();
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
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
	 * 제품차량작업 관리-입동지시
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData procBayInWo(GridData gdReq) throws JDTOException {
		String methodNm =  "제품차량작업 관리 - 입동지시[GdsYsJspFaEJB.procBayInWo]";
		String logId = commUtils.getLogId();
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
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
	 * 제품차량작업 관리-차량출발처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData procLeaveCar(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "제품차량작업 관리-차량출발처리[GdsYsJspFaEJB.procLeaveCar]";
		String logId = commUtils.getLogId();
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
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
	 * 제품차량작업 관리-차량하차완료처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData procCarUd(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "제품차량작업 관리-차량하차완료처리[GdsYsJspFaEJB.procCarUd]";
		String logId = commUtils.getLogId();
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procCarUd", new Class[] { GridData.class }, new Object[] { gdReq });

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
	 * 제품차량작업 관리-배차취소처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData delCarSchNCarPoint(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "제품차량작업 관리-배차취소처리[GdsYsJspFaEJB.delCarSchNCarPoint]";
		String logId = commUtils.getLogId();
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("delCarSchNCarPoint", new Class[] { GridData.class }, new Object[] { gdReq });

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
	 * 이송Lot등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regFtmvLot(GridData gdReq) throws DAOException {
		String methodNm =  "이송Lot등록[GdsYsJspFaEJB.regFtmvLot]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
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
	 * 설비인출보급 - 재료등록 (다베드 1단)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPulloutSupMtl(GridData gdReq) throws DAOException {
		String methodNm =  "설비인출보급 - 재료등록[GdsYsJspFaEJB.updPulloutSupMtl]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
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
	 * 설비인출보급 - 재료삭제 (다베드 1단)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delPulloutSupMtl(GridData gdReq) throws DAOException {
		String methodNm =  "설비인출보급 - 재료삭제[GdsYsJspFaEJB.delPulloutSupMtl]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			ejbConn.trx("delPulloutSupMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of delPulloutSupMtl	
	
	/**
	 * 설비인출보급 - 재료등록 (1베드 다단)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPulloutSupMtlMLyr(GridData gdReq) throws DAOException {
		String methodNm =  "설비인출보급 - 재료등록[GdsYsJspFaEJB.updPulloutSupMtlMLyr]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			ejbConn.trx("updPulloutSupMtlMLyr", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updPulloutSupMtlMLyr		
	
	/**
	 * 설비인출보급 - 재료삭제 (1베드 다단)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delPulloutSupMtlMLyr(GridData gdReq) throws DAOException {
		String methodNm =  "설비인출보급 - 재료삭제[GdsYsJspFaEJB.delPulloutSupMtlMLyr]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			ejbConn.trx("delPulloutSupMtlMLyr", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of delPulloutSupMtlMLyr	
	
	/**
	 * 설비인출보급 - CARRY-OUT
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqCarryOut(GridData gdReq) throws DAOException {
		String methodNm =  "설비인출보급 - CARRY-OUT[GdsYsJspFaEJB.reqCarryOut]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			if("N7YSL101".equals(gdReq.getParam("JMS_TC_CD"))) {
				//선재 입고 (N7YSL101)
				jrParam.setField("JMS_TC_CD"			, gdReq.getParam("JMS_TC_CD") );
				jrParam.setField("POSITION"   			, gdReq.getParam("POSITION"));   //1,2,3 : KDCH01~03 , 4 : KECH01 
				jrParam.setField("SSTL_NO"   			, gdReq.getParam("SSTL_NO"));   //선재용   
				jrParam.setField("L3_HMI"   			, "Y");
				
			} else {
				//봉강 PC 입고 (M6YSL101)
				jrParam.setField("JMS_TC_CD"			, gdReq.getParam("JMS_TC_CD") );
				jrParam.setField("YD_EQP_ID"			, gdReq.getParam("YS_STK_COL_GP") );
				jrParam.setField("YS_STK_BED_NO"		, gdReq.getParam("YS_STK_BED_NO") );
				jrParam.setField("SSTL_NO1"   			, gdReq.getParam("SSTL_NO1"));
				jrParam.setField("SSTL_NO2"   			, gdReq.getParam("SSTL_NO2"));
				jrParam.setField("SSTL_NO3"   			, gdReq.getParam("SSTL_NO3"));
				jrParam.setField("SSTL_NO4"   			, gdReq.getParam("SSTL_NO4"));
				jrParam.setField("SSTL_NO5"   			, gdReq.getParam("SSTL_NO5"));
				jrParam.setField("SSTL_NO6"   			, gdReq.getParam("SSTL_NO6"));
				jrParam.setField("SSTL_NO7"   			, gdReq.getParam("SSTL_NO7"));
				jrParam.setField("SSTL_NO8"   			, gdReq.getParam("SSTL_NO8"));
				jrParam.setField("L3_HMI"   			, "Y");
			}
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsL2RcvSeEJB", this);
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
		String methodNm =  "설비인출보급 - CARRY-IN[GdsYsJspFaEJB.reqCarryIn]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("JMS_TC_CD"			, gdReq.getParam("JMS_TC_CD") );
			jrParam.setField("YD_EQP_ID"			, gdReq.getParam("YS_STK_COL_GP") );
			jrParam.setField("YS_STK_BED_NO"		, gdReq.getParam("YS_STK_BED_NO") );
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsL2RcvSeEJB", this);
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
	 * 대차스케줄관리 - 대차초기화
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData initTcarSchMgt(GridData gdReq) throws DAOException {
		String methodNm =  "대차스케줄관리 대차초기화[GdsYsJspFaEJB.initTcarSchMgt]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
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
		String methodNm =  "작업예약 우선순위변경[GdsYsJspFaEJB.updWrkBookPrior]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
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
		String methodNm =  "작업예약삭제[GdsYsJspFaEJB.delWrkBook]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			//우선순위변경
			ejbConn.trx("delWrkBook", new Class[] { GridData.class }, new Object[] { gdReq });

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
		String methodNm = "대차상태설정[GdsYsJspFaEJB.trtTcarStatSet]";
		String logId = commUtils.getLogId();

		try {
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";

			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);

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
	 * 입고실적 BACKUP
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData rcptWrkBackUp(GridData gdReq) throws DAOException {
		String methodNm =  "입고실적 BACKUP[GdsYsJspFaEJB.rcptWrkBackUp]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			JDTORecord jrRst =  (JDTORecord)ejbConn.trx("rcptWrkBackUp", new Class[] { GridData.class }, new Object[] { gdReq });
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
		
	}	// end of rcptWrkBackUp		
	
	/**
	 * 준비스케줄 - 재료삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delPrepMtl(GridData gdReq) throws DAOException {
		String methodNm =  "준비스케줄 - 재료삭제[GdsYsJspFaEJB.delPrepMtl]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
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
		String methodNm =  "준비스케줄 - 삭제[GdsYsJspFaEJB.delFrToMoveLot]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
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
	 * 준비스케줄 - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPrepSchLot(GridData gdReq) throws DAOException {
		String methodNm =  "준비스케줄 - 수정[GdsYsJspFaEJB.updPrepSchLot]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
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
	 * Traverser 상태설정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	*/
	public GridData trtTsStatSet(GridData gdReq) throws DAOException {
		String methodNm = "Traverser상태설정[GdsYsJspFaEJB.trtTsStatSet]";
		String logId = commUtils.getLogId();

		try {
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";

			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);

			JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtTsStatSet", new Class[] { GridData.class }, new Object[] { gdReq });

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
	} // end of trtTsStatSet

	/**
	 * CoilCar 상태설정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	*/
	public GridData trtCcStatSet(GridData gdReq) throws DAOException {
		String methodNm = "CoilCar상태설정[GdsYsJspFaEJB.trtCcStatSet]";
		String logId = commUtils.getLogId();

		try {
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";

			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);

			JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtCcStatSet", new Class[] { GridData.class }, new Object[] { gdReq });
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
	} // end of trtCcStatSet
	
	/**
	 * 임시적치대 인출요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqTyBedCarryOut(GridData gdReq) throws DAOException {
		String methodNm =  "임시적치대 인출요구[GdsYsJspFaEJB.reqTyBedCarryOut]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			JDTORecord jrRst =  (JDTORecord)ejbConn.trx("reqTyBedCarryOut", new Class[] { GridData.class }, new Object[] { gdReq });
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
		
	}	// end of reqTyBedCarryOut		

	/**
	 * 임시적치대 인출요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqTyMLyrBedCarryOut(GridData gdReq) throws DAOException {
		String methodNm =  "다단임시적치대 인출요구[GdsYsJspFaEJB.reqTyMLyrBedCarryOut]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			JDTORecord jrRst =  (JDTORecord)ejbConn.trx("reqTyMLyrBedCarryOut", new Class[] { GridData.class }, new Object[] { gdReq });
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
		
	}	// end of reqTyMLyrBedCarryOut		
	
	/**
	 * RGV BookOut 등록 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	*/
	public GridData trtRgvBookOut(GridData gdReq) throws DAOException {
		String methodNm = "RGV BookOut등록[GdsYsJspFaEJB.trtRgvBookOut]";
		String logId = commUtils.getLogId();

		try {
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";

			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);

			JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtRgvBookOut", new Class[] { GridData.class }, new Object[] { gdReq });

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
	} // end of trtRgvBookOut
	
	/**
	 * B동 Bed 정리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reSetBedStat(GridData gdReq) throws DAOException {
		String methodNm =  "B동 Bed 정리[GdsYsJspFaEJB.reSetBedStat]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			ejbConn.trx("reSetBedStat", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of reSetBedStat	
	
	/**
	 * 선재 CRANE 권상처리 (PDA)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord regLoadPda(JDTORecord recPara) throws DAOException {
		
		String methodNm =  "선재 CRANE 권상처리 (PDA) [GdsYsJspFaEJB.regLoadPda]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) + "(" + commUtils.trim(recPara.getFieldString("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", recPara);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			//대차초기화
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("regLoadPda", new Class[] { JDTORecord.class }, new Object[] { recPara });

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
		
	}	// end of regLoadPda	

	/**
	 * 선재 CRANE 권하처리 (PDA)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord regUnLoadPda(JDTORecord recPara) throws DAOException {
		
		String methodNm =  "선재 CRANE 권하처리 (PDA) [GdsYsJspFaEJB.regUnLoadPda]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) + "(" + commUtils.trim(recPara.getFieldString("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", recPara);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			//대차초기화
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("regUnLoadPda", new Class[] { JDTORecord.class }, new Object[] { recPara });

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
		
	}	// end of regUnLoadPda
	
	/**
	 * 설비인출보급 - CARRY-OUT 완료실적 전송
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData sndCarryOutDone(GridData gdReq) throws DAOException {
		String methodNm =  "설비인출보급 - CARRY-OUT 완료실적 전송[GdsYsJspFaEJB.sndCarryOutDone]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			JDTORecord jrRst =  (JDTORecord)ejbConn.trx("sndCarryOutDone", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

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
	 * 설비인출보급 - CARRY-OUT BackUp
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqCarryOutBackUp(GridData gdReq) throws DAOException {
		String methodNm =  "설비인출보급 - CARRY-OUT BackUp[GdsYsJspFaEJB.reqCarryOutBackUp]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			JDTORecord jrRst =  (JDTORecord)ejbConn.trx("reqCarryOutBackUp", new Class[] { GridData.class }, new Object[] { gdReq });

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
		
	}	// end of reqCarryOutBackUp	
	
	
	
	/**
	 * 설비인출보급 - CARRY-OUTCarryOutAllBackUp
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqCarryOutAllBackUp(GridData gdReq) throws DAOException {
		String methodNm =  "설비인출보급 - CARRY-OUT AllBackUp[GdsYsJspFaEJB.reqCarryOutAllBackUp]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			JDTORecord jrRst =  (JDTORecord)ejbConn.trx("reqCarryOutAllBackUp", new Class[] { GridData.class }, new Object[] { gdReq });

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
		
	}	// end of reqCarryOutAllBackUp	
	
	
	
	
	
	
	
	
	/**
	 * 크레인상태관리 - 우선순위 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	
	public GridData crnChgSchPrior(GridData gdReq) throws JDTOException {						
 
		String methodNm =  "크레인상태관리 - 우선순위 변경[GdsYsJspFaEJB.crnChgSchPrior]"; 
		String logId 			  = commUtils.getLogId();
 
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord [] inRecord = ysComUtil.genJDTORecordSet(gdReq);	
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			ejbConn.trx("crnChgSchPrior", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			 
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
	} // end of crnChgSchPrior()
	/**
	 * [A] 오퍼레이션명 : 선재 제품단위 이적지시 -크레인작업SCH등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updgdsWrMoveWojm1(GridData gdReq) throws DAOException {
		String methodNm =  "선재 제품단위 이적지시 -크레인작업SCH등록[GdsYsJspFaEJB.updgdsWrMoveWojm1]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updgdsWrMoveWojm1", new Class[] { GridData.class }, new Object[] { gdReq });

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
		
	}	// end of updgdsWrMoveWojm		
	
	
	/**
	 * 봉강재공야드 저장위치 수정Mobile
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updStrLocModMobile(JDTORecord recPara) throws DAOException {
		String methodNm =  "봉강재공야드 저장위치 수정Mobile - 등록[GdsYsJspFaEJB.updStrLocModMobile]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) + "(" + commUtils.trim(recPara.getFieldString("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", recPara);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updStrLocModMobile", new Class[] { JDTORecord.class }, new Object[] { recPara });

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
		
	}	// end of updStrLocModMobile

	/**
	 * 봉강재공야드 저장위치 수정Mobile2
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updStrLocModMobile2(String userid , JDTORecord[] inRecordSet) throws DAOException {
		String methodNm =  "봉강재공야드 저장위치 수정Mobile2 - 등록[GdsYsJspFaEJB.updStrLocModMobile2]";
		String logId = commUtils.getLogId();
		
		try {
			// methodNm = methodNm + " < " + commUtils.trim(inRecordSet.getFieldString("jsp_page_nm")) + "(" + commUtils.trim(inRecordSet.getFieldString("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+");
			// commUtils.printLog(logId, methodNm, "F+", inRecordSet[0]);
			// for(int ii=1; ii<inRecordSet.length; ii++){
			//	 commUtils.printLog(logId, methodNm, "FL", inRecordSet[ii]);
			// }
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updStrLocModMobile2", new Class[] { String.class, JDTORecord[].class }, new Object[] { userid, inRecordSet });

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
		
	}	// end of updStrLocModMobile2
	/**
	 * 특수강 봉강 사외임가공사 이송완료실적 처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updInlnOutStlInfo_JY(GridData gdReq) throws DAOException {
		String methodNm =  "명륜 봉강 사외임가공사 이송완료실적 처리[GdsYsJspFaEJB.updInlnOutStlInfo_JY]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updInlnOutStlInfo_JY", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updInlnOutStlInfo	

	/**
	 * 차량작업관리 작업지연 알림톡전송
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procBayInWoSmsSend(GridData gdReq) throws DAOException {
		String methodNm =  "차량작업관리 작업지연 알림톡전송[GdsYsJspFaEJB.procBayInWoSmsSend]";
		String logId = commUtils.getLogId();
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procBayInWoSmsSend", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updInlnOutStlInfo	
	
	/**
	 * 박판 선재 지번정리
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	
	public GridData reSetLocationW(GridData gdReq) throws DAOException {
		String methodNm =  "박판선재 지번정리[GdsYsJspFaEJB.reSetLocationW]";
		String logId = commUtils.getLogId();
		
		try {

				methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
				commUtils.printLog(logId, methodNm, "F+", gdReq);
				
				
				EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
				
				ejbConn.trx("reSetLocationW", new Class[] { GridData.class }, new Object[] { gdReq });
				
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
			
			
	}	// end of reSetLocationW
	/**
	 * 출하차량 오류 처리
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	
	public GridData resetCarData(GridData gdReq) throws DAOException {
		String methodNm =  "출하차량 오류처리[GdsYsJspFaEJB.resetCarData]";
		String logId = commUtils.getLogId();
		
		try {

				methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
				commUtils.printLog(logId, methodNm, "F+", gdReq);
				
				
				EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
				
				ejbConn.trx("resetCarData", new Class[] { GridData.class }, new Object[] { gdReq });
				
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
			
			
	}	// end of resetCarData
	
	
	/***************************************************************************
	 * 인터페이스Test
	 **************************************************************************/
	
	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스Test
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrIfTest(GridData gdReq) throws DAOException {
		String methodNm = "인터페이스Test[GdsYsJspFaEJB.scrIfTest]";
		String logId = commUtils.getLogId();
		
		
		try {
			String trtGp = commUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("DI".equals(trtGp)) {
				methodNm += " 전송Data 저장";
				ejbMethod = "updIfTestData";
			} else if ("DS".equals(trtGp)) {
				methodNm += " 전송";
				ejbMethod = "sndIfTest";
			} else if ("ES".equals(trtGp)) {
				methodNm += " EAI 전송";
				ejbMethod = "sndIfTestEAI";
			} else if ("SL".equals(trtGp)) {
				methodNm += " I/F Layout 조회";
				ejbMethod = "getIfTest";
			} else {         
				methodNm += " I/F List 조회";
				ejbMethod = "getIfTest";
			}
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "GdsYsJspSeEJB", this);
			GridData gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			return gdRtn;  
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
}	