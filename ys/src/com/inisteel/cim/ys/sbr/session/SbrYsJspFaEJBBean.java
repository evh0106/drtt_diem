/**
 * @(#)SbrYsJspFaEJBBean
 *
 * @version          V1.00
 * @author           김현규
 * @date             2026/01/07
 *
 * @description      특수강소형야드 화면관리 Facade EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2026/01/07                         김현규      최초 등록
 */
package com.inisteel.cim.ys.sbr.session;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.ys.common.session.YsComm;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsConstant;
import com.metis.rapi4j.ResultData;
import com.metis.rapi5j.RAPI5J;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

/**
 * [A] 클래스명 : 대형 압연 옥내 야드 화면관리 Facade EJB
 *
 * @ejb.bean name="SbrYsJspFaEJB" jndi-name="SbrYsJspFaEJB" type="Stateless" view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
 */
public class SbrYsJspFaEJBBean extends BaseSessionBean {
	private static final long serialVersionUID = 1L;
	
	private YsCommUtils commUtils = new YsCommUtils();
	private YsComm ysComm = new YsComm();

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
		String methodNm = "작업예약등록[SbrYsJspFaEJB.updbtMvStkWrkBook(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
		String methodNm = "조회[SbrYsJspFaEJB.getListWithFlex(HashMap)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
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

			commUtils.printParam("paramMap", paramMap);

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
		String methodNm = "조회[SbrYsJspFaEJB.getMultiListWithFlex(HashMap)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		paramMap.put("logId", logId);
		
		try {
			String pageId = commUtils.trim((paramMap.get("page_id") != null) ? paramMap.get("page_id").toString() : "");
			String pageNm = commUtils.trim((paramMap.get("page_nm") != null) ? paramMap.get("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+", commUtils.hashMapToGridData(paramMap));
			
			EJBConnector ejbConn = new EJBConnector("default","SbrYsJspSeEJB", this);
			List rtnList = (List)ejbConn.trx("getMultiListWithFlex", new Class[] {HashMap.class}, new Object[] {paramMap});
			
			commUtils.printLog(logId, methodNm, "F-");
			return rtnList;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage());
		}
	}

	/**
	 * 스케줄기준관리 - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSchRule(GridData gdReq) throws DAOException {
		String methodNm = "스케줄기준관리 - 수정[SbrYsJspFaEJB.updSchRule]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			commUtils.printLog(logId, "YD_SCH_GP:" + gdReq.getParam("YD_SCH_GP").toString(), "");

			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
		String methodNm = "스케줄기준관리 - 선택복구[SbrYsJspFaEJB.resetSchRule]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);

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
		String methodNm = "스케줄기준관리 - 전체복구[SbrYsJspFaEJB.resetAllSchRule]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);

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
		String methodNm = "크레인작업예약관리 - 스케줄기동[SbrYsJspFaEJB.procCrnWrkBookStart]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("procCrnWrkBookStart", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);
				String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "001");
				if (sRuleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				} else {
					doRcvInterface(jrRst);
				}
			} else {
				String sYD_TO_LOC_GUIDE = "";
				for(int i = 0; i < gdReq.getHeader("CHECK").getRowCount(); i++) {
					if("1".equals(commUtils.getValue(gdReq, "CHECK", i))) {
						sYD_TO_LOC_GUIDE = gdReq.getHeader("YD_TO_LOC_GUIDE").getValue(i);
						
						if(sYD_TO_LOC_GUIDE.substring(2, 2) == "TC") {
							gdRet.addParam("errorMsg", "해당 대차["+gdReq.getParam("YD_TO_LOC_GUIDE")+"]는 고장입니다.");
						}
					}
				}
			}

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			return gdRet;

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
		String methodNm = "크레인작업예약관리 - 작업예약삭제[SbrYsJspFaEJB.delWrkBook]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
		String methodNm = "[SbrYsDAO.doRcvInterface]";
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

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("rcvInterface", new Class[] { JDTORecord.class }, new Object[] { jrMsg });
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
		String methodNm = "크레인상태설정[SbrYsJspFaEJB.updbtCrnStsSetPp]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updbtCrnStsSetPp", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "001");
				if (sRuleItem.equals("Y")) {
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
		String methodNm = "크레인작업관리-크레인변경[SbrYsJspFaEJB.updCraneChange]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			// 크레인변경
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updCraneChange", new Class[] { GridData.class }, new Object[] { gdReq });
			
			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "001");
				if (sRuleItem.equals("Y")) {
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
		String methodNm = "크레인작업관리-순위변경[SbrYsJspFaEJB.updPriorChange]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			// 크레인변경
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updPriorChange", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "001");
				if (sRuleItem.equals("Y")) {
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
		String methodNm = "크레인작업관리-긴급작업[SbrYsJspFaEJB.updPriorWrkChange]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);

			// 크레인변경
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updPriorWrkChange", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "001");
				if (sRuleItem.equals("Y")) {
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
		String methodNm = "크레인작업관리-권하위치변경[SbrYsJspFaEJB.updDownLocChange]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);

			// 크레인변경
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updDownLocChange", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "001");
				if (sRuleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
//"스케줄취소"일 경우 "TB_YS_Z_IF > YSN7L203 > PGM_NM1, PGM_NM2" 데이터가 없어서 Exception 발생함
//"스케줄취소"일 경우 doRcvInterface(jrRst) 실행 안해도 되는지 확인 필요함 
//				else if (ruleItem.equals("Y")) {
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
		String methodNm =  "크레인작업관리 - 작업취소 [SbrYsJspFaEJB.updCraneWrkCancel]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);
		
		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));
			
			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCraneWrkCancel", new Class[] { GridData.class }, new Object[] { gdReq });
	
			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "001");
				if (sRuleItem.equals("Y")) {
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
		String methodNm = "크레인작업관리 - 스케줄취소 [SbrYsJspFaEJB.updCraneSchCancel]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);

			// 크레인변경
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updCraneSchCancel", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "001");
				if (sRuleItem.equals("Y")) {
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
//"스케줄취소"일 경우 "TB_YS_Z_IF > YSN7L203 > PGM_NM1, PGM_NM2" 데이터가 없어서 Exception 발생함
//"스케줄취소"일 경우 doRcvInterface(jrRst) 실행 안해도 되는지 확인 필요함 
//				else if (ruleItem.equals("Y")) {
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
		String methodNm = "재료 저장위치수정 [SbrYsJspFaEJB.updStrLocMod]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);

			// 변경전 저장품등록 및 정합성 체크
			ejbConn.trx("insBtYsStock", new Class[] { GridData.class }, new Object[] { gdReq });

			// 재료저장위치 수정
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updStrLocMod", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "001");
				if (sRuleItem.equals("Y")) {
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
	 * 저장위치별현황 - 차량입고LOT등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regCarFtmvLot(GridData gdReq) throws DAOException {
		String methodNm =  "차량입고LOT등록[SbrYsJspFaEJB.regCarFtmvLot]";
		String logId = commUtils.getLogId();
		
		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
	 * 저장위치별현황 - 재료 지정 등록/해제
	 * @ejb.interface-method
	 * @param GridData
	 * @throws DAOException
	 */
	public GridData updStockAgsnReg(GridData gdReq) throws DAOException {
		String methodNm = "재료 지정 등록/해제[SbtYsJspFaEJB.updStockAgsnReg(GridData)]";
		String logId = commUtils.getLogId();
		
		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			ejbConn.trx("updStockAgsnReg", new Class[] { GridData.class }, new Object[] { gdReq });
			
			return gdReq;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog("", methodNm, e));
		}
		
	}	// end of updStockAgsnReg
	
	/**
	 * 이송재료 List - 이송LOT등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws DAOException
	 */
	public Integer regFtmvLot(JDTORecord[] gdReq) throws DAOException {
		String methodNm =  "이송LOT등록[SbrYsJspFaEJB.regFtmvLot]";
		String logId = commUtils.getLogId();
		
		try {
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
	 * 차량작업관리 > 차량Point작업현황 - 차량초기화
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public Integer initMvCarSchMgt(JDTORecord[] gdReq) throws DAOException {
		String methodNm =  "차량작업관리 > 차량Point작업현황 - 차량초기화[CtbYsJspFaEJB.initMvCarSchMgt]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		
		try{
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
	 * 차량작업관리 > 차량Point작업현황 - 입동지시
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public Integer procBayInWo(JDTORecord[] gdReq) throws JDTOException {
		String methodNm =  "차량작업관리 - 입동지시[SbrYsJspFaEJB.procBayInWo]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		
		try{
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
	 * 차량작업관리 > 차량Point작업현황- 포인트 개폐
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public Integer procPntUnit(JDTORecord[] gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "차량작업 포인트 현황 - 포인트 개폐[SbrYsJspFaEJB.procPntUnit]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		
		try{
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
		String methodNm =  "차량작업 관리- 입동순서 변경처리[SbrYsJspFaEJB.procGdsBayInWoSeqChang]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
//			setIPAddress(logId);
		
		try{
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
	 * 차량상차정보조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getgdsCarldInfoInqjl(GridData gdReq) throws DAOException {
		String methodNm = "차량상차정보조회 [SbrYsJspFaEJB.getgdsCarldInfoInqjl]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
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
			JDTORecordSet recordSet = (JDTORecordSet) ejbConn.trx("SbrYsJspSeEJB", "getgdsCarldInfoInqjl", inRecord);

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
		String methodNm = "차량상차정보조회 - 차상위치수정 [SbrYsJspFaEJB.updgdsCarldInfoInqjl]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updgdsCarldInfoInqjl", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "001");
				if (sRuleItem.equals("Y")) {
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
	 * 차량작업관리 > 배차내역 -차량회송처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData doDelCarSch(GridData gdReq) throws JDTOException {
		String methodNm =  "차량작업관리 > 배차내역-차량회송처리[SbrYsJspFaEJB.doDelCarSch]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);
		
		try{
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BtYsJspSeEJB", this);
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
		String methodNm =  "차량작업관리 > 배차내역 - 차량출발처리[SbrYsJspFaEJB.procLeaveCar]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		
		try{
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
	 * 차량작업관리 >  - 차량출발처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return Integer
	 * @throws JDTOException
	 */
	public GridData procCarUd(GridData gdReq) throws JDTOException {
		String methodNm =  "차량작업관리 > 배차내역 - 차량출발처리[SbrYsJspFaEJB.procLeaveCar]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		
		try{
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procCarUd", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
	 * 저장위치좌표설정 - 열정보 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStrLocPosSetCol(GridData gdReq) throws DAOException {
		String methodNm = "저장위치좌표설정 - 열정보 변경 [SbrYsJspFaEJB.updStrLocPosSetCol]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			// 대차초기화
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updStrLocPosSetCol", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "001");
				if (sRuleItem.equals("Y")) {
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
		String methodNm = "저장위치좌표설정 - Bed정보 변경 [SbrYsJspFaEJB.updStrLocPosSetBed]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);

			// 대차초기화
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updStrLocPosSetBed", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "001");
				if (sRuleItem.equals("Y")) {
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
	 * 대형압연재예정저장위치 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regLMillPlnStrLoc(GridData gdReq) throws DAOException {
		String methodNm = "대형압연재예정저장위치 등록 [SbrYsJspFaEJB.regLMillPlnStrLoc]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			ejbConn.trx("regLMillPlnStrLoc", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of regLMillPlnStrLoc
	
	/**
	 * 대형압연재예정저장위치 일괄등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regBulkLMillPlnStrLoc(GridData gdReq) throws DAOException {
		String methodNm = "대형압연재예정저장위치 일괄등록 [SbrYsJspFaEJB.regBulkLMillPlnStrLoc]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			ejbConn.trx("regBulkLMillPlnStrLoc", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of regLMillPlnStrLoc
	
	/**
	 * 기준관리  - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws DAOException
	 */
	public Integer updYsRuleSrchGdBt(JDTORecord[] gdReq) throws DAOException {
		String methodNm =  "기준관리 - 검색가이드 수정[SbrYsJspFaEJB.updYsRuleSrchGdBt]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		
		try {
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
		String methodNm = "이적작업팝업-작업예약등록[SbrYsJspFaEJB.callupdbtMvStkWrkBook(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("callupdbtMvStkWrkBook", new Class[] { GridData.class }, new Object[] { gdReq });
			
			if (jrRst.getField("ErrorMsg") != null && !jrRst.getField("ErrorMsg").toString().equals("")) {
				gdRet.addParam("errorMsg", jrRst.getField("ErrorMsg").toString());
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
	 * [A] 오퍼레이션명 : 저장위치별현황 - 차량입고LOT등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData callregCarFtmvLot(GridData gdReq) throws DAOException {
		String methodNm = "이적작업팝업-작업예약등록[SbrYsJspFaEJB.callregCarFtmvLot(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
	}
	
	/**
	 * 상차완료백업처리-구내운송차량출발
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqTsStart(GridData gdReq) throws DAOException {
		String methodNm = "상차완료백업처리-구내운송차량출발 [SbrYsJspFaEJB.reqTsStart]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
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
			jrParam.setField("WLOC_CD", "DMY1P");
			jrParam.setField("YD_PNT_CD", "1Z99");
			jrParam.setField("SPOS_WLOC_CD", "DMY1P");
			jrParam.setField("SPOS_YD_PNT_CD", "1Z99");
			jrParam.setField("ARR_WLOC_CD", gdReq.getParam("ARR_WLOC_CD"));
			jrParam.setField("TRN_WRK_FULLVOID_GP", "E");
			jrParam.setField("YD_WO_CNCL_YN", "N");
			jrParam.setField("L3_HMI", "Y"); // 백업화면 기동 여부

			EJBConnector ejbConn = new EJBConnector("default", "YsCommCarTSMvSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("rcvTSYSJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "001");
				if (sRuleItem.equals("Y")) {
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
		String methodNm = "상차완료백업처리팝업-등록 [SbrYsJspFaEJB.trtMvCarStatSet2]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("trtMvCarStatSet2", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "001");
				if (sRuleItem.equals("Y")) {
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
		String methodNm = "상차완료백업처리팝업-이송작업재료등록 [SbrYsJspFaEJB.updCarFtMvMtl]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);

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
		String methodNm = "상차완료백업처리팝업-이송작업재료삭제 [SbrYsJspFaEJB.delCarFtMvMtl]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);

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
		String methodNm = "상차완료백업처리팝업-이송작업재료위치변경 [SbrYsJspFaEJB.chgCarFtMvMtl]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);

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
		String methodNm = "하차백업생성팝업-등록 [SbrYsJspFaEJB.mkUdCarSch]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("mkUdCarSch", new Class[] { GridData.class }, new Object[] { gdReq });

			// 전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "001");
				if (sRuleItem.equals("Y")) {
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
		String methodNm = "야드맵정보 불일치 확인 - L2정보요구[SbrYsJspFaEJB.insStrLocMtlIn(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
	 * [A] 오퍼레이션명 : 위치검색열-스케줄별 위치검색 기준 저장
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCrnPosSearchCol_SCH(GridData gdReq) throws DAOException {
		String methodNm = "위치검색열-스케줄별 위치검색 기준 저장[SbrYsJspFaEJB.updCrnPosSearchCol_SCH]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updCrnPosSearchCol_SCH", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
	 * [A] 오퍼레이션명 : 위치검색열-미선택된 위치검색 열 추가or삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCrnPosSearchCol_SelCol(GridData gdReq) throws DAOException {
		String methodNm = "위치검색열-미선택된 위치검색 열 추가or삭제[SbrYsJspFaEJB.updCrnPosSearchCol_SelCol]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updCrnPosSearchCol_SelCol", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
	 * [A] 오퍼레이션명 : 프레스교정기보급요구 - 보급요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData callupdbtMvStkWrkBook_PrssStmc(GridData gdReq) throws DAOException {
		String methodNm = "이적작업팝업-작업예약등록[SbrYsJspFaEJB.callupdbtMvStkWrkBook_PrssStmc(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("callupdbtMvStkWrkBook_PrssStmc", new Class[] { GridData.class }, new Object[] { gdReq });

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
	 * [A] 오퍼레이션명 : 빌렛정정보급요구 - 보급요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData callupdbtMvStkWrkBook_Shear(GridData gdReq) throws DAOException {
		String methodNm = "이적작업팝업-작업예약등록[SbrYsJspFaEJB.callupdbtMvStkWrkBook_Shear(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("callupdbtMvStkWrkBook_Shear", new Class[] { GridData.class }, new Object[] { gdReq });

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
	 * [A] 오퍼레이션명 : 크레인작업매수관리 - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCrnWrkCntMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업매수관리 - 수정[SbrYsJspFaEJB.updCrnWrkCntMgt]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
		String methodNm = "크레인작업매수관리 - 삭제[SbrYsJspFaEJB.delCrnWrkCntMgt]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
	 * [A] 오퍼레이션명 : 압연출하상 - 추출완료
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updExtCmpl(GridData gdReq) throws DAOException {
		String methodNm = "압연출하상 - 추출완료[SbrYsJspFaEJB.updExtCmpl(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("updExtCmpl", new Class[] { GridData.class }, new Object[] { gdReq });

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
	 * [A] 오퍼레이션명 : 압연출하상 - 크레인작업지시 여부
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCrnWoYn(GridData gdReq) throws DAOException {
		String methodNm = "압연출하상 - 크레인작업지시 여부[SbrYsJspFaEJB.updCrnWoYn(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			ejbConn.trx("updCrnWoYn", new Class[] { GridData.class }, new Object[] { gdReq });
			// JDTORecord jrRst = (JDTORecord) 

			// 전송할 Data가 있으면 전송 처리
//			if (jrRst != null) {
//				jrRst.setResultCode(logId);
//				jrRst.setResultMsg(methodNm);
//
//				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
//				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
//			}

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			// 조회결과
			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updCrnWoYn
	
	/**
	 * [A] 오퍼레이션명 : 프레스교정기보급요구 - 예정저장위치 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updExtPlnStrLoc(GridData gdReq) throws DAOException {
		String methodNm = "프레스교정기보급요구 - 예정저장위치 등록[SbrYsJspFaEJB.updExtPlnStrLoc(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			ejbConn.trx("updExtPlnStrLoc", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			// 조회결과
			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updExtPlnStrLoc
	
	/**
	 * [A] 오퍼레이션명 : 차량 - 자동상차완료 여부
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updLdcCmplYn(GridData gdReq) throws DAOException {
		String methodNm = "차량 - 자동상차완료 여부[SbrYsJspFaEJB.updLdcCmplYn(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
	 * [A] 오퍼레이션명 : 대차작업관리 - 대차초기화
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData initTcarSchMgt(GridData gdReq) throws DAOException {
		String methodNm = "대차작업관리 - 대차초기화[SbrYsJspFaEJB.initTcarSchMgt(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("initTcarSchMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
	} // end of initTcarSchMgt
	
	/**
	 * [A] 오퍼레이션명 : 대차작업관리 - 우선순위변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData upTcardWrkBookPrior(GridData gdReq) throws DAOException {
		String methodNm = "대차작업관리 - 대차초기화[SbrYsJspFaEJB.upTcardWrkBookPrior(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			ejbConn.trx("upTcardWrkBookPrior", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			// 조회결과
			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of upTcardWrkBookPrior
	
	/**
	 * [A] 오퍼레이션명 : 대차작업관리 - 작업예약삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delTcarWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "대차작업관리 - 작업예약삭제[SbrYsJspFaEJB.delTcarWrkBook(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("delTcarWrkBook", new Class[] { GridData.class }, new Object[] { gdReq });
			
			if(jrRst != null) {
				gdRet.addParam("errorMsg", jrRst.getFieldString("errorMsg"));
			}

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			// 조회결과
			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of delTcarWrkBook
	
	/**
	 * [A] 오퍼레이션명 : 대차상태설정팝업 - 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	*/
	public GridData trtTcarStatSet(GridData gdReq) throws DAOException {
		String methodNm = "대차상태설정팝업 - 등록[SbrYsJspFaEJB.trtTcarStatSet]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtTcarStatSet", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
//			} else {
//				gdRet.addParam("errorMsg", "정의되지 않은 처리구분[" + gdReq.getParam("TRT_DTL_GP") + "] 입니다.");
//			}

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
	 * 이송Lot List - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws DAOException
	 */
	public Integer updPrepSchLot(GridData gdReq) throws DAOException {
		String methodNm =  "이송Lot List - 수정[SbrYsJspFaEJB.updPrepSchLot]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		
		try {

			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			ejbConn.trx("updPrepSchLot", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
	 * 이송Lot List - 삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public Integer delPrepSchLot(JDTORecord[] gdReq) throws DAOException {
		String methodNm =  "이송Lot List - 삭제[SbrYsJspFaEJB.delFrToMoveLot]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		
		try {

			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			
			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
	 * 이송Lot List - 재료삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws DAOException
	 */
	public Integer delPrepMtl(JDTORecord[] gdReq) throws DAOException {
		String methodNm =  "이송Lot List - 재료삭제[SbrYsJspFaEJB.delPrepMtl]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		
		try {
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
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
	 * 소형출하상 작업현황 - 예정저장위치 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regSmszStbrPlnStrLoc(GridData gdReq) throws DAOException {
		String methodNm = "소형출하상 작업현황 - 예정저장위치 등록 [SbrYsJspFaEJB.regSmszStbrPlnStrLoc]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			ejbConn.trx("regSmszStbrPlnStrLoc", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of regBulkLMillPlnStrLoc
	
	/**
	 * 대차스케줄관리 - 작업예약 우선순위변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updWrkBookPrior(GridData gdReq) throws DAOException {
		String methodNm =  "작업예약 우선순위변경[SbrYsJspFaEJB.updWrkBookPrior]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		
		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			ejbConn.trx("updWrkBookPrior", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");
			
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updWrkBookPrior
	
	/**
	 * 보류재 등록 - 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regRsgd(GridData gdReq) throws DAOException {
		String methodNm = "보류재 등록 - 등록 [SbrYsJspFaEJB.regRsgd]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			ejbConn.trx("regRsgd", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of regRsgd
	
	/**
	 * 보류재 조회 - 보류해제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData udpRsgdRel(GridData gdReq) throws DAOException {
		String methodNm = "보류재 조회 - 보류해제 [SbrYsJspFaEJB.udpRsgdRel]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbrYsJspSeEJB", this);
			ejbConn.trx("udpRsgdRel", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of udpRsgdRel
	
	/**
	 * 보류재 등록 - 빌렛보류등록처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getCodeSearch(GridData gdReq) throws DAOException {
		String methodNm = "보류재 등록 - 빌렛보류등록처리 [SbrYsJspFaEJB.getCodeSearch]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
	        RAPI5J RCaller = new RAPI5J();
	        RCaller.Initialize("YSD010");
	        RCaller.AddItemCount(1); 
	        RCaller.AddItemString(gdReq.getParam("CODE"));
	        
	        RCaller.MBRS_Run();
	        for (int i = 0; i < RCaller.getRowCount(); i++) {
	        	gdRet.addParam("HOLD_CD", RCaller.ReadString()); 		// 보류사유(결함특기사항1)
	        	gdRet.addParam("HOLD_GP", RCaller.ReadString()); 		// 보류처리구분(공정보류구분)
	        	gdRet.addParam("HOLD_TREAT_WAY", RCaller.ReadString()); // 처리방안(조치내용)
//                result.add( i , RCaller.ReadString() );	//결함특기사항1
//                result.add( i , RCaller.ReadString() );	//공정보류구분
//                result.add( i , RCaller.ReadString() );	//조치내용
            }

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of udpRsgdRel
}
