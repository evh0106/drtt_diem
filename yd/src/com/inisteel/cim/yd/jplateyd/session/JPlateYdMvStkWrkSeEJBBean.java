/*
 * @(#) 2후판정정야드 이적작업요구 Session EJB
 *
 * @version			V1.00
 * @author			김현우
 * @date			2013/01/24
 *
 * @description		이적작업요구 Session EJB
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2013/01/24   김현우      김현우       최초작성 
 */

package com.inisteel.cim.yd.jplateyd.session;

import java.util.Vector;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;

import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdCommonUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.14 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 * 이적작업요구  Session EJB
 *
 * @ejb.bean name= "JPlateYdMvStkWrkSeEJB" jndi-name= "JPlateYdMvStkWrkSeEJB" type= "Stateless"
 *           view-type= "remote" display-name= "" description= ""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool= "10" max-beans-in-free-pool= "100"
 * @ejb.transaction type= "Required"
 */
public class JPlateYdMvStkWrkSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;

	// Session Name
	private final static String SZ_SESSION_NAME = JPlateYdMvStkWrkSeEJBBean.class.getName();

	private JPlateYdUtils 		ydUtils 		= new JPlateYdUtils();
	private JPlateYdDaoUtils	ydDaoUtils 		= new JPlateYdDaoUtils();

	private YdPICommDAO   		ydPICommDAO   	= new YdPICommDAO();
	
	// [DEBUG] message flag
	private boolean bDebugFlag = true;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.14 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
    private YdUtils 			ydLogUtils  	= new YdUtils();
	
    
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * 오퍼레이션명 : 준비스케줄 LOT편성
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public String procPrepLotCompByCapa(JDTORecord msgRecord) throws DAOException {

		// DAO 선언
		JPlateYdWrkbookDAO 		ydWrkbookDao 	= new JPlateYdWrkbookDAO();				// 작업예약 DAO
		JPlateYdWrkbookMtlDAO 	ydWrkbookMtlDao = new JPlateYdWrkbookMtlDAO();			// 작업예약 재료 DAO
		JPlateYdStockDAO 		ydStockDao 		= new JPlateYdStockDAO();				// 저장품DAO
		JPlateYdSchRuleDAO		ydSchRuleDao	= new JPlateYdSchRuleDAO();				// 스케줄기준DAO
		JPlateYdEqpDAO			ydEqpDao		= new JPlateYdEqpDAO();					// 야드설비DAO

		//레코드 선언
		JDTORecord 		recPara     = null;
		JDTORecord 		recOutPara  = null;
		JDTORecord 		recSchPara  = null;
		JDTORecord 		recResult 	= null;
		JDTORecord 		recTemp 	= null;
		//레코드셋 선언
		JDTORecordSet 	rsResult 	= null;
		JDTORecordSet 	rsTemp 		= null;
		JDTORecordSet 	rsSchRule 	= JDTORecordFactory.getInstance().createRecordSet("");

		//리턴값(boolean)
		int 	intRtnVal 			= 0;

		String 	szMsg           	= "";
		String 	szMethodName    	= "procPrepLotCompByCapa";
		String 	szOperationName 	= "준비스케줄 LOT 편성";

		int 	intLotGpSh      	= 0;								// Lot 편성 재료 매수
		String 	szYD_GP				= null;								// 야드구분
		String 	szYD_BAY_GP			= null;								// 동구분
		String 	szYD_AIM_YD_GP		= null;								// 목표야드구분
		String 	szYD_AIM_BAY_GP		= null;								// 목표동구분
		String 	szYD_AIM_SPAN_GP	= null;								// 목표스판구분
		String 	szYD_AIM_COL_GP		= null;								// 목표적치열구분
		String 	szYD_AIM_BED_NO		= null;								// 목표적치BED구분
		String 	szYD_MAIN_WRK_GP 	= null;								// 주작업이적구분
		String 	szYD_TO_LOC_GUIDE_GP= null;								// TO위치가이드구분
		String 	szYD_SCH_CD         = "";								// 스케줄코드
		String 	szPREV_YD_SCH_CD    = "";								// 스케줄코드
		String 	szSTL_NO            = null;								// 재료번호
		String[] arrYD_STK_COL_GP 	= null;								// 적치열배열
		String[] arrYD_STK_BED_NO 	= null;								// 적치베드 배열
		String 	szTCAR 				= "";								// 대차
		String 	szPREV_TCAR 		= "";								// 대차
		String 	szYD_STK_COL_GP 	= "";								// 적치열구분
		String 	szYD_STK_COL_GP_CMP = "XX01";							// 이전 적치열구분 - 비교를 위해서 임의의 값을 설정
		String 	szYD_TO_LOC_DCSN_MTD= "";								// 야드 TO위치결정방법
		String 	szYD_TO_LOC_GUIDE 	= "";								// 야드TO위치가이드
		String 	szSTL_LIST 			= "";								// 야드TO위치가이드
		String	szREGISTER			= "";								// 등록자
		String	szYD_CURR_BAY_GP	= "";								// 대차현재동
		String	szYD_EQP_NAME		= "";								// 대차설비명
		String	szCrnschSkipFlag	= "";								// 크레인스케줄 호출 SKIP 여부

		double 	dblCurrWidth		= 0;								// 재료의 현재 폭
		long 	lngCurrWt			= 0;								// 재료의 현재 중량
		long 	lngSumWt 			= 0;								// 누적중량
		int 	intMtlSh			= 0;								// 크레인작업가능매수
		double 	dblMaxWidth			= 0;								// 크레인작업가능 폭
		boolean bIsInsideMv			= true;								// 동내이적인 지 동간이적인 지 판단하는 변수
		String 	szYD_TC_GP			= null;
		String 	szYD_EQP_WRK_SH		= null;								// 작업매수
		int 	intYD_EQP_WRK_SH	= -1;								// 작업매수
		String 	szARR_WLOC_CD		= null;
		String	szYD_WBOOK_ID		= null;								// 작업예약ID
		String	szYD_WRK_CRN		= null;								// 크레인설비ID
		boolean bookInFlag 			= false;							// BOOK-IN 여부
		String 	ydWrkCrnPrior 		= "1"; 								// 야드작업크레인우선순위
		int		insWrkMtlCnt 		= 0;
		int		iWBookInsCnt		= 0;								// 작업예약등록 건수
		int		iCrnSchCnt			= 0;								// 크레인스케쥴 호출 건수


//---------------------------------------------------------------------------------------------
// 2024.12.17 argument에 logId 없으면 새로 발본
//---------------------------------------------------------------------------------------------
		String logId                        = ydLogUtils.getJDTOLogId(msgRecord, "F");  // JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
		
		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F"); 				// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//---------------------------------------------------------------------------------------------
						
		StringBuffer sbARR_WBOOK_ID = new StringBuffer();

		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;	//리턴메세지정의

		// TC CODE 추출
		String szRcvTcCode 			= ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {
			szRtnMsg = "TC Code Error (" + szRcvTcCode + ")";
			szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			
			return szRtnMsg;
		}

		// TC CODE DISPLAY
		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		}

		try {
			szMsg    = "[" + szOperationName + "] ----------- START :: " + msgRecord.toString();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//----------------------------
			// 1. 입력 파라미터 정합성 체크
			//----------------------------
			/*
			 * 야드구분, 동구분, 스판(선택), 목표행선구분, 목표동, 주작업이적구분, TO위치가이드구분
			 */
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
			if ("".equals(szYD_GP)) {

				szRtnMsg = "[전문 이상] 야드구분이 없습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szRtnMsg;

			}
			// 동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
			if ("".equals(szYD_BAY_GP)) {
				szRtnMsg = "[전문 이상] 동구분이 없습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szRtnMsg;
			}

			// 적치열구분
			arrYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP").split(";");
			if (arrYD_STK_COL_GP == null || arrYD_STK_COL_GP.length == 0) {
				szRtnMsg = "[전문 이상] 적치열구분이 없습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szRtnMsg;
			}

			// 적치베드구분
			arrYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO").split(";");
			if (arrYD_STK_BED_NO == null || arrYD_STK_BED_NO.length == 0) {
				szRtnMsg = "[전문 이상] 적치베드번호가 없습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szRtnMsg;
			}

			// 주작업이적구분
			szYD_MAIN_WRK_GP  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MAIN_WRK_GP");
			if ("".equals(szYD_MAIN_WRK_GP)) {
				// 1:이적 , 	2:RT BOOK-IN ,  3:GAS장 보급 , 4:보수장 보급, 5:TOD보급
				//			6:RT BOOK-OUT , 7:GAS장 추출 , 8:보수장 추출, 9:TOD추출
				//          A:이송상차,      	B:이송하차
				szRtnMsg = "[전문 이상] 주작업이적구분이 없습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szRtnMsg;
			}
			// TO위치가이드구분
			szYD_TO_LOC_GUIDE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_LOC_GUIDE_GP");
			if ("".equals(szYD_TO_LOC_GUIDE_GP)) {
				szRtnMsg = "[전문 이상] TO위치가이드구분이 없습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szRtnMsg;

			} else {

				// 목표야드구분
				szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_YD_GP");
				if ("".equals(szYD_AIM_YD_GP)) {
					szRtnMsg = "[전문 이상] 목표야드구분이 없습니다.";
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					
					return szRtnMsg;
				}
				// 목표동구분
				szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP");
				if ("".equals(szYD_AIM_BAY_GP)) {
					szRtnMsg = "[전문 이상] 목표동구분이 없습니다.";
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					
					return szRtnMsg;
				}

				// To위치 Guide 일때
				if ("Y".equals(szYD_TO_LOC_GUIDE_GP)) {
					szYD_AIM_SPAN_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_SPAN_GP");		// 목표스판구분
					szYD_AIM_COL_GP  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_COL_GP");		// 목표적치열구분
					szYD_AIM_BED_NO  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BED_NO");		// 목표적치BED구분
				}
			}

			szREGISTER = ydDaoUtils.paraRecModifier(msgRecord);										// 등록자, 수정자

			szCrnschSkipFlag = ydDaoUtils.paraRecChkNull(msgRecord, "CRNSCH_SKIP_FLAG", "N");		// 크레인스케줄 SKIP 여부 : 'Y'일때 SKIP

			// BOOK-IN, GAS장보급, 보수장보급일경우  작업예약 ID를 건수만큼 생성
			if (JPlateYdConst.YD_MAIN_WRK_GP_RT_IN.equals(szYD_MAIN_WRK_GP) ||		// RT	Book-In
				JPlateYdConst.YD_MAIN_WRK_GP_GAS_IN.equals(szYD_MAIN_WRK_GP) ||		// GAS장	Book-In (보급)
				JPlateYdConst.YD_MAIN_WRK_GP_BS_IN.equals(szYD_MAIN_WRK_GP) ) {		// 보수장	Book-In (보급)

				bookInFlag = true;
			}

			//---------------------------------------------------------------------------------------------
			//	준비스케줄 등록 시 지정한 작업매수의 값을 전달하여 해당하는 매수만큼 작업예약에 등록
			//---------------------------------------------------------------------------------------------
			szYD_EQP_WRK_SH = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_SH");
			if (!"".equals(szYD_EQP_WRK_SH)) {
				intYD_EQP_WRK_SH = Integer.parseInt(szYD_EQP_WRK_SH);
				szMsg = "사용자가 지정한 작업매수 : " + intYD_EQP_WRK_SH;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			}

			szARR_WLOC_CD 	= ydDaoUtils.paraRecChkNull(msgRecord, "ARR_WLOC_CD");		//착지개소코드
			szYD_TC_GP 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_TC_GP");			//대차선택
			szYD_AIM_YD_GP 	= szYD_GP;													//목표야드구분
			szSTL_LIST 		= ydDaoUtils.paraRecChkNull(msgRecord, "STL_LIST");			//작업재료리스트

			// 2013.09.06 대차 작업일경우 대차의 현재동 체크 : 현재동에 대차가 없으면 오류로 처리
			if (!"".equals(szYD_TC_GP)) {
				rsResult = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
				recTemp  = JDTORecordFactory.getInstance().create();
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_EQP_ID", szYD_TC_GP); 				//대차설비ID

				intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult);
				if (intRtnVal <= 0) {
					szRtnMsg = "해당 대차가 존재하지 않습니다." + szYD_TC_GP;
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					
					return szRtnMsg;
				}
				rsResult.first();
				recTemp = rsResult.getRecord();

				szYD_CURR_BAY_GP = ydDaoUtils.paraRecChkNull(recTemp, "YD_CURR_BAY_GP");
				szYD_EQP_NAME	 = ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_NAME");

				if (!szYD_CURR_BAY_GP.equals(szYD_BAY_GP)) {
					szRtnMsg = "[" + szYD_EQP_NAME + "]가 현재 [" + szYD_CURR_BAY_GP + "] 동에 있어 작업할수 없습니다.";
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					
					return szRtnMsg;
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");

			// 주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차
			if (JPlateYdConst.YD_MAIN_WRK_GP_MV.equals(szYD_MAIN_WRK_GP)) {				// 이적인 경우

				//작업재료LIST 검색
//---------------------------------------------------------------------------------------------
// 2024.12.17 Argument에 logId 추가
//				szRtnMsg = chkPrepLotGpStlList(szSTL_LIST, szARR_WLOC_CD, rsResult);
				szRtnMsg = chkPrepLotGpStlList(szSTL_LIST, szARR_WLOC_CD, rsResult, logId);
//---------------------------------------------------------------------------------------------

				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					return szRtnMsg;
				}
			} else if (JPlateYdConst.YD_MAIN_WRK_GP_RT_IN.equals(szYD_MAIN_WRK_GP)
					|| JPlateYdConst.YD_MAIN_WRK_GP_RT_OUT.equals(szYD_MAIN_WRK_GP)) {		// BOOK-IN/OUT인 경우

				//작업재료LIST 검색
//---------------------------------------------------------------------------------------------
// 2024.12.17 Argument에 logId 추가
//				szRtnMsg = chkPrepLotGpStlList(szSTL_LIST, szARR_WLOC_CD, rsResult);
				szRtnMsg = chkPrepLotGpStlList(szSTL_LIST, szARR_WLOC_CD, rsResult, logId);
//---------------------------------------------------------------------------------------------

				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					return szRtnMsg;
				}
			} else {																		// 보급/추출인 경우

				//작업재료LIST 검색
//---------------------------------------------------------------------------------------------
// 2024.12.17 Argument에 logId 추가
//				szRtnMsg = chkPrepLotGpStlList(szSTL_LIST, szARR_WLOC_CD, rsResult);
				szRtnMsg = chkPrepLotGpStlList(szSTL_LIST, szARR_WLOC_CD, rsResult, logId);
//---------------------------------------------------------------------------------------------

				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					return szRtnMsg;
				}
			}

			intLotGpSh = rsResult.size();
			rsResult.first();

			Vector rsGroup = new Vector();

			//동일한 스판별로 대상재를 편성한다.
			//보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호  데이터를 세팅한다.
			for (int ii = 1; ii<=intLotGpSh; ii++) {

				//레코드 추출
				recPara = rsResult.getRecord();

				szSTL_NO 		= ydDaoUtils.paraRecChkNull(recPara, 		"STL_NO");			// 재료번호
				lngCurrWt 		= ydDaoUtils.paraRecChkNullLong(recPara, 	"YD_MTL_WT");		// 야드재료중량
				dblCurrWidth 	= ydDaoUtils.paraRecChkNullDouble(recPara, 	"YD_MTL_W");		// 야드재료폭
				szYD_STK_COL_GP	= ydDaoUtils.paraRecChkNull(recPara, 		"YD_STK_COL_GP");	// 적치열구분

				szMsg = "작업재료확인 : 현재Count[" + ii + "] 현재 재료번호[" + szSTL_NO + "]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 recPara에 logId 추가
				recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
						        			        																			              	        	
				szRtnMsg = this.chkExistSchdule(recPara);
				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {

					szMsg = "재료번호[" + szSTL_NO + "] 로 작업예약/크레인스케쥴 존재여부 체크 .. 실패 >>>>" + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					return szRtnMsg;
				}

				/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				 * 스케줄코드, 대차설비 ID 구하기
				 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				szYD_TO_LOC_DCSN_MTD 	= "";		// 야드 TO 위치결정방법
				szYD_TO_LOC_GUIDE 		= "";		// 야드 TO 위치가이드
				szTCAR 					= "";		// 대차

				// TO위치GUIDE가 존재하고 이고 목표동이 다른 경우에는 대차상차스케줄을 생성하고 대차 배정
				if ("Y".equals(szYD_TO_LOC_GUIDE_GP)) {				// TO위치GUIDE 이적인 경우
					szYD_TO_LOC_DCSN_MTD = "F";
					szYD_TO_LOC_GUIDE	 = szYD_AIM_YD_GP + szYD_AIM_BAY_GP + szYD_AIM_SPAN_GP + szYD_AIM_COL_GP + szYD_AIM_BED_NO;

					szMsg = "TO위치GUIDE 이적인 경우 szYD_TO_LOC_GUIDE : " + szYD_TO_LOC_GUIDE;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					if (szYD_BAY_GP.equals(szYD_AIM_BAY_GP))	{	// 목표동이 같은 경우 - 동내이적 스케줄
						bIsInsideMv = true;
						
//---------------------------------------------------------------------------------------------
// 2024.12.18 Argument에 logId 추가
//						szYD_SCH_CD = getSchCdByWrkGp(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, "");
						szYD_SCH_CD = getSchCdByWrkGp(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, "", logId);
//---------------------------------------------------------------------------------------------
						szMsg = ">> 목표동이 같은 경우 - 동내이적 스케줄 : " + szYD_SCH_CD + ", " + szTCAR;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					} else {										// 목표동이 다른 경우 - 대차상차스케줄
						bIsInsideMv = false;
						szTCAR = szYD_TC_GP;
						if ("".equals(szTCAR)) {
							String[] retValue = JPlateYdCommonUtils.getSchCdNTcar(szYD_STK_COL_GP);
							szTCAR = retValue[1];
						}
						
//---------------------------------------------------------------------------------------------
// 2024.12.18 Argument에 logId 추가
//						szYD_SCH_CD = getSchCdByWrkGp(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, szTCAR);
						szYD_SCH_CD = getSchCdByWrkGp(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, szTCAR, logId);
//---------------------------------------------------------------------------------------------

						szMsg = ">>>> 목표동이 다른 경우 - 대차상차스케줄[동간이적] : " + szYD_SCH_CD + ", " + szTCAR;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					}
				} else {			//위치검색기준으로 이적인 경우
					szYD_TO_LOC_GUIDE 	= szYD_AIM_YD_GP + szYD_AIM_BAY_GP;

					szMsg = "위치검색기준으로 이적인 경우 >>>> szYD_TO_LOC_GUIDE :: " + szYD_TO_LOC_GUIDE;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					if (szYD_BAY_GP.equals(szYD_AIM_BAY_GP)) {		//목표동이 같은 경우 - 동내이적 스케줄
						bIsInsideMv = true;
						
//---------------------------------------------------------------------------------------------
// 2024.12.18 Argument에 logId 추가
//						szYD_SCH_CD = getSchCdByWrkGp(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, "");
						szYD_SCH_CD = getSchCdByWrkGp(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, "", logId);
//---------------------------------------------------------------------------------------------
						
						szMsg = "목표동이 같은 경우 - 동내이적 스케줄 : " + szYD_SCH_CD + ", " + szTCAR;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					} else {											//목표동이 다른 경우 - 대차상차스케줄
						bIsInsideMv = false;
						szTCAR = szYD_TC_GP;
						if ("".equals(szTCAR)) {
							String[] retValue = JPlateYdCommonUtils.getSchCdNTcar(szYD_STK_COL_GP);
							szTCAR = retValue[1];
						}
						
//---------------------------------------------------------------------------------------------
// 2024.12.18 Argument에 logId 추가
//						szYD_SCH_CD = getSchCdByWrkGp(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, szTCAR);
						szYD_SCH_CD = getSchCdByWrkGp(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, szTCAR, logId);
//---------------------------------------------------------------------------------------------

						szMsg = "위치검색기준 .. 목표동이 다른 경우 - 대차상차스케줄[동간이적] : " + szYD_SCH_CD + ", " + szTCAR;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					}
				}

				szMsg = "[" + szOperationName + "] >>>>> TO위치결정방법[" + szYD_TO_LOC_DCSN_MTD + "], TO위치GUIDE[" + szYD_TO_LOC_GUIDE + "]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				szMsg = "[" + szOperationName + "] >>>>> 재료번호 [" + szSTL_NO + "] : 야드재료중량[" + lngCurrWt + "], 야드재료폭[" + dblCurrWidth + "], 적치열구분[" + szYD_STK_COL_GP + "], 목표동[" + szYD_AIM_BAY_GP + "]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				/*
				 * 스케줄기준과 크레인 할당
				 */
				if (!szPREV_YD_SCH_CD.equals(szYD_SCH_CD)) {
					szMsg = "[" + szOperationName + "] 스케줄코드비교 >>>>> YD_SCH_CD[" + szYD_SCH_CD + "], PREV_YD_SCH_CD[" + szPREV_YD_SCH_CD + "]";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					recResult = JDTORecordFactory.getInstance().create();
					
//---------------------------------------------------------------------------------------------
// 2024.12.19 Argument에 logId 추가
//					szRtnMsg = JPlateYdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recResult);
					szRtnMsg = JPlateYdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recResult, logId);
//---------------------------------------------------------------------------------------------
					
					if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
						return szRtnMsg;
					}
					// 크레인ID
					szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recResult, "YD_WRK_CRN");
				}

				//대차적치가능 중량 구하기
				if (!szPREV_TCAR.equals(szTCAR)) {
					szMsg = "[" + szOperationName + "] 대차설비비교 >>>>> TCAR[" + szTCAR + "], PREV_TCAR[" + szPREV_TCAR + "]";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				}

				//동일한 스판이 아닌 경우
				if (!szYD_STK_COL_GP.substring(0, 4).equals(szYD_STK_COL_GP_CMP.substring(0, 4))) {
					rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
					rsGroup.add(rsTemp);
					dblMaxWidth = dblCurrWidth;
					lngSumWt = lngCurrWt;
					intMtlSh = 1;
					szMsg = "[" + szOperationName + "] 동내이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					szMsg = "[" + szOperationName + "] 스판비교 >>>>> YD_STK_COL_GP[" + szYD_STK_COL_GP + "], PREV_YD_STK_COL_GP[" + szYD_STK_COL_GP_CMP + "]";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				} else if (bIsInsideMv) {		//동내이적

					if (bookInFlag) {
						szMsg = "[" + szOperationName + "] 동내이적 - BOOK-IN : ";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
						rsGroup.add(rsTemp);

					} else {
						/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
						 * 업무기준 : 주작업인 경우
						 * 		1. 작업재료를 동내이적 대상재는 크레인 작업능력만큼 LOT편성
						 * 		2. 작업재료를 동간이적 대상재는 대차 작업능력만큼 LOT편성
						 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
						szMsg = "[" + szOperationName + "] 동내이적 - bIsInsideMv : " + bIsInsideMv;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						//주작업인 경우에만.
						lngSumWt += lngCurrWt;
						intMtlSh++;
						szMsg = "[" + szOperationName + "] 동내이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						//크레인작업가능능력 체크
						intRtnVal = JPlateYdCommonUtils.chkGetCrnspec(dblCurrWidth, dblMaxWidth, lngSumWt, intMtlSh, recResult);
						szMsg = "[" + szOperationName + "] 동내이적(크레인작업가능능력 체크 : intRtnVal >> " + intRtnVal + ")";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					// 2013.07.22 김현우 재료번 1건의 작업 예약 ID생성하도록 보완 ..
					//
					//	if (intRtnVal == -1 || intRtnVal == -2 || intRtnVal == -3) {
							rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
							rsGroup.add(rsTemp);
							dblMaxWidth = dblCurrWidth;
							lngSumWt = lngCurrWt;
							intMtlSh = 1;
							szMsg = "[" + szOperationName + "] 동내이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					//		szMsg = "[" + szOperationName + "] 크레인작업가능능력 초과";
					//		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					//	}

					}

					if (dblMaxWidth < dblCurrWidth) {
						dblMaxWidth = dblCurrWidth;
					}

				} else {							//동간이적

					szMsg = "[" + szOperationName + "] 동간이적(대차적치능력 - 크레인작업가능능력으로 체크) - bIsInsideMv : " + bIsInsideMv;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					lngSumWt += lngCurrWt;
					intMtlSh++;
					szMsg = "[" + szOperationName + "] 동간이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					//크레인작업가능능력 체크
					intRtnVal = JPlateYdCommonUtils.chkGetCrnspec(dblCurrWidth, dblMaxWidth, lngSumWt, intMtlSh, recResult);
					szMsg = "[" + szOperationName + "] 동간이적(대차적치능력 - 크레인작업가능능력으로 체크 : intRtnVal " + intRtnVal + ")";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				// 2013.07.22 김현우 재료번 1건의 작업 예약 ID생성하도록 보완 ..
				//
				//	if (intRtnVal == -1 || intRtnVal == -2 || intRtnVal == -3) {

						rsTemp = JDTORecordFactory.getInstance().createRecordSet("yd");
						rsGroup.add(rsTemp);
						dblMaxWidth = dblCurrWidth;
						lngSumWt = lngCurrWt;
						intMtlSh = 1;
						szMsg = "[" + szOperationName + "] 동간이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				//		szMsg = "[" + szOperationName + "(대차적치능력)] 크레인작업가능능력으로 체크 후 초과";
				//		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				//
				//	}

					if (dblMaxWidth < dblCurrWidth) {
						dblMaxWidth = dblCurrWidth;
					}
				}

				rsTemp.addRecord(recPara);
				szYD_STK_COL_GP_CMP = szYD_STK_COL_GP;

				//다음 레코드로
				rsResult.next();

				szPREV_YD_SCH_CD 	= szYD_SCH_CD;			//스케줄코드
				szPREV_TCAR 		= szTCAR;				//대차설비ID

				//---------------------------------------------------------------------------------------------
				//	후판정정의 준비스케줄 등록 시 지정한 작업매수만큼 처리된 후에는 루프 종료
				//---------------------------------------------------------------------------------------------
				if (!"".equals(szYD_EQP_WRK_SH)) {
					if (ii > intYD_EQP_WRK_SH)	{
						szMsg = "사용자가 지정한 작업매수[" + intYD_EQP_WRK_SH + "]와 같으므로 루프 종료 - 반복변수값[" + ii + "]";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						
						break;
					}
				}
			} // end for

			szMsg = "[" + szOperationName + "] 대상재 작업예약 그룹 갯수 : " + rsGroup.size();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//----------------------------
			// 2. 작업예약등록
			//----------------------------
			szMsg = "[" + szOperationName + "] 작업예약등록 시작 ";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			for(int ii = 0 ; ii < rsGroup.size(); ii++) {
				rsTemp = (JDTORecordSet)rsGroup.get(ii);

				for(int jj = 0; jj < rsTemp.size(); jj++) {
					rsTemp.absolute(jj + 1);
					recOutPara = rsTemp.getRecord();

					if (jj==0 || bookInFlag) {							// 첫번째거나  Book-In일 경우는 작업예약 ID를 매번 생성
			//		if ((ii==0 && jj==0) || bookInFlag) {				// 첫번째거나  Book-In일 경우는 작업예약 ID를 매번 생성
						//----------------------------
						// 2.1. 작업예약ID SELECT
						//----------------------------
						szYD_WBOOK_ID = ydWrkbookDao.getSeqId();

						szMsg = "[" + szOperationName + "] >>>> 작업예약ID SELECT >>>> (" + ii + "," + jj + ")" + szYD_WBOOK_ID;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						if (iWBookInsCnt > 0) {
							sbARR_WBOOK_ID.append(";");
						}
						sbARR_WBOOK_ID.append(szYD_WBOOK_ID);
						iWBookInsCnt ++;

						//----------------------------
						// 2.2. 스케줄기준 정보 조회
						//----------------------------
						ydWrkCrnPrior = "1"; 							//야드작업크레인우선순위

						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_SCH_CD", szYD_SCH_CD); 	//야드스케쥴코드

						szMsg = "[" + szOperationName + "] 야드스케쥴기준 조회 START";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						intRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsSchRule);

						szMsg = "[" + szOperationName + "] 야드스케쥴기준 조회 End :: " + Integer.toString(intRtnVal);
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						if (intRtnVal == 1) {
							rsSchRule.first();
							recPara = rsSchRule.getRecord();
							ydWrkCrnPrior = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");

							szMsg = "[" + szOperationName + "] 야드스케쥴우선순위 :: " + ydWrkCrnPrior;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						} else {
							szMsg = "[" + szOperationName + "] 스케줄코드[" + szYD_SCH_CD + "]로 스케줄기준 조회 시 오류발생 ::" + Integer.toString(intRtnVal);
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						}

						//----------------------------
						// 2.3 작업예약 INSERT
						//----------------------------
						if ("Y".equals(szYD_TO_LOC_GUIDE_GP)) {
							szYD_AIM_YD_GP  = szYD_TO_LOC_GUIDE.substring(0, 1);
							szYD_AIM_BAY_GP = szYD_TO_LOC_GUIDE.substring(1, 2);
						}

						// 작업예약 레코드 생성
						recOutPara.setField("YD_LOT_GP_SH",       	Integer.toString(rsTemp.size())		);		// Lot편성 매수
						recOutPara.setField("YD_GP",      			szYD_GP								);		// 야드구분
						recOutPara.setField("YD_BAY_GP",      		szYD_BAY_GP							);		// 동구분
						recOutPara.setField("YD_AIM_YD_GP",      	szYD_AIM_YD_GP						);		// 목표야드구분
						recOutPara.setField("YD_AIM_BAY_GP",      	szYD_AIM_BAY_GP						);		// 목표동구분
						//-----------------------------------------------------------------------------------------------------------
						//	대차상차스케줄인 경우에는
						//	1. 동까지만 선택하는 경우에 대차하차스케줄의 TO위치결정 시는 위치검색베드 적용
						//	2. 베드까지 선택하는 경우에 대차하차스케줄의 TO위치결정 시는 사용자지정위치로 적용
						//-----------------------------------------------------------------------------------------------------------
						//if ("F".equals(szYD_TO_LOC_DCSN_MTD) && szYD_TO_LOC_GUIDE.length() == 8) {
						//	recOutPara.setField("YD_TO_LOC_DCSN_MTD",	szYD_TO_LOC_DCSN_MTD);			//야드 TO위치결정방법
						//	recOutPara.setField("YD_TO_LOC_GUIDE",     	szYD_TO_LOC_GUIDE);				//야드TO위치가이드
						//}
						recOutPara.setField("YD_TO_LOC_DCSN_MTD",	szYD_TO_LOC_DCSN_MTD				);		// 야드 TO위치결정방법
						recOutPara.setField("YD_TO_LOC_GUIDE",     	szYD_TO_LOC_GUIDE					);		// 야드TO위치가이드
						recOutPara.setField("YD_WRK_PLAN_TCAR",     szTCAR								);		// 작업계획대차
						recOutPara.setField("YD_WBOOK_ID",      	szYD_WBOOK_ID						);		// 작업예약ID
						recOutPara.setField("REGISTER", 			szREGISTER							);
						recOutPara.setField("MODIFIER", 			szREGISTER							);
						recOutPara.setField("YD_SCH_CD", 			szYD_SCH_CD							);		// 야드스케쥴코드
						recOutPara.setField("YD_SCH_PRIOR", 		ydWrkCrnPrior						);		// 야드스케쥴우선순위
						recOutPara.setField("YD_SCH_PROG_STAT", 	"W"									);		// 야드스케쥴진행상태(스케줄수행대기)
						recOutPara.setField("YD_SCH_ST_GP", 		"M"									);		// 야드스케쥴기동구분 (A:Auto,B:BackUp,M:Manual)
						recOutPara.setField("YD_SCH_REQ_GP", 		"X"									);		// 야드스케쥴요청구분

						szMsg = "[" + szOperationName + "] 2.3.작업예약 Insert :: ";	// + recOutPara.toString();
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						// 작업예약 Insert
						intRtnVal = ydWrkbookDao.insYdWrkbook(recOutPara);
						if (intRtnVal < 1) {
							szRtnMsg = "작업예약 데이터 등록 중 에러 .." + Integer.toString(intRtnVal);
							szMsg    = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							
							return szRtnMsg;
						}
					} else {
						recOutPara.setField("YD_WBOOK_ID",      	szYD_WBOOK_ID);			//작업예약ID
						recOutPara.setField("REGISTER", 			szREGISTER);
						recOutPara.setField("MODIFIER", 			szREGISTER);
					}

					szSTL_NO = ydDaoUtils.paraRecChkNull(recOutPara, "STL_NO");				//재료번호

					//----------------------------
					// 2.4. 작업예약재료 INSERT
					//----------------------------
					szMsg = "[" + szOperationName + "] 2.4.작업예약재료 Insert :: ";	// + recOutPara.toString();
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					intRtnVal = ydWrkbookMtlDao.insYdWrkbookMtl(recOutPara);
					if (intRtnVal < 1) {
						szRtnMsg = "작업예약재료 데이터 [" + szSTL_NO + "] 등록 중 에러 .. " + Integer.toString(intRtnVal);
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return szRtnMsg;
					}

					insWrkMtlCnt ++;

					//----------------------------------
					// 2.5. 저장품에 작업예약ID를 UPDATE
					//----------------------------------
					recOutPara.setField("STL_NO", 		szSTL_NO		);
					recOutPara.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID	);
					recOutPara.setField("YD_SCH_CD", 	szYD_SCH_CD		);

					szMsg = "[" + szOperationName + "] 2.5.저장품에 작업예약ID를 UPDATE ";	// + recOutPara.toString();
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					intRtnVal = ydStockDao.updYdStockWbook(recOutPara);
					if (intRtnVal < 1) {
						szRtnMsg = "저장품 데이터 [" + szSTL_NO + "] 수정 중 에러 .. " + Integer.toString(intRtnVal);
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return szRtnMsg;
					}

					//----------------------------------
					// 2.6. 야드적치단 활성상태 UPDATE
					//----------------------------------
					//적치단 야드적치단재료상태 수정
					/*
					recOutPara.setField("YD_STK_LYR_MTL_STAT", "U"); 				//권상대기

					szMsg = "[" + szOperationName + "] 2.6.야드적치단 활성상태 UPDATE ";	// + recOutPara.toString();
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					intRtnVal = ydStkLyrDao.updYdStklyrStat(recOutPara);
					if (intRtnVal <= 0) {
						szRtnMsg = "야드적치단재료상태 수정 ERROR .. " + Integer.toString(intRtnVal);
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
					*/
				}//End Loop jj
			}//End Loop ii

			if ("Y".equals(szCrnschSkipFlag)) {

				szMsg = "[" + szOperationName + "] ----------- 작업예약만 등록하고 크레인 스케줄 호출은 SKIP함 >>>>";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			} else {
				iCrnSchCnt = 0;
				if (insWrkMtlCnt > 0) {

					String[] arrWBookId = sbARR_WBOOK_ID.toString().split(";");

					szMsg = "[" + szOperationName + "] ----------- 3.1.이적 스케줄기동 START .... 재료건수 :: " + insWrkMtlCnt + " 작업예약ID :: " + sbARR_WBOOK_ID.toString();
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					for(int ii=0; ii<iWBookInsCnt; ii++) {

		    			//-----------------------------------------------------
			    		// 작업예약 건수 조회하여 MAX건 초과시는 크레인 스케쥴 기동 SKIP
		    			//-----------------------------------------------------
			    		if (iCrnSchCnt >= JPlateYdConst.MAX_CRN_SCH_CNT) {
			    			szMsg = "[" + szOperationName + "] 작업예약 " + JPlateYdConst.MAX_CRN_SCH_CNT + "건 등록  이후 재료 SKIP .... ii >>>> " + Integer.toString(ii);
			    			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			    			
			    			continue;
			    		}

						//----------------------------------
						// 3.1.이적 스케줄기동
						//----------------------------------
						recSchPara 	= JDTORecordFactory.getInstance().create();
						recSchPara.setField("MSG_ID", 		"YDYDJ"			);		// TC코드
						recSchPara.setField("YD_EQP_ID", 	szYD_WRK_CRN	);		// 크레인설비ID
						recSchPara.setField("YD_SCH_CD",	szYD_SCH_CD		);		// 크레인스케줄코드
						recSchPara.setField("YD_WBOOK_ID",	arrWBookId[ii]	);		// 작업예약ID
						recSchPara.setField("REGISTER", 	szREGISTER		);
						recSchPara.setField("MODIFIER", 	szREGISTER		);
						recSchPara.setField("CHK_FROM_LOC", "N"				);		// 권상위치에 작업예약 존재여부 체크 하지 안도록 SET
						/*
						if (ii==0) {
							recSchPara.setField("CHK_FROM_LOC", "Y");
						} else {
							recSchPara.setField("CHK_FROM_LOC", "N");				//권상위치에 작업예약 존재여부 체크 하지 안도록 SET
						}
						*/

						szMsg    = "[" + szOperationName + "] ----------- 3.2.이적 스케줄기동 START :: " + ii + " >>>> " + arrWBookId[ii];
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.19 recSchPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
						recSchPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
										        	
		    	        EJBConnector ejbConn = new EJBConnector("default", "JPlateYdCrnSchSeEJB", this);
		    	        szRtnMsg = (String)ejbConn.trx("procCrnSchMain", new Class[] { JDTORecord.class }, new Object[] { recSchPara });

						szMsg    = "[" + szOperationName + "] ----------- 3.3.이적 스케줄기동 END :: " + + ii + " >>>> " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		    	        if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {

		    	        	// 2013.07.24 야드 L2 전송이후에는 RollBack 안되도록 보완
		    	        	// 크레인작업지시 1건이상 생성후에는 작업예약 남겨놓고 권하시 크레인 지시 다시 생성
		    	        	if (iCrnSchCnt > 0) {
			    	        	szRtnMsg = "이적 스케줄기동 오류 .. 계속진행 .. <br>" + szRtnMsg;
								szMsg    = "[" + szOperationName + "] " + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		    	        	} else {
			    	        	szRtnMsg = "이적 스케줄기동 오류 .. <br>" + szRtnMsg;
								szMsg    = "[" + szOperationName + "] " + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
								return szRtnMsg;
		    	        	}
		    	        }

		    	        iCrnSchCnt ++;
					}
				}
			}

		} catch(Exception e) {
			//szMsg = szOperationName + " Error : " + e.getMessage();
			//ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			//return  JPlateYdConst.RETN_CD_FAILURE;
			return  e.getMessage();
		}

		szMsg    = "[" + szOperationName + "] ----------- END ";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}  //end of procPrepLotCompByCapa

	/**
	 * 오퍼레이션명 : 재료번호로 작업예약/크레인작업지시 존재여부 체크
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws DAOException
	 */
	public String chkExistSchdule(JDTORecord pRecPara) {

		JPlateYdWrkbookDAO 	ydWrkbookDao 	= new JPlateYdWrkbookDAO();
		JPlateYdCrnSchDAO  	ydCrnSchDao		= new JPlateYdCrnSchDAO();

		// 레코드 선언
		JDTORecord 		recPara 	= JDTORecordFactory.getInstance().create();
		//레코드셋 선언
		JDTORecordSet 	rsResult 	= null;

		String 	szMethodName 		= "chkExistSchdule";
		String 	szOperationName 	= "스케쥴 존재여부 체크";
		String 	szMsg 				= "";
		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
		String	szStlNo				= "";

//---------------------------------------------------------------------------------------------
// 2024.12.17 argument에 logId 없으면 새로 발본
//---------------------------------------------------------------------------------------------
		String logId                        = ydLogUtils.getJDTOLogId(pRecPara, "F");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
		
		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F"); 				// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//---------------------------------------------------------------------------------------------
								
		int 	intRtnVal 			= 0;

		try {

			szStlNo = ydDaoUtils.paraRecChkNull(pRecPara, "STL_NO");

			// ------------------------------------------------------------------------
			// 1. 작업예약 존재여부 확인
			// ------------------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", 		szStlNo);             	// 재료번호
			recPara.setField("YD_GP",		JPlateYdConst.YD_GP_F_PLATE_YARD);
			
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 recPara에 logId 추가
			recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
					        			        																			              	        	
			intRtnVal = ydWrkbookDao.getExistByStlNo(recPara, rsResult);
			if (intRtnVal > 0) {
				szRtnMsg = "해당 재료[" + szStlNo + "]로 작업예약이 존재!";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szRtnMsg;
			}

			// ------------------------------------------------------------------------
			// 2. 크레인 작업지시 존재여부 확인
			// ------------------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", 		szStlNo);             	// 재료번호
			recPara.setField("YD_GP",		JPlateYdConst.YD_GP_F_PLATE_YARD);
			
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 recPara에 logId 추가
			recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
					        			        																			              	        	
			intRtnVal = ydCrnSchDao.getExistByStlNo(recPara, rsResult);
			if (intRtnVal > 0) {
				szRtnMsg = "해당 재료" + szStlNo + "로 크레인 작업지시 존재!";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szRtnMsg;
			}

		} catch (Exception e) {
			szRtnMsg = "스케쥴 존재여부 체크 중 예외발생!";
			szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			//blnRtnVal = false;
			intRtnVal = -100;
			
			return "데이터 스케쥴 존재여부 체크 중 예외발생!" + e.getMessage();
		}
		return JPlateYdConst.RETN_CD_SUCCESS;
	}

	/**
	 * 오퍼레이션명 : 준비스케줄 시 대상재 검색
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws DAOException
	 */
	public String chkPrepLotGpStlList(String szSTL_LIST, String szARR_WLOC_CD, JDTORecordSet rsResult) throws DAOException {
		// 저장품 DAO
		JPlateYdStockDAO ydStockDao = new JPlateYdStockDAO();
		// 레코드 선언
		JDTORecord 		recPara 	= JDTORecordFactory.getInstance().create();
		JDTORecord 		recStock 	= null;
		//레코드셋 선언
		JDTORecordSet 	outRecSet 	= null;

		int 	intRtnVal 			= 0;
		String 	szMethodName 		= "chkPrepLotGpStlList";
		String 	szOperationName 	= "준비스케줄 시 대상재 검색";
		String 	szMsg 				= "";
		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
		String	szYdStkLyrMtlStat	= "";

		String[] strArrStlNo    	= null;

		try {

			strArrStlNo = szSTL_LIST.split(";");

			//적치열,베드에 적치된 해당하는 대상재를 조회
			for (int ii=0; ii<strArrStlNo.length; ii++) {

				outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				recPara.setField("STL_NO", strArrStlNo[ii]);

				intRtnVal = ydStockDao.getYdStockWithLoc(recPara, outRecSet);		// intGp == 110

				//조회결과를 체크
				if (intRtnVal == 0 ) {
					szRtnMsg = "재료번호 = " + strArrStlNo[ii] + "에 대상재가 존재하지 않습니다.";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					return szRtnMsg;
				} else if (intRtnVal == -2 ) {
					szRtnMsg = "재료번호 = " + strArrStlNo[ii] + ", 데이터 조회중 parameter error 발생!";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				} else if (intRtnVal < 0 ) {
					szRtnMsg = "재료번호 = " + strArrStlNo[ii] + "에 대상재를 조회 시 에러가 발생했습니다.";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
				szMsg = "재료번호 = " + strArrStlNo[ii] + "에 대상재가 존재합니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				// 재료적치상태 체크 : YD_STK_LYR_MTL_STAT
				outRecSet.first();
				recStock = outRecSet.getRecord();
				szYdStkLyrMtlStat = ydDaoUtils.paraRecChkNull(recStock, "YD_STK_LYR_MTL_STAT");
				if (!"C".equals(szYdStkLyrMtlStat)) {
					szRtnMsg = "재료번호 [" + strArrStlNo[ii] + "] 의 적치상태가 [" + szYdStkLyrMtlStat + "] 로 작업불가 합니다.";
					return szRtnMsg;
				}

				rsResult.addRecord(recStock);
			//	rsResult.addAll(outRecSet);
			}

			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, rsResult.toString(), JPlateYdConst.DEBUG);

			intRtnVal = rsResult.size();

			// 리턴값 메세지처리
			if (intRtnVal <= 0) {
				szRtnMsg = "이적대상 데이터가 없습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			} else {
				szMsg    = "[" + szOperationName + "] " + intRtnVal + "건의 이적대상 조회 완료";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			}

		} catch (Exception e) {
			szRtnMsg = "데이터 유무체크 및 데이터 반환 중 예외발생!";
			szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			//blnRtnVal = false;
			intRtnVal = -100;
			return "데이터 유무체크 및 데이터 반환 중 예외발생!" + e.getMessage();
		}
		return szRtnMsg;
	} // end of chkPrepLotGpStlList


	/**
	 * 오퍼레이션명 : 작업구분에 따른 스케줄코드 Set
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws DAOException
	 */
	public String getSchCdByWrkGp(String pYD_MAIN_WRK_GP, String pFromYdStrLoc, String pToYdStrLoc, String pTCar, String logId) {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.12.18 getSchCdByWrkGp argument 에 logId 항목 추가 개선
//	public String getSchCdByWrkGp(String pYD_MAIN_WRK_GP, String pFromYdStrLoc, String pToYdStrLoc, String pTCar) {
////////////////////////////////////////////////////////////////////////////////////////

		// pYD_MAIN_WRK_GP  -- 주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차, M:보수장이적
		// pFromYdStrLoc	-- 야드적치열구분 	(FROM위치)
		// pToYdStrLoc		-- 야드적치열구분 	(TO위치)
		// pTCar			-- 대차설비번호   	(FXTC01, FXTC02)

		// 이적은 FRom위치 기준으로 스케줄코드가 만들어 지고
		// 보급, Book-In은 To위치 기준으로 만들어짐

		String 	szYD_SCH_CD		= "";
		String	szMsg			= "";
		String	szOperationName	= "스케줄코드 Set";
		String	szMethodName	= "getSchCdByWrkGp";
		
//---------------------------------------------------------------------------------------------
// 2024.11.18 argument에 logId 없으면 새로 발본
//---------------------------------------------------------------------------------------------
		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F"); // log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본

		szMsg    = "[" + szOperationName + "] >>>> 파라미터 >>>> pYD_MAIN_WRK_GP:" + pYD_MAIN_WRK_GP + ", pFromYdStrLoc: " + pFromYdStrLoc
		         + ", pToYdStrLoc::"+pToYdStrLoc + ", pTCar::" + pTCar;
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		// 대차작업이면 대차 상차 스케쥴코드 SET
		if (!"".equals(pTCar)) {

			szYD_SCH_CD	= ydUtils.substr(pFromYdStrLoc, 0, 2) + ydUtils.substr(pTCar, 2, 4) + "UM";			// 대차상차

		//	이적일 경우
		} else if (JPlateYdConst.YD_MAIN_WRK_GP_MV.equals(pYD_MAIN_WRK_GP)) {

			// From위치 기준으로 스케쥴코드 생성하도록 변경
			// 대차일경우
			if ("TC".equals(ydUtils.substr(pFromYdStrLoc, 2, 2))) {
				szYD_SCH_CD	= ydUtils.substr(pFromYdStrLoc, 0, 6) + "LM";									// 대차하차
			} else {
				szYD_SCH_CD = ydUtils.substr(pFromYdStrLoc, 0, 2) + "YD" + ydUtils.substr(pFromYdStrLoc, 2, 2) + "MM";
			}

		} else {

			// BOOK-IN일 경우
			if (JPlateYdConst.YD_MAIN_WRK_GP_RT_IN.equals(pYD_MAIN_WRK_GP)) {								// RT BOOK-IN일 경우

			//	szYD_SCH_CD = ydUtils.substr(pToYdStrLoc, 0, 2) + "RT" + "00UM";
				szYD_SCH_CD = ydUtils.getRtSchCd(pToYdStrLoc, "UM");

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_GAS_IN.equals(pYD_MAIN_WRK_GP)) {					// GAS장보급일 경우 (BOOK-IN)

				szYD_SCH_CD = ydUtils.substr(pToYdStrLoc, 0, 2) + "CN" + ydUtils.substr(pToYdStrLoc, 4, 1) + "0UM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_BS_IN.equals(pYD_MAIN_WRK_GP)) {						// 보수장보급일 경우 (BOOK-IN)

				szYD_SCH_CD = ydUtils.substr(pToYdStrLoc, 0, 2) + "BS" + "00UM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_TOD_IN.equals(pYD_MAIN_WRK_GP)) {					// TOD보급일 경우 (BOOK-IN)

				szYD_SCH_CD = ydUtils.substr(pToYdStrLoc, 0, 2) + "TD" + "00UM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_PT_IN.equals(pYD_MAIN_WRK_GP)) {						// 이송차량 상차일 경우 [FCPT01UM]

				szYD_SCH_CD = ydUtils.substr(pToYdStrLoc, 0, 2) + "PT" + ydUtils.substr(pToYdStrLoc, 4, 2) + "UM";

			// BOOK-IN일 경우
			} else if (JPlateYdConst.YD_MAIN_WRK_GP_RT_OUT.equals(pYD_MAIN_WRK_GP)) {					// RT BOOK-OUT일 경우

			//	szYD_SCH_CD = ydUtils.substr(pFromYdStrLoc, 0, 2) + "RT" + "00LM";
				szYD_SCH_CD = ydUtils.getRtSchCd(pFromYdStrLoc, "LM");

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_GAS_OUT.equals(pYD_MAIN_WRK_GP)) {					// GAS장추출일 경우 (BOOK-OUT)

				szYD_SCH_CD = ydUtils.substr(pFromYdStrLoc, 0, 2) + "CN" + ydUtils.substr(pFromYdStrLoc, 4, 1) + "0LM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_BS_OUT.equals(pYD_MAIN_WRK_GP)) {					// 보수장추출일 경우 (BOOK-OUT)

				szYD_SCH_CD = ydUtils.substr(pFromYdStrLoc, 0, 2) + "BS" + "00LM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_TOD_OUT.equals(pYD_MAIN_WRK_GP)) {					// TOD추출일 경우 (BOOK-OUT)

				szYD_SCH_CD = ydUtils.substr(pFromYdStrLoc, 0, 2) + "TD" + "00LM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_PT_OUT.equals(pYD_MAIN_WRK_GP)) {					// 이송차량 하차일 경우 [FCPT01LM]

				szYD_SCH_CD = ydUtils.substr(pFromYdStrLoc, 0, 2) + "PT" + ydUtils.substr(pFromYdStrLoc, 4, 2) + "LM";
			}
		}

		szMsg    = "[" + szOperationName + "] >>>> 스케쥴코드 >>>> " + szYD_SCH_CD;
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return szYD_SCH_CD;
	} // end of getSchCdByWrkGp

	//---------------------------------------------------------------------------
	
	/**********************************************************
	* 1후판정정추가 SJH16
	**********************************************************/	

	/**
	 * 오퍼레이션명 : 1후판 준비스케줄 LOT편성
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public String procPrepLotCompByCapaYdP(JDTORecord msgRecord) throws DAOException {

		// DAO 선언
		JPlateYdWrkbookDAO 		ydWrkbookDao 	= new JPlateYdWrkbookDAO();				// 작업예약 DAO
		JPlateYdWrkbookMtlDAO 	ydWrkbookMtlDao = new JPlateYdWrkbookMtlDAO();			// 작업예약 재료 DAO
		JPlateYdStockDAO 		ydStockDao 		= new JPlateYdStockDAO();				// 저장품DAO
		JPlateYdSchRuleDAO		ydSchRuleDao	= new JPlateYdSchRuleDAO();				// 스케줄기준DAO
		JPlateYdEqpDAO			ydEqpDao		= new JPlateYdEqpDAO();					// 야드설비DAO

		//레코드 선언
		JDTORecord 		recPara     = null;
		JDTORecord 		recOutPara  = null;
		JDTORecord 		recSchPara  = null;
		JDTORecord 		recResult 	= null;
		JDTORecord 		recTemp 	= null;
		//레코드셋 선언
		JDTORecordSet 	rsResult 	= null;
		JDTORecordSet 	rsTemp 		= null;
		JDTORecordSet 	rsSchRule 	= JDTORecordFactory.getInstance().createRecordSet("");

		//리턴값(boolean)
		int 	intRtnVal 			= 0;

		String 	szMsg           	= "";
		String 	szMethodName    	= "procPrepLotCompByCapaYdP";
		String 	szOperationName 	= "준비스케줄 LOT 편성";

		int 	intLotGpSh      	= 0;								// Lot 편성 재료 매수
		String 	szYD_GP				= null;								// 야드구분
		String 	szYD_BAY_GP			= null;								// 동구분
		String 	szYD_AIM_YD_GP		= null;								// 목표야드구분
		String 	szYD_AIM_BAY_GP		= null;								// 목표동구분
		String 	szYD_AIM_SPAN_GP	= null;								// 목표스판구분
		String 	szYD_AIM_COL_GP		= null;								// 목표적치열구분
		String 	szYD_AIM_BED_NO		= null;								// 목표적치BED구분
		String 	szYD_MAIN_WRK_GP 	= null;								// 주작업이적구분
		String 	szYD_TO_LOC_GUIDE_GP= null;								// TO위치가이드구분
		String 	szYD_SCH_CD         = "";								// 스케줄코드
		String 	szPREV_YD_SCH_CD    = "";								// 스케줄코드
		String 	szSTL_NO            = null;								// 재료번호
		String[] arrYD_STK_COL_GP 	= null;								// 적치열배열
		String[] arrYD_STK_BED_NO 	= null;								// 적치베드 배열
		String 	szTCAR 				= "";								// 대차
		String 	szPREV_TCAR 		= "";								// 대차
		String 	szYD_STK_COL_GP 	= "";								// 적치열구분
		String 	szYD_STK_COL_GP_CMP = "XX01";							// 이전 적치열구분 - 비교를 위해서 임의의 값을 설정
		String 	szYD_TO_LOC_DCSN_MTD= "";								// 야드 TO위치결정방법
		String 	szYD_TO_LOC_GUIDE 	= "";								// 야드TO위치가이드
		String 	szSTL_LIST 			= "";								// 야드TO위치가이드
		String	szREGISTER			= "";								// 등록자
		String	szYD_CURR_BAY_GP	= "";								// 대차현재동
		String	szYD_EQP_NAME		= "";								// 대차설비명
		String	szCrnschSkipFlag	= "";								// 크레인스케줄 호출 SKIP 여부

		double 	dblCurrWidth		= 0;								// 재료의 현재 폭
		long 	lngCurrWt			= 0;								// 재료의 현재 중량
		long 	lngSumWt 			= 0;								// 누적중량
		int 	intMtlSh			= 0;								// 크레인작업가능매수
		double 	dblMaxWidth			= 0;								// 크레인작업가능 폭
		boolean bIsInsideMv			= true;								// 동내이적인 지 동간이적인 지 판단하는 변수
		String 	szYD_TC_GP			= null;
		String 	szYD_EQP_WRK_SH		= null;								// 작업매수
		int 	intYD_EQP_WRK_SH	= -1;								// 작업매수
		String 	szARR_WLOC_CD		= null;
		String	szYD_WBOOK_ID		= null;								// 작업예약ID
		String	szYD_WRK_CRN		= null;								// 크레인설비ID
		boolean bookInFlag 			= false;							// BOOK-IN 여부
		String 	ydWrkCrnPrior 		= "1"; 								// 야드작업크레인우선순위
		int		insWrkMtlCnt 		= 0;
		int		iWBookInsCnt		= 0;								// 작업예약등록 건수
		int		iCrnSchCnt			= 0;								// 크레인스케쥴 호출 건수
		
		String 	szCARD_NO			= null;								// CARD_NO --> L3화면에서 지시 작성여부 ('L3'면 L3화면에서 지시생성)

		StringBuffer sbARR_WBOOK_ID = new StringBuffer();

		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;	// 리턴 메세지 정의

//---------------------------------------------------------------------------------------------
// 2024.11.14 argument에 logId 없으면 새로 발본
//---------------------------------------------------------------------------------------------
		String logId                        = ydLogUtils.getJDTOLogId(msgRecord, "P");  // JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
		
		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P"); 				// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//---------------------------------------------------------------------------------------------
		
		// TC CODE 추출
		String szRcvTcCode 			= ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {
			szRtnMsg = "TC Code Error (" + szRcvTcCode + ")";
			szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		}

		// TC CODE DISPLAY
		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		}

		
		try {
			szMsg    = "[" + szOperationName + "] ----------- START :: " + msgRecord.toString();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");
			
			//----------------------------
			// 1. 입력 파라미터 정합성 체크
			//----------------------------
			/*
			 * 야드구분, 동구분, 스판(선택), 목표행선구분, 목표동, 주작업이적구분, TO위치가이드구분
			 */
			
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
			if ("".equals(szYD_GP)) {

				szRtnMsg = "[전문 이상] 야드구분이 없습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;

			}
			
			// 동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
			if ("".equals(szYD_BAY_GP)) {
				szRtnMsg = "[전문 이상] 동구분이 없습니다.";
				szMsg    = "[" + szOperationName +"] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			// 적치열구분  
			arrYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP").split(";");
			if (arrYD_STK_COL_GP == null || arrYD_STK_COL_GP.length == 0) {
				szRtnMsg = "[전문 이상] 적치열구분이 없습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			// 적치베드구분 
			arrYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO").split(";");
			if (arrYD_STK_BED_NO == null || arrYD_STK_BED_NO.length == 0) {
				szRtnMsg = "[전문 이상] 적치베드번호가 없습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			// 주작업이적구분
			szYD_MAIN_WRK_GP  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MAIN_WRK_GP");

			szMsg = "작업재료확인 : szYD_MAIN_WRK_GP " + szYD_MAIN_WRK_GP ;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			
			if ("".equals(szYD_MAIN_WRK_GP)) {
				// 1:이적 , 	2:RT BOOK-IN ,  3:GAS장 보급 , 4:보수장 보급, 5:TOD보급
				//			6:RT BOOK-OUT , 7:GAS장 추출 , 8:보수장 추출, 9:TOD추출
				//          A:이송상차,      	B:이송하차
				szRtnMsg = "[전문 이상] 주작업이적구분이 없습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}
			
			// TO위치가이드구분
			szYD_TO_LOC_GUIDE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_LOC_GUIDE_GP");
			if ("".equals(szYD_TO_LOC_GUIDE_GP)) {
				szRtnMsg = "[전문 이상] TO위치가이드구분이 없습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;

			} else {

				// 목표야드구분
				szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_YD_GP");
				if ("".equals(szYD_AIM_YD_GP)) {
					szRtnMsg = "[전문 이상] 목표야드구분이 없습니다.";
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
				// 목표동구분
				szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP");
				if ("".equals(szYD_AIM_BAY_GP)) {
					szRtnMsg = "[전문 이상] 목표동구분이 없습니다.";
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}

				// To위치 Guide 일때
				if ("Y".equals(szYD_TO_LOC_GUIDE_GP)) {
					szYD_AIM_SPAN_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_SPAN_GP");		// 목표스판구분
					szYD_AIM_COL_GP  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_COL_GP");		// 목표적치열구분
					szYD_AIM_BED_NO  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BED_NO");		// 목표적치BED구분
				}
			}

			szREGISTER = ydDaoUtils.paraRecModifier(msgRecord);										// 등록자, 수정자

			szCrnschSkipFlag = ydDaoUtils.paraRecChkNull(msgRecord, "CRNSCH_SKIP_FLAG", "N");		// 크레인스케줄 SKIP 여부 : 'Y'일때 SKIP

			// BOOK-IN, GAS장보급, 보수장보급일경우  작업예약 ID를 건수만큼 생성
			if (   JPlateYdConst.YD_MAIN_WRK_GP_RT_IN.equals(szYD_MAIN_WRK_GP) 			// RT	Book-In
				|| JPlateYdConst.YD_MAIN_WRK_GP_GAS_IN.equals(szYD_MAIN_WRK_GP) 			// GAS장	Book-In (보급)
				|| JPlateYdConst.YD_MAIN_WRK_GP_BC_IN.equals(szYD_MAIN_WRK_GP)			// 임가공 절단장	Book-In (보급)
				|| JPlateYdConst.YD_MAIN_WRK_GP_BS_IN.equals(szYD_MAIN_WRK_GP) ) {		// 보수장	Book-In (보급)

				bookInFlag = true;
			}

			//---------------------------------------------------------------------------------------------
			//	준비스케줄 등록 시 지정한 작업매수의 값을 전달하여 해당하는 매수만큼 작업예약에 등록
			//---------------------------------------------------------------------------------------------
			szYD_EQP_WRK_SH = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_SH");
			if (!"".equals(szYD_EQP_WRK_SH)) {
				intYD_EQP_WRK_SH = Integer.parseInt(szYD_EQP_WRK_SH);
				szMsg = "사용자가 지정한 작업매수 : " + intYD_EQP_WRK_SH;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			}
			
			szARR_WLOC_CD 	= ydDaoUtils.paraRecChkNull(msgRecord, "ARR_WLOC_CD");		// 착지개소코드
			szYD_TC_GP 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_TC_GP");			// 대차선택
			szYD_AIM_YD_GP 	= szYD_GP;													// 목표야드구분
			szSTL_LIST 		= ydDaoUtils.paraRecChkNull(msgRecord, "STL_LIST");			// 작업재료리스트

			// 2013.09.06 대차 작업일경우 대차의 현재동 체크 : 현재동에 대차가 없으면 오류로 처리
			if (!"".equals(szYD_TC_GP)) {
				rsResult = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
				recTemp  = JDTORecordFactory.getInstance().create();
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_EQP_ID", szYD_TC_GP); 				// 대차설비ID

				intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult);
				if (intRtnVal <= 0) {
					szRtnMsg = "해당 대차가 존재하지 않습니다." + szYD_TC_GP;
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
				rsResult.first();
				recTemp = rsResult.getRecord();

				szYD_CURR_BAY_GP = ydDaoUtils.paraRecChkNull(recTemp, "YD_CURR_BAY_GP");
				szYD_EQP_NAME	 = ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_NAME");

				if (!szYD_CURR_BAY_GP.equals(szYD_BAY_GP)) {
					szRtnMsg = "[" + szYD_EQP_NAME + "]가 현재 [" + szYD_CURR_BAY_GP + "] 동에 있어 작업할수 없습니다.";
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");


			// 작업재료LIST 검색
//---------------------------------------------------------------------------------------------
// 2024.11.14 Argument에 logId 추가 
//			szRtnMsg = chkPrepLotGpStlListYdP(szYD_GP,szSTL_LIST, szARR_WLOC_CD, rsResult);
			szRtnMsg = chkPrepLotGpStlListYdP(szYD_GP, szSTL_LIST, szARR_WLOC_CD, rsResult, logId);
//---------------------------------------------------------------------------------------------

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				return szRtnMsg;
			}

			intLotGpSh = rsResult.size();
			rsResult.first();

			
			Vector rsGroup = new Vector();

			// 동일한 스판별로 대상재를 편성한다.
			// 보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호  데이터를 세팅한다.
			for (int ii = 1; ii<=intLotGpSh; ii++) {

				// 레코드 추출
				recPara = rsResult.getRecord();

				szSTL_NO 		= ydDaoUtils.paraRecChkNull(recPara, 		"STL_NO"		);	// 재료번호
				lngCurrWt 		= ydDaoUtils.paraRecChkNullLong(recPara, 	"YD_MTL_WT"		);	// 야드재료중량
				dblCurrWidth 	= ydDaoUtils.paraRecChkNullDouble(recPara, 	"YD_MTL_W"		);	// 야드재료폭
				szYD_STK_COL_GP	= ydDaoUtils.paraRecChkNull(recPara, 		"YD_STK_COL_GP"	);	// 적치열구분

				szMsg = "작업재료확인 : 현재Count[" + ii + "] 현재 재료번호[" + szSTL_NO + "]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				//---------------------------------------------------------------------------------------------
				// 2024.11.14 recPara에 logId 추가 
				//---------------------------------------------------------------------------------------------
				recPara.setField("LOG_ID", logId);                                         
				
				szRtnMsg = this.chkExistSchduleYdP(recPara);
				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {

					szMsg = "재료번호[" + szSTL_NO + "] 로 작업예약/크레인스케쥴 존재여부 체크 .. 실패 >>>>" + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					return szRtnMsg;
				}

				
				/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				 * 스케줄코드, 대차설비 ID 구하기
				 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				szYD_TO_LOC_DCSN_MTD 	= "";		// 야드 TO위치결정방법
				szYD_TO_LOC_GUIDE 		= "";		// 야드TO위치가이드
				szTCAR 					= "";		// 대차

				// TO위치GUIDE가 존재하고 이고 목표동이 다른 경우에는 대차상차스케줄을 생성하고 대차 배정
				if ("Y".equals(szYD_TO_LOC_GUIDE_GP)) {				// TO위치GUIDE 이적인 경우
					szYD_TO_LOC_DCSN_MTD = "F";
					szYD_TO_LOC_GUIDE	 = szYD_AIM_YD_GP + szYD_AIM_BAY_GP + szYD_AIM_SPAN_GP + szYD_AIM_COL_GP + szYD_AIM_BED_NO;

					szMsg = "TO위치GUIDE 이적인 경우 szYD_TO_LOC_GUIDE : " + szYD_TO_LOC_GUIDE;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					if (szYD_BAY_GP.equals(szYD_AIM_BAY_GP))	{	//목표동이 같은 경우 - 동내이적 스케줄
						bIsInsideMv = true;
						//szYD_SCH_CD = getSchCdByWrkGpYdP(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, "");
//---------------------------------------------------------------------------------------------
// 2024.11.14 Argument에 logId 추가 
//						szYD_SCH_CD = getSchCdByWrkGpYdP(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, szYD_TC_GP);
						szYD_SCH_CD = getSchCdByWrkGpYdP(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, szYD_TC_GP, logId);
//---------------------------------------------------------------------------------------------
						
						szMsg = ">> 목표동이 같은 경우 - 동내이적 스케줄 : " + szYD_SCH_CD + ", " + szTCAR;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					} else {											//목표동이 다른 경우 - 대차상차스케줄
						bIsInsideMv = false;
						szTCAR = szYD_TC_GP;
						if ("".equals(szTCAR)) {
							String[] retValue = JPlateYdCommonUtils.getSchCdNTcar(szYD_STK_COL_GP);
							szTCAR = retValue[1];
						}
//---------------------------------------------------------------------------------------------
// 2024.11.14 Argument에 logId 추가 
//						szYD_SCH_CD = getSchCdByWrkGpYdP(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, szTCAR);
						szYD_SCH_CD = getSchCdByWrkGpYdP(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, szTCAR, logId);
//---------------------------------------------------------------------------------------------

						szMsg = ">>>> 목표동이 다른 경우 - 대차상차스케줄[동간이적] : " + szYD_SCH_CD + ", " + szTCAR;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					}
				} else {			// 위치검색기준으로 이적인 경우
					szYD_TO_LOC_GUIDE 	= szYD_AIM_YD_GP + szYD_AIM_BAY_GP;

					szMsg = "위치검색기준으로 이적인 경우 >>>> szYD_TO_LOC_GUIDE :: " + szYD_TO_LOC_GUIDE;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					if (szYD_BAY_GP.equals(szYD_AIM_BAY_GP)) {		// 목표동이 같은 경우 - 동내이적 스케줄
						bIsInsideMv = true;
						//szYD_SCH_CD = getSchCdByWrkGpYdP(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, "");
//---------------------------------------------------------------------------------------------
// 2024.11.14 Argument에 logId 추가 
//						szYD_SCH_CD = getSchCdByWrkGpYdP(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, szYD_TC_GP);
						szYD_SCH_CD = getSchCdByWrkGpYdP(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, szYD_TC_GP, logId);
//---------------------------------------------------------------------------------------------
						szMsg = "목표동이 같은 경우 - 동내이적 스케줄 : " + szYD_SCH_CD + ", " + szTCAR;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					} else {											//목표동이 다른 경우 - 대차상차스케줄
						bIsInsideMv = false;
						szTCAR = szYD_TC_GP;
						if ("".equals(szTCAR)) {
							String[] retValue = JPlateYdCommonUtils.getSchCdNTcar(szYD_STK_COL_GP);
							szTCAR = retValue[1];
						}
//---------------------------------------------------------------------------------------------
// 2024.11.14 Argument에 logId 추가 
//						szYD_SCH_CD = getSchCdByWrkGpYdP(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, szTCAR);
						szYD_SCH_CD = getSchCdByWrkGpYdP(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, szTCAR, logId);
//---------------------------------------------------------------------------------------------

						szMsg = "위치검색기준 .. 목표동이 다른 경우 - 대차상차스케줄[동간이적] : " + szYD_SCH_CD + ", " + szTCAR;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					}
				}

				szMsg = "[" + szOperationName + "] >>>>> TO위치결정방법[" + szYD_TO_LOC_DCSN_MTD + "], TO위치GUIDE[" + szYD_TO_LOC_GUIDE + "]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				szMsg = "[" + szOperationName + "] >>>>> 재료번호 [" + szSTL_NO + "] : 야드재료중량[" + lngCurrWt + "], 야드재료폭[" + dblCurrWidth + "], 적치열구분[" + szYD_STK_COL_GP + "], 목표동[" + szYD_AIM_BAY_GP + "]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				/*
				 * 스케줄기준과 크레인 할당
				 */
				// 2021.11.08 2후판정정은 기등록된 스케줄코드 기준이 없어서 여기서 조회를 못해온다. FC0205->FCPT05LM: 이스케줄코드가 없음.
				// 참고로 1후판은 각 동별로 스케줄코드 기준 다 있음..
				if (!szPREV_YD_SCH_CD.equals(szYD_SCH_CD)) {
					szMsg = "[" + szOperationName + "] 스케줄코드비교 >>>>> YD_SCH_CD[" + szYD_SCH_CD + "], PREV_YD_SCH_CD[" + szPREV_YD_SCH_CD + "]";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					recResult = JDTORecordFactory.getInstance().create();
					szRtnMsg = JPlateYdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recResult);
					if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
						return szRtnMsg;
					}
					// 크레인ID
					szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recResult, "YD_WRK_CRN");
				}
				
				// 대차적치가능 중량 구하기
				if (!szPREV_TCAR.equals(szTCAR)) {
					szMsg = "[" + szOperationName + "] 대차설비비교 >>>>> TCAR[" + szTCAR + "], PREV_TCAR[" + szPREV_TCAR + "]";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				}

				

				
				// 동일한 스판이 아닌 경우
				if (!szYD_STK_COL_GP.substring(0, 4).equals(szYD_STK_COL_GP_CMP.substring(0, 4))) {
					rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
					rsGroup.add(rsTemp);
					dblMaxWidth = dblCurrWidth;
					lngSumWt = lngCurrWt;
					intMtlSh = 1;
					szMsg = "[" + szOperationName + "] 동내이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					szMsg = "[" + szOperationName + "] 스판비교 >>>>> YD_STK_COL_GP[" + szYD_STK_COL_GP + "], PREV_YD_STK_COL_GP[" + szYD_STK_COL_GP_CMP + "]";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				} else if (bIsInsideMv) {		// 동내이적

					if (bookInFlag) {
						szMsg = "[" + szOperationName + "] 동내이적 - BOOK-IN : ";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
						rsGroup.add(rsTemp);

					} else {
						/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
						 * 업무기준 : 주작업인 경우
						 * 		1. 작업재료를 동내이적 대상재는 크레인 작업능력만큼 LOT편성
						 * 		2. 작업재료를 동간이적 대상재는 대차 작업능력만큼 LOT편성
						 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
						szMsg = "[" + szOperationName + "] 동내이적 - bIsInsideMv : " + bIsInsideMv;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						// 주작업인 경우에만.
						lngSumWt += lngCurrWt;
						intMtlSh++;
						szMsg = "[" + szOperationName + "] 동내이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						// 크레인작업가능능력 체크
						intRtnVal = JPlateYdCommonUtils.chkGetCrnspec(dblCurrWidth, dblMaxWidth, lngSumWt, intMtlSh, recResult);
						szMsg = "[" + szOperationName + "] 동내이적(크레인작업가능능력 체크 : intRtnVal >> "+ intRtnVal + ")";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					//	if (intRtnVal == -1 || intRtnVal == -2 || intRtnVal == -3) {
						rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
						rsGroup.add(rsTemp);
						dblMaxWidth = dblCurrWidth;
						lngSumWt = lngCurrWt;
						intMtlSh = 1;
						szMsg = "[" + szOperationName + "] 동내이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					//		szMsg = "[" + szOperationName + "] 크레인작업가능능력 초과";
					//		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					//	}

					}

					if (dblMaxWidth < dblCurrWidth) {
						dblMaxWidth = dblCurrWidth;
					}

				} else {							// 동간이적

					szMsg = "[" + szOperationName + "] 동간이적(대차적치능력 - 크레인작업가능능력으로 체크) - bIsInsideMv : " + bIsInsideMv;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					lngSumWt += lngCurrWt;
					intMtlSh++;
					szMsg = "[" + szOperationName + "] 동간이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					// 크레인작업가능능력 체크
					intRtnVal = JPlateYdCommonUtils.chkGetCrnspec(dblCurrWidth, dblMaxWidth, lngSumWt, intMtlSh, recResult);
					szMsg = "[" + szOperationName + "] 동간이적(대차적치능력 - 크레인작업가능능력으로 체크 : intRtnVal "+ intRtnVal + ")";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					rsTemp = JDTORecordFactory.getInstance().createRecordSet("yd");
					rsGroup.add(rsTemp);
					dblMaxWidth = dblCurrWidth;
					lngSumWt = lngCurrWt;
					intMtlSh = 1;
					szMsg = "[" + szOperationName + "] 동간이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);


					if (dblMaxWidth < dblCurrWidth) {
						dblMaxWidth = dblCurrWidth;
					}
				}

				rsTemp.addRecord(recPara);
				szYD_STK_COL_GP_CMP = szYD_STK_COL_GP;

				// 다음 레코드로
				rsResult.next();

				szPREV_YD_SCH_CD 	= szYD_SCH_CD;			// 스케줄코드
				szPREV_TCAR 		= szTCAR;				// 대차설비ID

				//---------------------------------------------------------------------------------------------
				//	후판정정의 준비스케줄 등록 시 지정한 작업매수만큼 처리된 후에는 루프 종료
				//---------------------------------------------------------------------------------------------
				if (!"".equals(szYD_EQP_WRK_SH)) {
					if (ii > intYD_EQP_WRK_SH)	{
						szMsg = "사용자가 지정한 작업매수[" + intYD_EQP_WRK_SH + "]와 같으므로 루프 종료 - 반복변수값["+ii+"]";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						break;
					}
				}
			} // end for


			
			szMsg = "[" + szOperationName + "] 대상재 작업예약 그룹 갯수 : " + rsGroup.size();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//----------------------------
			// 2. 작업예약등록
			//----------------------------
			szMsg = "[" + szOperationName + "] 작업예약등록 시작 ";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			
			for(int ii = 0 ; ii < rsGroup.size(); ii++) {
				rsTemp = (JDTORecordSet)rsGroup.get(ii);

				
				for(int jj = 0; jj < rsTemp.size(); jj++) {
					rsTemp.absolute(jj + 1);
					recOutPara = rsTemp.getRecord();

					if (jj==0 || bookInFlag) {							// 첫번째거나  Book-In일 경우는 작업예약 ID를 매번 생성
			//		if ((ii==0 && jj==0) || bookInFlag) {				// 첫번째거나  Book-In일 경우는 작업예약 ID를 매번 생성
						//----------------------------
						// 2.1. 작업예약ID SELECT
						//----------------------------
						szYD_WBOOK_ID = ydWrkbookDao.getSeqId();

						szMsg = "[" + szOperationName + "] >>>> 작업예약ID SELECT >>>> (" + ii + "," + jj + ")" + szYD_WBOOK_ID;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						if (iWBookInsCnt > 0) {
							sbARR_WBOOK_ID.append(";");
						}
						sbARR_WBOOK_ID.append(szYD_WBOOK_ID);
						iWBookInsCnt ++;

						//----------------------------
						// 2.2. 스케줄기준 정보 조회
						//----------------------------
						ydWrkCrnPrior = "1"; 							// 야드작업크레인우선순위

						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_SCH_CD", szYD_SCH_CD); 	// 야드스케쥴코드

						szMsg = "[" + szOperationName + "] 야드스케쥴기준 조회 START";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						intRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsSchRule);

						szMsg = "[" + szOperationName + "] 야드스케쥴기준 조회 End :: " + Integer.toString(intRtnVal);
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						if (intRtnVal == 1) {
							rsSchRule.first();
							recPara = rsSchRule.getRecord();
							ydWrkCrnPrior = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");

							szMsg = "[" + szOperationName + "] 야드스케쥴우선순위 :: " + ydWrkCrnPrior;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						} else {
							szMsg = "[" + szOperationName + "] 스케줄코드["+szYD_SCH_CD+"]로 스케줄기준 조회 시 오류발생 ::"+Integer.toString(intRtnVal);
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						}

						
						//----------------------------
						// 2.3 작업예약 INSERT
						//----------------------------
						if ("Y".equals(szYD_TO_LOC_GUIDE_GP)) {
							szYD_AIM_YD_GP  = szYD_TO_LOC_GUIDE.substring(0, 1);
							szYD_AIM_BAY_GP = szYD_TO_LOC_GUIDE.substring(1, 2);
						}

						// 작업예약 레코드 생성
						recOutPara.setField("YD_LOT_GP_SH",       	Integer.toString(rsTemp.size())	);	// Lot편성 매수
						recOutPara.setField("YD_GP",      			szYD_GP							);	// 야드구분
						recOutPara.setField("YD_BAY_GP",      		szYD_BAY_GP						);	// 동구분
						recOutPara.setField("YD_AIM_YD_GP",      	szYD_AIM_YD_GP					);	// 목표야드구분
						recOutPara.setField("YD_AIM_BAY_GP",      	szYD_AIM_BAY_GP					);	// 목표동구분
						//-----------------------------------------------------------------------------------------------------------
						//	대차상차스케줄인 경우에는
						//	1. 동까지만 선택하는 경우에 대차하차스케줄의 TO위치결정 시는 위치검색베드 적용
						//	2. 베드까지 선택하는 경우에 대차하차스케줄의 TO위치결정 시는 사용자지정위치로 적용
						//-----------------------------------------------------------------------------------------------------------
						//if ("F".equals(szYD_TO_LOC_DCSN_MTD) && szYD_TO_LOC_GUIDE.length() == 8) {
						//	recOutPara.setField("YD_TO_LOC_DCSN_MTD",	szYD_TO_LOC_DCSN_MTD);			//야드 TO위치결정방법
						//	recOutPara.setField("YD_TO_LOC_GUIDE",     	szYD_TO_LOC_GUIDE);				//야드TO위치가이드
						//}
						recOutPara.setField("YD_TO_LOC_DCSN_MTD",	szYD_TO_LOC_DCSN_MTD			);	// 야드 TO위치결정방법
						recOutPara.setField("YD_TO_LOC_GUIDE",     	szYD_TO_LOC_GUIDE				);	// 야드TO위치가이드
						recOutPara.setField("YD_WRK_PLAN_TCAR",     szTCAR							);	// 작업계획대차
						recOutPara.setField("YD_WBOOK_ID",      	szYD_WBOOK_ID					);	// 작업예약ID
						recOutPara.setField("REGISTER", 			szREGISTER						);
						recOutPara.setField("MODIFIER", 			szREGISTER						);
						recOutPara.setField("YD_SCH_CD", 			szYD_SCH_CD						);	// 야드스케쥴코드
						recOutPara.setField("YD_SCH_PRIOR", 		ydWrkCrnPrior					);	// 야드스케쥴우선순위
						recOutPara.setField("YD_SCH_PROG_STAT", 	"W"								);	// 야드스케쥴진행상태(스케줄수행대기)
						recOutPara.setField("YD_SCH_ST_GP", 		"M"								);	// 야드스케쥴기동구분 (A:Auto,B:BackUp,M:Manual)
						recOutPara.setField("YD_SCH_REQ_GP", 		"X"								);	// 야드스케쥴요청구분
						
						recOutPara.setField("CARD_NO", 				szCARD_NO						);	// CARD_NO --> L3화면에서 지시 작성여부 ('L3'면 L3화면에서 지시생성)

						szMsg = "[" + szOperationName + "] 2.3.작업예약 Insert :: ";	// + recOutPara.toString();
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						// 작업예약 Insert
						intRtnVal = ydWrkbookDao.insYdWrkbook(recOutPara);
						if (intRtnVal < 1) {
							szRtnMsg = "작업예약 데이터 등록 중 에러 .." + Integer.toString(intRtnVal);
							szMsg    = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							return szRtnMsg;
						}
					} else {
						recOutPara.setField("YD_WBOOK_ID",      	szYD_WBOOK_ID	);			// 작업예약ID
						recOutPara.setField("REGISTER", 			szREGISTER		);
						recOutPara.setField("MODIFIER", 			szREGISTER		);
					}

					szSTL_NO = ydDaoUtils.paraRecChkNull(recOutPara, "STL_NO"		);			// 재료번호

					//----------------------------
					// 2.4. 작업예약재료 INSERT
					//----------------------------
					szMsg = "[" + szOperationName + "] 2.4.작업예약재료 Insert :: ";	// + recOutPara.toString();
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					intRtnVal = ydWrkbookMtlDao.insYdWrkbookMtl(recOutPara);
					if (intRtnVal < 1) {
						szRtnMsg = "작업예약재료 데이터 ["+szSTL_NO+"] 등록 중 에러 .. " + Integer.toString(intRtnVal);
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
					}

					insWrkMtlCnt ++;

					//----------------------------------
					// 2.5. 저장품에 작업예약ID를 UPDATE
					//----------------------------------
					recOutPara.setField("STL_NO", 		szSTL_NO		);
					recOutPara.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID	);
					recOutPara.setField("YD_SCH_CD", 	szYD_SCH_CD		);

					szMsg = "[" + szOperationName + "] 2.5.저장품에 작업예약ID를 UPDATE ";	// + recOutPara.toString();
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					intRtnVal = ydStockDao.updYdStockWbook(recOutPara);
					if (intRtnVal < 1) {
						szRtnMsg = "저장품 데이터 ["+szSTL_NO+"] 수정 중 에러 .. " + Integer.toString(intRtnVal);
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
					}

					//----------------------------------
					// 2.6. 야드적치단 활성상태 UPDATE
					//----------------------------------
					//적치단 야드적치단재료상태 수정
					/*
					recOutPara.setField("YD_STK_LYR_MTL_STAT", "U"); 				//권상대기

					szMsg = "[" + szOperationName + "] 2.6.야드적치단 활성상태 UPDATE ";	// + recOutPara.toString();
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					intRtnVal = ydStkLyrDao.updYdStklyrStat(recOutPara);
					if (intRtnVal <= 0) {
						szRtnMsg = "야드적치단재료상태 수정 ERROR .. " + Integer.toString(intRtnVal);
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
					*/
				}//End Loop jj
			}//End Loop ii

			if ("Y".equals(szCrnschSkipFlag)) {

				szMsg = "[" + szOperationName + "] ----------- 작업예약만 등록하고 크레인 스케줄 호출은 SKIP함 >>>>";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			} else {
				iCrnSchCnt = 0;
				if (insWrkMtlCnt > 0) {

					String[] arrWBookId = sbARR_WBOOK_ID.toString().split(";");

					szMsg = "[" + szOperationName + "] ----------- 3.1.이적 스케줄기동 START .... 재료건수 :: " + insWrkMtlCnt + " 작업예약ID :: " + sbARR_WBOOK_ID.toString();
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					for(int ii=0; ii<iWBookInsCnt; ii++) {

		    			//-----------------------------------------------------
			    		// 작업예약 건수 조회하여 MAX건 초과시는 크레인 스케쥴 기동 SKIP
		    			//-----------------------------------------------------
			    		if (iCrnSchCnt >= JPlateYdConst.MAX_CRN_SCH_CNT) {
			    			szMsg = "[" + szOperationName + "] 작업예약 "+JPlateYdConst.MAX_CRN_SCH_CNT+"건 등록  이후 재료 SKIP .... ii >>>> " + Integer.toString(ii);
			    			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			    			continue;
			    		}

						//----------------------------------
						// 3.1.이적 스케줄기동
						//----------------------------------
						recSchPara 	= JDTORecordFactory.getInstance().create();
						
						recSchPara.setField("MSG_ID", 		"YDYDJ"			);		// TC코드
						recSchPara.setField("YD_EQP_ID", 	szYD_WRK_CRN	);		// 크레인설비ID
						recSchPara.setField("YD_SCH_CD",	szYD_SCH_CD		);		// 크레인스케줄코드
						recSchPara.setField("YD_WBOOK_ID",	arrWBookId[ii]	);		// 작업예약ID
						recSchPara.setField("REGISTER", 	szREGISTER		);
						recSchPara.setField("MODIFIER", 	szREGISTER		);
						recSchPara.setField("CHK_FROM_LOC", "N"				);		// 권상위치에 작업예약 존재여부 체크 하지 안도록 SET

						/*
						if (ii==0) {
							recSchPara.setField("CHK_FROM_LOC", "Y");
						} else {
							recSchPara.setField("CHK_FROM_LOC", "N");				//권상위치에 작업예약 존재여부 체크 하지 안도록 SET
						}
						*/

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.14 recSchPara에 logId 추가 
						recSchPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

						szMsg    = "[" + szOperationName + "] ----------- 3.2.이적 스케줄기동 START :: " + ii + " >>>> " + arrWBookId[ii];
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						if(!szYD_GP.equals("F")) // 1후판정정야드 크레인스케줄Main
						{
							//1후판 정정야드 크레인 스케줄 메인
							 EJBConnector ejbConn = new EJBConnector("default", "JPlateYdCrnSchYdPSeEJB", this);
				    	     szRtnMsg = (String)ejbConn.trx("procCrnSchMainYdP", new Class[] { JDTORecord.class }, new Object[] { recSchPara });
						}
						
						else //2후판정정야드 크레인스케줄Main
						{
							//2후판 정정야드 크레인 스케줄 메인  //이부분 분기처리되도록 변경해야함.
							 EJBConnector ejbConn2 = new EJBConnector("default", "JPlateYdCrnSchSeEJB", this);
				    	     szRtnMsg = (String)ejbConn2.trx("procCrnSchMain", new Class[] { JDTORecord.class }, new Object[] { recSchPara });	
						}
						
			    	     
						szMsg    = "[" + szOperationName + "] ----------- 3.3.이적 스케줄기동 END :: " + + ii + " >>>> " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		    	        if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {

		    	        	// 2013.07.24 야드 L2 전송이후에는 RollBack 안되도록 보완
		    	        	// 크레인작업지시 1건이상 생성후에는 작업예약 남겨놓고 권하시 크레인 지시 다시 생성
		    	        	if (iCrnSchCnt > 0) {
			    	        	szRtnMsg = "이적 스케줄기동 오류 .. 계속진행 .. <br>" + szRtnMsg;
								szMsg    = "[" + szOperationName + "] " + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		    	        	} else {
			    	        	szRtnMsg = "이적 스케줄기동 오류 .. <br>" + szRtnMsg;
								szMsg    = "[" + szOperationName + "] " + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
								return szRtnMsg;
		    	        	}
		    	        }

		    	        iCrnSchCnt ++;
					}
				}
			}

		} catch(Exception e) {
			//szMsg = szOperationName + " Error : " + e.getMessage();
			//ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			//return  JPlateYdConst.RETN_CD_FAILURE;
			return  e.getMessage();
		}

		szMsg    = "[" + szOperationName + "] ----------- END ";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}  //end of procPrepLotCompByCapaYdP	
	
	/**
	 * 오퍼레이션명 : 재료번호로 작업예약/크레인작업지시 존재여부 체크
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws DAOException
	 */
	public String chkExistSchduleYdP(JDTORecord pRecPara) {

		JPlateYdWrkbookDAO 	ydWrkbookDao 	= new JPlateYdWrkbookDAO();
		JPlateYdCrnSchDAO  	ydCrnSchDao		= new JPlateYdCrnSchDAO();

		// 레코드 선언
		JDTORecord 		recPara 	= JDTORecordFactory.getInstance().create();
		//레코드셋 선언
		JDTORecordSet 	rsResult 	= null;

		String 	szMethodName 		= "chkExistSchduleYdP";
		String 	szOperationName 	= "스케쥴 존재여부 체크";
		String 	szMsg 				= "";
		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
		String	szStlNo				= "";

		int 	intRtnVal 			= 0;

		//---------------------------------------------------------------------------------------------
		// 2024.11.14 pRecPara에 logId 없으면 새로 발본
		//---------------------------------------------------------------------------------------------
		String logId                = ydLogUtils.getJDTOLogId(pRecPara, "P");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
		
		try {

			szStlNo = ydDaoUtils.paraRecChkNull(pRecPara, "STL_NO");

			// ------------------------------------------------------------------------
			// 1. 작업예약 존재여부 확인
			// ------------------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", 		szStlNo);             	// 재료번호
			recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);

			intRtnVal = ydWrkbookDao.getExistByStlNo(recPara, rsResult);
			if (intRtnVal > 0) {
				szRtnMsg = "해당 재료[" + szStlNo + "]로 작업예약이 존재!";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			// ------------------------------------------------------------------------
			// 2. 크레인 작업지시 존재여부 확인
			// ------------------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", 		szStlNo);             	// 재료번호
			recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);

			intRtnVal = ydCrnSchDao.getExistByStlNo(recPara, rsResult);
			if (intRtnVal > 0) {
				szRtnMsg = "해당 재료" + szStlNo + "로 크레인 작업지시 존재!";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

		} catch (Exception e) {
			szRtnMsg = "스케쥴 존재여부 체크 중 예외발생!";
			szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			//blnRtnVal = false;
			intRtnVal = -100;
			return "데이터 스케쥴 존재여부 체크 중 예외발생!" + e.getMessage();
		}
		return JPlateYdConst.RETN_CD_SUCCESS;
	}
	
	/**
	 * 오퍼레이션명 : 준비스케줄 시 대상재 검색
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws DAOException
	 */
	public String chkPrepLotGpStlListYdP(String szYD_GP, String szSTL_LIST, String szARR_WLOC_CD, JDTORecordSet rsResult, String logId) throws DAOException {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.11.14 insMvWBookId argument 에 logId 항목 추가 개선
//  public String chkPrepLotGpStlListYdP(String szYD_GP, String szSTL_LIST, String szARR_WLOC_CD, JDTORecordSet rsResult) throws DAOException {
////////////////////////////////////////////////////////////////////////////////////////
		// 저장품 DAO
		JPlateYdStockDAO ydStockDao = new JPlateYdStockDAO();
		// 레코드 선언
		JDTORecord 		recPara 	= JDTORecordFactory.getInstance().create();
		JDTORecord 		recStock 	= null;
		// 레코드셋 선언
		JDTORecordSet 	outRecSet 	= null;

		int 	intRtnVal 			= 0;
		String 	szMethodName 		= "chkPrepLotGpStlListYdP";
		String 	szOperationName 	= "준비스케줄 시 대상재 검색";
		String 	szMsg 				= "";
		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
		String	szYdStkLyrMtlStat	= "";

//---------------------------------------------------------------------------------------------
// 2024.11.14 argument에 logId 없으면 새로 발본
//---------------------------------------------------------------------------------------------
		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P"); // log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본

		String[] strArrStlNo    	= null;

		try {

			strArrStlNo = szSTL_LIST.split(";");

			//적치열,베드에 적치된 해당하는 대상재를 조회
			for (int ii=0; ii<strArrStlNo.length; ii++) {

				outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				recPara.setField("STL_NO", strArrStlNo[ii]);
				recPara.setField("YD_GP", szYD_GP);
				
				intRtnVal = ydStockDao.getYdStockWithLoc(recPara, outRecSet);		// intGp == 110

				//조회결과를 체크
				if (intRtnVal == 0 ) {
					szRtnMsg = "재료번호 = " + strArrStlNo[ii] + "에 대상재가 존재하지 않습니다.";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					return szRtnMsg;
				} else if (intRtnVal == -2 ) {
					szRtnMsg = "재료번호 = " + strArrStlNo[ii] + ", 데이터 조회중 parameter error 발생!";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				} else if (intRtnVal < 0 ) {
					szRtnMsg = "재료번호 = " + strArrStlNo[ii] + "에 대상재를 조회 시 에러가 발생했습니다.";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
				szMsg = "재료번호 = " + strArrStlNo[ii] + "에 대상재가 존재합니다.";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				// 재료적치상태 체크 : YD_STK_LYR_MTL_STAT
				outRecSet.first();
				recStock = outRecSet.getRecord();
				szYdStkLyrMtlStat = ydDaoUtils.paraRecChkNull(recStock, "YD_STK_LYR_MTL_STAT");
				if (!"C".equals(szYdStkLyrMtlStat)) {
					szRtnMsg = "재료번호 [" + strArrStlNo[ii] + "] 의 적치상태가 [" + szYdStkLyrMtlStat + "] 로 작업불가 합니다.";
					return szRtnMsg;
				}

				rsResult.addRecord(recStock);
			//	rsResult.addAll(outRecSet);
			}

			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, rsResult.toString(), JPlateYdConst.DEBUG, logId);

			intRtnVal = rsResult.size();

			// 리턴값 메세지처리
			if (intRtnVal <= 0) {
				szRtnMsg = "이적대상 데이터가 없습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			} else {
				szMsg    = "[" + szOperationName + "] " + intRtnVal + "건의 이적대상 조회 완료";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			}

		} catch (Exception e) {
			szRtnMsg = "데이터 유무체크 및 데이터 반환 중 예외발생!";
			szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			//blnRtnVal = false;
			intRtnVal = -100;
			return "데이터 유무체크 및 데이터 반환 중 예외발생!" + e.getMessage();
		}
		return szRtnMsg;
	} // end of chkPrepLotGpStlListYdP

	/**
	 * 오퍼레이션명 : 작업구분에 따른 스케줄코드 Set
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws DAOException
	 */
	public String getSchCdByWrkGpYdP(String pYD_MAIN_WRK_GP, String pFromYdStrLoc, String pToYdStrLoc, String pTCar, String logId) {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.11.14 getSchCdByWrkGpYdP argument 에 logId 항목 추가 개선
//	public String getSchCdByWrkGpYdP(String pYD_MAIN_WRK_GP, String pFromYdStrLoc, String pToYdStrLoc, String pTCar) {
////////////////////////////////////////////////////////////////////////////////////////

		// pYD_MAIN_WRK_GP  -- 주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차, M:보수장이적
		// pFromYdStrLoc	-- 야드적치열구분 	(FROM위치)
		// pToYdStrLoc		-- 야드적치열구분 	(TO위치)
		// pTCar			-- 대차설비번호   	(FXTC01, FXTC02)

		// 이적은 FRom위치 기준으로 스케줄코드가 만들어 지고
		// 보급, Book-In은 To위치 기준으로 만들어짐

		String 	szYD_SCH_CD		= "";
		String	szMsg			= "";
		String	szOperationName	= "스케줄코드 Set";
		String	szMethodName	= "getSchCdByWrkGpYdP";

//---------------------------------------------------------------------------------------------
// 2024.11.14 argument에 logId 없으면 새로 발본
//---------------------------------------------------------------------------------------------
		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P"); // log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
		
		
		szMsg    = "[" + szOperationName + "] \n>>>> 파라미터 >>>> \npYD_MAIN_WRK_GP [" + pYD_MAIN_WRK_GP + "]\npFromYdStrLoc [" + pFromYdStrLoc
		         + "]\npToYdStrLoc [" + pToYdStrLoc + "]\npTCar [" + pTCar + "]";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		// 대차작업이면 대차 상차 스케쥴코드 SET
		if (!"".equals(pTCar)) {

			szYD_SCH_CD	= ydUtils.substr(pFromYdStrLoc, 0, 2) + ydUtils.substr(pTCar, 2, 4) + "UM";			// 대차상차

		//	이적일 경우
		} else if (JPlateYdConst.YD_MAIN_WRK_GP_MV.equals(pYD_MAIN_WRK_GP)) {

			// From위치 기준으로 스케쥴코드 생성하도록 변경
			// 대차일경우
			if ("TC".equals(ydUtils.substr(pFromYdStrLoc, 2, 2))) {
				szYD_SCH_CD	= ydUtils.substr(pFromYdStrLoc, 0, 6) + "LM";									// 대차하차
				
			} else if ("50".equals(ydUtils.substr(pFromYdStrLoc, 4, 2))) {
				szYD_SCH_CD	= ydUtils.substr(pFromYdStrLoc, 0, 2) + ydUtils.substr(pFromYdStrLoc, 4, 2) + ydUtils.substr(pFromYdStrLoc, 2, 2) + "LM";	// 하면검사대 추출(BOOK-OUT)
				//ex) PF5001LM, PF5002LM
			} else {
				String sApplyYnPI =ydPICommDAO.ApplyYnPI("",szOperationName,"APPPI1","T","*");
				
				if(sApplyYnPI.equals("Y")){  //배포 flag 적용시
					if(ydUtils.substr(pFromYdStrLoc, 0, 4).equals("PF01") &&  //F동 01SPAN 대상
					   ydUtils.substr(pFromYdStrLoc, 0, 2).equals(ydUtils.substr(pToYdStrLoc, 0, 2))&&  //동내이적인 경우만		
					   (
						ydUtils.substr(pFromYdStrLoc, 4, 2).equals("05")||    //05~10베드만
						ydUtils.substr(pFromYdStrLoc, 4, 2).equals("06")||
						ydUtils.substr(pFromYdStrLoc, 4, 2).equals("07")||
						ydUtils.substr(pFromYdStrLoc, 4, 2).equals("08")||
						ydUtils.substr(pFromYdStrLoc, 4, 2).equals("09")||
						ydUtils.substr(pFromYdStrLoc, 4, 2).equals("10")
						)	
					   ){
						szYD_SCH_CD = ydUtils.substr(pFromYdStrLoc, 0, 2) + "QA" + ydUtils.substr(pFromYdStrLoc, 2, 2) + "MM";//PFQA01MM

						szMsg    = "[" + szOperationName + "] >>>>품질이적 적용. SCH_CD >>>>:" + szYD_SCH_CD;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					}
					else{
					szYD_SCH_CD = ydUtils.substr(pFromYdStrLoc, 0, 2) + "YD" + ydUtils.substr(pFromYdStrLoc, 2, 2) + "MM";
					}
				}
				else{ //배포flag 미적용시
					szYD_SCH_CD = ydUtils.substr(pFromYdStrLoc, 0, 2) + "YD" + ydUtils.substr(pFromYdStrLoc, 2, 2) + "MM";
				}
			}

		} else {

			// BOOK-IN일 경우
			if (JPlateYdConst.YD_MAIN_WRK_GP_RT_IN.equals(pYD_MAIN_WRK_GP)) {								// RT BOOK-IN일 경우

			//	szYD_SCH_CD = ydUtils.substr(pToYdStrLoc, 0, 2) + "RT" + "00UM";
//---------------------------------------------------------------------------------------------
// 2024.11.14 스케쥴 코드 모듈 return
//---------------------------------------------------------------------------------------------
				szYD_SCH_CD = ydUtils.getRtSchCdYdP(pToYdStrLoc, "UM");

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_GAS_IN.equals(pYD_MAIN_WRK_GP)) {						// GAS장보급일 경우 (BOOK-IN)

				szYD_SCH_CD = ydUtils.substr(pToYdStrLoc, 0, 2) + "CN" + ydUtils.substr(pToYdStrLoc, 4, 1) + "0UM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_BS_IN.equals(pYD_MAIN_WRK_GP)) {						// 보수장보급일 경우 (BOOK-IN)

				szYD_SCH_CD = ydUtils.substr(pToYdStrLoc, 0, 2) + "BS" + "00UM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_BC_IN.equals(pYD_MAIN_WRK_GP)) {						// 임가공절단장 보급일 경우 (BOOK-IN)

				szYD_SCH_CD = ydUtils.substr(pToYdStrLoc, 0, 2) + "BC" + "00UM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_TOD_IN.equals(pYD_MAIN_WRK_GP)) {						// TOD보급일 경우 (BOOK-IN)

				szYD_SCH_CD = ydUtils.substr(pToYdStrLoc, 0, 2) + "TD" + "00UM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_PT_IN.equals(pYD_MAIN_WRK_GP)) {						// 이송차량 상차일 경우 [FCPT01UM]

				szYD_SCH_CD = ydUtils.substr(pToYdStrLoc, 0, 2) + "PT" + ydUtils.substr(pToYdStrLoc, 4, 2) + "UM";

			// BOOK-OUT일 경우
			} else if (JPlateYdConst.YD_MAIN_WRK_GP_RT_OUT.equals(pYD_MAIN_WRK_GP)) {						// RT BOOK-OUT일 경우

			//	szYD_SCH_CD = ydUtils.substr(pFromYdStrLoc, 0, 2) + "RT" + "00LM";
//---------------------------------------------------------------------------------------------
// 2024.11.14 스케쥴 코드 모듈 return
//---------------------------------------------------------------------------------------------
				szYD_SCH_CD = ydUtils.getRtSchCdYdP(pFromYdStrLoc, "LM");

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_GAS_OUT.equals(pYD_MAIN_WRK_GP)) {						// GAS장추출일 경우 (BOOK-OUT)

				szYD_SCH_CD = ydUtils.substr(pFromYdStrLoc, 0, 2) + "CN" + ydUtils.substr(pFromYdStrLoc, 4, 1) + "0LM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_BS_OUT.equals(pYD_MAIN_WRK_GP)) {						// 보수장추출일 경우 (BOOK-OUT)

				szYD_SCH_CD = ydUtils.substr(pFromYdStrLoc, 0, 2) + "BS" + "00LM";
				
				//보수장 BOOK-OUT 이면서 TO위치가 PCRT70(54020존) 이면 PCBS01LM 으로 변경한다.. 
				if("PCRT70".equals(ydUtils.substr(pToYdStrLoc,0,6))) {
					szYD_SCH_CD = ydUtils.substr(pFromYdStrLoc, 0, 2) + "BS" + "01LM";
				}

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_BC_OUT.equals(pYD_MAIN_WRK_GP)) {						// 임가공절단장 추출일 경우 (BOOK-OUT)

				szYD_SCH_CD = ydUtils.substr(pFromYdStrLoc, 0, 2) + "BC" + "00LM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_TOD_OUT.equals(pYD_MAIN_WRK_GP)) {						// TOD추출일 경우 (BOOK-OUT)

				szYD_SCH_CD = ydUtils.substr(pFromYdStrLoc, 0, 2) + "TD" + "00LM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_PT_OUT.equals(pYD_MAIN_WRK_GP)) {						// 이송차량 하차일 경우 [FCPT01LM]

				szYD_SCH_CD = ydUtils.substr(pFromYdStrLoc, 0, 2) + "PT" + ydUtils.substr(pFromYdStrLoc, 4, 2) + "LM";
			}
		}

		szMsg    = "[" + szOperationName + "] >>>> 스케쥴코드 >>>> " + szYD_SCH_CD;
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return szYD_SCH_CD;
	} // end of getSchCdByWrkGpYdP
	
	/**
	 * 오퍼레이션명 : 준비스케줄 시 대상재 검색
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws DAOException
	 */
	public String chkPrepLotGpStlList(String szSTL_LIST, String szARR_WLOC_CD, JDTORecordSet rsResult, String logId) throws DAOException {
		// 저장품 DAO
		JPlateYdStockDAO ydStockDao = new JPlateYdStockDAO();
		// 레코드 선언
		JDTORecord 		recPara 	= JDTORecordFactory.getInstance().create();
		JDTORecord 		recStock 	= null;
		//레코드셋 선언
		JDTORecordSet 	outRecSet 	= null;

		int 	intRtnVal 			= 0;
		String 	szMethodName 		= "chkPrepLotGpStlList";
		String 	szOperationName 	= "준비스케줄 시 대상재 검색";
		String 	szMsg 				= "";
		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
		String	szYdStkLyrMtlStat	= "";

		String[] strArrStlNo    	= null;

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F"); // log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본

		try {

			strArrStlNo = szSTL_LIST.split(";");

			//적치열,베드에 적치된 해당하는 대상재를 조회
			for (int ii=0; ii<strArrStlNo.length; ii++) {

				outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				recPara.setField("STL_NO", strArrStlNo[ii]);
				
//-------------------------------------------------------------------------------------------------------------------------
//2024.12.17 recPara에 logId 추가 : 2024.12.18 운영 적용후 개선 필요
				recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
						        			        																			              	        	
				intRtnVal = ydStockDao.getYdStockWithLoc(recPara, outRecSet);		// intGp == 110

				//조회결과를 체크
				if (intRtnVal == 0 ) {
					szRtnMsg = "재료번호 = " + strArrStlNo[ii] + "에 대상재가 존재하지 않습니다.";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					
					return szRtnMsg;
					
				} else if (intRtnVal == -2 ) {
					szRtnMsg = "재료번호 = " + strArrStlNo[ii] + ", 데이터 조회중 parameter error 발생!";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					
					return szRtnMsg;
					
				} else if (intRtnVal < 0 ) {
					szRtnMsg = "재료번호 = " + strArrStlNo[ii] + "에 대상재를 조회 시 에러가 발생했습니다.";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					
					return szRtnMsg;
					
				}
				
				szMsg = "재료번호 = " + strArrStlNo[ii] + "에 대상재가 존재합니다.";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				// 재료적치상태 체크 : YD_STK_LYR_MTL_STAT
				outRecSet.first();
				recStock = outRecSet.getRecord();
				szYdStkLyrMtlStat = ydDaoUtils.paraRecChkNull(recStock, "YD_STK_LYR_MTL_STAT");
				if (!"C".equals(szYdStkLyrMtlStat)) {
					szRtnMsg = "재료번호 [" + strArrStlNo[ii] + "] 의 적치상태가 [" + szYdStkLyrMtlStat + "] 로 작업불가 합니다.";
					
					return szRtnMsg;
				}

				rsResult.addRecord(recStock);
			//	rsResult.addAll(outRecSet);
			}

			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, rsResult.toString(), JPlateYdConst.DEBUG, logId);

			intRtnVal = rsResult.size();

			// 리턴값 메세지처리
			if (intRtnVal <= 0) {
				szRtnMsg = "이적대상 데이터가 없습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szRtnMsg;
			} else {
				szMsg    = "[" + szOperationName + "] " + intRtnVal + "건의 이적대상 조회 완료";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			}

		} catch (Exception e) {
			szRtnMsg = "데이터 유무체크 및 데이터 반환 중 예외발생!";
			szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			//blnRtnVal = false;
			intRtnVal = -100;
			
			return "데이터 유무체크 및 데이터 반환 중 예외발생!" + e.getMessage();
		}
		
		return szRtnMsg;
	} // end of chkPrepLotGpStlList

	
}
