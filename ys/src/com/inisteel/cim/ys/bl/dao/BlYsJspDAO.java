/**
 * @(#)BlYsJspDAO
 *
 * @version          V1.00
 * @author           조병기
 * @date             2014/12/22
 *
 * @description      BLOOM 야드 화면 처리 DAO
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2014/12/22   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.bl.dao;

import java.util.Iterator;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;

public class BlYsJspDAO extends DBAssistantDAO {



	/**
	 *      [A] 오퍼레이션명 : conversionFieldname 
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         int intGp             // 구분(0:"V_" 추가, 1:"V_" 제거
	 * @return JDTORecord			 // 필드명을 변환한 결과레코드
	 * @throws JDTOException 
	 */
	public JDTORecord conversionFieldname(JDTORecord recPara, int intGp) throws JDTOException {
		JDTORecord recRtnVal = JDTORecordFactory.getInstance().create();
		String szFieldName = null;
		Iterator itrFieldName = null;
		
		//필드명을 가져온다.
		itrFieldName = recPara.iterateName();
		
		//필드명 갯수만큼 루프를 돈다.
		while(itrFieldName.hasNext()) {
			
			szFieldName = (String)itrFieldName.next();
			//"V_" 추가
			if (intGp == 0) {
				recRtnVal.setField("V_" + szFieldName, recPara.getField(szFieldName));
			//"V_" 제거
			} else {
				recRtnVal.setField(szFieldName.substring(2), recPara.getField(szFieldName));
			}
		}
		
		return recRtnVal ;
	}
	/**
	 *      [A] 오퍼레이션명 : SELECT 메소드
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         String        queryId    QueryId 
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public JDTORecordSet select2(JDTORecord inRec, String queryId) throws DAOException, JDTOException {
		
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
		
		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			rsTemp = getRecordSet(recPara);
			
		} catch (Exception e) {
			
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return rsTemp;
	}
	

}