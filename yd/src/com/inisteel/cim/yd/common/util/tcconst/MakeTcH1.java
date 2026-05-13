package com.inisteel.cim.yd.common.util.tcconst;

import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;


/**
 * H1 (C열연MillL2) 송신 용 전문 생성
 * @author YHWHman
 *
 */
public class MakeTcH1 {

	// YDH1L001 압연분기Line-Off실적	
	// YDH1L002 재열재 Take-Out 완료


	//클래스명
	private static final String szClassName  = MakeTcH1.class.getName();


	/**
	 * YDH1L001 : 압연분기Line-Off실적
	 * @param 	JDTORecord inRec
	 * @return 	JDTORecordSet outRecSet
	 */
	public static int makeH1L001(JDTORecord inRec, JDTORecordSet outRecSet){
		YdUtils ydUtils 			= new YdUtils();
		YdDaoUtils ydDaoUtils 		= new YdDaoUtils();
		// 크레인스케줄Dao 객체 생성
		YdCrnSchDao ydCrnSchDao 	= new YdCrnSchDao();

		// 조회 결과를 담을 RecordSet생성
		JDTORecordSet rsGetYdCrnsch = JDTORecordFactory.getInstance().createRecordSet("");

		// 레코드 선언
		JDTORecord recPara 			= null;
		JDTORecord outRec 			= null;

		// 변수선언
		String szMethodName 		= "makeH1L001";
		String szMsg 				= "";
		String szOperationName      = "C열연MillL2 압연분기Line-Off실적";

		
		// 야드설비ID
		String szYD_EQP_ID	 		= "";

		// 재료번호
		String szSTL_NO	 			= "";

		// 야드적치Bed번호(권상실적위치중 끝에서 2자리)
		// YD_UP_WR_LOC (권상실적위치)
		String szYD_UP_WR_LOC       = "";
		String szYD_STK_BED_NO	 	= "";
		// TC Length =79
		int nTcLen 					= 19;
		int intRtnVal 				= 0;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH1L001() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	

			ydUtils.putLog(szClassName, szMethodName, "\n===== [makeH1L001 STEP 1]: MakeTc 들어옴 ======\n", 4);

//			// 크레인스케줄 조회
//			intRtnVal = ydCrnSchDao.getYdCrnsch(inRec, rsGetYdCrnsch, 3);
//			if(intRtnVal <= 0){
//				if(intRtnVal == 0){
//					szMsg= "STEP 1::YD_CRNSCH[크레인스케줄] SELECT Error :: DO NOT EXIST" ;
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//					return 0;
//				}else{
//					szMsg= "STEP 1::YD_CRNSCH[크레인스케줄] SELECT Error :: PARAMETER ERROR" ;
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//					return 0;
//				}	
//			}
//
//			// 크레인스케줄 조회결과 추출
//			
//			for( int i = 0; i < intRtnVal ; i++){
//
//				outRec = JDTORecordFactory.getInstance().create();
//				recPara = rsGetYdCrnsch.getRecord(i);
//
//				ydUtils.putLog(szClassName, szMethodName, "\n===== [makeH1L001 STEP 2]: 조회 결과 표시 ======\n", 4);
//				ydUtils.displayRecord(szOperationName, recPara);
//
//				// 야드설비ID
//				szYD_EQP_ID 	    = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_ID");
//
//				// 재료번호
//				szSTL_NO 		    = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
//
//				// 야드적치Bed번호
//				szYD_UP_WR_LOC      = ydDaoUtils.paraRecChkNull(recPara, "YD_UP_WR_LOC");
//				if(!szYD_UP_WR_LOC.equals("")){
//					szYD_STK_BED_NO = szYD_UP_WR_LOC.substring(6, 8);
//				}
//				else{
//					szYD_STK_BED_NO = "";
//				}
//
//
//				//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH1L001
//				//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
//				//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
//				//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
//				//		5.	전문길이					MSG_LEN					NUMBER(4)		
//				//		6.	임시						TEMP					VARCHAR2(29)		
//
//				outRec.setField("MSG_ID", 			new String("YDH1L001") );
//				outRec.setField("DATE", 			new String(YdUtils.getCurDate("yyyy-MM-dd")) );
//				outRec.setField("TIME", 			new String(YdUtils.getCurDate("HH-mm-ss")) );
//				outRec.setField("MSG_GP", 			new String("I") );
//				outRec.setField("MSG_LEN", 			new String(YdUtils.fillSpZr(""+nTcLen, 4, 0)) );
//				outRec.setField("TEMP", 			new String(YdUtils.fillSpZr("", 29, 1)) );
//
//				//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		야드구분(J) + 야드동구분 (D~H), 야드설비구분(CV01)
//				//		8.	재료번호					STL_NO					VARCHAR2(11)	
//				//		9.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		분기Converyor 인출위치 No("01"~"15")
//
//				outRec.setField("YD_EQP_ID", 		YdUtils.fillSpZr(szYD_EQP_ID, 6, 1));
//				outRec.setField("STL_NO", 			YdUtils.fillSpZr(szSTL_NO, 11, 1));
//				outRec.setField("YD_STK_BED_NO", 	YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
//				
//				// RecordSet으로 반환
//				outRecSet.addRecord(outRec);
//				
//				ydUtils.putLog(szClassName, szMethodName, "\n===== [makeH1L001 STEP 3]: 송신용 TC 내용 표시 ======\n", 4);
//
//				// Debug Msg
//				ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH1L001() OUT=========================\n", 4);	
//				ydUtils.displayRecord(szOperationName, outRec);	
//				ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
//			} // end of for
			
//			intRtnVal = ydCrnSchDao.getYdCrnsch(inRec, rsGetYdCrnsch, 3);
//			if(intRtnVal <= 0){
//				if(intRtnVal == 0){
//					szMsg= "STEP 1::YD_CRNSCH[크레인스케줄] SELECT Error :: DO NOT EXIST" ;
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//					return 0;
//				}else{
//					szMsg= "STEP 1::YD_CRNSCH[크레인스케줄] SELECT Error :: PARAMETER ERROR" ;
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//					return 0;
//				}	
//			}

			// 크레인스케줄 조회결과 추출
//				outRec = JDTORecordFactory.getInstance().create();
//				recPara = rsGetYdCrnsch.getRecord(i);

//				ydUtils.putLog(szClassName, szMethodName, "\n===== [makeH1L001 STEP 2]: 조회 결과 표시 ======\n", 4);
//				ydUtils.displayRecord(szOperationName, recPara);

				// 야드설비ID
				szYD_EQP_ID 	    = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID"); // 야드설비ID
				szSTL_NO 		    = ydDaoUtils.paraRecChkNull(inRec, "STL_NO"); // 재료번호
				szYD_STK_BED_NO		= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO"); // 야드적치Bed번호
				ydUtils.putLog(szClassName, szMethodName, szYD_EQP_ID , 4);
				ydUtils.putLog(szClassName, szMethodName, szSTL_NO , 4);
				ydUtils.putLog(szClassName, szMethodName, szYD_STK_BED_NO , 4);

				
//				szYD_UP_WR_LOC      = ydDaoUtils.paraRecChkNull(inRec, "YD_UP_WR_LOC");
//				if(!szYD_UP_WR_LOC.equals("")){
//					szYD_STK_BED_NO = szYD_UP_WR_LOC.substring(6, 8);
//				}
//				else{
//					szYD_STK_BED_NO = "";
//				}


				//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH1L001
				//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
				//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
				//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
				//		5.	전문길이					MSG_LEN					NUMBER(4)		
				//		6.	임시						TEMP					VARCHAR2(29)		
				outRec = JDTORecordFactory.getInstance().create();
				outRec.setField("MSG_ID", 			new String("YDH1L001") );
				outRec.setField("DATE", 			new String(YdUtils.getCurDate("yyyy-MM-dd")) );
				outRec.setField("TIME", 			new String(YdUtils.getCurDate("HH-mm-ss")) );
				outRec.setField("MSG_GP", 			new String("I") );
				outRec.setField("MSG_LEN", 			new String(YdUtils.fillSpZr(""+nTcLen, 4, 0)) );
				outRec.setField("TEMP", 			new String(YdUtils.fillSpZr("", 29, 1)) );

				//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		야드구분(J) + 야드동구분 (D~H), 야드설비구분(CV01)
				//		8.	재료번호					STL_NO					VARCHAR2(11)	
				//		9.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		분기Converyor 인출위치 No("01"~"15")

				outRec.setField("YD_EQP_ID", 		YdUtils.fillSpZr(szYD_EQP_ID, 6, 1));
				outRec.setField("STL_NO", 			YdUtils.fillSpZr(szSTL_NO, 11, 1));
				outRec.setField("YD_STK_BED_NO", 	YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
				
				// RecordSet으로 반환
				outRecSet.addRecord(outRec);
				
				ydUtils.putLog(szClassName, szMethodName, "\n===== [makeH1L001 STEP 3]: 송신용 TC 내용 표시 ======\n", 4);

				// Debug Msg
				ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH1L001() OUT=========================\n", 4);	
				ydUtils.displayRecord(szOperationName, outRec);	
				ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
			
			
		}catch(Exception e){
			szMsg = "YDH1L001[압연분기Line-Off실적] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}
		return outRecSet.size();
	} // end of makeH1L001()


	
	
	
	/**
	 * YDH1L002 : 재열재 Take-Out 완료(사용안함)
	 * @param 	JDTORecord inRec
	 * @return 	JDTORecordSet outRecSet
	 */
	public static int makeH1L002(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH1L001
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER  (4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		//      7.  야드설비 ID               YD_EQP_ID               VARCHAR2(6)     "CAPS01" 고정
		//      8.  적치BED.NO               YD_STK_BED_NO           VARCHAR2(2)     "01" 고정
		//      9.  재료번호                                   STL_NO                  VARCHAR2(11)
		
		// 레코드 선언
		JDTORecord outRec 			= null;

		// DAO객체 생성
		YdUtils ydUtils 			= new YdUtils();
		YdDaoUtils ydDaoUtils 		= new YdDaoUtils();
		
		String szMsg 				= "";
		String szMethodName 		= "makeH1L002";
		String szOperationName      = "C열연MillL2 재열재 Take-Out 완료";

		// 야드설비ID
		String szYD_EQP_ID	 		= "";

		// 적치BED.NO
		String szYD_STK_BED_NO      = "";
		
		// 재료번호
		String szSTL_NO	 			= "";

		// TC Length = 79 (60 + 19)
		int nTcLen 					= 19;

		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH1L002() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
			
			// 리턴 RecordSet 생성 및 Record 생성			
			outRec = JDTORecordFactory.getInstance().create();

			szYD_EQP_ID	 		= "CAPS01";
			szYD_STK_BED_NO      = "01";
			szSTL_NO 		    = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");

			outRec.setField("MSG_ID" , "YDH1L002");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));
			
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szYD_EQP_ID, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
				
			// RecordSet으로 반환
			outRecSet.addRecord(outRec);
				
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================makeH1L002() OUT==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
		}catch(Exception e){
			szMsg = "YDH1L002[재열재 Take-Out 완료] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}
		return outRecSet.size();
	} // end of makeH1L002()
} // end of class MakeTcH1
