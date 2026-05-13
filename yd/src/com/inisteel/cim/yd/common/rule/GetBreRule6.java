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
import com.metis.rapi4j.ResultData;
import com.metis.rapi4j.RuleException;

import jspeed.base.property.PropertyService;
import com.metis.rapi5j.*;

/**
 * @author Administrator
 *
 */
public class GetBreRule6 {
private static boolean bDebugFlag=true;
	
	private static final YdUtils	ydUtils	=new YdUtils();
	
	private static final String szClassName = GetBreRule6.class.getName();

	private static final BRERule6 breRule6 = new BRERule6();
	
	/**
	 * 후판제품야드에 적용되는 차량상차LOT편성 자동유무 판단값 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB698(JDTORecord jdtoRcd){
		
		String szMsg="";
		String szMethodName="getYDB698";

		String szItems [] =new String[] {
				 "AUTO_LOT_YN"		// 자동LOT편성사용유무
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		
		JDTORecord ydb001Rcd =jdtoRcd;
		
		try{
			
			bRtc=breRule6.YDB698( htRule			// Return Hashtable
					              );
			
			
			if(!bRtc){
				szMsg ="Rule YDB698 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB698_ColCnt");
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
		
		
		return ydUtils.cvtTblToRec("YDB698", szItems, htRule, ydb001Rcd, szClassName );		
		
		
	} // end of getYDB698()
	
	
	/**
	 * 후판제품-SPAN별 야드구분 변환기준 판단값 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB671(JDTORecord jdtoRcd) throws JDTOException{
		
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();
		
		String szMsg="";
		String szMethodName="getYDB671";
		
		String szItems [] =new String[] {
				 "YDB671_RV01_YD_GP"		// 업무기준 YDB671 반환값#1 야드구분
				,"YDB671_RV02_YD_COL_W_GP"	// 업무기준 YDB671 반환값#2 야드적치열폭구분
				,"YDB671_RV03_YD_MILE"		// 업무기준 YDB671 반환값#3 거리
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		try{
			PropertyService jprop = PropertyService.getInstance();
			
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			if("1.8".equals(javaVersion)){ 
				bRtc = breRule6.YDB671_NEW( htRule			// Return Hashtable
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_BAY_GP") //입력파라메터 변수#1 : 야드동구분
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_EQP_GP") //입력파라메터 변수#2 : 설비구분(여기서는 span )
			              );
			}
			else {
				bRtc=breRule6.YDB671( htRule			// Return Hashtable
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_BAY_GP") //입력파라메터 변수#1 : 야드동구분
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_EQP_GP") //입력파라메터 변수#2 : 설비구분(여기서는 span )
			              );
			}
			
			
			if(!bRtc){
				szMsg ="Rule YDB671 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB671_ColCnt");
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
		
		return ydUtils.cvtTblToRec("YDB671", szItems, htRule, jdtoRcd, szClassName );		
		
	} // end of getYDB671()

	/**
	 * 후판제품야드 제품 폭 MIN MAX 값 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB672(JDTORecord jdtoRcd) throws JDTOException{
		
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();
		
		String szMsg="";
		String szMethodName="getYDB672";
		
		String szItems [] =new String[] {
				 "YDB672_RV01_W_MIN_VAL"	// 업무기준 YDB672 반환값#1 폭 MIN VALUE
				,"YDB672_RV02_W_MAX_VAL"	// 업무기준 YDB672 반환값#2 폭 MAX VALUE
				,"YDB672_RV03_COL_H_MAX"	// 업무기준 YDB672 반환값#3 열 높이 MAX
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		try{
			PropertyService jprop = PropertyService.getInstance();
			
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			if("1.8".equals(javaVersion)){ 
				bRtc=breRule6.YDB672_NEW( htRule			// Return Hashtable
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_GP") //입력파라메터 변수#1 : 야드구분
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_STK_BED_W_GP") //입력파라메터 변수#2 : 야드적치Bed폭구분
			              );
			}
			else {
				bRtc=breRule6.YDB672( htRule			// Return Hashtable
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_GP") //입력파라메터 변수#1 : 야드구분
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_STK_BED_W_GP") //입력파라메터 변수#2 : 야드적치Bed폭구분
			              );
			}
		
			
			if(!bRtc){
				szMsg ="Rule YDB672 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB672_ColCnt");
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
		
		return ydUtils.cvtTblToRec("YDB672", szItems, htRule, jdtoRcd, szClassName );		
		
	} // end of getYDB672()
	
	/**
	 * 후판제품야드 제품 길이 MIN MAX 값 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB673(JDTORecord jdtoRcd) throws JDTOException{
		
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();
		
		String szMsg="";
		String szMethodName="getYDB673";
		
		String szItems [] =new String[] {
				 "YDB673_RV01_L_MIN_VAL"	// 업무기준 YDB673 반환값#1 길이 MIN VALUE
				,"YDB673_RV02_L_MAX_VAL"	// 업무기준 YDB673 반환값#2 길이 MAX VALUE
				,"YDB673_RV03_L_MILE"		// 업무기준 YDB673 반환값#3 거리
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		try{
			PropertyService jprop = PropertyService.getInstance();
			
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			if("1.8".equals(javaVersion)){ 
				bRtc=breRule6.YDB673_NEW( htRule			// Return Hashtable
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_GP") //입력파라메터 변수#1 : 야드구분
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_STK_BED_L_GP") //입력파라메터 변수#2 : 야드적치Bed길이구분
			              );
			}
			else {
				bRtc=breRule6.YDB673( htRule			// Return Hashtable
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_GP") //입력파라메터 변수#1 : 야드구분
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_STK_BED_L_GP") //입력파라메터 변수#2 : 야드적치Bed길이구분
			              );
			}

			
			if(!bRtc){
				szMsg ="Rule YDB673 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB673_ColCnt");
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
		
		return ydUtils.cvtTblToRec("YDB673", szItems, htRule, jdtoRcd, szClassName );		
		
	} // end of getYDB673()	
	
	
	/**
	 * 후판제품야드 3기 기능 적용여부 (테스트용) 값 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB670(JDTORecord jdtoRcd) throws JDTOException{
		
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();
		
		String szMsg="";
		String szMethodName="getYDB670";
		
		String szItems [] =new String[] {
				 "YDB670_RV01_USAGE_YN"	// 업무기준 YDB670 반환값#1 사용여부(Y/N)
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		try{
			PropertyService jprop = PropertyService.getInstance();
			
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			if("1.8".equals(javaVersion)){ 
				bRtc=breRule6.YDB670_NEW( htRule			// Return Hashtable
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "3G_FNC_ID") //입력파라메터 변수#1 : 3기 기능ID
						,"" //입력파라메터 변수#2 : 기능설명 (값 없어도 됨)
			              );
			}
			else {
				bRtc=breRule6.YDB670( htRule			// Return Hashtable
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "3G_FNC_ID") //입력파라메터 변수#1 : 3기 기능ID
						,"" //입력파라메터 변수#2 : 기능설명 (값 없어도 됨)
			              );
			}
	
			
			if(!bRtc){
				szMsg ="Rule YDB670 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB670_ColCnt");
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
		
		return ydUtils.cvtTblToRec("YDB670", szItems, htRule, jdtoRcd, szClassName );		
		
	} // end of getYDB670()	
	
	/**
	 * 후판제품창고 BOOKOUT_LOC 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB674(JDTORecord jdtoRcd) throws JDTOException{
		
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();
		
		String szMsg="";
		String szMethodName="getYDB674";
		
		String szItems [] =new String[] {
				 "YDB674_RV01_YD_BOOK_OUT_LOC"	// 업무기준 YDB674 반환값#1 YD_BOOK_OUT_LOC
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		try{
			PropertyService jprop= PropertyService.getInstance();
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			//업무기준 호출
			if("1.8".equals(javaVersion)){ 
				bRtc=breRule6.YDB674_NEW( htRule			// Return Hashtable
								,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_GP") 			//입력파라메터 변수#1 : 야드구분
								,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_BAY_GP") 		//입력파라메터 변수#2 : 야드동구분
								,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_STK_BED_NO")  	//입력파라메터 변수#3 : 야드적치Bed번호
					            );
			}
			else{
				bRtc=breRule6.YDB674( htRule			// Return Hashtable
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_GP") 			//입력파라메터 변수#1 : 야드구분
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_BAY_GP") 		//입력파라메터 변수#2 : 야드동구분
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_STK_BED_NO")  	//입력파라메터 변수#3 : 야드적치Bed번호
			            );				
			}
			
			if(!bRtc){
				szMsg ="Rule YDB674 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB674_ColCnt");
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
		
		return ydUtils.cvtTblToRec("YDB674", szItems, htRule, jdtoRcd, szClassName );		
		
	} // end of getYDB674()	
	
	
	/**
	 * 후판제품창고 좌표계산 기준 반환하는 메소드
	 * @param jdtoRcd
	 * @return
	 */
	public static boolean getYDB675(JDTORecord jdtoRcd) throws JDTOException{
		
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();
		
		String szMsg="";
		String szMethodName="getYDB675";
		
		String szItems [] =new String[] {
				 "YDB675_RV01_YD_CRN_GRAB_EA"			// 업무기준 YDB675 반환값#1 야드크레인Grab개수
				,"YDB675_RV02_YD_CRN_GRAB1_BM_MIN_L"	// 업무기준 YDB675 반환값#2 야드크레인Grab1Beam최소길이
				,"YDB675_RV03_YD_CRN_GRAB1_BM_EXPN_L"	// 업무기준 YDB675 반환값#3 야드크레인Grab1Beam신축(최대)길이
				,"YDB675_RV04_YD_CRN_GRAB2_BM_MIN_L"	// 업무기준 YDB675 반환값#4 야드크레인Grab2Beam최소길이
				,"YDB675_RV05_YD_CRN_GRAB2_BM_EXPN_L"	// 업무기준 YDB675 반환값#5 야드크레인Grab2Beam신축(최대)길이
				,"YDB675_RV06_YD_CRN_BM_MSTT_MGNT_GAP"	// 업무기준 YDB675 반환값#6 야드크레인Beam최외각Magnet간격 (Beam간격)
				,"YDB675_RV07_YD_STK_BED_XAXIS_TOL"		// 업무기준 YDB675 반환값#7 야드적치BedX축허용오차
				,"YDB675_RV08_YD_STK_BED_YAXIS_TOL"		// 업무기준 YDB675 반환값#8 야드적치BedY축허용오차
				,"YDB675_RV09_YD_CAR_WRK_XAXIS_TOL"		// 업무기준 YDB675 반환값#9 야드차량작업X축허용오차
				,"YDB675_RV010_YD_CAR_WRK_YAXIS_TOL"	// 업무기준 YDB675 반환값#10 야드차량착업Y축허용오차
				,"YDB675_RV011_YD_CRN_RULE_X_XYAXIS"	// 업무기준 YDB675 반환값#11 야드설비작업X축허용오차
				,"YDB675_RV012_YD_CRN_RULE_Y_XYAXIS"	// 업무기준 YDB675 반환값#12 야드설비작업Y축허용오차
		};
		
		Hashtable htRule =new Hashtable();
		
		int nColCnt=0;

		boolean bRtc=false;
		
		try{
			PropertyService jprop = PropertyService.getInstance();
			
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			if("1.8".equals(javaVersion)){ 
				bRtc=breRule6.YDB675_NEW( htRule			// Return Hashtable
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_EQP_ID") 			//입력파라메터 변수#1 : 크래인 설비 번호
			            );
			}
			else {
				bRtc=breRule6.YDB675( htRule			// Return Hashtable
						,ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_EQP_ID") 			//입력파라메터 변수#1 : 크래인 설비 번호
			            );
			}

			
			if(!bRtc){
				szMsg ="Rule YDB675 Getting Error";
				ydUtils.putLog(szClassName, szMethodName, szMsg,1);
			
				return false;
				
			} // end of if()
		
			Object objX =htRule.get("YDB675_ColCnt");
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
		
		return ydUtils.cvtTblToRec("YDB675", szItems, htRule, jdtoRcd, szClassName );		
		
	} // end of getYDB675()		
	
}
