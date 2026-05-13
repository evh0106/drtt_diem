package com.inisteel.cim.yd.common.util.tcconst;

//DAO import
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;




/**
 * PT (진행관리) 송신 용 전문 생성
 * @author YHWHman
 *
 */
public class MakeTcPT {
	// YDPTJ001 슬라브소재이송완료실적
	// YDPTJ002 코일소재이송완료실적	
	// YDPTJ003 코일소재임가공이송완료실적	
	// YDPTJ004 구입슬라브입고실적        	    (삭제)
	// YDPTJ006 냉연코일이송진행 상태실적   

	

	// 클래스명
	private static final String szClassName  = MakeTcPT.class.getName();
	
	
	
	/**
	 * YDPTJ001 : 슬라브소재이송완료실적 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makePTJ001(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 				VARCHAR2(11)	YDPTJ001
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시				DATE	(14)	YYYYMMDDHHMMSS (24시간개념)

		//		3.	재료번호				STL_NO					VARCHAR2(11)	이송완료 재료번호
		//		4.	주문번호				ORD_NO					VARCHAR2(8)	
		//		5.	주문행번				ORD_DTL					VARCHAR2(8)	
		//		6.	공장공정코드			PLNT_PROC_CD			VARCHAR2(8)	
		//		7.	재료외형구분			STL_APPEAR_GP			VARCHAR2(8)	
		//		8.	현재진도코드			CURR_PROG_CD			VARCHAR2(8)	
		//		9.	주문여재구분			ORD_YEOJAE_GP			VARCHAR2(8)	
		//		10.	재료중량				STL_WT					NUMBER  (5)		사용 안함
		//		11.	설계재료중량			DS_MTL_WT				NUMBER  (6)		사용 안함
		//		12.	재료상태구분			MTL_STAT_GP				VARCHAR2(1)		사용 안함
		//		13.	Record종료구분		RECORD_END_GP			VARCHAR2(1)		사용 안함
		//		14.	Record종료구분1		RECORD_END_GP1			VARCHAR2(1)		사용 안함
		//		15.	전진도코드			BEFO_PROG_CD			VARCHAR2(1)		사용 안함
		//		16.	전주문번호			BEF_ORD_NO				VARCHAR2(10)	사용 안함
		//		17.	전주문행번			BEF_ORD_DTL				VARCHAR2(3)		사용 안함
		//		18.	모재료번호			MMATL_FEE_NO			VARCHAR2(13)	사용 안함
		//		19.	목전충당구분			ORDERTRANS_MATCH_GP		VARCHAR2(1)		사용 안함
		
		// 레코드 선언
		JDTORecord recPara          = null;
		JDTORecord recTemp          = null;
		JDTORecordSet rsResult      = null;
		JDTORecordSet rsTemp      = null;
		JDTORecord recGetVal        = null;
		JDTORecord outRec           = null;
		
		// DAO객체 생성
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();	
		YdUtils ydUtils             = new YdUtils();

		// 변수선언
		String szMethodName         = "makePTJ001";
		String szMsg                = "";
		String szOperationName      = "진행관리 슬라브소재이송완료실적";
		
		String szRtnMsg             = "";
		String szSTLNO              = "";
		String szPT_TB_COMM         = "";

		// 리턴값
		int intRtnVal               = 0;
					
		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);	
			
			// 리턴 recordSet 생성 및 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//=======================================================================================================================
			// 차량 재료검색
			//=======================================================================================================================
			recPara.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CAR_SCH_ID").trim());
			intRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recPara, rsResult, 4);
			if(intRtnVal < 0) {
				szMsg = "차량이송재료 테이블 조회 오류  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;				
			} else if(intRtnVal == 0) {
				szMsg = "차량이송재료 테이블 조회건수 없음  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;				
			}
			
			for(int nIdx=0; nIdx<intRtnVal; nIdx++){
				recGetVal = rsResult.getRecord(nIdx);				
				szSTLNO = ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO");

				rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
	        	szRtnMsg = YdCommonUtils.getPtCommStock(szSTLNO, rsTemp);
	        	if(!szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	        		szMsg = "주편/슬라브공통테이블에서 재료[" + szSTLNO + "] 조회 시 오류발생 : " + szRtnMsg;
	                ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
	        		return -1;
	        	}			
	        	rsTemp.first();
	        	recTemp = rsTemp.getRecord();
	        	szPT_TB_COMM = ydDaoUtils.paraRecChkNull(recTemp, "PT_TB_COMM");
	        	
				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();						
	        	
	        	if(szPT_TB_COMM.equals("B")){
					// 헤더부
					outRec.setField("JMS_TC_CD", 			"YDPTJ001");
					outRec.setField("JMS_TC_CREATE_DDTT", 	YdUtils.getCurDate("yyyyMMddHHmmss"));
					
					// 재료번호
					outRec.setField("STL_NO", ydDaoUtils.paraRecChkNull(recTemp, "MSLAB_NO").trim());

					// 주문번호
					outRec.setField("ORD_NO", ydDaoUtils.paraRecChkNull(recTemp, "ORD_NO"));
					
					// 주문행번
					outRec.setField("ORD_DTL", ydDaoUtils.paraRecChkNull(recTemp, "ORD_DTL"));
					
					// 공장공정코드
					outRec.setField("PLNT_PROC_CD", ydDaoUtils.paraRecChkNull(recTemp, "PLNT_PROC_CD"));
					
					// 재료외형구분
					outRec.setField("STL_APPEAR_GP", ydDaoUtils.paraRecChkNull(recTemp, "STL_APPEAR_GP"));
					
					// 현재진도코드
					outRec.setField("CURR_PROG_CD", ydDaoUtils.paraRecChkNull(recTemp, "CURR_PROG_CD"));
					
					// 주문여재구분
					outRec.setField("ORD_YEOJAE_GP", ydDaoUtils.paraRecChkNull(recTemp, "ORD_YEOJAE_GP"));
					
					// 재료중량 (주편중량) 
					outRec.setField("STL_WT", ydDaoUtils.paraRecChkNull(recTemp, "MSLAB_WT"));
					
					
					//**************************************************************************************************************************
					// 설계재료중량
					outRec.setField("DS_MTL_WT", "");
					//**************************************************************************************************************************
					
					// 재료상태구분
					outRec.setField("MTL_STAT_GP", ydDaoUtils.paraRecChkNull(recTemp, "RECORD_PROG_STAT"));

					// Record 종료구분
					outRec.setField("RECORD_END_GP", ydDaoUtils.paraRecChkNull(recTemp, "RECORD_END_GP"));
					
					
					//**************************************************************************************************************************
					// Record 종료구분 1
					outRec.setField("RECORD_END_GP1", "");
					//**************************************************************************************************************************
					
					
					// 전진도 코드
					outRec.setField("BEFO_PROG_CD", ydDaoUtils.paraRecChkNull(recTemp, "BEFO_PROG_CD"));
					
					
					//**************************************************************************************************************************
					// 전주문 번호
					outRec.setField("BEF_ORD_NO", "");
					
					// 전주문 행번
					outRec.setField("BEF_ORD_DTL", "");

					// 모재료번호   
					outRec.setField("MMATL_FEE_NO", "");
					
					// 목전충당구분
					outRec.setField("ORDERTRANS_MATCH_GP", "");					
					//**************************************************************************************************************************
	        	
	        	} else if (szPT_TB_COMM.equals("S")) {
					outRec.setField("JMS_TC_CD", 			"YDPTJ001");
					outRec.setField("JMS_TC_CREATE_DDTT", 	YdUtils.getCurDate("yyyyMMddHHmmss"));
											
					// 재료번호
					outRec.setField("STL_NO", ydDaoUtils.paraRecChkNull(recTemp, "SLAB_NO").trim());
				
					// 주문번호
					outRec.setField("ORD_NO", ydDaoUtils.paraRecChkNull(recTemp, "ORD_NO"));
					
					// 주문행번
					outRec.setField("ORD_DTL", ydDaoUtils.paraRecChkNull(recTemp, "ORD_DTL"));
					
					// 공장공정코드
					outRec.setField("PLNT_PROC_CD", ydDaoUtils.paraRecChkNull(recTemp, "PLNT_PROC_CD"));
					
					// 재료외형구분
					outRec.setField("STL_APPEAR_GP", ydDaoUtils.paraRecChkNull(recTemp, "STL_APPEAR_GP"));
					
					// 현재진도코드
					outRec.setField("CURR_PROG_CD", ydDaoUtils.paraRecChkNull(recTemp, "CURR_PROG_CD"));
					
					// 주문여재구분
					outRec.setField("ORD_YEOJAE_GP", ydDaoUtils.paraRecChkNull(recTemp, "ORD_YEOJAE_GP"));
					
					// 재료중량 (SLAB중량) 
					outRec.setField("STL_WT", ydDaoUtils.paraRecChkNull(recTemp, "SLAB_WT"));
					
					
					//******************************************************************************************************************************
					// 설계재료중량
					outRec.setField("DS_MTL_WT", "");
					//******************************************************************************************************************************
					
					// 재료상태구분
					outRec.setField("MTL_STAT_GP", ydDaoUtils.paraRecChkNull(recTemp, "RECORD_PROG_STAT"));
					
					// Record 종료구분
					outRec.setField("RECORD_END_GP", ydDaoUtils.paraRecChkNull(recTemp, "RECORD_END_GP"));
					
					
					//******************************************************************************************************************************
					// Record 종료구분 1
					outRec.setField("RECORD_END_GP1", "");
					//******************************************************************************************************************************
					
					
					// 전진도 코드
					outRec.setField("BEFO_PROG_CD", ydDaoUtils.paraRecChkNull(recTemp, "BEFO_PROG_CD"));
					
					// 전주문 번호
					outRec.setField("BEF_ORD_NO", ydDaoUtils.paraRecChkNull(recTemp, "BEF_ORD_NO"));
					
					// 전주문 행번
					outRec.setField("BEF_ORD_DTL", ydDaoUtils.paraRecChkNull(recTemp, "BEF_ORD_DTL"));
					
					
					//******************************************************************************************************************************
					// 모재료번호   
					outRec.setField("MMATL_FEE_NO", "");
					//******************************************************************************************************************************

					
					// 목전충당구분
					outRec.setField("ORDERTRANS_MATCH_GP", ydDaoUtils.paraRecChkNull(recTemp, "MATCH_ORDERTRANS_GP"));	
	        	}
	        	
				// RecordSet에 추가
				outRecSet.addRecord(outRec);
				
				// Debug Msg
				ydUtils.putLog(szClassName, szMethodName, "\n======================OUT("+ Integer.toString(nIdx) + ")==========================\n", 4);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 4);						        	
			}
		}catch(Exception e){
			szMsg = "PT(진행관리) 송신 슬라브소재이송완료실적 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}	
		
		return outRecSet.size();
	} // end of makePTJ001()

	
	
	
	
	/**
	 * YDPTJ002 : 코일소재이송완료실적 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makePTJ002(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 				VARCHAR2(8)		YDPTJ002
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시				DATE	(14)	YYYYMMDDHHMMSS (24시간개념)

		//		3.	재료번호				STL_NO					VARCHAR2(11)	이송완료 재료번호
		//		4.	주문번호				ORD_NO					VARCHAR2(10)	
		//		5.	주문행번				ORD_DTL					VARCHAR2(3)	
		//		6.	공장공정코드			PLNT_PROC_CD			VARCHAR2(18)	
		//		7.	재료외형구분			STL_APPEAR_GP			VARCHAR2(1)	
		//		8.	현재진도코드			CURR_PROG_CD			VARCHAR2(8)	
		//		9.	주문여재구분			ORD_YEOJAE_GP			VARCHAR2(1)	
		//		10.	재료중량				STL_WT					NUMBER  (5)		사용 안함
		//		11.	설계재료중량			DS_MTL_WT				NUMBER  (6)		사용 안함
		//		12.	재료상태구분			MTL_STAT_GP				VARCHAR2(1)		사용 안함
		//		13.	Record종료구분		RECORD_END_GP			VARCHAR2(1)		사용 안함
		//		14.	Record종료구분1		RECORD_END_GP1			VARCHAR2(1)		사용 안함
		//		15.	전진도코드			BEFO_PROG_CD			VARCHAR2(1)		사용 안함
		//		16.	전주문번호			BEF_ORD_NO				VARCHAR2(10)	사용 안함
		//		17.	전주문행번			BEF_ORD_DTL				VARCHAR2(3)		사용 안함
		//		18.	모재료번호			MMATL_FEE_NO			VARCHAR2(13)	사용 안함
		//		19.	목전충당구분			ORDERTRANS_MATCH_GP		VARCHAR2(1)		사용 안함		
		
		// 레코드 선언
		JDTORecord recPara         = null;
		JDTORecordSet rsCoilResult = null;
		JDTORecord recGetVal       = null;
		JDTORecord outRec          = null;
		
		// DAO객체 생성
		YdDaoUtils ydDaoUtils      = new YdDaoUtils();	
		YdUtils ydUtils            = new YdUtils();
		YdCarSchDao ydCarSchDao    = new YdCarSchDao();
		
		// 변수선언
		String szMethodName        = "makePTJ002";
		String szMsg               = "";
		String szOperationName     = "진행관리 코일소재이송완료실적";

		// 리턴값
		int intRtnVal              = 0;
		
		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, inRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);	

			// 리턴 recordSet 생성 및 레코드 생성
			rsCoilResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();			
			
			//=======================================================================================================================
			// COIL공통 테이블 조회(차량스케쥴 + 차량이송재료 + COIL공통) (Key: 차량스케쥴 ID)
			//=======================================================================================================================
			recPara.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CAR_SCH_ID"));
			recPara.setField("STL_NO", ydDaoUtils.paraRecChkNull(inRec, "STL_NO"));
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsCoilResult, 5);		
			if(intRtnVal < 0) {
				szMsg = "COIL공통 테이블 조회오류  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			} else if(intRtnVal == 0) {
				szMsg = "COIL공통 테이블 조회건수 없음  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}				
			
			for(int nIdx=0; nIdx<intRtnVal; nIdx++) {
				recGetVal = rsCoilResult.getRecord(nIdx);	

				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();						
				outRec.setField("JMS_TC_CD", 			"YDPTJ002");
				outRec.setField("JMS_TC_CREATE_DDTT", 	YdUtils.getCurDate("yyyyMMddHHmmss"));				
				
				// 재료번호
				outRec.setField("STL_NO", ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO").trim());
				
				// 주문번호
				outRec.setField("ORD_NO", ydDaoUtils.paraRecChkNull(recGetVal, "ORD_NO"));
				
				// 주문행번
				outRec.setField("ORD_DTL", ydDaoUtils.paraRecChkNull(recGetVal, "ORD_DTL"));
				
				// 공장공정코드
				outRec.setField("PLNT_PROC_CD", ydDaoUtils.paraRecChkNull(recGetVal, "PLNT_PROC_CD"));
				
				// 재료외형구분
				outRec.setField("STL_APPEAR_GP", ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP"));
				
				// 현재진도코드
				outRec.setField("CURR_PROG_CD", ydDaoUtils.paraRecChkNull(recGetVal, "CURR_PROG_CD"));
				
				// 주문여재구분
				outRec.setField("ORD_YEOJAE_GP", ydDaoUtils.paraRecChkNull(recGetVal, "ORD_YEOJAE_GP"));
				
				// 재료중량 (COIL중량) 
				outRec.setField("STL_WT", ydDaoUtils.paraRecChkNull(recGetVal, "COIL_WT"));
				
				
				//**********************************************************************************************************************************
				// 설계재료중량
				outRec.setField("DS_MTL_WT", "");
				//**********************************************************************************************************************************
				
				
				// 재료상태구분
				outRec.setField("MTL_STAT_GP", ydDaoUtils.paraRecChkNull(recGetVal, "MTL_STAT_GP"));
				
				// Record 종료구분
				outRec.setField("RECORD_END_GP", ydDaoUtils.paraRecChkNull(recGetVal, "RECORD_END_GP"));
				
				
				//**********************************************************************************************************************************
				// Record 종료구분 1
				outRec.setField("RECORD_END_GP1", "");
				//**********************************************************************************************************************************
				
				
				// 전진도 코드
				outRec.setField("BEFO_PROG_CD", ydDaoUtils.paraRecChkNull(recGetVal, "BEFO_PROG_CD"));
				
				// 전주문 번호
				outRec.setField("BEF_ORD_NO", ydDaoUtils.paraRecChkNull(recGetVal, "BEF_ORD_NO"));
				
				// 전주문 행번
				outRec.setField("BEF_ORD_DTL", ydDaoUtils.paraRecChkNull(recGetVal, "BEF_ORD_DTL"));
				
				// 모재료번호   
				outRec.setField("MMATL_FEE_NO", ydDaoUtils.paraRecChkNull(recGetVal, "MMATL_FEE_NO"));
				
				// 목전충당구분
				outRec.setField("ORDERTRANS_MATCH_GP", ydDaoUtils.paraRecChkNull(recGetVal, "MATCH_ORDERTRANS_GP"));
				
				// RecordSet에 추가				
				outRecSet.addRecord(outRec);
		
				// Debug Msg
				ydUtils.putLog(szClassName, szMethodName, "\n======================OUT("+ Integer.toString(nIdx) + ")==========================\n", 3);	
				ydUtils.displayRecord(szOperationName, outRec);							
				ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);							
			}
		}catch(Exception e){
			szMsg = "PT(진행관리) 송신 코일소재이송완료실적 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
			return -1;
		}	
		
		return intRtnVal;
	} // end of makePTJ002()
	
	
	
	
	
	/**
	 * YDPTJ003 : 코일소재임가공이송완료실적 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makePTJ003(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 				VARCHAR2(8)		YDPTJ003
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시				DATE	(14)	YYYYMMDDHHMMSS (24시간개념)

		//		3.	재료번호				STL_NO					VARCHAR2(11)	이송완료 재료번호
		//		4.	주문번호				ORD_NO					VARCHAR2(10)	
		//		5.	주문행번				ORD_DTL					VARCHAR2(3)	
		//		6.	공장공정코드			PLNT_PROC_CD			VARCHAR2(18)	
		//		7.	재료외형구분			STL_APPEAR_GP			VARCHAR2(1)	
		//		8.	현재진도코드			CURR_PROG_CD			VARCHAR2(8)	
		//		9.	주문여재구분			ORD_YEOJAE_GP			VARCHAR2(1)	
		//		10.	재료중량				STL_WT					NUMBER  (5)		사용 안함
		//		11.	설계재료중량			DS_MTL_WT				NUMBER  (6)		사용 안함
		//		12.	재료상태구분			MTL_STAT_GP				VARCHAR2(1)		사용 안함
		//		13.	Record종료구분		RECORD_END_GP			VARCHAR2(1)		사용 안함
		//		14.	Record종료구분1		RECORD_END_GP1			VARCHAR2(1)		사용 안함
		//		15.	전진도코드			BEFO_PROG_CD			VARCHAR2(1)		사용 안함
		//		16.	전주문번호			BEF_ORD_NO				VARCHAR2(10)	사용 안함
		//		17.	전주문행번			BEF_ORD_DTL				VARCHAR2(3)		사용 안함
		//		18.	모재료번호			MMATL_FEE_NO			VARCHAR2(13)	사용 안함
		//		19.	목전충당구분			ORDERTRANS_MATCH_GP		VARCHAR2(1)		사용 안함
		
		// 레코드 선언
		JDTORecord recPara         = null;
		JDTORecordSet rsCoilResult = null;
		JDTORecord recGetVal       = null;
		JDTORecord outRec          = null;

		// DAO객체 생성
		YdDaoUtils ydDaoUtils      = new YdDaoUtils();	
		YdUtils ydUtils            = new YdUtils();
		YdCarSchDao ydCarSchDao    = new YdCarSchDao();
		
		// 변수선언
		String szMethodName        = "makePTJ003";
		String szMsg               = "";
		String szOperationName     = "진행관리 코일소재임가공이송완료실적";
		
		// 리턴값
		int intRtnVal              = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);	
			
			// 리턴 recordSet 생성 및 레코드 생성
			rsCoilResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			
			//=======================================================================================================================
			// COIL공통 테이블 조회(차량스케쥴 + 차량이송재료 + COIL공통) (Key: 차량스케쥴 ID)
			//=======================================================================================================================
			recPara.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CAR_SCH_ID"));
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsCoilResult, 5);		
			if(intRtnVal < 0) {
				szMsg = "COIL공통 테이블 조회오류  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			} else if(intRtnVal == 0) {
				szMsg = "COIL공통 테이블 조회건수 없음  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}				

			for(int nIdx=0; nIdx<intRtnVal; nIdx++) {
				recGetVal = rsCoilResult.getRecord(nIdx);
		
				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();						
				outRec.setField("JMS_TC_CD", 			"YDPTJ003");
				outRec.setField("JMS_TC_CREATE_DDTT", 	YdUtils.getCurDate("yyyyMMddHHmmss"));
		
				// 재료번호
				outRec.setField("STL_NO", ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO").trim());
				
				// 주문번호
				outRec.setField("ORD_NO", ydDaoUtils.paraRecChkNull(recGetVal, "ORD_NO"));
				
				// 주문행번
				outRec.setField("ORD_DTL", ydDaoUtils.paraRecChkNull(recGetVal, "ORD_DTL"));
				
				// 공장공정코드
				outRec.setField("PLNT_PROC_CD", ydDaoUtils.paraRecChkNull(recGetVal, "PLNT_PROC_CD"));
				
				// 재료외형구분
				outRec.setField("STL_APPEAR_GP", ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP"));
				
				// 현재진도코드
				outRec.setField("CURR_PROG_CD", ydDaoUtils.paraRecChkNull(recGetVal, "CURR_PROG_CD"));
				
				// 주문여재구분
				outRec.setField("ORD_YEOJAE_GP", ydDaoUtils.paraRecChkNull(recGetVal, "ORD_YEOJAE_GP"));
				
				// 재료중량 (COIL중량) 
				outRec.setField("STL_WT", ydDaoUtils.paraRecChkNull(recGetVal, "COIL_WT"));
				
				
				//**********************************************************************************************************************************
				// 설계재료중량
				outRec.setField("DS_MTL_WT", "");
				//**********************************************************************************************************************************

				
				// 재료상태구분
				outRec.setField("MTL_STAT_GP", ydDaoUtils.paraRecChkNull(recGetVal, "MTL_STAT_GP"));
				
				// Record 종료구분
				outRec.setField("RECORD_END_GP", ydDaoUtils.paraRecChkNull(recGetVal, "RECORD_END_GP"));
				
				// Record 종료구분 1
				outRec.setField("RECORD_END_GP1", ydDaoUtils.paraRecChkNull(recGetVal, "RECORD_END_GP1"));
				
				// 전진도 코드
				outRec.setField("BEFO_PROG_CD", ydDaoUtils.paraRecChkNull(recGetVal, "BEFO_PROG_CD"));
				
				// 전주문 번호
				outRec.setField("BEF_ORD_NO", ydDaoUtils.paraRecChkNull(recGetVal, "BEF_ORD_NO"));
				
				// 전주문 행번
				outRec.setField("BEF_ORD_DTL", ydDaoUtils.paraRecChkNull(recGetVal, "BEF_ORD_DTL"));
				
				// 모재료번호   
				outRec.setField("MMATL_FEE_NO", ydDaoUtils.paraRecChkNull(recGetVal, "MMATL_FEE_NO"));
				
				// 목전충당구분
				outRec.setField("ORDERTRANS_MATCH_GP", ydDaoUtils.paraRecChkNull(recGetVal, "MATCH_ORDERTRANS_GP"));

				// RecordSet에 추가				
				outRecSet.addRecord(outRec);
				
				// Debug Msg
				ydUtils.putLog(szClassName, szMethodName, "\n======================OUT("+ Integer.toString(nIdx) + ")==========================\n", 3);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);				
			}
		}catch(Exception e){
			szMsg = "PT(진행관리) 송신 코일소재임가공이송완료실적 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
			return -1;
		}	
		
		return intRtnVal;		
	} // end of makePTJ003()

	
	
	
	
	/**
	 * YDPTJ004 : 구입슬라브입고실적  (삭제된 전문)
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makePTJ004(JDTORecord inRec, JDTORecordSet outRecSet){
		JDTORecord outRec      = null;
		
		YdUtils ydUtils        = new YdUtils();
		
		// 변수선언
		String szMethodName    = "makePTJ004";
		String szOperationName = "진행관리 구입슬라브입고실적";
		
		// 리턴값
		int intRtnVal          = 0;
		
		try{

			//		1	JMS_TC_CD			JMSTCCODE 				VARCHAR2(8)		YDPTJ004
			//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시				DATE	(14)	YYYYMMDDHHMMSS (24시간개념)

			outRec = JDTORecordFactory.getInstance().create();						
			outRec.setField("JMS_TC_CD", 			"YDPTJ004");
			outRec.setField("JMS_TC_CREATE_DDTT", 	YdUtils.getCurDate("yyyyMMddHHmmss"));
			
			
			//		3.	재료번호				STL_NO					VARCHAR2(11)	이송완료 재료번호
			//		4.	주문번호				ORD_NO					VARCHAR2(8)	
			//		5.	주문행번				ORD_DTL					VARCHAR2(8)	
			//		6.	공장공정코드			PLNT_PROC_CD			VARCHAR2(8)	
			//		7.	재료외형구분			STL_APPEAR_GP			VARCHAR2(8)	
			//		8.	현재진도코드			CURR_PROG_CD			VARCHAR2(8)	
			//		9.	주문여재구분			ORD_YEOJAE_GP			VARCHAR2(8)	
			//		10.	재료중량				STL_WT					NUMBER(5,0)		사용 안함
			//		11.	설계재료중량			DS_MTL_WT				NUMBER(6,0)		사용 안함
			//		12.	재료상태구분			MTL_STAT_GP				VARCHAR2(1)		사용 안함
			//		13.	Record종료구분		RECORD_END_GP			VARCHAR2(1)		사용 안함
			//		14.	Record종료구분1		RECORD_END_GP1			VARCHAR2(1)		사용 안함
			//		15.	전진도코드			BEFO_PROG_CD			VARCHAR2(1)		사용 안함
			//		16.	전주문번호			BEF_ORD_NO				VARCHAR2(10)	사용 안함
			//		17.	전주문행번			BEF_ORD_DTL				VARCHAR2(3)		사용 안함
			//		18.	모재료번호			MMATL_FEE_NO			VARCHAR2(13)	사용 안함
			//		19.	목전충당구분			ORDERTRANS_MATCH_GP		VARCHAR2(1)		사용 안함

			outRec = JDTORecordFactory.getInstance().create();						

			// outRec 처리 . . .
			
			// RecordSet에 추가				
			outRecSet.addRecord(outRec);			

		}catch(Exception e){

			return -1;
		}	
		
		ydUtils.putLog(szClassName, szMethodName, "\n======================OUT==========================\n", 3);	
		ydUtils.displayRecord(szOperationName, outRec);
		ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);			
		
		return intRtnVal;
	} // end of makePTJ004()
  //---------------------------------------------------------------------------	
	
	/**
	 * YDPTJ005 : 진행관리 후판오버롤체크  
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makePTJ005(JDTORecord inRec, JDTORecordSet outRecSet){
		JDTORecord outRec      = null;
		
		YdDaoUtils ydDaoUtils      = new YdDaoUtils();	
		
		// 변수선언
		String szMethodName    = "makePTJ005";
		String szOperationName = "진행관리 후판오버롤체크";
		
		// 리턴값
		int intRtnVal          = 1;
		
		try{

			//		1	JMS_TC_CD			JMSTCCODE 				VARCHAR2(8)		YDPTJ004
			//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시				DATE	(14)	YYYYMMDDHHMMSS (24시간개념)
			outRec = JDTORecordFactory.getInstance().create();						
			outRec.setField("JMS_TC_CD", 			"YDPTJ005");
			outRec.setField("JMS_TC_CREATE_DDTT", 	YdUtils.getCurDate("yyyyMMddHHmmss"));
			outRec.setField("JMS_TC_CODE",     		ydDaoUtils.paraRecChkNull(inRec, "JMS_TC_CODE"));
			outRec.setField("STL_NO",      			ydDaoUtils.paraRecChkNull(inRec, "STL_NO"));
			outRec.setField("ORD_NO",      			ydDaoUtils.paraRecChkNull(inRec, "ORD_NO"));
			outRec.setField("ORD_DTL",      		ydDaoUtils.paraRecChkNull(inRec, "ORD_DTL"));
			outRec.setField("PLNT_PROC_CD",      	ydDaoUtils.paraRecChkNull(inRec, "PLNT_PROC_CD"));
			outRec.setField("STL_APPEAR_GP",      	ydDaoUtils.paraRecChkNull(inRec, "STL_APPEAR_GP"));
			outRec.setField("CURR_PROG_CD",      	ydDaoUtils.paraRecChkNull(inRec, "CURR_PROG_CD"));
			outRec.setField("ORD_YEOJAE_GP",      	ydDaoUtils.paraRecChkNull(inRec, "ORD_YEOJAE_GP"));
			outRec.setField("STL_WT",      			ydDaoUtils.paraRecChkNull(inRec, "STL_WT"));
			outRec.setField("DS_MTL_WT",      		ydDaoUtils.paraRecChkNull(inRec, "DS_MTL_WT"));
			outRec.setField("MTL_STAT_GP",      	ydDaoUtils.paraRecChkNull(inRec, "MTL_STAT_GP"));
			outRec.setField("RECORD_END_GP",      	ydDaoUtils.paraRecChkNull(inRec, "RECORD_END_GP"));
			outRec.setField("RECORD_END_GP1",      	ydDaoUtils.paraRecChkNull(inRec, "RECORD_END_GP1"));
			outRec.setField("BEFO_PROG_CD",     	ydDaoUtils.paraRecChkNull(inRec, "BEFO_PROG_CD"));
			outRec.setField("BEF_ORD_NO",     		ydDaoUtils.paraRecChkNull(inRec, "BEF_ORD_NO"));
			outRec.setField("BEF_ORD_DTL",     		ydDaoUtils.paraRecChkNull(inRec, "BEF_ORD_DTL"));
			outRec.setField("MMATL_FEE_NO",     	ydDaoUtils.paraRecChkNull(inRec, "MMATL_FEE_NO"));
			outRec.setField("ORDERTRANS_MATCH_GP", 	ydDaoUtils.paraRecChkNull(inRec, "ORDERTRANS_MATCH_GP"));
			// RecordSet에 추가				
			outRecSet.addRecord(outRec);			

		}catch(Exception e){

			return -1;
		}	
		
		return intRtnVal;
	} // end of makePTJ005()
	
	
	/**
	 * YDPTJ006 : 진행관리 냉연코일이송진행 상태실적   
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makePTJ006(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 				VARCHAR2(8)		YDPTJ002
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시				DATE	(14)	YYYYMMDDHHMMSS (24시간개념)

		//		3.	재료번호				STL_NO					VARCHAR2(11)	이송완료 재료번호
		//		4.	주문번호				ORD_NO					VARCHAR2(10)	
	
		
		// 레코드 선언
		JDTORecord recPara         = null;
		JDTORecordSet rsCoilResult = null;
		JDTORecord recGetVal       = null;
		JDTORecord outRec          = null;
		
		// DAO객체 생성
		YdDaoUtils ydDaoUtils      = new YdDaoUtils();	
		YdUtils ydUtils            = new YdUtils();
		YdCarSchDao ydCarSchDao    = new YdCarSchDao();
		YdStockDao  ydStockDao     = new YdStockDao();
		// 변수선언
		String szMethodName        = "makePTJ006";
		String szMsg               = "";
		String szOperationName     = "진행관리 냉연코일이송진행";

		// 리턴값
		int intRtnVal              = 0;
		
		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", 3);	
			ydUtils.displayRecord(szOperationName, inRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);	
 
				recPara = JDTORecordFactory.getInstance().create();	
				recPara.setField("STL_NO", ydDaoUtils.paraRecChkNull(inRec, "STL_NO"));
				
				/* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtCoilcommSUBLOC */	        
		        intRtnVal = ydStockDao.updPtComm_LOC(recPara, 4);

			
				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();						
				outRec.setField("JMS_TC_CD", 			"YDPTJ006");
				outRec.setField("JMS_TC_CREATE_DDTT", 	YdUtils.getCurDate("yyyyMMddHHmmss"));	
				
				
				// 재료번호
				outRec.setField("STL_NO", ydDaoUtils.paraRecChkNull(inRec, "STL_NO").trim());				
				// 소재이송상태구분
				outRec.setField("MATL_FTMV_STAT_GP", "6");
				 
					
				// RecordSet에 추가				
				outRecSet.addRecord(outRec);
		
				// Debug Msg
				ydUtils.putLog(szClassName, szMethodName, "\n======================OUT("+ ydDaoUtils.paraRecChkNull(inRec, "STL_NO") + ")==========================\n", 3);	
				ydUtils.displayRecord(szOperationName, outRec);							
				ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", 3);							
			 
		}catch(Exception e){
			szMsg = "PT(진행관리) 송신 코일소재이송완료실적 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
			return -1;
		}	
		
		return intRtnVal;
	} // end of makePTJ006()
  //---------------------------------------------------------------------------	
} // end of class MakeTcPT
