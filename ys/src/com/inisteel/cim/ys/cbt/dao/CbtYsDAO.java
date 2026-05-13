/**
 * @(#)CbtYsDAO
 *
 * @version          V1.00
 * @author           김현규
 * @date             2025/07/01
 *
 * @description      대형 압연 옥내 야드 화면 처리 DAO
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2025/07/01                         김현규      최초 등록
 */
package com.inisteel.cim.ys.cbt.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ct.common.util.CmnUtil;
import com.inisteel.cim.ys.common.util.YsCommUtils;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;

public class CbtYsDAO extends DBAssistantDAO {

	private YsCommUtils commUtils = new YsCommUtils();

	/**
	 * [A] 오퍼레이션명 : UPDATE 메소드
	 * 
	 * @param JDTORecord inRec parameter record String queryId QueryId String logId String mthdNm String trtNm
	 * @return int execution count
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int update(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {

		String methodNm = trtNm + "[CbtYsDAO.update] < " + mthdNm;

		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			// 필드명 변환 (필드명 -> V_필드명)
//			recPara = conversionFieldname(inRec, 0);
			// query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			// query execute
			intRtnVal = trtProcess(recPara);

			commUtils.printLog(logId, trtNm + "[CbtYsDAO.update] 결과 건수: " + intRtnVal, "DB");

		} catch (Exception e) {

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return intRtnVal;
	}
	
	/**
	 * @param java.util.HashMap
	 * @return JDTORecordSet
	 * @throws com.inisteel.cim.common.exception.DAOException
	 */
	public JDTORecordSet getListWithFlex(HashMap paramMap) throws DAOException {
		String jspeed_query_id = "";
		Object[] objs = null;
		List paramList = null;

		try {
			jspeed_query_id = "" + paramMap.get("jspeed_query_id");
			paramList = (ArrayList) paramMap.get("paramList");

			if (paramList == null || paramList.size() == 0) {
				objs = new Object[] {};
			} else {
				objs = new Object[paramList.size()];
				for (int ii = 0; ii < paramList.size(); ii++) {
					objs[ii] = "" + CmnUtil.nvl(paramList.get(ii), "");
				}
			}

			JDTORecordSet jrs = getRecordSet(jspeed_query_id, objs);
			commUtils.printLog("", "조회 결과 건수: " + jrs.toList().size(), "DB");

			return jrs;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		}
	}

	/**
	 * @param java.util.HashMap
	 * @return JDTORecordSet
	 * @throws com.inisteel.cim.common.exception.DAOException
	 */
	public List getMultiListWithFlex(HashMap paramMap) throws DAOException {
		int sql_count = 0;
		String jspeed_query_id = "";
		Object[] objs = null;
		List rtnList = new ArrayList();
		List paramList = null;

		try {
			sql_count = Integer.parseInt(CmnUtil.nvl(paramMap.get("sql_count"), "0"));

			for (int ii = 0; ii < sql_count; ii++) {
				jspeed_query_id = "" + paramMap.get("jspeed_query_id" + ii);
				paramList = (ArrayList) paramMap.get("paramList" + ii);

				if (paramList == null || paramList.size() == 0) {
					objs = new Object[] {};
				} else {
					objs = new Object[paramList.size()];
					for (int jj = 0; jj < paramList.size(); jj++) {
						objs[jj] = "" + CmnUtil.nvl(paramList.get(jj), "");
					}
				}

				List resultList = getRecordSet(jspeed_query_id, objs).toList();

				commUtils.printLog("", "조회 결과 건수: " + resultList.size(), "DB");

				rtnList.add(CmnUtil.listJdtoRecordTohashMap(resultList));
			}

			return rtnList;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		}
	}
}
