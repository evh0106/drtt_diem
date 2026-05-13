/**    
 * @(#)YsTotYsDAO
 *
 * @author : 현대제철
 * @date : 2025/07/11
 * @description : 특수강 사외 통합 야드 Jsp Facade EJB
 * @history
 *   - 2025/07/11 : 특수강 대형야드 신예화 프로젝트 초기 버전 작성 (양태호)
 */
package com.inisteel.cim.ys.ystot.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ct.common.util.CmnUtil;
import com.inisteel.cim.ys.common.util.YsCommUtils;

import jspeed.base.record.JDTORecordSet;

public class YsTotYsDAO extends DBAssistantDAO {
	
	private YsCommUtils commUtils = new YsCommUtils();

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
