/**
 * @(#)YdPlateCommDAO
 *
 * @version          V1.00
 * @author           조병기
 * @date             2012/11/22
 * 
 * @description      제품출하야드 공통 DAO
 * ------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/22   조병기      조병기      최초 등록
 */
package com.inisteel.cim.yd.common.dao;

import java.sql.Types;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import jspeed.base.record.JDTORecordFactory;

public class YdPlateCommDAO {
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	private CCommUtils commUtils = new CCommUtils();
	private static YdUtils        ydUtils        	= new YdUtils();
	
	/**
	 *      [A] 오퍼레이션명 : 후판제품야드 SELECT 메소드
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         String        queryId    QueryId 
	 * @return int           result size
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int select(JDTORecord inRec, JDTORecordSet outRecSet, String queryId) throws DAOException, JDTOException {
		
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
		
		try {
			
			//PIDEV
			//PIDEV_S :병행가동용:PI_YD 
//			String sPI_YD     = commUtils.nvl(inRec.getFieldString("PI_YD"), "*");	
//			queryId = ydPICommDAO.getYdRulePI("", "", "YD0001", queryId, "APPPI0", sPI_YD, "*" );
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
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
	 *      [A] 오퍼레이션명 : 후판제품야드 UPDATE 메소드
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
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
		} catch (Exception e) {

			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return intRtnVal;
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
		
		String methodNm = trtNm + "[YdPlateCommDAO.insert] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			String szMsg = "[YdPlateCommDAO.insert] 결과 건수: " + intRtnVal;
			
			ydUtils.putLogNew("YdPlateCommDAO", methodNm, szMsg, YdConstant.DEBUG, logId);
		
			
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return intRtnVal;	
	}

	/**
	 *      [A] 오퍼레이션명 : 후판제품야드 프로시져 호출 메소드
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
	 		
//			return dbAssDao.trtProcedure(queryId, add_query, inParam, inParamIndex, outParamKey, outParamType, outParamIndex);
	 		//PIDEV
	 		//PIDEV_S :병행가동용:PI_YD
//			String toQuery_ID = ydPICommDAO.getYdRulePI("", "", "YD0001", queryId, "APPPI0", "T", "*" );
//			return dbAssDao.trtProcedure(toQuery_ID, add_query, inParam, inParamIndex, outParamKey, outParamType, outParamIndex);

			
			return dbAssDao.trtProcedure(queryId, add_query, inParam, inParamIndex, outParamKey, outParamType, outParamIndex);
	 		

		} catch (Exception e) {

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 	
	}
	
	/**
	 *      [A] 오퍼레이션명 : B열연 신규모듈 적용여부 리턴 메소드
	 *      
	 * @param  void 
	 * @return JDTORecord
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public JDTORecord getNewModuleEffYn() throws DAOException, JDTOException {
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();	
		JDTORecordSet rsTemp = null;
		
		try {
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getNewModuleEffYn");
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getNewModuleEffYn 
			--B열연 신규모듈적용여부 
			
			SELECT 'YM00' AS REPR_CD_GP
			      ,'N'    AS BSLAB_EFF_YN --B열연 SLAB야드 신규모듈 적용여부(Y:적용, N:적용안함)
			      ,'Y'    AS BCOIL_EFF_YN --B열연 COIL야드 신규모듈 적용여부(Y:적용, N:적용안함)
			  FROM DUAL      
			  
			--SELECT REPR_CD_GP
			--      ,MAX(DECODE(CD_GP,'2',ITEM)) AS BSLAB_EFF_YN --B열연 SLAB야드 신규모듈 적용여부(Y:적용, N:적용안함)
			--      ,MAX(DECODE(CD_GP,'3',ITEM)) AS BCOIL_EFF_YN --B열연 COIL야드 신규모듈 적용여부(Y:적용, N:적용안함)
			--  FROM USRYMA.TB_YM_RULE
			-- WHERE REPR_CD_GP = 'YM00'
			-- GROUP BY REPR_CD_GP
			*/
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			if(rsTemp.size() <= 0) {
				recPara.setField("BSLAB_EFF_YN", "N");
				recPara.setField("BCOIL_EFF_YN", "N");
				recPara.setField("BCOIL_EFF_YN1", "N");

			} else {
				recPara.setField("BSLAB_EFF_YN", StringHelper.evl(rsTemp.getRecord(0).getFieldString("BSLAB_EFF_YN"),"N"));
				recPara.setField("BCOIL_EFF_YN", StringHelper.evl(rsTemp.getRecord(0).getFieldString("BCOIL_EFF_YN"),"N"));
				recPara.setField("BCOIL_EFF_YN1", StringHelper.evl(rsTemp.getRecord(0).getFieldString("BCOIL_EFF_YN1"),"N"));
			}
			
		} catch (Exception e) {
			
			recPara.setField("BSLAB_EFF_YN", "N");
			recPara.setField("BCOIL_EFF_YN", "N");
			recPara.setField("BCOIL_EFF_YN1", "N");
		}
		return recPara;
	}
	
	
	/**
     * [A] 오퍼레이션명 : A열연 신규모듈 적용여부 리턴 메소드
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
                recPara.setField("ASLAB_EFF_YN",	"N");
                recPara.setField("ACOIL_EFF_YN",	"N");
                recPara.setField("MODULE_YN",		"N");
            }
            else
            {
                recPara.setField("ASLAB_EFF_YN",	StringHelper.evl(rsTemp.getRecord(0).getFieldString("ASLAB_EFF_YN"),	"N"));
                recPara.setField("ACOIL_EFF_YN",	StringHelper.evl(rsTemp.getRecord(0).getFieldString("ACOIL_EFF_YN"),	"N"));
                recPara.setField("MODULE_YN",		StringHelper.evl(rsTemp.getRecord(0).getFieldString("MODULE_YN"),		"N"));
            }
        }
        catch (Exception e)
        {
            recPara.setField("ASLAB_EFF_YN",	"N");
            recPara.setField("ACOIL_EFF_YN",	"N");
            recPara.setField("MODULE_YN",		"N");
        }

        return recPara;
    }
    
	/**
	 *      [A] 오퍼레이션명 : 2열연 신규모듈 적용여부 리턴 메소드
	 *      
	 * @param  void 
	 * @return JDTORecord
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public JDTORecord get2HrAppYn() throws DAOException, JDTOException {
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();	
		JDTORecordSet rsTemp = null;
		
		try {
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getNewModuleEffYn");
			/* 
			SELECT 'N' AS CCOIL_EFF_YN --2열연 코일야드 신규 모듈 적용여부
              FROM DUAL
			*/

			rsTemp = dbAssDao.getRecordSet(recPara);
			
			if(rsTemp.size() <= 0) {
				recPara.setField("CCOIL_EFF_YN", "N");

			} else {
				recPara.setField("CCOIL_EFF_YN", StringHelper.evl(rsTemp.getRecord(0).getFieldString("CCOIL_EFF_YN"),"N"));
			}
			
		} catch (Exception e) {
			
			recPara.setField("CCOIL_EFF_YN", "N");
		}
		return recPara;
	}    
   
	
	/**
	 *  오퍼레이션명 : 후판슬라브 신규모듈 적용여부 리턴 메소드
	 *  2020-12-18 
	 *  염용선    
	 * @param  void 
	 * @return JDTORecord
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public JDTORecord getPSlabAppYn() throws DAOException, JDTOException {
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();	
		JDTORecordSet rsTemp = null;
		String gubunYn = "N";
		try {
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.pslabcommon.getNewModuleEffYn");
			/* 
			SELECT ITEM1 AS PSLAB_EFF_YN
			  FROM TB_YD_RULE
			 WHERE REPR_CD_GP = 'APP009' -- 후판슬라브 신규모듈 적용 여부(Y:적용/N:미적용)
			 
			 SELECT 'N' AS PSLAB_EFF_YN -- 후판슬라브 신규모듈 적용 여부(Y:적용/N:미적용)
              FROM DUAL
			*/

			rsTemp = dbAssDao.getRecordSet(recPara);
			
			if(rsTemp.size() <= 0) {
				recPara.setField("PSLAB_EFF_YN", gubunYn);

			} else {
				gubunYn = StringHelper.evl(rsTemp.getRecord(0).getFieldString("PSLAB_EFF_YN"),"N");
				recPara.setField("PSLAB_EFF_YN", gubunYn );
			}
			
		} catch (Exception e) {
			
			recPara.setField("PSLAB_EFF_YN", gubunYn);
		}
		return recPara;
	}  
}
