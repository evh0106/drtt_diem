/**
 * 
 */
package com.inisteel.cim.yd.common.dao.qmBuySlabInfoDao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

/**
 * @author Administrator
 *
 */
public class QmBuySlabInfoDao {
	// Dao Name
	private String szDaoName = getClass().getName();

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	private CCommUtils	   commUtils   = new CCommUtils();	
	
	private String szQueryIdGet2 = "com.inisteel.cim.yd.common.dao.qmBuySlabInfoDao.getMakeNameList";
	
	private String szQueryIdGet3 = "com.inisteel.cim.yd.common.dao.qmBuySlabInfoDao.getMakeNameList2_PIDEV";
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.common.dao.qmBuySlabInfoDao.";
	//update query id
	private String szQueryIdUpd2 = "com.inisteel.cim.yd.common.dao.qmBuySlabInfoDao.";
	
	/**
	 * 오퍼레이션명 : 구입슬라브정보 SELECT
	 * @param inRec
	 * @param outRecSet
	 * @param intGp			구분(
	 * 							1: 없음
	 * 						)
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getQmBuySlabInfo(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getQmBuySlabInfo";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
					
			//parameter check
			blnChk_Field = this.chkParameter(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting 
			if (intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet2);
			if (intGp == 2)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet3);
			
//PIDEV
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
	} //end of getQmBuySlabInfo
	
	/**
	 * 파라미터 체크
	 * @param inRec
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public boolean chkParameter(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
//		if( intGp == 0 || intGp == 1 ) {
//			szFieldName = "V_MSLAB_NO";						//주편번호
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.STRING_TYPE, 0, 0);
//			if (!blnErr) return blnErr;
//			
//		}
		return blnErr;
	}
}
