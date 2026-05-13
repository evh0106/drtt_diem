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
//  				YDB020 ~ YDB029
//
//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛



public class GetBreRule2 {
	

	private static boolean bDebugFlag=true;
	
	private static final YdUtils	ydUtils	=new YdUtils();
	
	private static final String szClassName = GetBreRule2.class.getName();

	private static final BRERule2 breRule2 = new BRERule2();
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드COIL차량Point부여기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * @param	String sYdCarPassgeGp 	// 야드차량통로구분
	 * @param	String sAreaGp 			// 동구분
	 * @param	String sMtlProgCd		// 재료진도코드
	 * @param	String sYdCarUseGp 		// 야드차량사용구분
     * @param	String sEqpKnd 			// 설비종류
	 * @param	String sSktStat			// 적재상태
	 * @param	String sColGp 			// 열구분
	 * @param	String sSktColActStat 	// 적치열활성상태
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB020(String sYdGp, String  sYdCarPassgeGp, String sAreaGp
			, String sMtlProgCd, String sYdCarUseGp, String sEqpKnd
			, String sSktStat, String sColGp, String sSktColActStat, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB020";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule2.YDB020( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					 			, sYdCarPassgeGp 	// 야드차량통로구분
					 			, sAreaGp 			// 동구분
					 			, sMtlProgCd		// 재료진도코드
					 			, sYdCarUseGp 		// 야드차량사용구분
					 			, sEqpKnd 			// 설비종류
					 			, sSktStat			// 적재상태
					 			, sColGp 			// 열구분
					 			, sSktColActStat 	// 적치열활성상태
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB020 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB020_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB020", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB020()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드PLATE차량Point부여기준
	 * 
	 * @param	String sYdGp 			// 야드구분
	 * @param	String sYdCarPassgeGp 	// 야드차량통로구분
	 * @param	String sAreaGp 			// 동구분
	 * @param	String sMtlProgCd		// 재료진도코드
	 * @param	String sYdCarUseGp 		// 야드차량사용구분
     * @param	String sEqpKnd 			// 설비종류
	 * @param	String sSktStat			// 적재상태
	 * @param	String sColGp 			// 열구분
	 * @param	String sSktColActStat 	// 적치열활성상태
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB021(String sYdGp, String  sYdCarPassgeGp, String sAreaGp
			, String sMtlProgCd, String sYdCarUseGp, String sEqpKnd
			, String sSktStat, String sColGp, String sSktColActStat, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB021";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule2.YDB021( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					 			, sYdCarPassgeGp 	// 야드차량통로구분
					 			, sAreaGp 			// 동구분
					 			, sMtlProgCd		// 재료진도코드
					 			, sYdCarUseGp 		// 야드차량사용구분
					 			, sEqpKnd 			// 설비종류
					 			, sSktStat			// 적재상태
					 			, sColGp 			// 열구분
					 			, sSktColActStat 	// 적치열활성상태
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB021 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB021_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB021", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB021()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드후판슬라브스케줄코드부여기준
	 * 
	 * @param	String sYdStrGtrCd 		// 야드저장집합코드
	 * @param	String sYdMtlOrdGp 		// 야드재료지시구분
	 * @param	String sWrkMd			// 작업Mode
	 * @param	String sBkupEqpYn 		// Backup설비유무
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB022(String sYdStrGtrCd, String  sYdMtlOrdGp, String sWrkMd
			, String sBkupEqpYn, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB022";
		
		String szItems [] =new String[] {
				 "야드대상스케줄코드"			// 야드대상스케줄코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule2.YDB022( htRule			// Return Hashtable
					 			, sYdStrGtrCd 		// 야드저장집합코드
					 			, sYdMtlOrdGp 		// 야드재료지시구분
					 			, sWrkMd			// 작업Mode
					 			, sBkupEqpYn 		// Backup설비유무
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB022 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB022_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB022", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB022()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드B열연슬라브스케줄코드부여기준
	 * 
	 * @param	String sYdStrGtrCd 		// 야드저장집합코드
	 * @param	String sYdMtlOrdGp 		// 야드재료지시구분
	 * @param	String sWrkMd			// 작업Mode
	 * @param	String sBkupEqpYn 		// Backup설비유무
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB023(String sYdStrGtrCd, String  sYdMtlOrdGp, String sWrkMd
			, String sBkupEqpYn, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB023";
		
		String szItems [] =new String[] {
				 "야드대상스케줄코드"			// 야드대상스케줄코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule2.YDB023( htRule			// Return Hashtable
					 			, sYdStrGtrCd 		// 야드저장집합코드
					 			, sYdMtlOrdGp 		// 야드재료지시구분
					 			, sWrkMd			// 작업Mode
					 			, sBkupEqpYn 		// Backup설비유무
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB023 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB023_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB023", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB023()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드A열연코일스케줄코드부여기준
	 * 
	 * @param	String sYdStrGtrCd 		// 야드저장집합코드
	 * @param	String sYdMtlOrdGp 		// 야드재료지시구분
	 * @param	String sWrkMd			// 작업Mode
	 * @param	String sBkupEqpYn 		// Backup설비유무
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB024(String sYdStrGtrCd, String  sYdMtlOrdGp, String sWrkMd
			, String sBkupEqpYn, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB024";
		
		String szItems [] =new String[] {
				 "야드대상스케줄코드"			// 야드대상스케줄코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule2.YDB024( htRule			// Return Hashtable
					 			, sYdStrGtrCd 		// 야드저장집합코드
					 			, sYdMtlOrdGp 		// 야드재료지시구분
					 			, sWrkMd			// 작업Mode
					 			, sBkupEqpYn 		// Backup설비유무
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB024 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB024_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB024", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB024()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드B열연코일스케줄코드부여기준
	 * 
	 * @param	String sYdStrGtrCd 		// 야드저장집합코드
	 * @param	String sYdMtlOrdGp 		// 야드재료지시구분
	 * @param	String sWrkMd			// 작업Mode
	 * @param	String sBkupEqpYn 		// Backup설비유무
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB025(String sYdStrGtrCd, String  sYdMtlOrdGp, String sWrkMd
			, String sBkupEqpYn, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB025";
		
		String szItems [] =new String[] {
				 "야드대상스케줄코드"			// 야드대상스케줄코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule2.YDB025( htRule			// Return Hashtable
					 			, sYdStrGtrCd 		// 야드저장집합코드
					 			, sYdMtlOrdGp 		// 야드재료지시구분
					 			, sWrkMd			// 작업Mode
					 			, sBkupEqpYn 		// Backup설비유무
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB025 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB025_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB025", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB025()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드C열연코일스케줄코드부여기준
	 * 
	 * @param	String sYdStrGtrCd 		// 야드저장집합코드
	 * @param	String sYdMtlOrdGp 		// 야드재료지시구분
	 * @param	String sWrkMd			// 작업Mode
	 * @param	String sBkupEqpYn 		// Backup설비유무
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB026(String sYdStrGtrCd, String  sYdMtlOrdGp, String sWrkMd
			, String sBkupEqpYn, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB026";
		
		String szItems [] =new String[] {
				 "야드대상스케줄코드"			// 야드대상스케줄코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule2.YDB026( htRule			// Return Hashtable
					 			, sYdStrGtrCd 		// 야드저장집합코드
					 			, sYdMtlOrdGp 		// 야드재료지시구분
					 			, sWrkMd			// 작업Mode
					 			, sBkupEqpYn 		// Backup설비유무
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB026 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB026_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB026", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB026()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드후판제품스케줄코드부여기준
	 * 
	 * @param	String sYdStrGtrCd 		// 야드저장집합코드
	 * @param	String sYdMtlOrdGp 		// 야드재료지시구분
	 * @param	String sWrkMd			// 작업Mode
	 * @param	String sBkupEqpYn 		// Backup설비유무
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB027(String sYdStrGtrCd, String  sYdMtlOrdGp, String sWrkMd
			, String sBkupEqpYn, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB027";
		
		String szItems [] =new String[] {
				 "야드대상스케줄코드"			// 야드대상스케줄코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule2.YDB027( htRule			// Return Hashtable
					 			, sYdStrGtrCd 		// 야드저장집합코드
					 			, sYdMtlOrdGp 		// 야드재료지시구분
					 			, sWrkMd			// 작업Mode
					 			, sBkupEqpYn 		// Backup설비유무
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB027 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB027_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB027", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB027()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드A열연코일차량Point부여기준
	 * 
	 * @param	String sYdGp			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB028(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB028";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule2.YDB028( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB028 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB028_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB028", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB028()
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드B열연코일차량Point부여기준
	 * 
	 * @param	String sYdGp			// 야드구분
	 * 
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return 
	 * @throws  RuleException
	 */	
	public boolean getYDB029(String sYdGp, JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB029";
		
		String szItems [] =new String[] {
				 "야드포인트코드"			// 야드포인트코드
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule2.YDB029( htRule			// Return Hashtable
					 			, sYdGp 			// 야드구분
					       	    );
			
			if(!bRtc){
				szMsg="Rule YDB029 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB029_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB029", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB029()
	
	/**
	 *	A후판슬라브야드-이적권하분리여부를 반환하는 메소드
	 * @param jdtoRcd					OUT PARAM
	 * @return
	 */
	public static boolean getYDB295(JDTORecord jdtoRcd){
	
		String szMsg="";
		String szMethodName ="getYDB295";
		
		String szItems [] =new String[] {
				 "USAGE_YN"								//사용여부
		};

		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;

		JDTORecord ydb000Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule2.YDB295( htRule							// Return Hashtable
								);
			
			if(!bRtc){
				szMsg="Rule YDB295 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);

				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB295_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB295", szItems, htRule, ydb000Rcd, szClassName );
	
		
	} // end of getYDB295()
	
	/**
	 * A후판슬라브야드에 적용되는 Pallet상차매수 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB296(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB296";

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
				bRtc = breRule2.YDB296_NEW( htRule			// Return Hashtable
            							);
			}
			else {
				bRtc=breRule2.YDB296( htRule			// Return Hashtable
									);
			}
			
			
			
			if(!bRtc){
				szMsg ="Rule YDB296 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB296_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB296", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB296()
	
	/**
	 * A후판슬라브야드에 적용되는 Trailer상차매수 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB297(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB297";

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
				bRtc = breRule2.YDB297_NEW( htRule			// Return Hashtable
            							);
			}
			else {
				bRtc=breRule2.YDB297( htRule			// Return Hashtable
									);
			}

			
			if(!bRtc){
				szMsg ="Rule YDB297 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB297_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB297", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB297()
	
	
	/**
	 * A후판슬라브야드에 장입LOT 편성 묶음수량값 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB298(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB298";

		String szItems [] =new String[] {
				 "LOT_DEP_CNT"		// 장입LOT편성묶음수량
				 ,"YD_STK_BED_H_MAX"
				 ,"YD_STK_BED_WT_MAX"
				 ,"YD_STK_BED_LYR_MAX"
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		
		JDTORecord ydb001Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule2.YDB298( htRule			// Return Hashtable
					              );
			
			
			if(!bRtc){
				szMsg ="Rule YDB298 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB298_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB298", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB298()

	
	
	/**
	 * A후판슬라브야드에 적용되는 차량상차LOT편성 자동유무 판단값 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB299(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB299";

		String szItems [] =new String[] {
				 "AUTO_LOT_YN"		// 자동LOT편성사용유무
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		
		JDTORecord ydb001Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule2.YDB299( htRule			// Return Hashtable
					              );
			
			
			if(!bRtc){
				szMsg ="Rule YDB299 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB299_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB299", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB299()
	
	
	
	
	/**
	 * A후판슬라브야드에 적용되는 보급요구 자동유무 판단값 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB230(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB230";

		String szItems [] =new String[] {
				 "AUTO_SUP_YN"		// 자동보급사용유무
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		
		JDTORecord ydb001Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule2.YDB230( htRule			// Return Hashtable
					              );
			
			
			if(!bRtc){
				szMsg ="Rule YDB230 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB230_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB230", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB230()

	

	
	
  //---------------------------------------------------------------------------
} // end of class
