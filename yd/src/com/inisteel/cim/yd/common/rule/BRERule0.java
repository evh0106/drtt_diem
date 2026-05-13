package com.inisteel.cim.yd.common.rule;


import java.util.Hashtable;
import java.util.Vector;

import com.metis.rapi4j.RAPI4J;
import com.metis.rapi4j.ResultData;
import com.metis.rapi4j.RuleException;


public class BRERule0 {

	/*
	 *
	 * YDB000 : 야드Interface관리
	 * 
	 * @작성 날짜: (2008-10-10 14:05:07)
	 * @param	item1	JMSTC코드
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB000[0] :메시지내용
	 *			<li>YDB000[1] :야드Message발생유형
	 *			<li>YDB000[2] :CLASS코드
	 *			<li>YDB000[3] :METHODE_명
	 *			<li>YDB000_ColCnt :4
	 *		<ul>
	 * @return 정상처리 여부
	 */
	/*++++++++++++++++++++++++++++++++++++++++
	 * AS-IS 방식
	 ++++++++++++++++++++++++++++++++++++++++*/
	/*public boolean YDB000(Hashtable table,
			String item1 // JMSTC코드
	) throws RuleException {     
		Vector vt = new Vector();
		int rc = 0;

		byte[] ItemType = {
				2
		};
		int[] ItemCd = {
				478
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB000", 1, ItemType, ItemCd);
			 사용자 입력값 설정 시작  
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item1);
			 사용자 입력값 설정   
			if (!RCaller.MBRS_Call(0)){                                                    						
				throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
			}																		 									
			byte resColTypes[] = new byte[RCaller.getColCount()];                     
			for (int j = 0; j < RCaller.getColCount(); j++) {                            				
				resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
			}                     																						
			ResultData    result=new ResultData();	
			result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
			for (int i = 0; i < RCaller.getRowCount(); i++) { 
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//메시지내용
				result.add( i , RCaller.ReadString() );	//메시지내용
				//System.out.println("  ROW[" + i + "] COL[2]:"+ RCaller.ReadString());	//야드Message발생유형
				result.add( i , RCaller.ReadString() );	//야드Message발생유형
				//System.out.println("  ROW[" + i + "] COL[3]:"+ RCaller.ReadString());	//CLASS코드
				result.add( i , RCaller.ReadString() );	//CLASS코드
				//System.out.println("  ROW[" + i + "] COL[4]:"+ RCaller.ReadString());	//METHODE_명
				result.add( i , RCaller.ReadString() );	//METHODE_명
			} 
			table.put("YDB000_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB000", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};*/ // end of YDB000

	/**
	 *
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2009-03-20 11:09:02)
	 * @param	item1	JMSTC코드
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB000[0] :CLASS_명
	 *			<li>YDB000[1] :METHODE_명
	 *			<li>YDB000[2] :코드설명
	 *			<li>YDB000_ColCnt :3
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean YDB000(Hashtable table,
	                        String item1 // JMSTC코드
	                      ) throws RuleException {     
	        Vector vt = new Vector();
	        int rc = 0;

	        RAPI4J  RCaller=null;
	        try {
	           RCaller = new RAPI4J( false,  "" );
	            RCaller.Initialize("YDB000");
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
	                //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//CLASS_명
	                result.add( i , RCaller.ReadString() );	//CLASS_명
	                //System.out.println("  ROW[" + i + "] COL[2]:"+ RCaller.ReadString());	//METHODE_명
	                result.add( i , RCaller.ReadString() );	//METHODE_명
	                //System.out.println("  ROW[" + i + "] COL[3]:"+ RCaller.ReadString());	//코드설명
	                result.add( i , RCaller.ReadString() );	//코드설명
	            } 
	            table.put("YDB000_ColCnt", new Integer(resColTypes.length));					
	            table.put("YDB000", result);																					
	            if (result.size() == 0) {																								
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	         																																
	    }


	/*
	 *
	 * YDB001 : 야드슬라브강종부여기준
	 * 
	 * @작성 날짜: (2008-10-14 09:18:41)
	 * @param	item1	야드구분
	 * @param	item2	재료외형구분
	 * @param	item3	주문여재구분
	 * @param	item4	규격약호
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB001[0] :강종코드
	 *			<li>YDB001_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
//	public boolean YDB001(Hashtable table,
//			String item1, // 야드구분
//			String item2, // 재료외형구분
//			String item3, // 주문여재구분
//			String item4 // 규격약호
//	) throws RuleException {     
//		Vector vt = new Vector();
//		int rc = 0;
//
//		byte[] ItemType = {
//				2,
//				2,
//				2,
//				2
//		};
//		int[] ItemCd = {
//				499,
//				346,
//				489,
//				301
//		};
//
//		RAPI4J  RCaller=null;
//		try {
//			RCaller = new RAPI4J( false,  "" );
//			RCaller.Initialize("YDB001", 4, ItemType, ItemCd);
//			/* 사용자 입력값 설정 시작 */ 
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item1);
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item2);
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item3);
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item4);
//			/* 사용자 입력값 설정  */ 
//			if (!RCaller.MBRS_Call(0)){                                                    						
//				throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
//			}																		 									
//			byte resColTypes[] = new byte[RCaller.getColCount()];                     
//			for (int j = 0; j < RCaller.getColCount(); j++) {                            				
//				resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
//			}                     																						
//			ResultData    result=new ResultData();	
//			result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
//			for (int i = 0; i < RCaller.getRowCount(); i++) { 
//				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//강종코드
//				result.add( i , RCaller.ReadString() );	//강종코드
//			} 
//			table.put("YDB001_ColCnt", new Integer(resColTypes.length));					
//			table.put("YDB001", result);																					
//			if (result.size() == 0) {																								
//				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
//			}																															
//			return true;																										
//		} catch (Exception e) {																						
//			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
//		}																																						
//
//	}; // end of YDB001
	
	
	    /**
	    *
	    **import com.metis.rapi4j.*; 
	    **import java.util.*; 
	    * item코드허용값 :
	    * @작성 날짜: (2009-08-26 11:40:04)
	    * @param	table 인수값 혹은 결과값(리턴정보)
	    *		<ul>
	    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	    *			<li>YDB001[0] :사용유무
	    *			<li>YDB001_ColCnt :1
	    *		<ul>
	    * @return 정상처리 여부
	    */
//	       public boolean YDB001(Hashtable table
//	                         ) throws RuleException {     
//	           Vector vt = new Vector();
//	           int rc = 0;
//
//	           RAPI4J  RCaller=null;
//	           try {
//	              RCaller = new RAPI4J( false,  "" );
//	               RCaller.Initialize("YDB001");
//	               /* 사용자 입력값 설정 시작 */ 
//	               /* 사용자 입력값 설정  */ 
//	               if (!RCaller.MBRS_Call(2)){                                                    						
//	                  	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
//	               }																		 									
//	               byte resColTypes[] = new byte[RCaller.getColCount()];                     
//	               for (int j = 0; j < RCaller.getColCount(); j++) {                            				
//	                   resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
//	               }                     																						
//	               ResultData    result=new ResultData();	
//	               result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
//	               for (int i = 0; i < RCaller.getRowCount(); i++) { 
//	                   //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//사용유무
//	                   result.add( i ,RCaller.ReadString() );	//사용유무
//	               } 
//	               table.put("YDB001_ColCnt", new Integer(resColTypes.length));					
//	               table.put("YDB001", result);																					
//	               if (result.size() == 0) {																								
//	                   table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
//	               }																															
//	               return true;																										
//	           } catch (Exception e) {																						
//	                  	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
//	           }																																						
//	            																																
//	       }

	/**
	 *
	 * YDB002 : 야드코일보급행선부여기준 
	 * 
	 * @작성 날짜: (2008-10-14 15:27:12)
	 * @param	item1	야드구분
	 * @param	item2	주문여재구분
	 * @param	item3	야드작업진도코드
	 * @param	item4	조업구분
	 * @param	item5	공장구분
	 * @param	item6	공정구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB002[0] :야드적치행선구분
	 *			<li>YDB002_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
//	public boolean YDB002(Hashtable table,
//			String item1, // 야드구분
//			String item2, // 주문여재구분
//			String item3, // 야드작업진도코드
//			String item4, // 조업구분
//			String item5, // 공장구분
//			String item6 // 공정구분
//	) throws RuleException {     
//		Vector vt = new Vector();
//		int rc = 0;
//
//		byte[] ItemType = {
//				2,
//				2,
//				2,
//				2,
//				2,
//				2
//		};
//		int[] ItemCd = {
//				499,
//				489,
//				503,
//				275,
//				276,
//				610
//		};
//
//		RAPI4J  RCaller=null;
//		try {
//			RCaller = new RAPI4J( false,  "" );
//			RCaller.Initialize("YDB002", 6, ItemType, ItemCd);
//			/* 사용자 입력값 설정 시작 */ 
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item1);
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item2);
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item3);
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item4);
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item5);
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item6);
//			/* 사용자 입력값 설정  */ 
//			if (!RCaller.MBRS_Call(0)){                                                    						
//				throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
//			}																		 									
//			byte resColTypes[] = new byte[RCaller.getColCount()];                     
//			for (int j = 0; j < RCaller.getColCount(); j++) {                            				
//				resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
//			}                     																						
//			ResultData    result=new ResultData();	
//			result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
//			for (int i = 0; i < RCaller.getRowCount(); i++) { 
//				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드적치행선구분
//				result.add( i , RCaller.ReadString() );	//야드적치행선구분
//			} 
//			table.put("YDB002_ColCnt", new Integer(resColTypes.length));					
//			table.put("YDB002", result);																					
//			if (result.size() == 0) {																								
//				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
//			}																															
//			return true;																										
//		} catch (Exception e) {																						
//			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
//		}																																						
//
//	}; // end of YDB002()
	
	
	
	
	
	/**
	 *
	 * YDB003 : 야드코일입고행선부여기준
	 * 
	 * @작성 날짜: (2008-10-14 15:27:43)
	 * @param	item1	야드구분
	 * @param	item2	주문여재구분
	 * @param	item3	야드작업진도코드
	 * @param	item4	제품등급
	 * @param	item5	수주구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB003[0] :야드적치행선구분
	 *			<li>YDB003_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
//	public boolean YDB003(Hashtable table,
//			String item1, // 야드구분
//			String item2, // 주문여재구분
//			String item3, // 야드작업진도코드
//			String item4, // 제품등급
//			String item5  // 수주구분
//	) throws RuleException {     
//		Vector vt = new Vector();
//		int rc = 0;
//
//		byte[] ItemType = {
//				2,
//				2,
//				2,
//				2,
//				2
//		};
//		int[] ItemCd = {
//				499,
//				489,
//				503,
//				505,
//				506
//		};
//
//		RAPI4J  RCaller=null;
//		try {
//			RCaller = new RAPI4J( false,  "" );
//			RCaller.Initialize("YDB003", 5, ItemType, ItemCd);
//			/* 사용자 입력값 설정 시작 */ 
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item1);
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item2);
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item3);
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item4);
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item5);
//			/* 사용자 입력값 설정  */ 
//			if (!RCaller.MBRS_Call(0)){                                                    						
//				throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
//			}																		 									
//			byte resColTypes[] = new byte[RCaller.getColCount()];                     
//			for (int j = 0; j < RCaller.getColCount(); j++) {                            				
//				resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
//			}                     																						
//			ResultData    result=new ResultData();	
//			result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
//			for (int i = 0; i < RCaller.getRowCount(); i++) { 
//				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드적치행선구분
//				result.add( i , RCaller.ReadString() );	//야드적치행선구분
//			} 
//			table.put("YDB003_ColCnt", new Integer(resColTypes.length));					
//			table.put("YDB003", result);																					
//			if (result.size() == 0) {																								
//				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
//			}																															
//			return true;																										
//		} catch (Exception e) {																						
//			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
//		}																																						
//
//	}; // end of YDB003()
	
	
	
	
	

	/**
	 * YDB004 : 야드후판입고행선부여기준
	 * 
	 * @작성 날짜: (2008-10-14 15:28:09)
	 * @param	item1	야드구분
	 * @param	item2	수주구분
	 * @param	item3	야드재료특별관리구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB004[0] :야드적치행선구분
	 *			<li>YDB004_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
//	public boolean YDB004(Hashtable table,
//			String item1, // 야드구분
//			String item2, // 수주구분
//			String item3 // 야드재료특별관리구분
//	) throws RuleException {     
//		Vector vt = new Vector();
//		int rc = 0;
//
//		byte[] ItemType = {
//				2,
//				2,
//				2
//		};
//		int[] ItemCd = {
//				499,
//				506,
//				507
//		};
//
//		RAPI4J  RCaller=null;
//		try {
//			RCaller = new RAPI4J( false,  "" );
//			RCaller.Initialize("YDB004", 3, ItemType, ItemCd);
//			/* 사용자 입력값 설정 시작 */ 
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item1);
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item2);
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item3);
//			/* 사용자 입력값 설정  */ 
//			if (!RCaller.MBRS_Call(0)){                                                    						
//				throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
//			}																		 									
//			byte resColTypes[] = new byte[RCaller.getColCount()];                     
//			for (int j = 0; j < RCaller.getColCount(); j++) {                            				
//				resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
//			}                     																						
//			ResultData    result=new ResultData();	
//			result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
//			for (int i = 0; i < RCaller.getRowCount(); i++) { 
//				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드적치행선구분
//				result.add( i , RCaller.ReadString() );	//야드적치행선구분
//			} 
//			table.put("YDB004_ColCnt", new Integer(resColTypes.length));					
//			table.put("YDB004", result);																					
//			if (result.size() == 0) {																								
//				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
//			}																															
//			return true;																										
//		} catch (Exception e) {																						
//			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
//		}																																						
//
//	};  // end of YDB004
	
	
	
	
	
//	/**
//	 * YDB005 : 야드슬라브적치폭구분부여기준
//	 * 
//	 * @작성 날짜: (2008-10-14 15:28:26)
//	 * @param	item1	야드구분
//	 * @param	item2	주문여재구분
//	 * @param	item3	야드재료폭
//	 * @param	table 인수값 혹은 결과값(리턴정보)
//	 *		<ul>
//	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
//	 *			<li>YDB005[0] :야드재료폭구분
//	 *			<li>YDB005_ColCnt :1
//	 *		<ul>
//	 * @return 정상처리 여부
//	 */
//	public boolean YDB0051(Hashtable table,
//			String item1, // 야드구분
//			String item2, // 주문여재구분
//			double item3 // 야드재료폭
//	) throws RuleException {     
//		Vector vt = new Vector();
//		int rc = 0;
//
//		byte[] ItemType = {
//				2,
//				2,
//				1
//		};
//		int[] ItemCd = {
//				499,
//				489,
//				508
//		};
//
//		RAPI4J  RCaller=null;
//		try {
//			RCaller = new RAPI4J( false,  "" );
//			RCaller.Initialize("YDB005", 3, ItemType, ItemCd);
//			/* 사용자 입력값 설정 시작 */ 
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item1);
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemString( item2);
//			RCaller.AddItemCount(1); 
//			RCaller.AddItemFraction(item3); 
//			/* 사용자 입력값 설정  */ 
//			if (!RCaller.MBRS_Call(0)){                                                    						
//				throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
//			}																		 									
//			byte resColTypes[] = new byte[RCaller.getColCount()];                     
//			for (int j = 0; j < RCaller.getColCount(); j++) {                            				
//				resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
//			}                     																						
//			ResultData    result=new ResultData();	
//			result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
//			for (int i = 0; i < RCaller.getRowCount(); i++) { 
//				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드재료폭구분
//				result.add( i , RCaller.ReadString() );	//야드재료폭구분
//			} 
//			table.put("YDB005_ColCnt", new Integer(resColTypes.length));					
//			table.put("YDB005", result);																					
//			if (result.size() == 0) {																								
//				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
//			}																															
//			return true;																										
//		} catch (Exception e) {																						
//			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
//		}																																						
//
//	}; // end of YDB005()
	
	
	
	/**
	 *	기준ID : YDB005
	 *  기준명 : 슬라브TO위치평점 
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2010-03-08 19:36:50)
	 * @param	item1	Slab산적Lot코드
	 * @param	item2	야드재료폭구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB005[0] :야드위치검색범위순서
	 *			<li>YDB005_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
    public static boolean YDB005(Hashtable table,
                        String item1, // Slab산적Lot코드
                        String item2 // 야드재료폭구분
                      ) throws RuleException {     
        //Vector vt = new Vector();
        //int rc = 0;

        RAPI4J  RCaller=null;
        try {
           RCaller = new RAPI4J( false,  "" );
            RCaller.Initialize("YDB005");
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
                //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadInt() );	//야드위치검색범위순서
                result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드위치검색범위순서
            } 
            table.put("YDB005_ColCnt", new Integer(resColTypes.length));					
            table.put("YDB005", result);																					
            if (result.size() == 0) {																								
                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
            }																															
            return true;																										
        } catch (Exception e) {																						
               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
        }																																						
         																																
    }
	

	/**
	 * YDB006 : 야드슬라브적치두께구분부여기준
	 * 
	 * @작성 날짜: (2008-10-14 15:28:45)
	 * @param	item1	야드구분
	 * @param	item2	주문여재구분
	 * @param	item3	재료두께
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB006[0] :야드재료두께구분
	 *			<li>YDB006_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB006(Hashtable table,
			String item1, // 야드구분
			String item2, // 주문여재구분
			double item3 // 재료두께
	) throws RuleException {     
		Vector vt = new Vector();
		int rc = 0;

		byte[] ItemType = {
				2,
				2,
				1
		};
		int[] ItemCd = {
				499,
				489,
				509
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB006", 3, ItemType, ItemCd);
			/* 사용자 입력값 설정 시작 */ 
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item1);
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item2);
			RCaller.AddItemCount(1); 
			RCaller.AddItemFraction(item3); 
			/* 사용자 입력값 설정  */ 
			if (!RCaller.MBRS_Call(0)){                                                    						
				throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
			}																		 									
			byte resColTypes[] = new byte[RCaller.getColCount()];                     
			for (int j = 0; j < RCaller.getColCount(); j++) {                            				
				resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
			}                     																						
			ResultData    result=new ResultData();	
			result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
			for (int i = 0; i < RCaller.getRowCount(); i++) { 
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드재료두께구분
				result.add( i , RCaller.ReadString() );	//야드재료두께구분
			} 
			table.put("YDB006_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB006", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	}; // end of YDB006()
	
	
	
	
	
	/**
	 * YDB007 : 야드후판제품적치길이구분부여기준
	 * 
	 * @작성 날짜: (2008-10-14 15:29:08)
	 * @param	item1	야드구분
	 * @param	item2	주문여재구분
	 * @param	item3	제품등급
	 * @param	item4	수주구분
	 * @param	item5	야드재료길이
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB007[0] :야드재료길이구분
	 *			<li>YDB007_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB007(Hashtable table,
			String item1, // 야드구분
			String item2, // 주문여재구분
			String item3, // 제품등급
			String item4, // 수주구분
			int item5 // 야드재료길이
	) throws RuleException {     
		Vector vt = new Vector();
		int rc = 0;

		byte[] ItemType = {
				2,
				2,
				2,
				2,
				3
		};
		int[] ItemCd = {
				499,
				489,
				505,
				506,
				510
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB007", 5, ItemType, ItemCd);
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
			RCaller.AddItemInt(  item5);
			/* 사용자 입력값 설정  */ 
			if (!RCaller.MBRS_Call(0)){                                                    						
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
				result.add( i , RCaller.ReadString() );	//야드재료길이구분
			} 
			table.put("YDB007_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB007", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	}; // end of YDB007()
	
	
	
	
	
	/**
	 * YDB008 :  야드후판제품적치폭구분부여기준
	 * 
	 * @작성 날짜: (2008-10-14 15:29:30)
	 * @param	item1	야드구분
	 * @param	item2	주문여재구분
	 * @param	item3	제품등급
	 * @param	item4	수주구분
	 * @param	item5	야드재료폭
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB008[0] :야드재료폭구분
	 *			<li>YDB008_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB008(Hashtable table,
			String item1, // 야드구분
			String item2, // 주문여재구분
			String item3, // 제품등급
			String item4, // 수주구분
			double item5 // 야드재료폭
	) throws RuleException {     
		Vector vt = new Vector();
		int rc = 0;

		byte[] ItemType = {
				2,
				2,
				2,
				2,
				1
		};
		int[] ItemCd = {
				499,
				489,
				505,
				506,
				508
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB008", 5, ItemType, ItemCd);
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
			RCaller.AddItemFraction(item5); 
			/* 사용자 입력값 설정  */ 
			if (!RCaller.MBRS_Call(0)){                                                    						
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
				result.add( i , RCaller.ReadString() );	//야드재료폭구분
			} 
			table.put("YDB008_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB008", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	}; // end of YDB008()   
	
	
	
	
	
	/**
	 * YDB009 : 야드코일제품적치외경구분부여기준
	 * 
	 * @작성 날짜: (2008-10-14 15:29:54)
	 * @param	item1	야드구분
	 * @param	item2	주문여재구분
	 * @param	item3	진도코드
	 * @param	item4	수주구분
	 * @param	item5	COIL외경
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB009[0] :야드코일외경군구분
	 *			<li>YDB009_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB009(Hashtable table,
			String item1, // 야드구분
			String item2, // 주문여재구분
			String item3, // 진도코드
			String item4, // 수주구분
			int item5 // COIL외경
	) throws RuleException {     
		Vector vt = new Vector();
		int rc = 0;

		byte[] ItemType = {
				2,
				2,
				2,
				2,
				3
		};
		int[] ItemCd = {
				499,
				489,
				512,
				506,
				513
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB009", 5, ItemType, ItemCd);
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
			RCaller.AddItemInt(  item5);
			/* 사용자 입력값 설정  */ 
			if (!RCaller.MBRS_Call(0)){                                                    						
				throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
			}																		 									
			byte resColTypes[] = new byte[RCaller.getColCount()];                     
			for (int j = 0; j < RCaller.getColCount(); j++) {                            				
				resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
			}                     																						
			ResultData    result=new ResultData();	
			result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
			for (int i = 0; i < RCaller.getRowCount(); i++) { 
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드코일외경군구분
				result.add( i , RCaller.ReadString() );	//야드코일외경군구분
			} 
			table.put("YDB009_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB009", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	}; // end of YDB009()
	
	/**
	 *
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2010-03-19 14:39:44)
	 * @param	item1	야드재료상태
	 * @param	item2	적치단
	 * @param	item3	Scarfing지시좌
	 * @param	item4	Scarfing지시우
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB010[0] :평가기본점수
	 *			<li>YDB010_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean YDB010(Hashtable table,
	                        String item1, // 야드재료상태
	                        String item2, // 적치단
	                        String item3, // Scarfing지시좌
	                        String item4 // Scarfing지시우
	                      ) throws RuleException {     
	        Vector vt = new Vector();
	        int rc = 0;

	        RAPI4J  RCaller=null;
	        try {
	           RCaller = new RAPI4J( false,  "" );
	            RCaller.Initialize("YDB010");
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
	                //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//평가기본점수
	                result.add( i ,RCaller.ReadString() );	//평가기본점수
	            } 
	            table.put("YDB010_ColCnt", new Integer(resColTypes.length));					
	            table.put("YDB010", result);																					
	            if (result.size() == 0) {																								
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	         																																
	    };  //end of YDB010()    



//	-----------------------------------------------------------------------------
} // end of class
