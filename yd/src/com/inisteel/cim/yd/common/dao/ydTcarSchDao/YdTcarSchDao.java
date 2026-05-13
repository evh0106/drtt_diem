package com.inisteel.cim.yd.common.dao.ydTcarSchDao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 *      [A] 클래스명 : 야드대차스케줄 DAO
 * 
*/

public class YdTcarSchDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarsch";
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschUDLDWRKBOOKID";
	//2009.02.17 권오창추가 (기본쿼리로 해도 될지는 테스트중이라 나중에 바꿔야됨 지금은 테스트 중이라)
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschByTcarSchId";
	
	//20090310 이현성 
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschYdGpYdTCar";
	
	//20090315 이현성 
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschByYdEqpId";
	
	//20090601 김진욱
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschId";

	// 20091016 권오창 (YD_TCAR_SCH_ID)
	private String szQueryIdGet7 = "com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschIdStlNoMtlWtByTcarSchId";

	//COIL 야드 대차 권상후
	private String szQueryIdGet200 = "com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschLD";
	private String szQueryIdGet201 = "com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschUD";
	private String szQueryIdGet202 = "com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarStat";
	private String szQueryIdGet203 = "com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarWrkBookId";
	
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.insYdTcarsch";
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.updYdTcarsch";
	//
	private String szQueryIdUpdDir1 = "com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.updYdTcarschYdTCarLdWrkBookId"; 	//  상차 대차  작업 예약 ID CLEAR
	private String szQueryIdUpdDir2 = "com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.updYdTcarschYdTCarUdWrkBookId"; 	//  하차 대차  작업 예약 ID CLEAR
	private String szQueryIdUpdDir3 = "com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.updYdTcarschDel"; 					//  대차스케줄 삭제

	
/*------------------------------------- SELECT -------------------------------------------*/
	
	/**
	 * 오퍼레이션명 : 야드대차스케쥴ID를 생성하여 반환하는 메소드
	 * @return String : 야드대차스케쥴ID
	 */
	public String getYdTcarschId() throws DAOException {
		//메소드명
		String szMethodName = "getYdTcarschId";
		//레코드
		JDTORecord recKey = JDTORecordFactory.getInstance().create();
		//대차스케쥴ID
		String szYdTCarSchId = "";
		try {
			//JSPEED 쿼리ID
			recKey.setField("JSPEED_QUERY_ID", 			szQueryIdGet6);
			recKey.setField("V_YD_TCAR_SCH_ID", 		"1");
			//쿼리 실행
			JDTORecordSet rsTemp = dbAssDao.getRecordSet(recKey);
			if( rsTemp.size() <= 0 ) {
				throw new JDTOException("야드대차스케줄ID 레코드가 존재하지 않음");
			}
			rsTemp.first();
			recKey = rsTemp.getRecord();
		
			szYdTCarSchId = ydDaoUtils.paraRecChkNull(recKey, "YD_TCAR_SCH_ID");
		}catch(JDTOException e) {
			String szMsg = "야드대차스케줄ID 생성 시 에러 발생";
			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
			throw new DAOException(szMsg, e);
		}
		return szYdTCarSchId;
	}
	
	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:YD_TCAR_SCH_ID
	 *                                      1:YD_CARUD_WRK_BOOK_ID,YD_CARLD_WRK_BOOK_ID
	 *                                      2:YD_TCAR_SCH_ID
	 *                                      3:YD_GP, YD_TCAR
	 *                                      4:YD_EQP_ID
	 *                                      5:YD_TCAR_SCH_ID[CREATE YD_TCAR_SCH_ID])
	 *                                      6:YD_TCAR_SCH_ID
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdTcarsch(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdTcarsch";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdTcarsch(recPara, intGp);
			
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
			else if (intGp == 200)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet200);
			else if (intGp == 201)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet201);
			else if (intGp == 202)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet202);
			else if (intGp == 203)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet203);
			
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
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal = rsTemp.size();
	} //end of getYdTcarsch
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec  parameter record
	 *         int intGp         구분(0:YD_TCAR_SCH_ID
	 *                               1:YD_CARUD_WRK_BOOK_ID,YD_CARLD_WRK_BOOK_ID
	 *                               2:YD_TCAR_SCH_ID
	 *                               3:YD_GP, YD_TCAR
	 *                               4:YD_EQP_ID)
	 *                               6:YD_TCAR_SCH_ID
	 * @return boolean           true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdTcarsch(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;

		try {
			if (intGp == 0 || intGp == 2 || intGp == 5 || intGp == 6) {
				szFieldName = "V_YD_TCAR_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			} else if (intGp == 1) {
				
				szFieldName = "V_YD_CARUD_WRK_BOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_CARLD_WRK_BOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
			} else if (intGp == 3) {
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_TCAR";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			} else if (intGp == 4) {
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdTcarsch
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdTcarsch(JDTORecord inRec) throws DAOException, JDTOException {
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
	} // end of insYdTcarsch
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄 INSERT parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_YD_TCAR_SCH_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
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
			
			szFieldName = "V_YD_EQP_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_EQP_WRK_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_PROG_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_EQP_WRK_SH";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_EQP_WRK_WT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_BED_TP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_CARLD_LEV_LOC";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_CARLD_LEV_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_CARLD_ARR_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARLD_WRK_BOOK_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARLD_SCH_REQ_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARLD_STOP_LOC";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARLD_ST_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_CARLD_CMPL_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARLD_WRK_ACT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARLD_WRK_CRN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARUD_WRK_ACT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARUD_LEV_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_CARUD_ARR_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARUD_WRK_BOOK_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARUD_SCH_REQ_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARUD_STOP_LOC";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARUD_ST_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_CARUD_CMPL_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARUD_WRK_CRN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CAR_PROG_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_TCAR_WRK_SEQ";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}

		return blnErr;
	} //end of chkParameter
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_TCAR_SCH_ID)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdTcarsch(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdTcarsch";
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
			intRtnVal = this.getYdTcarsch(inRec, outRecSet, 0);
			
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
	} // end of updYdTcarsch
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_YD_TCAR_SCH_ID";
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
			
			szFieldName = "V_YD_EQP_ID";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_WRK_STAT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_PROG_STAT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_WRK_SH";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_EQP_WRK_WT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_BED_TP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_CARLD_LEV_LOC";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_CARLD_LEV_DT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_CARLD_ARR_DT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARLD_WRK_BOOK_ID";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARLD_SCH_REQ_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARLD_STOP_LOC";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARLD_ST_DT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_CARLD_CMPL_DT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARLD_WRK_ACT_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARLD_WRK_CRN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARUD_WRK_ACT_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARUD_LEV_DT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_CARUD_ARR_DT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARUD_WRK_BOOK_ID";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARUD_SCH_REQ_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARUD_STOP_LOC";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARUD_ST_DT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_CARUD_CMPL_DT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARUD_WRK_CRN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CAR_PROG_STAT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_TCAR_WRK_SEQ";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}

	} // end of dataMapping
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드대차 스케줄 DIRECT UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분()
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdTCarschDir(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdTCarschDir";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			//recordSet create
			
			//변환용 레코드
			JDTORecord recInPara = null;
			
			//수정
			//필드명 변환 (필드명 -> V_필드명)
			recInPara = JDTORecordFactory.getInstance().create();
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);


		    if (intGp == 0) {
				szFieldName = "V_MODIFIER";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 10, 2, 'S', 0, 0);
				if (!blnErr) return intRtnVal = -2;
								
				szFieldName = "V_YD_CARLD_WRK_BOOK_ID";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 18, 1, 'S', 0, 0);
				
				if (!blnErr) return intRtnVal = -2;
				
			} else if (intGp == 1 ) {
				szFieldName = "V_MODIFIER";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 10, 2, 'S', 0, 0);
				if (!blnErr) return intRtnVal = -2;
								
				szFieldName = "V_YD_CARUD_WRK_BOOK_ID";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 18, 1, 'S', 0, 0);
				
				if (!blnErr) return intRtnVal = -2;
				
			} else if (intGp == 2 ) {
				
				szFieldName = "V_MODIFIER";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 10, 1, 'S', 0, 0);
				if (!blnErr) return intRtnVal = -2;
								
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 6, 1, 'S', 0, 0);
				
				if (!blnErr) return intRtnVal = -2;
				
			}

			//query id setting
			if (intGp == 0)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpdDir1);
			if (intGp == 1)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpdDir2);
			if (intGp == 2)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpdDir3);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);
			
			//execution error return
			if (intRtnVal < 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdTCarschDir
	
	
	
	
	
	
/*------------------------------------- DELETE -------------------------------------------*/
} // end of class






