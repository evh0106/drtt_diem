/**
 * @(#)CoilDAO
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      박판열연 COIL 야드  DAO
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.yf.acoilBak.dao;

import java.util.Iterator;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;

import com.inisteel.cim.common.dao.DBAssistantDAO;

public class ACoilDAO extends DBAssistantDAO
{
	/**
	 *      [A] 오퍼레이션명 : conversionFieldname 
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         int intGp             // 구분(0:"V_" 추가, 1:"V_" 제거
	 * @return JDTORecord			 // 필드명을 변환한 결과레코드
	 * @throws JDTOException 
	 */
	public JDTORecord conversionFieldname(JDTORecord recPara, int intGp) throws JDTOException 
	{
		JDTORecord recRtnVal = JDTORecordFactory.getInstance().create();
		String szFieldName = null;
		Iterator itrFieldName = null;
		
		itrFieldName = recPara.iterateName();	//필드명을 가져온다.
		
		//필드명 갯수만큼 루프를 돈다.
		while(itrFieldName.hasNext()) 
		{
			szFieldName = (String)itrFieldName.next();
			
			if (intGp == 0) 
			{
				recRtnVal.setField("V_" + szFieldName, recPara.getField(szFieldName));			//"V_" 추가
			} 
			else
			{
				recRtnVal.setField(szFieldName.substring(2), recPara.getField(szFieldName));	//"V_" 제거
			}
		}
		
		return recRtnVal ;
	}

		
}
