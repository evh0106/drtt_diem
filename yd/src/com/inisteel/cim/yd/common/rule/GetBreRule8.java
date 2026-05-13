/*
 * @(#) 2후판정정야드 에서 사용하는 BRE RULE Managed Class
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/12/04
 *
 * @description		2후판정정야드 에서 사용하는 BRE RULE Managed Class
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/12/04   김현우      김현우       최초작성
 */

package com.inisteel.cim.yd.common.rule;

import java.util.Hashtable;

import jspeed.base.record.JDTORecord;

import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.metis.rapi4j.RuleException;

import jspeed.base.property.PropertyService;
import com.metis.rapi5j.*;

//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
//
//	com.inisteel.cim.yd RULE Managed Class1
//				YDB801 ~ YDB802 , YDB804
//
//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

public class GetBreRule8 {

	private static boolean bDebugFlag = true;

	private static final YdUtils ydUtils	= new YdUtils();

	private static final String SZ_CLASS_NAME = GetBreRule8.class.getName();

	private static final BRERule8 BRE_RULE8 = new BRERule8();

	/**
	 * 후판정정야드에 적용되는 정정야드재료폭구분 반환하는 메소드
	 * @param
	 * @param	String	sYdGp			// 정정야드구분
	 * @param	double  dMtlW			// 재료폭
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return
	 * @throws  RuleException
	 */
	public static boolean getYDB801(String sYdGp, double dMtlW, JDTORecord jdtoRcd){

		String szMsg="";
		String szMethodName="getYDB801";

		String szItems[] = new String[] {
				 "YD_MTL_W_GP"		// 야드재료폭구분
		};

		Hashtable htRule =new Hashtable();

		int nColCnt = 0;

		boolean bRtc = false;

		JDTORecord ydb001Rcd = jdtoRcd;

		try{
			PropertyService jprop = PropertyService.getInstance();
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			if("1.8".equals(javaVersion)){ 
				bRtc=BRE_RULE8.YDB801_NEW( htRule,			// Return Hashtable
						sYdGp, 			// 야드구분
						dMtlW 			// 야드재료폭
		              );
			}
			else{
			bRtc=BRE_RULE8.YDB801( htRule,			// Return Hashtable
									sYdGp, 			// 야드구분
									dMtlW 			// 야드재료폭
					              );
			}

			if(!bRtc){
				szMsg ="Rule YDB801 Getting Error";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg,1);

				return false;

			} // end of if()

			Object objX =htRule.get("YDB801_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				szMsg="BRE Result - ColCnt="+nColCnt;
				if(bDebugFlag) {
					ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg,4);
				}
			}

			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg,1);

				return false;

			}

		} catch (RuleException re) {

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			return false;

		}// end of try

		return ydUtils.cvtTblToRec("YDB801", szItems, htRule, ydb001Rcd, SZ_CLASS_NAME );

	} // end of getYDB801()

	/**
	 * 후판정정야드에 적용되는 정정야드재료길이구분 반환하는 메소드
	 * @param
	 * @param	String	sYdGp			// 정정야드구분
	 * @param	double  dMtlL			// 재료길이
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return
	 * @throws  RuleException
	 */
	public static boolean getYDB802(String sYdGp, int iMtlL, JDTORecord jdtoRcd){

		String szMsg="";
		String szMethodName="getYDB802";

		String szItems[] = new String[] {
				 "YD_MTL_L_GP"		// 야드재료길이구분
		};

		Hashtable htRule =new Hashtable();

		int nColCnt = 0;

		boolean bRtc = false;

		JDTORecord ydb001Rcd = jdtoRcd;

		try{
			PropertyService jprop = PropertyService.getInstance();
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			if("1.8".equals(javaVersion)){ 
				bRtc=BRE_RULE8.YDB802_NEW( htRule,			// Return Hashtable
						sYdGp, 			// 야드구분
						iMtlL 			// 야드재료길이
		              );
			}
			else{
				bRtc=BRE_RULE8.YDB802( htRule,			// Return Hashtable
						sYdGp, 			// 야드구분
						iMtlL 			// 야드재료길이
		              );
			}

			if(!bRtc){
				szMsg ="Rule YDB802 Getting Error";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg,1);

				return false;

			} // end of if()

			Object objX =htRule.get("YDB802_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				szMsg="BRE Result - ColCnt="+nColCnt;
				if( bDebugFlag){
					ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg,4);
				}
			}

			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg,1);

				return false;

			}

		} catch (RuleException re) {

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			return false;

		}// end of try

		return ydUtils.cvtTblToRec("YDB802", szItems, htRule, ydb001Rcd, SZ_CLASS_NAME );

	} // end of getYDB802()

	/**
	 * 후판정정야드에 적용되는 TO위치검색기준을 반환하는 메소드
	 * @param	pYdStkColGp		야드적치열구분
	 * @param	pYdSchCd		야드스케쥴코드
	 * @param	pUsMaintmatl	보수완료구분
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return
	 * @throws  RuleException
	 */
	public static boolean getYDB805(String pYdStkColGp, String pYdSchCd, String pUsMaintmatl, JDTORecord jdtoRcd){

		String szMsg="";
		String szMethodName="getYDB805";

		String szItems[] = new String[] {
				 "YD_TO_LOC_GUIDE"		// 야드To위치Guide
				,"PL_SHEAR_YD_GRP_GP"	// 후판정정야드그룹구분
		};

		Hashtable htRule =new Hashtable();

		int nColCnt = 0;

		boolean bRtc = false;

		JDTORecord ydb001Rcd = jdtoRcd;

		try{
			
			PropertyService jprop = PropertyService.getInstance();
			
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			if("1.8".equals(javaVersion)){ 
				bRtc=BRE_RULE8.YDB805_NEW( 	htRule,				// Return Hashtable
						pYdStkColGp, 		// 야드적치열구분
						pYdSchCd,			// 야드스케쥴코드
						pUsMaintmatl		// 보수완료구분
		              );
			}
			else {
				bRtc=BRE_RULE8.YDB805( 	htRule,				// Return Hashtable
						pYdStkColGp, 		// 야드적치열구분
						pYdSchCd,			// 야드스케쥴코드
						pUsMaintmatl		// 보수완료구분
		              );
			}
			

			if(!bRtc){
				szMsg ="Rule YDB805 Getting Error";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg,1);

				return false;

			} // end of if()

			Object objX =htRule.get("YDB805_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				szMsg="BRE Result - ColCnt="+nColCnt;
				if(bDebugFlag) {
					ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, 4);
				}
			}

			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg,1);

				return false;

			}

		} catch (com.metis.rapi5j.RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			
			return false;

		}catch (RuleException re) {

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			return false;

		}// end of try

		return ydUtils.cvtTblToRec("YDB805", szItems, htRule, ydb001Rcd, SZ_CLASS_NAME );

	} // end of getYDB801()

	/**********************************************************
	* 1후판정정추가 SJH16
	**********************************************************/	 	
	/**
	 * 1후판정정야드에 적용되는 TO위치검색기준을 반환하는 메소드
	 * @param	pYdStkColGp		야드적치열구분
	 * @param	pYdSchCd		야드스케쥴코드
	 * @param	pUsMaintmatl	보수완료구분
	 * @param 	JDTORecord jdtoRcd		// Target JDTORecord
	 * @return
	 * @throws  RuleException
	 */
	public static boolean getYDB810(String pYdStkColGp, String pYdSchCd, String pUsMaintmatl, JDTORecord jdtoRcd){

		String szMsg="";
		String szMethodName="getYDB810";

		String szItems[] = new String[] {
				 "YD_TO_LOC_GUIDE"		// 야드To위치Guide
				,"PL_SHEAR_YD_GRP_GP"	// 후판정정야드그룹구분
		};

		Hashtable htRule =new Hashtable();

		int nColCnt = 0;

		boolean bRtc = false;

		JDTORecord ydb001Rcd = jdtoRcd;

		try{
			PropertyService jprop = PropertyService.getInstance();
			
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			if("1.8".equals(javaVersion)){ 
				bRtc=BRE_RULE8.YDB810_NEW( 	htRule,				// Return Hashtable
						pYdStkColGp, 		// 야드적치열구분
						pYdSchCd,			// 야드스케쥴코드
						pUsMaintmatl		// 보수완료구분
		              );
			}
			else {
				bRtc=BRE_RULE8.YDB810( 	htRule,				// Return Hashtable
						pYdStkColGp, 		// 야드적치열구분
						pYdSchCd,			// 야드스케쥴코드
						pUsMaintmatl		// 보수완료구분
		              );
			}



			if(!bRtc){
				szMsg ="Rule YDB810 Getting Error";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg,1);

				return false;

			} // end of if()

			Object objX =htRule.get("YDB810_ColCnt");
			if(objX instanceof Integer){
				nColCnt =((Integer)objX).intValue();
				szMsg="BRE Result - ColCnt="+nColCnt;
				if(bDebugFlag) {
					ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, 4);
				}
			}

			if( nColCnt<=0){

				szMsg="Column Count("+nColCnt+") Error";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg,1);

				return false;

			}

		} catch (com.metis.rapi5j.RuleException re){

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			
			return false;

		}catch (RuleException re) {

			szMsg="RuleException : "+re.getErrMsg();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			return false;

		}// end of try

		return ydUtils.cvtTblToRec("YDB810", szItems, htRule, ydb001Rcd, SZ_CLASS_NAME );

	} // end of getYDB810()
	
}
