package com.inisteel.cim.yd.common.rule;

import java.util.Hashtable;

import jspeed.base.record.JDTORecord;

import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.metis.rapi4j.RuleException;

//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
// 
//  	com.inisteel.cim.yd RULE Managed Class2
//  				YDB040 ~ YDB049
//
//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

public class GetBreRule4 {
	

	private static boolean bDebugFlag=true;
	
	private static final YdUtils	ydUtils	=new YdUtils();
	
	private static final String szClassName = GetBreRule4.class.getName();

	private static final BRERule4 breRule4 = new BRERule4();
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드A열연코일정정보급준비기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB040(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB040";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule4.YDB040( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB040 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB040_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				
				//
				// Debugging 용
				//
				szMsg="BRE Result - ColCnt="+nColCnt;
				if( bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		
			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			
				return false;
				
			}
			
			
			
		
		}catch (RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB040", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB040()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드B열연코일정정보급준비기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB041(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB041";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule4.YDB041( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB041 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB041_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				
				//
				// Debugging 용
				//
				szMsg="BRE Result - ColCnt="+nColCnt;
				if( bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		
			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			
				return false;
				
			}
			
			
			
		
		}catch (RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB041", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB041()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드C열연코일정정보급준비기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB042(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB042";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule4.YDB042( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB042 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB042_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				
				//
				// Debugging 용
				//
				szMsg="BRE Result - ColCnt="+nColCnt;
				if( bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		
			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			
				return false;
				
			}
			
			
			
		
		}catch (RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB042", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB041()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드연주슬라브Bed정리기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB043(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB043";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule4.YDB043( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB043 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB043_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				
				//
				// Debugging 용
				//
				szMsg="BRE Result - ColCnt="+nColCnt;
				if( bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		
			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			
				return false;
				
			}
			
			
			
		
		}catch (RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB043", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB043()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드B열연슬라브Bed정리기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB044(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB044";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule4.YDB044( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB044 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB044_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				
				//
				// Debugging 용
				//
				szMsg="BRE Result - ColCnt="+nColCnt;
				if( bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		
			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			
				return false;
				
			}
			
			
			
		
		}catch (RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB044", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB044()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드후판슬라브Bed정리기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB045(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB045";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule4.YDB045( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB045 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB045_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				
				//
				// Debugging 용
				//
				szMsg="BRE Result - ColCnt="+nColCnt;
				if( bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		
			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			
				return false;
				
			}
			
			
			
		
		}catch (RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB045", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB045()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드A열연코일소재Bed정리기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB046(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB046";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule4.YDB046( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB046 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB046_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				
				//
				// Debugging 용
				//
				szMsg="BRE Result - ColCnt="+nColCnt;
				if( bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		
			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			
				return false;
				
			}
			
			
			
		
		}catch (RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB046", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB046()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드B열연코일소재Bed정리기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB047(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB047";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule4.YDB047( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB047 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB047_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				
				//
				// Debugging 용
				//
				szMsg="BRE Result - ColCnt="+nColCnt;
				if( bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		
			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			
				return false;
				
			}
			
			
			
		
		}catch (RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB047", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB047()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드C열연코일소재Bed정리기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB048(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB048";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule4.YDB048( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB048 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB048_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				
				//
				// Debugging 용
				//
				szMsg="BRE Result - ColCnt="+nColCnt;
				if( bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		
			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			
				return false;
				
			}
			
			
			
		
		}catch (RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB048", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB048()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드A열연코일제품Bed정리기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB049(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB049";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule4.YDB049( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB049 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB049_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				
				//
				// Debugging 용
				//
				szMsg="BRE Result - ColCnt="+nColCnt;
				if( bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		
			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			
				return false;
				
			}
			
			
			
		
		}catch (RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB049", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB049()
	
	/**
	 *	C코일소재야드-대차스케줄기동기준을 반환하는 메소드
	 * @param szTCar					대차번호
	 * @param jdtoRcd					OUT PARAM
	 * @return
	 */
	public static boolean getYDB430(String szTCar
			, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB430";
		
		String szItems [] =new String[] {
				 "USAGE_YN"								//사용여부
				,"WORK_GP"								//작업구분
				,"YD_CARLD_BAY_GP"						//야드상차동구분
				,"YD_CARUD_BAY_GP"						//야드하차동구분
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule4.YDB430( htRule							// Return Hashtable
								, szTCar 							// 대차번호
								);
			
			if(!bRtc){
				szMsg="Rule YDB430 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB430_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				
				//
				// Debugging 용
				//
				szMsg="BRE Result - ColCnt="+nColCnt;
				if( bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		
			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			
				return false;
				
			}
			
			
			
		
		}catch (RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB430", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB430()
	
	/**
	 * C열연코일소재야드에 적용되는 차량상차LOT편성 자동유무 판단값 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB499(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB499";

		String szItems [] =new String[] {
				 "AUTO_LOT_YN"		// 자동LOT편성사용유무
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		
		JDTORecord ydb001Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule4.YDB499( htRule			// Return Hashtable
					              );
			
			
			if(!bRtc){
				szMsg ="Rule YDB499 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB499_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				szMsg="BRE Result - ColCnt="+nColCnt;
				if( bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		
			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			
				return false;
				
			}
			
			
			
		
		}catch (RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			
			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB499", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB499()
	
	
  //---------------------------------------------------------------------------
} // end of class
