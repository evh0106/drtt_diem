package com.inisteel.cim.yd.common.util.tcconst;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.yd.common.util.YdUtils;


/**
 * YD (야드관리) 송신 용 전문 생성
 * @author YHWHman
 *
 */

public class MakeTcYD {
	
	// YDYDJ001		
	// YDYDJ002		
	// YDYDJ003		
	// YDYDJ004		
	// YDYDJ005		
	// YDYDJ006		
	// YDYDJ007		
	// YDYDJ008	
	
	
	
	
	
	/**
	 * YDYDJ000 : 야드관리 내부 송수신TC 공통 Converter
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeYDJ000(JDTORecord inRec, JDTORecordSet outRecSet){

		YdUtils ydUtils =new YdUtils();
		String [] szaKeys={""};
		String szTcCode="";
		String szOperationName = "야드관리 내부 송수신TC 공통";
		int nKeyCnt=0;
		
		JDTORecord outRec=null;

		//
		// Debug MSG
		System.out.println("수신 JDTORecord Data (msgRecord)");
		ydUtils.displayRecord(szOperationName, inRec);
		
		

		try{
			
			szTcCode =ydUtils.getTcCode(inRec);
			if( szTcCode==null || szTcCode.equals("")){
				return -2;
			}
			
			
			szaKeys =ydUtils.getRecKey(inRec);
			nKeyCnt =szaKeys.length;
			if( nKeyCnt<=0){
				// Key is Empty
				return -3;
			}
			
			outRec =JDTORecordFactory.getInstance().create();
			outRec.setField("JMS_TC_CD", 			new String(szTcCode) );
			outRec.setField("JMS_TC_CREATE_DDTT", 	new String(YdUtils.getCurDate("yyyyMMddHHmmss")) );
		
			for(int i=1;i<nKeyCnt;i++){
				//수신 -> 송신 item set
				outRec.setField(szaKeys[i], new String(inRec.getFieldString(szaKeys[i])) );
			}
			outRecSet.addRecord(outRec);
			

		}catch(Exception e){
			System.out.println("makeYDJ000() Exception Error "+e.getLocalizedMessage());
			return -1;
		}	

				
		return outRecSet.size();
		
		
	} // end of makeYDJ001()	

	
	
  //---------------------------------------------------------------------------	
} // end of class MakeTcYD
