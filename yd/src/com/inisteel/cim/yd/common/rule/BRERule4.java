package com.inisteel.cim.yd.common.rule;


import java.util.Hashtable;
import java.util.Vector;

import com.metis.rapi4j.RAPI4J;
import com.metis.rapi4j.ResultData;
import com.metis.rapi4j.RuleException;

public class BRERule4 {

	/**
	 *
	 * YDB040 : 야드A열연코일정정보급준비기준
	 * 
	 * @작성 날짜: (2008-10-23 15:16:46)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB040[0] :야드포인트코드
	 *			<li>YDB040_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB040(Hashtable table,
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
			RCaller.Initialize("YDB040", 1, ItemType, ItemCd);
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
			table.put("YDB040_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB040", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	}; // end of YDB040()




	/**
	 *
	 * YDB041 : 야드B열연코일정정보급준비기준
	 * 
	 * @작성 날짜: (2008-10-23 15:17:07)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB041[0] :야드포인트코드
	 *			<li>YDB041_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB041(Hashtable table,
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
			RCaller.Initialize("YDB041", 1, ItemType, ItemCd);
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
			table.put("YDB041_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB041", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB041()




	/**
	 *
	 * YDB042 : 야드C열연코일정정보급준비기준
	 * 
	 * @작성 날짜: (2008-10-23 15:17:28)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB042[0] :야드포인트코드
	 *			<li>YDB042_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB042(Hashtable table,
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
			RCaller.Initialize("YDB042", 1, ItemType, ItemCd);
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
			table.put("YDB042_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB042", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB042()




	/**
	 *
	 * YDB043 : 야드연주슬라브Bed정리기준
	 * 
	 * @작성 날짜: (2008-10-23 15:17:49)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB043[0] :야드포인트코드
	 *			<li>YDB043_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB043(Hashtable table,
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
			RCaller.Initialize("YDB043", 1, ItemType, ItemCd);
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
			table.put("YDB043_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB043", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};   // end of YDB043()




	/**
	 *
	 * YDB044 : 야드B열연슬라브Bed정리기준
	 * 
	 * @작성 날짜: (2008-10-23 15:18:08)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB044[0] :야드포인트코드
	 *			<li>YDB044_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB044(Hashtable table,
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
			RCaller.Initialize("YDB044", 1, ItemType, ItemCd);
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
			table.put("YDB044_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB044", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};   // end of YDB044()




	/**
	 *
	 * YDB045 : 야드후판슬라브Bed정리기준
	 * 
	 * @작성 날짜: (2008-10-23 15:18:31)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB045[0] :야드포인트코드
	 *			<li>YDB045_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB045(Hashtable table,
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
			RCaller.Initialize("YDB045", 1, ItemType, ItemCd);
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
			table.put("YDB045_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB045", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};   // end of YDB045()




	/**
	 *
	 * YDB046 : 야드A열연코일소재Bed정리기준
	 * 
	 * @작성 날짜: (2008-10-23 15:18:54)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB046[0] :야드포인트코드
	 *			<li>YDB046_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB046(Hashtable table,
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
			RCaller.Initialize("YDB046", 1, ItemType, ItemCd);
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
			table.put("YDB046_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB046", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	}; // end of YDB046()




	/**
	 *
	 * YDB047 : 야드B열연코일소재Bed정리기준
	 * 
	 * @작성 날짜: (2008-10-23 15:19:13)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB047[0] :야드포인트코드
	 *			<li>YDB047_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB047(Hashtable table,
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
			RCaller.Initialize("YDB047", 1, ItemType, ItemCd);
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
			table.put("YDB047_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB047", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};   // end of YDB047()




	/**
	 *
	 * YDB048 : 야드C열연코일소재Bed정리기준
	 * 
	 * @작성 날짜: (2008-10-23 15:19:33)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB048[0] :야드포인트코드
	 *			<li>YDB048_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB048(Hashtable table,
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
			RCaller.Initialize("YDB048", 1, ItemType, ItemCd);
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
			table.put("YDB048_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB048", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};  // end of YDB048()




	/**
	 *
	 * YDB049 : 야드A열연코일제품Bed정리기준
	 * 
	 * @작성 날짜: (2008-10-23 15:19:55)
	 * @param	item1	야드구분
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB049[0] :야드포인트코드
	 *			<li>YDB049_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	public boolean YDB049(Hashtable table,
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
			RCaller.Initialize("YDB049", 1, ItemType, ItemCd);
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
			table.put("YDB049_ColCnt", new Integer(resColTypes.length));					
			table.put("YDB049", result);																					
			if (result.size() == 0) {																								
				table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
			}																															
			return true;																										
		} catch (Exception e) {																						
			throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
		}																																						

	};   // end of YDB049() 
	
	
	/**
	    *	기준ID : YDB430
	    *  	기준명 : C코일소재야드-대차작업지정기준 
	    **import com.metis.rapi4j.*; 
	    **import java.util.*; 
	    * item코드허용값 :
	    * @작성 날짜: (2010-03-16 11:27:26)
	    * @param	item1	야드작업대차
	    * @param	table 인수값 혹은 결과값(리턴정보)
	    *		<ul>
	    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	    *			<li>YDB430[0] :사용여부
	    *			<li>YDB430[1] :작업구분
	    *			<li>YDB430[2] :야드상차동구분
	    *			<li>YDB430[3] :야드하차동구분
	    *			<li>YDB430_ColCnt :4
	    *		<ul>
	    * @return 정상처리 여부
	    */
	   public boolean YDB430(Hashtable table,
	                       String item1 // 야드작업대차
	                     ) throws RuleException {     
	       //Vector vt = new Vector();
	       //int rc = 0;

	       RAPI4J  RCaller=null;
	       try {
	          RCaller = new RAPI4J( false,  "" );
	           RCaller.Initialize("YDB430");
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
	           table.put("YDB430_ColCnt", new Integer(resColTypes.length));					
	           table.put("YDB430", result);																					
	           if (result.size() == 0) {																								
	               table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	           }																															
	           return true;																										
	       } catch (Exception e) {																						
	              	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	       }																																						
	        																																
	   }



	/**
	 * 기준ID : YDB499
	 * 기준명 : C열연코일소재야드-차량LOT편성자동유무관리
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2009-08-26 12:57:48)
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>YDB499[0] :사용유무
	 *			<li>YDB499_ColCnt :1
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean YDB499(Hashtable table) throws RuleException {     
	        Vector vt = new Vector();
	        int rc = 0;

	        RAPI4J  RCaller=null;
	        try {
	           RCaller = new RAPI4J( false,  "" );
	            RCaller.Initialize("YDB499");
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
	            table.put("YDB499_ColCnt", new Integer(resColTypes.length));					
	            table.put("YDB499", result);																					
	            if (result.size() == 0) {																								
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	         																																
	    }			//end of YDB499







	

  //---------------------------------------------------------------------------
} // end of class
