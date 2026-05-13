/*
 * @(#) Y2 (1후판정정야드L2) 송신 용 전문 생성
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/14
 *
 * @description		(1후판정정야드L2) 송신 용 전문 생성
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/14   김현우      김현우       최초작성 
 */

package com.inisteel.cim.yd.jplateyd.util;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.DateHelper;

import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCarSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO;
import com.inisteel.cim.yd.jplateyd.delegate.JPlateYdDelegate;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 * Y2 (1후판정정야드L2) 송신 용 전문 생성
 * @author hwkim
 *
 */
public class JPlateYdMakeTcY2 {

	// YDY2L001	저장위치제원
	// YDY2L002	저장품제원
	// YDY2L004	크레인작업지시
	// YDY2L005	크레인작업실적응답
	// YDY2L006	가이던스메세지
	// YDY2L007	크레인작업메세지

	// 클래스명
	private static final String SZ_CLASS_NAME  = JPlateYdMakeTcY2.class.getName();

	/**
	 * YDY2L001 : 저장위치제원
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeYDY2L001(JDTORecord inRec, JDTORecordSet outRecSet) {
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDY2L001
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER  (4)
		//		6.	임시						TEMP					VARCHAR2(29)

		//		7.	야드정보동기화코드			YD_INFO_SYNC_CD			VARCHAR2(1)		1:동,2:SPAN,3:열,4:BED
		//		8.	야드구분					YD_GP					VARCHAR2(1)		"F"
		//		9.	야드동구분				YD_BAY_GP				VARCHAR2(1)		부하를 방지하기 위해 최대 동 단위를 허용함
		//		10.	야드설비구분				YD_EQP_GP				VARCHAR2(2)		야드의 설비, Span을 코드로 부여한 Data
		//		11.	야드적치열번호				YD_STK_COL_NO			VARCHAR2(2)		숫자 0을 포함하여 관리 ("01")
		//		12.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		숫자 0을 포함하여 관리 ("01")
		//		13.	야드적치Bed길이구분		YD_STK_BED_L_GP			VARCHAR2(1)		"S" 단척, "M" 중척, "L" 장척
		//		14.	야드적치Bed폭구분			YD_STK_BED_W_GP			VARCHAR2(1)		"N" 협폭, "M" 보폭, "W" 광폭
		//		15.	야드적치Bed방향구분		YD_STK_BED_DIR_GP		VARCHAR2(1)		"X" 주행, "Y" 횡행
		//		16.	야드적치Bed활성상태		YD_STK_BED_ACT_STAT		VARCHAR2(1)		DD 참조
		//		17.	야드적치Bed입출고상태		YD_STK_BED_WHIO_STAT	VARCHAR2(1)		DD 참조
		//		18.	야드적치BedX축			YD_STK_BED_XAXIS		NUMBER  (7)		Center 지점
		//		19.	야드적치BedY축			YD_STK_BED_YAXIS		NUMBER  (5)		Center 지점
		//		20.	야드적치BedZ축			YD_STK_BED_ZAXIS		NUMBER  (5)		최하단 바닥 높이
		//		21.	야드적치Bed단Max			YD_STK_BED_LYR_MAX		NUMBER  (3)
		//		22.	야드적치Bed중량Max			YD_STK_BED_WT_MAX		NUMBER  (7)
		//		23.	야드적치Bed높이Max			YD_STK_BED_H_MAX		NUMBER  (5)
		//		24.	야드적치Bed길이Max			YD_STK_BED_L_MAX		NUMBER  (5)		야드적치열길이로 SET [YD_STK_COL_L]
		//      25.	야드적치Bed폭Max			YD_STK_BED_W_MAX		NUMBER  (5,1)	9999.9(소수점 없는 유효 Data)
		//		26.	야드차량착발상태			YD_CAR_ARRSTRT_STAT		VARCHAR2(1)		"A": 도착,        "S": 출발
		//		37.	야드차량사용구분			YD_CAR_USE_GP			VARCHAR2(1)		"L" :구내운송차량,  "G": 제품출하차량
		//		48.	야드설비작업상태 			YD_EQP_WRK_STAT			VARCHAR2(1)		"L" : 공차(출하),  "U" : 영차(반입)
		//		29.	차량번호					CAR_NO					VARCHAR2(15)	제품출하차량
		//		30.	운송장비코드				TRN_EQP_CD				VARCHAR2(8)		구내운송차량
		//		31.	카드번호					CARD_NO					VARCHAR2(4)		제품출하차량
		//		32.	야드차량목표야드구분		YD_CAR_AIM_YD_GP		VARCHAR2(1)		차량하차 야드

		// 레코드 선언
		JDTORecord recPara 			 = null;
		JDTORecord recParaCarSch 	 = null;
		JDTORecordSet rsResult 		 = null;
		JDTORecordSet rsResultCarSch = null;
		JDTORecord recGetVal 		 = null;
		JDTORecord recGetValCarSch   = null;
		JDTORecord outRec 			 = null;

		// DAO객체 생성
		JPlateYdCarSchDAO 	ydCarSchDao	= new JPlateYdCarSchDAO();
		JPlateYdStkBedDAO 	ydStkBedDao = new JPlateYdStkBedDAO();
		JPlateYdDaoUtils 	ydDaoUtils  = new JPlateYdDaoUtils();
		JPlateYdUtils 		ydUtils     = new JPlateYdUtils();

		// 변수선언
		String szMethodName     	= "makeYDY2L001";
		String szOperationName      = "1후판정정야드L2 저장위치제원";
		String szMsg        	    = "";
		String szTemp        	    = "";
		String szConv               = "";

		String szYdCarArrstrtStat 	= "";
		String szYdEqpWrkStat     	= "";
		String szYdCarAimYdGp    	= "";
		String szYdInfoSyncCd     	= "";
		String szYdStkColGp       	= "";
		String szYdStkBedNo       	= "";
		String szYdCarProgStat    	= "";
		String szYdCarUseGp       	= "";
		String szTrnEqpCd          	= "";
		String szCarNo              = "";
		String szCardNo             = "";

		// 리턴값
		int intRtnVal               = 0;
		int intRtnValCarSch         = 0;

		// TC Length = 147 (HEADER:60 + BODY:87)
		int nTcLen                  = 87;

		try{
			// Debug MSG
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n=======================makeYDY2L001() IN========================\n", JPlateYdConst.DEBUG);
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG);

			// 레코드 생성
			rsResult      = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
			recPara       = JDTORecordFactory.getInstance().create();
			recParaCarSch = JDTORecordFactory.getInstance().create();

			szYdInfoSyncCd = ydDaoUtils.paraRecChkNull(inRec, "YD_INFO_SYNC_CD");
			szYdStkColGp   = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP");
			szYdStkBedNo   = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");

			// 넘겨 받음
			szYdCarProgStat = ydDaoUtils.paraRecChkNull(inRec, "YD_CAR_PROG_STAT");
			szYdEqpWrkStat  = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_WRK_STAT");

			//=======================================================================================================================
			// 적치BED, 적치열 테이블 조회
			//=======================================================================================================================
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);
			
			//=======================================================================================================================
			//L2 자체 좌표관리 베드는 L3->L2 적치대 정보전송 안함.
			//2021.12.17 박종호-소영조 차장 요청사항
			//대상야드:
			//PE015401, PE015402,PE015403,
			//PE015501, PE015502,PE015503,
			//PE015601, PE015602,PE015603,
			//PECB0101, PECB0102,PECB0103,
			//PECB0201, PECB0202,PECB0203,			
			//PECB0301, PECB0302,PECB0303,
			//PECB0401, PECB0402,PECB0403
			//=======================================================================================================================			
			if(
					(szYdStkColGp.equals("PE0154") || szYdStkColGp.equals("PE0155") || szYdStkColGp.equals("PE0156") || szYdStkColGp.equals("PECB01") || szYdStkColGp.equals("PECB02") || szYdStkColGp.equals("PECB03") || szYdStkColGp.equals("PECB04"))
				    && 
				    (szYdStkBedNo.equals("01") || szYdStkBedNo.equals("02") || szYdStkBedNo.equals("03"))
				)
			{
				szMsg = "Y2(1후판정정야드L2) 송신 예외 발생: L2 자체 관리 베드 정보. 해당 적치대: "+szYdStkColGp+szYdStkBedNo;
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return -1;	
			}
			
			
//2후판거 사용			
			intRtnVal = ydStkBedDao.getYDY7L001Info(recPara, rsResult);

			if (intRtnVal < 0) {
				szMsg = "적치BED 테이블 조회오류 YD_STK_COL_GP(" + szYdStkColGp + ") YD_STK_BED_NO(" + szYdStkBedNo + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return 0;
			} else if (intRtnVal == 0) {
				szMsg = "적치BED 테이블 조회건수 없음 YD_STK_COL_GP(" + szYdStkColGp + ") YD_STK_BED_NO(" + szYdStkBedNo + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return 0;
			}

			for(int ii=0; ii<intRtnVal; ii++) {
				recGetVal = rsResult.getRecord(ii);

				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();
				outRec.setField("MSG_ID", 				"YDY2L001");
				outRec.setField("DATE", 				JPlateYdUtils.getCurDate("yyyy-MM-dd"));
				outRec.setField("TIME", 				JPlateYdUtils.getCurDate("HH-mm-ss"));
				outRec.setField("MSG_GP", 				"I");
				outRec.setField("MSG_LEN", 				JPlateYdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0));
				outRec.setField("TEMP", 				JPlateYdUtils.fillSpZr("", 29, 1));

				// 야드정보동기화코드 [야드정보동기화코드]
				outRec.setField("YD_INFO_SYNC_CD", 		JPlateYdUtils.fillSpZr(szYdInfoSyncCd, 1, 1));

				// 야드구분 [야드구분]
				outRec.setField("YD_GP", 				JPlateYdUtils.fillSpZrRec(recGetVal, "YD_GP", 1, 1));

				// 야드동구분 [야드동구분]
				outRec.setField("YD_BAY_GP", 			JPlateYdUtils.fillSpZrRec(recGetVal, "YD_BAY_GP", 1, 1));

				// 야드설비구분 [야드설비구분]
				outRec.setField("YD_EQP_GP", 			JPlateYdUtils.fillSpZrRec(recGetVal, "YD_EQP_GP", 2, 1));

				// 야드적치열번호 [야드적치열번호]
				outRec.setField("YD_STK_COL_NO", 		JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STK_COL_NO", 2, 1));

				// 야드적치Bed번호 [야드적치Bed번호]
				outRec.setField("YD_STK_BED_NO", 		JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STK_BED_NO", 2, 1));

				// 야드적치Bed길이구분 [야드적치Bed길이구분]
				outRec.setField("YD_STK_BED_L_GP", 		JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STK_BED_L_GP", 1, 1));

				// 야드적치Bed폭구분 [야드적치Bed폭구분]
				outRec.setField("YD_STK_BED_W_GP", 		JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STK_BED_W_GP", 1, 1));

				// 야드적치Bed방향구분 [야드적치Bed방향구분]
				outRec.setField("YD_STK_BED_DIR_GP", 	JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STK_BED_DIR_GP", 1, 1));

				// 야드적치Bed활성상태 [야드적치Bed활성상태]
				outRec.setField("YD_STK_BED_ACT_STAT", 	JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STK_BED_ACT_STAT", 1, 1));

				// 야드적치Bed입출고상태 [야드적치Bed입출고상태]
				outRec.setField("YD_STK_BED_WHIO_STAT", JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STK_BED_WHIO_STAT", 1, 1));

				// 야드적치BedX축 [야드적치BedX축]
				outRec.setField("YD_STK_BED_XAXIS", 	JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STK_BED_XAXIS", 7, 0));

				// 야드적치BedY축 [야드적치BedY축]
				outRec.setField("YD_STK_BED_YAXIS", 	JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STK_BED_YAXIS", 5, 0));

				// 야드적치BedZ축 [야드적치BedZ축]
				outRec.setField("YD_STK_BED_ZAXIS", 	JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STK_BED_ZAXIS", 5, 0));

				// 야드적치Bed단Max [야드적치Bed단Max]
				outRec.setField("YD_STK_BED_LYR_MAX", 	JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STK_BED_LYR_MAX", 3, 1));

				// 야드적치Bed중량Max [야드적치Bed중량Max]
				outRec.setField("YD_STK_BED_WT_MAX", 	JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STK_BED_WT_MAX", 7, 0));

				// 야드적치Bed높이Max [야드적치Bed높이Max]
				outRec.setField("YD_STK_BED_H_MAX", 	JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STK_BED_H_MAX", 5, 0));

				// 야드적치Bed길이Max [야드적치Bed길이Max] <<-- 야드적치열길이로 SET
				outRec.setField("YD_STK_BED_L_MAX", 	JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STK_COL_L", 5, 0));

				// 야드적치Bed폭Max [야드적치Bed폭Max]
				szConv = JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STK_BED_W_MAX", 6, 1);
				outRec.setField("YD_STK_BED_W_MAX", 	ydUtils.floatLRPAD(szConv, 5, 1, '0'));

				// 차량정보
				szYdCarUseGp = ydDaoUtils.paraRecChkNull(recGetVal, "YD_CAR_USE_GP").trim();
				szTrnEqpCd   = ydDaoUtils.paraRecChkNull(recGetVal, "TRN_EQP_CD");
				szCarNo      = ydDaoUtils.paraRecChkNull(recGetVal, "CAR_NO");
				szCardNo     = ydDaoUtils.paraRecChkNull(recGetVal, "CARD_NO");

				//=======================================================================================================================
				// 차량스케쥴 조회
				//=======================================================================================================================
				szYdStkColGp = ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_COL_GP");
				if ("PT".equals(ydUtils.substr(szYdStkColGp, 2, 2))) {
					// 야드차량목표야드구분 [야드차량목표야드구분]
					rsResultCarSch = JDTORecordFactory.getInstance().createRecordSet("");
					if ("L".equals(szYdCarUseGp)) {
						recParaCarSch.setField("TRN_EQP_CD", szTrnEqpCd);
						intRtnValCarSch = ydCarSchDao.getYdCarschDaoTrnEqpCd(recParaCarSch, rsResultCarSch);
						if (intRtnValCarSch <= 0) {
							szYdCarAimYdGp = JPlateYdUtils.fillSpZr(" ", 1, 1);
						} else {
							rsResultCarSch.first();
							recGetValCarSch = rsResultCarSch.getRecord();
							szTemp = JPlateYdUtils.fillSpZrRec(recGetValCarSch, "YD_CARUD_STOP_LOC", 6, 1).trim();
							if ("".equals(szTemp.trim())) {
								szYdCarAimYdGp = JPlateYdUtils.fillSpZr(" ", 1, 1);
							} else {
								szYdCarAimYdGp = JPlateYdUtils.fillSpZr(ydUtils.substr(szTemp, 0, 1), 1, 1);
							}
						}
					} else if ("G".equals(szYdCarUseGp)) {
						recParaCarSch.setField("CAR_NO", szCarNo);
						recParaCarSch.setField("CARD_NO", szCardNo);
						intRtnValCarSch = ydCarSchDao.getYdCarschCarNoCardNo(recParaCarSch, rsResultCarSch);
						if (intRtnValCarSch <= 0) {
							szYdCarAimYdGp = JPlateYdUtils.fillSpZr(" ", 1, 1);
						} else {
							rsResultCarSch.first();
							recGetValCarSch = rsResultCarSch.getRecord();
							szTemp = JPlateYdUtils.fillSpZrRec(recGetValCarSch, "YD_CARUD_STOP_LOC", 6, 1).trim();
							if ("".equals(szTemp.trim())) {
								szYdCarAimYdGp = JPlateYdUtils.fillSpZr(" ", 1, 1);
							} else {
								szYdCarAimYdGp = JPlateYdUtils.fillSpZr(ydUtils.substr(szTemp, 0, 1), 1, 1);
							}
						}
					}

					// 야드차량착발상태 [야드차량진행상태]
					if ("".equals(szYdCarProgStat) || "A".equals(szYdCarProgStat)) {
						szYdCarArrstrtStat = JPlateYdUtils.fillSpZr("S", 1, 1);
					} else if ("2".equals(szYdCarProgStat) || "B".equals(szYdCarProgStat)) {
						szYdCarArrstrtStat = JPlateYdUtils.fillSpZr("A", 1, 1);
					} else{
						szYdCarArrstrtStat = JPlateYdUtils.fillSpZr(" ", 1, 1);
					}

				} else {
					szYdCarArrstrtStat = JPlateYdUtils.fillSpZr(" ", 1, 1);
					szYdCarAimYdGp     = JPlateYdUtils.fillSpZr(" ", 1, 1);
				}

				outRec.setField("YD_CAR_ARRSTRT_STAT", 	JPlateYdUtils.fillSpZr(szYdCarArrstrtStat, 1, 1));

				// 야드차량사용구분 [야드차량사용구분]
				outRec.setField("YD_CAR_USE_GP", 		JPlateYdUtils.fillSpZrRec(recGetVal, "YD_CAR_USE_GP", 1, 1));

				// 야드설비작업상태 [야드설비작업상태]
				outRec.setField("YD_EQP_WRK_STAT", 		JPlateYdUtils.fillSpZr(szYdEqpWrkStat, 1, 1));

				// 차량번호 [차량번호]
				outRec.setField("CAR_NO", 				JPlateYdUtils.fillSpZrKor(ydDaoUtils.paraRecChkNull(recGetVal, "CAR_NO"), 15, 1));

				// 운송장비코드 [운송장비코드]
				outRec.setField("TRN_EQP_CD", 			JPlateYdUtils.fillSpZrRec(recGetVal, "TRN_EQP_CD", 8, 1));

				// 카드번호 [카드번호]
				outRec.setField("CARD_NO", 				JPlateYdUtils.fillSpZrRec(recGetVal, "CARD_NO", 4, 1));

				// 야드차량목표야드구분 [야드차량목표야드구분]
				outRec.setField("YD_CAR_AIM_YD_GP", 	JPlateYdUtils.fillSpZr(szYdCarAimYdGp, 1, 1));

				// RecordSet에 추가
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n======================makeYDY2L001() OUT("+ Integer.toString(ii) + ")==========================\n", JPlateYdConst.DEBUG);
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG);
			}
		}catch(Exception e) {
			szMsg = "Y2(1후판정정야드L2) 송신  저장위치제원  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return -1;
		}

		return outRecSet.size();
	} // end of makeYDY2L001()

	/**
	 * YDY2L002 : 저장품제원
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeYDY2L002(JDTORecord inRec, JDTORecordSet outRecSet) {
		//		1.	전문 ID						MSG_ID					VARCHAR2(8)		YDY2L002
		//		2.	생성일						DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER  (4)
		//		6.	임시						TEMP					VARCHAR2(29)

		//		7.	야드정보동기화코드			YD_INFO_SYNC_CD			VARCHAR2(1)		1:동,2:SPAN,3:열,4:BED,5:지정저장품,D:생산종료(삭제),T:최상단(5단),S:최상단(30매)
		//		8.	야드재료정보송신매수		YD_STL_INFO_SND_SH		NUMBER  (3)
		//		9.	야드재료정보송신순번		YD_STL_INFO_SND_CNT		NUMBER  (3)
		//		10.	재료외형구분				STL_APPEAR_GP			VARCHAR2(1)
		//		11.	재료번호					STL_NO					VARCHAR2(11)
		//		12.	야드저장위치				YD_STR_LOC				VARCHAR2(8)		야드적치Bed까지 표현
		//		13.	야드적치단번호				YD_STK_LYR_NO			VARCHAR2(3)
		//		14.	야드재료중량				YD_STL_WT				NUMBER  (5)
		//		15.	야드재료두께				YD_STL_T				NUMBER  (6,3)
		//		16.	야드재료폭					YD_STL_W				NUMBER  (5,1)
		//		17.	야드재료길이				YD_STL_L				NUMBER  (7)
		//      18. 재료외경                                           MAT_ODIA                NUMBER  (5)
		//      19. 재료내경                                           MAT_IDIA                NUMBER  (5,1)
		//		20.	강종						STLKIND_CD				VARCHAR2(3)		연주전단지시에서 Heat 사양, 작업지시 Table 항목을 저장품Table에 등록
		//		21.	규격약호					SPEC_ABBSYM				VARCHAR2(15)	연주전단실적에서 주편/슬라브 공통Table 항목을 저장품Table에 등록
		//		22.	야드입고일자				YD_IPGO_DD				VARCHAR2(14)	현재 야드에 최초 적치 시 저장품 Table에 등록
		//		23.	공장공정코드				PLNT_PROC_CD			VARCHAR2(3)		직전 생산공장
		//		24.	현재진도코드				CURR_PROG_CD			VARCHAR2(1)
		//		25.	주문여재구분				ORD_YEOJAE_GP			VARCHAR2(1)
		//		26.	주문번호					ORD_NO					VARCHAR2(10)
		//		27.	주문행번					ORD_DTL					VARCHAR2(3)
		//		28.	구입SLAB번호				BUY_SLAB_NO				VARCHAR2(30)
		//		29.	SLAB지시행선코드			SLAB_WO_RT_CD			VARCHAR2(2)
		//		30.	설계HCR구분					ORD_HCR_GP				VARCHAR2(1)
		//		31.	HCR구분						HCR_GP					VARCHAR2(1)
		//		32.	연주Machine코드				CC_MC_CD				VARCHAR2(1)
		//		33.	SCARFING여부				SCARFING_YN				VARCHAR2(1)
		//		34.	SCARFING완료유무			SCARFING_DONE_YN		VARCHAR2(1)
		//      35. 주편손질방법                RPR_MTD                 VARCHAR2(1)
		//      36. SCARFING깊이                SCARFING_DEPTH          VARCHAR2(2)
		//		37.	재열재구분					REHEAT_SLAB_GP			VARCHAR2(1)
		//		38.	조업공장구분				PTOP_PLNT_GP			VARCHAR2(2)		조업공장구분의 두번째 자리
 		//		39.	가열로장입Lot번호	   		REFUR_CHG_LOT_NO		VARCHAR2(10)	2013.09.05 파일링코드로 사용
		//		40.	생산통제Lot스케줄일련번호	CT_LOT_SCH_SERNO		VARCHAR2(22)	압연지시에서 저장품Table에 등록
		//		41.	이송지시일자				FRTOMOVE_ORD_DATE		VARCHAR2(8)
		//		42.	이송공장구분				FRTOMOVE_PLANT_GP		VARCHAR2(2)		임가공인 경우 임가공사 코드
		//		43.	긴급이송작업지시구분		URGENT_FRTOMOVE_WORD_GP	VARCHAR2(1)		2013.09.04 긴급재구분으로 사용
		//		44.	HYSCO운송구분				HYSCO_TRANS_CLS			VARCHAR2(1)
		//		45.	외관종합판정등급			APPEAR_GRADE			VARCHAR2(1)
		//      46. 권취코일냉각방법            COOL_METHOD             VARCHAR2(1)
		//		47.	냉각완료구분				COOL_DONE_GP			VARCHAR2(1)
		//      48. 야드Conveyor분기코드        CONV_BRANCH_CD          VARCHAR2(2)
		//		49.	고객코드					CUST_CD					VARCHAR2(6)
		//		50.	목적지코드					DEST_CD					VARCHAR2(5)
		//		51.	납기기준일					DLVRDD_RULE_DD			VARCHAR2(8)
		//		52.	품명코드					ITEMNAME_CD				VARCHAR2(3)
		//		53.	종합판정등급				OVERALL_STAMP_GRADE		VARCHAR2(1)
		//		54.	수주구분					ORD_GP					VARCHAR2(1)
		//		55.	야드산적LotType				YD_STK_LOT_TP			VARCHAR2(2)
		//		56.	야드산적Lot코드				YD_STK_LOT_CD			VARCHAR2(18)
		//		57.	후판 L2 제품번호			PL_L2_TRK_NO			VARCHAR2(16)

		// 레코드 선언
		JDTORecord recPara 		= null;
		JDTORecordSet rsResult	= null;
		JDTORecordSet rsSResult	= null;
		JDTORecordSet rsTemp	= null;
		JDTORecordSet rsMResult = null;
		JDTORecord recGetVal    = null;
		JDTORecord recGetVal2   = null;
		JDTORecord outRec       = null;

		// DAO객체 생성
		JPlateYdDaoUtils 	ydDaoUtils  = new JPlateYdDaoUtils();
		JPlateYdUtils 		ydUtils     = new JPlateYdUtils();
		JPlateYdStockDAO 	ydStockDao  = new JPlateYdStockDAO();
		
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
    	YdUtils 			ydLogUtils  = new YdUtils();
//-------------------------------------------------------------------------------------------------------------------------

		// 변수선언 
		String 	szMethodName    = "makeYDY2L002";
		String 	szOperationName = "1후판정정야드L2 저장품제원";
		String 	szMsg           = "";

		String 	szConv          = "";

		String 	szYdInfoSyncCd 	= "";
		String 	szYdStkColGp  	= "";
		String 	szYdStkBedNo  	= "";

		String 	szStlkindCd     = "";
		String 	szSpecAbbsym    = "";
		String 	szMillPlntGp   	= "";
		String 	szScarfingDepth	= "";
		String 	szDelYnCheck   	= "";

		// 리턴값
		int 	intRtnVal       = 0;
		int		iLoopCnt		= 0;

		// TC Length = 309 (60 + 249)
		int 	nTcLen          = 265;

		String 	szStlNo         = "";
		String 	szRtnMsg        = "";
		String	szYdStkLoc		= "";
		String	szMsgGp			= "";
		String	szArrStlNo		= "";
		String	szUrgentGp		= "";
		String	szYdPilingCd	= "";
		String	szPlL2TrkNo		= "";
		String szRtSpeed ="";
		
		String szIsPillingModBookIn ="";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try{
			// Debug MSG
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n=======================makeYDY2L002() IN========================\n", JPlateYdConst.DEBUG, logId);
			ydUtils.displayRecord(szOperationName, inRec);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG, logId);

			// 레코드 생성
			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("ydTemp");
			recPara 	= JDTORecordFactory.getInstance().create();

			//=======================================================================================================================
			// 적치BED, 적치열 테이블 조회
			//=======================================================================================================================
			szStlNo        			= ydDaoUtils.paraRecChkNull(inRec, "STL_NO"				);
			szYdInfoSyncCd 			= ydDaoUtils.paraRecChkNull(inRec, "YD_INFO_SYNC_CD"	);
			szYdStkColGp   			= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP"		);
			szYdStkBedNo   			= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO"		);
			szDelYnCheck   			= ydDaoUtils.paraRecChkNull(inRec, "DEL_YN_CHECK"		);
			szMsgGp					= ydDaoUtils.paraRecChkNull(inRec, "MSG_GP", "I"		);
			szArrStlNo				= ydDaoUtils.paraRecChkNull(inRec, "ARR_STL_NO"			);

			szIsPillingModBookIn	= ydDaoUtils.paraRecChkNull(inRec, "isPillingModBookIn"	);
			//String szrecL2Para.setField("isPillingModBookIn", 			"Y");
			// 파라미터로 재료번호 Array가 넘어 왔을때 재료조회 안함
			if (!"".equals(szArrStlNo)) {

				//=======================================================================================================================
				// 재료 건수만큼 반복
				//=======================================================================================================================
				String[] arrStlNo = szArrStlNo.split(";");

				if (arrStlNo == null || arrStlNo.length < 1) {
					szMsg = "Y2 (1후판정정L2) 송신  저장품제원  데이터중 재료번호 오류 : " + szArrStlNo;
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return -1;
				}

				for(int ii=0; ii<arrStlNo.length; ii++) {
					rsTemp = JDTORecordFactory.getInstance().createRecordSet("yd");
					recPara  = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO",	arrStlNo[ii]);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recPara에 logId 추가 
					recPara.setField("LOG_ID", 			logId 											);      // logId
//-------------------------------------------------------------------------------------------------------------------------
					    	        
					intRtnVal = ydStockDao.getYdStkBedStl(recPara, rsTemp);
					
					if (intRtnVal > 0) {
						rsResult.addAll(rsTemp);
					}
				}
				intRtnVal = rsResult.size();

			} else {

				// 재료정보 유무에 따른 조회
				if ("1".equals(szYdInfoSyncCd) || "2".equals(szYdInfoSyncCd) || "3".equals(szYdInfoSyncCd) || "4".equals(szYdInfoSyncCd)) {

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_STK_COL_GP", szYdStkColGp);
					recPara.setField("YD_STK_BED_NO", szYdStkBedNo);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recPara에 logId 추가 
					recPara.setField("LOG_ID", 			logId 											);      // logId
//-------------------------------------------------------------------------------------------------------------------------
										    	        
					if(szIsPillingModBookIn.equals("Y")) {
						intRtnVal = ydStockDao.getYdStkBedNotStlForPillingBookIn(recPara, rsResult);
					}
					else {	
						intRtnVal = ydStockDao.getYdStkBedNotStl(recPara, rsResult);
					}
					
				} else if ("5".equals(szYdInfoSyncCd) || "A".equals(szYdInfoSyncCd) || "B".equals(szYdInfoSyncCd) || "C".equals(szYdInfoSyncCd)) {

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", szStlNo);
					
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recPara에 logId 추가 
					recPara.setField("LOG_ID", 			logId 											);      // logId
//-------------------------------------------------------------------------------------------------------------------------

					if ("N".equals(szDelYnCheck)) {
						intRtnVal = ydStockDao.getYdStkBedStlDelCheck(recPara, rsResult);
					} else {
						intRtnVal = ydStockDao.getYdStkBedStl(recPara, rsResult);
					}
				} else if ("T".equals(szYdInfoSyncCd)) {		// 최상단 5단

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_STK_COL_GP", szYdStkColGp);
					recPara.setField("YD_STK_BED_NO", szYdStkBedNo);
					
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recPara에 logId 추가 
					recPara.setField("LOG_ID", 			logId 											);      // logId
//-------------------------------------------------------------------------------------------------------------------------

// 2후판거 사용
					intRtnVal = ydStockDao.getY7YDL002TopLyr(recPara, rsResult);

				} else if ("S".equals(szYdInfoSyncCd)) {		// 최상단 30매

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_STK_COL_GP", szYdStkColGp);
					recPara.setField("YD_STK_BED_NO", szYdStkBedNo);
					
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recPara에 logId 추가 
					recPara.setField("LOG_ID", 			logId 											);      // logId
//-------------------------------------------------------------------------------------------------------------------------

// 2후판거 사용
					intRtnVal = ydStockDao.getY7YDL002TopCnt(recPara, rsResult);
				}
			}

			if (intRtnVal < 0) {
				szMsg = "저장품 + 적치BED + 적치단  테이블 조회오류 YD_INFO_SYNC_CD(" + szYdInfoSyncCd + ") YD_STK_COL_GP(" + szYdStkColGp
				      + ") YD_STK_BED_NO(" + szYdStkBedNo + ") STLNO(" + szStlNo +  ") [Ret : " + Integer.toString(intRtnVal) + "]";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return 0;
			} else if (intRtnVal == 0) {
				szMsg = "저장품 + 적치BED + 적치단  테이블 조회건수 없음  YD_INFO_SYNC_CD(" + szYdInfoSyncCd + ") YD_STK_COL_GP(" + szYdStkColGp
                      + ") YD_STK_BED_NO(" + szYdStkBedNo + ") STLNO(" + szStlNo +  ") [Ret : " + Integer.toString(intRtnVal) + "]";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				return 0;
			} else {
				szMsg = "저장품 + 적치BED + 적치단  테이블 조회성공 YD_INFO_SYNC_CD(" + szYdInfoSyncCd + ") YD_STK_COL_GP(" + szYdStkColGp
				      + ") YD_STK_BED_NO(" + szYdStkBedNo + ") STLNO(" + szStlNo +  ") [Ret : " + Integer.toString(intRtnVal) + "]";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			}

			iLoopCnt = rsResult.size();
			for(int ii=0; ii<iLoopCnt; ii++) {
				recGetVal = rsResult.getRecord(ii);

				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();
				outRec.setField("MSG_ID", 				"YDY2L002"																				);
				outRec.setField("DATE", 				JPlateYdUtils.getCurDate("yyyy-MM-dd")													);
				outRec.setField("TIME", 				JPlateYdUtils.getCurDate("HH-mm-ss")													);
				outRec.setField("MSG_GP", 				szMsgGp																					);
				outRec.setField("MSG_LEN", 				JPlateYdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0)									);
				outRec.setField("TEMP", 				JPlateYdUtils.fillSpZr("", 29, 1)														);

				// 야드정보동기화코드 [야드정보동기화코드]
				outRec.setField("YD_INFO_SYNC_CD", 		JPlateYdUtils.fillSpZr(szYdInfoSyncCd, 1, 1)											);

				// 야드재료정보송신매수 [야드재료정보송신매수]
				outRec.setField("YD_STL_INFO_SND_SH", 	JPlateYdUtils.fillSpZr(Integer.toString(iLoopCnt), 3, 0)								);

				// 야드재료정보송신순번 [야드재료정보송신순번]
				outRec.setField("YD_STL_INFO_SND_CNT", 	JPlateYdUtils.fillSpZr(Integer.toString(ii+1), 3, 0)									);

				// 재료외형구분 [재료외형구분]
				outRec.setField("STL_APPEAR_GP", 		JPlateYdUtils.fillSpZrRec(recGetVal, "STL_APPEAR_GP", 1, 1)								);

				// 재료번호 [재료번호]
				outRec.setField("STL_NO", 				JPlateYdUtils.fillSpZrRec(recGetVal, "STL_NO", 11, 1)									);

				// 야드저장위치 [야드저장위치 : BED까지]
				szYdStkLoc = ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_NO");

				outRec.setField("YD_STR_LOC", 			JPlateYdUtils.fillSpZr(szYdStkLoc, 8, 1)												);

				// 야드적치단번호 [야드적치단번호]
				outRec.setField("YD_STK_LYR_NO", 		JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STK_LYR_NO", 3, 1)								);

				// 야드재료중량 [야드재료중량]
				outRec.setField("YD_STL_WT", 			JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STL_WT", 5, 0)									);

				// 야드재료두께 [야드재료두께]
				szConv = JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STL_T", 7, 1);
				outRec.setField("YD_STL_T", 			ydUtils.floatLRPAD(szConv, 6, 3, '0')													);

				// 야드재료폭 [야드재료폭]
				szConv = JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STL_W", 6, 1);
				outRec.setField("YD_STL_W", 			ydUtils.floatLRPAD(szConv, 5, 1, '0')													);

				// 야드재료길이 [야드재료길이]
				outRec.setField("YD_STL_L", 			JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STL_L", 7, 0)									);

				// 재료외경
				outRec.setField("MAT_ODIA", 			JPlateYdUtils.fillSpZrRec(recGetVal, "MAT_ODIA", 5, 0)									);

				// 재료내경
				szConv = JPlateYdUtils.fillSpZrRec(recGetVal, "MAT_IDIA", 6, 1);
				outRec.setField("MAT_IDIA", 			ydUtils.floatLRPAD(szConv, 5, 1, '0')													);

				// 강종 [강종]
				szStlkindCd = JPlateYdUtils.fillSpZrRec(recGetVal, "STLKIND_CD", 3, 1);
				outRec.setField("STLKIND_CD", 			JPlateYdUtils.fillSpZr(szStlkindCd, 3, 1)												);

				// 규격약호 [규격약호]
				szSpecAbbsym = JPlateYdUtils.fillSpZrRec(recGetVal, "SPEC_ABBSYM", 15, 1);
				outRec.setField("SPEC_ABBSYM", 			JPlateYdUtils.fillSpZr(szSpecAbbsym, 15, 1)												);

				// 야드입고일자 [등록일자]
				outRec.setField("YD_IPGO_DD", 			JPlateYdUtils.fillSpZrRec(recGetVal, "REG_DDTT", 14, 1)									);

				// 공장공정코드 [공장공정코드]
				outRec.setField("PLNT_PROC_CD", 		JPlateYdUtils.fillSpZrRec(recGetVal, "PLNT_PROC_CD", 3, 1)								);

				// 현재진도코드 [재료진도코드]
				outRec.setField("CURR_PROG_CD", 		JPlateYdUtils.fillSpZrRec(recGetVal, "CURR_PROG_CD", 1, 1)								);

				// 주문여재구분 [주문여재구분]
				outRec.setField("ORD_YEOJAE_GP", 		JPlateYdUtils.fillSpZrRec(recGetVal, "ORD_YEOJAE_GP", 1, 1)								);

				// 주문번호 [주문번호]
				outRec.setField("ORD_NO", 				JPlateYdUtils.fillSpZrRec(recGetVal, "ORD_NO", 10, 1)									);

				// 주문행번 [주문행번]
				outRec.setField("ORD_DTL", 				JPlateYdUtils.fillSpZrRec(recGetVal, "ORD_DTL", 3, 1)									);

				// 구입SLAB번호 [구입SLAB번호]
				outRec.setField("BUY_SLAB_NO", 			JPlateYdUtils.fillSpZrKor(ydDaoUtils.paraRecChkNull(recGetVal, "BUY_SLAB_NO"), 30, 1)	);

				// SLAB지시행선코드 [SLAB지시행선코드]
				outRec.setField("SLAB_WO_RT_CD", 		JPlateYdUtils.fillSpZrRec(recGetVal, "SLAB_WO_RT_CD", 2, 1)								);

				// 설계HCR구분 [설계HCR구분]
				outRec.setField("ORD_HCR_GP", 			JPlateYdUtils.fillSpZrRec(recGetVal, "ORD_HCR_GP", 1, 1)								);

				// HCR구분 [HCR구분]
				outRec.setField("HCR_GP", 				JPlateYdUtils.fillSpZrRec(recGetVal, "HCR_GP", 1, 1)									);

				// 연주Machine코드 [야드CCM구분]
				outRec.setField("CC_MC_CD", 			JPlateYdUtils.fillSpZrRec(recGetVal, "CC_MC_CD", 1, 1)									);

				// SCARFING여부 [SCARFING여부]
				outRec.setField("SCARFING_YN", 			JPlateYdUtils.fillSpZrRec(recGetVal, "SCARFING_YN", 1, 1)								);

				// SCARFING완료유무 [SCARFING완료유무]
				outRec.setField("SCARFING_DONE_YN", 	JPlateYdUtils.fillSpZrRec(recGetVal, "SCARFING_DONE_YN", 1, 1)							);

				// 주편손질방법
				outRec.setField("RPR_MTD", 				JPlateYdUtils.fillSpZrRec(recGetVal, "RPR_MTD", 1, 1)									);

				szStlNo  	= ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO");

				szMsg = "STL_NO(" + szStlNo + ") 조업 공통테이블에서 조회 START >>>> " + szStlNo;
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				rsMResult 	= JDTORecordFactory.getInstance().createRecordSet("ydTemp");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO",	szStlNo);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 recPara에 logId 추가 
				recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
				
				intRtnVal 	= ydStockDao.getYdStockBookOut(recPara, rsMResult);
				
	        	if (intRtnVal <= 0) {
	        		szMsg = "조업 공통테이블에서 재료[" + szStlNo + "] 조회 시 오류발생 : " + szRtnMsg;
	                ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	        	} else {
					rsMResult.first();
					recGetVal2 		= rsMResult.getRecord();
					szScarfingDepth = ydDaoUtils.paraRecChkNull(recGetVal2, "SCARFING_DEPTH");			// 스카핑깊이
					szUrgentGp 		= ydDaoUtils.paraRecChkNull(recGetVal2, "URGENT_GP", "N");			// 긴급재구분
					szYdPilingCd	= ydDaoUtils.paraRecChkNull(recGetVal2, "YD_PILING_CD");			// 파일링코드
					szPlL2TrkNo		= ydDaoUtils.paraRecChkNull(recGetVal2, "PL_L2_TRK_NO");			// 파일링코드
	        	}

				szMsg = "STL_NO(" + szStlNo + ") SCARFING_DEPTH : " + szScarfingDepth + ", 긴급재 : " + szUrgentGp + ", 파일링코드 : " + szYdPilingCd;
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				// SCARFING깊이 [SCARFING깊이]
				outRec.setField("SCARFING_DEPTH", 			JPlateYdUtils.fillSpZr(szScarfingDepth, 2, 1)										);

				// 재열재구분 [재열재구분]
				outRec.setField("REHEAT_SLAB_GP", 			JPlateYdUtils.fillSpZrRec(recGetVal, "REHEAT_SLAB_GP", 1, 1)						);

				// 압연공장구분 [조업공장구분]
				szMillPlntGp = JPlateYdUtils.fillSpZrRec(recGetVal, "PTOP_PLNT_GP", 2, 1);
				outRec.setField("PTOP_PLNT_GP", 			JPlateYdUtils.fillSpZr(szMillPlntGp, 2, 1)											);

				// 가열로장입Lot번호 [가열로장입Lot번호]
			//	outRec.setField("REFUR_CHG_LOT_NO", 		JPlateYdUtils.fillSpZrRec(recGetVal, "REFUR_CHG_LOT_NO", 10, 1));
				outRec.setField("REFUR_CHG_LOT_NO", 		JPlateYdUtils.fillSpZr(szYdPilingCd, 10, 1)											);

				// 생산통제Lot스케줄일련번호 [생산통제Lot스케줄일련번호]
				outRec.setField("CT_LOT_SCH_SERNO", 		JPlateYdUtils.fillSpZrRec(recGetVal, "CT_LOT_SCH_SERNO", 22, 1)						);

				// 이송지시일자 [이송지시일자]
				outRec.setField("FRTOMOVE_ORD_DATE", 		JPlateYdUtils.fillSpZrRec(recGetVal, "FRTOMOVE_ORD_DATE", 8, 1)						);

				// 이송공장구분 [이송공장구분]
				outRec.setField("FRTOMOVE_PLANT_GP", 		JPlateYdUtils.fillSpZrRec(recGetVal, "FRTOMOVE_PLANT_GP", 2, 1)						);

				// 긴급이송작업지시구분 [긴급이송작업지시구분]	--> 긴급재구분으로 사용
			//	outRec.setField("URGENT_FRTOMOVE_WORD_GP", 	JPlateYdUtils.fillSpZrRec(recGetVal, "URGENT_FRTOMOVE_WORD_GP", 1, 1));
				outRec.setField("URGENT_FRTOMOVE_WORD_GP", 	JPlateYdUtils.fillSpZr(szUrgentGp, 1, 1)											);

				// HYSCO운송구분 [HYSCO운송구분]
				outRec.setField("HYSCO_TRANS_CLS", 			JPlateYdUtils.fillSpZrRec(recGetVal, "HYSCO_TRANS_CLS", 1, 1)						);

				// 외관종합판정등급 [외관종합판정등급]
				outRec.setField("APPEAR_GRADE", 			JPlateYdUtils.fillSpZrRec(recGetVal, "APPEAR_GRADE", 1, 1)							);

				// 권취코일냉각방법 [권취코일냉각방법]
				outRec.setField("COOL_METHOD", 				JPlateYdUtils.fillSpZrRec(recGetVal, "COOL_METHOD", 1, 1)							);

				// 냉각완료구분 [냉각완료구분]
				outRec.setField("COOL_DONE_GP", 			JPlateYdUtils.fillSpZrRec(recGetVal, "COOL_DONE_GP", 1, 1)							);

				// 야드Conveyor분기코드
				outRec.setField("CONV_BRANCH_CD", 			JPlateYdUtils.fillSpZrRec(recGetVal, "CONV_BRANCH_CD", 2, 1)						);

				// 고객코드 [고객코드]
				outRec.setField("CUST_CD", 					JPlateYdUtils.fillSpZrRec(recGetVal, "CUST_CD", 6, 1)								);

				szMsg = "RT SPEED 조회 시작";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				// 목적지코드가 비어있으면 목적지코드 필드에 RT SPEED 정보 추가.
				if(ydDaoUtils.paraRecChkNull(recGetVal, "DEST_CD").equals(""))
				{
					rsSResult 	= JDTORecordFactory.getInstance().createRecordSet("ydTemp");
					recPara 	= JDTORecordFactory.getInstance().create();
					
					recPara.setField("STL_NO", szStlNo);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 recPara에 logId 추가 
					recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
									
					intRtnVal	= ydStockDao.getRtSpeedByStlNo(recPara, rsSResult); 
					
					if(intRtnVal<=0){
						szMsg = "재료[" + szStlNo + "] 의 RT SPEED 조회 중 오류발생 : " + szRtnMsg;
		                ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
		                outRec.setField("DEST_CD", 					JPlateYdUtils.fillSpZrRec(recGetVal, "DEST_CD", 5, 1)						);  // 5자리만 채워서 전송
					}
					else{
						rsSResult.first();
						recGetVal2	= rsSResult.getRecord();
						szRtSpeed	= ydDaoUtils.paraRecChkNull(recGetVal2, "RT_SPEED");
						if(!szRtSpeed.equals("") && !szRtSpeed.equals("##1") && !szRtSpeed.equals("##2")){
							outRec.setField("DEST_CD", 	JPlateYdUtils.fillSpZr(szRtSpeed, 5, 1)													);
						}
						else{
							szMsg = "재료[" + szStlNo + "] 의 RT SPEED 조회 중 function exception : " + szRtSpeed + "발생 " + szRtnMsg;
			                ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			                outRec.setField("DEST_CD", 					JPlateYdUtils.fillSpZrRec(recGetVal, "DEST_CD", 5, 1)					); // 5자리만 채워서 전송
						}
					}
				}
				// 목적지 코드 사용중이면, RT SPEED 쓰지 않고, 사용중인 목적지 코드 기입 
				else {
					// 목적지코드 [목적지코드]
					outRec.setField("DEST_CD", 					JPlateYdUtils.fillSpZrRec(recGetVal, "DEST_CD", 5, 1)							);
					szMsg = "DEST_CD[" + ydDaoUtils.paraRecChkNull(recGetVal, "DEST_CD") + "] 가 존재하여 기존 DEST_CD 사용 : " + szRtnMsg;
		            ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				}
				 
				
				// 납기기준일 [납기기준일]
				outRec.setField("DLVRDD_RULE_DD", 			JPlateYdUtils.fillSpZrRec(recGetVal, "DLVRDD_RULE_DD", 8, 1)						);

				// 품명코드 [품명코드]
				outRec.setField("ITEMNAME_CD", 				JPlateYdUtils.fillSpZrRec(recGetVal, "ITEMNAME_CD", 3, 1)							);

				// 종합판정등급 [종합판정등급]
				outRec.setField("OVERALL_STAMP_GRADE", 		JPlateYdUtils.fillSpZrRec(recGetVal, "OVERALL_STAMP_GRADE", 1, 1)					);

				// 수주구분 [수주구분]
				outRec.setField("ORD_GP", 					JPlateYdUtils.fillSpZrRec(recGetVal, "ORD_GP", 1, 1)								);

				// 야드산적LotType [야드산적LotType]
				outRec.setField("YD_STK_LOT_TP", 			JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STK_LOT_TP", 2, 1)							);

				// 야드산적Lot코드 [야드산적Lot코드]
				outRec.setField("YD_STK_LOT_CD", 			JPlateYdUtils.fillSpZrRec(recGetVal, "YD_STK_LOT_CD", 18, 1)						);
				
				//후판 L2 제품번호 
				outRec.setField("PL_L2_TRK_NO", 			JPlateYdUtils.fillSpZr(szPlL2TrkNo, 16, 1)											);

				// RecordSet에 추가
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n======================makeYDY2L002() OUT("+ Integer.toString(ii) + ")==========================\n", JPlateYdConst.DEBUG, logId);
				ydUtils.displayRecord(szOperationName, outRec);
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG, logId);
			}
		}catch(Exception e) {
			szMsg = "Y2(1후판정정야드L2) 송신  저장품제원  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			return -1;
		}

		return outRecSet.size();
	} // end of makeYDY2L002()


	/**
	 * YDY2L004 : 크레인작업지시
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeYDY2L004(JDTORecord inRec, JDTORecordSet outRecSet) {

		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDY2L004
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER  (4)		1667
		//		6.	임시						TEMP					VARCHAR2(29)

		//		7	야드설비ID				YD_EQP_ID				VARCHAR2(6)	        크레인설비 ID
		//		8	야드작업진행상태			YD_WRK_PROG_STAT		VARCHAR2(1)	    "W" 작업지시대기, "1" 권상작업, "3" 권하작업
		//		9	야드스케쥴코드				YD_SCH_CD				VARCHAR2(8)
		//      10. 야드스케쥴명                             YD_SCH_NAME             VARCHAR2(30)
		//		11	야드크레인스케쥴ID			YD_CRN_SCH_ID			VARCHAR2(18)
		//		12	야드크레인작업매수			YD_CRN_WRK_SH			NUMBER  (2)
		//		13	야드크레인작업중량			YD_CRN_WRK_WT			NUMBER  (7)
		//		14	야드크레인작업총두께		YD_CRN_WRK_T			NUMBER  (7,3)
		//		15	야드크레인작업최대폭		YD_CRN_WRK_MAX_W		NUMBER  (5,1)
		//		16	야드크레인작업최대길이		YD_CRN_WRK_MAX_L		NUMBER  (7)
		//		17	야드크레인스케줄잔여회수		YD_CRN_SCH_RMD_CNT		NUMBER  (2)	        권상모음, 권하분리작업 시 크레인 Handling 잔여 회수

		//		33	야드권상지시위치			YD_UP_WO_LOC			VARCHAR2(8)
		//		34	야드권상지시단				YD_UP_WO_LAYER			VARCHAR2(3)
		//		35	야드권상지시X축			YD_UP_WO_LOC_XAXIS		NUMBER  (7)
		//		36	야드권상지시X축오차최대		YD_UP_WO_XAXIS_GAP_MAX	NUMBER  (5)
		//		37	야드권상지시X축오차최소		YD_UP_WO_XAXIS_GAP_MIN	NUMBER  (5)
		//		38	야드권상지시Y축			YD_UP_WO_LOC_YAXIS		NUMBER  (5)

		//		41	야드권상지시Y축오차최대		YD_UP_WO_YAXIS_GAP_MAX	NUMBER  (5)
		//		42	야드권상지시Y축오차최소		YD_UP_WO_YAXIS_GAP_MIN	NUMBER  (5)
		//		43	야드권상지시Z축			YD_UP_WO_LOC_ZAXIS		NUMBER  (5)
		//		44	야드권상지시Z축오차최대		YD_UP_WO_ZAXIS_GAP_MAX	NUMBER  (5)
		//		45	야드권상지시Z축오차최소		YD_UP_WO_ZAXIS_GAP_MIN	NUMBER  (5)
		//		46	야드권하지시위치			YD_DN_WO_LOC			VARCHAR2(8)
		//		47	야드권하지시단				YD_DN_WO_LAYER			VARCHAR2(3)
		//		48	야드권하지시X축			YD_DN_WO_LOC_XAXIS		NUMBER  (7)
		//		49	야드권하지시X축오차최대		YD_DN_WO_XAXIS_GAP_MAX	NUMBER  (5)
		//		50	야드권하지시X축오차최소		YD_DN_WO_XAXIS_GAP_MIN	NUMBER  (5)
		//		51	야드권하지시Y축			YD_DN_WO_LOC_YAXIS		NUMBER  (5)

		//		54	야드권하지시Y축오차최대		YD_DN_WO_YAXIS_GAP_MAX	NUMBER  (5)
		//		55	야드권하지시Y축오차최소		YD_DN_WO_YAXIS_GAP_MIN	NUMBER  (5)
		//		56	야드권하지시Z축			YD_DN_WO_LOC_ZAXIS		NUMBER  (5)
		//		57	야드권하지시Z축오차최대		YD_DN_WO_ZAXIS_GAP_MAX	NUMBER  (5)
		//		58	야드권하지시Z축오차최소		YD_DN_WO_ZAXIS_GAP_MIN	NUMBER  (5)
		//		59	야드설비ID2				YD_EQP_ID2				VARCHAR2(6)	        권상 또는 권하위치가 대차 및 차량인 경우
		//		60	야드대차목적동				YD_TC_AIM_BAY_GP		VARCHAR2(1)
		//		61	야드차량사용구분			YD_CAR_USE_GP			VARCHAR2(1)	        권상 또는 권하위치가 차량인 경우("L" 구내운송차량, "G" 제품출하차량)
		//		62	차량번호					CAR_NO					VARCHAR2(15)	권상 또는 권하위치가 제품출하차량인 경우
		                                                           //CAR_NO 15자리에 각 재료번호 긴급재여부 표기하는용도로 사용 2023.11.09 박종호. 강기석주임 요청  REQ202310501956  
		//		63	운송장비코드				TRN_EQP_CD				VARCHAR2(8)	        권상 또는 권하위치가 구내운송차량인 경우
		//		64	야드설비작업매수			YD_EQP_WRK_SH			NUMBER  (2,0)	대차, 차량스케줄의 설비작업매수
		//		65	야드설비잔량매수			YD_EQP_RMN_SH			NUMBER  (2,0)   대차, 차량스케줄의 설비작업매수 - 크레인 작업이 미완료된 매수
		//		GROUP[15]----------------------------------------------------------------------  STL1 : 최상단 . . . STL20 최하단
		//		66	재료번호					STL_NO1					VARCHAR2(11)
		//		67	파일링코드				YD_PILING_CD1			VARCHAR2(8)
		//		68	야드재료중량				YD_STL_WT1				NUMBER  (5)
		//		69	야드재료두께				YD_STL_T1				NUMBER  (6,3)
		//		70	야드재료폭				YD_STL_W1				NUMBER  (5,1)
		//		71	야드재료길이				YD_STL_L1				NUMBER  (7)
		//		72	권하지시위치				YD_DN_WO_LOC1			VARCHAR2(8)
		//		73	권하지시단				YD_DN_WO_LAYER1			VARCHAR2(3)
		//		GROUP[10]----------------------------------------------------------------------END
		//		186	야드스케쥴코드_Next		YD_SCH_CD_NEXT			VARCHAR2(8)		크레인스케줄에 등록된 다음 작업
		//      187 야드스케쥴명_Next         	YD_SCH_NAME_NEXT        VARCHAR2(30)  2022.01.28 박종호 15개 재료에 대한 각 현공정코드를 여기에 임시 사용하기로함. 소영조차장님과 협의완.
		//		188	야드권상지시위치_Next		YD_UP_WO_LOC_NEXT		VARCHAR2(8)
		//		189	야드권상지시단_Next		YD_UP_WO_LAYER_NEXT		VARCHAR2(3)
		//		190 야드권하지시위치_Next		YD_DN_WO_LOC_NEXT		VARCHAR2(8)
		//		191	야드권하지시단_Next		YD_DN_WO_LAYER_NEXT		VARCHAR2(3)
		//		192	재료번호_Next				STL_NO_NEXT				VARCHAR2(11)
		//		193	야드크레인작업매수_Next		YD_CRN_WRK_SH_NEXT		NUMBER  (2)
		//		194	야드크레인작업중량_Next		YD_CRN_WRK_WT_NEXT		NUMBER  (7)
		//		195	야드크레인작업총두께_Next	YD_CRN_WRK_T_NEXT		NUMBER  (7,3)
		//		196	야드크레인작업최대폭_Next	YD_CRN_WRK_MAX_W_NEXT	NUMBER  (5,1)
		//		197	야드크레인작업최대길이_Next	YD_CRN_WRK_MAX_L_NEXT	NUMBER  (7)
		//-- 2013.04.10 김현우 추가
		//		198	파일링구분				YD_PILING_GP			VARCHAR2(1)		P:파일링, H:횡행작업, N:일반작업, M:멀티, F:강제권상
		//		199	야드스케쥴우선순위			YD_SCH_PRIOR			NUMBER	(2)
		//		GROUP[10]----------------------------------------------------------------------
		//		200	TO야드저장위치1			YD_TO_LOC1				VARCHAR2(8)
		//		201	TO야드적치단1				YD_TO_LAYER1			VARCHAR2(3)
		//		202	TO위치재료적치상태1		YD_TO_STAT1				VARCHAR2(1)		E:가능,C:적치,V:점유,N:불가,U:권상지시,D:권하지시
		//		203	TO위치재료번호1			YD_TO_STL_NO1			VARCHAR2(11)
		//-- 2013.05.20 김현우 추가
		//		GROUP[10]----------------------------------------------------------------------
		//		240	FROM야드저장위치1			YD_FROM_LOC1			VARCHAR2(8)
		//		241	FROM야드적치단1			YD_FROM_LAYER1			VARCHAR2(3)
		//		242	FROM위치재료적치상태1		YD_FROM_STAT1			VARCHAR2(1)		E:가능,C:적치,V:점유,N:불가,U:권상지시,D:권하지시
		//		243	FROM위치재료번호1			YD_FROM_STL_NO1			VARCHAR2(11)
		//		GROUP[10]----------------------------------------------------------------------END
		//-- 2013.07.28 차상국 저장위치명 추가
		//		280 야드권상지시위치명			YD_UP_WO_LOC_NM			VARCHAR2(12)
		//		281	야드권하지시위치명			YD_DN_WO_LOC_NM			VARCHAR2(12)

		// 레코드 선언
		JDTORecord    recPara		= null;
		JDTORecordSet rsCrnSch   	= null;
		JDTORecordSet rsCrnMtl   	= null;
		JDTORecordSet rsRule   		= null;
		JDTORecordSet rsWrkBook		= null;
		JDTORecordSet rsToLoc   	= null;
		JDTORecordSet rsFrLoc   	= null;
		JDTORecordSet rsXyGap		= null;
		JDTORecordSet rsUgntList		= null;

		JDTORecord recCrnSch     	= null;
		JDTORecord recCrnMtl     	= null;
		JDTORecord recRule     		= null;
		JDTORecord recWrkBook    	= null;
		JDTORecord recToLoc     	= null;
		JDTORecord recFrLoc     	= null;
		JDTORecord outRec         	= null;

		// DAO객체 생성
		JPlateYdCrnSchDAO  	ydCrnSchDao  = new JPlateYdCrnSchDAO();
		JPlateYdSchRuleDAO 	ydSchRuleDao = new JPlateYdSchRuleDAO();
		JPlateYdWrkbookDAO 	ydWrkbookDao = new JPlateYdWrkbookDAO();
		JPlateYdStkLyrDAO	ydStkLyrDao  = new JPlateYdStkLyrDAO();

		JPlateYdDaoUtils 	ydDaoUtils   = new JPlateYdDaoUtils();
		JPlateYdUtils 		ydUtils      = new JPlateYdUtils();

    	JPlateYdCommDAO 		commDao 		= new JPlateYdCommDAO();
		
    	String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
    	
		// 변수선언
		String 	szMethodName     	= "makeYDY2L004";
		String 	szMsg            	= "";
		String 	szOperationName  	= "1후판정정야드L2 크레인작업지시";
		String 	szYdSchCd        	= "";
		String 	szYdSchName      	= "";
		String 	szYdSchCdNext   	= "";
		String 	szYdSchNameNext 	= "";
		String 	szYdEqpId2       	= "";
		String 	szCrnSchID       	= "";
		String 	szYdWrkProgStat 	= "";
		String 	szMsgGp          	= "";
		String 	szTemp           	= "";
		String	szYdUpWoLoc			= "";
		String	szYdUpWoLayer		= "";
		String	szYdDnWoLoc			= "";
		String	szYdDnWoLayer		= "";
		String	szydPilingGp		= "";
		String	szYdAidWrkYn		= "";			// 보조작업여부 (Y/N)
		String	szCdContents		= "";

		String	szYD_UP_WO_XAXIS_GAP_MAX	= "";
		String	szYD_UP_WO_XAXIS_GAP_MIN	= "";
		String	szYD_UP_WO_YAXIS_GAP_MAX	= "";
		String	szYD_UP_WO_YAXIS_GAP_MIN	= "";
		String	szYD_DN_WO_XAXIS_GAP_MAX	= "";
		String	szYD_DN_WO_XAXIS_GAP_MIN	= "";
		String	szYD_DN_WO_YAXIS_GAP_MAX	= "";
		String	szYD_DN_WO_YAXIS_GAP_MIN	= "";
		
		String szPL_WR_PRSNT_PROC_CD=""; //재료들의 현공정코드 merge용 String  2022.02.08 박종호

		// 문자열변환 임시변수
		String szConv           	= "";

		int		iYdEqpWrkSh 		= 0;
		int		iYdMtlWt			= 0;
		double	dYdMtlT     		= 0.0;

		// 리턴값
		int 	intRtnVal          	= 0;
		int 	nIdx                = 0;
		int		nToLocCnt			= 0;
		int		nFrLocCnt			= 0;

		// TC Length = 1727 (60 + 1667)
		int 	nTcLen             	= 1667;

		try{
			// Debug MSG
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n=======================makeYDY2L004() IN========================\n", JPlateYdConst.DEBUG);
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG);

			szCrnSchID 		= ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID");
			szYdWrkProgStat = ydDaoUtils.paraRecChkNull(inRec, "YD_WRK_PROG_STAT");
			szMsgGp 		= ydDaoUtils.paraRecChkNull(inRec, "MSG_GP");

			//=======================================================================================================================
			// [1] 크레인스케쥴+크레인작업재료 테이블 조회 (Key: 크레인스케쥴 ID)
			//=======================================================================================================================
			recPara  = JDTORecordFactory.getInstance().create();
			rsCrnSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
			recPara.setField("YD_CRN_SCH_ID", 		szCrnSchID);
			recPara.setField("YD_WRK_PROG_STAT", 	szYdWrkProgStat);
			intRtnVal = ydCrnSchDao.getYdCrnWrkMtlNext(recPara, rsCrnSch);
			if (intRtnVal < 0) {
				szMsg = "크레인스케쥴 테이블 조회오류  (" + szCrnSchID + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return 0;
			} else if (intRtnVal == 0) {
				szMsg = "크레인스케쥴 테이블 조회건수 없음 (" + szCrnSchID + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return 0;
			}

			outRec = JDTORecordFactory.getInstance().create();
			rsCrnSch.first();
			recCrnSch = rsCrnSch.getRecord();

			szYdUpWoLoc   = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");
			szYdUpWoLayer = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LAYER");
			szYdDnWoLoc   = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC");
			szYdDnWoLayer = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LAYER");

			szYdUpWoLoc   = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");
			szYdUpWoLayer = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LAYER");
			szYdDnWoLoc   = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC");
			szYdDnWoLayer = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LAYER");

			// 허용오차 Set
			recPara.setField("YD_UP_WO_LOC", 	szYdUpWoLoc);
			recPara.setField("YD_DN_WO_LOC", 	szYdDnWoLoc);
			rsXyGap = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getStkBedXyGap", logId, szMethodName, "TB_YD_STKBED 의 허용오차 조회");
			
			if (rsXyGap.size() > 0) {
				
				//권상위치 X,Y 좌표 허용오차
				szYD_UP_WO_XAXIS_GAP_MAX = rsXyGap.getRecord(0).getFieldString("UP_X_GAP");
				szYD_UP_WO_XAXIS_GAP_MIN = rsXyGap.getRecord(0).getFieldString("UP_X_GAP");
				szYD_UP_WO_YAXIS_GAP_MAX = rsXyGap.getRecord(0).getFieldString("UP_Y_GAP");
				szYD_UP_WO_YAXIS_GAP_MIN = rsXyGap.getRecord(0).getFieldString("UP_Y_GAP"); 
				
				//권하위치 X,Y 좌표 허용오차
				szYD_DN_WO_XAXIS_GAP_MAX = rsXyGap.getRecord(0).getFieldString("DN_X_GAP"); 
				szYD_DN_WO_XAXIS_GAP_MIN = rsXyGap.getRecord(0).getFieldString("DN_X_GAP");  
				szYD_DN_WO_YAXIS_GAP_MAX = rsXyGap.getRecord(0).getFieldString("DN_Y_GAP");  
				szYD_DN_WO_YAXIS_GAP_MIN = rsXyGap.getRecord(0).getFieldString("DN_Y_GAP");  
				
				if("0".equals(szYD_UP_WO_XAXIS_GAP_MAX)) {
					if(szYdUpWoLoc.startsWith("PF")){
						szYD_UP_WO_XAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapYYdP(szYdUpWoLoc));
					} else {
						szYD_UP_WO_XAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapXYdP(szYdUpWoLoc));
					}
				}
				
				if("0".equals(szYD_UP_WO_XAXIS_GAP_MIN)) {
					if(szYdUpWoLoc.startsWith("PF")){
						szYD_UP_WO_XAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapYYdP(szYdUpWoLoc));
					} else {
						szYD_UP_WO_XAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapXYdP(szYdUpWoLoc));
					}
				}
				
				if("0".equals(szYD_UP_WO_YAXIS_GAP_MAX)) {
					if(szYdUpWoLoc.startsWith("PF")){
						szYD_UP_WO_YAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapXYdP(szYdUpWoLoc));
					} else {
						szYD_UP_WO_YAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapYYdP(szYdUpWoLoc));
					}
				}

				if("0".equals(szYD_UP_WO_YAXIS_GAP_MIN)) {
					if(szYdUpWoLoc.startsWith("PF")){
						szYD_UP_WO_YAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapXYdP(szYdUpWoLoc));
					} else {
						szYD_UP_WO_YAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapYYdP(szYdUpWoLoc));
					}
				}

				if("0".equals(szYD_DN_WO_XAXIS_GAP_MAX)) {
					if(szYdUpWoLoc.startsWith("PF")){
						szYD_DN_WO_XAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapYYdP(szYdDnWoLoc));
					} else {
						szYD_DN_WO_XAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapXYdP(szYdDnWoLoc));
					}
				}
				
				if("0".equals(szYD_DN_WO_XAXIS_GAP_MIN)) {
					if(szYdUpWoLoc.startsWith("PF")){
						szYD_DN_WO_XAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapYYdP(szYdDnWoLoc));
					} else {
						szYD_DN_WO_XAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapXYdP(szYdDnWoLoc));
					}
				}

				if("0".equals(szYD_DN_WO_YAXIS_GAP_MAX)) {
					if(szYdUpWoLoc.startsWith("PF")){
						szYD_DN_WO_YAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapXYdP(szYdDnWoLoc));
					} else {
						szYD_DN_WO_YAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapYYdP(szYdDnWoLoc));
					}
				}

				if("0".equals(szYD_DN_WO_YAXIS_GAP_MIN)) {
					if(szYdUpWoLoc.startsWith("PF")){
						szYD_DN_WO_YAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapXYdP(szYdDnWoLoc));
					} else {
						szYD_DN_WO_YAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapYYdP(szYdDnWoLoc));
					}
				}
				
			} else {
			
				/*
				 * 2016.04.29 윤재광 수정
				 * 허용오차 계산시 A/B동과 F동은 베드적치 방향이 틀리기 때문에 반대로 처리.
				 */
				if(szYdUpWoLoc.startsWith("PF")){
					szYD_UP_WO_XAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapYYdP(szYdUpWoLoc));
					szYD_UP_WO_XAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapYYdP(szYdUpWoLoc));
					szYD_UP_WO_YAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapXYdP(szYdUpWoLoc));
					szYD_UP_WO_YAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapXYdP(szYdUpWoLoc));
	
					szYD_DN_WO_XAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapYYdP(szYdDnWoLoc));
					szYD_DN_WO_XAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapYYdP(szYdDnWoLoc));
					szYD_DN_WO_YAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapXYdP(szYdDnWoLoc));
					szYD_DN_WO_YAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapXYdP(szYdDnWoLoc));
				}else{
					szYD_UP_WO_XAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapXYdP(szYdUpWoLoc));
					szYD_UP_WO_XAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapXYdP(szYdUpWoLoc));
					szYD_UP_WO_YAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapYYdP(szYdUpWoLoc));
					szYD_UP_WO_YAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapYYdP(szYdUpWoLoc));
	
					szYD_DN_WO_XAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapXYdP(szYdDnWoLoc));
					szYD_DN_WO_XAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapXYdP(szYdDnWoLoc));
					szYD_DN_WO_YAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapYYdP(szYdDnWoLoc));
					szYD_DN_WO_YAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapYYdP(szYdDnWoLoc));
				}
			}

/*
			// 허용오차 Set
			szYD_UP_WO_XAXIS_GAP_MAX	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_XAXIS_GAP_MAX", "500");
			szYD_UP_WO_XAXIS_GAP_MIN	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_XAXIS_GAP_MIN", "500");
			szYD_UP_WO_YAXIS_GAP_MAX	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_YAXIS_GAP_MAX", "100");
			szYD_UP_WO_YAXIS_GAP_MIN	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_YAXIS_GAP_MIN", "100");

			szYD_DN_WO_XAXIS_GAP_MAX	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_XAXIS_GAP_MAX", "500");
			szYD_DN_WO_XAXIS_GAP_MIN	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_XAXIS_GAP_MIN", "500");
			szYD_DN_WO_YAXIS_GAP_MAX	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_YAXIS_GAP_MAX", "100");
			szYD_DN_WO_YAXIS_GAP_MIN	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_YAXIS_GAP_MIN", "100");
*/
			// 헤더부
			outRec.setField("MSG_ID", 		"YDY2L004");
			outRec.setField("DATE", 		JPlateYdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME", 		JPlateYdUtils.getCurDate("HH-mm-ss"));

			if ("D".equals(szMsgGp) || "U".equals(szMsgGp)) {
				outRec.setField("MSG_GP", 	JPlateYdUtils.fillSpZr(szMsgGp, 1, 1));
			} else {
				outRec.setField("MSG_GP", 	"I");
			}

			outRec.setField("MSG_LEN", 		JPlateYdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0));
			outRec.setField("TEMP", 		JPlateYdUtils.fillSpZr("", 29, 1));

			// 야드작업진행상태 [야드작업진행상태]
			szTemp = JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_WRK_PROG_STAT", 1, 1);

			// 명령선택 대기상테일때 설비ID CLEAR
			if ("W".equals(szTemp) || "0".equals(szTemp)) {
				outRec.setField("YD_EQP_ID", 		JPlateYdUtils.fillSpZr(" ", 6, 1));
				outRec.setField("YD_WRK_PROG_STAT", JPlateYdUtils.fillSpZr("W", 1, 1));

			} else {
				outRec.setField("YD_EQP_ID", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_ID", 6, 1));
				outRec.setField("YD_WRK_PROG_STAT", JPlateYdUtils.fillSpZr(szTemp, 1, 1));
			}

			// 야드스케쥴코드 [야드스케쥴코드]
			szYdSchCd 		= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD");
			szYdAidWrkYn	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_AID_WRK_YN");
			outRec.setField("YD_SCH_CD", 			JPlateYdUtils.fillSpZr(szYdSchCd, 8, 1));

			//=======================================================================================================================
			// [2] 스케쥴 기준 테이블 조회
			//=======================================================================================================================
			if (!"".equals(szYdSchCd.trim())) {
				recPara = JDTORecordFactory.getInstance().create();
				rsRule  = JDTORecordFactory.getInstance().createRecordSet("");
				recPara.setField("YD_SCH_CD", szYdSchCd);
				intRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsRule);
				if (intRtnVal < 0) {
					szMsg = "스케쥴 기준 테이블 조회오류 (" + szYdSchCd + ")" + "[Ret : " + Integer.toString(intRtnVal) + "]";
					ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

					szYdSchName = JPlateYdUtils.fillSpZrKor(" ", 30, 1);
					szYdEqpId2  = JPlateYdUtils.fillSpZr(" ", 6, 1);
				} else if (intRtnVal == 0) {
					szMsg = "스케쥴 기준 테이블 조회건수 없음 (" + szYdSchCd + ")" + "[Ret : " + Integer.toString(intRtnVal) + "]";
					ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					szYdSchName = JPlateYdUtils.fillSpZrKor(" ", 30, 1);
					szYdEqpId2  = JPlateYdUtils.fillSpZr(" ", 6, 1);
				} else {
					rsRule.first();
					recRule = rsRule.getRecord();

					if ("Y".equals(szYdAidWrkYn)) {
						szCdContents = "[보조]" + ydDaoUtils.paraRecChkNull(recRule, "CD_CONTENTS");
					} else {
						szCdContents = ydDaoUtils.paraRecChkNull(recRule, "CD_CONTENTS");
					}
					szYdSchName = JPlateYdUtils.fillSpZrKor(szCdContents, 30, 1);
					szYdEqpId2  = JPlateYdUtils.fillSpZrRec(recRule, "YD_ALT_CRN", 6, 1);
				}
			} else {
				szMsg = "스케쥴 코드가 없어서 기준테이블을 조회 안함 : YD_SCH_NAME, YD_EQP_ID2는 공백";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				szYdSchName = JPlateYdUtils.fillSpZrKor(" ", 30, 1);
				szYdEqpId2  = JPlateYdUtils.fillSpZr(" ", 6, 1);
			}

			// 야드스케쥴명
			outRec.setField("YD_SCH_NAME", 				szYdSchName);

			// 야드크레인스케쥴ID [야드크레인스케쥴ID]
			outRec.setField("YD_CRN_SCH_ID", 			JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_CRN_SCH_ID", 18, 1));

			// 야드크레인작업매수 [야드크레인작업매수]
			outRec.setField("YD_CRN_WRK_SH", 			JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_SH", 2, 0));

			// 야드크레인작업중량 [야드크레인작업중량]
			outRec.setField("YD_CRN_WRK_WT", 			JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_WT", 7, 0));

			// 야드크레인작업총두께 [야드크레인작업총두께]
			szConv = JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_T", 8, 1);
			outRec.setField("YD_CRN_WRK_T", 			ydUtils.floatLRPAD(szConv, 7, 3, '0'));

			// 야드크레인작업최대폭 [야드크레인작업최대폭]
			szConv = JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_MAX_W", 6, 1);
			outRec.setField("YD_CRN_WRK_MAX_W", 		ydUtils.floatLRPAD(szConv, 5, 1, '0'));

			// 야드크레인작업최대길이 [야드크레인작업최대길이]
			outRec.setField("YD_CRN_WRK_MAX_L", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_MAX_L", 7, 0));

			//*************************************************************************************************************************************
			// 야드크레인스케줄잔여회수 [야드크레인스케줄잔여회수]
			outRec.setField("YD_CRN_SCH_RMD_CNT", 		JPlateYdUtils.fillSpZr("00", 2, 1));
			//*************************************************************************************************************************************

			// 사용안하는 항목 ..
			outRec.setField("YD_CRN_GRAB_GP", 				JPlateYdUtils.fillSpZr("D", 1, 1));
			outRec.setField("YD_CRN_GRAB_USE_GP", 			JPlateYdUtils.fillSpZr("1", 1, 1));
			outRec.setField("YD_CRN_TT1_GRAB_NEW_AXIS_L", 	JPlateYdUtils.fillSpZr("0", 4, 0));
			outRec.setField("YD_CRN_TT2_GRAB_NEW_AXIS_L",	JPlateYdUtils.fillSpZr("0", 4, 0));
			outRec.setField("YD_CRN_MGNT_USE_EA", 			JPlateYdUtils.fillSpZr("0", 2, 0));
			outRec.setField("YD_CRN_MGNT_USE_YN1", 			JPlateYdUtils.fillSpZr("Y", 1, 1));
			outRec.setField("YD_CRN_MGNT_USE_YN2", 			JPlateYdUtils.fillSpZr("Y", 1, 1));
			outRec.setField("YD_CRN_MGNT_USE_YN3", 			JPlateYdUtils.fillSpZr("Y", 1, 1));
			outRec.setField("YD_CRN_MGNT_USE_YN4", 			JPlateYdUtils.fillSpZr("Y", 1, 1));
			outRec.setField("YD_CRN_MGNT_USE_YN5", 			JPlateYdUtils.fillSpZr("Y", 1, 1));
			outRec.setField("YD_CRN_MGNT_USE_YN6", 			JPlateYdUtils.fillSpZr("Y", 1, 1));
			outRec.setField("YD_CRN_MGNT_USE_YN7", 			JPlateYdUtils.fillSpZr("Y", 1, 1));
			outRec.setField("YD_CRN_MGNT_USE_YN8", 			JPlateYdUtils.fillSpZr("Y", 1, 1));
			outRec.setField("YD_CRN_MGNT_USE_YN9", 			JPlateYdUtils.fillSpZr("Y", 1, 1));
			outRec.setField("YD_CRN_MGNT_USE_YN10", 		JPlateYdUtils.fillSpZr("Y", 1, 1));

			// 야드권상지시위치 [야드권상지시위치]
			outRec.setField("YD_UP_WO_LOC", 			JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LOC", 8, 1));

			// 야드권상지시단 [야드권상지시단]
			outRec.setField("YD_UP_WO_LAYER", 			JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LAYER", 3, 1));

			// 야드권상지시X축[야드권상지시X축]
			outRec.setField("YD_UP_WO_LOC_XAXIS", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LOC_XAXIS", 7, 0));

			// 야드권상지시X축오차최대 [야드권상지시X축오차최대]
			outRec.setField("YD_UP_WO_XAXIS_GAP_MAX", 	JPlateYdUtils.fillSpZr(szYD_UP_WO_XAXIS_GAP_MAX, 5, 0));
			// 야드권상지시X축오차최소 [야드권상지시X축오차최소]
			outRec.setField("YD_UP_WO_XAXIS_GAP_MIN", 	JPlateYdUtils.fillSpZr(szYD_UP_WO_XAXIS_GAP_MIN, 5, 0));

			// 야드권상지시Y축 [야드권상지시Y축]
			outRec.setField("YD_UP_WO_LOC_YAXIS", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LOC_YAXIS", 5, 0));
		//	outRec.setField("YD_UP_WO_LOC_YAXIS1", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LOC_YAXIS", 5, 0));
		//	outRec.setField("YD_UP_WO_LOC_YAXIS2", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LOC_YAXIS", 5, 0));
			outRec.setField("YD_UP_WO_LOC_YAXIS1", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LOC_YAXIS1", 5, 0));
			outRec.setField("YD_UP_WO_LOC_YAXIS2", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LOC_YAXIS2", 5, 0));

			// 야드권상지시Y축오차최대 [야드권상지시Y축오차최대]
			outRec.setField("YD_UP_WO_YAXIS_GAP_MAX", 	JPlateYdUtils.fillSpZr(szYD_UP_WO_YAXIS_GAP_MAX, 5, 0));
			// 야드권상지시Y축오차최소 [야드권상지시Y축오차최소]
			outRec.setField("YD_UP_WO_YAXIS_GAP_MIN", 	JPlateYdUtils.fillSpZr(szYD_UP_WO_YAXIS_GAP_MIN, 5, 0));

			// 야드권상지시Z축 [야드권상지시Z축]
			outRec.setField("YD_UP_WO_LOC_ZAXIS", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LOC_ZAXIS", 5, 0));

			// 야드권상지시Z축오차최대 [야드권상지시Z축오차최대]
			outRec.setField("YD_UP_WO_ZAXIS_GAP_MAX", 	JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_ZAXIS_GAP_MAX", 5, 0));

			// 야드권상지시Z축오차최소 [야드권상지시Z축오차최소]
			outRec.setField("YD_UP_WO_ZAXIS_GAP_MIN", 	JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_ZAXIS_GAP_MIN", 5, 0));

			// 야드권하지시위치 [야드권하지시위치]
			outRec.setField("YD_DN_WO_LOC", 			JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LOC", 8, 1));

			// 야드권하지시단 [야드권하지시단]
			outRec.setField("YD_DN_WO_LAYER", 			JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LAYER", 3, 1));

			// 야드권하지시X축 [야드권하지시X축]
			outRec.setField("YD_DN_WO_LOC_XAXIS", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LOC_XAXIS", 7, 0));

			// 야드권하지시X축오차최대 [야드권하지시X축오차최대]
			outRec.setField("YD_DN_WO_XAXIS_GAP_MAX", 	JPlateYdUtils.fillSpZr(szYD_DN_WO_XAXIS_GAP_MAX, 5, 0));
			// 야드권하지시X축오차최소 [야드권하지시X축오차최소]
			outRec.setField("YD_DN_WO_XAXIS_GAP_MIN", 	JPlateYdUtils.fillSpZr(szYD_DN_WO_XAXIS_GAP_MIN, 5, 0));

			// 야드권하지시Y축 [야드권하지시Y축]
			outRec.setField("YD_DN_WO_LOC_YAXIS", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LOC_YAXIS", 5, 0));
		//	outRec.setField("YD_DN_WO_LOC_YAXIS1", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LOC_YAXIS", 5, 0));
		//	outRec.setField("YD_DN_WO_LOC_YAXIS2", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LOC_YAXIS", 5, 0));
			outRec.setField("YD_DN_WO_LOC_YAXIS1", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LOC_YAXIS1", 5, 0));
			outRec.setField("YD_DN_WO_LOC_YAXIS2", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LOC_YAXIS2", 5, 0));

			// 야드권하지시Y축오차최대 [야드권하지시Y축오차최대]
			outRec.setField("YD_DN_WO_YAXIS_GAP_MAX", 	JPlateYdUtils.fillSpZr(szYD_DN_WO_YAXIS_GAP_MAX, 5, 0));
			// 야드권하지시Y축오차최소 [야드권하지시Y축오차최소]
			outRec.setField("YD_DN_WO_YAXIS_GAP_MIN", 	JPlateYdUtils.fillSpZr(szYD_DN_WO_YAXIS_GAP_MIN, 5, 0));

			// 야드권하지시Z축 [야드권하지시Z축]
			outRec.setField("YD_DN_WO_LOC_ZAXIS", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LOC_ZAXIS", 5, 0));

			// 야드권하지시Z축오차최대 [야드권하지시Z축오차최대]
			outRec.setField("YD_DN_WO_ZAXIS_GAP_MAX", 	JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_ZAXIS_GAP_MAX", 5, 0));
			// 야드권하지시Z축오차최소 [야드권하지시Z축오차최소]
			outRec.setField("YD_DN_WO_ZAXIS_GAP_MIN", 	JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_ZAXIS_GAP_MIN", 5, 0));

			// 야드설비ID2 [야드설비ID2]
			outRec.setField("YD_EQP_ID2", 				szYdEqpId2);

			//=======================================================================================================================
			// [3.1] 작업예약 조회
			//=======================================================================================================================
			recPara   = JDTORecordFactory.getInstance().create();
			rsWrkBook = JDTORecordFactory.getInstance().createRecordSet("yd");
			recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID"));
			intRtnVal = ydWrkbookDao.getYdWrkbookMtl(recPara, rsWrkBook);

			szMsg = "작업예약 테이블 조회 [Ret : " + Integer.toString(intRtnVal) + "]";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			
			
			//=======================================================================================================================
			// [3.1.2] 긴급재 리스트 조회
			//=======================================================================================================================
			rsUgntList = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getUgntGpList", logId, szMethodName, "긴급재리스트 조회");
			String UGNT_LIST="";  //YYNNYYNN...  재료번호에 따른 15자리 긴급재 FLAG
			if(rsUgntList.size()>0){
				UGNT_LIST=rsUgntList.getRecord(0).getFieldString("YD_UGNT_LIST");  
			}
			/*
			SELECT YD_UGNT_GP1||YD_UGNT_GP2||YD_UGNT_GP3||YD_UGNT_GP4||YD_UGNT_GP5||YD_UGNT_GP6||YD_UGNT_GP7||YD_UGNT_GP8||YD_UGNT_GP9||YD_UGNT_GP10||YD_UGNT_GP11||YD_UGNT_GP12||YD_UGNT_GP13||YD_UGNT_GP14||YD_UGNT_GP15 AS YD_UGNT_LIST
			FROM
			(
				SELECT MAX(YD_UGNT_GP1) AS YD_UGNT_GP1, MAX(YD_UGNT_GP2) AS YD_UGNT_GP2, MAX(YD_UGNT_GP3) AS YD_UGNT_GP3, MAX(YD_UGNT_GP4) AS YD_UGNT_GP4, MAX(YD_UGNT_GP5) AS YD_UGNT_GP5,
				       MAX(YD_UGNT_GP6) AS YD_UGNT_GP6, MAX(YD_UGNT_GP7) AS YD_UGNT_GP7, MAX(YD_UGNT_GP8) AS YD_UGNT_GP8, MAX(YD_UGNT_GP9) AS YD_UGNT_GP9, MAX(YD_UGNT_GP10) AS YD_UGNT_GP10,
				       MAX(YD_UGNT_GP11) AS YD_UGNT_GP11, MAX(YD_UGNT_GP12) AS YD_UGNT_GP12, MAX(YD_UGNT_GP13) AS YD_UGNT_GP13, MAX(YD_UGNT_GP14) AS YD_UGNT_GP14, MAX(YD_UGNT_GP15) AS YD_UGNT_GP15
				FROM
				(
					SELECT NVL(DECODE(ROWNUM,1,YD_UGNT_GP,''),'N') AS YD_UGNT_GP1, NVL(DECODE(ROWNUM,2,YD_UGNT_GP,''),'N') AS YD_UGNT_GP2, NVL(DECODE(ROWNUM,3,YD_UGNT_GP,''),'N') AS YD_UGNT_GP3, NVL(DECODE(ROWNUM,4,YD_UGNT_GP,''),'N') AS YD_UGNT_GP4, NVL(DECODE(ROWNUM,5,YD_UGNT_GP,''),'N') AS YD_UGNT_GP5,       
					       NVL(DECODE(ROWNUM,6,YD_UGNT_GP,''),'N') AS YD_UGNT_GP6, NVL(DECODE(ROWNUM,7,YD_UGNT_GP,''),'N') AS YD_UGNT_GP7, NVL(DECODE(ROWNUM,8,YD_UGNT_GP,''),'N') AS YD_UGNT_GP8, NVL(DECODE(ROWNUM,9,YD_UGNT_GP,''),'N') AS YD_UGNT_GP9, NVL(DECODE(ROWNUM,10,YD_UGNT_GP,''),'N') AS YD_UGNT_GP10,
					       NVL(DECODE(ROWNUM,11,YD_UGNT_GP,''),'N') AS YD_UGNT_GP11, NVL(DECODE(ROWNUM,12,YD_UGNT_GP,''),'N') AS YD_UGNT_GP12, NVL(DECODE(ROWNUM,13,YD_UGNT_GP,''),'N') AS YD_UGNT_GP13, NVL(DECODE(ROWNUM,14,YD_UGNT_GP,''),'N') AS YD_UGNT_GP14, NVL(DECODE(ROWNUM,15,YD_UGNT_GP,''),'N') AS YD_UGNT_GP15	       
					FROM
					(
					SELECT B.STL_NO, 
					       -- 권하지시위치
					       CASE WHEN A.YD_EQP_WRK_SH > 1 AND B.YD_STK_LOT_TP IS NOT NULL AND SUBSTR(A.YD_DN_WO_LOC,1,2) <> 'XX'
					               THEN NVL((SELECT MAX(S.YD_STK_COL_GP || S.YD_STK_BED_NO)
					                           FROM TB_YD_STKLYR S
					                          WHERE S.STL_NO = B.STL_NO
					                            AND S.YD_STK_LYR_MTL_STAT = 'D'
					                            AND S.DEL_YN = 'N'
					                        ), (SUBSTR(A.YD_DN_WO_LOC,1,6) || B.YD_STK_LOT_TP))
					               ELSE A.YD_DN_WO_LOC
					           END AS YD_DN_WO_LOC,
					      -- 권하지시단
					       CASE WHEN A.YD_EQP_WRK_SH > 1 AND B.YD_STK_LOT_TP IS NOT NULL AND SUBSTR(A.YD_DN_WO_LOC,1,2) <> 'XX'
					               THEN NVL((SELECT MAX(S.YD_STK_LYR_NO)
					                           FROM TB_YD_STKLYR S
					                          WHERE S.STL_NO = B.STL_NO
					                            AND S.YD_STK_LYR_MTL_STAT = 'D'
					                            AND S.DEL_YN = 'N'
					                        ), B.YD_STK_LYR_NO)
					               ELSE A.YD_DN_WO_LAYER
					           END AS YD_DN_WO_LAYER,
					      DECODE(ORD_YEOJAE_GP,'1',YD_UGNT_GP,'N') AS YD_UGNT_GP     
					FROM TB_YD_CRNSCH A,
					     (SELECT Y.YD_CRN_SCH_ID
					           , Y.STL_NO
					           , Y.YD_STK_LYR_NO
					           , Y.YD_STK_LOT_TP
					           , X.YD_UGNT_GP
					           , Z.ORD_YEOJAE_GP
					      FROM TB_YD_SHRSTOCK  X
					         , TB_YD_CRNWRKMTL Y
					         , VW_YD_SHRSTOCK_URGENT_NO Z
					      WHERE X.STL_NO = Y.STL_NO
					      AND X.STL_NO =Z.STL_NO
					     ) B
					WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
					AND  A.YD_CRN_SCH_ID =:V_YD_CRN_SCH_ID--'202311010240155453'--'202303031038187858'
					ORDER BY YD_DN_WO_LAYER DESC, YD_DN_WO_LOC, STL_NO
					) A
				) AA
			) AAA
			*/
			
			if (intRtnVal < 0) {
				szMsg = "작업예약 테이블 조회오류 [Ret : " + Integer.toString(intRtnVal) + "]";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

				// 야드대차목적동[야드대차목적동]
				outRec.setField("YD_TC_AIM_BAY_GP", 	JPlateYdUtils.fillSpZr(" ", 1, 1));

				// 야드차량사용구분 [야드차량사용구분]
				outRec.setField("YD_CAR_USE_GP", 		JPlateYdUtils.fillSpZr(" ", 1, 1));

				// 차량번호 [차량번호]
				//outRec.setField("CAR_NO", 				JPlateYdUtils.fillSpZrKor(" ", 15, 1));
				outRec.setField("CAR_NO", 				JPlateYdUtils.fillSpZr(UGNT_LIST, 15, 1));  //차량번호15자리에 재료별 긴급재 FLAG 사용

				// 운송장비코드 [운송장비코드]
				outRec.setField("TRN_EQP_CD", 			JPlateYdUtils.fillSpZr(" ", 8, 1));
			} else if (intRtnVal == 0) {
				szMsg = "작업예약 테이블 조회건수 없음 [Ret : " + Integer.toString(intRtnVal) + "]";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				// 야드대차목적동[야드대차목적동]
				outRec.setField("YD_TC_AIM_BAY_GP", 	JPlateYdUtils.fillSpZr(" ", 1, 1));

				// 야드차량사용구분 [야드차량사용구분]
				outRec.setField("YD_CAR_USE_GP", 		JPlateYdUtils.fillSpZr(" ", 1, 1));

				// 차량번호 [차량번호]
				//outRec.setField("CAR_NO", 				JPlateYdUtils.fillSpZrKor(" ", 15, 1));
				outRec.setField("CAR_NO", 				JPlateYdUtils.fillSpZr(UGNT_LIST, 15, 1));  //차량번호15자리에 재료별 긴급재 FLAG 사용

				// 운송장비코드 [운송장비코드]
				outRec.setField("TRN_EQP_CD", 			JPlateYdUtils.fillSpZr(" ", 8, 1));
			} else {
				rsWrkBook.first();
				recWrkBook = rsWrkBook.getRecord();

				// 야드대차목적동[야드대차목적동]
				outRec.setField("YD_TC_AIM_BAY_GP", 	JPlateYdUtils.fillSpZrRec(recWrkBook, "YD_AIM_BAY_GP", 1, 1));

				// 야드차량사용구분 [야드차량사용구분]
				outRec.setField("YD_CAR_USE_GP", 		JPlateYdUtils.fillSpZrRec(recWrkBook, "YD_CAR_USE_GP", 1, 1));

				// 차량번호 [차량번호]
				//outRec.setField("CAR_NO", 				JPlateYdUtils.fillSpZrKor(ydDaoUtils.paraRecChkNull(recWrkBook, "CAR_NO"), 15, 1));
				outRec.setField("CAR_NO", 				JPlateYdUtils.fillSpZr(UGNT_LIST, 15, 1));  //차량번호15자리에 재료별 긴급재 FLAG 사용

				// 운송장비코드 [운송장비코드]
				outRec.setField("TRN_EQP_CD", 			JPlateYdUtils.fillSpZrRec(recWrkBook, "TRN_EQP_CD", 8, 1));
			}

			// 야드설비작업매수 [야드설비작업매수]
			outRec.setField("YD_EQP_WRK_SH", 			JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_SH", 2, 0));

			//*************************************************************************************************************************************
			// 야드설비잔량매수[야드설비잔량매수]
			outRec.setField("YD_EQP_RMN_SH", 			JPlateYdUtils.fillSpZr("0", 2, 0));
			//*************************************************************************************************************************************

			//=======================================================================================================================
			// [3.2] 크레인작업재료 조회
			//=======================================================================================================================
			recPara  = JDTORecordFactory.getInstance().create();
			rsCrnMtl = JDTORecordFactory.getInstance().createRecordSet("yd");
			recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID"));
			intRtnVal = ydCrnSchDao.getYdCrnWrkMtlDesc(recPara, rsCrnMtl);				// 적치단 역순으로 조회

			szMsg = "크레인작업재료 조회 .... 조회완료 >>>> 건수 ::" + intRtnVal;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			intRtnVal = (intRtnVal >= 15) ? 15 : intRtnVal;

			for(nIdx=0; nIdx<intRtnVal; nIdx++) {
				recCrnMtl = rsCrnMtl.getRecord(nIdx);

				// 재료번호 [재료번호]
				outRec.setField("STL_NO"+Integer.toString(1+nIdx), 			JPlateYdUtils.fillSpZrRec(recCrnMtl, "STL_NO", 11, 1));

				// 파일링코드
				outRec.setField("YD_PILING_CD"+Integer.toString(1+nIdx),	JPlateYdUtils.fillSpZrRec(recCrnMtl, "YD_PILING_CD", 8, 1));

				// 야드재료중량 [야드재료중량]
				outRec.setField("YD_STL_WT"+Integer.toString(1+nIdx), 		JPlateYdUtils.fillSpZrRec(recCrnMtl, "YD_MTL_WT", 5, 0));

				// 야드재료두께 [야드재료두께]
				szConv = JPlateYdUtils.fillSpZrRec(recCrnMtl, "YD_MTL_T", 7, 1);
				outRec.setField("YD_STL_T"+Integer.toString(1+nIdx), 		ydUtils.floatLRPAD(szConv, 6, 3, '0'));

				// 야드재료폭 [야드재료폭]
				szConv = JPlateYdUtils.fillSpZrRec(recCrnMtl, "YD_MTL_W", 6, 1);
				outRec.setField("YD_STL_W"+Integer.toString(1+nIdx), 		ydUtils.floatLRPAD(szConv, 5, 1, '0'));

				// 야드재료길이 [야드재료길이]
				outRec.setField("YD_STL_L"+Integer.toString(1+nIdx), 		JPlateYdUtils.fillSpZrRec(recCrnMtl, "YD_MTL_L", 7, 0));

				// 권하지시위치 [야드적치열+베드]
				outRec.setField("YD_DN_WO_LOC"+Integer.toString(1+nIdx), 	JPlateYdUtils.fillSpZrRec(recCrnMtl, "YD_DN_WO_LOC", 8, 1));

				// 권하지시단 [야드적치단]
				outRec.setField("YD_DN_WO_LAYER"+Integer.toString(1+nIdx),	JPlateYdUtils.fillSpZrRec(recCrnMtl, "YD_DN_WO_LAYER", 3, 1));

				// 재료 매수 두께, 중량  합계산 - 강제권상시 재료합과 상이한 경우 발생 .. 안정화후 삭제 필요
				iYdEqpWrkSh ++;
				iYdMtlWt	= iYdMtlWt + ydDaoUtils.paraRecChkNullInt(recCrnMtl,    "YD_MTL_WT");
				dYdMtlT     = dYdMtlT  + ydDaoUtils.paraRecChkNullDouble(recCrnMtl, "YD_MTL_T");
				
				szPL_WR_PRSNT_PROC_CD=szPL_WR_PRSNT_PROC_CD+JPlateYdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recCrnMtl, "PL_WR_PRSNT_PROC_CD"),2,1);
			}

			szMsg = "재료정보 .... 합계 >>>> 매수 :: " + iYdEqpWrkSh + "두께 :: " + dYdMtlT + "중량 :: " + iYdMtlWt;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			// ---- 재료별로 누적하여 다시 SET함
			// 야드크레인작업매수 [야드크레인작업매수]
			outRec.setField("YD_CRN_WRK_SH", 			JPlateYdUtils.fillSpZr(String.valueOf(iYdEqpWrkSh), 2, 0));
			// 야드크레인작업중량 [야드크레인작업중량]
			outRec.setField("YD_CRN_WRK_WT", 			JPlateYdUtils.fillSpZr(String.valueOf(iYdMtlWt), 7, 0));
			// 야드크레인작업총두께 [야드크레인작업총두께]
			// 2013.08.12 윤재광 수정 >>>> 2013.09.09 보완 완료
			// 항목에 null로 들어가서 막음(예: dYdMtlT값이 = 38.09999999994 이렇게 찍힘)
			outRec.setField("YD_CRN_WRK_T", 			ydUtils.floatLRPAD(String.valueOf(dYdMtlT), 7, 3, '0'));

			// 공백 건수처리
			for(int ii=nIdx; ii<15; ii++) {
				// 재료번호 [재료번호]
				outRec.setField("STL_NO"+Integer.toString(1+ii), 			JPlateYdUtils.fillSpZr(" ", 11, 1));

				// 파일링코드
				outRec.setField("YD_PILING_CD"+Integer.toString(1+ii),		JPlateYdUtils.fillSpZr(" ", 8, 1));

				// 야드재료중량 [야드재료중량]
				outRec.setField("YD_STL_WT"+Integer.toString(1+ii), 		JPlateYdUtils.fillSpZr(" ", 5, 1));

				// 야드재료두께 [야드재료두께]
				outRec.setField("YD_STL_T"+Integer.toString(1+ii), 			JPlateYdUtils.fillSpZr(" ", 6, 1));

				// 야드재료폭 [야드재료폭]
				outRec.setField("YD_STL_W"+Integer.toString(1+ii), 			JPlateYdUtils.fillSpZr(" ", 5, 1));

				// 야드재료길이 [야드재료길이]
				outRec.setField("YD_STL_L"+Integer.toString(1+ii), 			JPlateYdUtils.fillSpZr(" ", 7, 1));

				// 권하지시위치 [야드적치열+베드]
				outRec.setField("YD_DN_WO_LOC"+Integer.toString(1+ii), 		JPlateYdUtils.fillSpZr(" ", 8, 1));

				// 권하지시단 [야드적치단]
				outRec.setField("YD_DN_WO_LAYER"+Integer.toString(1+ii),	JPlateYdUtils.fillSpZr(" ", 3, 1));

			}

			rsCrnSch.first();
			recCrnSch = rsCrnSch.getRecord();

			// 야드스케쥴코드_Next [야드스케쥴코드_Next]
			szYdSchCdNext = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD_NEXT");
			outRec.setField("YD_SCH_CD_NEXT", JPlateYdUtils.fillSpZr(szYdSchCdNext, 8, 1));

			//=======================================================================================================================
			// [4] 스케쥴 기준 테이블 조회
			//=======================================================================================================================
			/*  SCH_NEXT 미사용: 해당 필드에 각 재료별 현공정코드 MERGE값 담기로함.
			if (!"".equals(szYdSchCdNext.trim())) {
				recPara = JDTORecordFactory.getInstance().create();
				rsRule  = JDTORecordFactory.getInstance().createRecordSet("");
				recPara.setField("YD_SCH_CD", szYdSchCdNext);
				intRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsRule);
				if (intRtnVal < 0) {
					szMsg = "스케쥴 기준 테이블_NEXT 조회오류 (" + szYdSchCdNext + ")" + "[Ret : " + Integer.toString(intRtnVal) + "]";
					ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

					szYdSchNameNext = JPlateYdUtils.fillSpZrKor(" ", 30, 1);
				} else if (intRtnVal == 0) {
					szMsg = "스케쥴 기준 테이블_NEXT 조회건수 없음 (" + szYdSchCdNext + ")" + "[Ret : " + Integer.toString(intRtnVal) + "]";
					ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					szYdSchNameNext = JPlateYdUtils.fillSpZrKor(" ", 30, 1);
				} else {
					rsRule.first();
					recRule = rsRule.getRecord();

					szYdSchNameNext = JPlateYdUtils.fillSpZrKor(ydDaoUtils.paraRecChkNull(recRule, "CD_CONTENTS"), 30, 1);
				}
			} else {
				szMsg = "스케쥴 코드_NEXT가 없어서 기준테이블을 조회 안함 : YD_SCH_NAME_NEXT는 공백";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				szYdSchNameNext = JPlateYdUtils.fillSpZrKor(" ", 30, 1);
			}
			*/
			
			//재료별 현 공정코드 쿼리
			/*
			 * SELECT  PL_WR_PRSNT_PROC_CD
				FROM TB_PR_PLATE_MAT
				WHERE PL_PLATE_NO = 'FC44004001'
			 * 
			 */
			// 야드스케쥴명_Next [야드스케쥴명_Next]
			//outRec.setField("YD_SCH_NAME_NEXT", 		szYdSchNameNext);  
			outRec.setField("YD_SCH_NAME_NEXT", 		JPlateYdUtils.fillSpZr(szPL_WR_PRSNT_PROC_CD, 30, 1));  //2022.01.28 박종호 NEXT 스케줄명에 15개 재료 현공정코드 정보 담기로함. 소영조 차장 협의완.
			
			// 야드권상지시위치_Next [야드권상지시위치_Next]
			outRec.setField("YD_UP_WO_LOC_NEXT", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LOC_NEXT", 8, 1));

			// 야드권상지시단_Next [야드권상지시단_Next]
			outRec.setField("YD_UP_WO_LAYER_NEXT", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LAYER_NEXT", 3, 1));

			// 야드권하지시위치_Next [야드권하지시위치_Next]
			outRec.setField("YD_DN_WO_LOC_NEXT", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LOC_NEXT", 8, 1));

			// 야드권하지시단_Next [야드권하지시단_Next]
			outRec.setField("YD_DN_WO_LAYER_NEXT", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LAYER_NEXT", 3, 1));

			// 재료번호_Next [재료번호_Next]
			outRec.setField("STL_NO_NEXT", 				JPlateYdUtils.fillSpZrRec(recCrnSch, "STL_NO_NEXT", 11, 1));

			// 야드크레인작업매수_Next [야드설비작업매수]
			outRec.setField("YD_CRN_WRK_SH_NEXT", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_SH_NEXT", 2, 9));

			// 야드크레인작업중량_Next [야드크레인작업중량_Next]
			outRec.setField("YD_CRN_WRK_WT_NEXT", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_WT_NEXT", 7, 9));

			// 야드크레인작업총두께_Next [야드크레인작업총두께_Next]
			szConv = JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_T_NEXT", 8, 1);
			szConv = ydUtils.floatLRPAD(szConv, 7, 3, '0');
			if ("0000000".equals(szConv)) {
				outRec.setField("YD_CRN_WRK_T_NEXT", 	JPlateYdUtils.fillSpZr("", 7, 1));
			} else {
				outRec.setField("YD_CRN_WRK_T_NEXT", 	szConv);
			}

			// 야드크레인작업최대폭_Next [야드크레인작업최대폭_Next]
			szConv = JPlateYdUtils.fillSpZrRec(recCrnSch, 	"YD_EQP_WRK_MAX_W_NEXT", 6, 1);
			szConv = ydUtils.floatLRPAD(szConv, 5, 1, '0');
			if ("00000".equals(szConv)) {
				outRec.setField("YD_CRN_WRK_MAX_W_NEXT", 	JPlateYdUtils.fillSpZr("", 5, 1));
			} else {
				outRec.setField("YD_CRN_WRK_MAX_W_NEXT", 	szConv);
			}

			// 야드크레인작업최대길이_Next [야드크레인작업최대길이_Next]
			outRec.setField("YD_CRN_WRK_MAX_L_NEXT", 	JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_MAX_L_NEXT", 7, 9));

			//	198	파일링구분 		[YD_PILING_GP] P:파일링, H:횡행작업, N:일반작업, M:멀티, F:강제권상
			szydPilingGp = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WRK_ACT_GP", "N");
			if ("FU".equals(ydUtils.substr(szYdSchCd, 2, 2))) {
				szydPilingGp = "F";
			}
			outRec.setField("YD_PILING_GP", 			JPlateYdUtils.fillSpZr(szydPilingGp, 1, 1));
			//	199	야드스케쥴우선순위 	[YD_SCH_PRIOR]
			outRec.setField("YD_SCH_PRIOR", 			JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_SCH_PRIOR", 2, 0));

			// TO야드저장위치 정보 SET :: YD_TO_LOC1, YD_TO_LAYER1, YD_TO_STAT1, YD_TO_STL_NO1
			recPara = JDTORecordFactory.getInstance().create();
			rsToLoc = JDTORecordFactory.getInstance().createRecordSet("Temp");

			recPara.setField("YD_STK_COL_GP", ydUtils.substr(szYdDnWoLoc, 0, 6));
			recPara.setField("YD_STK_LYR_NO", szYdDnWoLayer);

			intRtnVal = ydStkLyrDao.getToLocInfo(recPara, rsToLoc);
			nToLocCnt = (intRtnVal >= 10) ? 10 : intRtnVal;

			for(int ii=0; ii<nToLocCnt; ii++) {
				rsToLoc.absolute(ii+1);
				recToLoc = rsToLoc.getRecord();

				//	200	TO야드저장위치		[YD_TO_LOC]		VARCHAR2(8)
				outRec.setField("YD_TO_LOC"+(ii+1), 	JPlateYdUtils.fillSpZrRec(recToLoc, "YD_TO_LOC", 8, 1));

				//	201	TO야드적치단		[YD_TO_LAYER]	VARCHAR2(3)
				outRec.setField("YD_TO_LAYER"+(ii+1), 	JPlateYdUtils.fillSpZrRec(recToLoc, "YD_TO_LAYER", 3, 0));

				//	202	TO위치재료적치상태	[YD_TO_STAT]	VARCHAR2(1)		E:가능,C:적치,V:점유,N:불가
				outRec.setField("YD_TO_STAT"+(ii+1), 	JPlateYdUtils.fillSpZrRec(recToLoc, "YD_TO_STAT", 1, 1));

				//	203	TO위치재료번호		[YD_TO_STL_NO]	VARCHAR2(11)
				outRec.setField("YD_TO_STL_NO"+(ii+1),	JPlateYdUtils.fillSpZrRec(recToLoc, "YD_TO_STL_NO", 11, 1));
			}

			// 공백처리
			for(int ii=nToLocCnt; ii<10; ii++) {
				outRec.setField("YD_TO_LOC"+(ii+1), 	JPlateYdUtils.fillSpZr("",  8, 1));
				outRec.setField("YD_TO_LAYER"+(ii+1), 	JPlateYdUtils.fillSpZr("",  3, 1));
				outRec.setField("YD_TO_STAT"+(ii+1), 	JPlateYdUtils.fillSpZr("",  1, 1));
				outRec.setField("YD_TO_STL_NO"+(ii+1),	JPlateYdUtils.fillSpZr("", 11, 1));
			}

			// FROM야드저장위치 정보 SET :: YD_FROM_LOC1, YD_FROM_LAYER1, YD_FROM_STAT1, YD_FROM_STL_NO1
			recPara = JDTORecordFactory.getInstance().create();
			rsFrLoc = JDTORecordFactory.getInstance().createRecordSet("Temp");

			recPara.setField("YD_STK_COL_GP", ydUtils.substr(szYdUpWoLoc, 0, 6));
			recPara.setField("YD_STK_LYR_NO", szYdUpWoLayer);

			intRtnVal = ydStkLyrDao.getFromLocInfo(recPara, rsFrLoc);
			nFrLocCnt = (intRtnVal >= 10) ? 10 : intRtnVal;

			for(int ii=0; ii<nFrLocCnt; ii++) {
				rsFrLoc.absolute(ii+1);
				recFrLoc = rsFrLoc.getRecord();

				//	240	FROM야드저장위치		[YD_FROM_LOC]	VARCHAR2(8)
				outRec.setField("YD_FROM_LOC"+(ii+1), 		JPlateYdUtils.fillSpZrRec(recFrLoc, "YD_FROM_LOC", 8, 1));

				//	241	FROM야드적치단		[YD_FROM_LAYER]	VARCHAR2(3)
				outRec.setField("YD_FROM_LAYER"+(ii+1), 	JPlateYdUtils.fillSpZrRec(recFrLoc, "YD_FROM_LAYER", 3, 0));

				//	242	FROM위치재료적치상태	[YD_FROM_STAT]	VARCHAR2(1)		E:가능,C:적치,V:점유,N:불가
				outRec.setField("YD_FROM_STAT"+(ii+1), 		JPlateYdUtils.fillSpZrRec(recFrLoc, "YD_FROM_STAT", 1, 1));

				//	243	FROM위치재료번호		[YD_FROM_STL_NO]VARCHAR2(11)
				outRec.setField("YD_FROM_STL_NO"+(ii+1),	JPlateYdUtils.fillSpZrRec(recFrLoc, "YD_FROM_STL_NO", 11, 1));
			}

			// 공백처리
			for(int ii=nFrLocCnt; ii<10; ii++) {
				outRec.setField("YD_FROM_LOC"+(ii+1), 		JPlateYdUtils.fillSpZr("",  8, 1));
				outRec.setField("YD_FROM_LAYER"+(ii+1), 	JPlateYdUtils.fillSpZr("",  3, 1));
				outRec.setField("YD_FROM_STAT"+(ii+1), 		JPlateYdUtils.fillSpZr("",  1, 1));
				outRec.setField("YD_FROM_STL_NO"+(ii+1),	JPlateYdUtils.fillSpZr("", 11, 1));
			}

			//	280 야드권상지시위치명		YD_UP_WO_LOC_NM			VARCHAR2(12)
			outRec.setField("YD_UP_WO_LOC_NM",	JPlateYdUtils.fillSpZrKor(ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC_NM"), 12, 1));

			//	281	야드권하지시위치명		YD_DN_WO_LOC_NM			VARCHAR2(12)
			outRec.setField("YD_DN_WO_LOC_NM",	JPlateYdUtils.fillSpZrKor(ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC_NM"), 12, 1));

			// RecordSet에 추가
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n======================makeYDY2L004() OUT========================\n", JPlateYdConst.DEBUG);
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG);
		}catch(Exception e) {
			szMsg = "Y2(1후판정정야드L2) 송신  크레인작업지시  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return -1;
		}

		return outRecSet.size();
	} // end of makeYDY2L004()


	/**
	 * YDY2L005 : 크레인작업실적응답
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeYDY2L005(JDTORecord inRec, JDTORecordSet outRecSet) {
		//		1.	전문 ID				MSG_ID				VARCHAR2(8)		YDY2L005
		//		2.	생성일				DATE				VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간			TIME				VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분			MSG_GP				VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이			MSG_LEN				NUMBER  (4)
		//		6.	임시				TEMP				VARCHAR2(29)

		//		7.	야드설비ID			YD_EQP_ID			VARCHAR2(6)		크레인설비 ID
		//		8.	야드작업진행상태	YD_WRK_PROG_STAT	VARCHAR2(1)		"2" 권상실적, "4" 권하실적
		//		9.	야드스케쥴코드		YD_SCH_CD			VARCHAR2(8)
		//		10.	야드설비스케쥴ID	YD_CRN_SCH_ID		VARCHAR2(18)
		//		11.	야드L2실적구분		YD_L2_WR_GP			VARCHAR2(1)		U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
		//		12.	야드L3처리결과코드	YD_L3_HD_RS_CD		VARCHAR2(4)		0000:정상, 권상 및 권하에서 이상이 발생한 경우 코드 부여, 9999:크레인작업이 없을 경우
		//		13.	야드L3MESSAGE		YD_L3_MSG			VARCHAR2(40)	"권상(또는 권하)실적이 정상 처리 되었습니다.
		//																	권상(또는 권하)실적이 Error처리 되었습니다."

		// 레코드 선언
		JDTORecord outRec       		= null;

		// DAO객체 생성
		JPlateYdDaoUtils 	ydDaoUtils	= new JPlateYdDaoUtils();
		JPlateYdUtils 		ydUtils     = new JPlateYdUtils();
		
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 1후판 정정 로그 관련 야드공통 UTIL (putLogNew, getLogIdNew, isEmpty, getJDTOLogId) 부분 
//-------------------------------------------------------------------------------------------------------------------------
    	YdUtils 			ydLogUtils  = new YdUtils();
//-------------------------------------------------------------------------------------------------------------------------

		// 변수선언
		String 	szMethodName    = "makeYDY2L005";
		String 	szOperationName = "1후판정정야드L2 크레인작업실적응답";
		String 	szMsg           = "";
		String 	szYdL2WrGp 		= null;								// 야드L2실적구분
		String 	szYdL2WrName    = "";
		String 	szYdL3HdRsCd  	= "";								// 야드L3처리결과코드
		String	szYdL3Msg		= "";								// 야드L3처리결과메세지

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
	
		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
				

		// TC Length = 138(60 + 78)
		int 	nTcLen          = 78;

		try {
			// Debug MSG
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n=======================makeYDY2L005() IN========================\n", JPlateYdConst.DEBUG, logId);
			ydUtils.displayRecord(szOperationName, inRec);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG, logId);

			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 업무 : 크레인작업실적응답 전문 편집(YDY2L005)
	         * 		U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하에 따른 전문편집
	         *     (U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하)
	         * 수정자 : 임춘수
	         * 일자 : 2009.06.17
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			szYdL3Msg	= ydDaoUtils.paraRecChkNull(inRec, "YD_L3_MSG");
			szYdL2WrGp 	= ydDaoUtils.paraRecChkNull(inRec, "YD_L2_WR_GP");				// U:권상실적,D:권하실적,E:강제권상,R:고장,M:모드변경,J:지시요구,F:강제권하
			if (JPlateYdConst.CRN_WRK_RE_LD_WR.equals(szYdL2WrGp)) {					// U:권상실적
				szYdL2WrName = "권상실적";
			} else if ( JPlateYdConst.CRN_WRK_RE_DN_WR.equals(szYdL2WrGp)) {			// D:권하실적
				szYdL2WrName = "권하실적";
			} else if ( JPlateYdConst.CRN_WRK_RE_EMG_PTOP.equals(szYdL2WrGp)) {		// E:비상조업실적
				szYdL2WrName = "강제권상";
			} else if ( JPlateYdConst.CRN_WRK_RE_TRBL.equals(szYdL2WrGp)) {			// R:고장
				szYdL2WrName = "고장";
				//szYdL2WrName = "설비고장복구실적";
			} else if ( JPlateYdConst.CRN_WRK_RE_MD_MOD.equals(szYdL2WrGp)) {			// M:모드변경
				szYdL2WrName = "모드변경";
				//szYdL2WrName = "설비운전모드전환";
			} else if ( JPlateYdConst.CRN_WRK_RE_WO_DMD.equals(szYdL2WrGp)) {			// J:크레인작업지시요구
				szYdL2WrName = "지시요구";
			} else if ( JPlateYdConst.CRN_WRK_RE_FRCE_DN.equals(szYdL2WrGp)) {		// F:강제권하
				szYdL2WrName = "강제권하";
			} else {
				szMsg = "[크레인작업실적응답]야드L2실적구분 (" + szYdL2WrGp + ")가 정의된 값이 아닙니다.";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				return -1;
			}

			szMsg = "[크레인작업실적응답]야드L2실적구분이 " + szYdL2WrName + " [" + szYdL2WrGp + "] 입니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			outRec = JDTORecordFactory.getInstance().create();

			// 헤더부
			outRec.setField("MSG_ID" , "YDY2L005"															);
			outRec.setField("DATE"   , JPlateYdUtils.getCurDate("yyyy-MM-dd")								);
			outRec.setField("TIME"   , JPlateYdUtils.getCurDate("HH-mm-ss")									);
			outRec.setField("MSG_GP" , "I"																	);
			outRec.setField("MSG_LEN", JPlateYdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0)				);
			outRec.setField("TEMP"   , JPlateYdUtils.fillSpZr("", 29, 1)									);

			// 야드설비ID [크레인설비 ID]
			outRec.setField("YD_EQP_ID", 		JPlateYdUtils.fillSpZrRec(inRec, "YD_EQP_ID", 6, 1)			);

			// 야드작업진행상태 [야드작업진행상태]
			outRec.setField("YD_WRK_PROG_STAT", JPlateYdUtils.fillSpZrRec(inRec, "YD_WRK_PROG_STAT", 1, 1)	);

			// 야드스케쥴코드 [야드스케쥴코드]
			outRec.setField("YD_SCH_CD", 		JPlateYdUtils.fillSpZrRec(inRec, "YD_SCH_CD", 8, 1)			);
			
			// 야드설비스케쥴ID [야드설비스케쥴ID]
			outRec.setField("YD_CRN_SCH_ID", 	JPlateYdUtils.fillSpZrRec(inRec, "YD_CRN_SCH_ID", 18, 1)	);

			// 야드L2실적구분 [야드L2실적구분]
			szYdL2WrGp = ydDaoUtils.paraRecChkNull(inRec, "YD_L2_WR_GP");
			outRec.setField("YD_L2_WR_GP", JPlateYdUtils.fillSpZr(szYdL2WrGp, 1, 1)							);

			// 야드L3처리결과코드 [야드L3처리결과코드]
			szYdL3HdRsCd = ydDaoUtils.paraRecChkNull(inRec, "YD_L3_HD_RS_CD");
			outRec.setField("YD_L3_HD_RS_CD", JPlateYdUtils.fillSpZr(szYdL3HdRsCd, 4, 1)					);

			if (JPlateYdConst.CRN_WRK_RE_CD_NORMAL_HD.equals(szYdL3HdRsCd) || JPlateYdConst.CRN_WRK_RE_CD_NO_WRK.equals(szYdL3HdRsCd) ) {
				szYdL3Msg = szYdL2WrName + "이 정상 처리 되었습니다.";
			} else {
				if ("".equals(szYdL3Msg)) {
					szYdL3Msg = szYdL2WrName + "이 Error처리 되었습니다.";
				}
			}

			// 야드L3MESSAGE
			//outRec.setField("YD_L3_MSG", JPlateYdUtils.fillSpZrKor(szYdL3Msg, 40, 1));
			outRec.setField("YD_L3_MSG", YdUtils.fillSpZr_KOR(szYdL3Msg, 40, 1)								);
			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

			// RecordSet에 추가
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n======================makeYDY2L005() OUT========================\n", JPlateYdConst.DEBUG, logId);
			ydUtils.displayRecord(szOperationName, outRec);
			//szMsg = "[" + szOperationName + "] 송신데이타 :: " + outRec.toString();
			//ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG, logId);
		} catch(Exception e) {
			szMsg = "Y2(1후판정정야드L2) 송신  크레인작업실적응답  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			return -1;
		}

		return outRecSet.size();
	} // end of makeYDY2L005()


	/**
	 * YDY2L006 : 가이던스메세지
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeYDY2L006(JDTORecord inRec, JDTORecordSet outRecSet) {
		//		1.	전문 ID				MSG_ID				VARCHAR2(8)		YDY2L006
		//		2.	생성일				DATE				VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간			TIME				VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분			MSG_GP				VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이			MSG_LEN				NUMBER  (4)
		//		6.	임시				TEMP				VARCHAR2(29)

		//		7.	야드구분			YD_GP				VARCHAR2(1)
		//		8.	야드동구분			YD_BAY_GP			VARCHAR2(1)
		//		9.	OPERATION_TYPE		OPERATION_TYPE		VARCHAR2(1)		1:Book In, 2:Book Out
		//		10.	OPERATION_MODE		OPERATION_MODE		VARCHAR2(1)		1:Start, 2:End
		//		11.	OPERATION_SOURCE	OPERATION_SOURCE	VARCHAR2(6)		저장위치

		// 레코드 선언
		JDTORecord outRec       		= null;

		// DAO객체 생성
		JPlateYdUtils 		ydUtils     = new JPlateYdUtils();
		
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.10 1후판 정정 로그 관련 야드공통 UTIL (putLogNew, getLogIdNew, isEmpty, getJDTOLogId) 부분 
//-------------------------------------------------------------------------------------------------------------------------
    	YdUtils 			ydLogUtils  = new YdUtils();
//-------------------------------------------------------------------------------------------------------------------------

		// 변수선언
		String szMethodName     = "makeYDY2L006";
		String szMsg            = "";
		String szOperationName  = "1후판정정야드L2 가이던스메세지";

		// TC Length = 70(60 + 10)
		int nTcLen              = 10;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.10 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			// Debug MSG
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n=======================makeYDY2L006() IN========================\n", JPlateYdConst.DEBUG, logId);
			ydUtils.displayRecord(szOperationName, inRec);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG, logId);

			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 업무 : 가이던스메세지 전문 편집(YDY2L006)
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			outRec = JDTORecordFactory.getInstance().create();

			// 헤더부
			outRec.setField("MSG_ID", 			"YDY2L006"													);
			outRec.setField("DATE", 			JPlateYdUtils.getCurDate("yyyy-MM-dd")						);
			outRec.setField("TIME", 			JPlateYdUtils.getCurDate("HH-mm-ss")						);
			outRec.setField("MSG_GP", 			"I"															);
			outRec.setField("MSG_LEN", 			JPlateYdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0)		);
			outRec.setField("TEMP", 			JPlateYdUtils.fillSpZr("", 29, 1));

			// 야드구분
			outRec.setField("YD_GP", 			JPlateYdUtils.fillSpZrRec(inRec, "YD_GP", 1, 1)				);

			// 야드동구분
			outRec.setField("YD_BAY_GP", 		JPlateYdUtils.fillSpZrRec(inRec, "YD_BAY_GP", 1, 1)			);

			// OPERATION_TYPE
			outRec.setField("OPERATION_TYPE", 	JPlateYdUtils.fillSpZrRec(inRec, "OPERATION_TYPE", 1, 1)	);

			// OPERATION_MODE
			outRec.setField("OPERATION_MODE", 	JPlateYdUtils.fillSpZrRec(inRec, "OPERATION_MODE", 1, 1)	);

			// OPERATION_SOURCE
			outRec.setField("OPERATION_SOURCE", JPlateYdUtils.fillSpZrRec(inRec, "OPERATION_SOURCE", 6, 1)	);

			// RecordSet에 추가
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n======================makeYDY2L006() OUT========================\n", JPlateYdConst.DEBUG, logId);
			ydUtils.displayRecord(szOperationName, outRec);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG, logId);
		} catch(Exception e) {
			szMsg = "Y2(1후판정정야드L2) 송신  가이던스메세지  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			return -1;
		}

		return outRecSet.size();
	} // end of makeYDY2L006()

	/**
	 * YDY2L007 : 크레인작업메세지
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeYDY2L007(JDTORecord inRec, JDTORecordSet outRecSet) {
		//		1.	전문 ID				MSG_ID				VARCHAR2(8)		YDY2L007
		//		2.	생성일				DATE				VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간			TIME				VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분			MSG_GP				VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이			MSG_LEN				NUMBER  (4)		108
		//		6.	임시				TEMP				VARCHAR2(29)

		//		7.	야드메시지구분		YD_MSG_GP			VARCHAR2(1)		1:전체, 2:해당동, 3:해당설비
		//		8.	야드동구분			YD_BAY_GP			VARCHAR2(1)
		//		9.	야드설비ID			YD_EQP_ID			VARCHAR2(6)
		//		10.	야드작업메시지		YD_WRK_MSG			VARCHAR2(100)

		// 레코드 선언
		JDTORecord outRec       		= null;

		// DAO객체 생성
		JPlateYdUtils 		ydUtils     = new JPlateYdUtils();
		JPlateYdDaoUtils 	ydDaoUtils  = new JPlateYdDaoUtils();
		
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 1후판 정정 로그 관련 야드공통 UTIL (putLogNew, getLogIdNew, isEmpty, getJDTOLogId) 부분 
//-------------------------------------------------------------------------------------------------------------------------
    	YdUtils 			ydLogUtils  = new YdUtils();
//-------------------------------------------------------------------------------------------------------------------------

		// 변수선언
		String szMethodName     = "makeYDY2L007";
		String szMsg            = "";
		String szOperationName  = "1후판정정야드L2 크레인작업메세지 ";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
	
		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
				
		// TC Length = 168(60 + 108)
		int nTcLen              = 108;

		try {
			// Debug MSG
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n=======================makeYDY2L007() IN========================\n", JPlateYdConst.DEBUG, logId);
			ydUtils.displayRecord(szOperationName, inRec);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG, logId);

			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 업무 : 크레인작업메세지 전문 편집(YDY2L007)
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			outRec = JDTORecordFactory.getInstance().create();

			// 헤더부  
			outRec.setField("MSG_ID", 			"YDY2L007"																		);
			outRec.setField("DATE", 			JPlateYdUtils.getCurDate("yyyy-MM-dd")											);
			outRec.setField("TIME", 			JPlateYdUtils.getCurDate("HH-mm-ss")											);
			outRec.setField("MSG_GP", 			"I"																				);
			outRec.setField("MSG_LEN", 			JPlateYdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0)							);
			outRec.setField("TEMP", 			JPlateYdUtils.fillSpZr("", 29, 1)												);

			// 야드메시지구분
			outRec.setField("YD_GP", 			JPlateYdUtils.fillSpZrRec(inRec, "YD_MSG_GP", 1, 1)								);

			// 2021.12.30 박종호
			// makeYDY2L007TR에서 호출되는 경우 YD_MSG_GP가 아닌, YD_GP값으로 넘어온다. 따라서 YD_MSG_GP값이 빈값이면 YD_GP 기준으로 재탐색해야함. 
			if(JPlateYdUtils.fillSpZrRec(inRec, "YD_MSG_GP", 1, 1).trim().equals("")){
				outRec.setField("YD_GP", 			JPlateYdUtils.fillSpZrRec(inRec, "YD_GP", 1, 1)								);
			}

			// 야드동구분
			outRec.setField("YD_BAY_GP", 		JPlateYdUtils.fillSpZrRec(inRec, "YD_BAY_GP", 1, 1)								);

			// 야드설비ID
			outRec.setField("YD_EQP_ID", 		JPlateYdUtils.fillSpZrRec(inRec, "YD_EQP_ID", 6, 1)								);

			// 야드작업메시지
			//outRec.setField("YD_WRK_MSG", 		JPlateYdUtils.fillSpZrKor(ydDaoUtils.paraRecChkNull(inRec, "YD_WRK_MSG"), 100, 1));
			outRec.setField("YD_WRK_MSG", 		YdUtils.fillSpZr_KOR(ydDaoUtils.paraRecChkNull(inRec, "YD_WRK_MSG"), 100, 1)	);

			// RecordSet에 추가
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n======================makeYDY2L007() OUT========================\n", JPlateYdConst.DEBUG, logId);
			ydUtils.displayRecord(szOperationName, outRec);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG, logId);
		} catch(Exception e) {
			szMsg = "Y2(1후판정정야드L2) 송신  크레인작업메세지  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			return -1;
		}

		return outRecSet.size();
	} // end of makeYDY2L007()
	
	/**
	 * YDY2L007 : 크레인작업메세지
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static void makeYDY2L007TR(JDTORecord inRec) {
		//		1.	전문 ID				MSG_ID				VARCHAR2(8)		YDY2L007
		//		2.	생성일				DATE				VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간			TIME				VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분			MSG_GP				VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이			MSG_LEN				NUMBER  (4)		108
		//		6.	임시				TEMP				VARCHAR2(29)
		//		7.	야드메시지구분		YD_MSG_GP			VARCHAR2(1)		1:전체, 2:해당동, 3:해당설비
		//		8.	야드동구분			YD_BAY_GP			VARCHAR2(1)
		//		9.	야드설비ID			YD_EQP_ID			VARCHAR2(6)
		//		10.	야드작업메시지		YD_WRK_MSG			VARCHAR2(100)

		// 레코드 선언
		JDTORecordSet rsResult 		 = null;
		JDTORecord recGetVal 		 = null;
		JDTORecord outRec 			 = null;

		// DAO객체 생성
		JPlateYdStkBedDAO 	ydStkBedDao = new JPlateYdStkBedDAO();
		JPlateYdUtils 		ydUtils     = new JPlateYdUtils();
		JPlateYdDaoUtils 	ydDaoUtils  = new JPlateYdDaoUtils();
		JPlateYdDelegate	ydDelegate 	= new JPlateYdDelegate();
		
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 1후판 정정 로그 관련 야드공통 UTIL (putLogNew, getLogIdNew, isEmpty, getJDTOLogId) 부분 
//-------------------------------------------------------------------------------------------------------------------------
    	YdUtils 			ydLogUtils  = new YdUtils();
//-------------------------------------------------------------------------------------------------------------------------

		// 변수선언
		String szMethodName     	= "makeYDY2L007TR";
		String szOperationName      = "1후판정정야드L2 크레인작업메세지";
		String szMsg        	    = "";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
	
		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		// 리턴값
		int intRtnVal               = 0;

		// TC Length = 168(60 + 108)
		int nTcLen              = 108;

		try{
			// 레코드 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
					
			intRtnVal = ydStkBedDao.getYDY2L007TrInfo(inRec, rsResult);

			if (intRtnVal > 0){
				
				recGetVal = rsResult.getRecord(0);

				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 업무 : 크레인작업메세지 전문 편집(YDY2L007)
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				outRec = JDTORecordFactory.getInstance().create();

				// 헤더부
				outRec.setField("MSG_ID", 			"YDY2L007"																			);
				outRec.setField("DATE", 			JPlateYdUtils.getCurDate("yyyy-MM-dd")												);
				outRec.setField("TIME", 			JPlateYdUtils.getCurDate("HH-mm-ss")												);
				outRec.setField("MSG_GP", 			"I"																					);
				outRec.setField("MSG_LEN", 			JPlateYdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0)								);
				outRec.setField("TEMP", 			JPlateYdUtils.fillSpZr("", 29, 1)													);
				// 야드메시지구분
				outRec.setField("YD_GP", 			JPlateYdUtils.fillSpZrRec(recGetVal, "YD_MSG_GP", 1, 1)								);
				// 야드동구분
				outRec.setField("YD_BAY_GP", 		JPlateYdUtils.fillSpZrRec(recGetVal, "YD_BAY_GP", 1, 1)								);
				// 야드설비ID
				outRec.setField("YD_EQP_ID", 		JPlateYdUtils.fillSpZrRec(recGetVal, "YD_EQP_ID", 6, 1)								);
				// 야드작업메시지
				outRec.setField("YD_WRK_MSG", 		YdUtils.fillSpZr_KOR(ydDaoUtils.paraRecChkNull(recGetVal, "YD_WRK_MSG"), 100, 1)	);
	        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 outRec에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
				outRec.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
		              	
				String szRtnMsg = ydDelegate.sendMsg(outRec);
				
				String szLogMsg = "[JSP Session] " + szOperationName + " 작업재지시  - 리턴메세지 : [차량상차 크레인메세지 있슴] ";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			}else{
				String szLogMsg = "[JSP Session] " + szOperationName + " 작업재지시  - 리턴메세지 : [차량상차 크레인메세지 없슴] ";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			}
		}catch(Exception e) {
			szMsg = "Y2(1후판정정야드L2) 송신  크레인작업메세지  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
		}
	} // end of makeYDY2L007TR()

  //---------------------------------------------------------------------------
	
	/**
	 * YDY2L008 : 후판L2제품번호응답
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeYDY2L008(JDTORecord inRec, JDTORecordSet outRecSet) {
		//		1.	전문 ID				MSG_ID				VARCHAR2(8)		YDY2L008
		//		2.	생성일				DATE				VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간				TIME				VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분				MSG_GP				VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이				MSG_LEN				NUMBER  (4)		49
		//		6.	임시					TEMP				VARCHAR2(29)

		//		7.	PL_L2_TRK_NO		후판L2제품번호			CHAR	16				
		//		8.	PL_MTL_NO			후판재료번호			CHAR	10				
		//		9.  YD_STL_WT			야드재료중량			NUMBER	5				
		//		10. YD_STL_T			야드재료두께			NUMBER	6		자릿수(6.3) 		
		//		11. YD_STL_W			야드재료폭			NUMBER	5		자릿수(5.1)		
		//		12. YD_STL_L			야드재료길이			NUMBER	7				

		// 레코드 선언
		JDTORecord outRec       		= null;

		// DAO객체 생성
		JPlateYdUtils 		ydUtils     = new JPlateYdUtils();
		JPlateYdDaoUtils 	ydDaoUtils  = new JPlateYdDaoUtils();
		JPlateYdCommDAO 	commDao 	= new JPlateYdCommDAO();

		// 변수선언
		String szMethodName     = "makeYDY2L008";
		String szMsg            = "";
		String szOperationName  = "1후판정정야드 후판L2제품번호응답 ";

		// TC Length 
		int nTcLen              = 49;

		try {
			// Debug MSG
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n=======================makeYDY2L008() IN========================\n", JPlateYdConst.DEBUG);
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG);

			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 업무 : 후판L2제품번호응답 전문 편집(YDY2L008)
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			outRec = JDTORecordFactory.getInstance().create();

			// 헤더부
			outRec.setField("MSG_ID"	,"YDY2L008");
			outRec.setField("DATE"		,JPlateYdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"		,JPlateYdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP"	,"I");
			outRec.setField("MSG_LEN"	,JPlateYdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0));
			outRec.setField("TEMP"		,JPlateYdUtils.fillSpZr("", 29, 1));
			
			String sPlMtlNo 	= ydDaoUtils.paraRecChkNull(inRec, "PL_MTL_NO"); // 후판재료번호
			
			String sPlL2TrkNo 	= "";
			String sYD_STL_WT   = "";
			String sYD_STL_T    = "";
			String sYD_STL_W    = "";
			String sYD_STL_L    = "";
			
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("STL_NO" , sPlMtlNo);
			JDTORecordSet rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getL3No");
			
			if(rsResult.size() > 0) {
				
				sPlL2TrkNo	= rsResult.getRecord(0).getFieldString("STL_NO");	
				sYD_STL_WT	= rsResult.getRecord(0).getFieldString("YD_MTL_WT");	
				sYD_STL_T	= rsResult.getRecord(0).getFieldString("YD_MTL_T");	
				sYD_STL_W	= rsResult.getRecord(0).getFieldString("YD_MTL_W");	
				sYD_STL_L	= rsResult.getRecord(0).getFieldString("YD_MTL_L");	
			}
			
			outRec.setField("PL_L2_TRK_NO"	,JPlateYdUtils.fillSpZr(sPlL2TrkNo, 16, 1));
			outRec.setField("PL_MTL_NO"		,JPlateYdUtils.fillSpZr(sPlMtlNo	, 10, 1));
			// 야드재료중량 [야드재료중량]
			outRec.setField("YD_STL_WT"		,JPlateYdUtils.fillSpZr(sYD_STL_WT, 5, 1));
			// 야드재료두께 [야드재료두께]
			sYD_STL_T = JPlateYdUtils.fillSpZr(sYD_STL_T, 7, 1);
			outRec.setField("YD_STL_T"		,ydUtils.floatLRPAD(sYD_STL_T, 6, 3, '0'));	
			// 야드재료폭 [야드재료폭]
			sYD_STL_W = JPlateYdUtils.fillSpZr(sYD_STL_W, 6, 1);
			outRec.setField("YD_STL_W"		,ydUtils.floatLRPAD(sYD_STL_W, 5, 1, '0'));	
			// 야드재료길이 [야드재료길이]
			outRec.setField("YD_STL_L"		,JPlateYdUtils.fillSpZr(sYD_STL_L, 7, 1));
			
			// RecordSet에 추가
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n======================makeYDY2L008() OUT========================\n", JPlateYdConst.DEBUG);
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG);
		} catch(Exception e) {
			szMsg = "Y2(1후판정정야드L2) 송신  후판L2제품번호응답  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return -1;
		}

		return outRecSet.size();
	} // end of makeYDY2L008()

	/**
	 * YDY2L008 : 후판L2제원정보요구
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeYDY2L009(JDTORecord inRec, JDTORecordSet outRecSet) {
		//		1.	전문 ID				MSG_ID				VARCHAR2(8)		YDY2L008
		//		2.	생성일				DATE				VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간				TIME				VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분				MSG_GP				VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이				MSG_LEN				NUMBER  (4)		49
		//		6.	임시					TEMP				VARCHAR2(29)

		//		7.	YD_STR_LOC			야드저장위치			CHAR	8				

		// 레코드 선언
		JDTORecord outRec       		= null;

		// DAO객체 생성
		JPlateYdUtils 		ydUtils     = new JPlateYdUtils();
		JPlateYdDaoUtils 	ydDaoUtils  = new JPlateYdDaoUtils();
		JPlateYdCommDAO 	commDao 	= new JPlateYdCommDAO();

		// 변수선언
		String szMethodName     = "makeYDY2L009";
		String szMsg            = "";
		String szOperationName  = "1후판정정야드 후판L2제원정보요구 ";

		// TC Length 
		int nTcLen              = 8;

		try {
			// Debug MSG
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n=======================makeYDY2L009() IN========================\n", JPlateYdConst.DEBUG);
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG);

			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 업무 : 후판L2제품번호응답 전문 편집(YDY2L008)
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			outRec = JDTORecordFactory.getInstance().create();

			// 헤더부
			outRec.setField("MSG_ID"		,"YDY2L009");
			outRec.setField("DATE"			,JPlateYdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"			,JPlateYdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP"		,"I");
			outRec.setField("MSG_LEN"		,JPlateYdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0));
			outRec.setField("TEMP"			,JPlateYdUtils.fillSpZr("", 29, 1));
			
			
			String sYD_STR_LOC 	= ydDaoUtils.paraRecChkNull(inRec, "YD_STR_LOC"); // 야드저장위치
			
			
			/*--------------------------------------------------------
			 - YDY2L009 는 L2가 관리하는 갠트리 크레인 영역만 요구 할 수 있다.
			 - L3가 관리하는 영역을 요구하면 Error 처리 해야 한다.
			 - 갠트리 크레인 영역
			 - 50호기 PECB01~PECB04,PE0111
			 - 45호기 PD0162,PE0151,PE0154~PE0156
			 - 21호기 PB0361~PB0366
			 ---------------------------------------------------------*/
			boolean bFlag = false;
			
			if("PECB01".equals(sYD_STR_LOC) || "PECB02".equals(sYD_STR_LOC) || "PECB03".equals(sYD_STR_LOC) || "PECB04".equals(sYD_STR_LOC) || "PE0111".equals(sYD_STR_LOC)) {
				//50호기
				bFlag = true;
			} else if("PD0162".equals(sYD_STR_LOC) || "PE0151".equals(sYD_STR_LOC) || "PE0154".equals(sYD_STR_LOC) || "PE0155".equals(sYD_STR_LOC) || "PE0156".equals(sYD_STR_LOC)) {
				//45호기
				bFlag = true;
			} else if("PB0361".equals(sYD_STR_LOC) || "PB0362".equals(sYD_STR_LOC) || "PB0363".equals(sYD_STR_LOC) || "PB0364".equals(sYD_STR_LOC) || "PB0365".equals(sYD_STR_LOC) || "PB0366".equals(sYD_STR_LOC) ) {
				//21호기
				bFlag = true;
			}
			
			if(!bFlag) {
				szMsg = "Y2(1후판정정야드L2) 송신  후판L2제원정보요구  데이터 반환 중 예외발생! 예외메세지: " + sYD_STR_LOC + " 위치는 L2 영역이 아닙니다!";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return -1;
			}

			outRec.setField("YD_STR_LOC"	,JPlateYdUtils.fillSpZr(sYD_STR_LOC, 8, 1));
			
			
			// RecordSet에 추가
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n======================makeYDY2L009() OUT========================\n", JPlateYdConst.DEBUG);
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG);
		} catch(Exception e) {
			szMsg = "Y2(1후판정정야드L2) 송신  후판L2제원정보요구  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return -1;
		}

		return outRecSet.size();
	} // end of makeYDY2L009()
	
	
	/**
	 * YDY2L004V2 : 크레인작업지시 (크레인 작업재료에 DEL_YN = 'N' 만 대상)
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeYDY2L004V2(JDTORecord inRec, JDTORecordSet outRecSet) {

		//		1.	전문 ID						MSG_ID					VARCHAR2(8)		YDY2L004
		//		2.	생성일						DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER  (4)		1667
		//		6.	임시						TEMP					VARCHAR2(29)

		//		7	야드설비ID					YD_EQP_ID				VARCHAR2(6)	        크레인설비 ID
		//		8	야드작업진행상태			YD_WRK_PROG_STAT		VARCHAR2(1)	    "W" 작업지시대기, "1" 권상작업, "3" 권하작업
		//		9	야드스케쥴코드				YD_SCH_CD				VARCHAR2(8)
		//      10. 야드스케쥴명                             YD_SCH_NAME             VARCHAR2(30)
		//		11	야드크레인스케쥴ID			YD_CRN_SCH_ID			VARCHAR2(18)
		//		12	야드크레인작업매수			YD_CRN_WRK_SH			NUMBER  (2)
		//		13	야드크레인작업중량			YD_CRN_WRK_WT			NUMBER  (7)
		//		14	야드크레인작업총두께		YD_CRN_WRK_T			NUMBER  (7,3)
		//		15	야드크레인작업최대폭		YD_CRN_WRK_MAX_W		NUMBER  (5,1)
		//		16	야드크레인작업최대길이		YD_CRN_WRK_MAX_L		NUMBER  (7)
		//		17	야드크레인스케줄잔여회수		YD_CRN_SCH_RMD_CNT		NUMBER  (2)	        권상모음, 권하분리작업 시 크레인 Handling 잔여 회수

		//		33	야드권상지시위치			YD_UP_WO_LOC			VARCHAR2(8)
		//		34	야드권상지시단				YD_UP_WO_LAYER			VARCHAR2(3)
		//		35	야드권상지시X축				YD_UP_WO_LOC_XAXIS		NUMBER  (7)
		//		36	야드권상지시X축오차최대		YD_UP_WO_XAXIS_GAP_MAX	NUMBER  (5)
		//		37	야드권상지시X축오차최소		YD_UP_WO_XAXIS_GAP_MIN	NUMBER  (5)
		//		38	야드권상지시Y축				YD_UP_WO_LOC_YAXIS		NUMBER  (5)

		//		41	야드권상지시Y축오차최대		YD_UP_WO_YAXIS_GAP_MAX	NUMBER  (5)
		//		42	야드권상지시Y축오차최소		YD_UP_WO_YAXIS_GAP_MIN	NUMBER  (5)
		//		43	야드권상지시Z축				YD_UP_WO_LOC_ZAXIS		NUMBER  (5)
		//		44	야드권상지시Z축오차최대		YD_UP_WO_ZAXIS_GAP_MAX	NUMBER  (5)
		//		45	야드권상지시Z축오차최소		YD_UP_WO_ZAXIS_GAP_MIN	NUMBER  (5)
		//		46	야드권하지시위치			YD_DN_WO_LOC			VARCHAR2(8)
		//		47	야드권하지시단				YD_DN_WO_LAYER			VARCHAR2(3)
		//		48	야드권하지시X축				YD_DN_WO_LOC_XAXIS		NUMBER  (7)
		//		49	야드권하지시X축오차최대		YD_DN_WO_XAXIS_GAP_MAX	NUMBER  (5)
		//		50	야드권하지시X축오차최소		YD_DN_WO_XAXIS_GAP_MIN	NUMBER  (5)
		//		51	야드권하지시Y축				YD_DN_WO_LOC_YAXIS		NUMBER  (5)

		//		54	야드권하지시Y축오차최대		YD_DN_WO_YAXIS_GAP_MAX	NUMBER  (5)
		//		55	야드권하지시Y축오차최소		YD_DN_WO_YAXIS_GAP_MIN	NUMBER  (5)
		//		56	야드권하지시Z축				YD_DN_WO_LOC_ZAXIS		NUMBER  (5)
		//		57	야드권하지시Z축오차최대		YD_DN_WO_ZAXIS_GAP_MAX	NUMBER  (5)
		//		58	야드권하지시Z축오차최소		YD_DN_WO_ZAXIS_GAP_MIN	NUMBER  (5)
		//		59	야드설비ID2					YD_EQP_ID2				VARCHAR2(6)	        권상 또는 권하위치가 대차 및 차량인 경우
		//		60	야드대차목적동				YD_TC_AIM_BAY_GP		VARCHAR2(1)
		//		61	야드차량사용구분			YD_CAR_USE_GP			VARCHAR2(1)	        권상 또는 권하위치가 차량인 경우("L" 구내운송차량, "G" 제품출하차량)
		//		62	차량번호					CAR_NO					VARCHAR2(15)	권상 또는 권하위치가 제품출하차량인 경우
		//											CAR_NO 15자리에 각 재료번호 긴급재여부 표기하는용도로 사용 2023.11.09 박종호. 강기석주임 요청  REQ202310501956  
		//		63	운송장비코드				TRN_EQP_CD				VARCHAR2(8)	        권상 또는 권하위치가 구내운송차량인 경우
		//		64	야드설비작업매수			YD_EQP_WRK_SH			NUMBER  (2,0)	대차, 차량스케줄의 설비작업매수
		//		65	야드설비잔량매수			YD_EQP_RMN_SH			NUMBER  (2,0)   대차, 차량스케줄의 설비작업매수 - 크레인 작업이 미완료된 매수
		//		GROUP[15]----------------------------------------------------------------------  STL1 : 최상단 . . . STL20 최하단
		//		66	재료번호					STL_NO1					VARCHAR2(11)
		//		67	파일링코드					YD_PILING_CD1			VARCHAR2(8)
		//		68	야드재료중량				YD_STL_WT1				NUMBER  (5)
		//		69	야드재료두께				YD_STL_T1				NUMBER  (6,3)
		//		70	야드재료폭					YD_STL_W1				NUMBER  (5,1)
		//		71	야드재료길이				YD_STL_L1				NUMBER  (7)
		//		72	권하지시위치				YD_DN_WO_LOC1			VARCHAR2(8)
		//		73	권하지시단					YD_DN_WO_LAYER1			VARCHAR2(3)
		//		GROUP[10]----------------------------------------------------------------------END
		//		186	야드스케쥴코드_Next			YD_SCH_CD_NEXT			VARCHAR2(8)		크레인스케줄에 등록된 다음 작업
		//      187 야드스케쥴명_Next         	YD_SCH_NAME_NEXT        VARCHAR2(30)
		//		188	야드권상지시위치_Next		YD_UP_WO_LOC_NEXT		VARCHAR2(8)
		//		189	야드권상지시단_Next			YD_UP_WO_LAYER_NEXT		VARCHAR2(3)
		//		190 야드권하지시위치_Next		YD_DN_WO_LOC_NEXT		VARCHAR2(8)
		//		191	야드권하지시단_Next			YD_DN_WO_LAYER_NEXT		VARCHAR2(3)
		//		192	재료번호_Next				STL_NO_NEXT				VARCHAR2(11)
		//		193	야드크레인작업매수_Next		YD_CRN_WRK_SH_NEXT		NUMBER  (2)
		//		194	야드크레인작업중량_Next		YD_CRN_WRK_WT_NEXT		NUMBER  (7)
		//		195	야드크레인작업총두께_Next	YD_CRN_WRK_T_NEXT		NUMBER  (7,3)
		//		196	야드크레인작업최대폭_Next	YD_CRN_WRK_MAX_W_NEXT	NUMBER  (5,1)
		//		197	야드크레인작업최대길이_Next	YD_CRN_WRK_MAX_L_NEXT	NUMBER  (7)
		//-- 2013.04.10 김현우 추가
		//		198	파일링구분					YD_PILING_GP			VARCHAR2(1)		P:파일링, H:횡행작업, N:일반작업, M:멀티, F:강제권상
		//		199	야드스케쥴우선순위			YD_SCH_PRIOR			NUMBER	(2)
		//		GROUP[10]----------------------------------------------------------------------
		//		200	TO야드저장위치1				YD_TO_LOC1				VARCHAR2(8)
		//		201	TO야드적치단1				YD_TO_LAYER1			VARCHAR2(3)
		//		202	TO위치재료적치상태1			YD_TO_STAT1				VARCHAR2(1)		E:가능,C:적치,V:점유,N:불가,U:권상지시,D:권하지시
		//		203	TO위치재료번호1				YD_TO_STL_NO1			VARCHAR2(11)
		//-- 2013.05.20 김현우 추가
		//		GROUP[10]----------------------------------------------------------------------
		//		240	FROM야드저장위치1			YD_FROM_LOC1			VARCHAR2(8)
		//		241	FROM야드적치단1				YD_FROM_LAYER1			VARCHAR2(3)
		//		242	FROM위치재료적치상태1		YD_FROM_STAT1			VARCHAR2(1)		E:가능,C:적치,V:점유,N:불가,U:권상지시,D:권하지시
		//		243	FROM위치재료번호1			YD_FROM_STL_NO1			VARCHAR2(11)
		//		GROUP[10]----------------------------------------------------------------------END
		//-- 2013.07.28 차상국 저장위치명 추가
		//		280 야드권상지시위치명			YD_UP_WO_LOC_NM			VARCHAR2(12)
		//		281	야드권하지시위치명			YD_DN_WO_LOC_NM			VARCHAR2(12)

		// 레코드 선언
		JDTORecord    recPara		= null;
		JDTORecordSet rsCrnSch   	= null;
		JDTORecordSet rsCrnMtl   	= null;
		JDTORecordSet rsRule   		= null;
		JDTORecordSet rsWrkBook		= null;
		JDTORecordSet rsToLoc   	= null;
		JDTORecordSet rsFrLoc   	= null;
		JDTORecordSet rsXyGap		= null;
		JDTORecordSet rsUgntList	= null;		

		JDTORecord recCrnSch     	= null;
		JDTORecord recCrnMtl     	= null;
		JDTORecord recRule     		= null;
		JDTORecord recWrkBook    	= null;
		JDTORecord recToLoc     	= null;
		JDTORecord recFrLoc     	= null;
		JDTORecord outRec         	= null;

		// DAO객체 생성
		JPlateYdSchRuleDAO 	ydSchRuleDao = new JPlateYdSchRuleDAO();
		JPlateYdWrkbookDAO 	ydWrkbookDao = new JPlateYdWrkbookDAO();
		JPlateYdStkLyrDAO	ydStkLyrDao  = new JPlateYdStkLyrDAO();

		JPlateYdDaoUtils 	ydDaoUtils   = new JPlateYdDaoUtils();
		JPlateYdUtils 		ydUtils      = new JPlateYdUtils();
		
    	JPlateYdCommDAO 	commDao 	= new JPlateYdCommDAO();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
    	YdUtils 			ydLogUtils  = new YdUtils();
    	
// 2024.11.19 logId [P]<135016302> 형식 사용으로 변경    	
//    	String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
//-------------------------------------------------------------------------------------------------------------------------

		// 변수선언
		String 	szMethodName     	= "makeYDY2L004V2";
		String 	szMsg            	= "";
		String 	szOperationName  	= "1후판정정야드L2 크레인작업지시";
		String 	szYdSchCd        	= "";
		String 	szYdSchName      	= "";
		String 	szYdSchCdNext   	= "";
		String 	szYdSchNameNext 	= "";
		String 	szYdEqpId2       	= "";
		String 	szCrnSchID       	= "";
		String 	szYdWrkProgStat 	= "";
		String 	szMsgGp          	= "";
		String 	szTemp           	= "";
		String	szYdUpWoLoc			= "";
		String	szYdUpWoLayer		= "";
		String	szYdDnWoLoc			= "";
		String	szYdDnWoLayer		= "";
		String	szydPilingGp		= "";
		String	szYdAidWrkYn		= "";			// 보조작업여부 (Y/N)
		String	szCdContents		= "";

		String	szYD_UP_WO_XAXIS_GAP_MAX	= "";
		String	szYD_UP_WO_XAXIS_GAP_MIN	= "";
		String	szYD_UP_WO_YAXIS_GAP_MAX	= "";
		String	szYD_UP_WO_YAXIS_GAP_MIN	= "";
		String	szYD_DN_WO_XAXIS_GAP_MAX	= "";
		String	szYD_DN_WO_XAXIS_GAP_MIN	= "";
		String	szYD_DN_WO_YAXIS_GAP_MAX	= "";
		String	szYD_DN_WO_YAXIS_GAP_MIN	= "";

		String szPL_WR_PRSNT_PROC_CD=""; //재료들의 현공정코드 merge용 String  2022.02.08 박종호
		
		// 문자열변환 임시변수
		String szConv           	= "";
		
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  			// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                    	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		int		iYdEqpWrkSh 		= 0;
		int		iYdMtlWt			= 0;
		double	dYdMtlT     		= 0.0;

		// 리턴값
		int 	intRtnVal          	= 0;
		int 	nIdx                = 0;
		int		nToLocCnt			= 0;
		int		nFrLocCnt			= 0;

		// TC Length = 1727 (60 + 1667)
		int 	nTcLen             	= 1667;

		try{
			// Debug MSG
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n=======================makeYDY2L004() IN========================\n", JPlateYdConst.DEBUG, logId);
			ydUtils.displayRecord(szOperationName, inRec);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG, logId);

			szCrnSchID 		= ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID"		);
			szYdWrkProgStat = ydDaoUtils.paraRecChkNull(inRec, "YD_WRK_PROG_STAT"	);
			szMsgGp 		= ydDaoUtils.paraRecChkNull(inRec, "MSG_GP"				);

			//=======================================================================================================================
			// [1] 크레인스케쥴+크레인작업재료 테이블 조회 (Key: 크레인스케쥴 ID)
			//=======================================================================================================================
			recPara  = JDTORecordFactory.getInstance().create();
			rsCrnSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
			recPara.setField("YD_CRN_SCH_ID", 		szCrnSchID		);
			recPara.setField("YD_WRK_PROG_STAT", 	szYdWrkProgStat	);
			//intRtnVal = ydCrnSchDao.getYdCrnWrkMtlNext(recPara, rsCrnSch);
			rsCrnSch = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnWrkMtlNext2", logId, szMethodName, "[1] 크레인스케쥴+크레인작업재료 테이블 조회 (Key: 크레인스케쥴 ID)");
			intRtnVal = rsCrnSch.size();
			
			if (intRtnVal < 0) {
				szMsg = "크레인스케쥴 테이블 조회오류  (" + szCrnSchID + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return 0;
			} else if (intRtnVal == 0) {
				szMsg = "크레인스케쥴 테이블 조회건수 없음 (" + szCrnSchID + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				return 0;
			}

			outRec = JDTORecordFactory.getInstance().create();
			rsCrnSch.first();
			recCrnSch = rsCrnSch.getRecord();

			szYdUpWoLoc   = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC"		);
			szYdUpWoLayer = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LAYER"	);
			szYdDnWoLoc   = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC"		);
			szYdDnWoLayer = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LAYER"	);

			//szYdUpWoLoc   = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");
			//szYdUpWoLayer = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LAYER");
			//szYdDnWoLoc   = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC");
			//szYdDnWoLayer = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LAYER");

			// 허용오차 Set
			recPara.setField("YD_UP_WO_LOC", 	szYdUpWoLoc);
			recPara.setField("YD_DN_WO_LOC", 	szYdDnWoLoc);
			rsXyGap = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getStkBedXyGap", logId, szMethodName, "TB_YD_STKBED 의 허용오차 조회");
			
			if (rsXyGap.size() > 0) {
				
				//권상위치 X,Y 좌표 허용오차
				szYD_UP_WO_XAXIS_GAP_MAX = rsXyGap.getRecord(0).getFieldString("UP_X_GAP");
				szYD_UP_WO_XAXIS_GAP_MIN = rsXyGap.getRecord(0).getFieldString("UP_X_GAP");
				szYD_UP_WO_YAXIS_GAP_MAX = rsXyGap.getRecord(0).getFieldString("UP_Y_GAP");
				szYD_UP_WO_YAXIS_GAP_MIN = rsXyGap.getRecord(0).getFieldString("UP_Y_GAP"); 
				
				//권하위치 X,Y 좌표 허용오차
				szYD_DN_WO_XAXIS_GAP_MAX = rsXyGap.getRecord(0).getFieldString("DN_X_GAP"); 
				szYD_DN_WO_XAXIS_GAP_MIN = rsXyGap.getRecord(0).getFieldString("DN_X_GAP");  
				szYD_DN_WO_YAXIS_GAP_MAX = rsXyGap.getRecord(0).getFieldString("DN_Y_GAP");  
				szYD_DN_WO_YAXIS_GAP_MIN = rsXyGap.getRecord(0).getFieldString("DN_Y_GAP");  
				
				if("0".equals(szYD_UP_WO_XAXIS_GAP_MAX)) {
					if(szYdUpWoLoc.startsWith("PF")){
						szYD_UP_WO_XAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapYYdP(szYdUpWoLoc));
					} else {
						szYD_UP_WO_XAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapXYdP(szYdUpWoLoc));
					}
				}
				
				if("0".equals(szYD_UP_WO_XAXIS_GAP_MIN)) {
					if(szYdUpWoLoc.startsWith("PF")){
						szYD_UP_WO_XAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapYYdP(szYdUpWoLoc));
					} else {
						szYD_UP_WO_XAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapXYdP(szYdUpWoLoc));
					}
				}
				
				if("0".equals(szYD_UP_WO_YAXIS_GAP_MAX)) {
					if(szYdUpWoLoc.startsWith("PF")){
						szYD_UP_WO_YAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapXYdP(szYdUpWoLoc));
					} else {
						szYD_UP_WO_YAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapYYdP(szYdUpWoLoc));
					}
				}

				if("0".equals(szYD_UP_WO_YAXIS_GAP_MIN)) {
					if(szYdUpWoLoc.startsWith("PF")){
						szYD_UP_WO_YAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapXYdP(szYdUpWoLoc));
					} else {
						szYD_UP_WO_YAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapYYdP(szYdUpWoLoc));
					}
				}

				if("0".equals(szYD_DN_WO_XAXIS_GAP_MAX)) {
					if(szYdUpWoLoc.startsWith("PF")){
						szYD_DN_WO_XAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapYYdP(szYdDnWoLoc));
					} else {
						szYD_DN_WO_XAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapXYdP(szYdDnWoLoc));
					}
				}
				
				if("0".equals(szYD_DN_WO_XAXIS_GAP_MIN)) {
					if(szYdUpWoLoc.startsWith("PF")){
						szYD_DN_WO_XAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapYYdP(szYdDnWoLoc));
					} else {
						szYD_DN_WO_XAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapXYdP(szYdDnWoLoc));
					}
				}

				if("0".equals(szYD_DN_WO_YAXIS_GAP_MAX)) {
					if(szYdUpWoLoc.startsWith("PF")){
						szYD_DN_WO_YAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapXYdP(szYdDnWoLoc));
					} else {
						szYD_DN_WO_YAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapYYdP(szYdDnWoLoc));
					}
				}

				if("0".equals(szYD_DN_WO_YAXIS_GAP_MIN)) {
					if(szYdUpWoLoc.startsWith("PF")){
						szYD_DN_WO_YAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapXYdP(szYdDnWoLoc));
					} else {
						szYD_DN_WO_YAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapYYdP(szYdDnWoLoc));
					}
				}
				
			} else {
				
				/*
				 * 2016.04.29 윤재광 수정
				 * 허용오차 계산시 A/B동과 F동은 베드적치 방향이 틀리기 때문에 반대로 처리.
				 */
				if(szYdUpWoLoc.startsWith("PF")){
					szYD_UP_WO_XAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapYYdP(szYdUpWoLoc));
					szYD_UP_WO_XAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapYYdP(szYdUpWoLoc));
					szYD_UP_WO_YAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapXYdP(szYdUpWoLoc));
					szYD_UP_WO_YAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapXYdP(szYdUpWoLoc));
	
					szYD_DN_WO_XAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapYYdP(szYdDnWoLoc));
					szYD_DN_WO_XAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapYYdP(szYdDnWoLoc));
					szYD_DN_WO_YAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapXYdP(szYdDnWoLoc));
					szYD_DN_WO_YAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapXYdP(szYdDnWoLoc));
				}else{
					szYD_UP_WO_XAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapXYdP(szYdUpWoLoc));
					szYD_UP_WO_XAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapXYdP(szYdUpWoLoc));
					szYD_UP_WO_YAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapYYdP(szYdUpWoLoc));
					szYD_UP_WO_YAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapYYdP(szYdUpWoLoc));
	
					szYD_DN_WO_XAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapXYdP(szYdDnWoLoc));
					szYD_DN_WO_XAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapXYdP(szYdDnWoLoc));
					szYD_DN_WO_YAXIS_GAP_MAX	= Integer.toString(ydUtils.getCraneGapYYdP(szYdDnWoLoc));
					szYD_DN_WO_YAXIS_GAP_MIN	= Integer.toString(ydUtils.getCraneGapYYdP(szYdDnWoLoc));
				}
			}

/*
			// 허용오차 Set
			szYD_UP_WO_XAXIS_GAP_MAX	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_XAXIS_GAP_MAX", "500");
			szYD_UP_WO_XAXIS_GAP_MIN	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_XAXIS_GAP_MIN", "500");
			szYD_UP_WO_YAXIS_GAP_MAX	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_YAXIS_GAP_MAX", "100");
			szYD_UP_WO_YAXIS_GAP_MIN	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_YAXIS_GAP_MIN", "100");

			szYD_DN_WO_XAXIS_GAP_MAX	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_XAXIS_GAP_MAX", "500");
			szYD_DN_WO_XAXIS_GAP_MIN	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_XAXIS_GAP_MIN", "500");
			szYD_DN_WO_YAXIS_GAP_MAX	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_YAXIS_GAP_MAX", "100");
			szYD_DN_WO_YAXIS_GAP_MIN	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_YAXIS_GAP_MIN", "100");
*/
			// 헤더부
			outRec.setField("MSG_ID", 		"YDY2L004"								);
			outRec.setField("DATE", 		JPlateYdUtils.getCurDate("yyyy-MM-dd")	);
			outRec.setField("TIME", 		JPlateYdUtils.getCurDate("HH-mm-ss")	);

			if ("D".equals(szMsgGp) || "U".equals(szMsgGp)) {
				outRec.setField("MSG_GP", 	JPlateYdUtils.fillSpZr(szMsgGp, 1, 1));
			} else {
				outRec.setField("MSG_GP", 	"I");
			}

			outRec.setField("MSG_LEN", 		JPlateYdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0)	);
			outRec.setField("TEMP", 		JPlateYdUtils.fillSpZr("", 29, 1)						);

			// 야드작업진행상태 [야드작업진행상태]
			szTemp = JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_WRK_PROG_STAT", 1, 1);

			// 명령선택 대기상테일때 설비ID CLEAR
			if ("W".equals(szTemp) || "0".equals(szTemp)) {
				outRec.setField("YD_EQP_ID", 		JPlateYdUtils.fillSpZr(" ", 6, 1)						);
				outRec.setField("YD_WRK_PROG_STAT", JPlateYdUtils.fillSpZr("W", 1, 1)						);

			} else {
				outRec.setField("YD_EQP_ID", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_ID", 6, 1)	);
				outRec.setField("YD_WRK_PROG_STAT", JPlateYdUtils.fillSpZr(szTemp, 1, 1)					);
			}

			// 야드스케쥴코드 [야드스케쥴코드]
			szYdSchCd 		= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD"				);
			szYdAidWrkYn	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_AID_WRK_YN"			);
			outRec.setField("YD_SCH_CD", 			JPlateYdUtils.fillSpZr(szYdSchCd, 8, 1)	);

			//=======================================================================================================================
			// [2] 스케쥴 기준 테이블 조회
			//=======================================================================================================================
			if (!"".equals(szYdSchCd.trim())) {
				recPara = JDTORecordFactory.getInstance().create();
				rsRule  = JDTORecordFactory.getInstance().createRecordSet("");
				recPara.setField("YD_SCH_CD", szYdSchCd);
				intRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsRule);
				if (intRtnVal < 0) {
					szMsg = "스케쥴 기준 테이블 조회오류 (" + szYdSchCd + ")" + "[Ret : " + Integer.toString(intRtnVal) + "]";
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

					szYdSchName = JPlateYdUtils.fillSpZrKor(" ", 30, 1	);
					szYdEqpId2  = JPlateYdUtils.fillSpZr(" ", 6, 1		);
				} else if (intRtnVal == 0) {
					szMsg = "스케쥴 기준 테이블 조회건수 없음 (" + szYdSchCd + ")" + "[Ret : " + Integer.toString(intRtnVal) + "]";
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					szYdSchName = JPlateYdUtils.fillSpZrKor(" ", 30, 1	);
					szYdEqpId2  = JPlateYdUtils.fillSpZr(" ", 6, 1		);
				} else {
					rsRule.first();
					recRule = rsRule.getRecord();

					if ("Y".equals(szYdAidWrkYn)) {
						szCdContents = "[보조]" + ydDaoUtils.paraRecChkNull(recRule, "CD_CONTENTS");
					} else {
						szCdContents = ydDaoUtils.paraRecChkNull(recRule, "CD_CONTENTS");
					}
					szYdSchName = JPlateYdUtils.fillSpZrKor(szCdContents, 30, 1			);
					szYdEqpId2  = JPlateYdUtils.fillSpZrRec(recRule, "YD_ALT_CRN", 6, 1	);
				}
			} else {
				szMsg = "스케쥴 코드가 없어서 기준테이블을 조회 안함 : YD_SCH_NAME, YD_EQP_ID2는 공백";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				szYdSchName = JPlateYdUtils.fillSpZrKor(" ", 30, 1	);
				szYdEqpId2  = JPlateYdUtils.fillSpZr(" ", 6, 1		);
			}

			// 야드스케쥴명
			outRec.setField("YD_SCH_NAME", 				szYdSchName														);

			// 야드크레인스케쥴ID [야드크레인스케쥴ID]
			outRec.setField("YD_CRN_SCH_ID", 			JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_CRN_SCH_ID", 18, 1)	);

			// 야드크레인작업매수 [야드크레인작업매수]
			outRec.setField("YD_CRN_WRK_SH", 			JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_SH", 2, 0)		);

			// 야드크레인작업중량 [야드크레인작업중량]
			outRec.setField("YD_CRN_WRK_WT", 			JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_WT", 7, 0)		);

			// 야드크레인작업총두께 [야드크레인작업총두께]
			szConv = JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_T", 8, 1);
			outRec.setField("YD_CRN_WRK_T", 			ydUtils.floatLRPAD(szConv, 7, 3, '0')							);

			// 야드크레인작업최대폭 [야드크레인작업최대폭]
			szConv = JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_MAX_W", 6, 1);
			outRec.setField("YD_CRN_WRK_MAX_W", 		ydUtils.floatLRPAD(szConv, 5, 1, '0')							);

			// 야드크레인작업최대길이 [야드크레인작업최대길이]
			outRec.setField("YD_CRN_WRK_MAX_L", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_MAX_L", 7, 0)	);

			//*************************************************************************************************************************************
			// 야드크레인스케줄잔여회수 [야드크레인스케줄잔여회수]
			outRec.setField("YD_CRN_SCH_RMD_CNT", 		JPlateYdUtils.fillSpZr("00", 2, 1)								);
			//*************************************************************************************************************************************

			// 사용안하는 항목 ..
			outRec.setField("YD_CRN_GRAB_GP", 				JPlateYdUtils.fillSpZr("D", 1, 1));
			outRec.setField("YD_CRN_GRAB_USE_GP", 			JPlateYdUtils.fillSpZr("1", 1, 1));
			outRec.setField("YD_CRN_TT1_GRAB_NEW_AXIS_L", 	JPlateYdUtils.fillSpZr("0", 4, 0));
			outRec.setField("YD_CRN_TT2_GRAB_NEW_AXIS_L",	JPlateYdUtils.fillSpZr("0", 4, 0));
			outRec.setField("YD_CRN_MGNT_USE_EA", 			JPlateYdUtils.fillSpZr("0", 2, 0));
			outRec.setField("YD_CRN_MGNT_USE_YN1", 			JPlateYdUtils.fillSpZr("Y", 1, 1));
			outRec.setField("YD_CRN_MGNT_USE_YN2", 			JPlateYdUtils.fillSpZr("Y", 1, 1));
			outRec.setField("YD_CRN_MGNT_USE_YN3", 			JPlateYdUtils.fillSpZr("Y", 1, 1));
			outRec.setField("YD_CRN_MGNT_USE_YN4", 			JPlateYdUtils.fillSpZr("Y", 1, 1));
			outRec.setField("YD_CRN_MGNT_USE_YN5", 			JPlateYdUtils.fillSpZr("Y", 1, 1));
			outRec.setField("YD_CRN_MGNT_USE_YN6", 			JPlateYdUtils.fillSpZr("Y", 1, 1));
			outRec.setField("YD_CRN_MGNT_USE_YN7", 			JPlateYdUtils.fillSpZr("Y", 1, 1));
			outRec.setField("YD_CRN_MGNT_USE_YN8", 			JPlateYdUtils.fillSpZr("Y", 1, 1));
			outRec.setField("YD_CRN_MGNT_USE_YN9", 			JPlateYdUtils.fillSpZr("Y", 1, 1));
			outRec.setField("YD_CRN_MGNT_USE_YN10", 		JPlateYdUtils.fillSpZr("Y", 1, 1));

			// 야드권상지시위치 [야드권상지시위치]
			outRec.setField("YD_UP_WO_LOC", 			JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LOC", 8, 1)			);

			// 야드권상지시단 [야드권상지시단]
			outRec.setField("YD_UP_WO_LAYER", 			JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LAYER", 3, 1)		);

			// 야드권상지시X축[야드권상지시X축]
			outRec.setField("YD_UP_WO_LOC_XAXIS", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LOC_XAXIS", 7, 0)	);

			// 야드권상지시X축오차최대 [야드권상지시X축오차최대]
			outRec.setField("YD_UP_WO_XAXIS_GAP_MAX", 	JPlateYdUtils.fillSpZr(szYD_UP_WO_XAXIS_GAP_MAX, 5, 0)				);
			// 야드권상지시X축오차최소 [야드권상지시X축오차최소]
			outRec.setField("YD_UP_WO_XAXIS_GAP_MIN", 	JPlateYdUtils.fillSpZr(szYD_UP_WO_XAXIS_GAP_MIN, 5, 0)				);

			// 야드권상지시Y축 [야드권상지시Y축]
			outRec.setField("YD_UP_WO_LOC_YAXIS", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LOC_YAXIS", 5, 0)	);
		//	outRec.setField("YD_UP_WO_LOC_YAXIS1", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LOC_YAXIS", 5, 0)	);
		//	outRec.setField("YD_UP_WO_LOC_YAXIS2", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LOC_YAXIS", 5, 0)	);
			outRec.setField("YD_UP_WO_LOC_YAXIS1", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LOC_YAXIS1", 5, 0)	);
			outRec.setField("YD_UP_WO_LOC_YAXIS2", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LOC_YAXIS2", 5, 0)	);

			// 야드권상지시Y축오차최대 [야드권상지시Y축오차최대]
			outRec.setField("YD_UP_WO_YAXIS_GAP_MAX", 	JPlateYdUtils.fillSpZr(szYD_UP_WO_YAXIS_GAP_MAX, 5, 0)				);
			// 야드권상지시Y축오차최소 [야드권상지시Y축오차최소]
			outRec.setField("YD_UP_WO_YAXIS_GAP_MIN", 	JPlateYdUtils.fillSpZr(szYD_UP_WO_YAXIS_GAP_MIN, 5, 0)				);

			// 야드권상지시Z축 [야드권상지시Z축]
			outRec.setField("YD_UP_WO_LOC_ZAXIS", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LOC_ZAXIS", 5, 0)	);

			// 야드권상지시Z축오차최대 [야드권상지시Z축오차최대]
			outRec.setField("YD_UP_WO_ZAXIS_GAP_MAX", 	JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_ZAXIS_GAP_MAX", 5, 0));

			// 야드권상지시Z축오차최소 [야드권상지시Z축오차최소]
			outRec.setField("YD_UP_WO_ZAXIS_GAP_MIN", 	JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_ZAXIS_GAP_MIN", 5, 0));

			// 야드권하지시위치 [야드권하지시위치]
			outRec.setField("YD_DN_WO_LOC", 			JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LOC", 8, 1)			);

			// 야드권하지시단 [야드권하지시단]
			outRec.setField("YD_DN_WO_LAYER", 			JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LAYER", 3, 1)		);

			// 야드권하지시X축 [야드권하지시X축]
			outRec.setField("YD_DN_WO_LOC_XAXIS", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LOC_XAXIS", 7, 0)	);

			// 야드권하지시X축오차최대 [야드권하지시X축오차최대]
			outRec.setField("YD_DN_WO_XAXIS_GAP_MAX", 	JPlateYdUtils.fillSpZr(szYD_DN_WO_XAXIS_GAP_MAX, 5, 0)				);
			// 야드권하지시X축오차최소 [야드권하지시X축오차최소]
			outRec.setField("YD_DN_WO_XAXIS_GAP_MIN", 	JPlateYdUtils.fillSpZr(szYD_DN_WO_XAXIS_GAP_MIN, 5, 0)				);

			// 야드권하지시Y축 [야드권하지시Y축]
			outRec.setField("YD_DN_WO_LOC_YAXIS", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LOC_YAXIS", 5, 0)	);
		//	outRec.setField("YD_DN_WO_LOC_YAXIS1", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LOC_YAXIS", 5, 0));
		//	outRec.setField("YD_DN_WO_LOC_YAXIS2", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LOC_YAXIS", 5, 0));
			outRec.setField("YD_DN_WO_LOC_YAXIS1", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LOC_YAXIS1", 5, 0)	);
			outRec.setField("YD_DN_WO_LOC_YAXIS2", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LOC_YAXIS2", 5, 0)	);

			// 야드권하지시Y축오차최대 [야드권하지시Y축오차최대]
			outRec.setField("YD_DN_WO_YAXIS_GAP_MAX", 	JPlateYdUtils.fillSpZr(szYD_DN_WO_YAXIS_GAP_MAX, 5, 0)				);
			// 야드권하지시Y축오차최소 [야드권하지시Y축오차최소]
			outRec.setField("YD_DN_WO_YAXIS_GAP_MIN", 	JPlateYdUtils.fillSpZr(szYD_DN_WO_YAXIS_GAP_MIN, 5, 0)				);

			// 야드권하지시Z축 [야드권하지시Z축]
			outRec.setField("YD_DN_WO_LOC_ZAXIS", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LOC_ZAXIS", 5, 0)	);

			// 야드권하지시Z축오차최대 [야드권하지시Z축오차최대]
			outRec.setField("YD_DN_WO_ZAXIS_GAP_MAX", 	JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_ZAXIS_GAP_MAX", 5, 0));
			// 야드권하지시Z축오차최소 [야드권하지시Z축오차최소]
			outRec.setField("YD_DN_WO_ZAXIS_GAP_MIN", 	JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_ZAXIS_GAP_MIN", 5, 0));

			// 야드설비ID2 [야드설비ID2]
			outRec.setField("YD_EQP_ID2", 				szYdEqpId2															);

			//=======================================================================================================================
			// [3.1] 작업예약 조회
			//=======================================================================================================================
			recPara   = JDTORecordFactory.getInstance().create();
			rsWrkBook = JDTORecordFactory.getInstance().createRecordSet("yd");
			recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID"));
			intRtnVal = ydWrkbookDao.getYdWrkbookMtl(recPara, rsWrkBook);

			szMsg = "작업예약 테이블 조회 [Ret : " + Integer.toString(intRtnVal) + "]";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//=======================================================================================================================
			// [3.1.2] 긴급재 리스트 조회
			//=======================================================================================================================
			rsUgntList= commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getUgntGpList", logId, szMethodName, "긴급재리스트 조회");
			String UGNT_LIST="";  //YYNNYYNN...  재료번호에 따른 15자리 긴급재 FLAG
			if(rsUgntList.size()>0){
				UGNT_LIST=rsUgntList.getRecord(0).getFieldString("YD_UGNT_LIST");  
			}			
			
			if (intRtnVal < 0) {
				szMsg = "작업예약 테이블 조회오류 [Ret : " + Integer.toString(intRtnVal) + "]";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

				// 야드대차목적동[야드대차목적동]
				outRec.setField("YD_TC_AIM_BAY_GP", 	JPlateYdUtils.fillSpZr(" ", 1, 1)			);

				// 야드차량사용구분 [야드차량사용구분]
				outRec.setField("YD_CAR_USE_GP", 		JPlateYdUtils.fillSpZr(" ", 1, 1)			);

				// 차량번호 [차량번호]
				//outRec.setField("CAR_NO", 				JPlateYdUtils.fillSpZrKor(" ", 15, 1));
				outRec.setField("CAR_NO", 				JPlateYdUtils.fillSpZr(UGNT_LIST, 15, 1)	);  //차량번호15자리에 재료별 긴급재 FLAG 사용
				// 운송장비코드 [운송장비코드]
				outRec.setField("TRN_EQP_CD", 			JPlateYdUtils.fillSpZr(" ", 8, 1)			);
			} else if (intRtnVal == 0) {
				szMsg = "작업예약 테이블 조회건수 없음 [Ret : " + Integer.toString(intRtnVal) + "]";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				// 야드대차목적동[야드대차목적동]
				outRec.setField("YD_TC_AIM_BAY_GP", 	JPlateYdUtils.fillSpZr(" ", 1, 1)			);

				// 야드차량사용구분 [야드차량사용구분]
				outRec.setField("YD_CAR_USE_GP", 		JPlateYdUtils.fillSpZr(" ", 1, 1)			);

				// 차량번호 [차량번호]
				//outRec.setField("CAR_NO", 				JPlateYdUtils.fillSpZrKor(" ", 15, 1));
				outRec.setField("CAR_NO", 				JPlateYdUtils.fillSpZr(UGNT_LIST, 15, 1)	);  //차량번호15자리에 재료별 긴급재 FLAG 사용
				// 운송장비코드 [운송장비코드]
				outRec.setField("TRN_EQP_CD", 			JPlateYdUtils.fillSpZr(" ", 8, 1)			);
			} else {
				rsWrkBook.first();
				recWrkBook = rsWrkBook.getRecord();

				// 야드대차목적동[야드대차목적동]
				outRec.setField("YD_TC_AIM_BAY_GP", 	JPlateYdUtils.fillSpZrRec(recWrkBook, "YD_AIM_BAY_GP", 1, 1)	);

				// 야드차량사용구분 [야드차량사용구분]
				outRec.setField("YD_CAR_USE_GP", 		JPlateYdUtils.fillSpZrRec(recWrkBook, "YD_CAR_USE_GP", 1, 1)	);

				// 차량번호 [차량번호]
				//outRec.setField("CAR_NO", 				JPlateYdUtils.fillSpZrKor(ydDaoUtils.paraRecChkNull(recWrkBook, "CAR_NO"), 15, 1));
				outRec.setField("CAR_NO", 				JPlateYdUtils.fillSpZr(UGNT_LIST, 15, 1)						);  //차량번호15자리에 재료별 긴급재 FLAG 사용
				// 운송장비코드 [운송장비코드]
				outRec.setField("TRN_EQP_CD", 			JPlateYdUtils.fillSpZrRec(recWrkBook, "TRN_EQP_CD", 8, 1)		);
			}

			// 야드설비작업매수 [야드설비작업매수]
			outRec.setField("YD_EQP_WRK_SH", 			JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_SH", 2, 0)		);

			//*************************************************************************************************************************************
			// 야드설비잔량매수[야드설비잔량매수]
			outRec.setField("YD_EQP_RMN_SH", 			JPlateYdUtils.fillSpZr("0", 2, 0)								);
			//*************************************************************************************************************************************

			//=======================================================================================================================
			// [3.2] 크레인작업재료 조회
			//=======================================================================================================================
			recPara  = JDTORecordFactory.getInstance().create();
			rsCrnMtl = JDTORecordFactory.getInstance().createRecordSet("yd");
			recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID"));
			//intRtnVal = ydCrnSchDao.getYdCrnWrkMtlDesc(recPara, rsCrnMtl);				// 적치단 역순으로 조회
			if("PCRT40LM".equals(szYdSchCd)) {
				rsCrnMtl = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnWrkMtlDesc3", logId, szMethodName, "[3.2] 크레인작업재료 조회 (C동 WB 출측 BOOK-OUT(00012))");
			} else if("XX010101".equals(szYdDnWoLoc)) {
				rsCrnMtl = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnWrkMtlDesc3", logId, szMethodName, "[3.2] 크레인작업재료 조회 (권하지시위치가 XX010101)");
				//ORDER BY NVL(Z.YD_DN_WO_LAYER,Z.YD_STK_LYR_NO) DESC, DECODE(Z.YD_DN_WO_LOC,'XX010101',Z.YD_STK_LOT_TP,Z.YD_DN_WO_LOC), Z.STL_NO
			} else {
				rsCrnMtl = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnWrkMtlDesc2", logId, szMethodName, "[3.2] 크레인작업재료 조회 (Key: 크레인스케쥴 ID)");
			}
			intRtnVal = rsCrnMtl.size();

			szMsg = "크레인작업재료 조회 .... 조회완료 >>>> 건수 ::" + intRtnVal;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			intRtnVal = (intRtnVal >= 15) ? 15 : intRtnVal;
			
			for(nIdx=0; nIdx<intRtnVal; nIdx++) {
				recCrnMtl = rsCrnMtl.getRecord(nIdx);

				// 재료번호 [재료번호]
				outRec.setField("STL_NO"+Integer.toString(1+nIdx), 			JPlateYdUtils.fillSpZrRec(recCrnMtl, "STL_NO", 11, 1)		);

				// 파일링코드
				outRec.setField("YD_PILING_CD"+Integer.toString(1+nIdx),	JPlateYdUtils.fillSpZrRec(recCrnMtl, "YD_PILING_CD", 8, 1)	);

				// 야드재료중량 [야드재료중량]
				outRec.setField("YD_STL_WT"+Integer.toString(1+nIdx), 		JPlateYdUtils.fillSpZrRec(recCrnMtl, "YD_MTL_WT", 5, 0)		);

				// 야드재료두께 [야드재료두께]
				szConv = JPlateYdUtils.fillSpZrRec(recCrnMtl, "YD_MTL_T", 7, 1);
				outRec.setField("YD_STL_T"+Integer.toString(1+nIdx), 		ydUtils.floatLRPAD(szConv, 6, 3, '0')						);

				// 야드재료폭 [야드재료폭]
				szConv = JPlateYdUtils.fillSpZrRec(recCrnMtl, "YD_MTL_W", 6, 1);
				outRec.setField("YD_STL_W"+Integer.toString(1+nIdx), 		ydUtils.floatLRPAD(szConv, 5, 1, '0')						);

				// 야드재료길이 [야드재료길이]
				outRec.setField("YD_STL_L"+Integer.toString(1+nIdx), 		JPlateYdUtils.fillSpZrRec(recCrnMtl, "YD_MTL_L", 7, 0)		);

				// 권하지시위치 [야드적치열+베드]
				outRec.setField("YD_DN_WO_LOC"+Integer.toString(1+nIdx), 	JPlateYdUtils.fillSpZrRec(recCrnMtl, "YD_DN_WO_LOC", 8, 1)	);

				// 권하지시단 [야드적치단]
				outRec.setField("YD_DN_WO_LAYER"+Integer.toString(1+nIdx),	JPlateYdUtils.fillSpZrRec(recCrnMtl, "YD_DN_WO_LAYER", 3, 1));

				// 재료 매수 두께, 중량  합계산 - 강제권상시 재료합과 상이한 경우 발생 .. 안정화후 삭제 필요
				iYdEqpWrkSh ++;
				iYdMtlWt	= iYdMtlWt + ydDaoUtils.paraRecChkNullInt(recCrnMtl,    "YD_MTL_WT");
				dYdMtlT     = dYdMtlT  + ydDaoUtils.paraRecChkNullDouble(recCrnMtl, "YD_MTL_T");
				
				//작업지시내 재료들의 현공정코드 merge값
				szPL_WR_PRSNT_PROC_CD = szPL_WR_PRSNT_PROC_CD + JPlateYdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recCrnMtl,    "PL_WR_PRSNT_PROC_CD"),2,1);
			}

			szMsg = "재료정보 .... 합계 >>>> 매수 :: " + iYdEqpWrkSh + "두께 :: " + dYdMtlT + "중량 :: " + iYdMtlWt;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			
			szMsg = "공정코드  merge >>>>" + szPL_WR_PRSNT_PROC_CD;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			// ---- 재료별로 누적하여 다시 SET함
			// 야드크레인작업매수 [야드크레인작업매수]
			outRec.setField("YD_CRN_WRK_SH", 			JPlateYdUtils.fillSpZr(String.valueOf(iYdEqpWrkSh), 2, 0)	);
			// 야드크레인작업중량 [야드크레인작업중량]
			outRec.setField("YD_CRN_WRK_WT", 			JPlateYdUtils.fillSpZr(String.valueOf(iYdMtlWt), 7, 0)		);
			// 야드크레인작업총두께 [야드크레인작업총두께]
			// 2013.08.12 윤재광 수정 >>>> 2013.09.09 보완 완료
			// 항목에 null로 들어가서 막음(예: dYdMtlT값이 = 38.09999999994 이렇게 찍힘)
			outRec.setField("YD_CRN_WRK_T", 			ydUtils.floatLRPAD(String.valueOf(dYdMtlT), 7, 3, '0')		);

			// 공백 건수처리
			for(int ii=nIdx; ii<15; ii++) {
				// 재료번호 [재료번호]
				outRec.setField("STL_NO"+Integer.toString(1+ii), 			JPlateYdUtils.fillSpZr(" ", 11, 1));

				// 파일링코드
				outRec.setField("YD_PILING_CD"+Integer.toString(1+ii),		JPlateYdUtils.fillSpZr(" ", 8, 1));

				// 야드재료중량 [야드재료중량]
				outRec.setField("YD_STL_WT"+Integer.toString(1+ii), 		JPlateYdUtils.fillSpZr(" ", 5, 1));

				// 야드재료두께 [야드재료두께]
				outRec.setField("YD_STL_T"+Integer.toString(1+ii), 			JPlateYdUtils.fillSpZr(" ", 6, 1));

				// 야드재료폭 [야드재료폭]
				outRec.setField("YD_STL_W"+Integer.toString(1+ii), 			JPlateYdUtils.fillSpZr(" ", 5, 1));

				// 야드재료길이 [야드재료길이]
				outRec.setField("YD_STL_L"+Integer.toString(1+ii), 			JPlateYdUtils.fillSpZr(" ", 7, 1));

				// 권하지시위치 [야드적치열+베드]
				outRec.setField("YD_DN_WO_LOC"+Integer.toString(1+ii), 		JPlateYdUtils.fillSpZr(" ", 8, 1));

				// 권하지시단 [야드적치단]
				outRec.setField("YD_DN_WO_LAYER"+Integer.toString(1+ii),	JPlateYdUtils.fillSpZr(" ", 3, 1));

			}

			rsCrnSch.first();
			recCrnSch = rsCrnSch.getRecord();

			// 야드스케쥴코드_Next [야드스케쥴코드_Next]
			szYdSchCdNext = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD_NEXT");
			outRec.setField("YD_SCH_CD_NEXT", JPlateYdUtils.fillSpZr(szYdSchCdNext, 8, 1));

			//=======================================================================================================================
			// [4] 스케쥴 기준 테이블 조회
			//=======================================================================================================================
			/* NEXT 스케줄코드명칭은 사용하지 않고, 해당 위치에 재료들의 공정코드 MERGE 값을 대체해서 사용. 2022.02.08 박종호
			if (!"".equals(szYdSchCdNext.trim())) {
				recPara = JDTORecordFactory.getInstance().create();
				rsRule  = JDTORecordFactory.getInstance().createRecordSet("");
				recPara.setField("YD_SCH_CD", szYdSchCdNext);
				intRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsRule);
				if (intRtnVal < 0) {
					szMsg = "스케쥴 기준 테이블_NEXT 조회오류 (" + szYdSchCdNext + ")" + "[Ret : " + Integer.toString(intRtnVal) + "]";
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

					szYdSchNameNext = JPlateYdUtils.fillSpZrKor(" ", 30, 1);
				} else if (intRtnVal == 0) {
					szMsg = "스케쥴 기준 테이블_NEXT 조회건수 없음 (" + szYdSchCdNext + ")" + "[Ret : " + Integer.toString(intRtnVal) + "]";
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					szYdSchNameNext = JPlateYdUtils.fillSpZrKor(" ", 30, 1);
				} else {
					rsRule.first();
					recRule = rsRule.getRecord();

					szYdSchNameNext = JPlateYdUtils.fillSpZrKor(ydDaoUtils.paraRecChkNull(recRule, "CD_CONTENTS"), 30, 1);
				}
			} else {
				szMsg = "스케쥴 코드_NEXT가 없어서 기준테이블을 조회 안함 : YD_SCH_NAME_NEXT는 공백";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				szYdSchNameNext = JPlateYdUtils.fillSpZrKor(" ", 30, 1);
			}

			// 야드스케쥴명_Next [야드스케쥴명_Next]
			outRec.setField("YD_SCH_NAME_NEXT", 		szYdSchNameNext);
			*/
			
			// 야드스케쥴명_Next [야드스케쥴명_Next]
			outRec.setField("YD_SCH_NAME_NEXT", 		JPlateYdUtils.fillSpZr(szPL_WR_PRSNT_PROC_CD, 30, 1)				);
			
			// 야드권상지시위치_Next [야드권상지시위치_Next]
			outRec.setField("YD_UP_WO_LOC_NEXT", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LOC_NEXT", 8, 1)		);

			// 야드권상지시단_Next [야드권상지시단_Next]
			outRec.setField("YD_UP_WO_LAYER_NEXT", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_UP_WO_LAYER_NEXT", 3, 1)	);

			// 야드권하지시위치_Next [야드권하지시위치_Next]
			outRec.setField("YD_DN_WO_LOC_NEXT", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LOC_NEXT", 8, 1)		);

			// 야드권하지시단_Next [야드권하지시단_Next]
			outRec.setField("YD_DN_WO_LAYER_NEXT", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_DN_WO_LAYER_NEXT", 3, 1)	);

			// 재료번호_Next [재료번호_Next]
			outRec.setField("STL_NO_NEXT", 				JPlateYdUtils.fillSpZrRec(recCrnSch, "STL_NO_NEXT", 11, 1)			);

			// 야드크레인작업매수_Next [야드설비작업매수]
			outRec.setField("YD_CRN_WRK_SH_NEXT", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_SH_NEXT", 2, 9)	);

			// 야드크레인작업중량_Next [야드크레인작업중량_Next]
			outRec.setField("YD_CRN_WRK_WT_NEXT", 		JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_WT_NEXT", 7, 9)	);

			// 야드크레인작업총두께_Next [야드크레인작업총두께_Next]
			szConv = JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_T_NEXT", 8, 1);
			szConv = ydUtils.floatLRPAD(szConv, 7, 3, '0');
			if ("0000000".equals(szConv)) {
				outRec.setField("YD_CRN_WRK_T_NEXT", 	JPlateYdUtils.fillSpZr("", 7, 1)									);
			} else {
				outRec.setField("YD_CRN_WRK_T_NEXT", 	szConv																);
			}

			// 야드크레인작업최대폭_Next [야드크레인작업최대폭_Next]
			szConv = JPlateYdUtils.fillSpZrRec(recCrnSch, 	"YD_EQP_WRK_MAX_W_NEXT", 6, 1);
			szConv = ydUtils.floatLRPAD(szConv, 5, 1, '0');
			if ("00000".equals(szConv)) {
				outRec.setField("YD_CRN_WRK_MAX_W_NEXT", 	JPlateYdUtils.fillSpZr("", 5, 1)								);
			} else {
				outRec.setField("YD_CRN_WRK_MAX_W_NEXT", 	szConv															);
			}

			// 야드크레인작업최대길이_Next [야드크레인작업최대길이_Next]
			outRec.setField("YD_CRN_WRK_MAX_L_NEXT", 	JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_EQP_WRK_MAX_L_NEXT", 7, 9)	);

			//	198	파일링구분 		[YD_PILING_GP] P:파일링, H:횡행작업, N:일반작업, M:멀티, F:강제권상
			szydPilingGp = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WRK_ACT_GP", "N");
			if ("FU".equals(ydUtils.substr(szYdSchCd, 2, 2))) {
				szydPilingGp = "F";
			}
			outRec.setField("YD_PILING_GP", 			JPlateYdUtils.fillSpZr(szydPilingGp, 1, 1)							);
			//	199	야드스케쥴우선순위 	[YD_SCH_PRIOR]
			outRec.setField("YD_SCH_PRIOR", 			JPlateYdUtils.fillSpZrRec(recCrnSch, "YD_SCH_PRIOR", 2, 0)			);

			// TO야드저장위치 정보 SET :: YD_TO_LOC1, YD_TO_LAYER1, YD_TO_STAT1, YD_TO_STL_NO1
			recPara = JDTORecordFactory.getInstance().create();
			rsToLoc = JDTORecordFactory.getInstance().createRecordSet("Temp");

			recPara.setField("YD_STK_COL_GP", ydUtils.substr(szYdDnWoLoc, 0, 6)	);
			recPara.setField("YD_STK_LYR_NO", szYdDnWoLayer						);

			intRtnVal = ydStkLyrDao.getToLocInfo(recPara, rsToLoc);
			nToLocCnt = (intRtnVal >= 10) ? 10 : intRtnVal;

			for(int ii=0; ii<nToLocCnt; ii++) {
				rsToLoc.absolute(ii+1);
				recToLoc = rsToLoc.getRecord();

				//	200	TO야드저장위치		[YD_TO_LOC]		VARCHAR2(8)
				outRec.setField("YD_TO_LOC"+(ii+1), 	JPlateYdUtils.fillSpZrRec(recToLoc, "YD_TO_LOC", 8, 1)		);

				//	201	TO야드적치단		[YD_TO_LAYER]	VARCHAR2(3)
				outRec.setField("YD_TO_LAYER"+(ii+1), 	JPlateYdUtils.fillSpZrRec(recToLoc, "YD_TO_LAYER", 3, 0)	);

				//	202	TO위치재료적치상태	[YD_TO_STAT]	VARCHAR2(1)		E:가능,C:적치,V:점유,N:불가
				outRec.setField("YD_TO_STAT"+(ii+1), 	JPlateYdUtils.fillSpZrRec(recToLoc, "YD_TO_STAT", 1, 1)		);

				//	203	TO위치재료번호		[YD_TO_STL_NO]	VARCHAR2(11)
				outRec.setField("YD_TO_STL_NO"+(ii+1),	JPlateYdUtils.fillSpZrRec(recToLoc, "YD_TO_STL_NO", 11, 1)	);
			}

			// 공백처리
			for(int ii=nToLocCnt; ii<10; ii++) {
				outRec.setField("YD_TO_LOC"+(ii+1), 	JPlateYdUtils.fillSpZr("",  8, 1)	);
				outRec.setField("YD_TO_LAYER"+(ii+1), 	JPlateYdUtils.fillSpZr("",  3, 1)	);
				outRec.setField("YD_TO_STAT"+(ii+1), 	JPlateYdUtils.fillSpZr("",  1, 1)	);
				outRec.setField("YD_TO_STL_NO"+(ii+1),	JPlateYdUtils.fillSpZr("", 11, 1)	);
			}

			// FROM야드저장위치 정보 SET :: YD_FROM_LOC1, YD_FROM_LAYER1, YD_FROM_STAT1, YD_FROM_STL_NO1
			recPara = JDTORecordFactory.getInstance().create();
			rsFrLoc = JDTORecordFactory.getInstance().createRecordSet("Temp");

			recPara.setField("YD_STK_COL_GP", ydUtils.substr(szYdUpWoLoc, 0, 6)		);
			recPara.setField("YD_STK_LYR_NO", szYdUpWoLayer							);

			intRtnVal = ydStkLyrDao.getFromLocInfo(recPara, rsFrLoc);
			nFrLocCnt = (intRtnVal >= 10) ? 10 : intRtnVal;

			for(int ii=0; ii<nFrLocCnt; ii++) {
				rsFrLoc.absolute(ii+1);
				recFrLoc = rsFrLoc.getRecord();

				//	240	FROM야드저장위치		[YD_FROM_LOC]	VARCHAR2(8)
				outRec.setField("YD_FROM_LOC"+(ii+1), 		JPlateYdUtils.fillSpZrRec(recFrLoc, "YD_FROM_LOC", 8, 1)		);

				//	241	FROM야드적치단		[YD_FROM_LAYER]	VARCHAR2(3)
				outRec.setField("YD_FROM_LAYER"+(ii+1), 	JPlateYdUtils.fillSpZrRec(recFrLoc, "YD_FROM_LAYER", 3, 0)		);

				//	242	FROM위치재료적치상태	[YD_FROM_STAT]	VARCHAR2(1)		E:가능,C:적치,V:점유,N:불가
				outRec.setField("YD_FROM_STAT"+(ii+1), 		JPlateYdUtils.fillSpZrRec(recFrLoc, "YD_FROM_STAT", 1, 1)		);

				//	243	FROM위치재료번호		[YD_FROM_STL_NO]VARCHAR2(11)
				outRec.setField("YD_FROM_STL_NO"+(ii+1),	JPlateYdUtils.fillSpZrRec(recFrLoc, "YD_FROM_STL_NO", 11, 1)	);
			}

			// 공백처리
			for(int ii=nFrLocCnt; ii<10; ii++) {
				outRec.setField("YD_FROM_LOC"+(ii+1), 		JPlateYdUtils.fillSpZr("",  8, 1)	);
				outRec.setField("YD_FROM_LAYER"+(ii+1), 	JPlateYdUtils.fillSpZr("",  3, 1)	);
				outRec.setField("YD_FROM_STAT"+(ii+1), 		JPlateYdUtils.fillSpZr("",  1, 1)	);
				outRec.setField("YD_FROM_STL_NO"+(ii+1),	JPlateYdUtils.fillSpZr("", 11, 1)	);
			}

			//	280 야드권상지시위치명		YD_UP_WO_LOC_NM			VARCHAR2(12)
			outRec.setField("YD_UP_WO_LOC_NM",	JPlateYdUtils.fillSpZrKor(ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC_NM"), 12, 1));

			//	281	야드권하지시위치명		YD_DN_WO_LOC_NM			VARCHAR2(12)
			outRec.setField("YD_DN_WO_LOC_NM",	JPlateYdUtils.fillSpZrKor(ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC_NM"), 12, 1));

			// RecordSet에 추가
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n======================makeYDY2L004() OUT========================\n", JPlateYdConst.DEBUG, logId);
			ydUtils.displayRecord(szOperationName, outRec);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG, logId);
		}catch(Exception e) {
			szMsg = "Y2(1후판정정야드L2) 송신  크레인작업지시  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			return -1;
		}

		return outRecSet.size();
	} // end of makeYDY2L004V2()
	
} // end of class MaktTcY2
