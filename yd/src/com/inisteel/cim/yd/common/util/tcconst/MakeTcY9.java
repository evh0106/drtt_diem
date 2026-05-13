/**
 * @(#)MakeTcY9.java
 *
 * @version			1.0
 * @author 			조병기
 * @date			2013.05.07
 *
 * @description		Y9 (1후판전단PM45L2) 송신 용 전문 생성 클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용 
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2013.05.07   조병기      조병기      최초 등록 
 * V2.00  2021.05.07   윤재광      김광철      전사물류시스템개선 통합후판제품 크레인자동화 Y9모듈로 대체함(기존전문사용안함으로 인함) 
 */

package com.inisteel.cim.yd.common.util.tcconst;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSpecDao.YdCrnSpecDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

public class MakeTcY9 {

	// 클래스명
	private static final String SZ_CLASS_NAME  = MakeTcY9.class.getName();
	private static YdPICommDAO   ydPICommDAO   = new YdPICommDAO();

	/**
	 * YDY9L001 : 저장위치제원
	 * @param JDTORecord inRec
	 * @return JDTORecordSet outRecSet
	 */
	public static int makeY9L001(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDY9L001
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER  (4)
		//		6.	임시						TEMP					VARCHAR2(29)

		//		7.	야드정보동기화코드			YD_INFO_SYNC_CD			VARCHAR2(1)		1:동,2:SPAN,3:열,4:BED
		//		8.	야드구분					YD_GP					VARCHAR2(1)		"D"
		//		9.	야드동구분				YD_BAY_GP				VARCHAR2(1)		부하를 방지하기 위해 최대 동 단위를 허용함
		//		10.	야드설비구분				YD_EQP_GP				VARCHAR2(2)		야드의 설비, Span을 코드로 부여한 Data
		//		11.	야드적치열번호				YD_STK_COL_NO			VARCHAR2(2)		숫자 0을 포함하여 관리 ("01")
		//		12.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		숫자 0을 포함하여 관리 ("01")
		//		13.	야드적치Bed길이구분		YD_STK_BED_L_GP			VARCHAR2(1)		"S" 단척, "M" 중척, "L" 장척
		//		14.	야드적치Bed폭구분			YD_STK_BED_W_GP			VARCHAR2(1)		"N" 협폭, "M" 보폭, "W" 광폭
		//		15.	야드적치Bed방향구분		YD_STK_BED_DIR_GP		VARCHAR2(1)		"X" 주행, "Y" 횡행
		//		16.	야드적치Bed활성상태		YD_STK_BED_ACT_STAT		VARCHAR2(1)		C:비활성화, "L":적치가능, "N":사용불가
		//		17.	야드적치Bed입출고상태		YD_STK_BED_WHIO_STAT	VARCHAR2(1)		완산여부
		//		18.	야드적치BedX축			YD_STK_BED_XAXIS		NUMBER  (7)		Center 지점
		//		19.	야드적치BedY축			YD_STK_BED_YAXIS		NUMBER  (5)		Center 지점
		//		20.	야드적치BedZ축			YD_STK_BED_ZAXIS		NUMBER  (5)		최하단 바닥 높이
		//		21.	야드적치Bed단Max			YD_STK_BED_LYR_MAX		NUMBER  (3)
		//		22.	야드적치Bed중량Max			YD_STK_BED_WT_MAX		NUMBER  (7)
		//		23.	야드적치Bed높이Max			YD_STK_BED_H_MAX		NUMBER  (5)
		//		24.	야드적치Bed길이Max			YD_STK_BED_L_MAX		NUMBER  (5)
		//		25.	야드적치Bed폭Max			YD_STK_BED_W_MAX		NUMBER  (5,1)		9999.9(소수점 없는 유효 Data)
		//		26.	야드차량착발상태			YD_CAR_ARRSTRT_STAT		VARCHAR2(1)		"A": 도착,              "S": 출발
		//		27.	야드차량사용구분			YD_CAR_USE_GP			VARCHAR2(1)		"L" :구내운송차량,  "G": 제품출하차량
		//		28.	야드설비작업상태			YD_EQP_WRK_STAT			VARCHAR2(1)		"L" : 공차(출하),    "U" : 영차(반입)
		//		29.	차량번호					CAR_NO					VARCHAR2(15)	구내운송차량
		//		30.	운송장비코드				TRN_EQP_CD				VARCHAR2(8)		제품출하차량
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
		YdCarSchDao ydCarSchDao 	 = new YdCarSchDao();
		YdStkBedDao ydStkBedDao  	 = new YdStkBedDao();
		YdDaoUtils ydDaoUtils   	 = new YdDaoUtils();
		YdUtils ydUtils              = new YdUtils();

		// 변수선언
		String szMethodName     	 = "makeY9L001";
		String szMsg        	     = "";
		String szOperationName       = "통합후판제품L2 저장위치제원";

		String szTemp        	     = "";
		String szConv                = "";

		String szYD_CAR_ARRSTRT_STAT = "";
		String szYD_EQP_WRK_STAT     = "";
		String szYD_CAR_AIM_YD_GP    = "";
		String szYD_INFO_SYNC_CD     = "";
		String szYD_STK_COL_GP       = "";
		String szYD_STK_BED_NO 		 = "";
		String szYD_CAR_PROG_STAT    = "";
		String szYD_CAR_USE_GP       = "";
		String szTRN_EQP_CD          = "";
		String szCAR_NO              = "";
		String szCARD_NO             = "";

		// 리턴값
		int intRtnVal                = 0;
		int intRtnValCarSch          = 0;

		// TC Length = 147 (60 + 87)
		int nTcLen 					= 87;


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= inRec.getFieldString("LOG_ID");		// logid get
String logId                    		= ydUtils.getJDTOLogId(inRec, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 					// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

		try{
			// Debug MSG
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n=======================makeY9L001() IN==========================\n", YdConstant.DEBUG);
			ydUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "=======================makeY9L001() IN==========================", YdConstant.DEBUG, logId);
			ydUtils.displayRecord(szOperationName, inRec);
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
//			ydUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", YdConstant.DEBUG, logId);

			// 레코드 생성
			rsResult      = JDTORecordFactory.getInstance().createRecordSet("");
			recPara       = JDTORecordFactory.getInstance().create();
			recParaCarSch = JDTORecordFactory.getInstance().create();

			szYD_INFO_SYNC_CD = ydDaoUtils.paraRecChkNull(inRec, "YD_INFO_SYNC_CD");
			szYD_STK_COL_GP   = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP");
			szYD_STK_BED_NO   = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");

			// 넘겨 받음
			szYD_CAR_PROG_STAT = ydDaoUtils.paraRecChkNull(inRec, "YD_CAR_PROG_STAT");
			szYD_EQP_WRK_STAT  = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_WRK_STAT");

			//=======================================================================================================================
			// 적치BED, 적치열 테이블 조회
			//=======================================================================================================================
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsResult, 5);
			if(intRtnVal < 0){
				szMsg = "적치BED 테이블 조회오류 YD_STK_COL_GP(" + szYD_STK_COL_GP + ") YD_STK_BED_NO(" + szYD_STK_BED_NO + ") " + "[Ret : " + intRtnVal + "]";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.ERROR);
				ydUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.ERROR, logId);
				return 0;
			} else if(intRtnVal == 0){
				szMsg = "적치BED 테이블 조회건수 없음 YD_STK_COL_GP(" + szYD_STK_COL_GP + ") YD_STK_BED_NO(" + szYD_STK_BED_NO + ") " + "[Ret : " + intRtnVal + "]";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG, logId);
				return 0;
			}

			// 적치bed 조회결과 추출
			for(int nIdx=0; nIdx<intRtnVal; nIdx++){
				recGetVal = rsResult.getRecord(nIdx);

				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();
				outRec.setField("MSG_ID" , "YDY9L001");
				outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
				outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
				outRec.setField("MSG_GP" , "I");
				outRec.setField("MSG_LEN", YdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0));
				outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

				// 야드정보동기화코드 [야드정보동기화코드]
				outRec.setField("YD_INFO_SYNC_CD", YdUtils.fillSpZr(szYD_INFO_SYNC_CD, 1, 1));

				// 야드구분 [야드구분]
				outRec.setField("YD_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_GP"), 1, 1));

				// 야드동구분 [야드동구분]
				outRec.setField("YD_BAY_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_BAY_GP"), 1, 1));

				// 야드설비구분 [야드설비구분]
				outRec.setField("YD_EQP_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_EQP_GP"), 2, 1));

				// 야드적치열번호 [야드적치열번호]
				outRec.setField("YD_STK_COL_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_COL_NO"), 2, 1));

				// 야드적치Bed번호 [야드적치Bed번호]
				outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_NO"), 2, 1));

				// 야드적치Bed길이구분 [야드적치Bed길이구분]
				outRec.setField("YD_STK_BED_L_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_L_GP"), 1, 1));

				// 야드적치Bed폭구분 [야드적치Bed폭구분]
				outRec.setField("YD_STK_BED_W_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_W_GP"), 1, 1));

				// 야드적치Bed방향구분 [야드적치Bed방향구분]
				outRec.setField("YD_STK_BED_DIR_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_DIR_GP"), 1, 1));

				// 야드적치Bed활성상태 [야드적치Bed활성상태]
				outRec.setField("YD_STK_BED_ACT_STAT", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_ACT_STAT"), 1, 1));

				// 야드적치Bed입출고상태 [야드적치Bed입출고상태]
				outRec.setField("YD_STK_BED_WHIO_STAT", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_WHIO_STAT"), 1, 1));

				// 야드적치BedX축 [야드적치BedX축]
				outRec.setField("YD_STK_BED_XAXIS", PlateGdsYdUtil.genDecmailFomatter("0000000", ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_XAXIS")) );

				// 야드적치BedY축 [야드적치BedY축]
				outRec.setField("YD_STK_BED_YAXIS", PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_YAXIS")) );

				// 야드적치BedZ축 [야드적치BedZ축]
				outRec.setField("YD_STK_BED_ZAXIS", PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_ZAXIS")) );

				// 야드적치Bed단Max [야드적치Bed단Max]
				outRec.setField("YD_STK_BED_LYR_MAX", PlateGdsYdUtil.genDecmailFomatter("000", ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_LYR_MAX")) );

				// 야드적치Bed중량Max [야드적치Bed중량Max]
				outRec.setField("YD_STK_BED_WT_MAX", PlateGdsYdUtil.genDecmailFomatter("0000000", ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_WT_MAX")) );

				// 야드적치Bed높이Max [야드적치Bed높이Max]
				outRec.setField("YD_STK_BED_H_MAX", PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_H_MAX"))  );

				// 야드적치Bed길이Max [야드적치Bed길이Max]
				outRec.setField("YD_STK_BED_L_MAX", PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_L_MAX")) );

				// 야드적치Bed폭Max [야드적치Bed폭Max]
				outRec.setField("YD_STK_BED_W_MAX", PlateGdsYdUtil.genDecmailFomatter("0000.0", ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_W_MAX")) );

				// 차량정보
				szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recGetVal, "YD_CAR_USE_GP").trim();
				szTRN_EQP_CD    = ydDaoUtils.paraRecChkNull(recGetVal, "TRN_EQP_CD");
				szCAR_NO        = ydDaoUtils.paraRecChkNull(recGetVal, "CAR_NO");
				szCARD_NO       = ydDaoUtils.paraRecChkNull(recGetVal, "CARD_NO");

				//=======================================================================================================================
				// 차량스케쥴 조회
				//=======================================================================================================================
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_COL_GP");
				if("PT".equals(szYD_STK_COL_GP.substring(2, 4))){
					// 야드차량목표야드구분 [야드차량목표야드구분]
					rsResultCarSch = JDTORecordFactory.getInstance().createRecordSet("");
					if("L".equals(szYD_CAR_USE_GP)){
						recParaCarSch.setField("TRN_EQP_CD", szTRN_EQP_CD);
						intRtnValCarSch = ydCarSchDao.getYdCarsch(recParaCarSch, rsResultCarSch, 7);
						if(intRtnValCarSch <= 0) {
							szYD_CAR_AIM_YD_GP = YdUtils.fillSpZr(" ", 1, 1);
						}else{
							rsResultCarSch.first();
							recGetValCarSch = rsResultCarSch.getRecord();
							szTemp = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetValCarSch, "YD_CARUD_STOP_LOC"), 6, 1).trim();
							if("".equals(szTemp.trim()) ){
								szYD_CAR_AIM_YD_GP = YdUtils.fillSpZr(" ", 1, 1);
							}else {
								szYD_CAR_AIM_YD_GP = YdUtils.fillSpZr(szTemp.substring(0, 1), 1, 1);
							}
						}
					} else if("G".equals(szYD_CAR_USE_GP)){
						recParaCarSch.setField("CAR_NO", szCAR_NO);
						recParaCarSch.setField("CARD_NO", szCARD_NO);
//PIDEV_S :병행가동용:PI_YD
						recParaCarSch.setField("PI_YD",    	"T");
						intRtnValCarSch = ydCarSchDao.getYdCarsch(recParaCarSch, rsResultCarSch, 11);
						if(intRtnValCarSch <= 0) {
							szYD_CAR_AIM_YD_GP = YdUtils.fillSpZr(" ", 1, 1);
						}else{
							rsResultCarSch.first();
							recGetValCarSch = rsResultCarSch.getRecord();
							szTemp = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetValCarSch, "YD_CARUD_STOP_LOC"), 6, 1).trim();
							if("".equals(szTemp.trim())){
								szYD_CAR_AIM_YD_GP = YdUtils.fillSpZr(" ", 1, 1);
							}else {
								szYD_CAR_AIM_YD_GP = YdUtils.fillSpZr(szTemp.substring(0, 1), 1, 1);
							}
						}
					}

					// 야드차량착발상태 [야드차량진행상태]
					if("1".equals(szYD_CAR_PROG_STAT) || "A".equals(szYD_CAR_PROG_STAT)){
						szYD_CAR_ARRSTRT_STAT = YdUtils.fillSpZr("S", 1, 1);
					}else if("2".equals(szYD_CAR_PROG_STAT) || "B".equals(szYD_CAR_PROG_STAT)){
						szYD_CAR_ARRSTRT_STAT = YdUtils.fillSpZr("A", 1, 1);
					}else{
						szYD_CAR_ARRSTRT_STAT = YdUtils.fillSpZr(" ", 1, 1);
					}

				} else {
					szYD_CAR_ARRSTRT_STAT = YdUtils.fillSpZr(" ", 1, 1);
					szYD_CAR_AIM_YD_GP = YdUtils.fillSpZr(" ", 1, 1);
				}

				outRec.setField("YD_CAR_ARRSTRT_STAT", YdUtils.fillSpZr(szYD_CAR_ARRSTRT_STAT, 1, 1));

				// 야드차량사용구분 [야드차량사용구분]
				outRec.setField("YD_CAR_USE_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_CAR_USE_GP"), 1, 1));

				// 야드설비작업상태 [야드설비작업상태]
				outRec.setField("YD_EQP_WRK_STAT", YdUtils.fillSpZr(szYD_EQP_WRK_STAT, 1, 1));

				// 차량번호 [차량번호]
				outRec.setField("CAR_NO", YdUtils.fillSpZr_KOR(ydDaoUtils.paraRecChkNull(recGetVal, "CAR_NO"), 15, 1));

				// 운송장비코드 [운송장비코드]
				outRec.setField("TRN_EQP_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "TRN_EQP_CD"), 8, 1));

				// 카드번호 [카드번호]
				outRec.setField("CARD_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "CARD_NO"), 4, 1));

				// 야드차량목표야드구분 [야드차량목표야드구분]
				outRec.setField("YD_CAR_AIM_YD_GP", YdUtils.fillSpZr(szYD_CAR_AIM_YD_GP, 1, 1));

				// RecordSet에 추가
				outRecSet.addRecord(outRec);

				// Debug MSG
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n======================makeY9L001() OUT("+ Integer.toString(nIdx) + ")==========================\n", YdConstant.DEBUG);
				ydUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "======================makeY9L001() OUT("+ Integer.toString(nIdx) + ")==========================", YdConstant.DEBUG, logId);
				ydUtils.displayRecord(szOperationName, outRec);
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
//				ydUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", YdConstant.DEBUG, logId);
			} // end of for
		}catch(Exception e){
			szMsg = "Y8(2후판제품야드L2) 송신  저장위치제원  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.ERROR, logId);
			return -1;
		}

		return outRecSet.size();
	} // end of makeY9L001()






	/**
	 * YDY9L002 : 저장품제원
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeY9L002(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID						MSG_ID					VARCHAR2(8)		YDY9L002
		//		2.	생성일						DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간						TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분						MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이						MSG_LEN					NUMBER  (4)
		//		6.	임시							TEMP					VARCHAR2(29)

		//		7.	야드정보동기화코드				YD_INFO_SYNC_CD			VARCHAR2(1)		1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
		//		8.	야드재료정보송신매수			YD_STL_INFO_SND_SH		NUMBER  (3)
		//		9.	야드재료정보송신순번			YD_STL_INFO_SND_CNT		NUMBER  (3)
		//		10.	재료외형구분					STL_APPEAR_GP			VARCHAR2(1)
		//		11.	재료번호						STL_NO					VARCHAR2(11)
		//		12.	야드저장위치					YD_STR_LOC				VARCHAR2(8)		야드적치Bed까지 표현
		//		13.	야드적치단번호					YD_STK_LYR_NO			VARCHAR2(3)
		//		14.	야드재료중량					YD_STL_WT				NUMBER  (5)
		//		15.	야드재료두께					YD_STL_T				NUMBER  (6,3)
		//		16.	야드재료폭					YD_STL_W				NUMBER  (5,1)
		//		17.	야드재료길이					YD_STL_L				NUMBER  (7)
		//      18. 재료외경                                           MAT_ODIA                NUMBER  (5)
		//      19. 재료내경                                           MAT_IDIA                NUMBER  (5,1)
		//		20.	강종							STLKIND_CD				VARCHAR2(3)		연주전단지시에서 Heat 사양, 작업지시 Table 항목을 저장품Table에 등록
		//		21.	규격약호						SPEC_ABBSYM				VARCHAR2(15)	연주전단실적에서 주편/슬라브 공통Table 항목을 저장품Table에 등록
		//		22.	야드입고일자					YD_IPGO_DD				VARCHAR2(14)	현재 야드에 최초 적치 시 저장품 Table에 등록
		//		23.	공장공정코드					PLNT_PROC_CD			VARCHAR2(3)		직전 생산공장
		//		24.	현재진도코드					CURR_PROG_CD			VARCHAR2(1)
		//		25.	주문여재구분					ORD_YEOJAE_GP			VARCHAR2(1)
		//		26.	주문번호						ORD_NO					VARCHAR2(10)
		//		27.	주문행번						ORD_DTL					VARCHAR2(3)
		//		28.	구입SLAB번호					BUY_SLAB_NO				VARCHAR2(30)
		//		29.	SLAB지시행선코드				SLAB_WO_RT_CD			VARCHAR2(2)
		//		30.	설계HCR구분					ORD_HCR_GP				VARCHAR2(1)
		//		31.	HCR구분						HCR_GP					VARCHAR2(1)
		//		32.	연주Machine코드				CC_MC_CD				VARCHAR2(1)
		//		33.	SCARFING여부					SCARFING_YN				VARCHAR2(1)
		//		34.	SCARFING완료유무				SCARFING_DONE_YN		VARCHAR2(1)
		//      35. 주편손질방법                                    RPR_MTD                 VARCHAR2(1)
		//      36. SCARFING깊이                                 SCARFING_DEPTH          VARCHAR2(2)
		//		37.	재열재구분					REHEAT_SLAB_GP			VARCHAR2(1)
		//		38.	조업공장구분					PTOP_PLNT_GP			VARCHAR2(2)		조업공장구분의 두번째 자리
 		//		39.	가열로장입Lot번호	   		    REFUR_CHG_LOT_NO		VARCHAR2(10)	압연지시에서 저장품Table에 등록
		//		40.	생산통제Lot스케줄일련번호		CT_LOT_SCH_SERNO		VARCHAR2(22)	압연지시에서 저장품Table에 등록
		//		41.	이송지시일자					FRTOMOVE_ORD_DATE		VARCHAR2(8)
		//		42.	이송공장구분					FRTOMOVE_PLANT_GP		VARCHAR2(2)		임가공인 경우 임가공사 코드
		//		43.	긴급이송작업지시구분			URGENT_FRTOMOVE_WORD_GP	VARCHAR2(1)
		//		44.	HYSCO운송구분					HYSCO_TRANS_CLS			VARCHAR2(1)
		//		45.	외관종합판정등급				APPEAR_GRADE			VARCHAR2(1)
		//      46. 권취코일냉각방법                              COOL_METHOD             VARCHAR2(1)
		//		47.	냉각완료구분					COOL_DONE_GP			VARCHAR2(1)
		//      48. 야드Conveyor분기코드                    CONV_BRANCH_CD          VARCHAR2(2)
		//		49.	고객코드						CUST_CD					VARCHAR2(6)
		//		50.	목적지코드					DEST_CD					VARCHAR2(5)
		//		51.	납기기준일					DLVRDD_RULE_DD			VARCHAR2(8)
		//		52.	품명코드						ITEMNAME_CD				VARCHAR2(3)
		//		53.	종합판정등급					OVERALL_STAMP_GRADE		VARCHAR2(1)
		//		54.	수주구분						ORD_GP					VARCHAR2(1)
		//		55.	야드산적LotType				YD_STK_LOT_TP			VARCHAR2(2)
		//		56.	야드산적Lot코드				YD_STK_LOT_CD			VARCHAR2(18)
	    //      57. 운송지역명                  DEST_KO_NAME            VARCHAR2(40) --AT000 물류시스템 개선  2023.01.13 운송지역명 추가

		// 레코드 선언
		JDTORecord recPara 		= null;
		JDTORecordSet rsResult	= null;
		JDTORecord recGetVal    = null;
		JDTORecord outRec       = null;


		// DAO객체 생성
		YdDaoUtils ydDaoUtils   = new YdDaoUtils();
		YdUtils ydUtils         = new YdUtils();
		YdStockDao ydStockDao   = new YdStockDao();

		// 변수선언
		String szMethodName     = "makeY9L002";
		String szMsg            = "";
		String szOperationName  = "통합후판제품L2 저장품제원";

		String szConv           = "";


		String szSTLKIND_CD     = "";
		String szSPEC_ABBSYM    = "";
		String szMILL_PLNT_GP   = "";
		String szSCARFING_DEPTH = "";
		String szYD_INFO_SYNC_CD = "";
		String szDEL_YN_CHECK   = "";
		String szCUST_KO_NAME   = "";
		String szDEST_KO_NAME   ="";        //AT000 물류시스템 개선  2023.01.13 운송지역명 추가
		String szDETAIL_ARR_KO_NAME   ="";  ///AT000 물류시스템 개선  2023.01.13 인도처명 추가
		// 리턴값
		int intRtnVal           = 0;

		// TC Length = 309 (60 + 249) + 80
		int nTcLen              = 329; // 기존 249 + 80 AT000 물류시스템 개선  2023.01.13 운송지역명,인도처명 추가)
		
		String szSTLNO          = "";

		// 2021. 5. 17 2021. 05. 17 전문생성 시점에 PT_PLATE_COMM에 진도코드가 바뀌지 않은 문제가 있어
		// 파라메터로 전달받은 것들만 그대로 전달한다.
		String szCURR_PROG_CD       = "";


////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.02 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= inRec.getFieldString("LOG_ID");		// logid get
String logId                    		= ydUtils.getJDTOLogId(inRec, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 					// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
// 2024.09.02 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////


		try{
			// Debug MSG
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n=======================makeY9L002() IN==========================\n", YdConstant.DEBUG);
			ydUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "=======================makeY9L002() IN==========================", YdConstant.DEBUG, logId);
			ydUtils.displayRecord(szOperationName, inRec);
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
//			ydUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", YdConstant.DEBUG, logId);

			// 레코드 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();

			//=======================================================================================================================
			// 적치BED, 적치열 테이블 조회
			// [120] com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedNotStl_PIDEV
			// [26]  com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedStl_PIDEV
			// [180] com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedStlDelYNNoCheck_PIDEV
			//=======================================================================================================================
			szSTLNO = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");
			szYD_INFO_SYNC_CD = ydDaoUtils.paraRecChkNull(inRec, "YD_INFO_SYNC_CD");
			szDEL_YN_CHECK = ydDaoUtils.paraRecChkNull(inRec, "DEL_YN_CHECK");

			// 2021. 5. 17 Parameter로 던져진 후판제품 진도코드
			szCURR_PROG_CD = ydDaoUtils.paraRecChkNull(inRec, "CURR_PROG_CD");

			// 재료정보 유무에 따른 조회
			if("1".equals(szYD_INFO_SYNC_CD) || "2".equals(szYD_INFO_SYNC_CD) || "3".equals(szYD_INFO_SYNC_CD) || "4".equals(szYD_INFO_SYNC_CD)){
				recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP"));
				recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO"));
//PIDEV_S :병행가동용:PI_YD
				recPara.setField("PI_YD",    	"T");
				intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 120);
			} else if("5".equals(szYD_INFO_SYNC_CD) || "A".equals(szYD_INFO_SYNC_CD) || "B".equals(szYD_INFO_SYNC_CD) || "C".equals(szYD_INFO_SYNC_CD)){
				recPara.setField("STL_NO"       , szSTLNO);

				if("N".equals(szDEL_YN_CHECK)){
//PIDEV_S :병행가동용:PI_YD
					recPara.setField("PI_YD",    	"T");
					intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 180);
				}else {
					intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 26);
				}
			}

			if(intRtnVal < 0) {
				szMsg = "저장품 + 적치BED + 적치단  테이블 조회오류  YD_INFO_SYNC_CD(" + szYD_INFO_SYNC_CD + ") YD_STK_COL_GP(" + ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP") + ") YD_STK_BED_NO(" + ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO") + ") STL_NO(" + ydDaoUtils.paraRecChkNull(inRec, "STL_NO") +  ") [Ret : " + intRtnVal + "]";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.ERROR);
				ydUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.ERROR, logId);
				return 0;
			} else if(intRtnVal == 0) {
				szMsg = "저장품 + 적치BED + 적치단  테이블 조회건수 없음  YD_INFO_SYNC_CD(" + szYD_INFO_SYNC_CD + ") YD_STK_COL_GP(" + ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP") + ") YD_STK_BED_NO(" + ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO") + ") STL_NO(" + ydDaoUtils.paraRecChkNull(inRec, "STL_NO") +  ") [Ret : " + intRtnVal + "]";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG, logId);
				return 0;
			} else {
				szMsg = "저장품 + 적치BED + 적치단  테이블 조회성공 YD_INFO_SYNC_CD(" + szYD_INFO_SYNC_CD + ") YD_STK_COL_GP(" + ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP") + ") YD_STK_BED_NO(" + ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO") + ") STLNO(" + szSTLNO +  ") [Ret : " + intRtnVal + "]";
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG, logId);
			}

			for(int nIdx=0; nIdx<intRtnVal; nIdx++) {
				recGetVal = rsResult.getRecord(nIdx);

				if(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_NO").matches("\\d\\D")) {
					//BED 번호가 5A 같이 숫자,문자로 조합된 번지는 RT 가상베드로 2후판 L2로 전송하지 않는다. -- 2013.08.22 L2 요구사항
					continue;
				}

				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();
				outRec.setField("MSG_ID" , "YDY9L002");
				outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
				outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
				outRec.setField("MSG_GP" , "I");
				outRec.setField("MSG_LEN", YdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0));
				outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

				// 야드정보동기화코드 [야드정보동기화코드]
				outRec.setField("YD_INFO_SYNC_CD", YdUtils.fillSpZr(szYD_INFO_SYNC_CD, 1, 1));

				// 야드재료정보송신매수 [야드재료정보송신매수]
				outRec.setField("YD_STL_INFO_SND_SH", PlateGdsYdUtil.genDecmailFomatter("000", Integer.toString(intRtnVal)) );

				// 야드재료정보송신순번 [야드재료정보송신순번]
				outRec.setField("YD_STL_INFO_SND_CNT", PlateGdsYdUtil.genDecmailFomatter("000", Integer.toString(nIdx+1)) );

				// 재료외형구분 [재료외형구분]
				outRec.setField("STL_APPEAR_GP", YdUtils.fillSpZr(recGetVal.getFieldString("STL_APPEAR_GP"), 1, 1));

				// 재료번호 [재료번호]
				outRec.setField("STL_NO", YdUtils.fillSpZr(recGetVal.getFieldString("STL_NO"), 11, 1));

				//if("".equals(recGetVal.getFieldString("YD_STK_COL_GP"))) {

					// 야드저장위치 [야드저장위치 : BED까지]
				//	outRec.setField("YD_STR_LOC", YdUtils.fillSpZr(inRec.getFieldString("YD_STK_COL_GP")+inRec.getFieldString("YD_STK_BED_NO"), 8, 1));

					// 야드적치단번호 [야드적치단번호]
				//	outRec.setField("YD_STK_LYR_NO", YdUtils.fillSpZr(inRec.getFieldString("YD_STK_LYR_NO"), 3, 1));

				//} else {

				if(ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP").indexOf("XRTR") > 0) {
					// 입고대기장 도착시 L2로 저장품 제원 정보를 전송할 경우 입고존위치정보를 보내기 위해서 전달받은 파라메터로 야드저장위치와 단번호를 설정한다.
					// 야드저장위치 [야드저장위치 : BED까지]
					outRec.setField("YD_STR_LOC", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP")+ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO"), 8, 1));

					// 야드적치단번호 [야드적치단번호]
					outRec.setField("YD_STK_LYR_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(inRec, "YD_STK_LYR_NO"), 3, 0));
				} else {

					if("".equals(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_COL_GP"))) {
						//저장위치가 없으면  L2로 전송하지 않는다. -- 2013.09.12 L2 요구사항
						continue;
					}

					// 야드저장위치 [야드저장위치 : BED까지]
					outRec.setField("YD_STR_LOC", YdUtils.fillSpZr(recGetVal.getFieldString("YD_STK_COL_GP")+recGetVal.getFieldString("YD_STK_BED_NO"), 8, 1));

					// 야드적치단번호 [야드적치단번호]
					outRec.setField("YD_STK_LYR_NO", YdUtils.fillSpZr(recGetVal.getFieldString("YD_STK_LYR_NO"), 3, 1));
				}

				// 야드재료중량 [야드재료중량]
				outRec.setField("YD_STL_WT", PlateGdsYdUtil.genDecmailFomatter("00000", recGetVal.getFieldString("YD_STL_WT")) );

				// 야드재료두께 [야드재료두께]
				outRec.setField("YD_STL_T", PlateGdsYdUtil.genDecmailFomatter("000.000", recGetVal.getFieldString("YD_STL_T")) );

				// 야드재료폭 [야드재료폭]
				outRec.setField("YD_STL_W", PlateGdsYdUtil.genDecmailFomatter("0000.0", recGetVal.getFieldString("YD_STL_W")) );

				// 야드재료길이 [야드재료길이]
				outRec.setField("YD_STL_L", PlateGdsYdUtil.genDecmailFomatter("0000000", recGetVal.getFieldString("YD_STL_L")) );

				// 재료외경
				outRec.setField("MAT_ODIA", PlateGdsYdUtil.genDecmailFomatter("00000", recGetVal.getFieldString("MAT_ODIA")) );

				// 재료내경
				outRec.setField("MAT_IDIA", PlateGdsYdUtil.genDecmailFomatter("0000.0", recGetVal.getFieldString("MAT_ODIA"))  );

				// 강종 [강종]
				szSTLKIND_CD   = YdUtils.fillSpZr(recGetVal.getFieldString("STLKIND_CD"), 3, 1);
				outRec.setField("STLKIND_CD", YdUtils.fillSpZr(szSTLKIND_CD, 3, 1));

				// 규격약호 [규격약호]
				szSPEC_ABBSYM  = YdUtils.fillSpZr(recGetVal.getFieldString("SPEC_ABBSYM"), 15, 1);
				outRec.setField("SPEC_ABBSYM", YdUtils.fillSpZr(szSPEC_ABBSYM, 15, 1));

				// 야드입고일자 [등록일자]
				outRec.setField("YD_IPGO_DD", YdUtils.fillSpZr(recGetVal.getFieldString("REG_DDTT"), 14, 1));

				// 공장공정코드 [공장공정코드]
				outRec.setField("PLNT_PROC_CD", YdUtils.fillSpZr(recGetVal.getFieldString("PLNT_PROC_CD"), 3, 1));

				// 현재진도코드 [재료진도코드]
				// 2021. 5. 17 Parameter로 던져진 후판제품 진도코드가 존재하면 그 진도코드를 셋팅한다.
				if("".equals(szCURR_PROG_CD)){
					outRec.setField("CURR_PROG_CD", YdUtils.fillSpZr(recGetVal.getFieldString("CURR_PROG_CD"), 1, 1));
				}
				else{
					outRec.setField("CURR_PROG_CD", YdUtils.fillSpZr(szCURR_PROG_CD, 1, 1));
				}

				// 주문여재구분 [주문여재구분]
				outRec.setField("ORD_YEOJAE_GP", YdUtils.fillSpZr(recGetVal.getFieldString("ORD_YEOJAE_GP"), 1, 1));

				// 주문번호 [주문번호]
				outRec.setField("ORD_NO", YdUtils.fillSpZr(recGetVal.getFieldString("ORD_NO"), 10, 1));

				// 주문행번 [주문행번]
				outRec.setField("ORD_DTL", YdUtils.fillSpZr(recGetVal.getFieldString("ORD_DTL"), 3, 1));

				// 구입SLAB번호 [구입SLAB번호] - 고객코드명으로 송신.
				//outRec.setField("BUY_SLAB_NO", YdUtils.fillSpZr(recGetVal.getFieldString("CUST_KO_NAME"), 30, 1));

				szCUST_KO_NAME = ydDaoUtils.paraRecChkNull(recGetVal, "CUST_KO_NAME");
				if("".equals(szCUST_KO_NAME)) { szCUST_KO_NAME = YdUtils.fillSpZr_KOR(" ", 30, 1); }

				outRec.setField("BUY_SLAB_NO", YdUtils.fillSpZr_KOR(szCUST_KO_NAME, 30, 1));

				// SLAB지시행선코드 [SLAB지시행선코드]
				outRec.setField("SLAB_WO_RT_CD", YdUtils.fillSpZr(recGetVal.getFieldString("SLAB_WO_RT_CD"), 2, 1));

				// 설계HCR구분 [설계HCR구분]
				outRec.setField("ORD_HCR_GP", YdUtils.fillSpZr(recGetVal.getFieldString("ORD_HCR_GP"), 1, 1));

				// HCR구분 [HCR구분]
				outRec.setField("HCR_GP", YdUtils.fillSpZr(recGetVal.getFieldString("HCR_GP"), 1, 1));

				// 연주Machine코드 [야드CCM구분]
				outRec.setField("CC_MC_CD", YdUtils.fillSpZr(recGetVal.getFieldString("CC_MC_CD"), 1, 1));

				// SCARFING여부 [SCARFING여부]
				outRec.setField("SCARFING_YN", YdUtils.fillSpZr(recGetVal.getFieldString("SCARFING_YN"), 1, 1));

				// SCARFING완료유무 [SCARFING완료유무]
				outRec.setField("SCARFING_DONE_YN", YdUtils.fillSpZr(recGetVal.getFieldString("SCARFING_DONE_YN"), 1, 1));

				// 주편손질방법
				outRec.setField("RPR_MTD", YdUtils.fillSpZr(recGetVal.getFieldString("RPR_MTD"), 1, 1));

				// SCARFING깊이 [SCARFING깊이]
				// 후판제품과 코일은 SCARFING_DEPTH가 없음
				szSCARFING_DEPTH = YdUtils.fillSpZr(" ", 2, 1);
				outRec.setField("SCARFING_DEPTH", YdUtils.fillSpZr(szSCARFING_DEPTH, 2, 1));

				if(ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP").indexOf("XRTR") > 0) {
					// 입고대기장 도착시 L2로 저장품 제원 정보를 전송할 경우 재열재구분에 냉간교정작업여부를 셋팅한다.

					// 재열재구분 [재열재구분]
					outRec.setField("REHEAT_SLAB_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(inRec, "REHEAT_SLAB_GP"), 1, 1));

				} else {

					// 재열재구분 [재열재구분]
					//outRec.setField("REHEAT_SLAB_GP", YdUtils.fillSpZr(recGetVal.getFieldString("REHEAT_SLAB_GP"), 1, 1));

					//--2013.11.21 입고대기장이 아니라도  재열재구분에 냉간교정작업여부를 셋팅한다. (현업요청)
					// 야드재료폭 [야드재료폭]
					//if(ydDaoUtils.paraRecChkNullInt(recGetVal, "YD_STL_W") <= 2900) {
					if(ydDaoUtils.paraRecChkNullInt(recGetVal, "YD_STL_W") <= 3300) {
						outRec.setField("REHEAT_SLAB_GP", "Y");
					} else {
						outRec.setField("REHEAT_SLAB_GP", "N");
					}
				}

				// 압연공장구분 [조업공장구분]
				szMILL_PLNT_GP = YdUtils.fillSpZr(recGetVal.getFieldString("PTOP_PLNT_GP"), 2, 1);
				outRec.setField("PTOP_PLNT_GP", YdUtils.fillSpZr(szMILL_PLNT_GP, 2, 1));

				// 가열로장입Lot번호 [가열로장입Lot번호]
//				outRec.setField("REFUR_CHG_LOT_NO", YdUtils.fillSpZr(recGetVal.getFieldString("REFUR_CHG_LOT_NO"), 10, 1));
				outRec.setField("REFUR_CHG_LOT_NO", YdUtils.fillSpZr(recGetVal.getFieldString("YD_PILING_CD"), 10, 1));

				// 생산통제Lot스케줄일련번호 [생산통제Lot스케줄일련번호]
				//outRec.setField("CT_LOT_SCH_SERNO", YdUtils.fillSpZr(recGetVal.getFieldString("CT_LOT_SCH_SERNO"), 22, 1));
				outRec.setField("CT_LOT_SCH_SERNO", YdUtils.fillSpZr(recGetVal.getFieldString("CAR_LOTID"), 22, 1)); //2013.08.02 -- 차량LOT ID 를 CT_LOT_SCH_SERNO 항목에 넣어서 전송하기로 함

				// 이송지시일자 [이송지시일자]
				outRec.setField("FRTOMOVE_ORD_DATE", YdUtils.fillSpZr(recGetVal.getFieldString("FRTOMOVE_ORD_DATE"), 8, 1));

				// 이송공장구분 [이송공장구분]
				outRec.setField("FRTOMOVE_PLANT_GP", YdUtils.fillSpZr(recGetVal.getFieldString("FRTOMOVE_PLANT_GP"), 2, 1));

				// 긴급이송작업지시구분 [긴급이송작업지시구분]
				outRec.setField("URGENT_FRTOMOVE_WORD_GP", YdUtils.fillSpZr(recGetVal.getFieldString("URGENT_FRTOMOVE_WORD_GP"), 1, 1));

				// HYSCO운송구분 [HYSCO운송구분]
				outRec.setField("HYSCO_TRANS_CLS", YdUtils.fillSpZr(recGetVal.getFieldString("HYSCO_TRANS_CLS"), 1, 1));

				// 외관종합판정등급 [외관종합판정등급]
				outRec.setField("APPEAR_GRADE", YdUtils.fillSpZr(recGetVal.getFieldString("APPEAR_GRADE"), 1, 1));

				// 권취코일냉각방법 [권취코일냉각방법]
				outRec.setField("COOL_METHOD", YdUtils.fillSpZr(recGetVal.getFieldString("COOL_METHOD"), 1, 1));

				// 냉각완료구분 [냉각완료구분]
				outRec.setField("COOL_DONE_GP", YdUtils.fillSpZr(recGetVal.getFieldString("COOL_DONE_GP"), 1, 1));

				// 야드Conveyor분기코드
				outRec.setField("CONV_BRANCH_CD", YdUtils.fillSpZr(recGetVal.getFieldString("CONV_BRANCH_CD"), 2, 1));

				// 고객코드 [고객코드]
				outRec.setField("CUST_CD", YdUtils.fillSpZr(recGetVal.getFieldString("CUST_CD"), 6, 1));

				// 목적지코드 [목적지코드]
				outRec.setField("DEST_CD", YdUtils.fillSpZr(recGetVal.getFieldString("DEST_CD"), 5, 1));

				// 납기기준일 [납기기준일]
				outRec.setField("DLVRDD_RULE_DD", YdUtils.fillSpZr(recGetVal.getFieldString("DLVRDD_RULE_DD"), 8, 1));

				// 품명코드 [품명코드]
				outRec.setField("ITEMNAME_CD", YdUtils.fillSpZr(recGetVal.getFieldString("ITEMNAME_CD"), 3, 1));

				// 종합판정등급 [종합판정등급]
				outRec.setField("OVERALL_STAMP_GRADE", YdUtils.fillSpZr(recGetVal.getFieldString("OVERALL_STAMP_GRADE"), 1, 1));

				// 수주구분 [수주구분]
				outRec.setField("ORD_GP", YdUtils.fillSpZr(recGetVal.getFieldString("ORD_GP"), 1, 1));

				// 야드산적LotType [야드산적LotType]
				outRec.setField("YD_STK_LOT_TP", YdUtils.fillSpZr(recGetVal.getFieldString("YD_STK_LOT_TP"), 2, 1));

				// 야드산적Lot코드 [야드산적Lot코드]
//SJH				outRec.setField("YD_STK_LOT_CD", YdUtils.fillSpZr(recGetVal.getFieldString("YD_STK_LOT_CD"), 18, 1));
				outRec.setField("YD_STK_LOT_CD", YdUtils.fillSpZr(recGetVal.getFieldString("DETAIL_ARR_CD"), 18, 1));

				//AT000 물류시스템 개선2023.01.13  운송지역명 추가
                szDEST_KO_NAME = ydDaoUtils.paraRecChkNull(recGetVal, "DEST_KO_NAME");
                if("".equals(szDEST_KO_NAME)) { szDEST_KO_NAME = YdUtils.fillSpZr_KOR(" ", 40, 1); }

                outRec.setField("DEST_KO_NAME", YdUtils.fillSpZr_KOR(szDEST_KO_NAME, 40, 1));

               // AT000 물류시스템 개선2023.01.13 인도처명 추가
                szDETAIL_ARR_KO_NAME = ydDaoUtils.paraRecChkNull(recGetVal, "DETAIL_ARR_KO_NAME");
                if("".equals(szDETAIL_ARR_KO_NAME)) { szDETAIL_ARR_KO_NAME = YdUtils.fillSpZr_KOR(" ", 40, 1); }

                outRec.setField("DETAIL_ARR_KO_NAME", YdUtils.fillSpZr_KOR(szDETAIL_ARR_KO_NAME, 40, 1));
                
                //24.11.06 REQ202410631446 L2 선박명 I/F 추가 
				ydUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "L2 선박명 추가에 따라 I/F 컬럼 추가", YdConstant.DEBUG, logId);
				nTcLen			= 409;
				outRec.setField("MSG_LEN", YdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0));
				outRec.setField("SHIP_NAME", YdUtils.fillSpZr(recGetVal.getFieldString("SHIP_NAME_DTL"), 80, 1));
    			
    			
    			//25.03.13 임진후기사 요청 L2 반송여부 I/F 추가 
    			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szMethodName, "APP060", "T", "012");
    			
    			if("Y".equals(sApplyYnPI)){
    				ydUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "L2 반송여부 추가에 따라 I/F 추가", YdConstant.DEBUG, logId);
    				nTcLen			= 410;
    				outRec.setField("MSG_LEN", YdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0));
    				outRec.setField("RETURNHEAT_GP", YdUtils.fillSpZr(recGetVal.getFieldString("RETURNHEAT_GP"), 1, 1));
    			}

				// RecordSet에 추가
				outRecSet.addRecord(outRec);

				// Debug MSG
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n======================makeY9L002() OUT("+ Integer.toString(nIdx) + ")==========================\n", YdConstant.DEBUG);
				ydUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "======================makeY9L002() OUT("+ Integer.toString(nIdx) + ")==========================", YdConstant.DEBUG, logId);
				ydUtils.displayRecord(szOperationName, outRec);
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
//				ydUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", YdConstant.DEBUG, logId);
			}
		}catch(Exception e){
			szMsg = "Y8(2후판제품야드L2) 송신  저장품제원  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
// 2024.09.02 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.ERROR, logId);
			return -1;
		}

		return outRecSet.size();
	} // end of makeY9L002()

	/**
	 * YDY9L004 : 크레인작업지시
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeY9L004(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID						MSG_ID						VARCHAR2(8)		YDY9L004
		//		2.	생성일						DATE						VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간						TIME						VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분						MSG_GP						VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이						MSG_LEN						NUMBER  (4)
		//		6.	임시							TEMP						VARCHAR2(29)

		//		7.	야드설비ID					YD_EQP_ID					VARCHAR2(6)		크레인설비 ID
		//		8.	야드작업진행상태				YD_WRK_PROG_STAT			VARCHAR2(1)		"W" 작업지시대기, "1" 권상작업, "3" 권하작업
		//		9.	야드스케쥴코드					YD_SCH_CD					VARCHAR2(8)
		//		10.	야드스케줄명					YD_SCH_NAME					VARCHAR2(30)
		//		10.	야드크레인스케쥴ID				YD_CRN_SCH_ID				VARCHAR2(18)
		//		11.	야드크레인작업매수				YD_CRN_WRK_SH				NUMBER  (2)
		//		12.	야드크레인작업중량				YD_CRN_WRK_WT				NUMBER  (7)
		//		13.	야드크레인작업총두께			YD_CRN_WRK_T				NUMBER  (7,3)
		//		14.	야드크레인작업최대폭			YD_CRN_WRK_MAX_W			NUMBER  (5,1)
		//		15.	야드크레인작업최대길이			YD_CRN_WRK_MAX_L			NUMBER  (7)
		//*		16.	야드크레인스케줄잔여회수			YD_CRN_SCH_RMD_CNT			NUMBER  (2)		권상모음, 권하분리작업 시 크레인 Handling 잔여 회수
		//*		17.	야드크레인Grab구분				YD_CRN_GRAB_GP				VARCHAR2(1)
		//*		18.	야드크레인Grab사용구분			YD_CRN_GRAB_USE_GP		    VARCHAR2(1)		1:GBAL1만 사용 2:GBAB2만 사용 B:양쪽사용
		//*		19.	야드크레인1번Grab신축길이		YD_CRN_TT1_GRAB_NEW_AXIS_L	NUMBER  (4)
		//*		20.	야드크레인2번Grab신축길이		YD_CRN_TT2_GRAB_NEW_AXIS_L	NUMBER  (4)
		//*		21.	야드크레인Magnet사용갯수		YD_CRN_MGNT_USE_EA			NUMBER  (2)   	사용갯수에 따라 사용여부 check
		//*		22.	야드크레인Magnet사용여부1		YD_CRN_MGNT_USE_YN1			VARCHAR2(1)		"Y", "N"
		//*		23	야드크레인Magnet사용여부2		YD_CRN_MGNT_USE_YN2			VARCHAR2(1)		Y, "N"
		//*		24	야드크레인Magnet사용여부3		YD_CRN_MGNT_USE_YN3			VARCHAR2(1)		Y, "N"
		//*		25	야드크레인Magnet사용여부4		YD_CRN_MGNT_USE_YN4			VARCHAR2(1)		Y, "N"
		//*		26	야드크레인Magnet사용여부5		YD_CRN_MGNT_USE_YN5			VARCHAR2(1)		Y, "N"
		//*		27	야드크레인Magnet사용여부6		YD_CRN_MGNT_USE_YN6			VARCHAR2(1)		Y, "N"
		//*		28	야드크레인Magnet사용여부7		YD_CRN_MGNT_USE_YN7			VARCHAR2(1)		Y, "N"
		//*		29	야드크레인Magnet사용여부8		YD_CRN_MGNT_USE_YN8			VARCHAR2(1)		Y, "N"
		//*		30	야드크레인Magnet사용여부9		YD_CRN_MGNT_USE_YN9			VARCHAR2(1)		Y, "N"
		//*		31	야드크레인Magnet사용여부10		YD_CRN_MGNT_USE_YN10		VARCHAR2(1)  	Y, "N"
		//		32.	야드권상지시위치				YD_UP_WO_LOC				VARCHAR2(8)
		//		33.	야드권상지시단					YD_UP_WO_LAYER				VARCHAR2(3)
		//		34.	야드권상지시X축				YD_UP_WO_LOC_XAXIS			NUMBER  (7)
		//		35.	야드권상지시X축오차최대			YD_UP_WO_XAXIS_GAP_MAX		NUMBER  (5)
		//		36.	야드권상지시X축오차최소			YD_UP_WO_XAXIS_GAP_MIN		NUMBER  (5)
		//		37.	야드권상지시Y축				YD_UP_WO_LOC_YAXIS			NUMBER  (5)
		//		38.	야드권상지시Y축1				YD_UP_WO_LOC_YAXIS1			NUMBER  (5)
		//		39.	야드권상지시Y축2				YD_UP_WO_LOC_YAXIS2			NUMBER  (5)
		//		40.	야드권상지시Y축오차최대			YD_UP_WO_YAXIS_GAP_MAX		NUMBER  (5)
		//		41.	야드권상지시Y축오차최소			YD_UP_WO_YAXIS_GAP_MIN		NUMBER  (5)
		//		42.	야드권상지시Z축				YD_UP_WO_LOC_ZAXIS			NUMBER  (5)
		//		43.	야드권상지시Z축오차최대			YD_UP_WO_ZAXIS_GAP_MAX		NUMBER  (5)
		//		44.	야드권상지시Z축오차최소			YD_UP_WO_ZAXIS_GAP_MIN		NUMBER  (5)
		//		45.	야드권하지시위치				YD_DN_WO_LOC				VARCHAR2(8)
		//		46.	야드권하지시단					YD_DN_WO_LAYER				VARCHAR2(3)
		//		47.	야드권하지시X축				YD_DN_WO_LOC_XAXIS			NUMBER  (7)
		//		48.	야드권하지시X축오차최대			YD_DN_WO_XAXIS_GAP_MAX		NUMBER  (5)
		//		49.	야드권하지시X축오차최소			YD_DN_WO_XAXIS_GAP_MIN		NUMBER  (5)
		//		50.	야드권하지시Y축				YD_DN_WO_LOC_YAXIS			NUMBER  (5)
		//		51.	야드권하지시Y축1				YD_DN_WO_LOC_YAXIS1			NUMBER  (5)
		//		52.	야드권하지시Y축2				YD_DN_WO_LOC_YAXIS2			NUMBER  (5)
		//		53.	야드권하지시Y축오차최대			YD_DN_WO_YAXIS_GAP_MAX		NUMBER  (5)
		//		54.	야드권하지시Y축오차최소			YD_DN_WO_YAXIS_GAP_MIN		NUMBER  (5)
		//		55.	야드권하지시Z축				YD_DN_WO_LOC_ZAXIS			NUMBER  (5)
		//		56.	야드권하지시Z축오차최대			YD_DN_WO_ZAXIS_GAP_MAX		NUMBER  (5)
		//		57.	야드권하지시Z축오차최소			YD_DN_WO_ZAXIS_GAP_MIN		NUMBER  (5)
		//*		58.	야드설비ID2					YD_EQP_ID2					VARCHAR2(6)		권상 또는 권하위치가 대차 및 차량인 경우
		//*		59.	야드대차목적동					YD_TC_AIM_BAY_GP			VARCHAR2(1)
		//*		60.	야드차량사용구분				YD_CAR_USE_GP				VARCHAR2(1)		권상 또는 권하위치가 차량인 경우("L" 구내운송차량, "G" 제품출하차량)
		//*		61.	차량번호						CAR_NO						VARCHAR2(15)	권상 또는 권하위치가 제품출하차량인 경우
		//*		62.	운송장비코드					TRN_EQP_CD					VARCHAR2(8)		권상 또는 권하위치가 구내운송차량인 경우
		//*		63.	야드설비작업매수				YD_EQP_WRK_SH				NUMBER  (2)		대차, 차량스케줄의 설비작업매수
		//*		64.	야드설비잔량매수				YD_EQP_RMN_SH				NUMBER  (2)		대차, 차량스케줄의 설비작업매수 - 크레인 작업이 미완료된 매수
		//      ------------------------------ GROUP [10] -----------------------------
		//*		65.	재료번호						STL_NO1						VARCHAR2(11)
		//*		66.	야드재료중량					YD_STL_WT1					NUMBER  (5)
		//*		67.	야드재료두께					YD_STL_T1					NUMBER  (6,3)
		//*		68.	야드재료폭					YD_STL_W1					NUMBER  (5,1)
		//*		69.	야드재료길이					YD_STL_L1					NUMBER  (7)
		//      ------------------------------ GROUP END  -----------------------------
		//*		70.	야드스케쥴코드_Next			YD_SCH_CD_NEXT				VARCHAR2(8)		크레인스케줄에 등록된 다음 작업
		//*		70.	야드스케쥴명_Next				YD_SCH_NAME_NEXT			VARCHAR2(30)	크레인스케줄에 등록된 다음 작업
		//*		71.	야드권상지시위치_Next			YD_UP_WO_LOC_NEXT			VARCHAR2(8) 	YARD(1)+동(1)+SPAN(2)+열(2)+번지(2)
		//*		72.	야드권상지시단_Next			YD_UP_WO_LAYER_NEXT			VARCHAR2(3)
		//*		73.	야드권하지시위치_Next			YD_DN_WO_LOC_NEXT			VARCHAR2(8)
		//*		74.	야드권하지시단_Next			YD_DN_WO_LAYER_NEXT			VARCHAR2(3)
		//*		75.	재료번호_Next					STL_NO_NEXT					VARCHAR2(11)
		//*		76.	야드크레인작업매수_Next			YD_CRN_WRK_SH_NEXT			NUMBER  (2)
		//*		77.	야드크레인작업중량_Next			YD_CRN_WRK_WT_NEXT			NUMBER  (7)
		//*		78.	야드크레인작업총두께_Next		YD_CRN_WRK_T_NEXT			NUMBER  (7,3)
		//*		79.	야드크레인작업최대폭_Next		YD_CRN_WRK_MAX_W_NEXT		NUMBER  (5,1)
		//*		80.	야드크레인작업최대길이_Next     YD_CRN_WRK_MAX_L_NEXT		NUMBER  (7)



		// 크레인스케줄Dao 객체 생성
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();
		YdCrnSpecDao ydCrnSpecDao = new YdCrnSpecDao();
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		YdPlateCommDAO commDao = new YdPlateCommDAO();

		YdUtils ydUtils 		= new YdUtils();
		YdDaoUtils ydDaoUtils 	= new YdDaoUtils();

		// 조회 결과를 담을 RecordSet생성
		JDTORecordSet rsGetYdCrnsch  = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsYdSchRule    = null;
		JDTORecordSet rsYdGrabResult = null;
		JDTORecordSet rsResult2      = null;


		// 레코드 선언
		JDTORecord recPara	  = null;
		JDTORecord recPara2   = null;
		JDTORecord recPara3   = null;
		JDTORecord recIn      = null;
		JDTORecord outRec     = null;
		JDTORecord recGrab	  = null;
		JDTORecord recGetVal  = null;
		JDTORecord recGetVal2 = null;

		// 변수선언
		String szMethodName         = "makeY9L004";
		String szMsg                = "";
		String szOperationName      = "통합후판제품L2 크레인작업지시";
		String szTemp               = "";

		// 야드설비ID2
		String szYD_EQP_ID2			= "";
		// 야드대차목적동
		String szYD_TC_AIM_BAY_GP	= "";
		// 야드차량사용구분
		String szYD_CAR_USE_GP		= "";
		// 차량번호
		String szCAR_NO				= "";
		// 운송장비코드
		String szTRN_EQP_CD			= "";
		// 야드설비작업매수
		String szYD_EQP_WRK_SH		= "";
		// 야드설비잔량매수
		String szYD_EQP_RMN_SH		= "";
		// 소수점
		String szPoint 				= "";
		String szYD_SCH_NAME		= "";
		String szYD_SCH_NAME_NEXT   = "";

		String szCrnSchID           = "";
		String szYD_WRK_PROG_STAT   = "";
		String szMSG_GP             = "";
		String szYD_CRN_GRAB_TP     = "";
		String szYD_CRN_SB_CTL_H    = "";
		String szTemp2              = "";

		String szYD_AID_WRK_YN      = "";
		String szYD_AID_WRK_YN_NEXT      = "";
		String szORD_EDGE_CD        = "";
		// TC Length =664 ( 665 + 60 = 725)
		int nTcLen 		= 725;
		int intRtnVal 	= 0;
		int intRtnVal1  = 0;
		int intRtnVal2  = 0;
		int nGrabRet    = 0;

		try{
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================[STEP 1]::makeY9L004==============\n", YdConstant.DEBUG);
			ydUtils.displayRecord(szOperationName, inRec);

			szCrnSchID = ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID");
			szYD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(inRec, "YD_WRK_PROG_STAT");
			szMSG_GP = ydDaoUtils.paraRecChkNull(inRec, "MSG_GP");

			recIn = JDTORecordFactory.getInstance().create();
			recIn.setField("YD_CRN_SCH_ID", szCrnSchID);
			recIn.setField("YD_WRK_PROG_STAT", szYD_WRK_PROG_STAT);

			// 크레인스케줄 조회
			/* com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnWrkMtlNextWrkBookId_NEXT */
			intRtnVal =  commDao.select(recIn, rsGetYdCrnsch, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdCrnschYdStockYdCrnWrkMtlNextWrkBookId_NEXT");
//			intRtnVal = ydCrnSchDao.getYdCrnsch(recIn, rsGetYdCrnsch, 41);
			if(intRtnVal < 0){
				szMsg ="크레인스케줄 조회 중 오류 : [" +intRtnVal+"]";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			} else if(intRtnVal ==0){
				szMsg ="크레인스케줄 조회건수 없음 : [" +intRtnVal+"]";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);
				return 0;
			}


			// 크레인스케줄 조회결과 추출
			rsGetYdCrnsch.first();
			recPara = JDTORecordFactory.getInstance().create();
			outRec = JDTORecordFactory.getInstance().create();
			recPara = rsGetYdCrnsch.getRecord(0);

			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================[STEP 2]:: 조 회 결 과==============\n", YdConstant.DEBUG);
			ydUtils.displayRecord(szMethodName, recPara);


			outRec.setField("MSG_ID", 						"YDY9L004" );
			outRec.setField("DATE", 						YdUtils.getCurDate("yyyy-MM-dd") );
			outRec.setField("TIME", 						YdUtils.getCurDate("HH-mm-ss") );

			if("D".equals(szMSG_GP) || "U".equals(szMSG_GP)){
				outRec.setField("MSG_GP" , YdUtils.fillSpZr(szMSG_GP, 1, 1));
			} else {
				outRec.setField("MSG_GP" , "I");
			}

			outRec.setField("MSG_LEN", 						YdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0) );
			outRec.setField("TEMP", 						YdUtils.fillSpZr("", 29, 1) );

		    String szYD_EQP_ID 								= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_ID");
			outRec.setField("YD_EQP_ID", 					YdUtils.fillSpZr(szYD_EQP_ID, 6, 1) );

			// 2021.02.03 권하위치변경 flag
			if ("5".equals(szYD_WRK_PROG_STAT)){
				// L2요청으로 인하여 다음과 같이 정의한다.
				// 권상이전의 경우 '1'으로
				// 권상했으면 5번으로 전송요청
				if("2".equals(ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_PROG_STAT"))){
					szTemp2 = YdUtils.fillSpZr(szYD_WRK_PROG_STAT, 1, 1);
				}
				else{
					szTemp2 = YdUtils.fillSpZr("1", 1, 1);
				}
			}else
			{
				szTemp2 = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_PROG_STAT"), 1, 1);
			}

			if("W".equals(szTemp2)) {
				outRec.setField("YD_WRK_PROG_STAT", YdUtils.fillSpZr("1", 1, 1));
			}
			// DB의 크레인스케쥴이 "S"일 경우 -> 1로 변경한다.
			else if("S".equals(szTemp2)){
				outRec.setField("YD_WRK_PROG_STAT", YdUtils.fillSpZr("1", 1, 1));
			}
			else {
				outRec.setField("YD_WRK_PROG_STAT", YdUtils.fillSpZr(szTemp2, 1, 1));
			}
			String szYD_SCH_CD 								= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");
			outRec.setField("YD_SCH_CD", 					YdUtils.fillSpZr(szYD_SCH_CD, 8, 1) );
//090630 추가
			if(!"".equals(szYD_SCH_CD.trim())){
				//-- 통합 크레인 스케줄
				recPara2 = JDTORecordFactory.getInstance().create();
				rsYdSchRule = JDTORecordFactory.getInstance().createRecordSet("");

				recPara2.setField("YD_SCH_CD", szYD_SCH_CD);

				intRtnVal1 = commDao.select(recPara2, rsYdSchRule, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0098");

				if(intRtnVal1 <= 0) {
					szMsg ="스케쥴 기준 조회건수 없음 : [" +intRtnVal1+"]";
					ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);

					szYD_SCH_NAME = "";
					szYD_EQP_ID2 = YdUtils.fillSpZr(" ", 6, 1);;
				} else {

					//레코드 추출
					rsYdSchRule.first();
					recPara3 = rsYdSchRule.getRecord();

					szYD_SCH_NAME 	= ydDaoUtils.paraRecChkNull(recPara3, "YD_SCH_CONTENTS");
					szYD_EQP_ID2 = YdUtils.fillSpZr(" ", 6, 1);;
				}

			} else {
				szMsg = "스케쥴 코드가 없어서 기준테이블을 조회 안함 : YD_SCH_NAME, YD_EQP_ID2는 공백";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);

				szYD_SCH_NAME = YdUtils.fillSpZr_KOR(" ", 30, 1);
				szYD_EQP_ID2 = YdUtils.fillSpZr(" ", 6, 1);
			}

			szYD_AID_WRK_YN      = ydDaoUtils.paraRecChkNull(recPara, "YD_AID_WRK_YN");
			if("Y".equals(szYD_AID_WRK_YN)){ szYD_AID_WRK_YN = "[보조]";};

			if("Y".equals(szYD_AID_WRK_YN_NEXT)){ szYD_AID_WRK_YN_NEXT = "[보조]";};

			outRec.setField("YD_SCH_NAME", 					YdUtils.fillSpZr_KOR(szYD_AID_WRK_YN+szYD_SCH_NAME, 30, 1) );

			String szYD_CRN_SCH_ID 							= ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_SCH_ID");
			outRec.setField("YD_CRN_SCH_ID", 				YdUtils.fillSpZr(szYD_CRN_SCH_ID, 18, 1)) ;

			outRec.setField("YD_CRN_WRK_SH", 				PlateGdsYdUtil.genDecmailFomatter("00", ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_SH")) );
			outRec.setField("YD_CRN_WRK_WT", 				PlateGdsYdUtil.genDecmailFomatter("0000000", ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_WT")) ) ;
		   	outRec.setField("YD_CRN_WRK_T", 				PlateGdsYdUtil.genDecmailFomatter("0000.000", ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_T")) ) ;
		   	outRec.setField("YD_CRN_WRK_MAX_W", 			PlateGdsYdUtil.genDecmailFomatter("0000.0", ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_WRK_MAX_W")) ) ;
			outRec.setField("YD_CRN_WRK_MAX_L", 			PlateGdsYdUtil.genDecmailFomatter("0000000", ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_MAX_L"))) ;

			// 자동화크레인의 경우 S5코드 넘겨준다.
			String szYD_CRN_SCH_RMD_CNT 					= ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_RMD_CNT");
//			if("Y".equals(ydDaoUtils.paraRecChkNull(recPara, "IS_AUTO_CRN"))){
				// 받아온 값을 처리한다.
				if ("5".equals(szYD_WRK_PROG_STAT)){
					szYD_CRN_SCH_RMD_CNT = "S5";
				}
//			}else{
				else if("".equals(szYD_CRN_SCH_RMD_CNT)){
					szYD_CRN_SCH_RMD_CNT = "02";
				}
//			}
			outRec.setField("YD_CRN_SCH_RMD_CNT", 			YdUtils.fillSpZr(szYD_CRN_SCH_RMD_CNT, 2, 1)) ;


			//==========================================================================================================
			// YDCRNSPEC 테이블을 읽어서 타입을 가져온다.
			// GRAB구분이 'D'면  Manet사용갯수 9 'E'면 Magnet사용갯수 10으로 주고 일단 Magnet사용여부는 모두 'Y'
			// Grab신축길이는 모두 일단 공백처리
			// YD_CRN_SB_CTL_H를 읽어서 YD_CRN_GRAB_USE_GP 에 값을 넣는다. 1,2 => 1,2    3 => B로 변경
			//==========================================================================================================





			//==========================================================================================================
			// 크레인 사양테이블 조회
			//==========================================================================================================
			rsYdGrabResult = JDTORecordFactory.getInstance().createRecordSet("");
			recGrab = JDTORecordFactory.getInstance().create();
			recGrab.setField("YD_EQP_ID", szYD_EQP_ID);
			nGrabRet = ydCrnSpecDao.getYdCrnspec(recGrab, rsYdGrabResult, 0);

			rsYdGrabResult.first();
			recGetVal = rsYdGrabResult.getRecord();

			szYD_CRN_GRAB_TP = ydDaoUtils.paraRecChkNull(recGetVal, "YD_CRN_GRAB_TP"); // 크레인 Grab 구분

			if("X".equals(szYD_CRN_GRAB_TP)){
				outRec.setField("YD_CRN_GRAB_GP", YdUtils.fillSpZr("E", 1, 1));
			}else{
				outRec.setField("YD_CRN_GRAB_GP", YdUtils.fillSpZr("D", 1, 1));
			}

			//===============================================================================
			// 설비ID끝이 1이면 1호기 TWO GRAB => "E"   2면  2호기 ONE GRAB => "D"
			//===============================================================================
			/*
			if(szYD_EQP_ID.substring(5).equals("1")){
				outRec.setField("YD_CRN_GRAB_GP", YdUtils.fillSpZr("E", 1, 1)));
			} else if(szYD_EQP_ID.substring(5).equals("2")){
				outRec.setField("YD_CRN_GRAB_GP", YdUtils.fillSpZr("D", 1, 1)));
			} else {
				szMsg = "*********** 설비ID값이 유효하지 않음 *****************";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);

				outRec.setField("YD_CRN_GRAB_GP", YdUtils.fillSpZr(" ", 1, 1)));
			}
			*/

			//===============================================================================
			// YD_CRN_SB_CTL_H(1 or 2) => (1 or 2)   YD_CRN_SB_CTL_H(3) => ("B")
			//===============================================================================
			szYD_CRN_SB_CTL_H = ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_SB_CTL_H");
			if("1".equals(szYD_CRN_SB_CTL_H) || "2".equals(szYD_CRN_SB_CTL_H)) {
				outRec.setField("YD_CRN_GRAB_USE_GP", YdUtils.fillSpZr(szYD_CRN_SB_CTL_H, 1, 1));
			} else if("3".equals(szYD_CRN_SB_CTL_H)){
				outRec.setField("YD_CRN_GRAB_USE_GP", "B");
			} else {
				szMsg = "*********** 야드크레인SB제어높이 값이 유효하지 않음 YD_CRN_SB_CTL_H(" + szYD_CRN_SB_CTL_H + ")*****************";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);

				outRec.setField("YD_CRN_GRAB_USE_GP", YdUtils.fillSpZr(" ", 1, 1));
			}


			// 2009.11.20 이것은 일단 공백으로 넣지 말라고 지시
		    //String szYD_CRN_TT1_GRAB_NEW_AXIS_L 			= " ";
			//outRec.setField("YD_CRN_TT1_GRAB_NEW_AXIS_L", 	YdUtils.fillSpZr(szYD_CRN_TT1_GRAB_NEW_AXIS_L, 4, 1)) ;
		    //AT000 물류시스템 개선 Trimming 코드 추가(현업요청 사항) 2023.02.01
			szORD_EDGE_CD =  ydDaoUtils.paraRecChkNull(recPara, "ORD_EDGE_CD");
		    outRec.setField("YD_CRN_TT1_GRAB_NEW_AXIS_L", 	YdUtils.fillSpZr(szORD_EDGE_CD,4, 1));

		    String szYD_CRN_TT2_GRAB_NEW_AXIS_L 			= " ";
			outRec.setField("YD_CRN_TT2_GRAB_NEW_AXIS_L", 	YdUtils.fillSpZr(szYD_CRN_TT2_GRAB_NEW_AXIS_L, 4, 1)) ;

			// 2021. 10. 22 자동화크레인 관련 수정
			// 자동화크레인일 경우 마그네틱 포트갯수를 계산하여 Y9에 전송하며
			// 그렇지 않을 경우 기존 로직 그대로 이용한다.
			// 계산공식
			// 마그넷틱 사용 포트수 = (제품길이-OH)/빔간격+1;

			if(PlateGdsYdUtil.isApplyYn(szYD_EQP_ID+"좌표계산자동화신규여부")){
				// SQL조회로 대체하자!!
				JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecord params = JDTORecordFactory.getInstance().create();
		  		params.setField("YD_EQP_ID", szYD_EQP_ID);
		  		params.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
				if(commDao.select(params, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getCrnMgntPortInfo") > 0){
					outRec.setField("YD_CRN_MGNT_USE_EA", YdUtils.fillSpZr( rsResult.getRecord(0).getFieldString("YD_CRN_MGNT_USE_EA"), 2, 1)) ;
					outRec.setField("YD_CRN_MGNT_USE_YN1", YdUtils.fillSpZr( rsResult.getRecord(0).getFieldString("YD_CRN_MGNT_USE_YN1"), 1, 1)) ;
					outRec.setField("YD_CRN_MGNT_USE_YN2", YdUtils.fillSpZr( rsResult.getRecord(0).getFieldString("YD_CRN_MGNT_USE_YN2"), 1, 1)) ;
					outRec.setField("YD_CRN_MGNT_USE_YN3", YdUtils.fillSpZr( rsResult.getRecord(0).getFieldString("YD_CRN_MGNT_USE_YN3"), 1, 1)) ;
					outRec.setField("YD_CRN_MGNT_USE_YN4", YdUtils.fillSpZr( rsResult.getRecord(0).getFieldString("YD_CRN_MGNT_USE_YN4"), 1, 1)) ;
					outRec.setField("YD_CRN_MGNT_USE_YN5", YdUtils.fillSpZr( rsResult.getRecord(0).getFieldString("YD_CRN_MGNT_USE_YN5"), 1, 1)) ;
					outRec.setField("YD_CRN_MGNT_USE_YN6", YdUtils.fillSpZr( rsResult.getRecord(0).getFieldString("YD_CRN_MGNT_USE_YN6"), 1, 1)) ;
					outRec.setField("YD_CRN_MGNT_USE_YN7", YdUtils.fillSpZr( rsResult.getRecord(0).getFieldString("YD_CRN_MGNT_USE_YN7"), 1, 1)) ;
					outRec.setField("YD_CRN_MGNT_USE_YN8", YdUtils.fillSpZr( rsResult.getRecord(0).getFieldString("YD_CRN_MGNT_USE_YN8"), 1, 1)) ;
					outRec.setField("YD_CRN_MGNT_USE_YN9", YdUtils.fillSpZr( rsResult.getRecord(0).getFieldString("YD_CRN_MGNT_USE_YN9"), 1, 1)) ;
					outRec.setField("YD_CRN_MGNT_USE_YN10", YdUtils.fillSpZr( rsResult.getRecord(0).getFieldString("YD_CRN_MGNT_USE_YN10"), 1, 1)) ;
				}
			}
			else{

				if("X".equals(szYD_CRN_GRAB_TP)){
					outRec.setField("YD_CRN_MGNT_USE_EA", YdUtils.fillSpZr("10", 2, 1)) ;
				}else{
					outRec.setField("YD_CRN_MGNT_USE_EA", YdUtils.fillSpZr("9", 2, 1)) ;
				}

				String szYD_CRN_MGNT_USE_YN1 					= "Y";
				outRec.setField("YD_CRN_MGNT_USE_YN1", 			YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN1, 1, 1)) ;

				String szYD_CRN_MGNT_USE_YN2 					= "Y";
				outRec.setField("YD_CRN_MGNT_USE_YN2",			YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN2, 1, 1)) ;

				String szYD_CRN_MGNT_USE_YN3					= "Y";
				outRec.setField("YD_CRN_MGNT_USE_YN3",			YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN3, 1, 1)) ;

				String szYD_CRN_MGNT_USE_YN4					= "Y";
				outRec.setField("YD_CRN_MGNT_USE_YN4",			YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN4, 1, 1)) ;

				String szYD_CRN_MGNT_USE_YN5					= "Y";
				outRec.setField("YD_CRN_MGNT_USE_YN5",			YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN5, 1, 1)) ;

				String szYD_CRN_MGNT_USE_YN6					= "Y";
				outRec.setField("YD_CRN_MGNT_USE_YN6",			YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN6, 1, 1)) ;

				String szYD_CRN_MGNT_USE_YN7					= "Y";
				outRec.setField("YD_CRN_MGNT_USE_YN7",			YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN7, 1, 1)) ;

				String szYD_CRN_MGNT_USE_YN8					= "Y";
				outRec.setField("YD_CRN_MGNT_USE_YN8",			YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN8, 1, 1)) ;

				String szYD_CRN_MGNT_USE_YN9					= "Y";
				outRec.setField("YD_CRN_MGNT_USE_YN9",			YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN9, 1, 1)) ;

				String szYD_CRN_MGNT_USE_YN10					= "Y";
				outRec.setField("YD_CRN_MGNT_USE_YN10",			YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN10, 1, 1)) ;
			}

			//==========================================================================================================
			//=======================================================================
			// 차량형상 측정값을 확인하여 지시시 재계산한다.
			JDTORecord params = JDTORecordFactory.getInstance().create();
			JDTORecord calcXYZ = JDTORecordFactory.getInstance().create();
			params.setField("YD_CRN_SCH_ID", 		szYD_CRN_SCH_ID);
			params.setField("YD_EQP_ID", 			szYD_EQP_ID);
			params.setField("YD_UP_STK_COL_GP", 	ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LOC").substring(0, 6));
			params.setField("YD_UP_STK_BED_NO", 	ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LOC").substring(6, 8));
			params.setField("YD_DN_STK_COL_GP", 	ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC").substring(0, 6));
			params.setField("YD_DN_STK_BED_NO", 	ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC").substring(6, 8));

			String szYD_UP_WO_LOC_XAXIS = ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LOC_XAXIS");
			String szYD_UP_WO_LOC_YAXIS = ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LOC_YAXIS");
			String szYD_UP_WO_LOC_ZAXIS = ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LOC_ZAXIS");

			String szYD_DN_WO_LOC_XAXIS = ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC_XAXIS");
			String szYD_DN_WO_LOC_YAXIS = ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC_YAXIS");
			String szYD_DN_WO_LOC_ZAXIS = ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC_ZAXIS");

			String szYD_DN_WO_LOC_YAXIS1 = ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC_YAXIS1");
			String szYD_DN_WO_LOC_YAXIS2 = ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC_YAXIS2");
			String szYD_UP_WO_LOC_YAXIS1 = ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LOC_YAXIS1");
			String szYD_UP_WO_LOC_YAXIS2 = ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LOC_YAXIS2");

			// 차량형상 좌표계산하기
			if(PlateGdsYdUtil.isApplyYn("자동화크레인작업지시좌표계산여부")){
				PlateGdsYdUtil.procXYCalForPlateCrane3G(params, calcXYZ);

				szYD_UP_WO_LOC_XAXIS 					    = StringHelper.nvl(ydDaoUtils.paraRecChkNull(calcXYZ, "YD_STK_BED_UP_XAXIS"), szYD_UP_WO_LOC_XAXIS);
				szYD_UP_WO_LOC_YAXIS 					    = StringHelper.nvl(ydDaoUtils.paraRecChkNull(calcXYZ, "YD_STK_BED_UP_YAXIS"), szYD_UP_WO_LOC_YAXIS);

				szYD_DN_WO_LOC_XAXIS 					    = StringHelper.nvl(ydDaoUtils.paraRecChkNull(calcXYZ, "YD_STK_BED_DN_XAXIS"), szYD_DN_WO_LOC_XAXIS);
				szYD_DN_WO_LOC_YAXIS 					    = StringHelper.nvl(ydDaoUtils.paraRecChkNull(calcXYZ, "YD_STK_BED_DN_YAXIS"), szYD_DN_WO_LOC_YAXIS);

//				2021. 3. 16 재계산시 Grab값도 같이 변경
//				진행상태에 따른 좌표계산처리 여부는 확인 후
				szYD_UP_WO_LOC_YAXIS = StringHelper.nvl(ydDaoUtils.paraRecChkNull(calcXYZ, "Up_Grab_Y_Value"), szYD_UP_WO_LOC_YAXIS);
				szYD_UP_WO_LOC_YAXIS1 = StringHelper.nvl(ydDaoUtils.paraRecChkNull(calcXYZ, "Up_Grab_Y1_Value"), szYD_UP_WO_LOC_YAXIS1);
				szYD_UP_WO_LOC_YAXIS2 = StringHelper.nvl(ydDaoUtils.paraRecChkNull(calcXYZ, "Up_Grab_Y2_Value"), szYD_UP_WO_LOC_YAXIS2);

				szYD_DN_WO_LOC_YAXIS = StringHelper.nvl(ydDaoUtils.paraRecChkNull(calcXYZ, "Dn_Grab_Y_Value"), szYD_DN_WO_LOC_YAXIS);
				szYD_DN_WO_LOC_YAXIS1 = StringHelper.nvl(ydDaoUtils.paraRecChkNull(calcXYZ, "Dn_Grab_Y1_Value"), szYD_DN_WO_LOC_YAXIS1);
				szYD_DN_WO_LOC_YAXIS2 = StringHelper.nvl(ydDaoUtils.paraRecChkNull(calcXYZ, "Dn_Grab_Y2_Value"), szYD_DN_WO_LOC_YAXIS2);
			}
			// Z축 값을 재 계산처리한다.
			JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			params = JDTORecordFactory.getInstance().create();
	  		params.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
			params.setField("YD_UP_STK_COL_GP", 	ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LOC").substring(0, 6));
			params.setField("YD_UP_STK_BED_NO", 	ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LOC").substring(6, 8));
			params.setField("YD_DN_STK_COL_GP", 	ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC").substring(0, 6));
			params.setField("YD_DN_STK_BED_NO", 	ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC").substring(6, 8));
			if(commDao.select(params, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getCarPointFrmRsltByZ") > 0){
				szYD_UP_WO_LOC_ZAXIS = StringHelper.nvl(rsResult.getRecord(0).getFieldString("YD_STK_LYR_UP_ZAXIS"), szYD_UP_WO_LOC_ZAXIS);	//야드적치BedZ축 (권상)
				szYD_DN_WO_LOC_ZAXIS = StringHelper.nvl(rsResult.getRecord(0).getFieldString("YD_STK_LYR_DN_ZAXIS"), szYD_DN_WO_LOC_ZAXIS);	//야드적치BedZ축 (권하)
	  		}
			//==========================================================================================================

			String szYD_UP_WO_LOC 							= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LOC");
			outRec.setField("YD_UP_WO_LOC", 				YdUtils.fillSpZr(szYD_UP_WO_LOC, 8, 1)) ;

			String szYD_UP_WO_LAYER 						= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LAYER");
			outRec.setField("YD_UP_WO_LAYER", 				YdUtils.fillSpZr(szYD_UP_WO_LAYER, 3, 1)) ;

			outRec.setField("YD_UP_WO_LOC_XAXIS", 			PlateGdsYdUtil.genDecmailFomatter("0000000", szYD_UP_WO_LOC_XAXIS));
			outRec.setField("YD_UP_WO_XAXIS_GAP_MAX", 		PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_XAXIS_GAP_MAX")));
			outRec.setField("YD_UP_WO_XAXIS_GAP_MIN", 		PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_XAXIS_GAP_MIN")));


			outRec.setField("YD_UP_WO_LOC_YAXIS", 			PlateGdsYdUtil.genDecmailFomatter("00000", szYD_UP_WO_LOC_YAXIS));
			outRec.setField("YD_UP_WO_LOC_YAXIS1", 			PlateGdsYdUtil.genDecmailFomatter("00000", szYD_UP_WO_LOC_YAXIS1));
			outRec.setField("YD_UP_WO_LOC_YAXIS2", 			PlateGdsYdUtil.genDecmailFomatter("00000", szYD_UP_WO_LOC_YAXIS2));
			outRec.setField("YD_UP_WO_YAXIS_GAP_MAX", 		PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_YAXIS_GAP_MAX")) );
			outRec.setField("YD_UP_WO_YAXIS_GAP_MIN", 		PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_YAXIS_GAP_MIN")) );

			outRec.setField("YD_UP_WO_LOC_ZAXIS", 			PlateGdsYdUtil.genDecmailFomatter("00000", szYD_UP_WO_LOC_ZAXIS));
			outRec.setField("YD_UP_WO_ZAXIS_GAP_MAX", 		PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_ZAXIS_GAP_MAX")) ) ;
			outRec.setField("YD_UP_WO_ZAXIS_GAP_MIN", 		PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_ZAXIS_GAP_MIN")) ) ;

			String szYD_DN_WO_LOC 							= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC");
			outRec.setField("YD_DN_WO_LOC", 				YdUtils.fillSpZr(szYD_DN_WO_LOC, 8, 1)) ;

			String szYD_DN_WO_LAYER 						= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LAYER");
			outRec.setField("YD_DN_WO_LAYER", 				YdUtils.fillSpZr(szYD_DN_WO_LAYER, 3, 1)) ;

			outRec.setField("YD_DN_WO_LOC_XAXIS", 			PlateGdsYdUtil.genDecmailFomatter("0000000", szYD_DN_WO_LOC_XAXIS) );
			outRec.setField("YD_DN_WO_XAXIS_GAP_MAX", 		PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_XAXIS_GAP_MAX")) );
			outRec.setField("YD_DN_WO_XAXIS_GAP_MIN", 		PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_XAXIS_GAP_MIN")) );

			outRec.setField("YD_DN_WO_LOC_YAXIS", 			PlateGdsYdUtil.genDecmailFomatter("00000", szYD_DN_WO_LOC_YAXIS) );
			outRec.setField("YD_DN_WO_LOC_YAXIS1", 			PlateGdsYdUtil.genDecmailFomatter("00000", szYD_DN_WO_LOC_YAXIS1) );
			outRec.setField("YD_DN_WO_LOC_YAXIS2", 			PlateGdsYdUtil.genDecmailFomatter("00000", szYD_DN_WO_LOC_YAXIS2) );
			outRec.setField("YD_DN_WO_YAXIS_GAP_MAX", 		PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_YAXIS_GAP_MAX")) );
			outRec.setField("YD_DN_WO_YAXIS_GAP_MIN", 		PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_YAXIS_GAP_MIN")) );

			outRec.setField("YD_DN_WO_LOC_ZAXIS", 			PlateGdsYdUtil.genDecmailFomatter("00000", szYD_DN_WO_LOC_ZAXIS) );
			outRec.setField("YD_DN_WO_ZAXIS_GAP_MAX", 		PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_ZAXIS_GAP_MAX")) );
			outRec.setField("YD_DN_WO_ZAXIS_GAP_MIN", 		PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_ZAXIS_GAP_MIN")) );


			if("1".equals(szYD_WRK_PROG_STAT) && !"".equals(szYD_UP_WO_LOC)){
				szTemp = szYD_UP_WO_LOC.substring(2, 4);
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName,"\n권상지시위치.설비구분 :"+szTemp, YdConstant.DEBUG);
			}else if("3".equals(szYD_WRK_PROG_STAT) && !"".equals(szYD_UP_WO_LOC)){
				szTemp = szYD_DN_WO_LOC.substring(2, 4);
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName,"\n권하지시위치.설비구분 :"+szTemp, YdConstant.DEBUG);
			}

			// 설비ID2
			szYD_EQP_ID2 									= ydDaoUtils.paraRecChkNull(recPara, "IS_2UT_RETURN");
			outRec.setField("YD_EQP_ID2", 					YdUtils.fillSpZr(szYD_EQP_ID2, 6, 1));

			//=======================================================================================================================
			// [3] 작업예약 조회
			//
			// 2009.11.20
			// 권오창
			//=======================================================================================================================
			recPara2 = JDTORecordFactory.getInstance().create();
			rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
			recPara2.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID"));
			//PIDEV_S :병행가동용:PI_YD
			recPara2.setField("PI_YD",    	"J");
			intRtnVal2 = ydWrkbookDao.getYdWrkbook(recPara2, rsResult2, 2);

			/*
			 * T : 파일링가능, F : 파일링 SKIP   -- 파일링정보가 없을 경우.
             * S : 파일링가능, E : 파일링 SKIP   -- 파일링정보가 있는 경우.
			 */
			String sYdSchStGp = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara,"YD_SCH_ST_GP"), 1, 1);

			if("S".equals(sYdSchStGp)||"T".equals(sYdSchStGp)){
				sYdSchStGp = "T";
				//------------------------------------------------------------------------------------------------------------
    			//	전사물류개선 2022.11.29 L2 조민주부장 요청 막음(유,무인 모두 적용)
				//------------------------------------------------------------------------------------------------------------
				//JDTORecord parms                = JDTORecordFactory.getInstance().create();	//전문 Return
				//JDTORecordSet rsResultCrn 		= JDTORecordFactory.getInstance().createRecordSet("temp");
				//parms.setField("YD_EQP_ID", szYD_EQP_ID);

				//szMsg="[" + szOperationName + "] 크레인  :"+ szYD_EQP_ID;
				//ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);

				//if (commDao.select(parms, rsResultCrn,"com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqp") > 0)
				//{
				//	rsResultCrn.first();
				//	String sCrnWrkMode2 = rsResultCrn.getFieldString("YD_EQP_WRK_MODE2");

	    		//	szMsg="[" + szOperationName + "] 유무인 체크 :"+ sCrnWrkMode2;


	    		//	if ("A".equals(sCrnWrkMode2))
	    		//	{
	    				//220725 박성열 파일링 가능 일 때, 권하 위치를 권상과 똑같이 한다.
	    				//220808 박성열 무인일 때
	    				outRec.setField("YD_DN_WO_LOC", 				YdUtils.fillSpZr(szYD_UP_WO_LOC, 8, 1)) ;
	    				outRec.setField("YD_DN_WO_LAYER", 				YdUtils.fillSpZr(szYD_UP_WO_LAYER, 3, 1)) ;
	    				outRec.setField("YD_DN_WO_LOC_XAXIS", 			PlateGdsYdUtil.genDecmailFomatter("0000000", szYD_UP_WO_LOC_XAXIS));
	    				outRec.setField("YD_DN_WO_XAXIS_GAP_MAX", 		PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_XAXIS_GAP_MAX")));
	    				outRec.setField("YD_DN_WO_XAXIS_GAP_MIN", 		PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_XAXIS_GAP_MIN")));
	    				outRec.setField("YD_DN_WO_LOC_YAXIS", 			PlateGdsYdUtil.genDecmailFomatter("00000", szYD_UP_WO_LOC_YAXIS));
	    				outRec.setField("YD_DN_WO_LOC_YAXIS1", 			PlateGdsYdUtil.genDecmailFomatter("00000", szYD_UP_WO_LOC_YAXIS1));
	    				outRec.setField("YD_DN_WO_LOC_YAXIS2", 			PlateGdsYdUtil.genDecmailFomatter("00000", szYD_UP_WO_LOC_YAXIS2));
	    				outRec.setField("YD_DN_WO_YAXIS_GAP_MAX", 		PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_YAXIS_GAP_MAX")) );
	    				outRec.setField("YD_DN_WO_YAXIS_GAP_MIN", 		PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_YAXIS_GAP_MIN")) );
	    				outRec.setField("YD_DN_WO_LOC_ZAXIS", 			PlateGdsYdUtil.genDecmailFomatter("00000", szYD_UP_WO_LOC_ZAXIS));
	    				outRec.setField("YD_DN_WO_ZAXIS_GAP_MAX", 		PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_ZAXIS_GAP_MAX")) ) ;
	    				outRec.setField("YD_DN_WO_ZAXIS_GAP_MIN", 		PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_ZAXIS_GAP_MIN")) ) ;
	    		//	}
			    //}
			}else{
				sYdSchStGp = "F";
			}
			if(intRtnVal2 <= 0) {
				szMsg = "작업예약 테이블 조회건수 없음 [Ret : " + intRtnVal2 + "]";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);

				// 야드대차목적동[야드대차목적동] : 2012.06.15 윤재광 크레인파일링 지시 구분자로 사용 - S/T : 파일링지시
				outRec.setField("YD_TC_AIM_BAY_GP", sYdSchStGp);

				// 야드차량사용구분 [야드차량사용구분]
				outRec.setField("YD_CAR_USE_GP", YdUtils.fillSpZr(" ", 1, 1));

				// 차량번호 [차량번호]
				outRec.setField("CAR_NO", YdUtils.fillSpZr_KOR(" ", 15, 1));

				// 운송장비코드 [운송장비코드]
				outRec.setField("TRN_EQP_CD", YdUtils.fillSpZr(" ", 8, 1));

				// 야드설비작업매수 [야드설비작업매수]
				outRec.setField("YD_EQP_WRK_SH", YdUtils.fillSpZr(" ", 2, 1));

				// 야드설비잔여매수 [야드설비잔여매수]
				outRec.setField("YD_EQP_RMN_SH", YdUtils.fillSpZr(" ", 2, 1));
			}else {
				rsResult2.first();
				recGetVal2 = rsResult2.getRecord();

				// 야드대차목적동[야드대차목적동] : 2012.06.15 윤재광 크레인파일링 지시 구분자로 사용 - S/T : 파일링지시
				outRec.setField("YD_TC_AIM_BAY_GP", sYdSchStGp);

				// 야드차량사용구분 [야드차량사용구분]
				outRec.setField("YD_CAR_USE_GP", YdUtils.fillSpZr(recGetVal2.getFieldString("YD_CAR_USE_GP"), 1, 1));

				// 차량번호 [차량번호]
				outRec.setField("CAR_NO", YdUtils.fillSpZr_KOR(recGetVal2.getFieldString("CAR_NO"), 15, 1));

				// 운송장비코드 [운송장비코드] -> 대기차량수 [대기차량수]
				outRec.setField("TRN_EQP_CD", YdUtils.fillSpZr(recGetVal2.getFieldString("CAR_CNT"), 8, 1));

				// 야드설비작업매수 [야드설비작업매수]
				outRec.setField("YD_EQP_WRK_SH", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal2, "TOT_CNT"), 2, 1));

				// 야드설비잔여매수 [야드설비잔여매수]
				outRec.setField("YD_EQP_RMN_SH", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal2, "REM_CNT"), 2, 1));
			}

			if(intRtnVal >10){
				intRtnVal = 10;
			}
			/**
			 *  L2의 요청으로 인한 빈자리 0으로 채우기
			 *  YD_MTL_T	NUMBER(6,3)
				YD_MTL_W	NUMBER(5,1)
				YD_MTL_L	NUMBER(7,0)
				YD_MTL_WT	NUMBER(5,0)
			 */
			// GROUP{10] ------------------------------------------------------------------------------------------
			for(int i=0; i<intRtnVal; i++){
				recPara = rsGetYdCrnsch.getRecord(i);

				String szSTL_NO									= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				outRec.setField("STL_NO"+(i+1), 				YdUtils.fillSpZr(szSTL_NO, 11, 1)) ;
				outRec.setField("YD_STL_WT"+(i+1), 				PlateGdsYdUtil.genDecmailFomatter("00000", ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_WT")));
				outRec.setField("YD_STL_T"+(i+1), 				PlateGdsYdUtil.genDecmailFomatter("000.000", ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_T"))) ;
				outRec.setField("YD_STL_W"+(i+1), 				PlateGdsYdUtil.genDecmailFomatter("0000.0", ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_W"))) ;
				outRec.setField("YD_STL_L"+(i+1), 				PlateGdsYdUtil.genDecmailFomatter("0000000", ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_L"))) ;
			}
			for(int j=intRtnVal; j<10; j++){

				String szSTL_NO									= " ";
				outRec.setField("STL_NO"+(j+1), 				YdUtils.fillSpZr(szSTL_NO, 11, 1)) ;
				outRec.setField("YD_STL_WT"+(j+1), 				PlateGdsYdUtil.genDecmailFomatter("00000", "0")) ;
				outRec.setField("YD_STL_T"+(j+1), 				PlateGdsYdUtil.genDecmailFomatter("000.000", "0")) ;
				outRec.setField("YD_STL_W"+(j+1), 				PlateGdsYdUtil.genDecmailFomatter("0000.0", "0")) ;
				outRec.setField("YD_STL_L"+(j+1), 				PlateGdsYdUtil.genDecmailFomatter("0000000", "0")) ;
			}
			//*	GROUP{10] -----------------------------------------------------------------------------------------END

			recPara = rsGetYdCrnsch.getRecord(0);

			String szYD_SCH_CD_NEXT = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD_NEXT");
			outRec.setField("YD_SCH_CD_NEXT", YdUtils.fillSpZr(szYD_SCH_CD_NEXT, 8, 1)) ;

			if(!"".equals(szYD_SCH_CD_NEXT.trim())){

				//-- 통합 크레인 스케줄
				recPara2 = JDTORecordFactory.getInstance().create();
				rsYdSchRule = JDTORecordFactory.getInstance().createRecordSet("");

				recPara2.setField("YD_SCH_CD", szYD_SCH_CD_NEXT);

				intRtnVal2 = commDao.select(recPara2, rsYdSchRule, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0098");

				if(intRtnVal2 <= 0) {
					szMsg ="스케쥴 기준 조회건수 없음 : [" +intRtnVal2+"]";
					ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);

					szYD_SCH_NAME_NEXT = YdUtils.fillSpZr_KOR(" ", 30, 1);

				} else {

					//레코드 추출
					rsYdSchRule.first();
					recPara3 = rsYdSchRule.getRecord();

					szYD_SCH_NAME_NEXT = YdUtils.fillSpZr_KOR(ydDaoUtils.paraRecChkNull(recPara3, "YD_SCH_CONTENTS"), 30, 1);
				}

			} else {
				szMsg = "스케쥴 코드_NEXT가 없어서 기준테이블을 조회 안함 : YD_SCH_NAME_NEXT는 공백";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);

				szYD_SCH_NAME_NEXT = " ";
			}

			outRec.setField("YD_SCH_NAME_NEXT", 			YdUtils.fillSpZr_KOR(szYD_AID_WRK_YN_NEXT+szYD_SCH_NAME_NEXT,30,1));

			String szYD_UP_WO_LOC_NEXT						= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LOC_NEXT");
			outRec.setField("YD_UP_WO_LOC_NEXT", 			YdUtils.fillSpZr(szYD_UP_WO_LOC_NEXT, 8, 1)) ;

			String szYD_UP_WO_LAYER_NEXT					= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LAYER_NEXT");
			outRec.setField("YD_UP_WO_LAYER_NEXT", 			YdUtils.fillSpZr(szYD_UP_WO_LAYER_NEXT, 3, 1));

			String szYD_DN_WO_LOC_NEXT						= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC_NEXT");
			outRec.setField("YD_DN_WO_LOC_NEXT", 			YdUtils.fillSpZr(szYD_DN_WO_LOC_NEXT, 8, 1)) ;

			String szYD_DN_WO_LAYER_NEXT					= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LAYER_NEXT");
			outRec.setField("YD_DN_WO_LAYER_NEXT", 			YdUtils.fillSpZr(szYD_DN_WO_LAYER_NEXT, 3, 1)) ;

			String szSTL_NO_NEXT							= ydDaoUtils.paraRecChkNull(recPara, "STL_NO_NEXT");
			outRec.setField("STL_NO_NEXT", 					YdUtils.fillSpZr(szSTL_NO_NEXT, 11, 1)) ;

			outRec.setField("YD_CRN_WRK_SH_NEXT", 			PlateGdsYdUtil.genDecmailFomatter("00", ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_WRK_SH_NEXT")) ) ;
			outRec.setField("YD_CRN_WRK_WT_NEXT", 			PlateGdsYdUtil.genDecmailFomatter("0000000", ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_WRK_WT_NEXT")) ) ;
			outRec.setField("YD_CRN_WRK_T_NEXT", 			PlateGdsYdUtil.genDecmailFomatter("0000.000", ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_WRK_T_NEXT")) );
			outRec.setField("YD_CRN_WRK_MAX_W_NEXT", 		PlateGdsYdUtil.genDecmailFomatter("0000.0", ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_WRK_MAX_W_NEXT")) );
			outRec.setField("YD_CRN_WRK_MAX_L_NEXT", 		PlateGdsYdUtil.genDecmailFomatter("0000000", ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_WRK_MAX_L_NEXT")) );

			//RecordSet으로 반환
			outRecSet.addRecord(outRec);


			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================[STEP 3]:: 송신 TC내용 ==============\n", YdConstant.DEBUG);
			ydUtils.displayRecord(szOperationName, outRec);

		}catch(Exception e){

			szMsg = "YDY9L004[크레인작업지시] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}

		return outRecSet.size();

	} // end of makeY9L004()






	/**
	 * YDY9L005 : 크레인작업실적응답
	 * @param  JDTORecord inRec :: [YD_GP / YD_SCH_CD / YD_CRN_SCH_ID / YD_L2_WR_GP / YD_L3_HD_RS_CD]
	 * @return JDTORecordSet outRecSet
	 *
	 */
	public static int makeY9L005(JDTORecord inRec, JDTORecordSet outRecSet){

		YdUtils ydUtils 			= new YdUtils();
		YdDaoUtils ydDaoUtils 		= new YdDaoUtils();

		JDTORecord outRec 			= null;

		// 변수선언
		String szMethodName 		= "makeY9L005";
		String szMsg 				= "";
		String szOperationName      = "통합후판제품L2 크레인작업실적응답";

		// 야드설비ID
		String szYD_EQP_ID	 		= "";

		// 야드작업진행상태
		String szYD_WRK_PROG_STAT	= "";

		// 야드스케쥴코드
		String szYD_SCH_CD	 		= "";

		// 야드설비스케쥴ID
		String szYD_CRN_SCH_ID	 	= "";

		// 야드L2실적구분
		String szYD_L2_WR_GP	 	= "";

		// 야드L3처리결과코드
		String szYD_L3_HD_RS_CD	 	= "";

		// 야드L3MESSAGE
		String szTemp   			= "";

		// TC Length =138 /60+78
		int nTcLen 					= 78;

		try{

			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================[STEP 1]::makeY9L005==============\n", YdConstant.DEBUG);

			ydUtils.displayRecord(szOperationName, inRec);

			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 업무 : 크레인작업실적응답 전문 편집(YDY1L005)
	         * 		U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하에 따른 전문편집
	         *     (U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하)
	         * 수정자 : 임춘수
	         * 일자 : 2009.06.17
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			szYD_L2_WR_GP = ydDaoUtils.paraRecChkNull(inRec, "YD_L2_WR_GP");
			if(  YdConstant.CRN_WRK_RE_LD_WR.equals(szYD_L2_WR_GP)) {					//U:권상실적
				szTemp = "권상실적";
			}else if(  YdConstant.CRN_WRK_RE_DN_WR.equals(szYD_L2_WR_GP)) {				//D:권하실적
				szTemp = "권하실적";
			}else if(  YdConstant.CRN_WRK_RE_EMG_PTOP.equals(szYD_L2_WR_GP)) {			//E:비상조업실적
				szTemp = "비상조업실적";
			}else if(  YdConstant.CRN_WRK_RE_TRBL.equals(szYD_L2_WR_GP)) {				//R:복구
				szTemp = "복구";
			}else if(  YdConstant.CRN_WRK_RE_BREAK.equals(szYD_L2_WR_GP)) {				//B:고장
				szTemp = "고장";
			}else if(  YdConstant.CRN_WRK_RE_MD_MOD.equals(szYD_L2_WR_GP)) {			//M:모드변경
				szTemp = "모드변경";
				//szTemp = "설비운전모드전환";
			}else if(  YdConstant.CRN_WRK_RE_WO_DMD.equals(szYD_L2_WR_GP)) {			//J:크레인작업지시요구
				szTemp = "지시요구";
			}else if(  YdConstant.CRN_WRK_RE_FRCE_DN.equals(szYD_L2_WR_GP)) {			//F:강제권하
				szTemp = "강제권하";
			}else{
				szMsg = "[크레인작업실적응답]야드L2실적구분 (" + szYD_L2_WR_GP + ")가 정의된 값이 아닙니다.";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);
				return -1;
			}

			szMsg = "[크레인작업실적응답]야드L2실적구분이 " + szTemp + " [" + szYD_L2_WR_GP + "] 입니다.";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);

			// 야드설비ID
			szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");

			// 야드작업진행상태
			szYD_WRK_PROG_STAT 	= ydDaoUtils.paraRecChkNull(inRec, "YD_WRK_PROG_STAT");

			// 야드스케쥴코드
			szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(inRec, "YD_SCH_CD");

			// 야드설비스케쥴ID
			szYD_CRN_SCH_ID 	= ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID");

			// 야드L3처리결과코드
			szYD_L3_HD_RS_CD 	= ydDaoUtils.paraRecChkNull(inRec, "YD_L3_HD_RS_CD");

			// 야드L3MESSAGE
			if( !"".equals(ydDaoUtils.paraRecChkNull(inRec, "YD_L3_MSG")) ){
				szTemp += " "  + ydDaoUtils.paraRecChkNull(inRec, "YD_L3_MSG") + " " ;
			}

			if( YdConstant.CRN_WRK_RE_CD_NORMAL_HD.equals(szYD_L3_HD_RS_CD) ){
				szTemp += "이 정상 처리 되었습니다.";
			}
			else if(YdConstant.CRN_WRK_RE_CD_NO_WRK.equals(szYD_L3_HD_RS_CD)){
				if("".equals(ydDaoUtils.paraRecChkNull(inRec, "YD_L3_MSG"))){
					szTemp += "정보 없음.";
				}
			}
			else{
				szTemp += "이 Error처리 되었습니다.";
			}

			outRec = JDTORecordFactory.getInstance().create();

			//		1.	전문 ID				MSG_ID				VARCHAR2(8)		YDY9L005
			//		2.	생성일				DATE				VARCHAR2(10)	YYYY-MM-DD
			//		3.	생성시간				TIME				VARCHAR2(8)		24HH-MM-SS
			//		4.	전문구분				MSG_GP				VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
			//		5.	전문길이				MSG_LEN				NUMBER  (4)
			//		6.	임시					TEMP				VARCHAR2(29)
			//		7.	야드설비ID			YD_EQP_ID			VARCHAR2(6)		크레인설비 ID
			//		8.	야드작업진행상태		YD_WRK_PROG_STAT	VARCHAR2(1)		"2" 권상실적, "4" 권하실적
			//		9.	야드스케쥴코드			YD_SCH_CD			VARCHAR2(8)
			//		10.	야드설비스케쥴ID		YD_CRN_SCH_ID		VARCHAR2(18)
			//*		11.	야드L2실적구분			YD_L2_WR_GP			VARCHAR2(1)		U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경
			//*		12.	야드L3처리결과코드		YD_L3_HD_RS_CD		VARCHAR2(4)		0000:정상
			//*		13.	야드L3MESSAGE		YD_L3_MSG			VARCHAR2(40)	"권상(또는 권하)실적이 정상 처리 되었습니다.
			//																	  권상(또는 권하)실적이 Error 처리 되었습니다."
			outRec.setField("MSG_ID", 				"YDY9L005");
			outRec.setField("DATE", 				YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME", 				YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP", 				"I");
			outRec.setField("MSG_LEN", 				YdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0));
			outRec.setField("TEMP", 				YdUtils.fillSpZr("", 29, 1));
			outRec.setField("YD_EQP_ID", 			YdUtils.fillSpZr(szYD_EQP_ID, 6, 1));
			outRec.setField("YD_WRK_PROG_STAT", 	YdUtils.fillSpZr(szYD_WRK_PROG_STAT, 1, 1));
			outRec.setField("YD_SCH_CD", 			YdUtils.fillSpZr(szYD_SCH_CD, 8, 1));
			outRec.setField("YD_CRN_SCH_ID", 		YdUtils.fillSpZr(szYD_CRN_SCH_ID, 18, 1));
			outRec.setField("YD_L2_WR_GP", 			YdUtils.fillSpZr(szYD_L2_WR_GP, 1, 1));
			outRec.setField("YD_L3_HD_RS_CD", 		YdUtils.fillSpZr(szYD_L3_HD_RS_CD, 4, 1));
			outRec.setField("YD_L3_MSG", 			YdUtils.fillSpZr_KOR(szTemp, 40, 1));

			// RecordSet으로 반환
			outRecSet.addRecord(outRec);

			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================[STEP 3]:: 송신 TC내용 ==============\n", YdConstant.DEBUG);
			ydUtils.displayRecord(szOperationName, outRec);

		}catch(Exception e){
			szMsg = "YDY9L005[크레인작업실적응답] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}

		return outRecSet.size();

	} // end of makeY9L005()


	/**
	 * YDY9L010 : 상하차실적
	 * @param  JDTORecord inRec :: [YD_CRN_SCH_ID / CRN_JOB_RST_TYPE ]
	 * @return JDTORecordSet outRecSet
	 *
	 */
	public static int makeY9L010(JDTORecord inRec, JDTORecordSet outRecSet){
		YdUtils ydUtils 			= new YdUtils();
		YdDaoUtils ydDaoUtils 		= new YdDaoUtils();

		// 변수선언
		String szMethodName 		= "YDY9L010[상하차실적전송]";
		String szMsg 				= "";

		// 야드설비ID
		String szYD_CRN_SCH_ID	  = "";
		String szCRN_JOB_RST_TYPE = "";
		YdPlateCommDAO commDao = new YdPlateCommDAO();

		try{
			szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID");
			szCRN_JOB_RST_TYPE = ydDaoUtils.paraRecChkNull(inRec, "CRN_JOB_RST_TYPE");

			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================[STEP 1]::makeY9L010==============\n", YdConstant.DEBUG);

			JDTORecord recIn = JDTORecordFactory.getInstance().create();
			JDTORecordSet rsGetYdCrnsch  = JDTORecordFactory.getInstance().createRecordSet("");
			recIn.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
			recIn.setField("CRN_JOB_RST_TYPE", szCRN_JOB_RST_TYPE);

			if(commDao.select(recIn, rsGetYdCrnsch, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getYDY9L010") > 0 ){


				JDTORecord recordTmp = rsGetYdCrnsch.getRecord(0);
				JDTORecord sendMsg = JDTORecordFactory.getInstance().create();
				sendMsg.setField("MSG_ID",recordTmp.getFieldString("MSG_ID"));
				sendMsg.setField("DATE",recordTmp.getFieldString("DATE"));
				sendMsg.setField("TIME",recordTmp.getFieldString("TIME"));
				sendMsg.setField("MSG_GP",recordTmp.getFieldString("MSG_GP"));
				sendMsg.setField("MSG_LEN",recordTmp.getFieldString("MSG_LEN"));
				sendMsg.setField("TEMP",recordTmp.getFieldString("TEMP"));
				sendMsg.setField("PT_LOAD_LOC",recordTmp.getFieldString("PT_LOAD_LOC"));
				sendMsg.setField("CAR_NO",recordTmp.getFieldString("CAR_NO"));
				sendMsg.setField("YD_WRK_PROG_STAT",recordTmp.getFieldString("YD_WRK_PROG_STAT"));
				sendMsg.setField("WORK_COIL_MAX_CNT",recordTmp.getFieldString("WORK_COIL_MAX_CNT"));
				sendMsg.setField("STOCK_ID1",recordTmp.getFieldString("STOCK_ID1"));
				sendMsg.setField("STOCK_ID2",recordTmp.getFieldString("STOCK_ID2"));
				sendMsg.setField("STOCK_ID3",recordTmp.getFieldString("STOCK_ID3"));
				sendMsg.setField("STOCK_ID4",recordTmp.getFieldString("STOCK_ID4"));
				sendMsg.setField("STOCK_ID5",recordTmp.getFieldString("STOCK_ID5"));
				sendMsg.setField("STOCK_ID6",recordTmp.getFieldString("STOCK_ID6"));
				sendMsg.setField("STOCK_ID7",recordTmp.getFieldString("STOCK_ID7"));
				sendMsg.setField("STOCK_ID8",recordTmp.getFieldString("STOCK_ID8"));
				sendMsg.setField("STOCK_ID9",recordTmp.getFieldString("STOCK_ID9"));
				sendMsg.setField("STOCK_ID10",recordTmp.getFieldString("STOCK_ID10"));
				sendMsg.setField("SPARE",recordTmp.getFieldString("SPARE"));

				outRecSet.addRecord(sendMsg);

			}else{
				szMsg = "makeY9L010[상하차실적] 데이터가 존재하지 않습니다.";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.ERROR);
				return -1;
			}

			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================[STEP 3]:: 송신 완료 ==============\n", YdConstant.DEBUG);
		}catch(Exception e){
			szMsg = "makeY9L010[상하차실적] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}

		return outRecSet.size();
	}


	//---------------------------------------------------------------------------
}	// end of class MakeTcY9
