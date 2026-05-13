package com.inisteel.cim.yd.common.util.tcconst;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 * CT (생산통제) 송신 용 전문 생성
 * @author YHWHman
 *
 */
public class MakeTcCT {
	// YDCTJ021 후판창고저장계획송신	
	// YDCTJ031 A후판장입진행실적송신
	// YDCTJ032 B열연장입진행실적송신
	// YDCTJ033 C열연장입진행실적	
	// YDCTJ034  연주/후판슬라브야드 이송하차실적
	// YDCTJ035  연주/후판슬라브야드 이상재등록/해제	
	
	
	

	
	// 클래스명
	private static final String szClassName  = MakeTcCT.class.getName();

	
	/**
	 * YDCTJ021 : 후판창고저장계획송신 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeCTJ021(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 		VARCHAR2(8)		YDCTJ021
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시		DATE	(14)	YYYYMMDDHHMMSS (24시간개념)

		//		3	PTOP_PLNT_GP		조업공장구분		VARCHAR2(2)		HB:B열연,   HC:C열연,  PA:A후판,   PB:B후판
		//		4	STL_NO				재료번호			VARCHAR2(11)	생산예정 Plate No
		//		5	YD_PILING_CD		야드파일링코드		VARCHAR2(8)
		//		6	YD_BOOK_OUT_CD		BookoutCD		VARCHAR2(4)
		
		// 레코드 선언
		JDTORecord outRec       = null;
		YdUtils ydUtils         = new YdUtils();

		// 변수선언
		String szMethodName     = "makeCTJ021";
		String szMsg            = "";
		String szOperationName  = "생산통제 후판창고저장계획송신";
		
		try{
			outRec = JDTORecordFactory.getInstance().create();						
			outRec.setField("JMS_TC_CD"         , "YDCTJ021");
			outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));
		}catch(Exception e){
			szMsg = "CT(생산통제) 송신  후판제품저장계획 데이터 반환중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}	
		
		ydUtils.displayRecord(szOperationName, outRec);
		
		return (ydUtils.getRecKeyCnt(outRec));
	} // end of makeCTJ021()	
	
	
	
	
	
	/**
	 * YDCTJ031 : A후판장입진행실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */	
	public static int makeCTJ031(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 		VARCHAR2(8)		YDCTJ031
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시		DATE	(14)	YYYYMMDDHHMMSS (24시간개념)
		
		//		3	PTOP_PLNT_GP		조업공장구분		VARCHAR2(2)		HB:B열연,   HC:C열연,  PA:A후판,   PB:B후판
		//		4	STL_APPEAR_GP    	재료외형			VARCHAR2(1)		예정압연지시 주편과 슬라브를 구분
		//		5	CHG_SUP_PROG_STAT	장입보급진행상태	VARCHAR2(2)		"코드 : 상태 + 설비
		//																10 : 장입동적치 - 대차동간이적
		//																20 : 크레인 보급권상(보급스케줄시행)
		//																30 : W/B,장입구,디파일러(적치완료()
		//																31 : CTC적치완료
		//																40 : RollerTable적치완료(L3,CTC)
		//		6	WR_OCCR_DT			실적발생일시		VARCHAR2(14)	YYYYMMDDHHMMSS (24시간개념)
		//		7	YD_EQP_WR_CNT		야드설비작업매수	NUMBER(2)		예) 3
		//		8	STL_NO1				재료번호1			VARCHAR2(11)	있다
		//		9	STL_NO2				재료번호2			VARCHAR2(11)	있다
		//		10	STL_NO3				재료번호3			VARCHAR2(11)	있다
		//		11	STL_NO4				재료번호4			VARCHAR2(11)	없다
		//		12	STL_NO5				재료번호5			VARCHAR2(11)	없다
		//		13	STL_NO6				재료번호6			VARCHAR2(11)	없다
		//		14	STL_NO7				재료번호7			VARCHAR2(11)	없다
		//		15	STL_NO8				재료번호8			VARCHAR2(11)	없다
		//		16	STL_NO9				재료번호9			VARCHAR2(11)	없다
		//		17	STL_NO10			재료번호10		VARCHAR2(11)	없다
		//		18	STL_NO11			재료번호11		VARCHAR2(11)	없다
		//		19	STL_NO12			재료번호12		VARCHAR2(11)	없다		
		
		// 레코드 선언
		JDTORecord recPara      = null;
		JDTORecordSet rsResult  = null;
		JDTORecord recGetVal    = null;
		JDTORecord outRec       = null;		

		// DAO객체 생성
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
		YdDaoUtils ydDaoUtils   = new YdDaoUtils();	
		YdUtils ydUtils         = new YdUtils();
		YdStockDao ydStockDao	= new YdStockDao();

		// 변수선언
		String szMethodName     = "makeCTJ031";
		String szMsg            = "";
		String szOperationName  = "생산통제 A후판장입진행실적송신";
		
		// 리턴값
		int intRtnVal           = 0;
		
		//장입보급진행상태 - 임춘수 추가 2009.06.18
		String szCHG_SUP_PROG_STAT = "";
		String szWR_OCCR_DT = null;
		
		//재료 매수
		int intMtlCnt                 = 0;
				
		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);	
			
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 * 장입보급진행상태 - 임춘수 추가  2009.06.18
			 * 장입보급진행상태에 따른 전문 편집 구성을  구분하여 처리
			 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			szCHG_SUP_PROG_STAT = ydDaoUtils.paraRecChkNull(inRec, "CHG_SUP_PROG_STAT");
			szMsg = "장입보급진행상태 : " + szCHG_SUP_PROG_STAT;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			// 리턴 RecordSet 생성 및 Record 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			
			if( szCHG_SUP_PROG_STAT.equals("40") )	{
				//A후판가열로보급 TAKE-IN 완료 : RollerTable적치완료( L3,CTC)

				//=======================================================================================================================
				// 저장품 테이블 조회
				//=======================================================================================================================
				recPara.setField("STL_NO", ydDaoUtils.paraRecChkNull(inRec, "STL_NO1"));
				intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 0);
				if(intRtnVal < 0) {
					szMsg = "저장품 조회 오류 [" + ydDaoUtils.paraRecChkNull(inRec, "STL_NO1") + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);								
					return 0;
				} else if(intRtnVal == 0) {
					szMsg = "저장품 조회건수 없음  [" + ydDaoUtils.paraRecChkNull(inRec, "STL_NO1") + "]가 존재하지 않습니다.";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);								
					return 0;
				}

				rsResult.first();
				recGetVal = rsResult.getRecord();
				
				outRec = JDTORecordFactory.getInstance().create();
				
				// 헤더부
				outRec.setField("JMS_TC_CD"         , "YDCTJ031");
				outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));
				
				// 압연공장구분 [P]
				outRec.setField("PTOP_PLNT_GP"      , "PA");
				
				// 재료외형구분 [재료외형구분]
				outRec.setField("STL_APPEAR_GP"     , ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP"));
				
				// 장입보급진행상태
				outRec.setField("CHG_SUP_PROG_STAT" , szCHG_SUP_PROG_STAT);
				
				// 실적발생일시 [TAKE-IN 완료]
				outRec.setField("WR_OCCR_DT"        , ydDaoUtils.paraRecChkNull(inRec, "WR_OCCR_DT"));
				
				// 야드설비작업매수
				outRec.setField("YD_EQP_WR_CNT"     , ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_STL_SH"));	
				
				intMtlCnt = ydDaoUtils.paraRecChkNullInt(inRec, "YD_STK_BED_STL_SH");
				for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++ ) {
					// 재료번호 [재료번호]
					outRec.setField("STL_NO" + Loop_i, ydDaoUtils.paraRecChkNull(inRec, ("STL_NO" + Loop_i)));
				}
				
				szMsg = "장입보급진행상태 [" + szCHG_SUP_PROG_STAT + "] A후판가열로보급 TAKE-IN 완료 A후판장입진행실적 전문편집";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);	
			}else{
				//권하실적처리 - 장입CARRY-IN 스케줄
				//=======================================================================================================================
				// 크레인스케쥴+크레인작업재료 테이블 조회 (Key: 크레인스케쥴 ID) // 행선구분이 C3인것만 걸러서 처리해야 됨
				//=======================================================================================================================
				recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID"));
				recPara.setField("YD_AIM_RT_GP", YdConstant.AR_WRK_WAIT_A_MILL);
				intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsResult, 10);
				if(intRtnVal < 0) {
					szMsg = "크레인스케쥴 조회 오류  [Ret : " + intRtnVal + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
					return 0;
				} else if(intRtnVal == 0) {
					szMsg = "크레인스케쥴 조회건수 없음  [Ret : " + intRtnVal + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
					return 0;
				}		
				
				outRec = JDTORecordFactory.getInstance().create();						
	
				for(int nIdx=0; nIdx<intRtnVal; nIdx++) {
					recGetVal = rsResult.getRecord(nIdx);			
	
					szWR_OCCR_DT = ydDaoUtils.paraRecChkNull(recGetVal, "YD_DN_CMPL_DT");
					
					if( szWR_OCCR_DT.equals("") ) {
						szWR_OCCR_DT = ydDaoUtils.paraRecChkNull(recGetVal, "YD_UP_CMPL_DT");
					}
					
					if( szWR_OCCR_DT.equals("") ) {
						szWR_OCCR_DT = YdUtils.getCurDate("yyyyMMddHHmmss");
					}
					
					// 헤더부
					outRec.setField("JMS_TC_CD"         , "YDCTJ031");
					outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));		
					
					// 압연공장구분 [C]
					outRec.setField("PTOP_PLNT_GP"      , "PA");
					
					// 재료외형구분 [재료외형구분]
					outRec.setField("STL_APPEAR_GP"     , ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP"));
	
					// 장입보급진행상태 
					outRec.setField("CHG_SUP_PROG_STAT", szCHG_SUP_PROG_STAT);
					
					// 실적발생일시 [권하완료일시]
					outRec.setField("WR_OCCR_DT", 		szWR_OCCR_DT);
	
					// 야드설비작업매수 [조회된 레코드 수]
					outRec.setField("YD_EQP_WR_CNT", Integer.toString(intRtnVal));
					
					// 재료번호 [재료번호]
					outRec.setField("STL_NO" + (1+nIdx), ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO"));
				}
			}
			// RecordSet에 추가				
			outRecSet.addRecord(outRec);

			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n======================OUT==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);								
		}catch(Exception e){
			szMsg = "CT(생산통제) 송신  A후판장입진행실적 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}	
			
		return outRecSet.size();
	} // end of makeCTJ031()	
	
	
	
	
	
	/**
	 * YDCTJ032 : B열연장입진행실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */	
	public static int makeCTJ032(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 		VARCHAR2(8)		YDCTJ032
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시		DATE	(14)	YYYYMMDDHHMMSS (24시간개념)
		
		//		3	PTOP_PLNT_GP		조업공장구분		VARCHAR2(2)		HB:B열연,   HC:C열연,  PA:A후판,   PB:B후판
		//		4	STL_APPEAR_GP    	재료외형			VARCHAR2(1)		예정압연지시 주편과 슬라브를 구분
		//		5	CHG_SUP_PROG_STAT	장입보급진행상태	VARCHAR2(2)		"코드 : 상태 + 설비
		//																10 : 장입동적치 - 대차동간이적
		//																20 : 크레인 보급권상(보급스케줄시행)
		//																30 : W/B,장입구,디파일러(적치완료()
		//																31 : CTC적치완료
		//																40 : RollerTable적치완료(L3,CTC)
		//		6	WR_OCCR_DT			실적발생일시		VARCHAR2(14)	YYYYMMDDHHMMSS (24시간개념)
		//		7	YD_EQP_WR_CNT		야드설비작업매수	NUMBER(2)		예) 3
		//		8	STL_NO1				재료번호1			VARCHAR2(11)	있다
		//		9	STL_NO2				재료번호2			VARCHAR2(11)	있다
		//		10	STL_NO3				재료번호3			VARCHAR2(11)	있다
		//		11	STL_NO4				재료번호4			VARCHAR2(11)	없다
		//		12	STL_NO5				재료번호5			VARCHAR2(11)	없다
		//		13	STL_NO6				재료번호6			VARCHAR2(11)	없다
		//		14	STL_NO7				재료번호7			VARCHAR2(11)	없다
		//		15	STL_NO8				재료번호8			VARCHAR2(11)	없다
		//		16	STL_NO9				재료번호9			VARCHAR2(11)	없다
		//		17	STL_NO10			재료번호10		VARCHAR2(11)	없다
		//		18	STL_NO11			재료번호11		VARCHAR2(11)	없다
		//		19	STL_NO12			재료번호12		VARCHAR2(11)	없다		
		
		// 레코드 선언
		JDTORecord recPara      = null;
		JDTORecordSet rsResult  = null;
		JDTORecord recGetVal    = null;
		JDTORecord outRec       = null;		

		// DAO객체 생성
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
		YdDaoUtils ydDaoUtils   = new YdDaoUtils();	
		YdUtils ydUtils         = new YdUtils();

		// 변수선언
		String szMethodName     = "makeCTJ031";
		String szMsg            = "";
		String szOperationName  = "생산통제 B열연장입진행실적송신";
	
		// 권하실적 위치
		String szYdDnWrLoc      = "";
		
		// 동
		String szDong           = "";
		
		// 설비
		String szEquip          = "";
		
		// 리턴값
		int intRtnVal           = 0;
				
		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);	
			
			// 리턴 RecordSet 생성 및 Record 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			
			//=======================================================================================================================
			// 크레인스케쥴+크레인작업재료 테이블 조회 (Key: 크레인스케쥴 ID) // 행선구분이 21인것만 걸러서 처리해야 됨
			//=======================================================================================================================
			recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID"));
			recPara.setField("YD_AIM_RT_GP", YdConstant.AR_WRK_WAIT_B_MILL);
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsResult, 10);
			if(intRtnVal < 0) {
				szMsg = "크레인스케쥴 조회 오류  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
				return 0;
			} else if(intRtnVal == 0) {
				szMsg = "크레인스케쥴 조회 건수 없음 [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
				return 0;
			}		
			
			outRec = JDTORecordFactory.getInstance().create();						

			for(int nIdx=0; nIdx<intRtnVal; nIdx++) {
				recGetVal = rsResult.getRecord(nIdx);			

				// 헤더부
				outRec.setField("JMS_TC_CD"         , "YDCTJ032");
				outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));		
				
				// 압연공장구분 [C]
				outRec.setField("PTOP_PLNT_GP"      , "HB");
				
				// 재료외형구분 [재료외형구분]
				outRec.setField("STL_APPEAR_GP"     , ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP"));

				// 장입보급진행상태 [크레인스케줄 권하실적위치(동, 설비) 에 따라 설정]
				szYdDnWrLoc = ydDaoUtils.paraRecChkNull(recGetVal, "YD_DN_WR_LOC").trim();
				if(szYdDnWrLoc != null && !szYdDnWrLoc.equals("")){
					szDong = szYdDnWrLoc.substring(0, 1);
					szEquip = szYdDnWrLoc.substring(1, 3);
					if(szDong.equals("A")){
						if(szEquip.equals("RT")){
							outRec.setField("CHG_SUP_PROG_STAT", "40");
						}else if(szEquip.equals("PU")){
							outRec.setField("CHG_SUP_PROG_STAT", "30");						
						}else if(!szEquip.equals("RT") && !szEquip.equals("PB")){
							outRec.setField("CHG_SUP_PROG_STAT", "10");
						}else { // 스케쥴코드가 Carry-In
							outRec.setField("CHG_SUP_PROG_STAT", "20");						
						}			
					}else {
						outRec.setField("CHG_SUP_PROG_STAT", "");											
					}
				}else {	
					outRec.setField("CHG_SUP_PROG_STAT", "");											
				}				
				
				// 실적발생일시 [권하완료일시]
				outRec.setField("WR_OCCR_DT", ydDaoUtils.paraRecChkNull(recGetVal, "YD_DN_CMPL_DT"));

				// 야드설비작업매수 [조회된 레코드 수]
				outRec.setField("YD_EQP_WR_CNT", Integer.toString(intRtnVal));
				
				// 재료번호 [재료번호]
				outRec.setField("STL_NO" + (1+nIdx), ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO"));
			}
	
			// RecordSet에 추가				
			outRecSet.addRecord(outRec);

			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n======================OUT==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);								
		}catch(Exception e){
			szMsg = "CT(생산통제) 송신  B열연장입진행실적 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}	
			
		return outRecSet.size();
	} // end of makeCTJ031()	
	
	
	
	
	
	/**
	 * YDCTJ033 : C열연장입진행실적 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeCTJ033(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 		VARCHAR2(8)		YDCTJ033
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시		DATE	(14)	YYYYMMDDHHMMSS (24시간개념)

		//		3	PTOP_PLNT_GP		조업공장구분		VARCHAR2(2)		HB:B열연,   HC:C열연,  PA:A후판,   PB:B후판
		//		4	STL_APPEAR_GP		재료외형			VARCHAR2(1)		예정압연지시 주편과 슬라브를 구분
		//		5	CHG_SUP_PROG_STAT	장입보급진행상태	VARCHAR2(2)		"코드 : 상태 + 설비
		//																10 : 장입동적치 - 대차동간이적
		//																20 : 크레인 보급권상(보급스케줄시행)
		//																30 : W/B,장입구,디파일러(적치완료()
		//																31 : CTC적치완료
		//																40 : RollerTable적치완료( L3,CTC)"
		//		6	WR_OCCR_DT			실적발생일시		VARCHAR2(14)	YYYYMMDDHHMMSS (24시간개념)
		//		7	YD_EQP_WR_CNT		야드설비작업매수	NUMBER(2)		예) 3
		//		8	STL_NO1				재료번호1			VARCHAR2(11)	있다
		//		9	STL_NO2				재료번호2			VARCHAR2(11)	있다
		//		10	STL_NO3				재료번호3			VARCHAR2(11)	있다
		//		11	STL_NO4				재료번호4			VARCHAR2(11)	없다
		//		12	STL_NO5				재료번호5			VARCHAR2(11)	없다
		//		13	STL_NO6				재료번호6			VARCHAR2(11)	없다
		//		14	STL_NO7				재료번호7			VARCHAR2(11)	없다
		//		15	STL_NO8				재료번호8			VARCHAR2(11)	없다
		//		16	STL_NO9				재료번호9			VARCHAR2(11)	없다
		//		17	STL_NO10			재료번호10		VARCHAR2(11)	없다
		//		18	STL_NO11			재료번호11		VARCHAR2(11)	없다
		//		19	STL_NO12			재료번호12		VARCHAR2(11)	없다	
		
		// 레코드 선언
		JDTORecord recPara      = null;
		JDTORecordSet rsResult  = null;
		JDTORecord recGetVal    = null;
		JDTORecord outRec       = null;		

		// DAO객체 생성
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
		YdDaoUtils ydDaoUtils   = new YdDaoUtils();	
		YdUtils ydUtils         = new YdUtils();
		YdStockDao ydStockDao	= new YdStockDao();

		// 변수선언
		String szMethodName     = "makeCTJ033";
		String szMsg            = "";
		String szOperationName  = "생산통제 C열연장입진행실적";
		
		// 리턴값
		int intRtnVal           = 0;
		
		//재료 매수
		int intMtlCnt                 = 0;
		
		//장입보급진행상태 - 임춘수 추가 2009.06.16
		String szCHG_SUP_PROG_STAT = "";
		
		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, inRec);				
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);	

			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 * 장입보급진행상태 - 임춘수 추가 2009.06.16
			 * 장입보급진행상태에 따른 전문 편집 구성을  구분하여 처리
			 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			szCHG_SUP_PROG_STAT = ydDaoUtils.paraRecChkNull(inRec, "CHG_SUP_PROG_STAT");
			szMsg = "장입보급진행상태 : " + szCHG_SUP_PROG_STAT;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);	
			
			// 리턴 RecordSet 생성 및 Record 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			
			if( szCHG_SUP_PROG_STAT.equals("40") )	{
				//C열연가열로보급 TAKE-IN 완료 : RollerTable적치완료( L3,CTC)
				//=======================================================================================================================
				// 저장품 테이블 조회
				//=======================================================================================================================
				recPara.setField("STL_NO", ydDaoUtils.paraRecChkNull(inRec, "STL_NO1"));
				intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 0);
				if(intRtnVal < 0) {
					szMsg = "저장품 조회오류  [" + ydDaoUtils.paraRecChkNull(inRec, "STL_NO1") + "]가 존재하지 않습니다.";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);								
					return 0;
				} else if(intRtnVal == 0) {
					szMsg = "저장품 조회건수 없음  [" + ydDaoUtils.paraRecChkNull(inRec, "STL_NO1") + "]가 존재하지 않습니다.";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);								
					return 0;
				}
				
				rsResult.first();
				recGetVal = rsResult.getRecord();
				
				outRec = JDTORecordFactory.getInstance().create();
				// 헤더부
				outRec.setField("JMS_TC_CD"         , "YDCTJ033");
				outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));
				
				// 압연공장구분 [P]
				outRec.setField("PTOP_PLNT_GP"      , "HC");
				
				// 재료외형구분 [재료외형구분]
				outRec.setField("STL_APPEAR_GP"     , ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP"));
				
				// 장입보급진행상태
				outRec.setField("CHG_SUP_PROG_STAT", szCHG_SUP_PROG_STAT);
				
				// 실적발생일시 [TAKE-IN 완료]
				//outRec.setField("WR_OCCR_DT", 	ydDaoUtils.paraRecChkNull(inRec, "WR_OCCR_DT"));
				outRec.setField("WR_OCCR_DT", 		YdUtils.getCurDate("yyyyMMddHHmmss"));
				
				// 야드설비작업매수
				outRec.setField("YD_EQP_WR_CNT", 	ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_STL_SH"));	
				
				intMtlCnt = ydDaoUtils.paraRecChkNullInt(inRec, "YD_STK_BED_STL_SH");
				for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++ ) {
					// 재료번호 [재료번호]
					outRec.setField("STL_NO" + Loop_i, ydDaoUtils.paraRecChkNull(inRec, ("STL_NO" + Loop_i)));
				}
				
				szMsg = "장입보급진행상태 [" + szCHG_SUP_PROG_STAT + "] C열연가열로보급 TAKE-IN 완료 C열연장입진행실적 전문편집";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);	
			}else{
				//권하실적처리 - 장입CARRY-IN 스케줄
				//=======================================================================================================================
				// 크레인스케쥴+크레인작업재료 테이블 조회 (Key: 크레인스케쥴 ID) // 행선구분이 C2인것만 걸러서 처리해야 됨
				//=======================================================================================================================
				recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID"));
				recPara.setField("YD_AIM_RT_GP", YdConstant.AR_WRK_WAIT_C_MILL);
				intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsResult, 10);
				if(intRtnVal < 0) {
					szMsg = "크레인스케쥴 조회 오류  [Ret : " + intRtnVal + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);								
					return 0;
				} else if(intRtnVal == 0) {
					szMsg = "크레인스케쥴 조회건수 없음  [Ret : " + intRtnVal + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);								
					return 0;
				}		
		
				outRec = JDTORecordFactory.getInstance().create();						
	
				for(int nIdx=0; nIdx<intRtnVal; nIdx++) {
					recGetVal = rsResult.getRecord(nIdx);			
					
					// 헤더부
					outRec.setField("JMS_TC_CD"         , "YDCTJ033");
					outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));
	
					// 압연공장구분 [P]
					outRec.setField("PTOP_PLNT_GP"      , "HC");
					
					// 재료외형구분 [재료외형구분]
					outRec.setField("STL_APPEAR_GP"     , ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP"));
					
					// 장입보급진행상태
					outRec.setField("CHG_SUP_PROG_STAT", szCHG_SUP_PROG_STAT);
					
					// 실적발생일시 [권하완료일시]
					//outRec.setField("WR_OCCR_DT", ydDaoUtils.paraRecChkNull(recGetVal, "YD_DN_CMPL_DT"));
					outRec.setField("WR_OCCR_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
					
					// 야드설비작업매수 [조회된 레코드 수]
					outRec.setField("YD_EQP_WR_CNT", Integer.toString(intRtnVal));				
					
					// 재료번호 [재료번호]
					outRec.setField("STL_NO" + (1+nIdx), ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO"));
				} 
				
				szMsg = "장입보급진행상태 [" + szCHG_SUP_PROG_STAT + "] 권하실적처리 - 장입CARRY-IN 스케줄 시 C열연장입진행실적 전문편집";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);	
			}
			
			// RecordSet에 추가				
			outRecSet.addRecord(outRec);
			
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n======================OUT==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, outRec);							
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);					
			
		}catch(Exception e){
			szMsg = "CT(생산통제) 송신  C열연장입진행실적 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
			return -1;
		}	
				
		return outRecSet.size();
	} // end of makeCTJ033()

	
	
	
	
	/**
	 * YDCTJ034 : 연주/후판슬라브야드 이송하차실적 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeCTJ034(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 		VARCHAR2(8)		YDCTJ021
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시		DATE	(14)	YYYYMMDDHHMMSS (24시간개념)
		//		3	SLAB_NO		        SLAB번호		    VARCHAR2(11)
		

		// DAO 및 UTIL 객체 생성
		YdUtils ydUtils        = new YdUtils();
		YdDaoUtils ydDaoUtils  = new YdDaoUtils();
		
		// 레코드 선언
		JDTORecord outRec      = null;

		// 변수선언
		String szMethodName    = "makeCTJ034";
		String szMsg           = "";
		String szOperationName = "생산통제 연주/후판슬라브야드 이송하차실적";
		String szSLAB_NO       = "";
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeCTJ034() IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
			
			szSLAB_NO = ydDaoUtils.paraRecChkNull(inRec, "SLAB_NO");
			if(szSLAB_NO.equals("")){
				szMsg = "연주/후판슬라브야드 이송하차실적 - 파라미터로 넘어온 SLAB_NO 값이 없음";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;				
			}
			
			outRec = JDTORecordFactory.getInstance().create();						
			outRec.setField("JMS_TC_CD"         , "YDCTJ034");
			outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));
			outRec.setField("SLAB_NO"           , szSLAB_NO);
			
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n========================makeCTJ034() OUT========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
		}catch(Exception e){
			szMsg = "CT(생산통제) 송신  후연주/후판슬라브야드 이송하차실적 데이터 반환중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}	
		
		return outRecSet.size();
	} // end of makeCTJ034()	
	
	/**
	 * YDCTJ035 : 연주/후판슬라브야드 이상재 등록/해제
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeCTJ035(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 		VARCHAR2(8)		YDCTJ035
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시	DATE	(14)	YYYYMMDDHHMMSS (24시간개념)
		//		3	SLAB_NO		        SLAB번호		    VARCHAR2(11)
		//		4	PTOP_PLNT_GP		공장구분		    VARCHAR2(2)
		//		5	AB_OCCR_RSN_CD		 이상재원인코드	VARCHAR2(4)
		//		6	REGISTER		           등록자		    VARCHAR2(10)
		//		7	REG_DDTT		           등록일시		    DATE	(14)	YYYYMMDDHHMMSS (24시간개념)
		//		8	PROCESS_GP		           처리구분		    VARCHAR2(1) 1:등록, 2:해제
		
		// DAO 및 UTIL 객체 생성
		YdUtils ydUtils        = new YdUtils();
		YdDaoUtils ydDaoUtils  = new YdDaoUtils();
		
		// 레코드 선언
		JDTORecord outRec      = null;

		// 변수선언
		String szMethodName    = "makeCTJ035";
		String szMsg           = "";
		String szOperationName = "생산통제 연주/후판슬라브야드 이상재 등록/해제";
		String szSLAB_NO       = "";
		
		try{
			
			outRec = JDTORecordFactory.getInstance().create();						
			outRec.setField("JMS_TC_CD"         , "YDCTJ035");
			outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));
			outRec.setField("SLAB_NO"           , ydDaoUtils.paraRecChkNull(inRec, "SLAB_NO"));
			outRec.setField("PTOP_PLNT_GP"      , ydDaoUtils.paraRecChkNull(inRec, "PTOP_PLNT_GP"));
			outRec.setField("AB_OCCR_RSN_CD"    , ydDaoUtils.paraRecChkNull(inRec, "AB_OCCR_RSN_CD"));
			outRec.setField("REGISTER"          , ydDaoUtils.paraRecChkNull(inRec, "REGISTER"));
			outRec.setField("REG_DDTT"          , YdUtils.getCurDate("yyyyMMddHHmmss"));
			outRec.setField("PROCESS_GP"        , ydDaoUtils.paraRecChkNull(inRec, "PROCESS_GP"));
			
			outRecSet.addRecord(outRec);

		}catch(Exception e){
			szMsg = "CT(생산통제) 송신  후연주/후판슬라브야드 이상재 등록/해제 데이터 반환중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}	
		
		return outRecSet.size();
	} // end of YDCTJ035()	
	
	//---------------------------------------------------------------------------
} // end of class MakeTcCT
