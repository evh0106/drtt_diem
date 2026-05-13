package com.inisteel.cim.yd.jsp.common;

import java.util.Hashtable;

import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.ExceptionMessageUtil;
import com.metis.rapi4j.ResultData;

public class YDRuleBean {
	/**
	 * 로그서비스 선언
	 */
	private Logger logger = new Logger("yd");

	private Hashtable retTable = new Hashtable();
	private YDRuleApiGen ydRuleApiGen = new YDRuleApiGen();

	/**
	 * 
	 *     [A] 오퍼레이션명 : BOOK OUT 위치 변환 
	 * <BR>[B] 처리 개요 :  북아웃 코드 위치를 야드 저장위치 정보로 변경한다.
	 * .
	 * <BR>[C] 의사코드 (pseudo code)
	 * <BR>1.2 종료 및 return 없음 
	 * <BR> 
	 * <BR>[D] 참고사항
	 * @param JDTORecord
	 *<li>YD_BOOK_OUT_LOC		-북아웃 위치		 조건
	 * @return JDTORecord
	 *<li>YD_STK_COL_GP	-적치열 구분	 결과
	 *<li>YD_STK_BED_NO	-적치BED 번호 결과  
	 * @exception
	 * @modelguid XDE가 생성한 ID를 위치시킨다.
	 */
	public JDTORecord getRuleYD699(JDTORecord jRecordParam) throws DAOException {
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();

		try{

			String sRtnCol [] = new String[] {
				
					 "YD_STK_COL_GP"			//METHODE_명
					,"YD_STK_BED_NO"			//상세내용
	
			};
			

			String sJMS_TC_CD = jRecordParam.getFieldString("YD_BOOK_OUT_LOC");
			String YD_STK_COL_GP = "";
			String YD_STK_BED_NO = "";
		
			ydRuleApiGen.YDB699(retTable, sJMS_TC_CD, YD_STK_COL_GP, YD_STK_BED_NO);
			
			jrReturn= this.convToJDTORecord("YDB699", sRtnCol, retTable);
			
		
		} catch(Exception e) {
			logger.println(LogLevel.ERROR, this, ExceptionMessageUtil.getStackTrace(e)) ;
			throw new DAOException(e); 
		}
		
		return jrReturn;
		
	}
	
	
	public JDTORecord convToJDTORecord(String sRullID, String sRtnCol[],  Hashtable srcTable ) throws DAOException {
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();

		try{

			
			ResultData rData = (ResultData)srcTable.get(sRullID);
			
			for (int jj = 0 ; jj < rData.getColumnCount(); jj++) {
				
				if (jj > sRtnCol.length ) {
					jrReturn.setField(jj + "" , rData.get(0, jj));
				} else {
					jrReturn.setField(sRtnCol[jj], rData.get(0, jj));
				}
			}
			
			if ( rData.size() > 0) {
				jrReturn.setResultCode("SUCCESS") ; 
			} else {
				jrReturn.setResultCode("FAILURE") ;
				jrReturn.setResultMsg((String)srcTable.get("CHECK_VAL")) ; 
			}
			
			return jrReturn;
		
		} catch(Exception e) {
			logger.println(LogLevel.ERROR, this, ExceptionMessageUtil.getStackTrace(e)) ;
			throw new DAOException(e); 
			
		}
		
		
	}

	public JDTORecordSet convToJDTORecordSet(String sRullID, String sRtnCol[],  Hashtable srcTable ) throws DAOException {
		JDTORecordSet jrSetReturn = JDTORecordFactory.getInstance().createRecordSet("");

		try{

			
			ResultData rData = (ResultData)srcTable.get(sRullID);
			
			for (int ii = 0 ; ii < rData.getRowCount(); ii ++ ) {
				JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
				for (int jj = 0 ; jj < rData.getColumnCount(); jj++) {
					
					if (jj > sRtnCol.length ) {
						jrReturn.setField(jj + "" , rData.get(ii, jj));
					} else {
						jrReturn.setField(sRtnCol[jj], rData.get(ii, jj));
						
					}
					
				}
				jrSetReturn.addRecord(jrReturn);
			}
		
		} catch(Exception e) {
			logger.println(LogLevel.ERROR, this, ExceptionMessageUtil.getStackTrace(e)) ;
			throw new DAOException(e); 
			
		}
		
		return jrSetReturn;
		
		
	}
	
}
