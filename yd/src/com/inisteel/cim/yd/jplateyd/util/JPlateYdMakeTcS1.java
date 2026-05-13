/*
 * @(#) S1 (2후판전단L2) 송신 용 전문 생성
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/30
 *
 * @description		(2후판전단L2) 송신 용 전문 생성
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/30   김현우      김현우       최초작성
 */

package com.inisteel.cim.yd.jplateyd.util;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;

/**
 * S1 (2후판전단L2) 송신 용 전문 생성
 * @author 김현우
 *
 */
public class JPlateYdMakeTcS1 {

	// YDS1L005	BOOK IN/OUT 실적

	// 클래스명
	private static final String SZ_CLASS_NAME  = JPlateYdMakeTcS1.class.getName();

	/**
	 * YDS1L005	: BOOK IN/OUT 실적 (N건 전송)
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeYDS1L005(JDTORecord inRec, JDTORecordSet outRecSet) {
		//		1.	MSG_ID					전문ID					CHAR(8)			YDS1L005
		//		2.	DATE					생성일					CHAR(10)		YYYY-MM-DD
		//		3.	TIME					생성시간					CHAR(8)			24HH:MM:SS
		//		4.	MSG_GP					전문구분					CHAR(1)			I(new), U(modification), D(cancel,deletion)
		//		5.	MSG_LEN					전문길이					NUMBER(4)
		//		6.	TEMP					임시						CHAR(29)
		//		7.	PLATE_ID				PLATE_ID				CHAR(10)		Order Plate ID
		//		8.	SHIFT_CODE				SHIFT_CODE				CHAR(2)			근조
		//		9.	OPERATION_TYPE			OPERATION_TYPE			CHAR(1)			1:Book In, 2:Book Out
		//		10.	OPERATION_DESTINATION	OPERATION_DESTINATION	CHAR(8)			FROM위치/TO위치 (설비의 위치)
		//		11.	OPERATION_DATE			OPERATION_DATE			CHAR(14)		Date & Time (YYYYMMDDHH24MISS)

		// 레코드 선언
		JDTORecord    recPara 		= null;
		JDTORecordSet rsResult 		= null;
		JDTORecord    outRec 		= null;

		// DAO객체 생성
		JPlateYdStkLyrDAO 	ydStkLyrDao	= new JPlateYdStkLyrDAO();
		JPlateYdDaoUtils 	ydDaoUtils  = new JPlateYdDaoUtils();
		JPlateYdUtils 		ydUtils     = new JPlateYdUtils();

		// 변수선언
		String 		szMethodName     	= "makeYDS1L005";
		String 		szOperationName     = "BOOK IN/OUT 실적";
		String 		szMsg        	    = "";

		String 		szStlNo             = "";
		String 		szOperationType		= "";
		String 		szYdStkColGp		= "";
//		String		szYdStkBedNo		= "";
		String 		szOperationDate		= "";
		String		szL2ZoneNo			= "";
		String		szStlNoList			= "";
		String[]	arrStlNo		= null;

		// 리턴값
		int intRtnVal               = 0;

		// TC Length = 93 (HEADER:60 + BODY:35)
		int nTcLen                  = 35;

		try{
			// Debug MSG
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n=======================makeYDS1L005() IN========================\n", JPlateYdConst.DEBUG);
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG);

			// 레코드 생성
			rsResult        = JDTORecordFactory.getInstance().createRecordSet("");
			recPara         = JDTORecordFactory.getInstance().create();

			// 호출프로그램에서  넘겨 받음
			szStlNo 		= ydDaoUtils.paraRecChkNull(inRec, "STL_NO");						// 재료번호
			szStlNoList 	= ydDaoUtils.paraRecChkNull(inRec, "STL_NO_LIST");					// 재료번호 List
			szOperationType = ydDaoUtils.paraRecChkNull(inRec, "OPERATION_TYPE");				// 1:Book In, 2:Book Out
			szYdStkColGp	= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP");				// FROM위치
			szOperationDate = JPlateYdUtils.getCurDate("yyyyMMdd") + JPlateYdUtils.getCurDate("HHmmss");	// YYYYMMDDHH24MISS
			
			String sYdGp = "";
			if(szYdStkColGp.length() > 1){
				sYdGp = szYdStkColGp.substring(0, 1);
			}
			
			if (!"".equals(szStlNo)) {
				szStlNoList = szStlNo;   
			}
			arrStlNo 		= szStlNoList.split(";");

			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n======================= 대상건수 :: "+arrStlNo.length+" 건", 	JPlateYdConst.DEBUG);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n======================= 전송대상 :: "+szStlNoList, 			JPlateYdConst.DEBUG);

			for(int ii=0; ii<arrStlNo.length; ii++) {
				//=======================================================================================================================
				// 적치단 테이블 조회 : 조회조건 재료번호 , BOOK IN 시에만 TO위치를 조회
				//=======================================================================================================================
				szStlNo = arrStlNo[ii];

				if ("1".equals(szOperationType)) {

					recPara.setField("STL_NO", szStlNo);
					recPara.setField("YD_GP" , sYdGp);
					intRtnVal = ydStkLyrDao.getYdStklyrByStlNo(recPara, rsResult);

					if (intRtnVal < 0) {
						szMsg = "적치단 테이블 조회오류 .. 재료번호(" + szStlNo + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
						ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return 0;
					} else if (intRtnVal == 0) {
						szMsg = "적치단 테이블 조회건수 없음 .. 재료번호(" + szStlNo + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
						ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						//return 0;
						szYdStkColGp 	= "TCRTUT";
					}else{
						
						szYdStkColGp    = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_STK_COL_GP"); 	// 야드적치열
						szOperationDate = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "MOD_DDTT"); 		// 수정일시
					}
				}

				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();
				outRec.setField("MSG_ID" , "YDS1L005");
				outRec.setField("DATE"   , JPlateYdUtils.getCurDate("yyyy-MM-dd"));
				outRec.setField("TIME"   , JPlateYdUtils.getCurDate("HH:mm:ss"));
				outRec.setField("MSG_GP" , "I");
				outRec.setField("MSG_LEN", JPlateYdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0));
				outRec.setField("TEMP"   , JPlateYdUtils.fillSpZr("", 29, 1));

				// Order Plate ID : 재료번호 (10자리)
				outRec.setField("PLATE_ID",					JPlateYdUtils.fillSpZr(szStlNo, 10, 1));

				// 근조
				outRec.setField("SHIFT_CODE", 				JPlateYdUtils.fillSpZr("", 2, 1));

				// 1:Book In, 2:Book Out
				outRec.setField("OPERATION_TYPE", 			JPlateYdUtils.fillSpZr(szOperationType, 1, 1));
				
				//-------------------------------------------
				// 2015.09.09 윤재광	
				// #2UT 보급
				//-------------------------------------------
				if("TCRTUT".equals(szYdStkColGp)){
					String szYdStkBedNo = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
					if("01".equals(szYdStkBedNo)){
						szL2ZoneNo = "3270";
					}else{
						szL2ZoneNo = "3250";
					}
				}else{
					// FROM위치/TO위치
					// 2013.10.28 OPERATION_DESTINATION를 Zone No로 Set하도록 보완
					szL2ZoneNo = JPlateYdCommonUtils.getY7LocToRtZone(ydUtils.substr(szYdStkColGp, 0, 6));
				}
				outRec.setField("OPERATION_DESTINATION", 	JPlateYdUtils.fillSpZr(szL2ZoneNo, 8, 1));

				// Date & Time (YYYYMMDDHH24MISS)
				outRec.setField("OPERATION_DATE", 			JPlateYdUtils.fillSpZr(szOperationDate, 14, 1));

				// RecordSet에 추가
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n======================makeYDS1L005("+(ii+1)+") OUT ======================\n", JPlateYdConst.DEBUG);
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG);

			}

		} catch(Exception e) {
			szMsg = "S1 (2후판전단L2) 송신  BOOK IN/OUT 실적  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return -1;
		}

		return outRecSet.size();
	} // end of makeYDS1L005()

} // end of class MaktTcY7
