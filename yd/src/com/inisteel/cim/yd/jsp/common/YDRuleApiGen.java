package com.inisteel.cim.yd.jsp.common;

import java.util.Hashtable;
import java.util.Vector;

import com.metis.rapi4j.RAPI4J;
import com.metis.rapi4j.ResultData;
import com.metis.rapi4j.RuleException;

public class YDRuleApiGen {

	/**
	 *
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2009-06-13 14:48:33)
	 * @param	item1	야드BookOut위치
	 * @param	item2	야드적치열구분
	 * @param	item3	야드적치Bed번호
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB699[0] :야드적치열구분
	 *			<li>YDB699[1] :야드적치Bed번호
	 *			<li>YDB699_ColCnt :2
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean YDB699(Hashtable table,
	                        String item1, // 야드BookOut위치
	                        String item2, // 야드적치열구분
	                        String item3 // 야드적치Bed번호
	                      ) throws RuleException {     
	        Vector vt = new Vector();
	        int rc = 0;

	        RAPI4J  RCaller=null;
	        try {
	           RCaller = new RAPI4J( false,  "" );
	            RCaller.Initialize("YDB699");
	            /* 사용자 입력값 설정 시작 */ 
	            RCaller.AddItemCount(1); 
	            RCaller.AddItemString( item1);
	            RCaller.AddItemCount(1); 
	            RCaller.AddItemString( item2);
	            RCaller.AddItemCount(1); 
	            RCaller.AddItemString( item3);
	            /* 사용자 입력값 설정  */ 
	            if (!RCaller.MBRS_Call(2)){                                                    						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	            }																		 									
	            byte resColTypes[] = new byte[RCaller.getColCount()];                     
	            for (int j = 0; j < RCaller.getColCount(); j++) {                            				
	                resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
	            }                     																						
	            ResultData    result=new ResultData();	
	            result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
	            for (int i = 0; i < RCaller.getRowCount(); i++) { 
	                //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드적치열구분
	                result.add( i ,RCaller.ReadString() );	//야드적치열구분
	                //System.out.println("  ROW[" + i + "] COL[2]:"+ RCaller.ReadString());	//야드적치Bed번호
	                result.add( i ,RCaller.ReadString() );	//야드적치Bed번호
	            } 
	            table.put("YDB699_ColCnt", new Integer(resColTypes.length));					
	            table.put("YDB699", result);																					
	            if (result.size() == 0) {																								
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	         																																
	    };    


}
