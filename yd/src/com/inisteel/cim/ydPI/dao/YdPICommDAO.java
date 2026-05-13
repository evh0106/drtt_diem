/**
 * @(#)CCommDAO
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2019/05/02
 * 
 * @description      야드관리 공통 DAO
 * ------------------------------------------------------------------------------
 * Ver.   수정일자     요청자  수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2019/05/02   정종균  이현진      최초 등록
 * 
 */
package com.inisteel.cim.ydPI.dao;

import java.sql.Types;
import java.util.Iterator;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ydPI.common.util.PIYdUtils;

import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

/**
 * [A] 클래스명 : 야드관리 공통 DAO
 *
 */
public class YdPICommDAO extends DBAssistantDAO {

	
	private PIYdUtils commPiUtils = new PIYdUtils();
		
	/**
	 *      [A] 오퍼레이션명 : 수신된 전문의 정보를 조회
	 *      
	 *      @param String msgID : 수신된 전문의 MSG_ID
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMsgInfo(String msgID) throws DAOException {
		try {
			/*
			SELECT IF_NM
			      ,PGM_NM1 AS CLASS_NAME
			      ,PGM_NM2 AS METHODE_NAME
			      ,PGM_NM3 AS QUEUE_NAME
			     , BEF_PGM_NM1
			     , BEF_PGM_NM2			      
			  FROM USRYDA.TB_YD_Z_IF
			 WHERE IF_ID = :V_IF_ID
			 */
			return getRecordSet("com.inisteel.cim.yd.ccommon.dao.CCommDAO.getMsgInfo", new Object[] { msgID });
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
//
//	/**
//	 *      [A] 오퍼레이션명 : SELECT 메소드
//	 *      
//	 * @param  JDTORecord    inRec      parameter record
//	 *         String        queryId    QueryId 
//	 * @return JDTORecordSet
//	 * @throws DAOException
//	 * @throws JDTOException 
//	 */	
//	public JDTORecordSet select(JDTORecord inRec, String queryId) throws DAOException, JDTOException {
//		
//		JDTORecord recPara = null;	
//		JDTORecordSet rsTemp = null;
//		try {
//			
//			// PI적용대상
//			queryId = this.getYdRulePI("", "", "YD0001", queryId, "APPPI0", "*", "*" );
//			
//			//필드명 변환 (필드명 -> V_필드명)
//			recPara = conversionFieldname(inRec, 0);
//			//query id setting
//			recPara.setField("JSPEED_QUERY_ID", queryId);
//			//query execute
//			rsTemp = getRecordSet(recPara);
//		 
//		} catch (Exception e) {
//			
//			throw new DAOException(getClass().getName() + e.getMessage(), e);
//		}
//		return rsTemp;
//	}
//	
//	/**
//	 *      [A] 오퍼레이션명 : SELECT 메소드
//	 *      
//	 * @param  JDTORecord    inRec      parameter record
//	 *         String        queryId    QueryId 
//	 *         String     	 logId   	 
//	 *         String     	 mthdNm   	 
//	 *         String     	 trtNm   	 
//	 * @return JDTORecordSet
//	 * @throws DAOException
//	 * @throws JDTOException 
//	 */	
//	public JDTORecordSet select(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {
//		
//		String methodNm = trtNm + "[select] < " + mthdNm;
//		
//		JDTORecord recPara = null;	
//		
//		try {
//			
//			// PI적용대상
//			queryId = this.getYdRulePI("", methodNm, "YD0001", queryId, "APPPI0", "*", "*" );
//			
//			//필드명 변환 (필드명 -> V_필드명)
//			recPara = conversionFieldname(inRec, 0);
//			//query id setting
//			recPara.setField("JSPEED_QUERY_ID", queryId);
//			//query execute
//			JDTORecordSet rsTemp = getRecordSet(recPara);
//			
//			commPiUtils.printLog(logId, trtNm + "[select] 결과 건수: " + rsTemp.size() , "DB");
//			
//			return rsTemp;
//			
//		} catch (Exception e) {
//			
//			throw new DAOException(commPiUtils.makeErrorLog(logId, methodNm, e));
//		}
//	}
//	/**
//	 *      [A] 오퍼레이션명 :  SELECT 메소드
//	 *      
//	 * @param  JDTORecord    inRec      parameter record
//	 *         JDTORecordSet outRecSet  return recordSet
//	 *         String        queryId    QueryId 
//	 * @return int           result size
//	 * @throws DAOException
//	 * @throws JDTOException 
//	 */	
//	public int select(JDTORecord inRec, JDTORecordSet outRecSet, String queryId) throws DAOException, JDTOException {
//		
//		JDTORecord recPara = null;	
//		JDTORecordSet rsTemp = null;
//		
//		try {
//			
//			// PI적용대상
//			queryId = this.getYdRulePI("", "", "YD0001", queryId, "APPPI0", "T", "*" );
//					
//			
//			//필드명 변환 (필드명 -> V_필드명)
//			recPara = conversionFieldname(inRec, 0);
//			//query id setting
//			recPara.setField("JSPEED_QUERY_ID", queryId);
//			//query execute
//			rsTemp = getRecordSet(recPara);
//			
//			if (rsTemp.size() > 0) {
//				outRecSet.addAll(rsTemp);
//			} else {
//				return 0;
//			}
//			
//		} catch (Exception e) {
//			
//			throw new DAOException(getClass().getName() + e.getMessage(), e);
//		}
//		return outRecSet.size();
//	}
	
	/**
	 *      [A] 오퍼레이션명 : 코일야드SELECT 메소드
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         String        queryId    QueryId 
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public JDTORecordSet selectJ(JDTORecord inRec, String queryId) throws DAOException, JDTOException {
		
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
		try {
			
			// PI적용대상
//			queryId = this.getYdRulePI("", "", "YD0001", queryId, "APPPI0", "J", "*" );
	
			
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
	 *      [A] 오퍼레이션명 : 코일야드SELECT 메소드
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
	public JDTORecordSet selectJ(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {
		
		String methodNm = trtNm + "[select] < " + mthdNm;
		
		JDTORecord recPara = null;	
		
		try {
			
			// PI적용대상
//			queryId = this.getYdRulePI("", methodNm, "YD0001", queryId, "APPPI0", "J", "*" );

			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			JDTORecordSet rsTemp = getRecordSet(recPara);
			
			commPiUtils.printLog(logId, trtNm + "[select] 결과 건수: " + rsTemp.size() , "DB");
			
			return rsTemp;
			
		} catch (Exception e) {
			
			throw new DAOException(commPiUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 코일야드 SELECT 메소드
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         String        queryId    QueryId 
	 * @return int           result size
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int selectJ(JDTORecord inRec, JDTORecordSet outRecSet, String queryId) throws DAOException, JDTOException {
		
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
		
		try {
			
			// PI적용대상
//			queryId = this.getYdRulePI("", "", "YD0001", queryId, "APPPI0", "J", "*" );
	
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			rsTemp = getRecordSet(recPara);
			
			if (rsTemp.size() > 0) {
				outRecSet.addAll(rsTemp);
			} else {
				return 0;
			}
			
		} catch (Exception e) {
			
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return outRecSet.size();
	}

	/**
	 *      [A] 오퍼레이션명 : 후판야드SELECT 메소드
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         String        queryId    QueryId 
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public JDTORecordSet selectT(JDTORecord inRec, String queryId) throws DAOException, JDTOException {
		
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
		try {
			
			// PI적용대상
//			queryId = this.getYdRulePI("", "", "YD0001", queryId, "APPPI0", "T", "*" );

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
	 *      [A] 오퍼레이션명 : 후판야드SELECT 메소드
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
	public JDTORecordSet selectT(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {
		
		String methodNm = trtNm + "[select] < " + mthdNm;
		
		JDTORecord recPara = null;	
		
		try {
			
			// PI적용대상
//			queryId = this.getYdRulePI("", methodNm, "YD0001", queryId, "APPPI0", "T", "*" );

			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			JDTORecordSet rsTemp = getRecordSet(recPara);
			
			commPiUtils.printLog(logId, trtNm + "[select] 결과 건수: " + rsTemp.size() , "DB");
			
			return rsTemp;
			
		} catch (Exception e) {
			
			throw new DAOException(commPiUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 후판야드 SELECT 메소드
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         String        queryId    QueryId 
	 * @return int           result size
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int selectT(JDTORecord inRec, JDTORecordSet outRecSet, String queryId) throws DAOException, JDTOException {
		
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
		
		try {
			
			// PI적용대상
//			queryId = this.getYdRulePI("", "", "YD0001", queryId, "APPPI0", "T", "*" );
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			rsTemp = getRecordSet(recPara);
			
			if (rsTemp.size() > 0) {
				outRecSet.addAll(rsTemp);
			} else {
				return 0;
			}
			
		} catch (Exception e) {
			
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return outRecSet.size();
	}
	
	/**
	 *      [A] 오퍼레이션명 : 통합야드SELECT 메소드
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         String        queryId    QueryId 
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public JDTORecordSet selectS(JDTORecord inRec, String queryId) throws DAOException, JDTOException {
		
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
		try {
			
			// PI적용대상
//			queryId = this.getYdRulePI("", "", "YD0001", queryId, "APPPI0", "S", "*" );
			
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
	 *      [A] 오퍼레이션명 : 통합야드SELECT 메소드
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
	public JDTORecordSet selectS(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {
		
		String methodNm = trtNm + "[select] < " + mthdNm;
		
		JDTORecord recPara = null;	
		
		try {
			
			// PI적용대상
//			queryId = this.getYdRulePI("", methodNm, "YD0001", queryId, "APPPI0", "S", "*" );
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			JDTORecordSet rsTemp = getRecordSet(recPara);
			
			commPiUtils.printLog(logId, trtNm + "[select] 결과 건수: " + rsTemp.size() , "DB");
			
			return rsTemp;
			
		} catch (Exception e) {
			
			throw new DAOException(commPiUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 통합야드 SELECT 메소드
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         String        queryId    QueryId 
	 * @return int           result size
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int selectS(JDTORecord inRec, JDTORecordSet outRecSet, String queryId) throws DAOException, JDTOException {
		
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
		
		try {
			
			// PI적용대상
//			queryId = this.getYdRulePI("", "", "YD0001", queryId, "APPPI0", "S", "*" );

			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			rsTemp = getRecordSet(recPara);
			
			if (rsTemp.size() > 0) {
				outRecSet.addAll(rsTemp);
			} else {
				return 0;
			}
			
		} catch (Exception e) {
			
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return outRecSet.size();
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
		
		String methodNm = trtNm + "[YdUpdate] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			commPiUtils.printLog(logId, trtNm + "[YdUpdate] 결과 건수: " + intRtnVal , "DB");
			
		} catch (Exception e) {

			throw new DAOException(commPiUtils.makeErrorLog(logId, methodNm, e));
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
		
		String methodNm = trtNm + "[YdInsert] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			commPiUtils.printLog(logId, trtNm + "[YdInsert] 결과 건수: " + intRtnVal , "DB");
			
		} catch (Exception e) {

			throw new DAOException(commPiUtils.makeErrorLog(logId, methodNm, e));
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
		
		String methodNm = trtNm + "[Ymdelete] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			commPiUtils.printLog(logId, trtNm + "[Ymdelete] 결과 건수: " + intRtnVal , "DB");
			
		} catch (Exception e) {

			throw new DAOException(commPiUtils.makeErrorLog(logId, methodNm, e));
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
	 *      [A] 오퍼레이션명 : INSERT Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */		
	public int insertTx(JDTORecord rcvMsg,String queryId) throws DAOException {

		return this.updateTx(rcvMsg, queryId);
	}	
	
	/**
	 *      [A] 오퍼레이션명 : INSERT Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */			
	public int insertTx(JDTORecord rcvMsg,String queryId, String logId, String mthdNm, String trtNm) throws DAOException {
		
		String methodNm = trtNm + "[insertTx] < " + mthdNm;
		int intRtnVal = 0;
		
		try {
			commPiUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbConn = new EJBConnector("default", "CCommSeEJB", this);
			intRtnVal = ((Integer)ejbConn.trx("execQueryIdTx", new Class[] { JDTORecord.class, String.class }, new Object[] { rcvMsg, queryId })).intValue();
			
			commPiUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, methodNm, e));
		}
		return intRtnVal;
	}	
	
	/**
	 *      [A] 오퍼레이션명 : UPDATE Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */			
	public int updateTx(JDTORecord rcvMsg,String queryId) throws DAOException {
		
		String methodNm = "Transaction 분리메소드 호출 < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		int intRtnVal   = 0;
		
		try {
			commPiUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbConn = new EJBConnector("default", "CCommSeEJB", this);
			intRtnVal = ((Integer)ejbConn.trx("execQueryIdTx", new Class[] { JDTORecord.class, String.class }, new Object[] { rcvMsg, queryId })).intValue();
			
			commPiUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, methodNm, e));
		}
		return intRtnVal;
	}	
	
	/**
	 *      [A] 오퍼레이션명 : UPDATE Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */		
	public int updateTx(JDTORecord rcvMsg,String queryId, String logId, String mthdNm, String trtNm) throws DAOException {
		
		String methodNm = trtNm + "[CCommDao.updateTx] < " + mthdNm;
		int intRtnVal = 0;
		
		try {
			commPiUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbConn = new EJBConnector("default", "CCommSeEJB", this);
			intRtnVal = ((Integer)ejbConn.trx("execQueryIdTx", new Class[] { JDTORecord.class, String.class }, new Object[] { rcvMsg, queryId })).intValue();
			
			commPiUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, methodNm, e));
		}
		return intRtnVal;
	}
				
	
	/**
	 * 오퍼레이션명 : WebMethod 사용 여부 
	 * @return String : 사용여부 Y:WebMethod 사용 ,N:사용안함
	 */
	public String getWebMothodYn() throws DAOException {
		
		String sFlagYn = "N"; 
		
		try {
			
			/*
			SELECT 'YD00' AS REPR_CD_GP
			      ,'Y'    AS WEB_METHOD_YN -- YD WebMethod EAI 사용 여부(Y:사용, N:사용안함)
			  FROM  DUAL
			 */
			JDTORecordSet jsRst = getRecordSet("com.inisteel.cim.yd.ccommon.dao.CCommDAO.getWebMethodYn", null);
			
			if (jsRst.size() > 0) {
				sFlagYn = commPiUtils.trim(jsRst.getRecord(0).getFieldString("WEB_METHOD_YN")); //WebMethod 사용 여부
			}
			
		} catch (Exception e) {
			
			return sFlagYn;
		}
			
		return sFlagYn;
	}
	
	
	/**
	 *      [A] 오퍼레이션명 :  신규시스템 적용 여부_PIDEV1
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String ApplyYnPI(String logId, String mthdNms,String sReprCdGp, String sCdGp,String sItem) throws DAOException {
	
		String mthdNm = "신규시스템 적용여부[CCoilDao.ApplyYn] < " + mthdNms;
		String szAPPLY_YN = "N";
	
		try {
			commPiUtils.printLog(logId, mthdNm, "S+");
	
			//수신 항목 값
			/**********************************************************
			* 2. 열정보 read
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, "");
			jrParam.setField("REPR_CD_GP", sReprCdGp  ); //작업구분
			jrParam.setField("CD_GP"     , sCdGp      ); //구분
			jrParam.setField("ITEM"      , sItem      ); //ITEM
	
			/* 
			SELECT NVL(MAX(ITEM1),'N') AS APPLY_YN 
			  FROM USRYDA.TB_YD_RULE
			 WHERE REPR_CD_GP = :V_REPR_CD_GP  -- APP001
			   AND CD_GP  = :V_CD_GP            -- CD_GP
			   AND ITEM   = :V_ITEM
			   AND DEL_YN = 'N'
			*/  
			//필드명 변환 (필드명 -> V_필드명)
			jrParam = conversionFieldname(jrParam, 0);			
			//query id setting
			jrParam.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCCoilApplyYn_PIDEV");
			//query execute
			JDTORecordSet jsChk = getRecordSet(jrParam);

			if (jsChk.size() > 0) {
				szAPPLY_YN    = commPiUtils.trim(jsChk.getRecord(0).getFieldString("APPLY_YN"));
			}
	        
			commPiUtils.printLog(logId, mthdNm, "S-");
	
			return szAPPLY_YN;
		} catch (DAOException e) {
			commPiUtils.printErrorLog(commPiUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return szAPPLY_YN;
		} catch (Exception e) {
			return szAPPLY_YN;
		}
	}		

	/**
	 *      [A] 오퍼레이션명 :  Tb_YD_RULE_PI 조회1
	 *      -- AS_IS SQL Name에 해당하는 TO_BE SQL Name 값을 반환한다.
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String getYdRulePI(String logId, String mthdNms,String sReprCdGp, String sDtlItemAs, String sReprCdGp1, String sCdGp1,String sItem1) throws DAOException {
		String mthdNm = "getYdRulePI 조회[YdCommDAO.getYdRulePI] < " + mthdNms;
		
		String toBeSqlNm = "";
	
		try {
			commPiUtils.printLog(logId, mthdNm, "S+");
	
			/**********************************************************
			* 1. TB_YD_RULE_PI 조회
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commPiUtils.getParam("", mthdNm, "");
			jrParam.setField("REPR_CD_GP_PI"    , sReprCdGp  ); //작업구분(TB_YD_RULE_PI)
			jrParam.setField("DTL_ITEM_AS_PI"   , sDtlItemAs ); //구분(TB_YD_RULE_PI)
			jrParam.setField("REPR_CD_GP"     	, sReprCdGp1 ); //작업구분(TB_YD_RULE)
			jrParam.setField("CD_GP"     		, sCdGp1     ); //코드구분(TB_YD_RULE)			
			jrParam.setField("ITEM"     		, sItem1     ); //아이템(TB_YD_RULE)
	
			/*
				SELECT 
				       DTL_ITEM_TO
				  FROM 
				       TB_YD_RULE_PI
				 WHERE REPR_CD_GP  = :V_REPR_CD_GP_PI
				   AND DTL_ITEM_AS = :V_DTL_ITEM_AS_PI
				   AND DEL_YN      = 'N'
				   AND 'Y' = (
				                SELECT 
				                        NVL(MAX(ITEM1),'N') AS APPLY_YN
				                FROM 
				                        USRYDA.TB_YD_RULE
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
			jrParam.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdRulePi_PIDEV");
			//query execute
			JDTORecordSet jsChk = getRecordSet(jrParam);			
			
			if (jsChk.size() > 0) {
				toBeSqlNm    = commPiUtils.trim(jsChk.getRecord(0).getFieldString("DTL_ITEM_TO"));
			} else {
				toBeSqlNm    = sDtlItemAs;
			}
			
			
			commPiUtils.printLog(logId, mthdNm, "S-");
			
			return toBeSqlNm;
	
		} catch (DAOException e) {
			commPiUtils.printErrorLog(commPiUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return toBeSqlNm;
		} catch (Exception e) {
			return toBeSqlNm;
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
		String mthdNm = "getTrnFrtomoveGpPI 조회[YdCommDAO.getTrnFrtomoveGpPI] < " + mthdNms;
		
		String transFrtomoveGp = "";
		String hIssueGp = "";
		String[] rVal = new String[2];
	
		try {
			commPiUtils.printLog(logId, mthdNm, "S+");
	
			/**********************************************************
			* 1. VW_LM_P_TRANSWORDCOMM (운송지시공통) 조회
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commPiUtils.getParam("", mthdNm, "");
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
				transFrtomoveGp = commPiUtils.trim(jsChk.getRecord(0).getFieldString("TRANS_FRTOMOVE_GP"));
				hIssueGp = commPiUtils.trim(jsChk.getRecord(0).getFieldString("H_ISSUE_GP"));
			}
			
			rVal[0] = commPiUtils.trim(transFrtomoveGp);
			rVal[1] = commPiUtils.trim(hIssueGp);
			
			commPiUtils.printLog(logId, mthdNm, "S-");
			
			return rVal;
	
		} catch (DAOException e) {
			commPiUtils.printErrorLog(commPiUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return rVal;
		} catch (Exception e) {
			return rVal;
		}		
	}
	/**
	 *      [A] 오퍼레이션명 :  신규시스템 적용 여부
	 *      -- 
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String ApplyYn(String logId, String mthdNms,String sReprCdGp, String sCdGp,String sItem) throws DAOException {

		String mthdNm = "신규시스템 적용여부[YdPICommDAO.ApplyYn] < " + mthdNms;
		String szAPPLY_YN = "N";

		try {
			commPiUtils.printLog(logId, mthdNm, "S+");

			//수신 항목 값
			/**********************************************************
			* 2. 열정보 read
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, "");
			jrParam.setField("REPR_CD_GP", sReprCdGp  ); //작업구분
			jrParam.setField("CD_GP"     , sCdGp      ); //구분
			jrParam.setField("ITEM"      , sItem      ); //ITEM

			//필드명 변환 (필드명 -> V_필드명)
			jrParam = conversionFieldname(jrParam, 0);			
			//query id setting
			/* 
			SELECT NVL(MAX(ITEM1),'N') AS APPLY_YN 
			  FROM USRYDA.TB_YD_RULE
			 WHERE REPR_CD_GP = :V_REPR_CD_GP  -- APP001
			   AND CD_GP  = :V_CD_GP            -- CD_GP
			   AND ITEM   = :V_ITEM
			   AND DEL_YN = 'N'
			*/  
			//JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCCoilApplyYn", logId, mthdNm, "열정보 Read"); 
			jrParam.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getCCoilApplyYn");
			//query execute
			JDTORecordSet jsChk = getRecordSet(jrParam);

			if (jsChk.size() > 0) {
				szAPPLY_YN    = commPiUtils.trim(jsChk.getRecord(0).getFieldString("APPLY_YN"));
			}
			commPiUtils.printLog(logId, mthdNm, "S-");

			return szAPPLY_YN;
		} catch (DAOException e) {
			commPiUtils.printErrorLog(commPiUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return szAPPLY_YN;
		} catch (Exception e) {
			return szAPPLY_YN;
		}
	}		
}
