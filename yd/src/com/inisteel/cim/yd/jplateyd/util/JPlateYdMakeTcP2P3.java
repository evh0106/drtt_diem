/*
 * @(#) S1 (1후판전단L2) 송신 용 전문 생성
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/30
 *
 * @description		P2:전단 / P3: 열처리
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/30   김현우      김현우       최초작성 
 */

package com.inisteel.cim.yd.jplateyd.util;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 * S1 (2후판전단L2) 송신 용 전문 생성
 * @author 김현우
 *
 */
public class JPlateYdMakeTcP2P3 {

	// YDS1L005	BOOK IN/OUT 실적

	// 클래스명
	private static final String SZ_CLASS_NAME  = JPlateYdMakeTcP2P3.class.getName();

	/**
	 * YDP2L501	: BOOK IN/OUT 실적 (사용안함 SMS 송신은 별도 처리 함)
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeYDP2L501(JDTORecord inRec, JDTORecordSet outRecSet) {
		//01	MSG_ID			전문ID			CHAR	8	Y	인터페이스ID		
		//02	DATE			생성일			CHAR	10	Y	YYYY-MM-DD		
		//03	TIME			생성시간			CHAR	8	Y	24HH:MM:SS		
		//04	MSG_GP			전문구분			CHAR	1	Y	I(new), U(modification), D(cancel,deletion)		
		//05	MSG_LEN			전문길이			NUMBER	4	Y			
		//06	TEMP			임시				CHAR	29				
		//07	OPERATION_TYPE	OPERATION_TYPE	Float	1		1:Book In, 2:Book Out		
		//08	PL_L2_TRK_NO	후판L2제품번호		PcsString	16	//필수			
		//09	PL_MTL_NO		후판재료번호		PcsString	32	//필수			
		//10	PL_MEA_GDS_L	후판제촌제품길이	Float	4				
		//11	PL_MEA_GDS_W	후판제촌제품폭		Float	4				
		//12	PL_MEA_GDS_T	후판제촌제품두께	Float	4				
		//13	PL_TRCK_ZONE_ASGN후판트래킹존지정	Long	4		//필수			
		//14	PL_BOOK_OUT_MOD	후판북아웃모드		Long	4		"1 =  Only one plate 2 =  BookIn/out Continuous	3 =  End 2 : 해당bed위 모든 재료 외 1매만		
		//15	CRANE_NO	Crane_No			Long	4		?		
		//16	YARD_NO			Yard_No			CHAR	6		야드 저장위치 MAPPING 관리 함		
		//17	BED_NO			BED_NO			Long	4		?		
		//18	REASON_			CODE			REASON_CODE	Long	4		북아웃 원인코드 :888 - TEST , 999 - 취소처리		
		//18	NEXT_PROCESS	NEXT_PROCESS	Long	4		차행선 (1:SB, 2:열처리재, 3:강력교정기재)		

		// 레코드 선언
		JDTORecord    recPara 		= null;
		JDTORecordSet rsResult 		= null;
		JDTORecord    outRec 		= null;

		// DAO객체 생성
		JPlateYdStkLyrDAO 	ydStkLyrDao	= new JPlateYdStkLyrDAO();
		JPlateYdDaoUtils 	ydDaoUtils  = new JPlateYdDaoUtils();
		JPlateYdUtils 		ydUtils     = new JPlateYdUtils();

		// 변수선언
		String 		szMethodName     	= "makeYDP2L501";
		String 		szOperationName     = "BOOK IN/OUT 실적(사용안함 SMS 송신은 별도 처리 함)";
		String 		szMsg        	    = "";

		String 		szStlNo             = "";
		String 		szOperationType		= "";
		String 		szYdStkColGp		= "";
		String		szL2ZoneNo			= "";
		String		szStlNoList			= "";
		String[]	arrStlNo		= null;

		// 리턴값
		int intRtnVal               = 0;

		// TC Length = 228 (HEADER:60 + BODY:168)
		int nTcLen                  = 167;

		try{
			// Debug MSG
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n=======================makeYDP2L501() IN========================\n", JPlateYdConst.DEBUG);
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG);

			// 레코드 생성
			rsResult        = JDTORecordFactory.getInstance().createRecordSet("");
			recPara         = JDTORecordFactory.getInstance().create();

			// 호출프로그램에서  넘겨 받음
			szStlNo 		= ydDaoUtils.paraRecChkNull(inRec, "STL_NO");						// 재료번호
			szStlNoList 	= ydDaoUtils.paraRecChkNull(inRec, "STL_NO_LIST");					// 재료번호 List
			szOperationType = ydDaoUtils.paraRecChkNull(inRec, "OPERATION_TYPE");				// 1:Book In, 2:Book Out
			szYdStkColGp	= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP");				// FROM위치
			
			String sYdGp = "";
			if(szYdStkColGp.length() > 1){
				sYdGp = szYdStkColGp.substring(0, 1);
			}
			
			if (!"".equals(szStlNo)) {
				szStlNoList = szStlNo;   
			}
			arrStlNo 		= szStlNoList.split(";");

			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n======================= 대상건수 :: "+arrStlNo.length+" 건", 	JPlateYdConst.DEBUG);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n======================= 전송대상 :: "+szStlNoList, 			JPlateYdConst.DEBUG);

			for(int ii=0; ii<arrStlNo.length; ii++) {
				//=======================================================================================================================
				// 적치단 테이블 조회 : 조회조건 재료번호 , BOOK IN 시에만 TO위치를 조회
				//=======================================================================================================================
				szStlNo = arrStlNo[ii];

				if ("1".equals(szOperationType)) {

					recPara.setField("STL_NO", szStlNo);
					recPara.setField("YD_GP" , sYdGp);
					intRtnVal = ydStkLyrDao.getYdStklyrByStlNoYdP(recPara, rsResult);

					if (intRtnVal < 0) {
						szMsg = "적치단 테이블 조회오류 .. 재료번호(" + szStlNo + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
						ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return 0;
					} else if (intRtnVal == 0) {
						szMsg = "적치단 테이블 조회건수 없음 .. 재료번호(" + szStlNo + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
						ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						//return 0;
						szYdStkColGp 	= "TCRTUT";
					}else{
						
						szYdStkColGp    = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_STK_COL_GP"); 	// 야드적치열
					}
				}

				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();
				outRec.setField("MSG_ID" , "YDP2L501");
				outRec.setField("DATE"   , JPlateYdUtils.getCurDate("yyyy-MM-dd"));
				outRec.setField("TIME"   , JPlateYdUtils.getCurDate("HH:mm:ss"));
				outRec.setField("MSG_GP" , "I");
				outRec.setField("MSG_LEN", JPlateYdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0));
				outRec.setField("TEMP"   , JPlateYdUtils.fillSpZr("", 29, 1));

				// 1:Book In, 2:Book Out
				outRec.setField("OPERATION_TYPE", 			JPlateYdUtils.fillSpZr(szOperationType, 1, 1));

				// 후판L2제품번호 (16자리)
				outRec.setField("PL_L2_TRK_NO",				JPlateYdUtils.fillSpZr(szStlNo, 16, 1));

				// 재료번호 (32자리) 
				outRec.setField("PL_MTL_NO",				JPlateYdUtils.fillSpZr(szStlNo, 32, 1));

				outRec.setField("PL_MEA_GDS_L",				"00000");  
				outRec.setField("PL_MEA_GDS_W",				"000000");
				outRec.setField("PL_MEA_GDS_T",				"0000000");

			
				//PL_TRCK_ZONE_ASG
				szL2ZoneNo = JPlateYdCommonUtils.getY2LocToRtZone(ydUtils.substr(szYdStkColGp, 0, 6));
				outRec.setField("PL_TRCK_ZONE_ASGN", 	JPlateYdUtils.fillSpZr(szL2ZoneNo, 5, 1));
				
				outRec.setField("PL_BOOK_OUT_MOD", 			"1");
				outRec.setField("CRANE_NO", 				"  ");
				outRec.setField("YARD_NO", 					"      ");
				outRec.setField("BED_NO", 					"  ");
				outRec.setField("REASON_CODE", 				"   ");
				outRec.setField("NEXT_PROCESS", 			" ");
				outRec.setField("SPARE", 					"                                                                                "); //space 80
				
				// RecordSet에 추가
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n======================makeYDP2L501("+(ii+1)+") OUT ======================\n", JPlateYdConst.DEBUG);
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG);

			}

		} catch(Exception e) {
			szMsg = "P2 (1후판전단L2) 송신  BOOK IN/OUT 실적  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return -1;
		}

		return outRecSet.size();
	} // end of makeYDP2L501()
	
	
	/**
	 * YDP3L501	: BOOK IN/OUT 실적 (N건 전송)
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeYDP3L501(JDTORecord inRec, JDTORecordSet outRecSet) {
		//01	MSG_ID			전문ID			CHAR	8	Y	인터페이스ID		
		//02	DATE			생성일			CHAR	10	Y	YYYY-MM-DD		
		//03	TIME			생성시간			CHAR	8	Y	24HH:MM:SS		
		//04	MSG_GP			전문구분			CHAR	1	Y	I(new), U(modification), D(cancel,deletion)		
		//05	MSG_LEN			전문길이			NUMBER	4	Y			
		//06	TEMP			임시				CHAR	29				
		//07	OPERATION_TYPE	OPERATION_TYPE	Float	1		1:Book In, 2:Book Out		
		//08	PL_L2_TRK_NO	후판L2제품번호		PcsString	16	//필수			
		//09	PL_MTL_NO		후판재료번호		PcsString	32	//필수			
		//10	PL_MEA_GDS_L	후판제촌제품길이	Float	4				
		//11	PL_MEA_GDS_W	후판제촌제품폭		Float	4				
		//12	PL_MEA_GDS_T	후판제촌제품두께	Float	4				
		//13	PL_TRCK_ZONE_ASGN후판트래킹존지정	Long	4		//필수			
		//14	PL_BOOK_OUT_MOD	후판북아웃모드		Long	4		"1 =  Only one plate 2 =  BookIn/out Continuous	3 =  End 2 : 해당bed위 모든 재료 외 1매만		
		//15	CRANE_NO	Crane_No			Long	4		?		
		//16	YARD_NO			Yard_No			CHAR	6		야드 저장위치 MAPPING 관리 함		
		//17	BED_NO			BED_NO			Long	4		?		
		//18	REASON_			CODE			REASON_CODE	Long	4		북아웃 원인코드 :888 - TEST , 999 - 취소처리		
		//18	NEXT_PROCESS	NEXT_PROCESS	Long	4		차행선 (1:SB, 2:열처리재, 3:강력교정기재)		

		// 레코드 선언
		JDTORecord    recPara 		= null;
		JDTORecordSet rsResult 		= null;
		JDTORecord    outRec 		= null;

		// DAO객체 생성
		JPlateYdStkLyrDAO 	ydStkLyrDao	= new JPlateYdStkLyrDAO();
		JPlateYdDaoUtils 	ydDaoUtils  = new JPlateYdDaoUtils();
		JPlateYdUtils 		ydUtils     = new JPlateYdUtils();

		// 변수선언
		String 		szMethodName     	= "makeYDP3L501";
		String 		szOperationName     = "BOOK IN/OUT 실적";
		String 		szMsg        	    = "";

		String 		szStlNo             = "";
		String      szEqpId             = "";
		String 		szOperationType		= "";
		String 		szYdStkColGp		= "";
		String		szL2ZoneNo			= "";
		String		szStlNoList			= "";
		String		szCRANE_NO		= "";
		
		String[]	arrStlNo		= null;

		// 리턴값
		int intRtnVal               = 0;

		// TC Length = 93 (HEADER:60 + BODY:35)
		int nTcLen                  = 167;

		try{
			// Debug MSG
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n=======================makeYDP3L501() IN========================\n", JPlateYdConst.DEBUG);
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG);

			// 레코드 생성
			rsResult        = JDTORecordFactory.getInstance().createRecordSet("");
			recPara         = JDTORecordFactory.getInstance().create();

			// 호출프로그램에서  넘겨 받음
			szStlNo 		= ydDaoUtils.paraRecChkNull(inRec, "STL_NO");						// 재료번호
			szStlNoList 	= ydDaoUtils.paraRecChkNull(inRec, "STL_NO_LIST");					// 재료번호 List
			szOperationType = ydDaoUtils.paraRecChkNull(inRec, "OPERATION_TYPE");				// 1:Book In, 2:Book Out
			szYdStkColGp	= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP");				// FROM위치
			szEqpId			= ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");				    // FROM위치
			
			String sYdGp = "";
			if(szYdStkColGp.length() > 1){
				sYdGp = szYdStkColGp.substring(0, 1);
			}
			
			if (!"".equals(szStlNo)) {
				szStlNoList = szStlNo;   
			}
			arrStlNo 		= szStlNoList.split(";");

			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n======================= 대상건수 :: "+arrStlNo.length+" 건", 	JPlateYdConst.DEBUG);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n======================= 전송대상 :: "+szStlNoList, 			JPlateYdConst.DEBUG);

			for(int ii=0; ii<arrStlNo.length; ii++) {
				//=======================================================================================================================
				// 적치단 테이블 조회 : 조회조건 재료번호 , BOOK IN 시에만 TO위치를 조회
				//=======================================================================================================================
				szStlNo = arrStlNo[ii];

				if ("1".equals(szOperationType)) {

					recPara.setField("STL_NO", szStlNo);
					recPara.setField("YD_GP" , sYdGp);
					intRtnVal = ydStkLyrDao.getYdStklyrByStlNoYdP(recPara, rsResult);

					if (intRtnVal < 0) {
						szMsg = "적치단 테이블 조회오류 .. 재료번호(" + szStlNo + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
						ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return 0;
					} else if (intRtnVal == 0) {
						szMsg = "적치단 테이블 조회건수 없음 .. 재료번호(" + szStlNo + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
						ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						//return 0;
						szYdStkColGp 	= "TCRTUT";
					}else{
						
						szYdStkColGp    = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_STK_COL_GP"); 	// 야드적치열
					}
				}

				
				// 헤더부
//				recPara = JDTORecordFactory.getInstance().create();
//				recPara.setField("OPERATION_TYPE", 			JPlateYdUtils.fillSpZr(szOperationType, 1, 1));
//				recPara.setField("PL_L2_TRK_NO",			JPlateYdUtils.fillSpZr(szStlNo, 16, 1));
//				recPara.setField("PL_MTL_NO",				JPlateYdUtils.fillSpZr(szStlNo, 32, 1));
//				szL2ZoneNo = JPlateYdCommonUtils.getY2LocToRtZone(ydUtils.substr(szYdStkColGp, 0, 6));
//				recPara.setField("PL_TRCK_ZONE_ASGN", 		JPlateYdUtils.fillSpZr(szL2ZoneNo, 5, 1));
//				
//				intRtnVal = ydStkLyrDao.getYDP3L501(recPara, rsResult);
				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();
				outRec.setField("MSG_ID" 		, "YDP3L501");
				outRec.setField("DATE"   		, JPlateYdUtils.getCurDate("yyyy-MM-dd"));
				outRec.setField("TIME"   		, JPlateYdUtils.getCurDate("HH:mm:ss"));
				outRec.setField("MSG_GP" 		, "I");
				outRec.setField("MSG_LEN"		, JPlateYdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0));
				outRec.setField("TEMP"   		, JPlateYdUtils.fillSpZr("", 29, 1));

				// 1:Book In, 2:Book Out
				outRec.setField("OPERATION_TYPE", 			JPlateYdUtils.fillSpZr(szOperationType, 1, 1));

				// 후판L2제품번호 (16자리)
				outRec.setField("PL_L2_TRK_NO",				JPlateYdUtils.fillSpZr(szStlNo, 16, 1));

				// 재료번호 (32자리)
				outRec.setField("PL_MTL_NO",				JPlateYdUtils.fillSpZr(szStlNo, 32, 1));

				
				outRec.setField("PL_MEA_GDS_L",				"00000");
				outRec.setField("PL_MEA_GDS_W",				"000000");
				outRec.setField("PL_MEA_GDS_T",				"0000000");
			
				//PL_TRCK_ZONE_ASG
				szL2ZoneNo = JPlateYdCommonUtils.getY2LocToRtZone(ydUtils.substr(szYdStkColGp, 0, 6));
				outRec.setField("PL_TRCK_ZONE_ASGN", 	JPlateYdUtils.fillSpZr(szL2ZoneNo, 5, 1));
				
				if(szEqpId != null) {
					if(szEqpId.substring(4,6).equals("B1")) {
						szCRANE_NO = "44";
					} else if(szEqpId.substring(4,6).equals("B2")) {
						szCRANE_NO = "20";
					} else {
						szCRANE_NO = "44";
					}
					
				}
				
				outRec.setField("PL_BOOK_OUT_MOD", 			"1");
				outRec.setField("CRANE_NO", 				szCRANE_NO);
				outRec.setField("YARD_NO", 					ydUtils.substr(szYdStkColGp, 0, 6));
				outRec.setField("BED_NO", 					"01");
				outRec.setField("REASON_CODE", 				"   ");
				outRec.setField("NEXT_PROCESS", 			" ");
				outRec.setField("SPARE", 					"                                                                                "); //space 80
				
				// RecordSet에 추가
				outRecSet.addRecord(outRec);

				
				// RecordSet에 추가
	//			outRecSet.addRecord(rsResult.getRecord(0));

				// Debug MSG
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n======================makeYDP3L501("+(ii+1)+") OUT ======================\n", JPlateYdConst.DEBUG);
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG);

			}

		} catch(Exception e) {
			szMsg = "P3 (후판열처리L2) 송신  BOOK IN/OUT 실적  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return -1;
		}

		return outRecSet.size();
	} // end of makeYDP3L501()

	/**
	 * YDP3L501	: BOOK IN/OUT 실적 (N건 전송) - 신규
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeYDP3L501V2(JDTORecord inRec, JDTORecordSet outRecSet) {
		//01	MSG_ID				전문ID				CHAR	8	Y	인터페이스ID		
		//02	DATE				생성일				CHAR	10	Y	YYYY-MM-DD		
		//03	TIME				생성시간			CHAR	8	Y	24HH:MM:SS		
		//04	MSG_GP				전문구분			CHAR	1	Y	I(new), U(modification), D(cancel,deletion)		
		//05	MSG_LEN				전문길이			NUMBER	4	Y	167		
		//06	TEMP				임시				CHAR	29				
		//07	OPERATION_TYPE		OPERATION_TYPE		CHAR	1		1:Book In, 2:Book Out		
		//08	PL_L2_TRK_NO		후판L2제품번호		CHAR	16	Y	//필수			
		//09	PL_MTL_NO			후판재료번호		CHAR	32	Y	//필수			
		//10	PL_MEA_GDS_L		후판제촌제품길이	CHAR	5				
		//11	PL_MEA_GDS_W		후판제촌제품폭		CHAR	6				
		//12	PL_MEA_GDS_T		후판제촌제품두께	CHAR	7				
		//13	PL_TRCK_ZONE_ASGN	후판트래킹존지정	CHAR	5	Y	//필수			
		//14	PL_BOOK_OUT_MOD		후판북아웃모드		CHAR	1		"1 =  Only one plate 2 =  BookIn/out Continuous	3 =  End 2 : 해당bed위 모든 재료 외 1매만		
		//15	CRANE_NO			Crane_No			CHAR	2		?		
		//16	YARD_NO				Yard_No				CHAR	6		야드 저장위치 MAPPING 관리 함		
		//17	BED_NO				BED_NO				CHAR	2		?		
		//18	REASON_CODE			REASON_CODE			CHAR	3		북아웃 원인코드 :888 - TEST , 999 - 취소처리		
		//19	NEXT_PROCESS		NEXT_PROCESS		CHAR	1		차행선 (1:SB, 2:열처리재, 3:강력교정기재)		
		//20	PILNG_WRK_GP		파일링작업구분		CHAR	1		Y:파일링작업, N:일반작업	
		//21	PL_MTL_NO2			후판재료번호2		CHAR	10		2단 재료번호
		//22	PL_MTL_NO3			후판재료번호3		CHAR	10		3단 재료번호
		//23	SPARE				공백란				CHAR	59		

		// 레코드 선언
		JDTORecord    recPara 		= null;
		JDTORecordSet rsResult 		= null;
		JDTORecord    outRec 		= null;

		// DAO객체 생성
		JPlateYdStkLyrDAO 	ydStkLyrDao	= new JPlateYdStkLyrDAO();
		JPlateYdDaoUtils 	ydDaoUtils  = new JPlateYdDaoUtils();
		JPlateYdUtils 		ydUtils     = new JPlateYdUtils();
		
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
	    YdUtils 			ydLogUtils  = new YdUtils();

	    
		// 변수선언
		String 		szMethodName     	= "makeYDP3L501V2";
		String 		szOperationName     = "BOOK IN/OUT 실적 - 신규";
		String 		szMsg        	    = "";

		String 		szStlNo             = "";
		String      szStlNo2			= "";
		String      szStlNo3			= "";
		String      szEqpId             = "";
		String 		szOperationType		= "";
		String 		szYdStkColGp		= "";
		String		szL2ZoneNo			= "";
		String		szStlNoList			= "";
		String		szCRANE_NO			= "";
		String      szPilngWrkGp		= "N";
		
		String[]	arrStlNo		= null;
		
		String      szCARD_NO       = null;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		// 리턴값
		int intRtnVal               = 0;

		// TC Length = 167 (HEADER:60 + BODY:167)
		int nTcLen                  = 167;

		try{
			// Debug MSG
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n=======================makeYDP3L501V2() IN========================\n", JPlateYdConst.DEBUG, logId);
			//ydUtils.displayRecord(szOperationName, inRec);
        	szMsg = "전송 데이터 확인 >>>> " + inRec.toString();
            ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
            
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG, logId);

			// 레코드 생성
			rsResult        = JDTORecordFactory.getInstance().createRecordSet("");
			recPara         = JDTORecordFactory.getInstance().create();

			// 호출프로그램에서  넘겨 받음
			szStlNo 		= ydDaoUtils.paraRecChkNull(inRec, "STL_NO"			);	// 재료번호
			szStlNoList 	= ydDaoUtils.paraRecChkNull(inRec, "STL_NO_LIST"	);	// 재료번호 List
			szOperationType = ydDaoUtils.paraRecChkNull(inRec, "OPERATION_TYPE"	);	// 1:Book In, 2:Book Out
			szYdStkColGp	= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP"	);	// FROM위치
			szEqpId			= ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID"		);	// 야드설비ID
			szCARD_NO		= ydDaoUtils.paraRecChkNull(inRec, "CARD_NO"		);	// L3 화면에서 만들어진 지시여부
			
			String sYdGp = "";
			if(szYdStkColGp.length() > 1){
				sYdGp = szYdStkColGp.substring(0, 1);
			}
			
			if (!"".equals(szStlNo)) {
				szStlNoList = szStlNo;   
			}
			arrStlNo 		= szStlNoList.split(";");

			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n======================= 대상건수 :: " + arrStlNo.length + " 건", 	JPlateYdConst.DEBUG, logId);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n======================= 전송대상 :: " + szStlNoList, 			JPlateYdConst.DEBUG, logId);

			if(arrStlNo.length > 0) { //for(int ii=0; ii<arrStlNo.length; ii++) {
				
				//=======================================================================================================================
				// 적치단 테이블 조회 : 조회조건 재료번호 , BOOK IN 시에만 TO위치를 조회
				//=======================================================================================================================
				szStlNo = arrStlNo[0];		// 1단 재료번호
				szPilngWrkGp = "N";
				if(arrStlNo.length == 2) {
					szStlNo2 = arrStlNo[1];	// 2단 재료번호
					szPilngWrkGp = "Y";
				} else if(arrStlNo.length == 3) {
					szStlNo2 = arrStlNo[1];	// 2단 재료번호
					szStlNo3 = arrStlNo[2];	// 3단 재료번호
					szPilngWrkGp = "Y";
				}

				if ("1".equals(szOperationType)) { 
					// Book-In 인 경우 TO위치 RT Zone 위치를 파악하기 위해 
					// 권하 한 뒤 재료번호가 위치한 저장위치를 검색한다. 

					recPara.setField("STL_NO", szStlNo	);
					recPara.setField("YD_GP" , sYdGp	);
        	        
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recPara에 logId 추가 
					recPara.setField("LOG_ID", 			logId 									);      // logId
//-------------------------------------------------------------------------------------------------------------------------
        	          	        
					intRtnVal = ydStkLyrDao.getYdStklyrByStlNoYdP(recPara, rsResult);

					if (intRtnVal < 0) {
						szMsg = "적치단 테이블 조회오류 .. 재료번호(" + szStlNo + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
						ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return 0;
					} else if (intRtnVal == 0) {
						szMsg = "적치단 테이블 조회건수 없음 .. 재료번호(" + szStlNo + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
						ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						//return 0;
						szYdStkColGp 	= "TCRTUT";
					}else{
						
						szYdStkColGp    = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_STK_COL_GP"); 	// 야드적치열
					}
				}
				
				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();
				outRec.setField("MSG_ID" 		, "YDP3L501"												);
				outRec.setField("DATE"   		, JPlateYdUtils.getCurDate("yyyy-MM-dd")					);
				outRec.setField("TIME"   		, JPlateYdUtils.getCurDate("HH:mm:ss")						);
				outRec.setField("MSG_GP" 		, "I"														);
				outRec.setField("MSG_LEN"		, JPlateYdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0)	);
				outRec.setField("TEMP"   		, JPlateYdUtils.fillSpZr("", 29, 1)							);

				
				// 1:Book In, 2:Book Out
				outRec.setField("OPERATION_TYPE", 			JPlateYdUtils.fillSpZr(szOperationType, 1, 1)	);

				
				// 후판L2제품번호 (16자리)
				outRec.setField("PL_L2_TRK_NO",				JPlateYdUtils.fillSpZr(szStlNo, 16, 1)			);

				
				// 재료번호 (32자리)
				outRec.setField("PL_MTL_NO",				JPlateYdUtils.fillSpZr(szStlNo, 32, 1)			);

				
				
				outRec.setField("PL_MEA_GDS_L",				"00000"											);
				outRec.setField("PL_MEA_GDS_W",				"000000"										);
				outRec.setField("PL_MEA_GDS_T",				"0000000"										);
			
				
				//PL_TRCK_ZONE_ASG
				szL2ZoneNo = JPlateYdCommonUtils.getY2LocToRtZone(ydUtils.substr(szYdStkColGp, 0, 6));
				outRec.setField("PL_TRCK_ZONE_ASGN", 	JPlateYdUtils.fillSpZr(szL2ZoneNo, 5, 1)			);
				
				
				if(szEqpId != null) {
					szCRANE_NO = JPlateYdCommonUtils.getY2CraneNoL2(szEqpId);
				}
				
				
				if("L3".equals(szCARD_NO)) {
					outRec.setField("PL_BOOK_OUT_MOD", 			"4"											);
				} else {
					outRec.setField("PL_BOOK_OUT_MOD", 			"1"											);
				}
				
				outRec.setField("CRANE_NO", 				JPlateYdUtils.fillSpZr(szCRANE_NO, 2, 1)		);
				outRec.setField("YARD_NO", 					ydUtils.substr(szYdStkColGp, 0, 6)				);
				outRec.setField("BED_NO", 					"01"											);
				outRec.setField("REASON_CODE", 				JPlateYdUtils.fillSpZr("", 3, 1)				);
				outRec.setField("NEXT_PROCESS", 			JPlateYdUtils.fillSpZr("", 1, 1)				);
				outRec.setField("PILNG_WRK_GP",				JPlateYdUtils.fillSpZr(szPilngWrkGp, 1, 1)		);
				outRec.setField("PL_MTL_NO2",				JPlateYdUtils.fillSpZr(szStlNo2, 10, 1)			);
				outRec.setField("PL_MTL_NO3",				JPlateYdUtils.fillSpZr(szStlNo3, 10, 1)			);
				outRec.setField("SPARE", 					JPlateYdUtils.fillSpZr("", 59, 1)				); // space 59
				
				
				// RecordSet에 추가
				outRecSet.addRecord(outRec);

				
				// Debug MSG
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n======================makeYDP3L501V2 OUT ======================\n", JPlateYdConst.DEBUG, logId);
				//ydUtils.displayRecord(szOperationName, outRec);
	        	szMsg = "전송 데이터 확인 >>>> " + outRec.toString();
	            ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG, logId);

			}

		} catch(Exception e) {
			szMsg = "P3 (후판열처리L2) 송신  BOOK IN/OUT 실적  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			return -1;
		}

		return outRecSet.size();
	} // end of makeYDP3L501V2()
	

	/**
	 * YDP8L501	: 2024.11.21 
	 *            1후판 정정 #2 열처리 BOOK IN/OUT 실적
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeYDP8L501(JDTORecord inRec, JDTORecordSet outRecSet) {
		//01	MSG_ID				전문ID				CHAR	 8		인터페이스ID		
		//02	DATE				생성일				CHAR	10		YYYY-MM-DD		
		//03	TIME				생성시간			CHAR	 8		24HH:MM:SS		
		//04	MSG_GP				전문구분			CHAR	 1		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//05	MSG_LEN				전문길이			CHAR	 4		길이 : 167
		//06	MSG_SP				Spare				CHAR	29				
		//07	OPERATION_MODE		OPERATION_TYPE		CHAR	 1		1:Book-In, 2:Book-Out
		//08	PIECE_ID			후판L2제품번호		CHAR	16		
		//09	PPS_PIECE_ID		후판재료번호		CHAR	32		
		//10	PIECE_LENGTH		후판제촌제품길이	CHAR	 5				
		//11	PIECE_WIDTH			후판제촌제품폭		CHAR	 6				
		//12	PIECE_THICKNESS		후판제촌제품두께	CHAR	 7				
		//13	TRACK_ZONE_DESIG 	후판트래킹존지정	CHAR	 5		0013N, 0013 : Zone No, N : Book In Type
		//14	BOOKIN_CONTI_MODE	후판북아웃모드		CHAR	 1		1:Only One Plae, 2:Continuous, 3:End
		//15	CRANE_NO			Crane_No			CHAR	 2		
		//16	YARD_NO				Yard_No				CHAR	 6		
		//17	BED_NO				BED_NO				CHAR	 2		
		//18	REASON_CODE			REASON_CODE			CHAR	 3		
		//19	NEXT_PROCESS		NEXT_PROCESS		CHAR	 1		1:S/B, 2:HTF, 3:SCPL(차행선 (1:SB, 2:열처리재, 3:강력교정기재))		
		//20	PILNG_WRK_GP		파일링작업구분		CHAR	 1		Y:Piling, N:One Plate
		//21	PL_MTL_NO2			후판재료번호2		CHAR	10
		//22	PL_MTL_NO3			후판재료번호3		CHAR	10
		//23	CRANE_SEND_FLAG		SPARE				CHAR	59

		// 레코드 선언
		JDTORecord    recPara 			= null;
		JDTORecordSet rsResult 			= null;
		JDTORecord    outRec 			= null;

		// DAO객체 생성
		JPlateYdStkLyrDAO 	ydStkLyrDao	= new JPlateYdStkLyrDAO();
		JPlateYdDaoUtils 	ydDaoUtils  = new JPlateYdDaoUtils();
		JPlateYdUtils 		ydUtils     = new JPlateYdUtils();
		
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
	    YdUtils 			ydLogUtils  = new YdUtils();

	    
		// 변수선언
		String 		szMethodName     	= "makeYDP8L501";
		String 		szOperationName     = "#2 열처리 BOOK IN/OUT 실적";
		String 		szMsg        	    = "";

		String 		szStlNo             = "";
		String      szStlNo2			= "";
		String      szStlNo3			= "";
		String      szEqpId             = "";
		String 		szOperationType		= "";
		String 		szYdStkColGp		= "";
		String		szL2ZoneNo			= "";
		String		szStlNoList			= "";
		String		szCRANE_NO			= "";
		String      szPilngWrkGp		= "N";
		
		String[]	arrStlNo			= null;
		
		String      szCARD_NO       	= null;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본

		szMsg = "[" + szOperationName + "] ---- inRec.toString()  \n>>>> " + inRec.toString();
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
//-------------------------------------------------------------------------------------------------------------------------

		// 리턴값
		int intRtnVal               	= 0;

		// TC Length = 167 (HEADER:60 + BODY:167)
		int nTcLen                  	= 167;

		try{
			// Debug MSG
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n=======================makeYDP8L501() IN========================\n", JPlateYdConst.DEBUG, logId);
			//ydUtils.displayRecord(szOperationName, inRec);
        	szMsg = "전송 데이터 확인 >>>> " + inRec.toString();
            ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
            
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG, logId);

			// 레코드 생성
			rsResult        = JDTORecordFactory.getInstance().createRecordSet("");
			recPara         = JDTORecordFactory.getInstance().create();

			// 호출프로그램에서  넘겨 받음
			szStlNo 		= ydDaoUtils.paraRecChkNull(inRec, "STL_NO");						// 재료번호
			szStlNoList 	= ydDaoUtils.paraRecChkNull(inRec, "STL_NO_LIST");					// 재료번호 List
			szOperationType = ydDaoUtils.paraRecChkNull(inRec, "OPERATION_TYPE");				// 1:Book In, 2:Book Out
			szYdStkColGp	= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP");				// FROM위치
			szEqpId			= ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");				    // FROM위치
			szCARD_NO		= ydDaoUtils.paraRecChkNull(inRec, "CARD_NO");				    	// L3 화면에서 만들어진 지시여부
			
			String sYdGp = "";
			if(szYdStkColGp.length() > 1){
				sYdGp = szYdStkColGp.substring(0, 1);
			}
			
			if (!"".equals(szStlNo)) {
				szStlNoList = szStlNo;   
			}
			arrStlNo 		= szStlNoList.split(";");

			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n======================= 대상건수 :: " + arrStlNo.length + " 건", 	JPlateYdConst.DEBUG, logId);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n======================= 전송대상 :: " + szStlNoList, 				JPlateYdConst.DEBUG, logId);

			if(arrStlNo.length > 0) { 
				
				//=======================================================================================================================
				// 적치단 테이블 조회 : 조회조건 재료번호 , BOOK IN 시에만 TO위치를 조회
				//=======================================================================================================================
				szStlNo = arrStlNo[0];		// 1단 재료번호
				szPilngWrkGp = "N";
				if(arrStlNo.length == 2) {
					szStlNo2 		= arrStlNo[1];	// 2단 재료번호
					szPilngWrkGp 	= "Y";
				} else if(arrStlNo.length == 3) {
					szStlNo2 		= arrStlNo[1];	// 2단 재료번호
					szStlNo3 		= arrStlNo[2];	// 3단 재료번호
					szPilngWrkGp 	= "Y";
				}

				if ("1".equals(szOperationType)) { 
					// Book-In 인 경우 TO위치 RT Zone 위치를 파악하기 위해 
					// 권하 한 뒤 재료번호가 위치한 저장위치를 검색한다. 

					recPara.setField("STL_NO", szStlNo	);
					recPara.setField("YD_GP" , sYdGp	);
        	        
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recPara에 logId 추가 
					recPara.setField("LOG_ID", 			logId 									);      // logId
//-------------------------------------------------------------------------------------------------------------------------
        	          	        
					intRtnVal = ydStkLyrDao.getYdStklyrByStlNoYdP(recPara, rsResult);

					if (intRtnVal < 0) {
						szMsg = "적치단 테이블 조회오류 .. 재료번호(" + szStlNo + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
						ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return 0;
					} else if (intRtnVal == 0) {
						szMsg = "적치단 테이블 조회건수 없음 .. 재료번호(" + szStlNo + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
						ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
//						return 0;
// 2024.11.22 저장위치 -> RT 존 찾기 위해 막음						
//						szYdStkColGp 	= "TCRTUT";
					}else{
						
						szYdStkColGp    = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_STK_COL_GP"); 	// 야드적치열
					}
					
					
				}
				
				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();
				outRec.setField("MSG_ID" 		, "YDP8L501"												);
				outRec.setField("DATE"   		, JPlateYdUtils.getCurDate("yyyy-MM-dd")					);
				outRec.setField("TIME"   		, JPlateYdUtils.getCurDate("HH:mm:ss")						);
				outRec.setField("MSG_GP" 		, "I"														);
				outRec.setField("MSG_LEN"		, JPlateYdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0)	);
				outRec.setField("MSG_SP"   		, JPlateYdUtils.fillSpZr("", 29, 1));

				
				// 1:Book In, 2:Book Out
				outRec.setField("OPERATION_MODE", 			JPlateYdUtils.fillSpZr(szOperationType, 1, 1)	);

				
				// 후판L2제품번호 (16자리)
				outRec.setField("PIECE_ID",					JPlateYdUtils.fillSpZr(szStlNo, 16, 1)			);

				
				// 재료번호 (32자리)
				outRec.setField("PPS_PIECE_ID",				JPlateYdUtils.fillSpZr(szStlNo, 32, 1)			);

				
				
				outRec.setField("PIECE_LENGTH",				"00000"											);
				outRec.setField("PIECE_WIDTH",				"000000"										);
				outRec.setField("PIECE_THICKNESS",			"0000000"										);
			
				
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 기존 Hashtable 사용 부분을 TB_YD_RULE 사용으로 변경
//-------------------------------------------------------------------------------------------------------------------------
				// TRACK_ZONE_DESIG
				szL2ZoneNo = JPlateYdCommonUtils.selLocToY2RtZone(ydUtils.substr(szYdStkColGp, 0, 6));  // PFRT60 -> 59020
				outRec.setField("TRACK_ZONE_DESIG", 	JPlateYdUtils.fillSpZr(szL2ZoneNo, 5, 1)			);
				
				
				if(szEqpId != null) {
					szCRANE_NO = JPlateYdCommonUtils.getY2CraneNoL2(szEqpId);
				}
				
// 2024.11.21 szCARD_NO 어떤 정보 인지 확인 필요				
				if("L3".equals(szCARD_NO)) {
					outRec.setField("BOOKIN_CONTI_MODE", 			"4"										);
				} else {
					outRec.setField("BOOKIN_CONTI_MODE", 			"1"										);
				}
				
				outRec.setField("CRANE_NO", 				JPlateYdUtils.fillSpZr(szCRANE_NO, 2, 1)		);
				outRec.setField("YARD_NO", 					ydUtils.substr(szYdStkColGp, 0, 6)				);
				outRec.setField("BED_NO", 					"01"											);
				outRec.setField("REASON_CODE", 				JPlateYdUtils.fillSpZr("", 3, 1)				);
				outRec.setField("NEXT_PROCESS", 			JPlateYdUtils.fillSpZr("", 1, 1)				);
				outRec.setField("PILNG_WRK_GP",				JPlateYdUtils.fillSpZr(szPilngWrkGp, 1, 1)		);
				outRec.setField("PL_MTL_NO2",				JPlateYdUtils.fillSpZr(szStlNo2, 10, 1)			);
				outRec.setField("PL_MTL_NO3",				JPlateYdUtils.fillSpZr(szStlNo3, 10, 1)			);
				outRec.setField("CRANE_SEND_FLAG", 			JPlateYdUtils.fillSpZr("", 59, 1)				); //space 59
				
				// RecordSet에 추가
				outRecSet.addRecord(outRec);

				
				// Debug MSG
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n======================makeYDP8L501 OUT ======================\n", JPlateYdConst.DEBUG, logId);
				//ydUtils.displayRecord(szOperationName, outRec);
	        	szMsg = "전송 데이터 확인 >>>> " + outRec.toString();
	            ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG, logId);

			}

		} catch(Exception e) {
			szMsg = "P8 (후판 #2 열처리L2) 송신  BOOK IN/OUT 실적  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			return -1;
		}

		return outRecSet.size();
	} // end of makeYDP8L501()
	
} // 
