/*
 * @(#) 2후판정정야드 TO위치결정하는 클래스
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/12/13
 *
 * @description		2후판정정야드 TO위치결정하는 클래스
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/12/13   김현우      김현우       최초작성
 */

package com.inisteel.cim.yd.jplateyd.util;

import java.util.ArrayList;
import java.util.List;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.DateHelper;

import edu.emory.mathcs.backport.java.util.Collections;

import com.inisteel.cim.yd.common.rule.GetBreRule8;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 1후판 정정 2열처리 Book-In/Book-Out 관련 야드공통 UTIL (putLogNew, getLogIdNew, isEmpty, getJDTOLogId) 부분  
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

public class JPlateYdToLocUtil {

	private static final String SZ_CLASS_NAME 	= JPlateYdToLocUtil.class.getName();
	private static JPlateYdUtils 	ydUtils 	= new JPlateYdUtils();
	private static JPlateYdDaoUtils	ydDaoUtils 	= new JPlateYdDaoUtils();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 1후판 정정 2열처리 Book-In/Book-Out 관련 야드공통 UTIL (putLogNew, getLogIdNew, isEmpty, getJDTOLogId) 부분 
//-------------------------------------------------------------------------------------------------------------------------
    private static YdUtils 			ydLogUtils  = new YdUtils();
	
	//------------------------------------------------------------------------------------------------------------------------------------
	//	RT상으로 위치 결정 - BOOT-IN
	//------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * RT상TO위치결정(2후판정정)
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @return
	 * @throws JDTOException
	 */
	public static String procRtToLocForPlateYd(JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch) throws JDTOException {

		JPlateYdStockDAO   ydStockDao 	= new JPlateYdStockDAO();
		JPlateYdStkLyrDAO  ydStklyrDao 	= new JPlateYdStkLyrDAO();

		String szMethodName			= "procRtToLocForPlateYd";
		String szOperationName		= "RT상TO위치결정(2후판정정)";
		String szLogMsg				= null;
		String szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		ArrayList	listToLoc		= null;

//		JDTORecord	recLogMsg		= null;
		JDTORecord	recPara			= null;
		JDTORecord	recTemp			= null;
		JDTORecord	recTemp2		= null;
		JDTORecord	recUpCrnSch		= null;
		JDTORecordSet rsResult		= null;

		String 	szStlNo				= null;
//		String[] szYdStkLyrMtlStat	= {"D", "U"};
		String 	szYdUpWoLoc			= null;
		String 	szYdUpWoLayer		= null;
		String 	szYdDnWoLoc			= null;
		String 	szYdDnWoLayer		= null;
		String 	szYdUpStkColGp		= null;
		String 	szYdUpStkBedNo		= null;
		String 	szYdDnStkColGp		= null;
		String 	szYdDnStkBedNo		= null;
		String 	szYdMtlLGp			= null;
		String 	szYdSchCd			= null;
		String	szYdCrnSchId		= null;
		String 	szYdEqpId			= null;
		String	szYdToLocGuide		= null;
		String	szModifier			= null;

		int    	intYdEqpWrkSh		= 0;						//야드설비작업매수
		int    	intYdEqpWrkWt		= 0;						//야드설비작업중량
		double 	dblYdEqpWrkT		= 0;						//야드설비작업총두께
		int		intYdEqpWrkL		= 0;						//야드설비작업총길이
		String 	szYdEqpWrkMaxW		= null;						//작업재료 중 최대 폭
		String 	szYdEqpWrkMaxL		= null;						//작업재료 중 최대 길이
		int    	intRtnVal 			= 0;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 로그 개선 
	    String logId                     	= ydLogUtils.getJDTOLogId(recCrnSch, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
	
	    if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 		// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
		      		
	    szLogMsg = "[" + szOperationName + "] ---- recCrnSch.toString()  \n>>>> " + recCrnSch.toString();
	    ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
//-------------------------------------------------------------------------------------------------------------------------

		szLogMsg = "[" + szOperationName + "] 메소드 시작 - 크레인스케줄확인 ";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료의 최하단 재료정보를 가져온다.
		//----------------------------------------------------------------------------------------------------------------------
		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();

		szLogMsg = "[" + szOperationName + "] -------------------- 크레인작업재료의 최하단 재료정보 확인 --------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		ydUtils.displayRecord(szOperationName, recPara);

		szLogMsg = "[" + szOperationName + "] -------------------- 크레인스케줄정보 확인 --------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		ydUtils.displayRecord(szOperationName, recCrnSch);

		szYdCrnSchId    = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID");
		szYdEqpId      	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_EQP_ID");
		szYdToLocGuide	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_TO_LOC_GUIDE");

		intYdEqpWrkSh   = ydDaoUtils.paraRecChkNullInt(recPara, "SH_CNT");
		intYdEqpWrkWt   = ydDaoUtils.paraRecChkNullInt(recPara, "SUM_MTL_WT");
		dblYdEqpWrkT    = ydDaoUtils.paraRecChkNullDouble(recPara, "SUM_MTL_T");
		intYdEqpWrkL    = ydDaoUtils.paraRecChkNullInt(recPara, "MAX_MTL_L");		// "SUM_MTL_L"

		szYdEqpWrkMaxW	= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_W");
		szYdEqpWrkMaxL	= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_L");

		szModifier		= ydDaoUtils.paraRecModifier(recPara);

		szLogMsg = "[" + szOperationName + "] 크레인작업재료의 총매수 :" + intYdEqpWrkSh;
		szLogMsg += ", 총중량 :" + intYdEqpWrkWt;
		szLogMsg += ", 총높이  :" + dblYdEqpWrkT;
		szLogMsg += ", TO위치 :" + szYdToLocGuide;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		//	최하단 재료를 저장품으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szStlNo	= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

		szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보["+szStlNo+"]를 저장품에서 조회 시작 ";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
		intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsResult);

		if (intRtnVal == 0) {
			szRtnMsg = "["+szOperationName+"] 존재하지 않습니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szRtnMsg, JPlateYdConst.ERROR, logId);
			
			return szRtnMsg;
		} else if (intRtnVal == -2) {
			szRtnMsg = "["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szRtnMsg, JPlateYdConst.ERROR, logId);
			
			return szRtnMsg;
		} else if (intRtnVal < 0) {
			szRtnMsg = "["+szOperationName+"] 오류발생";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szRtnMsg, JPlateYdConst.ERROR, logId);
			
			return szRtnMsg;
		}

		rsResult.first();
		recTemp = rsResult.getRecord();

		szYdMtlLGp = ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L_GP");

		szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보["+szStlNo+"]를 저장품에서 조회 완료 - 길이구분["+szYdMtlLGp+"]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szYdUpStkColGp = recTemp.getFieldString("YD_STK_COL_GP");
		szYdUpStkBedNo = recTemp.getFieldString("YD_STK_BED_NO");
		szYdUpWoLoc    = szYdUpStkColGp + szYdUpStkBedNo;
		szYdUpWoLayer  = recTemp.getFieldString("YD_STK_LYR_NO");

		szLogMsg = "[" + szOperationName + "] 권상지시위치["+szYdUpWoLoc+"], 권상지시단["+szYdUpWoLayer+"]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

    	//----------------------------------------------------------------------------------------------------------------------
    	//	권상지시위치의 정보를 분석해서 RT상의 위치 구하기
    	//----------------------------------------------------------------------------------------------------------------------
    	szLogMsg = "[" + szOperationName + "] >>>> RT상의 베드번호 추출 시작 :: " + szYdToLocGuide;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

    	szYdSchCd = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD");

    	szYdDnStkColGp = ydUtils.substr(szYdToLocGuide, 0, 6);
    	if ("".equals(szYdDnStkColGp) || szYdDnStkColGp.length() < 6) {
    		szYdDnStkColGp = szYdSchCd.substring(0, 6);
    	}

    	szYdDnStkBedNo = JPlateYdToLocUtil.getRtStkLocByBedNoMtlLGp(szYdDnStkColGp, "01", szYdMtlLGp);

    	szLogMsg = "[" + szOperationName + "] >>>> RT상의 베드번호 추출 완료 - 권하지시적치열["+szYdDnStkColGp+"], 권하지시베드["+szYdDnStkBedNo+"]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

    	//----------------------------------------------------------------------------------------------------------------------
    	//	해당베드정보에 적치가능한 지를 체크
    	//YD_TO_LOC_GUIDE	- 사용자지정위치(적치열+적치베드)
		// * 				2) YD_EQP_WRK_SH	- 작업총매수
		// * 				3) YD_EQP_WRK_WT	- 작업총중량
		// * 				4) YD_EQP_WRK_T		- 작업총두께
		// * 				5) YD_EQP_WRK_L		- 작업총길이
		// * 				6) YD_SCH_CD		- 스케줄코드
    	//----------------------------------------------------------------------------------------------------------------------

		szLogMsg = "[" + szOperationName + "] 권하지시적치열["+szYdDnStkColGp+"], 권하지시베드["+szYdDnStkBedNo+"]에 적치가능한 지 비교 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

    	listToLoc = new ArrayList();

    	recTemp = JDTORecordFactory.getInstance().create();
    	recTemp.setField("YD_TO_LOC_GUIDE", 	szYdDnStkColGp + szYdDnStkBedNo);
    	recTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(intYdEqpWrkSh));
    	recTemp.setField("YD_EQP_WRK_WT", 		String.valueOf(intYdEqpWrkWt));
    	recTemp.setField("YD_EQP_WRK_T", 		String.valueOf(dblYdEqpWrkT));
    	recTemp.setField("YD_EQP_WRK_L", 		String.valueOf(intYdEqpWrkL));
    	recTemp.setField("YD_SCH_CD", 			szYdSchCd);
    	recTemp.setField("MODIFIER",			szModifier);

    	szRtnMsg = JPlateYdToLocUtil.procAsgnedBedStackable(recTemp, listToLoc, szMethodName);

    	int 	intYdBedErrCd	= 0;

    	if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {

    		JPlateYdStkLocVO ydStkLocVO = (JPlateYdStkLocVO)listToLoc.get(0);

    		intYdBedErrCd	= ydStkLocVO.getYdBedErrCd();

    		if (intYdBedErrCd == JPlateYdConst.YD_BED_STACKABLE) {

	    		szYdDnWoLoc 	= ydStkLocVO.getYdStkColGp() + ydStkLocVO.getYdStkBedNo();
	    		szYdDnWoLayer	= ydStkLocVO.getYdStkLyrNo();

	    		//----------------------------------------------------------------------------------------------------------------------
	    		//	적치단의 권하지시위치에 재료별 권하상태로 수정
	    		//----------------------------------------------------------------------------------------------------------------------
	    		recTemp = JDTORecordFactory.getInstance().create();
	    		String szYdDnWoLayer2		= "";
	    		for(int ii=1; ii<=rsCrnwrkmtl.size(); ii++) {
	    			rsCrnwrkmtl.absolute(ii);
	    			recTemp2 = rsCrnwrkmtl.getRecord();

	    			szStlNo = ydDaoUtils.paraRecChkNull(recTemp2, "STL_NO");
	    			szYdDnWoLayer2 = ydDaoUtils.stringPlusInt(szYdDnWoLayer, (ii-1));

	    	    	recTemp.setField("STL_NO", 					szStlNo);
	    	    	recTemp.setField("MODIFIER", 				szModifier);
	    	    	recTemp.setField("YD_STK_COL_GP", 			ydStkLocVO.getYdStkColGp());
	    	    	recTemp.setField("YD_STK_BED_NO", 			ydStkLocVO.getYdStkBedNo());
	    	    	recTemp.setField("YD_STK_LYR_NO", 			szYdDnWoLayer2);
	    	    	recTemp.setField("YD_STK_LYR_MTL_STAT", 	JPlateYdConst.YD_STK_LYR_MTL_STAT_DN_WAIT);

	    	    	szLogMsg = "[" + szOperationName + "] 권하지시위치["+szYdDnWoLoc+"], 권하지시단["+szYdDnWoLayer2+"]에 재료["+szStlNo+"] 등록 시작";
	        		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

	    	    	intRtnVal = ydStklyrDao.updYdStklyrStat(recTemp);	// intGp == 0

	    	    	szLogMsg = "[" + szOperationName + "] 권하지시위치["+szYdDnWoLoc+"], 권하지시단["+szYdDnWoLayer2+"]에 재료["+szStlNo+"] 등록 완료 - 메세지 : " + Integer.toString(intRtnVal);
	        		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

	    		}

	    		szLogMsg = "[" + szOperationName + "] 권하지시위치["+szYdDnWoLoc+"], 권하지시단["+szYdDnWoLayer+"]을 크레인스케줄에 수정 시작";
	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

	    		recUpCrnSch = JDTORecordFactory.getInstance().create();
	    		recUpCrnSch.setField("YD_CRN_SCH_ID", 			szYdCrnSchId);
	    		recUpCrnSch.setField("YD_EQP_ID", 				szYdEqpId);

	    		recUpCrnSch.setField("YD_UP_STK_COL_GP", 		szYdUpStkColGp);
				recUpCrnSch.setField("YD_UP_STK_BED_NO", 		szYdUpStkBedNo);
				recUpCrnSch.setField("YD_DN_STK_COL_GP", 		szYdDnStkColGp);
				recUpCrnSch.setField("YD_DN_STK_BED_NO", 		szYdDnStkBedNo);

				//----------------------------------------------------------------------------------------------------------------------
				//	TO위치결정방법이 T인 경우에는 크레인스케줄의 권상지시위치 업데이트
				//----------------------------------------------------------------------------------------------------------------------

	    		if ("T".equals(recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD"))) {
	    			szLogMsg = "[" + szOperationName + "] TO위치결정방법이 T인 경우에는 권상지시위치["+szYdUpWoLoc+"], 권상지시단["+szYdUpWoLayer+"] 크레인스케줄에 등록";
	        		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

	    			recUpCrnSch.setField("YD_UP_WO_LOC", 	szYdUpWoLoc);
	    			recUpCrnSch.setField("YD_UP_WO_LAYER", 	szYdUpWoLayer);
	    		}

	    		//----------------------------------------------------------------------------------------------------------------------

	    		recUpCrnSch.setField("YD_DN_WO_LOC", 		szYdDnWoLoc);
	    		recUpCrnSch.setField("YD_DN_WO_LAYER", 		szYdDnWoLayer);

	    		recUpCrnSch.setField("YD_EQP_WRK_SH", 		String.valueOf(intYdEqpWrkSh));
	    		recUpCrnSch.setField("YD_EQP_WRK_WT", 		String.valueOf(intYdEqpWrkWt));
	    		recUpCrnSch.setField("YD_EQP_WRK_T", 		String.valueOf(dblYdEqpWrkT));
	    		recUpCrnSch.setField("YD_EQP_WRK_L", 		String.valueOf(intYdEqpWrkL));
	    		recUpCrnSch.setField("YD_EQP_WRK_MAX_W", 	szYdEqpWrkMaxW);
	    		recUpCrnSch.setField("YD_EQP_WRK_MAX_L",	szYdEqpWrkMaxL);

	    		recUpCrnSch.setField("MODIFIER", 			(szMethodName.length() > 10) ? szMethodName.substring(0, 10) : szMethodName);
	    		
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recUpCrnSch에 logId 추가 
	    		recUpCrnSch.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
	    		
	    		szRtnMsg = JPlateYdCrnSchUtil.uptCrnSchXYCord(recUpCrnSch);

	        	szLogMsg = "[" + szOperationName + "] 권하지시위치["+szYdDnWoLoc+"], 권하지시단["+szYdDnWoLayer+"]을 크레인스케줄에 수정 완료";
	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

    		} else {
    			/*
    			//--------------------------------------------------------------------------------------------
    			//	해당베드정보에 적치가능한 지를 체크 후 적치불가능이면 로그메세지 전송
    			//--------------------------------------------------------------------------------------------
    			recLogMsg = JDTORecordFactory.getInstance().create();
    			recLogMsg.setField("JMS_TC_CD", 			JPlateYdConst.YDYDJ702);
    			recLogMsg.setField("YD_GP", 				szYdEqpId.substring(0, 1));
    			recLogMsg.setField("MONITORING_CHANNEL", 	"yd_monitorA");
    			recLogMsg.setField("YD_BAY_GP", 			szYdEqpId.substring(1, 2));
    			recLogMsg.setField("YD_EQP_ID", 			szYdEqpId);
    			recLogMsg.setField("YD_SCH_CD", 			szYdSchCd);
    			recLogMsg.setField("YD_EVT_GP", 			JPlateYdConst.YD_EVT_CRANE);
    			recLogMsg.setField("YD_MSG_OUTPWR_GRD",		"E");
    			recLogMsg.setField("YD_PGM_TP", 			JPlateYdConst.YD_PGMGP_SCH);
    			recLogMsg.setField("YD_IF_CD", 				"");
    			recLogMsg.setField("YD_E_J_B_ID", 			SZ_CLASS_NAME);
    			recLogMsg.setField("YD_MSG_NM", 			szMethodName);

    			JPlateYdCommonUtils.sndLogMsgForBedStatusInfo(intYdBedErrCd, szYdCrnSchId, szYdDnStkColGp, szYdDnStkBedNo, recLogMsg);
    			*/
    			//--------------------------------------------------------------------------------------------
    			szRtnMsg = "권하지시적치열["+szYdDnStkColGp+"], 권하지시베드["+szYdDnStkBedNo+"]에 적치불가능 - 베드에러코드["+intYdBedErrCd+"]";
    			szLogMsg = "[" + szOperationName + "] " + szRtnMsg;
    			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
    			
    			return szRtnMsg;
    		}

    	} else {
    		/*
    		//--------------------------------------------------------------------------------------------
			//	해당베드정보에 적치가능한 지를 체크 후 적치불가능이면 로그메세지 전송
			//--------------------------------------------------------------------------------------------
    		recLogMsg = JDTORecordFactory.getInstance().create();
    		recLogMsg.setField("JMS_TC_CD", 				JPlateYdConst.YDYDJ702);
    		recLogMsg.setField("YD_GP", 					szYdEqpId.substring(0, 1));
    		recLogMsg.setField("MONITORING_CHANNEL", 		"yd_monitorA");
    		recLogMsg.setField("YD_BAY_GP", 				szYdEqpId.substring(1, 2));
    		recLogMsg.setField("YD_EQP_ID", 				szYdEqpId);
    		recLogMsg.setField("YD_SCH_CD", 				szYdSchCd);
    		recLogMsg.setField("YD_EVT_GP", 				JPlateYdConst.YD_EVT_CRANE);
    		recLogMsg.setField("YD_MSG_OUTPWR_GRD", 		"E");
    		recLogMsg.setField("YD_PGM_TP", 				JPlateYdConst.YD_PGMGP_SCH);
    		recLogMsg.setField("YD_IF_CD", 					"");
    		recLogMsg.setField("YD_E_J_B_ID", 				SZ_CLASS_NAME);
    		recLogMsg.setField("YD_MSG_NM", 				szMethodName);

    		JPlateYdCommonUtils.sndLogMsgForBedInfo(szRtnMsg, szYdCrnSchId, szYdDnStkColGp, szYdDnStkBedNo, recLogMsg);
			*/
    		szRtnMsg = "권하지시적치열["+szYdDnStkColGp+"], 권하지시베드["+szYdDnStkBedNo+"]에 적치불가능 - 에러메세지["+szRtnMsg+"]";
    		szLogMsg = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			
			return szRtnMsg;
    	}

    	szLogMsg = "[" + szOperationName + "] 권하지시적치열["+szYdDnStkColGp+"], 권하지시베드["+szYdDnStkBedNo+"]에 적치가능한 지 비교 완료";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}

	/**
	 * RT상의 베드구하기
	 * @param szYdUpStkColGp
	 * @param szYdUpStkBedNo
	 * @param szYdMtlLGp
	 * @return
	 */
	public static String getRtStkLocByBedNoMtlLGp(String szYdDnStkColGp, String szYdDnStkBedNo, String szYdMtlLGp) {

		String 	szYdStkBedNo 		= "01";
		int		intRtnVal			= 0;

		String 	szMethodName		= "getRtStkLocByBedNoMtlLGp";
		String 	szOperationName		= "RT상의 베드구하기";
		String 	szLogMsg			= "";

		// 레코드 선언
		JDTORecordSet 	rsResult  	= null;
		JDTORecord 		recPara 	= null;
		JDTORecord 		recOut		= null;

		JPlateYdStkLyrDAO ydStkLyrDao	= new JPlateYdStkLyrDAO();

		try {

			// 2013.07.26 해당 저장위치의 베드 정보를 조회하여 베드정보 RETURN하도록 보완
			szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터확인 : 권하적치열구분["+szYdDnStkColGp+"], 권하베드번호["+szYdDnStkBedNo+"], 재료길이구분["+szYdMtlLGp+"]";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			rsResult = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			recPara  = JDTORecordFactory.getInstance().create();
			recOut 	 = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP",  szYdDnStkColGp);

			intRtnVal = ydStkLyrDao.getEmptyToLoc(recPara, rsResult);
			if (intRtnVal <= 0) {
				szLogMsg = "[" + szOperationName + "] 해당 RT에 적치가능한 저장위치가 없습니다!" + szYdDnStkColGp;
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			}

			rsResult.first();
			recOut = rsResult.getRecord();
	        szYdStkBedNo = ydDaoUtils.paraRecChkNull(recOut, "YD_STK_BED_NO");    			// RT 야드적치BED번호

			szLogMsg = "["+ szOperationName +"] RT상 베드번호["+szYdStkBedNo+"]";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			szLogMsg = "["+ szOperationName +"] 메소드 끝";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		} catch(Exception e) {
			szLogMsg = "["+szOperationName+"] Exception Error :: " + e.getMessage();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
		//	throw new DAOException(szLogMsg);
		} // end of try-catch

		return szYdStkBedNo;
	}

	/**
	 * 주작업TO위치결정(2후판정정)
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public static String procMainWrkToLocForPlateYd(JDTORecord msgRecord					/* 전문 */
													, JDTORecordSet rsCrnwrkmtl				/* 크레인작업재료 */
													, JDTORecord recCrnSch					/* 크레인스케줄정보 */
													, JDTORecord recWbook					/* 작업예약정보 */
													) throws JDTOException {

		JPlateYdStkBedDAO 	ydStkbedDao	= new JPlateYdStkBedDAO();
//		JPlateYdStkLyrDAO 	ydStkLyrDao	= new JPlateYdStkLyrDAO();
		JPlateYdStockDAO	ydStockDao	= new JPlateYdStockDAO();
		JPlateYdCrnSchDAO	ydCrnSchDao	= new JPlateYdCrnSchDAO();

		String szMethodName				= "procMainWrkToLocForPlateYd";
		String szOperationName			= "주작업TO위치결정(2후판정정)";
		String szDesc					= "";
		String szLogMsg					= null;
		String szRtnMsg 				= JPlateYdConst.RETN_CD_SUCCESS;

		ArrayList		 listToLoc		= null;
		JPlateYdStkLocVO ydStkLocVO		= null;

		JDTORecordSet	rsResult		= null;
		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;

		String 	szYD_UP_WO_LOC			= null;
		String 	szYD_UP_WO_LAYER		= null;
		String 	szYD_DN_WO_LOC			= null;
		String 	szYD_DN_WO_LAYER		= null;
		String 	szYD_UP_STK_COL_GP		= null;
		String 	szYD_UP_STK_BED_NO		= null;
		String 	szYD_DN_STK_COL_GP		= null;
		String 	szYD_DN_STK_BED_NO		= null;
		String 	szYD_SCH_CD				= null;
		String	szSTL_NO				= null;

		boolean bUP_UPDT_NEEDED			= false;

		String[] szYD_STK_LYR_MTL_STAT	= {"D", "U"};

		String	szYD_TO_LOC_GUIDE		= null;

		String 	szYD_CRN_SCH_ID			= null;
		String 	szYD_EQP_ID				= null;

		String 	szYD_MTL_W_GP			= null;						//야드재료폭구분
		String 	szYD_MTL_L_GP			= null;						//야드재료길이구분

		int 	intYD_EQP_WRK_SH		= 0;						//야드설비작업매수
		int 	intYD_EQP_WRK_WT		= 0;						//야드설비작업중량
		double 	dblYD_EQP_WRK_T			= 0;						//야드설비작업총두께
		int		intYD_EQP_WRK_L			= 0;						//야드설비작업총두께
		String 	szYD_EQP_WRK_MAX_W		= null;						//작업재료 중 최대 폭
		String 	szYD_EQP_WRK_MAX_L		= null;						//작업재료 중 최대 길이
		String 	szYD_STK_BED_L_GP		= null;
		String 	szYD_STK_BED_W_GP		= null;
		String 	szYD_STK_BED_WHIO_STAT	= null;
		String	szYD_FR_BAY				= "";
		String	szYD_TO_BAY				= "";
		String	szYD_TCAR_GP			= "";

		int 	intRtnVal               = 0;
		boolean bIsToLocStackable		= false;
		boolean bIS_BED_STACKABLE		= false;
		boolean isSameBay 				= false;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                    	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
				
//		String	szOCPY_BED_ERR			= null;						//점유베드 체크필드
		String	szMODIFIER				= ydDaoUtils.paraRecModifier(msgRecord);

		//----------------------------------------------------------------------------------------------------------------------
		//------------------------------------- 사용자정의위치에 대한 TO위치결정
		//----------------------------------------------------------------------------------------------------------------------

		szLogMsg = "[" + szOperationName + "] ---------------------- TO_LOC START";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szLogMsg = "[" + szOperationName + "] ---------------------- msgRecord :: " + msgRecord.toString();
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szLogMsg = "[" + szOperationName + "] ---------------------- recWbook :: " + recWbook.toString();
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		// 작업예약의 대차설비 ID 조회
		szYD_TCAR_GP = ydDaoUtils.paraRecChkNull(recWbook, "YD_WRK_PLAN_TCAR");

		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료의 최하단 재료정보를 가져온다.
		//----------------------------------------------------------------------------------------------------------------------
		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();

		szYD_SCH_CD	 = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");

		szLogMsg = "[" + szOperationName + "] szYD_SCH_CD :: " + szYD_SCH_CD;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		if (szYD_SCH_CD == null || szYD_SCH_CD.length() != 8) {
			szRtnMsg = "스케줄코드 오류발생 :: " + szYD_SCH_CD;
			szLogMsg = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			
			return szRtnMsg;
		}

		if ("PT".equals(szYD_SCH_CD.substring(2, 4))) {
			szDesc	= "차량";
		} else if ("RT".equals(szYD_SCH_CD.substring(2, 4))) {
			szDesc	= "RollerTable";
		} else if ("TF".equals(szYD_SCH_CD.substring(2, 4))) {
			szDesc	= "Transfer";
		} else if ("CN".equals(szYD_SCH_CD.substring(2, 4))) {
			szDesc	= "가스장보급";
		} else if ("BS".equals(szYD_SCH_CD.substring(2, 4))) {
			szDesc	= "보수장보급";
		} else if ("YD".equals(szYD_SCH_CD.substring(2, 4))) {
			szDesc	= "야드";
		}

		if ("TC".equals(szYD_SCH_CD.substring(2, 4))) {
			szDesc	= "대차";
			if ("U".equals(szYD_SCH_CD.substring(6, 7))) {
				szDesc += " 상차";
			} else {
				szDesc += " 하차";
			}
		} else {
			if ("L".equals(szYD_SCH_CD.substring(6, 7))) {
				szDesc += " BOOK-OUT";
			} else if ("U".equals(szYD_SCH_CD.substring(6, 7))) {
				szDesc += " BOOK-IN";
			} else if ("M".equals(szYD_SCH_CD.substring(6, 7))) {
				szDesc += " 이적";
			}
		}

		if (!"".equals(szDesc)) {
			szOperationName	+= "-" + szDesc;
		}

		szYD_CRN_SCH_ID     = ydDaoUtils.paraRecChkNull(recCrnSch,		"YD_CRN_SCH_ID"		);		// 크레인스케줄ID
		szYD_EQP_ID      	= ydDaoUtils.paraRecChkNull(recCrnSch,		"YD_EQP_ID"			);		// 크레인설비ID

		intYD_EQP_WRK_SH    = ydDaoUtils.paraRecChkNullInt(recPara,		"SH_CNT");					// 크레인작업재료 총매수
		intYD_EQP_WRK_WT    = ydDaoUtils.paraRecChkNullInt(recPara,		"SUM_MTL_WT"		);		// 크레인작업재료 총중량
		dblYD_EQP_WRK_T     = ydDaoUtils.paraRecChkNullDouble(recPara,	"SUM_MTL_T"			);		// 크레인작업재료 총높이
		intYD_EQP_WRK_L		= ydDaoUtils.paraRecChkNullInt(recPara,		"MAX_MTL_L"			);		// SUM_MTL_L		//크레인작업재료 총길이

		szYD_EQP_WRK_MAX_W	= ydDaoUtils.paraRecChkNull(recPara, 		"MAX_MTL_W"			);		// 크레인작업재료 중 최대 폭
		szYD_EQP_WRK_MAX_L	= ydDaoUtils.paraRecChkNull(recPara, 		"MAX_MTL_L"			);		// 크레인작업재료 중 최대 길이
		szYD_TO_LOC_GUIDE	= ydDaoUtils.paraRecChkNull(recWbook, 		"YD_TO_LOC_GUIDE"	);

		/* 표준화 지적사항 반영 >>>> StringBuffer 사용
		szLogMsg  = "[" + szOperationName + "] 크레인작업재료의 총매수 :: " + intYD_EQP_WRK_SH;
		szLogMsg += ", 총중량 :" + intYD_EQP_WRK_WT;
		szLogMsg += ", 총높이 :" + dblYD_EQP_WRK_T;
		szLogMsg += ", 최대폭 :" + szYD_EQP_WRK_MAX_W;
		szLogMsg += ", 최대길이 :" + szYD_EQP_WRK_MAX_L;
		szLogMsg += ", TO GUIDE :" + szYD_TO_LOC_GUIDE;
		szLogMsg += ", 대차ID :" + szYD_TCAR_GP;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
		 */
		StringBuffer szLogBuff = new StringBuffer();
		szLogBuff.append("[" + szOperationName + "] 크레인작업재료의 총매수 :: " + intYD_EQP_WRK_SH);
		szLogBuff.append(", 총중량 :" + intYD_EQP_WRK_WT);
		szLogBuff.append(", 총높이 :" + dblYD_EQP_WRK_T);
		szLogBuff.append(", 총길이 :" + intYD_EQP_WRK_L);
		szLogBuff.append(", 최대폭 :" + szYD_EQP_WRK_MAX_W);
		szLogBuff.append(", 최대길이 :" + szYD_EQP_WRK_MAX_L);
		szLogBuff.append(", TO GUIDE :" + szYD_TO_LOC_GUIDE);
		szLogBuff.append(", 대차ID :" + szYD_TCAR_GP);
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogBuff.toString(), JPlateYdConst.DEBUG, logId);

		if (szYD_TO_LOC_GUIDE.length() == 8) {
			szYD_DN_STK_COL_GP = szYD_TO_LOC_GUIDE.substring(0, 6);
			szYD_DN_STK_BED_NO = szYD_TO_LOC_GUIDE.substring(6);
		}

		szLogMsg = "[" + szOperationName + "] ---------------------- 야드To위치Guide :: " + szYD_TO_LOC_GUIDE;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		//	최하단 재료를 저장품으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

		szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 시작 ";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		rsResult  = JDTORecordFactory.getInstance().createRecordSet("");

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recPara에 logId 추가 
		recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
				
		intRtnVal = ydStockDao.getYdStock(recPara, rsResult);
		if (intRtnVal == 0) {
			szRtnMsg = "재료번호 ["+szSTL_NO+"]가 존재하지 않습니다.";
			szLogMsg = "["+szOperationName+"] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			
			return szRtnMsg;
		} else if (intRtnVal == -2) {
			szRtnMsg = "파라미터가 존재하지 않습니다.";
			szLogMsg = "["+szOperationName+"] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			
			return szRtnMsg;
		} else if (intRtnVal < 0) {
			szRtnMsg = "재료정보 조회시 오류발생 :: " + Integer.toString(intRtnVal);
			szLogMsg = "["+szOperationName+"] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			
			return szRtnMsg;
		}

		rsResult.first();
		recTemp = rsResult.getRecord();

		szYD_MTL_L_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L_GP");			//크레인작업 최하단재료의 길이구분
		szYD_MTL_W_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_W_GP");			//크레인작업 최하단재료의 폭구분

		//szYD_RCPT_PLN_STR_LOC = ydDaoUtils.paraRecChkNull(recTemp, "YD_RCPT_PLN_STR_LOC");//입고예정위치

		szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 완료 - 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		//	권하중이거나 권상중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYD_UP_WO_LOC 	 = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");
		szYD_UP_WO_LAYER = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LAYER");

		if ("".equals(szYD_UP_WO_LOC)) {

			szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]에 대한 권하 또는 권상위치 조회 시작 ";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			szRtnMsg = JPlateYdCommonUtils.getYdStkLyrWithMtlStat(recPara, rsResult, szYD_STK_LYR_MTL_STAT);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				return szRtnMsg;
			}

			rsResult.first();
			recTemp = rsResult.getRecord();

			szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]에 대한 권하 또는 권상위치 조회 완료  ";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			szYD_UP_STK_COL_GP	= recTemp.getFieldString("YD_STK_COL_GP");
			szYD_UP_STK_BED_NO 	= recTemp.getFieldString("YD_STK_BED_NO");
			szYD_UP_WO_LOC 		= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYD_UP_WO_LAYER 	= recTemp.getFieldString("YD_STK_LYR_NO");

			bUP_UPDT_NEEDED		= true;

			szLogMsg = "[" + szOperationName + "] 조회된 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		} else {
			szYD_UP_STK_COL_GP = szYD_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYD_UP_WO_LOC.substring(6);

			szLogMsg = "[" + szOperationName + "] 크레인스케줄에 등록된 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		}

		//----------------------------------------------------------------------------------------------------------------------
    	//	해당베드정보에 적치가능한 지를 체크
    	//YD_TO_LOC_GUIDE	- 사용자지정위치(적치열+적치베드)
		// * 				2) YD_EQP_WRK_SH	- 작업총매수
		// * 				3) YD_EQP_WRK_WT	- 작업총중량
		// * 				4) YD_EQP_WRK_T		- 작업총두께
		// * 				5) YD_SCH_CD		- 스케줄코드
    	//----------------------------------------------------------------------------------------------------------------------

		szLogMsg = "[" + szOperationName + "] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]과 권상지시적치열["+szYD_UP_STK_COL_GP+"]의 동이 같은 지 비교 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		//	TO위치가이드와 권상지시위치의 동이 다른 경우에는 TO위치가이드가 적치가능한 지를 체크하는 부분을 SKIP한다.
		//----------------------------------------------------------------------------------------------------------------------
		szYD_FR_BAY = ydUtils.substr(szYD_UP_STK_COL_GP, 1, 1);
		szYD_TO_BAY = ydUtils.substr(szYD_TO_LOC_GUIDE,  1, 1);
		if (szYD_TO_BAY.equals(szYD_FR_BAY)) {
			isSameBay = true;
			szLogMsg = "[" + szOperationName + "] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]과 권상지시적치열["+szYD_UP_STK_COL_GP+"]의 동이 같은 지 비교 완료 - 동이 같음";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		} else {
			szLogMsg = "[" + szOperationName + "] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]과 권상지시적치열["+szYD_UP_STK_COL_GP+"]의 동이 같은 지 비교 완료 - 동이 다름";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		}

		listToLoc = new ArrayList();

		// 입고시 예정위치정보는 SKIP
		//if (!"L".equals(szYD_SCH_CD.substring(6, 7)) && szYD_TO_LOC_GUIDE.length() == 8) {
//		if (szYD_TO_LOC_GUIDE.length() == 8) {
		if (szYD_TO_LOC_GUIDE.length() >= 6) {

			//----------------------------------------------------------------------------------------------------------------------
			bIsToLocStackable = false;

			if (isSameBay) {

				//----------------------------------------------------------------------------------------------------------------------
				//	일반야드인 경우에는 입고, 이적인 경우 최하단 크레인작업재료와 TO위치가이드위치의 Bed Size가 다르면 제외
				//	베드의 야드적치Bed입출고상태가 E(입출고가능)가 아니면 베드 제외
				//----------------------------------------------------------------------------------------------------------------------

				if (szYD_TO_LOC_GUIDE.substring(2, 4).matches("\\d\\d")) {	//일반야드는 숫자이므로 설비는 숫자가 아님

					//일반야드인 경우에는 입고, 이적인 경우 최하단 크레인작업재료와 TO위치가이드위치의 Bed Size가 다르면 제외
					//베드의 야드적치Bed입출고상태가 E(입출고가능)가 아니면 베드 제외

					szLogMsg = "[" + szOperationName + "] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]가 적치가능한 size[폭,길이]와 야드적치Bed입출고상태를 체크하기 위해 조회 시작";
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

					rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
					recTemp  = JDTORecordFactory.getInstance().create();
			    	recTemp.setField("YD_STK_COL_GP", 	ydUtils.substr(szYD_TO_LOC_GUIDE, 0, 6));
			    	recTemp.setField("YD_STK_BED_NO", 	(szYD_TO_LOC_GUIDE.length() > 6) ? ydUtils.substr(szYD_TO_LOC_GUIDE, 6, 2) : "01");

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recTemp에 logId 추가 
			    	recTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

			    	intRtnVal = ydStkbedDao.getYdStkbed(recTemp, rsResult);		// intGp == 0

			    	if (intRtnVal > 0) {

			    		rsResult.first();
			    		recTemp = rsResult.getRecord();

			    		szYD_STK_BED_L_GP		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_L_GP");
			    		szYD_STK_BED_W_GP		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_GP");
			    		szYD_STK_BED_WHIO_STAT	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT");

			    		if ("E".equals(szYD_STK_BED_WHIO_STAT)) {

			    			szLogMsg = "[" + szOperationName + "] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]의 야드적치Bed입출고상태가 입출고가능하므로 적치가능하므로 재료와 베드의 size비교 시작";
							ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

							if (szYD_STK_BED_L_GP.length() == 1 && szYD_STK_BED_W_GP.length() == 1) {

			    				szLogMsg = "[" + szOperationName + "] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]에 폭구분["+szYD_STK_BED_W_GP+"], 길이구분["+szYD_STK_BED_L_GP+"]이 동일하므로 적치가능";
								ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
								bIsToLocStackable = true;
				    		} else {
				    			szLogMsg = "[" + szOperationName + "] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]에 폭구분["+szYD_STK_BED_W_GP+"], 길이구분["+szYD_STK_BED_L_GP+"]이 존재하지 않습니다.";
								ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
				    		}
			    		} else {
			    			szLogMsg = "[" + szOperationName + "] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]의 야드적치Bed입출고상태가 입출고가능하지 않으므로 적치불가능";
							ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			    		}
			    	} else {
			    		return szRtnMsg;
			    	}
				} else {
					bIsToLocStackable = true;
				}

				//----------------------------------------------------------------------------------------------------------------------
				//	TO위치가이드와 권상지시위치의 동이 같으므로 TO위치가이드가 적치가능한 지를 비교
				//----------------------------------------------------------------------------------------------------------------------
				if (bIsToLocStackable) {

					szLogMsg = "[" + szOperationName + "] 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]에 적치가능한 지 비교 시작";
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			    	recTemp = JDTORecordFactory.getInstance().create();
			    	recTemp.setField("YD_TO_LOC_GUIDE", 	szYD_TO_LOC_GUIDE					);
			    	recTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(intYD_EQP_WRK_SH)	);		// 크레인작업재료 총매수
			    	recTemp.setField("YD_EQP_WRK_WT", 		String.valueOf(intYD_EQP_WRK_WT)	);		// 크레인작업재료 총중량
			    	recTemp.setField("YD_EQP_WRK_T", 		String.valueOf(dblYD_EQP_WRK_T)		);		// 크레인작업재료 총높이
			    	recTemp.setField("YD_EQP_WRK_L", 		String.valueOf(intYD_EQP_WRK_L)		);		// 크레인작업재료 총길이
			    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD							);
			    	recTemp.setField("STL_NO", 				szSTL_NO							);
			    	recTemp.setField("MODIFIER",			szMODIFIER							);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recTemp에 logId 추가 
			    	recTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
			    				    	
		    		szRtnMsg = JPlateYdToLocUtil.procAsgnedBedStackable(recTemp, listToLoc, szMethodName);

			    	if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {

			    		ydStkLocVO = (JPlateYdStkLocVO)listToLoc.get(0);
			    		ydStkLocVO.setSeq(JPlateYdConst.TO_LOC_PRIOR_USER);
			    		ydStkLocVO.setPrior(JPlateYdConst.TO_LOC_PRIOR_USER);
			    	}

			    	szLogMsg = "[" + szOperationName + "] 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]에 적치가능한 지 비교 완료 - 메세지 : " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
				} else {
					szLogMsg = "[" + szOperationName + "] 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]에 적치불가능하므로 적치가능비교하지 않음";
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
				}
			}//end if (isSameBay)

		} else {

			szLogMsg = "[" + szOperationName + "] 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]가 존재하지 않거나 자리수(8)가 맞지 않으므로 해당 TO위치Guide를 검색하지 않습니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		}

		//----------------------------------------------------------------------------------------------------------------------
		//	입고예정위치가 존재하지 않는 경우에는  길이구분/폭구분이 같은 해당 동의 모든 위치를 조회
		//----------------------------------------------------------------------------------------------------------------------
    	//szYD_GP		= szYD_UP_WO_LOC.substring(0, 1);
    	//szYD_BAY_GP	= szYD_UP_WO_LOC.substring(1, 2);
    	//szYD_EQP_GP	= szYD_UP_WO_LOC.substring(2, 4);

    	//권상지시위치에 따라 알맞은 적치가능한 베드 검색 방법을 적용
    	recTemp = JDTORecordFactory.getInstance().create();
    	recTemp.setField("YD_TO_LOC_GUIDE", 	szYD_TO_LOC_GUIDE					);
    	recTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(intYD_EQP_WRK_SH)	);		// 크레인작업재료 총매수
    	recTemp.setField("YD_EQP_WRK_WT", 		String.valueOf(intYD_EQP_WRK_WT)	);		// 크레인작업재료 총중량
    	recTemp.setField("YD_EQP_WRK_T", 		String.valueOf(dblYD_EQP_WRK_T)		);		// 크레인작업재료 총높이
    	recTemp.setField("YD_EQP_WRK_L", 		String.valueOf(intYD_EQP_WRK_L)		);		// 크레인작업재료 총길이
    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD							);		// 크레인스케줄코드
    	recTemp.setField("YD_MTL_L_GP", 		szYD_MTL_L_GP						);		// 크레인작업 최하단재료의 길이구분
    	recTemp.setField("YD_MTL_W_GP", 		szYD_MTL_W_GP						);		// 크레인작업 최하단재료의 폭구분
    	recTemp.setField("YD_STK_COL_GP", 		szYD_UP_STK_COL_GP					);		// 권상지시위치 - 적치열
    	recTemp.setField("YD_STK_BED_NO", 		szYD_UP_STK_BED_NO					);		// 권상지시위치 - 적치베드
    	recTemp.setField("YD_EQP_ID", 			szYD_EQP_ID							);		// 크레인설비ID
    	recTemp.setField("STL_NO", 				szSTL_NO							);
    	recTemp.setField("YD_TCAR_GP",			szYD_TCAR_GP						);		// 대차구분
    	recTemp.setField("MODIFIER",			szMODIFIER							);

		//----------------------------------------------------------------------------------------------------------------------
		// Sorting ?
		//----------------------------------------------------------------------------------------------------------------------
		if (listToLoc.size() > 0) {
			Collections.sort(listToLoc, new JPlateYdStkLocComparator());
		}

		//----------------------------------------------------------------------------------------------------------------------
		//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
		//----------------------------------------------------------------------------------------------------------------------
		for (int ii=0; ii<listToLoc.size(); ii++) {

			ydStkLocVO = (JPlateYdStkLocVO)listToLoc.get(ii);

			szLogMsg = "[" + szOperationName + "] ["+(ii + 1)+"] 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]의 오류코드 확인["+ydStkLocVO.getYdBedErrCd()+"]";
    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

    		if (ydStkLocVO.getYdBedErrCd() == JPlateYdConst.YD_BED_STACKABLE) {

				bIS_BED_STACKABLE = true;

				szLogMsg  = "[" + szOperationName + "] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
	    		szLogMsg += "[" + szOperationName + "] +++++++++++++++ ["+(ii + 1)+"] 조회된 베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++\n";
	    		szLogMsg += "[" + szOperationName + "] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

				break;
			}
		}

		//----------------------------------------------------------------------------------------------------------------------
		//	길이구분/폭구분이 동일한 공베드를 조회 : TO위치가이드가 적치가능 베드가 아닐때
		//----------------------------------------------------------------------------------------------------------------------
		if (!bIS_BED_STACKABLE) {

			//------------------------------------------------------------------------------
			//	위에서 조회된 결과를 갖고 있으므로 사용하기 전에 값을 초기화시킴
			//------------------------------------------------------------------------------
			ydStkLocVO = null;
			listToLoc  = new ArrayList();

			szLogMsg = "[" + szOperationName + "] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 적치가능한 공베드 조회 시작";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			szRtnMsg = getEmptyBedWithSameLWGp(recTemp, listToLoc);

			szLogMsg = "[" + szOperationName + "] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 적치가능한 공베드 조회 완료 - 메세지 :: " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			//----------------------------------------------------------------------------------------------------------------------
			// Sorting
			//----------------------------------------------------------------------------------------------------------------------
			szLogMsg = "[" + szOperationName + "] Sorting Start";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			szLogMsg = "[" + szOperationName + "] listToLoc Size >>>> " + listToLoc.size();
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			if (listToLoc.size() > 0) {
				Collections.sort(listToLoc, new JPlateYdStkLocComparator());
			}

			szLogMsg = "[" + szOperationName + "] Sorting End";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			//----------------------------------------------------------------------------------------------------------------------
			//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
			//----------------------------------------------------------------------------------------------------------------------
			for(int ii=0; ii<listToLoc.size(); ii++) {

				ydStkLocVO = (JPlateYdStkLocVO)listToLoc.get(ii);

				szLogMsg = "[" + szOperationName + "] ["+(ii+1)+"] 주작업 TO위치 결정 적치가능 판단 >>>> ["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"], 오류코드::["+ydStkLocVO.getYdBedErrCd()+"]";
	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

	    		if (ydStkLocVO.getYdBedErrCd() == JPlateYdConst.YD_BED_STACKABLE) {

					bIS_BED_STACKABLE = true;

					szLogMsg = "[" + szOperationName + "] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
		    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
					szLogMsg = "[" + szOperationName + "] +++++++++++++++ ["+(ii + 1)+"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 조회된 공베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++";
    	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
    	    		szLogMsg = "[" + szOperationName + "] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
    	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

					break;
				}
			}
		}

		if (!bIS_BED_STACKABLE) {
			szLogMsg = "[" + szOperationName + "] 권하위치검색 실패 - 적치가능한 베드가 존재하지 않습니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			// 2013.07.19 김현우 TO위치 결정못해도 크레인작업재료 총매수/중량/높이는 UPDATE 하도록 보완
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);								//크레인스케줄ID
			recPara.setField("YD_EQP_WRK_SH", 			String.valueOf(intYD_EQP_WRK_SH));				//크레인작업재료 총매수
			recPara.setField("YD_EQP_WRK_WT", 			String.valueOf(intYD_EQP_WRK_WT));				//크레인작업재료 총중량
			recPara.setField("YD_EQP_WRK_T", 			String.valueOf(dblYD_EQP_WRK_T));				//크레인작업재료 총높이
			recPara.setField("YD_EQP_WRK_L", 			String.valueOf(intYD_EQP_WRK_L));				//크레인작업재료 총높이
			recPara.setField("YD_EQP_WRK_MAX_W", 		szYD_EQP_WRK_MAX_W);							//크레인작업재료 중 최대 폭
			recPara.setField("YD_EQP_WRK_MAX_L", 		szYD_EQP_WRK_MAX_L);							//크레인작업재료 중 최대 길이

			intRtnVal = ydCrnSchDao.upYdEqpWrkInfo(recPara);

			szLogMsg = "[" + szOperationName + "] 크레인작업재료 총매수 UPDATE 결과 >>>> " + intRtnVal;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			return JPlateYdConst.RETN_NOT_EXIST_BED;

		}

		/*
		 * 권하위치 최종결정정보 셋팅.
		 */
		szYD_DN_STK_COL_GP 	= ydStkLocVO.getYdStkColGp();
		szYD_DN_STK_BED_NO 	= ydStkLocVO.getYdStkBedNo();
		szYD_DN_WO_LOC		= szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;
		szYD_DN_WO_LAYER 	= ydStkLocVO.getYdStkLyrNo();

		// 보수장일때 점유베드 체크
		/*
		if ("BS".equals(szYD_DN_STK_COL_GP.substring(2, 4))) {
			szLogMsg = "[" + szOperationName + "] 보수장일때 점유베드 간섭여부 체크 시작";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			szOCPY_BED_ERR = "Y";

			recPara.setField("YD_STK_COL_GP", 	szYD_DN_STK_COL_GP);			//권하지시위치
			recPara.setField("YD_STK_BED_NO",   szYD_DN_STK_BED_NO);			//권하지시위치 베드번호
			recPara.setField("YD_STK_LYR_NO",	szYD_DN_WO_LAYER);				//권하지시위치 적치단
			recPara.setField("MTL_MAX_W", 		szYD_EQP_WRK_MAX_W);			//크레인작업재료 중 최대 폭
			recPara.setField("MTL_MAX_L", 		szYD_EQP_WRK_MAX_L);			//크레인작업재료 중 최대 길이

			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal = ydStkLyrDao.getOcpyBedErr(recPara, rsResult);
			if (intRtnVal > 0) {
				rsResult.first();
				recTemp = rsResult.getRecord();
				szOCPY_BED_ERR = ydDaoUtils.paraRecChkNull(recTemp, "OCPY_BED_ERR");
			}

			if ("Y".equals(szOCPY_BED_ERR)) {
				szLogMsg = "[" + szOperationName + "] 보수장일때 점유베드 간섭 때문에 .. 권하위치검색 실패";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				return JPlateYdConst.RETN_NOT_EXIST_BED;
			}
		}
		*/

		//----------------------------------------------------------------------------------------------------------------------
		// 권하지시위치 수정
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "[" + szOperationName + "] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]을 크레인스케줄에 수정 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		JDTORecord recUpCrnSch	= null;

		recUpCrnSch = JDTORecordFactory.getInstance().create();
		recUpCrnSch.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);									//크레인스케줄ID
		recUpCrnSch.setField("YD_EQP_ID", 				szYD_EQP_ID);										//크레인설비ID
		recUpCrnSch.setField("YD_UP_STK_COL_GP", 		szYD_UP_STK_COL_GP);								//권상지시위치 - 적치열
		recUpCrnSch.setField("YD_UP_STK_BED_NO", 		szYD_UP_STK_BED_NO);								//권상지시위치 - 적치베드
		recUpCrnSch.setField("YD_DN_STK_COL_GP", 		szYD_DN_STK_COL_GP);								//권하지시위치 - 적치열
		recUpCrnSch.setField("YD_DN_STK_BED_NO", 		szYD_DN_STK_BED_NO);								//권하지시위치 - 적치베드

		//----------------------------------------------------------------------------------------------------------------------
		//	권상지시위치 업데이트
		//----------------------------------------------------------------------------------------------------------------------
		if (bUP_UPDT_NEEDED) {
			szLogMsg = "[" + szOperationName + "] 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"] 크레인스케줄에 등록";
    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			recUpCrnSch.setField("YD_UP_WO_LOC", 		szYD_UP_WO_LOC);									//권상지시위치
			recUpCrnSch.setField("YD_UP_WO_LAYER", 		szYD_UP_WO_LAYER);									//권상지시단
		}

		recUpCrnSch.setField("YD_DN_WO_LOC", 			szYD_DN_WO_LOC);									//권하지시위치
		recUpCrnSch.setField("YD_DN_WO_LAYER", 			szYD_DN_WO_LAYER);									//권하지시단
		recUpCrnSch.setField("YD_EQP_WRK_SH", 			String.valueOf(intYD_EQP_WRK_SH));					//크레인작업재료 총매수
		recUpCrnSch.setField("YD_EQP_WRK_WT", 			String.valueOf(intYD_EQP_WRK_WT));					//크레인작업재료 총중량
		recUpCrnSch.setField("YD_EQP_WRK_T", 			String.valueOf(dblYD_EQP_WRK_T));					//크레인작업재료 총높이
		recUpCrnSch.setField("YD_EQP_WRK_L", 			String.valueOf(intYD_EQP_WRK_L));					//크레인작업재료 총길이
		recUpCrnSch.setField("YD_EQP_WRK_MAX_W", 		szYD_EQP_WRK_MAX_W);								//크레인작업재료 중 최대 폭
		recUpCrnSch.setField("YD_EQP_WRK_MAX_L", 		szYD_EQP_WRK_MAX_L);								//크레인작업재료 중 최대 길이
		recUpCrnSch.setField("MODIFIER", 				szMODIFIER);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recUpCrnSch에 logId 추가 
		recUpCrnSch.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

		szRtnMsg = JPlateYdCrnSchUtil.uptCrnSchXYCord(recUpCrnSch);

    	szLogMsg = "[" + szOperationName + "] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]을 크레인스케줄에 수정 완료";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		//	권하지시위치에 재료를 권하대기로 등록
		//----------------------------------------------------------------------------------------------------------------------
    	szLogMsg = "[" + szOperationName + "] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]에 크레인작업재료 등록 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

//---------------------------------------------------------------------------------------------
// 2024.12.16 Argument에 logId 추가
//		szRtnMsg = uptDnWaitOnYdStkLyr(rsCrnwrkmtl, szYD_DN_WO_LOC, szYD_DN_WO_LAYER, szMODIFIER);
		szRtnMsg = uptDnWaitOnYdStkLyr(rsCrnwrkmtl, szYD_DN_WO_LOC, szYD_DN_WO_LAYER, szMODIFIER, logId);
//---------------------------------------------------------------------------------------------

		szLogMsg = "[" + szOperationName + "] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]에 크레인작업재료 등록 완료 - 메세지 : " + szRtnMsg;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}
	
	/**
	 * 동일길이/폭구분 공베드검색
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String getEmptyBedWithSameLWGp(JDTORecord recInPara, List listToLoc) throws JDTOException {
		/*
		 * 최상단 재료의  폭구분, 길이구분과 동일한 베드 조회
		 */
		String 	szMethodName			= "getEmptyBedWithSameLWGp";
		String 	szOperationName			= "동일길이/폭구분공베드검색";
		String 	szLogMsg				= null;
		String 	szRtnMsg 				= JPlateYdConst.RETN_CD_SUCCESS;

		String 	szYD_TO_LOC_GUIDE		= null;
		String 	szYD_MTL_L_GP			= null;
		String 	szYD_MTL_W_GP			= null;
		String 	szYD_STK_COL_GP			= null;
		String 	szYD_STK_BED_NO			= null;
		String 	szYD_GP					= null;
		String 	szYD_BAY_GP				= null;
		String 	szYD_EQP_WRK_SH			= null;
		String 	szYD_EQP_WRK_WT			= null;
		String 	szYD_EQP_WRK_T			= null;
		String 	szYD_EQP_WRK_L			= null;
		String 	szYD_SCH_CD				= null;
		String	szSTL_NO				= null;
		String	szYD_TCAR_GP			= null;

		JDTORecord		recPara			= null;
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인::" + recInPara.toString();
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		ydUtils.displayRecord(szOperationName, recInPara);

		szYD_TO_LOC_GUIDE 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE");		//사용자지정위치
		szYD_MTL_L_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");			//크레인작업 최하단 재료의 길이구분
		szYD_MTL_W_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");			//크레인작업 최하단 재료의 폭구분
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");		//권상지시위치 - 적치열
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");		//권상지시위치 - 적치베드
		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");		//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");		//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");			//크레인작업재료 총높이
		szYD_EQP_WRK_L 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_L");			//크레인작업재료 총길이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");			//크레인스케줄코드
		szSTL_NO			= ydDaoUtils.paraRecChkNull(recInPara, "STL_NO");				//재료번호
		szYD_TCAR_GP		= ydDaoUtils.paraRecChkNull(recInPara, "YD_TCAR_GP");			//대차설비ID

		//----------------------------------------------------------------------------------------------------------------------
		if (szYD_TO_LOC_GUIDE.length() == 8) {

			szYD_GP			= ydUtils.substr(szYD_TO_LOC_GUIDE, 0, 1);
			szYD_BAY_GP		= ydUtils.substr(szYD_TO_LOC_GUIDE, 1, 1);
			//SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_PILING_CD
		} else {
			//----------------------------------------------------------------------------------------------------------------------
			//	사용자지정위치(입고예정위치)가 존재하지 않는 경우에는 업무종료
			//	==>권상지시위치를 사용. : 2009.12.22
			//----------------------------------------------------------------------------------------------------------------------

			szYD_GP			= ydUtils.substr(szYD_STK_COL_GP, 0, 1);
			szYD_BAY_GP		= ydUtils.substr(szYD_STK_COL_GP, 1, 1);

			szLogMsg = "["+ szOperationName +"] 사용자지정위치(입고예정위치)가 존재하지 않으므로 권상지시위치를 사용";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			//----------------------------------------------------------------------------------------------------------------------
		}

		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 			szYD_GP);
		recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);
		recPara.setField("YD_MTL_L_GP", 	ydUtils.substr(szYD_MTL_L_GP, 0, 1));
		recPara.setField("YD_MTL_W_GP", 	ydUtils.substr(szYD_MTL_W_GP, 0, 1));
		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);
		recPara.setField("YD_EQP_WRK_L", 	szYD_EQP_WRK_L);
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
		recPara.setField("YD_STK_COL_GP",	szYD_STK_COL_GP);					//권상지시위치 - 적치열
		recPara.setField("YD_STK_BED_NO",	szYD_STK_BED_NO);					//권상지시위치 - 적치베드
		recPara.setField("YD_TO_LOC_GUIDE",	szYD_TO_LOC_GUIDE);
		recPara.setField("STL_NO",			szSTL_NO);
		recPara.setField("YD_TCAR_GP",		szYD_TCAR_GP);						//대차설비ID

		//----------------------------------------------------------------------------------------------------------------------
		//	동일한 길이구분/폭구분을  가진 모든 공베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 동일한 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"]을 가진 모든 공베드 정보 조회 시작 ";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		szRtnMsg = JPlateYdToLocUtil.srchEmptyBedWithSameLWGp(recPara, listToLoc);

		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 동일한 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"]을 가진 모든 공베드 정보 조회 완료 - 메세지 :: " + szRtnMsg;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	}

	/**
	 * 동일길이/폭구분 공베드정보조회 - 주작업
	 * @param recPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String srchEmptyBedWithSameLWGp(JDTORecord recInPara, List listToLoc) throws JDTOException {

		JPlateYdStkBedDAO ydStkbedDao 	= new JPlateYdStkBedDAO();
		JPlateYdStockDAO  ydStockDao	= new JPlateYdStockDAO();
		JPlateYdStkColDAO ydStkColDao	= new JPlateYdStkColDAO();

		String 	szMethodName			= "srchEmptyBedWithSameLWGp";
		String 	szOperationName			= "동일길이/폭구분 공베드정보조회 (주작업)";
		String 	szLogMsg				= "";
		String 	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;

		String	szYD_FR_BAY				= "";
		String	szYD_TO_BAY				= "";
		String	szYD_TO_STK_COL_GP		= "";
		String	szYD_TO_SPAN_NO			= "";
		String	szYD_TO_STK_BED_NO		= "";

		int    	intRtnVal 				= 0;
        // 후판정정야드그룹구분
        String	szPL_SHEAR_YD_GRP_GP	= "";
        String	szGRP_GP_ARR[]			= null;
        String	szGRP_GP[] 				= {"","","","",""};

		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;
		JDTORecordSet 	rsResult		= null;

		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인 :: " + recInPara.toString();
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		String	szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");						//권상지시위치 - 적치열
		String	szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시베드
		String	szYD_MTL_L_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		String	szYD_MTL_W_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		String	szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		String	szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		String	szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");							//크레인작업재료 총높이
		String	szYD_EQP_WRK_L 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_L");							//크레인작업재료 총길이
		String	szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");							//크레인스케줄코드
		String	szYD_TO_LOC_GUIDE	= ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE");						//사용자지정위치
		String	szYD_GP				= ydDaoUtils.paraRecChkNull(recInPara, "YD_GP");
		String	szYD_BAY_GP			= ydDaoUtils.paraRecChkNull(recInPara, "YD_BAY_GP");
		String	szSTL_NO			= ydDaoUtils.paraRecChkNull(recInPara, "STL_NO");								//재료번호
		String	szYD_TCAR_GP		= ydDaoUtils.paraRecChkNull(recInPara, "YD_TCAR_GP");							//대차설비ID

		szYD_GP						= ydUtils.substr(szYD_TO_LOC_GUIDE, 0, 1);
		szYD_BAY_GP					= ydUtils.substr(szYD_TO_LOC_GUIDE, 1, 1);

		String 	szBS_END			= ydStockDao.getBsEndCheck(recInPara);											//보수완료구분

		boolean	isTCarWk			= false;

		szYD_FR_BAY = ydUtils.substr(szYD_STK_COL_GP,   1, 1);
		szYD_TO_BAY = ydUtils.substr(szYD_TO_LOC_GUIDE, 1, 1);

		// 대차작업여부 체크
		if (szYD_TO_BAY.equals(szYD_FR_BAY) && "".equals(szYD_TCAR_GP) ) {
			isTCarWk = false;
		} else {
			isTCarWk = true;
		}

    	szLogMsg = ">>>> 대차작업 여부 : " + String.valueOf(isTCarWk);
    	ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		// 대차작업일 경우  TO위치 편집 : FA010101 , FXTC01 ==> FATC01
		if (isTCarWk) {
			szYD_TO_STK_COL_GP 	= ydUtils.substr(szYD_STK_COL_GP, 0, 2) + ydUtils.substr(szYD_TCAR_GP, 2, 4);
			szYD_TO_STK_BED_NO 	= "";
			szYD_BAY_GP			= ydUtils.substr(szYD_STK_COL_GP, 1, 1);	// 대차이적일때는 FROM동의 위치를 검색
		} else {
//			if ("".equals(szYD_TO_LOC_GUIDE) || szYD_TO_LOC_GUIDE.length() != 8) {
			if ("".equals(szYD_TO_LOC_GUIDE) || szYD_TO_LOC_GUIDE.length() <  6) {

				szYD_TO_STK_BED_NO = "";

				// BRE RULE 적용 - TO위치 검색기준 조회 : TO위치가 일반베드일때만 체크
				if (!"".equals(szYD_TO_LOC_GUIDE) && szYD_TO_LOC_GUIDE.length() >= 4) {
					szYD_TO_SPAN_NO = szYD_TO_LOC_GUIDE.substring(2,4);
				}

				if ("RT".equals(szYD_TO_SPAN_NO) || "BS".equals(szYD_TO_SPAN_NO) || "CN".equals(szYD_TO_SPAN_NO) || "TD".equals(szYD_TO_SPAN_NO)) {

					if (szYD_TO_LOC_GUIDE.length() >= 6) {
						szYD_TO_STK_COL_GP = szYD_TO_LOC_GUIDE.substring(0, 6);
					} else {
						szYD_TO_STK_COL_GP = szYD_TO_LOC_GUIDE;
					}

				} else {
					JDTORecord jdtoRcd = JDTORecordFactory.getInstance().create();
			    	boolean bRtnVal = GetBreRule8.getYDB805(szYD_STK_COL_GP, szYD_SCH_CD, szBS_END, jdtoRcd);

			    	szLogMsg = ">>>> GetBreRule8 조회 .... szYD_STK_COL_GP : " + szYD_STK_COL_GP + ", szYD_SCH_CD : " + szYD_SCH_CD + ", szBS_END : " + szBS_END + " >>>> Result : " + bRtnVal;
			    	ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			    	if (bRtnVal) {
			    		// TO위치가이드가 4자리이하일때 BRE기준에 등록된 위치로 Set :: 이상일때는 입력한 파라미터 사용
			    		// (전단L2에서 TO위치가 6자리 이하로 올때가 있기 때문에)
			    		if (szYD_TO_LOC_GUIDE.length() < 4) {
			    			szYD_TO_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_TO_LOC_GUIDE");		// 야드To위치Guide
			    		} else {
			    			szYD_TO_STK_COL_GP 	= ydUtils.substr(szYD_TO_LOC_GUIDE, 0, 6);
			    		}
			    		szPL_SHEAR_YD_GRP_GP 	= ydDaoUtils.paraRecChkNull(jdtoRcd, "PL_SHEAR_YD_GRP_GP");		// 후판정정야드그룹구분

			    		if (szPL_SHEAR_YD_GRP_GP != null) {
			    			szGRP_GP_ARR = szPL_SHEAR_YD_GRP_GP.split(",");
			    		}
			    		for(int ii=0; ii<szGRP_GP_ARR.length; ii++) {
			    			if (ii > szGRP_GP.length) {
			    				break;
			    			}
			    			szGRP_GP[ii] = szGRP_GP_ARR[ii];
			    		}
			    	} else {
			    		szYD_TO_STK_COL_GP = "";
			    	}

			    	szLogMsg = ">>>> szYD_TO_STK_COL_GP :" + szYD_TO_STK_COL_GP + ", szPL_SHEAR_YD_GRP_GP :" + szPL_SHEAR_YD_GRP_GP;
			    	ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				}

			} else {
				szYD_TO_STK_COL_GP = ydUtils.substr(szYD_TO_LOC_GUIDE, 0, 6);
				szYD_TO_STK_BED_NO = ydUtils.substr(szYD_TO_LOC_GUIDE, 6, 2);

				rsResult  = JDTORecordFactory.getInstance().createRecordSet("yd");
				recPara   = JDTORecordFactory.getInstance().create();
				recTemp   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP",	szYD_TO_STK_COL_GP);

				// 야드적치열에 후판정정야드그룹구분 조회
				intRtnVal = ydStkColDao.getYdStkcol(recPara, rsResult);
				if (intRtnVal > 0) {
					rsResult.first();
					recTemp = rsResult.getRecord();
					szGRP_GP[0] = ydDaoUtils.paraRecChkNull(recTemp, "PL_SHEAR_YD_GRP_GP");
				}
			}
		}

		szYD_MTL_W_GP = ydUtils.substr(szYD_MTL_W_GP, 0, 1);
		szYD_MTL_L_GP = ydUtils.substr(szYD_MTL_L_GP, 0, 1);

		szLogMsg = "FROM위치          >>>> 열구분 :: " + szYD_STK_COL_GP    + ", BED :: " + szYD_STK_BED_NO + ", 재료번호 :: " + szSTL_NO;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
		szLogMsg = "TO위치 검색범위 >>>> 열구분 :: " + szYD_TO_STK_COL_GP + ", BED :: " + szYD_TO_STK_BED_NO;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 				szYD_GP);
		recPara.setField("YD_BAY_GP", 			szYD_BAY_GP);
		recPara.setField("YD_STK_COL_GP",		szYD_STK_COL_GP);		// 권상위치를 제외하기 위한 조건
		recPara.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
		recPara.setField("YD_TO_STK_COL_GP",	szYD_TO_STK_COL_GP);
		recPara.setField("YD_TO_STK_BED_NO", 	szYD_TO_STK_BED_NO);
		recPara.setField("YD_STK_BED_L_GP", 	szYD_MTL_L_GP);
		recPara.setField("YD_STK_BED_W_GP", 	szYD_MTL_W_GP);
		recPara.setField("YD_EQP_WRK_SH", 		szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 		szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 		szYD_EQP_WRK_T);
		recPara.setField("YD_EQP_WRK_L", 		szYD_EQP_WRK_L);
		recPara.setField("YD_SCH_CD", 			szYD_SCH_CD);
		recPara.setField("STL_NO", 				szSTL_NO);
		recPara.setField("GRP_GP1", 			szGRP_GP[0]);
		recPara.setField("GRP_GP2", 			szGRP_GP[1]);
		recPara.setField("GRP_GP3", 			szGRP_GP[2]);
		recPara.setField("GRP_GP4", 			szGRP_GP[3]);
		recPara.setField("GRP_GP5", 			szGRP_GP[4]);

		szYD_TO_SPAN_NO = ydUtils.substr(szYD_TO_STK_COL_GP, 2, 2);

		if (!"CN".equals(szYD_TO_SPAN_NO) && !"RT".equals(szYD_TO_SPAN_NO) && !"BS".equals(szYD_TO_SPAN_NO)) {
			recPara.setField("YD_TO_STK_BED_NO", 	"01");			// TO위치 결정시 무조건 '01' 베드 선택하도록 보완
		}

		//----------------------------------------------------------------------------------------------------------------------
		//	조건에 해당하는 베드 정보 조회 - 동일길이/폭구분을 가진  모든 공베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 시작 ";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");

		if ("CN".equals(szYD_TO_SPAN_NO)) {

			// 가스장 적치가능한 공베드 조회
			intRtnVal = ydStkbedDao.getSameLWGpBookInCnc(recPara, rsResult);

		} else if ("RT".equals(szYD_TO_SPAN_NO) || "BS".equals(szYD_TO_SPAN_NO) ||
				   "TD".equals(szYD_TO_SPAN_NO) || "TC".equals(szYD_TO_SPAN_NO)) {

			// 길이구분/폭구분이 동일한 적치가능한 공베드 조회 [설비]
			intRtnVal = ydStkbedDao.getSameLWGpBookIn(recPara, rsResult);

		} else {

			// 길이구분/폭구분이 동일한 적치가능한 공베드 조회
			intRtnVal = ydStkbedDao.getSameLWGp(recPara, rsResult);		// intGp == 25

			// RT BOOK-OUT시 TO위치를 못찾으면 해당동 SPAN으로 다시 검색 ????
			if (intRtnVal < 1) {
				if ("RT".equals(ydUtils.substr(szYD_STK_COL_GP, 2, 2))) {
					szYD_TO_STK_COL_GP = ydUtils.substr(szYD_TO_STK_COL_GP, 0, 4);
					szYD_TO_STK_BED_NO = "";
					szLogMsg = "RT BOOK-OUT시 TO위치를 SPAN으로 다시 검색 >>>> 열구분 :: " + szYD_TO_STK_COL_GP + ", BED :: " + szYD_TO_STK_BED_NO;
					ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

					recPara.setField("YD_TO_STK_COL_GP",	szYD_TO_STK_COL_GP);
					recPara.setField("YD_TO_STK_BED_NO", 	szYD_TO_STK_BED_NO);

					intRtnVal = ydStkbedDao.getSameLWGp(recPara, rsResult);		// intGp == 25
				}
			}
		}

		szLogMsg = "["+ szOperationName +"] 동일길이/폭구분을 가진  모든 공베드 정보 조회 완료(주작업) - 메세지 : " + Integer.toString(intRtnVal);
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		if (intRtnVal > 0) {
		//	JPlateYdToLocUtil.srchNconvRecord2Vo(szYD_STK_COL_GP, szYD_STK_BED_NO, recPara, rsResult, listToLoc, JPlateYdConst.TO_LOC_PRIOR_PLATE_EMPTY_BED);
			JPlateYdToLocUtil.srchNconvRecord2Vo("", "", recPara, rsResult, listToLoc, JPlateYdConst.TO_LOC_PRIOR_PLATE_EMPTY_BED);
		} else {
			szRtnMsg = JPlateYdConst.RETN_CD_FAILURE;
		}

		szLogMsg = "["+ szOperationName +"] 메소드 끝 >>>> " + szRtnMsg;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	}

	/**
	 * 보조작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public static String procAidWrkToLocForPlateYd(JDTORecord msgRecord					/* 전문 */
												  ,JDTORecordSet rsCrnwrkmtl			/* 크레인작업재료 */
												  ,JDTORecord recCrnSch					/* 크레인스케줄정보 */
												  ,JDTORecord recWbook					/* 작업예약정보 */
												  ) throws JDTOException {

		String 	szMethodName			= "procAidWrkToLocForPlateYd";
		String 	szOperationName			= "보조작업TO위치결정";
		String 	szLogMsg				= null;
		String 	szRtnMsg 				= JPlateYdConst.RETN_CD_SUCCESS;

		ArrayList		 listToLoc		= null;
		JPlateYdStkLocVO ydStkLocVO		= null;
		JPlateYdStockDAO ydStockDao 	= new JPlateYdStockDAO();

		JDTORecordSet	rsResult		= null;
		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;

		String 	szYD_UP_WO_LOC			= null;
		String 	szYD_UP_WO_LAYER		= null;
		String 	szYD_DN_WO_LOC			= null;
		String 	szYD_DN_WO_LAYER		= null;
		String 	szYD_UP_STK_COL_GP		= null;
		String 	szYD_UP_STK_BED_NO		= null;
		String 	szYD_DN_STK_COL_GP		= null;
		String 	szYD_DN_STK_BED_NO		= null;
		String 	szYD_SCH_CD				= null;

		boolean bUP_UPDT_NEEDED			= false;

		String	szSTL_NO				= null;
		String[] szYD_STK_LYR_MTL_STAT	= {"D", "U"};

		String	szYD_TO_LOC_GUIDE		= null;

		String 	szYD_CRN_SCH_ID			= null;
		String 	szYD_EQP_ID				= null;

		String 	szYD_MTL_W_GP			= null;						//야드재료폭구분
		String 	szYD_MTL_L_GP			= null;						//야드재료길이구분

		int 	intYD_EQP_WRK_SH		= 0;						//야드설비작업매수
		int 	intYD_EQP_WRK_WT		= 0;						//야드설비작업중량
		double 	dblYD_EQP_WRK_T			= 0;						//야드설비작업총두께
		int 	intYD_EQP_WRK_L			= 0;						//야드설비작업길이
		String 	szYD_EQP_WRK_MAX_W		= null;						//작업재료 중 최대 폭
		String 	szYD_EQP_WRK_MAX_L		= null;						//작업재료 중 최대 길이
		int    	intRtnVal 				= 0;
		boolean bIS_BED_STACKABLE 		= false;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                    	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
				
		String	szMODIFIER				= ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");
		if ("".equals(szMODIFIER)) {
			szMODIFIER = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");
		}

		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료의 최하단 재료정보를 가져온다.
		//----------------------------------------------------------------------------------------------------------------------

		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();

		szLogMsg = "[" + szOperationName + "] -------------------- 크레인작업재료의 최하단 재료정보 확인 --------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		ydUtils.displayRecord(szOperationName, recPara);

		szYD_CRN_SCH_ID     = ydDaoUtils.paraRecChkNull(recCrnSch,"YD_CRN_SCH_ID"	);		// 크레인스케줄ID
		szYD_EQP_ID      	= ydDaoUtils.paraRecChkNull(recCrnSch,"YD_EQP_ID"		);		// 크레인설비ID

		intYD_EQP_WRK_SH    = ydDaoUtils.paraRecChkNullInt(recPara,"SH_CNT"			);		// 크레인작업재료 총매수
		intYD_EQP_WRK_WT   	= ydDaoUtils.paraRecChkNullInt(recPara,"SUM_MTL_WT"		);		// 크레인작업재료 총중량
		dblYD_EQP_WRK_T    	= ydDaoUtils.paraRecChkNullDouble(recPara,"SUM_MTL_T"	);		// 크레인작업재료 총높이
		intYD_EQP_WRK_L   	= ydDaoUtils.paraRecChkNullInt(recPara,"MAX_MTL_L"		);		// SUM_MTL_L	//크레인작업재료 총길이

		szYD_EQP_WRK_MAX_W	= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_W"		);		// 크레인작업재료 중 최대 폭
		szYD_EQP_WRK_MAX_L	= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_L"		);		// 크레인작업재료 중 최대 길이

		szLogMsg  = "[" + szOperationName + "] 크레인작업재료의 총매수 :" + intYD_EQP_WRK_SH;
		szLogMsg += ", 총중량 :" + intYD_EQP_WRK_WT;
		szLogMsg += ", 총높이 :" + dblYD_EQP_WRK_T;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szLogMsg = "[" + szOperationName + "] -------------------- 작업예약정보 확인 --------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szYD_TO_LOC_GUIDE	= ydDaoUtils.paraRecChkNull(recWbook, "YD_TO_LOC_GUIDE");				//사용자지정위치
		szYD_SCH_CD			= ydDaoUtils.paraRecChkNull(recWbook, "YD_SCH_CD");						//크레인스케줄코드

		//----------------------------------------------------------------------------------------------------------------------
		//	최하단 재료를 저장품으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szSTL_NO	= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

		szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 시작 ";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		rsResult = JDTORecordFactory.getInstance().createRecordSet("");

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recPara에 logId 추가 
		recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

		intRtnVal = ydStockDao.getYdStock(recPara, rsResult);				// intGp == 0
		if (intRtnVal == 0) {
			szRtnMsg = "["+szOperationName+"] 존재하지 않습니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szRtnMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		} else if (intRtnVal == -2) {
			szRtnMsg = "["+szOperationName+"] 파라미터가 존재하지 않습니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szRtnMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		} else if (intRtnVal < 0) {
			szRtnMsg = "["+szOperationName+"] 오류발생";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szRtnMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		}

		rsResult.first();
		recTemp = rsResult.getRecord();

		szYD_MTL_L_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L_GP");			//크레인작업 최하단재료의길이구분
		szYD_MTL_W_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_W_GP");			//크레인작업 최하단재료의폭구분

		szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]를 저장품에서 조회 완료 - 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		//	권상중이거나 권하중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYD_UP_WO_LOC 		= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");
		szYD_UP_WO_LAYER 	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LAYER");

		if ("".equals(szYD_UP_WO_LOC)) {

			szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]에 대한 권하 또는 권상위치 조회 시작 ";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			szRtnMsg = JPlateYdCommonUtils.getYdStkLyrWithMtlStat(recPara, rsResult, szYD_STK_LYR_MTL_STAT);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				return szRtnMsg;
			}

			rsResult.first();
			recTemp = rsResult.getRecord();

			szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보["+szSTL_NO+"]에 대한 권하 또는 권상위치 조회 완료  ";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			szYD_UP_STK_COL_GP	= recTemp.getFieldString("YD_STK_COL_GP");
			szYD_UP_STK_BED_NO 	= recTemp.getFieldString("YD_STK_BED_NO");
			szYD_UP_WO_LOC 		= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYD_UP_WO_LAYER 	= recTemp.getFieldString("YD_STK_LYR_NO");

			bUP_UPDT_NEEDED		= true;

			szLogMsg = "[" + szOperationName + "] 조회된 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		} else {
			szYD_UP_STK_COL_GP = szYD_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYD_UP_WO_LOC.substring(6);

			szLogMsg = "[" + szOperationName + "] 크레인스케줄에 등록된 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"]";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		}
		//----------------------------------------------------------------------------------------------------------------------

		listToLoc = new ArrayList();

		//권상지시위치에 따라 알맞은 적치가능한 베드 검색 방법을 적용
    	recTemp = JDTORecordFactory.getInstance().create();
    	recTemp.setField("YD_STK_COL_GP", 		szYD_UP_STK_COL_GP);					// 권상지시위치 - 적치열
    	recTemp.setField("YD_STK_BED_NO", 		szYD_UP_STK_BED_NO);					// 권상지시위치 - 적치베드
    	recTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(intYD_EQP_WRK_SH));		// 크레인작업재료 총매수
    	recTemp.setField("YD_EQP_WRK_WT", 		String.valueOf(intYD_EQP_WRK_WT));		// 크레인작업재료 총중량
    	recTemp.setField("YD_EQP_WRK_T", 		String.valueOf(dblYD_EQP_WRK_T));		// 크레인작업재료 총높이
    	recTemp.setField("YD_EQP_WRK_L", 		String.valueOf(intYD_EQP_WRK_L));		// 크레인작업재료 총길이
    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);							// 크레인 스케줄코드
    	recTemp.setField("YD_MTL_L_GP", 		szYD_MTL_L_GP);							// 크레인작업 최하단재료의 길이구분
    	recTemp.setField("YD_MTL_W_GP", 		szYD_MTL_W_GP);							// 크레인작업 최하단재료의 폭구분
    	recTemp.setField("YD_EQP_ID", 			szYD_EQP_ID);							// 크레인설비ID
    	recTemp.setField("YD_TO_LOC_GUIDE", 	szYD_TO_LOC_GUIDE);						// 사용자지정위치
    	recTemp.setField("STL_NO",				szSTL_NO);								// 재료번호

    	//----------------------------------------------------------------------------------------------------------------------
		//	적치가능한 베드 조회
		//----------------------------------------------------------------------------------------------------------------------
		//	길이구분/폭구분이 동일한 공베드를 조회
		//----------------------------------------------------------------------------------------------------------------------
		ydStkLocVO	= null;

		listToLoc 	= new ArrayList();

		szLogMsg = "[" + szOperationName + "] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 적치가능한 공베드 조회 시작(보조) :: " + recTemp.toString();
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szRtnMsg = getEmptyBedWithSameLWGpForAidWrk(recTemp, listToLoc);

		szLogMsg = "[" + szOperationName + "] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 적치가능한 공베드 조회 완료(보조) - 메세지 :: " + szRtnMsg;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		// Sorting
		//----------------------------------------------------------------------------------------------------------------------
		if (listToLoc.size() > 0) {
			Collections.sort(listToLoc, new JPlateYdStkLocComparator());
		}
		//----------------------------------------------------------------------------------------------------------------------

		//----------------------------------------------------------------------------------------------------------------------
		//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
		//----------------------------------------------------------------------------------------------------------------------
		for(int ii=0; ii<listToLoc.size(); ii++) {
			ydStkLocVO = (JPlateYdStkLocVO)listToLoc.get(ii);
			szLogMsg = "[" + szOperationName + "] ["+(ii+1)+"] 주작업 TO위치 결정 적치가능 판단 >>>> ["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"], 오류코드::["+ydStkLocVO.getYdBedErrCd()+"]";
    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

    		//----------------------------------------------------------------------------------------------------------------------
    		//	야드To위치Guide가 존재하고 길이정합성이 맞는 경우에는 보조작업 TO위치 결정 시는 SKIP 시킴
    		//----------------------------------------------------------------------------------------------------------------------
    		if (szYD_TO_LOC_GUIDE.length() == 8) {
    			//----------------------------------------------------------------------------------------------------------------------
    			//	야드To위치결정방법이 F이고 야드To위치Guide가 존재하는 경우에는 보조작업이적 시에는 SKIP시킴
    			//----------------------------------------------------------------------------------------------------------------------
    			if (szYD_TO_LOC_GUIDE.substring(0, 6).equals(ydStkLocVO.getYdStkColGp()) &&
    				szYD_TO_LOC_GUIDE.substring(6).equals(ydStkLocVO.getYdStkBedNo())) {
    				szLogMsg = "[" + szOperationName + "] ["+(ii + 1)+"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 조회된 공베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]가 야드To위치Guide["+szYD_TO_LOC_GUIDE+"]와 동일하므로 SKIP시킴";
    	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
    				continue;
    			}
    			//----------------------------------------------------------------------------------------------------------------------
    		}
    		//----------------------------------------------------------------------------------------------------------------------

    		//----------------------------------------------------------------------------------------------------------------------
    		//	베드가 적치가능한 지 판단 - 가능하면 루프 종료
    		//----------------------------------------------------------------------------------------------------------------------

			if (ydStkLocVO.getYdBedErrCd() == JPlateYdConst.YD_BED_STACKABLE) {

				bIS_BED_STACKABLE = true;

				szLogMsg = "[" + szOperationName + "] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
				szLogMsg = "[" + szOperationName + "] +++++++++++++++ ["+(ii + 1)+"] 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]이 동일한 조회된 공베드["+ydStkLocVO.getYdStkColGp()+" - "+ydStkLocVO.getYdStkBedNo()+"]에 적치가능함 +++++++++++++++";
	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
	    		szLogMsg = "[" + szOperationName + "] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

				break;
			}
		}

		if (!bIS_BED_STACKABLE) {

			szLogMsg = "[" + szOperationName + "] 권하위치검색 실패 - 적치가능한 베드가 존재하지 않습니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			return JPlateYdConst.RETN_NOT_EXIST_BED;
		}

		/*
		 * 권하위치 최종결정정보 셋팅.
		 */
		szYD_DN_STK_COL_GP 	= ydStkLocVO.getYdStkColGp();
		szYD_DN_STK_BED_NO 	= ydStkLocVO.getYdStkBedNo();
		szYD_DN_WO_LOC		= szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;
		szYD_DN_WO_LAYER 	= ydStkLocVO.getYdStkLyrNo();

		//----------------------------------------------------------------------------------------------------------------------
		// 권하지시위치 수정
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "[" + szOperationName + "] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]을 크레인스케줄에 수정 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		JDTORecord recUpCrnSch	= null;

		recUpCrnSch = JDTORecordFactory.getInstance().create();
		recUpCrnSch.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);							//크레인스케줄ID
		recUpCrnSch.setField("YD_EQP_ID", 				szYD_EQP_ID);								//크레인설비ID

		recUpCrnSch.setField("YD_UP_STK_COL_GP", 		szYD_UP_STK_COL_GP);						//권상지시위치 - 적치열
		recUpCrnSch.setField("YD_UP_STK_BED_NO", 		szYD_UP_STK_BED_NO);						//권상지시위치 - 적치베드
		recUpCrnSch.setField("YD_DN_STK_COL_GP", 		szYD_DN_STK_COL_GP);						//권하지시위치 - 적치열
		recUpCrnSch.setField("YD_DN_STK_BED_NO", 		szYD_DN_STK_BED_NO);						//권하지시위치 - 적치베드

		//----------------------------------------------------------------------------------------------------------------------
		//	권상지시위치 업데이트
		//----------------------------------------------------------------------------------------------------------------------
		if (bUP_UPDT_NEEDED) {
			szLogMsg = "[" + szOperationName + "] 권상지시위치["+szYD_UP_WO_LOC+"], 권상지시단["+szYD_UP_WO_LAYER+"] 크레인스케줄에 등록";
    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			recUpCrnSch.setField("YD_UP_WO_LOC", 		szYD_UP_WO_LOC);							//권상지시위치
			recUpCrnSch.setField("YD_UP_WO_LAYER", 		szYD_UP_WO_LAYER);							//권상지시단
		}
		//----------------------------------------------------------------------------------------------------------------------

		recUpCrnSch.setField("YD_DN_WO_LOC", 			szYD_DN_WO_LOC);							//권하지시위치
		recUpCrnSch.setField("YD_DN_WO_LAYER", 			szYD_DN_WO_LAYER);							//권하지시단
		recUpCrnSch.setField("YD_EQP_WRK_SH", 			String.valueOf(intYD_EQP_WRK_SH));			//크레인작업재료 총매수
		recUpCrnSch.setField("YD_EQP_WRK_WT", 			String.valueOf(intYD_EQP_WRK_WT));			//크레인작업재료 총중량
		recUpCrnSch.setField("YD_EQP_WRK_T", 			String.valueOf(dblYD_EQP_WRK_T));			//크레인작업재료 총높이
		recUpCrnSch.setField("YD_EQP_WRK_L", 			String.valueOf(intYD_EQP_WRK_L));			//크레인작업재료 총길이
		recUpCrnSch.setField("YD_EQP_WRK_MAX_W", 		szYD_EQP_WRK_MAX_W);						//크레인작업재료 중 최대 폭
		recUpCrnSch.setField("YD_EQP_WRK_MAX_L", 		szYD_EQP_WRK_MAX_L);						//크레인작업재료 중 최대 길이
		recUpCrnSch.setField("MODIFIER", 				szMODIFIER);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recUpCrnSch에 logId 추가 
		recUpCrnSch.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

		szRtnMsg = JPlateYdCrnSchUtil.uptCrnSchXYCord(recUpCrnSch);

    	szLogMsg = "[" + szOperationName + "] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]을 크레인스케줄에 수정 완료";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------

		//----------------------------------------------------------------------------------------------------------------------
		//	권하지시위치에 재료를 권하대기로 등록
		//----------------------------------------------------------------------------------------------------------------------
    	szLogMsg = "[" + szOperationName + "] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]에 크레인작업재료 등록 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

//---------------------------------------------------------------------------------------------
// 2024.12.16 Argument에 logId 추가
//		szRtnMsg = uptDnWaitOnYdStkLyr(rsCrnwrkmtl, szYD_DN_WO_LOC, szYD_DN_WO_LAYER, szMODIFIER);
		szRtnMsg = uptDnWaitOnYdStkLyr(rsCrnwrkmtl, szYD_DN_WO_LOC, szYD_DN_WO_LAYER, szMODIFIER, logId);
//---------------------------------------------------------------------------------------------

		szLogMsg = "[" + szOperationName + "] 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]에 크레인작업재료 등록 완료 - 메세지 : " + szRtnMsg;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}

	/**
	 * 동일길이/폭구분공베드검색 - 보조작업
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String getEmptyBedWithSameLWGpForAidWrk(JDTORecord recInPara, List listToLoc) throws JDTOException {
		/*
		 * 최상단 재료와 동일길이/폭구분공베드검색 - 보조작업
		 */
		JPlateYdStkBedDAO  ydStkbedDao	= new JPlateYdStkBedDAO();

		String 	szMethodName			= "getEmptyBedWithSameLWGpForAidWrk";
		String 	szOperationName			= "동일길이/폭구분공베드검색(보조작업)";
		String 	szLogMsg				= null;
		String 	szRtnMsg 				= JPlateYdConst.RETN_CD_SUCCESS;

		String 	szYD_STK_COL_GP			= null;
		String 	szYD_MTL_L_GP			= null;
		String 	szYD_MTL_W_GP			= null;
		String 	szYD_STK_BED_NO			= null;
		String 	szYD_GP					= null;
		String 	szYD_BAY_GP				= null;
		String 	szYD_EQP_WRK_SH			= null;
		String 	szYD_EQP_WRK_WT			= null;
		String 	szYD_EQP_WRK_T			= null;
		String 	szYD_EQP_WRK_L			= null;
		String 	szYD_SCH_CD				= null;
		String	szYD_TO_LOC_GUIDE		= null;
		String	szYD_TO_STK_COL_GP		= null;
		String	szYD_TO_STK_BED_NO		= null;
		String	szYD_SPAN				= null;
		String	szSTL_NO				= null;

		int    	intRtnVal 				= 0;

		JDTORecord		recPara			= null;
		JDTORecordSet	rsResult		= null;

		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인 :: " + recInPara.toString();
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");						//권상지시위치 - 적치열
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시베드
		szYD_MTL_L_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		szYD_MTL_W_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");							//크레인작업재료 총높이
		szYD_EQP_WRK_L 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_L");							//크레인작업재료 총길이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");							//크레인스케줄코드
		szYD_TO_LOC_GUIDE	= ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE");						//사용자지정위치
		szSTL_NO			= ydDaoUtils.paraRecChkNull(recInPara, "STL_NO");								//재료번호

		szYD_GP				= szYD_STK_COL_GP.substring(0, 1);
		szYD_BAY_GP			= szYD_STK_COL_GP.substring(1, 2);
		szYD_SPAN			= szYD_STK_COL_GP.substring(2, 4);
		//----------------------------------------------------------------------------------------------------------------------
		if ("".equals(szYD_TO_LOC_GUIDE) || szYD_TO_LOC_GUIDE.length() != 8) {
			// szYD_TO_STK_COL_GP = szYD_GP+szYD_BAY_GP;
			// 동일SPAN내에서 주작업, 보조작업이 이루어 지도록 변경
			szYD_TO_STK_COL_GP = szYD_GP + szYD_BAY_GP + szYD_SPAN;
			szYD_TO_STK_BED_NO = "";
		} else {
			szYD_TO_STK_COL_GP = szYD_TO_LOC_GUIDE.substring(0, 6);
			szYD_TO_STK_BED_NO = szYD_TO_LOC_GUIDE.substring(6, 8);
		}

		if (szYD_MTL_W_GP.length() >= 1) {
			szYD_MTL_W_GP = szYD_MTL_W_GP.substring(0, 1);
		}
		if (szYD_MTL_L_GP.length() >= 1) {
			szYD_MTL_L_GP = szYD_MTL_L_GP.substring(0, 1);
		}

		szLogMsg = "FROM위치           >>>>> 열구분 :: " + szYD_STK_COL_GP    + ", BED :: " + szYD_STK_BED_NO + ", 재료번호 :: " + szSTL_NO;;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
		szLogMsg = "TO위치 검색범위 >>>>> 열구분 :: " + szYD_TO_STK_COL_GP + ", BED :: " + szYD_TO_STK_BED_NO;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 				szYD_GP);
		recPara.setField("YD_BAY_GP", 			szYD_BAY_GP);
		recPara.setField("YD_SPAN_GP",			"");
		recPara.setField("YD_STK_COL_GP",		szYD_STK_COL_GP);		// 권상위치를 제외하기 위한 조건
		recPara.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
		recPara.setField("YD_TO_STK_COL_GP",	szYD_TO_STK_COL_GP);
		recPara.setField("YD_TO_STK_BED_NO", 	szYD_TO_STK_BED_NO);
		recPara.setField("YD_STK_BED_L_GP", 	szYD_MTL_L_GP);
		recPara.setField("YD_STK_BED_W_GP", 	szYD_MTL_W_GP);
		recPara.setField("YD_EQP_WRK_SH", 		szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 		szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 		szYD_EQP_WRK_T);
		recPara.setField("YD_EQP_WRK_L", 		szYD_EQP_WRK_L);
		recPara.setField("YD_SCH_CD", 			szYD_SCH_CD);
		recPara.setField("STL_NO", 				szSTL_NO);

		//----------------------------------------------------------------------------------------------------------------------
		//	동일한 길이/폭구분 공베드 검색
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 시작 ";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
		intRtnVal = ydStkbedDao.getSameLWGpColForAidWrk(recPara, rsResult);				// intGp == 27
		if (intRtnVal <= 0) {
			szRtnMsg = JPlateYdConst.RETN_CD_FAILURE;
		}

		szLogMsg = "["+ szOperationName +"] 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 완료(보조)  - 메세지 : " + szRtnMsg;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		recPara.setField("LOOP_I", "1");

		if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg) && rsResult.size() > 0) {
			JPlateYdToLocUtil.srchNconvRecord2Vo(szYD_STK_COL_GP,
												szYD_STK_BED_NO,
												recPara,
												rsResult,
												listToLoc,
												JPlateYdConst.TO_LOC_PRIOR_PLATE_SAME_PILING_CD);
		}

		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}

	/**
	 * 권하위치재료등록
	 * @param rsCrnwrkmtl
	 * @param szYD_DN_WO_LOC
	 * @param szYD_DN_WO_LAYER
	 * @return
	 * @throws JDTOException
	 */
	public static String uptDnWaitOnYdStkLyr(JDTORecordSet rsCrnwrkmtl				/* 크레인작업재료 */
											, String szYD_DN_WO_LOC
											, String szYD_DN_WO_LAYER
											, String szMODIFIER
											) throws JDTOException {

		JPlateYdStkLyrDAO 	ydStklyrDao 	= new JPlateYdStkLyrDAO();
//		JPlateYdStockDAO 	ydStockDao		= new JPlateYdStockDAO();

		String szMethodName			= "";
		String szOperationName		= "권하위치재료등록";
		String szLogMsg				= null;
		String szYD_STK_COL_GP		= szYD_DN_WO_LOC.substring(0, 6);
		String szYD_STK_BED_NO		= szYD_DN_WO_LOC.substring(6);
		String szYD_STK_LYR_NO		= "";
		String szSTL_NO				= null;
		int    intRtnVal 			= 0;
		JDTORecord		recPara		= null;
		JDTORecord		recTemp		= null;
//		JDTORecordSet 	rsOcpyBed	= JDTORecordFactory.getInstance().createRecordSet("Temp");
//		JDTORecord		recOcpyBed	= null;


		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 크레인작업재료 건수["+rsCrnwrkmtl.size()+"]";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", 		szYD_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
		recPara.setField("MODIFIER", 			szMODIFIER);

		for(int ii=1; ii<=rsCrnwrkmtl.size(); ii++) {
			rsCrnwrkmtl.absolute(ii);
			recTemp  = rsCrnwrkmtl.getRecord();
			szSTL_NO = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
			szYD_STK_LYR_NO = ydDaoUtils.stringPlusInt(szYD_DN_WO_LAYER, (ii - 1));

			szLogMsg = "["+ szOperationName +"] ["+ii+"] 크레인작업재료["+szSTL_NO+"] - 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"], 적치단["+szYD_STK_LYR_NO+"] 권하대기로 등록 시작 ";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			recPara.setField("STL_NO", 					szSTL_NO);
			recPara.setField("YD_STK_LYR_NO", 			szYD_STK_LYR_NO);
			recPara.setField("YD_STK_LYR_MTL_STAT", 	JPlateYdConst.YD_STK_LYR_MTL_STAT_DN_WAIT);

			intRtnVal = ydStklyrDao.updYdStklyrStat(recPara);		// intGp == 0

			if (intRtnVal <= 0) {
				szLogMsg = "["+ szOperationName +"] ["+ii+"] 크레인작업재료["+szSTL_NO+"] - 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"], 적치단["+szYD_STK_LYR_NO+"] 권하대기로 등록 시 오류발생 - 오류 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				return JPlateYdConst.RETN_CD_FAILURE;
			}

			// 보수장일때 점유베드 Set
			/* DAO에서 실시함으로 SKIP 처리
			if ("BS".equals(szYD_STK_COL_GP.substring(2, 4))) {
				szLogMsg = "["+ szOperationName +"] ["+ii+"]  보수장일때 점유베드 등록 시작 ";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				// 재료별 적치가능 베드수 조회
				intRtnVal = ydStockDao.getOcpyBedCnt(recPara, rsOcpyBed);

				if (intRtnVal > 0) {
					// 점유베드 조회
					rsOcpyBed.first();
					recOcpyBed = rsOcpyBed.getRecord();
					recPara.setField("COL_CNT", ydDaoUtils.paraRecChkNull(recOcpyBed, "COL_CNT"));
					recPara.setField("BED_CNT", ydDaoUtils.paraRecChkNull(recOcpyBed, "BED_CNT"));
				}

				intRtnVal = ydStklyrDao.updOcpyBedSet(recPara);
				if (intRtnVal <= 0) {
					szLogMsg = "["+ szOperationName +"] ["+ii+"] 보수장일때 점유베드 등록 .. 오류 :: " + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
					return JPlateYdConst.RETN_CD_FAILURE;
				}
			}
			*/
			szLogMsg = "["+ szOperationName +"] ["+ii+"] 크레인작업재료["+szSTL_NO+"] - 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"], 적치단["+szYD_STK_LYR_NO+"] 권하대기로 등록 완료 ";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
		}

		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}

	/**
	 * To위치결정-사용자지정(공통) : 해당bed에 적치가능 유무 판단 로직으로 사용
	 * @param recPara
	 * @param listToLoc
	 * @param szFromMethod
	 * @return
	 * @throws JDTOException
	 */
	public static String procAsgnedBedStackable(JDTORecord recPara,  List listToLoc, String szFromMethod) throws JDTOException {
		/*
		 * 업무기준 :		0. 해당 위치의 BED정보와 단 정보를 조회
		 * 				1. BED MAX 개수을 넘어서면 적치불가능
		 * 				2. BED MAX 중량을 넘어서면 적치불가능
		 * 				3. 해당 위치의 상단이 크레인스케줄이 존재하는 지 판단
		 * 					3-1. 크레인 스케줄(권상)이 존재하면 적치불가능
		 * 					3-2. 크레인 스케줄이 존재하지 않으면  **** 적치가능  ****
		 *
		 * 파라미터정의:	1) YD_TO_LOC_GUIDE	- 사용자지정위치(적치열+적치베드)
		 * 				2) YD_EQP_WRK_SH	- 작업총매수
		 * 				3) YD_EQP_WRK_WT	- 작업총중량
		 * 				4) YD_EQP_WRK_T		- 작업총두께
		 * 				5) YD_SCH_CD		- 스케줄코드
		 */
		if (listToLoc == null) {
			listToLoc = new ArrayList();
		}
		String 	szMethodName		= "procAsgnedBedStackable";
		String 	szOperationName		= "To위치결정-사용자지정(공통)";
		String 	szLogMsg			= null;
		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
		String	szYdBedErrCd		= null;

		JDTORecord recTemp			= null;
		JDTORecordSet rsTemp		= null;

		String 	szYdToLocGuide 		= null;
		String 	szYdStkColGp		= null;
		String 	szYdStkBedNo		= null;
		String 	szYdStkLyrNo		= null;
		String 	szStlNo				= null;
		String 	szYdSchCd			= null;
		String 	szYdStkBedActStat	= null;
		String 	szYdStkBedWhioStat	= null;
//		String 	szYdStkLyrMtlStat	= null;
		String	szModifier			= null;
		String	szYdSpanGp			= null;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(recPara, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		JPlateYdStkLocVO  ydStkLocVO	= null;
		JPlateYdStkLyrDAO ydStklyrDao	= new JPlateYdStkLyrDAO();

		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "[" + szOperationName + "] 메소드 시작 - 파라미터 확인 >>>> " + recPara.toString();
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO, logId);

		szStlNo			= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");				//재료번호
		szYdToLocGuide 	= ydDaoUtils.paraRecChkNull(recPara, "YD_TO_LOC_GUIDE");	//사용자지정위치
		szYdSchCd 		= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");			//스케줄코드
		szModifier		= ydDaoUtils.paraRecModifier(recPara);						//수정자

		szLogMsg = "[" + szOperationName + "] 파라미터로 전달된 TO위치가이드 [" + szYdToLocGuide + "] 입니다." + szYdSchCd;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO, logId);

		if ("".equals(szYdToLocGuide)) {
			szLogMsg = "[" + szOperationName + "] TO위치가이드가 존재하지 않습니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			return JPlateYdConst.RETN_CD_NOTEXIST;
		}

		//----------------------------------------------------------------------------------------------------------------------
		//	베드분석정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYdStkColGp = ydUtils.substr(szYdToLocGuide, 0, 6);
		szYdSpanGp	 = ydUtils.substr(szYdToLocGuide, 2, 2);
		szYdStkBedNo = ydUtils.substr(szYdToLocGuide, 6, 2);

		//	현업요청으로 TO위치 결정시 무조건 '01' 베드로 결정 (단 설비 베드일때는 예외)
		if (!"BC".equals(szYdSpanGp) && !"BS".equals(szYdSpanGp) && !"CB".equals(szYdSpanGp) && !"CN".equals(szYdSpanGp) && !"RT".equals(szYdSpanGp)) {
			if ("".equals(szYdStkBedNo)) {
				szYdStkBedNo = "01";
			}
		}

		//----------------------------------------------------------------------------------------------------------------------
		//	RT일때는 적치상태 Clear ('C' --> 'E')로 변경
		//----------------------------------------------------------------------------------------------------------------------
		if ("RT".equals(szYdSpanGp)) {

			szLogMsg = "[" + szOperationName + "] RT일경우 베드 적치상태를 CLEAR .. " + szYdStkColGp + szYdStkBedNo;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			// 기존 지시위치 에 쌓여 있는 정보 Clear
			recTemp = JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_STK_COL_GP",       szYdStkColGp);
            recTemp.setField("YD_STK_BED_NO",       szYdStkBedNo);
            recTemp.setField("YD_STK_LYR_NO",       "001");
            recTemp.setField("MODIFIER",       		szModifier);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.03 recTemp에 logId 추가 
            recTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
                                      
			ydStklyrDao.updRtBedClear(recTemp);
		}

		//베드분석정보 조회
		szLogMsg = "[" + szOperationName + "] 해당하는 적치열 [" + szYdStkColGp + "], 적치베드 [" + szYdStkBedNo + "] 로 베드정보 조회 시작 >>>>";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO, logId);

		rsTemp 	= JDTORecordFactory.getInstance().createRecordSet("ydTemp");
		recTemp = JDTORecordFactory.getInstance().create();
		recTemp.setField("YD_STK_COL_GP", 	szYdStkColGp);
		recTemp.setField("YD_STK_BED_NO", 	szYdStkBedNo);
		recTemp.setField("STL_NO", 			szStlNo);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 recSchPara에 logId 추가 
		recTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

		szRtnMsg = JPlateYdToLocUtil.getYdStkBedAnalysis(recTemp, rsTemp, JPlateYdConst.MTL_STAT_C_U_D);

		szLogMsg = "[" + szOperationName + "] 해당하는 적치열[" + szYdStkColGp + "], 적치베드[" + szYdStkBedNo + "]로 베드정보 조회 완료 - 반환값 : " + szRtnMsg;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO, logId);

		if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
			szLogMsg = "[" + szOperationName + "] 해당하는 적치열[" + szYdStkColGp + "], 적치베드[" + szYdStkBedNo + "]로 베드정보 조회 시 오류발생";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			return JPlateYdConst.RETN_CD_NOTEXIST;
		}

		//----------------------------------------------------------------------------------------------------------------------
		//	베드상태와 기존 스케줄이 존재하는 지 체크
		//----------------------------------------------------------------------------------------------------------------------
		rsTemp.first();
		recTemp = rsTemp.getRecord();

		szStlNo 			= ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
		szYdStkBedActStat 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_ACT_STAT");
		szYdStkBedWhioStat 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT");
//		szYdStkLyrMtlStat 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT");
		int iUpSchCnt		= ydDaoUtils.paraRecChkNullInt(recTemp, "UP_SCH_CNT");

		if (!JPlateYdConst.YD_STK_BED_ACTIVE.equals(szYdStkBedActStat)) {
			szLogMsg = "[" + szOperationName + "] 해당하는 적치열[" + szYdStkColGp + "], 적치베드[" + szYdStkBedNo + "]의 활성상태[" + szYdStkBedActStat + "]가 적치가능상태가 아닙니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			return JPlateYdConst.RETN_BED_INACT;
		}

		if (!JPlateYdConst.YD_STK_BED_WHIO_ENABLE.equals(szYdStkBedWhioStat)) {
			szLogMsg = "[" + szOperationName + "] 해당하는 적치열[" + szYdStkColGp + "], 적치베드[" + szYdStkBedNo + "]의 야드적치Bed입출고상태[" + szYdStkBedWhioStat + "]가 입고가능상태가 아닙니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			return JPlateYdConst.RETN_BED_WHIO_NOT_IN;
		}

	//	if (JPlateYdConst.YD_STK_LYR_MTL_STAT_UN_WAIT.equals(szYdStkLyrMtlStat)) {				//권상대기이면 적치불가능
	//		szLogMsg = "[" + szOperationName + "] 적치된 재료["+szStlNo+"]의 적치재료상태["+szYdStkLyrMtlStat+"]가 권상대기이므로 적치불가능";
		if (iUpSchCnt > 0) {
			szLogMsg = "[" + szOperationName + "] 해당 베드에 권상예약 정보가 존재하여 적치불가능";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			return JPlateYdConst.RETN_BED_UN_WAIT;
		}

		//----------------------------------------------------------------------------------------------------------------------
		//	베드 적치가능유무 판단
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "[" + szOperationName + "] 해당하는 적치열[" + szYdStkColGp + "], 적치베드[" + szYdStkBedNo + "]에 적치가능유무 판단 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO, logId);


//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 recPara에 logId 추가 
		recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
		                    
		szYdBedErrCd = JPlateYdCommonUtils.chkBedStackable(recPara, recTemp);

		szLogMsg = "[" + szOperationName + "] 해당하는 적치열[" + szYdStkColGp + "], 적치베드[" + szYdStkBedNo + "]에 적치가능유무 판단 완료 - 반환값 : " + szYdBedErrCd;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO, logId);

		//----------------------------------------------------------------------------------------------------------------------
		//	적치가능한 베드의 적치단 - 값이 없으면 001(1단)으로 설정
		//----------------------------------------------------------------------------------------------------------------------
		if (JPlateYdConst.RETN_CD_SUCCESS.equals(szYdBedErrCd)) {

			szYdStkLyrNo = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");

			szLogMsg = "[" + szOperationName + "] 적치베드의 적치가능 단[" + szYdStkLyrNo + "] - 값이 없으면 001(1단)으로 설정  ";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO, logId);

			if ("".equals(szYdStkLyrNo)) {									//값이 없으면
				szYdStkLyrNo = "001";										//1단
			}

			szLogMsg = "[" + szOperationName + "] 계산된 단[" + szYdStkLyrNo + "]";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO, logId);

		} else {

			szYdStkLyrNo = "000";

		}

		recTemp.setField("YD_STK_LYR_NO", szYdStkLyrNo);
		ydUtils.displayRecord(szOperationName, recTemp);

		//----------------------------------------------------------------------------------------------------------------------
		//	JDTORecord를 VO객체로 변환
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "[" + szOperationName + "] 레코드를 VO객체로 변환 시작  ";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO, logId);

		ydStkLocVO = procRecord2StkLoc(recTemp);

		szLogMsg = "[" + szOperationName + "] 레코드를 VO객체로 변환 완료  ";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO, logId);

		ydStkLocVO.setSeq(JPlateYdConst.TO_LOC_PRIOR_USER);
		ydStkLocVO.setPrior(JPlateYdConst.TO_LOC_PRIOR_USER);

		listToLoc.add(ydStkLocVO);
		//----------------------------------------------------------------------------------------------------------------------

		return JPlateYdConst.RETN_CD_SUCCESS;
	}

	/**
	 * 베드분석정보조회
	 * @param recPara
	 * @param rsOutPara
	 * @param szQueryType
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdStkBedAnalysis(JDTORecord recPara, JDTORecordSet rsOutPara, String szQueryType) throws JDTOException {
		/*
		 * 업무기준	:	해당베드에 적치된 정보 분석 - 베드와 단을 OUTER JOIN : 베드가 존재하는 경우에는 하나의 레코드는 반환이 됨
		 * 				1) 베드의 MAX 매수
		 * 				2) 베드의 MAX 중량
		 * 				3) 베드의 야드적치Bed활성상태
		 * 				4) 베드의 야드적치Bed활성상태
		 * 				5) 베드의 적치된 총매수
		 * 				6) 베드의 적치된 총중량
		 * 				7) 베드의 산적LOT타입별 개수
		 * 				8) 베드의 산적LOT코드별 개수
		 * 				9)
		 */
		String szMethodName		= "getYdStkBedAnalysis";
		String szOperationName	= "베드분석정보조회";
		String szLogMsg			= null;
		String szRtnMsg 		= JPlateYdConst.RETN_CD_SUCCESS;
		int    intRtnVal		= -100;
		String szYdStkColGp		= null;
		String szYdStkBedNo		= null;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(recPara, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		JPlateYdStkBedDAO ydStkBedDao = new JPlateYdStkBedDAO();

		szYdStkColGp = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
		szYdStkBedNo = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
		
		if (JPlateYdConst.MTL_STAT_C_U_D.equals(szQueryType)) {
			szLogMsg = "[" + szOperationName + "] 해당 베드[적치열:" + szYdStkColGp + ", 적치베드:" + szYdStkBedNo + "]의 적치중,권상,권하대기인 재료를 조회 시작";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			intRtnVal = ydStkBedDao.getYdStkBedAnalysis(recPara, rsOutPara);		// intGp == 24

			if (intRtnVal == 0) {
				szLogMsg = "[" + szOperationName + "] 해당 베드[적치열:" + szYdStkColGp+", 적치베드:" + szYdStkBedNo + "]의 적치중,권상,권하대기인 재료를 조회 시 정보가 존재하지 않습니다.";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
				return JPlateYdConst.RETN_CD_NOTEXIST;
			} else if (intRtnVal < 0) {
				szLogMsg = "[" + szOperationName + "] 해당 베드[적치열:" + szYdStkColGp+", 적치베드:" + szYdStkBedNo + "] 의 적치중,권상,권하대기인 재료를 조회 시 오류발생 - 반환값 : " + intRtnVal;
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
				return JPlateYdConst.RETN_CD_FAILURE;
			} else {
				szLogMsg = "[" + szOperationName + "] 해당 베드[적치열:" + szYdStkColGp+", 적치베드:" + szYdStkBedNo + "] 의 적치중,권상,권하대기인 재료정보가 존재합니다.";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			}

		} else {
			szLogMsg = "[" + szOperationName + "] 지원하지 않는 쿼리타입[" + szQueryType + "]입니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		}

		return szRtnMsg;
	}

	/**
	 * 베드적치유무판단/VO변환
	 * @param recPara
	 * @param rsResult
	 * @param listToLoc
	 * @param intPRIOR
	 * @throws JDTOException
	 */
	public static void srchNconvRecord2Vo(String sOrgStkCol, String sOrgStkBed, JDTORecord recPara,
			                              JDTORecordSet	rsResult, List listToLoc, int intPRIOR) throws JDTOException {

		String szMethodName			= "srchNconvRecord2Vo";
		String szOperationName		= "베드적치유무판단/VO변환";
		String szLogMsg				= null;
		String szRtnMsg				= null;

		JDTORecord recTemp			= null;
		JDTORecord recTemp1			= null;

		JPlateYdStkLocVO ydStkLocVO	= null;

		String 	szYD_STK_COL_GP		= null;
		String 	szYD_STK_BED_NO		= null;

		String 	szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_SH");				//크레인작업재료 총매수
		String 	szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_WT");				//크레인작업재료 총중량
		String 	szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_T");				//크레인작업재료 총높이
		String 	szYD_EQP_WRK_L 		= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_L");				//크레인작업재료 길이
		String 	szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");					//크레인스케줄코드
		String	szSTL_NO			= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");						//재료번호

		int intLOOP_I 				= ydDaoUtils.paraRecChkNullInt(recPara, "LOOP_I");

		recTemp1 = JDTORecordFactory.getInstance().create();
		for(int ii=1; ii<=rsResult.size(); ii++) {
			rsResult.absolute(ii);
			recTemp = rsResult.getRecord();

			szLogMsg = "["+ szOperationName +"] 레코드 추출["+ii+"]" + recTemp.toString();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_NO");

			if (sOrgStkCol.equals(szYD_STK_COL_GP) && sOrgStkBed.equals(szYD_STK_BED_NO)) {

				szLogMsg = "["+ szOperationName +"] 권상베드와 To위치가 동일.";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				continue;
			}

			recTemp1.setField("YD_STK_COL_GP", 		szYD_STK_COL_GP);
			recTemp1.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
			recTemp1.setField("YD_EQP_WRK_SH", 		szYD_EQP_WRK_SH);
			recTemp1.setField("YD_EQP_WRK_WT", 		szYD_EQP_WRK_WT);
			recTemp1.setField("YD_EQP_WRK_T", 		szYD_EQP_WRK_T);
			recTemp1.setField("YD_EQP_WRK_L", 		szYD_EQP_WRK_L);
			recTemp1.setField("YD_SCH_CD", 			szYD_SCH_CD);
			recTemp1.setField("STL_NO", 			szSTL_NO);

			ydStkLocVO = new JPlateYdStkLocVO();

			szLogMsg = "["+ szOperationName +"] 베드["+szYD_STK_COL_GP+" - "+szYD_STK_BED_NO+"]가 적치가능한 지 확인 시작";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			szRtnMsg = JPlateYdToLocUtil.procBedStackable(recTemp1, ydStkLocVO, szMethodName);

			szLogMsg = "["+ szOperationName +"] 베드["+szYD_STK_COL_GP+" - "+szYD_STK_BED_NO+"]가 적치가능한 지 확인 완료 :: " + szRtnMsg;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {

				ydStkLocVO.setPrior(intPRIOR);
				ydStkLocVO.setSeq(ii + (intLOOP_I * JPlateYdConst.TO_LOC_PRIOR_STEP * intPRIOR));

				listToLoc.add(ydStkLocVO);
				break;
			}
		}
	}

	/**
	 * BED적치가능유무판단
	 * @param recPara
	 * @param listToLoc
	 * @param szFromMethod
	 * @return
	 * @throws JDTOException
	 */
	public static String procBedStackable(JDTORecord recPara, JPlateYdStkLocVO ydStkLocVO, String szFromMethod) throws JDTOException {
		/*
		 * 업무기준 :		0. 해당 위치의 BED정보와 단 정보를 조회
		 * 				1. BED MAX 개수을 넘어서면 적치불가능
		 * 				2. BED MAX 중량을 넘어서면 적치불가능
		 * 				3. 해당 위치의 상단이 크레인스케줄이 존재하는 지 판단
		 * 					3-1. 크레인 스케줄이 존재하면 적치불가능 (권상)
		 * 					3-2. 크레인 스케줄이 존재하지 않으면  **** 적치가능  ****
		 *
		 * 파라미터정의:	1) YD_STK_COL_GP	- 적치열
		 * 				2) YD_STK_BED_NO	- 적치베드
		 * 				3) YD_EQP_WRK_SH	- 작업총매수
		 * 				4) YD_EQP_WRK_WT	- 작업총중량
		 * 				5) YD_EQP_WRK_T		- 작업총두께
		 * 				6) YD_SCH_CD		- 스케줄코드
		 */

		if (ydStkLocVO == null) {
			ydStkLocVO = new JPlateYdStkLocVO();
		}
		String 	szMethodName			= "procBedStackable";
		String 	szOperationName			= "BED적치가능유무판단";
		String 	szLogMsg				= null;
		String 	szRtnMsg 				= JPlateYdConst.RETN_CD_SUCCESS;
		String	szYdBedErrCd			= null;

		JDTORecord recTemp				= null;
		JDTORecordSet rsTemp			= null;

		String 	szYdStkColGp			= null;
		String 	szYdStkBedNo			= null;
		String 	szYdStkLyrNo			= null;
		String 	szStlNo					= null;
		String 	szYdStkBedActStat		= null;
		String 	szYdStkBedWhioStat		= null;
//		String 	szYdStkLyrMtlStat		= null;
		String	szModifier				= null;

		JPlateYdStkLyrDAO  ydStklyrDao 	= new JPlateYdStkLyrDAO();

		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인>>>>" + recPara.toString();
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO);

		szStlNo			= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");						//재료번호
		szYdStkColGp 	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");				//적치열구분
		szYdStkBedNo 	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");				//적치베드번호
		//szYdSchCd 	= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");					//스케줄코드
		szModifier		= ydDaoUtils.paraRecModifier(recPara);								//수정자

		if ("".equals(szYdStkColGp)) {
			szLogMsg = "["+ szOperationName +"] 적치열구분이 존재하지 않습니다.";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			return JPlateYdConst.RETN_CD_NOTEXIST;
		}

		if ("".equals(szYdStkBedNo)) {
			szLogMsg = "["+ szOperationName +"] 적치베드번호가 존재하지 않습니다.";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			return JPlateYdConst.RETN_CD_NOTEXIST;
		}

		// 권하완료후 CLEAR 함으로 추후 안정화 완료후 제거 필요
		//----------------------------------------------------------------------------------------------------------------------
		//	RT일때는 적치상태 Clear ('C' --> 'E')로 변경
		//----------------------------------------------------------------------------------------------------------------------
		if ("RT".equals(ydUtils.substr(szYdStkColGp,2,2))) {

			szLogMsg = "["+ szOperationName +"] RT일경우 베드 적치상태를 CLEAR .. " + szYdStkColGp + szYdStkBedNo;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			// 기존 지시위치 에 쌓여 있는 정보 Clear
			recTemp = JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_STK_COL_GP",       szYdStkColGp);
            recTemp.setField("YD_STK_BED_NO",       szYdStkBedNo);
            recTemp.setField("YD_STK_LYR_NO",       "001");
            recTemp.setField("MODIFIER",       		szModifier);

			ydStklyrDao.updRtBedClear(recTemp);
		}

		//----------------------------------------------------------------------------------------------------------------------
		//	베드분석정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYdStkColGp+"], 적치베드["+szYdStkBedNo+"]로 베드정보 조회 시작";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO);

		rsTemp = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
		recTemp = JDTORecordFactory.getInstance().create();
		recTemp.setField("YD_STK_COL_GP", 	szYdStkColGp);
		recTemp.setField("YD_STK_BED_NO", 	szYdStkBedNo);
		recTemp.setField("STL_NO",			szStlNo);

		szRtnMsg = JPlateYdToLocUtil.getYdStkBedAnalysis(recTemp, rsTemp, JPlateYdConst.MTL_STAT_C_U_D);

		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYdStkColGp+"], 적치베드["+szYdStkBedNo+"]로 베드정보 조회 완료 - 반환값 : " + szRtnMsg;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO);

		if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYdStkColGp+"], 적치베드["+szYdStkBedNo+"]로 베드정보 조회 시 오류발생";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			return JPlateYdConst.RETN_CD_NOTEXIST;
		}

		//----------------------------------------------------------------------------------------------------------------------
		//	베드상태와 기존 스케줄이 존재하는 지 체크
		//----------------------------------------------------------------------------------------------------------------------
		rsTemp.first();
		recTemp = rsTemp.getRecord();

		szStlNo 			= ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
		szYdStkBedActStat  	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_ACT_STAT");
		szYdStkBedWhioStat 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT");
//		szYdStkLyrMtlStat  	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT");
		int iUpSchCnt		= ydDaoUtils.paraRecChkNullInt(recTemp, "UP_SCH_CNT");

		if (!JPlateYdConst.YD_STK_BED_ACTIVE.equals(szYdStkBedActStat)) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYdStkColGp+"], 적치베드["+szYdStkBedNo+"]의 활성상태["+szYdStkBedActStat+"]가 적치가능상태가 아닙니다.";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			return JPlateYdConst.RETN_BED_INACT;
		}

		if (!JPlateYdConst.YD_STK_BED_WHIO_ENABLE.equals(szYdStkBedWhioStat)) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYdStkColGp+"], 적치베드["+szYdStkBedNo+"]의 입출고상태["+szYdStkBedWhioStat+"]가 입고가능상태가 아닙니다.";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			return JPlateYdConst.RETN_BED_WHIO_NOT_IN;
		}

//		if (JPlateYdConst.YD_STK_LYR_MTL_STAT_UN_WAIT.equals(szYdStkLyrMtlStat)) {				//권상대기이면 적치불가능
//			szLogMsg = "["+ szOperationName +"] 적치된 재료["+szSTL_NO+"]의 적치재료상태["+szYdStkLyrMtlStat+"]가 권상대기이므로 적치불가능";
		if (iUpSchCnt > 0) {
			szLogMsg = "["+ szOperationName +"] 해당 베드에 권상예약 정보가 존재하여 적치불가능";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			return JPlateYdConst.RETN_BED_UN_WAIT;
		}

		//----------------------------------------------------------------------------------------------------------------------
		//	베드 적치가능유무 판단
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYdStkColGp+"], 적치베드["+szYdStkBedNo+"]에 적치가능유무 판단 시작";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO);

		szYdBedErrCd = JPlateYdCommonUtils.chkBedStackable(recPara, recTemp);

		szLogMsg = "["+ szOperationName +"] 해당하는 적치열["+szYdStkColGp+"], 적치베드["+szYdStkBedNo+"]에 적치가능유무 판단 완료 - 반환값 : " + szYdBedErrCd;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO);

		//----------------------------------------------------------------------------------------------------------------------
		//	적치가능한 베드의 적치단을 1 증가 시킴 - 값이 없으면 001(1단)으로 설정
		//----------------------------------------------------------------------------------------------------------------------
		if (JPlateYdConst.RETN_CD_SUCCESS.equals(szYdBedErrCd)) {

			szYdStkLyrNo = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");

			szLogMsg = "["+ szOperationName +"] 적치베드의 적치가능 단["+szYdStkLyrNo+"] - 값이 없으면 001(1단)으로 설정  ";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO);

			if ("".equals(szYdStkLyrNo)) {									//값이 없으면
				szYdStkLyrNo = "001";										//1단
			}

			szLogMsg = "["+ szOperationName +"] 계산된 단["+szYdStkLyrNo+"]";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO);

		} else {

			szYdStkLyrNo = "000";

		}

		recTemp.setField("YD_STK_LYR_NO", szYdStkLyrNo);
		ydUtils.displayRecord(szOperationName, recTemp);

		//----------------------------------------------------------------------------------------------------------------------
		//	JDTORecord를 VO객체로 변환
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 레코드를 VO객체로 변환 시작  ";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO);

		procRecord2StkLoc(recTemp, ydStkLocVO);

		szLogMsg = "["+ szOperationName +"] 레코드를 VO객체로 변환 완료  ";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO);

		return JPlateYdConst.RETN_CD_SUCCESS;

	}

	/**
	 * JDTO Record를 VO객체로 변환하는 메소드
	 * @param recPara
	 * @return
	 * @throws JDTOException
	 */
	public static JPlateYdStkLocVO procRecord2StkLoc(JDTORecord recPara) throws JDTOException {

		JPlateYdStkLocVO ydStkLocVO = new JPlateYdStkLocVO();

		ydStkLocVO.setYdStkColGp(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP"));
		ydStkLocVO.setYdStkBedNo(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO"));
		ydStkLocVO.setYdStkLyrNo(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO"));
		ydStkLocVO.setYdStkBedLGp(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_L_GP"));
		ydStkLocVO.setYdStkBedWGp(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_W_GP"));
		ydStkLocVO.setYdStkBedActStat(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_ACT_STAT"));
		ydStkLocVO.setYdStkBedWhioStat(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_WHIO_STAT"));
		ydStkLocVO.setYdStkBedLyrMax(ydDaoUtils.paraRecChkNullInt(recPara, "YD_STK_BED_LYR_MAX"));
		ydStkLocVO.setYdStkBedWtMax(ydDaoUtils.paraRecChkNullInt(recPara, "YD_STK_BED_WT_MAX"));
		ydStkLocVO.setYdStkBedHMax(ydDaoUtils.paraRecChkNullDouble(recPara, "YD_STK_BED_H_MAX"));
		ydStkLocVO.setYdStkLyrActStat(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_ACT_STAT"));
		ydStkLocVO.setYdStkLyrMtlStat(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT"));

		ydStkLocVO.setYdStkableBedLyr(ydDaoUtils.paraRecChkNullInt(recPara, "YD_STKABLE_BED_LYR"));
		ydStkLocVO.setYdStkableBedWt(ydDaoUtils.paraRecChkNullInt(recPara, "YD_STKABLE_BED_WT"));
		ydStkLocVO.setYdStkableBedH(ydDaoUtils.paraRecChkNullDouble(recPara, "YD_STKABLE_BED_H"));
		ydStkLocVO.setYdBedErrCd(ydDaoUtils.paraRecChkNullInt(recPara, "BED_ERR_CD"));

		return ydStkLocVO;
	}

	/**
	 * JDTO Record를 VO객체로 변환하는 메소드
	 * @param recPara
	 * @return
	 * @throws JDTOException
	 */
	public static void procRecord2StkLoc(JDTORecord recPara, JPlateYdStkLocVO ydStkLocVO) throws JDTOException {

		ydStkLocVO.setYdStkColGp(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP"));
		ydStkLocVO.setYdStkBedNo(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO"));
		ydStkLocVO.setYdStkLyrNo(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO"));
		ydStkLocVO.setYdStkBedLGp(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_L_GP"));
		ydStkLocVO.setYdStkBedWGp(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_W_GP"));
		ydStkLocVO.setYdStkBedActStat(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_ACT_STAT"));
		ydStkLocVO.setYdStkBedWhioStat(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_WHIO_STAT"));
		ydStkLocVO.setYdStkBedLyrMax(ydDaoUtils.paraRecChkNullInt(recPara, "YD_STK_BED_LYR_MAX"));
		ydStkLocVO.setYdStkBedWtMax(ydDaoUtils.paraRecChkNullInt(recPara, "YD_STK_BED_WT_MAX"));
		ydStkLocVO.setYdStkBedHMax(ydDaoUtils.paraRecChkNullDouble(recPara, "YD_STK_BED_H_MAX"));
		ydStkLocVO.setYdStkLyrActStat(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_ACT_STAT"));
		ydStkLocVO.setYdStkLyrMtlStat(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT"));

		ydStkLocVO.setYdStkableBedLyr(ydDaoUtils.paraRecChkNullInt(recPara, "YD_STKABLE_BED_LYR"));
		ydStkLocVO.setYdStkableBedWt(ydDaoUtils.paraRecChkNullInt(recPara, "YD_STKABLE_BED_WT"));
		ydStkLocVO.setYdStkableBedH(ydDaoUtils.paraRecChkNullDouble(recPara, "YD_STKABLE_BED_H"));
		ydStkLocVO.setYdBedErrCd(ydDaoUtils.paraRecChkNullInt(recPara, "BED_ERR_CD"));

	}	
	
	
	/**********************************************************
	* 1후판정정추가 SJH16
	**********************************************************/	 
	/**
	 * RT상TO위치결정(1후판정정)
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @return
	 * @throws JDTOException
	 */
	public static String procRtToLocForPlateYdYdP(JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch) throws JDTOException {

		JPlateYdStockDAO   ydStockDao 	= new JPlateYdStockDAO();
		JPlateYdStkLyrDAO  ydStklyrDao 	= new JPlateYdStkLyrDAO();

		String szMethodName			= "procRtToLocForPlateYdYdP";
		String szOperationName		= "RT상TO위치결정(1후판정정)";
		String szLogMsg				= null;
		String szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		ArrayList	listToLoc		= null;

//		JDTORecord	recLogMsg		= null;
		JDTORecord	recPara			= null;
		JDTORecord	recTemp			= null;
		JDTORecord	recTemp2		= null;
		JDTORecord	recUpCrnSch		= null;
		JDTORecordSet rsResult		= null;

		String 	szStlNo				= null;
//		String[] szYdStkLyrMtlStat	= {"D", "U"};
		String 	szYdUpWoLoc			= null;
		String 	szYdUpWoLayer		= null;
		String 	szYdDnWoLoc			= null;
		String 	szYdDnWoLayer		= null;
		String 	szYdUpStkColGp		= null;
		String 	szYdUpStkBedNo		= null;
		String 	szYdDnStkColGp		= null;
		String 	szYdDnStkBedNo		= null;
		String 	szYdMtlLGp			= null;
		String 	szYdSchCd			= null;
		String	szYdCrnSchId		= null;
		String 	szYdEqpId			= null;
		String	szYdToLocGuide		= null;
		String	szModifier			= null;

		int    	intYdEqpWrkSh		= 0;						//야드설비작업매수
		int    	intYdEqpWrkWt		= 0;						//야드설비작업중량
		double 	dblYdEqpWrkT		= 0;						//야드설비작업총두께
		int		intYdEqpWrkL		= 0;						//야드설비작업총길이
		String 	szYdEqpWrkMaxW		= null;						//작업재료 중 최대 폭
		String 	szYdEqpWrkMaxL		= null;						//작업재료 중 최대 길이
		int    	intRtnVal 			= 0;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.02 로그 개선 
	    String logId                     	= ydLogUtils.getJDTOLogId(recCrnSch, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
	
	    if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 		// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
		      		
	    szLogMsg = "[" + szOperationName + "] ---- recCrnSch.toString()  \n>>>> " + recCrnSch.toString();
	    ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
//-------------------------------------------------------------------------------------------------------------------------

		szLogMsg = "[" + szOperationName + "] 메소드 시작 - 크레인스케줄확인 ";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료의 최하단 재료정보를 가져온다.
		//----------------------------------------------------------------------------------------------------------------------
		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();

		szLogMsg = "[" + szOperationName + "] -------------------- 크레인작업재료의 최하단 재료정보 확인 --------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		ydUtils.displayRecord(szOperationName, recPara);

		szLogMsg = "[" + szOperationName + "] -------------------- 크레인스케줄정보 확인 --------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		ydUtils.displayRecord(szOperationName, recCrnSch);

		szYdCrnSchId    = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID");
		szYdEqpId      	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_EQP_ID");
		szYdToLocGuide	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_TO_LOC_GUIDE");

		intYdEqpWrkSh   = ydDaoUtils.paraRecChkNullInt(recPara, "SH_CNT");
		intYdEqpWrkWt   = ydDaoUtils.paraRecChkNullInt(recPara, "SUM_MTL_WT");
		dblYdEqpWrkT    = ydDaoUtils.paraRecChkNullDouble(recPara, "SUM_MTL_T");
		intYdEqpWrkL    = ydDaoUtils.paraRecChkNullInt(recPara, "MAX_MTL_L");		// "SUM_MTL_L"

		szYdEqpWrkMaxW	= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_W");
		szYdEqpWrkMaxL	= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_L");

		szModifier		= ydDaoUtils.paraRecModifier(recPara);

		szLogMsg = "[" + szOperationName + "] 크레인작업재료의 총매수 :" + intYdEqpWrkSh;
		szLogMsg += ", 총중량 :" + intYdEqpWrkWt;
		szLogMsg += ", 총높이  :" + dblYdEqpWrkT;
		szLogMsg += ", TO위치 :" + szYdToLocGuide;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		//	최하단 재료를 저장품으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szStlNo	= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

		szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보[" + szStlNo + "]를 저장품에서 조회 시작 ";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
		intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsResult);

		if (intRtnVal == 0) {
			szRtnMsg = "[" + szOperationName + "] 존재하지 않습니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szRtnMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		} else if (intRtnVal == -2) {
			szRtnMsg = "[" + szOperationName + "] 파라미터가 존재하지 않습니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szRtnMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		} else if (intRtnVal < 0) {
			szRtnMsg = "[" + szOperationName + "] 오류발생";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szRtnMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		}

		rsResult.first();
		recTemp = rsResult.getRecord();

		szYdMtlLGp = ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L_GP");

		szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보[" + szStlNo + "]를 저장품에서 조회 완료 - 길이구분[" + szYdMtlLGp + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szYdUpStkColGp = recTemp.getFieldString("YD_STK_COL_GP");
		szYdUpStkBedNo = recTemp.getFieldString("YD_STK_BED_NO");
		szYdUpWoLoc    = szYdUpStkColGp + szYdUpStkBedNo;
		szYdUpWoLayer  = recTemp.getFieldString("YD_STK_LYR_NO");

		szLogMsg = "[" + szOperationName + "] 권상지시위치[" + szYdUpWoLoc + "], 권상지시단[" + szYdUpWoLayer + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

    	//----------------------------------------------------------------------------------------------------------------------
    	//	권상지시위치의 정보를 분석해서 RT상의 위치 구하기
    	//----------------------------------------------------------------------------------------------------------------------
    	szLogMsg = "[" + szOperationName + "] >>>> RT상의 베드번호 추출 시작 :: " + szYdToLocGuide;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

    	szYdSchCd = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD");
    	
    	szYdDnStkColGp = ydUtils.substr(szYdToLocGuide, 0, 6);
    	if ("".equals(szYdDnStkColGp) || szYdDnStkColGp.length() < 6) {
    		szYdDnStkColGp = szYdSchCd.substring(0, 6);
    	}

    	szYdDnStkBedNo = JPlateYdToLocUtil.getRtStkLocByBedNoMtlLGp(szYdDnStkColGp, "01", szYdMtlLGp);

    	szLogMsg = "[" + szOperationName + "] >>>> RT상의 베드번호 추출 완료 - 권하지시적치열[" + szYdDnStkColGp + "], 권하지시베드[" + szYdDnStkBedNo + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

    	//----------------------------------------------------------------------------------------------------------------------
    	//	해당베드정보에 적치가능한 지를 체크
    	//YD_TO_LOC_GUIDE	- 사용자지정위치(적치열+적치베드)
		// * 				2) YD_EQP_WRK_SH	- 작업총매수
		// * 				3) YD_EQP_WRK_WT	- 작업총중량
		// * 				4) YD_EQP_WRK_T		- 작업총두께
		// * 				5) YD_EQP_WRK_L		- 작업총길이
		// * 				6) YD_SCH_CD		- 스케줄코드
    	//----------------------------------------------------------------------------------------------------------------------

		szLogMsg = "[" + szOperationName + "] 권하지시적치열[" + szYdDnStkColGp + "], 권하지시베드[" + szYdDnStkBedNo + "]에 적치가능한 지 비교 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

    	listToLoc = new ArrayList();

    	recTemp = JDTORecordFactory.getInstance().create();
    	recTemp.setField("YD_TO_LOC_GUIDE", 	szYdDnStkColGp + szYdDnStkBedNo);
    	recTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(intYdEqpWrkSh));
    	recTemp.setField("YD_EQP_WRK_WT", 		String.valueOf(intYdEqpWrkWt));
    	recTemp.setField("YD_EQP_WRK_T", 		String.valueOf(dblYdEqpWrkT));
    	recTemp.setField("YD_EQP_WRK_L", 		String.valueOf(intYdEqpWrkL));
    	recTemp.setField("YD_SCH_CD", 			szYdSchCd);
    	recTemp.setField("MODIFIER",			szModifier);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 recTemp에 logId 추가 
    	recTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

    	szRtnMsg = JPlateYdToLocUtil.procAsgnedBedStackable(recTemp, listToLoc, szMethodName);

    	int 	intYdBedErrCd	= 0;

    	if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {

    		JPlateYdStkLocVO ydStkLocVO = (JPlateYdStkLocVO)listToLoc.get(0);

    		intYdBedErrCd	= ydStkLocVO.getYdBedErrCd();

    		if (intYdBedErrCd == JPlateYdConst.YD_BED_STACKABLE) {

	    		szYdDnWoLoc 	= ydStkLocVO.getYdStkColGp() + ydStkLocVO.getYdStkBedNo();
	    		szYdDnWoLayer	= ydStkLocVO.getYdStkLyrNo();

	    		//----------------------------------------------------------------------------------------------------------------------
	    		//	적치단의 권하지시위치에 재료별 권하상태로 수정
	    		//----------------------------------------------------------------------------------------------------------------------
	    		recTemp = JDTORecordFactory.getInstance().create();
	    		String szYdDnWoLayer2		= "";
	    		for(int ii=1; ii<=rsCrnwrkmtl.size(); ii++) {
	    			rsCrnwrkmtl.absolute(ii);
	    			recTemp2 = rsCrnwrkmtl.getRecord();

	    			szStlNo = ydDaoUtils.paraRecChkNull(recTemp2, "STL_NO");
	    			szYdDnWoLayer2 = ydDaoUtils.stringPlusInt(szYdDnWoLayer, (ii-1));

	    	    	recTemp.setField("STL_NO", 					szStlNo);
	    	    	recTemp.setField("MODIFIER", 				szModifier);
	    	    	recTemp.setField("YD_STK_COL_GP", 			ydStkLocVO.getYdStkColGp());
	    	    	recTemp.setField("YD_STK_BED_NO", 			ydStkLocVO.getYdStkBedNo());
	    	    	recTemp.setField("YD_STK_LYR_NO", 			szYdDnWoLayer2);
	    	    	recTemp.setField("YD_STK_LYR_MTL_STAT", 	JPlateYdConst.YD_STK_LYR_MTL_STAT_DN_WAIT);

	    	    	szLogMsg = "[" + szOperationName + "] 권하지시위치[" + szYdDnWoLoc + "], 권하지시단[" +szYdDnWoLayer2+ "]에 재료[" + szStlNo + "] 등록 시작";
	        		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.03 recTemp에 logId 추가 
	        		recTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
	        		                            
	    	    	intRtnVal = ydStklyrDao.updYdStklyrStat(recTemp);	// intGp == 0

	    	    	szLogMsg = "[" + szOperationName + "] 권하지시위치[" + szYdDnWoLoc + "], 권하지시단[" +szYdDnWoLayer2+ "]에 재료[" + szStlNo + "] 등록 완료 - 메세지 : " + Integer.toString(intRtnVal);
	        		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

	    		}

	    		szLogMsg = "[" + szOperationName + "] 권하지시위치[" + szYdDnWoLoc + "], 권하지시단[" + szYdDnWoLayer + "]을 크레인스케줄에 수정 시작";
	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

	    		recUpCrnSch = JDTORecordFactory.getInstance().create();
	    		recUpCrnSch.setField("YD_CRN_SCH_ID", 			szYdCrnSchId);
	    		recUpCrnSch.setField("YD_EQP_ID", 				szYdEqpId);

	    		recUpCrnSch.setField("YD_UP_STK_COL_GP", 		szYdUpStkColGp);
				recUpCrnSch.setField("YD_UP_STK_BED_NO", 		szYdUpStkBedNo);
				recUpCrnSch.setField("YD_DN_STK_COL_GP", 		szYdDnStkColGp);
				recUpCrnSch.setField("YD_DN_STK_BED_NO", 		szYdDnStkBedNo);

				//----------------------------------------------------------------------------------------------------------------------
				//	TO위치결정방법이 T인 경우에는 크레인스케줄의 권상지시위치 업데이트
				//----------------------------------------------------------------------------------------------------------------------

	    		if ("T".equals(recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD"))) {
	    			szLogMsg = "[" + szOperationName + "] TO위치결정방법이 T인 경우에는 권상지시위치[" + szYdUpWoLoc + "], 권상지시단[" + szYdUpWoLayer + "] 크레인스케줄에 등록";
	        		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

	    			recUpCrnSch.setField("YD_UP_WO_LOC", 	szYdUpWoLoc);
	    			recUpCrnSch.setField("YD_UP_WO_LAYER", 	szYdUpWoLayer);
	    		}

	    		//----------------------------------------------------------------------------------------------------------------------

	    		recUpCrnSch.setField("YD_DN_WO_LOC", 		szYdDnWoLoc);
	    		recUpCrnSch.setField("YD_DN_WO_LAYER", 		szYdDnWoLayer);

	    		recUpCrnSch.setField("YD_EQP_WRK_SH", 		String.valueOf(intYdEqpWrkSh));
	    		recUpCrnSch.setField("YD_EQP_WRK_WT", 		String.valueOf(intYdEqpWrkWt));
	    		recUpCrnSch.setField("YD_EQP_WRK_T", 		String.valueOf(dblYdEqpWrkT));
	    		recUpCrnSch.setField("YD_EQP_WRK_L", 		String.valueOf(intYdEqpWrkL));
	    		recUpCrnSch.setField("YD_EQP_WRK_MAX_W", 	szYdEqpWrkMaxW);
	    		recUpCrnSch.setField("YD_EQP_WRK_MAX_L",	szYdEqpWrkMaxL);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.02 MODIFIER  szMethodName -> szModifier 변경
	    		recUpCrnSch.setField("MODIFIER",			szModifier);
//	    		recUpCrnSch.setField("MODIFIER", 			(szMethodName.length() > 10) ? szMethodName.substring(0, 10) : szMethodName);
//-------------------------------------------------------------------------------------------------------------------------
	    		
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.02 recUpCrnSch에 logId 추가 
	    		recUpCrnSch.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
	    		
    			szRtnMsg = JPlateYdCrnSchUtil.uptCrnSchXYCordYdP(recUpCrnSch);
    			
	        	szLogMsg = "[" + szOperationName + "] 권하지시위치[" + szYdDnWoLoc + "], 권하지시단[" + szYdDnWoLayer + "]을 크레인스케줄에 수정 완료";
	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

    		} else {
    			/*
    			//--------------------------------------------------------------------------------------------
    			//	해당베드정보에 적치가능한 지를 체크 후 적치불가능이면 로그메세지 전송
    			//--------------------------------------------------------------------------------------------
    			recLogMsg = JDTORecordFactory.getInstance().create();
    			recLogMsg.setField("JMS_TC_CD", 			JPlateYdConst.YDYDJ702);
    			recLogMsg.setField("YD_GP", 				szYdEqpId.substring(0, 1));
    			recLogMsg.setField("MONITORING_CHANNEL", 	"yd_monitorA");
    			recLogMsg.setField("YD_BAY_GP", 			szYdEqpId.substring(1, 2));
    			recLogMsg.setField("YD_EQP_ID", 			szYdEqpId);
    			recLogMsg.setField("YD_SCH_CD", 			szYdSchCd);
    			recLogMsg.setField("YD_EVT_GP", 			JPlateYdConst.YD_EVT_CRANE);
    			recLogMsg.setField("YD_MSG_OUTPWR_GRD",		"E");
    			recLogMsg.setField("YD_PGM_TP", 			JPlateYdConst.YD_PGMGP_SCH);
    			recLogMsg.setField("YD_IF_CD", 				"");
    			recLogMsg.setField("YD_E_J_B_ID", 			SZ_CLASS_NAME);
    			recLogMsg.setField("YD_MSG_NM", 			szMethodName);

    			JPlateYdCommonUtils.sndLogMsgForBedStatusInfo(intYdBedErrCd, szYdCrnSchId, szYdDnStkColGp, szYdDnStkBedNo, recLogMsg);
    			*/
    			//--------------------------------------------------------------------------------------------
    			szRtnMsg = "권하지시적치열[" + szYdDnStkColGp + "], 권하지시베드[" + szYdDnStkBedNo + "]에 적치불가능 - 베드에러코드[" + intYdBedErrCd + "]";
    			szLogMsg = "[" + szOperationName + "] " + szRtnMsg;
    			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
    			return szRtnMsg;
    		}

    	} else {
    		/*
    		//--------------------------------------------------------------------------------------------
			//	해당베드정보에 적치가능한 지를 체크 후 적치불가능이면 로그메세지 전송
			//--------------------------------------------------------------------------------------------
    		recLogMsg = JDTORecordFactory.getInstance().create();
    		recLogMsg.setField("JMS_TC_CD", 				JPlateYdConst.YDYDJ702);
    		recLogMsg.setField("YD_GP", 					szYdEqpId.substring(0, 1));
    		recLogMsg.setField("MONITORING_CHANNEL", 		"yd_monitorA");
    		recLogMsg.setField("YD_BAY_GP", 				szYdEqpId.substring(1, 2));
    		recLogMsg.setField("YD_EQP_ID", 				szYdEqpId);
    		recLogMsg.setField("YD_SCH_CD", 				szYdSchCd);
    		recLogMsg.setField("YD_EVT_GP", 				JPlateYdConst.YD_EVT_CRANE);
    		recLogMsg.setField("YD_MSG_OUTPWR_GRD", 		"E");
    		recLogMsg.setField("YD_PGM_TP", 				JPlateYdConst.YD_PGMGP_SCH);
    		recLogMsg.setField("YD_IF_CD", 					"");
    		recLogMsg.setField("YD_E_J_B_ID", 				SZ_CLASS_NAME);
    		recLogMsg.setField("YD_MSG_NM", 				szMethodName);

    		JPlateYdCommonUtils.sndLogMsgForBedInfo(szRtnMsg, szYdCrnSchId, szYdDnStkColGp, szYdDnStkBedNo, recLogMsg);
			*/
    		szRtnMsg = "권하지시적치열[" + szYdDnStkColGp + "], 권하지시베드[" + szYdDnStkBedNo + "]에 적치불가능 - 에러메세지[" + szRtnMsg + "]";
    		szLogMsg = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
    	}

    	szLogMsg = "[" + szOperationName + "] 권하지시적치열[" + szYdDnStkColGp + "], 권하지시베드[" + szYdDnStkBedNo + "]에 적치가능한 지 비교 완료";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}
	
	/**
	 * 주작업TO위치결정(1후판정정)
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public static String procMainWrkToLocForPlateYdYdP(JDTORecord msgRecord					/* 전문 */
													, JDTORecordSet rsCrnwrkmtl				/* 크레인작업재료 */
													, JDTORecord recCrnSch					/* 크레인스케줄정보 */
													, JDTORecord recWbook					/* 작업예약정보 */
													) throws JDTOException {

		JPlateYdStkBedDAO 	ydStkbedDao	= new JPlateYdStkBedDAO();
//		JPlateYdStkLyrDAO 	ydStkLyrDao	= new JPlateYdStkLyrDAO();
		JPlateYdStockDAO	ydStockDao	= new JPlateYdStockDAO();
		JPlateYdCrnSchDAO	ydCrnSchDao	= new JPlateYdCrnSchDAO();

		String szMethodName				= "procMainWrkToLocForPlateYdYdP";
		String szOperationName			= "1후판 주작업TO위치결정";
		String szDesc					= "";
		String szLogMsg					= null;
		String szRtnMsg 				= JPlateYdConst.RETN_CD_SUCCESS;

		ArrayList		 listToLoc		= null;
		JPlateYdStkLocVO ydStkLocVO		= null;

		JDTORecordSet	rsResult		= null;
		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;

		String 	szYD_UP_WO_LOC			= null;
		String 	szYD_UP_WO_LAYER		= null;
		String 	szYD_DN_WO_LOC			= null;
		String 	szYD_DN_WO_LAYER		= null;
		String 	szYD_UP_STK_COL_GP		= null;
		String 	szYD_UP_STK_BED_NO		= null;
		String 	szYD_DN_STK_COL_GP		= null;
		String 	szYD_DN_STK_BED_NO		= null;
		String 	szYD_SCH_CD				= null;
		String	szSTL_NO				= null;

		boolean bUP_UPDT_NEEDED			= false;

		String[] szYD_STK_LYR_MTL_STAT	= {"D", "U"};

		String	szYD_TO_LOC_GUIDE		= null;

		String 	szYD_CRN_SCH_ID			= null;
		String 	szYD_EQP_ID				= null;

		String 	szYD_MTL_W_GP			= null;						//야드재료폭구분
		String 	szYD_MTL_L_GP			= null;						//야드재료길이구분

		int 	intYD_EQP_WRK_SH		= 0;						//야드설비작업매수
		int 	intYD_EQP_WRK_WT		= 0;						//야드설비작업중량
		double 	dblYD_EQP_WRK_T			= 0;						//야드설비작업총두께
		int		intYD_EQP_WRK_L			= 0;						//야드설비작업총두께
		String 	szYD_EQP_WRK_MAX_W		= null;						//작업재료 중 최대 폭
		String 	szYD_EQP_WRK_MAX_L		= null;						//작업재료 중 최대 길이
		String 	szYD_STK_BED_L_GP		= null;
		String 	szYD_STK_BED_W_GP		= null;
		String 	szYD_STK_BED_WHIO_STAT	= null;
		String	szYD_FR_BAY				= "";
		String	szYD_TO_BAY				= "";
		String	szYD_TCAR_GP			= "";

		int 	intRtnVal               = 0;
		boolean bIsToLocStackable		= false;
		boolean bIS_BED_STACKABLE		= false;
		boolean isSameBay 				= false;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                    	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
//		String	szOCPY_BED_ERR			= null;						//점유베드 체크필드
		String	szMODIFIER				= ydDaoUtils.paraRecModifier(msgRecord);

		//----------------------------------------------------------------------------------------------------------------------
		//------------------------------------- 사용자정의위치에 대한 TO위치결정
		//----------------------------------------------------------------------------------------------------------------------

		szLogMsg = "[" + szOperationName + "] ---------------------- TO_LOC START";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szLogMsg = "[" + szOperationName + "] ---------------------- msgRecord :: " + msgRecord.toString();
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szLogMsg = "[" + szOperationName + "] ---------------------- recWbook :: " + recWbook.toString();
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		// 작업예약의 대차설비 ID 조회
		szYD_TCAR_GP = ydDaoUtils.paraRecChkNull(recWbook, "YD_WRK_PLAN_TCAR");

		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료의 최하단 재료정보를 가져온다.
		//----------------------------------------------------------------------------------------------------------------------
		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();

		szYD_SCH_CD	 = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");

		szLogMsg = "[" + szOperationName + "] szYD_SCH_CD :: " + szYD_SCH_CD;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		if (szYD_SCH_CD == null || szYD_SCH_CD.length() != 8) {
			szRtnMsg = "스케줄코드 오류발생 :: " + szYD_SCH_CD;
			szLogMsg = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		}

		if ("PT".equals(szYD_SCH_CD.substring(2, 4))) {
			szDesc	= "차량";
		} else if ("RT".equals(szYD_SCH_CD.substring(2, 4))) {
			szDesc	= "RollerTable";
		} else if ("TF".equals(szYD_SCH_CD.substring(2, 4))) {
			szDesc	= "Transfer";
		} else if ("CN".equals(szYD_SCH_CD.substring(2, 4))) {
			szDesc	= "가스장보급";
		} else if ("BS".equals(szYD_SCH_CD.substring(2, 4))) {
			szDesc	= "보수장보급";
		} else if ("YD".equals(szYD_SCH_CD.substring(2, 4))) {
			szDesc	= "야드";
		}

		if ("TC".equals(szYD_SCH_CD.substring(2, 4))) {
			szDesc	= "대차";
			if ("U".equals(szYD_SCH_CD.substring(6, 7))) {
				szDesc += " 상차";
			} else {
				szDesc += " 하차";
			}
		} else {
			if ("L".equals(szYD_SCH_CD.substring(6, 7))) {
				szDesc += " BOOK-OUT";
			} else if ("U".equals(szYD_SCH_CD.substring(6, 7))) {
				szDesc += " BOOK-IN";
			} else if ("M".equals(szYD_SCH_CD.substring(6, 7))) {
				szDesc += " 이적";
			}
		}

		if (!"".equals(szDesc)) {
			szOperationName	+= "-" + szDesc;
		}

		szYD_CRN_SCH_ID     = ydDaoUtils.paraRecChkNull(recCrnSch,		"YD_CRN_SCH_ID");				//크레인스케줄ID
		szYD_EQP_ID      	= ydDaoUtils.paraRecChkNull(recCrnSch,		"YD_EQP_ID");					//크레인설비ID

		intYD_EQP_WRK_SH    = ydDaoUtils.paraRecChkNullInt(recPara,		"SH_CNT");						//크레인작업재료 총매수
		intYD_EQP_WRK_WT    = ydDaoUtils.paraRecChkNullInt(recPara,		"SUM_MTL_WT");					//크레인작업재료 총중량
		dblYD_EQP_WRK_T     = ydDaoUtils.paraRecChkNullDouble(recPara,	"SUM_MTL_T");					//크레인작업재료 총높이
		intYD_EQP_WRK_L		= ydDaoUtils.paraRecChkNullInt(recPara,		"MAX_MTL_L");	//SUM_MTL_L		//크레인작업재료 총길이

		szYD_EQP_WRK_MAX_W	= ydDaoUtils.paraRecChkNull(recPara, 		"MAX_MTL_W");					//크레인작업재료 중 최대 폭
		szYD_EQP_WRK_MAX_L	= ydDaoUtils.paraRecChkNull(recPara, 		"MAX_MTL_L");					//크레인작업재료 중 최대 길이
		szYD_TO_LOC_GUIDE	= ydDaoUtils.paraRecChkNull(recWbook, 		"YD_TO_LOC_GUIDE");

		/* 표준화 지적사항 반영 >>>> StringBuffer 사용
		szLogMsg  = "[" + szOperationName + "] 크레인작업재료의 총매수 :: " + intYD_EQP_WRK_SH;
		szLogMsg += ", 총중량 :" + intYD_EQP_WRK_WT;
		szLogMsg += ", 총높이 :" + dblYD_EQP_WRK_T;
		szLogMsg += ", 최대폭 :" + szYD_EQP_WRK_MAX_W;
		szLogMsg += ", 최대길이 :" + szYD_EQP_WRK_MAX_L;
		szLogMsg += ", TO GUIDE :" + szYD_TO_LOC_GUIDE;
		szLogMsg += ", 대차ID :" + szYD_TCAR_GP;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
		 */
		StringBuffer szLogBuff = new StringBuffer();
		szLogBuff.append("[" + szOperationName + "] 크레인작업재료의 총매수 :: " + intYD_EQP_WRK_SH);
		szLogBuff.append(", 총중량 :" + intYD_EQP_WRK_WT);
		szLogBuff.append(", 총높이 :" + dblYD_EQP_WRK_T);
		szLogBuff.append(", 총길이 :" + intYD_EQP_WRK_L);
		szLogBuff.append(", 최대폭 :" + szYD_EQP_WRK_MAX_W);
		szLogBuff.append(", 최대길이 :" + szYD_EQP_WRK_MAX_L);
		szLogBuff.append(", TO GUIDE :" + szYD_TO_LOC_GUIDE);
		szLogBuff.append(", 대차ID :" + szYD_TCAR_GP);
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogBuff.toString(), JPlateYdConst.DEBUG, logId);

		if (szYD_TO_LOC_GUIDE.length() == 8) {
			szYD_DN_STK_COL_GP = szYD_TO_LOC_GUIDE.substring(0, 6);
			szYD_DN_STK_BED_NO = szYD_TO_LOC_GUIDE.substring(6);
		}

		szLogMsg = "[" + szOperationName + "] ---------------------- 야드To위치Guide :: " + szYD_TO_LOC_GUIDE;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		//	최하단 재료를 저장품으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

		szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보[" + szSTL_NO + "]를 저장품에서 조회 시작 ";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		rsResult  = JDTORecordFactory.getInstance().createRecordSet("");

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 recPara에 logId 추가 
		recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
		
		intRtnVal = ydStockDao.getYdStock(recPara, rsResult);
		if (intRtnVal == 0) {
			szRtnMsg = "재료번호 [" + szSTL_NO + "]가 존재하지 않습니다.";
			szLogMsg = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		} else if (intRtnVal == -2) {
			szRtnMsg = "파라미터가 존재하지 않습니다.";
			szLogMsg = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		} else if (intRtnVal < 0) {
			szRtnMsg = "재료정보 조회시 오류발생 :: " + Integer.toString(intRtnVal);
			szLogMsg = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		}

		rsResult.first();
		recTemp = rsResult.getRecord();

		szYD_MTL_L_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L_GP");			//크레인작업 최하단재료의 길이구분
		szYD_MTL_W_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_W_GP");			//크레인작업 최하단재료의 폭구분

		//szYD_RCPT_PLN_STR_LOC = ydDaoUtils.paraRecChkNull(recTemp, "YD_RCPT_PLN_STR_LOC");//입고예정위치

		szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보[" + szSTL_NO + "]를 저장품에서 조회 완료 - 길이구분[" + szYD_MTL_L_GP + "], 폭구분[" + szYD_MTL_W_GP + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		//	권하중이거나 권상중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYD_UP_WO_LOC 	 = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");
		szYD_UP_WO_LAYER = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LAYER");

		if ("".equals(szYD_UP_WO_LOC)) {

			szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보[" + szSTL_NO + "]에 대한 권하 또는 권상위치 조회 시작 ";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			szRtnMsg = JPlateYdCommonUtils.getYdStkLyrWithMtlStat(recPara, rsResult, szYD_STK_LYR_MTL_STAT);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				return szRtnMsg;
			}

			rsResult.first();
			recTemp = rsResult.getRecord();

			szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보[" + szSTL_NO + "]에 대한 권하 또는 권상위치 조회 완료  ";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			szYD_UP_STK_COL_GP	= recTemp.getFieldString("YD_STK_COL_GP");
			szYD_UP_STK_BED_NO 	= recTemp.getFieldString("YD_STK_BED_NO");
			szYD_UP_WO_LOC 		= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYD_UP_WO_LAYER 	= recTemp.getFieldString("YD_STK_LYR_NO");

			bUP_UPDT_NEEDED		= true;

			szLogMsg = "[" + szOperationName + "] 조회된 권상지시위치[" + szYD_UP_WO_LOC + "], 권상지시단[" + szYD_UP_WO_LAYER + "]";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		} else {
			szYD_UP_STK_COL_GP = szYD_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYD_UP_WO_LOC.substring(6);

			szLogMsg = "[" + szOperationName + "] 크레인스케줄에 등록된 권상지시위치[" + szYD_UP_WO_LOC + "], 권상지시단[" + szYD_UP_WO_LAYER + "]";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		}

		//----------------------------------------------------------------------------------------------------------------------
    	//	해당베드정보에 적치가능한 지를 체크
    	//YD_TO_LOC_GUIDE	- 사용자지정위치(적치열+적치베드)
		// * 				2) YD_EQP_WRK_SH	- 작업총매수
		// * 				3) YD_EQP_WRK_WT	- 작업총중량
		// * 				4) YD_EQP_WRK_T		- 작업총두께
		// * 				5) YD_SCH_CD		- 스케줄코드
    	//----------------------------------------------------------------------------------------------------------------------

		szLogMsg = "[" + szOperationName + "] 야드To위치Guide[" + szYD_TO_LOC_GUIDE + "]과 권상지시적치열[" + szYD_UP_STK_COL_GP + "]의 동이 같은 지 비교 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		//	TO위치가이드와 권상지시위치의 동이 다른 경우에는 TO위치가이드가 적치가능한 지를 체크하는 부분을 SKIP한다.
		//----------------------------------------------------------------------------------------------------------------------
		szYD_FR_BAY = ydUtils.substr(szYD_UP_STK_COL_GP, 1, 1);
		szYD_TO_BAY = ydUtils.substr(szYD_TO_LOC_GUIDE,  1, 1);
		if (szYD_TO_BAY.equals(szYD_FR_BAY)) {
			isSameBay = true;
			szLogMsg = "[" + szOperationName + "] 야드To위치Guide[" + szYD_TO_LOC_GUIDE + "]과 권상지시적치열[" + szYD_UP_STK_COL_GP + "]의 동이 같은 지 비교 완료 - 동이 같음";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		} else {
			szLogMsg = "[" + szOperationName + "] 야드To위치Guide[" + szYD_TO_LOC_GUIDE + "]과 권상지시적치열[" + szYD_UP_STK_COL_GP + "]의 동이 같은 지 비교 완료 - 동이 다름";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		}

		listToLoc = new ArrayList();

		// 입고시 예정위치정보는 SKIP
		//if (!"L".equals(szYD_SCH_CD.substring(6, 7)) && szYD_TO_LOC_GUIDE.length() == 8) {
//		if (szYD_TO_LOC_GUIDE.length() == 8) {
		if (szYD_TO_LOC_GUIDE.length() >= 6) {

			//----------------------------------------------------------------------------------------------------------------------
			bIsToLocStackable = false;

			if (isSameBay) {

				//----------------------------------------------------------------------------------------------------------------------
				//	일반야드인 경우에는 입고, 이적인 경우 최하단 크레인작업재료와 TO위치가이드위치의 Bed Size가 다르면 제외
				//	베드의 야드적치Bed입출고상태가 E(입출고가능)가 아니면 베드 제외
				//----------------------------------------------------------------------------------------------------------------------

				if (szYD_TO_LOC_GUIDE.substring(2, 4).matches("\\d\\d")) {	//일반야드는 숫자이므로 설비는 숫자가 아님

					//일반야드인 경우에는 입고, 이적인 경우 최하단 크레인작업재료와 TO위치가이드위치의 Bed Size가 다르면 제외
					//베드의 야드적치Bed입출고상태가 E(입출고가능)가 아니면 베드 제외

					szLogMsg = "[" + szOperationName + "] 야드To위치Guide[" + szYD_TO_LOC_GUIDE + "]가 적치가능한 size[폭,길이]와 야드적치Bed입출고상태를 체크하기 위해 조회 시작";
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

					rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
					recTemp  = JDTORecordFactory.getInstance().create();
			    	recTemp.setField("YD_STK_COL_GP", 	ydUtils.substr(szYD_TO_LOC_GUIDE, 0, 6));
			    	recTemp.setField("YD_STK_BED_NO", 	(szYD_TO_LOC_GUIDE.length() > 6) ? ydUtils.substr(szYD_TO_LOC_GUIDE, 6, 2) : "01");


//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 recTemp에 logId 추가 
			    	recTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
			    	intRtnVal = ydStkbedDao.getYdStkbed(recTemp, rsResult);		// intGp == 0

			    	if (intRtnVal > 0) {

			    		rsResult.first();
			    		recTemp = rsResult.getRecord();

			    		szYD_STK_BED_L_GP		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_L_GP");
			    		szYD_STK_BED_W_GP		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_W_GP");
			    		szYD_STK_BED_WHIO_STAT	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_WHIO_STAT");

			    		if ("E".equals(szYD_STK_BED_WHIO_STAT)) {

			    			szLogMsg = "[" + szOperationName + "] 야드To위치Guide[" + szYD_TO_LOC_GUIDE + "]의 야드적치Bed입출고상태가 입출고가능하므로 적치가능하므로 재료와 베드의 size비교 시작";
							ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

							if (szYD_STK_BED_L_GP.length() == 1 && szYD_STK_BED_W_GP.length() == 1) {

			    				szLogMsg = "[" + szOperationName + "] 야드To위치Guide[" + szYD_TO_LOC_GUIDE + "]에 폭구분[" + szYD_STK_BED_W_GP + "], 길이구분[" + szYD_STK_BED_L_GP + "]이 동일하므로 적치가능";
								ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
								bIsToLocStackable = true;
				    		} else {
				    			szLogMsg = "[" + szOperationName + "] 야드To위치Guide[" + szYD_TO_LOC_GUIDE + "]에 폭구분[" + szYD_STK_BED_W_GP + "], 길이구분[" + szYD_STK_BED_L_GP + "]이 존재하지 않습니다.";
								ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
				    		}
			    		} else {
			    			szLogMsg = "[" + szOperationName + "] 야드To위치Guide[" + szYD_TO_LOC_GUIDE + "]의 야드적치Bed입출고상태가 입출고가능하지 않으므로 적치불가능";
							ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			    		}
			    	} else {
			    		return szRtnMsg;
			    	}
				} else {
					bIsToLocStackable = true;
				}

				//----------------------------------------------------------------------------------------------------------------------
				//	TO위치가이드와 권상지시위치의 동이 같으므로 TO위치가이드가 적치가능한 지를 비교
				//----------------------------------------------------------------------------------------------------------------------
				if (bIsToLocStackable) {

					szLogMsg = "[" + szOperationName + "] 권하지시적치열 [" + szYD_DN_STK_COL_GP + "], 권하지시베드[" + szYD_DN_STK_BED_NO + "]에 적치가능한 지 비교 시작";
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			    	recTemp = JDTORecordFactory.getInstance().create();
			    	recTemp.setField("YD_TO_LOC_GUIDE", 	szYD_TO_LOC_GUIDE);
			    	recTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(intYD_EQP_WRK_SH));				//크레인작업재료 총매수
			    	recTemp.setField("YD_EQP_WRK_WT", 		String.valueOf(intYD_EQP_WRK_WT));				//크레인작업재료 총중량
			    	recTemp.setField("YD_EQP_WRK_T", 		String.valueOf(dblYD_EQP_WRK_T));				//크레인작업재료 총높이
			    	recTemp.setField("YD_EQP_WRK_L", 		String.valueOf(intYD_EQP_WRK_L));				//크레인작업재료 총길이
			    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);
			    	recTemp.setField("STL_NO", 				szSTL_NO);
			    	recTemp.setField("MODIFIER",			szMODIFIER);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 recSchPara에 logId 추가 
			    	recTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
			    	
		    		szRtnMsg = JPlateYdToLocUtil.procAsgnedBedStackable(recTemp, listToLoc, szMethodName);

			    	if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
			    		
			    		ydStkLocVO = (JPlateYdStkLocVO)listToLoc.get(0);
			    		ydStkLocVO.setSeq(JPlateYdConst.TO_LOC_PRIOR_USER);
			    		ydStkLocVO.setPrior(JPlateYdConst.TO_LOC_PRIOR_USER);
			    	}

			    	szLogMsg = "[" + szOperationName + "] 권하지시적치열[" + szYD_DN_STK_COL_GP + "], 권하지시베드[" + szYD_DN_STK_BED_NO + "]에 적치가능한 지 비교 완료 - 메세지 : " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
				} else {
					szLogMsg = "[" + szOperationName + "] 권하지시적치열[" + szYD_DN_STK_COL_GP + "], 권하지시베드[" + szYD_DN_STK_BED_NO + "]에 적치불가능하므로 적치가능비교하지 않음";
					ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
				}
			}//end if (isSameBay)

		} else {

			szLogMsg = "[" + szOperationName + "] 야드To위치Guide[" + szYD_TO_LOC_GUIDE + "]가 존재하지 않거나 자리수(8)가 맞지 않으므로 해당 TO위치Guide를 검색하지 않습니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		}

		//----------------------------------------------------------------------------------------------------------------------
		//	입고예정위치가 존재하지 않는 경우에는  길이구분/폭구분이 같은 해당 동의 모든 위치를 조회
		//----------------------------------------------------------------------------------------------------------------------
    	//szYD_GP		= szYD_UP_WO_LOC.substring(0, 1);
    	//szYD_BAY_GP	= szYD_UP_WO_LOC.substring(1, 2);
    	//szYD_EQP_GP	= szYD_UP_WO_LOC.substring(2, 4);

    	//권상지시위치에 따라 알맞은 적치가능한 베드 검색 방법을 적용
    	recTemp = JDTORecordFactory.getInstance().create();
    	recTemp.setField("YD_TO_LOC_GUIDE", 	szYD_TO_LOC_GUIDE);
    	recTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(intYD_EQP_WRK_SH));		//크레인작업재료 총매수
    	recTemp.setField("YD_EQP_WRK_WT", 		String.valueOf(intYD_EQP_WRK_WT));		//크레인작업재료 총중량
    	recTemp.setField("YD_EQP_WRK_T", 		String.valueOf(dblYD_EQP_WRK_T));		//크레인작업재료 총높이
    	recTemp.setField("YD_EQP_WRK_L", 		String.valueOf(intYD_EQP_WRK_L));		//크레인작업재료 총길이
    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);							//크레인스케줄코드
    	recTemp.setField("YD_MTL_L_GP", 		szYD_MTL_L_GP);							//크레인작업 최하단재료의 길이구분
    	recTemp.setField("YD_MTL_W_GP", 		szYD_MTL_W_GP);							//크레인작업 최하단재료의 폭구분
    	recTemp.setField("YD_STK_COL_GP", 		szYD_UP_STK_COL_GP);					//권상지시위치 - 적치열
    	recTemp.setField("YD_STK_BED_NO", 		szYD_UP_STK_BED_NO);					//권상지시위치 - 적치베드
    	recTemp.setField("YD_EQP_ID", 			szYD_EQP_ID);							//크레인설비ID
    	recTemp.setField("STL_NO", 				szSTL_NO);
    	recTemp.setField("YD_TCAR_GP",			szYD_TCAR_GP);							//대차구분
    	recTemp.setField("MODIFIER",			szMODIFIER);

		//----------------------------------------------------------------------------------------------------------------------
		// Sorting ?
		//----------------------------------------------------------------------------------------------------------------------
		if (listToLoc.size() > 0) {
			Collections.sort(listToLoc, new JPlateYdStkLocComparator());
		}

		//----------------------------------------------------------------------------------------------------------------------
		//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
		//----------------------------------------------------------------------------------------------------------------------
		for (int ii=0; ii<listToLoc.size(); ii++) {

			ydStkLocVO = (JPlateYdStkLocVO)listToLoc.get(ii);

			szLogMsg = "[" + szOperationName + "] [" + (ii + 1) + "] 조회된 베드[" + ydStkLocVO.getYdStkColGp() + " - " + ydStkLocVO.getYdStkBedNo() + "]의 오류코드 확인[" + ydStkLocVO.getYdBedErrCd() + "]";
    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

    		if (ydStkLocVO.getYdBedErrCd() == JPlateYdConst.YD_BED_STACKABLE) {

				bIS_BED_STACKABLE = true;

				szLogMsg  = "\n[" + szOperationName + "] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
	    		szLogMsg += "[" + szOperationName + "] +++++++++++++++ [" + (ii + 1) + "] 조회된 베드[" + ydStkLocVO.getYdStkColGp() + " - " + ydStkLocVO.getYdStkBedNo() + "]에 적치가능함 +++++++++++++++\n";
	    		szLogMsg += "[" + szOperationName + "] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

				break;
			}
		}
		//25.07.11 김대현 매니저 요청 - A동 book-out시, 권하지시위치가 권상베드 등으로 사용중이라면 to위치 다른 곳 탐색 X. RITM1212853
		//야드가 협소하여, to위치 지정한 곳으로 무조건 가야함 
		//a동 이고 R/T 입고 시, to위치 guide 적치 불가면 xx 띄우자
		String ydGp = ydUtils.substr(szYD_UP_STK_COL_GP, 0, 1);
		String ydBayGp = ydUtils.substr(szYD_UP_STK_COL_GP, 1, 1);
		String ydEqpGp = ydUtils.substr(szYD_UP_STK_COL_GP, 2, 2);
		
		JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
		
		String sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A034"); //
		
		ydUtils.putLog(szOperationName, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 R/T입고 신규방식(A034) 적용여부  : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
		
		
		//1후판 정정야드의 r/t 입고작업에서 to위치 guide 가 적치불가능일시 
		if (sNEW_MODULE_EFF_YN.equals("Y") && "RT".equals(ydEqpGp) && "A".equals(ydBayGp) && "P".equals(ydGp) && !bIS_BED_STACKABLE) {
			szLogMsg = "[" + szOperationName + "] 1후판 정정야드 R/T 입고작업은 to위치 guide 적치 불가시 to위치 xx 처리";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
//			
			// 2013.07.19 김현우 TO위치 결정못해도 크레인작업재료 총매수/중량/높이는 UPDATE 하도록 보완
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);								//크레인스케줄ID
			recPara.setField("YD_EQP_WRK_SH", 			String.valueOf(intYD_EQP_WRK_SH));				//크레인작업재료 총매수
			recPara.setField("YD_EQP_WRK_WT", 			String.valueOf(intYD_EQP_WRK_WT));				//크레인작업재료 총중량
			recPara.setField("YD_EQP_WRK_T", 			String.valueOf(dblYD_EQP_WRK_T));				//크레인작업재료 총높이
			recPara.setField("YD_EQP_WRK_L", 			String.valueOf(intYD_EQP_WRK_L));				//크레인작업재료 총높이
			recPara.setField("YD_EQP_WRK_MAX_W", 		szYD_EQP_WRK_MAX_W);							//크레인작업재료 중 최대 폭
			recPara.setField("YD_EQP_WRK_MAX_L", 		szYD_EQP_WRK_MAX_L);							//크레인작업재료 중 최대 길이

			intRtnVal = ydCrnSchDao.upYdEqpWrkInfo(recPara);

			szLogMsg = "[" + szOperationName + "] 크레인작업재료 총매수 UPDATE 결과 >>>> " + intRtnVal;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			szLogMsg = "[" + szOperationName + "] 1후판 정정야드 A동 R/T 입고작업은 to위치 guide 적치 불가시 to위치 xx처리";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			return JPlateYdConst.RETN_NOT_EXIST_BED_ABAYIN;
		}
		

		//----------------------------------------------------------------------------------------------------------------------
		// 길이구분/폭구분이 동일한 공베드를 조회 : TO위치가이드가 적치가능 베드가 아닐때
		//----------------------------------------------------------------------------------------------------------------------
		if (!bIS_BED_STACKABLE) {

			//------------------------------------------------------------------------------
			// 위에서 조회된 결과를 갖고 있으므로 사용하기 전에 값을 초기화시킴
			//------------------------------------------------------------------------------
			ydStkLocVO = null;
			listToLoc  = new ArrayList();

			szLogMsg = "[" + szOperationName + "] 길이구분[" + szYD_MTL_L_GP + "]/폭구분[" + szYD_MTL_W_GP + "]이 동일한 적치가능한 공베드 조회 시작";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			szRtnMsg = getEmptyBedWithSameLWGpYdP(recTemp, listToLoc);

			szLogMsg = "[" + szOperationName + "] 길이구분[" + szYD_MTL_L_GP + "]/폭구분[" + szYD_MTL_W_GP + "]이 동일한 적치가능한 공베드 조회 완료 - 메세지 :: " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			//----------------------------------------------------------------------------------------------------------------------
			// Sorting
			//----------------------------------------------------------------------------------------------------------------------
			szLogMsg = "[" + szOperationName + "] Sorting Start";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			szLogMsg = "[" + szOperationName + "] listToLoc Size >>>> " + listToLoc.size();
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			if (listToLoc.size() > 0) {
				Collections.sort(listToLoc, new JPlateYdStkLocComparator());
			}

			szLogMsg = "[" + szOperationName + "] Sorting End";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			//----------------------------------------------------------------------------------------------------------------------
			//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
			//----------------------------------------------------------------------------------------------------------------------
			for(int ii=0; ii<listToLoc.size(); ii++) {

				ydStkLocVO = (JPlateYdStkLocVO)listToLoc.get(ii);

				szLogMsg = "[" + szOperationName + "] [" + (ii+1) + "] 주작업 TO위치 결정 적치가능 판단 >>>> [" + ydStkLocVO.getYdStkColGp() + " - " + ydStkLocVO.getYdStkBedNo() + "], 오류코드::[" + ydStkLocVO.getYdBedErrCd() + "]";
	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

	    		if (ydStkLocVO.getYdBedErrCd() == JPlateYdConst.YD_BED_STACKABLE) {

					bIS_BED_STACKABLE = true;

					szLogMsg = "[" + szOperationName + "] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
		    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
					szLogMsg = "[" + szOperationName + "] +++++++++++++++ [" + (ii + 1) + "] 길이구분[" + szYD_MTL_L_GP + "]/폭구분[" + szYD_MTL_W_GP + "]이 동일한 조회된 공베드[" + ydStkLocVO.getYdStkColGp() + " - " + ydStkLocVO.getYdStkBedNo() + "]에 적치가능함 +++++++++++++++";
    	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
    	    		szLogMsg = "[" + szOperationName + "] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
    	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

					break;
				}
			}
		}

		
		if (!bIS_BED_STACKABLE) {
			szLogMsg = "[" + szOperationName + "] 권하위치검색 실패 - 적치가능한 베드가 존재하지 않습니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			// 2013.07.19 김현우 TO위치 결정못해도 크레인작업재료 총매수/중량/높이는 UPDATE 하도록 보완
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);								//크레인스케줄ID
			recPara.setField("YD_EQP_WRK_SH", 			String.valueOf(intYD_EQP_WRK_SH));				//크레인작업재료 총매수
			recPara.setField("YD_EQP_WRK_WT", 			String.valueOf(intYD_EQP_WRK_WT));				//크레인작업재료 총중량
			recPara.setField("YD_EQP_WRK_T", 			String.valueOf(dblYD_EQP_WRK_T));				//크레인작업재료 총높이
			recPara.setField("YD_EQP_WRK_L", 			String.valueOf(intYD_EQP_WRK_L));				//크레인작업재료 총높이
			recPara.setField("YD_EQP_WRK_MAX_W", 		szYD_EQP_WRK_MAX_W);							//크레인작업재료 중 최대 폭
			recPara.setField("YD_EQP_WRK_MAX_L", 		szYD_EQP_WRK_MAX_L);							//크레인작업재료 중 최대 길이

			intRtnVal = ydCrnSchDao.upYdEqpWrkInfo(recPara);

			szLogMsg = "[" + szOperationName + "] 크레인작업재료 총매수 UPDATE 결과 >>>> " + intRtnVal;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			return JPlateYdConst.RETN_NOT_EXIST_BED;

		}

		/*
		 * 권하위치 최종결정정보 셋팅
		 */
		szYD_DN_STK_COL_GP 	= ydStkLocVO.getYdStkColGp();
		szYD_DN_STK_BED_NO 	= ydStkLocVO.getYdStkBedNo();
		szYD_DN_WO_LOC		= szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;
		szYD_DN_WO_LAYER 	= ydStkLocVO.getYdStkLyrNo();
	
		// 보수장일때 점유베드 체크
		/*
		if ("BS".equals(szYD_DN_STK_COL_GP.substring(2, 4))) {
			szLogMsg = "[" + szOperationName + "] 보수장일때 점유베드 간섭여부 체크 시작";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			szOCPY_BED_ERR = "Y";

			recPara.setField("YD_STK_COL_GP", 	szYD_DN_STK_COL_GP);			//권하지시위치
			recPara.setField("YD_STK_BED_NO",   szYD_DN_STK_BED_NO);			//권하지시위치 베드번호
			recPara.setField("YD_STK_LYR_NO",	szYD_DN_WO_LAYER);				//권하지시위치 적치단
			recPara.setField("MTL_MAX_W", 		szYD_EQP_WRK_MAX_W);			//크레인작업재료 중 최대 폭
			recPara.setField("MTL_MAX_L", 		szYD_EQP_WRK_MAX_L);			//크레인작업재료 중 최대 길이

			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal = ydStkLyrDao.getOcpyBedErr(recPara, rsResult);
			if (intRtnVal > 0) {
				rsResult.first();
				recTemp = rsResult.getRecord();
				szOCPY_BED_ERR = ydDaoUtils.paraRecChkNull(recTemp, "OCPY_BED_ERR");
			}

			if ("Y".equals(szOCPY_BED_ERR)) {
				szLogMsg = "[" + szOperationName + "] 보수장일때 점유베드 간섭 때문에 .. 권하위치검색 실패";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				return JPlateYdConst.RETN_NOT_EXIST_BED;
			}
		}
		*/

		//----------------------------------------------------------------------------------------------------------------------
		// 권하지시위치 수정
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "[" + szOperationName + "] 권하지시위치[" + szYD_DN_WO_LOC + "], 권하지시단[" + szYD_DN_WO_LAYER + "]을 크레인스케줄에 수정 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		JDTORecord recUpCrnSch	= null;

		recUpCrnSch = JDTORecordFactory.getInstance().create();
		recUpCrnSch.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);									// 크레인스케줄ID
		recUpCrnSch.setField("YD_EQP_ID", 				szYD_EQP_ID);										// 크레인설비ID
		recUpCrnSch.setField("YD_UP_STK_COL_GP", 		szYD_UP_STK_COL_GP);								// 권상지시위치 - 적치열
		recUpCrnSch.setField("YD_UP_STK_BED_NO", 		szYD_UP_STK_BED_NO);								// 권상지시위치 - 적치베드
		recUpCrnSch.setField("YD_DN_STK_COL_GP", 		szYD_DN_STK_COL_GP);								// 권하지시위치 - 적치열
		recUpCrnSch.setField("YD_DN_STK_BED_NO", 		szYD_DN_STK_BED_NO);								// 권하지시위치 - 적치베드

		//----------------------------------------------------------------------------------------------------------------------
		//	권상지시위치 업데이트
		//----------------------------------------------------------------------------------------------------------------------
		if (bUP_UPDT_NEEDED) {
			szLogMsg = "[" + szOperationName + "] 권상지시위치[" + szYD_UP_WO_LOC + "], 권상지시단[" + szYD_UP_WO_LAYER + "] 크레인스케줄에 등록";
    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			recUpCrnSch.setField("YD_UP_WO_LOC", 		szYD_UP_WO_LOC);									// 권상지시위치
			recUpCrnSch.setField("YD_UP_WO_LAYER", 		szYD_UP_WO_LAYER);									// 권상지시단
		}

		recUpCrnSch.setField("YD_DN_WO_LOC", 			szYD_DN_WO_LOC);									// 권하지시위치
		recUpCrnSch.setField("YD_DN_WO_LAYER", 			szYD_DN_WO_LAYER);									// 권하지시단
		recUpCrnSch.setField("YD_EQP_WRK_SH", 			String.valueOf(intYD_EQP_WRK_SH));					// 크레인작업재료 총매수
		recUpCrnSch.setField("YD_EQP_WRK_WT", 			String.valueOf(intYD_EQP_WRK_WT));					// 크레인작업재료 총중량
		recUpCrnSch.setField("YD_EQP_WRK_T", 			String.valueOf(dblYD_EQP_WRK_T));					// 크레인작업재료 총높이
		recUpCrnSch.setField("YD_EQP_WRK_L", 			String.valueOf(intYD_EQP_WRK_L));					// 크레인작업재료 총길이
		recUpCrnSch.setField("YD_EQP_WRK_MAX_W", 		szYD_EQP_WRK_MAX_W);								// 크레인작업재료 중 최대 폭
		recUpCrnSch.setField("YD_EQP_WRK_MAX_L", 		szYD_EQP_WRK_MAX_L);								// 크레인작업재료 중 최대 길이
		recUpCrnSch.setField("MODIFIER", 				szMODIFIER);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 recSchPara에 logId 추가 
		recUpCrnSch.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

		szRtnMsg = JPlateYdCrnSchUtil.uptCrnSchXYCordYdP(recUpCrnSch);
		
    	szLogMsg = "[" + szOperationName + "] 권하지시위치[" + szYD_DN_WO_LOC + "], 권하지시단[" + szYD_DN_WO_LAYER + "]을 크레인스케줄에 수정 완료";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		//	권하지시위치에 재료를 권하대기로 등록
		//----------------------------------------------------------------------------------------------------------------------
    	szLogMsg = "[" + szOperationName + "] 권하지시위치[" + szYD_DN_WO_LOC + "], 권하지시단[" + szYD_DN_WO_LAYER + "]에 크레인작업재료 등록 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

//---------------------------------------------------------------------------------------------
// 2024.11.19 Argument에 logId 추가 uptDnWaitOnYdStkLyr 신규 작성
//		szRtnMsg = uptDnWaitOnYdStkLyr(rsCrnwrkmtl, szYD_DN_WO_LOC, szYD_DN_WO_LAYER, szMODIFIER);
		szRtnMsg = uptDnWaitOnYdStkLyr(rsCrnwrkmtl, szYD_DN_WO_LOC, szYD_DN_WO_LAYER, szMODIFIER, logId);
//---------------------------------------------------------------------------------------------

		szLogMsg = "[" + szOperationName + "] 권하지시위치[" + szYD_DN_WO_LOC + "], 권하지시단[" + szYD_DN_WO_LAYER + "]에 크레인작업재료 등록 완료 - 메세지 : " + szRtnMsg;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}	
	
	/**
	 * 보조작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public static String procAidWrkToLocForPlateYdYdP(JDTORecord msgRecord					/* 전문 */
												  ,JDTORecordSet rsCrnwrkmtl			/* 크레인작업재료 */
												  ,JDTORecord recCrnSch					/* 크레인스케줄정보 */
												  ,JDTORecord recWbook					/* 작업예약정보 */
												  ) throws JDTOException {

		String 	szMethodName			= "procAidWrkToLocForPlateYdYdP";
		String 	szOperationName			= "보조작업TO위치결정";
		String 	szLogMsg				= null;
		String 	szRtnMsg 				= JPlateYdConst.RETN_CD_SUCCESS;

		ArrayList		 listToLoc		= null;
		JPlateYdStkLocVO ydStkLocVO		= null;
		JPlateYdStockDAO ydStockDao 	= new JPlateYdStockDAO();

		JDTORecordSet	rsResult		= null;
		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;

		String 	szYD_UP_WO_LOC			= null;
		String 	szYD_UP_WO_LAYER		= null;
		String 	szYD_DN_WO_LOC			= null;
		String 	szYD_DN_WO_LAYER		= null;
		String 	szYD_UP_STK_COL_GP		= null;
		String 	szYD_UP_STK_BED_NO		= null;
		String 	szYD_DN_STK_COL_GP		= null;
		String 	szYD_DN_STK_BED_NO		= null;
		String 	szYD_SCH_CD				= null;

		boolean bUP_UPDT_NEEDED			= false;

		String	szSTL_NO				= null;
		String[] szYD_STK_LYR_MTL_STAT	= {"D", "U"};

		String	szYD_TO_LOC_GUIDE		= null;

		String 	szYD_CRN_SCH_ID			= null;
		String 	szYD_EQP_ID				= null;

		String 	szYD_MTL_W_GP			= null;						//야드재료폭구분
		String 	szYD_MTL_L_GP			= null;						//야드재료길이구분

		int 	intYD_EQP_WRK_SH		= 0;						//야드설비작업매수
		int 	intYD_EQP_WRK_WT		= 0;						//야드설비작업중량
		double 	dblYD_EQP_WRK_T			= 0;						//야드설비작업총두께
		int 	intYD_EQP_WRK_L			= 0;						//야드설비작업길이
		String 	szYD_EQP_WRK_MAX_W		= null;						//작업재료 중 최대 폭
		String 	szYD_EQP_WRK_MAX_L		= null;						//작업재료 중 최대 길이
		int    	intRtnVal 				= 0;
		boolean bIS_BED_STACKABLE 		= false;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                    	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		String	szMODIFIER				= ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");
		if ("".equals(szMODIFIER)) {
			szMODIFIER = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");
		}

		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료의 최하단 재료정보를 가져온다.
		//----------------------------------------------------------------------------------------------------------------------

		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();

		szLogMsg = "["+ szOperationName +"] -------------------- 크레인작업재료의 최하단 재료정보 확인 --------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		ydUtils.displayRecord(szOperationName, recPara);

		szYD_CRN_SCH_ID     = ydDaoUtils.paraRecChkNull(recCrnSch,"YD_CRN_SCH_ID");					//크레인스케줄ID
		szYD_EQP_ID      	= ydDaoUtils.paraRecChkNull(recCrnSch,"YD_EQP_ID");						//크레인설비ID

		intYD_EQP_WRK_SH    = ydDaoUtils.paraRecChkNullInt(recPara,"SH_CNT");						//크레인작업재료 총매수
		intYD_EQP_WRK_WT   	= ydDaoUtils.paraRecChkNullInt(recPara,"SUM_MTL_WT");					//크레인작업재료 총중량
		dblYD_EQP_WRK_T    	= ydDaoUtils.paraRecChkNullDouble(recPara,"SUM_MTL_T");					//크레인작업재료 총높이
		intYD_EQP_WRK_L   	= ydDaoUtils.paraRecChkNullInt(recPara,"MAX_MTL_L");	// SUM_MTL_L	//크레인작업재료 총길이

		szYD_EQP_WRK_MAX_W	= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_W");						//크레인작업재료 중 최대 폭
		szYD_EQP_WRK_MAX_L	= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_L");						//크레인작업재료 중 최대 길이

		szLogMsg  = "[" + szOperationName + "] 크레인작업재료의 총매수 :" + intYD_EQP_WRK_SH;
		szLogMsg += ", 총중량 :" + intYD_EQP_WRK_WT;
		szLogMsg += ", 총높이 :" + dblYD_EQP_WRK_T;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szLogMsg = "[" + szOperationName + "] -------------------- 작업예약정보 확인 --------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szYD_TO_LOC_GUIDE	= ydDaoUtils.paraRecChkNull(recWbook, "YD_TO_LOC_GUIDE");				//사용자지정위치
		szYD_SCH_CD			= ydDaoUtils.paraRecChkNull(recWbook, "YD_SCH_CD");						//크레인스케줄코드

		//----------------------------------------------------------------------------------------------------------------------
		//	최하단 재료를 저장품으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szSTL_NO	= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

		szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보[" + szSTL_NO + "]를 저장품에서 조회 시작 ";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		rsResult = JDTORecordFactory.getInstance().createRecordSet("");

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 recPara에 logId 추가 
		recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

		intRtnVal = ydStockDao.getYdStock(recPara, rsResult);				// intGp == 0
		if (intRtnVal == 0) {
			szRtnMsg = "[" + szOperationName + "] 존재하지 않습니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szRtnMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		} else if (intRtnVal == -2) {
			szRtnMsg = "["  +szOperationName + "] 파라미터가 존재하지 않습니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szRtnMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		} else if (intRtnVal < 0) {
			szRtnMsg = "[" + szOperationName + "] 오류발생";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szRtnMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		}

		rsResult.first();
		recTemp = rsResult.getRecord();

		szYD_MTL_L_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L_GP");			//크레인작업 최하단재료의길이구분
		szYD_MTL_W_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_W_GP");			//크레인작업 최하단재료의폭구분

		szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보[" + szSTL_NO + "]를 저장품에서 조회 완료 - 길이구분[" + szYD_MTL_L_GP + "], 폭구분[" + szYD_MTL_W_GP + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		//	권상중이거나 권하중인 재료를 적치단으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szYD_UP_WO_LOC 		= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");
		szYD_UP_WO_LAYER 	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LAYER");

		if ("".equals(szYD_UP_WO_LOC)) {

			szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보[" + szSTL_NO + "]에 대한 권하 또는 권상위치 조회 시작 ";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			szRtnMsg = JPlateYdCommonUtils.getYdStkLyrWithMtlStat(recPara, rsResult, szYD_STK_LYR_MTL_STAT);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				return szRtnMsg;
			}

			rsResult.first();
			recTemp = rsResult.getRecord();

			szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보[" + szSTL_NO + "]에 대한 권하 또는 권상위치 조회 완료  ";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			szYD_UP_STK_COL_GP	= recTemp.getFieldString("YD_STK_COL_GP");
			szYD_UP_STK_BED_NO 	= recTemp.getFieldString("YD_STK_BED_NO");
			szYD_UP_WO_LOC 		= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYD_UP_WO_LAYER 	= recTemp.getFieldString("YD_STK_LYR_NO");

			bUP_UPDT_NEEDED		= true;

			szLogMsg = "[" + szOperationName + "] 조회된 권상지시위치[" + szYD_UP_WO_LOC + "], 권상지시단[" + szYD_UP_WO_LAYER + "]";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		} else {
			szYD_UP_STK_COL_GP = szYD_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYD_UP_WO_LOC.substring(6);

			szLogMsg = "[" + szOperationName + "] 크레인스케줄에 등록된 권상지시위치[" + szYD_UP_WO_LOC + "], 권상지시단[" + szYD_UP_WO_LAYER + "]";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		}
		//----------------------------------------------------------------------------------------------------------------------

		listToLoc = new ArrayList();

		//권상지시위치에 따라 알맞은 적치가능한 베드 검색 방법을 적용
    	recTemp = JDTORecordFactory.getInstance().create();
    	recTemp.setField("YD_STK_COL_GP", 		szYD_UP_STK_COL_GP);					// 권상지시위치 - 적치열
    	recTemp.setField("YD_STK_BED_NO", 		szYD_UP_STK_BED_NO);					// 권상지시위치 - 적치베드
    	recTemp.setField("YD_EQP_WRK_SH", 		String.valueOf(intYD_EQP_WRK_SH));		// 크레인작업재료 총매수
    	recTemp.setField("YD_EQP_WRK_WT", 		String.valueOf(intYD_EQP_WRK_WT));		// 크레인작업재료 총중량
    	recTemp.setField("YD_EQP_WRK_T", 		String.valueOf(dblYD_EQP_WRK_T));		// 크레인작업재료 총높이
    	recTemp.setField("YD_EQP_WRK_L", 		String.valueOf(intYD_EQP_WRK_L));		// 크레인작업재료 총길이
    	recTemp.setField("YD_SCH_CD", 			szYD_SCH_CD);							// 크레인 스케줄코드
    	recTemp.setField("YD_MTL_L_GP", 		szYD_MTL_L_GP);							// 크레인작업 최하단재료의 길이구분
    	recTemp.setField("YD_MTL_W_GP", 		szYD_MTL_W_GP);							// 크레인작업 최하단재료의 폭구분
    	recTemp.setField("YD_EQP_ID", 			szYD_EQP_ID);							// 크레인설비ID
    	recTemp.setField("YD_TO_LOC_GUIDE", 	szYD_TO_LOC_GUIDE);						// 사용자지정위치
    	recTemp.setField("STL_NO",				szSTL_NO);								// 재료번호

    	//----------------------------------------------------------------------------------------------------------------------
		//	적치가능한 베드 조회
		//----------------------------------------------------------------------------------------------------------------------
		//	길이구분/폭구분이 동일한 공베드를 조회
		//----------------------------------------------------------------------------------------------------------------------
		ydStkLocVO	= null;

		listToLoc 	= new ArrayList();

		szLogMsg = "[" + szOperationName + "] 길이구분[" + szYD_MTL_L_GP + "]/폭구분[" + szYD_MTL_W_GP + "]이 동일한 적치가능한 공베드 조회 시작(보조) :: " + recTemp.toString();
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szRtnMsg = getEmptyBedWithSameLWGpForAidWrk(recTemp, listToLoc);

		szLogMsg = "[" + szOperationName + "] 길이구분[" + szYD_MTL_L_GP + "]/폭구분[" + szYD_MTL_W_GP + "]이 동일한 적치가능한 공베드 조회 완료(보조) - 메세지 :: " + szRtnMsg;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		// Sorting
		//----------------------------------------------------------------------------------------------------------------------
		if (listToLoc.size() > 0) {
			Collections.sort(listToLoc, new JPlateYdStkLocComparator());
		}
		//----------------------------------------------------------------------------------------------------------------------

		//----------------------------------------------------------------------------------------------------------------------
		//	Sorting된 베드정보를 루핑을 돌면서 우선순위가 빠른 베드정보를 추출
		//----------------------------------------------------------------------------------------------------------------------
		for(int ii=0; ii<listToLoc.size(); ii++) {
			ydStkLocVO = (JPlateYdStkLocVO)listToLoc.get(ii);
			szLogMsg = "[" + szOperationName + "] [" + (ii+1) + "] 주작업 TO위치 결정 적치가능 판단 >>>> [" + ydStkLocVO.getYdStkColGp() + " - " + ydStkLocVO.getYdStkBedNo() + "], 오류코드::["+ydStkLocVO.getYdBedErrCd()+"]";
    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

    		//----------------------------------------------------------------------------------------------------------------------
    		//	야드To위치Guide가 존재하고 길이정합성이 맞는 경우에는 보조작업 TO위치 결정 시는 SKIP 시킴
    		//----------------------------------------------------------------------------------------------------------------------
    		if (szYD_TO_LOC_GUIDE.length() == 8) {
    			//----------------------------------------------------------------------------------------------------------------------
    			//	야드To위치결정방법이 F이고 야드To위치Guide가 존재하는 경우에는 보조작업이적 시에는 SKIP시킴
    			//----------------------------------------------------------------------------------------------------------------------
    			if (szYD_TO_LOC_GUIDE.substring(0, 6).equals(ydStkLocVO.getYdStkColGp()) &&
    				szYD_TO_LOC_GUIDE.substring(6).equals(ydStkLocVO.getYdStkBedNo())) {
    				szLogMsg = "[" + szOperationName + "] [" + (ii + 1) + "] 길이구분[" + szYD_MTL_L_GP + "]/폭구분[" + szYD_MTL_W_GP + "]이 동일한 조회된 공베드[" + ydStkLocVO.getYdStkColGp() + " - " + ydStkLocVO.getYdStkBedNo() + "]가 야드To위치Guide[" + szYD_TO_LOC_GUIDE + "]와 동일하므로 SKIP시킴";
    	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
    				continue;
    			}
    			//----------------------------------------------------------------------------------------------------------------------
    		}
    		//----------------------------------------------------------------------------------------------------------------------

    		//----------------------------------------------------------------------------------------------------------------------
    		//	베드가 적치가능한 지 판단 - 가능하면 루프 종료
    		//----------------------------------------------------------------------------------------------------------------------

			if (ydStkLocVO.getYdBedErrCd() == JPlateYdConst.YD_BED_STACKABLE) {

				bIS_BED_STACKABLE = true;

				szLogMsg = "[" + szOperationName + "] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
				szLogMsg = "[" + szOperationName + "] +++++++++++++++ [" + (ii + 1) + "] 길이구분[" + szYD_MTL_L_GP + "]/폭구분[" + szYD_MTL_W_GP + "]이 동일한 조회된 공베드[" + ydStkLocVO.getYdStkColGp() + " - " + ydStkLocVO.getYdStkBedNo() + "]에 적치가능함 +++++++++++++++";
	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
	    		szLogMsg = "[" + szOperationName + "] +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

				break;
			}
		}

		if (!bIS_BED_STACKABLE) {

			szLogMsg = "[" + szOperationName + "] 권하위치검색 실패 - 적치가능한 베드가 존재하지 않습니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			return JPlateYdConst.RETN_NOT_EXIST_BED;
		}

		/*
		 * 권하위치 최종결정정보 셋팅.
		 */
		szYD_DN_STK_COL_GP 	= ydStkLocVO.getYdStkColGp();
		szYD_DN_STK_BED_NO 	= ydStkLocVO.getYdStkBedNo();
		szYD_DN_WO_LOC		= szYD_DN_STK_COL_GP + szYD_DN_STK_BED_NO;
		szYD_DN_WO_LAYER 	= ydStkLocVO.getYdStkLyrNo();

		//----------------------------------------------------------------------------------------------------------------------
		// 권하지시위치 수정
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "[" + szOperationName + "] 권하지시위치[" + szYD_DN_WO_LOC + "], 권하지시단[" + szYD_DN_WO_LAYER + "]을 크레인스케줄에 수정 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		JDTORecord recUpCrnSch	= null;

		recUpCrnSch = JDTORecordFactory.getInstance().create();
		recUpCrnSch.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);							//크레인스케줄ID
		recUpCrnSch.setField("YD_EQP_ID", 				szYD_EQP_ID);								//크레인설비ID

		recUpCrnSch.setField("YD_UP_STK_COL_GP", 		szYD_UP_STK_COL_GP);						//권상지시위치 - 적치열
		recUpCrnSch.setField("YD_UP_STK_BED_NO", 		szYD_UP_STK_BED_NO);						//권상지시위치 - 적치베드
		recUpCrnSch.setField("YD_DN_STK_COL_GP", 		szYD_DN_STK_COL_GP);						//권하지시위치 - 적치열
		recUpCrnSch.setField("YD_DN_STK_BED_NO", 		szYD_DN_STK_BED_NO);						//권하지시위치 - 적치베드

		//----------------------------------------------------------------------------------------------------------------------
		//	권상지시위치 업데이트
		//----------------------------------------------------------------------------------------------------------------------
		if (bUP_UPDT_NEEDED) {
			szLogMsg = "[" + szOperationName + "] 권상지시위치[" + szYD_UP_WO_LOC + "], 권상지시단[" + szYD_UP_WO_LAYER + "] 크레인스케줄에 등록";
    		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			recUpCrnSch.setField("YD_UP_WO_LOC", 		szYD_UP_WO_LOC);							//권상지시위치
			recUpCrnSch.setField("YD_UP_WO_LAYER", 		szYD_UP_WO_LAYER);							//권상지시단
		}
		//----------------------------------------------------------------------------------------------------------------------

		recUpCrnSch.setField("YD_DN_WO_LOC", 			szYD_DN_WO_LOC);							//권하지시위치
		recUpCrnSch.setField("YD_DN_WO_LAYER", 			szYD_DN_WO_LAYER);							//권하지시단
		recUpCrnSch.setField("YD_EQP_WRK_SH", 			String.valueOf(intYD_EQP_WRK_SH));			//크레인작업재료 총매수
		recUpCrnSch.setField("YD_EQP_WRK_WT", 			String.valueOf(intYD_EQP_WRK_WT));			//크레인작업재료 총중량
		recUpCrnSch.setField("YD_EQP_WRK_T", 			String.valueOf(dblYD_EQP_WRK_T));			//크레인작업재료 총높이
		recUpCrnSch.setField("YD_EQP_WRK_L", 			String.valueOf(intYD_EQP_WRK_L));			//크레인작업재료 총길이
		recUpCrnSch.setField("YD_EQP_WRK_MAX_W", 		szYD_EQP_WRK_MAX_W);						//크레인작업재료 중 최대 폭
		recUpCrnSch.setField("YD_EQP_WRK_MAX_L", 		szYD_EQP_WRK_MAX_L);						//크레인작업재료 중 최대 길이
		recUpCrnSch.setField("MODIFIER", 				szMODIFIER);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 recUpCrnSch에 logId 추가 
		recUpCrnSch.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

		szRtnMsg = JPlateYdCrnSchUtil.uptCrnSchXYCordYdP(recUpCrnSch);

    	szLogMsg = "[" + szOperationName + "] 권하지시위치[" + szYD_DN_WO_LOC + "], 권하지시단[" + szYD_DN_WO_LAYER + "]을 크레인스케줄에 수정 완료";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		//----------------------------------------------------------------------------------------------------------------------

		//----------------------------------------------------------------------------------------------------------------------
		//	권하지시위치에 재료를 권하대기로 등록
		//----------------------------------------------------------------------------------------------------------------------
    	szLogMsg = "[" + szOperationName + "] 권하지시위치[" + szYD_DN_WO_LOC + "], 권하지시단[" + szYD_DN_WO_LAYER + "]에 크레인작업재료 등록 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

//---------------------------------------------------------------------------------------------
// 2024.11.19 Argument에 logId 추가 uptDnWaitOnYdStkLyr 신규 작성
//		szRtnMsg = uptDnWaitOnYdStkLyr(rsCrnwrkmtl, szYD_DN_WO_LOC, szYD_DN_WO_LAYER, szMODIFIER);
		szRtnMsg = uptDnWaitOnYdStkLyr(rsCrnwrkmtl, szYD_DN_WO_LOC, szYD_DN_WO_LAYER, szMODIFIER, logId);
//---------------------------------------------------------------------------------------------

		szLogMsg = "[" + szOperationName + "] 권하지시위치[" + szYD_DN_WO_LOC + "], 권하지시단[" + szYD_DN_WO_LAYER + "]에 크레인작업재료 등록 완료 - 메세지 : " + szRtnMsg;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}
	/**
	 * 1후판 동일길이/폭구분 공베드검색
	 * @param recInPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String getEmptyBedWithSameLWGpYdP(JDTORecord recInPara, List listToLoc) throws JDTOException {
		/*
		 * 최상단 재료의  폭구분, 길이구분과 동일한 베드 조회
		 */
		String 	szMethodName			= "getEmptyBedWithSameLWGpYdP";
		String 	szOperationName			= "1후판  동일길이/폭구분공베드검색";
		String 	szLogMsg				= null;
		String 	szRtnMsg 				= JPlateYdConst.RETN_CD_SUCCESS;

		String 	szYD_TO_LOC_GUIDE		= null;
		String 	szYD_MTL_L_GP			= null;
		String 	szYD_MTL_W_GP			= null;
		String 	szYD_STK_COL_GP			= null;
		String 	szYD_STK_BED_NO			= null;
		String 	szYD_GP					= null;
		String 	szYD_BAY_GP				= null;
		String 	szYD_EQP_WRK_SH			= null;
		String 	szYD_EQP_WRK_WT			= null;
		String 	szYD_EQP_WRK_T			= null;
		String 	szYD_EQP_WRK_L			= null;
		String 	szYD_SCH_CD				= null;
		String	szSTL_NO				= null;
		String	szYD_TCAR_GP			= null;

		JDTORecord		recPara			= null;
		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인::" + recInPara.toString();
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		ydUtils.displayRecord(szOperationName, recInPara);

		szYD_TO_LOC_GUIDE 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE");		//사용자지정위치
		szYD_MTL_L_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");			//크레인작업 최하단 재료의 길이구분
		szYD_MTL_W_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");			//크레인작업 최하단 재료의 폭구분
		szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");		//권상지시위치 - 적치열
		szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");		//권상지시위치 - 적치베드
		szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");		//크레인작업재료 총매수
		szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");		//크레인작업재료 총중량
		szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");			//크레인작업재료 총높이
		szYD_EQP_WRK_L 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_L");			//크레인작업재료 총길이
		szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");			//크레인스케줄코드
		szSTL_NO			= ydDaoUtils.paraRecChkNull(recInPara, "STL_NO");				//재료번호
		szYD_TCAR_GP		= ydDaoUtils.paraRecChkNull(recInPara, "YD_TCAR_GP");			//대차설비ID

		//----------------------------------------------------------------------------------------------------------------------
		if (szYD_TO_LOC_GUIDE.length() == 8) {

			szYD_GP			= ydUtils.substr(szYD_TO_LOC_GUIDE, 0, 1);
			szYD_BAY_GP		= ydUtils.substr(szYD_TO_LOC_GUIDE, 1, 1);
			//SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_PILING_CD
		} else {
			//----------------------------------------------------------------------------------------------------------------------
			//	사용자지정위치(입고예정위치)가 존재하지 않는 경우에는 업무종료
			//	==>권상지시위치를 사용. : 2009.12.22
			//----------------------------------------------------------------------------------------------------------------------

			szYD_GP			= ydUtils.substr(szYD_STK_COL_GP, 0, 1);
			szYD_BAY_GP		= ydUtils.substr(szYD_STK_COL_GP, 1, 1);

			szLogMsg = "["+ szOperationName +"] 사용자지정위치(입고예정위치)가 존재하지 않으므로 권상지시위치를 사용";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			//----------------------------------------------------------------------------------------------------------------------
		}

		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 			szYD_GP);
		recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);
		recPara.setField("YD_MTL_L_GP", 	ydUtils.substr(szYD_MTL_L_GP, 0, 1));
		recPara.setField("YD_MTL_W_GP", 	ydUtils.substr(szYD_MTL_W_GP, 0, 1));
		recPara.setField("YD_EQP_WRK_SH", 	szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 	szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 	szYD_EQP_WRK_T);
		recPara.setField("YD_EQP_WRK_L", 	szYD_EQP_WRK_L);
		recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
		recPara.setField("YD_STK_COL_GP",	szYD_STK_COL_GP);					//권상지시위치 - 적치열
		recPara.setField("YD_STK_BED_NO",	szYD_STK_BED_NO);					//권상지시위치 - 적치베드
		recPara.setField("YD_TO_LOC_GUIDE",	szYD_TO_LOC_GUIDE);
		recPara.setField("STL_NO",			szSTL_NO);
		recPara.setField("YD_TCAR_GP",		szYD_TCAR_GP);						//대차설비ID

		//----------------------------------------------------------------------------------------------------------------------
		//	동일한 길이구분/폭구분을  가진 모든 공베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 동일한 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"]을 가진 모든 공베드 정보 조회 시작 ";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		String sNEW_MODULE_EFF_YN = "N";
		
		JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
		
		sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A026"); //1후판정저야드 업무기준 YDB810 신규방식 적용여부
		
		ydUtils.putLog(szOperationName, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정저야드 업무기준 YDB810 신규방식 적용여부  : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
		
		if(sNEW_MODULE_EFF_YN.equals("Y")) {
			//신규모듈 적용
			szRtnMsg = JPlateYdToLocUtil.srchEmptyBedWithSameLWGpYdP2(recPara, listToLoc);
			
		} else {
			//기존방식 
			szRtnMsg = JPlateYdToLocUtil.srchEmptyBedWithSameLWGpYdP(recPara, listToLoc);
		}

		szLogMsg = "["+ szOperationName +"] 파라미터 설정 후 동일한 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"]을 가진 모든 공베드 정보 조회 완료 - 메세지 :: " + szRtnMsg;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		szLogMsg = "["+ szOperationName +"] 메소드 끝";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	}

	/**
	 * 1후판 동일길이/폭구분 공베드정보조회 - 주작업
	 * @param recPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String srchEmptyBedWithSameLWGpYdP(JDTORecord recInPara, List listToLoc) throws JDTOException {

		JPlateYdStkBedDAO ydStkbedDao 	= new JPlateYdStkBedDAO();
		JPlateYdStockDAO  ydStockDao	= new JPlateYdStockDAO();
		JPlateYdStkColDAO ydStkColDao	= new JPlateYdStkColDAO();

		String 	szMethodName			= "srchEmptyBedWithSameLWGpYdP";
		String 	szOperationName			= "1후판 동일길이/폭구분 공베드정보조회 (주작업)";
		String 	szLogMsg				= "";
		String 	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;

		String	szYD_FR_BAY				= "";
		String	szYD_TO_BAY				= "";
		String	szYD_TO_STK_COL_GP		= "";
		String	szYD_TO_SPAN_NO			= "";
		String	szYD_TO_STK_BED_NO		= "";

		int    	intRtnVal 				= 0;
        // 후판정정야드그룹구분
        String	szPL_SHEAR_YD_GRP_GP	= "";
        String	szGRP_GP_ARR[]			= null;
        String	szGRP_GP[] 				= {"","","","",""};

		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;
		JDTORecordSet 	rsResult		= null;

		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인 :: " + recInPara.toString();
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		String	szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");						//권상지시위치 - 적치열
		String	szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시베드
		String	szYD_MTL_L_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		String	szYD_MTL_W_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		String	szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		String	szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		String	szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");							//크레인작업재료 총높이
		String	szYD_EQP_WRK_L 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_L");							//크레인작업재료 총길이
		String	szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");							//크레인스케줄코드
		String	szYD_TO_LOC_GUIDE	= ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE");						//사용자지정위치
		String	szYD_GP				= ydDaoUtils.paraRecChkNull(recInPara, "YD_GP");
		String	szYD_BAY_GP			= ydDaoUtils.paraRecChkNull(recInPara, "YD_BAY_GP");
		String	szSTL_NO			= ydDaoUtils.paraRecChkNull(recInPara, "STL_NO");								//재료번호
		String	szYD_TCAR_GP		= ydDaoUtils.paraRecChkNull(recInPara, "YD_TCAR_GP");							//대차설비ID

		szYD_GP						= ydUtils.substr(szYD_TO_LOC_GUIDE, 0, 1);
		szYD_BAY_GP					= ydUtils.substr(szYD_TO_LOC_GUIDE, 1, 1);

		String 	szBS_END			= ydStockDao.getBsEndCheckYdP(recInPara);											//보수완료구분

		boolean	isTCarWk			= false;

		szYD_FR_BAY = ydUtils.substr(szYD_STK_COL_GP,   1, 1);
		szYD_TO_BAY = ydUtils.substr(szYD_TO_LOC_GUIDE, 1, 1);

		// 대차작업여부 체크
		if (szYD_TO_BAY.equals(szYD_FR_BAY) && "".equals(szYD_TCAR_GP) ) {
			isTCarWk = false;
		} else {
			isTCarWk = true;
		}

    	szLogMsg = ">>>> 대차작업 여부 : " + String.valueOf(isTCarWk);
    	ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		// 대차작업일 경우  TO위치 편집 : FA010101 , FXTC01 ==> FATC01
		if (isTCarWk) {
			szYD_TO_STK_COL_GP 	= ydUtils.substr(szYD_STK_COL_GP, 0, 2) + ydUtils.substr(szYD_TCAR_GP, 2, 4);
			szYD_TO_STK_BED_NO 	= "";
			szYD_BAY_GP			= ydUtils.substr(szYD_STK_COL_GP, 1, 1);	// 대차이적일때는 FROM동의 위치를 검색
		} else {
//			if ("".equals(szYD_TO_LOC_GUIDE) || szYD_TO_LOC_GUIDE.length() != 8) {
			if ("".equals(szYD_TO_LOC_GUIDE) || szYD_TO_LOC_GUIDE.length() <  6) {

				szYD_TO_STK_BED_NO = "";

				// BRE RULE 적용 - TO위치 검색기준 조회 : TO위치가 일반베드일때만 체크
				if (!"".equals(szYD_TO_LOC_GUIDE) && szYD_TO_LOC_GUIDE.length() >= 4) {
					szYD_TO_SPAN_NO = szYD_TO_LOC_GUIDE.substring(2,4);
				}

				if ("BC".equals(szYD_TO_SPAN_NO) ||"RT".equals(szYD_TO_SPAN_NO) || "BS".equals(szYD_TO_SPAN_NO) || "CN".equals(szYD_TO_SPAN_NO) || "TD".equals(szYD_TO_SPAN_NO)) {

					if (szYD_TO_LOC_GUIDE.length() >= 6) {
						szYD_TO_STK_COL_GP = szYD_TO_LOC_GUIDE.substring(0, 6);
					} else {
						szYD_TO_STK_COL_GP = szYD_TO_LOC_GUIDE;
					}

				} else {
					JDTORecord jdtoRcd = JDTORecordFactory.getInstance().create();
			    	boolean bRtnVal = GetBreRule8.getYDB810(szYD_STK_COL_GP, szYD_SCH_CD, szBS_END, jdtoRcd);

			    	szLogMsg = ">>>> GetBreRule8 조회 .... szYD_STK_COL_GP : " + szYD_STK_COL_GP + ", szYD_SCH_CD : " + szYD_SCH_CD + ", szBS_END : " + szBS_END + " >>>> Result : " + bRtnVal;
			    	ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			    	ydUtils.printParam("a", jdtoRcd);
			    	if (bRtnVal) {
			    		// TO위치가이드가 4자리이하일때 BRE기준에 등록된 위치로 Set :: 이상일때는 입력한 파라미터 사용
			    		// (전단L2에서 TO위치가 6자리 이하로 올때가 있기 때문에)
			    		if (szYD_TO_LOC_GUIDE.length() < 4) {
			    			szYD_TO_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_TO_LOC_GUIDE");		// 야드To위치Guide
			    		} else {
			    			szYD_TO_STK_COL_GP 	= ydUtils.substr(szYD_TO_LOC_GUIDE, 0, 6);
			    		}
			    		szPL_SHEAR_YD_GRP_GP 	= ydDaoUtils.paraRecChkNull(jdtoRcd, "PL_SHEAR_YD_GRP_GP");		// 후판정정야드그룹구분

			    		if (szPL_SHEAR_YD_GRP_GP != null) {
			    			szGRP_GP_ARR = szPL_SHEAR_YD_GRP_GP.split(",");
			    		}
			    		for(int ii=0; ii<szGRP_GP_ARR.length; ii++) {
			    			if (ii > szGRP_GP.length) {
			    				break;
			    			}
			    			szGRP_GP[ii] = szGRP_GP_ARR[ii];
			    		}
			    	} else {
			    		szYD_TO_STK_COL_GP = "";
			    	}

			    	szLogMsg = ">>>> szYD_TO_STK_COL_GP :" + szYD_TO_STK_COL_GP + ", szPL_SHEAR_YD_GRP_GP :" + szPL_SHEAR_YD_GRP_GP;
			    	ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				}

			} else {
				szYD_TO_STK_COL_GP = ydUtils.substr(szYD_TO_LOC_GUIDE, 0, 6);
				szYD_TO_STK_BED_NO = ydUtils.substr(szYD_TO_LOC_GUIDE, 6, 2);
				
				//베드정보가 존재 안 하는 경우 
				if("".equals(szYD_TO_STK_BED_NO)){
					szYD_TO_STK_BED_NO="01";
				}

				rsResult  = JDTORecordFactory.getInstance().createRecordSet("yd");
				recPara   = JDTORecordFactory.getInstance().create();
				recTemp   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP",	szYD_TO_STK_COL_GP);

				// 야드적치열에 후판정정야드그룹구분 조회
				intRtnVal = ydStkColDao.getYdStkcol(recPara, rsResult);
				if (intRtnVal > 0) {
					rsResult.first();
					recTemp = rsResult.getRecord();
					szGRP_GP[0] = ydDaoUtils.paraRecChkNull(recTemp, "PL_SHEAR_YD_GRP_GP");
				}
			}
		}

		szYD_MTL_W_GP = ydUtils.substr(szYD_MTL_W_GP, 0, 1);
		szYD_MTL_L_GP = ydUtils.substr(szYD_MTL_L_GP, 0, 1);

		szLogMsg = "FROM위치          >>>> 열구분 :: " + szYD_STK_COL_GP    + ", BED :: " + szYD_STK_BED_NO + ", 재료번호 :: " + szSTL_NO;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
		szLogMsg = "TO위치 검색범위 >>>> 열구분 :: " + szYD_TO_STK_COL_GP + ", BED :: " + szYD_TO_STK_BED_NO;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 				szYD_GP);
		recPara.setField("YD_BAY_GP", 			szYD_BAY_GP);
		recPara.setField("YD_STK_COL_GP",		szYD_STK_COL_GP);		// 권상위치를 제외하기 위한 조건
		recPara.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
		recPara.setField("YD_TO_STK_COL_GP",	szYD_TO_STK_COL_GP);
		recPara.setField("YD_TO_STK_BED_NO", 	szYD_TO_STK_BED_NO);
		recPara.setField("YD_STK_BED_L_GP", 	szYD_MTL_L_GP);
		recPara.setField("YD_STK_BED_W_GP", 	szYD_MTL_W_GP);
		recPara.setField("YD_EQP_WRK_SH", 		szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 		szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 		szYD_EQP_WRK_T);
		recPara.setField("YD_EQP_WRK_L", 		szYD_EQP_WRK_L);
		recPara.setField("YD_SCH_CD", 			szYD_SCH_CD);
		recPara.setField("STL_NO", 				szSTL_NO);
		recPara.setField("GRP_GP1", 			szGRP_GP[0]);
		recPara.setField("GRP_GP2", 			szGRP_GP[1]);
		recPara.setField("GRP_GP3", 			szGRP_GP[2]);
		recPara.setField("GRP_GP4", 			szGRP_GP[3]);
		recPara.setField("GRP_GP5", 			szGRP_GP[4]);

		szYD_TO_SPAN_NO = ydUtils.substr(szYD_TO_STK_COL_GP, 2, 2);

		if (!"BC".equals(szYD_TO_SPAN_NO) && !"CN".equals(szYD_TO_SPAN_NO) && !"RT".equals(szYD_TO_SPAN_NO) && !"BS".equals(szYD_TO_SPAN_NO)) {
			recPara.setField("YD_TO_STK_BED_NO", 	"01");			// TO위치 결정시 무조건 '01' 베드 선택하도록 보완
		}

		//----------------------------------------------------------------------------------------------------------------------
		//	조건에 해당하는 베드 정보 조회 - 동일길이/폭구분을 가진  모든 공베드 정보 조회
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 시작 ";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");

		if ("CN".equals(szYD_TO_SPAN_NO)) {

			// 가스장 적치가능한 공베드 조회
			intRtnVal = ydStkbedDao.getSameLWGpBookInCnc(recPara, rsResult);

		} else if ("RT".equals(szYD_TO_SPAN_NO) || "BS".equals(szYD_TO_SPAN_NO) || "BC".equals(szYD_TO_SPAN_NO) ||
				   "TD".equals(szYD_TO_SPAN_NO) || "TC".equals(szYD_TO_SPAN_NO)) {

			// 길이구분/폭구분이 동일한 적치가능한 공베드 조회 [설비]
//			intRtnVal = ydStkbedDao.getSameLWGpBookIn(recPara, rsResult);
			intRtnVal = ydStkbedDao.getSameLWGpBookInYdP(recPara, rsResult);
					
		} else {

			// 길이구분/폭구분이 동일한 적치가능한 공베드 조회
			intRtnVal = ydStkbedDao.getSameLWGpYdP(recPara, rsResult);		// intGp == 25

			// RT BOOK-OUT시 TO위치를 못찾으면 해당동 SPAN으로 다시 검색 ????
			if (intRtnVal < 1) {
				if ("RT".equals(ydUtils.substr(szYD_STK_COL_GP, 2, 2))) {
					szYD_TO_STK_COL_GP = ydUtils.substr(szYD_TO_STK_COL_GP, 0, 4);
					szYD_TO_STK_BED_NO = "";
					szLogMsg = "RT BOOK-OUT시 TO위치를 SPAN으로 다시 검색 >>>> 열구분 :: " + szYD_TO_STK_COL_GP + ", BED :: " + szYD_TO_STK_BED_NO;
					ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

					recPara.setField("YD_TO_STK_COL_GP",	szYD_TO_STK_COL_GP);
					recPara.setField("YD_TO_STK_BED_NO", 	szYD_TO_STK_BED_NO);

					intRtnVal = ydStkbedDao.getSameLWGpYdP(recPara, rsResult);		// intGp == 25
				}
			}
		}

		szLogMsg = "["+ szOperationName +"] 동일길이/폭구분을 가진  모든 공베드 정보 조회 완료(주작업) - 메세지 : " + Integer.toString(intRtnVal);
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		if (intRtnVal > 0) {
		//	JPlateYdToLocUtil.srchNconvRecord2Vo(szYD_STK_COL_GP, szYD_STK_BED_NO, recPara, rsResult, listToLoc, JPlateYdConst.TO_LOC_PRIOR_PLATE_EMPTY_BED);
			JPlateYdToLocUtil.srchNconvRecord2Vo("", "", recPara, rsResult, listToLoc, JPlateYdConst.TO_LOC_PRIOR_PLATE_EMPTY_BED);
		} else {
			szRtnMsg = JPlateYdConst.RETN_CD_FAILURE;
		}

		szLogMsg = "["+ szOperationName +"] 메소드 끝 >>>> " + szRtnMsg;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	}

	/**
	 * 1후판 동일길이/폭구분 공베드정보조회 - 주작업2 - 신규방식
	 * @param recPara
	 * @param listToLoc
	 * @return
	 * @throws JDTOException
	 */
	public static String srchEmptyBedWithSameLWGpYdP2(JDTORecord recInPara, List listToLoc) throws JDTOException {

		JPlateYdStkBedDAO ydStkbedDao 	= new JPlateYdStkBedDAO();
		JPlateYdStockDAO  ydStockDao	= new JPlateYdStockDAO();
		JPlateYdStkColDAO ydStkColDao	= new JPlateYdStkColDAO();

		String 	szMethodName			= "srchEmptyBedWithSameLWGpYdP2";
		String 	szOperationName			= "1후판 동일길이/폭구분 공베드정보조회 (주작업)2";
		String 	szLogMsg				= "";
		String 	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;

		String	szYD_FR_BAY				= "";
		String	szYD_TO_BAY				= "";
		String	szYD_TO_STK_COL_GP		= "";
		String	szYD_TO_SPAN_NO			= "";
		String	szYD_TO_STK_BED_NO		= "";

		int    	intRtnVal 				= 0;
        // 후판정정야드그룹구분
        String	szPL_SHEAR_YD_GRP_GP	= "";
        String	szGRP_GP_ARR[]			= null;
        String	szGRP_GP[] 				= {"","","","",""};
        String  szTO_LOC_ARR[]			= null;

		JDTORecord		recPara			= null;
		JDTORecord		recTemp			= null;
		JDTORecordSet 	rsResult		= null;

		//----------------------------------------------------------------------------------------------------------------------
		//	파라미터 확인
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg = "["+ szOperationName +"] 메소드 시작 - 파라미터 확인 :: " + recInPara.toString();
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		String	szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");						//권상지시위치 - 적치열
		String	szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");						//권상지시베드
		String	szYD_MTL_L_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_L_GP");							//크레인작업 최하단 재료의 길이구분
		String	szYD_MTL_W_GP 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_W_GP");							//크레인작업 최하단 재료의 폭구분
		String	szYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_SH");						//크레인작업재료 총매수
		String	szYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_WT");						//크레인작업재료 총중량
		String	szYD_EQP_WRK_T 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_T");							//크레인작업재료 총높이
		String	szYD_EQP_WRK_L 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_WRK_L");							//크레인작업재료 총길이
		String	szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD");							//크레인스케줄코드
		String	szYD_TO_LOC_GUIDE	= ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE");						//사용자지정위치
		String	szYD_GP				= ydDaoUtils.paraRecChkNull(recInPara, "YD_GP");
		String	szYD_BAY_GP			= ydDaoUtils.paraRecChkNull(recInPara, "YD_BAY_GP");
		String	szSTL_NO			= ydDaoUtils.paraRecChkNull(recInPara, "STL_NO");								//재료번호
		String	szYD_TCAR_GP		= ydDaoUtils.paraRecChkNull(recInPara, "YD_TCAR_GP");							//대차설비ID

		szYD_GP						= ydUtils.substr(szYD_TO_LOC_GUIDE, 0, 1);
		szYD_BAY_GP					= ydUtils.substr(szYD_TO_LOC_GUIDE, 1, 1);

		String 	szBS_END			= ydStockDao.getBsEndCheckYdP(recInPara);											//보수완료구분

		boolean	isTCarWk			= false;

		szYD_FR_BAY = ydUtils.substr(szYD_STK_COL_GP,   1, 1);
		szYD_TO_BAY = ydUtils.substr(szYD_TO_LOC_GUIDE, 1, 1);

		// 대차작업여부 체크
		if (szYD_TO_BAY.equals(szYD_FR_BAY) && "".equals(szYD_TCAR_GP) ) {
			isTCarWk = false;
		} else {
			isTCarWk = true;
		}

    	szLogMsg = ">>>> 대차작업 여부 : " + String.valueOf(isTCarWk);
    	ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		// 대차작업일 경우  TO위치 편집 : FA010101 , FXTC01 ==> FATC01
		if (isTCarWk) {
			szYD_TO_STK_COL_GP 	= ydUtils.substr(szYD_STK_COL_GP, 0, 2) + ydUtils.substr(szYD_TCAR_GP, 2, 4);
			szYD_TO_STK_BED_NO 	= "";
			szYD_BAY_GP			= ydUtils.substr(szYD_STK_COL_GP, 1, 1);	// 대차이적일때는 FROM동의 위치를 검색
		} else {
//			if ("".equals(szYD_TO_LOC_GUIDE) || szYD_TO_LOC_GUIDE.length() != 8) {
			if ("".equals(szYD_TO_LOC_GUIDE) || szYD_TO_LOC_GUIDE.length() <  6) {

				szYD_TO_STK_BED_NO = "";

				// BRE RULE 적용 - TO위치 검색기준 조회 : TO위치가 일반베드일때만 체크
				if (!"".equals(szYD_TO_LOC_GUIDE) && szYD_TO_LOC_GUIDE.length() >= 4) {
					szYD_TO_SPAN_NO = szYD_TO_LOC_GUIDE.substring(2,4);
				}

				if ("BC".equals(szYD_TO_SPAN_NO) ||"RT".equals(szYD_TO_SPAN_NO) || "BS".equals(szYD_TO_SPAN_NO) || "CN".equals(szYD_TO_SPAN_NO) || "TD".equals(szYD_TO_SPAN_NO)) {

					if (szYD_TO_LOC_GUIDE.length() >= 6) {
						szYD_TO_STK_COL_GP = szYD_TO_LOC_GUIDE.substring(0, 6);
					} else {
						szYD_TO_STK_COL_GP = szYD_TO_LOC_GUIDE;
					}

				} else {
					JDTORecord jdtoRcd = JDTORecordFactory.getInstance().create();
			    	boolean bRtnVal = GetBreRule8.getYDB810(szYD_STK_COL_GP, szYD_SCH_CD, szBS_END, jdtoRcd);

			    	szLogMsg = ">>>> GetBreRule8 조회 .... szYD_STK_COL_GP : " + szYD_STK_COL_GP + ", szYD_SCH_CD : " + szYD_SCH_CD + ", szBS_END : " + szBS_END + " >>>> Result : " + bRtnVal;
			    	ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			    	ydUtils.printParam("a", jdtoRcd);
			    	if (bRtnVal) {
			    		// TO위치가이드가 4자리이하일때 BRE기준에 등록된 위치로 Set :: 이상일때는 입력한 파라미터 사용
			    		// (전단L2에서 TO위치가 6자리 이하로 올때가 있기 때문에)
			    		if (szYD_TO_LOC_GUIDE.length() < 4) {
			    			szYD_TO_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_TO_LOC_GUIDE");		// 야드To위치Guide
			    		} else {
			    			szYD_TO_STK_COL_GP 	= ydUtils.substr(szYD_TO_LOC_GUIDE, 0, 6);
			    		}
			    		szPL_SHEAR_YD_GRP_GP 	= ydDaoUtils.paraRecChkNull(jdtoRcd, "PL_SHEAR_YD_GRP_GP");		// 후판정정야드그룹구분

			    		if (szPL_SHEAR_YD_GRP_GP != null) {
			    			szGRP_GP_ARR = szPL_SHEAR_YD_GRP_GP.split(",");
			    		}
			    		for(int ii=0; ii<szGRP_GP_ARR.length; ii++) {
			    			if (ii > szGRP_GP.length) {
			    				break;
			    			}
			    			szGRP_GP[ii] = szGRP_GP_ARR[ii];
			    		}
			    	} else {
			    		szYD_TO_STK_COL_GP = "";
			    	}

			    	szLogMsg = ">>>> szYD_TO_STK_COL_GP :" + szYD_TO_STK_COL_GP + ", szPL_SHEAR_YD_GRP_GP :" + szPL_SHEAR_YD_GRP_GP;
			    	ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				}

			} else {
				szYD_TO_STK_COL_GP = ydUtils.substr(szYD_TO_LOC_GUIDE, 0, 6);
				szYD_TO_STK_BED_NO = ydUtils.substr(szYD_TO_LOC_GUIDE, 6, 2);
				
				//베드정보가 존재 안 하는 경우 
				if("".equals(szYD_TO_STK_BED_NO)){
					szYD_TO_STK_BED_NO="01";
				}

				rsResult  = JDTORecordFactory.getInstance().createRecordSet("yd");
				recPara   = JDTORecordFactory.getInstance().create();
				recTemp   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP",	szYD_TO_STK_COL_GP);

				// 야드적치열에 후판정정야드그룹구분 조회
				intRtnVal = ydStkColDao.getYdStkcol(recPara, rsResult);
				if (intRtnVal > 0) {
					rsResult.first();
					recTemp = rsResult.getRecord();
					szGRP_GP[0] = ydDaoUtils.paraRecChkNull(recTemp, "PL_SHEAR_YD_GRP_GP");
				}
			}
		}

		szYD_MTL_W_GP = ydUtils.substr(szYD_MTL_W_GP, 0, 1);
		szYD_MTL_L_GP = ydUtils.substr(szYD_MTL_L_GP, 0, 1);

		szLogMsg = "FROM위치          >>>> 열구분 :: " + szYD_STK_COL_GP    + ", BED :: " + szYD_STK_BED_NO + ", 재료번호 :: " + szSTL_NO;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
		szLogMsg = "TO위치 검색범위 >>>> 열구분 :: " + szYD_TO_STK_COL_GP + ", BED :: " + szYD_TO_STK_BED_NO;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_GP", 				szYD_GP);
		recPara.setField("YD_BAY_GP", 			szYD_BAY_GP);
		recPara.setField("YD_STK_COL_GP",		szYD_STK_COL_GP);		// 권상위치를 제외하기 위한 조건
		recPara.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
		//recPara.setField("YD_TO_STK_COL_GP",	szYD_TO_STK_COL_GP);
		recPara.setField("YD_TO_STK_BED_NO", 	szYD_TO_STK_BED_NO);
		recPara.setField("YD_STK_BED_L_GP", 	szYD_MTL_L_GP);
		recPara.setField("YD_STK_BED_W_GP", 	szYD_MTL_W_GP);
		recPara.setField("YD_EQP_WRK_SH", 		szYD_EQP_WRK_SH);
		recPara.setField("YD_EQP_WRK_WT", 		szYD_EQP_WRK_WT);
		recPara.setField("YD_EQP_WRK_T", 		szYD_EQP_WRK_T);
		recPara.setField("YD_EQP_WRK_L", 		szYD_EQP_WRK_L);
		recPara.setField("YD_SCH_CD", 			szYD_SCH_CD);
		recPara.setField("STL_NO", 				szSTL_NO);
		recPara.setField("GRP_GP1", 			szGRP_GP[0]);
		recPara.setField("GRP_GP2", 			szGRP_GP[1]);
		recPara.setField("GRP_GP3", 			szGRP_GP[2]);
		recPara.setField("GRP_GP4", 			szGRP_GP[3]);
		recPara.setField("GRP_GP5", 			szGRP_GP[4]);

		
		
		int iTO_LOC_ARR_LEN = 0;
		
		if (szYD_TO_STK_COL_GP != null ) {
			if(!"".equals(szYD_TO_STK_COL_GP)) {
				szTO_LOC_ARR = szYD_TO_STK_COL_GP.split(",");
				iTO_LOC_ARR_LEN = szTO_LOC_ARR.length;
			}
		}
		
		intRtnVal = 0;
		
		szLogMsg = "szTO_LOC_ARR >>>> 길이 :: " + iTO_LOC_ARR_LEN;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
		
		for(int ii=0; ii<iTO_LOC_ARR_LEN; ii++) {
			
			
			szYD_TO_STK_COL_GP = szTO_LOC_ARR[ii];
			
			
			if(!"P".equals(ydUtils.substr(szYD_TO_STK_COL_GP,0,1)) && szYD_TO_STK_COL_GP.length() == 5) {
				//54020 처럼 R/T ZONE 번호을 입력했을 경우 적치열로 변환 시킨다.
				szYD_TO_STK_COL_GP = JPlateYdCommonUtils.getY2RtZoneToLoc(szYD_TO_STK_COL_GP);
				szLogMsg = "["+ szOperationName +"] RT존번호["+szTO_LOC_ARR[ii]+"] --> 적치열 ["+szYD_TO_STK_COL_GP+"]로 변환 ";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			}
			
			
			recPara.setField("YD_TO_STK_COL_GP",	szYD_TO_STK_COL_GP);
			szYD_TO_SPAN_NO = ydUtils.substr(szYD_TO_STK_COL_GP, 2, 2);

			if (!"BC".equals(szYD_TO_SPAN_NO) && !"CN".equals(szYD_TO_SPAN_NO) && !"RT".equals(szYD_TO_SPAN_NO) && !"BS".equals(szYD_TO_SPAN_NO)) {
				recPara.setField("YD_TO_STK_BED_NO", 	"01");			// TO위치 결정시 무조건 '01' 베드 선택하도록 보완
			}

			//----------------------------------------------------------------------------------------------------------------------
			//	조건에 해당하는 베드 정보 조회 - 동일길이/폭구분을 가진  모든 공베드 정보 조회
			//----------------------------------------------------------------------------------------------------------------------
			szLogMsg = "["+ szOperationName +"] 동일한 길이구분["+szYD_MTL_L_GP+"]/폭구분["+szYD_MTL_W_GP+"]을 가진  모든 공베드 정보 조회 시작 (" + ii + " 번째)";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");

			if ("CN".equals(szYD_TO_SPAN_NO)) {

				// 가스장 적치가능한 공베드 조회
				intRtnVal = ydStkbedDao.getSameLWGpBookInCnc(recPara, rsResult);

			} else if ("RT".equals(szYD_TO_SPAN_NO) || "BS".equals(szYD_TO_SPAN_NO) || "BC".equals(szYD_TO_SPAN_NO) ||
					   "TD".equals(szYD_TO_SPAN_NO) || "TC".equals(szYD_TO_SPAN_NO)) {

				// 길이구분/폭구분이 동일한 적치가능한 공베드 조회 [설비]
//				intRtnVal = ydStkbedDao.getSameLWGpBookIn(recPara, rsResult);
				intRtnVal = ydStkbedDao.getSameLWGpBookInYdP(recPara, rsResult);
						
			} else {

				// 길이구분/폭구분이 동일한 적치가능한 공베드 조회
				if(!"".equals(szGRP_GP[0])) {
					//용도구분을 지정했을 때...
					intRtnVal = ydStkbedDao.getSameLWGpYdP(recPara, rsResult);		// intGp == 25
				} else {
					//용도구분이 없을 때...
					intRtnVal = ydStkbedDao.getSameLWGpYdP2(recPara, rsResult);		
				}

				// RT BOOK-OUT시 TO위치를 못찾으면 해당동 SPAN으로 다시 검색 ????
				if (intRtnVal < 1) {
					if ("RT".equals(ydUtils.substr(szYD_STK_COL_GP, 2, 2))) {
						szYD_TO_STK_COL_GP = ydUtils.substr(szYD_TO_STK_COL_GP, 0, 4);
						szYD_TO_STK_BED_NO = "";
						szLogMsg = "RT BOOK-OUT시 TO위치를 SPAN으로 다시 검색 >>>> 열구분 :: " + szYD_TO_STK_COL_GP + ", BED :: " + szYD_TO_STK_BED_NO;
						ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

						recPara.setField("YD_TO_STK_COL_GP",	szYD_TO_STK_COL_GP);
						recPara.setField("YD_TO_STK_BED_NO", 	szYD_TO_STK_BED_NO);

						if(!"".equals(szGRP_GP[0])) {
							//용도구분을 지정했을 때...
							intRtnVal = ydStkbedDao.getSameLWGpYdP(recPara, rsResult);		// intGp == 25
						} else {
							//용도구분이 없을 때...
							intRtnVal = ydStkbedDao.getSameLWGpYdP2(recPara, rsResult);		
						}
					}
				}
			}

			szLogMsg = "["+ szOperationName +"] 동일길이/폭구분을 가진  모든 공베드 정보 조회 완료(주작업) - 메세지 : " + Integer.toString(intRtnVal);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			
			if(intRtnVal > 0) break;
		}
		
		
		

		if (intRtnVal > 0) {
		//	JPlateYdToLocUtil.srchNconvRecord2Vo(szYD_STK_COL_GP, szYD_STK_BED_NO, recPara, rsResult, listToLoc, JPlateYdConst.TO_LOC_PRIOR_PLATE_EMPTY_BED);
			JPlateYdToLocUtil.srchNconvRecord2Vo("", "", recPara, rsResult, listToLoc, JPlateYdConst.TO_LOC_PRIOR_PLATE_EMPTY_BED);
		} else {
			szRtnMsg = JPlateYdConst.RETN_CD_FAILURE;
		}

		szLogMsg = "["+ szOperationName +"] 메소드 끝 >>>> " + szRtnMsg;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	}
	
	/**
	 * RT상TO위치결정(1후판정정)
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @return
	 * @throws JDTOException
	 */
	public static String procRtToLocForPlateYdYdP2(JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch) throws JDTOException {

		JPlateYdStockDAO   ydStockDao 	= new JPlateYdStockDAO();
		JPlateYdStkLyrDAO  ydStklyrDao 	= new JPlateYdStkLyrDAO();

		String szMethodName			= "procRtToLocForPlateYdYdP2";
		String szOperationName		= "RT상TO위치결정(1후판정정)2";
		String szLogMsg				= null;
		String szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		ArrayList	listToLoc		= null;

//		JDTORecord	recLogMsg		= null;
		JDTORecord	recPara			= null;
		JDTORecord	recTemp			= null;
		JDTORecord	recTemp2		= null;
		JDTORecord	recUpCrnSch		= null;
		JDTORecordSet rsResult		= null;

		String 	szStlNo				= null;
//		String[] szYdStkLyrMtlStat	= {"D", "U"};
		String 	szYdUpWoLoc			= null;
		String 	szYdUpWoLayer		= null;
		String 	szYdDnWoLoc			= null;
		String 	szYdDnWoLayer		= null;
		String 	szYdUpStkColGp		= null;
		String 	szYdUpStkBedNo		= null;
		String 	szYdDnStkColGp		= null;
		String 	szYdDnStkBedNo		= null;
		String 	szYdMtlLGp			= null;
		String 	szYdSchCd			= null;
		String	szYdCrnSchId		= null;
		String 	szYdEqpId			= null;
		String	szYdToLocGuide		= null;
		String	szModifier			= null;

		int    	intYdEqpWrkSh		= 0;						//야드설비작업매수
		int    	intYdEqpWrkWt		= 0;						//야드설비작업중량
		double 	dblYdEqpWrkT		= 0;						//야드설비작업총두께
		int		intYdEqpWrkL		= 0;						//야드설비작업총길이
		String 	szYdEqpWrkMaxW		= null;						//작업재료 중 최대 폭
		String 	szYdEqpWrkMaxL		= null;						//작업재료 중 최대 길이
		int    	intRtnVal 			= 0;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.02 로그 개선 
	    String logId                     	= ydLogUtils.getJDTOLogId(recCrnSch, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
	
	    if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 		// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
		      		
	    szLogMsg = "[" + szOperationName + "] ---- recCrnSch.toString()  \n>>>> " + recCrnSch.toString();
	    ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
//-------------------------------------------------------------------------------------------------------------------------

		szLogMsg = "[" + szOperationName + "] 메소드 시작 - 크레인스케줄확인 ";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료의 최하단 재료정보를 가져온다.
		//----------------------------------------------------------------------------------------------------------------------
		rsCrnwrkmtl.first();
		recPara = rsCrnwrkmtl.getRecord();

		szLogMsg = "[" + szOperationName + "] -------------------- 크레인작업재료의 최하단 재료정보 확인 --------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		ydUtils.displayRecord(szOperationName, recPara);

		szLogMsg = "[" + szOperationName + "] -------------------- 크레인스케줄정보 확인 --------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		ydUtils.displayRecord(szOperationName, recCrnSch);

		szYdCrnSchId    = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID");
		szYdEqpId      	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_EQP_ID");
		szYdToLocGuide	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_TO_LOC_GUIDE");

		intYdEqpWrkSh   = ydDaoUtils.paraRecChkNullInt(recPara, "SH_CNT");
		intYdEqpWrkWt   = ydDaoUtils.paraRecChkNullInt(recPara, "SUM_MTL_WT");
		dblYdEqpWrkT    = ydDaoUtils.paraRecChkNullDouble(recPara, "SUM_MTL_T");
		intYdEqpWrkL    = ydDaoUtils.paraRecChkNullInt(recPara, "MAX_MTL_L");		// "SUM_MTL_L"

		szYdEqpWrkMaxW	= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_W");
		szYdEqpWrkMaxL	= ydDaoUtils.paraRecChkNull(recPara, "MAX_MTL_L");

		szModifier		= ydDaoUtils.paraRecModifier(recPara);

		szLogMsg = "[" + szOperationName + "] 크레인작업재료의 총매수 :" + intYdEqpWrkSh;
		szLogMsg += ", 총중량 :" + intYdEqpWrkWt;
		szLogMsg += ", 총높이  :" + dblYdEqpWrkT;
		szLogMsg += ", TO위치 :" + szYdToLocGuide;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//----------------------------------------------------------------------------------------------------------------------
		//	최하단 재료를 저장품으로부터 조회
		//----------------------------------------------------------------------------------------------------------------------
		szStlNo	= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

		szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보[" + szStlNo + "]를 저장품에서 조회 시작 ";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
		intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsResult);

		if (intRtnVal == 0) {
			szRtnMsg = "[" + szOperationName + "] 존재하지 않습니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szRtnMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		} else if (intRtnVal == -2) {
			szRtnMsg = "[" + szOperationName + "] 파라미터가 존재하지 않습니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szRtnMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		} else if (intRtnVal < 0) {
			szRtnMsg = "[" + szOperationName + "] 오류발생";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szRtnMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		}

		rsResult.first();
		recTemp = rsResult.getRecord();

		szYdMtlLGp = ydDaoUtils.paraRecChkNull(recTemp, "YD_MTL_L_GP");

		szLogMsg = "[" + szOperationName + "] 크레인작업재료의 최하단 재료정보[" + szStlNo + "]를 저장품에서 조회 완료 - 길이구분[" + szYdMtlLGp + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szYdUpStkColGp = recTemp.getFieldString("YD_STK_COL_GP");
		szYdUpStkBedNo = recTemp.getFieldString("YD_STK_BED_NO");
		szYdUpWoLoc    = szYdUpStkColGp + szYdUpStkBedNo;
		szYdUpWoLayer  = recTemp.getFieldString("YD_STK_LYR_NO");

		szLogMsg = "[" + szOperationName + "] 권상지시위치[" + szYdUpWoLoc + "], 권상지시단[" + szYdUpWoLayer + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		

		szYdDnWoLoc 	= szYdToLocGuide.substring(0,6) + "01";
		szYdDnWoLayer	= "001";

		szLogMsg = "[" + szOperationName + "] 권하지시위치[" + szYdDnWoLoc + "], 권하지시단[" + szYdDnWoLayer + "]을 크레인스케줄에 수정 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		recUpCrnSch = JDTORecordFactory.getInstance().create();
		recUpCrnSch.setField("YD_CRN_SCH_ID", 			szYdCrnSchId);
		recUpCrnSch.setField("YD_EQP_ID", 				szYdEqpId);

		recUpCrnSch.setField("YD_UP_STK_COL_GP", 		szYdUpStkColGp);
		recUpCrnSch.setField("YD_UP_STK_BED_NO", 		szYdUpStkBedNo);
		recUpCrnSch.setField("YD_DN_STK_COL_GP", 		szYdDnStkColGp);
		recUpCrnSch.setField("YD_DN_STK_BED_NO", 		szYdDnStkBedNo);

		recUpCrnSch.setField("YD_DN_WO_LOC", 		szYdDnWoLoc);
		recUpCrnSch.setField("YD_DN_WO_LAYER", 		szYdDnWoLayer);

		recUpCrnSch.setField("YD_EQP_WRK_SH", 		String.valueOf(intYdEqpWrkSh));
		recUpCrnSch.setField("YD_EQP_WRK_WT", 		String.valueOf(intYdEqpWrkWt));
		recUpCrnSch.setField("YD_EQP_WRK_T", 		String.valueOf(dblYdEqpWrkT));
		recUpCrnSch.setField("YD_EQP_WRK_L", 		String.valueOf(intYdEqpWrkL));
		recUpCrnSch.setField("YD_EQP_WRK_MAX_W", 	szYdEqpWrkMaxW);
		recUpCrnSch.setField("YD_EQP_WRK_MAX_L",	szYdEqpWrkMaxL);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.02 MODIFIER  szMethodName -> szModifier 변경
		recUpCrnSch.setField("MODIFIER", 			szModifier);
//		recUpCrnSch.setField("MODIFIER", 			(szMethodName.length() > 10) ? szMethodName.substring(0, 10) : szMethodName);
//-------------------------------------------------------------------------------------------------------------------------
		
//-------------------------------------------------------------------------------------------------------------------------
//2024.12.02 recUpCrnSch에 logId 추가 
		recUpCrnSch.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
		
		szRtnMsg = JPlateYdCrnSchUtil.uptCrnSchXYCordYdP(recUpCrnSch);
		
    	szLogMsg = "[" + szOperationName + "] 권하지시위치[" + szYdDnWoLoc + "], 권하지시단[" + szYdDnWoLayer + "]을 크레인스케줄에 수정 완료";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		
		return JPlateYdConst.RETN_CD_SUCCESS;
	}
	
	/**
	 * 2024.11.19 argument에 logId 추가 신규 작성 
	 * 권하위치재료등록
	 * @param rsCrnwrkmtl
	 * @param szYD_DN_WO_LOC
	 * @param szYD_DN_WO_LAYER
	 * @return
	 * @throws JDTOException
	 */
	public static String uptDnWaitOnYdStkLyr(JDTORecordSet rsCrnwrkmtl				/* 크레인작업재료 */
											, String szYD_DN_WO_LOC
											, String szYD_DN_WO_LAYER
											, String szMODIFIER
											, String logId
											) throws JDTOException {

		JPlateYdStkLyrDAO 	ydStklyrDao 	= new JPlateYdStkLyrDAO();

		String szMethodName			= "uptDnWaitOnYdStkLyr";
		String szOperationName		= "권하위치재료등록";
		String szLogMsg				= null;
		String szYD_STK_COL_GP		= szYD_DN_WO_LOC.substring(0, 6);
		String szYD_STK_BED_NO		= szYD_DN_WO_LOC.substring(6);
		String szYD_STK_LYR_NO		= "";
		String szSTL_NO				= null;
		int    intRtnVal 			= 0;
		JDTORecord		recPara		= null;
		JDTORecord		recTemp		= null;

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P"); // log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본

		szLogMsg = "[" + szOperationName + "] 메소드 시작 - 크레인작업재료 건수[" + rsCrnwrkmtl.size() + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_STK_COL_GP", 		szYD_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
		recPara.setField("MODIFIER", 			szMODIFIER);

		for(int ii=1; ii<=rsCrnwrkmtl.size(); ii++) {
			rsCrnwrkmtl.absolute(ii);
			recTemp  = rsCrnwrkmtl.getRecord();
			szSTL_NO = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
			szYD_STK_LYR_NO = ydDaoUtils.stringPlusInt(szYD_DN_WO_LAYER, (ii - 1));

			szLogMsg = "[" + szOperationName + "] [" + ii + "] 크레인작업재료[" + szSTL_NO + "] - 적치열[" + szYD_STK_COL_GP + "], 적치베드[" + szYD_STK_BED_NO + "], 적치단[" + szYD_STK_LYR_NO + "] 권하대기로 등록 시작 ";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			recPara.setField("STL_NO", 					szSTL_NO);
			recPara.setField("YD_STK_LYR_NO", 			szYD_STK_LYR_NO);
			recPara.setField("YD_STK_LYR_MTL_STAT", 	JPlateYdConst.YD_STK_LYR_MTL_STAT_DN_WAIT);


//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 recPara에 logId 추가 
			recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
			
			intRtnVal = ydStklyrDao.updYdStklyrStat(recPara);		// intGp == 0

			if (intRtnVal <= 0) {
				szLogMsg = "[" + szOperationName + "] ["+ii+"] 크레인작업재료[" + szSTL_NO + "] - 적치열[" + szYD_STK_COL_GP + "], 적치베드[" + szYD_STK_BED_NO + "], 적치단[" + szYD_STK_LYR_NO + "] 권하대기로 등록 시 오류발생 - 오류 :: " + Integer.toString(intRtnVal);
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
				return JPlateYdConst.RETN_CD_FAILURE;
			}

			szLogMsg = "[" + szOperationName + "] [" + ii + "] 크레인작업재료[" + szSTL_NO + "] - 적치열[" + szYD_STK_COL_GP + "], 적치베드[" + szYD_STK_BED_NO + "], 적치단[" + szYD_STK_LYR_NO + "] 권하대기로 등록 완료 ";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		}

		szLogMsg = "[" + szOperationName + "] 메소드 끝";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of uptDnWaitOnYdStkLyr() 
	
	
}
