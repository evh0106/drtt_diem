/**
 * @(#)CCoilDAO
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2019/05/02
 *
 * @description      2열연 COIL 야드  DAO
 * ------------------------------------------------------------------------------
 * Ver.   수정일자     요청자  수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2019/05/02   정종균  이현진      최초 등록
 * 
 */
package com.inisteel.cim.ysPI.dao;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ys.common.dao.YsCommDAO;

// package com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO 참고
public class YsPiDAO extends CommonDAO {
    
	private YsCommDAO commDao = new YsCommDAO();
	
   	public int requestupdateData(String queryCode, Object[] objs) throws DAOException{	   	 	
   		return super.updateData(queryCode,objs);
   	}	
	
	
}
