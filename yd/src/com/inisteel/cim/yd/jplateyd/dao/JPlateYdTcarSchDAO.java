/*
 * @(#) 야드대차스케줄 DAO
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/21
 *
 * @description		야드대차스케줄 DAO
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/21   김현우      김현우       최초작성
 */

package com.inisteel.cim.yd.jplateyd.dao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;

/**
 *      [A] 클래스명 : 야드대차스케줄 DAO
 *
*/

public class JPlateYdTcarSchDAO {

	// Dao Name
	private final String SZ_DAO_NAME = getClass().getName();

	private JPlateYdUtils 		ydUtils 	= new JPlateYdUtils();
	private DBAssistantDAO 		dbAssDao 	= new DBAssistantDAO();
	private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();

/*------------------------------------- SELECT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 대차스케줄ID 조회 [SEQ 생성] , intGp == 5
	 *
	 * @param  none
	 * @return String                   [스케쥴ID]
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public String getSeqId() throws DAOException, JDTOException {

		JDTORecordSet   rsTemp  = null;
		JDTORecord      recPara = null;
		String          szSeqId = null;

		try {

			recPara = JDTORecordFactory.getInstance().create();

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschId
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.getSeqId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.getSeqId");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				szSeqId = ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "YD_TCAR_SCH_ID");
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return szSeqId;
	} //end of getSeqId

	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄 SELECT , intGp == 0
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdTcarSch(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdTcarSch";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarsch
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.getYdTcarSch
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.getYdTcarSch");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0){
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			} else {
				//data not found
				String szMsg = "data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;

	} //end of getYdTcarSch

	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄 SELECT , intGp == 1
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getByWrkBookId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getByWrkBookId";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschUDLDWRKBOOKID
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.getByWrkBookId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.getByWrkBookId");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0){
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			} else {
				//data not found
				String szMsg = "data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;

	} //end of getByWrkBookId

	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄 SELECT , intGp == 4
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getByYdEqpId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getByYdEqpId";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschByYdEqpId
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.getByYdEqpId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.getByYdEqpId");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			} else {
				//data not found
				String szMsg = "data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;

	} //end of getByYdEqpId

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

		int 	intRtnVal 	= 0;
		boolean blnChkField = true;
		JDTORecord recPara 	= null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnChkField = this.chkParameter(recPara);

			//parameter error return
			if (!blnChkField) {
				return intRtnVal = -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.insYdTcarsch
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.insYdTcarsch
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.insYdTcarsch");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdTcarsch

/*------------------------------------- UPDATE -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄 UPDATE
	 *
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdTcarSch(JDTORecord inRec) throws DAOException, JDTOException {
		int 	intRtnVal 		= 0;

	/*------------------------------------------------------------------------
	 * 전체항목 UPDATE Query를 해당 항목만 UPDATE하도록 Method 세분화함으로써 주석처리함
	 *------------------------------------------------------------------------
		JDTORecord outRec 		= null;
		String 	szMethodName 	= "updYdTcarSch";
		String 	szMsg 			= null;
		boolean blnChkField 	= true;

		try {
			//recordSet create
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

			//변환용 레코드
			JDTORecord recInPara 	= null;
			JDTORecord recOutPara 	= null;

			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//update data select
			intRtnVal = this.getYdTcarSch(inRec, outRecSet);

			//parameter error return
			if (intRtnVal < 0) {
				szMsg = "parameter error!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, 1);
				return intRtnVal;
			}

			//data not found return
			if (intRtnVal == 0) {
				szMsg = "data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, 1);
				return intRtnVal;
			}

			//duplicate data return
			if (outRecSet.size() != 1) {
				szMsg = "duplicate data!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}

			outRecSet.first();
			outRec = outRecSet.getRecord();

			//필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(outRec, 0);

			//data mapping
			this.dataMapping(recInPara, recOutPara);

			//parameter check
			blnChkField = this.chkParameter(recOutPara);

			//parameter error return
			if (!blnChkField) {
				return intRtnVal = -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.updYdTcarsch
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.updYdTcarSch
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.updYdTcarSch");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);

			//execution error return
			if (intRtnVal <= 0) {
				intRtnVal = -3;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
	*/
		return intRtnVal;
	} // end of updYdTcarsch

	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄 _ 야드설비작업상태 UPDATE
	 *
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdEqpWrkStat(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.updYdEqpWrkStat
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.updYdEqpWrkStat");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdEqpWrkStat

	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄 상차/하차정보 UPDATE
	 *
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdCarLdUdInfo(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.updYdCarLdUdInfo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.updYdCarLdUdInfo");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdCarLdUdInfo

	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄 상차정보 UPDATE
	 *
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdCarLdInfo(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.updYdCarLdInfo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.updYdCarLdInfo");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdCarLdInfo

	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄 하차정보 UPDATE
	 *
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdCarUdInfo(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.updYdCarUdInfo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.updYdCarUdInfo");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdCarUdInfo

	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄 상차정보 CLEAR
	 *
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int clsYdCarLdWrkBookId(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.updYdTcarschYdTCarLdWrkBookId
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.clsYdCarLdWrkBookId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.clsYdCarLdWrkBookId");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of clsYdTCarLdWrkBookId

	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄 하차정보 Clear
	 *
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int clsYdCarUdWrkBookId(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.updYdTcarschYdTCarUdWrkBookId
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.clsYdCarUdWrkBookId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.clsYdCarUdWrkBookId");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of clsYdCarUdWrkBookId

/*------------------------------------- DELETE -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄 DELETE
	 *
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int delYdTcarsch(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.delYdTcarsch
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.delYdTcarsch");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of delYdTcarsch

/*------------------------------------- ETC    -------------------------------------------*/
	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄 UPDATE parameter mapping
	 *
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException
	 */
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {

		try {

			ydDaoUtils.mappingData(inRec, outRec, "V_YD_TCAR_SCH_ID");
			ydDaoUtils.mappingData(inRec, outRec, "V_REGISTER");
			ydDaoUtils.mappingData(inRec, outRec, "V_REG_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_MODIFIER");
			ydDaoUtils.mappingData(inRec, outRec, "V_MOD_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_DEL_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_ID");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_WRK_STAT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_PROG_STAT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_WRK_SH");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_WRK_WT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_TP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_LEV_LOC");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_LEV_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_ARR_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_WRK_BOOK_ID");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_SCH_REQ_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_STOP_LOC");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_ST_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_CMPL_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_WRK_ACT_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_WRK_CRN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_WRK_ACT_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_LEV_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_ARR_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_WRK_BOOK_ID");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_SCH_REQ_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_STOP_LOC");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_ST_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_CMPL_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_WRK_CRN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CAR_PROG_STAT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_TCAR_WRK_SEQ");

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}

	} // end of dataMapping

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
			if (!blnErr) { return blnErr; }

			szFieldName = "V_REGISTER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_REG_DDTT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

//			szFieldName = "V_MODIFIER";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
//			if (!blnErr) { return blnErr; }
//
//			szFieldName = "V_MOD_DDTT";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
//			if (!blnErr) { return blnErr; }
//
//			szFieldName = "V_DEL_YN";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
//			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_EQP_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_EQP_WRK_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_WRK_PROG_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_EQP_WRK_SH";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_EQP_WRK_WT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_STK_BED_TP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_LEV_LOC";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_LEV_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_ARR_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_WRK_BOOK_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_SCH_REQ_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_STOP_LOC";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_ST_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_CMPL_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_WRK_ACT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_WRK_CRN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_WRK_ACT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_LEV_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_ARR_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_WRK_BOOK_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_SCH_REQ_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_STOP_LOC";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_ST_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_CMPL_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_WRK_CRN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CAR_PROG_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_TCAR_WRK_SEQ";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		return blnErr;
	} //end of chkParameter

} // end of class
