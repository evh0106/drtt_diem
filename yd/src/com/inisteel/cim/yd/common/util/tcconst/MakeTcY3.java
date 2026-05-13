package com.inisteel.cim.yd.common.util.tcconst;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;





/**
 * Y3 (A후판슬라브야드L2) 송신 용 전문 생성
 * @author YHWHman
 *
 */
public class MakeTcY3 {
	
	// YDY3L001	저장위치제원	
	// YDY3L002	저장품제원	
	// YDY3L003	크레인작업계획 	
	// YDY3L004	크레인작업지시	
	// YDY3L005	크레인작업실적응답	
	
	
	// 클래스명
	private static final String szClassName  = MakeTcY3.class.getName();
	
	
	/**
	 * YDY3L001 : 저장위치제원
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeY3L001(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDY3L001
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
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
		//		18.	야드적치BedX축			YD_STK_BED_XAXIS		NUMBER(7)		Center 지점
		//		19.	야드적치BedY축			YD_STK_BED_YAXIS		NUMBER(5)		Center 지점
		//		20.	야드적치BedZ축			YD_STK_BED_ZAXIS		NUMBER(5)		최하단 바닥 높이
		//		21.	야드적치Bed단Max			YD_STK_BED_LYR_MAX		NUMBER(3)			
		//		22.	야드적치Bed중량Max			YD_STK_BED_WT_MAX		NUMBER(7)			
		//		23.	야드적치Bed높이Max			YD_STK_BED_H_MAX		NUMBER(5)			
		//		24.	야드적치Bed길이Max			YD_STK_BED_L_MAX		NUMBER(5)			
		//		25.	야드적치Bed폭Max			YD_STK_BED_W_MAX		NUMBER(5,1)		9999.9(소수점 없는 유효 Data)
		//		26.	야드차량착발상태			YD_CAR_ARRSTRT_STAT		VARCHAR2(1)		"A": 도착,        "S": 출발
		//		27.	야드차량사용구분			YD_CAR_USE_GP			VARCHAR2(1)		"L" :구내운송차량,  "G": 제품출하차량
		//		28.	야드설비작업상태			YD_EQP_WRK_STAT			VARCHAR2(1)		"L" : 공차(출하),  "U" : 영차(반입)
		//		29.	차량번호					CAR_NO					VARCHAR2(15)	제품출하차량
		//		30.	운송장비코드				TRN_EQP_CD				VARCHAR2(8)		구내운송차량
		//		31.	카드번호					CARD_NO					VARCHAR2(4)		제품출하차량
		//		32.	야드차량목표야드구분		YD_CAR_AIM_YD_GP		VARCHAR2(1)		차량하차 야드
		
		// 레코드 선언
		JDTORecord recPara 			 = null;
		JDTORecord recParaCarSch 	 = null;
		JDTORecordSet rsResult       = null;
		JDTORecordSet rsResultCarSch = null;
		JDTORecord recGetVal         = null;
		JDTORecord recGetValCarSch   = null;
		JDTORecord outRec            = null;			

		// DAO객체 생성
		YdCarSchDao ydCarSchDao 	 = new YdCarSchDao();
		YdStkBedDao ydStkBedDao      = new YdStkBedDao();		
		YdDaoUtils ydDaoUtils        = new YdDaoUtils();	
		YdUtils ydUtils              = new YdUtils();

		// 변수선언
		String szMethodName          = "makeY3L001";
		String szMsg        	     = "";
		String szOperationName       = "A후판슬라브L2 저장위치제원";
		String szTemp        	     = "";
		String szConv                = "";
		
		String szYD_CAR_ARRSTRT_STAT = "";
		String szYD_EQP_WRK_STAT     = "";
		String szYD_CAR_AIM_YD_GP    = "";
		String szYD_INFO_SYNC_CD     = "";
		String szYD_STK_COL_GP       = "";
		String szYD_STK_BED_NO       = "";
		String szYD_CAR_PROG_STAT    = "";
		String szYD_CAR_USE_GP       = "";
		String szTRN_EQP_CD          = "";
		String szCAR_NO              = "";
		String szCARD_NO             = "";
		
		// 리턴값
		int intRtnVal                = 0;
		int intRtnValCarSch          = 0;
		
		// TC Length = 147 (60 + 87)
		int nTcLen                   = 87;
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeY3L001() IN==========================\n", YdConstant.DEBUG);	
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
			if(intRtnVal < 0) {
				szMsg = "적치BED 테이블 조회오류 YD_STK_COL_GP(" + szYD_STK_COL_GP + ") YD_STK_BED_NO(" + szYD_STK_BED_NO + ") " + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			} else if(intRtnVal == 0) {
				szMsg = "적치BED 테이블 조회건수 없음 YD_STK_COL_GP(" + szYD_STK_COL_GP + ") YD_STK_BED_NO(" + szYD_STK_BED_NO + ") " + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;
			}
			
			for(int nIdx=0; nIdx<intRtnVal; nIdx++) {
				recGetVal = rsResult.getRecord(nIdx);	
	
				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();								
				outRec.setField("MSG_ID" , "YDY3L001");
				outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
				outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
				outRec.setField("MSG_GP" , "I");
				outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
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
				
//				//=======================================================================================================================
//				// 차량스케쥴 조회
//				//=======================================================================================================================
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
				ydUtils.putLog(szClassName, szMethodName, "\n======================makeY3L001() OUT("+ Integer.toString(nIdx) + ")==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);	
			}									
		}catch(Exception e){
			szMsg = "Y3(A후판슬라브야드L2) 송신  저장위치제원  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}
		
		return outRecSet.size();
	} // end of makeY3L001()
	
	
	
	
	
	/**
	 * YDY3L002 : 저장품제원
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeY3L002(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDY3L002
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER  (4)		
		//		6.	임시						TEMP					VARCHAR2(29)	

		//		7.	야드정보동기화코드			YD_INFO_SYNC_CD			VARCHAR2(1)		1:동,2:SPAN,3:열,4:BED,5:지정저장품,P:후판가열로보급
		//		8.	야드재료정보송신매수		YD_STL_INFO_SND_SH		NUMBER  (3)
		//		9.	야드재료정보송신순번		YD_STL_INFO_SND_CNT		NUMBER  (3)
		//		10.	재료외형구분				STL_APPEAR_GP			VARCHAR2(1)			
		//		11.	재료번호					STL_NO					VARCHAR2(11)		
		//		12.	야드저장위치				YD_STR_LOC				VARCHAR2(8)		야드적치Bed까지 표현
		//		13.	야드적치단번호				YD_STK_LYR_NO			VARCHAR2(3)	
		//		14.	야드재료중량				YD_STL_WT				NUMBER  (5)	
		//		15.	야드재료두께				YD_STL_T				NUMBER  (6,3)
		//		16.	야드재료폭				YD_STL_W				NUMBER  (5,1)	
		//		17.	야드재료길이				YD_STL_L				NUMBER  (7)	
		//      18. 재료외경                                   MAT_ODIA                NUMBER  (5)
		//      19. 재료내경                                   MAT_IDIA                NUMBER  (5,1)
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
		//		30.	설계HCR구분				ORD_HCR_GP				VARCHAR2(1)	
		//		31.	HCR구분					HCR_GP					VARCHAR2(1)	
		//      32. 연주Machine코드                     CC_MC_CD                VARCHAR2(1)
		//      33. SCARFING여부                          SCARFING_YN             VARCHAR2(1)
		//      34. SCARFING완료유무                   SCARFING_DONE_YN        VARCHAR2(1)
        //      35. 주편손질방법                             RPR_MTD                 VARCHAR2(1)
		//      36. SCARFING깊이                          SCARFING_DEPTH          VARCHAR2(2)
		//		37.	재열재구분				REHEAT_SLAB_GP			VARCHAR2(1)	
		//		38.	조업공장구분				PTOP_PLNT_GP			VARCHAR2(2)		조업공장구분의 두번째 자리
		//		39.	가열로장입Lot번호			REFUR_CHG_LOT_NO		VARCHAR2(10)	압연지시에서 저장품Table에 등록
		//		40.	생산통제Lot스케줄일련번호	CT_LOT_SCH_SERNO		VARCHAR2(22)	압연지시에서 저장품Table에 등록
		//		41.	이송지시일자				FRTOMOVE_ORD_DATE		VARCHAR2(8)	
		//		42.	이송공장구분				FRTOMOVE_PLANT_GP		VARCHAR2(2)		임가공인 경우 임가공사 코드
		//		43.	긴급이송작업지시구분		URGENT_FRTOMOVE_WORD_GP	VARCHAR2(1)	
		//      44. HYSCO운송구분                         HYSCO_TRANS_CLS         VARCHAR2(1)
		//		45.	외관종합판정등급			APPEAR_GRADE			VARCHAR2(1)	
		//      46. 권취코일냉각방법                      COOL_METHOD             VARCHAR2(1)
		//		47.	냉각완료구분				COOL_DONE_GP			VARCHAR2(1)
		//      48. 야드Conveyor분기코드            CONV_BRANCH_CD          VARCHAR2(2)
		//		49.	고객코드					CUST_CD					VARCHAR2(6)	
		//		50.	목적지코드				DEST_CD					VARCHAR2(5)	
		//		51.	납기기준일				DLVRDD_RULE_DD			VARCHAR2(8)	
		//		52.	품명코드					ITEMNAME_CD				VARCHAR2(3)	
		//		53.	종합판정등급				OVERALL_STAMP_GRADE   	VARCHAR2(1)	
		//		54.	수주구분					ORD_GP					VARCHAR2(1)	
		//		55.	야드산적LotType			YD_STK_LOT_TP			VARCHAR2(2)	
		//		56.	야드산적Lot코드			YD_STK_LOT_CD			VARCHAR2(18)	
		//      57. 날판번호                                   PL_MPL_NO               VARCHAR2(10)
		
		// 레코드 선언
		JDTORecord recPara       	  = null;
		JDTORecordSet rsResult        = null;
		JDTORecordSet rsMResult       = null;
		JDTORecordSet rsResultLot     = null;
		JDTORecordSet rsResultPLMPLNo = null;
		JDTORecord recGetVal          = null;
		JDTORecord recGetVal2         = null;
		JDTORecord recGetVal3         = null;
		JDTORecord outRec             = null;		
		JDTORecord recGetValPLMPLNo   = null;
		
		
		// DAO객체 생성
		YdStockDao ydStockDao   = new YdStockDao();
		YdDaoUtils ydDaoUtils   = new YdDaoUtils();	
		YdUtils ydUtils         = new YdUtils();

		
		// 변수선언
		String szMethodName     = "makeY3L002";
		String szMsg            = "";
		String szOperationName  = "A후판슬라브L2 저장품제원";		

//		String szYdMtlItem      = "";
		String szConv           = "";
		
	
		String szSTLKIND_CD     = "";
		String szSPEC_ABBSYM    = "";
		String szMILL_PLNT_GP   = "";
		String szSCARFING_DEPTH = "";
		String szYD_INFO_SYNC_CD = "";
		String szDEL_YN_CHECK = "";

		// 리턴값
		int intRtnVal           = 0;
		int nRet                = 0;
		
		String szSTLNO          = "";
		String szRtnMsg         = "";
		
		
		// TC Length = 319 (60 + 259)
		int nTcLen              = 259;
		
		try{	
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeY3L002() IN==========================\n", YdConstant.DEBUG);	
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
			} else if(szYD_INFO_SYNC_CD.equals("5") || szYD_INFO_SYNC_CD.equals("A") || szYD_INFO_SYNC_CD.equals("B") || szYD_INFO_SYNC_CD.equals("C") || szYD_INFO_SYNC_CD.equals("P")){
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
	
				outRec = JDTORecordFactory.getInstance().create();									
				outRec.setField("MSG_ID" , "YDY3L002");
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
				outRec.setField("STL_APPEAR_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP"), 1, 1));	
				
				// 재료번호 [재료번호]
				outRec.setField("STL_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO"), 11, 1));	
				
//				szYdMtlItem = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_MTL_ITEM"), 2, 1).trim();

				// 야드저장위치 [야드저장위치 : BED까지]  
				outRec.setField("YD_STR_LOC", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_NO"), 8, 1));	
			
				// 야드적치단번호 [야드적치단번호]
				outRec.setField("YD_STK_LYR_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_LYR_NO"), 3, 1));	
				
				// 야드재료중량 [야드재료중량]
				outRec.setField("YD_STL_WT", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STL_WT"), 5, 1));	
				
				// 야드재료두께 [야드재료두께]
				szConv = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STL_T"), 7, 1);
				outRec.setField("YD_STL_T", ydUtils.FloatLRPAD(szConv, 6, 3, '0'));	
				
				// 야드재료폭 [야드재료폭]
				szConv = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STL_W"), 6, 1);
				outRec.setField("YD_STL_W", ydUtils.FloatLRPAD(szConv, 5, 1, '0'));	
					
				// 야드재료길이 [야드재료길이]
				outRec.setField("YD_STL_L", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STL_L"), 7, 1));	
				
				// 재료외경
				outRec.setField("MAT_ODIA", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "MAT_ODIA"), 5, 1));	
				
				// 재료내경
				szConv = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "MAT_IDIA"), 6, 1);
				outRec.setField("MAT_IDIA", ydUtils.FloatLRPAD(szConv, 5, 1, '0'));	
										
				// 강종 [강종]
				szSTLKIND_CD   = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "STLKIND_CD"), 3, 1);
				outRec.setField("STLKIND_CD", YdUtils.fillSpZr(szSTLKIND_CD, 3, 1));

				// 규격약호 [규격약호]
				szSPEC_ABBSYM  = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "SPEC_ABBSYM"), 15, 1);
				outRec.setField("SPEC_ABBSYM", YdUtils.fillSpZr(szSPEC_ABBSYM, 15, 1));	

				// 야드입고일자 [등록일자]  
				outRec.setField("YD_IPGO_DD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "REG_DDTT"), 14, 1));

				// 공장공정코드 [공장공정코드]
				outRec.setField("PLNT_PROC_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "PLNT_PROC_CD"), 3, 1));

				// 현재진도코드 [재료진도코드]
				outRec.setField("CURR_PROG_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "CURR_PROG_CD"), 1, 1));

				// 주문여재구분 [주문여재구분]
				outRec.setField("ORD_YEOJAE_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "ORD_YEOJAE_GP"), 1, 1));

				// 주문번호 [주문번호]
				outRec.setField("ORD_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "ORD_NO"), 10, 1));

				// 주문행번 [주문행번]
				outRec.setField("ORD_DTL", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "ORD_DTL"), 3, 1));

				// 구입SLAB번호 [구입SLAB번호]
				outRec.setField("BUY_SLAB_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "BUY_SLAB_NO"), 30, 1));

				// SLAB지시행선코드 [SLAB지시행선코드]
				outRec.setField("SLAB_WO_RT_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "SLAB_WO_RT_CD"), 2, 1));

				// 설계HCR구분 [설계HCR구분]
				outRec.setField("ORD_HCR_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "ORD_HCR_GP"), 1, 1));

				// HCR구분 [HCR구분]
				outRec.setField("HCR_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "HCR_GP"), 1, 1));

				// 연주Machine코드 [야드CCM구분]
				outRec.setField("CC_MC_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "CC_MC_CD"), 1, 1));

				// SCARFING여부 [SCARFING여부]
				outRec.setField("SCARFING_YN", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "SCARFING_YN"), 1, 1));

				// SCARFING완료유무 [SCARFING완료유무]
				outRec.setField("SCARFING_DONE_YN", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "SCARFING_DONE_YN"), 1, 1));

				// 주편손질방법
				outRec.setField("RPR_MTD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "RPR_MTD"), 1, 1));
					
				szSTLNO = ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO");
				rsMResult = JDTORecordFactory.getInstance().createRecordSet("");
	        	szRtnMsg = YdCommonUtils.getPtCommStock(szSTLNO, rsMResult);
	        	if(!szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	        		szMsg = "주편/슬라브공통테이블에서 재료[" + szSTLNO + "] 조회 시 오류발생 : " + szRtnMsg;
	                ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
	                continue ;
	        	}			
        	
				rsMResult.first();
				recGetVal2 = rsMResult.getRecord();
				szSCARFING_DEPTH = ydDaoUtils.paraRecChkNull(recGetVal2, "SCARFING_DEPTH");					
				
				// SCARFING깊이 [SCARFING깊이]
				if(!szSCARFING_DEPTH.equals("")){
					outRec.setField("SCARFING_DEPTH", YdUtils.fillSpZr(szSCARFING_DEPTH, 2, 1));					
				} else {
					outRec.setField("SCARFING_DEPTH", YdUtils.fillSpZr(" ", 2, 1));					
				}

				szMsg = "STL_NO(" + szSTLNO + "로 주편/슬라브 공통 테이블에서 읽은 SCARFING_DEPTH(" + szSCARFING_DEPTH + ")";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);				

				szMsg = "======================= 재열재 구분 시작 =======================";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				// 재열재구분 [재열재구분]
				outRec.setField("REHEAT_SLAB_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "REHEAT_SLAB_GP"), 1, 1));

				szMsg = "======================= 조업공장구분 시작 =======================";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				// 압연공장구분 [조업공장구분]
				szMILL_PLNT_GP = ydDaoUtils.paraRecChkNull(recGetVal, "PTOP_PLNT_GP");				
				outRec.setField("PTOP_PLNT_GP", YdUtils.fillSpZr(szMILL_PLNT_GP, 2, 1));  
				

				szMsg = "======================= 가열로 장입 LOT번호 조회 시작 =======================";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				//=================================================================================
				// 가열로 장입  LOT번호 추출 (GP : 161)
				//    com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSLABCOMM_MillPlntGpBySTLNo
				//    
				//     파라미터 : V_PTOP_PLNT_GP : "PA"
				//              V_STL_NO       : 재료번호
				//=================================================================================
				rsResultLot = JDTORecordFactory.getInstance().createRecordSet("");
				recPara = JDTORecordFactory.getInstance().create();			
				recPara.setField("PTOP_PLNT_GP", "PA");
				recPara.setField("STL_NO"      , szSTLNO);
				nRet = ydStockDao.getYdStock(recPara, rsResultLot, 161);
				if(nRet < 0) {
					szMsg = "가열로 장입 LOT번호 조회 오류 - PTOP_PLNT_GP(PA) STL_NO(" + szSTLNO + ")";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
					return 0;
				} else if(nRet == 0) {
					szMsg = "가열로 장입 LOT번호 조회 건수가 없음 - PTOP_PLNT_GP(PA) STL_NO(" + szSTLNO + ")";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			

					// 가열로장입Lot번호 [가열로장입Lot번호]
					outRec.setField("REFUR_CHG_LOT_NO", YdUtils.fillSpZr(" ", 10, 1));

					// 생산통제Lot스케줄일련번호 [생산통제Lot스케줄일련번호]
					outRec.setField("CT_LOT_SCH_SERNO", YdUtils.fillSpZr(" ", 22, 1));
				} else {
					rsResultLot.first();
					recGetVal3 = rsResultLot.getRecord();
						
					// 가열로장입Lot번호 [가열로장입Lot번호]
					outRec.setField("REFUR_CHG_LOT_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal3, "REFUR_CHG_LOT_NO"), 10, 1));

					// 생산통제Lot스케줄일련번호 [생산통제Lot스케줄일련번호]
					outRec.setField("CT_LOT_SCH_SERNO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal3, "REFUR_CHG_PLN_SERNO"), 22, 1));
				}


				// 이송지시일자 [이송지시일자]
				outRec.setField("FRTOMOVE_ORD_DATE", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "FRTOMOVE_ORD_DATE"), 8, 1));

				// 이송공장구분 [이송공장구분]
				outRec.setField("FRTOMOVE_PLANT_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "FRTOMOVE_PLANT_GP"), 2, 1));

				// 긴급이송작업지시구분 [긴급이송작업지시구분]
				outRec.setField("URGENT_FRTOMOVE_WORD_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "URGENT_FRTOMOVE_WORD_GP"), 1, 1));

				// HYSCO운송구분 [HYSCO운송구분]
				outRec.setField("HYSCO_TRANS_CLS", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "HYSCO_TRANS_CLS"), 1, 1));

				// 외관종합판정등급 [외관종합판정등급]
				outRec.setField("APPEAR_GRADE", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "APPEAR_GRADE"), 1, 1));

				// 권취코일냉각방법 [권취코일냉각방법]
				outRec.setField("COOL_METHOD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "COOL_METHOD"), 1, 1));

				// 냉각완료구분 [냉각완료구분]
				outRec.setField("COOL_DONE_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "COOL_DONE_GP"), 1, 1));

				// 야드Conveyor분기코드
				outRec.setField("CONV_BRANCH_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "CONV_BRANCH_CD"), 2, 1));				
				
				// 고객코드 [고객코드]
				outRec.setField("CUST_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "CUST_CD"), 6, 1));

				// 목적지코드 [목적지코드]
				outRec.setField("DEST_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "DEST_CD"), 5, 1));

				// 납기기준일 [납기기준일]
				outRec.setField("DLVRDD_RULE_DD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "DLVRDD_RULE_DD"), 8, 1));

				// 품명코드 [품명코드]
				outRec.setField("ITEMNAME_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "ITEMNAME_CD"), 3, 1));

				// 종합판정등급 [종합판정등급]
				outRec.setField("OVERALL_STAMP_GRADE", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "OVERALL_STAMP_GRADE"), 1, 1));

				// 수주구분 [수주구분]
				outRec.setField("ORD_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "ORD_GP"), 1, 1));

				// 야드산적LotType [야드산적LotType] 
				outRec.setField("YD_STK_LOT_TP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_LOT_TP"), 2, 1));

				// 야드산적Lot코드 [야드산적Lot코드]  
				outRec.setField("YD_STK_LOT_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_LOT_CD"), 18, 1));
				
				
				//=================================================================================
				// 날판번호 추출 (GP : 171)
				//    com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSizeSLABCOMMPlPmpNo
				//    
				//     파라미터 : V_SLAB_NO : 재료번호
				//=================================================================================
				rsResultPLMPLNo = JDTORecordFactory.getInstance().createRecordSet("");
				recPara = JDTORecordFactory.getInstance().create();			
				recPara.setField("SLAB_NO", szSTLNO);
				nRet = ydStockDao.getYdStock(recPara, rsResultPLMPLNo, 171);
				if(nRet < 0) {
					szMsg = "날판번호 조회 오류 - SLAB_NO(" + szSTLNO + ")";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
					return 0;
				} else if(nRet == 0) {
					szMsg = "날판번호 조회 건수가 없음 - SLAB_NO(" + szSTLNO + ")";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			

					outRec.setField("PL_MPL_NO", YdUtils.fillSpZr(" ", 10, 1));

				} else {
					rsResultPLMPLNo.first();
					recGetValPLMPLNo = rsResultPLMPLNo.getRecord();
						
					// 날판번호
					outRec.setField("PL_MPL_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetValPLMPLNo, "PL_MPL_NO"), 10, 1));
				}
					
				
				// RecordSet에 추가	
				outRecSet.addRecord(outRec);				
				
				// Debug MSG
				ydUtils.putLog(szClassName, szMethodName, "\n======================makeY3L002() OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);	
			}
		}catch(Exception e){
			szMsg = "Y3(A후판슬라브야드L2) 송신  저장품제원  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}

		return outRecSet.size();
	} // end of makeY3L002()
	
	
	
	
	
	/**
	 * YDY3L003 : 크레인작업계획
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeY3L003(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID				VARCHAR2(8)		YDY3L003
		//		2.	생성일					DATE				VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME				VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP				VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN				NUMBER(4)		
		//		6.	임시						TEMP				VARCHAR2(29)		

		//		7.	야드정보동기화코드			YD_INFO_SYNC_CD		VARCHAR2(1)		
		//		8.	야드재료정보송신매수		YD_STL_INFO_SND_SH	NUMBER(3)		
		//		9.	야드재료정보송신순번		YD_STL_INFO_SND_CNT	NUMBER(3)		
		//		10.	재료번호					STL_NO				VARCHAR2(11)	
		//		11.	조업공장구분				PTOP_PLNT_GP		VARCHAR2(2)     2자리로 바뀌었음
		//		12.	가열로장입Lot번호			REFUR_CHG_LOT_NO	VARCHAR2(10)
		//		13.	생산통제Lot스케줄일련번호	CT_LOT_SCH_SERNO	NUMBER(22)
		//      14. 날판번호                                   PL_MPL_NO           VARCHAR2(10) 
		
		// 레코드 선언
		JDTORecord recPara            = null;
		JDTORecordSet rsResult        = null;
		JDTORecordSet rsResultPLMPLNo = null;
		JDTORecord recGetVal          = null;
		JDTORecord recGetValPLMPLNo   = null;
		JDTORecord outRec 	          = null;	
		
		// DAO객체 생성
		YdDaoUtils ydDaoUtils  = new YdDaoUtils();	
		YdUtils ydUtils		   = new YdUtils();
		YdStockDao ydStockDao  = new YdStockDao();

		// 변수선언
		String szMethodName    = "makeY3L003";
		String szMsg           = "";
		String szOperationName = "A후판슬라브L2 크레인작업계획";
		
		// 야드구분
		String szYdGp          = "";
		
		//야드정보동기화코드
		String szYD_INFO_SYNC_CD = null;
		//쿼리구분자
		int intGp = 0;
		
		// 압연공장구분
		String szPtopPlntGp    = "";

		// 재료번호
		String szSTL_NO        = "";
		
		// 리턴값
		int intRtnVal          = 0;
		
		int nRet               = 0;
		
		// TC Length = 112 (60 + 62)
		int nTcLen             = 62;
		
		try{
			// 레코드 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();			

			// 압연공장구분 (2자리로 변경 되었음 P B C => 2자리로 바뀌었음) : A후판슬라브 => D야드 => 동기화코드(P, Q) => (PA, PB)
			szYdGp = ydDaoUtils.paraRecChkNull(inRec, "YD_GP").trim();
			
			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 * 임춘수 수정 2009.06.18
			 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			if(!szYdGp.equals("D")){
				szMsg = "[크레인작업계획]A후판슬라브야드[D]가 아닙니다. - 전달된 야드구분[" + szYdGp + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				return 0;
			}
			szYD_INFO_SYNC_CD = ydDaoUtils.paraRecChkNull(inRec, "YD_INFO_SYNC_CD");
			if( szYD_INFO_SYNC_CD.equals("H") ) {				//H : C열연가열로보급 - C연주슬라브야드인 경우 - 발생안함
				szPtopPlntGp = "HC";	//C열연
				intGp = 87;
				szMsg = "[크레인작업계획]Y3(A후판슬라브야드L2) 송신  크레인작업계획  데이터 전송 시 C열연가열로보급인 야드정보동기화코드[" + szYD_INFO_SYNC_CD + "] 입니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}else if( szYD_INFO_SYNC_CD.equals("P") ) {			//P : A후판가열로보급 - A후판슬라브야드인 경우
				szPtopPlntGp = "PA";	//A후판
				intGp	= 13;
				szMsg = "[크레인작업계획]Y3(A후판슬라브야드L2) 송신  크레인작업계획  데이터 전송 시 A후판가열로보급인 야드정보동기화코드[" + szYD_INFO_SYNC_CD + "] 입니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}else if( szYD_INFO_SYNC_CD.equals("Q") ) {			//Q : B후판가열로보급 - B후판슬라브야드인 경우 - 발생안함
				szPtopPlntGp = "PB";	//B후판
				intGp	= 13;
				szMsg = "[크레인작업계획]Y3(A후판슬라브야드L2) 송신  크레인작업계획  데이터 전송 시 B후판가열로보급인 야드정보동기화코드[" + szYD_INFO_SYNC_CD + "] 입니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				szMsg = "[크레인작업계획]Y3(A후판슬라브야드L2) 송신  크레인작업계획  데이터 전송 시 지원하지 않는 야드정보동기화코드[" + szYD_INFO_SYNC_CD + "] 입니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
					
			//=======================================================================================================================
			// 슬라브 공통테이블조회
			//=======================================================================================================================
			recPara.setField("PTOP_PLNT_GP", szPtopPlntGp);
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, intGp);
			if(intRtnVal < 0) {
				szMsg = "슬라브 공통 테이블 조회오류  (" + szPtopPlntGp + ")" + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			} else if(intRtnVal == 0) {
				szMsg = "슬라브 공통 테이블 조회건수 없음  (" + szPtopPlntGp + ")" + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;
			}		
			
			for(int nIdx=0; nIdx<intRtnVal; nIdx++) {
				recGetVal = rsResult.getRecord(nIdx);		

				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();									
				outRec.setField("MSG_ID" , "YDY3L003");
				outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
				outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
				outRec.setField("MSG_GP" , "I");
				outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
				outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

				// 야드정보동기화코드 [야드정보동기화코드]
				outRec.setField("YD_INFO_SYNC_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(inRec, "YD_INFO_SYNC_CD"), 1, 1));
				
				// 야드재료정보송신매수 [야드재료정보송신매수]
				outRec.setField("YD_STL_INFO_SND_SH", YdUtils.fillSpZr(Integer.toString(intRtnVal), 3, 1));

				// 야드재료정보송신순번 [야드재료정보송신순번]
				outRec.setField("YD_STL_INFO_SND_CNT", YdUtils.fillSpZr(Integer.toString(nIdx+1), 3, 1));
				
				// 재료번호 [재료번호]
				szSTL_NO = YdUtils.fillSpZr(recGetVal.getFieldString("STL_NO"), 11, 1);
				outRec.setField("STL_NO", szSTL_NO);
				
				// 압연공장구분 [압연공장구분]
				outRec.setField("PTOP_PLNT_GP", YdUtils.fillSpZr(szPtopPlntGp, 2, 1));
		
				// 가열로장입Lot번호 [가열로장입Lot번호]
				outRec.setField("REFUR_CHG_LOT_NO", YdUtils.fillSpZr(recGetVal.getFieldString("REFUR_CHG_LOT_NO"), 10, 1));

				// 생산통제Lot스케줄일련번호 [생산통제Lot스케줄일련번호]
				outRec.setField("CT_LOT_SCH_SERNO", YdUtils.fillSpZr(recGetVal.getFieldString("REFUR_CHG_PLN_SERNO"), 22, 1));

				// 날판번호
				outRec.setField("PL_MPL_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "PL_MPL_NO"), 10, 1));
				
				// RecordSet에 추가	
				outRecSet.addRecord(outRec);
			}
		}catch(Exception e){
			szMsg = "Y3(A후판슬라브야드L2) 송신  크레인작업계획  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}
		
		return outRecSet.size();
	} // end of makeY3L003()
	
	
	
	
	
	/**
	 * YDY3L004 : 크레인작업지시
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeY3L004(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID						VARCHAR2(8)		YDY3L004
		//		2.	생성일					DATE						VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME						VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP						VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN						NUMBER(4)		
		//		6.	임시						TEMP						VARCHAR2(29)		

		//		7.	야드설비ID				YD_EQP_ID					VARCHAR2(6)		크레인설비 ID
		//		8.	야드작업진행상태			YD_WRK_PROG_STAT			VARCHAR2(1)		"W" 작업지시대기, "1" 권상작업, "3" 권하작업
		//		9.	야드스케쥴코드				YD_SCH_CD					VARCHAR2(8)		
		//      10. 야드스케쥴명                             YD_SCH_NAME                 VARCHAR2(30) 
		//		11.	야드설비스케쥴ID			YD_CRN_SCH_ID				VARCHAR2(18)
		//		12.	야드크레인작업매수			YD_EQP_WRK_SH				NUMBER(2)		
		//		13.	야드크레인작업중량			YD_EQP_WRK_WT				NUMBER(7)		
		//		14.	야드크레인작업총두께		YD_EQP_WRK_T				NUMBER(7,3)		
		//		15.	야드크레인작업최대폭		YD_EQP_WRK_MAX_W			NUMBER(5,1)		
		//		16.	야드크레인작업최대길이		YD_EQP_WRK_MAX_L			NUMBER(7)		
		//		17.	야드크레인스케줄잔여회수		YD_SCH_RMD_CNT				NUMBER(2)		권상모음, 권하분리작업 시 크레인 Handling 잔여 회수
		//		18.	야드권상지시위치			YD_UP_WO_LOC				VARCHAR2(8)		
		//		19.	야드권상지시단				YD_UP_WO_LAYER				VARCHAR2(3)		
		//		20.	야드권상지시X축			YD_UP_WO_LOC_XAXIS			NUMBER(7)		
		//		21.	야드권상지시X축오차최대		YD_UP_WO_XAXIS_GAP_MAX		NUMBER(5)		
		//		22.	야드권상지시X축오차최소		YD_UP_WO_XAXIS_GAP_MIN		NUMBER(5)		
		//		23.	야드권상지시Y축			YD_UP_WO_LOC_YAXIS			NUMBER(5)		
		//		24.	야드권상지시Y축오차최대		YD_UP_WO_YAXIS_GAP_MAX		NUMBER(5)		
		//		25.	야드권상지시Y축오차최소		YD_UP_WO_YAXIS_GAP_MIN		NUMBER(5)		
		//		26.	야드권상지시Z축			YD_UP_WO_LOC_ZAXIS			NUMBER(5)		
		//		27.	야드권상지시Z축오차최대		YD_UP_WO_ZAXIS_GAP_MAX		NUMBER(5)		
		//		28.	야드권상지시Z축오차최소		YD_UP_WO_ZAXIS_GAP_MIN		NUMBER(5)		
		//		29.	야드권하지시위치			YD_DN_WO_LOC				VARCHAR2(8)		
		//		30.	야드권하지시단				YD_DN_WO_LAYER				VARCHAR2(3)		
		//		31.	야드권하지시X축			YD_DN_WO_LOC_XAXIS			NUMBER(7)		
		//		32.	야드권하지시X축오차최대		YD_DN_WO_XAXIS_GAP_MAX		NUMBER(5)		
		//		33.	야드권하지시X축오차최소		YD_DN_WO_XAXIS_GAP_MIN		NUMBER(5)		
		//		34.	야드권하지시Y축			YD_DN_WO_LOC_YAXIS			NUMBER(5)		
		//		35.	야드권하지시Y축오차최대		YD_DN_WO_YAXIS_GAP_MAX		NUMBER(5)		
		//		36.	야드권하지시Y축오차최소		YD_DN_WO_YAXIS_GAP_MIN		NUMBER(5)		
		//		37.	야드권하지시Z축			YD_DN_WO_LOC_ZAXIS			NUMBER(5)		
		//		38.	야드권하지시Z축오차최대		YD_DN_WO_ZAXIS_GAP_MAX		NUMBER(5)		
		//		39.	야드권하지시Z축오차최소		YD_DN_WO_ZAXIS_GAP_MIN		NUMBER(5)		
		//		40.	야드설비ID2				YD_EQP_ID2					VARCHAR2(6)		권상 또는 권하위치가 대차 및 차량인 경우
		//		41.	야드대차목적동				YD_TC_AIM_BAY_GP			VARCHAR2(1)	
		//		42.	야드차량사용구분			YD_CAR_USE_GP				VARCHAR2(1)		"L" 구내운송차량, "G" 제품출하차량
		//		43.	차량번호					CAR_NO						VARCHAR2(15)	제품출하차량
		//		44.	운송장비코드				TRN_EQP_CD					VARCHAR2(8)		구내운송차량
		//		45.	야드설비작업매수			YD_EQP_WRK_SH				NUMBER(2,0)		대차, 차량스케줄의 설비작업매수
		//		46.	야드설비잔량매수			YD_EQP_RMN_SH				NUMBER(2,0)		대차, 차량스케줄의 설비작업매수 - 크레인 작업이 미완료된 매수 
		//		GROUP[10]---------------------------------------------------------- STL1 : 최상단 . . . STL10 최하단
		//		47.	재료번호					STL_NO1						VARCHAR2(11)		
		//		48.	야드재료중량				YD_STL_WT1					NUMBER(5)			
		//		49.	야드재료두께				YD_STL_T1					NUMBER(6,3)		
		//		50.	야드재료폭				YD_STL_W1					NUMBER(5,1)		
		//		51.	야드재료길이				YD_STL_L1					NUMBER(7)	
		//		GROUP[10]----------------------------------------------------------END
		//		52.	야드스케쥴코드_Next		YD_SCH_CD_NEXT				VARCHAR2(8)			
		//      53. 야드스케쥴명_Next         YD_SCH_NAME_NEXT            VARCHAR2(30) 
		//		54.	야드권상지시위치_Next		YD_UP_WO_LOC_NEXT			VARCHAR2(8)			
		//		55.	야드권상지시단_Next		YD_UP_WO_LAYER_NEXT			VARCHAR2(3)			
		//		56.	야드권하지시위치_Next		YD_DN_WO_LOC_NEXT			VARCHAR2(8)			
		//		57.	야드권하지시단_Next		YD_DN_WO_LAYER_NEXT			VARCHAR2(3)		
		//		58.	재료번호_Next				STL_NO_NEXT					VARCHAR2(11)
		//		59.	야드크레인작업매수_Next		YD_CRN_WRK_SH_NEXT			NUMBER(2)			
		//		60.	야드크레인작업중량_Next		YD_CRN_WRK_WT_NEXT			NUMBER(7)			
		//		61.	야드크레인작업총두께_Next	YD_CRN_WRK_T_NEXT			NUMBER(7,3)			
		//		62.	야드크레인작업최대폭_Next	YD_CRN_WRK_MAX_W_NEXT		NUMBER(5,1)			
		//		63.	야드크레인작업최대길이_Next	YD_CRN_WRK_MAX_L_NEXT		NUMBER(7)			
		
		// 레코드 선언
		JDTORecord recPara1       = null;
		JDTORecord recPara2       = null;
		JDTORecordSet rsResult1   = null;
		JDTORecordSet rsResult2   = null;
		JDTORecord recGetVal1     = null;
		JDTORecord recGetVal2     = null;
		JDTORecord outRec         = null;			

		// DAO객체 생성
		YdCrnSchDao ydCrnSchDao   = new YdCrnSchDao();
		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();
		
		// 변수선언
		String szMethodName       = "makeY3L004";
		String szMsg              = "";
		String szOperationName    = "A후판슬라브L2 크레인작업지시";
		String szYD_SCH_NAME      = "";
		String szYD_SCH_NAME_NEXT = "";
		String szYD_EQP_ID2       = "";
		String szCrnSchID         = "";
		String szYD_SCH_CD        = "";
		String szYD_SCH_CD_NEXT   = "";
		String szYD_WRK_PROG_STAT = "";
		String szMSG_GP           = "";
		String szTemp             = "";
		
		// 문자열변환 임시변수
		String szConv             = "";

		// 리턴값
		int intRtnVal1            = 0;
		int intRtnVal2            = 0;
		int nIdx                  = 0; 
		
		// TC Length = 743 (60 + 683)
		int nTcLen                = 683;
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeY3L004() IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);	
			
			szCrnSchID = ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID");
			szYD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(inRec, "YD_WRK_PROG_STAT");
			szMSG_GP = ydDaoUtils.paraRecChkNull(inRec, "MSG_GP");

			//=======================================================================================================================
			// [1] 크레인스케쥴+크레인작업재료 테이블 조회 (Key: 크레인스케쥴 ID) 
			//=======================================================================================================================
			recPara1 = JDTORecordFactory.getInstance().create();			
			rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
			recPara1.setField("YD_CRN_SCH_ID", szCrnSchID);
			recPara1.setField("YD_WRK_PROG_STAT", szYD_WRK_PROG_STAT);
			intRtnVal1 = ydCrnSchDao.getYdCrnsch(recPara1, rsResult1, 41);
			if(intRtnVal1 < 0) {
				szMsg = "크레인스케쥴 테이블 조회오류  (" + szCrnSchID + ") " + "[Ret : " + intRtnVal1 + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			} else if(intRtnVal1 == 0) {
				szMsg = "크레인스케쥴 테이블 조회건수 없음  (" + szCrnSchID + ") " + "[Ret : " + intRtnVal1 + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;
			}		

			outRec = JDTORecordFactory.getInstance().create();						
			
			rsResult1.first();
			recGetVal1 = rsResult1.getRecord();		

			// 헤더부
			outRec.setField("MSG_ID" , "YDY3L004");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			
			if(szMSG_GP.equals("D") || szMSG_GP.equals("U")){
				outRec.setField("MSG_GP" , YdUtils.fillSpZr(szMSG_GP, 1, 1));
			} else {
				outRec.setField("MSG_GP" , "I");
			}
			
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));
			
			// 야드설비ID [야드설비ID]
			outRec.setField("YD_EQP_ID", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_ID"), 6, 1));
			
			// 야드작업진행상태 [야드작업진행상태]
			szTemp = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal1, "YD_WRK_PROG_STAT"), 1, 1);
			if(szTemp.equals("W"))
				outRec.setField("YD_WRK_PROG_STAT", YdUtils.fillSpZr("1", 1, 1));
			else
				outRec.setField("YD_WRK_PROG_STAT", YdUtils.fillSpZr(szTemp, 1, 1));
			
			// 야드스케쥴코드 [야드스케쥴코드]
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recGetVal1, "YD_SCH_CD");
			outRec.setField("YD_SCH_CD", YdUtils.fillSpZr(szYD_SCH_CD, 8, 1));
			
			//=======================================================================================================================
			// [2] 스케쥴 기준 테이블 조회
			//=======================================================================================================================
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recGetVal1, "YD_SCH_CD");
			if(!szYD_SCH_CD.trim().equals("")){
				recPara2 = JDTORecordFactory.getInstance().create();			
				rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");			
				recPara2.setField("YD_SCH_CD", szYD_SCH_CD);
				intRtnVal2 = ydSchRuleDao.getYdSchrule(recPara2, rsResult2, 0);
				if(intRtnVal2 < 0) {
					szMsg = "스케쥴 기준 테이블 조회오류 (" + ydDaoUtils.paraRecChkNull(inRec, "YD_SCH_CD") + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
	
					szYD_SCH_NAME = YdUtils.fillSpZr_KOR(" ", 30, 1);	
					szYD_EQP_ID2 = YdUtils.fillSpZr(" ", 6, 1);			
				} else if(intRtnVal2 == 0) {
					szMsg = "스케쥴 기준 테이블 조회건수 없음 (" + ydDaoUtils.paraRecChkNull(inRec, "YD_SCH_CD") + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
					
					szYD_SCH_NAME = YdUtils.fillSpZr_KOR(" ", 30, 1);	
					szYD_EQP_ID2 = YdUtils.fillSpZr(" ", 6, 1);			
				}else {		
					rsResult2.first();
					recGetVal2 = rsResult2.getRecord();				
					
					szYD_SCH_NAME = YdUtils.fillSpZr_KOR(recGetVal2.getFieldString("CD_CONTENTS"), 30, 1);
					szYD_EQP_ID2 = YdUtils.fillSpZr(recGetVal2.getFieldString("YD_ALT_CRN"), 6, 1);
				}			
			} else {
				szMsg = "스케쥴 코드가 없어서 기준테이블을 조회 안함 : YD_SCH_NAME, YD_EQP_ID2는 공백";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				
				szYD_SCH_NAME = YdUtils.fillSpZr_KOR(" ", 30, 1);	
				szYD_EQP_ID2 = YdUtils.fillSpZr(" ", 6, 1);							
			}
			
			
			
			// 야드스케쥴명
			outRec.setField("YD_SCH_NAME", szYD_SCH_NAME);	
			
			// 야드크레인스케쥴ID [야드크레인스케쥴ID]
			outRec.setField("YD_CRN_SCH_ID", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_CRN_SCH_ID"), 18, 1));
			
			// 야드크레인작업매수 [야드크레인작업매수]
			outRec.setField("YD_CRN_WRK_SH", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_WRK_SH"), 2, 1));
			
			// 야드크레인작업중량 [야드크레인작업중량]
			outRec.setField("YD_CRN_WRK_WT", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_WRK_WT"), 7, 1));
			
			// 야드크레인작업총두께 [야드크레인작업총두께]
			szConv = YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_WRK_T"), 8, 1);
			outRec.setField("YD_CRN_WRK_T", ydUtils.FloatLRPAD(szConv, 7, 3, '0'));	
			
			// 야드크레인작업최대폭 [야드크레인작업최대폭]
			szConv = YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_WRK_MAX_W"), 6, 1);
			outRec.setField("YD_CRN_WRK_MAX_W", ydUtils.FloatLRPAD(szConv, 5, 1, '0'));	
			
			// 야드크레인작업최대길이 [야드크레인작업최대길이]
			outRec.setField("YD_CRN_WRK_MAX_L", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_WRK_MAX_L"), 7, 1));
			

			//*************************************************************************************************************************************
			// 야드크레인스케줄잔여회수 [야드크레인스케줄잔여회수]            
			outRec.setField("YD_CRN_SCH_RMD_CNT", YdUtils.fillSpZr("00", 2, 1));
			//*************************************************************************************************************************************
			
			
			// 야드권상지시위치 [야드권상지시위치]
			outRec.setField("YD_UP_WO_LOC", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_LOC"), 8, 1));
			
			// 야드권상지시단 [야드권상지시단]
			outRec.setField("YD_UP_WO_LAYER", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_LAYER"), 3, 1));
			
			// 야드권상지시X축[야드권상지시X축]
			outRec.setField("YD_UP_WO_LOC_XAXIS", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_LOC_XAXIS"), 7, 1));
			
			// 야드권상지시X축오차최대 [야드권상지시X축오차최대]
			outRec.setField("YD_UP_WO_XAXIS_GAP_MAX", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_XAXIS_GAP_MAX"), 5, 1));
			
			// 야드권상지시X축오차최소 [야드권상지시X축오차최소]
			outRec.setField("YD_UP_WO_XAXIS_GAP_MIN", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_XAXIS_GAP_MIN"), 5, 1));
			
			// 야드권상지시Y축 [야드권상지시Y축]
			outRec.setField("YD_UP_WO_LOC_YAXIS", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_LOC_YAXIS"), 5, 1));
			
			// 야드권상지시Y축오차최대 [야드권상지시Y축오차최대]
			outRec.setField("YD_UP_WO_YAXIS_GAP_MAX", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_YAXIS_GAP_MAX"), 5, 1));
			
			// 야드권상지시Y축오차최소 [야드권상지시Y축오차최소]
			outRec.setField("YD_UP_WO_YAXIS_GAP_MIN", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_YAXIS_GAP_MIN"), 5, 1));
			
			// 야드권상지시Z축 [야드권상지시Z축]
			outRec.setField("YD_UP_WO_LOC_ZAXIS", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_LOC_ZAXIS"), 5, 1));
			
			// 야드권상지시Z축오차최대 [야드권상지시Z축오차최대]
			outRec.setField("YD_UP_WO_ZAXIS_GAP_MAX", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_ZAXIS_GAP_MAX"), 5, 1));
			
			// 야드권상지시Z축오차최소 [야드권상지시Z축오차최소]
			outRec.setField("YD_UP_WO_ZAXIS_GAP_MIN", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_ZAXIS_GAP_MIN"), 5, 1));
			
			// 야드권하지시위치 [야드권하지시위치]
			outRec.setField("YD_DN_WO_LOC", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_LOC"), 8, 1));
			
			// 야드권하지시단 [야드권하지시단]
			outRec.setField("YD_DN_WO_LAYER", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_LAYER"), 3, 1));
			
			// 야드권하지시X축 [야드권하지시X축]
			outRec.setField("YD_DN_WO_LOC_XAXIS", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_LOC_XAXIS"), 7, 1));
			
			// 야드권하지시X축오차최대 [야드권하지시X축오차최대]
			outRec.setField("YD_DN_WO_XAXIS_GAP_MAX", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_XAXIS_GAP_MAX"), 5, 1));
			
			// 야드권하지시X축오차최소 [야드권하지시X축오차최소]
			outRec.setField("YD_DN_WO_XAXIS_GAP_MIN", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_XAXIS_GAP_MIN"), 5, 1));
			
			// 야드권하지시Y축 [야드권하지시Y축]
			outRec.setField("YD_DN_WO_LOC_YAXIS", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_LOC_YAXIS"), 5, 1));
			
			// 야드권하지시Y축오차최대 [야드권하지시Y축오차최대]
			outRec.setField("YD_DN_WO_YAXIS_GAP_MAX", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_YAXIS_GAP_MAX"), 5, 1));
			
			// 야드권하지시Y축오차최소 [야드권하지시Y축오차최소]
			outRec.setField("YD_DN_WO_YAXIS_GAP_MIN", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_YAXIS_GAP_MIN"), 5, 1));
			
			// 야드권하지시Z축 [야드권하지시Z축]
			outRec.setField("YD_DN_WO_LOC_ZAXIS", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_LOC_ZAXIS"), 5, 1));
			
			// 야드권하지시Z축오차최대 [야드권하지시Z축오차최대]
			outRec.setField("YD_DN_WO_ZAXIS_GAP_MAX", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_ZAXIS_GAP_MAX"), 5, 1));
			
			// 야드권하지시Z축오차최소 [야드권하지시Z축오차최소]
			outRec.setField("YD_DN_WO_ZAXIS_GAP_MIN", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_ZAXIS_GAP_MIN"), 5, 1));			

			// 야드설비ID2 [야드설비ID2]                             
			outRec.setField("YD_EQP_ID2", szYD_EQP_ID2);				
			
			//=======================================================================================================================
			// [3] 작업예약 조회
			//=======================================================================================================================
			recPara2 = JDTORecordFactory.getInstance().create();			
			rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");			
			recPara2.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID"));
			intRtnVal2 = ydWrkbookDao.getYdWrkbook(recPara2, rsResult2, 2);			
			if(intRtnVal2 < 0) {
				szMsg = "작업예약 테이블 조회오류 [Ret : " + intRtnVal2 + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	

				// 야드대차목적동[야드대차목적동]
				outRec.setField("YD_TC_AIM_BAY_GP", YdUtils.fillSpZr(" ", 1, 1));

				// 야드차량사용구분 [야드차량사용구분] 
				outRec.setField("YD_CAR_USE_GP", YdUtils.fillSpZr(" ", 1, 1));
				
				// 차량번호 [차량번호] 
				outRec.setField("CAR_NO", YdUtils.fillSpZr_KOR(" ", 15, 1));
				
				// 운송장비코드 [운송장비코드] 
				outRec.setField("TRN_EQP_CD", YdUtils.fillSpZr(" ", 8, 1));					
			} else if(intRtnVal2 == 0) {
				szMsg = "작업예약 테이블 조회건수 없음 [Ret : " + intRtnVal2 + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);	
				
				// 야드대차목적동[야드대차목적동]
				outRec.setField("YD_TC_AIM_BAY_GP", YdUtils.fillSpZr(" ", 1, 1));
				
				// 야드차량사용구분 [야드차량사용구분] 
				outRec.setField("YD_CAR_USE_GP", YdUtils.fillSpZr(" ", 1, 1));
				
				// 차량번호 [차량번호] 
				outRec.setField("CAR_NO", YdUtils.fillSpZr_KOR(" ", 15, 1));
				
				// 운송장비코드 [운송장비코드] 
				outRec.setField("TRN_EQP_CD", YdUtils.fillSpZr(" ", 8, 1));					
			}else {			
				rsResult2.first();
				recGetVal2 = rsResult2.getRecord();				
				
				// 야드대차목적동[야드대차목적동]
				outRec.setField("YD_TC_AIM_BAY_GP", YdUtils.fillSpZr(recGetVal2.getFieldString("YD_AIM_BAY_GP"), 1, 1));

				// 야드차량사용구분 [야드차량사용구분] 
				outRec.setField("YD_CAR_USE_GP", YdUtils.fillSpZr(recGetVal2.getFieldString("YD_CAR_USE_GP"), 1, 1));
				
				// 차량번호 [차량번호] 
				outRec.setField("CAR_NO", YdUtils.fillSpZr_KOR(recGetVal2.getFieldString("CAR_NO"), 15, 1));
				
				// 운송장비코드 [운송장비코드] 
				outRec.setField("TRN_EQP_CD", YdUtils.fillSpZr(recGetVal2.getFieldString("TRN_EQP_CD"), 8, 1));
			}		
			
			// 야드설비작업매수 [야드설비작업매수]
			outRec.setField("YD_EQP_WRK_SH", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_WRK_SH"), 2, 1)); 				


			//*************************************************************************************************************************************										   
			// 야드설비잔량매수[야드설비잔량매수]		       
			outRec.setField("YD_EQP_RMN_SH", YdUtils.fillSpZr("00", 2, 1));
			//*************************************************************************************************************************************										   

			
			intRtnVal1 = (intRtnVal1 >= 10) ? 10 : intRtnVal1;
			
			for(nIdx=0; nIdx<intRtnVal1; nIdx++) {
				recGetVal1 = rsResult1.getRecord(nIdx);		

				// 재료번호 [재료번호]
				outRec.setField("STL_NO"+(1+nIdx), YdUtils.fillSpZr(recGetVal1.getFieldString("STL_NO"), 11, 1));
				
				// 야드재료중량 [야드재료중량]
				outRec.setField("YD_STL_WT"+(1+nIdx), YdUtils.fillSpZr(recGetVal1.getFieldString("YD_MTL_WT"), 5, 1));
				
				// 야드재료두께 [야드재료두께]
				szConv = YdUtils.fillSpZr(recGetVal1.getFieldString("YD_MTL_T"), 7, 1);
				outRec.setField("YD_STL_T"+(1+nIdx), ydUtils.FloatLRPAD(szConv, 6, 3, '0'));	
				
				// 야드재료폭 [야드재료폭]
				szConv = YdUtils.fillSpZr(recGetVal1.getFieldString("YD_MTL_W"), 6, 1);
				outRec.setField("YD_STL_W"+(1+nIdx), ydUtils.FloatLRPAD(szConv, 5, 1, '0'));	
				
				// 야드재료길이 [야드재료길이]
				outRec.setField("YD_STL_L"+(1+nIdx), YdUtils.fillSpZr(recGetVal1.getFieldString("YD_MTL_L"), 7, 1));
			} // end of for(nIdx)			

			for(int nIdx3=nIdx; nIdx3<10; nIdx3++){
				// 재료번호 [재료번호]
				outRec.setField("STL_NO"+(1+nIdx3), YdUtils.fillSpZr(" ", 11, 1));

				// 야드재료중량 [야드재료중량]
				outRec.setField("YD_STL_WT"+(1+nIdx3), YdUtils.fillSpZr(" ", 5, 1));
				
				// 야드재료두께 [야드재료두께]
				outRec.setField("YD_STL_T"+(1+nIdx3), YdUtils.fillSpZr(" ", 6, 1));	
				
				// 야드재료폭 [야드재료폭]
				outRec.setField("YD_STL_W"+(1+nIdx3), YdUtils.fillSpZr(" ", 5, 1));	
				
				// 야드재료길이 [야드재료길이]
				outRec.setField("YD_STL_L"+(1+nIdx3), YdUtils.fillSpZr(" ", 7, 1));
			}
			
			rsResult1.first();
			recGetVal1 = rsResult1.getRecord();	
			
			// 야드스케쥴코드_Next [야드스케쥴코드_Next]
			szYD_SCH_CD_NEXT = ydDaoUtils.paraRecChkNull(recGetVal1, "YD_SCH_CD_NEXT");
			outRec.setField("YD_SCH_CD_NEXT", YdUtils.fillSpZr(szYD_SCH_CD_NEXT, 8, 1));

			//=======================================================================================================================
			// [4] 스케쥴 기준 테이블 조회
			//=======================================================================================================================
			if(!szYD_SCH_CD_NEXT.trim().equals("")){
				recPara2 = JDTORecordFactory.getInstance().create();			
				rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");			
				recPara2.setField("YD_SCH_CD", szYD_SCH_CD_NEXT);
				intRtnVal2 = ydSchRuleDao.getYdSchrule(recPara2, rsResult2, 0);
				if(intRtnVal2 < 0) {
					szMsg = "스케쥴 기준 테이블_NEXT 조회오류 (" + ydDaoUtils.paraRecChkNull(recGetVal1, "YD_SCH_CD_NEXT") + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
	
					szYD_SCH_NAME_NEXT = YdUtils.fillSpZr_KOR(" ", 30, 1);	
				} else if(intRtnVal2 == 0) {
					szMsg = "스케쥴 기준 테이블_NEXT 조회건수 없음 (" + ydDaoUtils.paraRecChkNull(recGetVal1, "YD_SCH_CD_NEXT") + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
					
					szYD_SCH_NAME_NEXT = YdUtils.fillSpZr_KOR(" ", 30, 1);	
				}else {		
					rsResult2.first();
					recGetVal2 = rsResult2.getRecord();				
	
					szYD_SCH_NAME_NEXT = YdUtils.fillSpZr_KOR(recGetVal2.getFieldString("CD_CONTENTS"), 30, 1);
				}
			} else {
				szMsg = "스케쥴 코드_NEXT가 없어서 기준테이블을 조회 안함 : YD_SCH_NAME_NEXT는 공백";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				
				szYD_SCH_NAME_NEXT = YdUtils.fillSpZr_KOR(" ", 30, 1);					
			}
			
			
			// 야드스케쥴명_Next [야드스케쥴명_Next]
			outRec.setField("YD_SCH_NAME_NEXT", szYD_SCH_NAME_NEXT);
			
			// 야드권상지시위치_Next [야드권상지시위치_Next]
			outRec.setField("YD_UP_WO_LOC_NEXT", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_LOC_NEXT"), 8, 1));
			
			// 야드권상지시단_Next [야드권상지시단_Next]
			outRec.setField("YD_UP_WO_LAYER_NEXT", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_LAYER_NEXT"), 3, 1));
			
			// 야드권하지시위치_Next [야드권하지시위치_Next]
			outRec.setField("YD_DN_WO_LOC_NEXT", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_LOC_NEXT"), 8, 1));
			
			// 야드권하지시단_Next [야드권하지시단_Next]
			outRec.setField("YD_DN_WO_LAYER_NEXT", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_LAYER_NEXT"), 3, 1));
			
			// 재료번호_Next [재료번호_Next]
			outRec.setField("STL_NO_NEXT", YdUtils.fillSpZr(recGetVal1.getFieldString("STL_NO_NEXT"), 11, 1));
			
			// 야드크레인작업매수_Next [야드설비작업매수]
			outRec.setField("YD_CRN_WRK_SH_NEXT", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_WRK_SH_NEXT"), 2, 1));
			
			// 야드크레인작업중량_Next [야드크레인작업중량_Next]
			outRec.setField("YD_CRN_WRK_WT_NEXT", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_WRK_WT_NEXT"), 7, 1));
			
			// 야드크레인작업총두께_Next [야드크레인작업총두께_Next]
			szConv = YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_WRK_T_NEXT"), 8, 1);
			outRec.setField("YD_CRN_WRK_T_NEXT", ydUtils.FloatLRPAD(szConv, 7, 3, '0'));	
			
			// 야드크레인작업최대폭_Next [야드크레인작업최대폭_Next]
			szConv = YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_WRK_MAX_W_NEXT"), 6, 1);
			outRec.setField("YD_CRN_WRK_MAX_W_NEXT", ydUtils.FloatLRPAD(szConv, 5, 1, '0'));
			
			// 야드크레인작업최대길이_Next [야드크레인작업최대길이_Next]
			outRec.setField("YD_CRN_WRK_MAX_L_NEXT", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_WRK_MAX_L_NEXT"), 7, 1));				
			
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================makeY3L004() OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);	
		}catch(Exception e){
			szMsg = "Y3(A후판슬라브야드L2) 송신  크레인작업지시  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}
		
		return outRecSet.size();
	} // end of makeY3L004()
	
	
	/**
	 * makeY3LXX2 : 크레인작업지시
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeY3LXX2(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID				VARCHAR2(8)		YDY3L003
		//		2.	생성일					DATE				VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME				VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP				VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN				NUMBER(4)		
		//		6.	임시						TEMP				VARCHAR2(29)		

		//		7.	야드정보동기화코드			YD_INFO_SYNC_CD		VARCHAR2(1)		
		//		8.	야드재료정보송신매수		YD_STL_INFO_SND_SH	NUMBER(3)		
		//		9.	야드재료정보송신순번		YD_STL_INFO_SND_CNT	NUMBER(3)		
		//		10.	재료번호					STL_NO				VARCHAR2(11)	
		//		11.	조업공장구분				PTOP_PLNT_GP		VARCHAR2(2)     2자리로 바뀌었음
		//		12.	가열로장입Lot번호			REFUR_CHG_LOT_NO	VARCHAR2(10)
		//		13.	생산통제Lot스케줄일련번호	CT_LOT_SCH_SERNO	NUMBER(22)
		//      14. 날판번호                                   PL_MPL_NO           VARCHAR2(10) 
		
		// 레코드 선언
		JDTORecord recPara            = null;
		JDTORecordSet rsResult        = null;
		JDTORecord recGetVal          = null;
		JDTORecord recGetValPLMPLNo   = null;
		JDTORecord outRec 	          = null;	
		
		// DAO객체 생성
		YdDaoUtils ydDaoUtils  = new YdDaoUtils();	
		YdUtils ydUtils		   = new YdUtils();
		YdStockDao ydStockDao  = new YdStockDao();

		// 변수선언
		String szMethodName    = "makeY1L002";
		String szMsg           = "";
		String szOperationName = "A후판슬라브L2 저장품제원";
		
		// 야드구분
		String szYdGp          = "";
		
		//야드정보동기화코드
		String szYD_INFO_SYNC_CD = "";
		
		String szSCARFING_DEPTH = "";
		String szConv           = "";
		String szSTLKIND_CD     = "";
		String szSPEC_ABBSYM    = "";
		//쿼리구분자
		int intGp = 0;
		
		// 압연공장구분
		String szPtopPlntGp    = "";

		// 리턴값
		int intRtnVal          = 0;
		
		// TC Length = 112 (60 + 62)
		int nTcLen             = 259;
		
		try{
			// 레코드 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();			

			// 압연공장구분 (2자리로 변경 되었음 P B C => 2자리로 바뀌었음) : A후판슬라브 => D야드 => 동기화코드(P, Q) => (PA, PB)
			szYdGp = ydDaoUtils.paraRecChkNull(inRec, "YD_GP").trim();
			
			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 * 임춘수 수정 2009.06.18
			 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			if(!szYdGp.equals("D")){
				szMsg = "[크레인작업계획]A후판슬라브야드[D]가 아닙니다. - 전달된 야드구분[" + szYdGp + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				return 0;
			}
			szYD_INFO_SYNC_CD = ydDaoUtils.paraRecChkNull(inRec, "YD_INFO_SYNC_CD");
			if( szYD_INFO_SYNC_CD.equals("H") ) {				//H : C열연가열로보급 - C연주슬라브야드인 경우 - 발생안함
				szPtopPlntGp = "HC";	//C열연
				intGp = 87;
				szMsg = "[크레인작업계획]Y3(A후판슬라브야드L2) 송신  크레인작업계획  데이터 전송 시 C열연가열로보급인 야드정보동기화코드[" + szYD_INFO_SYNC_CD + "] 입니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}else if( szYD_INFO_SYNC_CD.equals("P") ) {			//P : A후판가열로보급 - A후판슬라브야드인 경우
				szPtopPlntGp = "PA";	//A후판
				intGp	= 218;
				szMsg = "[크레인작업계획]Y3(A후판슬라브야드L2) 송신  크레인작업계획  데이터 전송 시 A후판가열로보급인 야드정보동기화코드[" + szYD_INFO_SYNC_CD + "] 입니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}else if( szYD_INFO_SYNC_CD.equals("Q") ) {			//Q : B후판가열로보급 - B후판슬라브야드인 경우 - 발생안함
				szPtopPlntGp = "PB";	//B후판
				intGp	= 218;
				szMsg = "[크레인작업계획]Y3(A후판슬라브야드L2) 송신  크레인작업계획  데이터 전송 시 B후판가열로보급인 야드정보동기화코드[" + szYD_INFO_SYNC_CD + "] 입니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				szMsg = "[크레인작업계획]Y3(A후판슬라브야드L2) 송신  크레인작업계획  데이터 전송 시 지원하지 않는 야드정보동기화코드[" + szYD_INFO_SYNC_CD + "] 입니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
					
			//=======================================================================================================================
			// 저장품 테이블 조회
			//=======================================================================================================================
			recPara.setField("PTOP_PLNT_GP", szPtopPlntGp);
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, intGp);
			if(intRtnVal < 0) {
				szMsg = "[크레인작업계획]저장품 테이블 조회오류  (" + szPtopPlntGp + ") " + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			} else if(intRtnVal == 0) {
				szMsg = "[크레인작업계획]저장품 테이블 조회건수 없음  (" + szPtopPlntGp + ") " + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;
			}		
			
			for(int nIdx=0; nIdx<intRtnVal; nIdx++) {
				recGetVal = rsResult.getRecord(nIdx);		
				
				outRec = JDTORecordFactory.getInstance().create();									
				outRec.setField("MSG_ID" , "YDY3L002");
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
				outRec.setField("STL_APPEAR_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP"), 1, 1));	
				
				// 재료번호 [재료번호]
				outRec.setField("STL_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO"), 11, 1));	
				
//				szYdMtlItem = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_MTL_ITEM"), 2, 1).trim();

				// 야드저장위치 [야드저장위치 : BED까지]  
				outRec.setField("YD_STR_LOC", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_NO"), 8, 1));	
			
				// 야드적치단번호 [야드적치단번호]
				outRec.setField("YD_STK_LYR_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_LYR_NO"), 3, 1));	
				
				// 야드재료중량 [야드재료중량]
				outRec.setField("YD_STL_WT", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STL_WT"), 5, 1));	
				
				// 야드재료두께 [야드재료두께]
				szConv = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STL_T"), 7, 1);
				outRec.setField("YD_STL_T", ydUtils.FloatLRPAD(szConv, 6, 3, '0'));	
				
				// 야드재료폭 [야드재료폭]
				szConv = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STL_W"), 6, 1);
				outRec.setField("YD_STL_W", ydUtils.FloatLRPAD(szConv, 5, 1, '0'));	
					
				// 야드재료길이 [야드재료길이]
				outRec.setField("YD_STL_L", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STL_L"), 7, 1));	
				
				// 재료외경
				outRec.setField("MAT_ODIA", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "MAT_ODIA"), 5, 1));	
				
				// 재료내경
				szConv = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "MAT_IDIA"), 6, 1);
				outRec.setField("MAT_IDIA", ydUtils.FloatLRPAD(szConv, 5, 1, '0'));	
										
				// 강종 [강종]
				szSTLKIND_CD   = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "STLKIND_CD"), 3, 1);
				outRec.setField("STLKIND_CD", YdUtils.fillSpZr(szSTLKIND_CD, 3, 1));

				// 규격약호 [규격약호]
				szSPEC_ABBSYM  = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "SPEC_ABBSYM"), 15, 1);
				outRec.setField("SPEC_ABBSYM", YdUtils.fillSpZr(szSPEC_ABBSYM, 15, 1));	

				// 야드입고일자 [등록일자]  
				outRec.setField("YD_IPGO_DD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "REG_DDTT"), 14, 1));

				// 공장공정코드 [공장공정코드]
				outRec.setField("PLNT_PROC_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "PLNT_PROC_CD"), 3, 1));

				// 현재진도코드 [재료진도코드]
				outRec.setField("CURR_PROG_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "CURR_PROG_CD"), 1, 1));

				// 주문여재구분 [주문여재구분]
				outRec.setField("ORD_YEOJAE_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "ORD_YEOJAE_GP"), 1, 1));

				// 주문번호 [주문번호]
				outRec.setField("ORD_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "ORD_NO"), 10, 1));

				// 주문행번 [주문행번]
				outRec.setField("ORD_DTL", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "ORD_DTL"), 3, 1));

				// 구입SLAB번호 [구입SLAB번호]
				outRec.setField("BUY_SLAB_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "BUY_SLAB_NO"), 30, 1));

				// SLAB지시행선코드 [SLAB지시행선코드]
				outRec.setField("SLAB_WO_RT_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "SLAB_WO_RT_CD"), 2, 1));

				// 설계HCR구분 [설계HCR구분]
				outRec.setField("ORD_HCR_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "ORD_HCR_GP"), 1, 1));

				// HCR구분 [HCR구분]
				outRec.setField("HCR_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "HCR_GP"), 1, 1));

				// 연주Machine코드 [야드CCM구분]
				outRec.setField("CC_MC_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "CC_MC_CD"), 1, 1));

				// SCARFING여부 [SCARFING여부]
				outRec.setField("SCARFING_YN", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "SCARFING_YN"), 1, 1));

				// SCARFING완료유무 [SCARFING완료유무]
				outRec.setField("SCARFING_DONE_YN", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "SCARFING_DONE_YN"), 1, 1));

				// 주편손질방법
				outRec.setField("RPR_MTD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "RPR_MTD"), 1, 1));
					
				szSCARFING_DEPTH = ydDaoUtils.paraRecChkNull(recGetVal, "SCARFING_DEPTH");					
				// SCARFING깊이 [SCARFING깊이]
				if(!szSCARFING_DEPTH.equals("")){
					outRec.setField("SCARFING_DEPTH", YdUtils.fillSpZr(szSCARFING_DEPTH, 2, 1));					
				} else {
					outRec.setField("SCARFING_DEPTH", YdUtils.fillSpZr(" ", 2, 1));					
				}

				// 재열재구분 [재열재구분]
				outRec.setField("REHEAT_SLAB_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "REHEAT_SLAB_GP"), 1, 1));

				// 압연공장구분 [조업공장구분]
				outRec.setField("PTOP_PLNT_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "PTOP_PLNT_GP"), 2, 1));  
										
				// 가열로장입Lot번호 [가열로장입Lot번호]
				outRec.setField("REFUR_CHG_LOT_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "REFUR_CHG_LOT_NO"), 10, 1));

				// 생산통제Lot스케줄일련번호 [생산통제Lot스케줄일련번호]
				outRec.setField("CT_LOT_SCH_SERNO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "REFUR_CHG_PLN_SERNO"), 22, 1));

				// 이송지시일자 [이송지시일자]
				outRec.setField("FRTOMOVE_ORD_DATE", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "FRTOMOVE_ORD_DATE"), 8, 1));

				// 이송공장구분 [이송공장구분]
				outRec.setField("FRTOMOVE_PLANT_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "FRTOMOVE_PLANT_GP"), 2, 1));

				// 긴급이송작업지시구분 [긴급이송작업지시구분]
				outRec.setField("URGENT_FRTOMOVE_WORD_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "URGENT_FRTOMOVE_WORD_GP"), 1, 1));

				// HYSCO운송구분 [HYSCO운송구분]
				outRec.setField("HYSCO_TRANS_CLS", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "HYSCO_TRANS_CLS"), 1, 1));

				// 외관종합판정등급 [외관종합판정등급]
				outRec.setField("APPEAR_GRADE", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "APPEAR_GRADE"), 1, 1));

				// 권취코일냉각방법 [권취코일냉각방법]
				outRec.setField("COOL_METHOD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "COOL_METHOD"), 1, 1));

				// 냉각완료구분 [냉각완료구분]
				outRec.setField("COOL_DONE_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "COOL_DONE_GP"), 1, 1));

				// 야드Conveyor분기코드
				outRec.setField("CONV_BRANCH_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "CONV_BRANCH_CD"), 2, 1));				
				
				// 고객코드 [고객코드]
				outRec.setField("CUST_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "CUST_CD"), 6, 1));

				// 목적지코드 [목적지코드]
				outRec.setField("DEST_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "DEST_CD"), 5, 1));

				// 납기기준일 [납기기준일]
				outRec.setField("DLVRDD_RULE_DD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "DLVRDD_RULE_DD"), 8, 1));

				// 품명코드 [품명코드]
				outRec.setField("ITEMNAME_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "ITEMNAME_CD"), 3, 1));

				// 종합판정등급 [종합판정등급]
				outRec.setField("OVERALL_STAMP_GRADE", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "OVERALL_STAMP_GRADE"), 1, 1));

				// 수주구분 [수주구분]
				outRec.setField("ORD_GP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "ORD_GP"), 1, 1));

				// 야드산적LotType [야드산적LotType] 
				outRec.setField("YD_STK_LOT_TP", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_LOT_TP"), 2, 1));

				// 야드산적Lot코드 [야드산적Lot코드]  
				outRec.setField("YD_STK_LOT_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_LOT_CD"), 18, 1));
				
				// 날판번호
				outRec.setField("PL_MPL_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "PL_MPL_NO"), 10, 1));
				
				// RecordSet에 추가	
				outRecSet.addRecord(outRec);
			}
		}catch(Exception e){
			szMsg = "Y3(A후판슬라브야드L2) 송신  크레인작업계획  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}
		
		return outRecSet.size();
	} // end of makeY3LXX2()
	
	
	
	/**
	 * YDY3L005 : 크레인작업실적응답
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeY3L005(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID				MSG_ID				VARCHAR2(8)		YDY3L005
		//		2.	생성일				DATE				VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간				TIME				VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분				MSG_GP				VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이				MSG_LEN				NUMBER  (4)		
		//		6.	임시					TEMP				VARCHAR2(29)		

		//		7.	야드설비ID			YD_EQP_ID			VARCHAR2(6)		크레인설비 ID
		//		8.	야드작업진행상태		YD_WRK_PROG_STAT	VARCHAR2(1)		"2" 권상실적, "4" 권하실적
		//		9.	야드스케쥴코드			YD_SCH_CD			VARCHAR2(8)		
		//		10.	야드설비스케쥴ID		YD_CRN_SCH_ID		VARCHAR2(18)			
		//		11.	야드L2실적구분			YD_L2_WR_GP			VARCHAR2(1)		U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경,,J:지시요구,F:강제권하
		//		12.	야드L3처리결과코드		YD_L3_HD_RS_CD		VARCHAR2(4)		0000:정상, 권상 및 권하에서 이상이 발생한 경우 코드 부여, 9999:크레인작업이 없을 경우
		//		13.	야드L3MESSAGE		YD_L3_MSG			VARCHAR2(40)	"권상(또는 권하)실적이 정상 처리 되었슴니다.
		//																	권상(또는 권하)실적이 Error처리 되었슴니다."		
		
		// 레코드 선언
		JDTORecord outRec       = null;			

		// DAO객체 생성
		YdDaoUtils ydDaoUtils   = new YdDaoUtils();	
		YdUtils ydUtils         = new YdUtils();

		// 메소드명
		String szMethodName     = "makeY3L005";
		String szMsg            = "";
		String szOperationName  = "A후판슬라브L2 크레인작업실적응답";
		
		//야드L2실적구분
		String szYD_L2_WR_GP = null;
		
		// 야드L3처리결과코드
		String szYdL3ResultCd   = "";
		
		// 임시
		String szTemp           = "";
		
		// TC Length = 138(60 + 78)
		int nTcLen              = 78;
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeY3L005() IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);	
			
			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 업무 : 크레인작업실적응답 전문 편집(YDY1L005)
	         * 		U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하에 따른 전문편집
	         *     (U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하)
	         * 수정자 : 임춘수
	         * 일자 : 2009.06.17
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			szYD_L2_WR_GP = ydDaoUtils.paraRecChkNull(inRec, "YD_L2_WR_GP");			//U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
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
			
			outRec = JDTORecordFactory.getInstance().create();

			// 헤더부
			outRec.setField("MSG_ID" , "YDY3L005");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));
			
			// 야드설비ID [크레인설비 ID]
			outRec.setField("YD_EQP_ID", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID"), 6, 1));
			
			// 야드작업진행상태 [야드작업진행상태]
			outRec.setField("YD_WRK_PROG_STAT", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(inRec, "YD_WRK_PROG_STAT"), 1, 1));
			
			// 야드스케쥴코드 [야드스케쥴코드]
			outRec.setField("YD_SCH_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(inRec, "YD_SCH_CD"), 8, 1));
			
			// 야드설비스케쥴ID [야드설비스케쥴ID]
			outRec.setField("YD_CRN_SCH_ID", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID"), 18, 1));
			
			// 야드L2실적구분 [야드L2실적구분]
			szYD_L2_WR_GP = ydDaoUtils.paraRecChkNull(inRec, "YD_L2_WR_GP");
			outRec.setField("YD_L2_WR_GP", YdUtils.fillSpZr(szYD_L2_WR_GP, 1, 1));
			
			// 야드L3처리결과코드 [야드L3처리결과코드]
			szYdL3ResultCd = ydDaoUtils.paraRecChkNull(inRec, "YD_L3_HD_RS_CD");
			outRec.setField("YD_L3_HD_RS_CD", YdUtils.fillSpZr(szYdL3ResultCd, 4, 1));
			if( szYdL3ResultCd.equals(YdConstant.CRN_WRK_RE_CD_NORMAL_HD) || szYdL3ResultCd.equals(YdConstant.CRN_WRK_RE_CD_NO_WRK) ){
				szTemp += "이 정상 처리 되었습니다.";
			//}else if(szYdL3ResultCd.equals("9999")){
			//	szTemp += "이 더 이상 없습니다.";
			}else {
				szTemp += "이 Error처리 되었습니다.";
			}
			
			// 야드L3MESSAGE
			outRec.setField("YD_L3_MSG", YdUtils.fillSpZr_KOR(szTemp, 40, 1));
			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================makeY3L005() OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);	
		}catch(Exception e){
			szMsg = "Y3(A후판슬라브야드L2) 송신  크레인작업실적응답  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}

		return outRecSet.size();
	} // end of makeY3L005()

	
	/**
	 * YDY3L006 : 대차출발지시 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeY3L006(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID				MSG_ID					VARCHAR2(8)		YDY3L006
		//		2.	생성일				DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간				TIME					VARCHAR2(8)		24HH-mm-ss
		//		4.	전문구분				MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이				MSG_LEN					NUMBER  (4)		
		//		6.	임시					TEMP					VARCHAR2(29)	
	
		//		7.	야드설비ID			YD_EQP_ID				VARCHAR2(6)		대차 야드설비ID (DD 참조)
		//		8.	야드설비작업상태		YD_EQP_WRK_STAT			VARCHAR2(1)		
		//		9.	야드목표동구분			YD_AIM_BAY_GP			VARCHAR2(1)		
		//		10.	야드대차상차위치		YD_TCAR_LD_LOC			VARCHAR2(6)		
		//		11.	야드대차하차위치		YD_TCAR_UD_LOC			VARCHAR2(6)		
		//		12.	야드설비작업매수		YD_EQP_WRK_SH			NUMBER(2)		
		//		13.	야드설비작업중량		YD_EQP_WRK_WT			NUMBER(7)		
		
		// 레코드 선언
		JDTORecord recPara 	       = null;
		JDTORecordSet rsResult     = null;
		JDTORecord recGetVal	   = null;
		JDTORecord outRec		   = null;

		// DAO객체 생성
		YdDaoUtils ydDaoUtils	   = new YdDaoUtils();	
		YdUtils ydUtils 		   = new YdUtils();
		YdTcarSchDao ydTcarSchDao  = new YdTcarSchDao();
		
		// 변수선언
		String szMethodName        = "makeY3L006";
		String szMsg               = "";
		String szOperationName     = "후판슬라브야드L2 대차출발지시";
		
		// 상태값
		String szYD_EQP_WRK_STAT   = "";
		
		// 야드상차정지위치
		String szYD_CARLD_STOP_LOC = "";

		// 야드하차정지위치
		String szYD_CARUD_STOP_LOC = "";
		
		// 상태값
		
		
		// 목표동구분
		String szYD_AIM_BAY_GP     = "";
		
		// 리턴값
		int intRtnVal              = 0;
		
		// Length 80 (60 + 29)
		int nTcLen                 = 29;
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeY3L006() IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
			
			// 레코드 생성			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();			
			
			//=======================================================================================================================
			// 대차스케쥴 테이블 조회 (Key: 대차스케쥴 ID)
			//=======================================================================================================================
			recPara.setField("YD_TCAR_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_TCAR_SCH_ID").trim());
			intRtnVal = ydTcarSchDao.getYdTcarsch(recPara, rsResult, 2);
			if(intRtnVal < 0) {
				szMsg = "[MakeTcY3::makeY3L006()] 대차스케쥴 테이블 조회오류  (" + ydDaoUtils.paraRecChkNull(inRec, "YD_TCAR_SCH_ID") + ")" + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			} else if(intRtnVal == 0) {
				szMsg = "[MakeTcY3::makeY3L006()] 대차스케쥴 테이블 조회건수 없음  (" + ydDaoUtils.paraRecChkNull(inRec, "YD_TCAR_SCH_ID") + ")" + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;
			}		
			
			for(int nIdx=0; nIdx<intRtnVal; nIdx++) {
				recGetVal = rsResult.getRecord(nIdx);			
			
				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();									
				outRec.setField("MSG_ID" , "YDY3L006");
				outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
				outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
				outRec.setField("MSG_GP" , "I");
				outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
				outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

				// 야드설비ID [야드설비ID]
				outRec.setField("YD_EQP_ID", YdUtils.fillSpZr(recGetVal.getFieldString("YD_EQP_ID"), 6, 1));

				// 야드설비작업상태 [야드설비작업상태]
				outRec.setField("YD_EQP_WRK_STAT", YdUtils.fillSpZr(recGetVal.getFieldString("YD_EQP_WRK_STAT"), 1, 1));				
								
				// 야드목표동구분 [야드목표동구분] - 야드하차정지위치, 상차정지위치
				// YD_EQP_WRK_STAT  야드설비작업상태    U:상차정지위치값   L:하차정지위치값
				szYD_EQP_WRK_STAT = YdUtils.fillSpZr(recGetVal.getFieldString("YD_EQP_WRK_STAT"), 6, 1).trim();
				szYD_CARLD_STOP_LOC = YdUtils.fillSpZr(recGetVal.getFieldString("YD_TCAR_LD_LOC"), 6, 1).trim();
				szYD_CARUD_STOP_LOC = YdUtils.fillSpZr(recGetVal.getFieldString("YD_TCAR_UD_LOC"), 6, 1).trim();
				if(szYD_EQP_WRK_STAT.equals("L")){
					if(szYD_CARUD_STOP_LOC != null && !szYD_CARUD_STOP_LOC.equals("")){
						szYD_AIM_BAY_GP = szYD_CARUD_STOP_LOC.substring(1, 2);											
					} else {
						szYD_AIM_BAY_GP = "";	
						szMsg = "[MakeTcY3::makeY3L006()] YD_CARUD_STOP_LOC 항목의 값이 없음";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);									
					}
				}else if(szYD_EQP_WRK_STAT.equals("U")){
					if(szYD_CARLD_STOP_LOC != null && !szYD_CARLD_STOP_LOC.equals("")){
						szYD_AIM_BAY_GP = szYD_CARLD_STOP_LOC.substring(1, 2);					
					} else {
						szYD_AIM_BAY_GP = "";	
						szMsg = "[MakeTcY3::makeY3L006()] YD_CARLD_STOP_LOC 항목의 값이 없음";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);									
					}
				}else {
					szMsg = "[MakeTcY3::makeY3L006()] 목표동구분에서 야드설비작업상태가 (L 혹은 U) 값이 없음: ";	
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);										
					return 0;
				}
				outRec.setField("YD_AIM_BAY_GP", YdUtils.fillSpZr(szYD_AIM_BAY_GP, 1, 1));				
				
				// 야드대차상차위치 [야드대차상차위치]
				outRec.setField("YD_TCAR_LD_LOC", YdUtils.fillSpZr(szYD_CARLD_STOP_LOC, 6, 1));				
				
				// 야드대차하차위치 [야드대차하차위치]
				outRec.setField("YD_TCAR_UD_LOC", YdUtils.fillSpZr(szYD_CARUD_STOP_LOC, 6, 1));		
				
				// 야드설비작업매수(나중에 고체세여)
				outRec.setField("YD_EQP_WRK_SH", "00");		

				// 야드설비작업중량(나중에 고체세여)
				outRec.setField("YD_EQP_WRK_WT", "0000000");		

				// RecordSet에 추가	
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szClassName, szMethodName, "\n======================makeY3L006() OUT("+ Integer.toString(nIdx) + ")==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);			
				ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
			}
		}catch(Exception e){
			szMsg = "[MakeTcY3::makeY3L006()] Y3(후판슬라브야드L2) 대차출발지시  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}
	
		return outRecSet.size();
	} // end of makeY3L006()	

	
} // end of class MakeTcY3
