package com.inisteel.cim.yd.common.util.tcconst;

import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;

import jspeed.base.record.JDTORecord; 
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;





/**
 * CS (연주조업) 송신 용 전문 생성
 * @author YHWHman
 *
 */

public class MakeTcCS {
	// YDCSJ001 슬라브수입실적	
	// YDCSJ002 슬라브이송지시요구
	
	

	
	
	// 클래스명
	private static final String szClassName  = MakeTcCS.class.getName();
	
	
	/**
	 * YDCSJ001 : 슬라브수입실적 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeCSJ001(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 		VARCHAR2(8)			YDCSJ001
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시		DATE	(14)		YYYYMMDDHHMMSS (24시간개념)

		//		3	MOD_GP				수정구분			VARCHAR2(1)			신규(I), 수정(U), 취소(D)
		//		4	YD_GP				야드구분			VARCHAR2(1)			연주전단슬라브 적치야드, 이송상차야드. 이송하차야드, 구입재 입고야드
		//		5	STL_APPEAR_GP		재료외형구분		VARCHAR2(1)			주편/슬라브/코일
		//		6	YD_STR_LOC			야드저장위치		VARCHAR2(10)		대표저장위치(최 하단의 재료)
		//		7	YD_DN_RSLT_DT		야드권하완료일시	DATE(14)			크레인이 야드에 권하완료한 시점(연주슬라브야드 수입일시, 부두야드입고일시)
		//		8	YD_EQP_WR_CNT		야드설비작업매수	NUMBER(2)			크레인 Handling 매수
		//		9	STL_NO1				재료번호1		    VARCHAR2(11)		크레인 Handling, 차량 실적에 포함된 재료  
		//		10	STL_NO2				재료번호2		    VARCHAR2(11)			
		//		11	STL_NO3				재료번호3		    VARCHAR2(11)			
		//		12	STL_NO4				재료번호4	 	    VARCHAR2(11)			
		//		13	STL_NO5				재료번호5		    VARCHAR2(11)			
		//		14	STL_NO6				재료번호6			VARCHAR2(11)			
		//		15	STL_NO7				재료번호7			VARCHAR2(11)			
		//		16	STL_NO8				재료번호8			VARCHAR2(11)			
		//		17	STL_NO9				재료번호9			VARCHAR2(11)			
		//		18	STL_NO10			재료번호10		VARCHAR2(11)			
		//		19	STL_NO11			재료번호11		VARCHAR2(11)			
		//		20	STL_NO12			재료번호12		VARCHAR2(11)	
		
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
		String szMethodName     = "makeCSJ001";
		String szMsg            = "";
		String szOperationName  = "연주조업 슬라브수입실적";
				
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
				
			//=======================================================================================================================
			// 크레인스케쥴+크레인작업재료 테이블 조회 (Key: 크레인스케쥴 ID)
			//=======================================================================================================================
			 recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID"));
			 intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsResult, 11);
			if(intRtnVal < 0) {
				szMsg = "크레인스케쥴 테이블 조회오류  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			} else if(intRtnVal == 0) {
				szMsg = "크레인스케쥴 테이블 조회건수 없음  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			}		
			
			outRec = JDTORecordFactory.getInstance().create();						

			rsResult.first();
			recGetVal = rsResult.getRecord();			

			// 헤더부
			outRec.setField("JMS_TC_CD"         , "YDCSJ001");
			outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));

			// 수정구분 [I] 
			outRec.setField("MOD_GP", "I");

			// 야드구분 [야드구분]
			outRec.setField("YD_GP", ydDaoUtils.paraRecChkNull(recGetVal, "YD_GP"));

			// 재료외형구분 [재료외형구분]
			outRec.setField("STL_APPEAR_GP", ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP"));

			// 야드저장위치 [야드권하실적위치]
			outRec.setField("YD_STR_LOC", ydDaoUtils.paraRecChkNull(recGetVal, "YD_DN_WR_LOC"));
			
			// 야드권하완료일시 [야드권하 완료일시]
			outRec.setField("YD_DN_RSLT_DT", ydDaoUtils.paraRecChkNull(recGetVal, "YD_DN_CMPL_DT"));

			// 야드설비작업매수 [보조작업여부가 Y가아닌 Record읽은 수]
			outRec.setField("YD_EQP_WR_CNT", Integer.toString(intRtnVal));

			for(int nIdx=0; nIdx<intRtnVal; nIdx++) {
				recGetVal = rsResult.getRecord(nIdx);			
			
				// 재료번호 [재료번호]
				outRec.setField("STL_NO"+(1+nIdx), ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO"));
			}
			
			// RecordSet에 추가				
			outRecSet.addRecord(outRec);
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================OUT==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);							
		}catch(Exception e){
			szMsg = "CS(연주조업) 송신 슬라브수입실적  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}	
	
		return outRecSet.size();
	} // end of makeCSJ001()	
	
	
	/**
	 * YDCSJ001 : 슬라브이송지시요구 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeCSJ002(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 		VARCHAR2(8)			YDCSJ001
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시		DATE	(14)		YYYYMMDDHHMMSS (24시간개념)

		//		3	MOD_GP				수정구분			VARCHAR2(1)			신규(I), 수정(U), 취소(D)
		//		4	YD_GP				야드구분			VARCHAR2(1)			연주전단슬라브 적치야드, 이송상차야드. 이송하차야드, 구입재 입고야드
		//		5	STL_APPEAR_GP		재료외형구분		VARCHAR2(1)			주편/슬라브/코일
		//		6	YD_STR_LOC			야드저장위치		VARCHAR2(10)		대표저장위치(최 하단의 재료)
		//		7	YD_DN_RSLT_DT		야드권하완료일시	DATE(14)			크레인이 야드에 권하완료한 시점(연주슬라브야드 수입일시, 부두야드입고일시)
		//		8	YD_EQP_WR_CNT		야드설비작업매수	NUMBER(2)			크레인 Handling 매수
		//		9	STL_NO				재료번호		    VARCHAR2(11)		크레인 Handling, 차량 실적에 포함된 재료  
 	
		
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
		String szMethodName     = "makeCSJ002";
		String szMsg            = "";
		String szOperationName  = "슬라브이송지시요구";
				
		// 리턴값
		int intRtnVal           = 0;
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);				
	 
			
			outRec = JDTORecordFactory.getInstance().create();						
 		

			// 헤더부
			outRec.setField("JMS_TC_CD"         , "YDCSJ002");
			outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));

			// 수정구분 [I] 
			outRec.setField("MOD_GP", "I"); 
			
			outRec.setField("STL_NO", ydDaoUtils.paraRecChkNull(inRec, "STL_NO"));
			
			outRec.setField("YD_GP" , ydDaoUtils.paraRecChkNull(inRec, "YD_GP"));
	 
			
			// RecordSet에 추가				
			outRecSet.addRecord(outRec);
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================OUT==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);							
		}catch(Exception e){
			szMsg = "CS(연주조업) 송신 슬라브이송지시요구   데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}	
	
		return outRecSet.size();
	} // end of makeCSJ002()	
  //---------------------------------------------------------------------------
} // end of class
