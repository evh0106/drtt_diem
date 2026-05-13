/**
 * @(#)BSlabJspFaEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      B열연 SLAB 야드 화면관리 Facade EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.ym.bslab.session;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bcommon.util.YmConstant;

/**
 *      [A] 클래스명 : B열연 SLAB 야드 화면관리 Facade EJB
 *
 * @ejb.bean name="BSlabJspFaEJB" jndi-name="BSlabJspFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class BSlabJspFaEJBSBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YmCommUtils commUtils = new YmCommUtils();
	private YmCommDAO commDao = new YmCommDAO();

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
		String methodNm =  "조회[BSlabJspFaEJB.getSelectData]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
			                    + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
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
		String methodNm =  "조회[BSlabJspFaEJB.getSelectData]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) 
			                    + "("   + commUtils.trim(recPara.getFieldString("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(recPara.getFieldString("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", recPara);

			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
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
	 * IFTest Layout 변경 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updIfTestData(GridData gdReq) throws DAOException {
		String methodNm =  "IFTest Layout 변경[BSlabJspFaEJB.updIfTestData]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
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
		String methodNm =  "IFTest 전송[BSlabJspFaEJB.sndIfTest]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
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
		String methodNm =  "IFTest EAI전송[BSlabJspFaEJB.sndIfTestEAI]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
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
	 * B열연 SLAB 위치별적치현황조회 - LAYER활성상태 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updLayerActStat(GridData gdReq) throws DAOException {
		String methodNm =  "B열연 SLAB 위치별적치현황조회 - LAYER활성상태 변경[BSlabJspFaEJB.updLayerActStat]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			ejbConn.trx("updLayerActStat", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updLayerActStat		
	
	/**
	 * 차량예정정보 전송
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regCarUdExplainInfo(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "차량예정정보 전송[BSlabJspFaEJB.regCarUdExplainInfo]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("regCarUdExplainInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			gdRet.setMessage(StringHelper.evl(jrRst.getFieldString("RTN_MSG"), ""));
			
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
	}	//end of regCarUdExplainInfo
	
	
	
	/**
	 * SLAB 재열재 조회 - 작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reHtWrkDmd(GridData gdReq) throws DAOException {
		String methodNm =  "작업요구[BSlabJspFaEJB.reHtWrkDmd]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {
	
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//SLAB 재열재 조회 - 작업요구
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("reHtWrkDmd", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
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
		
	}	// end of reHtWrkDmd		
	
	
	
	/**
	 * SLAB 재열재 조회 - 구분변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updReheatSlabGp(GridData gdReq) throws DAOException {
		String methodNm =  "구분변경[BSlabJspFaEJB.updReheatSlabGp]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {
	
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//SLAB 재열재 조회 - 구분변경
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updReheatSlabGp", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
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
		
	}	// end of updReheatSlabGp		
	
	
	
	/**
	 * 야드설비정비등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData insEqpTrblReg(GridData gdReq) throws DAOException {
		String methodNm =  "야드설비정비등록[BSlabJspFaEJB.insEqpTrblReg]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//야드설비정비상태 변경
			ejbConn.trx("updEqpTrblReg", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//야드설비정비등록
			GridData gdRet = (GridData)ejbConn.trx("insEqpTrblReg", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of insEqpTrblReg
		
	/**
	 *      [A] 오퍼레이션명 : 크레인SCH 기동 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData procCrnWrkBookStart(GridData gdReq) throws DAOException {
		String methodNm = "크레인상태설정[BSlabJspSeEJB.procCrnWrkBookStart]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procCrnWrkBookStart", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

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
	 * 크레인작업예약관리 - 작업예약삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delWrkBook(GridData gdReq) throws DAOException {
		String methodNm =  "작업예약삭제[BSlabJspSeEJB.delWrkBook]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		JDTORecord sndRecord = null;
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			sndRecord = (JDTORecord)ejbConn.trx("delWrkBook", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (sndRecord != null) {
				sndRecord.setResultCode(logId);
				sndRecord.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { sndRecord });
			}
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
	 * 크레인스케줄 기준 변경 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSchRuleMgt(GridData gdReq) throws DAOException {
		String methodNm =  "크레인스케줄 기준 변경[BSlabJspFaEJB.updSchRuleMgt]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//IFTest Layout 변경
			ejbConn.trx("updSchRuleMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	//end of updSchRuleMgt
	/**
	 * 야드및설비  열 정보수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSlabYdStkPosSet(GridData gdReq) throws DAOException {
		String methodNm =  "야드및설비 열정보수정[BSlabJspFaEJB.updSlabYdStkPosSet]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
					            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
					            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updSlabYdStkPosSet", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updSlabYdStkPosSet	
	/**
	 * 야드및설비 베드정보수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSlabYdStkPosSetBed(GridData gdReq) throws DAOException {
		String methodNm =  "야드및설비 베드정보수정[BSlabJspFaEJB.updSlabYdStkPosSetBed]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
					            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
					            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updSlabYdStkPosSetBed", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updSlabYdStkPosSetBed
	/**
	 * 설비상태 (변경 설비기준조회 )
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updEqpOprnStat(GridData gdReq) throws DAOException {
		String methodNm =  "설비상태 변경[BSlabJspFaEJB.updEqpOprnStat]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//설비상태 변경
			ejbConn.trx("updEqpOprnStat", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
	 * 저장영역별검색순서조회 - 저장 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStrAreaSrchSeq(GridData gdReq) throws DAOException {
		String methodNm =  "저장영역별검색순서조회 - 저장[BSlabJspFaEJB.updStrAreaSrchSeq]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//저장영역별검색순서 저장
			ejbConn.trx("updStrAreaSrchSeq", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	//end of updStrAreaSrchSeq
	
	/**
	 * 기준관리 - 세부항목수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updYmRule(GridData gdReq) throws DAOException {
		String methodNm =  "기준관리 - 수정[BSlabJspFaEJB.updYmRule]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			ejbConn.trx("updYmRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updYmRule
	
	/**
	 * 적치기준 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updateStackRuleInfo(GridData gdReq) throws DAOException {
		String methodNm =  "폭,외경기준 변경[BSlabJspFaEJB.updateStackRuleInfo]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
			                    + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("updateStackRuleInfo", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updateStackRuleInfo	
	
	/**
	 * 목적동순위  변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updateSlabMoveBayRank(GridData gdReq) throws DAOException {
		String methodNm =  "목적동순위  변경[BSlabJspFaEJB.updateSlabMoveBayRank]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//목적동순위  변경
			ejbConn.trx("updateSlabMoveBayRank", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updateSlabMoveBayRank
	
	
	/**
	 *      [A] 오퍼레이션명 : 이적작업 예약 등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updSlabMvStkWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "이적작업 예약 등록[BSlabJspFaEJB.updblMvStkWrkBook]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updSlabMvStkWrkBook", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
		
	} // updSlabMvStkWrkBook			
	
	
	/**
	 * SLAB  STACK_LAYER_ACTIVE_STAT  활성 비할성을 Toggle 처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updActiveStat(GridData gdReq) throws DAOException {
		String methodNm =  "SLAB Walking Beam 조회[BSlabJspFaEJB.updActiveStat]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//IFTest Layout 변경
			ejbConn.trx("updActiveStat", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	//end of updActiveStat		
	
	/**
	 * B열연 SLAB 설비 스케줄사용여부 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updEquipUseSch(GridData gdReq) throws DAOException {
		String methodNm =  "B열연 SLAB 설비 스케줄사용여부 변경[BSlabJspFaEJB.updEquipUseSch]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			ejbConn.trx("updEquipUseSch", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updEquipUseSch	
	
	/**
	 * B열연 SLAB 선작업지시사용여부 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updEquipBefWork(GridData gdReq) throws DAOException {
		String methodNm =  "B열연 SLAB 선작업지시사용여부 변경[BSlabJspFaEJB.updEquipBefWork]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			ejbConn.trx("updEquipBefWork", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updEquipBefWork
	
	/**
	 * W/B 1 Pitch 이동 (장입요구)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData callWB1PitchMove(GridData gdReq) throws DAOException {
		String methodNm =  "B열연 SLAB W/B 1 Pitch 이동 (장입요구)[BSlabJspFaEJB.callWB1PitchMove]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
			
			
			jrParam.setField("YD_SCH_CD", "2CWB01UM");
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "C동WB보급 스케줄기준 조회");
			if(rsResult.size() > 0) {
				jrParam.setField("YD_WRK_PLAN_CRN", rsResult.getRecord(0).getFieldString("YD_WRK_CRN"));
			} else {
				jrParam.setField("YD_WRK_PLAN_CRN", gdReq.getParam("CRANE_NO"));
			}
			
			jrParam.setField("JMS_TC_CD", "A8YML020" );
			//jrParam.setField("YD_WRK_PLAN_CRN", gdReq.getParam("CRANE_NO"));
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
			jrRst = (JDTORecord)ejbConn.trx("rcvA8YML020", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
		
	}	// end of callWB1PitchMove		
	
	/**
	 * #4 CTC Loading 실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData callCtc4LoadResult(GridData gdReq) throws DAOException {
		String methodNm =  "B열연 SLAB #4 CTC Loading 실적[BSlabJspFaEJB.runCtc4LoadResult]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
			String     sSTOCK_ID = "";
			
			jrParam.setField("STACK_COL_GP", "2CWB01");
			jrParam.setField("STACK_BED_GP", "05");
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getMaxLayerStockId", logId, methodNm, "WB 05Bed 에 적치된 STOCK 검색 "); 
			if(rsResult.size() > 0) {
				sSTOCK_ID = rsResult.getRecord(0).getFieldString("STOCK_ID");
			}
			
			jrParam.setField("JMS_TC_CD", "CF1PB16" );
			jrParam.setField("SLAB_NO"	, sSTOCK_ID );
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
			jrRst = (JDTORecord)ejbConn.trx("rcvCF1PB16", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
		
	}	// end of runCtc4LoadResult		
	
	
	/**
	 * #1, 2 CTC Loading Request => A동 CTC 보급요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData callCtcALoadRequest(GridData gdReq) throws DAOException {
		String methodNm =  "B열연 SLAB #1, 2, 4 CTC Loading Request[BSlabJspFaEJB.runCtcALoadRequest]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
	
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
			
			
			jrParam.setField("YD_SCH_CD", "2ACT01UM");
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "C동WB보급 스케줄기준 조회");
			if(rsResult.size() > 0) {
				jrParam.setField("YD_WRK_PLAN_CRN", rsResult.getRecord(0).getFieldString("YD_WRK_CRN"));
			} else {
				jrParam.setField("YD_WRK_PLAN_CRN", gdReq.getParam("CRANE_NO"));
			}
			
			jrParam.setField("JMS_TC_CD", "A8YML019" );
			jrParam.setField("POSITION", gdReq.getParam("POSITION"));
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
			jrRst = (JDTORecord)ejbConn.trx("rcvA8YML019", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
	} // end of callCtcALoadRequest
	
	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리-크레인변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCraneChange(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업요구현황조회-크레인변경[BSlabJspFaEJB.updCraneChange]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCraneChange", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCraneChange	
	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업요구현황조회-순위변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPriorChange(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업요구현황조회-순위변경[BSlabJspFaEJB.updPriorChange]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updPriorChange", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updPriorChange	
	
	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업요구현황조회-긴급작업
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPriorWrkChange(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업요구현황조회-긴급작업[BSlabJspFaEJB.updPriorWrkChange]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updPriorWrkChange", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updPriorChange		
	
	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업요구현황조회-권하위치변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updDownLocChange(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업요구현황조회-권하위치변경[BSlabJspFaEJB.updDownLocChange]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updDownLocChange", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updDownLocChange	
	
	
	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업요구현황조회-작업취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCraneWrkCancel(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업요구현황조회-작업취소[BSlabJspFaEJB.updCraneWrkCancel]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCraneWrkCancel", new Class[] { GridData.class }, new Object[] { gdReq });
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

	
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCraneWrkCancel		
	
	
	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업요구현황조회-스케줄취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCraneSchCancel(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업요구현황조회-스케줄취소[BSlabJspFaEJB.updCraneSchCancel]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCraneSchCancel", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCraneSchCancel		
	
	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업요구현황조회-스케줄재전송
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reSndCrnSch(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업요구현황조회-스케줄재전송[BSlabJspFaEJB.reSndCrnSch]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("reSndCrnSch", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of reSndCrnSch		
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업요구현황조회 - 권상권하처리 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updbtCrnStsSetPp(GridData gdReq) throws DAOException {
		String methodNm = "크레인상태설정[BSlabJspFaEJB.updbtCrnStsSetPp]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updbtCrnStsSetPp", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
	
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
		
			commUtils.printLog(logId, methodNm, "F-", gdReq);

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	/** 2017년 9월 5일 화요일 CTC보급요구 기능(스케줄 사용유무) - SCP
	 * B열연 SLAB A, C동 CTC보급요구 스케줄사용여부 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updEquipUseSchAC(GridData gdReq) throws DAOException {
		String methodNm =  "A, C동 CTC보급요구스케줄사용여부 변경[BSlabJspFaEJB.updEquipUseSchAC]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			ejbConn.trx("updEquipUseSchAC", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	
	
	
	
	/** 2017년 9월 5일 화요일 CTC보급요구 기능(선작업지시 유무) - SCP
	 * B열연 SLAB A, C동 CTC보급요구 선작업지시사용여부 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updEquipBefWorkAC(GridData gdReq) throws DAOException {
		String methodNm =  "A, C동 CTC보급요구 선작업지시사용여부 변경[BSlabJspFaEJB.updEquipBefWorkAC]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			ejbConn.trx("updEquipBefWorkAC", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
	 * 산적위치수정 - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStkLoc(GridData gdReq) throws DAOException {
		String methodNm =  "산적위치수정-수정[BSlabJspFaEJB.updStkLoc]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//산적위치수정 - 수정
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updStkLoc", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updStkLoc	
	

	/**
	 * 산적위치수정 - 삭제수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delStkLoc(GridData gdReq) throws DAOException {
		String methodNm =  "산적위치수정-수정[BSlabJspFaEJB.delStkLoc]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {
	
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//산적위치수정 - 삭제
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("delStkLoc", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
	
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
		
	}	// end of delStkLoc	
	
	
	/**
	 * 산적위치수정 - 전문백업
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStkLocBackUp(GridData gdReq) throws DAOException {
		String methodNm =  "산적위치수정-수정[BSlabJspFaEJB.updStkLoc]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//산적위치수정 - 전문백업
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updStkLocBackUp", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updStkLoc		
	
	
	/**
	 * 작업예약현황-TO위치 변경 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updWrkDmdMgt(GridData gdReq) throws DAOException {
		String methodNm =  "크레인스케줄 기준 변경[BSlabJspFaEJB.updSchRuleMgt]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//IFTest Layout 변경
			ejbConn.trx("updWrkDmdMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	//end of updWrkDmdMgt	
	
	/**
	 * [A] 오퍼레이션명 : W/B 보급(wbSupply)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData wbSupply(GridData gdReq) throws DAOException {
		String methodNm =  "W/B 보급(wbSupply)[BSlabJspFaEJB.wbSupply]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//W/B 보급
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("wbSupply", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
		
	}	// end of wbSupply		
	
	/**
	 * [A] 오퍼레이션명 :저장품의 장입순번 CLEAR(modifyZoneInNo)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData modifyZoneInNo(GridData gdReq) throws DAOException {
		String methodNm =  "저장품의 장입순번 CLEAR[BSlabJspFaEJB.modifyZoneInNo]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//W/B 보급
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("modifyZoneInNo", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
		
	}	// end of modifyZoneInNo		
	
	
	/**
	 * 동간작업기준 변경 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updBayBetWrkRule(GridData gdReq) throws DAOException {
		String methodNm =  "동간작업기준 변경[BSlabJspFaEJB.updBayBetWrkRule]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//IFTest Layout 변경
			ejbConn.trx("updBayBetWrkRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	//end of updBayBetWrkRule	
	
	
	/**
	 * 대차스케줄관리 - 대차초기화
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData initTcarSchMgt(GridData gdReq) throws DAOException {
		String methodNm =  "대차스케줄관리 대차초기화[BSlabJspFaEJB.initTcarSchMgt]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//대차초기화
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("initTcarSchMgt", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
	 * 설비상태 (대차스케줄복원 )
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData TcarSchRollBack(GridData gdReq) throws DAOException {
		String methodNm =  "대차스케줄복원[BSlabJspFaEJB.TcarSchRollBack]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//스케줄복원
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("TcarSchRollBack", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
		
	}	// end of updIfTestData		
	
	/**
	 * 대차상태설정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	*/
	public GridData trtTcarStatSet(GridData gdReq) throws DAOException {
		String methodNm = "대차상태설정[BSlabJspFaEJB.trtTcarStatSet]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);

		try { 
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";

			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);

			//대차상태설정
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtTcarStatSet", new Class[] { GridData.class }, new Object[] { gdReq });
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
		
	//이송지시 상차 등록 - SLAB 이송상차 지시
	/**
	 * SLAB 이송상차 지시- 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regCARLD(GridData gdReq) throws DAOException {
		String methodNm =  "SLAB 이송상차 지시- 등록[BSlabJspFaEJB.regCARLD]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {
	
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//SLAB  이송상차 지시 조회 - 작업요구
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("regCARLD", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
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
	}	// end of regCARLD
	
	
	//CTC 지시폭 수정
	/**
	 * CTC 지시폭 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSlabWMgt(GridData gdReq) throws DAOException {
		String methodNm =  "CTC 지시폭 변경[BSlabJspFaEJB.updSlabWMgt]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//IFTest Layout 변경
			ejbConn.trx("updSlabWMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	//end of updSlabWMgt
	
	/**
	 * B열연 SLAB 벤딩표시,해제,보급 설정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updBendingStat(GridData gdReq) throws DAOException {
		String methodNm =  "B열연 SLAB 벤딩표시,해제,보급 설정[BSlabJspFaEJB.updBendingStat]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updBendingStat", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
		
	}	// end of updBendingStat		
	
	/**
	 * 차량포인트 상태 변경(사용가능,사용불가)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCarPointStat(GridData gdReq) throws DAOException {
		String methodNm =  "차량포인트 상태 변경[BSlabJspFaEJB.updCarPointStat]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCarPointStat", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
		
	}	// end of updCarPointStat	
	
	/**
	 * Pallet조회(B) 화면 : 초기화
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData initMvCarSchMgt(GridData gdReq) throws DAOException {
		String methodNm =  "Pallet조회(B) 화면 - 초기화[BSlabJspFaEJB.initMvCarSchMgt]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		 
		try{

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			String modifier  = commUtils.trim(gdReq.getParam("userid")); //수정자
			
			EJBConnector ejbConn 	= null;
			JDTORecord outRecord  	= commUtils.getParam(logId, methodNm, modifier);
			JDTORecord jrRst 		= JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn 		= JDTORecordFactory.getInstance().create();
			
			//int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String sYdCarProgStat; //차량진행상태
			String sYdStkColActStat; //야드적치열활성상태
			String sTRN_EQP_CD = gdReq.getParam("TRN_EQP_CD");

			if(!"".equals(sTRN_EQP_CD)) {
				
				outRecord.setField("TC_CODE"        	, "TSYDJ004"); 
				outRecord.setField("TRN_EQP_CD"			, gdReq.getParam("TRN_EQP_CD"));
				outRecord.setField("TRN_WRK_FULLVOID_GP", "E");
				outRecord.setField("BACKUP_YN"			, "Y");
				sYdCarProgStat = gdReq.getParam("YD_CAR_PROG_STAT");
				if( "1".equals(sYdCarProgStat)||
					"2".equals(sYdCarProgStat)||
					"3".equals(sYdCarProgStat)||
					"4".equals(sYdCarProgStat)||
					"5".equals(sYdCarProgStat)) {
					outRecord.setField("WLOC_CD"		, gdReq.getParam("SPOS_WLOC_CD"));
				} else {
					outRecord.setField("WLOC_CD"		, gdReq.getParam("ARR_WLOC_CD"));
				}
				
				sYdStkColActStat = gdReq.getParam("YD_STK_COL_ACT_STAT");
					
				ejbConn = new EJBConnector("default", "YmCommCarMvSeEJB", this);
				if("R".equals(sYdStkColActStat)) {
					//예약 차량초기화 작업
					jrRtn 	= (JDTORecord)ejbConn.trx("initBookCarSch", new Class[] { JDTORecord.class }, new Object[] { outRecord });
				} else {
					jrRtn 	= (JDTORecord)ejbConn.trx("initCarSch", new Class[] { JDTORecord.class }, new Object[] { outRecord });
				}
				jrRst 	= commUtils.addSndData(jrRst, jrRtn);
					
			} else {
				//운송장비코드가 없는 경우
				outRecord.setField("YD_STK_COL_GP"	, gdReq.getParam("YD_STK_COL_GP"));
				
				ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
				jrRtn 	= (JDTORecord)ejbConn.trx("initCarLoc", new Class[] { JDTORecord.class }, new Object[] { outRecord });
			}
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
	}  //end of initMvCarSchMgt

	/**
	 * 상차완료Backup처리 화면 : 초기화2
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData initMvCarSchMgt2(GridData gdReq) throws DAOException {
		String methodNm =  "이송차량Backup처리 화면 - 초기화2[BSlabJspFaEJB.initMvCarSchMgt2]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		 
		try{

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			String modifier  = commUtils.trim(gdReq.getParam("userid")); //수정자
			
			EJBConnector ejbConn 	= null;
			JDTORecord outRecord  	= commUtils.getParam(logId, methodNm, modifier);
			JDTORecord jrRst 		= JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn 		= JDTORecordFactory.getInstance().create();
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String sYdCarProgStat;

			for (int ii = 0; ii < rowCnt; ii++) {
				
				outRecord.setField("TC_CODE"        	, "TSYDJ004"); 
				outRecord.setField("TRN_EQP_CD"			, commUtils.getValue(gdReq, "TRN_EQP_CD", ii));
				outRecord.setField("TRN_WRK_FULLVOID_GP", "E");
				outRecord.setField("BACKUP_YN"			, "Y");
				sYdCarProgStat = commUtils.getValue(gdReq, "YD_CAR_PROG_STAT", ii);
				if( "1".equals(sYdCarProgStat)||
					"2".equals(sYdCarProgStat)||
					"3".equals(sYdCarProgStat)||
					"4".equals(sYdCarProgStat)||
					"5".equals(sYdCarProgStat)) {
					outRecord.setField("WLOC_CD"		, commUtils.getValue(gdReq, "SPOS_WLOC_CD", ii));
				} else {
					outRecord.setField("WLOC_CD"		, commUtils.getValue(gdReq, "ARR_WLOC_CD", ii));
				}
					
				ejbConn = new EJBConnector("default", "YmCommCarMvSeEJB", this);
				jrRtn 	= (JDTORecord)ejbConn.trx("initCarSch", new Class[] { JDTORecord.class }, new Object[] { outRecord });
				jrRst 	= commUtils.addSndData(jrRst, jrRtn);
					
			}
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
	}  //end of initMvCarSchMgt2
	
	/**
	 * 이송차량 실적처리 팝업 - 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtMvCarStatSet2(GridData gdReq) throws DAOException {
		String methodNm =  "이송차량 실적처리 팝업 - 등록[BSlabJspFaEJB.trtMvCarStatSet2]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtMvCarStatSet2", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
		String methodNm =  "이송작업재료등록[BSlabJspFaEJB.updCarFtMvMtl]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCarFtMvMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			//GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
		String methodNm =  "이송작업재료삭제[BSlabJspFaEJB.delCarFtMvMtl]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
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
		String methodNm =  "이송작업재료위치변경[BSlabJspFaEJB.chgCarFtMvMtl]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
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
	 * 하차백업생성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData mkUdCarSch(GridData gdReq) throws DAOException {
		String methodNm =  "하차백업생성[BSlabJspFaEJB.mkUdCarSch]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		 
		try{

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("mkUdCarSch", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
	}  //end of initMvCarSchMgt
		
	/**
	 * Pallet조회 (C) 목적동변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updDestBay(GridData gdReq) throws DAOException {
		String methodNm =  "Pallet조회 (C) 목적동변경[BSlabJspFaEJB.updDestBay]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			ejbConn.trx("updDestBay", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updLayerActStat	
	
	/**
	 * 구내운송차량출발
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqTsStart(GridData gdReq) throws DAOException {
		String methodNm =  "구내운송차량출발 [BSlabJspFaEJB.reqTsStart]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("JMS_TC_CD"			, "TSYDJ004" );
			jrParam.setField("TRN_EQP_CD"			, gdReq.getParam("TRN_EQP_CD") );
			jrParam.setField("SPOS_WLOC_CD"			, "" );
			jrParam.setField("SPOS_YD_PNT_CD"		, "" );
			jrParam.setField("ARR_WLOC_CD"			, gdReq.getParam("SPOS_WLOC_CD") );
			jrParam.setField("ARR_YD_PNT_CD"		, "" );
			jrParam.setField("TRN_WRK_FULLVOID_GP"	, "E" );
			jrParam.setField("YD_WO_CNCL_YN"		, "N" );
			jrParam.setField("L3_HMI"				, "Y" );   //백업화면 기동 여부
			
			
			EJBConnector ejbConn = new EJBConnector("default", "YmCommCarMvSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("rcvTSYDJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
	 * 야드기준 변경1
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updYmRuleDtlItm1(GridData gdReq) throws DAOException {
		String methodNm =  "야드기준 변경1[BSlabJspFaEJB.updYmRuleDtlItm1]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			ejbConn.trx("updYmRuleDtlItm1", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updYmRuleDtlItm1	
	
	/**
	 * 스케줄 기준 야드멀티작업여부 설정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSchRuleMultiYn(GridData gdReq) throws DAOException {
		String methodNm =  "스케줄 기준 야드멀티작업여부 설정[BSlabJspFaEJB.updSchRuleMultiYn]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			ejbConn.trx("updSchRuleMultiYn", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updSchRuleMultiYn		
	
	/**
	 *      [A] 오퍼레이션명 : Pallet조회 (B) - 스케줄기동2 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData procCrnSchStart2(GridData gdReq) throws DAOException {
		String methodNm = "Pallet조회 (B) - 스케줄기동2 [BSlabJspFaEJB.procCrnSchStart2]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procCrnSchStart2", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String sSCH_CNT = commUtils.nvl(jrRst.getFieldString("SCH_CNT"),"0");
			
			commUtils.printLog(logId, "=======:::: SCH_CNT : " +  sSCH_CNT , "SL");
			
			if(!"0".equals(sSCH_CNT)) {
				
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);
				
				//크레인스케줄기동 전문
				EJBConnector ejbConn2 = new EJBConnector("default", "BSlabSchSeEJB", this);
				JDTORecord jrRst2 = (JDTORecord)ejbConn2.trx("rcvYMYMJ203", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				
				//전송할 Data가 있으면 전송 처리
				if (jrRst2 != null) {
					jrRst2.setResultCode(logId);
					jrRst2.setResultMsg(methodNm);

					EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst2 });
				}
			}
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of procCrnSchStart
	
	/**
	 *      [A] 오퍼레이션명 : Pallet조회 (B) - 스케줄기동 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData procCrnSchStart(GridData gdReq) throws DAOException {
		String methodNm = "Pallet조회 (B) - 스케줄기동 [BSlabJspSeEJB.procCrnSchStart]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procCrnSchStart", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String sYD_WBOOK_ID = jrRst.getFieldString("YD_WBOOK_ID");
			
			commUtils.printLog(logId, "=======:::: YD_WBOOK_ID : " +  sYD_WBOOK_ID , "SL");
			
			if(!"".equals(sYD_WBOOK_ID)) {
				
				//크레인스케줄기동 전문
				EJBConnector ejbConn2 = new EJBConnector("default", "BSlabSchSeEJB", this);
				JDTORecord jrRst2 = (JDTORecord)ejbConn2.trx("procYMYMJ202", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				
				//전송할 Data가 있으면 전송 처리
				if (jrRst2 != null) {
					jrRst2.setResultCode(logId);
					jrRst2.setResultMsg(methodNm);

					EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst2 });
				}
			}
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of procCrnSchStart
	
	/**
	 *      [A] 오퍼레이션명 : Pallet조회 (B) - 하차위치변경 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData procChangeUdLoc(GridData gdReq) throws DAOException {
		String methodNm = "Pallet조회 (B) - 하차위치변경 [BSlabJspSeEJB.procChangeUdLoc]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procChangeUdLoc", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of procChangeUdLoc
	
	/**
	 *      [A] 오퍼레이션명 : Pallet조회 (B) - 상차완료처리 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData runLdCmplProc(GridData gdReq) throws DAOException {
		String methodNm = "Pallet조회 (B) - 상차완료처리 [BSlabJspSeEJB.runLdCmplProc]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("runLdCmplProc", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of runLdCmplProc	

	/**
	 *      [A] 오퍼레이션명 : W/B, CTC - Take Out 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData runTakeOut(GridData gdReq) throws DAOException {
		String methodNm = "W/B, CTC - Take Out [BSlabJspSeEJB.runTakeOut]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("runTakeOut", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of runTakeOut	
	
	/**
	 * [A] 오퍼레이션명 : SCARFING 대상재조회 - 시편재 및 핸드스카핑 보급요구
	 * 작업예약을 생성한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData scarfingWork(GridData gdReq) throws DAOException {
		String methodNm =  "시편재 및 핸드스카핑 보급요구[BSlabJspFaEJB.scarfingWork]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
					            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
					            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("scarfingWork", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of scarfingWork
	
	
	/**
	 * [A] 오퍼레이션명 : SCARFING 대상재조회 - 시편재 및 핸드스카핑 추출요구
	 * 작업예약을 생성한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData scarfingOut(GridData gdReq) throws DAOException {
		String methodNm =  "시편재 및 핸드스카핑 추출요구[BSlabJspFaEJB.scarfingOut]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
					            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
					            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("scarfingOut", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of scarfingOut
	
	/**
	 * [A] 오퍼레이션명 : SCARFING 대상재조회 - 지연사유등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updYd_SlabScarfDelyReg(GridData gdReq) throws DAOException {
		String methodNm =  "지연사유등록[BSlabJspFaEJB.updYd_SlabScarfDelyReg]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
					            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
					            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			ejbConn.trx("updYd_SlabScarfDelyReg", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updYd_SlabScarfDelyReg
	
	/**
	 *      [A] 오퍼레이션명 : STE 비상보급 실행 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData runSteEmgSup(GridData gdReq) throws DAOException {
		String methodNm = "STE 비상보급 실행 [BSlabJspSeEJB.runSteEmgSup]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("runSteEmgSup", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of runTakeOut	

	/**
	 * 대차형상관리여부 설정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updYdTcarFormRule(GridData gdReq) throws DAOException {
		String methodNm =  "대차형상관리여부 설정[BSlabJspFaEJB.updYdTcarFormRule]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try { 

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			ejbConn.trx("updYdTcarFormRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updYdTcarFormRule		
	
	/**
	 * 적치기준 (조회 )
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStockRule(GridData gdReq) throws DAOException {
		String methodNm =  "적치기준[BSlabJspFaEJB.updStockRule]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//적치기준 변경
			ejbConn.trx("updStockRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updStockRule		
	
	
	
	/**
	 * Take Out 장입순번 복구 설정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updYdTakeOutRecvRule(GridData gdReq) throws DAOException {
		String methodNm =  "Take Out 장입순번 복구 여부설정[BSlabJspFaEJB.updYdTakeOutRecvRule]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try { 

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			ejbConn.trx("updYdTakeOutRecvRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updYdTakeOutRecvRule		
	
	/**
	 * 모음작업 처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procGatherWork(GridData gdReq) throws DAOException {
		String methodNm =  "모음작업 처리[BSlabJspFaEJB.procGatherWork]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try { 

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			ejbConn.trx("procGatherWork", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updYdTakeOutRecvRule		
		
	/**
	 * 이상정보정리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 */	
	public GridData delAbnomalInfo(GridData gdReq) throws DAOException {
		String methodNm =  "이상정보정리[BSlabJspFaEJB.delAbnomalInfo]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try { 

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			JDTORecord outRecord = (JDTORecord)ejbConn.trx("delAbnomalInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String sRTN_MSG	= commUtils.nvl(outRecord.getFieldString("RTN_MSG"), "");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			gdRet.setMessage(sRTN_MSG);
			
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			 
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} //end of delAbnomalInfo	
	
	
	//2018년 1월 15일 (월) - Scarfing 입출측 조회 화면 
	//Scarfing M/C상태 변경
	/**
	 * Scarfing M/C상태 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updbSlabMcState(GridData gdReq) throws DAOException {
		String methodNm =  "Scarfing입출측 조회 - Scarfing M/C상태 변경[BSlabJspFaEJB.updbSlabMcState]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//IFTest Layout 변경
			ejbConn.trx("updbSlabMcState", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	//end of updbSlabMcState
	
	//Scarfing입출측 조회 - 보급/추출 크레인 변경
	/**
	 * 보급/추출 크레인 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSlabScarfingSupp(GridData gdReq) throws DAOException {
		String methodNm =  "Scarfing입출측 조회 - 보급/추출 크레인 변경[BSlabJspFaEJB.updSlabScarfingSupp]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//IFTest Layout 변경
			ejbConn.trx("updSlabScarfingSupp", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	//end of updSlabScarfingSupp
		
	
	//SCARFING 실적조회 수정
	/**
	 * SCARFING 실적조회 - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSlabScarfingPattern(GridData gdReq) throws DAOException {
		String methodNm =  "스카핑결과 실적패턴 수정[BSlabJspFaEJB.updSlabScarfingPattern]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {
	
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//IFTest Layout 변경
			ejbConn.trx("updSlabScarfingPattern", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updSlabScarfingPattern	
	
	//SCARFING 근조/본수/중량 입력
	/**
	 * SCARFING 근조/본수/중량 입력
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSlabScarfingQtyWt(GridData gdReq) throws DAOException {
		String methodNm =  "근조/본수/중량 입력[BSlabJspFaEJB.updSlabScarfingQtyWt]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {
	
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//IFTest Layout 변경
			ejbConn.trx("updSlabScarfingQtyWt", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updSlabScarfingPattern
	
	//SCARFING 정지실적등록 수정
	/**
	 * SCARFING 정지실적등록 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSlabScarfingStopWrReg(GridData gdReq) throws DAOException {
		String methodNm =  "SCARFING 정지실적등록 수정[BSlabJspFaEJB.updSlabScarfingStopWrReg]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {
	
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//IFTest Layout 변경
			ejbConn.trx("updSlabScarfingStopWrReg", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updSlabScarfingPattern	
	
	
	
	/**
	 * Holding Bed 조회 - Layer 용도 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updLayerOption(GridData gdReq) throws DAOException {
		String methodNm =  "Layer 용도 변경[BSlabJspFaEJB.updLayerOption]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {
	
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			//Holding Bed 조회 - Layer 용도 변경
			ejbConn.trx("updLayerOption", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updLayerOption	
	
	/**
	 *      [A] 오퍼레이션명 : 구내운송 회송처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData runTsRetHt(GridData gdReq) throws DAOException {
		String methodNm = "구내운송 회송처리[BSlabJspFaEJB.runTsRetHt]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("runTsRetHt", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of runTsRetHt	
	
	
	
	/**
	 * 벤딩처리  : 2019.07.10
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData updStockBendReg(GridData gdReq) throws JDTOException {
		 
		String methodNm = "슬라브 벤딩처리(모바일)[BSlabJspFaEJB.updStockBendReg]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		EJBConnector  ejbConn   = null;
		
		try{
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			return (GridData)ejbConn.trx("updStockBendReg", new Class[] { GridData.class }, new Object[] { gdReq });
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of updStockBendReg
	
	/////
	/**
	 * 마킹처리  : 2019.10.14
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData updStockMarkReg(GridData gdReq) throws JDTOException {
		 
		String methodNm = "슬라브 마킹처리(모바일)[BSlabJspFaEJB.updStockMarkReg]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		EJBConnector  ejbConn   = null;
		
		try{
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			
			return (GridData)ejbConn.trx("updStockMarkReg", new Class[] { GridData.class }, new Object[] { gdReq });
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of updStockMarkReg
	
	/**
	 * SCARP 이동지시 전송
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData sendSlabScrapMove(GridData gdReq) throws DAOException {
		String methodNm =  "SCARP 이동지시 전송 - 등록[BSlabJspFaEJB.sendSlabScrapMove]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("sendSlabScrapMove", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
		
	}	// end of sendSlabScrapMove		
	/////
	
	/**
	 * E동 Turn 작업 시작
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData doTurnStart(GridData gdReq) throws DAOException {
		String methodNm =  "E동 Turn 작업 시작[BSlabJspFaEJB.doTurnStart]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("doTurnStart", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
		
	}	// end of doTurnStart		

	/**
	 * E동 Turn 작업 종료
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData doTurnEnd(GridData gdReq) throws DAOException {
		String methodNm =  "E동 Turn 작업 종료[BSlabJspFaEJB.doTurnEnd]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("doTurnEnd", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
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
		
	}	// end of doTurnStart		
	
	/**
	 * WB이상정보정리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 */	
	public GridData updWBChargeLotInfo(GridData gdReq) throws DAOException {
		String methodNm =  "WB이상정보정리[BSlabJspFaEJB.updWBChargeLotInfo]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try { 

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			JDTORecord outRecord = (JDTORecord)ejbConn.trx("updWBChargeLotInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String sRTN_MSG	= commUtils.nvl(outRecord.getFieldString("RTN_MSG"), "");
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			gdRet.setMessage(sRTN_MSG);
			
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			 
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} //end of delAbnomalInfo	
	
	/**
	 * 야드차량사용TYPE 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 */	
	public GridData updCarUseTypeGp(GridData gdReq) throws DAOException {
		String methodNm =  "야드차량사용TYPE 변경[BSlabJspFaEJB.updCarUseTypeGp]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try { 

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			ejbConn.trx("updCarUseTypeGp", new Class[] { GridData.class }, new Object[] { gdReq });
			
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

	} //end of updCarUseTypeGp	
	
	/**
	 * 기준관리 - 생산통제 압연지시 메세지 DEL_YN 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCtMsgRule(GridData gdReq) throws DAOException {
		String methodNm =  "기준관리 - 생산통제 압연지시 메세지 DEL_YN 수정[BSlabJspFaEJB.updCtMsgRule]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_2);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			ejbConn.trx("updCtMsgRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updYmRule
	
}
