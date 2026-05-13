package com.inisteel.cim.yd.common.dao.ydLocSrchBedDao;

import java.util.Iterator;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 *      [A] 클래스명 : 야드위치검색BED DAO
 * 
*/

public class YdLocSrchBedDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydlocsrchbeddao.YdLocsrchbedDao.getYdLocsrchbed";
	
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydlocsrchbeddao.YdLocsrchbedDao.getYdLocsrchbedBySchCdRouteGpRngRegSno";
	
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydlocsrchbeddao.YdLocsrchbedDao.getYdLocsrchbedWithMax";
//	2010.04.05 이종헌 추가(화면:위치검색SPAN관리) 
	private String szQueryIdGet300 = "com.inisteel.cim.yd.dao.ydlocsrchbeddao.YdLocsrchbedDao.getYdLocsrchbedCoil";
//	2010.04.05 이종헌 추가(화면:위치검색SPAN관리) 
	private String szQueryIdGet301 = "com.inisteel.cim.yd.dao.ydlocsrchbeddao.YdLocsrchbedDao.getYdLocsrchbedBySchCdRouteGpRngRegSnoCoil";
	
	
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydlocsrchbeddao.YdLocsrchbedDao.insYdLocsrchbed";
	
//	2010.04.05 이종헌 추가(화면:위치검색SPAN관리) 
	private String szQueryIdIns300 = "com.inisteel.cim.yd.dao.ydlocsrchbeddao.YdLocsrchbedDao.insYdLocSrchBedCoil";
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydlocsrchbeddao.YdLocsrchbedDao.updYdLocsrchbed";
	
//	2010.04.05 이종헌 추가(화면:위치검색SPAN관리) 
	private String szQueryIdDel300 = "com.inisteel.cim.yd.dao.ydlocsrchbeddao.YdLocsrchbedDao.delYdLocSrchBedCoil";
//	2010.04.05 이종헌 추가(화면:위치검색SPAN관리) 
	private String szQueryIdDel301 = "com.inisteel.cim.yd.dao.ydlocsrchbeddao.YdLocsrchbedDao.delYdLocSrchBedCoil_CHECK";
	
	private String szQueryIdDel302 = "com.inisteel.cim.yd.dao.ydlocsrchbeddao.YdLocsrchbedDao.delYdLocSrchBedCoil_CHECK2";

	
/*------------------------------------- SELECT -------------------------------------------*/
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드위치검색BED SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:YD_SCH_CD,YD_ROUTE_GP, YD_LOC_SRCH_RNG_REG_SNO,YD_LOC_SRCH_BED_REG_SNO
	 *                                      1:YD_LOC_SRCH_RNG_REG_SNO
	 *                                      2:YD_LOC_SRCH_RNG_REG_SNO
	 *                                     )
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdLocsrchbed(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdLocsrchbed";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdLocsrchbed(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet1);
			else if(intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet2);
			else if(intGp == 2)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet3);
			if(intGp == 300)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet300);
			else if(intGp == 301)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet301);
			
	
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			
			//result recordSet check
			if (rsTemp.size() > 0)
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
			else {
				//data not found
	//			szMsg = "data not found!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 3);
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal = rsTemp.size();
	} //end of getYdLocsrchbed
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드위치검색BED SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec  parameter record
	 *         int        intGp  구분(0:YD_SCH_CD,YD_ROUTE_GP,YD_LOC_SRCH_RNG_REG_SNO,YD_LOC_SRCH_BED_REG_SNO
	 *         						 1:YD_LOC_SRCH_RNG_REG_SNO
	 *         						 2:YD_LOC_SRCH_RNG_REG_SNO
	 *         )
	 * @return boolean           true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdLocsrchbed(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;

		try {
			if (intGp == 0) {
				szFieldName = "V_YD_LOC_SRCH_RNG_REG_SNO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'L', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_LOC_SRCH_BED_REG_SNO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, 'L', 0, 0);
			}else if(intGp ==1  ){
				szFieldName = "V_YD_LOC_SRCH_RNG_REG_SNO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'L', 0, 0);
				
			} else if(intGp==2 ){
							
				szFieldName = "V_YD_LOC_SRCH_RNG_REG_SNO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'L', 0, 0);
			}else if(intGp == 301){
				szFieldName = "V_YD_LOC_SRCH_RNG_REG_SNO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'L', 0, 0);
				
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdLocsrchbed
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드위치검색BED INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdLocsrchbed(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkParameter(recPara);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
	
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdIns1);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdLocsrchbed
	
	/** 2010.04.05
	 *      [A] 오퍼레이션명 : 야드위치검색BED INSERT (화면:위치검색SPAN관리)
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdLocSrchBedCoil(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
	
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdIns300);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdLocsrchbed
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드위치검색BED INSERT parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_YD_LOC_SRCH_RNG_REG_SNO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_LOC_SRCH_BED_REG_SNO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_REGISTER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_REG_DDTT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
//			szFieldName = "V_MODIFIER";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
//			if (!blnErr) return blnErr;
//	
//			szFieldName = "V_MOD_DDTT";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
//			if (!blnErr) return blnErr;
//			
//			szFieldName = "V_DEL_YN";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
//			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_SRCH_SEQ";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_COL_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		
		return blnErr;
	} //end of chkParameter
	
	/**
	 *      [A] 오퍼레이션명 : 야드위치검색BED INSERT parameter Check (화면:위치검색SPAN관리)
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameterCoil(JDTORecord inRec, String gbn) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_YD_LOC_SRCH_RNG_REG_SNO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
//			INSERT 시에는 PRAMETER가 없으므로 체크하지 않는다.(QUERY에서 처리)
			if(!gbn.equals("insYdLocSrchBedCoil")) {
				szFieldName = "V_YD_LOC_SRCH_BED_REG_SNO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, 'L', 0, 0);
				if (!blnErr) return blnErr;
			}
			szFieldName = "V_REGISTER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_REG_DDTT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_USER_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
//	
//			szFieldName = "V_MOD_DDTT";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
//			if (!blnErr) return blnErr;
//			
//			szFieldName = "V_DEL_YN";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
//			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_SRCH_SEQ";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_COL_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		
		return blnErr;
	} //end of chkParameterCoil
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드위치검색BED UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_SCH_CD,YD_ROUTE_GP,
	 *                                YD_LOC_SRCH_RNG_REG_SNO,YD_LOC_SRCH_BED_REG_SNO)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdLocsrchbed(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdLocsrchbed";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		try {
			//recordSet create
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
	
			//변환용 레코드
			JDTORecord recInPara = null;
			JDTORecord recOutPara = null;
	
			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//update data select
			intRtnVal = this.getYdLocsrchbed(inRec, outRecSet, 0);
			
			//parameter error return
			if (intRtnVal < 0) {
	//			szMsg = "parameter error!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			}
			
			//data not found return
			if (intRtnVal == 0) {
	//			szMsg = "data not found!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			}
			
			//duplicate data return
			if (outRecSet.size() != 1) {
	//			szMsg = "duplicate data!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}
			
			outRecSet.first();
			outRec = outRecSet.getRecord();
			
			//필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(outRec, 0);
			
			//data mapping
			this.dataMapping(recInPara, recOutPara);
			
			//parameter check
			blnChk_Field = this.chkParameter(recOutPara);
			
			//parameter error return
			if (!blnChk_Field) return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd1);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdLocsrchbed
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드위치검색BED UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_YD_LOC_SRCH_RNG_REG_SNO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_LOC_SRCH_BED_REG_SNO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_REGISTER";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_REG_DDTT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_MODIFIER";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_MOD_DDTT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_DEL_YN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_SRCH_SEQ";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_COL_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_NO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}

	} // end of dataMapping

/*------------------------------------- DELETE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드위치검색BED UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_SCH_CD,YD_ROUTE_GP,
	 *                                YD_LOC_SRCH_RNG_REG_SNO,YD_LOC_SRCH_BED_REG_SNO)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int delYdLocSrchBedCoil(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdLocsrchbed";
		String szMsg = null;
	
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		try {			
			JDTORecord recPara = null;
						
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			/*	JDTORecord
			 *	넘어온 recOutPara 헤더를 키를 본다
			 */
//			Iterator iter = recPara.iterateName();
//			while(iter.hasNext()){
//				String str = (String) iter.next();
//				ydUtils.putLog("DAO", szMethodName, "NAME::"+str +" -- value::" +recPara.getFieldString(str), YdConstant.INFO);
//			}
			
			//query id setting
			if(intGp == 300) recPara.setField("JSPEED_QUERY_ID", szQueryIdDel300);
			if(intGp == 301) recPara.setField("JSPEED_QUERY_ID", szQueryIdDel301);
			if(intGp == 302) recPara.setField("JSPEED_QUERY_ID", szQueryIdDel302);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of delYdLocSrchBedCoil
} // end of class






