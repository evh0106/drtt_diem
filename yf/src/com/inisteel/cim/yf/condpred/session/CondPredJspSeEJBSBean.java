/**
 * @(#)CondPredJspSeEJBSBean
 *
 * @version		V1.00
 * @author		현대제철
 * @date		2025/02/20
 *
 * @description	열연 결로 예측(Condensation Prediction) 시스템 Jsp Session EJB
 * 
 * -------------------------------------------------------------------------------
 * Ver.		수정일자	요청자	수정자	내용
 * =======	==========	======	======	==========================================
 * V1.00	2025/02/20	정종균	양태호	최초 등록
 *
 */
package com.inisteel.cim.yf.condpred.session;

import java.sql.Types;
import java.util.List;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yf.common.YFUserException;
import com.inisteel.cim.yf.common.YfCommUtils;
import com.inisteel.cim.yf.common.YfQueryIF;
import com.inisteel.cim.yf.common.YfQueryIF2;
import com.inisteel.cim.yf.common.dao.YfCommDAO;
import com.inisteel.cim.yf.common.session.YfComm;
import com.inisteel.cim.yf.condpred.CondPredUtil;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;
import xlib.cmc.GridHeader;
import xlib.cmc.OperateGridData;

/**
 * [A] 클래스명 : 열연 결로 예측(Condensation Prediction) 시스템 Jsp Session EJB
 *
 * @ejb.bean name="CondPredJspSeEJB" jndi-name="CondPredJspSeEJB" type="Stateless" view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
 */
public class CondPredJspSeEJBSBean extends BaseSessionBean implements YfQueryIF, YfQueryIF2 {
	private YfCommUtils commUtils = new YfCommUtils();
	private YfCommDAO commDao = new YfCommDAO();
	private YfComm comm = new YfComm();
	private String szSessionName = getClass().getName();
	private CondPredUtil condpredUtil = new CondPredUtil();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {

	}

	/**
	 * GridData - 단순 조회 : 빌드6
	 * 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData getSelectData(GridData gdReq) throws DAOException {
		String methodNm = "조회[CondPredJspSeEJB.getSelectData(GridData)] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq); // Grid date 를 JDTORecord data 로 변환
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			commDao.jspSelect(inRecord, outRecSet, inRecord.getFieldString("query_id"), logId, methodNm);

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
	}

	/**
	 * 단순 조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSelectData(JDTORecord recPara) throws DAOException {
		String methodNm = "조회[CondPredJspSeEJB.getSelectData(JDTORecord)] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+", recPara);

			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			commDao.jspSelect(recPara, outRecSet, recPara.getFieldString("query_id"), logId, methodNm);

			commUtils.printLog(logId, methodNm, "S-", recPara);

			return outRecSet;
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
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecord updateGridData(GridData gdReq) throws DAOException {
		String methodNm = "업데이트[CondPredJspSeEJB.updateGridData(GridData)] < " + gdReq.getNavigateValue();

		String logId = gdReq.getIPAddress();
		JDTORecord jrRtn = null;

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sUsrId = commUtils.trim(gdReq.getParam("userid"));
			String sQueryId = commUtils.trim(gdReq.getParam("query_id"));
			String sFuncNm = commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			JDTORecord jrParam = null;

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			if (rowCnt < 0) {
				throw new YFUserException("Grid 데이터가 없습니다.");
			}

			if ("".equals(sQueryId)) {
				throw new YFUserException("지정된 쿼리아이디가 없어 저장 할 수 없습니다.");
			}

			if ("".equals(sFuncNm)) {
				throw new YFUserException("저장을 수행할 Method가 없어 저장 할 수 없습니다.");
			}

			GridHeader[] header = gdReq.getHeaders();// 해더의 요소들
			GridHeader hd = null;

			for (int i = 0; i < rowCnt; i++) { // Row수
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", i))) {
					jrParam = commUtils.getParam(logId, methodNm, sUsrId);
					jrParam.addRecord(inRecord);
					for (int j = 0; j < header.length; j++) {// 컬럼해더 수
						hd = header[j];
						jrParam.setField(hd.getID(), commUtils.getValue(gdReq, hd.getID(), i));
					}
					commDao.update(jrParam, sQueryId, logId, methodNm, sFuncNm);
				}
			}
			commUtils.printLog(logId, methodNm, "S-", gdReq);
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (YFUserException e) {
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * [A] 오퍼레이션명 : YF-RULE 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 *            gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord modiYfRule(GridData gdReq) throws DAOException {
		String methodNm = "YF-RULE 수정[CondPredJspSeEJB.modiYfRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("DTL_ITEM1", commUtils.trim(gdReq.getParam("DTL_ITEM1")));
			jrParam.setField("DTL_ITEM2", commUtils.trim(gdReq.getParam("DTL_ITEM2")));
			jrParam.setField("DTL_ITEM3", commUtils.trim(gdReq.getParam("DTL_ITEM3")));
			jrParam.setField("DTL_ITEM4", commUtils.trim(gdReq.getParam("DTL_ITEM4")));
			jrParam.setField("DTL_ITEM5", commUtils.trim(gdReq.getParam("DTL_ITEM5")));
			jrParam.setField("DTL_ITEM6", commUtils.trim(gdReq.getParam("DTL_ITEM6")));
			jrParam.setField("DTL_ITEM7", commUtils.trim(gdReq.getParam("DTL_ITEM7")));
			jrParam.setField("DTL_ITEM8", commUtils.trim(gdReq.getParam("DTL_ITEM8")));
			jrParam.setField("DTL_ITEM9", commUtils.trim(gdReq.getParam("DTL_ITEM9")));
			jrParam.setField("DTL_ITEM10", commUtils.trim(gdReq.getParam("DTL_ITEM10")));

			jrParam.setField("REPR_CD_GP", commUtils.trim(gdReq.getParam("REPR_CD_GP")));
			jrParam.setField("CD_GP", commUtils.trim(gdReq.getParam("CD_GP")));
			jrParam.setField("ITEM", commUtils.trim(gdReq.getParam("ITEM")));

			commDao.update(jrParam, updYfRule, logId, methodNm, "YF_RULE수정");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * GridData - 테이블Data조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData getTableData(GridData gdReq) throws DAOException {
		String methodNm = "테이블Data조회[CondPredJspSeEJB.getTableData(GridData)] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		List listTableData = null;
		JDTORecord jrParam = null;

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sTableName = commUtils.nvl(gdReq.getParam("TABLE_NAME"), "").trim();
			String sWhere = commUtils.nvl(gdReq.getParam("SQL_WHERE"), "").trim();
			String sOrderBy = gdReq.getParam("SQL_ORDER_BY");
			String sQuery = "";
			String PAGE_NO = commUtils.nvl(gdReq.getParam("page_no"), "1").trim();
			String PAGE_SIZE = commUtils.nvl(gdReq.getParam("page_size"), "100").trim();
			String sUserId = commUtils.trim(gdReq.getParam("userid"));

			jrParam = commUtils.getParam(logId, methodNm, sUserId);
			jrParam.setField("TABLE_NAME", sTableName);
			JDTORecordSet jtR = commDao.select(jrParam, getYdTableColumnInfo, logId, methodNm, "야드기준 조회");

			String _SELECT_COLUMNS_ = "";

			if (jtR.size() > 0) {

				JDTORecord rd = null;
				String sTmpColumnName = "";
				String sTmpDataType = "";

				for (int i = 0; i < jtR.size(); ++i) {
					rd = jtR.getRecord(i);
					sTmpColumnName = rd.getFieldString("COLUMN_NAME");
					sTmpDataType = rd.getFieldString("DATA_TYPE");
					if ("DATE".equals(sTmpDataType)) {
						_SELECT_COLUMNS_ += "\n, TO_CHAR( T." + sTmpColumnName + ", 'YYYY-MM-DD HH24:MI:SS') AS " + sTmpColumnName;
					} else {
						_SELECT_COLUMNS_ += "\n, T." + sTmpColumnName;
					}
				}
			}

			if ("".equals(_SELECT_COLUMNS_)) {
				_SELECT_COLUMNS_ = "\n , T.*";
			}

			sQuery += " \n /* com.inisteel.cim.yf.common.dao.YfCommDAO.getYdTableData */";
			sQuery += " \n SELECT X.*";
			sQuery += " \n   FROM(SELECT ROWNUM AS RNUM";
			sQuery += " \n             , A.*";
			sQuery += " \n          FROM(SELECT COUNT(*) OVER() AS TOTALCOUNT";
			sQuery += " \n                    " + _SELECT_COLUMNS_;
			sQuery += " \n                 FROM " + sTableName + " T";
			if (!"".equals(sWhere)) {
				sQuery += "\n\n WHERE ";
				sQuery += sWhere.replaceAll("`", "'");
			}
			if ("".equals(sOrderBy)) {
				sQuery += " \n                ORDER BY 1, 2, 3";
			} else {
				sQuery += "\n\n ORDER BY ";
				sQuery += sOrderBy;
			}
			sQuery += " \n              ) A";
			sQuery += " \n         WHERE ROWNUM <= " + PAGE_NO + " * " + PAGE_SIZE + ") X";
			sQuery += " \n  WHERE RNUM >= ((" + PAGE_NO + "-1)* " + PAGE_SIZE + ")+1";

			listTableData = commDao.getCommonList(getDummySql, sQuery, new Object[] {});

			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			GridData gdRet = commUtils.jdtoRecordToGridData(gdRtn, listTableData, gdReq);

			commUtils.printLog(logId, methodNm, "S-", gdReq);

			return gdRet;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * GridData - 테이블Data조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData updTableData(GridData gdReq) throws DAOException {
		String methodNm = "야드기준 [CondPredJspSeEJB.updTableData(GridData)] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		GridData gdRet = null, gdRtn = null;
		JDTORecord jrParam = null;
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			String sUserId = commUtils.trim(gdReq.getParam("userid"));
			jrParam = commUtils.getParam(logId, methodNm, sUserId);

			gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			gdRet = commUtils.jdtoRecordToGridData(gdRtn, new java.util.ArrayList(), gdReq);

			String sTableName = commUtils.nvl(gdReq.getParam("TABLE_NAME"), "").trim();
			jrParam.setField("TABLE_NAME", sTableName);

			JDTORecordSet jtR = commDao.select(jrParam, getYdTableColumnInfo, logId, methodNm, "야드기준정보 조회");
			String sKey = "/* " + updYd + commUtils.getCamelCase(sTableName) + " */";
			String sMergeQuery = sKey;

			sMergeQuery += "\n MERGE INTO " + sTableName + " A";
			sMergeQuery += "\n USING( SELECT ";
			sMergeQuery += "\n $_SELECT_DUAL_$";
			sMergeQuery += "\n FROM DUAL ) B ON (";
			sMergeQuery += "\n $_ON_$";
			sMergeQuery += "\n )";
			sMergeQuery += "\n WHEN MATCHED THEN UPDATE SET";
			sMergeQuery += "\n $_UPDATE_SQL_$";
			sMergeQuery += "\n WHEN NOT MATCHED THEN INSERT(";
			sMergeQuery += "\n $_INS_SQL1_$";
			sMergeQuery += "\n )VALUES(";
			sMergeQuery += "\n $_INS_SQL2_$";
			sMergeQuery += "\n )";

			JDTORecord rd = null;
			String _SELECT_DUAL_ = "";
			String _ON_ = "";
			String _UPDATE_SQL_ = "";
			String _INS_SQL1_ = "";
			String _INS_SQL2_ = "";
			String sTmpColumnName = "";
			String sTmpDataType = "";
			String sTmpParam = "";
			java.util.List aColumns = new java.util.ArrayList();

			for (int i = 0; i < jtR.size(); ++i) {
				rd = jtR.getRecord(i);

				sTmpColumnName = rd.getFieldString("COLUMN_NAME");
				sTmpDataType = rd.getFieldString("DATA_TYPE");

				if (!"MOD_DDTT".equals(sTmpColumnName)) {
					aColumns.add(sTmpColumnName);
				}

				// if( !("REGISTER".equals(sTmpColumnName) || "REG_DDTT".equals(sTmpColumnName)) ){

				if ("DATE".equals(sTmpDataType)) {
					if ("MOD_DDTT".equals(sTmpColumnName)) {
						sTmpParam = "SYSDATE";
					} else {
						// sTmpParam = "TO_DATE( :V_"+sTmpColumnName+", 'YYYY-MM-DD HH24:MI:SS')";
						sTmpParam = "TO_DATE( ?, 'YYYY-MM-DD HH24:MI:SS')";
					}
				} else {
					// sTmpParam = ":V_"+sTmpColumnName;
					sTmpParam = "?";
				}

				// 변수타입에 따라서 처리합시다.
				if (_SELECT_DUAL_.length() > 0) {
					_SELECT_DUAL_ += "\n, ";
				}
				_SELECT_DUAL_ += sTmpParam + " AS " + sTmpColumnName;

				if ("P".equals(rd.getFieldString("CONSTRAINT_TYPE"))) {
					if (_ON_.length() > 0) {
						_ON_ += "\n AND";
					}
					_ON_ += " A." + sTmpColumnName + " = B." + sTmpColumnName;
				} else {
					if (!("REGISTER".equals(sTmpColumnName) || "REG_DDTT".equals(sTmpColumnName))) {
						if (_UPDATE_SQL_.length() > 0) {
							_UPDATE_SQL_ += ",";
						}
						_UPDATE_SQL_ += "\n  A." + sTmpColumnName + " = B." + sTmpColumnName;
					}
				}

				if (_INS_SQL1_.length() > 0) {
					_INS_SQL1_ += "\n ,";
				}
				_INS_SQL1_ += " A." + sTmpColumnName;

				if (_INS_SQL2_.length() > 0) {
					_INS_SQL2_ += "\n ,";
				}

				if ("REGISTER".equals(sTmpColumnName)) {
					_INS_SQL2_ += "'" + sUserId + "'";
				} else if ("REG_DDTT".equals(sTmpColumnName)) {
					_INS_SQL2_ += " SYSDATE";
				} else {
					_INS_SQL2_ += " B." + sTmpColumnName;
				}
			}

			sMergeQuery = StringHelper.replaceStr(sMergeQuery, "$_SELECT_DUAL_$", _SELECT_DUAL_);
			sMergeQuery = StringHelper.replaceStr(sMergeQuery, "$_ON_$", _ON_);
			sMergeQuery = StringHelper.replaceStr(sMergeQuery, "$_UPDATE_SQL_$", _UPDATE_SQL_);
			sMergeQuery = StringHelper.replaceStr(sMergeQuery, "$_INS_SQL1_$", _INS_SQL1_);
			sMergeQuery = StringHelper.replaceStr(sMergeQuery, "$_INS_SQL2_$", _INS_SQL2_);

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			Object[] objParam = null;
			int nParamLenth = aColumns.size();
			for (int i = 0; i < rowCnt; i++) {
				objParam = new Object[nParamLenth];
				for (int j = 0; j < nParamLenth; j++) {

					objParam[j] = gdReq.getHeader(String.valueOf(aColumns.get(j))).getValue(i);
				}
				commDao.updateData(updYd, sMergeQuery, objParam);
			}
			commUtils.printLog(logId, methodNm, "S-", gdReq);
			return gdRet;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * where 조건 이후 '' 된 문장들을 전부 파라메터로 정의한다.
	 * 
	 * @param sQuery
	 * @return
	 * @throws Exception
	 */
	private JDTORecord getQueryParam(String sQuery) throws DAOException {

		try {
			String tmpQuery = sQuery.toLowerCase();
			String sScript = "";

			int cutLen = tmpQuery.indexOf("where");

			if (cutLen == -1) {
				cutLen = tmpQuery.indexOf("values");
			}
			JDTORecord jrParam = commUtils.getParam("", "", "");
			Object[] oP = null;
			if (cutLen > -1) {
				sScript = sQuery.substring(cutLen).trim();

				int len = sScript.length();
				int ns = -1;
				int nl;
				boolean startPoint = false;
				java.util.List p = new java.util.ArrayList();
				for (int i = 0; i < len; i++) {
					startPoint = false;
					for (int j = i + 1; j < len; j++) {
						char currentChar2 = sScript.charAt(j);
						if (currentChar2 == '\'') {
							if (startPoint == false) {
								startPoint = true;
								ns = j;
							} else {
								nl = j;
								startPoint = false;
								p.add(sScript.substring(ns, nl + 1));
								ns = -1;
							}
						}
					}

					// 끝내자
					i = len;
					// }
				}

				if (!p.isEmpty()) {
					int pSize = p.size();
					oP = new Object[pSize];
					String sKeyVal = "";
					for (int i = 0; i < pSize; i++) {
						sKeyVal = (String) p.get(i);

						sScript = commUtils.replace(sScript, sKeyVal, "?");
						oP[i] = commUtils.replace(sKeyVal, "\'", "");
						// 특수문자 인식때문에.. 변경처리 2021.1.15
						// sScript = sScript.replaceAll(sKeyVal, "?");
						// oP[i] = sKeyVal.replaceAll("\'","");
					}

					sQuery = sQuery.substring(0, cutLen) + "\n" + sScript;
				}
			}
			jrParam.setField("QUERY", sQuery);
			jrParam.setField("QUERY_PARAM", oP);

			return jrParam;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog("", "getQueryParam", e));
		}
	}

	/**
	 * GridData - 쿼리스크립트 실행
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData executeQuery(GridData gdReq) throws DAOException {
		String methodNm = "야드기준 실행[CondPredJspSeEJB.executeQuery(GridData)] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		GridData gdRet = null;
		GridData gdRtn = null;

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			gdRet = commUtils.jdtoRecordToGridData(gdRtn, new java.util.ArrayList(), gdReq);

			String sQueryType = commUtils.nvl(gdReq.getParam("QUERY_TYPE"), "");
			if ("DML".equals(sQueryType)) {

				String sKey = "";
				String sQuery = commUtils.nvl(gdReq.getParam("QUERY_SCRIPT"), "");

				if (sQuery.indexOf("/*") == -1) {
					sKey = "/* com.inisteel.cim.yf.common.dao.YfCommDAO.updYdCrnSchCom */";
				}

				sQuery = sKey + "\n" + commUtils.nvl(gdReq.getParam("QUERY_SCRIPT"), "");

				JDTORecord p = this.getQueryParam(sQuery);

				Object[] objParam = null;
				if (p != null) {
					sQuery = (String) p.getField("QUERY");
					objParam = (Object[]) p.getField("QUERY_PARAM");
				}

				commDao.updateData(updYd, sQuery, objParam);
			} else {
				String sOwner = commUtils.nvl(gdReq.getParam("OWNER2"), "");
				String sObjectName = commUtils.nvl(gdReq.getParam("OBJECT_NAME"), "");

				String sKey = "/* " + updYd + commUtils.getCamelCase(sObjectName) + " */ ";
				String callScript = sKey + "\n CALL " + sOwner + "." + sObjectName + "(";
				int rowCnt = gdReq.getHeader("OBJECT_NAME").getRowCount();
				Object[][] param = new Object[rowCnt][2];
				for (int i = 0; i < rowCnt; i++) {
					if (i > 0) {
						callScript += ", ";
					}
					callScript += " ?";
					if ("IN".equals(gdReq.getHeader("IN_OUT").getValue(i))) {
						param[i][0] = "IN";
						param[i][1] = commUtils.trim(gdReq.getHeader("PARAM").getValue(i));
					} else if ("OUT".equals(gdReq.getHeader("IN_OUT").getValue(i))) {
						param[i][0] = "OUT";
						param[i][1] = new Integer(Types.VARCHAR);
					}
				}
				callScript += " )";
				callScript = StringHelper.replaceStr(callScript, updYd, updYd + commUtils.getCamelCase(sObjectName));

				JDTORecord excuteRto = commDao.execute(getDummySql, callScript, param);
				gdRet.setStatus(String.valueOf(excuteRto.getField("IS_SUCCESS")));

			}

			commUtils.printLog(logId, methodNm, "S-", gdRet);
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
	 *            gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updYfRule(GridData gdReq) throws DAOException {
		String methodNm = "기준관리 - 세부항목수정[CondPredJspSeEJB.updYfRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				// 세부항목수정
				if (!"".equals(commUtils.getValue(gdReq, "DTL_ITEM1", ii))) {
					jrParam.setField("DTL_ITEM1", commUtils.getValue(gdReq, "DTL_ITEM1", ii));
				}
				if (!"".equals(commUtils.getValue(gdReq, "DTL_ITEM2", ii))) {
					jrParam.setField("DTL_ITEM2", commUtils.getValue(gdReq, "DTL_ITEM2", ii));
				}
				if (!"".equals(commUtils.getValue(gdReq, "DTL_ITEM3", ii))) {
					jrParam.setField("DTL_ITEM3", commUtils.getValue(gdReq, "DTL_ITEM3", ii));
				}
				if (!"".equals(commUtils.getValue(gdReq, "DTL_ITEM4", ii))) {
					jrParam.setField("DTL_ITEM4", commUtils.getValue(gdReq, "DTL_ITEM4", ii));
				}
				if (!"".equals(commUtils.getValue(gdReq, "DTL_ITEM5", ii))) {
					jrParam.setField("DTL_ITEM5", commUtils.getValue(gdReq, "DTL_ITEM5", ii));
				}
				if (!"".equals(commUtils.getValue(gdReq, "DTL_ITEM6", ii))) {
					jrParam.setField("DTL_ITEM6", commUtils.getValue(gdReq, "DTL_ITEM6", ii));
				}
				if (!"".equals(commUtils.getValue(gdReq, "DTL_ITEM7", ii))) {
					jrParam.setField("DTL_ITEM7", commUtils.getValue(gdReq, "DTL_ITEM7", ii));
				}
				if (!"".equals(commUtils.getValue(gdReq, "DTL_ITEM8", ii))) {
					jrParam.setField("DTL_ITEM8", commUtils.getValue(gdReq, "DTL_ITEM8", ii));
				}
				if (!"".equals(commUtils.getValue(gdReq, "DTL_ITEM9", ii))) {
					jrParam.setField("DTL_ITEM9", commUtils.getValue(gdReq, "DTL_ITEM9", ii));
				}
				if (!"".equals(commUtils.getValue(gdReq, "DTL_ITEM10", ii))) {
					jrParam.setField("DTL_ITEM10", commUtils.getValue(gdReq, "DTL_ITEM10", ii));
				}

				jrParam.setField("REPR_CD_GP", commUtils.getValue(gdReq, "REPR_CD_GP", ii));
				jrParam.setField("CD_GP", commUtils.getValue(gdReq, "CD_GP", ii));
				jrParam.setField("ITEM", commUtils.getValue(gdReq, "ITEM", ii));
				commDao.update(jrParam, updYfRule, logId, methodNm, "기준관리 수정");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updYfRule

	/**
	 * [A] 오퍼레이션명 : 열연야드_THM_공장내외온습도정보요청(YDX1L002, YDX2L002, YDX3L002) 전문 생성
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 *            rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord getYDX1L002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "열연야드_THM_공장내외온습도정보요청 전문 생성[CondPredJspSeEJB.getYDX1L002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {

			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + " " + methodNm, rcvMsg);

			// 수신 항목 값
			String msgId = commUtils.trim(rcvMsg.getFieldString("MSG_ID"));
			String msgGp = commUtils.trim(rcvMsg.getFieldString("MSG_GP"));
			String ydGp = commUtils.trim(rcvMsg.getFieldString("YD_GP"));
			String meaDh = commUtils.trim(rcvMsg.getFieldString("MEA_DH"));

			/**********************************************************
			 * 1. 수신 항목 값 Check
			 **********************************************************/
			if ("".equals(msgId)) {
				return null;
			}
			if (meaDh.length() != 14 || !meaDh.matches("^[0-9]*$")) {
				return null;
			}

			/**********************************************************
			 * 2. 전문 생성
			 **********************************************************/
			StringBuffer sbMsg = new StringBuffer();

			sbMsg = sbMsg.append(msgId); // 전문ID
			sbMsg = sbMsg.append(commUtils.getDate10()); // 생성일자
			sbMsg = sbMsg.append(commUtils.getTime8()); // 생성시간
			sbMsg = sbMsg.append(commUtils.getRPad(msgGp, 1, " ")); // 전문구분
			sbMsg = sbMsg.append("0015"); // 전문길이
			sbMsg = sbMsg.append(commUtils.getRPad(" ", 29, " ")); // 임시
			sbMsg = sbMsg.append(commUtils.getRPad(ydGp, 1, " ")); // 야드구분
			sbMsg = sbMsg.append(commUtils.getRPad(meaDh, 14, " ")); // 측정일시

			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();

			sndL2Msg.setResultCode(logId); // Log ID
			sndL2Msg.setResultMsg(methodNm); // Log Method Name
			sndL2Msg.addField("JMS_TC_CD", msgId); // JMSTC코드
			sndL2Msg.addField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); // JMSTC생성일시(yyyyMMddHHmmss)
			sndL2Msg.addField("JMS_TC_MESSAGE", sbMsg.toString()); // JMSTCMessage

			// 전송 Data Return
			return commUtils.addSndData(sndL2Msg);
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			throw e;
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}

	/**
	 * [A] 오퍼레이션명 : 열연야드_THM_공장내외온습도정보요청(YDX1L002, YDX2L002, YDX3L002)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 *            rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord sndYDX1L002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "열연야드_THM_공장내외온습도정보요청[CondPredJspSeEJB.sndYDX1L002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); // 전문 Return
		JDTORecord jrRtnMsg = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + " 열연야드_THM_공장내외온습도정보요청 ", rcvMsg);

			// 수신 항목 값
			String sYD_GP = commUtils.trim(rcvMsg.getFieldString("YD_GP")); // 야드구분
			String sMEA_DH = commUtils.trim(rcvMsg.getFieldString("MEA_DH")); // 측정일시
			String msgId = commUtils.trim(rcvMsg.getFieldString("MSG_ID"));
			String msgGp = commUtils.trim(rcvMsg.getFieldString("MSG_GP"));

			// 수신 항목 값 확인
			if (msgId.equals("")) {
				throw new Exception("msgId 값이 정확하지 않습니다!! [" + msgId + "]");
			}
			if (msgGp.equals("")) {
				msgGp = "I";
			}
			if (sYD_GP.equals("")) {
				throw new Exception("YD_GP 값이 정확하지 않습니다!! [" + sYD_GP + "]");
			}
			if (sMEA_DH.length() != 14 || !sMEA_DH.matches("^[0-9]*$")) {
				throw new Exception("측정일시 값이 정확하지 않습니다!! [" + sMEA_DH + "]");
			}

			jrRtnMsg.setField("MSG_ID", msgId);
			jrRtnMsg.setField("MSG_GP", msgGp);
			jrRtnMsg.setField("YD_GP", sYD_GP);
			jrRtnMsg.setField("MEA_DH", sMEA_DH);

			jrRtn = commUtils.addSndData(jrRtn, getYDX1L002(jrRtnMsg));

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
}
