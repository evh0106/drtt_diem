/**    
 * @(#)CondPredJspFaEJBSBean
 *
 * @version		V1.00
 * @author		현대제철
 * @date		2025/02/20
 *
 * @description	열연 결로 예측(Condensation Prediction) 시스템 Jsp Facade EJB
 * 
 * -------------------------------------------------------------------------------
 * Ver.		수정일자	요청자	수정자	내용
 * =======	==========	======	======	==========================================
 * V1.00	2025/02/20	정종균	양태호	최초 등록
 * 
 */
package com.inisteel.cim.yf.condpred.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yf.common.YfCommUtils;
import com.inisteel.cim.yf.common.YfConstant;
import com.inisteel.cim.yf.common.YfQueryIF;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

/**
 * [A] 클래스명 : 열연 결로 예측(Condensation Prediction) 시스템 Jsp Facade EJB
 *
 * @ejb.bean name="CondPredJspFaEJB" jndi-name="CondPredJspFaEJB" type="Stateless" view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
 */
public class CondPredJspFaEJBSBean extends BaseSessionBean implements YfQueryIF {
	private YfCommUtils commUtils = new YfCommUtils();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {

	}

	/**
	 * GridData - 단순 조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getSelectData(GridData gdReq) throws DAOException {
		String methodNm = "조회[CondPredJspFaEJB.getSelectData(GridData)]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);

		try {
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")_"
					+ commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "CondPredJspSeEJB", this);
			GridData gdRet = (GridData) ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			// 조회결과
			return gdRet;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 단순 조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSelectData(JDTORecord recPara) throws DAOException {
		String methodNm = "조회[CondPredJspFaEJB.getSelectData(JDTORecord)]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		;

		try {
			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) + "(" + commUtils.trim(recPara.getFieldString("jsp_page_id")) + ")_"
					+ commUtils.trim(recPara.getFieldString("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", recPara);

			EJBConnector ejbConn = new EJBConnector("default", "CondPredJspSeEJB", this);
			JDTORecordSet recordSet = (JDTORecordSet) ejbConn.trx("getSelectData", new Class[] { JDTORecord.class }, new Object[] { recPara });

			commUtils.printLog(logId, methodNm, "F-");

			// 조회결과
			return recordSet;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 그리드의 선택된 행에 대해서 단순 업데이틀 수행
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */

	public GridData updateGridData(GridData gdReq) throws DAOException {
		String methodNm = "업데이트[CondPredJspFaEJB.updateGridData(GridData)]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);

		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")_"
					+ commUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "CondPredJspSeEJB", this);
			ejbConn.trx("updateGridData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}

	/**
	 * YF-RULE 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData modiYfRule(GridData gdReq) throws DAOException {
		String methodNm = "YF-RULE 수정[CondPredJspFaEJB.modiYfRule]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);

		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "CondPredJspSeEJB", this);

			JDTORecord jrRtn = (JDTORecord) ejbConn.trx("modiYfRule", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			commUtils.printLog(logId, methodNm, "F-");

			// 조회결과
			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}

	/**
	 * GridData - 야드기준 Data조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getTableData(GridData gdReq) throws DAOException {
		String methodNm = "야드기준 Data조회[CondPredJspFaEJB.getSelectData(GridData)]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		;

		try {
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")_"
					+ commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "CondPredJspSeEJB", this);
			GridData gdRet = (GridData) ejbConn.trx("getTableData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			// 조회결과
			return gdRet;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * GridData - 야드기준 Data 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updTableData(GridData gdReq) throws DAOException {
		String methodNm = "야드기준 Data 수정[CondPredJspFaEJB.updTableData(GridData)]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		;

		try {
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")_"
					+ commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "CondPredJspSeEJB", this);
			ejbConn.trx("updTableData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			// 조회결과
			return gdRet;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * GridData - 야드기준 Data 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData executeQuery(GridData gdReq) throws DAOException {
		String methodNm = "야드기준 Data 수정[CondPredJspFaEJB.executeQuery(GridData)]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		;

		try {
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")_"
					+ commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "CondPredJspSeEJB", this);
			ejbConn.trx("executeQuery", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			// 조회결과
			return gdRet;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * 기준관리 - 세부항목수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updYfRule(GridData gdReq) throws DAOException {
		String methodNm = "기준관리 - 수정[CondPredJspFaEJB.updYfRule]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);

		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "CondPredJspSeEJB", this);
			ejbConn.trx("updYfRule", new Class[] { GridData.class }, new Object[] { gdReq });

			// 조회
			GridData gdRet = (GridData) ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			// 조회결과
			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updYfRule

	/**
	 * [A] 오퍼레이션명 : 열연야드_THM_공장내외온습도정보요청(YDX1L002, YDX2L002, YDX3L002)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData sndYDX1L002(GridData gdReq) throws DAOException {
		String methodNm = "열연야드_THM_공장내외온습도정보요청[CondPredJspFaEJB.sndYDX1L002]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);

		try {
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			String YD_GP = commUtils.trim(gdReq.getParam("YD_GP"));
			String MEA_DH = commUtils.trim(gdReq.getParam("MEA_DH"));

			JDTORecord jrParam = JDTORecordFactory.getInstance().create(); // Query 실행시 파라메터 전달용 JDTORecord
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name
			if (YD_GP.equals("1")) {
				jrParam.setField("MSG_ID", "YDX1L002");
			} else if (YD_GP.equals("3")) {
				jrParam.setField("MSG_ID", "YDX2L002");
			} else if (YD_GP.equals("J")) {
				jrParam.setField("MSG_ID", "YDX3L002");
			}
			jrParam.setField("MSG_GP", "I");
			jrParam.setField("YD_GP", YD_GP);
			jrParam.setField("MEA_DH", MEA_DH);

			EJBConnector ejbCon = new EJBConnector("default", "CondPredJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbCon.trx("sndYDX1L002", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
}
