/*
 * @(#) 2후판정정야드 에서 사용하는 BRE RULE
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/12/04
 *
 * @description		2후판정정야드 에서 사용하는 BRE RULE
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/12/04   김현우      김현우       최초작성
 */

package com.inisteel.cim.yd.common.rule;

import java.util.Hashtable;
//import java.util.Vector;

import com.metis.rapi4j.RAPI4J;
import com.metis.rapi4j.ResultData;
import com.metis.rapi4j.RuleException;

import com.metis.rapi5j.*; 
import java.util.*; 

public class BRERule8 {


	/**
	 * 기준ID : YDB801
	 * 기준명   : 후판정정야드-정정야드재료폭구분
	 **import com.metis.rapi4j.*;
	 **import java.util.*;
	 * item코드허용값 :
	 * @작성 날짜: (2012-12-04 08:27:12)
	 * @param	item1	야드구분
	 * @param	item2	야드재료폭
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB801[0] :야드재료폭구분
	 *			<li>YDB801_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
    public boolean YDB801(Hashtable table,
                        String item1, // 야드구분
                        double item2 // 야드재료폭
                      ) throws RuleException {
//        Vector vt = new Vector();
//        int rc = 0;

        RAPI4J  RCaller = null;
        try {
        	RCaller = new RAPI4J( false,  "" );
            RCaller.Initialize("YDB801");
            /* 사용자 입력값 설정 시작 */
            RCaller.AddItemCount(1);
            RCaller.AddItemString( item1);
            RCaller.AddItemCount(1);
            RCaller.AddItemFraction(item2);
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
                //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드재료폭구분
                result.add( i ,RCaller.ReadString() );	//야드재료폭구분
            }
            table.put("YDB801_ColCnt", new Integer(resColTypes.length));
            table.put("YDB801", result);
            if (result.size() == 0) {
                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");
            }
            return true;
        } catch (Exception e) {
            throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
        }
    }	//end of YDB801
    /**
    *
    import com.metis.rapi5j.*; 
    import java.util.*; 
    * item코드허용값 :
    * @작성 날짜: (2022-06-21 15:51:17)
    * @param	item1	야드구분
    * @param	item2	야드재료폭
    * @param	table 인수값 혹은 결과값(리턴정보)
    *		<ul>
    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
    *			<li>YDB801[0] :야드재료폭구분
    *			<li>YDB801_ColCnt :1
    *		<ul>
    * @return 정상처리 여부
    */
       public boolean YDB801_NEW(Hashtable table,
                           String item1, // 야드구분
                           double item2 // 야드재료폭
                         ) throws RuleException {     
           Vector vt = new Vector();
           int rc = 0;

           RAPI5J  RCaller=new RAPI5J() ;
           RCaller.Initialize("YDB801");
           /* 사용자 입력값 설정 시작 */ 
           RCaller.AddItemCount(1); 
           RCaller.AddItemString( item1);
           RCaller.AddItemCount(1); 
           RCaller.AddItemFraction(item2); 
           /* 사용자 입력값 설정  */ 
          try{                                                    						
               RCaller.MBRS_Run();                                                      
               ResultData    result=new ResultData();	
               result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
               for (int i = 0; i < RCaller.getRowCount(); i++) { 
                   result.add( i , RCaller.ReadString() );	//야드재료폭구분
               } 
               table.put("YDB801_ColCnt", new Integer( RCaller.getColCount() ));					
               table.put("YDB801", result);																					
               if (result.size() == 0) {
                   table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");
               }																															
               return true;																										
           } catch (Exception e) {																						
                throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
           }																																						
       }     

    
    /**
	 * 기준ID : YDB802
	 * 기준명   : 후판정정야드-정정야드재료길이구분
	 *import com.metis.rapi4j.*;
	 **import java.util.*;
	 * item코드허용값 :
	 * @작성 날짜: (2012-12-04 08:33:13)
	 * @param	item1	야드구분
	 * @param	item2	야드재료길이
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB802[0] :야드재료길이구분
	 *			<li>YDB802_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
    public boolean YDB802(Hashtable table,
    					String item1, // 야드구분
                        int item2 // 야드재료길이
                        ) throws RuleException {
//    	Vector vt = new Vector();
//      int rc = 0;

        RAPI4J  RCaller=null;
        try {
        	RCaller = new RAPI4J( false,  "" );
            RCaller.Initialize("YDB802");
            /* 사용자 입력값 설정 시작 */
            RCaller.AddItemCount(1);
            RCaller.AddItemString( item1);
            RCaller.AddItemCount(1);
            RCaller.AddItemInt(  item2);
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
            	//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드재료길이구분
            	result.add( i ,RCaller.ReadString() );	//야드재료길이구분
            }
            table.put("YDB802_ColCnt", new Integer(resColTypes.length));
            table.put("YDB802", result);
            if (result.size() == 0) {
            	table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");
            }
            return true;
        } catch (Exception e) {
        	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
        }

    }   // end of YDB802
    /**
    *
    import com.metis.rapi5j.*; 
    import java.util.*; 
    * item코드허용값 :
    * @작성 날짜: (2023-01-02 09:48:31)
    * @param	item1	야드구분
    * @param	item2	야드재료길이
    * @param	table 인수값 혹은 결과값(리턴정보)
    *		<ul>
    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
    *			<li>YDB802[0] :야드재료길이구분
    *			<li>YDB802_ColCnt :1
    *		<ul>
    * @return 정상처리 여부
    */
       public boolean YDB802_NEW(Hashtable table,
                           String item1, // 야드구분
                           int item2 // 야드재료길이
                         ) throws RuleException {     
           Vector vt = new Vector();
           int rc = 0;

           RAPI5J  RCaller=new RAPI5J() ;
           RCaller.Initialize("YDB802");
           /* 사용자 입력값 설정 시작 */ 
           RCaller.AddItemCount(1); 
           RCaller.AddItemString( item1);
           RCaller.AddItemCount(1); 
           RCaller.AddItemInt(  item2);
           /* 사용자 입력값 설정  */ 
          try{                                                    						
               RCaller.MBRS_Run();                                                      
               ResultData    result=new ResultData();	
               result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
               for (int i = 0; i < RCaller.getRowCount(); i++) { 
                   result.add( i , RCaller.ReadString() );	//야드재료길이구분
               } 
               table.put("YDB802_ColCnt", new Integer( RCaller.getColCount() ));					
               table.put("YDB802", result);																					
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
    **import com.metis.rapi4j.*;
    **import java.util.*;
    * item코드허용값 :
    * @작성 날짜: (2013-03-25 14:43:37)
    * @param	item1	야드적치열구분
    * @param	item2	야드스케쥴코드
    * @param	item3	상면보수재
    * @param	table 인수값 혹은 결과값(리턴정보)
    *		<ul>
    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
    *			<li>YDB805[0] :야드To위치Guide
    *			<li>YDB805[1] :후판정정야드그룹구분
    *			<li>YDB805_ColCnt :2
    *		<ul>
    * @return 정상처리 여부
    */
   public boolean YDB805(Hashtable table,
                       String item1, // 야드적치열구분
                       String item2, // 야드스케쥴코드
                       String item3 // 상면보수재
                     ) throws RuleException {
//       Vector vt = new Vector();
//       int rc = 0;

       RAPI4J  RCaller=null;
       try {
          RCaller = new RAPI4J( false,  "" );
           RCaller.Initialize("YDB805");
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
               //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드To위치Guide
               result.add( i ,RCaller.ReadString() );	//야드To위치Guide
               //System.out.println("  ROW[" + i + "] COL[2]:"+ RCaller.ReadString());	//후판정정야드그룹구분
               result.add( i ,RCaller.ReadString() );	//후판정정야드그룹구분
           }
           table.put("YDB805_ColCnt", new Integer(resColTypes.length));
           table.put("YDB805", result);
           if (result.size() == 0) {
               table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");
           }
           return true;
       } catch (Exception e) {
              	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
       }
   }
   
	/**********************************************************
	* 1후판정정추가 SJH16
	**********************************************************/	 
   /**
   *
   **import com.metis.rapi4j.*; 
   **import java.util.*; 
   * item코드허용값 :
   * @작성 날짜: (2016-03-14 17:16:31)
   * @param	item1	야드적치열구분
   * @param	item2	야드스케쥴코드
   * @param	item3	상면보수재
   * @param	table 인수값 혹은 결과값(리턴정보)
   *		<ul>
   *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
   *			<li>YDB810[0] :야드To위치Guide
   *			<li>YDB810[1] :후판정정야드그룹구분
   *			<li>YDB810_ColCnt :2
   *		<ul>
   * @return 정상처리 여부
   */
      public boolean YDB810(Hashtable table,
                          String item1, // 야드적치열구분
                          String item2, // 야드스케쥴코드
                          String item3 // 상면보수재
                        ) throws RuleException {     
//          Vector vt = new Vector();
 //         int rc = 0;

          RAPI4J  RCaller=null;
          try {
             RCaller = new RAPI4J( false,  "" );
              RCaller.Initialize("YDB810");
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
                  //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드To위치Guide
                  result.add( i ,RCaller.ReadString() );	//야드To위치Guide
                  //System.out.println("  ROW[" + i + "] COL[2]:"+ RCaller.ReadString());	//후판정정야드그룹구분
                  result.add( i ,RCaller.ReadString() );	//후판정정야드그룹구분
              } 
              table.put("YDB810_ColCnt", new Integer(resColTypes.length));					
              table.put("YDB810", result);																					
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
      * @작성 날짜: (2022-03-17 10:07:23)
      * @param	item1	야드적치열구분
      * @param	item2	야드스케쥴코드
      * @param	item3	상면보수재
      * @param	table 인수값 혹은 결과값(리턴정보)
      *		<ul>
      *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
      *			<li>YDB805[0] :야드To위치Guide
      *			<li>YDB805[1] :후판정정야드그룹구분
      *			<li>YDB805_ColCnt :2
      *		<ul>
      * @return 정상처리 여부
      */
         public boolean YDB805_NEW(Hashtable table,
                             String item1, // 야드적치열구분
                             String item2, // 야드스케쥴코드
                             String item3 // 상면보수재
                           ) throws com.metis.rapi5j.RuleException {     
             Vector vt = new Vector();
             int rc = 0;

             RAPI5J  RCaller=new RAPI5J() ;
             RCaller.Initialize("YDB805");
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
                     result.add( i , RCaller.ReadString() );	//야드To위치Guide
                     result.add( i , RCaller.ReadString() );	//후판정정야드그룹구분
                 } 
                 table.put("YDB805_ColCnt", new Integer( RCaller.getColCount() ));					
                 table.put("YDB805", result);																					
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
         * @작성 날짜: (2022-03-17 10:08:07)
         * @param	item1	야드적치열구분
         * @param	item2	야드스케쥴코드
         * @param	item3	상면보수재
         * @param	table 인수값 혹은 결과값(리턴정보)
         *		<ul>
         *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
         *			<li>YDB810[0] :야드To위치Guide
         *			<li>YDB810[1] :후판정정야드그룹구분
         *			<li>YDB810_ColCnt :2
         *		<ul>
         * @return 정상처리 여부
         */
            public boolean YDB810_NEW(Hashtable table,
                                String item1, // 야드적치열구분
                                String item2, // 야드스케쥴코드
                                String item3 // 상면보수재
                              ) throws com.metis.rapi5j.RuleException {     
                Vector vt = new Vector();
                int rc = 0;

                RAPI5J  RCaller=new RAPI5J() ;
                RCaller.Initialize("YDB810");
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
                        result.add( i , RCaller.ReadString() );	//야드To위치Guide
                        result.add( i , RCaller.ReadString() );	//후판정정야드그룹구분
                    } 
                    table.put("YDB810_ColCnt", new Integer( RCaller.getColCount() ));					
                    table.put("YDB810", result);																					
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
