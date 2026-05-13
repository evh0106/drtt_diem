package com.inisteel.cim.yd.common.rule;


import java.util.Hashtable;
import java.util.Vector;

import com.metis.rapi4j.RAPI4J;
import com.metis.rapi4j.ResultData;
import com.metis.rapi4j.RuleException;

import com.metis.rapi5j.*; 
import java.util.*; 

public class BRERule3 {
	
	/**
	 *
	 * YDB030 : 야드C열연코일차량Point부여기준
	 * 
	 * @작성 날짜: (2008-10-23 15:05:57)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB030[0] :야드포인트코드
	 *			<li>YDB030_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB030(Hashtable table,
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
			RCaller.Initialize("YDB030", 1, ItemType, ItemCd);
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
			table.put("YDB030_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB030", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB030()




	/**
	 *
	 * YDB031 : 야드크레인스케줄상태부여기준
	 * 
	 * @작성 날짜: (2008-10-23 15:06:28)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB031[0] :야드포인트코드
	 *			<li>YDB031_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB031(Hashtable table,
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
			RCaller.Initialize("YDB031", 1, ItemType, ItemCd);
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
			table.put("YDB031_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB031", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};   // end of YDB031()




	/**
	 *
	 * YDB032 : 야드대차스케줄상태부여기준
	 * 
	 * @작성 날짜: (2008-10-23 15:06:49)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB032[0] :야드포인트코드
	 *			<li>YDB032_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB032(Hashtable table,
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
			RCaller.Initialize("YDB032", 1, ItemType, ItemCd);
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
			table.put("YDB032_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB032", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB032()




	/**
	 *
	 * YDB033 : 야드차량스케줄상태부여기준
	 * 
	 * @작성 날짜: (2008-10-23 15:07:15)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB033[0] :야드포인트코드
	 *			<li>YDB033_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB033(Hashtable table,
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
			RCaller.Initialize("YDB033", 1, ItemType, ItemCd);
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
			table.put("YDB033_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB033", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB033()




	/**
	 *
	 * YDB034 : 야드B열연슬라브장입준비기준
	 * 
	 * @작성 날짜: (2008-10-23 15:07:39)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB034[0] :야드포인트코드
	 *			<li>YDB034_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB034(Hashtable table,
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
			RCaller.Initialize("YDB034", 1, ItemType, ItemCd);
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
			table.put("YDB034_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB034", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB034()




	/**
	 *
	 * YDB035 : 야드C열연슬라브장입준비기준
	 * 
	 * @작성 날짜: (2008-10-23 15:08:01)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB035[0] :야드포인트코드
	 *			<li>YDB035_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB035(Hashtable table,
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
			RCaller.Initialize("YDB035", 1, ItemType, ItemCd);
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
			table.put("YDB035_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB035", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB035()




	/**
	 *
	 * YDB036 : 야드B열연슬라브스카핑보급준비기준
	 * 
	 * @작성 날짜: (2008-10-23 15:08:21)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB036[0] :야드포인트코드
	 *			<li>YDB036_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB036(Hashtable table,
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
			RCaller.Initialize("YDB036", 1, ItemType, ItemCd);
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
			table.put("YDB036_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB036", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	}; // end of YDB036()




	/**
	 *
	 * YDB037 : 야드C열연슬라브스카핑보급준비기준
	 * 
	 * @작성 날짜: (2008-10-23 15:08:46)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB037[0] :야드포인트코드
	 *			<li>YDB037_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB037(Hashtable table,
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
			RCaller.Initialize("YDB037", 1, ItemType, ItemCd);
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
			table.put("YDB037_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB037", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB037()




	/**
	 *
	 * YDB038 : 야드A후판주편스카핑보급준비기준
	 * 
	 * @작성 날짜: (2008-10-23 15:09:09)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB038[0] :야드포인트코드
	 *			<li>YDB038_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB038(Hashtable table,
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
			RCaller.Initialize("YDB038", 1, ItemType, ItemCd);
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
			table.put("YDB038_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB038", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};   // end of YDB038()




	/**
	 *
	 * YDB039 : 야드A후판주편전단보급준비기준
	 * 
	 * @작성 날짜: (2008-10-23 15:09:37)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB039[0] :야드포인트코드
	 *			<li>YDB039_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB039(Hashtable table,
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
			RCaller.Initialize("YDB039", 1, ItemType, ItemCd);
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
			table.put("YDB039_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB039", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB039()  


	/**
	 * 기준ID : YDB397
	 * 기준명 : 통합야드-Pallet상차매수
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2009-10-21 14:52:33)
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB397[0] :야드설비작업매수
	 *			<li>YDB397_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean YDB397(Hashtable table) throws RuleException {     
	        Vector vt = new Vector();
	        int rc = 0;

	        RAPI4J  RCaller=null;
	        try {
	           RCaller = new RAPI4J( false,  "" );
	            RCaller.Initialize("YDB397");
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
	            table.put("YDB397_ColCnt", new Integer(resColTypes.length));					
	            table.put("YDB397", result);																					
	            if (result.size() == 0) {																								
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	         																																
	    }

	    /**
	    * 기준ID : YDB398
	    * 기준명 : 통합야드-Trailer상차매수
	    **import com.metis.rapi4j.*; 
	    **import java.util.*; 
	    * item코드허용값 :
	    * @작성 날짜: (2009-10-21 14:53:23)
	    * @param	table 인수값 혹은 결과값(리턴정보)
	    *		<ul>
	    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	    *			<li>YDB398[0] :야드설비작업매수
	    *			<li>YDB398_ColCnt :1
	    *		<ul>
	    * @return 정상처리 여부
	    */
	       public boolean YDB398(Hashtable table) throws RuleException {     
	           Vector vt = new Vector();
	           int rc = 0;

	           RAPI4J  RCaller=null;
	           try {
	              RCaller = new RAPI4J( false,  "" );
	               RCaller.Initialize("YDB398");
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
	               table.put("YDB398_ColCnt", new Integer(resColTypes.length));					
	               table.put("YDB398", result);																					
	               if (result.size() == 0) {																								
	                   table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	               }																															
	               return true;																										
	           } catch (Exception e) {																						
	                  	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	           }																																						
	            																																
	       }   
	 
	/**
	 * 기준ID : YDB399
	 * 기준명 : 통합야드-차량LOT편성자동유무관리
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2009-08-26 13:01:19)
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB399[0] :사용유무
	 *			<li>YDB399_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean YDB399(Hashtable table) throws RuleException {     
	        Vector vt = new Vector();
	        int rc = 0;

	        RAPI4J  RCaller=null;
	        try {
	           RCaller = new RAPI4J( false,  "" );
	            RCaller.Initialize("YDB399");
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
	            table.put("YDB399_ColCnt", new Integer(resColTypes.length));					
	            table.put("YDB399", result);																					
	            if (result.size() == 0) {																								
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	         																																
	    }			//end of YDB399

	    /**
	    *
	    import com.metis.rapi5j.*; 
	    import java.util.*; 
	    * item코드허용값 :
	    * @작성 날짜: (2022-03-16 15:18:46)
	    * @param	table 인수값 혹은 결과값(리턴정보)
	    *		<ul>
	    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
	    *			<li>YDB397[0] :야드설비작업매수
	    *			<li>YDB397_ColCnt :1
	    *		<ul>
	    * @return 정상처리 여부
	    */
	       public boolean YDB397_NEW(Hashtable table
	                         ) throws com.metis.rapi5j.RuleException {     
	           Vector vt = new Vector();
	           int rc = 0;

	           RAPI5J  RCaller=new RAPI5J() ;
	           RCaller.Initialize("YDB397");
	           /* 사용자 입력값 설정 시작 */ 
	           /* 사용자 입력값 설정  */ 
	          try{                                                    						
	               RCaller.MBRS_Run();                                                      
	               ResultData    result=new ResultData();	
	               result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
	               for (int i = 0; i < RCaller.getRowCount(); i++) { 
	                   result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드설비작업매수
	               } 
	               table.put("YDB397_ColCnt", new Integer( RCaller.getColCount() ));					
	               table.put("YDB397", result);																					
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
	       * @작성 날짜: (2022-03-16 15:19:37)
	       * @param	table 인수값 혹은 결과값(리턴정보)
	       *		<ul>
	       *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
	       *			<li>YDB398[0] :야드설비작업매수
	       *			<li>YDB398_ColCnt :1
	       *		<ul>
	       * @return 정상처리 여부
	       */
	          public boolean YDB398_NEW(Hashtable table
	                            ) throws com.metis.rapi5j.RuleException {     
	              Vector vt = new Vector();
	              int rc = 0;

	              RAPI5J  RCaller=new RAPI5J() ;
	              RCaller.Initialize("YDB398");
	              /* 사용자 입력값 설정 시작 */ 
	              /* 사용자 입력값 설정  */ 
	             try{                                                    						
	                  RCaller.MBRS_Run();                                                      
	                  ResultData    result=new ResultData();	
	                  result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
	                  for (int i = 0; i < RCaller.getRowCount(); i++) { 
	                      result.add(  i ,new Integer(RCaller.ReadInt()) );	//야드설비작업매수
	                  } 
	                  table.put("YDB398_ColCnt", new Integer( RCaller.getColCount() ));					
	                  table.put("YDB398", result);																					
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
	          * @작성 날짜: (2022-03-16 15:19:49)
	          * @param	table 인수값 혹은 결과값(리턴정보)
	          *		<ul>
	          *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
	          *			<li>YDB399[0] :사용유무
	          *			<li>YDB399_ColCnt :1
	          *		<ul>
	          * @return 정상처리 여부
	          */
	             public boolean YDB399_NEW(Hashtable table
	                               ) throws com.metis.rapi5j.RuleException {     
	                 Vector vt = new Vector();
	                 int rc = 0;

	                 RAPI5J  RCaller=new RAPI5J() ;
	                 RCaller.Initialize("YDB399");
	                 /* 사용자 입력값 설정 시작 */ 
	                 /* 사용자 입력값 설정  */ 
	                try{                                                    						
	                     RCaller.MBRS_Run();                                                      
	                     ResultData    result=new ResultData();	
	                     result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
	                     for (int i = 0; i < RCaller.getRowCount(); i++) { 
	                         result.add( i , RCaller.ReadString() );	//사용유무
	                     } 
	                     table.put("YDB399_ColCnt", new Integer( RCaller.getColCount() ));					
	                     table.put("YDB399", result);																					
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
