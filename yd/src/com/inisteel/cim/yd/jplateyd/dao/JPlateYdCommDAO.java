/**
 * @(#)JPlateYdCommDAO
 *
 * @version          V1.00
 * @author           김현우
 * @date             2012/12/27
 *
 * @description      2후판정정야드 공통 DAO [주로 JSP에서 사용되는 Query실행]
 * ------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/12/27   김현우      김현우      최초 등록
 */
package com.inisteel.cim.yd.jplateyd.dao;

import java.sql.Types;
import java.util.Iterator;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;

public class JPlateYdCommDAO extends DBAssistantDAO {

	private YdSlabUtils commUtils = new YdSlabUtils();
	
	// Dao Name
	private static final String SZ_DAO_NAME = JPlateYdCommDAO.class.getName();

	private JPlateYdUtils 		ydUtils 	= new JPlateYdUtils();
	private DBAssistantDAO 		dbAssDao 	= new DBAssistantDAO();
	private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();
	private YdPICommDAO	   		ydPICommDAO   = new YdPICommDAO();
	
	/**
	 *      [A] 오퍼레이션명 : 2후판정정야드 SELECT 메소드
	 *
	 * @param  JDTORecord    inRec      parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         String        queryId    QueryId
	 * @return int           result size
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int select(JDTORecord inRec, JDTORecordSet outRecSet, String queryId) throws DAOException, JDTOException {

		JDTORecord recPara 		= null;
		JDTORecordSet rsTemp 	= null;
		String 	szMethodName 	= "select";
		int 	intRtnVal 		= -100;

		try {

//PIDEV:정정 작업
//			queryId = ydPICommDAO.getYdRulePI("", szMethodName, "YD0001", queryId, "APPPI0", "*", "*" );

			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			if (rsTemp.size() > 0) {
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();

				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			} else {
				//data not found
				String szMsg = "data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return 0;
			}

		} catch (Exception e) {

			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return intRtnVal;
	}
	/**
	 *      [A] 오퍼레이션명 : 1후판 압연전단 BOOK IN/OUT실적 전문 편집
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0: book in, 1:book out)
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getJPlateL2TelegramInfo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		
		JDTORecordSet rsTemp 	= null;
		JDTORecord recPara 		= null;
		
		int intRtnVal = 0;
		
		/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.rcvL2TelegramyY2 
		SELECT 
		      '00195'
		   || '24753'                                  
		   ||'000'
		   ||'00'
		   ||'00'      
		   ||TO_CHAR(SYSDATE, 'YYYY')  
		   ||TO_CHAR(SYSDATE, 'MM')   
		   ||TO_CHAR(SYSDATE, 'DD')  
		   ||TO_CHAR(SYSDATE, 'HH24')  
		   ||TO_CHAR(SYSDATE, 'MI')  
		   ||TO_CHAR(SYSDATE, 'SS')       
		   ||'00' 	    
		   ||RPAD(NVL(:OPERATION_TYPE , ' ') , 1, ' ')   
		   ||RPAD(NVL(PL_L2_TRK_NO , ' ') , 16, ' ')     --후판L2제품번호
		   ||RPAD(NVL(PL_MPL_NO             , ' ') , 32, ' ')  	 --후판재료번호
		   ||'00000'  --후판제촌제품길이
		   ||'000000' --푸한제촌제품폭
		   ||'0000000'  --후판제촌제품두께
		   ||RPAD(:PL_TRCK_ZONE_ASGN,5,'0')  --후판트래킹존지정
		   ||'1'         --후판북아웃모드
		   ||'00'    --CRANE_NO
		   ||'      '   --YARD_NO
		   ||'00'      --BED_NO
		   ||'000'     --RESON_CODE
		   ||'0'      --NEXT_PROCESS
		   ||RPAD('0',80,'0')
		   AS TY3ABC
		FROM
		( 
		    SELECT PL_L2_TRK_NO,SUBSTR(PL_MPL_NO,0,8) AS PL_MPL_NO FROM TB_PR_ROLL_MAT  WHERE PL_MPL_NO   = :PL_PLATE_NO
		    UNION
		    SELECT PL_L2_TRK_NO,PL_MPL_NO FROM TB_PR_PLATE_MAT WHERE PL_PLATE_NO = :PL_PLATE_NO
		)
	     */  
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.rcvL2TelegramyY2");
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0) {
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}else {
				//data not found
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getL2TelegramInfo	
	
	
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
			
//PIDEV
//			queryId = ydPICommDAO.getYdRulePI("", mthdNm, "YD0001", queryId, "APPPI0", "*", "*");

			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			rsTemp = getRecordSet(recPara);
			
			commUtils.printLog(logId, "조회[JPlateYdCommDAO.jspSelect] 결과 건수: " + rsTemp.size() , "DB");
			
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
		
		String methodNm = trtNm + "[JPlateYdCommDAO.select] < " + mthdNm;
		
		JDTORecord recPara = null;	
		
		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			JDTORecordSet rsTemp = getRecordSet(recPara);
			
			commUtils.printLog(logId, trtNm + "조회[JPlateYdCommDAO.select] 결과 건수: " + rsTemp.size() , "DB");
			
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
		
		String methodNm = trtNm + "[JPlateYdCommDAO.update] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			commUtils.printLog(logId, trtNm + "[JPlateYdCommDAO.update] 결과 건수: " + intRtnVal , "DB");
			
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
		
		String methodNm = trtNm + "[JPlateYdCommDAO.insert] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			commUtils.printLog(logId, trtNm + "[JPlateYdCommDAO.insert] 결과 건수: " + intRtnVal , "DB");
			
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
		
		String methodNm = trtNm + "[JPlateYdCommDAO.delete] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			commUtils.printLog(logId, trtNm + "[JPlateYdCommDAO.delete] 결과 건수: " + intRtnVal , "DB");
			
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
	 *      [A] 오퍼레이션명 : 1후판 정정야드 신규모듈 적용여부 리턴 메소드
	 *      
	 * @param  String 
	 * @return String
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public String getNewModuleEffYn(String item) throws DAOException, JDTOException {
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();	
		JDTORecordSet rsTemp = null;
		String retunValue = "N";
	
		try {
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getNewModuleEffYn");
			//parameter setting
			recPara.setField("V_ITEM", item);
			
			/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getNewModuleEffYn
			SELECT ITEM1 AS NEW_MODULE_EFF_YN
			FROM   TB_YD_RULE
			WHERE  REPR_CD_GP = 'P90000'
			AND    CD_GP      = '*'
			AND    ITEM       = :V_ITEM
			 */
			
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			if(rsTemp.size() <= 0) {
				retunValue = "N";
			} else {
				retunValue = StringHelper.evl(rsTemp.getRecord(0).getFieldString("NEW_MODULE_EFF_YN"),"N");
			}
		} catch (Exception e) {
			retunValue = "N";
		}
		return retunValue;
	}
	
	/**
	 *      [A] 오퍼레이션명 : 1후판 압연전단 BOOK IN/OUT실적 전문 편집 - 신규
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0: book in, 1:book out)
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getJPlateL2TelegramInfoV2(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		
		JDTORecordSet rsTemp 	= null;
		JDTORecord recPara 		= null;
		
		int intRtnVal = 0;
		
		/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.rcvL2TelegramyY2V2 
		SELECT 
		      '00167'
		   || '24753'                                  
		   ||'000'
		   ||'00'
		   ||'00'      
		   ||TO_CHAR(SYSDATE, 'YYYY')  
		   ||TO_CHAR(SYSDATE, 'MM')   
		   ||TO_CHAR(SYSDATE, 'DD')  
		   ||TO_CHAR(SYSDATE, 'HH24')  
		   ||TO_CHAR(SYSDATE, 'MI')  
		   ||TO_CHAR(SYSDATE, 'SS')       
		   ||'00' 	    
		   ||RPAD(NVL(:V_OPERATION_TYPE , ' ') , 1, ' ')   
		   ||RPAD(NVL(PL_L2_TRK_NO , ' ') , 16, ' ')     --후판L2제품번호
		   ||RPAD(NVL(PL_MPL_NO             , ' ') , 32, ' ')  	 --후판재료번호
		   ||'00000'  --후판제촌제품길이
		   ||'000000' --푸한제촌제품폭
		   ||'0000000'  --후판제촌제품두께
		   ||RPAD(NVL(:V_PL_TRCK_ZONE_ASGN,'0'),5,'0')  --후판트래킹존지정
		   ||'1'         --후판북아웃모드
		   ||RPAD(NVL(:V_CRANE_NO , '00') , 2, '0') --CRANE_NO
		   ||RPAD(:V_YD_STK_COL_GP,6,' ')   --YARD_NO
		   ||'01'      --BED_NO
		   ||'000'     --RESON_CODE
		   ||'0'      --NEXT_PROCESS
		   ||RPAD(NVL(:V_PILNG_WRK_GP, ' ') , 1, ' ') --파일링작업구분
		   ||RPAD(NVL(:V_PL_MTL_NO2, ' ') , 10, ' ') --2단재료번호
		   ||RPAD(NVL(:V_PL_MTL_NO3, ' ') , 10, ' ') --3단재료번호
		   ||RPAD('0',59,'0')
		   AS TY3ABC
		FROM
		( 
		    SELECT NVL(PL_L2_TRK_NO,PL_MPL_NO) AS PL_L2_TRK_NO
		         , PL_MPL_NO    AS PL_MPL_NO FROM TB_PR_ROLL_MAT  WHERE PL_MPL_NO   = :V_PL_PLATE_NO
		    UNION
		    SELECT NVL(PL_L2_TRK_NO,PL_PLATE_NO) AS PL_L2_TRK_NO
		         , PL_PLATE_NO  AS PL_MPL_NO FROM TB_PR_PLATE_MAT WHERE PL_PLATE_NO = :V_PL_PLATE_NO
		)
		*/
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.rcvL2TelegramyY2V2");
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0) {
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}else {
				//data not found
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getJPlateL2TelegramInfoV2	
	
	/**
	 *      [A] 오퍼레이션명 : 1후판 압연L2 BOOK IN/OUT실적 전문 편집 - 신규
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0: book in, 1:book out)
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getJPlateL2TelegramInfoV3(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		
		JDTORecordSet rsTemp 	= null;
		JDTORecord recPara 		= null;
		
		int intRtnVal = 0;
		
		/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.rcvL2TelegramyY2V3 
		SELECT 
		      '00167'
		   || '29783'                                  
		   ||'000'
		   ||'00'
		   ||'00'      
		   ||TO_CHAR(SYSDATE, 'YYYY')  
		   ||TO_CHAR(SYSDATE, 'MM')   
		   ||TO_CHAR(SYSDATE, 'DD')  
		   ||TO_CHAR(SYSDATE, 'HH24')  
		   ||TO_CHAR(SYSDATE, 'MI')  
		   ||TO_CHAR(SYSDATE, 'SS')       
		   ||'00' 	    
		   ||RPAD(NVL(:V_OPERATION_TYPE , ' ') , 1, ' ')   
		   ||RPAD(NVL(PL_L2_TRK_NO , ' ') , 16, ' ')     --후판L2제품번호
		   ||RPAD(NVL(PL_MPL_NO             , ' ') , 32, ' ')  	 --후판재료번호
		   ||'00000'  --후판제촌제품길이
		   ||'000000' --푸한제촌제품폭
		   ||'0000000'  --후판제촌제품두께
		   ||RPAD(NVL(:V_PL_TRCK_ZONE_ASGN,'0'),5,'0')  --후판트래킹존지정
		   ||DECODE(:V_CARD_NO,'L3','4','1')         --후판북아웃모드(PL_BOOK_OUT_MOD)
		   ||RPAD(NVL(:V_CRANE_NO,'00'),2,'0')    --CRANE_NO
		   ||RPAD(:V_YD_STK_COL_GP,6,' ')   --YARD_NO
		   ||'01'      --BED_NO
		   ||'000'     --RESON_CODE
		   ||'0'      --NEXT_PROCESS
		   ||RPAD(NVL(:V_PILNG_WRK_GP, ' ') , 1, ' ') --파일링작업구분
		   ||RPAD(NVL(:V_PL_MTL_NO2, ' ') , 10, ' ') --2단재료번호
		   ||RPAD(NVL(:V_PL_MTL_NO3, ' ') , 10, ' ') --3단재료번호
		   ||RPAD('0',3,'0')  --SPARE_ARRAY1
		   ||RPAD('0',56,'0') --SPARE
		   AS TY3MBC
		FROM
		( 
		    SELECT NVL(PL_L2_TRK_NO,PL_MPL_NO) AS PL_L2_TRK_NO
		         , PL_MPL_NO    AS PL_MPL_NO FROM TB_PR_ROLL_MAT  WHERE PL_MPL_NO   = :V_PL_PLATE_NO
		    UNION
		    SELECT NVL(PL_L2_TRK_NO,PL_PLATE_NO) AS PL_L2_TRK_NO
		         , PL_PLATE_NO  AS PL_MPL_NO FROM TB_PR_PLATE_MAT WHERE PL_PLATE_NO = :V_PL_PLATE_NO
		)
		*/
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.rcvL2TelegramyY2V3");
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0) {
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}else {
				//data not found
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getJPlateL2TelegramInfoV3		
	
	/**
	 *      [A] 오퍼레이션명 : 1후판 압연전단 BOOK IN/OUT실적 전문 편집 - 56020존 파일링 전용
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0: book in, 1:book out)
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	 
	public int getJPlateL2TelegramInfoV4(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		
		JDTORecordSet rsTemp 	= null;
		JDTORecord recPara 		= null;
		
		int intRtnVal = 0;
		
		/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.rcvL2TelegramyY2V2 
		SELECT 
		      '00167'
		   || '24753'                                  
		   ||'000'
		   ||'00'
		   ||'00'      
		   ||TO_CHAR(SYSDATE, 'YYYY')  
		   ||TO_CHAR(SYSDATE, 'MM')   
		   ||TO_CHAR(SYSDATE, 'DD')  
		   ||TO_CHAR(SYSDATE, 'HH24')  
		   ||TO_CHAR(SYSDATE, 'MI')  
		   ||TO_CHAR(SYSDATE, 'SS')       
		   ||'00' 	    
		   ||RPAD(NVL(:V_OPERATION_TYPE , ' ') , 1, ' ')   
		   ||RPAD(NVL(PL_L2_TRK_NO , ' ') , 16, ' ')     --후판L2제품번호
		   ||RPAD(NVL(PL_MPL_NO             , ' ') , 32, ' ')  	 --후판재료번호
		   ||'00000'  --후판제촌제품길이
		   ||'000000' --푸한제촌제품폭
		   ||'0000000'  --후판제촌제품두께
		   ||RPAD(NVL(:V_PL_TRCK_ZONE_ASGN,'0'),5,'0')  --후판트래킹존지정
		   ||'1'         --후판북아웃모드
		   ||RPAD(NVL(:V_CRANE_NO , '00') , 2, '0') --CRANE_NO
		   ||RPAD(:V_YD_STK_COL_GP,6,' ')   --YARD_NO
		   ||'01'      --BED_NO
		   ||'000'     --RESON_CODE
		   ||'0'      --NEXT_PROCESS
		   ||RPAD(NVL(:V_PILNG_WRK_GP, ' ') , 1, ' ') --파일링작업구분
		   ||RPAD(NVL(:V_PL_MTL_NO2, ' ') , 10, ' ') --2단재료번호
		   ||RPAD(NVL(:V_PL_MTL_NO3, ' ') , 10, ' ') --3단재료번호
		   ||RPAD('0',59,'0')
		   AS TY3ABC
		FROM
		( 
		    SELECT NVL(PL_L2_TRK_NO,PL_MPL_NO) AS PL_L2_TRK_NO
		         , PL_MPL_NO    AS PL_MPL_NO FROM TB_PR_ROLL_MAT  WHERE PL_MPL_NO   = :V_PL_PLATE_NO
		    UNION
		    SELECT NVL(PL_L2_TRK_NO,PL_PLATE_NO) AS PL_L2_TRK_NO
		         , PL_PLATE_NO  AS PL_MPL_NO FROM TB_PR_PLATE_MAT WHERE PL_PLATE_NO = :V_PL_PLATE_NO
		)
		*/
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.rcvL2TelegramyY2V4");
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0) {
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}else {
				//data not found
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getJPlateL2TelegramInfoV4		
}
