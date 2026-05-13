/**
 * @(#)YdCommDAO
 *
 * @version          V1.00
 * @author           허철호
 * @date             2012/11/22
 * 
 * @description      야드관리 공통 DAO
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2012/11/22   허철호      허철호      최초 등록
 */
package com.inisteel.cim.yd.common.dao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
/**
 * [A] 클래스명 : 야드관리 공통 DAO
 *
 */
public class YdCommDAO extends DBAssistantDAO {
	private YdSlabUtils slabUtils = new YdSlabUtils();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	private CCommUtils	   commUtils   = new CCommUtils();	
	
	/***************************************************************************
	 * Interface 처리 공통
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 수신된 전문의 정보를 조회
	 *      
	 *      @param String msgID : 수신된 전문의 MSG_ID te 
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMsgInfo(String msgID) throws DAOException {
		try {
			return getRecordSet("com.inisteel.cim.yd.common.dao.YdCommDAO.getMsgInfo", new Object[] { msgID });
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : Jsp 화면용 SELECT 메소드 
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         String        queryId    QueryId 
	 *         String        logId   	 
	 *         String        mthdNm   	 
	 * @return int           result size
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int jspSelect(JDTORecord inRec, JDTORecordSet outRecSet, String queryId, String mthdNm) throws DAOException, JDTOException {
		
		String 		methodNm = "조회[YmCommDAO.jspSelect] < " + mthdNm;
		String      szMsg        = "";
		
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		YdUtils ydUtils = new YdUtils();
		
		try {
			
//PIDEV
			//PIDEV_S :병행가동용:PI_YD
//			String sPI_YD     = commUtils.nvl(inRec.getFieldString("PI_YD"), "*");				
//			queryId = ydPICommDAO.getYdRulePI("", mthdNm, "YD0001", queryId, "APPPI0", sPI_YD, "*" );		

			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			rsTemp = getRecordSet(recPara);
			
			szMsg = "조회[YmCommDAO.jspSelect] 결과 건수: " + rsTemp.size();
			ydUtils.putLog(getClass().getName(), methodNm, szMsg, YdConstant.INFO);
			
			if (rsTemp.size() > 0) {
				outRecSet.addAll(rsTemp);
			} else {
				return 0;
			}
			
		} catch (Exception e) {
			
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		return outRecSet.size();
	}
	
	
	/**
	 * 오퍼레이션명 : WebMethod 사용 여부 
	 * @return String : 사용여부 Y:WebMethod 사용 ,N:사용안함
	 */
	public String getWebMothodYn() throws DAOException {
		
		String sFlagYn = "N"; 
		
		try {
			
			JDTORecordSet jsRst = getRecordSet("com.inisteel.cim.yd.bcommon.dao.YdCommDAO.getWebMethodYn", null);
			
			if (jsRst.size() > 0) {
				sFlagYn = slabUtils.trim(jsRst.getRecord(0).getFieldString("WEB_METHOD_YN")); //WebMethod 사용 여부
			}
			
		} catch (Exception e) {
			
			return sFlagYn;
		}
			
		return sFlagYn;
	}
	
	/**
	 * 오퍼레이션명 : 생산통제 신규 Queue 사용 여부 
	 * @return String : 사용여부 Y:CTD_MDB_QUEUE 사용 ,N:CT_MDB_QUEUE 사용
	 */
	public String getUsingCTNewQYn() throws DAOException {
		
		String sFlagYn = "N"; 
		
		try {
			
			JDTORecordSet jsRst = getRecordSet("com.inisteel.cim.yd.bcommon.dao.YdCommDAO.getUsingCTNewQYn", null);
			
			if (jsRst.size() > 0) {
				sFlagYn = slabUtils.trim(jsRst.getRecord(0).getFieldString("USING_CT_NEW_Q_YN")); //신규 큐 사용 여부
			}
			
		} catch (Exception e) {
			
			return sFlagYn;
		}
			
		return sFlagYn;
	}

}
