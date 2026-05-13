package com.inisteel.cim.yd.common.rule;


import java.util.Hashtable;
import java.util.Vector;

import jspeed.base.record.JDTORecord;

import com.metis.rapi4j.RAPI4J;
import com.metis.rapi4j.ResultData;
import com.metis.rapi4j.RuleException;

import com.metis.rapi5j.*; 
import java.util.*; 

public class BRERule1 {
	/**
	 * YDB010 : 야드SLAB행선부여기준
	 *
	 * @작성 날짜: (2008-10-14 15:30:21)
	 * @param	item1	Slab지시행선구분
	 * @param	item2	재료외형구분
	 * @param	item3	현재진도코드
	 * @param	item4	주문여재구분
	 * @param	item5	Scarfing여부
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB010[0] :야드적치행선구분
	 *			<li>YDB010_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB010(Hashtable table,
			String item1, // Slab지시행선구분
			String item2, // 재료외형구분
			String item3, // 현재진도코드
			String item4, // 주문여재구분
			String item5 // Scarfing여부
	) throws RuleException {     
		Vector vt = new Vector();
		int rc = 0;

		byte[] ItemType = {
				2,
				2,
				2,
				2,
				2
		};
		int[] ItemCd = {
				602,
				346,
				603,
				489,
				604
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB010", 5, ItemType, ItemCd);
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
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드적치행선구분
				result.add( i , RCaller.ReadString() );	//야드적치행선구분
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

	}; // end of YDB010()
	
	
	
	
	
	/**
	 * YDB011 : 야드COIL행선부여기준
	 *
	 * @작성 날짜: (2008-10-14 15:30:46)
	 * @param	item1	공장구분
	 * @param	item2	공장공정코드
	 * @param	item3	현재진도코드
	 * @param	item4	주문여재구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB011[0] :야드적치행선구분
	 *			<li>YDB011_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB011(Hashtable table,
			String item1, // 공장구분
			String item2, // 공장공정코드
			String item3, // 현재진도코드
			String item4 // 주문여재구분
	) throws RuleException {     
		Vector vt = new Vector();
		int rc = 0;

		byte[] ItemType = {
				2,
				2,
				2,
				2
		};
		int[] ItemCd = {
				276,
				601,
				603,
				489
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB011", 4, ItemType, ItemCd);
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
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드적치행선구분
				result.add( i , RCaller.ReadString() );	//야드적치행선구분
			} 
			table.put("YDB011_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB011", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	}; // end of YDB011()
	
	
	
	
	
	/**
	 * YDB012 : 야드PLATE행선부여기준
	 * 
	 * @작성 날짜: (2008-10-14 15:31:05)
	 * @param	item1	공장구분
	 * @param	item2	공장공정코드
	 * @param	item3	현재진도코드
	 * @param	item4	주문여재구분
	 * @param	item5	제품주등급
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB012[0] :야드적치행선구분
	 *			<li>YDB012_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB012(Hashtable table,
			String item1, // 공장구분
			String item2, // 공장공정코드
			String item3, // 현재진도코드
			String item4, // 주문여재구분
			String item5 // 제품주등급
	) throws RuleException {     
		Vector vt = new Vector();
		int rc = 0;

		byte[] ItemType = {
				2,
				2,
				2,
				2,
				2
		};
		int[] ItemCd = {
				276,
				601,
				603,
				489,
				605
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB012", 5, ItemType, ItemCd);
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
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드적치행선구분
				result.add( i , RCaller.ReadString() );	//야드적치행선구분
			} 
			table.put("YDB012_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB012", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	}; // end of YDB012()
	
	
	
	
	
	/**
	 * YDB013 : 야드SLAB스케줄코드부여기준
	 * 
	 * @작성 날짜: (2008-10-14 15:31:51)
	 * @param	item1	설비구분
	 * @param	item2	야드적치행선구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB013[0] :Schedule코드
	 *			<li>YDB013_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB013(Hashtable table,
			String item1, // 설비구분
			String item2 // 야드적치행선구분
	) throws RuleException {     
		Vector vt = new Vector();
		int rc = 0;

		byte[] ItemType = {
				2,
				2
		};
		int[] ItemCd = {
				609,
				606
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB013", 2, ItemType, ItemCd);
			/* 사용자 입력값 설정 시작 */ 
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item1);
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item2);
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
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//Schedule코드
				result.add( i , RCaller.ReadString() );	//Schedule코드
			} 
			table.put("YDB013_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB013", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	}; // end of YDB013()
	
	
	
	
	
	/**
	 * YDB014 : 야드COIL스케줄코드부여기준
	 * 
	 * @작성 날짜: (2008-10-14 15:32:07)
	 * @param	item1	설비구분
	 * @param	item2	야드적치행선구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB014[0] :Schedule코드
	 *			<li>YDB014_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB014(Hashtable table,
			String item1, // 설비구분
			String item2 // 야드적치행선구분
	) throws RuleException {     
		Vector vt = new Vector();
		int rc = 0;

		byte[] ItemType = {
				2,
				2
		};
		int[] ItemCd = {
				609,
				606
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB014", 2, ItemType, ItemCd);
			/* 사용자 입력값 설정 시작 */ 
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item1);
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item2);
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
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//Schedule코드
				result.add( i , RCaller.ReadString() );	//Schedule코드
			} 
			table.put("YDB014_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB014", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	}; // end of YDB014()
	
	
	
	
	
	/**
	 * YDB015 : 야드PLATE스케줄코드부여기준
	 * 
	 * @작성 날짜: (2008-10-14 15:32:24)
	 * @param	item1	설비구분
	 * @param	item2	야드적치행선구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB015[0] :Schedule코드
	 *			<li>YDB015_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB015(Hashtable table,
			String item1, // 설비구분
			String item2 // 야드적치행선구분
	) throws RuleException {     
		Vector vt = new Vector();
		int rc = 0;

		byte[] ItemType = {
				2,
				2
		};
		int[] ItemCd = {
				609,
				606
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB015", 2, ItemType, ItemCd);
			/* 사용자 입력값 설정 시작 */ 
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item1);
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item2);
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
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//Schedule코드
				result.add( i , RCaller.ReadString() );	//Schedule코드
			} 
			table.put("YDB015_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB015", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	}; // end of YDB015()
	
	
	
	
	
	/**
	 * YDB018 : 야드PLATE차량통로부여기준
	 * 
	 * @작성 날짜: (2008-10-14 15:32:50)
	 * @param	item1	야드구분
	 * @param	item2	동구분
	 * @param	item3	구역구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB018[0] :야드차량통로구분
	 *			<li>YDB018_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB018(Hashtable table,
			String item1, // 야드구분
			String item2, // 동구분
			String item3 // 구역구분
	) throws RuleException {     
		Vector vt = new Vector();
		int rc = 0;

		byte[] ItemType = {
				2,
				2,
				2
		};
		int[] ItemCd = {
				499,
				502,
				671
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB018", 3, ItemType, ItemCd);
			/* 사용자 입력값 설정 시작 */ 
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item1);
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item2);
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item3);
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
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드차량통로구분
				result.add( i , RCaller.ReadString() );	//야드차량통로구분
			} 
			table.put("YDB018_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB018", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	}; // end of YDB018()
	
	
	
	
	
	/**
	 * YDB019 : 야드SLAB차량Point부여기준
	 * 
	 * @작성 날짜: (2008-10-14 15:33:08)
	 * @param	item1	야드구분
	 * @param	item2	야드차량통로구분
	 * @param	item3	동구분
	 * @param	item4	재료진도코드
	 * @param	item5	야드차량사용구분
	 * @param	item6	설비종류
	 * @param	item7	적재상태
	 * @param	item8	열구분
	 * @param	item9	적치열활성상태
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB019[0] :야드포인트코드
	 *			<li>YDB019_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB019(Hashtable table,
			String item1, // 야드구분
			String item2, // 야드차량통로구분
			String item3, // 동구분
			String item4, // 재료진도코드
			String item5, // 야드차량사용구분
			String item6, // 설비종류
			String item7, // 적재상태
			String item8, // 열구분
			String item9 // 적치열활성상태
	) throws RuleException {     
		Vector vt = new Vector();
		int rc = 0;

		byte[] ItemType = {
				2,
				2,
				2,
				2,
				2,
				2,
				2,
				2,
				2
		};
		int[] ItemCd = {
				499,
				672,
				502,
				673,
				679,
				678,
				675,
				676,
				677
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB019", 9, ItemType, ItemCd);
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
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item6);
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item7);
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item8);
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item9);
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
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드포인트코드
				result.add( i , RCaller.ReadString() );	//야드포인트코드
			} 
			table.put("YDB019_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB019", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	}; // end of YDB019()
	
	/**
	 *	기준ID : YDB181
	 *  기준명 : C연주슬라브야드-대차스케줄기동기준 
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2010-02-24 13:41:00)
	 * @param	item1	야드작업대차
	 * @param	item2	운송작업영공구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB181[0] :야드스케쥴요청구분
	 *			<li>YDB181_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
    public boolean YDB181(Hashtable table,
                        String item1, // 야드작업대차
                        String item2 // 운송작업영공구분
                      ) throws RuleException {     
        //Vector vt = new Vector();
        //int rc = 0;

        RAPI4J  RCaller=null;
        try {
           RCaller = new RAPI4J( false,  "" );
            RCaller.Initialize("YDB181");
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
                //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드스케쥴요청구분
                result.add( i ,RCaller.ReadString() );	//야드스케쥴요청구분
            } 
            table.put("YDB181_ColCnt", new Integer(resColTypes.length));					
            table.put("YDB181", result);																					
            if (result.size() == 0) {																								
                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
            }																															
            return true;																										
        } catch (Exception e) {																						
               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
        }																																						
         																																
    }
    
    
    /**
    *	기준ID : YDB182
    *  	기준명 : C연주슬라브야드-대차작업지정기준 
    **import com.metis.rapi4j.*; 
    **import java.util.*; 
    * item코드허용값 :
    * @작성 날짜: (2010-02-25 12:12:26)
    * @param	item1	야드작업대차
    * @param	table 인수값 혹은 결과값(리턴정보)
    *		<ul>
    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
    *			<li>YDB182[0] :사용여부
    *			<li>YDB182[1] :작업구분
    *			<li>YDB182[2] :야드상차동구분
    *			<li>YDB182[3] :야드하차동구분
    *			<li>YDB182_ColCnt :4
    *		<ul>
    * @return 정상처리 여부
    */
   public boolean YDB182(Hashtable table,
                       String item1 // 야드작업대차
                     ) throws RuleException {     
       //Vector vt = new Vector();
       //int rc = 0;

       RAPI4J  RCaller=null;
       try {
          RCaller = new RAPI4J( false,  "" );
           RCaller.Initialize("YDB182");
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
               //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//사용여부
               result.add( i ,RCaller.ReadString() );	//사용여부
               //System.out.println("  ROW[" + i + "] COL[2]:"+ RCaller.ReadString());	//작업구분
               result.add( i ,RCaller.ReadString() );	//작업구분
               //System.out.println("  ROW[" + i + "] COL[3]:"+ RCaller.ReadString());	//야드상차동구분
               result.add( i ,RCaller.ReadString() );	//야드상차동구분
               //System.out.println("  ROW[" + i + "] COL[4]:"+ RCaller.ReadString());	//야드하차동구분
               result.add( i ,RCaller.ReadString() );	//야드하차동구분
           } 
           table.put("YDB182_ColCnt", new Integer(resColTypes.length));					
           table.put("YDB182", result);																					
           if (result.size() == 0) {																								
               table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
           }																															
           return true;																										
       } catch (Exception e) {																						
              	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
       }																																						
        																																
   }
   
   /**
    *	기준ID : YDB183
    *  기준명 : C연주슬라브야드-이적권하분리여부 
    **import com.metis.rapi4j.*; 
    **import java.util.*; 
    * item코드허용값 :
    * @작성 날짜: (2010-03-11 13:28:11)
    * @param	table 인수값 혹은 결과값(리턴정보)
    *		<ul>
    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
    *			<li>YDB183[0] :사용여부
    *			<li>YDB183_ColCnt :1
    *		<ul>
    * @return 정상처리 여부
    */
       public boolean YDB183(Hashtable table) throws RuleException {     
           //Vector vt = new Vector();
           //int rc = 0;

           RAPI4J  RCaller=null;
           try {
              RCaller = new RAPI4J( false,  "" );
               RCaller.Initialize("YDB183");
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
                   //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//사용여부
                   result.add( i ,RCaller.ReadString() );	//사용여부
               } 
               table.put("YDB183_ColCnt", new Integer(resColTypes.length));					
               table.put("YDB183", result);																					
               if (result.size() == 0) {																								
                   table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
               }																															
               return true;																										
           } catch (Exception e) {																						
                  	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
           }																																						
            																																
       }
	
	
	/**
	 *	기준ID : YDB192
	 *  기준명 : C연주슬라브야드-차량정지위치기본동
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2010-01-26 18:00:08)
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB192[0] :야드동구분
	 *			<li>YDB192_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean YDB192(Hashtable table) throws RuleException {     
	        //Vector vt = new Vector();
	        //int rc = 0;

	        RAPI4J  RCaller=null;
	        try {
	           RCaller = new RAPI4J( false,  "" );
	            RCaller.Initialize("YDB192");
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
	                //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드동구분
	                result.add( i ,RCaller.ReadString() );	//야드동구분
	            } 
	            table.put("YDB192_ColCnt", new Integer(resColTypes.length));					
	            table.put("YDB192", result);																					
	            if (result.size() == 0) {																								
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	         																																
	    }
	
	/**
	 * 기준ID : YDB193
	 * 기준명 : C연주슬라브야드-Pallet상차매수
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2010-01-18 14:45:05)
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB193[0] :야드설비작업매수
	 *			<li>YDB193_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
    public boolean YDB193(Hashtable table) throws RuleException {     
        //Vector vt = new Vector();
        //int rc = 0;

        RAPI4J  RCaller=null;
        try {
           RCaller = new RAPI4J( false,  "" );
            RCaller.Initialize("YDB193");
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
                //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadInt() );	//야드설비작업매수
                result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드설비작업매수
            } 
            table.put("YDB193_ColCnt", new Integer(resColTypes.length));					
            table.put("YDB193", result);																					
            if (result.size() == 0) {																								
                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
            }																															
            return true;																										
        } catch (Exception e) {																						
               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
        }																																						
         																																
    }

    /**
    * 기준ID : YDB193
	* 기준명 : C연주슬라브야드-Trailer상차매수
    **import com.metis.rapi4j.*; 
    **import java.util.*; 
    * item코드허용값 :
    * @작성 날짜: (2010-01-18 14:46:52)
    * @param	table 인수값 혹은 결과값(리턴정보)
    *		<ul>
    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
    *			<li>YDB194[0] :야드설비작업매수
    *			<li>YDB194_ColCnt :1
    *		<ul>
    * @return 정상처리 여부
    */
   public boolean YDB194(Hashtable table) throws RuleException {     
       //Vector vt = new Vector();
       //int rc = 0;

       RAPI4J  RCaller=null;
       try {
          RCaller = new RAPI4J( false,  "" );
           RCaller.Initialize("YDB194");
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
               //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadInt() );	//야드설비작업매수
               result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드설비작업매수
           } 
           table.put("YDB194_ColCnt", new Integer(resColTypes.length));					
           table.put("YDB194", result);																					
           if (result.size() == 0) {																								
               table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
           }																															
           return true;																										
       } catch (Exception e) {																						
              	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
       }																																						
        																																
   }
	
	/**
	 * 기준ID : YDB195
	 * 기준명 : C연주슬라브야드-정정보급자동여부
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2009-11-11 13:57:15)
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB195[0] :자동지시유무
	 *			<li>YDB195_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean YDB195(Hashtable table) throws RuleException {     
	        Vector vt = new Vector();
	        int rc = 0;

	        RAPI4J  RCaller=null;
	        try {
	           RCaller = new RAPI4J( false,  "" );
	            RCaller.Initialize("YDB195");
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
	                //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//자동지시유무
	                result.add( i ,RCaller.ReadString() );	//자동지시유무
	            } 
	            table.put("YDB195_ColCnt", new Integer(resColTypes.length));					
	            table.put("YDB195", result);																					
	            if (result.size() == 0) {																								
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	         																																
	    }
	
	/**
	 * 기준ID : YDB196
	 * 기준명 : C연주슬라브야드-스카핑보급자동여부
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2009-11-11 13:56:50)
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB196[0] :자동지시유무
	 *			<li>YDB196_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean YDB196(Hashtable table) throws RuleException {     
	        Vector vt = new Vector();
	        int rc = 0;

	        RAPI4J  RCaller=null;
	        try {
	           RCaller = new RAPI4J( false,  "" );
	            RCaller.Initialize("YDB196");
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
	                //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//자동지시유무
	                result.add( i ,RCaller.ReadString() );	//자동지시유무
	            } 
	            table.put("YDB196_ColCnt", new Integer(resColTypes.length));					
	            table.put("YDB196", result);																					
	            if (result.size() == 0) {																								
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	         																																
	    }
	
	/**
    * 기준ID : YDB197
	* 기준명 : C연주슬라브야드-정정보급LOT편성매수
    **import com.metis.rapi4j.*; 
    **import java.util.*; 
    * item코드허용값 :
    * @작성 날짜: (2009-11-11 13:53:48)
    * @param	table 인수값 혹은 결과값(리턴정보)
    *		<ul>
    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
    *			<li>YDB197[0] :야드설비작업매수
    *			<li>YDB197_ColCnt :1
    *		<ul>
    * @return 정상처리 여부
    */
       public boolean YDB197(Hashtable table) throws RuleException {     
           Vector vt = new Vector();
           int rc = 0;

           RAPI4J  RCaller=null;
           try {
              RCaller = new RAPI4J( false,  "" );
               RCaller.Initialize("YDB197");
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
                   //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadInt() );	//야드설비작업매수
                   result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드설비작업매수
               } 
               table.put("YDB197_ColCnt", new Integer(resColTypes.length));					
               table.put("YDB197", result);																					
               if (result.size() == 0) {																								
                   table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
               }																															
               return true;																										
           } catch (Exception e) {																						
                  	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
           }																																						
            																																
       }

	/**
    * 기준ID : YDB198
	* 기준명 : C연주슬라브야드-스카핑보급LOT편성매수
    **import com.metis.rapi4j.*; 
    **import java.util.*; 
    * item코드허용값 :
    * @작성 날짜: (2009-11-11 13:43:36)
    * @param	table 인수값 혹은 결과값(리턴정보)
    *		<ul>
    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
    *			<li>YDB198[0] :야드설비작업매수
    *			<li>YDB198_ColCnt :1
    *		<ul>
    * @return 정상처리 여부
    */
       public boolean YDB198(Hashtable table) throws RuleException {     
           Vector vt = new Vector();
           int rc = 0;

           RAPI4J  RCaller=null;
           try {
              RCaller = new RAPI4J( false,  "" );
               RCaller.Initialize("YDB198");
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
                   //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadInt() );	//야드설비작업매수
                   result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드설비작업매수
               } 
               table.put("YDB198_ColCnt", new Integer(resColTypes.length));					
               table.put("YDB198", result);																					
               if (result.size() == 0) {																								
                   table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
               }																															
               return true;																										
           } catch (Exception e) {																						
                  	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
           }																																						
            																																
       }
       
	/**
	 * 기준ID : YDB199
	 * 기준명 : C연주슬라브야드-차량LOT편성자동유무관리
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2009-08-26 12:47:18)
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB199[0] :사용유무
	 *			<li>YDB199_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean YDB199(Hashtable table) throws RuleException {     
	        Vector vt = new Vector();
	        int rc = 0;

	        RAPI4J  RCaller=null;
	        try {
	           RCaller = new RAPI4J( false,  "" );
	            RCaller.Initialize("YDB199");
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
	            table.put("YDB199_ColCnt", new Integer(resColTypes.length));					
	            table.put("YDB199", result);																					
	            if (result.size() == 0) {																								
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	         																																
	    }		//end of YDB199
	    
	    /**
	    *
	    import com.metis.rapi5j.*; 
	    import java.util.*; 
	    * item코드허용값 :
	    * @작성 날짜: (2022-03-16 14:45:20)
	    * @param	table 인수값 혹은 결과값(리턴정보)
	    *		<ul>
	    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
	    *			<li>YDB192[0] :야드동구분
	    *			<li>YDB192_ColCnt :1
	    *		<ul>
	    * @return 정상처리 여부
	    */
	    public boolean YDB192_NEW(Hashtable table) throws com.metis.rapi5j.RuleException {     
	           Vector vt = new Vector();
	           int rc = 0;

	           RAPI5J  RCaller=new RAPI5J() ;
	           RCaller.Initialize("YDB192");
	           /* 사용자 입력값 설정 시작 */ 
	           /* 사용자 입력값 설정  */ 
	          try{                                                    						
	               RCaller.MBRS_Run();                                                      
	               ResultData    result=new ResultData();	
	               result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
	               for (int i = 0; i < RCaller.getRowCount(); i++) { 
	                   result.add( i , RCaller.ReadString() );	//야드동구분
	               } 
	               table.put("YDB192_ColCnt", new Integer( RCaller.getColCount() ));					
	               table.put("YDB192", result);																					
	               if (result.size() == 0) {
	                   table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");
	               }																															
	               return true;																										
	           } catch (Exception e) {																						
	                throw new com.metis.rapi5j.RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	           }																																						
	       }     //end of YDB192_NEW
	    
	    /**
	    *
	    import com.metis.rapi5j.*; 
	    import java.util.*; 
	    * item코드허용값 :
	    * @작성 날짜: (2022-03-16 15:08:06)
	    * @param	table 인수값 혹은 결과값(리턴정보)
	    *		<ul>
	    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
	    *			<li>YDB193[0] :야드설비작업매수
	    *			<li>YDB193_ColCnt :1
	    *		<ul>
	    * @return 정상처리 여부
	    */
	       public boolean YDB193_NEW(Hashtable table
	                         ) throws com.metis.rapi5j.RuleException {     
	           Vector vt = new Vector();
	           int rc = 0;

	           RAPI5J  RCaller=new RAPI5J() ;
	           RCaller.Initialize("YDB193");
	           /* 사용자 입력값 설정 시작 */ 
	           /* 사용자 입력값 설정  */ 
	          try{                                                    						
	               RCaller.MBRS_Run();                                                      
	               ResultData    result=new ResultData();	
	               result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
	               for (int i = 0; i < RCaller.getRowCount(); i++) { 
	                   result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드설비작업매수
	               } 
	               table.put("YDB193_ColCnt", new Integer( RCaller.getColCount() ));					
	               table.put("YDB193", result);																					
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
	       * @작성 날짜: (2022-03-16 15:08:54)
	       * @param	table 인수값 혹은 결과값(리턴정보)
	       *		<ul>
	       *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
	       *			<li>YDB194[0] :야드설비작업매수
	       *			<li>YDB194_ColCnt :1
	       *		<ul>
	       * @return 정상처리 여부
	       */
	          public boolean YDB194_NEW(Hashtable table
	                            ) throws com.metis.rapi5j.RuleException {     
	              Vector vt = new Vector();
	              int rc = 0;

	              RAPI5J  RCaller=new RAPI5J() ;
	              RCaller.Initialize("YDB194");
	              /* 사용자 입력값 설정 시작 */ 
	              /* 사용자 입력값 설정  */ 
	             try{                                                    						
	                  RCaller.MBRS_Run();                                                      
	                  ResultData    result=new ResultData();	
	                  result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
	                  for (int i = 0; i < RCaller.getRowCount(); i++) { 
	                      result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드설비작업매수
	                  } 
	                  table.put("YDB194_ColCnt", new Integer( RCaller.getColCount() ));					
	                  table.put("YDB194", result);																					
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
	          * @작성 날짜: (2022-03-16 15:12:49)
	          * @param	table 인수값 혹은 결과값(리턴정보)
	          *		<ul>
	          *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
	          *			<li>YDB199[0] :사용유무
	          *			<li>YDB199_ColCnt :1
	          *		<ul>
	          * @return 정상처리 여부
	          */
	             public boolean YDB199_NEW(Hashtable table
	                               ) throws com.metis.rapi5j.RuleException {     
	                 Vector vt = new Vector();
	                 int rc = 0;

	                 RAPI5J  RCaller=new RAPI5J() ;
	                 RCaller.Initialize("YDB199");
	                 /* 사용자 입력값 설정 시작 */ 
	                 /* 사용자 입력값 설정  */ 
	                try{                                                    						
	                     RCaller.MBRS_Run();                                                      
	                     ResultData    result=new ResultData();	
	                     result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
	                     for (int i = 0; i < RCaller.getRowCount(); i++) { 
	                         result.add( i , RCaller.ReadString() );	//사용유무
	                     } 
	                     table.put("YDB199_ColCnt", new Integer( RCaller.getColCount() ));					
	                     table.put("YDB199", result);																					
	                     if (result.size() == 0) {
	                         table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");
	                     }																															
	                     return true;																										
	                 } catch (Exception e) {																						
	                      throw new com.metis.rapi5j.RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	                 }																																						
	             }     

	    

	    
//	-----------------------------------------------------------------------------
} // end of class
