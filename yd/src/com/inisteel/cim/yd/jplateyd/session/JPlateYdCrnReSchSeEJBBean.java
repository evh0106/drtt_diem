/*
 * @(#) 2후판정정야드 크레인리스케쥴요청 Session EJB
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/20
 *
 * @description		크레인리스케쥴요청 Session EJB
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/20   김현우      김현우       최초작성
 */

package com.inisteel.cim.yd.jplateyd.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;

import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO;
import com.inisteel.cim.yd.jplateyd.delegate.JPlateYdDelegate;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdCommonUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdCrnSchUtil;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;

/**
 * 크레인리스케쥴요청 Session EJB
 *
 * @ejb.bean name= "JPlateYdCrnReSchSeEJB" jndi-name= "JPlateYdCrnReSchSeEJB" type= "Stateless"
 *           view-type= "remote" display-name= "" description= ""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool= "10" max-beans-in-free-pool= "100"
 * @ejb.transaction type= "Required"
 */
public class JPlateYdCrnReSchSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;

	// Session Name
	private final static String SZ_SESSION_NAME = JPlateYdCrnReSchSeEJBBean.class.getName();

	private JPlateYdUtils 		ydUtils 		= new JPlateYdUtils();
	private JPlateYdDaoUtils 	ydDaoUtils 		= new JPlateYdDaoUtils();
	private JPlateYdSchRuleDAO 	ydSchRuleDao 	= new JPlateYdSchRuleDAO();

	private JPlateYdDelegate 	ydDelegate  	= new JPlateYdDelegate();

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
     * 오퍼레이션명 : 2후판정정야드 크레인 리스케줄
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return int
     * @throws JDTOException
     */
    public int procY7CrnReSch(JDTORecord msgRecord)throws JDTOException  {

    	JDTORecordSet 	outRecSet 		= null;
        JDTORecordSet 	rsReSchResult 	= JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecord 		recTemp 		= null;

        int intRtnVal 					= 0 ;
        String szMsg              		= "";
        String szMethodName       		= "procY7CrnReSch";

        String szEqpId                  = "";
        String szYdGp                   = "";
        String szYdBayGp                = "";

        boolean bRtnCheck               = true;

        String szRcvTcCode = ydUtils.getTcCode(msgRecord);

        if (szRcvTcCode == null) {
        	szMsg = "[ERROR] "+SZ_SESSION_NAME+ "::"+szMethodName+ "() TC Code Error ("+szRcvTcCode+ ")";
        	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
        	return -1;
        }

        if (bDebugFlag) {
            szMsg = "[DEBUG] 전문수신 : TCCODE= " +szRcvTcCode ;
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
        }

        try {

        	//파라미터로 넘어온 설비 ID로 설비 상태를 Check
        	szEqpId   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");

        	bRtnCheck = this.eqpStatCheckY7(szEqpId);

        	//설비아이디의 야드구분 동구분을 빼서 스케줄기준Table조회
        	szYdGp    = szEqpId.substring(0,1);
        	szYdBayGp = szEqpId.substring(1,2);
        	recTemp   = JDTORecordFactory.getInstance().create();
        	recTemp.setField("YD_GP",     szYdGp);
        	recTemp.setField("YD_BAY_GP", szYdBayGp);
        	outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
        	intRtnVal = ydSchRuleDao.getYdSchruleYdBayGp(recTemp, outRecSet);
			if (intRtnVal <= 0) {
				szMsg = "스케줄기준이 조회가 되지않았습니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return -1;
			}

			if (bRtnCheck) {
				//------------------------------------------------------------------------------------------------
				// 복구 리스케줄 호출 LOG 추가
				//------------------------------------------------------------------------------------------------
				szMsg = "procY7CrnReSch - 복구 리스케줄 호출";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				intRtnVal = this.resPairReSchY7(szEqpId, outRecSet, rsReSchResult);
				if (intRtnVal == -1) {
					szMsg = "복구 리스케줄 처리 Error";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return -1;
				}
			} else {
				//------------------------------------------------------------------------------------------------
				// 고장  리스케줄 호출 LOG 추가
				//------------------------------------------------------------------------------------------------
				szMsg = "procY7CrnReSch - 고장 리스케줄 호출";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				intRtnVal = this.disableReSchY7(szEqpId, outRecSet, rsReSchResult);
				if (intRtnVal == -1) {
					szMsg = "고장 리스케줄 처리 Error";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return -1;
				}
			}

        	//3. 호출한 메소드의 결과값으로 작업예약 Table및 크레인 스케줄 Table업데이트 메소드 호출
        	//intRtnVal = this.updWbookCrnSchY7(rsReSchResult);
			if (intRtnVal == -1) {
				szMsg = "작업예약 및 크레인 스케줄 등록 중 Error";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return -1;
			}

		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "크레인 리스케줄("+szMethodName+ ") 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		return 0;

	} //end of procY7CrnReSch()

	/**
	 * 오퍼레이션명 : 설비상태 체크
	 *
	 * @param  String szEqpId 설비ID
	 * @return boolean true(설비사용가능), false(설비사용불가)
	 * @throws JDTOException
	 */
	public boolean eqpStatCheckY7(String szEqpId)throws JDTOException  {

		//리턴값(boolean)
		boolean blnRtnVal      	= false;
		//메세지
		String szMsg           	= null;
		//메소드명
		String szMethodName    	= "eqpStatCheckY7";
		//설비상태
		String szYdEqpStat   	= null;
		//야드설비작업상태
		String szYdEqpWrkMode 	= null;
		//레코드 선언
		JDTORecord recPara     	= null;
		//레코드셋 선언
		JDTORecordSet rsResult 	= null;

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			//레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//설비ID를 작업크레인으로 설정
			recPara.setField("YD_EQP_ID", szEqpId);

			//설비 체크 및 데이터 조회
			blnRtnVal = this.chkGetEqpY7(szEqpId, rsResult);
			if (!blnRtnVal) {
				return blnRtnVal;
			}

			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			//설비상태
			szYdEqpStat 	= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
			//야드설비작업상태
			szYdEqpWrkMode 	= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_MODE");

			//크레인의 상태가 'T'이면 false 리턴.
			if (JPlateYdConst.YD_EQP_STAT_BREAK.equals(szYdEqpStat)) {

				szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYdEqpStat + ") 입니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			} else if ("2".equals(szYdEqpWrkMode)) {
				/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				 * OFF-LINE 체크
				 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				szMsg = "설비ID(" + szEqpId + ")의 야드설비작업상태(" + szYdEqpStat + " : OFF-LINE ) 입니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;
			} else {
				blnRtnVal = true;
			}
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		return blnRtnVal;

	} //end of eqpStatCheckY7

	/**
	 * 오퍼레이션명 : 설비 유무체크 및 조회결과 데이터 반환
	 *
	 * @param  String        szEqpId  설비ID
	 *         JDTORecordSet rsResult 결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetEqpY7(String szEqpId, JDTORecordSet rsResult)throws JDTOException  {

		//설비 DAO
		JPlateYdEqpDAO ydEqpDao	= new JPlateYdEqpDAO();
		//리턴값(boolean)
		boolean blnRtnVal     	= false;
		//리턴값(int)
		int intRtnVal         	= 0;
		//메소드명
		String szMethodName   	= "chkGetEqpY7";
		String szMsg          	= null;

		//레코드 선언
		JDTORecord recPara      = null;

		try {

			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();

			//설비ID
			recPara.setField("YD_EQP_ID", szEqpId);

			//설비 테이블 조회
			intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult);

			//리턴값 메세지처리
			if (intRtnVal > 1) {

				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 중복되었습니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 없습니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 parameter error 발생!";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 오류 발생!";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			}
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		return blnRtnVal;
	} //end of chkGetEqpY7

	/**
	 * 오퍼레이션명 : 복구리스케줄
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param szEqpId, rsSchRule, rsReSchResult
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int resPairReSchY7(String szEqpId, JDTORecordSet rsSchRule, JDTORecordSet rsReSchResult)throws JDTOException  {

		JDTORecord recTemp      = null;
		JDTORecord recResult    = null;
		JDTORecordSet rsResult	= JDTORecordFactory.getInstance().createRecordSet("Temp");

	    String szMsg            = "";
	    String szMethodName     = "resPairReSchY7";
	    String szWrkCrn         = "";
	    String szAltCrn         = "";

	    try {

	    	for(int ii=1; ii<=rsSchRule.size(); ii++) {
	    		rsSchRule.absolute(ii);
	    		recTemp = JDTORecordFactory.getInstance().create();
	    		recTemp.setRecord(rsSchRule.getRecord());
	    		szWrkCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_CRN");
	    		szAltCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_ALT_CRN");

	    		recResult = JDTORecordFactory.getInstance().create();
	    		//설비아이디가 스케줄 기준의 주크레인과 같거나 대체크레인과 같을 경우
	    		if (szEqpId.equals(szWrkCrn)) {
	    			//새로운 레코드 셋에 등록한다.(스케줄 코드, 설비ID, 스케줄우선순위)
	    			recResult.setField("YD_SCH_CD", 	recTemp.getFieldString("YD_SCH_CD"));
	    			recResult.setField("YD_EQP_ID", 	recTemp.getFieldString("YD_WRK_CRN"));
	    			recResult.setField("YD_SCH_PRIOR", 	recTemp.getFieldString("YD_WRK_CRN_PRIOR"));
	    			recResult.setField("WRK_CRN_YN", 	"Y");
	    			rsResult.addRecord(recResult);

	    		} else if (szEqpId.equals(szAltCrn)) {
	    			//새로운 레코드 셋에 등록한다.
	    			recResult.setField("YD_SCH_CD", 	recTemp.getFieldString("YD_SCH_CD"));
	    			recResult.setField("YD_SCH_PRIOR", 	recTemp.getFieldString("YD_WRK_CRN_PRIOR"));
	    			recResult.setField("WRK_CRN_YN", 	"N");
	    			rsResult.addRecord(recResult);
	    		}

	    	}//end of for
	    	rsReSchResult.addAll(rsResult);

		}catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "복구 리스케줄 처리("+szMethodName+ ") 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		return 1;

	} //end of resPairReSchY7()

	/**
	 * 오퍼레이션명 : 고장 리스케줄
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param szEqpId, rsSchRule, rsReSchResult
	 * @return
	 * @throws JDTOException
	 */
	public int disableReSchY7(String szEqpId, JDTORecordSet rsSchRule, JDTORecordSet rsReSchResult)throws JDTOException  {

		JDTORecord recTemp              = null;
		JDTORecord recResult            = null;
		JDTORecordSet rsResult          = JDTORecordFactory.getInstance().createRecordSet("Temp");

	    String szMsg              		= "";
	    String szMethodName       		= "disableReSchY7";
	    String szWrkCrn                 = "";
	    String szAltCrn                 = "";

	    try{

	    	for(int ii = 1; ii <= rsSchRule.size(); ii++) {
	    		rsSchRule.absolute(ii);
	    		recTemp = JDTORecordFactory.getInstance().create();
	    		recTemp.setRecord(rsSchRule.getRecord());
	    		szWrkCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_CRN");
	    		szAltCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_ALT_CRN");

	    		recResult = JDTORecordFactory.getInstance().create();
	    		//설비아이디가 스케줄 기준의 주크레인과 같거나 대체크레인과 같을 경우
	    		if (szEqpId.equals(szWrkCrn)) {
	    			//새로운 레코드 셋에 등록한다.(스케줄 코드, 설비ID, 스케줄우선순위)
	    			recResult.setField("YD_SCH_CD", 	recTemp.getFieldString("YD_SCH_CD"));
	    			recResult.setField("YD_EQP_ID", 	recTemp.getFieldString("YD_ALT_CRN"));
	    			recResult.setField("YD_SCH_PRIOR", 	recTemp.getFieldString("YD_ALT_CRN_PRIOR"));
	    			recResult.setField("WRK_CRN_YN", 	"Y");
	    			rsResult.addRecord(recResult);

	    		} else if (szEqpId.equals(szAltCrn)) {
	    			//새로운 레코드 셋에 등록한다.
	    			recResult.setField("YD_SCH_CD", 	recTemp.getFieldString("YD_SCH_CD"));
	    			recResult.setField("YD_SCH_PRIOR", 	recTemp.getFieldString("YD_ALT_CRN_PRIOR"));
	    			recResult.setField("WRK_CRN_YN", 	"N");
	    			rsResult.addRecord(recResult);
	    		}

	    	}//end of for
	    	rsReSchResult.addAll(rsResult);

		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "고장 리스케줄 처리("+szMethodName+ ") 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		return 1;

	} //end of disableReSchY7()


	/**
	 * 권하위치 변경 (크레인 상태관리화면)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public String updCrnDnPrsFix(JDTORecord inDto) throws DAOException {

		int 		intRtnVal 		= 0;
		String 		szMsg			= "";
		String 		szMethodName 	= "updCrnDnPrsFix";
		String 		szOperationName = "권하위치 변경";
		String		szModifier		= null;
		JDTORecord  recPara 		= null;
		JDTORecord  recTemp 		= null;
		JDTORecord  recCrnSch 		= null;
		JDTORecord  recSet 			= null;

		String 		szYdStkPos   	= null;
		String 		szYdStkColGp 	= null;
		String		szYdStkEqpGp	= null;
		String 		szYdStkBedNo 	= null;
		String 		szYdStkLyrNo 	= null;
		String		szYdStlNo		= null;
		String		szYdGp			= null;
    	String		szYdDnWrLoc 	= null;
        String		szYdDnWrBedNo	= null;
        String		szYdDnWrLayer 	= null;
        String		szYdUpWrkActGp	= null;			// 야드권상작업수행구분(YD_UP_WRK_ACT_GP) 항목사용 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)
        String		szYdDnWrkActGp	= null;			// 야드권하작업수행구분(YD_DN_WRK_ACT_GP) 항목사용 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)

		String 		szOldStkPos   	= null;
		String 		szOldStkLyrNo 	= null;
		int			iCrMtlCnt		= 0;

		JPlateYdStkLyrDAO 		ydStkLyrDao 	= new JPlateYdStkLyrDAO();
		JPlateYdCrnSchDAO 		ydCrnSchDao 	= new JPlateYdCrnSchDAO();
		JPlateYdCrnWrkMtlDAO  	ydCrnWrkMtlDao 	= new JPlateYdCrnWrkMtlDAO();

		JDTORecordSet	outRecSet  	= null;
		JDTORecordSet   rsStkLyr  	= null;

		String 		szLogMsg 		= null;
		String 		szRtnMsg 		= null;

		// 1건 이상의 작업재료 존재시 임시 사용변수
		String		szTempLyrNo		= "";
		String		szSaveLyrNo		= "";
		String		szSaveBedNo		= "";

		String		szStlNo			= "";
		String		szTopLyrNo		= "";
		int			iBedUseCnt		= 0;
		int			iYdStkBedCnt	= 0;		// 권하위치의 베드 갯수

		try {

			szLogMsg = "JSP-SESSION [ 권하위치 변경 (크레인 상태관리화면)] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			// 1. 기존 To 위치 정보 와 변경 To 위치 정보
			recPara   = JDTORecordFactory.getInstance().create();
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

			recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(inDto, "YD_CRN_SCH_ID"));
			intRtnVal = ydCrnSchDao.getYdCrnSch(recPara, outRecSet);		// intGp == 0

			if (intRtnVal < 1) {
				szRtnMsg = "해당 스케줄 정보: "+inDto.getField("YD_CRN_SCH_ID")+ "가 미존재!";
				szMsg    = "[JSP Session]권하위치 변경 - " + szRtnMsg;
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			recCrnSch = JDTORecordFactory.getInstance().create();

			outRecSet.first();
			recCrnSch.setRecord(outRecSet.getRecord());

			szOldStkPos   	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC");
			szOldStkLyrNo 	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LAYER");
			szYdStlNo		= ydDaoUtils.paraRecChkNull(recCrnSch, "LAST_STL_NO");

			szYdUpWrkActGp	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WRK_ACT_GP");
			if ("".equals(szYdUpWrkActGp)) {
				szYdUpWrkActGp	= ydDaoUtils.paraRecChkNull(inDto, "YD_UP_WRK_ACT_GP");
			}
			szYdDnWrkActGp	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WRK_ACT_GP");
			if ("".equals(szYdDnWrkActGp)) {
				szYdDnWrkActGp	= ydDaoUtils.paraRecChkNull(inDto, "YD_DN_WRK_ACT_GP");
			}

			szYdStkPos 		= ydDaoUtils.paraRecChkNull(inDto, "YD_DN_WO_LOC");
			szModifier		= ydDaoUtils.paraRecModifier(inDto);
			szYdGp			= ydDaoUtils.paraRecChkNull(inDto, "YD_GP", JPlateYdConst.YD_GP_F_PLATE_YARD);
			szYdStkLyrNo 	= ydDaoUtils.paraRecChkNull(inDto, "YD_DN_WO_LAYER");

			szMsg = "[JSP Session]권하위치 변경 - 변경전위치정보 :: "+ szOldStkPos + "-" +szOldStkLyrNo + ", 야드권상작업수행구분 :: " + szYdUpWrkActGp + ", 야드권하작업수행구분 :: " + szYdDnWrkActGp;
			ydUtils.putLog(SZ_SESSION_NAME,szMethodName,szMsg  , JPlateYdConst.DEBUG);

			if ("".equals(szYdStkPos)) {
				//권하지시위치 정보가 없습니다.
				szRtnMsg = "변경 권하지시위치 정보가 없습니다.";
				szMsg    = "[JSP Session]권하위치 변경 - " + szRtnMsg;
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
        		return szRtnMsg;
			}

			if ("".equals(szYdStkLyrNo)) {

				//권하지시단 정보가 없습니다.
				szRtnMsg = "변경 권하지시단 정보가 없습니다.";
				szMsg    = "[JSP Session]권하위치 변경 - " + szRtnMsg;
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			if (szYdStkPos.length() == 8) {
				szYdStkColGp = ydUtils.substr(szYdStkPos, 0, 6);
				szYdStkBedNo = ydUtils.substr(szYdStkPos, 6, 2);
			} else {
				szRtnMsg = "변경 권하지시위치 정보 길이 오류!";
				szMsg    = "[JSP Session]권하위치 변경 - " + szRtnMsg;
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			if (szYdStkPos.equals(szOldStkPos)) {
        		szRtnMsg = "권하위치 지시위치가 변경되지 않았습니다.";
				szMsg    = "[JSP Session] " + szRtnMsg;
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
        		return szRtnMsg;
			}

			// 신규 위치 적치단 정보
			recPara    = JDTORecordFactory.getInstance().create();
			recTemp    = outRecSet.getRecord();
			outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");

			recPara.setField("YD_STK_COL_GP", 	szYdStkColGp);
			recPara.setField("YD_STK_BED_NO", 	szYdStkBedNo);
			recPara.setField("STL_NO", 			szYdStlNo);

			intRtnVal = ydStkLyrDao.getYdStklyrByColGpBedNo(recPara, outRecSet);		// intGp == 29

			if (intRtnVal == 0) {
				// szStkLyrNo = "001";
				// 데이타를 못찾았을때 최상단 조회
				szYdStkLyrNo = JPlateYdCommonUtils.getTopLyrNoByColGp(szYdStkColGp, szYdStkBedNo, szYdStlNo, szYdUpWrkActGp, "");
				szMsg = "[JSP Session] 신규 위치 적치단 정보를 못찾아서 적치가능 최상단을 다시 조회 결과 >>>> " + szYdStkLyrNo;
				ydUtils.putLog(SZ_SESSION_NAME,szMethodName,szMsg  , JPlateYdConst.DEBUG);

			} else if (intRtnVal > 0) {
				outRecSet.first();
				recTemp 	 = outRecSet.getRecord();
				szYdStkLyrNo = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");
				iYdStkBedCnt = ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_CNT");
			}

			szMsg = "[JSP Session]권하위치 변경 - 신규위치정보 : " + szYdStkColGp + "-" + szYdStkBedNo + "-" +szYdStkLyrNo + ", 베드갯수 : " + iYdStkBedCnt;
			ydUtils.putLog(SZ_SESSION_NAME,szMethodName,szMsg  , JPlateYdConst.DEBUG);

			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(inDto, "YD_CRN_SCH_ID"));
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);

			outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
			intRtnVal = ydCrnWrkMtlDao.getBySchIdStlNo(recPara, outRecSet);

			if (intRtnVal < 1) {
				//해당 스케줄에 해당되는 재료가 없습니다.
				szRtnMsg = "스케줄 해당되는 재료가 미존재";
				szMsg    = "[JSP Session]권하위치 변경 - " + szRtnMsg;
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}
			iCrMtlCnt = intRtnVal;

			szMsg = "[JSP Session] 해당 스케줄에 해당되는 재료 건수 :: " + Integer.toString(iCrMtlCnt);
    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			outRecSet.first();

			for (int ii=0; ii<outRecSet.size(); ii++) {

				recTemp = JDTORecordFactory.getInstance().create();
				recTemp = outRecSet.getRecord(ii);

				// ------------------------------------
				// DEBUG 용도 나중에 삭제
				// ------------------------------------
				szMsg = "[JSP Session] >>>> DEBUG >>>> " + recTemp.toString();
	    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				// ------------------------------------

				if ("F".equals(ydUtils.substr(szOldStkPos, 0, 1))) {
					// 기존 지시위치 Clear
					recSet  = JDTORecordFactory.getInstance().create();
	                recSet.setField("STL_NO",              	ydDaoUtils.paraRecChkNull(recTemp, "STL_NO"));
		            recSet.setField("YD_STK_LYR_MTL_STAT", 	"D");
		            recSet.setField("YD_GP",				szYdGp);
	                recSet.setField("MODIFIER", 			szModifier);

	                intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recSet);		// intGp == 0

	            	if (intRtnVal < 1) {
	            		szMsg = "[JSP Session]권하위치 변경 - 기존 지시위치 에 쌓여 있는 정보 Clear 실패";
	            		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				//		return JPlateYdConst.RETN_CD_FAILURE;
					}
				}

				// 보수장/가스장일때 적치가능위치 재검색
				szYdStkEqpGp = ydUtils.substr(szYdStkPos, 2, 2);
				if ("BS".equals(szYdStkEqpGp) || "CN".equals(szYdStkEqpGp)) {

            		szMsg = "[JSP Session]권하위치 변경 - 보수장/가스장일때 권하위치 재검색 Start";
            		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    	        if ("BS".equals(szYdStkEqpGp)) {

	    	        	szYdDnWrLoc   = ydStkLyrDao.getEmptyBsLoc(szYdStkColGp, szYdStkBedNo, szYdStlNo);
	        	        szYdDnWrLayer = "001";
	                    szMsg = "["+szOperationName+"] 보수장일때 적치가능 베드 다시 조회 >>>> " + szYdDnWrLoc;
	        			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    	        } else if ("CN".equals(szYdStkEqpGp)) {

	    	        	szYdDnWrLoc   = ydStkLyrDao.getEmptyCncLoc(szYdStkColGp, szYdStkBedNo, szYdStlNo);
	        	        szYdDnWrLayer = "001";
	                    szMsg = "["+szOperationName+"] 가스장일때 적치가능 베드 다시 조회 >>>> " + szYdDnWrLoc;
	        			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	    	        }
	    	        szYdStkColGp  = ydUtils.substr(szYdDnWrLoc, 0, 6);
	    	        szYdStkBedNo  = ydUtils.substr(szYdDnWrLoc, 6, 2);
					szYdDnWrBedNo = szYdStkBedNo;

				} else {

					// 파일링 작업업 아니고 적치매수가 1이상일때
					if (iCrMtlCnt > 1 && !"P".equals(szYdUpWrkActGp)) {

						szStlNo		= ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");

						// 크레인작업재료의 적치단(권상시단정보)를 참조하여
						if (ii == 0) {
							szSaveBedNo = szYdStkBedNo;
							szSaveLyrNo = szYdStkLyrNo;
							szTopLyrNo	= ydDaoUtils.paraRecChkNull(recTemp, "TOP_LYR_NO", "000");
							szTopLyrNo  = ydDaoUtils.stringPlusInt(szTopLyrNo,  1);		// 적치단을    1증가
						} else {
							// 권상위치의 적치단이 변경시
							if (!szTempLyrNo.equals(ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO"))) {

								szSaveBedNo = szYdStkBedNo;											// 적치베드 처음으로 이동
								szSaveLyrNo = ydDaoUtils.stringPlusInt(szSaveLyrNo, 1);				// 적치단을    1증가

								iBedUseCnt  = ydDaoUtils.paraRecChkNullInt(recTemp, "BED_USE_CNT");	// 동일베드 적치 건수 - 횡작업여부 체크

								if (iCrMtlCnt > 3 || iBedUseCnt > 1) {
									szTopLyrNo = ydDaoUtils.stringPlusInt(szTopLyrNo,  1);			// 적치단을    1증가
								}

			                    szMsg = "["+szOperationName+"] szTopLyrNo >>>> " + szTopLyrNo;
			        			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

							} else {
								szSaveBedNo = ydDaoUtils.stringPlusInt2(szSaveBedNo, 1);			// 적치베드를 1증가, 단은 그대로 놔둠
							}

						}
						szTempLyrNo = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");

			            // szYdStkBedNo , szYdDnWrLayer 를 다시 Set
			            szYdDnWrBedNo = szSaveBedNo;
			            szYdDnWrLayer = szSaveLyrNo;

						// 강제권상시 권상위치 정보로 적치베드,적치단을 SET
			            if ("F".equals(szYdUpWrkActGp)) {
			            	szYdDnWrBedNo = ydUtils.substr(ydDaoUtils.paraRecChkNull(recTemp, "YD_UP_WO_LOC"), 6, 2);
							// 권하베드가 2베드이고 권상베드가 3베드일때
							if (!"".equals(szYdDnWrBedNo) && Integer.parseInt(szYdDnWrBedNo) > iYdStkBedCnt) {
								szYdDnWrBedNo = ydUtils.addLeftStr(Integer.toString(iYdStkBedCnt), 2, '0');
							}
			           		szYdDnWrLayer = JPlateYdCommonUtils.getTopLyrNoByColGp(szYdStkColGp, szYdDnWrBedNo, szStlNo, szYdUpWrkActGp, szTopLyrNo);
			            }

					} else {
						szYdDnWrBedNo = szYdStkBedNo;
						szYdDnWrLayer = ydDaoUtils.stringPlusInt(szYdStkLyrNo, ii);
					}
				}

				// 신규위치에 정보를 Setting
				recSet  = JDTORecordFactory.getInstance().create();
				recSet.setField("YD_STK_COL_GP",       	szYdStkColGp);
//	            recSet.setField("YD_STK_BED_NO",       	szYdStkBedNo);
	            recSet.setField("YD_STK_BED_NO",       	szYdDnWrBedNo);
	            recSet.setField("YD_STK_LYR_NO",       	szYdDnWrLayer);
	            recSet.setField("YD_STK_LYR_MTL_STAT", 	"D");
                recSet.setField("STL_NO",              	recTemp.getField("STL_NO"));
                recSet.setField("MODIFIER",				szModifier);

                intRtnVal = ydStkLyrDao.updYdStklyrDownStat(recSet);
            	if (intRtnVal < 1) {
					//신규위치에 정보를 Setting 실패
            		szRtnMsg = "신규위치에 정보를 Setting 실패";
            		szMsg    = "[JSP Session]권하위치 변경 - " + szRtnMsg;
            		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
            		return szRtnMsg;
				}
			}

			// 권하지시 위치의 적치열/적치베드 / 적치단 정보로 해당 적치단의 위치값을 읽어온다.
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_STK_COL_GP",       szYdStkColGp);
			recPara.setField("YD_STK_BED_NO",       szYdStkBedNo);
			recPara.setField("YD_STK_LYR_NO",       szYdStkLyrNo) ;

            rsStkLyr  = JDTORecordFactory.getInstance().createRecordSet("YD");

            intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr);		// intGp == 0

            if (intRtnVal < 0 ) {

            	szMsg = "[JSP Session - " + szOperationName + "[ - 적치단 정보 조회 ERROR";
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

            } else if (intRtnVal == 0) {
            	szMsg = "[JSP Session - " + szOperationName + "[ - 적치단 정보가 없습니다.";
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

            } else {
            	szMsg = "[JSP Session - " + szOperationName + "[ - 적치단 조회 완료(좌표)";
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
            }

        	rsStkLyr.first();
            recTemp = rsStkLyr.getRecord();

			// 권하위치 정보 스케줄 정보에서 변경
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setRecord(recCrnSch);
			recPara.setField("YD_CRN_SCH_ID", 			ydDaoUtils.paraRecChkNull(inDto, "YD_CRN_SCH_ID"));
			recPara.setField("YD_DN_WO_LOC", 			ydDaoUtils.paraRecChkNull(inDto, "YD_DN_WO_LOC"));
			recPara.setField("YD_DN_WO_LAYER", 			szYdStkLyrNo);
			recPara.setField("YD_DN_WO_LOC_XAXIS",		ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_XAXIS"));
			recPara.setField("YD_DN_WO_LOC_YAXIS",		ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_YAXIS"));
			recPara.setField("YD_DN_WO_LOC_ZAXIS",		ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_ZAXIS"));
			recPara.setField("MODIFIER", 				szModifier);
			recPara.setField("YD_DN_WO_FLAG",			"Y");				// 권하위치 변경 FLAG
			recPara.setField("YD_DN_WRK_ACT_GP",		szYdDnWrkActGp);	// 야드권하작업수행구분 : 강제권하

			szRtnMsg = JPlateYdCrnSchUtil.uptCrnSchXYCord(recPara);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				//권하위치 스케줄 정보 변경 실패
				szRtnMsg = "권하위치 스케줄 정보 변경 실패!";
				szMsg    = "[JSP Session]권하위치 변경 - " + szRtnMsg;
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
        		return szRtnMsg;
			}

			//-----------------------------------
			// 권하위치 변경  후  작업 지시 L2 재전송
			//-----------------------------------
			szMsg = "["+szOperationName+"] 권하위치변경후 .. 크레인작업지시(YDY7L004) 전문 송신 START";
    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    		recPara = JDTORecordFactory.getInstance().create();
    		//작업지시 전문 전송 data setup
    		recPara.setField("MSG_ID", 				"YDY7L004");
    		recPara.setField("YD_CRN_SCH_ID",    	ydDaoUtils.paraRecChkNull(inDto, "YD_CRN_SCH_ID"));
    		recPara.setField("YD_WRK_PROG_STAT", 	"2");
    		recPara.setField("YD_SCH_CD",        	ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_CD"));
    		recPara.setField("YD_GP",            	JPlateYdConst.YD_GP_F_PLATE_YARD);
    		recPara.setField("MODIFIER", 			szModifier);
    		recPara.setField("MSG_GP", 				"U");
        	szRtnMsg = ydDelegate.sendMsg(recPara);

			szMsg = "["+szOperationName+"] 권하위치변경후 .. 크레인작업지시(YDY7L004) 전문 송신 END >>>> " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			szLogMsg = "[JSP Session]권하위치 변경 에러발생 : ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION [ 권하위치 변경 (크레인 상태관리화면)] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}	// end of updCrnDnPrsFix

  //---------------------------------------------------------------------------

	/**********************************************************
	* 1후판정정추가 SJH16 
	**********************************************************/	
	
    /**
     * 오퍼레이션명 : 1후판정정야드 크레인 리스케줄
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return int
     * @throws JDTOException
     */
    public int procY2CrnReSch(JDTORecord msgRecord)throws JDTOException  {

    	JDTORecordSet 	outRecSet 		= null;
        JDTORecordSet 	rsReSchResult 	= JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecord 		recTemp 		= null;

        int intRtnVal 					= 0 ;
        String szMsg              		= "";
        String szMethodName       		= "procY2CrnReSch";

        String szEqpId                  = "";
        String szYdGp                   = "";
        String szYdBayGp                = "";

        boolean bRtnCheck               = true;

        String szRcvTcCode = ydUtils.getTcCode(msgRecord);

        if (szRcvTcCode == null) {
        	szMsg = "[ERROR] "+SZ_SESSION_NAME+ "::"+szMethodName+ "() TC Code Error ("+szRcvTcCode+ ")";
        	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
        	return -1;
        }

        if (bDebugFlag) {
            szMsg = "[DEBUG] 전문수신 : TCCODE= " +szRcvTcCode ;
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
        }

        try {

        	//파라미터로 넘어온 설비 ID로 설비 상태를 Check
        	szEqpId   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
        	//2후판 공통
        	bRtnCheck = this.eqpStatCheckY7(szEqpId);

        	//설비아이디의 야드구분 동구분을 빼서 스케줄기준Table조회
        	szYdGp    = szEqpId.substring(0,1);
        	szYdBayGp = szEqpId.substring(1,2);
        	recTemp   = JDTORecordFactory.getInstance().create();
        	recTemp.setField("YD_GP",     szYdGp);
        	recTemp.setField("YD_BAY_GP", szYdBayGp);
        	outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
        	
        	
        	
        	intRtnVal = ydSchRuleDao.getYdSchruleYdBayGp(recTemp, outRecSet);
			if (intRtnVal <= 0) {
				szMsg = "스케줄기준이 조회가 되지않았습니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return -1;
			}

			if (bRtnCheck) {
				//------------------------------------------------------------------------------------------------
				// 복구 리스케줄 호출 LOG 추가
				//------------------------------------------------------------------------------------------------
				szMsg = "procY2CrnReSch - 복구 리스케줄 호출";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				//2후판 공통
				intRtnVal = this.resPairReSchY7(szEqpId, outRecSet, rsReSchResult);
				if (intRtnVal == -1) {
					szMsg = "복구 리스케줄 처리 Error";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return -1;
				}
			} else {
				//------------------------------------------------------------------------------------------------
				// 고장  리스케줄 호출 LOG 추가
				//------------------------------------------------------------------------------------------------
				szMsg = "procY2CrnReSch - 고장 리스케줄 호출";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				//2후판 공통
				intRtnVal = this.disableReSchY7(szEqpId, outRecSet, rsReSchResult);
				if (intRtnVal == -1) {
					szMsg = "고장 리스케줄 처리 Error";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return -1;
				}
			}
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "크레인 리스케줄("+szMethodName+ ") 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		return 0;

	} //end of procY2CrnReSch()	
    
	/**
	 * 1후판정정 권하위치 변경 (크레인 상태관리화면)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public String updCrnDnPrsFixYdP(JDTORecord inDto) throws DAOException {

		int 		intRtnVal 		= 0;
		String 		szMsg			= "";
		String 		szMethodName 	= "updCrnDnPrsFixYdP";
		String 		szOperationName = "권하위치 변경";
		String		szModifier		= null;
		JDTORecord  recPara 		= null;
		JDTORecord  recTemp 		= null;
		JDTORecord  recCrnSch 		= null;
		JDTORecord  recSet 			= null;

		String 		szYdStkPos   	= null;
		String 		szYdStkColGp 	= null;
		String		szYdStkEqpGp	= null;
		String 		szYdStkBedNo 	= null;
		String 		szYdStkLyrNo 	= null;
		String		szYdStlNo		= null;
		String		szYdGp			= null;
    	String		szYdDnWrLoc 	= null;
        String		szYdDnWrBedNo	= null;
        String		szYdDnWrLayer 	= null;
        String		szYdUpWrkActGp	= null;			// 야드권상작업수행구분(YD_UP_WRK_ACT_GP) 항목사용 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)
        String		szYdDnWrkActGp	= null;			// 야드권하작업수행구분(YD_DN_WRK_ACT_GP) 항목사용 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)

		String 		szOldStkPos   	= null;
		String 		szOldStkLyrNo 	= null;
		int			iCrMtlCnt		= 0;

		JPlateYdStkLyrDAO 		ydStkLyrDao 	= new JPlateYdStkLyrDAO();
		JPlateYdCrnSchDAO 		ydCrnSchDao 	= new JPlateYdCrnSchDAO();
		JPlateYdCrnWrkMtlDAO  	ydCrnWrkMtlDao 	= new JPlateYdCrnWrkMtlDAO();

		JDTORecordSet	outRecSet  	= null;
		JDTORecordSet   rsStkLyr  	= null;

		String 		szLogMsg 		= null;
		String 		szRtnMsg 		= null;

		// 1건 이상의 작업재료 존재시 임시 사용변수
		String		szTempLyrNo		= "";
		String		szSaveLyrNo		= "";
		String		szSaveBedNo		= "";

		String		szStlNo			= "";
		String		szTopLyrNo		= "";
		int			iBedUseCnt		= 0;
		int			iYdStkBedCnt	= 0;		// 권하위치의 베드 갯수

		try {

			szLogMsg = "JSP-SESSION [ 권하위치 변경 (크레인 상태관리화면)] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			// 1. 기존 To 위치 정보 와 변경 To 위치 정보
			recPara   = JDTORecordFactory.getInstance().create();
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

			recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(inDto, "YD_CRN_SCH_ID"));

			intRtnVal = ydCrnSchDao.getYdCrnSch(recPara, outRecSet);		// intGp == 0

			if (intRtnVal < 1) {
				szRtnMsg = "해당 스케줄 정보: "+inDto.getField("YD_CRN_SCH_ID")+ "가 미존재!";
				szMsg    = "[JSP Session]권하위치 변경 - " + szRtnMsg;
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			recCrnSch = JDTORecordFactory.getInstance().create();

			outRecSet.first();
			recCrnSch.setRecord(outRecSet.getRecord());

			szOldStkPos   	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC");
			szOldStkLyrNo 	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LAYER");
			szYdStlNo		= ydDaoUtils.paraRecChkNull(recCrnSch, "LAST_STL_NO");

			szYdUpWrkActGp	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WRK_ACT_GP");
			if ("".equals(szYdUpWrkActGp)) {
				szYdUpWrkActGp	= ydDaoUtils.paraRecChkNull(inDto, "YD_UP_WRK_ACT_GP");
			}
			szYdDnWrkActGp	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WRK_ACT_GP");
			if ("".equals(szYdDnWrkActGp)) {
				szYdDnWrkActGp	= ydDaoUtils.paraRecChkNull(inDto, "YD_DN_WRK_ACT_GP");
			}

			szYdStkPos 		= ydDaoUtils.paraRecChkNull(inDto, "YD_DN_WO_LOC");
			szModifier		= ydDaoUtils.paraRecModifier(inDto);
			szYdGp			= ydDaoUtils.paraRecChkNull(inDto, "YD_GP", JPlateYdConst.YD_GP_P_PLATE_YARD);
			szYdStkLyrNo 	= ydDaoUtils.paraRecChkNull(inDto, "YD_DN_WO_LAYER");

			szMsg = "[JSP Session]권하위치 변경 - 변경전위치정보 :: "+ szOldStkPos + "-" +szOldStkLyrNo + ", 야드권상작업수행구분 :: " + szYdUpWrkActGp + ", 야드권하작업수행구분 :: " + szYdDnWrkActGp;
			ydUtils.putLog(SZ_SESSION_NAME,szMethodName,szMsg  , JPlateYdConst.DEBUG);

			if ("".equals(szYdStkPos)) {
				//권하지시위치 정보가 없습니다.
				szRtnMsg = "변경 권하지시위치 정보가 없습니다.";
				szMsg    = "[JSP Session]권하위치 변경 - " + szRtnMsg;
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
        		return szRtnMsg;
			}

			if ("".equals(szYdStkLyrNo)) {

				//권하지시단 정보가 없습니다.
				szRtnMsg = "변경 권하지시단 정보가 없습니다.";
				szMsg    = "[JSP Session]권하위치 변경 - " + szRtnMsg;
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			if (szYdStkPos.length() == 8) {
				szYdStkColGp = ydUtils.substr(szYdStkPos, 0, 6);
				szYdStkBedNo = ydUtils.substr(szYdStkPos, 6, 2);
			} else {
				szRtnMsg = "변경 권하지시위치 정보 길이 오류!";
				szMsg    = "[JSP Session]권하위치 변경 - " + szRtnMsg;
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			if (szYdStkPos.equals(szOldStkPos)) {
        		szRtnMsg = "권하위치 지시위치가 변경되지 않았습니다.";
				szMsg    = "[JSP Session] " + szRtnMsg;
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
        		return szRtnMsg;
			}

			// 신규 위치 적치단 정보
			recPara    = JDTORecordFactory.getInstance().create();
			recTemp    = outRecSet.getRecord();
			outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");

			recPara.setField("YD_STK_COL_GP", 	szYdStkColGp);
			recPara.setField("YD_STK_BED_NO", 	szYdStkBedNo);
			recPara.setField("STL_NO", 			szYdStlNo);
			intRtnVal = ydStkLyrDao.getYdStklyrByColGpBedNo(recPara, outRecSet);		// intGp == 29

			if (intRtnVal == 0) {
				// szStkLyrNo = "001";
				// 데이타를 못찾았을때 최상단 조회
				szYdStkLyrNo = JPlateYdCommonUtils.getTopLyrNoByColGp(szYdStkColGp, szYdStkBedNo, szYdStlNo, szYdUpWrkActGp, "");
				szMsg = "[JSP Session] 신규 위치 적치단 정보를 못찾아서 적치가능 최상단을 다시 조회 결과 >>>> " + szYdStkLyrNo;
				ydUtils.putLog(SZ_SESSION_NAME,szMethodName,szMsg  , JPlateYdConst.DEBUG);

			} else if (intRtnVal > 0) {
				outRecSet.first();
				recTemp 	 = outRecSet.getRecord();
				szYdStkLyrNo = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");
				iYdStkBedCnt = ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_CNT");
			}
			
			// 횡작업, 멀티작업 인 경우 최상단을 구한다.
			// 야드권상작업수행구분(YD_UP_WRK_ACT_GP) 항목사용 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)
			if ("H".equals(szYdUpWrkActGp) || "M".equals(szYdUpWrkActGp)) {
				szYdStkLyrNo = JPlateYdCommonUtils.getTopLyrNoByColGp(szYdStkColGp, szYdStkBedNo, szYdStlNo, szYdUpWrkActGp, "");
				szMsg = "[JSP Session] 횡작업, 멀티작업인 경우 적치가능 최상단을 다시 조회 결과 >>>> " + szYdStkLyrNo;
				ydUtils.putLog(SZ_SESSION_NAME,szMethodName,szMsg  , JPlateYdConst.DEBUG);
			}

			szMsg = "[JSP Session]권하위치 변경 - 신규위치정보 : " + szYdStkColGp + "-" + szYdStkBedNo + "-" +szYdStkLyrNo + ", 베드갯수 : " + iYdStkBedCnt;
			ydUtils.putLog(SZ_SESSION_NAME,szMethodName,szMsg  , JPlateYdConst.DEBUG);

			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(inDto, "YD_CRN_SCH_ID"));
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);

			outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
			/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getBySchIdStlNo 
			-- 크레인 작업재료 조회 (적치단,적치베드순)

			SELECT Z.*
			  FROM
			(
			    SELECT X.*
			           -- 파일링/횡작업후 권하예정 정보가 지워질수 있음으로 건수 체크하여 처리
			         , CASE WHEN X.CRNMTL_CNT = X.STKLYR_CNT
			                THEN X.YD_DN_WO_LOC_NEW
			                ELSE X.YD_DN_WO_LOC_OLD
			           END    AS YD_DN_WO_LOC
			         , CASE WHEN X.CRNMTL_CNT = X.STKLYR_CNT
			                THEN X.YD_DN_WO_LAYER_NEW
			                ELSE X.YD_DN_WO_LAYER_OLD
			           END    AS YD_DN_WO_LAYER
			         , MAX(X.BED_CNT) OVER()    AS BED_USE_CNT
			      FROM
			    (
			        SELECT A.YD_CRN_SCH_ID      AS YD_CRN_SCH_ID
			             , A.STL_NO             AS STL_NO
			             , A.YD_AID_WRK_YN      AS YD_AID_WRK_YN
			             , A.YD_STK_LYR_NO      AS YD_STK_LYR_NO
			             , A.YD_STK_LOT_TP      AS YD_STK_LOT_TP
			             , A.YD_STK_LOT_CD      AS YD_STK_LOT_CD
			             , A.HCR_GP             AS HCR_GP
			             , A.STL_PROG_CD        AS STL_PROG_CD
			             , A.YD_MTL_ITEM        AS YD_MTL_ITEM
			             , A.YD_ROUTE_GP        AS YD_ROUTE_GP
			             , C.YD_MTL_T           AS YD_MTL_T
			             , C.YD_MTL_W           AS YD_MTL_W
			             , C.YD_MTL_L           AS YD_MTL_L
			             , C.YD_MTL_WT          AS YD_MTL_WT

			             -- 권상지시위치 정보
			             , SUBSTR(B.YD_UP_WO_LOC,1,6) || A.YD_STK_LOT_TP    AS YD_UP_WO_LOC
			             , NVL(A.YD_STK_LYR_NO, B.YD_UP_WO_LAYER)           AS YD_UP_WO_LAYER

			             -- 권하지시위치 정보
			             , CASE WHEN SUBSTR(B.YD_DN_WO_LOC,1,2) = 'XX' THEN
			                         B.YD_DN_WO_LOC
			                    ELSE NVL((D.YD_STK_COL_GP || D.YD_STK_BED_NO), (SUBSTR(B.YD_DN_WO_LOC,1,6) || A.YD_STK_LOT_TP))
			               END                      AS YD_DN_WO_LOC_NEW
			             -- 권하지시단 정보
			             , NVL(D.YD_STK_LYR_NO, NVL(B.YD_DN_WO_LAYER, '001'))   AS YD_DN_WO_LAYER_NEW

			             , B.YD_DN_WO_LOC           AS YD_DN_WO_LOC_OLD
			             , B.YD_DN_WO_LAYER         AS YD_DN_WO_LAYER_OLD

			             , COUNT(1)        OVER()   AS CRNMTL_CNT
			             , COUNT(D.STL_NO) OVER()   AS STKLYR_CNT

			             -- 신규저장위치 최상단
			             , (SELECT NVL(MAX(S.YD_STK_LYR_NO), '000')
			                  FROM TB_YD_STKLYR S
			                 WHERE S.YD_STK_COL_GP = :V_YD_STK_COL_GP
			                   AND S.YD_STK_LYR_MTL_STAT IN ('C', 'U')
			               )                        AS TOP_LYR_NO

			             , COUNT(1) OVER(PARTITION BY A.YD_STK_LOT_TP) AS BED_CNT

			          FROM TB_YD_CRNWRKMTL      A
			             , TB_YD_CRNSCH         B
			             , TB_YD_SHRSTOCK       C
			             , TB_YD_STKLYR         D

			         WHERE A.YD_CRN_SCH_ID  = :V_YD_CRN_SCH_ID
			           AND A.STL_NO      LIKE :V_STL_NO || '%'
			           AND A.YD_CRN_SCH_ID  = B.YD_CRN_SCH_ID
			           AND A.STL_NO         = C.STL_NO
			           AND A.STL_NO         = D.STL_NO(+)
			           AND D.YD_STK_LYR_MTL_STAT(+) = 'D'
			           AND A.DEL_YN         = 'N'
			           AND B.DEL_YN         = 'N'
			           AND C.DEL_YN         = 'N'
			           AND D.DEL_YN(+)      = 'N'
			    ) X
			) Z
			 ORDER BY Z.YD_DN_WO_LAYER, Z.YD_DN_WO_LOC, YD_UP_WO_LAYER, YD_UP_WO_LOC
			 */
			 
			intRtnVal = ydCrnWrkMtlDao.getBySchIdStlNo(recPara, outRecSet);

			if (intRtnVal < 1) {
				//해당 스케줄에 해당되는 재료가 없습니다.
				szRtnMsg = "스케줄 해당되는 재료가 미존재";
				szMsg    = "[JSP Session]권하위치 변경 - " + szRtnMsg;
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}
			iCrMtlCnt = intRtnVal;

			szMsg = "[JSP Session] 해당 스케줄에 해당되는 재료 건수 :: " + Integer.toString(iCrMtlCnt);
    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			outRecSet.first();

			for (int ii=0; ii<outRecSet.size(); ii++) {

				recTemp = JDTORecordFactory.getInstance().create();
				recTemp = outRecSet.getRecord(ii);

				// ------------------------------------
//				if ("F".equals(ydUtils.substr(szOldStkPos, 0, 1))) {

				if ("P".equals(ydUtils.substr(szOldStkPos, 0, 1))) {
					// 기존 지시위치 Clear
					recSet  = JDTORecordFactory.getInstance().create();
	                recSet.setField("STL_NO",              	ydDaoUtils.paraRecChkNull(recTemp, "STL_NO"));
		            recSet.setField("YD_STK_LYR_MTL_STAT", 	"D");
		            recSet.setField("YD_GP",				szYdGp);
	                recSet.setField("MODIFIER", 			szModifier);
	                /* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrClearByStlNo 

	                UPDATE TB_YD_STKLYR
	                   SET MODIFIER             = :V_MODIFIER
	                     , MOD_DDTT             = SYSDATE
	                     , STL_NO               = ''
	                     , YD_STK_LYR_MTL_STAT  = 'E'
	                     , YD_OCPY_BED_GP       = ''
	                     , YD_OCPY_STK_BED_NO   = ''
	                     , YD_OCPY_STK_LYR_NO   = ''
	                 WHERE STL_NO = :V_STL_NO
	                   AND YD_STK_LYR_MTL_STAT LIKE :V_YD_STK_LYR_MTL_STAT || '%'
	                   AND YD_STK_COL_GP       LIKE NVL(:V_YD_GP, 'F') || '%'
	                */   
	                intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recSet);		// intGp == 0

	            	if (intRtnVal < 1) {
	            		szMsg = "[JSP Session]권하위치 변경 - 기존 지시위치 에 쌓여 있는 정보 Clear 실패";
	            		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				//		return JPlateYdConst.RETN_CD_FAILURE;
					}
				}

				// 보수장/가스장일때 적치가능위치 재검색
				szYdStkEqpGp = ydUtils.substr(szYdStkPos, 2, 2);
				if ("BS".equals(szYdStkEqpGp) || "CN".equals(szYdStkEqpGp)|| "BC".equals(szYdStkEqpGp)) {

            		szMsg = "[JSP Session]권하위치 변경 - 보수장/가스장일때 권하위치 재검색 Start";
            		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    	        if ("BS".equals(szYdStkEqpGp)||"BC".equals(szYdStkEqpGp)) {
	    	        	/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getEmptyBsLoc 
	    	        	-- 적치가능한 보수장 베드정보 조회

	    	        	SELECT
	    	        	        Z.*
	    	        	  FROM
	    	        	(
	    	        	SELECT
	    	        	       A.YD_STK_COL_GP          AS YD_STK_COL_GP            -- 야드적치열구분
	    	        	     , A.YD_STK_BED_NO          AS YD_STK_BED_NO            -- 야드적치BED번호
	    	        	     , A.YD_STK_LYR_NO          AS YD_STK_LYR_NO            -- 적치단
	    	        	     , A.STL_NO                 AS STL_NO                   -- 제품번호
	    	        	     , A.YD_STK_LYR_ACT_STAT    AS YD_STK_LYR_ACT_STAT      -- 야드적치단활성상태
	    	        	     , A.YD_STK_LYR_MTL_STAT    AS YD_STK_LYR_MTL_STAT      -- 야드적치단재료상태
	    	        	     , A.YD_OCPY_BED_GP         AS YD_OCPY_BED_GP           -- 야드점유Bed구분
	    	        	     , A.YD_OCPY_STK_BED_NO     AS YD_OCPY_STK_BED_NO       -- 야드점유적치Bed번호
	    	        	     , A.YD_OCPY_STK_LYR_NO     AS YD_OCPY_STK_LYR_NO       -- 야드점유적치단번호
	    	        	     , CASE WHEN A.STL_NO IS NOT NULL THEN '1'
	    	        	            WHEN (A.YD_STK_COL_GP || A.YD_STK_BED_NO) = (:V_YD_STK_COL_GP || :V_YD_STK_BED_NO) THEN '2' 
	    	        	            ELSE '9' 
	    	        	       END SORT_SEQ
	    	        	  FROM TB_YD_STKLYR A
	    	        	 WHERE A.YD_STK_COL_GP LIKE SUBSTR(:V_YD_STK_COL_GP,1,5) || '%'
	    	        	   AND A.DEL_YN = 'N'
	    	        	   AND A.YD_STK_LYR_ACT_STAT = 'E'                          -- 적치베드 활성상태
	    	        	   AND A.YD_STK_LYR_MTL_STAT IN ('E','D')                   -- 야드적치상태
	    	        	   AND (A.STL_NO IS NULL OR A.STL_NO = :V_STL_NO)
	    	        	) Z   
	    	        	 ORDER BY Z.SORT_SEQ, Z.YD_STK_COL_GP, Z.YD_STK_BED_NO, Z.YD_STK_LYR_NO DESC
	    	        	 */
	    	        	szYdDnWrLoc   = ydStkLyrDao.getEmptyBsLoc(szYdStkColGp, szYdStkBedNo, szYdStlNo);
	        	        szYdDnWrLayer = "001";
	        	        
	        	        //------------------
	        	        szYdStkLyrNo = "001";  //가스장, 보수장은  001 단 만 존재 ***
	        	        //------------------
	        	        
	                    szMsg = "["+szOperationName+"] 보수장일때 적치가능 베드 다시 조회 >>>> " + szYdDnWrLoc;
	        			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    	        } else if ("CN".equals(szYdStkEqpGp)) {
	    	        	/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getEmptyCncLoc 
	    	        	-- 적치가능한 가스장 베드정보 조회

	    	        	SELECT
	    	        	        Z.*
	    	        	  FROM
	    	        	(
	    	        	    SELECT
	    	        	           A.YD_STK_COL_GP          AS YD_STK_COL_GP            -- 야드적치열구분
	    	        	         , A.YD_STK_BED_NO          AS YD_STK_BED_NO            -- 야드적치BED번호
	    	        	         , A.YD_STK_LYR_NO          AS YD_STK_LYR_NO            -- 적치단
	    	        	         , A.STL_NO                 AS STL_NO                   -- 제품번호
	    	        	         , A.YD_STK_LYR_ACT_STAT    AS YD_STK_LYR_ACT_STAT      -- 야드적치단활성상태
	    	        	         , A.YD_STK_LYR_MTL_STAT    AS YD_STK_LYR_MTL_STAT      -- 야드적치단재료상태
	    	        	         , A.YD_OCPY_BED_GP         AS YD_OCPY_BED_GP           -- 야드점유Bed구분
	    	        	         , A.YD_OCPY_STK_BED_NO     AS YD_OCPY_STK_BED_NO       -- 야드점유적치Bed번호
	    	        	         , A.YD_OCPY_STK_LYR_NO     AS YD_OCPY_STK_LYR_NO       -- 야드점유적치단번호
	    	        	         , CASE WHEN A.STL_NO IS NOT NULL THEN '1'
	    	        	                WHEN (A.YD_STK_COL_GP || A.YD_STK_BED_NO) = (:V_YD_STK_COL_GP || :V_YD_STK_BED_NO) THEN '2'
	    	        	                WHEN SUBSTR(A.YD_STK_COL_GP, 5, 2) IN ('11','13') THEN '91'
	    	        	                ELSE '92'
	    	        	           END                      AS SORT_SEQ1
	    	        	         , (SELECT COUNT(1)
	    	        	              FROM TB_YD_STKLYR X
	    	        	             WHERE X.YD_STK_COL_GP  = A.YD_STK_COL_GP
	    	        	               AND X.YD_STK_BED_NO >= A.YD_STK_BED_NO
	    	        	               AND X.YD_STK_BED_NO <  (SELECT NVL(MIN(Y.YD_STK_BED_NO), '99') FROM TB_YD_STKLYR Y
	    	        	                                        WHERE Y.YD_STK_COL_GP  = A.YD_STK_COL_GP
	    	        	                                          AND Y.YD_STK_BED_NO >= A.YD_STK_BED_NO
	    	        	                                          AND Y.YD_STK_LYR_NO  = A.YD_STK_LYR_NO
	    	        	                                          AND Y.YD_STK_LYR_ACT_STAT = 'E'
	    	        	                                          AND Y.YD_STK_LYR_MTL_STAT IN ('C', 'U', 'D')
	    	        	                                      )
	    	        	               AND X.YD_STK_LYR_NO  = A.YD_STK_LYR_NO
	    	        	               AND X.YD_STK_LYR_ACT_STAT = 'E'
	    	        	               AND X.YD_STK_LYR_MTL_STAT = 'E'
	    	        	               AND X.DEL_YN = 'N'
	    	        	           )                        AS SORT_SEQ2

	    	        	      FROM TB_YD_STKLYR A
	    	        	     WHERE A.YD_STK_COL_GP LIKE SUBSTR(:V_YD_STK_COL_GP,1,5) || '%'
	    	        	--     AND A.YD_STK_COL_GP LIKE 'F_CN%'
	    	        	       AND A.DEL_YN = 'N'
	    	        	       AND A.YD_STK_LYR_ACT_STAT = 'E'                          -- 적치베드 활성상태
	    	        	       AND A.YD_STK_LYR_MTL_STAT IN ('E','D')                   -- 야드적치상태
	    	        	       AND (A.STL_NO IS NULL OR A.STL_NO = :V_STL_NO)
	    	        	) Z
	    	        	 ORDER BY Z.SORT_SEQ1, Z.SORT_SEQ2 DESC, Z.YD_STK_COL_GP, Z.YD_STK_BED_NO, Z.YD_STK_LYR_NO DESC

	    	        	 */
	    	        	szYdDnWrLoc   = ydStkLyrDao.getEmptyCncLoc(szYdStkColGp, szYdStkBedNo, szYdStlNo);
	        	        szYdDnWrLayer = "001";
	        	        
	        	        //------------------
	        	        szYdStkLyrNo = "001";  //가스장, 보수장은  001 단 만 존재 ***
	        	        //------------------
	        	        
	                    szMsg = "["+szOperationName+"] 가스장일때 적치가능 베드 다시 조회 >>>> " + szYdDnWrLoc;
	        			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	    	        }
	    	        szYdStkColGp  = ydUtils.substr(szYdDnWrLoc, 0, 6);
	    	        szYdStkBedNo  = ydUtils.substr(szYdDnWrLoc, 6, 2);
					szYdDnWrBedNo = szYdStkBedNo;

				} else {

					// 파일링 작업업 아니고 적치매수가 1이상일때
					if (iCrMtlCnt > 1 && !"P".equals(szYdUpWrkActGp)) {

						szStlNo		= ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");

						// 크레인작업재료의 적치단(권상시단정보)를 참조하여
						if (ii == 0) {
							szSaveBedNo = szYdStkBedNo;
							szSaveLyrNo = szYdStkLyrNo;
							szTopLyrNo	= ydDaoUtils.paraRecChkNull(recTemp, "TOP_LYR_NO", "000");
							szTopLyrNo  = ydDaoUtils.stringPlusInt(szTopLyrNo,  1);		// 적치단을    1증가
		                    szMsg = "["+szOperationName+"] szTopLyrNo(ii=0) >>>> " + szTopLyrNo;
		        			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						} else {
							// 권상위치의 적치단이 변경시
							if (!szTempLyrNo.equals(ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO"))) {

								szSaveBedNo = szYdStkBedNo;											// 적치베드 처음으로 이동
								szSaveLyrNo = ydDaoUtils.stringPlusInt(szSaveLyrNo, 1);				// 적치단을    1증가

								iBedUseCnt  = ydDaoUtils.paraRecChkNullInt(recTemp, "BED_USE_CNT");	// 동일베드 적치 건수 - 횡작업여부 체크

								if (iCrMtlCnt > 3 || iBedUseCnt > 1) {
									szTopLyrNo = ydDaoUtils.stringPlusInt(szTopLyrNo,  1);			// 적치단을    1증가
								}

			                    szMsg = "["+szOperationName+"] szTopLyrNo >>>> " + szTopLyrNo;
			        			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

							} else {
								szSaveBedNo = ydDaoUtils.stringPlusInt2(szSaveBedNo, 1);			// 적치베드를 1증가, 단은 그대로 놔둠
							}

						}
						szTempLyrNo = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");

			            // szYdStkBedNo , szYdDnWrLayer 를 다시 Set
			            szYdDnWrBedNo = szSaveBedNo;
			            szYdDnWrLayer = szSaveLyrNo;

						// 강제권상시 권상위치 정보로 적치베드,적치단을 SET
			            if ("F".equals(szYdUpWrkActGp)) {
			            	szYdDnWrBedNo = ydUtils.substr(ydDaoUtils.paraRecChkNull(recTemp, "YD_UP_WO_LOC"), 6, 2);
							// 권하베드가 2베드이고 권상베드가 3베드일때
							if (!"".equals(szYdDnWrBedNo) && Integer.parseInt(szYdDnWrBedNo) > iYdStkBedCnt) {
								szYdDnWrBedNo = ydUtils.addLeftStr(Integer.toString(iYdStkBedCnt), 2, '0');
							}
							/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getByLocMtlStat 
							-- 적치열,적치상태로 적치단정보 조회 (점유베드는 SKIP)

							SELECT YD_STK_COL_GP        AS YD_STK_COL_GP
							     , YD_STK_BED_NO        AS YD_STK_BED_NO
							     , YD_STK_LYR_NO        AS YD_STK_LYR_NO
							     , STL_NO               AS STL_NO
							     , YD_STK_LYR_ACT_STAT  AS YD_STK_LYR_ACT_STAT
							     , YD_STK_LYR_MTL_STAT  AS YD_STK_LYR_MTL_STAT
							     , YD_STK_LYR_XAXIS     AS YD_STK_LYR_XAXIS
							     , YD_STK_LYR_YAXIS     AS YD_STK_LYR_YAXIS
							     , YD_STK_LYR_ZAXIS     AS YD_STK_LYR_ZAXIS
							     , YD_OCPY_BED_GP       AS YD_OCPY_BED_GP
							     , YD_OCPY_STK_BED_NO   AS YD_OCPY_STK_BED_NO
							     , YD_OCPY_STK_LYR_NO   AS YD_OCPY_STK_LYR_NO
							  FROM TB_YD_STKLYR
							 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
							   AND DEL_YN        = 'N'
							   AND STL_NO IS NOT NULL
							   AND YD_STK_LYR_MTL_STAT IN (:V_YD_STK_LYR_MTL_STAT1, :V_YD_STK_LYR_MTL_STAT2, :V_YD_STK_LYR_MTL_STAT3)
							 ORDER BY YD_STK_LYR_NO DESC, YD_STK_BED_NO
							 */
							
			           		szYdDnWrLayer = JPlateYdCommonUtils.getTopLyrNoByColGp(szYdStkColGp, szYdDnWrBedNo, szStlNo, szYdUpWrkActGp, szTopLyrNo);
			           		
			            }

					} else {
						szYdDnWrBedNo = szYdStkBedNo;
						szYdDnWrLayer = ydDaoUtils.stringPlusInt(szYdStkLyrNo, ii);
					}
				}
				
				
				//신규 TO위치가 RT 일경우..
				if("RT".equals(ydUtils.substr(szYdStkColGp, 2, 2))) {
					
					// 신규위치에 정보를 Setting 하지 않는다.
					
				} else {
				
					// 신규위치에 정보를 Setting
					recSet  = JDTORecordFactory.getInstance().create();
					recSet.setField("YD_STK_COL_GP",       	szYdStkColGp);
		            recSet.setField("YD_STK_BED_NO",       	szYdDnWrBedNo);
		            recSet.setField("YD_STK_LYR_NO",       	szYdDnWrLayer);
		            recSet.setField("YD_STK_LYR_MTL_STAT", 	"D");
	                recSet.setField("STL_NO",              	recTemp.getField("STL_NO"));
	                recSet.setField("MODIFIER",				szModifier);
	                /* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrDownStat 
	                -- 권하예약 상태로 적치단 변경
	
	                UPDATE TB_YD_STKLYR
	                   SET MODIFIER            = :V_MODIFIER
	                     , MOD_DDTT            = SYSDATE
	                     , STL_NO              = :V_STL_NO
	                     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
	                     , YD_OCPY_BED_GP      = ''
	                     , YD_OCPY_STK_BED_NO  = ''
	                     , YD_OCPY_STK_LYR_NO  = ''
	                 WHERE YD_STK_COL_GP       = SUBSTR(:V_YD_STK_COL_GP,1,6)
	                   AND YD_STK_BED_NO       = :V_YD_STK_BED_NO
	                   AND YD_STK_LYR_NO       = :V_YD_STK_LYR_NO
	                   AND (YD_STK_LYR_MTL_STAT IN ('E','D') OR STL_NO IS NULL)
	               */ 
	                intRtnVal = ydStkLyrDao.updYdStklyrDownStat(recSet);
	            	if (intRtnVal < 1) {
						//신규위치에 정보를 Setting 실패
	            		szRtnMsg = "신규위치에 정보를 Setting 실패";
	            		szMsg    = "[JSP Session]권하위치 변경 - " + szRtnMsg;
	            		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	            		return szRtnMsg;
					}
				}
			}

			// 권하지시 위치의 적치열/적치베드 / 적치단 정보로 해당 적치단의 위치값을 읽어온다.
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_STK_COL_GP",       szYdStkColGp);
			recPara.setField("YD_STK_BED_NO",       szYdStkBedNo);
			recPara.setField("YD_STK_LYR_NO",       szYdStkLyrNo) ;

            rsStkLyr  = JDTORecordFactory.getInstance().createRecordSet("YD");

            /* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getYdStklyr 

            SELECT YD_STK_COL_GP        AS YD_STK_COL_GP
                 , YD_STK_BED_NO        AS YD_STK_BED_NO
                 , YD_STK_LYR_NO        AS YD_STK_LYR_NO
                 , REGISTER             AS REGISTER
                 , TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
                 , MODIFIER             AS MODIFIER
                 , TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
                 , DEL_YN               AS DEL_YN
                 , STL_NO               AS STL_NO
                 , YD_STK_LYR_ACT_STAT  AS YD_STK_LYR_ACT_STAT
                 , YD_STK_LYR_MTL_STAT  AS YD_STK_LYR_MTL_STAT
                 , YD_STK_LYR_XAXIS     AS YD_STK_LYR_XAXIS
                 , YD_STK_LYR_YAXIS     AS YD_STK_LYR_YAXIS
                 , YD_STK_LYR_ZAXIS     AS YD_STK_LYR_ZAXIS
                 , YD_OCPY_BED_GP       AS YD_OCPY_BED_GP
                 , YD_OCPY_STK_BED_NO   AS YD_OCPY_STK_BED_NO
                 , YD_OCPY_STK_LYR_NO   AS YD_OCPY_STK_LYR_NO
              FROM TB_YD_STKLYR
             WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
               AND YD_STK_BED_NO = :V_YD_STK_BED_NO
               AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
               AND DEL_YN = 'N'
            */	   
            intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr);		// intGp == 0

            if (intRtnVal < 0 ) {

            	szMsg = "[JSP Session - " + szOperationName + "[ - 적치단 정보 조회 ERROR";
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

            } else if (intRtnVal == 0) {
            	szMsg = "[JSP Session - " + szOperationName + "[ - 적치단 정보가 없습니다.";
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

            } else {
            	szMsg = "[JSP Session - " + szOperationName + "[ - 적치단 조회 완료(좌표)";
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
            }

        	rsStkLyr.first();
            recTemp = rsStkLyr.getRecord();

			// 권하위치 정보 스케줄 정보에서 변경
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setRecord(recCrnSch);
			recPara.setField("YD_CRN_SCH_ID", 			ydDaoUtils.paraRecChkNull(inDto, "YD_CRN_SCH_ID"));
			recPara.setField("YD_DN_WO_LOC", 			ydDaoUtils.paraRecChkNull(inDto, "YD_DN_WO_LOC"));
			recPara.setField("YD_DN_WO_LAYER", 			szYdStkLyrNo);
			recPara.setField("YD_DN_WO_LOC_XAXIS",		ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_XAXIS"));
			recPara.setField("YD_DN_WO_LOC_YAXIS",		ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_YAXIS"));
			recPara.setField("YD_DN_WO_LOC_ZAXIS",		ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_ZAXIS"));
			recPara.setField("MODIFIER", 				szModifier);
			recPara.setField("YD_DN_WO_FLAG",			"Y");				// 권하위치 변경 FLAG
			recPara.setField("YD_DN_WRK_ACT_GP",		szYdDnWrkActGp);	// 야드권하작업수행구분 : 강제권하

//			szRtnMsg = JPlateYdCrnSchUtil.uptCrnSchXYCord(recPara);
			szRtnMsg = JPlateYdCrnSchUtil.uptCrnSchXYCordYdP(recPara);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				//권하위치 스케줄 정보 변경 실패
				szRtnMsg = "권하위치 스케줄 정보 변경 실패!";
				szMsg    = "[JSP Session]권하위치 변경 - " + szRtnMsg;
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
        		return szRtnMsg;
			}

			//-----------------------------------
			// 권하위치 변경  후  작업 지시 L2 재전송
			//-----------------------------------
			szMsg = "["+szOperationName+"] 권하위치변경후 .. 크레인작업지시(YDY2L004) 전문 송신 START";
    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    		recPara = JDTORecordFactory.getInstance().create();
 
    		//작업지시 전문 전송 data setup
    		//recPara.setField("MSG_ID", 				"YDY2L004");
    		recPara.setField("MSG_ID", 				"YDY2L004V2");
    		recPara.setField("YD_CRN_SCH_ID",    	ydDaoUtils.paraRecChkNull(inDto, "YD_CRN_SCH_ID"));
    		recPara.setField("YD_WRK_PROG_STAT", 	"2");
    		recPara.setField("YD_SCH_CD",        	ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_CD"));
    		recPara.setField("YD_GP",            	JPlateYdConst.YD_GP_P_PLATE_YARD);
    		recPara.setField("MODIFIER", 			szModifier);
    		recPara.setField("MSG_GP", 				"U");
        	szRtnMsg = ydDelegate.sendMsg(recPara);

			szMsg = "["+szOperationName+"] 권하위치변경후 .. 크레인작업지시(YDY2L004) 전문 송신 END >>>> " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			szLogMsg = "[JSP Session]권하위치 변경 에러발생 : ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION [ 권하위치 변경 (크레인 상태관리화면)] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}	// end of updCrnDnPrsFixYdP
	
}
