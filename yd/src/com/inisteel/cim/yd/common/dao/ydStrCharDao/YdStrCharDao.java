package com.inisteel.cim.yd.common.dao.ydStrCharDao;

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
 *      [A] 클래스명 : 야드 저장속성 DAO
 * 
*/

public class YdStrCharDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	private CCommUtils	   commUtils   = new CCommUtils();	
	//저장속성ID생성 쿼리
	private String szQueryIdKey = "com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.getYdStrcharId";

	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.getYdStrchar";
	
	//20090615 이현성 
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.getYdStrcharGRP_CD_PIDEV";
	
	//20090615 이현성 
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.getBedPlanPos";
	
	//20090731 김종건 - 저장속성그룹
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.getYdStkGrpMgt_PIDEV";
	
	//20090921 김종건 - 저장속성 Matching
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.getYdStrCharForStrMatching";
	//임춘수 2009.09.24 - 저장속성그룹이 NULL인 저장속성 조회
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.getYdStrcharGRP_CDNull_PIDEV";
	//임춘수 2009.09.24 - 고객코드, 목적지코드, 수요가코드의 저장속성 조회
	private String szQueryIdGet7 = "com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.getYdStrcharByDestCustDemander";

	//
	private String szQueryIdGet300 = "com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.getYdStrcharRow1";
	private String szQueryIdGet301 = "com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.getYdStrcharByDetatilArrCd";
	private String szQueryIdGet302 = "com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.getYdStrcharByDetatilArrCdStrCharGrpCd";
	private String szQueryIdGet303 = "com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.getYdStrcharBySameOrdNo";
	private String szQueryIdGet304 = "com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.getYdStrcharByDetatilArrCdE";
	
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.insYdStrchar";
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.updYdStrchar";
	
	private String szQueryIdUpd2 = "com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.delYdStrchar_02";
	private String szQueryIdUpd3 = "com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.updYdStrchar_03";
	
	//실제적으로 레코드를 삭제하는 쿼리
	private String szQueryIdDel1 = "com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.delYdStrchar";
	/**
	 * 오퍼레이션명 : 야드저장속성ID를 생성하여 반환하는 메소드
	 * @return String : 야드저장속성ID
	 */
	public String getYdStrcharId() throws DAOException {
		//메소드명
		String szMethodName = "getYdStrCharId";
		//레코드
		JDTORecord recKey = JDTORecordFactory.getInstance().create();
		//차량스케쥴ID
		String szYdStrCharId = null;
		try {
			//JSPEED 쿼리ID
			recKey.setField("JSPEED_QUERY_ID", szQueryIdKey);
			//쿼리 실행
			JDTORecordSet rsTemp = dbAssDao.getRecordSet(recKey);
			if( rsTemp.size() <= 0 ) {
				throw new JDTOException("야드저장속성ID 레코드가 존재하지 않음");
			}
			rsTemp.first();
			recKey = rsTemp.getRecord();
		
			szYdStrCharId = ydDaoUtils.paraRecChkNull(recKey, "YD_STRCHAR_ID");
		}catch(JDTOException e) {
			String szMsg = "야드저장속성ID 생성 시 에러 발생";
			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
			throw new DAOException(szMsg, e);
		}
		return szYdStrCharId;
	}
	
	/**
	 * 물리적으로 저장속성레코드를 삭제하는 함수
	 * @param inRec
	 * @return
	 * @throws DAOException
	 */
	public int delYdStrchar(JDTORecord inRec) throws DAOException {
		int intRtnVal = 0;
		boolean blnChk_Field = false;
		JDTORecord recPara = null;
		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdStrchar(recPara, 0);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdDel1);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	}
	
	/**
	 * 물리적으로 저장속성레코드를 삭제하는 함수
	 * @param inRec
	 * @return
	 * @throws DAOException
	 */
	public int updateYdStrchar(JDTORecord inRec, int intGp) throws DAOException {
		int intRtnVal = 0;
		
		JDTORecord recPara = null;
		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			if(intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd1);
			else if(intGp == 2) 
				recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd2);
			else if(intGp == 3) 
				recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd3);

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	}
	
/*------------------------------------- SELECT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드 저장속성 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:YD_STRCHAR_ID
	 *                                      1:YD_STRCHAR_GRP_CD
	 *                                      3:SEARCH_GBN, YD_STRCHAR_ID, DEST_CD, DEMANDER_CD, ORD_NO, ORD_DTL
	 *                                      6:CUST_CD, DEST_CD, DEMANDER_CD
	 *                                      )
	 *                                      
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdStrchar(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdStrchar";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdStrchar(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet1);
			if (intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet2);
			if (intGp == 2)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet3);
			if (intGp == 3)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet4);
			if (intGp == 4)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet5);
			if (intGp == 5)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet6);
			if (intGp == 6)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet7);
			if (intGp == 300)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet300);
			if (intGp == 301)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet301);
			if (intGp == 302)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet302);
			if (intGp == 303)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet303);
			if (intGp == 304)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet304);
			
//PIDEV
			//PIDEV_S :병행가동용:PI_YD
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
	} //end of getYdStrchar
	
	/**
	 *      [A] 오퍼레이션명 : 야드 저장속성 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(
	 *                              0: V_YD_STRCHAR_ID
	 *                              1:YD_STRCHAR_GRP_CD
	 *                              3:SEARCH_GBN, YD_STRCHAR_ID, DEST_CD, DEMANDER_CD, ORD_NO, ORD_DTL
	 *                              )
	 * @return boolean          true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdStrchar(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		try {
			if (intGp == 0) {
				
				szFieldName = "V_YD_STRCHAR_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			
				
			} else if (intGp == 1) {
				szFieldName = "V_YD_STRCHAR_GRP_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'S', 0, 0);
				
				
			} else if (intGp == 2) {
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_ROUTE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				
			} else if (intGp == 3) {
				szFieldName = "V_SEARCH_GBN";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STRCHAR_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
//0116				
//				szFieldName = "V_DEST_CD";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
//				if (!blnErr) return blnErr;
				
				szFieldName = "V_DEMANDER_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_DTL";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
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
				
			} else if (intGp == 6) {
				szFieldName = "V_CUST_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
//0116				
//				szFieldName = "V_DEST_CD";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, YdDaoUtils.STRING_TYPE, 0, 0);
//				if (!blnErr) return blnErr;
				
				szFieldName = "V_DEMANDER_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdStrchar
	
	
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드 저장속성 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdStrchar(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = false;
		JDTORecord recPara = null;
		
		try {
			if( ydDaoUtils.paraRecChkNull(inRec, "YD_STRCHAR_ID").equals("") ) {
				inRec.setField("YD_STRCHAR_ID", getYdStrcharId());
			}
			
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
	} // end of insYdStrchar
	
	/**
	 *      [A] 오퍼레이션명 : 야드 저장속성 parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_YD_STRCHAR_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STRCHAR_GRP_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
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
			
			szFieldName = "V_ORD_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
//0116			
//			szFieldName = "V_DEST_CD";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
//			if (!blnErr) return blnErr;
			
			szFieldName = "V_CUST_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
//			szFieldName = "V_DEMANDER_CD";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
//			if (!blnErr) return blnErr;			
			
			szFieldName = "V_DELIVER_TERM_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_DTL";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}

		return blnErr;
	} //end of chkPara_YdStrchar
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드 저장속성 UPDATE
	 * 
	 * @param JDTORecord inRec parameter record
	 *         int       intGp 구분(0:YD_STRCHAR_ID)
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, 
	 *                         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStrchar(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdStrchar";
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
			intRtnVal = this.getYdStrchar(inRec, outRecSet, 0);
			
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
	} // end of updYdStrchar
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드 저장속성 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_YD_STRCHAR_ID";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
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
	    
			szFieldName = "V_ORD_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_DEST_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_CUST_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_DEMANDER_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_DELIVER_TERM_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_ORD_NO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_ORD_DTL";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		
	} // end of YdStrchar_DataMapping
	
/*------------------------------------- DELETE -------------------------------------------*/
} // end of class

