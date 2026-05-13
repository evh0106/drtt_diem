/*
 * @(#) 야드설비휴지 DAO
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/14
 *
 * @description		야드설비휴지 DAO
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/14   김현우      김현우       최초작성
 */

package com.inisteel.cim.yd.jplateyd.dao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;

/**
 *      [A] 클래스명 : 야드설비휴지 DAO
 *
*/

public class JPlateYdEqpPauseDAO {

	// Dao Name
	private final String SZ_DAO_NAME = getClass().getName();

	private DBAssistantDAO 		dbAssDao 	= new DBAssistantDAO();
	private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();

/*------------------------------------- SELECT -------------------------------------------*/


/*------------------------------------- INSERT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드설비휴지 INSERT
	 *
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int insYdEqpPause(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnErr = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnErr = this.chkParameter(recPara);

			//parameter error return
			if (!blnErr) {
				return -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydeqppausedao.YdEqppauseDao.insYdEqppause
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpPauseDAO.insYdEqpPause
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpPauseDAO.insYdEqpPause");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdEqpPause


/*------------------------------------- UPDATE -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 설비 휴지테이블 업데이트
	 *
	 * @param  JDTORecord inRec
	 * @return int              execution count(성공),    -2:parameter error,    -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdEqpPauseRepair(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecord recPara  = null;
		int intRtnVal       = 0;
//		boolean blnErr 		= true;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

//			//parameter check
//
//			//parameter error return
//			if(!blnErr) { return -2; }

			//쿼리 아이디 세팅
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydeqppausedao.YdEqppauseDao.updYdEqppauseRepair
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpPauseDAO.updYdEqpPauseRepair
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpPauseDAO.updYdEqpPauseRepair");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

			//execution error return
			if(intRtnVal <= 0){
				intRtnVal = -3;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdEqpPauseRepair

/*------------------------------------- DELETE -------------------------------------------*/

/*------------------------------------- ETC    -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드설비휴지 parameter Check
	 *
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {

		boolean blnErr = true;

		try {

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_PAUSE_OCCR_SEQ", 18, 1, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_ID", 6, 1, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_REGISTER", 10, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_REG_DDTT", 0, 3, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

//			blnErr = ydDaoUtils.chkField(inRec, "V_MODIFIER", 10, 2, 'S', 0, 0);
//			if(!blnErr) { return blnErr; }
//
//			blnErr = ydDaoUtils.chkField(inRec, "V_MOD_DDTT", 0, 3, 'S', 0, 0);
//			if(!blnErr) { return blnErr; }
//
//			blnErr = ydDaoUtils.chkField(inRec, "V_DEL_YN", 1, 2, 'S', 0, 0);
//			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_PAUSE_PASS_HR_CARRYOV", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_PAUSE_CODE", 4, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_PAUSE_OCC_WRK_DUTY", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_PAUSE_OCC_WRK_PARTY", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_PAUSE_OCC_DT", 0, 3, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_PAUSE_END_DT", 0, 3, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_PAUSE_END_WRK_DUTY", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_PAUSE_END_WRK_PARTY", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_PAUSE_PASS_HR", 6, 2, 'L', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_PAUSE_RCVR_CNTS", 100, 2, 'S', 0, 0);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter

} // end of class
