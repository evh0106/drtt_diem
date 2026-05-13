package com.inisteel.cim.yd.common.util.tcconst;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.yd.common.dao.YdCommDAO;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;





/**
 * Y5 (C열연코일야드L2) 송신 용 전문 생성
 * @author YHWHman
 *
 */
public class MakeTcY5 {

	// YDY5L001	저장위치제원
	// YDY5L002	저장품제원	
	// YDY5L004	크레인작업지시	
	// YDY5L005	크레인작업실적응답	
	// YDY5L006	대차출발지시	
	// YDY5L007 작업현황응답
	
	
	// 클래스명
	private static final String szClassName  = MakeTcY5.class.getName();
	private static YdDaoUtils ydDaoUtils =new YdDaoUtils();
	
	/**
	 * YDY5L001 : 저장위치제원
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec 
	 */
	public static int makeY5L001(JDTORecord inRec, JDTORecordSet outRecSet){
		//	1.	전문 ID							MSG_ID					VARCHAR2(8)		YDY5L001
		//	2.	생성일							DATE					VARCHAR2(10)	YYYY-MM-DD
		//	3.	생성시간							TIME					VARCHAR2(8)		24HH-MM-SS
		//	4.	전문구분							MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//	5.	전문길이							MSG_LEN					NUMBER(4)		
		//	6.	임시								TEMP					VARCHAR2(29)		

		//	7.	야드정보동기화코드					YD_INFO_SYNC_CD			VARCHAR2(1)		1:동,2:SPAN,3:열,4:BED
		//	8.	야드구분							YD_GP					VARCHAR2(1)		"D"
		//	9.	야드동구분						YD_BAY_GP				VARCHAR2(1)		부하를 방지하기 위해 최대 동 단위를 허용함
		//	10.	야드설비구분						YD_EQP_GP				VARCHAR2(2)		야드의 설비, Span을 코드로 부여한 Data
		//	11.	야드적치열번호						YD_STK_COL_NO			VARCHAR2(2)		숫자 0을 포함하여 관리 ("01") 
		//	12.	야드적치Bed번호					YD_STK_BED_NO			VARCHAR2(2)		숫자 0을 포함하여 관리 ("01") 
		//      야드적치단번호						YD_STK_LYR_NO			VARCHAR2(1)		"1" OR "2"
		//	13.	야드적치Bed길이구분				YD_STK_BED_L_GP			VARCHAR2(1)		"S" 단척, "M" 중척, "L" 장척
		//	14.	야드적치Bed폭구분					YD_STK_BED_W_GP			VARCHAR2(1)		"N" 협폭, "M" 보폭, "W" 광폭
		//	15.	야드적치Bed방향구분				YD_STK_BED_DIR_GP		VARCHAR2(1)		"X" 주행, "Y" 횡행
		//	16.	야드적치Bed활성상태				YD_STK_BED_ACT_STAT		VARCHAR2(1)		C:비활성화, "L":적치가능, "N":사용불가
		//	17.	야드적치Bed입출고상태				YD_STK_BED_WHIO_STAT	VARCHAR2(1)		완산여부
		//	18.	야드적치BedX축					YD_STK_BED_XAXIS		NUMBER(7)		Center 지점
		//	19.	야드적치BedY축					YD_STK_BED_YAXIS		NUMBER(5)		Center 지점
		//	20.	야드적치BedZ축					YD_STK_BED_ZAXIS		NUMBER(5)		최하단 바닥 높이
		//	21.	야드적치Bed단Max					YD_STK_BED_LYR_MAX		NUMBER(3)
		//	22.	야드적치Bed중량Max					YD_STK_BED_WT_MAX		NUMBER(7)	
		//	23.	야드적치Bed높이Max					YD_STK_BED_H_MAX		NUMBER(5)		
		//	24.	야드적치Bed길이Max					YD_STK_BED_L_MAX		NUMBER(5)	
		//	25.	야드적치Bed폭Max					YD_STK_BED_W_MAX		NUMBER(5,1)		9999.9(소수점 없는 유효 Data)
		//	26.	야드차량착발상태					YD_CAR_ARRSTRT_STAT		VARCHAR2(1)		"A": 도착,              "S": 출발
		//	27.	야드차량사용구분					YD_CAR_USE_GP			VARCHAR2(1)		"L" : 공차(출하),    "U" : 영차(반입)
		//	28.	야드설비작업상태					YD_EQP_WRK_STAT			VARCHAR2(1)		"L" :구내운송차량,  "G": 제품출하차량
		//	29.	차량번호							CAR_NO					VARCHAR2(15)	구내운송차량
		//	30.	운송장비코드						TRN_EQP_CD				VARCHAR2(8)		제품출하차량
		//	31.	카드번호							CARD_NO					VARCHAR2(4)		제품출하차량
		//	32.	야드차량목표야드구분				YD_CAR_AIM_YD_GP		VARCHAR2(1)		차량하차 야드


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
		String szMethodName 		 = "makeY5L001";
		String szMsg 				 = "";
		String szOperationName       = "C열연코일L2 저장위치제원";
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
		int nTcLen 					= 88;

		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeY5L001() IN==========================\n", YdConstant.DEBUG);	
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
			// [5] com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedYdStkColGpBed
			//=======================================================================================================================
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedYdStkColGpBed2*/
			intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsResult, 302);
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
				outRec.setField("MSG_ID" , "YDY5L001");
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
			
				// 야드적치단번호 
				outRec.setField("YD_STK_LYR_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_LYR_NO"), 1, 1));
				
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
				// [7]  com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschDaoTrnEqpCd
				// [11] com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarNoCardNo_PIDEV
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
					if(szYD_CAR_PROG_STAT.equals("1") || szYD_CAR_PROG_STAT.equals("A")|| szYD_CAR_PROG_STAT.equals("5")|| szYD_CAR_PROG_STAT.equals("E")|| szYD_CAR_PROG_STAT.equals("S")){
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
//				150924 hun 차량번호 없을때 넘오언 param으로 세팅
				if("".equals(ydDaoUtils.paraRecChkNull(recGetVal, "CAR_NO")) && "".equals(ydDaoUtils.paraRecChkNull(recGetVal, "TRN_EQP_CD")) ){
					outRec.setField("CAR_NO", YdUtils.fillSpZr_KOR(ydDaoUtils.paraRecChkNull(inRec, "CAR_NO"), 15, 1));
				}else{
					outRec.setField("CAR_NO", YdUtils.fillSpZr_KOR(ydDaoUtils.paraRecChkNull(recGetVal, "CAR_NO"), 15, 1));	
				}
				// 운송장비코드 [운송장비코드]
				outRec.setField("TRN_EQP_CD", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "TRN_EQP_CD"), 8, 1));		

				// 카드번호 [카드번호]
				outRec.setField("CARD_NO", YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "CARD_NO"), 4, 1));		

				// 야드차량목표야드구분 [야드차량목표야드구분]
				outRec.setField("YD_CAR_AIM_YD_GP", YdUtils.fillSpZr(szYD_CAR_AIM_YD_GP, 1, 1));					
				
				// RecordSet에 추가	
				outRecSet.addRecord(outRec);
				
				// Debug MSG
				ydUtils.putLog(szClassName, szMethodName, "\n======================makeY5L001() OUT("+ Integer.toString(nIdx) + ")==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
			} // end of for
		}catch(Exception e){
			szMsg = "Y5(C열연코일야드L2) 송신  저장위치제원  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}

		return outRecSet.size();
	} // end of makeY5L001()


	
	
	
	
	
	/**
	 * YDY5L002 : 저장품제원
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeY5L002(JDTORecord inRec, JDTORecordSet outRecSet){
//		01  MSG_ID              		    전문 ID                   	CHAR          (8 )     
//		02	DATE                          생성일                            		CHAR          (10)     
//		03	TIME                          생성시간                          		CHAR          (8 )     
//		04	MSG_GP                        전문구분                          		CHAR          (1 )     
//		05	MSG_LEN                       전문길이                          		NUMBER        (4 )     
//		06	TEMP                          임시                              		CHAR          (29)     
//		07	YD_INFO_SYNC_CD        		    야드정보동기화코드                	CHAR          (1 )     
//		08	YD_STL_INFO_SND_SH            야드재료정보송신매수              	NUMBER        (3 )     
//		09	YD_STL_INFO_SND_CNT           야드재료정보송신순번              	NUMBER        (3 )     
//		10	STL_APPEAR_GP                 재료외형구분                      		CHAR          (1 )     
//		11	STL_NO                        재료번호                          		CHAR          (11)     
//		12	YD_STR_LOC                    야드저장위치                      		CHAR          (8 )     
//		13	YD_STK_LYR_NO                 야드적치단번호                    		NUMBER        (3 )     
//		14	YD_STL_WT                     야드재료중량                      		NUMBER        (5 )     
//		15	YD_STL_T                      야드재료두께                      		NUMBER        (6 ) 6,3 
//		16	YD_STL_W                      야드재료폭                        		NUMBER        (5 ) 5,1 
//		17	YD_STL_L                      야드재료길이                      		NUMBER        (7 )     
//		18	MAT_ODIA                      재료외경                          		NUMBER        (5 )     
//		19	MAT_IDIA                      재료내경                          		NUMBER        (5 ) 5.1 
//		20	STLKIND_CD                    강종                              		CHAR          (3 )     
//		21	SPEC_ABBSYM                   규격약호                          		CHAR          (15)     
//		22	YD_IPGO_DD                    야드입고일자                      		CHAR          (14)     
//		23	PLNT_PROC_CD                  공장공정코드                      		CHAR          (3 )     
//		24	CURR_PROG_CD                  현재진도코드                      		CHAR          (1 )     
//		25	ORD_YEOJAE_GP                 주문여재구분                      		CHAR          (1 )     
//		26	ORD_NO                        주문번호                          		CHAR          (10)     
//		27	ORD_DTL                       주문행번                          		CHAR          (3 )     
//		28	BUY_SLAB_NO                   구입SLAB번호                      		CHAR          (30)     
//		29	SLAB_WO_RT_CD                 SLAB지시행선코드                  	CHAR          (2 )     
//		30	ORD_HCR_GP                    설계HCR구분                       		CHAR          (1 )     
//		31	HCR_GP                        HCR구분                           		CHAR          (1 )     
//		32	CC_MC_CD                      연주Machine코드                   	CHAR          (1 )     
//		33	SCARFING_YN                   SCARFING여부                      	CHAR          (1 )     
//		34	SCARFING_DONE_YN              SCARFING완료유무                  	CHAR          (1 )     
//		35	RPR_MTD                       주편손질방법                      		CHAR          (1 )     
//		36	SCARFING_DEPTH                SCARFING깊이                      	CHAR          (2 )     
//		37	REHEAT_SLAB_GP                재열재구분                        		CHAR          (1 )     
//		38	PTOP_PLNT_GP                  조업공장구분                     		CHAR          (2 )     
//		39	REFUR_CHG_LOT_NO              가열로장입Lot번호                 	CHAR          (10)     
//		40	CT_LOT_SCH_SERNO              생산통제Lot스케줄일련번호         	CHAR          (22)     
//		41	FRTOMOVE_ORD_DATE             이송지시일자                      		CHAR          (8 )     
//		42	FRTOMOVE_PLANT_GP             이송공장구분                      		CHAR          (2 )     
//		43	URGENT_FRTOMOVE_WORD_GP       긴급이송작업지시구분              	CHAR          (1 )     
//		44	HYSCO_TRANS_CLS               HYSCO운송구분                     	CHAR          (1 )     
//		45	APPEAR_GRADE                  외관종합판정등급                  	CHAR          (1 )     
//		46	COOL_METHOD                   권취코일냉각방법                  	CHAR          (1 )     
//		47	COOL_DONE_GP                  냉각완료구분                      		CHAR          (1 )     
//		48	CONV_BRANCH_CD                야드Conveyor분기코드              	CHAR          (2 )        
//		49	CUST_KO_NAME                  고객명	               		CHAR          (40)             
//		50	DEST_CD                       목적지코드                        		CHAR          (5 )     
//		51	DLVRDD_RULE_DD                납기기준일                        		CHAR          (8 )     
//		52	ITEMNAME_CD                   품명코드                          		CHAR          (3 )     
//		53	OVERALL_STATAMP_GRADE         종합판정등급                      		CHAR          (1 )     
//		54	ORD_GP                        수주구분                          		CHAR          (1 )     
//		55	YD_STK_LOT_TP                 야드산적LotType            	CHAR          (2 )     
//		56	YD_STK_LOT_CD                 야드산적Lot코드                   	CHAR          (18)	
//      57  YD_PLAN_PROC  				    계획공정                                       CHAR          (10)  
//      58  YD_PASS_PROC  				    통과공정                                       CHAR          (10)  
//      59  YD_NEXT_PROC  				    다음공정                                       CHAR          (2 )  
//      60  HRMILL_CMPL_DT  			    압연냉각일시                                 CHAR          (14)  

		
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
		String szMethodName     = "makeY5L002";
		String szMsg            = "";
		String szOperationName  = "C열연코일L2 저장품제원";				
		String szConv           = "";
		String szYD_GP			= "";
		
		String szSTLKIND_CD     = "";
		String szSPEC_ABBSYM    = "";
		String szMILL_PLNT_GP   = "";
		String szSCARFING_DEPTH = "";
		String szYD_INFO_SYNC_CD = "";
		String szDEL_YN_CHECK = "";
		
		// 리턴값
		int intRtnVal           = 0;
		
		// TC Length = 309 (60 + 249)
		int nTcLen              = 319;
		
		String szSTLNO          = "";
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeY5L002() IN==========================\n", YdConstant.DEBUG);	
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
			szSTLNO           = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");
			szYD_INFO_SYNC_CD = ydDaoUtils.paraRecChkNull(inRec, "YD_INFO_SYNC_CD");
			szDEL_YN_CHECK    = ydDaoUtils.paraRecChkNull(inRec, "DEL_YN_CHECK");
			
			// 재료정보 유무에 따른 조회
			if(szYD_INFO_SYNC_CD.equals("1") || szYD_INFO_SYNC_CD.equals("2") || szYD_INFO_SYNC_CD.equals("3") || szYD_INFO_SYNC_CD.equals("4")){
				recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP"));
				recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO"));
				/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedNotStlCoil_PIDEV*/
//				intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 120);
				intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 400);
						
			} else if(szYD_INFO_SYNC_CD.equals("5") || szYD_INFO_SYNC_CD.equals("A") || szYD_INFO_SYNC_CD.equals("B") || szYD_INFO_SYNC_CD.equals("C")){
				recPara.setField("STL_NO"       , szSTLNO);

				if(szDEL_YN_CHECK.equals("N")){
					/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedStlDelYNNoCheckCoil_PIDEV*/
//					intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 180);
					intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 401);
								
				}else {
					
					/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedStlCoil_PIDEV*/
//					intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 26);
					intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 402);
					
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
				
				szYD_GP =ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_COL_GP");
				
				if(szYD_GP.equals("")){
					szYD_GP ="";
				}else {
					szYD_GP=szYD_GP.substring(0, 1);
				}
				
				//C열연 야드가 안닌경우 제외처리 
				if(szYD_GP.equals("H") ||szYD_GP.equals("J")||szYD_GP.equals("X")||szYD_GP.equals("") ){
	
				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();									
				outRec.setField("MSG_ID" , "YDY5L002");
				outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
				outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
				outRec.setField("MSG_GP" , "I");
				outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
				outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

				// 7.야드정보동기화코드 [야드정보동기화코드]
				outRec.setField("YD_INFO_SYNC_CD"		, YdUtils.fillSpZr(szYD_INFO_SYNC_CD, 1, 1));
								
				// 8.야드재료정보송신매수 [야드재료정보송신매수]
				outRec.setField("YD_STL_INFO_SND_SH"	, YdUtils.fillSpZr(Integer.toString(intRtnVal), 3, 1));	
				
				// 9.야드재료정보송신순번 [야드재료정보송신순번]
				outRec.setField("YD_STL_INFO_SND_CNT"	, YdUtils.fillSpZr(Integer.toString(nIdx+1), 3, 1));	
				
				// 10.재료외형구분 [재료외형구분]
				outRec.setField("STL_APPEAR_GP"			, YdUtils.fillSpZr(recGetVal.getFieldString("STL_APPEAR_GP"), 1, 1));	
				
				// 11.재료번호 [재료번호]
				outRec.setField("STL_NO"				, YdUtils.fillSpZr(recGetVal.getFieldString("STL_NO"), 11, 1));	
				
				// 12.야드저장위치 [야드저장위치 : BED까지]  
				outRec.setField("YD_STR_LOC"			, YdUtils.fillSpZr(recGetVal.getFieldString("YD_STK_COL_GP")+recGetVal.getFieldString("YD_STK_BED_NO"), 8, 1));	
			
				// 13.야드적치단번호 [야드적치단번호]
				outRec.setField("YD_STK_LYR_NO"			, YdUtils.fillSpZr(recGetVal.getFieldString("YD_STK_LYR_NO"), 3, 1));	
				
				// 14.야드재료중량 [야드재료중량]
				outRec.setField("YD_STL_WT"				, YdUtils.fillSpZr(recGetVal.getFieldString("YD_STL_WT"), 5, 1));	
				
				// 15.야드재료두께 [야드재료두께]
				szConv = YdUtils.fillSpZr(recGetVal.getFieldString("YD_STL_T"), 7, 1);
				outRec.setField("YD_STL_T"				, ydUtils.FloatLRPAD(szConv, 6, 3, '0'));	
				
				// 16.야드재료폭 [야드재료폭]
				szConv = YdUtils.fillSpZr(recGetVal.getFieldString("YD_STL_W"), 6, 1);
				outRec.setField("YD_STL_W"				, ydUtils.FloatLRPAD(szConv, 5, 1, '0'));	
					
				// 17.야드재료길이 [야드재료길이]
				outRec.setField("YD_STL_L"				, YdUtils.fillSpZr(recGetVal.getFieldString("YD_STL_L"), 7, 1));	
				
				// 18.재료외경
				outRec.setField("MAT_ODIA"				, YdUtils.fillSpZr(recGetVal.getFieldString("MAT_ODIA"), 5, 1));	
				
				// 19.재료내경
				szConv = YdUtils.fillSpZr(recGetVal.getFieldString("MAT_IDIA"), 6, 1);
				outRec.setField("MAT_IDIA"				, ydUtils.FloatLRPAD(szConv, 5, 1, '0'));	
										
				// 20.강종 [강종]
				szSTLKIND_CD   = YdUtils.fillSpZr(recGetVal.getFieldString("STLKIND_CD"), 3, 1);
				outRec.setField("STLKIND_CD"			, YdUtils.fillSpZr(szSTLKIND_CD, 3, 1));

				// 21.규격약호 [규격약호]
				szSPEC_ABBSYM  = YdUtils.fillSpZr(recGetVal.getFieldString("SPEC_ABBSYM"), 15, 1);
				outRec.setField("SPEC_ABBSYM"			, YdUtils.fillSpZr(szSPEC_ABBSYM, 15, 1));	

				// 22.야드입고일자 [등록일자]  
				outRec.setField("YD_IPGO_DD"			, YdUtils.fillSpZr(recGetVal.getFieldString("REG_DDTT"), 14, 1));

				// 23.공장공정코드 [공장공정코드]
				outRec.setField("PLNT_PROC_CD"			, YdUtils.fillSpZr(recGetVal.getFieldString("PLNT_PROC_CD"), 3, 1));

				// 24.현재진도코드 [재료진도코드]
				outRec.setField("CURR_PROG_CD"			, YdUtils.fillSpZr(recGetVal.getFieldString("CURR_PROG_CD"), 1, 1));

				// 25.주문여재구분 [주문여재구분]
				outRec.setField("ORD_YEOJAE_GP"			, YdUtils.fillSpZr(recGetVal.getFieldString("ORD_YEOJAE_GP"), 1, 1));

				// 26.주문번호 [주문번호]
				outRec.setField("ORD_NO"				, YdUtils.fillSpZr(recGetVal.getFieldString("ORD_NO"), 10, 1));

				// 27.주문행번 [주문행번]
				outRec.setField("ORD_DTL"				, YdUtils.fillSpZr(recGetVal.getFieldString("ORD_DTL"), 3, 1));

				// 28.구입SLAB번호 [구입SLAB번호]
				outRec.setField("BUY_SLAB_NO"			, YdUtils.fillSpZr(recGetVal.getFieldString("BUY_SLAB_NO"), 30, 1));

				// 29.SLAB지시행선코드 [SLAB지시행선코드]
				outRec.setField("SLAB_WO_RT_CD"			, YdUtils.fillSpZr(recGetVal.getFieldString("SLAB_WO_RT_CD"), 2, 1));

				// 30.설계HCR구분 [설계HCR구분]
				outRec.setField("ORD_HCR_GP"			, YdUtils.fillSpZr(recGetVal.getFieldString("ORD_HCR_GP"), 1, 1));

				// 31.HCR구분 [HCR구분]
				outRec.setField("HCR_GP"				, YdUtils.fillSpZr(recGetVal.getFieldString("HCR_GP"), 1, 1));

				// 32.연주Machine코드 [야드CCM구분]
				outRec.setField("CC_MC_CD"				, YdUtils.fillSpZr(recGetVal.getFieldString("CC_MC_CD"), 1, 1));

				// 33.SCARFING여부 [SCARFING여부]
				outRec.setField("SCARFING_YN"			, YdUtils.fillSpZr(recGetVal.getFieldString("SCARFING_YN"), 1, 1));

				// 34.SCARFING완료유무 [SCARFING완료유무]
				outRec.setField("SCARFING_DONE_YN"		, YdUtils.fillSpZr(recGetVal.getFieldString("SCARFING_DONE_YN"), 1, 1));

				// 35.주편손질방법
				outRec.setField("RPR_MTD"				, YdUtils.fillSpZr(recGetVal.getFieldString("RPR_MTD"), 1, 1));
					
				// 36.SCARFING깊이 [SCARFING깊이]
				// 후판제품과 코일은 SCARFING_DEPTH가 없음
				szSCARFING_DEPTH = YdUtils.fillSpZr(" ", 2, 1);		        		
				outRec.setField("SCARFING_DEPTH"		, YdUtils.fillSpZr(szSCARFING_DEPTH, 2, 1));
				
				// 37.재열재구분 [재열재구분]
				outRec.setField("REHEAT_SLAB_GP"		, YdUtils.fillSpZr(recGetVal.getFieldString("REHEAT_SLAB_GP"), 1, 1));

				// 38.압연공장구분 [조업공장구분]
				szMILL_PLNT_GP = YdUtils.fillSpZr(recGetVal.getFieldString("PTOP_PLNT_GP"), 2, 1);				
				outRec.setField("PTOP_PLNT_GP"			, YdUtils.fillSpZr(szMILL_PLNT_GP, 2, 1));  
				
				// 39.가열로장입Lot번호 [가열로장입Lot번호]
				outRec.setField("REFUR_CHG_LOT_NO"		, YdUtils.fillSpZr(recGetVal.getFieldString("REFUR_CHG_LOT_NO"), 10, 1));

				// 40.생산통제Lot스케줄일련번호 [생산통제Lot스케줄일련번호]
				outRec.setField("CT_LOT_SCH_SERNO"		, YdUtils.fillSpZr(recGetVal.getFieldString("CT_LOT_SCH_SERNO"), 22, 1));

				// 41.이송지시일자 [이송지시일자]
				outRec.setField("FRTOMOVE_ORD_DATE"		, YdUtils.fillSpZr(recGetVal.getFieldString("FRTOMOVE_ORD_DATE"), 8, 1));

				// 42.이송공장구분 [이송공장구분]
				outRec.setField("FRTOMOVE_PLANT_GP"		, YdUtils.fillSpZr(recGetVal.getFieldString("FRTOMOVE_PLANT_GP"), 2, 1));

				// 43.긴급이송작업지시구분 [긴급이송작업지시구분]
				outRec.setField("URGENT_FRTOMOVE_WORD_GP", YdUtils.fillSpZr(recGetVal.getFieldString("URGENT_FRTOMOVE_WORD_GP"), 1, 1));

				// 44.HYSCO운송구분 [HYSCO운송구분]
				outRec.setField("HYSCO_TRANS_CLS"		, YdUtils.fillSpZr(recGetVal.getFieldString("HYSCO_TRANS_CLS"), 1, 1));

				// 45.외관종합판정등급 [외관종합판정등급]
				outRec.setField("APPEAR_GRADE"			, YdUtils.fillSpZr(recGetVal.getFieldString("APPEAR_GRADE"), 1, 1));

				// 46.권취코일냉각방법 [권취코일냉각방법]
				outRec.setField("COOL_METHOD"			, YdUtils.fillSpZr(recGetVal.getFieldString("COOL_METHOD"), 1, 1));

				// 47.냉각완료구분 [냉각완료구분]
				outRec.setField("COOL_DONE_GP"			, YdUtils.fillSpZr(recGetVal.getFieldString("COOL_DONE_GP"), 1, 1));

				// 48.야드Conveyor분기코드
				outRec.setField("CONV_BRANCH_CD"		, YdUtils.fillSpZr(recGetVal.getFieldString("CONV_BRANCH_CD"), 2, 1));				
				
				// 49.고객명
				outRec.setField("CUST_KO_NAME"			, YdUtils.fillSpZr_KOR(recGetVal.getFieldString("CUST_KO_NAME"), 40, 1));
	
				// 50.목적지코드 [목적지코드]
				outRec.setField("DEST_CD"				, YdUtils.fillSpZr(recGetVal.getFieldString("DEST_CD"), 5, 1));

				// 51.납기기준일 [납기기준일]
				outRec.setField("DLVRDD_RULE_DD"		, YdUtils.fillSpZr(recGetVal.getFieldString("DLVRDD_RULE_DD"), 8, 1));

				// 52.품명코드 [품명코드]
				outRec.setField("ITEMNAME_CD"			, YdUtils.fillSpZr(recGetVal.getFieldString("ITEMNAME_CD"), 3, 1));

				// 53.종합판정등급 [종합판정등급]
				outRec.setField("OVERALL_STAMP_GRADE"	, YdUtils.fillSpZr(recGetVal.getFieldString("OVERALL_STAMP_GRADE"), 1, 1));

				// 54.수주구분 [수주구분]
				outRec.setField("ORD_GP"				, YdUtils.fillSpZr(recGetVal.getFieldString("ORD_GP"), 1, 1));

				// 55.야드산적LotType [야드산적LotType] 
				outRec.setField("YD_STK_LOT_TP"			, YdUtils.fillSpZr(recGetVal.getFieldString("YD_STK_LOT_TP"), 2, 1));

				// 56.야드산적Lot코드 [야드산적Lot코드]  
				outRec.setField("YD_STK_LOT_CD"			, YdUtils.fillSpZr(recGetVal.getFieldString("YD_STK_LOT_CD"), 18, 1));

				
				// 57.계획공정
				outRec.setField("YD_PLAN_PROC"			, YdUtils.fillSpZr(recGetVal.getFieldString("YD_PLAN_PROC"), 10, 1));
				// 58.통과공정  
				outRec.setField("YD_PASS_PROC"			, YdUtils.fillSpZr(recGetVal.getFieldString("YD_PASS_PROC"), 10, 1));
				// 59.다음공정
				outRec.setField("YD_NEXT_PROC"			, YdUtils.fillSpZr(recGetVal.getFieldString("YD_NEXT_PROC"), 2, 1));
				// 60.열연압연완료일시 
				outRec.setField("HRMILL_CMPL_DT"		, YdUtils.fillSpZr(recGetVal.getFieldString("HRMILL_CMPL_DT"), 14, 1));
				
				// RecordSet에 추가	
				outRecSet.addRecord(outRec);				
				
				// Debug MSG
				ydUtils.putLog(szClassName, szMethodName, "\n======================makeY5L002() OUT("+ Integer.toString(nIdx) + ")==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
				}
			}
		}catch(Exception e){
			szMsg = "Y5(C열연코일야드L2) 송신  저장품제원  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}

		return outRecSet.size();

	} // end of makeY5L002()

	
	
	
	
	
	
	/**
	 * YDY5L004 : 크레인작업지시
	 * @param JDTORecord inRec
	 * @return JDTORecordSet outRecSet
	 */
	public static int makeY5L004(JDTORecord inRec, JDTORecordSet outRecSet){
		//	1.	전문 ID								MSG_ID					VARCHAR2(8)			YDY5L004
		//	2.	생성일								DATE					VARCHAR2(10)		YYYY-MM-DD
		//	3.	생성시간								TIME					VARCHAR2(8)			24HH-MM-SS
		//	4.	전문구분								MSG_GP					VARCHAR2(1)			I(신규), U(수정), D(취소,삭제), R(재 전송)
		//	5.	전문길이								MSG_LEN					NUMBER(4)		
		//	6.	임시									TEMP					VARCHAR2(29)	
		
		//	7.	야드설비ID							YD_EQP_ID				VARCHAR2(6)		크레인설비 ID
		//	8.	야드작업진행상태						YD_WRK_PROG_STAT		VARCHAR2(1)		"W" 작업지시대기, "1" 권상작업, "3" 권하작업
		//	9.	야드스케쥴코드							YD_SCH_CD				VARCHAR2(8)	
		//	10.	야드스케쥴명						    YD_SCH_NAME				VARCHAR2(30)	
		//	11.	야드크레인스케쥴ID						YD_CRN_SCH_ID			VARCHAR2(18)	
		//*	12.	야드크레인스케줄잔여회수					YD_CRN_SCH_RMD_CNT		NUMBER(2)		권상모음, 권하분리작업 시 크레인 Handling 잔여 회수
		//	13.	야드권상지시위치						YD_UP_WO_LOC			VARCHAR2(8)	
		//	14.	야드권상지시단							YD_UP_WO_LAYER			VARCHAR2(3)	
		//	15.	야드권상지시X축						YD_UP_WO_LOC_XAXIS		NUMBER(7)	
		//	16.	야드권상지시X축오차최대					YD_UP_WO_XAXIS_GAP_MAX	NUMBER(5)	
		//	17.	야드권상지시X축오차최소					YD_UP_WO_XAXIS_GAP_MIN	NUMBER(5)	
		//	18.	야드권상지시Y축						YD_UP_WO_LOC_YAXIS		NUMBER(5)	
		//	19.	야드권상지시Y축오차최대					YD_UP_WO_YAXIS_GAP_MAX	NUMBER(5)	
		//	20.	야드권상지시Y축오차최소					YD_UP_WO_YAXIS_GAP_MIN	NUMBER(5)	
		//	21.	야드권상지시Z축						YD_UP_WO_LOC_ZAXIS		NUMBER(5)	
		//	22.	야드권상지시Z축오차최대					YD_UP_WO_ZAXIS_GAP_MAX	NUMBER(5)	
		//	23.	야드권상지시Z축오차최소					YD_UP_WO_ZAXIS_GAP_MIN	NUMBER(5)	
		//	24.	야드권하지시위치						YD_DN_WO_LOC			VARCHAR2(8)				
		//	25.	야드권하지시단							YD_DN_WO_LAYER			VARCHAR2(3)	
		//	26.	야드권하지시X축						YD_DN_WO_LOC_XAXIS		NUMBER(7)	
		//	27.	야드권하지시X축오차최대					YD_DN_WO_XAXIS_GAP_MAX	NUMBER(5)	
		//	28.	야드권하지시X축오차최소					YD_DN_WO_XAXIS_GAP_MIN	NUMBER(5)	
		//	29.	야드권하지시Y축						YD_DN_WO_LOC_YAXIS		NUMBER(5)	
		//	30.	야드권하지시Y축오차최대					YD_DN_WO_YAXIS_GAP_MAX	NUMBER(5)	
		//	31.	야드권하지시Y축오차최소					YD_DN_WO_YAXIS_GAP_MIN	NUMBER(5)	
		//	32.	야드권하지시Z축						YD_DN_WO_LOC_ZAXIS		NUMBER(5)	
		//	33.	야드권하지시Z축오차최대					YD_DN_WO_ZAXIS_GAP_MAX	NUMBER(5)	
		//	34.	야드권하지시Z축오차최소					YD_DN_WO_ZAXIS_GAP_MIN	NUMBER(5)	
		//*	35.	야드설비ID2							YD_EQP_ID2				VARCHAR2(6)		권상 또는 권하위치가 대차 및 차량인경우
		//*	36.	야드대차목적동							YD_TC_AIM_BAY_GP		VARCHAR2(1)	
		//*	37.	야드차량사용구분						YD_CAR_USE_GP			VARCHAR2(1)		권상 또는 권하위치가 차량인 경우("L" 구내운송차량, "G" 제품출하차량)
		//*	38.	차량번호								CAR_NO					VARCHAR2(15)	권상 또는 권하위치가 제품출하차량인 경우
		//*	39.	운송장비코드							TRN_EQP_CD				VARCHAR2(8)		권상 또는 권하위치가 구내운송차량인 경우
		//*	40.	야드설비작업매수						YD_EQP_WRK_SH			NUMBER(2)		대차, 차량스케줄의 설비작업매수
		//*	41.	야드설비잔량매수						YD_EQP_RMN_SH			NUMBER(2)		대차, 차량스케줄의 설비작업매수 - 크레인 작업이 미완료된 매수 
		//*	42.	재료번호								STL_NO					VARCHAR2(11)
		//*	43.	야드재료중량							YD_STL_WT				NUMBER(5)
		//*	44.	야드재료두께							YD_STL_T				NUMBER(6,3)
		//*	45.	야드재료폭							YD_STL_W				NUMBER(5,1)
		//*	46.	야드재료길이							YD_STL_L				NUMBER(7)
		//	47.	Coil외경								COIL_OUTDIA				NUMBER(5,0)	
		//	48.	Coil내경								COIL_INDIA				NUMBER(5,1)		
		//	49.	야드스케쥴코드_Next					YD_SCH_CD_NEXT			VARCHAR2(8)		크레인스케줄에 등록된 다음 작업
        //	50.	야드스케쥴명_Next						YD_SCH_NAME_NEXT		VARCHAR2(30)	크레인스케줄에 등록된 다음 작업
		//	51.	야드권상지시위치_Next					YD_UP_WO_LOC_NEXT		VARCHAR2(8)	
		//	52.	야드권상지시단_Next					YD_UP_WO_LAYER_NEXT		VARCHAR2(3)	
		//	53.	야드권하지시위치_Next					YD_DN_WO_LOC_NEXT		VARCHAR2(8)	
		//	54.	야드권하지시단_Next					YD_DN_WO_LAYER_NEXT		VARCHAR2(3)	
		//	55.	재료번호_Next							STL_NO_NEXT				VARCHAR2(11)	
		//	56.	야드크레인작업중량_Next					YD_CRN_WRK_WT_NEXT		NUMBER(7)	
		//	57.	야드크레인작업총두께_Next				YD_CRN_WRK_T_NEXT		NUMBER(6,3)	
		//	58.	야드크레인작업최대폭_Next				YD_CRN_WRK_MAX_W_NEXT	NUMBER(5,1)	
		//	59.	야드크레인작업코일외경_Next				COIL_OUTDIA_NEXT		NUMBER(5,0)	
		//	60.	야드크레인작업코일내경_Next				COIL_INDIA_NEXT			NUMBER(5,1)	
			
		
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
		YdEqpDao     ydEqpDao     = new YdEqpDao();
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		// 변수선언
		String szMethodName       = "makeY5L004";
		String szMsg              = "";
		String szOperationName    = "C열연코일L2 크레인작업지시";
		String szYD_SCH_NAME      = "";
		String szYD_SCH_NAME_NEXT = "";
		String szYD_EQP_ID2       = "";
		String szCrnSchID         = "";
		String szYD_SCH_CD        = "";
		String szYD_WRK_PROG_STAT = "";
		String szMSG_GP           = "";
		String szTemp             = "";
		String szProgStatTemp     = "";
		String szYD_SCH_CD_NEXT   = "";
		String szYD_L2_REQUEST_STAT = "";
		
		String sQueryId = "";
	 		
		// 문자열변환 임시변수
		String szConv             = "";
				
		// 리턴값
		int intRtnVal			  = 0;
		int intRtnVal1            = 0;
		int intRtnVal2            = 0;
		
		// TC Length = 743 (60 + 683)
//		int nTcLen                = 359;
		int nTcLen                = 361;
			
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeY5L004() IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
			
			szCrnSchID = ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID");
			szYD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(inRec, "YD_WRK_PROG_STAT");
			szMSG_GP = ydDaoUtils.paraRecChkNull(inRec, "MSG_GP");
			
			
			
			//=======================================================================================================================
			// [1] 크레인스케쥴+크레인작업재료 테이블 조회 (Key: 크레인스케쥴 ID) 
			//
			// [13] com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnWrkMtlWrkBookId 
			//=======================================================================================================================
			recPara1 = JDTORecordFactory.getInstance().create();			
			rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
			recPara1.setField("YD_CRN_SCH_ID", szCrnSchID);
			recPara1.setField("YD_WRK_PROG_STAT", szYD_WRK_PROG_STAT);
			ydUtils.putLog(szClassName, szMethodName, "szYD_WRK_PROG_STAT ="+szYD_WRK_PROG_STAT, YdConstant.DEBUG);
			szProgStatTemp = szYD_WRK_PROG_STAT;
			
			
			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnWrkMtlNextWrkBookId_NEXT2*/
			intRtnVal1 = ydCrnSchDao.getYdCrnsch(recPara1, rsResult1, 303);
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
			
			
			szMsg = "makeY5L004() in AutoCrn check  ydEqpDao.chkAutoCrn(recGetVal1.getFieldString(YD_EQP_ID))(" + ydEqpDao.chkAutoCrn(recGetVal1.getFieldString("YD_EQP_ID")) + ") ";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
			
			// 150810 hun 크레인무인화 AutoCrn 인 경우 작업지시 대기상태 변경후 응답대기
			if(ydEqpDao.chkAutoCrn(recGetVal1.getFieldString("YD_EQP_ID"))){
				
				szMsg = "makeY5L004() in AutoCrn check = true 경우 ";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);		
				
				
				String szYD_WRK_PROG_STAT_S ="";

				
				
				
				if("S".equals( recGetVal1.getFieldString("YD_WRK_PROG_STAT") )){
					if("D".equals( recGetVal1.getFieldString("YD_L2_REQUEST_STAT") ) ){
						
						inRec.setField("MSG_GP", "D")	;
					}else if("5".equals( recGetVal1.getFieldString("YD_L2_REQUEST_STAT") ) ){
						
						inRec.setField("MSG_GP", "U")	;
					}else{
						
						inRec.setField("MSG_GP", "R")	;
					}
				}else{
					
					szYD_WRK_PROG_STAT_S    = "S";
					
					if("D".equals(szMSG_GP )){
						szYD_L2_REQUEST_STAT = "D";
						
					}else{
						
						szYD_L2_REQUEST_STAT = szYD_WRK_PROG_STAT;
					}
						sQueryId = "com.inisteel.cim.yd.dao.ydeqpdao.YdCrnSchDao.updYdCrnSchProgStat";
						intRtnVal = dao.updateData(sQueryId,new Object[]{szYD_WRK_PROG_STAT_S, szYD_L2_REQUEST_STAT, szCrnSchID });
					
				}
				
				
				
				return makeY5L004Auto(inRec, outRecSet);
			}
			
			// 헤더부
			outRec.setField("MSG_ID" , "YDY5L004");
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
			
			// 150713 hun 크레인 무인화 자동크레인일 경우 "5" 세팅
			if("5".equals(szProgStatTemp)){
				outRec.setField("YD_WRK_PROG_STAT", YdUtils.fillSpZr(szProgStatTemp, 1, 1));
			}
			
			// 야드스케쥴코드 [야드스케쥴코드]
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recGetVal1, "YD_SCH_CD");
			outRec.setField("YD_SCH_CD", YdUtils.fillSpZr(szYD_SCH_CD, 8, 1));
			
			//=======================================================================================================================
			// [2] 스케쥴 기준 테이블 조회
			//
			// [0] com.inisteel.cim.yd.dao.ydschruledao.YdSchruleDao.getYdSchrule
			//=======================================================================================================================
			if(!szYD_SCH_CD.trim().equals("")){
				recPara2 = JDTORecordFactory.getInstance().create();			
				rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
				recPara2.setField("YD_SCH_CD", szYD_SCH_CD);
				intRtnVal2 = ydSchRuleDao.getYdSchrule(recPara2, rsResult2, 0);
				if(intRtnVal2 < 0) {
					szMsg = "스케쥴 기준 테이블 조회오류 (" + szYD_SCH_CD + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
	
					szYD_SCH_NAME = YdUtils.fillSpZr_KOR(" ", 30, 1);	
					szYD_EQP_ID2 = YdUtils.fillSpZr(" ", 6, 1);			
				} else if(intRtnVal2 == 0) {
					szMsg = "스케쥴 기준 테이블 조회건수 없음 (" + szYD_SCH_CD + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
					
					szYD_SCH_NAME = YdUtils.fillSpZr_KOR(" ", 30, 1);	
					szYD_EQP_ID2 = YdUtils.fillSpZr(" ", 6, 1);			
				}else {		
					rsResult2.first();
					recGetVal2 = rsResult2.getRecord();				
	
					szYD_SCH_NAME = YdUtils.fillSpZr_KOR(recGetVal1.getFieldString("CD_CONTENTS"), 30, 1);
					szYD_EQP_ID2 = YdUtils.fillSpZr(recGetVal1.getFieldString("YEOJAE_CAUSE_CD"), 6, 1);
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
			
			//*************************************************************************************************************************************
			// 야드크레인스케줄잔여회수 [야드크레인스케줄잔여회수]               
			outRec.setField("YD_CRN_SCH_RMD_CNT", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_CRN_SCH_RMD_CNT"), 2, 1));
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
			//
			// [2] com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookYdWrkbookMtl_PIDEV
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

			
			// 재료번호 [재료번호]
			outRec.setField("STL_NO1", YdUtils.fillSpZr(recGetVal1.getFieldString("STL_NO"), 11, 1));
			
			// 야드재료중량 [야드재료중량]
			outRec.setField("YD_STL_WT1", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_MTL_WT"), 5, 1));
			
			// 야드재료두께 [야드재료두께]
			szConv = YdUtils.fillSpZr(recGetVal1.getFieldString("YD_MTL_T"), 7, 1);
			outRec.setField("YD_STL_T1", ydUtils.FloatLRPAD(szConv, 6, 3, '0'));	

			// 야드재료폭 [야드재료폭] 
			szConv = YdUtils.fillSpZr(recGetVal1.getFieldString("YD_MTL_W"), 6, 1);
			outRec.setField("YD_STL_W1", ydUtils.FloatLRPAD(szConv, 5, 1, '0'));	
			
			// 야드재료길이 [야드재료길이]
			outRec.setField("YD_STL_L1", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_MTL_L"), 7, 1));				

			// 코일외경 [코일외경]
			String szCOIL_OUTDIA = ydDaoUtils.paraRecChkNull(recGetVal1, "COIL_OUTDIA");
			outRec.setField("COIL_OUTDIA", YdUtils.fillSpZr(szCOIL_OUTDIA, 5, 1));
			
			// 코일내경 [코일내경]
			String szCOIL_INDIA = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal1, "COIL_INDIA"), 6, 1);
			outRec.setField("COIL_INDIA", ydUtils.FloatLRPAD(szCOIL_INDIA, 5, 1, '0'));
			
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
						
			// 야드크레인작업중량_Next [야드크레인작업중량_Next]
			outRec.setField("YD_CRN_WRK_WT_NEXT", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_WRK_WT_NEXT"), 7, 1));
			
			// 야드크레인작업총두께_Next [야드크레인작업총두께_Next]
			szConv = YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_WRK_T_NEXT"), 7, 1);
			outRec.setField("YD_CRN_WRK_T_NEXT", ydUtils.FloatLRPAD(szConv, 6, 3, '0'));					
			
			// 야드크레인작업최대폭_Next [야드크레인작업최대폭_Next]
			szConv = YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_WRK_MAX_W_NEXT"), 6, 1);
			outRec.setField("YD_CRN_WRK_MAX_W_NEXT", ydUtils.FloatLRPAD(szConv, 5, 1, '0'));					
			
			// 야드크레인작업코일외경_Next [야드크레인작업코일외경_Next]
			String szCOIL_OUTDIA_NEXT = ydDaoUtils.paraRecChkNull(recGetVal1, "COIL_OUTDIA_NEXT");
			outRec.setField("COIL_OUTDIA_NEXT", YdUtils.fillSpZr(szCOIL_OUTDIA_NEXT, 5, 1));
			
			// 야드크레인작업코일내경_Next [야드크레인작업코일내경_Next]
			String szCOIL_INDIA_NEXT = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal1, "COIL_INDIA_NEXT"), 6, 1);
			outRec.setField("COIL_INDIA_NEXT", ydUtils.FloatLRPAD(szCOIL_INDIA_NEXT, 5, 1, '0'));					
			
			// 2015.06.24 hun 크레인 무인화작업 권상위치 회전각도
			outRec.setField("UP_ROTATION_ANGLE", YdUtils.fillSpZr(recGetVal1.getFieldString("UP_ROTATION_ANGLE"), 1, 1));
			
			// 2015.06.24 hun 크레인 무인화작업 권하위치 회전각도
			outRec.setField("DOWN_ROTATION_ANGLE", YdUtils.fillSpZr(recGetVal1.getFieldString("DOWN_ROTATION_ANGLE"), 1, 1));
			
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);		
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================makeY5L004() OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
		}catch(Exception e){
			szMsg = "Y5(C열연코일야드L2) 송신  크레인작업지시  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}
			
		return outRecSet.size();
	} // end of makeY5L004()

	
	
	/**
	 * YDY5L004 : 크레인작업지시
	 * @param JDTORecord inRec
	 * @return JDTORecordSet outRecSet
	 */
	public static int makeY5L004Auto(JDTORecord inRec, JDTORecordSet outRecSet){
		//	1.	전문 ID								MSG_ID					VARCHAR2(8)			YDY5L004
		//	2.	생성일								DATE					VARCHAR2(10)		YYYY-MM-DD
		//	3.	생성시간								TIME					VARCHAR2(8)			24HH-MM-SS
		//	4.	전문구분								MSG_GP					VARCHAR2(1)			I(신규), U(수정), D(취소,삭제), R(재 전송)
		//	5.	전문길이								MSG_LEN					NUMBER(4)		
		//	6.	임시									TEMP					VARCHAR2(29)	
		
		//	7.	야드설비ID							YD_EQP_ID				VARCHAR2(6)		크레인설비 ID
		//	8.	야드작업진행상태						YD_WRK_PROG_STAT		VARCHAR2(1)		"W" 작업지시대기, "1" 권상작업, "3" 권하작업
		//	9.	야드스케쥴코드							YD_SCH_CD				VARCHAR2(8)	
		//	10.	야드스케쥴명						    YD_SCH_NAME				VARCHAR2(30)	
		//	11.	야드크레인스케쥴ID						YD_CRN_SCH_ID			VARCHAR2(18)	
		//*	12.	야드크레인스케줄잔여회수					YD_CRN_SCH_RMD_CNT		NUMBER(2)		권상모음, 권하분리작업 시 크레인 Handling 잔여 회수
		//	13.	야드권상지시위치						YD_UP_WO_LOC			VARCHAR2(8)	
		//	14.	야드권상지시단							YD_UP_WO_LAYER			VARCHAR2(3)	
		//	15.	야드권상지시X축						YD_UP_WO_LOC_XAXIS		NUMBER(7)	
		//	16.	야드권상지시X축오차최대					YD_UP_WO_XAXIS_GAP_MAX	NUMBER(5)	
		//	17.	야드권상지시X축오차최소					YD_UP_WO_XAXIS_GAP_MIN	NUMBER(5)	
		//	18.	야드권상지시Y축						YD_UP_WO_LOC_YAXIS		NUMBER(5)	
		//	19.	야드권상지시Y축오차최대					YD_UP_WO_YAXIS_GAP_MAX	NUMBER(5)	
		//	20.	야드권상지시Y축오차최소					YD_UP_WO_YAXIS_GAP_MIN	NUMBER(5)	
		//	21.	야드권상지시Z축						YD_UP_WO_LOC_ZAXIS		NUMBER(5)	
		//	22.	야드권상지시Z축오차최대					YD_UP_WO_ZAXIS_GAP_MAX	NUMBER(5)	
		//	23.	야드권상지시Z축오차최소					YD_UP_WO_ZAXIS_GAP_MIN	NUMBER(5)	
		//	24.	야드권하지시위치						YD_DN_WO_LOC			VARCHAR2(8)				
		//	25.	야드권하지시단							YD_DN_WO_LAYER			VARCHAR2(3)	
		//	26.	야드권하지시X축						YD_DN_WO_LOC_XAXIS		NUMBER(7)	
		//	27.	야드권하지시X축오차최대					YD_DN_WO_XAXIS_GAP_MAX	NUMBER(5)	
		//	28.	야드권하지시X축오차최소					YD_DN_WO_XAXIS_GAP_MIN	NUMBER(5)	
		//	29.	야드권하지시Y축						YD_DN_WO_LOC_YAXIS		NUMBER(5)	
		//	30.	야드권하지시Y축오차최대					YD_DN_WO_YAXIS_GAP_MAX	NUMBER(5)	
		//	31.	야드권하지시Y축오차최소					YD_DN_WO_YAXIS_GAP_MIN	NUMBER(5)	
		//	32.	야드권하지시Z축						YD_DN_WO_LOC_ZAXIS		NUMBER(5)	
		//	33.	야드권하지시Z축오차최대					YD_DN_WO_ZAXIS_GAP_MAX	NUMBER(5)	
		//	34.	야드권하지시Z축오차최소					YD_DN_WO_ZAXIS_GAP_MIN	NUMBER(5)	
		//*	35.	야드설비ID2							YD_EQP_ID2				VARCHAR2(6)		권상 또는 권하위치가 대차 및 차량인경우
		//*	36.	야드대차목적동							YD_TC_AIM_BAY_GP		VARCHAR2(1)	
		//*	37.	야드차량사용구분						YD_CAR_USE_GP			VARCHAR2(1)		권상 또는 권하위치가 차량인 경우("L" 구내운송차량, "G" 제품출하차량)
		//*	38.	차량번호								CAR_NO					VARCHAR2(15)	권상 또는 권하위치가 제품출하차량인 경우
		//*	39.	운송장비코드							TRN_EQP_CD				VARCHAR2(8)		권상 또는 권하위치가 구내운송차량인 경우
		//*	40.	야드설비작업매수						YD_EQP_WRK_SH			NUMBER(2)		대차, 차량스케줄의 설비작업매수
		//*	41.	야드설비잔량매수						YD_EQP_RMN_SH			NUMBER(2)		대차, 차량스케줄의 설비작업매수 - 크레인 작업이 미완료된 매수 
		//*	42.	재료번호								STL_NO					VARCHAR2(11)
		//*	43.	야드재료중량							YD_STL_WT				NUMBER(5)
		//*	44.	야드재료두께							YD_STL_T				NUMBER(6,3)
		//*	45.	야드재료폭							YD_STL_W				NUMBER(5,1)
		//*	46.	야드재료길이							YD_STL_L				NUMBER(7)
		//	47.	Coil외경								COIL_OUTDIA				NUMBER(5,0)	
		//	48.	Coil내경								COIL_INDIA				NUMBER(5,1)		
		//	49.	야드스케쥴코드_Next					YD_SCH_CD_NEXT			VARCHAR2(8)		크레인스케줄에 등록된 다음 작업
        //	50.	야드스케쥴명_Next						YD_SCH_NAME_NEXT		VARCHAR2(30)	크레인스케줄에 등록된 다음 작업
		//	51.	야드권상지시위치_Next					YD_UP_WO_LOC_NEXT		VARCHAR2(8)	
		//	52.	야드권상지시단_Next					YD_UP_WO_LAYER_NEXT		VARCHAR2(3)	
		//	53.	야드권하지시위치_Next					YD_DN_WO_LOC_NEXT		VARCHAR2(8)	
		//	54.	야드권하지시단_Next					YD_DN_WO_LAYER_NEXT		VARCHAR2(3)	
		//	55.	재료번호_Next							STL_NO_NEXT				VARCHAR2(11)	
		//	56.	야드크레인작업중량_Next					YD_CRN_WRK_WT_NEXT		NUMBER(7)	
		//	57.	야드크레인작업총두께_Next				YD_CRN_WRK_T_NEXT		NUMBER(6,3)	
		//	58.	야드크레인작업최대폭_Next				YD_CRN_WRK_MAX_W_NEXT	NUMBER(5,1)	
		//	59.	야드크레인작업코일외경_Next				COIL_OUTDIA_NEXT		NUMBER(5,0)	
		//	60.	야드크레인작업코일내경_Next				COIL_INDIA_NEXT			NUMBER(5,1)	
			
		
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
		String szMethodName       = "makeY5L004AutoCrn";
		String szMsg              = "";
		String szOperationName    = "C열연코일L2 크레인작업지시AutoCrn";
		String szYD_SCH_NAME      = "";
		String szYD_SCH_NAME_NEXT = "";
		String szYD_EQP_ID2       = "";
		String szCrnSchID         = "";
		String szYD_SCH_CD        = "";
		String szYD_WRK_PROG_STAT = "";
		String szMSG_GP           = "";
		String szTemp             = "";
		String szProgStatTemp     = "";
		String szYD_SCH_CD_NEXT   = "";
	 		
		// 문자열변환 임시변수
		String szConv             = "";
				
		// 리턴값
		int intRtnVal1            = 0;
		int intRtnVal2            = 0;
		
		// TC Length = 743 (60 + 683)
//		int nTcLen                = 359;
		int nTcLen                = 361;
			
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeY5L004AutoCrn() IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
			
			szCrnSchID = ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID");
			szYD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(inRec, "YD_WRK_PROG_STAT");
			szMSG_GP = ydDaoUtils.paraRecChkNull(inRec, "MSG_GP");
			
			//=======================================================================================================================
			// [1] 크레인스케쥴+크레인작업재료 테이블 조회 (Key: 크레인스케쥴 ID) 
			//
			// [13] com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnWrkMtlWrkBookId 
			//=======================================================================================================================
			recPara1 = JDTORecordFactory.getInstance().create();			
			rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
			recPara1.setField("YD_CRN_SCH_ID", szCrnSchID);
			recPara1.setField("YD_WRK_PROG_STAT", szYD_WRK_PROG_STAT);
			ydUtils.putLog(szClassName, szMethodName, "szYD_WRK_PROG_STAT ="+szYD_WRK_PROG_STAT, YdConstant.DEBUG);
			szProgStatTemp = szYD_WRK_PROG_STAT;
			
			
			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnWrkMtlNextWrkBookId_NEXT3*/
			intRtnVal1 = ydCrnSchDao.getYdCrnsch(recPara1, rsResult1, 510);
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
			outRec.setField("MSG_ID" , "YDY5L004");
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
			szTemp = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal1, "YD_L2_REQUEST_STAT"), 1, 1);
			
//			작업취소 W 인경우 => W변환후 아래 W체크에서 1
			if("D".equals(szTemp)){
				szTemp = "W";
			}else if("X".equals(szTemp)){
				szTemp = "D";
			}
			
//			일반 W경우 
			if(szTemp.equals("W") ){
				outRec.setField("YD_WRK_PROG_STAT", YdUtils.fillSpZr("1", 1, 1));
			}
			else{
				outRec.setField("YD_WRK_PROG_STAT", YdUtils.fillSpZr(szTemp, 1, 1));
			}
			// 150713 hun 크레인 무인화 자동크레인일 경우 "5" 세팅
//			if("5".equals(szProgStatTemp)){
//				outRec.setField("YD_WRK_PROG_STAT", YdUtils.fillSpZr(szProgStatTemp, 1, 1));
//			}
			
			// 야드스케쥴코드 [야드스케쥴코드]
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recGetVal1, "YD_SCH_CD");
			outRec.setField("YD_SCH_CD", YdUtils.fillSpZr(szYD_SCH_CD, 8, 1));
			
			//=======================================================================================================================
			// [2] 스케쥴 기준 테이블 조회
			//
			// [0] com.inisteel.cim.yd.dao.ydschruledao.YdSchruleDao.getYdSchrule
			//=======================================================================================================================
			if(!szYD_SCH_CD.trim().equals("")){
				recPara2 = JDTORecordFactory.getInstance().create();			
				rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
				recPara2.setField("YD_SCH_CD", szYD_SCH_CD);
				intRtnVal2 = ydSchRuleDao.getYdSchrule(recPara2, rsResult2, 0);
				if(intRtnVal2 < 0) {
					szMsg = "스케쥴 기준 테이블 조회오류 (" + szYD_SCH_CD + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
	
					szYD_SCH_NAME = YdUtils.fillSpZr_KOR(" ", 30, 1);	
					szYD_EQP_ID2 = YdUtils.fillSpZr(" ", 6, 1);			
				} else if(intRtnVal2 == 0) {
					szMsg = "스케쥴 기준 테이블 조회건수 없음 (" + szYD_SCH_CD + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
					
					szYD_SCH_NAME = YdUtils.fillSpZr_KOR(" ", 30, 1);	
					szYD_EQP_ID2 = YdUtils.fillSpZr(" ", 6, 1);			
				}else {		
					rsResult2.first();
					recGetVal2 = rsResult2.getRecord();				
	
					szYD_SCH_NAME = YdUtils.fillSpZr_KOR(recGetVal1.getFieldString("CD_CONTENTS"), 30, 1);
					szYD_EQP_ID2 = YdUtils.fillSpZr(recGetVal1.getFieldString("YEOJAE_CAUSE_CD"), 6, 1);
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
			
			//*************************************************************************************************************************************
			// 야드크레인스케줄잔여회수 [야드크레인스케줄잔여회수]               
			outRec.setField("YD_CRN_SCH_RMD_CNT", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_CRN_SCH_RMD_CNT"), 2, 1));
			//*************************************************************************************************************************************			
			
			// 야드권상지시위치 [야드권상지시위치]
			outRec.setField("YD_UP_WO_LOC", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_LOC"), 8, 1));
			
			// 야드권상지시단 [야드권상지시단]
			outRec.setField("YD_UP_WO_LAYER", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_LAYER"), 3, 1));
			
			// 야드권상지시X축[야드권상지시X축]
			outRec.setField("YD_UP_WO_LOC_XAXIS", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_LOC_XAXIS"), 7, 0));
			
			// 야드권상지시X축오차최대 [야드권상지시X축오차최대]
			outRec.setField("YD_UP_WO_XAXIS_GAP_MAX", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_XAXIS_GAP_MAX"), 5, 0));
			
			// 야드권상지시X축오차최소 [야드권상지시X축오차최소]
			outRec.setField("YD_UP_WO_XAXIS_GAP_MIN", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_XAXIS_GAP_MIN"), 5, 0));
			
			// 야드권상지시Y축 [야드권상지시Y축]
			outRec.setField("YD_UP_WO_LOC_YAXIS", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_LOC_YAXIS"), 5, 0));
			
			// 야드권상지시Y축오차최대 [야드권상지시Y축오차최대]
			outRec.setField("YD_UP_WO_YAXIS_GAP_MAX", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_YAXIS_GAP_MAX"), 5, 0));
			
			// 야드권상지시Y축오차최소 [야드권상지시Y축오차최소]
			outRec.setField("YD_UP_WO_YAXIS_GAP_MIN", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_YAXIS_GAP_MIN"), 5, 0));
			
			// 야드권상지시Z축 [야드권상지시Z축]
			outRec.setField("YD_UP_WO_LOC_ZAXIS", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_LOC_ZAXIS"), 5, 0));
			
			// 야드권상지시Z축오차최대 [야드권상지시Z축오차최대]
			outRec.setField("YD_UP_WO_ZAXIS_GAP_MAX", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_ZAXIS_GAP_MAX"), 5, 0));
			
			// 야드권상지시Z축오차최소 [야드권상지시Z축오차최소]
			outRec.setField("YD_UP_WO_ZAXIS_GAP_MIN", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_UP_WO_ZAXIS_GAP_MIN"), 5, 0));
			
			// 야드권하지시위치 [야드권하지시위치]
//			권하위치 변경시 백업 컬럼 셀렉트
			if("5".equals(szProgStatTemp) && !"".equals(recGetVal1.getFieldString("YD_DN_WO_LOC_TO")) ){
				outRec.setField("YD_DN_WO_LOC", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_LOC_TO"), 8, 1));
			}else{
//				권하위치 변경이 아닐경우
				outRec.setField("YD_DN_WO_LOC", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_LOC"), 8, 1));
			}
			
			// 야드권하지시단 [야드권하지시단]
			outRec.setField("YD_DN_WO_LAYER", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_LAYER"), 3, 1));
			
			// 야드권하지시X축 [야드권하지시X축]
			outRec.setField("YD_DN_WO_LOC_XAXIS", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_LOC_XAXIS"), 7, 0));
			
			// 야드권하지시X축오차최대 [야드권하지시X축오차최대]
			outRec.setField("YD_DN_WO_XAXIS_GAP_MAX", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_XAXIS_GAP_MAX"), 5, 0));
			
			// 야드권하지시X축오차최소 [야드권하지시X축오차최소]
			outRec.setField("YD_DN_WO_XAXIS_GAP_MIN", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_XAXIS_GAP_MIN"), 5, 0));
			
			// 야드권하지시Y축 [야드권하지시Y축]
			outRec.setField("YD_DN_WO_LOC_YAXIS", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_LOC_YAXIS"), 5, 0));
			
			// 야드권하지시Y축오차최대 [야드권하지시Y축오차최대]
			outRec.setField("YD_DN_WO_YAXIS_GAP_MAX", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_YAXIS_GAP_MAX"), 5, 0));
			
			// 야드권하지시Y축오차최소 [야드권하지시Y축오차최소]
			outRec.setField("YD_DN_WO_YAXIS_GAP_MIN", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_YAXIS_GAP_MIN"), 5, 0));
			
			// 야드권하지시Z축 [야드권하지시Z축]
			outRec.setField("YD_DN_WO_LOC_ZAXIS", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_LOC_ZAXIS"), 5, 0));
			
			// 야드권하지시Z축오차최대 [야드권하지시Z축오차최대]
			outRec.setField("YD_DN_WO_ZAXIS_GAP_MAX", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_ZAXIS_GAP_MAX"), 5, 0));
			
			// 야드권하지시Z축오차최소 [야드권하지시Z축오차최소]
			outRec.setField("YD_DN_WO_ZAXIS_GAP_MIN", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_DN_WO_ZAXIS_GAP_MIN"), 5, 0));			

			// 야드설비ID2 [야드설비ID2]                             
			outRec.setField("YD_EQP_ID2", szYD_EQP_ID2);				
					
			//=======================================================================================================================
			// [3] 작업예약 조회
			//
			// [2] com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookYdWrkbookMtl_PIDEV
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
			outRec.setField("YD_EQP_WRK_SH", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_WRK_SH"), 2, 0)); 				


			//*************************************************************************************************************************************										   
			// 야드설비잔량매수[야드설비잔량매수]		       
			outRec.setField("YD_EQP_RMN_SH", YdUtils.fillSpZr("00", 2, 0));
			//*************************************************************************************************************************************										   

			
			// 재료번호 [재료번호]
			outRec.setField("STL_NO1", YdUtils.fillSpZr(recGetVal1.getFieldString("STL_NO"), 11, 1));
			
			// 야드재료중량 [야드재료중량]
			outRec.setField("YD_STL_WT1", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_MTL_WT"), 5, 0));
			
			// 야드재료두께 [야드재료두께]
//			szConv = YdUtils.fillSpZr(recGetVal1.getFieldString("YD_MTL_T"), 7, 0);
			outRec.setField("YD_STL_T1", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_MTL_T"), 6, 0));	

			// 야드재료폭 [야드재료폭]
//			szConv = YdUtils.fillSpZr(recGetVal1.getFieldString("YD_MTL_W"), 6, 0);
			outRec.setField("YD_STL_W1", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_MTL_W"), 5, 0));	
			
			// 야드재료길이 [야드재료길이]
			outRec.setField("YD_STL_L1", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_MTL_L"), 7, 0));				

			// 코일외경 [코일외경]
			String szCOIL_OUTDIA = ydDaoUtils.paraRecChkNull(recGetVal1, "COIL_OUTDIA");
			outRec.setField("COIL_OUTDIA", YdUtils.fillSpZr(szCOIL_OUTDIA, 5, 0));
			
			// 코일내경 [코일내경]
			String szCOIL_INDIA = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal1, "COIL_INDIA"), 6, 1);
			outRec.setField("COIL_INDIA", ydUtils.FloatLRPAD(szCOIL_INDIA, 5, 1, '0'));
			
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
						
			// 야드크레인작업중량_Next [야드크레인작업중량_Next]
			outRec.setField("YD_CRN_WRK_WT_NEXT", YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_WRK_WT_NEXT"), 7, 0));
			
			// 야드크레인작업총두께_Next [야드크레인작업총두께_Next]
			szConv = YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_WRK_T_NEXT"), 7, 1);
			outRec.setField("YD_CRN_WRK_T_NEXT", ydUtils.FloatLRPAD(szConv, 6, 3, '0'));					
			
			// 야드크레인작업최대폭_Next [야드크레인작업최대폭_Next]
			szConv = YdUtils.fillSpZr(recGetVal1.getFieldString("YD_EQP_WRK_MAX_W_NEXT"), 6, 1);
			outRec.setField("YD_CRN_WRK_MAX_W_NEXT", ydUtils.FloatLRPAD(szConv, 5, 1, '0'));					
			
			// 야드크레인작업코일외경_Next [야드크레인작업코일외경_Next]
			String szCOIL_OUTDIA_NEXT = ydDaoUtils.paraRecChkNull(recGetVal1, "COIL_OUTDIA_NEXT");
			outRec.setField("COIL_OUTDIA_NEXT", YdUtils.fillSpZr(szCOIL_OUTDIA_NEXT, 5, 0));
			
			// 야드크레인작업코일내경_Next [야드크레인작업코일내경_Next]
			String szCOIL_INDIA_NEXT = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal1, "COIL_INDIA_NEXT"), 6, 1);
			outRec.setField("COIL_INDIA_NEXT", ydUtils.FloatLRPAD(szCOIL_INDIA_NEXT, 5, 1, '0'));					
			
			// 2015.06.24 hun 크레인 무인화작업 권상위치 회전각도
			outRec.setField("UP_ROTATION_ANGLE", YdUtils.fillSpZr(recGetVal1.getFieldString("UP_ROTATION_ANGLE"), 1, 1));
			
			// 2015.06.24 hun 크레인 무인화작업 권하위치 회전각도
			outRec.setField("DOWN_ROTATION_ANGLE", YdUtils.fillSpZr(recGetVal1.getFieldString("DOWN_ROTATION_ANGLE"), 1, 1));
			
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);		
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================makeY5L004AutoCrn() OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
		}catch(Exception e){
			szMsg = "Y5(C열연코일야드L2) 송신  크레인작업지시  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}
			
		return outRecSet.size();
	} // end of makeY5L004()

	
	
	
	
	/**
	 * YDY5L005 : 크레인작업실적응답
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeY5L005(JDTORecord inRec, JDTORecordSet outRecSet){
		
			YdUtils ydUtils 			= new YdUtils();
			YdDaoUtils ydDaoUtils 		= new YdDaoUtils();

			JDTORecord outRec 			= null;

			// 변수선언
			String szMethodName 		= "makeY5L005";
			String szMsg 				= "";
			String szOperationName      = "C열연코일L2 크레인작업실적응답";
			
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
			String szTemp      			= "";
			String szTemp1     			= "";
			
			String szYD_L3_MSG          = "";
			// TC Length =138 /60+78
			int nTcLen 					= 78;

			try{

				ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 1]::makeY5L005==============\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, inRec);

				/*
				 * 업무 : 크레인작업실적응답 전문 편집(YDY5L005)
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
				}else if(  szYD_L2_WR_GP.equals(YdConstant.CRN_WRK_RE_BREAK)) {				//B:고장
					szTemp = "고장";
					//szTemp = "설비고장복구실적";
				}else if(  szYD_L2_WR_GP.equals(YdConstant.CRN_WRK_RE_TRBL)) {				//R:고장(해제)
					szTemp = "고장해제";
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

				szYD_L3_MSG 		= ydDaoUtils.paraRecChkNull(inRec, "YD_L3_MSG");

				// 야드L3MESSAGE
				
				
				if( szYD_L3_HD_RS_CD.equals(YdConstant.CRN_WRK_RE_CD_NORMAL_HD) || szYD_L3_HD_RS_CD.equals(YdConstant.CRN_WRK_RE_CD_NO_WRK) ){
					szTemp += "가(이) 정상 처리 되었습니다.";
				}else {
					szTemp += "가(이) Error처리 되었습니다.";
				}
//sjh 추가 : 강제권하
				szMsg = "[크레인작업실적응답-->] " + szTemp + " [" + szYD_L3_MSG + "] 입니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);

				if(szYD_L3_MSG.length() > 10) {

					szTemp = szYD_L3_MSG;
				}
				
				outRec = JDTORecordFactory.getInstance().create();

				//		1.	전문 ID				MSG_ID				VARCHAR2(8)		YDY5L005
				//		2.	생성일				DATE				VARCHAR2(10)	YYYY-MM-DD
				//		3.	생성시간				TIME				VARCHAR2(8)		24HH-MM-SS
				//		4.	전문구분				MSG_GP				VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
				//		5.	전문길이				MSG_LEN				NUMBER(4)		
				//		6.	임시					TEMP				VARCHAR2(29)	
				//		7.	야드설비ID			YD_EQP_ID			VARCHAR2(6)		크레인설비 ID
				//		8.	야드작업진행상태		YD_WRK_PROG_STAT	VARCHAR2(1)		"2" 권상실적, "4" 권하실적
				//		9.	야드스케쥴코드			YD_SCH_CD			VARCHAR2(8)		
				//		10.	야드설비스케쥴ID		YD_CRN_SCH_ID		VARCHAR2(18)			
				//*		11.	야드L2실적구분			YD_L2_WR_GP			VARCHAR2(1)		U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경
				//*		12.	야드L3처리결과코드		YD_L3_HD_RS_CD		VARCHAR2(4)		0000:정상
				//*		13.	야드L3MESSAGE		YD_L3_MSG			VARCHAR2(40)	"권상(또는 권하)실적이 정상 처리 되었습니다.
				//																	  권상(또는 권하)실적이 Error 처리 되었습니다."
				outRec.setField("MSG_ID", 				new String("YDY5L005"));
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
				szMsg = "YDY5L005[크레인작업실적응답] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
				return -1;
			}

			return outRecSet.size();
		} // end of makeY5L005()

	
	
	
	
	
	
	/**
	 * YDY5L006 : 대차출발지시
	 * @param JDTORecord inRec
	 * @return JDTORecordSet outRecSet
	 */
	public static int makeY5L006(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID				MSG_ID					VARCHAR2(8)		YDY5L006
		//		2.	생성일				DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간				TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분				MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이				MSG_LEN					NUMBER(4)		
		//		6.	임시					TEMP					VARCHAR2(29)	
		//		7.	야드설비ID			YD_EQP_ID				VARCHAR2(6)		대차 야드설비ID (DD 참조)
		//		8.	야드설비작업상태		YD_EQP_WRK_STAT			VARCHAR2(1)	   U(하차) , L(상차)
		//*		9.	야드목표동구분			YD_AIM_BAY_GP			VARCHAR2(1)	
		//*		10.	야드대차상차위치		YD_TCAR_LD_LOC			VARCHAR2(6)	
		//*		11.	야드대차하차위치		YD_TCAR_UD_LOC			VARCHAR2(6)	
		//*		12.	야드대차작업매수		YD_EQP_WRK_SH			NUMBER(2)	
		//*		13.	야드대차작업중량		YD_EQP_WRK_WT			NUMBER(7)	

		// 대차스케줄Dao 객체 생성
		YdTcarSchDao ydTcarSchDao	= new YdTcarSchDao();
		YdUtils ydUtils 			= new YdUtils();
		YdDaoUtils ydDaoUtils 		= new YdDaoUtils();
		
		// 조회 결과를 담을 RecordSet생성
		JDTORecordSet rsGetYdTcarSch = JDTORecordFactory.getInstance().createRecordSet("");

		// 레코드 선언
		JDTORecord recPara 			= null;
		JDTORecord outRec 			= null;

		// 변수선언
		String szMethodName 		= "makeY5L006";
		String szMsg 				= "";
		String szOperationName      = "C열연코일L2 대차출발지시";
		
		// 야드설비ID(대차스케줄-야드설비ID)
		String szYD_EQP_ID			= "";

		// 야드설비작업상태(대차스케줄-야드설비작업상태)
		String szYD_EQP_WRK_STAT	= "";

		// 야드목표동구분
		String szYD_AIM_BAY_GP	 	= "";

		// 야드대차상차위치(대차스케줄-야드상차정지위치)
		String szYD_TCAR_LD_LOC	 	= "";

		// 야드대차하차위치(대차스케줄-야드하차정지위치)
		String szYD_TCAR_UD_LOC	 	= "";
		
		// 야드대차작업매수(대차스케줄-야드설비작업매수)
		String szYD_EQP_WRK_SH	 	= "";

		// 야드대차작업중량(대차스케줄-야드설비작업중량)
		String szYD_EQP_WRK_WT	 	= "";
		
		// TC Length =89
		int nTcLen 					= 29;
		int intRtnVal 				= 0;
		
		try{
			
			ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 1]::makeY5L006==============\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			
			// 대차스케줄 조회
			intRtnVal = ydTcarSchDao.getYdTcarsch(inRec, rsGetYdTcarSch, 2);
			if(intRtnVal < 0){
				szMsg = "대차스케줄 조회 중 오류 : [" +intRtnVal+"]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			} else if(intRtnVal == 0){
				szMsg = "대차스케줄 조회건수 없음 : [" +intRtnVal+"]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				return 0;
			}

			outRec = JDTORecordFactory.getInstance().create();

			// 대차스케줄 조회결과 추출
			rsGetYdTcarSch.first();
			recPara = JDTORecordFactory.getInstance().create();
			recPara = rsGetYdTcarSch.getRecord();

			
			ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 2]:: 조 회 결 과==============\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szMethodName, recPara);

			// 야드설비ID
			szYD_EQP_ID 		=ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_ID");

			// 야드설비작업상태
			szYD_EQP_WRK_STAT 	=ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_STAT");

			// 야드목표동구분
//			szYD_AIM_BAY_GP 	=ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
			szYD_AIM_BAY_GP 	=ydDaoUtils.paraRecChkNull(inRec, "YD_AIM_BAY_GP");

			// 야드대차상차위치
//			szYD_TCAR_LD_LOC 	=ydDaoUtils.paraRecChkNull(recPara, "YD_TCAR_LD_LOC");
			szYD_TCAR_LD_LOC 	=ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_STOP_LOC");

			// 야드대차하차위치YD_CARUD_STOP_LOC
//			szYD_TCAR_UD_LOC 	=ydDaoUtils.paraRecChkNull(recPara, "YD_TCAR_UD_LOC");
			szYD_TCAR_UD_LOC 	=ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_STOP_LOC");

			// 야드대차작업매수
			szYD_EQP_WRK_SH 	=ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_SH");

			// 야드대차작업중량
			szYD_EQP_WRK_WT 	=ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_WT");

			outRec.setField("MSG_ID", 				new String("YDY5L006") );
			outRec.setField("DATE", 				new String(YdUtils.getCurDate("yyyy-MM-dd")) );
			outRec.setField("TIME", 				new String(YdUtils.getCurDate("HH-mm-ss")) );
			outRec.setField("MSG_GP", 				new String("I") );
			outRec.setField("MSG_LEN", 				new String(YdUtils.fillSpZr(""+nTcLen, 4, 0)) );
			outRec.setField("TEMP", 				new String(YdUtils.fillSpZr("", 29, 1)) );
			outRec.setField("YD_EQP_ID", 			YdUtils.fillSpZr(szYD_EQP_ID, 		6, 1));
			outRec.setField("YD_EQP_WRK_STAT", 		YdUtils.fillSpZr(szYD_EQP_WRK_STAT, 1, 1));
			outRec.setField("YD_AIM_BAY_GP", 		YdUtils.fillSpZr(szYD_AIM_BAY_GP, 	1, 1));
			outRec.setField("YD_TCAR_LD_LOC", 		YdUtils.fillSpZr(szYD_TCAR_LD_LOC, 	6, 1));
			outRec.setField("YD_TCAR_UD_LOC", 		YdUtils.fillSpZr(szYD_TCAR_UD_LOC, 	6, 1));
			outRec.setField("YD_EQP_WRK_SH", 		YdUtils.fillSpZr(szYD_EQP_WRK_SH, 	2, 1));
			outRec.setField("YD_EQP_WRK_WT", 		YdUtils.fillSpZr(szYD_EQP_WRK_WT, 	7, 1));

			// RecordSet으로 반환
			outRecSet.addRecord(outRec);

			ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 3]:: 송신 TC내용 ==============\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);

		}catch(Exception e){
			szMsg = "YDY5L006[대차출발지시] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}
		
		return outRecSet.size();

	} // end of makeY5L006()
	
	
	
	/**
	 * YDY5L007 : 작업현황응답
	 * @param JDTORecord inRec
	 * @return JDTORecordSet outRecSet
	 */
	public static int makeY5L007(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID				MSG_ID					VARCHAR2(8)		YDY5L007
		//		2.	생성일				DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간				TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분				MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이				MSG_LEN					NUMBER(4)		
		//		6.	임시					TEMP					VARCHAR2(29)	
		//		7.	야드설비ID			YD_EQP_ID				VARCHAR2(6)		대차 야드설비ID (DD 참조)
		//		8	YD_SCH_FLAG1	요구스케쥴구분1	CHAR	1
		//		9	YD_SCH_CNT1		요구스케쥴건수1	CHAR	3
		//		10	YD_SCH_FLAG2	요구스케쥴구분2	CHAR	1
		//		11	YD_SCH_CNT2		요구스케쥴건수2	CHAR	3
		//		12	YD_SCH_FLAG3	요구스케쥴구분3	CHAR	1
		//		13	YD_SCH_CNT3		요구스케쥴건수3	CHAR	3
		//		14	YD_SCH_FLAG4	요구스케쥴구분4	CHAR	1
		//		15	YD_SCH_CNT4		요구스케쥴건수4	CHAR	3
		//		16	YD_SCH_FLAG5	요구스케쥴구분5	CHAR	1
		//		17	YD_SCH_CNT5		요구스케쥴건수5	CHAR	3
		//		18	YD_SCH_FLAG6	요구스케쥴구분6	CHAR	1
		//		19	YD_SCH_CNT6		요구스케쥴건수6	CHAR	3
		//		20	YD_SCH_FLAG7	요구스케쥴구분7	CHAR	1
		//		21	YD_SCH_CNT7		요구스케쥴건수7	CHAR	3
		//		22	YD_SCH_FLAG8	요구스케쥴구분8	CHAR	1
		//		23	YD_SCH_CNT8		요구스케쥴건수8	CHAR	3
		//		24	YD_SCH_FLAG9	요구스케쥴구분9	CHAR	1
		//		25	YD_SCH_CNT9		요구스케쥴건수9	CHAR	3
		//		26	YD_SCH_FLAG10	요구스케쥴구분10	CHAR	1
		//		27	YD_SCH_CNT10	요구스케쥴건수10	CHAR	3
	

		// 대차스케줄Dao 객체 생성
		YdWrkbookDao YdWrkbookDao	= new YdWrkbookDao();
		YdUtils ydUtils 			= new YdUtils();
		YdDaoUtils ydDaoUtils 		= new YdDaoUtils();
		
		// 조회 결과를 담을 RecordSet생성
		JDTORecordSet rsGetYdSch = JDTORecordFactory.getInstance().createRecordSet("");

		// 레코드 선언
		JDTORecord recPara 			= null;
		JDTORecord outRec 			= null;

		// 변수선언
		String szMethodName 		= "makeY5L007";
		String szMsg 				= "";
		String szOperationName      = "C열연코일L2 작업현황응답";
		
		// 야드설비ID(대차스케줄-야드설비ID)
		String szYD_EQP_ID			= "";

		// 요구스케쥴건수
		String szYD_SCH_CNT	 	= "";

		// 요구스케쥴구분
		String szYD_SCH_FLAG	 	= "";
		
		// TC Length =106
		int nTcLen 					= 46;
		int intRtnVal 				= 0;
		
		try{
			
			ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 1]::makeY5L007==============\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
//			 야드설비ID
			szYD_EQP_ID 		=ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			
			// 크레인 작업현형  조회
			//com.inisteel.cim.yd.dao.ydWrkbookDao.wrkbookydschflag
			intRtnVal = YdWrkbookDao.getYdWrkbook(inRec, rsGetYdSch, 504);
			if(intRtnVal < 0){
				szMsg = "C열연코일L2 작업현황응답 조회 중 오류 : [" +intRtnVal+"]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			} else if(intRtnVal == 0){
				szMsg = "C열연코일L2 작업현황응답 조회건수 없음 : [" +intRtnVal+"]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				return 0;
			}
			outRec = JDTORecordFactory.getInstance().create();
			
			outRec.setField("MSG_ID", 				new String("YDY5L007") );
			outRec.setField("DATE", 				new String(YdUtils.getCurDate("yyyy-MM-dd")) );
			outRec.setField("TIME", 				new String(YdUtils.getCurDate("HH-mm-ss")) );
			outRec.setField("MSG_GP", 				new String("I") );
			outRec.setField("MSG_LEN", 				new String(YdUtils.fillSpZr(""+nTcLen, 4, 0)) );
			outRec.setField("TEMP", 				new String(YdUtils.fillSpZr("", 29, 1)) );
			outRec.setField("YD_EQP_ID", 			YdUtils.fillSpZr(szYD_EQP_ID, 		6, 1));
			
			// 크레인스케줄 조회결과 추출
			for(int i=0; i<10 ; i++){
				
				if(i < intRtnVal){
					for(int j=0; j<intRtnVal ; j++){
					recPara = rsGetYdSch.getRecord(j);
					
					// 요구스케쥴구분
					szYD_SCH_FLAG 	=ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_FLAG");
	
					// 요구스케쥴건수
					szYD_SCH_CNT 	=ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CNT");
					
					outRec.setField("YD_SCH_FLAG"+(j+1), 		YdUtils.fillSpZr(szYD_SCH_FLAG, 1, 1));
					outRec.setField("YD_SCH_CNT"+(j+1), 		YdUtils.fillSpZr(szYD_SCH_CNT, 3, 1));
					
					i =j +1  ;
					}
					
					outRec.setField("YD_SCH_FLAG"+(i+1), 		YdUtils.fillSpZr("", 1, 1));
					outRec.setField("YD_SCH_CNT"+(i+1), 		YdUtils.fillSpZr("", 3, 1));
				}else {
					outRec.setField("YD_SCH_FLAG"+(i+1), 		YdUtils.fillSpZr("", 1, 1));
					outRec.setField("YD_SCH_CNT"+(i+1), 		YdUtils.fillSpZr("", 3, 1));
				}
				
			}
			
			

			// RecordSet으로 반환
			outRecSet.addRecord(outRec);

			ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 3]:: 송신 TC내용 ==============\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);

		}catch(Exception e){
			szMsg = "YDY5L007[C열연코일L2 작업현황응답] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}
		
		return outRecSet.size();

	} // end of makeY5L007()


	
	/**
	 * YDY5L008 : 차량작업 예정 정보
	 * @param JDTORecord inRec
	 * @return JDTORecordSet outRecSet
	 */
	public static int makeY5L008(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID				MSG_ID					VARCHAR2(8)		YDY5L008
		//		2.	생성일				DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간			TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분			MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이			MSG_LEN					NUMBER(4)		
		//		6.	임시				TEMP					VARCHAR2(29)
		//      7.  상차도 위치         PT_LOAD_LOC             CHAR(6)
		//      8.  차량번호            CAR_NO   				CHAR(15)
		//      9.  차량구분            PT_CLS					CHAR(2)
		//      10. 작업구분            WORK_CLS 				CHAR(1)
		//      11. 적재함 폭           PT_WTH					CHAR(4)
		//      12. 적재함 길이         PT_LEN					CHAR(5)
		//      13. 적재함 높이         PT_HEIGHT				CHAR(5)
		//      14. 우천차량 여부       RAIN_CLS				CHAR(1)
		//      15. 작업 총 수량        WORK_COIL_MAX_CNT		CHAR(2)
		//      16. 코일정보            
		//      17. 코일번호            MATL_NO					CHAR(11)
		//      18. 차량적재위치        LOAD_LOC_CD				CHAR(2)
		//      19. 코일 중량           MAT_WGT					CHAR(5)
		//      20. 코일 두께           MAT_THK					CHAR(6)
		//      21. 코일 폭             MAT_WTH					CHAR(5)
		//      22. 코일 길이           MAT_LEN					CHAR(6)
		//      23. 코일 외경           MAT_ODIA				CHAR(5)
		//      24. 코일 내경           MAT_IDIA				CHAR(5)
		//      25. 작업상태            WORK_STATE   			CHAR(1)
		//      26. 동정보              YD_CURR_BAY_GP   		CHAR(6)

		// 대차스케줄Dao 객체 생성
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		YdUtils ydUtils 			= new YdUtils();
		ymCommonDAO dao = ymCommonDAO.getInstance();
		YdEqpDao     ydEqpDao     = new YdEqpDao();
		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
		
		// 조회 결과를 담을 RecordSet생성
		JDTORecordSet rsGetYdCarSch = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsGetYdEqpId = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsGetYdCarSchChk = JDTORecordFactory.getInstance().createRecordSet("");

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recTempPara = null;
		JDTORecord outRec = null;
		YDDataUtil  yddatautil = new YDDataUtil();
		
		// 변수선언
		String szMethodName 		= "makeY5L008";
		String szMsg 				= "";
		String szOperationName      = "차량작업 예정 정보";
		String szMATL_NO			= "";
		String szYD_CAR_UPP_LOC_CD	= "";			
		String szMAT_WGT			= "";
		String szLOAD_LOC_CD		= "";
		String szMAT_THK			= "";
		String szMAT_WTH			= "";
		String szMAT_LEN			= "";
		String szMAT_ODIA			= "";			
		String szMAT_IDIA			= "";			
		String szWORK_STATE			= "";
		String szYD_CURR_BAY_GP		= "";
		String szYD_PT_CLS			= "";
		String szYD_CAR_UPP_LOC_CD_TO	= "";
		String sQueryId	= "";
		String szEqpId	= "";
		int nIdx                  = 0;
		int nUppCnt                  = 0; 
		
		
		// TC Length =89
		int nTcLen 					= 821;
		int intRtnVal 				= 0;
		int intRtnValTemp 				= 0;
		
		try{
			
			ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 1]::makeY5L008==============\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			
			// 차량작업 예정정보 조회
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarGetInWorkInfo_PIDEV*/
	    	//intRtnVal = ydCarSchDao.getYdCarsch(inRec, rsGetYdCarSch, 435);
	    	
	    	// 차량작업 예정정보 조회
	    	if ("".equals(ydDaoUtils.paraRecChkNull(inRec , "YD_CAR_SCH_ID") )){
	    		ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 2]::YD_CAR_SCH_ID 없을때 ", YdConstant.DEBUG);
	    		ydUtils.putLog(szMethodName, szMethodName, "callYDY5L008 YD_CRN_SCH_ID=("+ydDaoUtils.paraRecChkNull(inRec , "YD_CRN_SCH_ID")+")", YdConstant.DEBUG);
//PIDEV_S :병행가동용:PI_YD
	    		inRec.setField("PI_YD",    	"J");				    		
	    		/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarGetInWorkByCarNo_PIDEV*/
		    	intRtnVal = ydCarSchDao.getYdCarsch(inRec, rsGetYdCarSch, 437);
		    	
	    	}else{
	    		ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 2]::차량작업 요구시 ", YdConstant.DEBUG);
	    		ydUtils.putLog(szClassName, szMethodName, "\n================최초 요구시 차량 적재위치 update start ", YdConstant.DEBUG);
	    		
	    		
	    		// 150908 hun 차량작업 예정정보 최초 전송시 무인모드 체크 안함... ( 유무인 모두 나가게 수정 )
	    		/*
	    		// 차량 스케쥴ID 로 Auto 설비 check 
	    		rsGetYdEqpId = JDTORecordFactory.getInstance().createRecordSet("Temp");
				sQueryId = "com.inisteel.cim.yd.dao.ydeqpdao.YdCrnSchDao.YdGetEqpIdbyYdCarSchId";
				intRtnVal = ydCommDao.select(inRec, rsGetYdEqpId, sQueryId);
				
				if(intRtnVal <= 0 )
				{
					ydUtils.putLog(szClassName, szMethodName, "해당 설비 ID  정보 조회시 ERROR발생 ", YdConstant.DEBUG);
					return 0;
				}
				
				rsGetYdEqpId.absolute(1);
				recTempPara = JDTORecordFactory.getInstance().create();
				// Temp Data inDto에 다시 세팅 
				recTempPara.setRecord(rsGetYdEqpId.getRecord());
	    		
				szEqpId = yddatautil.setDataDefault(recTempPara.getField("YD_EQP_ID"), "");
	    		*/
				
				// 150824 hun 차량작업 예정정보 최초 전송시 무인모드 체크 안함... ( 유무인 모두 나가게 수정 )
//				if(ydEqpDao.chkAutoCrn(szEqpId)){
					
					// 조건 차량 구분 in ('TR', 'GT') and 차량적재위치(YD_CAR_UPP_LOC_CD) is null 일때 해당 코일 stock update실행
		    		
		    		/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarGetInWorkInfoChk*/
			    	intRtnVal = ydCarSchDao.getYdCarsch(inRec, rsGetYdCarSchChk, 438);
			    	
		    		
		    		for (int Loop_i = 1; Loop_i <= intRtnVal; Loop_i++){
		    			ydUtils.putLog(szMethodName, szMethodName, "callYDY5L008 >>"+Loop_i+"번째 coil 위치 세팅", YdConstant.DEBUG);
		    			
		    			rsGetYdCarSchChk.absolute(Loop_i);
		    			recPara = JDTORecordFactory.getInstance().create();
		    			recPara.setRecord(rsGetYdCarSchChk.getRecord());
		    			
						ydUtils.putLog(szMethodName, szMethodName, "callYDY5L008 >>"+Loop_i+"번째 Record 세팅", YdConstant.DEBUG);
						
						szYD_CAR_UPP_LOC_CD	= ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_UPP_LOC_CD");			
						szYD_PT_CLS 		= ydDaoUtils.paraRecChkNull(recPara, "YD_PT_CLS");
						szMATL_NO 			= ydDaoUtils.paraRecChkNull(recPara, "YD_STL_NO");
						//szYD_CAR_UPP_LOC_CD_TO = ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_UPP_LOC_CD_TO");
						
						// TR출하 OR 구내운송 상차 인경우 상차포인트를 야드에서 결정 한다.
						if(!"TT".equals(szYD_PT_CLS) ){
							
							sQueryId = "com.inisteel.cim.ym.dao.ydCarPointdao.ydStockDao.upYdCarUppLocCd";
							intRtnValTemp = dao.updateData(sQueryId,new Object[]{ szYD_CAR_UPP_LOC_CD, szMATL_NO });
							
				        	if (intRtnValTemp < 1)
							{
				        		ydUtils.putLog(szMethodName, szMethodName, "callYDY5L008 저장품 TABLE 저장실패", YdConstant.DEBUG);
							}else{
				        	
								ydUtils.putLog(szMethodName, szMethodName, "callYDY5L008 저장품 TABLE 저장성공", YdConstant.DEBUG);
							}
							
							
						}
		    		}
		    		
		    		ydUtils.putLog(szClassName, szMethodName, "\n================최초 요구시 차량 적재위치 update end ", YdConstant.DEBUG);
		    		
		    		ydUtils.putLog(szMethodName, szMethodName, "callYDY5L008 CAR_NO=("+ydDaoUtils.paraRecChkNull(inRec , "CAR_NO")+")", YdConstant.DEBUG);
		    		ydUtils.putLog(szMethodName, szMethodName, "callYDY5L008 YD_CARUD_STOP_LOC=("+ydDaoUtils.paraRecChkNull(inRec , "YD_CARUD_STOP_LOC")+")", YdConstant.DEBUG);
//PIDEV_S :병행가동용:PI_YD
		    		inRec.setField("PI_YD",    	"J");				    		
		    		/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarGetInWorkInfo_PIDEV*/
		    		intRtnVal = ydCarSchDao.getYdCarsch(inRec, rsGetYdCarSch, 435);
		    		
//				}else{ 
//					szMsg = "해당 설비가 무인모드가 아닙니다. [" +szEqpId+"]";
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//					
//					return 0;
//				}
	    	}
	    	
			if(intRtnVal < 0){
				szMsg = "대차스케줄 조회 중 오류 : [" +intRtnVal+"]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			} else if(intRtnVal == 0){
				
				
				if("".equals(ydDaoUtils.paraRecChkNull(inRec , "YD_CAR_SCH_ID") )){
					szMsg = "대차스케줄 조회건수 없음 : [" +intRtnVal+"] 예정정보 요구로 깡통전문 발송";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					
					outRec = JDTORecordFactory.getInstance().create();
					recPara       = JDTORecordFactory.getInstance().create();
					
					outRec.setField("MSG_ID", 				new String("YDY5L008") );
					outRec.setField("DATE", 				new String(YdUtils.getCurDate("yyyy-MM-dd")) );
					outRec.setField("TIME", 				new String(YdUtils.getCurDate("HH-mm-ss")) );
					outRec.setField("MSG_GP", 				new String("R") );

					outRec.setField("MSG_LEN", 				new String(YdUtils.fillSpZr(""+nTcLen, 4, 0)) );
					outRec.setField("TEMP", 				new String(YdUtils.fillSpZr("", 29, 1)) );
					
					outRec.setField("PT_LOAD_LOC", 		YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(inRec , "YD_CARUD_STOP_LOC"), 6, 1));
					outRec.setField("CAR_NO", 			YdUtils.fillSpZr_KOR(recPara.getFieldString("YD_CAR_NO"), 15, 1));
					outRec.setField("PT_CLS", 			YdUtils.fillSpZr(recPara.getFieldString("YD_PT_CLS"), 2, 1));
					outRec.setField("WORK_CLS", 		YdUtils.fillSpZr(recPara.getFieldString("YD_WORK_CLS"), 1, 1));
					outRec.setField("WORK_COIL_MAX_CNT",YdUtils.fillSpZr(recPara.getFieldString("YD_WORK_COIL_MAX_CNT"), 2, 0));
					// Coil 정보 setting
					for(int i=0; i<15 ; i++){
						
						outRec.setField("MATL_NO"+(i+1), 		YdUtils.fillSpZr("", 11, 1));
						outRec.setField("LOAD_LOC_CD"+(i+1), 		YdUtils.fillSpZr("", 2, 1));
						outRec.setField("MAT_WGT"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
						outRec.setField("MAT_THK"+(i+1), 		YdUtils.fillSpZr("", 6, 1));
						outRec.setField("MAT_WTH"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
						outRec.setField("MAT_LEN"+(i+1), 		YdUtils.fillSpZr("", 7, 1));
						outRec.setField("MAT_ODIA"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
						outRec.setField("MAT_IDIA"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
						outRec.setField("WORK_STATE"+(i+1), 		YdUtils.fillSpZr("", 1, 1));
						outRec.setField("YD_CURR_BAY_GP"+(i+1), 		YdUtils.fillSpZr("", 6, 1));
					}
					
					// RecordSet으로 반환
					outRecSet.addRecord(outRec);

					ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 3]:: 송신 TC내용 ==============\n", YdConstant.DEBUG);	
					ydUtils.displayRecord(szOperationName, outRec);
					
					return 1;
				}else{
				
					szMsg = "대차스케줄 조회건수 없음 : [" +intRtnVal+"]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					return 0;
				}
			}

			

			// 작업 조회결과 추출
			rsGetYdCarSch.first();
			recPara       = JDTORecordFactory.getInstance().create();
			recPara = rsGetYdCarSch.getRecord();
			ydUtils.displayRecord(szOperationName, recPara);
			outRec = JDTORecordFactory.getInstance().create();
			
			ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 2]:: 조 회 결 과==============\n"+intRtnVal, YdConstant.DEBUG);
			
			outRec.setField("MSG_ID", 				new String("YDY5L008") );
			outRec.setField("DATE", 				new String(YdUtils.getCurDate("yyyy-MM-dd")) );
			outRec.setField("TIME", 				new String(YdUtils.getCurDate("HH-mm-ss")) );
			
//			150828 hun 최초전문과 재요구 전문구분 추가
			if("".equals(ydDaoUtils.paraRecChkNull(inRec , "YD_CAR_SCH_ID") )){
				outRec.setField("MSG_GP", 				new String("R") );
			}else{
				outRec.setField("MSG_GP", 				new String("I") );
			}
			
			outRec.setField("MSG_LEN", 				new String(YdUtils.fillSpZr(""+nTcLen, 4, 0)) );
			outRec.setField("TEMP", 				new String(YdUtils.fillSpZr("", 29, 1)) );
			
			outRec.setField("PT_LOAD_LOC", 		YdUtils.fillSpZr(recPara.getFieldString("YD_PT_LOAD_LOC"), 6, 1));
//			outRec.setField("CAR_NO", 			YdUtils.fillSpZr(recPara.getFieldString("YD_CAR_NO"), 15, 1));
			outRec.setField("CAR_NO", 			YdUtils.fillSpZr_KOR(recPara.getFieldString("YD_CAR_NO"), 15, 1));
			outRec.setField("PT_CLS", 			YdUtils.fillSpZr(recPara.getFieldString("YD_PT_CLS"), 2, 1));
			outRec.setField("WORK_CLS", 		YdUtils.fillSpZr(recPara.getFieldString("YD_WORK_CLS"), 1, 1));
//			outRec.setField("PT_WTH", 			YdUtils.fillSpZr(recPara.getFieldString("YD_PT_WTH"), 4, 1));
//			outRec.setField("PT_LEN", 			YdUtils.fillSpZr(recPara.getFieldString("YD_PT_LEN"), 5, 1));
//			outRec.setField("PT_HEIGHT", 		YdUtils.fillSpZr(recPara.getFieldString("YD_PT_HEIGHT"), 5, 1));			
//			outRec.setField("RAIN_CLS", 		YdUtils.fillSpZr(recPara.getFieldString("YD_RAIN_CLS"), 1, 1));				
			outRec.setField("WORK_COIL_MAX_CNT",YdUtils.fillSpZr(recPara.getFieldString("YD_WORK_COIL_MAX_CNT"), 2, 0));
			
			intRtnVal = (intRtnVal >= 15) ? 15 : intRtnVal;
			
			// Coil 정보 setting
			for(int i=0; i<15 ; i++){
				
				if(i < intRtnVal){
					for(int j=0; j<intRtnVal ; j++){
						recPara       = JDTORecordFactory.getInstance().create();
						recPara = rsGetYdCarSch.getRecord(j);
					
						szMATL_NO 			= ydDaoUtils.paraRecChkNull(recPara, "YD_STL_NO");
						szLOAD_LOC_CD		= ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_UPP_LOC_CD");			
						szMAT_WGT 			= ydDaoUtils.paraRecChkNull(recPara, "YD_COIL_WT");
						szMAT_THK 			= ydDaoUtils.paraRecChkNull(recPara, "YD_COIL_T");
						szMAT_WTH 			= ydDaoUtils.paraRecChkNull(recPara, "YD_COIL_W");
						szMAT_LEN 			= ydDaoUtils.paraRecChkNull(recPara, "YD_COIL_LEN");
						szMAT_ODIA 			= ydDaoUtils.paraRecChkNull(recPara, "YD_COIL_OUTDIA");			
						szMAT_IDIA 			= ydDaoUtils.paraRecChkNull(recPara, "YD_COIL_INDIA");
						szWORK_STATE 		= ydDaoUtils.paraRecChkNull(recPara, "YD_WORK_STATE");
						szYD_CURR_BAY_GP 	= ydDaoUtils.paraRecChkNull(recPara, "YD_CURR_BAY_GP");
						
						ydUtils.putLog(szClassName, szMethodName, "[STEP 2] szMATL_NO="+szMATL_NO, YdConstant.DEBUG);
						ydUtils.putLog(szClassName, szMethodName, "[STEP 2] szLOAD_LOC_CD="+szLOAD_LOC_CD, YdConstant.DEBUG);
						ydUtils.putLog(szClassName, szMethodName, "[STEP 2] szMAT_WGT="+szMAT_WGT, YdConstant.DEBUG);
						ydUtils.putLog(szClassName, szMethodName, "[STEP 2] szMAT_THK="+szMAT_THK, YdConstant.DEBUG);
						ydUtils.putLog(szClassName, szMethodName, "[STEP 2] szMAT_WTH="+szMAT_WTH, YdConstant.DEBUG);
						ydUtils.putLog(szClassName, szMethodName, "[STEP 2] szMAT_LEN="+szMAT_LEN, YdConstant.DEBUG);
						ydUtils.putLog(szClassName, szMethodName, "[STEP 2] szMAT_ODIA="+szMAT_ODIA, YdConstant.DEBUG);
						ydUtils.putLog(szClassName, szMethodName, "[STEP 2] szMAT_IDIA="+szMAT_IDIA, YdConstant.DEBUG);
						ydUtils.putLog(szClassName, szMethodName, "[STEP 2] szWORK_STATE="+szWORK_STATE, YdConstant.DEBUG);
						ydUtils.putLog(szClassName, szMethodName, "[STEP 2] szYD_CURR_BAY_GP="+szYD_CURR_BAY_GP, YdConstant.DEBUG);
						
						outRec.setField("MATL_NO"+(j+1), 		YdUtils.fillSpZr(szMATL_NO, 11, 1));
						outRec.setField("LOAD_LOC_CD"+(j+1), 		YdUtils.fillSpZr(szLOAD_LOC_CD, 2, 0));
						outRec.setField("MAT_WGT"+(j+1), 		YdUtils.fillSpZr(szMAT_WGT, 5, 0));
						
						
						// 16-4.Coil 두께 [Coil 두께]
						szMAT_THK = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara, "YD_COIL_T"), 7, 1);
						outRec.setField("MAT_THK"+(j+1)			, ydUtils.FloatLRPAD(szMAT_THK, 6, 3, '0'));	
//						outRec.setField("MAT_THK"+(j+1), 		YdUtils.fillSpZr(szMAT_THK, 6, 0));

						// 16-5.Coil 폭 [Coil 폭]
						szMAT_WTH = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara, "YD_COIL_W"), 6, 1);
						outRec.setField("MAT_WTH"+(j+1)			, ydUtils.FloatLRPAD(szMAT_WTH, 5, 1, '0'));	
//						outRec.setField("MAT_WTH"+(j+1), 		YdUtils.fillSpZr(szMAT_WTH, 5, 0));
						
						
						outRec.setField("MAT_LEN"+(j+1), 		YdUtils.fillSpZr(szMAT_LEN, 7, 0));
						outRec.setField("MAT_ODIA"+(j+1), 		YdUtils.fillSpZr(szMAT_ODIA, 5, 0));
						
						
						// 16.Coil 내경 [Coil 내경]
						szMAT_IDIA = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara, "YD_COIL_INDIA"), 6, 1);
						outRec.setField("MAT_IDIA"+(j+1)			, ydUtils.FloatLRPAD(szMAT_IDIA, 5, 1, '0'));
//						outRec.setField("MAT_IDIA"+(j+1), 		YdUtils.fillSpZr(szMAT_IDIA, 5, 0));
						
						outRec.setField("WORK_STATE"+(j+1), 		YdUtils.fillSpZr(szWORK_STATE, 1, 1));
						outRec.setField("YD_CURR_BAY_GP"+(j+1), 		YdUtils.fillSpZr(szYD_CURR_BAY_GP, 6, 1));
						
					
					i =j +1  ;
					}
					
					outRec.setField("MATL_NO"+(i+1), 		YdUtils.fillSpZr("", 11, 1));
					outRec.setField("LOAD_LOC_CD"+(i+1), 		YdUtils.fillSpZr("", 2, 1));
					outRec.setField("MAT_WGT"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
					outRec.setField("MAT_THK"+(i+1), 		YdUtils.fillSpZr("", 6, 1));
					outRec.setField("MAT_WTH"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
					outRec.setField("MAT_LEN"+(i+1), 		YdUtils.fillSpZr("", 7, 1));
					outRec.setField("MAT_ODIA"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
					outRec.setField("MAT_IDIA"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
					outRec.setField("WORK_STATE"+(i+1), 		YdUtils.fillSpZr("", 1, 1));
					outRec.setField("YD_CURR_BAY_GP"+(i+1), 		YdUtils.fillSpZr("", 6, 1));
				}else {
					outRec.setField("MATL_NO"+(i+1), 		YdUtils.fillSpZr("", 11, 1));
					outRec.setField("LOAD_LOC_CD"+(i+1), 		YdUtils.fillSpZr("", 2, 1));
					outRec.setField("MAT_WGT"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
					outRec.setField("MAT_THK"+(i+1), 		YdUtils.fillSpZr("", 6, 1));
					outRec.setField("MAT_WTH"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
					outRec.setField("MAT_LEN"+(i+1), 		YdUtils.fillSpZr("", 7, 1));
					outRec.setField("MAT_ODIA"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
					outRec.setField("MAT_IDIA"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
					outRec.setField("WORK_STATE"+(i+1), 		YdUtils.fillSpZr("", 1, 1));
					outRec.setField("YD_CURR_BAY_GP"+(i+1), 		YdUtils.fillSpZr("", 6, 1));
				}
				
			}
			
			

			// RecordSet으로 반환
			outRecSet.addRecord(outRec);

			ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 3]:: 송신 TC내용 ==============\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);

		}catch(Exception e){
			szMsg = "YDY5L008[차량작업 예정 정보] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}
		
		return outRecSet.size();

	} // end of makeY5L008()
	

	/**
	 * YDY5L008 : 차량작업 예정 정보 BackUp 화면
	 * @param JDTORecord inRec
	 * @return JDTORecordSet outRecSet
	 */
	public static int makeY5L008BackUp(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID				MSG_ID					VARCHAR2(8)		YDY5L008
		//		2.	생성일				DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간			TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분			MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이			MSG_LEN					NUMBER(4)		
		//		6.	임시				TEMP					VARCHAR2(29)
		//      7.  상차도 위치         PT_LOAD_LOC             CHAR(6)
		//      8.  차량번호            CAR_NO   				CHAR(15)
		//      9.  차량구분            PT_CLS					CHAR(2)
		//      10. 작업구분            WORK_CLS 				CHAR(1)
		//      11. 적재함 폭           PT_WTH					CHAR(4)
		//      12. 적재함 길이         PT_LEN					CHAR(5)
		//      13. 적재함 높이         PT_HEIGHT				CHAR(5)
		//      14. 우천차량 여부       RAIN_CLS				CHAR(1)
		//      15. 작업 총 수량        WORK_COIL_MAX_CNT		CHAR(2)
		//      16. 코일정보            
		//      17. 코일번호            MATL_NO					CHAR(11)
		//      18. 차량적재위치        LOAD_LOC_CD				CHAR(2)
		//      19. 코일 중량           MAT_WGT					CHAR(5)
		//      20. 코일 두께           MAT_THK					CHAR(6)
		//      21. 코일 폭             MAT_WTH					CHAR(5)
		//      22. 코일 길이           MAT_LEN					CHAR(6)
		//      23. 코일 외경           MAT_ODIA				CHAR(5)
		//      24. 코일 내경           MAT_IDIA				CHAR(5)
		//      25. 작업상태            WORK_STATE   			CHAR(1)
		//      26. 동정보              YD_CURR_BAY_GP   		CHAR(6)

		// 대차스케줄Dao 객체 생성
		YdUtils ydUtils 			= new YdUtils();
		
		// 레코드 선언
		JDTORecord outRec = null;
		
		// 변수선언
		String szMethodName 		= "makeY5L008BackUp";
		String szMsg 				= "";
		String szOperationName      = "차량작업 예정 정보BackUp";
		
		// TC Length =89
		int nTcLen 					= 821;
		int intRtnVal 				= 0;
		
		try{
			
			ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 1]::makeY5L008BackUp==============\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
			
			inRec.setField("MSG_ID", 				new String("YDY5L008") );
			// RecordSet으로 반환
			outRecSet.addRecord(inRec);

			ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 3]:: 송신 TC내용 ==============\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);

		}catch(Exception e){
			szMsg = "YDY5L008BackUp[차량작업 예정 정보] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}
		
		return outRecSet.size();

	} // end of makeY5L008BackUp()
	
	/**
	 * YDY5L009 : 이상코일 발생 정보
	 * @param JDTORecord inRec
	 * @return JDTORecordSet outRecSet
	 */
	public static int makeY5L009(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID				MSG_ID					VARCHAR2(8)		YDY5L008
		//		2.	생성일				DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간			TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분			MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이			MSG_LEN					NUMBER(4)		
		//		6.	임시				TEMP					VARCHAR2(29)
		//      7.  차량번호            CAR_NO                  CHAR(15)
		//      8.  지시번호            TRANS_ORD_SEQNO   	    NUMBER(6)	
		//      9.  이상코일발생수량    AB_COIL_NUM	    		NUMBER(1)	
		//      10. 이상코일번호        AB_STL_NO1 				CHAR(11)
		//      11. 이상코드            YD_AB_CD1				NUMBER(3)
		//      12. 이상코드상세        YD_AB_CD_DETAIL1		NUMBER(2)
		//      13. 이상코일번호        AB_STL_NO1 				CHAR(11)
		//      14. 이상코드            YD_AB_CD1				NUMBER(3)
		//      15. 이상코드상세        YD_AB_CD_DETAIL1		NUMBER(2)
		//      16. 이상코일번호        AB_STL_NO1 				CHAR(11)
		//      17. 이상코드            YD_AB_CD1				NUMBER(3)
		//      18. 이상코드상세        YD_AB_CD_DETAIL1		NUMBER(2)

		YdCommDAO ydCommDao = new YdCommDAO();
		YdUtils ydUtils 			= new YdUtils();

		// 조회 결과를 담을 RecordSet생성
		JDTORecordSet rsGetYdCarList = JDTORecordFactory.getInstance().createRecordSet("");

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord outRec = null;

		// 변수선언
		String szMethodName 		= "makeY5L009";
		String szMsg 				= "";
		String szOperationName      = "이상코일 발생정보";

		
		// TC Length =89
		int nTcLen 					= 70;
		int intRtnVal 				= 0;
		
		try{
			
			ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 1]::makeY5L009==============\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			
			String queryId = "com.inisteel.cim.yd.common.dao.YdCommDAO.getYDY5L009";
			intRtnVal = ydCommDao.jspSelect(inRec, rsGetYdCarList,queryId, "getSelectData");
			
			if(intRtnVal <= 0){		
				szMsg = "makeY5L009 전문대상 조회건수 없음";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				return 0;
			}
			rsGetYdCarList.absolute(1);
			recPara = JDTORecordFactory.getInstance().create();
			recPara = rsGetYdCarList.getRecord();
	
			outRec = JDTORecordFactory.getInstance().create();
			
			outRec.setField("MSG_ID", 				new String("YDY5L009") );
			outRec.setField("DATE", 				new String(YdUtils.getCurDate("yyyy-MM-dd")) );
			outRec.setField("TIME", 				new String(YdUtils.getCurDate("HH-mm-ss")) );
			outRec.setField("MSG_GP", 				new String("I") );

			outRec.setField("MSG_LEN", 				new String(YdUtils.fillSpZr(""+nTcLen, 4, 0)) );
			outRec.setField("TEMP", 				new String(YdUtils.fillSpZr("", 29, 1)) );
			
			outRec.setField("CAR_NO", 				YdUtils.fillSpZr_KOR(ydDaoUtils.paraRecChkNull(recPara , "CAR_NO"), 15, 1));
			outRec.setField("TRANS_ORD_SEQNO", 				YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara , "TRANS_ORD_SEQNO"), 6, 1));
			outRec.setField("AB_COIL_NUM", 			YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara , "AB_COIL_NUM"), 1, 0));
			outRec.setField("STL_NO1", 				YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara , "STL_NO1"), 11, 1));
			outRec.setField("YD_AB_CD1", 			YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara , "YD_AB_CD1"), 3, 0));
			outRec.setField("YD_AB_CD_DETAIL1", 	YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara , "YD_AB_CD_DETAIL1"), 2, 0));
			outRec.setField("STL_NO2", 				YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara , "STL_NO2"), 11, 1));
			outRec.setField("YD_AB_CD2", 			YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara , "YD_AB_CD2"), 3, 0));
			outRec.setField("YD_AB_CD_DETAIL2", 	YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara , "YD_AB_CD_DETAIL2"), 2, 0));
			outRec.setField("STL_NO3",		 		YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara , "STL_NO3"), 11, 1));
			outRec.setField("YD_AB_CD3", 			YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara , "YD_AB_CD3"), 3, 0));
			outRec.setField("YD_AB_CD_DETAIL3", 	YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recPara , "YD_AB_CD_DETAIL3"), 2, 0));
			
			// RecordSet으로 반환
			outRecSet.addRecord(outRec);

			ydUtils.putLog(szClassName, szMethodName, "\n================[STEP 2]:: 송신 TC내용 ==============\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			
		
		}catch(Exception e){
			szMsg = "YDY5L009[이상코일발생정보] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}
		
		return outRecSet.size();

	} // end of makeY5L009()
	
  //---------------------------------------------------------------------------	
} // end of class MakeTcY5


