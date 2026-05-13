/*
 * @(#) 송신 할 TC Data 생성
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/14
 *
 * @description		송신 할 TC Data 생성
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/14   김현우      김현우       최초작성 
 */ 

package com.inisteel.cim.yd.jplateyd.util;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;

/**
 * TC Code를 기반으로 송신 할 TC Code 생성
 * @author YHWHman
 *
 */
public class JPlateYdTcMgr {

	private static final String SZ_SESSION_NAME = JPlateYdTcMgr.class.getName();

	private JPlateYdUtils ydUtils = new JPlateYdUtils();

	/**
	 * 수신 한 Record를 기반으로 송신 할 TC Data 생성
	 *
	 * @param msgRecord, tcRecSet
	 * @return 생성 한 Key의 갯수
	 */
	public int makeTc(JDTORecord msgRecord, JDTORecordSet tcRecSet){

		String szMsg = "";
		String szMethodName = "makeTc";
		int    nRtc = -99;

		String szTcCode = "";

		try {
			szTcCode = ydUtils.getTcCode(msgRecord);
			if( (szTcCode == null) || "".equals(szTcCode)){
				szMsg = "TC Code("+szTcCode+") Error";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return -2;
			}
		}catch(Exception e){
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return -1;
		} // end of try-catch


		// ┏━━━━━━━━━━━━━━━━━━━┓
		//     Y7 : 후판정정야드L2 송신전문
		// ┗━━━━━━━━━━━━━━━━━━━┛
		// YDY7L001	저장위치제원정보
		if( "YDY7L001".equals(szTcCode)) {
			return JPlateYdMakeTcY7.makeYDY7L001(msgRecord, tcRecSet);
		// YDY7L002	저장품제원
		} else if( "YDY7L002".equals(szTcCode)) {
			return JPlateYdMakeTcY7.makeYDY7L002(msgRecord, tcRecSet);
		// YDY7L004	크레인작업지시
		} else if( "YDY7L004".equals(szTcCode)) {
			return JPlateYdMakeTcY7.makeYDY7L004(msgRecord, tcRecSet);
		// YDY7L005	크레인작업실적응답
		} else if( "YDY7L005".equals(szTcCode)) {
			return JPlateYdMakeTcY7.makeYDY7L005(msgRecord, tcRecSet);
		// YDY7L006	가이던스메세지
		} else if( "YDY7L006".equals(szTcCode)) {
			return JPlateYdMakeTcY7.makeYDY7L006(msgRecord, tcRecSet);
		// YDY7L007	크레인작업메세지
		} else if( "YDY7L007".equals(szTcCode)) {
			return JPlateYdMakeTcY7.makeYDY7L007(msgRecord, tcRecSet);

		// ┏━━━━━━━━━━━━━━━━━━━┓
		//     S1 : 2후판전단L2 송신전문
		// ┗━━━━━━━━━━━━━━━━━━━┛
		// YDS1L005	BOOK IN/OUT 실적
		} else if( "YDS1L005".equals(szTcCode)) {
			return JPlateYdMakeTcS1.makeYDS1L005(msgRecord, tcRecSet);

		// ┏━━━━━━━━━━━━━━━━━━━┓
		//     PP : 후판조업L3 송신전문
		// ┗━━━━━━━━━━━━━━━━━━━┛
		// YDPPJ011	저장위치변경정보
		} else if( "YDPPJ011".equals(szTcCode)) {
			return JPlateYdMakeTcPP.makeYDPPJ011(msgRecord, tcRecSet);
 
		// ┏━━━━━━━━━━━━━━━━━━━┓
		//     Y2 : 1후판정정야드L2 송신전문
		// ┗━━━━━━━━━━━━━━━━━━━┛
		} else if( "YDY2L001".equals(szTcCode)) {
			return JPlateYdMakeTcY2.makeYDY2L001(msgRecord, tcRecSet);

		} else if( "YDY2L002".equals(szTcCode)) {
			return JPlateYdMakeTcY2.makeYDY2L002(msgRecord, tcRecSet);

		} else if( "YDY2L004".equals(szTcCode)) {
				   JPlateYdMakeTcY2.makeYDY2L007TR(msgRecord);
			return JPlateYdMakeTcY2.makeYDY2L004(msgRecord, tcRecSet);
		
		} else if( "YDY2L004V2".equals(szTcCode)) {
			       JPlateYdMakeTcY2.makeYDY2L007TR(msgRecord);	
			return JPlateYdMakeTcY2.makeYDY2L004V2(msgRecord, tcRecSet);
			
		} else if( "YDY2L005".equals(szTcCode)) {
			return JPlateYdMakeTcY2.makeYDY2L005(msgRecord, tcRecSet);
		
		} else if( "YDY2L006".equals(szTcCode)) {
			return JPlateYdMakeTcY2.makeYDY2L006(msgRecord, tcRecSet);

		} else if( "YDY2L007".equals(szTcCode)) {
			return JPlateYdMakeTcY2.makeYDY2L007(msgRecord, tcRecSet);

		} else if( "YDY2L008".equals(szTcCode)) {
			return JPlateYdMakeTcY2.makeYDY2L008(msgRecord, tcRecSet);
			
		} else if( "YDY2L009".equals(szTcCode)) {
			return JPlateYdMakeTcY2.makeYDY2L009(msgRecord, tcRecSet);
			
		// ┏━━━━━━━━━━━━━━━━━━━┓
		//     PR : 1후판조업L3 송신전문
		// ┗━━━━━━━━━━━━━━━━━━━┛
		// YDPRJ011	저장위치변경정보
		} else if( "YDPRJ011".equals(szTcCode)) {
			return JPlateYdMakeTcPR.makeYDPRJ011(msgRecord, tcRecSet);

		// ┏━━━━━━━━━━━━━━━━━━━┓
		//     P2 : 1후판정정L2 송신전문
		// ┗━━━━━━━━━━━━━━━━━━━┛
		//  BOOK IN/OUT 실적
		} else if( "YDP2L501".equals(szTcCode)) {
			return JPlateYdMakeTcP2P3.makeYDP2L501(msgRecord, tcRecSet);

		// ┏━━━━━━━━━━━━━━━━━━━┓
		//     P3 : 1후판열처리L2 송신전문
		// ┗━━━━━━━━━━━━━━━━━━━┛
		//  BOOK IN/OUT 실적
		} else if( "YDP3L501".equals(szTcCode)) {
			return JPlateYdMakeTcP2P3.makeYDP3L501(msgRecord, tcRecSet);

			//  BOOK IN/OUT 실적 - 신규
		} else if( "YDP3L501V2".equals(szTcCode)) {
			return JPlateYdMakeTcP2P3.makeYDP3L501V2(msgRecord, tcRecSet);
			
		} else if( "YDP8L501".equals(szTcCode)) {
//---------------------------------------------------------------------------------------------
// 2024.11.21 YDP8L501 전문 처리 부분 추가 
//---------------------------------------------------------------------------------------------
			// ┏━━━━━━━━━━━━━━━━━━━┓
			//     P8 : 1후판 #2 열처리 L2 송신전문
			// ┗━━━━━━━━━━━━━━━━━━━┛
			//  BOOK IN/OUT 실적
			return JPlateYdMakeTcP2P3.makeYDP8L501(msgRecord, tcRecSet);

		} else {
			szMsg = "Unknown TC Code() Error :: "+szTcCode;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return nRtc;
		} // end of if-else

	} // end of makeTc()

  //---------------------------------------------------------------------------
} // end of class
