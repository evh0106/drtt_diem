/**
 * @(#)YsTotYsJspSeEJBBean
 *
 * @author : 현대제철
 * @date : 2025/06/30
 * @description : 특수강 사외 통합 야드 Jsp Facade EJB
 * @history
 *   - 2025-06-30 : 특수강 대형야드 신예화 프로젝트 초기 버전 작성 (양태호)
 */
package com.inisteel.cim.ys.ystot.session;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.ct.common.util.CmnUtil;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.session.YsComm;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsConstant;
import com.inisteel.cim.ys.common.util.YsQueryIF;
import com.inisteel.cim.ys.common.util.YsQueryIFCar;
import com.inisteel.cim.ys.common.util.YsQueryIFYsTot;
import com.inisteel.cim.ys.sbt.dao.SbtYsDAO;
import com.inisteel.cim.ys.ystot.dao.YsTotYsDAO;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

/**
 * [A] 클래스명 : 특수강 사외 통합 야드 Jsp Session EJB
 *
 * @ejb.bean name="YsTotYsJspSeEJB" jndi-name="YsTotYsJspSeEJB" type="Stateless" view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
 */
public class YsTotYsJspSeEJBBean extends BaseSessionBean implements YsQueryIF, YsQueryIFYsTot, YsQueryIFCar {
	private static final long serialVersionUID = 1L;

	private YsCommUtils commUtils = new YsCommUtils();
	private YsConstant  constant  = new YsConstant();
	private YsCommDAO 	commDao   = new YsCommDAO();
	private YsComm      ysComm    = new YsComm();
	private YsTotYsComm ystotYsComm = new YsTotYsComm();
	
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
		String methodNm = "조회[YsTotYsJspSeEJB.getListWithFlex(HashMap)]";
		String logId = (paramMap.get("logId") != null) ? paramMap.get("logId").toString() : "";

		try {
			commUtils.printLog(logId, methodNm, "S+", commUtils.hashMapToGridData(paramMap));

			List rtnList = CmnUtil.listJdtoRecordTohashMap(new YsTotYsDAO().getListWithFlex(paramMap).toList());
			
			commUtils.printLog(logId, methodNm, "S-", commUtils.hashMapToGridData(paramMap));
			return rtnList;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	/**
	 * [A] 오퍼레이션명 : Flex에 멀티쿼리를 실행시키기 위한 메소드
	 * 
	 * @ejb.interface-method
	 */
	public List getMultiListWithFlex(HashMap paramMap) throws DAOException {
		String methodNm = "조회[YsTotYsJspSeEJB.getMultiListWithFlex(HashMap)]";
		String logId = (paramMap.get("logId") != null) ? paramMap.get("logId").toString() : "";

		try {
			commUtils.printLog(logId, methodNm, "S+", commUtils.hashMapToGridData(paramMap));

			List rtnList = new SbtYsDAO().getMultiListWithFlex(paramMap);

			commUtils.printLog(logId, methodNm, "S-", commUtils.hashMapToGridData(paramMap));
			return rtnList;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	/**
	 * GridData - 단순 조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData getSelectData(GridData gdReq) throws DAOException {
		String methodNm = "조회[YsTotYsJspSeEJB.getSelectData] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			// Grid date 를 JDTORecord data 로 변환
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			commDao.jspSelect(inRecord, outRecSet, inRecord.getFieldString("QUERY_ID"), logId, methodNm);
			
			// UI로 반환 할 Grid data 를 생성
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			GridData gdRet = commUtils.jdtoRecordToGridData(gdRtn, outRecSet.toList(), gdReq);

			commUtils.printLog(logId, methodNm, "S-", gdReq);

			return gdRet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of getSelectData
	
	/**
	 * 단순 조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSelectData(JDTORecord recPara) throws DAOException {
		String methodNm = "조회[YsTotYsJspSeEJB.getSelectData] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();

		try {

			commUtils.printLog(logId, methodNm, "S+", recPara);

			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

			commDao.jspSelect(recPara, outRecSet, recPara.getFieldString("QUERY_ID"), logId, methodNm);

			commUtils.printLog(logId, methodNm, "S-", recPara);

			return outRecSet;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of getSelectData
	
	/**
	 * 저장위치수정 - 저장품등록 및 변경전 정합성 체크
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord insBtYsStock(GridData gdReq) throws DAOException {
		String methodNm = "저장위치수정 - 저장품등록 및 변경전 정합성 체크[YsTotYsJspSeEJB.insBtYsStock] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet jsTemp = null;
			JDTORecord jrTemp = null;

			String szStlNo = null;
			String szYsStkColGp = null;
			String szYsStkBedNo = null;
			String szYsStkLyrNo = null;
			String szYsStkSeqNo = null;

			String szOldStlNo = null;
			String szOldYsStkColGp = null;
			String szOldYsStkBedNo = null;
			String szOldYsStkLyrNo = null;
			String szOldYsStkSeqNo = null;

			String szFromStlNo = null;
			String szFromYsStkColGp = null;
			String szFromYsStkBedNo = null;
			String szFromYsStkLyrNo = null;
			String szFromYsStkSeqNo = null;

			String szStkStlNo = null;
			// String szCurrProgCd = null;
			// String szOrdYeojaeGp = null;
			String szWbookId = null;
			String szCrnSchId = null;
			String szToLocMtlStat = null;

			String szModGp = null; // 작업구분
			String sFromLoc = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {

				szStlNo = commUtils.getValue(gdReq, "SSTL_NO", ii);
				szYsStkColGp = commUtils.getValue(gdReq, "YS_STK_COL_GP", ii);
				szYsStkBedNo = commUtils.getValue(gdReq, "YS_STK_BED_NO", ii);
				szYsStkLyrNo = commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii);
				szYsStkSeqNo = commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii);

				szOldStlNo = commUtils.getValue(gdReq, "OLD_SSTL_NO", ii);
				szOldYsStkColGp = commUtils.getValue(gdReq, "OLD_YS_STK_COL_GP", ii);
				szOldYsStkBedNo = commUtils.getValue(gdReq, "OLD_YS_STK_BED_NO", ii);
				szOldYsStkLyrNo = commUtils.getValue(gdReq, "OLD_YS_STK_LYR_NO", ii);
				szOldYsStkSeqNo = commUtils.getValue(gdReq, "OLD_YS_STK_SEQ_NO", ii);

				szFromStlNo = commUtils.getValue(gdReq, "FROM_SSTL_NO", ii);
				szFromYsStkColGp = commUtils.getValue(gdReq, "FROM_YS_STK_COL_GP", ii);
				szFromYsStkBedNo = commUtils.getValue(gdReq, "FROM_YS_STK_BED_NO", ii);
				szFromYsStkLyrNo = commUtils.getValue(gdReq, "FROM_YS_STK_LYR_NO", ii);
				szFromYsStkSeqNo = commUtils.getValue(gdReq, "FROM_YS_STK_SEQ_NO", ii);

				if (szStlNo.equals(szOldStlNo) && szYsStkColGp.equals(szOldYsStkColGp) && szYsStkBedNo.equals(szOldYsStkBedNo) && szYsStkLyrNo.equals(szOldYsStkLyrNo)
						&& szYsStkSeqNo.equals(szOldYsStkSeqNo)) {
					// 변경사항이 없음으로 Skip 한다.
					continue;
				}

				if ("".equals(szStlNo) && !"".equals(szOldStlNo)) {
					// 삭제처리
					szModGp = "DELETE";
					jrParam.setField("SSTL_NO", szOldStlNo);
				}

				if (!"".equals(szStlNo) && "".equals(szOldStlNo) && "".equals(szFromStlNo)) {
					// 추가처리
					szModGp = "ADD";
					jrParam.setField("SSTL_NO", szStlNo);
				}

				if (!"".equals(szStlNo) && "".equals(szOldStlNo) && szStlNo.equals(szFromStlNo)) {
					// 이동처리
					szModGp = "MOVE";
					jrParam.setField("SSTL_NO", szStlNo);
				}

				if (!szYsStkColGp.equals(szOldYsStkColGp) || !szYsStkBedNo.equals(szOldYsStkBedNo) || !szYsStkLyrNo.equals(szOldYsStkLyrNo) || !szYsStkSeqNo.equals(szOldYsStkSeqNo)) {
					// SEQ변경처리 UP,DOWN
					szModGp = "UPDOWN";
					jrParam.setField("SSTL_NO", szStlNo);
				}

				jrParam.setField("YS_STK_COL_GP", szYsStkColGp);
				jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
				jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
				jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);

				// 제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BILLET공통에서 현재진도코드를 가져온다.
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");

				if (jsTemp != null && jsTemp.size() > 0) {
					jrTemp = jsTemp.getRecord(0);

					szStkStlNo = commUtils.trim(jrTemp.getFieldString("SSTL_NO"));
					// szCurrProgCd = commUtils.trim(jrTemp.getFieldString("CURR_PROG_CD"));
					// szOrdYeojaeGp = commUtils.trim(jrTemp.getFieldString("ORD_YEOJAE_GP"));
					szWbookId = commUtils.trim(jrTemp.getFieldString("YD_WBOOK_ID"));
					szCrnSchId = commUtils.trim(jrTemp.getFieldString("YD_CRN_SCH_ID"));
					szToLocMtlStat = commUtils.trim(jrTemp.getFieldString("TOLOC_MTL_STAT"));

				} else {
					throw new Exception("제품번호로 저장품 및 작업대상여부 조회시 에러가 발생했습니다!");
				}

				// 저장품에 존재하는 제품번호인지 체크
				if ("ADD".equals(szModGp) || "MOVE".equals(szModGp)) {
					if ("".equals(szStkStlNo)) {
						// 저장품 등록
						commDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.insBtYdStock", logId, methodNm, "저장품 등록");
					}
				}

				// 추가,삭제,이동 모두 크레인스케줄에 작업대상인지 체크
				if (!"".equals(szCrnSchId)) {
					throw new Exception("제품번호 : " + jrParam.getFieldString("SSTL_NO") + " 가 크레인스케줄(" + szCrnSchId + ")에 작업대상으로 잡혀있습니다! 크레인작업이 완료된 이후나 크래인작업을 취소 한 후 수정이 가능합니다.");
				}

				// 삭제는 작업예약에 대상으로 잡혀있으면 삭제 불가함
				if ("DELETE".equals(szModGp) && !"".equals(szWbookId) && "C".equals(szToLocMtlStat)) {
					throw new Exception("제품번호 : " + jrParam.getFieldString("SSTL_NO") + " 가 작업예약(" + szWbookId + ")에 작업대상으로 잡혀있습니다! 작업예약을 취소한 후 삭제가 가능합니다.");
				}

				if ("ADD".equals(szModGp) || "MOVE".equals(szModGp)) {
					// TO위치의 재료상태가 'E' 가 아니면 작업할 수 없음
					if (!"E".equals(szToLocMtlStat)) {
						throw new Exception("TO 위치의 재료상태가 " + szToLocMtlStat + " 입니다. 등록(이동) 작업을 할 수 없습니다.");
					}
				} else {
					// TO위치의 재료상태가 'U'나 'D'일 경우 수정작업을 할 수 없음
					// if("U".equals(szToLocMtlStat) || "D".equals(szToLocMtlStat) ) {
					// throw new Exception("TO 위치의 재료상태가 " + szToLocMtlStat + " 로 변경되었습니다. 삭제(변경) 작업을 할 수 없습니다.");
					// }
				}

				// 이동인 경우 From위치에 szStlNo가 적치중 인지 확인
				if ("MOVE".equals(szModGp)) {
					jrParam.setField("YS_STK_COL_GP", szFromYsStkColGp);
					jrParam.setField("YS_STK_BED_NO", szFromYsStkBedNo);
					jrParam.setField("YS_STK_LYR_NO", szFromYsStkLyrNo);
					jrParam.setField("YS_STK_SEQ_NO", szFromYsStkSeqNo);

					// From 위치 확인 하기
					jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyr", logId, methodNm, "적치단정보조회");

					if (jsTemp != null && jsTemp.size() > 0) {
						jrTemp = jsTemp.getRecord(0);

						if (!szStlNo.equals(commUtils.trim(jrTemp.getFieldString("SSTL_NO")))) {

							sFromLoc = szFromYsStkColGp + "-" + szFromYsStkBedNo + "-" + szFromYsStkLyrNo + "-" + szFromYsStkSeqNo;

							throw new Exception("From 위치[" + sFromLoc + "]의 재료번호가 [" + szStlNo + "]가 아닙니다. Crane작업이나 다른 작업자에 의해 이미 수정되었습니다.");
						}

					} else {
						throw new Exception("From 위치 조회시 에러가 발생했습니다!");
					}
				}

				// SEQ변경처리 UP,DOWN 인 경우 이전위치에 szStlNo가 적치중 인지 확인
				if ("UPDOWN".equals(szModGp)) {

					jrParam.setField("YS_STK_COL_GP", szOldYsStkColGp);
					jrParam.setField("YS_STK_BED_NO", szOldYsStkBedNo);
					jrParam.setField("YS_STK_LYR_NO", szOldYsStkLyrNo);
					jrParam.setField("YS_STK_SEQ_NO", szOldYsStkSeqNo);

					// From 위치 확인 하기
					jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyr", logId, methodNm, "적치단정보조회");

					if (jsTemp != null && jsTemp.size() > 0) {
						jrTemp = jsTemp.getRecord(0);

						if (!szStlNo.equals(commUtils.trim(jrTemp.getFieldString("SSTL_NO")))) {

							sFromLoc = szOldYsStkColGp + "-" + szOldYsStkBedNo + "-" + szOldYsStkLyrNo + "-" + szOldYsStkSeqNo;

							throw new Exception("이전위치[" + sFromLoc + "]의 재료번호가 [" + szStlNo + "]가 아닙니다. Crane작업이나 다른 작업자에 의해 이미 수정되었습니다.");
						}

					} else {
						throw new Exception("이전(Old) 위치 조회시 에러가 발생했습니다!");
					}
				}

				// DELETE인 경우 이전위치에 szOldStlNo가 적치중 인지 확인
				if ("DELETE".equals(szModGp)) {

					jrParam.setField("YS_STK_COL_GP", szYsStkColGp);
					jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
					jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
					jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);

					// From 위치 확인 하기
					jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyr", logId, methodNm, "적치단정보조회");

					if (jsTemp != null && jsTemp.size() > 0) {
						jrTemp = jsTemp.getRecord(0);

						if (!szOldStlNo.equals(commUtils.trim(jrTemp.getFieldString("SSTL_NO")))) {

							sFromLoc = szYsStkColGp + "-" + szYsStkBedNo + "-" + szYsStkLyrNo + "-" + szYsStkSeqNo;

							throw new Exception("현재 위치[" + sFromLoc + "]의 재료번호가 [" + szOldStlNo + "]가 아닙니다. Crane작업이나 다른 작업자에 의해 이미 수정되었습니다.");
						}

					} else {
						throw new Exception("현재 위치 조회시 에러가 발생했습니다!");
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
	} // end of insBtYsStock
	
	
	/**
	 * 저장위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord updStrLocMod(GridData gdReq) throws DAOException {
		String methodNm = "저장위치 수정[YsTotYsJspSeEJB.updStrLocMod] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet jsTemp = null;
			JDTORecord jrTemp = null;

			String szStlNo = null;
			String szYsStkColGp = null;
			String szYsStkBedNo = null;
			String szYsStkLyrNo = null;
			String szYsStkSeqNo = null;

			String szOldStlNo = null;
			String szOldYsStkColGp = null;
			String szOldYsStkBedNo = null;
			String szOldYsStkLyrNo = null;
			String szOldYsStkSeqNo = null;

			String szFromStlNo = null;
			String szFromYsStkColGp = null;
			String szFromYsStkBedNo = null;
			String szFromYsStkLyrNo = null;
			String szFromYsStkSeqNo = null;

			String szStkStlNo = null;
			String szCurrProgCd = null;
			String szOrdYeojaeGp = null;
			// String szWbookId = null;
			// String szCrnSchId = null;
			// String szToLocMtlStat = null;

			String szModGp = null; // 작업구분
			String szFtmvCarudCmplYn = null; // 이송하차완료처리

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			// 이송하차완료처리
			szFtmvCarudCmplYn = commUtils.trim(gdReq.getParam("FTMV_CARUD_CMPL_YN"));

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {

				szStlNo = commUtils.getValue(gdReq, "SSTL_NO", ii);
				szYsStkColGp = commUtils.getValue(gdReq, "YS_STK_COL_GP", ii);
				szYsStkBedNo = commUtils.getValue(gdReq, "YS_STK_BED_NO", ii);
				szYsStkLyrNo = commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii);
				szYsStkSeqNo = commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii);

				szOldStlNo = commUtils.getValue(gdReq, "OLD_SSTL_NO", ii);
				szOldYsStkColGp = commUtils.getValue(gdReq, "OLD_YS_STK_COL_GP", ii);
				szOldYsStkBedNo = commUtils.getValue(gdReq, "OLD_YS_STK_BED_NO", ii);
				szOldYsStkLyrNo = commUtils.getValue(gdReq, "OLD_YS_STK_LYR_NO", ii);
				szOldYsStkSeqNo = commUtils.getValue(gdReq, "OLD_YS_STK_SEQ_NO", ii);

				szFromStlNo = commUtils.getValue(gdReq, "FROM_SSTL_NO", ii);
				szFromYsStkColGp = commUtils.getValue(gdReq, "FROM_YS_STK_COL_GP", ii);
				szFromYsStkBedNo = commUtils.getValue(gdReq, "FROM_YS_STK_BED_NO", ii);
				szFromYsStkLyrNo = commUtils.getValue(gdReq, "FROM_YS_STK_LYR_NO", ii);
				szFromYsStkSeqNo = commUtils.getValue(gdReq, "FROM_YS_STK_SEQ_NO", ii);

				if (szStlNo.equals(szOldStlNo) && szYsStkColGp.equals(szOldYsStkColGp) && szYsStkBedNo.equals(szOldYsStkBedNo) && szYsStkLyrNo.equals(szOldYsStkLyrNo)
						&& szYsStkSeqNo.equals(szOldYsStkSeqNo)) {
					// 변경사항이 없음으로 Skip 한다.
					continue;
				}

				if ("".equals(szStlNo) && !"".equals(szOldStlNo)) {
					// 삭제처리
					szModGp = "DELETE";
					jrParam.setField("SSTL_NO", szOldStlNo);
				}

				if (!"".equals(szStlNo) && "".equals(szOldStlNo) && "".equals(szFromStlNo)) {
					// 추가처리
					szModGp = "ADD";
					jrParam.setField("SSTL_NO", szStlNo);
				}

				if (!"".equals(szStlNo) && "".equals(szOldStlNo) && szStlNo.equals(szFromStlNo)) {
					// 이동처리
					szModGp = "MOVE";
					jrParam.setField("SSTL_NO", szStlNo);
				}

				if (!szYsStkColGp.equals(szOldYsStkColGp) || !szYsStkBedNo.equals(szOldYsStkBedNo) || !szYsStkLyrNo.equals(szOldYsStkLyrNo) || !szYsStkSeqNo.equals(szOldYsStkSeqNo)) {
					// SEQ변경처리 UP,DOWN
					szModGp = "UPDOWN";
					jrParam.setField("SSTL_NO", szStlNo);
				}

				jrParam.setField("YS_STK_COL_GP", szYsStkColGp);
				jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
				jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
				jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);

				// 제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BILLET공통에서 현재진도코드를 가져온다.
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");

				if (jsTemp != null && jsTemp.size() > 0) {
					jrTemp = jsTemp.getRecord(0);

					szStkStlNo = commUtils.trim(jrTemp.getFieldString("SSTL_NO"));
					szCurrProgCd = commUtils.trim(jrTemp.getFieldString("CURR_PROG_CD"));
					szOrdYeojaeGp = commUtils.trim(jrTemp.getFieldString("ORD_YEOJAE_GP"));
					// szWbookId = commUtils.trim(jrTemp.getFieldString("YD_WBOOK_ID"));
					// szCrnSchId = commUtils.trim(jrTemp.getFieldString("YD_CRN_SCH_ID"));
					// szToLocMtlStat = commUtils.trim(jrTemp.getFieldString("TOLOC_MTL_STAT"));

				} else {
					throw new Exception("제품번호로 저장품 및 작업대상여부 조회시 에러가 발생했습니다!");
				}

				// 저장품에 존재하는 제품번호인지 체크
				if ("ADD".equals(szModGp) || "MOVE".equals(szModGp)) {
					if ("".equals(szStkStlNo)) {
// 26.02.26 사외통합야드 엑셀 기능 추가 -->
//						throw new Exception("제품번호 : " + jrParam.getFieldString("SSTL_NO") + " 가 TB_YS_STOCK 에 없습니다.");
						commDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.insBtYdStock", logId, methodNm, "저장품 등록");
// 26.02.26 사외통합야드 엑셀 기능 추가 <--
					}
				}

				// ------------------------------------------------------------------------------------------
				if ("ADD".equals(szModGp)) {

					// SSTL_NO로 저장위치 조회하여 FROM위치가 존재하면 그 위치에서 SSTL_NO를 Clear 한다.
					jrParam.setField("SSTL_NO", szStlNo);
					jrParam.setField("YD_GP", gdReq.getParam("YD_GP"));

					JDTORecordSet jsStkLyrStlNo = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStrLocRegPda", logId, methodNm, "재료번호로 조회");

					if (jsStkLyrStlNo.size() > 0) {

						String sFromLoc = null;

						for (int mm = 0; mm < jsStkLyrStlNo.size(); mm++) {
							if (!"".equals(jsStkLyrStlNo.getRecord(mm).getFieldString("YD_CRN_SCH_ID"))) {
								// 크레인스케줄 편성 대상이면 에러 메세지를 리턴하고 종료한다.

								sFromLoc = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP") + "-" + jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_BED_NO") + "-"
										+ jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_LYR_NO") + "-" + jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_SEQ_NO");

								throw new Exception("재료번호: " + jsStkLyrStlNo.getRecord(mm).getFieldString("SSTL_NO") + " 는 FROM 위치(" + sFromLoc + ")에서  크레인스케줄에 편성되어 있습니다. 등록 작업을 할 수 없습니다.");
							} else {
								// 작업이력에 남길 From 위치설정를 읽어 온다.
								szFromYsStkColGp = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP");
								szFromYsStkBedNo = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_BED_NO");
								szFromYsStkLyrNo = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_LYR_NO");
								szFromYsStkSeqNo = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_SEQ_NO");
							}
						}
					}
				}
				// ------------------------------------------------------------------------------------------

				// SSTL_NO 로 STKLYR 'C','U','D' 모두 Clear 하기
				jrParam.setField("SSTL_NO", szStlNo);
				// 사외통합야드는 전용 clearstklyr 사용
				commDao.update(jrParam, "com.inisteel.cim.ys.ystot.dao.clearstklyr", logId, methodNm, "모든 SSTL_NO가 있던 위치 Clear");

				if ("UPDOWN".equals(szModGp)) {
					// UP,DOWN 키를 눌러 SEQ 가 변경되었다면 해당 야드맵의 적치단재료상태를 재료번호가 있으면 적치중으로 없으면 적치가능으로 설정한다.

					jrParam.setField("SSTL_NO", szStlNo); // szStlNo 에는 삭제일경우 빈값이 들어있고 그외는 제품번호가 들어 있다.
					jrParam.setField("YD_STK_LYR_ACT_STAT", ""); // "" 값은 이전값을 변경안한다는 의미
					if ("".equals(szStlNo)) {
						jrParam.setField("YD_STK_LYR_MTL_STAT", "E"); // 적치가능
					} else {
						jrParam.setField("YD_STK_LYR_MTL_STAT", "C"); // 적치중
					}
					jrParam.setField("YS_STK_COL_GP", szYsStkColGp);
					jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
					jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
					jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);

					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "적치단정보 수정");

				} else {

					// To 위치 적치단 정보 수정
					jrParam.setField("SSTL_NO", szStlNo); // szStlNo 에는 삭제일경우 빈값이 들어있고 그외는 제품번호가 들어 있다.
					jrParam.setField("YD_STK_LYR_ACT_STAT", ""); // "" 값은 이전값을 변경안한다는 의미
					if ("DELETE".equals(szModGp)) {
						jrParam.setField("YD_STK_LYR_MTL_STAT", "E"); // 적치가능
					} else {
						jrParam.setField("YD_STK_LYR_MTL_STAT", "C"); // 적치중
					}
					jrParam.setField("YS_STK_COL_GP", szYsStkColGp);
					jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
					jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
					jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);

					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "적치단정보 수정");
				}

				// BILLET공통 위치정보 수정하기
				if ("DELETE".equals(szModGp)) {
					// 삭제일경우 처리 ???
					jrParam.setField("FNL_REG_PGM", "updStrLocReg");
					jrParam.setField("YD_GP", "_");
					jrParam.setField("YD_BAY_GP", szYsStkColGp.substring(1, 2));
					jrParam.setField("YD_EQP_GP", szYsStkColGp.substring(2, 4));
					jrParam.setField("YS_STK_COL_NO", szYsStkColGp.substring(4, 6));
					jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
					jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
					jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);
					jrParam.setField("YS_STR_LOC", "_" + szYsStkColGp.substring(1, 6) + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo);
					jrParam.setField("SSTL_NO", szOldStlNo); // 삭제된 번호

					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLoc", logId, methodNm, "BILLET공통 야드저장위치 수정");
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
				} else {
					// 추가, 이동
					jrParam.setField("FNL_REG_PGM", "updStrLocReg");
					jrParam.setField("YD_GP", szYsStkColGp.substring(0, 1));
					jrParam.setField("YD_BAY_GP", szYsStkColGp.substring(1, 2));
					jrParam.setField("YD_EQP_GP", szYsStkColGp.substring(2, 4));
					jrParam.setField("YS_STK_COL_NO", szYsStkColGp.substring(4, 6));
					jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
					jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
					jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);
					jrParam.setField("YS_STR_LOC", szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo);
					jrParam.setField("SSTL_NO", szStlNo);

					if ("Y".equals(szFtmvCarudCmplYn)) {

						// 이송하차완료 처리
						if ("1".equals(szOrdYeojaeGp)) { // 주문재인경우
							jrParam.setField("CURR_PROG_CD", "B");
						} else if ("2".equals(szOrdYeojaeGp)) { // 여재인경우
							jrParam.setField("CURR_PROG_CD", "Y");
						} else {
							jrParam.setField("CURR_PROG_CD", szCurrProgCd);
						}

						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLocProgCd", logId, methodNm, "BILLET공통 야드저장위치,진도코드 수정");

						// 진행관리로 YSPBJ002 전송
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSPBJ002", jrParam));

						// 2)이송지시 테이블 변경
						// - 완료일자,계상일자,STATUS('*') 변경하기
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updFtmvCarudCmpl", logId, methodNm, "이송하차완료 처리");

					} else {
						if ("D".equals(szCurrProgCd) && "1".equals(szOrdYeojaeGp)) {
							// 공통의 진도코드가 'D':이송지시대기 이고 주여구분이 '1':주문재 이면 야드저장품의 재료진도코드를 'B':지시대기 로 변경한다. + 위치정보 수정
							jrParam.setField("CURR_PROG_CD", "B");
							commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLocProgCd", logId, methodNm, "BILLET공통 야드저장위치,진도코드 수정");
						} else {
							commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLoc", logId, methodNm, "BILLET공통 야드저장위치 수정");
							commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
						}
					}
				}

				// 야드저장품 위치정보 수정하기
				if ("DELETE".equals(szModGp)) {
					// 삭제일경우 처리 ???
					jrParam.setField("YS_STK_COL_GP", "_" + szYsStkColGp.substring(1, 6));
					jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
					jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
					jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);
					jrParam.setField("YS_STR_LOC", "_" + szYsStkColGp.substring(1, 6) + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo);
					jrParam.setField("SSTL_NO", szOldStlNo); // 삭제된 번호

					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
				} else {
					// 추가, 이동
					jrParam.setField("YS_STK_COL_GP", szYsStkColGp);
					jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
					jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
					jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);
					jrParam.setField("YS_STR_LOC", szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo);
					jrParam.setField("SSTL_NO", szStlNo);

					if ("D".equals(szCurrProgCd) && "1".equals(szOrdYeojaeGp)) {
						// 공통의 진도코드가 'D':이송지시대기 이고 주여구분이 '1':주문재 이면 야드저장품의 재료진도코드를 'B':지시대기 로 변경한다. + 위치정보 수정
						jrParam.setField("STL_PROG_CD", "B");
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLocProgCd", logId, methodNm, "야드저장품 야드저장위치,재료진도코드 수정");
					} else {
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
					}
				}

				// 이력정보 등록하기
				if ("DELETE".equals(szModGp)) {
					// 삭제일경우
					jrParam.setField("SSTL_NO", szOldStlNo);
					jrParam.setField("YD_GP", szYsStkColGp.substring(0, 1));
					jrParam.setField("YD_SCH_CD", szYsStkColGp.substring(0, 2) + "YD01MM");

					jrParam.setField("YS_UP_WR_LOC", szYsStkColGp + szYsStkBedNo);
					jrParam.setField("YS_UP_WR_LAYER", szYsStkLyrNo);
					jrParam.setField("YS_UP_WR_SEQ_NO", szYsStkSeqNo);

					jrParam.setField("YS_DN_WR_LOC", "");
					jrParam.setField("YS_DN_WR_LAYER", "");
					jrParam.setField("YS_DN_WR_SEQ_NO", "");

				} else if ("ADD".equals(szModGp)) {
					// 추가
					jrParam.setField("SSTL_NO", szStlNo);
					jrParam.setField("YD_GP", szYsStkColGp.substring(0, 1));
					jrParam.setField("YD_SCH_CD", szYsStkColGp.substring(0, 2) + "YD01MM");

					jrParam.setField("YS_UP_WR_LOC", szFromYsStkColGp + szFromYsStkBedNo);
					jrParam.setField("YS_UP_WR_LAYER", szFromYsStkLyrNo);
					jrParam.setField("YS_UP_WR_SEQ_NO", szFromYsStkSeqNo);

					jrParam.setField("YS_DN_WR_LOC", szYsStkColGp + szYsStkBedNo);
					jrParam.setField("YS_DN_WR_LAYER", szYsStkLyrNo);
					jrParam.setField("YS_DN_WR_SEQ_NO", szYsStkSeqNo);

				} else if ("MOVE".equals(szModGp)) {
					// 이동
					jrParam.setField("SSTL_NO", szStlNo);
					jrParam.setField("YD_GP", szYsStkColGp.substring(0, 1));
					jrParam.setField("YD_SCH_CD", szYsStkColGp.substring(0, 2) + "YD01MM");

					jrParam.setField("YS_UP_WR_LOC", szFromYsStkColGp + szFromYsStkBedNo);
					jrParam.setField("YS_UP_WR_LAYER", szFromYsStkLyrNo);
					jrParam.setField("YS_UP_WR_SEQ_NO", szFromYsStkSeqNo);

					jrParam.setField("YS_DN_WR_LOC", szYsStkColGp + szYsStkBedNo);
					jrParam.setField("YS_DN_WR_LAYER", szYsStkLyrNo);
					jrParam.setField("YS_DN_WR_SEQ_NO", szYsStkSeqNo);

				} else {
					// UPDOWN
					jrParam.setField("SSTL_NO", szStlNo);
					jrParam.setField("YD_GP", szYsStkColGp.substring(0, 1));
					jrParam.setField("YD_SCH_CD", szYsStkColGp.substring(0, 2) + "YD01MM");

					jrParam.setField("YS_UP_WR_LOC", szOldYsStkColGp + szOldYsStkBedNo);
					jrParam.setField("YS_UP_WR_LAYER", szOldYsStkLyrNo);
					jrParam.setField("YS_UP_WR_SEQ_NO", szOldYsStkSeqNo);

					jrParam.setField("YS_DN_WR_LOC", szYsStkColGp + szYsStkBedNo);
					jrParam.setField("YS_DN_WR_LAYER", szYsStkLyrNo);
					jrParam.setField("YS_DN_WR_SEQ_NO", szYsStkSeqNo);
				}
				jrParam.setField("YD_SCH_ST_GP", "B"); // 야드스케줄 기동 구분 "B" 로 넣어준다. B:작업자 Backup
				jrParam.setField("YD_AID_WRK_YN", "N"); // 야드보조작업여부 - N:주작업

				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkHistByJsp", logId, methodNm, "화면에의한 이력정보 수정");

				// L2로 재원정보 전문 전송
				if ("DELETE".equals(szModGp)) {
					jrParam.setField("YD_INFO_SYNC_CD", "D"); // 야드정보동기화코드 D:생산종료(삭제)
				} else if ("ADD".equals(szModGp)) {
					jrParam.setField("YD_INFO_SYNC_CD", "A"); // 야드정보동기화코드 A:생산실적
				} else {
					jrParam.setField("YD_INFO_SYNC_CD", "5"); // 야드정보동기화코드 5:지정저장품
				}

				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L202", jrParam));

////////////////////////////////////////////////////////////				
// 2025.08.21 타부분 전송 막음 START				
//				// TO위치가 장입대(TZ)이면 생산통제 소형압연장입진행실적 (YSCUJ032) 전송
//				if ("ADD".equals(szModGp) || "MOVE".equals(szModGp)) {
//
//					if ("TZ".equals(szYsStkColGp.substring(2, 4)) && "01".equals(szYsStkBedNo)) {
//
//						jrParam.setField("CHG_SUP_PROG_STAT", "30");
//						jrParam.setField("SSTL_NO", szStlNo);
//						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSCUJ032Backup", jrParam));
//					}
//				}

//				// 생산통제 빌렛입고실적(YSCUJ038)
//				jrParam.setField("SSTL_NO", szStlNo);
//				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSCUJ038Backup", jrParam));
//
//				
//				// 소형압연L2로 장입이상재 CARRY-OUT 완료 실적을 전송
//				if (("MOVE".equals(szModGp) && "LB".equals(szFromYsStkColGp.substring(2, 4))) || ("DELETE".equals(szModGp) && "LB".equals(szYsStkColGp.substring(2, 4)))) {
//					jrParam.setField("SSTL_NO1", "");
//					jrParam.setField("SSTL_NO2", "");
//					jrParam.setField("SSTL_NO3", "");
//					jrParam.setField("SSTL_NO4", "");
//					jrParam.setField("SSTL_NO5", "");
//					jrParam.setField("SSTL_NO6", "");
//					jrParam.setField("SSTL_NO7", "");
//					jrParam.setField("SSTL_NO8", "");
//					jrParam.setField("SSTL_NO9", "");
//					jrParam.setField("SSTL_NO10", "");
//
//					jrParam.setField("YD_STK_BED_STL_SH", "1");
//					jrParam.setField("YD_EQP_WRK_SH", "1");
//
//					if ("DELETE".equals(szModGp)) {
//						jrParam.setField("SSTL_NO1", szOldStlNo);
//						jrParam.setField("YD_STK_COL_GP", szYsStkColGp);
//						jrParam.setField("YD_STK_BED_NO", szYsStkBedNo);
//					} else {
//						jrParam.setField("SSTL_NO1", szStlNo);
//						jrParam.setField("YD_STK_COL_GP", szFromYsStkColGp);
//						jrParam.setField("YD_STK_BED_NO", szFromYsStkBedNo);
//					}
//
//					// 장입이상재 Carry-out 완료 송신
//					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM5L101BackUp", jrParam));
//				
//				}
// 2025.08.21 타부분 전송 막음 END				
////////////////////////////////////////////////////////////

				// String sApplyYnPI1 = commDao.ApplyYnPI("", methodNm, "APPPI1", "*", "*");

				/*
				 * MES_PI 2022-09-14 이준기 당진공장 내 특수강 이송실적 통계로 송신 USRPDA.SP_SS_PD_MATL_FTMV_WR_MAIN('WBV041018','KD01','KDCS','20220711',:v1)
				 */// PIDEV

				// if("Y".equals(sApplyYnPI1)){

////////////////////////////////////////////////////////////
// 2025.08.21 타부분 처리 막음 START				
//				String fromLoc = "";
//				String toLoc = "";
//				// JDTORecord recordSp = null;
//				int[] inParamIndex = { 1, 2, 3, 4 };
//				String currDt = commUtils.getDateTime14(); // 현재일시(yyyyMMddHHmmss)
//				String iniDate = YsCommUtils.getIniDate(currDt);
//
//				if ("DELETE".equals(szModGp) && "C".equals(szYsStkColGp.substring(1, 2))) {
//
//					commUtils.printLog(logId, "szOldStlNo : " + szOldStlNo, "반입SL");
//
//					fromLoc = "S220";
//					toLoc = "S210";
//					Object[] inParam = { szOldStlNo, fromLoc, toLoc, iniDate };
//					// recordSp = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_PIDEV");
//					commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_PIDEV");
//
//					commUtils.printLog(logId, "sstlNo : " + szOldStlNo, "반입SL");
//					commUtils.printLog(logId, "ydWrkHdsDd : " + iniDate, "반입SL");
//					commUtils.printLog(logId, "fromLoc : " + fromLoc, "반입SL");
//					commUtils.printLog(logId, "toLoc : " + toLoc, "반입SL");
//
//				} else if ("ADD".equals(szModGp) && ("A".equals(szYsStkColGp.substring(1, 2)) || "B".equals(szYsStkColGp.substring(1, 2)))) {
//
//					commUtils.printLog(logId, "szStlNo : " + szStlNo, "입고SL");
//
//					fromLoc = "S210";
//					toLoc = "S220";
//					Object[] inParam = { szStlNo, fromLoc, toLoc, iniDate };
//					// recordSp = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_PIDEV");
//					commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_PIDEV");
//
//					commUtils.printLog(logId, "sstlNo : " + szStlNo, "입고SL");
//					commUtils.printLog(logId, "ydWrkHdsDd : " + iniDate, "입고SL");
//					commUtils.printLog(logId, "fromLoc : " + fromLoc, "입고SL");
//					commUtils.printLog(logId, "toLoc : " + toLoc, "입고SL");
//				}
// 2025.08.21 타부분 처리 막음 END				
////////////////////////////////////////////////////////////
				// }
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updStrLocMod

	/**
	 * 사외통합야드 PDA 저장위치등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord updStrLocReg(GridData gdReq) throws DAOException {
		String methodNm = "사외통합야드 PDA 저장위치등록[YsTotYsJspSeEJB.updStrLocReg] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			String szStlNo = null;
			String szYsStkColGp = null;
			String szYsStkBedNo = null;
			String szYsStkLyrNo = null;
			String szYsStkSeqNo = null;

			String szOldStlNo = null;
			String szOldYsStkColGp = null;
			String szOldYsStkBedNo = null;
			String szOldYsStkLyrNo = null;
			String szOldYsStkSeqNo = null;

			String szFromStlNo = null;
			String szFromYsStkColGp = null;
			String szFromYsStkBedNo = null;
			String szFromYsStkLyrNo = null;
			String szFromYsStkSeqNo = null;

			String szCurrProgCd = null;
			String szOrdYeojaeGp = null;

			String szModGp = null; // 작업구분
			String sApplyYnPI 	= "N";

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			commUtils.printParam("gdReq", commUtils.gridDataTojdtoRecord(gdReq));

			szStlNo = commUtils.trim(gdReq.getParam("SSTL_NO"));
			szYsStkColGp = commUtils.trim(gdReq.getParam("YS_STK_COL_GP"));
			szYsStkBedNo = commUtils.trim(gdReq.getParam("YS_STK_BED_NO"));
			szYsStkLyrNo = commUtils.trim(gdReq.getParam("YS_STK_LYR_NO"));
			szYsStkSeqNo = commUtils.trim(gdReq.getParam("YS_STK_SEQ_NO"));
			szOldStlNo = commUtils.trim(gdReq.getParam("OLD_SSTL_NO"));
			szOldYsStkColGp = commUtils.trim(gdReq.getParam("OLD_YS_STK_COL_GP"));
			szOldYsStkBedNo = commUtils.trim(gdReq.getParam("OLD_YS_STK_BED_NO"));
			szOldYsStkLyrNo = commUtils.trim(gdReq.getParam("OLD_YS_STK_LYR_NO"));
			szOldYsStkSeqNo = commUtils.trim(gdReq.getParam("OLD_YS_STK_SEQ_NO"));
			szFromStlNo = commUtils.trim(gdReq.getParam("FROM_SSTL_NO"));
			szFromYsStkColGp = commUtils.trim(gdReq.getParam("FROM_YS_STK_COL_GP"));
			szFromYsStkBedNo = commUtils.trim(gdReq.getParam("FROM_YS_STK_BED_NO"));
			szFromYsStkLyrNo = commUtils.trim(gdReq.getParam("FROM_YS_STK_LYR_NO"));
			szFromYsStkSeqNo = commUtils.trim(gdReq.getParam("FROM_YS_STK_SEQ_NO"));

			if ("".equals(szStlNo) && !"".equals(szOldStlNo)) {
				// 삭제처리
				szModGp = "DELETE";
				jrParam.setField("SSTL_NO", szOldStlNo);
			} else if (!"".equals(szStlNo) && "".equals(szOldStlNo) && "".equals(szFromStlNo)) {
				// 추가처리
				szModGp = "ADD";
				jrParam.setField("SSTL_NO", szStlNo);
			} else if (!"".equals(szStlNo) && "".equals(szOldStlNo) && szStlNo.equals(szFromStlNo)) {
				// 이동처리
				szModGp = "MOVE";
				jrParam.setField("SSTL_NO", szStlNo);
			} else if (!szYsStkColGp.equals(szOldYsStkColGp) || !szYsStkBedNo.equals(szOldYsStkBedNo) || !szYsStkLyrNo.equals(szOldYsStkLyrNo) || !szYsStkSeqNo.equals(szOldYsStkSeqNo)) {
				// SEQ변경처리 UP,DOWN
				szModGp = "UPDOWN";
				jrParam.setField("SSTL_NO", szStlNo);
			}

			commUtils.printLog(logId, methodNm, "szModGp:" + szModGp);

			jrParam.setField("YS_STK_COL_GP", szYsStkColGp);
			jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
			jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
			jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);

			/* 사외통합야드는 소형 제품 및 스크랩등 기타 무정보 제품 때문에 제품 기준정보 확인은 건너 띈다. */
//			// 제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BILLET공통에서 현재진도코드를 가져온다.
//			jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");
//
//			if (jsTemp != null && jsTemp.size() > 0) {
//				jrTemp = jsTemp.getRecord(0);
//
//				szStkStlNo = commUtils.trim(jrTemp.getFieldString("SSTL_NO"));
//				szCurrProgCd = commUtils.trim(jrTemp.getFieldString("CURR_PROG_CD"));
//				szOrdYeojaeGp = commUtils.trim(jrTemp.getFieldString("ORD_YEOJAE_GP"));
//
//			} else {
//				throw new Exception("제품번호로 저장품 및 작업대상여부 조회시 에러가 발생했습니다!");
//			}
//
//			// 저장품에 존재하는 제품번호인지 체크
//			if ("ADD".equals(szModGp) || "MOVE".equals(szModGp)) {
//				if ("".equals(szStkStlNo)) {
//					throw new Exception("제품번호 : " + jrParam.getFieldString("SSTL_NO") + " 가 TB_YS_STOCK 에 없습니다.");
//				}
//			}

			// ------------------------------------------------------------------------------------------
			if ("ADD".equals(szModGp)) {

				// SSTL_NO로 저장위치 조회하여 FROM위치가 존재하면 그 위치에서 SSTL_NO를 Clear 한다.
				jrParam.setField("SSTL_NO", szStlNo);
				jrParam.setField("YD_GP", gdReq.getParam("YD_GP"));

				JDTORecordSet jsStkLyrStlNo = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStrLocRegPda", logId, methodNm, "재료번호로 조회");

				if (jsStkLyrStlNo.size() > 0) {
					for (int mm = 0; mm < jsStkLyrStlNo.size(); mm++) {
						szFromYsStkColGp = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP");
						szFromYsStkBedNo = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_BED_NO");
						szFromYsStkLyrNo = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_LYR_NO");
						szFromYsStkSeqNo = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_SEQ_NO");
					}
				}
			}
			// ------------------------------------------------------------------------------------------

			// SSTL_NO 로 STKLYR 'C','U','D' 모두 Clear 하기
			jrParam.setField("SSTL_NO", szStlNo);
			// 사외통합야드는 전용 clearstklyr 사용
			commDao.update(jrParam, "com.inisteel.cim.ys.ystot.dao.clearstklyr", logId, methodNm, "모든 SSTL_NO가 있던 위치 Clear");

			if ("UPDOWN".equals(szModGp)) {
				// UP,DOWN 키를 눌러 SEQ 가 변경되었다면 해당 야드맵의 적치단재료상태를 재료번호가 있으면 적치중으로 없으면 적치가능으로 설정한다.

				jrParam.setField("SSTL_NO", szStlNo); // szStlNo 에는 삭제일경우 빈값이 들어있고 그외는 제품번호가 들어 있다.
				jrParam.setField("YD_STK_LYR_ACT_STAT", ""); // "" 값은 이전값을 변경안한다는 의미
				if ("".equals(szStlNo)) {
					jrParam.setField("YD_STK_LYR_MTL_STAT", "E"); // 적치가능
				} else {
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C"); // 적치중
				}
				jrParam.setField("YS_STK_COL_GP", szYsStkColGp);
				jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
				jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
				jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);

				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "적치단정보 수정");

			} else {

				// To 위치 적치단 정보 수정
				jrParam.setField("SSTL_NO", szStlNo); // szStlNo 에는 삭제일경우 빈값이 들어있고 그외는 제품번호가 들어 있다.
				jrParam.setField("YD_STK_LYR_ACT_STAT", ""); // "" 값은 이전값을 변경안한다는 의미
				if ("DELETE".equals(szModGp)) {
					jrParam.setField("YD_STK_LYR_MTL_STAT", "E"); // 적치가능
				} else {
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C"); // 적치중
				}
				jrParam.setField("YS_STK_COL_GP", szYsStkColGp);
				jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
				jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
				jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);

				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "적치단정보 수정");
			}

			// BILLET공통 위치정보 수정하기
			if ("DELETE".equals(szModGp)) {
				// 삭제일경우 처리 ???
				jrParam.setField("FNL_REG_PGM", "updStrLocReg");
				jrParam.setField("YD_GP", "_");
				jrParam.setField("YD_BAY_GP", szYsStkColGp.substring(1, 2));
				jrParam.setField("YD_EQP_GP", szYsStkColGp.substring(2, 4));
				jrParam.setField("YS_STK_COL_NO", szYsStkColGp.substring(4, 6));
				jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
				jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
				jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);
				jrParam.setField("YS_STR_LOC", "_" + szYsStkColGp.substring(1, 6) + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo);
				jrParam.setField("SSTL_NO", szOldStlNo); // 삭제된 번호

				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLoc", logId, methodNm, "BILLET공통 야드저장위치 수정");
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
			} else {
				// 추가, 이동
				jrParam.setField("FNL_REG_PGM", "updStrLocReg");
				jrParam.setField("YD_GP", szYsStkColGp.substring(0, 1));
				jrParam.setField("YD_BAY_GP", szYsStkColGp.substring(1, 2));
				jrParam.setField("YD_EQP_GP", szYsStkColGp.substring(2, 4));
				jrParam.setField("YS_STK_COL_NO", szYsStkColGp.substring(4, 6));
				jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
				jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
				jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);
				jrParam.setField("YS_STR_LOC", szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo);
				jrParam.setField("SSTL_NO", szStlNo);

				if ("D".equals(szCurrProgCd) && "1".equals(szOrdYeojaeGp)) {
					// 공통의 진도코드가 'D':이송지시대기 이고 주여구분이 '1':주문재 이면 야드저장품의 재료진도코드를 'B':지시대기 로 변경한다. + 위치정보 수정
					jrParam.setField("CURR_PROG_CD", "B");
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLocProgCd", logId, methodNm, "BILLET공통 야드저장위치,진도코드 수정");
				} else {
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLoc", logId, methodNm, "BILLET공통 야드저장위치 수정");
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
				}

			}

			// 야드저장품 위치정보 수정하기
			if ("DELETE".equals(szModGp)) {
				// 삭제일경우 처리 ???
				jrParam.setField("YS_STK_COL_GP", "_" + szYsStkColGp.substring(1, 6));
				jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
				jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
				jrParam.setField("YS_STK_SEQ_NO", String.valueOf(Integer.parseInt(szYsStkSeqNo)));
				jrParam.setField("YS_STR_LOC", "_" + szYsStkColGp.substring(1, 6) + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo);
				jrParam.setField("SSTL_NO", szOldStlNo); // 삭제된 번호

				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
			} else {
				// 추가, 이동
				jrParam.setField("YS_STK_COL_GP", szYsStkColGp);
				jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
				jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
				jrParam.setField("YS_STK_SEQ_NO", String.valueOf(Integer.parseInt(szYsStkSeqNo)));
				jrParam.setField("YS_STR_LOC", szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo);
				jrParam.setField("SSTL_NO", szStlNo);

				if ("D".equals(szCurrProgCd) && "1".equals(szOrdYeojaeGp)) {
					// 공통의 진도코드가 'D':이송지시대기 이고 주여구분이 '1':주문재 이면 야드저장품의 재료진도코드를 'B':지시대기 로 변경한다. + 위치정보 수정
					jrParam.setField("STL_PROG_CD", "B");
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLocProgCd", logId, methodNm, "야드저장품 야드저장위치,재료진도코드 수정");
				} else {
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
				}
			}

			// 이력정보 등록하기
			if ("DELETE".equals(szModGp)) {
				// 삭제일경우
				jrParam.setField("SSTL_NO", szOldStlNo);
				jrParam.setField("YD_GP", szYsStkColGp.substring(0, 1));
				jrParam.setField("YD_SCH_CD", szYsStkColGp.substring(0, 2) + "YD01MM");

				jrParam.setField("YS_UP_WR_LOC", szYsStkColGp + szYsStkBedNo);
				jrParam.setField("YS_UP_WR_LAYER", szYsStkLyrNo);
				jrParam.setField("YS_UP_WR_SEQ_NO", String.valueOf(Integer.parseInt(szYsStkSeqNo)));

				jrParam.setField("YS_DN_WR_LOC", "");
				jrParam.setField("YS_DN_WR_LAYER", "");
				jrParam.setField("YS_DN_WR_SEQ_NO", "");

			} else

			if ("ADD".equals(szModGp)) {
				// 추가
				jrParam.setField("SSTL_NO", szStlNo);
				jrParam.setField("YD_GP", szYsStkColGp.substring(0, 1));
				jrParam.setField("YD_SCH_CD", szYsStkColGp.substring(0, 2) + "YD01MM");

				jrParam.setField("YS_UP_WR_LOC", szFromYsStkColGp + szFromYsStkBedNo);
				jrParam.setField("YS_UP_WR_LAYER", szFromYsStkLyrNo);
				jrParam.setField("YS_UP_WR_SEQ_NO", szFromYsStkSeqNo);

				jrParam.setField("YS_DN_WR_LOC", szYsStkColGp + szYsStkBedNo);
				jrParam.setField("YS_DN_WR_LAYER", szYsStkLyrNo);
				jrParam.setField("YS_DN_WR_SEQ_NO", String.valueOf(Integer.parseInt(szYsStkSeqNo)));

			} else if ("MOVE".equals(szModGp)) {
				// 이동
				jrParam.setField("SSTL_NO", szStlNo);
				jrParam.setField("YD_GP", szYsStkColGp.substring(0, 1));
				jrParam.setField("YD_SCH_CD", szYsStkColGp.substring(0, 2) + "YD01MM");

				jrParam.setField("YS_UP_WR_LOC", szFromYsStkColGp + szFromYsStkBedNo);
				jrParam.setField("YS_UP_WR_LAYER", szFromYsStkLyrNo);
				jrParam.setField("YS_UP_WR_SEQ_NO", szFromYsStkSeqNo);

				jrParam.setField("YS_DN_WR_LOC", szYsStkColGp + szYsStkBedNo);
				jrParam.setField("YS_DN_WR_LAYER", szYsStkLyrNo);
				jrParam.setField("YS_DN_WR_SEQ_NO", String.valueOf(Integer.parseInt(szYsStkSeqNo)));

			} else {
				// UPDOWN
				jrParam.setField("SSTL_NO", szStlNo);
				jrParam.setField("YD_GP", szYsStkColGp.substring(0, 1));
				jrParam.setField("YD_SCH_CD", szYsStkColGp.substring(0, 2) + "YD01MM");

				jrParam.setField("YS_UP_WR_LOC", szOldYsStkColGp + szOldYsStkBedNo);
				jrParam.setField("YS_UP_WR_LAYER", szOldYsStkLyrNo);
				jrParam.setField("YS_UP_WR_SEQ_NO", szOldYsStkSeqNo);

				jrParam.setField("YS_DN_WR_LOC", szYsStkColGp + szYsStkBedNo);
				jrParam.setField("YS_DN_WR_LAYER", szYsStkLyrNo);
				jrParam.setField("YS_DN_WR_SEQ_NO", String.valueOf(Integer.parseInt(szYsStkSeqNo)));
			}
			
			jrParam.setField("YD_SCH_ST_GP", "B"); // 야드스케줄 기동 구분 "B" 로 넣어준다. B:작업자 Backup
			jrParam.setField("YD_AID_WRK_YN", "N"); // 야드보조작업여부 - N:주작업

			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkHistByJsp", logId, methodNm, "PDA 저장위치등록 으로 인한 이력정보 수정");
			
			// 인천 트렉슈 제품 처리 - 제품 신규 추가 인 경우
			JDTORecordSet jsTemp = null;
			String fromLoc = "S210";
			String toLoc   = "G342";
			String  currDt      = commUtils.getDateTime14(); //현재일시(yyyyMMddHHmmss)
			String	iniDate     = YsCommUtils.getIniDate(currDt); 
			
			sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "GH9999", "001", "*");
			if("Y".equals(sApplyYnPI)){
				commUtils.printLog(logId, methodNm, "인천트렉슈처리 테스트 시작");
				
				JDTORecord jrParam2 = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				jrParam2.setResultCode(logId); // Log ID
				jrParam2.setResultMsg(methodNm); // Log Method Name
				jrParam2.setField("SSTL_NO", szStlNo);

				jsTemp = commDao.select(jrParam2, "com.inisteel.cim.ys.ystot.dao.selMatUsgIBltInfo", logId, methodNm, "인천 트렉슈 제품 정보 조회 (사외통합야드 PDA용)");
				
				if (jsTemp != null && jsTemp.size() > 0) {
					int[] inParamIndex = {1,2,3,4};

					Object[] inParam = {szStlNo, fromLoc, toLoc, iniDate};	
					commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_PIDEV");

					commUtils.printLog(logId, "sstlNo : "+ szStlNo, "반입SL");
					commUtils.printLog(logId, "ydWrkHdsDd : "+iniDate, "반입SL");
					commUtils.printLog(logId, "fromLoc : "+fromLoc, "반입SL");
					commUtils.printLog(logId, "toLoc : "+toLoc, "반입SL");		
				}

				commUtils.printLog(logId, methodNm, "인천트렉슈처리 테스트 완료");
			}
			
			sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "GH0010", "001", "*");
			if("Y".equals(sApplyYnPI)){
				commUtils.printLog(logId, methodNm, "인천트렉슈처리 시작");
				
				if ("ADD".equals(szModGp)) {
					JDTORecord jrParam2 = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
					jrParam2.setResultCode(logId); // Log ID
					jrParam2.setResultMsg(methodNm); // Log Method Name
					jrParam2.setField("SSTL_NO", szStlNo);

					jsTemp = commDao.select(jrParam2, "com.inisteel.cim.ys.ystot.dao.selMatUsgIBltInfo", logId, methodNm, "인천 트렉슈 제품 정보 조회 (사외통합야드 PDA용)");
					
					if (jsTemp != null && jsTemp.size() > 0) {
						int[] inParamIndex = {1,2,3,4};

						Object[] inParam = {szStlNo, fromLoc, toLoc, iniDate};	
						commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_PIDEV");

						commUtils.printLog(logId, "sstlNo : "+szStlNo, "반입SL");
						commUtils.printLog(logId, "ydWrkHdsDd : "+iniDate, "반입SL");
						commUtils.printLog(logId, "fromLoc : "+fromLoc, "반입SL");
						commUtils.printLog(logId, "toLoc : "+toLoc, "반입SL");		
					}
				}
				
				commUtils.printLog(logId, methodNm, "인천트렉슈처리 종료");
			}
			

			commUtils.printLog(logId, methodNm, "S-");
			//
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updStrLocReg
	
	/**
	 * 재료 지정 등록/해제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updStockAgsnReg(GridData gdReq) throws DAOException {
		String methodNm = "재료 지정 등록/해제[YsTotYsJspSeEJB.updStockAgsnReg(GridData)]";
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
	 * GridData - 차량상차정보조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getgdsCarldInfoInqjl(JDTORecord recPara) throws DAOException {
		String methodNm = "차량상차정보조회 [YsTotYsJspSeEJB.getgdsCarldInfoInqjl] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();
		String szCarProgStat = null;
		JDTORecordSet jsTcar = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		try {
			commUtils.printLog(logId, methodNm, "S+", recPara);
			commUtils.printParam("recPara", recPara);

//			recPara.setField("CAR_NO", commUtils.nvl(recPara.getFieldString("CAR_NO"), ""));
//			recPara.setField("TRN_EQP_CD", commUtils.nvl(recPara.getFieldString("CAR_NO"), ""));
//
//			// 기본정보조회
//			JDTORecordSet jsCrn = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCarSch", logId, methodNm, "TB_YS_CARSCH");
//			if (jsCrn == null || jsCrn.size() <= 0) {
//				throw new Exception("차량스케줄에서 조회시 에러..스케줄 정보가 존재하지 않습니다.");
//			}
//
//			JDTORecord jrCrn = jsCrn.getRecord(0);

			// 차량 진행 상태 코드 값이 '1','2',(상차출발, 상차도착) 인 경우
			szCarProgStat = commUtils.trim(recPara.getFieldString("YD_CAR_PROG_STAT")); // 야드작업진행상태

			// ******************************
			// 2개의 쿼리는 컬럼명을 동일하게 하여 읽어올수 있도록 한다.
			// ******************************
			if ("1".equals(szCarProgStat) || "2".equals(szCarProgStat)) {
				// 차량 스케줄에 상차 작업예약 ID 로 작업예약 재료 정보 조회를 한다.
				if (commUtils.trim(recPara.getFieldString("YD_CARLD_WRK_BOOK_ID")).equals("")) {
					// throw new Exception("작업예약 ID가 없습니다( 차량진도코드가 : 1, 2 경우)");
					commUtils.printLog(logId, methodNm, "S-");
					return jsTcar;
				} else {
					jsTcar = commDao.select(recPara, getCarldInfoInqjlByYdWrkBook, logId, methodNm, "차량번호로 작업예약 조회");
				}
			} else {
				// 차량 진행 상태 코드값이 그 이외인 경우 는 차량 이송재료 정보를 읽어온다.
				recPara.setField("YD_CAR_SCH_ID", commUtils.trim(recPara.getFieldString("YD_CAR_SCH_ID")));
				jsTcar = commDao.select(recPara, getCarldInfoInqjlByCarFtmvMtl, logId, methodNm, "차량 스케줄번호로 차량이송재료 조회");
			}

			if (jsTcar == null || jsTcar.size() == 0) {
			}
			// 데이터 존재시 첫번째 레코드 위치에 차량진도코드를 보내준다.
			// jsTcar.first();
			// recCarProgStat = jsTcar.getRecord(0);
			// recCarProgStat.setField("CAR_PROG_STAT", szCarProgStat);
			commUtils.printLog(logId, methodNm, "S-");

			return jsTcar;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}// end of getgdsCarldInfoInqjl
	
	
	/**
	 * [A] 오퍼레이션명 : 차량상차정보 조회 - 차상위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updgdsCarldInfoInqjl(GridData gdReq) throws DAOException {
		String methodNm = "차량상차정보 조회 - 차상위치 수정[YsTotYsJspSeEJB.updgdsCarldInfoInqjl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					// 차량상세 수정
					jrParam.setField("SSTL_NO", commUtils.getValue(gdReq, "SSTL_NO", ii));
					jrParam.setField("YD_CAR_SCH_ID", commUtils.getValue(gdReq, "YD_CAR_SCH_ID", ii));
					jrParam.setField("YS_STK_COL_GP", commUtils.getValue(gdReq, "YS_STK_COL_GP", ii));
					jrParam.setField("YS_STK_BED_NO", commUtils.getValue(gdReq, "YS_STK_BED_NO", ii)); // 차상위치
					jrParam.setField("YS_STK_LYR_NO", commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii)); // 단
					
					commUtils.printParam("jrParam" + Integer.toString(ii), jrParam);

					// 차량재료정보 수정
					commDao.update(jrParam, udtWhsPlnInfojlByCarFtmvMtl, logId, methodNm, "TB_YS_CARFTMVMTL");

					// 기존위치 CLEAR
					commDao.update(jrParam, udtWhsPlnInfojlByStkLyrLn, logId, methodNm, "TB_YS_STKLYR");

					// 차량위치 등록
					commDao.update(jrParam, udtWhsPlnInfojlByCarStkLyr, logId, methodNm, "TB_YS_STKLYR");

					// 차량재료정보 수정
					commDao.update(jrParam, udtWhsPlnInfojlByCarFtmvMtl, logId, methodNm, "TB_YS_CARFTMVMTL");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 이송Lot등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regFtmvLot(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "이송Lot등록[YsTotYsJspSeEJB.regFtmvLot] < ";
		String logId = commUtils.getLogId();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq[0].getFieldString("userid")));
			String ydPrepSchId = commDao.getSeqId(logId, methodNm, "PrepSch");
			
			if ("".equals(ydPrepSchId)) {
				throw new Exception("준비스케쥴ID 생성 실패");
			}			

			jrParam.setField("YD_PREP_SCH_ID", ydPrepSchId); //야드준비스케쥴ID
			jrParam.setField("YD_SCH_CD",      gdReq[0].getField("YD_SCH_CD")); //스케줄코드
			jrParam.setField("YD_PREP_WK_ST", commUtils.nvl(gdReq[0].getField("YD_PREP_WK_ST"),"")); //야드준비작업상태 
			
			for (int ii = 0; ii < gdReq.length; ii++) {
				
				//준비재료 등록
				jrParam.setField("SSTL_NO"			, commUtils.nvl(gdReq[ii].getFieldString("SSTL_NO"), "")); 
				jrParam.setField("YS_STK_COL_GP"	, commUtils.nvl(gdReq[ii].getFieldString("YD_STR_LOC"), "").substring(0,6)); 
				jrParam.setField("YS_STK_BED_NO"	, commUtils.nvl(gdReq[ii].getFieldString("YD_STR_LOC"), "").substring(7,8)); 
				jrParam.setField("YS_STK_LYR_NO"	, commUtils.nvl(gdReq[ii].getFieldString("YS_STK_LYR_NO"), "")); 
				jrParam.setField("YS_STK_SEQ_NO"	, commUtils.nvl(gdReq[ii].getFieldString("YS_STK_SEQ_NO"), "")); 
				
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepMtl", logId, methodNm, "준비재료 등록");
			}
			
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepSch", logId, methodNm, "준비스케줄 등록");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of regFtmvLot
	
	/**
	 * 차량작업관리 > 차량Point작업현황 - 차량초기화
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord initMvCarSchMgt(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "이송차량스케줄 초기화[YsTotYsJspSeEJB.initMvCarSchMgt] < ";
		String logId = gdReq[0].getRequestUserIp();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			String ydCarSchId  		= null;
			String trnEqpCd    		= null;
			String ysStkColGp 		= null;  
			String ydCarpntCd 		= null;  
			
			String WLOC_CD			= null;
			String YD_PNT_CD		= null;
			
	    	int				intLevLocGp     	    = 0;
	    	int 			intRtnVal				= 0;
	    	String			szMsg					= null;
	    	String 			szYD_CARLD_STOP_LOC		= null;
	    	
			JDTORecordSet 	rsStkCol 				= JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord 		recInTemp 				= JDTORecordFactory.getInstance().create();
	    	JDTORecord		recOutTemp				= JDTORecordFactory.getInstance().create();
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq[0].getFieldString("userId")));

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			String ydGp		 	= commUtils.trim(gdReq[0].getFieldString("YD_GP"));
			
			for (int ii = 0; ii < gdReq.length; ii++) {
				
				ydCarSchId		= commUtils.nvl(gdReq[ii].getFieldString("YD_CAR_SCH_ID"), "");
				trnEqpCd		= commUtils.nvl(gdReq[ii].getFieldString("TRN_EQP_CD"), "");
				ysStkColGp 		= commUtils.nvl(gdReq[ii].getFieldString("YS_STK_COL_GP"), "");
				ydCarpntCd		= commUtils.nvl(gdReq[ii].getFieldString("YD_CARPNT_CD"), "");
				WLOC_CD			= commUtils.nvl(gdReq[ii].getFieldString("WLOC_CD"), "");
				YD_PNT_CD 		= commUtils.nvl(gdReq[ii].getFieldString("YD_PNT_CD"), "");
				
				/**********************************************************
		    	 * 5.출발지 적치열 베드/단 정보 체크
		    	 **********************************************************/			
		    	recInTemp.setField("WLOC_CD",   WLOC_CD);
		    	recInTemp.setField("YD_PNT_CD", YD_PNT_CD);

		    	rsStkCol = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCd", logId, methodNm, "적치열 조회"); 
		    	intLevLocGp = rsStkCol.size();	    	
		    	if (rsStkCol == null || intLevLocGp == 0) {
		    		szMsg= "[" + methodNm + "] 발지개소["+WLOC_CD+"] 및 포인트 코드["+YD_PNT_CD+"]가 타공정코드가 아니고 대기장입니다.";
		    		commUtils.printLog(logId, szMsg, "SL");
		    	}
		    	
				/**********************************************************
		    	 * 6.출발지 정보 CLEAR / 비활성화 상태(YD_STK_COL_ACT_STAT = C)로 업데이트
		    	 **********************************************************/
		    	if(intLevLocGp > 0) {
		    		
		    		rsStkCol.absolute(1);
			    	recOutTemp.setRecord(rsStkCol.getRecord());		    	
			    	szYD_CARLD_STOP_LOC     	= commUtils.trim(recOutTemp.getFieldString("YS_STK_COL_GP")); 
			    	String szCOL_TRN_EQP_CD   	= commUtils.trim(recOutTemp.getFieldString("TRN_EQP_CD")); 		    	
			    	szMsg = "[" + methodNm + "] 발지개소코드["+WLOC_CD+"], " +
			    			"발지개소POINT코드["+YD_PNT_CD+"]로 야드에서 관리되는 적치열구분[출발지:"+szYD_CARLD_STOP_LOC+"]이 존재합니다.";
			    	commUtils.printLog(logId, szMsg, "SL");
					
					/**********************************************************
			    	 * 6-1.(적치열의 운송코드 = 전문 운송코드) -> 맵 Clear
			    	 **********************************************************/
					if( szCOL_TRN_EQP_CD.equals(trnEqpCd))	{					
						szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CARLD_STOP_LOC+"]의 운송장비코드["+szCOL_TRN_EQP_CD+"]와 전문의 운송장비코드["+trnEqpCd+"]가 같으므로 맵 Clear 시작 ";
						commUtils.printLog(logId, szMsg, "SL");
						
						/**********************************************************
				    	 * 6-1-1. 출발야드 적치열 -> 비활성상태(C) 로 업데이트
				    	 **********************************************************/
						szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CARLD_STOP_LOC+"]을 비활성상태로 변경처리 시작 ";
						commUtils.printLog(logId, szMsg, "SL");
						
				    	recInTemp.setField("YS_STK_COL_GP",        szYD_CARLD_STOP_LOC);
				    	recInTemp.setField("YD_STK_COL_ACT_STAT",  "C");
				    	recInTemp.setField("YD_CAR_USE_GP",        "");
				    	recInTemp.setField("TRN_EQP_CD",           "");
				    	recInTemp.setField("CAR_NO",               "");
				    	recInTemp.setField("CARD_NO",              "");
				    	recInTemp.setField("MODIFIER", 			   commUtils.trim(gdReq[0].getFieldString("userid")));
				    	
				    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStatClear", logId, methodNm, "TB_YS_STKCOL 등록");
						if(intRtnVal <= 0) {
							szMsg="[" + methodNm + "] 적치열[" + szYD_CARLD_STOP_LOC + "]활성화중 ERROR 발생.";
							commUtils.printLog(logId, methodNm, "SL");
							m_ctx.setRollbackOnly();
							throw new DAOException(szMsg);
						}
					
						/**********************************************************
				    	 * 6-1-2. 차량포인트통합관리 
				    	 **********************************************************/
						//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
						//YsCarPointinforeg2("2","","",szYD_CARLD_STOP_LOC,"","","C",logId,methodNm);

						recInTemp.setField("STAT", "C");
						recInTemp.setField("YS_STK_COL_GP", szYD_CARLD_STOP_LOC);
						
						intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointstackcolgpupdateCT", logId, methodNm, "저장위치로 초기화 하는 경우(구내운송)");
						if(intRtnVal <= 0) {
							szMsg="저장위치로 차량포인트 초기화중 ERROR 발생.";
							commUtils.printLog(logId, methodNm, "SL");
							throw new DAOException(szMsg);
						}
						
						 // 적치베드 비활성상태로 변경
						/**********************************************************
				    	 * 6-1-3. 출발야드 적치베드 -> 야드적치베드활성상태(=C(비활성상태), YD_STK_BED_ACT_STAT) 
				    	 *                         및 BED중량MAX(=기본값, YD_STK_BED_WT_MAX) 으로 업데이트
				    	 **********************************************************/
						szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CARLD_STOP_LOC+"]의 베드를 비활성상태와 BED중량MAX기본값으로 변경처리 시작 ";
						commUtils.printLog(logId, szMsg, "SL");
						
						recInTemp.setField("YD_STK_BED_WT_MAX", YsConstant.YD_STK_BED_WT_MAX_DEFAULT); // YsConstant
						recInTemp.setField("YS_STK_COL_GP", szYD_CARLD_STOP_LOC);
						recInTemp.setField("YD_STK_BED_ACT_STAT", "C");
						
						intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp", logId, methodNm, "TB_YS_STKBED 등록");
						if(intRtnVal <= 0) {
							szMsg="[" + methodNm + "] 적치BED[" + szYD_CARLD_STOP_LOC + "]활성화중 ERROR 발생.";
							commUtils.printLog(logId, methodNm, "SL");
							throw new DAOException(szMsg);
						}
						
						/**********************************************************
				    	 * 6-1-4. 출발야드 적치단 -> 야드적치단활성상태(=C(비활성상태), YD_STK_LYR_ACT_STAT) 
				    	 *                       및 야드적치단재료상태(=E(적치가능), YD_STK_LYR_MTL_STAT) 로 업데이트
				    	 **********************************************************/
						szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CARLD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 시작 ";
						commUtils.printLog(logId, methodNm, "SL");
						
						recInTemp.setResultCode(logId);	//Log ID
						recInTemp.setResultMsg(methodNm);	//Log Method Name
						recInTemp.setField("YS_STK_COL_GP", szYD_CARLD_STOP_LOC);
						recInTemp.setField("YD_STK_LYR_ACT_STAT", "C");
						recInTemp.setField("SSTL_NO", "");
						recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
						
						intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "TB_YS_STKLYR 등록");
						if(intRtnVal <= 0) {
							szMsg="[" + methodNm + "] 적치단[" + szYD_CARLD_STOP_LOC + "]활성화중 ERROR 발생.";
							commUtils.printLog(logId, szMsg, "SL");
							throw new DAOException(szMsg);
						}
						
						/**********************************************************
				    	 * 6-1-5. 차량 출발 시 상차지 저장위치 제원 야드 L2 로 전송
				    	 *          야드BLOOM:N1,BILLET:N2,선재:N3,봉강:N4,선재자동화:N5,봉강자동화:N6
				    	 *          YSN8L001 저장위치제원
				    	 *          YSN8L002 저장품제원
				    	 *          YSN8L003 크레인작업지시
				    	 *          YSN8L004 크레인작업실적응답
				    	 **********************************************************/
						// 사외통합야드는 L2 전송 기능 없음
//						String	szJMS_TC_CD = "YSN8L001";
//			    		recInTemp.setField("MSG_ID"			,    szJMS_TC_CD);
//						recInTemp.setField("YD_INFO_SYNC_CD", "3");						//1:동,2:SPAN,3:열,4:BED
//						recInTemp.setField("YD_GP"			, szYD_CARLD_STOP_LOC.substring(0, 1));
//						recInTemp.setField("YS_STK_COL_GP"	, szYD_CARLD_STOP_LOC);
//						recInTemp.setField("YD_CAR_PROG_STAT", "1");
//						recInTemp.setField("YD_EQP_WRK_STAT" , "U");
//						szMsg = "[" + methodNm + "] 공차출발시 시 저장위치 제원 야드L2로 전송";
//						
//						//전송 Data 생성
//						jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2(szJMS_TC_CD, recInTemp));
					}
		    	}
				
				/**********************************************************
				* 2. 기존 이송차량스케줄/재료 삭제
				**********************************************************/
				jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
				jrParam.setField("MODIFIER", commUtils.trim(gdReq[0].getFieldString("userid")));

				//이송차량재료 초기화
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSchMtl", logId, methodNm, "이송차량재료 초기화");

				//이송차량스케줄 초기화
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch", logId, methodNm, "이송차량스케줄 초기화");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of initMvCarSchMgt
	
	/**
	 * 차량작업관리 > 차량Point작업현황 - Point개폐
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procPntUnit(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-포인트 사용 등록[EbtYsJspSeEJB.procPntUnit] < ";
		String logId = gdReq[0].getRequestUserIp();
		String szMsg = null;
		String szMethodName = "procPntUnit";
		String szYS_STK_COL_GP		= null;
		String szYD_STK_COL_ACT_STAT= null;
		String szOLD_YD_STK_COL_ACT_STAT= null;
		String szJMS_TC_CD  = null; 
		JDTORecord recInTemp = null;
		JDTORecord recInTemp1 = null;
		boolean isSendable				= true;
		try {
			commUtils.printLog(logId, methodNm, "S+");
			//Return Value
			JDTORecord recOutTemp = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq[0].getFieldString("userid")));
			JDTORecordSet rsStkCol = JDTORecordFactory.getInstance().createRecordSet("");	
			JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
			
			for (int ii = 0; ii < gdReq.length; ii++) {
				
				szYS_STK_COL_GP 		= commUtils.nvl(gdReq[ii].getFieldString("YS_STK_COL_GP"), "");
				szYD_STK_COL_ACT_STAT	= commUtils.nvl(gdReq[ii].getFieldString("YD_STK_COL_ACT_STAT"), "");

    			recOutTemp = JDTORecordFactory.getInstance().create();
    			jrParam.setField("YS_STK_COL_GP", szYS_STK_COL_GP);
    			jrParam.setField("YD_STK_COL_ACT_STAT", szYD_STK_COL_ACT_STAT);
    			jrParam.setField("MODIFIER", 	szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
    	    	
    	    	rsStkCol = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkCol1", logId, methodNm, "적치열 조회"); 
		    	
		    	if (rsStkCol == null || rsStkCol.size() <= 0) {
					szMsg="["+methodNm+"] 적치열 조회 getYdStkcol data not found";
					throw new Exception(szMsg);
				}

		    	rsStkCol.absolute(1);
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsStkCol.getRecord());

		    	szOLD_YD_STK_COL_ACT_STAT   = commUtils.trim(recOutTemp.getFieldString("YD_STK_COL_ACT_STAT"       )); 
    	    	
    	    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStat 
				UPDATE TB_YS_STKCOL
				   SET MOD_DDTT     = SYSDATE             
					 , MODIFIER     = :V_MODIFIER             
					 , YD_STK_COL_ACT_STAT = NVL(:V_YD_STK_COL_ACT_STAT,YD_STK_COL_ACT_STAT)
					 , TRN_EQP_CD   = NVL(:V_TRN_EQP_CD, TRN_EQP_CD)       
					 , CAR_NO       = NVL(:V_CAR_NO, CAR_NO)           
					 , CARD_NO      = NVL(:V_CARD_NO, CARD_NO)              
				     , YD_STKBED_USG_CD = NVL(:V_STKBED_USG_CD,YD_STKBED_USG_CD)
				WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
			   */
		    	commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStat", logId, methodNm, "TB_YS_STKCOL 등록");				    	    	
    	    	
		    	commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarpoint", logId, methodNm, "Car-Point 등록");
		    	
		    	/******************************************
		    	 * 포인트 구내 운송 으로 전송처리
		    	 ***************************************/
		    	recInTemp1  = JDTORecordFactory.getInstance().create();
		    	recInTemp1.setResultCode(logId);	//Log ID
		    	recInTemp1.setResultMsg(methodNm);	//Log Method Name
		    	recInTemp1.setField("JMS_TC_CD",		"YSTSJ012");
		    	recInTemp1.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
		    	recInTemp1.setField("YD_GP", 			szYS_STK_COL_GP.substring(0,1));
		    	recInTemp1.setField("YS_STK_COL_GP", 	szYS_STK_COL_GP);
				
		    	szMsg= "szYD_STK_COL_ACT_STAT: ["+szYD_STK_COL_ACT_STAT+"  szOLD_YD_STK_COL_ACT_STAT: ["+szOLD_YD_STK_COL_ACT_STAT+"] 비교";
				commUtils.printLog(logId, szMsg, "SL");		
				
				
		    	if(szYD_STK_COL_ACT_STAT.equals ("C") 
						|| szYD_STK_COL_ACT_STAT.equals("L")
						|| szYD_STK_COL_ACT_STAT.equals("R")){
		    		
					if( szOLD_YD_STK_COL_ACT_STAT.equals("N")) {			//사용불가
						recInTemp1.setField("PNT_UNIT_CL_GP",	"C");
						sndRecord = commUtils.addSndData(sndRecord,recInTemp1);	

					}else{
						isSendable = false;
					}
				}else if(szYD_STK_COL_ACT_STAT.equals ("N")){
					
					recInTemp1.setField("PNT_UNIT_CL_GP",		"C");
					sndRecord = commUtils.addSndData(sndRecord,recInTemp1);						
				}		    
		    	
		    	// 사외통합야드는 L2 전송 기능 없음
//		    	if( isSendable ) {
//		    		// szYdGp = szYS_STK_COL_GP.substring(0,2);
//		    		/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//		    		 * 업무기준 : 차량출발시 저장위치 제원 야드L2로 전송
//		    		 * 야드BLOOM:N1,BILLET:N2,선재:N3,봉강:N4,선재자동화:N5,봉강자동화:N6 	
//			         *         YSN8L001 저장위치제원
//				     *         YSN8L002 저장품제원
//				     *         YSN8L003 크레인작업지시
//				     *         YSN8L004 크레인작업실적응답
//		    		 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
//					szJMS_TC_CD = "YSN8L001";
//					recInTemp  = JDTORecordFactory.getInstance().create();
//					recInTemp.setResultCode(logId);	//Log ID
//					recInTemp.setResultMsg(methodNm);	//Log Method Name
//		    		recInTemp.setField("MSG_ID",    szJMS_TC_CD);
//					recInTemp.setField("YD_INFO_SYNC_CD", "3");						//1:동,2:SPAN,3:열,4:BED
//					recInTemp.setField("YD_GP", szYS_STK_COL_GP.substring(0, 1));
//					recInTemp.setField("YS_STK_COL_GP", szYS_STK_COL_GP);
//					
//					//전송 Data 생성
//					sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2(szJMS_TC_CD, recInTemp));
//
//					szMsg="["+methodNm+"] 포인트 개패시 시 저장위치 제원 야드L2로 전송";
//					commUtils.printLog(logId, szMsg, "SL");			    		
//		    	}
				
			}

			szMsg="[구내내운송 소재차량Point개폐 전송  성공]";
			commUtils.printLog(logId, szMsg, "SL");
	    	
	    	szMsg = "["+methodNm+"] YS_STK_COL_GP["+szYS_STK_COL_GP+"]의 진행상태["+szYD_STK_COL_ACT_STAT+"] 변경처리함";
			commUtils.printLog(logId, szMsg, "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return sndRecord;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of procPntUnit
	
	/**
	 * 차량작업관리 > 차량Point작업현황- 입동차량순서 변경처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procGdsBayInWoSeqChang(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-입동지시[YsTotYsJspSeEJB.procGdsBayInWoSeqChang] < ";
		String logId = gdReq[0].getRequestUserIp();
		JDTORecord recInTemp = null;
		JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
		int RtnVal = 0;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			String syd_car_sch_id = null;
				
			for(int x = 0; x < gdReq.length; x++){
				for(int i = 1; i <= 5; i++){
					syd_car_sch_id = commUtils.nvl(gdReq[x].getField("YD_CAR_SCH_ID"+i), "");
					
					if(!syd_car_sch_id.equals("")){
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("YD_CAR_SCH_ID"	    ,commUtils.nvl(gdReq[x].getFieldString("YD_CAR_SCH_ID"+i), ""));
						recInTemp.setField("YD_BAYIN_WO_SEQ"	,commUtils.nvl(gdReq[x].getFieldString("YD_BAYIN_WO_SEQ"+i), ""));
						recInTemp.setField("MODIFIER"			,commUtils.nvl(gdReq[0].getFieldString("MODIFIER"), ""));

						/*com.inisteel.cim.ys.common.dao.YsCommDAO.updBayInWoSeqChang
						UPDATE TB_YS_CARSCH
						   SET MOD_DDTT = SYSDATE
						     , MODIFIER = :V_MODIFIER
						     , YD_BAYIN_WO_SEQ = :V_YD_BAYIN_WO_SEQ
						 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  
						*/
						RtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBayInWoSeqChang", logId, methodNm, "차량스케쥴 등록");		
					}
					if (RtnVal < 0) {
						commUtils.printLog(logId, "차량스케쥴 등록 오류", "SL");
					} // end of if
				}	
			}
			commUtils.printLog(logId, methodNm, "S-");
			return sndRecord;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of procGdsBayInWoSeqChang
	
	/**
	 * 차량작업관리 > 차량Point작업현황 - 입동지시
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procBayInWo(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-입동지시[YsTotYsJspSeEJB.procBayInWo] < ";
		String logId = gdReq[0].getRequestUserIp();
		JDTORecord recInTemp = null;
		JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setResultCode(logId);	//Log ID
			recInTemp.setResultMsg(methodNm);	//Log Method Name
			recInTemp.setField("JMS_TC_CD"				,"YSYSJ801");  //차량입동지시 요구 기존:YDYDJ662
			recInTemp.setField("JMS_TC_CREATE_DDTT"		,commUtils.getDateTime14());
				
			for (int ii = 0; ii < gdReq.length; ii++) {
				
				commUtils.printLog(logId, commUtils.nvl(gdReq[ii].getFieldString("YD_CARPNT_CD"), ""), "SL");
				
				recInTemp.setField("YD_CARPNT_CD"	, commUtils.nvl(gdReq[ii].getFieldString("YD_CARPNT_CD"), ""));		//입동포인트
				recInTemp.setField("YD_CAR_STOP_LOC", commUtils.nvl(gdReq[ii].getFieldString("YS_STK_COL_GP"), ""));		//입동포인트
//				recInTemp.setField("YD_CAR_SCH_ID"	, commUtils.getValue(gdReq, "YD_CAR_SCH_ID", ii));	        //차량스케줄ID
				recInTemp.setField("CAR_NO" 		, commUtils.nvl(gdReq[ii].getFieldString("TRN_EQP_CD"), ""));
				sndRecord = commUtils.addSndData(sndRecord,recInTemp);
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return sndRecord;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of procBayInWo
	
	/**
	 * 차량작업관리 > 배차내역 - 차량회송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord doDelCarSch(GridData gdReq) throws DAOException {
		String methodNm = "빌렛차량작업 관리-차량회송처리[YsTotYsJspSeEJB.doDelCarSch] < " + gdReq.getNavigateValue();
		String logId 			= gdReq.getIPAddress();
		String szMsg 			= null;
		String szARR_YD_PNT_CD 	= null;
		String szCurrDate 		= YsCommUtils.getCurDate("yyyyMMddHHmmss");
		int intRtnVal 			= 0;
		
		JDTORecord sndRecord	= JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);	
			
			String szYD_CAR_SCH_ID 		= commUtils.getValue(gdReq, "YD_CAR_SCH_ID", 0);
			String szYD_WBOOK_ID 		= commUtils.getValue(gdReq, "YD_WBOOK_ID", 0);
			String szYD_CRN_SCH_ID 		= commUtils.getValue(gdReq, "YD_CRN_SCH_ID", 0);
			String szARR_WLOC_CD 		= commUtils.getValue(gdReq, "ARR_WLOC_CD", 0);
			String szTRN_EQP_CD 		= commUtils.getValue(gdReq, "TRN_EQP_CD", 0);
			String szYD_CAR_STOP_LOC 	= commUtils.getValue(gdReq, "YD_CAR_STOP_LOC", 0);
			String szYD_CAR_PROG_STAT 	= commUtils.getValue(gdReq, "YD_CAR_PROG_STAT", 0);
			String szYD_CAR_USE_GP 		= commUtils.getValue(gdReq, "YD_CAR_USE_GP", 0);
			
			szMsg = "차량 STATUS["+szYD_CAR_PROG_STAT+"],차량스케줄ID["+szYD_CAR_SCH_ID+"], 작업예약ID["+szYD_WBOOK_ID+"], 스케쥴ID["+szYD_CRN_SCH_ID+"], 운송장비코드["+szTRN_EQP_CD+"], 착지개소코드["+szARR_WLOC_CD+"]";
			
			commUtils.printLog(logId, szMsg, "SL");
			
			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
			
			if("B".equals(szYD_CAR_PROG_STAT)||
			   "C".equals(szYD_CAR_PROG_STAT)||
			   "D".equals(szYD_CAR_PROG_STAT)||
			   "E".equals(szYD_CAR_PROG_STAT)){
				/**********************************************************
		    	 * 1. 출발야드 적치열 -> 비활성상태(C) 로 업데이트
		    	 **********************************************************/
				szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CAR_STOP_LOC+"]을 비활성상태로 변경처리 시작 ";
				commUtils.printLog(logId, szMsg, "SL");
				
		    	recInTemp.setField("YS_STK_COL_GP",        szYD_CAR_STOP_LOC);
		    	recInTemp.setField("YD_STK_COL_ACT_STAT",  "C");
		    	recInTemp.setField("YD_CAR_USE_GP",        "");
		    	recInTemp.setField("TRN_EQP_CD",           "");
		    	recInTemp.setField("CAR_NO",               "");
		    	recInTemp.setField("CARD_NO",              "");
		    	recInTemp.setField("MODIFIER", 			   commUtils.trim(gdReq.getParam("userid")));
		    	
		    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStatClear", logId, methodNm, "TB_YS_STKCOL 등록");
								
				/**********************************************************
		    	 * 2. 차량포인트통합관리 
		    	 **********************************************************/
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YS_STK_COL_GP", szYD_CAR_STOP_LOC);
				recInTemp.setField("STAT", "C");
				
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointstackcolgpupdateCT", logId, methodNm, "저장위치로 초기화 하는 경우(구내운송)");
				
				/**********************************************************
		    	 * 3. 출발야드 적치베드 -> 야드적치베드활성상태(=C(비활성상태), YD_STK_BED_ACT_STAT) 
		    	 *                     및 BED중량MAX(=기본값, YD_STK_BED_WT_MAX) 으로 업데이트
		    	 **********************************************************/
				szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CAR_STOP_LOC+"]의 베드를 비활성상태와 BED중량MAX기본값으로 변경처리 시작 ";
				commUtils.printLog(logId, szMsg, "SL");
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_BED_WT_MAX", YsConstant.YD_STK_BED_WT_MAX_DEFAULT);
				recInTemp.setField("YS_STK_COL_GP", szYD_CAR_STOP_LOC);
				recInTemp.setField("YD_STK_BED_ACT_STAT", "C");
				
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp", logId, methodNm, "TB_YS_STKBED 등록");
				
				/**********************************************************
		    	 * 4. 출발야드 적치단 -> 야드적치단활성상태(=C(비활성상태), YD_STK_LYR_ACT_STAT) 
		    	 *                   및 야드적치단재료상태(=E(적치가능), YD_STK_LYR_MTL_STAT) 로 업데이트
		    	 **********************************************************/
				szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CAR_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 시작 ";
				commUtils.printLog(logId, methodNm, "SL");
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);	//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				recInTemp.setField("YS_STK_COL_GP", szYD_CAR_STOP_LOC);
				recInTemp.setField("YD_STK_LYR_ACT_STAT", "C");
				recInTemp.setField("SSTL_NO", "");
				recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
				
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "TB_YS_STKLYR 등록");
				
			}
			
			/**********************************************************
			* 5. 기존 이송차량스케줄/재료 삭제
			**********************************************************/
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);

			//이송차량재료 초기화
			commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSchMtl", logId, methodNm, "이송차량재료 초기화");

			//이송차량스케줄 초기화
			commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch", logId, methodNm, "이송차량스케줄 초기화");
			
			
			/**********************************************************
	    	 * 6. 빌렛옥외야드(L2)이송시 송신
	    	 **********************************************************/
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			recInTemp.setField("YD_INFO_SYNC_CD", "7" );
			
			// 사외통합야드는 L2 전송하는 기능 없음
			//전송 Data 생성
//			sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YSN8L002", recInTemp));
			
           /**********************************************************
			* 7.소재차량회송 하차완료 전송 시작
			* 2025.09.04 전문 삭제
			**********************************************************/
			sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL3("YSTSJ016", recInTemp));
						
			commUtils.printLog(logId, methodNm, "S-");
				
			return sndRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end doDelCarSch
	
	/**
	 * 차량작업 포인트 현황- 차량출발
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procLeaveCar(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-차량출발[YsTotYsJspSeEJB.procLeaveCar] < ";
		String logId = gdReq[0].getRequestUserIp();
		String szMsg = null;
		String szYD_CAR_SCH_ID		= null;
		String szYD_CAR_PROG_STAT	= null;
		String szCAR_NO				= null;
		String szCARD_NO			= null;
		String szSPOS_WLOC_CD		= null;
		String szYD_PNT_CD			= null;
		String szTRANS_ORD_DATE		= null;
		String szTRANS_ORD_SEQNO	= null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			//Return Value
			JDTORecord jrRtn = null;
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq[0].getFieldString("userid")));
			
			for (int ii = 0; ii < gdReq.length; ii++) {
				
				szYD_CAR_SCH_ID = commUtils.nvl(gdReq[ii].getFieldString("YD_CAR_SCH_ID"), "");
				
				
				//--------------------------------------------------------------------------------
				//	차량스케줄ID로 차량스케줄 조회
				//--------------------------------------------------------------------------------
				
			
				JDTORecord recTemp			= JDTORecordFactory.getInstance().create();
				jrParam.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
				
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch
				SELECT *
				FROM TB_YS_CARSCH C
				WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/
				//차량스케쥴 조회
				JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch", logId, methodNm, "차량스케쥴 조회");

				int rowCnt1 = jsCarSch.size();

				if (rowCnt1 <= 0) {
					commUtils.printLog(logId, "차량스케줄이 업습니다. SKIP", "SL");
					continue;
				}				
				
				jsCarSch.first();
				recTemp		= jsCarSch.getRecord();
				
				szYD_CAR_PROG_STAT		= commUtils.trim(recTemp.getFieldString("YD_CAR_PROG_STAT"));
				
				if( !szYD_CAR_PROG_STAT.equals(YsConstant.YD_CARLD_CMPL)) {
					szMsg = "["+methodNm+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량진행상태["+szYD_CAR_PROG_STAT+"]가 상차완료가 아니므로 SKIP시킴";
					commUtils.printLog(logId, szMsg, "SL");
					continue;
				}
				
				szCAR_NO				=  commUtils.trim(recTemp.getFieldString("CAR_NO"));
				szCARD_NO				=  commUtils.trim(recTemp.getFieldString("CARD_NO"));
				szSPOS_WLOC_CD			=  commUtils.trim(recTemp.getFieldString("SPOS_WLOC_CD"));
				szYD_PNT_CD				=  commUtils.trim(recTemp.getFieldString("YD_PNT_CD1"));
				szTRANS_ORD_DATE		=  commUtils.trim(recTemp.getFieldString("TRANS_ORD_DATE"));
				szTRANS_ORD_SEQNO		=  commUtils.trim(recTemp.getFieldString("TRANS_ORD_SEQNO"));
				
				
				//--------------------------------------------------------------------------------
				
				szMsg = "차량스케줄["+szYD_CAR_SCH_ID+"]의 차량출발 처리 EJB 호출";
				commUtils.printLog(logId, szMsg, "SL");
				
				JDTORecord recInTemp			= JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);	//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name				
				recInTemp.setField("CARD_NO", 				szCARD_NO);
				recInTemp.setField("CAR_NO", 				szCAR_NO);			
				recInTemp.setField("SPOS_WLOC_CD", 			szSPOS_WLOC_CD);
				recInTemp.setField("SPOS_YD_PNT_CD", 		szYD_PNT_CD);
				recInTemp.setField("TRANS_ORD_DATE", 		szTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
				recInTemp.setResultCode(logId);		//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				
				EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
				jrRtn = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
				
				//ydDelegate.sendMsg(recInTemp);
			
				szMsg = "차량스케줄["+szYD_CAR_SCH_ID+"]의 차량출발 처리 EJB 호출완료";
				commUtils.printLog(logId, szMsg, "SL");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end procLeaveCar
	
	/**
	 * 차량작업관리 > 배차내역 - 하차완료처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord procCarUd(GridData gdReq) throws DAOException {
		String methodNm = "차량작업관리화면 하차완료처리[YsTotYsJspSeEJB.procCarUd] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String szARR_YD_PNT_CD = null;
		String szCurrDate = YsCommUtils.getCurDate("yyyyMMddHHmmss");
		JDTORecord sndRecord	= JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);	
	
			//DAO Parameter - Log ID, Method, 수정자 Set
			
			String szYD_CAR_SCH_ID 		= commUtils.getValue(gdReq, "YD_CAR_SCH_ID", 0);
			String szYD_WBOOK_ID 		= commUtils.getValue(gdReq, "YD_WRK_BOOK_ID", 0);
			String szARR_WLOC_CD 		= commUtils.getValue(gdReq, "ARR_WLOC_CD", 0);
			String szTRN_EQP_CD 		= commUtils.getValue(gdReq, "TRN_EQP_CD", 0);
			String szYD_CAR_STOP_LOC 	= commUtils.getValue(gdReq, "YD_CAR_STOP_LOC", 0);
			String szYD_CAR_USE_GP 		= commUtils.getValue(gdReq, "YD_CAR_USE_GP", 0);
			
			
//			if( !szYD_CAR_STOP_LOC.equals("") ) {
//				szYD_GP = szYD_CAR_STOP_LOC.substring(0, 1);
//			}
			
			
			szMsg = "차량스케줄ID["+szYD_CAR_SCH_ID+"], 작업예약ID["+szYD_WBOOK_ID+"], 운송장비코드["+szTRN_EQP_CD+"], 착지개소코드["+szARR_WLOC_CD+"]";
			commUtils.printLog(logId, szMsg, "SL");
			
			//------------------------------------------------------------------------------------------------------
			//	차량스케줄 조회 후 차량진행상태 확인 시작 - 다른유저에 의해서 상태가 변경될 수 있으므로 먼저 상태를 확인 필요
			//	차량스케줄 조회
			//------------------------------------------------------------------------------------------------------
			szMsg = "차량스케줄ID["+szYD_CAR_SCH_ID+"]로 조회 전";
			commUtils.printLog(logId, szMsg, "SL");
			
			JDTORecord recTemp	= JDTORecordFactory.getInstance().create();
			JDTORecord recPara	= JDTORecordFactory.getInstance().create();
			JDTORecord recStkCol= JDTORecordFactory.getInstance().create();
			//1. 차량스케줄을 먼저 조회해서 존재하는 지를 확인
			recTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch
			SELECT *
			FROM TB_YS_CARSCH C
			WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			*/
			//차량스케쥴 조회
			JDTORecordSet jsCarSch = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch", logId, methodNm, "차량스케쥴 조회");
			if (jsCarSch.size() <= 0) {
				throw new Exception( "차량스케줄ID["+szYD_CAR_SCH_ID+"]이 존재하지 않습니다");
			}				
		
			jsCarSch.first();
			recPara = jsCarSch.getRecord();

			String szYD_CAR_PROG_STAT = commUtils.trim(recPara.getFieldString("YD_CAR_PROG_STAT"          ));	//차량진행상태
			szMsg = "차량스케줄ID["+szYD_CAR_SCH_ID+"]로 야드차량진행상태["+szYD_CAR_PROG_STAT+"]";
			commUtils.printLog(logId, szMsg, "SL");

			if( !szYD_CAR_PROG_STAT.equals("B") && !szYD_CAR_PROG_STAT.equals("C")) {
				throw new Exception( "차량스케줄["+szYD_CAR_SCH_ID+"]의 야드차량진행상태["+szYD_CAR_PROG_STAT+"]가 하차완료[하차완료가능상태 : 하차도착(B), 하차검수(C)]할 수 있는 상태가 아닙니다.");
			}
			
			szMsg = "차량스케줄["+szYD_CAR_SCH_ID+"]의 야드차량진행상태["+szYD_CAR_PROG_STAT+"]가 하차완료처리가능한 상태입니다.";
			commUtils.printLog(logId, szMsg, "SL");

			//------------------------------------------------------------------------------------------------------
			// 차량스케줄의 차량진행상태를 하차완료로 변경 - 삭제처리를 하지 않음
			//------------------------------------------------------------------------------------------------------
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID"	, szYD_CAR_SCH_ID);			//차량스케줄ID
			recPara.setField("YD_EQP_WRK_STAT"	, "U");						//야드설비작업상태
			recPara.setField("YD_CARUD_ST_DT"	, szCurrDate);				//하차개시일시
			recPara.setField("YD_CARUD_CMPL_DT"	, szCurrDate);				//하차완료일시
			recPara.setField("YD_CAR_PROG_STAT"	, "E");						//차량진행상태 : 하차완료[E]
			recPara.setField("MODIFIER"			, commUtils.trim(gdReq.getParam("userid")));					//수정자
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarSchCarWrkDn 
			UPDATE TB_YS_CARSCH
			   SET MODIFIER     = :V_MODIFIER
			     , MOD_DDTT     = SYSDATE
			     , YD_EQP_WRK_STAT = NVL(:V_YD_EQP_WRK_STAT,YD_EQP_WRK_STAT)
			     , YD_CARUD_ST_DT= NVL(:V_YD_CARUD_ST_DT,YD_CARUD_ST_DT)
			     , YD_CARUD_CMPL_DT= NVL(:V_YD_CARUD_CMPL_DT,YD_CARUD_CMPL_DT)
			     , YD_CAR_PROG_STAT= NVL(:V_YD_CAR_PROG_STAT,YD_CAR_PROG_STAT)
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			 */
			int intRtnVal = commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarSchCarWrkDn", logId, methodNm, "차량스케줄 갱신");
			
			if( intRtnVal == 0 ) {
				throw new Exception( "차량스케줄["+szYD_CAR_SCH_ID+"]에 하차개시일시, 하차완료일시, 차량진행상태[하차완료-E]를 업데이트시 차량스케줄이 존재하지 않습니다");
			}
			
			//------------------------------------------------------------------------------------------------------
			// 1. 차량 이송재료를 조회 후 삭제처리
			//------------------------------------------------------------------------------------------------------
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			recPara.setField("DEL_YN", "Y");
			recPara.setField("MODIFIER",commUtils.trim(gdReq.getParam("userid")));					//수정자
			
			//차량이송소재 종료
			//intRtnVal = ydCarFtmvMtlDao.updYdCarftmvmtl(recInTemp, 1) ;
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarFtmvMtl  
			UPDATE TB_YS_CARFTMVMTL
			   SET MODIFIER = :V_MODIFIER
			     , MOD_DDTT    = SYSDATE
		  	     , DEL_YN = :V_DEL_YN
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			   AND DEL_YN = 'N'
			  */ 
			
			commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBook", logId, methodNm, "TB_YS_CARFTMVMTL 종료");

			if(szYD_CAR_USE_GP.equals("G")) {
			
			} else {	
				//구내운송
				/**********************************************************
				* 1.하차개시 전송 시작
				**********************************************************/
				recPara         = JDTORecordFactory.getInstance().create();
				recStkCol       = JDTORecordFactory.getInstance().create();
				recPara.setField("YS_STK_COL_GP", 			szYD_CAR_STOP_LOC);
	
				//적치열 Table를 조회한다.
	    		 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkCol1 
	    		SELECT 
	    			YS_STK_COL_GP AS YS_STK_COL_GP
	    			,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
	    			,REGISTER AS REGISTER
	    			,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
	    			,MODIFIER AS MODIFIER
	    			,DEL_YN AS DEL_YN
	    			,YD_GP AS YD_GP
	    			,YD_BAY_GP AS YD_BAY_GP
	    			,YD_EQP_GP	AS YD_EQP_GP
	    			,YD_STK_COL_NO AS YD_STK_COL_NO
	    			,YD_STK_COL_ACT_STAT AS YD_STK_COL_ACT_STAT
	    			,YD_STK_COL_RULE_XAXIS AS YD_STK_COL_RULE_XAXIS
	    			,YD_STK_COL_RULE_YAXIS AS YD_STK_COL_RULE_YAXIS
	    			,YD_STK_COL_W AS YD_STK_COL_W
	    			,YD_STK_COL_L AS YD_STK_COL_L
	    			,YD_CAR_USE_GP AS YD_CAR_USE_GP
	    			,TRN_EQP_CD AS TRN_EQP_CD
	    			,CAR_NO AS CAR_NO
	    			,CARD_NO AS CARD_NO
	    			,WLOC_CD AS WLOC_CD
	    			,YD_PNT_CD AS YD_PNT_CD
	    			,YS_STK_COL_T_GP AS YS_STK_COL_T_GP
	    			,YS_STK_COL_W_GP AS YS_STK_COL_W_GP
	    			,YS_STK_COL_L_GP AS YD_STK_COL_L_GP
	    		--	,YD_STK_COL_H_MAX AS YD_STK_COL_H_MAX--
	    			--,YD_STK_COL_BED_L_TP AS YD_STK_COL_BED_L_TP
	    		    ,YS_OUTDIA_GRP_GP AS YS_OUTDIA_GRP_GP 
	    		    ,YD_STKBED_USG_CD
	    		FROM TB_YS_STKCOL
	    		WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
	    		AND DEL_YN ='N'
	    			*/
				JDTORecordSet rsStkCol = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkCol1", logId, methodNm, "적치열 조회"); 
		    	
		    	if (rsStkCol == null || rsStkCol.size() <= 0) {
					szMsg="차량정지위치[" + szYD_CAR_STOP_LOC + "]에 대한 개소코드가 없습니다.";
					throw new Exception(szMsg);
				}
				
		    	rsStkCol.first();
				recStkCol = rsStkCol.getRecord();
				
				szARR_WLOC_CD   = commUtils.trim(recStkCol.getFieldString("WLOC_CD"          ));
				szARR_YD_PNT_CD = commUtils.trim(recStkCol.getFieldString("YD_PNT_CD"          ));
				szMsg="차량정지위치[" + szYD_CAR_STOP_LOC + "]에 대한 개소코드[" + szARR_WLOC_CD + "]와 야드포인트코드[" + szARR_YD_PNT_CD + "]";
				commUtils.printLog(logId, szMsg, "SL");
				
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setResultCode(logId);	//Log ID
				recPara.setResultMsg(methodNm);	//Log Method Name
				/**********************************************************
				* 1.하차개시 전송 시작
				*  JMS_TC_CD	JMSTC코드	CHAR	8
				*  JMS_TC_CREATE_DDTT	JMSTC생성일시	DATE	14
				*  TRN_EQP_CD            운송장비코드	CHAR	8
				*  ARR_WLOC_CD           착지개소코드	CHAR	5
				*  ARR_YD_PNT_CD         착지야드포인트코드	CHAR	4
				*  TRN_WRK_ST_DT			운송작업시작일시	DATE	14
				**********************************************************/
				
				recPara.setField("JMS_TC_CD", 			"YSTSJ009");
				recPara.setField("JMS_TC_CREATE_DDTT", 	szCurrDate);
				recPara.setField("TRN_EQP_CD",     		szTRN_EQP_CD);
				recPara.setField("ARR_WLOC_CD", 		szARR_WLOC_CD);
				recPara.setField("ARR_YD_PNT_CD", 		szARR_YD_PNT_CD);
				recPara.setField("TRN_WRK_ST_DT", 		szCurrDate);
				sndRecord = commUtils.addSndData(sndRecord,recPara);
				
				commUtils.printLog(logId, "하차개시전문을 구내운송으로 전송 완료", "SL");
							
				//+++++++++++++++++ 하차개시 전송 끝 ++++++++++++++++
				
				//+++++++++++++++++ 하차완료 전송 시작 ++++++++++++++++
				/**********************************************************
				* 1.하차완료 전송 시작
				*  JMS_TC_CD	JMSTC코드	CHAR	8
				*  JMS_TC_CREATE_DDTT	JMSTC생성일시	DATE	14
				*  TRN_EQP_CD            운송장비코드	CHAR	8
				*  ARR_WLOC_CD           착지개소코드	CHAR	5
				*  ARR_YD_PNT_CD         착지야드포인트코드	CHAR	4
				*  CARUD_CMPL_DT		  하차완료일시	DATE	14
				**********************************************************/
				
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setResultCode(logId);	//Log ID
				recPara.setResultMsg(methodNm);	//Log Method Name
				//2. 하차완료를 구내운송으로 전송
				recPara.setField("JMS_TC_CD", 			"YSTSJ010");
				recPara.setField("JMS_TC_CREATE_DDTT", 	szCurrDate);
				recPara.setField("TRN_EQP_CD",     		szTRN_EQP_CD);
				recPara.setField("ARR_WLOC_CD", 		szARR_WLOC_CD);
				recPara.setField("ARR_YD_PNT_CD", 		szARR_YD_PNT_CD);
				recPara.setField("CARUD_CMPL_DT", 		YsCommUtils.getCurDate("yyyyMMddHHmmss"));
				
				sndRecord = commUtils.addSndData(sndRecord,recPara);
				
				commUtils.printLog(logId, "하차완료전문을 구내운송으로 전송 완료", "SL");
				//+++++++++++++++++ 하차완료 전송 끝 ++++++++++++++++
				
				commUtils.printLog(logId, methodNm, "S-");
			}	
			return sndRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end procCarUd
	
	/**
	 * 기준관리 - 검색가이드 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updYsRuleSrchGdBt(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "기준관리 - 검색가이드 수정[YsTotYsJspSeEJB.updYsRuleSrchGdBt] < ";
		String logId = gdReq[0].getRequestUserIp();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq[0].getFieldString("userid")));

			//수정할 레코드 수
			int rowCnt = 0;
			
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
					
					commDao.update(jrParam, "com.inisteel.cim.ys.ystot.dao.delYsRule", logId, methodNm, "기준관리 삭제");
						
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
	 * 이송Lot편성List - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updPrepSchLot(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "준비스케줄 - 수정[YsTotYsJspSeEJB.updPrepSchLot] < ";
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
	 * 이송Lot편성List - 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delPrepSchLot(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "준비스케줄 - 삭제[YsTotYsJspSeEJB.delPrepSchLot] < ";
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
	 * 상차완료백업처리팝업-등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord trtMvCarStatSet2(GridData gdReq) throws DAOException {
		String methodNm = "상차완료백업처리팝업-등록 [YsTotYsJspSeEJB.trtMvCarStatSet2] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sJMS_TC_CD = commUtils.trim(gdReq.getParam("JMS_TC_CD"));
			String sTRN_WRK_FULLVOID_GP = commUtils.trim(gdReq.getParam("TRN_WRK_FULLVOID_GP"));
			String sTRN_EQP_CD = commUtils.trim(gdReq.getParam("TRN_EQP_CD"));
			String sYD_CAR_SCH_ID = commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"));
			String sSPOS_WLOC_CD = commUtils.trim(gdReq.getParam("SPOS_WLOC_CD"));
			String sYD_PNT_CD1 = commUtils.trim(gdReq.getParam("YD_PNT_CD1"));
			String sYD_CARLD_STOP_LOC = commUtils.trim(gdReq.getParam("YD_CARLD_STOP_LOC"));
			String sARR_WLOC_CD = commUtils.trim(gdReq.getParam("ARR_WLOC_CD"));
			String sYD_PNT_CD3 = commUtils.trim(gdReq.getParam("YD_PNT_CD3"));
			String sYD_CARUD_STOP_LOC = commUtils.trim(gdReq.getParam("YD_CARUD_STOP_LOC"));
			String sTO_LOC = commUtils.trim(gdReq.getParam("TO_LOC"));
			String sWLOC_CD = null;
			String sYD_PNT_CD = null;

			String modifier = commUtils.trim(gdReq.getParam("userid")); // 수정자
			String currDate = commUtils.getDateTime14(); // 현재시각

			if ("".equals(sTRN_EQP_CD)) {
				throw new Exception("운송장비코드가 없습니다.");
			}

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);

			jrYdMsg.setField("JMS_TC_CD", sJMS_TC_CD);

			if ("TSYSJ003".equals(sJMS_TC_CD)) { // 소재차량도착
				jrYdMsg.setField("TRN_EQP_CD", sTRN_EQP_CD);
				if ("F".equals(sTRN_WRK_FULLVOID_GP)) {
					// 하차도착
					jrYdMsg.setField("ARR_WLOC_CD", sARR_WLOC_CD);
					jrYdMsg.setField("ARR_YD_PNT_CD", sYD_PNT_CD3);
					jrYdMsg.setField("TRN_WRK_FULLVOID_GP", sTRN_WRK_FULLVOID_GP);
				} else if ("E".equals(sTRN_WRK_FULLVOID_GP)) {
					// 상차도착
					jrYdMsg.setField("ARR_WLOC_CD", sSPOS_WLOC_CD);
					jrYdMsg.setField("ARR_YD_PNT_CD", sYD_PNT_CD1);
					jrYdMsg.setField("TRN_WRK_FULLVOID_GP", sTRN_WRK_FULLVOID_GP);
				}

				EJBConnector sndConn = new EJBConnector("default", "YsCommCarTSMvSeEJB", this);
				jrRtn = (JDTORecord) sndConn.trx("rcvTSYSJ003", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

			} else if ("TSYSJ004".equals(sJMS_TC_CD)) { // 소재차량출발
				jrYdMsg.setField("TRN_EQP_CD", sTRN_EQP_CD);
				jrYdMsg.setField("TRN_WRK_FULLVOID_GP", sTRN_WRK_FULLVOID_GP);
				jrYdMsg.setField("TRN_EQP_STK_CAPA", "80000");

				if ("F".equals(sTRN_WRK_FULLVOID_GP)) { // 영차:하차하러 출발
					jrYdMsg.setField("SPOS_WLOC_CD", sSPOS_WLOC_CD);
					jrYdMsg.setField("SPOS_YD_PNT_CD", sYD_PNT_CD1);
					jrYdMsg.setField("ARR_WLOC_CD", sARR_WLOC_CD);
					jrYdMsg.setField("ARR_YD_PNT_CD", sYD_PNT_CD3);

				} else if ("E".equals(sTRN_WRK_FULLVOID_GP)) { // 하차완료후 출발처리로 착지개소를 DMY1P로 줌으로써 차량스케줄 완료처리를 한다.
					jrYdMsg.setField("SPOS_WLOC_CD", sARR_WLOC_CD);
					jrYdMsg.setField("SPOS_YD_PNT_CD", sYD_PNT_CD3);
					jrYdMsg.setField("ARR_WLOC_CD", "DMY1P");
					jrYdMsg.setField("ARR_YD_PNT_CD", "");
				}

				EJBConnector sndConn = new EJBConnector("default", "YsCommCarTSMvSeEJB", this);
				jrRtn = (JDTORecord) sndConn.trx("rcvTSYSJ004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

			} else if ("YSTSJ007".equals(sJMS_TC_CD)) { // 소재차량상차개시
				// 차량진행상태를 하차완료로 설정
				jrParam.setField("YD_CAR_PROG_STAT", "4"); // 상차개시
				jrParam.setField("YD_CARLD_CMPL_DT", ""); // 상차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_CMPL_DT", ""); // 하차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);

				commDao.update(jrParam, updMvCarSchCmpl, logId, methodNm, "이송차량스케줄 상차개시로 수정");

				jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate); // JMSTC생성일시
				jrYdMsg.setField("TRN_EQP_CD", sTRN_EQP_CD); // 운송장비코드
				jrYdMsg.setField("SPOS_WLOC_CD", sSPOS_WLOC_CD); // 발지개소코드
				jrYdMsg.setField("SPOS_YD_PNT_CD", sYD_PNT_CD1); // 발지야드포인트코드
				jrYdMsg.setField("ARR_WLOC_CD", sARR_WLOC_CD); // 착지개소코드
				jrYdMsg.setField("TRN_WRK_ST_DT", currDate); // 운송작업시작일시

				// 전송할 전문에 추가
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);

			} else if ("YDTSJ008".equals(sJMS_TC_CD)) { // 소재차량상차완료
				// 차량진행상태를 상차완료로 설정
				jrParam.setField("YD_CAR_PROG_STAT", "5"); // 상차완료
				jrParam.setField("YD_CARLD_CMPL_DT", currDate); // 상차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_CMPL_DT", ""); // 하차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);

				commDao.update(jrParam, updMvCarSchCmpl, logId, methodNm, "이송차량스케줄 상차완료로 수정");

				jrYdMsg.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID); // 차량스케줄ID
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ008", jrYdMsg));

			} else if ("YSTSJ009".equals(sJMS_TC_CD)) { // 소재차량하차개시
				// 차량진행상태를 하차완료로 설정
				jrParam.setField("YD_CAR_PROG_STAT", "D"); // 하차개시
				jrParam.setField("YD_CARLD_CMPL_DT", ""); // 상차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_CMPL_DT", ""); // 하차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);

				commDao.update(jrParam, updMvCarSchCmpl, logId, methodNm, "이송차량스케줄 하차개시로 수정");

				jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate); // JMSTC생성일시
				jrYdMsg.setField("TRN_EQP_CD", sTRN_EQP_CD); // 운송장비코드
				jrYdMsg.setField("ARR_WLOC_CD", sARR_WLOC_CD); // 착지개소코드
				jrYdMsg.setField("ARR_YD_PNT_CD", sYD_PNT_CD3); // 착지야드포인트코드
				jrYdMsg.setField("TRN_WRK_ST_DT", currDate); // 운송작업시작일시

				// 전송할 전문에 추가
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);

			} else if ("YSTSJ010".equals(sJMS_TC_CD)) { // 소재차량하차완료
				// 차량진행상태를 하차완료로 설정
				jrParam.setField("YD_CAR_PROG_STAT", "E"); // 하차완료
				jrParam.setField("YD_CARLD_CMPL_DT", ""); // 상차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_CMPL_DT", currDate); // 하차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);

				commDao.update(jrParam, updMvCarSchCmpl, logId, methodNm, "이송차량스케줄 하차완료로 수정");
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate); // JMSTC생성일시
				jrYdMsg.setField("TRN_EQP_CD", sTRN_EQP_CD); // 운송장비코드
				jrYdMsg.setField("ARR_WLOC_CD", sARR_WLOC_CD); // 착지개소코드
				jrYdMsg.setField("ARR_YD_PNT_CD", sYD_PNT_CD3); // 착지야드포인트코드
				jrYdMsg.setField("TRN_WRK_ST_DT", currDate); // 운송작업시작일시

				// 전송할 전문에 추가
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);

			} else if ("YSTSJ011".equals(sJMS_TC_CD)) { // 소재차량Point지시
				// 야드적치열구분으로 차량포인트 정보 조회
				jrParam.setField("YD_STK_COL_GP", sTO_LOC);
				JDTORecordSet jsCol = commDao.select(jrParam, getYdPntByStkColGp, logId, methodNm, "야드적치열구분으로 차량포인트 정보 조회");

				if (jsCol != null && jsCol.size() > 0) {
					sWLOC_CD = commUtils.trim(jsCol.getRecord(0).getFieldString("WLOC_CD"));
					sYD_PNT_CD = commUtils.trim(jsCol.getRecord(0).getFieldString("YD_PNT_CD"));

					if ("".equals(sWLOC_CD) || "".equals(sYD_PNT_CD)) {
						throw new Exception(sTO_LOC + " 의 개소코드 또는 야드포인트에 NULL 값이 있습니다.");
					}

					if (!"".equals(commUtils.trim(jsCol.getRecord(0).getFieldString("TRN_EQP_CD")))) {
						throw new Exception(sTO_LOC + " 에 이미 " + commUtils.trim(jsCol.getRecord(0).getFieldString("TRN_EQP_CD")) + " 운송장비가 점유하고  있습니다.");
					}

					if (!"".equals(commUtils.trim(jsCol.getRecord(0).getFieldString("CAR_NO")))) {
						throw new Exception(sTO_LOC + " 에 이미 " + commUtils.trim(jsCol.getRecord(0).getFieldString("CAR_NO")) + " 차량이 점유하고  있습니다.");
					}

				} else {
					throw new Exception(sTO_LOC + " 의 개소코드와 야드포인트를 TB_YD_CARPOINT 에서 찾지 못했습니다.");
				}

				jrYdMsg.setField("JMS_TC_CD", sJMS_TC_CD); // "YSTSJ011"
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate); // JMSTC생성일시
				jrYdMsg.setField("TRN_EQP_CD", sTRN_EQP_CD); // 운송장비코드
				jrYdMsg.setField("WLOC_CD", sWLOC_CD);
				jrYdMsg.setField("YD_PNT_CD", sYD_PNT_CD);
				jrYdMsg.setField("PNT_WO_GP", "A");
				jrYdMsg.setField("PNT_WO_DT", currDate);

				// 전송할 전문에 추가
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);

				String sYD_CARLD_PNT_WO_DT = "";
				String sYD_CARUD_PNT_WO_DT = "";

				// 차량스케줄의 개소코드, 야드포인트, 정지위치를 UDPATE 한다.
				if ("E".equals(sTRN_WRK_FULLVOID_GP)) { // 공차:상차
					sSPOS_WLOC_CD = sWLOC_CD;
					sYD_PNT_CD1 = sYD_PNT_CD;
					sYD_CARLD_STOP_LOC = sTO_LOC;
					sYD_CARLD_PNT_WO_DT = currDate;

				} else { // 영차:하차
					sARR_WLOC_CD = sWLOC_CD;
					sYD_PNT_CD3 = sYD_PNT_CD;
					sYD_CARUD_STOP_LOC = sTO_LOC;
					sYD_CARUD_PNT_WO_DT = currDate;
				}

				// 이송차량스케줄 수정
				jrParam.setField("YD_CAR_PROG_STAT", "1"); // ""이면 이전 상태 유지된다.
				jrParam.setField("SPOS_WLOC_CD", sSPOS_WLOC_CD);
				jrParam.setField("YD_CARLD_PNT_WO_DT", sYD_CARLD_PNT_WO_DT);
				jrParam.setField("YD_PNT_CD1", sYD_PNT_CD1);
				jrParam.setField("YD_CARLD_STOP_LOC", sYD_CARLD_STOP_LOC);
				jrParam.setField("ARR_WLOC_CD", sARR_WLOC_CD);
				jrParam.setField("YD_CARUD_PNT_WO_DT", sYD_CARUD_PNT_WO_DT);
				jrParam.setField("YD_PNT_CD3", sYD_PNT_CD3);
				jrParam.setField("YD_CARUD_STOP_LOC", sYD_CARUD_STOP_LOC);
				jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);

				commDao.update(jrParam, updMvCarSchPntWo, logId, methodNm, "차량포이트 지시 수정");

				// TB_YM_STACKCOL 예약정보등록
				jrParam.setField("YD_STK_COL_ACT_STAT", "L");
				jrParam.setField("YS_STK_COL_GP", sTO_LOC);

				commDao.update(jrParam, updColActStat, logId, methodNm, "적치열 활성상태 변경");

				// TB_YD_CARPOINT 포인트지시 예약하기
				EJBConnector ejbConn1 = new EJBConnector("default", "YsCommCarTSMvSeEJB", this);
				ejbConn1.trx("YsCarPointinforeg2", new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class },
						new Object[] { "3", "", sTRN_EQP_CD, sTO_LOC, "", "", "R", logId, methodNm });
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
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
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm = "상차완료백업처리팝업-이송작업재료등록 [YsTotYsJspSeEJB.updCarFtMvMtl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			String sstlNo;
			JDTORecordSet rsResult;

			// 등록 할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					sstlNo = commUtils.getValue(gdReq, "SSTL_NO", ii);
					jrParam.setField("SSTL_NO", sstlNo);
					rsResult = commDao.select(jrParam, getYsStockchk, logId, methodNm, "TB_YS_STOCK 에 존재 하는지 확인");

					if (rsResult.size() <= 0) {
						throw new Exception("TB_YS_STOCK에 존재하지 않는 제품 : " + sstlNo);
					}

					// 이송작업재료삭제
					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID")));
					jrParam.setField("SSTL_NO", commUtils.getValue(gdReq, "OLD_SSTL_NO", ii));

					commDao.update(jrParam, delCarFtMvMtl, logId, methodNm, "이송작업재료삭제");

					// 이송작업재료등록
					jrParam.setField("YS_STK_BED_NO", commUtils.getValue(gdReq, "YS_STK_BED_NO", ii));
					jrParam.setField("YS_STK_LYR_NO", commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii));
					jrParam.setField("YS_STK_SEQ_NO", Integer.parseInt(commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii)));
					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID")));
					jrParam.setField("MODIFIE", commUtils.trim(gdReq.getParam("userid")));
					jrParam.setField("SSTL_NO", commUtils.getValue(gdReq, "SSTL_NO", ii));

					commDao.insert(jrParam, updCarFtMvMtl, logId, methodNm, "이송작업재료등록");
				}
			}

			// 차량 작업 상태,매수,작업완료시간 update
			jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID")));
			commDao.update(jrParam, updCarSchWrkSt, logId, methodNm, "이송차량스케줄 차량작업상태 수정");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;

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
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord delCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm = "상차완료백업처리팝업-이송작업재료삭제 [YsTotYsJspSeEJB.delCarFtMvMtl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			// 등록 할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					// 이송작업재료삭제
					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID")));
					jrParam.setField("SSTL_NO", commUtils.getValue(gdReq, "OLD_SSTL_NO", ii));

					commDao.insert(jrParam, delCarFtMvMtl, logId, methodNm, "이송작업재료삭제");
				}
			}

			// 차량 작업 상태,매수,작업완료시간 update
			jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID")));
			commDao.update(jrParam, updCarSchWrkSt, logId, methodNm, "이송차량스케줄 차량작업상태 수정");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;

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
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord chgCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm = "상차완료백업처리팝업-이송작업재료위치변경 [YsTotYsJspSeEJB.chgCarFtMvMtl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			// 등록 할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					// 이송작업재료삭제
					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID")));
					jrParam.setField("SSTL_NO", commUtils.getValue(gdReq, "SSTL_NO", ii));

					commDao.insert(jrParam, delCarFtMvMtl, logId, methodNm, "이송작업재료위치변경");
				}
			}

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					// 이송작업재료등록
					jrParam.setField("YS_STK_BED_NO", commUtils.getValue(gdReq, "YS_STK_BED_NO", ii));
					jrParam.setField("YS_STK_LYR_NO", commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii));
					jrParam.setField("YS_STK_SEQ_NO", Integer.parseInt(commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii)));
					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID")));
					jrParam.setField("MODIFIE", commUtils.trim(gdReq.getParam("userid")));
					jrParam.setField("SSTL_NO", commUtils.getValue(gdReq, "OLD_SSTL_NO", ii));

					commDao.insert(jrParam, updCarFtMvMtl, logId, methodNm, "이송작업재료등록");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;

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
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord mkUdCarSch(GridData gdReq) throws DAOException {
		String methodNm = "하차백업생성팝업-등록 [YsTotYsJspSeEJB.mkUdCarSch] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("YD_CAR_SCH_ID", commDao.getSeqId(logId, methodNm, "CarSch")); // 야드차량스케쥴ID
			jrParam.setField("REGISTER", commUtils.trim(gdReq.getParam("userid"))); // 야드차량스케쥴ID
			jrParam.setField("YD_CAR_PROG_STAT", "5"); // 차량진행상태 (5:상차완료)
			jrParam.setField("YD_CAR_USE_GP", "L"); // 야드차량사용구분 (L:구내운송, G:출하차량 )
			jrParam.setField("SPOS_WLOC_CD", gdReq.getParam("SPOS_WLOC_CD")); // 발지개소코드(상차지)
			jrParam.setField("ARR_WLOC_CD", gdReq.getParam("ARR_WLOC_CD")); // 착지개소코드(하차지)
			jrParam.setField("YD_PNT_CD", ""); // 야드상차포인트코드(발지)
			jrParam.setField("TRN_EQP_CD", gdReq.getParam("TRN_EQP_CD")); // 운송장비코드

			commDao.insert(jrParam, insYsCarsch, logId, methodNm, "차량스케쥴 상차출발(5)로 INSERT ");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of mkUdCarSch
	
	/**
	 * PDA 차량하차처리 (구내운송)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procUnloading(GridData gdReq) throws DAOException {
		String methodNm = "차량하차처리 [YsTotYsJspSeEJB.procUnloading] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String szCurrDate = YsCommUtils.getCurDate("yyyyMMddHHmmss");

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecord recPara = JDTORecordFactory.getInstance().create();

			commUtils.printParam("gdReq", commUtils.gridDataTojdtoRecord(gdReq));

			String carNo = commUtils.trim(gdReq.getParam("CAR_NO"));
			String ydCarSchId = commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"));
			String ydPntCd3 = commUtils.trim(gdReq.getParam("YD_PNT_CD3"));
			String arrWlocCd = commUtils.trim(gdReq.getParam("ARR_WLOC_CD"));
			String location = commUtils.trim(gdReq.getParam("LOCATION")); // GH01A104
			String ysStkLyrNo = commUtils.trim(gdReq.getParam("YS_STK_LYR_NO"));
			String maxSeq = commUtils.trim(gdReq.getParam("MAX_SEQ"));

			String loc_ydGp = "";
			String loc_ydBay = "";
			String loc_ydEqp = "";
			String loc_ydCol = "";

			if (location != null && location.length() == 6) {
				loc_ydGp = location.substring(0, 1);
				loc_ydBay = location.substring(1, 2);
				loc_ydEqp = location.substring(2, 4);
				loc_ydCol = location.substring(4, 6);
			}
			String loc_stkcolgp = loc_ydGp + loc_ydBay + loc_ydEqp + loc_ydCol;

			JDTORecord recTemp = JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_CAR_SCH_ID", ydCarSchId);

			szMsg = "loc_stkcolgp:[" + loc_stkcolgp + "], 차량스케줄ID[" + ydCarSchId + "], 운송장비코드[" + carNo + "], 착지개소코드[" + arrWlocCd + "]";
			commUtils.printLog(logId, szMsg, "SL");

			// ------------------------------------------------------------------------------------------------------
			// 차량스케줄 조회 후 차량진행상태 확인 시작 - 다른유저에 의해서 상태가 변경될 수 있으므로 먼저 상태를 확인 필요
			// 차량스케줄 조회
			// ------------------------------------------------------------------------------------------------------
			szMsg = "차량스케줄ID[" + ydCarSchId + "]로 조회 전";
			commUtils.printLog(logId, szMsg, "SL");

			/*
			 * com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch SELECT * FROM TB_YS_CARSCH C WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			 */
			// 차량스케쥴 조회
			JDTORecordSet jsCarSch = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch", logId, methodNm, "차량스케쥴 조회");
			if (jsCarSch.size() <= 0) {
				throw new Exception("차량스케줄ID[" + ydCarSchId + "]이 존재하지 않습니다");
			}

			jsCarSch.first();
			recPara = jsCarSch.getRecord();

			String szYD_CAR_PROG_STAT = commUtils.trim(recPara.getFieldString("YD_CAR_PROG_STAT")); // 차량진행상태
			String szYD_CARUD_WRK_BOOK_ID = commUtils.trim(recPara.getFieldString("YD_CARUD_WRK_BOOK_ID"));
			String szYD_GP = commUtils.trim(recPara.getFieldString("YD_GP"));

			szMsg = "YD_GP:" + szYD_GP + ", maxSeq:" + maxSeq + ", 차량스케줄ID[" + ydCarSchId + "]로 야드차량진행상태[" + szYD_CAR_PROG_STAT + "]" + "크레인작업예약 YD_CARLD_WRK_BOOK_ID[" + szYD_CARUD_WRK_BOOK_ID + "]";
			commUtils.printLog(logId, szMsg, "SL");

			if (!szYD_CAR_PROG_STAT.equals("B") && !szYD_CAR_PROG_STAT.equals("D")) {
				throw new Exception("차량스케줄[" + ydCarSchId + "]의 야드차량진행상태[" + szYD_CAR_PROG_STAT + "]가 하차완료[하차완료가능상태 : 하차도착(B), 하차개시(D)]할 수 있는 상태가 아닙니다.");
			}

			szMsg = "차량스케줄[" + ydCarSchId + "]의 야드차량진행상태[" + szYD_CAR_PROG_STAT + "]가 하차완료처리가능한 상태입니다.";
			commUtils.printLog(logId, szMsg, "SL");

			// ------------------------------------------------------------------------------------------------------
			// 차량스케줄의 차량진행상태를 하차완료로 변경 - 삭제처리를 하지 않음
			// ------------------------------------------------------------------------------------------------------
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID", ydCarSchId); // 차량스케줄ID
			recPara.setField("YD_EQP_WRK_STAT", "U"); // 야드설비작업상태
			recPara.setField("YD_CARUD_ST_DT", szCurrDate); // 하차개시일시
			recPara.setField("YD_CARUD_CMPL_DT", szCurrDate); // 하차완료일시
			recPara.setField("YD_CAR_PROG_STAT", "E"); // 차량진행상태 : 하차완료[E]
			recPara.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid"))); // 수정자

			int intRtnVal = commDao.update(recPara, updYdCarSchCarWrkDn_ystot, logId, methodNm, "차량스케줄 갱신");

			if (intRtnVal == 0) {
				throw new Exception("차량스케줄[" + ydCarSchId + "]에 하차개시일시, 하차완료일시, 차량진행상태[하차완료-E]를 업데이트시 차량스케줄이 존재하지 않습니다");
			}

			// ------------------------------------------------------------------------------------------------------
			// 차량 이송재료를 PDA에서 입력한 야드에 강제 배치 (김현진 부장님과 협의)
			// ------------------------------------------------------------------------------------------------------
			JDTORecordSet rsCarBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
			JDTORecord recInTemp = null;

			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID", ydCarSchId);

			rsCarBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
			rsCarBookMtl = commDao.select(recInTemp, getYdCarftmvmtlIDLm, logId, methodNm, "차량재료 조회");

			commUtils.printParam("rsCarBookMtl", rsCarBookMtl);

			// 최상단 빈 배드 그룹 찾기
			JDTORecord recTemp2 = JDTORecordFactory.getInstance().create();
			recTemp2.setField("YS_STK_COL_GP", loc_stkcolgp);
			JDTORecordSet jsrtn = commDao.select(recTemp2, getTopEmptyBedGrp, logId, methodNm, "최 상단 빈 배드 그룹 위치 찾기");
			
			commUtils.printParam("jsrtn", jsrtn);

			String tmpBed = "01";
			int tmpSeq = 0;
			
			JDTORecord recOutTemp = null;
			if (jsrtn.size() > 0) {
				recOutTemp = jsrtn.getRecord(0);
				
				tmpBed = commUtils.trim(recOutTemp.getFieldString("BED_GRP"));
				ysStkLyrNo = commUtils.trim(recOutTemp.getFieldString("MIN_LYR"));
			}
			
			commUtils.printLog(logId, "최상단 찾기 결과 - ysStkLyrNo:" + ysStkLyrNo, "SL");
			
			String carBedNow = "";
			String carBedBefore = "";
			Boolean chgBed = false;
			Boolean bedChged = false;
			
			// 차량작업 재료 등록
			for (int Loop_i = 1; Loop_i <= rsCarBookMtl.size(); Loop_i++) {
				rsCarBookMtl.absolute(Loop_i);
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsCarBookMtl.getRecord());
				
				carBedNow = recOutTemp.getFieldString("YS_STK_BED_NO");
				commUtils.printLog(logId, "Loop_i:" + Loop_i + ",carBedNow:" + carBedNow + ",carBedBefore:" + carBedBefore , "SL");
				
				if (!carBedNow.equals(carBedBefore)) {
					carBedBefore = carBedNow;
					if (Loop_i > 1) {
						chgBed = true;
					}
				}
				
				if (bedChged == false && Loop_i == (Integer.parseInt(maxSeq) + 1) || chgBed == true) {
					commUtils.printLog(logId, "Loop_i:" + Loop_i + ",tmpBed:" + tmpBed + ",bedChged:" + bedChged , "SL");
					bedChged = true;
					chgBed = false;
					int bed = Integer.parseInt(tmpBed);
					bed = bed + 1;
					
					tmpBed = (bed < 10) ? "0" + bed : String.valueOf(bed);
					tmpSeq = 0;
				}
				
				tmpSeq += 1;
				
				commUtils.printLog(logId, "Loop_i:" + Loop_i + ",tmpBed:" + tmpBed + ",tmpSeq:" + tmpSeq , "SL");

				GridData inParam = new GridData();
				inParam.addParam("YD_GP", szYD_GP);
				inParam.addParam("userid", commUtils.trim(gdReq.getParam("userid")));
				inParam.addParam("SSTL_NO", recOutTemp.getFieldString("SSTL_NO"));
				inParam.addParam("YS_STK_COL_GP", loc_stkcolgp);
				inParam.addParam("YS_STK_BED_NO", tmpBed);
				inParam.addParam("YS_STK_LYR_NO", ysStkLyrNo);
				inParam.addParam("YS_STK_SEQ_NO", String.valueOf(tmpSeq));
				inParam.addParam("OLD_SSTL_NO", "");
				inParam.addParam("OLD_YS_STK_COL_GP", "");
				inParam.addParam("OLD_YS_STK_BED_NO", "");
				inParam.addParam("OLD_YS_STK_LYR_NO", "");
				inParam.addParam("OLD_YS_STK_SEQ_NO", "");
				inParam.addParam("FROM_SSTL_NO", "");
				inParam.addParam("FROM_YS_STK_COL_GP", "");
				inParam.addParam("FROM_YS_STK_BED_NO", "");
				inParam.addParam("FROM_YS_STK_LYR_NO", "");
				inParam.addParam("FROM_YS_STK_SEQ_NO", "");

				commUtils.printParam("inParam", commUtils.gridDataTojdtoRecord(inParam));

				updStrLocReg(inParam);
			}

			// ------------------------------------------------------------------------------------------------------
			// 크레인 작업 예약 종료
			// ------------------------------------------------------------------------------------------------------
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_WBOOK_ID", szYD_CARUD_WRK_BOOK_ID);
			recInTemp.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
			recInTemp.setField("DEL_YN", "Y");

			commDao.update(recInTemp, updDelYnWrkBookMtl, logId, methodNm, "TB_YS_WRKBOOKMTL 삭제");

			commDao.update(recInTemp, updDelYnWrkBook, logId, methodNm, "TB_YS_WRKBOOK 삭제");
			
			
			// ------------------------------------------------------------------------------------------------------
			// 이송지시 수정
			// ------------------------------------------------------------------------------------------------------
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID", ydCarSchId);
			recInTemp.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));

			commDao.update(recInTemp, updN7YSL206StlfrToMove_ystot, logId, methodNm, "TB_PB_STLFRTOMOVE 수정");
			

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setResultCode(logId); // Log ID
			recPara.setResultMsg(methodNm); // Log Method Name
			/**********************************************************
			 * 1.하차개시 전송 시작 JMS_TC_CD JMSTC코드 CHAR 8 JMS_TC_CREATE_DDTT JMSTC생성일시 DATE 14 TRN_EQP_CD 운송장비코드 CHAR 8 ARR_WLOC_CD 착지개소코드 CHAR 5 ARR_YD_PNT_CD 착지야드포인트코드 CHAR 4 TRN_WRK_ST_DT 운송작업시작일시 DATE 14
			 **********************************************************/

			recPara.setField("JMS_TC_CD", "YSTSJ009");
			recPara.setField("JMS_TC_CREATE_DDTT", szCurrDate);
			recPara.setField("TRN_EQP_CD", carNo);
			recPara.setField("ARR_WLOC_CD", arrWlocCd);
			recPara.setField("ARR_YD_PNT_CD", ydPntCd3);
			recPara.setField("TRN_WRK_ST_DT", szCurrDate);
			jrRtn = commUtils.addSndData(jrRtn, recPara);

			/**********************************************************
			 * 1.하차완료 전송 시작 JMS_TC_CD JMSTC코드 CHAR 8 JMS_TC_CREATE_DDTT JMSTC생성일시 DATE 14 TRN_EQP_CD 운송장비코드 CHAR 8 ARR_WLOC_CD 착지개소코드 CHAR 5 ARR_YD_PNT_CD 착지야드포인트코드 CHAR 4 CARUD_CMPL_DT 하차완료일시 DATE 14
			 **********************************************************/

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setResultCode(logId); // Log ID
			recPara.setResultMsg(methodNm); // Log Method Name
			// 2. 하차완료를 구내운송으로 전송
			recPara.setField("JMS_TC_CD", "YSTSJ010");
			recPara.setField("JMS_TC_CREATE_DDTT", szCurrDate);
			recPara.setField("TRN_EQP_CD", carNo);
			recPara.setField("ARR_WLOC_CD", arrWlocCd);
			recPara.setField("ARR_YD_PNT_CD", ydPntCd3);
			recPara.setField("CARUD_CMPL_DT", szCurrDate);

			jrRtn = commUtils.addSndData(jrRtn, recPara);

			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of procUnloading
	
	
	/**
	 * PDA 차량상차처리 (구내운송)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procLoading(GridData gdReq) throws DAOException {
		String methodNm = "차량상차처리 [YsTotYsJspSeEJB.procLoading] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String szCurrDate = YsCommUtils.getCurDate("yyyyMMddHHmmss");

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			commUtils.printParam("gdReq", commUtils.gridDataTojdtoRecord(gdReq));
			String carNo = commUtils.trim(gdReq.getParam("CAR_NO"));
			szMsg = "운송장비코드[" + carNo + "]";
//
			// ------------------------------------------------------------------------------------------------------
			// 차량스케줄 조회 후 차량진행상태 확인 시작 - 다른유저에 의해서 상태가 변경될 수 있으므로 먼저 상태를 확인 필요
			// 차량스케줄 조회
			// ------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, szMsg, "SL");
//
			// 차량스케쥴 조회
			JDTORecord recTemp = JDTORecordFactory.getInstance().create();
			recTemp.setField("TRN_EQP_CD", carNo);
			
			JDTORecordSet jsCarSch = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCarSch", logId, methodNm, "차량스케쥴 조회");
			if (jsCarSch.size() <= 0) {
				throw new Exception("차량스케줄ID[" + carNo + "]이 존재하지 않습니다");
			}

			jsCarSch.first();
			recPara = jsCarSch.getRecord();
			
			String szYD_CAR_PROG_STAT = commUtils.trim(recPara.getFieldString("YD_CAR_PROG_STAT")); // 차량진행상태
			String szYD_CARUD_WRK_BOOK_ID = commUtils.trim(recPara.getFieldString("YD_CARUD_WRK_BOOK_ID"));
			String szYD_GP = commUtils.trim(recPara.getFieldString("YD_GP"));
			String ydCarSchId =  commUtils.trim(recPara.getFieldString("YD_CAR_SCH_ID"));
			String arrWlocCd =  commUtils.trim(recPara.getFieldString("ARR_WLOC_CD"));

			szMsg = "YD_GP:" + szYD_GP + ", 차량스케줄ID[" + ydCarSchId + "]로 야드차량진행상태[" + szYD_CAR_PROG_STAT + "]" + "크레인작업예약 YD_CARLD_WRK_BOOK_ID[" + szYD_CARUD_WRK_BOOK_ID + "]";
			commUtils.printLog(logId, szMsg, "SL");

			if (!szYD_CAR_PROG_STAT.equals("2")) { 
				throw new Exception("차량스케줄[" + ydCarSchId + "]의 야드차량진행상태[" + szYD_CAR_PROG_STAT + "]가 상차도착[상차도착가능상태 : 상차도착(2)]할 수 있는 상태가 아닙니다.");
			}

			szMsg = "차량스케줄[" + ydCarSchId + "]의 야드차량진행상태[" + szYD_CAR_PROG_STAT + "]가 상차완료처리가능한 상태입니다. arrWlocCd:" + arrWlocCd;
			commUtils.printLog(logId, szMsg, "SL");

			// ------------------------------------------------------------------------------------------------------
			// 차량스케줄의 차량진행상태를 상차완료로 변경 - 삭제처리를 하지 않음
			// ------------------------------------------------------------------------------------------------------
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID", ydCarSchId); // 차량스케줄ID
			recPara.setField("YD_EQP_WRK_STAT", "L"); // 야드설비작업상태 : 영차
			recPara.setField("YD_CARUD_ST_DT", szCurrDate); // 하차개시일시
			recPara.setField("YD_CARUD_CMPL_DT", szCurrDate); // 하차완료일시
			recPara.setField("YD_CAR_PROG_STAT", "5"); // 차량진행상태 : 상차완료
			recPara.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid"))); // 수정자

			int intRtnVal = commDao.update(recPara, updYdCarSchCarWrkDn_ystot, logId, methodNm, "차량스케줄 갱신");

			if (intRtnVal == 0) {
				throw new Exception("차량스케줄[" + ydCarSchId + "]에 상차개시일시, 상차완료일시, 차량진행상태[상차완료-5]를 업데이트시 차량스케줄이 존재하지 않습니다");
			}
			
			// 차량스케줄 정보 할당
			String sstlNo = "";
			String sposWlocCd = "";
			String YdPntCd1 = "";
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					if (sstlNo.isEmpty()) {
						sstlNo = commUtils.getValue(gdReq, "SSTL_NO", ii);
					}
				}
			}
			
			// 차량스케쥴 조회
			JDTORecord recTemp2 = JDTORecordFactory.getInstance().create();
			recTemp2.setField("SSTL_NO", sstlNo);
			recTemp2.setField("CAR_NO", carNo);
			JDTORecordSet jsCarMv = commDao.select(recTemp2, selCarMvInfoByStlCarNo, logId, methodNm, "차량스케쥴과 이송정보 조회");
			
			commUtils.printParam("jsCarMv", jsCarMv);

			JDTORecord recOutTemp = null;
			if (jsCarMv.size() > 0) {
				recOutTemp = jsCarMv.getRecord(0);
				
				sposWlocCd = commUtils.trim(recOutTemp.getFieldString("SPOS_WLOC_CD"));
				arrWlocCd = commUtils.trim(recOutTemp.getFieldString("ARR_WLOC_CD"));
				YdPntCd1 = commUtils.trim(recOutTemp.getFieldString("YD_PNT_CD1"));
			}
			
			commUtils.printLog(logId, "sstlNo:" + sstlNo + ",carNo:" + carNo + ",sposWlocCd:" + sposWlocCd + ",arrWlocCd:" + arrWlocCd + ",YdPntCd1:" + YdPntCd1, "SL");

			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String tmpBed = "01";
			String maxSeq = "09";
			
			// 재료 번호로 제품 구분과 작업 매수를 가져온다.
			recTemp2 = JDTORecordFactory.getInstance().create();
			recTemp2.setField("SSTL_NO", sstlNo);
			recTemp2.setField("CAR_NO", carNo);
			JDTORecordSet jsRtn = commDao.select(recTemp2, getWrkPerCount, logId, methodNm, "상차완료 기준 조회");
			
			commUtils.printParam("jsRtn", jsRtn);

			recOutTemp = null;
			String st = "";
			String sl = "";
			String stlknd = "";
			String rPcnt = "";
			String rMcnt = "";
			
			if (jsRtn.size() > 0) {
				recOutTemp = jsRtn.getRecord(0);
				
				st = commUtils.trim(recOutTemp.getFieldString("S_T"));			// 사이즈 T
				sl = commUtils.trim(recOutTemp.getFieldString("S_L"));			// 사이즈 L
				stlknd = commUtils.trim(recOutTemp.getFieldString("STLKND"));	// 재료 구분 (각강빌렛:QT, 봉강:RR)
				rPcnt = commUtils.trim(recOutTemp.getFieldString("R_PCNT"));	// 작업매수
				rMcnt = commUtils.trim(recOutTemp.getFieldString("R_MCNT"));	// 상차완료 기준 매수
			}
			
			commUtils.printLog(logId, "sstlNo:" + sstlNo + ",st:" + st + ",sl:" + sl + ",stlknd:" + stlknd + ",rPcnt:" + rPcnt + ",rMcnt:" + rMcnt, "SL");
			
			int tmpSeq = 0;
			// 수정할 레코드 수
			rowCnt = gdReq.getHeader("CHECK").getRowCount();
			Boolean bedChged = false;

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					commUtils.printLog(logId, methodNm, "SSTL_NO:" + commUtils.getValue(gdReq, "SSTL_NO", ii));
					commUtils.printLog(logId, methodNm, "YS_STK_COL_GP:" + commUtils.getValue(gdReq, "YS_STK_COL_GP", ii));
					
					if (sstlNo.isEmpty()) {
						sstlNo = commUtils.getValue(gdReq, "SSTL_NO", ii);
					}
					
					if (stlknd.equals("QT")) {
						// 각강인 경우는 1회 작업 수량으로 배드를 나눈다.
						if (
							bedChged == false &&
							(ii == Integer.parseInt(rPcnt)) || (ii == (Integer.parseInt(maxSeq) - 1))
						) {
							bedChged = true;
							tmpBed = "02";
							tmpSeq = 0;
						}
					} else if (stlknd.equals("RR")) {
						// 봉강인 경우는 상차완료 수량을 절반으로 나누어 배드를 나눈다 
						if (
							bedChged == false &&
								(ii >= (Integer.parseInt(rMcnt) /2 )) || (ii == (Integer.parseInt(maxSeq) - 1))
						) {
							bedChged = true;
							tmpBed = "02";
							tmpSeq = 0;
						}
					}
					
					tmpSeq += 1;
					
					commUtils.printLog(logId, methodNm, "tmpSeq:" + tmpSeq + ",tmpBed:" + tmpBed);

//					// ------------------------------------------------------------------------------------------------------
//					// CARSCH MTL 추가
//					// ------------------------------------------------------------------------------------------------------
					// 이송작업재료등록
					jrParam.setField("YS_STK_BED_NO", tmpBed);
					jrParam.setField("YS_STK_LYR_NO", "01");
					jrParam.setField("YS_STK_SEQ_NO", String.valueOf(tmpSeq));
					jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
					jrParam.setField("MODIFIE", commUtils.trim(gdReq.getParam("userid")));
					jrParam.setField("SSTL_NO", commUtils.getValue(gdReq, "SSTL_NO", ii));

					commDao.insert(jrParam, updCarFtMvMtl, logId, methodNm, "이송작업재료등록");
					
					// GH01A101071
//					// ------------------------------------------------------------------------------------------------------
//					// 야드 맵 삭제
//					// ------------------------------------------------------------------------------------------------------
					GridData inParam = new GridData();
					inParam.addParam("YD_GP", szYD_GP);
					inParam.addParam("userid", commUtils.trim(gdReq.getParam("userid")));
					inParam.addParam("SSTL_NO", "");
					inParam.addParam("YS_STK_COL_GP", commUtils.getValue(gdReq, "YS_STK_COL_GP", ii).substring(0, 6));
					inParam.addParam("YS_STK_BED_NO", commUtils.getValue(gdReq, "YS_STK_COL_GP", ii).substring(6, 8));
					inParam.addParam("YS_STK_LYR_NO", commUtils.getValue(gdReq, "YS_STK_COL_GP", ii).substring(8, 10));
					inParam.addParam("YS_STK_SEQ_NO", commUtils.getValue(gdReq, "YS_STK_COL_GP", ii).substring(10, 11));
					inParam.addParam("OLD_SSTL_NO", commUtils.getValue(gdReq, "SSTL_NO", ii));
					inParam.addParam("OLD_YS_STK_COL_GP", "");
					inParam.addParam("OLD_YS_STK_BED_NO", "");
					inParam.addParam("OLD_YS_STK_LYR_NO", "");
					inParam.addParam("OLD_YS_STK_SEQ_NO", "");
					inParam.addParam("FROM_SSTL_NO", "");
					inParam.addParam("FROM_YS_STK_COL_GP", "");
					inParam.addParam("FROM_YS_STK_BED_NO", "");
					inParam.addParam("FROM_YS_STK_LYR_NO", "");
					inParam.addParam("FROM_YS_STK_SEQ_NO", "");

					commUtils.printParam("inParam", commUtils.gridDataTojdtoRecord(inParam));

					updStrLocReg(inParam);

//					// ------------------------------------------------------------------------------------------------------
//					// 준비재료 삭제
//					// ------------------------------------------------------------------------------------------------------
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updPrepMtlDelYBySstlNo", logId, methodNm, "준비재료 삭제");
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updPrepSchDelYBySstlNo", logId, methodNm, "준비스케줄 삭제");
				}
			}
			
			jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			String currDate = commUtils.getDateTime14(); // 현재시각

			jrYdMsg.setField("JMS_TC_CD", "YSTSJ007");
			jrParam.setField("YD_CAR_PROG_STAT", "4"); // 상차개시
			jrParam.setField("YD_CARLD_CMPL_DT", ""); // 상차완료일시 ""이면 이전값 유지
			jrParam.setField("YD_CARUD_CMPL_DT", ""); // 하차완료일시 ""이면 이전값 유지
			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);

			commDao.update(jrParam, updMvCarSchCmpl, logId, methodNm, "이송차량스케줄 상차개시로 수정");
			
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate); // JMSTC생성일시
			jrYdMsg.setField("TRN_EQP_CD", carNo); // 운송장비코드
			jrYdMsg.setField("SPOS_WLOC_CD", sposWlocCd); // 발지개소코드
			jrYdMsg.setField("SPOS_YD_PNT_CD", YdPntCd1); // 발지야드포인트코드
			jrYdMsg.setField("ARR_WLOC_CD", arrWlocCd); // 착지개소코드
			jrYdMsg.setField("TRN_WRK_ST_DT", currDate); // 운송작업시작일시

			// 전송할 전문에 추가
			jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);

			// 차량진행상태를 상차완료로 설정
			jrParam.setField("YD_CAR_PROG_STAT", "5"); // 상차완료
			jrParam.setField("YD_CARLD_CMPL_DT", currDate); // 상차완료일시 ""이면 이전값 유지
			jrParam.setField("YD_CARUD_CMPL_DT", ""); // 하차완료일시 ""이면 이전값 유지
			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);

			commDao.update(jrParam, updMvCarSchCmpl, logId, methodNm, "이송차량스케줄 상차완료로 수정");

			jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrYdMsg.setField("ARR_WLOC_CD", arrWlocCd); // 차량스케줄ID
			jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId); // 차량스케줄ID
			jrRtn = commUtils.addSndData(jrRtn, ystotYsComm.getMsgL3("YSTSJ008", jrYdMsg));
			
			
			// 도착 개소 코드 변경
			jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("ARR_WLOC_CD", arrWlocCd);
			jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);

			commDao.update(jrParam, udtCarschArrWloc, logId, methodNm, "이송차량스케 도착 개소코드 수정");
			
			commUtils.printParam("jrRtn", jrRtn);

			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of procLoading
	
	
	/**
	 *      [A] 오퍼레이션명 : 작업예약등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updbtMvStkWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "작업예약등록[YsTotYsJspSeEJB.updbtMvStkWrkBook] < " + gdReq.getNavigateValue();
		String logId    = gdReq.getIPAddress();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord    jrRtn  = null;
			JDTORecordSet jsMsg  = JDTORecordFactory.getInstance().createRecordSet("");

			String stlNos        = commUtils.trim(gdReq.getParam("SSTL_NOS"         ));	//재료번호들
			String ysStkColGp    = commUtils.trim(gdReq.getParam("YS_STK_COL_GP"   ));	//야드적치열구분(6자리 이상)
			String ydToLocGuide  = commUtils.trim(gdReq.getParam("YD_TO_LOC_GUIDE" ));	//야드To위치Guide
			String ydWrkCrn		 = commUtils.trim(gdReq.getParam("YD_WRK_CRN"));		//야드작업크레인(작업자지정 크레인)
			String ydPrepSchId   = commUtils.trim(gdReq.getParam("YD_PREP_SCH_ID" ));	//야드준비스케쥴ID(차량상차작업예약ID)

			StringBuffer sbImpPros  = new StringBuffer();	//주요진행내용로그
			
			
			/**********************************************************
			* 0. 파라미터 체크
			**********************************************************/
			sbImpPros.append("[작업예약등록 > TO파라미터체크]::시작 \r\n");
			sbImpPros.append("[=========================================================================================================================================] \r\n");
			
			//0.1. 재료번호 존재유무 체크
			if( "".equals(stlNos) ) {
				throw new Exception("이적 재료번호가 없습니다.");
			//0.2. 야드적치열구분(6자리 이상) 체크
			} else if( "".equals(ysStkColGp) || ysStkColGp.length() < 6 ) {
				throw new Exception("적치열[" + ysStkColGp + "] 정보가 없습니다.");
			}

			sbImpPros.append("[작업예약등록 > TO파라미터체크]::종료 \r\n");
			
			/**********************************************************
			* 1. 스케줄코드 설정
			**********************************************************/
			String ydSchCd    = "";							//야드스케줄코드
			String ydAimBayGp = "";							//야드목표동구분
			
			//1.1. "야드목표동구분" 설정
			if( "".equals(ydToLocGuide) ) {
				ydAimBayGp = ysStkColGp.substring(1, 2);
			} else {
				ydAimBayGp = ydToLocGuide.substring(1, 2);
				//"To위치"가 "야드+동"까지만 있으면 "위치검색BED 기준 적용"
				if( ydToLocGuide.length() < 4 ) {
					ydToLocGuide = "";
				}
			}
			
			//1.2. 스케줄코드 생성
			String sStkSpan    = ysStkColGp.substring(2, 4);	// From-SPAN   : [01:대형옥내/02:빌렛정정][TY/PC/TR/TF/RT]
			String sStkSpanCol = ysStkColGp.substring(2, 6);	// From-SPANCOL: [PC42.PC41.PC32.PC31.PC51.PC01.PC02.PC03.PC04/TR11.TR21.TR41.TR42/TF01.TF02.TF03/0101-03/0201-10/TY01-11]

			sbImpPros.append("[작업예약등록 > 스케줄코드설정]::종료: ydSchCd ["+ydSchCd+"] \r\n");
			sbImpPros.append("[=========================================================================================================================================] \r\n");
			
						
			/**********************************************************
			* 2. 작업예약 등록
			**********************************************************/
			sbImpPros.append("[작업예약등록 > 재료번호로조회]::시작 \r\n");
			
			//2.1. 작업예약 대상재료 조회
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord    jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("SSTL_NOS"        , stlNos       );	//재료번호들
			jrParam.setField("YS_STK_COL_GP"   , ysStkColGp   );	//야드적치열구분
			
			JDTORecordSet jsWbMtl = commDao.select(jrParam, getMvStkWrkBookMtlPp, logId, methodNm, "재료번호로 조회");

			int rowCnt = jsWbMtl.size();
			if( rowCnt <= 0 ) {
				throw new Exception("이적 재료 정보가 없습니다.");
			}

			sbImpPros.append("[작업예약등록 > 재료번호로조회]::종료 \r\n");
			sbImpPros.append("[=========================================================================================================================================] \r\n");

			//2.2. 작업예약등록
			jrParam.setField("YD_SCH_CD"       , ydSchCd      );	//야드스케줄코드
			jrParam.setField("YD_AIM_BAY_GP"   , ydAimBayGp   );	//야드목표동구분
			jrParam.setField("YD_TO_LOC_GUIDE" , ydToLocGuide );	//야드To위치Guide
			jrParam.setField("YD_WRK_CRN"	   , ydWrkCrn     );	//야드작업크레인
			jrParam.setField("YD_PREP_SCH_ID"  , ydPrepSchId  );	//야드준비스케쥴ID(차량상차작업예약ID)

			sbImpPros.append("[작업예약등록 > 작업예약등록]::시작 \r\n");
			
			jsMsg.addRecord(this.insMvstkWrkBook(jrParam, jsWbMtl, sbImpPros));
			
			/**********************************************************
			* 3. 동내이적 작업 크레인별 첫번째 스케줄 전송
			**********************************************************/

			String ruleItem = ysComm.getYsRuleItem(logId, methodNm, "APPGI1", "*");
			if (!ruleItem.equals("Y")) {
				jrRtn = commUtils.addSndData(jrRtn, this.setCrnSchMsg(jsMsg, logId, methodNm));
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 *      [A] 오퍼레이션명 : 작업예약등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord insMvstkWrkBook(JDTORecord jrParam, JDTORecordSet jsWbMtl, StringBuffer sbImpPros) throws DAOException {
		String methodNm = "작업예약등록[YsTotYsJspSeEJB.insMvstkWrkBook] < " + jrParam.getResultMsg();
		String logId    = jrParam.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			String ydSchCd       = commUtils.trim(jrParam.getFieldString("YD_SCH_CD"       ));	//야드스케줄코드
			String ydAimBayGp    = commUtils.trim(jrParam.getFieldString("YD_AIM_BAY_GP"   ));	//야드목표동구분
			String ydToLocGuide  = commUtils.trim(jrParam.getFieldString("YD_TO_LOC_GUIDE" ));	//야드To위치Guide
			String modifier      = commUtils.trim(jrParam.getFieldString("MODIFIER"        ));	//수정자
			String ydWrkCrn      = commUtils.trim(jrParam.getFieldString("YD_WRK_CRN"      ));	//야드작업크레인
			String ydPrepSchId   = commUtils.trim(jrParam.getFieldString("YD_PREP_SCH_ID"  ));	//야드준비스케쥴ID(차량상차작업예약ID)
			
			/**********************************************************
			* 1. 스케줄코드, 크레인 체크
			**********************************************************/
			sbImpPros.append("[작업예약등록 > 작업예약등록 > 스케줄코드,크레인체크]::시작 \r\n");
			
			JDTORecord jrCrnSpec = ystotYsComm.chkSchCdEqp(jrParam);
			
			String ydGp           = ydAimBayGp.substring(0, 1);									//야드구분
			String ydBayGp        = ydAimBayGp.substring(1, 1);									//야드동구분
			String ydEqpId		  = "";
			String ydSchPrior	  = "";
			
			if(jrCrnSpec.size() >= 1) {
				ydEqpId        = commUtils.trim(jrCrnSpec.getFieldString("YD_EQP_ID"   ));	//야드설비ID(크레인): 사용가능여부, 우선순위 고려한 크레인 결정
				ydSchPrior     = commUtils.trim(jrCrnSpec.getFieldString("YD_SCH_PRIOR"));	//야드스케줄우선순위
			} else {
				ydEqpId        = "";	//야드설비ID(크레인): 사용가능여부, 우선순위 고려한 크레인 결정
				ydSchPrior     = "";	//야드스케줄우선순위
			}
			
			String ydToLocDcsnMtd = "S";														//야드TO위치결정방법(S:스케줄지정, F:지정위치)
			
			// "야드목적동구분" 체크
			if( "".equals(ydAimBayGp) ) {
				ydAimBayGp = ydBayGp;
			}
			// "야드TO위치결정방법" 체크
			if( !"".equals(ydToLocGuide) ) {
				ydToLocDcsnMtd = "F";
			}

			sbImpPros.append("[작업예약등록 > 작업예약등록 > 스케줄코드,크레인체크]::종료 \r\n");
			
			
			/**********************************************************
			* 2. 크레인사양 분리
			**********************************************************/
			sbImpPros.append("[작업예약등록 > 작업예약등록 > 크레인사양분리]::시작 \r\n");
			
			JDTORecord jRecode = JDTORecordFactory.getInstance().create();
			
			jRecode.setResultCode(logId);		//Log ID
			jRecode.setResultMsg(methodNm);	//Log Method Name
//			jrCrnSpec.setResultCode(logId);		//Log ID
//			jrCrnSpec.setResultMsg(methodNm);	//Log Method Name
			
			Vector vcLot = this.setCrnSpecSpr(jRecode, jsWbMtl);

			sbImpPros.append("[작업예약등록 > 작업예약등록 > 크레인사양분리]::종료: 크레인사양분리 작업예약수 ["+vcLot.size()+"] \r\n");
			
			
			/**********************************************************
			* 3. 작업예약 등록
			**********************************************************/
			JDTORecordSet jsLotMtl        = null;
			JDTORecord    jrRow           = null;
			
			int           lotCnt          = vcLot.size();	//크레인사양분리 작업예약수
			int           lotMtlSh        = 0;				//작업예약재료매수
			String        ydWbookId       = "";				//야드작업예약ID
			String        ydWbookIdFst    = "";				//야드작업예약ID(첫번째)
			String        sEmptyTYS       = "";				//조회된 공BED 들...

    		StringBuffer  sbImpPros_ToLoc = null;
    		
			commUtils.printLog(logId, "lotCnt:"+lotCnt, "SL");
			
			for( int ii = 0; ii < lotCnt; ii++ ) {
				
				jsLotMtl = (JDTORecordSet)vcLot.get(ii);	//Lot: 작업예약별 재료묶음
				lotMtlSh = jsLotMtl.size();					//작업재료 개수
				if( lotMtlSh <= 0 ) {
					continue;
				}

        		sbImpPros_ToLoc = new StringBuffer();
        		sbImpPros_ToLoc.append("[작업예약등록 > 작업예약등록 > 작업예약등록]::시작 \r\n");
				
        		
				//3.1. 작업예약ID 조회
				ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
				if( "".equals(ydWbookId) ) {
					throw new Exception("작업예약ID 생성 실패");
				}

        		sbImpPros_ToLoc.append("[작업예약등록 > 작업예약등록 > 작업예약등록 > 작업예약ID조회     ]::YD_WBOOK_ID ["+ydWbookId+"] \r\n");
				
				//크레인스케줄 기동용 첫번째 작업예약번호 저장
				if( ii == 0 ) {
					ydWbookIdFst = ydWbookId;
				}
				
				//3.2. 예외처리: "차량 하차"일 경우 작업예약별 TO위치 변경
				String sStkSpan = commUtils.trim(jsLotMtl.getRecord(0).getFieldString("YS_STK_COL_GP")).substring(2, 4);	//적치-SPAN
				if( "TR".equals(sStkSpan) ) {
					
					commUtils.printLog(logId, "예외처리: '차량 하차'일 경우 TO위치 변경 ... 시작.", "SL");
					sbImpPros.append("[작업예약등록 > 작업예약등록 > 작업예약등록 > 예외처리 > '차량 하차'일경우TO위치변경]::시작 \r\n");
					
					//3.2.1. 차량 하차 시 "빌렛/각강"이면 TO위치 변경
					//         --------------------------------------------------------
					//         1) TO위치 변경: 소형적치대 임시BED (GFTY01-03) 중 공BED인 열 조회
					//            if( 소형적치대 임시BED (GFTY01-03) 중 공BED인 열이 없으면 )
					//               3) 스케줄코드 변경: GFBS01LM -> GFTR01LM
					//               4) TO위치     변경: ""
					//         ----------------------------------------------------------
					
					//"대형옥외 밴드쏘보급" 이면...
					if( constant.SCH_CD_GFBS01LM.equals(ydSchCd) ) {
						
						//TO위치 변경 : 소형적치대 임시BED (GFTY01-03) 중 공BED인 열 조회
						JDTORecord    jrParam2  = JDTORecordFactory.getInstance().create();
								      jrParam2.setField("YS_STK_COL_GP", "GFTY");
								      jrParam2.setField("EMPTY_TY"     , sEmptyTYS);	//사용된 공BED (ex: 'GFTY01,GFTY02'): 공BED 검색 시 제외시킴
						JDTORecordSet jsEmptyTY = commDao.select(jrParam2, getEmptyCol, logId, methodNm, "소재가 없는 열 조회");
						
						//GFTY01-03 중 공BED가 존재하면 To위치 결정
						if( jsEmptyTY.size() > 0 ) {
							
							jsEmptyTY.first();
							JDTORecord jrEmptyTY = jsEmptyTY.getRecord();
							ydToLocGuide = commUtils.trim(jrEmptyTY.getFieldString("YS_STK_COL_GP"));	//조회된 공BED 열로 결정
							
							sEmptyTYS += ydToLocGuide + ",";	//사용된 공BED 모음 (ex: 'GFTY01,GFTY02'): 공BED 검색 시 제외시킴
							
							sbImpPros.append("[작업예약등록 > 작업예약등록 > 작업예약등록 > 예외처리 > '차량 하차'일경우 > 조회된공BED열로결정]:: ydSchCd ["+ydSchCd+"], ydToLocGuide ["+ydToLocGuide+"], sEmptyTYS ["+sEmptyTYS+"]\r\n");
						
						//GFTY01-03 중 공BED가 존재하지 않으며 스케줄코드 변경, To위치 미결정
						} else {
							//1) 스케줄코드 변경
							ydSchCd = constant.SCH_CD_GFTR01LM;	//대형옥외 이송입고
							
							//2) TO위치 변경 : 소형적치대 임시BED (GFTY01-03) 중 공BED가 없으면 야드에서 최적적치대검색
							ydToLocGuide = "";
							
							sbImpPros.append("[작업예약등록 > 작업예약등록 > 작업예약등록 > 예외처리 > '차량 하차'일경우 > 임시BED(GFTY01-03)중'공BED'가없어야드최적적치대검색]:: ydSchCd ["+ydSchCd+"], ydToLocGuide ["+ydToLocGuide+"], sEmptyTYS ["+sEmptyTYS+"]\r\n");
						}
					}

					commUtils.printLog(logId, "예외처리: '차량 하차'일 경우 TO위치 변경 ... 종료. ydToLocGuide ["+ydToLocGuide+"]", "SL");
					sbImpPros.append("[작업예약등록 > 작업예약등록 > 작업예약등록 > 예외처리 > '차량 하차'일경우TO위치변경]::종료 \r\n");
				}
				
				//3.2. 작업예약 등록
				jrParam.setField("YD_WBOOK_ID"       , ydWbookId     );	//야드작업예약ID
				jrParam.setField("MODIFIER"          , modifier      ); //수정자
				jrParam.setField("YD_GP"             , ydGp          ); //야드구분
				jrParam.setField("YD_BAY_GP"         , ydBayGp       ); //야드동구분
				jrParam.setField("YD_SCH_CD"         , ydSchCd       ); //야드스케줄코드
				jrParam.setField("YD_SCH_PRIOR"      , ydSchPrior    ); //야드스케줄우선순위
				jrParam.setField("YD_SCH_PROG_STAT"  , "W"           ); //야드스케줄진행상태(W:스케줄수행대기)
				jrParam.setField("YD_SCH_ST_GP"      , "M"           ); //야드스케줄기동구분(M:Manual)
				jrParam.setField("YD_SCH_REQ_GP"     , "M"           ); //야드스케줄요청구분(M:이적)
				jrParam.setField("YD_AIM_YD_GP"      , ydGp          ); //야드목표야드구분
				jrParam.setField("YD_AIM_BAY_GP"     , ydAimBayGp    ); //야드목표동구분
				jrParam.setField("YD_TO_LOC_DCSN_MTD", ydToLocDcsnMtd); //야드TO위치결정방법
				jrParam.setField("YD_TO_LOC_GUIDE"   , ydToLocGuide  ); //야드To위치Guide
				jrParam.setField("YD_WRK_PLAN_CRN"   , ydWrkCrn      ); //야드작업계획크레인
				jrParam.setField("CAR_YD_WBOOK_ID"   , ydPrepSchId   ); //야드준비스케쥴ID(차량상차작업예약ID)
				
				commDao.insert(jrParam, insWrkBook_ystot, logId, methodNm, "TB_YS_WRKBOOK");

				sbImpPros_ToLoc.append("[작업예약등록 > 작업예약등록 > 작업예약등록 > 작업예약_Insert    ]::YD_WBOOK_ID ["+ydWbookId+"], YD_SCH_CD["+ydSchCd+"], YD_SCH_PRIOR["+ydSchPrior+"], YD_TO_LOC_GUIDE["+ydToLocGuide+"], YD_WRK_PLAN_CRN["+ydWrkCrn+"] \r\n");
				
				//3.3. 작업예약재료 등록
				String sSSTL_NO = "";
				for( int jj = 0; jj < lotMtlSh; jj++ ) {
					
					jrRow = jsLotMtl.getRecord(jj);	//작업재료
					
					JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
					jrRtn1.setField("YD_WBOOK_ID"   , ydWbookId                                            );	//야드작업예약ID
					jrRtn1.setField("SSTL_NO"      	, commUtils.trim(jrRow.getFieldString("SSTL_NO"      )));	//재료번호
					jrRtn1.setField("YS_STK_COL_GP" , commUtils.trim(jrRow.getFieldString("YS_STK_COL_GP")));	//야드적치열구분
					jrRtn1.setField("YS_STK_BED_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO")));	//야드적치Bed번호
					jrRtn1.setField("YS_STK_LYR_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO")));	//야드적치단번호
//					jrRtn1.setField("YS_STK_SEQ_NO" , ydWbookId                                            );	//야드작업예약ID <- 스케줄 생성 시 "크레인작업재료에 현재 적치단 저장위치 Update"에서 처리 ??
					jrRtn1.setField("MODIFIER"     	, modifier                                             );	//등록자
					
					commDao.insert(jrRtn1, insWrkBookMtl_ystot, logId, methodNm, "TB_YS_WRKBOOKMTL");

					sSSTL_NO += commUtils.trim(jrRow.getFieldString("SSTL_NO")) + " ";
				}

				sbImpPros_ToLoc.append("[작업예약등록 > 작업예약등록 > 작업예약등록 > 작업예약재료_Insert]::YD_WBOOK_ID ["+ydWbookId+"], SSTL_NO["+sSSTL_NO+"] \r\n");
				sbImpPros_ToLoc.append("[작업예약등록 > 작업예약등록 > 작업예약등록]::종료 \r\n");
				sbImpPros.append("[=========================================================================================================================================] \r\n");
				
				
				//주요진행내용로그: 단계별항목정보 ([logId],[재료번호],[적치열],[적치BED],[적치단],[적치SEQ],[야드To위치Guide],[야드스케줄코드],[야드작업계획크레인])
				String sSCH_CONTENTS = logId+","+sSSTL_NO+","+commUtils.trim(jrParam.getFieldString("YS_STK_COL_GP"))+",,,,"+ydToLocGuide+","+ydSchCd+","+ydWrkCrn;	
				String sParamVal = "WB"+"#"+"G"+"#"+"E"+"#"+ydWbookId+"#"+" "+"#"+"2"+"#"+sbImpPros.toString()+sbImpPros_ToLoc.toString()+"\r\n"+"#"+sSCH_CONTENTS;
				JDTORecord   jrSchlog = JDTORecordFactory.getInstance().create();
						     jrSchlog.setField("PARAM_VALUE", sParamVal);
				EJBConnector SchLogConn = new EJBConnector("default", "EbtYsSchSeEJB", this);
							 SchLogConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrSchlog });
			}
			
			/**********************************************************
			* 4. 크레인스케줄(YSYSJ502) 전송용 기초 전문 생성
			**********************************************************/
			//크레인스케줄 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);

			jrYdMsg.setField("YD_WBOOK_ID"     , ydWbookIdFst ); //야드작업예약ID(첫번째꺼만)
			jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케줄코드
			jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케줄기동구분(M:Manual)
			jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케줄요청구분(M:이적)
			
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrYdMsg;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * 차량입고LOT등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regCarFtmvLot(GridData gdReq) throws DAOException {
		String methodNm = "차량입고LOT등록[YsTotYsJspSeEJB.regCarFtmvLot] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			
			JDTORecord recCarSch        	= null;
			JDTORecord rcvMsgCol			= null;
			JDTORecord rcvMsg				= null;
			
			String szTRN_EQP_CD    			= null;
			String szARR_WLOC_CD			= null;
			String szARR_YD_PNT_CD			= null;
		    String szYD_WBOOK_ID   			= "";
		    String szYD_SCH_CD				= "";
		    String szSSTL_NO				= "";
			
			String szMsg           			= null;
			
			JDTORecordSet rsResult 			= null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String ydPrepSchId = commDao.getSeqId(logId, methodNm, "PrepSch");
			
			if ("".equals(ydPrepSchId)) {
				throw new Exception("입고이송Lot 준비스케쥴ID 생성 실패");
			}
			
			jrParam.setField("YD_PREP_SCH_ID", 	ydPrepSchId); 										//야드준비스케쥴ID
			jrParam.setField("YD_SCH_CD", 		gdReq.getParam("YD_SCH_CD")); 						//스케줄코드
			jrParam.setField("YD_PREP_WK_ST", 	commUtils.nvl(gdReq.getParam("YD_PREP_WK_ST"),"")); //야드준비작업상태
			jrParam.setField("YD_AIM_BAY_GP", 	gdReq.getParam("YD_AIM_BAY_GP")); 					//목적동
			jrParam.setField("YD_TO_LOC_GUIDE", gdReq.getParam("YD_TO_LOC_GUIDE")); 				//야드To위치Guide

			//등록 할  레코드 수  gdReq.getHeader("CHECK").getRowCount() 결과값 0 막음
//			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			int rowCnt = 100;
			
			szYD_SCH_CD = gdReq.getParam("YD_SCH_CD"); 						//스케줄코드
            szMsg = "\n\t YD_PREP_SCH_ID   	: " 	+ ydPrepSchId 
                  + "\n\t YD_SCH_CD      	: " 	+ szYD_SCH_CD 
                  + "\n\t YD_PREP_WK_ST     : " 	+ commUtils.nvl(gdReq.getParam("YD_PREP_WK_ST"),"") 
                  + "\n\t YD_AIM_BAY_GP   	: " 	+ gdReq.getParam("YD_AIM_BAY_GP") 
                  + "\n\t YD_GP   			: " 	+ gdReq.getParam("YD_GP") 
                  + "\n\t YD_BAY_GP   		: " 	+ gdReq.getParam("YD_BAY_GP") 
                  + "\n\t userid   			: " 	+ gdReq.getParam("userid") 
                  ;

      		commUtils.printLog(logId, szMsg, "");
			
			for (int ii = 0; ii < rowCnt; ii++) {
				szSSTL_NO = "";
				szSSTL_NO = commUtils.getValue(gdReq, "SSTL_NO", ii);
				if (!"".equals(szSSTL_NO)) {
					//준비재료 등록
					jrParam.setField("SSTL_NO"			, commUtils.getValue(gdReq, "SSTL_NO", ii)); 
					jrParam.setField("YD_GP"			, commUtils.getValue(gdReq, "YS_STR_LOC", ii).substring(0,1)); 
					jrParam.setField("YS_STK_COL_GP"	, commUtils.getValue(gdReq, "YS_STR_LOC", ii).substring(0,6)); 
					jrParam.setField("YS_STK_BED_NO"	, commUtils.getValue(gdReq, "YS_STR_LOC", ii).substring(6,8)); 
					jrParam.setField("YS_STK_LYR_NO"	, commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii)); 
					jrParam.setField("YS_STK_SEQ_NO"	, commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii)); 
					
					commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepMtl", logId, methodNm, "준비재료 등록");
				}
				
			}
			
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepSchAimBay", logId, methodNm, "준비스케줄 등록");
			
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updToLocGuide", logId, methodNm, "야드To위치Guide 입력");
			
// ****************************************
// 2025.08.21 차량 예약 등록 START
// ****************************************
	    	boolean isReqCheck = true; // 차량 예약 등록

			if( isReqCheck ){
				String modifier 			= commUtils.trim(gdReq.getParam("userid"));	//수정자
			    String sYD_SCH_CD 			= "";  // 스케줄코드 x, 열정보 o
				String sWLOC_CD				= null;
				String sYD_PNT_CD			= null;
			    String szYD_CAR_SCH_ID 		= "";
				String stlNos 				= "";
				String sYS_STK_COL_GP 		= "";
				
			    int intRtnVal 				= 0 ;
			    
				JDTORecord		recPara					= null;	
				JDTORecord	  	sndRecord	= JDTORecordFactory.getInstance().create();
				GridData inGridData = new GridData();


				sYD_SCH_CD = gdReq.getParam("YD_SCH_CD");
				
				// 차량위치로 TB_YS_STKCOL에서 개소코드와 야드포인트를 읽어온다. 
				jrParam.setField("YS_STK_COL_GP", sYD_SCH_CD);

				szMsg = "\n\t YS_STK_COL_GP   		: " 	+ sYD_SCH_CD;

		      	commUtils.printLog(logId, szMsg, "");

				JDTORecordSet jsCol = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkCol1", logId, methodNm, "적치열 개소코드,포인트 조회");
				
				if(jsCol != null && jsCol.size() > 0) {
					
					sWLOC_CD		= commUtils.trim(jsCol.getRecord(0).getFieldString("WLOC_CD"));
					sYD_PNT_CD		= commUtils.trim(jsCol.getRecord(0).getFieldString("YD_PNT_CD"));
					szYD_CAR_SCH_ID = commDao.getSeqId(logId, methodNm, "CarSch");
					
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("REGISTER",         modifier);
				    recPara.setField("YD_EQP_WRK_STAT",  "L");                    			// 야드설비작업상태
				    recPara.setField("YD_EQP_ID",        YsConstant.YD_TS_CAR_EQP_ID);		// 야드설비ID
				    recPara.setField("TRN_EQP_CD",       szTRN_EQP_CD);               		// 운송장비코드
				    recPara.setField("YD_CAR_USE_GP",    "L");                              // 차량사용구분(L:구내,G:출하)
				    recPara.setField("SPOS_WLOC_CD",     sWLOC_CD);                			// 발지개소코드
				    recPara.setField("YD_CARLD_LEV_LOC", "");          						// 야드상차출발위치
				    recPara.setField("YD_CARLD_LEV_DT",  commUtils.getDateTime14());   		// 상차출발일시
				    recPara.setField("YD_BAYIN_WO_SEQ",  "99");                    			// 입동지시순번 - WC 수정 : 각강 입고 차량의 경우 입고검수완료 전까지 99로 설정함, 검수 이후 9로 셋팅
				    recPara.setField("YD_CAR_PROG_STAT", YsConstant.YD_CARLD_LEV);          // 상차출발상태
				    recPara.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);

//				    intRtnVal = commDao.insert(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.insYdCarsch", logId, methodNm, "차량스케줄 등록"); // 2025.10.22 차량에 재료가 중복으로 등록되는 현상으로 주석 처리
//			        if( intRtnVal <= 0 ){
//			        	szMsg= methodNm + "개소코드["+sWLOC_CD+"] : 차량스케줄["+szYD_CAR_SCH_ID+"] 생성 시 오류발생 - 반환값 : " + intRtnVal;
//			        	commUtils.printLog(logId, szMsg, "SL");
//			        	sndRecord.setTaskCode("-1");
//			        	return sndRecord;
//			        }
					
			        szYD_CAR_SCH_ID = commUtils.trim(recPara.getFieldString("YD_CAR_SCH_ID"));
			        szMsg= methodNm + "개소코드["+sWLOC_CD+"] : 차량스케줄["+szYD_CAR_SCH_ID+"] 생성 완료 - 반환값 : " + intRtnVal;
			        commUtils.printLog(logId, szMsg, "SL");
			      
					for (int ii = 0; ii < rowCnt; ii++) {
						szSSTL_NO = "";
						szSSTL_NO = commUtils.getValue(gdReq, "SSTL_NO", ii);
						if (!"".equals(szSSTL_NO)) {
							
							jrParam = JDTORecordFactory.getInstance().create();
							jrParam.setResultCode(logId);	//Log ID
							jrParam.setResultMsg(methodNm);	//Log Method Name
							jrParam.setField("SSTL_NO", 		commUtils.getValue(gdReq, "SSTL_NO", ii));
							jrParam.setField("LOC"	  , 		commUtils.getValue(gdReq, "YS_STR_LOC", ii)); 
							jrParam.setField("YD_CAR_SCH_ID", 	szYD_CAR_SCH_ID);
							
							// 차량 이송 재료 등록
//							commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insSbCarmvmtl", logId, methodNm, "차량 이송재료 등록"); // 2025.10.21 차량에 재료가 중복으로 등록되는 현상으로 주석 처리
							
							if(ii != 0) {
								stlNos += ",";
							}
							
							stlNos += szSSTL_NO;	// 재료번호
							sYS_STK_COL_GP = commUtils.getValue(gdReq, "YS_STK_COL_GP", ii);
						}
						
					}
			     
					// 작업예약등록
					inGridData.addParam("SSTL_NOS", 		stlNos);						// 재료번호들
					inGridData.addParam("YS_STK_COL_GP", 	sYS_STK_COL_GP);				// 야드적치열구분(6자리 이상)
					inGridData.addParam("YD_TO_LOC_GUIDE", 	sYD_SCH_CD);		// 야드To위치Guide
					inGridData.addParam("YD_WRK_CRN", 		"");							// 야드작업크레인(작업자지정 크레인)
					inGridData.addParam("YD_PREP_SCH_ID", 	ydPrepSchId);					// 야드준비스케쥴ID(차량상차작업예약ID)
					inGridData.addParam("userid", 			modifier);						// 수정자
					
					szMsg = "\n\t SSTL_NO   		: " 	+ stlNos 
	                	  + "\n\t YS_STK_COL_GP     : " 	+ sYS_STK_COL_GP
	                	  + "\n\t YD_TO_LOC_GUIDE     : " 	+ sYD_SCH_CD
	                	  ;

		      		commUtils.printLog(logId, szMsg, "");
					
					jrRtn = updbtMvStkWrkBook(inGridData);
					
				}			
				
			}
			
// ****************************************
// 2025.08.21 차량 예약 등록 END
// ****************************************
			
			
			//이송LOT를 생성한 동에 도착한 이송차량이 있는지 체크 (차량스케줄에 위치가 이송LOT생성한 동이고 상차도착상태에 작업예약이 없는 스케줄)
//			jrParam.setField("YD_GP"		, gdReq.getParam("YD_GP")); //야드구분
//			jrParam.setField("YD_BAY_GP"	, gdReq.getParam("YD_BAY_GP")); //동구분
//			
//			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchArrNoJob", logId, methodNm, "작업예약 없는 도착차량 조회");
//
//			if (jsCarSch != null && jsCarSch.size() > 0) {
//			
//				jsCarSch.first();
//				rcvMsg = jsCarSch.getRecord(); //상차도착전문 정보를 담는다..  	
//				
//				szTRN_EQP_CD 		= rcvMsg.getFieldString("TRN_EQP_CD");
//				szARR_WLOC_CD		= rcvMsg.getFieldString("ARR_WLOC_CD");
//				szARR_YD_PNT_CD		= rcvMsg.getFieldString("ARR_YD_PNT_CD");
//				
//
//				//운송장비코드로 차량스케줄 조회 --------------------------------------------------------------------------------	    
//				jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD);
//		    	
//				rsResult = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschDaoTrnEqpCd", logId, methodNm, "차량스케줄을 조회"); 	
//		    	
//				if (rsResult == null || rsResult.size() < 0) {
//					szMsg="["+methodNm+"] 이송Lot생성 후 차량스케줄 조회시 운송장비코드["+szTRN_EQP_CD+"] : parameter error";
//					commUtils.printLog(logId, szMsg, "SL");
//					throw new Exception(szMsg);
//				} else if (rsResult.size() > 1) {
//					szMsg= "[" + methodNm + "] 이송Lot생성 후 차량스케줄 조회 시 오류발생 - 운송장비코드로 차량스케줄이 여러건["+rsResult.size()+"]이 존재합니다.";
//					commUtils.printLog(logId, szMsg, "SL");
//					throw new Exception(szMsg);
//				}
//		    	
//		    	rsResult.first();
//		    	recCarSch = rsResult.getRecord(); 
//
//
//		    	//착지개소코드와 착지야드포인트코드로 적치열을 조회한다. ------------------------------------------------------------
//		    	jrParam.setField("WLOC_CD",   szARR_WLOC_CD);
//		    	jrParam.setField("YD_PNT_CD", szARR_YD_PNT_CD);
//
//		    	rsResult = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCd", logId, methodNm, "적치열 조회"); 
//		    	
//		    	if (rsResult == null || rsResult.size() <= 0) {
//		    		szMsg="["+methodNm+"] 수신된 착지개소코드["+szARR_WLOC_CD+"]와 수신된 착지야드포인트코드["+szARR_YD_PNT_CD+"] 적치열 조회 시 적치열이 존재하지 않습니다.";
//		    		commUtils.printLog(logId, szMsg, "SL");
//					throw new Exception(szMsg);
//		    	}
//		    	
//		    	rsResult.first();
//		    	rcvMsgCol = rsResult.getRecord();
//		    	
//	    		if(commUtils.trim(rcvMsgCol.getFieldString("YD_STK_COL_ACT_STAT")).equals("N")) {
//	    			szMsg="["+methodNm+"] 차량정지위치가 사용 불가상태입니다.";
//		    		commUtils.printLog(logId, szMsg, "SL");
//					throw new Exception(szMsg);
//		    	}
//		    	
//		    	
//		    	//작업예약정보에서 --------------------------------------------------------------------------------------------
//		    	//운송장비코드 , 야드차량사용구분으로  조회 
//		    	//해당된 작업예약 재료 정보를 가지고 온다
//	    		jrParam.setField("TRN_EQP_CD",    szTRN_EQP_CD);
//		    
//	    		rsResult = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookMtlbyCarUsrGpTrnEqpCd", logId, methodNm, "작업예약을 조회"); 
//				
//		    	if (rsResult == null || rsResult.size() < 0 ) {
//					szMsg="["+methodNm+"] 운송장비코드["+szTRN_EQP_CD+"]로 작업예약 조회 시 : parameter error";
//					commUtils.printLog(logId, szMsg, "SL");
//					throw new Exception(szMsg);
//				} else if (rsResult.size() == 0 ){
//					
//				} else {
//					
//			    	rsResult.first();
//			    	JDTORecord recOutTemp = rsResult.getRecord();
//			    	
//					szYD_WBOOK_ID	= commUtils.trim(recOutTemp.getFieldString("YD_WBOOK_ID")); 
//					szYD_SCH_CD    	= commUtils.trim(recOutTemp.getFieldString("YD_SCH_CD")); 
//				}
//	    		
//	    		rcvMsg.setField("YD_CARLD_WRK_BOOK_ID"	, szYD_WBOOK_ID);
//	    		rcvMsg.setField("YD_SCH_CD"				, szYD_SCH_CD);
//	    		
//	    		//소재차량 공차도착 실적 호출
//				EJBConnector ejbConn = new EJBConnector("default", "YsCommCarTSMvSeEJB", this);
//				
//				jrRtn = (JDTORecord)ejbConn.trx("procLDMatlCarArr", new Class[] { String.class, JDTORecord.class, JDTORecord.class, JDTORecord.class }, new Object[] { logId, rcvMsg, recCarSch , rcvMsgCol });
//	    		
//			} else {
//				
//				//상차위치가 Lot편성 동과 같고 작업예약 없는 상차출발차량 조회 : 이 차량이 들어올 차량임으로 포인트 지시를 할 필요 없음  
//				jrParam.setField("YD_GP"		, gdReq.getParam("YD_GP")); //야드구분
//				jrParam.setField("YD_BAY_GP"	, gdReq.getParam("YD_BAY_GP")); //동구분
//				
//				jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchStartToLd", logId, methodNm, "상차위치 있고 작업예약 없는 상차출발차량 조회");
//				
//				if (jsCarSch.size() == 0) {
//					
//					//차량스케줄 중에 상차출발이면서 도착포인트가 없는 차량이 있으면 포인트 지시 처리
//					jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchNoLdStopLoc", logId, methodNm, "상차위치 없는 상차출발차량 조회");
//					
//					if (jsCarSch != null && jsCarSch.size() > 0) {
//						
//						jsCarSch.first();
//						rcvMsg = jsCarSch.getRecord(); 
//						
//						String sTRN_EQP_CD 		= rcvMsg.getFieldString("TRN_EQP_CD");
//						String sYD_CAR_SCH_ID	= rcvMsg.getFieldString("YD_CAR_SCH_ID");
//						String sYD_GP			= gdReq.getParam("YD_GP");
//// 2025.08.22 기존 로직 수정						
////						String sTO_LOC 			= gdReq.getParam("YD_GP") + gdReq.getParam("YD_BAY_GP") + "TR1";
//						String sTO_LOC 			= szYD_SCH_CD.substring(0,6); 
//						
//						String sWLOC_CD		= null;
//						String sYD_PNT_CD	= null;
//						
//						String modifier 			= commUtils.trim(gdReq.getParam("userid"));	//수정자
//						
//						JDTORecord jrYdMsg 			= commUtils.getParam(logId, methodNm, modifier);
//						jrYdMsg.setResultCode(logId);	//Log ID
//						jrYdMsg.setResultMsg(methodNm);	//Log Method Name
//						
//						String currDate 			= commUtils.getDateTime14();	//현재시각
//						String sTRN_WRK_FULLVOID_GP = "E";	//공차
//						String sSPOS_WLOC_CD		= "";
//						String sYD_PNT_CD1			= "";
//						String sYD_CARLD_STOP_LOC	= "";
//						String sARR_WLOC_CD			= "";
//						String sYD_PNT_CD3			= "";
//						String sYD_CARUD_STOP_LOC	= "";
//						
//						//-------------------------------------------------------------------------------------------
//						//소재차량Point지시 
//
//						
//						//목표지위치로 TB_YS_STKCOL에서 개소코드와 야드포인트를 읽어온다. 
//						jrParam.setField("YS_STK_COL_GP", sTO_LOC);
//
//						szMsg = "\n\t YS_STK_COL_GP   		: " 	+ sTO_LOC;
//
//				      	commUtils.printLog(logId, szMsg, "");
//						
//						JDTORecordSet jsCol = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkCol1", logId, methodNm, "적치열 개소코드,포인트 조회");
//						
//						if(jsCol != null && jsCol.size() > 0) {
//							sWLOC_CD	= commUtils.trim(jsCol.getRecord(0).getFieldString("WLOC_CD"));
//							sYD_PNT_CD	= commUtils.trim(jsCol.getRecord(0).getFieldString("YD_PNT_CD"));
//							
//							if("".equals(sWLOC_CD) || "".equals(sYD_PNT_CD)) {
//								
//								throw new Exception(sTO_LOC + " 의 개소코드 또는 야드포인트에 NULL 값이 있습니다.");
//							}
//							
//							if(!"".equals(commUtils.trim(jsCol.getRecord(0).getFieldString("TRN_EQP_CD")))) {
//								
//								throw new Exception(sTO_LOC + " 에 이미 " + commUtils.trim(jsCol.getRecord(0).getFieldString("TRN_EQP_CD")) + " 운송장비가 점유하고  있습니다.");
//							}
//							
//						} else {
//							throw new Exception(sTO_LOC + " 의 개소코드와 야드포인트를 TB_YS_SCKCOL 에서 찾지 못했습니다.");
//						}
//						
//						// YSTSJ011(소재차량Point지시)
//						jrYdMsg.setField("JMS_TC_CD"         	, YsConstant.YSTSJ011);
//						jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, currDate    ); //JMSTC생성일시
//						jrYdMsg.setField("TRN_EQP_CD"        	, sTRN_EQP_CD); //운송장비코드
//						jrYdMsg.setField("WLOC_CD"     	 		, sWLOC_CD);
//						jrYdMsg.setField("YD_PNT_CD"     		, sYD_PNT_CD); 
//						jrYdMsg.setField("PNT_WO_GP"     		, "A"    	);
//						jrYdMsg.setField("PNT_WO_DT"     		, currDate ); 
//// 2025.08.19 YD_MSG_NM(야드메시지) 추가	
//						jrYdMsg.setField("YD_MSG_NM"			, "특수강정정야드 -> 현황관리 -> 저장위치별 현황 -> 차량이송LOT 실행. 사용자[" + modifier + "]");
//						
//						//전송할 전문에 추가
//						jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
//						
//						String sYD_CARLD_PNT_WO_DT = "";
//						String sYD_CARUD_PNT_WO_DT = "";
//
//						//차량스케줄의 개소코드, 야드포인트, 정지위치를 UDPATE 한다.
//						if("E".equals(sTRN_WRK_FULLVOID_GP)) { //공차:상차
//							sSPOS_WLOC_CD 		= sWLOC_CD;
//							sYD_PNT_CD1			= sYD_PNT_CD;	
//							sYD_CARLD_STOP_LOC 	= sTO_LOC;
//							sYD_CARLD_PNT_WO_DT = currDate;
//						} else { //영차:하차
//							sARR_WLOC_CD 		= sWLOC_CD;
//							sYD_PNT_CD3			= sYD_PNT_CD;	
//							sYD_CARUD_STOP_LOC 	= sTO_LOC;
//							sYD_CARUD_PNT_WO_DT = currDate;
//						}
//						
//						jrParam.setField("YD_CAR_PROG_STAT"		, "");  //""이면 이전 상태 유지된다.
//						jrParam.setField("SPOS_WLOC_CD"			, sSPOS_WLOC_CD);
//						jrParam.setField("YD_CARLD_PNT_WO_DT"	, sYD_CARLD_PNT_WO_DT);
//						jrParam.setField("YD_PNT_CD1"			, sYD_PNT_CD1);
//						jrParam.setField("YD_CARLD_STOP_LOC"	, sYD_CARLD_STOP_LOC); 
//						jrParam.setField("ARR_WLOC_CD"			, sARR_WLOC_CD);
//						jrParam.setField("YD_CARUD_PNT_WO_DT"	, sYD_CARUD_PNT_WO_DT);
//						jrParam.setField("YD_PNT_CD3"			, sYD_PNT_CD3);
//						jrParam.setField("YD_CARUD_STOP_LOC"	, sYD_CARUD_STOP_LOC); 
//						jrParam.setField("YD_CAR_SCH_ID"		, sYD_CAR_SCH_ID); 
//						
//						//이송차량스케줄 수정 - 차량포인트 수정
//						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updMvCarSchPntWo", logId, methodNm, "차량포인트 지시 수정");
//						
//						
//						jrParam.setField("YD_GP"				, sYD_GP);
//						jrParam.setField("TRN_EQP_CD"			, sTRN_EQP_CD);
//						jrParam.setField("YS_STK_COL_GP"		, sTO_LOC);
//						
//						//적치열 포인트지시 예약하기
//						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkColPntWo", logId, methodNm, "적치열 포인트지시 예약하기");
//						
//						//TB_YD_CARPOINT 포인트지시 예약하기 
//						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCarPointPntWo", logId, methodNm, "TB_YD_CARPOINT 포인트지시 예약하기");
//						
//						if("E".equals(sTRN_WRK_FULLVOID_GP)) { //공차:상차
//
//							jrParam.setField("YD_GP", sYD_GP);
//							jrParam.setField("YD_SCH_CD", sTO_LOC.substring(0,2) + "TR___M");
//							
//							JDTORecordSet jsPrepSch = commDao.select(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.getPrepSchWithOutTrnEqpCd", logId, methodNm, "예약 안걸린 이송LOT ID 조회");
//
//							if(jsPrepSch != null && jsPrepSch.size() > 0) {
//								
//								String sYD_PREP_SCH_ID	= commUtils.trim(jsPrepSch.getRecord(0).getFieldString("YD_PREP_SCH_ID"));
//								
//								jrParam.setField("TRN_EQP_CD"			, sTRN_EQP_CD);
//								jrParam.setField("YD_PREP_SCH_ID"		, sYD_PREP_SCH_ID);
//								
//								//TB_YS_PREPSCH 이송LOT 예약하기
//								commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updPreSchYdToLocGuide", logId, methodNm, "이송LOT 예약하기");
//							}		
//						}
//						//-------------------------------------------------------------------------------------------
//					}
//
//					
//				}
//			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of regCarFtmvLot
	
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
		String methodNm = "크레인사양분리[YsTotYsJspSeEJB.setCrnSpecSpr] < " + jrCrnSpec.getResultMsg();
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
			
			int    iYD_MTL_WT          = 0;		//소재 중량
			int    iYD_MTL_WT_SUM      = 0;		//소재 중량 합계

			int    iWM_STK_SH          = 4;		//품목별 적치단의 최대 적치 가능 매수 (= 크레인 작업가능 재료매수)
			int    iWM_LOT_CNT         = 0;		//크레인 작업 재료매수
			
			int    rowCnt               = jsWrkMtl.size();	//소재 개수
			
			//크레인사양분리 기준: [동일 "열/BED/단"에 있는 작업재료 묶음] + [차량 상/하차 작업이면 작업소재 중량합계를 12톤 이하로 묶음]
			for( int ii = 0; ii < rowCnt; ii++ ) {
				
				jrRow = jsWrkMtl.getRecord(ii);
				
				szYS_STK_COL_GP = commUtils.trim(jrRow.getFieldString("YS_STK_COL_GP"));
				szYS_STK_BED_NO = commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO"));
				szYS_STK_LYR_NO = commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO"));
				iYD_MTL_WT      = Integer.parseInt(commUtils.nvl(jrRow.getFieldString("YD_MTL_WT"),"0"));	//소재 중량
				
				if( ii > 0 ) {
					
					String sLoc_BEF  = szYS_STK_COL_GP_BEF + szYS_STK_BED_NO_BEF + szYS_STK_LYR_NO_BEF;
					String sLoc_CUR  = szYS_STK_COL_GP     + szYS_STK_BED_NO     + szYS_STK_LYR_NO;

					iYD_MTL_WT_SUM = iYD_MTL_WT_SUM + iYD_MTL_WT;
					
					//(이전 위치정보 != 현재 위치정보) || (작업소재 중량합계를 20톤 이하로 제한)
					if( !(sLoc_BEF).equals(sLoc_CUR) || (iYD_MTL_WT_SUM / 100) > 200  || iWM_LOT_CNT % iWM_STK_SH == 0 ) {

						// "크레인사양분리결과"에 이전 Lot 추가
						vcLot.add(jsLot);
						
						// 신규 Lot 생성 & 작업재료 추가
						jsLot = JDTORecordFactory.getInstance().createRecordSet("");
						
						szYS_STK_COL_GP_BEF  = szYS_STK_COL_GP;
						szYS_STK_BED_NO_BEF  = szYS_STK_BED_NO;
						szYS_STK_LYR_NO_BEF  = szYS_STK_LYR_NO;

						iYD_MTL_WT_SUM       = iYD_MTL_WT;
						iWM_LOT_CNT          = 0;
					}
					
				} else {
					szYS_STK_COL_GP_BEF  = szYS_STK_COL_GP;
					szYS_STK_BED_NO_BEF  = szYS_STK_BED_NO;
					szYS_STK_LYR_NO_BEF  = szYS_STK_LYR_NO;
					
					iYD_MTL_WT_SUM       = iYD_MTL_WT;
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
	 *      [A] 오퍼레이션명 : 크레인스케줄전문정리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecordSet jsMsg
	 *      @param String logId
	 *      @param String mthdNm
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord setCrnSchMsg(JDTORecordSet jsMsg, String logId, String mthdNm) throws DAOException {
		String methodNm = "크레인스케줄전문정리[YsTotYsJspSeEJB.setCrnSchMsg] < " + mthdNm;

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			if( !commUtils.isEmpty(jsMsg) ) {
				
				String  ydEqpId   = "";		//야드설비ID(크레인)
				String  ydEqpStat = "";		//야드설비상태
				boolean fstYn     = false;	//동일크레인에서 첫번째 여부
				
				//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord    jrParam = commUtils.getParam(logId, methodNm, "");
				JDTORecord    jrRow   = null;
				JDTORecordSet jsChk   = null;

				int rowCnt = jsMsg.size();
				
				for( int ii = rowCnt - 1; ii >= 0; ii-- ) {
					
					jrRow = jsMsg.getRecord(ii);
					jrRow.setResultCode(logId);		//Log ID
					jrRow.setResultMsg(methodNm);	//Log Method Name	
					
					fstYn   = true;
					ydEqpId = commUtils.trim(jrRow.getFieldString("YD_EQP_ID"));
					
					for( int jj = 0; jj < ii; jj++ ) {
						if( ydEqpId.equals(jsMsg.getRecord(jj).getFieldString("YD_EQP_ID")) ) {
							fstYn = false;
							break;
						}
					}
					
					//동일크레인에서 첫번째 이면
					if( fstYn ) {
						
						//크레인 상태 확인
						jrParam.setField("YD_EQP_ID", ydEqpId); //야드설비ID

						jsChk = commDao.select(jrParam, getStatEqp, logId, methodNm, "설비상태조회");
						
						ydEqpStat = "";

						if( jsChk.size() > 0 ) {
							ydEqpStat = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT"));
						}

						//크레인이 작업대기 상태이면 크레인스케줄 전송
						if( "W".equals(ydEqpStat) ) {	//W: 대기(wait)
							jrRtn = commUtils.addSndData(jrRtn, ystotYsComm.getCrnSchMsg(jrRow));
						}
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
	}
	
	/**
	 *      [A] 오퍼레이션명 : 저장위치별현황 - 차량입고LOT등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord callregCarFtmvLot(GridData gdReq) throws DAOException {
		String methodNm = "이적작업팝업-작업예약등록[YsTotYsJspSeEJB.callregCarFtmvLot] < " + gdReq.getNavigateValue();
		String logId    = gdReq.getIPAddress();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord jrRtn = this.regCarFtmvLot(gdReq);
			
			//특수강이송지시 등록
			JDTORecord jrParam = null;
			jrParam = JDTORecordFactory.getInstance().create();
			
			String SPOS_WLOC_CD = "";
			String vYD_SCH_CD = gdReq.getParam("YD_SCH_CD");
			
			switch (vYD_SCH_CD) {
				case "GHTR02":
					SPOS_WLOC_CD = "BSY04";
					break;
			}
			
			String ARR_WLOC_CD = gdReq.getParam("ARR_WLOC_CD");
			
			for(int ii = 0; ii < 100; ii++) {
				String vSSTL_NO = "";
				vSSTL_NO = commUtils.getValue(gdReq, "SSTL_NO", ii);
				
				if(!"".equals(vSSTL_NO)) {
					jrParam.setField("SSTL_NO"      , commUtils.getValue(gdReq, "SSTL_NO", ii)	);	//재료번호들
					jrParam.setField("USER_ID"      , gdReq.getParam("userid")					);	//사용자
					jrParam.setField("SPOS_WLOC_CD" , SPOS_WLOC_CD								);	//대형옥내 남4문 - (빌렛/각광) 출하상, 봉강출하상은 무조건 남4문 이송출고
					jrParam.setField("ARR_WLOC_CD"  , ARR_WLOC_CD								);  //착지 개소 코드 - TB_YS_LMILLPLNSTRLOC.YD_RCPT_PLN_STR_LOC(야드입고예정위치)
					jrParam.setField("REG_PGM"      , "YardSystem"								);	//등록프로그램
					
					EJBConnector sndConn = new EJBConnector("default", "CbtYsL2RcvSeEJB", this);
					sndConn.trx("insPbStlFrToMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				}
			}
			
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		} 
	} // end of callregCarFtmvLot
	
	/**
	 * 저장위치좌표설정 - 열정보 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updStrLocPosSetCol(GridData gdReq) throws DAOException {
		String methodNm = "저장위치좌표설정 - 열정보 변경 [YsTotYsJspSeEJB.updStrLocPosSetCol] < " + gdReq.getNavigateValue();
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
					// 열정보 수정
					jrParam.setField("YD_GP", commUtils.getValue(gdReq, "YD_GP", ii));
					jrParam.setField("YD_BAY_GP", commUtils.getValue(gdReq, "YD_BAY_GP", ii));
					jrParam.setField("YD_EQP_GP", commUtils.getValue(gdReq, "YD_EQP_GP", ii));
					jrParam.setField("YD_STK_COL_NO", commUtils.getValue(gdReq, "YD_STK_COL_NO", ii));
					jrParam.setField("YD_STK_COL_ACT_STAT", commUtils.getValue(gdReq, "YD_STK_COL_ACT_STAT", ii));
					jrParam.setField("YD_STK_COL_RULE_XAXIS", commUtils.getValue(gdReq, "YD_STK_COL_RULE_XAXIS", ii));
					jrParam.setField("YD_STK_COL_RULE_YAXIS", commUtils.getValue(gdReq, "YD_STK_COL_RULE_YAXIS", ii));
					jrParam.setField("YD_STK_COL_W", commUtils.getValue(gdReq, "YD_STK_COL_W", ii));
					jrParam.setField("YD_STK_COL_L", commUtils.getValue(gdReq, "YD_STK_COL_L", ii));
					jrParam.setField("YS_STK_COL_L_GP", commUtils.getValue(gdReq, "YS_STK_COL_L_GP", ii));
					jrParam.setField("YS_STK_COL_GP", commUtils.getValue(gdReq, "YS_STK_COL_GP", ii));
					jrParam.setField("YD_STKBED_USG_CD", commUtils.getValue(gdReq, "YD_STKBED_USG_CD", ii));
					jrParam.setField("YD_STK_BED_LYR_MAX", commUtils.getValue(gdReq, "YD_STK_BED_LYR_MAX", ii));
					jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
	
					commDao.update(jrParam, updYdStkcol, logId, methodNm, "열정보 수정");
					
					commDao.update(jrParam, updYdStkbedLyrMax, logId, methodNm, "BED MAX 단 일괄 수정");

					jrParam.setField("YD_INFO_SYNC_CD", "4"); // 야드정보동기화코드(Bed)
					jrParam.setField("YS_STK_COL_GP", commUtils.trim(gdReq.getHeader("YS_STK_COL_GP").getValue(ii))); // 야드적치열구분
					// jrParam.setField("YD_STK_BED_NO" , "01" ); //야드적치Bed번호
	
					// 전송Data 조회
//					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN2L001", jrParam));
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updStrLocPosSetCol
}
