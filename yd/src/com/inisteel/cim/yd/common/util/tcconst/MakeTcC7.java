package com.inisteel.cim.yd.common.util.tcconst;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;


/**
 * C7 (C연주2정정L2) 송신 용 전문 생성
 * @author YHWHman
 *
 */
public class MakeTcC7 {
	// YDC7L001	수불구변경응답	
	// YDC7L002	OHC Take-Out 완료	
	// YDC7L003	Carry-Out완료	
	// YDC7L008	OHC TAKE IN 완료
	
	// 클래스명
	private static final String szClassName  = MakeTcC7.class.getName();
	
	
	/**
	 * YDC7L001 : 수불구변경응답 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeC7L001(JDTORecord inRec, JDTORecordSet outRecSet){
		
		//		1.	전문 ID				MSG_ID					VARCHAR2(8)		YDC7L001
		//		2.	생성일				DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간				TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분				MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이				MSG_LEN					NUMBER  (4)		
		//		6.	임시					TEMP					VARCHAR2(29)
		
		//		7.	야드설비ID			YD_EQP_ID				VARCHAR2(6)		수불구 야드설비ID (DD 참조)
		//		8.	야드적치Bed용도구분1	YD_STK_BED_USG_GP1	    VARCHAR2(1)		수입구('S'),불출구('B'),Close('C')
		//		9.	야드적치Bed용도구분2	YD_STK_BED_USG_GP2	    VARCHAR2(1)		
		//		10.	야드적치Bed용도구분3	YD_STK_BED_USG_GP3	    VARCHAR2(1)		
		//		11.	야드적치Bed용도구분4	YD_STK_BED_USG_GP4  	VARCHAR2(1)		
		//		12.	야드적치Bed용도구분5	YD_STK_BED_USG_GP5	    VARCHAR2(1)		
		//		13.	야드적치Bed용도구분6	YD_STK_BED_USG_GP6  	VARCHAR2(1)
		//		14.	야드적치Bed용도구분7	YD_STK_BED_USG_GP7  	VARCHAR2(1)
				
		// 레코드 선언
		JDTORecordSet rsResult  = null;
		JDTORecord recPara      = null;
		JDTORecord recGetVal    = null;
		JDTORecord outRec       = null;
		
		// DAO객체 생성
		YdDaoUtils ydDaoUtils   = new YdDaoUtils();	
		YdUtils    ydUtils      = new YdUtils();
		YdStkBedDao ydStkBedDao = new YdStkBedDao();

		// 변수선언
		String szMethodName     = "makeC7L001";
		String szMsg            = "";
		String szOperationName  = "C연주2정정L2 수불구변경응답";
		
		// 야드설비ID
		String szYdEqpId        = "";
		
		// TC Length = 73 (60 + 13)
		int nTcLen              = 13;
		
		int iArry				= 7;
		
		// 인덱스
		int nIdx                = 0;
		
		// 리턴값
		int intRtnVal           = 0;
		
		try{			

			// 리턴 RecordSet 생성 및 Record 생성			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();						

			//=======================================================================================================================
			// 적치BED 테이블 조회 (전문에 따라 7개로 고정, 두번째 파라미터는 건수로 처리)
			//=======================================================================================================================
			recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP"));
			recPara.setField("YD_STK_BED_NO", "07");
			intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsResult, 10);
			if(intRtnVal < 0) {
				szMsg = "[MakeTcC7::makeC7L001()] 적치BED 테이블 조회오류  (" + ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP") + ")(6) " + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			} else if(intRtnVal == 0){
				szMsg = "[MakeTcC7::makeC7L001()] 적치BED 테이블 조회건수가 없음 " + ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP") + ")(6)";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;				
			}

			// 야드설비ID [적치열구분]
			szYdEqpId = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP");     
			
			outRec = JDTORecordFactory.getInstance().create();

			// 헤더부
			outRec.setField("MSG_ID"   , "YDC7L001");
			outRec.setField("DATE"     , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"     , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP"   , "I");
			outRec.setField("MSG_LEN"  , YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"     , YdUtils.fillSpZr("", 29, 1));                                                         

			// 야드설비ID
			outRec.setField("YD_EQP_ID", YdUtils.fillSpZr(szYdEqpId, 6, 1));

			// MAX 7건
			intRtnVal = (intRtnVal >= iArry) ? iArry : intRtnVal;
			
			// 적치 BED 용도구분 [적치 BED 용도구분]
			for(nIdx=0; nIdx<intRtnVal; nIdx++) {
				
				recGetVal = rsResult.getRecord(nIdx);	
				outRec.setField("YD_STK_BED_USG_GP"+(1+nIdx), YdUtils.fillSpZr(recGetVal.getFieldString("YD_STK_BED_USG_GP"), 1, 1));							
			}

			// 공백 건수처리
			for(int nIdx2 = nIdx; nIdx2 < iArry; nIdx2++){
				outRec.setField("YD_STK_BED_USG_GP"+(1+nIdx2), YdUtils.fillSpZr(" ", 1, 1));											
			}
			
			// RecordSet에 추가				
			outRecSet.addRecord(outRec);
			
		}catch(Exception e){
			szMsg = "[MakeTcC7::makeC7L001()] C7(C연주2정정L2) 송신  수불구변경응답  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}
		
		return outRecSet.size();	
	} // end of makeC7L001()	


	
	
	
	/**
	 * YDC7L002 : OHC Take-Out완료 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeC7L002(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID				MSG_ID					VARCHAR2(8)		YDC7L002
		//		2.	생성일				DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간				TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분				MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이				MSG_LEN					NUMBER  (4)		
		//		6.	임시					TEMP					VARCHAR2(29)	
		                                
		//		7	야드설비ID			YD_EQP_ID	 			VARCHAR2(6)
		//		8	야드적치Bed번호		YD_STK_BED_NO			VARCHAR2(2)
		//		9	재료번호				STL_NO					VARCHAR2(11)
		//      10    작업구분                           YD_WRK_GP               VARCHAR2(1)     1: 권상     2:권하
		//		11	야드적치Bed재료매수	YD_STK_BED_STL_SH		NUMBER  (3)
		//		12	재료번호1				STL_NO1					VARCHAR2(11)
		//		13	재료번호2				STL_NO2					VARCHAR2(11)
		//		14	재료번호3				STL_NO3					VARCHAR2(11)
		//		15	재료번호4				STL_NO4					VARCHAR2(11)
		//		16	재료번호5				STL_NO5					VARCHAR2(11)
		//		17	재료번호6				STL_NO6					VARCHAR2(11)
		//		18	재료번호7				STL_NO7					VARCHAR2(11)
		//		19	재료번호8				STL_NO8					VARCHAR2(11)
		//		20	재료번호9				STL_NO9					VARCHAR2(11)
		//		21	재료번호10			STL_NO10				VARCHAR2(11)
		//		22	재료번호11			STL_NO11				VARCHAR2(11)
		//		23	재료번호12			STL_NO12				VARCHAR2(11)
		//		24	재료번호13			STL_NO13				VARCHAR2(11)
		//		25	재료번호14			STL_NO14				VARCHAR2(11)
		//		26	재료번호15			STL_NO15				VARCHAR2(11)
		//		27	재료번호16			STL_NO16				VARCHAR2(11)
		//		28	재료번호17			STL_NO17				VARCHAR2(11)
		//		29	재료번호18			STL_NO18				VARCHAR2(11)
		//		30	재료번호19			STL_NO19				VARCHAR2(11)
		//		31	재료번호20			STL_NO20				VARCHAR2(11)
		
		// 레코드 선언
		JDTORecordSet rsResult  = null;
		JDTORecord recPara      = null;
		JDTORecord recGetVal    = null;
		JDTORecord outRec       = null;
		
		// DAO객체 생성
		YdDaoUtils ydDaoUtils   = new YdDaoUtils();	
		YdUtils ydUtils         = new YdUtils();
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		// 변수선언
		String szMethodName     = "makeC7L002";
		String szMsg            = "";
		String szOperationName  = "C연주2정정L2 OHC Take-Out완료";
		
		// 야드권상위치
		String szYdUpWrLoc      = "";
		
		// 야드 권하위치
		String szYdDnWrLoc      = "";
		
		// 야드 설비ID
		String szEqpId          = "";
		
		// 야드 적치 BED번호
		String szBedNo          = "";
		
		// 작업구분
		String szWrkGp          = "";
		String szCrnSchId       = "";

		
		// 권상 권하 체크
		String szYD_WRK_PROG_STAT = "";
		
		// 인덱스
		int nIdx                = 0;

		// TC Length = 303 (60 + 243)
		int nTcLen              = 243;

		// 리턴값
		int intRtnVal           = 0;
		
		try{						
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeC7L002() IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);

			// 레코드 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();			
			
			szCrnSchId = ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID");

			//=======================================================================================================================
			// 크레인 스케쥴 조회
			//=======================================================================================================================
			recPara.setField("YD_CRN_SCH_ID", szCrnSchId);
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsResult, 3);
			if(intRtnVal < 0) {
				szMsg = "[MakeTcC7::makeC7L002()] 크레인스케쥴 + 크레인작업재료 + 저장품 테이블 조회실패  (" + ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID") + ") " + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			}else if(intRtnVal == 0){
				szMsg = "[MakeTcC7::makeC7L002()] 크레인스케쥴 + 크레인작업재료 + 저장품 테이블 조회건수가 없음 (" + ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID") + ") " + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;				
			} else {
				szMsg = "[MakeTcC7::makeC7L002()] 크레인스케줄 + 크레인작업재료 + 저장품 테이블 조회성공";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
			}

			rsResult.first();
			recGetVal = rsResult.getRecord();

			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();								
			outRec.setField("MSG_ID" , "YDC7L002");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			szYD_WRK_PROG_STAT = YdUtils.fillSpZr(recGetVal.getFieldString("YD_WRK_PROG_STAT"), 1, 1);
			
			// 권상위치
			szYdUpWrLoc = YdUtils.fillSpZr(recGetVal.getFieldString("YD_UP_WR_LOC"), 8, 1).trim();

			// 권하위치
			szYdDnWrLoc = YdUtils.fillSpZr(recGetVal.getFieldString("YD_DN_WR_LOC"), 8, 1).trim();
			
			szMsg = "[MakeTcC7::makeC7L002()] 작업진행 상태 (" + szYD_WRK_PROG_STAT + ")";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
			if(szYD_WRK_PROG_STAT.equals("2")){
				// 권상실적 위치에서 설비ID와 BED번호 처리
				szEqpId = szYdUpWrLoc.substring(0, 6);
				szBedNo = szYdUpWrLoc.substring(6, 8);

				// 야드설비ID [야드설비ID]  
				outRec.setField("YD_EQP_ID", szEqpId);

				// 야드적치Bed번호 [야드적치Bed번호]
				outRec.setField("YD_STK_BED_NO", szBedNo);

				// 재료번호 [Take-Out 재료번호(야드 L3에서 Take-Out없이 송신할 경우 Space)]
				outRec.setField("STL_NO", YdUtils.fillSpZr(recGetVal.getFieldString("STL_NO"), 11, 1));		
				
				// 작업구분
				szWrkGp = "1";
				outRec.setField("YD_WRK_GP", YdUtils.fillSpZr(szWrkGp, 1, 1));	
				
				// 야드적치BED재료매수
				outRec.setField("YD_STK_BED_STL_SH", YdUtils.fillSpZr("1", 3, 1));		

				// 공백 건수처리
				outRec.setField("STL_NO1", YdUtils.fillSpZr(recGetVal.getFieldString("STL_NO"), 11, 1));
				
				for(int i=1; i<20; i++){
					outRec.setField("STL_NO" + Integer.toString(i+1), YdUtils.fillSpZr(" ", 11, 1));						
				}
			} else if(szYD_WRK_PROG_STAT.equals("4")){
				// 권하실적 위치에서 설비ID와 BED번호 처리
				szEqpId = szYdDnWrLoc.substring(0, 6);
				szBedNo = szYdDnWrLoc.substring(6, 8);
				
				// 야드설비ID [야드설비ID]  
				outRec.setField("YD_EQP_ID", szEqpId);

				// 야드적치Bed번호 [야드적치Bed번호] 
				outRec.setField("YD_STK_BED_NO", szBedNo);
				
				// 재료번호 [Take-Out 재료번호(야드 L3에서 Take-Out없이 송신할 경우 Space)]
				outRec.setField("STL_NO", YdUtils.fillSpZr(recGetVal.getFieldString("STL_NO"), 11, 1));		
				
				// 작업구분
				szWrkGp = "2";
				outRec.setField("YD_WRK_GP", YdUtils.fillSpZr(szWrkGp, 1, 1));	
				
				//=======================================================================================================================
				// 적치단 테이블에서 설비ID(적치열구분), BED.No, 야드적치단재료상태로 재료정보 조회
				//=======================================================================================================================
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recPara = JDTORecordFactory.getInstance().create();			
				recPara.setField("YD_STK_COL_GP"      , szEqpId);
				recPara.setField("YD_STK_BED_NO"      , szBedNo);
				recPara.setField("YD_STK_LYR_MTL_STAT", "C");
				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 72);
				if(intRtnVal < 0){
					szMsg = "[MakeTcC7::makeC7L002()] 적치단 테이블 조회오류 [" + intRtnVal + "] YD_STK_COL_GP(" + szEqpId + ") YD_STK_BED_NO(" + szBedNo + ") YD_STK_LYR_MTL_STAT(C)";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
					return 0;				
				} else if(intRtnVal == 0){
					szMsg = "[MakeTcC7::makeC7L002()] 적치단 테이블 조회건수가 없음 [" + intRtnVal + "] YD_STK_COL_GP(" + szEqpId + ") YD_STK_BED_NO(" + szBedNo + ") YD_STK_LYR_MTL_STAT(C)";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
					return 0;								
				} else {
					szMsg = "[MakeTcC7::makeC7L002()] 적치단 테이블 조회성공";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				}
				
				// 야드적치BED재료매수
				outRec.setField("YD_STK_BED_STL_SH", YdUtils.fillSpZr(Integer.toString(intRtnVal), 3, 1));		
				
				intRtnVal = (intRtnVal >= 20) ? 20 : intRtnVal;
				
				// EMG Bed 권하 후 Bed 적치 재료 번호 
				for(nIdx=0; nIdx<intRtnVal; nIdx++) {
					recGetVal = rsResult.getRecord(nIdx);	

					outRec.setField("STL_NO" + Integer.toString(nIdx+1), YdUtils.fillSpZr(recGetVal.getFieldString("STL_NO"), 11, 1));
				}

				// 공백 건수처리
				for(int nIdx2=nIdx; nIdx2<20; nIdx2++){
					outRec.setField("STL_NO" + Integer.toString(nIdx2+1), YdUtils.fillSpZr(" ", 11, 1));						
				}
			} else {
				szMsg = "[MakeTcC7::makeC7L002()] 권상 또는 권하상태가 아닙니다 : " + intRtnVal + "] szYD_WRK_PROG_STAT(" + szYD_WRK_PROG_STAT + ")";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;									
			}
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================makeC7L002() OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);

			// RecordSet에 추가
			outRecSet.addRecord(outRec);
		}catch(Exception e){
			szMsg = "[MakeTcC7::makeC7L002()] C7(C연주2정정L2) 송신  OHC Take-Out완료   데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}

		return outRecSet.size();
	} // end of makeC7L002()	

	

	
	
	/**
	 * YDC7L003 : Carry-Out완료 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeC7L003(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID				MSG_ID					VARCHAR2(8)		YDC7L003
		//		2.	생성일				DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간				TIME					VARCHAR2(8)		24HH-mm-ss
		//		4.	전문구분				MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이				MSG_LEN					NUMBER  (4)		
		//		6.	임시					TEMP					VARCHAR2(29)	
		
		//		7.	야드설비ID 			YD_EQP_ID				VARCHAR2(6)		야드구분(1)동구분(1)설비종류(2)설비번호(2)	
		//		8.	야드적치Bed번호		YD_STK_BED_NO			VARCHAR2(2)		적치BED번호(1~6)	
		//		10.	야드적치Bed재료매수	YD_STK_BED_STL_SH		NUMBER  (3)		Carry-Out완료 후 수입구 적치BED잔량 매수	
		//		11.	Carry-Out완료구분		CARRY_OUT_END_GP		VARCHAR2(1)	    "Y"Carry-Out완료, "N" 추가 Carry-Out 잔량 	
		//      12. 야드설비작업매수              YD_EQP_WRK_SH           VARCHAR2(2)     크레인이 Carry-out 한 매수	 
		//		13.	재료번호1				STL_NO1					VARCHAR2(11)	크레인이 Carry-out 한 재료번호 (크레인 작업재료 하단에서 상단으로)
		//		14.	재료번호2				STL_NO2					VARCHAR2(11)		
		//		15.	재료번호3				STL_NO3					VARCHAR2(11)		
		//		16.	재료번호4				STL_NO4	 				VARCHAR2(11)		
		//		17.	재료번호5				STL_NO5	 				VARCHAR2(11)		
		
		// 레코드 선언
		JDTORecordSet rsResult    = null;
		JDTORecordSet rsResultLyr = null;
		JDTORecord recPara        = null;
		JDTORecord recParaStkLyr  = null;
		JDTORecord recGetVal      = null;
		JDTORecord outRec         = null;

		// DAO객체 생성
		YdCrnSchDao ydCrnSchDao   = new YdCrnSchDao();
		YdStkLyrDao ydStkLyrDao   = new YdStkLyrDao();
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeC7L003";
		String szMsg              = "";
		String szOperationName    = "C연주2정정L2 Carry-Out완료";

		// 권상위치
		String szYdUpWoLoc        = "";

		// 야드 설비ID
		String szEqpId            = "";
		
		// 야드 적치 BED번호
		String szBedNo            = "";	
		
		String szCrnSchId         = "";
		String szTempCrnSchId     = "";
		String szCARRY_OUT_END_GP = "";

		
		// 인덱스
		int nIdx                  = 0;
		
		// TC Length = (60 + 69)
		int nTcLen                = 69;

		// 리턴값
		int intRtnVal1            = 0;
		int intRtnVal2            = 0;
		int nRet                  = 0;
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeC7L003() IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
			
			szCrnSchId = ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID");

			// 레코드 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();						

			//=======================================================================================================================
			// 요구구분이 Y인지 N인지  미리 검사
			//=======================================================================================================================
			recPara.setField("YD_CRN_SCH_ID", szCrnSchId);
			nRet = ydCrnSchDao.getYdCrnsch(recPara, rsResult, 42);
			if(nRet < 0) {
				szMsg = "[MakeTcC7::makeC7L003()] 크레인스케쥴 테이블 조회실패 [QueryID : 42]  (" + szCrnSchId + ") " + "[Ret : " + nRet + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			}else if(nRet == 0){
				szMsg = "[MakeTcC7::makeC7L003()] 크레인스케쥴 조회건수가 없음 [QueryID : 42] (" + szCrnSchId + ") " + "[Ret : " + nRet + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;				
			} else {
				szMsg = "[MakeTcC7::makeC7L003()] 크레인스케쥴 테이블 조회성공 [QueryID : 42] ";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
			}

			rsResult.first();
			recGetVal = rsResult.getRecord();
			
			// 조회해서 나온 크레인스케쥴ID : DESC로 조회했기때문에 first로 조회하면 가장 마지막 크레인 스케쥴ID
			szTempCrnSchId = ydDaoUtils.paraRecChkNull(recGetVal, "YD_CRN_SCH_ID");
			
			// 파라미터로 넘어온 크레인스케쥴ID와 DB조회해서 나온 마지막 크레인스케쥴ID를 비교
			if(szCrnSchId.equals(szTempCrnSchId)){
				szCARRY_OUT_END_GP = "Y";
			} else {
				szCARRY_OUT_END_GP = "N";			
			}

			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();						
			//=======================================================================================================================
			// 크레인 스케쥴 테이블 조회
			//=======================================================================================================================
			recPara.setField("YD_CRN_SCH_ID", szCrnSchId);
			intRtnVal1 = ydCrnSchDao.getYdCrnsch(recPara, rsResult, 3);
			if(intRtnVal1 < 0) {
				szMsg = "[MakeTcC7::makeC7L003()] 크레인스케쥴 + 크레인작업재료 + 저장품  테이블 조회오류 [QueryID : 3] (" + ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID") + ") " + "[Ret : " + intRtnVal1 + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			} else if(intRtnVal1 == 0){
				szMsg = "[MakeTcC7::makeC7L003()] 크레인스케쥴 + 크레인작업재료 + 저장품  테이블 조회건수가 없음  [QueryID : 3] (" + ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID") + ") " + "[Ret : " + intRtnVal1 + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;			
			}

			rsResult.first();
			recGetVal = rsResult.getRecord();	
				
			// 권상위치
			szYdUpWoLoc = YdUtils.fillSpZr(recGetVal.getFieldString("YD_UP_WO_LOC"), 8, 1).trim();
			if(szYdUpWoLoc != null && !szYdUpWoLoc.equals("")){				
				// 권상위치 위치에서 설비ID와 BED번호 처리
				szEqpId = szYdUpWoLoc.substring(0, 6);
				szBedNo = szYdUpWoLoc.substring(6, 8);
			}else{
				szMsg = "[MakeTcC7::makeC7L003()] 권상지시 위치가 없습니다 : " + intRtnVal1 + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;					
			}
			
			outRec = JDTORecordFactory.getInstance().create();								

			// 헤더부
			outRec.setField("MSG_ID" , "YDC7L003");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));
			
			// 야드설비ID [적치열구분]
			outRec.setField("YD_EQP_ID", YdUtils.fillSpZr(szEqpId, 6, 1));

			// 야드적치Bed번호 [야드적치Bed번호]
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szBedNo, 2, 1));			
			
			//=======================================================================================================================
			// 적치단 테이블에서 적치중("C")인 데이터 조회
			//=======================================================================================================================
			recParaStkLyr = JDTORecordFactory.getInstance().create();
			rsResultLyr = JDTORecordFactory.getInstance().createRecordSet("");
			recParaStkLyr.setField("YD_STK_COL_GP"      , szEqpId);
			recParaStkLyr.setField("YD_STK_BED_NO"      , szBedNo);
			recParaStkLyr.setField("YD_STK_LYR_MTL_STAT", "C");
			intRtnVal2 = ydStkLyrDao.getYdStklyr(recParaStkLyr, rsResultLyr, 27);
			if(intRtnVal2 < 0) {
				szMsg = "[MakeTcC7::makeC7L003()] 적치단 테이블 조회 오류 (" + szEqpId + ")(" + szBedNo + ")(C) " + "[Ret : " + intRtnVal2 + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			} else if(intRtnVal2 == 0){
				// 야드적치Bed재료매수 [야드적치Bed재료매수]       
				outRec.setField("YD_STK_BED_STL_SH", YdUtils.fillSpZr(Integer.toString(intRtnVal2), 3, 1));
			} else if(intRtnVal2 > 0){
				// 야드적치Bed재료매수 [야드적치Bed재료매수]       
				outRec.setField("YD_STK_BED_STL_SH", YdUtils.fillSpZr(Integer.toString(intRtnVal2), 3, 1));
			}

			// Carry-Out완료구분 [Carry-Out완료구분]
			// 제일 처음에 검사해서 나온 Carry-Out완료구분을 넣음
			outRec.setField("CARRY_OUT_END_GP", YdUtils.fillSpZr(szCARRY_OUT_END_GP, 1, 1));			

			// 야드설비작업매수
			outRec.setField("YD_EQP_WRK_SH", YdUtils.fillSpZr(Integer.toString(intRtnVal1), 2, 1));

			intRtnVal1 = (intRtnVal1 >= 5) ? 5 : intRtnVal1;
			
			// 재료번호 [재료번호]                 
			for(nIdx=0; nIdx<intRtnVal1; nIdx++) {
				recGetVal = rsResult.getRecord(nIdx);	
				
				outRec.setField("STL_NO"+(1+nIdx), YdUtils.fillSpZr(recGetVal.getFieldString("STL_NO"), 11, 1));
			}

			// 공백 건수 처리
			for(int nIdx2=nIdx; nIdx2<5; nIdx2++) {				
				outRec.setField("STL_NO"+(1+nIdx2), YdUtils.fillSpZr(" ", 11, 1));
			}			
			
			// RecordSet에 추가
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================makeC7L003() OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
		}catch(Exception e){
			szMsg = "[MakeTcC7::makeC7L003()] C7(C연주2정정L2) 송신  Carry-Out완료  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}	

		return outRecSet.size();
	} // end of makeC7L003()	

	
	/**
	 * YDC7L008 : OhcTake-In완료 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeC7L008(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID				MSG_ID					VARCHAR2(8)		YDC7L008
		//		2.	생성일				DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간				TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분				MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이				MSG_LEN					NUMBER  (4)		
		//		6.	임시					TEMP					VARCHAR2(29)

		//		7.	야드설비ID			YD_EQP_ID				VARCHAR2(6)		야드설비ID (DD 참조)
		//		8.	CT적재위치               	YD_STK_BED_NO        	VARCHAR2(2)		BED.NO
		//		9.	OHC Take-In 재료정보   SLAB_NO	                VARCHAR2(11)	재료번호	
		
		// 레코드 선언
		JDTORecord outRec       = null;
		
		// DAO객체 생성
		YdDaoUtils ydDaoUtils   = new YdDaoUtils();	
		YdUtils    ydUtils      = new YdUtils();

		// 변수선언
		String szMethodName     = "makeC7L008";
		String szMsg            = "";
		String szOperationName  = "C연주2정정L2 OhcTake-In완료";
				
		// 야드설비ID
		String szYdEqpId        = "";

		// CT적재위치
		String szYdBedNo        = "";
		
		// OHC Take-In 재료정보
		String szSTLNo          = "";

		// TC Length = 79      (60 + 19)
		int nTcLen              = 19;
				
		try{			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeC7L008() IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);

			szYdEqpId = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYdBedNo = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTLNo   = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");
			
			outRec = JDTORecordFactory.getInstance().create();
			
			// 헤더부
			outRec.setField("MSG_ID" , "YDC7L008");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));                                                         

			// 야드설비ID
			outRec.setField("YD_EQP_ID", YdUtils.fillSpZr(szYdEqpId, 6, 1));
			
			// CT적재위치
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYdBedNo, 2, 1));
						
			// OHC Take-In 재료정보
			outRec.setField("SLAB_NO", YdUtils.fillSpZr(szSTLNo, 11, 1));
			
			outRecSet.addRecord(outRec);
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================makeC7L008() OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);	
		}catch(Exception e){
			szMsg = "[MakeTcC7::makeC7L008()] C7(C연주2정정L2) OHC TAKE IN 완료  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}
		
		return outRecSet.size();	
	}	
	//---------------------------------------------------------------------------	
} // end of class MakeTcC7
