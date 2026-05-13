/**
 * @(#)YsCommDAO
 *
 * @version          V1.00
 * @author           허철호
 * @date             2012/11/22
 * 
 * @description      야드관리 공통 DAO
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2012/11/22   허철호      허철호      최초 등록
 * v1.10  2014/12/15   윤재광      조병기     yd->ys 변환
 */
package com.inisteel.cim.ys.common.dao;

import java.sql.Types;
import java.util.Iterator;

import xlib.cmc.GridData;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.hr.common.util.CmnUtil;
import com.inisteel.cim.ys.common.util.YsCommUtils;
/**
 * [A] 클래스명 : 야드관리 공통 DAO
 *
 */
public class YsCommDAO extends DBAssistantDAO { 
	
	private DBAssistantDAO assistantDAO = new DBAssistantDAO();
	private YsCommUtils commUtils = new YsCommUtils();
			
	/**
	 *      [A] 오퍼레이션명 : 수신된 전문의 정보를 조회
	 *      
	 *      @param String msgID : 수신된 전문의 MSG_ID
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMsgInfo(String msgID) throws DAOException {
		try {
			return getRecordSet("com.inisteel.cim.ys.common.dao.YsCommDAO.getMsgInfo", new Object[] { msgID });
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
		
		String methodNm = "조회[YsCommDAO.jspSelect] < " + mthdNm;
		
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
		
		try {
			
			// PIDEV
			//queryId = this.getYsRulePI("", methodNm, "YS0001", queryId, "APPPI0", "K");
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			rsTemp = getRecordSet(recPara);
			
			commUtils.printLog(logId, "조회[YsCommDAO.jspSelect] 결과 건수: " + rsTemp.size() , "DB");
			
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
		JDTORecordSet extData =JDTORecordFactory.getInstance().createRecordSet("Temp");
		try {
			
			// PIDEV
			//queryId = this.getYsRulePI("", "YsCommDAO.select2", "YS0001", queryId, "APPPI0", "K");		
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			rsTemp = getRecordSet(recPara);
			
			
			if (rsTemp.size() > 0) {
				JDTORecord jrChk = null;
				jrChk = rsTemp.getRecord(0);
				
				if("YSTSJ008".equals(jrChk.getField("JMS_TC_CD"))){
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("JMS_TC_CD" 	, jrChk.getField("JMS_TC_CD") ); 
					recPara.setField("JMS_TC_CREATE_DDTT" 	, jrChk.getField("JMS_TC_CREATE_DDTT") ); 
					recPara.setField("TC_CODE" 			, jrChk.getField("TC_CODE") ); 
					recPara.setField("TC_CREATE_DDTT" 	, jrChk.getField("TC_CREATE_DDTT") ); 
					recPara.setField("TRN_EQP_CD" 		, jrChk.getField("TRN_EQP_CD") ); 
					recPara.setField("SPOS_WLOC_CD" 	, jrChk.getField("SPOS_WLOC_CD") ); 
					recPara.setField("SPOS_YD_PNT_CD" 	, jrChk.getField("SPOS_YD_PNT_CD") );
					
					recPara.setField("ARR_WLOC_CD" 	, jrChk.getField("ARR_WLOC_CD") );
					recPara.setField("TRN_WRK_MTL_GP" 	, jrChk.getField("TRN_WRK_MTL_GP") );
					recPara.setField("MTL_UGNT_GP" 	, jrChk.getField("MTL_UGNT_GP") );
					recPara.setField("HCR_GP" 		, jrChk.getField("HCR_GP") );
					recPara.setField("CARLD_CMPL_DT" 	, jrChk.getField("CARLD_CMPL_DT") );
					recPara.setField("CARLD_SH" 	, jrChk.getField("CARLD_SH") );
					
					recPara.setField("SSTL_NO1" 	, jrChk.getField("SSTL_NO1") );
					recPara.setField("STL_WT1" 		, jrChk.getField("STL_WT1") );
					recPara.setField("SSTL_LOC1" 	, jrChk.getField("SSTL_LOC1") );
					
					recPara.setField("SSTL_NO2" 	, jrChk.getField("SSTL_NO2") );
					recPara.setField("STL_WT2" 		, jrChk.getField("STL_WT2") );
					recPara.setField("SSTL_LOC2" 	, jrChk.getField("SSTL_LOC2") );
					
					recPara.setField("SSTL_NO3" 	, jrChk.getField("SSTL_NO3") );
					recPara.setField("STL_WT3" 		, jrChk.getField("STL_WT3") );
					recPara.setField("SSTL_LOC3" 	, jrChk.getField("SSTL_LOC3") );
					
					recPara.setField("SSTL_NO4" 	, jrChk.getField("SSTL_NO4") );
					recPara.setField("STL_WT4" 		, jrChk.getField("STL_WT4") );
					recPara.setField("SSTL_LOC4" 	, jrChk.getField("SSTL_LOC4") );
					
					recPara.setField("SSTL_NO5" 	, jrChk.getField("SSTL_NO5") );
					recPara.setField("STL_WT5" 		, jrChk.getField("STL_WT5") );
					recPara.setField("SSTL_LOC5" 	, jrChk.getField("SSTL_LOC5") );
					
					recPara.setField("SSTL_NO6" 	, jrChk.getField("SSTL_NO6") );
					recPara.setField("STL_WT6" 		, jrChk.getField("STL_WT6") );
					recPara.setField("SSTL_LOC6" 	, jrChk.getField("SSTL_LOC6") );
					
					recPara.setField("SSTL_NO7" 	, jrChk.getField("SSTL_NO7") );
					recPara.setField("STL_WT7" 		, jrChk.getField("STL_WT7") );
					recPara.setField("SSTL_LOC7" 	, jrChk.getField("SSTL_LOC7") );
					
					recPara.setField("SSTL_NO8" 	, jrChk.getField("SSTL_NO8") );
					recPara.setField("STL_WT8" 		, jrChk.getField("STL_WT8") );
					recPara.setField("SSTL_LOC8" 	, jrChk.getField("SSTL_LOC8") );
					
					recPara.setField("SSTL_NO9" 	, jrChk.getField("SSTL_NO9") );
					recPara.setField("STL_WT9" 		, jrChk.getField("STL_WT9") );
					recPara.setField("SSTL_LOC9" 	, jrChk.getField("SSTL_LOC9") );
					
					recPara.setField("SSTL_NO10" 	, jrChk.getField("SSTL_NO10") );
					recPara.setField("STL_WT10" 	, jrChk.getField("STL_WT10") );
					recPara.setField("SSTL_LOC10" 	, jrChk.getField("SSTL_LOC10") );
					
					recPara.setField("SSTL_NO11" 	, jrChk.getField("SSTL_NO11") );
					recPara.setField("STL_WT11" 	, jrChk.getField("STL_WT11") );
					recPara.setField("SSTL_LOC11" 	, jrChk.getField("SSTL_LOC11") );
					
					recPara.setField("SSTL_NO12" 	, jrChk.getField("SSTL_NO12") );
					recPara.setField("STL_WT12" 	, jrChk.getField("STL_WT12") );
					recPara.setField("SSTL_LOC12" 	, jrChk.getField("SSTL_LOC12") );
					
					recPara.setField("SSTL_NO13" 	, jrChk.getField("SSTL_NO13") );
					recPara.setField("STL_WT13" 	, jrChk.getField("STL_WT13") );
					recPara.setField("SSTL_LOC13" 	, jrChk.getField("SSTL_LOC13") );
					
					recPara.setField("SSTL_NO14" 	, jrChk.getField("SSTL_NO14") );
					recPara.setField("STL_WT14" 	, jrChk.getField("STL_WT14") );
					recPara.setField("SSTL_LOC14" 	, jrChk.getField("SSTL_LOC14") );
					
					recPara.setField("SSTL_NO15" 	, jrChk.getField("SSTL_NO15") );
					recPara.setField("STL_WT15" 	, jrChk.getField("STL_WT15") );
					recPara.setField("SSTL_LOC15" 	, jrChk.getField("SSTL_LOC15") );
					
					recPara.setField("SSTL_NO16" 	, jrChk.getField("SSTL_NO16") );
					recPara.setField("STL_WT16" 	, jrChk.getField("STL_WT16") );
					recPara.setField("SSTL_LOC16" 	, jrChk.getField("SSTL_LOC16") );
					
					recPara.setField("SSTL_NO17" 	, jrChk.getField("SSTL_NO17") );
					recPara.setField("STL_WT17" 	, jrChk.getField("STL_WT17") );
					recPara.setField("SSTL_LOC17" 	, jrChk.getField("SSTL_LOC17") );
					
					recPara.setField("SSTL_NO18" 	, jrChk.getField("SSTL_NO18") );
					recPara.setField("STL_WT18" 	, jrChk.getField("STL_WT18") );
					recPara.setField("SSTL_LOC18" 	, jrChk.getField("SSTL_LOC18") );
					
					recPara.setField("SSTL_NO19" 	, jrChk.getField("SSTL_NO19") );
					recPara.setField("STL_WT19" 	, jrChk.getField("STL_WT19") );
					recPara.setField("SSTL_LOC19" 	, jrChk.getField("SSTL_LOC19") );
					
					recPara.setField("SSTL_NO20" 	, jrChk.getField("SSTL_NO20") );
					recPara.setField("STL_WT20" 	, jrChk.getField("STL_WT20") );
					recPara.setField("SSTL_LOC20" 	, jrChk.getField("SSTL_LOC20") );
					
					recPara.setField("SSTL_NO21" 	, jrChk.getField("SSTL_NO21") );
					recPara.setField("STL_WT21" 	, jrChk.getField("STL_WT21") );
					recPara.setField("SSTL_LOC21" 	, jrChk.getField("SSTL_LOC21") );
					
					recPara.setField("SSTL_NO22" 	, jrChk.getField("SSTL_NO22") );
					recPara.setField("STL_WT22" 	, jrChk.getField("STL_WT22") );
					recPara.setField("SSTL_LOC22" 	, jrChk.getField("SSTL_LOC22") );
					
					recPara.setField("SSTL_NO23" 	, jrChk.getField("SSTL_NO23") );
					recPara.setField("STL_WT23" 	, jrChk.getField("STL_WT23") );
					recPara.setField("SSTL_LOC23" 	, jrChk.getField("SSTL_LOC23") );
					
					recPara.setField("SSTL_NO24" 	, jrChk.getField("SSTL_NO24") );
					recPara.setField("STL_WT24" 	, jrChk.getField("STL_WT24") );
					recPara.setField("SSTL_LOC24" 	, jrChk.getField("SSTL_LOC24") );
					
					recPara.setField("SSTL_NO25" 	, jrChk.getField("SSTL_NO25") );
					recPara.setField("STL_WT25" 	, jrChk.getField("STL_WT25") );
					recPara.setField("SSTL_LOC25" 	, jrChk.getField("SSTL_LOC25") );
					
					recPara.setField("SSTL_NO26" 	, jrChk.getField("SSTL_NO26") );
					recPara.setField("STL_WT26" 	, jrChk.getField("STL_WT26") );
					recPara.setField("SSTL_LOC26" 	, jrChk.getField("SSTL_LOC26") );
					
					recPara.setField("SSTL_NO27" 	, jrChk.getField("SSTL_NO27") );
					recPara.setField("STL_WT27" 	, jrChk.getField("STL_WT27") );
					recPara.setField("SSTL_LOC27" 	, jrChk.getField("SSTL_LOC27") );
					
					recPara.setField("SSTL_NO28" 	, jrChk.getField("SSTL_NO28") );
					recPara.setField("STL_WT28" 	, jrChk.getField("STL_WT28") );
					recPara.setField("SSTL_LOC28" 	, jrChk.getField("SSTL_LOC28") );
					
					extData.addRecord(recPara);
					return extData;
				}
			}
		 
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
		
		String methodNm = trtNm + "[YsCommDAO.select] < " + mthdNm;
		
		JDTORecord recPara = null;	
		
		try {
			
			// PIDEV
			//queryId = this.getYsRulePI("", "YsCommDAO.select", "YS0001", queryId, "APPPI0", "K");				
						
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			JDTORecordSet rsTemp = getRecordSet(recPara);
			
			commUtils.printLog(logId, "조회[YsCommDAO.select] 결과 건수: " + rsTemp.size() , "DB");
			
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
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public JDTORecordSet select(JDTORecord inRec, String queryId) throws DAOException, JDTOException
	{	
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
		
		try 
		{
			// PIDEV PI_YD
			//String piYd = commUtils.nvl(commUtils.trim(inRec.getFieldString("PI_YD")), "*");
			
			// PIDEV
			//queryId = this.getYfRulePI("", "", "YF0001", queryId, "APPPI0", piYd, "*" );
			
			recPara = conversionFieldname(inRec, 0);		//필드명 변환 (필드명 -> V_필드명)
			recPara.setField("JSPEED_QUERY_ID", queryId);	//query id setting
			rsTemp = super.getRecordSet(recPara);					//query execute
		 
		}
		catch (Exception e)
		{	
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return rsTemp;
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
		
		String methodNm = trtNm + "[YsCommDAO.update] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			commUtils.printLog(logId, trtNm + "[YsCommDAO.update] 결과 건수: " + intRtnVal , "DB");
			
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
		
		String methodNm = trtNm + "[YsCommDAO.insert] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			commUtils.printLog(logId, trtNm + "[YsCommDAO.insert] 결과 건수: " + intRtnVal , "DB");
			
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
		
		String methodNm = trtNm + "[YsCommDAO.delete] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			commUtils.printLog(logId, trtNm + "[YsCommDAO.delete] 결과 건수: " + intRtnVal , "DB");
			
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
		String methodNm = "공통Batch등록[YsCommDAO.upsBatch] < " + mthdNm;
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
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdWbPrior";
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
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdCrnSch";
			} else if ("WrkBook".equals(trtGp)) {
				trtNm = "야드작업예약ID";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdWrkBook";
			} else if ("PrepSch".equals(trtGp)) {
				trtNm = "야드준비스케쥴ID";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdPrepSch";
			} else if ("TcarSch".equals(trtGp)) {
				trtNm = "야드대차스케쥴ID";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdTcarSch";
			} else if ("CarSch".equals(trtGp)) {
				trtNm = "야드차량스케쥴ID";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdCarSch";
			}  else if ("FtMvLot".equals(trtGp)) {
				trtNm = "소재이송lotID";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getFtMvLot";
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
		String methodNm = "L2전문생성[YsCommDAO.getMsgL2] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";

			//--------------------------------------------------------------------------------------------
			//-- BLOOM야드 L2 : N1 ------------------------------------------------------------------------
			if("YSN1L001".equals(msgId)) {//저장위치제원정보
			    
				trtNm = "BLOOM저장위치제원";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN1L001";
				
		    } else if("YSN1L002".equals(msgId)) {//저장품제원정보 
				
		    	trtNm = "BLOOM저장품제원정보";
				
				//야드정보동기화코드 
				String ydInfoSyncCd = commUtils.trim(jrParam.getFieldString("YD_INFO_SYNC_CD"));
				
				if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
					//위치별 >> 1:동,2:SPAN,3:열,4:BED
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN1L002ByLoc";
				} else {
					//재료별 >> 5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제),H:C열연장입,P:1후판장입,Q:2후판장입
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN1L002";
				}
				
		    } else if("YSN1L002ChgWo".equals(msgId)) {//저장품제원정보 (장입지시대상)
				
		    	trtNm = "BLOOM저장품제원정보 (장입지시대상)";
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN1L002ChgWo";
		    	
		    } else if("YSN1L003".equals(msgId)) {//크레인작업지시
				
		    	trtNm = "BLOOM크레인작업지시";
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSNxL003";
		    	
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	
			} else if("YSN1L007".equals(msgId)) {//크레인작업계획 (장입)
				
				trtNm = "BLOOM크레인작업계획";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN1L007";
				
			} else if("YSN1L006".equals(msgId)) { //대차작업실적
			
				trtNm = "BLOOM대차작업실적";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN1L006";
				
			} else if("YSN1L005".equals(msgId)) { //대차출발지시
				
				trtNm = "BLOOM대차출발지시";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN1L005";
				
			} else if("YSM2L101".equals(msgId)) { //Carry-out 완료
				
				trtNm = "BLOOM야드 Carry-out완료";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM2L101";
				
			} else if("YSM2L101BackUp".equals(msgId)) { //Carry-out 완료 BackUp
				
				trtNm = "BLOOM야드 Carry-out완료 BackUp";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM2L101BackUp";
				
			} else if("YSM3L102".equals(msgId)) { //장입이상재 Carry-out 완료
				
				trtNm = "BLOOM야드 장입이상재 Carry-out완료";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM3L102";
				
			} else if("YSM3L102BackUp".equals(msgId)) { //장입이상재 Carry-out 완료 BackUp
				
				trtNm = "BLOOM야드 장입이상재 Carry-out완료 BackUp";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM3L102BackUp";
				
			} else if("YSM3L101".equals(msgId)) { //장입 Carry-in 완료
				
				trtNm = "BLOOM야드 장입 Carry-in완료";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM3L101";
				
			} else if("YSM3L101BackUp".equals(msgId)) { //장입 Carry-in 완료 BackUp
				
				trtNm = "BLOOM야드 장입 Carry-in완료 BackUp";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM3L101BackUp";
			
			} else
			
			//--------------------------------------------------------------------------------------------
			//-- BILLET야드 L2 : N2 ------------------------------------------------------------------------
			if("YSN2L001".equals(msgId)) {//저장위치제원정보
			    
				trtNm = "BILLET저장위치제원";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN2L001";
				
		    }else if("YSN2L002".equals(msgId)) {//저장품제원정보 
				
		    	trtNm = "BILLET저장품제원정보";
				
				//야드정보동기화코드 
				String ydInfoSyncCd = commUtils.trim(jrParam.getFieldString("YD_INFO_SYNC_CD"));
				
				if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
					//위치별 >> 1:동,2:SPAN,3:열,4:BED
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN2L002ByLoc";
				} else {
					//재료별 >> 5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제),H:C열연장입,P:1후판장입,Q:2후판장입
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN2L002";
				}
				
		    } else if("YSN2L002ChgWo".equals(msgId)) {//저장품제원정보 (장입지시대상)
				
		    	trtNm = "BILLET저장품제원정보 (장입지시대상)";
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN2L002ChgWo";
		    	
		    } else if("YSN2L003".equals(msgId)) {//크레인작업지시
				
		    	trtNm = "BILLET크레인작업지시";
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSNxL003";
		    	
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	
			} else if("YSN2L005".equals(msgId)) {//크레인작업계획 (장입)
				
				trtNm = "BILLET크레인작업계획";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN2L005";

			} else if("YSN2L006".equals(msgId)) {//라우팅지시 -> YSM4L001 전문으로 변경됨에 따라 사용안함
				
				trtNm = "BILLET라우팅지시";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN2L006";
			
			} else if("YSM4L001".equals(msgId)) {//라우팅지시 
				
				trtNm = "BILLET라우팅지시";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM4L001";
				
			} else if("YSN2L007".equals(msgId)) {//라우팅지시 
				
				trtNm = "포항BILLET라벨정보";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN2L007";	

			} else if("YSM5L101".equals(msgId)) { //이상재 Carry-out 완료
				
				trtNm = "BILLET야드 이상재 Carry-out 완료";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM5L101";

			} else if("YSM5L101BackUp".equals(msgId)) { //이상재 Carry-out 완료 BackUp
				
				trtNm = "BILLET야드 이상재 Carry-out 완료 BackUp";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM5L101BackUp";
				
			} else if("YSM5L102".equals(msgId)) { //Carry-in 완료
				
				trtNm = "BILLET야드 Carry-in 완료";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM5L102";
				
			} else if("YSM5L102BackUp".equals(msgId)) { //Carry-in 완료 BackUp
				
				trtNm = "BILLET야드 Carry-in 완료 BackUp";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM5L102BackUp";
				
			} else 
			
			//--------------------------------------------------------------------------------------------
			//-- 제품창고 ----------------------------------------------------------------------------------
			//-- N3:선재일반창고 , N4:봉강일반창고, N5:선재자동창고, N6:봉강자동창고
			if("YSN3L001".equals(msgId) || "YSN4L001".equals(msgId) || "YSN5L001".equals(msgId) || "YSN6L001".equals(msgId) ) { //저장위치제원정보
				
		    	trtNm = msgId + " 저장위치제원";
				
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSL2L001";
				
			} else if("YSN3L002".equals(msgId) || "YSN4L002".equals(msgId) || "YSN5L002".equals(msgId) || "YSN6L002".equals(msgId) ) { //저장품제원정보
				
		    	trtNm = msgId + " 저장품제원정보";
				
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	
				//야드정보동기화코드 
				String ydInfoSyncCd = commUtils.trim(jrParam.getFieldString("YD_INFO_SYNC_CD"));
				
				if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
					//위치별 >> 1:동,2:SPAN,3:열,4:BED
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSL2L002ByLoc";
				} else if("6".equals(ydInfoSyncCd) || "A".equals(ydInfoSyncCd)){
					//재료별 >> 6:재공야드 조회
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSL2L002Mobile";
				} else if("6_1".equals(ydInfoSyncCd) ){
					//재료별 >> 6:빌렛 재공야드 조회
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSL2L002Mobile_01";
				} else if("6_2".equals(ydInfoSyncCd) ){
					//재료별 >> 6:번들 재공야드 조회
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSL2L002Mobile_02";	
				} else if("6_3".equals(ydInfoSyncCd) ){
					//재료별 >> 6_3:진양특수강 상차(철분말->진양 상차)/하차(철분말->진양 하차)시, L2 야드 위치 변경 요청전문
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSL2L002Mobile_03";
				} else if("7".equals(ydInfoSyncCd) ){
					//재료별 >> 7:빌렛이송정보 송신
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSL2L002Bt";	
				} else {
					//재료별 >> 5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제),H:C열연장입,P:1후판장입,Q:2후판장입
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSL2L002";
				}
				
		    } else if("YSN3L003".equals(msgId) || "YSN4L003".equals(msgId) ) {//크레인작업지시
				
		    	trtNm = msgId + " 크레인작업지시";
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSNxL003Gds";
		    	
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	
		    } else if("YSN5L003".equals(msgId) || "YSN6L003".equals(msgId)) {//자동화창고 크레인작업지시
				
		    	trtNm = msgId + " 자동화창고 크레인작업지시";
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSNxL003Auto";
		    	
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	
			} else if("YSN4L006".equals(msgId)) { //봉강수동창고 대차작업실적
				
				trtNm = "봉강수동창고 대차작업실적";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN4L006";
				
			} else if("YSN4L005".equals(msgId)) { //대차출발지시
				
				trtNm = "봉강수동창고 대차출발지시";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN4L005";
				
			} else if("YSM6L101".equals(msgId)) { //입고 Carry-out 완료
				
				trtNm = "봉강야드 입고 Carry-out완료";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM6L101";
				
			} else if("YSM6L101BackUp".equals(msgId)) { //입고 Carry-out 완료 BackUp
				
				trtNm = "봉강야드 입고 Carry-out완료 BackUp";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM6L101BackUp";
			
			} else if("YSM9L001".equals(msgId)) { //선재 코일핸들링L2 입고정보 송신
				
				trtNm = "선재 코일핸들링L2 입고정보 송신";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM9L001";
			
			} else if("YSN7L101".equals(msgId)) { //선재야드 입고 라우팅 지시
				
				trtNm = "선재야드 입고 라우팅 지시";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L101";
				
		    } else if("YSN5L005".equals(msgId) || "YSN6L005".equals(msgId)) {//S-크레인 작업순서 변경
				
		    	trtNm = msgId + " S-크레인 작업순서 변경";
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN6L005";
				
		    } else if( "YSN6L005BM".equals(msgId)) {//S-크레인 작업순서 변경/반납 정보 추가
				
		    	trtNm = msgId + " S-크레인 작업순서 변경/반납";
		    	jrParam.setField("TC_CD", "YSN6L005"); //TC_CD 설정
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN6L005BM";
				
		    } else if("YSN5L005All".equals(msgId) || "YSN6L005All".equals(msgId)) {//S-크레인 작업순서 변경(전체)
				
		    	trtNm = msgId + " S-크레인 작업순서 변경";
		    	jrParam.setField("TC_CD", msgId.substring(0,8)); //TC_CD 설정
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN6L005All";
				
		    } else if("YSN5L006".equals(msgId)) {//선재 S-크레인 작업지시
				
		    	trtNm = msgId + " 선재 S-크레인 작업지시";
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN5L006";
				
		    } else if("YSN6L006".equals(msgId)) {//봉강 S-크레인 작업지시
				
		    	trtNm = msgId + " 봉강 S-크레인 작업지시";
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN6L006";
			
		    } else if("YSN7L001".equals(msgId)) {//저장위치 이력정보 수신응답
				
		    	trtNm = msgId + " 저장위치 이력정보 수신응답";
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L001";
		    
		    } else if("YSN7L004".equals(msgId)) {//포항이송재 이송지시
				
		    	trtNm = msgId + " 포항이송재 이송지시";
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L004";
		    	
		    } else if("YSN7L005".equals(msgId)) {//특수강시편채취 전송지시
				
		    	trtNm = msgId + " 특수강시편채취 전송지시";
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	jrParam.setField("HEAT_NO", commUtils.trim(jrParam.getFieldString("HEAT_NO"))); 
		    	jrParam.setField("MATL_LENGTH", commUtils.trim(jrParam.getFieldString("MATL_LENGTH")));
		    	jrParam.setField("MATL_NO", commUtils.trim(jrParam.getFieldString("MATL_NO")));
		    	jrParam.setField("MATL_SIZE", commUtils.trim(jrParam.getFieldString("MATL_SIZE")));
		    	jrParam.setField("MATL_WEIGHT", commUtils.trim(jrParam.getFieldString("MATL_WEIGHT")));
		    	jrParam.setField("REAGENT_NO", commUtils.trim(jrParam.getFieldString("REAGENT_NO")));
		    	jrParam.setField("REAGENT_PICK_LENGTH", commUtils.trim(jrParam.getFieldString("REAGENT_PICK_LENGTH")));
		    	jrParam.setField("REAGENT_PICK_TARGET_YN", commUtils.trim(jrParam.getFieldString("REAGENT_PICK_TARGET_YN")));
		    	jrParam.setField("REMARK_MSG", commUtils.trim(jrParam.getFieldString("REMARK_MSG")));
		    	jrParam.setField("STL_APPEAR_GP", commUtils.trim(jrParam.getFieldString("STL_APPEAR_GP")));
		    	
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L005";
		    } else 
			
		    //------------------------------------------------------------------------------------
		    //-----빌렛정정재공야드 N7--------------------------------------------------------------
		    if("YSN7L002".equals(msgId)) {
		    	trtNm = msgId + " 빌렛정정 야드투입 우선순위";
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L002";
		    } else if("YSN7L003".equals(msgId)) {
		    	trtNm = msgId + " 빌렛정정 저장품제원정보";
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L003NEW";
		    } else
			//--------------------------------------------------------------------------------------------
			//-- 공통 ----------------------------------------------------------------------------------
			if("YSN1L002DnWr".equals(msgId) || "YSN2L002DnWr".equals(msgId) || 
			   "YSN3L002DnWr".equals(msgId) || "YSN4L002DnWr".equals(msgId) ||
			   "YSN5L002DnWr".equals(msgId) || "YSN6L002DnWr".equals(msgId) || 
			   "YSN7L202DnWr".equals(msgId) || "YSN8L002DnWr".equals(msgId) ) {
// 2025.08.20 특수강 정정 야드 (N7), 대형 봉강 옥외 야드   (N8) 추가				
		    	trtNm = msgId + " 권하실적 저장품제원정보";
		    	jrParam.setField("TC_CD", msgId.substring(0,8)); //TC_CD 설정
		    	jrParam.setField("YD_INFO_SYNC_CD", "5"); //야드정보동기화코드 5:지정저장품
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSNxL002DnWr";
		    } else 
//-- 특수강 대형야드 신예화 시작 -----------------------------------------------------------------------------
	// 2025.07.28 ~
		    if("YSN7L201".equals(msgId)) {
		    	trtNm = "[" + methodNm + "] " + msgId + "빌렛정정 저장위치제원정보";
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L201";
		    	
		        String szMsg	= "[" + methodNm + "] " + "********* [TcYSN7L201] param < " 
		                          + "\n\t YD_INFO_SYNC_CD : " 	+ jrParam.getFieldString("YD_INFO_SYNC_CD") 
		                          + "\n\t YS_STK_COL_GP : "   	+ jrParam.getFieldString("YS_STK_COL_GP")
		                          + "\n\t YS_STK_BED_NO : "   	+ jrParam.getFieldString("YS_STK_BED_NO")
		                          ;

				commUtils.printLog(logId, szMsg , "DB");
		    	
		    }			
			else if("YSN7L202".equals(msgId)) { 	// 저장품제원정보 
				
		    	trtNm = "빌렛정정 저장품제원정보";
				
				// 야드정보동기화코드 
				String ydInfoSyncCd = commUtils.trim(jrParam.getFieldString("YD_INFO_SYNC_CD"));
				
				if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
					// 위치별 >> 1:동,2:SPAN,3:열,4:BED
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L202ByLoc";
				} 
				else {
					// 재료별 >> 5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제),H:C열연장입,P:1후판장입,Q:2후판장입
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L202";
				}

		    	
		        String szMsg	= "[" + methodNm + "] " + "********* [TcYSN7L202] param < " 
		                          + "\n\t YD_INFO_SYNC_CD : " 	+ jrParam.getFieldString("YD_INFO_SYNC_CD") 
		                          + "\n\t YS_STK_COL_GP : "   	+ jrParam.getFieldString("YS_STK_COL_GP")
		                          + "\n\t YS_STK_BED_NO : "   	+ jrParam.getFieldString("YS_STK_BED_NO")
		                          + "\n\t YD_GP : "   			+ jrParam.getFieldString("YD_GP")
		                          + "\n\t SSTL_NO : "   		+ jrParam.getFieldString("SSTL_NO")
		                          ;

				commUtils.printLog(logId, szMsg , "DB");
		    	
				
		    } else if("YSN7L203".equals(msgId)) {	// 크레인작업지시
				
		    	trtNm = "빌렛정정 크레인작업지시";
// 2025.11.13 Query 변경			    	
//			    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSNxL003";
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L203";
		    	
		    	jrParam.setField("TC_CD", msgId); 	//TC_CD 설정
		    	
		    } else if("YSN7L205".equals(msgId)) {	// 차량작업예정정보
				
		    	trtNm = "차량작업예정정보";
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L205";
		    	
		    	jrParam.setField("TC_CD", msgId); 	// TC_CD 설정
		    	
		    } else if("YSN7L210".equals(msgId)) {	// 야드적치현황정보요구
				
		    	trtNm = "야드적치현황정보요구";
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L210";
		    	
		    	jrParam.setField("TC_CD", msgId); 	// TC_CD 설정
		    	
		    }
			else if("YSN8L001".equals(msgId)) {
		    	trtNm = "[" + methodNm + "] " + msgId + "대형 봉강 옥외 저장위치제원정보";
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN8L001";
		    	
		        String szMsg	= "[" + methodNm + "] " + "********* [TcYSN7L201] param < " 
		                          + "\n\t YD_INFO_SYNC_CD : " 	+ jrParam.getFieldString("YD_INFO_SYNC_CD") 
		                          + "\n\t YS_STK_COL_GP : "   	+ jrParam.getFieldString("YS_STK_COL_GP")
		                          + "\n\t YS_STK_BED_NO : "   	+ jrParam.getFieldString("YS_STK_BED_NO")
		                          ;

				commUtils.printLog(logId, szMsg , "DB");
		    	
		    }			
			else if("YSN8L002".equals(msgId)) { 	// 저장품제원정보 
				
		    	trtNm = "대형 봉강 옥외  저장품제원정보";
				
				// 야드정보동기화코드 
				String ydInfoSyncCd = commUtils.trim(jrParam.getFieldString("YD_INFO_SYNC_CD"));
				
				if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
					// 위치별 >> 1:동,2:SPAN,3:열,4:BED
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN8L002ByLoc";
				} 
				else {
					// 재료별 >> 5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제),H:C열연장입,P:1후판장입,Q:2후판장입
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN8L002";
				}

		    	
		        String szMsg	= "[" + methodNm + "] " + "********* [TcYSN7L202] param < " 
		                          + "\n\t YD_INFO_SYNC_CD : " 	+ jrParam.getFieldString("YD_INFO_SYNC_CD") 
		                          + "\n\t YS_STK_COL_GP : "   	+ jrParam.getFieldString("YS_STK_COL_GP")
		                          + "\n\t YS_STK_BED_NO : "   	+ jrParam.getFieldString("YS_STK_BED_NO")
		                          + "\n\t YD_GP : "   			+ jrParam.getFieldString("YD_GP")
		                          + "\n\t SSTL_NO : "   		+ jrParam.getFieldString("SSTL_NO")
		                          ;

				commUtils.printLog(logId, szMsg , "DB");
		    	
				
		    } else if("YSN8L003".equals(msgId)) {// 크레인작업지시
				
		    	trtNm = "대형 옥외 크레인작업지시";
// 2025.11.13 Query 변경			    	
//			    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSNxL003";
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN8L003";
		    	
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	
		    } else if("YSN8L005".equals(msgId)) {// 차량작업예정정보
				
		    	trtNm = "차량작업예정정보";
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN8L005";
		    	
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	
		    } else if("YSN8L010".equals(msgId)) {	// 야드적치현황정보요구
				
		    	trtNm = "야드적치현황정보요구";
		    	jrParam.setField("TC_CD", msgId); 	// TC_CD 설정
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN8L010";
		    	
			} else if("YSM3L201".equals(msgId)) { // 대형압연 빌렛/봉강 출하상 추출완료
				
				trtNm = "대형압연 빌렛/봉강 출하상 추출완료";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM3L201";
			} else if("YSM4L211".equals(msgId)) { // PRESS교정 추출완료
				
				trtNm = "PRESS교정 추출완료";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM4L211";
			} else if("YSM4L212".equals(msgId)) { // PRESS교정 Reject완료
				
				trtNm = "PRESS교정 Reject완료";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM4L212";
			} else if("YSM4L214".equals(msgId)) { // PRESS교정 Reject완료
// 2025.10.22 전문 추가					
				trtNm = "ShotBlast Reject완료";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM4L214";
			} else if("YSM4L215".equals(msgId)) { // 빌렛정정출하상 추출완료
				
				trtNm = "빌렛정정출하상 추출완료";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				
				// 야드정보동기화코드 
				String YD_EQP_ID = commUtils.trim(jrParam.getFieldString("YD_EQP_ID"));
				
				if("GEPC05".equals(YD_EQP_ID.substring(0, 6))){
// 2025.11.14 빌렛정정 S/B 크래들(GEPC05) 크레인 작업 실적으로 처리 하는것이 아니라 남42문 차량 도착시 상차완료 하면서 전송 						
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM4L215PC05";
				}
				else {
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM4L215";
				}

			} else if("YSM4L210".equals(msgId)) { // PRESS교정 보급완료
				
				trtNm = "PRESS교정 보급완료";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM4L210";
			} else if("YSM4L213".equals(msgId)) { // ShotBlast 장입대 보급완료
				
				trtNm = "ShotBlast 장입대 보급완료";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM4L213";
			} else if("YSM4L216".equals(msgId)) { // 소형적재테이블 보급완료
				
				trtNm = "소형적재테이블 보급완료";
				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM4L216";
		    } else

//-- 특수강 대형야드 신예화 끝 -----------------------------------------------------------------------------
 
//-- 특수강 소형야드 신예화 시작 -----------------------------------------------------------------------------
// 2026.01.19 ~
			if("YSN7L301".equals(msgId)) {
		    	trtNm = "[" + methodNm + "] " + msgId + "소형야드 저장위치제원정보";
		    	
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L301";
		    	
		        String szMsg	= "[" + methodNm + "] " + "********* [TcYSN7L301] param < " 
		                          + "\n\t YD_INFO_SYNC_CD : " 	+ jrParam.getFieldString("YD_INFO_SYNC_CD") 
		                          + "\n\t YS_STK_COL_GP : "   	+ jrParam.getFieldString("YS_STK_COL_GP")
		                          + "\n\t YS_STK_BED_NO : "   	+ jrParam.getFieldString("YS_STK_BED_NO")
		                          ;

				commUtils.printLog(logId, szMsg , "DB");

			}
			else if("YSN7L302".equals(msgId)) { 	// 저장품제원정보 
				
		    	trtNm = "[" + methodNm + "] " + msgId + "소형야드 저장품제원정보";
				
				// 야드정보동기화코드 
				String ydInfoSyncCd = commUtils.trim(jrParam.getFieldString("YD_INFO_SYNC_CD"));
				
		    	jrParam.setField("TC_CD", msgId); //TC_CD 설정
				
				if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
					// 위치별 >> 1:동,2:SPAN,3:열,4:BED
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L302ByLoc";
				} 
				else {
					// 재료별 >> 5:지정저장품
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L302";
				}

		    	
		        String szMsg	= "[" + methodNm + "] " + "********* [TcYSN7L302] param < " 
		                          + "\n\t YD_INFO_SYNC_CD : " 	+ jrParam.getFieldString("YD_INFO_SYNC_CD") 
		                          + "\n\t YS_STK_COL_GP : "   	+ jrParam.getFieldString("YS_STK_COL_GP")
		                          + "\n\t YS_STK_BED_NO : "   	+ jrParam.getFieldString("YS_STK_BED_NO")
		                          + "\n\t YD_GP : "   			+ jrParam.getFieldString("YD_GP")
		                          + "\n\t SSTL_NO : "   		+ jrParam.getFieldString("SSTL_NO")
		                          ;

				commUtils.printLog(logId, szMsg , "DB");
				
		    } else if("YSN7L303".equals(msgId)) {	// 크레인작업지시
				
		    	trtNm = "[" + methodNm + "] " + msgId + "소형야드 크레인작업지시";
			    	
		    	jrParam.setField("TC_CD", msgId); 	//TC_CD 설정

		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L303";
		    	
		    } else if("YSN7L305".equals(msgId)) {	// 차량작업예정정보
				
		    	trtNm = "[" + methodNm + "] " + msgId + "소형야드 차량작업예정정보";
				
		    	jrParam.setField("TC_CD", msgId); 	// TC_CD 설정

		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L305";
		    	
		    } else if("YSN7L310".equals(msgId)) {	// 야드적치현황정보요구
				
		    	trtNm = "[" + methodNm + "] " + msgId + "소형야드 야드적치현황정보요구";

		    	jrParam.setField("TC_CD", msgId); 	// TC_CD 설정

		    	jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L310";
		    	
			} else if("YSN7L311".equals(msgId)) { // 대차실적(YSN7L311)
// 2026.01.28 L2 요청으로 상차실적,하차실적 대차실적으로 통합
		    	trtNm = "[" + methodNm + "] " + msgId + "소형야드 대차실적";

				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L311";
			} else if("YSN7L312".equals(msgId)) { // 대차이동요구(YSN7L312)
// 2026.01.28 L2 요청으로 YSN7L323 -> YSN7L312 전문ID 변경
		    	trtNm = "[" + methodNm + "] " + msgId + "소형야드 대차이동요구";

				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L312";
			} else if("YSN7L321".equals(msgId)) { // 대차상차실적(YSN7L321)
				
		    	trtNm = "[" + methodNm + "] " + msgId + "소형야드 대차상차실적";

				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L321";
			} else if("YSN7L322".equals(msgId)) { // 대차하차실적(YSN7L322)
				
		    	trtNm = "[" + methodNm + "] " + msgId + "소형야드 대차하차실적";

				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L322";
			} else if("YSN7L323".equals(msgId)) { // 대차이동요구(YSN7L323)
// 2026.01.28 L2 요청으로 YSN7L323 -> YSN7L312 전문ID 변경
		    	trtNm = "[" + methodNm + "] " + msgId + "소형야드 대차이동요구";

				jrParam.setField("TC_CD", msgId); //TC_CD 설정
				
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSN7L323";
			} else if("YSM5L301".equals(msgId)) { // 소형압연 추출대 추출완료
				
		    	trtNm = "[" + methodNm + "] " + msgId + "소형압연 추출대 추출완료";

				jrParam.setField("TC_CD", msgId); //TC_CD 설정

				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM5L301";
			} else if("YSM5L302".equals(msgId)) { // 재정렬재 ReTracking 정보
// 2026.04.06 YSM5L302(재정렬재 ReTracking 정보) 전문 추가
				
		    	trtNm = "[" + methodNm + "] " + msgId + "재정렬재 ReTracking 정보";

				jrParam.setField("TC_CD", msgId); //TC_CD 설정

				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM5L302";
		    }

//-- 특수강 소형야드 신예화 끝 -----------------------------------------------------------------------------
			
			JDTORecordSet jsRst = null;
			
			if(!"".equals(jspeed_query_id)) {
				trtNm = trtNm + "(" + msgId + ") : ";
				
				jsRst = this.select2(jrParam, jspeed_query_id);
					
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
		String methodNm = "L3전문생성[YsCommDAO.getMsgL3] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			
			jrParam.setField("JMS_TC_CD"       , msgId);
			
// 출하	
			if ("YSDSJ001".equals(msgId) ) {
				trtNm = "제품입고작업실적";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ001_PIDEV";
			} else if ("YSDSJ001L2".equals(msgId) ) {
				trtNm = "제품이적작업실적L2";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ001L2_PIDEV";	
			} else if ("YSDSJ001BySchId".equals(msgId) ) {
				trtNm = "제품이적작업실적";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ001BySchId_PIDEV";
			} else if ("YSDSJ001OUT".equals(msgId) ) {
				trtNm = "제품입고사외임가공";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ001OUT_PIDEV";	
			} else if ("YSDSJ002".equals(msgId) ) {
				trtNm = "제품이적작업실적";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ002_PIDEV";
			} else if ("YSDSJ002BySchId".equals(msgId) ) {
				trtNm = "제품이적작업실적";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ002BySchId";
			} else if ("YSDSJ005".equals(msgId)) {
				trtNm = "입동지시 송신";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ005";
			} else if ("YSDSJ007".equals(msgId)) {
				trtNm = "일품출하상차실적";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ007_PIDEV";
			} else if ("YSDSJ008".equals(msgId)) {
				trtNm = "특수강출하상차완료";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ008_PIDEV";
			} else if ("YSDSJ009".equals(msgId)) {
				trtNm = "특수강제품검수완료실적";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ009_PIDEV";
			} else if ("YSDSJ011BySchId".equals(msgId)) {
				trtNm = "반납확인 전문조회";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ011BySchId_PIDEV";
// 구내운송	
			} else if ("YSTSJ007".equals(msgId)) {
				trtNm = "소재 상차개시";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSTSJ007";
			} else if ("YSTSJ008".equals(msgId)) {
				trtNm = "소재 상차완료";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSTSJ008";
			} else if ("YSTSJ009".equals(msgId)) {
				trtNm = "소재 하차개시";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSTSJ009";
			} else if ("YSTSJ010".equals(msgId)) {
				trtNm = "소재 하차완료";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSTSJ010";
			} else if ("YSTSJ011".equals(msgId)) {
				trtNm = "소재차량 포인트 지시";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSTSJ011";
			} else if ("YSTSJ013".equals(msgId)) {
				trtNm = "소재차량 초기화 정보";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSTSJ013";
			} else if ("YSTSJ016".equals(msgId)) {
				trtNm = "소재차량 회송 정보";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSTSJ016";	
// 생산통제
			} else if ("YSCUJ031".equals(msgId)) {
				trtNm = "대형압연장입진행실적";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSCUJ031";
			} else if ("YSCUJ031Backup".equals(msgId)) {
				trtNm = "대형압연장입진행실적 BackUp";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSCUJ031Backup";
			} else if ("YSCUJ032".equals(msgId)) {
				trtNm = "소형압연장입진행실적";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSCUJ032";
			} else if ("YSCUJ032Backup".equals(msgId)) {
				trtNm = "소형압연장입진행실적 BackUp";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSCUJ032Backup";
			} else if ("YSCUJ038".equals(msgId)) {
				trtNm = "소형압연입고실적";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSCUJ038";
			} else if ("YSCUJ038Backup".equals(msgId)) {
				trtNm = "소형압연입고실적 BackUp";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSCUJ038Backup";
			} else if ("YSCUJ038_M4YSL001".equals(msgId)) {
				trtNm = "소형압연입고실적 BackUp";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSCUJ038_M4YSL001";		
//진행관리       
			} else if ("YSPBJ001".equals(msgId)) {
				trtNm = "BLOOM야드에의한진도변경 (저장위치변경)";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.YSPBJ001";
			} else if ("YSPBJ002BySchId".equals(msgId)) {
				trtNm = "BILLET야드에의한진도변경 (권하처리)";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.YSPBJ002BySchId";
			} else if ("YSPBJ002ByStlNo".equals(msgId)) {
				trtNm = "BILLET야드에의한진도변경 (진양 철분말 권하처리)";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.YSPBJ002ByStlNo";
			} else if ("YSPBJ002".equals(msgId)) {
				trtNm = "BILLET야드에의한진도변경 (저장위치변경)";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.YSPBJ002";
			} else if ("YSPBJ004ByStlNo".equals(msgId)) {
					trtNm = "BUNDLE야드에의한진도변경 (저장위치변경)";
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.YSPBJ004ByStlNo";
//압연조업				
			} else if ("YSSBJ001".equals(msgId)) {
				trtNm = "TY BED 인 경우 압연조업으로 반납실적 정보 송신";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSSBJ001_PIDEV";
			} else if ("YSSBJ001CAR".equals(msgId)) {
				trtNm = "TY BED 인 경우 압연조업으로 반납실적 정보 송신";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSSBJ001_CAR_PIDEV";
			}	
			//PIDEV			
			else if ("M10YDLMJ1034".equals(msgId) ) {
				trtNm = "제품이적작업실적";
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.TcM10YDLMJ1034_PIDEV  
				WITH PARA_TABLE AS
				(
				   SELECT :V_JMS_TC_CD AS JMS_TC_CD 
				        , :V_SSTL_NO    AS SSTL_NO
				     FROM DUAL
				)
				SELECT P.JMS_TC_CD                         AS MQ_TC_CD                                           
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT     
				     , 'K'                                 AS YD_GP
				     , 'R'                                 AS DIST_GOODS_GP
				     , ''                                  AS YARD_GP 
				     , A.SSTL_NO                           AS GOODS_NO
				     , B.YS_STR_LOC_HIS1                   AS STORE_LOC_CD_FROM    
				     , A.YS_STR_LOC                        AS STORE_LOC_CD_TO    
				     , TO_CHAR(SYSDATE,'YYYYMMDD')         AS MOVENSTACK_DATE
				     , TO_CHAR(SYSDATE,'HH24MISS')         AS MOVENSTACK_TIME
				  FROM TB_YS_STOCK A
				     , TB_PB_BUNDLECOMM  B
				     , PARA_TABLE P
				 WHERE A.SSTL_NO = B.BNDL_NO
				   AND A.SSTL_NO = P.SSTL_NO    
				*/   
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcM10YDLMJ1034_PIDEV";
			} else if ("M10YDLMJ1034BySchId".equals(msgId) ) {
				trtNm = "제품이적작업실적";
				/* 이적실적 전문조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.TcM10YDLMJ1034BySchId_PIDEV
				SELECT  'M10YDLMJ1034'                                           AS MQ_TC_CD             --IF구분코드
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')                      AS MQ_TC_CREATE_DDTT    --TC생성일시
				      , 'K'                                                      AS YD_GP
				      , 'R'                                                      AS DIST_GOODS_GP
				      , ''                                                       AS YARD_GP
				      , MT.SSTL_NO                                               AS GOODS_NO
				      , SC.YS_UP_WR_LOC || SC.YS_UP_WR_LAYER || MT.YS_STK_SEQ_NO AS STORE_LOC_CD_FROM
				      , SC.YS_DN_WR_LOC || SC.YS_DN_WR_LAYER || MT.YS_STK_SEQ_NO AS STORE_LOC_CD_TO
				      , TO_CHAR(SYSDATE,'YYYYMMDD')                              AS MOVENSTACK_DATE
				      , TO_CHAR(SYSDATE,'HH24MISS')                              AS MOVENSTACK_TIME
				       
				FROM   TB_YS_CRNSCH SC
				      ,TB_YS_CRNWRKMTL MT
				      ,TB_PB_BUNDLECOMM BC

				WHERE  SC.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				AND    SC.YD_CRN_SCH_ID = MT.YD_CRN_SCH_ID
				AND    MT.SSTL_NO = BC.BNDL_NO  
				AND    BC.CURR_PROG_CD NOT IN ('F','G','H')
				ORDER BY SC.YS_DN_WR_LOC, SC.YS_DN_WR_LAYER, MT.YS_STK_SEQ_NO 
				*/
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcM10YDLMJ1034BySchId_PIDEV";
			} else if ("M10YDLMJ1064".equals(msgId)) {
				trtNm = "입동지시 송신";
				/* 차량입동지시 전문조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.TcM10YDLMJ1064_PIDEV 
				SELECT 'M10YDLMJ1064'                          AS MQ_TC_CD             -- IF구분코드
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')    AS MQ_TC_CREATE_DDTT    -- TC생성일시
				      , TRANS_ORD_DATE                         AS TRN_REQ_DATE         -- 운송작업지시일자
				      , TRANS_ORD_SEQNO                        AS TRN_REQ_SEQ          -- 운송작업지시순번      
				      , CAR_NO                                                           -- 차량번호
				      , YD_GP                                                             -- 야드구분
				      , 'R'                                    AS DIST_GOODS_GP        -- 출하제품구분      
				      , 'N'                                    AS SCH_YN               -- 스케쥴여부
				      , TO_CHAR(SYSDATE,'YYYYMMDD')            AS BAYIN_DATE           -- 입동일시
				      , TO_CHAR(SYSDATE,'HH24MISS')            AS BAYIN_TIME           -- 입동시간
				      , YD_CARPNT_CD                                                   -- 야드차량포인트코드
				      , 'Y'                                    AS LOAN_PULLOUT_ABLE_YN -- 차입인출가능여부
				  FROM (SELECT  TS.CARD_NO
				              , TS.CAR_NO
				              , TS.TRANS_ORD_DATE
				              , TS.TRANS_ORD_SEQNO
				              , SC.WLOC_CD
				              , SC.YD_PNT_CD
				              , TS.YD_CAR_USE_GP
				              , NVL(TS.YD_BAYIN_WO_SEQ,9) AS YD_BAYIN_WO_SEQ
				              , TS.YD_CAR_SCH_ID
				              ,(SELECT YD_CARPNT_CD FROM TB_YD_CARPOINT WHERE YS_STK_COL_GP = SC.YS_STK_COL_GP AND ROWNUM = 1) AS YD_CARPNT_CD 
				              , SC.YD_GP
				          FROM TB_YS_STKCOL SC
				              ,TB_YD_CARSCH TS
				         WHERE SC.YS_STK_COL_GP       = :V_YS_STK_COL_GP
				           AND SC.DEL_YN              = 'N'
				           AND SC.YD_STK_COL_ACT_STAT = 'C' --비활성화
				           AND (SC.YD_STKBED_USG_CD IS NULL OR SC.YD_STKBED_USG_CD != 'GT') --출하
				           AND ((TS.YD_CAR_PROG_STAT = '1' AND TS.YD_CARLD_STOP_LOC = SC.YS_STK_COL_GP)
				             OR (TS.YD_CAR_PROG_STAT = 'A' AND TS.YD_CARUD_STOP_LOC = SC.YS_STK_COL_GP))
				           AND TS.DEL_YN             = 'N'
				         ORDER BY YD_BAYIN_WO_SEQ, YD_CAR_SCH_ID)
				 WHERE ROWNUM = 1          --첫번째가
				   AND YD_CAR_USE_GP = 'G' --출하차량
				   */
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcM10YDLMJ1064_PIDEV";
			} else if ("M10YDLMJ1014".equals(msgId)) {
				trtNm = "제품입고작업실적";
				/* 입고실적적 전문조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ001_PIDEV 
				SELECT 'M10YDLMJ1014'                       AS MQ_TC_CD            --JMSTC코드
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT   --JMSTC생성일시
				      , LYR.YD_GP                           AS YD_GP 
				      , 'R'                                 AS DIST_GOODS_GP 
				      , ''                                  AS YARD_GP
				      , STK.SSTL_NO                         AS GOODS_NO
				      , LYR.YS_STK_COL_GP || LYR.YS_STK_BED_NO || LYR.YS_STK_LYR_NO || LYR.YS_STK_SEQ_NO AS STORE_LOC_CD
				      , TO_CHAR(SYSDATE,'YYYYMMDD')         AS RECEIPT_DATE
				      , TO_CHAR(SYSDATE,'HH24MISS')         AS RECEIPT_TIME
				--      ,BDC.PRD_ITM_CD AS PROD_ITEM_CODE
				FROM   TB_YS_STOCK STK
				      ,TB_YS_STKLYR LYR
				      ,TB_PB_BUNDLECOMM BDC

				WHERE  STK.SSTL_NO = :V_SSTL_NO
				AND    STK.SSTL_NO = LYR.SSTL_NO
				AND    STK.SSTL_NO = BDC.BNDL_NO(+)
				AND    LYR.YD_STK_LYR_MTL_STAT IN ('C','U')
				AND    STK.DEL_YN = 'N'
				AND    LYR.DEL_YN = 'N'
				*/
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ001_PIDEV";
			}  else if ("M10YDLMJ1014_I".equals(msgId)) {
				trtNm = "진양특수강입고작업실적";
				/* 입고실적 전문조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.M10YDLMJ1014
				SELECT 'M10YDLMJ1014'                       AS MQ_TC_CD            --JMSTC코드
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT   --JMSTC생성일시
				      , LYR.YD_GP                           AS YD_GP 
				      , 'R'                                 AS DIST_GOODS_GP 
				      , ''                                  AS YARD_GP
				      , BDC.BNDL_NO                         AS GOODS_NO
				      , BDC.YS_STR_LOC                      AS STORE_LOC_CD
				      , TO_CHAR(SYSDATE,'YYYYMMDD')         AS RECEIPT_DATE
				      , TO_CHAR(SYSDATE,'HH24MISS')         AS RECEIPT_TIME
				FROM   TB_PB_BUNDLECOMM BDC
				WHERE 1=1
				  AND BDC.BNDL_NO = :V_MATL_NO
				*/
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.M10YDLMJ1014";
			} else if ("M10YDLMJ1084".equals(msgId)) {
				trtNm = "일품출하상차실적";
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ007_PIDEV 
				SELECT 'M10YDLMJ1084'                      AS MQ_TC_CD          --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT --JMSTC생성일시
				      ,TS.TRANS_ORD_DATE                   AS TRN_REQ_DATE      --운송지시일자
				      ,TS.TRANS_ORD_SEQNO                  AS TRN_REQ_SEQ      --운송지시순번
				      ,TS.CAR_NO                           AS CAR_NO               --차량번호
				      ,SUBSTR(TS.YD_CARLD_STOP_LOC,1,1)    AS YD_GP              --야드구분
				      ,'R'                                 AS DIST_GOODS_GP
				      ,'N'                                 AS SCH_YN
				      ,COUNT(*) OVER ()                    AS GOODS_EA           --제품개수
				      ,TM.SSTL_NO                          AS GOODS_NO           --제품번호
				  FROM (
				            SELECT *
				            FROM   TB_YS_CARSCH
				            WHERE  YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				       ) TS
				      ,(
				            SELECT SSTL_NO 
				            FROM   TB_YS_CRNWRKMTL
				            WHERE  YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				       ) TM
				*/      
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ007_PIDEV";
			} else if ("M10YDLMJ1094".equals(msgId)) {
				trtNm = "특수강출하상차완료";
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ008_PIDEV 
				--출하상차완료 전문조회 
				SELECT 'M10YDLMJ1094'                          AS MQ_TC_CD          --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')     AS MQ_TC_CREATE_DDTT --JMSTC생성일시
				      ,MIN(TS.TRANS_ORD_DATE )                 AS TRN_REQ_DATE     --운송지시일자
				      ,MIN(TS.TRANS_ORD_SEQNO)                 AS TRN_REQ_SEQ    --운송지시순번
				      ,TS.CAR_NO                                                     --차량번호
				      ,MIN(SUBSTR(TS.YD_CARLD_STOP_LOC,1,1))   AS YD_GP  
				      ,'R'                                     AS DIST_GOODS_GP
				      ,'N'                                     AS SCH_YN
				      ,TO_CHAR(TS.YD_CARLD_CMPL_DT,'YYYYMMDD') AS CARLOAD_END_DATE   --상차완료일자
				      ,TO_CHAR(TS.YD_CARLD_CMPL_DT,'HH24MISS') AS CARLOAD_END_TIME   --상차완료시각
				  FROM TB_YS_CARSCH     TS
				 WHERE TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 GROUP BY TS.CARD_NO, TS.CAR_NO, TS.YD_CARLD_CMPL_DT
			*/	 
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ008_PIDEV";
			} else if ("M10YDLMJ1104".equals(msgId)) {
				trtNm = "특수강제품검수완료실적";
				/* 특수강제품검수완료실적(YSDSJ009) - com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ009_PIDEV 
				SELECT 'M10YDLMJ1104'                      AS MQ_TC_CD            --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT   --JMSTC생성일시

				      ,TRANS_ORD_DATE                      AS TRN_REQ_DATE   --운송지시일자
				      ,TRANS_ORD_SEQNO                     AS TRN_REQ_SEQ     --운송지시순번
				      ,CAR_NO
				      ,'K'                                 AS YD_GP
				      ,'R'                                 AS DIST_GOODS_GP
				      ,'N'                                 AS SCH_YN
				      ,CARLD_CHK_DONE_DATE                 AS CARLD_CHK_DONE_DATE  --상차검수완료일자
				      ,CARLD_CHK_DONE_TIME                 AS CARLD_CHK_DONE_TIME  --상차검수완료시각
				      ,BD_NO_CNT                           AS GOODS_CNT            --제품갯수
				      
				      ,BD_NO_1                             AS GOODS_NO_1           --제품번호1
				      ,GOODS_CHK_AB_CD_1                   AS GOODS_CHK_AB_CD_1    --이상코드1
				      ,LABEL_REISSUE_YN_1                  AS LABEL_REISSUE_YN_1   --라벨재발행유무1
				      ,''                                  AS GDS_CARLD_LOC1  
				      ,BD_NO_2                             AS GOODS_NO_2           --제품번호1
				      ,GOODS_CHK_AB_CD_2                   AS GOODS_CHK_AB_CD_2    --이상코드1
				      ,LABEL_REISSUE_YN_2                  AS LABEL_REISSUE_YN_2   --라벨재발행유무1
				      ,''                                  AS GDS_CARLD_LOC2  
				      ,BD_NO_3                             AS GOODS_NO_3           --제품번호1
				      ,GOODS_CHK_AB_CD_3                   AS GOODS_CHK_AB_CD_3    --이상코드1
				      ,LABEL_REISSUE_YN_3                  AS LABEL_REISSUE_YN_3   --라벨재발행유무1
				      ,''                                  AS GDS_CARLD_LOC3  
				      ,BD_NO_4                             AS GOODS_NO_4           --제품번호1
				      ,GOODS_CHK_AB_CD_4                   AS GOODS_CHK_AB_CD_4    --이상코드1
				      ,LABEL_REISSUE_YN_4                  AS LABEL_REISSUE_YN_4   --라벨재발행유무1
				      ,''                                  AS GDS_CARLD_LOC4  
				      ,BD_NO_5                             AS GOODS_NO_5           --제품번호1
				      ,GOODS_CHK_AB_CD_5                   AS GOODS_CHK_AB_CD_5    --이상코드1
				      ,LABEL_REISSUE_YN_5                  AS LABEL_REISSUE_YN_5   --라벨재발행유무1
				      ,''                                  AS GDS_CARLD_LOC5  
				      ,BD_NO_6                             AS GOODS_NO_6           --제품번호1
				      ,GOODS_CHK_AB_CD_6                   AS GOODS_CHK_AB_CD_6    --이상코드1
				      ,LABEL_REISSUE_YN_6                  AS LABEL_REISSUE_YN_6   --라벨재발행유무1
				      ,''                                  AS GDS_CARLD_LOC6  
				      ,BD_NO_7                             AS GOODS_NO_7           --제품번호1
				      ,GOODS_CHK_AB_CD_7                   AS GOODS_CHK_AB_CD_7    --이상코드1
				      ,LABEL_REISSUE_YN_7                  AS LABEL_REISSUE_YN_7   --라벨재발행유무1
				      ,''                                  AS GDS_CARLD_LOC7  
				      ,BD_NO_8                             AS GOODS_NO_8          --제품번호1
				      ,GOODS_CHK_AB_CD_8                   AS GOODS_CHK_AB_CD_8    --이상코드1
				      ,LABEL_REISSUE_YN_8                  AS LABEL_REISSUE_YN_8   --라벨재발행유무1
				      ,''                                  AS GDS_CARLD_LOC8  
				      ,BD_NO_9                             AS GOODS_NO_9           --제품번호1
				      ,GOODS_CHK_AB_CD_9                   AS GOODS_CHK_AB_CD_9    --이상코드1
				      ,LABEL_REISSUE_YN_9                  AS LABEL_REISSUE_YN_9   --라벨재발행유무1
				      ,''                                  AS GDS_CARLD_LOC9  
				      ,BD_NO_10                            AS GOODS_NO_10           --제품번호1
				      ,GOODS_CHK_AB_CD_10                  AS GOODS_CHK_AB_CD_10    --이상코드1
				      ,LABEL_REISSUE_YN_10                 AS LABEL_REISSUE_YN_10   --라벨재발행유무1
				      ,''                                  AS GDS_CARLD_LOC10  
				FROM (

				    SELECT
				           DD.TRANS_ORD_DATE
				          ,DD.TRANS_ORD_SEQNO
				          ,MAX(CAR_NO) AS CAR_NO
				          ,TO_CHAR(SYSDATE,'YYYYMMDD') AS CARLD_CHK_DONE_DATE
				          ,TO_CHAR(SYSDATE,'HH24MISS') AS CARLD_CHK_DONE_TIME
				          ,TO_CHAR(COUNT(*) OVER ()) AS BD_NO_CNT
				          ,MAX(DECODE(NO,1,DD.SSTL_NO,'')) AS BD_NO_1
				          ,MAX(DECODE(NO,1,DD.YD_AB_CD,'')) AS GOODS_CHK_AB_CD_1
				          ,MAX(DECODE(NO,1,DD.LABEL_YN,'')) AS LABEL_REISSUE_YN_1
				          ,MAX(DECODE(NO,2,DD.SSTL_NO,'')) AS BD_NO_2
				          ,MAX(DECODE(NO,2,DD.YD_AB_CD,'')) AS GOODS_CHK_AB_CD_2
				          ,MAX(DECODE(NO,2,DD.LABEL_YN,'')) AS LABEL_REISSUE_YN_2
				          ,MAX(DECODE(NO,3,DD.SSTL_NO,'')) AS BD_NO_3
				          ,MAX(DECODE(NO,3,DD.YD_AB_CD,'')) AS GOODS_CHK_AB_CD_3
				          ,MAX(DECODE(NO,3,DD.LABEL_YN,'')) AS LABEL_REISSUE_YN_3
				          ,MAX(DECODE(NO,4,DD.SSTL_NO,'')) AS BD_NO_4
				          ,MAX(DECODE(NO,4,DD.YD_AB_CD,'')) AS GOODS_CHK_AB_CD_4
				          ,MAX(DECODE(NO,4,DD.LABEL_YN,'')) AS LABEL_REISSUE_YN_4
				          ,MAX(DECODE(NO,5,DD.SSTL_NO,'')) AS BD_NO_5
				          ,MAX(DECODE(NO,5,DD.YD_AB_CD,'')) AS GOODS_CHK_AB_CD_5
				          ,MAX(DECODE(NO,5,DD.LABEL_YN,'')) AS LABEL_REISSUE_YN_5
				          ,MAX(DECODE(NO,6,DD.SSTL_NO,'')) AS BD_NO_6
				          ,MAX(DECODE(NO,6,DD.YD_AB_CD,'')) AS GOODS_CHK_AB_CD_6
				          ,MAX(DECODE(NO,6,DD.LABEL_YN,'')) AS LABEL_REISSUE_YN_6
				          ,MAX(DECODE(NO,7,DD.SSTL_NO,'')) AS BD_NO_7
				          ,MAX(DECODE(NO,7,DD.YD_AB_CD,'')) AS GOODS_CHK_AB_CD_7
				          ,MAX(DECODE(NO,7,DD.LABEL_YN,'')) AS LABEL_REISSUE_YN_7
				          ,MAX(DECODE(NO,8,DD.SSTL_NO,'')) AS BD_NO_8
				          ,MAX(DECODE(NO,8,DD.YD_AB_CD,'')) AS GOODS_CHK_AB_CD_8
				          ,MAX(DECODE(NO,8,DD.LABEL_YN,'')) AS LABEL_REISSUE_YN_8
				          ,MAX(DECODE(NO,9,DD.SSTL_NO,'')) AS BD_NO_9
				          ,MAX(DECODE(NO,9,DD.YD_AB_CD,'')) AS GOODS_CHK_AB_CD_9
				          ,MAX(DECODE(NO,9,DD.LABEL_YN,'')) AS LABEL_REISSUE_YN_9
				          ,MAX(DECODE(NO,10,DD.SSTL_NO,'')) AS BD_NO_10
				          ,MAX(DECODE(NO,10,DD.YD_AB_CD,'')) AS GOODS_CHK_AB_CD_10
				          ,MAX(DECODE(NO,10,DD.LABEL_YN,'')) AS LABEL_REISSUE_YN_10
				          ,MAX(SPST_FRTOMOVE_GP) AS SPST_FRTOMOVE_GP
				    FROM   (
				                SELECT ROWNUM NO
				                      ,EXA.TRANS_ORD_DATE
				                      ,EXA.TRANS_ORD_SEQNO
				                      ,EXA.SSTL_NO
				                      ,EXA.YD_AB_CD
				                      ,EXA.LABEL_YN
				                      ,STK.SPST_FRTOMOVE_GP 
				                      ,stk.car_no
				                FROM   TB_YS_EXAMINATIONCHKLIST EXA
				                      ,TB_YS_STOCK STK
				                WHERE  EXA.TRANS_ORD_DATE = :V_TRANS_ORD_DATE
				                AND    EXA.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				                AND    EXA.SSTL_NO = STK.SSTL_NO(+)

				           ) DD
				    GROUP BY TRANS_ORD_DATE, TRANS_ORD_SEQNO       

				)      
				*/
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ009_PIDEV";
			} else if ("M10YDLMJ1134".equals(msgId)) {
				trtNm = "사외창고 무정보이송(특수강)";
				/* 사외창고 무정보이송(특수강)(M10YDLMJ1134) - com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSDSJ009_PIDEV 
				SELECT 'M10YDLMJ1134' AS MQ_TC_CD  --JMSTC코드
				       ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT   --JMSTC생성일시
				       ,'K'   AS YD_GP  --야드 구분  (G로?B로? K로.이재현 책임 확인 2023.0323)
				       ,YARD_GP_FROM  --출하창고구분FROM(철분말:S210->G342)
				       ,YARD_GP_TO  --출하창고구분TO(진양:G724)
				       ,'22' AS TRN_FRTOMOVE_GP --운송이송구분(22 - 사외이송)
				       ,'1C' AS DELIVER_TERM_CD --  착지차상도(1C)
				       ,'1' AS TRN_MEANS_GP --운송수단구분(1:내수)
				       ,'11' AS 	TRN_METHOD_GP --운송방법구분(11:내수육송)
				       ,'TR' AS CAR_KIND --차량종류(TR:트레일러)
				       ,CAR_NO  --차량번호
				       ,TRNC_CD --운송사코드
				       ,DRIVER_NAME --운전기사명
				       ,DRIVER_HANDPHONE_NO --운전기사핸드폰번호
				       ,REGISTER AS USER_ID --등록자명
				       ,CARLD_SH --상차매수
				       ,STL_NO1,STL_NO2,STL_NO3,STL_NO4,STL_NO5,STL_NO6,STL_NO7,STL_NO8,STL_NO9
				       ,STL_NO10,STL_NO11,STL_NO12,STL_NO13,STL_NO14,STL_NO15,STL_NO16,STL_NO17,STL_NO18,STL_NO19
				       ,STL_NO20,STL_NO21,STL_NO22,STL_NO23,STL_NO24,STL_NO25,STL_NO26,STL_NO27,STL_NO28,STL_NO29       
				       ,STL_NO30,STL_NO31,STL_NO32,STL_NO33,STL_NO34,STL_NO35,STL_NO36,STL_NO37,STL_NO38,STL_NO39       
				       ,STL_NO40,STL_NO41,STL_NO42,STL_NO43,STL_NO44,STL_NO45,STL_NO46,STL_NO47,STL_NO48,STL_NO49       
				       ,STL_NO50,STL_NO51,STL_NO52,STL_NO53,STL_NO54,STL_NO55,STL_NO56,STL_NO57,STL_NO58,STL_NO59       
				       ,STL_NO60,STL_NO61,STL_NO62,STL_NO63,STL_NO64,STL_NO65,STL_NO66,STL_NO67,STL_NO68,STL_NO69       
				       ,STL_NO70,STL_NO71,STL_NO72,STL_NO73,STL_NO74,STL_NO75,STL_NO76,STL_NO77,STL_NO78,STL_NO79       
				       ,STL_NO80,STL_NO81,STL_NO82,STL_NO83,STL_NO84,STL_NO85,STL_NO86,STL_NO87,STL_NO88,STL_NO89      
				       ,STL_NO90,STL_NO91,STL_NO92,STL_NO93,STL_NO94,STL_NO95,STL_NO96,STL_NO97,STL_NO98,STL_NO99       
                FROM
				(
				SELECT ROWNUM AS RN, COUNT(*) OVER() AS CARLD_SH,A.*
				FROM TB_YS_C_CARLDWR A
				WHERE CARLD_ID =:V_CARLD_ID--'20230317113501'
				)
				PIVOT(MAX(MATL_NO)  --최대99매까지 피봇팅
      			FOR RN IN (1 AS STL_NO1,2 AS STL_NO2,3 AS STL_NO3,4 AS STL_NO4,5 AS STL_NO5,6 AS STL_NO6,7 AS STL_NO7,8 AS STL_NO8,9 AS STL_NO9,
		        		   10 AS STL_NO10,11 AS STL_NO11,12 AS STL_NO12,13 AS STL_NO13,14 AS STL_NO14,15 AS STL_NO15,16 AS STL_NO16,17 AS STL_NO17,18 AS STL_NO18,19 AS STL_NO19,
		        		   20 AS STL_NO20,21 AS STL_NO21,22 AS STL_NO22,23 AS STL_NO23,24 AS STL_NO24,25 AS STL_NO25,26 AS STL_NO26,27 AS STL_NO27,28 AS STL_NO28,29 AS STL_NO29,
   		        		   30 AS STL_NO30,31 AS STL_NO31,32 AS STL_NO32,33 AS STL_NO33,34 AS STL_NO34,35 AS STL_NO35,36 AS STL_NO36,37 AS STL_NO37,38 AS STL_NO38,39 AS STL_NO39,
   		        		   40 AS STL_NO40,41 AS STL_NO41,42 AS STL_NO42,43 AS STL_NO43,44 AS STL_NO44,45 AS STL_NO45,46 AS STL_NO46,47 AS STL_NO47,48 AS STL_NO48,49 AS STL_NO49,
   		        		   50 AS STL_NO50,51 AS STL_NO51,52 AS STL_NO52,53 AS STL_NO53,54 AS STL_NO54,55 AS STL_NO55,56 AS STL_NO56,57 AS STL_NO57,58 AS STL_NO58,59 AS STL_NO59,
   		        		   60 AS STL_NO60,61 AS STL_NO61,62 AS STL_NO62,63 AS STL_NO63,64 AS STL_NO64,65 AS STL_NO65,66 AS STL_NO66,67 AS STL_NO67,68 AS STL_NO68,69 AS STL_NO69,
   		        		   70 AS STL_NO70,71 AS STL_NO71,72 AS STL_NO72,73 AS STL_NO73,74 AS STL_NO74,75 AS STL_NO75,76 AS STL_NO76,77 AS STL_NO77,78 AS STL_NO78,79 AS STL_NO79,
   		        		   80 AS STL_NO80,81 AS STL_NO81,82 AS STL_NO82,83 AS STL_NO83,84 AS STL_NO84,85 AS STL_NO85,86 AS STL_NO86,87 AS STL_NO87,88 AS STL_NO88,89 AS STL_NO89,   		        
   		        		   90 AS STL_NO90,91 AS STL_NO91,92 AS STL_NO92,93 AS STL_NO93,94 AS STL_NO94,95 AS STL_NO95,96 AS STL_NO96,97 AS STL_NO97,98 AS STL_NO98,99 AS STL_NO99   		        
                          )
                     )      
				*/
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYDLMJ1134_PIDEV";
			}else if ("M10YDLMJ1125_JY".equals(msgId)) {
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.TcYDLMJ1125_JY_PIDEV  
				SELECT 'M10YDLMJ1125' AS MQ_TC_CD,
				        TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MQ_TC_CREATE_DDTT,
				       --TRN_REQ_DATE  --BY SSTL_NO
				       --TRN_REQ_SEQ   --BY SSTL_NO
				       CAR_NO,
				       'K' AS YD_GP,
				       'U' AS DIST_GOODS_GP,  --출하제품구분 H:열연코일, P:후판, C:슬라브, R:특수강, T:HR-PLATE, U:진양
				       YARD_GP_TO  AS YARD_GP,
				       TO_CHAR(SYSDATE,'YYYYMMDD') AS CARUD_CMPL_DATE,
				       TO_CHAR(SYSDATE,'HH24MISS') AS CARUD_CMPL_TIME       
				FROM TB_YS_C_CARLDWR A
				WHERE CARLD_ID =  --가장 최근 상차실적 이력이 하차 대상아이디라 가정하고 처리
				(
				SELECT MAX(CARLD_ID) 
				FROM TB_YS_C_CARLDWR R
				WHERE 1=1
				AND YARD_GP_FROM ='DK1N'  --철분말
				AND YARD_GP_TO ='DK1K' --진양
				AND YS_STR_LOC ='GHTR0101011'
				AND MATL_NO =:V_SSTL_NO
				) 
				*/ 
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYDLMJ1125_JY_PIDEV";
			}else if ("YSPDJ001".equals(msgId)) {
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getInlnOutStlInfo_03_PIDEV  
				SELECT 'YSPDJ001'                          AS JMS_TC_CD
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT
				     , BNDL_NO                             AS STL_NO
				     , 'S310'                              AS FROM_LOC
				     , DECODE(YS_STR_LOC,'GGM00101011','G081','GGS00101011','G051') AS TO_LOC
				     , 'updInlnOutStl'                     AS PGM_ID
				     , TO_CHAR(SYSDATE,'YYYYMMDD') AS ERP_HDS_DD
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MODIFIER
				  FROM TB_PB_BUNDLECOMM 
				 WHERE 1=1
				   AND YS_STR_LOC IN ('GGM00101011','GGS00101011')
				   AND BNDL_NO IN (SELECT MATL_NO FROM TB_YS_C_CARLDWR WHERE CARLD_ID = :V_CARLD_ID ) 
				*/ 
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getInlnOutStlInfo_03_PIDEV";
			} else if ("YSPDJ001_BILLET".equals(msgId)) {
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSPDJ001_BILLET 
				SELECT 'YSPDJ001'                                AS JMS_TC_CD
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')       AS JMS_TC_CREATE_DDTT
				     , BLT_NO                                   AS STL_NO
				     , :V_FROM_LOC                               AS FROM_LOC
				     , :V_TO_LOC                                 AS TO_LOC
				     , 'regCarUdStlChk'                          AS PGM_ID
				      ,  TO_CHAR(SYSDATE - (6 / 24), 'YYYYMMDD') AS ERP_HDS_DD
				     , :V_MODIFIER                               AS MODIFIER
				  FROM TB_PB_BILLETCOMM
				 WHERE 1=1
				   AND BLT_NO = :V_MATL_NO
				*/ 
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSPDJ001_BILLET";
			}		
			else if ("YSPDJ001_BUNDLE".equals(msgId)) {
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSPDJ001_BILLET  */
				/* SELECT 'YSPDJ001'                                AS JMS_TC_CD
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')       AS JMS_TC_CREATE_DDTT
				     , BLT_NO                                    AS STL_NO
				     , :V_YARD_GP_FROM                           AS FROM_LOC
				     , :V_YARD_GP_TO                             AS TO_LOC
				     , 'regCarUdStlChk'                          AS PGM_ID
				      ,  TO_CHAR(SYSDATE - (6 / 24), 'YYYYMMDD') AS ERP_HDS_DD
				     , :V_MODIFIER                               AS MODIFIER
				  FROM TB_PB_BUNDLECOMM 
				 WHERE 1=1
				   AND BNDL_NO = :V_MATL_NO*/
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSPDJ001_BUNDLE";
			}		
			 else if ("YSPDJ001_JY".equals(msgId)) {
				 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getInlnOutStlInfo_JY*/
				 /*SELECT 'YSPDJ001'                          AS JMS_TC_CD
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT
				      , BLT_NO                             AS STL_NO
				      , 'S210'                              AS FROM_LOC
				      , 'G724'                              AS TO_LOC
				      , 'updInlnOutStlM'                    AS PGM_ID
				 --     , TO_CHAR(SYSDATE,'YYYYMMDD') AS ERP_HDS_DD
				       ,  TO_CHAR(SYSDATE - (6 / 24), 'YYYYMMDD') AS ERP_HDS_DD
				      , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS MODIFIER
				   FROM TB_PB_BILLETCOMM 
				  WHERE 1=1
				    AND YS_STR_LOC IN ('GJM11111111')
				    AND BLT_NO IN (SELECT MATL_NO FROM TB_YS_C_CARLDWR WHERE CARLD_ID = :V_CARLD_ID AND MATL_NO = :V_SSTL_NO)*/
					jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getInlnOutStlInfo_JY";

			 }
			JDTORecordSet jsRst = null;
			
			if(!"".equals(jspeed_query_id)) {
				trtNm = trtNm + "(" + msgId + ") : ";
				
				jsRst = this.select2(jrParam, jspeed_query_id);
					
				commUtils.printLog(logId, trtNm + jsRst.size(), "DB");
				
				String sApplyYnPI = ApplyYnPI("", methodNm, "APPPI1", "*", "*");
				
				if("Y".equals(sApplyYnPI)){
				//---[JMS IF 로그 조회 시 순서바뀜 현상 수정 추가 시작]-------------------------------------------------------
				JDTORecordSet addData = JDTORecordFactory.getInstance().createRecordSet("");
				String sITM_ID;
				String sITM_VALUE;
				
				if(jsRst.size()>0) 
				{
					// PIDEV
					if("M10".equals(msgId.substring(0, 3))) {
						jrParam.setField("PI_YD", "1");
					}
					JDTORecord jrAdd = JDTORecordFactory.getInstance().create();
					jrParam.setField("IF_ID",msgId);
					String getIfTestLayout = "com.inisteel.cim.ys.dcommon.dao.YsCommDAO.getIfTestLayout_PIDEV";
					JDTORecordSet jsLayOut = this.select(jrParam, getIfTestLayout);
					
						if(jsLayOut.size()>0)	//Layout에 데이터가 없는 경우 기존 로직으로 탈 수 있도록 분기처리
						{
							for(int ii = 0; ii < jsRst.size(); ii++) 
							{	
								for(int jj = 0; jj < jsLayOut.size(); jj++ )
								{	
									sITM_ID = jsLayOut.getRecord(jj).getFieldString("ITM_ID");
									sITM_VALUE = jsRst.getRecord(ii).getFieldString(sITM_ID);
									
									jrAdd.setField(sITM_ID , sITM_VALUE);
								}
								addData.addRecord(jrAdd);
							}
							jsRst = JDTORecordFactory.getInstance().createRecordSet("");
							jsRst.addAll(addData);
						}
				}
				//---[JMS IF 로그 조회 시 순서바뀜 현상 수정 추가 종료]-------------------------------------------------------
				}
				
			}
			
			return jsRst;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, trtNm + methodNm, e));
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
	public JDTORecordSet getYsCode(GridData gdReq) throws DAOException {
		String methodNm = "코드조회[YsCommDAO.getYsCode] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;
			
			String itmGp = commUtils.trim(gdReq.getParam("V_ITM_GP")); //코드항목구분

			commUtils.printLog(logId, "조회[YsCommDAO.jspSelect] 결과 건수: " + itmGp , "DB");
			
			if ("YD_BAY_GP".equals(itmGp)) {
				trtNm = "동구분";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getCodeYdBayGp";  
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_YD_GP")) //야드구분
						, commUtils.trim(gdReq.getParam("V_LOC_GP")) //작업장구분
								};

			} else if ("YD_EQP_GP".equals(itmGp)) {
				trtNm = "설비구분";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getCodeYdEqpGp";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_YS_STK_COL_GP")) //야드구분 + 동구분
					};
			} else if ("YD_EQP_GP_NOT_TY".equals(itmGp)) {
				trtNm = "설비구분";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getCodeYdEqpGpNotTY";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_YS_STK_COL_GP")) //야드구분 + 동구분
					};				
			} else if ("YD_STK_COL_NO".equals(itmGp)) {
				trtNm = "적치열번호";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getCodeYdStkColNo";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_YS_STK_COL_GP")) //야드구분 + 동구분 + Span구분
					};
			} else if ("YS_STK_BED_NO".equals(itmGp)) {
				trtNm = "적치Bed번호";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getCodeYdStkBedNo";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_YS_STK_COL_GP")) //야드적치열구분
					};
			} else if ("YS_STK_LYR_NO".equals(itmGp)) {
				trtNm = "적치단번호";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getCodeYdStkLyrNo";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_YS_STK_COL_GP")) //야드적치열구분
					   ,commUtils.trim(gdReq.getParam("V_YS_STK_BED_NO")) //BED구분
					};
			} else if ("YD_EQP_ID_CR".equals(itmGp)) {
				trtNm = "크레인설비ID";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getCodeYdEqp";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_YD_GP"    )) //야드구분
					   ,commUtils.trim(gdReq.getParam("V_YD_BAY_GP")) //동구분
					};
			} else if ("YD_EQP_ID_SC".equals(itmGp)) {
				trtNm = "크레인설비ID";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getCodeYdEqpSC";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_YD_GP"    )) //야드구분
					   ,commUtils.trim(gdReq.getParam("V_YD_BAY_GP")) //동구분
					};
			} else if ("YD_EQP_ID_TC".equals(itmGp)) {
				trtNm = "대차설비ID";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getCodeYdEqpTc";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_YD_GP")) //야드구분
					};
			} else if ("YD_SCH_CD".equals(itmGp)) {
				trtNm = "스케줄코드";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getCodeYdSchCd";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_YD_GP"    )) //야드구분
					   ,commUtils.trim(gdReq.getParam("V_YD_BAY_GP")) //동구분
					};
			} else if ("YD_STK_ABLE_BED".equals(itmGp)) {
				trtNm = "적치가능Bed";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getCodeStkAbleBed";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_ITM_ID"    )) //항목ID
					   ,commUtils.trim(gdReq.getParam("V_YS_STK_COL_GP")) //야드적치열구분
					};
			} else if ("ORD_CONV_T".equals(itmGp)) {
				trtNm = "주문사이즈";
				jspeed_query_id = "com.inisteel.cim.ys.bl.dao.BlYsDAO.getMillWoInqOrdSize";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_YD_GP"    )) //야드구분
					   ,commUtils.trim(gdReq.getParam("V_YD_BAY_GP")) //동구분
					};
			} 
			
			/*			
			else if ("YD_STK_ABLE_BED".equals(itmGp)) {
				trtNm = "적치가능Bed";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCodeStkAbleBed";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_ITM_ID"       )) //항목ID
						,commUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드적치열구분
					};
			} else { //공통코드조회
				trtNm = "[" + itmGp + "]코드";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCodeCmCodes";
				param = new Object[] {
						itmGp //코드영문ID
					   ,commUtils.trim(gdReq.getParam("V_CD_CAT_ID")) //코드카테고리ID
					};
			}
*/			
			trtNm += " : ";
			commUtils.printLog(logId, "조회[YsCommDAO.jspSelect] 결과 건수11: " + itmGp , "DB");

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 제품창고 야드L2 저장품 제원정보 전문 생성
	 *      
	 *      @param String msgId
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMsgYSL2L002(JDTORecord jrParam) throws DAOException {
		String methodNm = "제품창고 야드L2 저장품 제원정보 전문 생성[YsCommDAO.getMsgYSL2L002] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";
		String msgId = "";

		try {
			String jspeed_query_id = "";
			
	    	trtNm = "저장품제원정보";
			
			//야드정보동기화코드 
			String ydInfoSyncCd = commUtils.trim(jrParam.getFieldString("YD_INFO_SYNC_CD"));
			String ydL2Gp 		= commUtils.trim(jrParam.getFieldString("YD_L2_GP"));   //L2 위치 수신
			
			if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
				//위치별 >> 1:동,2:SPAN,3:열,4:BED
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSL2L002ByLoc";
				
				if (ydL2Gp.equals("")) {
					//저장위치의 동구분을 가지고 TC코드를 설정한다.   
					String ysStkColGp = commUtils.trim(jrParam.getFieldString("YS_STK_COL_GP"));
					String ysBayGp = ysStkColGp.substring(1,2);
					
					if("A".equals(ysBayGp)) {
						jrParam.setField("TC_CD", "YSN6L002"); //봉강자동창고
					} else if("B".equals(ysBayGp)) {
						jrParam.setField("TC_CD", "YSN4L002"); //봉강수동창고
					} else if("D".equals(ysBayGp)) {
						jrParam.setField("TC_CD", "YSN5L002"); //선재자동창고
					} else if("E".equals(ysBayGp)) {
						jrParam.setField("TC_CD", "YSN3L002"); //선재수동창고
					} else {
						//throw new Exception("제품창고 저장품제원정보 TC코드 설정시 에러발생!!! ysBayGp : "+ysBayGp);
						jspeed_query_id = "";
					}
				} else {
					
					if("N6".equals(ydL2Gp)) {
						jrParam.setField("TC_CD", "YSN6L002"); //봉강자동창고
					} else if("N4".equals(ydL2Gp)) {
						jrParam.setField("TC_CD", "YSN4L002"); //봉강수동창고
					} else if("N5".equals(ydL2Gp)) {
						jrParam.setField("TC_CD", "YSN5L002"); //선재자동창고
					} else if("N3".equals(ydL2Gp)) {
						jrParam.setField("TC_CD", "YSN3L002"); //선재수동창고
					} else {
						//throw new Exception("제품창고 저장품제원정보 TC코드 설정시 에러발생!!! ysBayGp : "+ysBayGp);
						jspeed_query_id = "";
					}
					
					
				}
				
					
			} else {
				//재료별 >> 5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제),H:C열연장입,P:1후판장입,Q:2후판장입
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSL2L002";

				if (ydL2Gp.equals("")) {
				
					//야드저장품테이블의 저장위치나 입고예정위치의 동구분을 가지고 TC코드를 설정한다. (쿼리에서 결정)
					JDTORecordSet jsRstStk = null;
					jsRstStk = this.select2(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsStkColGp");
					
					jsRstStk.first();
					
					JDTORecord jrRstStk		= jsRstStk.getRecord();
					
					String ysStkColGp		= commUtils.trim(jrRstStk.getFieldString("YS_STK_COL_GP"));
					
					String ysBayGp = ysStkColGp.substring(1,2);
					
					if("A".equals(ysBayGp)) {
						jrParam.setField("TC_CD", "YSN6L002"); //봉강자동창고
					} else if("B".equals(ysBayGp)) {
						jrParam.setField("TC_CD", "YSN4L002"); //봉강수동창고
					} else if("D".equals(ysBayGp)) {
						jrParam.setField("TC_CD", "YSN5L002"); //선재자동창고
					} else if("E".equals(ysBayGp)) {
						jrParam.setField("TC_CD", "YSN3L002"); //선재수동창고
					} else {
						//throw new Exception("제품창고 저장품제원정보 TC코드 설정시 에러발생!!! ysBayGp : "+ysBayGp);
						jspeed_query_id = "";
					}
				} else{
					if("N6".equals(ydL2Gp)) {
						jrParam.setField("TC_CD", "YSN6L002"); //봉강자동창고
					} else if("N4".equals(ydL2Gp)) {
						jrParam.setField("TC_CD", "YSN4L002"); //봉강수동창고
					} else if("N5".equals(ydL2Gp)) {
						jrParam.setField("TC_CD", "YSN5L002"); //선재자동창고
					} else if("N3".equals(ydL2Gp)) {
						jrParam.setField("TC_CD", "YSN3L002"); //선재수동창고
					} else {
						//throw new Exception("제품창고 저장품제원정보 TC코드 설정시 에러발생!!! ysBayGp : "+ysBayGp);
						jspeed_query_id = "";
					}
				}	
			}
			
			JDTORecordSet jsRst = null;
			
			if(!"".equals(jspeed_query_id)) {
				trtNm = trtNm + "(" + msgId + ") : ";
				
				jsRst = this.select2(jrParam, jspeed_query_id);
					
				commUtils.printLog(logId, trtNm + jsRst.size(), "DB");
			}

			return jsRst;
			
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 빌렛정정 재공야드 현황자료
	 * 
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getYsInlnLocInfo(GridData gdReq) throws DAOException {
		String jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsInlnLocInfo";
		Object[] objs = null;

		try {
			objs = new Object[]{
					 CmnUtil.nvl(gdReq.getParam("V_YS_STR_LOC1"), ""),	//작업일자
					 CmnUtil.nvl(gdReq.getParam("V_YS_STR_LOC2"), "")	//작업일자
				};

			return getRecordSet(jspeed_query_id, objs);
		} catch(Exception e) {
			CmnUtil.printSqlLog(jspeed_query_id, objs);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 이상재메일내용생성
	 * 
	 *      @param Object objs
	 *      @return int
	 *      @throws DAOException
	*/
	public int insSstlNoAbOccurMailContents_01(Object[] objs) throws DAOException {
		//String jspeed_query_id = "com.inisteel.cim.hr.hrcomm.dao.HrCommDAO.insMillAbOccurMailContents";		
		String jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.insSstlNoAbOccurMailContents";
		
		try {
			// 첫번째 수행할 쿼리
			return assistantDAO.trtProcess(jspeed_query_id, objs);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			 
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 메일조회(본문,대상자)
	 * 
	 *      @param Object objs
	 *      @return int
	 *      @throws DAOException
	*/
	public JDTORecordSet getHrMailContents(Object[] objs) throws DAOException {
		String jspeed_query_id = "com.inisteel.cim.hr.hrcomm.dao.HrCommDAO.getHrMailContents";			
		try{
    		return assistantDAO.getRecordSet(jspeed_query_id, objs);
    	} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 선재 라벨정보 조회
	 *      
	 *      @param String msgID : 수신된 전문의 MSG_ID
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getSriLblInfo(String sBndlNo) throws DAOException {
		try {
			return getRecordSet("com.inisteel.cim.ys.common.dao.YsCommDAO.getSriLblInfo", new Object[] { sBndlNo });
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	
	/**
	 * 오퍼레이션명 : WebMethod 사용 여부 
	 * @return String : 사용여부 Y:WebMethod 사용 ,N:사용안함
	 */
	public String getWebMothodYn() throws DAOException {
		
		String sFlagYn = "N"; 
		
		try {
			
			JDTORecordSet jsRst = getRecordSet("com.inisteel.cim.ys.common.dao.YsCommDAO.getWebMethodYn", null);
			
			if (jsRst.size() > 0) {
				sFlagYn = commUtils.trim(jsRst.getRecord(0).getFieldString("WEB_METHOD_YN")); //WebMethod 사용 여부
			}
			
		} catch (Exception e) {
			
			return sFlagYn;
		}
			
		return sFlagYn;
	}

	/**
	 *      [A] 오퍼레이션명 :  Tb_YS_RULE_PI 조회
	 *      -- AS_IS SQL Name에 해당하는 TO_BE SQL Name 값을 반환한다.
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String getYsRulePI(String logId, String mthdNms,String sReprCdGpPi, String sDtlItemAsPi, String sReprCdGp, String sCdGp) throws DAOException {
		String mthdNm = "getYdRulePI 조회[YsCommDAO.getYsRulePI] < " + mthdNms;
		
		String toBeSqlNm = "";

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			/**********************************************************
			* 1. TB_YD_RULE_PI 조회
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam("", mthdNm, "");
			jrParam.setField("REPR_CD_GP_PI"		, sReprCdGpPi  ); //작업구분(TS_YS_RULE_PI)
			jrParam.setField("DTL_ITEM_AS_PI"      , sDtlItemAsPi ); //구분(TB_YS_RULE_PI)
			jrParam.setField("REPR_CD_GP"     	, sReprCdGp ); //작업구분(TB_YS_RULE)
			jrParam.setField("CD_GP"     		, sCdGp     ); //코드구분(TB_YS_RULE)

			/*
				SELECT 
				       DTL_ITEM_TO
				  FROM 
				       TB_YS_RULE_PI
				 WHERE REPR_CD_GP  = :V_REPR_CD_GP_PI
				   AND DTL_ITEM_AS = :V_DTL_ITEM_AS_PI
				   AND DEL_YN      = 'N'
				   AND 'Y' = (
				                SELECT 
				                        NVL(MAX(ITEM),'N') AS APPLY_YN
				                FROM 
				                        TB_YS_RULE
				                WHERE 
				                      REPR_CD_GP = :V_REPR_CD_GP  -- APP001
				                  AND CD_GP  = :V_CD_GP           -- CD_GP
				                  AND DEL_YN = 'N'   
				              )
			*/ 
			//필드명 변환 (필드명 -> V_필드명)
			jrParam = conversionFieldname(jrParam, 0);			
			//query id setting
			jrParam.setField("JSPEED_QUERY_ID", "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsRulePi_PIDEV");
			//query execute
			JDTORecordSet jsChk = getRecordSet(jrParam);			
			
			if (jsChk.size() > 0) {
				toBeSqlNm    = commUtils.trim(jsChk.getRecord(0).getFieldString("DTL_ITEM_TO"));
			} else {
				toBeSqlNm    = sDtlItemAsPi;
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
		String mthdNm = "PI시스템 적용여부[YsCommDAO.ApplyYnPI] < " + mthdNms;
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
//			jrParam.setField("ITEM"      , sItem      ); //ITEM

			/* 
                SELECT 
                        NVL(MAX(ITEM),'N') AS APPLY_YN
                FROM 
                        TB_YS_RULE
                WHERE 
                      REPR_CD_GP = :V_REPR_CD_GP  -- APP001
                  AND CD_GP  = :V_CD_GP           -- CD_GP
                  AND DEL_YN = 'N'   
			*/  
			//필드명 변환 (필드명 -> V_필드명)
			jrParam = conversionFieldname(jrParam, 0);			
			//query id setting
			jrParam.setField("JSPEED_QUERY_ID", "com.inisteel.cim.ys.common.dao.YsCommDAO.getApplyYn_PIDEV");
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
	 * [A] 오퍼레이션명 :  특수강이송관리 조회
	 *
	 * @param GridData gdReq
	 * @return JDTORecordSet
	 * @throws DAOException 
	 * 
	 */
	//PIDEV 
	public JDTORecordSet getYsSpstFtmv(GridData gdReq) throws DAOException {
		String jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsSpstFtmv_PIDEV";
		Object[] objs = null;

		try {
			objs = new Object[] {
				 CmnUtil.nvl(gdReq.getParam("V_DATE_FR"   	), "") 	//시작일자
				,CmnUtil.nvl(gdReq.getParam("V_DATE_TO"  	), "")	//종료일자
				,CmnUtil.nvl(gdReq.getParam("V_CD"	), "") 	//구분
				,CmnUtil.nvl(gdReq.getParam("V_STL_NO" 	), "")	//재료번호
				,CmnUtil.nvl(gdReq.getParam("viewRows"      ), "")   //viewRows
				,CmnUtil.nvl(gdReq.getParam("viewPage"     	), "") 	//viewPage
				,CmnUtil.nvl(gdReq.getParam("viewRows"      ), "")   //viewRows
				,CmnUtil.nvl(gdReq.getParam("viewPage"     	), "") 	//viewPage
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
	 *      @param String trtGp
	 *      @param Object[] objs
	 *      @return int
	 *      @throws DAOException
	*/
	//PIDEV 
	public int insYsSpstFtmv(Object[] objs) throws DAOException {
		String jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.insYsSpstFtmv_PIDEV";

		try {
			CmnUtil.printSqlLog(jspeed_query_id, objs);
			
        	return trtProcess(jspeed_query_id, objs);
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}

	
	
	

	
	
	/***************************************************************************
	 * 인터페이스Test
	 **************************************************************************/
	
	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스Test 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getIfTest(GridData gdReq) throws DAOException {
		String methodNm = "인터페이스Test[YsCommDAO.getIf] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("SI".equals(gdReq.getParam("V_TRT_GP"))) {
				trtNm = "I/F List 조회";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getIfTestList";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("V_SYS_GP"      )) //시스템구분
						,commUtils.trim(gdReq.getParam("V_SYS_GP"      )) //시스템구분
						,commUtils.trim(gdReq.getParam("V_OPRN_SYS_GP" )) //운영시스템구분
						,commUtils.trim(gdReq.getParam("V_IF_MTH_GP"   )) //IF방법구분
						,commUtils.trim(gdReq.getParam("V_IF_SNDRCV_GP")) //IF송수신구분
						,commUtils.trim(gdReq.getParam("V_IF_ID"       )) //IFID
						,commUtils.trim(gdReq.getParam("V_IF_ID"       )) //IFID
					};
			} else {
				trtNm = "I/F Layout 조회";
				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getIfTestLayout";
				param = new Object[] {
						commUtils.trim(gdReq.getParam("IF_ID")) //IFID
					};
			}
			
			trtNm += " : ";

// 2025.07.25 체크 부분			
			JDTORecordSet jrRtn = getRecordSet(jspeed_query_id, param);

			return jrRtn;
			
//			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

}