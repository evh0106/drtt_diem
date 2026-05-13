/**
 * @(#)MakeTcS1.java
 * 
 * @version			1.0
 * @author 			조병기
 * @date			2013.05.22
 * 
 * @description		PP (2후판조업) 송신 용 전문 생성 클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2013.05.22   조병기      조병기      최초 등록
 * 
 */

package com.inisteel.cim.yd.common.util.tcconst;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdConstant;


public class MakeTcPP {
	// YDPPJ001 후판제품반납하차실적 	
	
	//클래스명
	private static final String szClassName  = MakeTcPR.class.getName();
	
	
	/**
	 * YDPPJ001 : 2후판제품반납하차실적 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makePPJ001(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 			VARCHAR2(8)			YDPPJ001
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시			DATE	(14)		YYYYMMDDHHMMSS (24시간개념)
		
		//		3	TRN_EQP_CD          운송장비코드			VARCHAR2(8)
		//		4	SPOS_WLOC_CD        발지개소코드			VARCHAR2(7)
		//		5	SPOS_YD_PNT_CD      발지야드포인트코드		VARCHAR2(4)
		//		6	ARR_WLOC_CD         착지개소코드			VARCHAR2(7)
		//		7	TRN_WRK_MTL_GP      운송작업재료구분		VARCHAR2(1)
		//		8	MTL_UGNT_GP         재료긴급구분			VARCHAR2(1)
		//		9	HCR_GROUP           HCR구분				VARCHAR2(1)
		//		10	CARLD_SH            상차매수				NUMBER  (22)
		//		------------------------- Group[100] --------------------------------
		//		11	STL_NO	재료번호							VARCHAR2(11)
		//		12	STL_WT	재료중량							NUMBER  (5)
		//		------------------------- Group[100] --------------------------------
		//		209	CARLD_CMPL_DT       상차완료일시			VARCHAR2(14)
		
		// 레코드 선언
		JDTORecord recPara               = null;
		JDTORecord recParaCol            = null;
		JDTORecordSet rsResult           = null;
		JDTORecordSet rsResultCol        = null;
		JDTORecord recGetVal             = null;
		JDTORecord recGetValCol          = null;
		JDTORecord outRec                = null;

		// DAO객체 생성
		YdCrnSchDao ydCrnSchDao          = new YdCrnSchDao();
		YdCarSchDao ydCarSchDao          = new YdCarSchDao();
		YdStkColDao ydStkColDao          = new YdStkColDao();
		YdDaoUtils ydDaoUtils            = new YdDaoUtils();	
		YdUtils ydUtils                  = new YdUtils();

		// 변수선언
		String szMethodName              = "makePPJ001";
		String szMsg                     = "";
		String szOperationName           = "2후판조업 후판제품반납하차실적";
		
		
		// 야드설비작업상태 (L/U)
		String szEqpWrkStat              = "";
				
		// 발지야드포인트코드
		String szSposYdPndCd             = "";
				
		// 크레인 스케줄ID
		String szYD_CRN_SCH_ID           = "";

		// 차량 스케줄ID
		String szYD_CAR_SCH_ID           = "";
		
		// 운송장비코드
		String szTRN_EQP_CD              = "";

		// 발지개소코드
		String szSPOS_WLOC_CD            = "";

		// 착지개소코드
		String szARR_WLOC_CD             = "";
	
		// 운송작업재료구분
		String szYD_MTL_ITEM             = "";

		// 재료긴급구분
		String szURGENT_FRTOMOVE_WORD_GP = "";

		// HCR구분
		String szHCR_GP                  = "";

		// 상차매수
		String szYD_EQP_WRK_SH           = "";
		
		// 상차완료일시
		String szCARLD_CMPL_DT           = "";

		// 적치열구분
		String szYD_STK_COL_GP           = "";

		// 리턴값
		int intRtnVal                    = 0;
		int intRtnValCol                 = 0;
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);															
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
			
			
			// 리턴 recordSet 생성 및 레코드 생성
			rsResult    = JDTORecordFactory.getInstance().createRecordSet("");
			recPara     = JDTORecordFactory.getInstance().create();			
			rsResultCol = JDTORecordFactory.getInstance().createRecordSet("");
			recParaCol  = JDTORecordFactory.getInstance().create();			
			
			
			// 전문에서 항목 추출
			szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID");
			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(inRec, "YD_CAR_SCH_ID");
			
			szMsg = "수신항목 YD_CRN_SCH_ID(" + szYD_CRN_SCH_ID + ") YD_CAR_SCH_ID(" + szYD_CAR_SCH_ID + ")";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			
			// 존재 항목에 따른 분기
			if(!szYD_CRN_SCH_ID.equals("")){
				// 수신전문에 크레인스케줄ID가 있었다면
				recPara.setField("YD_AIM_RT_GP", "");
				recPara.setField("YD_CRN_SCH_ID" , szYD_CRN_SCH_ID);
				intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsResult, 10);
				if(intRtnVal < 0) {
					szMsg = "크레인스케쥴 테이블 조회오류  [Ret : " + intRtnVal + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					return 0;
				} else if(intRtnVal == 0) {
					szMsg = "크레인스케쥴 테이블 조회건수 없음  [Ret : " + intRtnVal + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					return 0;
				}		
								
				// 레코드 생성
				outRec = JDTORecordFactory.getInstance().create();						

				// 헤더부
				outRec.setField("JMS_TC_CD",          "YDPPJ001");
				outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));			
				
				outRec.setField("TRN_EQP_CD"    , "");										
				outRec.setField("SPOS_WLOC_CD"  , "");
				outRec.setField("SPOS_YD_PNT_CD", "");
				outRec.setField("ARR_WLOC_CD"   , "");
				outRec.setField("TRN_WRK_MTL_GP", "");
				outRec.setField("MTL_UGNT_GP"   , "");
				outRec.setField("HCR_GROUP"     , "");
				outRec.setField("CARLD_SH"      , "" + intRtnVal);   // 조회매수

				for(int nIdx=0; nIdx<intRtnVal; nIdx++) {
					recGetVal = rsResult.getRecord(nIdx);
		
					// 재료번호 [재료번호]
					outRec.setField("STL_NO" + (1+nIdx), ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO"));
	
					// 재료중량 [재료중량]
					outRec.setField("STL_WT" + (1+nIdx), ydDaoUtils.paraRecChkNull(recGetVal, "YD_MTL_WT"));				
				}
				
				// 상차완료일시 [상차완료일시]
				outRec.setField("CARLD_CMPL_DT" , "");
			} else if(!szYD_CAR_SCH_ID.equals("")){
				// 수신전문에 차량스케줄ID가 있었다면
				//=======================================================================================================================
				// 차량스케쥴 테이블 조회 (Key: 차량스케쥴 ID)
				//=======================================================================================================================
				// 레코드 생성
				outRec = JDTORecordFactory.getInstance().create();						

				recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult, 4);		
				if(intRtnVal < 0) {
					szMsg = "차량스케쥴 테이블 조회오류  [Ret : " + intRtnVal + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					return 0;
				} else if(intRtnVal == 0) {
					szMsg = "차량스케쥴 테이블 조회건수 없음  [Ret : " + intRtnVal + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					return 0;
				}			
				
				// 첫번째 레코드 선택하여 공통적 사용항목을 가져오기
				rsResult.first();
				recGetVal = rsResult.getRecord();

				
				// 레코드에서 항목 추출
				szTRN_EQP_CD              = ydDaoUtils.paraRecChkNull(recGetVal, "TRN_EQP_CD");
				szSPOS_WLOC_CD            = ydDaoUtils.paraRecChkNull(recGetVal, "SPOS_WLOC_CD");
				szEqpWrkStat              =  ydDaoUtils.paraRecChkNull(recGetVal, "YD_EQP_WRK_STAT").trim();
				szYD_STK_COL_GP           = ydDaoUtils.paraRecChkNull(recGetVal, "YD_CARLD_STOP_LOC").trim();
				szARR_WLOC_CD             = ydDaoUtils.paraRecChkNull(recGetVal, "ARR_WLOC_CD");
				szYD_MTL_ITEM             = ydDaoUtils.paraRecChkNull(recGetVal, "YD_MTL_ITEM");
				szURGENT_FRTOMOVE_WORD_GP = ydDaoUtils.paraRecChkNull(recGetVal, "URGENT_FRTOMOVE_WORD_GP");
				szHCR_GP                  = ydDaoUtils.paraRecChkNull(recGetVal, "HCR_GP");
				szYD_EQP_WRK_SH           = ydDaoUtils.paraRecChkNull(recGetVal, "YD_EQP_WRK_SH");
				szCARLD_CMPL_DT           = ydDaoUtils.paraRecChkNull(recGetVal, "YD_CARLD_CMPL_DT");
				
				
				// 헤더부
				outRec.setField("JMS_TC_CD"         , "YDPPJ001");
				outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));			

				// 운송장비코드 [운송장비코드]
				outRec.setField("TRN_EQP_CD"        , szTRN_EQP_CD);										
				
				// 발지개소코드 [발지개소코드]
				outRec.setField("SPOS_WLOC_CD"      , szSPOS_WLOC_CD);

				// 발지야드포인트코드 [상차정지위치 = 적치열구분]
				if(szEqpWrkStat != "" && szEqpWrkStat.equals("U")){
					recParaCol.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
					intRtnValCol = ydStkColDao.getYdStkcol(recParaCol, rsResultCol, 0);
					if(intRtnValCol > 0){
						for(int nIdx2=0; nIdx2<intRtnValCol; nIdx2++) {
							recGetValCol = rsResultCol.getRecord(nIdx2);
							szSposYdPndCd = ydDaoUtils.paraRecChkNull(recGetValCol, "YD_PNT_CD");
						}
					} else if(intRtnValCol == 0){
						szMsg = "적치열 테이블 조회건수 없음  [Ret : " + intRtnValCol + "]";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);					
					} else if(intRtnValCol < 0){
						szMsg = "적치열 테이블 조회오류  [Ret : " + intRtnValCol + "]";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);					
					}
				}
				outRec.setField("SPOS_YD_PNT_CD", szSposYdPndCd);

				// 착지개소코드 [착지개소코드]
				outRec.setField("ARR_WLOC_CD"   , szARR_WLOC_CD);

				// 운송작업재료구분 [야드재료품목]
				outRec.setField("TRN_WRK_MTL_GP", szYD_MTL_ITEM);

				// 재료긴급구분 [긴급이송지작업지시구분]
				outRec.setField("MTL_UGNT_GP"   , szURGENT_FRTOMOVE_WORD_GP);

				// HCR구분 [HCR구분]
				outRec.setField("HCR_GROUP"     , szHCR_GP);

				// 상차매수 [야드설비작업매수]
				outRec.setField("CARLD_SH"      , szYD_EQP_WRK_SH);


				for(int nIdx=0; nIdx<intRtnVal; nIdx++) {
					recGetVal = rsResult.getRecord(nIdx);
		
					// 재료번호 [재료번호]
					outRec.setField("STL_NO" + (1+nIdx), ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO"));
	
					// 재료중량 [재료중량]
					outRec.setField("STL_WT" + (1+nIdx), ydDaoUtils.paraRecChkNull(recGetVal, "YD_MTL_WT"));				
				}
				
				// 상차완료일시 [상차완료일시]
				outRec.setField("CARLD_CMPL_DT", szCARLD_CMPL_DT);
			}

			// RecordSet에 추가				
			outRecSet.addRecord(outRec);

			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	
			
		}catch(Exception e){
			szMsg = "PP(2후판조업) 송신 후판제품반납하차실적  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);							
			return -1;
		}	
		
		return outRecSet.size();
	} // end of makePPJ001()
	
	
	
} // end of class MakeTcPP


