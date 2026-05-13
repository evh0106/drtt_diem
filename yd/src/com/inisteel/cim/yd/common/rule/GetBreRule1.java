package com.inisteel.cim.yd.common.rule;

import java.util.Hashtable;

import jspeed.base.record.JDTORecord;

import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.metis.rapi4j.RuleException;

import jspeed.base.property.PropertyService;
import com.metis.rapi5j.*;
//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
// 
//  	com.inisteel.cim.yd RULE Managed Class1
//  				YDB010 ~ YDB019
//
//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛


public class GetBreRule1 {
	

	private static boolean bDebugFlag=true;
	
	private static final YdUtils	ydUtils	=new YdUtils();
	
	private static final String szClassName = GetBreRule1.class.getName();

	
	private static final BRERule1 breRule1 =new BRERule1();
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드SLAB행선부여기준
	 * 
	 * @param 
	 * @param	String	szSlabWoRtGp	// Slab지시행선구분(2)
	 * @param	String  sAppGp			// 재료외형구분
	 * @param	String	szCurrProcCd	// 현재진도코드(2)
	 * @param	String	sOrdRmngp		// 주문여재구분
	 * @param	String	sScrfYn			// Scarfing여부
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB010(String szSlabWoRtGp, String  sAppGp, String szCurrProcCd
			, String sOrdRmngp, String sScrfYn, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB010";
		
		String szItems [] =new String[] {
				 "야드적치행선구분"			// 야드적치행선구분
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule1.YDB010( htRule			// Return Hashtable
					            , szSlabWoRtGp		// Slab지시행선구분(2)
					       	    , sAppGp			// 재료외형구분
					       	    , szCurrProcCd		// 현재진도코드(2)
					       	    , sOrdRmngp			// 주문여재구분
					       	    , sScrfYn			// Scarfing여부
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB010 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB010_ColCnt");
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
			ydUtils.putLog(szClassName, szMethodName, szMsg,1);

			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB010", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB010()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드COIL행선부여기준
	 * 
	 * @param	String sPlntGp		 	// 공장구분
	 * @param	String sPlntPorcCode 	// 공장공정코드
	 * @param	String sCurrProgCode 	// 현재진도코드
	 * @param	String sOrdRmngp 		// 주문여재구분
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB011(String sPlntGp, String  sPlntPorcCode, String sCurrProgCode
			, String sOrdRmngp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB011";
		
		String szItems [] =new String[] {
				 "야드적치행선구분"			// 야드적치행선구분
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule1.YDB011( htRule			// Return Hashtable
					            , sPlntGp		 	// 공장구분     
					            , sPlntPorcCode 	// 공장공정코드 
					            , sCurrProgCode 	// 현재진도코드 
					            , sOrdRmngp 		// 주문여재구분부
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB011 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB011_ColCnt");
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
			ydUtils.putLog(szClassName, szMethodName, szMsg,1);

			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB011", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB011()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드PLATE행선부여기준
	 * 
	 * @param	String sPlntGp	 		// 공장구분
 	 * @param	String sPlntPorcCode	// 공장공정코드
	 * @param	String sCurrProgCode 	// 현재진도코드
	 * @param	String sOrdRmngp 	 	// 주문여재구분
	 * @param	String sGdsMainGds	 	// 제품주등급
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB012(String sPlntGp, String  sPlntPorcCode, String sCurrProgCode
			, String sOrdRmngp, String sGdsMainGds, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB012";
		
		String szItems [] =new String[] {
				 "야드적치행선구분"			// 야드적치행선구분
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule1.YDB012( htRule			// Return Hashtable
					            , sPlntGp		 	// 공장구분     
					            , sPlntPorcCode 	// 공장공정코드 
					            , sCurrProgCode 	// 현재진도코드 
					            , sOrdRmngp 		// 주문여재구분부
					            , sGdsMainGds	 	// 제품주등급
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB012 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB012_ColCnt");
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
			ydUtils.putLog(szClassName, szMethodName, szMsg,1);

			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB012", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB012()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드SLAB스케줄코드부여기준
	 * 
	 * @param	String sEqpGp 			// 설비구분
	 * @param	String sYdStkRtGp 		// 야드적치행선구분
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB013(String sEqpGp, String  sYdStkRtGp, 
			JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB013";
		
		String szItems [] =new String[] {
				 "Schedule코드"				// Schedule코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule1.YDB013( htRule			// Return Hashtable
								, sEqpGp 			// 설비구분
								, sYdStkRtGp 		// 야드적치행선구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB013 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB013_ColCnt");
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
			ydUtils.putLog(szClassName, szMethodName, szMsg,1);

			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB013", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB013()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드COIL스케줄코드부여기준
	 * 
	 * @param	String sEqpGp 			// 설비구분
	 * @param	String sYdStkRtGp 		// 야드적치행선구분
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB014(String sEqpGp, String  sYdStkRtGp, 
			JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB014";
		
		String szItems [] =new String[] {
				 "Schedule코드"				// Schedule코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule1.YDB014( htRule			// Return Hashtable
								, sEqpGp 			// 설비구분
								, sYdStkRtGp 		// 야드적치행선구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB014 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB014_ColCnt");
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
			ydUtils.putLog(szClassName, szMethodName, szMsg,1);

			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB014", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB014()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드PLATE스케줄코드부여기준
	 * 
	 * @param	String sEqpGp 			// 설비구분
	 * @param	String sYdStkRtGp 		// 야드적치행선구분
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB015(String sEqpGp, String  sYdStkRtGp, 
			JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB015";
		
		String szItems [] =new String[] {
				 "Schedule코드"				// Schedule코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule1.YDB015( htRule			// Return Hashtable
								, sEqpGp 			// 설비구분
								, sYdStkRtGp 		// 야드적치행선구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB015 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB015_ColCnt");
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
			ydUtils.putLog(szClassName, szMethodName, szMsg,1);

			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB015", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB015()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드PLATE차량통로부여기준
	 * 
	 * @param	String szYdGp 			// 야드구분
	 * @param 	String szAreaGp 		// 동구분
	 * @param 	String szSectGp 		// 구역구분
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB018(String szYdGp, String  szAreaGp, 
			String szSectGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB018";
		
		String szItems [] =new String[] {
				 "야드차량통로구분"				// 야드차량통로구분
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule1.YDB018( htRule			// Return Hashtable
								, szYdGp 			// 야드구분
								, szAreaGp 			// 동구분
								, szSectGp 			// 구역구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB018 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB018_ColCnt");
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
			ydUtils.putLog(szClassName, szMethodName, szMsg,1);

			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB018", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB018()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드SLAB차량Point부여기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * @param	String sYdCarPassgeGp 	// 야드차량통로구분
	 * @param 	String szAreaGp 		// 동구분
	 * @param	String sMtlProgCd		// 재료진도코드
	 * @param	String sYdCarUseGp		// 야드차량사용구분
	 * @param	String sEqpKnd			// 설비종류
	 * @param	String sSktStat			// 적재상태
	 * @param	String sColGp 			// 열구분
	 * @param	String sStkColActStat 	// 적치열활성상태
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB019(String sYdGp, String  sYdCarPassgeGp, String szAreaGp,
			String sMtlProgCd, String sYdCarUseGp, String sEqpKnd, String sSktStat,
			 String sColGp, String sStkColActStat, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB019";
		
		String szItems [] =new String[] {
				 "야드포인트코드"				// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule1.YDB019( htRule			// Return Hashtable
								, sYdGp 			// 야드구분
								, sYdCarPassgeGp 	// 야드차량통로구분
								, szAreaGp 			// 동구분
								, sMtlProgCd		// 재료진도코드
								, sYdCarUseGp		// 야드차량사용구분
								, sEqpKnd			// 설비종류
								, sSktStat			// 적재상태
								, sColGp 			// 열구분
								, sStkColActStat 	// 적치열활성상태
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB019 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB019_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB019", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB019()
	
	
	/**
	 *	C연주슬라브야드-대차스케줄기동기준을 반환하는 메소드
	 * @param szTCar					대차번호
	 * @param szTRN_WRK_FULLVOID_GP		영공구분
	 * @param jdtoRcd					OUT PARAM
	 * @return
	 */
	public static boolean getYDB181(String szTCar
			, String  szTRN_WRK_FULLVOID_GP
			, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB181";
		
		String szItems [] =new String[] {
				 "YD_SCH_REQ_GP"								//야드스케쥴요청구분
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule1.YDB181( htRule							// Return Hashtable
								, szTCar 							// 대차번호
								, szTRN_WRK_FULLVOID_GP 			// 영공구분
								);
			
			if(!bRtc){
				szMsg="Rule YDB181 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB181_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB181", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB181()
	
	
	/**
	 *	C연주슬라브야드-대차스케줄기동기준을 반환하는 메소드
	 * @param szTCar					대차번호
	 * @param jdtoRcd					OUT PARAM
	 * @return
	 */
	public static boolean getYDB182(String szTCar
			, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB182";
		
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
			
			bRtc=breRule1.YDB182( htRule							// Return Hashtable
								, szTCar 							// 대차번호
								);
			
			if(!bRtc){
				szMsg="Rule YDB182 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB182_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB182", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB182()
	
	/**
	 *	C연주슬라브야드-이적권하분리여부를 반환하는 메소드
	 * @param jdtoRcd					OUT PARAM
	 * @return
	 */
	public static boolean getYDB183(JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB183";
		
		String szItems [] =new String[] {
				 "USAGE_YN"								//사용여부
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule1.YDB183( htRule							// Return Hashtable
								);
			
			if(!bRtc){
				szMsg="Rule YDB183 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB183_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB183", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB183()
	
	/**
	 * C연주슬라브야드에 적용되는 차량정지위치기본동 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB192(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB192";

		String szItems [] =new String[] {
				 "DEFAULT_YD_BAY_GP"					// 야드동구분
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		
		JDTORecord ydb001Rcd =jdtoRcd;
		
		try{
			
			PropertyService jprop = PropertyService.getInstance();
			
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			if("1.8".equals(javaVersion)){ 
				bRtc = breRule1.YDB192_NEW( htRule			// Return Hashtable
            							);
			}
			else {
				bRtc=breRule1.YDB192( htRule			// Return Hashtable
									);
			}

			
			
			if(!bRtc){
				szMsg ="Rule YDB192 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB192_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				szMsg="BRE Result - ColCnt="+nColCnt;
				if( bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg,4);
			}
		
			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
				return false;
				
			}
			
			
			
		
		}catch (com.metis.rapi5j.RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
			return false;

		}catch (RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB192", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB192()
	
	/**
	 * C연주슬라브야드에 적용되는 Pallet상차매수 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB193(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB193";

		String szItems [] =new String[] {
				 "YD_EQP_WRK_SH"		// 야드설비작업매수
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		
		JDTORecord ydb001Rcd =jdtoRcd;
		
		try{
			PropertyService jprop = PropertyService.getInstance();
			
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			if("1.8".equals(javaVersion)){ 
				bRtc = breRule1.YDB193_NEW( htRule			// Return Hashtable
            							);
			}
			else {
				bRtc=breRule1.YDB193( htRule			// Return Hashtable
									);
			}
			
			
			
			if(!bRtc){
				szMsg ="Rule YDB193 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB193_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				szMsg="BRE Result - ColCnt="+nColCnt;
				if( bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg,4);
			}
		
			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
				return false;
				
			}
			
			
			
		
		}catch (com.metis.rapi5j.RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
			return false;

		}catch (RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB193", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB193()
	
	/**
	 * C연주슬라브야드에 적용되는 Trailer상차매수 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB194(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB194";

		String szItems [] =new String[] {
				 "YD_EQP_WRK_SH"		// 야드설비작업매수
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		
		JDTORecord ydb001Rcd =jdtoRcd;
		
		try{
			PropertyService jprop = PropertyService.getInstance();
			
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			if("1.8".equals(javaVersion)){ 
				bRtc = breRule1.YDB194_NEW( htRule			// Return Hashtable
            							);
			}
			else {
				bRtc=breRule1.YDB194( htRule			// Return Hashtable
									);
			}
			

			
			if(!bRtc){
				szMsg ="Rule YDB194 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB194_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				szMsg="BRE Result - ColCnt="+nColCnt;
				if( bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg,4);
			}
		
			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
				return false;
				
			}
			
			
			
		
		}catch (com.metis.rapi5j.RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
			return false;

		}catch (RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB194", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB194()
	
	/**
	 * C연주슬라브야드에 적용되는 정정보급자동여부를 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB195(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB195";

		String szItems [] =new String[] {
				 "AUTO_ORD_YN"		// 자동지시유무
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		
		JDTORecord ydb001Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule1.YDB195( htRule			// Return Hashtable
					              );
			
			
			if(!bRtc){
				szMsg ="Rule YDB195 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB195_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB195", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB195()
	
	/**
	 * C연주슬라브야드에 적용되는 스카핑보급자동여부를 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB196(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB196";

		String szItems [] =new String[] {
				 "AUTO_ORD_YN"		// 자동지시유무
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		
		JDTORecord ydb001Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule1.YDB196( htRule			// Return Hashtable
					              );
			
			
			if(!bRtc){
				szMsg ="Rule YDB196 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB196_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB196", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB196()
	
	/**
	 * C연주슬라브야드에 적용되는 정정보급LOT편성매수를 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB197(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB197";

		String szItems [] =new String[] {
				 "YD_EQP_WRK_SH"		// 야드설비작업매수
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		
		JDTORecord ydb001Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule1.YDB197( htRule			// Return Hashtable
					              );
			
			
			if(!bRtc){
				szMsg ="Rule YDB197 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB197_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB197", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB197()

	/**
	 * C연주슬라브야드에 적용되는 스카핑보급LOT편성매수를 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB198(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB198";

		String szItems [] =new String[] {
				 "YD_EQP_WRK_SH"		// 야드설비작업매수
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		
		JDTORecord ydb001Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule1.YDB198( htRule			// Return Hashtable
					              );
			
			
			if(!bRtc){
				szMsg ="Rule YDB198 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB198_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB198", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB198()

	/**
	 * C연주슬라브야드에 적용되는 차량상차LOT편성 자동유무 판단값 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB199(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB199";

		String szItems [] =new String[] {
				 "AUTO_LOT_YN"		// 자동LOT편성사용유무
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		
		JDTORecord ydb001Rcd =jdtoRcd;
		
		try{
			PropertyService jprop = PropertyService.getInstance();
			
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			if("1.8".equals(javaVersion)){ 
				bRtc = breRule1.YDB199_NEW( htRule			// Return Hashtable
            							);
			}
			else {
				bRtc=breRule1.YDB199( htRule			// Return Hashtable
									);
			}
		
			
			
			if(!bRtc){
				szMsg ="Rule YDB199 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB199_ColCnt");
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
			
			
			
		
		}catch (com.metis.rapi5j.RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
			return false;

		}catch (RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			
			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB199", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB199()
	
	

	
	
  //---------------------------------------------------------------------------
} // end of class
