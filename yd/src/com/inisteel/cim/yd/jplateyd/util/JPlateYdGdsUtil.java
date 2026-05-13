/*
 * @(#) 2후판정정야드 크레인작업지시 관련 공통
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/12/13
 *
 * @description		2후판정정야드 크레인작업지시 관련 공통
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/12/13   김현우      김현우       최초작성  
 */

package com.inisteel.cim.yd.jplateyd.util;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSpecDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;

//-------------------------------------------------------------------------------------------------------------------------
//2024.11.19 1후판 정정 2열처리 Book-In/Book-Out 관련 야드공통 UTIL (putLogNew, getLogIdNew, isEmpty, getJDTOLogId) 부분 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

public class JPlateYdGdsUtil {

	private static final String	SZ_CLASS_NAME	= JPlateYdGdsUtil.class.getName();

	private static JPlateYdDaoUtils	ydDaoUtils	= new JPlateYdDaoUtils();
	private static JPlateYdUtils    ydUtils     	= new JPlateYdUtils();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 1후판 정정 2열처리 Book-In/Book-Out 관련 야드공통 UTIL (putLogNew, getLogIdNew, isEmpty, getJDTOLogId) 부분 
//-------------------------------------------------------------------------------------------------------------------------
    private static YdUtils 			ydLogUtils  = new YdUtils();

    /**
     * 폭/두께에 따른 크레인작업가능매수
     * @param dYdMtlW
     * @param dYdMtlT
     * @return
     */
    public static int getCrnWrkableShBasedOnWT(double dYdMtlT, double dYdMtlW) {

    	int intCrnWrkableSh	= 0;
    	if (dYdMtlT <= 2100) {
    		if (dYdMtlW <= 8) {
    			intCrnWrkableSh = 5;
    		} else if (dYdMtlW <= 10) {
    			intCrnWrkableSh = 4;
    		} else if (dYdMtlW <= 20) {
    			intCrnWrkableSh = 3;
    		} else if (dYdMtlW <= 30) {
    			intCrnWrkableSh = 2;
    		} else {
    			intCrnWrkableSh = 1;
    		}
    	} else if (dYdMtlT <= 3450) {
    		if (dYdMtlW <= 8) {
    			intCrnWrkableSh = 5;
    		} else if (dYdMtlW <= 10) {
    			intCrnWrkableSh = 4;
    		} else if (dYdMtlW <= 20) {
    			intCrnWrkableSh = 3;
    		} else if (dYdMtlW <= 30) {
    			intCrnWrkableSh = 2;
    		} else {
    			intCrnWrkableSh = 1;
    		}
    	} else if (dYdMtlT <= 4800) {
    		if (dYdMtlW <= 8) {
    			intCrnWrkableSh = 5;
    		} else if (dYdMtlW <= 10) {
    			intCrnWrkableSh = 4;
    		} else if (dYdMtlW <= 20) {
    			intCrnWrkableSh = 3;
    		} else if (dYdMtlW <= 30) {
    			intCrnWrkableSh = 2;
    		} else {
    			intCrnWrkableSh = 1;
    		}
    	}

    	return intCrnWrkableSh;
    }

    /**
     * 2후판정정야드 XY좌표계산
     * @param recPara
     * @param recResult
     * @return
     * @throws JDTOException
     */
    public static String procXYCalForPlateCrane(JDTORecord recPara, JDTORecord recResult) throws JDTOException {

    	String szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
    	String szLogMsg 			= "";
    	String szMethodName			= "procXYCalForPlateCrane";
		String szOperationName		= "2후판정정야드 XY좌표계산";
		int intRtnVal				= -100;

		JDTORecordSet	rsResult	= null;
		JDTORecord		recInPara	= null;
		JDTORecord		recOutPara	= null;
		String szYD_CRN_SCH_ID		= null;
		String szYdUpStkColGp		= null;
		String szYdUpStkBedNo		= null;
		String szYdDnStkColGp		= null;
		String szYdDnStkBedNo		= null;
		String szYdEqpId			= null;
		String szYdMtlW				= null;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(recPara, "F");  			// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                    	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
				
		JPlateYdCrnWrkMtlDAO ydCrnWrkMtlDao	= new JPlateYdCrnWrkMtlDAO();

		//--------------------------------------- 파라미터 ------------------------------------------------------------
		szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_SCH_ID"	);		// 크레인스케줄ID
		szYdEqpId 		= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_ID"		);		// 크레인설비ID
		szYdUpStkColGp 	= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_STK_COL_GP"	);		// 권상위치 적치열
		szYdUpStkBedNo 	= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_STK_BED_NO"	);		// 권상위치 적치베드
		szYdDnStkColGp 	= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_STK_COL_GP"	);		// 권하위치 적치열
		szYdDnStkBedNo 	= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_STK_BED_NO"	);		// 권하위치 적치베드
		//------------------------------------------------------------------------------------------------------------

		//------------------------------------------------------------------------------------------------------------
		//1. 크레인작업재료중에서 길이가 제일 긴 재료 구하기
		//------------------------------------------------------------------------------------------------------------
		String szStlNo = "";
		rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
		recInPara 	= JDTORecordFactory.getInstance().create();
		recInPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recInPara에 logId 추가 
		recInPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
				
		intRtnVal 	= ydCrnWrkMtlDao.getSortStlLengthDesc(recInPara, rsResult);			// intGp == 16

		if (intRtnVal <= 0) {
			szLogMsg = "["+szOperationName+":"+szMethodName+"] 크레인작업재료의 길이가 제일 긴 재료 조회 시 execution failed - 반환값 : " + intRtnVal;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			
			return JPlateYdConst.RETN_CD_SUCCESS;
		}

		rsResult.first();
		recInPara = rsResult.getRecord();

		szStlNo = ydDaoUtils.paraRecChkNull(recInPara, "STL_NO");

		szLogMsg = "["+szOperationName+":"+szMethodName+"] 크레인작업재료의 길이가 제일 긴 재료["+szStlNo+"] 조회 성공";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		//------------------------------------------------------------------------------------------------------------

		//------------------------------------------------------------------------------------------------------------
		//2. 크레인작업재료중에서 폭이 제일 넓은 재료조회
		//------------------------------------------------------------------------------------------------------------
		rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
		recInPara 	= JDTORecordFactory.getInstance().create();
		recInPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recInPara에 logId 추가 
		recInPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
						
		intRtnVal 	= ydCrnWrkMtlDao.getSortStlWidthDesc(recInPara, rsResult);			// intGp == 20

		if (intRtnVal <= 0) {
			szLogMsg = "["+szOperationName+":"+szMethodName+"] 크레인작업재료의 폭이 제일 넓은 재료 조회 시 execution failed - 반환값 : " + intRtnVal;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			return JPlateYdConst.RETN_CD_SUCCESS;
		}

		rsResult.first();
		recInPara = rsResult.getRecord();

		szYdMtlW = ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W");

		szLogMsg = "["+szOperationName+":"+szMethodName+"] 크레인작업재료의 폭이 제일 넓은 재료["+ydDaoUtils.paraRecChkNull(recInPara, "STL_NO")+"] 조회 성공";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//------------------------------------------------------------------------------------------------------------
		// X, Y좌표 계산.
		//------------------------------------------------------------------------------------------------------------
		recInPara  = JDTORecordFactory.getInstance().create();
		recOutPara = JDTORecordFactory.getInstance().create();

		recInPara.setField("STL_NO", 				szStlNo			);
		recInPara.setField("YD_UP_STK_COL_GP", 		szYdUpStkColGp	);
		recInPara.setField("YD_UP_STK_BED_NO", 		szYdUpStkBedNo	);
		recInPara.setField("YD_DN_STK_COL_GP", 		szYdDnStkColGp	);
		recInPara.setField("YD_DN_STK_BED_NO", 		szYdDnStkBedNo	);
		recInPara.setField("YD_EQP_ID", 			szYdEqpId		);
		recInPara.setField("YD_MTL_W", 				szYdMtlW		);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recInPara에 logId 추가 
		recInPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
				
		szRtnMsg = calPlateCraneXY(recInPara, recOutPara);

		String szCraneGrabUseGp = ydDaoUtils.paraRecChkNull(recOutPara, "CRANE_GRAB_USE_GP"	);
		String szUpGrabXValue   = ydDaoUtils.paraRecChkNull(recOutPara, "UP_GRAB_X_VALUE"	);
		String szUpGrabYValue   = ydDaoUtils.paraRecChkNull(recOutPara, "UP_GRAB_Y_VALUE"	);
		String szUpGrabY1Addr   = ydDaoUtils.paraRecChkNull(recOutPara, "UP_GRAB_Y1_ADDR"	);
		String szUpGrabY1Value  = ydDaoUtils.paraRecChkNull(recOutPara, "UP_GRAB_Y1_VALUE"	);
		String szUpGrabY2Addr   = ydDaoUtils.paraRecChkNull(recOutPara, "UP_GRAB_Y2_ADDR"	);
		String szUpGrabY2Value  = ydDaoUtils.paraRecChkNull(recOutPara, "UP_GRAB_Y2_VALUE"	);

		String szDnGrabXValue   = ydDaoUtils.paraRecChkNull(recOutPara, "DN_GRAB_X_VALUE"	);
		String szDnGrabYValue   = ydDaoUtils.paraRecChkNull(recOutPara, "DN_GRAB_Y_VALUE"	);
		String szDnGrabY1Addr   = ydDaoUtils.paraRecChkNull(recOutPara, "DN_GRAB_Y1_ADDR"	);
		String szDnGrabY1Value  = ydDaoUtils.paraRecChkNull(recOutPara, "DN_GRAB_Y1_VALUE"	);
		String szDnGrabY2Addr   = ydDaoUtils.paraRecChkNull(recOutPara, "DN_GRAB_Y2_ADDR"	);
		String szDnGrabY2Value	= ydDaoUtils.paraRecChkNull(recOutPara, "DN_GRAB_Y2_VALUE"	);

		//------------------------------------------------------------------------------------------------------------
		// 크레인 Grab 구분 [D : 1Grab]
		// 1Grab Crane : Y좌표만 설정 Y1, Y2는 0으로 설정
		// --> 2013.05.02 김현우 수정
		// 1Grab Crane : Y, Y1좌표  설정  Y2는 0으로 설정
		//------------------------------------------------------------------------------------------------------------
		recResult.setField("CRANE_GRAB_USE_GP", 	szCraneGrabUseGp	);
		recResult.setField("UP_GRAB_X_VALUE", 		szUpGrabXValue	);
		recResult.setField("UP_GRAB_Y_VALUE", 		szUpGrabYValue	);
		recResult.setField("UP_GRAB_Y1_ADDR", 		szUpGrabY1Addr	);
		recResult.setField("UP_GRAB_Y1_VALUE", 		szUpGrabY1Value	);
		recResult.setField("UP_GRAB_Y2_ADDR", 		szUpGrabY2Addr	);
		recResult.setField("UP_GRAB_Y2_VALUE", 		szUpGrabY2Value	);
		recResult.setField("DN_GRAB_X_VALUE", 		szDnGrabXValue	);
		recResult.setField("DN_GRAB_Y_VALUE", 		szDnGrabYValue	);
		recResult.setField("DN_GRAB_Y1_ADDR", 		szDnGrabY1Addr	);
		recResult.setField("DN_GRAB_Y1_VALUE", 		szDnGrabY1Value	);
		recResult.setField("DN_GRAB_Y2_ADDR", 		szDnGrabY2Addr	);
		recResult.setField("DN_GRAB_Y2_VALUE", 		szDnGrabY2Value	);

		szLogMsg	= "["+szOperationName+"] -------------------------- OUT -------------------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		ydUtils.displayRecord(szOperationName, recResult);
		szLogMsg	= "["+szOperationName+"] --------------------------------------------------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		return szRtnMsg;
    }

    /**
     * 오퍼레이션명 : 2후판정정야드 크레인 작업지시에 내려질 Y1, Y2 좌표 계산
     *
     * @param  ● szStlNo          : 제품번호			- 크레인스케줄작업재료들중에서 제일 긴 재료번호
     *           szYdStkColGp     : 야드적치열구분		- 베드정보 조회 시 사용
     * 			 szYdStkBedNo     : 야드적치Bed번호	- 베드정보 조회 시 사용
     * 			 szYdEqpId        : 야드설비ID		- 빔길이 조회 시 사용
     * 			 szYdCrnGrabGp    : 크레인 Grab 구분	- D : 1Grab Crane
     *
     *           결과값
     *           JDTORecord resResult.CRANE_GRAB_USE_GP : Grab 사용 구분
     *           JDTORecord resResult.GRAB_Y1_ADDR      : Grab Y1 Address 코드
     *           JDTORecord resResult.GRAB_Y1_VALUE     : Grab Y1 좌표값
     *           JDTORecord resResult.GRAB_Y2_ADDR      : Grab Y2 Address 코드
     *           JDTORecord resResult.GRAB_Y2_VALUE     : Grab Y2 좌표값
     * @return ● False, True
     * @throws ● JDTOException
     */
    public static boolean calPlateCraneXY(String szStlNo,
    		                              String szYdStkColGp,
    		                              String szYdStkBedNo,
    		                              String szYdEqpId,
    		                              JDTORecord recResult) throws JDTOException {

    	// DAO 객체 생성
    	JPlateYdCrnSpecDAO	ydCrnSpecDao  	= new JPlateYdCrnSpecDAO();
    	JPlateYdStkBedDAO   ydStkBedDao    	= new JPlateYdStkBedDAO();
    	JPlateYdStockDAO    ydStockDao     	= new JPlateYdStockDAO();

    	// Method 선언
    	String szMethodName          	= "calPlateCraneXY";
    	String szOperationName			= "좌표계산(2후판정정야드): 사용안함";

    	// 레코드 선언
    	JDTORecord    recPara        	= null;
    	JDTORecordSet outRecSet      	= null;
    	JDTORecord    recCrnSpec     	= null;
    	JDTORecord    recStrBed       	= null;

    	String szYdCrnGrabGp			= "";
    	String szYdStkBedLGp			= null;

    	// 변수 선언
    	int    intRtnVal                = 0;
		double dTmpX 					= 0.0d;
		int    iTmpY 					= 0;

		int    iTmpYdMtlL         		= 0;
		int    iTmpYdStkBedYaxis 		= 0;
//		int    iTmpYdCrnTongL    		= 0;

		//---------------------------------------------------------------------------------------------------------
		//	1Grab Type 변수 정의
		//---------------------------------------------------------------------------------------------------------
		int iTmpD1 						= 0;
		int iTmpD2 						= 0;
		int iTmpD3 						= 0;
		int iTmpD4 						= 0;
		int iTmpD5 						= 0;
		int iTmpD6 						= 0;
		int iTmpD7 						= 0;
		int iTmpD8 						= 0;
		int iTmpDX 						= 0;

		//---------------------------------------------------------------------------------------------------------
    	String szMsg                 	= "";
    	double dblYdMtlW				= 0;
    	String szYdMtlL               	= "";
    	String szYdCrnTongL     	  	= "";
    	int    intYdStkBedXaxis 		= 0;
    	String szYdStkBedYaxis 			= "";
		String szCraneGrabUseGp 		= "";
		String szGrabXValue     		= "";
		String szGrabYValue     		= "";
		String szGrabY1Addr      		= "";
		String szGrabY1Value     		= "";
		String szGrabY2Addr      		= "";
		String szGrabY2Value     		= "";

    	try {
    		szMsg = "["+szOperationName+"] 메소드 시작 - 파라미터 확인 " ;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "["+szOperationName+"] 재료번호 - " + szStlNo;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "["+szOperationName+"] 적치열구분 - " + szYdStkColGp + ", 베드번호 - " + szYdStkBedNo;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "["+szOperationName+"] 크레인설비ID - " + szYdEqpId;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if ("1".equals(szYdEqpId.substring(5))) {
				szYdCrnGrabGp = "E";
			} else {
				szYdCrnGrabGp = "D";
			}

			szMsg = "["+szOperationName+"] 크레인 Grab 구분[D : 1Grab Crane, E : 2Grab Crane] - " + szYdCrnGrabGp;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
    		//---------------------------------------------------------------------------------------------------------
    		// 저장품 조회
    		// 제품길이 (YD_MTL_L) 추출
    		//---------------------------------------------------------------------------------------------------------
			szMsg = "["+szOperationName+"] 재료번호[" + szStlNo + "]로 저장품 조회 시작";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    		recPara = JDTORecordFactory.getInstance().create();
    		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");

    		recPara.setField("STL_NO", szStlNo);
    		intRtnVal = ydStockDao.getYdStock(recPara, outRecSet);		// intGp == 0
			if (intRtnVal < 0) {
				szMsg = "["+szOperationName+" - 오류발생]: 저장품조회  중 오류 [" + intRtnVal + "]" ;
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return false;
			} else if (intRtnVal == 0) {
				szMsg = "["+szOperationName+" - 오류발생]: 저장품조회  중 건수가 없음 [" + intRtnVal + "]" ;
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return false;
			}

			outRecSet.first();
    		recCrnSpec = outRecSet.getRecord();

    		dblYdMtlW = ydDaoUtils.paraRecChkNullDouble(recCrnSpec, "YD_MTL_W"); 			// 제품폭
    		szYdMtlL = ydDaoUtils.paraRecChkNull(recCrnSpec, "YD_MTL_L"); 			// 제품길이
    		szYdStkBedLGp = ydDaoUtils.paraRecChkNull(recCrnSpec, "YD_MTL_L_GP"); 	// 베드의 길이구분은 제품의 길이구분으로 대체

    		szMsg = "["+szOperationName+"] 재료번호[" + szStlNo + "] - 제품폭["+dblYdMtlW+"], 제품길이["+szYdMtlL+"], 제품의길이구분["+szYdStkBedLGp+"]";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    		if (!"".equals(szYdStkBedLGp)) {
    			szYdStkBedLGp = szYdStkBedLGp.substring(0, 1);
    			szMsg = "["+szOperationName+"] 재료번호[" + szStlNo + "]의 제품의길이구분["+szYdStkBedLGp+"]존재하므로 베드의 길이구분자로 대체 사용 가능";
    			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
    		}
    		//---------------------------------------------------------------------------------------------------------

    		//---------------------------------------------------------------------------------------------------------
    		// 크레인사양 조회
    		// 설비ID로  크레인 Beam 길이(YD_CRN_TONG_L) 추출
    		//---------------------------------------------------------------------------------------------------------
    		szMsg = "["+szOperationName+"] 크레인설비ID[" + szYdEqpId + "]로 크레인사양 조회 시작";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    		recPara = JDTORecordFactory.getInstance().create();
    		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");

    		recPara.setField("YD_EQP_ID", szYdEqpId);
    		intRtnVal = ydCrnSpecDao.getYdCrnSpec(recPara, outRecSet);		// intGp == 0
			if (intRtnVal < 0) {
				szMsg = "["+szOperationName+" - 오류발생]: 크레인사양조회  중 오류 [" + intRtnVal + "]" ;
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return false;
			} else if (intRtnVal == 0) {
				szMsg = "["+szOperationName+" - 오류발생]: 크레인사양조회  중 건수가 없음 [" + intRtnVal + "]" ;
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return false;
			}

			outRecSet.first();
    		recCrnSpec = outRecSet.getRecord();

    		szYdCrnTongL = ydDaoUtils.paraRecChkNull(recCrnSpec, "YD_CRN_TONG_L"); // 크레인 Beam 길이

    		szMsg = "["+szOperationName+"] 크레인설비ID[" + szYdEqpId + "]의 크레인Beam길이[YD_CRN_TONG_L:"+szYdCrnTongL+"]";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
    		//---------------------------------------------------------------------------------------------------------

    		//---------------------------------------------------------------------------------------------------------
    		// 적치Bed 조회
    		// 야드적치열구분, 야드적치Bed번호로  야드적치BedY축(YD_STK_BED_YAXIS) 추출
    		//---------------------------------------------------------------------------------------------------------
			szMsg = "["+szOperationName+"] 적치열구분[" + szYdStkColGp + "], 베드번호[" + szYdStkBedNo + "]로 베드 조회 시작";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    		recPara = JDTORecordFactory.getInstance().create();
    		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");

    		recPara.setField("YD_STK_COL_GP", szYdStkColGp);
    		recPara.setField("YD_STK_BED_NO", szYdStkBedNo);
    		intRtnVal = ydStkBedDao.getYdStkbed(recPara, outRecSet);		// intGp == 0
			if (intRtnVal < 0) {
				szMsg = "["+szOperationName+" - 오류발생]: 적치Bed 조회  중 오류 [" + intRtnVal + "]" ;
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return false;
			} else if (intRtnVal == 0) {
				szMsg = "["+szOperationName+" - 오류발생]: 적치Bed 조회  중 건수가 없음 [" + intRtnVal + "]" ;
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return false;
			}

			outRecSet.first();
			recStrBed = outRecSet.getRecord();

			intYdStkBedXaxis = ydDaoUtils.paraRecChkNullInt(recStrBed, "YD_STK_BED_XAXIS"); 	// 야드적치BedY축
    		szYdStkBedYaxis  = ydDaoUtils.paraRecChkNull(recStrBed, "YD_STK_BED_YAXIS"); 		// 야드적치BedY축
   			szYdStkBedLGp    = ydDaoUtils.paraRecChkNull(recStrBed, "YD_STK_BED_L_GP"); 		//야드적치Bed길이구분

    		szMsg = "["+szOperationName+"] 적치열구분[" + szYdStkColGp + "], 베드번호[" + szYdStkBedNo + "]의  야드적치BED X축["+intYdStkBedXaxis+"], 야드적치BED Y축["+szYdStkBedYaxis+"], 야드적치Bed길이구분["+szYdStkBedLGp+"] 조회 완료";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    		//---------------------------------------------------------------------------------------------------------
    		// 계산
    		//---------------------------------------------------------------------------------------------------------
    		// 크레인 Grab 구분      szYdCrnGrabGp
    		// 야드적치Bed번호        szYdStkBedNo
    		// 야드적치Bed길이구분 szYdStkBedLGp
    		// 제품길이                     szYdMtlL
    		// 야드설비작업최대길이 szYD_EQP_WRK_MAX_L
    		// Bed Y-Address    szYdStkBedYaxis
    		// 크레인 Beam 길이       szYdCrnTongL
    		//---------------------------------------------------------------------------------------------------------
    		if ("".equals(szYdMtlL.trim())) {
    			szYdMtlL = "0";
    		}
   		    iTmpYdMtlL = Integer.parseInt(szYdMtlL);             		// 크레인스케줄작업재료들중에서 제일 긴 재료의 제품길이

    		if ("".equals(szYdStkBedYaxis)) {
    			szYdStkBedYaxis = "0";
    		}
   		    iTmpYdStkBedYaxis = Integer.parseInt(szYdStkBedYaxis);     	// Bed Y-Address

    		if ("".equals(szYdCrnTongL)) {
    			szYdCrnTongL = "0";
    		}
//    		iTmpYdCrnTongL = Integer.parseInt(szYdCrnTongL);       		// 크레인 Beam 길이

    		//----------------------------------------------------------------------------------------------
			//	1 Grab Type Crane
			//----------------------------------------------------------------------------------------------
    		if ("D".equals(szYdCrnGrabGp)) {

    			//---------------------------------------------------------------------------------------------------------
	    		//	Y좌표값 계산식
	    		//---------------------------------------------------------------------------------------------------------

    			/*
	    		 * D1 계산 로직
	    		 * Bed Y_기준점+ 6,490
	    		 */
    			iTmpD1	= iTmpYdStkBedYaxis + 6490;

    			/*
	    		 * D2 계산 로직
	    		 * Bed Y_기준점+ 8,790
	    		 */
    			iTmpD2	= iTmpYdStkBedYaxis + 8790;

    			/*
	    		 * D3 계산 로직
	    		 * 10,600
	    		 */
    			iTmpD3	= 10600;

    			/*
	    		 * D4 계산 로직
	    		 * Bed Y_기준점 + (제품길이 / 2)
	    		 */
    			iTmpD4	= iTmpYdStkBedYaxis + (iTmpYdMtlL / 2);

    			/*
	    		 * D5 계산 로직
	    		 * 24,300
	    		 */
    			iTmpD5	= 24300;

    			/*
	    		 * D6 계산 로직
	    		 * Bed Y_기준점 + (제품길이 / 2)
	    		 */
    			iTmpD6	= iTmpYdStkBedYaxis + (iTmpYdMtlL / 2);

    			/*
	    		 * D7 계산 로직
	    		 * Bed Y_기준점 + (제품길이 / 2)
	    		 */
    			iTmpD7	= iTmpYdStkBedYaxis + (iTmpYdMtlL / 2);

    			/*
	    		 * D8 계산 로직
	    		 * Bed Y_기준점 - (제품길이 - 14,000) + (제품길이 / 2)
	    		 */
    			iTmpD8	= iTmpYdStkBedYaxis - (iTmpYdMtlL - 14000) + (iTmpYdMtlL / 2);

    			//---------------------------------------------------------------------------------------------------------

    			szCraneGrabUseGp = "1";

    			// DONG_INSERT
    			if ("RT".equals(szYdStkColGp.substring(2, 4))) {

    				//----------------------------------------------------------------------------------------------
    				//	Roller Table 상
    				//----------------------------------------------------------------------------------------------

    				if ("10".equals(szYdStkBedNo) || "30".equals(szYdStkBedNo) ||
						"50".equals(szYdStkBedNo) || "70".equals(szYdStkBedNo)) {

    					szGrabY1Addr    = "D1";
		    			szGrabY1Value   = Integer.toString(iTmpD1);
		    			szGrabY2Addr    = "DX";
		    			szGrabY2Value   = Integer.toString(iTmpDX);

    				} else if ("20".equals(szYdStkBedNo) || "40".equals(szYdStkBedNo) ||
    						   "60".equals(szYdStkBedNo) || "80".equals(szYdStkBedNo)) {

    					szGrabY1Addr    = "D2";
		    			szGrabY1Value	= Integer.toString(iTmpD2);
		    			szGrabY2Addr    = "DX";
		    			szGrabY2Value   = Integer.toString(iTmpDX);

    				}

    			} else if ("TF".equals(szYdStkColGp.substring(2, 4))) {

    				//----------------------------------------------------------------------------------------------
    				//	Transfer 상
    				//----------------------------------------------------------------------------------------------

    				if ("06".equals(szYdStkBedNo)) {

		    			szGrabY1Addr      = "D1";
		    			szGrabY1Value     = Integer.toString(iTmpD1);
		    			szGrabY2Addr      = "DX";
		    			szGrabY2Value     = Integer.toString(iTmpDX);

    				} else if ("16".equals(szYdStkBedNo)) {

    					szGrabY1Addr      = "D2";
		    			szGrabY1Value     = Integer.toString(iTmpD2);
		    			szGrabY2Addr      = "DX";
		    			szGrabY2Value     = Integer.toString(iTmpDX);

    				}

    			} else {

    				//----------------------------------------------------------------------------------------------
    				//	일반 Bed 상
    				//----------------------------------------------------------------------------------------------

    				if ("S".equals(szYdStkBedLGp)) {

	    				if ("1".equals(szYdStkBedNo)) {

	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 1번지, 단척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					szGrabY1Addr      = "D3";
	    	    			szGrabY1Value     = Integer.toString(iTmpD3);
	    	    			szGrabY2Addr      = "DX";
	    	    			szGrabY2Value     = Integer.toString(iTmpDX);

	    				} else if ("2".equals(szYdStkBedNo)) {

	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 2번지, 단척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					szGrabY1Addr      = "D4";
	    	    			szGrabY1Value     = Integer.toString(iTmpD4);
	    	    			szGrabY2Addr      = "DX";
	    	    			szGrabY2Value     = Integer.toString(iTmpDX);

	    				} else if ("3".equals(szYdStkBedNo)) {

	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 3번지, 단척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					szGrabY1Addr      = "D5";
	    	    			szGrabY1Value     = Integer.toString(iTmpD5);
	    	    			szGrabY2Addr      = "DX";
	    	    			szGrabY2Value     = Integer.toString(iTmpDX);

	    				}

	    			} else if ("M".equals(szYdStkBedLGp)) {

	    				if ("01".equals(szYdStkBedNo)) {

	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 1번지, 중척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					szGrabY1Addr      = "D6";
	    	    			szGrabY1Value     = Integer.toString(iTmpD6);
	    	    			szGrabY2Addr      = "DX";
	    	    			szGrabY2Value     = Integer.toString(iTmpDX);

	    				} else if ("02".equals(szYdStkBedNo)) {

	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 2번지, 중척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					szGrabY1Addr      = "D7";
	    	    			szGrabY1Value     = Integer.toString(iTmpD7);
	    	    			szGrabY2Addr      = "DX";
	    	    			szGrabY2Value     = Integer.toString(iTmpDX);

	    				} else if ("03".equals(szYdStkBedNo)) {

	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 3번지, 중척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					szGrabY1Addr      = "D8";
	    	    			szGrabY1Value     = Integer.toString(iTmpD8);
	    	    			szGrabY2Addr      = "DX";
	    	    			szGrabY2Value     = Integer.toString(iTmpDX);

	    				}
	    			} else if ("L".equals(szYdStkBedLGp)) {

	    				if ("01".equals(szYdStkBedNo)) {

	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 1번지, 장척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					szGrabY1Addr      = "D6";
	    	    			szGrabY1Value     = Integer.toString(iTmpD6);
	    	    			szGrabY2Addr      = "DX";
	    	    			szGrabY2Value     = Integer.toString(iTmpDX);

	    				} else if ("02".equals(szYdStkBedNo)) {

	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 2번지, 장척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					szGrabY1Addr      = "D7";
	    	    			szGrabY1Value     = Integer.toString(iTmpD7);
	    	    			szGrabY2Addr      = "DX";
	    	    			szGrabY2Value     = Integer.toString(iTmpDX);

	    				} else if ("03".equals(szYdStkBedNo)) {

	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 3번지, 장척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					szGrabY1Addr      = "D8";
	    	    			szGrabY1Value     = Integer.toString(iTmpD8);
	    	    			szGrabY2Addr      = "DX";
	    	    			szGrabY2Value     = Integer.toString(iTmpDX);

	    				}
	    			} else if ("X".equals(szYdStkBedLGp)) {

	    				if ("01".equals(szYdStkBedNo)) {

	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 1번지, 초장척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					szGrabY1Addr      = "D6";
	    	    			szGrabY1Value     = Integer.toString(iTmpD6);
	    	    			szGrabY2Addr      = "DX";
	    	    			szGrabY2Value     = Integer.toString(iTmpDX);

	    				} else if ("02".equals(szYdStkBedNo)) {

	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 2번지, 초장척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					szGrabY1Addr      = "D7";
	    	    			szGrabY1Value     = Integer.toString(iTmpD7);
	    	    			szGrabY2Addr      = "DX";
	    	    			szGrabY2Value     = Integer.toString(iTmpDX);

	    				} else if ("03".equals(szYdStkBedNo)) {

	    					//---------------------------------------------------------------------------------
	    					//	일반Bed이고, 1Grab Type이고, 3번지, 초장척이면 Grab 1 적용
	    					//---------------------------------------------------------------------------------
	    					szGrabY1Addr      = "D8";
	    	    			szGrabY1Value     = Integer.toString(iTmpD8);
	    	    			szGrabY2Addr      = "DX";
	    	    			szGrabY2Value     = Integer.toString(iTmpDX);

	    				}
	    			}
    			}
    		}

    		//---------------------------------------------------------------------------------------------------------
    		// X, Y좌표 구하기
    		//---------------------------------------------------------------------------------------------------------
    		if ("TF".equals(szYdStkColGp.substring(2, 4))) {
    			dTmpX = intYdStkBedXaxis - (dblYdMtlW / 2);
    		} else {
    			dTmpX = intYdStkBedXaxis + (dblYdMtlW / 2);
    		}

    		iTmpY = iTmpYdStkBedYaxis + (iTmpYdMtlL / 2);

    		szGrabXValue = String.valueOf(dTmpX);
    		szGrabYValue = String.valueOf(iTmpY);

    		szMsg = "["+szOperationName+"] 소수점 포함 X축값["+szGrabXValue+"], Y축값["+szGrabYValue+"] " ;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if (!"".equals(szGrabXValue)) {
				int pointIdx = szGrabXValue.lastIndexOf(".");
				if (pointIdx >= 0) {
					szGrabXValue = szGrabXValue.substring(0, pointIdx);
				}
			}

			szGrabXValue = String.valueOf(Integer.parseInt(szGrabXValue));

			szMsg = "["+szOperationName+"] 정수변환 후 X축값["+szGrabXValue+"], Y축값["+szGrabYValue+"] " ;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    		//---------------------------------------------------------------------------------------------------------
    		recResult.setField("CRANE_GRAB_USE_GP", szCraneGrabUseGp); 		// Grab 사용 구분[1: 1Grab, 2: 2Grab, 3:양쪽Grab 사용]
    		recResult.setField("GRAB_X_VALUE",      szGrabXValue);      	// Grab X 좌표값
    		recResult.setField("GRAB_Y_VALUE",     	szGrabYValue);     		// Grab Y 좌표값
    		recResult.setField("GRAB_Y1_ADDR",      szGrabY1Addr);      	// Grab Y1 Address 코드
    		recResult.setField("GRAB_Y1_VALUE",     szGrabY1Value);     	// Grab Y1 좌표값
    		recResult.setField("GRAB_Y2_ADDR",      szGrabY2Addr);      	// Grab Y2 Address 코드
    		recResult.setField("GRAB_Y2_VALUE",     szGrabY2Value);     	// Grab Y2 좌표값
		    //---------------------------------------------------------------------------------------------------------

    		szMsg = "["+szOperationName+"] -------------------------------- Out -------------------------------- " ;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			ydUtils.displayRecord(szOperationName, recResult);

			szMsg = "["+szOperationName+"] --------------------------------------------------------------------- " ;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
    		szMsg = "["+szOperationName+"] 메소드 끝 " ;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    	} catch(Exception e) {

			szMsg = "<calPlateCraneXY> Error : "+ e.getLocalizedMessage();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return false;
    	}
    	return true;
    }

    /**
     * 좌표계산(2후판정정야드)
     * @param recInPara
     * @param recOutPara
     * @return
     * @throws JDTOException
     */
    public static String calPlateCraneXY(JDTORecord recInPara, JDTORecord recOutPara) throws JDTOException {

		JPlateYdStockDAO 	ydStockDao 		= new JPlateYdStockDAO();
		JPlateYdCrnSpecDAO	ydCrnspecDao	= new JPlateYdCrnSpecDAO();
		JPlateYdStkColDAO 	ydStkcolDao		= new JPlateYdStkColDAO();
		JPlateYdStkBedDAO 	ydStkbedDao		= new JPlateYdStkBedDAO();

    	String szOperationName		= "좌표계산(2후판정정야드)";
    	String szMethodName			= "calPlateCraneXY";
    	String szMsg				= null;
    	String szRtnMsg				= null;

    	JDTORecord		recPara		= null;
    	JDTORecord		recTemp		= null;
    	JDTORecordSet	outRecSet	= null;
    	//-------------------------------------------------------------------------------------------------
    	//	파라미터로 전달되는 항목 정의
    	//-------------------------------------------------------------------------------------------------
    	String szStlNo				= null;					//크레인작업재료들중에서 길이가 제일 긴 재료번호
    	String szYdUpStkColGp		= null;					//권상지시위치 - 적치열
    	String szYdUpStkBedNo		= null;					//권상지시위치 - 적치베드
    	String szYdDnStkColGp		= null;					//권하지시위치 - 적치열
    	String szYdDnStkBedNo		= null;					//권하지시위치 - 적치베드
    	String szYdEqpId			= null;					//크레인설비ID
    	//-------------------------------------------------------------------------------------------------

    	String szYdCrnGrabGp		= null;					//Grab Type
    	String szYdCrnTongL			= null;					//Crane Beam 길이
    	String szYdCrnGrabTp		= null;					//1,2 Grab 구분

    	String szYdMtlW				= null;					//재료 폭
//    	int    intYdMtlW			= 0;					//재료 폭
    	String szYdMtlL				= null;					//재료 길이
//    	int    intYdMtlL			= 0;					//재료 길이

    	//-------------------------------------------------------------------------------------------------
    	//	권상위치 정보
    	//-------------------------------------------------------------------------------------------------
    	String szYdUpStkColBedLTp 	= null;					//권상지시위치 - 야드적치열Bed길이Type
    	String szYdUpCarUseGp		= null;					//차량사용구분
    	String szUpTrnEqpCd			= null;					//운송장비코드
    	String szUpCarNo			= null;					//차량번호
    	String szUpCardNo			= null;					//카드번호

    	int    intYdUpStkBedXaxis	= 0;					//권상지시위치베드 - 야드적치BedX축
    	int    intYdUpStkBedYaxis	= 0;					//권상지시위치베드 - 야드적치BedY축
    	String szYdUpStkBedLGp		= null;					//권상지시위치베드 - 야드적치Bed길이구분
//    	String szYdUpStkBedWGp 		= null;					//권상지시위치베드 - 야드적치Bed폭구분
//      String szYdUpStkBedWhioStat = null;					//권상지시위치베드 - 야드적치Bed상태구분
        String szYdUpStkBedWMax		= null;
//    	int    intYdUpStkBedWMax	= 0;
//    	int    intYdUpStkBedLMax	= 0;
    	//-------------------------------------------------------------------------------------------------

    	//-------------------------------------------------------------------------------------------------
    	//	권하위치 정보
    	//-------------------------------------------------------------------------------------------------
    	String szYdDnStkColBedLTp 	= null;					//권하지시위치 - 야드적치열Bed길이Type
    	String szYdDnCarUseGp		= null;					//차량사용구분
    	String szDnTrnEqpCd			= null;					//운송장비코드
    	String szDnCarNo			= null;					//차량번호
    	String szDnCardNo			= null;					//카드번호

    	int    intYdDnStkBedXaxis	= 0;					//권하지시위치베드 - 야드적치BedX축
    	int    intYdDnStkBedYaxis	= 0;					//권하지시위치베드 - 야드적치BedY축
    	String szYdDnStkBedLGp		= null;					//권하지시위치베드 - 야드적치Bed길이구분
//    	String szYdDnStkBedWGp 		= null;					//권하지시위치베드 - 야드적치Bed폭구분
//    	String szYdDnStkBedWhioStat	= null;					//권하지시위치베드 - 야드적치Bed상태구분

    	String szYdDnStkBedWMax		= null;
//    	int    intYdDnStkBedWMax	= 0;
//    	int    intYdDnStkBedLMax	= 0;
    	//-------------------------------------------------------------------------------------------------

    	String szCraneGrabUseGp 	= null;
		String szUpGrabXValue     	= null;
		String szUpGrabYValue     	= null;
		int    intUpGrabY1Value     = 0;
		int    intUpGrabY2Value     = 0;

		String szDnGrabXValue     	= null;
		String szDnGrabYValue     	= null;
		int    intDnGrabY1Value     = 0;
		int    intDnGrabY2Value     = 0;
		int    intRtnVal 			= 0;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(recInPara, "F");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

    	szMsg = "[" + szOperationName + "] -------------------------- 메소드 시작 - 파라미터 확인 --------------------------" ;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		szStlNo			= ydDaoUtils.paraRecChkNull(recInPara, "STL_NO"				);		// 크레인작업재료들중에서 길이가 제일 긴 재료번호
		szYdUpStkColGp 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_STK_COL_GP"	);		// 권상지시위치 - 적치열
		szYdUpStkBedNo	= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_STK_BED_NO"	);		// 권상지시위치 - 적치베드
		szYdDnStkColGp	= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_STK_COL_GP"	);		// 권하지시위치 - 적치열
		szYdDnStkBedNo	= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_STK_BED_NO"	);		// 권하지시위치 - 적치베드
		szYdEqpId		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID"			);		// 크레인설비ID
		szYdMtlW 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W"			); 		// 크레인작업재료들중에서 폭이 제일 넓은 재료의 폭 값

		szMsg = "[" + szOperationName + "] 크레인작업재료들중에서 길이가 제일 긴 재료번호 - " + szStlNo;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		szMsg = "[" + szOperationName + "] 권상지시위치[적치열구분 - " + szYdUpStkColGp + ", 베드번호 - " + szYdUpStkBedNo + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		szMsg = "[" + szOperationName + "] 권하지시위치[적치열구분 - " + szYdDnStkColGp + ", 베드번호 - " + szYdDnStkBedNo + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		szMsg = "[" + szOperationName + "] 크레인설비ID - " + szYdEqpId;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		//---------------------------------------------------------------------------------------------------------
		// 저장품 조회
		// 제품길이 (YD_MTL_L) 추출
		//---------------------------------------------------------------------------------------------------------
		szMsg = "[" + szOperationName + "] 재료번호[" + szStlNo + "]로 저장품 조회 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		outRecSet	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("STL_NO", szStlNo);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recPara에 logId 추가 
		recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
				
		intRtnVal = ydStockDao.getYdStock(recPara, outRecSet);		// intGp == 0

		if (intRtnVal <= 0) {
			szMsg = "[" + szOperationName + "] 재료번호[" + szStlNo + "]로 저장품 조회 시 오류발생 - 메세지 : " + Integer.toString(intRtnVal);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			
			return szRtnMsg;
		}

		outRecSet.first();
		recTemp  = outRecSet.getRecord();

		szYdMtlL = ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L"); 			// 제품길이

		int pointIdx = szYdMtlW.lastIndexOf(".");

		if (pointIdx >= 0) {
			szYdMtlW = szYdMtlW.substring(0, pointIdx);
		}

//		intYdMtlW = Integer.parseInt(szYdMtlW);
//		intYdMtlL = Integer.parseInt(szYdMtlL);

		szMsg = "[" + szOperationName + "] 크레인작업재료들중에서 길이가 제일 긴 재료번호[" + szStlNo + "] - 제품길이[" + szYdMtlL + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		szMsg = "[" + szOperationName + "] 크레인작업재료들중에서 폭이 제일 넓은 재료의 제품폭[" + szYdMtlW + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		//---------------------------------------------------------------------------------------------------------
		// 크레인사양 조회
		// 설비ID로  크레인 Beam 길이(YD_CRN_TONG_L) 추출
		//---------------------------------------------------------------------------------------------------------
		szMsg = "[" + szOperationName + "] 크레인설비ID[" + szYdEqpId + "]로 크레인사양 조회 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		recPara   = JDTORecordFactory.getInstance().create();
		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
		recPara.setField("YD_EQP_ID", szYdEqpId);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recPara에 logId 추가 
		recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
				
		intRtnVal = ydCrnspecDao.getYdCrnSpec(recPara, outRecSet);		// intGp == 0

		if (intRtnVal <= 0) {
			szMsg = "[" + szOperationName + "] 크레인설비ID[" + szYdEqpId + "]로 크레인사양 조회 시 오류발생 - 메세지 : " + Integer.toString(intRtnVal);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			
			return szRtnMsg;
		}

		outRecSet.first();
		recTemp = outRecSet.getRecord();

		szYdCrnTongL = ydDaoUtils.paraRecChkNull(recTemp, "YD_CRN_TONG_L"); // 크레인 Beam 길이

		szMsg = "[" + szOperationName + "] 크레인설비ID[" + szYdEqpId + "]의 크레인Beam길이[YD_CRN_TONG_L:" + szYdCrnTongL + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		// 2후판 정정야드는 1Grab 크레인임
		szYdCrnGrabTp = "P";

		if ("X".equals(szYdCrnGrabTp)) {
			szYdCrnGrabGp = "E";
		} else {
			szYdCrnGrabGp = "D";
		}

		szMsg = "[" + szOperationName + "] 크레인 Grab 구분[D : 1Grab Crane, E : 2Grab Crane] - " + szYdCrnGrabGp;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		//---------------------------------------------------------------------------------------------------------
		// 권상지시위치 - 적치열조회 : 야드적치열Bed길이Type, 차량정보가 존재하면 차량정보 조회
		//---------------------------------------------------------------------------------------------------------
		szMsg = "[" + szOperationName + "] 권상지시위치[적치열구분:" + szYdUpStkColGp + "]로 적치열 조회 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
		recPara   = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", szYdUpStkColGp);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recPara에 logId 추가 
		recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
				
		intRtnVal = ydStkcolDao.getYdStkcol(recPara, outRecSet);		// intGp == 0

		if (intRtnVal <= 0) {
			szMsg = "[" + szOperationName + "] 권상지시위치[적치열구분:" + szYdUpStkColGp + "]로 적치열 조회 시 오류발생 - 메세지 : " + Integer.toString(intRtnVal);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			
			return szRtnMsg;
		}

		outRecSet.first();
		recTemp = outRecSet.getRecord();

		szYdUpStkColBedLTp 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_BED_L_TP"	); 		// 야드적치열Bed길이Type
		szYdUpCarUseGp		= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_USE_GP"		); 		// 차량사용구분
		szUpTrnEqpCd		= ydDaoUtils.paraRecChkNull(recTemp, "TRN_EQP_CD"			); 		// 운송장비코드
		szUpCarNo			= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO"				); 		// 차량번호
		szUpCardNo			= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO"				); 		// 카드번호

		szMsg = "[" + szOperationName + "] 권상지시위치[적치열구분:" + szYdUpStkColGp + "]로 적치열 조회 완료 - 야드적치열Bed길이Type[" + szYdUpStkColBedLTp + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		szMsg = "[" + szOperationName + "] 권상지시위치[적치열구분:" + szYdUpStkColGp + "] - 차량사용구분[" + szYdUpCarUseGp + "], 운송장비코드[" + szUpTrnEqpCd + "], 차량번호[" + szUpCarNo + "], 카드번호[" + szUpCardNo + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		//---------------------------------------------------------------------------------------------------------
		// 권하지시위치 - 적치열조회 : 야드적치열Bed길이Type, 차량정보가 존재하면 차량정보 조회
		//---------------------------------------------------------------------------------------------------------
		if ("XX".equals(ydUtils.substr(szYdDnStkColGp, 0, 2))) {

			szMsg	= "[" + szOperationName + "] 권하지시 위치 XX 일때 차량정보 조회 SKIP 처리 >>>>" + szYdDnStkColGp;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			szYdDnStkColBedLTp	= ""; 				// 야드적치열Bed길이Type
			szYdDnCarUseGp		= ""; 				// 차량사용구분
			szDnTrnEqpCd		= ""; 				// 운송장비코드
			szDnCarNo			= ""; 				// 차량번호
			szDnCardNo			= ""; 				// 카드번호

		} else {

			szMsg = "[" + szOperationName + "] 권하지시위치[적치열구분:" + szYdDnStkColGp + "]로 적치열 조회 시작";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdDnStkColGp);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recPara에 logId 추가 
			recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
					
			intRtnVal = ydStkcolDao.getYdStkcol(recPara, outRecSet);	// intGp == 0

			if (intRtnVal <= 0) {
				szMsg = "[" + szOperationName + "] 권하지시위치[적치열구분:" + szYdDnStkColGp + "]로 적치열 조회 시 오류발생 - 메세지 : " + Integer.toString(intRtnVal);
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szRtnMsg;
			}

			outRecSet.first();
			recTemp = outRecSet.getRecord();

			szYdDnStkColBedLTp	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_BED_L_TP"	); 		// 야드적치열Bed길이Type
			szYdDnCarUseGp		= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_USE_GP"		); 		// 차량사용구분
			szDnTrnEqpCd		= ydDaoUtils.paraRecChkNull(recTemp, "TRN_EQP_CD"			); 		// 운송장비코드
			szDnCarNo			= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO"				); 		// 차량번호
			szDnCardNo			= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO"				); 		// 카드번호

			szMsg = "[" + szOperationName + "] 권하지시위치[적치열구분:" + szYdDnStkColGp + "]로 적치열 조회 완료 - 야드적치열Bed길이Type[" + szYdDnStkColBedLTp + "]";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			szMsg = "[" + szOperationName + "] 권하지시위치[적치열구분:" + szYdDnStkColGp + "] - 차량사용구분[" + szYdDnCarUseGp + "], 운송장비코드[" + szDnTrnEqpCd + "], 차량번호[" + szDnCarNo + "], 카드번호[" + szDnCardNo + "]";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		}

		//---------------------------------------------------------------------------------------------------------
		// 권상지시위치 - 적치Bed 조회
		// 야드적치열구분, 야드적치Bed번호로  야드적치BedY축(YD_STK_BED_YAXIS) 추출
		//---------------------------------------------------------------------------------------------------------
		szMsg = "[" + szOperationName + "] 권상지시위치[적치열구분:" + szYdUpStkColGp + ", 베드번호:" + szYdUpStkBedNo + "]로 베드 조회 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
		recPara   = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", szYdUpStkColGp);
		recPara.setField("YD_STK_BED_NO", szYdUpStkBedNo);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recPara에 logId 추가 
		recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
				
		intRtnVal = ydStkbedDao.getYdStkbed(recPara, outRecSet);		// intGp == 0

		if (intRtnVal <= 0) {
			szMsg = "[" + szOperationName + "] 권상지시위치[적치열구분:" + szYdUpStkColGp + ", 베드번호:" + szYdUpStkBedNo + "]로 베드 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			
			return szRtnMsg;
		}

		outRecSet.first();
		recTemp = outRecSet.getRecord();

		intYdUpStkBedXaxis	 	= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_XAXIS"		); 		// 야드적치BedX축
		intYdUpStkBedYaxis 		= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_YAXIS"		); 		// 야드적치BedY축
		szYdUpStkBedLGp 		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_L_GP"			); 		// 야드적치Bed길이구분
//		szYdUpStkBedWGp 		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_GP"); 			// 야드적치Bed폭구분
//		szYdUpStkBedWhioStat	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT"); 		// 야드적치Bed상태구분
		szYdUpStkBedWMax		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_MAX"			); 		// 야드적치Bed폭Max
//		intYdUpStkBedLMax		= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_L_MAX"); 		// 야드적치Bed길이Max

		szMsg = "[" + szOperationName + "] szYdUpStkBedWMax Before >>>> " + szYdUpStkBedWMax;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		if ("".equals(szYdUpStkBedWMax)) {
			szYdUpStkBedWMax = "0";
		}

		pointIdx = szYdUpStkBedWMax.lastIndexOf(".");

		if (pointIdx >= 0) {
			szYdUpStkBedWMax = szYdUpStkBedWMax.substring(0, pointIdx);
		}

		szMsg = "[" + szOperationName + "] szYdUpStkBedWMax After >>>> " + szYdUpStkBedWMax;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

//		intYdUpStkBedWMax = Integer.parseInt(szYdUpStkBedWMax);

		szMsg = "[" + szOperationName + "] 권상지시위치[적치열구분:" + szYdUpStkColGp + ", 베드번호:" + szYdUpStkBedNo + "]의  야드적치BED X축[" + intYdUpStkBedXaxis + "], 야드적치BED Y축[" + intYdUpStkBedYaxis + "], 야드적치Bed길이구분[" + szYdUpStkBedLGp + "] 조회 완료";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		//---------------------------------------------------------------------------------------------------------
		// 권하지시위치 - 적치Bed 조회
		// 야드적치열구분, 야드적치Bed번호로  야드적치BedY축(YD_STK_BED_YAXIS) 추출
		//---------------------------------------------------------------------------------------------------------
		if ("XX".equals(ydUtils.substr(szYdDnStkColGp, 0, 2))) {

			szMsg	= "[" + szOperationName + "] 권하지시 위치 XX 일때 적치Bed 조회 SKIP 처리 >>>>" + szYdDnStkColGp;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			intYdDnStkBedXaxis 		= 0; 			// 야드적치BedX축
			intYdDnStkBedYaxis 		= 0; 			// 야드적치BedY축
			szYdDnStkBedLGp 		= ""; 			// 야드적치Bed길이구분
//			szYdDnStkBedWGp 		= ""; 			// 야드적치Bed폭구분
//			szYdDnStkBedWhioStat	= ""; 			// 야드적치Bed상태구분
			szYdDnStkBedWMax		= ""; 			// 야드적치Bed폭Max
//			intYdDnStkBedLMax		= 0; 			// 야드적치Bed길이Max

		} else {

			szMsg = "[" + szOperationName + "] 권하지시위치[적치열구분:" + szYdDnStkColGp + ", 베드번호:" + szYdDnStkBedNo + "]로 베드 조회 시작";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdDnStkColGp);
			recPara.setField("YD_STK_BED_NO", szYdDnStkBedNo);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recPara에 logId 추가 
			recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
					
			intRtnVal = ydStkbedDao.getYdStkbed(recPara, outRecSet);			// intGp == 0
			if (intRtnVal <= 0) {
				szMsg = "[" + szOperationName + "] 권하지시위치[적치열구분:" + szYdDnStkColGp + ", 베드번호:" + szYdDnStkBedNo + "]로 베드 조회 시 오류발생 - 메세지 : " + Integer.toString(intRtnVal);
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szRtnMsg;
			}

			outRecSet.first();
			recTemp = outRecSet.getRecord();

			intYdDnStkBedXaxis 		= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_XAXIS"		); 		// 야드적치BedX축
			intYdDnStkBedYaxis 		= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_YAXIS"		); 		// 야드적치BedY축
			szYdDnStkBedLGp 		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_L_GP"			); 		// 야드적치Bed길이구분
//			szYdDnStkBedWGp 		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_GP"); 			// 야드적치Bed폭구분
//			szYdDnStkBedWhioStat	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT"); 		// 야드적치Bed상태구분
			szYdDnStkBedWMax		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_MAX"			); 		// 야드적치Bed폭Max
//			intYdDnStkBedLMax		= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_L_MAX"); 		// 야드적치Bed길이Max
		}

		szMsg = "[" + szOperationName + "] szYdDnStkBedWMax Before >>>> " + szYdDnStkBedWMax;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		if ("".equals(szYdDnStkBedWMax)) {
			szYdDnStkBedWMax = "0";
		}

		pointIdx = szYdDnStkBedWMax.lastIndexOf(".");

		if (pointIdx >= 0) {
			szYdDnStkBedWMax = szYdDnStkBedWMax.substring(0, pointIdx);
		}

		szMsg = "[" + szOperationName + "] szYdDnStkBedWMax After >>>> " + szYdDnStkBedWMax;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		szMsg = "[" + szOperationName + "] 권하지시위치[적치열구분:" + szYdDnStkColGp + ", 베드번호:" + szYdDnStkBedNo + "]의  야드적치BED X축[" + intYdDnStkBedXaxis + "], 야드적치BED Y축[" + intYdDnStkBedYaxis + "], 야드적치Bed길이구분[" + szYdDnStkBedLGp + "] 조회 완료";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		//---------------------------------------------------------------------------------------------------------
		//	X좌표 구하기
		//---------------------------------------------------------------------------------------------------------
		szUpGrabXValue = String.valueOf(intYdUpStkBedXaxis);
		szDnGrabXValue = String.valueOf(intYdDnStkBedXaxis);

		//---------------------------------------------------------------------------------------------------------
		//	Y좌표 구하기
		//---------------------------------------------------------------------------------------------------------
		szUpGrabYValue 	 = String.valueOf(intYdUpStkBedYaxis);
		szDnGrabYValue 	 = String.valueOf(intYdDnStkBedYaxis);
		intUpGrabY1Value = intYdUpStkBedYaxis;
		intUpGrabY2Value = intYdUpStkBedYaxis;
		intDnGrabY1Value = intYdDnStkBedYaxis;
		intDnGrabY2Value = intYdDnStkBedYaxis;
		szCraneGrabUseGp = "1";

	//	2후판정정야드는 Y1, Y2좌표 구하기 .. SKIP

		recOutPara.setField("CRANE_GRAB_USE_GP",	szCraneGrabUseGp					);
		recOutPara.setField("UP_GRAB_X_VALUE", 		szUpGrabXValue						);
		recOutPara.setField("UP_GRAB_Y_VALUE", 		szUpGrabYValue						);
		recOutPara.setField("UP_GRAB_Y1_VALUE", 	String.valueOf(intUpGrabY1Value)	);
		recOutPara.setField("UP_GRAB_Y2_VALUE", 	String.valueOf(intUpGrabY2Value)	);
		recOutPara.setField("DN_GRAB_X_VALUE", 		szDnGrabXValue						);
		recOutPara.setField("DN_GRAB_Y_VALUE", 		szDnGrabYValue						);
		recOutPara.setField("DN_GRAB_Y1_VALUE", 	String.valueOf(intDnGrabY1Value)	);
		recOutPara.setField("DN_GRAB_Y2_VALUE", 	String.valueOf(intDnGrabY2Value)	);
		//---------------------------------------------------------------------------------------------------------

    	return JPlateYdConst.RETN_CD_SUCCESS;
    }

    /**
     * 1 Grab Y1좌표 구하는 메소드
     * @param intYdStkBedYaxis - DB기준 Y값
     * @param intYdMtlL        - 크레인작업재료들중 제일 긴 재료의 제품길이
     * @param intYdCrnBeamL    - BEAM 길이
     * @return int
     */
    public static int procY1CalFor1GrabCrn(int intYdStkBedYaxis, int intYdMtlL, int intYdCrnBeamL) {
    	int intY1Value = 0;

    	if (intYdCrnBeamL <= intYdMtlL) {
    		intY1Value	= intYdStkBedYaxis + (intYdMtlL / 2);
    	} else {
    		intY1Value	= intYdStkBedYaxis + (intYdCrnBeamL / 2);
    	}
    	return intY1Value;
    }

    /**
     * X축값구하기(2후판정정)
     * @param intYdStkBedXaxis 		- DB기준 X축값
     * @param intYdMtlW				- 가장 긴 재료의 폭 값
     * @param intYdStkBedWMax		- 베드폭
     * @param szYdStkColGp			- 적치열구분
     * @param szYdDnStkBedWGp 		- 야드적치Bed폭구분
     * @param szYdDnStkBedWhioStat - 야드적치Bed상태구분
     * @return
     */
    public static int procXCalForPlateYd(int intYdStkBedXaxis, int intYdMtlW, int intYdStkBedWMax, String szYdStkColGp,
    		                             String szYdDnStkBedWGp, String szYdDnStkBedWhioStat) {

    	String szOperationName	= "X축값구하기(2후판정정)";
    	String szMethodName		= "procXCalForPlateYd";
    	String szLogMsg			= null;
    	int    intXValue 		= 0;

    	try {

	    	if ("G".equals(szYdDnStkBedWhioStat)) {			// 가적장
	    		if ("S".equals(szYdDnStkBedWGp)) {			// 협폭
	    			intXValue = intYdStkBedXaxis + 1050;
	    		} else if ("L".equals(szYdDnStkBedWGp)) {	// 광폭
	    			intXValue = intYdStkBedXaxis + 2400;
	    		} else {									// 중폭
	    			intXValue = intYdStkBedXaxis + 1700;
	    		}

	    		szLogMsg = "["+szOperationName+":"+szMethodName+"] 가적베드 좌표값 계산="+szYdDnStkBedWGp
	    		         + "="+Integer.toString(intYdStkBedXaxis)+"="+Integer.toString(intXValue);
	    		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

	    	} else {
		    	if ("TF".equals(szYdStkColGp.substring(2, 4))) {
		    		intXValue = intYdStkBedXaxis - (intYdMtlW / 2);
		    	} else if ("PT".equals(szYdStkColGp.substring(2, 4))) {
		    		intXValue = intYdStkBedXaxis;
		    	} else if ("GJ".equals(szYdStkColGp.substring(2, 4))) {
		    		intXValue = intYdStkBedXaxis + (intYdMtlW / 2);
		    	} else {
		    		intXValue = intYdStkBedXaxis + (intYdMtlW / 2);
		    	}
	    	}

    	} catch(Exception ex) {
    		szLogMsg = ex.getMessage();
    		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
		}

    	return intXValue;
    }

    /**
     * RT상의 베드에 대응하는 야드베드번지 반환하는 메소드
     * @param szYD_STK_BED_NO
     * @return
     */
    public static String getYdBedNoFromRtBedNo(String szYD_STK_BED_NO) {

    	String rtnBed = "";
/*
    	if ("10".equals(szYD_STK_BED_NO) || "30".equals(szYD_STK_BED_NO) ||
			"50".equals(szYD_STK_BED_NO) || "70".equals(szYD_STK_BED_NO)) {
    		rtnBed = "03";
		} else if ("90".equals(szYD_STK_BED_NO) || "B0".equals(szYD_STK_BED_NO)) {
			rtnBed = "03";
		} else if ("85".equals(szYD_STK_BED_NO) || "A5".equals(szYD_STK_BED_NO)) {
			rtnBed = "04";
		} else if ("95".equals(szYD_STK_BED_NO) || "B5".equals(szYD_STK_BED_NO)) {
			rtnBed = "02";
		} else {
			rtnBed = "01";
		}
*/
		rtnBed = "01";

    	return rtnBed;
    }

    /**
     * TF상의 베드에 대응하는 야드베드번지 반환하는 메소드
     * @param szYD_STK_BED_NO
     * @return
     */
    public static String getYdBedNoFromTfBedNo(String szYD_STK_BED_NO) {
/*
    	if ("06".equals(szYD_STK_BED_NO) || "05".equals(szYD_STK_BED_NO)) {
			return "03";
		} else {
			return "01";
		}
*/
    	return "01";
    }
    

	/**********************************************************
	* 1후판정정추가 SJH16
	**********************************************************/	    
    
    
   /**
     * 1후판정정야드 XY좌표계산
     * @param recPara
     * @param recResult
     * @return
     * @throws JDTOException
     */
    public static String procXYCalForPlateCraneYdP(JDTORecord recPara, JDTORecord recResult) throws JDTOException {

    	String szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
    	String szLogMsg 			= "";
    	String szMethodName			= "procXYCalForPlateCraneYdP";
		String szOperationName		= "1후판정정야드 XY좌표계산";
		int intRtnVal				= -100;

		JDTORecordSet	rsResult	= null;
		JDTORecord		recInPara	= null;
		JDTORecord		recOutPara	= null;
		String szYD_CRN_SCH_ID		= null;
		String szYdUpStkColGp		= null;
		String szYdUpStkBedNo		= null;
		String szYdDnStkColGp		= null;
		String szYdDnStkBedNo		= null;
		String szYdEqpId			= null;
		String szYdMtlW				= null;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(recPara, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		JPlateYdCrnWrkMtlDAO ydCrnWrkMtlDao	= new JPlateYdCrnWrkMtlDAO();

		//--------------------------------------- 파라미터 ------------------------------------------------------------
		szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_SCH_ID");			//크레인스케줄ID
		szYdEqpId 		= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_ID");				//크레인설비ID
		szYdUpStkColGp 	= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_STK_COL_GP");		//권상위치 적치열
		szYdUpStkBedNo 	= ydDaoUtils.paraRecChkNull(recPara, "YD_UP_STK_BED_NO");		//권상위치 적치베드
		szYdDnStkColGp 	= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_STK_COL_GP");		//권하위치 적치열
		szYdDnStkBedNo 	= ydDaoUtils.paraRecChkNull(recPara, "YD_DN_STK_BED_NO");		//권하위치 적치베드
		//------------------------------------------------------------------------------------------------------------

		//------------------------------------------------------------------------------------------------------------
		//1. 크레인작업재료중에서 길이가 제일 긴 재료 구하기
		//------------------------------------------------------------------------------------------------------------
		String szStlNo = "";
		rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
		recInPara 	= JDTORecordFactory.getInstance().create();
		recInPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
		/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getSortStlLengthDesc 

		SELECT *
		  FROM
		(
		    SELECT A.STL_NO
		          , A.YD_AID_WRK_YN
		          , A.YD_STK_LYR_NO
		          , A.YD_STK_LOT_TP
		          , A.YD_STK_LOT_CD
		          , A.HCR_GP
		          , A.STL_PROG_CD
		          , A.YD_MTL_ITEM
		          , A.YD_ROUTE_GP
		          , A.YD_TO_LOC_DCSN_MTD
		          , B.YD_MTL_L
		       FROM TB_YD_CRNWRKMTL A
		          , TB_YD_SHRSTOCK B
		      WHERE A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
		        AND A.STL_NO = B.STL_NO
		        AND A.DEL_YN = 'N'
		        AND B.DEL_YN = 'N'
		      ORDER BY B.YD_MTL_L DESC
		)
		WHERE ROWNUM <= 1
		*/

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 recSchPara에 logId 추가 
		recInPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
		intRtnVal 	= ydCrnWrkMtlDao.getSortStlLengthDesc(recInPara, rsResult);			// intGp == 16

		if (intRtnVal <= 0) {
			szLogMsg = "[" + szOperationName + ":" + szMethodName + "] 크레인작업재료의 길이가 제일 긴 재료 조회 시 execution failed - 반환값 : " + intRtnVal;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			return JPlateYdConst.RETN_CD_SUCCESS;
		}

		rsResult.first();
		recInPara = rsResult.getRecord();

		szStlNo = ydDaoUtils.paraRecChkNull(recInPara, "STL_NO");

		szLogMsg = "[" + szOperationName + ":" + szMethodName + "] 크레인작업재료의 길이가 제일 긴 재료[" + szStlNo + "] 조회 성공";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		//------------------------------------------------------------------------------------------------------------

		//------------------------------------------------------------------------------------------------------------
		//2. 크레인작업재료중에서 폭이 제일 넓은 재료조회
		//------------------------------------------------------------------------------------------------------------
		rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
		recInPara 	= JDTORecordFactory.getInstance().create();
		recInPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
		/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getSortStlWidthDesc

		SELECT *
		  FROM
		(
		    SELECT A.STL_NO
		          , A.YD_AID_WRK_YN
		          , A.YD_STK_LYR_NO
		          , A.YD_STK_LOT_TP
		          , A.YD_STK_LOT_CD
		          , A.HCR_GP
		          , A.STL_PROG_CD
		          , A.YD_MTL_ITEM
		          , A.YD_ROUTE_GP
		          , A.YD_TO_LOC_DCSN_MTD
		          , B.YD_MTL_W
		       FROM TB_YD_CRNWRKMTL A
		          , TB_YD_SHRSTOCK B
		      WHERE A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
		        AND A.STL_NO = B.STL_NO
		        AND A.DEL_YN = 'N'
		        AND B.DEL_YN = 'N'
		      ORDER BY B.YD_MTL_W DESC
		)
		WHERE ROWNUM <= 1
		*/

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 recSchPara에 logId 추가 
		recInPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
		
		intRtnVal 	= ydCrnWrkMtlDao.getSortStlWidthDesc(recInPara, rsResult);			// intGp == 20

		if (intRtnVal <= 0) {
			szLogMsg = "[" + szOperationName + ":" + szMethodName + "] 크레인작업재료의 폭이 제일 넓은 재료 조회 시 execution failed - 반환값 : " + intRtnVal;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			return JPlateYdConst.RETN_CD_SUCCESS;
		}

		rsResult.first();
		recInPara = rsResult.getRecord();

		szYdMtlW = ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W");

		szLogMsg = "[" + szOperationName + ":" + szMethodName + "] 크레인작업재료의 폭이 제일 넓은 재료[" + ydDaoUtils.paraRecChkNull(recInPara, "STL_NO") + "] 조회 성공";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//------------------------------------------------------------------------------------------------------------
		// X, Y좌표 계산.
		//------------------------------------------------------------------------------------------------------------
		recInPara  = JDTORecordFactory.getInstance().create();
		recOutPara = JDTORecordFactory.getInstance().create();

		recInPara.setField("STL_NO", 				szStlNo);
		recInPara.setField("YD_UP_STK_COL_GP", 		szYdUpStkColGp);
		recInPara.setField("YD_UP_STK_BED_NO", 		szYdUpStkBedNo);
		recInPara.setField("YD_DN_STK_COL_GP", 		szYdDnStkColGp);
		recInPara.setField("YD_DN_STK_BED_NO", 		szYdDnStkBedNo);
		recInPara.setField("YD_EQP_ID", 			szYdEqpId);
		recInPara.setField("YD_MTL_W", 				szYdMtlW);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 recSchPara에 logId 추가 
		recInPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
		
		szRtnMsg = calPlateCraneXYYdP(recInPara, recOutPara);

		String szCraneGrabUseGp = ydDaoUtils.paraRecChkNull(recOutPara, "CRANE_GRAB_USE_GP");
		String szUpGrabXValue   = ydDaoUtils.paraRecChkNull(recOutPara, "UP_GRAB_X_VALUE");
		String szUpGrabYValue   = ydDaoUtils.paraRecChkNull(recOutPara, "UP_GRAB_Y_VALUE");
		String szUpGrabY1Addr   = ydDaoUtils.paraRecChkNull(recOutPara, "UP_GRAB_Y1_ADDR");
		String szUpGrabY1Value  = ydDaoUtils.paraRecChkNull(recOutPara, "UP_GRAB_Y1_VALUE");
		String szUpGrabY2Addr   = ydDaoUtils.paraRecChkNull(recOutPara, "UP_GRAB_Y2_ADDR");
		String szUpGrabY2Value  = ydDaoUtils.paraRecChkNull(recOutPara, "UP_GRAB_Y2_VALUE");

		String szDnGrabXValue   = ydDaoUtils.paraRecChkNull(recOutPara, "DN_GRAB_X_VALUE");
		String szDnGrabYValue   = ydDaoUtils.paraRecChkNull(recOutPara, "DN_GRAB_Y_VALUE");
		String szDnGrabY1Addr   = ydDaoUtils.paraRecChkNull(recOutPara, "DN_GRAB_Y1_ADDR");
		String szDnGrabY1Value  = ydDaoUtils.paraRecChkNull(recOutPara, "DN_GRAB_Y1_VALUE");
		String szDnGrabY2Addr   = ydDaoUtils.paraRecChkNull(recOutPara, "DN_GRAB_Y2_ADDR");
		String szDnGrabY2Value	= ydDaoUtils.paraRecChkNull(recOutPara, "DN_GRAB_Y2_VALUE");

		//------------------------------------------------------------------------------------------------------------
		// 크레인 Grab 구분 [D : 1Grab]
		// 1Grab Crane : Y좌표만 설정 Y1, Y2는 0으로 설정
		// --> 2013.05.02 김현우 수정
		// 1Grab Crane : Y, Y1좌표  설정  Y2는 0으로 설정
		//------------------------------------------------------------------------------------------------------------
		recResult.setField("CRANE_GRAB_USE_GP", 	szCraneGrabUseGp);
		recResult.setField("UP_GRAB_X_VALUE", 		szUpGrabXValue);
		recResult.setField("UP_GRAB_Y_VALUE", 		szUpGrabYValue);
		recResult.setField("UP_GRAB_Y1_ADDR", 		szUpGrabY1Addr);
		recResult.setField("UP_GRAB_Y1_VALUE", 		szUpGrabY1Value);
		recResult.setField("UP_GRAB_Y2_ADDR", 		szUpGrabY2Addr);
		recResult.setField("UP_GRAB_Y2_VALUE", 		szUpGrabY2Value);
		recResult.setField("DN_GRAB_X_VALUE", 		szDnGrabXValue);
		recResult.setField("DN_GRAB_Y_VALUE", 		szDnGrabYValue);
		recResult.setField("DN_GRAB_Y1_ADDR", 		szDnGrabY1Addr);
		recResult.setField("DN_GRAB_Y1_VALUE", 		szDnGrabY1Value);
		recResult.setField("DN_GRAB_Y2_ADDR", 		szDnGrabY2Addr);
		recResult.setField("DN_GRAB_Y2_VALUE", 		szDnGrabY2Value);

		szLogMsg	= "[" + szOperationName + "] -------------------------- OUT -------------------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		ydUtils.displayRecord(szOperationName, recResult);
		szLogMsg	= "[" + szOperationName + "] --------------------------------------------------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		return szRtnMsg;
    }	
    
    /**
     * 좌표계산(1후판정정야드)
     * @param recInPara
     * @param recOutPara
     * @return
     * @throws JDTOException
     */
    public static String calPlateCraneXYYdP(JDTORecord recInPara, JDTORecord recOutPara) throws JDTOException {

		JPlateYdStockDAO 	ydStockDao 		= new JPlateYdStockDAO();
		JPlateYdCrnSpecDAO	ydCrnspecDao	= new JPlateYdCrnSpecDAO();
		JPlateYdStkColDAO 	ydStkcolDao		= new JPlateYdStkColDAO();
		JPlateYdStkBedDAO 	ydStkbedDao		= new JPlateYdStkBedDAO();

    	String szOperationName		= "좌표계산(1후판정정야드)";
    	String szMethodName			= "calPlateCraneXYYdP";
    	String szMsg				= null;
    	String szRtnMsg				= null;

    	JDTORecord		recPara		= null;
    	JDTORecord		recTemp		= null;
    	JDTORecordSet	outRecSet	= null;
    	//-------------------------------------------------------------------------------------------------
    	//	파라미터로 전달되는 항목 정의
    	//-------------------------------------------------------------------------------------------------
    	String szStlNo				= null;					//크레인작업재료들중에서 길이가 제일 긴 재료번호
    	String szYdUpStkColGp		= null;					//권상지시위치 - 적치열
    	String szYdUpStkBedNo		= null;					//권상지시위치 - 적치베드
    	String szYdDnStkColGp		= null;					//권하지시위치 - 적치열
    	String szYdDnStkBedNo		= null;					//권하지시위치 - 적치베드
    	String szYdEqpId			= null;					//크레인설비ID
    	//-------------------------------------------------------------------------------------------------

    	String szYdCrnGrabGp		= null;					//Grab Type
    	String szYdCrnTongL			= null;					//Crane Beam 길이
    	String szYdCrnGrabTp		= null;					//1,2 Grab 구분

    	String szYdMtlW				= null;					//재료 폭
//    	int    intYdMtlW			= 0;					//재료 폭
    	String szYdMtlL				= null;					//재료 길이
//    	int    intYdMtlL			= 0;					//재료 길이

    	//-------------------------------------------------------------------------------------------------
    	//	권상위치 정보
    	//-------------------------------------------------------------------------------------------------
    	String szYdUpStkColBedLTp 	= null;					//권상지시위치 - 야드적치열Bed길이Type
    	String szYdUpCarUseGp		= null;					//차량사용구분
    	String szUpTrnEqpCd			= null;					//운송장비코드
    	String szUpCarNo			= null;					//차량번호
    	String szUpCardNo			= null;					//카드번호

    	int    intYdUpStkBedXaxis	= 0;					//권상지시위치베드 - 야드적치BedX축
    	int    intYdUpStkBedYaxis	= 0;					//권상지시위치베드 - 야드적치BedY축
    	String szYdUpStkBedLGp		= null;					//권상지시위치베드 - 야드적치Bed길이구분
//    	String szYdUpStkBedWGp 		= null;					//권상지시위치베드 - 야드적치Bed폭구분
//      String szYdUpStkBedWhioStat = null;					//권상지시위치베드 - 야드적치Bed상태구분
        String szYdUpStkBedWMax		= null;
//    	int    intYdUpStkBedWMax	= 0;
//    	int    intYdUpStkBedLMax	= 0;
    	//-------------------------------------------------------------------------------------------------

    	//-------------------------------------------------------------------------------------------------
    	//	권하위치 정보
    	//-------------------------------------------------------------------------------------------------
    	String szYdDnStkColBedLTp 	= null;					//권하지시위치 - 야드적치열Bed길이Type
    	String szYdDnCarUseGp		= null;					//차량사용구분
    	String szDnTrnEqpCd			= null;					//운송장비코드
    	String szDnCarNo			= null;					//차량번호
    	String szDnCardNo			= null;					//카드번호

    	int    intYdDnStkBedXaxis	= 0;					//권하지시위치베드 - 야드적치BedX축
    	int    intYdDnStkBedYaxis	= 0;					//권하지시위치베드 - 야드적치BedY축
    	String szYdDnStkBedLGp		= null;					//권하지시위치베드 - 야드적치Bed길이구분
//    	String szYdDnStkBedWGp 		= null;					//권하지시위치베드 - 야드적치Bed폭구분
//    	String szYdDnStkBedWhioStat	= null;					//권하지시위치베드 - 야드적치Bed상태구분

    	String szYdDnStkBedWMax		= null;
//    	int    intYdDnStkBedWMax	= 0;
//    	int    intYdDnStkBedLMax	= 0;
    	//-------------------------------------------------------------------------------------------------

    	String szCraneGrabUseGp 	= null;
		String szUpGrabXValue     	= null;
		String szUpGrabYValue     	= null;
		int    intUpGrabY1Value     = 0;
		int    intUpGrabY2Value     = 0;

		String szDnGrabXValue     	= null;
		String szDnGrabYValue     	= null;
		int    intDnGrabY1Value     = 0;
		int    intDnGrabY2Value     = 0;
		int    intRtnVal 			= 0;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(recInPara, "P");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

    	szMsg = "[" + szOperationName + "] -------------------------- 메소드 시작 - 파라미터 확인 --------------------------" ;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		szStlNo			= ydDaoUtils.paraRecChkNull(recInPara, "STL_NO");					//크레인작업재료들중에서 길이가 제일 긴 재료번호
		szYdUpStkColGp 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_STK_COL_GP");			//권상지시위치 - 적치열
		szYdUpStkBedNo	= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_STK_BED_NO");			//권상지시위치 - 적치베드
		szYdDnStkColGp	= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_STK_COL_GP");			//권하지시위치 - 적치열
		szYdDnStkBedNo	= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_STK_BED_NO");			//권하지시위치 - 적치베드
		szYdEqpId		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID");				//크레인설비ID
		szYdMtlW 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W"); 				//크레인작업재료들중에서 폭이 제일 넓은 재료의 폭 값

		szMsg = "[" + szOperationName + "] 크레인작업재료들중에서 길이가 제일 긴 재료번호 - " + szStlNo;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		szMsg = "[" + szOperationName + "] 권상지시위치[적치열구분 - " + szYdUpStkColGp + ", 베드번호 - " + szYdUpStkBedNo + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		szMsg = "[" + szOperationName + "] 권하지시위치[적치열구분 - " + szYdDnStkColGp + ", 베드번호 - " + szYdDnStkBedNo + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		szMsg = "[" + szOperationName + "] 크레인설비ID - " + szYdEqpId;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		//---------------------------------------------------------------------------------------------------------
		// 저장품 조회
		// 제품길이 (YD_MTL_L) 추출
		//---------------------------------------------------------------------------------------------------------
		szMsg = "[" + szOperationName + "] 재료번호[" + szStlNo + "]로 저장품 조회 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		outRecSet	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("STL_NO", szStlNo);
		/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStock 

		SELECT 
		      STL_NO                    AS STL_NO                    -- 재료번호
		     ,REGISTER                  AS REGISTER                  -- 등록자
		     ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT      -- 등록일시
		     ,MODIFIER                  AS MODIFIER                  -- 수정자
		     ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT      -- 수정일시
		     ,DEL_YN                    AS DEL_YN                    -- 삭제유무
		     ,YD_WBOOK_ID               AS YD_WBOOK_ID               -- 야드작업예약ID
		     ,YD_SCH_CD                 AS YD_SCH_CD                 -- 야드스케쥴코드
		     ,PTOP_PLNT_GP              AS PTOP_PLNT_GP              -- 조업공장구분
		     ,YD_MTL_ITEM               AS YD_MTL_ITEM               -- 야드재료품목
		     ,ITEMNAME_CD               AS ITEMNAME_CD               -- 품명코드
		     ,YD_MTL_STAT               AS YD_MTL_STAT               -- 야드재료상태
		     ,STL_PROG_CD               AS STL_PROG_CD               -- 재료진도코드
		     ,ORD_YEOJAE_GP             AS ORD_YEOJAE_GP             -- 주문여재구분
		     ,FRTOMOVE_ORD_DATE         AS FRTOMOVE_ORD_DATE         -- 이송지시일자
		     ,FRTOMOVE_PLANT_GP         AS FRTOMOVE_PLANT_GP         -- 이송공장구분
		     ,STL_APPEAR_GP             AS STL_APPEAR_GP             -- 재료외형구분
		     ,PLNT_PROC_CD              AS PLNT_PROC_CD              -- 공장공정코드
		     ,YD_MTL_T                  AS YD_MTL_T                  -- 야드재료두께
		     ,YD_MTL_W                  AS YD_MTL_W                  -- 야드재료폭
		     ,YD_MTL_L                  AS YD_MTL_L                  -- 야드재료길이
		     ,YD_MTL_WT                 AS YD_MTL_WT                 -- 야드재료중량
		     ,YD_MTL_W_GP               AS YD_MTL_W_GP               -- 야드재료폭구분
		     ,YD_MTL_T_GP               AS YD_MTL_T_GP               -- 야드재료두께구분
		     ,YD_MTL_L_GP               AS YD_MTL_L_GP               -- 야드재료길이구분
		     ,COOL_DONE_GP              AS COOL_DONE_GP              -- 냉각완료구분
		     ,REHEAT_SLAB_GP            AS REHEAT_SLAB_GP            -- 재열재구분
		     ,TRANS_ORD_DATE            AS TRANS_ORD_DATE            -- 운송지시일자
		     ,TRANS_ORD_SEQNO           AS TRANS_ORD_SEQNO           -- 운송지시순번
		     ,CAR_NO                    AS CAR_NO                    -- 차량번호
		     ,CARD_NO                   AS CARD_NO                   -- 카드번호
		     ,YD_STK_BED_NO             AS YD_STK_BED_NO             -- 야드적치Bed번호
		     ,YD_STK_COL_GP             AS YD_STK_COL_GP             -- 야드적치열구분
		     ,ARR_WLOC_CD               AS ARR_WLOC_CD               -- 착지개소코드
		     ,YD_FRTOMOVE_YD_GP         AS YD_FRTOMOVE_YD_GP         -- 야드이송야드구분
		     ,YD_FRTOMOVE_BAY_GP        AS YD_FRTOMOVE_BAY_GP        -- 야드이송동구분
		     ,URGENT_FRTOMOVE_WORD_GP   AS URGENT_FRTOMOVE_WORD_GP   -- 긴급이송작업지시구분
		     ,YD_FTMV_MEANS_GP          AS YD_FTMV_MEANS_GP          -- 야드이송수단구분
		     ,MMATL_FEE_NO              AS MMATL_FEE_NO              -- 모재료번호
		     ,YD_WRK_PLAN_CRN           AS YD_WRK_PLAN_CRN           -- 야드작업계획크레인
		     ,YD_WRK_PLAN_TCAR          AS YD_WRK_PLAN_TCAR          -- 야드작업계획대차
		     ,YD_CAR_UPP_LOC_CD         AS YD_CAR_UPP_LOC_CD         -- 야드차상위치코드
		     ,YD_CURR_STR_LOC           AS YD_CURR_STR_LOC           -- 야드현저장위치
		     ,YD_RCPT_DATE              AS YD_RCPT_DATE              -- 야드입고일자
		     ,SNDBK_RSN_CD              AS SNDBK_RSN_CD              -- 반송원인코드
		     ,SNDBK_GP                  AS SNDBK_GP                  -- 반송요청구분
		     ,SNDBK_REGISTER            AS SNDBK_REGISTER            -- 반송요청자
		     ,TO_CHAR(SNDBK_REG_DDTT, 'YYYYMMDDHH24MISS') AS SNDBK_REG_DDTT -- 반송요청일자
		     ,CAR_LOTID                 AS CAR_LOTID                 -- 차량LotID
		     ,TO_CHAR(CAR_LOTID_REG_DDTT, 'YYYYMMDDHH24MISS') AS CAR_LOTID_REG_DDTT        -- 차량LotID등록일자
		     ,DETAIL_ARR_CD             AS DETAIL_ARR_CD             -- 상세착지코드
		     ,YD_FTMV_WRK_CMPL_GP       AS YD_FTMV_WRK_CMPL_GP       -- 야드이송작업완료구분
		     ,TO_CHAR(YD_FTMV_WRK_CMPL_DD, 'YYYYMMDDHH24MISS') AS YD_FTMV_WRK_CMPL_DD       -- 야드이송작업완료일자
		     ,BOOK_OUT_RESN             AS BOOK_OUT_RESN             -- Book-Out원인
		     ,BOOK_OUT_DATE             AS BOOK_OUT_DATE             -- Book-Out일자
		     ,BOOK_OUT_PROG             AS BOOK_OUT_PROG             -- Book-Out공정
		     ,US_MAINTMATL              AS US_MAINTMATL              -- 상면보수재
		     ,US_MAINT_SCH_MAKE_YN      AS US_MAINT_SCH_MAKE_YN      -- 상면보수스케줄작성여부
		     ,US_MAINT_WRK_CMPL_YN      AS US_MAINT_WRK_CMPL_YN      -- 상면보수작업완료여부
		     ,LS_MAINTMATL              AS LS_MAINTMATL              -- 하면보수재
		     ,LS_MAINT_SCH_MAKE_YN      AS LS_MAINT_SCH_MAKE_YN      -- 하면보수스케줄작성여부
		     ,LS_MAINT_WRK_CMPL_YN      AS LS_MAINT_WRK_CMPL_YN      -- 하면보수작업완료여부
		     ,CPL_WRK_MTL               AS CPL_WRK_MTL               -- 냉간교정재
		     ,CR_CORR_SCH_MAKE_YN       AS CR_CORR_SCH_MAKE_YN       -- 냉간교정스케줄작성여부
		     ,CR_CORR_WRK_CMPL_YN       AS CR_CORR_WRK_CMPL_YN       -- 냉간교정작업완료여부
		     ,HTTRT_HPL_MTL             AS HTTRT_HPL_MTL             -- 열처리교정재
		     ,HTTRT_CORR_SCH_MAKE_YN    AS HTTRT_CORR_SCH_MAKE_YN    -- 열처리교정스케줄작성여부
		     ,HTTRT_CORR_WRK_CMPL_YN    AS HTTRT_CORR_WRK_CMPL_YN    -- 열처리교정작업완료여부
		     ,GAS_WRK_MTL               AS GAS_WRK_MTL               -- GAS작업재
		     ,GAS_WRK_SCH_MAKE_YN       AS GAS_WRK_SCH_MAKE_YN       -- Gas작업스케줄작성여부
		     ,GAS_WRK_WRK_CMPL_YN       AS GAS_WRK_WRK_CMPL_YN       -- Gas작업작업완료여부
		     ,SHOT_BLST_WRK_MTL         AS SHOT_BLST_WRK_MTL         -- ShortBlast작업재
		     ,S_BLST_WRK_SCH_MAKE_YN    AS S_BLST_WRK_SCH_MAKE_YN    -- ShortBlast작업스케줄작성여부
		     ,S_BLST_WRK_WRK_CMPL_YN    AS S_BLST_WRK_WRK_CMPL_YN    -- ShortBlast작업작업완료여부
		     ,PRESS_WRK_MTL             AS PRESS_WRK_MTL             -- 프레스교정재
		     ,PRS_CORR_SCH_MAKE_YN      AS PRS_CORR_SCH_MAKE_YN      -- Press교정스케줄작성여부
		     ,PRS_CORR_WRK_CMPL_YN      AS PRS_CORR_WRK_CMPL_YN      -- Press교정작업완료여부
		     ,PL_WR_PRSNT_PROC_CD       AS PL_WR_PRSNT_PROC_CD       -- 후판실적현공정코드
		 FROM TB_YD_SHRSTOCK A
		WHERE STL_NO = :V_STL_NO
		*/

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 recSchPara에 logId 추가 
		recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
		
		intRtnVal = ydStockDao.getYdStock(recPara, outRecSet);		// intGp == 0

		if (intRtnVal <= 0) {
			szMsg = "[" + szOperationName + "] 재료번호[" + szStlNo + "]로 저장품 조회 시 오류발생 - 메세지 : " + Integer.toString(intRtnVal);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		}

		outRecSet.first();
		recTemp  = outRecSet.getRecord();

		szYdMtlL = ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L"); 			// 제품길이

		int pointIdx = szYdMtlW.lastIndexOf(".");

		if (pointIdx >= 0) {
			szYdMtlW = szYdMtlW.substring(0, pointIdx);
		}

		szMsg = "[" + szOperationName + "] 크레인작업재료들중에서 길이가 제일 긴 재료번호[" + szStlNo + "] - 제품길이[" + szYdMtlL + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		szMsg = "[" + szOperationName + "] 크레인작업재료들중에서 폭이 제일 넓은 재료의 제품폭[" + szYdMtlW + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		//---------------------------------------------------------------------------------------------------------
		// 크레인사양 조회
		// 설비ID로  크레인 Beam 길이(YD_CRN_TONG_L) 추출
		//---------------------------------------------------------------------------------------------------------
		szMsg = "[" + szOperationName + "] 크레인설비ID[" + szYdEqpId + "]로 크레인사양 조회 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		recPara   = JDTORecordFactory.getInstance().create();
		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
		recPara.setField("YD_EQP_ID", szYdEqpId);
		/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSpecDAO.getYdCrnSpec 

		SELECT YD_EQP_ID                AS YD_EQP_ID
		      ,REGISTER                 AS REGISTER
		      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
		      ,MODIFIER                 AS MODIFIER
		      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
		      ,DEL_YN                   AS DEL_YN
		      ,YD_CRN_GRAB_TP           AS YD_CRN_GRAB_TP
		      ,YD_CRN_TONG_H            AS YD_CRN_TONG_H
		      ,YD_CRN_TONG_L            AS YD_CRN_TONG_L
		      ,YD_CRN_TONG_INTVL_W      AS YD_CRN_TONG_INTVL_W
		      ,YD_CRN_TONG_END_T        AS YD_CRN_TONG_END_T
		      ,YD_CRN_TONG_W_TOL        AS YD_CRN_TONG_W_TOL
		      ,YD_EQP_WAIT_LOC          AS YD_EQP_WAIT_LOC
		      ,YD_CRN_SB_H              AS YD_CRN_SB_H
		      ,YD_WRK_ABLE_L            AS YD_WRK_ABLE_L
		      ,YD_WRK_ABLE_W            AS YD_WRK_ABLE_W
		      ,YD_WRK_ABLE_SH           AS YD_WRK_ABLE_SH
		      ,YD_WRK_ABLE_WT           AS YD_WRK_ABLE_WT
		      ,YD_CRN_GRAB1_ABLE_SH     AS YD_CRN_GRAB1_ABLE_SH
		      ,YD_CRN_GRAB1_ABLE_WT     AS YD_CRN_GRAB1_ABLE_WT
		      ,YD_CRN_GRAB2_ABLE_SH     AS YD_CRN_GRAB2_ABLE_SH
		      ,YD_CRN_GRAB2_ABLE_WT     AS YD_CRN_GRAB2_ABLE_WT
		      ,YD_WRK_ABLE_XAXIS_FR     AS YD_WRK_ABLE_XAXIS_FR
		      ,YD_WRK_ABLE_XAXIS_TO     AS YD_WRK_ABLE_XAXIS_TO
		      ,YD_WRK_ABLE_YAXIS_FR     AS YD_WRK_ABLE_YAXIS_FR
		      ,YD_WRK_ABLE_YAXIS_TO     AS YD_WRK_ABLE_YAXIS_TO
		      ,YD_WRK_ABLE_ZAXIS_FR     AS YD_WRK_ABLE_ZAXIS_FR
		      ,YD_WRK_ABLE_ZAXIS_TO     AS YD_WRK_ABLE_ZAXIS_TO
		      ,YD_CRN_GRAB1_BM_EXPN_L   AS YD_CRN_GRAB1_BM_EXPN_L
		      ,YD_CRN_GRAB1_MGNT_CNT    AS YD_CRN_GRAB1_MGNT_CNT
		      ,YD_CRN_GRAB1_MGNT_GAP    AS YD_CRN_GRAB1_MGNT_GAP
		      ,YD_CRN_GRAB2_BM_EXPN_L   AS YD_CRN_GRAB2_BM_EXPN_L
		      ,YD_CRN_GRAB2_MGNT_CNT    AS YD_CRN_GRAB2_MGNT_CNT
		      ,YD_CRN_GRAB2_MGNT_GAP    AS YD_CRN_GRAB2_MGNT_GAP
		      ,YD_CRN_PILNG_EQPM_EXN    AS YD_CRN_PILNG_EQPM_EXN
		  FROM TB_YD_CRNSPEC
		 WHERE YD_EQP_ID = :V_YD_EQP_ID
		 */

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 recPara에 logId 추가 
		recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
		
		intRtnVal = ydCrnspecDao.getYdCrnSpec(recPara, outRecSet);		// intGp == 0

		if (intRtnVal <= 0) {
			szMsg = "[" + szOperationName + "] 크레인설비ID[" + szYdEqpId + "]로 크레인사양 조회 시 오류발생 - 메세지 : " + Integer.toString(intRtnVal);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		}

		outRecSet.first();
		recTemp = outRecSet.getRecord();

		szYdCrnTongL = ydDaoUtils.paraRecChkNull(recTemp, "YD_CRN_TONG_L"); // 크레인 Beam 길이

		szMsg = "[" + szOperationName + "] 크레인설비ID[" + szYdEqpId + "]의 크레인Beam길이[YD_CRN_TONG_L:" + szYdCrnTongL + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

//SJH16		// 2후판 정정야드는 1Grab 크레인임
		szYdCrnGrabTp = "P";

		if ("X".equals(szYdCrnGrabTp)) {
			szYdCrnGrabGp = "E";
		} else {
			szYdCrnGrabGp = "D";
		}

		szMsg = "[" + szOperationName + "] 크레인 Grab 구분[D : 1Grab Crane, E : 2Grab Crane] - " + szYdCrnGrabGp;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		//---------------------------------------------------------------------------------------------------------
		// 권상지시위치 - 적치열조회 : 야드적치열Bed길이Type, 차량정보가 존재하면 차량정보 조회
		//---------------------------------------------------------------------------------------------------------
		szMsg = "[" + szOperationName + "] 권상지시위치[적치열구분:" + szYdUpStkColGp + "]로 적치열 조회 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
		recPara   = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", szYdUpStkColGp);
		
		/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.getYdStkcol 

		SELECT YD_STK_COL_GP            AS YD_STK_COL_GP
		     , TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
		     , REGISTER                 AS REGISTER
		     , TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
		     , MODIFIER                                 AS MODIFIER
		     , DEL_YN                   AS DEL_YN
		     , YD_GP                    AS YD_GP
		     , YD_BAY_GP                AS YD_BAY_GP
		     , YD_EQP_GP                AS YD_EQP_GP
		     , YD_STK_COL_NO            AS YD_STK_COL_NO
		     , YD_STK_COL_ACT_STAT      AS YD_STK_COL_ACT_STAT
		     , YD_STK_COL_RULE_XAXIS    AS YD_STK_COL_RULE_XAXIS
		     , YD_STK_COL_RULE_YAXIS    AS YD_STK_COL_RULE_YAXIS
		     , YD_STK_COL_W             AS YD_STK_COL_W
		     , YD_STK_COL_L             AS YD_STK_COL_L
		     , YD_CAR_USE_GP            AS YD_CAR_USE_GP
		     , TRN_EQP_CD               AS TRN_EQP_CD
		     , CAR_NO                   AS CAR_NO
		     , CARD_NO                  AS CARD_NO
		     , WLOC_CD                  AS WLOC_CD
		     , YD_PNT_CD                AS YD_PNT_CD
		     , YD_STK_COL_W_GP          AS YD_STK_COL_W_GP
		     , YD_STK_COL_H_MAX         AS YD_STK_COL_H_MAX
		     , YD_STK_COL_BED_L_TP      AS YD_STK_COL_BED_L_TP
		     , YD_COIL_OUTDIA_GRP_GP    AS YD_COIL_OUTDIA_GRP_GP
		     , YD_STKBED_USG_CD         AS YD_STKBED_USG_CD
		     , PL_SHEAR_YD_GRP_GP       AS PL_SHEAR_YD_GRP_GP
		  FROM TB_YD_STKCOL
		 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
		   AND DEL_YN ='N'
		*/	   

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 recPara에 logId 추가 
		recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
		
		intRtnVal = ydStkcolDao.getYdStkcol(recPara, outRecSet);		// intGp == 0

		if (intRtnVal <= 0) {
			szMsg = "[" + szOperationName + "] 권상지시위치[적치열구분:" + szYdUpStkColGp + "]로 적치열 조회 시 오류발생 - 메세지 : " + Integer.toString(intRtnVal);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		}

		outRecSet.first();
		recTemp = outRecSet.getRecord();

		szYdUpStkColBedLTp 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_BED_L_TP"); 		//야드적치열Bed길이Type
		szYdUpCarUseGp		= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_USE_GP"); 				//차량사용구분
		szUpTrnEqpCd		= ydDaoUtils.paraRecChkNull(recTemp, "TRN_EQP_CD"); 				//운송장비코드
		szUpCarNo			= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO"); 					//차량번호
		szUpCardNo			= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO"); 					//카드번호

		szMsg = "[" + szOperationName + "] 권상지시위치[적치열구분:" + szYdUpStkColGp + "]로 적치열 조회 완료 - 야드적치열Bed길이Type[" + szYdUpStkColBedLTp + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		szMsg = "[" + szOperationName + "] 권상지시위치[적치열구분:" + szYdUpStkColGp + "] - 차량사용구분[" + szYdUpCarUseGp + "], 운송장비코드[" + szUpTrnEqpCd + "], 차량번호[" + szUpCarNo + "], 카드번호[" + szUpCardNo + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		//---------------------------------------------------------------------------------------------------------
		// 권하지시위치 - 적치열조회 : 야드적치열Bed길이Type, 차량정보가 존재하면 차량정보 조회
		//---------------------------------------------------------------------------------------------------------
		if ("XX".equals(ydUtils.substr(szYdDnStkColGp, 0, 2))) {

			szMsg	= "[" + szOperationName + "] 권하지시 위치 XX 일때 차량정보 조회 SKIP 처리 >>>>" + szYdDnStkColGp;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			szYdDnStkColBedLTp	= ""; 				//야드적치열Bed길이Type
			szYdDnCarUseGp		= ""; 				//차량사용구분
			szDnTrnEqpCd		= ""; 				//운송장비코드
			szDnCarNo			= ""; 				//차량번호
			szDnCardNo			= ""; 				//카드번호

		} else {

			szMsg = "[" + szOperationName + "] 권하지시위치[적치열구분:" + szYdDnStkColGp + "]로 적치열 조회 시작";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdDnStkColGp);
			/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.getYdStkcol 

			SELECT YD_STK_COL_GP            AS YD_STK_COL_GP
			     , TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
			     , REGISTER                 AS REGISTER
			     , TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
			     , MODIFIER                                 AS MODIFIER
			     , DEL_YN                   AS DEL_YN
			     , YD_GP                    AS YD_GP
			     , YD_BAY_GP                AS YD_BAY_GP
			     , YD_EQP_GP                AS YD_EQP_GP
			     , YD_STK_COL_NO            AS YD_STK_COL_NO
			     , YD_STK_COL_ACT_STAT      AS YD_STK_COL_ACT_STAT
			     , YD_STK_COL_RULE_XAXIS    AS YD_STK_COL_RULE_XAXIS
			     , YD_STK_COL_RULE_YAXIS    AS YD_STK_COL_RULE_YAXIS
			     , YD_STK_COL_W             AS YD_STK_COL_W
			     , YD_STK_COL_L             AS YD_STK_COL_L
			     , YD_CAR_USE_GP            AS YD_CAR_USE_GP
			     , TRN_EQP_CD               AS TRN_EQP_CD
			     , CAR_NO                   AS CAR_NO
			     , CARD_NO                  AS CARD_NO
			     , WLOC_CD                  AS WLOC_CD
			     , YD_PNT_CD                AS YD_PNT_CD
			     , YD_STK_COL_W_GP          AS YD_STK_COL_W_GP
			     , YD_STK_COL_H_MAX         AS YD_STK_COL_H_MAX
			     , YD_STK_COL_BED_L_TP      AS YD_STK_COL_BED_L_TP
			     , YD_COIL_OUTDIA_GRP_GP    AS YD_COIL_OUTDIA_GRP_GP
			     , YD_STKBED_USG_CD         AS YD_STKBED_USG_CD
			     , PL_SHEAR_YD_GRP_GP       AS PL_SHEAR_YD_GRP_GP
			  FROM TB_YD_STKCOL
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			   AND DEL_YN ='N'
			*/	   

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 recPara에 logId 추가 
			recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
		
			intRtnVal = ydStkcolDao.getYdStkcol(recPara, outRecSet);	// intGp == 0

			if (intRtnVal <= 0) {
				szMsg = "[" + szOperationName + "] 권하지시위치[적치열구분:" + szYdDnStkColGp + "]로 적치열 조회 시 오류발생 - 메세지 : " + Integer.toString(intRtnVal);
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			outRecSet.first();
			recTemp = outRecSet.getRecord();

			szYdDnStkColBedLTp	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_BED_L_TP"); 		//야드적치열Bed길이Type
			szYdDnCarUseGp		= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_USE_GP"); 				//차량사용구분
			szDnTrnEqpCd		= ydDaoUtils.paraRecChkNull(recTemp, "TRN_EQP_CD"); 				//운송장비코드
			szDnCarNo			= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO"); 					//차량번호
			szDnCardNo			= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO"); 					//카드번호

			szMsg = "[" + szOperationName + "] 권하지시위치[적치열구분:" + szYdDnStkColGp + "]로 적치열 조회 완료 - 야드적치열Bed길이Type[" + szYdDnStkColBedLTp + "]";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			szMsg = "[" + szOperationName + "] 권하지시위치[적치열구분:" + szYdDnStkColGp + "] - 차량사용구분[" + szYdDnCarUseGp + "], 운송장비코드[" + szDnTrnEqpCd + "], 차량번호[" + szDnCarNo + "], 카드번호[" + szDnCardNo + "]";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		}

		//---------------------------------------------------------------------------------------------------------
		// 권상지시위치 - 적치Bed 조회
		// 야드적치열구분, 야드적치Bed번호로  야드적치BedY축(YD_STK_BED_YAXIS) 추출
		//---------------------------------------------------------------------------------------------------------
		szMsg = "[" + szOperationName + "] 권상지시위치[적치열구분:" + szYdUpStkColGp + ", 베드번호:" + szYdUpStkBedNo + "]로 베드 조회 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
		recPara   = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", szYdUpStkColGp);
		recPara.setField("YD_STK_BED_NO", szYdUpStkBedNo);
		/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYdStkbed 

		SELECT 
			 A.YD_STK_COL_GP AS YD_STK_COL_GP
			,A.YD_STK_BED_NO AS YD_STK_BED_NO
			,A.YD_STR_GTR_CD AS YD_STR_GTR_CD
			,A.REGISTER AS REGISTER
			,TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
			,A.MODIFIER AS MODIFIER
			,TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
			,A.DEL_YN AS DEL_YN
			,A.YD_STK_BED_TP AS YD_STK_BED_TP
			,A.YD_STK_BED_L_GP AS YD_STK_BED_L_GP
			,A.YD_STK_BED_W_GP AS YD_STK_BED_W_GP
			,A.YD_STK_BED_DIR_GP AS YD_STK_BED_DIR_GP
			,A.YD_STK_BED_ACT_STAT AS YD_STK_BED_ACT_STAT
			,A.YD_STK_BED_WHIO_STAT AS YD_STK_BED_WHIO_STAT
			,A.YD_STK_BED_USG_GP AS YD_STK_BED_USG_GP
			,A.YD_STK_BED_XAXIS AS YD_STK_BED_XAXIS
			,A.YD_STK_BED_YAXIS AS YD_STK_BED_YAXIS
			,A.YD_STK_BED_ZAXIS AS YD_STK_BED_ZAXIS
			,A.YD_STK_BED_LYR_MAX AS YD_STK_BED_LYR_MAX
			,A.YD_STK_BED_WT_MAX AS YD_STK_BED_WT_MAX
			,A.YD_STK_BED_H_MAX AS YD_STK_BED_H_MAX
			,A.YD_STK_BED_L_MAX AS YD_STK_BED_L_MAX
			,A.YD_STK_BED_W_MAX AS YD_STK_BED_W_MAX
			,A.YD_STK_BED_XAXIS_TOL AS YD_STK_BED_XAXIS_TOL
			,A.YD_STK_BED_YAXIS_TOL AS YD_STK_BED_YAXIS_TOL
			,A.YD_L_S_GRP_GP AS YD_L_S_GRP_GP
		    ,NVL(A.YD_COIL_OUTDIA_GRP_GP,B.YD_COIL_OUTDIA_GRP_GP) AS YD_COIL_OUTDIA_GRP_GP
		FROM TB_YD_STKBED A
		   , TB_YD_STKCOL B
		WHERE A.YD_STK_COL_GP=B.YD_STK_COL_GP
		  AND A.YD_STK_COL_GP = :V_YD_STK_COL_GP
		  AND A.YD_STK_BED_NO = :V_YD_STK_BED_NO
		  AND A.DEL_YN ='N'
		*/	  
		
//-------------------------------------------------------------------------------------------------------------------------
//2024.11.20 recPara에 logId 추가 
		recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
		
		intRtnVal = ydStkbedDao.getYdStkbed(recPara, outRecSet);		// intGp == 0

		if (intRtnVal <= 0) {
			szMsg = "[" + szOperationName + "] 권상지시위치[적치열구분:" + szYdUpStkColGp + ", 베드번호:" + szYdUpStkBedNo + "]로 베드 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		}

		outRecSet.first();
		recTemp = outRecSet.getRecord();

		intYdUpStkBedXaxis	 	= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_XAXIS"); 		// 야드적치BedX축
		intYdUpStkBedYaxis 		= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_YAXIS"); 		// 야드적치BedY축
		szYdUpStkBedLGp 		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_L_GP"); 			// 야드적치Bed길이구분
//		szYdUpStkBedWGp 		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_GP"); 			// 야드적치Bed폭구분
//		szYdUpStkBedWhioStat	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT"); 		// 야드적치Bed상태구분
		szYdUpStkBedWMax		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_MAX"); 			// 야드적치Bed폭Max
//		intYdUpStkBedLMax		= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_L_MAX"); 		// 야드적치Bed길이Max

		szMsg = "[" + szOperationName + "] szYdUpStkBedWMax Before >>>> " + szYdUpStkBedWMax;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		if ("".equals(szYdUpStkBedWMax)) {
			szYdUpStkBedWMax = "0";
		}

		pointIdx = szYdUpStkBedWMax.lastIndexOf(".");

		if (pointIdx >= 0) {
			szYdUpStkBedWMax = szYdUpStkBedWMax.substring(0, pointIdx);
		}

		szMsg = "[" + szOperationName + "] szYdUpStkBedWMax After >>>> " + szYdUpStkBedWMax;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

//		intYdUpStkBedWMax = Integer.parseInt(szYdUpStkBedWMax);

		szMsg = "[" + szOperationName + "] 권상지시위치[적치열구분:" + szYdUpStkColGp + ", 베드번호:" + szYdUpStkBedNo + "]의  야드적치BED X축[" + intYdUpStkBedXaxis + "], 야드적치BED Y축[" + intYdUpStkBedYaxis + "], 야드적치Bed길이구분[" + szYdUpStkBedLGp + "] 조회 완료";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		//---------------------------------------------------------------------------------------------------------
		// 권하지시위치 - 적치Bed 조회
		// 야드적치열구분, 야드적치Bed번호로  야드적치BedY축(YD_STK_BED_YAXIS) 추출
		//---------------------------------------------------------------------------------------------------------
		if ("XX".equals(ydUtils.substr(szYdDnStkColGp, 0, 2))) {

			szMsg	= "[" + szOperationName + "] 권하지시 위치 XX 일때 적치Bed 조회 SKIP 처리 >>>>" + szYdDnStkColGp;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			intYdDnStkBedXaxis 		= 0; 			// 야드적치BedX축
			intYdDnStkBedYaxis 		= 0; 			// 야드적치BedY축
			szYdDnStkBedLGp 		= ""; 			// 야드적치Bed길이구분
//			szYdDnStkBedWGp 		= ""; 			// 야드적치Bed폭구분
//			szYdDnStkBedWhioStat	= ""; 			// 야드적치Bed상태구분
			szYdDnStkBedWMax		= ""; 			// 야드적치Bed폭Max
//			intYdDnStkBedLMax		= 0; 			// 야드적치Bed길이Max

		} else {

			szMsg = "[" + szOperationName + "] 권하지시위치[적치열구분:" + szYdDnStkColGp + ", 베드번호:" + szYdDnStkBedNo + "]로 베드 조회 시작";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdDnStkColGp);
			recPara.setField("YD_STK_BED_NO", szYdDnStkBedNo);
			/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYdStkbed 

			SELECT 
				 A.YD_STK_COL_GP AS YD_STK_COL_GP
				,A.YD_STK_BED_NO AS YD_STK_BED_NO
				,A.YD_STR_GTR_CD AS YD_STR_GTR_CD
				,A.REGISTER AS REGISTER
				,TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
				,A.MODIFIER AS MODIFIER
				,TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
				,A.DEL_YN AS DEL_YN
				,A.YD_STK_BED_TP AS YD_STK_BED_TP
				,A.YD_STK_BED_L_GP AS YD_STK_BED_L_GP
				,A.YD_STK_BED_W_GP AS YD_STK_BED_W_GP
				,A.YD_STK_BED_DIR_GP AS YD_STK_BED_DIR_GP
				,A.YD_STK_BED_ACT_STAT AS YD_STK_BED_ACT_STAT
				,A.YD_STK_BED_WHIO_STAT AS YD_STK_BED_WHIO_STAT
				,A.YD_STK_BED_USG_GP AS YD_STK_BED_USG_GP
				,A.YD_STK_BED_XAXIS AS YD_STK_BED_XAXIS
				,A.YD_STK_BED_YAXIS AS YD_STK_BED_YAXIS
				,A.YD_STK_BED_ZAXIS AS YD_STK_BED_ZAXIS
				,A.YD_STK_BED_LYR_MAX AS YD_STK_BED_LYR_MAX
				,A.YD_STK_BED_WT_MAX AS YD_STK_BED_WT_MAX
				,A.YD_STK_BED_H_MAX AS YD_STK_BED_H_MAX
				,A.YD_STK_BED_L_MAX AS YD_STK_BED_L_MAX
				,A.YD_STK_BED_W_MAX AS YD_STK_BED_W_MAX
				,A.YD_STK_BED_XAXIS_TOL AS YD_STK_BED_XAXIS_TOL
				,A.YD_STK_BED_YAXIS_TOL AS YD_STK_BED_YAXIS_TOL
				,A.YD_L_S_GRP_GP AS YD_L_S_GRP_GP
			    ,NVL(A.YD_COIL_OUTDIA_GRP_GP,B.YD_COIL_OUTDIA_GRP_GP) AS YD_COIL_OUTDIA_GRP_GP
			FROM TB_YD_STKBED A
			   , TB_YD_STKCOL B
			WHERE A.YD_STK_COL_GP=B.YD_STK_COL_GP
			  AND A.YD_STK_COL_GP = :V_YD_STK_COL_GP
			  AND A.YD_STK_BED_NO = :V_YD_STK_BED_NO
			  AND A.DEL_YN ='N'
			*/	  

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 recPara에 logId 추가 
			recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
		
			intRtnVal = ydStkbedDao.getYdStkbed(recPara, outRecSet);			// intGp == 0
			if (intRtnVal <= 0) {
				szMsg = "[" + szOperationName + "] 권하지시위치[적치열구분:" + szYdDnStkColGp + ", 베드번호:" + szYdDnStkBedNo + "]로 베드 조회 시 오류발생 - 메세지 : " + Integer.toString(intRtnVal);
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			outRecSet.first();
			recTemp = outRecSet.getRecord();

			intYdDnStkBedXaxis 		= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_XAXIS"); 		// 야드적치BedX축
			intYdDnStkBedYaxis 		= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_YAXIS"); 		// 야드적치BedY축
			szYdDnStkBedLGp 		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_L_GP"); 			// 야드적치Bed길이구분
//			szYdDnStkBedWGp 		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_GP"); 			// 야드적치Bed폭구분
//			szYdDnStkBedWhioStat	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT"); 		// 야드적치Bed상태구분
			szYdDnStkBedWMax		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_MAX"); 			// 야드적치Bed폭Max
//			intYdDnStkBedLMax		= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_L_MAX"); 		// 야드적치Bed길이Max
		}

		szMsg = "[" + szOperationName + "] szYdDnStkBedWMax Before >>>> " + szYdDnStkBedWMax;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		if ("".equals(szYdDnStkBedWMax)) {
			szYdDnStkBedWMax = "0";
		}

		pointIdx = szYdDnStkBedWMax.lastIndexOf(".");

		if (pointIdx >= 0) {
			szYdDnStkBedWMax = szYdDnStkBedWMax.substring(0, pointIdx);
		}

		szMsg = "[" + szOperationName + "] szYdDnStkBedWMax After >>>> " + szYdDnStkBedWMax;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		szMsg = "[" + szOperationName + "] 권하지시위치[적치열구분:" + szYdDnStkColGp + ", 베드번호:" + szYdDnStkBedNo + "]의  야드적치BED X축[" + intYdDnStkBedXaxis + "], 야드적치BED Y축[" + intYdDnStkBedYaxis + "], 야드적치Bed길이구분[" + szYdDnStkBedLGp + "] 조회 완료";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		//---------------------------------------------------------------------------------------------------------
		//	X좌표 구하기
		//---------------------------------------------------------------------------------------------------------
		szUpGrabXValue = String.valueOf(intYdUpStkBedXaxis);
		szDnGrabXValue = String.valueOf(intYdDnStkBedXaxis);

		//---------------------------------------------------------------------------------------------------------
		//	Y좌표 구하기
		//---------------------------------------------------------------------------------------------------------
		szUpGrabYValue 	 = String.valueOf(intYdUpStkBedYaxis);
		szDnGrabYValue 	 = String.valueOf(intYdDnStkBedYaxis);
		intUpGrabY1Value = intYdUpStkBedYaxis;
		intUpGrabY2Value = intYdUpStkBedYaxis;
		intDnGrabY1Value = intYdDnStkBedYaxis;
		intDnGrabY2Value = intYdDnStkBedYaxis;
		szCraneGrabUseGp = "1";

	//	1후판정정야드는 Y1, Y2좌표 구하기 .. SKIP

		recOutPara.setField("CRANE_GRAB_USE_GP",	szCraneGrabUseGp);
		recOutPara.setField("UP_GRAB_X_VALUE", 		szUpGrabXValue);
		recOutPara.setField("UP_GRAB_Y_VALUE", 		szUpGrabYValue);
		recOutPara.setField("UP_GRAB_Y1_VALUE", 	String.valueOf(intUpGrabY1Value));
		recOutPara.setField("UP_GRAB_Y2_VALUE", 	String.valueOf(intUpGrabY2Value));
		recOutPara.setField("DN_GRAB_X_VALUE", 		szDnGrabXValue);
		recOutPara.setField("DN_GRAB_Y_VALUE", 		szDnGrabYValue); 
		recOutPara.setField("DN_GRAB_Y1_VALUE", 	String.valueOf(intDnGrabY1Value));
		recOutPara.setField("DN_GRAB_Y2_VALUE", 	String.valueOf(intDnGrabY2Value));
		//---------------------------------------------------------------------------------------------------------

    	return JPlateYdConst.RETN_CD_SUCCESS;
    } // end of calPlateCraneXYYdP()	   
    
}
