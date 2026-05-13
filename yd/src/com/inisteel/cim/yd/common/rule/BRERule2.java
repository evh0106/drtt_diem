package com.inisteel.cim.yd.common.rule;


import java.util.Hashtable;
import java.util.Vector;

import com.metis.rapi4j.RAPI4J;
import com.metis.rapi4j.ResultData;
import com.metis.rapi4j.RuleException;

import com.metis.rapi5j.*; 
import java.util.*; 

public class BRERule2 {
	/**
	 * YDB020 : 야드COIL차량Point부여기준
	 * 
	 * @작성 날짜: (2008-10-14 15:33:28)
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
	 *			<li>YDB020[0] :야드포인트코드
	 *			<li>YDB020_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB020(Hashtable table,
			String item1, // 야드구분
			String item2, // 야드차량통로구분
			String item3, // 동구분
			String item4, // 재료진도코드
			String item5, // 야드차량사용구분
			String item6, // 설비종류
			String item7, // 적재상태
			String item8, // 열구분
			String item9  // 적치열활성상태
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
			RCaller.Initialize("YDB020", 9, ItemType, ItemCd);
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
			table.put("YDB020_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB020", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	}; // end of YDB020()
	
	
	
	
	
	/**
	 * YDB021 : 야드PLATE차량Point부여기준
	 * 
	 * @작성 날짜: (2008-10-14 15:34:03)
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
	 *			<li>YDB021[0] :야드포인트코드
	 *			<li>YDB021_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB021(Hashtable table,
			String item1, // 야드구분
			String item2, // 야드차량통로구분
			String item3, // 동구분
			String item4, // 재료진도코드
			String item5, // 야드차량사용구분
			String item6, // 설비종류
			String item7, // 적재상태
			String item8, // 열구분
			String item9  // 적치열활성상태
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
			RCaller.Initialize("YDB021", 9, ItemType, ItemCd);
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
			table.put("YDB021_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB021", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB021()
	
	
	
	
	/**
	 *
	 * YDB022 : 야드후판슬라브스케줄코드부여기준
	 *
	 * @작성 날짜: (2008-10-23 14:52:46)
	 * @param	item1	야드저장집합코드
	 * @param	item2	야드재료지시구분
	 * @param	item3	작업Mode
	 * @param	item4	Backup설비유무
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB022[0] :야드대상스케줄코드
	 *			<li>YDB022_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB022(Hashtable table,
			String item1, // 야드저장집합코드
			String item2, // 야드재료지시구분
			String item3, // 작업Mode
			String item4  // Backup설비유무
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
				714,
				715,
				716,
				717
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB022", 4, ItemType, ItemCd);
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
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드대상스케줄코드
				result.add( i , RCaller.ReadString() );	//야드대상스케줄코드
			} 
			table.put("YDB022_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB022", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};   // end of YDB022()




	/**
	 *
	 * YDB023 : 야드B열연슬라브스케줄코드부여기준
	 *
	 * @작성 날짜: (2008-10-23 14:54:10)
	 * @param	item1	야드저장집합코드
	 * @param	item2	야드재료지시구분
	 * @param	item3	작업Mode
	 * @param	item4	Backup설비유무
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB023[0] :야드대상스케줄코드
	 *			<li>YDB023_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB023(Hashtable table,
			String item1, // 야드저장집합코드
			String item2, // 야드재료지시구분
			String item3, // 작업Mode
			String item4  // Backup설비유무
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
				714,
				715,
				716,
				717
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB023", 4, ItemType, ItemCd);
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
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드대상스케줄코드
				result.add( i , RCaller.ReadString() );	//야드대상스케줄코드
			} 
			table.put("YDB023_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB023", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB023()




	/**
	 *
	 * YDB024 : 야드A열연코일스케줄코드부여기준
	 *
	 * @작성 날짜: (2008-10-23 14:54:38)
	 * @param	item1	야드저장집합코드
	 * @param	item2	야드재료지시구분
	 * @param	item3	작업Mode
	 * @param	item4	Backup설비유무
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB024[0] :야드대상스케줄코드
	 *			<li>YDB024_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB024(Hashtable table,
			String item1, // 야드저장집합코드
			String item2, // 야드재료지시구분
			String item3, // 작업Mode
			String item4  // Backup설비유무
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
				714,
				715,
				716,
				717
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB024", 4, ItemType, ItemCd);
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
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드대상스케줄코드
				result.add( i , RCaller.ReadString() );	//야드대상스케줄코드
			} 
			table.put("YDB024_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB024", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};   // end of YDB024()




	/**
	 *
	 * YDB025 : 야드B열연코일스케줄코드부여기준
	 *
	 * @작성 날짜: (2008-10-23 14:55:13)
	 * @param	item1	야드저장집합코드
	 * @param	item2	야드재료지시구분
	 * @param	item3	작업Mode
	 * @param	item4	Backup설비유무
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB025[0] :야드대상스케줄코드
	 *			<li>YDB025_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB025(Hashtable table,
			String item1, // 야드저장집합코드
			String item2, // 야드재료지시구분
			String item3, // 작업Mode
			String item4  // Backup설비유무
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
				714,
				715,
				716,
				717
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB025", 4, ItemType, ItemCd);
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
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드대상스케줄코드
				result.add( i , RCaller.ReadString() );	//야드대상스케줄코드
			} 
			table.put("YDB025_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB025", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB025()




	/**
	 *
	 * YDB026 : 야드C열연코일스케줄코드부여기준
	 *
	 * @작성 날짜: (2008-10-23 14:55:41)
	 * @param	item1	야드저장집합코드
	 * @param	item2	야드재료지시구분
	 * @param	item3	작업Mode
	 * @param	item4	Backup설비유무
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB026[0] :야드대상스케줄코드
	 *			<li>YDB026_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB026(Hashtable table,
			String item1, // 야드저장집합코드
			String item2, // 야드재료지시구분
			String item3, // 작업Mode
			String item4  // Backup설비유무
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
				714,
				715,
				716,
				717
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB026", 4, ItemType, ItemCd);
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
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드대상스케줄코드
				result.add( i , RCaller.ReadString() );	//야드대상스케줄코드
			} 
			table.put("YDB026_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB026", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB026()




	/**
	 *
	 * YDB027 : 야드후판제품스케줄코드부여기준
	 *
	 * @작성 날짜: (2008-10-23 14:56:06)
	 * @param	item1	야드저장집합코드
	 * @param	item2	야드재료지시구분
	 * @param	item3	작업Mode
	 * @param	item4	Backup설비유무
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB027[0] :야드대상스케줄코드
	 *			<li>YDB027_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB027(Hashtable table,
			String item1, // 야드저장집합코드
			String item2, // 야드재료지시구분
			String item3, // 작업Mode
			String item4  // Backup설비유무
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
				714,
				715,
				716,
				717
		};

		RAPI4J  RCaller=null;
		try {
			RCaller = new RAPI4J( false,  "" );
			RCaller.Initialize("YDB027", 4, ItemType, ItemCd);
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
				//System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//야드대상스케줄코드
				result.add( i , RCaller.ReadString() );	//야드대상스케줄코드
			} 
			table.put("YDB027_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB027", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};   // end of YDB027()




	/**
	 *
	 * YDB028 : 야드A열연코일차량Point부여기준
	 *
	 * @작성 날짜: (2008-10-23 14:56:49)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB028[0] :야드포인트코드
	 *			<li>YDB028_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB028(Hashtable table,
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
			RCaller.Initialize("YDB028", 1, ItemType, ItemCd);
			/* 사용자 입력값 설정 시작 */ 
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item1);
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
			table.put("YDB028_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB028", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB028()




	/**
	 *
	 * YDB029 : 야드B열연코일차량Point부여기준
	 * 
	 * @작성 날짜: (2008-10-23 14:57:21)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB029[0] :야드포인트코드
	 *			<li>YDB029_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB029(Hashtable table,
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
			RCaller.Initialize("YDB029", 1, ItemType, ItemCd);
			/* 사용자 입력값 설정 시작 */ 
			RCaller.AddItemCount(1); 
			RCaller.AddItemString( item1);
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
			table.put("YDB029_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB029", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};    // end of YDB029()
	
	/**
	 *	기준ID : YDB295
	 *  기준명 : A후판슬라브야드-이적권하분리여부 
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2010-03-11 13:43:42)
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB295[0] :사용여부
	 *			<li>YDB295_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean YDB295(Hashtable table) throws RuleException {     
	        //Vector vt = new Vector();
	        //int rc = 0;

	        RAPI4J  RCaller=null;
	        try {
	           RCaller = new RAPI4J( false,  "" );
	            RCaller.Initialize("YDB295");
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
	            table.put("YDB295_ColCnt", new Integer(resColTypes.length));					
	            table.put("YDB295", result);																					
	            if (result.size() == 0) {																								
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	         																																
	    }
	
	/**
	 * 기준ID : YDB296
	 * 기준명 : A후판슬라브야드-Pallet상차매수
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2010-01-18 14:48:33)
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB296[0] :야드설비작업매수
	 *			<li>YDB296_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
    public boolean YDB296(Hashtable table) throws RuleException {     
        //Vector vt = new Vector();
        //int rc = 0;

        RAPI4J  RCaller=null;
        try {
           RCaller = new RAPI4J( false,  "" );
            RCaller.Initialize("YDB296");
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
            table.put("YDB296_ColCnt", new Integer(resColTypes.length));					
            table.put("YDB296", result);																					
            if (result.size() == 0) {																								
                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
            }																															
            return true;																										
        } catch (Exception e) {																						
               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
        }																																						
         																																
    }
    
    /**
    * 기준ID : YDB297
	* 기준명 : A후판슬라브야드-Trailer상차매수
    **import com.metis.rapi4j.*; 
    **import java.util.*; 
    * item코드허용값 :
    * @작성 날짜: (2010-01-18 14:49:57)
    * @param	table 인수값 혹은 결과값(리턴정보)
    *		<ul>
    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
    *			<li>YDB297[0] :야드설비작업매수
    *			<li>YDB297_ColCnt :1
    *		<ul>
    * @return 정상처리 여부
    */
   public boolean YDB297(Hashtable table) throws RuleException {     
       //Vector vt = new Vector();
       //int rc = 0;

       RAPI4J  RCaller=null;
       try {
          RCaller = new RAPI4J( false,  "" );
           RCaller.Initialize("YDB297");
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
           table.put("YDB297_ColCnt", new Integer(resColTypes.length));					
           table.put("YDB297", result);																					
           if (result.size() == 0) {																								
               table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
           }																															
           return true;																										
       } catch (Exception e) {																						
              	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
       }																																						
        																																
   }
	
	/**
	 * 기준ID : YDB298
	 * 기준명 : A후판슬라브야드-장입Deliper설정
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2010-01-14 15:35:22)
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB298[0] :편성매수
	 *			<li>YDB298[1] :야드적치Bed높이Max
	 *			<li>YDB298[2] :야드적치Bed중량Max
	 *			<li>YDB298[3] :야드적치Bed단Max
	 *			<li>YDB298_ColCnt :4
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean YDB298(Hashtable table
	                      ) throws RuleException {     
	        Vector vt = new Vector();
	        int rc = 0;

	        RAPI4J  RCaller=null;
	        try {
	           RCaller = new RAPI4J( false,  "" );
	            RCaller.Initialize("YDB298");
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
	                //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadInt() );	//편성매수
	                result.add(  i ,new Integer(RCaller.ReadInt()) );	//편성매수
	                //System.out.println("  ROW[" + i + "] COL[2]:"+ RCaller.ReadInt() );	//야드적치Bed높이Max
	                result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드적치Bed높이Max
	                //System.out.println("  ROW[" + i + "] COL[3]:"+ RCaller.ReadInt() );	//야드적치Bed중량Max
	                result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드적치Bed중량Max
	                //System.out.println("  ROW[" + i + "] COL[4]:"+ RCaller.ReadInt() );	//야드적치Bed단Max
	                result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드적치Bed단Max
	            } 
	            table.put("YDB298_ColCnt", new Integer(resColTypes.length));					
	            table.put("YDB298", result);																					
	            if (result.size() == 0) {																								
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	         																																
	    };    




	/**
	 * 기준ID : YDB299
	 * 기준명 : A후판슬라브야드-차량LOT편성자동유무관리
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2009-08-26 12:48:48)
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB299[0] :사용유무
	 *			<li>YDB299_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean YDB299(Hashtable table) throws RuleException {     
	        Vector vt = new Vector();
	        int rc = 0;

	        RAPI4J  RCaller=null;
	        try {
	           RCaller = new RAPI4J( false,  "" );
	            RCaller.Initialize("YDB299");
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
	            table.put("YDB299_ColCnt", new Integer(resColTypes.length));					
	            table.put("YDB299", result);																					
	            if (result.size() == 0) {																								
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	         																																
	    }			//end of YDB299




	    /**
	    *
	    **import com.metis.rapi4j.*; 
	    **import java.util.*; 
	    * item코드허용값 :
	    * @작성 날짜: (2010-02-22 16:02:55)
	    * @param	table 인수값 혹은 결과값(리턴정보)
	    *		<ul>
	    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	    *			<li>YDB230[0] :사용유무
	    *			<li>YDB230_ColCnt :1
	    *		<ul>
	    * @return 정상처리 여부
	    */
	       public boolean YDB230(Hashtable table) throws RuleException {     
	           Vector vt = new Vector();
	           int rc = 0;

	           RAPI4J  RCaller=null;
	           try {
	              RCaller = new RAPI4J( false,  "" );
	               RCaller.Initialize("YDB230");
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
	               table.put("YDB230_ColCnt", new Integer(resColTypes.length));					
	               table.put("YDB230", result);																					
	               if (result.size() == 0) {																								
	                   table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	               }																															
	               return true;																										
	           } catch (Exception e) {																						
	                  	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	           }																																						
	            																																
	       } // end of YDB230

	       /**
	       *
	       import com.metis.rapi5j.*; 
	       import java.util.*; 
	       * item코드허용값 :
	       * @작성 날짜: (2022-03-16 15:14:52)
	       * @param	table 인수값 혹은 결과값(리턴정보)
	       *		<ul>
	       *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
	       *			<li>YDB296[0] :야드설비작업매수
	       *			<li>YDB296_ColCnt :1
	       *		<ul>
	       * @return 정상처리 여부
	       */
	          public boolean YDB296_NEW(Hashtable table
	                            ) throws com.metis.rapi5j.RuleException {     
	              Vector vt = new Vector();
	              int rc = 0;

	              RAPI5J  RCaller=new RAPI5J() ;
	              RCaller.Initialize("YDB296");
	              /* 사용자 입력값 설정 시작 */ 
	              /* 사용자 입력값 설정  */ 
	             try{                                                    						
	                  RCaller.MBRS_Run();                                                      
	                  ResultData    result=new ResultData();	
	                  result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
	                  for (int i = 0; i < RCaller.getRowCount(); i++) { 
	                      result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드설비작업매수
	                  } 
	                  table.put("YDB296_ColCnt", new Integer( RCaller.getColCount() ));					
	                  table.put("YDB296", result);																					
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
	          * @작성 날짜: (2022-03-16 15:16:23)
	          * @param	table 인수값 혹은 결과값(리턴정보)
	          *		<ul>
	          *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
	          *			<li>YDB297[0] :야드설비작업매수
	          *			<li>YDB297_ColCnt :1
	          *		<ul>
	          * @return 정상처리 여부
	          */
	             public boolean YDB297_NEW(Hashtable table
	                               ) throws com.metis.rapi5j.RuleException {     
	                 Vector vt = new Vector();
	                 int rc = 0;

	                 RAPI5J  RCaller=new RAPI5J() ;
	                 RCaller.Initialize("YDB297");
	                 /* 사용자 입력값 설정 시작 */ 
	                 /* 사용자 입력값 설정  */ 
	                try{                                                    						
	                     RCaller.MBRS_Run();                                                      
	                     ResultData    result=new ResultData();	
	                     result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
	                     for (int i = 0; i < RCaller.getRowCount(); i++) { 
	                         result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드설비작업매수
	                     } 
	                     table.put("YDB297_ColCnt", new Integer( RCaller.getColCount() ));					
	                     table.put("YDB297", result);																					
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
