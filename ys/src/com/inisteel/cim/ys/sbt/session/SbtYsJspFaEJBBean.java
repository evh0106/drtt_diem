/**    
 * @(#)SbtYsJspFaEJBSBean
 *
 * @author : 현대제철
 * @date : 2025/06/30
 * @description : 특수강 정정 야드 Jsp Facade EJB
 * @history
 *   - 2025-06-30 : 특수강 대형야드 신예화 프로젝트 초기 버전 작성 (양태호)
 */
package com.inisteel.cim.ys.sbt.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsConstant;
import com.inisteel.cim.ys.common.util.YsQueryIF;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

/**
 * [A] 클래스명 : 특수강 정정 야드 Jsp Facade EJB
 *
 * @ejb.bean name="SbtYsJspFaEJB" jndi-name="SbtYsJspFaEJB" type="Stateless" view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
 */
public class SbtYsJspFaEJBBean extends BaseSessionBean implements YsQueryIF {
	private static final long serialVersionUID = 1L;

	private YsCommUtils commUtils = new YsCommUtils();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
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
		String methodNm = "조회[SbtYsJspFaEJB.getListWithFlex(HashMap)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
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

			EJBConnector ejbConn = new EJBConnector("default", "SbtYsJspSeEJB", this);
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
		String methodNm = "조회[SbtYsJspFaEJB.getMultiListWithFlex(HashMap)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		paramMap.put("logId", logId);
		
		try {
			String pageId = commUtils.trim((paramMap.get("page_id") != null) ? paramMap.get("page_id").toString() : "");
			String pageNm = commUtils.trim((paramMap.get("page_nm") != null) ? paramMap.get("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+", commUtils.hashMapToGridData(paramMap));
			
			EJBConnector ejbConn = new EJBConnector("default","SbtYsJspSeEJB", this);
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
		String methodNm = "스케줄기준관리 - 수정[SbtYsJspFaEJB.updSchRule]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			commUtils.printLog(logId, "YD_SCH_GP:" + gdReq.getParam("YD_SCH_GP").toString(), "");

			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbtYsJspSeEJB", this);
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
		String methodNm = "스케줄기준관리 - 선택복구[SbtYsJspFaEJB.resetSchRule]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			EJBConnector ejbConn = new EJBConnector("default", "SbtYsJspSeEJB", this);

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
		String methodNm = "스케줄기준관리 - 전체복구[SbtYsJspFaEJB.resetAllSchRule]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			EJBConnector ejbConn = new EJBConnector("default", "SbtYsJspSeEJB", this);

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
	 * 재료 지정 등록/해제
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
			
			EJBConnector ejbConn = new EJBConnector("default", "SbtYsJspSeEJB", this);
			ejbConn.trx("updStockAgsnReg", new Class[] { GridData.class }, new Object[] { gdReq });
			
			return gdReq;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog("", methodNm, e));
		}
		
	}	// end of updStockAgsnReg
	
	/**
	 * 보급Lot List - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws DAOException
	 */
	public Integer updPrepSchLot(JDTORecord[] gdReq) throws DAOException {
		String methodNm =  "준비스케줄 - 수정[SbtYsJspFaEJB.updPrepSchLot]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		
		try {

			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "SbtYsJspSeEJB", this);
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
		String methodNm =  "준비스케줄 - 삭제[SbtYsJspFaEJB.delFrToMoveLot]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		
		try {

			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			
			EJBConnector ejbConn = new EJBConnector("default", "SbtYsJspSeEJB", this);
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
		String methodNm =  "준비스케줄 - 재료삭제[SbtYsJspFaEJB.delPrepMtl]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		
		try {
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "SbtYsJspSeEJB", this);
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
	 * 기준관리  - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return Integer
	 * @throws DAOException
	 */
	public Integer updYsRuleSrchGdBt(JDTORecord[] gdReq) throws DAOException {
		String methodNm =  "기준관리 - 검색가이드 수정[SbtYsJspFaEJB.updYsRuleSrchGdBt]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		
		try {
			String pageId = commUtils.trim((gdReq[0].getFieldString("page_id") != null) ? gdReq[0].getFieldString("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq[0].getFieldString("page_nm") != null) ? gdReq[0].getFieldString("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			
			commUtils.printLog(logId, methodNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "SbtYsJspSeEJB", this);
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
			
			EJBConnector ejbConn = new EJBConnector("default", "SbtYsJspSeEJB", this);
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
	 * 빌렛정정 - 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regBulkLMillPlnStrLoc(GridData gdReq) throws DAOException {
		String methodNm = "빌렛정정 - 등록 [SbtYsJspFaEJB.regBulkLMillPlnStrLoc]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbtYsJspSeEJB", this);
			ejbConn.trx("regBulkLMillPlnStrLoc", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of regBulkLMillPlnStrLoc
	
//	/**
//	 * 빌렛정정보급요구 - 개별등록
//	 * 
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param GridData
//	 * @return GridData
//	 * @throws DAOException
//	 */
//	public GridData regLMillPlnStrLoc(GridData gdReq) throws DAOException {
//		String methodNm = "빌렛정정보급요구 - 개별등록 [SbtYsJspFaEJB.regLMillPlnStrLoc]";
//		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
//		gdReq.setIPAddress(logId);
//
//		try {
//			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
//			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
//			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
//			commUtils.printLog(logId, methodNm, "F+", gdReq);
//			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));
//
//			EJBConnector ejbConn = new EJBConnector("default", "SbtYsJspSeEJB", this);
//			ejbConn.trx("regLMillPlnStrLoc", new Class[] { GridData.class }, new Object[] { gdReq });
//
//			commUtils.printLog(logId, methodNm, "F-");
//
//			return gdReq;
//
//		} catch (DAOException e) {
//			throw e;
//		} catch (Exception e) {
//			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
//		}
//	} // end of regLMillPlnStrLoc
	
	/**
	 * 저장위치별온도관리 - 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData insStrLocTmpReg(GridData gdReq) throws DAOException {
		String methodNm = "저장위치별온도관리 - 등록 [SbtYsJspFaEJB.insStrLocTmpReg]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			commUtils.printParam("paramMap", commUtils.gridDataTojdtoRecord(gdReq));

			EJBConnector ejbConn = new EJBConnector("default", "SbtYsJspSeEJB", this);
			ejbConn.trx("insStrLocTmpReg", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of regBulkLMillPlnStrLoc
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업매수관리 - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCrnWrkCntMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업매수관리 - 수정[SbtYsJspFaEJB.updCrnWrkCntMgt]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			EJBConnector ejbConn = new EJBConnector("default", "SbtYsJspSeEJB", this);
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
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			EJBConnector ejbConn = new EJBConnector("default", "SbtYsJspSeEJB", this);
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
	 * [A] 오퍼레이션명 : 빌렛정정실적 완료처리 요구 - 실적요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData BitSherCmplTrPmcDmd(GridData gdReq) throws DAOException {
		String methodNm = "빌렛정정실적 완료처리 요구 - 실적요구[SbtYsJspFaEJB.BitSherCmplTrPmcDmd(GridData)]";
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		gdReq.setIPAddress(logId);

		try {
			String pageId = commUtils.trim((gdReq.getParam("page_id") != null) ? gdReq.getParam("page_id").toString() : "");
			String pageNm = commUtils.trim((gdReq.getParam("page_nm") != null) ? gdReq.getParam("page_nm").toString() : "");
			methodNm = methodNm + " < " + pageId + "(" + pageNm + ")";

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "SbtYsJspSeEJB", this);
			ejbConn.trx("BitSherCmplTrPmcDmd", new Class[] { GridData.class }, new Object[] { gdReq });

//			// 전송할 Data가 있으면 전송 처리
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
	}
}
