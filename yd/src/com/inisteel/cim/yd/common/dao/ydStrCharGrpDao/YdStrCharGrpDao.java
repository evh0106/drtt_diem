package com.inisteel.cim.yd.common.dao.ydStrCharGrpDao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
    
/**
 *      [A] 클래스명 : 야드 저장속성 그룹 DAO
 * 
*/

public class YdStrCharGrpDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	private CCommUtils	   commUtils   = new CCommUtils();	
	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();

	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydstrchargrpdao.YdStrchargrpDao.getYdStrchargrp";
	
	//2009.06.15 (이현성 ) 
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydstrchargrpdao.YdStrchargrpDao.getYdStrchargrpUsgGp";
	//2009.06.17 (김종건 ) 입고예정위치등록
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydstrchargrpdao.YdStrchargrpDao.getPlateYdRcptPlnLocReg";
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydstrchargrpdao.YdStrchargrpDao.getPlateYdRcptPlnLocRegRTop";
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydstrchargrpdao.YdStrchargrpDao.getPlateYdRcptPlnLocRegRBot";
	//2009.07.09 (김종건 ) 후판제품야드 선별 대상제품 처리 저장속성 그룹코드 팝업
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ydstrchargrpdao.YdStrchargrpDao.getPlateYdStrCharGrpPop_PIDEV";
	//2009.08.03 (김종건 ) 후판제품야드 저장속성그룹 기존 및 신규그룹 조회
	private String szQueryIdGet7 = "com.inisteel.cim.yd.dao.ydstrchargrpdao.YdStrchargrpDao.getPlateYdStrCharGrp";
	//2014.08.03 (윤재광 ) 후판제품야드 OS신규주문속성 팝업정보 조회
	private String szQueryIdGet8 = "com.inisteel.cim.yd.dao.ydstrchargrpdao.YdStrchargrpDao.getPlateYdStrCharGrp2";
	
	private String szQueryIdGet9 = "com.inisteel.cim.yd.dao.ydstrchargrpdao.YdStrchargrpDao.getYdAbleStrchargrp";
	
	// 후판제품야드 저장속성그룹 기존 및 신규그룹 엑셀조회
	private String szQueryIdGet300 = "com.inisteel.cim.yd.dao.ydstrchargrpdao.YdStrchargrpDao.getPlateYdStrCharGrpXL_PIDEV";
																							
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydstrchargrpdao.YdStrchargrpDao.insYdStrchargrp";
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydstrchargrpdao.YdStrchargrpDao.updYdStrchargrp";
	
/*------------------------------------- SELECT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드 저장속성 그룹 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:YD_STRCHAR_GRP_CD
	 *                                      1: V_YD_STRCHAR_GRP_CD, V_UNION_GP
	 *                                      2: SRC_MODE, ORD_CONS_DATE_FROM, ORD_CONS_DATE_TO, ORD_NO, ORD_DTL, DEST_CD, CUST_CD, DEMANDER_CD, STRCHAR_GP
	 *                                      3: PILING_CD, ORD_CONS_DATE_FROM, ORD_CONS_DATE_TO
	 *                                      4: YD_STK_COL_SPAN, YD_PILING_CD, ORD_CONV_T
	 *                                      5: YD_STR_CHAR_GP, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                      6: YD_STRCHAR_GRP_CD, YD_STR_CHAR_GRP_ACT_STAT, GRP_SEQ_NO, ROW_CNT1, V_PAGE_CNT1, ROW_CNT2, V_PAGE_CNT2
	 *           )
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdStrchargrp(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdStrchargrp";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdStrchargrp(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet1);
			else if (intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet2);
			else if (intGp == 2)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet3);
			else if (intGp == 3)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet4);
			else if (intGp == 4)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet5);
			else if (intGp == 5)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet6);
			else if (intGp == 6)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet7);
			else if (intGp == 7)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet8);
			else if (intGp == 8)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet9);
			else if (intGp == 300)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet300);
	
//PIDEV
//			//PIDEV_S :병행가동용:PI_YD
//			String sPI_YD     = commUtils.nvl(recPara.getFieldString("V_PI_YD"), "*");				
//			String toQuery_ID = ydPICommDAO.getYdRulePI("", szMethodName, "YD0001", commUtils.trim(recPara.getFieldString("JSPEED_QUERY_ID")), "APPPI0", sPI_YD, "*" );
//			recPara.setField("JSPEED_QUERY_ID", commUtils.trim(toQuery_ID));

			
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0)
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
			else {
	//			szMsg = "data not found!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 3);
				return intRtnVal = 0;
			}
		} catch (Exception e) {  
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal = rsTemp.size();
	} //end of getYdStrchargrp
	
	/**
	 *      [A] 오퍼레이션명 : 야드 저장속성 그룹 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_STRCHAR_GRP_CD
	 *                              1: V_YD_STRCHAR_GRP_CD, V_UNION_GP
	 *                              2: V_SRC_MODE, V_ORD_CONS_DATE_FROM, V_ORD_CONS_DATE_TO, V_ORD_NO, V_ORD_DTL, V_DEST_CD, V_CUST_CD, V_DEMANDER_CD, V_STRCHAR_GP
	 *                              3: V_PILING_CD, ORD_CONS_DATE_FROM, ORD_CONS_DATE_TO
	 *                              4: YD_STK_COL_SPAN, YD_PILING_CD, ORD_CONV_T
	 *                              5: YD_STR_CHAR_GP, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                              6: YD_STRCHAR_GRP_CD, YD_STR_CHAR_GRP_ACT_STAT, GRP_SEQ_NO, ROW_CNT1, V_PAGE_CNT1, ROW_CNT2, V_PAGE_CNT2
	 *         )
	 * @return boolean          true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdStrchargrp(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		try {
			if (intGp == 0) {
				
				szFieldName = "V_YD_STRCHAR_GRP_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'S', 0, 0);
				
			}else  if (intGp == 1) {				
				szFieldName = "V_YD_STRCHAR_GRP_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_UNION_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			}else  if (intGp == 2) {				
				szFieldName = "V_SRC_MODE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_CONS_DATE_FROM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_CONS_DATE_TO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_DTL";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
//0116				
				szFieldName = "V_DEST_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CUST_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DEMANDER_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_STRCHAR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			}else  if (intGp == 3) {				
				szFieldName = "V_PILING_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_CONS_DATE_FROM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_CONS_DATE_TO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			}else  if (intGp == 4) {				
				szFieldName = "V_YD_STK_COL_SPAN";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PILING_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_CONV_T";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			}else if (intGp == 5) {
				szFieldName = "V_YD_STR_CHAR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			}else if (intGp == 6) {
				szFieldName = "V_YD_STR_CHAR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
//				szFieldName = "V_DEST_CD";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
//				if (!blnErr) return blnErr;
				
//				szFieldName = "V_DEMANDER_CD";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
//				if (!blnErr) return blnErr;
				szFieldName = "V_YD_STR_CHAR_GRP_ACT_STAT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_GRP_SEQ_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);	
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdStrchargrp
	
	
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드 저장속성 그룹 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdStrchargrp(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = false;
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
	} // end of insYdStrchargrp
	
	/**
	 *      [A] 오퍼레이션명 : 야드 저장속성 그룹 parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_YD_STRCHAR_GRP_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'S', 0, 0);
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
			
			szFieldName = "V_YD_GP";	
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_DEST_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CUST_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_DEMANDER_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STR_CHAR_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			

			szFieldName = "V_YD_STR_CHAR_GRP_ACT_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			

			szFieldName = "V_YD_STR_CHAR_GRP_NM";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 50, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}

		return blnErr;
	} //end of chkPara_YdStrchargrp
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드 저장속성 그룹 UPDATE
	 * 
	 * @param JDTORecord inRec parameter record
	 *         int       intGp 구분(0:YD_STRCHAR_GRP_CD)
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, 
	 *                         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStrchargrp(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdStrchargrp";
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
			intRtnVal = this.getYdStrchargrp(inRec, outRecSet, 0);
			
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
			
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStrchargrp
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드 저장속성 그룹 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_YD_STRCHAR_GRP_CD";
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
			
			szFieldName = "V_YD_GP";	
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
					
			szFieldName = "V_DEST_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_CUST_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_DEMANDER_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STR_CHAR_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STR_CHAR_GRP_ACT_STAT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STR_CHAR_GRP_NM";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		
	} // end of YdStrchargrp_DataMapping
	
	
	
/*------------------------------------- DELETE -------------------------------------------*/
} // end of class

