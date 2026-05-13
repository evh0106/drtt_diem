package com.inisteel.cim.yd.common.util.tcconst;

// DAO import
import java.util.List;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;





/**
 * TS (구내운송) 송신 용 전문 생성
 * @author YHWHman
 *
 */
public class MakeTcTS {	
	// YDTSJ007 소재차량상차개시	
	// YDTSJ008 소재차량상차완료	
	// YDTSJ009 소재차량하차개시	
	// YDTSJ010 소재차량하차완료	
	// YDTSJ011 소재차량Point지시	
	// YDTSJ012 소재차량Point개폐	
	// YDTSJ013 소재차량상하차지연사유	
	// YDTSJ014 여재 Slab 소재운송요구
	// YDTSJ015 제품운송요구(후판제품이송지시)
	
	
	// 클래스명
	private static final String szClassName  = MakeTcTS.class.getName();

	
	
	
	/**
	 * YDTSJ007 : 소재차량상차개시 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeTSJ007(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 			VARCHAR2(8)			YDTSJ007
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시			DATE	(14)		YYYYMMDDHHMMSS (24시간개념)

		//		3.	TRN_EQP_CD			운송장비코드			VARCHAR2(8)
		//		4.	SPOS_WLOC_CD        발지개소코드			VARCHAR2(5)
		//		5.	SPOS_YD_PNT_CD      발지야드포인트코드		VARCHAR2(4)
		//		6.	ARR_WLOC_CD         착지개소코드			VARCHAR2(5)
		//		7.	TRN_WRK_ST_DT		운송작업시작일시		VARCHAR2(14)
		
		// 레코드 선언
		JDTORecord recPara        = null;
		JDTORecord recParaCol     = null;
		JDTORecordSet rsResult    = null;
		JDTORecordSet rsResultCol = null;
		JDTORecord recGetVal      = null;
		JDTORecord recGetValCol   = null;
		JDTORecord outRec         = null;		

		// DAO객체 생성
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		YdStkColDao ydStkColDao   = new YdStkColDao();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();
		
		// 메소드명
		String szMethodName       = "makeTSJ007";
		String szMsg              = "";
		String szOperationName    = "구내운송 소재차량상차개시";
				
		// 발지야드포인트코드
		String szSPOS_WLOC_CD	  = null;
		String szSposYdPndCd      = "";
		String szARR_WLOC_CD 	  = null;
		
		
		// 리턴값
		int intRtnVal             = 0;
		int intRtnValCol          = 0;
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);	
			
			// 리턴 Record 생성 및 Record 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();		
			rsResultCol = JDTORecordFactory.getInstance().createRecordSet("");
			recParaCol = JDTORecordFactory.getInstance().create();		
			
			//=======================================================================================================================
			// 차량스케쥴 테이블 조회 (Key: 차량스케쥴 ID) - 기본쿼리 썼음  한건만 조회될 것임
			//=======================================================================================================================
			recPara.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CAR_SCH_ID"));
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult, 0);
			if (intRtnVal < 0) {
				szMsg = "차량스케쥴 조회 오류  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);												
				return 0;
			} else if (intRtnVal == 0) {
				szMsg = "차량스케쥴 조회건수 없음  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);												
				return 0;
			}			
			
			String szWLOC_CD = ydDaoUtils.paraRecChkNull(inRec, "ARR_WLOC_CD");
			
			rsResult.first();
			recGetVal = rsResult.getRecord();
			
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(recGetVal, "SPOS_WLOC_CD");

			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("JMS_TC_CD", 			"YDTSJ007");
			outRec.setField("JMS_TC_CREATE_DDTT",   YdUtils.getCurDate("yyyyMMddHHmmss"));
			
			// 운송장비코드 [운송장비코드]
			outRec.setField("TRN_EQP_CD", ydDaoUtils.paraRecChkNull(recGetVal, "TRN_EQP_CD"));										
			
			// 발지개소코드 [발지개소코드]
			outRec.setField("SPOS_WLOC_CD", szSPOS_WLOC_CD);
			
			if( szSPOS_WLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT) ||
				szSPOS_WLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT)	) {
				szSposYdPndCd = YdConstant.WLOC_CD_A_PLATE_PLANT_PNT_CD;
			}else if( szSPOS_WLOC_CD.equals(YdConstant.WLOC_CD_C_HR_PLANT) ) {
				szSposYdPndCd = YdConstant.WLOC_CD_C_HR_PLANT_PNT_CD;
			}else{
				// 발지 야드포인트코드 [상차정지위치 = 적치열구분]
				recParaCol.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recGetVal, "YD_CARLD_STOP_LOC"));
				intRtnValCol = ydStkColDao.getYdStkcol(recParaCol, rsResultCol, 0);
				if(intRtnValCol > 0){
					for(int nIdx2=0; nIdx2<intRtnValCol; nIdx2++) {
						recGetValCol = rsResultCol.getRecord(nIdx2);
						szSposYdPndCd = ydDaoUtils.paraRecChkNull(recGetValCol, "YD_PNT_CD");
					}
				} else if(intRtnValCol == 0){
					szMsg = "적치열 테이블  조회건수 없음  [Ret : " + intRtnVal + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);					
				} else if(intRtnValCol < 0){
					szMsg = "적치열 테이블 조회 오류  [Ret : " + intRtnVal + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
				}
			}
			
			outRec.setField("SPOS_YD_PNT_CD", szSposYdPndCd);
			
			// 착지개소코드 [착지개소코드]
			szARR_WLOC_CD = ydDaoUtils.paraRecChkNull(recGetVal, "ARR_WLOC_CD");
			if(szARR_WLOC_CD.equals("")) 
				szARR_WLOC_CD = szWLOC_CD;
			outRec.setField("ARR_WLOC_CD", szARR_WLOC_CD);

			// 운송작업시작일시 [상차개시일시]
			outRec.setField("TRN_WRK_ST_DT", ydDaoUtils.paraRecChkNull(recGetVal, "YD_CARLD_ST_DT"));
			
			// RecordSet에 추가				
			outRecSet.addRecord(outRec);
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================OUT==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, outRec);				
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);	
		}catch(Exception e){
			szMsg = "TS(구내운송)송신 소재차량상차개시 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}	
	
		return intRtnVal;
	} // end of makeTSJ007()
		
	
	
	
	
	/**
	 * YDTSJ008 : 소재차량상차완료 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeTSJ008(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 			VARCHAR2(8)			YDTSJ008
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시			DATE	(14)		YYYYMMDDHHMMSS (24시간개념)
		
		//		3	TRN_EQP_CD          운송장비코드			VARCHAR2(8)			구내운송 차량 제원 등록 NO.
		//		4	SPOS_WLOC_CD        발지개소코드			VARCHAR2(5)			상차작업을 수행한 개소코드
		//		5	SPOS_YD_PNT_CD      발지야드포인트코드		VARCHAR2(4)			상차작업을 수행한 포인트코드
		//		6	ARR_WLOC_CD         착지개소코드			VARCHAR2(5)			하차작업을 수행할 개소코드
		//		7	TRN_WRK_MTL_GP      운송작업재료구분		VARCHAR2(1)			S/L("S"),B/T("T"),  H/C("H"), C/C("C") 재료외형활용
		//		8	MTL_UGNT_GP         재료긴급구분			VARCHAR2(1)			긴급재("Y"), 기타("N")
		//		9	HCR_GP              HCR구분				VARCHAR2(1)			HCR("H"),WCR("W"),일반재("C")
		//		10	CARLD_SH            상차매수				NUMBER  (22)		상차 매수
		//		---------------------- GROUP[100] -------------------------
		//		11	STL_NO1				재료번호1~100			VARCHAR2(11)		운송지시 Table 내에 저장된 재료번호
		//		12	STL_WT1				재료중량1~100			NUMBER  (5)			단위[Kg]
		//		-----------------------------------------------------------
		//		13	CARLD_CMPL_DT       야드상차완료일시		DATE	(14)		YYYYMMDDHHMMSS	
		
		// 레코드 선언
		JDTORecord recPara        = null;
		JDTORecord recParaCol     = null;
		JDTORecordSet rsResult    = null;
		JDTORecordSet rsResultCol = null;
		JDTORecord recGetVal      = null;
		JDTORecord recGetValCol   = null;
		JDTORecord outRec         = null;	
		
		// DAO객체 생성
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		YdStkColDao ydStkColDao   = new YdStkColDao();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeTSJ008";
		String szMsg              = "";
		String szOperationName    = "구내운송 소재차량상차완료";
		
		// 발지야드포인트코드
		String szSPOS_WLOC_CD		= null;
		String szSposYdPndCd      = "";
		
		String szYD_MTL_ITEM      = "";
		
		// 리턴값
		int intRtnVal             = 0;
		int intRtnValCol          = 0;
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, inRec);				
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);
			
			// 리턴 Record 생성 및 Record 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			rsResultCol = JDTORecordFactory.getInstance().createRecordSet("");
			recParaCol = JDTORecordFactory.getInstance().create();
					
			//=======================================================================================================================
			// 차량스케쥴 테이블 조회 (Key: 차량스케쥴 ID)
			//=======================================================================================================================
			recPara.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CAR_SCH_ID"));
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult, 4);		
			if(intRtnVal < 0) {
				szMsg = "차량스케쥴 조회 오류  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);						
				return 0;
			} else if(intRtnVal == 0) {
				szMsg = "차량스케쥴 조회건수 없음  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);						
				return 0;
			}			
			
			outRec = JDTORecordFactory.getInstance().create();	
			
			rsResult.first();
			recGetVal = rsResult.getRecord();
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(recGetVal, "SPOS_WLOC_CD");

			for(int nIdx=0; nIdx<intRtnVal; nIdx++) {
				recGetVal = rsResult.getRecord(nIdx);
				
				// 헤더부
				outRec.setField("JMS_TC_CD", 			"YDTSJ008");
				outRec.setField("JMS_TC_CREATE_DDTT", 	YdUtils.getCurDate("yyyyMMddHHmmss"));
			
				// 운송장비코드 [운송장비코드]
				outRec.setField("TRN_EQP_CD", ydDaoUtils.paraRecChkNull(recGetVal, "TRN_EQP_CD"));										

				// 발지개소코드 [발지개소코드]
				outRec.setField("SPOS_WLOC_CD", szSPOS_WLOC_CD);
				if( szSPOS_WLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT)||
					szSPOS_WLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT)	) {
					szSposYdPndCd = YdConstant.WLOC_CD_A_PLATE_PLANT_PNT_CD;
				}else if( szSPOS_WLOC_CD.equals(YdConstant.WLOC_CD_C_HR_PLANT) ) {
					szSposYdPndCd = YdConstant.WLOC_CD_C_HR_PLANT_PNT_CD;
				}else{
					// 발지 야드포인트코드 [상차정지위치 = 적치열구분]
					recParaCol.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recGetVal, "YD_CARLD_STOP_LOC"));
					intRtnValCol = ydStkColDao.getYdStkcol(recParaCol, rsResultCol, 0);
					if(intRtnValCol > 0){
						for(int nIdx2=0; nIdx2<intRtnValCol; nIdx2++) {
							recGetValCol = rsResultCol.getRecord(nIdx2);
							szSposYdPndCd = ydDaoUtils.paraRecChkNull(recGetValCol, "YD_PNT_CD");
						}
					} else if(intRtnValCol == 0){
						szMsg = "적치열 테이블  조회건수 없음  [Ret : " + intRtnVal + "]";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);					
					} else if(intRtnValCol < 0){
						szMsg = "적치열 테이블 조회 오류  [Ret : " + intRtnVal + "]";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
					}				
				}
				outRec.setField("SPOS_YD_PNT_CD", szSposYdPndCd);
				
				// 착지개소코드 [착지개소코드]
				outRec.setField("ARR_WLOC_CD", ydDaoUtils.paraRecChkNull(recGetVal, "ARR_WLOC_CD"));

				// 운송작업재료구분 
				// [*** 2009.08.31 권오창 재료외형으로 하는것이 아니고 예전에 했던  재료품목에서 앞자리 1자리 읽어서 처리 ***]
				// [*** 2009.09.02 임석만부장님이 CG=>C  CM=>H  SM,SH=>S  PG=>T로 보내야 된다고 했음  ***]
				/*
				 * 항목이 들어가지 않는 부분이 발생하여 수정
				 * 수정자 : 임춘수
				 * 수정일 : 2009.09.21
				 */
				szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(recGetVal, "YD_MTL_ITEM");
				if(szYD_MTL_ITEM == null || szYD_MTL_ITEM.equals("")){
					outRec.setField("TRN_WRK_MTL_GP", YdUtils.fillSpZr(" ", 1, 1));						
					szMsg = "[MakeTcTS::makeTSJ008()] YD_MTL_ITEM 항목의 값이 없음";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);						
				} else if(szYD_MTL_ITEM.equals("SP") 			/*후판슬라브*/
						|| szYD_MTL_ITEM.equals("SH") 			/*열연슬라브*/
						|| szYD_MTL_ITEM.equals("SG") 			/*외판슬라브*/
						|| szYD_MTL_ITEM.equals("SM") 			/*Slab소재*/
						|| szYD_MTL_ITEM.equals("SZ") 			/*후판 Sizing Slab*/
						|| szYD_MTL_ITEM.equals("BP") 			/*후판주편*/
						|| szYD_MTL_ITEM.equals("BH") 			/*열연주편*/
						|| szYD_MTL_ITEM.equals("BK") 			/*후판비축주편*/
						|| szYD_MTL_ITEM.equals("BM")			/*주편*/
						){		
					outRec.setField("TRN_WRK_MTL_GP", YdUtils.fillSpZr("S", 1, 1));				//SLAB			
				} else if(szYD_MTL_ITEM.equals("PG") 			/*후판제품*/
						|| szYD_MTL_ITEM.equals("PT")			/*후판미입고제품*/
						){										
					outRec.setField("TRN_WRK_MTL_GP", YdUtils.fillSpZr("T", 1, 1));				//후판제품				
				} else if(szYD_MTL_ITEM.equals("CM")){
					outRec.setField("TRN_WRK_MTL_GP", YdUtils.fillSpZr("H", 1, 1));				//열연COIL
				} else if(szYD_MTL_ITEM.equals("CG")){
					outRec.setField("TRN_WRK_MTL_GP", YdUtils.fillSpZr("C", 1, 1));				//COIL제품
				}
				
				// 재료긴급구분 [긴급이송지작업지시구분]
				outRec.setField("MTL_UGNT_GP", ydDaoUtils.paraRecChkNull(recGetVal, "URGENT_FRTOMOVE_WORD_GP"));

				// HCR구분 [HCR구분]   2009.06.12 김진욱수정  HCR_GROUP -> HCR_GP로 변경
				outRec.setField("HCR_GP", ydDaoUtils.paraRecChkNull(recGetVal, "HCR_GP"));

				// 상차매수 [야드설비작업매수] 2009.06.12 김진욱수정 .trim()추가 (구내운송의 요청!! 빈공백은 빼달라고 요청이 옴...)
				outRec.setField("CARLD_SH", Integer.toString(intRtnVal).trim());

				// 재료번호 [재료번호]
				outRec.setField("STL_NO" + (1+nIdx), ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO"));

				// 재료중량 [재료중량]
				outRec.setField("STL_WT" + (1+nIdx), ydDaoUtils.paraRecChkNull(recGetVal, "YD_MTL_WT"));				
				
				/////// 21.09.06 통합야드  상차시 구내운송으로 차상위치 전송 추가
				/////// 21.09.15 슬라브야드 상차시 구내운송으로 차상위치 전송 추가
				//String ydGp = ydDaoUtils.paraRecChkNull(recGetVal, "YD_GP");
				//if("S".equals(ydGp)){
				outRec.setField("STL_LOC" + (1+nIdx), ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_LYR_NO"));			
				//}
				  
				 
				//////
				// 상차완료일시 [상차완료일시] 2009.06.12 김진욱수정  YD_CARLD_END_DT -> CARLD_CMPL_DT로 변경
				outRec.setField("CARLD_CMPL_DT", ydDaoUtils.paraRecChkNull(recGetVal, "YD_CARLD_CMPL_DT"));
			}			

			// RecordSet에 추가 (STL구분으로 모두 처리 한건)				
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================OUT==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, outRec);				
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);			
		}catch(Exception e){
			szMsg = "TS(구내운송)송신 소재차량상차완료  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}	
				
		return outRecSet.size();
	} // end of makeTSJ008()
	

	
	
	
	/**
	 * YDTSJ009 : 소재차량하차개시 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeTSJ009(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 			VARCHAR2(8)			YDTSJ009
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시			DATE	(14)    	YYYYMMDDHHMMSS (24시간개념)	// 레코드 선언

		//		3.	TRN_EQP_CD			운송장비코드			VARCHAR2(8)
		//		4.	ARR_WLOC_CD         착지개소코드			VARCHAR2(5)
		//		5.	ARR_YD_PNT_CD       착지야드포인트코드		VARCHAR2(4)
		//		6.	TRN_WRK_ST_DT		운송작업시작일시		VARCHAR2(14)
		
		// 레코드 선언		
		JDTORecord recPara        = null;
		JDTORecord recParaCol     = null;
		JDTORecordSet rsResult    = null;
		JDTORecordSet rsResultCol = null;
		JDTORecord recGetVal      = null;
		JDTORecord recGetValCol   = null;
		JDTORecord outRec         = null;	
		
		// DAO객체 생성
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		YdStkColDao ydStkColDao   = new YdStkColDao();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeTSJ009";
		String szMsg              = "";
		String szOperationName    = "구내운송 소재차량하차개시";
		
		//착지개소코드
		String szARR_WLOC_CD		= "";
		// 착지야드포인트코드
		String szArrYdPndCd       = "";
		//하차개시일시
		String szYD_CARUD_ST_DT				= null;
		
		// 리턴값
		int intRtnVal             = 0;
		int intRtnValCol          = 0;	
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, inRec);							
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);			

			// 리턴 RecordSet 생성 및 Record 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			rsResultCol = JDTORecordFactory.getInstance().createRecordSet("");
			recParaCol = JDTORecordFactory.getInstance().create();
						
			//=======================================================================================================================
			// 차량스케쥴 테이블 조회 (Key: 차량스케쥴 ID) - 기본쿼리 썼음  한건만 조회될 것임
			//=======================================================================================================================
			recPara.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CAR_SCH_ID"));
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult, 0);
			if(intRtnVal < 0) {
				szMsg = "차량스케쥴 조회 오류  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);						
				return 0;
			} else if(intRtnVal == 0) {
				szMsg = "차량스케쥴 조회건수 없음  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);						
				return 0;
			}							
			
			String szUD_ST_DT = ydDaoUtils.paraRecChkNull(inRec, "YD_CARUD_ST_DT");

			rsResult.first();
			recGetVal = rsResult.getRecord();
			
			szARR_WLOC_CD = ydDaoUtils.paraRecChkNull(recGetVal, "ARR_WLOC_CD");
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();			
			outRec.setField("JMS_TC_CD", 			"YDTSJ009");
			outRec.setField("JMS_TC_CREATE_DDTT", 	YdUtils.getCurDate("yyyyMMddHHmmss"));

			// 운송장비코드 [운송장비코드]
			outRec.setField("TRN_EQP_CD", ydDaoUtils.paraRecChkNull(recGetVal, "TRN_EQP_CD"));										

			// 착지개소코드 [착지개소코드]
			outRec.setField("ARR_WLOC_CD", szARR_WLOC_CD);
			
			
			if( szARR_WLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT)||
				szARR_WLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT)) {
				szArrYdPndCd = YdConstant.WLOC_CD_A_PLATE_PLANT_PNT_CD;
			}else if( szARR_WLOC_CD.equals(YdConstant.WLOC_CD_C_HR_PLANT) ) {
				szArrYdPndCd = YdConstant.WLOC_CD_C_HR_PLANT_PNT_CD;
			}else{
				// 착지야드포인트코드 [하차정지위치 = 적치열구분]
				recParaCol.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recGetVal, "YD_CARUD_STOP_LOC"));
				intRtnValCol = ydStkColDao.getYdStkcol(recParaCol, rsResultCol, 0);
				if(intRtnValCol > 0){
					for(int nIdx2=0; nIdx2<intRtnValCol; nIdx2++) {
						recGetValCol = rsResultCol.getRecord(nIdx2);
						szArrYdPndCd = ydDaoUtils.paraRecChkNull(recGetValCol, "YD_PNT_CD");
					}
				} else if(intRtnValCol == 0){
					szMsg = "적치열 테이블  조회건수 없음  [Ret : " + intRtnVal + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);					
				} else if(intRtnValCol < 0){
					szMsg = "적치열 테이블 조회 오류  [Ret : " + intRtnVal + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
				}	
			}
			
			outRec.setField("ARR_YD_PNT_CD", szArrYdPndCd);
			
			// 운송작업시작일시 [하차개시일시] 
			szYD_CARUD_ST_DT = ydDaoUtils.paraRecChkNull(recGetVal, "YD_CARUD_ST_DT");
			if(szYD_CARUD_ST_DT.equals("")) 
				szYD_CARUD_ST_DT = szUD_ST_DT;
			outRec.setField("TRN_WRK_ST_DT", szYD_CARUD_ST_DT);

			// RecordSet에 추가				
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================OUT==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, outRec);							
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);	
		}catch(Exception e){
			szMsg = "TS(구내운송)송신 소재차량하차개시  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);							
			return -1;
		}	
		
		return intRtnVal;
	} // end of makeTSJ009()
		
	
	
	
	
	/**
	 * YDTSJ010 : 소재차량하차완료 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeTSJ010(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 			VARCHAR2(8)			YDTSJ010
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시			DATE	(14)		YYYYMMDDHHMMSS (24시간개념)

		//		3	TRN_EQP_CD          운송장비코드			VARCHAR2(8)
		//		4	ARR_WLOC_CD         착지개소코드			VARCHAR2(5)
		//		5	ARR_YD_PNT_CD		착지야드포인트코드		VARCHAR2(4)
		//		6	CARUD_CMPL_DT       하차완료일시			VARCHAR2(14)
		
		// 레코드 선언
		JDTORecord recPara        = null;
		JDTORecord recParaCol     = null;
		JDTORecordSet rsResult    = null;
		JDTORecordSet rsResultCol = null;
		JDTORecord recGetVal      = null;
		JDTORecord recGetValCol   = null;
		JDTORecord outRec         = null;	
		
		// DAO객체 생성
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		YdStkColDao ydStkColDao   = new YdStkColDao();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeTSJ010";
		String szMsg              = "";
		String szOperationName    = "구내운송 소재차량하차완료";
		
		//착지개소코드
		String szARR_WLOC_CD		= "";
		// 착지야드포인트코드
		String szArrYdPndCd       = "";
		
		// 리턴값
		int intRtnVal             = 0;
		int intRtnValCol          = 0;

		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);
			
			// 리턴 RecordSet 생성 및 Record 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			rsResultCol = JDTORecordFactory.getInstance().createRecordSet("");
			recParaCol = JDTORecordFactory.getInstance().create();
					
			//=======================================================================================================================
			// 차량스케쥴 테이블 조회 (Key: 차량스케쥴 ID) - 기본쿼리 썼음  한건만 조회될 것임
			//=======================================================================================================================
			recPara.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CAR_SCH_ID"));
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult, 0);
			if(intRtnVal < 0) {
				szMsg = "차량스케쥴 조회 오류  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);						
				return 0;
			} else if(intRtnVal == 0) {
				szMsg = "차량스케쥴 조회건수 없음  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);						
				return 0;
			}							

			rsResult.first();
			recGetVal = rsResult.getRecord();
			
			szARR_WLOC_CD = ydDaoUtils.paraRecChkNull(recGetVal, "ARR_WLOC_CD");
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();			
			outRec.setField("JMS_TC_CD", 			"YDTSJ010");
			outRec.setField("JMS_TC_CREATE_DDTT", 	YdUtils.getCurDate("yyyyMMddHHmmss"));

			// 운송장비코드 [운송장비코드]
			outRec.setField("TRN_EQP_CD", ydDaoUtils.paraRecChkNull(recGetVal, "TRN_EQP_CD"));										

			// 착지개소코드 [착지개소코드]
			outRec.setField("ARR_WLOC_CD", szARR_WLOC_CD);

			if( szARR_WLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT)||
				szARR_WLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT)) {
				szArrYdPndCd = YdConstant.WLOC_CD_A_PLATE_PLANT_PNT_CD;
			}else if( szARR_WLOC_CD.equals(YdConstant.WLOC_CD_C_HR_PLANT) ) {
				szArrYdPndCd = YdConstant.WLOC_CD_C_HR_PLANT_PNT_CD;
			}else{
				// 착지야드포인트코드 [하차정지위치 = 적치열구분]
				recParaCol.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recGetVal, "YD_CARUD_STOP_LOC"));
				intRtnValCol = ydStkColDao.getYdStkcol(recParaCol, rsResultCol, 0);
				if(intRtnValCol > 0){
					for(int nIdx2=0; nIdx2<intRtnValCol; nIdx2++) {
						recGetValCol = rsResultCol.getRecord(nIdx2);
						szArrYdPndCd = ydDaoUtils.paraRecChkNull(recGetValCol, "YD_PNT_CD");
					}
				} else if(intRtnValCol == 0){
					szMsg = "적치열 테이블  조회건수 없음  [Ret : " + intRtnVal + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);					
				} else if(intRtnValCol < 0){
					szMsg = "적치열 테이블 조회 오류  [Ret : " + intRtnVal + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
				}
			}
			
			outRec.setField("ARR_YD_PNT_CD", szArrYdPndCd);
							
			// 하차완료일시 [하차완료일시]
			outRec.setField("CARUD_CMPL_DT", ydDaoUtils.paraRecChkNull(recGetVal, "YD_CARUD_CMPL_DT"));

			// RecordSet에 추가				
			outRecSet.addRecord(outRec);
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================OUT==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);	
		}catch(Exception e){
			szMsg = "TS(구내운송)송신 소재차량하차완료  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}	

		return intRtnVal;
	} // end of makeTSJ010()
	
	
	
	
	
	/**
	 * YDTSJ011 : 소재차량Point지시 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeTSJ011(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 			VARCHAR2(8)			YDTSJ011
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시			DATE	(14)		YYYYMMDDHHMMSS (24시간개념)
		
		//		3	TRN_EQP_CD      	운송장비코드			VARCHAR2(8)
		//		4	WLOC_CD         	개소코드				VARCHAR2(5)
		//		5	YD_PNT_CD       	야드포인트코드			VARCHAR2(4)
		//		6	PNT_WO_GP       	포인트지시구분			VARCHAR2(1)
		//		7	PNT_WO_DT			포인트지시일시			VARCHAR2(14)		
		
		// 레코드 선언
		JDTORecord recPara        = null;
		JDTORecord recParaCol     = null;
		JDTORecordSet rsResult    = null;
		JDTORecordSet rsResultCol = null;
		JDTORecord recGetVal      = null;
		JDTORecord recGetValCol   = null;
		JDTORecord outRec         = null;	
		
		// DAO객체 생성
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		YdStkColDao ydStkColDao   = new YdStkColDao();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeTSJ011";
		String szMsg              = "";
		String szOperationName    = "구내운송 소재차량Point지시";
		
		// 야드설비작업상태 (L/U)
		String szEqpWrkStat       = "";
		String szYD_SCH_CD   	  = "";
		// 포인트코드(발지/착지 야드포인트코드)
		String szYdPndCd          = "";
		//point지시일시
		String szPNT_WO_DT			= null;
		//상차point지시일시
		String szYD_CARLD_PNT_WO_DT	= null;
		//하차point지시일시
		String szYD_CARUD_PNT_WO_DT	= null;
		String szYD_STK_COL_GP = null;
		String szARR_WLOC_CD = "";
		// 리턴값
		int intRtnVal             = 0;
		int intRtnValCol          = 0;
		String trnEqpQueryId = "";
		String trnEqpQueryId2 = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		List unloadEndPointList = null;
		
		String szYdCarProgStat = "";
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);
			
			// 리턴 RecordSet 생성 및 Record 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			rsResultCol = JDTORecordFactory.getInstance().createRecordSet("");
			recParaCol = JDTORecordFactory.getInstance().create();
			
			szPNT_WO_DT		= ydDaoUtils.paraRecChkNull(inRec, "PNT_WO_DT");
			//szYD_SCH_CD		= ydDaoUtils.paraRecChkNull(inRec, "YD_SCH_CD");
			//=======================================================================================================================
			// 차량스케쥴 테이블 조회 (Key: 차량스케쥴 ID) - 기본쿼리 썼음  한건만 조회될 것임
			//=======================================================================================================================
			recPara.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CAR_SCH_ID"));
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult, 0);
			if(intRtnVal < 0) {
				szMsg = "차량스케쥴 조회 오류  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);						
				return 0;
			} else if(intRtnVal == 0) {
				szMsg = "차량스케쥴 조회건수 없음  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);						
				return 0;
			}									
						
			rsResult.first();
			recGetVal = rsResult.getRecord();

			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();			
			outRec.setField("JMS_TC_CD", 		  "YDTSJ011");
			outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));

			// 운송장비코드 [운송장비코드]
			outRec.setField("TRN_EQP_CD", ydDaoUtils.paraRecChkNull(recGetVal, "TRN_EQP_CD"));										

			//szEqpWrkStat =  ydDaoUtils.paraRecChkNull(recGetVal, "YD_EQP_WRK_STAT").trim();
			
			szYdCarProgStat = ydDaoUtils.paraRecChkNull(recGetVal, "YD_CAR_PROG_STAT").trim();
			
			//상차 경우
			if( YdConstant.YD_CARLD_LEV.equals(szYdCarProgStat)	 ||
			    YdConstant.YD_CARLD_ARR.equals(szYdCarProgStat)	 ||
			    YdConstant.YD_CARLD_CHK.equals(szYdCarProgStat)	 ||
			    YdConstant.YD_CARLD_ST.equals(szYdCarProgStat)	 ||
			    YdConstant.YD_CARLD_CMPL.equals(szYdCarProgStat)
				){
				szEqpWrkStat = "U";
			}
			//하차 경우
			else if( YdConstant.YD_CARUD_LEV.equals(szYdCarProgStat)	 ||
			    YdConstant.YD_CARUD_ARR.equals(szYdCarProgStat)	 ||
			    YdConstant.YD_CARUD_CHK.equals(szYdCarProgStat)	 ||
			    YdConstant.YD_CARUD_ST.equals(szYdCarProgStat)	 ||
			    YdConstant.YD_CARUD_CMPL.equals(szYdCarProgStat)
				){
				szEqpWrkStat = "L";
			}
			
			
			//if(szEqpWrkStat.equals("U")){
			if(szEqpWrkStat.equals("U")){
				// 개소코드[ 발지개소코드]
				outRec.setField("WLOC_CD", ydDaoUtils.paraRecChkNull(recGetVal, "SPOS_WLOC_CD"));	
				
				szYD_CARLD_PNT_WO_DT = ydDaoUtils.paraRecChkNull(recGetVal, "YD_CARLD_PNT_WO_DT");
				if(szYD_CARLD_PNT_WO_DT.equals("")) 
					szYD_CARLD_PNT_WO_DT = szPNT_WO_DT;
			
				// 포인트지시일시 [상차POINT지시일시]
				outRec.setField("PNT_WO_DT", szYD_CARLD_PNT_WO_DT);			
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP");
				if( !szYD_STK_COL_GP.equals("")) {
					// 야드 포인트코드 [적치BED 포인트코드]
					recParaCol.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
					intRtnValCol = ydStkColDao.getYdStkcol(recParaCol, rsResultCol, 0);
					if(intRtnValCol > 0){
						for(int nIdx2=0; nIdx2<intRtnValCol; nIdx2++) {
							recGetValCol = rsResultCol.getRecord(nIdx2);
							szYdPndCd = ydDaoUtils.paraRecChkNull(recGetValCol, "YD_PNT_CD");
						}
					} else if(intRtnValCol == 0){
						szMsg = "적치열 테이블  조회건수 없음  [Ret : " + intRtnVal + "]";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);					
					} else if(intRtnValCol < 0){
						szMsg = "적치열 테이블 조회 오류  [Ret : " + intRtnVal + "]";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
					}
				}else{
					szYdPndCd = ydDaoUtils.paraRecChkNull(recGetVal, "YD_PNT_CD1");
				}
				if( szYdPndCd.equals("") ) {
					szYdPndCd = "0000";
				}
				outRec.setField("YD_PNT_CD", szYdPndCd);							
			}else if(szEqpWrkStat.equals("L")){
				// 개소코드 [착지개소코드]
				outRec.setField("WLOC_CD", ydDaoUtils.paraRecChkNull(recGetVal, "ARR_WLOC_CD"));		
				
				szYD_CARUD_PNT_WO_DT = ydDaoUtils.paraRecChkNull(recGetVal, "YD_CARUD_PNT_WO_DT");
				if(szYD_CARUD_PNT_WO_DT.equals("")) 
					szYD_CARUD_PNT_WO_DT = szPNT_WO_DT;
				
				// 포인트지시일시 [하차POINT지시일시]
				outRec.setField("PNT_WO_DT", szYD_CARUD_PNT_WO_DT);			
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP");
				// 야드 포인트코드 [적치BED 포인트코드]
				if( !szYD_STK_COL_GP.equals("")) {
					recParaCol.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
					intRtnValCol = ydStkColDao.getYdStkcol(recParaCol, rsResultCol, 0);
					if(intRtnValCol > 0){
						for(int nIdx2=0; nIdx2<intRtnValCol; nIdx2++) {
							recGetValCol = rsResultCol.getRecord(nIdx2);
							szYdPndCd = ydDaoUtils.paraRecChkNull(recGetValCol, "YD_PNT_CD");
						}
					} else if(intRtnValCol == 0){
						szMsg = "적치열 테이블  조회건수 없음  [Ret : " + intRtnVal + "]";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);					
					} else if(intRtnValCol < 0){
						szMsg = "적치열 테이블 조회 오류  [Ret : " + intRtnVal + "]";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
					}
				}else{
					szYdPndCd = ydDaoUtils.paraRecChkNull(recGetVal, "YD_PNT_CD3");
				}
				if( szYdPndCd.equals("") ) {
					szYdPndCd = "0000"; 
				}
				outRec.setField("YD_PNT_CD", szYdPndCd);							
			} else {
				return 0;
			}
			
			// 포인트 지시구분 [] 
			outRec.setField("PNT_WO_GP", "A");                     	       //********* 일단 값 적용하지 말라고 지시 *********

			
			// 포인트 대기장 메시지 처리-------------------------------------------------------------
			
			if(szYdPndCd.equals("0000")){
				
				szARR_WLOC_CD = ydDaoUtils.paraRecChkNull(outRec, "WLOC_CD");
				
				//코일야드 만 해당됨
				if(	szARR_WLOC_CD.equals("D2Y44")||szARR_WLOC_CD.equals("D2Y45")||
					szARR_WLOC_CD.equals("D3Y41")||szARR_WLOC_CD.equals("D3Y42")||
					szARR_WLOC_CD.equals("DJY21")||szARR_WLOC_CD.equals("DJY22")||
					szARR_WLOC_CD.equals("DJY1E")){
					
					if(!szYD_STK_COL_GP.equals("")){
						szYD_SCH_CD =szYD_STK_COL_GP.substring(0 , 2)+"PT"; //HAPT
					}else{
						szYD_SCH_CD ="H_PT";
					}
					
						//개소코드 체크
						trnEqpQueryId = "ym.tsinfo.getListUnloadEndpointCyard_chk";
			    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szYD_SCH_CD,szARR_WLOC_CD});
			    		if(unloadEndPointList.size() <= 0){
			    			outRec.setField("YD_MSG_NM",szYD_SCH_CD.substring(1 ,2)+ "동 지시개소코드가 야드와 틀림.");
			    		}else{
			    		
				    		//포인트 체크
							trnEqpQueryId = "ym.tsinfo.getListUnloadEndpointCyard_chk2";
				    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szYD_SCH_CD,szARR_WLOC_CD});
				    		if(unloadEndPointList.size() <= 0){
				    			outRec.setField("YD_MSG_NM", szYD_SCH_CD.substring(1 ,2)+ "동 개소지의 야드포인트가 사용불가.");
				    		}else{
						 
					    		//다른차량 체크
								trnEqpQueryId = "ym.tsinfo.getListUnloadEndpointCyard_chk3";
					    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szYD_SCH_CD,szARR_WLOC_CD});
					    		if(unloadEndPointList.size() > 0){
					    			outRec.setField("YD_MSG_NM", szYD_SCH_CD.substring(1 ,2)+ "동 해당개소에 다른 차량 점유.");
					    		}else{
					    			
					    			//해당위치 차량용도 체크
									trnEqpQueryId = "ym.tsinfo.getListUnloadEndpointCyard_chk4";
						    		unloadEndPointList = dao.getCommonList(trnEqpQueryId, new Object[]{szYD_SCH_CD,szARR_WLOC_CD});
						    		if(unloadEndPointList.size() <= 0){
						    			outRec.setField("YD_MSG_NM", szYD_SCH_CD.substring(1 ,2)+ "동 구내운송 전용구분 확인 요망.");
						    		}else{
						    			
						    			if(szYD_STK_COL_GP.equals("")){
											outRec.setField("YD_MSG_NM", "목적동OR이송대상이 존재 안함.");
										}else{
											outRec.setField("YD_MSG_NM", "시스템 담당자 확인 요망.");
										}
						    		}
					    		}
				    		}
			    		}
					 
					
				}else{
					outRec.setField("YD_MSG_NM", "");
				}
				
				
				
			}
			
			//*********************************************************************************
		 
			//-------------------------------------------------------
			//******************메시지 이력 관리*************************
			//-------------------------------------------------------
			
			String szYD_MSG_CD = ydDaoUtils.paraRecChkNull(outRec, "YD_MSG_NM");
		 
			trnEqpQueryId2	= "ym.tsinfo.updateEquipMsgRecode";
  	        int iSeq = dao.updateData(trnEqpQueryId2, new Object[]{szYD_MSG_CD,ydDaoUtils.paraRecChkNull(recGetVal, "TRN_EQP_CD")});	
		 
			
			// RecordSet에 추가				
			outRecSet.addRecord(outRec);
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================OUT==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);					
		}catch(Exception e){
			szMsg = "TS(구내운송)송신 소재차량Point지시  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
			return -1;
		}	
		
		return outRecSet.size();
	} // end of makeTSJ011()
	
	
	
	
	
	/**
	 * YDTSJ012 : 소재차량Point개폐 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeTSJ012(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 		VARCHAR2(8)			YDTSJ012
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시		DATE	(14)		YYYYMMDDHHMMSS (24시간개념)
		
		//		3	PRSNT_LOC_WLOC_CD   현위치개소코드		VARCHAR2(5)
		//		4	YD_PNT_CD           야드포인트코드		VARCHAR2(4)
		//		5	PNT_UNIT_CL_GP      포인트개폐구분		VARCHAR2(1)
		//		6	YD_PNT_OP_CL_TT		야드포인트개폐시각	VARCHAR2(14)
		
		// 레코드 생성		
		JDTORecord recPara      = null;
		JDTORecordSet rsResult  = null;
		JDTORecord recGetVal    = null;
		JDTORecord outRec       = null;
		
		// DAO객체 생성
		YdDaoUtils ydDaoUtils   = new YdDaoUtils();	
		YdStkColDao ydStkColDao = new YdStkColDao();
		YdUtils ydUtils         = new YdUtils();

		// 변수선언
		String szMethodName     = "makeTSJ012";
		String szMsg            = "";
		String szOperationName  = "구내운송 소재차량Point개폐";
		
		// 현위치 개소코드
		String szCurrLocCd      = "";
		
		// 야드포인트코드
		String szYdPntCd        = "";

		// 리턴값
		int intRtnVal           = 0;
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, inRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);
			
			// 리턴 RecordSet 생성 및 Record 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();			
			outRec.setField("JMS_TC_CD", 		  "YDTSJ012");
			outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));
			
			//=======================================================================================================================
			// 현위치 개소코드, 야드 포인트코드 [적치BED 포인트코드]- 기본쿼리 썼음  한건만 조회될 것임
			//=======================================================================================================================
			recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP"));
			intRtnVal = ydStkColDao.getYdStkcol(recPara, rsResult, 0);
			if(intRtnVal > 0){
				for(int nIdx2=0; nIdx2<intRtnVal; nIdx2++) {
					recGetVal = rsResult.getRecord(nIdx2);
					szCurrLocCd = ydDaoUtils.paraRecChkNull(recGetVal, "WLOC_CD");
					szYdPntCd = ydDaoUtils.paraRecChkNull(recGetVal, "YD_PNT_CD");
				}
			} else if(intRtnVal == 0){
				szMsg = "적치열 테이블  조회건수 없음  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);					
			} else if(intRtnVal < 0){
				szMsg = "적치열 테이블 조회 오류  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
			}				
			outRec.setField("PRSNT_LOC_WLOC_CD", szCurrLocCd);		
			outRec.setField("YD_PNT_CD", szYdPntCd);							
		
			// 포인트개폐구분 [포인트개폐구분]
			outRec.setField("PNT_UNIT_CL_GP", ydDaoUtils.paraRecChkNull(inRec, "PNT_UNIT_CL_GP"));

			// 야드 포인트개폐시각 [현재시스템시각]
			outRec.setField("YD_PNT_OP_CL_TT", YdUtils.getCurDate("yyyyMMddHHmmss"));	
			
			// RecordSet에 추가				
			outRecSet.addRecord(outRec);
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================OUT==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, outRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);	
		}catch(Exception e){
			szMsg = "TS(구내운송)송신 소재차량Point개폐  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
			return -1;
		}	
		
		return outRecSet.size();
	} // end of makeTSJ012()
		
	
	
	
	
	/**
	 * YDTSJ013 : 소재차량상하차지연사유 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeTSJ013(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD				JMSTCCODE 				VARCHAR2(8)			YDTSJ013
		//		2	JMS_TC_CREATE_DDTT		JMSTC생성일시				DATE	(14)		YYYYMMDDHHMMSS (24시간개념)
		
		//		3	TRN_EQP_CD              운송장비코드				VARCHAR2(8)
		//		4	PRSNT_LOC_WLOC_CD       현위치개소코드				VARCHAR2(5)
		//		5	PNT_CD                  포인트코드				VARCHAR2(4)
		//		6	TS_CARLDUD_SECT_MIN     구내운송상하차지구분		VARCHAR2(1)
		//		7	TRN_WRK_DELY_CD         운송작업지연코드			VARCHAR2(2)
		//		8	TRN_WRK_DELY_OCCR_DT	지연사유발생일시			VARCHAR2(14)
		
		// 레코드 선언
		JDTORecord recPara        = null;
		JDTORecord recParaCol     = null;
		JDTORecordSet rsResult    = null;
		JDTORecordSet rsResultCol = null;
		JDTORecord recGetVal      = null;
		JDTORecord recGetValCol   = null;
		JDTORecord outRec         = null;
		
		// DAO객체 생성
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		YdStkColDao ydStkColDao   = new YdStkColDao();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeTSJ013";
		String szMsg              = "";
		String szOperationName    = "구내운송 소재차량상하차지연사유";
		
		// 야드설비작업상태 (L/U)
		String szEqpWrkStat       = "";
				
		// 포인트코드(발지/착지 야드포인트코드)
		String szYdPndCd          = "";
		
		// 리턴값
		int intRtnVal             = 0;
		int intRtnValCol          = 0;

		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, inRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);	
			
			// 리턴 RecordSet 생성 및 Record 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			rsResultCol = JDTORecordFactory.getInstance().createRecordSet("");
			recParaCol = JDTORecordFactory.getInstance().create();
	
			//=======================================================================================================================
			// 차량스케쥴 테이블 조회 (Key: 차량스케쥴 ID) - 기본쿼리 썼음  한건만 조회될 것임
			//=======================================================================================================================
			recPara.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CAR_SCH_ID"));
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult, 0);
			if(intRtnVal < 0) {
				szMsg = "차량스케쥴 조회 오류  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);						
				return 0;
			} else if(intRtnVal == 0) {
				szMsg = "차량스케쥴 조회건수 없음  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);						
				return 0;
			}							
			
			for(int nIdx=0; nIdx<intRtnVal; nIdx++) {
				recGetVal = rsResult.getRecord(nIdx);			

				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();			
				outRec.setField("JMS_TC_CD", 		  "YDTSJ013");
				outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));

				// 운송장비코드 [운송장비코드] 
				outRec.setField("TRN_EQP_CD", ydDaoUtils.paraRecChkNull(recGetVal, "TRN_EQP_CD"));										
	
				szEqpWrkStat =  ydDaoUtils.paraRecChkNull(recGetVal, "YD_EQP_WRK_STAT").trim();                // L:영차->하차작업해야됨     U:공차->상차작업 해야됨 		
				if(szEqpWrkStat != ""  && szEqpWrkStat.equals("U")){	                                              				
					// 현위치개소코드 [발지개소코드]
					outRec.setField("PRSNT_LOC_WLOC_CD", ydDaoUtils.paraRecChkNull(recGetVal, "SPOS_WLOC_CD"));														
	
					// 야드 포인트코드 [상차정지위치 = 적치열구분]
					recParaCol.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recGetVal, "YD_CARLD_STOP_LOC"));
					intRtnValCol = ydStkColDao.getYdStkcol(recParaCol, rsResultCol, 0);
					if(intRtnValCol > 0){
						for(int nIdx2=0; nIdx2<intRtnValCol; nIdx2++) {
							recGetValCol = rsResultCol.getRecord(nIdx2);
							szYdPndCd = ydDaoUtils.paraRecChkNull(recGetValCol, "YD_PNT_CD");
						}
					} else if(intRtnValCol == 0){
						szMsg = "적치열 테이블  조회건수 없음  [Ret : " + intRtnVal + "]";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);					
					} else if(intRtnValCol < 0){
						szMsg = "적치열 테이블 조회 오류  [Ret : " + intRtnVal + "]";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
					}		
					
					outRec.setField("PNT_CD", szYdPndCd);	
					
					// 구내운송 상하차지구분 
					outRec.setField("TS_CARLDUD_SECT_MIN", "S");						
	
					// 운송작업지연코드 [야드운송작업지연코드]
					outRec.setField("TRN_WRK_DELY_CD", ydDaoUtils.paraRecChkNull(recGetVal, "YD_TRN_WRK_DELY_CD"));						
					
					// 지연사유발생일시 [상차완료일시]
					outRec.setField("TRN_WRK_DELY_OCCR_DT", ydDaoUtils.paraRecChkNull(recGetVal, "YD_CARLD_CMPL_DT"));						
				}else if(szEqpWrkStat != "" && szEqpWrkStat.equals("L")){
					// 현위치개소코드 [착지개소코드]
					outRec.setField("PRSNT_LOC_WLOC_CD", ydDaoUtils.paraRecChkNull(recGetVal, "ARR_WLOC_CD"));		
			
					// 야드 포인트코드 [하차정지위치 = 적치열구분]
					recParaCol.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recGetVal, "YD_CARUD_STOP_LOC"));
					intRtnValCol = ydStkColDao.getYdStkcol(recParaCol, rsResultCol, 0);
					if(intRtnValCol > 0){
						for(int nIdx2=0; nIdx2<intRtnValCol; nIdx2++) {
							recGetValCol = rsResultCol.getRecord(nIdx2);
							szYdPndCd = ydDaoUtils.paraRecChkNull(recGetValCol, "YD_PNT_CD");
						}
					} else if(intRtnValCol == 0){
						szMsg = "적치열 테이블  조회건수 없음  [Ret : " + intRtnVal + "]";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);					
					} else if(intRtnValCol < 0){
						szMsg = "적치열 테이블 조회 오류  [Ret : " + intRtnVal + "]";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
					}						
					outRec.setField("PNT_CD", szYdPndCd);				
	
					// 구내운송 상하차지구분  [아직 값이 정해지지 않았음]
					outRec.setField("TS_CARLDUD_SECT_MIN", "H");						
	
					// 운송작업지연코드 [야드운송작업지연코드]
					outRec.setField("TRN_WRK_DELY_CD", ydDaoUtils.paraRecChkNull(recGetVal, "YD_TRN_WRK_DELY_CD"));							
	
					// 지연사유발생일시 [하차완료일시]
					outRec.setField("TRN_WRK_DELY_OCCR_DT", ydDaoUtils.paraRecChkNull(recGetVal, "YD_CARUD_CMPL_DT"));						
				}
				
				// RecordSet에 추가				
				outRecSet.addRecord(outRec);
				
				// Debug MSG
				ydUtils.putLog(szClassName, szMethodName, "\n======================OUT("+ Integer.toString(nIdx) + ")==========================\n", 3);	
				ydUtils.displayRecord(szOperationName, outRec);			
				ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);	
			}
		}catch(Exception e){
			szMsg = "TS(구내운송)송신 소재차량상하차지연사유  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
			return -1;
		}	

		return outRecSet.size();
	} // end of makeTSJ013()
	
	
	
	
	
	/**
	 * YDTSJ014 : 여재 Slab 소재운송요구 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeTSJ014(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD	         JMSTCCODE 	            VARCHAR2(8)	    YDTSJ014
		//      2	JMS_TC_CREATE_DDTT   JMSTC생성일시	        VARCHAR2(14)	YYYYMMDDHHMMSS (24시간개념)
		//		3	STL_NO	                          재료번호	                VARCHAR2(13) 	Space면 Error
		//		4	TRANSWORD_SEQNO	          운송지시차수	            NUMBER  (3)		
		//		5	TRN_WRK_MTL_GP	          운송작업재료구분	        VARCHAR2(1)		'S'=Slab,'P'=Plate,'H'=HotCoil
		//		6	TS_MATL_FTMV_STAT_GP 구내운송소재이송상태구분	VARCHAR2(1)		1: 이송지시, 2: 이송취소
		//		7	SPOS_WLOC_CD         발지개소코드	            VARCHAR2(5)		개소코드Master에 없으면 Error
		//		8	ARR_WLOC_CD          착지개소코드	            VARCHAR2(5)		개소코드Master에 없으면 Error
		//		9	STL_WT	                          재료중량	                NUMBER	(5)		
		//		10	MTL_UGNT_GP	                  재료긴급구분	            VARCHAR2(1)		Y:긴급재, N:일반재
		//		11	HCR_GP	             HCR구분	                VARCHAR2(1)		C: CCR, H: HCR, W: WCR
		//		12	ORD_YEOJAE_GP        주문여재구분	            VARCHAR2(1)		1: 주문재, 2: 여재
		//		13	MATL_FTMV_WO_DT	          소재이송지시일시	        DATE	(14)	YYYYMMDDHHMMSS
		//		14	MATL_FTMV_DDLN_DD	  소재이송기한일자	        DATE	(8)		YYYYMMDD
		//		15	TRN_WO_CNCL_DT	          운송지시취소일시	        DATE	(14)	YYYYMMDDHHMMSS

		
		// 레코드 생성		
		JDTORecord recPara      = null;
		JDTORecordSet rsResult  = null;
		JDTORecord recGetVal    = null;
		JDTORecord outRec       = null;
		
		// DAO객체 생성
		YdDaoUtils ydDaoUtils   = new YdDaoUtils();	
		YdStkColDao ydStkColDao = new YdStkColDao();
		YdUtils ydUtils         = new YdUtils();

		// 변수선언
		String szMethodName     = "makeTSJ014";
		String szMsg            = "";
		String szOperationName  = "구내운송 여재 Slab 소재운송요구";

		// 리턴값
		int nRet                = 0;
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, inRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);
			
			// 재료번호
			outRec.setField("STL_NO", ydDaoUtils.paraRecChkNull(inRec, "STL_NO"));	
			
			// 운송지시차수 
			outRec.setField("TRANSWORD_SEQNO", ydDaoUtils.paraRecChkNull(inRec, "TRANSWORD_SEQNO"));
			
			// 운송작업재료구분
			outRec.setField("TRN_WRK_MTL_GP", "T");				

			// 구내운송소재이송상태구분 (1: 이송지시, 2: 이송취소)
			outRec.setField("TS_MATL_FTMV_STAT_GP", ydDaoUtils.paraRecChkNull(inRec, "TS_MATL_FTMV_STAT_GP"));				
			
			// 발지개소코드
			outRec.setField("SPOS_WLOC_CD", ydDaoUtils.paraRecChkNull(inRec, "SPOS_WLOC_CD"));	

			// 착지개소코드
			outRec.setField("ARR_WLOC_CD", ydDaoUtils.paraRecChkNull(inRec, "ARR_WLOC_CD"));	

			// 재료중량 
			outRec.setField("STL_WT", ydDaoUtils.paraRecChkNull(inRec, "STL_WT"));			

			// 재료긴급구분 (Y:긴급재, N:일반)
			outRec.setField("MTL_UGNT_GP", ydDaoUtils.paraRecChkNull(inRec, "MTL_UGNT_GP"));	
			
			// HCR구분 (C: CCR, H: HCR, W: WCR)
			outRec.setField("MTL_UGNT_GP", ydDaoUtils.paraRecChkNull(inRec, "MTL_UGNT_GP"));	
			
			// 주문여재구분 (1: 주문재, 2: 여재)
			outRec.setField("ORD_YEOJAE_GP", ydDaoUtils.paraRecChkNull(inRec, "ORD_YEOJAE_GP"));				
	                                        
			// 소재이송지시일시 (YYYYMMDDHHMMSS)
			outRec.setField("MATL_FTMV_WO_DT", YdUtils.getCurDate(ydDaoUtils.paraRecChkNull(inRec, "MATL_FTMV_WO_DT")));	
			
			// 소재이송기한일자 (YYYYMMDD)
			outRec.setField("MATL_FTMV_DDLN_DD", YdUtils.getCurDate(ydDaoUtils.paraRecChkNull(inRec, "MATL_FTMV_DDLN_DD")));	
			
			// 운송지시취소일시 (YYYYMMDDHHMMSS)
			outRec.setField("TRN_WO_CNCL_DT", YdUtils.getCurDate(ydDaoUtils.paraRecChkNull(inRec, "TRN_WO_CNCL_DT")));	
			
			
			// RecordSet에 추가				
			outRecSet.addRecord(outRec);
			
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================OUT==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, outRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);	
		}catch(Exception e){
			szMsg = "TS(구내운송)송신 여재 Slab 소재운송요구  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
			return -1;
		}	
		
		return outRecSet.size();
	} // end of makeTSJ014()

	
	/**
	 * YDTSJ015 : 제품운송요구(후판제품이송지시) 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeTSJ015(JDTORecord inRec, JDTORecordSet outRecSet){
		//	01	 JMS_TC_CD	              JMSTCCODE 	        VARCHAR2(8)	    YDTSJ014
		//  02	 JMS_TC_CREATE_DDTT       JMSTC생성일시	        VARCHAR2(14)	YYYYMMDDHHMMSS (24시간개념)		
		//  03   STL_NO                   재료번호                              VARCHAR2(13)     Space면 Error	                                                  
		//	04	 TRANSWORD_SEQNO          운송지시차수                       NUMBER(3)    	                                                              
		//	05	 TRN_WRK_MTL_GP           운송작업재료구분                 VARCHAR2(1)      'S'=Slab,'P'=Plate,'H'=HotCoil,'C'=제품Coil,'T'=후판제품	
		//	06	 TS_MATL_FTMV_STAT_GP     구내운송소재이송상태구분    VARCHAR2(1)      1: 이송지시, 2: 이송취소, 3: 불출완료, 4: 인수완료	          
		//	07	 SPOS_WLOC_CD             발지개소코드                       VARCHAR2(5)      개소코드Master에 없으면 Error	                                  
		//	08	 SPOS_YD_PNT_CD           발지야드포인트코드            VARCHAR2(4)       Point Master에 없으면 Error	                                 
		//	09	 ARR_WLOC_CD              착지개소코드                      VARCHAR2(5)       개소코드Master에 없으면 Error	                                  
		//	10	 ARR_YD_PNT_CD            착지야드포인트코드            VARCHAR2(4)       Point Master에 없으면 Error	                                    
		//	11	 STL_WT                   재료중량                             NUMBER(5)    	                                                           
		//	12	 MTL_UGNT_GP              재료긴급구분                      VARCHAR2(1)       Y:긴급재, N:일반재	                                            
		//	13	 HCR_GP                   HCR구분                             VARCHAR2(1)       C: CCR, H: HCR, W: WCR	                                        
		//	14	 ORD_YEOJAE_GP            주문여재구분                      VARCHAR2(1)       1: 주문재, 2: 여재	                                         
		//	15	 MATL_FTMV_WO_DT          소재이송지시일시                DATE(14)          YYYYMMDDHHMMSS	                                                
		//	16	 MATL_FTMV_DDLN_DD        소재이송기한일자                DATE(8)           YYYYMMDD	                                                      
		//	17	 TRN_WO_CNCL_DT           운송지시취소일시                DATE(14)          YYYYMMDDHHMMSS	                                             
		//	18	 MATL_FTMV_WO_NML_HD_YN   소재이송지시정상처리여부    VARCHAR2(1)       Y:정상, N:비정상			
		
		// 레코드 생성		
		JDTORecord recPara      = null;
		JDTORecordSet rsResult  = null;
		JDTORecord recGetVal    = null;
		JDTORecord outRec       = null;
		
		// DAO객체 생성
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		YdStkColDao ydStkColDao   = new YdStkColDao();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();
		
		// 변수선언
		String szMethodName     = "makeTSJ015";
		String szMsg            = "";
		String szOperationName  = "구내운송 제품운송요구(후판제품이송지시)";

		// 리턴값
		int nRet                = 0;
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, inRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);
			
			// 리턴 RecordSet 생성 및 Record 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();			
			outRec.setField("JMS_TC_CD", 		  "YDTSJ015");
			outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));
			
			//----------------------------------------------------------------------------------------------------------
			// 송신 전문 편집 
			// 2009.11.05 석창화 과장이 만든 화면에서 송신 P/G 호출
			// YDTSJ015 Lay-Out 항목은 화면에서 전부 파라미터로 넘겨주기로 결정.
			//
			// 아래 1~5은 화면에서 파라미터로 주지 않을 경우를 대비하여 항목 편성한 내용임 
			//
			// 1. 화면에서 파라미터로 수신하는 항목 
			//          재료번호               (STL_NO) 
			//          운송작업재료구분  (TRN_WRK_MTL_GP) ('S'=Slab,'P'=Plate,'H'=HotCoil,'C'=제품Coil,'T'=후판제품)
			//
			//
			// 2. PT_소재이송지시 (USRPTA.TB_PT_STLFRTOMOVE) :
			//              항목명                                 송신항목ID              Table-ID
			//          ---------------------------------------------------------------------------------------------
			//          운송지시차수                     (TRANSWORD_SEQNO)       TRANSWORD_SEQNO
			//          구내운송소재이송상태구분 (TS_MATL_FTMV_STAT_GP)  FRTOMOVE_STAT_CD         (1: 이송지시, 2: 이송취소)
			//          HCR구분                            (HCR_GP)                HCR_GP                   (C: CCR, H: HCR, W: WCR)
			//          소재이송지시일시               (MATL_FTMV_WO_DT)       FRTOMOVE_WORD_DATE       (YYYYMMDDHHMMSS)
			//          운송지시취소일시               (TRN_WO_CNCL_DT)        FRTOMOVE_ORD_CANCEL_DATE (YYYYMMDDHHMMSS)
			//          재료긴급구분                      (MTL_UGNT_GP)           URGENT_FRTOMOVE_WORD_GP  (Y:긴급재, N:일반재)
			//          발지개소코드                      (SPOS_WLOC_CD)          SPOS_WLOC_CD
			//          착지개소코드                      (ARR_WLOC_CD)           ARR_WLOC_CD
			//          재료중량                             (STL_WT)                STL_WT
			//
			//
			// 3. PT_PLATE공통 (USRPTA.TB_PT_PLATECOMM)      
			//              항목명                                 송신항목ID              Table-ID
			//          ---------------------------------------------------------------------------------------------			
			//          재료번호                                    (STL_NO)             PLATE_NO
			//          주문여재구분                             (ORD_YEOJAE_GP)      ORD_YEOJAE_GP          (1: 주문재, 2: 여재)
			//
			//
			// 4. 적치열 Table(USRYDA.TB_YD_STKCOL)에서 개소코드(WLOC_CD)로 검색으로  야드포인트코드(YD_PNT_CD) 추출
			//          select YD_PNT_CD from USRYDA.TB_YD_STKCOL where WLOC_CD = 'DJY30'
			// 			발지야드포인트코드            (SPOS_YD_PNT_CD)                
			//          착지야드포인트코드            (ARR_YD_PNT_CD)  
			//
			// 5.             
			//          소재이송기한일자               (MATL_FTMV_DDLN_DD)                                 (YYYYMMDD) 
			//          소재이송지시정상처리여부  (MATL_FTMV_WO_NML_HD_YN)                            (Y:정상, N:비정상)
			//----------------------------------------------------------------------------------------------------------

			// 재료번호 [후판제품번호]
			outRec.setField("STL_NO", ydDaoUtils.paraRecChkNull(inRec, "STL_NO"));	
			
			// 운송지시차수 
			outRec.setField("TRANSWORD_SEQNO", ydDaoUtils.paraRecChkNull(inRec, "TRANSWORD_SEQNO"));
			
			// 운송작업재료구분
			outRec.setField("TRN_WRK_MTL_GP", "T");				

			// 구내운송소재이송상태구분 (1: 이송지시, 2: 이송취소)
			outRec.setField("TS_MATL_FTMV_STAT_GP", ydDaoUtils.paraRecChkNull(inRec, "TS_MATL_FTMV_STAT_GP"));				
			
			// 발지개소코드
			outRec.setField("SPOS_WLOC_CD", ydDaoUtils.paraRecChkNull(inRec, "SPOS_WLOC_CD"));	

			// 발지야드포인트코드
			outRec.setField("SPOS_YD_PNT_CD", ydDaoUtils.paraRecChkNull(inRec, "SPOS_YD_PNT_CD"));	
                                  
			// 착지개소코드
			outRec.setField("ARR_WLOC_CD", ydDaoUtils.paraRecChkNull(inRec, "ARR_WLOC_CD"));	

			// 착지야드포인트코드
			outRec.setField("ARR_YD_PNT_CD", ydDaoUtils.paraRecChkNull(inRec, "ARR_YD_PNT_CD"));	
			
			// 재료중량 
			outRec.setField("STL_WT", ydDaoUtils.paraRecChkNull(inRec, "STL_WT"));			

			// 재료긴급구분 (Y:긴급재, N:일반)
			outRec.setField("MTL_UGNT_GP", ydDaoUtils.paraRecChkNull(inRec, "MTL_UGNT_GP"));	
			
			// HCR구분 (C: CCR, H: HCR, W: WCR)
			outRec.setField("MTL_UGNT_GP", ydDaoUtils.paraRecChkNull(inRec, "MTL_UGNT_GP"));	
			
			// 주문여재구분 (1: 주문재, 2: 여재)
			outRec.setField("ORD_YEOJAE_GP", ydDaoUtils.paraRecChkNull(inRec, "ORD_YEOJAE_GP"));				
	                                        
			// 소재이송지시일시 (YYYYMMDDHHMMSS)
			outRec.setField("MATL_FTMV_WO_DT", YdUtils.getCurDate(ydDaoUtils.paraRecChkNull(inRec, "MATL_FTMV_WO_DT")));	
			
			// 소재이송기한일자 (YYYYMMDD)
			outRec.setField("MATL_FTMV_DDLN_DD", YdUtils.getCurDate(ydDaoUtils.paraRecChkNull(inRec, "MATL_FTMV_DDLN_DD")));	
			
			// 운송지시취소일시 (YYYYMMDDHHMMSS)
			outRec.setField("TRN_WO_CNCL_DT", YdUtils.getCurDate(ydDaoUtils.paraRecChkNull(inRec, "TRN_WO_CNCL_DT")));	
			
			// 소재이송지시정상처리여부 (Y:정상, N:비정상	)
			outRec.setField("MATL_FTMV_WO_NML_HD_YN", "Y");	
			
			// RecordSet에 추가				
			outRecSet.addRecord(outRec);			
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================OUT==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, outRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);	

		}catch(Exception e){
			szMsg = "TS(구내운송)송신 제품운송요구(후판제품이송지시) 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
			return -1;
		}	
		
		return outRecSet.size();
	} // end of makeTSJ015()
	
	 
  //---------------------------------------------------------------------------	
} // end of class MakeTcTS
