/*
 * @(#) 야드작업이력 DAO
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/12/05
 *
 * @description		야드작업이력 DAO
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/12/05   김현우      김현우       최초작성
 */

package com.inisteel.cim.yd.jplateyd.dao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;

/**
 *      [A] 클래스명 : 야드작업이력 DAO
 *
*/

public class JPlateYdWrkHistDAO {

	// Dao Name
	private static final String SZ_DAO_NAME = JPlateYdWrkHistDAO.class.getName();

	private JPlateYdUtils 		ydUtils 	= new JPlateYdUtils();
	private DBAssistantDAO 		dbAssDao 	= new DBAssistantDAO();
	private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();

/*------------------------------------- SELECT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드작업이력 SELECT [SEQ조회 : YD_WRK_HIST_ID]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public String getSeqId() throws DAOException, JDTOException {
		JDTORecordSet rsTemp 	= null;
		JDTORecord recPara 		= null;
		String sMethodName 		= "getSeqId";
		String sSeqId 			= null;

		try {

			recPara = JDTORecordFactory.getInstance().create();

			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO.getSeqId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO.getSeqId");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp != null && rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				sSeqId = ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "YD_WRK_HIST_ID"); //Sequence ID
			}else {
				//data not found
				String sMsg = "["+sMethodName+"]data not found!";
				ydUtils.putLog(SZ_DAO_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return sSeqId;
	} //end of getSeqId

	/**
	 *      [A] 오퍼레이션명 : 야드작업이력 SELECT , intGp == 0
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdWrkHist(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		String sMethodName 		= "getYdWrkHist";
		JDTORecordSet rsTemp 	= null;
		int intRtnVal 			= -100;
		boolean blnChkField 	= true;
		String sMsg 			= null;
		JDTORecord recPara 		= null;

		try {

			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnChkField = ydDaoUtils.chkField(recPara, "V_YD_WRK_HIST_ID", 18, 1, 'S', 0, 0);

			if (!blnChkField) {
				return intRtnVal = -2;
			}

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkHistDao
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO.getYdWrkHist
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO.getYdWrkHist");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				sMsg = "["+sMethodName+"]data found!";
				ydUtils.putLog(SZ_DAO_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

			} else {
				//data not found
				sMsg = "["+sMethodName+"]data not found!";
				ydUtils.putLog(SZ_DAO_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);
				return intRtnVal = 0;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getYdWrkHist


	/**
	 *      [A] 오퍼레이션명 : 야드작업이력 SELECT [V_YD_CRN_SCH_ID, V_STL_NO] , intGp == 4
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdWrkHistByPlate(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		String sMethodName 		= "getYdWrkHist";
		JDTORecordSet rsTemp 	= null;
		int intRtnVal 			= -100;
		boolean blnChkField 	= true;
		String sMsg 			= null;
		JDTORecord recPara 		= null;

		try {

			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnChkField = ydDaoUtils.chkField(recPara, "V_YD_CRN_SCH_ID", 18, 1, 'S', 0, 0);
			if (blnChkField) {
				blnChkField = ydDaoUtils.chkField(recPara, "V_STL_NO", 11, 1, 'S', 0, 0);
			}

			if (!blnChkField) {
				return intRtnVal = -2;
			}

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdCrnStockByPlate
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO.getYdWrkHistByPlate
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO.getYdWrkHistByPlate");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				sMsg = "["+sMethodName+"]data found!";
				ydUtils.putLog(SZ_DAO_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

			} else {
				//data not found
				sMsg = "["+sMethodName+"]data not found!";
				ydUtils.putLog(SZ_DAO_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);
				return intRtnVal = 0;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getYdWrkHistByPlate

	/**
	 *      [A] 오퍼레이션명 : 야드작업이력 SELECT [조회조건:재료번호], intGp == 9
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdWrkHistByStlNo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		String sMethodName 		= "getYdWrkHistByStlNo";
		JDTORecordSet rsTemp 	= null;
		int intRtnVal 			= -100;
		boolean blnChkField 	= true;
		String sMsg 			= null;
		JDTORecord recPara 		= null;

		try {

			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnChkField = ydDaoUtils.chkField(recPara, "V_STL_NO", 11, 1, 'S', 0, 0);

			if (!blnChkField) {
				return intRtnVal = -2;
			}

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkHistDaoStlNo
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO.getYdWrkHistByStlNo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO.getYdWrkHistByStlNo");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				sMsg = "["+sMethodName+"] data found!";
				ydUtils.putLog(SZ_DAO_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

			} else {
				//data not found
				sMsg = "["+sMethodName+"]data not found!";
				ydUtils.putLog(SZ_DAO_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);
				return intRtnVal = 0;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getYdWrkHistByStlNo

/*------------------------------------- INSERT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드작업이력 INSERT
	 *
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int insYdWrkHist(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal 		= 0;
		JDTORecord recPara 	= null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			String sYdWrkHistId = ydDaoUtils.paraRecChkNull(recPara,"V_YD_WRK_HIST_ID");

			if ("".equals(sYdWrkHistId)) {
				recPara.setField("V_YD_WRK_HIST_ID", this.getSeqId());		// 야드작업이력ID NULL이면 SEQ 채번
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdWrkHistDao
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO.insYdWrkHist
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO.insYdWrkHist");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdWrkHist


	/**
	 *      [A] 오퍼레이션명 : TO위치 HIST INSERT
	 *
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int insYdCrnTolocHist(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal 		= 0;
		JDTORecord recPara 	= null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdWrkHistDao
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO.insYdCrnTolocHist
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO.insYdCrnTolocHist");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdCrnTolocHist

/*------------------------------------- UPDATE -------------------------------------------*/


/*------------------------------------- ETC -------------------------------------------*/


} // end of class
