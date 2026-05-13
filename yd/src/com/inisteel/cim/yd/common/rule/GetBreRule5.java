/**
 * 
 */
package com.inisteel.cim.yd.common.rule;

import java.util.Hashtable;

import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;

import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.tcconst.MakeTcCS;
import com.metis.rapi4j.RuleException;

/**
 * @author Administrator
 *
 */
public class GetBreRule5 {
	private static boolean bDebugFlag=true;
	
	private static final YdUtils ydUtils = new YdUtils();
	
	private static final String szClassName = GetBreRule5.class.getName();

	private static final BRERule5 breRule5 = new BRERule5();
	
	/**
	 * C열연코일제품야드에 적용되는 차량상차LOT편성 자동유무 판단값 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB599(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB599";

		String szItems [] =new String[] {
				 "AUTO_LOT_YN"		// 자동LOT편성사용유무
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		
		JDTORecord ydb001Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule5.YDB599( htRule			// Return Hashtable
					              );
			
			
			if(!bRtc){
				szMsg ="Rule YDB599 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB599_ColCnt");
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
			
			
			
		
		}catch (RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
			return false;

		}// end of try
		
		
		return ydUtils.cvtTblToRec("YDB599", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB599()
	
	
	
	
	
	/**
	 *  권오창 - 코드매핑 동분산 구분 2010.01.12
	 * 
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB001(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		Hashtable htRule          = new Hashtable();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();

		String szMethodName       = "getYDB001";
		String szMsg              = "";
		String szOperationName    = "코드매핑 동분산 구분";
		String szSCARFING_YN      = "";
		String szSCARFING_DONE_YN = "";
		String szHCR_GP           = "";
		String szORD_YEOJAE_GP    = "";
		String szSTL_APPEAR_GP    = "";
		String szItems []         = new String[] {
													"YD_BAY_GP"    // 야드동구분
												 };
		int nColCnt               = 0;
		boolean bRtc              = false;
		boolean bRet              = false;
		
		
		try{
			// Debug MSG
			//ydUtils.putLog(szClassName, szMethodName, "\n=======================getYDB001() IN==========================\n", YdConstant.DEBUG);	
			//ydUtils.displayRecord(szOperationName, inRec);
			//ydUtils.putLog(szClassName, szMethodName, "\n===============================================================\n", YdConstant.DEBUG);				

			// 파라미터 추출
			szSCARFING_YN      = ydDaoUtils.paraRecChkNull(inRec, "SCARFING_YN");
			szSCARFING_DONE_YN = ydDaoUtils.paraRecChkNull(inRec, "SCARFING_DONE_YN");
			szHCR_GP           = ydDaoUtils.paraRecChkNull(inRec, "HCR_GP");
			szORD_YEOJAE_GP    = ydDaoUtils.paraRecChkNull(inRec, "ORD_YEOJAE_GP");
			szSTL_APPEAR_GP    = ydDaoUtils.paraRecChkNull(inRec, "STL_APPEAR_GP");
		 	
			// BRE 호출
			PropertyService jprop = PropertyService.getInstance();			
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			if("1.8".equals(javaVersion)){ 
				bRtc = breRule5.YDB001_NEW(htRule, szSCARFING_YN, szSCARFING_DONE_YN, szHCR_GP, szORD_YEOJAE_GP, szSTL_APPEAR_GP);
			}
			else {
				bRtc = breRule5.YDB001(htRule, szSCARFING_YN, szSCARFING_DONE_YN, szHCR_GP, szORD_YEOJAE_GP, szSTL_APPEAR_GP);
			}
			
			if(!bRtc){
				szMsg = "Rule YDB001 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return false;
			}
			
			// 카운트 추출
			Object objX = htRule.get("YDB001_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				szMsg = "BRE Result - ColCnt=" + nColCnt;
				if(bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			if(nColCnt <= 0){
				szMsg = "Column Count(" + nColCnt + ") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return false;
			}
		}catch (RuleException re){
			szMsg = "RuleException : " + re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return false;
		}
		
		bRet = ydUtils.cvtTblToRec("YDB001", szItems, htRule, outRec, szClassName);
		
		// Debug MSG
		//ydUtils.putLog(szClassName, szMethodName, "\n=======================getYDB001() OUT==========================\n", YdConstant.DEBUG);	
		//ydUtils.displayRecord(szOperationName, outRec);
		//ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);				

		return bRet; 		
	} 

	
	
	
	
	/**
	 *  권오창 - 코드매핑 목표야드 2010.01.12
	 * 
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB002(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		Hashtable htRule          = new Hashtable();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();

		String szMethodName       = "getYDB002";
		String szMsg              = "";
		String szOperationName    = "코드매핑 목표야드";
		String szCurrYD           = "";
		String szSLAB_WO_RT_CD    = "";
		String szSTL_PROG_CD      = "";
		String szARR_WLOC_CD      = "";
		String szSTL_APPEAR_GP    = "";
		String szItems []         = new String[] {
													"YD_AIM_YD_GP"    // 목표야드구분
												 };
		int nColCnt               = 0;
		boolean bRtc              = false;
		boolean bRet              = false;
		
		
		try{
			// Debug MSG
			//ydUtils.putLog(szClassName, szMethodName, "\n=======================getYDB002() IN==========================\n", YdConstant.DEBUG);	
			//ydUtils.displayRecord(szOperationName, inRec);
			//ydUtils.putLog(szClassName, szMethodName, "\n===============================================================\n", YdConstant.DEBUG);				

			// 파라미터 추출
			szCurrYD        = ydDaoUtils.paraRecChkNull(inRec, "YD_GP");
			szSLAB_WO_RT_CD = ydDaoUtils.paraRecChkNull(inRec, "SLAB_WO_RT_CD");
			szSTL_PROG_CD   = ydDaoUtils.paraRecChkNull(inRec, "CURR_PROG_CD");
			szARR_WLOC_CD   = ydDaoUtils.paraRecChkNull(inRec, "ARR_WLOC_CD");

			szMsg = "Rule YDB002 in:"+szCurrYD+","+szSLAB_WO_RT_CD+","+szSTL_PROG_CD+","+szARR_WLOC_CD;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.INFO);
			
			// BRE 호출 
			PropertyService jprop = PropertyService.getInstance();			
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			if("1.8".equals(javaVersion)){ 
				bRtc = breRule5.YDB002_NEW(htRule, szCurrYD, szSLAB_WO_RT_CD, szSTL_PROG_CD, szARR_WLOC_CD);
			}else{
				bRtc = breRule5.YDB002(htRule, szCurrYD, szSLAB_WO_RT_CD, szSTL_PROG_CD, szARR_WLOC_CD);
			}
			
			if(!bRtc){
				szMsg = "Rule YDB002 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return false;
			}
			
			// 카운트 추출
			Object objX = htRule.get("YDB002_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				szMsg = "BRE Result - ColCnt=" + nColCnt;
				if(bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			if(nColCnt <= 0){
				szMsg = "Column Count(" + nColCnt + ") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return false;
			}
		}catch (RuleException re){
			szMsg = "RuleException : " + re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return false;
		}
		
		bRet = ydUtils.cvtTblToRec("YDB002", szItems, htRule, outRec, szClassName);
		
		// Debug MSG
		//ydUtils.putLog(szClassName, szMethodName, "\n=======================getYDB002() OUT==========================\n", YdConstant.DEBUG);	
		//ydUtils.displayRecord(szOperationName, outRec);
		//ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);				

		return bRet; 		
	} 

	
	
	
	
	/**
	 *  권오창 - 코드매핑 목표동  2010.01.12
	 * 
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB003(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		Hashtable htRule           = new Hashtable();
		YdDaoUtils ydDaoUtils      = new YdDaoUtils();

		String szMethodName        = "getYDB003";
		String szMsg               = "";
		String szOperationName     = "코드매핑 목표동";
		String szYD_AIM_YD_GP      = "";
		String szSLAB_WO_RT_CD     = "";
		String szSTL_APPEAR_GP     = "";
		String szBayDistributionGp = "";
		String szItems []         = new String[] {
												     "YD_AIM_BAY_GP"    // 목표동구분
			 									 };
		int nColCnt               = 0;
		boolean bRtc              = false;
		boolean bRet              = false;


		
		try{
			// Debug MSG
			//ydUtils.putLog(szClassName, szMethodName, "\n=======================getYDB003() IN==========================\n", YdConstant.DEBUG);	
			//ydUtils.displayRecord(szOperationName, inRec);
			//ydUtils.putLog(szClassName, szMethodName, "\n===============================================================\n", YdConstant.DEBUG);				

			// 파라미터 추출
			szYD_AIM_YD_GP      = ydDaoUtils.paraRecChkNull(inRec, "YD_AIM_YD_GP");
			szSLAB_WO_RT_CD     = ydDaoUtils.paraRecChkNull(inRec, "SLAB_WO_RT_CD");
			szSTL_APPEAR_GP     = ydDaoUtils.paraRecChkNull(inRec, "STL_APPEAR_GP");
			szBayDistributionGp = ydDaoUtils.paraRecChkNull(inRec, "YD_BAY_GP");

			
			// BRE 호출 
			PropertyService jprop = PropertyService.getInstance();			
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			if("1.8".equals(javaVersion)){ 
				bRtc = breRule5.YDB003_NEW(htRule, szYD_AIM_YD_GP, szSLAB_WO_RT_CD, szSTL_APPEAR_GP, szBayDistributionGp);
			}else{
				bRtc = breRule5.YDB003(htRule, szYD_AIM_YD_GP, szSLAB_WO_RT_CD, szSTL_APPEAR_GP, szBayDistributionGp);	
			}
			
			if(!bRtc){
				szMsg = "Rule YDB003 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return false;
			}
			
			// 카운트 추출
			Object objX = htRule.get("YDB003_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				szMsg = "BRE Result - ColCnt=" + nColCnt;
				if(bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			if(nColCnt <= 0){
				szMsg = "Column Count(" + nColCnt + ") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return false;
			}
		}catch (RuleException re){
			szMsg = "RuleException : " + re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return false;
		}
		
		bRet = ydUtils.cvtTblToRec("YDB003", szItems, htRule, outRec, szClassName);
		
		// Debug MSG
		//ydUtils.putLog(szClassName, szMethodName, "\n=======================getYDB003() OUT==========================\n", YdConstant.DEBUG);	
		//ydUtils.displayRecord(szOperationName, outRec);
		//ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);				

		return bRet; 		
	} 

	
	
	
	
	/**
	 *  권오창 - 코드매핑 목표행선구분  2010.01.12
	 * 
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB004(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		Hashtable htRule           = new Hashtable();
		YdDaoUtils ydDaoUtils      = new YdDaoUtils();

		String szMethodName        = "getYDB004";
		String szMsg               = "";
		String szOperationName     = "코드매핑 목표행선구분";
		String szYD_AIM_YD_GP      = "";
		String szSLAB_WO_RT_CD     = "";
		String szSTL_APPEAR_GP     = "";
		String szSTL_PROG_CD       = "";
		String szBayDistributionGp = "";
		String szItems []         = new String[] {
												     "YD_AIM_RT_GP"    // 목표행선구분
			 									 };
		int nColCnt               = 0;
		boolean bRtc              = false;
		boolean bRet              = false;


		
		try{
			// Debug MSG
			//ydUtils.putLog(szClassName, szMethodName, "\n=======================getYDB004() IN==========================\n", YdConstant.DEBUG);	
			//ydUtils.displayRecord(szOperationName, inRec);
			//ydUtils.putLog(szClassName, szMethodName, "\n===============================================================\n", YdConstant.DEBUG);				

			// 파라미터 추출
			szSLAB_WO_RT_CD     = ydDaoUtils.paraRecChkNull(inRec, "SLAB_WO_RT_CD");
			szSTL_APPEAR_GP     = ydDaoUtils.paraRecChkNull(inRec, "STL_APPEAR_GP");
			szBayDistributionGp = ydDaoUtils.paraRecChkNull(inRec, "YD_BAY_GP");
			szSTL_PROG_CD       = ydDaoUtils.paraRecChkNull(inRec, "STL_PROG_CD");

			
			// BRE 호출 
			PropertyService jprop = PropertyService.getInstance();			
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			if("1.8".equals(javaVersion)){ 
				bRtc = breRule5.YDB004_NEW(htRule, szSLAB_WO_RT_CD, szSTL_PROG_CD, szBayDistributionGp, szSTL_APPEAR_GP);
			}else{
				bRtc = breRule5.YDB004(htRule, szSLAB_WO_RT_CD, szSTL_PROG_CD, szBayDistributionGp, szSTL_APPEAR_GP);
			}
			if(!bRtc){
				szMsg = "Rule YDB004 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return false;
			}
			
			// 카운트 추출
			Object objX = htRule.get("YDB004_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				szMsg = "BRE Result - ColCnt=" + nColCnt;
				if(bDebugFlag)
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			if(nColCnt <= 0){
				szMsg = "Column Count(" + nColCnt + ") Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return false;
			}
		}catch (RuleException re){
			szMsg = "RuleException : " + re.getErrMsg();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return false;
		}
		
		bRet = ydUtils.cvtTblToRec("YDB004", szItems, htRule, outRec, szClassName);
		
		// Debug MSG
		//ydUtils.putLog(szClassName, szMethodName, "\n=======================getYDB004() OUT==========================\n", YdConstant.DEBUG);	
		//ydUtils.displayRecord(szOperationName, outRec);
		//ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);				

		return bRet; 		
	} 	
}
