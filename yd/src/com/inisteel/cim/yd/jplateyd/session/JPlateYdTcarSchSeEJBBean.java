/*
 * @(#) 2후판정정야드 대차스케쥴 Session EJB
 *
 * @version			V1.00
 * @author			김현우
 * @date			2013/04/18
 *
 * @description		권하실적처리 Session EJB
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2013/04/18   김현우      김현우       최초작성
 */

package com.inisteel.cim.yd.jplateyd.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO;
import com.inisteel.cim.yd.jplateyd.delegate.JPlateYdDelegate;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdCommonUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;

/**
 * 대차스케쥴 Session EJB
 *
 * @ejb.bean name="JPlateYdTcarSchSeEJB" jndi-name="JPlateYdTcarSchSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class JPlateYdTcarSchSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;

	// Session Name
	private final String SZ_SESSION_NAME = getClass().getName();

	private JPlateYdUtils 		ydUtils     = new JPlateYdUtils();
	private JPlateYdDaoUtils	ydDaoUtils  = new JPlateYdDaoUtils();
	private JPlateYdDelegate 	ydDelegate 	= new JPlateYdDelegate();

	// [DEBUG] message flag
	private boolean bDebugFlag = true;

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * 오퍼레이션명 : 2후판정정야드 대차 스케줄
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public String procY7TcarSch(JDTORecord msgRecord)throws DAOException  {

	    int 	intRtnVal 				= 0 ;

	    String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
	    String 	szMsg              		= "";
	    String 	szMethodName       		= "procY7TcarSch";
	    String 	szOperationName			= "2후판정정 대차스케줄";

	    //상하차 구분
	    String 	szLdUdGp				= "";

        String szRcvTcCode = ydUtils.getTcCode(msgRecord);
        if (szRcvTcCode==null || "".equals(szRcvTcCode)) {
        	szRtnMsg = "TC Code Error ("+szRcvTcCode+")";
        	szMsg = "["+szOperationName+"] " + szRtnMsg;
        	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
        	return szRtnMsg;
        }

        if (bDebugFlag) {
            szMsg = "["+szOperationName+"] [DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
        }

	    try {
	    	szMsg = "["+szOperationName+"] ------------------- 메소드 시작 -------------------";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

            szLdUdGp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_LD_UD_GP");

	    	//------------------------------------------------------------------------------------------
    		// 권상 실적 처리중 호출한 경우 - 하차 시
	    	//------------------------------------------------------------------------------------------
    		if ("U".equals(szLdUdGp)) {

    			szMsg = "["+szOperationName+"] 권상실적 처리 시 호출 - 대차하차스케줄 모듈 시작";
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    			//대차 하차스케줄 호출
                szRtnMsg = this.procY7AfterUpCrnWrk(msgRecord);

    			szMsg = "["+szOperationName+"] 권상실적 처리 시 호출 - 대차하차스케줄 모듈 완료 - 반환값 : " + intRtnVal;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    		//------------------------------------------------------------------------------------------
    		// 권하 실적 처리중 호출한 경우 - 상차 시
    		//------------------------------------------------------------------------------------------
    		} else if ("L".equals(szLdUdGp)){

    			szMsg = "["+szOperationName+"] 권하실적 처리 시 호출 - 대차상차스케줄 모듈 시작";
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    			//대차 상차 스케줄 호출
                szRtnMsg = this.procY7AfterDnCrnWrk(msgRecord);

    			szMsg = "["+szOperationName+"] 권하실적 처리 시 호출 - 대차상차스케줄 모듈 완료 - 반환값 : " + intRtnVal;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}
		} catch(Exception e) {

			szMsg = "["+szOperationName+"] 예외 발생 : " +e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}

		szMsg = "["+szOperationName+"] ------------------- 메소드 끝 -------------------";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return  JPlateYdConst.RETN_CD_SUCCESS;
	} //end of procY7TcarSch()

	/**
	 * 오퍼레이션명 : 대차 하차 스케줄  (대차 도착처리후 호출)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public String procY7LdTcarSch(JDTORecord msgRecord)throws DAOException  {

		//동간이적요구시 화면에서  송신하는 경우
		JPlateYdEqpDAO      	ydEqpDao        = new JPlateYdEqpDAO();
		JPlateYdSchRuleDAO		ydSchRuleDao    = new JPlateYdSchRuleDAO();
		JPlateYdWrkbookDAO  	ydWrkbookDao    = new JPlateYdWrkbookDAO();
		JPlateYdStkBedDAO   	ydStkBedDao		= new JPlateYdStkBedDAO();
		JPlateYdWrkbookMtlDAO	ydWrkbookMtlDao	= new JPlateYdWrkbookMtlDAO();
		JPlateYdStockDAO 		ydStockDao		= new JPlateYdStockDAO();

		JDTORecordSet 	rsResult        = null;
//		JDTORecordSet 	rsWBook        	= null;
		JDTORecord    	recPara       	= null;
		JDTORecord    	recOutTemp      = null;
		JDTORecord    	recSchPara		= null;
	    int intRtnVal 					= 0 ;

	    String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
	    String 	szMsg              		= "";
	    String 	szMethodName       		= "procY7LdTcarSch";
	    String 	szOperationName			= "대차 하차 스케줄";

	    String 	szTcarEqpId             = "";					// 대차설비 ID
	    String 	szYdSchCd               = "";					// 스케쥴코드

	    String 	szYdTcarCurrBayGp   	= "";					// 대차 현재동

	    String	szYdWbookId				= "";					// 작업예약 ID
	    String	szYdGp					= "";					// 야드구분
	    String 	szYdBayGp				= "";					// 야드동구분
	    String	szYdWrkCrnPrior			= "";					// 야드스케쥴우선순위
	    String	szYdSchProhExn			= "";					// 스케쥴금지여부
		String	szYdWrkCrn				= "";					// 주작업크레인 :: YdSchRule에 등록된 크레인Id
	    String	szStlNo					= "";					// 재료번호
	    String	szYdStkColGp			= "";					// 야드적치열구분
		String	szYdStkBedNo 			= "";					// 야드적치Bed번호
		String	szYdStkLyrNo 			= "";					// 야드적치단번호
		String	szYdToLocGuide			= "";					// 야드To위치 Guide
	    String 	szModifier				= "";					// 수정자
	    String	szYdWrkPlanTcar			= "";					// 야드작업계획대차 (대차하차위치)

	    int		iWBookInsCnt			= 0;					// 작업예약 등록 건수
	    int		iWBookSkipCnt			= 0;					// 기존작업예약 존재 .. SKIP CNT

		String[]	arrWbookId 			= new String[JPlateYdConst.MAX_CRN_SCH_CNT];
		String[]	arrToLocGuide 		= new String[JPlateYdConst.MAX_CRN_SCH_CNT];

	    try {

	    	szMsg = "["+szOperationName+"] 대차 하차 스케줄 .... START >>>> " + msgRecord.toString();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    	szTcarEqpId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");		//대차설비ID
//	    	szYdToBay 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_BAY");		//공대차출발 시 사용자가 지정한 목표동(값 없을수 있슴)
	    	szModifier	= ydDaoUtils.paraRecModifier(msgRecord);					//수정자

	    	//-------------------------------------------------------------------------------------
	    	//	대차설비ID로 설비테이블을 조회한다.
	    	//-------------------------------------------------------------------------------------
	    	szMsg = "["+szOperationName+"] 대차 하차시 .. 대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    		rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
    		recPara  = JDTORecordFactory.getInstance().create();
    		recPara.setField("YD_EQP_ID", 			szTcarEqpId);

    		intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult);		// intGp == 0

    		szMsg = "["+szOperationName+"] 대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if (intRtnVal <= 0) {
    			szRtnMsg = "대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 시 오류발생 - 반환값 : " + intRtnVal;
    			szMsg = "["+szOperationName+"] " + szRtnMsg;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			return szRtnMsg;
			}

			rsResult.absolute(1);
    		recOutTemp = JDTORecordFactory.getInstance().create();
    		recOutTemp.setRecord(rsResult.getRecord());

    		szYdTcarCurrBayGp	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CURR_BAY_GP");	//현재동

    		//-------------------------------------------------------------------------------------
    		//	작업예약ID가 미존재시 대차위치의 재료를 조회하여 작업예약을 등록 한다. 대차설비ID :: FXTC01
    		//-------------------------------------------------------------------------------------
    		szYdGp		 = ydUtils.substr(szTcarEqpId, 0, 1);
    		szYdBayGp	 = szYdTcarCurrBayGp;
    		szYdStkColGp = szYdGp + szYdTcarCurrBayGp + ydUtils.substr(szTcarEqpId, 2, 4);
    		szYdSchCd	 = szYdStkColGp + "LM";		// 대차하차 스케쥴코드 : FBTC01LM

			//-----------------------------------------------------
			// 스케줄기준 정보 Check (야드스케쥴금지유무, 야드스케쥴우선순위)
			//-----------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
    		recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_SCH_CD", szYdSchCd); //야드스케쥴코드

			ydSchRuleDao.getYdSchrule(recPara, rsResult);

			if (rsResult != null && rsResult.size() > 0) {
				szYdSchProhExn  = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_SCH_PROH_EXN");
				szYdWrkCrnPrior = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_CRN_PRIOR");
				szYdWrkCrn		= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_CRN");
			}

			if ("".equals(szYdSchProhExn)) {
				//스케줄기준 Table 정보 Check
				szRtnMsg = "오류:스케쥴코드[" + szYdSchCd + "] 정보 없음";
    			szMsg = "["+szOperationName+"] " + szRtnMsg;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			return szRtnMsg;
			} else if ("Y".equals(szYdSchProhExn)) {
				//스케줄 금지여부 Check
				szRtnMsg = "오류:스케쥴코드[" + szYdSchCd + "] 기동금지";
    			szMsg = "["+szOperationName+"] " + szRtnMsg;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			return szRtnMsg;
			}

			//-----------------------------------------------------
			// 대차위에 재료정보 조회
			//-----------------------------------------------------
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("yd");
    		recPara   = JDTORecordFactory.getInstance().create();
    		recPara.setField("YD_STK_COL_GP", szYdStkColGp);
    		intRtnVal = ydStkBedDao.getTcarMtlWithWBookId(recPara, rsResult);
    		if (intRtnVal <= 0) {
    			szRtnMsg = "대차위에 재료정보가 미존재하여 .. 대차 스케쥴기동 SKIP 처리";
    			szMsg = "["+szOperationName+"] " + szRtnMsg;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    		//	return szRtnMsg;
    			return JPlateYdConst.RETN_CD_SUCCESS;
    		}

    		rsResult.first();
    		for(int ii=0; ii<rsResult.size(); ii++) {
    			rsResult.absolute(ii+1);
	    		recOutTemp = rsResult.getRecord();

	    		// 해당 재료가 작업예약 미등록시에만 작업 예약 등록 처리
	    		if ("".equals(ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID"))) {

		    		szStlNo 	 	= ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO");
		    		szYdStkColGp 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_COL_GP");
		    		szYdStkBedNo 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_BED_NO");
		    		szYdStkLyrNo 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_LYR_NO");
		    		szYdWrkPlanTcar = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PLAN_TCAR");

	    			//---------------------------------------------------------------------
		    		// 2013.07.13 매번 작업 예약 ID 생성 하도록 변경
	    			//---------------------------------------------------------------------
	    			szYdWbookId = ydWrkbookDao.getSeqId();

	    			if ("".equals(szYdWbookId)) {
		    			szRtnMsg = "오류:작업예약ID 생성 실패";
		    			szMsg = "["+szOperationName+"] " + szRtnMsg;
		    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
		    			return szRtnMsg;
	    			}

					szMsg = "["+szOperationName+"] ----------- 작업예약 ID 생성 :: " + szYdWbookId + ", 건수 :: " + iWBookInsCnt;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					if ("".equals(szYdWrkPlanTcar)) {
						szYdToLocGuide = szYdGp+szYdBayGp;
					} else {
						szYdToLocGuide = szYdWrkPlanTcar;
					}

	    			//---------------------------------------
	    			// 작업예약 등록
	    			//---------------------------------------
	    			recPara.setField("YD_WBOOK_ID", 		szYdWbookId); 		//야드작업예약ID
	    			recPara.setField("YD_GP", 				szYdGp); 			//야드구분
	    			recPara.setField("YD_BAY_GP", 			szYdBayGp); 		//야드동구분
	    			recPara.setField("YD_SCH_CD", 			szYdSchCd); 		//야드스케쥴코드
	    			recPara.setField("YD_SCH_PRIOR", 		szYdWrkCrnPrior); 	//야드스케쥴우선순위
	    			recPara.setField("YD_SCH_PROG_STAT", 	"W"); 				//야드스케쥴진행상태(스케줄수행대기)
	    			recPara.setField("YD_SCH_ST_GP", 		"M"); 				//야드스케쥴기동구분(Manual 작업)
	    			recPara.setField("YD_SCH_REQ_GP", 		"X"); 				//야드스케쥴요청구분(강제권상요구)
	    			recPara.setField("YD_AIM_YD_GP", 		szYdGp); 			//야드목표야드구분
	    			recPara.setField("YD_AIM_BAY_GP", 		szYdTcarCurrBayGp); //야드목표동구분
	    			recPara.setField("YD_TO_LOC_DCSN_MTD", 	"S"); 				//야드TO위치결정방법(스케줄기준적용)
	    			recPara.setField("YD_TO_LOC_GUIDE",		szYdToLocGuide);	//야드To위치Guide
	    			recPara.setField("REGISTER",			szModifier);
	    			recPara.setField("MODIFIER",			szModifier);

	    			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
	    			if (intRtnVal <= 0) {
		    			szRtnMsg = "오류:작업예약 등록 실패 >>>> " + Integer.toString(intRtnVal);
		    			szMsg = "["+szOperationName+"] " + szRtnMsg;
		    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
		    			return szRtnMsg;
	    			}

	    			//---------------------------------------
	    			// 작업예약 재료 등록
	    			//---------------------------------------
					recPara.setField("STL_NO", 				szStlNo); 			//재료번호
					recPara.setField("YD_STK_COL_GP",		szYdStkColGp);
		    		recPara.setField("YD_STK_BED_NO",		szYdStkBedNo);
					recPara.setField("YD_STK_LYR_NO", 		szYdStkLyrNo); 		//야드적치단번호

					ydWrkbookMtlDao.insYdWrkbookMtl(recPara);

					//저장품 작업예약정보 수정 (TB_YD_STOCK)
					ydStockDao.updYdStockWbook(recPara);

	    		} else {

					szYdWbookId 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID");
					szYdToLocGuide 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TO_LOC_GUIDE");

					iWBookSkipCnt ++;
	    			szMsg = "["+szOperationName+"] 기존 작업예약이 존재하여 INSERT SKIP >>>> " + ii + " :: " + szYdWbookId;
	    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

	    		}

	    		if (ii < JPlateYdConst.MAX_CRN_SCH_CNT) {
	    			arrWbookId[ii] 		= szYdWbookId;
		    		arrToLocGuide[ii] 	= szYdToLocGuide;
	    		}
				iWBookInsCnt ++;


    		} // end for

			szMsg = "["+szOperationName+"] 작업예약 등록 완료 >>>> 등록건수 :: " + Integer.toString(iWBookInsCnt) + ", SKIP CNT :: " + iWBookSkipCnt;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			for(int ii=0; ii<iWBookInsCnt; ii++) {

    			//-----------------------------------------------------
	    		// 작업예약 건수 조회하여 MAX건 초과시는 크레인 스케쥴 기동 SKIP
    			//-----------------------------------------------------
	    		if (ii >= JPlateYdConst.MAX_CRN_SCH_CNT) {
	    			szMsg = "["+szOperationName+"] 작업예약 "+JPlateYdConst.MAX_CRN_SCH_CNT+"건 등록  이후 재료 SKIP .... ii >>>> " + Integer.toString(ii);
	    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	    			break;
	    		}

				if (ii >= arrWbookId.length || arrWbookId[ii] == null || "".equals(arrWbookId[ii])) {
	    			szMsg = "["+szOperationName+"] 작업예약  오류로 SKIP 처리 >>>> " + arrWbookId[ii];
	    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					break;
				}

		    	//------------------------------------------------
				// 크레인 스케줄 호출
	    		//------------------------------------------------
				if ("".equals(arrToLocGuide[ii])) {
					arrToLocGuide[ii] = szYdGp + szYdBayGp;
				}
				recSchPara 	= JDTORecordFactory.getInstance().create();
				recSchPara.setField("MSG_ID", 			"YDYDJ");			//TC코드
				recSchPara.setField("YD_EQP_ID", 		szYdWrkCrn);		//크레인설비ID
				recSchPara.setField("YD_SCH_CD",		szYdSchCd);			//크레인스케줄코드
				recSchPara.setField("YD_WBOOK_ID",		arrWbookId[ii]);	//작업예약ID
				recSchPara.setField("REGISTER", 		szModifier);
				recSchPara.setField("MODIFIER", 		szModifier);
				recSchPara.setField("YD_TO_LOC_GUIDE",	arrToLocGuide[ii]);	//야드To위치Guide
				if (ii==0) {
					recSchPara.setField("CHK_FROM_LOC", "Y");
				} else {
					recSchPara.setField("CHK_FROM_LOC", "N");				//권상위치에 작업예약 존재여부 체크 하지 안도록 SET
				}

				szMsg    = "["+szOperationName+"] ----------- 크레인 스케줄기동 START >>>> idx :: " + ii + ", 작업예약ID :: " + arrWbookId[ii];
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		        EJBConnector ejbConn = new EJBConnector("default", "JPlateYdCrnSchSeEJB", this);
		        szRtnMsg = (String)ejbConn.trx("procCrnSchMain", new Class[] { JDTORecord.class }, new Object[] { recSchPara });

				szMsg    = "["+szOperationName+"] ----------- 크레인 스케줄기동 END :: " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}  // end for

			szMsg = "["+szOperationName+"] 대차 하차 스케줄 완료";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch(Exception e) {

			szMsg = "["+szOperationName+"] 대차 하차 스케줄  Error : " +e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}

		szMsg = "["+szOperationName+"] 대차 하차 스케줄 .... END";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;

	} //end of procY7LdTcarSch()


	/**
	 * 오퍼레이션명 : 대차 상차 스케줄  (대차 도착처리후 호출)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public String procY7UdTcarSch(JDTORecord msgRecord)throws DAOException  {

		//동간이적요구시 화면에서  송신하는 경우
		JPlateYdEqpDAO      	ydEqpDao        = new JPlateYdEqpDAO();
		JPlateYdSchRuleDAO		ydSchRuleDao    = new JPlateYdSchRuleDAO();
		JPlateYdWrkbookDAO  	ydWrkbookDao    = new JPlateYdWrkbookDAO();

		JDTORecordSet 	rsResult        = null;
		JDTORecord    	recPara       	= null;
		JDTORecord    	recOutTemp      = null;
		JDTORecord		recSchPara		= null;
	    int intRtnVal 					= 0 ;

	    String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
	    String 	szMsg              		= "";
	    String 	szMethodName       		= "procY7UdTcarSch";
	    String 	szOperationName			= "대차 상차 스케줄";

	    String 	szTcarEqpId             = "";					// 대차설비 ID
	    String 	szYdSchCd               = "";					// 스케쥴코드

	    String 	szYdTcarCurrBayGp   	= "";					// 대차 현재동

	    String	szYdWbookId				= "";					// 작업예약 ID
	    String	szYdGp					= "";					// 야드구분
	    String	szYdSchProhExn			= "";					// 스케쥴금지여부
		String	szYdWrkCrn				= "";					// 주작업크레인 :: YdSchRule에 등록된 크레인Id
	    String	szYdStkColGp			= "";					// 야드적치열구분
	    String 	szModifier				= "";					// 수정자
	    String	szYdToLocGuide			= "";
	    int		iWBookInsCnt			= 0;					// 작업예약 등록 건수 :: MAX건 등록여부 체크시 사용

	    try {

	    	szMsg = "["+szOperationName+"] 대차 상차 스케줄 .... START >>>> " + msgRecord.toString();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    	szTcarEqpId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");					//대차설비ID
//	    	szYdToBay 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_BAY");					//공대차출발 시 사용자가 지정한 목표동(값 없을수 있슴)
	    	szYdWbookId	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");					//작업예약ID(값 없을수 있슴)
	    	szModifier	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");					//수정자
	    	if ("".equals(szModifier)) {
	    		szModifier	= ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER", "YARDSYSTEM");	//수정자
	    	}

	    	//-------------------------------------------------------------------------------------
	    	//	대차설비ID로 설비테이블을 조회한다.
	    	//-------------------------------------------------------------------------------------
	    	szMsg = "["+szOperationName+"] 대차 상차시 .... 대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    		rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
    		recPara  = JDTORecordFactory.getInstance().create();
    		recPara.setField("YD_EQP_ID", 			szTcarEqpId);

    		intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult);		// intGp == 0

    		szMsg = "["+szOperationName+"] 대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if (intRtnVal <= 0) {
    			szRtnMsg = "대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 시 오류발생 - 반환값 : " + intRtnVal;
    			szMsg = "["+szOperationName+"] " + szRtnMsg;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			return szRtnMsg;
			}

			rsResult.absolute(1);
    		recOutTemp = JDTORecordFactory.getInstance().create();
    		recOutTemp.setRecord(rsResult.getRecord());

    		szYdTcarCurrBayGp	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CURR_BAY_GP");	//현재동

	    	if ("".equals(szYdWbookId)) {

	    		//-------------------------------------------------------------------------------------
	    		//	작업예약ID가 미존재시 해당 스케쥴코드로 작업예약을 조회 한다. 대차설비ID :: FXTC01
	    		//-------------------------------------------------------------------------------------
	    		szYdGp		 = ydUtils.substr(szTcarEqpId, 0, 1);
	    		szYdStkColGp = szYdGp + szYdTcarCurrBayGp + ydUtils.substr(szTcarEqpId, 2, 4);
	    		szYdSchCd	 = szYdStkColGp + "UM";		// 대차 상차 스케쥴코드 : FBTC01UM

    			//-----------------------------------------------------
    			// 스케줄기준 정보 Check (야드스케쥴금지유무, 야드스케쥴우선순위)
    			//-----------------------------------------------------
    			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
	    		recPara  = JDTORecordFactory.getInstance().create();
    			recPara.setField("YD_SCH_CD", szYdSchCd); //야드스케쥴코드

    			ydSchRuleDao.getYdSchrule(recPara, rsResult);

    			if (rsResult != null && rsResult.size() > 0) {
    				szYdSchProhExn  = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_SCH_PROH_EXN");
    				szYdWrkCrn		= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_CRN");
    			}

    			if ("".equals(szYdSchProhExn)) {
    				//스케줄기준 Table 정보 Check
    				szRtnMsg = "오류:스케쥴코드[" + szYdSchCd + "] 정보 없음";
	    			szMsg = "["+szOperationName+"] " + szRtnMsg;
	    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    			return szRtnMsg;
    			} else if ("Y".equals(szYdSchProhExn)) {
    				//스케줄 금지여부 Check
    				szRtnMsg = "오류:스케쥴코드[" + szYdSchCd + "] 기동금지";
	    			szMsg = "["+szOperationName+"] " + szRtnMsg;
	    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    			return szRtnMsg;
    			}

				rsResult  = JDTORecordFactory.getInstance().createRecordSet("yd");
	    		recPara   = JDTORecordFactory.getInstance().create();
    			recPara.setField("YD_SCH_CD", szYdSchCd); //야드스케쥴코드
	    		intRtnVal = ydWrkbookDao.getBySchCdWithCrnSchNo(recPara, rsResult);

	    		if (intRtnVal <= 0) {
	    			szRtnMsg = "대차 작업예약 정보가 미존재하여 .. 대차 상차 스케쥴기동 SKIP 처리";
	    			szMsg = "["+szOperationName+"] " + szRtnMsg;
	    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		//	return szRtnMsg;
	    			return JPlateYdConst.RETN_CD_SUCCESS;
	    		}

	    		rsResult.first();
	    		for(int ii=0; ii<rsResult.size(); ii++) {
	    			rsResult.absolute(ii+1);
		    		recOutTemp = rsResult.getRecord();

	    			//-----------------------------------------------------
		    		// 작업예약 건수 조회하여 MAX건 초과시는 크레인 스케쥴 기동 SKIP
	    			//-----------------------------------------------------
		    		if (iWBookInsCnt >= JPlateYdConst.MAX_CRN_SCH_CNT) {
		    			szMsg = "["+szOperationName+"] 작업예약 "+JPlateYdConst.MAX_CRN_SCH_CNT+"건 등록 .. 이후 재료 SKIP .... 크레인 작업지시 호출 건수 :: " + Integer.toString(iWBookInsCnt);
		    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
		    			continue;
		    		}

		    		szYdWbookId 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID");
		    		szYdToLocGuide	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TO_LOC_GUIDE");

			    	//------------------------------------------------
					// 크레인 스케줄 호출
		    		//------------------------------------------------
					recSchPara 	= JDTORecordFactory.getInstance().create();
					recSchPara.setField("MSG_ID", 			"YDYDJ");			//TC코드
					recSchPara.setField("YD_EQP_ID", 		szYdWrkCrn);		//크레인설비ID
					recSchPara.setField("YD_SCH_CD",		szYdSchCd);			//크레인스케줄코드
					recSchPara.setField("YD_WBOOK_ID",		szYdWbookId);		//작업예약ID
					recSchPara.setField("REGISTER", 		szModifier);
					recSchPara.setField("MODIFIER", 		szModifier);
					recSchPara.setField("YD_TO_LOC_GUIDE",	szYdToLocGuide);	//야드To위치Guide

					szMsg   = "["+szOperationName+"] ----------- 크레인 스케줄기동 START :: " + szYdWbookId;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			        EJBConnector ejbConn = new EJBConnector("default", "JPlateYdCrnSchSeEJB", this);
			        szRtnMsg = (String)ejbConn.trx("procCrnSchMain", new Class[] { JDTORecord.class }, new Object[] { recSchPara });

					szMsg    = "["+szOperationName+"] ----------- 크레인 스케줄기동 END :: " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					iWBookInsCnt ++;

	    		}	// end for
	    	}

			szMsg = "["+szOperationName+"] 대차 상차 스케줄("+szMethodName+") 완료";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch(Exception e) {

			szMsg = "["+szOperationName+"] 대차 상차 스케줄  Error : " +e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}

		szMsg = "["+szOperationName+"] 대차 상차 스케줄 메소드  END";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;

	} //end of procY7UdTcarSch()

	/**
	 * 오퍼레이션명 : 대차 상차 스케줄 , 크레인 권하완료후 호출
	 * 				--> 2013.12.03 대차 상차후 하차스케줄 안만들도록 보완 (정정야드재료 테이블에 YD_WRK_PLAN_TCAR(야드작업계획대차) 항목사용
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public String procY7AfterDnCrnWrk(JDTORecord msgRecord)throws DAOException  {

		JPlateYdTcarSchDAO     ydTcarSchDao   	= new JPlateYdTcarSchDAO();

		JDTORecordSet rsResult          = null;

		JDTORecord    recPara         	= null;
		JDTORecord    recTcarSch        = null;
		JDTORecord    recWrkBookMtl     = null;

	    int 	intRtnVal 				= 0;

	    String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
	    String 	szMsg              		= "";
	    String 	szMethodName       		= "procY7AfterDnCrnWrk";
	    String 	szOperationName			= "대차상차스케줄";

//	    String 	szYD_WBOOK_ID           = "";
	    String	szYD_EQP_ID				= "";
	    String 	szYD_SCH_CD             = "";
	    String	szYD_TCAR_SCH_ID		= "";

	    try {
	    	szMsg = "["+szOperationName+"] 대차 상차 스케줄 (크레인 권하완료후 호출) 시작 -------------------";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    	szYD_EQP_ID 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");

	    	rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	recPara   = JDTORecordFactory.getInstance().create();
	    	recPara.setField("YD_EQP_ID", 		szYD_EQP_ID);
	    	intRtnVal = ydTcarSchDao.getByYdEqpId(recPara, rsResult);
	    	if (intRtnVal <= 0) {
	    		szRtnMsg = "대차 스케쥴 조회시 오류 :: " + Integer.toString(intRtnVal);
	    		szMsg = "[" + szOperationName + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}
	    	rsResult.first();
	    	recTcarSch = rsResult.getRecord();
	    	szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_TCAR_SCH_ID");

    		//------------------------------------------------------------------------------------------
	    	// 하차작업예약 생성 및 등록
	    	//------------------------------------------------------------------------------------------
			szMsg = ">>>> 대차 하차작업예약 생성 및 등록";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    	//하차 작업예약 및 작업예약재료 생성
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = this.makeY7WrkBook(msgRecord, rsResult);
	    	if (intRtnVal == -1 || rsResult.size() < 1) {
	    		szRtnMsg = "대차 하차작업예약 생성 및 등록시 오류 :: " + Integer.toString(intRtnVal);
	    		szMsg = "[" + szOperationName + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

    		//------------------------------------------------------------------------------------------
	    	// 대차스케줄에 하차작업예약id 등록
	    	//------------------------------------------------------------------------------------------
			szMsg = ">>>> 대차스케줄에 하차작업예약id 등록";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    	rsResult.absolute(1);
	    	recWrkBookMtl = JDTORecordFactory.getInstance().create();
	    	recWrkBookMtl.setRecord(rsResult.getRecord());

	    	szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_SCH_CD");

	    	recPara = JDTORecordFactory.getInstance().create();
	    	recPara.setField("YD_TCAR_SCH_ID", 			szYD_TCAR_SCH_ID);
	    	recPara.setField("YD_CARUD_WRK_BOOK_ID", 	ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_WBOOK_ID"));
	    	recPara.setField("YD_EQP_WRK_STAT", 		"L");
	    	recPara.setField("YD_CAR_PROG_STAT", 		"5");
	    	recPara.setField("YD_CARUD_STOP_LOC", 		ydUtils.substr(szYD_SCH_CD, 0, 6));

	    	intRtnVal = ydTcarSchDao.updYdCarUdInfo(recPara);
			if (intRtnVal <= 0) {
	    		szRtnMsg = "대차 하차 스케줄 작업예약id 등록 오류 :: " + Integer.toString(intRtnVal);
	    		szMsg = "[" + szOperationName + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
    		}

		} catch(Exception e) {
			szRtnMsg = "대차 하차 스케줄  Exception 발생";
			szMsg    = "("+szMethodName+") ---- " + szRtnMsg + ">>>>" +e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szRtnMsg);
		}

		szMsg = "("+szMethodName+") ---- 대차 상차 스케줄  완료 ---- ";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;

	} //end of procY7AfterDnCrnWrk()


	/**
	 * 오퍼레이션명 : 대차 하차 스케줄 , 크레인 권상완료후 호출
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public String procY7AfterUpCrnWrk(JDTORecord msgRecord)throws DAOException  {

		// 2후판정정야드는 대차이송재료 테이블 사용안함으로 주석처리
/*
		JPlateYdTcarSchDAO     ydTcarSchDao     = new JPlateYdTcarSchDAO();
		JPlateYdTcarFtmvMtlDAO ydTcarFtmvMtlDao = new JPlateYdTcarFtmvMtlDAO();

		JDTORecordSet rsResult          = null;
		JDTORecordSet rsTcarSch         = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;

		int intRtnVal 					= 0 ;

		String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
	    String 	szMsg              		= "";
	    String 	szMethodName       		= "procY7AfterUpCrnWrk";
	    String 	szOperationName			= "대차하차스케줄";

	    String 	szWbookId				= "";
	    String 	szYD_EQP_ID				= "";

	    try {
	    	/*
			//하차 완료 Check를 한다.
	    	//작업예약ID로 대차하차작업예약ID로 대차스케줄을 조회해서 대차스케줄ID를 조회한다.
	    	szWbookId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");

	    	szMsg = "["+szOperationName+"] 대차 하차 스케줄 START >>>> " + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    	//대차스케줄 조회용
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CARUD_WRK_BOOK_ID", szWbookId);
	    	rsTcarSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = ydTcarSchDao.getByWrkBookId(recInTemp, rsTcarSch);				// intGp == 1

	    	//대차이송재료조회
	    	rsTcarSch.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsTcarSch.getRecord());
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");

	    	szYD_EQP_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_ID");

	    	//대차이송재료가 전부 삭제상태인지 확인한다.
	    	intRtnVal = ydTcarFtmvMtlDao.getByYdTcarSchId(recOutTemp, rsResult);		// intGp == 1

	    	//하차가 완료된 경우
	    	if (intRtnVal == 0) {

	    		//대차스케줄 삭제처리
	    		recInTemp = JDTORecordFactory.getInstance().create();
	    		recInTemp.setField("YD_TCAR_SCH_ID", 	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID"));
	    		recInTemp.setField("DEL_YN", 			"Y");
	    		recInTemp.setField("YD_EQP_WRK_STAT", 	"U");
				recInTemp.setField("YD_CAR_PROG_STAT", 	"E");

				intRtnVal = ydTcarSchDao.delYdTcarsch(recInTemp);		// intGp == 0

				//
				// * 대차스케줄생성 메소드 호출
				// * msgRecord에 값을 비워서 보내도록 변경
				// * 이전작업예약이 남아있어 대차상차작업예약ID에 대차 하차작업예약을 등록함.
				// * 작업예약이 없는 상태에서 다음 작업예약을 찾도록 조회!!
				//
				msgRecord = JDTORecordFactory.getInstance().create();
				msgRecord.setField("YD_EQP_ID", szYD_EQP_ID);

				szRtnMsg = this.procY7LdTcarSch(msgRecord);
				if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					return szRtnMsg;
				}
	    	}

		} catch(Exception e) {

			szMsg = "대차 하차 스케줄 Error:" +e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}

		szMsg = "대차 하차 스케줄("+szMethodName+") 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
*/
		return JPlateYdConst.RETN_CD_SUCCESS;

	} //end of procY7AfterUpCrnWrk()

	/**
	 * 오퍼레이션명 : 작업예약재료생성
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, rsWbooId
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int makeY7WrkBook(JDTORecord msgRecord, JDTORecordSet rsWbooId)throws DAOException  {

		JPlateYdWrkbookMtlDAO  	ydWrkbookMtlDao	= new JPlateYdWrkbookMtlDAO();
		JPlateYdWrkbookDAO     	ydWrkbookDao    = new JPlateYdWrkbookDAO();
		JPlateYdSchRuleDAO     	ydSchRuleDao    = new JPlateYdSchRuleDAO();
		JPlateYdStockDAO		ydStockDao		= new JPlateYdStockDAO();

		JDTORecordSet rsResult          = null;
		JDTORecordSet rsWrkBookMtl      = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;

	    int 	intRtnVal 				= 0;

	    String 	szMsg              		= "";
	    String 	szMethodName       		= "makeY7WrkBook";

	    String	szYdEqpId				= "";
	    String 	szWbookId               = "";
	    String 	szYdGp                  = "";
	    String 	szYdBayGp               = "";
	    String 	szCurSchCd              = "";
	    String 	szWrkPlanTcar           = "";
	    String 	szSchPrior              = "";
	    String 	szYdToLocDcsnMtd		= null;
	    String 	szYdToLocGuide			= null;
	    String	szModifier				= "";
//    	String	szYdCrnSchId			= "";

	    try {

	    	szMsg = "작업예약재료생성 .. START : " + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    	szWbookId   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	szYdEqpId	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
//	    	szYdCrnSchId= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
	    	szModifier  = ydDaoUtils.paraRecModifier(msgRecord);

	    	//작업예약재료 Table를 조회한다.
	    	rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal    = ydWrkbookMtlDao.getTcarDnWrkMtl(msgRecord, rsWrkBookMtl);		// intGp == 11
			if (intRtnVal <= 0) {
				if (intRtnVal == 0) {
					szMsg = "["+szMethodName+"] getYdWrkbookmtl data not found";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING);
				}else if (intRtnVal == -2) {
					szMsg = "["+szMethodName+"] getYdWrkbookmtl parameter error";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				}
    			throw new DAOException(SZ_SESSION_NAME + " : " + szMethodName + " - " + szMsg);
			}

	    	//작업예약id로 작업예약Table를 조회한다.
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
	    	rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = ydWrkbookDao.getYdWrkbookWithDel(recInTemp, rsResult);				// intGp == 10
			if (intRtnVal <= 0) {
				if (intRtnVal == 0) {
					szMsg = "["+szMethodName+"] getYdWrkbook : data not found";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING);
				}else if (intRtnVal == -2) {
					szMsg = "["+szMethodName+"] getYdWrkbook : parameter error";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				}
    			throw new DAOException(SZ_SESSION_NAME + " : " + szMethodName + " - " + szMsg);
			}

	    	rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsResult.getRecord());

	    	szYdGp     			= ydUtils.substr(szYdEqpId, 0, 1);
	    	szYdBayGp     		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_AIM_BAY_GP");
	    	szWrkPlanTcar 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PLAN_TCAR");
	    	szCurSchCd    		= szYdGp + szYdBayGp + "TC" + ydUtils.substr(szWrkPlanTcar, 4,2) + "LM";	// 대차 하차 스케쥴
	    	szYdToLocDcsnMtd	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TO_LOC_DCSN_MTD");
			szYdToLocGuide		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TO_LOC_GUIDE");

			/*
	    	 * 2010.12.30 윤재광 - To위치 정합성 체크 추가
	    	 */
			if ("F".equals(szYdToLocDcsnMtd)&& szYdToLocGuide.length()>2) {
				if (!szYdBayGp.equals(szYdToLocGuide.substring(1, 2))) {
					szYdToLocDcsnMtd	= "S";
					szYdToLocGuide		= "";
					szMsg = "대차 하차 작업예약 생성중 To위치 가이드 정보 변경 완료";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				}
			}

	    	//스케줄코드로 스케줄기준Table조회
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_SCH_CD", szCurSchCd);

	    	//스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = ydSchRuleDao.getYdSchrule(recInTemp, rsResult);				// intGp == 0
			if (intRtnVal <= 0) {
				if (intRtnVal == 0) {
					szMsg = "["+szMethodName+"] getYdSchrule : data not found";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING);
				} else if (intRtnVal == -2) {
					szMsg = "["+szMethodName+"] getYdSchrule : parameter error";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				}
				return intRtnVal = -1;
			}

	    	rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsResult.getRecord());
	    	szSchPrior = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_CRN_PRIOR");

	    	//작업예약id 생성
			//작업예약 테이블의 시퀀스를 이용해 작업예약ID를 구해온다.
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
			szWbookId = ydWrkbookDao.getSeqId();		// intGp == 1

	    	//작업예약항목SETTING
	    	recInTemp = JDTORecordFactory.getInstance().create();

	    	recInTemp.setField("YD_WBOOK_ID", 			szWbookId);
	    	recInTemp.setField("REGISTER", 				szModifier);
	    	recInTemp.setField("MODIFIER", 				szModifier);
	    	recInTemp.setField("YD_GP", 				szYdGp);
	    	recInTemp.setField("YD_BAY_GP", 			szYdBayGp);
	    	recInTemp.setField("YD_AIM_YD_GP", 			szYdGp);
	    	recInTemp.setField("YD_AIM_BAY_GP", 		szYdBayGp);
	    	recInTemp.setField("YD_SCH_PRIOR", 			szSchPrior);
	    	recInTemp.setField("YD_SCH_CD", 			szCurSchCd);
	    	recInTemp.setField("YD_WRK_PLAN_TCAR", 		szYdToLocGuide);
	    	recInTemp.setField("YD_TO_LOC_DCSN_MTD", 	szYdToLocDcsnMtd);
	    	recInTemp.setField("YD_TO_LOC_GUIDE", 		szYdToLocGuide);

	    	/*
	    	intRtnVal = ydWrkbookDao.insYdWrkbook(recInTemp);
    		if (intRtnVal <= 0) {
				szMsg = "["+szMethodName+"] parameter error";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return intRtnVal = -1;
    		}
    		*/

    		rsWbooId.addRecord(recInTemp);

	    	//작업예약재료 등록 --> 2013.12.03 대차 상차후 하차스케줄 안만들도록 보완 (정정야드재료 테이블에 YD_WRK_PLAN_TCAR(야드작업계획대차) 항목사용
	    	for (int ii = 1; ii <= rsWrkBookMtl.size(); ii++) {
	    		recInTemp  = JDTORecordFactory.getInstance().create();
	    		recOutTemp = JDTORecordFactory.getInstance().create();

	    		rsWrkBookMtl.absolute(ii);
	    		recOutTemp.setRecord(rsWrkBookMtl.getRecord());
	    	/*
	    		recInTemp.setField("YD_WBOOK_ID",    szWbookId);
	    		recInTemp.setField("REGISTER",       szModifier);
	    		recInTemp.setField("MODIFIER",       szModifier);
	    		recInTemp.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_COL_GP"));
	    		recInTemp.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_BED_NO"));
	    		recInTemp.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_LYR_NO"));
	    		recInTemp.setField("STL_NO",         ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO"));
	    		recInTemp.setField("YD_UP_COLL_SEQ", Integer.toString(rsWrkBookMtl.size()- ii + 1) );

	    		intRtnVal = ydWrkbookMtlDao.insYdWrkbookMtl(recInTemp);
	    		if (intRtnVal <= 0) {
					szMsg = "["+szMethodName+"] parameter error";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return intRtnVal = -1;
	    		}
	    	*/

	    		recInTemp.setField("STL_NO",         	ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO"));
	    		recInTemp.setField("YD_WRK_PLAN_TCAR", 	szYdToLocGuide);
	    		recInTemp.setField("REGISTER",       	szModifier);
	    		recInTemp.setField("MODIFIER",       	szModifier);

	    		intRtnVal = ydStockDao.updYdWrkPlanTcar(recInTemp);
	    		if (intRtnVal <= 0) {
					szMsg = "["+szMethodName+"] parameter error";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return intRtnVal = -1;
	    		}

	    	}

		} catch(Exception e) {
			szMsg = "["+szMethodName+"] 작업예약생성 Exception 발생 :" +e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}

		szMsg = "["+szMethodName+"] 작업예약생성  완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		return intRtnVal = 1;
	} //end of makeY7WrkBook()

	/**
	 * 오퍼레이션명 : 2후판정정야드 대차 도착처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public String procY7TcarStop(JDTORecord msgRecord)throws DAOException  {

		JPlateYdEqpDAO		ydEqpDao	= new JPlateYdEqpDAO();
		JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();
		JPlateYdCrnSchDAO	ydCrnSchDao	= new JPlateYdCrnSchDAO();

		JDTORecordSet 	rsResult		= null;
		JDTORecord    	recOutTemp      = null;
		JDTORecord    	recPara         = null;

	    int 	intRtnVal 				= 0;

	    String 	szMsg              		= "";
	    String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;

	    String 	szMethodName       		= "procY7TcarStop";
	    String 	szOperationName			= "2후판정정야드 대차 도착처리";

		String	szYD_EQP_ID				= "";
		String	szYD_GP					= "";		// 야드구분
		String	szYD_CURR_BAY_GP		= "";		// 대차 현재동  [화면에서 입력 받은 값]
	    String	szYD_START_LOC 	 		= "";		// 대차 출발 위치
		String	szYD_STOP_LOC 			= "";		// 대차 도착 위치
    	String	szLD_BAY				= "";		// 상차동
		String	szUD_BAY				= "";  		// 하차동
		String	szYD_SCH_CD				= "";
    	String	szMODIFIER				= "";
    	String	szOLD_YD_CURR_BAY_GP	= "";		// 실제 대차의 현재동

	    try {

			szMsg = "[" + szOperationName + "] 대차도착처리 START >>>> " + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	    	szYD_CURR_BAY_GP	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CURR_BAY_GP");
	    	szLD_BAY			= ydDaoUtils.paraRecChkNull(msgRecord, "LD_BAY");				// 상차동
	    	szUD_BAY			= ydDaoUtils.paraRecChkNull(msgRecord, "UD_BAY");				// 하차동
	    	szMODIFIER  		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");
	    	if ("".equals(szMODIFIER)) {
	    		szMODIFIER = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER", "YARDSYSTEM");
	    	}
	    	szYD_GP = ydUtils.substr(szYD_EQP_ID, 0, 1);

	    	//-------------------------------------------------------------
	    	// 0. 현재 위치로 크레인 작업지시 생성시에는 대차 도착 처리 못하도록 체크
	    	//-------------------------------------------------------------
	    	szYD_SCH_CD = szYD_GP + "_" + ydUtils.substr(szYD_EQP_ID, 2, 4) + "_M";

	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
	    	recPara  = JDTORecordFactory.getInstance().create();
	    	recPara.setField("YD_SCH_CD", szYD_SCH_CD);
	    	intRtnVal = ydCrnSchDao.getByYdSchCd(recPara, rsResult);
	    	if (intRtnVal > 0) {
		    	szRtnMsg = "크레인 작업지시가 존재하여 도착처리 불가합니다.!";
		    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

	    	//-------------------------------------------------------------
	    	// 1. 설비ID로 대차 현재동을 조회한다.
	    	//-------------------------------------------------------------
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
	    	recPara  = JDTORecordFactory.getInstance().create();

	    	recPara.setField("YD_EQP_ID", szYD_EQP_ID);
	    	intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult);
	    	if (intRtnVal <= 0) {
		    	szRtnMsg = "해당 설비로 데이타 조회시 오류 >>>> " + szYD_EQP_ID;
		    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

	    	rsResult.first();
	    	recOutTemp = rsResult.getRecord();

	    	szOLD_YD_CURR_BAY_GP = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CURR_BAY_GP");
	    //	szYD_CURR_BAY_GP 	 = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CURR_BAY_GP");

	    	/*
	    	if ("FXTC03".equals(szYD_EQP_ID)) {
		    	if ("C".equals(szYD_CURR_BAY_GP)) {
		    		szYD_CURR_BAY_GP 	= "D";
		    		szYD_START_LOC 		= szYD_GP + "C" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    		szYD_STOP_LOC		= szYD_GP + "D" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    	} else {
		    		szYD_CURR_BAY_GP 	= "C";
		    		szYD_START_LOC 		= szYD_GP + "D" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    		szYD_STOP_LOC		= szYD_GP + "C" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    	}
	    	} else {
		    	if ("C".equals(szYD_CURR_BAY_GP)) {
		    		szYD_CURR_BAY_GP 	= "B";
		    		szYD_START_LOC 		= szYD_GP + "C" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    		szYD_STOP_LOC		= szYD_GP + "B" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    	} else {
		    		szYD_CURR_BAY_GP 	= "C";
		    		szYD_START_LOC 		= szYD_GP + "B" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    		szYD_STOP_LOC		= szYD_GP + "C" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    	}
	    	}
	    	*/
	    	// 2013.07.07 --> 화면에서 입력한 현재동으로 도착 처리하도록 보완
	    	if ("FXTC03".equals(szYD_EQP_ID)) {
		    	if ("C".equals(szYD_CURR_BAY_GP)) {
		    		szYD_CURR_BAY_GP 	= "C";
		    		szYD_START_LOC 		= szYD_GP + "D" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    		szYD_STOP_LOC		= szYD_GP + "C" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    	} else {
		    		szYD_CURR_BAY_GP 	= "D";
		    		szYD_START_LOC 		= szYD_GP + "C" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    		szYD_STOP_LOC		= szYD_GP + "D" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    	}
	    	} else {
		    	if ("C".equals(szYD_CURR_BAY_GP)) {
		    		szYD_CURR_BAY_GP 	= "C";
		    		szYD_START_LOC 		= szYD_GP + "B" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    		szYD_STOP_LOC		= szYD_GP + "C" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    	} else {
		    		szYD_CURR_BAY_GP 	= "B";
		    		szYD_START_LOC 		= szYD_GP + "C" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    		szYD_STOP_LOC		= szYD_GP + "B" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    	}
	    	}

	    	//-------------------------------------------------------------
	    	// 2. 대차 현재동구분 변경
	    	//-------------------------------------------------------------
	    	recPara = JDTORecordFactory.getInstance().create();
	    	recPara.setField("YD_EQP_ID", 			szYD_EQP_ID);
	    	recPara.setField("YD_CURR_BAY_GP",		szYD_CURR_BAY_GP);
	    	recPara.setField("MODIFIER", 			szMODIFIER);
	    	recPara.setField("YD_START_LOC",		szYD_START_LOC);		// 대차 출발 위치
			recPara.setField("YD_STOP_LOC",			szYD_STOP_LOC);			// 대차 도착 위치
			recPara.setField("LD_BAY",				szLD_BAY);				// 상차동
			recPara.setField("UD_BAY",				szUD_BAY);				// 하차동

			intRtnVal = ydEqpDao.updYdCurrBayGp(recPara);
	    	if (intRtnVal <= 0) {
		    	szRtnMsg = "대차 현재 동구분 변경시 오류 .." + Integer.toString(intRtnVal);
		    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

    		//-------------------------------------------------------------------------------------
    		// 3. 대차스케줄 항목 업데이트
    		//-------------------------------------------------------------------------------------
	    	szRtnMsg = this.updTcarSchUdInfo(recPara);
	    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szRtnMsg = "대차도착처리시  대차스케줄 수정시 오류 발생 : " + szRtnMsg;
		    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
	    	}

	    	// 설비테이블의 현재동과 화면에서 입력한 현재동이 틀릴 경우만 수행 하도록 보완
	    	if (!szYD_CURR_BAY_GP.equals(szOLD_YD_CURR_BAY_GP)) {
		    	//-------------------------------------------------------------
		    	// 4. 대차 도착 저장위치(베드) 활성화 처리
		    	//  - 저장위치 수정 : 출발위치의 재료의 저장위치를 도착위치로 변경
		    	//-------------------------------------------------------------
		    	szRtnMsg = this.enableFromBed(recPara);
		    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					szRtnMsg = "대차 도착 저장위치(베드) 활성화 처리시 오류 발생 : " + szRtnMsg;
			    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
		    	}

		    	//-------------------------------------------------------------
		    	// 5. 대차 출발 저장위치(베드) 비활성화 처리
		    	//-------------------------------------------------------------
		    	szRtnMsg = this.disableToBed(recPara);
		    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					szRtnMsg = "대차 출발 저장위치(베드) 비활성화 처리시 오류 발생 : " + szRtnMsg;
			    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
		    	}

		    	//-------------------------------------------------------------
		    	// 야드  L2 Interface 처리
		    	//-------------------------------------------------------------
		    	// - 저장위치 제원정보 야드L2전송 (FROM:비활성화)
		    	if (!"FD".equals(ydUtils.substr(szYD_START_LOC, 0, 2))) {		// D동일때는 L2에서 관리 안함으로 SKIP 처리
			    	recPara  = JDTORecordFactory.getInstance().create();
					recPara.setField("MSG_ID", 			"YDY7L001");
					recPara.setField("YD_INFO_SYNC_CD", "3");  // 1:동, 2:SPAN, 3:열, 4:BED
					recPara.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);
					recPara.setField("YD_STK_COL_GP", 	szYD_START_LOC);
					recPara.setField("YD_STK_BED_NO", 	"");
					szRtnMsg = ydDelegate.sendMsg(recPara);

					szMsg = "[Jsp-Session "+szOperationName+" ] 대차출발 위치 비활성화 정보 송신 완료 >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		    	}
		    	if (!"FD".equals(ydUtils.substr(szYD_STOP_LOC, 0, 2))) {		// D동일때는 L2에서 관리 안함으로 SKIP 처리
			    	// - 저장위치 제원정보 야드L2전송 (TO:활성화)
			    	recPara  = JDTORecordFactory.getInstance().create();
					recPara.setField("MSG_ID", 			"YDY7L001");
					recPara.setField("YD_INFO_SYNC_CD", "3");  // 1:동, 2:SPAN, 3:열, 4:BED
					recPara.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);
					recPara.setField("YD_STK_COL_GP", 	szYD_STOP_LOC);
					recPara.setField("YD_STK_BED_NO", 	"");
					szRtnMsg = ydDelegate.sendMsg(recPara);

					szMsg = "[Jsp-Session "+szOperationName+" ] 대차도착 위치 활성화 정보 송신 완료 >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		    	}

				// 대차위에(TO위치) 재료가 존재시에만 실행
				rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
		    	recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP",	szYD_STOP_LOC);
				recPara.setField("YD_STK_BED_NO",	"");
				recPara.setField("YD_EQP_WRK_SH",	"1");
				intRtnVal = ydStkLyrDao.getStlNoTopCnt(recPara, rsResult);

				if (intRtnVal > 0) {

			    	if (!"FD".equals(ydUtils.substr(szYD_STOP_LOC, 0, 2))) {		// D동일때는 L2에서 관리 안함으로 SKIP 처리
				    	// - 저장품 제원정보 야드L2전송
				    	recPara  = JDTORecordFactory.getInstance().create();
						recPara.setField("JMS_TC_CD", 		"YDY7L002");                            // TC-CODE
						recPara.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);		// 야드구분
						recPara.setField("YD_STK_COL_GP", 	szYD_STOP_LOC);                         // 야드적치열구분
						recPara.setField("YD_STK_BED_NO", 	"");    								// 야드적치BED번호
						recPara.setField("YD_INFO_SYNC_CD", "3");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
						recPara.setField("STL_NO", 			"");	        						// 재료번호
						szRtnMsg = ydDelegate.sendMsg(recPara);

						szMsg = "[Jsp-Session "+szOperationName+" ] 대차도착 위치 제원정보 정보 송신 완료 >>>> " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			    	}

			    	//-------------------------------------------------------------
			    	// 조업  L3 Interface 처리
			    	//-------------------------------------------------------------
			    	// - 조업L3 저장위치 변경정보 송신
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("MSG_ID", 			"YDPPJ011");
					recPara.setField("YD_STK_COL_FR", 	szYD_START_LOC);						// From적치열
					recPara.setField("YD_STK_BED_FR", 	"");									// From적치BED
					recPara.setField("YD_STK_COL_TO", 	szYD_STOP_LOC);							// TO적치열
		        	recPara.setField("YD_STK_BED_TO", 	"");									// TO적치BED
		        	recPara.setField("YD_EQP_WRK_SH", 	"20");									// 야드설비작업매수

		        //	szRtnMsg = ydDelegate.sendMsg(recPara);
		            szRtnMsg = JPlateYdCommonUtils.sendL3YDPPJ011(recPara);

					szMsg = "[ " +szOperationName + "] 조업L3 저장위치 변경정보 송신 END >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				} else {
					szMsg = "[ " +szOperationName + "] 도착위치에 재료정보가 미존재하여 저장품 제원정보 송신 SKIP";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				}
	    	}

	        if (szYD_CURR_BAY_GP.equals(szLD_BAY)) {				// 도착동이 상차동 이면 상차 스케쥴 호출

		    	//-------------------------------------------------------------
		    	// 대차 상차 스케쥴 기동
		    	//-------------------------------------------------------------
				szMsg = "[Jsp-Session "+szOperationName+" ] 대차 상차 스케쥴 기동 .... START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		    	szRtnMsg = this.procY7UdTcarSch(msgRecord);

				szMsg = "[Jsp-Session "+szOperationName+" ] 대차 상차 스케쥴 기동 .... END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        } else if (szYD_CURR_BAY_GP.equals(szUD_BAY)) {			// 도착동이 상차동 이면 상차 스케쥴 호출

		    	//-------------------------------------------------------------
		    	// 대차 하차 스케쥴 기동
		    	//-------------------------------------------------------------
				szMsg = "[Jsp-Session "+szOperationName+" ] 대차 하차 스케쥴 기동 .... START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				/*
				 * 2015.01.07 강찬호 하차작업생성 SKIP요청
				 */
		    	//szRtnMsg = this.procY7LdTcarSch(msgRecord);
 
				szMsg = "[Jsp-Session "+szOperationName+" ] 대차 하차 스케쥴 기동 .... END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        }

		} catch(Exception e) {
			szRtnMsg = "대차도착처리시  Exception 발생 :" +e.getMessage();
	    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szRtnMsg;
		}

		szMsg = "[" + szOperationName + "] 대차도착처리 END >>>> ";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	}

	/**
	 * 오퍼레이션명 : 대차 도착 저장위치(베드) 활성화 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public String enableFromBed(JDTORecord msgRecord)throws DAOException  {

		JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();
		JPlateYdStkBedDAO	ydStkBedDao	= new JPlateYdStkBedDAO();
		JPlateYdStkColDAO	ydStkColDao	= new JPlateYdStkColDAO();

		JDTORecord    	recPara         = null;

	    int 	intRtnVal 				= 0;
	    String 	szMsg              		= "";
	    String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;

	    String 	szMethodName       		= "enableFromBed";
	    String 	szOperationName			= "대차 도착 저장위치(베드) 활성화 처리";

		String	szMODIFIER				= "";
    	String	szYD_START_LOC			= "";		// 대차 출발 위치 (대차 상차 정지 위치)
		String	szYD_STOP_LOC			= "";  		// 대차 도착 위치 (대차 하차 정지 위치)

	    try {

			szMsg = "[" + szOperationName + "] 대차도착 BED 활성화 START >>>> " + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMODIFIER  	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");
			if ("".equals(szMODIFIER)) {
				szMODIFIER  = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER", "YARDSYSTEM");
			}
	    	szYD_START_LOC	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_START_LOC");		// 대차 출발 위치
	    	szYD_STOP_LOC 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_STOP_LOC");		// 대차 도착 위치

	    	if (szYD_START_LOC.equals(szYD_STOP_LOC) || "".equals(szYD_START_LOC) || "".equals(szYD_STOP_LOC)) {
	    		szRtnMsg = "대차도착 적치단 활성화 처리시 오류 >>>> 출발위치 :: " + szYD_START_LOC + ", 도착위치:: " + szYD_STOP_LOC;
	    		szMsg = "[" + szOperationName + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

	    	recPara  = JDTORecordFactory.getInstance().create();
	    	recPara.setField("YD_STK_COL_GP_FR",	szYD_START_LOC);
	    	recPara.setField("YD_STK_COL_GP_TO",	szYD_STOP_LOC);
	    	recPara.setField("YD_STK_LYR_ACT_STAT",	"E");					// C:Close(비활성화), E:적치 가능, F:적치 완료, N:사용 불가
	    	recPara.setField("MODIFIER", 			szMODIFIER);
	    	recPara.setField("OCPY_CHK_FLAG",		"N");					// 점유베드 체크 안함

	    	// 대차 도착 저장위치(적치단) 활성화 처리 및 재료적치정보 복사
	    	intRtnVal = ydStkLyrDao.copyTcarFromBed(recPara);
	    	if (intRtnVal <= 0) {
	    		szRtnMsg = "대차도착 적치단 활성화 처리시 오류 :: " + Integer.toString(intRtnVal);
	    		szMsg = "[" + szOperationName + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

	    	// 대차 도착 저장위치(베드) 활성화 처리
	    	recPara  = JDTORecordFactory.getInstance().create();
	    	recPara.setField("YD_STK_COL_GP",		szYD_STOP_LOC);
	    	recPara.setField("YD_STK_BED_NO",		"");
	    	recPara.setField("YD_STK_BED_ACT_STAT",	"L");					// C:Close(비활성화), L:적치 가능(활성), N:사용 불가
	    	recPara.setField("MODIFIER", 			szMODIFIER);

	    	intRtnVal = ydStkBedDao.updYdStkBedActStat(recPara);
	    	if (intRtnVal <= 0) {
	    		szRtnMsg = "대차도착 BED 활성화 처리시 오류 :: " + Integer.toString(intRtnVal);
	    		szMsg = "[" + szOperationName + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

	    	// 대차 도착 저장위치(적치열) 활성화 처리
	    	recPara  = JDTORecordFactory.getInstance().create();
	    	recPara.setField("YD_STK_COL_GP",		szYD_STOP_LOC);
	    	recPara.setField("YD_STK_COL_ACT_STAT",	"L");					// C:Close(비활성화), L:적치 가능(활성), N:사용 불가
	    	recPara.setField("MODIFIER", 			szMODIFIER);

	    	intRtnVal = ydStkColDao.updYdStkColActStat(recPara);
	    	if (intRtnVal <= 0) {
	    		szRtnMsg = "대차도착 적치열 활성화 처리시 오류 :: " + Integer.toString(intRtnVal);
	    		szMsg = "[" + szOperationName + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

		} catch(Exception e) {
			szRtnMsg = "대차도착 BED 활성화 처리시  Exception 발생 :" +e.getMessage();
	    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szRtnMsg;
		}

		szMsg = "[" + szOperationName + "] 대차도착 BED 활성화 END >>>> " + szYD_STOP_LOC;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	}

	/**
	 * 오퍼레이션명 : 대차 출발 저장위치(베드) 비활성화 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public String disableToBed(JDTORecord msgRecord)throws DAOException  {

		JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();
		JPlateYdStkBedDAO	ydStkBedDao	= new JPlateYdStkBedDAO();
		JPlateYdStkColDAO	ydStkColDao	= new JPlateYdStkColDAO();

		JDTORecord    	recPara         = null;

	    int 	intRtnVal 				= 0;
	    String 	szMsg              		= "";
	    String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;

	    String 	szMethodName       		= "enableFromBed";
	    String 	szOperationName			= "대차 출발 저장위치(베드) 비활성화 처리";

		String	szMODIFIER				= "";
    	String	szYD_START_LOC			= "";		// 대차 상차 정지 위치

	    try {

			szMsg = "[" + szOperationName + "] 대차출발 BED 비활성화 START >>>> " + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    	szMODIFIER  	= ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");
			if ("".equals(szMODIFIER)) {
				szMODIFIER  = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER", "YARDSYSTEM");
			}

	    	szYD_START_LOC	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_START_LOC");

	    	if ("".equals(szYD_START_LOC)) {
	    		szRtnMsg = "대차 출발위치의 베드 비활성화  처리시 오류 >>>> 출발위치 :: " + szYD_START_LOC;
	    		szMsg = "[" + szOperationName + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

	    	recPara = JDTORecordFactory.getInstance().create();
	    	recPara.setField("STL_NO",				"");
	    	recPara.setField("MODIFIER",			szMODIFIER);
	    	recPara.setField("YD_STK_LYR_ACT_STAT",	"C");					// C:Close(비활성화), E:적치 가능, F:적치 완료, N:사용 불가
	    	recPara.setField("YD_STK_LYR_MTL_STAT",	"E");
	    	recPara.setField("YD_STK_COL_GP",		szYD_START_LOC);
	    	recPara.setField("YD_STK_BED_NO",		"");
	    	recPara.setField("YD_OCPY_BED_GP",		"");
	    	recPara.setField("YD_OCPY_STK_BED_NO",	"");
	    	recPara.setField("YD_OCPY_STK_LYR_NO",	"");
	    	recPara.setField("OCPY_CHK_FLAG",		"N");					// 점유베드 체크 안함

	    	// 대차 도착 저장위치(적치단) 비활성화 처리
	    	intRtnVal = ydStkLyrDao.updYdStkLyrActStat(recPara);
	    	if (intRtnVal <= 0) {
	    		szRtnMsg = "대차 출발위치의 베드 비활성화 처리시 오류 :: " + szYD_START_LOC;
	    		szMsg = "[" + szOperationName + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

	    	// 대차 도착 저장위치(베드) 비활성화 처리
	    	recPara  = JDTORecordFactory.getInstance().create();
	    	recPara.setField("YD_STK_COL_GP",		szYD_START_LOC);
	    	recPara.setField("YD_STK_BED_NO",		"");
	    	recPara.setField("YD_STK_BED_ACT_STAT",	"C");					// C:Close(비활성화), L:적치 가능(활성), N:사용 불가
	    	recPara.setField("MODIFIER", 			szMODIFIER);

	    	intRtnVal = ydStkBedDao.updYdStkBedActStat(recPara);
	    	if (intRtnVal <= 0) {
	    		szRtnMsg = "대차도착 BED 비활성화 처리시 오류 :: " + Integer.toString(intRtnVal);
	    		szMsg = "[" + szOperationName + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

	    	// 대차 도착 저장위치(적치열) 비활성화 처리
	    	recPara  = JDTORecordFactory.getInstance().create();
	    	recPara.setField("YD_STK_COL_GP",		szYD_START_LOC);
	    	recPara.setField("YD_STK_COL_ACT_STAT",	"C");					// C:Close(비활성화), L:적치 가능(활성), N:사용 불가
	    	recPara.setField("MODIFIER", 			szMODIFIER);

	    	intRtnVal = ydStkColDao.updYdStkColActStat(recPara);
	    	if (intRtnVal <= 0) {
	    		szRtnMsg = "대차도착 적치열 비활성화 처리시 오류 :: " + Integer.toString(intRtnVal);
	    		szMsg = "[" + szOperationName + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

		} catch(Exception e) {
			szRtnMsg = "대차출발 BED 비활성화 처리시  Exception 발생 :" +e.getMessage();
	    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szRtnMsg;
		}

		szMsg = "[" + szOperationName + "] 대차출발 BED 비활성화 END >>>> " + szYD_START_LOC;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	}

	/**
	 * 오퍼레이션명 : 대차스케줄 항목 업데이트 (대차도착)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public String updTcarSchUdInfo(JDTORecord msgRecord)throws DAOException  {

		JPlateYdTcarSchDAO	ydTcarSchDao	= new JPlateYdTcarSchDAO();

		JDTORecordSet 	rsResult		= null;
		JDTORecord    	recPara         = null;
		JDTORecord    	recTcar         = null;

	    int 	intRtnVal 				= 0;

	    String 	szMsg              		= "";
	    String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;

	    String 	szMethodName       		= "updTcarSchUdInfo";
	    String 	szOperationName			= "대차스케줄 항목 업데이트";

		String	szYD_EQP_ID				= "";
		String	szYD_CURR_BAY_GP		= "";
    	String	szLD_BAY				= "";		// 상차동
		String	szUD_BAY				= "";  		// 하차동
    	String	szYD_CARLD_STOP_LOC		= "";		// 대차상차정지위치
		String	szYD_CARUD_STOP_LOC		= "";  		// 대차하차정지위치
		String	szYD_GP					= "";
		String	szMODIFIER				= "";

	    try {

			szMsg = "[" + szOperationName + "] 대차스케줄 항목 업데이트 START >>>> " + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szYD_EQP_ID 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	    	szYD_CURR_BAY_GP= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CURR_BAY_GP");
	    	szLD_BAY		= ydDaoUtils.paraRecChkNull(msgRecord, "LD_BAY");
	    	szUD_BAY		= ydDaoUtils.paraRecChkNull(msgRecord, "UD_BAY");
	    	szMODIFIER  	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");
	    	if ("".equals(szMODIFIER)) {
	    		szMODIFIER 	= ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER", "YARDSYSTEM");
	    	}
	    	szYD_GP = ydUtils.substr(szYD_EQP_ID, 0, 1);

	    	if (!"".equals(szLD_BAY)) {
	    		szYD_CARLD_STOP_LOC = szYD_GP + szLD_BAY + ydUtils.substr(szYD_EQP_ID, 2, 4);	// 대차 상차 정지위치
	    	}
	    	if (!"".equals(szUD_BAY)) {
	    		szYD_CARUD_STOP_LOC = szYD_GP + szUD_BAY + ydUtils.substr(szYD_EQP_ID, 2, 4);	// 대차 하차 정지위치
	    	}

    		rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
	    	recTcar	 = JDTORecordFactory.getInstance().create();
	    	recPara  = JDTORecordFactory.getInstance().create();
	    	recPara  = JDTORecordFactory.getInstance().create();
	    	recPara.setField("YD_EQP_ID", 			szYD_EQP_ID);

	    	intRtnVal = ydTcarSchDao.getByYdEqpId(recPara, rsResult);
	    	if (intRtnVal <= 0) {
	    		szRtnMsg = "대차스케줄 항목 조회시 오류 :: " + szYD_EQP_ID;
	    		szMsg = "[" + szOperationName + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

	    	rsResult.first();
	    	recTcar = rsResult.getRecord();

	    	// UPDATE 항목 SET
	    	if ("".equals(szLD_BAY) || "".equals(szUD_BAY)) {
		    	recTcar.setField("YD_EQP_WRK_STAT", 		"U");					// (L:영차 ,U:공차)
	        	recTcar.setField("YD_CAR_PROG_STAT", 		"0");					// 상차도착 (0:상차대기, 1:상차출발, 2:상차도착, 3:상차검수, 4:상차개시, 5:상차완료, A:하차출발, B:하차도착, C:하차검수, D:하차개시, E:하차완료)
	    	} else {
		    	if (szYD_CURR_BAY_GP.equals(szLD_BAY)) {
			    	recTcar.setField("YD_EQP_WRK_STAT", 	"U");					// (L:영차 ,U:공차)
		        	recTcar.setField("YD_CAR_PROG_STAT", 	"2");					// 상차도착 (0:상차대기, 1:상차출발, 2:상차도착, 3:상차검수, 4:상차개시, 5:상차완료, A:하차출발, B:하차도착, C:하차검수, D:하차개시, E:하차완료)
		    	} else {
			    	recTcar.setField("YD_EQP_WRK_STAT", 	"L");
		        	recTcar.setField("YD_CAR_PROG_STAT", 	"B");					// 하차도착 (0:상차대기, 1:상차출발, 2:상차도착, 3:상차검수, 4:상차개시, 5:상차완료, A:하차출발, B:하차도착, C:하차검수, D:하차개시, E:하차완료)
		    	}
	    	}

        	recTcar.setField("YD_CARLD_STOP_LOC", 		szYD_CARLD_STOP_LOC);		// 대차 상차 정지 위치
			recTcar.setField("YD_CARUD_STOP_LOC",		szYD_CARUD_STOP_LOC);  		// 대차 하차 정지 위치
	    	recTcar.setField("MODIFIER", 				szMODIFIER);

	    	intRtnVal = ydTcarSchDao.updYdCarLdUdInfo(recTcar);
	    	if (intRtnVal <= 0) {
	    		szRtnMsg = "대차스케줄 항목 업데이트 UPDATE시 오류 :: " + Integer.toString(intRtnVal);
	    		szMsg = "[" + szOperationName + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

		} catch(Exception e) {
			szRtnMsg = "대차스케줄 항목 업데이트 처리시  Exception 발생 :" +e.getMessage();
	    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szRtnMsg;
		}

		szMsg = "[" + szOperationName + "] 대차스케줄 항목 업데이트 END";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	}

	//---------------------------------------------------------------------------
	/**********************************************************
	* 1후판정정추가 SJH16 
	**********************************************************/	
	/**
	 * 오퍼레이션명 : 1후판정정야드 대차 스케줄
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public String procY2TcarSch(JDTORecord msgRecord)throws DAOException  {

	    int 	intRtnVal 				= 0 ;

	    String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
	    String 	szMsg              		= "";
	    String 	szMethodName       		= "procY2TcarSch";
	    String 	szOperationName			= "1후판정정 대차스케줄";

	    //상하차 구분
	    String 	szLdUdGp				= "";

        String szRcvTcCode = ydUtils.getTcCode(msgRecord);
        if (szRcvTcCode==null || "".equals(szRcvTcCode)) {
        	szRtnMsg = "TC Code Error ("+szRcvTcCode+")";
        	szMsg = "["+szOperationName+"] " + szRtnMsg;
        	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
        	return szRtnMsg;
        }

        if (bDebugFlag) {
            szMsg = "["+szOperationName+"] [DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
        }

	    try {
	    	szMsg = "["+szOperationName+"] ------------------- 메소드 시작 -------------------";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

            szLdUdGp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_LD_UD_GP");

	    	//------------------------------------------------------------------------------------------
    		// 권상 실적 처리중 호출한 경우 - 하차 시
	    	//------------------------------------------------------------------------------------------
    		if ("U".equals(szLdUdGp)) {

    			szMsg = "["+szOperationName+"] 권상실적 처리 시 호출 - 대차하차스케줄 모듈 시작";
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    			//대차 하차스케줄 호출
// 사용안함            
//                szRtnMsg = this.procY7AfterUpCrnWrk(msgRecord);

    			szMsg = "["+szOperationName+"] 권상실적 처리 시 호출 - 대차하차스케줄 모듈 완료 - 반환값 : " + intRtnVal;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    		//------------------------------------------------------------------------------------------
    		// 권하 실적 처리중 호출한 경우 - 상차 시
    		//------------------------------------------------------------------------------------------
    		} else if ("L".equals(szLdUdGp)){

    			szMsg = "["+szOperationName+"] 권하실적 처리 시 호출 - 대차상차스케줄 모듈 시작";
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    			//대차 상차 스케줄 호출
                szRtnMsg = this.procY7AfterDnCrnWrk(msgRecord);

    			szMsg = "["+szOperationName+"] 권하실적 처리 시 호출 - 대차상차스케줄 모듈 완료 - 반환값 : " + intRtnVal;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}
		} catch(Exception e) {

			szMsg = "["+szOperationName+"] 예외 발생 : " +e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}

		szMsg = "["+szOperationName+"] ------------------- 메소드 끝 -------------------";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return  JPlateYdConst.RETN_CD_SUCCESS;
	} //end of procY2TcarSch()

	/**
	 * 오퍼레이션명 : 대차 하차 스케줄  (대차 도착처리후 호출)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public String procY2LdTcarSch(JDTORecord msgRecord)throws DAOException  {

		//동간이적요구시 화면에서  송신하는 경우
		JPlateYdEqpDAO      	ydEqpDao        = new JPlateYdEqpDAO();
		JPlateYdSchRuleDAO		ydSchRuleDao    = new JPlateYdSchRuleDAO();
		JPlateYdWrkbookDAO  	ydWrkbookDao    = new JPlateYdWrkbookDAO();
		JPlateYdStkBedDAO   	ydStkBedDao		= new JPlateYdStkBedDAO();
		JPlateYdWrkbookMtlDAO	ydWrkbookMtlDao	= new JPlateYdWrkbookMtlDAO();
		JPlateYdStockDAO 		ydStockDao		= new JPlateYdStockDAO();

		JDTORecordSet 	rsResult        = null;
//		JDTORecordSet 	rsWBook        	= null;
		JDTORecord    	recPara       	= null;
		JDTORecord    	recOutTemp      = null;
		JDTORecord    	recSchPara		= null;
	    int intRtnVal 					= 0 ;

	    String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
	    String 	szMsg              		= "";
	    String 	szMethodName       		= "procY2LdTcarSch";
	    String 	szOperationName			= "대차 하차 스케줄";

	    String 	szTcarEqpId             = "";					// 대차설비 ID
	    String 	szYdSchCd               = "";					// 스케쥴코드

	    String 	szYdTcarCurrBayGp   	= "";					// 대차 현재동

	    String	szYdWbookId				= "";					// 작업예약 ID
	    String	szYdGp					= "";					// 야드구분
	    String 	szYdBayGp				= "";					// 야드동구분
	    String	szYdWrkCrnPrior			= "";					// 야드스케쥴우선순위
	    String	szYdSchProhExn			= "";					// 스케쥴금지여부
		String	szYdWrkCrn				= "";					// 주작업크레인 :: YdSchRule에 등록된 크레인Id
	    String	szStlNo					= "";					// 재료번호
	    String	szYdStkColGp			= "";					// 야드적치열구분
		String	szYdStkBedNo 			= "";					// 야드적치Bed번호
		String	szYdStkLyrNo 			= "";					// 야드적치단번호
		String	szYdToLocGuide			= "";					// 야드To위치 Guide
	    String 	szModifier				= "";					// 수정자
	    String	szYdWrkPlanTcar			= "";					// 야드작업계획대차 (대차하차위치)

	    int		iWBookInsCnt			= 0;					// 작업예약 등록 건수
	    int		iWBookSkipCnt			= 0;					// 기존작업예약 존재 .. SKIP CNT

		String[]	arrWbookId 			= new String[JPlateYdConst.MAX_CRN_SCH_CNT];
		String[]	arrToLocGuide 		= new String[JPlateYdConst.MAX_CRN_SCH_CNT];

	    try {

	    	szMsg = "["+szOperationName+"] 대차 하차 스케줄 .... START >>>> " + msgRecord.toString();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    	szTcarEqpId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");		//대차설비ID
//	    	szYdToBay 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_BAY");		//공대차출발 시 사용자가 지정한 목표동(값 없을수 있슴)
	    	szModifier	= ydDaoUtils.paraRecModifier(msgRecord);					//수정자

	    	//-------------------------------------------------------------------------------------
	    	//	대차설비ID로 설비테이블을 조회한다.
	    	//-------------------------------------------------------------------------------------
	    	szMsg = "["+szOperationName+"] 대차 하차시 .. 대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    		rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
    		recPara  = JDTORecordFactory.getInstance().create();
    		recPara.setField("YD_EQP_ID", 			szTcarEqpId);

    		intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult);		// intGp == 0

    		szMsg = "["+szOperationName+"] 대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if (intRtnVal <= 0) {
    			szRtnMsg = "대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 시 오류발생 - 반환값 : " + intRtnVal;
    			szMsg = "["+szOperationName+"] " + szRtnMsg;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			return szRtnMsg;
			}

			rsResult.absolute(1);
    		recOutTemp = JDTORecordFactory.getInstance().create();
    		recOutTemp.setRecord(rsResult.getRecord());

    		szYdTcarCurrBayGp	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CURR_BAY_GP");	//현재동

    		//-------------------------------------------------------------------------------------
    		//	작업예약ID가 미존재시 대차위치의 재료를 조회하여 작업예약을 등록 한다. 대차설비ID :: FXTC01
    		//-------------------------------------------------------------------------------------
    		szYdGp		 = ydUtils.substr(szTcarEqpId, 0, 1);
    		szYdBayGp	 = szYdTcarCurrBayGp;
    		szYdStkColGp = szYdGp + szYdTcarCurrBayGp + ydUtils.substr(szTcarEqpId, 2, 4);
    		szYdSchCd	 = szYdStkColGp + "LM";		// 대차하차 스케쥴코드 : FBTC01LM

			//-----------------------------------------------------
			// 스케줄기준 정보 Check (야드스케쥴금지유무, 야드스케쥴우선순위)
			//-----------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
    		recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_SCH_CD", szYdSchCd); //야드스케쥴코드

			ydSchRuleDao.getYdSchrule(recPara, rsResult);

			if (rsResult != null && rsResult.size() > 0) {
				szYdSchProhExn  = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_SCH_PROH_EXN");
				szYdWrkCrnPrior = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_CRN_PRIOR");
				szYdWrkCrn		= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_CRN");
			}

			if ("".equals(szYdSchProhExn)) {
				//스케줄기준 Table 정보 Check
				szRtnMsg = "오류:스케쥴코드[" + szYdSchCd + "] 정보 없음";
    			szMsg = "["+szOperationName+"] " + szRtnMsg;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			return szRtnMsg;
			} else if ("Y".equals(szYdSchProhExn)) {
				//스케줄 금지여부 Check
				szRtnMsg = "오류:스케쥴코드[" + szYdSchCd + "] 기동금지";
    			szMsg = "["+szOperationName+"] " + szRtnMsg;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			return szRtnMsg;
			}

			//-----------------------------------------------------
			// 대차위에 재료정보 조회
			//-----------------------------------------------------
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("yd");
    		recPara   = JDTORecordFactory.getInstance().create();
    		recPara.setField("YD_STK_COL_GP", szYdStkColGp);
    		intRtnVal = ydStkBedDao.getTcarMtlWithWBookId(recPara, rsResult);
    		if (intRtnVal <= 0) {
    			szRtnMsg = "대차위에 재료정보가 미존재하여 .. 대차 스케쥴기동 SKIP 처리";
    			szMsg = "["+szOperationName+"] " + szRtnMsg;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    		//	return szRtnMsg;
    			return JPlateYdConst.RETN_CD_SUCCESS;
    		}

    		rsResult.first();
    		for(int ii=0; ii<rsResult.size(); ii++) {
    			rsResult.absolute(ii+1);
	    		recOutTemp = rsResult.getRecord();

	    		// 해당 재료가 작업예약 미등록시에만 작업 예약 등록 처리
	    		if ("".equals(ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID"))) {

		    		szStlNo 	 	= ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO");
		    		szYdStkColGp 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_COL_GP");
		    		szYdStkBedNo 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_BED_NO");
		    		szYdStkLyrNo 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_LYR_NO");
		    		szYdWrkPlanTcar = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PLAN_TCAR");

	    			//---------------------------------------------------------------------
		    		// 2013.07.13 매번 작업 예약 ID 생성 하도록 변경
	    			//---------------------------------------------------------------------
	    			szYdWbookId = ydWrkbookDao.getSeqId();

	    			if ("".equals(szYdWbookId)) {
		    			szRtnMsg = "오류:작업예약ID 생성 실패";
		    			szMsg = "["+szOperationName+"] " + szRtnMsg;
		    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
		    			return szRtnMsg;
	    			}

					szMsg = "["+szOperationName+"] ----------- 작업예약 ID 생성 :: " + szYdWbookId + ", 건수 :: " + iWBookInsCnt;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					if ("".equals(szYdWrkPlanTcar)) {
						szYdToLocGuide = szYdGp+szYdBayGp;
					} else {
						szYdToLocGuide = szYdWrkPlanTcar;
					}

	    			//---------------------------------------
	    			// 작업예약 등록
	    			//---------------------------------------
	    			recPara.setField("YD_WBOOK_ID", 		szYdWbookId); 		//야드작업예약ID
	    			recPara.setField("YD_GP", 				szYdGp); 			//야드구분
	    			recPara.setField("YD_BAY_GP", 			szYdBayGp); 		//야드동구분
	    			recPara.setField("YD_SCH_CD", 			szYdSchCd); 		//야드스케쥴코드
	    			recPara.setField("YD_SCH_PRIOR", 		szYdWrkCrnPrior); 	//야드스케쥴우선순위
	    			recPara.setField("YD_SCH_PROG_STAT", 	"W"); 				//야드스케쥴진행상태(스케줄수행대기)
	    			recPara.setField("YD_SCH_ST_GP", 		"M"); 				//야드스케쥴기동구분(Manual 작업)
	    			recPara.setField("YD_SCH_REQ_GP", 		"X"); 				//야드스케쥴요청구분(강제권상요구)
	    			recPara.setField("YD_AIM_YD_GP", 		szYdGp); 			//야드목표야드구분
	    			recPara.setField("YD_AIM_BAY_GP", 		szYdTcarCurrBayGp); //야드목표동구분
	    			recPara.setField("YD_TO_LOC_DCSN_MTD", 	"S"); 				//야드TO위치결정방법(스케줄기준적용)
	    			recPara.setField("YD_TO_LOC_GUIDE",		szYdToLocGuide);	//야드To위치Guide
	    			recPara.setField("REGISTER",			szModifier);
	    			recPara.setField("MODIFIER",			szModifier);

	    			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
	    			if (intRtnVal <= 0) {
		    			szRtnMsg = "오류:작업예약 등록 실패 >>>> " + Integer.toString(intRtnVal);
		    			szMsg = "["+szOperationName+"] " + szRtnMsg;
		    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
		    			return szRtnMsg;
	    			}

	    			//---------------------------------------
	    			// 작업예약 재료 등록
	    			//---------------------------------------
					recPara.setField("STL_NO", 				szStlNo); 			//재료번호
					recPara.setField("YD_STK_COL_GP",		szYdStkColGp);
		    		recPara.setField("YD_STK_BED_NO",		szYdStkBedNo);
					recPara.setField("YD_STK_LYR_NO", 		szYdStkLyrNo); 		//야드적치단번호

					ydWrkbookMtlDao.insYdWrkbookMtl(recPara);

					//저장품 작업예약정보 수정 (TB_YD_STOCK)
					ydStockDao.updYdStockWbook(recPara);

	    		} else {

					szYdWbookId 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID");
					szYdToLocGuide 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TO_LOC_GUIDE");

					iWBookSkipCnt ++;
	    			szMsg = "["+szOperationName+"] 기존 작업예약이 존재하여 INSERT SKIP >>>> " + ii + " :: " + szYdWbookId;
	    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

	    		}

	    		if (ii < JPlateYdConst.MAX_CRN_SCH_CNT) {
	    			arrWbookId[ii] 		= szYdWbookId;
		    		arrToLocGuide[ii] 	= szYdToLocGuide;
	    		}
				iWBookInsCnt ++;


    		} // end for

			szMsg = "["+szOperationName+"] 작업예약 등록 완료 >>>> 등록건수 :: " + Integer.toString(iWBookInsCnt) + ", SKIP CNT :: " + iWBookSkipCnt;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			for(int ii=0; ii<iWBookInsCnt; ii++) {

    			//-----------------------------------------------------
	    		// 작업예약 건수 조회하여 MAX건 초과시는 크레인 스케쥴 기동 SKIP
    			//-----------------------------------------------------
	    		if (ii >= JPlateYdConst.MAX_CRN_SCH_CNT) {
	    			szMsg = "["+szOperationName+"] 작업예약 "+JPlateYdConst.MAX_CRN_SCH_CNT+"건 등록  이후 재료 SKIP .... ii >>>> " + Integer.toString(ii);
	    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	    			break;
	    		}

				if (ii >= arrWbookId.length || arrWbookId[ii] == null || "".equals(arrWbookId[ii])) {
	    			szMsg = "["+szOperationName+"] 작업예약  오류로 SKIP 처리 >>>> " + arrWbookId[ii];
	    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					break;
				}

		    	//------------------------------------------------
				// 크레인 스케줄 호출
	    		//------------------------------------------------
				if ("".equals(arrToLocGuide[ii])) {
					arrToLocGuide[ii] = szYdGp + szYdBayGp;
				}
				recSchPara 	= JDTORecordFactory.getInstance().create();
				recSchPara.setField("MSG_ID", 			"YDYDJ");			//TC코드
				recSchPara.setField("YD_EQP_ID", 		szYdWrkCrn);		//크레인설비ID
				recSchPara.setField("YD_SCH_CD",		szYdSchCd);			//크레인스케줄코드
				recSchPara.setField("YD_WBOOK_ID",		arrWbookId[ii]);	//작업예약ID
				recSchPara.setField("REGISTER", 		szModifier);
				recSchPara.setField("MODIFIER", 		szModifier);
				recSchPara.setField("YD_TO_LOC_GUIDE",	arrToLocGuide[ii]);	//야드To위치Guide
				if (ii==0) {
					recSchPara.setField("CHK_FROM_LOC", "Y");
				} else {
					recSchPara.setField("CHK_FROM_LOC", "N");				//권상위치에 작업예약 존재여부 체크 하지 안도록 SET
				}

				szMsg    = "["+szOperationName+"] ----------- 크레인 스케줄기동 START >>>> idx :: " + ii + ", 작업예약ID :: " + arrWbookId[ii];
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		        EJBConnector ejbConn = new EJBConnector("default", "JPlateYdCrnSchYdPSeEJB", this);
		        szRtnMsg = (String)ejbConn.trx("procCrnSchMainYdP", new Class[] { JDTORecord.class }, new Object[] { recSchPara });

				szMsg    = "["+szOperationName+"] ----------- 크레인 스케줄기동 END :: " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}  // end for

			szMsg = "["+szOperationName+"] 대차 하차 스케줄 완료";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch(Exception e) {

			szMsg = "["+szOperationName+"] 대차 하차 스케줄  Error : " +e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}

		szMsg = "["+szOperationName+"] 대차 하차 스케줄 .... END";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;

	} //end of procY2LdTcarSch()


	/**
	 * 오퍼레이션명 : 대차 상차 스케줄  (대차 도착처리후 호출)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public String procY2UdTcarSch(JDTORecord msgRecord)throws DAOException  {

		//동간이적요구시 화면에서  송신하는 경우
		JPlateYdEqpDAO      	ydEqpDao        = new JPlateYdEqpDAO();
		JPlateYdSchRuleDAO		ydSchRuleDao    = new JPlateYdSchRuleDAO();
		JPlateYdWrkbookDAO  	ydWrkbookDao    = new JPlateYdWrkbookDAO();

		JDTORecordSet 	rsResult        = null;
		JDTORecord    	recPara       	= null;
		JDTORecord    	recOutTemp      = null;
		JDTORecord		recSchPara		= null;
	    int intRtnVal 					= 0 ;

	    String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
	    String 	szMsg              		= "";
	    String 	szMethodName       		= "procY2UdTcarSch";
	    String 	szOperationName			= "대차 상차 스케줄";

	    String 	szTcarEqpId             = "";					// 대차설비 ID
	    String 	szYdSchCd               = "";					// 스케쥴코드

	    String 	szYdTcarCurrBayGp   	= "";					// 대차 현재동

	    String	szYdWbookId				= "";					// 작업예약 ID
	    String	szYdGp					= "";					// 야드구분
	    String	szYdSchProhExn			= "";					// 스케쥴금지여부
		String	szYdWrkCrn				= "";					// 주작업크레인 :: YdSchRule에 등록된 크레인Id
	    String	szYdStkColGp			= "";					// 야드적치열구분
	    String 	szModifier				= "";					// 수정자
	    String	szYdToLocGuide			= "";
	    int		iWBookInsCnt			= 0;					// 작업예약 등록 건수 :: MAX건 등록여부 체크시 사용

	    try {

	    	szMsg = "["+szOperationName+"] 대차 상차 스케줄 .... START >>>> " + msgRecord.toString();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    	szTcarEqpId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");					//대차설비ID
//	    	szYdToBay 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_BAY");					//공대차출발 시 사용자가 지정한 목표동(값 없을수 있슴)
	    	szYdWbookId	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");					//작업예약ID(값 없을수 있슴)
	    	szModifier	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");					//수정자
	    	if ("".equals(szModifier)) {
	    		szModifier	= ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER", "YARDSYSTEM");	//수정자
	    	}

	    	//-------------------------------------------------------------------------------------
	    	//	대차설비ID로 설비테이블을 조회한다.
	    	//-------------------------------------------------------------------------------------
	    	szMsg = "["+szOperationName+"] 대차 상차시 .... 대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    		rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
    		recPara  = JDTORecordFactory.getInstance().create();
    		recPara.setField("YD_EQP_ID", 			szTcarEqpId);

    		intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult);		// intGp == 0

    		szMsg = "["+szOperationName+"] 대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if (intRtnVal <= 0) {
    			szRtnMsg = "대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 시 오류발생 - 반환값 : " + intRtnVal;
    			szMsg = "["+szOperationName+"] " + szRtnMsg;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			return szRtnMsg;
			}

			rsResult.absolute(1);
    		recOutTemp = JDTORecordFactory.getInstance().create();
    		recOutTemp.setRecord(rsResult.getRecord());

    		szYdTcarCurrBayGp	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CURR_BAY_GP");	//현재동

	    	if ("".equals(szYdWbookId)) {

	    		//-------------------------------------------------------------------------------------
	    		//	작업예약ID가 미존재시 해당 스케쥴코드로 작업예약을 조회 한다. 대차설비ID :: FXTC01
	    		//-------------------------------------------------------------------------------------
	    		szYdGp		 = ydUtils.substr(szTcarEqpId, 0, 1);
	    		szYdStkColGp = szYdGp + szYdTcarCurrBayGp + ydUtils.substr(szTcarEqpId, 2, 4);
	    		szYdSchCd	 = szYdStkColGp + "UM";		// 대차 상차 스케쥴코드 : FBTC01UM

    			//-----------------------------------------------------
    			// 스케줄기준 정보 Check (야드스케쥴금지유무, 야드스케쥴우선순위)
    			//-----------------------------------------------------
    			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
	    		recPara  = JDTORecordFactory.getInstance().create();
    			recPara.setField("YD_SCH_CD", szYdSchCd); //야드스케쥴코드

    			ydSchRuleDao.getYdSchrule(recPara, rsResult);

    			if (rsResult != null && rsResult.size() > 0) {
    				szYdSchProhExn  = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_SCH_PROH_EXN");
    				szYdWrkCrn		= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_CRN");
    			}

    			if ("".equals(szYdSchProhExn)) {
    				//스케줄기준 Table 정보 Check
    				szRtnMsg = "오류:스케쥴코드[" + szYdSchCd + "] 정보 없음";
	    			szMsg = "["+szOperationName+"] " + szRtnMsg;
	    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    			return szRtnMsg;
    			} else if ("Y".equals(szYdSchProhExn)) {
    				//스케줄 금지여부 Check
    				szRtnMsg = "오류:스케쥴코드[" + szYdSchCd + "] 기동금지";
	    			szMsg = "["+szOperationName+"] " + szRtnMsg;
	    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    			return szRtnMsg;
    			}

				rsResult  = JDTORecordFactory.getInstance().createRecordSet("yd");
	    		recPara   = JDTORecordFactory.getInstance().create();
    			recPara.setField("YD_SCH_CD", szYdSchCd); //야드스케쥴코드
	    		intRtnVal = ydWrkbookDao.getBySchCdWithCrnSchNo(recPara, rsResult);

	    		if (intRtnVal <= 0) {
	    			szRtnMsg = "대차 작업예약 정보가 미존재하여 .. 대차 상차 스케쥴기동 SKIP 처리";
	    			szMsg = "["+szOperationName+"] " + szRtnMsg;
	    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		//	return szRtnMsg;
	    			return JPlateYdConst.RETN_CD_SUCCESS;
	    		}

	    		rsResult.first();
	    		for(int ii=0; ii<rsResult.size(); ii++) {
	    			rsResult.absolute(ii+1);
		    		recOutTemp = rsResult.getRecord();

	    			//-----------------------------------------------------
		    		// 작업예약 건수 조회하여 MAX건 초과시는 크레인 스케쥴 기동 SKIP
	    			//-----------------------------------------------------
		    		if (iWBookInsCnt >= JPlateYdConst.MAX_CRN_SCH_CNT) {
		    			szMsg = "["+szOperationName+"] 작업예약 "+JPlateYdConst.MAX_CRN_SCH_CNT+"건 등록 .. 이후 재료 SKIP .... 크레인 작업지시 호출 건수 :: " + Integer.toString(iWBookInsCnt);
		    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
		    			continue;
		    		}

		    		szYdWbookId 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID");
		    		szYdToLocGuide	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TO_LOC_GUIDE");

			    	//------------------------------------------------
					// 크레인 스케줄 호출
		    		//------------------------------------------------
					recSchPara 	= JDTORecordFactory.getInstance().create();
					recSchPara.setField("MSG_ID", 			"YDYDJ");			//TC코드
					recSchPara.setField("YD_EQP_ID", 		szYdWrkCrn);		//크레인설비ID
					recSchPara.setField("YD_SCH_CD",		szYdSchCd);			//크레인스케줄코드
					recSchPara.setField("YD_WBOOK_ID",		szYdWbookId);		//작업예약ID
					recSchPara.setField("REGISTER", 		szModifier);
					recSchPara.setField("MODIFIER", 		szModifier);
					recSchPara.setField("YD_TO_LOC_GUIDE",	szYdToLocGuide);	//야드To위치Guide

					szMsg   = "["+szOperationName+"] ----------- 크레인 스케줄기동 START :: " + szYdWbookId;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			        EJBConnector ejbConn = new EJBConnector("default", "JPlateYdCrnSchYdPSeEJB", this);
			        szRtnMsg = (String)ejbConn.trx("procCrnSchMainYdP", new Class[] { JDTORecord.class }, new Object[] { recSchPara });

					szMsg    = "["+szOperationName+"] ----------- 크레인 스케줄기동 END :: " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					iWBookInsCnt ++;

	    		}	// end for
	    	}

			szMsg = "["+szOperationName+"] 대차 상차 스케줄("+szMethodName+") 완료";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch(Exception e) {

			szMsg = "["+szOperationName+"] 대차 상차 스케줄  Error : " +e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}

		szMsg = "["+szOperationName+"] 대차 상차 스케줄 메소드  END";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;

	} //end of procY2UdTcarSch()


	/**
	 * 오퍼레이션명 : 1후판정정야드 대차 도착처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public String procY2TcarStop(JDTORecord msgRecord)throws DAOException  {

		JPlateYdEqpDAO		ydEqpDao	= new JPlateYdEqpDAO();
		JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();
		JPlateYdCrnSchDAO	ydCrnSchDao	= new JPlateYdCrnSchDAO();

		JDTORecordSet 	rsResult		= null;
		JDTORecord    	recOutTemp      = null;
		JDTORecord    	recPara         = null;

	    int 	intRtnVal 				= 0;

	    String 	szMsg              		= "";
	    String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;

	    String 	szMethodName       		= "procY2TcarStop";
	    String 	szOperationName			= "1후판정정야드 대차 도착처리";

		String	szYD_EQP_ID				= "";
		String	szYD_GP					= "";		// 야드구분
		String	szYD_CURR_BAY_GP		= "";		// 대차 현재동  [화면에서 입력 받은 값]
	    String	szYD_START_LOC 	 		= "";		// 대차 출발 위치
		String	szYD_STOP_LOC 			= "";		// 대차 도착 위치
    	String	szLD_BAY				= "";		// 상차동
		String	szUD_BAY				= "";  		// 하차동
		String	szYD_SCH_CD				= "";
    	String	szMODIFIER				= "";
    	String	szOLD_YD_CURR_BAY_GP	= "";		// 실제 대차의 현재동

	    try {

			szMsg = "[" + szOperationName + "] 대차도착처리 START >>>> " + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	    	szYD_CURR_BAY_GP	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CURR_BAY_GP");
	    	szLD_BAY			= ydDaoUtils.paraRecChkNull(msgRecord, "LD_BAY");				// 상차동
	    	szUD_BAY			= ydDaoUtils.paraRecChkNull(msgRecord, "UD_BAY");				// 하차동
	    	szMODIFIER  		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");
	    	if ("".equals(szMODIFIER)) {
	    		szMODIFIER = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER", "YARDSYSTEM");
	    	}
	    	szYD_GP = ydUtils.substr(szYD_EQP_ID, 0, 1);

	    	//-------------------------------------------------------------
	    	// 0. 현재 위치로 크레인 작업지시 생성시에는 대차 도착 처리 못하도록 체크
	    	//-------------------------------------------------------------
	    	szYD_SCH_CD = szYD_GP + "_" + ydUtils.substr(szYD_EQP_ID, 2, 4) + "_M";

	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
	    	recPara  = JDTORecordFactory.getInstance().create();
	    	recPara.setField("YD_SCH_CD", szYD_SCH_CD);
	    	intRtnVal = ydCrnSchDao.getByYdSchCd(recPara, rsResult);
	    	if (intRtnVal > 0) {
		    	szRtnMsg = "크레인 작업지시가 존재하여 도착처리 불가합니다.!";
		    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

	    	//-------------------------------------------------------------
	    	// 1. 설비ID로 대차 현재동을 조회한다.
	    	//-------------------------------------------------------------
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
	    	recPara  = JDTORecordFactory.getInstance().create();

	    	recPara.setField("YD_EQP_ID", szYD_EQP_ID);
	    	intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult);
	    	if (intRtnVal <= 0) {
		    	szRtnMsg = "해당 설비로 데이타 조회시 오류 >>>> " + szYD_EQP_ID;
		    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

	    	rsResult.first();
	    	recOutTemp = rsResult.getRecord();

	    	szOLD_YD_CURR_BAY_GP = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CURR_BAY_GP");

	    	// 2013.07.07 --> 화면에서 입력한 현재동으로 도착 처리하도록 보완
//SJH16 수정해야 함	    	
	    	if ("PXTC01".equals(szYD_EQP_ID)) {
		    	if ("C".equals(szYD_CURR_BAY_GP)) {
		    		szYD_CURR_BAY_GP 	= "C";
		    		szYD_START_LOC 		= szYD_GP + "D" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    		szYD_STOP_LOC		= szYD_GP + "C" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    	} else {
		    		szYD_CURR_BAY_GP 	= "D";
		    		szYD_START_LOC 		= szYD_GP + "C" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    		szYD_STOP_LOC		= szYD_GP + "D" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    	}
	    	} else {
		    	if ("C".equals(szYD_CURR_BAY_GP)) {
		    		szYD_CURR_BAY_GP 	= "C";
		    		szYD_START_LOC 		= szYD_GP + "B" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    		szYD_STOP_LOC		= szYD_GP + "C" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    	} else {
		    		szYD_CURR_BAY_GP 	= "B";
		    		szYD_START_LOC 		= szYD_GP + "C" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    		szYD_STOP_LOC		= szYD_GP + "B" + ydUtils.substr(szYD_EQP_ID, 2, 4);
		    	}
	    	}

	    	//-------------------------------------------------------------
	    	// 2. 대차 현재동구분 변경
	    	//-------------------------------------------------------------
	    	recPara = JDTORecordFactory.getInstance().create();
	    	recPara.setField("YD_EQP_ID", 			szYD_EQP_ID);
	    	recPara.setField("YD_CURR_BAY_GP",		szYD_CURR_BAY_GP);
	    	recPara.setField("MODIFIER", 			szMODIFIER);
	    	recPara.setField("YD_START_LOC",		szYD_START_LOC);		// 대차 출발 위치
			recPara.setField("YD_STOP_LOC",			szYD_STOP_LOC);			// 대차 도착 위치
			recPara.setField("LD_BAY",				szLD_BAY);				// 상차동
			recPara.setField("UD_BAY",				szUD_BAY);				// 하차동

			intRtnVal = ydEqpDao.updYdCurrBayGp(recPara);
	    	if (intRtnVal <= 0) {
		    	szRtnMsg = "대차 현재 동구분 변경시 오류 .." + Integer.toString(intRtnVal);
		    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

    		//-------------------------------------------------------------------------------------
    		// 3. 대차스케줄 항목 업데이트
    		//-------------------------------------------------------------------------------------
	    	szRtnMsg = this.updTcarSchUdInfo(recPara);
	    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szRtnMsg = "대차도착처리시  대차스케줄 수정시 오류 발생 : " + szRtnMsg;
		    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
	    	}

	    	// 설비테이블의 현재동과 화면에서 입력한 현재동이 틀릴 경우만 수행 하도록 보완
	    	if (!szYD_CURR_BAY_GP.equals(szOLD_YD_CURR_BAY_GP)) {
		    	//-------------------------------------------------------------
		    	// 4. 대차 도착 저장위치(베드) 활성화 처리
		    	//  - 저장위치 수정 : 출발위치의 재료의 저장위치를 도착위치로 변경
		    	//-------------------------------------------------------------
		    	szRtnMsg = this.enableFromBed(recPara);
		    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					szRtnMsg = "대차 도착 저장위치(베드) 활성화 처리시 오류 발생 : " + szRtnMsg;
			    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
		    	}

		    	//-------------------------------------------------------------
		    	// 5. 대차 출발 저장위치(베드) 비활성화 처리
		    	//-------------------------------------------------------------
		    	szRtnMsg = this.disableToBed(recPara);
		    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					szRtnMsg = "대차 출발 저장위치(베드) 비활성화 처리시 오류 발생 : " + szRtnMsg;
			    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
		    	}

		    	//-------------------------------------------------------------
		    	// 야드  L2 Interface 처리
		    	//-------------------------------------------------------------
		    	// - 저장위치 제원정보 야드L2전송 (FROM:비활성화)
		    	if (!"FD".equals(ydUtils.substr(szYD_START_LOC, 0, 2))) {		// D동일때는 L2에서 관리 안함으로 SKIP 처리
			    	recPara  = JDTORecordFactory.getInstance().create();
					recPara.setField("MSG_ID", 			"YDY2L001");
					recPara.setField("YD_INFO_SYNC_CD", "3");  // 1:동, 2:SPAN, 3:열, 4:BED
					recPara.setField("YD_GP", 			szYD_GP);
					recPara.setField("YD_STK_COL_GP", 	szYD_START_LOC);
					recPara.setField("YD_STK_BED_NO", 	"");
					szRtnMsg = ydDelegate.sendMsg(recPara);

					szMsg = "[Jsp-Session "+szOperationName+" ] 대차출발 위치 비활성화 정보 송신 완료 >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		    	}
		    	if (!"FD".equals(ydUtils.substr(szYD_STOP_LOC, 0, 2))) {		// D동일때는 L2에서 관리 안함으로 SKIP 처리
			    	// - 저장위치 제원정보 야드L2전송 (TO:활성화)
			    	recPara  = JDTORecordFactory.getInstance().create();
					recPara.setField("MSG_ID", 			"YDY2L001");
					recPara.setField("YD_INFO_SYNC_CD", "3");  // 1:동, 2:SPAN, 3:열, 4:BED
					recPara.setField("YD_GP", 			szYD_GP);
					recPara.setField("YD_STK_COL_GP", 	szYD_STOP_LOC);
					recPara.setField("YD_STK_BED_NO", 	"");
					szRtnMsg = ydDelegate.sendMsg(recPara);

					szMsg = "[Jsp-Session "+szOperationName+" ] 대차도착 위치 활성화 정보 송신 완료 >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		    	}

				// 대차위에(TO위치) 재료가 존재시에만 실행
				rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
		    	recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP",	szYD_STOP_LOC);
				recPara.setField("YD_STK_BED_NO",	"");
				recPara.setField("YD_EQP_WRK_SH",	"1");
				intRtnVal = ydStkLyrDao.getStlNoTopCnt(recPara, rsResult);

				if (intRtnVal > 0) {

			    	if (!"FD".equals(ydUtils.substr(szYD_STOP_LOC, 0, 2))) {		// D동일때는 L2에서 관리 안함으로 SKIP 처리
				    	// - 저장품 제원정보 야드L2전송
				    	recPara  = JDTORecordFactory.getInstance().create();
						recPara.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
						recPara.setField("YD_GP", 			szYD_GP);		// 야드구분
						recPara.setField("YD_STK_COL_GP", 	szYD_STOP_LOC);                         // 야드적치열구분
						recPara.setField("YD_STK_BED_NO", 	"");    								// 야드적치BED번호
						recPara.setField("YD_INFO_SYNC_CD", "3");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
						recPara.setField("STL_NO", 			"");	        						// 재료번호
						szRtnMsg = ydDelegate.sendMsg(recPara);

						szMsg = "[Jsp-Session "+szOperationName+" ] 대차도착 위치 제원정보 정보 송신 완료 >>>> " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			    	}

			    	//-------------------------------------------------------------
			    	// 조업  L3 Interface 처리
			    	//-------------------------------------------------------------
			    	// - 조업L3 저장위치 변경정보 송신
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("MSG_ID", 			"YDPRJ011");
					recPara.setField("YD_STK_COL_FR", 	szYD_START_LOC);						// From적치열
					recPara.setField("YD_STK_BED_FR", 	"");									// From적치BED
					recPara.setField("YD_STK_COL_TO", 	szYD_STOP_LOC);							// TO적치열
		        	recPara.setField("YD_STK_BED_TO", 	"");									// TO적치BED
		        	recPara.setField("YD_EQP_WRK_SH", 	"20");									// 야드설비작업매수

		        //	szRtnMsg = ydDelegate.sendMsg(recPara);
		            szRtnMsg = JPlateYdCommonUtils.sendL3YDPRJ011(recPara);

					szMsg = "[ " +szOperationName + "] 조업L3 저장위치 변경정보 송신 END >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				} else {
					szMsg = "[ " +szOperationName + "] 도착위치에 재료정보가 미존재하여 저장품 제원정보 송신 SKIP";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				}
	    	}

	        if (szYD_CURR_BAY_GP.equals(szLD_BAY)) {				// 도착동이 상차동 이면 상차 스케쥴 호출

		    	//-------------------------------------------------------------
		    	// 대차 상차 스케쥴 기동
		    	//-------------------------------------------------------------
				szMsg = "[Jsp-Session "+szOperationName+" ] 대차 상차 스케쥴 기동 .... START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		    	szRtnMsg = this.procY2UdTcarSch(msgRecord);

				szMsg = "[Jsp-Session "+szOperationName+" ] 대차 상차 스케쥴 기동 .... END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        } else if (szYD_CURR_BAY_GP.equals(szUD_BAY)) {			// 도착동이 상차동 이면 상차 스케쥴 호출

		    	//-------------------------------------------------------------
		    	// 대차 하차 스케쥴 기동
		    	//-------------------------------------------------------------
				szMsg = "[Jsp-Session "+szOperationName+" ] 대차 하차 스케쥴 기동 .... START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				/*
				 *  하차작업생성 SKIP 
				 */
		    	//szRtnMsg = this.procY2LdTcarSch(msgRecord);
 
				szMsg = "[Jsp-Session "+szOperationName+" ] 대차 하차 스케쥴 기동 .... END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        }

		} catch(Exception e) {
			szRtnMsg = "대차도착처리시  Exception 발생 :" +e.getMessage();
	    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szRtnMsg;
		}

		szMsg = "[" + szOperationName + "] 대차도착처리 END >>>> ";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	}

	/**
	 * 오퍼레이션명 : 대차 도착 저장위치(베드) 활성화 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public String enableFromBed2(JDTORecord msgRecord)throws DAOException  {

		JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();
		JPlateYdStkBedDAO	ydStkBedDao	= new JPlateYdStkBedDAO();
		JPlateYdStkColDAO	ydStkColDao	= new JPlateYdStkColDAO();

		JDTORecord    	recPara         = null;

	    int 	intRtnVal 				= 0;
	    String 	szMsg              		= "";
	    String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;

	    String 	szMethodName       		= "enableFromBed";
	    String 	szOperationName			= "대차 도착 저장위치(베드) 활성화 처리";

		String	szMODIFIER				= "";
    	String	szYD_START_LOC			= "";		// 대차 출발 위치 (대차 상차 정지 위치)
		String	szYD_STOP_LOC			= "";  		// 대차 도착 위치 (대차 하차 정지 위치)

	    try {

			szMsg = "[" + szOperationName + "] 대차도착 BED 활성화 START >>>> " + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMODIFIER  	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");
			if ("".equals(szMODIFIER)) {
				szMODIFIER  = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER", "YARDSYSTEM");
			}
	    	szYD_START_LOC	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_START_LOC");		// 대차 출발 위치
	    	szYD_STOP_LOC 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_STOP_LOC");		// 대차 도착 위치

	    	if ( "".equals(szYD_START_LOC) || "".equals(szYD_STOP_LOC)) {
	    		szRtnMsg = "대차도착 적치단 활성화 처리시 오류 >>>> 출발위치 :: " + szYD_START_LOC + ", 도착위치:: " + szYD_STOP_LOC;
	    		szMsg = "[" + szOperationName + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

	    	recPara  = JDTORecordFactory.getInstance().create();
	    	recPara.setField("YD_STK_COL_GP_FR",	szYD_START_LOC);
	    	recPara.setField("YD_STK_COL_GP_TO",	szYD_STOP_LOC);
	    	recPara.setField("YD_STK_LYR_ACT_STAT",	"E");					// C:Close(비활성화), E:적치 가능, F:적치 완료, N:사용 불가
	    	recPara.setField("MODIFIER", 			szMODIFIER);
	    	recPara.setField("OCPY_CHK_FLAG",		"N");					// 점유베드 체크 안함

	    	// 대차 도착 저장위치(적치단) 활성화 처리 및 재료적치정보 복사
	    	intRtnVal = ydStkLyrDao.copyTcarFromBed(recPara);
	    	if (intRtnVal <= 0) {
	    		szRtnMsg = "대차도착 적치단 활성화 처리시 오류 :: " + Integer.toString(intRtnVal);
	    		szMsg = "[" + szOperationName + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

	    	// 대차 도착 저장위치(베드) 활성화 처리
	    	recPara  = JDTORecordFactory.getInstance().create();
	    	recPara.setField("YD_STK_COL_GP",		szYD_STOP_LOC);
	    	recPara.setField("YD_STK_BED_NO",		"");
	    	recPara.setField("YD_STK_BED_ACT_STAT",	"L");					// C:Close(비활성화), L:적치 가능(활성), N:사용 불가
	    	recPara.setField("MODIFIER", 			szMODIFIER);

	    	intRtnVal = ydStkBedDao.updYdStkBedActStat(recPara);
	    	if (intRtnVal <= 0) {
	    		szRtnMsg = "대차도착 BED 활성화 처리시 오류 :: " + Integer.toString(intRtnVal);
	    		szMsg = "[" + szOperationName + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

	    	// 대차 도착 저장위치(적치열) 활성화 처리
	    	recPara  = JDTORecordFactory.getInstance().create();
	    	recPara.setField("YD_STK_COL_GP",		szYD_STOP_LOC);
	    	recPara.setField("YD_STK_COL_ACT_STAT",	"L");					// C:Close(비활성화), L:적치 가능(활성), N:사용 불가
	    	recPara.setField("MODIFIER", 			szMODIFIER);

	    	intRtnVal = ydStkColDao.updYdStkColActStat(recPara);
	    	if (intRtnVal <= 0) {
	    		szRtnMsg = "대차도착 적치열 활성화 처리시 오류 :: " + Integer.toString(intRtnVal);
	    		szMsg = "[" + szOperationName + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    		return szRtnMsg;
	    	}

		} catch(Exception e) {
			szRtnMsg = "대차도착 BED 활성화 처리시  Exception 발생 :" +e.getMessage();
	    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szRtnMsg;
		}

		szMsg = "[" + szOperationName + "] 대차도착 BED 활성화 END >>>> " + szYD_STOP_LOC;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	}

} // end of class JPlateYdTcarSchSeEJBBean
