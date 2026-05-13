package com.inisteel.cim.yd.common.rule;

import java.util.Hashtable;

import jspeed.base.record.JDTORecord;

import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.metis.rapi4j.RuleException;

//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
// 
//  	com.inisteel.cim.yd RULE Managed Class0
//  				YDB000 ~ YDB009
//
//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛



public class GetBreRule0 {
	

	private static boolean bDebugFlag=true;
	
	private static YdUtils	ydUtils	=new YdUtils();
	
	private static String szClassName = GetBreRule0.class.getName();

	private static BRERule0 breRule0 = new BRERule0();


	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드Interface관리
	 * 
	 * @param 
	 * @param	String szJMSTcCode	// JMS TC Code
	 * @param	JDTORecord jdtoRcd	// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public static boolean getYDB000(String szJMSTcCode, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB000";
		
		String szItems [] =new String[] {
				 "메시지내용"		// 메시지내용
				,"야드Message"	// 야드메시지 발생유형
				,"CLASS코드"		// 클래스 코드
				,"METHODE_명"	// 메소드명
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule0.YDB000( htRule		// Return Hashtable
					            , szJMSTcCode	// JMS TC Code
					            );
			
			if(!bRtc){
				szMsg="Rule YDB000 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB000_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB000", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB000()
	
	
	
	
	
	/**
	 * 전야드에 공통으로 적용되는 차량상차LOT편성 자동유무 판단값 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
//	public static  boolean getYDB001(JDTORecord jdtoRcd){
//		
//		String szMsg="";
//		String szMethodName="getYDB001";
//
//		String szItems [] =new String[] {
//				 "AUTO_LOT_YN"		// 자동LOT편성사용유무
//		};
//		
//		Hashtable htRule =new Hashtable();
//		
//		int nColCnt=0;
//
//		boolean bRtc=false;
//		
//		
//		JDTORecord ydb001Rcd =jdtoRcd;
//		
//		try{
//			
//			bRtc=breRule0.YDB001( htRule			// Return Hashtable
//					              );
//			
//			
//			if(!bRtc){
//				szMsg ="Rule YDB001 Getting Error";
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//			
//				return false;
//				
//			} // end of if()
//		
//			Object objX =htRule.get("YDB001_ColCnt");
//			if(objX instanceof Integer){
//				nColCnt =((Integer)objX).intValue();
//				szMsg="BRE Result - ColCnt="+nColCnt;
//				if( bDebugFlag)
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//			}
//		
//			if( nColCnt<=0){
//
//				szMsg="Column Count("+nColCnt+") Error";
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//			
//				return false;
//				
//			}
//			
//			
//			
//		
//		}catch (RuleException re){
//
//			szMsg="RuleException : "+re.getErrMsg();
//			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//			
//			return false;
//
//		}// end of try
//		
//		
//		return ydUtils.cvtTblToRec("YDB001", szItems, htRule, ydb001Rcd, szClassName );		
//		
//		
//	} // end of getYDB001()
	
	/*
	 *      [A] 오퍼레이션명 : 야드슬라브강종부여기준
	 * 
	 * @param 	String sYdGp, 		// 야드구분
	 * @param 	String sMtlAppGp, 	// 재료외형구분
	 * @param 	String sOrdRmn, 	// 주문여재구분
	 * @param 	String szSpecCode 	// 규격약호
	 * @param 	JDTORecord jdtoRcd	// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
//	public boolean getYDB001(String sYdGp, String sMtlAppGp, String sOrdRmn
//			, String szSpecCode, JDTORecord jdtoRcd){
//		
//		String szMsg="";
//		String szMethodName="getYDB001";
//
//		String szItems [] =new String[] {
//				 "강종코드"		// 강종코드
//		};
//		
//		Hashtable htRule =new Hashtable();
//		
//		int nColCnt=0;
//
//		boolean bRtc=false;
//		
//		
//		JDTORecord ydb001Rcd =jdtoRcd;
//		
//		try{
//			
//			bRtc=breRule0.YDB001( htRule			// Return Hashtable
//					              , sYdGp			// 야드구	분
//					              , sMtlAppGp		// 재료외형구분
//					              , sOrdRmn			// 주문여재구분
//					           	  , szSpecCode		// 규격약호
//			 					  );
//			
//			
//			if(!bRtc){
//				szMsg ="Rune YDB001 Getting Error";
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//			
//				return false;
//				
//			} // end of if()
//		
//			Object objX =htRule.get("YDB001_ColCnt");
//			if(objX instanceof Integer){
//				nColCnt =((Integer)objX).intValue();
//				szMsg="BRE Result - ColCnt="+nColCnt;
//				if( bDebugFlag)
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//			}
//		
//			if( nColCnt<=0){
//
//				szMsg="Column Count("+nColCnt+") Error";
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//			
//				return false;
//				
//			}
//			
//			
//			
//		
//		}catch (RuleException re){
//
//			szMsg="RuleException : "+re.getErrMsg();
//			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//			
//			return false;
//
//		}// end of try
//		
//		
//		return ydUtils.cvtTblToRec("YDB001", szItems, htRule, ydb001Rcd, szClassName );		
//		
//		
//	} // end of getYDB001()
	

	
	
	/**
	 *      [A] 오퍼레이션명 : 야드코일보급행선부여기준
	 *  
	 * @param	String sYdGp, 			// 야드구분
	 * @param 	String sOrdRmn,			// 주문여재구분
	 * @param 	String sYdWrkProgCd, 	// 야드작업진도코드
	 * @param 	String sPtorGp, 		// 조업구분
	 * @param 	String sPlntGp, 		// 공장구분
	 * @param 	String sProcGp 			// 공정구분
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws RuleException
	 */	
//	public boolean getYDB002(String sYdGp, String sOrdRmn
//			, String sYdWrkProgCd, String sPtorGp, String sPlntGp
//			, String sProcGp, JDTORecord jdtoRcd){
//		
//
//		String szMsg="";
//		String szMethodName="getYDB002";
//
//		String szItems [] =new String[] {
//				 "야드적치행선구분"		// 야드적치행선구분
//		};
//		
//		Hashtable htRule =new Hashtable();
//		
//		int nColCnt=0;
//
//		boolean bRtc=false;
//
//		
//		
//		JDTORecord ydb001Rcd =jdtoRcd;
//		
//		try{
//			
//			bRtc=breRule0.YDB002( htRule			// Return Hashtable
//								  , sYdGp 			// 야드구분
//								  , sOrdRmn			// 주문여재구분
//								  , sYdWrkProgCd 	// 야드작업진도코드
//								  , sPtorGp	 		// 조업구분
//								  , sPlntGp 		// 공장구분
//								  , sProcGp 		// 공정구분
//								  );
//			
//			
//			if(!bRtc){
//				szMsg="Rule YDB002 Getting Error";
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//				
//				return false;
//				
//			} // end of if()
//		
//			Object objX =htRule.get("YDB002_ColCnt");
//			if(objX instanceof Integer){
//				nColCnt =((Integer)objX).intValue();
//				szMsg="BRE Result - ColCnt="+nColCnt;
//				if( bDebugFlag)
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//			}
//		
//			if( nColCnt<=0){
//
//				szMsg="Column Count("+nColCnt+") Error";
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//			
//				return false;
//				
//			}
//			
//			
//			
//		
//		}catch (RuleException re){
//
//			szMsg="RuleException : "+re.getErrMsg();
//			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//			
//			return false;
//
//		}// end of try
//		
//		
//		return ydUtils.cvtTblToRec("YDB002", szItems, htRule, ydb001Rcd, szClassName );		
//
//		
//	} // end of getYDB002()
	

	
	
	/**
	 *      [A] 오퍼레이션명 : 야드코일입고행선부여기준
	 * 
	 * @param	String sYdGp, 			// 야드구분
	 * @param	String sOrdRmn,			// 주문여재구분
	 * @param	String sYdWrkProgCd, 	// 야드작업진도코드
	 * @param	String sGdsGrd, 		// 제품등급
	 * @param	String sOrdGp, 		    // 수주구분
	 * @param	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws RuleException
	 */	
//	public boolean getYDB003(String sYdGp, String sOrdRmn
//			, String sYdWrkProgCd, String sGdsGrd, String sOrdGp, JDTORecord jdtoRcd){
//		
//
//		String szMsg="";
//		String szMethodName="getYDB003";
//
//		String szItems [] =new String[] {
//				 "야드적치행선구분"		// 야드적치행선구분
//		};
//		
//		Hashtable htRule =new Hashtable();
//		
//		int nColCnt=0;
//
//		boolean bRtc=false;
//
//		
//		
//		JDTORecord ydb001Rcd =jdtoRcd;
//		
//		try{
//			
//			bRtc=breRule0.YDB003( htRule			// Return Hashtable
//								  , sYdGp 			// 야드구분
//								  , sOrdRmn			// 주문여재구분
//								  , sYdWrkProgCd 	// 야드작업진도코드
//								  , sGdsGrd 		// 제품등급
//								  , sOrdGp  		// 수주구분
//								  );
//			
//			
//			if(!bRtc){
//				szMsg="Rule YDB003 Getting Error";
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//				
//				return false;
//				
//			} // end of if()
//		
//			Object objX =htRule.get("YDB003_ColCnt");
//			if(objX instanceof Integer){
//				nColCnt =((Integer)objX).intValue();
//				szMsg="BRE Result - ColCnt="+nColCnt;
//				if( bDebugFlag)
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//			}
//		
//			if( nColCnt<=0){
//
//				szMsg="Column Count("+nColCnt+") Error";
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//			
//				return false;
//				
//			}
//			
//			
//			
//		
//		}catch (RuleException re){
//
//			szMsg="RuleException : "+re.getErrMsg();
//			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//			
//			return false;
//
//		}// end of try
//		
//		
//		return ydUtils.cvtTblToRec("YDB003", szItems, htRule, ydb001Rcd, szClassName );		
//
//		
//	} // end of getYDB003()
	

	
	
	/**
	 *      [A] 오퍼레이션명 : 야드코일입고행선부여기준
	 * 
	 * @param	String sYdGp, 			// 야드구분(1)
	 * @param 	String sOrdGp, 		    // 수주구분(1)
	 * @param 	String szYdMtlSpecMgtGp // 야드재료특별관리구분(2)
	 * @param  	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
//	public boolean getYDB004(String sYdGp, String sOrdGp
//			, String szYdMtlSpecMgtGp, JDTORecord jdtoRcd){
//
//		
//		String szMsg="";
//		String szMethodName="getYDB004";
//
//		String szItems [] =new String[] {
//				 "야드적치행선구분"		// 야드적치행선구분
//		};
//		
//		Hashtable htRule =new Hashtable();
//		
//		int nColCnt=0;
//
//		boolean bRtc=false;
//
//		
//		
//		JDTORecord ydb001Rcd =jdtoRcd;
//		
//		try{
//			
//			bRtc=breRule0.YDB004( htRule				// Return Hashtable
//								  , sYdGp 				// 야드구분
//								  , sOrdGp  			// 수주구분
//								  , szYdMtlSpecMgtGp	// 야드재료특별관리구분);
//								  );
//
//			
//			if(!bRtc){
//				szMsg="Rule YDB004 Getting Error";
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//				
//				return false;
//				
//			} // end of if()
//		
//			Object objX =htRule.get("YDB004_ColCnt");
//			if(objX instanceof Integer){
//				nColCnt =((Integer)objX).intValue();
//				szMsg="BRE Result - ColCnt="+nColCnt;
//				if( bDebugFlag)
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//			}
//		
//			if( nColCnt<=0){
//
//				szMsg="Column Count("+nColCnt+") Error";
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//			
//				return false;
//				
//			}
//			
//			
//			
//		
//		}catch (RuleException re){
//
//			szMsg="RuleException : "+re.getErrMsg();
//			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//			
//			return false;
//
//		}// end of try
//		
//		
//		return ydUtils.cvtTblToRec("YDB004", szItems, htRule, ydb001Rcd, szClassName );		
//
//		
//	} // end of getYDB004()
	

	
	
//	/**
//	 *      [A] 오퍼레이션명 : 야드슬라브적치폭구분부여기준
//	 *
//	 * @param	String sYdGp, 			// 야드구분(1)
//	 * @param	String sOrdRmn			// 주문여재구분
//	 * @param	double szYdMtlWidth 	// 야드재료폭
//	 * @param	JDTORecord jdtoRcd		// Target JDTORecord
//	 * @return 
//	 * @throws  RuleException
//	 */	
//	public boolean getYDB0051(String sYdGp, String sOrdRmn
//			, String szYdMtlWidth, JDTORecord jdtoRcd){
// 
//		
//		String szMsg="";
//		String szMethodName="getYDB005";
//
//		String szItems [] =new String[] {
//				 "야드재료폭구분"		// 야드재료폭구분
//		};
//		
//		Hashtable htRule =new Hashtable();
//		
//		int nColCnt=0;
//
//		boolean bRtc=false;
//
//		double dblYdMtlWidth= Double.parseDouble(szYdMtlWidth);
//		
//		
//		JDTORecord ydb001Rcd =jdtoRcd;
//		
//		try{
//			
//			bRtc=breRule0.YDB0051( htRule				// Return `
//								  , sYdGp 				// 야드구분
//								  , sOrdRmn				// 주문여재구분
//								  , dblYdMtlWidth 		// 야드재료폭
//								  );
//
//
//			
//			if(!bRtc){
//				szMsg="Rule YDB005 Getting Error";
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//				
//				return false;
//				
//			} // end of if()
//		
//			Object objX =htRule.get("YDB005_ColCnt");
//			if(objX instanceof Integer){
//				nColCnt =((Integer)objX).intValue();
//				szMsg="BRE Result - ColCnt="+nColCnt;
//				if( bDebugFlag)
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//			}
//		
//			if( nColCnt<=0){
//
//				szMsg="Column Count("+nColCnt+") Error";
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//			
//				return false;
//				
//			}
//			
//			
//			
//		
//		}catch (RuleException re){
//
//			szMsg="RuleException : "+re.getErrMsg();
//			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//			
//			return false;
//
//		}// end of try
//		
//		
//		return ydUtils.cvtTblToRec("YDB005", szItems, htRule, ydb001Rcd, szClassName );		
//
//		
//	} // end of getYDB005()

	/**
	 *      [A] 오퍼레이션명 : 야드슬라브적치폭구분부여기준
	 *
	 * @param	String szYD_STK_LOT_CD_GRADE, 		// 야드산적LOT코드비교결과
	 * @param	String szYD_STL_W_CMP_GRADE			// 슬라브폭편차비교결과
	 * @param	JDTORecord jdtoRcd					// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public static boolean getYDB005(String szYD_STK_LOT_CD_GRADE, 
			String szYD_STL_W_CMP_GRADE,
			JDTORecord jdtoRcd){

		
		String szMsg="";
		String szMethodName="getYDB005";

		String szItems [] =new String[] {
				 "YD_LOC_SRCH_RNG_SEQ"					//야드위치검색범위순서
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb001Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule0.YDB005( htRule								// Return `
								  , szYD_STK_LOT_CD_GRADE 				// 야드산적LOT코드비교결과
								  , szYD_STL_W_CMP_GRADE				// 슬라브폭편차비교결과
								  );


			
			if(!bRtc){
				szMsg="Rule YDB005 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB005_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB005", szItems, htRule, ydb001Rcd, szClassName );		

		
	} // end of getYDB005()

	
	
	/**
	 *      [A] 오퍼레이션명 : 야드슬라브적치두께구분부여기준
	 * 
	 * @param	String sYdGp, 			// 야드구분(1)
	 * @param	String sOrdRmn			// 주문여재구분
	 * @param	double szYdMtlThick 	// 야드재료두께
	 * @param	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB006(String sYdGp, String sOrdRmn
			, String szYdMtlThick, JDTORecord jdtoRcd){
 
		
		String szMsg="";
		String szMethodName="getYDB006";

		String szItems [] =new String[] {
				 "야드재료두께구분"		// 야드재료두께구분
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		double dblYdMtlThick= Double.parseDouble(szYdMtlThick);
		
		
		JDTORecord ydb001Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule0.YDB006( htRule				// Return `
								  , sYdGp 				// 야드구분
								  , sOrdRmn				// 주문여재구분
								  , dblYdMtlThick 		// 야드재료두께
								  );


			
			if(!bRtc){
				szMsg="Rule YDB006 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB006_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB006", szItems, htRule, ydb001Rcd, szClassName );		

		
	} // end of getYDB006()

	

	
	
	/**
	 *      [A] 오퍼레이션명 : 야드후판제품적치길이구분부여기준
	 * 
	 * @param	String sYdGp			// 야드구분
	 * @param	String sOrdRmn			// 주문여재구분
	 * @param	String sGdsGrd			// 제품등급
	 * @param	String sOrdGp			// 수주구분
	 * @param	String szYdMtlLength	// 야드재료길이
	 * @param	JDTORecord jdtoRcd		// Target JDTORecord
	 * 
	 * @return	
	 *  
	 * @throws  RuleException
	 */	
	public boolean getYDB007(String sYdGp, String sOrdRmn
			, String sGdsGrd, String sOrdGp, String szYdMtlLength, JDTORecord jdtoRcd){
 
		
		String szMsg="";
		String szMethodName="getYDB007";

		String szItems [] =new String[] {
				 "야드재료길이구분"		// 야드재료길이구분
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		int nYdMtlLength= Integer.parseInt(szYdMtlLength);
		
		
		JDTORecord ydRuleRcd =jdtoRcd;
		
		try{
			
			bRtc=breRule0.YDB007( htRule				// Return `
								  , sYdGp 				// 야드구분
								  , sOrdRmn				// 주문여재구분
								  , sGdsGrd				// 제품등급
								  , sOrdGp				// 수주구분
								  , nYdMtlLength		// 야드재료길이
								  );

			
			if(!bRtc){
				szMsg="Rule YDB007 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB007_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB007", szItems, htRule, ydRuleRcd, szClassName );		

		
	} // end of getYDB007()

	

	
	
	/**
	 *      [A] 오퍼레이션명 : 야드후판제품적치폭구분부여기준
	 * 
	 * @param	String sYdGp			// 야드구분
	 * @param	String sOrdRmn			// 주문여재구분
	 * @param	String sGdsGrd			// 제품등급
	 * @param	String sOrdGp			// 수주구분
	 * @param	String szYdMtlWidth		// 야드재료폭
	 * @param	JDTORecord jdtoRcd		// Target JDTORecord
	 * 
	 * @return	
	 *  
	 * @throws  RuleException
	 */	
	public boolean getYDB008(String sYdGp, String sOrdRmn
			, String sGdsGrd, String sOrdGp, String szYdMtlWidth, JDTORecord jdtoRcd){
 
		
		String szMsg="";
		String szMethodName="getYDB008";

		String szItems [] =new String[] {
				 "야드재료폭구분"		// 야드재료폭구분
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		double dblYdMtlWidth= Double.parseDouble(szYdMtlWidth);
		
		
		JDTORecord ydRuleRcd =jdtoRcd;
		
		try{
			
			bRtc=breRule0.YDB008( htRule				// Return `
								  , sYdGp 				// 야드구분
								  , sOrdRmn				// 주문여재구분
								  , sGdsGrd				// 제품등급
								  , sOrdGp				// 수주구분
								  , dblYdMtlWidth		// 야드재료폭
								  );

			
			if(!bRtc){
				szMsg="Rule YDB008 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB008_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB008", szItems, htRule, ydRuleRcd, szClassName );		

		
	} // end of getYDB008()

	

	
	
	/**
	 *      [A] 오퍼레이션명 : 야드후판제품적치폭구분부여기준
	 * 
	 * @param	String sYdGp			// 야드구분
	 * @param	String sOrdRmn			// 주문여재구분
	 * @param	String sProgCd			// 진도코드
	 * @param	String sOrdGp			// 수주구분
	 * @param	String szCoilOutDia		// COIL외경                     
	 * @param	JDTORecord jdtoRcd		// Target JDTORecord
	 * 
	 * @return	
	 *  
	 * @throws  RuleException
	 */	
	public boolean getYDB009(String sYdGp, String sOrdRmn
			, String sProgCd, String sOrdGp, String szCoilOutDia, JDTORecord jdtoRcd){

		
		String szMsg="";
		String szMethodName="getYDB009";

		String szItems [] =new String[] {
				 "야드코일외경군구분"		// 야드코일외경군구분
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		int nCoilOutDia= Integer.parseInt(szCoilOutDia);
		
		
		JDTORecord ydRuleRcd =jdtoRcd;
		
		try{
			
			bRtc=breRule0.YDB009( htRule				// Return `
								  , sYdGp 				// 야드구분
								  , sOrdRmn				// 주문여재구분
								  , sProgCd				// 제품등급
								  , sOrdGp				// 수주구분
								  , nCoilOutDia			// 야드재료폭
								  );

			
			if(!bRtc){
				szMsg="Rule YDB009 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB009_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB009", szItems, htRule, ydRuleRcd, szClassName );		

		
	} // end of getYDB009()
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일야드To위치평점
	 * 
	 * @param	String sCoilStat		// 스케줄코일상태
	 * @param	String sStkLyr			// 적치단
	 * @param	String sLeftGrade		// 좌측1단코일상태
	 * @param	String sRightGrade		// 우측1단코일상태
	 * @param	JDTORecord jdtoRcd		// Target JDTORecord
	 * 
	 * @return	
	 *  
	 * @throws  RuleException
	 */	
	public static boolean getYDB010(String sCoilStat, String sStkLyr
			, String sLeftGrade, String sRightGrade, JDTORecord jdtoRcd){

		
		String szMsg="";
		String szMethodName="getYDB010";

		String szItems [] =new String[] {
				 "TO_POS_GRD"		// To위치평점
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		//int nCoilOutDia= Integer.parseInt(szCoilOutDia);
		
		
		JDTORecord ydRuleRcd =jdtoRcd;
		
		try{
			
			bRtc=breRule0.YDB010( htRule				// Return `
								  , sCoilStat 				// 스케줄코일상태
								  , sStkLyr				// 적치단
								  , sLeftGrade				// 좌측1단코일상태
								  , sRightGrade				// 우측1단코일상태
								  );

			
			if(!bRtc){
				szMsg="Rule YDB010 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB010_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB010", szItems, htRule, ydRuleRcd, szClassName );		

		
	} // end of getYDB010()
	

	
	
  //---------------------------------------------------------------------------
} // end of class
