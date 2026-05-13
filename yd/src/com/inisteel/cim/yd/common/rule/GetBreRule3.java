package com.inisteel.cim.yd.common.rule;

import java.util.Hashtable;

import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTORecord;

import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.metis.rapi4j.RuleException;

//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
// 
//  	com.inisteel.cim.yd RULE Managed Class2
//  				YDB030 ~ YDB039
//
//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

public class GetBreRule3 {
	

	private static boolean bDebugFlag=true;
	
	private static final YdUtils	ydUtils	=new YdUtils();
	
	private static final String szClassName = GetBreRule3.class.getName();

	private static final BRERule3 breRule3 = new BRERule3();
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드C열연코일차량Point부여기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB030(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB030";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule3.YDB030( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB030 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB030_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB030", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB030()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄상태부여기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB031(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB031";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule3.YDB031( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB031 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB031_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB031", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB031()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드대차스케줄상태부여기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB032(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB032";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule3.YDB032( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB032 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB032_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB032", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB032()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량스케줄상태부여기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB033(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB033";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule3.YDB033( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB033 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB033_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB033", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB033()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드B열연슬라브장입준비기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB034(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB034";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule3.YDB034( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB034 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB034_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB034", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB034()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드C열연슬라브장입준비기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB035(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB035";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule3.YDB035( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB035 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB035_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB035", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB035()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드B열연슬라브스카핑보급준비기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB036(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB036";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule3.YDB036( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB036 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB036_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB036", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB036()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드C열연슬라브스카핑보급준비기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB037(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB037";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule3.YDB037( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB037 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB037_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB037", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB037()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드A후판주편스카핑보급준비기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB038(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB038";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule3.YDB038( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB038 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB038_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB038", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB038()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드A후판주편전단보급준비기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB039(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB039";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule3.YDB039( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB039 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB039_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB039", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB039()

	/**
	 * 통합야드에 적용되는 Pallet상차매수 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB397(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB397";

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
				bRtc = breRule3.YDB397_NEW( htRule			// Return Hashtable
            							);
			}
			else {
				bRtc=breRule3.YDB397( htRule			// Return Hashtable
									);
			}

			
			
			if(!bRtc){
				szMsg ="Rule YDB397 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB397_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB397", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB397()
	
	/**
	 * 통합야드에 적용되는 Trailer상차매수 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB398(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB398";

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
				bRtc = breRule3.YDB398_NEW( htRule			// Return Hashtable
            							);
			}
			else {
				bRtc=breRule3.YDB398( htRule			// Return Hashtable
									);
			}

			if(!bRtc){
				szMsg ="Rule YDB398 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB398_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB398", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB398()
	
	/**
	 * 통합야드에 적용되는 차량상차LOT편성 자동유무 판단값 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB399(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB399";

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
				bRtc = breRule3.YDB399_NEW( htRule			// Return Hashtable
            							);
			}
			else {
				bRtc=breRule3.YDB399( htRule			// Return Hashtable
									);
			}

			if(!bRtc){
				szMsg ="Rule YDB399 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB399_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB399", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB399()
	
  //---------------------------------------------------------------------------
} // eod of class
