/**    
 * @(#)EbtYsJspFaEJBSBean
 *
 * @author : 현대제철
 * @date : 2025/06/30
 * @description : 대형 봉강 옥외 야드 Jsp Facade EJB
 * @history
 *   - 2025-06-30 : 특수강 대형야드 신예화 프로젝트 초기 버전 작성 (양태호)
 */
package com.inisteel.cim.ys.ebt.session;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.ys.common.session.YsComm;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsConstant;
import com.inisteel.cim.ys.common.util.YsQueryIF;
import com.inisteel.cim.ys.ebt.dao.EbtYsDAO;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

/**
 * [A] 클래스명 : 대형 봉강 옥외 야드 Jsp Facade EJB
 *
 * @ejb.bean name="EbtYsJspFaEJB" jndi-name="EbtYsJspFaEJB" type="Stateless" view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
 */
public class EbtYsJspFaEJBBean extends BaseSessionBean implements YsQueryIF {
	private static final long serialVersionUID = 1L;

	private YsCommUtils commUtils = new YsCommUtils();
	private YsComm ysComm = new YsComm();
	private EbtYsDAO ebtYsDAO = new EbtYsDAO();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	/**
	 * [A] 오퍼레이션명 : 작업예약등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updbtMvStkWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "작업예약등록[EbtYsJspFaEJB.updbtMvStkWrkBook(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updbtMvStkWrkBook", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			// 조회결과
			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : Flex에 쿼리를 실행시키기 위한 메소드
	 * 
	 * @ejb.interface-method
	 * @param HashMap
	 * @return List
	 * @throws DAOException
	 */
	public List getListWithFlex(HashMap paramMap) throws DAOException {
		String methodNm = "조회[EbtYsJspFaEJB.getListWithFlex(HashMap)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		paramMap.put("logId", logId);

		try {
			String pageId = commUtils.trim((paramMap.get("page_id") != null) ? paramMap.get("page_id").toString() : "");
			String pageNm = commUtils.trim((paramMap.get("page_nm") != null) ? paramMap.get("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			commUtils.printLog(logId, methodNm, "F+", commUtils.hashMapToGridData(paramMap));

			String pageNo = (paramMap.get("PAGE_NO") != null) ? paramMap.get("PAGE_NO").toString() : "";
			String pageRowCount = (paramMap.get("PAGE_ROW_COUNT") != null) ? paramMap.get("PAGE_ROW_COUNT").toString() : "";

			if (!"".equals(pageNo) && !"".equals(pageRowCount)) {
				ArrayList paramList = (ArrayList) paramMap.get("paramList");
				paramList.add(pageNo);
				paramList.add(pageRowCount);
			}

			// commUtils.printParam("paramMap1", paramMap);

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			List rtnList = (List) ejbConn.trx("getListWithFlex", new Class[] { HashMap.class }, new Object[] { paramMap });

			commUtils.printLog(logId, methodNm, "F-");
			return rtnList;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage());
		}
	}

	/**
	 * [A] 오퍼레이션명 : Flex에 멀티쿼리를 실행시키기 위한 메소드
	 * 
	 * @ejb.interface-method
	 * @param HashMap
	 * @return List
	 * @throws DAOException
	 */
	public List getMultiListWithFlex(HashMap paramMap) throws DAOException {
		String methodNm = "조회[EbtYsJspFaEJB.getMultiListWithFlex(HashMap)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		paramMap.put("logId", logId);

		try {
			String pageId = commUtils.trim((paramMap.get("page_id") != null) ? paramMap.get("page_id").toString() : "");
			String pageNm = commUtils.trim((paramMap.get("page_nm") != null) ? paramMap.get("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			commUtils.printLog(logId, methodNm, "F+", commUtils.hashMapToGridData(paramMap));

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			List rtnList = (List) ejbConn.trx("getMultiListWithFlex", new Class[] { HashMap.class }, new Object[] { paramMap });

			commUtils.printLog(logId, methodNm, "F-");
			return rtnList;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage());
		}
	}
	
	/**
	 * GridData - 단순 조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData,
	 * @throws DAOException
	 */
	public GridData getSelectData(GridData gdReq) throws DAOException {
		String methodNm = "조회[EbtYsJspFaEJB.getSelectData]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")_"
					+ commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
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
		String methodNm = "조회[EbtYsJspFaEJB.getSelectData]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		recPara.setResultCode(logId);

		try {

			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) + "(" + commUtils.trim(recPara.getFieldString("jsp_page_id")) + ")_"
					+ commUtils.trim(recPara.getFieldString("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", recPara);

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecordSet recordSet = (JDTORecordSet) ejbConn.trx("getSelectData", new Class[] { JDTORecord.class }, new Object[] { recPara });

			commUtils.printLog(logId, methodNm, "F-");

			// 조회결과
			return recordSet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} // end of getSelectData

	/**
	 * 스케줄기준관리 - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSchRule(GridData gdReq) throws DAOException {
		String methodNm = "스케줄기준관리 - 수정[EbtYsJspFaEJB.updSchRule]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			commUtils.printLog(logId, "YD_SCH_GP:" + gdReq.getParam("YD_SCH_GP").toString(), "");

			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updSchRule", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} // end of updSchRule

	/**
	 * 스케줄기준관리 - 선택복구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData resetSchRule(GridData gdReq) throws DAOException {
		String methodNm = "스케줄기준관리 - 선택복구[EbtYsJspFaEJB.resetSchRule]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);

			// 스케줄기준관리 - 선택복구
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("resetSchRule", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} // end of resetSchRule

	/**
	 * 스케줄기준관리 - 전체복구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData resetAllSchRule(GridData gdReq) throws DAOException {
		String methodNm = "스케줄기준관리 - 전체복구[EbtYsJspFaEJB.resetAllSchRule]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);

			// 스케줄기준관리 - 전체복구
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("resetAllSchRule", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of resetAllSchRule

	/**
	 * 크레인작업예약관리 - 스케줄기동
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procCrnWrkBookStart(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업예약관리 - 스케줄기동[EbtYsJspFaEJB.procCrnWrkBookStart]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("procCrnWrkBookStart", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);
				String ruleItem = ysComm.getYsRuleItem(logId, methodNm, "APPGI1", "*");

				if (!ruleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				} else {
					doRcvInterface(jrRst);
				}
			}

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
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
		String methodNm = "크레인작업예약관리 - 작업예약삭제[EbtYsJspFaEJB.delWrkBook]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord sndRecord = (JDTORecord) ejbConn.trx("delWrkBook", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (sndRecord != null) {
				sndRecord.setResultCode(logId);
				sndRecord.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { sndRecord });
			}

			commUtils.printLog(logId, methodNm, "F-");

			// 조회결과
			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} // end of delWrkBook

	/**
	 * parseSendData
	 * 
	 * @param sndData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public void doRcvInterface(JDTORecord sndData) throws DAOException, JDTOException {
		String methodNm = "[EbtYsDAO.doRcvInterface]";
		commUtils.printParam(methodNm, sndData);

		try {
			JDTORecordSet sendData = (JDTORecordSet) sndData.getField("SEND_DATA");

			JDTORecord jrMsg = JDTORecordFactory.getInstance().create();
			String msgId = "";

			if (sendData == null) {
				return;
			}

			for (int i = 0; i < sendData.size(); i++) {
				jrMsg = sendData.getRecord(i);
				msgId = commUtils.getMsgId(jrMsg);

				commUtils.printLog("methodNm", msgId, "");
				if (msgId != null && msgId.length() >= 4) {
					if ("YS".equals(msgId.substring(2, 4))) {
						EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
						sndConn.trx("rcvInterface", new Class[] { JDTORecord.class }, new Object[] { jrMsg });
					}
				}
			}

		} catch (DAOException e) {
			throw e;
		} catch (RemoteException e) {
			throw new DAOException(e);
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : 크레인상태설정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updbtCrnStsSetPp(GridData gdReq) throws DAOException {
		String methodNm = "크레인상태설정[EbtYsJspFaEJB.updbtCrnStsSetPp]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updbtCrnStsSetPp", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String ruleItem = ysComm.getYsRuleItem(logId, methodNm, "APPGI1", "*");

				if (!ruleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				} else {
					doRcvInterface(jrRst);
				}
			}

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			// 조회결과
			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리-크레인변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCraneChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리-크레인변경[EbtYsJspFaEJB.updCraneChange]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			// 크레인변경
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updCraneChange", new Class[] { GridData.class }, new Object[] { gdReq });
			
			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String ruleItem = ysComm.getYsRuleItem(logId, methodNm, "APPGI1", "*");

				if (!ruleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				} else {
					doRcvInterface(jrRst);
				}
			}

			commUtils.printLog(logId, methodNm, "F-");

			// 조회결과
			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} // end of updCraneChange
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리-순위변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPriorChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리-순위변경[EbtYsJspFaEJB.updPriorChange]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			// 크레인변경
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updPriorChange", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String ruleItem = ysComm.getYsRuleItem(logId, methodNm, "APPGI1", "*");

				if (!ruleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				} else {
					doRcvInterface(jrRst);
				}
			}

			commUtils.printLog(logId, methodNm, "F-");

			// 조회결과
			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} // end of updPriorChange

	/**
	 * [A] 오퍼레이션명 : 크레인작업관리-긴급작업
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPriorWrkChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리-긴급작업[EbtYsJspFaEJB.updPriorWrkChange]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);

			// 크레인변경
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updPriorWrkChange", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String ruleItem = ysComm.getYsRuleItem(logId, methodNm, "APPGI1", "*");

				if (!ruleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				} else {
					doRcvInterface(jrRst);
				}
			}

			commUtils.printLog(logId, methodNm, "F-");
			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updPriorChange
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리-권하위치변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updDownLocChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리-권하위치변경[EbtYsJspFaEJB.updDownLocChange]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);

			// 크레인변경
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updDownLocChange", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String ruleItem = ysComm.getYsRuleItem(logId, methodNm, "APPGI1", "*");

				if (!ruleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				} else {
					doRcvInterface(jrRst);
				}
			}

			commUtils.printLog(logId, methodNm, "F-");

			// 조회결과
			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} // end of updDownLocChange
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리-작업취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCraneWrkCancel(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업관리 - 작업취소 [EbtYsJspFaEJB.updCraneWrkCancel]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);
		
		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));
			
			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCraneWrkCancel", new Class[] { GridData.class }, new Object[] { gdReq });
	
			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String ruleItem = ysComm.getYsRuleItem(logId, methodNm, "APPGI1", "*");

				if (!ruleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				} else {
					doRcvInterface(jrRst);
				}
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCraneWrkCancel
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리 - 스케줄취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCraneSchCancel(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 - 스케줄취소 [EbtYsJspFaEJB.updCraneSchCancel]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);

			// 크레인변경
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updCraneSchCancel", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String ruleItem = ysComm.getYsRuleItem(logId, methodNm, "APPGI1", "*");

				if (!ruleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
//"스케줄취소"일 경우 "TB_YS_Z_IF > YSN7L203 > PGM_NM1, PGM_NM2" 데이터가 없어서 Exception 발생함
//"스케줄취소"일 경우 doRcvInterface(jrRst) 실행 안해도 되는지 확인 필요함 
//				else {
//					doRcvInterface(jrRst);
//				}
			}

			commUtils.printLog(logId, methodNm, "F-");

			// 조회결과
			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} // end of updCraneSchCancel
	
	/**
	 * 재료 저장위치 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStrLocMod(GridData gdReq) throws DAOException {
		String methodNm = "재료 저장위치수정 [EbtYsJspFaEJB.updStrLocMod]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);

			// 변경전 저장품등록 및 정합성 체크
			ejbConn.trx("insBtYsStock", new Class[] { GridData.class }, new Object[] { gdReq });

			// 재료저장위치 수정
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updStrLocMod", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String ruleItem = ysComm.getYsRuleItem(logId, methodNm, "APPGI1", "*");

				if (!ruleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				} else {
					doRcvInterface(jrRst);
				}
			}
			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} // end of updStrLocMod
	
	/**
	 * 재료 지정 등록/해제
	 * @ejb.interface-method
	 * @param GridData
	 * @throws DAOException
	 */
	public GridData updStockAgsnReg(GridData gdReq) throws DAOException {
		String methodNm = "재료 지정 등록/해제[EbtYsJspFaEJB.updStockAgsnReg(GridData)]";
		String logId = commUtils.getLogId();
		
		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			ejbConn.trx("updStockAgsnReg", new Class[] { GridData.class }, new Object[] { gdReq });
			
			return gdReq;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog("", methodNm, e));
		}
		
	}	// end of updStockAgsnReg
	
	/**
	 * 차량상차정보조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getgdsCarldInfoInqjl(GridData gdReq) throws DAOException {
		String methodNm = "차량상차정보조회 [EbtYsJspFaEJB.getgdsCarldInfoInqjl]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			inRecord.setResultCode(logId);

			EJBConnector ejbConn = new EJBConnector("default", this);
			JDTORecordSet recordSet = (JDTORecordSet) ejbConn.trx("EbtYsJspSeEJB", "getgdsCarldInfoInqjl", inRecord);

			if (recordSet != null && recordSet.size() != 0) {
				gdRtn = commUtils.jdtoRecordToGridData(gdRtn, recordSet, gdReq);
			} else {
				gdRtn = gdReq;
			}

			commUtils.printLog(logId, methodNm, "F-");

			// 조회결과
			return gdRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of getgdsCarldInfoInqjl
	
	/**
	 * 차상위치수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updgdsCarldInfoInqjl(GridData gdReq) throws DAOException {
		String methodNm = "차량상차정보조회 - 차상위치수정 [EbtYsJspFaEJB.updgdsCarldInfoInqjl]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updgdsCarldInfoInqjl", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String ruleItem = ysComm.getYsRuleItem(logId, methodNm, "APPGI1", "*");

				if (!ruleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				} else {
					doRcvInterface(jrRst);
				}
			}

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updgdsCarldInfoInqjl
	
	/**
	 * 차량작업관리 > 차량Point작업현황 - 입고동결정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws JDTOException
	 */
	public GridData procPntSelect(GridData gdReq) throws JDTOException {
		String methodNm =  "차량작업 포인트 현황 - 포인트 개폐[EbtYsJspFaEJB.procPntSelect]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);
		
		try{
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			ejbConn.trx("updAutoSupRule", new Class[] { GridData.class }, new Object[] { gdReq });
		
			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of procPntSelect
	
	/**
	 * 차량작업관리 > 차량Point작업현황 - 차량초기화
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public Integer initMvCarSchMgt(JDTORecord[] gdReq) throws DAOException {
		String methodNm =  "차량작업관리 > 차량Point작업현황 - 차량초기화[EtbYsJspFaEJB.initMvCarSchMgt]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		
		try{
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("initMvCarSchMgt", new Class[] { JDTORecord[].class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			return 0;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 차량작업관리 > 차량Point작업현황 - 포인트 개폐
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public Integer procPntUnit(JDTORecord[] gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "차량작업 포인트 현황 - 포인트 개폐[EbtYsJspFaEJB.procPntUnit]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		
		try{
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procPntUnit", new Class[] { JDTORecord[].class }, new Object[] { gdReq });
			
			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			return 0;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 차량작업관리 > 차량Point작업현황- 입동차량순서 변경처리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public Integer procGdsBayInWoSeqChang(JDTORecord[] gdReq) throws JDTOException {
		String methodNm =  "차량작업 관리- 입동순서 변경처리[EbtYsJspFaEJB.procGdsBayInWoSeqChang]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
//			setIPAddress(logId);
		
		try{
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procGdsBayInWoSeqChang", new Class[] { JDTORecord[].class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			return 0;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 차량작업관리 > 차량Point작업현황 - 입동지시
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public Integer procBayInWo(JDTORecord[] gdReq) throws JDTOException {
		String methodNm =  "차량작업관리 - 입동지시[EbtYsJspFaEJB.procBayInWo]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		
		try{
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procBayInWo", new Class[] { JDTORecord[].class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			return 0;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 차량작업관리 > 배차내역 -차량회송처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData doDelCarSch(GridData gdReq) throws JDTOException {
		String methodNm =  "차량작업관리 > 배차내역-차량회송처리[EbtYsJspFaEJB.doDelCarSch]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);
		
		try{
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("doDelCarSch", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 차량작업관리 > 배차내역 - 차량출발처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public Integer procLeaveCar(JDTORecord[] gdReq) throws JDTOException {
		String methodNm =  "차량작업관리 > 배차내역 - 차량출발처리[EbtYsJspFaEJB.procLeaveCar]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		
		try{
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procLeaveCar", new Class[] { JDTORecord[].class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
	
			commUtils.printLog(logId, methodNm, "F-");

			return 0;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 차량작업관리 > 배차내역 - 하차완료
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return Integer
	 * @throws JDTOException
	 */
	public Integer procCarUd(GridData gdReq) throws JDTOException {
		String methodNm =  "차량작업관리 > 배차내역 - 하차완료[EbtYsJspFaEJB.procCarUd]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		
		try{
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procCarUd", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
	
			commUtils.printLog(logId, methodNm, "F-");

			return 0;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 이송재료 List - 이송LOT등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws DAOException
	 */
	public Integer regFtmvLot(JDTORecord[] gdReq) throws DAOException {
		String methodNm =  "이송LOT등록[EbtYsJspFaEJB.regFtmvLot]";
		String logId = commUtils.getLogId();
		
		try {
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			ejbConn.trx("regFtmvLot", new Class[] { JDTORecord[].class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");
			
			return 0;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	// end of regFtmvLot
	
	/**
	 * 저장위치좌표설정 - 열정보 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStrLocPosSetCol(GridData gdReq) throws DAOException {
		String methodNm = "저장위치좌표설정 - 열정보 변경 [EbtYsJspFaEJB.updStrLocPosSetCol]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			// 대차초기화
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updStrLocPosSetCol", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String ruleItem = ysComm.getYsRuleItem(logId, methodNm, "APPGI1", "*");

				if (!ruleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				} else {
					doRcvInterface(jrRst);
				}
			}

			commUtils.printLog(logId, methodNm, "F-");

			// 조회결과
			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updStrLocPosSetCol
	
	/**
	 * 저장위치좌표설정 - BED정보 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStrLocPosSetBed(GridData gdReq) throws DAOException {
		String methodNm = "저장위치좌표설정 - Bed정보 변경 [EbtYsJspFaEJB.updStrLocPosSetBed]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);

			// 대차초기화
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updStrLocPosSetBed", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String ruleItem = ysComm.getYsRuleItem(logId, methodNm, "APPGI1", "*");

				if (!ruleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				} else {
					doRcvInterface(jrRst);
				}
			}

			commUtils.printLog(logId, methodNm, "F-");

			// 조회결과
			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} // end of updStrLocPosSetBed
	
	/**
	 * 기준관리  - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws DAOException
	 */
	public Integer updYsRuleSrchGdBt(JDTORecord[] gdReq) throws DAOException {
		String methodNm =  "기준관리 - 검색가이드 수정[EbtYsJspFaEJB.updYsRuleSrchGdBt]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		
		try {
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			ejbConn.trx("updYsRuleSrchGdBt", new Class[] { JDTORecord[].class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return 0;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updYsRuleSrchGdBt
	
	/**
	 * [A] 오퍼레이션명 : 이적작업팝업-작업예약등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData callupdbtMvStkWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "이적작업팝업-작업예약등록[EbtYsJspFaEJB.callupdbtMvStkWrkBook(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("callupdbtMvStkWrkBook", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			// 조회결과
			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of callupdbtMvStkWrkBook
	
	/**
	 * 저장위치별현황 - 차량입고LOT등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regCarFtmvLot(GridData gdReq) throws DAOException {
		String methodNm =  "차량입고LOT등록[EbtYsJspFaEJB.regCarFtmvLot]";
		String logId = commUtils.getLogId();
		
		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("regCarFtmvLot", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");
			
			return gdReq;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	// end of regFtmvLot
	
	/**
	 * [A] 오퍼레이션명 : 저장위치별현황 - 차량입고LOT등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData callregCarFtmvLot(GridData gdReq) throws DAOException {
		String methodNm = "이적작업팝업-작업예약등록[EbtYsJspFaEJB.callregCarFtmvLot(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("callregCarFtmvLot", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			// 조회결과
			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of callregCarFtmvLot
	
	/**
	 * 보급Lot List - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws DAOException
	 */
	public Integer updPrepSchLot(JDTORecord[] gdReq) throws DAOException {
		String methodNm =  "준비스케줄 - 수정[EbtYsJspFaEJB.updPrepSchLot]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		
		try {

			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			ejbConn.trx("updPrepSchLot", new Class[] { JDTORecord[].class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return 0;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updPrepSchLot
	
	/**
	 * 보급Lot List - 삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public Integer delPrepSchLot(JDTORecord[] gdReq) throws DAOException {
		String methodNm =  "준비스케줄 - 삭제[EbtYsJspFaEJB.delFrToMoveLot]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		
		try {

			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			
			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			ejbConn.trx("delPrepSchLot", new Class[] { JDTORecord[].class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return 0;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of delPrepSchLot
	
	/**
	 * 보급Lot List - 재료삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws DAOException
	 */
	public Integer delPrepMtl(JDTORecord[] gdReq) throws DAOException {
		String methodNm =  "준비스케줄 - 재료삭제[EbtYsJspFaEJB.delPrepMtl]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		
		try {
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			ejbConn.trx("delPrepMtl", new Class[] { JDTORecord[].class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return 0;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of delPrepMtl
	
	/**
	 * 상차완료백업처리-구내운송차량출발
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqTsStart(GridData gdReq) throws DAOException {
		String methodNm = "상차완료백업처리-구내운송차량출발 [EbtYsJspFaEJB.reqTsStart]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			// 인터페이스 담당자 확인
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name
			jrParam.setField("JMS_TC_CD", "TSYSJ004");
			jrParam.setField("TRN_EQP_CD", gdReq.getParam("TRN_EQP_CD"));
			jrParam.setField("SPOS_WLOC_CD", "");
			jrParam.setField("SPOS_YD_PNT_CD", "");
			jrParam.setField("ARR_WLOC_CD", gdReq.getParam("SPOS_WLOC_CD"));
			jrParam.setField("ARR_YD_PNT_CD", "");
			jrParam.setField("TRN_WRK_FULLVOID_GP", "E");
			jrParam.setField("YD_WO_CNCL_YN", "N");
			jrParam.setField("L3_HMI", "Y"); // 백업화면 기동 여부

			EJBConnector ejbConn = new EJBConnector("default", "YsCommCarTSMvSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("rcvTSYSJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String ruleItem = ysComm.getYsRuleItem(logId, methodNm, "APPGI1", "*");

				if (!ruleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				} else {
					doRcvInterface(jrRst);
				}
			}

			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of reqTsStart

	/**
	 * 상차완료백업처리팝업-등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtMvCarStatSet2(GridData gdReq) throws DAOException {
		String methodNm = "상차완료백업처리팝업-등록 [EbtYsJspFaEJB.trtMvCarStatSet2]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("trtMvCarStatSet2", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String ruleItem = ysComm.getYsRuleItem(logId, methodNm, "APPGI1", "*");

				if (!ruleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				} else {
					doRcvInterface(jrRst);
				}
			}

			commUtils.printLog(logId, methodNm, "F-");

			// 조회결과
			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} // end of trtMvCarStatSet2

	/**
	 * 상차완료백업처리팝업-이송작업재료등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm = "상차완료백업처리팝업-이송작업재료등록 [EbtYsJspFaEJB.updCarFtMvMtl]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);

			ejbConn.trx("updCarFtMvMtl", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			// 조회결과
			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} // end of updCarFtMvMtl

	/**
	 * 상차완료백업처리팝업-이송작업재료삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm = "상차완료백업처리팝업-이송작업재료삭제 [EbtYsJspFaEJB.delCarFtMvMtl]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);

			ejbConn.trx("delCarFtMvMtl", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of delCarFtMvMtl

	/**
	 * 상차완료백업처리팝업-이송작업재료위치변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData chgCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm = "상차완료백업처리팝업-이송작업재료위치변경 [EbtYsJspFaEJB.chgCarFtMvMtl]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);

			ejbConn.trx("chgCarFtMvMtl", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} // end of chgCarFtMvMtl

	/**
	 * 하차백업생성팝업-등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData mkUdCarSch(GridData gdReq) throws DAOException {
		String methodNm = "하차백업생성팝업-등록 [EbtYsJspFaEJB.mkUdCarSch]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("mkUdCarSch", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String ruleItem = ysComm.getYsRuleItem(logId, methodNm, "APPGI1", "*");

				if (!ruleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				} else {
					doRcvInterface(jrRst);
				}
			}

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of initMvCarSchMgt
	
	/**
	 * [A] 오퍼레이션명 : 야드맵정보 불일치 확인 - L2정보요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData insStrLocMtlIn(GridData gdReq) throws DAOException {
		String methodNm = "야드맵정보 불일치 확인 - L2정보요구[EbtYsJspFaEJB.insStrLocMtlIn(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			ejbConn.trx("insStrLocMtlIn", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			// 조회결과
			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 보급Lot List - 작업예약등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws DAOException
	 */
	public Integer insTrfLotWrkBook(JDTORecord[] gdReq) throws DAOException {
		String methodNm =  "준비스케줄 - 재료삭제[SbtYsJspFaEJB.insTrfLotWrkBook]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		
		try {
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			ejbConn.trx("insTrfLotWrkBook", new Class[] { JDTORecord[].class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return 0;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of insTrfLotWrkBook
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업매수관리 - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCrnWrkCntMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업매수관리 - 수정[EbtYsJspFaEJB.updCrnWrkCntMgt]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			ejbConn.trx("updCrnWrkCntMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-", gdReq);

			// 조회결과
			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updCrnWrkCntMgt
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업매수관리 - 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delCrnWrkCntMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업매수관리 - 삭제[SbtYsJspFaEJB.delCrnWrkCntMgt]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			ejbConn.trx("delCrnWrkCntMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-", gdReq);

			// 조회결과
			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of delCrnWrkCntMgt
	
	/**
	 * [A] 오퍼레이션명 : 차량 - 자동상차완료 여부
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updLdcCmplYn(GridData gdReq) throws DAOException {
		String methodNm = "차량 - 자동상차완료 여부[EbtYsJspFaEJB.updLdcCmplYn(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			ejbConn.trx("updLdcCmplYn", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			// 조회결과
			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updLdcCmplYn

	/**
	 * [A] 오퍼레이션명 : 대형옥외야드 -> 사외통합야드(명륜) 차량이송소재 데이터 백업&정리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updClearData_MRSteel(GridData gdReq) throws DAOException {
		String methodNm = "대형옥외야드 -> 사외통합야드(명륜) 차량이송소재 데이터 백업&정리[EbtYsJspFaEJB.updClearData_MRSteel(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);
		
		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			ejbConn.trx("updClearData_MRSteel", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			// 조회결과
			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updLdcCmplYn
	
	/**
	 * [A] 오퍼레이션명 : 하차예정저장위치 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updGffPlnStrLoc(GridData gdReq) throws DAOException {
		String methodNm = "하차예정저장위치 등록[EbtYsJspFaEJB.updGffPlnStrLoc(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			ejbConn.trx("updGffPlnStrLoc", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			// 조회결과
			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updLdcCmplYn
	
	/**
	 * 대형봉강옥외야드 PDA 저장위치등록 bulk
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param listData
	 * @return GridData
	 * @throws DAOException
	 */
	public JDTORecord updStrLocRegBulk(HashMap mapList) throws DAOException {
		String methodNm = "저장위치등록 [EbtYsJspFaEJB.updStrLocRegBulk(HashMap)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			List mapList2 = (List)mapList.get("mapList");

			String sYD_GP = (String)mapList.get("YD_GP");
			String userId = (String)mapList.get("userid");
			
			GridData gdReq = new GridData(); 
			
			commUtils.printLog(logId, "sYD_GP:" + sYD_GP + "userId:" + userId, "");
			
			for (int i = 0; i < mapList2.size(); i++) {
				Map item = (Map) mapList2.get(i);
				
				Iterator keyIter = item.keySet().iterator();
				
				String sSSTL_NO = "";
				String sLOCATION = "";

				while (keyIter.hasNext()) {
					Object key = keyIter.next();
					Object value = item.get(key);
					
					String sKey = key.toString();
					String sValue = value.toString();
					commUtils.printLog(logId, sKey + "," + sValue , "");
					
					if ("sstlNo".equals(sKey)) {
						sSSTL_NO = sValue;
						continue;
					} else if ("localtion".equals(sKey)) {
						sLOCATION = sValue;
						continue;
					}
				}
				
				commUtils.printLog(logId, "sSSTL_NO:" + sSSTL_NO + "sLOCATION:" + sLOCATION, "");
			
				String sYS_STK_COL_GP = sLOCATION.substring(0, 6);
				String sYS_STK_BED_NO = sLOCATION.substring(6, 8);
				String sYS_STK_LYR_NO = sLOCATION.substring(8, 10);
				String sYS_STK_SEQ_NO = sLOCATION.substring(10, 11);

				String sOLD_SSTL_NO = "";
				String sOLD_YS_STK_COL_GP = "";
				String sOLD_YS_STK_BED_NO = "";
				String sOLD_YS_STK_LYR_NO = "";
				String sOLD_YS_STK_SEQ_NO = "";
				String sFROM_SSTL_NO = "";
				String sFROM_YS_STK_COL_GP = "";
				String sFROM_YS_STK_BED_NO = "";
				String sFROM_YS_STK_LYR_NO = "";
				String sFROM_YS_STK_SEQ_NO = "";
	
				GridData gridParam = new GridData();
				gridParam.addParam("YD_GP", sYD_GP);
				gridParam.addParam("SSTL_NO", sSSTL_NO);
				gridParam.addParam("YS_STK_COL_GP", sYS_STK_COL_GP);
				gridParam.addParam("YS_STK_BED_NO", sYS_STK_BED_NO);
				gridParam.addParam("YS_STK_LYR_NO", sYS_STK_LYR_NO);
				gridParam.addParam("YS_STK_SEQ_NO", sYS_STK_SEQ_NO);
				gridParam.addParam("OLD_SSTL_NO", sOLD_SSTL_NO);
				gridParam.addParam("OLD_YS_STK_COL_GP", sOLD_YS_STK_COL_GP);
				gridParam.addParam("OLD_YS_STK_BED_NO", sOLD_YS_STK_BED_NO);
				gridParam.addParam("OLD_YS_STK_LYR_NO", sOLD_YS_STK_LYR_NO);
				gridParam.addParam("OLD_YS_STK_SEQ_NO", sOLD_YS_STK_SEQ_NO);
				gridParam.addParam("FROM_SSTL_NO", sFROM_SSTL_NO);
				gridParam.addParam("FROM_YS_STK_COL_GP", sFROM_YS_STK_COL_GP);
				gridParam.addParam("FROM_YS_STK_BED_NO", sFROM_YS_STK_BED_NO);
				gridParam.addParam("FROM_YS_STK_LYR_NO", sFROM_YS_STK_LYR_NO);
				gridParam.addParam("FROM_YS_STK_SEQ_NO", sFROM_YS_STK_SEQ_NO);
	
				gridParam.addParam("userid", userId);	// 유저 아이디
				gridParam.setIPAddress(logId);
			
				commUtils.printParam("gridParam", commUtils.gridDataTojdtoRecord(gridParam));
				
				EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
				JDTORecord jrRst = (JDTORecord) ejbConn.trx("updStrLocReg", new Class[] { GridData.class }, new Object[] { gridParam });
				
				// 전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);
	
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
			}

			commUtils.printLog(logId, methodNm, "F-", gdReq);
			
			JDTORecord gdRet = commUtils.hashMapTojdtoRecord(mapList);

			// 조회결과
			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	// 2026.03.27 이송소재상차확인 -->
	/**
	 * PDA 상차재료취소처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public JDTORecord LdCancelProcPDA(HashMap mapList) throws DAOException {
		String methodNm = "PDA 상차재료취소처리 [EbtYsJspFaEJB.LdCancelProcPDA]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);

		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			List mapList2 = (List)mapList.get("mapList");

			String userId = (String)mapList.get("userid");
			String sYD_GP = (String)mapList.get("YD_GP");
			
			commUtils.printLog(logId, "sYD_GP:" + sYD_GP + ", userId:" + userId, "");
			
			GridData gridParam = new GridData();
			gridParam.setIPAddress(logId);
			gridParam.createHeader("CHECK", OperateGridData.t_checkbox);
			gridParam.createHeader("SSTL_NO", "T");
			gridParam.createHeader("MATCHING", "T");
			gridParam.createHeader("CANCEL_YN", "T");
			gridParam.createHeader("YD_CAR_SCH_ID", "T");
			gridParam.createHeader("YD_CAR_UPP_LOC_CD", "T");
			gridParam.addParam("userid", userId);	// 유저 아이디
			gridParam.addParam("YD_GP", sYD_GP);
			
			if (mapList2 != null) {
				for (int i = 0; i < mapList2.size(); i++) {
					Map item = (Map) mapList2.get(i);
					
					String sstlNo = (String) item.get("SSTL_NO");
					String matching = (String) item.get("MATCHING");
					String cancelYn = (String) item.get("CANCEL_YN");
					String ydCarSchId = (String) item.get("YD_CAR_SCH_ID");
					
					if (sstlNo.length() == 0) {
						continue;
					}
					
					gridParam.getHeader("CHECK").addValue("1", "");
					gridParam.getHeader("SSTL_NO").addValue(sstlNo, "");
					gridParam.getHeader("MATCHING").addValue(matching, "");
					gridParam.getHeader("CANCEL_YN").addValue(cancelYn, "");
					gridParam.getHeader("YD_CAR_SCH_ID").addValue(ydCarSchId, "");
				}
			}

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("LdCancelProcPDA", new Class[] { GridData.class }, new Object[] { gridParam });
			
			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String ruleItem = ysComm.getYsRuleItem(logId, methodNm, "APPGI0", "*");

				if (!ruleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				} else {
					commUtils.printParam("jrRst", jrRst);
				}
			}
			
			JDTORecord gdRet = commUtils.hashMapTojdtoRecord(mapList);

			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of LdCancelProcPDA
	
	/**
	 * PDA 상차확인처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public JDTORecord LdConfirmProcPDA(HashMap mapList) throws DAOException {
		String methodNm = "PDA 상차확인처리 [EbtYsJspFaEJB.LdConfirmProcPDA]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_F);

		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			List mapList2 = (List)mapList.get("mapList");

			String userId = (String)mapList.get("userid");
			String sYD_GP = (String)mapList.get("YD_GP");
			
			commUtils.printLog(logId, "sYD_GP:" + sYD_GP + ", userId:" + userId, "");
			
			GridData gridParam = new GridData();
			gridParam.setIPAddress(logId);
			gridParam.createHeader("CHECK", OperateGridData.t_checkbox);
			gridParam.createHeader("SSTL_NO", "T");
			gridParam.createHeader("MATCHING", "T");
			gridParam.createHeader("CANCEL_YN", "T");
			gridParam.createHeader("YD_CAR_SCH_ID", "T");
			gridParam.createHeader("YD_CAR_UPP_LOC_CD", "T");
			gridParam.addParam("userid", userId);	// 유저 아이디
			gridParam.addParam("YD_GP", sYD_GP);
			
			if (mapList2 != null) {
				for (int i = 0; i < mapList2.size(); i++) {
					Map item = (Map) mapList2.get(i);
					
					String sstlNo = (String) item.get("SSTL_NO");
					String matching = (String) item.get("MATCHING");
					String cancelYn = (String) item.get("CANCEL_YN");
					String ydCarSchId = (String) item.get("YD_CAR_SCH_ID");
					
					if (sstlNo.length() == 0) {
						continue;
					}
					
					gridParam.getHeader("CHECK").addValue("1", "");
					gridParam.getHeader("SSTL_NO").addValue(sstlNo, "");
					gridParam.getHeader("MATCHING").addValue(matching, "");
					gridParam.getHeader("CANCEL_YN").addValue(cancelYn, "");
					gridParam.getHeader("YD_CAR_SCH_ID").addValue(ydCarSchId, "");
				}
			}

			EJBConnector ejbConn = new EJBConnector("default", "EbtYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("LdConfirmProcPDA", new Class[] { GridData.class }, new Object[] { gridParam });
			
			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String ruleItem = ysComm.getYsRuleItem(logId, methodNm, "APPGI0", "*");

				if (!ruleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				} else {
					commUtils.printParam("jrRst", jrRst);
				}
			}
			
			JDTORecord gdRet = commUtils.hashMapTojdtoRecord(mapList);

			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of LdConfirmProcPDA
	// 2026.03.27 이송소재상차확인 <--
}
