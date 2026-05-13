package com.inisteel.cim.yd.common.rule;


import java.util.Hashtable;
import java.util.Vector;

import com.metis.rapi4j.RAPI4J;
import com.metis.rapi4j.ResultData;
import com.metis.rapi4j.RuleException;

import com.metis.rapi5j.*; 
import java.util.*; 

public class BRERule6 {

	
	/**
	 * 기준ID : YDB698
	 * 기준명 : 후판제품야드-차량LOT편성자동유무관리
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2009-08-26 12:59:59)
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB698[0] :사용유무
	 *			<li>YDB698_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
    public boolean YDB698(Hashtable table) throws RuleException {     
        Vector vt = new Vector();
        int rc = 0;

        RAPI4J  RCaller=null;
        try {
           RCaller = new RAPI4J( false,  "" );
            RCaller.Initialize("YDB698");
            /* 사용자 입력값 설정 시작 */ 
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
                //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//사용유무
                result.add( i ,RCaller.ReadString() );	//사용유무
            } 
            table.put("YDB698_ColCnt", new Integer(resColTypes.length));					
            table.put("YDB698", result);																					
            if (result.size() == 0) {																								
                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
            }																															
            return true;																										
        } catch (Exception e) {																						
               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
        }																																						
         																																
    }			//end of YDB698

    /**
	* 기준ID : YDB671
	* 기준명 : 후판제품-SPAN별 야드구분 변환기준
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2012-10-19 09:43:12)
	 * @param	item1	야드동구분
	 * @param	item2	야드설비구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB671[0] :야드구분
	 *			<li>YDB671[1] :야드적치열폭구분
	 *			<li>YDB671[2] :거리
	 *			<li>YDB671_ColCnt :3
	 *		<ul>
	 * @return 정상처리 여부
	 */
    public boolean YDB671(Hashtable table,
                        String item1, // 야드동구분
                        String item2 // 야드설비구분
                      ) throws RuleException {     
        Vector vt = new Vector();
        int rc = 0;

        RAPI4J  RCaller=null;
        try {
           RCaller = new RAPI4J( false,  "" );
            RCaller.Initialize("YDB671");
            /* 사용자 입력값 설정 시작 */ 
            RCaller.AddItemCount(1); 
            RCaller.AddItemString( item1);
            RCaller.AddItemCount(1); 
            RCaller.AddItemString( item2);
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
                //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드구분
                result.add( i ,RCaller.ReadString() );	//야드구분
                //System.out.println("  ROW[" + i + "] COL[2]:"+ RCaller.ReadString());	//야드적치열폭구분
                result.add( i ,RCaller.ReadString() );	//야드적치열폭구분
                //System.out.println("  ROW[" + i + "] COL[3]:"+ RCaller.ReadInt() );	//거리
                result.add(  i ,new Integer(RCaller.ReadInt()) );	//거리
            } 
            
            table.put("YDB671_ColCnt", new Integer(resColTypes.length));					
            table.put("YDB671", result);																					
            if (result.size() == 0) {																								
                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
            }																															
            return true;																										
        } catch (Exception e) {																						
               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
        }																																						
         																																
    }   
    
	/**
	* 기준ID : YDB672
	* 기준명 : 후판제품야드 제품 폭 MIN MAX 값
	**import com.metis.rapi4j.*; 
	**import java.util.*; 
	* item코드허용값 :
	* @작성 날짜: (2012-10-23 09:15:53)
	* @param	item1	야드구분
	* @param	item2	야드적치Bed폭구분
	* @param	table 인수값 혹은 결과값(리턴정보)
	*		<ul>
	*			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	*			<li>YDB672[0] :Min값
	*			<li>YDB672[1] :Max값
	*			<li>YDB672[2] :야드적치열높이Max
	*			<li>YDB672_ColCnt :3
	*		<ul>
	* @return 정상처리 여부
	*/
   public boolean YDB672(Hashtable table,
                       String item1, // 야드구분
                       String item2 // 야드적치Bed폭구분
                     ) throws RuleException {     
       Vector vt = new Vector();
       int rc = 0;

       RAPI4J  RCaller=null;
       try {
          RCaller = new RAPI4J( false,  "" );
           RCaller.Initialize("YDB672");
           /* 사용자 입력값 설정 시작 */ 
           RCaller.AddItemCount(1); 
           RCaller.AddItemString( item1);
           RCaller.AddItemCount(1); 
           RCaller.AddItemString( item2);
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
               //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadFraction();	//Min값
               result.add( i , new Double(RCaller.ReadFraction()) );	//Min값
               //System.out.println("  ROW[" + i + "] COL[2]:"+ RCaller.ReadFraction();	//Max값
               result.add( i , new Double(RCaller.ReadFraction()) );	//Max값
               //System.out.println("  ROW[" + i + "] COL[3]:"+ RCaller.ReadInt() );	//야드적치열높이Max
               result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드적치열높이Max
           } 
           table.put("YDB672_ColCnt", new Integer(resColTypes.length));					
           table.put("YDB672", result);																					
           if (result.size() == 0) {																								
               table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
           }																															
           return true;																										
       } catch (Exception e) {																						
              	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
       }																																						
        																																
   }
    
    
   /**
	* 기준ID : YDB673
	* 기준명 : 후판제품야드 제품 길이 MIN, MAX 기준
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2012-10-23 10:32:08)
	 * @param	item1	야드구분
	 * @param	item2	야드적치Bed길이구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB673[0] :Min값
	 *			<li>YDB673[1] :Max값
	 *			<li>YDB673[2] :거리
	 *			<li>YDB673_ColCnt :3
	 *		<ul>
	 * @return 정상처리 여부
	 */
    public boolean YDB673(Hashtable table,
                        String item1, // 야드구분
                        String item2 // 야드적치Bed길이구분
                      ) throws RuleException {     
        Vector vt = new Vector();
        int rc = 0;

        RAPI4J  RCaller=null;
        try {
           RCaller = new RAPI4J( false,  "" );
            RCaller.Initialize("YDB673");
            /* 사용자 입력값 설정 시작 */ 
            RCaller.AddItemCount(1); 
            RCaller.AddItemString( item1);
            RCaller.AddItemCount(1); 
            RCaller.AddItemString( item2);
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
                //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadFraction();	//Min값
                result.add( i , new Double(RCaller.ReadFraction()) );	//Min값
                //System.out.println("  ROW[" + i + "] COL[2]:"+ RCaller.ReadFraction();	//Max값
                result.add( i , new Double(RCaller.ReadFraction()) );	//Max값
                //System.out.println("  ROW[" + i + "] COL[3]:"+ RCaller.ReadInt() );	//거리
                result.add(  i ,new Integer(RCaller.ReadInt()) );	//거리
            } 
            table.put("YDB673_ColCnt", new Integer(resColTypes.length));					
            table.put("YDB673", result);																					
            if (result.size() == 0) {																								
                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
            }																															
            return true;																										
        } catch (Exception e) {																						
               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
        }																																						
         																																
    }   
    
    
    /**
	* 기준ID : YDB670
	* 기준명 : 3기 기능 적용여부 (테스트용)
    **import com.metis.rapi4j.*; 
    **import java.util.*; 
    * item코드허용값 :
    * @작성 날짜: (2013-02-05 13:09:04)
    * @param	item1	Segment1 기능ID
    * @param	item2	Segment2 기능설명
    * @param	table 인수값 혹은 결과값(리턴정보)
    *		<ul>
    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
    *			<li>YDB670[0] :사용여부
    *			<li>YDB670_ColCnt :1
    *		<ul>
    * @return 정상처리 여부
    */
   public boolean YDB670(Hashtable table,
                       String item1, // 기능ID
                       String item2 // 기능설명 (사용하지 않음, 입력값 없어도 됨)
                     ) throws RuleException {     
       Vector vt = new Vector();
       int rc = 0;

       RAPI4J  RCaller=null;
       try {
          RCaller = new RAPI4J( false,  "" );
           RCaller.Initialize("YDB670");
           /* 사용자 입력값 설정 시작 */ 
           RCaller.AddItemCount(1); 
           RCaller.AddItemString( item1);
           RCaller.AddItemCount(1); 
           RCaller.AddItemString( item2);
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
               //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//사용여부
               result.add( i ,RCaller.ReadString() );	//사용여부
           } 
           table.put("YDB670_ColCnt", new Integer(resColTypes.length));					
           table.put("YDB670", result);																					
           if (result.size() == 0) {																								
               table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
           }																															
           return true;																										
       } catch (Exception e) {																						
              	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
       }																																						
        																																
   };    
   
   /**
   *
   **import com.metis.rapi4j.*; 
   **import java.util.*; 
   * item코드허용값 :
   * @작성 날짜: (2013-05-04 16:37:42)
   * @param	item1	야드구분
   * @param	item2	야드동구분
   * @param	item3	야드적치Bed길이구분
   * @param	table 인수값 혹은 결과값(리턴정보)
   *		<ul>
   *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
   *			<li>YDB674[0] :후판북아웃위치
   *			<li>YDB674_ColCnt :1
   *		<ul>
   * @return 정상처리 여부
   */
  public boolean YDB674(Hashtable table,
                      String item1, // 야드구분
                      String item2, // 야드동구분
                      String item3 // 야드적치Bed길이구분
                    ) throws RuleException {     
      Vector vt = new Vector();
      int rc = 0;

      RAPI4J  RCaller=null;
      try {
         RCaller = new RAPI4J( false,  "" );
          RCaller.Initialize("YDB674");
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
              //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadInt() );	//후판북아웃위치
              result.add(  i ,new Integer(RCaller.ReadInt()) );	//후판북아웃위치
          } 
          table.put("YDB674_ColCnt", new Integer(resColTypes.length));					
          table.put("YDB674", result);																					
          if (result.size() == 0) {																								
              table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
          }																															
          return true;																										
      } catch (Exception e) {																						
             	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
      }																																						
       																																
  };    
  
  
  /**
  *
  **import com.metis.rapi4j.*; 
  **import java.util.*; 
  * item코드허용값 :
  * @작성 날짜: (2013-06-14 17:05:12)
  * @param	item1	Segment1
  * @param	table 인수값 혹은 결과값(리턴정보)
  *		<ul>
  *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
  *			<li>YDB675[0] :야드크레인Grab개수
  *			<li>YDB675[1] :야드크레인Grab1Beam최소길이
  *			<li>YDB675[2] :야드크레인Grab1Beam신축길이
  *			<li>YDB675[3] :야드크레인Grab2Beam최소길이
  *			<li>YDB675[4] :야드크레인Grab2Beam신축길이
  *			<li>YDB675[5] :야드크레인Beam최외각Magnet간격
  *			<li>YDB675[6] :야드적치BedX축허용오차
  *			<li>YDB675[7] :야드적치BedY축허용오차
  *			<li>YDB675[8] :야드차량작업X축허용오차
  *			<li>YDB675[9] :야드차량작업Y축허용오차
  *			<li>YDB675[10]:야드설비작업X축허용오차
  *			<li>YDB675[11]:야드설비작업Y축허용오차
  *			<li>YDB675_ColCnt :12
  *		<ul>
  * @return 정상처리 여부
  */
     public boolean YDB675(Hashtable table,
                         String item1 // 크래인 설비 코드
                       ) throws RuleException {     
         Vector vt = new Vector();
         int rc = 0;

         RAPI4J  RCaller=null;
         try {
            RCaller = new RAPI4J( false,  "" );
             RCaller.Initialize("YDB675");
             /* 사용자 입력값 설정 시작 */ 
             RCaller.AddItemCount(1); 
             RCaller.AddItemString( item1);
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
                 //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadInt() );	//야드크레인Grab개수
                 result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드크레인Grab개수
                 //System.out.println("  ROW[" + i + "] COL[2]:"+ RCaller.ReadInt() );	//야드크레인Grab1Beam최소길이
                 result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드크레인Grab1Beam최소길이
                 //System.out.println("  ROW[" + i + "] COL[3]:"+ RCaller.ReadInt() );	//야드크레인Grab1Beam신축길이
                 result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드크레인Grab1Beam신축길이
                 //System.out.println("  ROW[" + i + "] COL[4]:"+ RCaller.ReadInt() );	//야드크레인Grab2Beam최소길이
                 result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드크레인Grab2Beam최소길이
                 //System.out.println("  ROW[" + i + "] COL[5]:"+ RCaller.ReadInt() );	//야드크레인Grab2Beam신축길이
                 result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드크레인Grab2Beam신축길이
                 //System.out.println("  ROW[" + i + "] COL[6]:"+ RCaller.ReadInt() );	//야드크레인Beam최외각Magnet간격
                 result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드크레인Beam최외각Magnet간격
                 //System.out.println("  ROW[" + i + "] COL[7]:"+ RCaller.ReadInt() );	//야드적치BedX축허용오차
                 result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드적치BedX축허용오차
                 //System.out.println("  ROW[" + i + "] COL[8]:"+ RCaller.ReadInt() );	//야드적치BedY축허용오차
                 result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드적치BedY축허용오차
                 //System.out.println("  ROW[" + i + "] COL[9]:"+ RCaller.ReadInt() );	//야드차량작업X축허용오차
                 result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드차량작업X축허용오차
                 //System.out.println("  ROW[" + i + "] COL[10]:"+ RCaller.ReadInt() );	//야드차량착업Y축허용오차
                 result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드차량착업Y축허용오차
                 //System.out.println("  ROW[" + i + "] COL[11]:"+ RCaller.ReadInt() );	//야드설비작업X축허용오차
                 result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드설비작업X축허용오차
                 //System.out.println("  ROW[" + i + "] COL[12]:"+ RCaller.ReadInt() );	//야드설비작업Y축허용오차
                 result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드설비작업Y축허용오차
             } 
             table.put("YDB675_ColCnt", new Integer(resColTypes.length));					
             table.put("YDB675", result);																					
             if (result.size() == 0) {																								
                 table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
             }																															
             return true;																										
         } catch (Exception e) {																						
                	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
         }																																						
          																																
     };    
  
     /**
     *
     import com.metis.rapi5j.*; 
     import java.util.*; 
     * item코드허용값 :
     * @작성 날짜: (2022-03-17 10:03:16)
     * @param	item1	Segment1
     * @param	item2	Segment2
     * @param	table 인수값 혹은 결과값(리턴정보)
     *		<ul>
     *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
     *			<li>YDB670[0] :사용여부
     *			<li>YDB670_ColCnt :1
     *		<ul>
     * @return 정상처리 여부
     */
        public boolean YDB670_NEW(Hashtable table,
                            String item1, // Segment1
                            String item2 // Segment2
                          ) throws com.metis.rapi5j.RuleException {     
            Vector vt = new Vector();
            int rc = 0;

            RAPI5J  RCaller=new RAPI5J() ;
            RCaller.Initialize("YDB670");
            /* 사용자 입력값 설정 시작 */ 
            RCaller.AddItemCount(1); 
            RCaller.AddItemString( item1);
            RCaller.AddItemCount(1); 
            RCaller.AddItemString( item2);
            /* 사용자 입력값 설정  */ 
           try{                                                    						
                RCaller.MBRS_Run();                                                      
                ResultData    result=new ResultData();	
                result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
                for (int i = 0; i < RCaller.getRowCount(); i++) { 
                    result.add( i , RCaller.ReadString() );	//사용여부
                } 
                table.put("YDB670_ColCnt", new Integer( RCaller.getColCount() ));					
                table.put("YDB670", result);																					
                if (result.size() == 0) {
                    table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");
                }																															
                return true;																										
            } catch (Exception e) {																						
                 throw new com.metis.rapi5j.RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
            }																																						
        }     
        
        /**
        *
        import com.metis.rapi5j.*; 
        import java.util.*; 
        * item코드허용값 :
        * @작성 날짜: (2022-03-17 10:04:57)
        * @param	item1	야드동구분
        * @param	item2	야드설비구분
        * @param	table 인수값 혹은 결과값(리턴정보)
        *		<ul>
        *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
        *			<li>YDB671[0] :야드구분
        *			<li>YDB671[1] :야드적치열폭구분
        *			<li>YDB671[2] :거리
        *			<li>YDB671_ColCnt :3
        *		<ul>
        * @return 정상처리 여부
        */
           public boolean YDB671_NEW(Hashtable table,
                               String item1, // 야드동구분
                               String item2 // 야드설비구분
                             ) throws com.metis.rapi5j.RuleException {     
               Vector vt = new Vector();
               int rc = 0;

               RAPI5J  RCaller=new RAPI5J() ;
               RCaller.Initialize("YDB671");
               /* 사용자 입력값 설정 시작 */ 
               RCaller.AddItemCount(1); 
               RCaller.AddItemString( item1);
               RCaller.AddItemCount(1); 
               RCaller.AddItemString( item2);
               /* 사용자 입력값 설정  */ 
              try{                                                    						
                   RCaller.MBRS_Run();                                                      
                   ResultData    result=new ResultData();	
                   result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
                   for (int i = 0; i < RCaller.getRowCount(); i++) { 
                       result.add( i , RCaller.ReadString() );	//야드구분
                       result.add( i , RCaller.ReadString() );	//야드적치열폭구분
                       result.add(  i ,new Integer(RCaller.ReadInt()) );	//거리
                   } 
                   table.put("YDB671_ColCnt", new Integer( RCaller.getColCount() ));					
                   table.put("YDB671", result);																					
                   if (result.size() == 0) {
                       table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");
                   }																															
                   return true;																										
               } catch (Exception e) {																						
                    throw new com.metis.rapi5j.RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
               }																																						
           }     
           
           /**
           *
           import com.metis.rapi5j.*; 
           import java.util.*; 
           * item코드허용값 :
           * @작성 날짜: (2022-03-17 10:05:33)
           * @param	item1	야드구분
           * @param	item2	야드적치Bed폭구분
           * @param	table 인수값 혹은 결과값(리턴정보)
           *		<ul>
           *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
           *			<li>YDB672[0] :Min값
           *			<li>YDB672[1] :Max값
           *			<li>YDB672[2] :야드적치열높이Max
           *			<li>YDB672_ColCnt :3
           *		<ul>
           * @return 정상처리 여부
           */
              public boolean YDB672_NEW(Hashtable table,
                                  String item1, // 야드구분
                                  String item2 // 야드적치Bed폭구분
                                ) throws com.metis.rapi5j.RuleException {     
                  Vector vt = new Vector();
                  int rc = 0;

                  RAPI5J  RCaller=new RAPI5J() ;
                  RCaller.Initialize("YDB672");
                  /* 사용자 입력값 설정 시작 */ 
                  RCaller.AddItemCount(1); 
                  RCaller.AddItemString( item1);
                  RCaller.AddItemCount(1); 
                  RCaller.AddItemString( item2);
                  /* 사용자 입력값 설정  */ 
                 try{                                                    						
                      RCaller.MBRS_Run();                                                      
                      ResultData    result=new ResultData();	
                      result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
                      for (int i = 0; i < RCaller.getRowCount(); i++) { 
                          result.add( i , new Double(RCaller.ReadFraction()) );	//Min값
                          result.add( i , new Double(RCaller.ReadFraction()) );	//Max값
                          result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드적치열높이Max
                      } 
                      table.put("YDB672_ColCnt", new Integer( RCaller.getColCount() ));					
                      table.put("YDB672", result);																					
                      if (result.size() == 0) {
                          table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");
                      }																															
                      return true;																										
                  } catch (Exception e) {																						
                       throw new com.metis.rapi5j.RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
                  }																																						
              }     
              
              /**
              *
              import com.metis.rapi5j.*; 
              import java.util.*; 
              * item코드허용값 :
              * @작성 날짜: (2022-03-17 10:06:05)
              * @param	item1	야드구분
              * @param	item2	야드적치Bed길이구분
              * @param	table 인수값 혹은 결과값(리턴정보)
              *		<ul>
              *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
              *			<li>YDB673[0] :Min값
              *			<li>YDB673[1] :Max값
              *			<li>YDB673[2] :거리
              *			<li>YDB673_ColCnt :3
              *		<ul>
              * @return 정상처리 여부
              */
                 public boolean YDB673_NEW(Hashtable table,
                                     String item1, // 야드구분
                                     String item2 // 야드적치Bed길이구분
                                   ) throws com.metis.rapi5j.RuleException {     
                     Vector vt = new Vector();
                     int rc = 0;

                     RAPI5J  RCaller=new RAPI5J() ;
                     RCaller.Initialize("YDB673");
                     /* 사용자 입력값 설정 시작 */ 
                     RCaller.AddItemCount(1); 
                     RCaller.AddItemString( item1);
                     RCaller.AddItemCount(1); 
                     RCaller.AddItemString( item2);
                     /* 사용자 입력값 설정  */ 
                    try{                                                    						
                         RCaller.MBRS_Run();                                                      
                         ResultData    result=new ResultData();	
                         result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
                         for (int i = 0; i < RCaller.getRowCount(); i++) { 
                             result.add( i , new Double(RCaller.ReadFraction()) );	//Min값
                             result.add( i , new Double(RCaller.ReadFraction()) );	//Max값
                             result.add(  i ,new Integer(RCaller.ReadInt()) );	//거리
                         } 
                         table.put("YDB673_ColCnt", new Integer( RCaller.getColCount() ));					
                         table.put("YDB673", result);																					
                         if (result.size() == 0) {
                             table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");
                         }																															
                         return true;																										
                     } catch (Exception e) {																						
                          throw new com.metis.rapi5j.RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
                     }																																						
                 }     

                 /**
                 *
                 import com.metis.rapi5j.*; 
                 import java.util.*; 
                 * item코드허용값 :
                 * @작성 날짜: (2022-05-12 13:39:23)
                 * @param	item1	야드구분
                 * @param	item2	야드동구분
                 * @param	item3	야드적치Bed번호
                 * @param	table 인수값 혹은 결과값(리턴정보)
                 *		<ul>
                 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
                 *			<li>YDB674[0] :후판북아웃위치
                 *			<li>YDB674_ColCnt :1
                 *		<ul>
                 * @return 정상처리 여부
                 */
                    public boolean YDB674_NEW(Hashtable table,
                                        String item1, // 야드구분
                                        String item2, // 야드동구분
                                        String item3 // 야드적치Bed번호
                                      ) throws RuleException {     
                        Vector vt = new Vector();
                        int rc = 0;

                        RAPI5J  RCaller=new RAPI5J() ;
                        RCaller.Initialize("YDB674");
                        /* 사용자 입력값 설정 시작 */ 
                        RCaller.AddItemCount(1); 
                        RCaller.AddItemString( item1);
                        RCaller.AddItemCount(1); 
                        RCaller.AddItemString( item2);
                        RCaller.AddItemCount(1); 
                        RCaller.AddItemString( item3);
                        /* 사용자 입력값 설정  */ 
                       try{                                                    						
                            RCaller.MBRS_Run();                                                      
                            ResultData    result=new ResultData();	
                            result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
                            for (int i = 0; i < RCaller.getRowCount(); i++) { 
                                result.add(  i ,new Integer(RCaller.ReadInt()) );	//후판북아웃위치
                            } 
                            table.put("YDB674_ColCnt", new Integer( RCaller.getColCount() ));					
                            table.put("YDB674", result);																					
                            if (result.size() == 0) {
                                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");
                            }																															
                            return true;																										
                        } catch (Exception e) {																						
                             throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
                        }																																						
                    }     

                 
                 /**
                 *
                 import com.metis.rapi5j.*; 
                 import java.util.*; 
                 * item코드허용값 :
                 * @작성 날짜: (2022-03-17 10:06:35)
                 * @param	item1	Segment1
                 * @param	table 인수값 혹은 결과값(리턴정보)
                 *		<ul>
                 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
                 *			<li>YDB675[0] :야드크레인Grab개수
                 *			<li>YDB675[1] :야드크레인Grab1Beam최소길이
                 *			<li>YDB675[2] :야드크레인Grab1Beam신축길이
                 *			<li>YDB675[3] :야드크레인Grab2Beam최소길이
                 *			<li>YDB675[4] :야드크레인Grab2Beam신축길이
                 *			<li>YDB675[5] :야드크레인Beam최외각Magnet간격
                 *			<li>YDB675[6] :야드적치BedX축허용오차
                 *			<li>YDB675[7] :야드적치BedY축허용오차
                 *			<li>YDB675[8] :야드차량작업X축허용오차
                 *			<li>YDB675[9] :야드차량착업Y축허용오차
                 *			<li>YDB675[10] :야드크레인기준X좌표
                 *			<li>YDB675[11] :야드크레인기준Y좌표
                 *			<li>YDB675_ColCnt :12
                 *		<ul>
                 * @return 정상처리 여부
                 */
                    public boolean YDB675_NEW(Hashtable table,
                                        String item1 // Segment1
                                      ) throws com.metis.rapi5j.RuleException {     
                        Vector vt = new Vector();
                        int rc = 0;

                        RAPI5J  RCaller=new RAPI5J() ;
                        RCaller.Initialize("YDB675");
                        /* 사용자 입력값 설정 시작 */ 
                        RCaller.AddItemCount(1); 
                        RCaller.AddItemString( item1);
                        /* 사용자 입력값 설정  */ 
                       try{                                                    						
                            RCaller.MBRS_Run();                                                      
                            ResultData    result=new ResultData();	
                            result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
                            for (int i = 0; i < RCaller.getRowCount(); i++) { 
                                result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드크레인Grab개수
                                result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드크레인Grab1Beam최소길이
                                result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드크레인Grab1Beam신축길이
                                result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드크레인Grab2Beam최소길이
                                result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드크레인Grab2Beam신축길이
                                result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드크레인Beam최외각Magnet간격
                                result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드적치BedX축허용오차
                                result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드적치BedY축허용오차
                                result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드차량작업X축허용오차
                                result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드차량착업Y축허용오차
                                result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드크레인기준X좌표
                                result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드크레인기준Y좌표
                            } 
                            table.put("YDB675_ColCnt", new Integer( RCaller.getColCount() ));					
                            table.put("YDB675", result);																					
                            if (result.size() == 0) {
                                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");
                            }																															
                            return true;																										
                        } catch (Exception e) {																						
                             throw new com.metis.rapi5j.RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
                        }																																						
                    }     

   
    
    
  //---------------------------------------------------------------------------
} // end of class
