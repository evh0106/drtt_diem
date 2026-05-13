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
package com.inisteel.cim.ydPI.dao;

import java.util.Iterator;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.record.JDTOException;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.ccommon.util.CConstant;

import com.inisteel.cim.ydPI.common.util.PIYdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;


public class YdPiDAO extends DBAssistantDAO {
	
	private PIYdUtils     commPiUtils = new PIYdUtils();
	private YdPICommDAO   commPiDao   = new YdPICommDAO();
	

}
