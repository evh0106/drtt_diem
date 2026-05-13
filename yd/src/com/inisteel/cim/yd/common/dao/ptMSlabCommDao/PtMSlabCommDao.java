/**
 * 
 */
package com.inisteel.cim.yd.common.dao.ptMSlabCommDao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import jspeed.base.util.StringHelper;
import com.inisteel.cim.yd.common.util.YdConstant;
import java.util.List;
import jspeed.base.ejb.EJBConnector;
/**
 * [A] 클래스명 : PT 주편공통 DAO
 *
 */
public class PtMSlabCommDao {    
	// Dao Name
	private String szDaoName = getClass().getName();

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	//update query id - 소재이송일시
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.common.dao.ptMSlabCommDao.updPtMSlabCommMatlFtmvDt";
	//update query id - 소재인수일시
	private String szQueryIdUpd2 = "com.inisteel.cim.yd.common.dao.ptMSlabCommDao.updPtMSlabCommMatlTkovDt";
	
	public int updPtMSlabComm(JDTORecord inRec, int intGp) throws DAOException, JDTOException {

		String szMethodName         = "updPtMSlabComm";
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
//				//트렌젝션 분리 적용	
	    		
	    		String StringGp=Integer.toString(intGp) ;
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updPtMSlabCommReTX", new Class[] { JDTORecord.class, String.class }, new Object[] { inRec ,StringGp});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.updPtMSlabCommTX(inRec, intGp);
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
	} // end of updPtMSlabComm
	
	/**
	 * 주편공통테이블 업데이트 처리
	 * @param inRec
	 * @param intGp
	 * @return
	 * @throws DAOException
	 */
	public int updPtMSlabCommTX(JDTORecord inRec, int intGp) throws DAOException {
		String szMethodName = "updPtMSlabCommTX";
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
			if (intGp == 1)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd2);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			if (intRtnVal <= 0) intRtnVal = -3;
		}catch(JDTOException e) {	
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	}
	
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
		if( intGp == 0 || intGp == 1 ) {
			szFieldName = "V_MSLAB_NO";						//주편번호
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.STRING_TYPE, 0, 0);
			if (!blnErr) return blnErr;
			//소재이송일시는 SYSDATE로 쿼리에서 직접처리함
			/*
			szFieldName = "V_MATL_FTMV_DT";					//소재이송일시
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			*/
		}
		return blnErr;
	}
}
