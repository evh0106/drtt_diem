package com.inisteel.cim.yd.common.dao.ptPlateCommDao;

import java.util.List;

import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;

public class PtPlateCommDao {
	// Dao Name
	private String szDaoName = getClass().getName();

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	private String szQueryIdGet1 = "com.inisteel.cim.yd.common.dao.ptPlateCommDao.getPtPlateCommForGoodsReturn_PIDEV";
	
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.common.dao.ptPlateCommDao.updPtPlateCommPilingBookOut";
	
	private String szQueryIdUpd2 = "com.inisteel.cim.yd.common.dao.ptPlateCommDao.updPtPlateCommStrLocBookOut";
	//윤재광(2010. 04. 13) 야드저장품 파일링코드 UPDATE 
	private String szQueryIdUpd3 = "com.inisteel.cim.yd.common.dao.ptPlateCommDao.updYdStockPilingCd";
	//윤재광(2010. 04. 13) Plate공통  파일링코드 UPDATE
	private String szQueryIdUpd4 = "com.inisteel.cim.yd.common.dao.ptPlateCommDao.updPtPlateCommPilingCd";

	
	private String szQueryIdUpd5 = "com.inisteel.cim.yd.common.dao.ptPlateCommDao.updYdStockPilingCdOrdNo";
	// Plate공통  파일링코드 UPDATE
	private String szQueryIdUpd6 = "com.inisteel.cim.yd.common.dao.ptPlateCommDao.updPtPlateCommPilingCdStlNo";

	// Plate공통  파일링코드 UPDATE
	private String szQueryIdUpd7 = "com.inisteel.cim.yd.common.dao.ptPlateCommDao.updPtPlateCommPilingStrLocBookOut";
	
	private String szQueryIdUpd8 = "com.inisteel.cim.yd.common.dao.ptPlateCommDao.updYdStockPilingCdOrdNo2";//파일링코드 NULL 여부 상관없이 UPDATE
	
	
	
	/**
	 *      [A] 오퍼레이션명 : OS공통테이블 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:YD_WTCL_TNK_SCH_ID,STL_NO, 
	 *         								1:YD_WTCL_TNK_SCH_ID
	 *         								4:V_PAGE_CNT1, V_ROW_CNT1, V_PAGE_CNT2, V_ROW_CNT1
	 *         								6:ORD_NO
	 *         								7:7:V_SEARCH_GBN, V_CONFIRM_DELIVER_END_DATE, V_YD_STR_CHAR_GP, V_DEST_CD, V_DEMANDER_CD, V_ORD_NO, V_ORD_DTL
	 *         								8:V_ROLL_UNIT_NAME
	 *         								9:ORD_NO, ORD_DTL
	 *         								)
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getPtPlateCommDao(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getPtPlateCommDao";
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
	} //end of getPtPlateCommDao
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드 저장속성 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(
	 *                              0: V_YD_STRCHAR_ID
	 *                              1:YD_STRCHAR_GRP_CD
	 *                              3:SEARCH_GBN, YD_STRCHAR_ID, DEST_CD, DEMANDER_CD, ORD_NO, ORD_DTL
	 *                              4:V_PAGE_CNT1, V_ROW_CNT1, V_PAGE_CNT2, V_ROW_CNT1
	 *                              6:ORD_NO
	 *                              7:V_SEARCH_GBN, V_CONFIRM_DELIVER_END_DATE, V_YD_STR_CHAR_GP, V_DEST_CD, V_DEMANDER_CD, V_ORD_NO, V_ORD_DTL
	 *                              8:V_ROLL_UNIT_NAME
	 *                              9:ORD_NO, ORD_DTL
	 *                              )
	 * @return boolean          true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameterForSelect(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		try {
			if (intGp == 0) {
				szFieldName = "V_PLATE_NO";								//후판제품번호
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			}
				
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkParameterForSelect
	

	/**
	 * 후판공통테이블 업데이트 처리
	 * @param inRec
	 * @param intGp
	 * @return
	 * @throws DAOException
	 */
	public int updPtPlateComm(JDTORecord inRec, int intGp) throws DAOException {

		String szMethodName         = "updYdStock";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
	    		
	    		String StringGp=Integer.toString(intGp) ;
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updPtPlateCommReTX", new Class[] { JDTORecord.class, String.class }, new Object[] { inRec ,StringGp});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.updPtPlateCommTX(inRec, intGp);
//	    		if(intRtnVal ==0){
//	    			return intRtnVal = -1;
//	    		}
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updPtPlateComm
	
	
	/**
	 * 후판공통테이블 업데이트 처리
	 * @param inRec
	 * @param intGp
	 * @return
	 * @throws DAOException
	 */
	public int updPtPlateCommTX(JDTORecord inRec, int intGp) throws DAOException {
		String szMethodName = "updPtPlateCommTX";
		String szMsg = null;
		int intRtnVal = 0;
		JDTORecord recOutPara = null;
		boolean blnChk_Field = true;
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkParameter(recOutPara, intGp);
			//parameter error return
			if (!blnChk_Field) return intRtnVal = -1;
			
			if (intGp == 0)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd1);
			else if (intGp == 1)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd2);
			else if (intGp == 2)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd3);
			else if (intGp == 3)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd4);
			else if (intGp == 4)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd5);
			else if (intGp == 5)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd6);
			else if (intGp == 6)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd7);
			else if (intGp == 7)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd8);			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			if (intRtnVal <= 0) intRtnVal = -3;
		}catch(JDTOException e) {	
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} //updPtPlateCommTX
	
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
		if( intGp == 0 ) {
			szFieldName = "V_PLATE_NO";								//후판제품번호
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_PILING_CD";							//야드Piling코드
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_BOOK_OUT_LOC";						//야드BookOut위치
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
		} else if( intGp == 1 ) {
			szFieldName = "V_PLATE_NO";								//후판제품번호
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STR_LOC";							//야드저장위치
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_BOOK_OUT_LOC";						//야드BookOut위치
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
		}  else if(intGp == 2) {
			szFieldName = "V_ORG_YD_PILING_CD";					//Piling 코드
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CHG_YD_PILING_CD";					//Piling 위치
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_NO";						//주문번호
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
		}  else if(intGp == 2) {
			szFieldName = "V_ORG_YD_PILING_CD";					//Piling 코드
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CHG_YD_PILING_CD";					//Piling 위치
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_NO";						//주문번호
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			
		}
		return blnErr;
	}
}
