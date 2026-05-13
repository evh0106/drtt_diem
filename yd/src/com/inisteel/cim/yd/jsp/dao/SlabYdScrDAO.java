/**
 * @(#)SlabYdScrDAO
 *
 * @version          V1.00
 * @author           허철호
 * @date             2012/11/22
 *
 * @description      Slab야드 화면 처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2012/11/22   허철호      허철호      최초 등록
 */
package com.inisteel.cim.yd.jsp.dao;

import xlib.cmc.GridData;   
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.yd.common.util.YdSlabUtils;

public class SlabYdScrDAO extends DBAssistantDAO {

	private YdSlabUtils slabUtils = new YdSlabUtils();
	
	/**
	 *      [A] 오퍼레이션명 :  스카핑 픽업 모니터링 정보 조회
	 *      
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getPickUpScarfMonitor(GridData gdReq) throws DAOException {
		String methodNm = "스카핑 픽업 모니터링 정보 조회[SlabYdScrDAO.getPickUpScarfMonitor] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String jspeed_query_id = "";
		Object[] param = null; 
		
		try {
			jspeed_query_id = "com.inisteel.cim.yd.jsp.dao.SlabYdScrDAO.getScafPickUpBed";
			
			param = new Object[] {
				slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드적치열구분 
			   ,slabUtils.trim(gdReq.getParam("V_YD_STK_BED_NO")) //야드적치Bed구분 
			};
			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
}
