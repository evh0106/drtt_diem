/*
 * @(#) 야드설비사양 DAO
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/12/11
 *
 * @description		야드설비사양 DAO
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/12/11   김현우      김현우       최초작성
 */

package com.inisteel.cim.yd.jplateyd.dao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;

/**
 *      [A] 클래스명 : 야드설비사양 DAO
 *
*/

public class JPlateYdCrnSpecDAO {

	// Dao Name
	private final static String SZ_DAO_NAME = JPlateYdCrnSpecDAO.class.getName();

	private JPlateYdUtils 		ydUtils 	= new JPlateYdUtils();
	private DBAssistantDAO 		dbAssDao 	= new DBAssistantDAO();
	private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();

/*------------------------------------- SELECT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드설비사양 SELECT
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdCrnSpec(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp = null;
		JDTORecord 		recPara = null;

		String 	sMethodName = "getYdCrnSpec";
		int 	intRtnVal 	= -100;
		boolean blnChkField = true;
		String 	sMsg 		= null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnChkField = ydDaoUtils.chkField(recPara, "V_YD_EQP_ID", 6, 1, 'S', 0, 0);

			//parameter error return
			if (!blnChkField) {
				return intRtnVal = -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnspecdao.YdCrnspecDao.getYdCrnspec
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSpecDAO.getYdCrnSpec
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSpecDAO.getYdCrnSpec");

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
	} //end of getYdCrnSpec

/*------------------------------------- INSERT -------------------------------------------*/

/*------------------------------------- UPDATE -------------------------------------------*/

/*------------------------------------- DELETE -------------------------------------------*/

/*------------------------------------- ETC    -------------------------------------------*/

} // end of class
