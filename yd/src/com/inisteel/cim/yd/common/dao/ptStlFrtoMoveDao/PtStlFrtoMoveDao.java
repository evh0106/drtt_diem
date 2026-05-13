/**
 * 
 */
package com.inisteel.cim.yd.common.dao.ptStlFrtoMoveDao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdDaoUtils;

/**
 * @author Administrator
 *
 */
public class PtStlFrtoMoveDao {
	// Dao Name
	private String szDaoName = getClass().getName();

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.common.dao.ptStlFrtoMoveDao.getPtStlFrtoMove";
	
	
	//update query id
	//이송상차일자 업데이트
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.common.dao.ptStlFrtoMoveDao.updPtStlFrtoMove1";
	//이송완료일자, 이송계상일자, 이송상태코드 업데이트
	private String szQueryIdUpd2 = "com.inisteel.cim.yd.common.dao.ptStlFrtoMoveDao.updPtStlFrtoMove2";
	
	//이송상차일자 CLEAR (재료정보 삭제시이송상차일자를 UPDATE 해준다)
	private String szQueryIdUpd3 = "com.inisteel.cim.yd.common.dao.ptStlFrtoMoveDao.updPtStlFrtoMoveUpdUpDate";
	
	/**
	 * 이송지시테이블조회
	 * @param inRec
	 * @param outRecSet
	 * @param intGp
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getPtStlFrtoMove(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdStklyr";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkParameterForSelect(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet1);
			
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
	}
	/**
	 * 이송지시테이블 업데이트 처리
	 * @param inRec
	 * @param intGp
	 * @return
	 * @throws DAOException
	 */
	public int updPtStlFrtoMove(JDTORecord inRec, int intGp) throws DAOException {
		String szMethodName = "updPtStlFrtoMove";
		String szMsg = null;
		int intRtnVal = 0;
		JDTORecord recOutPara = null;
		boolean blnChk_Field = true;
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkParameterForUpt(recOutPara, intGp);
			//parameter error return
			if (!blnChk_Field) return intRtnVal = -1;
			
			if (intGp == 0)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd1);
			else if( intGp == 1 )
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd2);
			else if( intGp == 2 )
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd3);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			if (intRtnVal <= 0) intRtnVal = -3;
		}catch(JDTOException e) {	
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	}
	
	/**
	 * 파라미터 체크 - 조회용
	 * @param inRec
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public boolean chkParameterForSelect(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		if( intGp == 0 ) {
			szFieldName = "V_STL_NO";									//재료번호
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
		}
		return blnErr;
	}
	
	/**
	 * 파라미터 체크 - 업데이트용
	 * @param inRec
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public boolean chkParameterForUpt(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		if( intGp == 0 ) {
			szFieldName = "V_FRTOMOVE_CARLOAD_DATE";					//이송상차일자
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_MTL_PLN_STR_FR_LOC_CD";					//야드재료예정저장From위치코드
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_STL_NO";									//재료번호
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_TRANSWORD_SEQNO";							//이송지시차수
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, YdDaoUtils.INTEGER_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
		}else if( intGp == 1 ) {
			szFieldName = "V_YD_MTL_PLN_STR_TO_LOC_CD";					//야드재료예정저장To위치코드
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_FRTOMOVE_STAT_CD";							//이송상태코드
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_STL_NO";									//재료번호
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_TRANSWORD_SEQNO";							//이송지시차수
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, YdDaoUtils.INTEGER_TYPE, 0, 0);
			if (!blnErr) return blnErr;
		} else if( intGp == 2 ) {
			
			szFieldName = "V_FRTOMOVE_CARLOAD_DATE";					//이송상차일자
			blnErr = ydDaoUtils.chkField(inRec, szFieldName,  8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_MTL_PLN_STR_FR_LOC_CD";					//이송상차일자
			blnErr = ydDaoUtils.chkField(inRec, szFieldName,  10, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			
			szFieldName = "V_MODIFIER";									//수정자
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_STL_NO";									//재료번호
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
		
			
			
		}
		return blnErr;
	}
}
