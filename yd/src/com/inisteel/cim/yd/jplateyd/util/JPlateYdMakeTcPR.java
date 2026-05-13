/*
 * @(#) PP (1후판조업L3) 송신 용 전문 생성
 *
 * @version			V1.00
 * @author			
 * @date			2012/11/30
 *
 * @description		(후판조업L3) 송신 용 전문 생성
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

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 * PR (후판조업L3) 송신 용 전문 생성
 * @author 김현우
 *
 */
public class JPlateYdMakeTcPR { 

	// YDPRJ011	저장위치변경정보
	// 2013.09.16 FROM 위치 NULL일때 NULL로 전송

	// 클래스명
	private static final String SZ_CLASS_NAME  = JPlateYdMakeTcPR.class.getName();

	/**
	 * YDPRJ011	: 저장위치변경정보 (크레인권하처리, 저장위치변경처리)
	 * 호출파라미터
	 * 			- YD_STK_COL_FR : From적치열
	 * 			- YD_STK_BED_FR : From적치BED
	 * 			- YD_STK_COL_TO : TO적치열
	 * 			- YD_STK_BED_TO : TO적치BED
	 * 			- YD_EQP_WRK_SH : 야드설비작업매수
	 * 			- ARR_STL_NO	: 재료번호 Array
	 *
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeYDPRJ011(JDTORecord inRec, JDTORecordSet outRecSet) {

		// 		1.  JMS_TC_CD				JMSTC코드				CHAR(8)			YDPRJ011
		// 		2.  JMS_TC_CREATE_DDTT		JMSTC생성일시				DATE(14)
		// 		3.	YD_GP					야드구분					CHAR(1)			P:1후판정정야드
		// 		4.	FROM_STORE_LOC			From저장위치				CHAR(8)			야드적치열구분(6자리) + 야드적치Bed번호(2자리)
		// 		5.	TO_STORE_LOC			To저장위치				CHAR(8)			야드적치열구분(6자리) + 야드적치Bed번호(2자리)
		// 		6.	YD_EQP_WRK_SH			야드설비작업매수			NUMBER(2)		크레인 1 Handring 매수
		// 		7.	STL_NO1					재료번호1					CHAR(11)		후판Plate번호 또는 후판날판번호
		// 		8.	BOOK_OUT_RESN1			Book-Out원인1			CHAR(3)			Book-Out원인
		// 		9.	PL_BOOK_INOUT_GP1		후판북인아웃구분1			CHAR(1)			1:Book In, 2:Book Out
		// 		10.	YD_STK_LYR_NO1			야드적치단1				CHAR(3)			적치단
		// 		11.	PL_TRCK_ZONE_ASG		후판트래킹존지정			CHAR(5)	 chito20230202
		//      12. PL_BOOK_OUT_PIT         북아웃 작업풀핏          chito20230202
		//      13. PL_BOOK_OUT_CRANE       북아웃 작업크레인(예:PBCRB1) chito20230202
		//      14. PL_BOOK_OUT_MOD         북아웃모드 chito20230202

		// 레코드 선언
		JDTORecord    recPara 		= null;
		JDTORecordSet rsResult 		= null;
		JDTORecord    outRec 		= null;
		JDTORecord    tempRec 		= null;

		// DAO객체 생성
		JPlateYdStkLyrDAO 	ydStkLyrDao	= new JPlateYdStkLyrDAO();
		JPlateYdDaoUtils 	ydDaoUtils  = new JPlateYdDaoUtils();
		JPlateYdUtils 		ydUtils     = new JPlateYdUtils();
		
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
	    YdUtils 			ydLogUtils  = new YdUtils();

		// 변수선언
		String 	szMethodName     	= "makeYDPRJ011";
		String 	szOperationName     = "저장위치변경정보";
		String 	szMsg        	    = "";

		String 	szYdStkColFr		= "";		// From적치열
		String 	szYdStkBedFr		= "";		// From적치베드
		String 	szYdStkColTo		= "";		// To적치열
		String 	szYdStkBedTo		= "";		// To적치베드
		String 	szYdEqpWrkSh		= "";		// 야드설비작업매수
		String 	szFromStoreLoc		= "";		// From저장위치 : 적치열+베드
		String 	szToStoreLoc		= "";		// To저장위치   : 적치열+베드
		String 	szPlBookInoutGp		= "";		// BOOK-OUT FLAG : 1-(TO저장위치가 RT일때), 2-FROM저장위치가 RT일때, '':RT가 아닐때
		String	szArrStlNo			= "";
		String	szYdStkLyrNo		= "";
		String	szBookOutResn		= "";		// BOOK-OUT 원인코드
		String	szPlTrckZoneAsg		= "";		// 후판트래킹존지정 chito20230202
		String	szPlBookOutPit		= "";		// 북아웃 작업풀핏  chito20230202
		String	szPlBookOutCrane	= "";		// 북아웃 작업크레인(예:PBCRB1) chito20230202
		String	szPlBookOutMod		= "";		// 북아웃모드 chito20230202
		String  szIS_PF_TO_PF       = "";
		// 리턴값
		int intRtnVal               = 0;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
								
		try {

			// Debug MSG
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n=======================makeYDPRJ011() IN========================\n", JPlateYdConst.DEBUG, logId);
			ydUtils.displayRecord(szOperationName, inRec);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG, logId);

			// 레코드 생성
			rsResult        = JDTORecordFactory.getInstance().createRecordSet("yd");
			recPara         = JDTORecordFactory.getInstance().create();

			// 호출프로그램에서  넘겨 받음
			szYdStkColFr		= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_FR"			);		// From적치열
			szYdStkBedFr		= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_FR"			);		// From적치BED
			szYdStkColTo		= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_TO"			);		// TO적치열
			szYdStkBedTo		= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_TO"			);		// TO적치BED
			szYdEqpWrkSh		= ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_WRK_SH", "20"	);		// 야드설비작업매수
			szArrStlNo			= ydDaoUtils.paraRecChkNull(inRec, "ARR_STL_NO"				);		// 재료번호 Array
			szPlBookOutCrane 	= ydDaoUtils.paraRecChkNull(inRec, "PL_BOOK_OUT_CRANE"		);		// 북아웃 작업크레인(예:PBCRB1)chito20230202
			szIS_PF_TO_PF       = ydDaoUtils.paraRecChkNull(inRec, "IS_PF_TO_PF");			// 공장간 이송 여부
			

			szMsg = "makeYDPRJ011 szIS_PF_TO_PF 공장간 이송여부 확인 : " + szIS_PF_TO_PF;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			
			// 파라미터로 재료번호 Array가 넘어 왔을때 재료조회 안함
			if (!"".equals(szArrStlNo)) {

				if (!"".equals(szYdStkColFr)) {
					szFromStoreLoc  = szYdStkColFr + ("".equals(szYdStkBedFr)?"01":szYdStkBedFr);
				}
				if (!"".equals(szYdStkColTo)) {
					szToStoreLoc    = szYdStkColTo + szYdStkBedTo;
				}

				// 2013.07.22 보완 :: FROM위치가 NULL일때 보완
				// 2013.09.16 보완 :: FROM위치가 NULL일때 NULL로 전송 (조업요청)
				//if ("".equals(szFromStoreLoc)) {
				//	szFromStoreLoc = ydUtils.substr(szToStoreLoc, 0, 2) + "010101";
				//}

				//=======================================================================================================================
				// 재료 건수만큼 반복
				//=======================================================================================================================
				String[] arrStlNo = szArrStlNo.split(";");

				if (arrStlNo == null || arrStlNo.length < 1) {
					szMsg = "S1 (1후판조업L3) 송신  저장위치변경정보  데이터중 재료번호 오류 : " + szArrStlNo;
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					
					return -1;
				}

				// Interface Layout에 20건임으로 Max 20건만 전송
				if (arrStlNo.length <= 20) {
					szYdEqpWrkSh = Integer.toString(arrStlNo.length);
				}

				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();
				outRec.setField("JMS_TC_CD", 			"YDPRJ011"									);
				outRec.setField("JMS_TC_CREATE_DDTT", 	JPlateYdUtils.getCurDate("yyyyMMddHHmmss")	);
				outRec.setField("YD_GP", 				JPlateYdConst.YD_GP_P_PLATE_YARD			);		// 2후판정정야드
				outRec.setField("FROM_STORE_LOC", 		szFromStoreLoc								);		// From저장위치 : 적치열+베드
				outRec.setField("TO_STORE_LOC", 		szToStoreLoc								);		// To저장위치 : 적치열+베드
				outRec.setField("YD_EQP_WRK_SH", 		szYdEqpWrkSh								);		// 야드설비작업매수

				// To위치가 RT일때 - Book-In으로 Set
				if ("RT".equals(ydUtils.substr(szToStoreLoc,2,2))) {
					szPlBookInoutGp = JPlateYdConst.PP_BOOK_IN_GP;			// '1' : BOOK-IN
				}

				// From위치가 RT일때 - Book-Out으로 Set
				if ("RT".equals(ydUtils.substr(szFromStoreLoc,2,2))) {
					szPlBookInoutGp = JPlateYdConst.PP_BOOK_OUT_GP;		// '2' : BOOK-OUT
				}

				for(int ii=0; ii<arrStlNo.length; ii++) {

					if (ii < 20) {
						rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
						recPara  = JDTORecordFactory.getInstance().create();
						tempRec  = JDTORecordFactory.getInstance().create();
						recPara.setField("STL_NO", 				arrStlNo[ii]						);
						recPara.setField("YD_STK_LYR_MTL_STAT", "C"									);
						recPara.setField("YD_GP", 				JPlateYdConst.YD_GP_P_PLATE_YARD	);
			        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
						recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
													              	
						intRtnVal = ydStkLyrDao.getYdStklyrByStlNoStat(recPara, rsResult);
						if (intRtnVal > 0) {
							rsResult.first();
							tempRec = rsResult.getRecord();
							szYdStkLyrNo 	= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_LYR_NO"	);
							szBookOutResn 	= ydDaoUtils.paraRecChkNull(tempRec, "BOOK_OUT_RESN"	);
							szPlTrckZoneAsg	= ydDaoUtils.paraRecChkNull(tempRec, "PL_TRCK_ZONE_ASG"	); //chito20230202
							szPlBookOutMod	= ydDaoUtils.paraRecChkNull(tempRec, "PL_BOOK_OUT_MOD"	); //chito20230202
							szPlBookOutPit	= ydDaoUtils.paraRecChkNull(tempRec, "PL_BOOK_OUT_PIT"	); //chito20230202	
						}

						// 후판Plate번호 또는 후판날판번호 : 재료번호 (11자리)
						outRec.setField("STL_NO"+Integer.toString(ii+1),  			arrStlNo[ii]	);

						// Book-Out원인 : Book-Out 일때만 Set
						if (JPlateYdConst.PP_BOOK_OUT_GP.equals(szPlBookInoutGp)) {		// '2' : BOOK-OUT
							outRec.setField("BOOK_OUT_RESN"+Integer.toString(ii+1), szBookOutResn	);
						} else {
							outRec.setField("BOOK_OUT_RESN"+Integer.toString(ii+1), ""				);
						}

						// 후판북인아웃구분
						outRec.setField("PL_BOOK_INOUT_GP"+Integer.toString(ii+1), 	szPlBookInoutGp	);

						// 저장위치
						outRec.setField("YD_STK_LYR_NO"+Integer.toString(ii+1), 	szYdStkLyrNo	);
					}
				}
				
				outRec.setField("PL_TRCK_ZONE_NO", 		szPlTrckZoneAsg		);  // 북아웃존코드 추가
				outRec.setField("PL_BOOK_OUT_MOD", 	    szPlBookOutMod		);  // 북아웃모드 추가
				outRec.setField("PL_BOOK_OUT_CRANE", 	szPlBookOutCrane	);  // 북아웃 작업크레인 추가
				outRec.setField("PL_BOOK_OUT_PIT", 	    szPlBookOutPit		);  // 북아웃 작업풀핏 추가
				
				if(!"".equals(szIS_PF_TO_PF)){
					outRec.setField("IS_PF_TO_PF", 	    szIS_PF_TO_PF);    
				}
				
				

			} else {

				if (!"".equals(szYdStkColFr)) {
					szFromStoreLoc  = szYdStkColFr + ("".equals(szYdStkBedFr)?"01":szYdStkBedFr);
				}
				if (!"".equals(szYdStkColTo)) {
					szToStoreLoc    = szYdStkColTo + ("".equals(szYdStkBedTo)?"01":szYdStkBedTo);
				}
				// 2013.07.22 보완 :: FROM위치가 NULL일때 보완
				// 2013.09.16 보완 :: FROM위치가 NULL일때 NULL로 전송 (조업요청)
				//if ("".equals(szFromStoreLoc)) {
				//	szFromStoreLoc = ydUtils.substr(szToStoreLoc, 0, 2) + "010101";
				//}

				//=======================================================================================================================
				// 적치단 테이블 조회 : 조회조건 TO저장위치로 매수만큼 상단부터 조회
				//=======================================================================================================================
				recPara.setField("YD_STK_COL_GP", szYdStkColTo);
				recPara.setField("YD_STK_BED_NO", szYdStkBedTo);
				recPara.setField("YD_EQP_WRK_SH", szYdEqpWrkSh);
	        	
//-------------------------------------------------------------------------------------------------------------------------
//2024.12.06 recPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
				recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
						              	
				intRtnVal = ydStkLyrDao.getStlNoTopCnt(recPara, rsResult);

				if (intRtnVal < 0) {
					szMsg = "적치단 테이블 조회오류 .. 저장위치(" + szToStoreLoc + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					
					return 0;
				} else if (intRtnVal == 0) {
					szMsg = "적치단 테이블 조회건수 없음 .. 저장위치(" + szToStoreLoc + ") " + "[Ret : " + Integer.toString(intRtnVal) + "]";
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					
					return 0;
				}

				// Interface Layout에 20건임으로 Max 20건만 전송
				if (rsResult.size() <= 20) {
					szYdEqpWrkSh = Integer.toString(rsResult.size());
				}

				// 헤더부
				outRec = JDTORecordFactory.getInstance().create();
				outRec.setField("JMS_TC_CD", 			"YDPRJ011"									);
				outRec.setField("JMS_TC_CREATE_DDTT", 	JPlateYdUtils.getCurDate("yyyyMMddHHmmss")	);
				outRec.setField("YD_GP", 				JPlateYdConst.YD_GP_P_PLATE_YARD			);		// 2후판정정야드
				outRec.setField("FROM_STORE_LOC", 		szFromStoreLoc								);		// From저장위치 : 적치열+베드
				outRec.setField("TO_STORE_LOC", 		szToStoreLoc								);		// To저장위치 : 적치열+베드
				outRec.setField("PL_TRCK_ZONE_NO", 		szPlTrckZoneAsg								);		// 북아웃존코드 chito20230202
				outRec.setField("PL_BOOK_OUT_MOD", 		szPlBookOutMod								);		// 북아웃모드 chito20230202
				outRec.setField("PL_BOOK_OUT_CRANE", 	szPlBookOutCrane							);		// 북아웃 작업크레인(예:PBCRB1) chito20230202
				outRec.setField("PL_BOOK_OUT_PIT", 		szPlBookOutPit								);		// 북아웃 작업풀핏   chito20230202
				outRec.setField("YD_EQP_WRK_SH", 		szYdEqpWrkSh								);		// 야드설비작업매수

				// To위치가 RT일때 - Book-In으로 Set
				if ("RT".equals(ydUtils.substr(szToStoreLoc,2,2))) {
					szPlBookInoutGp = JPlateYdConst.PP_BOOK_IN_GP;		// '1' : BOOK-IN
				}

				// From위치가 RT일때 - Book-Out으로 Set
				if ("RT".equals(ydUtils.substr(szFromStoreLoc,2,2))) {
					szPlBookInoutGp = JPlateYdConst.PP_BOOK_OUT_GP;		// '2' : BOOK-OUT
				}

				for(int ii=0; ii<rsResult.size(); ii++) {

					if (ii < 20) {
						// 후판Plate번호 또는 후판날판번호 : 재료번호 (11자리)
						outRec.setField("STL_NO"+Integer.toString(ii+1),  ydDaoUtils.paraRecChkNull(rsResult.getRecord(ii), "STL_NO")					);

						if (JPlateYdConst.PP_BOOK_OUT_GP.equals(szPlBookInoutGp)) {		// '2' : BOOK-OUT
							// Book-Out원인 : Book-Out 일때만 Set
							outRec.setField("BOOK_OUT_RESN"+Integer.toString(ii+1), ydDaoUtils.paraRecChkNull(rsResult.getRecord(ii), "BOOK_OUT_RESN")	);
						}
						// 후판북인아웃구분
						outRec.setField("PL_BOOK_INOUT_GP"+Integer.toString(ii+1), 	szPlBookInoutGp														);
						// 저장위치
						outRec.setField("YD_STK_LYR_NO"+Integer.toString(ii+1), 	ydDaoUtils.paraRecChkNull(rsResult.getRecord(ii), "YD_STK_LYR_NO")	);
					}
				}
				
				if(!"".equals(szIS_PF_TO_PF)){
					outRec.setField("IS_PF_TO_PF", 	    szIS_PF_TO_PF);    
				}
				
			}

			// RecordSet에 추가
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n======================makeYDPRJ011() OUT =======================\n", JPlateYdConst.DEBUG, logId);
			ydUtils.displayRecord(szOperationName, outRec);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG, logId);

		} catch(Exception e) {
			szMsg = "S1 (1후판조업L3) 송신  저장위치변경정보  데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			return -1;
		}

		return outRecSet.size();
	} // end of makeYDPRJ011()

} // end of class MaktTcPP
