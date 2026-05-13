/**
 * @(#)BtYsDAO
 *
 * @version          V1.00
 * @author           조병기
 * @date             2015/01/19
 *
 * @description      BLOOM 야드 화면 처리 DAO
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2015/01/19   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.bt.dao;

import java.sql.Types;
import java.util.Iterator;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.hr.common.util.CmnUtil;
import com.inisteel.cim.ys.common.util.YsCommUtils;
public class BtYsDAO extends DBAssistantDAO {
	
	private YsCommUtils commUtils = new YsCommUtils();

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
		
		String methodNm = "조회[BtYsDAO.jspSelect] < " + mthdNm;
		
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
		
		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			rsTemp = getRecordSet(recPara);
			
			commUtils.printLog(logId, "조회[BtYsDAO.jspSelect] 결과 건수: " + rsTemp.size() , "DB");
			
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
	public JDTORecordSet select2(JDTORecord inRec, String queryId) throws DAOException, JDTOException {
		
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
		
		try {
			
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
		
		String methodNm = trtNm + "[BtYsDAO.select] < " + mthdNm;
		
		JDTORecord recPara = null;	
		
		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			JDTORecordSet rsTemp = getRecordSet(recPara);
			
			commUtils.printLog(logId, "조회[BtYsDAO.select] 결과 건수: " + rsTemp.size() , "DB");
			
			return rsTemp;
			
		} catch (Exception e) {
			
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
	public int update2(JDTORecord inRec, String queryId) throws DAOException, JDTOException {
		
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
		
		String methodNm = trtNm + "[BtYsDAO.update] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			commUtils.printLog(logId, trtNm + "[BtYsDAO.update] 결과 건수: " + intRtnVal , "DB");
			
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
	public int insert2(JDTORecord inRec, String queryId) throws DAOException, JDTOException {
		
		return this.update2(inRec, queryId);
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
		
		String methodNm = trtNm + "[BtYsDAO.insert] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			commUtils.printLog(logId, trtNm + "[BtYsDAO.insert] 결과 건수: " + intRtnVal , "DB");
			
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
	public int delete2(JDTORecord inRec, String queryId) throws DAOException, JDTOException {
		
		return this.update2(inRec, queryId);
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
		
		String methodNm = trtNm + "[BtYsDAO.delete] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			commUtils.printLog(logId, trtNm + "[BtYsDAO.delete] 결과 건수: " + intRtnVal , "DB");
			
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
	
	/**
	 *      [A] 오퍼레이션명 : Batch 처리
	 *      
	 *      @param String trtGp
	 *      @param String[][] param
	 *      @param String logId
	 *      @param String methodNm
	 *      @return int
	 *      @throws DAOException
	*/
	public int upsBatch(String trtGp, String[][] param, String logId, String mthdNm) throws DAOException {
		String methodNm = "공통Batch등록[BtYsDAO.upsBatch] < " + mthdNm;
		String trtNm = "";

		try {
			String jspeed_query_id = "";

			if ("StkLyrStlNo".equals(trtGp)) {
				trtNm = "적치단(TB_YD_STKLYR) 재료번호 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdStkLyrStlNo";
			} else if ("PrepMtl".equals(trtGp)) {
				trtNm = "준비재료(TB_YD_PREPMTL) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.insSlabYdPrepMtl";
			} else if ("WbCrn".equals(trtGp)) {
				trtNm = "작업예약(TB_YD_WRKBOOK) 야드작업계획크레인 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdWbCrn";
			} else if ("WbPrior".equals(trtGp)) {
				trtNm = "작업예약(TB_YD_WRKBOOK) 야드스케쥴우선순위 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdWbPrior";
			} else if ("WrkBookMtl".equals(trtGp)) {
				trtNm = "작업예약재료(TB_YD_WRKBOOKMTL) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.insSlabYdWrkBookMtl";
			} else if ("WrkBookDel".equals(trtGp)) {
				trtNm = "작업예약(TB_YD_WRKBOOK) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updDelYnWrkBook";
			} else if ("WrkBookMtlDel".equals(trtGp)) {
				trtNm = "작업예약재료(TB_YD_WRKBOOKMTL) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updDelYnWrkBookMtl";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			commUtils.printParam(logId, trtNm, param);
			
			int[] trtRst = trtProcess(jspeed_query_id, param);

			return trtRst.length;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	/**
	 * 야드관리 > 야드공통 > 특수강이상재 현황A
	 *
	 * @param inDto
	 * @return
	 * @throws Exception
	 */
	public JDTORecordSet getMtlErrorList(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = null;
		try {
			inDto.setField("JSPEED_QUERY_ID",
			"com.inisteel.cim.ys.bt.dao.BtYsDAO.getMtlErrorList");
			
			// 검색문을 실행합니다.
			outRecordSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.getStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 
		return outRecordSet;
	}	
	/**
	 * [A] 오퍼레이션명 :  특수강이송관리 조회
	 *
	 *	MES_PI 2022.09.20 문예섭
	 * @param GridData gdReq
	 * @return JDTORecordSet
	 * @throws DAOException 
	 * 
	 * //PIDEV 
	 */
	
	public JDTORecordSet getYsSpstFtmv(GridData gdReq) throws DAOException {
		String jspeed_query_id = "com.inisteel.cim.ys.bt.dao.BtYsDAO.getYsSpstFtmv_PIDEV";
		Object[] objs = null;

		try {
			objs = new Object[] {
				 CmnUtil.nvl(gdReq.getParam("V_DATE_FR"   	), "") 	//시작일자
				,CmnUtil.nvl(gdReq.getParam("V_DATE_TO"  	), "")	//종료일자
				,CmnUtil.nvl(gdReq.getParam("V_CD"	), "") 	//구분
				,CmnUtil.nvl(gdReq.getParam("V_STL_NO" 	), "")	//재료번호
			};

			return getRecordSet(jspeed_query_id, objs);
		} catch(Exception e) {
			CmnUtil.printSqlLog(jspeed_query_id, objs);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 특수강이송관리 전송
	 * 
	 * 		MES_PI 2022.09.20 문예섭
	 *      @param String trtGp
	 *      @param Object[] objs
	 *      @return int
	 *      @throws DAOException
	 *      
	 *      //PIDEV 
	*/ 
	
	public int insYsSpstFtmv(Object[] objs) throws DAOException {
		String jspeed_query_id = "com.inisteel.cim.ys.bt.dao.BtYsDAO.insYsSpstFtmv_PIDEV";

		try {
			CmnUtil.printSqlLog(jspeed_query_id, objs);
			
        	return trtProcess(jspeed_query_id, objs);
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
}
