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
import com.inisteel.cim.yd.common.rule.GetBreRule6;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;





/**
 * Y4 (후판제품야드L2) 송신 용 전문 생성
 * @author 
 *
 */
public class MakeTcY4 {

	// YDY4L001	저장위치제원	
	// YDY4L002	저장품제원	
	// YDY4L004	크레인작업지시	
	// YDY4L005	크레인작업실적응답	
	
	
	
	// 클래스명
	private static final String szClassName  = MakeTcY4.class.getName();
	
	
	/**
	 * YDY4L001 : 저장위치제원
	 * @param JDTORecord inRec
	 * @return JDTORecordSet outRecSet
	 */
	public static int makeY4L001(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDY4L001
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
		String szMethodName     	 = "makeY4L001";
		String szMsg        	     = "";
		String szOperationName       = "A후판제품L2 저장위치제원";
		
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

		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeY4L001() IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
	
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
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			} else if(intRtnVal == 0){
				szMsg = "적치BED 테이블 조회건수 없음 YD_STK_COL_GP(" + szYD_STK_COL_GP + ") YD_STK_BED_NO(" + szYD_STK_BED_NO + ") " + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				return 0;
			}
			
			// 적치bed 조회결과 추출
			for(int nIdx=0; nIdx<intRtnVal; nIdx++){
				recGetVal = rsResult.getRecord(nIdx);
				
				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();								
				outRec.setField("MSG_ID" , "YDY4L001");
				outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
				outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
				outRec.setField("MSG_GP" , "I");
				outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
				outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));
				
				// 야드정보동기화코드 [야드정보동기화코드]
				outRec.setField("YD_INFO_SYNC_CD", (YdUtils.fillSpZr(szYD_INFO_SYNC_CD, 1, 1)));
				
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
				outRec.setField("YD_STK_BED_XAXIS", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_XAXIS"), 7, 1));				
				
				// 야드적치BedY축 [야드적치BedY축]
				outRec.setField("YD_STK_BED_YAXIS", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_YAXIS"), 5, 1));				
				
				// 야드적치BedZ축 [야드적치BedZ축]
				outRec.setField("YD_STK_BED_ZAXIS", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_ZAXIS"), 5, 1));				
				
				// 야드적치Bed단Max [야드적치Bed단Max]
				outRec.setField("YD_STK_BED_LYR_MAX", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_LYR_MAX"), 3, 1));				
				
				// 야드적치Bed중량Max [야드적치Bed중량Max]
				outRec.setField("YD_STK_BED_WT_MAX", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_WT_MAX"), 7, 1));				
				
				// 야드적치Bed높이Max [야드적치Bed높이Max]
				outRec.setField("YD_STK_BED_H_MAX", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_H_MAX"), 5, 1));				
				
				// 야드적치Bed길이Max [야드적치Bed길이Max]
				outRec.setField("YD_STK_BED_L_MAX", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_L_MAX"), 5, 1));				
				
				// 야드적치Bed폭Max [야드적치Bed폭Max]
				szConv = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_W_MAX"), 6, 1);
				outRec.setField("YD_STK_BED_W_MAX", ydUtils.FloatLRPAD(szConv, 5, 1, '0'));			
				
				// 차량정보
				szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recGetVal, "YD_CAR_USE_GP").trim();
				szTRN_EQP_CD    = ydDaoUtils.paraRecChkNull(recGetVal, "TRN_EQP_CD");
				szCAR_NO        = ydDaoUtils.paraRecChkNull(recGetVal, "CAR_NO");
				szCARD_NO       = ydDaoUtils.paraRecChkNull(recGetVal, "CARD_NO");

				//=======================================================================================================================
				// 차량스케쥴 조회
				//=======================================================================================================================
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_COL_GP");
				if((szYD_STK_COL_GP.substring(2, 4)).equals("PT")){
					// 야드차량목표야드구분 [야드차량목표야드구분]
					rsResultCarSch = JDTORecordFactory.getInstance().createRecordSet("");
					if(szYD_CAR_USE_GP.equals("L")){
						recParaCarSch.setField("TRN_EQP_CD", szTRN_EQP_CD);
						intRtnValCarSch = ydCarSchDao.getYdCarsch(recParaCarSch, rsResultCarSch, 7);
						if(intRtnValCarSch <= 0) {
							szYD_CAR_AIM_YD_GP = YdUtils.fillSpZr(" ", 1, 1);							
						}else{
							rsResultCarSch.first();
							recGetValCarSch = rsResultCarSch.getRecord();
							szTemp = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetValCarSch, "YD_CARUD_STOP_LOC"), 6, 1).trim();
							if(szTemp.trim().equals("") ){
								szYD_CAR_AIM_YD_GP = YdUtils.fillSpZr(" ", 1, 1);
							}else {
								szYD_CAR_AIM_YD_GP = YdUtils.fillSpZr(szTemp.substring(0, 1), 1, 1);
							}							
						}
					} else if(szYD_CAR_USE_GP.equals("G")){
						recParaCarSch.setField("CAR_NO", szCAR_NO);
						recParaCarSch.setField("CARD_NO", szCARD_NO);
						intRtnValCarSch = ydCarSchDao.getYdCarsch(recParaCarSch, rsResultCarSch, 11);
						if(intRtnValCarSch <= 0) {
							szYD_CAR_AIM_YD_GP = YdUtils.fillSpZr(" ", 1, 1);							
						}else{
							rsResultCarSch.first();
							recGetValCarSch = rsResultCarSch.getRecord();
							szTemp = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetValCarSch, "YD_CARUD_STOP_LOC"), 6, 1).trim();
							if(szTemp.trim().equals("")){
								szYD_CAR_AIM_YD_GP = YdUtils.fillSpZr(" ", 1, 1);
							}else {
								szYD_CAR_AIM_YD_GP = YdUtils.fillSpZr(szTemp.substring(0, 1), 1, 1);
							}							
						}
					}
					
					// 야드차량착발상태 [야드차량진행상태]
					if(szYD_CAR_PROG_STAT.equals("1") || szYD_CAR_PROG_STAT.equals("A")){
						szYD_CAR_ARRSTRT_STAT = YdUtils.fillSpZr("S", 1, 1);				
					}else if(szYD_CAR_PROG_STAT.equals("2") || szYD_CAR_PROG_STAT.equals("B")){
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
				ydUtils.putLog(szClassName, szMethodName, "\n======================makeY4L001() OUT("+ Integer.toString(nIdx) + ")==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
			} // end of for
		}catch(Exception e){
			szMsg = "Y4(후판제품야드L2) 송신  저장위치제원  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}

		return outRecSet.size();
	} // end of makeY4L001()
	
	
	
	
	
	
	/**
	 * YDY4L002 : 저장품제원
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeY4L002(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID						MSG_ID					VARCHAR2(8)		YDY4L002
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
		String szMethodName     = "makeY4L002";
		String szMsg            = "";
		String szOperationName  = "A후판제품L2 저장품제원";
		
		String szConv           = "";
		
		
		String szSTLKIND_CD     = "";
		String szSPEC_ABBSYM    = "";
		String szMILL_PLNT_GP   = "";
		String szSCARFING_DEPTH = "";
		String szYD_INFO_SYNC_CD = "";
		String szDEL_YN_CHECK   = "";
		String szCUST_KO_NAME   = "";
		// 리턴값
		int intRtnVal           = 0;
		
		// TC Length = 309 (60 + 249)
		int nTcLen              = 249;
		
		String szSTLNO          = "";
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeY4L002() IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);

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
			
			// 재료정보 유무에 따른 조회
			if(szYD_INFO_SYNC_CD.equals("1") || szYD_INFO_SYNC_CD.equals("2") || szYD_INFO_SYNC_CD.equals("3") || szYD_INFO_SYNC_CD.equals("4")){
				recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP"));
				recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO"));
				intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 120);
			} else if(szYD_INFO_SYNC_CD.equals("5") || szYD_INFO_SYNC_CD.equals("A") || szYD_INFO_SYNC_CD.equals("B") || szYD_INFO_SYNC_CD.equals("C")){
				recPara.setField("STL_NO"       , szSTLNO);

				if(szDEL_YN_CHECK.equals("N")){
					intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 180);
				}else {
					intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 26);
				}
			}
			
			if(intRtnVal < 0) {
				szMsg = "저장품 + 적치BED + 적치단  테이블 조회오류  YD_INFO_SYNC_CD(" + szYD_INFO_SYNC_CD + ") YD_STK_COL_GP(" + ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP") + ") YD_STK_BED_NO(" + ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO") + ") STL_NO(" + ydDaoUtils.paraRecChkNull(inRec, "STL_NO") +  ") [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			} else if(intRtnVal == 0) {
				szMsg = "저장품 + 적치BED + 적치단  테이블 조회건수 없음  YD_INFO_SYNC_CD(" + szYD_INFO_SYNC_CD + ") YD_STK_COL_GP(" + ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP") + ") YD_STK_BED_NO(" + ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO") + ") STL_NO(" + ydDaoUtils.paraRecChkNull(inRec, "STL_NO") +  ") [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;
			} else {
				szMsg = "저장품 + 적치BED + 적치단  테이블 조회성공 YD_INFO_SYNC_CD(" + szYD_INFO_SYNC_CD + ") YD_STK_COL_GP(" + ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP") + ") YD_STK_BED_NO(" + ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO") + ") STLNO(" + szSTLNO +  ") [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);				
			}	
			
			for(int nIdx=0; nIdx<intRtnVal; nIdx++) {
				recGetVal = rsResult.getRecord(nIdx);	

				
				if("".equals(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_COL_GP"))) {
					//저장위치가 없으면  L2로 전송하지 않는다. -- 2013.09.12 L2 요구사항
					continue;
				}
				
				
				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();									
				outRec.setField("MSG_ID" , "YDY4L002");
				outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
				outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
				outRec.setField("MSG_GP" , "I");
				outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
				outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

				// 야드정보동기화코드 [야드정보동기화코드]
				outRec.setField("YD_INFO_SYNC_CD", YdUtils.fillSpZr(szYD_INFO_SYNC_CD, 1, 1));
								
				// 야드재료정보송신매수 [야드재료정보송신매수]
				outRec.setField("YD_STL_INFO_SND_SH", YdUtils.fillSpZr(Integer.toString(intRtnVal), 3, 1));	
				
				// 야드재료정보송신순번 [야드재료정보송신순번]
				outRec.setField("YD_STL_INFO_SND_CNT", YdUtils.fillSpZr(Integer.toString(nIdx+1), 3, 1));	
				
				// 재료외형구분 [재료외형구분]
				outRec.setField("STL_APPEAR_GP", YdUtils.fillSpZr(recGetVal.getFieldString("STL_APPEAR_GP"), 1, 1));	
				
				// 재료번호 [재료번호]
				outRec.setField("STL_NO", YdUtils.fillSpZr(recGetVal.getFieldString("STL_NO"), 11, 1));	
				
				// 야드저장위치 [야드저장위치 : BED까지]  
				outRec.setField("YD_STR_LOC", YdUtils.fillSpZr(recGetVal.getFieldString("YD_STK_COL_GP")+recGetVal.getFieldString("YD_STK_BED_NO"), 8, 1));	
			
				// 야드적치단번호 [야드적치단번호]
				outRec.setField("YD_STK_LYR_NO", YdUtils.fillSpZr(recGetVal.getFieldString("YD_STK_LYR_NO"), 3, 1));	
				
				// 야드재료중량 [야드재료중량]
				outRec.setField("YD_STL_WT", YdUtils.fillSpZr(recGetVal.getFieldString("YD_STL_WT"), 5, 1));	
				
				// 야드재료두께 [야드재료두께]
				szConv = YdUtils.fillSpZr(recGetVal.getFieldString("YD_STL_T"), 7, 1);
				outRec.setField("YD_STL_T", ydUtils.FloatLRPAD(szConv, 6, 3, '0'));	
				
				// 야드재료폭 [야드재료폭]
				szConv = YdUtils.fillSpZr(recGetVal.getFieldString("YD_STL_W"), 6, 1);
				outRec.setField("YD_STL_W", ydUtils.FloatLRPAD(szConv, 5, 1, '0'));	
					
				// 야드재료길이 [야드재료길이]
				outRec.setField("YD_STL_L", YdUtils.fillSpZr(recGetVal.getFieldString("YD_STL_L"), 7, 1));	
				
				// 재료외경
				outRec.setField("MAT_ODIA", YdUtils.fillSpZr(recGetVal.getFieldString("MAT_ODIA"), 5, 1));	
				
				// 재료내경
				szConv = YdUtils.fillSpZr(recGetVal.getFieldString("MAT_IDIA"), 6, 1);
				outRec.setField("MAT_IDIA", ydUtils.FloatLRPAD(szConv, 5, 1, '0'));	
										
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
				outRec.setField("CURR_PROG_CD", YdUtils.fillSpZr(recGetVal.getFieldString("CURR_PROG_CD"), 1, 1));

				// 주문여재구분 [주문여재구분]
				outRec.setField("ORD_YEOJAE_GP", YdUtils.fillSpZr(recGetVal.getFieldString("ORD_YEOJAE_GP"), 1, 1));

				// 주문번호 [주문번호]
				outRec.setField("ORD_NO", YdUtils.fillSpZr(recGetVal.getFieldString("ORD_NO"), 10, 1));

				// 주문행번 [주문행번]
				outRec.setField("ORD_DTL", YdUtils.fillSpZr(recGetVal.getFieldString("ORD_DTL"), 3, 1));

				// 구입SLAB번호 [구입SLAB번호] - 고객코드명으로 송신.
				//outRec.setField("BUY_SLAB_NO", YdUtils.fillSpZr(recGetVal.getFieldString("CUST_KO_NAME"), 30, 1));
				
				szCUST_KO_NAME = ydDaoUtils.paraRecChkNull(recGetVal, "CUST_KO_NAME");
				if("".equals(szCUST_KO_NAME)) szCUST_KO_NAME = YdUtils.fillSpZr_KOR(" ", 30, 1);	
				
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
				
				// 재열재구분 [재열재구분]
				outRec.setField("REHEAT_SLAB_GP", YdUtils.fillSpZr(recGetVal.getFieldString("REHEAT_SLAB_GP"), 1, 1));

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
					
				// RecordSet에 추가	
				outRecSet.addRecord(outRec);				
				
				// Debug MSG
				ydUtils.putLog(szClassName, szMethodName, "\n======================makeY4L002() OUT("+ Integer.toString(nIdx) + ")==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
			}
		}catch(Exception e){
			szMsg = "Y4(후판제품야드L2) 송신  저장품제원  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}

		return outRecSet.size();
	} // end of makeY4L002()
	
	
	
	
	
	
	/**
	 * YDY4L004 : 크레인작업지시
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec	
	 */
	public static int makeY4L004(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID						MSG_ID						VARCHAR2(8)		YDY4L004
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
		String szMethodName         = "makeY4L004";
		String szMsg                = "";
		String szOperationName      = "A후판제품L2 크레인작업지시";
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

		// TC Length =664 ( 665 + 60 = 725)
		int nTcLen 		= 725;
		int intRtnVal 	= 0;
		int intRtnVal1  = 0;
		int intRtnVal2  = 0;
		int nGrabRet    = 0;
	
		String szUsageYn = "N";	
		
		try{
			ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 1]::makeY4L004==============\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);

			szCrnSchID = ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID");
			szYD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(inRec, "YD_WRK_PROG_STAT");
			szMSG_GP = ydDaoUtils.paraRecChkNull(inRec, "MSG_GP");
		
			recIn = JDTORecordFactory.getInstance().create();
			recIn.setField("YD_CRN_SCH_ID", szCrnSchID);
			recIn.setField("YD_WRK_PROG_STAT", szYD_WRK_PROG_STAT);

			// 크레인스케줄 조회
			intRtnVal = ydCrnSchDao.getYdCrnsch(recIn, rsGetYdCrnsch, 41);
			if(intRtnVal < 0){
				szMsg ="크레인스케줄 조회 중 오류 : [" +intRtnVal+"]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			} else if(intRtnVal ==0){
				szMsg ="크레인스케줄 조회건수 없음 : [" +intRtnVal+"]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				return 0;
			}
			
			
			// 크레인스케줄 조회결과 추출
			rsGetYdCrnsch.first();
			recPara = JDTORecordFactory.getInstance().create();
			outRec = JDTORecordFactory.getInstance().create();
			recPara = rsGetYdCrnsch.getRecord(0);
			
			ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 2]:: 조 회 결 과==============\n", YdConstant.DEBUG);
			ydUtils.displayRecord(szMethodName, recPara);


			outRec.setField("MSG_ID", 						new String("YDY4L004") );
			outRec.setField("DATE", 						new String(YdUtils.getCurDate("yyyy-MM-dd")) );
			outRec.setField("TIME", 						new String(YdUtils.getCurDate("HH-mm-ss")) );

			if(szMSG_GP.equals("D") || szMSG_GP.equals("U")){
				outRec.setField("MSG_GP" , YdUtils.fillSpZr(szMSG_GP, 1, 1));
			} else {
				outRec.setField("MSG_GP" , "I");
			}
			
			outRec.setField("MSG_LEN", 						new String(YdUtils.fillSpZr(""+nTcLen, 4, 0)) );
			outRec.setField("TEMP", 						new String(YdUtils.fillSpZr("", 29, 1)) );
			
		    String szYD_EQP_ID 								= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_ID");
			outRec.setField("YD_EQP_ID", 					(YdUtils.fillSpZr(szYD_EQP_ID, 6, 1)) );
			
			// 야드작업진행상태 [야드작업진행상태]
			szTemp2 = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_PROG_STAT"), 1, 1);
			if(szTemp2.equals("W"))
				outRec.setField("YD_WRK_PROG_STAT", YdUtils.fillSpZr("1", 1, 1));
			else
				outRec.setField("YD_WRK_PROG_STAT", YdUtils.fillSpZr(szTemp2, 1, 1));
					
			String szYD_SCH_CD 								= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");
			outRec.setField("YD_SCH_CD", 					(YdUtils.fillSpZr(szYD_SCH_CD, 8, 1)) );

			if(!szYD_SCH_CD.trim().equals("")){
				
				//-- 통합 크레인 스케줄
				recPara2 = JDTORecordFactory.getInstance().create();
				rsYdSchRule = JDTORecordFactory.getInstance().createRecordSet("");
				
				recPara2.setField("YD_SCH_CD", szYD_SCH_CD);
				
				intRtnVal1 = commDao.select(recPara2, rsYdSchRule, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0098");	
				
				if(intRtnVal1 <= 0) {
					szMsg ="스케쥴 기준 조회건수 없음 : [" +intRtnVal1+"]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
					
					szYD_SCH_NAME = "";
					szYD_EQP_ID2  = "";	
				} else {
				
					//레코드 추출
					rsYdSchRule.first();
					recPara3 = rsYdSchRule.getRecord();
					
					szYD_SCH_NAME 	= ydDaoUtils.paraRecChkNull(recPara3, "YD_SCH_CONTENTS");
					szYD_EQP_ID2 	=  "";
				}
				
			} else {
				szMsg = "스케쥴 코드가 없어서 기준테이블을 조회 안함 : YD_SCH_NAME, YD_EQP_ID2는 공백";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				
				szYD_SCH_NAME = YdUtils.fillSpZr_KOR(" ", 30, 1);	
				szYD_EQP_ID2 = YdUtils.fillSpZr(" ", 6, 1);											
			}
			
			outRec.setField("YD_SCH_NAME", 					(YdUtils.fillSpZr_KOR(szYD_SCH_NAME, 30, 1)) );
			
			String szYD_CRN_SCH_ID 							= ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_SCH_ID");
			outRec.setField("YD_CRN_SCH_ID", 				(YdUtils.fillSpZr(szYD_CRN_SCH_ID, 18, 1)) );
			
		    String szYD_CRN_WRK_SH 							= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_SH");
			outRec.setField("YD_CRN_WRK_SH", 				(YdUtils.fillSpZr(szYD_CRN_WRK_SH, 2, 1)) );
			
		    String szYD_CRN_WRK_WT 							= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_WT");
			outRec.setField("YD_CRN_WRK_WT", 				(YdUtils.fillSpZr(szYD_CRN_WRK_WT, 7, 1)) );
			
			szPoint 										= YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_T"), 8, 1);
		   	outRec.setField("YD_CRN_WRK_T", 				(ydUtils.FloatLRPAD(szPoint, 7, 3, '0')) );
			
		   	szPoint 										= YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_MAX_W"), 6, 1);
		   	outRec.setField("YD_CRN_WRK_MAX_W", 			(ydUtils.FloatLRPAD(szPoint, 5, 1, '0')) );
			
		    String szYD_CRN_WRK_MAX_L 						= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_MAX_L");
			outRec.setField("YD_CRN_WRK_MAX_L", 			(YdUtils.fillSpZr(szYD_CRN_WRK_MAX_L, 7, 1)) );
			
			String szYD_CRN_SCH_RMD_CNT 					= "02";
			outRec.setField("YD_CRN_SCH_RMD_CNT", 			(YdUtils.fillSpZr(szYD_CRN_SCH_RMD_CNT, 2, 1)) );
			
			
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
				outRec.setField("YD_CRN_GRAB_GP", (YdUtils.fillSpZr("E", 1, 1)));
			}else{
				outRec.setField("YD_CRN_GRAB_GP", (YdUtils.fillSpZr("D", 1, 1)));
			}
			
			//===============================================================================
			// 설비ID끝이 1이면 1호기 TWO GRAB => "E"   2면  2호기 ONE GRAB => "D" 
			//===============================================================================
			/*
			if(szYD_EQP_ID.substring(5).equals("1")){
				outRec.setField("YD_CRN_GRAB_GP", (YdUtils.fillSpZr("E", 1, 1)));
			} else if(szYD_EQP_ID.substring(5).equals("2")){
				outRec.setField("YD_CRN_GRAB_GP", (YdUtils.fillSpZr("D", 1, 1)));
			} else {
				szMsg = "*********** 설비ID값이 유효하지 않음 *****************";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);							

				outRec.setField("YD_CRN_GRAB_GP", (YdUtils.fillSpZr(" ", 1, 1)));
			}
			*/
			
			//===============================================================================
			// YD_CRN_SB_CTL_H(1 or 2) => (1 or 2)   YD_CRN_SB_CTL_H(3) => ("B")
			//===============================================================================
			szYD_CRN_SB_CTL_H = ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_SB_CTL_H");
			if(szYD_CRN_SB_CTL_H.equals("1") || szYD_CRN_SB_CTL_H.equals("2")) {
				outRec.setField("YD_CRN_GRAB_USE_GP", YdUtils.fillSpZr(szYD_CRN_SB_CTL_H, 1, 1));
			} else if(szYD_CRN_SB_CTL_H.equals("3")){
				outRec.setField("YD_CRN_GRAB_USE_GP", "B");
			} else {
				szMsg = "*********** 야드크레인SB제어높이 값이 유효하지 않음 YD_CRN_SB_CTL_H(" + szYD_CRN_SB_CTL_H + ")*****************";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);							
				
				outRec.setField("YD_CRN_GRAB_USE_GP", (YdUtils.fillSpZr(" ", 1, 1)));
			}
			
			
			// 2009.11.20 이것은 일단 공백으로 넣지 말라고 지시
		    String szYD_CRN_TT1_GRAB_NEW_AXIS_L 			= " ";
			outRec.setField("YD_CRN_TT1_GRAB_NEW_AXIS_L", 	(YdUtils.fillSpZr(szYD_CRN_TT1_GRAB_NEW_AXIS_L, 4, 1)) );
			
		    String szYD_CRN_TT2_GRAB_NEW_AXIS_L 			= " ";
			outRec.setField("YD_CRN_TT2_GRAB_NEW_AXIS_L", 	(YdUtils.fillSpZr(szYD_CRN_TT2_GRAB_NEW_AXIS_L, 4, 1)) );
			
			/*
			if(szYD_EQP_ID.substring(5).equals("1")){
				outRec.setField("YD_CRN_MGNT_USE_EA", (YdUtils.fillSpZr("10", 2, 1)) );
			} else if(szYD_EQP_ID.substring(5).equals("2")){
				outRec.setField("YD_CRN_MGNT_USE_EA", (YdUtils.fillSpZr("9", 2, 1)) );
			} else {
				szMsg = "*********** 설비ID값이 유효하지 않음  YD_CRN_MGNT_USE_EA 갯수 설정 오류 *****************";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);							

				outRec.setField("YD_CRN_MGNT_USE_EA", (YdUtils.fillSpZr(" ", 1, 1)));
			}
			*/
			if("X".equals(szYD_CRN_GRAB_TP)){
				outRec.setField("YD_CRN_MGNT_USE_EA", (YdUtils.fillSpZr("10", 2, 1)) );
			}else{
				outRec.setField("YD_CRN_MGNT_USE_EA", (YdUtils.fillSpZr("9", 2, 1)) );
			} 

			// YD_CRN_GRAB_TP("D") => 9     YD_CRN_GRAB_TP("E") => 10
//			if(szYD_CRN_GRAB_TP.equals("D")){
//				outRec.setField("YD_CRN_MGNT_USE_EA", (YdUtils.fillSpZr("9", 2, 1)) );
//			}else if(szYD_CRN_GRAB_TP.equals("E")){
//				outRec.setField("YD_CRN_MGNT_USE_EA", (YdUtils.fillSpZr("10", 2, 1)) );
//			}

			
			String szYD_CRN_MGNT_USE_YN1 					= "Y";
			outRec.setField("YD_CRN_MGNT_USE_YN1", 			(YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN1, 1, 1)) );
			
			String szYD_CRN_MGNT_USE_YN2 					= "Y";				
			outRec.setField("YD_CRN_MGNT_USE_YN2",			(YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN2, 1, 1)) );						

			String szYD_CRN_MGNT_USE_YN3					= "Y";				
			outRec.setField("YD_CRN_MGNT_USE_YN3",			(YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN3, 1, 1)) );						

			String szYD_CRN_MGNT_USE_YN4					= "Y";				
			outRec.setField("YD_CRN_MGNT_USE_YN4",			(YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN4, 1, 1)) );						

			String szYD_CRN_MGNT_USE_YN5					= "Y";				
			outRec.setField("YD_CRN_MGNT_USE_YN5",			(YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN5, 1, 1)) );	
			
			String szYD_CRN_MGNT_USE_YN6					= "Y";				
			outRec.setField("YD_CRN_MGNT_USE_YN6",			(YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN6, 1, 1)) );	
			
			String szYD_CRN_MGNT_USE_YN7					= "Y";		
			outRec.setField("YD_CRN_MGNT_USE_YN7",			(YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN7, 1, 1)) );	
			
			String szYD_CRN_MGNT_USE_YN8					= "Y";		
			outRec.setField("YD_CRN_MGNT_USE_YN8",			(YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN8, 1, 1)) );
			
			String szYD_CRN_MGNT_USE_YN9					= "Y";		
			outRec.setField("YD_CRN_MGNT_USE_YN9",			(YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN9, 1, 1)) );	
			
			String szYD_CRN_MGNT_USE_YN10					= "Y";		
			outRec.setField("YD_CRN_MGNT_USE_YN10",			(YdUtils.fillSpZr(szYD_CRN_MGNT_USE_YN10, 1, 1)) );				

			//==========================================================================================================
			
			String szYD_UP_WO_LOC 							= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LOC");
			outRec.setField("YD_UP_WO_LOC", 				(YdUtils.fillSpZr(szYD_UP_WO_LOC, 8, 1)) );

			String szYD_UP_WO_LAYER 						= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LAYER");
			outRec.setField("YD_UP_WO_LAYER", 				(YdUtils.fillSpZr(szYD_UP_WO_LAYER, 3, 1)) );

			String szYD_UP_WO_LOC_XAXIS 					= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LOC_XAXIS");
			outRec.setField("YD_UP_WO_LOC_XAXIS", 			(YdUtils.fillSpZr(szYD_UP_WO_LOC_XAXIS, 7, 1)) );
			
			String szYD_UP_WO_XAXIS_GAP_MAX 				= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_XAXIS_GAP_MAX");
			outRec.setField("YD_UP_WO_XAXIS_GAP_MAX", 		(YdUtils.fillSpZr(szYD_UP_WO_XAXIS_GAP_MAX, 5, 1)) );
			
			String szYD_UP_WO_XAXIS_GAP_MIN 				= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_XAXIS_GAP_MIN");
			outRec.setField("YD_UP_WO_XAXIS_GAP_MIN", 		(YdUtils.fillSpZr(szYD_UP_WO_XAXIS_GAP_MIN, 5, 1)) );
			
			String szYD_UP_WO_LOC_YAXIS 					= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LOC_YAXIS");
			outRec.setField("YD_UP_WO_LOC_YAXIS", 			(YdUtils.fillSpZr(szYD_UP_WO_LOC_YAXIS, 5, 1)) );
			
			String szYD_UP_WO_LOC_YAXIS1 					= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LOC_YAXIS1");
			outRec.setField("YD_UP_WO_LOC_YAXIS1", 			(YdUtils.fillSpZr(szYD_UP_WO_LOC_YAXIS1, 5, 1)) );
			
			String szYD_UP_WO_LOC_YAXIS2 					= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LOC_YAXIS2");
			outRec.setField("YD_UP_WO_LOC_YAXIS2", 			(YdUtils.fillSpZr(szYD_UP_WO_LOC_YAXIS2, 5, 1)) );
			
			String szYD_UP_WO_YAXIS_GAP_MAX 				= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_YAXIS_GAP_MAX");
			outRec.setField("YD_UP_WO_YAXIS_GAP_MAX", 		(YdUtils.fillSpZr(szYD_UP_WO_YAXIS_GAP_MAX, 5, 1)) );
			
			String szYD_UP_WO_YAXIS_GAP_MIN 				= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_YAXIS_GAP_MIN");
			outRec.setField("YD_UP_WO_YAXIS_GAP_MIN", 		(YdUtils.fillSpZr(szYD_UP_WO_YAXIS_GAP_MIN, 5, 1)) );
			
			String szYD_UP_WO_LOC_ZAXIS						= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LOC_ZAXIS");
			outRec.setField("YD_UP_WO_LOC_ZAXIS", 			(YdUtils.fillSpZr(szYD_UP_WO_LOC_ZAXIS, 5, 1)) );
			
			String szYD_UP_WO_ZAXIS_GAP_MAX 				= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_ZAXIS_GAP_MAX");
			outRec.setField("YD_UP_WO_ZAXIS_GAP_MAX", 		(YdUtils.fillSpZr(szYD_UP_WO_ZAXIS_GAP_MAX, 5, 1)) );
			
			String szYD_UP_WO_ZAXIS_GAP_MIN					= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_ZAXIS_GAP_MIN");
			outRec.setField("YD_UP_WO_ZAXIS_GAP_MIN", 		(YdUtils.fillSpZr(szYD_UP_WO_ZAXIS_GAP_MIN, 5, 1)) );
			
			String szYD_DN_WO_LOC 							= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC");
			outRec.setField("YD_DN_WO_LOC", 				(YdUtils.fillSpZr(szYD_DN_WO_LOC, 8, 1)) );
			
			String szYD_DN_WO_LAYER 						= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LAYER");
			outRec.setField("YD_DN_WO_LAYER", 				(YdUtils.fillSpZr(szYD_DN_WO_LAYER, 3, 1)) );
			
			String szYD_DN_WO_LOC_XAXIS 					= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC_XAXIS");
			outRec.setField("YD_DN_WO_LOC_XAXIS", 			(YdUtils.fillSpZr(szYD_DN_WO_LOC_XAXIS, 7, 1)) );
			
			String szYD_DN_WO_XAXIS_GAP_MAX 				= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_XAXIS_GAP_MAX");
			outRec.setField("YD_DN_WO_XAXIS_GAP_MAX", 		(YdUtils.fillSpZr(szYD_DN_WO_XAXIS_GAP_MAX, 5, 1)) );
			
			String szYD_DN_WO_XAXIS_GAP_MIN 				= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_XAXIS_GAP_MIN");
			outRec.setField("YD_DN_WO_XAXIS_GAP_MIN", 		(YdUtils.fillSpZr(szYD_DN_WO_XAXIS_GAP_MIN, 5, 1)) );
			
			String szYD_DN_WO_LOC_YAXIS 					= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC_YAXIS");
			outRec.setField("YD_DN_WO_LOC_YAXIS", 			(YdUtils.fillSpZr(szYD_DN_WO_LOC_YAXIS, 5, 1)) );
			
			String szYD_DN_WO_LOC_YAXIS1 					= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC_YAXIS1");
			outRec.setField("YD_DN_WO_LOC_YAXIS1", 			(YdUtils.fillSpZr(szYD_DN_WO_LOC_YAXIS1, 5, 1)) );
			
			String szYD_DN_WO_LOC_YAXIS2 					= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC_YAXIS2");
			outRec.setField("YD_DN_WO_LOC_YAXIS2", 			(YdUtils.fillSpZr(szYD_DN_WO_LOC_YAXIS2, 5, 1)) );
			
			String szYD_DN_WO_YAXIS_GAP_MAX 				= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_YAXIS_GAP_MAX");
			outRec.setField("YD_DN_WO_YAXIS_GAP_MAX", 		(YdUtils.fillSpZr(szYD_DN_WO_YAXIS_GAP_MAX, 5, 1)) );
			
			String szYD_DN_WO_YAXIS_GAP_MIN					= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_YAXIS_GAP_MIN");
			outRec.setField("YD_DN_WO_YAXIS_GAP_MIN", 		(YdUtils.fillSpZr(szYD_DN_WO_YAXIS_GAP_MIN, 5, 1)) );
			
			String szYD_DN_WO_LOC_ZAXIS 					= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC_ZAXIS");
			outRec.setField("YD_DN_WO_LOC_ZAXIS", 			(YdUtils.fillSpZr(szYD_DN_WO_LOC_ZAXIS, 5, 1)) );
			
			String szYD_DN_WO_ZAXIS_GAP_MAX 				= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_ZAXIS_GAP_MAX");
			outRec.setField("YD_DN_WO_ZAXIS_GAP_MAX", 		(YdUtils.fillSpZr(szYD_DN_WO_ZAXIS_GAP_MAX, 5, 1)) );
			
			String szYD_DN_WO_ZAXIS_GAP_MIN 				= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_ZAXIS_GAP_MIN");
			outRec.setField("YD_DN_WO_ZAXIS_GAP_MIN", 		(YdUtils.fillSpZr(szYD_DN_WO_ZAXIS_GAP_MIN, 5, 1)) );
			
		
			if(szYD_WRK_PROG_STAT.equals("1") && !szYD_UP_WO_LOC.equals("")){
				szTemp = szYD_UP_WO_LOC.substring(2, 4);
				ydUtils.putLog(szClassName, szMethodName,"\n권상지시위치.설비구분 :"+szTemp, YdConstant.DEBUG);
			}else if(szYD_WRK_PROG_STAT.equals("3") && !szYD_UP_WO_LOC.equals("")){
				szTemp = szYD_DN_WO_LOC.substring(2, 4);
				ydUtils.putLog(szClassName, szMethodName,"\n권하지시위치.설비구분 :"+szTemp, YdConstant.DEBUG);
			}
			
			//==============================================================================
			// 2009.11.20
			// 권오창
			//
			// 왜 죄다... 공백처리했을까... 잔량빼고는 한쿼리에 다 가져오게끔 했는데...
			//==============================================================================
//			if(szTemp.equals("PT") || szTemp.equals("TR")){
//				
//				ydUtils.putLog(szClassName, szMethodName,"\n야드설비ID2-권상 또는 권하위치가 차량 인 경우 :"+szTemp, YdConstant.DEBUG);
//				szYD_EQP_ID2 		= "";
//				 szYD_TC_AIM_BAY_GP = "";
//				szYD_CAR_USE_GP 	= "";
//				szCAR_NO 			= "";
//				szTRN_EQP_CD		= "";
//				szYD_EQP_WRK_SH 	= "";
//				szYD_EQP_RMN_SH 	= "";
//				
//			}else if(szTemp.equals("TC")){
//				
//				ydUtils.putLog(szClassName, szMethodName,"\n야드설비ID2- 권상 또는 권하위치가 대차 인 경우"+szTemp, YdConstant.DEBUG);
//				szYD_EQP_ID2 		="";
//				szYD_TC_AIM_BAY_GP 	= "";
//				szYD_CAR_USE_GP 	= "";
//				szCAR_NO 			= "";
//				szTRN_EQP_CD		= "";
//				szYD_EQP_WRK_SH 	= "";
//				szYD_EQP_RMN_SH 	= "";
//			}else{
//				
//				ydUtils.putLog(szClassName, szMethodName,"\n야드설비ID2-권상 또는 권하위치가 대차 및 차량인 경우 없음", YdConstant.DEBUG);
//				szYD_EQP_ID2 		= " ";
//				szYD_TC_AIM_BAY_GP	= " ";
//				szYD_CAR_USE_GP 	= " ";
//				szCAR_NO 			= " ";
//				szTRN_EQP_CD		= " ";
//				szYD_EQP_WRK_SH 	= " ";
//				szYD_EQP_RMN_SH 	= " ";
//			}
			
			
			
			
			
			// 설비ID2
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
			recPara2.setField("PI_YD",    	"T");		
			intRtnVal2 = ydWrkbookDao.getYdWrkbook(recPara2, rsResult2, 2);	
			
			/*
			 * T : 파일링가능, F : 파일링 SKIP   -- 파일링정보가 없을 경우.
             * S : 파일링가능, E : 파일링 SKIP   -- 파일링정보가 있는 경우. 
			 */
			String sYdSchStGp = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara,"YD_SCH_ST_GP"), 1, 1);
			
			if("S".equals(sYdSchStGp)||"T".equals(sYdSchStGp)){
				sYdSchStGp = "T";
			}else{
				sYdSchStGp = "F";
			}
			if(intRtnVal2 <= 0) {
				szMsg = "작업예약 테이블 조회건수 없음 [Ret : " + intRtnVal2 + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);	
				
				// 야드대차목적동[야드대차목적동] : 2012.06.15 윤재광 크레인파일링 지시 구분자로 사용 - S/T : 파일링지시 
				outRec.setField("YD_TC_AIM_BAY_GP", sYdSchStGp);
				
				// 야드차량사용구분 [야드차량사용구분] 
				outRec.setField("YD_CAR_USE_GP", YdUtils.fillSpZr(" ", 1, 1));
				
				// 차량번호 [차량번호] 
				outRec.setField("CAR_NO", YdUtils.fillSpZr_KOR(" ", 15, 1));
				
				// 운송장비코드 [운송장비코드] -> 대기차량수 [대기차량수]
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
			// GROUP{10] ------------------------------------------------------------------------------------------	
			for(int i=0; i<intRtnVal; i++){
				recPara = rsGetYdCrnsch.getRecord(i);	
				
				String szSTL_NO									= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				outRec.setField("STL_NO"+(i+1), 				YdUtils.fillSpZr(szSTL_NO, 11, 1)) ;

				String szYD_STL_WT								= ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_WT");
				outRec.setField("YD_STL_WT"+(i+1), 				YdUtils.fillSpZr(szYD_STL_WT, 5, 1)) ;

				szPoint 										= YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_T"), 7, 1);
				outRec.setField("YD_STL_T"+(i+1), 				ydUtils.FloatLRPAD(szPoint, 6, 3, '0')) ;

				szPoint 										= YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_W"), 6, 1);
				outRec.setField("YD_STL_W"+(i+1), 				ydUtils.FloatLRPAD(szPoint, 5, 1, '0')) ;

				String szYD_STL_L								= ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_L");
				outRec.setField("YD_STL_L"+(i+1), 				YdUtils.fillSpZr(szYD_STL_L, 7, 1)) ;
			}
			for(int j=intRtnVal; j<10; j++){
				
				String szSTL_NO									= " ";
				outRec.setField("STL_NO"+(j+1), 				YdUtils.fillSpZr(szSTL_NO, 11, 1)) ;
				
				String szYD_STL_WT								= " ";
				outRec.setField("YD_STL_WT"+(j+1), 				YdUtils.fillSpZr(szYD_STL_WT, 5, 1)) ;
				
				String szYD_STL_T								= " ";
				outRec.setField("YD_STL_T"+(j+1), 				YdUtils.fillSpZr(szYD_STL_T, 6, 1)) ;
				
				String szYD_STL_W								= " ";
				outRec.setField("YD_STL_W"+(j+1), 				YdUtils.fillSpZr(szYD_STL_W, 5, 1)) ;
				
				String szYD_STL_L								= " ";
				outRec.setField("YD_STL_L"+(j+1), 				YdUtils.fillSpZr(szYD_STL_L, 7, 1)) ;
			}
			//*	GROUP{10] -----------------------------------------------------------------------------------------END	
					
			recPara = rsGetYdCrnsch.getRecord(0);
			
			String szYD_SCH_CD_NEXT = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD_NEXT");
			outRec.setField("YD_SCH_CD_NEXT", (YdUtils.fillSpZr(szYD_SCH_CD_NEXT, 8, 1)) );

			if(!szYD_SCH_CD_NEXT.trim().equals("")){
				
				if("Y".equals(szUsageYn)) {
					//-- 통합 크레인 스케줄
					recPara2 = JDTORecordFactory.getInstance().create();
					rsYdSchRule = JDTORecordFactory.getInstance().createRecordSet("");
					
					recPara2.setField("YD_SCH_CD", szYD_SCH_CD_NEXT);
					
					intRtnVal2 = commDao.select(recPara2, rsYdSchRule, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0098");	
					
					if(intRtnVal2 <= 0) {
						szMsg ="스케쥴 기준 조회건수 없음 : [" +intRtnVal2+"]";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
						
						szYD_SCH_NAME_NEXT = YdUtils.fillSpZr_KOR(" ", 30, 1);
						
					} else {
					
						//레코드 추출
						rsYdSchRule.first();
						recPara3 = rsYdSchRule.getRecord();
						
						szYD_SCH_NAME_NEXT = YdUtils.fillSpZr_KOR(ydDaoUtils.paraRecChkNull(recPara3, "YD_SCH_CONTENTS"), 30, 1);
					}
					
					
				} else {
				
					recPara2 = JDTORecordFactory.getInstance().create();			
					rsYdSchRule = JDTORecordFactory.getInstance().createRecordSet("");			
					recPara2.setField("YD_SCH_CD", szYD_SCH_CD_NEXT);
					intRtnVal2 = ydSchRuleDao.getYdSchrule(recPara2, rsYdSchRule, 0);
					if(intRtnVal2 < 0) {
						szMsg = "스케쥴 기준 테이블 조회오류 (" + szYD_SCH_CD_NEXT +")" + "[Ret : " + intRtnVal2 + "]";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
		
						szYD_SCH_NAME_NEXT = YdUtils.fillSpZr_KOR(" ", 30, 1);	
					} else if(intRtnVal2 == 0) {
						szMsg = "스케쥴 기준 테이블 조회건수 없음 (" + szYD_SCH_CD_NEXT +")" + "[Ret : " + intRtnVal2 + "]";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
						
						szYD_SCH_NAME_NEXT = YdUtils.fillSpZr_KOR(" ", 30, 1);	
					}else {		
						rsYdSchRule.first();
						recPara3 = rsYdSchRule.getRecord();				
		
						szYD_SCH_NAME_NEXT = YdUtils.fillSpZr_KOR(ydDaoUtils.paraRecChkNull(recPara3, "YD_SCH_CD_NEXT"), 30, 1);
					}
				}
			} else {
				szMsg = "스케쥴 코드_NEXT가 없어서 기준테이블을 조회 안함 : YD_SCH_NAME_NEXT는 공백";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);							
				
				szYD_SCH_NAME_NEXT = YdUtils.fillSpZr_KOR(" ", 30, 1);					
			}
			
			outRec.setField("YD_SCH_NAME_NEXT", 			szYD_SCH_NAME_NEXT);
			
			String szYD_UP_WO_LOC_NEXT						= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LOC_NEXT");
			outRec.setField("YD_UP_WO_LOC_NEXT", 			(YdUtils.fillSpZr(szYD_UP_WO_LOC_NEXT, 8, 1)) );
			
			String szYD_UP_WO_LAYER_NEXT					= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WO_LAYER_NEXT");
			outRec.setField("YD_UP_WO_LAYER_NEXT", 			(YdUtils.fillSpZr(szYD_UP_WO_LAYER_NEXT, 3, 1)) );
			
			String szYD_DN_WO_LOC_NEXT						= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC_NEXT");
			outRec.setField("YD_DN_WO_LOC_NEXT", 			(YdUtils.fillSpZr(szYD_DN_WO_LOC_NEXT, 8, 1)) );
			
			String szYD_DN_WO_LAYER_NEXT					= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LAYER_NEXT");
			outRec.setField("YD_DN_WO_LAYER_NEXT", 			(YdUtils.fillSpZr(szYD_DN_WO_LAYER_NEXT, 3, 1)) );
			
			String szSTL_NO_NEXT							= ydDaoUtils.paraRecChkNull(recPara, "STL_NO_NEXT");
			outRec.setField("STL_NO_NEXT", 					(YdUtils.fillSpZr(szSTL_NO_NEXT, 11, 1)) );
			
			String szYD_CRN_WRK_SH_NEXT						= ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_WRK_SH_NEXT");
			outRec.setField("YD_CRN_WRK_SH_NEXT", 			(YdUtils.fillSpZr(szYD_CRN_WRK_SH_NEXT, 2, 1)) );
			
			String szYD_CRN_WRK_WT_NEXT						= ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_WRK_WT_NEXT");
			outRec.setField("YD_CRN_WRK_WT_NEXT", 			(YdUtils.fillSpZr(szYD_CRN_WRK_WT_NEXT, 7, 1)) );
			
			szPoint 										= YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_WRK_T_NEXT"), 8, 1);
			outRec.setField("YD_CRN_WRK_T_NEXT", 			(ydUtils.FloatLRPAD(szPoint, 7, 3, '0')) );
			
			szPoint 										= YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_WRK_MAX_W_NEXT"), 6, 1);
			outRec.setField("YD_CRN_WRK_MAX_W_NEXT", 		(ydUtils.FloatLRPAD(szPoint, 5, 1, '0')) );
			
			String szYD_CRN_WRK_MAX_L_NEXT					= ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_WRK_MAX_L_NEXT");
			outRec.setField("YD_CRN_WRK_MAX_L_NEXT", 		(YdUtils.fillSpZr(szYD_CRN_WRK_MAX_L_NEXT, 7, 1)) );
			
			//RecordSet으로 반환
			outRecSet.addRecord(outRec);

			
			ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 3]:: 송신 TC내용 ==============\n", YdConstant.DEBUG);
			ydUtils.displayRecord(szOperationName, outRec);

		}catch(Exception e){
			
			szMsg = "YDY4L004[크레인작업지시] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}

		return outRecSet.size();

	} // end of makeY4L004()
	
	
	
	
	

	/**
	 * YDY4L005 : 크레인작업실적응답
	 * @param  JDTORecord inRec :: [YD_GP / YD_SCH_CD / YD_CRN_SCH_ID / YD_L2_WR_GP / YD_L3_HD_RS_CD]  
	 * @return JDTORecordSet outRecSet 
	 *
	 */
	public static int makeY4L005(JDTORecord inRec, JDTORecordSet outRecSet){

		YdUtils ydUtils 			= new YdUtils();
		YdDaoUtils ydDaoUtils 		= new YdDaoUtils();

		JDTORecord outRec 			= null;

		// 변수선언
		String szMethodName 		= "makeY4L005";
		String szMsg 				= "";
		String szOperationName      = "A후판제품L2 크레인작업실적응답";

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

			ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 1]::makeY4L005==============\n", YdConstant.DEBUG);	

			ydUtils.displayRecord(szOperationName, inRec);
			
			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 업무 : 크레인작업실적응답 전문 편집(YDY1L005)
	         * 		U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하에 따른 전문편집
	         *     (U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하)
	         * 수정자 : 임춘수
	         * 일자 : 2009.06.17
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			szYD_L2_WR_GP = ydDaoUtils.paraRecChkNull(inRec, "YD_L2_WR_GP");				
			if(  szYD_L2_WR_GP.equals(YdConstant.CRN_WRK_RE_LD_WR)) {					//U:권상실적
				szTemp = "권상실적";
			}else if(  szYD_L2_WR_GP.equals(YdConstant.CRN_WRK_RE_DN_WR)) {				//D:권하실적
				szTemp = "권하실적";
			}else if(  szYD_L2_WR_GP.equals(YdConstant.CRN_WRK_RE_EMG_PTOP)) {			//E:비상조업실적
				szTemp = "비상조업실적";
			}else if(  szYD_L2_WR_GP.equals(YdConstant.CRN_WRK_RE_TRBL)) {				//R:고장
				szTemp = "고장";
				//szTemp = "설비고장복구실적";
			}else if(  szYD_L2_WR_GP.equals(YdConstant.CRN_WRK_RE_MD_MOD)) {			//M:모드변경
				szTemp = "모드변경";
				//szTemp = "설비운전모드전환";
			}else if(  szYD_L2_WR_GP.equals(YdConstant.CRN_WRK_RE_WO_DMD)) {			//J:크레인작업지시요구
				szTemp = "지시요구";
			}else if(  szYD_L2_WR_GP.equals(YdConstant.CRN_WRK_RE_FRCE_DN)) {			//F:강제권하
				szTemp = "강제권하";
			}else{
				szMsg = "[크레인작업실적응답]야드L2실적구분 (" + szYD_L2_WR_GP + ")가 정의된 값이 아닙니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);	
				return -1;
			}
			
			szMsg = "[크레인작업실적응답]야드L2실적구분이 " + szTemp + " [" + szYD_L2_WR_GP + "] 입니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);

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
			if( szYD_L3_HD_RS_CD.equals(YdConstant.CRN_WRK_RE_CD_NORMAL_HD) || szYD_L3_HD_RS_CD.equals(YdConstant.CRN_WRK_RE_CD_NO_WRK) ){
				szTemp += "이 정상 처리 되었습니다.";
			}else {
				szTemp += "이 Error처리 되었습니다.";
			}

			outRec = JDTORecordFactory.getInstance().create();

			//		1.	전문 ID				MSG_ID				VARCHAR2(8)		YDY4L005
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
			outRec.setField("MSG_ID", 				new String("YDY4L005"));
			outRec.setField("DATE", 				new String(YdUtils.getCurDate("yyyy-MM-dd")));
			outRec.setField("TIME", 				new String(YdUtils.getCurDate("HH-mm-ss")));
			outRec.setField("MSG_GP", 				new String("I"));
			outRec.setField("MSG_LEN", 				new String(YdUtils.fillSpZr(""+nTcLen, 4, 0)));
			outRec.setField("TEMP", 				new String(YdUtils.fillSpZr("", 29, 1)));
			outRec.setField("YD_EQP_ID", 			YdUtils.fillSpZr(szYD_EQP_ID, 6, 1));
			outRec.setField("YD_WRK_PROG_STAT", 	YdUtils.fillSpZr(szYD_WRK_PROG_STAT, 1, 1));
			outRec.setField("YD_SCH_CD", 			YdUtils.fillSpZr(szYD_SCH_CD, 8, 1));
			outRec.setField("YD_CRN_SCH_ID", 		YdUtils.fillSpZr(szYD_CRN_SCH_ID, 18, 1));
			outRec.setField("YD_L2_WR_GP", 			YdUtils.fillSpZr(szYD_L2_WR_GP, 1, 1));
			outRec.setField("YD_L3_HD_RS_CD", 		YdUtils.fillSpZr(szYD_L3_HD_RS_CD, 4, 1));
			outRec.setField("YD_L3_MSG", 			YdUtils.fillSpZr_KOR(szTemp, 40, 1));

			// RecordSet으로 반환
			outRecSet.addRecord(outRec);
			
			ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 3]:: 송신 TC내용 ==============\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);

		}catch(Exception e){
			szMsg = "YDY4L005[크레인작업실적응답] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}

		return outRecSet.size();

	} // end of makeY4L005()

	
  //---------------------------------------------------------------------------
} // end of class MakeTcY4
