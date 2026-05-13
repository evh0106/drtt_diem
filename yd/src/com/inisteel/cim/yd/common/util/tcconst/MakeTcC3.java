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
 * C3 (C연주정정L2) 송신 용 전문 생성
 * @author YHWHman
 *
 */
public class MakeTcC3 {
	// YDC3L001	수불구변경응답	
	// YDC3L002	OHC Take-Out 완료	
	// YDC3L003	Carry-Out완료	
	// YDC3L004	Carry-In완료	
	// YDC3L005	Carry-In재료정보	
	// YDC3L006	대차출발지시	
	// YDC3L007	대차작업실적	
	// YDC3L008	OHC TAKE IN 완료
	// YDC3L009	열연재열재 재료정보
		
	
	// 클래스명
	private static final String szClassName  = MakeTcC3.class.getName();
	
	
	/**
	 * YDC3L001 : 수불구변경응답 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeC3L001(JDTORecord inRec, JDTORecordSet outRecSet){
		
		//		1.	전문 ID				MSG_ID					VARCHAR2(8)		YDC3L001
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
		String szMethodName     = "makeC3L001";
		String szMsg            = "";
		String szOperationName  = "C연주정정L2 수불구변경응답";
		
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
				szMsg = "[MakeTcC3::makeC3L001()] 적치BED 테이블 조회오류  (" + ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP") + ")(6) " + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			} else if(intRtnVal == 0){
				szMsg = "[MakeTcC3::makeC3L001()] 적치BED 테이블 조회건수가 없음 " + ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP") + ")(6)";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;				
			}

			// 야드설비ID [적치열구분]
			szYdEqpId = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP");     
			
			outRec = JDTORecordFactory.getInstance().create();

			// 헤더부
			outRec.setField("MSG_ID"   , "YDC3L001");
			String ydGp = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP").substring(0,1);
			if (ydGp.equals("M"))	outRec.setField("MSG_ID"   , "YDE9L001"); //항만슬라브야드 기능추가 - 2016.01.07 LeeJY
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
			szMsg = "[MakeTcC3::makeC3L001()] C3(C연주정정L2) 송신  수불구변경응답  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}
		
		return outRecSet.size();	
	} // end of makeC3L001()	


	
	
	
	/**
	 * YDC3L002 : OHC Take-Out완료 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeC3L002(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID				MSG_ID					VARCHAR2(8)		YDC3L002
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
		String szMethodName     = "makeC3L002";
		String szMsg            = "";
		String szOperationName  = "C연주정정L2 OHC Take-Out완료";
		
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
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeC3L002() IN==========================\n", YdConstant.DEBUG);	
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
				szMsg = "[MakeTcC3::makeC3L002()] 크레인스케쥴 + 크레인작업재료 + 저장품 테이블 조회실패  (" + ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID") + ") " + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			}else if(intRtnVal == 0){
				szMsg = "[MakeTcC3::makeC3L002()] 크레인스케쥴 + 크레인작업재료 + 저장품 테이블 조회건수가 없음 (" + ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID") + ") " + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;				
			} else {
				szMsg = "[MakeTcC3::makeC3L002()] 크레인스케줄 + 크레인작업재료 + 저장품 테이블 조회성공";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
			}

			rsResult.first();
			recGetVal = rsResult.getRecord();

			// 헤더부
			String ydGp = ydDaoUtils.paraRecChkNull(outRec, "YD_EQP_ID").substring(0,1);
			outRec = JDTORecordFactory.getInstance().create();								
			outRec.setField("MSG_ID" , "YDC3L002");
			if (ydGp.equals("M"))	outRec.setField("MSG_ID"   , "YDE9L002"); //항만슬라브야드 기능추가 - 2016.01.07 LeeJY
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
			
			szMsg = "[MakeTcC3::makeC3L002()] 작업진행 상태 (" + szYD_WRK_PROG_STAT + ")";
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
					szMsg = "[MakeTcC3::makeC3L002()] 적치단 테이블 조회오류 [" + intRtnVal + "] YD_STK_COL_GP(" + szEqpId + ") YD_STK_BED_NO(" + szBedNo + ") YD_STK_LYR_MTL_STAT(C)";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
					return 0;				
				} else if(intRtnVal == 0){
					szMsg = "[MakeTcC3::makeC3L002()] 적치단 테이블 조회건수가 없음 [" + intRtnVal + "] YD_STK_COL_GP(" + szEqpId + ") YD_STK_BED_NO(" + szBedNo + ") YD_STK_LYR_MTL_STAT(C)";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
					return 0;								
				} else {
					szMsg = "[MakeTcC3::makeC3L002()] 적치단 테이블 조회성공";
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
				szMsg = "[MakeTcC3::makeC3L002()] 권상 또는 권하상태가 아닙니다 : " + intRtnVal + "] szYD_WRK_PROG_STAT(" + szYD_WRK_PROG_STAT + ")";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;									
			}
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================makeC3L002() OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);

			// RecordSet에 추가
			outRecSet.addRecord(outRec);
		}catch(Exception e){
			szMsg = "[MakeTcC3::makeC3L002()] C3(C연주정정L2) 송신  OHC Take-Out완료   데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}

		return outRecSet.size();
	} // end of makeC3L002()	

	

	
	
	/**
	 * YDC3L003 : Carry-Out완료 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeC3L003(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID				MSG_ID					VARCHAR2(8)		YDC3L003
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
		String szMethodName       = "makeC3L003";
		String szMsg              = "";
		String szOperationName    = "C연주정정L2 Carry-Out완료";

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
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeC3L003() IN==========================\n", YdConstant.DEBUG);	
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
				szMsg = "[MakeTcC3::makeC3L003()] 크레인스케쥴 테이블 조회실패 [QueryID : 42]  (" + szCrnSchId + ") " + "[Ret : " + nRet + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			}else if(nRet == 0){
				szMsg = "[MakeTcC3::makeC3L003()] 크레인스케쥴 조회건수가 없음 [QueryID : 42] (" + szCrnSchId + ") " + "[Ret : " + nRet + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;				
			} else {
				szMsg = "[MakeTcC3::makeC3L003()] 크레인스케쥴 테이블 조회성공 [QueryID : 42] ";
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
				szMsg = "[MakeTcC3::makeC3L003()] 크레인스케쥴 + 크레인작업재료 + 저장품  테이블 조회오류 [QueryID : 3] (" + ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID") + ") " + "[Ret : " + intRtnVal1 + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			} else if(intRtnVal1 == 0){
				szMsg = "[MakeTcC3::makeC3L003()] 크레인스케쥴 + 크레인작업재료 + 저장품  테이블 조회건수가 없음  [QueryID : 3] (" + ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID") + ") " + "[Ret : " + intRtnVal1 + "]";
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
				szMsg = "[MakeTcC3::makeC3L003()] 권상지시 위치가 없습니다 : " + intRtnVal1 + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;					
			}
			
			outRec = JDTORecordFactory.getInstance().create();								

			// 헤더부
			outRec.setField("MSG_ID" , "YDC3L003");
			String ydGp = szEqpId.substring(0,1);
			if (ydGp.equals("M"))	outRec.setField("MSG_ID"   , "YDE9L003");  //항만슬라브야드 기능추가 - 2016.01.07 LeeJY
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
				szMsg = "[MakeTcC3::makeC3L003()] 적치단 테이블 조회 오류 (" + szEqpId + ")(" + szBedNo + ")(C) " + "[Ret : " + intRtnVal2 + "]";
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
			ydUtils.putLog(szClassName, szMethodName, "\n======================makeC3L003() OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
		}catch(Exception e){
			szMsg = "[MakeTcC3::makeC3L003()] C3(C연주정정L2) 송신  Carry-Out완료  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}	

		return outRecSet.size();
	} // end of makeC3L003()	

		

	
		
	/**
	 * YDC3L004 : Carry-In완료 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeC3L004(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID				MSG_ID					VARCHAR2(8)		YDC3L004
		//		2.	생성일				DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간				TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분				MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이				MSG_LEN					NUMBER  (4)		
		//		6.	임시					TEMP					VARCHAR2(29)	
		
		//		7.	야드설비ID 			YD_EQP_ID				VARCHAR2(6)		야드구분(1)동구분(1)설비종류(2)설비번호(2)	
		//		8.	야드적치Bed번호		YD_STK_BED_NO			VARCHR2 (2)	        적치BED번호(1~6)	
		//		9.	야드적치Bed재료매수	YD_STK_BED_STL_SH		NUMBER  (3)		Carry-In완료 후 불출구 적치BED잔량 매수	
		//		10.	Carry-In완료구분	    CARRY_IN_END_GP     	VARCHAR2(1)		"Y"Carry-In완료, "N" 추가 Carry-In 잔량 		
		//      11. 야드설비작업매수              YD_EQP_WRK_SH           NUMBER  (2)     크레인이 Carry-In 한 매수	
		//		12.	재료번호1				STL_NO1					VARCHAR2(11)	크레인이 Carry-In 한 재료번호 (크레인 작업재료 하단에서 상단으로)	
		//		13.	재료번호2				STL_NO2					VARCHAR2(11)		
		//		14.	재료번호3				STL_NO3					VARCHAR2(11)		
		//		15.	재료번호4				STL_NO4					VARCHAR2(11)
		//		15.	재료번호5				STL_NO5					VARCHAR2(11)
		
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
		String szMethodName       = "makeC3L004";
		String szMsg              = "";
		String szOperationName    = "C연주정정L2 Carry-In완료";

		// 권하위치
		String szYdDnWoLoc        = "";
	
		// 야드 설비ID
		String szEqpId            = "";
		
		// 야드 적치 BED번호
		String szBedNo            = "";	
		
		// 인덱스
		int nIdx                  = 0;
		
		// 리턴값
		int intRtnVal1            = 0;
		int intRtnVal2            = 0;
		
		// TC Length = (60 + 69)
		int nTcLen                = 69;
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeC3L004() IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);

			// 레코드 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();			
						
			//=======================================================================================================================
			// 크레인 스케쥴 테이블 조회
			//=======================================================================================================================
			recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID"));
			intRtnVal1 = ydCrnSchDao.getYdCrnsch(recPara, rsResult, 3);
			if(intRtnVal1 < 0) {
				szMsg = "[MakeTcC3::makeC3L004()] 크레인스케쥴 + 크레인작업재료 + 저장품 테이블 조회오류  (" + ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID") + ")" + "[Ret : " + intRtnVal1 + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			} else if(intRtnVal1 == 0) {
				szMsg = "[MakeTcC3::makeC3L004()] 크레인스케쥴 + 크레인작업재료 + 저장품 테이블 조회건수 없음  (" + ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID") + ")" + "[Ret : " + intRtnVal1 + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;
			}

			rsResult.first();
			recGetVal = rsResult.getRecord();	
				
			// 권하위치
			szYdDnWoLoc = YdUtils.fillSpZr(recGetVal.getFieldString("YD_DN_WO_LOC"), 8, 1).trim();
			if(szYdDnWoLoc != null && !szYdDnWoLoc.equals("")){				
				// 권하지시 위치에서 설비ID와 BED번호 처리
				szEqpId = szYdDnWoLoc.substring(0, 6);
				szBedNo = szYdDnWoLoc.substring(6, 8);
			}else{
				szMsg = "[MakeTcC3::makeC3L004()] 권하지시 위치가 없습니다 : " + intRtnVal1 + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;					
			}

			outRec = JDTORecordFactory.getInstance().create();								
			
			// 헤더부
			outRec.setField("MSG_ID" , "YDC3L004");
			String ydGp = szEqpId.substring(0,1);
			if (ydGp.equals("M"))	outRec.setField("MSG_ID"   , "YDE9L004");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));
						
			// 야드설비ID [적치열구분]
			outRec.setField("YD_EQP_ID", YdUtils.fillSpZr(szEqpId, 6, 1));

			// 야드적치Bed번호 [야드적치Bed번호]
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szBedNo, 2, 1));				

			
			szMsg = "[적치단 조회] 항목  YD_STK_COL_GP(" + szEqpId + ") YD_STK_BED_NO(" + szBedNo + ")";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			

			// 적치단 테이블에서 적치중("C")인 데이터 조회
			recParaStkLyr = JDTORecordFactory.getInstance().create();
			rsResultLyr = JDTORecordFactory.getInstance().createRecordSet("");
			recParaStkLyr.setField("YD_STK_COL_GP"      , szEqpId);
			recParaStkLyr.setField("YD_STK_BED_NO"      , szBedNo);
			recParaStkLyr.setField("YD_STK_LYR_MTL_STAT", "C");
			intRtnVal2 = ydStkLyrDao.getYdStklyr(recParaStkLyr, rsResultLyr, 27);
			if(intRtnVal2 < 0) {
				szMsg = "[MakeTcC3::makeC3L004()] 적치단 테이블 조회 오류 (" + szEqpId + ")(" + szBedNo + ") " + "[Ret : " + intRtnVal2 + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			} else if(intRtnVal2 == 0){
				// 야드적치Bed재료매수 [야드적치Bed재료매수]       
				outRec.setField("YD_STK_BED_STL_SH", YdUtils.fillSpZr(Integer.toString(intRtnVal2), 3, 1));
				
				// Carry-In완료구분 [Carry-In완료구분]
				outRec.setField("CARRY_IN_END_GP", YdUtils.fillSpZr("N", 1, 1));			
			} else if(intRtnVal2 > 0){
				// 야드적치Bed재료매수 [야드적치Bed재료매수]       
				outRec.setField("YD_STK_BED_STL_SH", YdUtils.fillSpZr(Integer.toString(intRtnVal2), 3, 1));

				// Carry-In완료구분 [Carry-In완료구분]
				outRec.setField("CARRY_IN_END_GP", YdUtils.fillSpZr("Y", 1, 1));			
			}
		
			// 야드설비작업매수
			outRec.setField("YD_EQP_WRK_SH", YdUtils.fillSpZr(Integer.toString(intRtnVal1), 2, 1));
			
			intRtnVal1 = (intRtnVal1 >= 5) ? 5 : intRtnVal1;

			// 재료번호 [재료번호]                 
			for(nIdx=0; nIdx<intRtnVal1; nIdx++) {
				recGetVal = rsResult.getRecord(nIdx);	
				
				outRec.setField("STL_NO"+(1+nIdx), YdUtils.fillSpZr(recGetVal.getFieldString("STL_NO"), 11, 1));
			}

			// 공백 건수처리
			for(int nIdx2=nIdx; nIdx2<5; nIdx2++) {				
				outRec.setField("STL_NO"+(1+nIdx2), YdUtils.fillSpZr(" ", 11, 1));
			}			
			
			// RecordSet에 추가				
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================makeC3L004() OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
		}catch(Exception e){
			szMsg = "[MakeTcC3::makeC3L004()] C3(C연주정정L2) 송신  Carry-In완료  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
			return -1;
		}
		
		return outRecSet.size();
	} // end of makeC3L004()	
	
	/**
	 * YDC3L005 : Carry-In재료정보 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeC3L005(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID						VARCHAR2(8)		YDC3L005
		//		2.	생성일					DATE						VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME						VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP						VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN						NUMBER  (4)		
		//		6.	임시						TEMP						VARCHAR2(29)	

		//      정보그룹 [5] 매   ARRAY
		//		7.	주편번호					MSLAB_NO					VARCHAR2(11)		
		//      8.  예정주편번호                            PLN_MSLAB_NO                VARCHAR2(9)    
		//		8.	연주Machine코드			CC_MC_CD					VARCHAR2(1)		
		//		9.	주조Strand번호			CS_STR_NO					VARCHAR2(2)		
		//		10.	출강목표기호				SPEC_HEATOUT_AIM			VARCHAR2(13)		
		//		11.	가열로장입Lot번호			REFUR_CHG_LOT_NO			VARCHAR2(10)		
		//		12.	Slab산적Lot코드			STACK_LOT_NO				VARCHAR2(14)		
		//		13.	HCR구분					HCR_GP						VARCHAR2(1)		
		//		14.	주편두께					MSLAB_T						NUMBER  (6,3)		
		//		15.	주편폭					MSLAB_W						NUMBER  (5,1)		
        //		16.	주편길이					MSLAB_L						NUMBER  (5)		
		//		17.	주편중량					MSLAB_WT					NUMBER  (5)		
		//      18. 지시주편손질방법                      WO_MSLAB_RPR_MTD            VARCHAR2(1)
		//		19.	연주주편손질구분			CCSLAB_RPR_GP				VARCHAR2(1)		
		//      20. Tracking주편손질방법            TRK_MSLAB_RPR_MTD           VARCHAR2(1)
		//      21. 주편지정주편손질방법               MSLAB_ASGN_MSLAB_RPR_MTD    VARCHAR2(1)
		//      22. 조업지정주편손질방법               PTOP_ASGN_MSLAB_RPR_MTD     VARCHAR2(1)
		//      23. 품질Traking이상조치코드1   QT_TRK_AB_TRT_CD1           VARCHAR2(3)
		//      23. 품질Traking이상조치코드2   QT_TRK_AB_TRT_CD2           VARCHAR2(3)
		//      23. 품질Traking이상조치코드3   QT_TRK_AB_TRT_CD3           VARCHAR2(3)
		//		24.	Scarfing조업자부여구분		SCARF_OPRNER_SET_GP			VARCHAR2(1)		
		//		25.	Scarfing조업자부여원인구분	SCARF_OPRNER_SET_RSN_GP		VARCHAR2(1)		
		//		26.	주편절사지시길이			MSLAB_DSCD_WO_L				VARCHAR2(2)		
		//		27.	전단주편지정구분			FS_MSLAB_ASGN_GP			VARCHAR2(1)		
		//		28.	주문여재구분				ORD_YEOJAE_GP				VARCHAR2(1)		
		//		29.	Tapered구분				TAPER_E_D_GP				VARCHAR2(1)		
		//		30.	주편형상구분				MSLAB_FRM_GP				VARCHAR2(2)		
		//		31.	Slab지시행선구분			SLAB_WO_RT_GP				VARCHAR2(2)		
		//		32.	연주주편Sampling코드		CCSLAB_SMPL_CD				VARCHAR2(4)		
		//		33.	용도코드					USAGE_CD					VARCHAR2(4)		
		//		34.	사내보증기호				STLQLTY_SYM					VARCHAR2(10)		
		//		35.	품명코드					ITEMNAME_CD					VARCHAR2(3)
		//		36.	압연주문두께				MSLAB_T						NUMBER  (6,3)		
		//		37.	후판재트리밍코드			ITEMNAME_CD					VARCHAR2(1)	
		
		// 레코드 선언
		JDTORecord recPara         = null;
		JDTORecordSet rsResult     = null;
		JDTORecord recGetVal       = null;
		JDTORecordSet rsResultTemp = null;
		JDTORecord outRec          = null;

		// DAO객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();
		YdStockDao ydStockDao     = new YdStockDao();

		// 변수선언
		String szMethodName       = "makeC3L005";
		String szMsg              = "";
		String szOperationName    = "C연주정정L2 Carry-In재료정보";			
		String szConv             = "";
		
		// 재료번호
		String szSTL_NO           = "";
		
		// 주편번호
		String szMSLAB            = "";
		
		// 예정주편번호
		String szPLN_MSLAB_NO     = "";

		// 연주Machine코드
		String szCC_MC_CD         = "";
		
		// 주조Strand 번호
		String szCS_STR_NO        = "";
		
		// 출강목표기호
		String szSPEC_HEATOUT_AIM = "";
		
		// 가열로장입Lot번호
		String szREFUR_CHG_LOT_NO = "";
		
		// Slab산적Lot코드
		String szSTACK_LOT_NO     = "";
		
		// HCR구분
		String szHCR_GP           = "";
		
		// 주편두께
		String szMSLAB_T          = "";
		
		// 주편폭
		String szMSLAB_W          = "";
		
		// 주편길이
		String szMSLAB_L          = "";
		
		// 주편중량
		String szMSLAB_WT         = "";
		
		// 연주주편손질구분
		String szCCSLAB_RPR_GP    = "";
		
		// Scarfing조업자부여구분
		String szSCARF_OPRNER_SET_GP = "";
		
		// Scarfing조업자부여원인구분	
		String szSCARF_OPRNER_SET_RSN_GP = "";
		
		// 주편절사지시길이			
		String szMSLAB_DSCD_WO_L  = "";
		
		// 전단주편지정구분			
		String szFS_MSLAB_ASGN_GP = "";
		
		// 주문여재구분				
		String szORD_YEOJAE_GP    = "";
		
		// Tapered구분	
		String szTAPER_E_D_GP     = "";
		
		// 주편형상구분				
		String szMSLAB_FRM_GP     = "";
		
		// Slab지시행선구분			
		String szSLAB_WO_RT_GP    = "";
		
		// 연주주편Sampling코드		
		String szCCSLAB_SMPL_CD   = "";
		
		// 용도코드					
		String szUSAGE_CD         = "";
		
		// 사내보증기호				
		String szSTLQLTY_SYM      = "";
		
		// 품명코드					
		String szITEMNAME_CD      = "";
		
		// 압연주문두께					
		String szORD_CONV_T       = "";
		
		// 후판재트리밍코드					
		String szORD_EDGE_CD      = "";
		
		// 지시주편손질방벙
		String szWO_MSLAB_RPR_MTD = "";
		
		String szSCARFING_SIGN    = "";
		
		// Tracking주편손질방법
		String szTRK_MSLAB_RPR_MTD = "";
		
		// 주편지정주편손질방법
		String szMSLAB_ASGN_MSLAB_RPR_MTD = "";
		
		// 조업지정주편손질방법
		String szPTOP_ASGN_MSLAB_RPR_MTD = "";
		
		// 품질Traking이상조치코드1
		String szQT_TRK_AB_TRT_CD1 = "";
		
		// 품질Traking이상조치코드2
		String szQT_TRK_AB_TRT_CD2 = "";
		
		// 품질Traking이상조치코드3
		String szQT_TRK_AB_TRT_CD3 = "";
		
		// 리턴값
		int intRtnVal             = 0;
		int intRtnVal2            = 0;

		// TC Length = 700        60 + (135 * 5)
		int nTcLen                = 675;
		
		int iArry				  = 5;
		
		int nIdx                  = 0;
		
		try{
			
			// 레코드 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();			
						
			//======================================================================================================================
			// 크레인스케쥴 재료번호 조회
			//======================================================================================================================
			recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID"));	
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 70);
			if(intRtnVal < 0) {
				szMsg = "[MakeTcC3::makeC3L005()] 크레인스케줄 테이블 조회오류  (" + ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID") + ")" + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			} else if(intRtnVal == 0) {
				szMsg = "[MakeTcC3::makeC3L005()] 크레인스케줄 테이블 조회건수 없음  (" + ydDaoUtils.paraRecChkNull(inRec, "YD_CRN_SCH_ID") + ")" + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;
			}
		
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();								
			outRec.setField("MSG_ID" , 			"YDC3L005");
			outRec.setField("DATE"   , 			YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , 			YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , 			"I");
			outRec.setField("MSG_LEN", 			YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , 			YdUtils.fillSpZr("", 29, 1));
			
			intRtnVal = (intRtnVal >=  iArry) ? iArry : intRtnVal;

			for(nIdx=0; nIdx < intRtnVal; nIdx++) {
				
				recGetVal = rsResult.getRecord(nIdx);	
	
				szSTL_NO = recGetVal.getFieldString("STL_NO");
				
				//======================================================================================================================
				// STL_NO 로 주편공통 테이블 조회
				//======================================================================================================================
				recPara = JDTORecordFactory.getInstance().create();			
				recPara.setField("STL_NO", szSTL_NO);	
				rsResultTemp = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal2 = ydStockDao.getYdStock(recPara, rsResultTemp, 63);
				if(intRtnVal2 < 0) {
					szMsg = "[MakeTcC3::makeC3L005()] 주편공통 테이블(TB_PT_MSLABCOMM) 조회오류  (" + szSTL_NO + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				} else if(intRtnVal2 == 0) {
					szMsg = "[MakeTcC3::makeC3L005()] 주편공통 테이블(TB_PT_MSLABCOMM) 조회건수 없음  (" + szSTL_NO + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				}	
				
				rsResultTemp.first();
				recGetVal = rsResultTemp.getRecord();	

				szMSLAB = YdUtils.fillSpZr(recGetVal.getFieldString("MSLAB_NO"), 11, 1);
				
				szPLN_MSLAB_NO = YdUtils.fillSpZr(recGetVal.getFieldString("PLN_MSLAB_NO"), 9, 1);
				
				szCC_MC_CD = YdUtils.fillSpZr(recGetVal.getFieldString("CC_CCM_NO"), 1, 1);
				
				szSPEC_HEATOUT_AIM = YdUtils.fillSpZr(recGetVal.getFieldString("SPEC_HEATOUT_AIM"), 13, 1);
				
				szSTACK_LOT_NO = YdUtils.fillSpZr(recGetVal.getFieldString("STACK_LOT_NO"), 14, 1);
				
				szHCR_GP = YdUtils.fillSpZr(recGetVal.getFieldString("HCR_GP"), 1, 1);
				
				szConv = YdUtils.fillSpZr(recGetVal.getFieldString("MSLAB_T"), 7, 1);
				szMSLAB_T = ydUtils.FloatLRPAD(szConv, 6, 3, '0');
				
				szConv = YdUtils.fillSpZr(recGetVal.getFieldString("MSLAB_W"), 6, 1);
				szMSLAB_W = ydUtils.FloatLRPAD(szConv, 5, 1, '0');
				
				szMSLAB_L = YdUtils.fillSpZr(recGetVal.getFieldString("MSLAB_L"), 5, 1);
				
				szMSLAB_WT = YdUtils.fillSpZr(recGetVal.getFieldString("MSLAB_WT"), 5, 1);
				
				szSCARF_OPRNER_SET_GP = YdUtils.fillSpZr(recGetVal.getFieldString("SCARF_OPRNER_SET_GP"), 1, 1);

				szSCARF_OPRNER_SET_RSN_GP = YdUtils.fillSpZr(recGetVal.getFieldString("SCARF_OPRNER_SET_RSN_GP"), 1, 1);
				
				szMSLAB_DSCD_WO_L = YdUtils.fillSpZr(recGetVal.getFieldString("MSLAB_DSCD_WO_L"), 2, 1);
				
				szFS_MSLAB_ASGN_GP = YdUtils.fillSpZr(recGetVal.getFieldString("FS_MSLAB_ASGN_GP"), 1, 1);
				
				szORD_YEOJAE_GP = YdUtils.fillSpZr(recGetVal.getFieldString("ORD_YEOJAE_GP"), 1, 1);
				
				szTAPER_E_D_GP = YdUtils.fillSpZr(recGetVal.getFieldString("TAPERED_GP"), 1, 1);
				
				szMSLAB_FRM_GP = YdUtils.fillSpZr(recGetVal.getFieldString("MSLAB_FRM_GP"), 2, 1);
				
				szSLAB_WO_RT_GP = YdUtils.fillSpZr(recGetVal.getFieldString("SLAB_WO_RT_CD"), 2, 1);

				szUSAGE_CD = YdUtils.fillSpZr(recGetVal.getFieldString("USAGE_CD"), 4, 1);
				
				szSTLQLTY_SYM = YdUtils.fillSpZr(recGetVal.getFieldString("STLQLTY_SYM"), 10, 1);
				//스카핑,그라인딩 실적항목으로 대체 2015.05.20 윤재광
				szITEMNAME_CD = YdUtils.fillSpZr(recGetVal.getFieldString("MC_GM_CD"), 3, 1);

				szWO_MSLAB_RPR_MTD = YdUtils.fillSpZr(recGetVal.getFieldString("WO_MSLAB_RPR_MTD"), 1, 1);

				szSCARFING_SIGN = YdUtils.fillSpZr(recGetVal.getFieldString("SCARFING_SIGN"), 4, 1);

				szConv = YdUtils.fillSpZr(recGetVal.getFieldString("ORD_CONV_T"), 7, 1);
				szORD_CONV_T = ydUtils.FloatLRPAD(szConv, 6, 3, '0');
				
				szORD_EDGE_CD = YdUtils.fillSpZr(recGetVal.getFieldString("ORD_EDGE_CD"), 1, 1);
				
				//======================================================================================================================
				// SCARFING_SIGN으로 (TB_PM_B_SLABRPRGP_SLABMATCH) 테이블 조회
				//======================================================================================================================
				recPara = JDTORecordFactory.getInstance().create();			
				recPara.setField("SCARFING_SIGN", szSCARFING_SIGN);					
				rsResultTemp = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal2 = ydStockDao.getYdStock(recPara, rsResultTemp, 102);
				if(intRtnVal2 < 0) {
					szMsg = "[MakeTcC3::makeC3L005()] PM_SLAB손질구분_SLAB충당 테이블(TB_PM_B_SLABRPRGP_SLABMATCH) 조회오류  (" + szSCARFING_SIGN + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
					szCCSLAB_RPR_GP = YdUtils.fillSpZr(" ", 1, 1);
				} else if(intRtnVal2 == 0) {
					szMsg = "[MakeTcC3::makeC3L005()] PM_SLAB손질구분_SLAB충당 테이블(TB_PM_B_SLABRPRGP_SLABMATCH) 조회건수 없음  (" + szSCARFING_SIGN + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
					szCCSLAB_RPR_GP = YdUtils.fillSpZr(" ", 1, 1);
				} else {
					rsResultTemp.first();
					recGetVal = rsResultTemp.getRecord();	
					szCCSLAB_RPR_GP = YdUtils.fillSpZr(recGetVal.getFieldString("CCSLAB_RPR_GP"), 1, 1);
				}
				
				//======================================================================================================================
				// MSLAB_NO로 (TB_QM_MSLABQLTYINFO) 테이블 조회
				//======================================================================================================================
				recPara = JDTORecordFactory.getInstance().create();			
				recPara.setField("STL_NO", szSTL_NO);					
				rsResultTemp = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal2 = ydStockDao.getYdStock(recPara, rsResultTemp, 103);
				if(intRtnVal2 < 0) {
					szMsg = "[MakeTcC3::makeC3L005()] QM_주편품질정보 테이블(TB_QM_MSLABQLTYINFO) 조회오류  (" + szSTL_NO + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
					szTRK_MSLAB_RPR_MTD = YdUtils.fillSpZr(" ", 1, 1);
					szMSLAB_ASGN_MSLAB_RPR_MTD = YdUtils.fillSpZr(" ", 1, 1);
					szPTOP_ASGN_MSLAB_RPR_MTD = YdUtils.fillSpZr(" ", 1, 1);
				} else if(intRtnVal2 == 0) {
					szMsg = "[MakeTcC3::makeC3L005()] QM_주편품질정보 테이블(TB_QM_MSLABQLTYINFO) 조회건수 없음  (" + szSTL_NO + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
					szTRK_MSLAB_RPR_MTD = YdUtils.fillSpZr(" ", 1, 1);
					szMSLAB_ASGN_MSLAB_RPR_MTD = YdUtils.fillSpZr(" ", 1, 1);
					szPTOP_ASGN_MSLAB_RPR_MTD = YdUtils.fillSpZr(" ", 1, 1);
				} else {
					rsResultTemp.first();
					recGetVal = rsResultTemp.getRecord();	

					szTRK_MSLAB_RPR_MTD = YdUtils.fillSpZr(recGetVal.getFieldString("TRK_MSLAB_RPR_MTD"), 1, 1);
					szMSLAB_ASGN_MSLAB_RPR_MTD = YdUtils.fillSpZr(recGetVal.getFieldString("MSLAB_ASGN_MSLAB_RPR_MTD"), 1, 1);
					szPTOP_ASGN_MSLAB_RPR_MTD = YdUtils.fillSpZr(recGetVal.getFieldString("PTOP_ASGN_MSLAB_RPR_MTD"), 1, 1);
				}
				
				//********************************************************************************************************************************
				szQT_TRK_AB_TRT_CD1 = YdUtils.fillSpZr(" ", 3, 1);
				szQT_TRK_AB_TRT_CD2 = YdUtils.fillSpZr(" ", 3, 1);
				szQT_TRK_AB_TRT_CD3 = YdUtils.fillSpZr(" ", 3, 1);
				//********************************************************************************************************************************
				
				//======================================================================================================================
				// 재료번호로 Heat작업지시(TB_CT_F_HEATWO) 테이블 조회				
				//======================================================================================================================
				rsResultTemp = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal2 = ydStockDao.getYdStock(recPara, rsResultTemp, 64);
				if(intRtnVal2 < 0) {
					szMsg = "[MakeTcC3::makeC3L005()] Heat작업지시(TB_CT_F_HEATWO) 조회오류  (" + szSTL_NO + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
					szCCSLAB_SMPL_CD = YdUtils.fillSpZr(" ", 4, 1);
				} else if(intRtnVal2 == 0) {
					szMsg = "[MakeTcC3::makeC3L005()] Heat작업지시(TB_CT_F_HEATWO) 조회건수 없음 (" + szSTL_NO + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
					szCCSLAB_SMPL_CD = YdUtils.fillSpZr(" ", 4, 1);
				} else {
					rsResultTemp.first();
					recGetVal = rsResultTemp.getRecord();	
					szCCSLAB_SMPL_CD = YdUtils.fillSpZr(recGetVal.getFieldString("CCSLAB_SMPL_CD"), 4, 1);
				}
				
				//======================================================================================================================
				// 재료번호로 주편전단실적(TB_CS_MSLABFSWR) 테이블 조회
				//======================================================================================================================
				rsResultTemp = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal2 = ydStockDao.getYdStock(recPara, rsResultTemp, 65);
				if(intRtnVal2 < 0) {
					szMsg = "[MakeTcC3::makeC3L005()] 주편전단실적(TB_CS_MSLABFSWR) 테이블 조회오류  (" + szSTL_NO + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
					szCS_STR_NO = YdUtils.fillSpZr(" ", 2, 1);										
				} else if(intRtnVal2 == 0) {
					szMsg = "[MakeTcC3::makeC3L005()] 주편전단실적(TB_CS_MSLABFSWR) 테이블 조회건수 없음  (" + szSTL_NO + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);	
					szCS_STR_NO = YdUtils.fillSpZr(" ", 2, 1);	
				} else {
					rsResultTemp.first();
					recGetVal = rsResultTemp.getRecord();	
					szCS_STR_NO = YdUtils.fillSpZr(recGetVal.getFieldString("CS_STR_NO"), 2, 1);					
				}

				//======================================================================================================================
				// 재료번호로 SLAB작업지시(TB_CT_F_SLABWO) 조회
				//======================================================================================================================
				rsResultTemp = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal2 = ydStockDao.getYdStock(recPara, rsResultTemp, 203);
				if(intRtnVal2 < 0) {
					szMsg = "[MakeTcC3::makeC3L005()] SLAB작업지시(TB_CT_L_HRMILLWO) 테이블 조회오류  (" + szSTL_NO + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
					szREFUR_CHG_LOT_NO = YdUtils.fillSpZr(" ", 10, 1);					
				} else if(intRtnVal2 == 0) {
					szMsg = "[MakeTcC3::makeC3L005()] SLAB작업지시(TB_CT_L_HRMILLWO) 테이블 조회건수 없음  (" + szSTL_NO + ")" + "[Ret : " + intRtnVal2 + "]";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
					szREFUR_CHG_LOT_NO = YdUtils.fillSpZr(" ", 10, 1);					
				} else {
					rsResultTemp.first();
					recGetVal = rsResultTemp.getRecord();	
					szREFUR_CHG_LOT_NO = YdUtils.fillSpZr(recGetVal.getFieldString("REFUR_CHG_LOT_NO"), 10, 1);					
				}
				
				// 주편번호 [주편번호]                                   
				outRec.setField("MSLAB_NO"+(nIdx+1), szMSLAB);

				// 예정주편번호 [예정주편번호]                                   
				outRec.setField("PLN_MSLAB_NO"+(nIdx+1), szPLN_MSLAB_NO);

				// 연주Machine코드 [연주Machine코드]                                   
				outRec.setField("CC_MC_CD"+(nIdx+1), szCC_MC_CD);

				// 주조Strand번호 [주조Strand번호]                                   
				outRec.setField("CS_STR_NO"+(nIdx+1), szCS_STR_NO);

				// 출강목표기호 [출강목표기호]                                                      
				outRec.setField("SPEC_HEATOUT_AIM"+(nIdx+1), szSPEC_HEATOUT_AIM);
				
				// 가열로장입Lot번호 [가열로장입Lot번호]                                                      
				outRec.setField("REFUR_CHG_LOT_NO"+(nIdx+1), szREFUR_CHG_LOT_NO);

				// Slab산적Lot코드 [Slab산적Lot코드]                                                      
				outRec.setField("STACK_LOT_NO"+(nIdx+1), szSTACK_LOT_NO);
				
				// HCR구분 [HCR구분]                                                      
				outRec.setField("HCR_GP"+(nIdx+1), szHCR_GP);
				
				// 주편두께 [주편두께]       
				outRec.setField("MSLAB_T"+(nIdx+1), szMSLAB_T);	
								
				// 주편폭 [주편폭]                                                      
				outRec.setField("MSLAB_W"+(nIdx+1), szMSLAB_W);	

				// 주편길이 [주편길이]                                                      
				outRec.setField("MSLAB_L"+(nIdx+1), szMSLAB_L);
				
				// 주편중량 [주편중량]                                                      
				outRec.setField("MSLAB_WT"+(nIdx+1), szMSLAB_WT);
				
				// 지시주편손질방법
				outRec.setField("WO_MSLAB_RPR_MTD"+(nIdx+1), szWO_MSLAB_RPR_MTD);
				
				// 연주주편손질구분 [연주주편손질구분]                                                      
				outRec.setField("CCSLAB_RPR_GP"+(nIdx+1), szCCSLAB_RPR_GP);
				
				// Tracking주편손질방법
				outRec.setField("TRK_MSLAB_RPR_MTD"+(nIdx+1), szTRK_MSLAB_RPR_MTD);

				// 주편지정주편손질방법
				outRec.setField("MSLAB_ASGN_MSLAB_RPR_MTD"+(nIdx+1), szMSLAB_ASGN_MSLAB_RPR_MTD);

				// 조업지정주편손질방법
				outRec.setField("PTOP_ASGN_MSLAB_RPR_MTD"+(nIdx+1), szPTOP_ASGN_MSLAB_RPR_MTD);

				// 품질Traking이상조치코드1
				outRec.setField("QT_TRK_AB_TRT_CD1"+(nIdx+1), szQT_TRK_AB_TRT_CD1);

				// 품질Traking이상조치코드2
				outRec.setField("QT_TRK_AB_TRT_CD2"+(nIdx+1), szQT_TRK_AB_TRT_CD2);

				// 품질Traking이상조치코드3
				outRec.setField("QT_TRK_AB_TRT_CD3"+(nIdx+1), szQT_TRK_AB_TRT_CD3);
				
				outRec.setField("SCARF_OPRNER_SET_GP"+(nIdx+1), szSCARF_OPRNER_SET_GP);
				
				// Scarfing조업자부여원인구분 [Scarfing조업자부여원인구분]                                                      
				outRec.setField("SCARF_OPRNER_SET_RSN_GP"+(nIdx+1), szSCARF_OPRNER_SET_RSN_GP);
				
				// 주편절사지시길이 [주편절사지시길이]                                                      
				outRec.setField("MSLAB_DSCD_WO_L"+(nIdx+1), szMSLAB_DSCD_WO_L);
				
				// 전단주편지정구분 [전단주편지정구분]                                                      
				outRec.setField("FS_MSLAB_ASGN_GP"+(nIdx+1), szFS_MSLAB_ASGN_GP);
				
				// 주문여재구분 [주문여재구분]                                                      
				outRec.setField("ORD_YEOJAE_GP"+(nIdx+1), szORD_YEOJAE_GP);
				
				// Tapered구분 [Tapered구분]                                                      
				outRec.setField("TAPER_E_D_GP"+(nIdx+1), szTAPER_E_D_GP);
				
				// 주편형상구분 [주편형상구분]                                                      
				outRec.setField("MSLAB_FRM_GP"+(nIdx+1), szMSLAB_FRM_GP);
				
				// Slab지시행선구분 [Slab지시행선구분]                                                      
				outRec.setField("SLAB_WO_RT_GP"+(nIdx+1), szSLAB_WO_RT_GP);
				
				// 연주주편Sampling코드 [연주주편Sampling코드]                                                      
				outRec.setField("CCSLAB_SMPL_CD"+(nIdx+1), szCCSLAB_SMPL_CD);
				
				// 용도코드 [용도코드]                                                      
				outRec.setField("USAGE_CD"+(nIdx+1), szUSAGE_CD);
				
				// 사내보증기호 [사내보증기호]                                                      
				outRec.setField("STLQLTY_SYM"+(nIdx+1), szSTLQLTY_SYM);
				
				// 품명코드 [품명코드]                                                      
				outRec.setField("ITEMNAME_CD"+(nIdx+1), szITEMNAME_CD);
				
				// 압연주문두께                                                     
				outRec.setField("ORD_CONV_T"+(nIdx+1), szORD_CONV_T);
				
				// 후판재트리밍코드                                                     
				outRec.setField("ORD_EDGE_CD"+(nIdx+1), szORD_EDGE_CD);
				
			}
			
			// 공백 건수처리
			for(int nIdx3 = nIdx; nIdx3 < iArry; nIdx3++){
				// 주편번호 [주편번호]                                   
				outRec.setField("MSLAB_NO"+(nIdx3+1), YdUtils.fillSpZr(" ", 11, 1));

				// 예정주편번호 [예정주편번호]                                   
				outRec.setField("PLN_MSLAB_NO"+(nIdx3+1), YdUtils.fillSpZr(" ", 9, 1));

				// 연주Machine코드 [연주Machine코드]                                   
				outRec.setField("CC_MC_CD"+(nIdx3+1), YdUtils.fillSpZr(" ", 1, 1));

				// 주조Strand번호 [주조Strand번호]                                   
				outRec.setField("CS_STR_NO"+(nIdx3+1), YdUtils.fillSpZr(" ", 2, 1));

				// 출강목표기호 [출강목표기호]                                                      
				outRec.setField("SPEC_HEATOUT_AIM"+(nIdx3+1), YdUtils.fillSpZr(" ", 13, 1));
				
				// 가열로장입Lot번호 [가열로장입Lot번호]                                                      
				outRec.setField("REFUR_CHG_LOT_NO"+(nIdx3+1), YdUtils.fillSpZr(" ", 10, 1));

				// Slab산적Lot코드 [Slab산적Lot코드]                                                      
				outRec.setField("STACK_LOT_NO"+(nIdx3+1), YdUtils.fillSpZr(" ", 14, 1));
				
				// HCR구분 [HCR구분]                                                      
				outRec.setField("HCR_GP"+(nIdx3+1), YdUtils.fillSpZr(" ", 1, 1));
				
				// 주편두께 [주편두께]       
				outRec.setField("MSLAB_T"+(nIdx3+1), YdUtils.fillSpZr(" ", 6, 1));
								
				// 주편폭 [주편폭]                                                      
				outRec.setField("MSLAB_W"+(nIdx3+1), YdUtils.fillSpZr(" ", 5, 1));	

				// 주편길이 [주편길이]                                                      
				outRec.setField("MSLAB_L"+(nIdx3+1), YdUtils.fillSpZr(" ", 5, 1));
				
				// 주편중량 [주편중량]                                                      
				outRec.setField("MSLAB_WT"+(nIdx3+1), YdUtils.fillSpZr(" ", 5, 1));
				
				// 지시주편손질방법
				outRec.setField("WO_MSLAB_RPR_MTD"+(nIdx3+1), YdUtils.fillSpZr(" ", 1, 1));
				
				// 연주주편손질구분 [연주주편손질구분]                                                      
				outRec.setField("CCSLAB_RPR_GP"+(nIdx3+1), YdUtils.fillSpZr(" ", 1, 1));
				
				// Tracking주편손질방법
				outRec.setField("TRK_MSLAB_RPR_MTD"+(nIdx3+1), YdUtils.fillSpZr(" ", 1, 1));

				// 주편지정주편손질방법
				outRec.setField("MSLAB_ASGN_MSLAB_RPR_MTD"+(nIdx3+1), YdUtils.fillSpZr(" ", 1, 1));

				// 조업지정주편손질방법
				outRec.setField("PTOP_ASGN_MSLAB_RPR_MTD"+(nIdx3+1), YdUtils.fillSpZr(" ", 1, 1));

				// 품질Traking이상조치코드1
				outRec.setField("QT_TRK_AB_TRT_CD1"+(nIdx3+1), YdUtils.fillSpZr(" ", 3, 1));

				// 품질Traking이상조치코드2
				outRec.setField("QT_TRK_AB_TRT_CD2"+(nIdx3+1), YdUtils.fillSpZr(" ", 3, 1));

				// 품질Traking이상조치코드3
				outRec.setField("QT_TRK_AB_TRT_CD3"+(nIdx3+1), YdUtils.fillSpZr(" ", 3, 1));
				
				outRec.setField("SCARF_OPRNER_SET_GP"+(nIdx3+1), YdUtils.fillSpZr(" ", 1, 1));
				
				// Scarfing조업자부여원인구분 [Scarfing조업자부여원인구분]                                                      
				outRec.setField("SCARF_OPRNER_SET_RSN_GP"+(nIdx3+1), YdUtils.fillSpZr(" ", 1, 1));
				
				// 주편절사지시길이 [주편절사지시길이]                                                      
				outRec.setField("MSLAB_DSCD_WO_L"+(nIdx3+1), YdUtils.fillSpZr(" ", 2, 1));
				
				// 전단주편지정구분 [전단주편지정구분]                                                      
				outRec.setField("FS_MSLAB_ASGN_GP"+(nIdx3+1), YdUtils.fillSpZr(" ", 1, 1));
				
				// 주문여재구분 [주문여재구분]                                                      
				outRec.setField("ORD_YEOJAE_GP"+(nIdx3+1), YdUtils.fillSpZr(" ", 1, 1));
				
				// Tapered구분 [Tapered구분]                                                      
				outRec.setField("TAPER_E_D_GP"+(nIdx3+1), YdUtils.fillSpZr(" ", 1, 1));
				
				// 주편형상구분 [주편형상구분]                                                      
				outRec.setField("MSLAB_FRM_GP"+(nIdx3+1), YdUtils.fillSpZr(" ", 2, 1));
				
				// Slab지시행선구분 [Slab지시행선구분]                                                      
				outRec.setField("SLAB_WO_RT_GP"+(nIdx3+1), YdUtils.fillSpZr(" ", 2, 1));
				
				// 연주주편Sampling코드 [연주주편Sampling코드]                                                      
				outRec.setField("CCSLAB_SMPL_CD"+(nIdx3+1), YdUtils.fillSpZr(" ", 4, 1));
				
				// 용도코드 [용도코드]                                                      
				outRec.setField("USAGE_CD"+(nIdx3+1), YdUtils.fillSpZr(" ", 4, 1));
				
				// 사내보증기호 [사내보증기호]                                                      
				outRec.setField("STLQLTY_SYM"+(nIdx3+1), YdUtils.fillSpZr(" ", 10, 1));
				
				// 품명코드 [품명코드]                                                      
				outRec.setField("ITEMNAME_CD"+(nIdx3+1), YdUtils.fillSpZr(" ", 3, 1));
				
				// 압연주문두께 
				outRec.setField("ORD_CONV_T"+(nIdx3+1), YdUtils.fillSpZr(" ", 6, 1));
								
				// 후판재트리밍코드
				outRec.setField("ORD_EDGE_CD"+(nIdx3+1), YdUtils.fillSpZr(" ", 1, 1));
				
			}

			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

		}catch(Exception e){
			szMsg = "[MakeTcC3::makeC3L005()] C3(C연주정정L2) 송신  Carry-In재료정보   데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
			return -1;
		}
		
		return outRecSet.size();
	} // end of makeC3L005()	

	
	
	
	
	/**
	 * YDC3L006 : 대차출발지시 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeC3L006(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID				MSG_ID					VARCHAR2(8)		YDC3L006
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
		String szMethodName        = "makeC3L006";
		String szMsg               = "";
		String szOperationName     = "C연주정정L2 대차출발지시";
		
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
		
		// Length 80 (60 + 20)
		int nTcLen                 = 20;
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeC3L006() IN==========================\n", YdConstant.DEBUG);	
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
				szMsg = "[MakeTcC3::makeC3L006()] 대차스케쥴 테이블 조회오류  (" + ydDaoUtils.paraRecChkNull(inRec, "YD_TCAR_SCH_ID") + ")" + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			} else if(intRtnVal == 0) {
				szMsg = "[MakeTcC3::makeC3L006()] 대차스케쥴 테이블 조회건수 없음  (" + ydDaoUtils.paraRecChkNull(inRec, "YD_TCAR_SCH_ID") + ")" + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;
			}		
			
			for(int nIdx=0; nIdx<intRtnVal; nIdx++) {
				recGetVal = rsResult.getRecord(nIdx);			
			
				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();									
				outRec.setField("MSG_ID" , "YDC3L006");
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
						szMsg = "[MakeTcC3::makeC3L006()] YD_CARUD_STOP_LOC 항목의 값이 없음";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);									
					}
				}else if(szYD_EQP_WRK_STAT.equals("U")){
					if(szYD_CARLD_STOP_LOC != null && !szYD_CARLD_STOP_LOC.equals("")){
						szYD_AIM_BAY_GP = szYD_CARLD_STOP_LOC.substring(1, 2);					
					} else {
						szYD_AIM_BAY_GP = "";	
						szMsg = "[MakeTcC3::makeC3L006()] YD_CARLD_STOP_LOC 항목의 값이 없음";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);									
					}
				}else {
					szMsg = "[MakeTcC3::makeC3L006()] 목표동구분에서 야드설비작업상태가 (L 혹은 U) 값이 없음: ";	
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);										
					return 0;
				}
				outRec.setField("YD_AIM_BAY_GP", YdUtils.fillSpZr(szYD_AIM_BAY_GP, 1, 1));				
				
				// 야드대차상차위치 [야드대차상차위치]
				outRec.setField("YD_TCAR_LD_LOC", YdUtils.fillSpZr(szYD_CARLD_STOP_LOC, 6, 1));				
				
				// 야드대차하차위치 [야드대차하차위치]
				outRec.setField("YD_TCAR_UD_LOC", YdUtils.fillSpZr(szYD_CARUD_STOP_LOC, 6, 1));		
				
				// RecordSet에 추가	
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szClassName, szMethodName, "\n======================makeC3L006() OUT("+ Integer.toString(nIdx) + ")==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);			
				ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
			}
		}catch(Exception e){
			szMsg = "[MakeTcC3::makeC3L006()] C3(C연주정정L2) 대차출발지시  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}
	
		return outRecSet.size();
	} // end of makeC3L006()	

	
	
	
	
	/**
	 * YDC3L007 : 대차작업실적 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeC3L007(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID				MSG_ID				VARCHAR2(8)			YDC3L007
		//		2.	생성일				DATE				VARCHAR2(10)		YYYY-MM-DD
		//		3.	생성시간				TIME				VARCHAR2(8)			24HH-MM-SS
		//		4.	전문구분				MSG_GP				VARCHAR2(1)			I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이				MSG_LEN				NUMBER  (4)		
		//		6.	임시					TEMP				VARCHAR2(29)	
		
		//		7.	설비ID               YD_EQP_ID			VARCHAR2(6)		
		//      8      야드대차현재동구분           YD_TCAR_BAY         VARCHAR2(1)
		//      9.  야드대차현재정지위치        YD_TCAR_STOP_LOC    VARCHAR2(6)
		//      10. 적재상태                            YD_EQP_WRK_STAT     VARCHAR2(1)
		//      11. 적치적재현재수량(누계)  YD_STK_CUR_SH       NUMBER(2)
		//      12. 적치적재현재중량               YD_STK_CUR_WT       NUMBER(7)
		//      13. 크래인Handling매수         YD_CRN_HANDLING_SH  NUMBER(2)
		//      14. 크레인Handling중량         YD_CRN_HANDLING_WT  NUMBER(7)
		//		15.	재료번호1			  	 STL_NO1			VARCHAR2(11)		
		//		16.	재료번호2				 STL_NO2			VARCHAR2(11)		
		//		17.	재료번호3				 STL_NO3			VARCHAR2(11)		
		//		18.	재료번호4				 STL_NO4			VARCHAR2(11)		
		//		19.	재료번호5				 STL_NO5			VARCHAR2(11)		
		//		20.	재료번호6				 STL_NO6			VARCHAR2(11)		
		//		21.	재료번호7				 STL_NO7			VARCHAR2(11)		
		//		22.	재료번호8				 STL_NO8			VARCHAR2(11)		
		//		23.	재료번호9				 STL_NO9			VARCHAR2(11)		
		//		24.	재료번호10			 STL_NO10			VARCHAR2(11)		
  		
		// 레코드 선언
		JDTORecord recPara            = null;
		JDTORecord recWrkPara         = null;
		JDTORecordSet rsResult        = null;
		JDTORecordSet rsWrkResult     = null;
		JDTORecord recGetVal          = null;
		JDTORecord outRec             = null;

		// DAO객체 생성
		YdDaoUtils ydDaoUtils         = new YdDaoUtils();	
		YdUtils ydUtils               = new YdUtils();
		YdTcarSchDao ydTcarSchDao     = new YdTcarSchDao();
		
		// 변수선언
		String szMethodName           = "makeC3L007";
		String szMsg                  = "";
		String szOperationName        = "C연주정정L2 대차작업실적";
			
		// 야드설비ID
		String szYD_EQP_ID            = "";

		// 야드상차정지위치
		String szYD_CARLD_STOP_LOC    = "";
		
		// 야드하차정지위치
		String szYD_CARUD_STOP_LOC    = "";
		
		// 야드설비작업상태
		String szYD_EQP_WRK_STAT      = "";
				
		// 야드설비작업매수
		String szYD_EQP_WRK_SH        = "";
		
		// 야드설비작업중량
		String szYD_EQP_WRK_WT        = "";
		
		// 차량진행상태
		String szYD_CAR_PROG_STAT     = "";
		
		// 재료번호
		String szSTL_NO               = "";
		
		// 상차작업예약ID
		String szYD_CARLD_WRK_BOOK_ID = "";
		
		// 하차작업예약ID
		String szYD_CARUD_WRK_BOOK_ID = "";

		// 작업예약ID
		String szYD_WRK_BOOK_ID       = "";

		String strTemp                = "";

		int nTotalWgt                 = 0;
		
		// 리턴값
		int intRtnVal                 = 0;
		int nRtnVal                   = 0;
		
		// TC Length = 202       (60 + 142)
		int nTcLen                    = 142;
		
		int nIdx                      = 0;
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeC3L007() IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
			
			// 레코드 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();			
			rsWrkResult = JDTORecordFactory.getInstance().createRecordSet("");
			recWrkPara = JDTORecordFactory.getInstance().create();			
									
			//=======================================================================================================================
			// 대차스케쥴 테이블 조회 (Key: 대차스케쥴스케쥴 ID)
			//=======================================================================================================================
			recPara.setField("YD_TCAR_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_TCAR_SCH_ID").trim());
			intRtnVal = ydTcarSchDao.getYdTcarsch(recPara, rsResult, 2);
			if(intRtnVal < 0) {
				szMsg = "[MakeTcC3::makeC3L007()] 대차스케쥴 테이블 조회오류  (" + ydDaoUtils.paraRecChkNull(inRec, "YD_TCAR_SCH_ID") + ")" + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			} else if(intRtnVal == 0) {
				szMsg = "[MakeTcC3::makeC3L007()] 대차스케쥴 테이블 조회건수 없음  (" + ydDaoUtils.paraRecChkNull(inRec, "YD_TCAR_SCH_ID") + ")" + "[Ret : " + intRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				return 0;
			}else {
				szMsg = "[MakeTcC3::makeC3L007()] 대차스케쥴 조회 성공";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
			}
			
			rsResult.first();
			recGetVal = rsResult.getRecord();

			szYD_EQP_ID            = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(recGetVal, "YD_EQP_ID"), 6, 1);
			szYD_CARLD_STOP_LOC    = ydDaoUtils.paraRecChkNull(recGetVal, "YD_TCAR_LD_LOC"); 
			szYD_CARUD_STOP_LOC    = ydDaoUtils.paraRecChkNull(recGetVal, "YD_TCAR_UD_LOC");
			szYD_CARLD_WRK_BOOK_ID = YdUtils.fillSpZr(recGetVal.getFieldString("YD_CARLD_WRK_BOOK_ID"), 18, 1);
			szYD_CARUD_WRK_BOOK_ID = YdUtils.fillSpZr(recGetVal.getFieldString("YD_CARUD_WRK_BOOK_ID"), 18, 1);
			szYD_CAR_PROG_STAT     = YdUtils.fillSpZr(recGetVal.getFieldString("YD_CAR_PROG_STAT"), 1, 1);        // 0~5:상차   A~E:하차

			szMsg = "[MakeTcC3::makeC3L007()] 전문편집 시작";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
			
			// 레코드 생성
			outRec = JDTORecordFactory.getInstance().create();									
			
			// 헤더부
			outRec.setField("MSG_ID" , "YDC3L007");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 야드설비구분 [야드설비ID]
			outRec.setField("YD_EQP_ID", szYD_EQP_ID);

			szMsg = "[MakeTcC3::makeC3L007()] 상차 하차 체크 . . .";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			

			// 상차(L)
			if(szYD_CAR_PROG_STAT.equals("1") || szYD_CAR_PROG_STAT.equals("2") || szYD_CAR_PROG_STAT.equals("3") || szYD_CAR_PROG_STAT.equals("4") || szYD_CAR_PROG_STAT.equals("5")){
				if(szYD_CARLD_STOP_LOC != null && !szYD_CARLD_STOP_LOC.equals("")){
					// 야드대차현재동구분  (상차)
					outRec.setField("YD_TCAR_BAY", YdUtils.fillSpZr(szYD_CARLD_STOP_LOC.substring(1, 2), 1, 1));					

					// 야드대차현재정지위치  (상차)             
					outRec.setField("YD_TCAR_STOP_LOC", YdUtils.fillSpZr(szYD_CARLD_STOP_LOC, 6, 1));					
				} else {
					outRec.setField("YD_TCAR_BAY", YdUtils.fillSpZr(" ", 1, 1));					
					outRec.setField("YD_TCAR_STOP_LOC", YdUtils.fillSpZr(" ", 6, 1));										
				}
				
				szYD_WRK_BOOK_ID = szYD_CARLD_WRK_BOOK_ID;	
				szYD_EQP_WRK_STAT = "L";
			}else if(szYD_CAR_PROG_STAT.equals("A") || szYD_CAR_PROG_STAT.equals("B") || szYD_CAR_PROG_STAT.equals("C") || szYD_CAR_PROG_STAT.equals("D") || szYD_CAR_PROG_STAT.equals("E")){
			// 하차(U)
				if(szYD_CARUD_STOP_LOC != null && !szYD_CARUD_STOP_LOC.equals("")){
					// 야드대차현재동구분  (하차)              
					outRec.setField("YD_TCAR_BAY", YdUtils.fillSpZr(szYD_CARUD_STOP_LOC.substring(1, 2), 1, 1));
					
					// 야드대차현재정지위치  (하차)             
					outRec.setField("YD_TCAR_STOP_LOC", YdUtils.fillSpZr(szYD_CARUD_STOP_LOC, 6, 1));
				} else {
					outRec.setField("YD_TCAR_BAY", YdUtils.fillSpZr(" ", 1, 1));					
					outRec.setField("YD_TCAR_STOP_LOC", YdUtils.fillSpZr(" ", 6, 1));										
				}

				szYD_WRK_BOOK_ID = szYD_CARUD_WRK_BOOK_ID;
				szYD_EQP_WRK_STAT = "U";
			} else {
				szMsg = "[MakeTcC3::makeC3L007()] 상차도 아니고 하차도 아니고 . . .";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
			}
				
			// 야드적재상태(상차/하차 : L/U)
			outRec.setField("YD_EQP_WRK_STAT", YdUtils.fillSpZr(szYD_EQP_WRK_STAT, 1, 1));				
			

			//=======================================================================================================================
			// 2009.10.16
			// 권오창
			// 대차스케줄 + 대차이송재료 조회 (대차스케줄ID)
			// 구분자    : 6  [com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschIdStlNoMtlWtByTcarSchId]
			// 정렬        : STL_NO ASC
			// 파라미터 : YD_TCAR_SCH_ID   
			//=======================================================================================================================
			recWrkPara.setField("YD_TCAR_SCH_ID", ydDaoUtils.paraRecChkNull(inRec, "YD_TCAR_SCH_ID"));
			nRtnVal = ydTcarSchDao.getYdTcarsch(recWrkPara, rsWrkResult, 6);
			if(nRtnVal < 0) {
				szMsg = "[MakeTcC3::makeC3L007()] 대차스케줄 + 대차이송재료 + 저장품 테이블 조회오류  (" + szYD_WRK_BOOK_ID + ")" + "[Ret : " + nRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
				return 0;
			} else if(nRtnVal == 0) {
				szMsg = "[MakeTcC3::makeC3L007()] 대차스케줄 + 대차이송재료 + 저장품 테이블 조회건수 없음  (" + szYD_WRK_BOOK_ID + ")" + "[Ret : " + nRtnVal + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			

				// 적치적재현재수량(누계)
				outRec.setField("YD_STK_CUR_SH", YdUtils.fillSpZr(" ", 2, 1));				
	
				// 적치적재현재중량
				outRec.setField("YD_STK_CUR_WT", YdUtils.fillSpZr(" ", 7, 1));				
	
				// 크레인Handling매수
				outRec.setField("YD_CRN_HANDLING_SH", YdUtils.fillSpZr(" ", 2, 1));				
	
				// 크레인Handling중량
				outRec.setField("YD_CRN_HANDLING_WT", YdUtils.fillSpZr(" ", 7, 1));				
	
				// 재료번호 10건 모두 공백 건수처리
				for(int nIdx2=0; nIdx2<10; nIdx2++) {
					outRec.setField("STL_NO" + (1+nIdx2), YdUtils.fillSpZr(" ", 11, 1));					
				}							
			} else {
				// 매수
				szYD_EQP_WRK_SH = "" + nRtnVal;
	
				// 중량합 구하기
				szMsg = "[MakeTcC3::makeC3L007()] 중량합 구하기";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);			
				for(nIdx=0; nIdx<nRtnVal; nIdx++) {
					recGetVal = rsWrkResult.getRecord(nIdx);	
					strTemp = ydDaoUtils.paraRecChkNull(recGetVal, "YD_MTL_WT");
					nTotalWgt += Integer.parseInt(strTemp);
				}
				szYD_EQP_WRK_WT = "" + nTotalWgt; 
				
				// 적치적재현재수량(누계)
				outRec.setField("YD_STK_CUR_SH", YdUtils.fillSpZr(szYD_EQP_WRK_SH, 2, 1));				
	
				// 적치적재현재중량
				outRec.setField("YD_STK_CUR_WT", YdUtils.fillSpZr(szYD_EQP_WRK_WT, 7, 1));				
	
				// 크레인Handling매수
				outRec.setField("YD_CRN_HANDLING_SH", YdUtils.fillSpZr(szYD_EQP_WRK_SH, 2, 1));				
	
				// 크레인Handling중량
				outRec.setField("YD_CRN_HANDLING_WT", YdUtils.fillSpZr(szYD_EQP_WRK_WT, 7, 1));				
	
				// 재료번호 [재료번호]
				for(nIdx=0; nIdx<nRtnVal; nIdx++) {
					recGetVal = rsWrkResult.getRecord(nIdx);	
	
					szSTL_NO = YdUtils.fillSpZr(recGetVal.getFieldString("STL_NO"), 11, 1);
					outRec.setField("STL_NO" + (1+nIdx), szSTL_NO);		
				}		
				
				// 공백 건수처리
				for(int nIdx2=nIdx; nIdx2<10; nIdx2++) {
					outRec.setField("STL_NO" + (1+nIdx2), YdUtils.fillSpZr(" ", 11, 1));					
				}			
			}
				
				
				
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================makeC3L007() OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
		}catch(Exception e){
			szMsg = "[MakeTcC3::makeC3L007()] C3(C연주정정L2) 송신  대차작업실적  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}
	
		return outRecSet.size();
	} // end of makeC3L007()	

	
	
	
	
	/**
	 * YDC3L008 : OHC Take-In 완료 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeC3L008(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID				MSG_ID					VARCHAR2(8)		YDC3L008
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
		String szMethodName     = "makeC3L008";
		String szMsg            = "";
		String szOperationName  = "C연주정정L2 OHC Take-In 완료";
				
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
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeC3L008() IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);

			szYdEqpId = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYdBedNo = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTLNo   = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");
			
			outRec = JDTORecordFactory.getInstance().create();
			
			// 헤더부
			outRec.setField("MSG_ID" , "YDC3L008");
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
			ydUtils.putLog(szClassName, szMethodName, "\n======================makeC3L008() OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);	
		}catch(Exception e){
			szMsg = "[MakeTcC3::makeC3L008()] C3(C연주정정L2) OHC TAKE IN 완료  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}
		
		return outRecSet.size();	
	}	
	
	/**
	 * YDC3L009 : 열연재열재 재료정보 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeC3L009(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID				MSG_ID					VARCHAR2(8)		YDC3L008
		//		2.	생성일				DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간				TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분				MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이				MSG_LEN					NUMBER  (4)		
		//		6.	임시					TEMP					VARCHAR2(29)
		
		//		7.	Slab번호   			SLAB_NO	                VARCHAR2(11)	재료번호	
		//		8.	예정Slab번호		  	PLAN_SLAB_NO            VARCHAR2(11)	재료번호
		
		// 레코드 선언
		JDTORecord outRec       = null;
		
		// DAO객체 생성
		YdDaoUtils ydDaoUtils   = new YdDaoUtils();	
		YdUtils    ydUtils      = new YdUtils();

		// 변수선언
		String szMethodName     = "makeC3L009";
		String szMsg            = "";
		String szOperationName  = "열연 재열재 재료정보";
		// Slab번호
		String szSTLNo          = "";

		// TC Length = 82      (60 + 22)
		int nTcLen              = 22;
				
		try{			
			// Debug MSG

			szSTLNo   = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");
			
			outRec = JDTORecordFactory.getInstance().create();
			
			// 헤더부
			outRec.setField("MSG_ID" , "YDC3L009");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));                                                         

			// Slab번호
			outRec.setField("SLAB_NO", YdUtils.fillSpZr(szSTLNo, 11, 1));
			// 예정Slab번호
			outRec.setField("PLAN_SLAB_NO", YdUtils.fillSpZr(" ", 11, 1));
			
			outRecSet.addRecord(outRec);
			
		}catch(Exception e){
			szMsg = "[MakeTcC3::makeC3L009()] C3(C연주정정L2) 열연재열재 재료정보  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}
		
		return outRecSet.size();	
	}	
	//---------------------------------------------------------------------------	
} // end of class MakeTcC3
