/*
 * @(#) 2후판정정야드 크레인 스케줄 공통 클래스
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/12/12
 *
 * @description		2후판정정야드 크레인 스케줄 공통 클래스
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/12/12   김현우      김현우       최초작성  
 */

package com.inisteel.cim.yd.jplateyd.util;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;

import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 1후판 정정 2열처리 Book-In/Book-Out 관련 야드공통 UTIL (putLogNew, getLogIdNew, isEmpty, getJDTOLogId) 부분 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 * @author Administrator
 *
 */
public class JPlateYdCrnSchUtil {

	private static final String SZ_CLASS_NAME 	= JPlateYdCrnSchUtil.class.getName();

	private static JPlateYdUtils 	ydUtils 		= new JPlateYdUtils();
	private static JPlateYdDaoUtils	ydDaoUtils	= new JPlateYdDaoUtils();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 1후판 정정 2열처리 Book-In/Book-Out 관련 야드공통 UTIL (putLogNew, getLogIdNew, isEmpty, getJDTOLogId) 부분 
//-------------------------------------------------------------------------------------------------------------------------
    private static YdUtils		ydLogUtils  		= new YdUtils();
	
	/*--------------------------------------------------------------------------------------------------------
	 * 1. 대상재 데이터 정합성 체크
	 * 2. 크레인스케줄 수행조건 판단
	 * 3. 크레인 작업대상 베드별 조회
	 * 4. 작업대상에 그룹핑 파라미터 설정
	 * 	W:보조작업
	 * 	A:주작업
	 * 	M:보조작업/주작업
	 * 	R:리버스주작업(모음작업)
	 * 	B:베드의 최하단주작업(모음작업)
	 * 	T:최종위치 주작업
	 * 	S:베드의 최하단주작업(최종위치)
	 * 5. 핸들링LOT별로 정렬
	 *  . 보조작업, 보조작업/주작업에 대한 크레인사양 적용
	 * 6. 보조작업, 보조작업/주작업에 대한 권상위치와 권하위치 업데이트
	 * 7. 모음작업/최종위치에 대한 권상위치 업데이트 와 크레인작업대상재 추가
	 * 8. 모음작업/최종위치에 대한 크레인사양 적용
	 * 9. 모음작업/최종위치에 대한 권하위치 업데이트
	 --------------------------------------------------------------------------------------------------------*/

	/**
	 * 2후판정정야드 크레인스케줄수행판단
	 * @param recPara
	 * @return
	 */
	public static String procCheckIfCrnSchRunnableForPlateGds(JDTORecord recPara) {
		//기본변수정의
		String szLogMsg			= null;
		String szOperationName	= "2후판정정야드 크레인스케줄수행판단";
		String szMethodName		= "procCheckIfCrnSchRunnableForPlateGds";
		String szRtnMsg			= null;

		szLogMsg = "["+szOperationName+"] -------------------------- 메소드 시작 - 파라미터확인 -------------------------------";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		szRtnMsg = procCheckIfCrnSchRunnable(recPara);

		szLogMsg = "["+szOperationName+"] 크레인스케줄수행판단 모듈 호출 후 메세지 : " + szRtnMsg;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		szLogMsg = "["+szOperationName+"] -------------------------- 메소드 끝 -------------------------------";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	}

	/**
	 * 크레인스케줄수행판단
	 * @param recPara
	 * @return
	 */
	public static String procCheckIfCrnSchRunnable(JDTORecord recPara) {

		//기본변수정의
		String szLogMsg			= null;
		String szOperationName	= "크레인스케줄수행판단";
		String szMethodName		= "procCheckIfCrnSchRunnable";
		String szRtnMsg			= null;

		JDTORecord recTemp		= null;

		//로컬변수 정의
		String szYdSchCd		= null;					//스케줄코드
		String szYdWbookId		= null;					//작업예약ID

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(recPara, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {

			//-------------------------------------------------------------------------------------------------------------
			// 파라미터 확인
			//-------------------------------------------------------------------------------------------------------------
			szYdSchCd	= ydDaoUtils.paraRecChkNull(recPara, 	"YD_SCH_CD");
			szYdWbookId	= ydDaoUtils.paraRecChkNull(recPara, 	"YD_WBOOK_ID");

			szLogMsg	= "[" + szOperationName + "] 작업예약[" + szYdWbookId + "]의 스케줄수행판단 시작";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			//-------------------------------------------------------------------------------------------------------------
			// 크레인스케줄 기준 체크 - 스케줄금지유무와 주/대체크레인 교체 유무 판단
			//-------------------------------------------------------------------------------------------------------------
			if (!"".equals(szYdSchCd)) {	// 크레인스케줄코드가 존재할 때만 체크

				szLogMsg="[" + szOperationName + "] 크레인스케줄을 수행할 작업예약[" + szYdWbookId  +"]의 스케줄코드[" + szYdSchCd + "]의 스케줄기준 조회 시작";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

				szRtnMsg = JPlateYdCommonUtils.getWrkableCrnBySchRule(szYdSchCd, recPara);

				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					szLogMsg="[" + szOperationName + "] 크레인스케줄을 수행할 작업예약[" + szYdWbookId + "]의 스케줄코드[" + szYdSchCd + "]의 스케줄기준 조회 후 오류발생 - 메세지 : " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
					return szRtnMsg;
				}

				szLogMsg="[" + szOperationName + "] 크레인스케줄을 수행할 작업예약[" + szYdWbookId + "]의 스케줄코드[" + szYdSchCd  +"]의 스케줄기준 조회 완료 - 메세지 : " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			}

			//-------------------------------------------------------------------------------------------------------------
			//	해당 작업예약의 작업예약재료들을 대상으로 크레인스케줄수행판단 모듈 호출
			//-------------------------------------------------------------------------------------------------------------
			szLogMsg = "[" + szOperationName + "] 작업스케쥴[" + szYdSchCd + "], 작업예약[" + szYdWbookId + "]의 작업재료로 크레인스케줄수행판단 모듈 호출 시작";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			recTemp = JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_SCH_CD",	szYdSchCd);
			recTemp.setField("YD_WBOOK_ID", szYdWbookId);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 recSchPara에 logId 추가 
			recTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
			
			szRtnMsg = checkIfSchableByYdWbookId(recTemp);

			szLogMsg = "[" + szOperationName + "] 작업예약[" + szYdSchCd + "]의 작업재료로 크레인스케줄수행판단 모듈 호출 완료 - 메세지 : " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {

				szLogMsg = "[" + szOperationName + "] 작업예약[" + szYdSchCd + "]의 작업재료로 크레인스케줄수행판단 모듈 호출 시 오류발생 ";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);

				return szRtnMsg;
			}
			
			//동일한 재료가 두곳이상 적치되어있는지 체크시작. 2024.05.13 동일제품이 두곳적치되어 중복 작업재료 생성되는 현상 발견. ORA-00001 24-04-24 19:35  미리 체크해서 EXCEPTION 처리 필요

		} catch(Exception ex) {

			szLogMsg = "[" + szOperationName + "] Exception 발생 ";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);

			throw new DAOException(ex);
		}

		szLogMsg = "[" + szOperationName + "] -------------------------- 메소드 끝 -------------------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}

	/**
	 * 크레인스케줄수행판단(작업예약ID)
	 * @param recPara
	 * @return
	 * @throws JDTOException
	 */
	public static String checkIfSchableByYdWbookId(JDTORecord recPara) throws JDTOException {
		/*
		 * 업무기준 	:	1. 스케줄코드로 스케줄기준 조회 후 스케줄금지유무판단과 주/대체작업크레인 조회
		 * 				2. 작업예약의 작업재료들의 상단 더미재가 크레인스케줄의 작업재료로 등록되어 있는 지 조회
		 * 					2-1. 등록되어 있으면 동일한 스케줄코드인 지 판단.
		 *
		 * 파라미터	:	1. YD_WBOOK_ID		- 작업예약ID
		 * 				2. YD_SCH_CD		- 스케줄코드
		 *
		 */

		//기본변수정의
		String szLogMsg			= null;
		String szOperationName	= "크레인스케줄수행판단(작업예약ID)";
		String szMethodName		= "checkIfSchableByYdWbookId";
		String szRtnMsg			= null;

		JDTORecord    recTemp	= null;
		JDTORecordSet rsResult	= null;

		//로컬변수 정의
		String szYdWbookId		= null;			//작업예약ID
		String szYdCrnSchId		= null;			//크레인스케줄ID
		String szStlNo			= null;			//작업재료

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.15 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(recPara, "P");  			// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                    	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		JPlateYdCrnWrkMtlDAO ydCrnWrkMtlDao = new JPlateYdCrnWrkMtlDAO();

		//-------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//-------------------------------------------------------------------------------------------------------------
		szYdWbookId = ydDaoUtils.paraRecChkNull(recPara, 	"YD_WBOOK_ID");
		//-------------------------------------------------------------------------------------------------------------

		//-------------------------------------------------------------------------------------------------------------
		//	1. 작업예약재료 대상재들이 이미 크레인 스케줄의 작업대상으로 되어 있는 지를 판단
		//		크레인스케줄의 작업대상으로 되어 있으면 크레인스케줄을 수행하지 않는다.
		//		해당 크레인스케줄 수행이 완료되고 난 후 권하실적 처리에서 크레인작업요구지시 모듈을 호출하면 모듈내에서
		//		작업예약을 풀어준다.
		//-------------------------------------------------------------------------------------------------------------

		szLogMsg = "[" + szOperationName + "] 해당작업예약[" + szYdWbookId + "]의 작업재료가 다른 작업예약의 크레인스케줄의 작업재료로 존재하는 지 조회 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		rsResult = JDTORecordFactory.getInstance().createRecordSet("");

		szRtnMsg = ydCrnWrkMtlDao.getExistByWbookId(recPara, rsResult);		// intGp = 18

		szLogMsg = "[" + szOperationName + "] 해당작업예약[" + szYdWbookId + "]의 작업재료가 다른 작업예약의 크레인스케줄의 작업재료로 존재하는 지 조회 완료 - 메세지 : " + szRtnMsg;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {

			rsResult.first();
			recTemp		 = rsResult.getRecord();
			szYdCrnSchId = ydDaoUtils.paraRecChkNull(recTemp, "YD_CRN_SCH_ID");
			szStlNo		 = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
			szLogMsg	 = "[" + szOperationName + "] 해당작업예약[" + szYdWbookId + "]의 작업재료가 다른 작업예약의 크레인스케줄[" + szYdCrnSchId + "]의 작업재료[" + szStlNo + "]로 존재함으로 스케줄기동이 불가합니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);

			return JPlateYdConst.RETN_CRN_EXIST_SCH;

		} else if (!JPlateYdConst.RETN_CD_NOTEXIST.equals(szRtnMsg)) {

			szLogMsg	= "[" + szOperationName + "] 해당작업예약[" + szYdWbookId + "]의 작업재료가 다른 작업예약의 크레인스케줄의 작업재료로 존재하는 지 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);

			return szRtnMsg;
		}

		//-------------------------------------------------------------------------------------------------------------
		//	2. 작업예약재료 대상재들의 상단 더미재들이 크레인스케줄의 작업대상재로 존재하는 지 판단
		//		존재하는 경우에는 동일한 스케줄일 때만 크레인스케줄 수행을 처리
		//-------------------------------------------------------------------------------------------------------------

		szLogMsg = "[" + szOperationName + "] 해당작업예약[" + szYdWbookId + "]의 작업재료들의 상단재료들이 다른 스케줄코드의 크레인스케줄의 작업재료로 존재하는 지 조회 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		szRtnMsg = ydCrnWrkMtlDao.getExistByWbookId(recPara, rsResult);		// intGp = 19

		szLogMsg = "[" + szOperationName + "] 해당작업예약[" + szYdWbookId + "]의 작업재료들의 상단재료들이 다른 스케줄코드의 크레인스케줄의 작업재료로 존재하는 지 조회 완료 - 메세지 : " + szRtnMsg;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {

			rsResult.first();

			recTemp		 = rsResult.getRecord();
			szYdCrnSchId = ydDaoUtils.paraRecChkNull(recTemp, "YD_CRN_SCH_ID");
			szStlNo		 = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");

			szLogMsg	 = "[" + szOperationName + "] 해당작업예약[" + szYdWbookId + "]의 작업재료들의 상단재료들이 다른 스케줄코드의 크레인스케줄[" + szYdCrnSchId + "]의 작업재료[" + szStlNo + "]로 존재함으로 스케줄기동이 불가합니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);

			return JPlateYdConst.RETN_CRN_EXIST_SCH;

		} else if (!JPlateYdConst.RETN_CD_NOTEXIST.equals(szRtnMsg)) {

			szLogMsg		= "[" + szOperationName + "] 해당작업예약[" + szYdWbookId + "]의 작업재료들의 상단재료들이 다른 스케줄코드의 크레인스케줄의 작업재료로 존재하는 지 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);

			return szRtnMsg;
		}

		//-------------------------------------------------------------------------------------------------------------

		szLogMsg	= "[" + szOperationName + "] -------------------------- 메소드 끝 -------------------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}

	/**
	 * 크레인XY좌표수정
	 * @param recInPara
	 * @return
	 * @throws JDTOException
	 */
	public static String uptCrnSchXYCord(JDTORecord recInPara) throws JDTOException {

		JPlateYdStkBedDAO ydStkbedDao = new JPlateYdStkBedDAO();
		JPlateYdCrnSchDAO ydCrnschDao = new JPlateYdCrnSchDAO();

		String szLogMsg				= null;
		String szOperationName		= "크레인XY좌표수정";
		String szMethodName			= "uptCrnSchXYCord";

		String szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;

		JDTORecord    	recUpdCrn	= null;
		JDTORecord		recPara 	= null;
		JDTORecordSet 	rsResult	= null;

		String 	szYdCrnSchId		= null;
		String 	szYdEqpId			= null;
		String 	szYdUpStkColGp		= null;
		String 	szYdUpStkBedNo		= null;
		String 	szYdDnStkColGp		= null;
		String 	szYdDnStkBedNo		= null;

		String 	szYdUpWoLoc			= null;
		String 	szYdUpWoLayer		= null;
		String 	szYdDnWoLoc			= null;
		String 	szYdDnWoLayer		= null;

		String 	szYdEqpWrkSh		= null;
		String 	szYdEqpWrkWt		= null;
		String 	szYdEqpWrkT			= null;
		String 	szYdEqpWrkMaxW		= null;
		String 	szYdEqpWrkMaxL		= null;
		String	szModifier			= null;

		int    	intCraneGapUpX		= JPlateYdConst.PLATE_CRANE_GAP_X;
		int    	intCraneGapUpY		= JPlateYdConst.PLATE_CRANE_GAP_Y;
		int    	intCraneGapUpZ		= JPlateYdConst.PLATE_CRANE_GAP_Z;
		int    	intCraneGapDnX		= JPlateYdConst.PLATE_CRANE_GAP_X;
		int    	intCraneGapDnY		= JPlateYdConst.PLATE_CRANE_GAP_Y;
		int    	intCraneGapDnZ		= JPlateYdConst.PLATE_CRANE_GAP_Z;

		String 	szUpCraneGrabUseGp 	= "";
		String 	szUpGrabXValue     	= "";
		String 	szUpGrabYValue     	= "";
		String 	szDnGrabXValue     	= "";
		String 	szDnGrabYValue     	= "";

		String 	szYdUpWoLocZaxis	= "";
		String 	szYdDnWoLocZaxis	= "";
		String	szYdDnWoFlag		= "N";		// 권하위치만 변경 (강제권하시)
		String	szYdToLocGuide		= "";
		String	szYdDnWrkActGp		= "";		//

		int     intRtnVal			= 0;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(recInPara, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                    	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
				
		//-------------------------------------------------------------------------------------------------------------
		//	파라미터확인
		//-------------------------------------------------------------------------------------------------------------
		szLogMsg = "[" + szOperationName + "] -------------------------- 메소드 시작 - 파라미터확인 :: " + recInPara.toString();
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szYdDnWoFlag 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_WO_FLAG", "N"	);		// 권하위치 변경 FLAG
		szYdCrnSchId 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_CRN_SCH_ID"		);
		szYdEqpId 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID"			);

		szYdUpStkColGp 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_STK_COL_GP"	);
		szYdUpStkBedNo 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_STK_BED_NO"	);
		szYdDnStkColGp 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_STK_COL_GP"	);
		szYdDnStkBedNo 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_STK_BED_NO"	);

		szYdUpWoLoc		= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_WO_LOC"		);
		szYdUpWoLayer	= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_WO_LAYER"		);
		szYdDnWoLoc		= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_WO_LOC"		);
		szYdDnWoLayer	= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_WO_LAYER"		);
		szYdDnWrkActGp	= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_WRK_ACT_GP"	);

		if ("".equals(szYdUpStkColGp)) {
			szYdUpStkColGp 	= ydUtils.substr(szYdUpWoLoc, 0, 6);
			szYdUpStkBedNo 	= ydUtils.substr(szYdUpWoLoc, 6, 2);
		}
		if ("".equals(szYdDnStkColGp)) {
			szYdDnStkColGp 	= ydUtils.substr(szYdDnWoLoc, 0, 6);
			szYdDnStkBedNo 	= ydUtils.substr(szYdDnWoLoc, 6, 2);
		}

		szYdEqpWrkSh	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH"		);
		szYdEqpWrkWt	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT"		);
		szYdEqpWrkT		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T"		);
		szYdEqpWrkMaxW	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_MAX_W"	);
		szYdEqpWrkMaxL	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_MAX_L"	);
		szModifier		= ydDaoUtils.paraRecModifier(recInPara);

		//-------------------------------------------------------------------------------------------------------------
		//	크레인 허용 오차 및 크레인 X, Y좌표 계산
		//-------------------------------------------------------------------------------------------------------------
		//----------------------------------------------------------------
		//	권상 시 크레인 허용오차
		//----------------------------------------------------------------
		intCraneGapUpX = ydUtils.getCraneGapX(szYdUpStkColGp);
		intCraneGapUpY = ydUtils.getCraneGapY(szYdUpStkColGp);
		intCraneGapUpZ = JPlateYdConst.PLATE_CRANE_GAP_Z;

		//----------------------------------------------------------------
		//	권하 시 크레인 허용오차
		//----------------------------------------------------------------
		intCraneGapDnX = ydUtils.getCraneGapX(szYdDnStkColGp);
		intCraneGapDnY = ydUtils.getCraneGapY(szYdDnStkColGp);
		intCraneGapDnZ = JPlateYdConst.PLATE_CRANE_GAP_Z;

		szLogMsg = "[" + szOperationName + "] 허용오차  . 권상:: " + intCraneGapUpX + "," + intCraneGapUpY + ", 권하:: " + intCraneGapDnX + "," + intCraneGapDnY;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szLogMsg = "[" + szOperationName + "] -------------------------- 2후판정정야드 X,Y 좌표계산 시작 -------------------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		recPara 			 = JDTORecordFactory.getInstance().create();
		JDTORecord recResult = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_CRN_SCH_ID", 		szYdCrnSchId		);
		recPara.setField("YD_EQP_ID", 			szYdEqpId			);
		recPara.setField("YD_UP_STK_COL_GP", 	szYdUpStkColGp		);
		recPara.setField("YD_UP_STK_BED_NO", 	szYdUpStkBedNo		);
		recPara.setField("YD_DN_STK_COL_GP", 	szYdDnStkColGp		);
		recPara.setField("YD_DN_STK_BED_NO", 	szYdDnStkBedNo		);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recPara에 logId 추가 
		recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
				
		JPlateYdGdsUtil.procXYCalForPlateCrane(recPara, recResult);

		szUpCraneGrabUseGp	= ydDaoUtils.paraRecChkNull(recResult, "CRANE_GRAB_USE_GP"	);
		szUpGrabXValue     	= ydDaoUtils.paraRecChkNull(recResult, "UP_GRAB_X_VALUE"	);
		szUpGrabYValue     	= ydDaoUtils.paraRecChkNull(recResult, "UP_GRAB_Y_VALUE"	);
		szDnGrabXValue     	= ydDaoUtils.paraRecChkNull(recResult, "DN_GRAB_X_VALUE"	);
		szDnGrabYValue     	= ydDaoUtils.paraRecChkNull(recResult, "DN_GRAB_Y_VALUE"	);

		szLogMsg = "[" + szOperationName + "] -------------------------- 2후판정정야드 X,Y 좌표계산 완료 -------------------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		if (!"".equals(szYdUpStkColGp)) {
			//------------------------------------------------------------------------------------------------------
			//	권상지시베드의 X,Y 좌표 조회
			//------------------------------------------------------------------------------------------------------
			szLogMsg = "[" + szOperationName + "] 권상지시베드[적치열:" + szYdUpStkColGp + ",적치베드:" + szYdUpStkBedNo + "]의 X,Y 좌표 조회 시작";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_STK_COL_GP", 		szYdUpStkColGp);
			recPara.setField("YD_STK_BED_NO", 		szYdUpStkBedNo);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recPara에 logId 추가 
			recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
					
			intRtnVal = ydStkbedDao.getYdStkbed(recPara, rsResult);		// intGp = 0

			if (intRtnVal <= 0) {
				szRtnMsg = "권상지시베드 조회 오류";
				szLogMsg = "[" + szOperationName + "] 권상지시베드[적치열:" + szYdUpStkColGp + ",적치베드:" + szYdUpStkBedNo + "]의 X,Y 좌표 조회 시 오류발생 - 메세지 : " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
				return szRtnMsg;
			}

			rsResult.first();
			recPara = rsResult.getRecord();

			szUpGrabXValue = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_XAXIS");
			szUpGrabYValue = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_YAXIS");

			szLogMsg = "[" + szOperationName + "] 권상지시베드[적치열:" + szYdUpStkColGp + ",적치베드:" + szYdUpStkBedNo + "]의 X,Y 좌표 조회 완료";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		}

		if (!"".equals(szYdDnStkColGp)) {
			//------------------------------------------------------------------------------------------------------
			//	권하지시베드의 X,Y 좌표 조회
			//------------------------------------------------------------------------------------------------------
			if ("XX".equals(ydUtils.substr(szYdDnStkColGp, 0, 2))) {

				szDnGrabXValue = "00000";
				szDnGrabYValue = "00000";

				szLogMsg	= "[" + szOperationName + "] 권하지시 위치 XX 일때 SKIP 처리 >>>>" + szYdDnStkColGp;
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			} else {

				szLogMsg = "[" + szOperationName + "] 권하지시베드[적치열:" + szYdDnStkColGp + ",적치베드:" + szYdDnStkBedNo + "]의 X,Y 좌표 조회 시작";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recPara  = JDTORecordFactory.getInstance().create();

				recPara.setField("YD_STK_COL_GP", 		szYdDnStkColGp);
				recPara.setField("YD_STK_BED_NO", 		szYdDnStkBedNo);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recPara에 logId 추가 
				recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
									
				intRtnVal = ydStkbedDao.getYdStkbed(recPara, rsResult);		// intGp == 0

				if (intRtnVal <= 0) {
					szRtnMsg = "권하지시베드 조회 오류";
					szLogMsg = "[" + szOperationName + "] 권하지시베드[적치열:" + szYdDnStkColGp + ",적치베드:" + szYdDnStkBedNo + "]의 X,Y 좌표 조회 시 오류발생 - 메세지 : " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
					return szRtnMsg;
				}

				rsResult.first();
				recPara = rsResult.getRecord();

				szDnGrabXValue = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_XAXIS");
				szDnGrabYValue = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_YAXIS");

				szLogMsg	= "[" + szOperationName + "] 권하지시베드[적치열:" + szYdDnStkColGp + ",적치베드:" + szYdDnStkBedNo + "]의 X,Y 좌표 조회 완료";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			}
		}

		//-------------------------------------------------------------------------------------------------------------
		//크레인 스케줄  권하지시위치 업데이트
		//-------------------------------------------------------------------------------------------------------------
		recUpdCrn = JDTORecordFactory.getInstance().create();

		recUpdCrn.setField("YD_CRN_SCH_ID",				szYdCrnSchId						);
		recUpdCrn.setField("MODIFIER",    				szModifier							);
		recUpdCrn.setField("YD_EQP_WRK_SH",    			szYdEqpWrkSh						);
		recUpdCrn.setField("YD_EQP_WRK_WT",    			szYdEqpWrkWt						);
		recUpdCrn.setField("YD_EQP_WRK_T",     			szYdEqpWrkT							);
		recUpdCrn.setField("YD_EQP_WRK_MAX_W", 			szYdEqpWrkMaxW						);
		recUpdCrn.setField("YD_EQP_WRK_MAX_L", 			szYdEqpWrkMaxL						);
		recUpdCrn.setField("YD_CRN_SB_CTL_H", 			szUpCraneGrabUseGp					);		// Grab 사용 구분

		if (!"".equals(szYdUpWoLoc)) {
			recUpdCrn.setField("YD_UP_WO_LOC",   		szYdUpWoLoc							);
			recUpdCrn.setField("YD_UP_WO_LAYER", 		szYdUpWoLayer						);
		}

		recUpdCrn.setField("YD_UP_WO_LOC_XAXIS",   		szUpGrabXValue						);
		recUpdCrn.setField("YD_UP_WO_XAXIS_GAP_MAX",  	String.valueOf(intCraneGapUpX)		);
		recUpdCrn.setField("YD_UP_WO_XAXIS_GAP_MIN",  	String.valueOf(intCraneGapUpX)		);
		recUpdCrn.setField("YD_UP_WO_LOC_YAXIS",   		szUpGrabYValue						);
	//	2후판정정 야드는 계산된 좌표값으로 SET 안함
	//	recUpdCrn.setField("YD_UP_WO_LOC_YAXIS1",  		szUpGrabY1Value);
	//	recUpdCrn.setField("YD_UP_WO_LOC_YAXIS2",  		szUpGrabY2Value);
		recUpdCrn.setField("YD_UP_WO_LOC_YAXIS1",  		szUpGrabYValue						);
		recUpdCrn.setField("YD_UP_WO_LOC_YAXIS2",  		"0"									);
		recUpdCrn.setField("YD_UP_WO_YAXIS_GAP_MAX",  	String.valueOf(intCraneGapUpY)		);
		recUpdCrn.setField("YD_UP_WO_YAXIS_GAP_MIN",  	String.valueOf(intCraneGapUpY)		);
		recUpdCrn.setField("YD_UP_WO_LOC_ZAXIS",  		szYdUpWoLocZaxis					);
		recUpdCrn.setField("YD_UP_WO_ZAXIS_GAP_MAX",  	String.valueOf(intCraneGapUpZ)		);
		recUpdCrn.setField("YD_UP_WO_ZAXIS_GAP_MIN",  	String.valueOf(intCraneGapUpZ)		);

		if (!"".equals(szYdDnWoLoc)) {
			recUpdCrn.setField("YD_DN_WO_LOC",   		szYdDnWoLoc							);
			recUpdCrn.setField("YD_DN_WO_LAYER", 		szYdDnWoLayer						);
		}

		recUpdCrn.setField("YD_DN_WO_LOC_XAXIS",   		szDnGrabXValue						);
		recUpdCrn.setField("YD_DN_WO_XAXIS_GAP_MAX",  	String.valueOf(intCraneGapDnX)		);
		recUpdCrn.setField("YD_DN_WO_XAXIS_GAP_MIN",  	String.valueOf(intCraneGapDnX)		);
		recUpdCrn.setField("YD_DN_WO_LOC_YAXIS",   		szDnGrabYValue						);
	//	2후판정정 야드는 계산된 좌표값으로 SET 안함
	//	recUpdCrn.setField("YD_DN_WO_LOC_YAXIS1",  		szDnGrabY1Value);
	//	recUpdCrn.setField("YD_DN_WO_LOC_YAXIS2",  		szDnGrabY2Value);
		recUpdCrn.setField("YD_DN_WO_LOC_YAXIS1",  		szDnGrabYValue						);
		recUpdCrn.setField("YD_DN_WO_LOC_YAXIS2",  		"0"									);
		recUpdCrn.setField("YD_DN_WO_YAXIS_GAP_MAX",  	String.valueOf(intCraneGapDnY)		);
		recUpdCrn.setField("YD_DN_WO_YAXIS_GAP_MIN",  	String.valueOf(intCraneGapDnY)		);
		recUpdCrn.setField("YD_DN_WO_LOC_ZAXIS",  		szYdDnWoLocZaxis					);
		recUpdCrn.setField("YD_DN_WO_ZAXIS_GAP_MAX",  	String.valueOf(intCraneGapDnZ)		);
		recUpdCrn.setField("YD_DN_WO_ZAXIS_GAP_MIN",  	String.valueOf(intCraneGapDnZ)		);
		recUpdCrn.setField("YD_DN_WRK_ACT_GP",  		szYdDnWrkActGp						);		// 야드권하작업수행구분 : 강제권하

		if ("".equals(szYdToLocGuide)) {
			szYdToLocGuide = szYdDnWoLoc;
		}
		recUpdCrn.setField("YD_TO_LOC_GUIDE",			szYdToLocGuide);

		if ("Y".equals(szYdDnWoFlag)) {
			// 권하위치 변경
			intRtnVal = ydCrnschDao.updDnWoInfo(recUpdCrn);
		} else {
			// 권상 권하 위치 변경
			intRtnVal = ydCrnschDao.updEqpUpDnWoInfo(recUpdCrn);		// intGp == 303
		}

		if (intRtnVal <= 0) {
			if (intRtnVal == 0) {
				szRtnMsg = "[" + szOperationName + "] 크레인 스케줄이 존재하지 않습니다.";
			} else if (intRtnVal == -1) {
				szRtnMsg = "[" + szOperationName + "] 크레인 스케줄이 중복됩니다.";
			} else if (intRtnVal == -2) {
				szRtnMsg = "[" + szOperationName + "] 크레인 스케줄 수정 시 파라미터가 존재하지 않습니다.";
			} else if (intRtnVal == -3) {
				szRtnMsg = "[" + szOperationName + "] 크레인스케줄 수정 시 오류발생 - 반환값 : " + intRtnVal;
			}
		}

		return JPlateYdConst.RETN_CD_SUCCESS;
	}

	/**********************************************************
	* 1후판정정추가 SJH16
	**********************************************************/	

	/**
	 * 1후판 정정 크레인XY좌표수정
	 * @param recInPara
	 * @return
	 * @throws JDTOException
	 */
	public static String uptCrnSchXYCordYdP(JDTORecord recInPara) throws JDTOException {

		JPlateYdStkBedDAO ydStkbedDao = new JPlateYdStkBedDAO();
		JPlateYdCrnSchDAO ydCrnschDao = new JPlateYdCrnSchDAO();

		String szLogMsg				= null;
		String szOperationName		= "1후판 정정크레인XY좌표수정";
		String szMethodName			= "uptCrnSchXYCordYdP";

		String szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;

		JDTORecord    	recUpdCrn	= null;
		JDTORecord		recPara 	= null;
		JDTORecordSet 	rsResult	= null;

		String 	szYdCrnSchId		= null;
		String 	szYdEqpId			= null;
		String 	szYdUpStkColGp		= null;
		String 	szYdUpStkBedNo		= null;
		String 	szYdDnStkColGp		= null;
		String 	szYdDnStkBedNo		= null;

		String 	szYdUpWoLoc			= null;
		String 	szYdUpWoLayer		= null;
		String 	szYdDnWoLoc			= null;
		String 	szYdDnWoLayer		= null;

		String 	szYdEqpWrkSh		= null;
		String 	szYdEqpWrkWt		= null;
		String 	szYdEqpWrkT			= null;
		String 	szYdEqpWrkMaxW		= null;
		String 	szYdEqpWrkMaxL		= null;
		String	szModifier			= null;

		int    	intCraneGapUpX		= JPlateYdConst.PPLATE_CRANE_GAP_X;
		int    	intCraneGapUpY		= JPlateYdConst.PPLATE_CRANE_GAP_Y;
		int    	intCraneGapUpZ		= JPlateYdConst.PPLATE_CRANE_GAP_Z;
		int    	intCraneGapDnX		= JPlateYdConst.PPLATE_CRANE_GAP_X;
		int    	intCraneGapDnY		= JPlateYdConst.PPLATE_CRANE_GAP_Y;
		int    	intCraneGapDnZ		= JPlateYdConst.PPLATE_CRANE_GAP_Z;

		String 	szUpCraneGrabUseGp 	= "";
		String 	szUpGrabXValue     	= "";
		String 	szUpGrabYValue     	= "";
		String 	szDnGrabXValue     	= "";
		String 	szDnGrabYValue     	= "";

		String 	szYdUpWoLocZaxis	= "";
		String 	szYdDnWoLocZaxis	= "";
		String	szYdDnWoFlag		= "N";		// 권하위치만 변경 (강제권하시)
		String	szYdToLocGuide		= "";
		String	szYdDnWrkActGp		= "";		//

		int     intRtnVal			= 0;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(recInPara, "P");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		//-------------------------------------------------------------------------------------------------------------
		//	파라미터확인
		//-------------------------------------------------------------------------------------------------------------
		szLogMsg = "[" + szOperationName + "] -------------------------- 메소드 시작 - 파라미터확인 :: " + recInPara.toString();
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szYdDnWoFlag 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_WO_FLAG", "N");		// 권하위치 변경 FLAG
		szYdCrnSchId 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_CRN_SCH_ID");
		szYdEqpId 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID");

		szYdUpStkColGp 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_STK_COL_GP");
		szYdUpStkBedNo 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_STK_BED_NO");
		szYdDnStkColGp 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_STK_COL_GP");
		szYdDnStkBedNo 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_STK_BED_NO");

		szYdUpWoLoc		= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_WO_LOC");
		szYdUpWoLayer	= ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_WO_LAYER");
		szYdDnWoLoc		= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_WO_LOC");
		szYdDnWoLayer	= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_WO_LAYER");
		szYdDnWrkActGp	= ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_WRK_ACT_GP");

		if ("".equals(szYdUpStkColGp)) {
			szYdUpStkColGp 	= ydUtils.substr(szYdUpWoLoc, 0, 6);
			szYdUpStkBedNo 	= ydUtils.substr(szYdUpWoLoc, 6, 2);
		}
		if ("".equals(szYdDnStkColGp)) {
			szYdDnStkColGp 	= ydUtils.substr(szYdDnWoLoc, 0, 6);
			szYdDnStkBedNo 	= ydUtils.substr(szYdDnWoLoc, 6, 2);
		}

		szYdEqpWrkSh	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");
		szYdEqpWrkWt	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");
		szYdEqpWrkT		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");
		szYdEqpWrkMaxW	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_MAX_W");
		szYdEqpWrkMaxL	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_MAX_L");
		szModifier		= ydDaoUtils.paraRecModifier(recInPara);

		//-------------------------------------------------------------------------------------------------------------
		//	크레인 허용 오차 및 크레인 X, Y좌표 계산
		//-------------------------------------------------------------------------------------------------------------
		//----------------------------------------------------------------
		//	권상 시 크레인 허용오차
		//----------------------------------------------------------------
		intCraneGapUpX = ydUtils.getCraneGapXYdP(szYdUpStkColGp);
		intCraneGapUpY = ydUtils.getCraneGapYYdP(szYdUpStkColGp);
		intCraneGapUpZ = JPlateYdConst.PPLATE_CRANE_GAP_Z;

		//----------------------------------------------------------------
		//	권하 시 크레인 허용오차
		//----------------------------------------------------------------
		intCraneGapDnX = ydUtils.getCraneGapXYdP(szYdDnStkColGp);
		intCraneGapDnY = ydUtils.getCraneGapYYdP(szYdDnStkColGp);
		intCraneGapDnZ = JPlateYdConst.PPLATE_CRANE_GAP_Z;

		szLogMsg = "[" + szOperationName + "] 허용오차  . 권상:: " + intCraneGapUpX + "," + intCraneGapUpY + ", 권하:: " + intCraneGapDnX + "," + intCraneGapDnY;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szLogMsg = "[" + szOperationName + "] -------------------------- 1후판정정야드 X,Y 좌표계산 시작 -------------------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		recPara 			 = JDTORecordFactory.getInstance().create();
		JDTORecord recResult = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_CRN_SCH_ID", 		szYdCrnSchId);
		recPara.setField("YD_EQP_ID", 			szYdEqpId);
		recPara.setField("YD_UP_STK_COL_GP", 	szYdUpStkColGp);
		recPara.setField("YD_UP_STK_BED_NO", 	szYdUpStkBedNo);
		recPara.setField("YD_DN_STK_COL_GP", 	szYdDnStkColGp);
		recPara.setField("YD_DN_STK_BED_NO", 	szYdDnStkBedNo);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 recSchPara에 logId 추가 
		recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

		JPlateYdGdsUtil.procXYCalForPlateCraneYdP(recPara, recResult);

		szUpCraneGrabUseGp	= ydDaoUtils.paraRecChkNull(recResult, "CRANE_GRAB_USE_GP");
		szUpGrabXValue     	= ydDaoUtils.paraRecChkNull(recResult, "UP_GRAB_X_VALUE");
		szUpGrabYValue     	= ydDaoUtils.paraRecChkNull(recResult, "UP_GRAB_Y_VALUE");
		szDnGrabXValue     	= ydDaoUtils.paraRecChkNull(recResult, "DN_GRAB_X_VALUE");
		szDnGrabYValue     	= ydDaoUtils.paraRecChkNull(recResult, "DN_GRAB_Y_VALUE");

		szLogMsg = "[" + szOperationName + "] -------------------------- 1후판정정야드 X,Y 좌표계산 완료 -------------------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		if (!"".equals(szYdUpStkColGp)) {
			//------------------------------------------------------------------------------------------------------
			//	권상지시베드의 X,Y 좌표 조회
			//------------------------------------------------------------------------------------------------------
			szLogMsg = "[" + szOperationName + "] 권상지시베드[적치열:" + szYdUpStkColGp + ",적치베드:" + szYdUpStkBedNo + "]의 X,Y 좌표 조회 시작";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_STK_COL_GP", 		szYdUpStkColGp);
			recPara.setField("YD_STK_BED_NO", 		szYdUpStkBedNo);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 recPara에 logId 추가 
			recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
			intRtnVal = ydStkbedDao.getYdStkbed(recPara, rsResult);		// intGp = 0

			if (intRtnVal <= 0) {
				szRtnMsg = "권상지시베드 조회 오류";
				szLogMsg = "[" + szOperationName + "] 권상지시베드[적치열:" + szYdUpStkColGp + ",적치베드:" + szYdUpStkBedNo + "]의 X,Y 좌표 조회 시 오류발생 - 메세지 : " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
				return szRtnMsg;
			}

			rsResult.first();
			recPara = rsResult.getRecord();

			szUpGrabXValue = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_XAXIS");
			szUpGrabYValue = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_YAXIS");

			szLogMsg = "[" + szOperationName + "] 권상지시베드[적치열:" + szYdUpStkColGp + ",적치베드:" + szYdUpStkBedNo + "]의 X,Y 좌표 조회 완료";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		}

		if (!"".equals(szYdDnStkColGp)) {
			//------------------------------------------------------------------------------------------------------
			//	권하지시베드의 X,Y 좌표 조회
			//------------------------------------------------------------------------------------------------------
			if ("XX".equals(ydUtils.substr(szYdDnStkColGp, 0, 2))) {

				szDnGrabXValue = "00000";
				szDnGrabYValue = "00000";

				szLogMsg	= "[" + szOperationName + "] 권하지시 위치 XX 일때 SKIP 처리 >>>>" + szYdDnStkColGp;
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			} else {

				szLogMsg = "[" + szOperationName + "] 권하지시베드[적치열:" + szYdDnStkColGp + ",적치베드:" + szYdDnStkBedNo + "]의 X,Y 좌표 조회 시작";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recPara  = JDTORecordFactory.getInstance().create();

				recPara.setField("YD_STK_COL_GP", 		szYdDnStkColGp);
				recPara.setField("YD_STK_BED_NO", 		szYdDnStkBedNo);


//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 recPara에 logId 추가 
				recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
				intRtnVal = ydStkbedDao.getYdStkbed(recPara, rsResult);		// intGp == 0

				if (intRtnVal <= 0) {
					szRtnMsg = "권하지시베드 조회 오류";
					szLogMsg = "[" + szOperationName + "] 권하지시베드[적치열:" + szYdDnStkColGp + ",적치베드:" + szYdDnStkBedNo + "]의 X,Y 좌표 조회 시 오류발생 - 메세지 : " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
					return szRtnMsg;
				}

				rsResult.first();
				recPara = rsResult.getRecord();

				szDnGrabXValue = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_XAXIS");
				szDnGrabYValue = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_YAXIS");

				szLogMsg	= "[" + szOperationName + "] 권하지시베드[적치열:" + szYdDnStkColGp + ",적치베드:" + szYdDnStkBedNo + "]의 X,Y 좌표 조회 완료";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			}
		}

		//-------------------------------------------------------------------------------------------------------------
		//크레인 스케줄  권하지시위치 업데이트
		//-------------------------------------------------------------------------------------------------------------
		recUpdCrn = JDTORecordFactory.getInstance().create();

		recUpdCrn.setField("YD_CRN_SCH_ID",				szYdCrnSchId);
		recUpdCrn.setField("MODIFIER",    				szModifier);
		recUpdCrn.setField("YD_EQP_WRK_SH",    			szYdEqpWrkSh);
		recUpdCrn.setField("YD_EQP_WRK_WT",    			szYdEqpWrkWt);
		recUpdCrn.setField("YD_EQP_WRK_T",     			szYdEqpWrkT);
		recUpdCrn.setField("YD_EQP_WRK_MAX_W", 			szYdEqpWrkMaxW);
		recUpdCrn.setField("YD_EQP_WRK_MAX_L", 			szYdEqpWrkMaxL);
		recUpdCrn.setField("YD_CRN_SB_CTL_H", 			szUpCraneGrabUseGp);			//Grab 사용 구분

		if (!"".equals(szYdUpWoLoc)) {
			recUpdCrn.setField("YD_UP_WO_LOC",   		szYdUpWoLoc);
			recUpdCrn.setField("YD_UP_WO_LAYER", 		szYdUpWoLayer);
		}

		recUpdCrn.setField("YD_UP_WO_LOC_XAXIS",   		szUpGrabXValue);
		recUpdCrn.setField("YD_UP_WO_XAXIS_GAP_MAX",  	String.valueOf(intCraneGapUpX));
		recUpdCrn.setField("YD_UP_WO_XAXIS_GAP_MIN",  	String.valueOf(intCraneGapUpX));
		recUpdCrn.setField("YD_UP_WO_LOC_YAXIS",   		szUpGrabYValue);
	//	2후판정정 야드는 계산된 좌표값으로 SET 안함
	//	recUpdCrn.setField("YD_UP_WO_LOC_YAXIS1",  		szUpGrabY1Value);
	//	recUpdCrn.setField("YD_UP_WO_LOC_YAXIS2",  		szUpGrabY2Value);
		recUpdCrn.setField("YD_UP_WO_LOC_YAXIS1",  		szUpGrabYValue);
		recUpdCrn.setField("YD_UP_WO_LOC_YAXIS2",  		"0");
		recUpdCrn.setField("YD_UP_WO_YAXIS_GAP_MAX",  	String.valueOf(intCraneGapUpY));
		recUpdCrn.setField("YD_UP_WO_YAXIS_GAP_MIN",  	String.valueOf(intCraneGapUpY));
		recUpdCrn.setField("YD_UP_WO_LOC_ZAXIS",  		szYdUpWoLocZaxis);
		recUpdCrn.setField("YD_UP_WO_ZAXIS_GAP_MAX",  	String.valueOf(intCraneGapUpZ));
		recUpdCrn.setField("YD_UP_WO_ZAXIS_GAP_MIN",  	String.valueOf(intCraneGapUpZ));

		if (!"".equals(szYdDnWoLoc)) {
			recUpdCrn.setField("YD_DN_WO_LOC",   		szYdDnWoLoc);
			recUpdCrn.setField("YD_DN_WO_LAYER", 		szYdDnWoLayer);
		}

		recUpdCrn.setField("YD_DN_WO_LOC_XAXIS",   		szDnGrabXValue);
		recUpdCrn.setField("YD_DN_WO_XAXIS_GAP_MAX",  	String.valueOf(intCraneGapDnX));
		recUpdCrn.setField("YD_DN_WO_XAXIS_GAP_MIN",  	String.valueOf(intCraneGapDnX));
		recUpdCrn.setField("YD_DN_WO_LOC_YAXIS",   		szDnGrabYValue);
	//	2후판정정 야드는 계산된 좌표값으로 SET 안함
	//	recUpdCrn.setField("YD_DN_WO_LOC_YAXIS1",  		szDnGrabY1Value);
	//	recUpdCrn.setField("YD_DN_WO_LOC_YAXIS2",  		szDnGrabY2Value);
		recUpdCrn.setField("YD_DN_WO_LOC_YAXIS1",  		szDnGrabYValue);
		recUpdCrn.setField("YD_DN_WO_LOC_YAXIS2",  		"0");
		recUpdCrn.setField("YD_DN_WO_YAXIS_GAP_MAX",  	String.valueOf(intCraneGapDnY));
		recUpdCrn.setField("YD_DN_WO_YAXIS_GAP_MIN",  	String.valueOf(intCraneGapDnY));
		recUpdCrn.setField("YD_DN_WO_LOC_ZAXIS",  		szYdDnWoLocZaxis);
		recUpdCrn.setField("YD_DN_WO_ZAXIS_GAP_MAX",  	String.valueOf(intCraneGapDnZ));
		recUpdCrn.setField("YD_DN_WO_ZAXIS_GAP_MIN",  	String.valueOf(intCraneGapDnZ));
		recUpdCrn.setField("YD_DN_WRK_ACT_GP",  		szYdDnWrkActGp);					// 야드권하작업수행구분 : 강제권하

		if ("".equals(szYdToLocGuide)) {
			szYdToLocGuide = szYdDnWoLoc;
		}
		recUpdCrn.setField("YD_TO_LOC_GUIDE",			szYdToLocGuide);

		if ("Y".equals(szYdDnWoFlag)) {
			// 권하위치 변경
			intRtnVal = ydCrnschDao.updDnWoInfo(recUpdCrn);
		} else {
			// 권상 권하 위치 변경
			intRtnVal = ydCrnschDao.updEqpUpDnWoInfo(recUpdCrn);		// intGp == 303
		}
 
		if (intRtnVal <= 0) {
			if (intRtnVal == 0) {
				szRtnMsg = "[" + szOperationName + "] 크레인 스케줄이 존재하지 않습니다.";
			} else if (intRtnVal == -1) {
				szRtnMsg = "[" + szOperationName + "] 크레인 스케줄이 중복됩니다.";
			} else if (intRtnVal == -2) {
				szRtnMsg = "[" + szOperationName + "] 크레인 스케줄 수정 시 파라미터가 존재하지 않습니다.";
			} else if (intRtnVal == -3) {
				szRtnMsg = "[" + szOperationName + "] 크레인스케줄 수정 시 오류발생 - 반환값 : " + intRtnVal;
			}
		}

		return JPlateYdConst.RETN_CD_SUCCESS;
	}	
		
	
}