/*
 * @(#) PP (후판조업L3) 송신 용 전문 생성
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/30
 *
 * @description		(후판조업L3) 송신 용 전문 생성
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
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

/**
 * PP (후판조업L3) 송신 용 전문 생성
 * @author 김현우
 *
 */
public class JPlateYdMakeTcPP {

	// YDPPJ011	저장위치변경정보
	// 2013.09.16 FROM 위치 NULL일때 NULL로 전송

	// 클래스명
	private static final String SZ_CLASS_NAME  = JPlateYdMakeTcPP.class.getName();

	/**
	 * YDPPJ011	: 저장위치변경정보 (크레인권하처리, 저장위치변경처리)
	 * 호출파라미터
	 * 			- YD_STK_COL_FR : From적치열
	 * 			- YD_STK_BED_FR : From적치BED
	 * 			- YD_STK_COL_TO : TO적치열
	 * 			- YD_STK_BED_TO : TO적치BED
	 * 			- YD_EQP_WRK_SH : 야드설비작업매수
	 * 			- ARR_STL_NO	: 재료번호 Array
	 *
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeYDPPJ011(JDTORecord inRec, JDTORecordSet outRecSet) {

		// 		1.  JMS_TC_CD				JMSTC코드				CHAR(8)			YDPPJ011
		// 		2.  JMS_TC_CREATE_DDTT		JMSTC생성일시				DATE(14)
		// 		3.	YD_GP					야드구분					CHAR(1)			F:2후판정정야드
		// 		4.	FROM_STORE_LOC			From저장위치				CHAR(8)			야드적치열구분(6자리) + 야드적치Bed번호(2자리)
		// 		5.	TO_STORE_LOC			To저장위치				CHAR(8)			야드적치열구분(6자리) + 야드적치Bed번호(2자리)
		// 		6.	YD_EQP_WRK_SH			야드설비작업매수			NUMBER(2)		크레인 1 Handring 매수
		// 		7.	STL_NO1					재료번호1					CHAR(11)		후판Plate번호 또는 후판날판번호
		// 		8.	BOOK_OUT_RESN1			Book-Out원인1			CHAR(3)			Book-Out원인
		// 		9.	PL_BOOK_INOUT_GP1		후판북인아웃구분1			CHAR(1)			1:Book In, 2:Book Out
		// 		10.	YD_STK_LYR_NO1			야드적치단1				CHAR(3)			적치단

		// 레코드 선언
		JDTORecord    recPara 		= null;
		JDTORecordSet rsResult 		= null;
		JDTORecord    outRec 		= null;
		JDTORecord    tempRec 		= null;

		// DAO객체 생성
		JPlateYdStkLyrDAO 	ydStkLyrDao	= new JPlateYdStkLyrDAO();
		JPlateYdDaoUtils 	ydDaoUtils  = new JPlateYdDaoUtils();
		JPlateYdUtils 		ydUtils     = new JPlateYdUtils();

		// 변수선언
		String 	szMethodName     	= "makeYDPPJ011";
		String 	szOperationName     = "저장위치변경정보";
		String 	szMsg        	    = "";

		String 	szYdStkColFr		= "";		// From적치열
		String 	szYdStkBedFr		= "";		// From적치베드
		String 	szYdStkColTo		= "";		// To적치열
		String 	szYdStkBedTo		= "";		// To적치베드
		String 	szYdEqpWrkSh		= "";		// 야드설비작업매수
		String 	szFromStoreLoc		= "";		// From저장위치 : 적치열+베드
		String 	szToStoreLoc		= "";		// To저장위치   : 적치열+베드
		String 	szPlBookInoutGp		= "";		// BOOK-OUT FLAG : 1-(TO저장위치가 RT일때), 2-FROM저장위치가 RT일때, '':RT가 아닐때
		String	szArrStlNo			= "";
		String	szYdStkLyrNo		= "";
		String	szBookOutResn		= "";		// BOOK-OUT 원인코드
		String  szPL_TRCK_ZONE_ASG  = "";       //북아웃요청(S1YDL013)시 수신받은 트랙킹 정보(Operation_Source)
		String  szPL_BOOK_OUT_MOD  = "";        //북아웃요청(S1YDL013)시 수신받은 북아웃 모드(Operation_Mode)
		String  szBookOutCrn        = "";       // 작업크레인
		String  szIS_PF_TO_PF       = "";
		
		YdPICommDAO   ydPICommDAO   = new YdPICommDAO();
		

		// 리턴값
		int intRtnVal               = 0;

		try {

			// Debug MSG
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n=======================makeYDPPJ011() IN========================\n", JPlateYdConst.DEBUG);
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG);

			// 레코드 생성
			rsResult        = JDTORecordFactory.getInstance().createRecordSet("yd");
			recPara         = JDTORecordFactory.getInstance().create();

			// 호출프로그램에서  넘겨 받음
			szYdStkColFr	= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_FR");		// From적치열
			szYdStkBedFr	= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_FR");		// From적치BED
			szYdStkColTo	= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_TO");		// TO적치열
			szYdStkBedTo	= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_TO");		// TO적치BED
			szYdEqpWrkSh	= ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_WRK_SH", "20");	// 야드설비작업매수
			szArrStlNo		= ydDaoUtils.paraRecChkNull(inRec, "ARR_STL_NO");			// 재료번호 Array
			szBookOutCrn    = ydDaoUtils.paraRecChkNull(inRec, "BOOK_OUT_CRN");			// 작업크레인
			
			szIS_PF_TO_PF       = ydDaoUtils.paraRecChkNull(inRec, "IS_PF_TO_PF");			// 공장간 이송 여부
			 
			szMsg = "YDPPJ011수신 내 설비 수신 확인:" + szBookOutCrn;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			
			szMsg = "YDPPJ011수신 szIS_PF_TO_PF 확인:" + szIS_PF_TO_PF;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//REQ202306466285 여기 권하실적시(Y7YDL009) 크레인정보 추가

			// 파라미터로 재료번호 Array가 넘어 왔을때 재료조회 안함
			if (!"".equals(szArrStlNo)) {

				if (!"".equals(szYdStkColFr)) {
					szFromStoreLoc  = szYdStkColFr + ("".equals(szYdStkBedFr)?"01":szYdStkBedFr);
				}
				if (!"".equals(szYdStkColTo)) {
					szToStoreLoc    = szYdStkColTo + szYdStkBedTo;
				}

				// 2013.07.22 보완 :: FROM위치가 NULL일때 보완
				// 2013.09.16 보완 :: FROM위치가 NULL일때 NULL로 전송 (조업요청)
				//if ("".equals(szFromStoreLoc)) {
				//	szFromStoreLoc = ydUtils.substr(szToStoreLoc, 0, 2) + "010101";
				//}

				//=======================================================================================================================
				// 재료 건수만큼 반복
				//=======================================================================================================================
				String[] arrStlNo = szArrStlNo.split(";");

				if (arrStlNo == null || arrStlNo.length < 1) {
					szMsg = "S1 (후판조업L3) 송신  저장위치변경정보  데이터중 재료번호 오류 : " + szArrStlNo;
					ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return -1;
				}

				// Interface Layout에 20건임으로 Max 20건만 전송
				if (arrStlNo.length <= 20) {
					szYdEqpWrkSh = Integer.toString(arrStlNo.length);
				}

				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();
				outRec.setField("JMS_TC_CD", 			"YDPPJ011");
				outRec.setField("JMS_TC_CREATE_DDTT", 	JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));
				outRec.setField("YD_GP", 				JPlateYdConst.YD_GP_F_PLATE_YARD);	// 2후판정정야드
				outRec.setField("FROM_STORE_LOC", 		szFromStoreLoc);					// From저장위치 : 적치열+베드
				outRec.setField("TO_STORE_LOC", 		szToStoreLoc);						// To저장위치 : 적치열+베드
				outRec.setField("YD_EQP_WRK_SH", 		szYdEqpWrkSh);						// 야드설비작업매수

				// To위치가 RT일때 - Book-In으로 Set
				if ("RT".equals(ydUtils.substr(szToStoreLoc,2,2))) {
					szPlBookInoutGp = JPlateYdConst.PP_BOOK_IN_GP;		// '1' : BOOK-IN
				}

				// From위치가 RT일때 - Book-Out으로 Set
				if ("RT".equals(ydUtils.substr(szFromStoreLoc,2,2))) {
					szPlBookInoutGp = JPlateYdConst.PP_BOOK_OUT_GP;		// '2' : BOOK-OUT
				}

				for(int ii=0; ii<arrStlNo.length; ii++) {

					if (ii < 20) {
						rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
						recPara  = JDTORecordFactory.getInstance().create();
						tempRec  = JDTORecordFactory.getInstance().create();
						recPara.setField("STL_NO", 				arrStlNo[ii]);
						recPara.setField("YD_STK_LYR_MTL_STAT", "C");
						recPara.setField("YD_GP", 				JPlateYdConst.YD_GP_F_PLATE_YARD);
						intRtnVal = ydStkLyrDao.getYdStklyrByStlNoStat(recPara, rsResult);
						if (intRtnVal > 0) {
							rsResult.first();
							tempRec = rsResult.getRecord();
							szYdStkLyrNo 	= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_LYR_NO");
							szBookOutResn 	= ydDaoUtils.paraRecChkNull(tempRec, "BOOK_OUT_RESN");
							
							
							szPL_TRCK_ZONE_ASG = ydDaoUtils.paraRecChkNull(tempRec, "PL_TRCK_ZONE_ASG");  //REQ202306466285
							szPL_BOOK_OUT_MOD =ydDaoUtils.paraRecChkNull(tempRec, "PL_BOOK_OUT_MOD");  	//REQ202306466285
						}

						// 후판Plate번호 또는 후판날판번호 : 재료번호 (11자리)
						outRec.setField("STL_NO"+Integer.toString(ii+1),  			arrStlNo[ii]);

						// Book-Out원인 : Book-Out 일때만 Set
						if (JPlateYdConst.PP_BOOK_OUT_GP.equals(szPlBookInoutGp)) {		// '2' : BOOK-OUT
							outRec.setField("BOOK_OUT_RESN"+Integer.toString(ii+1), szBookOutResn);
							
							outRec.setField("PL_TRCK_ZONE_NO"+Integer.toString(ii+1), szPL_TRCK_ZONE_ASG);  //REQ202306466285
							outRec.setField("PL_BOOK_OUT_MOD"+Integer.toString(ii+1), szPL_BOOK_OUT_MOD); //REQ202306466285
							
						} else {
							outRec.setField("BOOK_OUT_RESN"+Integer.toString(ii+1), "");
						}

						// 후판북인아웃구분
						outRec.setField("PL_BOOK_INOUT_GP"+Integer.toString(ii+1), 	szPlBookInoutGp);

						// 저장위치
						outRec.setField("YD_STK_LYR_NO"+Integer.toString(ii+1), 	szYdStkLyrNo);
					}
				}
				
				if(!"".equals(szIS_PF_TO_PF)){
					outRec.setField("IS_PF_TO_PF", 	    szIS_PF_TO_PF);    
				}
				

			} else {

				if (!"".equals(szYdStkColFr)) {
					szFromStoreLoc  = szYdStkColFr + ("".equals(szYdStkBedFr)?"01":szYdStkBedFr);
				}
				if (!"".equals(szYdStkColTo)) {
					szToStoreLoc    = szYdStkColTo + ("".equals(szYdStkBedTo)?"01":szYdStkBedTo);
				}
				// 2013.07.22 보완 :: FROM위치가 NULL일때 보완
				// 2013.09.16 보완 :: FROM위치가 NULL일때 NULL로 전송 (조업요청)
				//if ("".equals(szFromStoreLoc)) {
				//	szFromStoreLoc = ydUtils.substr(szToStoreLoc, 0, 2) + "010101";
				//}

				//=======================================================================================================================
				// 적치단 테이블 조회 : 조회조건 TO저장위치로 매수만큼 상단부터 조회
				//=======================================================================================================================
				recPara.setField("YD_STK_COL_GP", szYdStkColTo);
				recPara.setField("YD_STK_BED_NO", szYdStkBedTo);
				recPara.setField("YD_EQP_WRK_SH", szYdEqpWrkSh);
				intRtnVal = ydStkLyrDao.getStlNoTopCnt(recPara, rsResult);

				if (intRtnVal < 0) {
					szMsg = "적치단 테이블 조회오류 .. 저장위치(" + szToStoreLoc + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
					ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return 0;
				} else if (intRtnVal == 0) {
					szMsg = "적치단 테이블 조회건수 없음 .. 저장위치(" + szToStoreLoc + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
					ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					return 0;
				}

				// Interface Layout에 20건임으로 Max 20건만 전송
				if (rsResult.size() <= 20) {
					szYdEqpWrkSh = Integer.toString(rsResult.size());
				}

				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();
				outRec.setField("JMS_TC_CD", 			"YDPPJ011");
				outRec.setField("JMS_TC_CREATE_DDTT", 	JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));
				outRec.setField("YD_GP", 				JPlateYdConst.YD_GP_F_PLATE_YARD);	// 2후판정정야드
				outRec.setField("FROM_STORE_LOC", 		szFromStoreLoc);					// From저장위치 : 적치열+베드
				outRec.setField("TO_STORE_LOC", 		szToStoreLoc);						// To저장위치 : 적치열+베드
				outRec.setField("YD_EQP_WRK_SH", 		szYdEqpWrkSh);						// 야드설비작업매수

				// To위치가 RT일때 - Book-In으로 Set
				if ("RT".equals(ydUtils.substr(szToStoreLoc,2,2))) {
					szPlBookInoutGp = JPlateYdConst.PP_BOOK_IN_GP;		// '1' : BOOK-IN
				}

				// From위치가 RT일때 - Book-Out으로 Set
				if ("RT".equals(ydUtils.substr(szFromStoreLoc,2,2))) {
					szPlBookInoutGp = JPlateYdConst.PP_BOOK_OUT_GP;		// '2' : BOOK-OUT
				}

				for(int ii=0; ii<rsResult.size(); ii++) {

					if (ii < 20) {
						// 후판Plate번호 또는 후판날판번호 : 재료번호 (11자리)
						outRec.setField("STL_NO"+Integer.toString(ii+1),  ydDaoUtils.paraRecChkNull(rsResult.getRecord(ii), "STL_NO"));

						if (JPlateYdConst.PP_BOOK_OUT_GP.equals(szPlBookInoutGp)) {		// '2' : BOOK-OUT
							// Book-Out원인 : Book-Out 일때만 Set
							outRec.setField("BOOK_OUT_RESN"+Integer.toString(ii+1), ydDaoUtils.paraRecChkNull(rsResult.getRecord(ii), "BOOK_OUT_RESN"));
						}
						// 후판북인아웃구분
						outRec.setField("PL_BOOK_INOUT_GP"+Integer.toString(ii+1), 	szPlBookInoutGp);
						// 저장위치
						outRec.setField("YD_STK_LYR_NO"+Integer.toString(ii+1), 	ydDaoUtils.paraRecChkNull(rsResult.getRecord(ii), "YD_STK_LYR_NO"));
					}
				}
			}
			
			
			if (JPlateYdConst.PP_BOOK_OUT_GP.equals(szPlBookInoutGp)) {		// '2' : BOOK-OUT일때만 아래 항목 추가 전송
				outRec.setField("PL_BOOK_OUT_CRANE", 	szBookOutCrn);  //REQ202306466285
				outRec.setField("PL_BOOK_OUT_PIT", 	    "");  //REQ202306466285   
			}
			
			if(!"".equals(szIS_PF_TO_PF)){
				outRec.setField("IS_PF_TO_PF", 	    szIS_PF_TO_PF);    
			}
			
			

			// RecordSet에 추가
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n======================makeYDPPJ011() OUT =======================\n", JPlateYdConst.DEBUG);
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG);

		} catch(Exception e) {
			szMsg = "S1 (후판조업L3) 송신  저장위치변경정보  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return -1;
		}

		return outRecSet.size();
	} // end of makeYDPPJ011()

} // end of class MaktTcPP
