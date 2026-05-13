/**    
 * @(#)EbtYsDAO
 *
 * @author : 현대제철
 * @date : 2025/07/11
 * @description : 특수강 정정 야드 Jsp Facade EJB
 * @history
 *   - 2025/07/11 : 특수강 대형야드 신예화 프로젝트 초기 버전 작성 (양태호)
 */
package com.inisteel.cim.ys.ebt.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ct.common.util.CmnUtil;
import com.inisteel.cim.ys.common.util.YsCommUtils;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

public class EbtYsDAO extends DBAssistantDAO {

	private YsCommUtils commUtils = new YsCommUtils();

	/**
	 * [A] 오퍼레이션명 : conversionFieldname
	 * 
	 * @param JDTORecord recPara // 파라미터 레코드 int intGp // 구분(0:"V_" 추가, 1:"V_" 제거
	 * @return JDTORecord // 필드명을 변환한 결과레코드
	 * @throws JDTOException
	 */
	public JDTORecord conversionFieldname(JDTORecord recPara, int intGp) throws JDTOException {
		JDTORecord recRtnVal = JDTORecordFactory.getInstance().create();
		String szFieldName = null;
		Iterator itrFieldName = null;

		// 필드명을 가져온다.
		itrFieldName = recPara.iterateName();

		// 필드명 갯수만큼 루프를 돈다.
		while (itrFieldName.hasNext()) {

			szFieldName = (String) itrFieldName.next();
			// "V_" 추가
			if (intGp == 0) {
				recRtnVal.setField("V_" + szFieldName, recPara.getField(szFieldName));
				// "V_" 제거
			} else {
				recRtnVal.setField(szFieldName.substring(2), recPara.getField(szFieldName));
			}
		}

		return recRtnVal;
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

	/**
	 * 야드 기준 항목 조회
	 * 
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public String getYsRuleItem(String logId, String mthdNms, String sReprCdGp, String sCdGp) throws DAOException {
		String mthdNm = "야드 기준 항목 조회[EbtYsDAO.getYsRuleItem] < " + mthdNms;
		String ruleItem = "";

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			// 수신 항목 값
			/**********************************************************
			 * 2. 열정보 read
			 **********************************************************/
			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("REPR_CD_GP", sReprCdGp); // 작업구분
			jrParam.setField("CD_GP", sCdGp); // 구분
			// jrParam.setField("ITEM" , sItem ); //ITEM

			// 필드명 변환 (필드명 -> V_필드명)
			jrParam = conversionFieldname(jrParam, 0);
			// query id setting
			jrParam.setField("JSPEED_QUERY_ID", "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsRuleList");
			// query execute
			JDTORecordSet jsChk = getRecordSet(jrParam);

			if (jsChk.size() > 0) {
				ruleItem = commUtils.trim(jsChk.getRecord(0).getFieldString("ITEM"));
				commUtils.printLog(logId, mthdNm, "ITEM : " + ruleItem);
			}

			commUtils.printLog(logId, mthdNm, "S-");

			return ruleItem;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return ruleItem;
		} catch (Exception e) {
			return ruleItem;
		}
	}
}
