/**
 * @(#)SbtYsJspSeEJBBean
 *
 * @author : 현대제철
 * @date : 2025/06/30
 * @description : 특수강 정정 야드 Jsp Facade EJB
 * @history
 *   - 2025-06-30 : 특수강 대형야드 신예화 프로젝트 초기 버전 작성 (양태호)
 */

package com.inisteel.cim.ys.sbt.session;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ct.common.util.CmnUtil;
import com.inisteel.cim.ys.cbt.session.CbtYsComm;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsConstant;
import com.inisteel.cim.ys.common.util.YsQueryIF;
import com.inisteel.cim.ys.common.util.YsQueryIFCbt;
import com.inisteel.cim.ys.sbt.dao.SbtYsDAO;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.record.JDTORecordFactory;

//GridData 사용
//import com.inisteel.cim.ys.cbt.session.CbtYsJspSeEJBBean; 
import xlib.cmc.GridData;

/**
 * [A] 클래스명 : 특수강 정정 야드 Jsp Session EJB
 *
 * @ejb.bean name="SbtYsJspSeEJB" jndi-name="SbtYsJspSeEJB" type="Stateless" view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
 */
public class SbtYsJspSeEJBBean extends BaseSessionBean implements YsQueryIF, YsQueryIFCbt {
	private static final long serialVersionUID = 1L;

	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();
	private CbtYsComm cbtYsComm  = new CbtYsComm();  
	
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
	 */
	public List getListWithFlex(HashMap paramMap) throws DAOException {
		String methodNm = "조회[SbtYsJspSeEJB.getListWithFlex(HashMap)]";
		String logId = (paramMap.get("logId") != null) ? paramMap.get("logId").toString() : "";

		try {
			commUtils.printLog(logId, methodNm, "S+", commUtils.hashMapToGridData(paramMap));

			List rtnList = CmnUtil.listJdtoRecordTohashMap(new SbtYsDAO().getListWithFlex(paramMap).toList());
			
			commUtils.printLog(logId, methodNm, "S-", commUtils.hashMapToGridData(paramMap));
			return rtnList;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	/**
	 * [A] 오퍼레이션명 : Flex에 멀티쿼리를 실행시키기 위한 메소드
	 * @ejb.interface-method
	*/
	public List getMultiListWithFlex(HashMap paramMap) throws DAOException {
		String methodNm = "조회[SbtYsJspSeEJB.getMultiListWithFlex(HashMap)]";
		String logId = (paramMap.get("logId") != null) ? paramMap.get("logId").toString() : "";
		
		try {
			commUtils.printLog(logId, methodNm, "S+", commUtils.hashMapToGridData(paramMap));
			
			List rtnList = new SbtYsDAO().getMultiListWithFlex(paramMap);
			
			commUtils.printLog(logId, methodNm, "S-", commUtils.hashMapToGridData(paramMap));
			return rtnList ;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}
	}

	/**
	 * 스케줄기준관리 - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updSchRule(GridData gdReq) throws DAOException {
		String methodNm = "스케줄기준관리 - 수정[SbtYsJspSeEJB.updSchRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			commUtils.printLog(logId, "rowCnt:" + rowCnt, "");
			commUtils.printLog(logId, "YD_SCH_GP:" + gdReq.getParam("YD_SCH_GP"), "");

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					// 2025.08.25 김현진, 김현규 요청으로 크레인 상태 수정 기능 추가
					// 스케줄기준 수정
					jrParam.setField("M_CRN_PRIOR1", commUtils.getValue(gdReq, "M_CRN_PRIOR1", ii));
					jrParam.setField("M_CRN_PRIOR2", commUtils.getValue(gdReq, "M_CRN_PRIOR2", ii));
					jrParam.setField("YD_CRN_STAT1", commUtils.getValue(gdReq, "YD_CRN_STAT1", ii));
					jrParam.setField("YD_CRN_STAT2", commUtils.getValue(gdReq, "YD_CRN_STAT2", ii));
					jrParam.setField("USERID", commUtils.trim(gdReq.getParam("userid")));
					jrParam.setField("YD_SCH_CD", commUtils.getValue(gdReq, "YD_SCH_CD", ii));
					jrParam.setField("YD_SCH_GP", commUtils.trim(gdReq.getParam("YD_SCH_GP")));
					jrParam.setField("YD_CRN1", commUtils.getValue(gdReq, "YD_CRN1", ii));
					jrParam.setField("YD_CRN2", commUtils.getValue(gdReq, "YD_CRN2", ii));

					commDao.update(jrParam, updYdSchRuleLn, logId, methodNm, "스케줄기준 수정");
				}
			}

			if ("CR".equals(gdReq.getParam("YD_SCH_GP"))) {
				for (int ii = 0; ii < rowCnt; ii++) {
					if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
						// 스케줄금지여부 수정
						jrParam.setField("YD_SCH_PROH_EXN", commUtils.getValue(gdReq, "YD_SCH_PROH_EXN", ii));
						jrParam.setField("YD_SCH_CD", commUtils.getValue(gdReq, "YD_SCH_CD", ii));

						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdSchProhExn", logId, methodNm, "스케줄금지여부수정");
					}
				}
			}

			if (!"CR".equals(gdReq.getParam("YD_SCH_GP"))) {

				String msgId;

				for (int ii = 0; ii < rowCnt; ii++) {

					if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
						// S-Crane 작업순위 변경 전송
						if ("A".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
							msgId = "YSN6L005";
						} else if ("D".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
							msgId = "YSN5L005";
						} else {
							msgId = "";
						}

						jrParam.setField("YD_GP", commUtils.trim(gdReq.getParam("YD_GP")));
						jrParam.setField("YD_BAY_GP", commUtils.trim(gdReq.getParam("YD_BAY_GP")));
						jrParam.setField("YD_EQP_ID", commUtils.trim(gdReq.getParam("CRN_NO")));
						jrParam.setField("YD_SCH_PRIOR", commUtils.getValue(gdReq, "M_CRN_PRIOR1", ii));
						jrParam.setField("YD_SCH_CD", commUtils.getValue(gdReq, "YD_SCH_CD", ii));

						// 전송Data 조회
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2(msgId, jrParam));
					}
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
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
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord resetSchRule(GridData gdReq) throws DAOException {
		String methodNm = "스케줄기준관리 - 선택복구[SbtYsJspSeEJB.resetSchRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					// 스케줄기준 수정
					jrParam.setField("R_CRN_PRIOR1", commUtils.getValue(gdReq, "R_CRN_PRIOR1", ii));
					jrParam.setField("R_CRN_PRIOR2", commUtils.getValue(gdReq, "R_CRN_PRIOR2", ii));
					jrParam.setField("YD_SCH_CD", commUtils.getValue(gdReq, "YD_SCH_CD", ii));
					jrParam.setField("YD_SCH_GP", commUtils.trim(gdReq.getParam("YD_SCH_GP")));
					jrParam.setField("YD_CRN_STAT1", commUtils.getValue(gdReq, "YD_CRN_STAT1", ii));
					jrParam.setField("YD_CRN_STAT2", commUtils.getValue(gdReq, "YD_CRN_STAT2", ii));

					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.resetSchRule", logId, methodNm, "스케줄기준 선택복구");
				}
			}

			if (!"CR".equals(gdReq.getParam("YD_SCH_GP"))) {
				String msgId;

				for (int ii = 0; ii < rowCnt; ii++) {
					if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
						// S-Crane 작업순위 변경 전송
						if ("A".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
							msgId = "YSN6L005";
						} else if ("D".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
							msgId = "YSN5L005";
						} else {
							msgId = "";
						}

						jrParam.setField("YD_GP", commUtils.trim(gdReq.getParam("YD_GP")));
						jrParam.setField("YD_BAY_GP", commUtils.trim(gdReq.getParam("YD_BAY_GP")));
						jrParam.setField("YD_EQP_ID", commUtils.trim(gdReq.getParam("CRN_NO")));
						jrParam.setField("YD_SCH_PRIOR", commUtils.getValue(gdReq, "M_CRN_PRIOR1", ii));
						jrParam.setField("YD_SCH_CD", commUtils.getValue(gdReq, "YD_SCH_CD", ii));

						// 전송Data 조회
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2(msgId, jrParam));
					}
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
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
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord resetAllSchRule(GridData gdReq) throws DAOException {
		String methodNm = "스케줄기준관리 - 전체복구[SbtYsJspSeEJB.resetAllSchRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			// 스케줄기준 수정
			jrParam.setField("USERID", commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("YD_GP", commUtils.trim(gdReq.getParam("YD_GP")));
			jrParam.setField("YD_BAY_GP", commUtils.trim(gdReq.getParam("YD_BAY_GP")));

			commDao.update(jrParam, resetAllSchRuleLn, logId, methodNm, "스케줄기준 전체복구");

			if (!"CR".equals(gdReq.getParam("YD_SCH_GP"))) {

				String msgId;

				// S-Crane 작업순위 변경 전송
				if ("A".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
					msgId = "YSN6L005All";
				} else if ("D".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
					msgId = "YSN5L005All";
				} else {
					msgId = "";
				}

				jrParam.setField("YD_GP", commUtils.trim(gdReq.getParam("YD_GP")));
				jrParam.setField("YD_BAY_GP", commUtils.trim(gdReq.getParam("YD_BAY_GP")));

				// 전송Data 조회
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2(msgId, jrParam));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of resetAllSchRule
	
	/**
	 * 재료 지정 등록/해제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updStockAgsnReg(GridData gdReq) throws DAOException {
		String methodNm = "재료 지정 등록/해제[SbtYsJspSeEJB.updStockAgsnReg(GridData)]";
		String logId = gdReq.getIPAddress();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
		
			String vStock_No = gdReq.getParam("V_STL_NOS");
			String vStockList[] = vStock_No.split(",");
				
			for (int i = 0; i < vStockList.length; i++) {
				//열정보 수정
				
				if(gdReq.getParam("V_GP").equals("1")) {
					jrParam.setField("SSTL_NO"		, vStockList[i]); 
					jrParam.setField("CHK_YN"		, gdReq.getParam("V_CHK_YN")); 
		
					commDao.update(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.updYdStkAsgnReg", logId, methodNm, "재료 지정 등록");
					commUtils.printLog(logId, methodNm, "S-");
				}
				else {
					jrParam.setField("SSTL_NO"		, vStockList[i]); 
					jrParam.setField("CHK_YN"		, null); 
		
					commDao.update(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.updYdStkAsgnReg", logId, methodNm, "재료 지정 해제");
					commUtils.printLog(logId, methodNm, "S-");
				}
			}
			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updStockAgsnReg
	
	/**
	 * 보급Lot List - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updPrepSchLot(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "준비스케줄 - 수정[SbtYsJspSeEJB.updPrepSchLot] < ";
		String logId = gdReq[0].getRequestUserIp();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq[0].getFieldString("userid")));
			
			for (int ii = 0; ii < gdReq.length; ii++) {
				
				jrParam.setField("YD_PREP_SCH_ID"	, commUtils.nvl(gdReq[ii].getFieldString("YD_PREP_SCH_ID"), ""));
				jrParam.setField("YD_AIM_BAY_GP"	, commUtils.nvl(gdReq[ii].getFieldString("YD_AIM_BAY_GP"), "")); 
				jrParam.setField("YD_CARASGN_SEQ"	, commUtils.nvl(gdReq[ii].getFieldString("YD_CARASGN_SEQ"), "")); 
				
				//준비스케줄 수정
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updPrepSch", logId, methodNm, "준비스케줄 수정");
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updPrepSchLot
	
	/**
	 * 준비스케줄 - 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delPrepSchLot(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "준비스케줄 - 삭제[SbtYsJspSeEJB.delPrepSchLot] < ";
		String logId = gdReq[0].getRequestUserIp();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet rsResult = null;
			
			String SSTL_NO = gdReq[0].getFieldString("SSTL_NO");
			String vStockList[] = SSTL_NO.split(",");

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq[0].getFieldString("userid")));
			
			for (int ii = 0; ii < gdReq.length; ii++) {
				
				jrParam.setField("YD_PREP_SCH_ID"	, commUtils.nvl(gdReq[ii].getFieldString("YD_PREP_SCH_ID"), ""));
				
				//준비스케줄 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updPrepSchDelY", logId, methodNm, "준비스케줄 삭제");
				
				//이송LOT ID로 재료번호 조회
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.selSSTLNO", logId, methodNm, "재료번호 조회");
				
				// 재료번호 갯수 만큼 작업
				for(int jj = 0; jj < rsResult.size(); jj++) {
					String v_SSTL_NO = rsResult.getRecord(jj).getFieldString("SSTL_NO");
					
					jrParam.setField("SSTL_NO", v_SSTL_NO);
					
					//준비재료 삭제
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updPrepMtlDelY", logId, methodNm, "준비재료 삭제");
					
					// 소재이송지시 취소
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStlFrtonMoveStat", logId, methodNm, "소재이송지시 취소");
					
					v_SSTL_NO = "";
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delPrepSchLot
	
	/**
	 * 보급Lot List - 재료삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delPrepMtl(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "준비스케줄 - 재료삭제[SbtYsJspSeEJB.delPrepMtl] < ";
		String logId = gdReq[0].getRequestUserIp();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq[0].getFieldString("userid")));
			
			for (int ii = 0; ii < gdReq.length; ii++) {
				
				//준비재료 삭제
				jrParam.setField("SSTL_NO"			, commUtils.nvl(gdReq[ii].getFieldString("SSTL_NO"), "")); 
				jrParam.setField("YD_PREP_SCH_ID"	, commUtils.nvl(gdReq[ii].getFieldString("YD_PREP_SCH_ID"), "")); 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updPrepMtlDelY", logId, methodNm, "준비재료 삭제");
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delPrepMtl
	
	/**
	 * 기준관리 - 검색가이드 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updYsRuleSrchGdBt(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "기준관리 - 검색가이드 수정[SbtYsJspSeEJB.updYsRuleSrchGdBt] < ";
		String logId = gdReq[0].getRequestUserIp();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq[0].getFieldString("userid")));
			
			int ruleCnt = Integer.parseInt(gdReq[0].getFieldString("REPR_CD_GP_CNT"));
			String szRuleCdGp = null;
			String szRuleCdContents = null;
			
			for (int ii = 0; ii < gdReq.length; ii++) {
				
				String szRuleCdGps = null;
				String sCdGp = null;
				String sItem = null;
			
				for (int jj = 0; jj < ruleCnt; jj++) {
					
					szRuleCdGp = gdReq[0].getFieldString("REPR_CD_GP"+(jj+1)); // C00011, C00012, C00013, C00014, C00015 
					szRuleCdGps = gdReq[ii].getFieldString("REPR_CD_GPS"+(jj+1)); //0, 1
					szRuleCdContents = gdReq[0].getFieldString("REPR_CD_CONTENTS"+(jj+1)); 
					sCdGp = "00"+gdReq[ii].getFieldString("YS_STK_BED_NO");
					sItem = gdReq[ii].getFieldString("YS_STK_COL_GP");
					
					jrParam.setField("REPR_CD_GP"	, szRuleCdGp );
					jrParam.setField("CD_GP"		, sCdGp );
					jrParam.setField("ITEM"			, sItem);
					
					commDao.update(jrParam, "com.inisteel.cim.ys.sbt.dao.delYsRule", logId, methodNm, "기준관리 삭제");
						
					if("1".equals(szRuleCdGps)) {
						//기준 등록
						jrParam.setField("REPR_CD_GP"		, szRuleCdGp );
						jrParam.setField("CD_GP"			, commUtils.nvl(gdReq[ii].getFieldString("YS_STK_BED_NO"), "")); 
						jrParam.setField("ITEM"				, commUtils.nvl(gdReq[ii].getFieldString("YS_STK_COL_GP"), "")); 
						jrParam.setField("REPR_CD_CONTENTS"	, szRuleCdContents); 
						
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insYsRule", logId, methodNm, "기준관리 등록");
					}
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updYsRuleSrchGdBt
	
	/**
	 * 보급Lot List - 작업예약등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord insTrfLotWrkBook(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "보급Lot List - 작업예약등록[SbtYsJspSeEJB.insTrfLotWrkBook] < ";
		String logId = gdReq[0].getRequestUserIp();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;
			
			StringBuffer sbImpPros  = new StringBuffer();	//주요진행내용로그

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq[0].getFieldString("userid")));
			
			for (int ii = 0; ii < gdReq.length; ii++) {
				
				System.out.println("===================== 시작 ========================");
				sbImpPros.append("[작업예약등록]::시작 \r\n");
				
				jrParam.setField("YD_PREP_SCH_ID"	, commUtils.nvl(gdReq[ii].getFieldString("YD_PREP_SCH_ID"), ""));
				
				JDTORecordSet jsWbMtl = commDao.select(jrParam, "com.inisteel.cim.ys.sbt.dao.selPrepMtl", logId, methodNm, "재료번호로 조회");
				
				jrParam.setField("YD_SCH_CD"		, commUtils.nvl(gdReq[ii].getFieldString("YD_SCH_CD"), ""));
				jrParam.setField("YD_AIM_BAY_GP"	, commUtils.nvl(gdReq[ii].getFieldString("YD_AIM_BAY_GP"), "")); 
				jrParam.setField("YD_TO_LOC_GUIDE"	, commUtils.nvl(gdReq[ii].getFieldString("YD_TO_LOC_GUIDE"), ""));
				jrParam.setField("YD_WRK_CRN"		, commUtils.nvl(gdReq[ii].getFieldString("YD_WRK_CRN"), ""));
				jrParam.setField("YD_PREP_SCH_ID"	, commUtils.nvl(gdReq[ii].getFieldString("YD_PREP_SCH_ID"), ""));
				
				EJBConnector ejbConn = new EJBConnector("default", "CbtYsJspSeEJB", this);
				ejbConn.trx("insMvstkWrkBook", new Class[] { JDTORecord.class, JDTORecordSet.class, StringBuffer.class }, new Object[] { jrParam, jsWbMtl, sbImpPros });

				System.out.println("===================== 완료 ========================");
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updPrepSchLot
	
	/**
	 *      [A] 오퍼레이션명 : 크레인사양분리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrCrnSpec
	 *      @param JDTORecordSet jsWrkMtl
	 *      @return Vector
	 *      @throws DAOException
	*/
	public Vector setCrnSpecSpr(JDTORecord jrCrnSpec, JDTORecordSet jsWrkMtl) throws DAOException {
		String methodNm = "크레인사양분리[SbtYsJspSeEJB.setCrnSpecSpr] < " + jrCrnSpec.getResultMsg();
		String logId = jrCrnSpec.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			Vector        vcLot = new Vector();											//크레인사양분리결과
			JDTORecord    jrRow = null;													//현재 Row
			JDTORecordSet jsLot = JDTORecordFactory.getInstance().createRecordSet("");	//Lot: 크레인사양 (작업예약별 작업재료묶음)
			
			String szYS_STK_COL_GP     = "";	//현재 열
			String szYS_STK_BED_NO     = "";	//현재 BED
			String szYS_STK_LYR_NO     = "";	//현재 단
			String szYS_STK_COL_GP_BEF = "";	//이전 열
			String szYS_STK_BED_NO_BEF = "";	//이전 BED
			String szYS_STK_LYR_NO_BEF = "";	//이전 단
			
			String sITEMNAME_CD        = "";	//품목 [강각:SRQ],[빌렛:SRT->SRI,SRW],[봉강:SRR]

			int    iWM_STK_SH          = 6;		//품목별 적치단의 최대 적치 가능 매수 (= 크레인 작업가능 재료매수)
			int    iWM_LOT_CNT         = 0;		//크레인 작업 재료매수
			int    rowCnt              = jsWrkMtl.size();	//소재 개수
			
			//크레인사양분리 기준: 동일 "열/BED/단"에 있는 작업재료 묶음
			for( int ii = 0; ii < rowCnt; ii++ ) {
				
				jrRow = jsWrkMtl.getRecord(ii);
				
				szYS_STK_COL_GP = commUtils.trim(jrRow.getFieldString("YS_STK_COL_GP"));
				szYS_STK_BED_NO = commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO"));
				szYS_STK_LYR_NO = commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO"));
				
				if( ii > 0 ) {
					String sLoc_BEF = szYS_STK_COL_GP_BEF + szYS_STK_BED_NO_BEF + szYS_STK_LYR_NO_BEF;
					String sLoc_CUR = szYS_STK_COL_GP     + szYS_STK_BED_NO     + szYS_STK_LYR_NO;
					
					//이전 위치정보 != 현재 위치정보
					if( !(sLoc_BEF).equals(sLoc_CUR) || iWM_LOT_CNT % iWM_STK_SH == 0 ) {
						// "크레인사양분리결과"에 이전 Lot 추가
						vcLot.add(jsLot);
						
						// 신규 Lot 생성 & 작업재료 추가
						jsLot = JDTORecordFactory.getInstance().createRecordSet("");
						szYS_STK_COL_GP_BEF  = szYS_STK_COL_GP;
						szYS_STK_BED_NO_BEF  = szYS_STK_BED_NO;
						szYS_STK_LYR_NO_BEF  = szYS_STK_LYR_NO;
						
						iWM_LOT_CNT = 0;
					}
					
				} else {
					szYS_STK_COL_GP_BEF  = szYS_STK_COL_GP;
					szYS_STK_BED_NO_BEF  = szYS_STK_BED_NO;
					szYS_STK_LYR_NO_BEF  = szYS_STK_LYR_NO;

					sITEMNAME_CD = commUtils.trim(jrRow.getFieldString("ITEMNAME_CD"));
					if( ("SRR").equals(sITEMNAME_CD) ) {
						iWM_STK_SH = 4;	//봉강      적치단의 최대 적치 가능 매수는 4 == 크레인 작업가능 재료매수
					} else {
						iWM_STK_SH = 6;	//각강/빌렛 적치단의 최대 적치 가능 매수는 6 == 크레인 작업가능 재료매수
					}
				}
				
				//만들어진 Lot에 작업재료 추가 
				jsLot.addRecord(jrRow);
				
				++iWM_LOT_CNT;
			}
			
			//마지막 Lot 추가
			vcLot.add(jsLot);
			
			commUtils.printLog(logId, methodNm, "S-");

			return vcLot;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 빌렛정정보급요구 - 일괄등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord regBulkLMillPlnStrLoc(GridData gdReq) throws DAOException {
		String methodNm = "빌렛정정보급요구 - 일괄등록 [SbtYsJspSeEJB.regBulkLMillPlnStrLoc] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecord jrParam1 = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrParam2 = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam1.setField("REPR_CD_GP", "APPGI3");
			jrParam1.setField("CD_GP", commUtils.trim(gdReq.getParam("YD_STR_LOC")));
			jrParam1.setField("ITEM", commUtils.trim(gdReq.getParam("PLNSTRLOC")));
			jrParam1.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
			
			commDao.update(jrParam1, "com.inisteel.cim.ys.sbt.dao.updRule", logId, methodNm, "TB_YS_RULE");
			
			jrParam2.setField("REPR_CD_GP", "APPGI5");
			jrParam2.setField("CD_GP", commUtils.trim(gdReq.getParam("YD_STR_LOC")));
			jrParam2.setField("ITEM", commUtils.trim(gdReq.getParam("TRFDSTN")));
			jrParam2.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
			
			commDao.update(jrParam2, "com.inisteel.cim.ys.sbt.dao.updRule", logId, methodNm, "TB_YS_RULE");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}// end of regBulkLMillPlnStrLoc
	
//	/**
//	 * 빌렛정정보급요구 - 개별등록
//	 *
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param GridData gdReq
//	 * @return JDTORecord
//	 * @throws DAOException
//	 */
//	public JDTORecord regLMillPlnStrLoc(GridData gdReq) throws DAOException {
//		String methodNm = "빌렛정정보급요구 - 개별등록 [SbtYsJspSeEJB.regLMillPlnStrLoc] < " + gdReq.getNavigateValue();
//		String logId = gdReq.getIPAddress();
//
//		try {
//			commUtils.printLog(logId, methodNm, "S+", gdReq);
//
//			// Return Value
//			JDTORecord jrRtn = null;
//			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
//
//			// 수정할 레코드 수
//			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
//
//			for (int ii = 0; ii < rowCnt; ii++) {
//				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
//					jrParam.setField("SSTL_NO", commUtils.getValue(gdReq, "SSTL_NO", ii));
//					jrParam.setField("HEAT_NO", commUtils.getValue(gdReq, "HEAT_NO", ii));
//					jrParam.setField("SPEC_ABBSYM", commUtils.getValue(gdReq, "SPEC_ABBSYM", ii));
//					jrParam.setField("ITEMNAME_CD", commUtils.getValue(gdReq, "ITEMNAME_CD", ii));
//					jrParam.setField("YD_CHG_NO", commUtils.getValue(gdReq, "YD_CHG_NO", ii));
//					jrParam.setField("SPST_FRTOMOVE_GP", commUtils.getValue(gdReq, "SPST_FRTOMOVE_GP", ii));
//					jrParam.setField("URGENT_FRTOMOVE_WORD_GP", commUtils.getValue(gdReq, "URGENT_FRTOMOVE_WORD_GP", ii));
//					jrParam.setField("YD_RCPT_PLN_STR_LOC", commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", ii));
//					jrParam.setField("REGISTER", commUtils.trim(gdReq.getParam("userid")));
//					jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
//
//					// 대형압연재예정저장위치 등록 및 수정
//					commDao.update(jrParam, "com.inisteel.cim.ys.sbt.dao.insLmillplnstrloc", logId, methodNm, "TB_YS_STOCK");
//				}
//			}
//
//			commUtils.printLog(logId, methodNm, "S-");
//
//			return jrRtn;
//		} catch (DAOException e) {
//			throw e;
//		} catch (Exception e) {
//			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
//		}
//	}// end of regLMillPlnStrLoc
	
	/**
	 * 저장위치별온도관리 - 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord insStrLocTmpReg(GridData gdReq) throws DAOException {
		String methodNm = "저장위치별온도관리 - 등록 [SbtYsJspSeEJB.insStrLocTmpReg] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("TMP_REG_DT", commUtils.trim(gdReq.getParam("TMP_REG_DT")));
			jrParam.setField("YD_TEMP1", commUtils.nvl(commUtils.getValue(gdReq, "YD_TEMP1", 0), "0"));
			jrParam.setField("YD_TEMP2", commUtils.nvl(commUtils.getValue(gdReq, "YD_TEMP2", 0), "0"));
			jrParam.setField("YD_TEMP3", commUtils.nvl(commUtils.getValue(gdReq, "YD_TEMP3", 0), "0"));
			jrParam.setField("YD_TEMP4", commUtils.nvl(commUtils.getValue(gdReq, "YD_TEMP4", 0), "0"));
			jrParam.setField("YD_TEMP5", commUtils.nvl(commUtils.getValue(gdReq, "YD_TEMP5", 0), "0"));
			jrParam.setField("YD_TEMP6", commUtils.nvl(commUtils.getValue(gdReq, "YD_TEMP6", 0), "0"));
			jrParam.setField("YD_TEMP7", commUtils.nvl(commUtils.getValue(gdReq, "YD_TEMP7", 0), "0"));
			jrParam.setField("YD_TEMP8", commUtils.nvl(commUtils.getValue(gdReq, "YD_TEMP8", 0), "0"));
			jrParam.setField("YD_TEMP9", commUtils.nvl(commUtils.getValue(gdReq, "YD_TEMP9", 0), "0"));
			jrParam.setField("YD_TEMP10", commUtils.nvl(commUtils.getValue(gdReq, "YD_TEMP10", 0), "0"));
			jrParam.setField("REMARKS", commUtils.trim(gdReq.getParam("REMARKS")));
			jrParam.setField("REGISTER", commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
			
			commDao.update(jrParam, "com.inisteel.cim.ys.sbt.dao.insStrLocTmpReg", logId, methodNm, "TB_YS_BLTYDTMPMGT");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}// end of regBulkLMillPlnStrLoc
	
	/**
	 * 크레인작업매수관리 - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updCrnWrkCntMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업매수관리 - 수정[SbtYsJspSeEJB.updCrnWrkCntMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					
					String ITM_GP = commUtils.getValue(gdReq, "ITEM_GP", ii).trim();
					String T_LWLM = commUtils.getValue(gdReq, "T_LWLM", ii).trim();
					String T_UWLM = commUtils.getValue(gdReq, "T_UWLM", ii).trim();
					String L_LWLM = commUtils.getValue(gdReq, "L_LWLM", ii).trim();
					String L_UWLM = commUtils.getValue(gdReq, "L_UWLM", ii).trim();
					String MTL_CNT = commUtils.getValue(gdReq, "MTL_CNT", ii).trim();
					String R_DESC = commUtils.getValue(gdReq, "R_DESC", ii).trim();
					String REPR_CD_CONTENTS = ITM_GP+"#"+T_LWLM+"#"+T_UWLM+"#"+L_LWLM+"#"+L_UWLM+"#"+MTL_CNT+"#"+R_DESC;
					
					jrParam.setField("REPR_CD_GP", commUtils.getValue(gdReq, "REPR_CD_GP", ii));
					jrParam.setField("CD_GP", commUtils.getValue(gdReq, "CD_GP", ii));
					jrParam.setField("ITEM", commUtils.getValue(gdReq, "ITEM", ii));
					jrParam.setField("REPR_CD_CONTENTS", REPR_CD_CONTENTS);
					jrParam.setField("REGISTER", commUtils.trim(gdReq.getParam("userid")));
					jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));

					commDao.update(jrParam, "com.inisteel.cim.ys.sbt.dao.updCrnWrkCntMgt", logId, methodNm, "크레인작업매수관리 수정");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updCrnWrkCntMgt
	
	/**
	 * 크레인작업매수관리 - 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord delCrnWrkCntMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업매수관리 - 삭제[SbtYsJspSeEJB.delCrnWrkCntMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

//			commUtils.printLog(logId, "rowCnt:" + rowCnt, "");
//			commUtils.printLog(logId, "YD_SCH_GP:" + gdReq.getParam("YD_SCH_GP"), "");

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					
					jrParam.setField("USERID", commUtils.trim(gdReq.getParam("userid")));
					jrParam.setField("REPR_CD_GP", commUtils.getValue(gdReq, "REPR_CD_GP", ii));
					jrParam.setField("CD_GP", commUtils.getValue(gdReq, "CD_GP", ii));
					jrParam.setField("ITEM", commUtils.getValue(gdReq, "ITEM", ii));
					
					commDao.update(jrParam, "com.inisteel.cim.ys.sbt.dao.delCrnWrkCntMgt", logId, methodNm, "크레인작업매수관리 삭제");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of delCrnWrkCntMgt
	
	/**
	 * 빌렛정정실적 완료처리 요구 - 실적요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord BitSherCmplTrPmcDmd(GridData gdReq) throws DAOException {
		String methodNm = "빌렛정정실적 완료처리 요구 - 실적요구[SbtYsJspSeEJB.BitSherCmplTrPmcDmd] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null; 
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			JDTORecord Send_Data = JDTORecordFactory.getInstance().create();
			
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name
			
			String SSTL_NOList[] = new String[40];
			String vSTL_List[] = gdReq.getParam("SSTL_NO").split(",");
			
			jrParam.setField("JMS_TC_CD", "YSM4L217");
			jrParam.setField("DATE", commUtils.getDate10());
			jrParam.setField("TIME", commUtils.getTime8());
			jrParam.setField("MSG_GP", "I");
			jrParam.setField("MSG_LEN", "0482");
			jrParam.setField("TEMP", "");
			jrParam.setField("YD_GP", "G");
			jrParam.setField("STL_SH", commUtils.trim(gdReq.getParam("STL_SH")));
			
			for(int i = 0; i < SSTL_NOList.length; i++) {
				SSTL_NOList[i] = "";
			}
			
			for(int j = 0; j < vSTL_List.length; j++) {
				SSTL_NOList[j] = vSTL_List[j];
			}
			
			
			for(int i = 0; i < SSTL_NOList.length; i++) {
				int idx = i + 1;
				jrParam.setField("SSTL_NO"+idx, SSTL_NOList[i]);
			}
			
			Send_Data.setField("SEND_DATA", jrParam);
			
			//송신 공통 EJB를 이용하여 L2로 전송
			EJBConnector ejbConn = new EJBConnector("default", "YsCommEJB", this);
			ejbConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { Send_Data });
//			sndInterfacePI sndToEAI
			
//			EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
//			sndConn.trx("rcvInterface", new Class[] { JDTORecord.class }, new Object[] { jrParam });

//			EJBConnector sndConn = new EJBConnector("default", "CbtYsL2RcvSeEJB", this);
//			jrRtn = (JDTORecord) sndConn.trx("rcvN7YSL206", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of BitSherCmplTrPmcDmd
}