package com.inisteel.cim.yd.common.rule;


import java.util.Hashtable;
import java.util.Vector;

import com.metis.rapi4j.RAPI4J;
import com.metis.rapi4j.ResultData;
import com.metis.rapi4j.RuleException;
import com.metis.rapi5j.RAPI5J;

public class BRERule5 {
	/**
	 *
	 * YDB050 : 야드B열연코일제품Bed정리기준
	 * 
	 * @작성 날짜: (2008-10-23 15:33:50)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB050[0] :야드포인트코드
	 *			<li>YDB050_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB050(Hashtable table,
			String item1 // 야드구분
	) throws RuleException {     
		Vector vt = new Vector();
		int rc = 0;

		byte[] ItemType = {
				2
		};
		int[] ItemCd = {
				499
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB050", 1, ItemType, ItemCd);
			/* 사용자 입력값 설정 시작 */ 
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item1);
			/* 사용자 입력값 설정  */ 
			if(!RCaller.MBRS_Call(0)){                                                    						
				throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
			}																		 									
			byte resColTypes[] = new byte[RCaller.getColCount()];                     
			for (int j = 0; j < RCaller.getColCount(); j++) {                            				
				resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
			}                     																						
			ResultData    result=new ResultData();	
			result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
			for (int i = 0; i < RCaller.getRowCount(); i++) { 
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드포인트코드
				result.add( i , RCaller.ReadString() );	//야드포인트코드
			} 
			table.put("YDB050_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB050", result);																					
			if(result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	}; // end of YDB050()




	/**
	 *
	 * YDB051 : 야드C열연코일제품Bed정리기준
	 * 
	 * @작성 날짜: (2008-10-23 15:34:17)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB051[0] :야드포인트코드
	 *			<li>YDB051_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB051(Hashtable table,
			String item1 // 야드구분
	) throws RuleException {     
		Vector vt = new Vector();
		int rc = 0;

		byte[] ItemType = {
				2
		};
		int[] ItemCd = {
				499
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB051", 1, ItemType, ItemCd);
			/* 사용자 입력값 설정 시작 */ 
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item1);
			/* 사용자 입력값 설정  */ 
			if(!RCaller.MBRS_Call(0)){                                                    						
				throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
			}																		 									
			byte resColTypes[] = new byte[RCaller.getColCount()];                     
			for (int j = 0; j < RCaller.getColCount(); j++) {                            				
				resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
			}                     																						
			ResultData    result=new ResultData();	
			result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
			for (int i = 0; i < RCaller.getRowCount(); i++) { 
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드포인트코드
				result.add( i , RCaller.ReadString() );	//야드포인트코드
			} 
			table.put("YDB051_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB051", result);																					
			if(result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB051()




	/**
	 *
	 * YDB052 : 야드후판제품입고Bed정리기준
	 * 
	 * @작성 날짜: (2008-10-23 15:34:47)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB052[0] :야드포인트코드
	 *			<li>YDB052_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB052(Hashtable table,
			String item1 // 야드구분
	) throws RuleException {     
		Vector vt = new Vector();
		int rc = 0;

		byte[] ItemType = {
				2
		};
		int[] ItemCd = {
				499
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB052", 1, ItemType, ItemCd);
			/* 사용자 입력값 설정 시작 */ 
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item1);
			/* 사용자 입력값 설정  */ 
			if(!RCaller.MBRS_Call(0)){                                                    						
				throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
			}																		 									
			byte resColTypes[] = new byte[RCaller.getColCount()];                     
			for (int j = 0; j < RCaller.getColCount(); j++) {                            				
				resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
			}                     																						
			ResultData    result=new ResultData();	
			result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
			for (int i = 0; i < RCaller.getRowCount(); i++) { 
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드포인트코드
				result.add( i , RCaller.ReadString() );	//야드포인트코드
			} 
			table.put("YDB052_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB052", result);																					
			if(result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB052()




	/**
	 *
	 * YDB053 : 야드후판제품선별작업기준
	 * 
	 * @작성 날짜: (2008-10-23 15:35:23)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB053[0] :야드포인트코드
	 *			<li>YDB053_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB053(Hashtable table,
			String item1 // 야드구분
	) throws RuleException {     
		Vector vt = new Vector();
		int rc = 0;

		byte[] ItemType = {
				2
		};
		int[] ItemCd = {
				499
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB053", 1, ItemType, ItemCd);
			/* 사용자 입력값 설정 시작 */ 
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item1);
			/* 사용자 입력값 설정  */ 
			if(!RCaller.MBRS_Call(0)){                                                    						
				throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
			}																		 									
			byte resColTypes[] = new byte[RCaller.getColCount()];                     
			for (int j = 0; j < RCaller.getColCount(); j++) {                            				
				resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
			}                     																						
			ResultData    result=new ResultData();	
			result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
			for (int i = 0; i < RCaller.getRowCount(); i++) { 
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드포인트코드
				result.add( i , RCaller.ReadString() );	//야드포인트코드
			} 
			table.put("YDB053_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB053", result);																					
			if(result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB053()




	/**
	 *
	 * YDB054 : 야드후판제품주문저장집합Match기준
	 * 
	 * @작성 날짜: (2008-10-23 15:35:49)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB054[0] :야드포인트코드
	 *			<li>YDB054_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB054(Hashtable table,
			String item1 // 야드구분
	) throws RuleException {     
		Vector vt = new Vector();
		int rc = 0;

		byte[] ItemType = {
				2
		};
		int[] ItemCd = {
				499
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB054", 1, ItemType, ItemCd);
			/* 사용자 입력값 설정 시작 */ 
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item1);
			/* 사용자 입력값 설정  */ 
			if(!RCaller.MBRS_Call(0)){                                                    						
				throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
			}																		 									
			byte resColTypes[] = new byte[RCaller.getColCount()];                     
			for (int j = 0; j < RCaller.getColCount(); j++) {                            				
				resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
			}                     																						
			ResultData    result=new ResultData();	
			result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
			for (int i = 0; i < RCaller.getRowCount(); i++) { 
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드포인트코드
				result.add( i , RCaller.ReadString() );	//야드포인트코드
			} 
			table.put("YDB054_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB054", result);																					
			if(result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB054()  


	/**
	 * 기준ID : YDB599
	 * 기준명 : C열연코일제품야드-차량LOT편성자동유무관리
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2009-08-26 12:59:06)
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB599[0] :사용유무
	 *			<li>YDB599_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean YDB599(Hashtable table) throws RuleException {     
	        Vector vt = new Vector();
	        int rc = 0;

	        RAPI4J  RCaller=null;
	        try {
	           RCaller = new RAPI4J( false,  "" );
	            RCaller.Initialize("YDB599");
	            /* 사용자 입력값 설정 시작 */ 
	            /* 사용자 입력값 설정  */ 
	            if(!RCaller.MBRS_Call(2)){                                                    						
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
	            table.put("YDB599_ColCnt", new Integer(resColTypes.length));					
	            table.put("YDB599", result);																					
	            if(result.size() == 0) {																								
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	         																																
	    }				//end of YDB599




	    
	//==================================================================
	// [코드매핑] 동분산구분
	// 2010.01.12
	// 권오창
	//
	//==================================================================
	/**
	*
	**import com.metis.rapi4j.*; 
	**import java.util.*; 
	* item코드허용값 :
	* @작성 날짜: (2010-01-12 13:43:29)
	* @param	item1	Scarfing여부
	* @param	item2	Scarfing완료유무
	* @param	item3	HCR구분
	* @param	item4	주문여재구분
	* @param	item5	재료외형구분
	* @param	table 인수값 혹은 결과값(리턴정보)
	*		<ul>
	*			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	*			<li>YDB001[0] :야드동구분
	*			<li>YDB001_ColCnt :1
	*		<ul>
	* @return 정상처리 여부
	*/
    public boolean YDB001(Hashtable table,
		String item1, // Scarfing여부
		String item2, // Scarfing완료유무
		String item3, // HCR구분
		String item4, // 주문여재구분
		String item5 // 재료외형구분
	) throws RuleException {     
	 Vector vt      = new Vector();
	 int rc         = 0;
	 RAPI4J RCaller = null;
	 
	 try {
		RCaller = new RAPI4J(false, "");
		RCaller.Initialize("YDB001");
		
		/* 사용자 입력값 설정 시작 */ 
		RCaller.AddItemCount(1); 
		RCaller.AddItemString(item1);
		RCaller.AddItemCount(1); 
		RCaller.AddItemString(item2);
		RCaller.AddItemCount(1); 
		RCaller.AddItemString(item3);
		RCaller.AddItemCount(1); 
		RCaller.AddItemString(item4);
		RCaller.AddItemCount(1); 
		RCaller.AddItemString(item5);
			   
		/* 사용자 입력값 설정  */ 
		if(!RCaller.MBRS_Call(2)){                                                    						
			throw new RuleException(RCaller.getErrorCode(), RCaller.getErrorMessage());
		}																		 									
		   
		byte resColTypes[] = new byte[RCaller.getColCount()];                     
		   
		for(int j=0; j<RCaller.getColCount(); j++) {                            				
			resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
		}                     																						
		   
		ResultData result = new ResultData();	
		result.setRowCol(RCaller.getRowCount(), RCaller.getColCount()); 
		   
		for(int i=0; i<RCaller.getRowCount(); i++) { 
			//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드동구분
			result.add(i, RCaller.ReadString());	//야드동구분
		} 
		   
		table.put("YDB001_ColCnt", new Integer(resColTypes.length));					
		table.put("YDB001", result);																					
		   
		if(result.size() == 0){																								
			table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
		}																															
		   
		return true;																										
	 }catch(Exception e){																						
	     throw new RuleException(RCaller.getErrorCode(), RCaller.getErrorMessage());
	 }																																						
    };    
	    
    /**
    *
    import com.metis.rapi5j.*; 
    import java.util.*; 
    * item코드허용값 :
    * @작성 날짜: (2022-03-17 09:23:37)
    * @param	item1	Scarfing여부
    * @param	item2	Scarfing완료유무
    * @param	item3	HCR구분
    * @param	item4	주문여재구분
    * @param	item5	재료외형구분
    * @param	table 인수값 혹은 결과값(리턴정보)
    *		<ul>
    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
    *			<li>YDB001[0] :야드동구분
    *			<li>YDB001_ColCnt :1
    *		<ul>
    * @return 정상처리 여부
    */
       public boolean YDB001_NEW(Hashtable table,
                           String item1, // Scarfing여부
                           String item2, // Scarfing완료유무
                           String item3, // HCR구분
                           String item4, // 주문여재구분
                           String item5 // 재료외형구분
                         ) throws RuleException {     
           Vector vt = new Vector();
           int rc = 0;

           RAPI5J  RCaller=new RAPI5J() ;
           RCaller.Initialize("YDB001");
           /* 사용자 입력값 설정 시작 */ 
           RCaller.AddItemCount(1); 
           RCaller.AddItemString( item1);
           RCaller.AddItemCount(1); 
           RCaller.AddItemString( item2);
           RCaller.AddItemCount(1); 
           RCaller.AddItemString( item3);
           RCaller.AddItemCount(1); 
           RCaller.AddItemString( item4);
           RCaller.AddItemCount(1); 
           RCaller.AddItemString( item5);
           /* 사용자 입력값 설정  */ 
          try{                                                    						
               RCaller.MBRS_Run();                                                      
               ResultData    result=new ResultData();	
               result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
               for (int i = 0; i < RCaller.getRowCount(); i++) { 
                   result.add( i , RCaller.ReadString() );	//야드동구분
               } 
               table.put("YDB001_ColCnt", new Integer( RCaller.getColCount() ));					
               table.put("YDB001", result);																					
               if (result.size() == 0) {
                   table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");
               }																															
               return true;																										
           } catch (Exception e) {																						
                throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
           }																																						
       }  ;      
    
    
    
	//==================================================================
	// [코드매핑] 목표야드
	// 2010.01.12
	// 권오창
	//
    //==================================================================
    /**
    *
    **import com.metis.rapi4j.*; 
    **import java.util.*; 
    * item코드허용값 :
    * @작성 날짜: (2010-01-12 17:15:11)
    * @param	item1	야드구분
    * @param	item2	Slab지시행선코드
    * @param	item3	재료진도코드
    * @param	item4	착지개소코드
    * @param	table 인수값 혹은 결과값(리턴정보)
    *		<ul>
    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
    *			<li>YDB002[0] :야드목표야드구분
    *			<li>YDB002_ColCnt :1
    *		<ul>
    * @return 정상처리 여부
    */
    public boolean YDB002(Hashtable table,
            String item1, // 야드구분
            String item2, // Slab지시행선코드
            String item3, // 재료진도코드
            String item4 // 착지개소코드
          ) throws RuleException {      
	 Vector vt      = new Vector();
	 int rc         = 0;
	 RAPI4J RCaller = null;
	 
	 try {
		RCaller = new RAPI4J(false, "");
		RCaller.Initialize("YDB002");
		
		/* 사용자 입력값 설정 시작 */ 
		RCaller.AddItemCount(1); 
		RCaller.AddItemString(item1);
		RCaller.AddItemCount(1); 
		RCaller.AddItemString(item2);
		RCaller.AddItemCount(1); 
		RCaller.AddItemString(item3);
		RCaller.AddItemCount(1); 
		RCaller.AddItemString(item4);
			   
		/* 사용자 입력값 설정  */ 
		if(!RCaller.MBRS_Call(2)){                                                    						
			throw new RuleException(RCaller.getErrorCode(), RCaller.getErrorMessage());
		}																		 									
		   
		byte resColTypes[] = new byte[RCaller.getColCount()];                     
		   
		for(int j=0; j<RCaller.getColCount(); j++) {                            				
			resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
		}                     																						
		   
		ResultData result = new ResultData();	
		result.setRowCol(RCaller.getRowCount(), RCaller.getColCount()); 
		   
		for(int i=0; i<RCaller.getRowCount(); i++) { 
			//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드동구분
			result.add(i, RCaller.ReadString());	//야드동구분
		} 
		   
		table.put("YDB002_ColCnt", new Integer(resColTypes.length));					
		table.put("YDB002", result);																					
		   
		if(result.size() == 0){																								
			table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
		}																															
		   
		return true;																										
	 }catch(Exception e){																						
	     throw new RuleException(RCaller.getErrorCode(), RCaller.getErrorMessage());
	 }																																						
    };  
     
    
    /**
	 *
	 import com.metis.rapi5j.*; 
	 import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2022-03-17 09:24:40)
	 * @param	item1	야드구분
	 * @param	item2	Slab지시행선코드
	 * @param	item3	재료진도코드
	 * @param	item4	착지개소코드
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
	 *			<li>YDB002[0] :야드목표야드구분
	 *			<li>YDB002_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean YDB002_NEW(Hashtable table,
	                        String item1, // 야드구분
	                        String item2, // Slab지시행선코드
	                        String item3, // 재료진도코드
	                        String item4 // 착지개소코드
	                      ) throws RuleException {     
	        Vector vt = new Vector();
	        int rc = 0;

	        RAPI5J  RCaller=new RAPI5J() ;
	        RCaller.Initialize("YDB002");
	        /* 사용자 입력값 설정 시작 */ 
	        RCaller.AddItemCount(1); 
	        RCaller.AddItemString( item1);
	        RCaller.AddItemCount(1); 
	        RCaller.AddItemString( item2);
	        RCaller.AddItemCount(1); 
	        RCaller.AddItemString( item3);
	        RCaller.AddItemCount(1); 
	        RCaller.AddItemString( item4);
	        /* 사용자 입력값 설정  */ 
	       try{                                                    						
	            RCaller.MBRS_Run();                                                      
	            ResultData    result=new ResultData();	
	            result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
	            for (int i = 0; i < RCaller.getRowCount(); i++) { 
	                result.add( i , RCaller.ReadString() );	//야드목표야드구분
	            } 
	            table.put("YDB002_ColCnt", new Integer( RCaller.getColCount() ));					
	            table.put("YDB002", result);																					
	            if (result.size() == 0) {
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	             throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	    } ;  
    
    
	//==================================================================
	// [코드매핑] 목표동
	// 2010.01.12
	// 권오창
	//
	//==================================================================
    /**
    *
    **import com.metis.rapi4j.*; 
    **import java.util.*; 
    * item코드허용값 :
    * @작성 날짜: (2010-01-12 17:34:33)
    * @param	item1	야드목표야드구분
    * @param	item2	Slab지시행선코드
    * @param	item3	재료외형구분
    * @param	item4	야드동구분
    * @param	table 인수값 혹은 결과값(리턴정보)
    *		<ul>
    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
    *			<li>YDB003[0] :야드목표동구분
    *			<li>YDB003_ColCnt :1
    *		<ul>
    * @return 정상처리 여부
    */
    public boolean YDB003(Hashtable table,
            String item1, // 야드목표야드구분
            String item2, // Slab지시행선코드
            String item3, // 재료외형구분
            String item4 // 야드동구분
          ) throws RuleException {         
	 Vector vt      = new Vector();
	 int rc         = 0;
	 RAPI4J RCaller = null;
	 
	 try {
		RCaller = new RAPI4J(false, "");
		RCaller.Initialize("YDB003");
		
		/* 사용자 입력값 설정 시작 */ 
		RCaller.AddItemCount(1); 
		RCaller.AddItemString(item1);
		RCaller.AddItemCount(1); 
		RCaller.AddItemString(item2);
		RCaller.AddItemCount(1); 
		RCaller.AddItemString(item3);
		RCaller.AddItemCount(1); 
		RCaller.AddItemString(item4);
			   
		/* 사용자 입력값 설정  */ 
		if(!RCaller.MBRS_Call(2)){                                                    						
			throw new RuleException(RCaller.getErrorCode(), RCaller.getErrorMessage());
		}																		 									
		   
		byte resColTypes[] = new byte[RCaller.getColCount()];                     
		   
		for(int j=0; j<RCaller.getColCount(); j++) {                            				
			resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
		}                     																						
		   
		ResultData result = new ResultData();	
		result.setRowCol(RCaller.getRowCount(), RCaller.getColCount()); 
		   
		for(int i=0; i<RCaller.getRowCount(); i++) { 
			//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드동구분
			result.add(i, RCaller.ReadString());	//야드동구분
		} 
		   
		table.put("YDB003_ColCnt", new Integer(resColTypes.length));					
		table.put("YDB003", result);																					
		   
		if(result.size() == 0){																								
			table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
		}																															
		   
		return true;																										
	 }catch(Exception e){																						
	     throw new RuleException(RCaller.getErrorCode(), RCaller.getErrorMessage());
	 }																																						
    };  

    
    /**
	 *
	 import com.metis.rapi5j.*; 
	 import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2022-03-17 09:25:56)
	 * @param	item1	야드구분
	 * @param	item2	Slab지시행선코드
	 * @param	item3	재료외형구분
	 * @param	item4	야드동구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
	 *			<li>YDB003[0] :야드목표동구분
	 *			<li>YDB003_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean YDB003_NEW(Hashtable table,
	                        String item1, // 야드구분
	                        String item2, // Slab지시행선코드
	                        String item3, // 재료외형구분
	                        String item4 // 야드동구분
	                      ) throws RuleException {     
	        Vector vt = new Vector();
	        int rc = 0;

	        RAPI5J  RCaller=new RAPI5J() ;
	        RCaller.Initialize("YDB003");
	        /* 사용자 입력값 설정 시작 */ 
	        RCaller.AddItemCount(1); 
	        RCaller.AddItemString( item1);
	        RCaller.AddItemCount(1); 
	        RCaller.AddItemString( item2);
	        RCaller.AddItemCount(1); 
	        RCaller.AddItemString( item3);
	        RCaller.AddItemCount(1); 
	        RCaller.AddItemString( item4);
	        /* 사용자 입력값 설정  */ 
	       try{                                                    						
	            RCaller.MBRS_Run();                                                      
	            ResultData    result=new ResultData();	
	            result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
	            for (int i = 0; i < RCaller.getRowCount(); i++) { 
	                result.add( i , RCaller.ReadString() );	//야드목표동구분
	            } 
	            table.put("YDB003_ColCnt", new Integer( RCaller.getColCount() ));					
	            table.put("YDB003", result);																					
	            if (result.size() == 0) {
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	             throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	    }     ;
    
    
	//==================================================================
	// [코드매핑] 목표행선구분
	// 2010.01.12
	// 권오창
	//
	//==================================================================
    /**
    *
    **import com.metis.rapi4j.*; 
    **import java.util.*; 
    * item코드허용값 :
    * @작성 날짜: (2010-01-12 18:02:06)
    * @param	item1	Slab지시행선코드
    * @param	item2	재료진도코드
    * @param	item3	야드동구분
    * @param	item4	재료외형구분
    * @param	table 인수값 혹은 결과값(리턴정보)
    *		<ul>
    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
    *			<li>YDB004[0] :야드목표행선구분
    *			<li>YDB004_ColCnt :1
    *		<ul>
    * @return 정상처리 여부
    */
    public boolean YDB004(Hashtable table,
            String item1, // Slab지시행선코드
            String item2, // 재료진도코드
            String item3, // 야드동구분
            String item4 // 재료외형구분
          ) throws RuleException {           
	 Vector vt      = new Vector();
	 int rc         = 0;
	 RAPI4J RCaller = null;
	 
	 try {
		RCaller = new RAPI4J(false, "");
		RCaller.Initialize("YDB004");
		
		/* 사용자 입력값 설정 시작 */ 
		RCaller.AddItemCount(1); 
		RCaller.AddItemString(item1);
		RCaller.AddItemCount(1); 
		RCaller.AddItemString(item2);
		RCaller.AddItemCount(1); 
		RCaller.AddItemString(item3);
		RCaller.AddItemCount(1); 
		RCaller.AddItemString(item4);
			   
		/* 사용자 입력값 설정  */ 
		if(!RCaller.MBRS_Call(2)){                                                    						
			throw new RuleException(RCaller.getErrorCode(), RCaller.getErrorMessage());
		}																		 									
		   
		byte resColTypes[] = new byte[RCaller.getColCount()];                     
		   
		for(int j=0; j<RCaller.getColCount(); j++) {                            				
			resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
		}                     																						
		   
		ResultData result = new ResultData();	
		result.setRowCol(RCaller.getRowCount(), RCaller.getColCount()); 
		   
		for(int i=0; i<RCaller.getRowCount(); i++) { 
			//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드동구분
			result.add(i, RCaller.ReadString());	//야드동구분
		} 
		   
		table.put("YDB004_ColCnt", new Integer(resColTypes.length));					
		table.put("YDB004", result);																					
		   
		if(result.size() == 0){																								
			table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
		}																															
		   
		return true;																										
	 }catch(Exception e){																						
	     throw new RuleException(RCaller.getErrorCode(), RCaller.getErrorMessage());
	 }																																						
    };  
    
    
    /**
	 *
	 import com.metis.rapi5j.*; 
	 import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2022-03-17 09:26:58)
	 * @param	item1	Slab지시행선코드
	 * @param	item2	재료진도코드
	 * @param	item3	야드동구분
	 * @param	item4	재료외형구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
	 *			<li>YDB004[0] :야드목표행선구분
	 *			<li>YDB004_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean YDB004_NEW(Hashtable table,
	                        String item1, // Slab지시행선코드
	                        String item2, // 재료진도코드
	                        String item3, // 야드동구분
	                        String item4 // 재료외형구분
	                      ) throws RuleException {     
	        Vector vt = new Vector();
	        int rc = 0;

	        RAPI5J  RCaller=new RAPI5J() ;
	        RCaller.Initialize("YDB004");
	        /* 사용자 입력값 설정 시작 */ 
	        RCaller.AddItemCount(1); 
	        RCaller.AddItemString( item1);
	        RCaller.AddItemCount(1); 
	        RCaller.AddItemString( item2);
	        RCaller.AddItemCount(1); 
	        RCaller.AddItemString( item3);
	        RCaller.AddItemCount(1); 
	        RCaller.AddItemString( item4);
	        /* 사용자 입력값 설정  */ 
	       try{                                                    						
	            RCaller.MBRS_Run();                                                      
	            ResultData    result=new ResultData();	
	            result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
	            for (int i = 0; i < RCaller.getRowCount(); i++) { 
	                result.add( i , RCaller.ReadString() );	//야드목표행선구분
	            } 
	            table.put("YDB004_ColCnt", new Integer( RCaller.getColCount() ));					
	            table.put("YDB004", result);																					
	            if (result.size() == 0) {
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	             throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	    }     ;
  //---------------------------------------------------------------------------
} // end of class
