/**
 * @(#)YmCommDAO
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 * 
 * @description      야드관리 공통 DAO
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.ym.bcommon.dao;

import java.sql.Types;
import java.util.Iterator;

import xlib.cmc.GridData;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;

/**
 * [A] 클래스명 : 야드관리 공통 DAO
 *
 */
public class YmCommDAO extends DBAssistantDAO {
	
	private YmCommUtils commUtils = new YmCommUtils();
	private DBAssistantDAO dbAssDao = new DBAssistantDAO(); 
	/**
	 *      [A] 오퍼레이션명 : 수신된 전문의 정보를 조회
	 *      
	 *      @param String msgID : 수신된 전문의 MSG_ID
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMsgInfo(String msgID) throws DAOException {
		try {
			return getRecordSet("com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getMsgInfo", new Object[] { msgID });
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : conversionFieldname 
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         int intGp             // 구분(0:"V_" 추가, 1:"V_" 제거
	 * @return JDTORecord			 // 필드명을 변환한 결과레코드
	 * @throws JDTOException 
	 */
	public JDTORecord conversionFieldname(JDTORecord recPara, int intGp) throws JDTOException {
		JDTORecord recRtnVal = JDTORecordFactory.getInstance().create();
		String szFieldName = null;
		Iterator itrFieldName = null;
		
		//필드명을 가져온다.
		itrFieldName = recPara.iterateName();
		
		//필드명 갯수만큼 루프를 돈다.
		while(itrFieldName.hasNext()) {
			
			szFieldName = (String)itrFieldName.next();
			//"V_" 추가
			if (intGp == 0) {
				recRtnVal.setField("V_" + szFieldName, recPara.getField(szFieldName));
			//"V_" 제거
			} else {
				recRtnVal.setField(szFieldName.substring(2), recPara.getField(szFieldName));
			}
		}
		
		return recRtnVal ;
	}

	/**
	 *      [A] 오퍼레이션명 : Jsp 화면용 SELECT 메소드
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         String        queryId    QueryId 
	 *         String        logId   	 
	 *         String        mthdNm   	 
	 * @return int           result size
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int jspSelect(JDTORecord inRec, JDTORecordSet outRecSet, String queryId, String logId, String mthdNm) throws DAOException, JDTOException {
		
		String methodNm = "조회[YmCommDAO.jspSelect] < " + mthdNm;
		
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
		
		try {
			
			//PIDEV_S :병행가동용:PI_YD
//			String sPI_YD     = commUtils.nvl(inRec.getFieldString("PI_YD"), "*");				
//			
//			// PIDEV
//			queryId = this.getYmRulePI("", methodNm, "YM0001", queryId, "APPPI0", sPI_YD, "*" );

			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			rsTemp = getRecordSet(recPara);
			
			commUtils.printLog(logId, "조회[YmCommDAO.jspSelect] 결과 건수: " + rsTemp.size() , "DB");
			
			if (rsTemp.size() > 0) {
				outRecSet.addAll(rsTemp);
			} else {
				return 0;
			}
			
		} catch (Exception e) {
			
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return outRecSet.size();
	}

	/**
	 *      [A] 오퍼레이션명 : SELECT 메소드
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         String        queryId    QueryId 
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public JDTORecordSet select(JDTORecord inRec, String queryId) throws DAOException, JDTOException {
		
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
//		JDTORecordSet extData =JDTORecordFactory.getInstance().createRecordSet("Temp");
		try {
			// PIDEV PI_YD
			String piYd = commUtils.nvl(commUtils.trim(inRec.getFieldString("PI_YD")), "*");
			
//			queryId = this.getYmRulePI("", "", "YM0001", queryId, "APPPI0", piYd, "*" );

			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			rsTemp = getRecordSet(recPara);
		 
		} catch (Exception e) {
			
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return rsTemp;
	}
	
	/**
	 *      [A] 오퍼레이션명 : SELECT 메소드
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         String        queryId    QueryId 
	 *         String     	 logId   	 
	 *         String     	 mthdNm   	 
	 *         String     	 trtNm   	 
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public JDTORecordSet select(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {
		
		String methodNm = trtNm + "[YmCommDAO.select] < " + mthdNm;
		
		JDTORecord recPara = null;	
		
		try {
			// PIDEV PI_YD
			String piYd = commUtils.nvl(commUtils.trim(inRec.getFieldString("PI_YD")), "*");
			
//			queryId = this.getYmRulePI("", methodNm, "YM0001", queryId, "APPPI0", piYd, "*" );
	
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			JDTORecordSet rsTemp = getRecordSet(recPara);
			
			commUtils.printLog(logId, trtNm + "[YmCommDAO.select] 결과 건수: " + rsTemp.size() , "DB");
			
			return rsTemp;
			
		} catch (Exception e) {
			
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	// PIDEV PI_YD
	public JDTORecordSet select3(JDTORecord inRec, String queryId) throws DAOException, JDTOException {
		
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
//		JDTORecordSet extData =JDTORecordFactory.getInstance().createRecordSet("Temp");
		try {
			// PIDEV PI_YD
			String piYd = commUtils.nvl(commUtils.trim(inRec.getFieldString("PI_YD")), "3");
			
//			queryId = this.getYmRulePI("", "", "YM0001", queryId, "APPPI0", piYd, "*" );

			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			rsTemp = getRecordSet(recPara);
		 
		} catch (Exception e) {
			
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return rsTemp;
	}
	
	public JDTORecordSet select3(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {
		
		String methodNm = trtNm + "[YmCommDAO.select3] < " + mthdNm;
		
		JDTORecord recPara = null;	
		
		try {
			// PIDEV PI_YD
			String piYd = commUtils.nvl(commUtils.trim(inRec.getFieldString("PI_YD")), "3");
			
//			queryId = this.getYmRulePI("", methodNm, "YM0001", queryId, "APPPI0", piYd, "*" );

			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			JDTORecordSet rsTemp = getRecordSet(recPara);
			
			commUtils.printLog(logId, trtNm + "[YmCommDAO.select3] 결과 건수: " + rsTemp.size() , "DB");
			
			return rsTemp;
			
		} catch (Exception e) {
			
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SELECT 메소드
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         String        queryId    QueryId 
	 *         String     	 logId   	 
	 *         String     	 mthdNm   	 
	 *         String     	 trtNm   	 
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public JDTORecordSet selectL2(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {
		
		String methodNm = trtNm + "[YmCommDAO.select] < " + mthdNm;
		
		JDTORecord recPara = null;
		JDTORecordSet rsTemp =  null;
		
		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			rsTemp = getRecordSet(recPara);
			
			commUtils.printLog(logId, trtNm + "[YmCommDAO.select] 결과 건수: " + rsTemp.size() , "DB");
			
			return rsTemp;
			
		} catch (Exception e) {
			return rsTemp;
		}
		
	}	
	/**
	 *      [A] 오퍼레이션명 : UPDATE 메소드
	 * 
	 * @param  JDTORecord inRec 		parameter record
	 *         String     queryId   	QueryId 
	 * @return int        execution count
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int update(JDTORecord inRec, String queryId) throws DAOException, JDTOException {
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {		
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
		} catch (Exception e) {

			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return intRtnVal;
	} 

	/**
	 *      [A] 오퍼레이션명 : UPDATE 메소드
	 * 
	 * @param  JDTORecord inRec 		parameter record
	 *         String     queryId   	QueryId 
	 *         String     logId   	 
	 *         String     mthdNm   	 
	 *         String     trtNm   	 
	 * @return int        execution count
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int update(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {
		
		String methodNm = trtNm + "[YmCommDAO.update] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			commUtils.printLog(logId, trtNm + "[YmCommDAO.update] 결과 건수: " + intRtnVal , "DB");
			
		} catch (Exception e) {

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return intRtnVal;
	} 
	
	/**
	 *      [A] 오퍼레이션명 : INSERT 메소드
	 * 
	 * @param  JDTORecord inRec 		parameter record
	 *         String     queryId   	QueryId 
	 * @return int        execution count
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insert(JDTORecord inRec, String queryId) throws DAOException, JDTOException {
		
		return this.update(inRec, queryId);
	} 	

	/**
	 *      [A] 오퍼레이션명 : INSERT 메소드
	 * 
	 * @param  JDTORecord inRec 		parameter record
	 *         String     queryId   	QueryId 
	 *         String     logId   	 
	 *         String     mthdNm   	 
	 *         String     trtNm   	 
	 * @return int        execution count
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insert(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {
		
		String methodNm = trtNm + "[YmCommDAO.insert] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			commUtils.printLog(logId, trtNm + "[YmCommDAO.insert] 결과 건수: " + intRtnVal , "DB");
			
		} catch (Exception e) {

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return intRtnVal;	
	}
	
	/**
	 *      [A] 오퍼레이션명 : DELETE 메소드
	 * 
	 * @param  JDTORecord inRec 		parameter record
	 *         String     queryId   	QueryId 
	 * @return int        execution count
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int delete(JDTORecord inRec, String queryId) throws DAOException, JDTOException {
		
		return this.update(inRec, queryId);
	} 	
	
	/**
	 *      [A] 오퍼레이션명 : DELETE 메소드
	 * 
	 * @param  JDTORecord inRec 		parameter record
	 *         String     queryId   	QueryId 
	 *         String     logId   	 
	 *         String     mthdNm   	 
	 *         String     trtNm   	 
	 * @return int        execution count
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int delete(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {
		
		String methodNm = trtNm + "[YmCommDAO.delete] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			commUtils.printLog(logId, trtNm + "[YmCommDAO.delete] 결과 건수: " + intRtnVal , "DB");
			
		} catch (Exception e) {

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return intRtnVal;	
	} 	
	
	/**
	 *      [A] 오퍼레이션명 : Procesure 호출 메소드
	 * 
	 * @param  Object[] 		inParam 		procedure input parameter array
	 *         int[]   	 		inParamIndex   	procedure input parameter seq array 
	 *         String    		queryId   		QueryId 
	 * @return JDTORecord		procedure Result
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public JDTORecord callProcedure( Object[] inParam, int[] inParamIndex , String queryId) throws DAOException, JDTOException {
		try {
			
			String add_query = "";

	 		String[] outParamKey = {"OUT_RTN_CODE"};
	 		int[] outParamType = {Types.VARCHAR}; // Types.INTEGER, Types.NUMERIC, Types.DECIMAL
	 		int[] outParamIndex = {inParamIndex.length+1};
	 		
			return trtProcedure(queryId, add_query, inParam, inParamIndex, outParamKey, outParamType, outParamIndex);

		} catch (Exception e) {

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 	
	}		
	
	/***************************************************************************
	 * Code조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 공통야드 코드 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getYmCode(GridData gdReq) throws DAOException {
		String methodNm = "코드조회[YmCommDAO.getYmCode] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;
			
			String itmGp = commUtils.trim(gdReq.getParam("V_ITM_GP")); //코드항목구분

			commUtils.printLog(logId, "조회[YmCommDAO.jspSelect] 결과 건수: " + itmGp , "DB");
			
			if ("YD_BAY_GP".equals(itmGp)) {
				trtNm = "동구분";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCodeYdBayGp";  
				param = new Object[] {
						  commUtils.trim(gdReq.getParam("V_YD_GP"))  //야드구분
						, commUtils.trim(gdReq.getParam("V_LOC_GP")) //작업장구분
								};

			} else if ("YD_EQP_GP".equals(itmGp)) { //00~99
				trtNm = "설비구분";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCodeYdEqpGp";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_STACK_COL_GP")) //야드구분 + 동구분
					};
			} else if ("YD_LOC_GP".equals(itmGp)) { //00~99, 설비
				trtNm = "위치구분";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCodeYdLocGp";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_STACK_COL_GP")) //야드구분 + 동구분
					};				
			} else if ("YD_STK_COL_NO".equals(itmGp)) {
				trtNm = "적치열번호";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCodeYdStkColNo";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_STACK_COL_GP")) //야드구분 + 동구분 + Span구분
					};
			} else if ("YD_STK_BED_NO".equals(itmGp)) {
				trtNm = "적치Bed번호";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCodeYdStkBedNo";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_STACK_COL_GP")) //야드적치열구분
					};
			} else if ("STACK_LAYER_GP".equals(itmGp)) {
				trtNm = "적치단번호";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCodeYdStkLyrNo";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_STACK_COL_GP")) //야드적치열구분
					   ,commUtils.trim(gdReq.getParam("V_STACK_BED_GP")) //BED구분
					};
			} else if ("YD_EQP_ID_CR".equals(itmGp)) {
				trtNm = "크레인설비ID";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCodeYdEqp";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_YD_GP"    )) //야드구분
					   ,commUtils.trim(gdReq.getParam("V_YD_BAY_GP")) //동구분
				};
			} else if ("YD_EQP_ID_TC".equals(itmGp)) {
				trtNm = "대차설비ID";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCodeYdEqpTc";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_YD_GP")) //야드구분
				};
			} else if ("YD_EQP_ID_TC_BY_BAY".equals(itmGp)) {
				trtNm = "대차설비ID";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCodeYdEqpTcByBay";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_YD_GP")) //야드구분
					   ,commUtils.trim(gdReq.getParam("V_YD_BAY_GP")) //동구분
					   ,commUtils.trim(gdReq.getParam("V_TO_BAY_GP")) //동구분
				};
			} else if ("YD_SCH_CD".equals(itmGp)) {
				trtNm = "스케줄코드";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCodeYdSchCd";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_YD_GP"    )) //야드구분
					   ,commUtils.trim(gdReq.getParam("V_YD_BAY_GP")) //동구분
					};
			} else if ("YD_SCH_CD_OPRN".equals(itmGp)) {
				trtNm = "스케줄코드";
				jspeed_query_id = "com.inisteel.cim.ym.common.dao.YmCommDAO.getCodeYdSchCdOprn";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_YD_GP"    )) //야드구분
					   ,commUtils.trim(gdReq.getParam("V_YD_BAY_GP")) //동구분
					};		
			} else if ("YD_STK_ABLE_SPAN".equals(itmGp)) { 
				trtNm = "적치가능Span";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getAbleYdLocGp";
				param = new Object[] {
					   commUtils.trim(gdReq.getParam("V_STACK_COL_GP")) //야드적치열구분
					};				
			} else if ("YD_STK_ABLE_COL".equals(itmGp)) { 
				trtNm = "적치가능col";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getAbleStkColNo";
				param = new Object[] {
					   commUtils.trim(gdReq.getParam("V_STACK_COL_GP")) //야드적치열구분
					};				
			} else if ("YD_STK_ABLE_BED".equals(itmGp)) { 
				trtNm = "적치가능Bed";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getUsableBedList";
				param = new Object[] {
					   commUtils.trim(gdReq.getParam("V_STACK_COL_GP")) //야드적치열구분
					};
			} else if ("YD_STK_ABLE_LYR".equals(itmGp)) {
				trtNm = "적치가능Lyr";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getUsableLyrList";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_STACK_COL_GP")) //야드적치열구분
					   ,commUtils.trim(gdReq.getParam("V_STACK_BED_GP")) //야드적치열구분
					};
			} else if ("YD_RT".equals(itmGp)) {
				trtNm = "야드행선";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdRt";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_YD_GP")) //야드구분
					  , commUtils.trim(gdReq.getParam("V_YD_SCH_CD")) //스케줄코드
					};
			} else if ("YD_RT_SLAB".equals(itmGp)) {
				trtNm = "야드행선";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdRtSlab";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_YD_GP")) //야드구분
					  , commUtils.trim(gdReq.getParam("V_YD_SCH_CD")) //스케줄코드
					};
			} else { //공통코드조회
				trtNm = "[" + itmGp + "]코드";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCodeCmCodes";
				param = new Object[] {
						itmGp //코드영문ID
					   ,commUtils.trim(gdReq.getParam("V_CD_CAT_ID")) //코드카테고리ID
					};
			}
			
			trtNm += " : ";
			commUtils.printLog(logId, "조회[YmCommDAO.jspSelect] 결과 건수11: " + itmGp , "DB");

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	/***************************************************************************
	 * L2 송신 전문 조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : L2전문조회
	 *      
	 *      @param String msgId
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMsgL2(String msgId, JDTORecord jrParam) throws DAOException {
		String methodNm = "L2전문생성[YmCommDAO.getMsgL2] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";

			/* B열연 COIL야드 L2 송신 *************************************************************************************/			    	
			if("YMA7L001".equals(msgId)) {
			
				trtNm = "B열연 COIL 저장위치 제원";
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L001_PIDEV 

				SELECT JMS_TC_CD                                  --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				      ,JMS_TC_CD                                  --전문ID
				     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')    --생성일시
				     ||'I'                                        --전문구분
				     ||'0090'                                     --전문길이
				     ||RPAD(' ',29,' ')                           --임시
				     ||RPAD(NVL(YD_INFO_SYNC_CD     ,' '), 1,' ') --야드정보동기화코드
				     ||RPAD(NVL(YD_GP               ,' '), 1,' ') --야드구분
				     ||RPAD(NVL(BAY_GP              ,' '), 1,' ') --야드동구분
				     ||RPAD(NVL(SECT_GP             ,' '), 2,' ') --야드설비구분
				     ||RPAD(NVL(COL_GP              ,' '), 2,' ') --야드적치열번호
				     ||RPAD(NVL(STACK_BED_GP        ,' '), 2,' ') --야드적치Bed번호
				     ||RPAD(NVL(STACK_LAYER_GP      ,' '), 2,' ') --야드적치단번호
				     ||RPAD(NVL(YS_STK_BED_L_GP     ,' '), 1,' ') --야드적치Bed길이구분
				     ||RPAD(NVL(YS_STK_BED_W_GP     ,' '), 1,' ') --야드적치Bed폭구분
				     ||RPAD(NVL(YD_STK_BED_DIR_GP   ,' '), 1,' ') --야드적치Bed방향구분
				     ||RPAD(NVL(YD_STK_BED_ACT_STAT ,' '), 1,' ') --야드적치Bed활성상태
				     ||RPAD(NVL(YD_STK_BED_WHIO_STAT,' '), 1,' ') --야드적치Bed입출고상태
				     ||RPAD(NVL(YD_STK_BED_XAXIS    ,' '), 7,' ') --야드적치BedX축(주행)
				     ||RPAD(NVL(YD_STK_BED_YAXIS    ,' '), 5,' ') --야드적치BedY축(주행)
				     ||RPAD(NVL(YD_STK_BED_ZAXIS_SYM,' '), 1,' ') --야드적치BedZ축부호
				     ||RPAD(NVL(YD_STK_BED_ZAXIS    ,' '), 5,' ') --야드적치BedZ축(주행)
				     ||RPAD(NVL(YD_STK_BED_LYR_MAX  ,' '), 3,' ') --야드적치Bed단Max
				     ||RPAD(NVL(YD_STK_BED_WT_MAX   ,' '), 7,' ') --야드적치Bed중량Max
				     ||RPAD(NVL(YD_STK_BED_H_MAX    ,' '), 5,' ') --야드적치Bed높이Max
				     ||RPAD(NVL(YD_STK_BED_L_MAX    ,' '), 5,' ') --야드적치Bed길이Max
				     ||RPAD(NVL(YD_STK_BED_W_MAX    ,' '), 5,' ') --야드적치Bed폭Max
				     ||RPAD(NVL(YD_CAR_ARRSTRT_STAT ,' '), 1,' ') --야드차량착발상태
				     ||RPAD(NVL(YD_CAR_USE_GP       ,' '), 1,' ') --야드차량사용구분
				     ||RPAD(NVL(YD_EQP_WRK_STAT     ,' '), 1,' ') --야드설비작업상태
				     ||RPAD(NVL(CAR_NO              ,' '),15,' ') --차량번호
				     ||RPAD(NVL(TRN_EQP_CD          ,' '), 8,' ') --운송장비코드
				     ||RPAD(NVL(CARD_NO             ,' '), 4,' ') --카드번호
				     ||RPAD(NVL(YD_CAR_AIM_YD_GP    ,' '), 1,' ') --야드차량목표야드구분
				       AS JMS_TC_MESSAGE --JMSTCMessage
				  FROM (
				  
				        SELECT 'YMA7L001'                                        AS JMS_TC_CD
				              ,:V_YD_INFO_SYNC_CD                                AS YD_INFO_SYNC_CD
				              ,SC.YD_GP                                          AS YD_GP
				              ,SC.BAY_GP                                         AS BAY_GP
				              ,SC.SECT_GP                                        AS SECT_GP
				              ,SC.COL_GP                                         AS COL_GP 
				              ,SC.STACK_COL_GP                                   AS STACK_COL_GP
				              ,SB.STACK_BED_GP                                   AS STACK_BED_GP
				              ,SL.STACK_LAYER_GP                                 AS STACK_LAYER_GP
				              ,''                                                AS YS_STK_BED_L_GP
				              ,''                                                AS YS_STK_BED_W_GP
				              ,''                                                AS YD_STK_BED_DIR_GP
				              ,SB.STACK_BED_ACTIVE_STAT                          AS YD_STK_BED_ACT_STAT
				              ,''                                                AS YD_STK_BED_WHIO_STAT
				              ,TO_CHAR(NVL(SB.STACK_BED_X_AXIS,0)  ,'FM0000000') AS YD_STK_BED_XAXIS
				              ,TO_CHAR(NVL(SB.STACK_BED_Y_AXIS,0)  ,'FM00000'  ) AS YD_STK_BED_YAXIS
				              ,CASE WHEN SB.STACK_BED_Z_AXIS >= 0  THEN '+'
				                    ELSE '-' END                                 AS YD_STK_BED_ZAXIS_SYM
				              ,TO_CHAR(NVL(SB.STACK_BED_Z_AXIS,0)  ,'FM00000'  ) AS YD_STK_BED_ZAXIS
				              ,TO_CHAR(SB.STACK_BED_ABLE_QNTY      ,'FM000'    ) AS YD_STK_BED_LYR_MAX
				              ,TO_CHAR(SB.STACK_BED_WT_MAX         ,'FM0000000') AS YD_STK_BED_WT_MAX
				              ,TO_CHAR(SB.STACK_BED_HIGH_MAX       ,'FM00000'  ) AS YD_STK_BED_H_MAX
				              ,TO_CHAR(SB.STACK_BED_LEN_MAX        ,'FM00000'  ) AS YD_STK_BED_L_MAX
				              ,TO_CHAR(SB.STACK_BED_W_MAX          ,'FM0000V0' ) AS YD_STK_BED_W_MAX
				              ,DECODE(TS.YD_CAR_PROG_STAT,'1','S','A','S','2','A','B','A') 
				                                                                 AS YD_CAR_ARRSTRT_STAT
				              ,SC.YD_CAR_USE_GP                                  AS YD_CAR_USE_GP
				              ,TS.YD_EQP_WRK_STAT                                AS YD_EQP_WRK_STAT
				              ,SC.CAR_NO                                         AS CAR_NO
				              ,SC.TRN_EQP_CD                                     AS TRN_EQP_CD
				              ,SC.CARD_NO                                        AS CARD_NO
				              ,TS.YD_CAR_AIM_YD_GP                               AS YD_CAR_AIM_YD_GP 
				          FROM TB_YM_STACKER    SB
				             , TB_YM_STACKCOL   SC
				             , TB_YM_STACKLAYER SL
				             , (SELECT STACK_COL_GP
				                      ,SUBSTR(YD_CARUD_STOP_LOC,1,1) AS YD_CAR_AIM_YD_GP
				                      ,YD_CAR_PROG_STAT
				                      ,YD_EQP_WRK_STAT
				                  FROM (SELECT SC.STACK_COL_GP
				                              ,TS.YD_CARUD_STOP_LOC
				                              ,TS.YD_CAR_PROG_STAT
				                              ,TS.YD_EQP_WRK_STAT
				                              ,ROW_NUMBER() OVER (ORDER BY TS.YD_CAR_SCH_ID DESC) AS RN
				                          FROM TB_YM_STACKCOL SC
				                              ,TB_YD_CARSCH TS
				                         WHERE SC.STACK_COL_GP = :V_STACK_COL_GP
				                           AND SUBSTR(SC.STACK_COL_GP,3,2) IN ('TR','PT')
				                           AND SC.DEL_YN        = 'N'
				                           AND TS.DEL_YN        = 'N'
				                           AND ((TS.TRN_EQP_CD    = SC.TRN_EQP_CD
				                             AND SC.YD_CAR_USE_GP = 'L')  --구내운송
				                             OR (TS.CAR_NO        = SC.CAR_NO
				                             AND TS.CARD_NO       = SC.CARD_NO
				                             AND SC.YD_CAR_USE_GP = 'G')) --출하차량
				                         UNION ALL
				                        SELECT SC.STACK_COL_GP
				                              ,TS.YD_CARUD_STOP_LOC
				                              ,TS.YD_CAR_PROG_STAT
				                              ,TS.YD_EQP_WRK_STAT
				                              ,ROW_NUMBER() OVER (ORDER BY TS.YD_TCAR_SCH_ID DESC) AS RN
				                          FROM TB_YM_STACKCOL  SC
				                              ,TB_YM_TCARSCH TS
				                         WHERE SC.STACK_COL_GP = :V_STACK_COL_GP
				                           AND SC.STACK_COL_GP LIKE '__TC%'
				                           AND SC.DEL_YN        = 'N'
				                           AND TS.YD_EQP_ID     = SUBSTR(SC.STACK_COL_GP,1,1)||'X'||SUBSTR(SC.STACK_COL_GP,3)
				                           AND TS.DEL_YN        = 'N')
				                 WHERE RN = 1) TS

				         WHERE SC.STACK_COL_GP = SB.STACK_COL_GP
				           AND SB.STACK_COL_GP = SL.STACK_COL_GP
				           AND SB.STACK_BED_GP = SL.STACK_BED_GP
				           AND SC.STACK_COL_GP = TS.STACK_COL_GP(+)
				           AND SB.STACK_COL_GP LIKE NVL(:V_STACK_COL_GP,'X')||'%'
				           AND SB.STACK_BED_GP LIKE     :V_STACK_BED_GP||'%'
				           AND SC.DEL_YN = 'N'
				           AND SB.DEL_YN = 'N'
				           AND SL.DEL_YN = 'N'
				         ORDER BY BAY_GP, SECT_GP, STACK_COL_GP, STACK_BED_GP, STACK_LAYER_GP    
				  
				       )

	
		    	 */
		    	jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L001_PIDEV";
		    	
				// PIDEV
				jrParam.setField("PI_YD"	, "3");
		    	
			} else if("YMA7L001_CarInfo".equals(msgId)) {

		    	trtNm = "B열연 코일 저장위치제원(차량정보Backup";
		    	jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L001_CarInfo";
			} else if("YMA7L002_SCRAP".equals(msgId)) {
				
				trtNm = "B열연 코일 저장품제원";
				
				String ydInfoSyncCd = commUtils.trim(jrParam.getFieldString("YD_INFO_SYNC_CD"));
				
				if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
					//위치별 >> 1:동,2:SPAN,3:열,4:BED
					jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L002ByLoc_SCRAP";
				} else {
					jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L002_SCRAP";
				}
			} else if("YMA7L002".equals(msgId)) {

		    	trtNm = "B열연 코일 저장품제원";
		    	
				//야드정보동기화코드 
				String ydInfoSyncCd = commUtils.trim(jrParam.getFieldString("YD_INFO_SYNC_CD"));
				
				if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
					//위치별 >> 1:동,2:SPAN,3:열,4:BED
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L002ByLoc_PIDEV
					-- 저장품제원정보
					SELECT JMS_TC_CD                                     --JMSTC코드
					      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
					      ,JMS_TC_CD                                     --전문ID
					     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')       --생성일시
					     ||RPAD(NVL(MSG_GP                 ,'I'), 1,' ') --전문구분
					     ||'0318'                                        --전문길이
					     ||RPAD(' ',29,' ')                              --임시
					     ||RPAD(NVL(YD_INFO_SYNC_CD        ,' '), 1,' ') --야드정보동기화코드
					     ||TO_CHAR(LEAST(COUNT(*) OVER (),999),'FM000')  --야드재료정보송신매수
					     ||TO_CHAR(LEAST(ROWNUM          ,999),'FM000')  --야드재료정보송신순번
					     ||RPAD(NVL(STL_APPEAR_GP          ,' '), 1,' ') --재료외형구분
					     ||RPAD(NVL(STOCK_ID               ,' '),11,' ') --재료번호
					     ||RPAD(NVL(YD_STR_LOC             ,' '), 8,' ') --야드저장위치
					     ||RPAD(NVL(STACK_LAYER_GP         ,' '), 2,' ') --야드적치단번호
					     ||LPAD(NVL(YD_STL_WT              ,'0'), 5,'0') --야드재료중량
					     ||LPAD(NVL(YD_STL_T               ,'0'), 6,'0') --야드재료두께
					     ||LPAD(NVL(YD_STL_W               ,'0'), 5,'0') --야드재료폭
					     ||LPAD(NVL(YD_STL_L               ,'0'), 7,'0') --야드재료길이
					     ||LPAD(NVL(MAT_ODIA               ,'0'), 5,'0') --재료외경
					     ||LPAD(NVL(MAT_IDIA               ,'0'), 5,'0') --재료내경
					     ||RPAD(NVL(STLKIND_CD             ,' '), 3,' ') --강종
					     ||RPAD(NVL(SPEC_ABBSYM            ,' '),15,' ') --규격약호
					     ||RPAD(NVL(YD_IPGO_DD             ,' '),14,' ') --야드입고일자
					     ||RPAD(NVL(PLNT_PROC_CD           ,' '), 3,' ') --공장공정코드
					     ||RPAD(NVL(CURR_PROG_CD           ,' '), 1,' ') --현재진도코드
					     ||RPAD(NVL(ORD_YEOJAE_GP          ,' '), 1,' ') --주문여재구분
					     ||RPAD(NVL(ORD_NO                 ,' '),10,' ') --주문번호
					     ||RPAD(NVL(ORD_DTL                ,' '), 3,' ') --주문행번
					     ||RPAD(NVL(BUY_SLAB_NO            ,' '),30,' ') --구입SLAB번호
					     ||RPAD(NVL(SLAB_WO_RT_CD          ,' '), 2,' ') --SLAB지시행선코드
					     ||RPAD(NVL(ORD_HCR_GP             ,' '), 1,' ') --설계HCR구분
					     ||RPAD(NVL(HCR_GP                 ,' '), 1,' ') --HCR구분
					     ||RPAD(NVL(CC_MC_CD               ,' '), 1,' ') --연주Machine코드          
					     ||RPAD(NVL(SCARFING_YN            ,' '), 1,' ') --SCARFING여부             
					     ||RPAD(NVL(SCARFING_DONE_YN       ,' '), 1,' ') --SCARFING완료유무         
					     ||RPAD(NVL(RPR_MTD                ,' '), 1,' ') --주편손질방법             
					     ||RPAD(NVL(SCARFING_DEPTH         ,' '), 2,' ') --SCARFING깊이             
					     ||RPAD(NVL(REHEAT_SLAB_GP         ,' '), 1,' ') --재열재구분               
					     ||RPAD(NVL(PTOP_PLNT_GP           ,' '), 2,' ') --조업공장구분             
					     ||RPAD(NVL(REFUR_CHG_LOT_NO       ,' '),10,' ') --가열로장입Lot번호        
					     ||LPAD(NVL(CT_LOT_SCH_SERNO       ,'0'),22,'0') --생산통제Lot스케줄일련번호
					     ||RPAD(NVL(FRTOMOVE_ORD_DATE      ,' '), 8,' ') --이송지시일자             
					     ||RPAD(NVL(FRTOMOVE_PLANT_GP      ,' '), 2,' ') --이송공장구분             
					     ||RPAD(NVL(URGENT_FRTOMOVE_WORD_GP,' '), 1,' ') --긴급이송작업지시구분     
					     ||RPAD(NVL(HYSCO_TRANS_CLS        ,' '), 1,' ') --HYSCO운송구분            
					     ||RPAD(NVL(APPEAR_GRADE           ,' '), 1,' ') --외관종합판정등급         
					     ||RPAD(NVL(COOL_METHOD            ,' '), 1,' ') --권취코일냉각방법         
					     ||RPAD(NVL(COOL_DONE_GP           ,' '), 1,' ') --냉각완료구분             
					     ||RPAD(NVL(CONV_BRANCH_CD         ,' '), 2,' ') --야드Conveyor분기코드     
					     ||RPAD(NVL(CUST_KO_NAME           ,' '),40,' ') --고객사명                 
					     ||RPAD(NVL(DEST_CD                ,' '), 5,' ') --목적지코드               
					     ||RPAD(NVL(DLVRDD_RULE_DD         ,' '), 8,' ') --납기기준일               
					     ||RPAD(NVL(ITEMNAME_CD            ,' '), 3,' ') --품명코드                 
					     ||RPAD(NVL(OVERALL_STATAMP_GRADE  ,' '), 1,' ') --종합판정등급             
					     ||RPAD(NVL(ORD_GP                 ,' '), 1,' ') --수주구분                 
					     ||RPAD(NVL(YD_STK_LOT_TP          ,' '), 2,' ') --야드산적LotType          
					     ||RPAD(NVL(YD_STK_LOT_CD          ,' '),18,' ') --야드산적Lot코드          
					     ||RPAD(NVL(YD_PLAN_PROC           ,' '),10,' ') --계획공정                 
					     ||RPAD(NVL(YD_PASS_PROC           ,' '),10,' ') --통과공정                 
					     ||RPAD(NVL(YD_NEXT_PROC           ,' '), 2,' ') --다음공정                 
					     ||RPAD(NVL(HRMILL_CMPL_DT         ,' '),14,' ') --열연압연완료일시         

					       AS JMS_TC_MESSAGE --JMSTCMESSAGE
					FROM (

					        SELECT   'YMA7L002'                             AS JMS_TC_CD                --전문ID
					               , :V_MSG_GP                              AS MSG_GP                   --전문구분
					               , :V_YD_INFO_SYNC_CD                     AS YD_INFO_SYNC_CD          --야드정보동기화코드  
					               , '001'                                  AS YD_STL_INFO_SND_SH       --야드재료정보송신매수
					               , '001'                                  AS YD_STL_INFO_SND_CNT      --야드재료정보송신순번
					               , CC.STL_APPEAR_GP                       AS STL_APPEAR_GP            --재료외형구분
					               , ST.STOCK_ID                            AS STOCK_ID                 --재료번호
					               , LY.STACK_COL_GP || LY.STACK_BED_GP     AS YD_STR_LOC               --야드저장위치
					               , LY.STACK_LAYER_GP                      AS STACK_LAYER_GP           --야드적치단번호
					               , TO_CHAR(CC.COIL_WT    , 'FM00000'  )   AS YD_STL_WT                --야드재료중량
					               , TO_CHAR(CC.COIL_T     , 'FM000V000')   AS YD_STL_T                 --야드재료두께
					               , TO_CHAR(CC.COIL_W     , 'FM0000V0' )   AS YD_STL_W                 --야드재료폭
					               , TO_CHAR(CC.COIL_LEN   , 'FM0000000')   AS YD_STL_L                 --야드재료길이
					               , TO_CHAR(CC.COIL_OUTDIA, 'FM00000'  )   AS MAT_ODIA                 --재료외경
					               , TO_CHAR(CC.COIL_INDIA , 'FM0000V0' )   AS MAT_IDIA                 --재료내경
					               , NULL                                   AS STLKIND_CD               --강종
					               , CC.SPEC_ABBSYM                         AS SPEC_ABBSYM              --규격약호
					               , CC.RECEIPT_DATE                        AS YD_IPGO_DD               --야드입고일자
					               , CC.PLNT_PROC_CD                        AS PLNT_PROC_CD             --공장공정코드
					               , CC.CURR_PROG_CD                        AS CURR_PROG_CD             --현재진도코드
					               , CC.ORD_YEOJAE_GP                       AS ORD_YEOJAE_GP            --주문여재구분
					               , CC.ORD_NO                              AS ORD_NO                   --주문번호
					               , CC.ORD_DTL                             AS ORD_DTL                  --주문행번
					               , NULL                                   AS BUY_SLAB_NO              --구입SLAB번호
					               , NULL                                   AS SLAB_WO_RT_CD            --SLAB지시행선코드
					               , OC.ORD_HCR_GP                          AS ORD_HCR_GP               --설계HCR구분
					               , CC.HCR_GP                              AS HCR_GP                   --HCR구분
					               , NULL                                   AS CC_MC_CD                 --연주Machine코드
					               , NULL                                   AS SCARFING_YN              --SCARFING여부
					               , NULL                                   AS SCARFING_DONE_YN         --SCARFING완료유무
					               , NULL                                   AS RPR_MTD                  --주편손질방법
					               , NULL                                   AS SCARFING_DEPTH           --SCARFING깊이
					               , NULL                                   AS REHEAT_SLAB_GP           --재열재구분
					               , CC.PTOP_PLNT_GP                        AS PTOP_PLNT_GP             --조업공장구분
					               , NULL                                   AS REFUR_CHG_LOT_NO         --가열로장입Lot번호
					               , NULL                                   AS CT_LOT_SCH_SERNO         --생산통제Lot스케줄일련번호
					               , CC.FRTOMOVE_ORD_DATE                   AS FRTOMOVE_ORD_DATE         --이송지시일자
					               , CC.FRTOMOVE_PLANT_GP                   AS FRTOMOVE_PLANT_GP         --이송공장구분
					               , ''                                     AS URGENT_FRTOMOVE_WORD_GP  --긴급이송작업지시구분
					               , CC.HYSCO_TRANS_GP                      AS HYSCO_TRANS_CLS          --HYSCO운송구분
					               , CC.APPEAR_GRADE                        AS APPEAR_GRADE             --외관종합판정등급
					               , OC.COOL_METHOD                         AS COOL_METHOD              --권취코일냉각방법
					               , CC.COOL_DONE_GP                                   AS COOL_DONE_GP             --냉각완료구분
					               , NULL                                   AS CONV_BRANCH_CD           --야드Conveyor분기코드
					               , (SELECT REPLACE(CUST_KO_NAME,'㈜','(주)') 
					                    FROM TB_SM_CUSTINFO 
					                   WHERE CUST_CD = OC.CUST_CD)          AS CUST_KO_NAME             --고객사명
					               , OC.DEST_CD                             AS DEST_CD                  --목적지코드
					               , TO_CHAR(OC.ORD_CONS_DATE,'YYYYMMDD')   AS DLVRDD_RULE_DD           --납기기준일
					               , CC.ITEMNAME_CD                         AS ITEMNAME_CD              --품명코드
					               , CC.OVERALL_STAMP_GRADE                 AS OVERALL_STATAMP_GRADE    --종합판정등급
					               , OC.ORD_GP                              AS ORD_GP                   --수주구분
					               , NULL                                   AS YD_STK_LOT_TP             --야드산적LotType
					               , ST.STACK_LOT_NO                        AS YD_STK_LOT_CD             --야드산적Lot코드
					               , CC.PLAN_PROC1||CC.PLAN_PROC2||CC.PLAN_PROC3||CC.PLAN_PROC4||CC.PLAN_PROC5  AS YD_PLAN_PROC  --계획공정
					               , CC.PASS_PROC1||CC.PASS_PROC2||CC.PASS_PROC3||CC.PASS_PROC4||CC.PASS_PROC5  AS YD_PASS_PROC  --통과공정
					               , CC.NEXT_PROC                           AS YD_NEXT_PROC              --다음공정
					               , TO_CHAR(CC.HRMILL_CMPL_DT,'YYYYMMDDHH24MISS') AS HRMILL_CMPL_DT     --열연압연완료일시
					               
					          FROM 
					                 TB_YM_STOCK ST
					               , TB_YM_STACKLAYER LY
					               , TB_PT_COILCOMM CC
					               , TB_PT_OSCOMM OC
					              
					         WHERE LY.STACK_COL_GP LIKE :V_STACK_COL_GP || '%'
					           AND LY.STACK_BED_GP LIKE :V_STACK_BED_GP || '%'
					           AND LY.STACK_LAYER_STAT IN ('C','U','L')
					           AND LY.DEL_YN = 'N'
					           AND LY.STOCK_ID = ST.STOCK_ID
					           AND ST.STOCK_ID = CC.COIL_NO
					           AND CC.ORD_NO  = OC.ORD_NO(+)        
					           AND CC.ORD_DTL = OC.ORD_DTL(+) 
					     )

					*/
			    	jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L002ByLoc_PIDEV";
			    	
				} else {
					//재료별 >> 5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제),H:C열연장입,P:1후판장입,Q:2후판장입
			    	jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L002_PIDEV";
				}
				
				// PIDEV
				jrParam.setField("PI_YD"	, "3");
				
			} else if("YMA7L002DnWr".equals(msgId)) {
			    
				trtNm = "B열연 코일 저장품 제원";
		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L002DnWr_PIDEV
				-- 저장품제원정보
				SELECT JMS_TC_CD                                     --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				      ,JMS_TC_CD                                     --전문ID
				     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')       --생성일시
				     ||'I'                                           --전문구분
				     ||'0318'                                        --전문길이
				     ||RPAD(' ',29,' ')                              --임시
				     ||RPAD(NVL(YD_INFO_SYNC_CD        ,' '), 1,' ') --야드정보동기화코드
				     ||RPAD(NVL(YD_STL_INFO_SND_SH     ,' '), 3,' ') --야드재료정보송신매수
				     ||RPAD(NVL(YD_STL_INFO_SND_CNT    ,' '), 3,' ') --야드재료정보송신순번
				     ||RPAD(NVL(STL_APPEAR_GP          ,' '), 1,' ') --재료외형구분
				     ||RPAD(NVL(STOCK_ID               ,' '),11,' ') --재료번호
				     ||RPAD(NVL(YD_STR_LOC             ,' '), 8,' ') --야드저장위치
				     ||RPAD(NVL(STACK_LAYER_GP         ,' '), 2,' ') --야드적치단번호
				     ||RPAD(NVL(YD_STL_WT              ,' '), 5,' ') --야드재료중량
				     ||RPAD(NVL(YD_STL_T               ,' '), 6,' ') --야드재료두께
				     ||RPAD(NVL(YD_STL_W               ,' '), 5,' ') --야드재료폭
				     ||RPAD(NVL(YD_STL_L               ,' '), 7,' ') --야드재료길이
				     ||RPAD(NVL(MAT_ODIA               ,' '), 5,' ') --재료외경
				     ||RPAD(NVL(MAT_IDIA               ,' '), 5,' ') --재료내경
				     ||RPAD(NVL(STLKIND_CD             ,' '), 3,' ') --강종
				     ||RPAD(NVL(SPEC_ABBSYM            ,' '),15,' ') --규격약호
				     ||RPAD(NVL(YD_IPGO_DD             ,' '),14,' ') --야드입고일자
				     ||RPAD(NVL(PLNT_PROC_CD           ,' '), 3,' ') --공장공정코드
				     ||RPAD(NVL(CURR_PROG_CD           ,' '), 1,' ') --현재진도코드
				     ||RPAD(NVL(ORD_YEOJAE_GP          ,' '), 1,' ') --주문여재구분
				     ||RPAD(NVL(ORD_NO                 ,' '),10,' ') --주문번호
				     ||RPAD(NVL(ORD_DTL                ,' '), 3,' ') --주문행번
				     ||RPAD(NVL(BUY_SLAB_NO            ,' '),30,' ') --구입SLAB번호
				     ||RPAD(NVL(SLAB_WO_RT_CD          ,' '), 2,' ') --SLAB지시행선코드
				     ||RPAD(NVL(ORD_HCR_GP             ,' '), 1,' ') --설계HCR구분
				     ||RPAD(NVL(HCR_GP                 ,' '), 1,' ') --HCR구분
				     ||RPAD(NVL(CC_MC_CD               ,' '), 1,' ') --연주Machine코드          
				     ||RPAD(NVL(SCARFING_YN            ,' '), 1,' ') --SCARFING여부             
				     ||RPAD(NVL(SCARFING_DONE_YN       ,' '), 1,' ') --SCARFING완료유무         
				     ||RPAD(NVL(RPR_MTD                ,' '), 1,' ') --주편손질방법             
				     ||RPAD(NVL(SCARFING_DEPTH         ,' '), 2,' ') --SCARFING깊이             
				     ||RPAD(NVL(REHEAT_SLAB_GP         ,' '), 1,' ') --재열재구분               
				     ||RPAD(NVL(PTOP_PLNT_GP           ,' '), 2,' ') --조업공장구분             
				     ||RPAD(NVL(REFUR_CHG_LOT_NO       ,' '),10,' ') --가열로장입Lot번호        
				     ||RPAD(NVL(CT_LOT_SCH_SERNO       ,' '),22,' ') --생산통제Lot스케줄일련번호
				     ||RPAD(NVL(FRTOMOVE_ORD_DATE      ,' '), 8,' ') --이송지시일자             
				     ||RPAD(NVL(FRTOMOVE_PLANT_GP      ,' '), 2,' ') --이송공장구분             
				     ||RPAD(NVL(URGENT_FRTOMOVE_WORD_GP,' '), 1,' ') --긴급이송작업지시구분     
				     ||RPAD(NVL(HYSCO_TRANS_CLS        ,' '), 1,' ') --HYSCO운송구분            
				     ||RPAD(NVL(APPEAR_GRADE           ,' '), 1,' ') --외관종합판정등급         
				     ||RPAD(NVL(COOL_METHOD            ,' '), 1,' ') --권취코일냉각방법         
				     ||RPAD(NVL(COOL_DONE_GP           ,' '), 1,' ') --냉각완료구분             
				     ||RPAD(NVL(CONV_BRANCH_CD         ,' '), 2,' ') --야드Conveyor분기코드     
				     ||RPAD(NVL(CUST_KO_NAME           ,' '),40,' ') --고객사명                 
				     ||RPAD(NVL(DEST_CD                ,' '), 5,' ') --목적지코드               
				     ||RPAD(NVL(DLVRDD_RULE_DD         ,' '), 8,' ') --납기기준일               
				     ||RPAD(NVL(ITEMNAME_CD            ,' '), 3,' ') --품명코드                 
				     ||RPAD(NVL(OVERALL_STATAMP_GRADE  ,' '), 1,' ') --종합판정등급             
				     ||RPAD(NVL(ORD_GP                 ,' '), 1,' ') --수주구분                 
				     ||RPAD(NVL(YD_STK_LOT_TP          ,' '), 2,' ') --야드산적LotType          
				     ||RPAD(NVL(YD_STK_LOT_CD          ,' '),18,' ') --야드산적Lot코드          
				     ||RPAD(NVL(YD_PLAN_PROC           ,' '),10,' ') --계획공정                 
				     ||RPAD(NVL(YD_PASS_PROC           ,' '),10,' ') --통과공정                 
				     ||RPAD(NVL(YD_NEXT_PROC           ,' '), 2,' ') --다음공정                 
				     ||RPAD(NVL(HRMILL_CMPL_DT         ,' '),14,' ') --열연압연완료일시         
				
				       AS JMS_TC_MESSAGE --JMSTCMESSAGE
				FROM (
				
				  SELECT  'YMA7L002'                 AS JMS_TC_CD
				        , :V_YD_INFO_SYNC_CD         AS YD_INFO_SYNC_CD            --야드정보동기화코드              
				        , '001'                      AS YD_STL_INFO_SND_SH        --야드재료정보송신매수
				        , '001'                      AS YD_STL_INFO_SND_CNT       --야드재료정보송신순번
				        , B.STL_APPEAR_GP            AS STL_APPEAR_GP             --재료외형구분
				        , A.STOCK_ID                 AS STOCK_ID                  --재료번호
				        , SUBSTR(A.CARUNLOAD_PUT_LOC,1,8)                 
				                                     AS YD_STR_LOC                --야드저장위치
				        , SUBSTR(A.CARUNLOAD_PUT_LOC,9,2)
				                                     AS STACK_LAYER_GP            --야드적치단번호
				        , TO_CHAR(B.COIL_WT    , 'FM00000'  ) AS YD_STL_WT        --야드재료중량
				        , TO_CHAR(B.COIL_T     , 'FM000V000') AS YD_STL_T         --야드재료두께
				        , TO_CHAR(B.COIL_W     , 'FM0000V0' ) AS YD_STL_W         --야드재료폭
				        , TO_CHAR(B.COIL_LEN   , 'FM0000000') AS YD_STL_L         --야드재료길이
				        , TO_CHAR(B.COIL_OUTDIA, 'FM00000'  ) AS MAT_ODIA         --재료외경
				        , TO_CHAR(B.COIL_INDIA , 'FM0000V0' ) AS MAT_IDIA         --재료내경
				        , NULL                       AS STLKIND_CD                --강종
				        , B.SPEC_ABBSYM              AS SPEC_ABBSYM               --규격약호
				        , B.RECEIPT_DATE             AS YD_IPGO_DD                --야드입고일자
				        , B.PLNT_PROC_CD             AS PLNT_PROC_CD              --공장공정코드
				        , B.CURR_PROG_CD             AS CURR_PROG_CD              --현재진도코드
				        , B.ORD_YEOJAE_GP            AS ORD_YEOJAE_GP             --주문여재구분
				        , B.ORD_NO                   AS ORD_NO                    --주문번호
				        , B.ORD_DTL                  AS ORD_DTL                   --주문행번
				        , NULL                       AS BUY_SLAB_NO               --구입SLAB번호
				        , NULL                       AS SLAB_WO_RT_CD             --SLAB지시행선코드
				        , C.ORD_HCR_GP               AS ORD_HCR_GP                --설계HCR구분
				        , B.HCR_GP                   AS HCR_GP                    --HCR구분
				        , NULL                       AS CC_MC_CD                  --연주Machine코드
				        , NULL                       AS SCARFING_YN               --SCARFING여부
				        , NULL                       AS SCARFING_DONE_YN          --SCARFING완료유무
				        , NULL                       AS RPR_MTD                   --주편손질방법
				        , NULL                       AS SCARFING_DEPTH            --SCARFING깊이
				        , NULL                       AS REHEAT_SLAB_GP            --재열재구분
				        , B.PTOP_PLNT_GP             AS PTOP_PLNT_GP              --조업공장구분
				        , NULL                       AS REFUR_CHG_LOT_NO          --가열로장입Lot번호
				        , NULL                       AS CT_LOT_SCH_SERNO          --생산통제Lot스케줄일련번호
				        , B.FRTOMOVE_ORD_DATE        AS FRTOMOVE_ORD_DATE         --이송지시일자
				        , B.FRTOMOVE_PLANT_GP        AS FRTOMOVE_PLANT_GP         --이송공장구분
				        , NULL                       AS URGENT_FRTOMOVE_WORD_GP   --긴급이송작업지시구분
				        , B.HYSCO_TRANS_GP           AS HYSCO_TRANS_CLS           --HYSCO운송구분
				        , B.APPEAR_GRADE             AS APPEAR_GRADE              --외관종합판정등급
				        , C.COOL_METHOD              AS COOL_METHOD               --권취코일냉각방법
				        , B.COOL_DONE_GP             AS COOL_DONE_GP              --냉각완료구분
				        , NULL                       AS CONV_BRANCH_CD            --야드Conveyor분기코드
				        , (SELECT REPLACE(CUST_KO_NAME,'㈜','(주)') FROM TB_SM_CUSTINFO WHERE CUST_CD =B.CUST_CD)           
				                                     AS CUST_KO_NAME              --고객사명
				        , C.DEST_CD                  AS DEST_CD                   --목적지코드
				        , NULL                       AS DLVRDD_RULE_DD            --납기기준일
				        , B.ITEMNAME_CD              AS ITEMNAME_CD               --품명코드
				        , NULL                       AS OVERALL_STATAMP_GRADE     --종합판정등급
				        , C.ORD_GP                   AS ORD_GP                    --수주구분
				        , NULL                       AS YD_STK_LOT_TP             --야드산적LotType
				        , A.STACK_LOT_NO             AS YD_STK_LOT_CD             --야드산적Lot코드
				        , B.PLAN_PROC1||B.PLAN_PROC2||B.PLAN_PROC3||B.PLAN_PROC4||B.PLAN_PROC5 
				        AS YD_PLAN_PROC              --계획공정
				        , B.PASS_PROC1||B.PASS_PROC2||B.PASS_PROC3||B.PASS_PROC4||B.PASS_PROC5            AS YD_PASS_PROC              --통과공정
				        , B.NEXT_PROC                AS YD_NEXT_PROC              --다음공정
				        , TO_CHAR(B.HRMILL_CMPL_DT,'YYYYMMDDHH24MISS') AS HRMILL_CMPL_DT     --열연압연완료일시
					   
				FROM  TB_YM_STOCK A
				    , USRPTA.TB_PT_COILCOMM B
				    , USRPTA.TB_PT_OSCOMM C 
				WHERE A.STOCK_ID  = B.COIL_NO
				  AND B.ORD_NO  = C.ORD_NO(+)        
				  AND B.ORD_DTL = C.ORD_DTL(+) 
				  AND A.STOCK_ID IN (
				
				            SELECT CM.STOCK_ID
				            FROM   TB_YM_CRNWRKMTL CM
				                  ,TB_YM_STACKLAYER    SL
				            WHERE  CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID  
				            AND    CM.STOCK_ID      = SL.STOCK_ID
				            AND    SL.STACK_LAYER_STAT IN ('C','U')
				
				        )
				)
		    	*/
		    	jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L002DnWr_PIDEV";
		    	
			} else if("YMA7L004".equals(msgId)) {
				
				//DEFAULT
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMA7L004_PIDEV";
				// PIDEV
				jrParam.setField("PI_YD"	, "3");
				
				//SCRAP
		    	trtNm = "1열연 COIL 작업지시";

		    	String sAPP022  = "N";
		    	JDTORecord jrParam1 = commUtils.getParam("", methodNm, "");
				jrParam1.setField("REPR_CD_GP", "APP022"  ); 
				jrParam1.setField("CD_GP"     , "3"       ); 
				jrParam1.setField("ITEM"      , "1"       ); 

				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getBCoilApplyYn
				SELECT NVL(MAX(DTL_ITM1),'N') AS APPLY_YN
				  FROM USRYMA.TB_YM_RULE
				 WHERE REPR_CD_GP = :V_REPR_CD_GP  -- APP001
				   AND CD_GP = :V_CD_GP            -- CD_GP
				   AND ITEM  = :V_ITEM
				   AND DEL_YN = 'N'
				*/  
				JDTORecordSet jsChk = this.select(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getBCoilApplyYn", logId, methodNm, "열정보 Read"); 

				if (jsChk.size() > 0) {
					sAPP022    = commUtils.trim(jsChk.getRecord(0).getFieldString("APPLY_YN"));
				}
	            
		    	if("Y".equals(sAPP022)) {
			    	/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getSchScrapInfoWithSchId 
			    	SELECT STOCK_ID
			    	     , (SELECT MAX(STEP_NO) FROM USRPOA.TB_PO_COILSHEARORD_SCRAP WHERE SCRAP_COIL_NO = MT.STOCK_ID ) AS STEP_NO
			    	  FROM TB_YM_CRNSCH     SC
			    	     , TB_YM_CRNWRKMTL  MT
			    	 WHERE SC.YD_CRN_SCH_ID = MT.YD_CRN_SCH_ID
			    	   AND SC.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			    	   AND SC.DEL_YN = 'N'
			    	   AND MT.DEL_YN = 'N'
					 */
					JDTORecordSet schInfo = this.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getSchScrapInfoWithSchId");
	
					if (schInfo.size() > 0) {
						String sSTOCK_ID      = StringHelper.evl(schInfo.getRecord(0).getFieldString("STOCK_ID"), "");
						String sSTEP_NO       = StringHelper.evl(schInfo.getRecord(0).getFieldString("STEP_NO"), "");
						if("S".equals(sSTOCK_ID.substring(0,1))) {
							jrParam.setField("STEP_NO"	, sSTEP_NO);
							jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMA7L004Scrap";		
						}
					}
		    	} //END if(sAPP022.equals("Y")) {
			} else if("YMA7L004WC".equals(msgId)) {
				
				trtNm = "1열연 분동COIL 작업지시";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMA7L004WeightCoil";
				
			} else if("YMA7L004ROT".equals(msgId)) {
				
				trtNm = "1열연 설비보급 회전 작업지시";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMA7L004Rotaion";
				
			}else if("YMA7L006".equals(msgId)) {
				
		    	trtNm = "B열연 COIL 대차출발지시";
		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMA7L006
		    	SELECT JMS_TC_CD --JMSTC코드
		    	      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
		    	      ,JMS_TC_CD                                  --전문ID
		    	     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')    --생성일시
		    	     ||'I'                                        --전문구분
		    	     ||'0029' --전문길이
		    	     ||RPAD(' ',29,' ')                           --임시
		    	     ||RPAD(NVL(YD_EQP_ID          ,' '), 6,' ')  --야드설비ID
		    	     ||RPAD(NVL(YD_EQP_WRK_STAT    ,' '), 1,' ')  --야드설비작업상태
		    	     ||RPAD(NVL(YD_AIM_BAY_GP      ,' '), 1,' ')  --야드목표동구분
		    	     ||RPAD(NVL(YD_TCAR_LD_LOC     ,' '), 6,' ')  --야드상차정지위치
		    	     ||RPAD(NVL(YD_TCAR_UD_LOC     ,' '), 6,' ')  --야드하차정지위치
		    	     ||RPAD(NVL(YD_EQP_WRK_SH      ,' '), 2,' ')  --야드대차작업매수
		    	     ||RPAD(NVL(YD_EQP_WRK_WT      ,' '), 7,' ')  --야드대차작업중량
		    	     
		    	       AS JMS_TC_MESSAGE    --JMSTCMessage
		    	  FROM (SELECT 'YMA7L006'  AS JMS_TC_CD
		    	              ,YD_EQP_ID
		    	              ,YD_EQP_WRK_STAT
		    	              ,SUBSTR(DECODE(YD_EQP_WRK_STAT,'L',YD_CARUD_STOP_LOC,YD_CARLD_STOP_LOC),2,1) AS YD_AIM_BAY_GP
		    	              ,YD_CARLD_STOP_LOC                         AS YD_TCAR_LD_LOC
		    	              ,YD_CARUD_STOP_LOC                         AS YD_TCAR_UD_LOC
		    	              ,TO_CHAR(NVL(YD_EQP_WRK_SH,0),'FM00'     ) AS YD_EQP_WRK_SH
		    	              ,TO_CHAR(NVL(YD_EQP_WRK_WT,0),'FM0000000') AS YD_EQP_WRK_WT
		    	          FROM (SELECT TS.YD_EQP_ID
		    	                      ,MIN(TS.YD_CARLD_STOP_LOC) AS YD_CARLD_STOP_LOC
		    	                      ,MIN(TS.YD_CARUD_STOP_LOC) AS YD_CARUD_STOP_LOC
		    	                      ,MIN(TS.YD_EQP_WRK_STAT  ) AS YD_EQP_WRK_STAT
		    	                      ,COUNT(ST.COIL_NO)         AS YD_EQP_WRK_SH
		    	                      ,SUM(ST.COIL_WT)           AS YD_EQP_WRK_WT
		    	                  FROM TB_YM_TCARSCH         TS
		    	                      ,TB_YM_TCARFTMVMTL     TM
		    	                      ,USRPTA.TB_PT_COILCOMM ST
		    	                 WHERE TS.YD_TCAR_SCH_ID = TM.YD_TCAR_SCH_ID(+)
		    	                   AND TM.DEL_YN(+)      = 'N'
		    	                   AND TM.STOCK_ID       = ST.COIL_NO(+)
		    	                   AND TS.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
		    	                 GROUP BY TS.YD_EQP_ID))
		    	 WHERE JMS_TC_CD IS NOT NULL
		    	 */                
		    	jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMA7L006";
		    			    	
			} else if("YMA8L006".equals(msgId)) {
				
		    	trtNm = "B열연 SLAB 대차출발지시";
		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMA8L006 
		    	SELECT JMS_TC_CD --JMSTC코드
		    	      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
		    	      ,JMS_TC_CD                                  --전문ID
		    	     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')    --생성일시
		    	     ||'I'                                        --전문구분
		    	     ||'0034' --전문길이
		    	     ||RPAD(' ',31,' ')                           --임시
		    	     ||RPAD(NVL(YD_EQP_ID          ,' '), 6,' ')  --야드설비ID
		    	     ||RPAD(NVL(YD_EQP_WRK_STAT    ,' '), 1,' ')  --야드설비작업상태
		    	     ||RPAD(NVL(YD_AIM_BAY_GP      ,' '), 6,' ')  --야드목표동구분
		    	     ||RPAD(NVL(YD_TCAR_LD_LOC     ,' '), 6,' ')  --야드상차정지위치
		    	     ||RPAD(NVL(YD_TCAR_UD_LOC     ,' '), 6,' ')  --야드하차정지위치
		    	     ||RPAD(NVL(YD_EQP_WRK_SH      ,' '), 2,' ')  --야드대차작업매수
		    	     ||RPAD(NVL(YD_EQP_WRK_WT      ,' '), 7,' ')  --야드대차작업중량
		    	     
		    	       AS JMS_TC_MESSAGE    --JMSTCMessage
		    	  FROM (SELECT 'YMA8L006'  AS JMS_TC_CD
		    	              ,YD_EQP_ID
		    	              ,YD_EQP_WRK_STAT
		    	              ,DECODE(YD_EQP_WRK_STAT,'L',YD_CARUD_STOP_LOC,YD_CARLD_STOP_LOC) AS YD_AIM_BAY_GP
		    	              ,CASE WHEN NVL(:V_HOME_BAY,'N') = 'Y' THEN  :V_YD_CARLD_STOP_LOC
		    	                    ELSE YD_CARLD_STOP_LOC     END          AS YD_TCAR_LD_LOC
		    	              ,CASE WHEN NVL(:V_HOME_BAY,'N') = 'Y' THEN  :V_YD_CARUD_STOP_LOC
		    	                    ELSE YD_CARUD_STOP_LOC     END          AS YD_TCAR_UD_LOC
		    	              ,TO_CHAR(NVL(YD_EQP_WRK_SH,0),'FM00'     ) AS YD_EQP_WRK_SH
		    	              ,TO_CHAR(NVL(YD_EQP_WRK_WT,0),'FM0000000') AS YD_EQP_WRK_WT
		    	          FROM (SELECT TS.YD_EQP_ID
		    	                      ,MIN(TS.YD_CARLD_STOP_LOC) AS YD_CARLD_STOP_LOC
		    	                      ,MIN(TS.YD_CARUD_STOP_LOC) AS YD_CARUD_STOP_LOC
		    	                      ,MIN(TS.YD_EQP_WRK_STAT  ) AS YD_EQP_WRK_STAT
		    	                      ,COUNT(ST.SLAB_NO)         AS YD_EQP_WRK_SH
		    	                      ,SUM(ST.SLAB_WT)           AS YD_EQP_WRK_WT
		    	                  FROM TB_YM_TCARSCH         TS
		    	                      ,TB_YM_TCARFTMVMTL     TM
		    	                      ,USRPTA.TB_PT_SLABCOMM ST
		    	                 WHERE TS.YD_TCAR_SCH_ID = TM.YD_TCAR_SCH_ID(+)
		    	                   AND TM.DEL_YN(+)      = 'N'
		    	                   AND TM.STOCK_ID       = ST.SLAB_NO(+)
		    	                   AND TS.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
		    	                 GROUP BY TS.YD_EQP_ID))
		    	 WHERE JMS_TC_CD IS NOT NULL 
		    	 */                
		    	jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMA8L006";
		    			    	
			} else if("YMA8L006BACKUP".equals(msgId)) {
				
		    	trtNm = "B열연 SLAB 대차출발지시 백업";
		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMA8L006 
		    	SELECT JMS_TC_CD --JMSTC코드
		    	      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
		    	      ,JMS_TC_CD                                  --전문ID
		    	     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')    --생성일시
		    	     ||'I'                                        --전문구분
		    	     ||'0034' --전문길이
		    	     ||RPAD(' ',31,' ')                           --임시
		    	     ||RPAD(NVL(YD_EQP_ID          ,' '), 6,' ')  --야드설비ID
		    	     ||RPAD(NVL(YD_EQP_WRK_STAT    ,' '), 1,' ')  --야드설비작업상태
		    	     ||RPAD(NVL(YD_AIM_BAY_GP      ,' '), 6,' ')  --야드목표동구분
		    	     ||RPAD(NVL(YD_TCAR_LD_LOC     ,' '), 6,' ')  --야드상차정지위치
		    	     ||RPAD(NVL(YD_TCAR_UD_LOC     ,' '), 6,' ')  --야드하차정지위치
		    	     ||RPAD(NVL(YD_EQP_WRK_SH      ,' '), 2,' ')  --야드대차작업매수
		    	     ||RPAD(NVL(YD_EQP_WRK_WT      ,' '), 7,' ')  --야드대차작업중량
		    	     
		    	       AS JMS_TC_MESSAGE    --JMSTCMessage
				  FROM (SELECT 'YMA8L006'  AS JMS_TC_CD
				              ,YD_EQP_ID
				              ,YD_EQP_WRK_STAT
				              ,DECODE(YD_EQP_WRK_STAT,'L',YD_CARUD_STOP_LOC,YD_CARLD_STOP_LOC) AS YD_AIM_BAY_GP
				              ,YD_CARLD_STOP_LOC            AS YD_TCAR_LD_LOC
				              ,YD_CARUD_STOP_LOC            AS YD_TCAR_UD_LOC
				              ,TO_CHAR(NVL(YD_EQP_WRK_SH,0),'FM00'     ) AS YD_EQP_WRK_SH
				              ,TO_CHAR(NVL(YD_EQP_WRK_WT,0),'FM0000000') AS YD_EQP_WRK_WT
				          FROM (SELECT TS.YD_EQP_ID
				                      ,MIN(TS.YD_CARLD_STOP_LOC) AS YD_CARLD_STOP_LOC
				                      ,MIN(TS.YD_CARUD_STOP_LOC) AS YD_CARUD_STOP_LOC
				                      ,MIN(TS.YD_EQP_WRK_STAT  ) AS YD_EQP_WRK_STAT
				                      ,COUNT(ST.SLAB_NO)         AS YD_EQP_WRK_SH
				                      ,SUM(ST.SLAB_WT)           AS YD_EQP_WRK_WT
				                  FROM TB_YM_TCARSCH         TS
				                      ,TB_YM_TCARFTMVMTL     TM
				                      ,USRYDA.VW_YD_SLABCOMM ST
				                 WHERE TS.YD_TCAR_SCH_ID = TM.YD_TCAR_SCH_ID(+)
				                   AND TM.DEL_YN(+)      = 'N'
				                   AND TM.STOCK_ID       = ST.SLAB_NO(+)
				                   AND TS.YD_EQP_ID = :V_YD_EQP_ID
				                   AND TS.DEL_YN    = 'N'
				                 GROUP BY TS.YD_EQP_ID))
				 WHERE JMS_TC_CD IS NOT NULL 
		    	 */                
		    	jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMA8L006BackUp";
		    			    			    	
			} else if("YMA7L007".equals(msgId)) {
				
		    	trtNm = "작업 현황 응답";
		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMA7L007 
		    	SELECT 'YMA7L007'                          AS JMS_TC_CD --JMSTC코드
		    	      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
		    	      ,'YMA7L007'                                 --전문ID
		    	     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')    --생성일시
		    	     ||'I'                                        --전문구분
		    	     ||'0046' --전문길이
		    	     ||RPAD(' ',46,' ')                           --임시
		    	     ||RPAD(NVL(:V_YD_EQP_ID       ,' '), 6,' ')  --야드설비ID
		    	     ||RPAD(NVL(YD_SCH_FLAG1       ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT1        ,' '), 3,' ')  --요구스케쥴건수1
		    	     ||RPAD(NVL(YD_SCH_FLAG2       ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT2        ,' '), 3,' ')  --요구스케쥴건수1
		    	     ||RPAD(NVL(YD_SCH_FLAG3       ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT3        ,' '), 3,' ')  --요구스케쥴건수1
		    	     ||RPAD(NVL(YD_SCH_FLAG4       ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT4        ,' '), 3,' ')  --요구스케쥴건수1
		    	     ||RPAD(NVL(YD_SCH_FLAG5       ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT5        ,' '), 3,' ')  --요구스케쥴건수1
		    	     ||RPAD(NVL(YD_SCH_FLAG6       ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT6        ,' '), 3,' ')  --요구스케쥴건수1
		    	     ||RPAD(NVL(YD_SCH_FLAG7       ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT7        ,' '), 3,' ')  --요구스케쥴건수1
		    	     ||RPAD(NVL(YD_SCH_FLAG8       ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT8        ,' '), 3,' ')  --요구스케쥴건수1
		    	     ||RPAD(NVL(YD_SCH_FLAG9       ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT9        ,' '), 3,' ')  --요구스케쥴건수1
		    	     ||RPAD(NVL(YD_SCH_FLAG10      ,' '), 1,' ')  --요구스케쥴구분1
		    	     ||RPAD(NVL(YD_SCH_CNT10       ,' '), 3,' ')  --요구스케쥴건수1
		    	     
		    	       AS JMS_TC_MESSAGE    --JMSTCMessage
		    	  FROM (

		    	        SELECT MAX(DECODE(CNT, 1,YD_SCH_FLAG,''))  AS YD_SCH_FLAG1
		    	             , MAX(DECODE(CNT, 1,YD_SCH_CNT,''))   AS YD_SCH_CNT1
		    	             , MAX(DECODE(CNT, 2,YD_SCH_FLAG,''))  AS YD_SCH_FLAG2
		    	             , MAX(DECODE(CNT, 2,YD_SCH_CNT,''))   AS YD_SCH_CNT2
		    	             , MAX(DECODE(CNT, 3,YD_SCH_FLAG,''))  AS YD_SCH_FLAG3
		    	             , MAX(DECODE(CNT, 3,YD_SCH_CNT,''))   AS YD_SCH_CNT3
		    	             , MAX(DECODE(CNT, 4,YD_SCH_FLAG,''))  AS YD_SCH_FLAG4
		    	             , MAX(DECODE(CNT, 4,YD_SCH_CNT,''))   AS YD_SCH_CNT4
		    	             , MAX(DECODE(CNT, 5,YD_SCH_FLAG,''))  AS YD_SCH_FLAG5
		    	             , MAX(DECODE(CNT, 5,YD_SCH_CNT,''))   AS YD_SCH_CNT5
		    	             , MAX(DECODE(CNT, 6,YD_SCH_FLAG,''))  AS YD_SCH_FLAG6
		    	             , MAX(DECODE(CNT, 6,YD_SCH_CNT,''))   AS YD_SCH_CNT6
		    	             , MAX(DECODE(CNT, 7,YD_SCH_FLAG,''))  AS YD_SCH_FLAG7
		    	             , MAX(DECODE(CNT, 7,YD_SCH_CNT,''))   AS YD_SCH_CNT7
		    	             , MAX(DECODE(CNT, 8,YD_SCH_FLAG,''))  AS YD_SCH_FLAG8
		    	             , MAX(DECODE(CNT, 8,YD_SCH_CNT,''))   AS YD_SCH_CNT8
		    	             , MAX(DECODE(CNT, 9,YD_SCH_FLAG,''))  AS YD_SCH_FLAG9
		    	             , MAX(DECODE(CNT, 9,YD_SCH_CNT,''))   AS YD_SCH_CNT9
		    	             , MAX(DECODE(CNT,10,YD_SCH_FLAG,''))  AS YD_SCH_FLAG10
		    	             , MAX(DECODE(CNT,10,YD_SCH_CNT,''))   AS YD_SCH_CNT10
		    	          FROM     
		    	        (     
		    	        SELECT YD_SCH_FLAG 
		    	             , LPAD(TO_CHAR(COUNT(*)),'3',0) AS YD_SCH_CNT
		    	             , ROW_NUMBER() OVER(ORDER BY YD_SCH_FLAG) AS CNT
		    	          FROM (
		    	                SELECT (CASE WHEN A.YD_SCH_CD LIKE '3_KE0_LM' THEN 'A'--수입
		    	                             WHEN (B.CD_CONTENTS LIKE '%보급%' or B.CD_CONTENTS LIKE '%TakeIn%')  THEN 'B'--보급
		    	                             WHEN A.YD_SCH_CD LIKE '3_TC%'    THEN 'C'--대차
		    	                             WHEN (B.CD_CONTENTS LIKE '%이송입고%' OR B.CD_CONTENTS LIKE '%반입%') THEN 'D'--반입
		    	                             WHEN A.YD_SCH_CD LIKE '3_PT01UM' THEN 'E'-- 출하
		    	                             WHEN A.YD_SCH_CD LIKE '3_PT03UM' THEN 'F'--이송
		    	                             WHEN B.CD_CONTENTS LIKE '%입측TakeOut%' THEN 'G'--입측추출
		    	                             WHEN B.CD_CONTENTS LIKE '%출측TakeOut%' THEN 'H'--출측추출
		    	                             WHEN B.YD_SCH_CD LIKE '%3_PT08UM%' THEN 'J'--차량이적
		    	                         END) AS YD_SCH_FLAG,B.YD_WRK_CRN ,B.YD_ALT_CRN,A.YD_SCH_CD
		    	                FROM USRYMA.TB_YM_CRNSCH A
		    	                   , TB_YM_SCHEDULERULE B
		    	                   , TB_YM_EQUIP C
		    	                WHERE A.YD_SCH_CD = B.YD_SCH_CD
		    	                 AND B.YD_WRK_CRN = C.EQUIP_GP
		    	                 AND A.DEL_YN='N'
		    	                 AND (CASE C.WPROG_STAT WHEN 'B' THEN B.YD_ALT_CRN ELSE A.YD_EQP_ID END)=:V_YD_EQP_ID
		    	                ) A 
		    	         WHERE YD_SCH_FLAG IS NOT NULL
		    	         GROUP BY YD_SCH_FLAG
		    	        ) 
		    	)
		    	 */
		    	jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.YMA7L007";
		    			    	
			} else if("YMA8L007".equals(msgId)) { 
				 trtNm = "B열연 SALB 작업 현황 응답";
				    /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMA8L007 
				    SELECT 'YMA8L007'                          AS JMS_TC_CD --JMSTC코드
				          ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				          ,'YMA8L007'                                 --전문ID
				         ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')    --생성일시
				         ||'I'                                        --전문구분
				         ||'0046' --전문길이
				         ||RPAD(' ',29,' ')                           --임시
				         ||RPAD(NVL(:V_YD_EQP_ID       ,' '), 6,' ')  --야드설비ID
				         ||RPAD(NVL(YD_SCH_FLAG1       ,' '), 1,' ')  --요구스케쥴구분1
				         ||RPAD(NVL(YD_SCH_CNT1        ,' '), 3,' ')  --요구스케쥴건수1
				         ||RPAD(NVL(YD_SCH_FLAG2       ,' '), 1,' ')  --요구스케쥴구분1
				         ||RPAD(NVL(YD_SCH_CNT2        ,' '), 3,' ')  --요구스케쥴건수1
				         ||RPAD(NVL(YD_SCH_FLAG3       ,' '), 1,' ')  --요구스케쥴구분1
				         ||RPAD(NVL(YD_SCH_CNT3        ,' '), 3,' ')  --요구스케쥴건수1
				         ||RPAD(NVL(YD_SCH_FLAG4       ,' '), 1,' ')  --요구스케쥴구분1
				         ||RPAD(NVL(YD_SCH_CNT4        ,' '), 3,' ')  --요구스케쥴건수1
				         ||RPAD(NVL(YD_SCH_FLAG5       ,' '), 1,' ')  --요구스케쥴구분1
				         ||RPAD(NVL(YD_SCH_CNT5        ,' '), 3,' ')  --요구스케쥴건수1
				         ||RPAD(NVL(YD_SCH_FLAG6       ,' '), 1,' ')  --요구스케쥴구분1
				         ||RPAD(NVL(YD_SCH_CNT6        ,' '), 3,' ')  --요구스케쥴건수1
				         ||RPAD(NVL(YD_SCH_FLAG7       ,' '), 1,' ')  --요구스케쥴구분1
				         ||RPAD(NVL(YD_SCH_CNT7        ,' '), 3,' ')  --요구스케쥴건수1
				         ||RPAD(NVL(YD_SCH_FLAG8       ,' '), 1,' ')  --요구스케쥴구분1
				         ||RPAD(NVL(YD_SCH_CNT8        ,' '), 3,' ')  --요구스케쥴건수1
				         ||RPAD(NVL(YD_SCH_FLAG9       ,' '), 1,' ')  --요구스케쥴구분1
				         ||RPAD(NVL(YD_SCH_CNT9        ,' '), 3,' ')  --요구스케쥴건수1
				         ||RPAD(NVL(YD_SCH_FLAG10      ,' '), 1,' ')  --요구스케쥴구분1
				         ||RPAD(NVL(YD_SCH_CNT10       ,' '), 3,' ')  --요구스케쥴건수1
				         
				           AS JMS_TC_MESSAGE    --JMSTCMessage
				      FROM (

				            SELECT MAX(DECODE(CNT, 1,YD_SCH_FLAG,''))  AS YD_SCH_FLAG1
				                 , MAX(DECODE(CNT, 1,YD_SCH_CNT,''))   AS YD_SCH_CNT1
				                 , MAX(DECODE(CNT, 2,YD_SCH_FLAG,''))  AS YD_SCH_FLAG2
				                 , MAX(DECODE(CNT, 2,YD_SCH_CNT,''))   AS YD_SCH_CNT2
				                 , MAX(DECODE(CNT, 3,YD_SCH_FLAG,''))  AS YD_SCH_FLAG3
				                 , MAX(DECODE(CNT, 3,YD_SCH_CNT,''))   AS YD_SCH_CNT3
				                 , MAX(DECODE(CNT, 4,YD_SCH_FLAG,''))  AS YD_SCH_FLAG4
				                 , MAX(DECODE(CNT, 4,YD_SCH_CNT,''))   AS YD_SCH_CNT4
				                 , MAX(DECODE(CNT, 5,YD_SCH_FLAG,''))  AS YD_SCH_FLAG5
				                 , MAX(DECODE(CNT, 5,YD_SCH_CNT,''))   AS YD_SCH_CNT5
				                 , MAX(DECODE(CNT, 6,YD_SCH_FLAG,''))  AS YD_SCH_FLAG6
				                 , MAX(DECODE(CNT, 6,YD_SCH_CNT,''))   AS YD_SCH_CNT6
				                 , MAX(DECODE(CNT, 7,YD_SCH_FLAG,''))  AS YD_SCH_FLAG7
				                 , MAX(DECODE(CNT, 7,YD_SCH_CNT,''))   AS YD_SCH_CNT7
				                 , MAX(DECODE(CNT, 8,YD_SCH_FLAG,''))  AS YD_SCH_FLAG8
				                 , MAX(DECODE(CNT, 8,YD_SCH_CNT,''))   AS YD_SCH_CNT8
				                 , MAX(DECODE(CNT, 9,YD_SCH_FLAG,''))  AS YD_SCH_FLAG9
				                 , MAX(DECODE(CNT, 9,YD_SCH_CNT,''))   AS YD_SCH_CNT9
				                 , MAX(DECODE(CNT,10,YD_SCH_FLAG,''))  AS YD_SCH_FLAG10
				                 , MAX(DECODE(CNT,10,YD_SCH_CNT,''))   AS YD_SCH_CNT10
				              FROM     
				            (     
				            SELECT YD_SCH_FLAG 
				                 , LPAD(TO_CHAR(COUNT(*)),'3',0) AS YD_SCH_CNT
				                 , ROW_NUMBER() OVER(ORDER BY YD_SCH_FLAG) AS CNT
				              FROM (
				                    SELECT (CASE WHEN A.YD_SCH_CD LIKE '2_PT02UM'    THEN 'A'--이송상차       
				                                 WHEN A.YD_SCH_CD LIKE '2ESE01UM'    THEN 'B'--스카핑보급
				                                 
				                                 WHEN A.YD_SCH_CD LIKE '2_WB01UM'    THEN 'D'--W/B 보급                            
				                                 WHEN A.YD_SCH_CD LIKE '2_PT02_M'    THEN 'E'--이송하차       
				                                 WHEN A.YD_SCH_CD LIKE '2ESE01LM'    THEN 'F'--스카핑추출
				                                 
				                                 WHEN A.YD_SCH_CD LIKE '2_YD11MM'    THEN 'H'--동내이적(1)    
				                                 WHEN A.YD_SCH_CD LIKE '2_CT01UM'    THEN 'I'--CTC 보급       
				                                 -- J절단장 보급
				                                 WHEN A.YD_SCH_CD LIKE '2_HB01LM'    THEN 'L'--H/B LineOff    
				                                 --M 절단장 추출
				                                 --N 트레일러하차
				                                 --O ET CAR 하차
				                                 WHEN A.YD_SCH_CD LIKE '2_PT02_M'    THEN 'N'--이송상하차
				                                 WHEN SUBSTR(YD_UP_WO_LOC, 3, 3) = 'TC1' THEN 'C'--대차하차(1)    
				                                 WHEN SUBSTR(YD_DN_WO_LOC, 3, 3) = 'TC1' THEN 'C'--대차하차(1)    
				                                 WHEN SUBSTR(YD_UP_WO_LOC, 3, 3) = 'TC2' THEN 'G'--대차하차(2)    
				                                 WHEN SUBSTR(YD_DN_WO_LOC, 3, 3) = 'TC2' THEN 'G'--대차하차(2)    
				                                 WHEN SUBSTR(YD_UP_WO_LOC, 3, 3) = 'TC3' THEN 'K'--대차하차(3)    
				                                 WHEN SUBSTR(YD_DN_WO_LOC, 3, 3) = 'TC3' THEN 'K'--대차하차(4)    
				            --                     WHEN A.YD_SCH_CD LIKE '2_HB02UM'    THEN 'L'--STE 비상보급   
				                             END) AS YD_SCH_FLAG,B.YD_WRK_CRN ,B.YD_ALT_CRN,A.YD_SCH_CD
				                    FROM TB_YM_CRNSCH A
				                       , TB_YM_SCHEDULERULE B
				                       , TB_YM_EQUIP C
				                    WHERE A.YD_SCH_CD = B.YD_SCH_CD
				                     AND B.YD_WRK_CRN = C.EQUIP_GP
				                     AND A.DEL_YN='N'
				                     AND (CASE C.WPROG_STAT WHEN 'B' THEN B.YD_ALT_CRN ELSE A.YD_EQP_ID END)=:V_YD_EQP_ID
				                     AND A.YD_GP = '2'
				                     AND '1' = CASE WHEN (SELECT COUNT(*)
				                                  FROM TB_YM_CRNSCH A1
				                                     , TB_YM_CRNWRKMTL B1
				                                     , TB_YM_STACKLAYER C1
				                                 WHERE A1.YD_CRN_SCH_ID  = B1.YD_CRN_SCH_ID
				                                   AND B1.STOCK_ID       = C1.STOCK_ID
				                                   AND C1.STACK_COL_GP||C1.STACK_BED_GP = A1.YD_UP_WO_LOC
				                                   AND A1.DEL_YN = 'N'
				                                   AND B1.DEL_YN = 'N'
				                                   AND C1.STACK_LAYER_GP >=
				                                       (
				                                        SELECT NVL(MAX(C2.STACK_LAYER_GP) ,'01')
				                                          FROM TB_YM_CRNSCH A2
				                                             , TB_YM_CRNWRKMTL B2
				                                             , TB_YM_STACKLAYER C2
				                                         WHERE A2.YD_CRN_SCH_ID  = B2.YD_CRN_SCH_ID
				                                           AND B2.STOCK_ID       = C2.STOCK_ID
				                                           AND C2.STACK_COL_GP||C2.STACK_BED_GP = A.YD_UP_WO_LOC
				                                       ) 
				                                   AND A1.YD_DN_WO_LOC = 'XX010101'
				                                ) > 0 
				                          THEN '2'           
				                          ELSE '1' END   
				                    ) A 
				             WHERE YD_SCH_FLAG IS NOT NULL
				             GROUP BY YD_SCH_FLAG
				            ) 
				    )
				*/    
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMA8L007";
				
		    } else if("YMA7L008".equals(msgId)) {
				
		    	trtNm = "B열연 COIL 차량예정정보";
		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.YMA7L008 
		    	-- 차량작업예정정보
		    	SELECT JMS_TC_CD  --JMSTC코드
		    	      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT  --JMSTC생성일시
		    	      ,JMS_TC_CD                                                  --전문ID
		    	     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')                    --생성일시
		    	     ||'I'                                                        --전문구분
		    	     ||'0825'                                                     --전문길이
		    	     ||RPAD(' ',29,' ')                                           --임시
		    	     ||RPAD(NVL(A.PT_LOAD_LOC                        ,' '), 6,' ') --상차도위치
		    	     ||RPAD(NVL(A.CAR_NO                             ,' '),15,' ') --차량번호
		    	     ||RPAD(NVL(A.CARD_NO                            ,' '), 4,' ') --차량번호
		    	     ||RPAD(NVL(A.PT_CLS                             ,' '), 2,' ') --차량구분
		    	     ||RPAD(NVL(A.WORK_CLS                           ,' '), 1,' ') --자업구분
		    	     ||LPAD(NVL(A.WORK_COIL_MAX_CNT                  ,'0'), 2,'0') --야드적치Bed번호
		    	      
		    	     ||RPAD(NVL(A.STOCK_ID_0                         ,' '),11,' ') --재료번호_0
		    	     ||RPAD(NVL(A.LOAD_LOC_CD_0                      ,' '), 2,' ') --차량적재위치_0
		    	     ||LPAD(NVL(A.COIL_WT_0                          ,'0'), 5,'0') --재료중량_0
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_T_0,'FM000V000')      ,'0'), 6,'0') --재료두께_0
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_W_0,'FM0000V0')       ,'0'), 5,'0') --재료폭_0
		    	     ||LPAD(NVL(A.COIL_LEN_0                         ,'0'), 7,'0') --재료길이_0
		    	     ||LPAD(NVL(A.COIL_OUTDIA_0                      ,'0'), 5,'0') --재료외경_0
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_INDIA_0,'FM0000V0')   ,'0'), 5,'0') --재료내경_0
		    	     ||RPAD(NVL(A.WORK_STATE_0                       ,' '), 1,' ') --작업상태_0
		    	     ||RPAD(NVL(A.YD_CURR_BAY_GP_0                   ,' '), 6,' ') --동정보_0

		    	     ||RPAD(NVL(A.STOCK_ID_1                         ,' '),11,' ') --재료번호_1
		    	     ||RPAD(NVL(A.LOAD_LOC_CD_1                      ,' '), 2,' ') --차량적재위치_1
		    	     ||LPAD(NVL(A.COIL_WT_1                          ,'0'), 5,'0') --재료중량_1
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_T_1,'FM000V000')      ,'0'), 6,'0') --재료두께_1
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_W_1,'FM0000V0')       ,'0'), 5,'0') --재료폭_1
		    	     ||LPAD(NVL(A.COIL_LEN_1                         ,'0'), 7,'0') --재료길이_1
		    	     ||LPAD(NVL(A.COIL_OUTDIA_1                      ,'0'), 5,'0') --재료외경_1
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_INDIA_1,'FM0000V0')   ,'0'), 5,'0') --재료내경_1
		    	     ||RPAD(NVL(A.WORK_STATE_1                       ,' '), 1,' ') --작업상태_1
		    	     ||RPAD(NVL(A.YD_CURR_BAY_GP_1                   ,' '), 6,' ') --동정보_1
		    	 
		    	     ||RPAD(NVL(A.STOCK_ID_2                         ,' '),11,' ') --재료번호_2
		    	     ||RPAD(NVL(A.LOAD_LOC_CD_2                      ,' '), 2,' ') --차량적재위치_2
		    	     ||LPAD(NVL(A.COIL_WT_2                          ,'0'), 5,'0') --재료중량_2
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_T_2,'FM000V000')      ,'0'), 6,'0') --재료두께_2
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_W_2,'FM0000V0')       ,'0'), 5,'0') --재료폭_2
		    	     ||LPAD(NVL(A.COIL_LEN_2                         ,'0'), 7,'0') --재료길이_2
		    	     ||LPAD(NVL(A.COIL_OUTDIA_2                      ,'0'), 5,'0') --재료외경_2
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_INDIA_2,'FM0000V0')   ,'0'), 5,'0') --재료내경_2
		    	     ||RPAD(NVL(A.WORK_STATE_2                       ,' '), 1,' ') --작업상태_2
		    	     ||RPAD(NVL(A.YD_CURR_BAY_GP_2                   ,' '), 6,' ') --동정보_2
		    	 
		    	     ||RPAD(NVL(A.STOCK_ID_3                         ,' '),11,' ') --재료번호_3
		    	     ||RPAD(NVL(A.LOAD_LOC_CD_3                      ,' '), 2,' ') --차량적재위치_3
		    	     ||LPAD(NVL(A.COIL_WT_3                          ,'0'), 5,'0') --재료중량_3
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_T_3,'FM000V000')      ,'0'), 6,'0') --재료두께_3
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_W_3,'FM0000V0')       ,'0'), 5,'0') --재료폭_3
		    	     ||LPAD(NVL(A.COIL_LEN_3                         ,'0'), 7,'0') --재료길이_3
		    	     ||LPAD(NVL(A.COIL_OUTDIA_3                      ,'0'), 5,'0') --재료외경_3
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_INDIA_3,'FM0000V0')   ,'0'), 5,'0') --재료내경_3
		    	     ||RPAD(NVL(A.WORK_STATE_3                       ,' '), 1,' ') --작업상태_3
		    	     ||RPAD(NVL(A.YD_CURR_BAY_GP_3                   ,' '), 6,' ') --동정보_3
		    	 
		    	     ||RPAD(NVL(A.STOCK_ID_4                         ,' '),11,' ') --재료번호_4
		    	     ||RPAD(NVL(A.LOAD_LOC_CD_4                      ,' '), 2,' ') --차량적재위치_4
		    	     ||LPAD(NVL(A.COIL_WT_4                          ,'0'), 5,'0') --재료중량_4
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_T_4,'FM000V000')      ,'0'), 6,'0') --재료두께_4
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_W_4,'FM0000V0')       ,'0'), 5,'0') --재료폭_4
		    	     ||LPAD(NVL(A.COIL_LEN_4                         ,'0'), 7,'0') --재료길이_4
		    	     ||LPAD(NVL(A.COIL_OUTDIA_4                      ,'0'), 5,'0') --재료외경_4
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_INDIA_4,'FM0000V0')   ,'0'), 5,'0') --재료내경_4
		    	     ||RPAD(NVL(A.WORK_STATE_4                       ,' '), 1,' ') --작업상태_4
		    	     ||RPAD(NVL(A.YD_CURR_BAY_GP_4                   ,' '), 6,' ') --동정보_4
		    	 
		    	     ||RPAD(NVL(A.STOCK_ID_5                         ,' '),11,' ') --재료번호_5
		    	     ||RPAD(NVL(A.LOAD_LOC_CD_5                      ,' '), 2,' ') --차량적재위치_5
		    	     ||LPAD(NVL(A.COIL_WT_5                          ,'0'), 5,'0') --재료중량_5
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_T_5,'FM000V000')      ,'0'), 6,'0') --재료두께_5
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_W_5,'FM0000V0')       ,'0'), 5,'0') --재료폭_5
		    	     ||LPAD(NVL(A.COIL_LEN_5                         ,'0'), 7,'0') --재료길이_5
		    	     ||LPAD(NVL(A.COIL_OUTDIA_5                      ,'0'), 5,'0') --재료외경_5
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_INDIA_5,'FM0000V0')   ,'0'), 5,'0') --재료내경_5
		    	     ||RPAD(NVL(A.WORK_STATE_5                       ,' '), 1,' ') --작업상태_5
		    	     ||RPAD(NVL(A.YD_CURR_BAY_GP_5                   ,' '), 6,' ') --동정보_5
		    	 
		    	     ||RPAD(NVL(A.STOCK_ID_6                         ,' '),11,' ') --재료번호_6
		    	     ||RPAD(NVL(A.LOAD_LOC_CD_6                      ,' '), 2,' ') --차량적재위치_6
		    	     ||LPAD(NVL(A.COIL_WT_6                          ,'0'), 5,'0') --재료중량_6
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_T_6,'FM000V000')      ,'0'), 6,'0') --재료두께_6
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_W_6,'FM0000V0')       ,'0'), 5,'0') --재료폭_6
		    	     ||LPAD(NVL(A.COIL_LEN_6                         ,'0'), 7,'0') --재료길이_6
		    	     ||LPAD(NVL(A.COIL_OUTDIA_6                      ,'0'), 5,'0') --재료외경_6
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_INDIA_6,'FM0000V0')   ,'0'), 5,'0') --재료내경_6
		    	     ||RPAD(NVL(A.WORK_STATE_6                       ,' '), 1,' ') --작업상태_6
		    	     ||RPAD(NVL(A.YD_CURR_BAY_GP_6                   ,' '), 6,' ') --동정보_6
		    	 
		    	     ||RPAD(NVL(A.STOCK_ID_7                         ,' '),11,' ') --재료번호_7
		    	     ||RPAD(NVL(A.LOAD_LOC_CD_7                      ,' '), 2,' ') --차량적재위치_7
		    	     ||LPAD(NVL(A.COIL_WT_7                          ,'0'), 5,'0') --재료중량_7
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_T_7,'FM000V000')      ,'0'), 6,'0') --재료두께_7
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_W_7,'FM0000V0')       ,'0'), 5,'0') --재료폭_7
		    	     ||LPAD(NVL(A.COIL_LEN_7                         ,'0'), 7,'0') --재료길이_7
		    	     ||LPAD(NVL(A.COIL_OUTDIA_7                      ,'0'), 5,'0') --재료외경_7
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_INDIA_7,'FM0000V0')   ,'0'), 5,'0') --재료내경_7
		    	     ||RPAD(NVL(A.WORK_STATE_7                       ,' '), 1,' ') --작업상태_7
		    	     ||RPAD(NVL(A.YD_CURR_BAY_GP_7                   ,' '), 6,' ') --동정보_7
		    	 
		    	     ||RPAD(NVL(A.STOCK_ID_8                         ,' '),11,' ') --재료번호_8
		    	     ||RPAD(NVL(A.LOAD_LOC_CD_8                      ,' '), 2,' ') --차량적재위치_8
		    	     ||LPAD(NVL(A.COIL_WT_8                          ,'0'), 5,'0') --재료중량_8
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_T_8,'FM000V000')      ,'0'), 6,'0') --재료두께_8
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_W_8,'FM0000V0')       ,'0'), 5,'0') --재료폭_8
		    	     ||LPAD(NVL(A.COIL_LEN_8                         ,'0'), 7,'0') --재료길이_8
		    	     ||LPAD(NVL(A.COIL_OUTDIA_8                      ,'0'), 5,'0') --재료외경_8
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_INDIA_8,'FM0000V0')   ,'0'), 5,'0') --재료내경_8
		    	     ||RPAD(NVL(A.WORK_STATE_8                       ,' '), 1,' ') --작업상태_8
		    	     ||RPAD(NVL(A.YD_CURR_BAY_GP_8                   ,' '), 6,' ') --동정보_8
		    	 
		    	     ||RPAD(NVL(A.STOCK_ID_9                         ,' '),11,' ') --재료번호_9
		    	     ||RPAD(NVL(A.LOAD_LOC_CD_9                      ,' '), 2,' ') --차량적재위치_9
		    	     ||LPAD(NVL(A.COIL_WT_9                          ,'0'), 5,'0') --재료중량_9
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_T_9,'FM000V000')      ,'0'), 6,'0') --재료두께_9
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_W_9,'FM0000V0')       ,'0'), 5,'0') --재료폭_9
		    	     ||LPAD(NVL(A.COIL_LEN_9                         ,'0'), 7,'0') --재료길이_9
		    	     ||LPAD(NVL(A.COIL_OUTDIA_9                      ,'0'), 5,'0') --재료외경_9
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_INDIA_9,'FM0000V0')   ,'0'), 5,'0') --재료내경_9
		    	     ||RPAD(NVL(A.WORK_STATE_9                       ,' '), 1,' ') --작업상태_9
		    	     ||RPAD(NVL(A.YD_CURR_BAY_GP_9                   ,' '), 6,' ') --동정보_9
		    	 
		    	     ||RPAD(NVL(A.STOCK_ID_10                        ,' '),11,' ') --재료번호_10
		    	     ||RPAD(NVL(A.LOAD_LOC_CD_10                     ,' '), 2,' ') --차량적재위치_10
		    	     ||LPAD(NVL(A.COIL_WT_10                         ,'0'), 5,'0') --재료중량_10
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_T_10,'FM000V000')     ,'0'), 6,'0') --재료두께_10
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_W_10,'FM0000V0')      ,'0'), 5,'0') --재료폭_10
		    	     ||LPAD(NVL(A.COIL_LEN_10                        ,'0'), 7,'0') --재료길이_10
		    	     ||LPAD(NVL(A.COIL_OUTDIA_10                     ,'0'), 5,'0') --재료외경_10
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_INDIA_10,'FM0000V0')  ,'0'), 5,'0') --재료내경_10
		    	     ||RPAD(NVL(A.WORK_STATE_10                      ,' '), 1,' ') --작업상태_10
		    	     ||RPAD(NVL(A.YD_CURR_BAY_GP_10                  ,' '), 6,' ') --동정보_10
		    	 
		    	     ||RPAD(NVL(A.STOCK_ID_11                        ,' '),11,' ') --재료번호_11
		    	     ||RPAD(NVL(A.LOAD_LOC_CD_11                     ,' '), 2,' ') --차량적재위치_11
		    	     ||LPAD(NVL(A.COIL_WT_11                         ,'0'), 5,'0') --재료중량_11
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_T_11,'FM000V000')     ,'0'), 6,'0') --재료두께_11
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_W_11,'FM0000V0')      ,'0'), 5,'0') --재료폭_11
		    	     ||LPAD(NVL(A.COIL_LEN_11                        ,'0'), 7,'0') --재료길이_11
		    	     ||LPAD(NVL(A.COIL_OUTDIA_11                     ,'0'), 5,'0') --재료외경_11
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_INDIA_11,'FM0000V0')  ,'0'), 5,'0') --재료내경_11
		    	     ||RPAD(NVL(A.WORK_STATE_11                      ,' '), 1,' ') --작업상태_11
		    	     ||RPAD(NVL(A.YD_CURR_BAY_GP_11                  ,' '), 6,' ') --동정보_11

		    	     ||RPAD(NVL(A.STOCK_ID_12                        ,' '),11,' ') --재료번호_12
		    	     ||RPAD(NVL(A.LOAD_LOC_CD_12                     ,' '), 2,' ') --차량적재위치_12
		    	     ||LPAD(NVL(A.COIL_WT_12                         ,'0'), 5,'0') --재료중량_12
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_T_12,'FM000V000')     ,'0'), 6,'0') --재료두께_12
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_W_12,'FM0000V0')      ,'0'), 5,'0') --재료폭_12
		    	     ||LPAD(NVL(A.COIL_LEN_12                        ,'0'), 7,'0') --재료길이_12
		    	     ||LPAD(NVL(A.COIL_OUTDIA_12                     ,'0'), 5,'0') --재료외경_12
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_INDIA_12,'FM0000V0')  ,'0'), 5,'0') --재료내경_12
		    	     ||RPAD(NVL(A.WORK_STATE_12                      ,' '), 1,' ') --작업상태_12
		    	     ||RPAD(NVL(A.YD_CURR_BAY_GP_12                  ,' '), 6,' ') --동정보_12
		    	     
		    	     ||RPAD(NVL(A.STOCK_ID_13                        ,' '),11,' ') --재료번호_13
		    	     ||RPAD(NVL(A.LOAD_LOC_CD_13                     ,' '), 2,' ') --차량적재위치_13
		    	     ||LPAD(NVL(A.COIL_WT_13                         ,'0'), 5,'0') --재료중량_13
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_T_13,'FM000V000')     ,'0'), 6,'0') --재료두께_13
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_W_13,'FM0000V0')      ,'0'), 5,'0') --재료폭_13
		    	     ||LPAD(NVL(A.COIL_LEN_13                        ,'0'), 7,'0') --재료길이_13
		    	     ||LPAD(NVL(A.COIL_OUTDIA_13                     ,'0'), 5,'0') --재료외경_13
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_INDIA_13,'FM0000V0')  ,'0'), 5,'0') --재료내경_13
		    	     ||RPAD(NVL(A.WORK_STATE_13                      ,' '), 1,' ') --작업상태_13
		    	     ||RPAD(NVL(A.YD_CURR_BAY_GP_13                  ,' '), 6,' ') --동정보_13
		    	     
		    	     ||RPAD(NVL(A.STOCK_ID_14                        ,' '),11,' ') --재료번호_14
		    	     ||RPAD(NVL(A.LOAD_LOC_CD_14                     ,' '), 2,' ') --차량적재위치_14
		    	     ||LPAD(NVL(A.COIL_WT_14                         ,'0'), 5,'0') --재료중량_14
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_T_14,'FM000V000')     ,'0'), 6,'0') --재료두께_14
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_W_14,'FM0000V0')      ,'0'), 5,'0') --재료폭_14
		    	     ||LPAD(NVL(A.COIL_LEN_14                        ,'0'), 7,'0') --재료길이_14
		    	     ||LPAD(NVL(A.COIL_OUTDIA_14                     ,'0'), 5,'0') --재료외경_14
		    	     ||LPAD(NVL(TO_CHAR(A.COIL_INDIA_14,'FM0000V0')  ,'0'), 5,'0') --재료내경_14
		    	     ||RPAD(NVL(A.WORK_STATE_14                      ,' '), 1,' ') --작업상태_14
		    	     ||RPAD(NVL(A.YD_CURR_BAY_GP_14                  ,' '), 6,' ') --동정보_14
		    	     
		    	       AS JMS_TC_MESSAGE --JMSTCMessage
		    	      
		    	  FROM (
		    	  
		    	        SELECT 'YMA7L008'           AS JMS_TC_CD
		    	              ,:V_PT_LOAD_LOC       AS PT_LOAD_LOC
		    	              ,:V_CAR_NO            AS CAR_NO
		    	              ,:V_CARD_NO           AS CARD_NO
		    	              ,:V_PT_CLS            AS PT_CLS
		    	              ,:V_WORK_CLS          AS WORK_CLS
		    	              ,:V_WORK_COIL_MAX_CNT AS WORK_COIL_MAX_CNT
		    	              
		    	              ,SUBSTR(STOCK_INFO_0  ,1 ,11)            AS STOCK_ID_0
		    	              ,SUBSTR(STOCK_INFO_0  ,12, 2)            AS LOAD_LOC_CD_0
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_0  ,14,10)) AS COIL_WT_0
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_0  ,24,10)) AS COIL_T_0
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_0  ,34,10)) AS COIL_W_0
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_0  ,44,10)) AS COIL_LEN_0
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_0  ,54,10)) AS COIL_OUTDIA_0
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_0  ,64,10)) AS COIL_INDIA_0
		    	              ,SUBSTR(STOCK_INFO_0  ,74, 1)            AS WORK_STATE_0
		    	              ,SUBSTR(STOCK_INFO_0  ,75, 6)            AS YD_CURR_BAY_GP_0
		    	              
		    	              ,SUBSTR(STOCK_INFO_1  ,1 ,11)            AS STOCK_ID_1
		    	              ,SUBSTR(STOCK_INFO_1  ,12, 2)            AS LOAD_LOC_CD_1
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_1  ,14,10)) AS COIL_WT_1
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_1  ,24,10)) AS COIL_T_1
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_1  ,34,10)) AS COIL_W_1
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_1  ,44,10)) AS COIL_LEN_1
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_1  ,54,10)) AS COIL_OUTDIA_1
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_1  ,64,10)) AS COIL_INDIA_1
		    	              ,SUBSTR(STOCK_INFO_1  ,74, 1)            AS WORK_STATE_1
		    	              ,SUBSTR(STOCK_INFO_1  ,75, 6)            AS YD_CURR_BAY_GP_1

		    	              ,SUBSTR(STOCK_INFO_2  ,1 ,11)            AS STOCK_ID_2
		    	              ,SUBSTR(STOCK_INFO_2  ,12, 2)            AS LOAD_LOC_CD_2
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_2  ,14,10)) AS COIL_WT_2
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_2  ,24,10)) AS COIL_T_2
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_2  ,34,10)) AS COIL_W_2
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_2  ,44,10)) AS COIL_LEN_2
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_2  ,54,10)) AS COIL_OUTDIA_2
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_2  ,64,10)) AS COIL_INDIA_2
		    	              ,SUBSTR(STOCK_INFO_2  ,74, 1)            AS WORK_STATE_2
		    	              ,SUBSTR(STOCK_INFO_2  ,75, 6)            AS YD_CURR_BAY_GP_2

		    	              ,SUBSTR(STOCK_INFO_3  ,1 ,11)            AS STOCK_ID_3
		    	              ,SUBSTR(STOCK_INFO_3  ,12, 2)            AS LOAD_LOC_CD_3
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_3  ,14,10)) AS COIL_WT_3
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_3  ,24,10)) AS COIL_T_3
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_3  ,34,10)) AS COIL_W_3
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_3  ,44,10)) AS COIL_LEN_3
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_3  ,54,10)) AS COIL_OUTDIA_3
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_3  ,64,10)) AS COIL_INDIA_3
		    	              ,SUBSTR(STOCK_INFO_3  ,74, 1)            AS WORK_STATE_3
		    	              ,SUBSTR(STOCK_INFO_3  ,75, 6)            AS YD_CURR_BAY_GP_3

		    	              ,SUBSTR(STOCK_INFO_4  ,1 ,11)            AS STOCK_ID_4
		    	              ,SUBSTR(STOCK_INFO_4  ,12, 2)            AS LOAD_LOC_CD_4
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_4  ,14,10)) AS COIL_WT_4
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_4  ,24,10)) AS COIL_T_4
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_4  ,34,10)) AS COIL_W_4
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_4  ,44,10)) AS COIL_LEN_4
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_4  ,54,10)) AS COIL_OUTDIA_4
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_4  ,64,10)) AS COIL_INDIA_4
		    	              ,SUBSTR(STOCK_INFO_4  ,74, 1)            AS WORK_STATE_4
		    	              ,SUBSTR(STOCK_INFO_4  ,75, 6)            AS YD_CURR_BAY_GP_4

		    	              ,SUBSTR(STOCK_INFO_5  ,1 ,11)            AS STOCK_ID_5
		    	              ,SUBSTR(STOCK_INFO_5  ,12, 2)            AS LOAD_LOC_CD_5
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_5  ,14,10)) AS COIL_WT_5
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_5  ,24,10)) AS COIL_T_5
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_5  ,34,10)) AS COIL_W_5
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_5  ,44,10)) AS COIL_LEN_5
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_5  ,54,10)) AS COIL_OUTDIA_5
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_5  ,64,10)) AS COIL_INDIA_5
		    	              ,SUBSTR(STOCK_INFO_5  ,74, 1)            AS WORK_STATE_5
		    	              ,SUBSTR(STOCK_INFO_5  ,75, 6)            AS YD_CURR_BAY_GP_5

		    	              ,SUBSTR(STOCK_INFO_6  ,1 ,11)            AS STOCK_ID_6
		    	              ,SUBSTR(STOCK_INFO_6  ,12, 2)            AS LOAD_LOC_CD_6
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_6  ,14,10)) AS COIL_WT_6
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_6  ,24,10)) AS COIL_T_6
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_6  ,34,10)) AS COIL_W_6
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_6  ,44,10)) AS COIL_LEN_6
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_6  ,54,10)) AS COIL_OUTDIA_6
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_6  ,64,10)) AS COIL_INDIA_6
		    	              ,SUBSTR(STOCK_INFO_6  ,74, 1)            AS WORK_STATE_6
		    	              ,SUBSTR(STOCK_INFO_6  ,75, 6)            AS YD_CURR_BAY_GP_6

		    	              ,SUBSTR(STOCK_INFO_7  ,1 ,11)            AS STOCK_ID_7
		    	              ,SUBSTR(STOCK_INFO_7  ,12, 2)            AS LOAD_LOC_CD_7
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_7  ,14,10)) AS COIL_WT_7
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_7  ,24,10)) AS COIL_T_7
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_7  ,34,10)) AS COIL_W_7
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_7  ,44,10)) AS COIL_LEN_7
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_7  ,54,10)) AS COIL_OUTDIA_7
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_7  ,64,10)) AS COIL_INDIA_7
		    	              ,SUBSTR(STOCK_INFO_7  ,74, 1)            AS WORK_STATE_7
		    	              ,SUBSTR(STOCK_INFO_7  ,75, 6)            AS YD_CURR_BAY_GP_7

		    	              ,SUBSTR(STOCK_INFO_8  ,1 ,11)            AS STOCK_ID_8
		    	              ,SUBSTR(STOCK_INFO_8  ,12, 2)            AS LOAD_LOC_CD_8
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_8  ,14,10)) AS COIL_WT_8
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_8  ,24,10)) AS COIL_T_8
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_8  ,34,10)) AS COIL_W_8
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_8  ,44,10)) AS COIL_LEN_8
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_8  ,54,10)) AS COIL_OUTDIA_8
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_8  ,64,10)) AS COIL_INDIA_8
		    	              ,SUBSTR(STOCK_INFO_8  ,74, 1)            AS WORK_STATE_8
		    	              ,SUBSTR(STOCK_INFO_8  ,75, 6)            AS YD_CURR_BAY_GP_8

		    	              ,SUBSTR(STOCK_INFO_9  ,1 ,11)            AS STOCK_ID_9
		    	              ,SUBSTR(STOCK_INFO_9  ,12, 2)            AS LOAD_LOC_CD_9
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_9  ,14,10)) AS COIL_WT_9
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_9  ,24,10)) AS COIL_T_9
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_9  ,34,10)) AS COIL_W_9
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_9  ,44,10)) AS COIL_LEN_9
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_9  ,54,10)) AS COIL_OUTDIA_9
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_9  ,64,10)) AS COIL_INDIA_9
		    	              ,SUBSTR(STOCK_INFO_9  ,74, 1)            AS WORK_STATE_9
		    	              ,SUBSTR(STOCK_INFO_9  ,75, 6)            AS YD_CURR_BAY_GP_9

		    	              ,SUBSTR(STOCK_INFO_10  ,1 ,11)           AS STOCK_ID_10
		    	              ,SUBSTR(STOCK_INFO_10  ,12, 2)           AS LOAD_LOC_CD_10
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_10 ,14,10)) AS COIL_WT_10
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_10 ,24,10)) AS COIL_T_10
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_10 ,34,10)) AS COIL_W_10
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_10 ,44,10)) AS COIL_LEN_10
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_10 ,54,10)) AS COIL_OUTDIA_10
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_10 ,64,10)) AS COIL_INDIA_10
		    	              ,SUBSTR(STOCK_INFO_10  ,74, 1)           AS WORK_STATE_10
		    	              ,SUBSTR(STOCK_INFO_10  ,75, 6)           AS YD_CURR_BAY_GP_10

		    	              ,SUBSTR(STOCK_INFO_11  ,1 ,11)           AS STOCK_ID_11
		    	              ,SUBSTR(STOCK_INFO_11  ,12, 2)           AS LOAD_LOC_CD_11
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_11 ,14,10)) AS COIL_WT_11
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_11 ,24,10)) AS COIL_T_11
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_11 ,34,10)) AS COIL_W_11
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_11 ,44,10)) AS COIL_LEN_11
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_11 ,54,10)) AS COIL_OUTDIA_11
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_11 ,64,10)) AS COIL_INDIA_11
		    	              ,SUBSTR(STOCK_INFO_11  ,74, 1)           AS WORK_STATE_11
		    	              ,SUBSTR(STOCK_INFO_11  ,75, 6)           AS YD_CURR_BAY_GP_11

		    	              ,SUBSTR(STOCK_INFO_12  ,1 ,11)           AS STOCK_ID_12
		    	              ,SUBSTR(STOCK_INFO_12  ,12, 2)           AS LOAD_LOC_CD_12
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_12 ,14,10)) AS COIL_WT_12
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_12 ,24,10)) AS COIL_T_12
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_12 ,34,10)) AS COIL_W_12
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_12 ,44,10)) AS COIL_LEN_12
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_12 ,54,10)) AS COIL_OUTDIA_12
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_12 ,64,10)) AS COIL_INDIA_12
		    	              ,SUBSTR(STOCK_INFO_12  ,74, 1)           AS WORK_STATE_12
		    	              ,SUBSTR(STOCK_INFO_12  ,75, 6)           AS YD_CURR_BAY_GP_12

		    	              ,SUBSTR(STOCK_INFO_13  ,1 ,11)           AS STOCK_ID_13
		    	              ,SUBSTR(STOCK_INFO_13  ,12, 2)           AS LOAD_LOC_CD_13
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_13 ,14,10)) AS COIL_WT_13
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_13 ,24,10)) AS COIL_T_13
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_13 ,34,10)) AS COIL_W_13
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_13 ,44,10)) AS COIL_LEN_13
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_13 ,54,10)) AS COIL_OUTDIA_13
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_13 ,64,10)) AS COIL_INDIA_13
		    	              ,SUBSTR(STOCK_INFO_13  ,74, 1)           AS WORK_STATE_13
		    	              ,SUBSTR(STOCK_INFO_13  ,75, 6)           AS YD_CURR_BAY_GP_13

		    	              ,SUBSTR(STOCK_INFO_14  ,1 ,11)           AS STOCK_ID_14
		    	              ,SUBSTR(STOCK_INFO_14  ,12, 2)           AS LOAD_LOC_CD_14
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_14 ,14,10)) AS COIL_WT_14
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_14 ,24,10)) AS COIL_T_14
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_14 ,34,10)) AS COIL_W_14
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_14 ,44,10)) AS COIL_LEN_14
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_14 ,54,10)) AS COIL_OUTDIA_14
		    	              ,TO_NUMBER(SUBSTR(STOCK_INFO_14 ,64,10)) AS COIL_INDIA_14
		    	              ,SUBSTR(STOCK_INFO_14  ,74, 1)           AS WORK_STATE_14
		    	              ,SUBSTR(STOCK_INFO_14  ,75, 6)           AS YD_CURR_BAY_GP_14

		    	              
		    	        FROM  (SELECT :V_STOCK_INFO_0   AS STOCK_INFO_0 
		    	                    , :V_STOCK_INFO_1   AS STOCK_INFO_1 
		    	                    , :V_STOCK_INFO_2   AS STOCK_INFO_2 
		    	                    , :V_STOCK_INFO_3   AS STOCK_INFO_3 
		    	                    , :V_STOCK_INFO_4   AS STOCK_INFO_4 
		    	                    , :V_STOCK_INFO_5   AS STOCK_INFO_5 
		    	                    , :V_STOCK_INFO_6   AS STOCK_INFO_6 
		    	                    , :V_STOCK_INFO_7   AS STOCK_INFO_7 
		    	                    , :V_STOCK_INFO_8   AS STOCK_INFO_8 
		    	                    , :V_STOCK_INFO_9   AS STOCK_INFO_9 
		    	                    , :V_STOCK_INFO_10  AS STOCK_INFO_10
		    	                    , :V_STOCK_INFO_11  AS STOCK_INFO_11
		    	                    , :V_STOCK_INFO_12  AS STOCK_INFO_12
		    	                    , :V_STOCK_INFO_13  AS STOCK_INFO_13
		    	                    , :V_STOCK_INFO_14  AS STOCK_INFO_14
		    	                 FROM DUAL)   
		    	       ) A
		    	       */
		    	
		    	
		    	jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.YMA7L008";

			} else if("YMA7L008BackUp".equals(msgId)) {
				/* 차량작업예정정보BackUp - com.inisteel.cim.ym.bcommon.dao.YmCommDAO.YMA7L008BackUp 
				SELECT JMS_TC_CD  --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT  --JMSTC생성일시
				      ,JMS_TC_CD                                                  --전문ID
				     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')                    --생성일시
				     ||'I'                                                        --전문구분
				     ||'0825'                                                     --전문길이
				     ||RPAD(' ',29,' ')                                           --임시
				     ||RPAD(NVL(A.PT_LOAD_LOC                       ,' '), 6,' ') --상차도위치
				     ||RPAD(NVL(A.CAR_NO                            ,' '),15,' ') --차량번호
				     ||RPAD(NVL(A.CARD_NO                           ,' '), 4,' ') --카드번호
				     ||RPAD(NVL(A.PT_CLS                            ,' '), 2,' ') --차량구분
				     ||RPAD(NVL(A.WORK_CLS                          ,' '), 1,' ') --자업구분
				     ||LPAD(NVL(A.WORK_COIL_MAX_CNT                 ,'0'), 2,'0') --야드적치Bed번호
				     
				     ||RPAD(NVL(A.STOCK_ID_0                        ,' '),11,' ') --재료번호_0
				     ||RPAD(NVL(A.LOAD_LOC_CD_0                     ,' '), 2,' ') --차량적재위치_0
				     ||LPAD(NVL(C_0.COIL_WT                         ,'0'), 5,'0') --재료중량_0
				     ||LPAD(NVL(TO_CHAR(C_0.COIL_T,'FM000V000')     ,'0'), 6,'0') --재료두께_0
				     ||LPAD(NVL(TO_CHAR(C_0.COIL_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_0
				     ||LPAD(NVL(C_0.COIL_LEN                        ,'0'), 7,'0') --재료길이_0
				     ||LPAD(NVL(C_0.COIL_OUTDIA                     ,'0'), 5,'0') --재료외경_0
				     ||LPAD(NVL(TO_CHAR(C_0.COIL_INDIA,'FM0000V0')  ,'0'), 5,'0') --재료내경_0
				     ||RPAD(NVL(A.WORK_STATE_0                      ,' '), 1,' ') --작업상태_0
				     ||RPAD(DECODE(A.STOCK_ID_0,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_0
				     
				     ||RPAD(NVL(A.STOCK_ID_1                        ,' '),11,' ') --재료번호_1
				     ||RPAD(NVL(A.LOAD_LOC_CD_1                     ,' '), 2,' ') --차량적재위치_1
				     ||LPAD(NVL(C_1.COIL_WT                         ,'0'), 5,'0') --재료중량_1
				     ||LPAD(NVL(TO_CHAR(C_1.COIL_T,'FM000V000')     ,'0'), 6,'0') --재료두께_1
				     ||LPAD(NVL(TO_CHAR(C_1.COIL_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_1
				     ||LPAD(NVL(C_1.COIL_LEN                        ,'0'), 7,'0') --재료길이_1
				     ||LPAD(NVL(C_1.COIL_OUTDIA                     ,'0'), 5,'0') --재료외경_1
				     ||LPAD(NVL(TO_CHAR(C_1.COIL_INDIA,'FM0000V0')  ,'0'), 5,'0') --재료내경_1
				     ||RPAD(NVL(A.WORK_STATE_1                      ,' '), 1,' ') --작업상태_1
				     ||RPAD(DECODE(A.STOCK_ID_1,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_1
				     
				     ||RPAD(NVL(A.STOCK_ID_2                        ,' '),11,' ') --재료번호_2
				     ||RPAD(NVL(A.LOAD_LOC_CD_2                     ,' '), 2,' ') --차량적재위치_2
				     ||LPAD(NVL(C_2.COIL_WT                         ,'0'), 5,'0') --재료중량_2
				     ||LPAD(NVL(TO_CHAR(C_2.COIL_T,'FM000V000')     ,'0'), 6,'0') --재료두께_2
				     ||LPAD(NVL(TO_CHAR(C_2.COIL_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_2
				     ||LPAD(NVL(C_2.COIL_LEN                        ,'0'), 7,'0') --재료길이_2
				     ||LPAD(NVL(C_2.COIL_OUTDIA                     ,'0'), 5,'0') --재료외경_2
				     ||LPAD(NVL(TO_CHAR(C_2.COIL_INDIA,'FM0000V0')  ,'0'), 5,'0') --재료내경_2
				     ||RPAD(NVL(A.WORK_STATE_2                      ,' '), 1,' ') --작업상태_2
				     ||RPAD(DECODE(A.STOCK_ID_2,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_2
				     
				     ||RPAD(NVL(A.STOCK_ID_3                        ,' '),11,' ') --재료번호_3
				     ||RPAD(NVL(A.LOAD_LOC_CD_3                     ,' '), 2,' ') --차량적재위치_3
				     ||LPAD(NVL(C_3.COIL_WT                         ,'0'), 5,'0') --재료중량_3
				     ||LPAD(NVL(TO_CHAR(C_3.COIL_T,'FM000V000')     ,'0'), 6,'0') --재료두께_3
				     ||LPAD(NVL(TO_CHAR(C_3.COIL_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_3
				     ||LPAD(NVL(C_3.COIL_LEN                        ,'0'), 7,'0') --재료길이_3
				     ||LPAD(NVL(C_3.COIL_OUTDIA                     ,'0'), 5,'0') --재료외경_3
				     ||LPAD(NVL(TO_CHAR(C_3.COIL_INDIA,'FM0000V0')  ,'0'), 5,'0') --재료내경_3
				     ||RPAD(NVL(A.WORK_STATE_3                      ,' '), 1,' ') --작업상태_3
				     ||RPAD(DECODE(A.STOCK_ID_3,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_3
				     
				     ||RPAD(NVL(A.STOCK_ID_4                        ,' '),11,' ') --재료번호_4
				     ||RPAD(NVL(A.LOAD_LOC_CD_4                     ,' '), 2,' ') --차량적재위치_4
				     ||LPAD(NVL(C_4.COIL_WT                         ,'0'), 5,'0') --재료중량_4
				     ||LPAD(NVL(TO_CHAR(C_4.COIL_T,'FM000V000')     ,'0'), 6,'0') --재료두께_4
				     ||LPAD(NVL(TO_CHAR(C_4.COIL_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_4
				     ||LPAD(NVL(C_4.COIL_LEN                        ,'0'), 7,'0') --재료길이_4
				     ||LPAD(NVL(C_4.COIL_OUTDIA                     ,'0'), 5,'0') --재료외경_4
				     ||LPAD(NVL(TO_CHAR(C_4.COIL_INDIA,'FM0000V0')  ,'0'), 5,'0') --재료내경_4
				     ||RPAD(NVL(A.WORK_STATE_4                      ,' '), 1,' ') --작업상태_4
				     ||RPAD(DECODE(A.STOCK_ID_4,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_4
				     
				     ||RPAD(NVL(A.STOCK_ID_5                        ,' '),11,' ') --재료번호_5
				     ||RPAD(NVL(A.LOAD_LOC_CD_5                     ,' '), 2,' ') --차량적재위치_5
				     ||LPAD(NVL(C_5.COIL_WT                         ,'0'), 5,'0') --재료중량_5
				     ||LPAD(NVL(TO_CHAR(C_5.COIL_T,'FM000V000')     ,'0'), 6,'0') --재료두께_5
				     ||LPAD(NVL(TO_CHAR(C_5.COIL_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_5
				     ||LPAD(NVL(C_5.COIL_LEN                        ,'0'), 7,'0') --재료길이_5
				     ||LPAD(NVL(C_5.COIL_OUTDIA                     ,'0'), 5,'0') --재료외경_5
				     ||LPAD(NVL(TO_CHAR(C_5.COIL_INDIA,'FM0000V0')  ,'0'), 5,'0') --재료내경_5
				     ||RPAD(NVL(A.WORK_STATE_5                      ,' '), 1,' ') --작업상태_5
				     ||RPAD(DECODE(A.STOCK_ID_5,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_5
				     
				     ||RPAD(NVL(A.STOCK_ID_6                        ,' '),11,' ') --재료번호_6
				     ||RPAD(NVL(A.LOAD_LOC_CD_6                     ,' '), 2,' ') --차량적재위치_6
				     ||LPAD(NVL(C_6.COIL_WT                         ,'0'), 5,'0') --재료중량_6
				     ||LPAD(NVL(TO_CHAR(C_6.COIL_T,'FM000V000')     ,'0'), 6,'0') --재료두께_6
				     ||LPAD(NVL(TO_CHAR(C_6.COIL_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_6
				     ||LPAD(NVL(C_6.COIL_LEN                        ,'0'), 7,'0') --재료길이_6
				     ||LPAD(NVL(C_6.COIL_OUTDIA                     ,'0'), 5,'0') --재료외경_6
				     ||LPAD(NVL(TO_CHAR(C_6.COIL_INDIA,'FM0000V0')  ,'0'), 5,'0') --재료내경_6
				     ||RPAD(NVL(A.WORK_STATE_6                      ,' '), 1,' ') --작업상태_6
				     ||RPAD(DECODE(A.STOCK_ID_6,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_6
				     
				     ||RPAD(NVL(A.STOCK_ID_7                        ,' '),11,' ') --재료번호_7
				     ||RPAD(NVL(A.LOAD_LOC_CD_7                     ,' '), 2,' ') --차량적재위치_7
				     ||LPAD(NVL(C_7.COIL_WT                         ,'0'), 5,'0') --재료중량_7
				     ||LPAD(NVL(TO_CHAR(C_7.COIL_T,'FM000V000')     ,'0'), 6,'0') --재료두께_7
				     ||LPAD(NVL(TO_CHAR(C_7.COIL_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_7
				     ||LPAD(NVL(C_7.COIL_LEN                        ,'0'), 7,'0') --재료길이_7
				     ||LPAD(NVL(C_7.COIL_OUTDIA                     ,'0'), 5,'0') --재료외경_7
				     ||LPAD(NVL(TO_CHAR(C_7.COIL_INDIA,'FM0000V0')  ,'0'), 5,'0') --재료내경_7
				     ||RPAD(NVL(A.WORK_STATE_7                      ,' '), 1,' ') --작업상태_7
				     ||RPAD(DECODE(A.STOCK_ID_7,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_7
				     
				     ||RPAD(NVL(A.STOCK_ID_8                        ,' '),11,' ') --재료번호_8
				     ||RPAD(NVL(A.LOAD_LOC_CD_8                     ,' '), 2,' ') --차량적재위치_8
				     ||LPAD(NVL(C_8.COIL_WT                         ,'0'), 5,'0') --재료중량_8
				     ||LPAD(NVL(TO_CHAR(C_8.COIL_T,'FM000V000')     ,'0'), 6,'0') --재료두께_8
				     ||LPAD(NVL(TO_CHAR(C_8.COIL_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_8
				     ||LPAD(NVL(C_8.COIL_LEN                        ,'0'), 7,'0') --재료길이_8
				     ||LPAD(NVL(C_8.COIL_OUTDIA                     ,'0'), 5,'0') --재료외경_8
				     ||LPAD(NVL(TO_CHAR(C_8.COIL_INDIA,'FM0000V0')  ,'0'), 5,'0') --재료내경_8
				     ||RPAD(NVL(A.WORK_STATE_8                      ,' '), 1,' ') --작업상태_8
				     ||RPAD(DECODE(A.STOCK_ID_8,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_8
				     
				     ||RPAD(NVL(A.STOCK_ID_9                        ,' '),11,' ') --재료번호_9
				     ||RPAD(NVL(A.LOAD_LOC_CD_9                     ,' '), 2,' ') --차량적재위치_9
				     ||LPAD(NVL(C_9.COIL_WT                         ,'0'), 5,'0') --재료중량_9
				     ||LPAD(NVL(TO_CHAR(C_9.COIL_T,'FM000V000')     ,'0'), 6,'0') --재료두께_9
				     ||LPAD(NVL(TO_CHAR(C_9.COIL_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_9
				     ||LPAD(NVL(C_9.COIL_LEN                        ,'0'), 7,'0') --재료길이_9
				     ||LPAD(NVL(C_9.COIL_OUTDIA                     ,'0'), 5,'0') --재료외경_9
				     ||LPAD(NVL(TO_CHAR(C_9.COIL_INDIA,'FM0000V0')  ,'0'), 5,'0') --재료내경_9
				     ||RPAD(NVL(A.WORK_STATE_9                      ,' '), 1,' ') --작업상태_9
				     ||RPAD(DECODE(A.STOCK_ID_9,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_9
				     
				     ||RPAD(NVL(A.STOCK_ID_10                        ,' '),11,' ') --재료번호_10
				     ||RPAD(NVL(A.LOAD_LOC_CD_10                     ,' '), 2,' ') --차량적재위치_10
				     ||LPAD(NVL(C_10.COIL_WT                         ,'0'), 5,'0') --재료중량_10
				     ||LPAD(NVL(TO_CHAR(C_10.COIL_T,'FM000V000')     ,'0'), 6,'0') --재료두께_10
				     ||LPAD(NVL(TO_CHAR(C_10.COIL_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_10
				     ||LPAD(NVL(C_10.COIL_LEN                        ,'0'), 7,'0') --재료길이_10
				     ||LPAD(NVL(C_10.COIL_OUTDIA                     ,'0'), 5,'0') --재료외경_10
				     ||LPAD(NVL(TO_CHAR(C_10.COIL_INDIA,'FM0000V0')  ,'0'), 5,'0') --재료내경_10
				     ||RPAD(NVL(A.WORK_STATE_10                      ,' '), 1,' ') --작업상태_10
				     ||RPAD(DECODE(A.STOCK_ID_10,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_10
				     
				     ||RPAD(NVL(A.STOCK_ID_11                        ,' '),11,' ') --재료번호_11
				     ||RPAD(NVL(A.LOAD_LOC_CD_11                     ,' '), 2,' ') --차량적재위치_11
				     ||LPAD(NVL(C_11.COIL_WT                         ,'0'), 5,'0') --재료중량_11
				     ||LPAD(NVL(TO_CHAR(C_11.COIL_T,'FM000V000')     ,'0'), 6,'0') --재료두께_11
				     ||LPAD(NVL(TO_CHAR(C_11.COIL_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_11
				     ||LPAD(NVL(C_11.COIL_LEN                        ,'0'), 7,'0') --재료길이_11
				     ||LPAD(NVL(C_11.COIL_OUTDIA                     ,'0'), 5,'0') --재료외경_11
				     ||LPAD(NVL(TO_CHAR(C_11.COIL_INDIA,'FM0000V0')  ,'0'), 5,'0') --재료내경_11
				     ||RPAD(NVL(A.WORK_STATE_11                      ,' '), 1,' ') --작업상태_11
				     ||RPAD(DECODE(A.STOCK_ID_11,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_11
				     
				     ||RPAD(NVL(A.STOCK_ID_12                        ,' '),11,' ') --재료번호_12
				     ||RPAD(NVL(A.LOAD_LOC_CD_12                     ,' '), 2,' ') --차량적재위치_12
				     ||LPAD(NVL(C_12.COIL_WT                         ,'0'), 5,'0') --재료중량_12
				     ||LPAD(NVL(TO_CHAR(C_12.COIL_T,'FM000V000')     ,'0'), 6,'0') --재료두께_12
				     ||LPAD(NVL(TO_CHAR(C_12.COIL_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_12
				     ||LPAD(NVL(C_12.COIL_LEN                        ,'0'), 7,'0') --재료길이_12
				     ||LPAD(NVL(C_12.COIL_OUTDIA                     ,'0'), 5,'0') --재료외경_12
				     ||LPAD(NVL(TO_CHAR(C_12.COIL_INDIA,'FM0000V0')  ,'0'), 5,'0') --재료내경_12
				     ||RPAD(NVL(A.WORK_STATE_12                      ,' '), 1,' ') --작업상태_12
				     ||RPAD(DECODE(A.STOCK_ID_12,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_12
				     
				     ||RPAD(NVL(A.STOCK_ID_13                        ,' '),11,' ') --재료번호_13
				     ||RPAD(NVL(A.LOAD_LOC_CD_13                     ,' '), 2,' ') --차량적재위치_13
				     ||LPAD(NVL(C_13.COIL_WT                         ,'0'), 5,'0') --재료중량_13
				     ||LPAD(NVL(TO_CHAR(C_13.COIL_T,'FM000V000')     ,'0'), 6,'0') --재료두께_13
				     ||LPAD(NVL(TO_CHAR(C_13.COIL_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_13
				     ||LPAD(NVL(C_13.COIL_LEN                        ,'0'), 7,'0') --재료길이_13
				     ||LPAD(NVL(C_13.COIL_OUTDIA                     ,'0'), 5,'0') --재료외경_13
				     ||LPAD(NVL(TO_CHAR(C_13.COIL_INDIA,'FM0000V0')  ,'0'), 5,'0') --재료내경_13
				     ||RPAD(NVL(A.WORK_STATE_13                      ,' '), 1,' ') --작업상태_13
				     ||RPAD(DECODE(A.STOCK_ID_13,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_13
				     
				     ||RPAD(NVL(A.STOCK_ID_14                        ,' '),11,' ') --재료번호_14
				     ||RPAD(NVL(A.LOAD_LOC_CD_14                     ,' '), 2,' ') --차량적재위치_14
				     ||LPAD(NVL(C_14.COIL_WT                         ,'0'), 5,'0') --재료중량_14
				     ||LPAD(NVL(TO_CHAR(C_14.COIL_T,'FM000V000')     ,'0'), 6,'0') --재료두께_14
				     ||LPAD(NVL(TO_CHAR(C_14.COIL_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_14
				     ||LPAD(NVL(C_14.COIL_LEN                        ,'0'), 7,'0') --재료길이_14
				     ||LPAD(NVL(C_14.COIL_OUTDIA                     ,'0'), 5,'0') --재료외경_14
				     ||LPAD(NVL(TO_CHAR(C_14.COIL_INDIA,'FM0000V0')  ,'0'), 5,'0') --재료내경_14
				     ||RPAD(NVL(A.WORK_STATE_14                      ,' '), 1,' ') --작업상태_14
				     ||RPAD(DECODE(A.STOCK_ID_14,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_14
				     
				       AS JMS_TC_MESSAGE --JMSTCMessage
				      
				  FROM (
				  
				        SELECT 'YMA7L008'           AS JMS_TC_CD
				              ,:V_PT_LOAD_LOC       AS PT_LOAD_LOC
				              ,:V_CAR_NO            AS CAR_NO
				              ,:V_CARD_NO           AS CARD_NO
				              ,:V_PT_CLS            AS PT_CLS
				              ,:V_WORK_CLS          AS WORK_CLS
				              ,:V_WORK_COIL_MAX_CNT AS WORK_COIL_MAX_CNT
				              
				              ,:V_STOCK_ID_0        AS STOCK_ID_0
				              ,:V_LOAD_LOC_CD_0     AS LOAD_LOC_CD_0
				              ,:V_WORK_STATE_0      AS WORK_STATE_0
				              
				              ,:V_STOCK_ID_1        AS STOCK_ID_1
				              ,:V_LOAD_LOC_CD_1     AS LOAD_LOC_CD_1
				              ,:V_WORK_STATE_1      AS WORK_STATE_1
				              
				              ,:V_STOCK_ID_2        AS STOCK_ID_2
				              ,:V_LOAD_LOC_CD_2     AS LOAD_LOC_CD_2
				              ,:V_WORK_STATE_2      AS WORK_STATE_2
				              
				              ,:V_STOCK_ID_3        AS STOCK_ID_3
				              ,:V_LOAD_LOC_CD_3     AS LOAD_LOC_CD_3
				              ,:V_WORK_STATE_3      AS WORK_STATE_3
				              
				              ,:V_STOCK_ID_4        AS STOCK_ID_4
				              ,:V_LOAD_LOC_CD_4     AS LOAD_LOC_CD_4
				              ,:V_WORK_STATE_4      AS WORK_STATE_4
				              
				              ,:V_STOCK_ID_5        AS STOCK_ID_5
				              ,:V_LOAD_LOC_CD_5     AS LOAD_LOC_CD_5
				              ,:V_WORK_STATE_5      AS WORK_STATE_5
				              
				              ,:V_STOCK_ID_6        AS STOCK_ID_6
				              ,:V_LOAD_LOC_CD_6     AS LOAD_LOC_CD_6
				              ,:V_WORK_STATE_6      AS WORK_STATE_6
				              
				              ,:V_STOCK_ID_7        AS STOCK_ID_7
				              ,:V_LOAD_LOC_CD_7     AS LOAD_LOC_CD_7
				              ,:V_WORK_STATE_7      AS WORK_STATE_7
				              
				              ,:V_STOCK_ID_8        AS STOCK_ID_8
				              ,:V_LOAD_LOC_CD_8     AS LOAD_LOC_CD_8
				              ,:V_WORK_STATE_8      AS WORK_STATE_8
				              
				              ,:V_STOCK_ID_9        AS STOCK_ID_9
				              ,:V_LOAD_LOC_CD_9     AS LOAD_LOC_CD_9
				              ,:V_WORK_STATE_9      AS WORK_STATE_9
				              
				              ,:V_STOCK_ID_10       AS STOCK_ID_10
				              ,:V_LOAD_LOC_CD_10    AS LOAD_LOC_CD_10
				              ,:V_WORK_STATE_10     AS WORK_STATE_10
				              
				              ,:V_STOCK_ID_11       AS STOCK_ID_11
				              ,:V_LOAD_LOC_CD_11    AS LOAD_LOC_CD_11
				              ,:V_WORK_STATE_11     AS WORK_STATE_11
				              
				              ,:V_STOCK_ID_12       AS STOCK_ID_12
				              ,:V_LOAD_LOC_CD_12    AS LOAD_LOC_CD_12
				              ,:V_WORK_STATE_12     AS WORK_STATE_12
				              
				              ,:V_STOCK_ID_13       AS STOCK_ID_13
				              ,:V_LOAD_LOC_CD_13    AS LOAD_LOC_CD_13
				              ,:V_WORK_STATE_13     AS WORK_STATE_13
				              
				              ,:V_STOCK_ID_14       AS STOCK_ID_14
				              ,:V_LOAD_LOC_CD_14    AS LOAD_LOC_CD_14
				              ,:V_WORK_STATE_14     AS WORK_STATE_14
				              
				        FROM   DUAL
				  
				       ) A
				      ,TB_PT_COILCOMM C_0
				      ,TB_PT_COILCOMM C_1
				      ,TB_PT_COILCOMM C_2
				      ,TB_PT_COILCOMM C_3
				      ,TB_PT_COILCOMM C_4
				      ,TB_PT_COILCOMM C_5
				      ,TB_PT_COILCOMM C_6
				      ,TB_PT_COILCOMM C_7
				      ,TB_PT_COILCOMM C_8
				      ,TB_PT_COILCOMM C_9
				      ,TB_PT_COILCOMM C_10
				      ,TB_PT_COILCOMM C_11
				      ,TB_PT_COILCOMM C_12
				      ,TB_PT_COILCOMM C_13
				      ,TB_PT_COILCOMM C_14
				 WHERE A.STOCK_ID_0 = C_0.COIL_NO(+)
				  AND  A.STOCK_ID_1 = C_1.COIL_NO(+)
				  AND  A.STOCK_ID_2 = C_2.COIL_NO(+)
				  AND  A.STOCK_ID_3 = C_3.COIL_NO(+)
				  AND  A.STOCK_ID_4 = C_4.COIL_NO(+)
				  AND  A.STOCK_ID_5 = C_5.COIL_NO(+)
				  AND  A.STOCK_ID_6 = C_6.COIL_NO(+)
				  AND  A.STOCK_ID_7 = C_7.COIL_NO(+)
				  AND  A.STOCK_ID_8 = C_8.COIL_NO(+)
				  AND  A.STOCK_ID_9 = C_9.COIL_NO(+)
				  AND  A.STOCK_ID_10 = C_10.COIL_NO(+)
				  AND  A.STOCK_ID_11 = C_11.COIL_NO(+)
				  AND  A.STOCK_ID_12 = C_12.COIL_NO(+)
				  AND  A.STOCK_ID_13 = C_13.COIL_NO(+)
				  AND  A.STOCK_ID_14 = C_14.COIL_NO(+)
				  */
		    	trtNm = "B열연 COIL 차량예정정보 Backup";
		    	jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.YMA7L008BackUp";

			} else if("YMA7L009".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L009_PIDEV
				--압연지시 송신
				SELECT JMS_TC_CD                                     				--JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT 	--JMSTC생성일시
				      ,JMS_TC_CD                                     				--전문ID
				     ||TO_CHAR(SYSDATE,'YYYY-MM-DD')       							--생성일시
				     ||TO_CHAR(SYSDATE,'HH24-MI-SS')       							--생성일시
				     ||RPAD(NVL(TC_ID                 ,'I'), 1,' ') 				--전문구분
				     ||'0112'                                        				--전문길이
				     ||RPAD(NVL(STOCK_ID              , ' '),  11, ' ')
				     ||RPAD(NVL(GRP                   , ' '),  1 , ' ')
				     ||RPAD(NVL(ORD_NO_DTL            , ' '),  13, ' ')
				     ||RPAD(NVL(YD_STL_T              , ' '),  6 , ' ')
				     ||RPAD(NVL(YD_STL_W              , ' '),  5 , ' ')
				     ||RPAD(NVL(YD_STL_L              , ' '),  7 , ' ')
				     ||RPAD(NVL(MAT_ODIA              , ' '),  5 , ' ')
				     ||RPAD(NVL(YD_STL_WT             , ' '),  5 , ' ')
				     ||RPAD(NVL(BRANCH_CD             , ' '),  2 , ' ')
				     ||RPAD(NVL(EXTEND_BRANCH_CD      , ' '),  2 , ' ')
				     ||RPAD(NVL(COOL_METHOD           , ' '),  1 , ' ')
				     ||RPAD(NVL(DEMANDER_NM           , ' '),  40, ' ')
				     ||RPAD(NVL(HRMILL_CMPL_DT        , ' '),  12, ' ')
				     ||RPAD(NVL(NEXT_PROC             , ' '),  2 , ' ')

				       AS JMS_TC_MESSAGE --JMSTCMessage
				  FROM (
				        SELECT 'YMA7L009'                               AS JMS_TC_CD  
				             , 'I'                                      AS TC_ID                   	--전문구분
				             , A.COIL_NO                                AS STOCK_ID 
				             , '2'                                      AS GRP
				             , A.ORD_NO||A.ORD_DTL                      AS ORD_NO_DTL
				             , TO_CHAR(A.COIL_T         , 'FM000V000')  AS YD_STL_T
				             , TO_CHAR(A.COIL_W         , 'FM0000V0' )  AS YD_STL_W
				             , TO_CHAR(A.CURR_COIL_LEN  , 'FM0000000')  AS YD_STL_L
				             , TO_CHAR(A.COIL_OUTDIA    , 'FM00000'  )  AS MAT_ODIA
				             , TO_CHAR(decode(A.COIL_WT,0,A.NET_CAL_WT,A.COIL_WT)    
				                                        , 'FM00000'  )  AS YD_STL_WT
				             , A.BRANCH_CD 		                        AS BRANCH_CD
				             , A.EXTEND_CONVEYOR_BRANCH_CD 
				                                                        AS EXTEND_BRANCH_CD
				             , A.COOL_METHOD 	                        AS COOL_METHOD
				             , B.CUST_KO_NAME                           AS DEMANDER_NM
				             , TO_CHAR(A.HRMILL_CMPL_DT,'YYYYMMDDHH24mi') AS HRMILL_CMPL_DT
				             , A.NEXT_PROC 		                         AS NEXT_PROC
				          FROM TB_PT_COILCOMM A
				             , TB_SM_CUSTINFO B
				         WHERE A.DEMANDER_CD =B.CUST_CD 
				           AND A.COIL_NO = :V_COIL_NO   -- 재료번호(KCN48660) 
				       )
				 WHERE 1 = 1
				 */
				trtNm = "압연지시 송신"; 
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L009_PIDEV";
			} else if("YMA7L010".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L010 
				SELECT JMS_TC_CD                                     --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				      ,JMS_TC_CD                                     --전문ID
				     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')       --생성일시
				     ||RPAD(NVL(MSG_GP                 ,'I'), 1,' ') --전문구분
				     ||'0058'                                        --전문길이
				     ||RPAD(' ',29,' ')                              --임시
				     ||RPAD(NVL(STOCK_ID               ,' '),11,' ') --COILNO
				     ||RPAD(NVL(GRP                    ,' '), 1,' ') --군
				     ||RPAD(NVL(ORD_NO_DTL             ,' '),13,' ') --제작번호행번
				     ||RPAD(NVL(YD_STL_T               ,' '), 6,' ') --야드재료두께
				     ||LPAD(NVL(YD_STL_W               ,'0'), 5,'0') --야드재료폭
				     ||LPAD(NVL(YD_STL_L               ,'0'), 7,'0') --야드재료길이
				     ||LPAD(NVL(MAT_ODIA               ,'0'), 5,'0') --재료외경
				     ||LPAD(NVL(YD_STL_WT              ,'0'), 5,'0') --야드재료중량
				     ||RPAD(NVL(BRANCH_CD              ,' '), 2,' ') --분기CONV위치CODE
				     ||RPAD(NVL(EXTEND_BRANCH_CD       ,' '), 2,' ') --확장CONV분기위치CODE
				     ||RPAD(NVL(COOL_METHOD            ,' '), 1,' ') --냉각방법
				       AS JMS_TC_MESSAGE --JMSTCMESSAGE
				FROM (
				        SELECT'YMA7L010'            AS JMS_TC_CD                --전문ID
				             , 'I'                  AS MSG_GP                   --전문구분
				             , A.COIL_NO            AS STOCK_ID
				             , '2'                  AS GRP
				             , A.ORD_NO|| A.ORD_DTL AS ORD_NO_DTL
				             , TO_CHAR(A.COIL_T     , 'FM000V000') AS YD_STL_T
				             , TO_CHAR(A.COIL_W     , 'FM0000V0' ) AS YD_STL_W
				             , TO_CHAR(A.COIL_LEN   , 'FM0000000') AS YD_STL_L
				             , TO_CHAR(A.COIL_OUTDIA, 'FM00000'  ) AS MAT_ODIA
				             , TO_CHAR(A.COIL_WT    , 'FM00000'  ) AS YD_STL_WT
				             , DECODE(A.NEXT_PROC,'5K','4E','5R','4E','5H','4E','6K','4E','6H','4E',A.BRANCH_CD) AS BRANCH_CD
				             , DECODE(A.NEXT_PROC,'5K','5E','5R','5E','5H','5E','6K','5E','6H','5E','5A','5E','1Q','5E',A.EXTEND_CONVEYOR_BRANCH_CD) AS EXTEND_BRANCH_CD
				             , A.COOL_METHOD AS COOL_METHOD 
				          FROM
				               TB_PT_COILCOMM A 
				         WHERE A.COIL_NO = :V_STOCK_ID
				        )   
				 */
				trtNm = "코일 분기 Conv To 확장 Conv 시점정보";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L010";				
			} else if("YMA7L011".equals(msgId)) {
				/*
				SELECT JMS_TC_CD                                    --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				     , JMS_TC_CD                                 --전문ID
				     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS') --발생일자
				     ||RPAD(NVL(MSG_GP      , ' '), 1 , ' ') --전문구분
				     ||RPAD(NVL(MSG_LEN     , ' '), 4 , ' ') --전문길이
				     ||RPAD(NVL(TEMP        , ' '), 29, ' ') --임시
				     ||RPAD(NVL(YD_EQP_ID   , ' '), 6 , ' ') --설비코드    
				       AS JMS_TC_MESSAGE --JMSTCMessage
				  FROM (
				        SELECT 'YMA7L011' AS JMS_TC_CD  
				             , 'I'        AS MSG_GP    
				             , '0006'     AS MSG_LEN     
				             , ''         AS TEMP      
				             , 'SSX1'     AS YD_EQP_ID      
				          FROM DUAL
				         WHERE 1 = 1
				       )
				 WHERE 1 = 1
				 */
				trtNm = "코일 1냉연 대차이동요구";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L011";
		    	
			} else if("YMA7L012".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L012
				SELECT JMS_TC_CD                                    --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				     , JMS_TC_CD                                 --전문ID
				     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS') --발생일자
				     ||RPAD(NVL(MSG_GP      , ' '), 1 , ' ') --전문구분
				     ||RPAD(NVL(MSG_LEN     , ' '), 4 , ' ') --전문길이
				     ||RPAD(NVL(TEMP        , ' '), 29, ' ') --임시
				     ||RPAD(NVL(STOCK_ID    , ' '), 11, ' ') --설비코드  
				       AS JMS_TC_MESSAGE --JMSTCMessage
				 FROM (
				        SELECT'YMA7L012'    AS JMS_TC_CD --전문ID
				             , 'I'          AS MSG_GP    --전문구분
				             , '0011'       AS MSG_LEN     
							 , ''           AS TEMP 
				             , :V_STOCK_ID   AS STOCK_ID
				          FROM DUAL
				      )  
				 */
				trtNm = "확장 CONV LINE OFF 요구 응답";
				//LINE OFF 요구시 송신
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L012";
		    	
			} else if("YMA7L013".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L013 
				SELECT JMS_TC_CD                                    --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				     , JMS_TC_CD                                 --전문ID
				     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS') --발생일자
				     ||RPAD(NVL(MSG_GP          , ' '), 1 , ' ') --전문구분
				     ||RPAD(NVL(MSG_LEN         , ' '), 4 , ' ') --전문길이
				     ||RPAD(NVL(TEMP            , ' '), 29, ' ') --임시
				     ||RPAD(NVL(YD_EQP_ID       , ' '), 6 , ' ') --설비코드  
				     ||RPAD(NVL(MV_GP           , ' '), 1 , ' ') --이동구분
				     ||LPAD(NVL(YD_WO_LOC_XAXIS , '0'), 7 , '0') --이동지시X축  
				     ||LPAD(NVL(YD_WO_LOC_YAXIS , '0'), 5 , '0') --이동지시Y축  
				       AS JMS_TC_MESSAGE --JMSTCMessage
				 FROM (
				        SELECT'YMA7L013'            AS JMS_TC_CD --전문ID
				             , 'I'                  AS MSG_GP    --전문구분
				             , '0019'               AS MSG_LEN     
							 , ''                   AS TEMP 
				             , :V_YD_EQP_ID         AS YD_EQP_ID
				             , :V_MV_GP             AS MV_GP
				             , :V_YD_WO_LOC_XAXIS   AS YD_WO_LOC_XAXIS
				             , :V_YD_WO_LOC_YAXIS   AS YD_WO_LOC_YAXIS
				          FROM DUAL
				      )  
				*/
				trtNm = "코일 HOME 이동지시";
				//LINE OFF 요구시 송신
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L013";
				
			}else if("YMA7L014".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L014 
				SELECT JMS_TC_CD                                    --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				     , JMS_TC_CD                                 --전문ID
				     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS') --발생일자
				     ||RPAD(NVL(MSG_GP          , ' '), 1 , ' ') --전문구분
				     ||RPAD(NVL(MSG_LEN         , ' '), 4 , ' ') --전문길이
				     ||RPAD(NVL(TEMP            , ' '), 29, ' ') --임시
				     ||RPAD(NVL(CAR_NO       , ' '), 15 , ' ') --차량번호
				     ||LPAD(NVL(TRANS_ORD_SEQNO , '0'), 6 , '0')--지시번호
				     ||LPAD(NVL(AB_COIL_NUM , '0'), 1 , '0')--이상코일 발생 수량
				     ||RPAD(NVL(STL_NO1       , ' '), 11 , ' ') --이상코일 넘버1
				     ||LPAD(NVL(YD_AB_CD1 , '0'), 3 , '0')--이상코일종류1
				     ||LPAD(NVL(YD_AB_CD_DETAIL1 , '0'), 2 , '0')--이상코일상세1
				     ||RPAD(NVL(STL_NO2       , ' '), 11 , ' ') --이상코일 넘버1
				     ||LPAD(NVL(YD_AB_CD2 , '0'), 3 , '0')--이상코일종류1
				     ||LPAD(NVL(YD_AB_CD_DETAIL2 , '0'), 2 , '0')--이상코일상세1
				     ||RPAD(NVL(STL_NO3       , ' '), 11 , ' ') --이상코일 넘버1
				     ||LPAD(NVL(YD_AB_CD3 , '0'), 3 , '0')--이상코일종류1
				     ||LPAD(NVL(YD_AB_CD_DETAIL3 , '0'), 2 , '0')--이상코일상세1
				     
				       AS JMS_TC_MESSAGE --JMSTCMessage
				 FROM (
				        SELECT'YMA7L014'            AS JMS_TC_CD --전문ID
				             , 'I'                  AS MSG_GP    --전문구분
				             , '0070'               AS MSG_LEN     
				             , ''                   AS TEMP 
				             ,CAR_NO
				          ,TRANS_ORD_DATE
				          ,TRANS_ORD_SEQNO    
				          ,COUNT(STL_NO)          AS  AB_COIL_NUM
				          ,MAX(STL_NO1)           AS  STL_NO1
				          ,MAX(YD_AB_CD1)         AS  YD_AB_CD1
				          ,MAX(YD_AB_CD_DETAIL1)  AS  YD_AB_CD_DETAIL1
				          ,MAX(STL_NO2)           AS  STL_NO2
				          ,MAX(YD_AB_CD2)         AS  YD_AB_CD2
				          ,MAX(YD_AB_CD_DETAIL2)  AS  YD_AB_CD_DETAIL2
				          ,MAX(STL_NO3)           AS  STL_NO3
				          ,MAX(YD_AB_CD3)         AS  YD_AB_CD3
				          ,MAX(YD_AB_CD_DETAIL3)  AS  YD_AB_CD_DETAIL3
				     FROM   (    
				        SELECT CAR_NO
				              ,TRANS_ORD_DATE
				              ,TRANS_ORD_SEQNO
				              ,STL_NO
				              ,CASE WHEN RN = 1 THEN STL_NO     ELSE '' END AS STL_NO1
				              ,CASE WHEN RN = 1 THEN YD_AB_CD   ELSE '' END AS YD_AB_CD1
				              ,CASE WHEN RN = 1 THEN YD_AB_CD2  ELSE '' END AS YD_AB_CD_DETAIL1
				              ,CASE WHEN RN = 2 THEN STL_NO     ELSE '' END AS STL_NO2
				              ,CASE WHEN RN = 2 THEN YD_AB_CD   ELSE '' END AS YD_AB_CD2
				              ,CASE WHEN RN = 2 THEN YD_AB_CD2  ELSE '' END AS YD_AB_CD_DETAIL2
				              ,CASE WHEN RN = 3 THEN STL_NO     ELSE '' END AS STL_NO3
				              ,CASE WHEN RN = 3 THEN YD_AB_CD   ELSE '' END AS YD_AB_CD3
				              ,CASE WHEN RN = 3 THEN YD_AB_CD2  ELSE '' END AS YD_AB_CD_DETAIL3
				              
				         FROM (
				                SELECT ROWNUM AS RN
				                       ,CAR_NO
				                      ,TRANS_ORD_DATE
				                      ,TRANS_ORD_SEQNO
				                      ,STL_NO
				                      ,YD_AB_CD
				                      ,YD_AB_CD2
				                  FROM TB_YD_EXAMINATIONCHKLIST A
				                 WHERE 1=1
				                  AND TRANS_ORD_DATE = :V_TRANS_ORD_DATE
				                  AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				                  AND YD_AB_CD IS NOT NULL 
				               )
				    )
				    GROUP BY CAR_NO
				          ,TRANS_ORD_DATE
				          ,TRANS_ORD_SEQNO
				          ,STL_NO
				      )*/
				trtNm = "이상코일발생정보";
				//LINE OFF 요구시 송신
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA7L014";
				
			}else if("CF1BP04".equals(msgId)) {
				trtNm = "분기 CONV' 분기 LINE OFF";
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCF1BP04 
				SELECT JMS_TC_CD                                     				--JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT 	--JMSTC생성일시
				      ,JMS_TC_CD                                     				--전문ID
				     ||TO_CHAR(SYSDATE,'YYYY-MM-DD')       							--생성일시
				     ||TO_CHAR(SYSDATE,'HH24-MI-SS')       							--생성일시
				     ||RPAD(NVL(TC_ID                 ,'I'), 1,' ') 				--전문구분
				     ||'0012'                                        				--전문길이
				     ||RPAD(NVL(STOCK_ID              ,' '),10,' ') 				--COILNO
				     ||RPAD(' ',2,' ')                              				--임시
				       AS JMS_TC_MESSAGE --JMSTCMESSAGE
				FROM (
				        SELECT'CF1BP04'               AS JMS_TC_CD                	--전문ID
				             , 'I'                    AS TC_ID                   	--전문구분
				             , A.STOCK_ID             AS STOCK_ID
				          FROM USRYMA.TB_YM_CRNWRKMTL A
				         WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID  
				        )   
			
				*/
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCF1BP04";	
				
			} else if("CF1BP04B".equals(msgId)) {
				trtNm = "분기 CONV' 분기 LINE OFF";
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCF1BP04BackUp 
				SELECT JMS_TC_CD                                     --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				      ,JMS_TC_CD                                     --전문ID
				     ||TO_CHAR(SYSDATE,'YYYY-MM-DD')       --생성일시
				     ||TO_CHAR(SYSDATE,'HH24-MI-SS')       --생성일시
				     ||RPAD(NVL(TC_ID                 ,'I'), 1,' ') --전문구분
				     ||'0012'                                        --전문길이
				     ||RPAD(NVL(STOCK_ID              ,' '),10,' ') --COILNO
				     ||RPAD(' ',2,' ')                              --임시
				       AS JMS_TC_MESSAGE --JMSTCMESSAGE
				FROM (
				        SELECT'CF1BP04'             AS JMS_TC_CD    --전문ID
				             , 'I'                  AS TC_ID        --전문구분
				             , A.STOCK_ID           AS STOCK_ID
				          FROM USRYMA.TB_YM_CRNWRKMTL A
				         WHERE YD_CRN_SCH_ID = (
				                                  SELECT MAX(CM.YD_CRN_SCH_ID) AS YD_CRN_SCH_ID
				                                    FROM TB_YM_CRNWRKMTL CM
				                                       , TB_YM_CRNSCH    CR
				                                   WHERE CM.YD_CRN_SCH_ID = CR.YD_CRN_SCH_ID
				                                     AND CM.STOCK_ID = :V_STOCK_ID
				                               )
				        )     
				*/
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCF1BP04BackUp";	
				
			} else if("CF1BP05".equals(msgId)) {
				trtNm = "분기 CONV' Take Out";
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCF1BP05 
				SELECT JMS_TC_CD                                     				--JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT 	--JMSTC생성일시
				      ,JMS_TC_CD                                     				--전문ID
				     ||TO_CHAR(SYSDATE,'YYYY-MM-DD')       							--생성일시
				     ||TO_CHAR(SYSDATE,'HH24-MI-SS')       							--생성일시
				     ||RPAD(NVL(TC_ID                 ,'I'), 1,' ') 				--전문구분
				     ||'0012'                                        				--전문길이
				     ||RPAD(NVL(STOCK_ID              ,' '),10,' ') 				--COILNO
				     ||RPAD(' ',2,' ')                              				--임시
				       AS JMS_TC_MESSAGE --JMSTCMESSAGE
				FROM (
				        SELECT'CF1BP05'               AS JMS_TC_CD                	--전문ID
				             , 'I'                    AS TC_ID                   	--전문구분
				             , A.STOCK_ID             AS STOCK_ID
				          FROM USRYMA.TB_YM_CRNWRKMTL A
				         WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID  
				        )   
			
				*/
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCF1BP05";
				
			} else if("CF1BP06".equals(msgId)) {
				trtNm = "분기 CONV' Take In";
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCF1BP06 
				SELECT JMS_TC_CD                                     				--JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT 	--JMSTC생성일시
				      ,JMS_TC_CD                                     				--전문ID
				     ||TO_CHAR(SYSDATE,'YYYY-MM-DD')       							--생성일시
				     ||TO_CHAR(SYSDATE,'HH24-MI-SS')       							--생성일시
				     ||RPAD(NVL(TC_ID                 ,'I'), 1,' ') 				--전문구분
				     ||'0012'                                        				--전문길이
				     ||RPAD(NVL(STOCK_ID              ,' '),10,' ') 				--COILNO
				     ||RPAD(' ',2,' ')                              				--임시
				       AS JMS_TC_MESSAGE --JMSTCMESSAGE
				FROM (
				        SELECT'CF1BP06'               AS JMS_TC_CD                	--전문ID
				             , 'I'                    AS TC_ID                   	--전문구분
				             , A.STOCK_ID             AS STOCK_ID
				          FROM USRYMA.TB_YM_CRNWRKMTL A
				         WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID  
				        )   
			
				*/
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCF1BP06";				
		    	
				
				
				
				
				
				
				
				
				
				
				
		    /* B열연 SLAB 야드 L2 송신 ************************************************************************************/			    	
			} else if("YMA8L001".equals(msgId)) {

		    	trtNm = "B열연 SLAB 저장위치제원";
		    	jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA8L001";
		    	
			} else if("YMA8L001_CarInfo".equals(msgId)) {

		    	trtNm = "B열연 SLAB 저장위치제원(차량정보Backup";
		    	jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA8L001_CarInfo";
		    	
			} else if("YMA8L002".equals(msgId)) {

		    	trtNm = "B열연 SLAB 저장품제원";
		    	
				//야드정보동기화코드 
				String ydInfoSyncCd = commUtils.trim(jrParam.getFieldString("YD_INFO_SYNC_CD"));
				
				if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
					//위치별 >> 1:동,2:SPAN,3:열,4:BED
			    	jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA8L002ByLoc_PIDEV";
				} else {
					//재료별 >> 5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제),H:C열연장입,P:1후판장입,Q:2후판장입
			    	jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA8L002_PIDEV";
				}

			} else if("YMA8L002ChgWoClear".equals(msgId)) {
				
				trtNm = "B열연 SLAB 저장품제원 장입LOT 삭제 정보";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA8L002ChgWoClear_PIDEV";
				
			} else if("YMA8L002ChgWoSet".equals(msgId)) {
				
				trtNm = "B열연 SLAB 저장품제원 장입LOT 설정 정보";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA8L002ChgWoSet_PIDEV";
				
			} else if("YMA8L002DnWr".equals(msgId)) {
				
				trtNm = "B열연 SLAB 저장품제원 권하실적";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA8L002DnWr_PIDEV";
				
			}else if("YMA8L002Del".equals(msgId)) {
				
				trtNm = "1열연 SLAB 저장품제원 산적위치 삭제";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA8L002Del";
				
			} else if("YMA8L008BackUp".equals(msgId)) {
				
		    	trtNm = "B열연 SLAB 차량예정정보 Backup";
		    	jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.YMA8L008BackUp";
		    	
		    	
		    /* 1냉연 송신 ***********************************************************************************************/		    	
			} else if("MIMH220".equals(msgId)) {
				
		    	trtNm = "1냉연 대차이동정보";

		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcMIMH220 
		    	SELECT JMS_TC_CD                                  --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				      ,JMS_TC_CD                                  --전문ID
				     ||TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')    --생성일시
				     ||RPAD(NVL(WORK_EXP            ,' '), 2,' ') --작업근조
				     ||RPAD(NVL(WORK_PLANT          ,' '), 1,' ') --작업공장
				     ||RPAD(NVL(WORK_PROC           ,' '), 1,' ') --작업공정
				     ||RPAD(NVL(TC_COUNT            ,' '), 1,' ') --전문count
				     ||RPAD(NVL(TC_DEMAND_GP        ,' '), 1,' ') --전문요구구분
				     ||RPAD(' ',13,'0')                           --임시
				     ||RPAD(NVL(MOVE_GP             ,' '), 1,' ') --이동구분
				     ||RPAD(NVL(LEAVE_CELLNO        ,' '), 10,' ') --출발번지
				     ||RPAD(NVL(ARRIVE_CELLNO       ,' '), 10,' ') --도착번지
				       AS JMS_TC_MESSAGE --JMSTCMessage
				  FROM 
				       (SELECT 'MIMH220'        AS JMS_TC_CD        --JMSTC코드 
				             , :V_WORK_EXP      AS WORK_EXP	        --작업근조        
				             , 'I'              AS WORK_PLANT	    --작업공장      
				             , 'I'              AS WORK_PROC	    --작업공정        
				             , '*'              AS TC_COUNT	        --전문count       
				             , 'I'              AS TC_DEMAND_GP	    --전문요구구분
				             , :V_MV_GP         AS MOVE_GP	        --이동구분          
				             , :V_LEV_CELLNO    AS LEAVE_CELLNO	    --출발번지    
				             , :V_ARR_CELLNO    AS ARRIVE_CELLNO    --도착번지   
				          FROM DUAL	)
		    	*/
		    	jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcMIMH220";		    	
			
			} else if ("MIMH110".equals(msgId)) {
				
				trtNm = "코일1냉연 대차상차실적";
				
				/*
				SELECT JMS_TC_CD                                    --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				     , JMS_TC_CD                                 --전문ID
				     ||TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') --발생일자
				     ||RPAD(NVL(WORK_EXP      , ' '), 2 , ' ') --작업근조     
				     ||RPAD(NVL(WORK_PLANT    , ' '), 1 , ' ') --작업공장     
				     ||RPAD(NVL(WORK_PROC     , ' '), 1 , ' ') --작업공정     
				     ||RPAD(NVL(TC_COUNT      , ' '), 1 , ' ') --전문COUNT    
				     ||RPAD(NVL(TC_DEMAND_GP  , ' '), 1 , ' ') --전문요구구분 
				     ||RPAD(NVL(EXTRA_ITEM    , ' '), 13, ' ') --여분항목    
				     ||RPAD(NVL(EQUIP_CD      , ' '), 4 , ' ') --설비코드    
				     ||RPAD(NVL(WORK_COIL_QNTY, ' '), 1 , ' ') --작업COIL수  
				     ||RPAD(NVL(COILNO1       , ' '), 15, ' ') --COILNO1     
				     ||RPAD(NVL(CON_NO1       , ' '), 10, ' ') --계약번호1   
				     ||RPAD(NVL(CON_DTL1      , ' '), 5 , ' ') --계약행번1   
				     ||RPAD(NVL(COILNO2       , ' '), 15, ' ') --COILNO2     
				     ||RPAD(NVL(CON_NO2       , ' '), 10, ' ') --계약번호2   
				     ||RPAD(NVL(CON_DTL2      , ' '), 5 , ' ') --계약행번2   
				     ||RPAD(NVL(COILNO3       , ' '), 15, ' ') --COILNO3     
				     ||RPAD(NVL(CON_NO3       , ' '), 10, ' ') --계약번호3   
				     ||RPAD(NVL(CON_DTL3      , ' '), 5 , ' ') --계약행번3   
				       AS JMS_TC_MESSAGE --JMSTCMessage
				  FROM (
				        SELECT 'MIMH110'         AS JMS_TC_CD  
				             , :V_WORK_EXP       AS WORK_EXP      --작업근조
				             , 'I'       AS WORK_PLANT    
				             , 'I'       AS WORK_PROC     
				             , '*'       AS TC_COUNT      
				             , 'I'       AS TC_DEMAND_GP  
				             , ''        AS EXTRA_ITEM    
				             , 'SSX1'       AS EQUIP_CD      
				             , WORK_COIL_QNTY
				             , CASE WHEN BED_GP = '01' THEN STOCK_ID ELSE '' END AS COILNO1
				             , CASE WHEN BED_GP = '01' THEN ORD_NO   ELSE '' END AS CON_NO1
				             , CASE WHEN BED_GP = '01' THEN ORD_DTL  ELSE '' END AS CON_DTL1
				             , CASE WHEN BED_GP = '02' THEN STOCK_ID ELSE '' END AS COILNO2
				             , CASE WHEN BED_GP = '02' THEN ORD_NO   ELSE '' END AS CON_NO2
				             , CASE WHEN BED_GP = '02' THEN ORD_DTL  ELSE '' END AS CON_DTL2
				             , CASE WHEN BED_GP = '03' THEN STOCK_ID ELSE '' END AS COILNO3
				             , CASE WHEN BED_GP = '03' THEN ORD_NO   ELSE '' END AS CON_NO3
				             , CASE WHEN BED_GP = '03' THEN ORD_DTL  ELSE '' END AS CON_DTL3
				          FROM(
				                SELECT COUNT(A.STOCK_ID) OVER() AS WORK_COIL_QNTY
				                     , A.STOCK_ID
				                     , B.ORD_NO
				                     , B.ORD_DTL
				                     , B.BED_GP
				                  FROM( SELECT MT.STOCK_ID
				                             , SC.YD_SCH_CD
				                          FROM TB_YM_CRNSCH    SC
				                             , TB_YM_CRNWRKMTL MT
				                         WHERE 1 = 1
				                           AND SC.YD_CRN_SCH_ID = MT.YD_CRN_SCH_ID
				                           AND SC.DEL_YN = 'N'
				                           AND MT.DEL_YN = 'N'
				                           AND SUBSTR(SC.YD_DN_WO_LOC, 3, 2) = 'TC'
				                           AND SC.YD_SCH_CD LIKE '__TC12UM' --대차출하상차
				                      ) A
				                     ,( SELECT A.ORD_NO          
				                             , '00'||A.ORD_DTL         AS ORD_DTL
				                             , A.HYSCO_TRANS_GP  
				                             , A.COIL_NO               AS STOCK_ID
				                             , C.FRTOMOVE_EQUIP_BED_GP AS BED_GP
				                          FROM TB_PT_COILCOMM  A
				                             , TB_PT_OSCOMM    B 
				                             , TB_YM_STOCK     C
				                         WHERE A.ORD_NO  = B.ORD_NO(+)
				                           AND A.ORD_DTL = B.ORD_DTL(+)
				                           AND A.COIL_NO = C.STOCK_ID
				                           AND A.HYSCO_TRANS_GP    = 'C'
				                      ) B
				                 WHERE 1 = 1
				                   AND A.STOCK_ID = B.STOCK_ID
				              )
				         WHERE 1 = 1
				       )
				 WHERE 1 = 1
				 */
				
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getMIMH110";
				
			} else if ("MIMH210".equals(msgId)) {
				
				trtNm = "코일1냉연 대차상태정보";
				/*
				SELECT JMS_TC_CD                                    --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				     , JMS_TC_CD                                 --전문ID
				     ||TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') --발생일자
				     ||RPAD(NVL(WORK_EXP    , ' '), 2 , ' ') 
				     ||RPAD(NVL(WORK_PLANT  , ' '), 1 , ' ') 
				     ||RPAD(NVL(WORK_PROC   , ' '), 1 , ' ') 
				     ||RPAD(NVL(TC_COUNT    , ' '), 1 , ' ') 
				     ||RPAD(NVL(TC_DEMAND_GP, ' '), 1 , ' ') 
				     ||RPAD(NVL(EXTRA_ITEM  , ' '), 13, ' ') 
				     ||RPAD(NVL(EQUIP_CD    , ' '), 4 , ' ') 
				     ||RPAD(NVL(DRV_STAT    , ' '), 1 , ' ') 
				     ||RPAD(NVL(EQP_SATT    , ' '), 1 , ' ') 
				       AS JMS_TC_MESSAGE --JMSTCMessage
				  FROM (
				        SELECT 'MIMH210'   AS JMS_TC_CD  
				             , :V_WORK_EXP AS WORK_EXP   --작업근조
				             , 'I'         AS WORK_PLANT --작업공장( 현대제철 : 'I', HYSCO : 'B')
				             , 'I'         AS WORK_PROC  --작업공정( 현대제철 : 'I', HYSCO : 'H')
				             , '*'         AS TC_COUNT   --전문 발생 count(전문이 3개 생성시 첫번째 1, 두번째 2, 마지막 전문일경우 *)
				             , 'I'         AS TC_DEMAND_GP --전문요구구분
				             , ''          AS EXTRA_ITEM   --여분항목
				             , 'SSX1'      AS EQUIP_CD    --설비코드
				             , NVL(:V_DRV_STAT, 'A') AS DRV_STAT    --운전상태 'A':AUTO, 'M':MANUAL( AUTO Mode를 제외한 운전 Mode )
				             , :V_EQP_SATT AS EQP_SATT    --설비상태 '0' : 고장, '1' : 정상
				          FROM DUAL
				         WHERE 1 = 1
				       )
				 WHERE 1 = 1
				 */
				
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.tcMIMH210";
				
			} else if ("MIMH510".equals(msgId)) {
				
				trtNm = "코일1냉연 코일상세정보";
				/*
				SELECT JMS_TC_CD                                    --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				     , JMS_TC_CD                                 --전문ID
				     ||TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') --발생일자
				     ||RPAD(NVL(WORK_EXP    , ' '), 2 , ' ') 
				     ||RPAD(NVL(WORK_PLANT  , ' '), 1 , ' ') 
				     ||RPAD(NVL(WORK_PROC   , ' '), 1 , ' ') 
				     ||RPAD(NVL(TC_COUNT    , ' '), 1 , ' ') 
				     ||RPAD(NVL(TC_DEMAND_GP, ' '), 1 , ' ') 
				     ||RPAD(NVL(EXTRA_ITEM  , ' '), 13, ' ') 
				     
				     ||RPAD(NVL(CON_NO               , ' '), 10, ' ')
				     ||RPAD(NVL(CON_NO_DTL           , ' '), 5 , ' ')
				     ||RPAD(NVL(COILNO               , ' '), 15, ' ')
				     ||RPAD(NVL(STACK_LOC            , ' '), 10, ' ')
				     ||RPAD(NVL(ORD_T                , ' '), 4 , ' ')
				     ||RPAD(NVL(ORD_W                , ' '), 5 , ' ')
				     ||RPAD(NVL(NET_WT               , ' '), 5 , ' ')
				     ||RPAD(NVL(INDIA                , ' '), 4 , ' ')
				     ||RPAD(NVL(OUTDIA               , ' '), 5 , ' ')
				     ||RPAD(NVL(LEN                  , ' '), 4 , ' ')
				     ||RPAD(NVL(SUPPLY_COM_SPEC      , ' '), 20, ' ')
				     ||RPAD(NVL(HEATNO               , ' '), 15, ' ')
				     ||RPAD(NVL(SUPPLY_COM_ISSUE_DATE, ' '), 8 , ' ')
				     ||RPAD(NVL(MC_YN                , ' '), 1 , ' ')
				     ||RPAD(NVL(COIL_MELT_TEMP       , ' '), 4 , ' ')
				     ||RPAD(NVL(COOL_METHOD          , ' '), 1 , ' ')
				     ||RPAD(NVL(ANNEALING1           , ' '), 4 , ' ')
				     ||RPAD(NVL(ANNEALING2           , ' '), 5 , ' ')
				     ||RPAD(NVL(FORM_INFO1           , ' '), 3 , ' ')
				     ||RPAD(NVL(FORM_INFO2           , ' '), 3 , ' ')
				     ||RPAD(NVL(FORM_INFO3           , ' '), 3 , ' ')
				     ||RPAD(NVL(FORM_INFO4           , ' '), 3 , ' ')
				
				       AS JMS_TC_MESSAGE --JMSTCMessage
				  FROM (
				        SELECT 'MIMH210'   AS JMS_TC_CD  
				             , :V_WORK_EXP AS WORK_EXP   --작업근조
				             , 'I'         AS WORK_PLANT --작업공장( 현대제철 : 'I', HYSCO : 'B')
				             , 'I'         AS WORK_PROC  --작업공정( 현대제철 : 'I', HYSCO : 'H')
				             , '*'         AS TC_COUNT   --전문 발생 count(전문이 3개 생성시 첫번째 1, 두번째 2, 마지막 전문일경우 *)
				             , 'I'         AS TC_DEMAND_GP --전문요구구분
				             , ''          AS EXTRA_ITEM   --여분항목
				             
				             , CON_NO
				             , CON_NO_DTL
				             , COILNO
				             , STACK_LOC
				             , TO_CHAR(ORD_T ) AS ORD_T
				             , TO_CHAR(ORD_W ) AS ORD_W
				             , TO_CHAR(NET_WT) AS NET_WT
				             , TO_CHAR(INDIA ) AS INDIA
				             , TO_CHAR(OUTDIA) AS OUTDIA
				             , TO_CHAR(LEN   ) AS LEN
				             , '' AS SUPPLY_COM_SPEC
				             , HEATNO
				             , SUPPLY_COM_ISSUE_DATE
				             , '' AS MC_YN
				             , '' AS COIL_MELT_TEMP
				             , COOL_METHOD
				             , TO_CHAR(ANNEALING1) AS ANNEALING1
				             , TO_CHAR(ANNEALING2) AS ANNEALING2
				             , '' AS FORM_INFO1
				             , '' AS FORM_INFO2
				             , '' AS FORM_INFO3
				             , '' AS FORM_INFO4
				          FROM (
				                SELECT A.ORD_NO                     AS CON_NO
				                     , '00'||A.ORD_DTL              AS CON_NO_DTL
				                     , A.COIL_NO                    AS COILNO
				                     , NVL(B.ORD_CONV_T, 0) * 1000  AS ORD_T
				                     , NVL(B.ORD_CONV_W, 0) * 10    AS ORD_W
				                     , NVL(A.COIL_WT, 0)            AS NET_WT
				                     , NVL(A.COIL_INDIA, 0) * 25.4  AS INDIA
				                     , NVL(A.COIL_OUTDIA, 0)        AS OUTDIA
				                     , NVL(A.CURR_COIL_LEN, 0)      AS LEN
				                     , TO_CHAR(SYSDATE, 'YYYYMMDD') AS SUPPLY_COM_ISSUE_DATE
				                     , DECODE(A.COOL_METHOD, 'A', '1', 'W', '2') AS COOL_METHOD
				                     , A.HYSCO_TRANS_GP 
				                  FROM USRPTA.TB_PT_COILCOMM  A
				                     , USRPTA.TB_PT_OSCOMM    B 
				                WHERE A.ORD_NO  = B.ORD_NO(+)
				                  AND A.ORD_DTL = B.ORD_DTL(+)
				                  AND A.COIL_NO = :V_COIL_NO          
				               ) A
				             , (
				                SELECT B.BUY_HEAT_NO AS HEATNO
				                     , A.COIL_NO
				                  FROM VW_YD_SLABCOMM    A
				                     , TB_QM_BUYSLABINFO B 
				                 WHERE A.MSLAB_NO = B.MSLAB_NO
				                   AND A.COIL_NO  = :V_COIL_NO             
				               ) B
				             , (
				                SELECT EXT_TEMP     AS ANNEALING1
				                     , TOT_INFUR_HR AS ANNEALING2
				                     , COIL_NO
				                  FROM TB_PO_SLABREHEATFURWRSLT
				                 WHERE COIL_NO = :V_COIL_NO
				                   AND ROWNUM = 1
				                 ORDER BY STEP_NO DESC             
				               ) C
				             , (
				                SELECT CASE WHEN FRTOMOVE_EQUIP_BED_GP = '01' THEN 'B1XSS-X101'    --이송설비번지
				                            WHEN FRTOMOVE_EQUIP_BED_GP = '02' THEN 'B1XSS-X102'
				                            WHEN FRTOMOVE_EQUIP_BED_GP = '03' THEN 'B1XSS-X103'
				                            ELSE NULL
				                        END AS STACK_LOC
				                     , STOCK_ID AS COIL_NO 
				                  FROM TB_YM_STOCK
				                 WHERE STOCK_ID = :V_COIL_NO              
				               ) D
				         WHERE 1 = 1
				           AND A.COILNO = D.COIL_NO
				           AND A.COILNO = B.COIL_NO(+)
				           AND A.COILNO = C.COIL_NO(+)
				       )
				 WHERE 1 = 1
				 */
				
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.tcMIMH510";
			} else if ("CF1BP14".equals(msgId)) {
					
					trtNm = "슬라브 W/B 4,5 정보";
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.tcCF1BP14 

					SELECT NEW_TC_CD AS JMS_TC_CD  --JMSTC코드
					      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시  
					      ,TC_CD  
					     ||TC_Date  
					     ||TC_Time
					     ||TC_ID  
					     ||TC_Length
					     ||RPAD(NVL(SLAB_NO1    , ' '), 12, ' ') 
					     ||RPAD(NVL(SLAB_T1     , ' '),  3, ' ') 
					     ||RPAD(NVL(SLAB_W1     , ' '),  4, ' ') 
					     ||RPAD(NVL(SLAB_LEN1   , ' '),  5, ' ') 
					     ||RPAD(NVL(SLAB_WT1    , ' '),  5, ' ') 
					     ||RPAD(NVL(SLAB_NO2    , ' '), 12, ' ') 
					     ||RPAD(NVL(SLAB_T2     , ' '),  3, ' ') 
					     ||RPAD(NVL(SLAB_W2     , ' '),  4, ' ') 
					     ||RPAD(NVL(SLAB_LEN2   , ' '),  5, ' ') 
					     ||RPAD(NVL(SLAB_WT2    , ' '),  5, ' ') 
					     ||RPAD(NVL(SLAB_NO3    , ' '), 12, ' ') 
					     ||RPAD(NVL(SLAB_T3     , ' '),  3, ' ') 
					     ||RPAD(NVL(SLAB_W3     , ' '),  4, ' ') 
					     ||RPAD(NVL(SLAB_LEN3   , ' '),  5, ' ') 
					     ||RPAD(NVL(SLAB_WT3    , ' '),  5, ' ')
					     ||RPAD(NVL(SLAB_NO4    , ' '), 12, ' ') 
					     ||RPAD(NVL(SLAB_T4     , ' '),  3, ' ') 
					     ||RPAD(NVL(SLAB_W4     , ' '),  4, ' ') 
					     ||RPAD(NVL(SLAB_LEN4   , ' '),  5, ' ') 
					     ||RPAD(NVL(SLAB_WT4    , ' '),  5, ' ') 
					     ||RPAD(NVL(SLAB_NO5    , ' '), 12, ' ') 
					     ||RPAD(NVL(SLAB_T5     , ' '),  3, ' ') 
					     ||RPAD(NVL(SLAB_W5     , ' '),  4, ' ') 
					     ||RPAD(NVL(SLAB_LEN5   , ' '),  5, ' ') 
					     ||RPAD(NVL(SLAB_WT5    , ' '),  5, ' ')  
					     ||RPAD(NVL(SLAB_NO6    , ' '), 12, ' ') 
					     ||RPAD(NVL(SLAB_T6     , ' '),  3, ' ') 
					     ||RPAD(NVL(SLAB_W6     , ' '),  4, ' ') 
					     ||RPAD(NVL(SLAB_LEN6   , ' '),  5, ' ') 
					     ||RPAD(NVL(SLAB_WT6    , ' '),  5, ' ') 
					     ||RPAD(NVL(SLAB_NO7    , ' '), 12, ' ') 
					     ||RPAD(NVL(SLAB_T7     , ' '),  3, ' ') 
					     ||RPAD(NVL(SLAB_W7     , ' '),  4, ' ') 
					     ||RPAD(NVL(SLAB_LEN7   , ' '),  5, ' ') 
					     ||RPAD(NVL(SLAB_WT7    , ' '),  5, ' ')  
					     ||RPAD(NVL(SLAB_NO8    , ' '), 12, ' ') 
					     ||RPAD(NVL(SLAB_T8     , ' '),  3, ' ') 
					     ||RPAD(NVL(SLAB_W8     , ' '),  4, ' ') 
					     ||RPAD(NVL(SLAB_LEN8   , ' '),  5, ' ') 
					     ||RPAD(NVL(SLAB_WT8    , ' '),  5, ' ')  AS JMS_TC_MESSAGE
					FROM 
					     (       SELECT  
					                    'POCFL114' AS NEW_TC_CD
					                   ,'CF1BP14' TC_CD                             --JMSTC코드
					                   ,TO_CHAR(SYSDATE,'YYYY-MM-DD') AS TC_Date   --JMSTC생성일시
					                   ,TO_CHAR(SYSDATE,'HH24-MI-SS') AS TC_Time   --JMSTC생성일시
					                   ,'I'                           AS TC_ID     --전문구분
					                   ,'0254'                        AS TC_Length --길이 
					                   
					                   ,MAX(DECODE(CNT,1,STOCK_ID,''))   AS SLAB_NO1
					                   ,MAX(DECODE(CNT,1,SLAB_T,  ''))   AS SLAB_T1
					                   ,MAX(DECODE(CNT,1,SLAB_W,  ''))   AS SLAB_W1 
					                   ,MAX(DECODE(CNT,1,SLAB_LEN,''))   AS SLAB_LEN1  
					                   ,MAX(DECODE(CNT,1,SLAB_WT, ''))   AS SLAB_WT1 
					                   ,MAX(DECODE(CNT,2,STOCK_ID,''))   AS SLAB_NO2
					                   ,MAX(DECODE(CNT,2,SLAB_T,  ''))   AS SLAB_T2
					                   ,MAX(DECODE(CNT,2,SLAB_W,  ''))   AS SLAB_W2 
					                   ,MAX(DECODE(CNT,2,SLAB_LEN,''))   AS SLAB_LEN2 
					                   ,MAX(DECODE(CNT,2,SLAB_WT, ''))   AS SLAB_WT2
					                   ,MAX(DECODE(CNT,3,STOCK_ID,''))   AS SLAB_NO3
					                   ,MAX(DECODE(CNT,3,SLAB_T,  ''))   AS SLAB_T3
					                   ,MAX(DECODE(CNT,3,SLAB_W,  ''))   AS SLAB_W3 
					                   ,MAX(DECODE(CNT,3,SLAB_LEN,''))   AS SLAB_LEN3  
					                   ,MAX(DECODE(CNT,3,SLAB_WT, ''))   AS SLAB_WT3  
					                   ,MAX(DECODE(CNT,4,STOCK_ID,''))   AS SLAB_NO4
					                   ,MAX(DECODE(CNT,4,SLAB_T,  ''))   AS SLAB_T4
					                   ,MAX(DECODE(CNT,4,SLAB_W,  ''))   AS SLAB_W4 
					                   ,MAX(DECODE(CNT,4,SLAB_LEN,''))   AS SLAB_LEN4  
					                   ,MAX(DECODE(CNT,4,SLAB_WT, ''))   AS SLAB_WT4 
					                   ,MAX(DECODE(CNT,5,STOCK_ID,''))   AS SLAB_NO5
					                   ,MAX(DECODE(CNT,5,SLAB_T,  ''))   AS SLAB_T5
					                   ,MAX(DECODE(CNT,5,SLAB_W,  ''))   AS SLAB_W5 
					                   ,MAX(DECODE(CNT,5,SLAB_LEN,''))   AS SLAB_LEN5  
					                   ,MAX(DECODE(CNT,5,SLAB_WT, ''))   AS SLAB_WT5 
					                   ,MAX(DECODE(CNT,6,STOCK_ID,''))   AS SLAB_NO6
					                   ,MAX(DECODE(CNT,6,SLAB_T,  ''))   AS SLAB_T6
					                   ,MAX(DECODE(CNT,6,SLAB_W,  ''))   AS SLAB_W6 
					                   ,MAX(DECODE(CNT,6,SLAB_LEN,''))   AS SLAB_LEN6 
					                   ,MAX(DECODE(CNT,6,SLAB_WT, ''))   AS SLAB_WT6
					                   ,MAX(DECODE(CNT,7,STOCK_ID,''))   AS SLAB_NO7
					                   ,MAX(DECODE(CNT,7,SLAB_T,  ''))   AS SLAB_T7
					                   ,MAX(DECODE(CNT,7,SLAB_W,  ''))   AS SLAB_W7 
					                   ,MAX(DECODE(CNT,7,SLAB_LEN,''))   AS SLAB_LEN7  
					                   ,MAX(DECODE(CNT,7,SLAB_WT, ''))   AS SLAB_WT7  
					                   ,MAX(DECODE(CNT,8,STOCK_ID,''))   AS SLAB_NO8
					                   ,MAX(DECODE(CNT,8,SLAB_T,  ''))   AS SLAB_T8
					                   ,MAX(DECODE(CNT,8,SLAB_W,  ''))   AS SLAB_W8 
					                   ,MAX(DECODE(CNT,8,SLAB_LEN,''))   AS SLAB_LEN8  
					                   ,MAX(DECODE(CNT,8,SLAB_WT, ''))   AS SLAB_WT8        
					             FROM 
					                (SELECT
					                     A.STACK_COL_GP
					                    ,ROW_NUMBER() OVER(ORDER BY A.STACK_BED_GP DESC) AS CNT        
					                    ,A.STOCK_ID  
					                    ,TO_CHAR(NVL(B.SLAB_T,0))   AS SLAB_T  
					                    ,TO_CHAR(NVL(B.SLAB_W,0))   AS SLAB_W  
					                    ,TO_CHAR(NVL(B.SLAB_LEN,0)) AS SLAB_LEN  
					                    ,TO_CHAR(NVL(B.SLAB_WT,0))  AS SLAB_WT  
					                FROM 
					                     TB_YM_STACKLAYER A
					                    ,VW_YD_SLABCOMM B 
					                WHERE A.STACK_COL_GP = '2CWB01'
					                  AND (A.STACK_BED_GP ='04' OR  A.STACK_BED_GP = '05') 
					                  AND A.STOCK_ID = B.SLAB_NO(+)
					             ORDER BY A.STACK_BED_GP DESC) TEMP 
					             GROUP BY STACK_COL_GP)  TEMP */
					jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.tcCF1BP14";
			} else if ("CF1BP03".equals(msgId)) {
					
					trtNm = "슬라브 Line Off 완료실적";
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.tcCF1BP03 
					SELECT NEW_TC_CD AS JMS_TC_CD  --JMSTC코드
					      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시 
					      ,TC_CD  
					     ||TC_Date  
					     ||TC_Time
					     ||TC_ID  
					     ||TC_Length
					     ||RPAD(NVL(SLAB_NO    , ' '), 12, ' ') --SLAB_NO(11)+SPACE(1) = 12
					     AS JMS_TC_MESSAGE --JMSTCMessage
					  FROM (
					        SELECT  'POCFL103' AS NEW_TC_CD
					               ,'CF1BP03' AS TC_CD  
					               ,TO_CHAR(SYSDATE,'YYYY-MM-DD') AS TC_Date   --JMSTC생성일시
						           ,TO_CHAR(SYSDATE,'HH24-MI-SS') AS TC_Time   --JMSTC생성일시
						           ,'I'                           AS TC_ID     --전문구분
						           ,'0012'                        AS TC_Length --길이
					               ,:V_SLAB_NO                    AS SLAB_NO      
					          FROM DUAL
					         WHERE 1 = 1
					       )
					 WHERE 1 = 1 */
					jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.tcCF1BP03";
			}  else if ("CF1BP12".equals(msgId)) {
				
				trtNm = "CTC 권하완료실적 ";
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.tcCF1BP12 
				SELECT NEW_TC_CD AS JMS_TC_CD  --JMSTC코드
			          ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
			          ,TC_CD  
				     ||TC_Date  
				     ||TC_Time
				     ||TC_ID  
				     ||TC_Length
				     ||RPAD(NVL(SLAB_NO     , ' '), 12, ' ') --SLAB_NO(11)+SPACE(1) = 12
				     ||RPAD(NVL(POSITION    , ' '), 1, ' ') 
				     AS JMS_TC_MESSAGE --JMSTCMessage
				  FROM (
				        SELECT    'POCFL112' AS NEW_TC_CD
			                     ,'CF1BP12' AS TC_CD  
				                 ,TO_CHAR(SYSDATE,'YYYY-MM-DD') AS TC_Date   --JMSTC생성일시
					             ,TO_CHAR(SYSDATE,'HH24-MI-SS') AS TC_Time   --JMSTC생성일시
					             ,'I'                           AS TC_ID     --전문구분
					             ,'0013'                        AS TC_Length --길이
				                 ,:V_SLAB_NO                    AS SLAB_NO      
				                 ,CASE WHEN SUBSTR(:V_POSITION,1,6) ='2ACT01' THEN '1'
				                       WHEN SUBSTR(:V_POSITION,1,6) ='2ACT02' THEN '2'
				                       WHEN SUBSTR(:V_POSITION,1,6) ='2BCT03' THEN '3'
				                       WHEN SUBSTR(:V_POSITION,1,6) ='2CCT04' THEN '4'
				                       ELSE '' END POSITION  
				                  
				          FROM DUAL
				         WHERE 1 = 1
				       )
				 WHERE 1 = 1 */
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.tcCF1BP12";
			} else if("YMA8L004".equals(msgId)) {
				
		    	trtNm = "B열연 SLAB 작업지시";
		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMA8L004
		    	WITH TEMP_CRN_SCH_ID AS 
		    	( SELECT *
		    	    FROM  (
		    	            SELECT LEAD(YD_CRN_SCH_ID) OVER (ORDER BY YD_CRN_SCH_ID)  AS YD_CRN_SCH_ID_NEXT 
		    	                 , A.*
		    	              FROM TB_YM_CRNSCH A
		    	             WHERE YD_EQP_ID  = (SELECT YD_EQP_ID 
		    	                                   FROM TB_YM_CRNSCH 
		    	                                  WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID )
		    	               AND DEL_YN = 'N'                                                    
		    	            ORDER BY YD_CRN_SCH_ID                         
		    	          )
		    	          WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
		    	) 

		    	SELECT 'YMA8L004'                          AS JMS_TC_CD          --JMSTC코드
		    	      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
		    	      ,'YMA8L004'                                     --전문ID
		    	     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')         --생성일시
		    	     ||NVL(:V_MSG_GP,'I')                              --전문구분
		    	     ||'0460'                                          --전문길이
		    	     ||RPAD(' ',29,' ')                                --임시
		    	     ||RPAD(NVL(YD_EQP_ID                                   ,' '),6 ,' ') --야드설비ID                 
		    	     ||RPAD(NVL(YD_WRK_PROG_STAT                            ,' '),1 ,' ') --야드작업진행상태           
		    	     ||RPAD(NVL(YD_SCH_CD                                   ,' '),8 ,' ') --야드스케쥴코드             
		    	     ||RPAD(NVL(YD_SCH_NAME                                 ,' '),30,' ') --야드스케줄명               
		    	     ||RPAD(NVL(YD_CRN_SCH_ID                               ,' '),18,' ') --야드크레인스케쥴ID         
		    	     ||RPAD(NVL(YD_CRN_SCH_RMD_CNT                          ,' '),2 ,' ') --야드크레인스케줄잔여회수   
		    	     ||LPAD(NVL(TO_CHAR(WORK_ORD_NUMSHEET      ,'FM00'     ),'0'),2 ,'0') --작업지시 매수   	     
		    	     
		    	     ||RPAD(NVL(YD_UP_WO_LOC                                ,' '),8 ,' ') --야드권상지시위치           
		    	     ||LPAD(NVL(YD_UP_WO_LAYER                              ,'0'),3 ,'0') --야드권상지시단 
		    	     ||LPAD(NVL(TO_CHAR(YD_UP_WO_LOC_XAXIS     ,'FM0000000'),'0'),7 ,'0') --야드권상지시X축            
		    	     ||LPAD(NVL(TO_CHAR(YD_UP_WO_XAXIS_GAP_MAX ,'FM00000'  ),'0'),5 ,'0') --야드권상지시X축오차최대    
		    	     ||LPAD(NVL(TO_CHAR(YD_UP_WO_XAXIS_GAP_MIN ,'FM00000'  ),'0'),5 ,'0') --야드권상지시X축오차최소    
		    	     ||LPAD(NVL(TO_CHAR(YD_UP_WO_LOC_YAXIS     ,'FM00000'  ),'0'),5 ,'0') --야드권상지시Y축            
		    	     ||LPAD(NVL(TO_CHAR(YD_UP_WO_YAXIS_GAP_MAX ,'FM00000'  ),'0'),5 ,'0') --야드권상지시Y축오차최대    
		    	     ||LPAD(NVL(TO_CHAR(YD_UP_WO_YAXIS_GAP_MIN ,'FM00000'  ),'0'),5 ,'0') --야드권상지시Y축오차최소   
		    	     ||RPAD(NVL(YD_UP_WO_LOC_ZAXIS_SYM                      ,' '),1 ,' ') --야드권상지시Z축 부호   	      
		    	     ||LPAD(NVL(TO_CHAR(YD_UP_WO_LOC_ZAXIS     ,'FM00000'  ),'0'),5 ,'0') --야드권상지시Z축            
		    	     ||LPAD(NVL(TO_CHAR(YD_UP_WO_ZAXIS_GAP_MAX ,'FM00000'  ),'0'),5 ,'0') --야드권상지시Z축오차최대    
		    	     ||LPAD(NVL(TO_CHAR(YD_UP_WO_ZAXIS_GAP_MIN ,'FM00000'  ),'0'),5 ,'0') --야드권상지시Z축오차최소  
		    	     ||LPAD(NVL(TO_CHAR(UP_ROTATION_ANGLE      ,'FM0'      ),'0'),1 ,'0') --권상위치 회전각도             
		    	     
		    	     ||RPAD(NVL(YD_DN_WO_LOC                                ,' '),8 ,' ') --야드권하지시위치           
		    	     ||LPAD(NVL(YD_DN_WO_LAYER                              ,'0'),3 ,'0') --야드권하지시단             
		    	     ||LPAD(NVL(TO_CHAR(YD_DN_WO_LOC_XAXIS     ,'FM0000000'),'0'),7 ,'0') --야드권하지시X축            
		    	     ||LPAD(NVL(TO_CHAR(YD_DN_WO_XAXIS_GAP_MAX ,'FM00000'  ),'0'),5 ,'0') --야드권하지시X축오차최대    
		    	     ||LPAD(NVL(TO_CHAR(YD_DN_WO_XAXIS_GAP_MIN ,'FM00000'  ),'0'),5 ,'0') --야드권하지시X축오차최소    
		    	     ||LPAD(NVL(TO_CHAR(YD_DN_WO_LOC_YAXIS     ,'FM00000'  ),'0'),5 ,'0') --야드권하지시Y축            
		    	     ||LPAD(NVL(TO_CHAR(YD_DN_WO_YAXIS_GAP_MAX ,'FM00000'  ),'0'),5 ,'0') --야드권하지시Y축오차최대    
		    	     ||LPAD(NVL(TO_CHAR(YD_DN_WO_YAXIS_GAP_MIN ,'FM00000'  ),'0'),5 ,'0') --야드권하지시Y축오차최소    
		    	     ||RPAD(NVL(YD_DN_WO_LOC_ZAXIS_SYM                      ,' '),1 ,' ') --야드권하지시Z축 부호 
		    	     ||LPAD(NVL(TO_CHAR(YD_DN_WO_LOC_ZAXIS     ,'FM00000'  ),'0'),5 ,'0') --야드권하지시Z축            
		    	     ||LPAD(NVL(TO_CHAR(YD_DN_WO_ZAXIS_GAP_MAX ,'FM00000'  ),'0'),5 ,'0') --야드권하지시Z축오차최대    
		    	     ||LPAD(NVL(TO_CHAR(YD_DN_WO_ZAXIS_GAP_MIN ,'FM00000'  ),'0'),5 ,'0') --야드권하지시Z축오차최소
		    	     ||RPAD(NVL(TO_CHAR(DOWN_ROTATION_ANGLE    ,'FM0'      ),'0'),1 ,'0') --권하위치 회전각도 
		    	     
		    	     ||RPAD(NVL(GRUSH_COIL_YN                               ,' '),1 ,' ') --짱구코일 여부              
		    	     ||RPAD(NVL(SPM_HFL_YN                                  ,' '),1 ,' ') --SPM/HFL  유무              
		    	     ||RPAD(NVL(CUT_YN                                      ,' '),1 ,' ') --절단재 여부                
		    	     ||RPAD(NVL(YD_EQP_ID2                                  ,' '),6 ,' ') --야드설비ID2                
		    	     ||RPAD(NVL(YD_TC_AIM_BAY_GP                            ,' '),1 ,' ') --야드대차목적동             
		    	     ||RPAD(NVL(YD_CAR_USE_GP                               ,' '),1 ,' ') --야드차량사용구분           
		    	     ||RPAD(NVL(CAR_NO                                      ,' '),15,' ') --차량번호                   
		    	     ||RPAD(NVL(TRN_EQP_CD                                  ,' '),8 ,' ') --운송장비코드  
		    	     ||RPAD(NVL(CARD_NO                                     ,' '),4 ,' ') --카드번호 
		    	     ||RPAD(NVL(TO_CHAR(YD_EQP_WRK_SH          ,'FM00'     ),'00'),2,' ') --야드설비작업매수  
		    	     ||RPAD(NVL(TO_CHAR(YD_EQP_RMN_SH          ,'FM00'     ),'00'),2,' ') --야드설비잔량매수      
		    	     ||RPAD(NVL(STOCK_ID1                                   ,' '),11,' ') --재료번호1                   
		    	     ||LPAD(NVL(TO_CHAR(YD_STL_WT1             ,'FM00000'  ),'0'),5 ,'0') --야드재료중량1              
		    	     ||LPAD(NVL(TO_CHAR(YD_STL_T1              ,'FM000V000'),'0'),6 ,'0') --야드재료두께1              
		    	     ||LPAD(NVL(TO_CHAR(YD_STL_W1              ,'FM0000V0' ),'0'),5 ,'0') --야드재료폭1                
		    	     ||LPAD(NVL(TO_CHAR(YD_STL_L1              ,'FM0000000'),'0'),7 ,'0') --야드재료길이1
		    	     ||RPAD(NVL(WORK_ORD_SEQNO1                             ,' '),1 ,' ') --슬라브1 Hot 소재여부       
		    	     ||RPAD(NVL(TREAT_GP1                                   ,' '),1 ,' ') --고강도 강종여부1           
		    	     ||RPAD(NVL(CHARGE_SEQNO1                               ,' '),10,' ') --장입순위1  
		    	     ||RPAD(NVL(STOCK_ID2                                   ,' '),11,' ') --재료번호2                  
		    	     ||LPAD(NVL(TO_CHAR(YD_STL_WT2             ,'FM00000'  ),'0'),5 ,'0') --야드재료중량2              
		    	     ||LPAD(NVL(TO_CHAR(YD_STL_T2              ,'FM000V000'),'0'),6 ,'0') --야드재료두께2              
		    	     ||LPAD(NVL(TO_CHAR(YD_STL_W2              ,'FM0000V0' ),'0'),5 ,'0') --야드재료폭2                
		    	     ||LPAD(NVL(TO_CHAR(YD_STL_L2              ,'FM0000000'),'0'),7 ,'0') --야드재료길이2 
		    	     ||RPAD(NVL(WORK_ORD_SEQNO2                             ,' '),1 ,' ') --슬라브2 Hot 소재여부       
		    	     ||RPAD(NVL(TREAT_GP2                                   ,' '),1 ,' ') --고강도 강종여부2           
		    	     ||RPAD(NVL(CHARGE_SEQNO2                               ,' '),10,' ') --장입순위2
		    	     ||RPAD(NVL(YD_SCH_CD_NEXT                              ,' '),8 ,' ') --야드스케쥴코드_Next        
		    	     ||RPAD(NVL(YD_SCH_NAME_NEXT                            ,' '),30,' ') --야드스케줄명_NEXT          
		    	     ||RPAD(NVL(YD_UP_WO_LOC_NEXT                           ,' '),8 ,' ') --야드권상지시위치_Next      
		    	     ||LPAD(NVL(YD_UP_WO_LAYER_NEXT                         ,'0'),3 ,'0') --야드권상지시단_Next        
		    	     ||RPAD(NVL(YD_DN_WO_LOC_NEXT                           ,' '),8 ,' ') --야드권하지시위치_Next      
		    	     ||LPAD(NVL(YD_DN_WO_LAYER_NEXT                         ,'0'),3 ,'0') --야드권하지시단_Next        
		    	     ||RPAD(NVL(STOCK_ID_NEXT                               ,' '),11,' ') --재료번호_Next              
		    	     ||LPAD(NVL(TO_CHAR(YD_CRN_WRK_WT_NEXT     ,'FM0000000'),'0'),7 ,'0') --야드크레인작업중량_Next    
		    	     ||LPAD(NVL(TO_CHAR(YD_CRN_WRK_T_NEXT      ,'FM000V000'),'0'),6 ,'0') --야드크레인작업총두께_Next  
		    	     ||LPAD(NVL(TO_CHAR(YD_CRN_WRK_MAX_W_NEXT  ,'FM0000V0' ),'0'),5 ,'0') --야드크레인작업최대폭_Next  
		    	     ||LPAD(NVL(TO_CHAR(COIL_OUTDIA_NEXT       ,'FM00000'  ),'0'),5 ,'0') --야드크레인작업코일외경_Next
		    	     ||LPAD(NVL(TO_CHAR(COIL_INDIA_NEXT        ,'FM0000V0' ),'0'),5 ,'0') --야드크레인작업코일내경_Next
		    	     ||RPAD(NVL(STOCK_ID_NEXT2                              ,' '),11,' ') --재료번호2_Next
		    	     ||LPAD(NVL(TO_CHAR(YD_CRN_WRK_WT_NEXT2    ,'FM0000000'),'0'),7 ,'0') --야드크레인작업중량2_Next    
		    	     ||LPAD(NVL(TO_CHAR(YD_CRN_WRK_T_NEXT2     ,'FM000V000'),'0'),6 ,'0') --야드크레인작업총두께2_Next  
		    	     ||LPAD(NVL(TO_CHAR(YD_CRN_WRK_MAX_W_NEXT2 ,'FM0000V0' ),'0'),5 ,'0') --야드크레인작업최대폭2_Next  
		    	     ||LPAD(NVL(TO_CHAR(COIL_OUTDIA_NEXT2      ,'FM00000'  ),'0'),5 ,'0') --야드크레인작업코일외경2_Next
		    	     ||LPAD(NVL(TO_CHAR(COIL_INDIA_NEXT2       ,'FM0000V0' ),'0'),5 ,'0') --야드크레인작업코일내경2_Next
		    	     ||RPAD(NVL(LAST_WORK_ORD_GP                            ,' '),1 ,' ') --마지막 지시 구분
		    	     
		    	     AS JMS_TC_MESSAGE --JMSTCMessage
		    	     , LPAD(NVL(TO_CHAR(YD_UP_WO_LOC_ZAXIS     ,'FM00000'  ),'0'),5 ,'0') AS YD_UP_WO_LOC_ZAXIS--야드권상지시Z축            
		    	     , LPAD(NVL(TO_CHAR(YD_DN_WO_LOC_ZAXIS     ,'FM00000'  ),'0'),5 ,'0') AS YD_DN_WO_LOC_ZAXIS--야드권하지시Z축            

		    	  FROM (
		    	        SELECT K.*
		    	          FROM 
		    	              (         

		    	            SELECT /*+ INDEX(B PK_YM_STACKLAYER )
		    	                   CUR_INFO.YD_EQP_ID                AS YD_EQP_ID                 --야드설비ID
		    	                 , CASE WHEN CUR_INFO.YD_L2_REQUEST_STAT = 'D' THEN '1'
		    	                        WHEN CUR_INFO.YD_L2_REQUEST_STAT = 'X' THEN 'D'
		    	                        WHEN CUR_INFO.YD_L2_REQUEST_STAT = '5' THEN '5'
		    	                        ELSE DECODE(CUR_INFO.YD_WRK_PROG_STAT,'S','1','W','1',CUR_INFO.YD_WRK_PROG_STAT)  END
		    	                                                     AS YD_WRK_PROG_STAT          --야드작업진행상태
		    	                 , CUR_INFO.YD_SCH_CD                AS YD_SCH_CD                 --야드스케쥴코드
		    	                 , SUBSTRB((SELECT CD_CONTENTS
		    	                              FROM USRYMA.TB_YM_SCHEDULERULE WHERE YD_SCH_CD = CUR_INFO.YD_SCH_CD)
		    	                          || (CASE WHEN CUR_INFO.YD_AID_WRK_YN='N' THEN '[주]' ELSE '[보조]' END),1,30) AS YD_SCH_NAME
		    	                 , CUR_INFO.YD_CRN_SCH_ID            AS YD_CRN_SCH_ID             --야드크레인스케쥴ID
		    	                 , DECODE(CUR_INFO.YD_SCH_PRIOR,0,'Y','N')||CUR_INFO.CURR_PROG_CD
		    	                                                     AS YD_CRN_SCH_RMD_CNT        --야드크레인스케줄잔여회수
		    	                 , CASE WHEN CUR_INFO.STOCK_ID2 IS NOT NULL THEN '2' ELSE '1' END
		    	                                                     AS WORK_ORD_NUMSHEET         --작업지시매수

		    	                 , CUR_INFO.YD_UP_WO_LOC             AS YD_UP_WO_LOC              --야드권상지시위치
		    	                 , CUR_INFO.YD_UP_WO_LAYER           AS YD_UP_WO_LAYER            --야드권상지시단
		    	--                 , CUR_INFO.YD_UP_WO_LOC_XAXIS       AS YD_UP_WO_LOC_XAXIS        --야드권상지시X축

		    	                 -- D동이송하차/C동 1번대차하차 는 형상값
		    	                 -- 형상 사용구분이 'Y' 이고 형상값이 들어왔는지 CHECK
		    	                 , CASE WHEN CUR_INFO.YD_SCH_CD = '2DPT02LM'  AND CUR_INFO.FACE_USE_YN = 'Y' AND  CUR_INFO.FACE_GP  = 'PT' THEN
		    	                             CASE WHEN CUR_INFO.YD_EQP_WRK_SH = 1 THEN WGT_CENTER_XAXIS1
		    	                                  WHEN CUR_INFO.YD_EQP_WRK_SH = 2 THEN WGT_CENTER_XAXIS2  END
		    	                        WHEN CUR_INFO.YD_SCH_CD = '2DPT02LM'  AND CUR_INFO.FACE_USE_YN = 'Y' AND CUR_INFO.FACE_GP != 'PT' THEN 0        -- 형상값 없음         

		    	                        WHEN SUBSTR(CUR_INFO.YD_UP_WO_LOC,1,5) = '2CTC1' AND CUR_INFO.FACE_USE_YN = 'Y' AND CUR_INFO.FACE_GP  = 'TC'  THEN
		    	                             CASE WHEN CUR_INFO.YD_EQP_WRK_SH = 1 THEN WGT_CENTER_XAXIS1
		    	                                  WHEN CUR_INFO.YD_EQP_WRK_SH = 2 THEN WGT_CENTER_XAXIS2  END     
		    	                        WHEN SUBSTR(CUR_INFO.YD_UP_WO_LOC,1,5) = '2CTC1' AND CUR_INFO.FACE_USE_YN = 'Y' AND CUR_INFO.FACE_GP != 'TC' THEN 0
		    	                        ELSE CUR_INFO.YD_UP_WO_LOC_XAXIS 
		    	                        END                          AS YD_UP_WO_LOC_XAXIS        --야드권상지시X축

		    	                 , CUR_INFO.YD_UP_WO_XAXIS_GAP_MAX   AS YD_UP_WO_XAXIS_GAP_MAX    --야드권상지시X축오차최대
		    	                 , CUR_INFO.YD_UP_WO_XAXIS_GAP_MIN   AS YD_UP_WO_XAXIS_GAP_MIN    --야드권상지시X축오차최소

		    	                 -- D동이송하차/C동 1번대차하차 는 형상값
		    	                 , CASE WHEN CUR_INFO.YD_SCH_CD = '2DPT02LM'  AND CUR_INFO.FACE_USE_YN = 'Y' AND CUR_INFO.FACE_GP != 'PT' THEN 0
		    	                        WHEN CUR_INFO.YD_SCH_CD = '2DPT02LM'  AND CUR_INFO.FACE_USE_YN = 'Y' AND CUR_INFO.FACE_GP  = 'PT' THEN
		    	                             CASE WHEN CUR_INFO.YD_EQP_WRK_SH = 1 THEN WGT_CENTER_YAXIS1
		    	                                  WHEN CUR_INFO.YD_EQP_WRK_SH = 2 THEN WGT_CENTER_YAXIS2  END
		    	                        
		    	                        WHEN SUBSTR(CUR_INFO.YD_UP_WO_LOC,1,5) = '2CTC1' AND CUR_INFO.FACE_USE_YN = 'Y' AND CUR_INFO.FACE_GP != 'TC' THEN 0
		    	                        WHEN SUBSTR(CUR_INFO.YD_UP_WO_LOC,1,5) = '2CTC1' AND CUR_INFO.FACE_USE_YN = 'Y' AND CUR_INFO.FACE_GP  = 'TC' THEN
		    	                             CASE WHEN CUR_INFO.YD_EQP_WRK_SH = 1 THEN WGT_CENTER_YAXIS1
		    	                                  WHEN CUR_INFO.YD_EQP_WRK_SH = 2 THEN WGT_CENTER_YAXIS2  END 
		    	                        ELSE CUR_INFO.YD_UP_WO_LOC_YAXIS 
		    	                        END                          AS YD_UP_WO_LOC_YAXIS        --야드권상지시Y축

		    	                 , CUR_INFO.YD_UP_WO_YAXIS_GAP_MAX   AS YD_UP_WO_YAXIS_GAP_MAX    --야드권상지시Y축오차최대
		    	                 , CUR_INFO.YD_UP_WO_YAXIS_GAP_MIN   AS YD_UP_WO_YAXIS_GAP_MIN    --야드권상지시Y축오차최소

		    	                 -- 야드권상지시Z축
		    	                 -- 이송일 경우 
		    	                     -- 1매인 경우 기준높이 + 1번재재료 형상값 + 1번재재료 밴딩값 - (1번재 재료두께 * 0.6)
		    	                     -- 2매인 경우 기준높이 + 2번재재료 형상값 + 2번재재료 밴딩값 - (1번재 재료두께 * 0.6)
		    	                 -- 이송이 아닌 경우
		    	                     -- 1매인 경우 기준높이 + 재료두께합                          - (1번재 재료두께 * 0.6)
		    	                     -- 2매인 경우 기준높이 + 재료두께합       - 1번재 재료두께   - (2번재 재료두께 * 0.6)
		    	                 , CASE WHEN YD_UP_WO_LOC_ZAXIS  > 10 THEN YD_UP_WO_LOC_ZAXIS
		    	                        WHEN CUR_INFO.YD_SCH_CD = '2DPT02LM'  AND CUR_INFO.FACE_USE_YN = 'Y' AND CUR_INFO.FACE_GP != 'PT' THEN 0
		    	                        WHEN CUR_INFO.YD_SCH_CD = '2DPT02LM'  AND CUR_INFO.FACE_USE_YN = 'Y' AND CUR_INFO.FACE_GP  = 'PT' THEN
		    	                             CASE WHEN CUR_INFO.YD_EQP_WRK_SH = 1  AND BENDING_GP1 = '+' THEN
		    	                                       CUR_INFO.UP_BASE_SLAB_T + NVL(CUR_INFO.WGT_CENTER_ZAXIS1,0) + NVL(BENDING_AXIS1,0) - (CUR_INFO.YD_MTL_T1 * 0.6)
		    	                                  WHEN CUR_INFO.YD_EQP_WRK_SH = 1  AND BENDING_GP1 = '-' THEN
		    	                                       CUR_INFO.UP_BASE_SLAB_T + NVL(CUR_INFO.WGT_CENTER_ZAXIS1,0) - NVL(BENDING_AXIS1,0) - (CUR_INFO.YD_MTL_T1 * 0.6)
		    	                                  WHEN CUR_INFO.YD_EQP_WRK_SH = 2  AND BENDING_GP2 = '+' THEN
		    	                                       CUR_INFO.UP_BASE_SLAB_T + NVL(CUR_INFO.WGT_CENTER_ZAXIS2,0) + NVL(BENDING_AXIS2,0) - (CUR_INFO.YD_MTL_T2 * 0.6)
		    	                                  WHEN CUR_INFO.YD_EQP_WRK_SH = 2  AND BENDING_GP2 = '-' THEN
		    	                                       CUR_INFO.UP_BASE_SLAB_T + NVL(CUR_INFO.WGT_CENTER_ZAXIS2,0) - NVL(BENDING_AXIS2,0) - (CUR_INFO.YD_MTL_T2 * 0.6)
		    	                                  END 
		    	                        WHEN SUBSTR(CUR_INFO.YD_UP_WO_LOC,1,5) = '2CTC1' AND CUR_INFO.FACE_USE_YN = 'Y' AND CUR_INFO.FACE_GP != 'TC' THEN 0     
		    	                        WHEN SUBSTR(CUR_INFO.YD_UP_WO_LOC,1,5) = '2CTC1' AND CUR_INFO.FACE_USE_YN = 'Y' AND CUR_INFO.FACE_GP  = 'TC' THEN
		    	                             CASE WHEN CUR_INFO.YD_EQP_WRK_SH = 1  AND BENDING_GP1 = '+' THEN
		    	                                       CUR_INFO.UP_BASE_SLAB_T + NVL(CUR_INFO.WGT_CENTER_ZAXIS1,0) + NVL(BENDING_AXIS1,0) - (CUR_INFO.YD_MTL_T1 * 0.6)
		    	                                  WHEN CUR_INFO.YD_EQP_WRK_SH = 1  AND BENDING_GP1 = '-' THEN
		    	                                       CUR_INFO.UP_BASE_SLAB_T + NVL(CUR_INFO.WGT_CENTER_ZAXIS1,0) - NVL(BENDING_AXIS1,0) - (CUR_INFO.YD_MTL_T1 * 0.6)
		    	                                  WHEN CUR_INFO.YD_EQP_WRK_SH = 2  AND BENDING_GP2 = '+' THEN
		    	                                       CUR_INFO.UP_BASE_SLAB_T + NVL(CUR_INFO.WGT_CENTER_ZAXIS2,0) + NVL(BENDING_AXIS2,0) - (CUR_INFO.YD_MTL_T2 * 0.6)
		    	                                  WHEN CUR_INFO.YD_EQP_WRK_SH = 2  AND BENDING_GP2 = '-' THEN
		    	                                       CUR_INFO.UP_BASE_SLAB_T + NVL(CUR_INFO.WGT_CENTER_ZAXIS2,0) - NVL(BENDING_AXIS2,0) - (CUR_INFO.YD_MTL_T2 * 0.6)
		    	                                  END 
		    	                        
		    	                        
		    	                        ELSE     
		    	                             CASE WHEN CUR_INFO.YD_EQP_WRK_SH = 1 
		    	                                       THEN (CUR_INFO.UP_BASE_SLAB_T + CUR_INFO.UP_MTL_SLAB_T)                      - (CUR_INFO.YD_MTL_T1 * 0.6)
		    	                                  WHEN CUR_INFO.YD_EQP_WRK_SH = 2 
		    	                                       THEN (CUR_INFO.UP_BASE_SLAB_T + CUR_INFO.UP_MTL_SLAB_T) - CUR_INFO.YD_MTL_T1 - (CUR_INFO.YD_MTL_T2 * 0.6)
		    	                                  END 
		    	                  END                                AS YD_UP_WO_LOC_ZAXIS         

		    	                         

		    	                 ----
		    	                 , CUR_INFO.YD_UP_WO_ZAXIS_GAP_MAX   AS YD_UP_WO_ZAXIS_GAP_MAX    --야드권상지시Z축오차최대
		    	                 , CUR_INFO.YD_UP_WO_ZAXIS_GAP_MIN   AS YD_UP_WO_ZAXIS_GAP_MIN    --야드권상지시Z축오차최소

		    	                 , CASE WHEN CUR_INFO.YD_L2_REQUEST_STAT='5'
		    	                        THEN CUR_INFO.YD_DN_WO_LOC_TO ELSE CUR_INFO.YD_DN_WO_LOC   END
		    	                                                     AS YD_DN_WO_LOC              --야드권하지시위치
		    	                 , CASE WHEN CUR_INFO.YD_L2_REQUEST_STAT='5'
		    	                        THEN CUR_INFO.STK_LYR_NO_TEMP ELSE CUR_INFO.YD_DN_WO_LAYER END
		    	                                                     AS YD_DN_WO_LAYER            --야드권하지시단
		    	                 , CASE WHEN CUR_INFO.YD_L2_REQUEST_STAT='5'
		    	                        THEN B.STACK_LAYER_X_AXIS ELSE CUR_INFO.YD_DN_WO_LOC_XAXIS END
		    	                                                     AS YD_DN_WO_LOC_XAXIS        --야드권하지시X축
		    	                 , CUR_INFO.YD_DN_WO_XAXIS_GAP_MAX   AS YD_DN_WO_XAXIS_GAP_MAX    --야드권하지시X축오차최대
		    	                 , CUR_INFO.YD_DN_WO_XAXIS_GAP_MIN   AS YD_DN_WO_XAXIS_GAP_MIN    --야드권하지시X축오차최소

		    	                 , ROUND(CASE WHEN CUR_INFO.YD_L2_REQUEST_STAT = '5' THEN B.STACK_LAYER_Y_AXIS
		    	                              ELSE CUR_INFO.YD_DN_WO_LOC_YAXIS
		    	                              END )                  AS YD_DN_WO_LOC_YAXIS        --야드권하지시Y축
		    	                 , CUR_INFO.YD_DN_WO_YAXIS_GAP_MAX   AS YD_DN_WO_YAXIS_GAP_MAX    --야드권하지시Y축오차최대
		    	                 , CUR_INFO.YD_DN_WO_YAXIS_GAP_MIN   AS YD_DN_WO_YAXIS_GAP_MIN    --야드권하지시Y축오차최소

		    	                 --야드권하지시Z축
		    	                 ,  CASE WHEN CUR_INFO.YD_EQP_WRK_SH = 1 THEN (CUR_INFO.DN_BASE_SLAB_T + CUR_INFO.DN_MTL_SLAB_T) + (CUR_INFO.YD_MTL_T1 * 0.3)
		    	                         WHEN CUR_INFO.YD_EQP_WRK_SH = 2 THEN (CUR_INFO.DN_BASE_SLAB_T + CUR_INFO.DN_MTL_SLAB_T) + (CUR_INFO.YD_MTL_T2 * 0.3)
		    	                         END                         AS YD_DN_WO_LOC_ZAXIS        --야드권하지시Z축 
		    	                 --

		    	                 , CUR_INFO.YD_DN_WO_ZAXIS_GAP_MAX   AS YD_DN_WO_ZAXIS_GAP_MAX    --야드권하지시Z축오차최대
		    	                 , CUR_INFO.YD_DN_WO_ZAXIS_GAP_MIN   AS YD_DN_WO_ZAXIS_GAP_MIN    --야드권하지시Z축오차최소
		    	 
		    	                 , ''                                AS GRUSH_COIL_YN             --짱구코일 여부
		    	                 , ''                                AS SPM_HFL_YN                --SPM/HFL  유무
		    	                 , ''                                AS CUT_YN                    --절단재 여부
		    	                 , CASE WHEN SUBSTR(CUR_INFO.YD_SCH_CD, 3, 2) = 'TC' AND YD_AID_WRK_YN = 'N' THEN CUR_INFO.TCAR_GP
		    	                        WHEN SUBSTR(CUR_INFO.YD_SCH_CD, 3, 2) = 'PT' AND YD_AID_WRK_YN = 'N' AND SUBSTR(CUR_INFO.YD_SCH_CD, 7, 2) = 'UM' THEN SUBSTR(CUR_INFO.YD_DN_WO_LOC, 1, 6)
		    	                        WHEN SUBSTR(CUR_INFO.YD_SCH_CD, 3, 2) = 'PT' AND YD_AID_WRK_YN = 'N' AND SUBSTR(CUR_INFO.YD_SCH_CD, 7, 2) = 'LM' THEN SUBSTR(CUR_INFO.YD_UP_WO_LOC, 1, 6)
		    	                        ELSE '' END                  AS YD_EQP_ID2                          --야드설비ID2
		    	                 , C.YD_AIM_BAY_GP                   AS YD_TC_AIM_BAY_GP          --야드대차목적동
		    	                 , C.YD_CAR_USE_GP                   AS YD_CAR_USE_GP             --야드차량사용구분
		    	                 , C.CAR_NO                          AS CAR_NO                    --차량번호

		    	                 , C.CARD_NO                         AS CARD_NO                   --차량번호
		    	                 , C.TRN_EQP_CD                      AS TRN_EQP_CD                --운송장비코드
		    	                 , CUR_INFO.YD_EQP_WRK_SH            AS YD_EQP_WRK_SH             --야드설비작업매수
		    	                 --대차 및 차량이송 하차 작업시
		    	                 , CASE WHEN SUBSTR(CUR_INFO.YD_SCH_CD,3,2) IN ('TC','PT') AND SUBSTR(CUR_INFO.YD_SCH_CD,7,2) IN ('LM')   THEN
		    	                             (SELECT COUNT(*)
		    	                                FROM TB_YM_STACKLAYER SL
		    	                               WHERE SL.STACK_COL_GP = SUBSTR(CUR_INFO.YD_UP_WO_LOC,1,6)
		    	                                 AND SL.STACK_BED_GP = SUBSTR(CUR_INFO.YD_UP_WO_LOC,7,2)
		    	                                 AND SL.STACK_LAYER_STAT IN ('C','U')) - CUR_INFO.YD_EQP_WRK_SH 
		    	                        ELSE 0 END                   AS YD_EQP_RMN_SH             --야드설비잔량매수
		    	                 
		    	                 , CUR_INFO.STOCK_ID1                AS STOCK_ID1                 --재료번호1
		    	                 , CUR_INFO.YD_MTL_WT1               AS YD_STL_WT1                --야드재료중량1
		    	                 , CUR_INFO.YD_MTL_T1                AS YD_STL_T1                 --야드재료두께1
		    	                 , CUR_INFO.YD_MTL_W1                AS YD_STL_W1                 --야드재료폭1
		    	                 , CUR_INFO.YD_MTL_L1                AS YD_STL_L1                 --야드재료길이1
		    	                 , CUR_INFO.WORK_ORD_SEQNO1          AS WORK_ORD_SEQNO1
		    	                 , NVL(NVL(   (SELECT '1' 
		    	                                 FROM USRYDA.VW_YD_SLABCOMM
		    	                                WHERE SLAB_NO        = CUR_INFO.STOCK_ID1 
		    	                                  AND SCARFING_DONE_YN ='Y'
		    	                                  AND SPEC_ABBSYM IN ('API-J55', 'JS-45C', 'JS-SS490', 'HSC-1470HPF'
		    	                                                     ,'APIJ55', 'JS45C' , 'JSSS490' , 'HSC1470HPF'))
		    	                              ,
		    	                              (SELECT CASE WHEN B1.C_WRSLT>=0.20 THEN '1'
		    	                                           ELSE '0' END 
		    	                                 FROM TB_PM_HEATWRSLTCOMM B1
		    	                                    , VW_YD_SLABCOMM D1
		    	                                WHERE D1.SLAB_NO = CUR_INFO.STOCK_ID1 
		    	                                  AND B1.HEAT_NO = D1.HEAT_NO
		    	                                --AND B1.C_WRSLT>=0.20
		    	                                  AND D1.SCARFING_DONE_YN='Y')        
		    	                            
		    	                            ),'0')                   AS TREAT_GP1  
		    	                        
		    	                 , CUR_INFO.CHARGE_SEQNO1            AS CHARGE_SEQNO1

		    	                 , CUR_INFO.STOCK_ID2                AS STOCK_ID2                 --재료번호2
		    	                 , CUR_INFO.YD_MTL_WT2               AS YD_STL_WT2                --야드재료중량2
		    	                 , CUR_INFO.YD_MTL_T2                AS YD_STL_T2                 --야드재료두께2
		    	                 , CUR_INFO.YD_MTL_W2                AS YD_STL_W2                 --야드재료폭2
		    	                 , CUR_INFO.YD_MTL_L2                AS YD_STL_L2                 --야드재료길이2
		    	                 , CUR_INFO.WORK_ORD_SEQNO2          AS WORK_ORD_SEQNO2
		    	                 , CASE WHEN CUR_INFO.STOCK_ID2  IS NOT NULL THEN 
		    	                        NVL(NVL(  (SELECT '1' FROM USRYDA.VW_YD_SLABCOMM
		    	                                    WHERE SLAB_NO        = CUR_INFO.STOCK_ID2 
		    	                                      AND SCARFING_DONE_YN ='Y'
		    	                                      AND SPEC_ABBSYM IN ('API-J55', 'JS-45C', 'JS-SS490', 'HSC-1470HPF'
		    	                                                          ,'APIJ55', 'JS45C', 'JSSS490', 'HSC1470HPF'))
		    	                                ,
		    	                                  (SELECT CASE WHEN B1.C_WRSLT>=0.20 THEN '1'
		    	                                               ELSE '0' END 
		    	                                     FROM TB_PM_HEATWRSLTCOMM B1
		    	                                        , VW_YD_SLABCOMM D1
		    	                                    WHERE D1.SLAB_NO = CUR_INFO.STOCK_ID2 
		    	                                      AND B1.HEAT_NO = D1.HEAT_NO
		    	--                                    AND B1.C_WRSLT>=0.20
		    	                                      AND D1.SCARFING_DONE_YN='Y')        
		    	                                ),'0')
		    	                        ELSE '0' END                 AS TREAT_GP2                         
		    	                 , CUR_INFO.CHARGE_SEQNO2            AS CHARGE_SEQNO2

		    	                 , NEXT_INFO.YD_SCH_CD_NEXT          AS YD_SCH_CD_NEXT            --야드스케쥴코드_Next
		    	                 , SUBSTRB((SELECT CD_CONTENTS
		    	                              FROM USRYMA.TB_YM_SCHEDULERULE WHERE YD_SCH_CD = NEXT_INFO.YD_SCH_CD_NEXT)
		    	                          || (CASE WHEN NEXT_INFO.YD_SCH_CD_NEXT IS NULL THEN ''
		    	                                   WHEN NEXT_INFO.YD_AID_WRK_YN_NEXT='N' THEN '[주]' 
		    	                                   ELSE '[보조]' END),1,30)
		    	                                                     AS YD_SCH_NAME_NEXT          --야드스케줄명_NEXT
		    	                 , NEXT_INFO.YD_UP_WO_LOC_NEXT       AS YD_UP_WO_LOC_NEXT         --야드권상지시위치_Next
		    	                 , NEXT_INFO.YD_UP_WO_LAYER_NEXT     AS YD_UP_WO_LAYER_NEXT       --야드권상지시단_Next
		    	                 , NEXT_INFO.YD_DN_WO_LOC_NEXT       AS YD_DN_WO_LOC_NEXT         --야드권하지시위치_Next
		    	                 , NEXT_INFO.YD_DN_WO_LAYER_NEXT     AS YD_DN_WO_LAYER_NEXT       --야드권하지시단_Next

		    	                 , NEXT_INFO.STOCK_ID_NEXT           AS STOCK_ID_NEXT             --재료번호_Next
		    	                 , NEXT_INFO.YD_MTL_WT_NEXT          AS YD_CRN_WRK_WT_NEXT        --야드크레인작업중량_Next
		    	                 , NEXT_INFO.YD_MTL_T_NEXT           AS YD_CRN_WRK_T_NEXT         --야드크레인작업총두께_Next
		    	                 , NEXT_INFO.YD_CRN_WRK_MAX_W_NEXT   AS YD_CRN_WRK_MAX_W_NEXT     --야드크레인작업최대폭_Next

		    	                 , ''                                AS COIL_OUTDIA_NEXT          --야드크레인작업코일외경_Next
		    	                 , ''                                AS COIL_INDIA_NEXT           --야드크레인작업코일내경_Next

		    	                 , NEXT_INFO.STOCK_ID_NEXT2          AS STOCK_ID_NEXT2            --재료번호_Next
		    	                 , NEXT_INFO.YD_MTL_WT_NEXT2         AS YD_CRN_WRK_WT_NEXT2       --야드크레인작업중량_Next
		    	                 , NEXT_INFO.YD_MTL_T_NEXT2          AS YD_CRN_WRK_T_NEXT2        --야드크레인작업총두께_Next
		    	                 , NEXT_INFO.YD_CRN_WRK_MAX_W_NEXT2  AS YD_CRN_WRK_MAX_W_NEXT2    --야드크레인작업최대폭_Next

		    	                 , ''                                AS COIL_OUTDIA_NEXT2         --야드크레인작업코일외경_Next
		    	                 , ''                                AS COIL_INDIA_NEXT2          --야드크레인작업코일내경_Next

		    	                 , CUR_INFO.UP_ROTATION_ANGLE        AS UP_ROTATION_ANGLE         --권상위치 회전각도
		    	                 , CUR_INFO.DOWN_ROTATION_ANGLE      AS DOWN_ROTATION_ANGLE       --권하위치 회전각도

		    	                 , CUR_INFO.RN                       AS RN
		    	                 , CUR_INFO.YD_L2_REQUEST_STAT       AS YD_L2_REQUEST_STAT

		    	                 , CASE WHEN CUR_INFO.YD_UP_WO_LOC_ZAXIS >= 0  THEN '+'
		    	                        ELSE '-' END                 AS YD_UP_WO_LOC_ZAXIS_SYM
		    	                 , CASE WHEN CUR_INFO.YD_DN_WO_LOC_YAXIS >= 0  THEN '+'
		    	                        ELSE '-' END                 AS YD_DN_WO_LOC_ZAXIS_SYM
		    	                        
		    	--                 , 'E' AS LAST_WORK_ORD_GP 
		    	                 --대차 및 차량이송 하차 작업시
		    	                 , CASE WHEN SUBSTR(CUR_INFO.YD_SCH_CD,3,2) IN ('TC','PT') 
		    	                         AND SUBSTR(CUR_INFO.YD_SCH_CD,7,2) IN ('LM')  
		    	                         AND (SELECT COUNT(*)
		    	                                FROM TB_YM_STACKLAYER SL
		    	                               WHERE SL.STACK_COL_GP = SUBSTR(CUR_INFO.YD_UP_WO_LOC,1,6)
		    	                                 AND SL.STACK_BED_GP = SUBSTR(CUR_INFO.YD_UP_WO_LOC,7,2)
		    	                                 AND SL.STACK_LAYER_STAT IN ('C','U')) - CUR_INFO.YD_EQP_WRK_SH > 0 THEN ''
		    	                         ELSE 'E' END                AS LAST_WORK_ORD_GP      
		    	              FROM  -- 대상작업지시
		    	                   (
		    	                    SELECT A.*
		    	                           --권상위치 두께 합 
		    	                         , (SELECT nvl(SUM(SLAB_T),0)
		    	                              FROM TB_YM_STACKLAYER SL
		    	                                 , VW_YD_SLABCOMM   SC 
		    	                             WHERE SL.STACK_COL_GP = SUBSTR(A.YD_UP_WO_LOC,1,6)
		    	                               AND SL.STACK_BED_GP = SUBSTR(A.YD_UP_WO_LOC,7,2)
		    	                               AND SL.STOCK_ID = SC.SLAB_NO 
		    	                               AND SL.STACK_LAYER_STAT IN ('C','U')
		    	                           )                         AS UP_MTL_SLAB_T
		    	                         ,   --기준 위치
		    	                           (SELECT NVL(STACK_BED_Z_AXIS,0)
		    	                              FROM TB_YM_STACKER SK
		    	                             WHERE SK.STACK_COL_GP = SUBSTR(A.YD_UP_WO_LOC,1,6)
		    	                               AND SK.STACK_BED_GP = SUBSTR(A.YD_UP_WO_LOC,7,2)
		    	                               AND ROWNUM = 1
		    	                           )                         AS UP_BASE_SLAB_T
		    	                         ,  --권하위치 두께 합 
		    	                           (SELECT nvl(SUM(SLAB_T),0)
		    	                              FROM TB_YM_STACKLAYER SL
		    	                                 , VW_YD_SLABCOMM   SC 
		    	                             WHERE SL.STACK_COL_GP = SUBSTR(A.YD_DN_WO_LOC,1,6)
		    	                               AND SL.STACK_BED_GP = SUBSTR(A.YD_DN_WO_LOC,7,2) 
		    	                               AND SL.STOCK_ID = SC.SLAB_NO 
		    	                               AND SL.STACK_LAYER_STAT IN ('C')
		    	                           )                         AS DN_MTL_SLAB_T
		    	                            
		    	                         ,   --기준 위치
		    	                           (SELECT NVL(STACK_BED_Z_AXIS,0)
		    	                              FROM TB_YM_STACKER SK
		    	                             WHERE SK.STACK_COL_GP = SUBSTR(A.YD_DN_WO_LOC,1,6)
		    	                               AND SK.STACK_BED_GP = SUBSTR(A.YD_DN_WO_LOC,7,2)
		    	                               AND ROWNUM = 1
		    	                           )                         AS DN_BASE_SLAB_T  
		    	                      FROM ( 
		    	                            SELECT A.*
		    	                                 , B.STOCK_ID                   AS STOCK_ID1
		    	                                 , C.SLAB_WT                    AS YD_MTL_WT1
		    	                                 , C.SLAB_T                     AS YD_MTL_T1
		    	                                 , C.SLAB_W                     AS YD_MTL_W1
		    	                                 , C.SLAB_LEN                   AS YD_MTL_L1
		    	                                 , C.WORK_ORD_SEQNO             AS WORK_ORD_SEQNO1
		    	                                 , ST.CHARGE_LOT_NO             AS CHARGE_SEQNO1  --장입순번
		    	                                 -- 형상값여부 : 차량인경우 대차 형상인 경우 CHECK
		    	                                 , CASE WHEN ST.WGT_CENTER_XAXIS BETWEEN '300000' AND '320000' THEN 'PT'
		    	                                        WHEN ST.WGT_CENTER_XAXIS BETWEEN  '53000' AND  '57000' THEN 'TC'
		    	                                        ELSE 'ETC' END          AS FACE_GP
		    	                                 -- 형상값 사용여부
		    	                                 , (SELECT NVL(DTL_ITM1,'N') 
		    	                                      FROM USRYMA.TB_YM_RULE 
		    	                                     WHERE REPR_CD_GP= 'YM2006' AND ITEM = A.YD_BAY_GP) AS FACE_USE_YN
		    	                                 , ST.WGT_CENTER_XAXIS          AS WGT_CENTER_XAXIS1
		    	                                 , ST.WGT_CENTER_YAXIS          AS WGT_CENTER_YAXIS1
		    	                                 , ST.WGT_CENTER_ZAXIS          AS WGT_CENTER_ZAXIS1
		    	                                 , ST.BENDING_GP                AS BENDING_GP1
		    	                                 , ST.BENDING_AXIS              AS BENDING_AXIS1
		    	                                 , LEAD(B.STOCK_ID)             OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS STOCK_ID2
		    	                                 , LEAD(C.SLAB_WT)              OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS YD_MTL_WT2
		    	                                 , LEAD(C.SLAB_T)               OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS YD_MTL_T2
		    	                                 , LEAD(C.SLAB_W)               OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS YD_MTL_W2
		    	                                 , LEAD(C.SLAB_LEN)             OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS YD_MTL_L2
		    	                                 , LEAD(C.WORK_ORD_SEQNO)       OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS WORK_ORD_SEQNO2
		    	                                 , LEAD(ST.CHARGE_LOT_NO)       OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS CHARGE_SEQNO2
		    	                                 , LEAD(ST.WGT_CENTER_XAXIS)    OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS WGT_CENTER_XAXIS2
		    	                                 , LEAD(ST.WGT_CENTER_YAXIS)    OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS WGT_CENTER_YAXIS2
		    	                                 , LEAD(ST.WGT_CENTER_ZAXIS)    OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS WGT_CENTER_ZAXIS2
		    	                                 , LEAD(ST.BENDING_GP)          OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS BENDING_GP2
		    	                                 , LEAD(ST.BENDING_AXIS)        OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS BENDING_AXIS2

		    	                                 , B.YD_AID_WRK_YN
		    	                                 , C.HR_SPEC_ABBSYM
		    	                                 , C.CURR_PROG_CD

		    	                                 , (SELECT YD_WRK_PLAN_TCAR 
		    	                                      FROM TB_YM_WRKBOOK 
		    	                                     WHERE YD_WBOOK_ID = A.YD_WBOOK_ID
		    	                                       AND DEL_YN = 'N'
		    	                                     GROUP BY YD_WRK_PLAN_TCAR
		    	                                   ) AS TCAR_GP
		    	                                 , ROW_NUMBER() OVER(PARTITION BY A.YD_WBOOK_ID, A.YD_CRN_SCH_ID ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC) AS RN
		    	                              FROM TEMP_CRN_SCH_ID A
		    	                                 , TB_YM_CRNWRKMTL B
		    	                                 , (SELECT A.*
		    	                                         , DECODE(ORD_HCR_GP,NULL,'0',
		    	                                                           DECODE(LEAST(TRUNC((SYSDATE - NVL(SLAB_CREATE_DDTT, A.REG_DDTT))*24),DECODE(SCARFING_YN,'Y',24,12)),DECODE(SCARFING_YN,'Y',24,12),'0',NULL,'0','1')
		    	                                            ) AS WORK_ORD_SEQNO
		    	                                         
		    	                                      FROM VW_YD_SLABCOMM A
		    	                                         
		    	                                   )  C
		    	                                 , TB_YM_STOCK     ST 
		    	                             WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID 
		    	                               AND B.STOCK_ID      = C.SLAB_NO
		    	                               AND A.DEL_YN        = 'N'
		    	                               AND B.STOCK_ID      = ST.STOCK_ID
		    	                           ) A
		    	                     WHERE RN = 1
		    	                   ) CUR_INFO   
		    	                   -- 다음작업지시
		    	                 , (
		    	                    SELECT A.*
		    	                      FROM (
		    	                            SELECT A.YD_EQP_ID      AS YD_EQP_ID
		    	                                 , A.YD_SCH_CD      AS YD_SCH_CD_NEXT
		    	                                 , A.YD_UP_WO_LOC   AS YD_UP_WO_LOC_NEXT
		    	                                 , A.YD_UP_WO_LAYER AS YD_UP_WO_LAYER_NEXT
		    	                                 , A.YD_DN_WO_LOC   AS YD_DN_WO_LOC_NEXT 
		    	                                 , A.YD_DN_WO_LAYER AS YD_DN_WO_LAYER_NEXT 
		    	                                 , B.YD_AID_WRK_YN  AS YD_AID_WRK_YN_NEXT
		    	                                 , B.STOCK_ID       AS STOCK_ID_NEXT
		    	                                 , C.SLAB_WT        AS YD_MTL_WT_NEXT
		    	                                 , C.SLAB_T         AS YD_MTL_T_NEXT
		    	                                 , C.SLAB_W         AS YD_CRN_WRK_MAX_W_NEXT
		    	                                 
		    	                                 , LEAD(B.STOCK_ID) OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS STOCK_ID_NEXT2
		    	                                 , LEAD(C.SLAB_WT)  OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS YD_MTL_WT_NEXT2
		    	                                 , LEAD(C.SLAB_T)   OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS YD_MTL_T_NEXT2
		    	                                 , LEAD(C.SLAB_W)   OVER (ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC)  AS YD_CRN_WRK_MAX_W_NEXT2

		    	                                 , ROW_NUMBER() OVER(PARTITION BY A.YD_WBOOK_ID, A.YD_CRN_SCH_ID ORDER BY A.YD_CRN_SCH_ID ASC, B.STACK_LAYER_GP DESC) AS RN
		    	                              FROM TEMP_CRN_SCH_ID A
		    	                                 , TB_YM_CRNWRKMTL B
		    	                                 , VW_YD_SLABCOMM  C
		    	                                 , TB_YM_STOCK     ST
		    	                             WHERE A.YD_CRN_SCH_ID_NEXT = B.YD_CRN_SCH_ID
		    	                               AND B.STOCK_ID           = C.SLAB_NO
		    	                               AND A.DEL_YN||''         = 'N'
		    	                               AND B.STOCK_ID           = ST.STOCK_ID
		    	                           ) A
		    	                     WHERE RN = 1
		    	                ) NEXT_INFO 
		    	                , TB_YM_STACKLAYER B
		    	             --차량정보
		    	                , (SELECT YD_CRN_SCH_ID, YD_AIM_BAY_GP, YD_CAR_USE_GP, TRN_EQP_CD, CAR_NO, CARD_NO
		    	                     FROM (
		    	                          SELECT :V_YD_CRN_SCH_ID                AS YD_CRN_SCH_ID
		    	                               , ''                              AS YD_AIM_BAY_GP    --B열연:확인
		    	                               , (CASE WHEN A.TRN_EQP_CD IS NOT NULL THEN 'L'
		    	                                       WHEN A.CAR_NO IS NOT NULL THEN 'G'
		    	                                       ELSE A.YD_CAR_USE_GP END) AS YD_CAR_USE_GP
		    	                               , A.TRN_EQP_CD                    AS TRN_EQP_CD
		    	                               , A.CAR_NO                        AS CAR_NO
		    	                               , A.CARD_NO                       AS CARD_NO
		    	                            FROM TB_YM_WRKBOOK A
		    	                           WHERE A.YD_WBOOK_ID = (
		    	                                                  SELECT YD_WBOOK_ID
		    	                                                    FROM TB_YM_CRNSCH
		    	                                                   WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID )
		    	                          )
		    	                  ) C

		    	            WHERE SUBSTR(CUR_INFO.YD_DN_WO_LOC_TO,1,6) =B.STACK_COL_GP(+)    
		    	              AND SUBSTR(CUR_INFO.YD_DN_WO_LOC_TO,7,2) =B.STACK_BED_GP(+)      
		    	              AND CUR_INFO.STK_LYR_NO_TEMP = B.STACK_LAYER_GP(+)   
		    	              AND CUR_INFO.YD_EQP_ID     = NEXT_INFO.YD_EQP_ID(+)
		    	              AND CUR_INFO.YD_CRN_SCH_ID = C.YD_CRN_SCH_ID(+)
		    	              AND CUR_INFO.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
		    	              AND CUR_INFO.YD_WRK_PROG_STAT IN ('W', 'S', '1', '2', '3')
		    	              
		    	              ) K          
		    	       ) SN 
		    	*/
		    	jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMA8L004";
		    	
			} else if("YMA8L009".equals(msgId)) {
				
				trtNm = "B열연 SLAB Scarfing 작업지시";
				
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMA8L009";
				
			} else if("YMA8L010".equals(msgId)) {
				
				trtNm = "B열연 SLAB HOME 이동지시";
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYMA8L010 
				SELECT JMS_TC_CD                                    --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				     , JMS_TC_CD                                 --전문ID
				     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS') --발생일자
				     ||RPAD(NVL(MSG_GP          , ' '), 1 , ' ') --전문구분
				     ||RPAD(NVL(MSG_LEN         , ' '), 4 , ' ') --전문길이
				     ||RPAD(NVL(TEMP            , ' '), 29, ' ') --임시
				     ||RPAD(NVL(YD_EQP_ID       , ' '), 6 , ' ') --설비코드  
				     ||RPAD(NVL(MV_GP           , ' '), 1 , ' ') --이동구분
				     ||LPAD(NVL(YD_WO_LOC_XAXIS , '0'), 7 , '0') --이동지시X축  
				     ||LPAD(NVL(YD_WO_LOC_YAXIS , '0'), 5 , '0') --이동지시Y축  
				       AS JMS_TC_MESSAGE --JMSTCMessage
				 FROM (
				        SELECT'YMA8L010'            AS JMS_TC_CD --전문ID
				             , 'I'                  AS MSG_GP    --전문구분
				             , '0019'               AS MSG_LEN     
							 , ''                   AS TEMP 
				             , :V_YD_EQP_ID         AS YD_EQP_ID
				             , :V_MV_GP             AS MV_GP
				             , :V_YD_WO_LOC_XAXIS   AS YD_WO_LOC_XAXIS
				             , :V_YD_WO_LOC_YAXIS   AS YD_WO_LOC_YAXIS
				          FROM DUAL
				      ) */
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMA8L010";
				
			}
			
			JDTORecordSet jsRst = null;
			
			if(!"".equals(jspeed_query_id)) {
				trtNm = trtNm + "(" + msgId + ") : ";
				
				jsRst = this.select(jrParam, jspeed_query_id);
					
				commUtils.printLog(logId, trtNm + jsRst.size(), "DB");
			}

			return jsRst;
			
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	/***************************************************************************
	 * L3 송신 전문 조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : L3전문생성
	 *      
	 *      @param String msgId
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMsgL3(String msgId, JDTORecord jrParam) throws DAOException {
		String methodNm = "L3전문생성[YmCommDAO.getMsgL3] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			
			jrParam.setField("JMS_TC_CD"       , msgId);
			
/* 출하관리  */	
			if ("YDDMR001".equals(msgId) ) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR001 
				-- 입고실적적 전문조회 
				SELECT 'YDDMR001'                          AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDDMR001'                          AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				     , MT.STOCK_ID                         AS GOODS_NO
				     , TO_CHAR(SYSDATE,'YYYYMMDD') AS RECEIPT_DATE
				     , TO_CHAR(SYSDATE,'HH24MISS') AS RECEIPT_TIME
				     , SC.YD_GP
				--      ,SC.YS_DN_WR_LOC || SC.YS_DN_WR_LAYER || SC.YS_DN_WR_SEQ_NO AS STORE_LOC
				     , (SELECT STACK_COL_GP||STACK_BED_GP||STACK_LAYER_GP
				           FROM TB_YM_STACKLAYER 
				          WHERE STOCK_ID = MT.STOCK_ID AND ROWNUM = 1) AS STORE_LOC
				      ,PT.PRD_ITM_CD AS PROD_ITEM_CODE
				      ,PT.CURR_PROG_CD 
				FROM   TB_YM_WRKBOOK SC
				      ,TB_YM_WRKBOOKMTL MT
				      ,TB_PT_COILCOMM PT
				WHERE  SC.YD_WBOOK_ID = :V_YD_WBOOK_ID
				AND    SC.YD_WBOOK_ID = MT.YD_WBOOK_ID
				AND    MT.STOCK_ID = PT.COIL_NO
				AND    PT.CURR_PROG_CD IN ('H')
				 */       				
				trtNm = "입고실적";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR001";
			}
//			 else if("YDDMR003".equals(msgId)) {
				 /*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR003
				 SELECT B.STOCK_ID               AS GOODS_NO
				       ,SUBSTR(TO_CHAR(A.YD_DN_CMPL_DT, 'YYYYMMDDHH24MISS'),1,8) AS RECEIPT_DATE  
				       ,SUBSTR(TO_CHAR(A.YD_DN_CMPL_DT, 'YYYYMMDDHH24MISS'),9,6) AS RECEIPT_TIME
				       ,A.YD_GP                                 AS YD_GP
				       ,SUBSTR(A.YD_DN_WR_LOC,1,8)
				       ||CASE WHEN  (TO_NUMBER(A.YD_DN_WR_LAYER) +(TO_NUMBER(B.STACK_LAYER_GP) -1)) <10 THEN 
				                   '00'|| TO_CHAR(TO_NUMBER(A.YD_DN_WR_LAYER) +(TO_NUMBER(B.STACK_LAYER_GP) -1))
				                   
				             WHEN  (TO_NUMBER(A.YD_DN_WR_LAYER) +(TO_NUMBER(B.STACK_LAYER_GP) -1))  > 9  AND
				                   (TO_NUMBER(A.YD_DN_WR_LAYER) +(TO_NUMBER(B.STACK_LAYER_GP) -1)) < 100 THEN
				                   '0'||TO_CHAR(TO_NUMBER(A.YD_DN_WR_LAYER) +(TO_NUMBER(B.STACK_LAYER_GP) -1))
				                   
				             WHEN  (TO_NUMBER(A.YD_DN_WR_LAYER) +(TO_NUMBER(B.STACK_LAYER_GP) -1))  > 99 THEN
				                   TO_CHAR(TO_NUMBER(A.YD_DN_WR_LAYER) +(TO_NUMBER(B.STACK_LAYER_GP) -1))
				             ELSE '' END AS     STORE_LOC
				   
				   FROM USRYMA.TB_YM_CRNSCH A
				       ,USRYMA.TB_YM_CRNWRKMTL B
				        
				  WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
				    AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				    ORDER BY B.STACK_LAYER_GP   
				  */
//					trtNm = "임가공입고작업실적";
//					jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR003";
//					
//			}
			 else if ("YDDMR004".equals(msgId) ) {
				 /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR004  
				 WITH PARA_TABLE AS
				 (
				    SELECT 'YDDMR004'     AS JMS_TC_CD 
				         , :V_STOCK_ID    AS STOCK_ID
				      FROM DUAL
				 )
				 SELECT P.JMS_TC_CD                                              
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT
				      , P.JMS_TC_CD                         AS TC_CODE                                             
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT
				      , A.STOCK_ID                          AS GOODS_NO
				      , B.YD_STR_LOC_HIS1                   AS BEFO_STORE_LOC    --특수강야드저장위치이력
				      , C.STACK_COL_GP||STACK_BED_GP||STACK_LAYER_GP          AS TO_STORE_LOC    --저장위치
				      , TO_CHAR(SYSDATE,'YYYYMMDD')         AS MOVENSTACK_DATE
				      , TO_CHAR(SYSDATE,'HH24MISS')         AS MOVENSTACK_TIME
				   FROM USRYMA.TB_YM_STOCK A
				      , USRPTA.TB_PT_COILCOMM B
				      , USRYMA.TB_YM_STACKLAYER C
				      , PARA_TABLE P
				  WHERE A.STOCK_ID  = B.COIL_NO
				    AND A.STOCK_ID  = P.STOCK_ID    
				    AND A.STOCK_ID  = C.STOCK_ID 
				    AND C.STACK_LAYER_ACTIVE_STAT IN('C','U') --적치중, 권상대기
				   */ 
					trtNm = "코일제품이적작업실적";
					jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR004";				
				}			
//			 else if ("YDDMR007".equals(msgId) ) {
//				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR007 
//				SELECT 'YDDMR007'                          AS JMS_TC_CD          --JMSTC코드
//				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
//				      ,'YDDMR007'                          AS TC_CODE            --IF구분코드
//				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT     --TC생성일시
//				      ,TS.CAR_NO                                                 --차량번호
//				      ,SUBSTR(TS.YD_CARLD_STOP_LOC,1,1)    AS YD_GP              --야드구분
//				      ,COUNT(*) OVER ()                    AS BD_EA           --제품개수
//				      ,TM.STOCK_ID                         AS BD_NO           --제품번호
//				      ,TS.TRANS_ORD_DATE                   AS TRANS_WORD_DATE                                --운송지시일자
//				      ,TS.TRANS_ORD_SEQNO                  AS TRANS_WORD_SEQNO                               --운송지시순번
//				      ,'1'                                 AS SPST_FRTOMOVE_GP
//				  FROM (
//				            SELECT *
//				            FROM   TB_YD_CARSCH
//				            WHERE  YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
//				       ) TS
//				      ,(
//				            SELECT STOCK_ID 
//				            FROM   TB_YM_CRNWRKMTL
//				            WHERE  YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
//				       ) TM
//				 */       				
//				trtNm = "출하차량상차개시";
//				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR007";
//			} 
				else if ("YDDMR011".equals(msgId) ) {
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR011 
					SELECT 'YDDMR011'                          AS JMS_TC_CD          --JMSTC코드
					     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
					     , 'YDDMR011'                          AS TC_CODE            --IF구분코드
					     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT     --TC생성일시
					     , TS.CARD_NO                                                 --카드번호   
					     , TS.CAR_NO                                                 --차량번호
					     , SUBSTR(TS.YD_CARLD_STOP_LOC,1,1)    AS YD_GP              --야드구분
					     , :V_GOODS_EA                         AS GOODS_EA           --제품개수완료시(*)
					     , :V_STOCK_ID                         AS GOODS_NO           --제품번호
					     , TS.TRANS_ORD_DATE                   AS TRANS_WORD_DATE                                        --운송지시일자
					     , TS.TRANS_ORD_SEQNO                  AS TRANS_WORD_SEQNO                               --운송지시순번
					  FROM USRYDA.TB_YD_CARSCH TS
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					*/ 
					
					trtNm = "코일일품출하상차실적";
					jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR011";	
				}			 
			 else if ("YDDMR015".equals(msgId) ) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR015 
				--출하상차완료 전문조회 
				SELECT 'YDDMR015'                              AS JMS_TC_CD          --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')     AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				      ,'YDDMR015'                              AS TC_CODE            --IF구분코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')     AS TC_CREATE_DDTT     --TC생성일시
				      ,TS.CAR_NO                                                     --차량번호
				      ,TS.CARD_NO 
				      ,MIN(SUBSTR(TS.YD_CARLD_STOP_LOC,1,1))   AS YD_GP  
				      ,TO_CHAR(TS.YD_CARLD_CMPL_DT,'YYYYMMDD') AS CARLOAD_END_DATE   --상차완료일자
				      ,TO_CHAR(TS.YD_CARLD_CMPL_DT,'HH24MISS') AS CARLOAD_END_TIME   --상차완료시각
				      ,MIN(TS.TRANS_ORD_DATE )                 AS TRANS_WORD_DATE     --운송지시일자
				      ,MIN(TS.TRANS_ORD_SEQNO)                 AS TRANS_WORD_SEQNO    --운송지시순번
				    --  ,'1' AS SPST_FRTOMOVE_GP
				  FROM TB_YD_CARSCH     TS
				 WHERE TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 GROUP BY TS.CARD_NO, TS.CAR_NO, TS.YD_CARLD_CMPL_DT
				 */       				
				trtNm = "출하차량상차완료";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR015";
			}
			else if ("YDDMR019".equals(msgId) ) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR019 
				SELECT  'YDDMR019'                          AS JMS_TC_CD            --JMSTC코드
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				      , 'YDDMR019'                          AS TC_CODE              --IF구분코드
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				      , TM.UPCARUNLOAD_GP
				      , TM.CARD_NO
				      , TM.CAR_NO 
				      , TM.YD_GP
				      , TM.CARLOAD_START_DATE
				      , TM.CARLOAD_START_TIME
				      , MAX(DECODE(NO,1 ,TM.STL_NO,''))          AS GOODS_NO1 
					  , MAX(DECODE(NO,1 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE1 
					  , MAX(DECODE(NO,1 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO1   
				      , MAX(DECODE(NO,2 ,TM.STL_NO,''))          AS GOODS_NO2 
					  , MAX(DECODE(NO,2 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE2 
					  , MAX(DECODE(NO,2 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO2   
				      , MAX(DECODE(NO,3 ,TM.STL_NO,''))          AS GOODS_NO3 
					  , MAX(DECODE(NO,3 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE3 
					  , MAX(DECODE(NO,3 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO3   	  	  
				      , MAX(DECODE(NO,4 ,TM.STL_NO,''))          AS GOODS_NO4 
					  , MAX(DECODE(NO,4 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE4 
					  , MAX(DECODE(NO,4 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO4   
				      , MAX(DECODE(NO,5 ,TM.STL_NO,''))          AS GOODS_NO5 
					  , MAX(DECODE(NO,5 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE5 
					  , MAX(DECODE(NO,5 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO5   
				      , MAX(DECODE(NO,6 ,TM.STL_NO,''))          AS GOODS_NO6 
					  , MAX(DECODE(NO,6 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE6 
					  , MAX(DECODE(NO,6 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO6  
				      , MAX(DECODE(NO,7 ,TM.STL_NO,''))          AS GOODS_NO7 
					  , MAX(DECODE(NO,7 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE7 
					  , MAX(DECODE(NO,7 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO7   
				      , MAX(DECODE(NO,8 ,TM.STL_NO,''))          AS GOODS_NO8 
					  , MAX(DECODE(NO,8 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE8 
					  , MAX(DECODE(NO,8 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO8   
				      , MAX(DECODE(NO,9 ,TM.STL_NO,''))          AS GOODS_NO9 
					  , MAX(DECODE(NO,9 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE9 
					  , MAX(DECODE(NO,9 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO9   	  	  
				      , MAX(DECODE(NO,10,TM.STL_NO,''))          AS GOODS_NO10
					  , MAX(DECODE(NO,10,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE10
					  , MAX(DECODE(NO,10,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO10  
				      , MAX(DECODE(NO,11,TM.STL_NO,''))          AS GOODS_NO11
					  , MAX(DECODE(NO,11,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE11
					  , MAX(DECODE(NO,11,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO11  
				      , MAX(DECODE(NO,12,TM.STL_NO,''))          AS GOODS_NO12
					  , MAX(DECODE(NO,12,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE12
					  , MAX(DECODE(NO,12,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO12
				      , MAX(DECODE(NO,13,TM.STL_NO,''))          AS GOODS_NO13
					  , MAX(DECODE(NO,13,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE13
					  , MAX(DECODE(NO,13,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO13  
				      , MAX(DECODE(NO,14,TM.STL_NO,''))          AS GOODS_NO14
					  , MAX(DECODE(NO,14,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE14
					  , MAX(DECODE(NO,14,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO14  
				      , MAX(DECODE(NO,15,TM.STL_NO,''))          AS GOODS_NO15
					  , MAX(DECODE(NO,15,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE15
					  , MAX(DECODE(NO,15,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO15  	  	  

				   FROM         
				        (SELECT
				                ROWNUM NO 
				              , CASE WHEN  TA.YD_EQP_WRK_STAT ='L' THEN 'D'
				                     ELSE  'U' END  AS UPCARUNLOAD_GP
				              , TX.CAR_CARD_NO 		AS CARD_NO       
				              , (CASE YD_CAR_USE_GP WHEN 'G' THEN TX.CAR_NO2 ELSE TA.TRN_EQP_CD END)		AS CAR_NO  
				              , '3' AS YD_GP
				              , SUBSTR(TA.YD_CARLD_ST_DT,1,8)  AS CARLOAD_START_DATE
				              , SUBSTR(TA.YD_CARLD_ST_DT,9,6)  AS CARLOAD_START_TIME 
				              , TA.STL_NO AS STL_NO
				              , NVL(FRTOMOVE_WORD_DATE,NVL(TX.TRANS_ORD_DATE2 ,SUBSTR(TX.TRANS_WORD_NO,1,8))) AS TRANS_ORD_DATE
				              , NVL(FRTOMOVE_WORD_SEQNO,NVL(TX.TRANS_ORD_SEQNO2,SUBSTR(TX.TRANS_WORD_NO,9)))  AS TRANS_ORD_SEQNO 
				           FROM 
				               (SELECT A.YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
				                     , A.TRN_EQP_CD AS TRN_EQP_CD
				                     , (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END) AS YD_EQP_WRK_STAT              
				                     , A.CAR_NO AS CAR_NO
				                     , TO_CHAR(NVL(A.YD_CARLD_ST_DT,SYSDATE) , 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT                              
				                     , TO_CHAR(A.YD_CARUD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_ST_DT
				                     , A.CARD_NO AS CARD_NO                 
				                     , C.STL_NO AS STL_NO
				                     , YD_CAR_USE_GP
				                  FROM TB_YD_CARSCH A
				                     , TB_YD_PREPSCH B
				                     , TB_YD_PREPMTL C
				                 WHERE A.YD_CARLD_WRK_BOOK_ID = B.YD_WBOOK_ID
				                   AND B.YD_PREP_SCH_ID=C.YD_PREP_SCH_ID
				                   AND (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END)='U' --상차
				                 UNION ALL
				                SELECT A.YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
				                     , A.TRN_EQP_CD AS TRN_EQP_CD
				                     , (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END) AS YD_EQP_WRK_STAT              
				                     , A.CAR_NO AS CAR_NO
				                     , TO_CHAR(NVL(A.YD_CARLD_ST_DT,SYSDATE) , 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT                              
				                     , TO_CHAR(A.YD_CARUD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_ST_DT
				                     , A.CARD_NO AS CARD_NO                 
				                     , C.STL_NO AS STL_NO
				                     , YD_CAR_USE_GP
				                  FROM TB_YD_CARSCH A
				                     , TB_YD_CARFTMVMTL C
				                 WHERE A.YD_CAR_SCH_ID=C.YD_CAR_SCH_ID
				                   AND (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END)='L' --하차
				                   AND A.DEL_YN='N'
				               ) TA
				               , USRYMA.TB_YM_STOCK TX           
				               , USRYDA.TB_YD_STOCK TY
				               , TB_DM_GOODSFRTOMOVEWORD  @DL_SMDB DM
				           WHERE TA.STL_NO = TX.STOCK_ID 
				             AND TA.STL_NO = TY.STL_NO(+)
				             AND TA.STL_NO = DM.GOODS_NO(+)
				             AND DM.FRTOMOVE_WORD_DATE(+)>=TO_CHAR(SYSDATE-1,'YYYYMMDD')
				             AND YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				             AND DM.DEL_YN='N') TM
				GROUP BY   UPCARUNLOAD_GP,CARD_NO,CAR_NO ,YD_GP,CARLOAD_START_DATE, CARLOAD_START_TIME                 
				*/
				trtNm = "코일제품고간이송상하차개시";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR019";
			}
			else if ("YDDMR020".equals(msgId) ) {
				 /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR020  
				SELECT 'YDDMR020'                          AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDDMR020'                          AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				     , CASE WHEN YD_CAR_PROG_STAT IN ('2','3','4','5')       THEN 'U'   
				            ELSE 'D' END                   AS UPCARUNLOAD_GP
				     , CARD_NO                             AS CARD_NO         
				     , CAR_NO                              AS CAR_NO
				     , SUBSTR(YD_CARLD_STOP_LOC,1,1)       AS YD_GP
				     , CASE WHEN  YD_CAR_PROG_STAT IN ('2','3','4','5') 
				            THEN SUBSTR(TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS'),1,8) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS'),1,8) END  AS CARLOAD_START_DATE
				     , CASE WHEN  YD_CAR_PROG_STAT IN ('2','3','4','5')
				            THEN SUBSTR(TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS'),9,6) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS'),9,6) END  AS CARLOAD_START_TIME 
				     , TRANS_ORD_DATE                      AS TRANS_WORD_DATE
				     , TRANS_ORD_SEQNO                     AS TRANS_WORD_SEQNO
				  FROM TB_YD_CARSCH C
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/ 
				trtNm = "임가공이송상하차개시";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR020";
			}
			else if ("YDDMR021".equals(msgId) ) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR021_PIDEV
				SELECT 'YDDMR021'                          AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDDMR021'                          AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				     , UPCARUNLOAD_GP
				     , CARD_NO
				     , CAR_NO
				     , YD_PNT_CD AS ARR_YD_PNT_CD
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  AS ISSUE_DDTT
				     , TO_CHAR(COUNT(*) OVER ()) AS TREAT_EA
				     , MAX(DECODE(NO,1,DD.STL_NO,''))          AS GOOODS_NO1
				     , MAX(DECODE(NO,1,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE1
				     , MAX(DECODE(NO,1,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO1
				     , MAX(DECODE(NO,1,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD1    
				     , MAX(DECODE(NO,1,DD.YD_GP,''))           AS YD_GP1    
				     , MAX(DECODE(NO,1,DD.BAY_GP,''))          AS BAY_GP1      
				     , MAX(DECODE(NO,1,DD.YD_STK_BED_NO,''))   AS SPAN1
				     , MAX(DECODE(NO,1,DD.YD_STK_LYR_NO,''))   AS STK_LYR1    
				     , MAX(DECODE(NO,2,DD.STL_NO,''))          AS GOOODS_NO2
				     , MAX(DECODE(NO,2,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE2
				     , MAX(DECODE(NO,2,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO2
				     , MAX(DECODE(NO,2,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD2    
				     , MAX(DECODE(NO,2,DD.YD_GP,''))           AS YD_GP2    
				     , MAX(DECODE(NO,2,DD.BAY_GP,''))          AS BAY_GP2      
				     , MAX(DECODE(NO,2,DD.YD_STK_BED_NO,''))   AS SPAN2
				     , MAX(DECODE(NO,2,DD.YD_STK_LYR_NO,''))   AS STK_LYR2 
				     , MAX(DECODE(NO,3,DD.STL_NO,''))          AS GOOODS_NO3
				     , MAX(DECODE(NO,3,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE3
				     , MAX(DECODE(NO,3,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO3
				     , MAX(DECODE(NO,3,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD3    
				     , MAX(DECODE(NO,3,DD.YD_GP,''))           AS YD_GP3    
				     , MAX(DECODE(NO,3,DD.BAY_GP,''))          AS BAY_GP3      
				     , MAX(DECODE(NO,3,DD.YD_STK_BED_NO,''))   AS SPAN3
				     , MAX(DECODE(NO,3,DD.YD_STK_LYR_NO,''))   AS STK_LYR3
				     , MAX(DECODE(NO,4,DD.STL_NO,''))          AS GOOODS_NO4
				     , MAX(DECODE(NO,4,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE4
				     , MAX(DECODE(NO,4,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO4
				     , MAX(DECODE(NO,4,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD4    
				     , MAX(DECODE(NO,4,DD.YD_GP,''))           AS YD_GP4    
				     , MAX(DECODE(NO,4,DD.BAY_GP,''))          AS BAY_GP4      
				     , MAX(DECODE(NO,4,DD.YD_STK_BED_NO,''))   AS SPAN4
				     , MAX(DECODE(NO,4,DD.YD_STK_LYR_NO,''))   AS STK_LYR4
				     , MAX(DECODE(NO,5,DD.STL_NO,''))          AS GOOODS_NO5
				     , MAX(DECODE(NO,5,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE5
				     , MAX(DECODE(NO,5,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO5
				     , MAX(DECODE(NO,5,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD5    
				     , MAX(DECODE(NO,5,DD.YD_GP,''))           AS YD_GP5    
				     , MAX(DECODE(NO,5,DD.BAY_GP,''))          AS BAY_GP5      
				     , MAX(DECODE(NO,5,DD.YD_STK_BED_NO,''))   AS SPAN5
				     , MAX(DECODE(NO,5,DD.YD_STK_LYR_NO,''))   AS STK_LYR5
				     , MAX(DECODE(NO,6,DD.STL_NO,''))          AS GOOODS_NO6
				     , MAX(DECODE(NO,6,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE6
				     , MAX(DECODE(NO,6,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO6
				     , MAX(DECODE(NO,6,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD6    
				     , MAX(DECODE(NO,6,DD.YD_GP,''))           AS YD_GP6    
				     , MAX(DECODE(NO,6,DD.BAY_GP,''))          AS BAY_GP6      
				     , MAX(DECODE(NO,6,DD.YD_STK_BED_NO,''))   AS SPAN6
				     , MAX(DECODE(NO,6,DD.YD_STK_LYR_NO,''))   AS STK_LYR6   
				     , MAX(DECODE(NO,7,DD.STL_NO,''))          AS GOOODS_NO7
				     , MAX(DECODE(NO,7,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE7
				     , MAX(DECODE(NO,7,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO7
				     , MAX(DECODE(NO,7,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD7    
				     , MAX(DECODE(NO,7,DD.YD_GP,''))           AS YD_GP7    
				     , MAX(DECODE(NO,7,DD.BAY_GP,''))          AS BAY_GP7      
				     , MAX(DECODE(NO,7,DD.YD_STK_BED_NO,''))   AS SPAN7
				     , MAX(DECODE(NO,7,DD.YD_STK_LYR_NO,''))   AS STK_LYR7   
				     , MAX(DECODE(NO,8,DD.STL_NO,''))          AS GOOODS_NO8
				     , MAX(DECODE(NO,8,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE8
				     , MAX(DECODE(NO,8,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO8
				     , MAX(DECODE(NO,8,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD8    
				     , MAX(DECODE(NO,8,DD.YD_GP,''))           AS YD_GP8    
				     , MAX(DECODE(NO,8,DD.BAY_GP,''))          AS BAY_GP8      
				     , MAX(DECODE(NO,8,DD.YD_STK_BED_NO,''))   AS SPAN8
				     , MAX(DECODE(NO,8,DD.YD_STK_LYR_NO,''))   AS STK_LYR8   
				     , MAX(DECODE(NO,9,DD.STL_NO,''))          AS GOOODS_NO9
				     , MAX(DECODE(NO,9,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE9
				     , MAX(DECODE(NO,9,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO9
				     , MAX(DECODE(NO,9,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD9    
				     , MAX(DECODE(NO,9,DD.YD_GP,''))           AS YD_GP9    
				     , MAX(DECODE(NO,9,DD.BAY_GP,''))          AS BAY_GP9      
				     , MAX(DECODE(NO,9,DD.YD_STK_BED_NO,''))   AS SPAN9
				     , MAX(DECODE(NO,9,DD.YD_STK_LYR_NO,''))   AS STK_LYR9   
				     , MAX(DECODE(NO,10,DD.STL_NO,''))          AS GOOODS_NO10
				     , MAX(DECODE(NO,10,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE10
				     , MAX(DECODE(NO,10,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO10
				     , MAX(DECODE(NO,10,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD10    
				     , MAX(DECODE(NO,10,DD.YD_GP,''))           AS YD_GP10    
				     , MAX(DECODE(NO,10,DD.BAY_GP,''))          AS BAY_GP10      
				     , MAX(DECODE(NO,10,DD.YD_STK_BED_NO,''))   AS SPAN10
				     , MAX(DECODE(NO,10,DD.YD_STK_LYR_NO,''))   AS STK_LYR10   
				     , MAX(DECODE(NO,11,DD.STL_NO,''))          AS GOOODS_NO11
				     , MAX(DECODE(NO,11,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE11
				     , MAX(DECODE(NO,11,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO11
				     , MAX(DECODE(NO,11,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD11    
				     , MAX(DECODE(NO,11,DD.YD_GP,''))           AS YD_GP11    
				     , MAX(DECODE(NO,11,DD.BAY_GP,''))          AS BAY_GP11      
				     , MAX(DECODE(NO,11,DD.YD_STK_BED_NO,''))   AS SPAN11
				     , MAX(DECODE(NO,11,DD.YD_STK_LYR_NO,''))   AS STK_LYR11   
				     , MAX(DECODE(NO,12,DD.STL_NO,''))          AS GOOODS_NO12
				     , MAX(DECODE(NO,12,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE12
				     , MAX(DECODE(NO,12,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO12
				     , MAX(DECODE(NO,12,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD12    
				     , MAX(DECODE(NO,12,DD.YD_GP,''))           AS YD_GP12    
				     , MAX(DECODE(NO,12,DD.BAY_GP,''))          AS BAY_GP12      
				     , MAX(DECODE(NO,12,DD.YD_STK_BED_NO,''))   AS SPAN12
				     , MAX(DECODE(NO,12,DD.YD_STK_LYR_NO,''))   AS STK_LYR12   
				     , MAX(DECODE(NO,13,DD.STL_NO,''))          AS GOOODS_NO13
				     , MAX(DECODE(NO,13,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE13
				     , MAX(DECODE(NO,13,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO13
				     , MAX(DECODE(NO,13,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD13    
				     , MAX(DECODE(NO,13,DD.YD_GP,''))           AS YD_GP13    
				     , MAX(DECODE(NO,13,DD.BAY_GP,''))          AS BAY_GP13      
				     , MAX(DECODE(NO,13,DD.YD_STK_BED_NO,''))   AS SPAN13
				     , MAX(DECODE(NO,13,DD.YD_STK_LYR_NO,''))   AS STK_LYR13   
				     , MAX(DECODE(NO,14,DD.STL_NO,''))          AS GOOODS_NO14
				     , MAX(DECODE(NO,14,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE14
				     , MAX(DECODE(NO,14,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO14
				     , MAX(DECODE(NO,14,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD14    
				     , MAX(DECODE(NO,14,DD.YD_GP,''))           AS YD_GP14    
				     , MAX(DECODE(NO,14,DD.BAY_GP,''))          AS BAY_GP14      
				     , MAX(DECODE(NO,14,DD.YD_STK_BED_NO,''))   AS SPAN14
				     , MAX(DECODE(NO,14,DD.YD_STK_LYR_NO,''))   AS STK_LYR14   
				     , MAX(DECODE(NO,15,DD.STL_NO,''))          AS GOOODS_NO15
				     , MAX(DECODE(NO,15,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE15
				     , MAX(DECODE(NO,15,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO15
				     , MAX(DECODE(NO,15,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD15    
				     , MAX(DECODE(NO,15,DD.YD_GP,''))           AS YD_GP15    
				     , MAX(DECODE(NO,15,DD.BAY_GP,''))          AS BAY_GP15      
				     , MAX(DECODE(NO,15,DD.YD_STK_BED_NO,''))   AS SPAN15
				     , MAX(DECODE(NO,15,DD.YD_STK_LYR_NO,''))   AS STK_LYR15
				     , MAX(DECODE(NO,16,DD.STL_NO,''))          AS GOOODS_NO16
				     , MAX(DECODE(NO,16,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE16
				     , MAX(DECODE(NO,16,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO16
				     , MAX(DECODE(NO,16,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD16    
				     , MAX(DECODE(NO,16,DD.YD_GP,''))           AS YD_GP16    
				     , MAX(DECODE(NO,16,DD.BAY_GP,''))          AS BAY_GP16      
				     , MAX(DECODE(NO,16,DD.YD_STK_BED_NO,''))   AS SPAN16
				     , MAX(DECODE(NO,16,DD.YD_STK_LYR_NO,''))   AS STK_LYR16   
				     , MAX(DECODE(NO,17,DD.STL_NO,''))          AS GOOODS_NO17
				     , MAX(DECODE(NO,17,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE17
				     , MAX(DECODE(NO,17,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO17
				     , MAX(DECODE(NO,17,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD17    
				     , MAX(DECODE(NO,17,DD.YD_GP,''))           AS YD_GP17    
				     , MAX(DECODE(NO,17,DD.BAY_GP,''))          AS BAY_GP17      
				     , MAX(DECODE(NO,17,DD.YD_STK_BED_NO,''))   AS SPAN17
				     , MAX(DECODE(NO,17,DD.YD_STK_LYR_NO,''))   AS STK_LYR17   
				     , MAX(DECODE(NO,18,DD.STL_NO,''))          AS GOOODS_NO18
				     , MAX(DECODE(NO,18,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE18
				     , MAX(DECODE(NO,18,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO18
				     , MAX(DECODE(NO,18,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD18    
				     , MAX(DECODE(NO,18,DD.YD_GP,''))           AS YD_GP18    
				     , MAX(DECODE(NO,18,DD.BAY_GP,''))          AS BAY_GP18      
				     , MAX(DECODE(NO,18,DD.YD_STK_BED_NO,''))   AS SPAN18
				     , MAX(DECODE(NO,18,DD.YD_STK_LYR_NO,''))   AS STK_LYR18   
				     , MAX(DECODE(NO,19,DD.STL_NO,''))          AS GOOODS_NO19
				     , MAX(DECODE(NO,19,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE19
				     , MAX(DECODE(NO,19,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO19
				     , MAX(DECODE(NO,19,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD19    
				     , MAX(DECODE(NO,19,DD.YD_GP,''))           AS YD_GP19    
				     , MAX(DECODE(NO,19,DD.BAY_GP,''))          AS BAY_GP19      
				     , MAX(DECODE(NO,19,DD.YD_STK_BED_NO,''))   AS SPAN19
				     , MAX(DECODE(NO,19,DD.YD_STK_LYR_NO,''))   AS STK_LYR19   
				     , MAX(DECODE(NO,20,DD.STL_NO,''))          AS GOOODS_NO20
				     , MAX(DECODE(NO,20,DD.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE20
				     , MAX(DECODE(NO,20,DD.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO20
				     , MAX(DECODE(NO,20,DD.STORE_LOC_CD,''))    AS STORE_LOC_CD20    
				     , MAX(DECODE(NO,20,DD.YD_GP,''))           AS YD_GP20    
				     , MAX(DECODE(NO,20,DD.BAY_GP,''))          AS BAY_GP20      
				     , MAX(DECODE(NO,20,DD.YD_STK_BED_NO,''))   AS SPAN20
				     , MAX(DECODE(NO,20,DD.YD_STK_LYR_NO,''))   AS STK_LYR20      
				  FROM
				       ( SELECT ROWNUM NO
				              , CASE WHEN  TA.YD_EQP_WRK_STAT ='L' THEN 'D'
				                     ELSE  'U' END              AS UPCARUNLOAD_GP
				              , TX.CAR_CARD_NO    	            AS CARD_NO       
				              , (CASE YD_CAR_USE_GP WHEN 'G' THEN TX.CAR_NO2 ELSE TA.TRN_EQP_CD END)		
				                                                AS CAR_NO 
				              , TA.STL_NO                       AS STL_NO
				              , TA.YD_PNT_CD
				              , '3'                             AS YD_GP
				              , NVL(FRTOMOVE_WORD_DATE,NVL(TX.TRANS_ORD_DATE2 ,SUBSTR(TX.TRANS_WORD_NO,1,8))) AS TRANS_ORD_DATE
				              , NVL(FRTOMOVE_WORD_SEQNO,NVL(TX.TRANS_ORD_SEQNO2,SUBSTR(TX.TRANS_WORD_NO,9)))  AS TRANS_ORD_SEQNO 
				              , (SELECT STACK_COL_GP ||STACK_BED_GP ||STACK_LAYER_GP 
				                   FROM USRYMA.TB_YM_STACKLAYER
				                  WHERE STOCK_ID  =TA.STL_NO
				                    AND ROWNUM<=1 ) AS STORE_LOC_CD
				              , SUBSTR(TA.YD_CARLD_STOP_LOC,2,1) AS BAY_GP   
				              , (SELECT SUBSTR(STACK_COL_GP,3,2) 
				                   FROM USRYMA.TB_YM_STACKLAYER
				                  WHERE STOCK_ID =TA.STL_NO
				                  AND ROWNUM<=1 )                AS YD_STK_BED_NO  
				              , TA.YD_STK_LYR_NO	             AS YD_STK_LYR_NO 
				          
				          FROM (SELECT A.YD_CAR_SCH_ID           AS YD_CAR_SCH_ID
				                     , A.TRN_EQP_CD              AS TRN_EQP_CD
				                     , (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END) AS YD_EQP_WRK_STAT
				                     , A.YD_CARLD_STOP_LOC       AS YD_CARLD_STOP_LOC 
				                     , A.CAR_NO AS CAR_NO
				                     , TO_CHAR(A.YD_CARLD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT                              
				                     , TO_CHAR(A.YD_CARUD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_ST_DT
				                     , A.CARD_NO                 AS CARD_NO                 
				                     , C.STL_NO                  AS STL_NO
				                     , YD_CAR_USE_GP
				                     , C.YD_STK_LYR_NO           AS YD_STK_LYR_NO
				                     , NVL(A.YD_PNT_CD1,A.YD_PNT_CD2) AS YD_PNT_CD                              
				                  FROM TB_YD_CARSCH A
				                     , TB_YD_PREPSCH B
				                     , TB_YD_PREPMTL C
				                 WHERE A.YD_CARLD_WRK_BOOK_ID = B.YD_WBOOK_ID
				                   AND B.YD_PREP_SCH_ID=C.YD_PREP_SCH_ID
				                   AND (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END)='U' --상차
				                 UNION ALL
				                SELECT A.YD_CAR_SCH_ID           AS YD_CAR_SCH_ID
				                     , A.TRN_EQP_CD              AS TRN_EQP_CD
				                     , (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END) AS YD_EQP_WRK_STAT      
				                     , A.YD_CARLD_STOP_LOC       AS YD_CARLD_STOP_LOC    
				                     , A.CAR_NO                  AS CAR_NO
				                     , TO_CHAR(A.YD_CARLD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT                              
				                     , TO_CHAR(A.YD_CARUD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_ST_DT
				                     , A.CARD_NO                 AS CARD_NO                 
				                     , C.STL_NO                  AS STL_NO
				                     , YD_CAR_USE_GP
				                     , C.YD_STK_LYR_NO           AS YD_STK_LYR_NO        
				                     , NVL(A.YD_PNT_CD1,A.YD_PNT_CD2) AS YD_PNT_CD                          
				                  FROM TB_YD_CARSCH A
				                     , TB_YD_CARFTMVMTL C
				                 WHERE A.YD_CAR_SCH_ID=C.YD_CAR_SCH_ID
				                   AND (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END)='L' --하차
				                   AND A.DEL_YN='N'
				               ) TA
				             , USRYMA.TB_YM_STOCK TX           
				             , USRYDA.TB_YD_STOCK TY
				             , TB_DM_GOODSFRTOMOVEWORD  @DL_SMDB DM
				         WHERE TA.STL_NO = TX.STOCK_ID 
				           AND TA.STL_NO = TY.STL_NO(+)
				           AND TA.STL_NO = DM.GOODS_NO(+)
				           AND DM.FRTOMOVE_WORD_DATE(+)>=TO_CHAR(SYSDATE-1,'YYYYMMDD')
				           AND YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				           AND DM.DEL_YN='N'
				     ) DD
				 GROUP BY   UPCARUNLOAD_GP,CARD_NO,CAR_NO,YD_PNT_CD   
				*/ 
				trtNm = "코일제품고간이송상하차완료";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR021_PIDEV";
			}
			else if ("YDDMR022".equals(msgId) ) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR022 
				SELECT 'YDDMR022'                          AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDDMR022'                          AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
					 , CASE WHEN YD_CAR_PROG_STAT IN ('3','4','5') THEN 'U'   
						    ELSE 'D' END                   AS UPCARUNLOAD_GP
				     , CARD_NO                             AS CARD_NO
				     , CAR_NO                              AS CAR_NO
				     , SUBSTR(YD_CARLD_STOP_LOC,1,1)       AS YD_GP
				     , CASE WHEN YD_CAR_PROG_STAT IN ('3','4','5') 
				            THEN SUBSTR(TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS'),1,8) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS'),1,8) END  AS CARLOAD_DONE_DATE
				     , CASE WHEN  YD_CAR_PROG_STAT IN ('3','4','5')
				            THEN SUBSTR(TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS'),9,6) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS'),9,6) END AS CARLOAD_DONE_TIME        

				     , TRANS_ORD_DATE                      AS TRANS_WORD_DATE
				     , TRANS_ORD_SEQNO                     AS TRANS_WORD_SEQNO
				  FROM TB_YD_CARSCH C
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 */
				trtNm = "임가공이송상차완료";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR022";
			}
			 else if("YDDMR024".equals(msgId)) {
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR024 
					SELECT 'YDDMR024'                          AS JMS_TC_CD          --JMSTC코드
					     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
					     , 'YDDMR024'                          AS TC_CODE            --IF구분코드
					     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT     --TC생성일시
					     , :V_GOODS_NO                         AS GOODS_NO           -- 제품 번호
					     , :V_FROM_STORE_LOC_CD                AS FROM_STORE_LOC_CD  --FROM저장 위치
					     , :V_TRANS_DATE                       AS TRANS_DATE         --이송일자
					     , :V_TRANS_TIME                       AS TRANS_TIME         --이송시각
					     , :V_CR_FRTOMOVE_GP                   AS CR_FRTOMOVE_GP     --냉연구분 
					  FROM DUAL				
					*/
				    trtNm = "HYSCO대차이송실적";
					jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR024";

				} 
			 else if("YDDMR025".equals(msgId)) {
				 /*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR025
				 SELECT 'YDDMR025'                          AS JMS_TC_CD          --JMSTC코드     
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시 
				      , 'YDDMR025'                          AS TC_CODE            --IF구분코드    
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT     --TC생성일시    
				      , :V_GOODS_NO                         AS GOODS_NO           -- 제품 번호 
				   FROM DUAL	                                   				
					*/
				    trtNm = "HYSCO수냉실적";
					jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR025";

				} 				
			else if ("YDDMR026".equals(msgId) ) {
				/* Origin >com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcol_PIDEV */
				/* Adapter> com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR026*/
				//--포인트사용실적
				/*SELECT   'YDDMR026'                          AS JMS_TC_CD          --JMSTC코드
							,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
							,'YDDMR026'                          AS TC_CODE            --IF구분코드
							,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT     --TC생성일시
							,WLOC_CD
							,YD_PNT_CD
					        ,'B' AS YD_PNT_OCPY_GP
					        ,CASE WHEN STACK_COL_ACTIVE_STAT  ='C' THEN 'O'
					              ELSE 'C' END AS YD_PNT_UNIT_CL_GP
					        ,'Y' AS LOAN_PULLOUT_ABLE_YN     
					        ,DECODE(NVL(CARD_NO ,''),'','0000',CARD_NO)  AS OCPY_TRN_EQP_CD
							 FROM TB_YM_STACKCOL      
							WHERE STACK_COL_GP = :V_YD_STK_COL_GP
					      AND DEL_YN ='N'*/			
				trtNm = "포인트사용실적";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR026";
			}
			else if ("YDDMR036".equals(msgId) ||"YDDMR074".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR036 
				SELECT CASE WHEN NVL(CR_FRTOMOVE_GP,'') ='' THEN 'YDDMR036'
				            ELSE 'YDDMR074' END             AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , CASE WHEN NVL(CR_FRTOMOVE_GP,'') ='' THEN 'YDDMR036'
				            ELSE 'YDDMR074' END             AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  AS TC_CREATE_DDTT       --TC생성일시
				     , TRANS_ORD_DATE                       AS TRANS_WORD_DATE   --운송지시일자
				     , TRANS_ORD_SEQNO                      AS TRANS_WORD_SEQNO     --운송지시순번
				     , CAR_NO2                              AS CAR_NO
				     , CARLD_CHK_DONE_DATE              --상차검수완료일자
				     , CARLD_CHK_DONE_TIME              --상차검수완료시각
				     , GOOODS_NO_CNT                    --제품갯수
				     , GOOODS_NO1                      --제품번호1
				     , GOODS_CHK_AB_CD1    --이상코드1
				     , LABEL_REISSUE_YN1   --라벨재발행유무1
				     , GOOODS_NO2              --제품번호2
				     , GOODS_CHK_AB_CD2    --이상코드2
				     , LABEL_REISSUE_YN2   --라벨재발행유무2
				     , GOOODS_NO3              --제품번호3
				     , GOODS_CHK_AB_CD3    --이상코드3
				     , LABEL_REISSUE_YN3   --라벨재발행유무3
				     , GOOODS_NO4              --제품번호4
				     , GOODS_CHK_AB_CD4    --이상코드4
				     , LABEL_REISSUE_YN4   --라벨재발행유무4
				     , GOOODS_NO5              --제품번호5
				     , GOODS_CHK_AB_CD5    --이상코드5
				     , LABEL_REISSUE_YN5   --라벨재발행유무5
				     , GOOODS_NO6              --제품번호6
				     , GOODS_CHK_AB_CD6    --이상코드6
				     , LABEL_REISSUE_YN6   --라벨재발행유무6
				     , GOOODS_NO7              --제품번호7
				     , GOODS_CHK_AB_CD7    --이상코드7
				     , LABEL_REISSUE_YN7   --라벨재발행유무7
				     , GOOODS_NO8              --제품번호8
				     , GOODS_CHK_AB_CD8    --이상코드8
				     , LABEL_REISSUE_YN8   --라벨재발행유무8
				     , GOOODS_NO9              --제품번호9
				     , GOODS_CHK_AB_CD9    --이상코드9
				     , LABEL_REISSUE_YN9   --라벨재발행유무9
				     , GOOODS_NO10             --제품번호10
				     , GOODS_CHK_AB_CD10   --이상코드10
				     , LABEL_REISSUE_YN10  --라벨재발행유무10
				     , GOOODS_NO11             --제품번호11
				     , GOODS_CHK_AB_CD11   --이상코드11
				     , LABEL_REISSUE_YN11  --라벨재발행유무11
				     , GOOODS_NO12             --제품번호12
				     , GOODS_CHK_AB_CD12   --이상코드12
				     , LABEL_REISSUE_YN12  --라벨재발행유무12
				     , GOOODS_NO13             --제품번호13
				     , GOODS_CHK_AB_CD13   --이상코드13
				     , LABEL_REISSUE_YN13  --라벨재발행유무13
				     , GOOODS_NO14             --제품번호10
				     , GOODS_CHK_AB_CD14   --이상코드14
				     , LABEL_REISSUE_YN14  --라벨재발행유무14   
				     , GOOODS_NO15             --제품번호15
				     , GOODS_CHK_AB_CD15   --이상코드15
				     , LABEL_REISSUE_YN15  --라벨재발행유무15              
				     , CR_FRTOMOVE_GP     --냉연이송구분
				FROM (

				        SELECT
				               DD.TRANS_ORD_DATE
				              ,DD.TRANS_ORD_SEQNO
				              ,MAX(CAR_NO2) AS CAR_NO2
				              ,TO_CHAR(SYSDATE,'YYYYMMDD')          AS CARLD_CHK_DONE_DATE
				              ,TO_CHAR(SYSDATE,'HH24MISS')          AS CARLD_CHK_DONE_TIME
				              ,SUM(CNT)                             AS GOOODS_NO_CNT
				              ,MAX(DECODE(NO,1,DD.STL_NO,''))       AS GOOODS_NO1
				              ,MAX(DECODE(NO,1,DD.YD_AB_CD,''))     AS GOODS_CHK_AB_CD1
				              ,MAX(DECODE(NO,1,DD.LABEL_YN,''))     AS LABEL_REISSUE_YN1
				              ,MAX(DECODE(NO,2,DD.STL_NO,''))       AS GOOODS_NO2
				              ,MAX(DECODE(NO,2,DD.YD_AB_CD,''))     AS GOODS_CHK_AB_CD2
				              ,MAX(DECODE(NO,2,DD.LABEL_YN,''))     AS LABEL_REISSUE_YN2
				              ,MAX(DECODE(NO,3,DD.STL_NO,''))       AS GOOODS_NO3
				              ,MAX(DECODE(NO,3,DD.YD_AB_CD,''))     AS GOODS_CHK_AB_CD3
				              ,MAX(DECODE(NO,3,DD.LABEL_YN,''))     AS LABEL_REISSUE_YN3
				              ,MAX(DECODE(NO,4,DD.STL_NO,''))       AS GOOODS_NO4
				              ,MAX(DECODE(NO,4,DD.YD_AB_CD,''))     AS GOODS_CHK_AB_CD4
				              ,MAX(DECODE(NO,4,DD.LABEL_YN,''))     AS LABEL_REISSUE_YN4
				              ,MAX(DECODE(NO,5,DD.STL_NO,''))       AS GOOODS_NO5
				              ,MAX(DECODE(NO,5,DD.YD_AB_CD,''))     AS GOODS_CHK_AB_CD5
				              ,MAX(DECODE(NO,5,DD.LABEL_YN,''))     AS LABEL_REISSUE_YN5
				              ,MAX(DECODE(NO,6,DD.STL_NO,''))       AS GOOODS_NO6
				              ,MAX(DECODE(NO,6,DD.YD_AB_CD,''))     AS GOODS_CHK_AB_CD6
				              ,MAX(DECODE(NO,6,DD.LABEL_YN,''))     AS LABEL_REISSUE_YN6
				              ,MAX(DECODE(NO,7,DD.STL_NO,''))       AS GOOODS_NO7
				              ,MAX(DECODE(NO,7,DD.YD_AB_CD,''))     AS GOODS_CHK_AB_CD7
				              ,MAX(DECODE(NO,7,DD.LABEL_YN,''))     AS LABEL_REISSUE_YN7
				              ,MAX(DECODE(NO,8,DD.STL_NO,''))       AS GOOODS_NO8
				              ,MAX(DECODE(NO,8,DD.YD_AB_CD,''))     AS GOODS_CHK_AB_CD8
				              ,MAX(DECODE(NO,8,DD.LABEL_YN,''))     AS LABEL_REISSUE_YN8
				              ,MAX(DECODE(NO,9,DD.STL_NO,''))       AS GOOODS_NO9
				              ,MAX(DECODE(NO,9,DD.YD_AB_CD,''))     AS GOODS_CHK_AB_CD9
				              ,MAX(DECODE(NO,9,DD.LABEL_YN,''))     AS LABEL_REISSUE_YN9
				              ,MAX(DECODE(NO,10,DD.STL_NO,''))      AS GOOODS_NO10
				              ,MAX(DECODE(NO,10,DD.YD_AB_CD,''))    AS GOODS_CHK_AB_CD10
				              ,MAX(DECODE(NO,10,DD.LABEL_YN,''))    AS LABEL_REISSUE_YN10
				              ,MAX(DECODE(NO,11,DD.STL_NO,''))      AS GOOODS_NO11
				              ,MAX(DECODE(NO,11,DD.YD_AB_CD,''))    AS GOODS_CHK_AB_CD11
				              ,MAX(DECODE(NO,11,DD.LABEL_YN,''))    AS LABEL_REISSUE_YN11
				              ,MAX(DECODE(NO,12,DD.STL_NO,''))      AS GOOODS_NO12
				              ,MAX(DECODE(NO,12,DD.YD_AB_CD,''))    AS GOODS_CHK_AB_CD12
				              ,MAX(DECODE(NO,12,DD.LABEL_YN,''))    AS LABEL_REISSUE_YN12
				              ,MAX(DECODE(NO,13,DD.STL_NO,''))      AS GOOODS_NO13
				              ,MAX(DECODE(NO,13,DD.YD_AB_CD,''))    AS GOODS_CHK_AB_CD13
				              ,MAX(DECODE(NO,13,DD.LABEL_YN,''))    AS LABEL_REISSUE_YN13
				              ,MAX(DECODE(NO,14,DD.STL_NO,''))      AS GOOODS_NO14
				              ,MAX(DECODE(NO,14,DD.YD_AB_CD,''))    AS GOODS_CHK_AB_CD14
				              ,MAX(DECODE(NO,14,DD.LABEL_YN,''))    AS LABEL_REISSUE_YN14
				              ,MAX(DECODE(NO,15,DD.STL_NO,''))      AS GOOODS_NO15
				              ,MAX(DECODE(NO,15,DD.YD_AB_CD,''))    AS GOODS_CHK_AB_CD15
				              ,MAX(DECODE(NO,15,DD.LABEL_YN,''))    AS LABEL_REISSUE_YN15                      
				              ,MAX(CR_FRTOMOVE_GP)                  AS CR_FRTOMOVE_GP
				        FROM   (
				                    SELECT ROWNUM NO
				                         , EXA.TRANS_ORD_DATE
				                         , EXA.TRANS_ORD_SEQNO
				                         , EXA.STL_NO
				                         , EXA.YD_AB_CD
				                         , EXA.LABEL_YN
				                         , STK.CR_FRTOMOVE_GP   
				                         , STK.CAR_NO2 
				                         , 1 AS CNT 
				                    FROM   TB_YD_EXAMINATIONCHKLIST EXA
				                          ,USRYMA.TB_YM_STOCK STK
				                    WHERE  EXA.TRANS_ORD_DATE = :V_TRANS_ORD_DATE
				                    AND    EXA.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				                    AND    EXA.STL_NO = STK.STOCK_ID(+)

				               ) DD
				        GROUP BY TRANS_ORD_DATE, TRANS_ORD_SEQNO   

				    )
				    )
				*/
				
				trtNm = "검수완료실적";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR036";	
			}
			else if("YDDMR028".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR028 
				SELECT 'YDDMR028'                          AS JMS_TC_CD            --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				      ,'YDDMR028'                          AS TC_CODE              --IF구분코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				      ,CARD_NO                                                     --카트번호
				      ,CAR_NO                                                      --차량번호      
				      ,TRANS_ORD_DATE                      AS TRANS_WORD_DATE      --운송작업지시일자
				      ,TRANS_ORD_SEQNO                     AS TRANS_WORD_SEQNO     --운송작업지시순번
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS BAYIN_DDTT           --입동일시
				      ,ARR_WLOC_CD                         AS WLOC_CD  --개소코드(착지) 
				      ,YD_PNT_CD                                                --야드차량트코드
				      ,'Y'                                 AS LOAN_PULLOUT_ABLE_YN --차입인출가능여부
				  FROM (SELECT TS.CARD_NO
				              ,TS.CAR_NO
				              ,TS.TRANS_ORD_DATE
				              ,TS.TRANS_ORD_SEQNO
				              ,SC.WLOC_CD
				              ,SC.YD_PNT_CD
				              ,TS.YD_CAR_USE_GP
				              ,NVL(TS.YD_BAYIN_WO_SEQ,9) AS YD_BAYIN_WO_SEQ
				              ,TS.YD_CAR_SCH_ID
				              ,(SELECT YD_CARPNT_CD FROM USRYDA.TB_YD_CARPOINT  WHERE YD_STK_COL_GP  = SC.STACK_COL_GP AND ROWNUM = 1) AS YD_CARPNT_CD 
				              ,TS.ARR_WLOC_CD 
				          FROM USRYMA.TB_YM_STACKCOL SC
				              ,USRYDA.TB_YD_CARSCH TS
				         WHERE SC.STACK_COL_GP       = :V_STACK_COL_GP
				           AND SC.DEL_YN              = 'N'
				           AND SC.STACK_COL_ACTIVE_STAT  = 'C' --비활성화
				           AND (SC.STACK_COL_USAGE_CD  IS NULL OR SC.STACK_COL_USAGE_CD != 'GT') --출하
				           AND ((TS.YD_CAR_PROG_STAT = '1' AND TS.YD_CARLD_STOP_LOC = SC.STACK_COL_GP)
				             OR (TS.YD_CAR_PROG_STAT = 'A' AND TS.YD_CARUD_STOP_LOC = SC.STACK_COL_GP))
				           AND TS.DEL_YN             = 'N'
				         ORDER BY YD_BAYIN_WO_SEQ, YD_CAR_SCH_ID)
				 WHERE ROWNUM = 1          --첫번째가
				   AND YD_CAR_USE_GP = 'G' --출하차량
				 */
				trtNm = "차량입동지시";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR028";

			}
			 else if("YDDMR029".equals(msgId)) {
					trtNm = "제품출하차량도착실적)";
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR029 
					SELECT 'YDDMR029'                          AS JMS_TC_CD          --JMSTC코드
					     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
					     , 'YDDMR029'                          AS TC_CODE            --IF구분코드
					     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT     --TC생성일시
					     , :V_YD_GP                            AS YD_GP              --야드구분
					     , :V_TRANS_WORD_DATE                  AS TRANS_WORD_DATE    --운송지시일자
					     , :V_TRANS_WORD_SEQNO                 AS TRANS_WORD_SEQNO   --운송지시시각
					     , :V_CAR_NO                           AS CAR_NO             --차량번호
					     , :V_CARD_NO                          AS CARD_NO            --카드번호
					     , :V_ARR_WLOC_CD                      AS ARR_WLOC_CD        --착지개소코드 
					     , :V_ARR_YD_PNT_CD                    AS ARR_YD_PNT_CD      --착지야드포인트코드
					     , :V_CAR_ARR_DT                       AS CAR_ARR_DT         --차량도착일시
					  FROM DUAL				
					*/
					jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR029";

			 }			
			 else if("YDDMR050".equals(msgId)) {
					trtNm = "상차완료(야드핸드링)";
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR050 
					SELECT 'YDDMR050'                          AS JMS_TC_CD          --JMSTC코드
					     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
					     , 'YDDMR050'                          AS TC_CODE            --IF구분코드
					     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT     --TC생성일시
					     , :V_YD_GP                            AS YD_GP              --야드구분
					     , :V_TRANS_ORD_DT                     AS TRANS_ORD_DT       --운송지시일자
					     , :V_TRANS_ORD_SEQNO                  AS TRANS_ORD_SEQNO    --운송지시시각
					     , :V_CMBN_CARLD_YN                    AS CMBN_CARLD_YN      -- 
					     , :V_CARLD_PNT_CD                     AS CARLD_PNT_CD       -- 
					     , :V_CAR_NO                           AS CAR_NO             --차량번호
					     , :V_HANDLING_CNT                     AS HANDLING_CNT       --핸드링수
					     , :V_YD_STK_BED_WHIO_STAT             AS YD_STK_BED_WHIO_STAT
					  FROM DUAL				
					*/
					jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR050";

			} 			
			else if("YDDMR070".equals(msgId)) {
				/*--차량입동지시 전문조회 - com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR070
				SELECT 'YDDMR070'                          AS JMS_TC_CD            --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				      ,'YDDMR070'                          AS TC_CODE              --IF구분코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				      ,CARD_NO                                                     --카트번호
				      ,CAR_NO                                                      --차량번호      
				      ,TRANS_ORD_DATE                      AS TRANS_WORD_DATE      --운송작업지시일자
				      ,TRANS_ORD_SEQNO                     AS TRANS_WORD_SEQNO     --운송작업지시순번
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS BAYIN_DATE           --입동일시
				      ,YD_PNT_CD                                                --야드차량트코드
				      ,'Y'                                 AS LOAN_PULLOUT_ABLE_YN --차입인출가능여부
				      ,'1'                                 AS CR_FRTOMOVE_GP
				  FROM (SELECT TS.CARD_NO
				              ,TS.CAR_NO
				              ,TS.TRANS_ORD_DATE
				              ,TS.TRANS_ORD_SEQNO
				              ,SC.WLOC_CD
				              ,SC.YD_PNT_CD
				              ,TS.YD_CAR_USE_GP
				              ,NVL(TS.YD_BAYIN_WO_SEQ,9) AS YD_BAYIN_WO_SEQ
				              ,TS.YD_CAR_SCH_ID
				              ,(SELECT YD_CARPNT_CD FROM USRYDA.TB_YD_CARPOINT  WHERE YD_STK_COL_GP  = SC.STACK_COL_GP AND ROWNUM = 1) AS YD_CARPNT_CD 
				              ,TS.ARR_WLOC_CD 
				          FROM USRYMA.TB_YM_STACKCOL SC
				              ,USRYDA.TB_YD_CARSCH TS
				         WHERE SC.STACK_COL_GP       = :V_STACK_COL_GP
				           AND SC.DEL_YN              = 'N'
				           AND SC.STACK_COL_ACTIVE_STAT  = 'C' --비활성화
				           AND (SC.STACK_COL_USAGE_CD  IS NULL OR SC.STACK_COL_USAGE_CD != 'GT') --출하
				           AND ((TS.YD_CAR_PROG_STAT = '1' AND TS.YD_CARLD_STOP_LOC = SC.STACK_COL_GP)
				             OR (TS.YD_CAR_PROG_STAT = 'A' AND TS.YD_CARUD_STOP_LOC = SC.STACK_COL_GP))
				           AND TS.DEL_YN             = 'N'
				         ORDER BY YD_BAYIN_WO_SEQ, YD_CAR_SCH_ID)
				 WHERE ROWNUM = 1          --첫번째가
				   AND YD_CAR_USE_GP = 'G' --출하차량	
			*/	   		
				trtNm = "차량입동지시";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR070";

			} 
			else if("YDDMR071".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR071 
				-- 코일이송 상차개시
				SELECT 'YDDMR071'                           AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDDMR071'                           AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  AS TC_CREATE_DDTT       --TC생성일시		
				     , CAR_NO		                    AS CAR_NO
				     , CARD_NO 		                    AS CARD_NO 
				     , SUBSTR(YD_CARLD_STOP_LOC,1,1) AS YD_GP   
				     , CASE WHEN YD_CARLD_ST_DT IS NOT NULL AND LENGTH(TRIM(YD_CARLD_ST_DT)) = 14  
				            THEN SUBSTR(YD_CARLD_ST_DT,1,8)
				            ELSE '' END                     AS CARLOAD_START_DATE   
				     , CASE WHEN YD_CARLD_ST_DT IS NOT NULL AND LENGTH(TRIM(YD_CARLD_ST_DT)) = 14
				            THEN SUBSTR(YD_CARLD_ST_DT,9,6)
				            ELSE '' END                     AS CARLOAD_START_TIME  
				     , TRANS_ORD_DATE                   AS TRANS_WORD_DATE
				     , TRANS_ORD_SEQNO                  AS TRANS_WORD_SEQNO   
				     , (SELECT CR_FRTOMOVE_GP 
				          FROM TB_YM_STOCK
				         WHERE TRANS_ORD_DATE2 = A.TRANS_ORD_DATE
				           AND TRANS_ORD_SEQNO2 = A.TRANS_ORD_SEQNO 
				           AND ROWNUM = 1)                  AS CR_FRTOMOVE_GP   
				  FROM (SELECT A.YD_CAR_SCH_ID              AS YD_CAR_SCH_ID
				             , A.TRN_EQP_CD                 AS TRN_EQP_CD
				             , A.SPOS_WLOC_CD               AS SPOS_WLOC_CD
				             , A.ARR_WLOC_CD                AS ARR_WLOC_CD
				             , A.YD_EQP_WRK_STAT            AS YD_EQP_WRK_STAT
				             , A.YD_CARLD_CMPL_DT           AS YD_CARLD_CMPL_DT
				             , A.YD_CARLD_STOP_LOC          AS YD_CARLD_STOP_LOC
				             , A.CAR_NO                     AS CAR_NO
				             , A.YD_EQP_ID                  AS YD_EQP_ID
				             , TO_CHAR(A.YD_CARLD_ST_DT     , 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT                              
				             , TO_CHAR(A.YD_CARUD_ST_DT     , 'YYYYMMDDHH24MISS') AS YD_CARUD_ST_DT
				             , TO_CHAR(A.YD_CARUD_CMPL_DT   , 'YYYYMMDDHH24MISS') AS YD_CARUD_CMPL_DT
				             , A.CARD_NO                    AS CARD_NO                 
				             , A.DEL_YN 
				             , A.TRANS_ORD_DATE
				             , A.TRANS_ORD_SEQNO
				          FROM TB_YD_CARSCH A
				         WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID 
				           AND A.DEL_YN = 'N'
				        )  A 

				*/	   
				trtNm = "코일이송 상차개시";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR071";

			}
			else if("YDDMR072".equals(msgId)) {
				/*--코일일품출하상차실적??? - 
				/* Origin  >> com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdStockYdCarFtmvMtlSTLNo */
				/* Adapter >> com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR072 */
				/*SELECT 
                        'YDDMR072'                           AS JMS_TC_CD            --JMSTC코드
						,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
						,'YDDMR072'                          AS TC_CODE              --IF구분코드
						,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시	
						,TA.YD_CAR_SCH_ID                    AS YD_CAR_SCH_ID                         
						,TA.CARD_NO		                     AS CARD_NO                                 
						,TA.CAR_NO		                     AS CAR_NO                                  
						,substr(TA.YD_CARLD_STOP_LOC,1,1)    AS YD_GP                 
						,TB.STOCK_ID                         AS GOODS_NO                                        
						,TB.TRANS_ORD_DATE2                  AS TRANS_ORD_DATE                       
						,TB.TRANS_ORD_SEQNO2                 AS TRANS_ORD_SEQNO             
					  ,TB.CR_FRTOMOVE_GP                     AS CR_FRTOMOVE_GP
					FROM TB_YD_CARSCH TA
					    ,TB_YM_STOCK TB
				 WHERE TA.TRANS_ORD_DATE = TB.TRANS_ORD_DATE2
					 AND TA.TRANS_ORD_SEQNO = TB.TRANS_ORD_SEQNO2
					 AND TB.STOCK_ID=  :V_STOCK_ID
					AND  TA.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID*/			
				trtNm = "코일일품출하상차실적";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR072";

			}	else if("YDDMR073".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR073 
				SELECT 'YDDMR073'                           AS JMS_TC_CD            --JMSTC코드
					 , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
					 , 'YDDMR073'                           AS TC_CODE              --IF구분코드
					 , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  AS TC_CREATE_DDTT       --TC생성일시		
				     , TA.CAR_NO		                    AS CAR_NO
				     , TA.CARD_NO 		                    AS CARD_NO 
				     , substr(TA.YD_CARLD_STOP_LOC,1,1)     AS YD_GP   
				     , SUBSTR(TA.YD_CARLD_CMPL_DT,1,8)      AS CARLOAD_END_DATE   
				     , SUBSTR(TA.YD_CARLD_CMPL_DT,9,6)      AS CARLOAD_END_TIME  
				     , TB.TRANS_ORD_DATE2                   AS TRANS_WORD_DATE
				     , TB.TRANS_ORD_SEQNO2                  AS TRANS_WORD_SEQNO   
				     , TB.CR_FRTOMOVE_GP                    AS CR_FRTOMOVE_GP
				  FROM (SELECT A.YD_CAR_SCH_ID      AS YD_CAR_SCH_ID
				             , B.DEL_YN             AS DEL_YN
				             , A.TRN_EQP_CD         AS TRN_EQP_CD
				             , A.SPOS_WLOC_CD       AS SPOS_WLOC_CD
				             , A.ARR_WLOC_CD        AS ARR_WLOC_CD
				             , A.YD_EQP_WRK_STAT    AS YD_EQP_WRK_STAT
				             , B.STL_NO             AS STL_NO
				             , A.YD_CARLD_STOP_LOC  AS YD_CARLD_STOP_LOC
				             , B.YD_CAR_UPP_LOC_CD  AS YD_CAR_UPP_LOC_CD
				             , B.YD_STK_BED_NO      AS YD_STK_BED_NO
				             , B.YD_STK_LYR_NO      AS YD_STK_LYR_NO
				             , A.CAR_NO             AS CAR_NO
				             , A.YD_EQP_ID          AS YD_EQP_ID
				             , NVL(TO_CHAR(A.YD_CARLD_CMPL_DT, 'YYYYMMDDHH24MISS'),:V_WR_DT) AS YD_CARLD_CMPL_DT --상차완료                             
				             , A.CARD_NO            AS CARD_NO                 
				             , A.YD_CARUD_STOP_LOC  AS YD_CARUD_STOP_LOC
				             , A.YD_PNT_CD1
				          FROM TB_YD_CARSCH A
				             , TB_YD_CARFTMVMTL B
				         WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
				           AND A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				           AND ROWNUM = 1
				        ) TA
				      , USRYMA.TB_YM_STOCK TB
				  WHERE TA.STL_NO = TB.STOCK_ID
				    AND TA.DEL_YN = 'N'
				*/		   
				trtNm = "코일이송 상차완료";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR073";

			}
			else if("YDDMR075".equals(msgId)) {
				/*--코일이송하차개시전송 PDA - */	
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR075 
				SELECT 'YDDMR075'                          AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDDMR075'                          AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				     , TA.CARD_NO		                   AS CARD_NO     
				     , TA.CAR_NO	                       AS CAR_NO            
				     , substr(TA.YD_CARUD_STOP_LOC,1,1)    AS YD_GP
				     , substr(TA.YD_CARUD_ST_DT,1,8)	   AS CARUD_START_DATE
				     , substr(TA.YD_CARUD_ST_DT,9,6)	   AS CARUD_START_TIME     
				     , TB.TRANS_ORD_DATE2                  AS TRANS_WORD_DATE
				     , TB.TRANS_ORD_SEQNO2                 AS TRANS_WORD_SEQNO   
				     , TB.CR_FRTOMOVE_GP                   AS CR_FRTOMOVE_GP 
				  FROM (SELECT A.YD_CAR_SCH_ID      AS YD_CAR_SCH_ID
				             , B.DEL_YN             AS DEL_YN
				             , A.TRN_EQP_CD         AS TRN_EQP_CD
				             , A.SPOS_WLOC_CD       AS SPOS_WLOC_CD
				             , A.ARR_WLOC_CD        AS ARR_WLOC_CD
				             , A.YD_EQP_WRK_STAT    AS YD_EQP_WRK_STAT
				             , A.YD_EQP_WRK_SH      AS YD_EQP_WRK_SH
				             , B.STL_NO             AS STL_NO
				             , A.YD_CARLD_CMPL_DT   AS YD_CARLD_CMPL_DT
				             , A.YD_CARLD_STOP_LOC  AS YD_CARLD_STOP_LOC
				             , B.YD_CAR_UPP_LOC_CD  AS YD_CAR_UPP_LOC_CD
				             , B.YD_STK_BED_NO      AS YD_STK_BED_NO
				             , B.YD_STK_LYR_NO      AS YD_STK_LYR_NO
				             , A.CAR_NO             AS CAR_NO
				             , A.YD_EQP_ID          AS YD_EQP_ID
				             , NVL(TO_CHAR(A.YD_CARUD_ST_DT ,'YYYYMMDDHH24MISS'),:V_WR_DT) AS YD_CARUD_ST_DT
				             , A.CARD_NO            AS CARD_NO                 
				             , A.YD_CARUD_STOP_LOC  AS YD_CARUD_STOP_LOC
				             , A.YD_PNT_CD1
				          FROM TB_YD_CARSCH A, TB_YD_CARFTMVMTL B
				         WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
				           AND A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				           AND ROWNUM = 1) TA
				      ,TB_YM_STOCK TB
				 WHERE TA.STL_NO = TB.STOCK_ID
				   */
				trtNm = "코일이송하차개시전송 PDA";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR075";
			}
			else if("YDDMR076".equals(msgId)) {
				/*--코일이송하차완료전송 PDA - */	
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR076 
				SELECT 'YDDMR076'                          AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDDMR076'                          AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				     , CARD_NO		                       AS CARD_NO     
				     , CAR_NO		                       AS CAR_NO            
				     , SUBSTR(YD_CARUD_STOP_LOC,1,1)       AS YD_GP
				     , SUBSTR(NVL(TO_CHAR(C.YD_CARUD_CMPL_DT ,'YYYYMMDDHH24MISS'),:V_WR_DT),1,8)	       AS CARUD_START_DATE
				     , SUBSTR(NVL(TO_CHAR(C.YD_CARUD_CMPL_DT ,'YYYYMMDDHH24MISS'),:V_WR_DT),9,6)    	   AS CARUD_START_TIME     
				     , TRANS_ORD_DATE                      AS TRANS_ORD_DATE
				     , TRANS_ORD_SEQNO                     AS TRANS_ORD_SEQNO   
				     , (SELECT MIN(B.CR_FRTOMOVE_GP) 
				          FROM TB_YD_CARFTMVMTL A
				             , TB_YM_STOCK B
				         WHERE A.STL_NO = B.STOCK_ID               
				           AND A.YD_CAR_SCH_ID = C.YD_CAR_SCH_ID)
				                                          AS CR_FRTOMOVE_GP 
				  FROM TB_YD_CARSCH C
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/ 
				trtNm = "코일이송하차완료전송 PDA";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR076";
			}
			
			/* 구내운송  */  			
//			else if("YDTSJ007".equals(msgId)) {
//				trtNm = "소재차량상차개시";
//				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDTSJ007 
//				--소재차량상차개시 전문
//				SELECT 'YDTSJ007'                          AS JMS_TC_CD          --JMSTC코드
//				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
//				      ,TS.TRN_EQP_CD                                             --운송장비코드
//				      ,SC.WLOC_CD                          AS SPOS_WLOC_CD       --발지개소코드
//				      ,SC.YD_PNT_CD                        AS SPOS_YD_PNT_CD     --발지개소포인트
//				      ,TS.ARR_WLOC_CD                                            --착지개소코드
//				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TRN_WRK_ST_DT --운송작업시작일시
//				  FROM TB_YM_STACKCOL SC
//				      ,TB_YD_CARSCH TS
//				 WHERE SC.YD_CAR_USE_GP = TS.YD_CAR_USE_GP
//				   AND SC.TRN_EQP_CD    = TS.TRN_EQP_CD
//				   AND SC.STACK_COL_GP  = :V_STACK_COL_GP
//				   AND SC.YD_CAR_USE_GP = 'L'           --L:구내운송, G:출하차량
//				   AND SC.DEL_YN        = 'N'
//				   --AND TS.YD_CAR_PROG_STAT IN ('5') --차량진행상태 5:상차완료
//				   AND TS.YD_CAR_PROG_STAT IN ('3','2') --차량진행상태 5:상차완료
//				   AND TS.DEL_YN        = 'N'
//				*/	   
//				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDTSJ007";

			//} 
		else if("YDTSJ008".equals(msgId)) {
				trtNm = "소재차량상차완료";
				/* 소재차량상차완료(YDTSJ008) - com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDTSJ008 

				SELECT 'YDTSJ008'                          AS JMS_TC_CD            --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				      ,'YDTSJ008'                          AS TC_CODE              --IF구분코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				      ,TRN_EQP_CD           --운송장비코드
				      ,SPOS_WLOC_CD         --발지개소코드
				      ,SPOS_YD_PNT_CD       --발지야드포인트코드
				      ,ARR_WLOC_CD          --착지개소코드
				      ,TRN_WRK_MTL_GP       --운송작업재료구분
				      ,MTL_UGNT_GP          --재료긴급구분
				      ,HCR_GP               --HCR구분
				      ,CARLD_CMPL_DT        --상차완료일시
				      ,CARLD_SH             --상차매수
				      ,SSTL_NO1             --특수강재료번호1
				      ,STL_WT1              --재료중량1
				      ,SSTL_NO2             --특수강재료번호2
				      ,STL_WT2              --재료중량2
				      ,SSTL_NO3             --특수강재료번호3
				      ,STL_WT3              --재료중량3
				      ,SSTL_NO4             --특수강재료번호4
				      ,STL_WT4              --재료중량4
				      ,SSTL_NO5             --특수강재료번호5
				      ,STL_WT5              --재료중량5
				      ,SSTL_NO6             --특수강재료번호6
				      ,STL_WT6              --재료중량6
				      ,SSTL_NO7             --특수강재료번호7
				      ,STL_WT7              --재료중량7
				      ,SSTL_NO8             --특수강재료번호8
				      ,STL_WT8              --재료중량8
				      ,SSTL_NO9             --특수강재료번호9
				      ,STL_WT9              --재료중량9
				      ,SSTL_NO10            --특수강재료번호10
				      ,STL_WT10             --재료중량10
				      ,SSTL_NO11            --특수강재료번호11
				      ,STL_WT11             --재료중량11
				      ,SSTL_NO12            --특수강재료번호12
				      ,STL_WT12             --재료중량12
				      
				FROM (

				    SELECT
				           DD.YD_CAR_SCH_ID
				          ,MAX(DD.TRN_EQP_CD) AS TRN_EQP_CD
				          ,MAX(DD.SPOS_WLOC_CD) AS SPOS_WLOC_CD
				          ,MAX(DD.SPOS_YD_PNT_CD) AS SPOS_YD_PNT_CD
				          ,MAX(DD.ARR_WLOC_CD) AS ARR_WLOC_CD
				          ,MAX(DD.TRN_WRK_MTL_GP) AS TRN_WRK_MTL_GP
				          ,MAX(DD.MTL_UGNT_GP) AS MTL_UGNT_GP
				          ,MAX(DD.HCR_GP) AS HCR_GP
				          ,MAX(DD.CARLD_CMPL_DT) AS CARLD_CMPL_DT
				          ,COUNT(*) AS CARLD_SH
				          ,MAX(DECODE(NO,1,DD.STL_NO,'')) AS SSTL_NO1
				          ,MAX(DECODE(NO,1,DD.YD_MTL_WT,'')) AS STL_WT1
				          ,MAX(DECODE(NO,2,DD.STL_NO,'')) AS SSTL_NO2
				          ,MAX(DECODE(NO,2,DD.YD_MTL_WT,'')) AS STL_WT2
				          ,MAX(DECODE(NO,3,DD.STL_NO,'')) AS SSTL_NO3
				          ,MAX(DECODE(NO,3,DD.YD_MTL_WT,'')) AS STL_WT3
				          ,MAX(DECODE(NO,4,DD.STL_NO,'')) AS SSTL_NO4
				          ,MAX(DECODE(NO,4,DD.YD_MTL_WT,'')) AS STL_WT4
				          ,MAX(DECODE(NO,5,DD.STL_NO,'')) AS SSTL_NO5
				          ,MAX(DECODE(NO,5,DD.YD_MTL_WT,'')) AS STL_WT5
				          ,MAX(DECODE(NO,6,DD.STL_NO,'')) AS SSTL_NO6
				          ,MAX(DECODE(NO,6,DD.YD_MTL_WT,'')) AS STL_WT6
				          ,MAX(DECODE(NO,7,DD.STL_NO,'')) AS SSTL_NO7
				          ,MAX(DECODE(NO,7,DD.YD_MTL_WT,'')) AS STL_WT7
				          ,MAX(DECODE(NO,8,DD.STL_NO,'')) AS SSTL_NO8
				          ,MAX(DECODE(NO,8,DD.YD_MTL_WT,'')) AS STL_WT8
				          ,MAX(DECODE(NO,9,DD.STL_NO,'')) AS SSTL_NO9
				          ,MAX(DECODE(NO,9,DD.YD_MTL_WT,'')) AS STL_WT9
				          ,MAX(DECODE(NO,10,DD.STL_NO,'')) AS SSTL_NO10
				          ,MAX(DECODE(NO,10,DD.YD_MTL_WT,'')) AS STL_WT10
				          ,MAX(DECODE(NO,11,DD.STL_NO,'')) AS SSTL_NO11
				          ,MAX(DECODE(NO,11,DD.YD_MTL_WT,'')) AS STL_WT11
				          ,MAX(DECODE(NO,12,DD.STL_NO,'')) AS SSTL_NO12
				          ,MAX(DECODE(NO,12,DD.YD_MTL_WT,'')) AS STL_WT12
				    FROM   (
				                SELECT A.YD_CAR_SCH_ID 
				                      ,A.TRN_EQP_CD 
				                      ,A.SPOS_WLOC_CD 
				                      ,C.YD_PNT_CD AS SPOS_YD_PNT_CD
				                      ,A.ARR_WLOC_CD 
				                      ,DECODE( A.SPOS_WLOC_CD, 'D3Y43', 'S' , DECODE(D.STOCK_ITEM , 'CM', 'C' , 'CG', 'H' , '') ) AS TRN_WRK_MTL_GP -- D3Y43:SLAB야드 > H(SLAB), CM > C(COIL제품), CG > H(열연COIL)
				                      ,'' AS MTL_UGNT_GP
				                      ,B.HCR_GP 
				                      ,TO_CHAR(A.YD_CARLD_CMPL_DT, 'YYYYMMDDHH24MISS') AS CARLD_CMPL_DT
				                      ,B.STL_NO
				                      --,D.YD_MTL_WT 
				                      ,ROWNUM NO
				                      ,E.COIL_WT
				                      ,F.SLAB_WT
				                      ,DECODE( A.SPOS_WLOC_CD, 'D3Y43', F.SLAB_WT , E.COIL_WT ) AS YD_MTL_WT 
				                      
				                  FROM TB_YD_CARSCH A
				                      ,TB_YD_CARFTMVMTL B
				                      ,TB_YM_STACKCOL C
				                      ,TB_YM_STOCK D
				                      ,TB_PT_COILCOMM E
				                      ,VW_YD_SLABCOMM F
				                      
				                 WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				                 AND   A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
				                 AND   A.YD_CARLD_STOP_LOC = C.STACK_COL_GP 
				                 AND   B.STL_NO = D.STOCK_ID
				                 AND   B.STL_NO = E.COIL_NO(+)
				                 AND   B.STL_NO = F.SLAB_NO(+)
				                 
				           ) DD
				    GROUP BY YD_CAR_SCH_ID      

				)    
				*/
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDTSJ008";
				
			} else if("YDTSJ009".equals(msgId)) {
				trtNm = "소재차량하차개시";
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDTSJ009 
				--소재차량하차개시 전문
				SELECT 'YDTSJ009'                          AS JMS_TC_CD          --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				      ,TS.TRN_EQP_CD                                             --운송장비코드
				      ,TS.ARR_WLOC_CD                                            --착지개소코드
				      ,SC.YD_PNT_CD                        AS ARR_YD_PNT_CD      --착지야드포인트코드
				      ,NVL(:V_WR_DT,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')) AS TRN_WRK_ST_DT --운송작업시작일시
				  FROM TB_YM_STACKCOL SC
				      ,TB_YM_CARSCH TS
				 WHERE SC.YD_CAR_USE_GP = TS.YD_CAR_USE_GP
				   AND SC.TRN_EQP_CD    = TS.TRN_EQP_CD
				   AND SC.STACK_COL_GP = :V_STACK_COL_GP
				   AND SC.YD_CAR_USE_GP = 'L'           --구내운송
				   AND SC.DEL_YN        = 'N'
				   AND TS.YD_CAR_PROG_STAT IN ('B','C') --하차도착,검수
				   AND TS.DEL_YN        = 'N'
				*/	   
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDTSJ009";

			} else if("YDTSJ010".equals(msgId)) {
				trtNm = "소재차량하차완료";
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDTSJ010 
				--소재차량하차완료 전문
				SELECT 'YDTSJ010'                          AS JMS_TC_CD          --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				      ,TS.TRN_EQP_CD                                             --운송장비코드
				      ,TS.ARR_WLOC_CD                                            --착지개소코드
				      ,SC.YD_PNT_CD                        AS ARR_YD_PNT_CD      --착지야드포인트코드
				      ,TO_CHAR(TS.YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS') AS CARUD_CMPL_DT --하차완료일시
				  FROM TB_YM_STACKCOL SC
				      ,TB_YD_CARSCH TS
				 WHERE SC.STACK_COL_GP = TS.YD_CARUD_STOP_LOC
				   AND TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID

				*/   
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDTSJ010";
				
			} else if("YDTSJ010_BSLAB".equals(msgId)) {
				trtNm = "소재차량하차완료(B열연 SLAB야드)";
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.TcYDTSJ010_BSLAB 
				--B열연 SLAB 소재차량하차완료 전문
				SELECT 'YDTSJ010'                          AS JMS_TC_CD          --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				      ,TS.TRN_EQP_CD                                             --운송장비코드
				      ,'D3Y43' AS ARR_WLOC_CD                                    --착지개소코드
				      ,SC.YD_PNT_CD                        AS ARR_YD_PNT_CD      --착지야드포인트코드
				      ,TO_CHAR(TS.YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS') AS CARUD_CMPL_DT --하차완료일시
				  FROM TB_YM_STACKCOL SC
				      ,TB_YD_CARSCH TS
				 WHERE SC.STACK_COL_GP = TS.YD_CARUD_STOP_LOC
				   AND TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
   				*/
				jspeed_query_id = "com.inisteel.cim.ym.bslab.dao.BSlabDAO.TcYDTSJ010_BSLAB";
				
				
			} else if("YDTSJ011".equals(msgId)) {
				trtNm = "소재차량 포인트 지시";
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDTSJ011 
				--구내운송 소재차량 포인트 지시(YDTSJ011) 
				SELECT  'YDTSJ011'                          AS JMS_TC_CD            --JMSTC코드
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				      , 'YDTSJ011'                          AS TC_CODE              --IF구분코드
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				      , :V_TRN_EQP_CD AS TRN_EQP_CD      -- 운송장비코드
				      , :V_WLOC_CD    AS WLOC_CD         -- 개소코드
				      , :V_YD_PNT_CD  AS YD_PNT_CD       -- 야드포인트코드
				      , :V_PNT_WO_GP  AS PNT_WO_GP       -- 포인트지시구분
				      , :V_PNT_WO_DT  AS PNT_WO_DT       -- 포인트지시일시
				      , :V_YD_MSG_NM  AS YD_MSG_NM
				      , :V_TRN_WRK_MTL_GP AS TRN_WRK_MTL_GP
				   FROM DUAL
				*/   
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDTSJ011";

/* 진행관리  */			
			} else if("YDPTJ002".equals(msgId)) {
				trtNm = "코일소재이송완료실적";
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDPTJ002 
				SELECT 'YDPTJ002'                          AS JMS_TC_CD          --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				     , 'YDPTJ002'                          AS TC_CODE            --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT     --TC생성일시

				     , A.COIL_NO                           AS STL_NO
				     , A.ORD_NO                            AS ORD_NO
				     , A.ORD_DTL                           AS ORD_DTL
				     , A.PLNT_PROC_CD                      AS PLNT_PROC_CD
				     , A.STL_APPEAR_GP                     AS STL_APPEAR_GP
				     , A.CURR_PROG_CD                      AS CURR_PROG_CD
				     , A.ORD_YEOJAE_GP                     AS ORD_YEOJAE_GP
				     , A.COIL_WT                           AS STL_WT
				     , ''                                  AS DS_MTL_WT
				     , A.RECORD_PROG_STAT                  AS MTL_STAT_GP
				     , A.RECORD_END_GP                     AS RECORD_END_GP
				     , ''                                  AS RECORD_END_GP1
				     , A.BEFO_PROG_CD                      AS BEFO_PROG_CD
				     , A.BEF_ORD_NO                        AS BEF_ORD_NO
				     , A.BEF_ORD_DTL                       AS BEF_ORD_DTL
				     , A.MMATL_FEE_NO                      AS MMATL_FEE_NO
				     , A.MATCH_ORDERTRANS_GP               AS ORDERTRANS_MATCH_GP
				  FROM TB_PT_COILCOMM A
				 WHERE A.COIL_NO = :V_COIL_NO				
				*/
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDPTJ002";
				
			} else if("YDPTJ003".equals(msgId)) {
				trtNm = "임가공코일소재이송완료실적";
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDPTJ003 
				SELECT 'YDPTJ003'                          AS JMS_TC_CD          --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				     , 'YDPTJ003'                          AS TC_CODE            --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT     --TC생성일시

				     , A.COIL_NO                           AS STL_NO
				     , A.ORD_NO                            AS ORD_NO
				     , A.ORD_DTL                           AS ORD_DTL
				     , A.PLNT_PROC_CD                      AS PLNT_PROC_CD
				     , A.STL_APPEAR_GP                     AS STL_APPEAR_GP
				     , A.CURR_PROG_CD                      AS CURR_PROG_CD
				     , A.ORD_YEOJAE_GP                     AS ORD_YEOJAE_GP
				     , A.COIL_WT                           AS STL_WT
				     , ''                                  AS DS_MTL_WT
				     , A.RECORD_PROG_STAT                  AS MTL_STAT_GP
				     , A.RECORD_END_GP                     AS RECORD_END_GP
				     , ''                                  AS RECORD_END_GP1
				     , A.BEFO_PROG_CD                      AS BEFO_PROG_CD
				     , A.BEF_ORD_NO                        AS BEF_ORD_NO
				     , A.BEF_ORD_DTL                       AS BEF_ORD_DTL
				     , A.MMATL_FEE_NO                      AS MMATL_FEE_NO
				     , A.MATCH_ORDERTRANS_GP               AS ORDERTRANS_MATCH_GP
				  FROM TB_PT_COILCOMM A
				 WHERE A.COIL_NO = :V_COIL_NO				
				*/
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDPTJ003";

			} else if("YDPTJ006".equals(msgId)) {
				trtNm = "냉연코일이송진행 상태실적";
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDPTJ006 
				SELECT 'YDPTJ006'                          AS JMS_TC_CD          --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				     , 'YDPTJ006'                          AS TC_CODE            --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT     --TC생성일시
				     , :V_STL_NO                           AS STL_NO
				     , '6'                                 AS MATL_FTMV_STAT_GP   --재료이송상태
				  FROM DUAL			
				*/
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDPTJ006";

			} else if("YDPTJ007".equals(msgId)) {
				trtNm = "냉연코일이송진행 상태실적";
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDPTJ007
				SELECT 'YDPTJ007'                          AS JMS_TC_CD          --JMSTC코드 
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시 
				     , 'YDPTJ007'                          AS TC_CODE            --IF구분코드 
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT     --TC생성일시 
				     , :V_STL_NO                           AS STL_NO 
				     , :V_SPOS_WLOC_CD                     AS SPOS_WLOC_CD 
				     , :V_ARR_WLOC_CD                      AS ARR_WLOC_CD 
				     , :V_ORD_YEOJAE_GP                    AS ORD_YEOJAE_GP 
				     , :V_RE_WO_LMT_RSN_CD                 AS RE_WO_LMT_RSN_CD 
				     , :V_RE_WO_LMT_YN                     AS RE_WO_LMT_YN 
				     , TO_CHAR(SYSDATE,'YYYYMMDD')         AS CANCEL_DATE 
				  FROM DUAL   
				 
				*/
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDPTJ007";
/* 조업  */ 
			} else if("YMPOJ161".equals(msgId)) {
				trtNm = "조업 송신:코일보급 및 보급 취소 처리";
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMPO161 
				SELECT 'YMPOJ161'                           AS JMS_TC_CD          --JMSTC코드  
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시  
				     , 'YMPOJ161'                           AS tcCode
				     , substr(TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),1,8) AS tcDate
				     , substr(TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),9,6) AS tcTime
				     , 'B'                                 AS plantGbn  
				     , SUBSTR(A.TRK_INFO,1,1)              AS procGbn            --S:1SPM,N:2SPM,H:HFL 
				     , A.STOCK_ID                          AS coilNo  
				     , SUBSTR(A.TRK_INFO,2,1)              AS processId  
				     , TO_CHAR(SYSDATE,'YYYYMMDD')         AS downDate  
				     , TO_CHAR(SYSDATE,'HH24MISS')         AS downTime  
				     , SUBSTR(A.TRK_INFO,2,1)              AS positionNo  
				  FROM (SELECT CASE WHEN SUBSTR(LOC,3,2) IN ('KE') AND SUBSTR(YD_SCH_CD,2,6) IN ('DKE01U','EKE01U') THEN 'N1'  --2SPM 보급존 
				                    WHEN SUBSTR(LOC,3,2) IN ('KE') AND SUBSTR(YD_SCH_CD,3,5) IN ('KE01U'          ) THEN 'S1'  --1SPM 보급존 
				                    WHEN SUBSTR(LOC,3,2) IN ('KE') AND SUBSTR(YD_SCH_CD,3,6) IN ('DKE03U','EKE03U') THEN 'N5'  --2SPM TAKE IN 
				                    WHEN SUBSTR(LOC,3,2) IN ('KE') AND SUBSTR(YD_SCH_CD,3,5) IN ('KE03U'          ) THEN 'S5'  --1SPM TAKE IN 
				                    WHEN SUBSTR(LOC,3,2) IN ('FE') AND SUBSTR(YD_SCH_CD,3,5) IN ('FE01U'          ) THEN 'H1'  --HFL  보급존 
				                    WHEN SUBSTR(LOC,3,2) IN ('FE') AND SUBSTR(YD_SCH_CD,3,5) IN ('FE03U'          ) THEN 'H5'  --HFL  TAKE IN 
				                    WHEN SUBSTR(LOC,3,2) IN ('FE')                                                  THEN 'F1'  --2SPM내 HFL보급 
				                    ELSE '' END AS TRK_INFO 
				            , STOCK_ID 
				         FROM (SELECT :V_YD_DN_WR_LOC  AS LOC  
				                    , STOCK_ID         AS STOCK_ID
				                    , (SELECT YD_SCH_CD FROM TB_YM_CRNSCH WHERE YD_CRN_SCH_ID = C.YD_CRN_SCH_ID) AS YD_SCH_CD
				                 FROM TB_YM_CRNWRKMTL C 
				                WHERE C.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID) 
				       ) A           	
				*/
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMPOJ161";
			} else if("YMPOJ161B".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMPOJ161BackUp 
				SELECT 'YMPOJ161'                          AS JMS_TC_CD          --JMSTC코드  
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시  
				     , 'YMPOJ161'                          AS tcCode
				     , substr(TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),1,8) AS tcDate
				     , substr(TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),9,6) AS tcTime
				     , 'B'                                 AS plantGbn  
				     , SUBSTR(A.TRK_INFO,1,1)              AS procGbn            --S:1SPM,N:2SPM,H:HFL 
				     , A.STOCK_ID                          AS coilNo  
				     , SUBSTR(A.TRK_INFO,2,1)              AS processId  
				     , TO_CHAR(SYSDATE,'YYYYMMDD')         AS downDate  
				     , TO_CHAR(SYSDATE,'HH24MISS')         AS downTime  
				     , SUBSTR(A.TRK_INFO,2,1)              AS positionNo  
				  FROM (SELECT CASE WHEN SUBSTR(LOC,3,2) IN ('KE') AND SUBSTR(YD_SCH_CD,2,6) IN ('DKE01U','EKE01U') THEN 'N1'  --2SPM 보급존 
				                    WHEN SUBSTR(LOC,3,2) IN ('KE') AND SUBSTR(YD_SCH_CD,3,5) IN ('KE01U'          ) THEN 'S1'  --1SPM 보급존 
				                    WHEN SUBSTR(LOC,3,2) IN ('KE') AND SUBSTR(YD_SCH_CD,3,6) IN ('DKE03U','EKE03U') THEN 'N5'  --2SPM TAKE IN 
				                    WHEN SUBSTR(LOC,3,2) IN ('KE') AND SUBSTR(YD_SCH_CD,3,5) IN ('KE03U'          ) THEN 'S5'  --1SPM TAKE IN 
				                    WHEN SUBSTR(LOC,3,2) IN ('FE') AND SUBSTR(YD_SCH_CD,3,5) IN ('FE01U'          ) THEN 'H1'  --HFL  보급존 
				                    WHEN SUBSTR(LOC,3,2) IN ('FE') AND SUBSTR(YD_SCH_CD,3,5) IN ('FE03U'          ) THEN 'H5'  --HFL  TAKE IN 
				                    WHEN SUBSTR(LOC,3,2) IN ('FE')                                                  THEN 'F1'  --2SPM내 HFL보급 
				                    ELSE '' END AS TRK_INFO 
				            , STOCK_ID 
				         FROM (SELECT :V_YD_DN_WR_LOC  AS LOC  
				                    , STOCK_ID         AS STOCK_ID
				                    , (SELECT YD_SCH_CD FROM TB_YM_CRNSCH WHERE YD_CRN_SCH_ID = C.YD_CRN_SCH_ID) AS YD_SCH_CD
				                 FROM TB_YM_CRNWRKMTL C 
				                WHERE C.YD_CRN_SCH_ID = (
				                                        SELECT MAX(CM.YD_CRN_SCH_ID) AS YD_CRN_SCH_ID
				                                          FROM TB_YM_CRNWRKMTL CM
				                                             , TB_YM_CRNSCH    CR
				                                         WHERE CM.YD_CRN_SCH_ID = CR.YD_CRN_SCH_ID
				                                           AND CM.STOCK_ID = :V_STOCK_ID
				                                        )
				                
				              ) 
				       ) A               
				 */
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMPOJ161BackUp";
/* 품질  */ 
			} else if("YDQMJ002".equals(msgId)) {
				trtNm = "품질 송신:열연정정입측보급실적";
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDQMJ002 
				SELECT 'YDQMJ002'                          AS JMS_TC_CD          --JMSTC코드  
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시  
				     , A.PTOP_PLNT_GP                      AS PTOP_PLNT_GP            --IF구분코드  
				     , A.STL_APPEAR_GP                     AS STL_APPEAR_GP     --TC생성일시  
				     , A.COIL_NO                           AS STL_NO  
				  FROM (SELECT B.PTOP_PLNT_GP
				             , B.STL_APPEAR_GP
				             , B.COIL_NO
				          FROM TB_YM_CRNWRKMTL A
				             , TB_PT_COILCOMM  B
				         WHERE A.STOCK_ID = B.COIL_NO
				           AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
				       ) A			
				*/
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDQMJ002";
			} else if ("YDQMJ002B".equals(msgId)) {
				/*
				SELECT 'YDQMJ002'                          AS JMS_TC_CD          --JMSTC코드  
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시  
				     , A.PTOP_PLNT_GP                      AS PTOP_PLNT_GP            --IF구분코드  
				     , A.STL_APPEAR_GP                     AS STL_APPEAR_GP     --TC생성일시  
				     , A.COIL_NO                           AS STL_NO  
				  FROM (SELECT B.PTOP_PLNT_GP
				             , B.STL_APPEAR_GP
				             , B.COIL_NO
				          FROM TB_YM_CRNWRKMTL A
				             , TB_PT_COILCOMM  B
				         WHERE A.STOCK_ID = B.COIL_NO
				           AND A.YD_CRN_SCH_ID = (
				                                  SELECT MAX(CM.YD_CRN_SCH_ID) AS YD_CRN_SCH_ID
				                                    FROM TB_YM_CRNWRKMTL CM
				                                       , TB_YM_CRNSCH    CR
				                                   WHERE CM.YD_CRN_SCH_ID = CR.YD_CRN_SCH_ID
				                                     AND CM.STOCK_ID = :V_STOCK_ID
				                                    )
				       ) A		
				 */
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDQMJ002BackUp";
				
			} else if ("YDCTJ032".equals(msgId)) {
				
				trtNm = "생산통제 장입진행실적";
				/* 생산통제 장입진행실적 - com.inisteel.cim.ym.bslab.dao.BSlabDAO.TcYDCTJ032 

				SELECT DD.JMS_TC_CD                                                   --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')      AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				      ,'HB'                                     AS PTOP_PLNT_GP       --조업공장구분
				      ,'C'                                      AS STL_APPEAR_GP      --재료외형구분(Bloom)
				      ,:V_CHG_SUP_PROG_STAT                     AS CHG_SUP_PROG_STAT  --장입보급진행상태
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')      AS WR_OCCR_DT         --실적발생일시
				      ,TO_CHAR(COUNT(*))                        AS YD_EQP_WR_CNT      --야드설비작업매수
				      ,MIN(DECODE(STACK_LAYER_GP, 1,STOCK_ID))  AS STL_NO1            --재료번호1
				      ,MIN(DECODE(STACK_LAYER_GP, 2,STOCK_ID))  AS STL_NO2            --재료번호2
				      ,MIN(DECODE(STACK_LAYER_GP, 3,STOCK_ID))  AS STL_NO3            --재료번호3
				      ,MIN(DECODE(STACK_LAYER_GP, 4,STOCK_ID))  AS STL_NO4            --재료번호4
				      ,MIN(DECODE(STACK_LAYER_GP, 5,STOCK_ID))  AS STL_NO5            --재료번호5
				      ,MIN(DECODE(STACK_LAYER_GP, 6,STOCK_ID))  AS STL_NO6            --재료번호6
				      ,MIN(DECODE(STACK_LAYER_GP, 7,STOCK_ID))  AS STL_NO7            --재료번호7
				      ,MIN(DECODE(STACK_LAYER_GP, 8,STOCK_ID))  AS STL_NO8            --재료번호8
				      ,MIN(DECODE(STACK_LAYER_GP, 9,STOCK_ID))  AS STL_NO9            --재료번호9
				      ,MIN(DECODE(STACK_LAYER_GP,10,STOCK_ID))  AS STL_NO10           --재료번호10
				      ,MIN(DECODE(STACK_LAYER_GP,11,STOCK_ID))  AS STL_NO11           --재료번호11
				      ,MIN(DECODE(STACK_LAYER_GP,12,STOCK_ID))  AS STL_NO12           --재료번호12

				FROM  (

				        SELECT 'YDCTJ032' AS JMS_TC_CD
				              ,STOCK_ID
				              ,NVL(STACK_LAYER_GP,ROWNUM) AS STACK_LAYER_GP
				        FROM   TB_YM_CRNSCH CS
				              ,TB_YM_CRNWRKMTL CM
				        WHERE  CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				        AND    CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID

				      ) DD */
				jspeed_query_id = "com.inisteel.cim.ym.bslab.dao.BSlabDAO.TcYDCTJ032";
			} else if ("M10YDLMJ1075".equals(msgId)) {
				//*물류진행	 *//
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1075_PIDEV 
				SELECT 
				       'M10YDLMJ1075'                      AS MQ_TC_CD  --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT       --TC생성일시
				     , TRANS_ORD_DATE                      AS TRN_REQ_DATE
				     , TRANS_ORD_SEQNO                     AS TRN_REQ_SEQ     
				     , CAR_NO                              AS CAR_NO
				     , SUBSTR(YD_CARLD_STOP_LOC,1,1)       AS YD_GP
				     , 'H'								   AS DIST_GOODS_GP
				     , ''								   AS YARD_GP
				 --    , CARD_NO                           AS CARD_NO              
				--     , CASE WHEN YD_CAR_PROG_STAT IN ('2','3','4','5') THEN 'U'   
				--			 ELSE 'D' END                   AS UPCARUNLOAD_GP      
				     , CASE WHEN  YD_CAR_PROG_STAT IN ('2','3','4','5') 
				            THEN SUBSTR(TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS'),1,8) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS'),1,8) END  AS CARLOAD_START_DATE
				     , CASE WHEN  YD_CAR_PROG_STAT IN ('2','3','4','5')
				            THEN SUBSTR(TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS'),9,6) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS'),9,6) END  AS CARLOAD_START_TIME 
				  FROM 
				  	   TB_YD_CARSCH C
				 WHERE 
				 	   YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/ 
				trtNm = "임가공이송상차개시";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1075_PIDEV";
			} else if ("M10YDLMJ1095".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1095A_PIDEV 
				SELECT 'M10YDLMJ1095'                        AS MQ_TC_CD              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 	 AS MQ_TC_CREATE_DDTT       --TC생성일시
				     , TRANS_ORD_DATE             			 AS TRN_REQ_DATE
				     , TRANS_ORD_SEQNO                     	 AS TRN_REQ_SEQ
				     , CAR_NO                              	 AS CAR_NO
				-- 	 , CARD_NO                             	 AS CARD_NO               
				     , SUBSTR(YD_CARLD_STOP_LOC,1,1)       	 AS YD_GP
				     , 'H'									 AS DIST_GOODS_GP
				     , ''									 AS YARD_GP     
				     , CASE WHEN YD_CAR_PROG_STAT IN ('3','4','5') 
				            THEN SUBSTR(TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS'),1,8) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS'),1,8) END AS CARLD_CMPL_DATE
				     , CASE WHEN  YD_CAR_PROG_STAT IN ('3','4','5')
				            THEN SUBSTR(TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS'),9,6) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS'),9,6) END AS CARLD_CMPL_TIME        
				  FROM TB_YD_CARSCH C
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/ 
				trtNm = "임가공이송상차완료개시";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1095A_PIDEV";
			} else if ("M10YDLMJ1115".equals(msgId)) {
				 /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1115_PIDEV  
				SELECT 
				       'M10YDLMJ1115'                      AS MQ_TC_CD          --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT       --TC생성일시
				     , TRANS_ORD_DATE                      AS TRN_REQ_DATE
				     , TRANS_ORD_SEQNO                     AS TRN_REQ_SEQ     
				     , CAR_NO                              AS CAR_NO
				     , SUBSTR(YD_CARLD_STOP_LOC,1,1)       AS YD_GP
				     , 'H'			  					   AS DIST_GOODS_GP
				     , ''								   AS YARD_GP
				 --    , CARD_NO                           AS CARD_NO              
				--     , CASE WHEN YD_CAR_PROG_STAT IN ('2','3','4','5') THEN 'U'   
				--			 ELSE 'D' END                   AS UPCARUNLOAD_GP      
				     , CASE WHEN  YD_CAR_PROG_STAT IN ('2','3','4','5') 
				            THEN SUBSTR(TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS'),1,8) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS'),1,8) END  AS CARLOAD_START_DATE
				     , CASE WHEN  YD_CAR_PROG_STAT IN ('2','3','4','5')
				            THEN SUBSTR(TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS'),9,6) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS'),9,6) END  AS CARLOAD_START_TIME 
				  FROM 
				  		 TB_YD_CARSCH C
				 WHERE 
				 			 YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/ 
				trtNm = "임가공이송하차개시";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1115_PIDEV";
			} else if ("M10YDLMJ1011".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYDDMR001 
				 */       				
				trtNm = "입고실적";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1011_PIDEV";
			
			} else if ("M10YDLMJ1031".equals(msgId)) {
				 /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1031_PIDEV 
				 WITH PARA_TABLE AS
				 (
				    SELECT 'M10YDLMJ1031'     AS JMS_TC_CD 
				         , :V_STOCK_ID    AS STOCK_ID
				      FROM DUAL
				 )
				 SELECT 
				        P.JMS_TC_CD                                                                           -- JMSTC코드
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT                             -- JMSTC생성일시
				      , P.JMS_TC_CD AS MQ_TC_CD                                                               -- IF구분코드
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT                              -- 전송일시
				      , B.YD_GP                                                                               -- 야드구분
				      , 'H' AS DIST_GOODS_GP                                                                  -- 출하제품구분
				      , '' AS YARD_GP                                                                         -- 출하창고구분
				      , A.STOCK_ID                            AS GOODS_NO                                       -- 제품 번호
				      , B.YD_STR_LOC                        AS STORE_LOC_CD_FROM                              -- FROM 저장위치
				      , C.STACK_COL_GP||STACK_BED_GP||STACK_LAYER_GP     AS STORE_LOC_CD_TO                -- 저장위치코드
				      , TO_CHAR(SYSDATE,'YYYYMMDD')         AS MOVENSTACK_DATE                                -- 이적 일자
				      , TO_CHAR(SYSDATE,'HH24MISS')         AS MOVENSTACK_TIME                                -- 이적 시각
				   FROM USRYMA.TB_YM_STOCK A
				      , USRPTA.TB_PT_COILCOMM B
				      , USRYMA.TB_YM_STACKLAYER C
				      , PARA_TABLE P
				  WHERE A.STOCK_ID  = B.COIL_NO
				    AND A.STOCK_ID  = P.STOCK_ID    
				    AND A.STOCK_ID  = C.STOCK_ID 
				    AND C.STACK_LAYER_STAT IN('C','U') --적치중, 권상대기
				*/
					trtNm = "코일제품이적작업실적";
					jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1031_PIDEV";				
			} else if ("M10YDLMJ1081A".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1081A_PIDEV 
				SELECT 'M10YDLMJ1081'                      AS MQ_TC_CD           -- IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT  -- TC생성일시
				     , TS.TRANS_ORD_DATE                  AS TRN_REQ_DATE -- 운송지시일자
				     , TS.TRANS_ORD_SEQNO                 AS TRN_REQ_SEQ -- 운송지시순번
				     , SUBSTR(TS.YD_CARLD_STOP_LOC,1,1)    AS YD_GP              -- 야드구분
				--     , TS.CARD_NO                                                -- 카드번호   
				     , TS.CAR_NO                                                 -- 차량번호
				     , 'H'                                 AS DIST_GOODS_GP      -- 출하제품구분
				     , 'N'                                 AS SCH_YN             -- 스케줄여부
				     , :V_GOODS_EA                         AS GOODS_EA           -- 제품개수완료시(*)
				     , :V_STOCK_ID                         AS GOODS_NO           -- 제품번호
				  FROM USRYDA.TB_YD_CARSCH TS
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/ 
				trtNm = "코일일품출하상차실적";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1081A_PIDEV";
				
			} else if ("M10YDLMJ1091A".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1091A_PIDEV 
				--출하상차완료 전문조회 
				SELECT 'M10YDLMJ1091'                           AS MQ_TC_CD            --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')      AS MQ_TC_CREATE_DDTT     --TC생성일시
				     , MIN(TS.TRANS_ORD_DATE )                  AS TRN_REQ_DATE     --운송지시일자
				     , MIN(TS.TRANS_ORD_SEQNO)                  AS TRN_REQ_SEQ    --운송지시순번     
				     , 'H'                                      AS DIST_GOODS_GP
				     , 'N'                                      AS SCH_YN
				--     , TS.CARD_NO 
				     , TS.CAR_NO                                                     --차량번호
				     , MIN(SUBSTR(TS.YD_CARLD_STOP_LOC,1,1))   AS YD_GP  
				     , TO_CHAR(TS.YD_CARLD_CMPL_DT,'YYYYMMDD') AS CARLD_CMPL_DATE   --상차완료일자
				     , TO_CHAR(TS.YD_CARLD_CMPL_DT,'HH24MISS') AS CARLD_CMPL_TIME   --상차완료시각
				  FROM TB_YD_CARSCH     TS
				 WHERE TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 GROUP BY TS.CARD_NO
				        , TS.CAR_NO
				        , TS.YD_CARLD_CMPL_DT
				 */       				
				trtNm = "출하차량상차완료";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1091A_PIDEV";
				
			} else if ("M10YDLMJ1111A".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1111A_PIDEV 
				SELECT  'M10YDLMJ1111'                           AS MQ_TC_CD             --IF구분코드
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')      AS MQ_TC_CREATE_DDTT    --TC생성일시
				--      , TM.CARD_NO
				      , TM.CAR_NO 
				      , TM.YD_GP
				      , 'H'                                      AS DIST_GOODS_GP
				      , ''                                       AS YARD_GP
				      , TM.UPCARUNLOAD_GP
				      , TM.CARLOAD_START_DATE                    AS CARUD_ST_DATE
				      , TM.CARLOAD_START_TIME                    AS CARUD_ST_TIME
				      , '20'                                     AS GOODS_CNT
				      , MAX(DECODE(NO,1 ,TM.STL_NO,''))          AS GOODS_NO1 
					  , MAX(DECODE(NO,1 ,TM.TRANS_ORD_DATE,''))  AS TRANS_WORD_DATE 
					  , MAX(DECODE(NO,1 ,TM.TRANS_ORD_SEQNO,'')) AS TRANS_WORD_SEQNO   
				      , MAX(DECODE(NO,2 ,TM.STL_NO,''))          AS GOODS_NO2 
				      , MAX(DECODE(NO,3 ,TM.STL_NO,''))          AS GOODS_NO3 
				      , MAX(DECODE(NO,4 ,TM.STL_NO,''))          AS GOODS_NO4 
				      , MAX(DECODE(NO,5 ,TM.STL_NO,''))          AS GOODS_NO5 
				      , MAX(DECODE(NO,6 ,TM.STL_NO,''))          AS GOODS_NO6 
				      , MAX(DECODE(NO,7 ,TM.STL_NO,''))          AS GOODS_NO7 
				      , MAX(DECODE(NO,8 ,TM.STL_NO,''))          AS GOODS_NO8 
				      , MAX(DECODE(NO,9 ,TM.STL_NO,''))          AS GOODS_NO9 
				      , MAX(DECODE(NO,10,TM.STL_NO,''))          AS GOODS_NO10
				      , MAX(DECODE(NO,11,TM.STL_NO,''))          AS GOODS_NO11
				      , MAX(DECODE(NO,12,TM.STL_NO,''))          AS GOODS_NO12
				      , MAX(DECODE(NO,13,TM.STL_NO,''))          AS GOODS_NO13
				      , MAX(DECODE(NO,14,TM.STL_NO,''))          AS GOODS_NO14
				      , MAX(DECODE(NO,15,TM.STL_NO,''))          AS GOODS_NO15
				      , MAX(DECODE(NO,16,TM.STL_NO,''))          AS GOODS_NO16
				      , MAX(DECODE(NO,17,TM.STL_NO,''))          AS GOODS_NO17
				      , MAX(DECODE(NO,18,TM.STL_NO,''))          AS GOODS_NO18
				      , MAX(DECODE(NO,19,TM.STL_NO,''))          AS GOODS_NO19
				      , MAX(DECODE(NO,20,TM.STL_NO,''))          AS GOODS_NO20

				   FROM         
				        (SELECT
				                ROWNUM NO 
				              , CASE WHEN  TA.YD_EQP_WRK_STAT ='L' THEN 'D'
				                     ELSE  'U' END  AS UPCARUNLOAD_GP
				              , TX.CAR_CARD_NO 		AS CARD_NO       
				              , (CASE YD_CAR_USE_GP WHEN 'G' THEN TX.CAR_NO2 ELSE TA.TRN_EQP_CD END)		AS CAR_NO  
				              , '3' AS YD_GP
				              , SUBSTR(TA.YD_CARLD_ST_DT,1,8)  AS CARLOAD_START_DATE
				              , SUBSTR(TA.YD_CARLD_ST_DT,9,6)  AS CARLOAD_START_TIME 
				              , TA.STL_NO AS STL_NO
				              , NVL(DM.TRANS_WORD_DATE,NVL(TX.TRANS_ORD_DATE2 ,SUBSTR(TX.TRANS_WORD_NO,1,8))) AS TRANS_ORD_DATE
				              , NVL(DM.TRANS_WORD_SEQNO,NVL(TX.TRANS_ORD_SEQNO2,SUBSTR(TX.TRANS_WORD_NO,9)))  AS TRANS_ORD_SEQNO 
				           FROM 
				               (SELECT A.YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
				                     , A.TRN_EQP_CD AS TRN_EQP_CD
				                     , (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END) AS YD_EQP_WRK_STAT              
				                     , A.CAR_NO AS CAR_NO
				                     , TO_CHAR(NVL(A.YD_CARLD_ST_DT,SYSDATE) , 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT                              
				                     , TO_CHAR(A.YD_CARUD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_ST_DT
				                     , A.CARD_NO AS CARD_NO                 
				                     , C.STL_NO AS STL_NO
				                     , YD_CAR_USE_GP
				                  FROM TB_YD_CARSCH A
				                     , TB_YD_PREPSCH B
				                     , TB_YD_PREPMTL C
				                 WHERE A.YD_CARLD_WRK_BOOK_ID = B.YD_WBOOK_ID
				                   AND B.YD_PREP_SCH_ID=C.YD_PREP_SCH_ID
				                   AND (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END)='U' --상차
				                 UNION ALL
				                SELECT A.YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
				                     , A.TRN_EQP_CD AS TRN_EQP_CD
				                     , (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END) AS YD_EQP_WRK_STAT              
				                     , A.CAR_NO AS CAR_NO
				                     , TO_CHAR(NVL(A.YD_CARLD_ST_DT,SYSDATE) , 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT                              
				                     , TO_CHAR(A.YD_CARUD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_ST_DT
				                     , A.CARD_NO AS CARD_NO                 
				                     , C.STL_NO AS STL_NO
				                     , YD_CAR_USE_GP
				                  FROM TB_YD_CARSCH A
				                     , TB_YD_CARFTMVMTL C
				                 WHERE A.YD_CAR_SCH_ID=C.YD_CAR_SCH_ID
				                   AND (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END)='L' --하차
				                   AND A.DEL_YN='N'
				               ) TA
				               , USRYMA.TB_YM_STOCK TX           
				               , USRYDA.TB_YD_STOCK TY
				               , VW_LM_P_TRANSWORDGOODS DM
				           WHERE TA.STL_NO = TX.STOCK_ID 
				             AND TA.STL_NO = TY.STL_NO(+)
				             AND TA.STL_NO = DM.GOODS_NO(+)
				             AND DM.TRANS_WORD_DATE(+)>=TO_CHAR(SYSDATE-1,'YYYYMMDD')
				             AND YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				             AND DM.DEL_YN='N' 
				             ) TM
				GROUP BY   UPCARUNLOAD_GP,CAR_NO ,YD_GP,CARLOAD_START_DATE, CARLOAD_START_TIME  
				*/
				trtNm = "코일제품고간이송상하차개시";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1111A_PIDEV";

			} else if ("M10YDLMJ1125".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1125_PIDEV 
				SELECT 
				       'M10YDLMJ1125'                         	AS MQ_TC_CD              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 		AS MQ_TC_CREATE_DDTT       --TC생성일시
				     , TRANS_ORD_DATE                      		AS TRANS_REQ_DATE
				     , TRANS_ORD_SEQNO                     		AS TRANS_REQ_SEQNO     
				     , CAR_NO                              		AS CAR_NO
				     , SUBSTR(YD_CARLD_STOP_LOC,1,1)            AS YD_GP		 
				     , 'H'										AS DIST_GOODS_GP
				     , ''										AS YARD_GP     
				     , CASE WHEN YD_CAR_PROG_STAT IN ('3','4','5') 
				            THEN SUBSTR(TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS'),1,8) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS'),1,8) END  AS CARUD_CMPL_DATE
				     , CASE WHEN  YD_CAR_PROG_STAT IN ('3','4','5')
				            THEN SUBSTR(TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS'),9,6) 
				            ELSE SUBSTR(TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS'),9,6) END AS CARUD_CMPL_TIME        
				  FROM 
				  	   TB_YD_CARSCH C
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 */
				trtNm = "임가공이송상차완료";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1125_PIDEV";
				
			} else if("M10YDLMJ1051".equals(msgId)) {
					trtNm = "상차완료(야드핸드링)";
					
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1051_PIDEV 
					SELECT 'M10YDLMJ1051'                          AS MQ_TC_CD            -- IF구분코드
					     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT       -- TC생성일시
					     , :V_YD_GP                            AS YD_GP                 -- 야드구분     
					     , 'H'                                 AS DIST_GOODS_GP         -- 출하제품구분
					     , :V_CAR_NO                           AS CAR_NO                -- 차량번호     
					     , :V_TRANS_ORD_DT                     AS TRN_REQ_DATE          -- 운송지시일자
					     , :V_TRANS_ORD_SEQNO                  AS TRN_REQ_SEQ           -- 운송지시시각
					     , :V_CMBN_CARLD_YN                    AS CMBN_CARLD_YN         -- 조합상차유무
					     , :V_CARLD_PNT_CD                     AS CARLD_PNT_CD          -- 상차포인트코드
					     , :V_HANDLING_CNT                      AS HANDLING_CNT         -- 핸들링횟수
					     , :V_YD_STK_BED_WHIO_STAT             AS YD_STK_BED_WHIO_STAT  -- 야드적치BED입출고상태
					  FROM DUAL 
					*/
					
					jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1051_PIDEV";
					
			} else if("M10YDLMJ1071B".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1071B_PIDEV
				-- 코일이송 상차개시
				SELECT 'M10YDLMJ1071'                       AS MQ_TC_CD              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  AS MQ_TC_CREATE_DDTT       --TC생성일시		
				     , TRANS_ORD_DATE                       AS TRN_REQ_DATE
				     , TRANS_ORD_SEQNO                      AS TRN_REQ_SEQ   
				     , CAR_NO		                        AS CAR_NO
				--     , CARD_NO 		                        AS CARD_NO 
				     , SUBSTR(YD_CARLD_STOP_LOC,1,1)        AS YD_GP   
				     , 'H'                                  AS DIST_GOODS_GP   
				     , 'Y'                                  AS SCH_YN
				     , SUBSTR(YD_CARLD_STOP_LOC,1,1) AS YD_GP
				     , CASE WHEN YD_CARLD_ST_DT IS NOT NULL AND LENGTH(TRIM(YD_CARLD_ST_DT)) = 14  
				            THEN SUBSTR(YD_CARLD_ST_DT,1,8)
				            ELSE '' END                     AS CARLOAD_START_DATE   
				     , CASE WHEN YD_CARLD_ST_DT IS NOT NULL AND LENGTH(TRIM(YD_CARLD_ST_DT)) = 14
				            THEN SUBSTR(YD_CARLD_ST_DT,9,6)
				            ELSE '' END                     AS CARLOAD_START_TIME
				     , (SELECT CR_FRTOMOVE_GP 
				          FROM TB_YM_STOCK
				         WHERE TRANS_ORD_DATE2 = A.TRANS_ORD_DATE
				           AND TRANS_ORD_SEQNO2 = A.TRANS_ORD_SEQNO 
				           AND ROWNUM = 1)                  AS CR_FRTOMOVE_GP   
				  FROM (SELECT A.YD_CAR_SCH_ID              AS YD_CAR_SCH_ID
				             , A.TRN_EQP_CD                 AS TRN_EQP_CD
				             , A.SPOS_WLOC_CD               AS SPOS_WLOC_CD
				             , A.ARR_WLOC_CD                AS ARR_WLOC_CD
				             , A.YD_EQP_WRK_STAT            AS YD_EQP_WRK_STAT
				             , A.YD_CARLD_CMPL_DT           AS YD_CARLD_CMPL_DT
				             , A.YD_CARLD_STOP_LOC          AS YD_CARLD_STOP_LOC
				             , A.CAR_NO                     AS CAR_NO
				             , A.YD_EQP_ID                  AS YD_EQP_ID
				             , TO_CHAR(A.YD_CARLD_ST_DT     , 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT                              
				             , TO_CHAR(A.YD_CARUD_ST_DT     , 'YYYYMMDDHH24MISS') AS YD_CARUD_ST_DT
				             , TO_CHAR(A.YD_CARUD_CMPL_DT   , 'YYYYMMDDHH24MISS') AS YD_CARUD_CMPL_DT
				             , A.CARD_NO                    AS CARD_NO                 
				             , A.DEL_YN 
				             , A.TRANS_ORD_DATE
				             , A.TRANS_ORD_SEQNO
				          FROM TB_YD_CARSCH A
				         WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID 
				           AND A.DEL_YN = 'N'
				        )  A 

				 */
				trtNm = "코일이송 상차개시";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1071B_PIDEV";

			} else if("M10YDLMJ1081B".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1081B_PIDEV
				SELECT 'M10YDLMJ1081'                          AS MQ_TC_CD             -- IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')     AS MQ_TC_CREATE_DDTT    -- TC생성일시	
				     , TB.TRANS_ORD_DATE2                      AS TRN_REQ_DATE         -- 운송의뢰일자
				     , TB.TRANS_ORD_SEQNO2                     AS TRN_REQ_SEQ          -- 운송의뢰순번
				--     , TA.CARD_NO		                       AS CARD_NO              -- 카드번호
				     , TA.CAR_NO		                       AS CAR_NO               -- 차량번호
				     , substr(TA.YD_CARLD_STOP_LOC,1,1)        AS YD_GP                -- 야드구분
				     , 'H'                                     AS DIST_GOODS_GP        -- 출하제품구분
				     , 'Y'                                     AS SCH_YN               -- 스케쥴여부
				     , TB.STOCK_ID                             AS GOODS_NO             -- 제품 번호
				     , TA.YD_CAR_SCH_ID                        AS YD_CAR_SCH_ID        
				--   ,TB.CR_FRTOMOVE_GP                   AS CR_FRTOMOVE_GP
				  FROM TB_YD_CARSCH TA
					 ,TB_YM_STOCK TB
				WHERE TA.TRANS_ORD_DATE = TB.TRANS_ORD_DATE2
				  AND TA.TRANS_ORD_SEQNO = TB.TRANS_ORD_SEQNO2
				  AND TB.STOCK_ID=  :V_STOCK_ID
				  AND TA.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/			
				trtNm = "코일일품출하상차실적";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1081B_PIDEV";

			} else if("M10YDLMJ1091B".equals(msgId)) {
				/**/
				trtNm = "코일이송 상차완료";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1091B_PIDEV";
				
			} else if("M10YDLMJ1111B".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1111B_PIDEV
				SELECT 'M10YDLMJ1111'                      AS MQ_TC_CD            -- IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT   -- TC생성일시
				     , TB.TRANS_ORD_DATE2                  AS TRN_REQ_DATE
				     , TB.TRANS_ORD_SEQNO2                 AS TRN_REQ_SEQ
				--     , TA.CARD_NO		                   AS CARD_NO
				     , TA.CAR_NO	                       AS CAR_NO
				     , substr(TA.YD_CARUD_STOP_LOC,1,1)    AS YD_GP
				     , 'H'                                 AS DIST_GOODS_GP
				     , ''                                  AS YARD_GP
				     , '0'                                 AS GOODS_CNT
				     , substr(TA.YD_CARUD_ST_DT,1,8)	     AS CARUD_ST_DATE
				     , substr(TA.YD_CARUD_ST_DT,9,6)	     AS CARUD_ST_TIME
				     , TB.CR_FRTOMOVE_GP                   AS CR_FRTOMOVE_GP
				  FROM (SELECT A.YD_CAR_SCH_ID      AS YD_CAR_SCH_ID
				             , B.DEL_YN             AS DEL_YN
				             , A.TRN_EQP_CD         AS TRN_EQP_CD
				             , A.SPOS_WLOC_CD       AS SPOS_WLOC_CD
				             , A.ARR_WLOC_CD        AS ARR_WLOC_CD
				             , A.YD_EQP_WRK_STAT    AS YD_EQP_WRK_STAT
				             , A.YD_EQP_WRK_SH      AS YD_EQP_WRK_SH
				             , B.STL_NO             AS STL_NO
				             , A.YD_CARLD_CMPL_DT   AS YD_CARLD_CMPL_DT
				             , A.YD_CARLD_STOP_LOC  AS YD_CARLD_STOP_LOC
				             , B.YD_CAR_UPP_LOC_CD  AS YD_CAR_UPP_LOC_CD
				             , B.YD_STK_BED_NO      AS YD_STK_BED_NO
				             , B.YD_STK_LYR_NO      AS YD_STK_LYR_NO
				             , A.CAR_NO             AS CAR_NO
				             , A.YD_EQP_ID          AS YD_EQP_ID
				             , NVL(TO_CHAR(A.YD_CARUD_ST_DT ,'YYYYMMDDHH24MISS'),:V_WR_DT) AS YD_CARUD_ST_DT
				             , A.CARD_NO            AS CARD_NO                 
				             , A.YD_CARUD_STOP_LOC  AS YD_CARUD_STOP_LOC
				             , A.YD_PNT_CD1
				          FROM TB_YD_CARSCH A, TB_YD_CARFTMVMTL B
				         WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
				           AND A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				           AND ROWNUM = 1) TA
				      ,TB_YM_STOCK TB
				 WHERE TA.STL_NO = TB.STOCK_ID
				*/
				trtNm = "코일이송하차개시전송 PDA";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1111B_PIDEV";

			} else if("M10YDLMJ1121A".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1121A_PIDEV
				SELECT
				       'M10YDLMJ1121'                            AS MQ_TC_CD             -- IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')       AS MQ_TC_CREATE_DDTT    -- TC생성일시
				     , MAX(DECODE(NO,1,DD.TRANS_ORD_DATE,''))    AS TRN_REQ_DATE
				     , MAX(DECODE(NO,1,DD.TRANS_ORD_SEQNO,''))   AS TRN_REQ_SEQ
				     , CAR_NO                                    AS CAR_NO
				     , YD_GP                                     AS YD_GP
				     , 'H'                                       AS DIST_GOODS_GP
				     , ''                                        AS YARD_GP
				     , UPCARUNLOAD_GP                            AS UPCARUNLOAD_GP
				     , YD_PNT_CD                                 AS ARR_YD_PNT_CD
				     , TO_CHAR(SYSDATE,'YYYYMMDD')               AS CARUD_CMPL_DATE
				     , TO_CHAR(SYSDATE,'HH24MISS')               AS CARUD_CMPL_TIME
				     , TO_CHAR(COUNT(*) OVER ())                 AS GOODS_CNT
				     , MAX(DECODE(NO, 1,DD.STL_NO,''))           AS GOODS_NO1
				     , MAX(DECODE(NO, 1,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD1
				     , MAX(DECODE(NO, 2,DD.STL_NO,''))           AS GOODS_NO2
				     , MAX(DECODE(NO, 2,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD2
				     , MAX(DECODE(NO, 3,DD.STL_NO,''))           AS GOODS_NO3
				     , MAX(DECODE(NO, 3,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD3
				     , MAX(DECODE(NO, 4,DD.STL_NO,''))           AS GOODS_NO4
				     , MAX(DECODE(NO, 4,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD4
				     , MAX(DECODE(NO, 5,DD.STL_NO,''))           AS GOODS_NO5
				     , MAX(DECODE(NO, 5,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD5
				     , MAX(DECODE(NO, 6,DD.STL_NO,''))           AS GOODS_NO6
				     , MAX(DECODE(NO, 6,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD6
				     , MAX(DECODE(NO, 7,DD.STL_NO,''))           AS GOODS_NO7
				     , MAX(DECODE(NO, 7,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD7
				     , MAX(DECODE(NO, 8,DD.STL_NO,''))           AS GOODS_NO8
				     , MAX(DECODE(NO, 8,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD8
				     , MAX(DECODE(NO, 9,DD.STL_NO,''))           AS GOODS_NO9
				     , MAX(DECODE(NO, 9,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD9
				     , MAX(DECODE(NO,10,DD.STL_NO,''))           AS GOODS_NO10
				     , MAX(DECODE(NO,10,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD10
				     , MAX(DECODE(NO,11,DD.STL_NO,''))           AS GOODS_NO11
				     , MAX(DECODE(NO,11,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD11
				     , MAX(DECODE(NO,12,DD.STL_NO,''))           AS GOODS_NO12
				     , MAX(DECODE(NO,12,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD12
				     , MAX(DECODE(NO,13,DD.STL_NO,''))           AS GOODS_NO13
				     , MAX(DECODE(NO,13,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD13
				     , MAX(DECODE(NO,14,DD.STL_NO,''))           AS GOODS_NO14
				     , MAX(DECODE(NO,14,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD14
				     , MAX(DECODE(NO,15,DD.STL_NO,''))           AS GOODS_NO15
				     , MAX(DECODE(NO,15,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD15
				     , MAX(DECODE(NO,16,DD.STL_NO,''))           AS GOODS_NO16
				     , MAX(DECODE(NO,16,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD16
				     , MAX(DECODE(NO,17,DD.STORE_LOC_CD,''))     AS GOODS_NO17
				     , MAX(DECODE(NO,17,DD.STL_NO,''))           AS STORE_LOC_CD17
				     , MAX(DECODE(NO,18,DD.STORE_LOC_CD,''))     AS GOODS_NO18
				     , MAX(DECODE(NO,18,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD18
				     , MAX(DECODE(NO,19,DD.STL_NO,''))           AS GOODS_NO19
				     , MAX(DECODE(NO,19,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD19
				     , MAX(DECODE(NO,20,DD.STL_NO,''))           AS GOODS_NO20
				     , MAX(DECODE(NO,20,DD.STORE_LOC_CD,''))     AS STORE_LOC_CD20
				  FROM
				       ( SELECT ROWNUM NO
				              , CASE WHEN  TA.YD_EQP_WRK_STAT ='L' THEN 'D'
				                     ELSE  'U' END              AS UPCARUNLOAD_GP
				              , TX.CAR_CARD_NO                  AS CARD_NO
				              , (CASE YD_CAR_USE_GP WHEN 'G' THEN TX.CAR_NO2 ELSE TA.TRN_EQP_CD END)
				                                                AS CAR_NO
				              , TA.STL_NO                       AS STL_NO
				              , TA.YD_PNT_CD
				              , '3'                             AS YD_GP
				              , NVL(DM.TRANS_WORD_DATE,NVL(TX.TRANS_ORD_DATE2 ,SUBSTR(TX.TRANS_WORD_NO,1,8))) AS TRANS_ORD_DATE
				              , NVL(DM.TRANS_WORD_SEQNO,NVL(TX.TRANS_ORD_SEQNO2,SUBSTR(TX.TRANS_WORD_NO,9)))  AS TRANS_ORD_SEQNO
				              , (SELECT STACK_COL_GP ||STACK_BED_GP ||STACK_LAYER_GP
				                   FROM USRYMA.TB_YM_STACKLAYER
				                  WHERE STOCK_ID  =TA.STL_NO
				                    AND ROWNUM<=1 ) AS STORE_LOC_CD
				              , SUBSTR(TA.YD_CARLD_STOP_LOC,2,1) AS BAY_GP
				              , (SELECT SUBSTR(STACK_COL_GP,3,2)
				                   FROM USRYMA.TB_YM_STACKLAYER
				                  WHERE STOCK_ID =TA.STL_NO
				                  AND ROWNUM<=1 )                AS YD_STK_BED_NO
				              , TA.YD_STK_LYR_NO                 AS YD_STK_LYR_NO
				          FROM (SELECT A.YD_CAR_SCH_ID           AS YD_CAR_SCH_ID
				                     , A.TRN_EQP_CD              AS TRN_EQP_CD
				                     , (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END) AS YD_EQP_WRK_STAT
				                     , A.YD_CARLD_STOP_LOC       AS YD_CARLD_STOP_LOC
				                     , A.CAR_NO AS CAR_NO
				                     , TO_CHAR(A.YD_CARLD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT
				                     , TO_CHAR(A.YD_CARUD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_ST_DT
				                     , A.CARD_NO                 AS CARD_NO
				                     , C.STL_NO                  AS STL_NO
				                     , YD_CAR_USE_GP
				                     , C.YD_STK_LYR_NO           AS YD_STK_LYR_NO
				                     , NVL(A.YD_PNT_CD1,A.YD_PNT_CD2) AS YD_PNT_CD
				                  FROM TB_YD_CARSCH A
				                     , TB_YD_PREPSCH B
				                     , TB_YD_PREPMTL C
				                 WHERE A.YD_CARLD_WRK_BOOK_ID = B.YD_WBOOK_ID
				                   AND B.YD_PREP_SCH_ID=C.YD_PREP_SCH_ID
				                   AND (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END)='U' --상차
				                 UNION ALL
				                SELECT A.YD_CAR_SCH_ID           AS YD_CAR_SCH_ID
				                     , A.TRN_EQP_CD              AS TRN_EQP_CD
				                     , (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END) AS YD_EQP_WRK_STAT
				                     , A.YD_CARLD_STOP_LOC       AS YD_CARLD_STOP_LOC
				                     , A.CAR_NO                  AS CAR_NO
				                     , TO_CHAR(A.YD_CARLD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT
				                     , TO_CHAR(A.YD_CARUD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_ST_DT
				                     , A.CARD_NO                 AS CARD_NO
				                     , C.STL_NO                  AS STL_NO
				                     , YD_CAR_USE_GP
				                     , C.YD_STK_LYR_NO           AS YD_STK_LYR_NO
				                     , NVL(A.YD_PNT_CD1,A.YD_PNT_CD2) AS YD_PNT_CD
				                  FROM TB_YD_CARSCH A
				                     , TB_YD_CARFTMVMTL C
				                 WHERE A.YD_CAR_SCH_ID=C.YD_CAR_SCH_ID
				                   AND (CASE WHEN YD_CAR_PROG_STAT BETWEEN '1' AND '5' THEN 'U' ELSE 'L' END)='L' --하차
				                   AND A.DEL_YN='N'
				               ) TA
				             , USRYMA.TB_YM_STOCK TX
				             , USRYDA.TB_YD_STOCK TY
				             , VW_LM_P_TRANSWORDGOODS DM
				         WHERE TA.STL_NO = TX.STOCK_ID
				           AND TA.STL_NO = TY.STL_NO(+)
				           AND TA.STL_NO = DM.GOODS_NO(+)
				           AND DM.TRANS_WORD_DATE(+)>=TO_CHAR(SYSDATE-1,'YYYYMMDD')
				           AND YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				           AND DM.DEL_YN='N'
				     ) DD
				 GROUP BY   UPCARUNLOAD_GP,CARD_NO,CAR_NO,YD_PNT_CD
				 */
				trtNm = "코일이송하차완료전송 PDA";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1121A_PIDEV";

			} else if("M10YDLMJ1121B".equals(msgId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1121_PIDEV 
				SELECT 
				       'M10YDLMJ1121'                          AS MQ_TC_CD             -- IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')     AS MQ_TC_CREATE_DDTT    -- TC생성일시
				     , TRANS_ORD_DATE                          AS TRN_REQ_DATE
				     , TRANS_ORD_SEQNO                         AS TRN_REQ_SEQ
				--     , CARD_NO		                             AS CARD_NO
				     , CAR_NO		                           AS CAR_NO
				     , SUBSTR(YD_CARUD_STOP_LOC,1,1)           AS YD_GP
				     , 'H'                                     AS DIST_GOODS_GP
				     , ''                                      AS YARD_GP
				     , '0'                                     AS GOODS_CNT
				     , SUBSTR(NVL(TO_CHAR(C.YD_CARUD_CMPL_DT ,'YYYYMMDDHH24MISS'),:V_WR_DT),1,8)	       AS CARUD_CMPL_DATE
				     , SUBSTR(NVL(TO_CHAR(C.YD_CARUD_CMPL_DT ,'YYYYMMDDHH24MISS'),:V_WR_DT),9,6)    	   AS CARUD_CMPL_TIME
				     , (SELECT MIN(B.CR_FRTOMOVE_GP) 
				          FROM TB_YD_CARFTMVMTL A
				             , TB_YD_STOCK      B
				         WHERE 1=1
				           AND A.STL_NO = B.STL_NO
				           AND A.YD_CAR_SCH_ID = C.YD_CAR_SCH_ID
				       ) AS CR_FRTOMOVE_GP
				  FROM TB_YD_CARSCH C
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/
				trtNm = "코일이송하차완료전송 PDA";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcM10YDLMJ1121B_PIDEV";				
			}

			/* 원본
			JDTORecordSet jsRst = null;
			JDTORecordSet addData = JDTORecordFactory.getInstance().createRecordSet("");
			
			if(!"".equals(jspeed_query_id)) {
				trtNm = trtNm + "(" + msgId + ") : ";
				
				jsRst = this.select(jrParam, jspeed_query_id);
					
				commUtils.printLog(logId, trtNm + jsRst.size(), "DB");
				
				//----------------------------------------------------------
				String sITM_ID;
				String sITM_VALUE;
				
				if(jsRst.size()>0) {
					
					JDTORecord jrAdd = JDTORecordFactory.getInstance().create();
					jrAdd.setField("IF_ID",msgId);
					JDTORecordSet jsLayOut = this.select(jrAdd, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getIfTestLayout_PIDEV");
					
					for(int ii = 0; ii < jsRst.size(); ii++) {
						
						for(int jj = 0; jj < jsLayOut.size(); jj++ ) {
							
							sITM_ID = jsLayOut.getRecord(jj).getFieldString("ITM_ID");
							sITM_VALUE = jsRst.getRecord(ii).getFieldString(sITM_ID);
							
							jrAdd.setField(sITM_ID , sITM_VALUE);
							
						}
						addData.addRecord(jrAdd);
					}
				}
				//----------------------------------------------------------
			}
			
			return addData; */
			
			JDTORecordSet jsRst = null;
			
			if(!"".equals(jspeed_query_id)) {
				trtNm = trtNm + "(" + msgId + ") : ";
				
				jsRst = this.select(jrParam, jspeed_query_id);
					
				commUtils.printLog(logId, trtNm + jsRst.size(), "DB");
				
				//---[JMS IF 로그 조회 시 순서바뀜 현상 수정 추가 시작]-------------------------------------------------------
				JDTORecordSet addData = JDTORecordFactory.getInstance().createRecordSet("");
				String sITM_ID;
				String sITM_VALUE;
				
				if(jsRst.size()>0) {
					// PIDEV
					if("M10".equals(msgId.substring(0, 3))) {
						jrParam.setField("PI_YD", "3");
					}
					JDTORecord jrAdd = JDTORecordFactory.getInstance().create();
					jrParam.setField("IF_ID",msgId);
					JDTORecordSet jsLayOut = this.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getIfTestLayout_PIDEV");
					
					for(int ii = 0; ii < jsRst.size(); ii++) {
						
						for(int jj = 0; jj < jsLayOut.size(); jj++ ) {
							
							sITM_ID = jsLayOut.getRecord(jj).getFieldString("ITM_ID");
							sITM_VALUE = jsRst.getRecord(ii).getFieldString(sITM_ID);
							
							jrAdd.setField(sITM_ID , sITM_VALUE);
						}
						addData.addRecord(jrAdd);
					}
					
					jsRst = JDTORecordFactory.getInstance().createRecordSet("");
					jsRst.addAll(addData);
				}
				//---[JMS IF 로그 조회 시 순서바뀜 현상 수정 추가 종료]-------------------------------------------------------
			}
			
			return jsRst;			
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : Sequence ID 조회
	 *      
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String trtGp
	 *      @return String
	 *      @throws DAOException
	*/
	public String getSeqId(String logId, String mthdNm, String trtGp) throws DAOException {
		String methodNm = "SeqID조회[YsCommDAO.getSeqId] < " + mthdNm;
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			String seqId = ""; //반환할 Sequence ID
 
			if ("CrnSch".equals(trtGp)) {
				trtNm = "야드크레인스케쥴ID";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSeqIdCrnSch";
			} else if ("WrkBook".equals(trtGp)) {
				trtNm = "야드작업예약ID";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSeqIdWrkBook";
//			} else if ("PrepSch".equals(trtGp)) {
//				trtNm = "야드준비스케쥴ID";
//				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdPrepSch";
			} else if ("TcarSch".equals(trtGp)) {
				trtNm = "야드대차스케쥴ID";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSeqIdTcarSch";
			} else if ("CarSch".equals(trtGp)) {
				trtNm = "야드차량스케쥴ID";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSeqIdCarSch";
			}  else if ("FtMvWo".equals(trtGp)) {
				trtNm = "이송작업지시번호";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getFrToMoveWordNo";
			}  else if ("RetHt".equals(trtGp)) {
				trtNm = "회송이력ID";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getRetHtHistID";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			JDTORecordSet jsRst = getRecordSet(jspeed_query_id, null);

			if (jsRst.size() > 0) {
				seqId = commUtils.trim(jsRst.getRecord(0).getFieldString("SEQ_ID")); //Sequence ID
			}
			
			return seqId;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}	
	
	/**
	 * 오퍼레이션명 : 야드차량스케쥴ID를 생성하여 반환하는 메소드
	 * @return String : 야드차량스케쥴ID
	 */
	public String getYdCarschId(String logId ) throws DAOException {
		//메소드명
		//String szMethodName = "getYdCarschId";
		String methodNm = "야드차량스케쥴ID[YmCommDAO.getYdCarschId]" ;
		//레코드
		JDTORecord recKey = JDTORecordFactory.getInstance().create();
		//차량스케쥴ID
		String szYdCarSchId = "";
 
		try {
			//JSPEED 쿼리ID
			recKey.setField("JSPEED_QUERY_ID", "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarschId");
			//쿼리 실행
			JDTORecordSet rsTemp = dbAssDao.getRecordSet(recKey);
			if( rsTemp.size() <= 0 ) {
				throw new JDTOException("야드차량스케줄ID 레코드가 존재하지 않음");
			}
			rsTemp.first();
			recKey = rsTemp.getRecord();
			commUtils.trim(recKey.getFieldString("YD_CAR_SCH_ID")) ;
			szYdCarSchId = commUtils.trim(recKey.getFieldString("YD_CAR_SCH_ID")) ;
		}catch(JDTOException e) {
			
			String szMsg ="["+methodNm+"] 야드차량스케줄ID 생성 시 에러 발생";
			commUtils.printLog(logId, szMsg, "SL");	
			throw new DAOException(szMsg, e);
		}
		return szYdCarSchId;
	}
	
	
	/**
	 * 오퍼레이션명 : 벤딩재 처리 (표시/해제)
	 * @return GridData
	 */
	public GridData updStockBendReg(GridData jrParam) throws DAOException {
    
		String methodNm = "벤딩재 처리 (모바일)[updStockBendReg] : ";
		String logId = "updStockBendReg";  //gdReq.getIPAddress();
		String trtNm = ",updStockBendReg";
		int result = 0;

		try {
		
			int processBendingCount = 0;
			String vStock_No = jrParam.getParam("V_STL_NOS");
			String bendingYN = jrParam.getParam("V_BENDING_YN");
			String userId = jrParam.getParam("V_MODIFIER");
			String vStockList[] = vStock_No.split(",");
			Object oParam[]   = null;
			
			System.out.println("   -. 재료정보(Parms) : " + vStockList);
			
			String queryId = "com.inisteel.cim.ym.bcommon.dao.YmCommDao.updStockBendReg";
			
			for (int i = 0; i < vStockList.length; i++) {
				oParam = new Object[] {
						 bendingYN
						,bendingYN
						,bendingYN
						,userId
						,vStockList[i]
				};
	
				// INSERT 쿼리 실행
				result = dbAssDao.trtProcess(queryId, oParam);
				processBendingCount++;
			} // for
	
			System.out.println("   -. 재료정보 벤딩처리 건수 : " + processBendingCount);
	
			return jrParam;

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + ":" + e.getMessage(), e);
		}
	} 	
	/**
	 * 오퍼레이션명 : 마킹재 처리 test (표시/해제)
	 * @return GridData
	 */
	public GridData updStockMarkReg(GridData jrParam) throws DAOException {
    
		String methodNm = "마킹재 처리 (모바일)[updStockBendReg] : ";
		String logId = "updStockMarkReg";  //gdReq.getIPAddress();
		String trtNm = ",updStockMarkReg";
		int result = 0;

		try {
		
			int processMarkingCount = 0;
			String vStock_No = jrParam.getParam("V_STL_NOS");
			String markingYN = jrParam.getParam("V_MARKING_YN");
			String userId = jrParam.getParam("V_MODIFIER");
			String vStockList[] = vStock_No.split(",");
			Object oParam[]   = null;
			
			System.out.println("   -. 재료정보(Parms) : " + vStockList);
			
			String queryId = "com.inisteel.cim.ym.bcommon.dao.YmCommDao.updStockMarkReg";
			
			for (int i = 0; i < vStockList.length; i++) {
				oParam = new Object[] {
						markingYN
						,userId
						,vStockList[i]
				};
	
				// INSERT 쿼리 실행
				result = dbAssDao.trtProcess(queryId, oParam);
				processMarkingCount++;
			} // for
	
			System.out.println("   -. 재료정보 마킹처리 건수 : " + processMarkingCount);
	
			return jrParam;

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + ":" + e.getMessage(), e);
		}
	} 	
	
	/**
	 * 오퍼레이션명 : WebMethod 사용 여부 
	 * @return String : 사용여부 Y:WebMethod 사용 ,N:사용안함
	 */
	public String getWebMothodYn() throws DAOException {
		
		String sFlagYn = "N"; 
		
		try {
			
			JDTORecordSet jsRst = getRecordSet("com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getWebMethodYn", null);
			
			if (jsRst.size() > 0) {
				sFlagYn = commUtils.trim(jsRst.getRecord(0).getFieldString("WEB_METHOD_YN")); //WebMethod 사용 여부
			}
			
		} catch (Exception e) {
			
			return sFlagYn;
		}
			
		return sFlagYn;
	}
	
	/**
     * [A] 오퍼레이션명 : 박판 열연(yf) 신규모듈 적용여부 리턴 메소드
     *
     * @param  void
     * @return JDTORecord
     * @throws DAOException
     * @throws JDTOException
     */
    public JDTORecord getYfNewModuleEffYn() throws DAOException, JDTOException
    {
        JDTORecord      recPara = JDTORecordFactory.getInstance().create();
        JDTORecordSet   rsTemp  = null;

        try
        {
            //query id setting
            recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yf.acommon.dao.YfCommDAO.getNewModuleEffYn");
            rsTemp = dbAssDao.getRecordSet(recPara);

            if(rsTemp.size() <= 0)
            {
                recPara.setField("ASLAB_EFF_YN", "N");
                recPara.setField("ACOIL_EFF_YN", "N");
                recPara.setField("MODULE_YN", "N");
            }
            else
            {
                recPara.setField("ASLAB_EFF_YN", StringHelper.evl(rsTemp.getRecord(0).getFieldString("ASLAB_EFF_YN"),"N"));
                recPara.setField("ACOIL_EFF_YN", StringHelper.evl(rsTemp.getRecord(0).getFieldString("ACOIL_EFF_YN"),"N"));
                recPara.setField("MODULE_YN", StringHelper.evl(rsTemp.getRecord(0).getFieldString("MODULE_YN"),"N"));
            }
        }
        catch (Exception e)
        {
            recPara.setField("ASLAB_EFF_YN", "N");
            recPara.setField("ACOIL_EFF_YN", "N");
            recPara.setField("MODULE_YN", "N");
        }

        return recPara;
    }	

	// PIDEV
	/**
	 *      [A] 오퍼레이션명 :  Tb_YM_RULE_PI 조회
	 *      -- AS_IS SQL Name에 해당하는 TO_BE SQL Name 값을 반환한다.
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String getYmRulePI(String logId, String mthdNms,String sReprCdGp, String sDtlItemAs, String sReprCdGp1, String sCdGp1,String sItem1) throws DAOException {
		String mthdNm = "getYmRulePI 조회[YmCommDAO.getYmRulePI] < " + mthdNms;
		
		String toBeSqlNm = "";

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			/**********************************************************
			* 1. TB_YM_RULE_PI 조회
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam("", mthdNm, "");
			jrParam.setField("REPR_CD_GP_PI"		, sReprCdGp  ); //작업구분(TB_YM_RULE_PI)
			jrParam.setField("DTL_ITEM_AS_PI"      , sDtlItemAs ); //구분(TB_YM_RULE_PI)
			jrParam.setField("REPR_CD_GP"     	, sReprCdGp1 ); //작업구분(TB_YM_RULE)
			jrParam.setField("CD_GP"     		, sCdGp1     ); //코드구분(TB_YM_RULE)
			jrParam.setField("ITEM"     		, sItem1     ); //아이템(TB_YM_RULE)

			/*
				SELECT 
				       DTL_ITEM_TO
				  FROM 
				       TB_YM_RULE_PI
				 WHERE REPR_CD_GP  = :V_REPR_CD_GP_PI
				   AND DTL_ITEM_AS = :V_DTL_ITEM_AS_PI
				   AND DEL_YN      = 'N'
				   AND 'Y' = (
				                SELECT 
				                        NVL(MAX(DTL_ITM1),'N') AS APPLY_YN
				                FROM 
				                        TB_YM_RULE
				                WHERE 
				                      REPR_CD_GP = :V_REPR_CD_GP  -- APP001
				                  AND CD_GP  = :V_CD_GP           -- CD_GP
				                  AND ITEM   = :V_ITEM
				                  AND DEL_YN = 'N'   
				              )
			*/
			//필드명 변환 (필드명 -> V_필드명)
			jrParam = conversionFieldname(jrParam, 0);			
			//query id setting
			jrParam.setField("JSPEED_QUERY_ID", "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRulePi_PIDEV");
			//query execute
			JDTORecordSet jsChk = getRecordSet(jrParam);
			
			if (jsChk.size() > 0) {
				toBeSqlNm    = commUtils.trim(jsChk.getRecord(0).getFieldString("DTL_ITEM_TO"));
			} else {
				toBeSqlNm    = sDtlItemAs;
			}
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return toBeSqlNm;

		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return toBeSqlNm;
		} catch (Exception e) {
			return toBeSqlNm;
		}
	}		    

	/**
	 *      [A] 오퍼레이션명 :  PI시스템 적용 여부_PIDEV
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String ApplyYnPI(String logId, String mthdNms,String sReprCdGp, String sCdGp,String sItem) throws DAOException {
		String mthdNm = "PI시스템 적용여부[YmCommDAO.ApplyYnPI] < " + mthdNms;
		String szAPPLY_YN = "N";

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			//수신 항목 값
			/**********************************************************
			* 2. 열정보 read
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("REPR_CD_GP", sReprCdGp  ); //작업구분
			jrParam.setField("CD_GP"     , sCdGp      ); //구분
			jrParam.setField("ITEM"      , sItem      ); //ITEM

			/* 
				SELECT NVL(MAX(DTL_ITM1),'N') AS APPLY_YN
				  FROM USRYMA.TB_YM_RULE
				 WHERE REPR_CD_GP = :V_REPR_CD_GP  -- APP001
				   AND CD_GP = :V_CD_GP            -- CD_GP
				   AND ITEM  = :V_ITEM
				   AND DEL_YN = 'N'
			*/  
			//필드명 변환 (필드명 -> V_필드명)
			jrParam = conversionFieldname(jrParam, 0);			
			//query id setting
			jrParam.setField("JSPEED_QUERY_ID", "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getBCoilApplyYn_PIDEV");
			//query execute
			JDTORecordSet jsChk = getRecordSet(jrParam);

			if (jsChk.size() > 0) {
				szAPPLY_YN    = commUtils.trim(jsChk.getRecord(0).getFieldString("APPLY_YN"));
			}
           
			commUtils.printLog(logId, mthdNm, "S-");

			return szAPPLY_YN;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return szAPPLY_YN;
		} catch (Exception e) {
			return szAPPLY_YN;
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 :  운송이송구분 반환_PIDEV
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String[] getTrnFrtomoveGpPI(String logId, String mthdNms,String sTrnOrdDate, String sTrnOrdSeqno) throws DAOException {
		String mthdNm = "getTrnFrtomoveGpPI 조회[YmCommDAO.getTrnFrtomoveGpPI] < " + mthdNms;
		
		String transFrtomoveGp = "";
		String hIssueGp = "";
		String[] rVal = new String[2];
	
		try {
			commUtils.printLog(logId, mthdNm, "S+");
	
			/**********************************************************
			* 1. VW_LM_P_TRANSWORDCOMM (운송지시공통) 조회
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam("", mthdNm, "");
			jrParam.setField("TRN_ORD_DATE"		  , sTrnOrdDate  );	//  운송지시일자
			jrParam.setField("TRN_ORD_SEQNO"      , sTrnOrdSeqno ); // 운송지시순번
	
			/*
				SELECT 
				       TRANS_FRTOMOVE_GP
				     , H_ISSUE_GP
				  FROM 
				       VW_LM_P_TRANSWORDCOMM
				 WHERE TRANS_WORD_DATE  = :V_TRN_ORD_DATE
				   AND TRANS_WORD_SEQNO = :V_TRN_ORD_SEQNO
			*/ 
			//필드명 변환 (필드명 -> V_필드명)
			jrParam = conversionFieldname(jrParam, 0);			
			//query id setting
			jrParam.setField("JSPEED_QUERY_ID", "com.inisteel.cim.ydPI.dao.getTrnFrtomoveGpPi_PIDEV");
			//query execute
			JDTORecordSet jsChk = getRecordSet(jrParam);			
			
			if (jsChk.size() > 0) {
				transFrtomoveGp = commUtils.trim(jsChk.getRecord(0).getFieldString("TRANS_FRTOMOVE_GP"));
				hIssueGp = commUtils.trim(jsChk.getRecord(0).getFieldString("H_ISSUE_GP"));
			}
			
			rVal[0] = commUtils.trim(transFrtomoveGp);
			rVal[1] = commUtils.trim(hIssueGp);
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return rVal;
	
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return rVal;
		} catch (Exception e) {
			return rVal;
		}		
	}
}
