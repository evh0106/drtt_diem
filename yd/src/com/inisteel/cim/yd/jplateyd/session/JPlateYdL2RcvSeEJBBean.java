/*
 * @(#) 2후판정정야드 L2수신 처리 Session EJB클래스
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/14
 *
 * @description		2후판정정야드 L2수신 처리 Session EJB클래스
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/14   김현우      김현우       최초작성
 * V1.01  2024.11.15               1후판 정정 #2 열처리 Book-In/Book-Out Request(P8YDL501) 처리  
 *                                 procP8BookInOutReq, selY2RtZoneToLoc, selLocToY2RtZone Method 추가   
 */

package com.inisteel.cim.yd.jplateyd.session; 

//UTIL IMPORT
import xlib.cmc.GridData;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.DateHelper;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jplateyd.delegate.JPlateYdDelegate;

import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpPauseDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdCommonUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.15 selY2RtZoneToLoc 1후판 정정야드 RT Zone -> 저장위치 리턴 메소드에서 사용
//-------------------------------------------------------------------------------------------------------------------------
import jspeed.base.record.JDTOException;

/**
 * 2후판정정야드 L2수신 처리
 *
 * @ejb.bean name="JPlateYdL2RcvSeEJB" jndi-name="JPlateYdL2RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */

public class JPlateYdL2RcvSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;

	// Session Name
	private static final String SZ_SESSION_NAME = JPlateYdL2RcvSeEJBBean.class.getName();

	private JPlateYdDaoUtils ydDaoUtils	 = new JPlateYdDaoUtils();
	private JPlateYdUtils    ydUtils     = new JPlateYdUtils();
	private JPlateYdDelegate ydDelegate  = new JPlateYdDelegate();

	// [DEBUG] message flag
	private boolean bDebugFlag    	= true;   
	private int test=0;
	
	private YdPICommDAO   ydPICommDAO   = new YdPICommDAO();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.15 1후판 정정 로그 관련 야드공통 UTIL 
    private YdUtils 		ydLogUtils  = new YdUtils();
// 2024.11.15 selY2RtZoneToLoc 1후판 정정야드 RT Zone -> 저장위치 리턴 메소드에서 사용
    private DBAssistantDAO 	dbAssDao 	= new DBAssistantDAO();
//-------------------------------------------------------------------------------------------------------------------------

	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * 오퍼레이션명 : 2후판정정야드 저장위치제원요구 (Y7YDL001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public void procY7StrLocSpecReq(JDTORecord msgRecord) throws DAOException {
		// 파라미터 NULL 체크 후 레코드 데이터
        JDTORecord getParamRecord  	= JDTORecordFactory.getInstance().create();

        // 델리게이트 호출을 위한 편집 레코드 데이터
        JDTORecord setCrnschRecord 	= JDTORecordFactory.getInstance().create();

        JPlateYdDelegate ydDelegate = new JPlateYdDelegate();

    	String szMethodName     = "procY7StrLocSpecReq";
		String szMsg            = "";
		String szOperationName	= "2후판정정야드L2 저장위치제원요구";
		String szTemp           = "";
		String szRcvTcCode      = ydUtils.getTcCode(msgRecord);

		int nRtnVal             = 0;

		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "\n=========Y7YDL001========\n", JPlateYdConst.DEBUG);
		szMsg = "[" + szOperationName + "] 수신데이타 :: " + msgRecord.toString();
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		if (szRcvTcCode == null) {
			szMsg = SZ_SESSION_NAME + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return;
		}

		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		}

		try {
			//=============================================================
			// Log 테이블 등록
			//=============================================================
			szMsg = "[2후판정정] 저장위치제원요구 수신";
			ydUtils.putLogMsg("A", "", szMsg, "", "", "", "I", "A", "I", szRcvTcCode, SZ_SESSION_NAME, szMethodName);

			// 파라미터 Check
	        nRtnVal = this.paramY7YDL001Check(msgRecord, getParamRecord);

	        if (nRtnVal == -1) {
                szMsg = "파라미터 Check중 Error : " + Integer.toString(nRtnVal);
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
                return;
	        }

	        // 적치열구분 생성
	        szTemp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP")
	               + ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP")
	        	   + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP")
	        	   + ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO");

	        // 델리게이트 호출을 위한 레코드 편집
	        setCrnschRecord.setField("JMS_TC_CD"      , "YDY7L001");                                        // TC-CODE
	        setCrnschRecord.setField("YD_GP"          , getParamRecord.getFieldString("YD_GP"));            // 야드구분
	        setCrnschRecord.setField("YD_STK_COL_GP"  , szTemp);                                            // 야드적치열구분
	        setCrnschRecord.setField("YD_STK_BED_NO"  , getParamRecord.getFieldString("YD_STK_BED_NO"));    // 야드적치BED번호
	        setCrnschRecord.setField("YD_INFO_SYNC_CD", getParamRecord.getFieldString("YD_INFO_SYNC_CD"));	// 야드정보동기화코드

	        // 델리게이트 호출
	        ydDelegate.sendMsg(setCrnschRecord);
	    }catch(DAOException e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
	    }catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
	    } // end of try~catch
	} // end of procY7StrLocSpecReq

    /**
     * 오퍼레이션명 : 2후판정정야드 저장위치제원요구 파라미터 체크
     *
     * @param  ● msgRecord, outRecord
     * @return ● nRtnVal
     * @throws ● DAOException
     */
	public int paramY7YDL001Check(JDTORecord msgRecord, JDTORecord outRecord) throws DAOException {
		// 파라미터 체크 결과 레코드 생성
		JDTORecord setRecord = JDTORecordFactory.getInstance().create();

		// 변수
        String szMethodName  = "paramY7YDL001Check";
    	String szMsg         = "";

        int nRtnVal          = 1;

        try {
        	// 레코드 값 체크
			setRecord.setField("YD_INFO_SYNC_CD", ydDaoUtils.paraRecChkNull(msgRecord, "YD_INFO_SYNC_CD"));
			setRecord.setField("YD_GP"          , ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP"));
			setRecord.setField("YD_BAY_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP"));
			setRecord.setField("YD_EQP_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP"));
			setRecord.setField("YD_STK_COL_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO"));
			setRecord.setField("YD_STK_BED_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO"));

			// 레퍼런스 레코드인자에 설정
			outRecord.setRecord(setRecord);

			//======================================================================================================
			// LOG 출력 - 그냥 테스트용으로 출력 레코드값체크하는 코드는 제외하고 나중에 삭제
			szMsg = "[1] 야드동기화코드 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_INFO_SYNC_CD");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[2] 야드구분 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_GP");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[3] 야드 동구분 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_BAY_GP");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[4] 야드 설비구분 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_EQP_GP");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[5] 야드 적치열번호 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_COL_NO");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[6] 야드 적치BED번호 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_BED_NO");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			//======================================================================================================

        }catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        } // end of try~catch

		return nRtnVal;
	} // end of ParamY7YDL001Check


	/**
	 * 오퍼레이션명 : 2후판정정야드 저장품제원요구 (Y7YDL002)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public void procY7StockSpecReq(JDTORecord msgRecord) throws DAOException {
		// 파라미터 NULL 체크 후 레코드 데이터
		JDTORecord getParamRecord  	= JDTORecordFactory.getInstance().create();

		// 델리게이트 호출을 위한 편집 레코드 데이터
		JDTORecord setCrnschRecord 	= JDTORecordFactory.getInstance().create();

		JPlateYdDelegate ydDelegate	= new JPlateYdDelegate();

		String szMethodName        	= "procY7StockSpecReq";
		String szMsg               	= "";
		String szOperationName     	= "2후판정정야드L2 저장품제원요구";
		String szTemp              	= "";
		String szRcvTcCode         	= ydUtils.getTcCode(msgRecord);

		int nRtnVal                	= 0;

		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "\n=========Y7YDL002========\n", JPlateYdConst.DEBUG);
		szMsg = "[" + szOperationName + "] 수신데이타 :: " + msgRecord.toString();
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		if (szRcvTcCode == null) {
			szMsg = SZ_SESSION_NAME + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return;
		}

		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		}

		try {
			//=============================================================
			// Log 테이블 등록
			//=============================================================
			szMsg = "[2후판정정] 저장품제원요구 수신 >>>> ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			// 파라미터 Check
			nRtnVal = this.paramY7YDL002Check(msgRecord, getParamRecord, 0);
			if (nRtnVal == -1) {
				szMsg = "파라미터 Check중 Error : " + Integer.toString(nRtnVal);
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return;
			}

			// 적치열구분 생성
			szTemp  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP")
					+ ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP")
					+ ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP")
					+ ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO");

			// 델리게이트 호출을 위한 레코드 편집
			setCrnschRecord.setField("JMS_TC_CD"      , "YDY7L002");                                        // TC-CODE
			setCrnschRecord.setField("YD_GP"          , getParamRecord.getFieldString("YD_GP"));            // 야드구분
			setCrnschRecord.setField("YD_STK_COL_GP"  , szTemp);                                            // 야드적치열구분
			setCrnschRecord.setField("YD_STK_BED_NO"  , getParamRecord.getFieldString("YD_STK_BED_NO"));    // 야드적치BED번호
			setCrnschRecord.setField("YD_INFO_SYNC_CD", getParamRecord.getFieldString("YD_INFO_SYNC_CD"));	// 야드정보동기화코드
			setCrnschRecord.setField("STL_NO"         , getParamRecord.getFieldString("STL_NO"));	        // 재료번호

			// 델리게이트 호출
			ydDelegate.sendMsg(setCrnschRecord);
		}catch(DAOException e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        } // end of try~catch
	} // end of procY7StockSpecReq


	/**
	 * 오퍼레이션명 : 2후판정정야드 저장품제원요구 파라미터 체크
	 *
	 * @param  ● msgRecord, outRecord, intGp
	 * @return ● nRtnVal
	 * @throws ● DAOException
	 */
	public int paramY7YDL002Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws DAOException {
		// 파라미터 체크 결과 레코드 생성
		JDTORecord setRecord = JDTORecordFactory.getInstance().create();

		// 변수
		String szMethodName  = "paramY7YDL002Check";
		String szMsg         = "";

		int nRtnVal          = 1;

		try {
			// 레코드 값 체크
			setRecord.setField("YD_INFO_SYNC_CD", ydDaoUtils.paraRecChkNull(msgRecord, "YD_INFO_SYNC_CD"));
			setRecord.setField("YD_GP"          , ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP"));
			setRecord.setField("YD_BAY_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP"));
			setRecord.setField("YD_EQP_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP"));
			setRecord.setField("YD_STK_COL_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO"));
			setRecord.setField("YD_STK_BED_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO"));
			setRecord.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"));

			// 레퍼런스 레코드인자에 설정
			outRecord.setRecord(setRecord);

			//======================================================================================================
			// LOG 출력  - 그냥 테스트용으로 출력 나중에 삭제
			szMsg = "[1] 정보동기화코드  : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_INFO_SYNC_CD");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[2] 야드구분  : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_GP");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[3] 야드동구분  : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_BAY_GP");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[4] 야드설비구분  : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_EQP_GP");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[5] 야드적치열번호  : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_COL_NO");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[6] 야드적치Bed번호  : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_BED_NO");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			//======================================================================================================

		}catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} // end of try~catch

		return nRtnVal;
	} // end of ParamY7YDL002Check

	/**
	 * 오퍼레이션명 : 2후판정정야드 설비운전모드전환 (Y7YDL003)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public String procY7EqpDrvModeChg(JDTORecord msgRecord) throws DAOException {

		// 레코드 선언
		JDTORecord recPara         	= null;
        JDTORecord setCrnschRecord 	= null;

		// DAO 및 UTIL 객체 생성
		JPlateYdDelegate ydDelegate	= new JPlateYdDelegate();
		EJBConnector ejbConn       	= null;

		// 변수 선언
    	String 	szMethodName        = "procY7EqpDrvModeChg";
		String 	szMsg               = "";
//		String 	szOperationName     = "2후판정정야드 L2 설비운전모드전환";
		String 	szYdEqpId		   	= "";
		String 	szYdEqpWrkMode   	= "";
		String	szModifier			= "";
		int 	nRet                = 0;

		String	szRcvTcCode 		= ydUtils.getTcCode(msgRecord);

		if (szRcvTcCode == null) {
			szMsg = SZ_SESSION_NAME + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szMsg;
		}

		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		}

		try {
			//=============================================================
			// Log 테이블 등록
			//=============================================================
			szMsg = "[2후판정정야드] 설비운전모드전환 수신 >>>> " + msgRecord.toString();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		//	ydUtils.putLogMsg("A", "", szMsg, "", "", "", "I", "A", "I", szRcvTcCode, SZ_SESSION_NAME, szMethodName);

	        setCrnschRecord = JDTORecordFactory.getInstance().create();

	        // 설비테이블에 야드설비작업Mode 업데이트
	        szYdEqpId      	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	        szYdEqpWrkMode 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_MODE");
	        szModifier		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");
	        if ("".equals(szModifier)) {
		        szModifier	= ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER", szRcvTcCode);
	        }

	        setCrnschRecord.setField("YD_EQP_ID", 		szYdEqpId);         // 설비ID
	        setCrnschRecord.setField("YD_EQP_WRK_MODE", szYdEqpWrkMode);   // 1: On-Line, 2: Off-Line
	        setCrnschRecord.setField("MODIFIER", 		szRcvTcCode);

	        nRet = this.updEqpDrvMode(setCrnschRecord);

	        if (nRet == -1) {
                szMsg = "설비테이블 업데이트 중  Error : (YD_EQP_ID : " + szYdEqpId + "), (YD_EQP_WRK_MODE : " + szYdEqpWrkMode + "), Ret : " + Integer.toString(nRet);
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
                return szMsg;
	        }

        	//------------------------------------------------------------------------------------------------
	        // YD_EQP_WRK_MODE (1: On-Line, 2: Off-Line)에 따른 업무 정의
	        // 1: On-Line  - 크레인 리스케줄 호출[복구], 2후판정정야드 크레인 작업지시 호출[YD_EQP_ID, YD_WRK_PROG_STAT(W)]
	        // 2: Off-Line - 크레인 리스케줄 호출[고장]
            //------------------------------------------------------------------------------------------------
	        if ("1".equals(szYdEqpWrkMode)) {			// 1: On-Line

	        	recPara = JDTORecordFactory.getInstance().create();

	        	//크레인 리스케줄 호출[복구]
	        	recPara.setField("MSG_ID", 		"YDYDJ751");
	        	recPara.setField("YD_EQP_ID", 	szYdEqpId);

	        	//------------------------------------------------------------------------------------------------
	        	// EJB 호출로 변경
	        	//------------------------------------------------------------------------------------------------
	        	// ydDelegate.sendMsg(recPara);
	        	ejbConn = new EJBConnector("default", this);
	        	ejbConn.trx("JPlateYdCrnReSchSeEJB", "procY7CrnReSch", recPara);
	        	//------------------------------------------------------------------------------------------------

	        	szMsg = "[2후판정정야드 설비운전모드전환]2후판정정야드 크레인 리스케줄 호출[복구]";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        } else if ("2".equals(szYdEqpWrkMode)) {		// 2: Off-Line

	        	//크레인 리스케줄 호출[고장]
	        	recPara = JDTORecordFactory.getInstance().create();
	        	recPara.setField("MSG_ID",   	"YDYDJ751");
	        	recPara.setField("YD_EQP_ID", 	szYdEqpId);

	        	//------------------------------------------------------------------------------------------------
	        	// EJB 호출로 변경
	        	//------------------------------------------------------------------------------------------------
	        	// ydDelegate.sendMsg(recPara);
	        	ejbConn = new EJBConnector("default", this);
	        	ejbConn.trx("JPlateYdCrnReSchSeEJB", "procY7CrnReSch", recPara);
	        	//------------------------------------------------------------------------------------------------

	        	szMsg = "[2후판정정야드 설비운전모드전환]2후판정정야드 크레인 리스케줄 호출[고장]";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	        } else {	// 정의되지 않은 값
	        	szMsg = "[2후판정정야드 설비운전모드전환]야드설비작업Mode[" + szYdEqpWrkMode + "]가 정의되지 않은 값입니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szMsg;
	        }

	        //------------------------------------------------------------------
	        // 	업무 : 크레인작업실적응답 전문 전송(YDY7L005)
	        //------------------------------------------------------------------
	        recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("MSG_ID", 			"YDY7L005");
			recPara.setField("YD_EQP_ID", 		szYdEqpId);
			recPara.setField("YD_L2_WR_GP", 	"M");				//U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경
			recPara.setField("YD_L3_HD_RS_CD", 	"0000");			//야드L3처리결과코드
			ydDelegate.sendMsg(recPara);

			return JPlateYdConst.RETN_CD_SUCCESS;

		} catch(DAOException e) {
            //szMsg = "JDTOError : " + e.getLocalizedMessage();
            //ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
            //return;
			throw new DAOException(getClass().getName() + e.getMessage(),e);
	    } catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
	    }
	} // end of procY7EqpDrvModeChg()

    /**
     * 오퍼레이션명 : 2후판정정 야드 설비테이블 야드설비작업Mode 업데이트
     *
     * @param  ● msgRecord
     * @return ● nRtnVal
     * @throws ● DAOException
     */
    public int updEqpDrvMode(JDTORecord msgRecord) throws DAOException {
    	// DAO 생성
    	JPlateYdEqpDAO ydEqpDao = new JPlateYdEqpDAO();

    	// 변수 선언
    	String szMethodName = "updEqpDrvMode";
    	String szMsg        = "";
    	int nRtnVal         = 0;

    	try {
    		// YD_EQP_WRK_MODE UPDATE
    		nRtnVal = ydEqpDao.updYdEqpWrkMode(msgRecord);

			switch(nRtnVal) {
				case 0 :
				    szMsg = "No Data Found!!!, ErrorCode:" + Integer.toString(nRtnVal);
				    ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				    return nRtnVal = -1;
				case -1	:
				    szMsg = "Dup_val_on_index!!!, ErrorCode:" + Integer.toString(nRtnVal);
				    ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				    return nRtnVal = -1;
				case -2	:
				    szMsg = "Parameter Error!!!, ErrorCode:" + Integer.toString(nRtnVal);
				    ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				    return nRtnVal = -1;
				case -3	:
				    szMsg = "Execution Failed!!!, ErrorCode:" + Integer.toString(nRtnVal);
			        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			        return nRtnVal = -1;
			}
		}catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }
    	return nRtnVal;
    }

	/**
	 * 오퍼레이션명 : 2후판정정야드 설비고장복구실적 수신처리 (Y7YDL004)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return String
	 * @throws DAOException
	 */
	public String procY7EqpTrblRcvrWr(JDTORecord msgRecord) throws DAOException {

		// 레코드 선언
		JDTORecord recPara         = null;
        JDTORecord setCrnschRecord = null;

		// DAO 및 UTIL 객체 생성
		JPlateYdDelegate ydDelegate = new JPlateYdDelegate();
		EJBConnector ejbConn        = null;

		// 변수선언
    	String 	szMethodName		= "procY7EqpTrblRcvrWr";
		String 	szMsg               = "";
//		String 	szOperationName     = "2후판정정야드 L2 설비고장복구실적";
		String 	szYdEqpId           = "";
		String 	szYdEqpStat         = "";
		String 	szYdEqpPauseCode    = "";
		String 	szYdEqpTrblRcvrDt	= "";
		String 	szYdEqpStatUpd      = "";
		String	szModifier			= "";
		int 	nRet                = 0;

		String	szRcvTcCode 		= ydUtils.getTcCode(msgRecord);
		if (szRcvTcCode == null) {
			szMsg = SZ_SESSION_NAME + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szMsg;
		}

		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		}

		try {
			//=============================================================
			// Log 테이블 등록
			//=============================================================
			szMsg = "[2후판정정] 설비고장복구실적 수신 >>>> " + msgRecord.toString();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		//	ydUtils.putLogMsg("A", "", szMsg, "", "", "", "I", "A", "I", szRcvTcCode, SZ_SESSION_NAME, szMethodName);

	        setCrnschRecord = JDTORecordFactory.getInstance().create();

	        szYdEqpId  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	        szModifier = ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");
	        if ("".equals(szModifier)) {
		        szModifier = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER", szRcvTcCode);
	        }

	        if ("".equals(szYdEqpId)) {
	            szMsg = "설비ID가 존재하지 않습니다.";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	        	return szMsg;
	        }

	        szYdEqpStat = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_STAT");
	        if ("".equals(szYdEqpStat)) {
	            szMsg = "설비상태가  존재하지 않습니다.";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	        	return szMsg;
	        }

	        szYdEqpPauseCode = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
	        if ("".equals(szYdEqpPauseCode)) {
	            szMsg = "휴지코드가  존재하지 않습니다.";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	        	return szMsg;
	        }

	        szYdEqpTrblRcvrDt = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_TRBL_RCVR_DT");
	        if ("".equals(szYdEqpTrblRcvrDt)) {
	            szMsg = "야드설비고장복구일시가  존재하지 않습니다.";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	        	return szMsg;
	        }

			//============================================================================
			// 변환...
			//============================================================================
			if (JPlateYdConst.YD_EQP_STAT_BREAK.equals(szYdEqpStat)) {
				// 고장
				szYdEqpStatUpd = JPlateYdConst.YD_EQP_STAT_BREAK;
				if ("0000".equals(szYdEqpPauseCode) || "".equals(szYdEqpPauseCode)) {
					szYdEqpPauseCode = "B000";
				} else {
					szYdEqpPauseCode = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
				}

			} else if ("R".equals(szYdEqpStat) || JPlateYdConst.YD_EQP_STAT_NORM.equals(szYdEqpStat)) {
				// 복구
				szYdEqpStatUpd = JPlateYdConst.YD_EQP_STAT_NORM;
				if ("0000".equals(szYdEqpPauseCode) || "".equals(szYdEqpPauseCode)) {
					szYdEqpPauseCode = "R000";
				} else {
					szYdEqpPauseCode = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
				}
			}

			//============================================================================
	        // 휴지테이블 업데이트
			// 고장이나 복구일 때 남김
			//============================================================================
			if (JPlateYdConst.YD_EQP_STAT_BREAK.equals(szYdEqpStatUpd) || JPlateYdConst.YD_EQP_STAT_NORM.equals(szYdEqpStatUpd)) {
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMethodName + "::    [2] 설비 휴지테이블에 야드 설비휴지코드 업데이트 처리", JPlateYdConst.DEBUG);
		        this.procEqpPause(szRcvTcCode, szYdEqpId, szYdEqpStatUpd, szYdEqpPauseCode, szYdEqpTrblRcvrDt);
			}

			//============================================================================
			// 크레인 설비인 경우 설비 작업 상태 및 스케줄 변경
			//============================================================================
			String lzRtnMsg = null;

			if (JPlateYdConst.YD_EQP_GP_CRANE.equals(szYdEqpId.substring(2,4))) {

				// 고장 UPDATE 시
				if (JPlateYdConst.YD_EQP_STAT_BREAK.equals(szYdEqpStatUpd)) {
					// 해당 설비 스케줄이 권상지시(YD_EQP_STAT_UP_WO) 일경우 IDLE 상태로 변경 YD_EQP_STAT_IDLE

					lzRtnMsg = this.updCrnWrkProgStatUpWoToIdle(szYdEqpId);

					if (JPlateYdConst.RETN_CD_SUCCESS.equals(lzRtnMsg)) {
						szMsg = "스케줄 변경 성공 하였습니다.";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					} else if (JPlateYdConst.RETN_CD_FAILURE.equals(lzRtnMsg)) {
						szMsg = "스케줄 변경 실패 하였습니다.";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					}
				} else {
					// 정상 UPDATE 시
					szYdEqpStatUpd = JPlateYdConst.YD_EQP_STAT_IDLE;
				}
			}
			//============================================================================
	        // 설비테이블에 야드설비상태 업데이트
			//============================================================================
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMethodName + "::    [3] 설비 테이블에 업데이트 처리", JPlateYdConst.DEBUG);
	        setCrnschRecord.setField("YD_EQP_ID"  , szYdEqpId);       	// 설비ID
	        setCrnschRecord.setField("YD_EQP_STAT", szYdEqpStatUpd); 	// "B": 고장, "W": 대기
	        setCrnschRecord.setField("MODIFIER"	  , szRcvTcCode);
	        nRet = this.updYdEqpStat(setCrnschRecord);
	        if (nRet == -1) {
                szMsg = "설비테이블 업데이트 중  Error : (YD_EQP_ID : " + szYdEqpId + "),(YD_EQP_STAT : " + szYdEqpStat + "), Ret : " + Integer.toString(nRet);
                return szMsg;
	        }

			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * YD_EQP_STAT (R: 복구, B: 고장)에 따른 업무 정의
	         * R: 복구 - 크레인 리스케줄링 호출[복구], 2후판정정 크레인 작업지시 호출[YD_EQP_ID, YD_WRK_PROG_STAT(W)]
	         * B: 고장 - 크레인 리스케줄링 호출[고장]
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	        if ("R".equals(szYdEqpStat) || JPlateYdConst.YD_EQP_STAT_NORM.equals(szYdEqpStat)) {			// R: 복구
	        	recPara = JDTORecordFactory.getInstance().create();

	        	//크레인 리스케줄링 호출[복구]
	        	recPara.setField("MSG_ID",      "YDYDJ751");
	        	recPara.setField("YD_EQP_ID", 	szYdEqpId);

	        	ejbConn = new EJBConnector("default", this);
			 	ejbConn.trx("JPlateYdCrnReSchSeEJB", "procY7CrnReSch", recPara);

	        	szMsg = "[2후판정정 설비고장복구실적]크레인 리스케줄링 호출[복구]";
	        	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	szMsg = "[2후판정정 설비고장복구실적]2후판정정 크레인 작업지시 호출";
	        	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        } else if (JPlateYdConst.YD_EQP_STAT_BREAK.equals(szYdEqpStat)) {		//B: 고장
	        	//크레인 리스케줄링 호출[고장]
	        	recPara = JDTORecordFactory.getInstance().create();
	        	recPara.setField("MSG_ID",   	"YDYDJ751");
	        	recPara.setField("YD_EQP_ID", 	szYdEqpId);

	        	ejbConn = new EJBConnector("default", this);
			 	ejbConn.trx("JPlateYdCrnReSchSeEJB", "procY7CrnReSch", recPara);

	        	szMsg = "[2후판정정 설비고장복구실적]크레인 리스케줄링 호출[고장]";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        } else {											// 정의되지 않은 값
	        	szMsg = "[2후판정정 설비고장복구실적]야드설비상태[" + szYdEqpStat + "]가 정의되지 않은 값입니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szMsg;
	        }

	        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 업무 : 크레인작업실적응답 전문 전송(YDY7L005)
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	        recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("MSG_ID"        , "YDY7L005");
			recPara.setField("YD_EQP_ID"     , szYdEqpId);
			recPara.setField("YD_L2_WR_GP"   , "R");			//U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경
			recPara.setField("YD_L3_HD_RS_CD", "0000");			//야드L3처리결과코드
			ydDelegate.sendMsg(recPara);
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

			return JPlateYdConst.RETN_CD_SUCCESS;

	    } catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
	    }
	}

    /**
     * 오퍼레이션명 : 2후판정정 야드 설비테이블 설비상태 업데이트
     *
     * @param  ● msgRecord, intGp
     * @return ● nRtnVal
     * @throws ● DAOException
     */
    public int updYdEqpStat(JDTORecord msgRecord) throws DAOException {
    	// DAO 생성
    	JPlateYdEqpDAO ydEqpDao = new JPlateYdEqpDAO();

    	// 변수 선언
    	String szMethodName = "updYdEqpStat";
    	String szMsg        = "";
    	int nRet            = 0;

    	try {
    		nRet = ydEqpDao.updYdEqpStat(msgRecord);	// updYdEqpStat

			switch(nRet) {
				case 0 :
				    szMsg = "No Data Found!!!, ErrorCode:" + Integer.toString(nRet);
				    ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				    return nRet = -1;
				case -1	:
				    szMsg = "Dup_val_on_index!!!, ErrorCode:" + Integer.toString(nRet);
				    ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				    return nRet = -1;
				case -2	:
				    szMsg = "Parameter Error!!!, ErrorCode:" + Integer.toString(nRet);
				    ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				    return nRet = -1;
				case -3	:
				    szMsg = "Execution Failed!!!, ErrorCode:" + Integer.toString(nRet);
			        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			        return nRet = -1;
			}
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
    	return nRet;
    }

    /**
     * 오퍼레이션명 : 설비고장복구실적 휴지테이블 처리
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● TC코드, 설비ID, 수신된 전문의 고장(B)/복구(N), 휴지코드, 발생일시
     * @return ● void
     * @throws ● DAOException
     */
	public void procEqpPause(String strRcvTcCode, String szYdEqpId, String szYdEqpStatUpd, String szYdEqpPauseCode, String szYdEqpTrblRcvrDt) throws DAOException {

		// DAO 및 UTIL 객체 생성
        JPlateYdEqpDAO 		ydEqpDao      = new JPlateYdEqpDAO();
        JPlateYdEqpPauseDAO ydEqpPauseDao = new JPlateYdEqpPauseDAO();

        // 레코드 선언
		JDTORecord recPara          = null;
        JDTORecord setCrnschRecord  = null;
        JDTORecord recGetVal        = null;
		JDTORecordSet rsResult      = null;

		// 변수 선언
    	String szMethodName         = "procEqpPause";
		String szMsg                = "";
		String szYdEqpPauseOccrSeq  = "";
		String szYdEqpPauseOccDt	= "";
		String szYdEqpPauseRcvrCnts = "";
		int nRet                    = 0;


		try {
			// 데이터 항목 점검
			if ("".equals(strRcvTcCode)) {
	            szMsg = "TC CODE가 존재하지 않습니다.";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	        	return;
			}

			if ("".equals(szYdEqpId)) {
	            szMsg = "설비ID가 존재하지 않습니다.";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	        	return;
			}

			if ("".equals(szYdEqpStatUpd)) {
	            szMsg = "설비상태가 존재하지 않습니다.";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	        	return;
			}

			if ("".equals(szYdEqpPauseCode)) {
	            szMsg = "설비휴지코드가 존재하지 않습니다.";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	        	return;
			}

			if ("".equals(szYdEqpTrblRcvrDt)) {
	            szMsg = "휴지일시가 존재하지 않습니다.";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	        	return;
			}

			recPara 		= JDTORecordFactory.getInstance().create();
			setCrnschRecord = JDTORecordFactory.getInstance().create();
			rsResult 		= JDTORecordFactory.getInstance().createRecordSet("");

			// 설비테이블과 휴지테이블에서 해당 설비ID를 가지고 MAX차수의 상태값을 가져온다.
	        // 읽어온 현재 설비의 상태값이 고장일 경우 수신받은 상태가 복구이면 휴지테이블에 MAX차수에 UPDATE 를 하고
	        // 수신받은 상태가 고장이면 PASS
	        // 읽어온 현재 설비의 상태값이 복구일 경우 수신받은 상태가 고장이면 휴지테이블에 MAX차수+1에 INSERT 를 하고
	        // 수신받은 상태가 복구이면 PASS

	        //=========================================================================================
	        // 설비ID로 현재 설비의 상태와 휴지테이블에서 MAX차수의 값을 추출
	        //=========================================================================================
	        recPara.setField("YD_EQP_ID", szYdEqpId);
	        nRet = ydEqpDao.getEqpStatOfMax(recPara, rsResult);
	        if (nRet < 0) {
	            szMsg = "설비 휴지 테이블 조회 오류 [" + Integer.toString(nRet) + "] YD_EQP_ID(" + szYdEqpId + ")";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	        	return;
	        } else if (nRet == 0) {
    			// 1차
    			szYdEqpPauseOccrSeq = ydUtils.increaseStrToInt("0", 1, 18);

    			// 레코드 편성
    			setCrnschRecord = JDTORecordFactory.getInstance().create();
                setCrnschRecord.setField("REGISTER"             , strRcvTcCode);            // 등록자
                setCrnschRecord.setField("MODIFIER"             , strRcvTcCode);            // 수정자
                setCrnschRecord.setField("DEL_YN"               , "N");                     // 삭제유무
                setCrnschRecord.setField("YD_EQP_ID"            , szYdEqpId);              	// 설비ID
    	        setCrnschRecord.setField("YD_EQP_PAUSE_OCCR_SEQ", szYdEqpPauseOccrSeq);		// 1차
    	        setCrnschRecord.setField("YD_EQP_PAUSE_CODE"    , szYdEqpPauseCode);      	// 설비휴지코드
    	        setCrnschRecord.setField("YD_EQP_PAUSE_OCC_DT"  , szYdEqpTrblRcvrDt);    	// 야드설비휴지발생일시

    	        // 설비휴지테이블 ISNERT
    	        nRet = ydEqpPauseDao.insYdEqpPause(setCrnschRecord);
    			if (nRet < 0) {
    	        	szMsg = "설비 휴지테이블 INSERT 중  Error : " + Integer.toString(nRet) + " : YD_EQP_ID(" + szYdEqpId + ") YD_EQP_PAUSE_OCCR_SEQ(" + szYdEqpPauseOccrSeq + ") YD_EQP_PAUSE_CODE(" + szYdEqpPauseCode + ") YD_EQP_TRBL_RCVR_DT(" + szYdEqpTrblRcvrDt + ")";
                    ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
                    return;
    	        }

	        	szMsg = "설비 휴지테이블 INSERT 성공 : YD_EQP_ID(" + szYdEqpId + ") YD_EQP_PAUSE_OCCR_SEQ(" + szYdEqpPauseOccrSeq + ") YD_EQP_PAUSE_CODE(" + szYdEqpPauseCode + ") YD_EQP_TRBL_RCVR_DT(" + szYdEqpTrblRcvrDt + ")";
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        } else {
		        rsResult.first();
		        recGetVal = rsResult.getRecord();

		        // DB조회를 한 현재 설비의 상태와 차수를 가져온다
		        szYdEqpPauseOccrSeq = ydDaoUtils.paraRecChkNull(recGetVal, "YD_EQP_PAUSE_OCCR_SEQ");
		        szYdEqpPauseOccDt   = ydDaoUtils.paraRecChkNull(recGetVal, "YD_EQP_PAUSE_OCC_DT");

		        if (JPlateYdConst.YD_EQP_STAT_BREAK.equals(szYdEqpStatUpd)) {
	        		// 수신전문의 설비상태가 고장일 경우 해당설비ID의 차수+1에 업데이트
	    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMethodName + "::    [3] 설비 휴지테이블에 INSERT 처리", JPlateYdConst.DEBUG);

	    			// 해당차수를 1증가 처리
	    			szYdEqpPauseOccrSeq = ydUtils.increaseStrToInt(szYdEqpPauseOccrSeq, 1, 18);

	    			// 레코드 편성
	    			setCrnschRecord = JDTORecordFactory.getInstance().create();
	                setCrnschRecord.setField("REGISTER"             , strRcvTcCode);            // 등록자
	                setCrnschRecord.setField("MODIFIER"             , strRcvTcCode);            // 수정자
	                setCrnschRecord.setField("DEL_YN"               , "N");                     // 삭제유무
	                setCrnschRecord.setField("YD_EQP_ID"            , szYdEqpId);              	// 설비ID
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_OCCR_SEQ", szYdEqpPauseOccrSeq);  	// 차수 + 1
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_CODE"    , szYdEqpPauseCode);      	// 설비휴지코드
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_OCC_DT"  , szYdEqpTrblRcvrDt);    	// 야드설비휴지발생일시

	    	        // 설비휴지테이블 ISNERT
	    	        nRet = ydEqpPauseDao.insYdEqpPause(setCrnschRecord);

		        	szMsg = "설비 휴지테이블 INSERT 성공 : YD_EQP_ID(" + szYdEqpId + ") YD_EQP_PAUSE_OCCR_SEQ(" + szYdEqpPauseOccrSeq + ") YD_EQP_PAUSE_CODE(" + szYdEqpPauseCode + ") YD_EQP_TRBL_RCVR_DT(" + szYdEqpTrblRcvrDt + ")";
	                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	} else {
	        		// 수신전문의 설비상태가 복구일 경우 해당설비ID의 해당차수에 업데이트
	    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMethodName + "::    [3] 설비 휴지테이블에 UPDATE 처리", JPlateYdConst.DEBUG);

	    			setCrnschRecord = JDTORecordFactory.getInstance().create();
	                setCrnschRecord.setField("YD_EQP_ID"              , szYdEqpId);             // 설비ID
	                setCrnschRecord.setField("MODIFIER"               , strRcvTcCode);          // 수정자
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_OCCR_SEQ"  , szYdEqpPauseOccrSeq);  	// 해당 차수
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_CODE"      , szYdEqpPauseCode);      // 설비휴지코드
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_END_DT"    , szYdEqpTrblRcvrDt);    	// 야드설비휴지종료일시
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_OCC_DT"    , szYdEqpPauseOccDt);    	// 야드설비휴지발생일시(차를 계산하기 위함)
	    	        setCrnschRecord.setField("YD_EQP_PAUSE_RCVR_CNTS" , szYdEqpPauseRcvrCnts); 	// 야드설비휴지복구내용 (일단 항목이 없음)*************************************************

	    	        // 설비휴지테이블 UPDATE
	        		nRet = ydEqpPauseDao.updYdEqpPauseRepair(setCrnschRecord);

	        		szMsg = "설비 휴지테이블 UPDATE 성공 : YD_EQP_ID(" + szYdEqpId + ") YD_EQP_PAUSE_OCCR_SEQ(" + szYdEqpPauseOccrSeq + ") YD_EQP_PAUSE_CODE(" + szYdEqpPauseCode + ") YD_EQP_TRBL_RCVR_DT(" + szYdEqpTrblRcvrDt + ")";
	                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	        	}
	        }
        } catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }

        return;
	}

    /**
     * 오퍼레이션명 : 해당 설비 스케줄 작업 상태를 IDLE 상태로 변경
     *
     * @param  ● Stirng pYdEqpId
     * @return ● nRtnVal
     * @throws ● DAOException
     */
	public String  updCrnWrkProgStatUpWoToIdle(String pYdEqpId) throws DAOException {
		// 레코드선언
		JDTORecord setRecord 	= null;
		JDTORecord recRecord 	= null;

		// 변수 선언
        String szMethodName  	= "updCrnWrkProgStatUpWoToIdle";
    	String szMsg         	= "";
    	String szOperationName 	= "설비 스케줄 작업 상태 IDLE로 변경";

    	//JDTORecordSet
    	JDTORecordSet rsResult  = null;

    	//DAO
    	JPlateYdCrnSchDAO ydCrnSchDao = new JPlateYdCrnSchDAO();

    	int intRtnVal = 0;

        try {
        	szMsg = "["+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//------------------------------------------------------------------------------------------------
			// 설비 ID 로 크레인 작업지시가 내려간 스케줄 조회
			//------------------------------------------------------------------------------------------------
			recRecord = JDTORecordFactory.getInstance().create();
			recRecord.setField("YD_EQP_ID"          , pYdEqpId);
			recRecord.setField("YD_WRK_PROG_STAT"   , JPlateYdConst.YD_EQP_STAT_UP_WO);

			rsResult = JDTORecordFactory.getInstance().createRecordSet("rsResult");

			intRtnVal = ydCrnSchDao.getYdWrkProgStat(recRecord, rsResult);

			if (intRtnVal < 0) {
				szMsg = "["+szOperationName+"] 스케줄 조회 ERROR";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szMsg;
			} else if (intRtnVal == 0) {
				szMsg = "["+szOperationName+"] 변경할 데이터가 없습니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				return JPlateYdConst.RETN_CD_SUCCESS;
			}
			szMsg = "["+szOperationName+"] 크레인 스케줄 조회 성공!";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			//------------------------------------------------------------------------------------------------

			//------------------------------------------------------------------------------------------------
			// 크레인 스케줄 정보 IDLE 상태로 변경
			//------------------------------------------------------------------------------------------------
			recRecord = JDTORecordFactory.getInstance().create();
			setRecord = JDTORecordFactory.getInstance().create();
			rsResult.first();
			recRecord = rsResult.getRecord();

			setRecord.setField("YD_WRK_PROG_STAT", 	JPlateYdConst.YD_EQP_STAT_IDLE);
			setRecord.setField("MODIFIER", 			"updCrnWrk");
			setRecord.setField("YD_CRN_SCH_ID", 	recRecord.getField("YD_CRN_SCH_ID"));

			intRtnVal = ydCrnSchDao.updYdCrnWrkProgStat(setRecord);

			if (intRtnVal < 0) {
				szMsg = "["+szOperationName+"] UPDATE ERROR";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szMsg;

			} else if (intRtnVal == 0) {
				szMsg = "["+szOperationName+"] UPDATE 할 스케줄이 없습니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return JPlateYdConst.RETN_CD_SUCCESS;
			}

			szMsg = "["+szOperationName+"] 스케줄 ID : " + recRecord.getFieldString("YD_CRN_SCH_ID") + "정보를 변경하였습니다.";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			//------------------------------------------------------------------------------------------------

			szMsg = "["+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }

		return JPlateYdConst.RETN_CD_SUCCESS;
	}

    /**
     * 오퍼레이션명 : 2후판정정 크레인 작업지시요구 (Y7YDL007)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
     */
    public String procY7CrnWrkOrdReq(JDTORecord msgRecord)throws DAOException  {

    	JPlateYdDelegate   	ydDelegate   = new JPlateYdDelegate();

    	JDTORecord recCrnSch 		= JDTORecordFactory.getInstance().create();
    	JDTORecord recInPara 		= null;

        JDTORecordSet rsCrnSch 		= JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecordSet rsResult 		= JDTORecordFactory.getInstance().createRecordSet("temp");
		JDTORecordSet rsCrnInfo 	= null;

        int 	intRtnVal 			= 0;

        String 	szMsg              	= "";
        String 	szMethodName       	= "procY7CrnWrkOrdReq";
        String 	szOperationName		= "2후판정정 크레인작업지시 요구";

        String 	szEqpId             = "";
        String 	szYdWrkProgStat		= "";
        String	szModifier			= "";

        //스케쥴코드
        String 	szYdSchCd			= "";

        boolean blnRtnVal			= true;

        String 	szRtnMsg			= "";
        String 	szRcvTcCode			= ydUtils.getTcCode(msgRecord);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.12 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "F");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
                
        if (szRcvTcCode==null || "".equals(szRcvTcCode)) {
        	szMsg = "[ERROR] " + SZ_SESSION_NAME + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
        	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
        	return szMsg;
        }

        try {
        	//=============================================================
        	// Log 테이블 등록
        	//=============================================================
        	szMsg = "[2후판정정] 크레인작업지시 수신";
       // 	ydUtils.putLogMsg("A", "", szMsg, "", "", "", "I", "A", "I", szRcvTcCode, SZ_SESSION_NAME, szMethodName);
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	szEqpId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
        	szModifier 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");
        	if ("".equals(szModifier)) {
            	szModifier 	= ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER", szRcvTcCode);
        	}

        	//------------------------------------------------------------------------------------------------------
        	// 야드설비상태 Check		수신받은  야드설비Id로 설비Table를 조회하여 야드설비상태를 Check하고 고장이면 return
        	//------------------------------------------------------------------------------------------------------
        	szMsg = "[" + szOperationName + "] 크레인설비[" + szEqpId + "] 상태 체크 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			rsCrnInfo = JDTORecordFactory.getInstance().createRecordSet("");
//---------------------------------------------------------------------------------------------
// 2024.12.12 Argument에 logId 추가 eqpStatCheck 신규 작성
//---------------------------------------------------------------------------------------------
//        	szRtnMsg = this.eqpStatCheck(szEqpId, rsCrnInfo);
        	szRtnMsg = this.eqpStatCheck(szEqpId, rsCrnInfo, logId);
//---------------------------------------------------------------------------------------------

        	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
        		szRtnMsg = "설비 상태 체크 시 오류발생 - 메세지 : " + szRtnMsg;
        		szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

        		this.sendErrCrnWrkOrdReq(msgRecord, szRtnMsg);
        		return szRtnMsg;
        	}

        	szMsg = "[" + szOperationName + "] 크레인설비[" + szEqpId + "] 상태 체크 완료 - 메세지 : " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	//------------------------------------------------------------------------------------------------------
			// 스케줄 기준 체크 -- 현재 2후판정정야드는 스케줄코드를 SET하지 않고 작업지시를 요청한다.
        	//------------------------------------------------------------------------------------------------------
			szYdSchCd 	 = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if (!"".equals(szYdSchCd)) {
				blnRtnVal = JPlateYdCommonUtils.chkGetSchRule(szYdSchCd, rsResult);
				if (!blnRtnVal) {
					szRtnMsg = "스케줄 기준 체크 조회시 오류 발생!";
	        		szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

	        		this.sendErrCrnWrkOrdReq(msgRecord, szRtnMsg);
					return szRtnMsg;
				}
				// 레코드 추출
				rsResult.first();
				JDTORecord recPara = rsResult.getRecord();
				// 스케줄 금지 유무
				String szYdSchProhExn = ydDaoUtils.paraRecChkNull(recPara,"YD_SCH_PROH_EXN");

				// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
				if ("Y".equals(szYdSchProhExn)) {
					szRtnMsg = "스케쥴코드(" + szYdSchCd + ")가 기동 금지 입니다";
	        		szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

	        		this.sendErrCrnWrkOrdReq(msgRecord, szRtnMsg);
					return szRtnMsg;
				}
			}

        	// 야드 작업 진행상태를 check한다.
			rsCrnSch  = JDTORecordFactory.getInstance().createRecordSet("temp");
    		intRtnVal = this.chkWrkProgStat(msgRecord, rsCrnSch);

    		if (intRtnVal == 0) {

        		szRtnMsg = "명령선택된 작업지시가 없음";
        		szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

        		this.sendErrCrnWrkOrdReq(msgRecord, "");
				return szRtnMsg;

    		} else if (intRtnVal == -1) {

				szRtnMsg = "스케줄 기준 체크 조회시 오류 .. 오류코드 : " + Integer.toString(intRtnVal);
        		szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

        		this.sendErrCrnWrkOrdReq(msgRecord, szRtnMsg);
				return szRtnMsg;
    		}

        	recCrnSch = JDTORecordFactory.getInstance().create();
        	recCrnSch.setRecord(rsCrnSch.getRecord(0));

        	szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WRK_PROG_STAT");

        	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 * 업무기준 : 2후판정정야드와 통합야드가 같은 로직을 사용하므로 2후판정정야드와 관련된 L2로만 전송 필요
			 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        	szMsg = "[크레인 작업지시] 작업지시 전문 전송 START >>>>";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	recInPara = JDTORecordFactory.getInstance().create();
    		//작업지시 전문 전송 data setup
			recInPara.setField("MSG_ID", 			"YDY7L004"												);
        	recInPara.setField("YD_CRN_SCH_ID", 	ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID")	);
       		recInPara.setField("YD_WRK_PROG_STAT", 	szYdWrkProgStat											);
        	recInPara.setField("YD_WORD_DT",    	JPlateYdUtils.getCurDate("yyyyMMddHHmmss")				);
        	recInPara.setField("MODIFIER", 			"YDSYSTEM"												);
        	recInPara.setField("MSG_GP", 			"U"														);
        	recInPara.setField("YD_SCH_CD", 		ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD")		);
        	recInPara.setField("YD_GP", 			ydDaoUtils.paraRecChkNull(recCrnSch, "YD_GP")			);
        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.12 recInPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
        	recInPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
			        	
			szRtnMsg = ydDelegate.sendMsg(recInPara);

			szMsg = "[크레인 작업지시]크레인 작업지시 메세지 전송 완료 >>>> " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		} catch(Exception e) {
			return e.getMessage();
		}

		szMsg = "크레인 작업지시(" + szMethodName + ") 완료";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;

	} //end of procY7CrnWrkOrdReq()

    /**
     * 오퍼레이션명 : 크레인 작업지시요구 에러시 응답메시지 전송
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
     */
    public String sendErrCrnWrkOrdReq(JDTORecord msgRecord, String pYdL3Msg)throws DAOException  {

    	JPlateYdDelegate   	ydDelegate   = new JPlateYdDelegate();

    	JDTORecord recInPara 		= null;

        String 	szMsg              	= "";
        String	szRtnMsg			= "";
        String 	szMethodName       	= "sendErrCrnWrkOrdReq";
        String 	szOperationName		= "크레인작업지시 요구 응답메시지 전송";

        String 	szEqpId             = "";
        String 	szYdWrkProgStat		= "";
        String 	szYdSchCd			= "";
        String	szYdCrnSchId		= "";
        String	szYdL3Msg			= "";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.12 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "F");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
                      
        try {

    		szMsg = "[" + szOperationName + "] 응답메시지 전송 .... START";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        szEqpId         = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"		);
	        szYdWrkProgStat	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT"	);
	        szYdSchCd		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD"			);
	        szYdCrnSchId	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID"		);

	        if (pYdL3Msg != null && !"".equals(pYdL3Msg)) {
	        	szYdL3Msg = pYdL3Msg;
	        }

            /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 2후판정정 야드L2 크레인작업실적응답 전송  - YDY7L005
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        	recInPara = JDTORecordFactory.getInstance().create();
        	recInPara.setField("MSG_ID", 				"YDY7L005"								);
        	recInPara.setField("YD_EQP_ID", 			szEqpId									);		// 야드설비ID
        	recInPara.setField("YD_WRK_PROG_STAT", 		szYdWrkProgStat							);		// 야드작업진행상태
        	recInPara.setField("YD_SCH_CD", 			szYdSchCd								);		// 야드스케줄코드
        	recInPara.setField("YD_CRN_SCH_ID", 		szYdCrnSchId							);		// 야드크레인스케줄ID
        	recInPara.setField("YD_L2_WR_GP", 			JPlateYdConst.CRN_WRK_RE_WO_DMD		);		// 야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
        	if ("".equals(szYdL3Msg)) {
            	recInPara.setField("YD_L3_HD_RS_CD",	JPlateYdConst.CRN_WRK_RE_CD_NO_WRK	);		// 야드L3처리결과코드 - 9999 : 크레인작업지시가 없을 경우
        	} else {
            	recInPara.setField("YD_L3_HD_RS_CD",	JPlateYdConst.CRN_WRK_RE_CD_ERROR		);		// 야드L3처리결과코드 - 8888 : 오류
        		recInPara.setField("YD_L3_MSG", 		szYdL3Msg								);
        	}
        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.12 recInPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
        	recInPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
			        	
        	szRtnMsg = ydDelegate.sendMsg(recInPara);

    		szMsg = "[" + szOperationName + "] 응답메시지 전송 .... END >>>> " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		} catch(Exception e) {
			return e.getMessage();
		}

		szMsg = "크레인 작업지시(" + szMethodName + ") 완료";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;

	} // end of sendErrCrnWrkOrdReq()

	/**
	 * 오퍼레이션명 : 설비상태 체크
	 *
	 * @param   String szEqpId 설비ID
	 * @return boolean true(설비사용가능), false(설비사용불가)
	 * @throws DAOException
	 */
	public String eqpStatCheck(String szEqpId,JDTORecordSet rsResult)throws DAOException  {
		//메세지
		String szMsg           	= null;
		//메소드명
		String szMethodName    	= "eqpStatCheck";
		String szOperationName  = "설비조회";

		String szYdEqpStat   	= null;				//설비상태
		String szYdEqpWrkMode	= null;				//야드설비작업Mode
		String szYdEqpId		= null;

		//레코드 선언
		JDTORecord recPara     	= null;

		int intRtnVal			= -100;

		try {
			//레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			//설비ID를 작업크레인으로 설정
			recPara.setField("YD_EQP_ID", szEqpId);

			//설비 체크 및 데이터 조회
//			blnRtnVal = this.chkGetEqp(szEqpId, rsResult);
//			if (!blnRtnVal) { return blnRtnVal; }

			JPlateYdEqpDAO ydEqpDao = new JPlateYdEqpDAO();

			intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult);

			if (intRtnVal == 0) {
				szMsg = "["+szOperationName+"] 존재하지 않습니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return JPlateYdConst.RETN_CD_NOTEXIST;
			} else if (intRtnVal == -2) {
				szMsg = "["+szOperationName+"] 파라미터가 존재하지 않습니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return JPlateYdConst.RETN_CD_NO_PARAM;
			} else if (intRtnVal < 0) {
				szMsg = "["+szOperationName+"] 오류발생";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szMsg;
			} else {
				szMsg = "["+szOperationName+"] 존재합니다. - 대상재["+intRtnVal+"]";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			//설비상태
			szYdEqpStat 	= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
			//야드설비작업Mode
			szYdEqpWrkMode 	= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_MODE");
			//야드설비작업Mode
			szYdEqpId 		= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_ID");

			szMsg = "설비ID(" + szYdEqpId + ")의 (" + szYdEqpStat + ") 입니다.";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

			//크레인의 상태가 'T'이면 false 리턴.
			if (JPlateYdConst.YD_EQP_STAT_BREAK.equals(szYdEqpStat)) {

				szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYdEqpStat + ") 입니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				//blnRtnVal = false;

				return JPlateYdConst.YD_EQP_STAT_BREAK;
			} else if (JPlateYdConst.YD_EQP_WRK_MODE_OFF_LINE.equals(szYdEqpWrkMode)) {

				szMsg = "설비ID(" + szEqpId + ")의 상태가  OFF LINE(" + szYdEqpWrkMode + ")상태 입니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				//blnRtnVal = false;

				return JPlateYdConst.YD_EQP_WRK_MODE_OFF_LINE;
			} else {

				//blnRtnVal = true;
				return JPlateYdConst.RETN_CD_SUCCESS;
			}
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		//return blnRtnVal;

	} //end of eqpStatCheck

    /**
     * 오퍼레이션명 : 크레인 작업지시(작업지시를 재요구 하는 경우 사용한다.)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws DAOException
     */
    public int chkWrkProgStat(JDTORecord msgRecord, JDTORecordSet rsCrnSch)throws DAOException  {

    	JPlateYdCrnSchDAO ydCrnSchDao 	= new JPlateYdCrnSchDAO();
    	JPlateYdEqpDAO    ydEqpDao 		= new JPlateYdEqpDAO();

        JDTORecordSet 	rsResult    = null;
        JDTORecord 		recOutTemp 	= null;

    	int intRtnVal 				= 0;

        String szMsg              	= "";
        String szMethodName       	= "chkWrkProgStat";
        String szYdEqpStat          = "";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.12 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "F");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

        try {

        	//설비Table를 조회하여 설비상태가 '1 : 권상지시'또는 '3 : 권하지시'이라면 크레인 스케줄 Table에서 야드작업진행 상태가 1,2,3인 것만 찾는다. 1건만 나와야 정상이고 1건이 아니라면 Error처리
        	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
        	intRtnVal = ydEqpDao.getYdEqp(msgRecord, rsResult);
			if (intRtnVal <= 0) {
				if (intRtnVal == 0) {
					szMsg = "chkWrkProgStat getYdEqp : data not found";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING, logId);
				} else if (intRtnVal == -2) {
					szMsg = "chkWrkProgStat getYdEqp : parameter error";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				}
				return intRtnVal = -1;
			}

			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult.getRecord(0));
			szYdEqpStat = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_STAT");

			//설비상태가 1또는 3인경우
			if ("1".equals(szYdEqpStat) || "2".equals(szYdEqpStat) || "3".equals(szYdEqpStat)) {
				//설비id로 크레인 스케줄을 조회한다. 현재 작업진행상태가 1또는 3인 경우를 조회...
	        	rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
	        	
		    	intRtnVal = ydCrnSchDao.getWrkProgStatYdF(msgRecord, rsResult);
				if (intRtnVal <= 0) {
					//에러처리
					szMsg = "현재 작업진행상태가 1또는 3인 크레인 스케줄을 조회 중 Error";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					//return intRtnVal = -1;
					return intRtnVal;
				}
			} else {
				intRtnVal = 0;
			}

        	rsCrnSch.addAll(rsResult);

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "chkWrkProgStat("+szMethodName+") 처리 완료 " + Integer.toString(intRtnVal);
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		return intRtnVal;
	} // end of chkWrkProgStat()

	/**
	 * 오퍼레이션명 : 크레인 작업지시(작업이 없을 경우 요구한다.)-- 사용하는데 없음
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public int chkWrkProgStatW(JDTORecord msgRecord, JDTORecordSet rsCrnSch)throws DAOException  {

		JPlateYdEqpDAO    	ydEqpDao 	= new JPlateYdEqpDAO();
		JPlateYdCrnSchDAO 	ydCrnSchDao	= new JPlateYdCrnSchDAO();
        JPlateYdDelegate  	ydDelegate 	= new JPlateYdDelegate();

		JDTORecordSet 		rsResult 	= null;
		JDTORecord 			recIntTemp  = null;
		JDTORecord 			para  		= null;

	    int 	intRtnVal 		= 0;
	    String 	szMsg           = "";
	    String 	szMethodName    = "chkWrkProgStatW";

	    String 	szYdEqpId     	= "";
	    String	szModifier		= "";

	    try {
	    	szYdEqpId  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	    	szModifier = ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");
	    	if ("".equals(szModifier)) {
		    	szModifier = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER", "YARDSYSTEM");
	    	}

	    	//크레인 스케줄 전체에서 우선순위가 가장 빠른 작업을 조회한다. 크레인 스케줄을 조회한다.
        	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
        	para = JDTORecordFactory.getInstance().create();
        	para.setField("YD_EQP_ID", szYdEqpId);

	    	intRtnVal = ydCrnSchDao.getYdCrnSchEqpIdPrior(para, rsResult);

        	if (intRtnVal == 0) {
        		//더이상 크레인 스케줄에 작업이 없을 경우 설비Table에 설비상태를 'W' 명령선택대기(idle)로 변경한다.
        		recIntTemp = JDTORecordFactory.getInstance().create();
        		recIntTemp.setField("YD_EQP_ID", 	szYdEqpId);
        		recIntTemp.setField("YD_EQP_STAT", 	"W");
        		recIntTemp.setField("MODIFIER", 	szModifier);

        		intRtnVal = ydEqpDao.updYdEqpStat(recIntTemp);
        		if (intRtnVal <= 0) {
        			if (intRtnVal == 0) {
	    				szMsg = " chkWrkProgStatW updYdEqp : data not found";
	    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING);
	    			} else if (intRtnVal == -1) {
	    				szMsg = " chkWrkProgStatW updYdEqp : duplicate data,";
	    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    			} else if (intRtnVal == -2) {
	    				szMsg = " chkWrkProgStatW updYdEqp : parameter error";
	    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    			} else if (intRtnVal == -3) {
	    				szMsg = " chkWrkProgStatW updYdEqp : execution failed";
	    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    			}
	    		}

    			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    	         * 2후판정정 야드L2 크레인작업실적응답 전송  - YDY7L005
    	         * 업무기준 Desc : 2후판정정 야드L2에서 크레인 작업지시 요구 시 크레인 작업이 더 이상 존재하지 않을 경우 크레인작업실적응답 전송
    	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    			recIntTemp = JDTORecordFactory.getInstance().create();
    			recIntTemp.setField("MSG_ID"        , "YDY7L005");
    			recIntTemp.setField("YD_EQP_ID"     , szYdEqpId);							//야드설비ID
    			recIntTemp.setField("YD_L2_WR_GP"   , JPlateYdConst.CRN_WRK_RE_WO_DMD);		//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
    			recIntTemp.setField("YD_L3_HD_RS_CD", JPlateYdConst.CRN_WRK_RE_CD_NO_WRK);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
    			ydDelegate.sendMsg(recIntTemp);
    			szMsg = "[2후판정정 크레인 작업지시 요구]크레인 작업이 더 이상 존재하지 않을 경우 2후판정정 야드L2 크레인작업실적응답[YDY7L005] 전송 완료";
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
    	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

    			szMsg = "더이상의 크레인스케줄이 조회되지 않습니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING);

        		return intRtnVal = 0;

        	} else if (intRtnVal < 0) {
        		szMsg = "크레인 스케줄 조회중 Error";
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			return intRtnVal = -1;
        	}

        	//-------------------------------------------------------------------------------------------------------------
        	//	다음스케줄의 작업상태를 체크하여 W이면 설비의 작업상태를 1로 변경, 아니면 변경하지 않음
        	//-------------------------------------------------------------------------------------------------------------

        	//-------------------------------------------------------------------------------------------------------------
        	//	먼저 복사해서 아래부분에서 사라지는 문제를 해결
        	//-------------------------------------------------------------------------------------------------------------
        	rsCrnSch.addAll(rsResult);
        	//-------------------------------------------------------------------------------------------------------------

        	rsResult.first();
        	recIntTemp = rsResult.getRecord();

        	String szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recIntTemp, "YD_WRK_PROG_STAT");

        	//-------------------------------------------------------------------------------------------------------------
        	// 다음 스케줄을 찾았을 경우
        	//-------------------------------------------------------------------------------------------------------------
        	if ("".equals(szYdWrkProgStat) || JPlateYdConst.YD_EQP_STAT_IDLE.equals(szYdWrkProgStat)) {
	    		recIntTemp = JDTORecordFactory.getInstance().create();
	    		recIntTemp.setField("YD_EQP_ID", 	szYdEqpId);
	    		recIntTemp.setField("YD_EQP_STAT", 	"1");
	    		recIntTemp.setField("MODIFIER",   	szModifier);
	    		intRtnVal = ydEqpDao.updYdEqpStat(recIntTemp);
	    		if (intRtnVal <= 0) {
	    			if (intRtnVal == 0) {
	    				szMsg = " chkWrkProgStatW updYdEqp : data not found";
	    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING);
	    			} else if (intRtnVal == -1) {
	    				szMsg = " chkWrkProgStatW updYdEqp : duplicate data,";
	    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    			} else if (intRtnVal == -2) {
	    				szMsg = " chkWrkProgStatW updYdEqp : parameter error";
	    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    			} else if (intRtnVal == -3) {
	    				szMsg = " chkWrkProgStatW updYdEqp : execution failed";
	    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    			}
	    			return intRtnVal = -1;
	    		}
        	} else {
        		szMsg = "크레인의 스케줄의 야드작업진행상태["+szYdWrkProgStat+"]가 W가 아니므로 크레인설비의 상태를 변경하지 않음";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
        	}

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "chkWrkProgStatW("+szMethodName+") 처리완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		return intRtnVal = 1;

	} //end of chkWrkProgStatW()

    /**
     * 오퍼레이션명 : 2후판정정 크레인권상실적등록 (Y7YDL008)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
     */
    public String procY7CrnUpWr(JDTORecord msgRecord)throws DAOException  {

		EJBConnector ejbConn 	= null;
    	JDTORecord recInPara 	= null;

		String 	szMethodName    = "procY7CrnUpWr";
		String 	szRtnMsg        = "";
		String 	szLogMsg        = "";
		String 	szSendMsg       = "";
		String 	szEjbJndiName 	= "JPlateYdCrnLoadWrkSeEJB";
		String 	szEjbMethod 	= "procY7CrnUpWr";
        String	szEqpId         = "";
        String	szYdWrkProgStat	= "";
        String	szYdSchCd		= "";
        String	szYdCrnSchId	= "";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.13 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "F");  	// JDTORecord 에서 logid get(1 : JDTORecord.getResultCode(), Field명 - 2 : UNIQUE_ID, 3 : LOG_ID, 4 : 새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
      		                     
		try {

			szLogMsg = "[2후판정정]크레인 권상실적처리 호출[" + szEjbJndiName + "." + szEjbMethod + "] - 메소드 콜 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			//2후판정정 권상실적처리
			ejbConn  = new EJBConnector("default", this);
			szRtnMsg = (String)ejbConn.trx("JPlateYdCrnLoadWrkSeEJB", "procY7CrnUpWr", msgRecord);

			szLogMsg = "[2후판정정]크레인 권상실적처리 호출[" + szEjbJndiName + "." + szEjbMethod + "] - 메소드 콜 종료>>>>" + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg) ||
				JPlateYdConst.RETN_CRN_NO_SCH.equals(szRtnMsg) ||
				JPlateYdConst.RETN_CRN_NO_WRK.equals(szRtnMsg) ) {
				szLogMsg = "[2후판정정]크레인 권상실적처리 호출[" + szEjbJndiName + "." + szEjbMethod + "] - 메소드 콜 완료";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			} else {
				szLogMsg = "[2후판정정]크레인 권상실적처리 호출[" + szEjbJndiName + "." + szEjbMethod + "] - 메소드 콜 오류발생";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);

		        szEqpId         = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
		        szYdWrkProgStat	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");
		        szYdSchCd		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
		        szYdCrnSchId	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");

	            /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 2후판정정 야드L2 크레인작업실적응답 전송  - YDY7L005
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("MSG_ID", 				"YDY7L005");
	        	recInPara.setField("YD_EQP_ID", 			szEqpId);									//야드설비ID
	        	recInPara.setField("YD_WRK_PROG_STAT", 		szYdWrkProgStat);							//야드작업진행상태
	        	recInPara.setField("YD_SCH_CD", 			szYdSchCd);									//야드스케줄코드
	        	recInPara.setField("YD_CRN_SCH_ID", 		szYdCrnSchId);								//야드크레인스케줄ID
	        	recInPara.setField("YD_L2_WR_GP", 			JPlateYdConst.CRN_WRK_RE_LD_WR);			//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
            	recInPara.setField("YD_L3_HD_RS_CD",		JPlateYdConst.CRN_WRK_RE_CD_ERROR);			//야드L3처리결과코드 - 8888 : 오류
        		recInPara.setField("YD_L3_MSG", 			szRtnMsg);
	        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.13 recInPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
        		recInPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
							        	
        		szSendMsg = ydDelegate.sendMsg(recInPara);

        		szLogMsg = "[2후판정정] 크레인작업실적응답 전문 전송 END >>>> " + szSendMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			    m_ctx.setRollbackOnly();
				return szRtnMsg;
			}

		} catch (Exception e) {
			szLogMsg = "[2후판정정]크레인 권상실적처리 호출[" + szEjbJndiName + "." + szEjbMethod + "] - 메소드 오류발생";
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
		}
		return JPlateYdConst.RETN_CD_SUCCESS;
    } // end of procY7CrnUpWr()

	/**
     * 오퍼레이션명 : 2후판정정야드 권하실적처리 (Y7YDL009)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
	 */
	public String procY7CrnDownWr(JDTORecord msgRecord) throws DAOException {

		EJBConnector ejbConn 	= null;
		String 	szMethodName    = "procY7CrnDownWr";
        String 	szOperationName	= "2후판정정야드 권하실적처리(Y7YDL009)";
		String 	szRtnMsg        = "";
		String 	szLogMsg        = "";
		String 	szEjbJndiName 	= "JPlateYdCrnUnloadWrkSeEJB";
		String 	szEjbMethod 	= "procY7CrnDnWr";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.13 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "F");  	// JDTORecord 에서 logid get(1 : JDTORecord.getResultCode(), Field명 - 2 : UNIQUE_ID, 3 : LOG_ID, 4 : 새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		      		                     
		try {

			szLogMsg = "[2후판정정]크레인 권하실적처리 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			//2후판정정 권하실적처리
			ejbConn  = new EJBConnector("default", this);
			szRtnMsg = (String)ejbConn.trx("JPlateYdCrnUnloadWrkSeEJB", "procY7CrnDnWr", msgRecord);

			if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg) ||
				JPlateYdConst.RETN_CRN_NO_SCH.equals(szRtnMsg) ||
				JPlateYdConst.RETN_CRN_NO_WRK.equals(szRtnMsg) ) {
				szLogMsg = "[" + szOperationName + "] 메소드 콜 완료";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			} else {
				szLogMsg = "[" + szOperationName + "] 권하실적처리 호출 오류발생 >>>> " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);

				m_ctx.setRollbackOnly();
				return szRtnMsg;
			}

		} catch (Exception e) {
			szRtnMsg = "권하실적처리 호출 Exception 오류발생";
			szLogMsg = "[" + szOperationName + "]" + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);

			m_ctx.setRollbackOnly();
			return szRtnMsg;
		}
		return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of procY7CrnDownWr

	/**
     * 오퍼레이션명 : 2후판정정야드 강제권상요구 (Y7YDL010)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
	 */
	public String procY7OffCrnUpWr(JDTORecord msgRecord) throws DAOException {

		// 변수 선언
		EJBConnector ejbConn 		= null;
    	String 	szMethodName        = "procY7OffCrnUpWr";
    	String	szOperationName		= "2후판정정야드 강제권상요구";
		String 	sRtnCd				= "";
		String 	szRtnMsg         	= "";
		String	szYdL3Msg			= "";
		String 	szMsg         		= "";
		String 	szYdEqpId    		= "";
		JDTORecord outRecord		= JDTORecordFactory.getInstance().create();
		JDTORecord recPara			= null;

		try {
			//=============================================================
			// Log 테이블 등록
			//=============================================================
			szMsg = "["+ szMethodName + "] 강제권상요구 수신 .... START >>>> " + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szYdEqpId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"); 		//야드설비ID

			//2후판정정 강제권상요구
			ejbConn   	= new EJBConnector("default", "JPlateYdCrnLoadWrkSeEJB", this);
			outRecord 	= (JDTORecord)ejbConn.trx("procY7OffCrnUpWr", new Class[] { JDTORecord.class }, new Object[] { msgRecord });
			sRtnCd    	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), JPlateYdConst.RETN_CD_FAILURE);
			szYdL3Msg	= StringHelper.evl(outRecord.getFieldString("YD_L3_MSG"), "");

			szMsg = "[" + szOperationName + "] 강제권상 실적처리 호출결과 >>>> RTN_CD::" + sRtnCd + ", RTN_MSG::" + szYdL3Msg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(sRtnCd)) {			//성공이 아닐때

		        //------------------------------------------------------------------
		        // 	업무 : 크레인작업실적응답 전문 전송(YDY7L005)
		        //------------------------------------------------------------------
            	szMsg = "[" + szOperationName + "] 크레인작업실적응답 전문 전송 START ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		        recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("MSG_ID", 			"YDY7L005");
				recPara.setField("YD_EQP_ID", 		szYdEqpId);
			//	recPara.setField("YD_L2_WR_GP", 	"U");								//U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
				recPara.setField("YD_L2_WR_GP", 	"E");								//U:권상실적,P:권하실적,E:강제권상,R:고장,M:모드변경,J:지시요구,F:강제권하
				recPara.setField("YD_L3_HD_RS_CD", 	"8888");							//야드L3처리결과코드
				recPara.setField("YD_L3_MSG", 		ydUtils.substr(szYdL3Msg, 0, 40));	//야드L3처리결과메시지
				szRtnMsg = ydDelegate.sendMsg(recPara);

            	szMsg = "[" + szOperationName + "] 크레인작업실적응답 전문 전송 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				szMsg = "[" + szOperationName + "] 강제권상요구 오류 (" + szYdL3Msg + ")";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return szYdL3Msg;
			}

			szMsg = "["+ szMethodName + "] 강제권상요구 수신 .... END";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch (Exception e) {

			// Exception 발생시에도 응답메시지 전송
			try {
		        //------------------------------------------------------------------
		        // 	업무 : 크레인작업실적응답 전문 전송(YDY7L005)
		        //------------------------------------------------------------------
	        	szMsg = "[" + szOperationName + "] 크레인작업실적응답 (Exception) 전문 전송 START ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		        recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("MSG_ID", 			"YDY7L005");
				recPara.setField("YD_EQP_ID", 		szYdEqpId);
			//	recPara.setField("YD_L2_WR_GP", 	"U");								//U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
				recPara.setField("YD_L2_WR_GP", 	"E");								//U:권상실적,P:권하실적,E:강제권상,R:고장,M:모드변경,J:지시요구,F:강제권하
				recPara.setField("YD_L3_HD_RS_CD", 	"8888");							//야드L3처리결과코드
				szRtnMsg = ydDelegate.sendMsg(recPara);

	        	szMsg = "[" + szOperationName + "] 크레인작업실적응답 (Exception) 전문 전송 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			} catch (Exception ee) {
				szMsg = "["+ szMethodName + "] 크레인작업실적응답 (Exception) 전문 전송시 Exception 발생";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
		}
		return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of procY7OffCrnUpWr

	/**
     * 오퍼레이션명 : 2후판정정야드 강제권하요구 (Y7YDL011)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
	 */
	public String procY7OffCrnDnWr(JDTORecord msgRecord) throws DAOException {

		EJBConnector ejbConn 		= null;
		String 	szMethodName    	= "procY7OffCrnDnWr";
    	String	szOperationName		= "2후판정정야드 강제권하요구";
        String 	sRtnCd				= JPlateYdConst.RETN_CD_FAILURE;
        String	szYdL3Msg			= "";
		String 	szRtnMsg        	= "";
		String 	szMsg         		= "";
    	String	szYdEqpId			= "";
		JDTORecord outRecord		= JDTORecordFactory.getInstance().create();
		JDTORecord recPara			= null;

		try {

			szMsg = "["+ szOperationName + "] 강제권하요구 수신 .... START";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szYdEqpId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"); 		//야드설비ID

			//2후판정정 권하실적처리
			ejbConn 	= new EJBConnector("default", "JPlateYdCrnUnloadWrkSeEJB", this);
			outRecord  	= (JDTORecord)ejbConn.trx("procY7OffCrnDnWr", new Class[] { JDTORecord.class }, new Object[] { msgRecord });
			sRtnCd    	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), JPlateYdConst.RETN_CD_FAILURE);
			szYdL3Msg	= StringHelper.evl(outRecord.getFieldString("YD_L3_MSG"), "");

			szMsg = "[" + szOperationName + "] 강제권하 실적처리 호출결과 >>>> RTN_CD::" + sRtnCd + ", RTN_MSG::" + szYdL3Msg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(sRtnCd)) {			//실패

		        //------------------------------------------------------------------
		        // 	업무 : 크레인작업실적응답 전문 전송(YDY7L005)
		        //------------------------------------------------------------------
            	szMsg = "[" + szOperationName + "] 크레인작업실적응답 전문 전송 START ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		        recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("MSG_ID", 			"YDY7L005");
				recPara.setField("YD_EQP_ID", 		szYdEqpId);
				recPara.setField("YD_L2_WR_GP", 	"F");								//U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
				recPara.setField("YD_L3_HD_RS_CD", 	"8888");							//야드L3처리결과코드
				recPara.setField("YD_L3_MSG", 		ydUtils.substr(szYdL3Msg, 0, 40));	//야드L3처리결과메시지
				szRtnMsg = ydDelegate.sendMsg(recPara);

            	szMsg = "[" + szOperationName + "] 크레인작업실적응답 전문 전송 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				m_ctx.setRollbackOnly();

				szMsg = "강제권하요구 오류 (" + szYdL3Msg + ")";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			//	throw new DAOException(szRtnMsg);
				return szMsg;
			}

			szMsg = "["+ szMethodName + "] 강제권하요구 수신 .... END";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch (Exception e) {

			// Exception 발생시에도 응답메시지 전송
			try {
		        //------------------------------------------------------------------
		        // 	업무 : 크레인작업실적응답 전문 전송(YDY7L005)
		        //------------------------------------------------------------------
	        	szMsg = "[" + szOperationName + "] 크레인작업실적응답 (Exception) 전문 전송 START ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		        recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("MSG_ID", 			"YDY7L005");
				recPara.setField("YD_EQP_ID", 		szYdEqpId);
				recPara.setField("YD_L2_WR_GP", 	"F");								//U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
				recPara.setField("YD_L3_HD_RS_CD", 	"8888");							//야드L3처리결과코드
				szRtnMsg = ydDelegate.sendMsg(recPara);

	        	szMsg = "[" + szOperationName + "] 크레인작업실적응답 (Exception) 전문 전송 END >>>> " + e.getMessage();
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			} catch (Exception ee) {
				szMsg = "["+ szMethodName + "] 크레인작업실적응답 (Exception) 전문 전송시 Exception 발생";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				m_ctx.setRollbackOnly();
				return ee.getMessage();
			}

			m_ctx.setRollbackOnly();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
		//	throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
			return e.getMessage();
		}
		return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of procY7OffCrnDnWr

	/**
     * 오퍼레이션명 : 2후판정정야드 명령선택처리 (Y7YDL012)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
	 */
	public String procY7CrnOrderSel(JDTORecord msgRecord) throws DAOException {

		JPlateYdCrnSchDAO 	ydCrnSchDao	= new JPlateYdCrnSchDAO();

		// 레코드 선언
		JDTORecord recPara         	= null;
		JDTORecordSet rsCrnSch		= null;

		// DAO 및 UTIL 객체 생성
		JPlateYdDelegate ydDelegate	= new JPlateYdDelegate();

		// 변수 선언
    	String 	szMethodName        = "procY7CrnOrderSel";
        String	szOperationName		= "2후판정정야드 명령선택처리";
		String 	szMsg               = "";
		String	szRtnMsg			= "";
		String 	szRcvTcCode      	= ydUtils.getTcCode(msgRecord);
		String 	szYdL3HdRsCd 		= "";
		String 	szYdL3Msg 			= "";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.13 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "F");  	// JDTORecord 에서 logid get(1 : JDTORecord.getResultCode(), Field명 - 2 : UNIQUE_ID, 3 : LOG_ID, 4 : 새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		                     
		int		intRtnVal			= 0;

		try {
			//=============================================================
			// Log 테이블 등록
			//=============================================================
			szMsg = "[2후판정정야드] 크레인명령선택 수신 >>>> " + msgRecord.toString();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        // 설비테이블에 야드설비작업Mode 업데이트
	        String 	szYdWrkProgStat = "";
	        String 	szYdEqpId       = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"		);		// 야드설비ID
	        String 	szYdReqProgStat = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CMD_PKUP_GP"	);		// 야드명령선택구분 (S:선택, C:취소)
	        String 	szYdCrnSchId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID"	);		// 야드크레인스케쥴ID
	        String	szYdSchCd		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD"		);		// 야드크레인스케쥴코드
	        String 	szModifier		= ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER"		);		// 수정자
	        if ("".equals(szModifier)) {
	        	szModifier = szRcvTcCode;
	        }

	        // W:명령선택대기, 1:권상지시, 3:권하지시
	        if ("S".equals(szYdReqProgStat)) {
	        	szYdWrkProgStat = JPlateYdConst.YD_EQP_STAT_UP_WO;		// "1" - 권상지시
	        } else {
	        	szYdWrkProgStat = JPlateYdConst.YD_EQP_STAT_IDLE;			// "W" - 명령선택 대기 상태
	        }

        	//------------------------------------------------------------------------------------------------------
        	// 상단에 다른 크레인 작업지시 존재여부 체크
        	//------------------------------------------------------------------------------------------------------
        	szYdCrnSchId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");

	        if ("S".equals(szYdReqProgStat)) {
	        	szMsg = "[" + szOperationName + "] 해당 저장위치 상단에 다른 작업지시 존재여부 체크 .. 시작 >>>> " + szYdCrnSchId;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				rsCrnSch  = JDTORecordFactory.getInstance().createRecordSet("temp");
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID",		szYdCrnSchId);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.13 recPara에 logId 추가 
				recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

	        	intRtnVal = ydCrnSchDao.getUpLyrCrnSchExistYdF(recPara, rsCrnSch);
	        	
	        } else {
	        	intRtnVal = 0;
	        	szMsg = "[" + szOperationName + "] 명령선택 취소일때는 SKIP";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        }

        	if (intRtnVal > 0) {
        		szRtnMsg = "해당 저장위치 상단에 다른 작업지시 존재";
        		szMsg    = "[" + szOperationName + "] " + szRtnMsg + "하여 오류발생 >>>> " + intRtnVal;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

        		szYdL3HdRsCd = "8888";
				szYdL3Msg	 = szRtnMsg;

        	} else {

	        	szMsg = "[" + szOperationName + "] 해당 저장위치 상단에 다른 작업지시 존재여부 체크 .. 완료";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				// 명령선택처리
				// 1. 야드명령선택구분 (S:선택, C:취소)에 따라 크레인스케줄(TB_YD_CRNSCH)에 야드작업진행상태(YD_WRK_PROG_STAT)를 변경처리
				// 선택이면 W->1로 처리 , 취소이면 1->W 로 처리

//---------------------------------------------------------------------------------------------
// 2024.12.13 updCrnWrkProgStat call argument에 logId 추가  
//				String lzRtnMsg = this.updCrnWrkProgStat(szYdCrnSchId, szYdEqpId, szYdWrkProgStat, szModifier);
				String lzRtnMsg = this.updCrnWrkProgStat(szYdCrnSchId, szYdEqpId, szYdWrkProgStat, szModifier, logId);
//---------------------------------------------------------------------------------------------

				if (JPlateYdConst.RETN_CD_SUCCESS.equals(lzRtnMsg)) {
					szMsg = "[" + szOperationName + "] 크레인명령선택 처리 성공 하였습니다.";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					szYdL3HdRsCd = "0000";
				} else {
					szMsg = "[" + szOperationName + "] 크레인명령선택 처리 실패 하였습니다. >>>> " + lzRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					szYdL3HdRsCd = "8888";
				//	szYdL3Msg	 = "크레인명령선택 처리 실패 하였습니다.";
					szYdL3Msg	 = lzRtnMsg;
				}
        	}

			if ("0000".equals(szYdL3HdRsCd)) {

				if ("S".equals(szYdReqProgStat)) {
			        //------------------------------------------------------------------
			        // 	업무 : 작업지시 전문 전송 (YDY7L004)
			        //------------------------------------------------------------------
			        recPara = JDTORecordFactory.getInstance().create();

					recPara.setField("MSG_ID", 				"YDY7L004"							);
					recPara.setField("YD_CRN_SCH_ID",    	szYdCrnSchId						);
					recPara.setField("YD_WRK_PROG_STAT", 	szYdWrkProgStat						);
					recPara.setField("YD_SCH_CD",        	szYdSchCd							);
					recPara.setField("YD_GP",            	JPlateYdConst.YD_GP_F_PLATE_YARD	);
					recPara.setField("MODIFIER", 			"Y7YDL007"							);
					recPara.setField("MSG_GP", 				"U"									);
		        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.13 recPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
					recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
								        	
					szRtnMsg = ydDelegate.sendMsg(recPara);

	            	szMsg = "[" + szOperationName + "] 현재크레인이 명령선택한 작업지시를 전송 END >>>> " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				} else {

					// 명령선택 취소시에는 updCrnWrkProgStat 메서드에서 취소전문 송신함으로 L2 전송은 SKIP
	            	szMsg = "[" + szOperationName + "] 명령선택 취소 처리 완료 .... L2 전송 SKIP";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				}

			} else {

		        //------------------------------------------------------------------
		        // 	업무 : 크레인작업실적응답 전문 전송(YDY7L005)
		        //------------------------------------------------------------------
            	szMsg = "[" + szOperationName + "] 크레인작업실적응답 전문 전송 START ";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		        recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("MSG_ID", 			"YDY7L005"			);
				recPara.setField("YD_EQP_ID", 		szYdEqpId			);
				recPara.setField("YD_L2_WR_GP", 	"J"					);		// U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
				recPara.setField("YD_L3_HD_RS_CD", 	szYdL3HdRsCd		);		// 야드L3처리결과코드
				recPara.setField("YD_L3_MSG", 		szYdL3Msg			);
	        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.13 recPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
				recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
							        	
				szRtnMsg = ydDelegate.sendMsg(recPara);

            	szMsg = "[" + szOperationName + "] 크레인작업실적응답 전문 전송 END >>>> " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				m_ctx.setRollbackOnly();

				return szYdL3Msg;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
		}
		return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of procY7CrnOrderSel()

    /**
     * 오퍼레이션명 : 해당 크레인 설비 스케줄 작업 상태를  변경 [명령선택]
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● Stirng pYdCrnSchId, String pYdWrkProgStat
     * @return ● nRtnVal
     * @throws ● DAOException
     */
	public String  updCrnWrkProgStat(String pYdCrnSchId, String pYdEqpId, String pYdWrkProgStat, String pModifier, String logId) throws DAOException {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.12.13 updCrnWrkProgStat argument 에 logId 항목 추가 개선
//	public String  updCrnWrkProgStat(String pYdCrnSchId, String pYdEqpId, String pYdWrkProgStat, String pModifier) throws DAOException {
////////////////////////////////////////////////////////////////////////////////////////
		// 레코드선언
		JDTORecord setRecord 	= null;
		JDTORecord recRecord 	= null;
		JDTORecord recPara 		= null;
		JDTORecord recL2Msg		= null;

		// 변수 선언
        String 	szMethodName  	= "updCrnWrkProgStat";
        String	szRtnMsg		= "";
    	String 	szMsg         	= "";
    	String 	szOperationName = "설비 스케줄 작업 상태  변경(" + pYdWrkProgStat + ")";
    	String 	szYdWrkProgStat	= "";
    	String	szYdEqpId		= "";
    	String	szYdUpWoLoc		= "";
    	String	szYdCrnSchId	= "";		// 기존작업지시 크레인스케줄 ID
		
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.13 logId 개선 
    	if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                    // log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

    	//JDTORecordSet
    	JDTORecordSet rsResult  = null;

    	//DAO
    	JPlateYdCrnSchDAO 	ydCrnSchDao = new JPlateYdCrnSchDAO();
    	JPlateYdEqpDAO		ydEqpDAO	= new JPlateYdEqpDAO();

    	int intRtnVal = 0;

        try {
        	szMsg = "[" + szOperationName + "] 메소드 시작 ";
        	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//------------------------------------------------------------------------------------------------
			//  크레인 작업지시  스케줄 조회
			//------------------------------------------------------------------------------------------------
			rsResult  	= JDTORecordFactory.getInstance().createRecordSet("ydTemp");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", 	pYdCrnSchId);
        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.13 recPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
			recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
        	
			intRtnVal = ydCrnSchDao.getYdCrnSchYdF(recPara, rsResult);

			if (intRtnVal < 0) {
				szRtnMsg = "스케줄 조회 ERROR";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szRtnMsg;
				
			} else if (intRtnVal == 0) {
				szMsg = "[" + szOperationName + "] 변경할 데이터가 없습니다.";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
				return JPlateYdConst.RETN_CD_SUCCESS;
				
			}
			szMsg = "[" + szOperationName + "] 크레인 스케줄 조회 성공!";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//------------------------------------------------------------------------------------------------
			// 명령선택 가능여부 체크
			//------------------------------------------------------------------------------------------------
			recRecord = JDTORecordFactory.getInstance().create();
			rsResult.first();
			recRecord = rsResult.getRecord();

			szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recRecord, "YD_WRK_PROG_STAT");
			szYdEqpId		= ydDaoUtils.paraRecChkNull(recRecord, "YD_EQP_ID");

			if (JPlateYdConst.YD_EQP_STAT_UP_CMPL.equals(szYdWrkProgStat) || 			// 권상완료
				JPlateYdConst.YD_EQP_STAT_DN_WO.equals(szYdWrkProgStat)) {			// 권하지시

				szRtnMsg = "권상처리후 크레인명령선택 불가.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
				return szRtnMsg;
        	}

			// 권상위치와 설비ID 체크 (C동 크레인이 다른동의 작업 선택 못하도록 보완)
			szYdUpWoLoc 	= ydDaoUtils.paraRecChkNull(recRecord, "YD_UP_WO_LOC");
			szYdUpWoLoc		= ydUtils.substr(szYdUpWoLoc, 0, 2);
			if (!szYdUpWoLoc.equals(ydUtils.substr(pYdEqpId, 0, 2))) {
				szRtnMsg = "해당 크레인이 크레인명령선택 불가한 FROM위치.";
				szMsg	 = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
				return szRtnMsg;
			}

			// 현재 명령선택중인 작업지시 정보를 체크
			rsResult  	= JDTORecordFactory.getInstance().createRecordSet("ydTemp");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("YD_EQP_ID", 	pYdEqpId);
        	
//-------------------------------------------------------------------------------------------------------------------------
//2024.12.13 recPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
			recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------

			intRtnVal = ydCrnSchDao.getWrkProgStatYdF(recPara, rsResult);
			if (intRtnVal > 0) {

				// 기존의 명령선택 정보 CLEAR
				setRecord = JDTORecordFactory.getInstance().create();
				setRecord.setField("YD_WRK_PROG_STAT", 	JPlateYdConst.YD_EQP_STAT_IDLE);
				setRecord.setField("MODIFIER", 			pModifier);
				setRecord.setField("YD_EQP_ID", 		szYdEqpId);
				intRtnVal = ydCrnSchDao.updProgStatByEqpId(setRecord);

				rsResult.first();

				for (int ii=0; ii<rsResult.size(); ii++) {

					recRecord = JDTORecordFactory.getInstance().create();
					recRecord = rsResult.getRecord();

					szYdCrnSchId    = ydDaoUtils.paraRecChkNull(recRecord, "YD_CRN_SCH_ID");
					szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recRecord, "YD_WRK_PROG_STAT");

					szMsg = "[" + szOperationName + "] >>>> 기존지시 " + szYdCrnSchId + ", 작업 상태  >>>> " + szYdWrkProgStat;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					// 기존의 지시가 명령선택중이면 지시취소정보 전송
					if (JPlateYdConst.YD_EQP_STAT_UP_WO.equals(szYdWrkProgStat)) {						// '1' : 권상지시

				        recL2Msg = JDTORecordFactory.getInstance().create();
				        recL2Msg.setField("MSG_ID", 				"YDY7L004"							);		// 크레인 작업지시
				        recL2Msg.setField("YD_CRN_SCH_ID",    		szYdCrnSchId						);		// 크레인 스케줄 ID
				        recL2Msg.setField("YD_WRK_PROG_STAT",		JPlateYdConst.YD_EQP_STAT_IDLE	);		// 'W' : 명령선택 대기
				        recL2Msg.setField("MSG_GP",           		"U"									);
						szMsg = "[" + szOperationName + "] >>>> 명령선택 취소 전문 전송 .. 데이타 >>>> " + recL2Msg.toString();
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.13 recL2Msg에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
						recL2Msg.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------

						szRtnMsg = ydDelegate.sendMsg(recL2Msg);

						szMsg = "[" + szOperationName + "] >>>> 명령선택 취소 전문 전송 .. 결과 >>>> " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					} else if (JPlateYdConst.YD_EQP_STAT_UP_CMPL.equals(szYdWrkProgStat) || 	// 2 , 권상완료
							   JPlateYdConst.YD_EQP_STAT_DN_WO.equals(szYdWrkProgStat)) { 		// 3 , 권하지시

						szRtnMsg = "권상처리후 크레인명령선택 불가.";
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						return szRtnMsg;

					}
				}

			} else {
				szMsg = "[" + szOperationName + "] 현재 명령 선택중인 기존 작업지시는 없습니다.";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			}

			// 야드작업진행상태 변경
			setRecord = JDTORecordFactory.getInstance().create();
			setRecord.setField("YD_WRK_PROG_STAT", 	pYdWrkProgStat								);
			setRecord.setField("MODIFIER", 			pModifier									);
			setRecord.setField("YD_CRN_SCH_ID", 	pYdCrnSchId									);
			setRecord.setField("YD_EQP_ID", 		pYdEqpId									);
			setRecord.setField("YD_WORD_DT", 		JPlateYdUtils.getCurDate("yyyyMMddHHmmss")	);	// 명령선택일시

			intRtnVal = ydCrnSchDao.updYdCrnWrkProgStat(setRecord);

			if (intRtnVal < 0) {
				szRtnMsg = "UPDATE ERROR";
				szMsg = "[" + szOperationName + "] UPDATE ERROR";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szRtnMsg;

			} else if (intRtnVal == 0) {
				szRtnMsg = "UPDATE 할 스케줄이 없습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
				return JPlateYdConst.RETN_CD_SUCCESS;
			}

			szMsg = "[" + szOperationName + "] 스케줄 ID : " + pYdCrnSchId + "정보를 변경하였습니다.";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//------------------------------------------------------------------------------------------------
			// 야드설비상태 변경 [YD_EQP_STAT]
			//------------------------------------------------------------------------------------------------
			// 야드작업진행상태 변경
			setRecord = JDTORecordFactory.getInstance().create();
			setRecord.setField("MODIFIER", 			pModifier);
			setRecord.setField("YD_EQP_STAT", 		pYdWrkProgStat);
			setRecord.setField("YD_EQP_ID", 		pYdEqpId);

			intRtnVal = ydEqpDAO.updYdEqpStat(setRecord);
			if (intRtnVal < 0) {
				szRtnMsg = "야드작업진행상태 변경시 오류";
				szMsg = "[" + szOperationName + "] UPDATE ERROR";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			szMsg = "[" + szOperationName + "] 메소드 끝 ";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }

		return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of updCrnWrkProgStat()

    /**
     * 오퍼레이션명 : 해당 크레인 파일링실적 수신 (Y7YDL013)
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● Stirng pYdCrnSchId, String pYdWrkProgStat
     * @return ● nRtnVal
     * @throws ● DAOException
     */
	public String procY7PilingRslt(JDTORecord msgRecord) throws DAOException {

    	//DAO
		JPlateYdStockDAO 	 	ydStockDao 		= new JPlateYdStockDAO();
		JPlateYdCrnSchDAO 	 	ydCrnSchDao 	= new JPlateYdCrnSchDAO();
		JPlateYdCrnWrkMtlDAO 	ydCrnWrkMtlDao 	= new JPlateYdCrnWrkMtlDAO();
		JPlateYdWrkbookDAO		ydWrkbookDao	= new JPlateYdWrkbookDAO();
		JPlateYdWrkbookMtlDAO	ydWrkbookMtlDao	= new JPlateYdWrkbookMtlDAO();
		JPlateYdStkLyrDAO 		ydStkLyrDao		= new JPlateYdStkLyrDAO();
		JPlateYdEqpDAO			ydEqpDao		= new JPlateYdEqpDAO();
		JPlateYdStkColDAO		ydStkColDao		= new JPlateYdStkColDAO();
		JPlateYdStkBedDAO 		ydStkBedDao 	= new JPlateYdStkBedDAO();

		// 변수 선언
        String 	szMethodName  	= "procY7PilingRslt";
        String	szRtnMsg		= "";
    	String 	szMsg         	= "";
    	String 	szOperationName = "파일링실적 수신";

    	String 	szYdCrnSchId	= "";					// 스케쥴ID
    	String 	szOldYdCrnSchId	= "";					// 스케쥴ID Old
    	String 	szNewYdCrnSchId	= "";					// 스케쥴ID New
    	String	szOldYdWbookId	= "";					// 작업예약ID Old
    	String	szNewYdWbookId	= "";					// 작업예약ID New
		String 	szStlNo			= "";					// 재료번호
		String 	szYdUpWoLayer	= "";					// 야드권상지시단
		String 	szRcvTcCode		= ydUtils.getTcCode(msgRecord);

    	String 	arrSchId[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 스케쥴코드
    	String 	arrWbookId[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 기존작업예약ID
    	String 	arrStlNo[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 재료번호
    	String 	arrYdLoc[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 재료적치열+재료적치베드
    	String 	arrLayer[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 재료적치단

    	String	szYdGp			= "";
    	String	szYdEqpId		= "";
    	String	szYdPilingSh 	= "";					// 파일링매수
		String	szYdPilingGp	= "";					// 파일링구분 (P:파일링, H:횡행작업, M:멀티작업)
		String	szModifier		= "";
		String 	szSendMsg		= "";
		String	szYdSchCd		= "";
    	String	szYdUpWoLoc 	= "";
    	String	szYdUpWoBed		= "";
    	String	szYdDnWoLoc 	= "";
    	String	szYdStkBedNo 	= "";
    	String	szYdStkLyrNo	= "";
    	String	szYdAidWrkYn	= "";					// 보조작업여부
    	String	szSaveAidWrkYn	= "";					// 주작업+보조작업 으로 파일링 못하도록 체크하기위한 필드
    	String	szYdStkColGp 	= "";					// 적치열구분 		(횡작업길이체크 용도)
		String	szYdStkSpanGp 	= "";					// 적치스판구분 	(횡작업길이체크 용도)
		String	szFstLyrNo		= "";
		String	szYdWrkStat		= "";
		String	szYdEqpStat		= "";

    	double	dArrWrkT[]		= {0,0,0,0,0};			// 야드설비작업총두께	[적치단]
    	int		iArrWrkLen[]	= {0,0,0,0,0};			// 야드설비작업총길이	[적치베드]

    	int		iSumWrkWt		= 0;					// 야드설비작업총중량
    	double	dSumWrkT		= 0;					// 야드설비작업총두께
    	int		iSumWrkLen		= 0;					// 야드설비작업총길이
    	int		iMtlL			= 0;					// 재료의길이
    	int		iWrkCnt			= 0;
    	int		iUpWrkCnt		= 0;
    	int		iDnWrkCnt		= 0;

		// 레코드선언
		JDTORecordSet rsResult  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara      = null;
		JDTORecord recEqp 		= JDTORecordFactory.getInstance().create();
		JDTORecord recSch 		= JDTORecordFactory.getInstance().create();
		JDTORecord recMtl 		= JDTORecordFactory.getInstance().create();
		JDTORecord recCol 		= JDTORecordFactory.getInstance().create();
		JDTORecord recWbook		= JDTORecordFactory.getInstance().create();
		JDTORecord recL2Msg		= null;

    	int 	intRtnVal 		= 0;
    	int		iBedIdx 		= 0;		// 적치베드 	Index (0~2)
    	int		iLyrIdx 		= 0;		// 적치단 	Index (0~5)
    	int		iYdPilingSh		= 0;
    	int		iYdStkColL		= 0;
    	boolean	bLocCheck		= true;

        try {
        	szMsg = "["+szOperationName+"] 파일링 실적 .. 메소드 시작 >>>>" + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if ("".equals(szRcvTcCode)) {
				szRcvTcCode = "Y7YDL013";
			}

			szYdEqpId		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			szYdPilingSh 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_PILING_SH");
			szYdPilingGp	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_PILING_GP");			// 파일링구분 (P:파일링, H:횡행작업, M:멀티작업)
			szModifier		= ydDaoUtils.paraRecModifier(msgRecord);
			iYdPilingSh		= ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_PILING_SH");

			//------------------------------------------------------------------------------------------------
			// 설비상태  체크
			//------------------------------------------------------------------------------------------------
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			recEqp    = JDTORecordFactory.getInstance().create();
        	recPara   = JDTORecordFactory.getInstance().create();
        	recPara.setField("YD_EQP_ID", 			szYdEqpId);

			intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult);
			if (intRtnVal > 0) {
				rsResult.first();
				recEqp = rsResult.getRecord();
				szYdEqpStat = ydDaoUtils.paraRecChkNull(recEqp, "YD_EQP_STAT");
			}

			if (JPlateYdConst.YD_EQP_STAT_UP_CMPL.equals(szYdEqpStat) || 			// 권상완료
				JPlateYdConst.YD_EQP_STAT_DN_WO.equals(szYdEqpStat)) {				// 권하지시

				szRtnMsg = "권상처리후 파일링/횡작업 불가.";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return szRtnMsg;
        	}

			// 파라미터 체크
			if (iYdPilingSh < 1 || iYdPilingSh > 15) {
				szRtnMsg = "파일링 매수 오류 :: " + szYdPilingSh;
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}
			szYdGp = ydUtils.substr(szYdEqpId, 0, 1);

			if (!"H".equals(szYdPilingGp) && !"P".equals(szYdPilingGp) && !"M".equals(szYdPilingGp)) {
				szRtnMsg = "파일링 구분 오류 :: " + szYdPilingGp;
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			if ("H".equals(szYdPilingGp) && iYdPilingSh > 3) {
				szRtnMsg = "횡작업시 3매 초과!!";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}
			if ("P".equals(szYdPilingGp) && iYdPilingSh > 5) {
				szRtnMsg = "파일링시 5매 초과!!";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}
			if ("M".equals(szYdPilingGp) && iYdPilingSh > 15) {
				szRtnMsg = "멀티작업시 15매 초과!!";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			/*
			YD_EQP_ID			야드설비ID			CHAR	6
			YD_PILING_SH		파일링매수			NUMBER	2
			YD_PILING_GP		파일링구분			CHAR	1		파일링구분 (P:파일링, H:횡행작업, M:멀티작업)

			YD_CRN_SCH_ID1		야드크레인스케쥴ID1	CHAR	18
			STL_NO1				재료번호1				CHAR	11
			YD_UP_WO_LOC1		권상지시위치1			CHAR	8
			YD_UP_WO_LAYER1		권상지시단1			CHAR	3
						:
			YD_CRN_SCH_ID15		야드크레인스케쥴ID15	CHAR	18
			STL_NO15			재료번호15			CHAR	11
			YD_UP_WO_LOC15		권상지시위치15			CHAR	8
			YD_UP_WO_LAYER15	권상지시단15			CHAR	3
			*/

        	recPara  = JDTORecordFactory.getInstance().create();

        	iBedIdx 	= 0;		// 적치베드 	Index (0~2)
        	iLyrIdx 	= 0;		// 적치단 	Index (0~5)
        	szFstLyrNo 	= "";

        	// 파일링 실적중 최하단 구하기
			for(int ii=0; ii<iYdPilingSh; ii++) {
				szYdUpWoLayer 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WO_LAYER"+Integer.toString(ii+1));	// 권상지시단
				if (ii==0) {
					szFstLyrNo	= szYdUpWoLayer;
				}
				if (!"".equals(szYdUpWoLayer) && !"".equals(szFstLyrNo)) {
					if (Integer.parseInt(szYdUpWoLayer) < Integer.parseInt(szFstLyrNo)) {
						szFstLyrNo	= szYdUpWoLayer;
					}
				}
			}
			szMsg    = "["+szOperationName+"] 파일링/횡작업 실적 수신데이타중 최하단 정보 >>>> " + szFstLyrNo;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			// 야드크레인스케쥴ID, 재료번호, 권상지시위치, 권상지시단 체크
			for(int ii=0; ii<iYdPilingSh; ii++) {
				szYdCrnSchId  	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID"+Integer.toString(ii+1));		// 야드크레인스케쥴ID
				szStlNo		  	= ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"+Integer.toString(ii+1));			// 재료번호
				szYdUpWoLoc 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WO_LOC"+Integer.toString(ii+1));		// 권상지시위치
				szYdUpWoLayer 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WO_LAYER"+Integer.toString(ii+1));	// 권상지시단
        		szYdStkColGp  	= ydUtils.substr(szYdUpWoLoc, 0, 6);		// 권상위치구분
				szYdStkSpanGp	= ydUtils.substr(szYdUpWoLoc, 2, 2);		// 권상위치 스판구분
				szYdUpWoBed		= ydUtils.substr(szYdUpWoLoc, 6, 2);		// 권상위치 베드 번호

				// 스케쥴 ID 체크
				if ("".equals(szYdCrnSchId)) {
					szRtnMsg = "스케쥴 ID 오류 :: " + szYdCrnSchId;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// 재료번호 체크
				if ("".equals(szStlNo)) {
					szRtnMsg = "재료번호 오류 :: " + szStlNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// 권상지시위치 체크
				bLocCheck = true;
				if ("".equals(szYdUpWoLoc)) {
					bLocCheck = false;
				} else {
		        	rsResult  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		        	recPara   = JDTORecordFactory.getInstance().create();
		        	recPara.setField("YD_STK_COL_GP", 		szYdStkColGp);
		        	recPara.setField("YD_STK_BED_NO", 		szYdUpWoBed);

		        	intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsResult);
		        	if (intRtnVal <= 0) {
		        		bLocCheck = false;
		        	}
				}

				if (!bLocCheck) {
					szRtnMsg = "권상위치 오류 :: " + szYdUpWoLoc;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// 권상지시단 체크
				if ("".equals(szYdUpWoLayer)) {
					szRtnMsg = "권상위치단 오류 :: " + szYdUpWoLayer;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				if (!"".equals(szYdCrnSchId)) {
					if ("".equals(szOldYdCrnSchId)) {
						szOldYdCrnSchId = szYdCrnSchId;
					}

					// RT일경우 열로 Bed Index 가져온다
                    if ("RT".equals(ydUtils.substr(szYdUpWoLoc, 2, 2))) {
    		        	iBedIdx = this.getBedIdx(arrYdLoc, szYdUpWoLoc);
    		        	iLyrIdx = Integer.parseInt(szYdUpWoLayer) - 1;										// 적치단 	Index (0~5)
                    } else {
    		        	iBedIdx = Integer.parseInt(szYdUpWoBed)   - 1;										// 적치베드 	Index (0~2)
    		        	iLyrIdx = Integer.parseInt(szYdUpWoLayer) - 1;										// 적치단 	Index (0~5)
                    }
                    
					if ((iBedIdx >= 0 && iBedIdx < 3) && (iLyrIdx >= 0 && iLyrIdx < 5)) {

				    	if ("".equals(arrLayer[iLyrIdx][iBedIdx])) {
						    arrSchId[iLyrIdx][iBedIdx] = szYdCrnSchId;		// 스케쥴코드
					    	arrStlNo[iLyrIdx][iBedIdx] = szStlNo;			// 재료번호
					    	arrYdLoc[iLyrIdx][iBedIdx] = szYdUpWoLoc;		// 재료적치열
					    	arrLayer[iLyrIdx][iBedIdx] = szYdUpWoLayer;		// 재료적치단
				    	} else {
							szRtnMsg = "적치단 중복 :: " + (szYdUpWoLoc + "-" + szYdUpWoLayer);
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
				    	}

					} else {
						szRtnMsg = "적치단 ERROR :: " + (szYdUpWoLoc + "-" + szYdUpWoLayer);
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					//------------------------------------------------------------------------------------------------
		    		// 기존 크레인작업지시 조회 --> 작업예약 ID SET
					//------------------------------------------------------------------------------------------------
		        	recPara = JDTORecordFactory.getInstance().create();
		        	recPara.setField("YD_CRN_SCH_ID", 		szYdCrnSchId);

		        	rsResult  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		        	intRtnVal = ydCrnSchDao.getYdCrnSch(recPara, rsResult);
		        	if (intRtnVal <= 0) {
						szRtnMsg = "기존 크레인작업지시 조회 오류 ::" + Integer.toString(intRtnVal);
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
		        	}
		        	rsResult.first();
		        	recSch = rsResult.getRecord();
		        	arrWbookId[iLyrIdx][iBedIdx] = ydDaoUtils.paraRecChkNull(recSch, "YD_WBOOK_ID");
		        	szYdAidWrkYn = ydDaoUtils.paraRecChkNull(recSch, "YD_AID_WRK_YN", "N");
		        	if ("".equals(szSaveAidWrkYn)) {
		        		szSaveAidWrkYn = szYdAidWrkYn;
		        	}
		        	if (!szSaveAidWrkYn.equals(szYdAidWrkYn)) {
						szRtnMsg = "(보조작업,주작업)을 파일링/횡작업 불가!!";
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
		        	}

		        	// 스케쥴 코드 체크하여 RT일때는 파일링/횡작업 안되도록 처리
		        	szYdSchCd 	= ydDaoUtils.paraRecChkNull(recSch, "YD_SCH_CD");
		        	szYdUpWoLoc = ydDaoUtils.paraRecChkNull(recSch, "YD_UP_WO_LOC");
		        	szYdDnWoLoc = ydDaoUtils.paraRecChkNull(recSch, "YD_DN_WO_LOC");
		    		szYdWrkStat = ydDaoUtils.paraRecChkNull(recSch, "YD_WRK_PROG_STAT");

					szMsg = "["+szOperationName+"] 파일링/횡작업 확인 >>>> 스케쥴코드::" + szYdSchCd + ", 권상위치::" + szYdUpWoLoc + ", 권하위치::" + szYdDnWoLoc + ", 보조작업::" + szYdAidWrkYn + ", 작업상태::" + szYdWrkStat;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		        	//작업진행상태 체크(2, 3) 2,3이 아니면 에러 메시지 출력후 리턴
			        if ("2".equals(szYdWrkStat) || "3".equals(szYdWrkStat) || "4".equals(szYdWrkStat)) {
						szRtnMsg = "대기/명령선택 상태에서만 작업가능!!" + szYdWrkStat;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
			        }

					// 2013.07.27 RT에서 파일링 되도록 변경
					/*
					if ("RT".equals(ydUtils.substr(szYdSchCd, 2, 2))) {
						szRtnMsg = "RT에서는 파일링/횡작업 불가 .. 스케쥴코드::" + szYdSchCd;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
					*/

					// TO위치가 보수장일때 파일링 안되도록 처리 --> 2013.09.12 TO위치가 보수장이어도 파일링 되도록 변경
					/*
					if ("P".equals(szYdPilingGp) && "BS".equals(ydUtils.substr(szYdDnWoLoc, 2, 2))) {
						szRtnMsg = "보수장에서는 파일링 불가 .. 권하위치::" + szYdDnWoLoc;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
					*/

		        	if ("".equals(szOldYdWbookId)) {
			        	recWbook = rsResult.getRecord();		// 작업예약 등록을 위해 Record Save
		        		szOldYdWbookId = ydDaoUtils.paraRecChkNull(recWbook, "YD_WBOOK_ID");
		        	}

		        	// TO위치에 따라 열길이 체크 (횡작업, 멀티작업 일때만 체크)
		        	if (!"XX010101".equals(szYdDnWoLoc) && !"XXYY0101".equals(szYdDnWoLoc) && !"P".equals(szYdPilingGp)) {

		        		szYdStkColGp  = ydUtils.substr(szYdDnWoLoc, 0, 6);		// 적치열구분
		        		szYdStkSpanGp = ydUtils.substr(szYdDnWoLoc, 2, 2);		// 스판구분

						// 권하지시위치 체크
			        	rsResult  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			        	recPara   = JDTORecordFactory.getInstance().create();
			        	recPara.setField("YD_STK_COL_GP", 		szYdStkColGp);
			        	recPara.setField("YD_STK_BED_NO", 		szYdUpWoBed);

			        	intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsResult);
			        	if (intRtnVal <= 0) {
							szRtnMsg = "TO BED정보 조회 오류! [" + szYdUpWoBed + "]";
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
			        	}

		        		if ("BS".equals(szYdStkSpanGp) || "CN".equals(szYdStkSpanGp) || "CB".equals(szYdStkSpanGp) || "RT".equals(szYdStkSpanGp)) {

							szMsg = "["+szOperationName+"] 설비일때 횡작업일때 열길이 체크 SKIP :: " + szYdStkSpanGp;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		        		} else {
				        	//------------------------------------------------------------------------------------------------
							// 열정보 조회 (횡작업시 길이 체크)
							//------------------------------------------------------------------------------------------------
							rsResult = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				        	recPara  = JDTORecordFactory.getInstance().create();
							recPara.setField("YD_STK_COL_GP", szYdStkColGp);

							intRtnVal = ydStkColDao.getYdStkcol(recPara, rsResult);
							if (intRtnVal < 1) {
								szRtnMsg = "TO 위치 길이 체크 오류! [" + szYdStkColGp + "]";
								szMsg    = "["+szOperationName+"] " + szRtnMsg;
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
								return szRtnMsg;
							}
				        	rsResult.first();
							recCol = rsResult.getRecord();

							if (ydDaoUtils.paraRecChkNullInt(recCol, "YD_STK_COL_L") > iYdStkColL) {
								iYdStkColL = ydDaoUtils.paraRecChkNullInt(recCol, "YD_STK_COL_L");
							}

							szMsg = "["+szOperationName+"] 횡작업일때 열길이 조회 >>>> TO위치 :: " + szYdDnWoLoc + ", 결과 ::" + iYdStkColL;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		        		}
		        	}

		        	//------------------------------------------------------------------------------------------------
					// 재료정보 조회
					//------------------------------------------------------------------------------------------------
					rsResult = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		        	recPara  = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", szStlNo);
					intRtnVal = ydStockDao.getYdStock(recPara, rsResult);
		        	if (intRtnVal <= 0) {
						szRtnMsg = "재료정보 조회 오류 .. 재료번호::" + szStlNo;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
		        	}
		        	rsResult.first();
		        	recMtl  = rsResult.getRecord();

		        	iSumWrkWt = iSumWrkWt + ydDaoUtils.paraRecChkNullInt(recMtl, "YD_MTL_WT");
		        	if (ydDaoUtils.paraRecChkNullDouble(recMtl, "YD_MTL_T") > dArrWrkT[iLyrIdx]) {
		        		dArrWrkT[iLyrIdx] = ydDaoUtils.paraRecChkNullDouble(recMtl, "YD_MTL_T");
		        	}

		        	iMtlL = ydDaoUtils.paraRecChkNullInt(recMtl, "YD_MTL_L");
		        	if (iMtlL > iYdStkColL) {
		        		iMtlL = iYdStkColL;
		        	}

					if (!"P".equals(szYdPilingGp)) {		// 횡작업,멀티작업 일때
						iArrWrkLen[iLyrIdx] = iArrWrkLen[iLyrIdx]  + iMtlL;
					} else {
						if (iArrWrkLen[iLyrIdx] < iMtlL) {
							iArrWrkLen[iLyrIdx] = iMtlL;
						}
					}
				}
			}

			if (iYdStkColL == 0) {
				iYdStkColL = 25000;
			}

        	dSumWrkT	= 0;
        	iSumWrkLen	= 0;

			for(int kk=0; kk<5; kk++) {
				dSumWrkT = dSumWrkT + dArrWrkT[kk];
				if (iArrWrkLen[kk] > iSumWrkLen) {
					iSumWrkLen = iArrWrkLen[kk];
				}
			}

			szMsg = "["+szOperationName+"] >>>> 파일링/횡작업 재료 정보 >>>> 길이 : " + iArrWrkLen.toString() + ", 두께 : " + dArrWrkT.toString() + ", 중량 : " + iSumWrkWt;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "["+szOperationName+"] >>>> 파일링/횡작업 재료 계산 >>>> 길이 : " + iSumWrkLen + ", 두께 : " + dSumWrkT + ", 중량 : " + iSumWrkWt;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			// 재료길이 25M 넘는 재료는 횡작업 불가 [횡작업,멀티작업 일때만 체크]
			if (!"P".equals(szYdPilingGp) && iSumWrkLen > iYdStkColL) {
				szRtnMsg = "재료길이 합이 "+(iYdStkColL/1000)+"M 넘어 횡작업 불가! [" + iSumWrkLen + "]";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}
			// 재료두께 25T 넘는 재료는 파일링 불가 [파일링,멀티작업 일때만 체크]
			if (!"H".equals(szYdPilingGp) && dSumWrkT > 50) {
				szRtnMsg = "재료두께 합이 50t 넘어 파일링작업 불가! [" + dSumWrkT + "]";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}
			
			if("PECRE2".equals(szYdEqpId)) {
				// 35톤 넘는 재료는 불가
				if (iSumWrkWt > 35000) {
					szRtnMsg = "재료중량 합이 35톤 넘어 작업 불가! [" + iSumWrkWt + "]";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
			} 
			else if("PFCRF2".equals(szYdEqpId)){  //21호기(PFCRF2) 제한 중량 기준 변경 25T->30T 2021.12.06 박종호. 허남웅 책임 요청사항.
				// 30톤 넘는 재료는 불가
				if (iSumWrkWt > 30000) {
					szRtnMsg = "재료중량 합이 30톤 넘어 작업 불가! [" + iSumWrkWt + "]";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
			}
			else {
				// 25톤 넘는 재료는 불가
				if (iSumWrkWt > 25000) {
					szRtnMsg = "재료중량 합이 25톤 넘어 작업 불가! [" + iSumWrkWt + "]";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
			}
			
			szMsg = "["+szOperationName+"] >>>> 크레인스케쥴 등록 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//------------------------------------------------------------------------------------------------
			// 1. 크레인 작업예약 생성 [신규 파일링 작업예약]
			//------------------------------------------------------------------------------------------------
			// 1.1. 작업예약ID를 할당받는다 (보조작업일때는 작업예약 생성 안함)
			if ("Y".equals(szYdAidWrkYn)) {
				szNewYdWbookId = szOldYdWbookId;
			} else {
				szNewYdWbookId = ydWrkbookDao.getSeqId();		// intGp = 9
	    		if ("".equals(szNewYdWbookId)) {
					szRtnMsg = "작업예약 Id를 생성하지 못했습니다.";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
	    		}

				// 1.2. 작업예약생성
	    		recWbook.setField("YD_WBOOK_ID", 	szNewYdWbookId);
	    		recWbook.setField("REGISTER",	    szModifier);   			// 등록자
	    		recWbook.setField("MODIFIER",	    szModifier);			// 수정자
	    		intRtnVal = ydWrkbookDao.insYdWrkbook(recWbook);
	        	if (intRtnVal <= 0) {
					szRtnMsg = "신규 작업예약 생성 오류 ::" + Integer.toString(intRtnVal);
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
	        	}
			}

			//------------------------------------------------------------------------------------------------
			// 2. 크레인 작업지시  생성 [신규 파일링 작업지시]
			//------------------------------------------------------------------------------------------------
			// 2.1. 크레인스케줄ID를 할당받는다
			szNewYdCrnSchId = ydCrnSchDao.getSeqId();		// intGp = 9
    		if ("".equals(szNewYdCrnSchId)) {
				szRtnMsg = "크레인스케줄 Id를 생성하지 못했습니다.";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
    		}

			//------------------------------------------------------------------------------------------------
    		// 2.2. 기존 크레인작업지시 조회
			//------------------------------------------------------------------------------------------------
        	recPara = JDTORecordFactory.getInstance().create();
        	recPara.setField("YD_CRN_SCH_ID", 		szOldYdCrnSchId);

        	rsResult  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
        	intRtnVal = ydCrnSchDao.getYdCrnSch(recPara, rsResult);
        	if (intRtnVal <= 0) {
				szRtnMsg = "기존 크레인작업지시 조회 오류 ::" + Integer.toString(intRtnVal);
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
        	}
        	rsResult.first();
        	recSch = rsResult.getRecord();

        	szYdSchCd = ydDaoUtils.paraRecChkNull(recSch, "YD_SCH_CD");

			szMsg = "["+szOperationName+"] DATA 확인 >>>>" + rsResult.size() + ">>>>" + recSch.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

        	recSch.setField("YD_CRN_SCH_ID", 		szNewYdCrnSchId);								// 신규 야드크레인스케쥴ID
        	recSch.setField("YD_WBOOK_ID", 			szNewYdWbookId);								// 신규 작업예약ID
        	recSch.setField("REGISTER",	     		szModifier);   									// 등록자
        	recSch.setField("MODIFIER",	        	szModifier);									// 수정자
        	recSch.setField("YD_EQP_ID",			szYdEqpId);										// 야드설비ID
        	recSch.setField("YD_MAIN_WRK_MTL_SH",	szYdPilingSh);									// 야드주작업재료매수
        	recSch.setField("YD_EQP_WRK_SH",	    szYdPilingSh);									// 야드설비작업매수
        	recSch.setField("YD_EQP_WRK_WT",	    Integer.toString(iSumWrkWt));					// 야드설비작업중량
        	recSch.setField("YD_EQP_WRK_T",	        Double.toString(dSumWrkT));						// 야드설비작업총두께
        	recSch.setField("YD_EQP_WRK_L",	        Double.toString(iSumWrkLen));					// 야드설비작업총길이
        	recSch.setField("YD_WRK_PROG_STAT", 	JPlateYdConst.YD_EQP_STAT_DN_WO);				// 권하지시
        	recSch.setField("YD_UP_WR_LOC",			recSch.getField("YD_UP_WO_LOC"));				// 야드권상실적위치
        	recSch.setField("YD_UP_WR_LAYER",		recSch.getField("YD_UP_WO_LAYER"));				// 야드권상실적단
        //	recSch.setField("YD_UP_WRK_ACT_GP",		"M");											// 야드권상작업수행구분
        	recSch.setField("YD_UP_WRK_ACT_GP",	    szYdPilingGp);									// 파일링작업구분 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)
        	recSch.setField("YD_UP_WR_XAXIS",		recSch.getField("YD_UP_WO_XAXIS"));				// 야드권상실적X축
        	recSch.setField("YD_UP_WR_YAXIS",		recSch.getField("YD_UP_WO_YAXIS"));				// 야드권상실적Y축
        	recSch.setField("YD_UP_WR_YAXIS1",		recSch.getField("YD_UP_WO_YAXIS1"));			// 야드권상실적Y축1
        	recSch.setField("YD_UP_WR_YAXIS2",		recSch.getField("YD_UP_WO_YAXIS2"));			// 야드권상실적Y축2
        	recSch.setField("YD_UP_WR_ZAXIS",		recSch.getField("YD_UP_WO_ZAXIS"));				// 야드권상실적Z축
        	recSch.setField("YD_WORD_DT", 			JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));	// 명령선택일시
        	recSch.setField("YD_UP_CMPL_DT",		JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));	// 권상완료일시

			//------------------------------------------------------------------------------------------------
    		// 2.3. 크레인작업지시 등록
			//------------------------------------------------------------------------------------------------
        	intRtnVal = ydCrnSchDao.insYdCrnsch(recSch);
        	if (intRtnVal <= 0) {
				szRtnMsg = "기존 크레인작업지시 등록 오류 ::" + Integer.toString(intRtnVal);
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
        	}

        	// 크레인스케쥴의 권하위치로 SET
        	szYdDnWoLoc = ydDaoUtils.paraRecChkNull(recSch, "YD_DN_WO_LOC");

			szMsg = "["+szOperationName+"] >>>> 크레인작업재료 등록 시작 >>>> " + szYdDnWoLoc;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

        	iWrkCnt = 0;
			for(int ii=0; ii<5; ii++) {
				for(int jj=0; jj<3; jj++) {
					if (!"".equals(arrStlNo[ii][jj])) {
						//------------------------------------------------------------------------------------------------
			    		// 2.4. 크레인 작업재료 조회
						//------------------------------------------------------------------------------------------------
			        	recPara.setField("YD_CRN_SCH_ID", 	arrSchId[ii][jj]);
			        	recPara.setField("STL_NO", 			arrStlNo[ii][jj]);
			        	recPara.setField("YD_WBOOK_ID", 	arrWbookId[ii][jj]);

			        	rsResult  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			        	intRtnVal = ydCrnWrkMtlDao.getYdCrnWrkMtl(recPara, rsResult);
			        	if (intRtnVal <= 0) {
							szRtnMsg = "기존 크레인작업재료 조회 .. 재료번호 ::" + arrStlNo[ii][jj] + " >> " + Integer.toString(intRtnVal);
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
			        	}
			        	rsResult.first();
			        	recMtl = rsResult.getRecord();

						//------------------------------------------------------------------------------------------------
			    		// 2.5. 크레인작업재료 등록
						//------------------------------------------------------------------------------------------------
			        	recMtl.setField("YD_CRN_SCH_ID",	szNewYdCrnSchId);							// 야드크레인스케쥴ID
	        			recMtl.setField("STL_NO",			arrStlNo[ii][jj]);							// 재료번호
						recMtl.setField("MODIFIER",	        szModifier);								// 수정자
						recMtl.setField("YD_STK_LYR_NO",	arrLayer[ii][jj]);      					// 야드적치단번호
						recMtl.setField("YD_STK_LOT_TP",	ydUtils.substr(arrYdLoc[ii][jj], 6, 2));	// 권상위치 BED 번호
						recMtl.setField("YD_STK_LOT_CD", 	arrYdLoc[ii][jj]);							// 권상위치

						intRtnVal = ydCrnWrkMtlDao.insYdCrnWrkMtl(recMtl);
			        	if (intRtnVal <= 0) {
							szRtnMsg = "기존 크레인작업재료 등록 오류 ::" + arrStlNo[ii][jj] + " >> " + Integer.toString(intRtnVal);
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
			        	}

						if ("N".equals(szYdAidWrkYn)) {		// 주작업일때만 작업예약 신규로 생성
							//------------------------------------------------------------------------------------------------
				        	// 2.6. 작업예약재료 조회
							//------------------------------------------------------------------------------------------------
				        	rsResult  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				        	intRtnVal = ydCrnWrkMtlDao.getYdCrnWrkMtl(recPara, rsResult);
				        	if (intRtnVal <= 0) {
								szRtnMsg = "기존 작업예약재료 조회 .. 재료번호 ::" + arrStlNo[ii][jj] + " >> " + Integer.toString(intRtnVal);
								szMsg    = "["+szOperationName+"] " + szRtnMsg;
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
								return szRtnMsg;
				        	}
				        	rsResult.first();
				        	recMtl = rsResult.getRecord();

							//------------------------------------------------------------------------------------------------
				        	// 2.7. 작업예약재료 등록
							//------------------------------------------------------------------------------------------------
				        	recMtl.setField("YD_WBOOK_ID",		szNewYdWbookId);	// 작업예약ID
		        			recMtl.setField("STL_NO",			arrStlNo[ii][jj]);	// 재료번호
							recMtl.setField("MODIFIER",	        szModifier);		// 수정자

							intRtnVal = ydWrkbookMtlDao.insYdWrkbookMtl(recMtl);
				        	if (intRtnVal <= 0) {
								szRtnMsg = "기존 작업예약재료 등록 오류 ::" + arrStlNo[ii][jj] + " >> " + Integer.toString(intRtnVal);
								szMsg    = "["+szOperationName+"] " + szRtnMsg;
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
								return szRtnMsg;
				        	}

							//------------------------------------------------------------------------------------------------
				        	// 2.8. 기존작업예약재료 삭제
							//------------------------------------------------------------------------------------------------
				        	recPara.setField("YD_WBOOK_ID", 	arrWbookId[ii][jj]);
				        	recPara.setField("STL_NO", 			arrStlNo[ii][jj]);

							intRtnVal = ydWrkbookMtlDao.deldWrkbookMtl(recPara);
				        	if (intRtnVal <= 0) {
								szRtnMsg = "기존 작업예약재료 삭제 오류 ::" + arrStlNo[ii][jj] + " >> " + Integer.toString(intRtnVal);
								szMsg    = "["+szOperationName+"] " + szRtnMsg;
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							//	return szRtnMsg;
				        	}

							//------------------------------------------------------------------------------------------------
				        	// 2.9. 기존작업예약 삭제
							//------------------------------------------------------------------------------------------------
							intRtnVal = ydWrkbookDao.delYdWrkbook(recPara);
				        	if (intRtnVal <= 0) {
								szRtnMsg = "기존 작업예약 삭제 오류 ::" + arrStlNo[ii][jj] + " >> " + Integer.toString(intRtnVal);
								szMsg    = "["+szOperationName+"] " + szRtnMsg;
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							//	return szRtnMsg;
				        	}

				        	//------------------------------------------------------------------------------------------------
				        	// 2.10. 저장품에 작업예약ID 수정
							//------------------------------------------------------------------------------------------------
				        	recPara.setField("YD_WBOOK_ID",		szNewYdWbookId);	// 작업예약ID
				        	recPara.setField("STL_NO",			arrStlNo[ii][jj]);	// 재료번호
				        	recPara.setField("MODIFIER",	    szModifier);		// 수정자
				        	recPara.setField("YD_SCH_CD",	    szYdSchCd);			// 스케쥴코드
							intRtnVal = ydStockDao.updYdStockWbook(recPara);
				        	if (intRtnVal <= 0) {
								szRtnMsg = "저장품에 작업예약ID 수정 오류 .. 재료번호::" + arrStlNo[ii][jj];
								szMsg    = "["+szOperationName+"] " + szRtnMsg;
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
								return szRtnMsg;
				        	}
						}

			        	iWrkCnt ++;
			        }
				}	// end of Loop jj
			}	// end of Loop ii

			szMsg = "["+szOperationName+"] >>>> 크레인작업재료 등록 정상종료 :: " + iWrkCnt + "건";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//------------------------------------------------------------------------------------------------
			// 설비상태 UPATE --> 크레인 상태를 권하지시로 SET
			//------------------------------------------------------------------------------------------------
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_EQP_ID", 		szYdEqpId);
			recPara.setField("YD_EQP_STAT", 	"3");
			recPara.setField("MODIFIER", 		szModifier);

			intRtnVal = ydEqpDao.updYdEqpStat(recPara);
			if (intRtnVal <= 0) {
				szRtnMsg = "설비상태 변경시 오류 발생 : " + Integer.toString(intRtnVal);
				szMsg = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			//------------------------------------------------------------------------------------------------
			// 2. 저장위치 변경 크레인으로
			//------------------------------------------------------------------------------------------------
			// 2.1. 권상 지시위치의 적치 상태 정보 CLEAR
			// 2.2. 현재료의 적지위치를 크레인으로 변경
			// 2.3. 권하 지시위치의 적치 상태 정보 SET
			//------------------------------------------------------------------------------------------------
        	iWrkCnt 		= 0;
        	szYdStkBedNo	= ydUtils.substr(szYdDnWoLoc,6,2);
        	if (szYdStkBedNo == null || "".equals(szYdStkBedNo)) {
        		szYdStkBedNo = "01";
        	}

			for(int ii=0; ii<5; ii++) {
				for(int jj=0; jj<3; jj++) {
					if (!"".equals(arrStlNo[ii][jj])) {
						// 2.1. 권상위치 CLEAR
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("MODIFIER",			szModifier);
						recPara.setField("YD_STK_LYR_MTL_STAT",	"U");
						recPara.setField("STL_NO",				arrStlNo[ii][jj]);
						recPara.setField("YD_GP",				szYdGp);

						intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
						if (intRtnVal <= 0) {
							szRtnMsg = "파일링 재료 권상위치 CLEAR 오류발생 : " + Integer.toString(intRtnVal);
							szMsg = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						//  RT에서 북아웃시 기존위치가 다른 재료로 변경됨으로 계속 진행하도록 보완
						//	return szRtnMsg;
						} else {
							iUpWrkCnt ++;
						}

						// 2.2. 크레인으로 저장위치 변경
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_STK_COL_GP",       szYdEqpId);
		    			recPara.setField("YD_STK_BED_NO",       "01");
		    			recPara.setField("YD_STK_LYR_NO",       arrLayer[ii][jj]);
						recPara.setField("YD_STK_LYR_MTL_STAT",	"C");
						recPara.setField("STL_NO",				arrStlNo[ii][jj]);
						recPara.setField("MODIFIER",			szModifier);
		                intRtnVal = ydStkLyrDao.updYdStklyrStat(recPara);  	//크레인 적치단의 재료정보 UPDATE

		                // 2.3. 권하위치 CLEAR
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("MODIFIER",			szModifier);
						recPara.setField("YD_STK_LYR_MTL_STAT",	"D");
						recPara.setField("STL_NO",				arrStlNo[ii][jj]);
						recPara.setField("YD_GP",				szYdGp);

						intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
						if (intRtnVal <= 0) {
							szRtnMsg = "파일링 재료 권하위치 CLEAR 오류발생 : " + Integer.toString(intRtnVal);
							szMsg = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						//	return szRtnMsg;
						} else {
							iDnWrkCnt ++;
						}
					}
				}	// end of loop jj
			}	// end of loop ii

			szMsg = "["+szOperationName+"] >>>> 파일링 재료 권상위치 CLEAR 정상종료 :: " + iUpWrkCnt + "건";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "["+szOperationName+"] >>>> 파일링 재료 권하위치 CLEAR 정상종료 :: " + iDnWrkCnt + "건";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//------------------------------------------------------------------------------------------------
			// 2.4. 파일링후 권하위치 SET
			//------------------------------------------------------------------------------------------------
			String szTopLyr = JPlateYdCommonUtils.getTopLyrNoByColGp(ydUtils.substr(szYdDnWoLoc,0,6), "01", "", szYdPilingGp, "");

			szMsg = "["+szOperationName+"] >>>> 파일링 재료 권하위치 단 :: " + szTopLyr;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			for(int ii=0; ii<5; ii++) {
				szYdStkLyrNo = ydDaoUtils.stringPlusInt(szTopLyr, ii);			// 적치단을    1증가
				szYdStkBedNo = "00";

				for(int jj=0; jj<3; jj++) {
					szYdStkBedNo = ydDaoUtils.stringPlusInt2(szYdStkBedNo, 1);	// 적치베드를 1증가, 단은 그대로 놔둠
					if (!"".equals(arrStlNo[ii][jj])) {
						// 2.4.1 저장위치 공베드 조회 (횡작업일때 베드 증가 , 파일링일때 적치단 증가)
						rsResult = JDTORecordFactory.getInstance().createRecordSet("retTmp");
						recPara  = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_STK_COL_GP",		ydUtils.substr(szYdDnWoLoc,0,6));
						recPara.setField("YD_STK_BED_NO",		szYdStkBedNo);
						recPara.setField("YD_PILING_GP",		szYdPilingGp);		// 파일링/횡작업 구분
						recPara.setField("YD_STK_LYR_NO",		szYdStkLyrNo);

						intRtnVal = ydStkLyrDao.getPilingToLoc(recPara, rsResult);
						if (intRtnVal > 0) {

							rsResult.first();
							recPara = JDTORecordFactory.getInstance().create();
							recPara = rsResult.getRecord();

			    			szYdStkBedNo = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
			    			szYdStkLyrNo = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");

							szMsg = "["+szOperationName+"] >>>> 파일링 재료  권하위치  >>>> " + szYdStkBedNo + "-" + szYdStkLyrNo;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			                // 2.4.2. 권하위치 SET
							recPara = JDTORecordFactory.getInstance().create();
							recPara = rsResult.getRecord();
							recPara.setField("YD_STK_COL_GP",       ydUtils.substr(szYdDnWoLoc,0,6));
			    			recPara.setField("YD_STK_BED_NO",       szYdStkBedNo);
			    			recPara.setField("YD_STK_LYR_NO",       szYdStkLyrNo);
							recPara.setField("YD_STK_LYR_MTL_STAT",	"D");
							recPara.setField("STL_NO",				arrStlNo[ii][jj]);
							recPara.setField("MODIFIER",			szModifier);
			                intRtnVal = ydStkLyrDao.updYdStklyrStat(recPara);  	//크레인 적치단의 재료정보 UPDATE

						} else {

							szMsg = "["+szOperationName+"] >>>> 파일링 재료 권하위치 미존재로 SKIP >>>> ";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						}
					}
				}	// end of loop jj
			}	// end of loop ii

			//------------------------------------------------------------------------------------------------
			// 3. 크레인 작업지시  취소 [파일링전 작업지시]
			//------------------------------------------------------------------------------------------------
        	iWrkCnt = 0;
			for(int ii=0; ii<5; ii++) {
				for(int jj=0; jj<3; jj++) {
					if (!"".equals(arrStlNo[ii][jj])) {
						recPara.setField("YD_CRN_SCH_ID",	arrSchId[ii][jj]);		// 야드크레인스케쥴ID
						recPara.setField("MODIFIER",	    szModifier);		// 수정자
						recPara.setField("DEL_YN",	    	"Y");				// 삭제여부

			        	intRtnVal = ydCrnSchDao.delYdCrnSch(recPara);
			        	if (intRtnVal <= 0) {
							szRtnMsg = "기존 크레인작업재료 삭제 오류 .. 야드크레인스케쥴ID ::" + szOldYdCrnSchId + " >> " + Integer.toString(intRtnVal);
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
			        	}
					}

					if (!"".equals(arrStlNo[ii][jj])) {
			    		// 3.1. 크레인 작업재료 조회
			        	recPara.setField("YD_CRN_SCH_ID", 	arrSchId[ii][jj]);
			        	recPara.setField("STL_NO", 			arrStlNo[ii][jj]);
						recPara.setField("MODIFIER",	    szModifier);		// 수정자
						recPara.setField("DEL_YN",	    	"Y");				// 삭제여부

			        	intRtnVal =	ydCrnWrkMtlDao.delYdCrnWrkMtl(recPara);
			        	if (intRtnVal <= 0) {
							szRtnMsg = "기존 크레인작업재료 삭제 오류 ::" + arrStlNo[ii][jj] + " >> " + Integer.toString(intRtnVal);
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
			        	}
			        	iWrkCnt ++;
					}
				}
			}
			szMsg = "["+szOperationName+"] >>>> 크레인작업재료 삭제 정상종료 :: " + iWrkCnt + "건";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//------------------------------------------------------------------------------------------------
			// 4.크레인 작업지시 취소 전송 [기존 파일링 전 작업지시]
			//------------------------------------------------------------------------------------------------
			szMsg = "["+szOperationName+"] >>>> 작업지시 취소 전문 전송 .. 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

        	iWrkCnt = 0;
			for(int ii=0; ii<5; ii++) {
				for(int jj=0; jj<3; jj++) {
					if (!"".equals(arrStlNo[ii][jj])) {
				        recL2Msg = JDTORecordFactory.getInstance().create();
				        recL2Msg.setField("MSG_ID", 					"YDY7L004");						// 크레인 작업지시
				        recL2Msg.setField("YD_CRN_SCH_ID",    			arrSchId[ii][jj]);
				        recL2Msg.setField("YD_WRK_PROG_STAT", 			JPlateYdConst.YD_EQP_STAT_UP_WO);	// 크레인 작업상태
				        recL2Msg.setField("MSG_GP",           			"D");

						szMsg = "["+szOperationName+"] >>>> 작업지시 취소 전문 전송 .. 데이타 >>>> " + recL2Msg.toString();
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				        szSendMsg = ydDelegate.sendMsg(recL2Msg);
				        iWrkCnt ++;

						szMsg = "["+szOperationName+"] >>>> 작업지시 취소 전문 전송 .. 결과 >>>> " + szSendMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					}
				}
			}
			szMsg = "["+szOperationName+"] >>>> 작업지시 취소 전문 전송 .. 종료 :: " + iWrkCnt + "건 전송완료";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//------------------------------------------------------------------------------------------------
			// 5.크레인 작업지시 전송 [신규 파일링 작업지시] - 권하작업지시로 Set
			//------------------------------------------------------------------------------------------------
			szMsg = "["+szOperationName+"] >>>> 신규 작업지시 전문 전송 .. 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        recL2Msg = JDTORecordFactory.getInstance().create();
	        recL2Msg.setField("MSG_ID",						"YDY7L004");						// 크레인 작업지시
	        recL2Msg.setField("YD_CRN_SCH_ID",    			szNewYdCrnSchId);
	        recL2Msg.setField("YD_WRK_PROG_STAT", 			JPlateYdConst.YD_EQP_STAT_DN_WO);	// 권하지시
	        recL2Msg.setField("MSG_GP",           			"I");

	        szSendMsg = ydDelegate.sendMsg(recL2Msg);

			szMsg = "["+szOperationName+"] >>>> 신규 작업지시 전문 전송 .. 종료 :: " + szSendMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "["+szOperationName+"] 메소드 끝 ";

			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }

		return JPlateYdConst.RETN_CD_SUCCESS;
	}
	
	/**
     * 오퍼레이션명 : RT일때 적치열로 Bed 인덱스 구하기
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● 적치위치Arr, 적치위치
     * @return ● nRtnVal
     */	
	public int getBedIdx(String pArrYdLoc[][], String pYdUpWoLoc) {
		int  nRtnVal = -1;
		
		// 1단에 적치위치를 비교하여 공백이거나 동일하면 Return 
		for(int ii=0; ii<3; ii++) {
			if ("".equals(pArrYdLoc[0][ii]) || pYdUpWoLoc.equals(pArrYdLoc[0][ii])) {
				nRtnVal = ii;
				break;
			}
		}

		return nRtnVal;
	}

	/**
     * 오퍼레이션명 : 가스장일때 적치열로 Bed 인덱스 구하기
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● 적치위치Arr, 적치위치
     * @return ● nRtnVal
     */	
	public int getBedIdxCn(String pArrYdLoc[][], String pYdUpWoLoc, int iLyrIdx) {
		int  nRtnVal = -1;
		
		// 1단에 적치위치를 비교하여 공백이거나 동일하면 Return 
		for(int ii=0; ii<3; ii++) {
			if ("".equals(pArrYdLoc[iLyrIdx][ii]) || pYdUpWoLoc.equals(pArrYdLoc[iLyrIdx][ii])) {
				nRtnVal = ii;
				break;
			}
		}

		return nRtnVal;
	}
	
    /**
     * 오퍼레이션명 : 2후판전단 L2시스템으로부터 Book-In/Book-Out요구 수신 [S1YDL013]
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● Stirng pYdCrnSchId, String pYdWrkProgStat
     * @return ● nRtnVal
     * @throws ● DAOException
     */
	public String  procS1BookInOutReq(JDTORecord msgRecord) throws DAOException { 
		// 레코드선언
		JDTORecordSet 	rsResult  	= null;
		JDTORecord 		inRec 		= null;
		JDTORecord 		tempRec 	= null;
		JDTORecord 		recPara   	= null;
		JDTORecord 		recSchPara  = null;
		JDTORecord 		recL2Para   = null;

		// 변수 선언
        String 	szMethodName  		= "procS1BookInOutReq";
        String	szRtnMsg			= JPlateYdConst.RETN_CD_SUCCESS;
    	String 	szMsg         		= "";
    	String 	szOperationName 	= "Book-In/Book-Out요구 수신";

		String 	szPlateId 			= "";			// PLATE_NO
		String 	szOperationType 	= "";			// 1:Book In, 2:Book Out
		String 	szFromLoc			= "";			// From  야드L3형식(EX:FBRT03)
		String  szFromTrkZone       = "";           // From  L2 트랙킹존 형식(EX:2330)
		String 	szToLoc 			= "";			// To
		String 	szReasonCode		= "";			// Book-Out원인
		String 	szOperationMode		= "";			// 1:one time 2:Start 3:End
		String 	szOperationBed		= "";			// Book in/Book-Out시 야드적치Bed번호
		String 	szOperationDate		= "";			// BOOKOUT일시
		String	szCraneNo			= "";			// 북인대상재 구분 'A1':북인대상재

		String 	szYdGp          	= ""; 			// 야드구분
		String 	szYdBayGp       	= ""; 			// 야드동구분

		String 	szYdSchCd       	= "";
		String 	szYdSchProhExn  	= "";  			// 야드스케쥴금지유무
		String 	szYdWrkCrnPrior 	= "1"; 			// 야드작업크레인우선순위
		String 	szYdStkLyrNo	 	= "001";		// 야드적치단 , RT는 무조건 1단
		String 	szYdWrkCrn      	= "";			// 야드설비ID (NOT NULL이기 때문에 RULE에 등록된 설비ID SET)
		String	szYdWbookId 		= "";			// 작업예약ID 생성
		String	szYdStkColGp		= "";

		String	szYdToLocGuide		= "";

        String 	szRegister			= "";			// 등록자, MSG_ID
        
        String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";

    	int 	intRtnVal 			= 0;
		int		iWBookInsCnt		= 0;			// 작업예약등록 건수
		int		iCrnSchCnt			= 0;			// 크레인스케쥴 호출 건수
		int		iSchOkCnt			= 0;			// 스케줄호출 OK 건수

		StringBuffer sbARR_WBOOK_ID = new StringBuffer();

    	//DAO
    	JPlateYdStockDAO    ydStockDao   	= new JPlateYdStockDAO();
    	JPlateYdWrkbookDAO	ydWrkbookDao 	= new JPlateYdWrkbookDAO();
    	JPlateYdCrnSchDAO 	ydCrnSchDao		= new JPlateYdCrnSchDAO();
    	JPlateYdStkLyrDAO   ydStkLyrDao		= new JPlateYdStkLyrDAO();
    	JPlateYdSchRuleDAO  ydSchRuleDao	= new JPlateYdSchRuleDAO();

        try {
        	szMsg = "["+szOperationName+"] ---- 메소드 시작  >>>> " + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			/*
			PLATE_ID				PLATE_ID				CHAR	10		Operation_Mode가 '2'또는'3'일때 'PL999999'
			SHIFT_CODE				SHIFT_CODE				CHAR	2		근조
			OPERATION_TYPE			OPERATION_TYPE			CHAR	1		1:Book In, 2:Book Out
			OPERATION_SOURCE		OPERATION_SOURCE		CHAR	8		FROM위치
			OPERATION_DESTINATION	OPERATION_DESTINATION	CHAR	8		TO위치
			REASON_CODE				REASON_CODE				CHAR	3		Book-Out원인 (888:TEST , 999:북아웃취소)
			CRANE_NO				CRANE_NO				CHAR	2		크레인번호
			OPERATION_MODE			OPERATION_MODE			CHAR	1		1:one time 2:Start 3:End
			OPERATION_BED			OPERATION_BED			CHAR	2		Book in/Book-Out시 야드적치Bed번호
			OPERATION_DATE			OPERATION_DATE			CHAR	14		Date & Time
			*/
			szPlateId 		= ydDaoUtils.paraRecChkNull(msgRecord, "PLATE_ID");					// PLATE_NO
			szOperationType = ydDaoUtils.paraRecChkNull(msgRecord, "OPERATION_TYPE");			// 1:Book In, 2:Book Out
			szFromLoc		= ydDaoUtils.paraRecChkNull(msgRecord, "OPERATION_SOURCE");			// From
			szFromTrkZone   = ydDaoUtils.paraRecChkNull(msgRecord, "OPERATION_SOURCE");			// From
			
			szToLoc 		= ydDaoUtils.paraRecChkNull(msgRecord, "OPERATION_DESTINATION");	// To
			if ("".equals(szToLoc)) { //2050
				szToLoc		= ydDaoUtils.paraRecChkNull(msgRecord, "OPERATION_TARGET");			// (OPERATION_TARGET , OPERATION_DESTINATION) I/F Layout에 항목 변경
			}
			szReasonCode	= ydDaoUtils.paraRecChkNull(msgRecord, "REASON_CODE");				// Book-Out원인 (888:TEST , 999:북아웃취소)
			szCraneNo		= ydDaoUtils.paraRecChkNull(msgRecord, "CRANE_NO");					// 북인대상재 구분 'A1' : 북인대상재
			szOperationMode	= ydDaoUtils.paraRecChkNull(msgRecord, "OPERATION_MODE");			// 1:one time 2:Start 3:End
			szOperationBed	= ydDaoUtils.paraRecChkNull(msgRecord, "OPERATION_BED");			// Book in/Book-Out시 야드적치Bed번호
			szOperationDate = ydDaoUtils.paraRecChkNull(msgRecord, "OPERATION_DATE");			// Book out 일시
			szRegister 		= ydDaoUtils.paraRecModifier(msgRecord);							// 등록자, 수정자
			if ("".equals(szRegister)) {
				szRegister = "S1YDL013";
			}
			
			if ("2".equals(szOperationType)) {
				if ("3250".equals(szFromLoc)||
					"3260".equals(szFromLoc)||	
					"3270".equals(szFromLoc)){
					/*
					 * 2015.09.09 윤재광
					 * #2UT설비 북아웃요구 처리 > 제품창고 Y8BOOK-OUT요구(Y8YDL012)로 호출
					 */
					JDTORecord inRecord    = JDTORecordFactory.getInstance().create();
					
					if("3250".equals(szFromLoc)||"3260".equals(szFromLoc)){
						szFromLoc = "TCRTUT02";
					}else if("3270".equals(szFromLoc)){
						szFromLoc = "TCRTUT01";
					}
					inRecord.setField("MSG_ID", 		"Y8YDL012");        // MSG_ID
					inRecord.setField("CURR_LOC", 		szFromLoc);         // 현위치
					inRecord.setField("MTL_SH", 		"01");             	// 재료매수
					inRecord.setField("STL_NO1", 		szPlateId);         // 재료번호
					inRecord.setField("TO_LOC", 		szToLoc);           // To위치
					
					EJBConnector ydEjbCon = new EJBConnector("default", this);
					ydEjbCon.trx("RcptWrkDmdSeEJB", "procY8BookOutReq", inRecord); 
					
					return JPlateYdConst.RETN_CD_SUCCESS; 
				}
			}	
			
			if (!"1".equals(szOperationMode) && !"2".equals(szOperationMode) && !"3".equals(szOperationMode)) {
				szRtnMsg = "수신전문중 OPERATION_MODE ERROR";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}
			if (szFromLoc.equals(szToLoc)) {
				szRtnMsg = "수신전문중 From, To 위치가 동일";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			// 북아웃 원인코드 체크 :: 888 - TEST , 999 - 취소처리
			if ("888".equals(szReasonCode)) {
				szRtnMsg = "북아웃 원인코드가 TEST재로 SKIP 처리함 :: " + szReasonCode;
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}
			// 북아웃원인 코드가 취소일때  (999)
			if ("999".equals(szReasonCode)) {

				szMsg    = "["+szOperationName+"] 북아웃 취소 처리 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				szRtnMsg = this.procS1BookInOutCancel(msgRecord);

				szMsg    = "["+szOperationName+"] 북아웃 취소 처리 END >>>> 결과 :: " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return szRtnMsg;
			}

			//------------------------------------------------------------------------------------------------
			// 1. 파라미터 체크
			//------------------------------------------------------------------------------------------------
			if ("1".equals(szOperationType)) {				// Book In

/* 2013.07.05 북인시 재료번호 필수 아님으로 주석처리
				// PLATE_ID
				if ("".equals(szPlateId)) {
					szRtnMsg = "수신전문중 PLATE_ID ERROR";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
*/

				// From 위치
				if (szFromLoc == null || szFromLoc.length() < 6) {

					// 저장위치 없이 판번호만 들어 왔을때 처리
					if (!"".equals(szPlateId)) {

						szMsg    = "["+szOperationName+"] 저장위치 없이 판번호만 들어 왔을때 .... SKIP";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					} else {
						szRtnMsg = "수신전문중 FROM위치 ERROR >>>> " + szFromLoc;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}

				// 2013.05.14 TO 위치는 RT ZONE NO로 변경
				if (!"".equals(szToLoc) && szToLoc.length() == 4) {
					// ZONE NO를 야드저장위치로 변경 :: 2300 >>>> FART01
					szToLoc = JPlateYdCommonUtils.getY7RtZoneToLoc(szToLoc);
				} else {
					szRtnMsg = "수신전문중 TO위치 ERROR >>>> " + szToLoc;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// To 위치
				if (!"F".equals(ydUtils.substr(szToLoc, 0, 1)) || szToLoc.length() < 6) {
					szRtnMsg = "수신전문중 TO위치 ERROR >>>> " + szToLoc;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

			} else if ("2".equals(szOperationType)) {		// Book Out

				// PLATE_ID
				// BOOK-OUT일때는 2,3의 경우는 미존재함 즉 'PL999999'는 미존재
				if ("".equals(szPlateId) || "PL999999".equals(szPlateId)) {
					szRtnMsg = "수신전문중 PLATE_ID ERROR";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// 2013.04.19 FROM 위치는 RT ZONE NO로 변경
				if (!"".equals(szFromLoc) && szFromLoc.length() == 4) {
					// ZONE NO를 야드저장위치로 변경 :: 2300 >>>> FART01
					szFromLoc = JPlateYdCommonUtils.getY7RtZoneToLoc(szFromLoc);
				}

				// From 위치
				if (szFromLoc == null || szFromLoc.length() < 6) {
					szRtnMsg = "수신전문중 FROM위치 ERROR >>>> " + szFromLoc;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// To 위치
			//	if (!"F".equals(ydUtils.substr(szToLoc, 0, 1)) || szToLoc.length() < 6) {
				if (!"F".equals(ydUtils.substr(szToLoc, 0, 1)) || szToLoc.length() < 4) {
					// L2전문 수신시에만 TO위치 체크 : L3 백업시에는 SKIP
					if ("S1YDL013".equals(szRegister)) {
						szRtnMsg = "수신전문중 TO위치 ERROR >>>> " + szToLoc;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}

				// TO위치의 동과 FROM위치의 동이 상이할 경우 TO위치의 동을 스케줄 기준으로 변경 : 2014.07.08 보완
				String sToBay = ydUtils.substr(szToLoc,   1, 1);
				String sFrBay = ydUtils.substr(szFromLoc, 1, 1);
				if (!sToBay.equals(sFrBay)) {
					szMsg   = "["+szOperationName+"] To위치의 동과 From위치의 동이 상이하여 스케쥴 기준 적용 >>>>" + szToLoc + "," + szFromLoc;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					szToLoc = ydUtils.substr(szFromLoc, 0, 2);
				}

				// Book-Out원인
				if ("".equals(szReasonCode)) {
					szRtnMsg = "수신전문중 Book-Out원인 ERROR";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szMsg;
				}

				// BOOK-OUT시 BED는 '01' 임 (FROM위치가 RT임으로)
				szOperationBed = "01";
			}

			// Book In 시 저장위치 조회
			if ("1".equals(szOperationType)) {

				if ("".equals(szPlateId)) {			// 재료번호 미존재시

	    			szYdStkColGp = szFromLoc;

					szMsg    = "["+szOperationName+"] 북인작업중 .... 재료번호 미존재 >>>> 저장위치 :: " + szYdStkColGp;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				} else {
					// 베드정보 조회
					rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
					inRec    = JDTORecordFactory.getInstance().create();
					inRec.setField("STL_NO", 		szPlateId);             	// 재료번호

	    			intRtnVal = ydStockDao.getYdStockWithLoc(inRec, rsResult);
					if (intRtnVal < 1) {
						szRtnMsg = "야드재료가 미존재 합니다 .... 재료번호:" + szPlateId;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

	    			rsResult.first();
					tempRec = JDTORecordFactory.getInstance().create();
	    			tempRec = rsResult.getRecord();

	    			szYdStkColGp 	= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_COL_GP");
	    			szOperationBed 	= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_BED_NO");
					szYdStkLyrNo	= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_LYR_NO");
				}

				String sToBay = ydUtils.substr(szToLoc, 0, 2);
				if (!ydUtils.substr(szYdStkColGp,0,2).equals(sToBay)) {
					szRtnMsg = "해당동의 재료만 Book-IN 가능! .... 재료위치:: " + szYdStkColGp + ", Book-IN위치:: " + szToLoc;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
			}

			//------------------------------------------------------------------------------------------------
			// 1.2. 해당 재료 작업예약/스케쥴 존재여부 확인
			//------------------------------------------------------------------------------------------------
			if (!"".equals(szPlateId)) {
				// ------------------------------------------------------------------------
				// 1.2.1. 작업예약 존재여부 확인
				// ------------------------------------------------------------------------
				rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 		szPlateId);             	// 재료번호
				recPara.setField("YD_GP",		JPlateYdConst.YD_GP_F_PLATE_YARD);

				intRtnVal = ydWrkbookDao.getExistByStlNo(recPara, rsResult);
				if (intRtnVal > 0) {
					szRtnMsg = "해당 재료["+szPlateId+"]로 작업예약이 존재!";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// ------------------------------------------------------------------------
				// 1.2.2. 크레인 작업지시 존재여부 확인
				// ------------------------------------------------------------------------
				rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 		szPlateId);             	// 재료번호
				recPara.setField("YD_GP",		JPlateYdConst.YD_GP_F_PLATE_YARD);

				intRtnVal = ydCrnSchDao.getExistByStlNo(recPara, rsResult);
				if (intRtnVal > 0) {
					szRtnMsg = "해당 재료"+szPlateId+"로 크레인 작업지시 존재!";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				//------------------------------------------------------------------
				// 1.2.3. 현재 저장위치가 2후판 정정야드가 아닐경우 오류로 처리
				//------------------------------------------------------------------
				szRtnMsg = JPlateYdCommonUtils.checkUpdYdLoc(szPlateId, JPlateYdConst.YD_GP_F_PLATE_YARD, "N");
				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					szMsg = "[ " +szOperationName + "] 북아웃시 저장위치 확인 오류! >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
			}

			//------------------------------------------------------------------------------------------------
			// 2. 야드재료 등록 (BOOK-OUT시)
			//------------------------------------------------------------------------------------------------
			if ("2".equals(szOperationType)) {		// Book Out

				//------------------------------------------------------------------------------------------------
				// 2.1. 야드재료 조회
				//------------------------------------------------------------------------------------------------
				szMsg    = "["+szOperationName+"] ----------- 야드재료 조회 START ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				// 레코드 편성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
				inRec = JDTORecordFactory.getInstance().create();
				inRec.setField("STL_NO", 		szPlateId);             	// 재료번호

    			intRtnVal = ydStockDao.getYdStockWithLoc(inRec, rsResult);
				if (intRtnVal > 0) {

					tempRec = JDTORecordFactory.getInstance().create();

	    			rsResult.first();
	    			tempRec = rsResult.getRecord();
	    			szYdStkColGp = ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_COL_GP");

	    			if ("".equals(szYdStkColGp) || "RT".equals(ydUtils.substr(szYdStkColGp, 2, 2))) {

						szMsg    = "["+szOperationName+"] 해당 재료의 저장위치 :: " + szYdStkColGp + " 계속 진행 ";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    			} else {
						szRtnMsg = "야드재료가 이미 존재합니다 .... 현위치::" + szYdStkColGp;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
	    			}
				}

				//------------------------------------------------------------------------------------------------
				// 2.3. 야드재료 등록
				//------------------------------------------------------------------------------------------------
				szMsg    = "["+szOperationName+"] ----------- 야드재료 등록 START ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				// 레코드 편성
				inRec = JDTORecordFactory.getInstance().create();
				inRec.setField("REGISTER", 			szRegister);							// 등록자
				inRec.setField("STL_NO", 			szPlateId);             				// 재료번호
				inRec.setField("BOOK_OUT_RESN",		szReasonCode);							// Book-Out원인
				inRec.setField("BOOK_OUT_DATE",     JPlateYdUtils.getCurDate("yyyyMMdd"));	// Book-Out일자
				inRec.setField("BOOK_OUT_PROG",  	"");           							// Book-Out공정
				inRec.setField("FRTOMOVE_PLANT_GP",	szCraneNo);           					// 북인대상재 구분 (이송공장구분 항목사용)
				
				
				inRec.setField("YD_FRTOMOVE_YD_GP", szOperationMode);  //북아웃모드 추가(1,2,3 Start, Continuous, End)
				if(szFromTrkZone.length()==4){   //수신받은 트랙킹존 형식일경우만 추가(FBRT01같은 야드 형식이면 INSERT불가(컬럼자리수5)
						inRec.setField("ARR_WLOC_CD", szFromTrkZone);  //트랙킹존 추가 ex:2330
				}
				//REQ202306466285 강길모책임 요청. 북아웃 모드, 트랙킹존 추가
				
    			intRtnVal = ydStockDao.insYdStockBookOut(inRec);
				if (intRtnVal <= 0) {
//					szRtnMsg = "야드재료 등록 ERROR .. " + Integer.toString(intRtnVal);
					szRtnMsg = "재료정보 미존재로 오류 발생 .. " + szPlateId + ", 오류코드::" + Integer.toString(intRtnVal);
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				//------------------------------------------------------------------------------------------------
				// 2.4. 적치단 수정
				//------------------------------------------------------------------------------------------------
				szMsg    = "["+szOperationName+"] ----------- 적치단 야드적치단재료상태 수정 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				//적치단 야드적치단재료상태 수정
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP", 		szFromLoc);                 // 야드적치열구분
				recPara.setField("YD_STK_BED_NO", 		"01");    					// 야드적치BED번호
				recPara.setField("YD_STK_LYR_NO", 		"001");    					// 야드적치단
				recPara.setField("STL_NO", 				szPlateId);             	// 재료번호
				recPara.setField("YD_STK_LYR_MTL_STAT", "C"); 						// 적치완료
				recPara.setField("MODIFIER", 			szRegister);				// 등록자

				intRtnVal = ydStkLyrDao.updYdStklyrStat(recPara);
				if (intRtnVal <= 0) {
					szRtnMsg = "야드적치단재료상태 수정 ERROR .. " + Integer.toString(intRtnVal);
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				//------------------------------------------------------------------------------------------------
				// 2.5. 야드재료 등록후 L2에 저장품제원 정보 송신 .. 작업지시 송신 이전에 전송해야함
				//      야드L2 전문송신 (저장품제원 :: YDY7L002 전송)
				//------------------------------------------------------------------------------------------------
				szMsg = "[ " +szOperationName + "] 야드L2 저장품제원 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				recL2Para = JDTORecordFactory.getInstance().create();
				recL2Para.setField("JMS_TC_CD", 		"YDY7L002");                            // TC-CODE
				recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);		// 야드구분
				recL2Para.setField("YD_STK_COL_GP", 	szFromLoc);                          	// 야드적치열구분
				recL2Para.setField("YD_STK_BED_NO", 	"01");    								// 야드적치BED번호
				recL2Para.setField("YD_INFO_SYNC_CD", 	"4");									// 야드정보동기화코드  [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
				recL2Para.setField("STL_NO", 			szPlateId);	        					// 재료번호
				szRtnMsg = ydDelegate.sendMsg(recL2Para);

				szMsg = "[ " +szOperationName + "] 야드L2 저장품제원 전문송신 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}

			/**********************************************************
			* 3. 스케줄기준 정보 Check
			**********************************************************/
			if ("".equals(szFromLoc)) {
				szYdGp    = ydUtils.substr(szToLoc, 0, 1); 		// 야드구분
				szYdBayGp = ydUtils.substr(szToLoc, 1, 1); 		// 야드동구분
			} else {
				szYdGp    = ydUtils.substr(szFromLoc, 0, 1); 	// 야드구분
				szYdBayGp = ydUtils.substr(szFromLoc, 1, 1); 	// 야드동구분
			}

			//야드스케쥴코드 : 야드+동+RT+마그네틱크레인번호+L(BOOK-OUT)+M(분할없음) : FART0?LM, FBRT0?LM, FCRT0?LM
			//야드스케쥴코드 : 야드+동+RT+마그네틱크레인번호+U(BOOK-IN)+M(분할없음)  : FART0?UM, FBRT0?UM, FCRT0?UM

			if ("1".equals(szOperationType)) {				// Book IN
			//	szYdSchCd   = szYdGp + szYdBayGp + "RT0?UM";
				szYdSchCd	= ydUtils.getRtSchCd(szToLoc, "UM");
			} else if ("2".equals(szOperationType)) {		// Book Out
			//	szYdSchCd   = szYdGp + szYdBayGp + "RT0?LM";
				szYdSchCd	= ydUtils.getRtSchCd(szFromLoc, "LM");
			}

			szMsg    = "[" + szOperationName + "] >>>> FROM위치 >>>> [" + szFromLoc + "]";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg    = "[" + szOperationName + "] >>>> TO위치 >>>> [" + szToLoc + "]";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg    = "[" + szOperationName + "] >>>> S1YDL013 스케쥴코드 >>>> " + szYdSchCd;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szYdSchProhExn  = "";  			// 야드스케쥴금지유무
			szYdWrkCrnPrior = "1"; 			// 야드작업크레인우선순위
			szYdWrkCrn      = "";			// 야드설비ID (NOT NULL이기 때문에 RULE에 등록된 설비ID SET)

			recPara  = JDTORecordFactory.getInstance().create();
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
			recPara.setField("YD_SCH_CD", szYdSchCd); //야드스케쥴코드

			//야드스케쥴금지유무 조회
			ydSchRuleDao.getYdSchrule(recPara, rsResult);

			if (rsResult != null && rsResult.size() > 0) {
				szYdSchProhExn  = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_SCH_PROH_EXN");
				//야드작업크레인우선순위 : 야드스케쥴코드에 해당하는 작업크레인의 우선순위이므로
				//실제 요청한 크레인의 우선순위와 다를 수 있음
				szYdWrkCrnPrior = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_CRN_PRIOR");
				szYdWrkCrn		= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_CRN");
			}

			if ("".equals(szYdSchProhExn)) {
				//스케줄기준 Table 정보 Check
				szRtnMsg = "스케쥴코드[" + szYdSchCd + "] 정보 없음";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			} else if ("Y".equals(szYdSchProhExn)) {
				//스케줄 금지여부 Check
				szRtnMsg = "스케쥴코드[" + szYdSchCd + "] 기동금지";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			//이부분을 else if로 돌리고 위에  if ("1".equals(szOperationType) && "3".equals(szOpmode)) { 추가

			if ("1".equals(szOperationType) && "3".equals(szOperationMode)) {  //BOOK-IN & 후판북아웃모드가 3:End 이면  
				
				EJBConnector 		ejbConn 	= null;
				
				String szYdCrnSchId;
				String szStlNo;
				String szC_YD_WRK_PROG_STAT;
				String szYD_WBOOK_ID;
				String szYD_SCH_CD;
				String szYD_EQP_ID;
				
				JDTORecord recCheck = null;
				JDTORecord outRecord1 = null;
				JDTORecord setRecord 	= JDTORecordFactory.getInstance().create();
				JPlateYdCommDAO  commDao 	= new JPlateYdCommDAO();
				
				String 	sRTN_CD					= "";
				String 	sRTN_MSG				= "";
				
				//해당 열에 잡힌 크레인 작업지시를 조회하여 작업취소 
				JDTORecord recPara2		= JDTORecordFactory.getInstance().create();
				
				recPara2.setField("YD_STK_COL_GP"	, ydUtils.substr(szFromLoc, 0, 6));
				
				//이거 2후판용도 되는지 확인 필요.CHECK  ->안됨 2후판용(F)있는지 찾아보고 없으면 만들자.->없어서 2후판용 만듬 
				JDTORecordSet getRecSet	= commDao.select(recPara2, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdCrnSchIdByLoc2", logId, szMethodName, "해당 열에 잡힌 크레인 작업지시 조회");
				
				if (getRecSet.size() > 0) {
					//크레인 작업지시 취소 처리
					for (int ii=0; ii<getRecSet.size(); ii++) {
						szYdCrnSchId  	= getRecSet.getRecord(ii).getFieldString("YD_CRN_SCH_ID");
						szYD_SCH_CD 		= getRecSet.getRecord(ii).getFieldString("YD_SCH_CD"); 
						szStlNo			= getRecSet.getRecord(ii).getFieldString("STL_NO");
						
						if ("".equals(szYdCrnSchId)) {
							//szMsg = "["+szOperationName+"] 스케줄 취소 처리("+szMethodName+") 실패, YD_CRN_SCH_ID값이 없음";
							//ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							continue;
						}
						
						recPara2.setField("YD_CRN_SCH_ID", szYdCrnSchId);
						recPara2.setField("YD_SCH_CD",     szYD_SCH_CD);
						recPara2.setField("DEL_YN",        "N");
						recPara2.setField("MODIFIER",      szRegister);
						
						/*
						 * 크레인 스케줄에서 선택된 스케줄 ID로 작업예약을 조회한 후
						 * 삭제되지 않는 작업예약에 포함된 크레인 스케줄중 첫번째 값이
						 * 2,3 인 경우 후처리를 하지않기 위해  Check Logic 반영
						 */
						
						rsResult = JDTORecordFactory.getInstance().createRecordSet("temRs");
						//com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getCheckYdCrnSchId
						intRtnVal = ydCrnSchDao.getCheckYdCrnSchId(recPara2, rsResult);		// intGp == 36
						
						if (intRtnVal < 1) {
							szMsg = "["+szOperationName+"] 취소 작업을 완료 하였습니다.";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
							continue;
						}
						
						rsResult.first();
						recCheck = rsResult.getRecord();
						
						szC_YD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recCheck, "YD_WRK_PROG_STAT");
						
						//2,3, 인 경우 와 혹시모를 4이면서도 스케줄 삭제가 되지 않은 경우
						if ("2".equals(szC_YD_WRK_PROG_STAT) || "3".equals(szC_YD_WRK_PROG_STAT) || "4".equals(szC_YD_WRK_PROG_STAT)) {
							szMsg = "["+szOperationName+"] 크레인 작업이 완료되지 않았습니다!!";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
							continue;
						}
						
						/*
						 * 2,3이 아닌 경우 스케줄 취소기능에  첫번째 크레인 스케줄 ID 정보를 전송
						 */
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recCheck, "YD_CRN_SCH_ID"));
						recPara.setField("YD_SCH_CD",     ydDaoUtils.paraRecChkNull(recCheck, "YD_SCH_CD"));
						recPara.setField("DEL_YN",        ydDaoUtils.paraRecChkNull(recCheck, "DEL_YN"));
						recPara.setField("MODIFIER",      szRegister);
						
						//크레인스케줄 ID보다 이상인 ID 삭제  업데이트 실행(적치단정보까지 CLEAR)
						//스케줄취소
						szMsg = "["+szOperationName+"] 작업지시 취소 시작";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						
						//이거 2후판용도 되는지 확인 필요.CHECK
						//JPlateYdJspSeEJBBean.cancelJPlateYdCrnSch 이걸로 바로 대체 가능한지 확인-->대체 가능. 대상 수신받는곳(Y2/Y7)만빼고 똑같음.				
						//ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
						//outRecord1 	= (JDTORecord)ejbConn.trx("cancelJPlateYdCrnSch", new Class[] { JDTORecord.class }, new Object[] { recPara });
						ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
						outRecord1 	= (JDTORecord)ejbConn.trx("cancelJPlateYdCrnSch", new Class[] { JDTORecord.class }, new Object[] { recPara });

						sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
						sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
						
						szMsg = "["+szOperationName+"] ---- 작업지시 취소 종료!! >>>> " + sRTN_CD + " , " + sRTN_MSG;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						
						if ("0".equals(sRTN_CD)) {
							m_ctx.setRollbackOnly();
							szRtnMsg = "작업지시 취소 ERROR .. " + sRTN_CD;
							szMsg    = "["+szOperationName+"] " + sRTN_MSG;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}
						
						szMsg = "["+szOperationName+"] ---- 작업예약 취소 시작";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						
						//이거 2후판용도 되는지 확인 필요.CHECK-->대체 가능. 대상 수신받는곳(Y2/Y7)만빼고 똑같음.
						//JPlateYdJspSeEJBBean.delJPlateWBook
						//ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);						
						//outRecord1 	= (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { outRecord1 });
						ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
						outRecord1 	= (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { outRecord1 });
						
						sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
						sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
						
						szMsg = "["+szOperationName+"] ---- 작업예약 취소 종료!! >>>> " + outRecord1.toString();
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);				
						
						if ("0".equals(sRTN_CD)) {
							m_ctx.setRollbackOnly();
							szRtnMsg = "작업예약 취소 ERROR .. " + sRTN_CD;
							szMsg    = "["+szOperationName+"] " + sRTN_MSG;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}
						
						// F?RT??LM :: RT BOOK-OUT일때 재료정보 , 적치위치 Clear  <--이 부분 1후판 복사해왔는데 2후판에도 적용되야하는지 체크 필요.
						//북인작업에 대한 스케줄 취소처리인데, 북아웃 체크해서 해당 작업의 rt재료 클리어하는 이유 모르겠음. 우선 해당 부분 제외처리하고,
						//추후 문의오면 대응하는 식으로 처리.
						/*if ("RT".equals(ydUtils.substr(szYD_SCH_CD,2,2)) && "LM".equals(ydUtils.substr(szYD_SCH_CD,6,2))) {

							recPara = JDTORecordFactory.getInstance().create();
							recPara.setField("YD_CRN_SCH_ID", szYdCrnSchId);
							recPara.setField("YD_SCH_CD",     szYD_SCH_CD);
							recPara.setField("STL_NO",        szStlNo);
							recPara.setField("MODIFIER",      szRegister);

							szMsg = "["+szOperationName+"] ---- RT BOOK-OUT 재료정보 CLEAR 시작 >>>> " + recPara.toString();
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

							ejbConn 		= new EJBConnector("default", this);
							//2후판용: JPlateYdJspSeEJBBean.delStockLocOnRt
							//String rtnMsg	= (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "delStockLocOnRt", recPara);
							String rtnMsg	= (String)ejbConn.trx("JPlateYdJspSeEJB", "delStockLocOnRt", recPara);
							
							szMsg = "["+szOperationName+"] ---- RT BOOK-OUT 재료정보 CLEAR 종료 >>>> " + rtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						}*/						
					}
					szMsg = "["+szOperationName+"] ---- 정상 취소 처리 완료>>>> " ;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);					
				}
				//해당 열에 잡힌 크레인 작업예약을 조회하여 작업삭제 
				recPara2.setField("YD_STK_COL_GP"	, szFromLoc);
				//이거 2후판용도 되는지 확인 필요.CHECK-->2후판용 새로 만듬 2로.
				//getRecSet	= commDao.select(recPara2, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdWrkBookIdByLoc", logId, szMethodName, "해당 열에 잡힌 크레인 작업예약 조회");
				getRecSet	= commDao.select(recPara2, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdWrkBookIdByLoc2", logId, szMethodName, "해당 열에 잡힌 크레인 작업예약 조회");
				
				if (getRecSet.size() > 0) {
					//크레인 작업예약 삭제 처리
					for (int ii=0; ii<getRecSet.size(); ii++) {
						szYD_WBOOK_ID	= getRecSet.getRecord(ii).getFieldString("YD_WBOOK_ID");
						szYD_SCH_CD 	= getRecSet.getRecord(ii).getFieldString("YD_SCH_CD");
						szYD_EQP_ID 	= getRecSet.getRecord(ii).getFieldString("YD_EQP_ID");	
						
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("MODIFIER",      	szRegister);
						recPara.setField("YD_WBOOK_ID",		szYD_WBOOK_ID);
						recPara.setField("YD_EQP_ID",       szYD_EQP_ID);
						recPara.setField("YD_SCH_CD",     	szYD_SCH_CD);			
						
						szMsg = "["+szOperationName+"] ---- 작업예약 취소 시작";		
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						
						//이거 2후판용도 되는지 확인 필요.CHECK-->대체 가능. 대상 수신받는곳(Y2/Y7)만빼고 똑같음.
						//JPlateYdJspSeEJBBean.delJPlateWBook						
						//ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
						//outRecord1 	= (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { recPara });
						ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
						outRecord1 	= (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { recPara });
						
						sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
						sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
						
						szMsg = "["+szOperationName+"] ---- 작업예약 취소 종료!! >>>> " + outRecord1.toString();
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						
						if ("0".equals(sRTN_CD)) {
							m_ctx.setRollbackOnly();
							szRtnMsg = "작업예약 취소 ERROR .. " + sRTN_CD;
							szMsg    = "["+szOperationName+"] " + sRTN_MSG;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}						
					}
				}
			}
			else if ("1".equals(szOperationType) && "".equals(szPlateId)) { 			// BOOK-IN & 재료번호 미존재시 작업예약재료 등록

				// FROM위치로 재료정보를 조회하여 작업예약 등록
				rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
				inRec    = JDTORecordFactory.getInstance().create();
				inRec.setField("YD_STK_COL_GP", 	ydUtils.substr(szFromLoc, 0, 6));   // 야드적치열
			//	inRec.setField("YD_STK_BED_NO", 	ydUtils.substr(szFromLoc, 6, 2));	// 야드적치베드
				inRec.setField("YD_STK_BED_NO", 	"");								// 연속북인시 베드정보 무시하도록 보완
				if ("2".equals(szOperationMode)) {										// 1:one time 2:Start 3:End
					inRec.setField("ROW_CNT", 		"999");		// FROM위치의 모든 재료
				} else {
					inRec.setField("ROW_CNT", 		"1");		// 1매
				}

				// BOOK-IN 대상재 조회 (적치단 역순으로)
		    	intRtnVal = ydStkLyrDao.getRTBookInMtl(inRec, rsResult);
		    	if (intRtnVal < 1) {
					szRtnMsg = "BOOK-IN 대상재 미존재 .... 저장위치 :: " + szFromLoc;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
		    	}

		    	rsResult.first();
		    	for(int ii=0; ii<rsResult.size(); ii++) {

		    		tempRec = rsResult.getRecord(ii);

					//------------------------------------------------------------------------------------------------
					// 4. 작업예약 등록 [연속 북인시]
					//------------------------------------------------------------------------------------------------
					szYdWbookId = ydWrkbookDao.getSeqId();						//작업예약ID 생성

					szMsg    = "["+szOperationName+"] ----------- 작업예약ID 생성 :: " + ii + " >>>> " + szYdWbookId;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					if ("".equals(szYdWbookId)) {
						szRtnMsg = "작업예약ID 생성 ERROR";
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					szMsg    = "["+szOperationName+"] ----------- 작업예약 등록";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					szYdToLocGuide = szToLoc;

					//작업예약 등록
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID", 		szYdWbookId); 		//야드작업예약ID
					recPara.setField("YD_GP", 				szYdGp); 			//야드구분
					recPara.setField("YD_BAY_GP", 			szYdBayGp); 		//야드동구분
					recPara.setField("YD_SCH_CD", 			szYdSchCd); 		//야드스케쥴코드
					recPara.setField("YD_SCH_PRIOR", 		szYdWrkCrnPrior); 	//야드스케쥴우선순위
					recPara.setField("YD_SCH_PROG_STAT", 	"W"); 				//야드스케쥴진행상태(스케줄수행대기)
					recPara.setField("YD_SCH_ST_GP", 		"M"); 				//야드스케쥴기동구분
					recPara.setField("YD_SCH_REQ_GP", 		"X"); 				//야드스케쥴요청구분
					recPara.setField("YD_AIM_YD_GP", 		szYdGp); 			//야드목표야드구분
					recPara.setField("YD_AIM_BAY_GP", 		szYdBayGp); 		//야드목표동구분
					recPara.setField("YD_TO_LOC_DCSN_MTD",	"A"); 				//야드TO위치결정방법(스케줄기준적용)
					recPara.setField("YD_TO_LOC_GUIDE",		szYdToLocGuide);	//야드To위치Guide
					recPara.setField("REGISTER", 			szRegister);
					recPara.setField("MODIFIER", 			szRegister);

					intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
					if (intRtnVal <= 0) {
						szRtnMsg = "작업예약 등록 ERROR .. " + Integer.toString(intRtnVal);
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					//작업예약재료 등록
					recPara.setField("YD_WBOOK_ID", 	szYdWbookId); 											//야드작업예약ID
					recPara.setField("STL_NO", 			ydDaoUtils.paraRecChkNull(tempRec, "STL_NO")); 			//재료번호
					recPara.setField("YD_STK_COL_GP", 	ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_COL_GP"));	//야드적치열구분
					recPara.setField("YD_STK_BED_NO", 	ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_BED_NO"));	//야드적치베드번호
					recPara.setField("YD_STK_LYR_NO", 	ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_LYR_NO")); 	//야드적치단번호
					recPara.setField("YD_TAKE_OUT_DT", 	szOperationDate);										//BOOK-OUT일시
					recPara.setField("YD_TAKE_OUT_CD", 	szReasonCode);											//BOOK-OUT원인코드

					szRtnMsg = this.insWrkbookMtl(recPara);

			        if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
						szMsg    = "["+szOperationName+"] 작업예약재료 등록 ERROR .. " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
			        }

					if (iWBookInsCnt > 0) {
						sbARR_WBOOK_ID.append(";");
					}
					sbARR_WBOOK_ID.append(szYdWbookId);
					iWBookInsCnt ++;
		    	}

			} else {

				//------------------------------------------------------------------------------------------------
				// 4. 작업예약 등록 [북아웃 또는 1매 북인]
				//------------------------------------------------------------------------------------------------
				szYdWbookId = ydWrkbookDao.getSeqId();						//작업예약ID 생성

				szMsg    = "["+szOperationName+"] ----------- 작업예약ID 생성 :: " + szYdWbookId;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				if ("".equals(szYdWbookId)) {
					szRtnMsg = "작업예약ID 생성 ERROR";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				szMsg    = "["+szOperationName+"] ----------- 작업예약 등록";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				szYdToLocGuide = szToLoc;

				//작업예약 등록
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID", 		szYdWbookId); 		//야드작업예약ID
				recPara.setField("YD_GP", 				szYdGp); 			//야드구분
				recPara.setField("YD_BAY_GP", 			szYdBayGp); 		//야드동구분
				recPara.setField("YD_SCH_CD", 			szYdSchCd); 		//야드스케쥴코드
				recPara.setField("YD_SCH_PRIOR", 		szYdWrkCrnPrior); 	//야드스케쥴우선순위
				recPara.setField("YD_SCH_PROG_STAT", 	"W"); 				//야드스케쥴진행상태(스케줄수행대기)
				recPara.setField("YD_SCH_ST_GP", 		"M"); 				//야드스케쥴기동구분
				recPara.setField("YD_SCH_REQ_GP", 		"X"); 				//야드스케쥴요청구분
				recPara.setField("YD_AIM_YD_GP", 		szYdGp); 			//야드목표야드구분
				recPara.setField("YD_AIM_BAY_GP", 		szYdBayGp); 		//야드목표동구분
				recPara.setField("YD_TO_LOC_DCSN_MTD",	"A"); 				//야드TO위치결정방법(스케줄기준적용)
				recPara.setField("YD_TO_LOC_GUIDE",		szYdToLocGuide);	//야드To위치Guide
				recPara.setField("REGISTER", 			szRegister);
				recPara.setField("MODIFIER", 			szRegister);

				intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
				if (intRtnVal <= 0) {
					szRtnMsg = "작업예약 등록 ERROR .. " + Integer.toString(intRtnVal);
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				//작업예약재료 등록
				recPara.setField("YD_WBOOK_ID", 	szYdWbookId); 		//야드작업예약ID
				recPara.setField("STL_NO", 			szPlateId); 		//재료번호
				recPara.setField("YD_STK_COL_GP", 	szFromLoc);			//야드적치열구분
				recPara.setField("YD_STK_BED_NO", 	szOperationBed);	//야드적치베드번호
				recPara.setField("YD_STK_LYR_NO", 	szYdStkLyrNo); 		//야드적치단번호
				recPara.setField("YD_TAKE_OUT_DT", 	szOperationDate);	//BOOK-OUT일시
				recPara.setField("YD_TAKE_OUT_CD", 	szReasonCode);		//BOOK-OUT원인코드

				szRtnMsg = this.insWrkbookMtl(recPara);

		        if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					szMsg    = "["+szOperationName+"] 작업예약재료 등록 ERROR .. " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
		        }

				if (iWBookInsCnt > 0) {
					sbARR_WBOOK_ID.append(";");
				}
				sbARR_WBOOK_ID.append(szYdWbookId);
				iWBookInsCnt ++;

			}

			//------------------------------------------------------------------------------------------------
			// 연속 BOOK-IN/OUT일때 가이던스 메시지 전송
			//------------------------------------------------------------------------------------------------
			if ("2".equals(szOperationMode) || "3".equals(szOperationMode)) {					// 1:one time 2:Start 3:End

				szMsg = "[" +szOperationName + "] 야드L2 가이던스메시지 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				recL2Para = JDTORecordFactory.getInstance().create();
				recL2Para.setField("JMS_TC_CD", 			"YDY7L006");                            // TC-CODE
				recL2Para.setField("YD_GP", 				JPlateYdConst.YD_GP_F_PLATE_YARD);		// 야드구분
				recL2Para.setField("YD_BAY_GP", 			szFromLoc);                          	// 야드동구분
				recL2Para.setField("OPERATION_TYPE", 		szOperationType);    					// 1:Book In, 2:Book Out
				if ("2".equals(szOperationMode)) {
					recL2Para.setField("OPERATION_MODE", 	"1");									// 1:Start
				} else {
					recL2Para.setField("OPERATION_MODE", 	"2");									// 2:End
				}
				if ("1".equals(szOperationType)) {			// 1:Book-in 일때
					recL2Para.setField("OPERATION_SOURCE", 	szToLoc);	        					// Book-In일때  TO위치
				} else {
					recL2Para.setField("OPERATION_SOURCE", 	szFromLoc);	        					// Book-Out일때 From위치
				}

				szRtnMsg = ydDelegate.sendMsg(recL2Para);

				szMsg = "[ " +szOperationName + "] 야드L2 가이던스메시지 전문송신 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

			iCrnSchCnt  = 0;
			iSchOkCnt	= 0;
			int iCrnSchCntRule = 0;
			
			if (iWBookInsCnt > 0) {

				String[] arrWBookId = sbARR_WBOOK_ID.toString().split(";");

				szMsg = "["+szOperationName+"] ----------- 3.1. BOOK-IN/OUT 스케줄기동 START .... 예약건수 :: " + iWBookInsCnt + " 작업예약ID :: " + sbARR_WBOOK_ID.toString();
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				if ("1".equals(szOperationType)) {// Book In
					iCrnSchCntRule = 4;
				}else{// Book Out
					iCrnSchCntRule = 12;
				}
				
				for(int ii=0; ii<iWBookInsCnt; ii++) {

	    			//-----------------------------------------------------
	        		// 스케쥴 코드로 크레인작업지시를 조회하여 5건 이하시 스케쥴 기동
	    			//-----------------------------------------------------
	        		rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
	        		recPara  = JDTORecordFactory.getInstance().create();
	        		recPara.setField("YD_SCH_CD", 		szYdSchCd);

	        		iCrnSchCnt = ydCrnSchDao.getByYdSchCd(recPara, rsResult);
	    			if (iCrnSchCnt >= iCrnSchCntRule ) {
	        			szMsg = "["+szOperationName+"] ----------- BOOK-IN/OUT 스케줄 기동 SKIP :: " + ii + " .. 스케쥴 건수 .. " + iCrnSchCnt;
	        			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	    				break;
	    			}

					//------------------------------------------------------------------------------------------------
					// 스케쥴 기동 (BOOK-OUT)
					//------------------------------------------------------------------------------------------------
					recSchPara 	= JDTORecordFactory.getInstance().create();
					recSchPara.setField("MSG_ID", 			"YDYDJ");			//TC코드
					recSchPara.setField("YD_EQP_ID", 		szYdWrkCrn);		//크레인설비ID
					recSchPara.setField("YD_SCH_CD",		szYdSchCd);			//크레인스케줄코드
					recSchPara.setField("YD_WBOOK_ID",		arrWBookId[ii]);	//작업예약ID
					recSchPara.setField("REGISTER", 		szRegister);
					recSchPara.setField("MODIFIER", 		szRegister);
					recSchPara.setField("YD_TO_LOC_GUIDE",	szYdToLocGuide);	//야드To위치Guide
					recSchPara.setField("CHK_FROM_LOC",		"N");				//RT작업일경우 권상예약 체크 안하도록 보완

					szMsg    = "["+szOperationName+"] ----------- 3.2.BOOK-IN/OUT 스케줄기동 START :: " + ii + " >>>> " + arrWBookId[ii];
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			        EJBConnector ejbConn = new EJBConnector("default", "JPlateYdCrnSchSeEJB", this);
			        szRtnMsg = (String)ejbConn.trx("procCrnSchMain", new Class[] { JDTORecord.class }, new Object[] { recSchPara });

					szMsg    = "["+szOperationName+"] ----------- 3.3.BOOK-IN/OUT 스케줄기동 END :: " + + ii + " >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			        if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
			        	// 크레인 작업지시 전송후에는 오류발생 데이타 SKIP
			        	if (iSchOkCnt > 0) {
							szMsg    = "["+szOperationName+"] BOOK-IN/OUT 스케줄기동시 오류 발생 .... SKIP >>>> " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			        	} else {
				        	szRtnMsg = "BOOK_IN/OUT 스케줄기동 오류 .. <br>" + szRtnMsg;
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
			        	}
			        } else {
			        	iSchOkCnt ++;
			        }
				}
			}

			szMsg = "["+szOperationName+"] 메소드 끝 >>>> 스케줄 호출 건수 :: " + iSchOkCnt;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }

		return JPlateYdConst.RETN_CD_SUCCESS;
	}

    /**
     * 오퍼레이션명 : Book-In/Book-Out 취소처리
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● Stirng pYdCrnSchId, String pYdWrkProgStat
     * @return ● nRtnVal
     * @throws ● DAOException
     */
	public String  procS1BookInOutCancel(JDTORecord msgRecord) throws DAOException {
		// 레코드선언
		JDTORecordSet 	rsResult  	= null;
		JDTORecord 		inRec 		= null;
		JDTORecord 		outRecord	= JDTORecordFactory.getInstance().create();
		JDTORecord 		tempRec 	= null;
		JDTORecord 		recPara   	= null;

		// 변수 선언
        String 	szMethodName  		= "procS1BookInOutCancel";
        String	szRtnMsg			= JPlateYdConst.RETN_CD_SUCCESS;
    	String 	szMsg         		= "";
    	String 	szOperationName 	= "Book-In/Book-Out 취소처리";

		String 	szPlateId 			= "";			// PLATE_NO
		String 	szOperationType 	= "";			// 1:Book In, 2:Book Out
		String 	szFromLoc			= "";			// From
		String 	szToLoc 			= "";			// To
		String 	szReasonCode		= "";			// Book-Out원인
		String 	szOperationMode		= "";			// 1:one time 2:Start 3:End

		String	szYdStkColGp		= "";
		String	szYdStkBedNo		= "";
        String 	szModifier			= "";			// 등록자, MSG_ID
        String	szYdWrkProgStat		= "";			// 크레인작업진행상태
        String	szYdCrnSchId		= "";			// 크레인작업지시ID

    	int 	intRtnVal 			= 0;

    	//DAO
    	JPlateYdStockDAO    ydStockDao   	= new JPlateYdStockDAO();
    	JPlateYdWrkbookDAO	ydWrkbookDao 	= new JPlateYdWrkbookDAO();
    	JPlateYdCrnSchDAO 	ydCrnSchDao		= new JPlateYdCrnSchDAO();
    	JPlateYdStkLyrDAO   ydStkLyrDao		= new JPlateYdStkLyrDAO();

		EJBConnector 		ejbConn 	= null;

        try {
        	szMsg = "["+szOperationName+"] ---- 메소드 시작  >>>> " + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			/*
			PLATE_ID				PLATE_ID				CHAR	10		Operation_Mode가 '2'또는'3'일때 'PL999999'
			SHIFT_CODE				SHIFT_CODE				CHAR	2		근조
			OPERATION_TYPE			OPERATION_TYPE			CHAR	1		1:Book In, 2:Book Out
			OPERATION_SOURCE		OPERATION_SOURCE		CHAR	8		FROM위치
			OPERATION_DESTINATION	OPERATION_DESTINATION	CHAR	8		TO위치
			REASON_CODE				REASON_CODE				CHAR	3		Book-Out원인 (888:TEST , 999:북아웃취소)
			CRANE_NO				CRANE_NO				CHAR	2		크레인번호
			OPERATION_MODE			OPERATION_MODE			CHAR	1		1:one time 2:Start 3:End
			OPERATION_BED			OPERATION_BED			CHAR	2		Book in/Book-Out시 야드적치Bed번호
			OPERATION_DATE			OPERATION_DATE			CHAR	14		Date & Time
			*/
			szPlateId 		= ydDaoUtils.paraRecChkNull(msgRecord, "PLATE_ID");					// PLATE_NO
			szOperationType = ydDaoUtils.paraRecChkNull(msgRecord, "OPERATION_TYPE");			// 1:Book In, 2:Book Out
			szFromLoc		= ydDaoUtils.paraRecChkNull(msgRecord, "OPERATION_SOURCE");			// From
			szToLoc 		= ydDaoUtils.paraRecChkNull(msgRecord, "OPERATION_DESTINATION");	// To
			if ("".equals(szToLoc)) {
				szToLoc		= ydDaoUtils.paraRecChkNull(msgRecord, "OPERATION_TARGET");			// (OPERATION_TARGET , OPERATION_DESTINATION) I/F Layout에 항목 변경
			}
			szReasonCode	= ydDaoUtils.paraRecChkNull(msgRecord, "REASON_CODE");				// Book-Out원인 (888:TEST , 999:북아웃취소)
			szOperationMode	= ydDaoUtils.paraRecChkNull(msgRecord, "OPERATION_MODE");			// 1:one time 2:Start 3:End
			szModifier 		= ydDaoUtils.paraRecModifier(msgRecord);							// 등록자, 수정자

			if (!"1".equals(szOperationMode) && !"2".equals(szOperationMode)) {
				szRtnMsg = "수신전문중 OPERATION_MODE ERROR";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			//------------------------------------------------------------------------------------------------
			// 1. 파라미터 체크
			//------------------------------------------------------------------------------------------------
			if ("1".equals(szOperationType)) {				// Book In

				// From 위치
				if (szFromLoc == null || szFromLoc.length() < 6) {

					// 저장위치 없이 판번호만 들어 왔을때 처리
					if (!"".equals(szPlateId)) {

						szMsg    = "["+szOperationName+"] 저장위치 없이 판번호만 들어 왔을때 .... SKIP";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					} else {
						szRtnMsg = "수신전문중 FROM위치 ERROR >>>> " + szFromLoc;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}

				// 2013.05.14 TO 위치는 RT ZONE NO로 변경
				if (!"".equals(szToLoc) && szToLoc.length() == 4) {
					// ZONE NO를 야드저장위치로 변경 :: 2300 >>>> FART01
					szToLoc = JPlateYdCommonUtils.getY7RtZoneToLoc(szToLoc);
				} else {
					szRtnMsg = "수신전문중 TO위치 ERROR >>>> " + szToLoc;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// To 위치
				if (!"F".equals(ydUtils.substr(szToLoc, 0, 1)) || szToLoc.length() < 6) {
					szRtnMsg = "수신전문중 TO위치 ERROR >>>> " + szToLoc;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

			} else if ("2".equals(szOperationType)) {		// Book Out

				// PLATE_ID
				// BOOK-OUT일때는 2,3의 경우는 미존재함 즉 'PL999999'는 미존재
				if ("".equals(szPlateId) || "PL999999".equals(szPlateId)) {
					szRtnMsg = "수신전문중 PLATE_ID ERROR";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// 2013.04.19 FROM 위치는 RT ZONE NO로 변경
				if (!"".equals(szFromLoc) && szFromLoc.length() == 4) {
					// ZONE NO를 야드저장위치로 변경 :: 2300 >>>> FART01
					szFromLoc = JPlateYdCommonUtils.getY7RtZoneToLoc(szFromLoc);
				}

				// From 위치
				if (szFromLoc == null || szFromLoc.length() < 6) {
					szRtnMsg = "수신전문중 FROM위치 ERROR >>>> " + szFromLoc;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// TO위치의 동과 FROM위치의 동이 상이할 경우 TO위치의 동을 스케줄 기준으로 변경 : 2014.07.08 보완
				String sToBay = ydUtils.substr(szToLoc,   1, 1);
				String sFrBay = ydUtils.substr(szFromLoc, 1, 1);
				if (!sToBay.equals(sFrBay)) {
					szMsg   = "["+szOperationName+"] To위치의 동과 From위치의 동이 상이하여 스케쥴 기준 적용 >>>>" + szToLoc + "," + szFromLoc;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					szToLoc = ydUtils.substr(szFromLoc, 0, 2);
				}

				// Book-Out원인
				if ("".equals(szReasonCode)) {
					szRtnMsg = "수신전문중 Book-Out원인 ERROR";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szMsg;
				}
			}

			// Book In 시 저장위치 조회
			if ("1".equals(szOperationType)) {

				if ("".equals(szPlateId)) {			// 재료번호 미존재시

	    			szYdStkColGp = szFromLoc;

					szMsg    = "["+szOperationName+"] 북인 취소 작업중 .... 재료번호 미존재 >>>> 저장위치 :: " + szYdStkColGp;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				} else {
					// 베드정보 조회
					rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
					inRec    = JDTORecordFactory.getInstance().create();
					inRec.setField("STL_NO", 		szPlateId);             	// 재료번호

	    			intRtnVal = ydStockDao.getYdStockWithLoc(inRec, rsResult);
					if (intRtnVal < 1) {
						szRtnMsg = "야드재료가 미존재 합니다 .... 재료번호:" + szPlateId;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

	    			rsResult.first();
					tempRec = JDTORecordFactory.getInstance().create();
	    			tempRec = rsResult.getRecord();

	    			szYdStkColGp 	= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_COL_GP");
				}
			}

			//------------------------------------------------------------------------------------------------
			// 2. 해당 재료 작업예약/스케쥴 존재여부 확인
			//------------------------------------------------------------------------------------------------
			if (!"".equals(szPlateId)) {

				// ------------------------------------------------------------------------
				// 2.1. 크레인 작업지시 존재여부 확인
				// ------------------------------------------------------------------------
				rsResult = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
				recPara  = JDTORecordFactory.getInstance().create();
				tempRec  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 		szPlateId);             	// 재료번호
				recPara.setField("YD_GP",		JPlateYdConst.YD_GP_F_PLATE_YARD);

				intRtnVal = ydCrnSchDao.getExistByStlNo(recPara, rsResult);
				if (intRtnVal <= 0) {
					szMsg    = "["+szOperationName+"] 해당 재료"+szPlateId+"로 크레인 작업지시 미존재! .... SKIP";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				} else {
					rsResult.first();
					tempRec = rsResult.getRecord();
					szYdWrkProgStat = ydDaoUtils.paraRecChkNull(tempRec, "YD_WRK_PROG_STAT");
					szYdCrnSchId 	= ydDaoUtils.paraRecChkNull(tempRec, "YD_CRN_SCH_ID");

					if (!"W".equals(szYdWrkProgStat) && !"1".equals(szYdWrkProgStat)) {

						szRtnMsg = "재료번호[" + szPlateId + "]의 크레인작업지시[" + szYdCrnSchId + "]를 취소할수 없는 상태! " + szYdWrkProgStat;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;

					}

					// 작업상태가 2,3이 아닌 경우 크레인 작업지시 취소 처리
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(tempRec, "YD_CRN_SCH_ID"));
					recPara.setField("YD_SCH_CD",     ydDaoUtils.paraRecChkNull(tempRec, "YD_SCH_CD"));
					recPara.setField("DEL_YN",        "N");
					recPara.setField("MODIFIER",      szModifier);

					// 크레인 스케줄취소 처리
					szMsg = "["+szOperationName+"] 작업지시 취소 시작";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					outRecord	= JDTORecordFactory.getInstance().create();
					ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
					outRecord 	= (JDTORecord)ejbConn.trx("cancelJPlateYdCrnSch", new Class[] { JDTORecord.class }, new Object[] { recPara });

					szMsg = "["+szOperationName+"] ---- 작업지시 취소 종료!! >>>> " + outRecord.toString();
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				}

				// ------------------------------------------------------------------------
				// 2.2. 작업예약 존재여부 확인
				// ------------------------------------------------------------------------
				rsResult = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
				recPara  = JDTORecordFactory.getInstance().create();
				tempRec  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 		szPlateId);             	// 재료번호
				recPara.setField("YD_GP",		JPlateYdConst.YD_GP_F_PLATE_YARD);

				intRtnVal = ydWrkbookDao.getExistByStlNo(recPara, rsResult);
				if (intRtnVal <= 0) {
					szMsg    = "["+szOperationName+"] 해당 재료"+szPlateId+"로 작업예약 미존재! .... SKIP";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				} else {
					rsResult.first();
					tempRec = rsResult.getRecord();

					// 작업 예약 삭제 처리
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID",	ydDaoUtils.paraRecChkNull(tempRec, "YD_WBOOK_ID"));
					recPara.setField("MODIFIER",    szModifier);

					szMsg = "["+szOperationName+"] ---- 작업예약 취소 시작";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					outRecord	= JDTORecordFactory.getInstance().create();
					ejbConn 	= new EJBConnector("default", "JPlateYdJspSeEJB", this);
					outRecord 	= (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { recPara });

					szMsg = "["+szOperationName+"] ---- 작업예약 취소 종료!! >>>> " + outRecord.toString();
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				}
			}

			//------------------------------------------------------------------------------------------------
			// 3. 야드재료 삭제 (BOOK-OUT시)
			//------------------------------------------------------------------------------------------------
			if ("2".equals(szOperationType)) {		// Book Out

				// ------------------------------------------------------------------------
				// 3.1. 저장위치 정보 조회
				// ------------------------------------------------------------------------
				szMsg = "[ " +szOperationName + "] 3.1.저장위치 정보 조회 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				rsResult = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
				recPara  = JDTORecordFactory.getInstance().create();
				tempRec  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 		szPlateId);             	// 재료번호
				recPara.setField("YD_GP",		JPlateYdConst.YD_GP_F_PLATE_YARD);

				intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsResult);
				if (intRtnVal <= 0) {
					szRtnMsg = "야드재료가 미존재 합니다 .... 재료번호:" + szPlateId;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

		        szYdStkColGp	= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_COL_GP");
		        szYdStkBedNo	= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_BED_NO");

				szMsg = "[ " +szOperationName + "] 3.1.저장위치 정보 조회 END >>>> " + szYdStkColGp + szYdStkBedNo;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				// ------------------------------------------------------------------------
				// 3.2. 현재위치가 RT인지 체크
				// ------------------------------------------------------------------------
				if (!"RT".equals(ydUtils.substr(szYdStkColGp,2,2))) {
					szRtnMsg = "현재위치가 RT가 아닙니다 .... 저장위치:" + szYdStkColGp + szYdStkBedNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				//	return szRtnMsg;
				}

				// ------------------------------------------------------------------------
				// 3.3. 야드 L2 저장품재원 삭제전문 전송
				// ------------------------------------------------------------------------
				szMsg = "[ " +szOperationName + "] 3.2.야드L2 저장품제원 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("JMS_TC_CD", 			"YDY7L002");                            // TC-CODE
				recPara.setField("YD_GP", 				JPlateYdConst.YD_GP_F_PLATE_YARD);		// 야드구분
				recPara.setField("YD_STK_COL_GP", 		szYdStkColGp);            				// 야드적치열구분
				recPara.setField("YD_STK_BED_NO", 		szYdStkBedNo);    						// 야드적치BED번호
				recPara.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
				recPara.setField("STL_NO", 				szPlateId);	        					// 재료번호
				recPara.setField("MSG_GP", 				"D");	        						// 전문구분
				szRtnMsg = ydDelegate.sendMsg(recPara);

				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				if (!"".equals(szYdStkColGp)) {
				// ------------------------------------------------------------------------
				// 3.4. 조업 L3 저장위치변경정보 전송
				// ------------------------------------------------------------------------
	            szMsg    = "["+szOperationName+"] 3.3.후판조업 저장위치변경정보 전송 ---- START";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	            recPara = JDTORecordFactory.getInstance().create();
		        recPara.setField("MSG_ID", 				"YDPPJ011");
		        recPara.setField("YD_STK_COL_FR", 		szYdStkColGp);							// From적치열
		        recPara.setField("YD_STK_BED_FR", 		szYdStkBedNo);							// From적치BED
		        recPara.setField("YD_STK_COL_TO", 		"");									// TO적치열
		        recPara.setField("YD_STK_BED_TO", 		"");									// TO적치BED
		        recPara.setField("YD_EQP_WRK_SH", 		"1");									// 야드설비작업매수
		        recPara.setField("ARR_STL_NO", 			szPlateId);

		        szRtnMsg = ydDelegate.sendMsg(recPara);

				szMsg = "["+szOperationName+"] 후판조업 저장위치변경정보 전송 완료>>>>" + szRtnMsg;
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				}

				// ------------------------------------------------------------------------
				// 3.5. 재료정보 삭제처리
				// ------------------------------------------------------------------------
				szMsg = "[ " +szOperationName + "] 3.4. 재료정보 삭제처리";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO",				szPlateId);				// 재료번호
				recPara.setField("YD_GP",				JPlateYdConst.YD_GP_F_PLATE_YARD);
				recPara.setField("MODIFIER", 			szModifier);

				intRtnVal = ydStockDao.delYdStock(recPara);
				if (intRtnVal < 0) {
					szRtnMsg = "재료정보 삭제처리시 오류 발생! .... 오류코드 :" + Integer.toString(intRtnVal);
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				//	return szRtnMsg;
				}

				// ------------------------------------------------------------------------
				// 3.6. 저장위치 CLEAR
				// ------------------------------------------------------------------------
				szMsg = "[ " +szOperationName + "] 3.5. 저장위치 CLEAR";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 				szPlateId);             // 재료번호
				recPara.setField("YD_STK_LYR_MTL_STAT", "C");             		// 야드적치단재료상태
				recPara.setField("YD_GP",				JPlateYdConst.YD_GP_F_PLATE_YARD);
				recPara.setField("MODIFIER", 			szModifier);

				intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
				if (intRtnVal < 0) {
					szRtnMsg = "저장위치 삭제 처리시 오류 발생! .... 오류코드 :" + Integer.toString(intRtnVal);
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				//	return szRtnMsg;
				}
	        }

			szMsg = "["+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }

		return JPlateYdConst.RETN_CD_SUCCESS;
	}

    /**
     * 오퍼레이션명 : 작업예약 재료 등록
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● Stirng pYdCrnSchId, String pYdWrkProgStat
     * @return ● nRtnVal
     * @throws ● DAOException
     */
	public String  insWrkbookMtl(JDTORecord recPara) throws DAOException {

        String 	szMethodName  		= "insYdWrkbookMtl";
        String	szRtnMsg			= JPlateYdConst.RETN_CD_SUCCESS;
    	String 	szMsg         		= "";
    	String 	szOperationName 	= "작업예약 재료 등록";

    	int 	intRtnVal 			= 0;

    	//DAO
    	JPlateYdStockDAO      ydStockDao   		= new JPlateYdStockDAO();
    	JPlateYdWrkbookMtlDAO ydWrkbookMtlDao 	= new JPlateYdWrkbookMtlDAO();
//    	JPlateYdStkLyrDAO     ydStkLyrDao		= new JPlateYdStkLyrDAO();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.10 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(recPara, "P");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

        try {

			szMsg    = "[" + szOperationName + "] ----------- 작업예약재료 등록";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			intRtnVal = ydWrkbookMtlDao.insYdWrkbookMtl(recPara);
			if (intRtnVal <= 0) {
				szRtnMsg = "작업예약재료 등록 ERROR .. " + Integer.toString(intRtnVal);
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			szMsg    = "[" + szOperationName + "] ----------- 저장품 작업예약정보 수정 ";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//저장품 작업예약정보 수정 (TB_YD_SHRSTOCK)
			intRtnVal = ydStockDao.updYdStockWbook(recPara);
			if (intRtnVal <= 0) {
				szRtnMsg = "저장품 작업예약정보 수정 ERROR .. " + Integer.toString(intRtnVal);
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

		/* 2013.07.08 작업예약 생성하면서 적치단 수정 안하도록 보완 :: 스케줄 편성시 문제 있음
			szMsg    = "[" + szOperationName + "] ----------- 적치단 야드적치단재료상태 수정 ";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//적치단 야드적치단재료상태 수정
			recPara.setField("YD_STK_LYR_MTL_STAT", "U"); 				//권상대기
			intRtnVal = ydStkLyrDao.updYdStklyrStat(recPara);
			if (intRtnVal <= 0) {
				szRtnMsg = "야드적치단재료상태 수정 ERROR .. " + Integer.toString(intRtnVal);
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}
		*/
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
	    }

		return JPlateYdConst.RETN_CD_SUCCESS;
	}

	//---------------------------------------------------------------------------
	
	
	/**********************************************************
	* 1후판정정추가 SJH16 
	**********************************************************/		
	
	 
	/**
	 * 오퍼레이션명 : 1후판정정 저장위치제원요구 (Y2YDL001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public void procY2StrLocSpecReq(JDTORecord msgRecord) throws DAOException {
		// 파라미터 NULL 체크 후 레코드 데이터
        JDTORecord getParamRecord  	= JDTORecordFactory.getInstance().create();

        // 델리게이트 호출을 위한 편집 레코드 데이터
        JDTORecord setCrnschRecord 	= JDTORecordFactory.getInstance().create();

        JPlateYdDelegate ydDelegate = new JPlateYdDelegate();

    	String szMethodName     = "procY2StrLocSpecReq";
		String szMsg            = "";
		String szOperationName	= "1후판정정야드L2 저장위치제원요구";
		String szTemp           = "";
		String szRcvTcCode      = ydUtils.getTcCode(msgRecord);

		int nRtnVal             = 0;

		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "\n=========Y2YDL001========\n", JPlateYdConst.DEBUG);
		szMsg = "[" + szOperationName + "] 수신데이타 :: " + msgRecord.toString();
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		if (szRcvTcCode == null) {
			szMsg = SZ_SESSION_NAME + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return;
		}

		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		}

		try {
			//=============================================================
			// Log 테이블 등록
			//=============================================================
			szMsg = "[1후판정정] 저장위치제원요구 수신";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			// 파라미터 Check
	        nRtnVal = this.paramY2YDL001Check(msgRecord, getParamRecord);

	        if (nRtnVal == -1) {
                szMsg = "파라미터 Check중 Error : " + Integer.toString(nRtnVal);
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
                return;
	        }
	        
	        if(!ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP").equals("P")) {
				szMsg = "파라미터 Check중 야드구분 Error : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return;
            }
	        
	        // 적치열구분 생성
	        szTemp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP")
	               + ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP")
	        	   + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP")
	        	   + ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO");

	        // 델리게이트 호출을 위한 레코드 편집
	        setCrnschRecord.setField("JMS_TC_CD"      , "YDY2L001");                                        // TC-CODE
	        setCrnschRecord.setField("YD_GP"          , getParamRecord.getFieldString("YD_GP"));            // 야드구분
	        setCrnschRecord.setField("YD_STK_COL_GP"  , szTemp);                                            // 야드적치열구분
	        setCrnschRecord.setField("YD_STK_BED_NO"  , getParamRecord.getFieldString("YD_STK_BED_NO"));    // 야드적치BED번호
	        setCrnschRecord.setField("YD_INFO_SYNC_CD", getParamRecord.getFieldString("YD_INFO_SYNC_CD"));	// 야드정보동기화코드

	        // 델리게이트 호출
	        ydDelegate.sendMsg(setCrnschRecord);
	    }catch(DAOException e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
	    }catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
	    } // end of try~catch
	} // end of procY2StrLocSpecReq

    /**
     * 오퍼레이션명 : 1후판정정야드 저장위치제원요구 파라미터 체크
     *
     * @param  ● msgRecord, outRecord
     * @return ● nRtnVal
     * @throws ● DAOException
     */
	public int paramY2YDL001Check(JDTORecord msgRecord, JDTORecord outRecord) throws DAOException {
		// 파라미터 체크 결과 레코드 생성
		JDTORecord setRecord = JDTORecordFactory.getInstance().create();

		// 변수
        String szMethodName  = "paramY2YDL001Check";
    	String szMsg         = "";

        int nRtnVal          = 1;

        try {
        	// 레코드 값 체크
			setRecord.setField("YD_INFO_SYNC_CD", ydDaoUtils.paraRecChkNull(msgRecord, "YD_INFO_SYNC_CD"));
			setRecord.setField("YD_GP"          , ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP"));
			setRecord.setField("YD_BAY_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP"));
			setRecord.setField("YD_EQP_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP"));
			setRecord.setField("YD_STK_COL_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO"));
			setRecord.setField("YD_STK_BED_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO"));

			// 레퍼런스 레코드인자에 설정
			outRecord.setRecord(setRecord);

			//======================================================================================================
			// LOG 출력 - 그냥 테스트용으로 출력 레코드값체크하는 코드는 제외하고 나중에 삭제
			szMsg = "[1] 야드동기화코드 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_INFO_SYNC_CD");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[2] 야드구분 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_GP");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[3] 야드 동구분 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_BAY_GP");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[4] 야드 설비구분 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_EQP_GP");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[5] 야드 적치열번호 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_COL_NO");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[6] 야드 적치BED번호 : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_BED_NO");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			//======================================================================================================

        }catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        } // end of try~catch

		return nRtnVal;
	} // end of ParamY2YDL001Check


	/**
	 * 오퍼레이션명 : 1후판정정야드 저장품제원요구 (Y2YDL002)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public void procY2StockSpecReq(JDTORecord msgRecord) throws DAOException {
		// 파라미터 NULL 체크 후 레코드 데이터
		JDTORecord getParamRecord  	= JDTORecordFactory.getInstance().create();

		// 델리게이트 호출을 위한 편집 레코드 데이터
		JDTORecord setCrnschRecord 	= JDTORecordFactory.getInstance().create();

		JPlateYdDelegate ydDelegate	= new JPlateYdDelegate();

		String szMethodName        	= "procY2StockSpecReq";
		String szMsg               	= "";
		String szOperationName     	= "1후판정정야드L2 저장품제원요구";
		String szTemp              	= "";
		String szRcvTcCode         	= ydUtils.getTcCode(msgRecord);

		int nRtnVal                	= 0;

		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "\n=========Y2YDL002========\n", JPlateYdConst.DEBUG);
		szMsg = "[" + szOperationName + "] 수신데이타 :: " + msgRecord.toString();
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		if (szRcvTcCode == null) {
			szMsg = SZ_SESSION_NAME + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return;
		}

		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		}

		try {
			//=============================================================
			// Log 테이블 등록
			//=============================================================
			szMsg = "[1후판정정] 저장품제원요구 수신 >>>> ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			// 파라미터 Check
			nRtnVal = this.paramY2YDL002Check(msgRecord, getParamRecord, 0);
			
			if (nRtnVal == -1) {
				szMsg = "파라미터 Check중 Error : " + Integer.toString(nRtnVal);
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return;
			}
            if(!ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP").equals("P")) {
				szMsg = "파라미터 Check중 야드구분 Error : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return;
            }
            
            String szStlNo = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO");
            
            if(!"".equals(szStlNo)){
            	
            	int intRtnVal = 0;
            	JDTORecord recPara        = null;
        		JDTORecordSet rsResult    = null;
        		
        		JPlateYdStockDAO 		ydStockDao		= new JPlateYdStockDAO();
        		
            	rsResult = JDTORecordFactory.getInstance().createRecordSet("rsTemp");
    			recPara  = JDTORecordFactory.getInstance().create();
    			recPara.setField("STL_NO",		szStlNo);			// 재료번호
    			recPara.setField("YD_GP",		"P");			// 야드구분
    			intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsResult);

    			// 재료 정보 존재여부 체크
    			if (intRtnVal < 1) {
    				// 기존 TB_YD_SHRSTOCK에 미존재시 등록처리함
    				recPara = JDTORecordFactory.getInstance().create();
    				recPara.setField("REGISTER",	"PM21");			// 등록자
    				recPara.setField("MODIFIER",	"PM21");			// 수정자
    				recPara.setField("STL_NO", 	szStlNo);             	// 재료번호

        			intRtnVal = ydStockDao.insYdStockBookOut(recPara);
    				if (intRtnVal <= 0) {
    					szMsg = "[ " +szOperationName + "] 야드재료 등록 ERROR .. " + Integer.toString(intRtnVal);
    					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    				}
    			}
            }
         	
			// 적치열구분 생성
			szTemp  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP","P")
					+ ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP","X")
					+ ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP","XX")
					+ ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO","00");

			
			/**************************************************
			 *  D-62, E-11, E-51 영역일 경우 (L2,L3 공용 영역) 
			 *   - L3 해당 영역을 Clear 한다.
			 *   - Y2YDL016 전문 전송
			 **************************************************/
			if("PD0162".equals(szTemp) || "PE0111".equals(szTemp) || "PE0151".equals(szTemp)) {

				JDTORecord recPara		= JDTORecordFactory.getInstance().create();
				String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
				JPlateYdCommDAO  commDao 	= new JPlateYdCommDAO();
				
				//요청한 L2관리영역 모든 단정보 Clear 하기
				recPara.setField("MODIFIER"			, szRcvTcCode);
				recPara.setField("YD_STK_COL_GP"	, szTemp);             		
				recPara.setField("YD_STK_BED_NO"	, "");

				commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updL2AreaClear", logId, szMethodName, "요청한 L2관리영역 모든 단정보 Clear 하기");

				
				setCrnschRecord.setField("MSG_ID", 			"YDY2L009");			// YDY2L009 : L2제원정보요구
				setCrnschRecord.setField("YD_MSG_GP", 		"I");
				setCrnschRecord.setField("YD_STR_LOC", 		szTemp);
				
				ydDelegate.sendMsg(setCrnschRecord);
				
			} else if("TC".equals(szTemp.substring(2,4)) && "T".equals(getParamRecord.getFieldString("YD_INFO_SYNC_CD"))) {
				
				//대차위치에서 최상단재료 저장품 제원요구 했을 때.. 대차이동 처리
				String sNEW_MODULE_EFF_YN = "N";
			
				JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
				
				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A030"); //1후판정정야드 대차 강제권상시 스케줄삭제 및 이동처리 실행 여부
				
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 대차 강제권상시 스케줄삭제 및 이동처리 실행 여부  : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
				
				if(sNEW_MODULE_EFF_YN.equals("Y")) {
					
					// 현재 대차위치가 강제권상요구 위치와 일치하는지 체크해서 일치하지 않을 경우 아래 로직을 수행한다.
					JPlateYdCommDAO  commDao 	= new JPlateYdCommDAO();
					String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
					JDTORecord jrParam		= JDTORecordFactory.getInstance().create();
					JDTORecordSet rsResult  = null;
					
					jrParam.setField("YD_STK_COL_GP", "P_" + ydUtils.substr(szTemp,2,4));    
					rsResult	= commDao.select(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdStatStkColInfo", logId, szMethodName, " 적치열 정보 조회 - 대차위치");

					if(rsResult.size() > 0) {
						if(!rsResult.getRecord(0).getFieldString("TC_TR_INFO").equals(ydUtils.substr(szTemp,0,6))) {
							//강제권상요구한 위치와 현재 대차 위치가 일치하지 않을 경우
							
							// 1) 대차에 권하하는 크레인 스케줄 삭제
							EJBConnector 		ejbConn 	= null;
							
							String szYdCrnSchId;
							String szYdSchCd;
							String szC_YD_WRK_PROG_STAT;
							String szYD_WBOOK_ID;
							String szYD_SCH_CD;
							String szYD_EQP_ID;
							
							JDTORecord recCheck = null;
							JDTORecord outRecord1 = null;
							
							String 	sRTN_CD					= "";
							String 	sRTN_MSG				= "";
							
							String 	ydFrColGp	= "";	//대차 출발 적치열구분
							String 	modifier   	= "Y2YDL010";
							String  szTCAR_NO   = "PX" + ydUtils.substr(szTemp,2,4); 
							
							//JDTORecordSet rsResult  = null;
							int 	intRtnVal 		= 0 ;
							
							JPlateYdCrnSchDAO   ydCrnSchDao = new JPlateYdCrnSchDAO();		// 크레인스케쥴DAO
							
							JDTORecord recPara			= null;
							String 	szRtnMsg         	= "";
							String szCrnSchCmplYn       = "Y";
							
							if("PCTC".equals(ydUtils.substr(szTemp, 0, 4))) {
								//강제권상 위치가 C동 대차 이면
								ydFrColGp = "PDTC" + ydUtils.substr(szTemp, 4, 2); 
								
							} else if("PDTC".equals(ydUtils.substr(szTemp, 0, 4))) {
								//강제권상 위치가 D동 대차 이면
								ydFrColGp = "PCTC" + ydUtils.substr(szTemp, 4, 2);
								
							} else {
								throw new DAOException(getClass().getName() + ":" + sRTN_CD);
							}
							
							
							//해당 열에 잡힌 크레인 작업지시를 조회하여 작업취소 
							JDTORecord recPara2		= JDTORecordFactory.getInstance().create();
							//String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
							//JPlateYdCommDAO  commDao 	= new JPlateYdCommDAO();
							
							recPara2.setField("YD_STK_COL_GP"	, ydFrColGp);    
							
							JDTORecordSet getRecSet	= commDao.select(recPara2, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdCrnSchIdByLocUD", logId, szMethodName, "해당 열에 잡힌 크레인 작업지시 조회");
							
							if (getRecSet.size() > 0) {
								
								//크레인 작업지시 취소 처리
								for (int ii=0; ii<getRecSet.size(); ii++) {
									
									szYdCrnSchId  	= getRecSet.getRecord(ii).getFieldString("YD_CRN_SCH_ID");
									szYdSchCd 		= getRecSet.getRecord(ii).getFieldString("YD_SCH_CD"); 
									szStlNo			= getRecSet.getRecord(ii).getFieldString("STL_NO"); 
									
									if ("".equals(szYdCrnSchId)) {
										//szMsg = "["+szOperationName+"] 스케줄 취소 처리("+szMethodName+") 실패, YD_CRN_SCH_ID값이 없음";
										//ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
										continue;
									}		
									
									recPara2.setField("YD_CRN_SCH_ID", szYdCrnSchId);
									recPara2.setField("YD_SCH_CD",     szYdSchCd);
									recPara2.setField("DEL_YN",        "N");
									recPara2.setField("MODIFIER",      modifier);
									
									/*
									 * 크레인 스케줄에서 선택된 스케줄 ID로 작업예약을 조회한 후
									 * 삭제되지 않는 작업예약에 포함된 크레인 스케줄중 첫번째 값이
									 * 2,3 인 경우 후처리를 하지않기 위해  Check Logic 반영
									 */
									rsResult = JDTORecordFactory.getInstance().createRecordSet("temRs");

									intRtnVal = ydCrnSchDao.getCheckYdCrnSchId(recPara2, rsResult);		// intGp == 36

									if (intRtnVal < 1) {
										szMsg = "["+szOperationName+"] 취소 작업을 완료 하였습니다.";
										ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
										continue;
									}

									rsResult.first();
									recCheck = rsResult.getRecord();

									szC_YD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recCheck, "YD_WRK_PROG_STAT");

									//2,3, 인 경우 와 혹시모를 4이면서도 스케줄 삭제가 되지 않은 경우
									if ("2".equals(szC_YD_WRK_PROG_STAT) || "3".equals(szC_YD_WRK_PROG_STAT) || "4".equals(szC_YD_WRK_PROG_STAT)) {
										szMsg = "["+szOperationName+"] 크레인 작업이 완료되지 않았습니다!!";
										ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
										
										szCrnSchCmplYn = "N";
										break;
									}

									/*
									 * 2,3이 아닌 경우 스케줄 취소기능에  첫번째 크레인 스케줄 ID 정보를 전송
									 */
									recPara = JDTORecordFactory.getInstance().create();
									recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recCheck, "YD_CRN_SCH_ID"));
									recPara.setField("YD_SCH_CD",     ydDaoUtils.paraRecChkNull(recCheck, "YD_SCH_CD"));
									recPara.setField("DEL_YN",        ydDaoUtils.paraRecChkNull(recCheck, "DEL_YN"));
									recPara.setField("MODIFIER",      modifier);

									//크레인스케줄 ID보다 이상인 ID 삭제  업데이트 실행(적치단정보까지 CLEAR)
									//스케줄취소
									szMsg = "["+szOperationName+"] 작업지시 취소 시작";
									ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

									ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
									outRecord1 	= (JDTORecord)ejbConn.trx("cancelJPlateYdCrnSch", new Class[] { JDTORecord.class }, new Object[] { recPara });

									sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
									sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

									szMsg = "["+szOperationName+"] ---- 작업지시 취소 종료!! >>>> " + sRTN_CD + " , " + sRTN_MSG;
									ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
									
									if ("0".equals(sRTN_CD)) {
										m_ctx.setRollbackOnly();
										throw new DAOException(getClass().getName() + ":" + sRTN_CD);
									}

									szMsg = "["+szOperationName+"] ---- 작업예약 취소 시작";
									ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

									ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
									outRecord1 	= (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { outRecord1 });

									sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
									sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
									
									szMsg = "["+szOperationName+"] ---- 작업예약 취소 종료!! >>>> " + outRecord1.toString();
									ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

									if ("0".equals(sRTN_CD)) {
										m_ctx.setRollbackOnly();
										throw new DAOException(getClass().getName() + ":" + sRTN_CD);
									}

									// F?RT??LM :: RT BOOK-OUT일때 재료정보 , 적치위치 Clear
									if ("RT".equals(ydUtils.substr(szYdSchCd,2,2)) && "LM".equals(ydUtils.substr(szYdSchCd,6,2))) {

										recPara = JDTORecordFactory.getInstance().create();
										recPara.setField("YD_CRN_SCH_ID", szYdCrnSchId);
										recPara.setField("YD_SCH_CD",     szYdSchCd);
										recPara.setField("STL_NO",        szStlNo);
										recPara.setField("MODIFIER",      modifier);

										szMsg = "["+szOperationName+"] ---- RT BOOK-OUT 재료정보 CLEAR 시작 >>>> " + recPara.toString();
										ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

										ejbConn 		= new EJBConnector("default", this);
										String rtnMsg	= (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "delStockLocOnRt", recPara);

										szMsg = "["+szOperationName+"] ---- RT BOOK-OUT 재료정보 CLEAR 종료 >>>> " + rtnMsg;
										ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
									}
								}
							}
							
							
							if(!"N".equals(szCrnSchCmplYn)) { //크레인 작업이 완료되지 않은게 없을 경우 수행하지 않는다.
								
								//해당 열에 잡힌 크레인 작업예약을 조회하여 작업삭제 
								recPara2.setField("YD_STK_COL_GP"	, ydFrColGp);    
								
								getRecSet	= commDao.select(recPara2, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdWrkBookIdByLoc", logId, szMethodName, "해당 열에 잡힌 크레인 작업예약 조회");
								
								if (getRecSet.size() > 0) {
									
									//크레인 작업예약 삭제 처리
									for (int ii=0; ii<getRecSet.size(); ii++) {
										szYD_WBOOK_ID	= getRecSet.getRecord(ii).getFieldString("YD_WBOOK_ID");
										szYD_SCH_CD 	= getRecSet.getRecord(ii).getFieldString("YD_SCH_CD");
										szYD_EQP_ID 	= getRecSet.getRecord(ii).getFieldString("YD_EQP_ID");
					
										recPara = JDTORecordFactory.getInstance().create();
										recPara.setField("MODIFIER",      	modifier);
										recPara.setField("YD_WBOOK_ID",		szYD_WBOOK_ID);
										recPara.setField("YD_EQP_ID",       szYD_EQP_ID);
										recPara.setField("YD_SCH_CD",     	szYD_SCH_CD);
					
										
										szMsg = "["+szOperationName+"] ---- 작업예약 취소 시작";
										ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					
										ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
										outRecord1 	= (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { recPara });
					
										sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
										sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
										
										szMsg = "["+szOperationName+"] ---- 작업예약 취소 종료!! >>>> " + outRecord1.toString();
										ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					
										if ("0".equals(sRTN_CD)) {
											m_ctx.setRollbackOnly();
											throw new DAOException(getClass().getName() + ":" + sRTN_CD);
										}
									}
									
								}
								
								// 2) 대차이동 처리
	
								//TO위치 대차 지정 (TB_YD_EQP 현재동변경);
								jrParam.setField("MODIFIER"				, modifier);
								jrParam.setField("YD_CURR_BAY_GP"		, szTemp.substring(1,2) );
								jrParam.setField("YD_EQP_ID"			, szTCAR_NO );
								jrParam.setField("YD_GP"				, "P" );
								commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updTcarCurrBayGp", logId, szMethodName, "대차 현재동 지정 ");
								
								jrParam.setField("YD_EQP_ID", 			szTCAR_NO);
								jrParam.setField("YD_CURR_BAY_GP",		szTemp.substring(1,2));
								jrParam.setField("MODIFIER", 			modifier);
								jrParam.setField("YD_START_LOC",		ydFrColGp);		// 대차 출발 위치
								jrParam.setField("YD_STOP_LOC",			ydUtils.substr(szTemp,0,6));		// 대차 도착 위치
								
						    	//-------------------------------------------------------------
						    	// 대차 도착 저장위치(베드) 활성화 처리
								ejbConn = new EJBConnector("default", "JPlateYdTcarSchSeEJB", this);
								szRtnMsg = (String)ejbConn.trx("enableFromBed", new Class[] { JDTORecord.class }, new Object[] { jrParam });
								
						    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
									
									szRtnMsg = "대차 도착 저장위치(베드) 활성화 처리시 오류 발생 : " + szRtnMsg;
							    	szMsg 	 = "[" + szMethodName + "] " + szRtnMsg;
									ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
									
									m_ctx.setRollbackOnly();
									throw new DAOException(getClass().getName() + ":" + szRtnMsg);
						    	}
								
						    	//-------------------------------------------------------------
						    	// 대차 출발 저장위치(베드) 비활성화 처리
								szRtnMsg = (String)ejbConn.trx("disableToBed", new Class[] { JDTORecord.class }, new Object[] { jrParam });
								
						    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
									
									szRtnMsg = "대차 출발 저장위치(베드) 비활성화 처리시 오류 발생 : " + szRtnMsg;
							    	szMsg 	 = "[" + szMethodName + "] " + szRtnMsg;
									ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
									
									m_ctx.setRollbackOnly();
									throw new DAOException(getClass().getName() + ":" + szRtnMsg);
						    	}			
								
						    	//-------------------------------------------------------------
						    	// 야드  L2 Interface 처리
						    	//-------------------------------------------------------------
								JDTORecord recL2Para = JDTORecordFactory.getInstance().create();;
						    	
						    	// - 저장위치 제원정보 야드L2전송 (FROM:비활성화)
								recL2Para.setField("MSG_ID", 			"YDY2L001");
								recL2Para.setField("YD_INFO_SYNC_CD", "3");  // 1:동, 2:SPAN, 3:열, 4:BED
								recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);
								recL2Para.setField("YD_STK_COL_GP", 	ydFrColGp);
								recL2Para.setField("YD_STK_BED_NO", 	"");
								szRtnMsg = ydDelegate.sendMsg(recL2Para);
								
						    	// - 저장위치 제원정보 야드L2전송 (TO:활성화)
								recL2Para.setField("MSG_ID", 			"YDY2L001");
								recL2Para.setField("YD_INFO_SYNC_CD", "3");  // 1:동, 2:SPAN, 3:열, 4:BED
								recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);
								recL2Para.setField("YD_STK_COL_GP", 	ydUtils.substr(szTemp,0,6));
								recL2Para.setField("YD_STK_BED_NO", 	"");
								szRtnMsg = ydDelegate.sendMsg(recL2Para);
							}
							
						}
					}
				} 

				// 델리게이트 호출을 위한 레코드 편집
				setCrnschRecord.setField("JMS_TC_CD"      , "YDY2L002");                                        // TC-CODE
				setCrnschRecord.setField("YD_GP"          , getParamRecord.getFieldString("YD_GP"));            // 야드구분
				setCrnschRecord.setField("YD_STK_COL_GP"  , szTemp);                                            // 야드적치열구분
				setCrnschRecord.setField("YD_STK_BED_NO"  , getParamRecord.getFieldString("YD_STK_BED_NO"));    // 야드적치BED번호
				setCrnschRecord.setField("YD_INFO_SYNC_CD", getParamRecord.getFieldString("YD_INFO_SYNC_CD"));	// 야드정보동기화코드
				setCrnschRecord.setField("STL_NO"         , getParamRecord.getFieldString("STL_NO"));	        // 재료번호
	
				// 델리게이트 호출
				ydDelegate.sendMsg(setCrnschRecord);
					
				
			} else {
			
				// 델리게이트 호출을 위한 레코드 편집
				setCrnschRecord.setField("JMS_TC_CD"      , "YDY2L002");                                        // TC-CODE
				setCrnschRecord.setField("YD_GP"          , getParamRecord.getFieldString("YD_GP"));            // 야드구분
				setCrnschRecord.setField("YD_STK_COL_GP"  , szTemp);                                            // 야드적치열구분
				setCrnschRecord.setField("YD_STK_BED_NO"  , getParamRecord.getFieldString("YD_STK_BED_NO"));    // 야드적치BED번호
				setCrnschRecord.setField("YD_INFO_SYNC_CD", getParamRecord.getFieldString("YD_INFO_SYNC_CD"));	// 야드정보동기화코드
				setCrnschRecord.setField("STL_NO"         , getParamRecord.getFieldString("STL_NO"));	        // 재료번호
	
				// 델리게이트 호출
				ydDelegate.sendMsg(setCrnschRecord);
			}
			
			
			
		}catch(DAOException e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        } // end of try~catch
	} // end of procY2StockSpecReq


	/**
	 * 오퍼레이션명 : 1후판정정야드 저장품제원요구 파라미터 체크
	 *
	 * @param  ● msgRecord, outRecord, intGp
	 * @return ● nRtnVal
	 * @throws ● DAOException
	 */
	public int paramY2YDL002Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws DAOException {
		// 파라미터 체크 결과 레코드 생성
		JDTORecord setRecord = JDTORecordFactory.getInstance().create();

		// 변수
		String szMethodName  = "paramY2YDL002Check";
		String szMsg         = "";

		int nRtnVal          = 1;

		try {
			// 레코드 값 체크
			setRecord.setField("YD_INFO_SYNC_CD", ydDaoUtils.paraRecChkNull(msgRecord, "YD_INFO_SYNC_CD"));
			setRecord.setField("YD_GP"          , ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP"));
			setRecord.setField("YD_BAY_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP"));
			setRecord.setField("YD_EQP_GP"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP"));
			setRecord.setField("YD_STK_COL_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO"));
			setRecord.setField("YD_STK_BED_NO"  , ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO"));
			setRecord.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"));

			// 레퍼런스 레코드인자에 설정
			outRecord.setRecord(setRecord);

			//======================================================================================================
			// LOG 출력  - 그냥 테스트용으로 출력 나중에 삭제
			szMsg = "[1] 정보동기화코드  : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_INFO_SYNC_CD");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[2] 야드구분  : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_GP");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[3] 야드동구분  : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_BAY_GP");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[4] 야드설비구분  : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_EQP_GP");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[5] 야드적치열번호  : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_COL_NO");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[6] 야드적치Bed번호  : " + ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_BED_NO");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			//======================================================================================================

		}catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} // end of try~catch

		return nRtnVal;
	} // end of ParamY2YDL002Check

	/**
	 * 오퍼레이션명 : 1후판정정야드 설비운전모드전환 (Y2YDL003)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public String procY2EqpDrvModeChg(JDTORecord msgRecord) throws DAOException {

		// 레코드 선언
		JDTORecord recPara         	= null;
        JDTORecord setCrnschRecord 	= null;

		// DAO 및 UTIL 객체 생성
		JPlateYdDelegate ydDelegate	= new JPlateYdDelegate();
		EJBConnector ejbConn       	= null;

		// 변수 선언
    	String 	szMethodName        = "procY2EqpDrvModeChg";
		String 	szMsg               = "";
//		String 	szOperationName     = "2후판정정야드 L2 설비운전모드전환";
		String 	szYdEqpId		   	= "";
		String 	szYdEqpWrkMode   	= "";
		String	szModifier			= "";
		int 	nRet                = 0;

		String	szRcvTcCode 		= ydUtils.getTcCode(msgRecord);

		if (szRcvTcCode == null) {
			szMsg = SZ_SESSION_NAME + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szMsg;
		}

		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		}

		try {
			//=============================================================
			// Log 테이블 등록
			//=============================================================
			szMsg = "[1후판정정] 설비운전모드전환 수신 >>>> " + msgRecord.toString();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		//	ydUtils.putLogMsg("A", "", szMsg, "", "", "", "I", "A", "I", szRcvTcCode, SZ_SESSION_NAME, szMethodName);

	        setCrnschRecord = JDTORecordFactory.getInstance().create();

	        // 설비테이블에 야드설비작업Mode 업데이트
	        szYdEqpId      	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	        szYdEqpWrkMode 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_MODE");
	        szModifier		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");
	        if ("".equals(szModifier)) {
		        szModifier	= ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER", szRcvTcCode);
	        }

	        setCrnschRecord.setField("YD_EQP_ID", 		szYdEqpId);         // 설비ID
	        setCrnschRecord.setField("YD_EQP_WRK_MODE", szYdEqpWrkMode);   // 1: On-Line, 2: Off-Line
	        setCrnschRecord.setField("MODIFIER", 		szRcvTcCode);

	        nRet = this.updEqpDrvMode(setCrnschRecord);

	        if (nRet == -1) {
                szMsg = "설비테이블 업데이트 중  Error : (YD_EQP_ID : " + szYdEqpId + "), (YD_EQP_WRK_MODE : " + szYdEqpWrkMode + "), Ret : " + Integer.toString(nRet);
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
                return szMsg;
	        }

        	//------------------------------------------------------------------------------------------------
	        // YD_EQP_WRK_MODE (1: On-Line, 2: Off-Line)에 따른 업무 정의
	        // 1: On-Line  - 크레인 리스케줄 호출[복구], 2후판정정야드 크레인 작업지시 호출[YD_EQP_ID, YD_WRK_PROG_STAT(W)]
	        // 2: Off-Line - 크레인 리스케줄 호출[고장]
            //------------------------------------------------------------------------------------------------
	        if ("1".equals(szYdEqpWrkMode)) {			// 1: On-Line

	        	recPara = JDTORecordFactory.getInstance().create();

	        	//크레인 리스케줄 호출[복구]
	        	recPara.setField("MSG_ID", 		"YDYDJ751");    //??
	        	recPara.setField("YD_EQP_ID", 	szYdEqpId);

	        	//------------------------------------------------------------------------------------------------
	        	// EJB 호출로 변경
	        	//------------------------------------------------------------------------------------------------
	        	// ydDelegate.sendMsg(recPara);
	        	ejbConn = new EJBConnector("default", this);
	        	ejbConn.trx("JPlateYdCrnReSchSeEJB", "procY2CrnReSch", recPara);
	        	//------------------------------------------------------------------------------------------------

	        	szMsg = "1후판정정 설비운전모드전환]2후판정정야드 크레인 리스케줄 호출[복구]";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        } else if ("2".equals(szYdEqpWrkMode)) {		// 2: Off-Line

	        	//크레인 리스케줄 호출[고장]
	        	recPara = JDTORecordFactory.getInstance().create();
	        	recPara.setField("MSG_ID",   	"YDYDJ751");     
	        	recPara.setField("YD_EQP_ID", 	szYdEqpId);

	        	//------------------------------------------------------------------------------------------------
	        	// EJB 호출로 변경
	        	//------------------------------------------------------------------------------------------------
	        	// ydDelegate.sendMsg(recPara);
	        	ejbConn = new EJBConnector("default", this);
	        	ejbConn.trx("JPlateYdCrnReSchSeEJB", "procY2CrnReSch", recPara);
	        	//------------------------------------------------------------------------------------------------

	        	szMsg = "[1후판정정 설비운전모드전환]2후판정정야드 크레인 리스케줄 호출[고장]";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	        } else {	// 정의되지 않은 값
	        	szMsg = "[1후판정정 설비운전모드전환]야드설비작업Mode[" + szYdEqpWrkMode + "]가 정의되지 않은 값입니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szMsg;
	        }

	        //------------------------------------------------------------------
	        // 	업무 : 크레인작업실적응답 전문 전송(YDY2L005)
	        //------------------------------------------------------------------
	        recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("MSG_ID", 			"YDY2L005");
			recPara.setField("YD_EQP_ID", 		szYdEqpId);
			recPara.setField("YD_L2_WR_GP", 	"M");				//U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경
			recPara.setField("YD_L3_HD_RS_CD", 	"0000");			//야드L3처리결과코드
			ydDelegate.sendMsg(recPara);

			return JPlateYdConst.RETN_CD_SUCCESS;

		} catch(DAOException e) {
            //szMsg = "JDTOError : " + e.getLocalizedMessage();
            //ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
            //return;
			throw new DAOException(getClass().getName() + e.getMessage(),e);
	    } catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
	    }
	} // end of procY2EqpDrvModeChg()


	/**
	 * 오퍼레이션명 : 1후판정정 설비고장복구실적 수신처리 (Y2YDL004)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return String
	 * @throws DAOException
	 */
	public String procY2EqpTrblRcvrWr(JDTORecord msgRecord) throws DAOException {

		// 레코드 선언
		JDTORecord recPara         = null;
        JDTORecord setCrnschRecord = null;

		// DAO 및 UTIL 객체 생성
		JPlateYdDelegate ydDelegate = new JPlateYdDelegate();
		EJBConnector ejbConn        = null;

		// 변수선언
    	String 	szMethodName		= "procY2EqpTrblRcvrWr";
		String 	szMsg               = "";
//		String 	szOperationName     = "1후판정정 L2 설비고장복구실적";
		String 	szYdEqpId           = "";
		String 	szYdEqpStat         = "";
		String 	szYdEqpPauseCode    = "";
		String 	szYdEqpTrblRcvrDt	= "";
		String 	szYdEqpStatUpd      = "";
		String	szModifier			= "";
		int 	nRet                = 0;

		String	szRcvTcCode 		= ydUtils.getTcCode(msgRecord);
		if (szRcvTcCode == null) {
			szMsg = SZ_SESSION_NAME + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szMsg;
		}

		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		}

		try {
			//=============================================================
			// Log 테이블 등록
			//=============================================================
			szMsg = "[1후판정정] 설비고장복구실적 수신 >>>> " + msgRecord.toString();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		//	ydUtils.putLogMsg("A", "", szMsg, "", "", "", "I", "A", "I", szRcvTcCode, SZ_SESSION_NAME, szMethodName);

	        setCrnschRecord = JDTORecordFactory.getInstance().create();

	        szYdEqpId  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	        szModifier = ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");
	        if ("".equals(szModifier)) {
		        szModifier = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER", szRcvTcCode);
	        }

	        if ("".equals(szYdEqpId)) {
	            szMsg = "설비ID가 존재하지 않습니다.";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	        	return szMsg;
	        }

	        szYdEqpStat = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_STAT");
	        if ("".equals(szYdEqpStat)) {
	            szMsg = "설비상태가  존재하지 않습니다.";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	        	return szMsg;
	        }

	        szYdEqpPauseCode = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
	        if ("".equals(szYdEqpPauseCode)) {
	            szMsg = "휴지코드가  존재하지 않습니다.";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	        	return szMsg;
	        }

	        szYdEqpTrblRcvrDt = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_TRBL_RCVR_DT");
	        if ("".equals(szYdEqpTrblRcvrDt)) {
	            szMsg = "야드설비고장복구일시가  존재하지 않습니다.";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	        	return szMsg;
	        }

			//============================================================================
			// 변환...
			//============================================================================
			if (JPlateYdConst.YD_EQP_STAT_BREAK.equals(szYdEqpStat)) {
				// 고장
				szYdEqpStatUpd = JPlateYdConst.YD_EQP_STAT_BREAK;
				if ("0000".equals(szYdEqpPauseCode) || "".equals(szYdEqpPauseCode)) {
					szYdEqpPauseCode = "B000";
				} else {
					szYdEqpPauseCode = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
				}

			} else if ("R".equals(szYdEqpStat) || JPlateYdConst.YD_EQP_STAT_NORM.equals(szYdEqpStat)) {
				// 복구
				szYdEqpStatUpd = JPlateYdConst.YD_EQP_STAT_NORM;
				if ("0000".equals(szYdEqpPauseCode) || "".equals(szYdEqpPauseCode)) {
					szYdEqpPauseCode = "R000";
				} else {
					szYdEqpPauseCode = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_PAUSE_CODE");
				}
			}

			//============================================================================
	        // 휴지테이블 업데이트
			// 고장이나 복구일 때 남김
			//============================================================================
			if (JPlateYdConst.YD_EQP_STAT_BREAK.equals(szYdEqpStatUpd) || JPlateYdConst.YD_EQP_STAT_NORM.equals(szYdEqpStatUpd)) {
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMethodName + "::    [2] 설비 휴지테이블에 야드 설비휴지코드 업데이트 처리", JPlateYdConst.DEBUG);
		        this.procEqpPause(szRcvTcCode, szYdEqpId, szYdEqpStatUpd, szYdEqpPauseCode, szYdEqpTrblRcvrDt);
			}

			//============================================================================
			// 크레인 설비인 경우 설비 작업 상태 및 스케줄 변경
			//============================================================================
			String lzRtnMsg = null;

			if (JPlateYdConst.YD_EQP_GP_CRANE.equals(szYdEqpId.substring(2,4))) {

				// 고장 UPDATE 시
				if (JPlateYdConst.YD_EQP_STAT_BREAK.equals(szYdEqpStatUpd)) {
					// 해당 설비 스케줄이 권상지시(YD_EQP_STAT_UP_WO) 일경우 IDLE 상태로 변경 YD_EQP_STAT_IDLE

					lzRtnMsg = this.updCrnWrkProgStatUpWoToIdle(szYdEqpId);

					if (JPlateYdConst.RETN_CD_SUCCESS.equals(lzRtnMsg)) {
						szMsg = "스케줄 변경 성공 하였습니다.";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					} else if (JPlateYdConst.RETN_CD_FAILURE.equals(lzRtnMsg)) {
						szMsg = "스케줄 변경 실패 하였습니다.";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					}
				} else {
					// 정상 UPDATE 시
					szYdEqpStatUpd = JPlateYdConst.YD_EQP_STAT_IDLE;
				}
			}
			//============================================================================
	        // 설비테이블에 야드설비상태 업데이트
			//============================================================================
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMethodName + "::    [3] 설비 테이블에 업데이트 처리", JPlateYdConst.DEBUG);
	        setCrnschRecord.setField("YD_EQP_ID"  , szYdEqpId);       	// 설비ID
	        setCrnschRecord.setField("YD_EQP_STAT", szYdEqpStatUpd); 	// "B": 고장, "W": 대기
	        setCrnschRecord.setField("MODIFIER"	  , szRcvTcCode);
	        nRet = this.updYdEqpStat(setCrnschRecord);
	        if (nRet == -1) {
                szMsg = "설비테이블 업데이트 중  Error : (YD_EQP_ID : " + szYdEqpId + "),(YD_EQP_STAT : " + szYdEqpStat + "), Ret : " + Integer.toString(nRet);
                return szMsg;
	        }

			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * YD_EQP_STAT (R: 복구, B: 고장)에 따른 업무 정의
	         * R: 복구 - 크레인 리스케줄링 호출[복구], 1후판정정 크레인 작업지시 호출[YD_EQP_ID, YD_WRK_PROG_STAT(W)]
	         * B: 고장 - 크레인 리스케줄링 호출[고장]
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	        if ("R".equals(szYdEqpStat) || JPlateYdConst.YD_EQP_STAT_NORM.equals(szYdEqpStat)) {			// R: 복구
	        	recPara = JDTORecordFactory.getInstance().create();

	        	//크레인 리스케줄링 호출[복구]
	        	recPara.setField("MSG_ID",      "YDYDJ751");												//??
	        	recPara.setField("YD_EQP_ID", 	szYdEqpId);

	        	ejbConn = new EJBConnector("default", this);
			 	ejbConn.trx("JPlateYdCrnReSchSeEJB", "procY2CrnReSch", recPara);

	        	szMsg = "[1후판정정 설비고장복구실적]크레인 리스케줄링 호출[복구]";
	        	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	szMsg = "[1후판정정 설비고장복구실적]1후판정정 크레인 작업지시 호출";
	        	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        } else if (JPlateYdConst.YD_EQP_STAT_BREAK.equals(szYdEqpStat)) {		//B: 고장
	        	//크레인 리스케줄링 호출[고장]
	        	recPara = JDTORecordFactory.getInstance().create();
	        	recPara.setField("MSG_ID",   	"YDYDJ751");									//??
	        	recPara.setField("YD_EQP_ID", 	szYdEqpId);

	        	ejbConn = new EJBConnector("default", this);
			 	ejbConn.trx("JPlateYdCrnReSchSeEJB", "procY2CrnReSch", recPara);

	        	szMsg = "[1후판정정 설비고장복구실적]크레인 리스케줄링 호출[고장]";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        } else {											// 정의되지 않은 값
	        	szMsg = "[1후판정정 설비고장복구실적]야드설비상태[" + szYdEqpStat + "]가 정의되지 않은 값입니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szMsg;
	        }

	        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 업무 : 크레인작업실적응답 전문 전송(YDY2L005)
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	        recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("MSG_ID"        , "YDY2L005");
			recPara.setField("YD_EQP_ID"     , szYdEqpId);
			recPara.setField("YD_L2_WR_GP"   , "R");			//U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경
			recPara.setField("YD_L3_HD_RS_CD", "0000");			//야드L3처리결과코드
			ydDelegate.sendMsg(recPara);
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

			return JPlateYdConst.RETN_CD_SUCCESS;

	    } catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
	    }
	}


 

    /**
     * 오퍼레이션명 : 1후판정정 크레인 작업지시요구 (Y2YDL007)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
     */
    public String procY2CrnWrkOrdReq(JDTORecord msgRecord)throws DAOException  {

    	JPlateYdDelegate   	ydDelegate   = new JPlateYdDelegate();

    	JDTORecord recCrnSch 		= JDTORecordFactory.getInstance().create();
    	JDTORecord recInPara 		= null;

        JDTORecordSet rsCrnSch 		= JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecordSet rsResult 		= JDTORecordFactory.getInstance().createRecordSet("temp");
		JDTORecordSet rsCrnInfo 	= null;

        int 	intRtnVal 			= 0;

        String 	szMsg              	= "";
        String 	szMethodName       	= "procY2CrnWrkOrdReq";
        String 	szOperationName		= "1후판정정 크레인작업지시 요구";

        String 	szEqpId             = "";
        String 	szYdWrkProgStat		= "";
        String	szModifier			= "";

        //스케쥴코드
        String 	szYdSchCd			= "";

        boolean blnRtnVal			= true;

        String 	szRtnMsg			= "";
        String 	szRcvTcCode			= ydUtils.getTcCode(msgRecord);

        if (szRcvTcCode==null || "".equals(szRcvTcCode)) {
        	szMsg = "[ERROR] "+SZ_SESSION_NAME+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
        	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
        	return szMsg;
        }

        try {
        	//=============================================================
        	// Log 테이블 등록
        	//=============================================================
        	szMsg = "[1후판정정] 크레인작업지시 수신";
       // 	ydUtils.putLogMsg("A", "", szMsg, "", "", "", "I", "A", "I", szRcvTcCode, SZ_SESSION_NAME, szMethodName);
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

        	szEqpId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
        	szModifier 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");
        	if ("".equals(szModifier)) {
            	szModifier 	= ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER", szRcvTcCode);
        	}

        	//------------------------------------------------------------------------------------------------------
        	// 야드설비상태 Check		수신받은  야드설비Id로 설비Table를 조회하여 야드설비상태를 Check하고 고장이면 return
        	//------------------------------------------------------------------------------------------------------
        	szMsg = "["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			rsCrnInfo = JDTORecordFactory.getInstance().createRecordSet("");
        	szRtnMsg = this.eqpStatCheck(szEqpId, rsCrnInfo);

        	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
        		szRtnMsg = "설비 상태 체크 시 오류발생 - 메세지 : " + szRtnMsg;
        		szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

        		this.sendErrCrnWrkOrdReqYdP(msgRecord, szRtnMsg);
        		return szRtnMsg;
        	}

        	szMsg = "["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 완료 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

        	//------------------------------------------------------------------------------------------------------
			// 스케줄 기준 체크 -- 현재 1후판정정야드는 스케줄코드를 SET하지 않고 작업지시를 요청한다.
        	//------------------------------------------------------------------------------------------------------
			szYdSchCd 	 = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if (!"".equals(szYdSchCd)) {
				blnRtnVal = JPlateYdCommonUtils.chkGetSchRule(szYdSchCd, rsResult);
				if (!blnRtnVal) {
					szRtnMsg = "스케줄 기준 체크 조회시 오류 발생!";
	        		szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

	        		this.sendErrCrnWrkOrdReqYdP(msgRecord, szRtnMsg);
					return szRtnMsg;
				}
				// 레코드 추출
				rsResult.first();
				JDTORecord recPara = rsResult.getRecord();
				// 스케줄 금지 유무
				String szYdSchProhExn = ydDaoUtils.paraRecChkNull(recPara,"YD_SCH_PROH_EXN");

				// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
				if ("Y".equals(szYdSchProhExn)) {
					szRtnMsg = "스케쥴코드(" + szYdSchCd + ")가 기동 금지 입니다";
	        		szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

	        		this.sendErrCrnWrkOrdReqYdP(msgRecord, szRtnMsg);
					return szRtnMsg;
				}
			}

        	//야드 작업 진행상태를 check한다.
			rsCrnSch  = JDTORecordFactory.getInstance().createRecordSet("temp");
    		intRtnVal = this.chkWrkProgStat(msgRecord, rsCrnSch);

    		if (intRtnVal == 0) {

        		szRtnMsg = "명령선택된 작업지시가 없음";
        		szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

        		this.sendErrCrnWrkOrdReqYdP(msgRecord, "");
				return szRtnMsg;

    		} else if (intRtnVal == -1) {

				szRtnMsg = "스케줄 기준 체크 조회시 오류 .. 오류코드 : " + Integer.toString(intRtnVal);
        		szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

        		this.sendErrCrnWrkOrdReqYdP(msgRecord, szRtnMsg);
				return szRtnMsg;
    		}

        	recCrnSch = JDTORecordFactory.getInstance().create();
        	recCrnSch.setRecord(rsCrnSch.getRecord(0));

        	szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WRK_PROG_STAT");

        	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 * 업무기준 : 1후판정정야드와 통합야드가 같은 로직을 사용하므로 1후판정정야드와 관련된 L2로만 전송 필요
			 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        	szMsg = "[크레인 작업지시] 작업지시 전문 전송 START >>>>";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

        	recInPara = JDTORecordFactory.getInstance().create();
    		//작업지시 전문 전송 data setup
			//recInPara.setField("MSG_ID", 			"YDY2L004");
        	recInPara.setField("MSG_ID", 			"YDY2L004V2");
        	recInPara.setField("YD_CRN_SCH_ID", 	ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID"));
       		recInPara.setField("YD_WRK_PROG_STAT", 	szYdWrkProgStat);
        	recInPara.setField("YD_WORD_DT",    	JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));
        	recInPara.setField("MODIFIER", 			"YDSYSTEM");
        	recInPara.setField("MSG_GP", 			"U");
        	recInPara.setField("YD_SCH_CD", 		ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD"));
        	recInPara.setField("YD_GP", 			ydDaoUtils.paraRecChkNull(recCrnSch, "YD_GP"));

			szRtnMsg = ydDelegate.sendMsg(recInPara);

			szMsg = "[크레인 작업지시]크레인 작업지시 메세지 전송 완료 >>>> " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch(Exception e) {
			return e.getMessage();
		}

		szMsg = "크레인 작업지시("+szMethodName+") 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;

	} //end of procY2CrnWrkOrdReq()

    /**
     * 오퍼레이션명 : 1후판정정 크레인권상실적등록 (Y2YDL008)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
     */
    public String procY2CrnUpWr(JDTORecord msgRecord)throws DAOException  {

		EJBConnector ejbConn 	= null;
    	JDTORecord recInPara 	= null;

		String 	szMethodName    = "procY2CrnUpWr";
		String 	szRtnMsg        = "";
		String 	szLogMsg        = "";
		String 	szSendMsg       = "";
		String 	szEjbJndiName 	= "JPlateYdCrnLoadWrkSeEJB";
		String 	szEjbMethod 	= "procY2CrnUpWr";
        String	szEqpId         = "";
        String	szYdWrkProgStat	= "";
        String	szYdSchCd		= "";
        String	szYdCrnSchId	= "";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.05 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {

			szLogMsg = "[1후판정정]크레인 권상실적처리 호출[" + szEjbJndiName + "." + szEjbMethod + "] - 메소드 콜 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			String sNEW_MODULE_EFF_YN = "N";
			
			JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
			sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A006"); // 1후판정정야드 권상실적(Y2YDL008) 신규모듈적용여부
			
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(Y2YDL008)---[[[ 1후판정정야드신규적용 : " + sNEW_MODULE_EFF_YN + " ]]]---", JPlateYdConst.DEBUG, logId);
			
			//1후판정정 권상실적처리
			ejbConn  = new EJBConnector("default", this);
			
			if(sNEW_MODULE_EFF_YN.equals("Y")) {
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.05 msgRecord에 logId 추가  
//-------------------------------------------------------------------------------------------------------------------------
				msgRecord.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
				
				// 신규 메소드 호출
				szRtnMsg = (String)ejbConn.trx("JPlateYdCrnLoadWrkSeEJB", "procY2CrnUpWr2", msgRecord);
			} else {
				// 기존 메소드 호출
				szRtnMsg = (String)ejbConn.trx("JPlateYdCrnLoadWrkSeEJB", "procY2CrnUpWr", msgRecord);
			}

			szLogMsg = "[1후판정정]크레인 권상실적처리 호출[" + szEjbJndiName + "." + szEjbMethod + "] - 메소드 콜 종료>>>>" + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg) ||
				JPlateYdConst.RETN_CRN_NO_SCH.equals(szRtnMsg) ||
				JPlateYdConst.RETN_CRN_NO_WRK.equals(szRtnMsg) ) {
				szLogMsg = "[1후판정정]크레인 권상실적처리 호출[" + szEjbJndiName + "." + szEjbMethod + "] - 메소드 콜 완료";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			} else {
				szLogMsg = "[1후판정정]크레인 권상실적처리 호출[" + szEjbJndiName + "." + szEjbMethod + "] - 메소드 콜 오류발생";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);

		        szEqpId         = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
		        szYdWrkProgStat	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");
		        szYdSchCd		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
		        szYdCrnSchId	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");

	            /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 1후판정정 야드L2 크레인작업실적응답 전송  - YDY2L005
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("MSG_ID", 				"YDY2L005");
	        	recInPara.setField("YD_EQP_ID", 			szEqpId);									// 야드설비ID
	        	recInPara.setField("YD_WRK_PROG_STAT", 		szYdWrkProgStat);							// 야드작업진행상태
	        	recInPara.setField("YD_SCH_CD", 			szYdSchCd);									// 야드스케줄코드
	        	recInPara.setField("YD_CRN_SCH_ID", 		szYdCrnSchId);								// 야드크레인스케줄ID
	        	recInPara.setField("YD_L2_WR_GP", 			JPlateYdConst.CRN_WRK_RE_LD_WR);			// 야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
            	recInPara.setField("YD_L3_HD_RS_CD",		JPlateYdConst.CRN_WRK_RE_CD_ERROR);		// 야드L3처리결과코드 - 8888 : 오류
        		recInPara.setField("YD_L3_MSG", 			szRtnMsg);

        		szSendMsg = ydDelegate.sendMsg(recInPara);

        		szLogMsg = "[1후판정정] 크레인작업실적응답 전문 전송 END >>>> " + szSendMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			    m_ctx.setRollbackOnly();
				return szRtnMsg;
			}

		} catch (Exception e) {
			szLogMsg = "[1후판정정]크레인 권상실적처리 호출[" + szEjbJndiName + "." + szEjbMethod + "] - 메소드 오류발생";
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
		}
		
		return JPlateYdConst.RETN_CD_SUCCESS;
    } // end of procY2CrnUpWr()

	/**
     * 오퍼레이션명 : 1후판정정야드 권하실적처리 (Y2YDL009)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
	 */
	public String procY2CrnDownWr(JDTORecord msgRecord) throws DAOException {

		EJBConnector ejbConn 	= null;
		String 	szMethodName    = "procY2CrnDownWr";
        String 	szOperationName	= "1후판정정야드 권하실적처리(Y2YDL009)";
		String 	szRtnMsg        = "";
		String 	szLogMsg        = "";
		String 	szEjbJndiName 	= "JPlateYdCrnUnloadWrkSeEJB";
		String 	szEjbMethod 	= "procY2CrnDnWr";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {

			szLogMsg = "[1후판정정]크레인 권하실적처리 호출[" + szEjbJndiName + "." + szEjbMethod + "] - 메소드 콜 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			
			String sNEW_MODULE_EFF_YN = "N";
			
			JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
			sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A001"); //1후판정정야드 권하실적(Y2YDL009) 신규모듈적용여부
			
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(Y2YDL009)---[[[ 1후판정정야드신규적용 : " + sNEW_MODULE_EFF_YN + " ]]]---", JPlateYdConst.DEBUG, logId);

			//1후판정정 권하실적처리
			ejbConn  = new EJBConnector("default", this);
			
			if(sNEW_MODULE_EFF_YN.equals("Y")) {
				//신규 메소드 호출
				szRtnMsg = (String)ejbConn.trx("JPlateYdCrnUnloadWrkSeEJB", "procY2CrnDnWr2", msgRecord);
			} else {
				//기존 메소드 호출
				szRtnMsg = (String)ejbConn.trx("JPlateYdCrnUnloadWrkSeEJB", "procY2CrnDnWr", msgRecord);
			}

			if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg) ||
				JPlateYdConst.RETN_CRN_NO_SCH.equals(szRtnMsg) ||
				JPlateYdConst.RETN_CRN_NO_WRK.equals(szRtnMsg) ) {
				szLogMsg = "[" + szOperationName + "] 메소드 콜 완료";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			} else {
				szLogMsg = "[" + szOperationName + "] 권하실적처리 호출 오류발생 >>>> " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);

				m_ctx.setRollbackOnly();
				return szRtnMsg;
			}

		} catch (Exception e) {
			szRtnMsg = "권하실적처리 호출 Exception 오류발생";
			szLogMsg = "[" + szOperationName + "]" + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);

			m_ctx.setRollbackOnly();
			return szRtnMsg;
		}
		return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of procY2CrnDownWr

	/**
     * 오퍼레이션명 : 1후판정정야드 강제권상요구 (Y2YDL010)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
	 */
	public String procY2OffCrnUpWr(JDTORecord msgRecord) throws DAOException {

		// 변수 선언
		EJBConnector ejbConn 		= null;
    	String 	szMethodName        = "procY2OffCrnUpWr";
    	String	szOperationName		= "1후판정정야드 강제권상요구";
		String 	sRtnCd				= "";
		String 	szRtnMsg         	= "";
		String	szYdL3Msg			= "";
		String 	szMsg         		= "";
		String 	szYdEqpId    		= "";
		JDTORecord outRecord		= JDTORecordFactory.getInstance().create();
		JDTORecord recPara			= null;

		try {
			//=============================================================
			// Log 테이블 등록
			//=============================================================
			szMsg = "["+ szMethodName + "] 강제권상요구 수신 .... START >>>> " + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szYdEqpId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"); 		//야드설비ID

			//1후판정정 강제권상요구
			ejbConn   	= new EJBConnector("default", "JPlateYdCrnLoadWrkSeEJB", this);
			outRecord 	= (JDTORecord)ejbConn.trx("procY2OffCrnUpWr", new Class[] { JDTORecord.class }, new Object[] { msgRecord });
			sRtnCd    	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), JPlateYdConst.RETN_CD_FAILURE);
			szYdL3Msg	= StringHelper.evl(outRecord.getFieldString("YD_L3_MSG"), "");

			szMsg = "[" + szOperationName + "] 강제권상 실적처리 호출결과 >>>> RTN_CD::" + sRtnCd + ", RTN_MSG::" + szYdL3Msg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(sRtnCd)) {			//성공이 아닐때

		        //------------------------------------------------------------------
		        // 	업무 : 크레인작업실적응답 전문 전송(YDY2L005)
		        //------------------------------------------------------------------
            	szMsg = "[" + szOperationName + "] 크레인작업실적응답 전문 전송 START ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		        recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("MSG_ID", 			"YDY2L005");
				recPara.setField("YD_EQP_ID", 		szYdEqpId);
			//	recPara.setField("YD_L2_WR_GP", 	"U");								//U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
				recPara.setField("YD_L2_WR_GP", 	"E");								//U:권상실적,P:권하실적,E:강제권상,R:고장,M:모드변경,J:지시요구,F:강제권하
				recPara.setField("YD_L3_HD_RS_CD", 	"8888");							//야드L3처리결과코드
				recPara.setField("YD_L3_MSG", 		ydUtils.substr(szYdL3Msg, 0, 40));	//야드L3처리결과메시지
				szRtnMsg = ydDelegate.sendMsg(recPara);

            	szMsg = "[" + szOperationName + "] 크레인작업실적응답 전문 전송 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				szMsg = "[" + szOperationName + "] 강제권상요구 오류 (" + szYdL3Msg + ")";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return szYdL3Msg;
			}

			szMsg = "["+ szMethodName + "] 강제권상요구 수신 .... END";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch (Exception e) {

			// Exception 발생시에도 응답메시지 전송
			try {
		        //------------------------------------------------------------------
		        // 	업무 : 크레인작업실적응답 전문 전송(YDY2L005)
		        //------------------------------------------------------------------
	        	szMsg = "[" + szOperationName + "] 크레인작업실적응답 (Exception) 전문 전송 START ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		        recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("MSG_ID", 			"YDY2L005");
				recPara.setField("YD_EQP_ID", 		szYdEqpId);
			//	recPara.setField("YD_L2_WR_GP", 	"U");								//U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
				recPara.setField("YD_L2_WR_GP", 	"E");								//U:권상실적,P:권하실적,E:강제권상,R:고장,M:모드변경,J:지시요구,F:강제권하
				recPara.setField("YD_L3_HD_RS_CD", 	"8888");							//야드L3처리결과코드
				szRtnMsg = ydDelegate.sendMsg(recPara);

	        	szMsg = "[" + szOperationName + "] 크레인작업실적응답 (Exception) 전문 전송 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			} catch (Exception ee) {
				szMsg = "["+ szMethodName + "] 크레인작업실적응답 (Exception) 전문 전송시 Exception 발생";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
		}
		return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of procY2OffCrnUpWr

	/**
     * 오퍼레이션명 : 1후판정정야드 강제권하요구 (Y2YDL011)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
	 */
	public String procY2OffCrnDnWr(JDTORecord msgRecord) throws DAOException {

		EJBConnector ejbConn2 		= null;
		String 	szMethodName    	= "procY2OffCrnDnWr";
    	String	szOperationName		= "1후판정정야드 강제권하요구";
        String 	sRtnCd				= JPlateYdConst.RETN_CD_FAILURE;
        String	szYdL3Msg			= "";
		String 	szRtnMsg        	= "";
		String 	szMsg         		= "";
    	String	szYdEqpId			= "";
		JDTORecord outRecord		= JDTORecordFactory.getInstance().create();
		JDTORecord recPara			= null;
		String  szTemp				= "";

		try {

			szMsg = "["+ szOperationName + "] 강제권하요구 수신 .... START";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szYdEqpId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"); 		//야드설비ID
			szTemp	  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC"); 		//야드설비ID
			
			
			if("TC".equals(szTemp.substring(2,4))) {
				
				//대차위치에서 최상단재료 저장품 제원요구 했을 때.. 대차이동 처리
				String sNEW_MODULE_EFF_YN = "N";
			
				JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
				
				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A030"); //1후판정정야드 대차 강제권상,권하시 스케줄삭제 및 이동처리 실행 여부
				
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 대차 강제권상,권하시 스케줄삭제 및 이동처리 실행 여부  : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
				
				if(sNEW_MODULE_EFF_YN.equals("Y")) {
					
					// 현재 대차위치가 강제권상요구 위치와 일치하는지 체크해서 일치하지 않을 경우 아래 로직을 수행한다.
					JPlateYdCommDAO  commDao 	= new JPlateYdCommDAO();
					String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
					JDTORecord jrParam		= JDTORecordFactory.getInstance().create();
					JDTORecordSet rsResult  = null;
					
					jrParam.setField("YD_STK_COL_GP", "P_" + ydUtils.substr(szTemp,2,4));    
					rsResult	= commDao.select(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdStatStkColInfo", logId, szMethodName, " 적치열 정보 조회 - 대차위치");

					if(rsResult.size() > 0) {
						if(!rsResult.getRecord(0).getFieldString("TC_TR_INFO").equals(ydUtils.substr(szTemp,0,6))) {
							//강제권하요구한 위치와 현재 대차 위치가 일치하지 않을 경우
							
							// 1) 대차에 권하하는 크레인 스케줄 삭제
							EJBConnector 		ejbConn 	= null;
							
							String szYdCrnSchId;
							String szYdSchCd;
							String szC_YD_WRK_PROG_STAT;
							String szYD_WBOOK_ID;
							String szYD_SCH_CD;
							String szYD_EQP_ID;
							
							JDTORecord recCheck = null;
							JDTORecord outRecord1 = null;
							
							String 	sRTN_CD					= "";
							String 	sRTN_MSG				= "";
							String  szStlNo					= "";
							
							String 	ydFrColGp	= "";	//대차출발위치
							String 	modifier   	= "Y2YDL011";
							String  szTCAR_NO   = "PX" + ydUtils.substr(szTemp,2,4); 
							
							//JDTORecordSet rsResult  = null;
							int 	intRtnVal 		= 0 ;
							
							JPlateYdCrnSchDAO   ydCrnSchDao = new JPlateYdCrnSchDAO();		// 크레인스케쥴DAO
							
							//JDTORecord recPara			= null;
							//String 	szRtnMsg         	= "";
							String szCrnSchCmplYn       = "Y";
							
							if("PCTC".equals(ydUtils.substr(szTemp, 0, 4))) {
								//강제권하 위치가 C동 대차 이면
								ydFrColGp = "PDTC" + ydUtils.substr(szTemp, 4, 2); 
								
							} else if("PDTC".equals(ydUtils.substr(szTemp, 0, 4))) {
								//강제권하 위치가 D동 대차 이면
								ydFrColGp = "PCTC" + ydUtils.substr(szTemp, 4, 2);
								
							} else {
								throw new DAOException(getClass().getName() + ":" + sRTN_CD);
							}
							
							
							//해당 열에 잡힌 크레인 작업지시를 조회하여 작업취소 
							JDTORecord recPara2		= JDTORecordFactory.getInstance().create();
							//String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
							//JPlateYdCommDAO  commDao 	= new JPlateYdCommDAO();
							
							recPara2.setField("YD_STK_COL_GP"	, ydFrColGp);    
							
							JDTORecordSet getRecSet	= commDao.select(recPara2, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdCrnSchIdByLocUD", logId, szMethodName, "해당 열에 잡힌 크레인 작업지시 조회");
							
							if (getRecSet.size() > 0) {
								
								//크레인 작업지시 취소 처리
								for (int ii=0; ii<getRecSet.size(); ii++) {
									
									szYdCrnSchId  	= getRecSet.getRecord(ii).getFieldString("YD_CRN_SCH_ID");
									szYdSchCd 		= getRecSet.getRecord(ii).getFieldString("YD_SCH_CD"); 
									szStlNo			= getRecSet.getRecord(ii).getFieldString("STL_NO"); 
									
									if ("".equals(szYdCrnSchId)) {
										//szMsg = "["+szOperationName+"] 스케줄 취소 처리("+szMethodName+") 실패, YD_CRN_SCH_ID값이 없음";
										//ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
										continue;
									}		
									
									recPara2.setField("YD_CRN_SCH_ID", szYdCrnSchId);
									recPara2.setField("YD_SCH_CD",     szYdSchCd);
									recPara2.setField("DEL_YN",        "N");
									recPara2.setField("MODIFIER",      modifier);
									
									/*
									 * 크레인 스케줄에서 선택된 스케줄 ID로 작업예약을 조회한 후
									 * 삭제되지 않는 작업예약에 포함된 크레인 스케줄중 첫번째 값이
									 * 2,3 인 경우 후처리를 하지않기 위해  Check Logic 반영
									 */
									rsResult = JDTORecordFactory.getInstance().createRecordSet("temRs");

									intRtnVal = ydCrnSchDao.getCheckYdCrnSchId(recPara2, rsResult);		// intGp == 36

									if (intRtnVal < 1) {
										szMsg = "["+szOperationName+"] 취소 작업을 완료 하였습니다.";
										ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
										continue;
									}

									rsResult.first();
									recCheck = rsResult.getRecord();

									szC_YD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recCheck, "YD_WRK_PROG_STAT");

									//2,3, 인 경우 와 혹시모를 4이면서도 스케줄 삭제가 되지 않은 경우
									if ("2".equals(szC_YD_WRK_PROG_STAT) || "3".equals(szC_YD_WRK_PROG_STAT) || "4".equals(szC_YD_WRK_PROG_STAT)) {
										szMsg = "["+szOperationName+"] 크레인 작업이 완료되지 않았습니다!!";
										ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
										
										szCrnSchCmplYn = "N";
										break;
									}

									/*
									 * 2,3이 아닌 경우 스케줄 취소기능에  첫번째 크레인 스케줄 ID 정보를 전송
									 */
									recPara = JDTORecordFactory.getInstance().create();
									recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recCheck, "YD_CRN_SCH_ID"));
									recPara.setField("YD_SCH_CD",     ydDaoUtils.paraRecChkNull(recCheck, "YD_SCH_CD"));
									recPara.setField("DEL_YN",        ydDaoUtils.paraRecChkNull(recCheck, "DEL_YN"));
									recPara.setField("MODIFIER",      modifier);

									//크레인스케줄 ID보다 이상인 ID 삭제  업데이트 실행(적치단정보까지 CLEAR)
									//스케줄취소
									szMsg = "["+szOperationName+"] 작업지시 취소 시작";
									ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

									ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
									outRecord1 	= (JDTORecord)ejbConn.trx("cancelJPlateYdCrnSch", new Class[] { JDTORecord.class }, new Object[] { recPara });

									sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
									sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

									szMsg = "["+szOperationName+"] ---- 작업지시 취소 종료!! >>>> " + sRTN_CD + " , " + sRTN_MSG;
									ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
									
									if ("0".equals(sRTN_CD)) {
										m_ctx.setRollbackOnly();
										throw new DAOException(getClass().getName() + ":" + sRTN_CD);
									}

									szMsg = "["+szOperationName+"] ---- 작업예약 취소 시작";
									ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

									ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
									outRecord1 	= (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { outRecord1 });

									sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
									sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
									
									szMsg = "["+szOperationName+"] ---- 작업예약 취소 종료!! >>>> " + outRecord1.toString();
									ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

									if ("0".equals(sRTN_CD)) {
										m_ctx.setRollbackOnly();
										throw new DAOException(getClass().getName() + ":" + sRTN_CD);
									}

									// F?RT??LM :: RT BOOK-OUT일때 재료정보 , 적치위치 Clear
									if ("RT".equals(ydUtils.substr(szYdSchCd,2,2)) && "LM".equals(ydUtils.substr(szYdSchCd,6,2))) {

										recPara = JDTORecordFactory.getInstance().create();
										recPara.setField("YD_CRN_SCH_ID", szYdCrnSchId);
										recPara.setField("YD_SCH_CD",     szYdSchCd);
										recPara.setField("STL_NO",        szStlNo);
										recPara.setField("MODIFIER",      modifier);

										szMsg = "["+szOperationName+"] ---- RT BOOK-OUT 재료정보 CLEAR 시작 >>>> " + recPara.toString();
										ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

										ejbConn 		= new EJBConnector("default", this);
										String rtnMsg	= (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "delStockLocOnRt", recPara);

										szMsg = "["+szOperationName+"] ---- RT BOOK-OUT 재료정보 CLEAR 종료 >>>> " + rtnMsg;
										ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
									}
								}
							}
							
							
							if(!"N".equals(szCrnSchCmplYn)) { //크레인 작업이 완료되지 않은게 없을 경우 수행하지 않는다.
								
								//해당 열에 잡힌 크레인 작업예약을 조회하여 작업삭제 
								recPara2.setField("YD_STK_COL_GP"	, ydFrColGp);    
								
								getRecSet	= commDao.select(recPara2, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdWrkBookIdByLoc", logId, szMethodName, "해당 열에 잡힌 크레인 작업예약 조회");
								
								if (getRecSet.size() > 0) {
									
									//크레인 작업예약 삭제 처리
									for (int ii=0; ii<getRecSet.size(); ii++) {
										szYD_WBOOK_ID	= getRecSet.getRecord(ii).getFieldString("YD_WBOOK_ID");
										szYD_SCH_CD 	= getRecSet.getRecord(ii).getFieldString("YD_SCH_CD");
										szYD_EQP_ID 	= getRecSet.getRecord(ii).getFieldString("YD_EQP_ID");
					
										recPara = JDTORecordFactory.getInstance().create();
										recPara.setField("MODIFIER",      	modifier);
										recPara.setField("YD_WBOOK_ID",		szYD_WBOOK_ID);
										recPara.setField("YD_EQP_ID",       szYD_EQP_ID);
										recPara.setField("YD_SCH_CD",     	szYD_SCH_CD);
					
										
										szMsg = "["+szOperationName+"] ---- 작업예약 취소 시작";
										ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					
										ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
										outRecord1 	= (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { recPara });
					
										sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
										sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
										
										szMsg = "["+szOperationName+"] ---- 작업예약 취소 종료!! >>>> " + outRecord1.toString();
										ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					
										if ("0".equals(sRTN_CD)) {
											m_ctx.setRollbackOnly();
											throw new DAOException(getClass().getName() + ":" + sRTN_CD);
										}
									}
									
								}
								
								// 2) 대차이동 처리
	
								//TO위치 대차 지정 (TB_YD_EQP 현재동변경);
								jrParam.setField("MODIFIER"				, modifier);
								jrParam.setField("YD_CURR_BAY_GP"		, szTemp.substring(1,2) );
								jrParam.setField("YD_EQP_ID"			, szTCAR_NO );
								jrParam.setField("YD_GP"				, "P" );
								commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updTcarCurrBayGp", logId, szMethodName, "대차 현재동 지정 ");
								
								jrParam.setField("YD_EQP_ID", 			szTCAR_NO);
								jrParam.setField("YD_CURR_BAY_GP",		szTemp.substring(1,2));
								jrParam.setField("MODIFIER", 			modifier);
								jrParam.setField("YD_START_LOC",		ydFrColGp);		// 대차 출발 위치
								jrParam.setField("YD_STOP_LOC",			ydUtils.substr(szTemp,0,6));		// 대차 도착 위치
								
						    	//-------------------------------------------------------------
						    	// 대차 도착 저장위치(베드) 활성화 처리
								ejbConn = new EJBConnector("default", "JPlateYdTcarSchSeEJB", this);
								szRtnMsg = (String)ejbConn.trx("enableFromBed", new Class[] { JDTORecord.class }, new Object[] { jrParam });
								
						    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
									
									szRtnMsg = "대차 도착 저장위치(베드) 활성화 처리시 오류 발생 : " + szRtnMsg;
							    	szMsg 	 = "[" + szMethodName + "] " + szRtnMsg;
									ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
									
									m_ctx.setRollbackOnly();
									throw new DAOException(getClass().getName() + ":" + szRtnMsg);
						    	}
								
						    	//-------------------------------------------------------------
						    	// 대차 출발 저장위치(베드) 비활성화 처리
								szRtnMsg = (String)ejbConn.trx("disableToBed", new Class[] { JDTORecord.class }, new Object[] { jrParam });
								
						    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
									
									szRtnMsg = "대차 출발 저장위치(베드) 비활성화 처리시 오류 발생 : " + szRtnMsg;
							    	szMsg 	 = "[" + szMethodName + "] " + szRtnMsg;
									ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
									
									m_ctx.setRollbackOnly();
									throw new DAOException(getClass().getName() + ":" + szRtnMsg);
						    	}			
								
						    	//-------------------------------------------------------------
						    	// 야드  L2 Interface 처리
						    	//-------------------------------------------------------------
								JDTORecord recL2Para = JDTORecordFactory.getInstance().create();;
						    	
						    	// - 저장위치 제원정보 야드L2전송 (FROM:비활성화)
								recL2Para.setField("MSG_ID", 			"YDY2L001");
								recL2Para.setField("YD_INFO_SYNC_CD", "3");  // 1:동, 2:SPAN, 3:열, 4:BED
								recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);
								recL2Para.setField("YD_STK_COL_GP", 	ydFrColGp);
								recL2Para.setField("YD_STK_BED_NO", 	"");
								szRtnMsg = ydDelegate.sendMsg(recL2Para);
								
						    	// - 저장위치 제원정보 야드L2전송 (TO:활성화)
								recL2Para.setField("MSG_ID", 			"YDY2L001");
								recL2Para.setField("YD_INFO_SYNC_CD", "3");  // 1:동, 2:SPAN, 3:열, 4:BED
								recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);
								recL2Para.setField("YD_STK_COL_GP", 	ydUtils.substr(szTemp,0,6));
								recL2Para.setField("YD_STK_BED_NO", 	"");
								szRtnMsg = ydDelegate.sendMsg(recL2Para);
								
								// - 저장품 제원정보 야드L2전송
								recL2Para.setField("MSG_ID", 			"YDY2L002");                        // TC-CODE
								recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);	// 야드구분
								recL2Para.setField("YD_STK_COL_GP", 	ydUtils.substr(szTemp,0,6));        // 야드적치열구분
								recL2Para.setField("YD_STK_BED_NO", 	"");    							// 야드적치BED번호
								recL2Para.setField("YD_INFO_SYNC_CD",   "3");								// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
								recL2Para.setField("STL_NO", 			"");	        					// 재료번호
								szRtnMsg = ydDelegate.sendMsg(recL2Para);
								
							}
							
						}
					}
				} 
				
			}			

			//1후판정정 권하실적처리
			ejbConn2 	= new EJBConnector("default", "JPlateYdCrnUnloadWrkSeEJB", this);
			outRecord  	= (JDTORecord)ejbConn2.trx("procY2OffCrnDnWr", new Class[] { JDTORecord.class }, new Object[] { msgRecord });
			sRtnCd    	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), JPlateYdConst.RETN_CD_FAILURE);
			szYdL3Msg	= StringHelper.evl(outRecord.getFieldString("YD_L3_MSG"), "");

			szMsg = "[" + szOperationName + "] 강제권하 실적처리 호출결과 >>>> RTN_CD::" + sRtnCd + ", RTN_MSG::" + szYdL3Msg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(sRtnCd)) {			//실패

		        //------------------------------------------------------------------
		        // 	업무 : 크레인작업실적응답 전문 전송(YDY2L005)
		        //------------------------------------------------------------------
            	szMsg = "[" + szOperationName + "] 크레인작업실적응답 전문 전송 START ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		        recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("MSG_ID", 			"YDY2L005");
				recPara.setField("YD_EQP_ID", 		szYdEqpId);
				recPara.setField("YD_L2_WR_GP", 	"F");								//U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
				recPara.setField("YD_L3_HD_RS_CD", 	"8888");							//야드L3처리결과코드
				recPara.setField("YD_L3_MSG", 		ydUtils.substr(szYdL3Msg, 0, 40));	//야드L3처리결과메시지
				szRtnMsg = ydDelegate.sendMsg(recPara);

            	szMsg = "[" + szOperationName + "] 크레인작업실적응답 전문 전송 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				m_ctx.setRollbackOnly();

				szMsg = "강제권하요구 오류 (" + szYdL3Msg + ")";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			//	throw new DAOException(szRtnMsg);
				return szMsg;
			}

			szMsg = "["+ szMethodName + "] 강제권하요구 수신 .... END";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch (Exception e) {

			// Exception 발생시에도 응답메시지 전송
			try {
		        //------------------------------------------------------------------
		        // 	업무 : 크레인작업실적응답 전문 전송(YDY2L005)
		        //------------------------------------------------------------------
	        	szMsg = "[" + szOperationName + "] 크레인작업실적응답 (Exception) 전문 전송 START ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		        recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("MSG_ID", 			"YDY2L005");
				recPara.setField("YD_EQP_ID", 		szYdEqpId);
				recPara.setField("YD_L2_WR_GP", 	"F");								//U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
				recPara.setField("YD_L3_HD_RS_CD", 	"8888");							//야드L3처리결과코드
				szRtnMsg = ydDelegate.sendMsg(recPara);

	        	szMsg = "[" + szOperationName + "] 크레인작업실적응답 (Exception) 전문 전송 END >>>> " + e.getMessage();
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			} catch (Exception ee) {
				szMsg = "["+ szMethodName + "] 크레인작업실적응답 (Exception) 전문 전송시 Exception 발생";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				m_ctx.setRollbackOnly();
				return ee.getMessage();
			}

			m_ctx.setRollbackOnly();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
		//	throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
			return e.getMessage();
		}
		return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of procY2OffCrnDnWr

	/**
     * 오퍼레이션명 : 1후판정정야드 명령선택처리 (Y2YDL012)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
	 */
	public String procY2CrnOrderSel(JDTORecord msgRecord) throws DAOException {

		JPlateYdCrnSchDAO 	ydCrnSchDao	= new JPlateYdCrnSchDAO();

		// 레코드 선언
		JDTORecord recPara         	= null;
		JDTORecordSet rsCrnSch		= null;

		// DAO 및 UTIL 객체 생성
		JPlateYdDelegate ydDelegate	= new JPlateYdDelegate();

		// 변수 선언
    	String 	szMethodName        = "procY2CrnOrderSel";
        String	szOperationName		= "1후판정정야드 명령선택처리";
		String 	szMsg               = "";
		String	szRtnMsg			= "";
		String 	szRcvTcCode      	= ydUtils.getTcCode(msgRecord);
		String 	szYdL3HdRsCd 		= "";
		String 	szYdL3Msg 			= "";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 		// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		int		intRtnVal			= 0;

		try {
			//=============================================================
			// Log 테이블 등록
			//=============================================================
			szMsg = "[1후판정정야드] 크레인명령선택 수신 >>>> " + msgRecord.toString();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        // 설비테이블에 야드설비작업Mode 업데이트
	        String 	szYdWrkProgStat = "";
	        String 	szYdEqpId       = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"		);		// 야드설비ID
	        String 	szYdReqProgStat = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CMD_PKUP_GP"	);		// 야드명령선택구분 (S:선택, C:취소)
	        String 	szYdCrnSchId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID"	);		// 야드크레인스케쥴ID
	        String	szYdSchCd		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD"		);		// 야드크레인스케쥴코드
	        String 	szModifier		= ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER"		);		// 수정자
	        if ("".equals(szModifier)) {
	        	szModifier = szRcvTcCode;
	        }

	        // W:명령선택대기, 1:권상지시, 3:권하지시
	        if ("S".equals(szYdReqProgStat)) {
	        	szYdWrkProgStat = JPlateYdConst.YD_EQP_STAT_UP_WO;		// "1" - 권상지시
	        } else {
	        	szYdWrkProgStat = JPlateYdConst.YD_EQP_STAT_IDLE;			// "W" - 명령선택 대기 상태
	        }

        	//------------------------------------------------------------------------------------------------------
        	// 상단에 다른 크레인 작업지시 존재여부 체크
        	//------------------------------------------------------------------------------------------------------
        	szYdCrnSchId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");

	        if ("S".equals(szYdReqProgStat)) {
	        	szMsg = "[" + szOperationName + "] 해당 저장위치 상단에 다른 작업지시 존재여부 체크 .. 시작 >>>> " + szYdCrnSchId;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				rsCrnSch  	= JDTORecordFactory.getInstance().createRecordSet("temp");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID",		szYdCrnSchId);
	        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
				recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
				        	
	        	intRtnVal = ydCrnSchDao.getUpLyrCrnSchExistYdP(recPara, rsCrnSch);
	        	
	        } else {
	        	intRtnVal = 0;
	        	szMsg = "[" + szOperationName + "] 명령선택 취소일때는 SKIP";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        }

        	if (intRtnVal > 0) {
        		szRtnMsg = "해당 저장위치 상단에 다른 작업지시 존재";
        		szMsg    = "[" + szOperationName + "] " + szRtnMsg + "하여 오류발생 >>>> " + intRtnVal;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

        		szYdL3HdRsCd = "8888";
				szYdL3Msg	 = szRtnMsg;

        	} else {

	        	szMsg = "[" + szOperationName + "] 해당 저장위치 상단에 다른 작업지시 존재여부 체크 .. 완료";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				// 명령선택처리
				// 1. 야드명령선택구분 (S:선택, C:취소)에 따라 크레인스케줄(TB_YD_CRNSCH)에 야드작업진행상태(YD_WRK_PROG_STAT)를 변경처리
				// 선택이면 W->1로 처리 , 취소이면 1->W 로 처리

//---------------------------------------------------------------------------------------------
// 2024.12.06 updCrnWrkProgStatYdP call argument에 logId 추가  
//				String lzRtnMsg = this.updCrnWrkProgStatYdP(szYdCrnSchId, szYdEqpId, szYdWrkProgStat, szModifier);
				String lzRtnMsg = this.updCrnWrkProgStatYdP(szYdCrnSchId, szYdEqpId, szYdWrkProgStat, szModifier, logId);
//---------------------------------------------------------------------------------------------

				if (JPlateYdConst.RETN_CD_SUCCESS.equals(lzRtnMsg)) {
					szMsg = "[" + szOperationName + "] 크레인명령선택 처리 성공 하였습니다.";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					szYdL3HdRsCd = "0000";
				} else {
					szMsg = "[" + szOperationName + "] 크레인명령선택 처리 실패 하였습니다. >>>> " + lzRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					szYdL3HdRsCd = "8888";
				//	szYdL3Msg	 = "크레인명령선택 처리 실패 하였습니다.";
					szYdL3Msg	 = lzRtnMsg;
				}
        	}

			if ("0000".equals(szYdL3HdRsCd)) {

				if ("S".equals(szYdReqProgStat)) {
			        //------------------------------------------------------------------
			        // 	업무 : 작업지시 전문 전송 (YDY2L004)
			        //------------------------------------------------------------------
			        recPara = JDTORecordFactory.getInstance().create();

					//recPara.setField("MSG_ID", 				"YDY2L004");
					recPara.setField("MSG_ID", 				"YDY2L004V2"						);
					recPara.setField("YD_CRN_SCH_ID",    	szYdCrnSchId						);
					recPara.setField("YD_WRK_PROG_STAT", 	szYdWrkProgStat						);
					recPara.setField("YD_SCH_CD",        	szYdSchCd							);
					recPara.setField("YD_GP",            	JPlateYdConst.YD_GP_P_PLATE_YARD	);
					recPara.setField("MODIFIER", 			"Y2YDL007"							);
					recPara.setField("MSG_GP", 				"U"									);
		        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
					recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
					
					szRtnMsg = ydDelegate.sendMsg(recPara);

	            	szMsg = "[" + szOperationName + "] 현재크레인이 명령선택한 작업지시를 전송 END >>>> " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				} else {

					// 명령선택 취소시에는 updCrnWrkProgStat 메서드에서 취소전문 송신함으로 L2 전송은 SKIP
	            	szMsg = "[" + szOperationName + "] 명령선택 취소 처리 완료 .... L2 전송 SKIP";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				}

			} else {

		        //------------------------------------------------------------------
		        // 	업무 : 크레인작업실적응답 전문 전송(YDY2L005)
		        //------------------------------------------------------------------
            	szMsg = "[" + szOperationName + "] 크레인작업실적응답 전문 전송 START ";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		        recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("MSG_ID", 			"YDY2L005"		);
				recPara.setField("YD_EQP_ID", 		szYdEqpId		);
				recPara.setField("YD_L2_WR_GP", 	"J"				);		// U:권상실적,P:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
				recPara.setField("YD_L3_HD_RS_CD", 	szYdL3HdRsCd	);		// 야드L3처리결과코드
				recPara.setField("YD_L3_MSG", 		szYdL3Msg		);
	        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
				recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
				
				szRtnMsg = ydDelegate.sendMsg(recPara);

            	szMsg = "[" + szOperationName + "] 크레인작업실적응답 전문 전송 END >>>> " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				m_ctx.setRollbackOnly();

				return szYdL3Msg;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
		}
		return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of procY2CrnOrderSel

 
    /**
     * 오퍼레이션명 : 해당 크레인 파일링실적 수신 (Y2YDL013)
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● Stirng pYdCrnSchId, String pYdWrkProgStat
     * @return ● nRtnVal
     * @throws ● DAOException
     */
	public String procY2PilingRslt(JDTORecord msgRecord) throws DAOException {

    	//DAO
		JPlateYdStockDAO 	 	ydStockDao 		= new JPlateYdStockDAO();
		JPlateYdCrnSchDAO 	 	ydCrnSchDao 	= new JPlateYdCrnSchDAO();
		JPlateYdCrnWrkMtlDAO 	ydCrnWrkMtlDao 	= new JPlateYdCrnWrkMtlDAO();
		JPlateYdWrkbookDAO		ydWrkbookDao	= new JPlateYdWrkbookDAO();
		JPlateYdWrkbookMtlDAO	ydWrkbookMtlDao	= new JPlateYdWrkbookMtlDAO();
		JPlateYdStkLyrDAO 		ydStkLyrDao		= new JPlateYdStkLyrDAO();
		JPlateYdEqpDAO			ydEqpDao		= new JPlateYdEqpDAO();
		JPlateYdStkColDAO		ydStkColDao		= new JPlateYdStkColDAO();
		JPlateYdStkBedDAO 		ydStkBedDao 	= new JPlateYdStkBedDAO();

		// 변수 선언
        String 	szMethodName  	= "procY2PilingRslt";
        String	szRtnMsg		= "";
    	String 	szMsg         	= "";
    	String 	szOperationName = "파일링실적 수신";

    	String 	szYdCrnSchId	= "";					// 스케쥴ID
    	String 	szOldYdCrnSchId	= "";					// 스케쥴ID Old
    	String 	szNewYdCrnSchId	= "";					// 스케쥴ID New
    	String	szOldYdWbookId	= "";					// 작업예약ID Old
    	String	szNewYdWbookId	= "";					// 작업예약ID New
		String 	szStlNo			= "";					// 재료번호
		String 	szYdUpWoLayer	= "";					// 야드권상지시단
		String 	szRcvTcCode		= ydUtils.getTcCode(msgRecord);

    	String 	arrSchId[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 스케쥴코드
    	String 	arrWbookId[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 기존작업예약ID
    	String 	arrStlNo[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 재료번호
    	String 	arrYdLoc[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 재료적치열+재료적치베드
    	String 	arrLayer[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 재료적치단

    	String	szYdGp			= "";
    	String	szYdEqpId		= "";
    	String	szYdPilingSh 	= "";					// 파일링매수
		String	szYdPilingGp	= "";					// 파일링구분 (P:파일링, H:횡행작업, M:멀티작업)
		String	szModifier		= "";
		String 	szSendMsg		= "";
		String	szYdSchCd		= "";
    	String	szYdUpWoLoc 	= "";
    	String	szYdUpWoBed		= "";
    	String	szYdDnWoLoc 	= "";
    	String	szYdStkBedNo 	= "";
    	String	szYdStkLyrNo	= "";
    	String	szYdAidWrkYn	= "";					// 보조작업여부
    	String	szSaveAidWrkYn	= "";					// 주작업+보조작업 으로 파일링 못하도록 체크하기위한 필드
    	String	szYdStkColGp 	= "";					// 적치열구분 		(횡작업길이체크 용도)
		String	szYdStkSpanGp 	= "";					// 적치스판구분 	(횡작업길이체크 용도)
		String	szFstLyrNo		= "";
		String	szLastLyrNo		= "";
		String	szYdWrkStat		= "";
		String	szYdEqpStat		= "";

    	double	dArrWrkT[]		= {0,0,0,0,0};			// 야드설비작업총두께	[적치단]
    	int		iArrWrkLen[]	= {0,0,0,0,0};			// 야드설비작업총길이	[적치베드]

    	int		iSumWrkWt		= 0;					// 야드설비작업총중량
    	double	dSumWrkT		= 0;					// 야드설비작업총두께
    	int		iSumWrkLen		= 0;					// 야드설비작업총길이
    	int		iMtlL			= 0;					// 재료의길이
    	int		iWrkCnt			= 0;
    	int		iUpWrkCnt		= 0;
    	int		iDnWrkCnt		= 0;

		// 레코드선언
		JDTORecordSet rsResult  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara      = null;
		JDTORecord recEqp 		= JDTORecordFactory.getInstance().create();
		JDTORecord recSch 		= JDTORecordFactory.getInstance().create();
		JDTORecord recMtl 		= JDTORecordFactory.getInstance().create();
		JDTORecord recCol 		= JDTORecordFactory.getInstance().create();
		JDTORecord recWbook		= JDTORecordFactory.getInstance().create();
		JDTORecord recL2Msg		= null;

    	int 	intRtnVal 		= 0;
    	int		iBedIdx 		= 0;		// 적치베드 	Index (0~2)
    	int		iLyrIdx 		= 0;		// 적치단 	Index (0~5)
    	int		iYdPilingSh		= 0;
    	int		iYdStkColL		= 0;
    	boolean	bLocCheck		= true;
    	
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본

        try {
        	szMsg = "["+szOperationName+"] 파일링 실적 .. 메소드 시작 >>>>" + msgRecord.toString();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			if ("".equals(szRcvTcCode)) {
				szRcvTcCode = "Y2YDL013";
			}

			szYdEqpId		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			szYdPilingSh 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_PILING_SH");
			szYdPilingGp	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_PILING_GP");			// 파일링구분 (P:파일링, H:횡행작업, M:멀티작업)
			szModifier		= ydDaoUtils.paraRecModifier(msgRecord);
			iYdPilingSh		= ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_PILING_SH");

			//------------------------------------------------------------------------------------------------
			// 설비상태  체크
			//------------------------------------------------------------------------------------------------
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			recEqp    = JDTORecordFactory.getInstance().create();
        	recPara   = JDTORecordFactory.getInstance().create();
        	recPara.setField("YD_EQP_ID", 			szYdEqpId);
        	
        	/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.getYdEqp 

        	SELECT 
        	    YD_EQP_ID AS YD_EQP_ID
        	    ,REGISTER AS REGISTER
        	    ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
        	    ,MODIFIER AS MODIFIER
        	    ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
        	    ,DEL_YN AS DEL_YN
        	    ,YD_GP AS YD_GP
        	    ,YD_BAY_GP AS YD_BAY_GP
        	    ,YD_EQP_GP AS YD_EQP_GP
        	    ,YD_EQP_NO AS YD_EQP_NO
        	    ,YD_WRK_ALW_XAXIS_TO AS YD_WRK_ALW_XAXIS_TO
        	    ,YD_EQP_NAME AS YD_EQP_NAME
        	    ,YD_EQP_STAT AS YD_EQP_STAT
        	    ,YD_EQP_WRK_MODE AS YD_EQP_WRK_MODE
        	    ,YD_WRK_ALW_XAXIS_FR AS YD_WRK_ALW_XAXIS_FR
        	    ,YD_WRK_ALW_YAXIS_FR AS YD_WRK_ALW_YAXIS_FR
        	    ,YD_WRK_ALW_YAXIS_TO AS YD_WRK_ALW_YAXIS_TO
        	    ,YD_WRK_ALW_ZAXIS_FR AS YD_WRK_ALW_ZAXIS_FR
        	    ,YD_WRK_ALW_ZAXIS_TO AS YD_WRK_ALW_ZAXIS_TO
        	    ,YD_CRN_TRAVL_OFFSET AS YD_CRN_TRAVL_OFFSET
        	    ,YD_CRN_GRAB_TP AS YD_CRN_GRAB_TP
        	    ,YD_CRN_TRAVS_OFFSET AS YD_CRN_TRAVS_OFFSET
        	    ,YD_L2_HMI_STAT AS YD_L2_HMI_STAT
        	    ,YD_CTS_RELAY_YN AS YD_CTS_RELAY_YN
        	    ,YD_CTS_RELAY_BAY_GP AS YD_CTS_RELAY_BAY_GP
        	    ,YD_CRN_GRAB1_ACT_STAT AS YD_CRN_GRAB1_ACT_STAT
        	    ,YD_CRN_GRAB2_ACT_STAT AS YD_CRN_GRAB2_ACT_STAT
        	    ,YD_WRK_ABLE_XAXIS_FR AS YD_WRK_ABLE_XAXIS_FR
        	    ,YD_WRK_ABLE_XAXIS_TO AS YD_WRK_ABLE_XAXIS_TO
        	    ,YD_WRK_ABLE_YAXIS_FR AS YD_WRK_ABLE_YAXIS_FR
        	    ,YD_WRK_ABLE_YAXIS_TO AS YD_WRK_ABLE_YAXIS_TO
        	    ,YD_WRK_ABLE_ZAXIS_FR AS YD_WRK_ABLE_ZAXIS_FR
        	    ,YD_WRK_ABLE_ZAXIS_TO AS YD_WRK_ABLE_ZAXIS_TO
        	    ,YD_CURR_BAY_GP AS YD_CURR_BAY_GP
        	    ,YD_HOME_BAY_GP AS YD_HOME_BAY_GP
        	    ,YD_TCAR_WRK_ABLE_BAY1 AS YD_TCAR_WRK_ABLE_BAY1
        	    ,YD_TCAR_WRK_ABLE_BAY2 AS YD_TCAR_WRK_ABLE_BAY2
        	    ,YD_TCAR_WRK_ABLE_BAY3 AS YD_TCAR_WRK_ABLE_BAY3
        	    ,YD_TCAR_WRK_ABLE_BAY4 AS YD_TCAR_WRK_ABLE_BAY4
        	    ,YD_TCAR_WRK_ABLE_BAY5 AS YD_TCAR_WRK_ABLE_BAY5    
        	    ,YD_TCAR_WRK_ABLE_BAY6 AS YD_TCAR_WRK_ABLE_BAY6    
        	    ,YD_TCAR_WRK_ABLE_BAY7 AS YD_TCAR_WRK_ABLE_BAY7    
        	    ,YD_TCAR_WRK_ABLE_BAY8 AS YD_TCAR_WRK_ABLE_BAY8    
        	    ,YD_CRN_USE_SEQ AS YD_CRN_USE_SEQ
        	    ,YD_CRN_CONT_CARASGN_CNT AS YD_CRN_CONT_CARASGN_CNT
        	    ,YD_CRN_CONT_CARASGN_WR AS YD_CRN_CONT_CARASGN_WR
        		,(SELECT ITEM 
        	        FROM TB_YD_RULE 
        	       WHERE REPR_CD_GP = 'H00031' 
        	         AND CD_GP   = SUBSTR(YD_EQP_ID,6,1)
        	         AND ITEM1 = 'Y') TCAR_HO_CNT 
        		,(SELECT ITEM_VALUE1  AS 
        	        FROM TB_YD_RULE 
        	       WHERE REPR_CD_GP = 'H00031' 
        	         AND CD_GP   = SUBSTR(YD_EQP_ID,6,1) 
        	         AND ITEM1 = 'Y')TCAR_HO_WGT 
        	FROM TB_YD_EQP
        	WHERE YD_EQP_ID = :V_YD_EQP_ID
        	    AND DEL_YN='N'
        	    	*/
			intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult);
			if (intRtnVal > 0) {
				rsResult.first();
				recEqp = rsResult.getRecord();
				szYdEqpStat = ydDaoUtils.paraRecChkNull(recEqp, "YD_EQP_STAT");
			}

			if (JPlateYdConst.YD_EQP_STAT_UP_CMPL.equals(szYdEqpStat) || 			// 권상완료
				JPlateYdConst.YD_EQP_STAT_DN_WO.equals(szYdEqpStat)) {				// 권하지시

				szRtnMsg = "권상처리후 파일링/횡작업 불가.";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
        	}

			// 파라미터 체크
			if (iYdPilingSh < 1 || iYdPilingSh > 15) {
				szRtnMsg = "파일링 매수 오류 :: " + szYdPilingSh;
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szRtnMsg;
			}
			szYdGp = ydUtils.substr(szYdEqpId, 0, 1);

			if (!"H".equals(szYdPilingGp) && !"P".equals(szYdPilingGp) && !"M".equals(szYdPilingGp)) {
				szRtnMsg = "파일링 구분 오류 :: " + szYdPilingGp;
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			if ("H".equals(szYdPilingGp) && iYdPilingSh > 3) {
				szRtnMsg = "횡작업시 3매 초과!!";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}
			if ("P".equals(szYdPilingGp) && iYdPilingSh > 5) {
				szRtnMsg = "파일링시 5매 초과!!";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}
			if ("M".equals(szYdPilingGp) && iYdPilingSh > 15) {
				szRtnMsg = "멀티작업시 15매 초과!!";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			/*
			YD_EQP_ID			야드설비ID			CHAR	6
			YD_PILING_SH		파일링매수			NUMBER	2
			YD_PILING_GP		파일링구분			CHAR	1		파일링구분 (P:파일링, H:횡행작업, M:멀티작업)

			YD_CRN_SCH_ID1		야드크레인스케쥴ID1	CHAR	18
			STL_NO1				재료번호1				CHAR	11
			YD_UP_WO_LOC1		권상지시위치1			CHAR	8
			YD_UP_WO_LAYER1		권상지시단1			CHAR	3
						:
			YD_CRN_SCH_ID15		야드크레인스케쥴ID15	CHAR	18
			STL_NO15			재료번호15			CHAR	11
			YD_UP_WO_LOC15		권상지시위치15			CHAR	8
			YD_UP_WO_LAYER15	권상지시단15			CHAR	3
			*/

        	recPara  = JDTORecordFactory.getInstance().create();

        	iBedIdx 	= 0;		// 적치베드 	Index (0~2)
        	iLyrIdx 	= 0;		// 적치단 	Index (0~5)
        	szFstLyrNo 	= "";       // 최하단 
        	szLastLyrNo 	= "";

        	// 파일링 실적중 최하단 구하기
			for(int ii=0; ii<iYdPilingSh; ii++) {
				szYdUpWoLayer 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WO_LAYER"+Integer.toString(ii+1));	// 권상지시단
				if (ii==0) {
					szFstLyrNo	= szYdUpWoLayer;
					szLastLyrNo = szYdUpWoLayer;
				}
				if (!"".equals(szYdUpWoLayer) && !"".equals(szFstLyrNo)) {
					if (Integer.parseInt(szYdUpWoLayer) < Integer.parseInt(szFstLyrNo)) {
						szFstLyrNo	= szYdUpWoLayer;
					}
				}
				if (!"".equals(szYdUpWoLayer) && !"".equals(szLastLyrNo)) {
					if (Integer.parseInt(szYdUpWoLayer) > Integer.parseInt(szLastLyrNo)) {
						szLastLyrNo	= szYdUpWoLayer;
					}
				}
			}
			
			if ("M".equals(szYdPilingGp)||"P".equals(szYdPilingGp)) {		// 파일링작업,멀티작업 일때
				if (szFstLyrNo.equals(szLastLyrNo)){
					//szRtnMsg = "멀티작업 및 파일링 작업인데 권상지시 단이 동일 함 :: " + szYdCrnSchId;
					szRtnMsg = "M/P UP =" + szYdCrnSchId;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
			}

			if ("H".equals(szYdPilingGp)) {		// 횡작업 일때
				if (!szFstLyrNo.equals(szLastLyrNo)){
					//szRtnMsg = "횡작업 작업인데 권상지시 단이 틀림 :: " + szYdCrnSchId;
					szRtnMsg = "H UP X" + szYdCrnSchId;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
			}
			
			szMsg    = "["+szOperationName+"] 파일링/횡작업 실적 수신데이타중 최하단 정보 >>>> " + szFstLyrNo;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			// 야드크레인스케쥴ID, 재료번호, 권상지시위치, 권상지시단 체크
			for(int ii=0; ii<iYdPilingSh; ii++) {
				szYdCrnSchId  	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID"+Integer.toString(ii+1));		// 야드크레인스케쥴ID
				szStlNo		  	= ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"+Integer.toString(ii+1));			// 재료번호
				szYdUpWoLoc 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WO_LOC"+Integer.toString(ii+1));		// 권상지시위치
				szYdUpWoLayer 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WO_LAYER"+Integer.toString(ii+1));	// 권상지시단
        		szYdStkColGp  	= ydUtils.substr(szYdUpWoLoc, 0, 6);		// 권상위치구분
				szYdStkSpanGp	= ydUtils.substr(szYdUpWoLoc, 2, 2);		// 권상위치 스판구분
				szYdUpWoBed		= ydUtils.substr(szYdUpWoLoc, 6, 2);		// 권상위치 베드 번호
				

				// 스케쥴 ID 체크
				if ("".equals(szYdCrnSchId)) {
					szRtnMsg = "스케쥴 ID 오류 :: " + szYdCrnSchId;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}

				// 재료번호 체크
				if ("".equals(szStlNo)) {
					szRtnMsg = "재료번호 오류 :: " + szStlNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}

				// 권상지시위치 체크
				bLocCheck = true;
				if ("".equals(szYdUpWoLoc)) {
					bLocCheck = false;
				} else {
		        	rsResult  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		        	recPara   = JDTORecordFactory.getInstance().create();
		        	recPara.setField("YD_STK_COL_GP", 		szYdStkColGp);
		        	recPara.setField("YD_STK_BED_NO", 		szYdUpWoBed);
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
		        	intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsResult);
		        	if (intRtnVal <= 0) {
		        		bLocCheck = false;
		        	}
				}

				if (!bLocCheck) {
					szRtnMsg = "권상위치 오류 :: " + szYdUpWoLoc;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}

				// 권상지시단 체크
				if ("".equals(szYdUpWoLayer)) {
					szRtnMsg = "권상위치단 오류 :: " + szYdUpWoLayer;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}

				if (!"".equals(szYdCrnSchId)) {
					if ("".equals(szOldYdCrnSchId)) {
						szOldYdCrnSchId = szYdCrnSchId;
					}

					// RT일경우 열로 Bed Index 가져온다
                    if ("RT".equals(ydUtils.substr(szYdUpWoLoc, 2, 2))) {
    		        	iBedIdx = this.getBedIdx(arrYdLoc, szYdUpWoLoc);
    		        	iLyrIdx = Integer.parseInt(szYdUpWoLayer) - 1;										// 적치단 	Index (0~5)
                    } else {
    		        	iBedIdx = Integer.parseInt(szYdUpWoBed)   - 1;										// 적치베드 	Index (0~2)
    		        	iLyrIdx = Integer.parseInt(szYdUpWoLayer) - 1;										// 적치단 	Index (0~5)
                    }
                    
					if ((iBedIdx >= 0 && iBedIdx < 3) && (iLyrIdx >= 0 && iLyrIdx < 5)) {

				    	if ("".equals(arrLayer[iLyrIdx][iBedIdx])) {
						    arrSchId[iLyrIdx][iBedIdx] = szYdCrnSchId;		// 스케쥴코드
					    	arrStlNo[iLyrIdx][iBedIdx] = szStlNo;			// 재료번호
					    	arrYdLoc[iLyrIdx][iBedIdx] = szYdUpWoLoc;		// 재료적치열
					    	arrLayer[iLyrIdx][iBedIdx] = szYdUpWoLayer;		// 재료적치단
				    	} else {
							szRtnMsg = "적치단 중복 :: " + (szYdUpWoLoc + "-" + szYdUpWoLayer);
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							return szRtnMsg;
				    	}

					} else {
						szRtnMsg = "적치단 ERROR :: " + (szYdUpWoLoc + "-" + szYdUpWoLayer);
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
					}

					//------------------------------------------------------------------------------------------------
		    		// 기존 크레인작업지시 조회 --> 작업예약 ID SET
					//------------------------------------------------------------------------------------------------
		        	recPara = JDTORecordFactory.getInstance().create();
		        	recPara.setField("YD_CRN_SCH_ID", 		szYdCrnSchId);

		        	rsResult  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		        	/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnSch 

		        	SELECT YD_EQP_ID                                    AS YD_EQP_ID
		        	      ,YD_CRN_SCH_ID                                AS YD_CRN_SCH_ID
		        	      ,REGISTER                                     AS REGISTER
		        	      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')        AS REG_DDTT
		        	      ,MODIFIER                                     AS MODIFIER
		        	      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')        AS MOD_DDTT
		        	      ,DEL_YN                                       AS DEL_YN
		        	      ,YD_WBOOK_ID                                  AS YD_WBOOK_ID
		        	      ,YD_EQP_ID                                    AS YD_EQP_ID
		        	      ,YD_GP                                        AS YD_GP
		        	      ,YD_BAY_GP                                    AS YD_BAY_GP
		        	      ,YD_SCH_CD                                    AS YD_SCH_CD
		        	      ,YD_SCH_ST_GP                                 AS YD_SCH_ST_GP
		        	      ,YD_SCH_REQ_GP                                AS YD_SCH_REQ_GP
		        	      ,YD_SCH_PRIOR                                 AS YD_SCH_PRIOR
		        	      ,YD_EQP_WRK_STAT                              AS YD_EQP_WRK_STAT
		        	      ,YD_WRK_PROG_STAT                             AS YD_WRK_PROG_STAT
		        	      ,TO_CHAR(YD_WBOOK_DT, 'YYYYMMDDHH24MISS')     AS YD_WBOOK_DT
		        	      ,TO_CHAR(YD_SCH_DT, 'YYYYMMDDHH24MISS')       AS YD_SCH_DT
		        	      ,TO_CHAR(YD_WORD_DT, 'YYYYMMDDHH24MISS')      AS YD_WORD_DT
		        	      ,TO_CHAR(YD_UP_CMPL_DT, 'YYYYMMDDHH24MISS')   AS YD_UP_CMPL_DT
		        	      ,TO_CHAR(YD_DN_CMPL_DT, 'YYYYMMDDHH24MISS')   AS YD_DN_CMPL_DT
		        	      ,YD_WRK_HDS_DD                                AS YD_WRK_HDS_DD
		        	      ,YD_WRK_DUTY                                  AS YD_WRK_DUTY
		        	      ,YD_WRK_PARTY                                 AS YD_WRK_PARTY
		        	      ,YD_MAIN_WRK_MTL_SH                           AS YD_MAIN_WRK_MTL_SH
		        	      ,YD_AID_WRK_MTL_SH                            AS YD_AID_WRK_MTL_SH
		        	      ,YD_AID_WRK_UPDN_GP                           AS YD_AID_WRK_UPDN_GP
		        	      ,YD_TO_LOC_DCSN_MTD                           AS YD_TO_LOC_DCSN_MTD
		        	      ,YD_TO_LOC_GUIDE                              AS YD_TO_LOC_GUIDE
		        	      ,YD_EQP_WRK_SH                                AS YD_EQP_WRK_SH
		        	      ,YD_EQP_WRK_WT                                AS YD_EQP_WRK_WT
		        	      ,YD_EQP_WRK_T                                 AS YD_EQP_WRK_T
		        	      ,YD_EQP_WRK_MAX_W                             AS YD_EQP_WRK_MAX_W
		        	      ,YD_EQP_WRK_MAX_L                             AS YD_EQP_WRK_MAX_L
		        	      ,YD_CRN_SB_CTL_H                              AS YD_CRN_SB_CTL_H
		        	      ,YD_CRN_GRAB_USE_RULE_ID                      AS YD_CRN_GRAB_USE_RULE_ID
		        	      ,YD_UP_WO_LOC                                 AS YD_UP_WO_LOC
		        	      ,YD_UP_WO_LAYER                               AS YD_UP_WO_LAYER
		        	      ,YD_UP_WO_LOC_XAXIS                           AS YD_UP_WO_LOC_XAXIS
		        	      ,YD_UP_WO_XAXIS_GAP_MAX                       AS YD_UP_WO_XAXIS_GAP_MAX
		        	      ,YD_UP_WO_XAXIS_GAP_MIN                       AS YD_UP_WO_XAXIS_GAP_MIN
		        	      ,YD_UP_WO_LOC_YAXIS                           AS YD_UP_WO_LOC_YAXIS
		        	      ,YD_UP_WO_LOC_YAXIS1                          AS YD_UP_WO_LOC_YAXIS1
		        	      ,YD_UP_WO_LOC_YAXIS2                          AS YD_UP_WO_LOC_YAXIS2
		        	      ,YD_UP_WO_YAXIS_GAP_MAX                       AS YD_UP_WO_YAXIS_GAP_MAX
		        	      ,YD_UP_WO_YAXIS_GAP_MIN                       AS YD_UP_WO_YAXIS_GAP_MIN
		        	      ,YD_UP_WO_LOC_ZAXIS                           AS YD_UP_WO_LOC_ZAXIS
		        	      ,YD_UP_WO_ZAXIS_GAP_MAX                       AS YD_UP_WO_ZAXIS_GAP_MAX
		        	      ,YD_UP_WO_ZAXIS_GAP_MIN                       AS YD_UP_WO_ZAXIS_GAP_MIN
		        	      ,YD_DN_WO_LOC                                 AS YD_DN_WO_LOC
		        	      ,YD_DN_WO_LAYER                               AS YD_DN_WO_LAYER
		        	      ,YD_DN_WO_LOC_XAXIS                           AS YD_DN_WO_LOC_XAXIS
		        	      ,YD_DN_WO_XAXIS_GAP_MAX                       AS YD_DN_WO_XAXIS_GAP_MAX
		        	      ,YD_DN_WO_XAXIS_GAP_MIN                       AS YD_DN_WO_XAXIS_GAP_MIN
		        	      ,YD_DN_WO_LOC_YAXIS                           AS YD_DN_WO_LOC_YAXIS
		        	      ,YD_DN_WO_LOC_YAXIS1                          AS YD_DN_WO_LOC_YAXIS1
		        	      ,YD_DN_WO_LOC_YAXIS2                          AS YD_DN_WO_LOC_YAXIS2
		        	      ,YD_DN_WO_YAXIS_GAP_MAX                       AS YD_DN_WO_YAXIS_GAP_MAX
		        	      ,YD_DN_WO_YAXIS_GAP_MIN                       AS YD_DN_WO_YAXIS_GAP_MIN
		        	      ,YD_DN_WO_LOC_ZAXIS                           AS YD_DN_WO_LOC_ZAXIS
		        	      ,YD_DN_WO_ZAXIS_GAP_MAX                       AS YD_DN_WO_ZAXIS_GAP_MAX
		        	      ,YD_DN_WO_ZAXIS_GAP_MIN                       AS YD_DN_WO_ZAXIS_GAP_MIN
		        	      ,YD_UP_WR_LOC                                 AS YD_UP_WR_LOC
		        	      ,YD_UP_WR_LAYER                               AS YD_UP_WR_LAYER
		        	      ,YD_UP_WRK_ACT_GP                             AS YD_UP_WRK_ACT_GP
		        	      ,YD_UP_WR_XAXIS                               AS YD_UP_WR_XAXIS
		        	      ,YD_UP_WR_YAXIS                               AS YD_UP_WR_YAXIS
		        	      ,YD_UP_WR_YAXIS1                              AS YD_UP_WR_YAXIS1
		        	      ,YD_UP_WR_YAXIS2                              AS YD_UP_WR_YAXIS2
		        	      ,YD_UP_WR_ZAXIS                               AS YD_UP_WR_ZAXIS
		        	      ,YD_DN_WR_LOC                                 AS YD_DN_WR_LOC
		        	      ,YD_DN_WR_LAYER                               AS YD_DN_WR_LAYER
		        	      ,YD_DN_WRK_ACT_GP                             AS YD_DN_WRK_ACT_GP
		        	      ,YD_DN_WR_XAXIS                               AS YD_DN_WR_XAXIS
		        	      ,YD_DN_WR_YAXIS                               AS YD_DN_WR_YAXIS
		        	      ,YD_DN_WR_YAXIS1                              AS YD_DN_WR_YAXIS1
		        	      ,YD_DN_WR_YAXIS2                              AS YD_DN_WR_YAXIS2
		        	      ,YD_DN_WR_ZAXIS                               AS YD_DN_WR_ZAXIS
		        	      ,(SELECT MAX(S.YD_AID_WRK_YN) FROM TB_YD_CRNWRKMTL S WHERE S.YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YD_AID_WRK_YN
		        	      --- 최상단 재료번호
		        	      ,(SELECT MAX(S.STL_NO)
		        	          FROM TB_YD_CRNWRKMTL S
		        	         WHERE S.YD_CRN_SCH_ID = A.YD_CRN_SCH_ID
		        	           AND S.YD_STK_LYR_NO = (SELECT MAX(T.YD_STK_LYR_NO) FROM TB_YD_CRNWRKMTL T WHERE T.YD_CRN_SCH_ID = A.YD_CRN_SCH_ID AND T.DEL_YN = 'N')
		        	       )                                            AS TOP_STL_NO
		        	      -- 최하단 재료번호
		        	      ,(SELECT MAX(S.STL_NO)
		        	          FROM TB_YD_CRNWRKMTL S
		        	         WHERE S.YD_CRN_SCH_ID = A.YD_CRN_SCH_ID
		        	           AND S.YD_STK_LYR_NO = (SELECT MIN(T.YD_STK_LYR_NO) FROM TB_YD_CRNWRKMTL T WHERE T.YD_CRN_SCH_ID = A.YD_CRN_SCH_ID AND T.DEL_YN = 'N')
		        	       )                                            AS LAST_STL_NO

		        	  FROM TB_YD_CRNSCH A
		        	 WHERE A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
		        	 ORDER BY YD_CRN_SCH_ID
		        	 */
		        	intRtnVal = ydCrnSchDao.getYdCrnSch(recPara, rsResult);
		        	if (intRtnVal <= 0) {
						szRtnMsg = "기존 크레인작업지시 조회 오류 ::" + Integer.toString(intRtnVal);
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
		        	}
		        	rsResult.first();
		        	recSch = rsResult.getRecord();
		        	arrWbookId[iLyrIdx][iBedIdx] = ydDaoUtils.paraRecChkNull(recSch, "YD_WBOOK_ID");
		        	szYdAidWrkYn = ydDaoUtils.paraRecChkNull(recSch, "YD_AID_WRK_YN", "N");
		        	if ("".equals(szSaveAidWrkYn)) {
		        		szSaveAidWrkYn = szYdAidWrkYn;
		        	}
		        	if (!szSaveAidWrkYn.equals(szYdAidWrkYn)) {
						szRtnMsg = "(보조작업,주작업)을 파일링/횡작업 불가!!";
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
		        	}

		        	// 스케쥴 코드 체크하여 RT일때는 파일링/횡작업 안되도록 처리
		        	szYdSchCd 	= ydDaoUtils.paraRecChkNull(recSch, "YD_SCH_CD");
		        	szYdUpWoLoc = ydDaoUtils.paraRecChkNull(recSch, "YD_UP_WO_LOC");
		        	szYdDnWoLoc = ydDaoUtils.paraRecChkNull(recSch, "YD_DN_WO_LOC");
		    		szYdWrkStat = ydDaoUtils.paraRecChkNull(recSch, "YD_WRK_PROG_STAT");

					szMsg = "["+szOperationName+"] 파일링/횡작업 확인 >>>> 스케쥴코드::" + szYdSchCd + ", 권상위치::" + szYdUpWoLoc + ", 권하위치::" + szYdDnWoLoc + ", 보조작업::" + szYdAidWrkYn + ", 작업상태::" + szYdWrkStat;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

		        	//작업진행상태 체크(2, 3) 2,3이 아니면 에러 메시지 출력후 리턴
			        if ("2".equals(szYdWrkStat) || "3".equals(szYdWrkStat) || "4".equals(szYdWrkStat)) {
						szRtnMsg = "대기/명령선택 상태에서만 작업가능!!" + szYdWrkStat;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
			        }

					
		        	if ("".equals(szOldYdWbookId)) {
			        	recWbook = rsResult.getRecord();		// 작업예약 등록을 위해 Record Save
		        		szOldYdWbookId = ydDaoUtils.paraRecChkNull(recWbook, "YD_WBOOK_ID");
		        	}

		        	// TO위치에 따라 열길이 체크 (횡작업, 멀티작업 일때만 체크)
		        	if (!"XX010101".equals(szYdDnWoLoc) && !"XXYY0101".equals(szYdDnWoLoc) && !"P".equals(szYdPilingGp)) {

		        		szYdStkColGp  = ydUtils.substr(szYdDnWoLoc, 0, 6);		// 적치열구분
		        		szYdStkSpanGp = ydUtils.substr(szYdDnWoLoc, 2, 2);		// 스판구분

						// 권하지시위치 체크
			        	rsResult  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			        	recPara   = JDTORecordFactory.getInstance().create();
			        	recPara.setField("YD_STK_COL_GP", 		szYdStkColGp);
			        	recPara.setField("YD_STK_BED_NO", 		szYdUpWoBed);
			        	
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
			        	intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsResult);
			        	if (intRtnVal <= 0) {
							szRtnMsg = "TO BED정보 조회 오류! [" + szYdUpWoBed + "]";
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							return szRtnMsg;
			        	}

		        		if ("BC".equals(szYdStkSpanGp) ||"BS".equals(szYdStkSpanGp) || "CN".equals(szYdStkSpanGp) || "CB".equals(szYdStkSpanGp) || "RT".equals(szYdStkSpanGp)) {

							szMsg = "["+szOperationName+"] 설비일때 횡작업일때 열길이 체크 SKIP :: " + szYdStkSpanGp;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		        		} else {
				        	//------------------------------------------------------------------------------------------------
							// 열정보 조회 (횡작업시 길이 체크)
							//------------------------------------------------------------------------------------------------
							rsResult = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				        	recPara  = JDTORecordFactory.getInstance().create();
							recPara.setField("YD_STK_COL_GP", szYdStkColGp);
							
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
							intRtnVal = ydStkColDao.getYdStkcol(recPara, rsResult);
							if (intRtnVal < 1) {
								szRtnMsg = "TO 위치 길이 체크 오류! [" + szYdStkColGp + "]";
								szMsg    = "["+szOperationName+"] " + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
								return szRtnMsg;
							}
				        	rsResult.first();
							recCol = rsResult.getRecord();

							if (ydDaoUtils.paraRecChkNullInt(recCol, "YD_STK_COL_L") > iYdStkColL) {
								iYdStkColL = ydDaoUtils.paraRecChkNullInt(recCol, "YD_STK_COL_L");
							}

							szMsg = "["+szOperationName+"] 횡작업일때 열길이 조회 >>>> TO위치 :: " + szYdDnWoLoc + ", 결과 ::" + iYdStkColL;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		        		}
		        	}

		        	//------------------------------------------------------------------------------------------------
					// 재료정보 조회
					//------------------------------------------------------------------------------------------------
					rsResult = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		        	recPara  = JDTORecordFactory.getInstance().create();
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
					intRtnVal = ydStockDao.getYdStock(recPara, rsResult);
		        	if (intRtnVal <= 0) {
						szRtnMsg = "재료정보 조회 오류 .. 재료번호::" + szStlNo;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
		        	}
		        	rsResult.first();
		        	recMtl  = rsResult.getRecord();

		        	iSumWrkWt = iSumWrkWt + ydDaoUtils.paraRecChkNullInt(recMtl, "YD_MTL_WT");
		        	if (ydDaoUtils.paraRecChkNullDouble(recMtl, "YD_MTL_T") > dArrWrkT[iLyrIdx]) {
		        		dArrWrkT[iLyrIdx] = ydDaoUtils.paraRecChkNullDouble(recMtl, "YD_MTL_T");
		        	}

		        	iMtlL = ydDaoUtils.paraRecChkNullInt(recMtl, "YD_MTL_L");
		        	if (iMtlL > iYdStkColL) {
		        		iMtlL = iYdStkColL;
		        	}

					if (!"P".equals(szYdPilingGp)) {		// 횡작업,멀티작업 일때
						iArrWrkLen[iLyrIdx] = iArrWrkLen[iLyrIdx]  + iMtlL;
					} else {
						if (iArrWrkLen[iLyrIdx] < iMtlL) {
							iArrWrkLen[iLyrIdx] = iMtlL;
						}
					}
					
				}
			}

			if (iYdStkColL == 0) {
				iYdStkColL = 25000;
			}

        	dSumWrkT	= 0;
        	iSumWrkLen	= 0;

			for(int kk=0; kk<5; kk++) {
				dSumWrkT = dSumWrkT + dArrWrkT[kk];
				if (iArrWrkLen[kk] > iSumWrkLen) {
					iSumWrkLen = iArrWrkLen[kk];
				}
			}

			szMsg = "["+szOperationName+"] >>>> 파일링/횡작업 재료 정보 >>>> 길이 : " + iArrWrkLen + ", 두께 : " + dArrWrkT + ", 중량 : " + iSumWrkWt;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			szMsg = "["+szOperationName+"] >>>> 파일링/횡작업 재료 계산 >>>> 길이 : " + iSumWrkLen + ", 두께 : " + dSumWrkT + ", 중량 : " + iSumWrkWt;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			// 재료길이 25M 넘는 재료는 횡작업 불가 [횡작업,멀티작업 일때만 체크]
			if (!"P".equals(szYdPilingGp) && iSumWrkLen > iYdStkColL) {
				szRtnMsg = "재료길이 합이 "+(iYdStkColL/1000)+"M 넘어 횡작업 불가! [" + iSumWrkLen + "]";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}
			// 재료두께 25T 넘는 재료는 파일링 불가 [파일링,멀티작업 일때만 체크]
			if (!"H".equals(szYdPilingGp) && dSumWrkT > 50) {
				szRtnMsg = "재료두께 합이 50t 넘어 파일링작업 불가! [" + dSumWrkT + "]";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}
			
			if("PECRE2".equals(szYdEqpId)) {
				// 35톤 넘는 재료는 불가
				if (iSumWrkWt > 35000) {
					szRtnMsg = "재료중량 합이 35톤 넘어 작업 불가! [" + iSumWrkWt + "]";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
			} else if("PFCRF2".equals(szYdEqpId)){  //21호기(PFCRF2) 제한 중량 기준 변경 25T->30T 2021.12.06 박종호. 허남웅 책임 요청사항.
				// 30톤 넘는 재료는 불가
				if (iSumWrkWt > 30000) {
					szRtnMsg = "재료중량 합이 30톤 넘어 작업 불가! [" + iSumWrkWt + "]";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
			}else {
				// 25톤 넘는 재료는 불가
				if (iSumWrkWt > 25000) {
					szRtnMsg = "재료중량 합이 25톤 넘어 작업 불가! [" + iSumWrkWt + "]";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
			}

			szMsg = "["+szOperationName+"] >>>> 크레인스케쥴 등록 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//------------------------------------------------------------------------------------------------
			// 1. 크레인 작업예약 생성 [신규 파일링 작업예약]
			//------------------------------------------------------------------------------------------------
			// 1.1. 작업예약ID를 할당받는다 (보조작업일때는 작업예약 생성 안함)
			if ("Y".equals(szYdAidWrkYn)) {
				szNewYdWbookId = szOldYdWbookId;
			} else {
				szNewYdWbookId = ydWrkbookDao.getSeqId();		// intGp = 9
	    		if ("".equals(szNewYdWbookId)) {
					szRtnMsg = "작업예약 Id를 생성하지 못했습니다.";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
	    		}

				// 1.2. 작업예약생성
	    		recWbook.setField("YD_WBOOK_ID", 	szNewYdWbookId);
	    		recWbook.setField("REGISTER",	    szModifier);   			// 등록자
	    		recWbook.setField("MODIFIER",	    szModifier);			// 수정자
	    		
	    		/*com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.insYdWrkbook*/
	    		intRtnVal = ydWrkbookDao.insYdWrkbook(recWbook);
	        	if (intRtnVal <= 0) {
					szRtnMsg = "신규 작업예약 생성 오류 ::" + Integer.toString(intRtnVal);
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
	        	}
			}

			//------------------------------------------------------------------------------------------------
			// 2. 크레인 작업지시  생성 [신규 파일링 작업지시]
			//------------------------------------------------------------------------------------------------
			// 2.1. 크레인스케줄ID를 할당받는다
			szNewYdCrnSchId = ydCrnSchDao.getSeqId();		// intGp = 9
    		if ("".equals(szNewYdCrnSchId)) {
				szRtnMsg = "크레인스케줄 Id를 생성하지 못했습니다.";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
    		}

			//------------------------------------------------------------------------------------------------
    		// 2.2. 기존 크레인작업지시 조회
			//------------------------------------------------------------------------------------------------
        	recPara = JDTORecordFactory.getInstance().create();
        	recPara.setField("YD_CRN_SCH_ID", 		szOldYdCrnSchId);

        	rsResult  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
        	/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnSch 

        	SELECT YD_EQP_ID                                    AS YD_EQP_ID
        	      ,YD_CRN_SCH_ID                                AS YD_CRN_SCH_ID
        	      ,REGISTER                                     AS REGISTER
        	      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')        AS REG_DDTT
        	      ,MODIFIER                                     AS MODIFIER
        	      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')        AS MOD_DDTT
        	      ,DEL_YN                                       AS DEL_YN
        	      ,YD_WBOOK_ID                                  AS YD_WBOOK_ID
        	      ,YD_EQP_ID                                    AS YD_EQP_ID
        	      ,YD_GP                                        AS YD_GP
        	      ,YD_BAY_GP                                    AS YD_BAY_GP
        	      ,YD_SCH_CD                                    AS YD_SCH_CD
        	      ,YD_SCH_ST_GP                                 AS YD_SCH_ST_GP
        	      ,YD_SCH_REQ_GP                                AS YD_SCH_REQ_GP
        	      ,YD_SCH_PRIOR                                 AS YD_SCH_PRIOR
        	      ,YD_EQP_WRK_STAT                              AS YD_EQP_WRK_STAT
        	      ,YD_WRK_PROG_STAT                             AS YD_WRK_PROG_STAT
        	      ,TO_CHAR(YD_WBOOK_DT, 'YYYYMMDDHH24MISS')     AS YD_WBOOK_DT
        	      ,TO_CHAR(YD_SCH_DT, 'YYYYMMDDHH24MISS')       AS YD_SCH_DT
        	      ,TO_CHAR(YD_WORD_DT, 'YYYYMMDDHH24MISS')      AS YD_WORD_DT
        	      ,TO_CHAR(YD_UP_CMPL_DT, 'YYYYMMDDHH24MISS')   AS YD_UP_CMPL_DT
        	      ,TO_CHAR(YD_DN_CMPL_DT, 'YYYYMMDDHH24MISS')   AS YD_DN_CMPL_DT
        	      ,YD_WRK_HDS_DD                                AS YD_WRK_HDS_DD
        	      ,YD_WRK_DUTY                                  AS YD_WRK_DUTY
        	      ,YD_WRK_PARTY                                 AS YD_WRK_PARTY
        	      ,YD_MAIN_WRK_MTL_SH                           AS YD_MAIN_WRK_MTL_SH
        	      ,YD_AID_WRK_MTL_SH                            AS YD_AID_WRK_MTL_SH
        	      ,YD_AID_WRK_UPDN_GP                           AS YD_AID_WRK_UPDN_GP
        	      ,YD_TO_LOC_DCSN_MTD                           AS YD_TO_LOC_DCSN_MTD
        	      ,YD_TO_LOC_GUIDE                              AS YD_TO_LOC_GUIDE
        	      ,YD_EQP_WRK_SH                                AS YD_EQP_WRK_SH
        	      ,YD_EQP_WRK_WT                                AS YD_EQP_WRK_WT
        	      ,YD_EQP_WRK_T                                 AS YD_EQP_WRK_T
        	      ,YD_EQP_WRK_MAX_W                             AS YD_EQP_WRK_MAX_W
        	      ,YD_EQP_WRK_MAX_L                             AS YD_EQP_WRK_MAX_L
        	      ,YD_CRN_SB_CTL_H                              AS YD_CRN_SB_CTL_H
        	      ,YD_CRN_GRAB_USE_RULE_ID                      AS YD_CRN_GRAB_USE_RULE_ID
        	      ,YD_UP_WO_LOC                                 AS YD_UP_WO_LOC
        	      ,YD_UP_WO_LAYER                               AS YD_UP_WO_LAYER
        	      ,YD_UP_WO_LOC_XAXIS                           AS YD_UP_WO_LOC_XAXIS
        	      ,YD_UP_WO_XAXIS_GAP_MAX                       AS YD_UP_WO_XAXIS_GAP_MAX
        	      ,YD_UP_WO_XAXIS_GAP_MIN                       AS YD_UP_WO_XAXIS_GAP_MIN
        	      ,YD_UP_WO_LOC_YAXIS                           AS YD_UP_WO_LOC_YAXIS
        	      ,YD_UP_WO_LOC_YAXIS1                          AS YD_UP_WO_LOC_YAXIS1
        	      ,YD_UP_WO_LOC_YAXIS2                          AS YD_UP_WO_LOC_YAXIS2
        	      ,YD_UP_WO_YAXIS_GAP_MAX                       AS YD_UP_WO_YAXIS_GAP_MAX
        	      ,YD_UP_WO_YAXIS_GAP_MIN                       AS YD_UP_WO_YAXIS_GAP_MIN
        	      ,YD_UP_WO_LOC_ZAXIS                           AS YD_UP_WO_LOC_ZAXIS
        	      ,YD_UP_WO_ZAXIS_GAP_MAX                       AS YD_UP_WO_ZAXIS_GAP_MAX
        	      ,YD_UP_WO_ZAXIS_GAP_MIN                       AS YD_UP_WO_ZAXIS_GAP_MIN
        	      ,YD_DN_WO_LOC                                 AS YD_DN_WO_LOC
        	      ,YD_DN_WO_LAYER                               AS YD_DN_WO_LAYER
        	      ,YD_DN_WO_LOC_XAXIS                           AS YD_DN_WO_LOC_XAXIS
        	      ,YD_DN_WO_XAXIS_GAP_MAX                       AS YD_DN_WO_XAXIS_GAP_MAX
        	      ,YD_DN_WO_XAXIS_GAP_MIN                       AS YD_DN_WO_XAXIS_GAP_MIN
        	      ,YD_DN_WO_LOC_YAXIS                           AS YD_DN_WO_LOC_YAXIS
        	      ,YD_DN_WO_LOC_YAXIS1                          AS YD_DN_WO_LOC_YAXIS1
        	      ,YD_DN_WO_LOC_YAXIS2                          AS YD_DN_WO_LOC_YAXIS2
        	      ,YD_DN_WO_YAXIS_GAP_MAX                       AS YD_DN_WO_YAXIS_GAP_MAX
        	      ,YD_DN_WO_YAXIS_GAP_MIN                       AS YD_DN_WO_YAXIS_GAP_MIN
        	      ,YD_DN_WO_LOC_ZAXIS                           AS YD_DN_WO_LOC_ZAXIS
        	      ,YD_DN_WO_ZAXIS_GAP_MAX                       AS YD_DN_WO_ZAXIS_GAP_MAX
        	      ,YD_DN_WO_ZAXIS_GAP_MIN                       AS YD_DN_WO_ZAXIS_GAP_MIN
        	      ,YD_UP_WR_LOC                                 AS YD_UP_WR_LOC
        	      ,YD_UP_WR_LAYER                               AS YD_UP_WR_LAYER
        	      ,YD_UP_WRK_ACT_GP                             AS YD_UP_WRK_ACT_GP
        	      ,YD_UP_WR_XAXIS                               AS YD_UP_WR_XAXIS
        	      ,YD_UP_WR_YAXIS                               AS YD_UP_WR_YAXIS
        	      ,YD_UP_WR_YAXIS1                              AS YD_UP_WR_YAXIS1
        	      ,YD_UP_WR_YAXIS2                              AS YD_UP_WR_YAXIS2
        	      ,YD_UP_WR_ZAXIS                               AS YD_UP_WR_ZAXIS
        	      ,YD_DN_WR_LOC                                 AS YD_DN_WR_LOC
        	      ,YD_DN_WR_LAYER                               AS YD_DN_WR_LAYER
        	      ,YD_DN_WRK_ACT_GP                             AS YD_DN_WRK_ACT_GP
        	      ,YD_DN_WR_XAXIS                               AS YD_DN_WR_XAXIS
        	      ,YD_DN_WR_YAXIS                               AS YD_DN_WR_YAXIS
        	      ,YD_DN_WR_YAXIS1                              AS YD_DN_WR_YAXIS1
        	      ,YD_DN_WR_YAXIS2                              AS YD_DN_WR_YAXIS2
        	      ,YD_DN_WR_ZAXIS                               AS YD_DN_WR_ZAXIS
        	      ,(SELECT MAX(S.YD_AID_WRK_YN) FROM TB_YD_CRNWRKMTL S WHERE S.YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YD_AID_WRK_YN
        	      --- 최상단 재료번호
        	      ,(SELECT MAX(S.STL_NO)
        	          FROM TB_YD_CRNWRKMTL S
        	         WHERE S.YD_CRN_SCH_ID = A.YD_CRN_SCH_ID
        	           AND S.YD_STK_LYR_NO = (SELECT MAX(T.YD_STK_LYR_NO) FROM TB_YD_CRNWRKMTL T WHERE T.YD_CRN_SCH_ID = A.YD_CRN_SCH_ID AND T.DEL_YN = 'N')
        	       )                                            AS TOP_STL_NO
        	      -- 최하단 재료번호
        	      ,(SELECT MAX(S.STL_NO)
        	          FROM TB_YD_CRNWRKMTL S
        	         WHERE S.YD_CRN_SCH_ID = A.YD_CRN_SCH_ID
        	           AND S.YD_STK_LYR_NO = (SELECT MIN(T.YD_STK_LYR_NO) FROM TB_YD_CRNWRKMTL T WHERE T.YD_CRN_SCH_ID = A.YD_CRN_SCH_ID AND T.DEL_YN = 'N')
        	       )                                            AS LAST_STL_NO

        	  FROM TB_YD_CRNSCH A
        	 WHERE A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
        	 ORDER BY YD_CRN_SCH_ID
        	 */
        	intRtnVal = ydCrnSchDao.getYdCrnSch(recPara, rsResult);
        	if (intRtnVal <= 0) {
				szRtnMsg = "기존 크레인작업지시 조회 오류 ::" + Integer.toString(intRtnVal);
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
        	}
        	rsResult.first();
        	recSch = rsResult.getRecord();

        	szYdSchCd = ydDaoUtils.paraRecChkNull(recSch, "YD_SCH_CD");

			szMsg = "["+szOperationName+"] DATA 확인 >>>>" + rsResult.size() + ">>>>" + recSch.toString();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	recSch.setField("YD_CRN_SCH_ID", 		szNewYdCrnSchId);								// 신규 야드크레인스케쥴ID
        	recSch.setField("YD_WBOOK_ID", 			szNewYdWbookId);								// 신규 작업예약ID
        	recSch.setField("REGISTER",	     		szModifier);   									// 등록자
        	recSch.setField("MODIFIER",	        	szModifier);									// 수정자
        	recSch.setField("YD_EQP_ID",			szYdEqpId);										// 야드설비ID
        	recSch.setField("YD_MAIN_WRK_MTL_SH",	szYdPilingSh);									// 야드주작업재료매수
        	recSch.setField("YD_EQP_WRK_SH",	    szYdPilingSh);									// 야드설비작업매수
        	recSch.setField("YD_EQP_WRK_WT",	    Integer.toString(iSumWrkWt));					// 야드설비작업중량
        	recSch.setField("YD_EQP_WRK_T",	        Double.toString(dSumWrkT));						// 야드설비작업총두께
        	recSch.setField("YD_EQP_WRK_L",	        Double.toString(iSumWrkLen));					// 야드설비작업총길이
        	recSch.setField("YD_WRK_PROG_STAT", 	JPlateYdConst.YD_EQP_STAT_DN_WO);				// 권하지시
        	recSch.setField("YD_UP_WR_LOC",			recSch.getField("YD_UP_WO_LOC"));				// 야드권상실적위치
        	recSch.setField("YD_UP_WR_LAYER",		recSch.getField("YD_UP_WO_LAYER"));				// 야드권상실적단
        //	recSch.setField("YD_UP_WRK_ACT_GP",		"M");											// 야드권상작업수행구분
        	recSch.setField("YD_UP_WRK_ACT_GP",	    szYdPilingGp);									// 파일링작업구분 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)
        	recSch.setField("YD_UP_WR_XAXIS",		recSch.getField("YD_UP_WO_XAXIS"));				// 야드권상실적X축
        	recSch.setField("YD_UP_WR_YAXIS",		recSch.getField("YD_UP_WO_YAXIS"));				// 야드권상실적Y축
        	recSch.setField("YD_UP_WR_YAXIS1",		recSch.getField("YD_UP_WO_YAXIS1"));			// 야드권상실적Y축1
        	recSch.setField("YD_UP_WR_YAXIS2",		recSch.getField("YD_UP_WO_YAXIS2"));			// 야드권상실적Y축2
        	recSch.setField("YD_UP_WR_ZAXIS",		recSch.getField("YD_UP_WO_ZAXIS"));				// 야드권상실적Z축
        	recSch.setField("YD_WORD_DT", 			JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));	// 명령선택일시
        	recSch.setField("YD_UP_CMPL_DT",		JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));	// 권상완료일시

			//------------------------------------------------------------------------------------------------
    		// 2.3. 크레인작업지시 등록
			//------------------------------------------------------------------------------------------------
        	/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.insYdCrnsch 

        	INSERT INTO TB_YD_CRNSCH
        	(
        	     YD_CRN_SCH_ID
        	    ,REGISTER
        	    ,REG_DDTT
        	    ,MODIFIER
        	    ,MOD_DDTT
        	    ,DEL_YN
        	    ,YD_WBOOK_ID
        	    ,YD_EQP_ID
        	    ,YD_GP
        	    ,YD_BAY_GP
        	    ,YD_SCH_CD
        	    ,YD_SCH_ST_GP
        	    ,YD_SCH_REQ_GP
        	    ,YD_SCH_PRIOR
        	    ,YD_EQP_WRK_STAT
        	    ,YD_WRK_PROG_STAT
        	    ,YD_WBOOK_DT
        	    ,YD_SCH_DT
        	    ,YD_WORD_DT
        	    ,YD_UP_CMPL_DT
        	    ,YD_DN_CMPL_DT
        	    ,YD_WRK_HDS_DD
        	    ,YD_WRK_DUTY
        	    ,YD_WRK_PARTY
        	    ,YD_MAIN_WRK_MTL_SH
        	    ,YD_AID_WRK_MTL_SH
        	    ,YD_AID_WRK_UPDN_GP
        	    ,YD_TO_LOC_DCSN_MTD
        	    ,YD_TO_LOC_GUIDE
        	    ,YD_EQP_WRK_SH
        	    ,YD_EQP_WRK_WT
        	    ,YD_EQP_WRK_T
        	    ,YD_EQP_WRK_MAX_W
        	    ,YD_EQP_WRK_MAX_L
        	    ,YD_CRN_SB_CTL_H
        	    ,YD_CRN_GRAB_USE_RULE_ID
        	    ,YD_UP_WO_LOC
        	    ,YD_UP_WO_LAYER
        	    ,YD_UP_WO_LOC_XAXIS
        	    ,YD_UP_WO_XAXIS_GAP_MAX
        	    ,YD_UP_WO_XAXIS_GAP_MIN
        	    ,YD_UP_WO_LOC_YAXIS
        	    ,YD_UP_WO_LOC_YAXIS1
        	    ,YD_UP_WO_LOC_YAXIS2
        	    ,YD_UP_WO_YAXIS_GAP_MAX
        	    ,YD_UP_WO_YAXIS_GAP_MIN
        	    ,YD_UP_WO_LOC_ZAXIS
        	    ,YD_UP_WO_ZAXIS_GAP_MAX
        	    ,YD_UP_WO_ZAXIS_GAP_MIN
        	    ,YD_DN_WO_LOC
        	    ,YD_DN_WO_LAYER
        	    ,YD_DN_WO_LOC_XAXIS
        	    ,YD_DN_WO_XAXIS_GAP_MAX
        	    ,YD_DN_WO_XAXIS_GAP_MIN
        	    ,YD_DN_WO_LOC_YAXIS
        	    ,YD_DN_WO_LOC_YAXIS1
        	    ,YD_DN_WO_LOC_YAXIS2
        	    ,YD_DN_WO_YAXIS_GAP_MAX
        	    ,YD_DN_WO_YAXIS_GAP_MIN
        	    ,YD_DN_WO_LOC_ZAXIS
        	    ,YD_DN_WO_ZAXIS_GAP_MAX
        	    ,YD_DN_WO_ZAXIS_GAP_MIN
        	    ,YD_UP_WR_LOC
        	    ,YD_UP_WR_LAYER
        	    ,YD_UP_WRK_ACT_GP
        	    ,YD_UP_WR_XAXIS
        	    ,YD_UP_WR_YAXIS
        	    ,YD_UP_WR_YAXIS1
        	    ,YD_UP_WR_YAXIS2
        	    ,YD_UP_WR_ZAXIS
        	    ,YD_DN_WR_LOC
        	    ,YD_DN_WR_LAYER
        	    ,YD_DN_WRK_ACT_GP
        	    ,YD_DN_WR_XAXIS
        	    ,YD_DN_WR_YAXIS
        	    ,YD_DN_WR_YAXIS1
        	    ,YD_DN_WR_YAXIS2
        	    ,YD_DN_WR_ZAXIS
        	)
        	VALUES
        	(
        	     :V_YD_CRN_SCH_ID
        	    ,:V_REGISTER
        	    ,SYSDATE
        	    ,:V_REGISTER
        	    ,SYSDATE
        	    ,'N'
        	    ,:V_YD_WBOOK_ID
        	    ,:V_YD_EQP_ID
        	    ,:V_YD_GP
        	    ,:V_YD_BAY_GP
        	    ,:V_YD_SCH_CD
        	    ,:V_YD_SCH_ST_GP
        	    ,:V_YD_SCH_REQ_GP
        	    ,:V_YD_SCH_PRIOR
        	    ,:V_YD_EQP_WRK_STAT
        	    ,:V_YD_WRK_PROG_STAT
        	    ,TO_DATE(:V_YD_WBOOK_DT,'YYYYMMDDHH24MISS')
        	    ,SYSDATE
        	    ,TO_DATE(:V_YD_WORD_DT,'YYYYMMDDHH24MISS')
        	    ,TO_DATE(:V_YD_UP_CMPL_DT,'YYYYMMDDHH24MISS')
        	    ,TO_DATE(:V_YD_DN_CMPL_DT,'YYYYMMDDHH24MISS')
        	    ,TO_CHAR(SYSDATE - (6 / 24), 'YYYYMMDD')        -- 야드작업계상일자
        	    ,CASE WHEN TO_CHAR(SYSDATE,'HH24MISS') BETWEEN '000000' AND '065959' THEN '3'
        	          WHEN TO_CHAR(SYSDATE,'HH24MISS') BETWEEN '070000' AND '145959' THEN '1'
        	          WHEN TO_CHAR(SYSDATE,'HH24MISS') BETWEEN '150000' AND '225959' THEN '2'
        	          WHEN TO_CHAR(SYSDATE,'HH24MISS') BETWEEN '230000' AND '235959' THEN '3'
        	     END                                            -- 야드작업근
        	    ,:V_YD_WRK_PARTY
        	    ,:V_YD_MAIN_WRK_MTL_SH
        	    ,:V_YD_AID_WRK_MTL_SH
        	    ,:V_YD_AID_WRK_UPDN_GP
        	    ,:V_YD_TO_LOC_DCSN_MTD
        	    ,:V_YD_TO_LOC_GUIDE
        	    ,:V_YD_EQP_WRK_SH
        	    ,:V_YD_EQP_WRK_WT
        	    ,:V_YD_EQP_WRK_T
        	    ,:V_YD_EQP_WRK_MAX_W
        	    ,:V_YD_EQP_WRK_MAX_L
        	    ,:V_YD_CRN_SB_CTL_H
        	    ,:V_YD_CRN_GRAB_USE_RULE_ID
        	    ,:V_YD_UP_WO_LOC
        	    ,:V_YD_UP_WO_LAYER
        	    ,:V_YD_UP_WO_LOC_XAXIS
        	    ,:V_YD_UP_WO_XAXIS_GAP_MAX
        	    ,:V_YD_UP_WO_XAXIS_GAP_MIN
        	    ,:V_YD_UP_WO_LOC_YAXIS
        	    ,:V_YD_UP_WO_LOC_YAXIS1
        	    ,:V_YD_UP_WO_LOC_YAXIS2
        	    ,:V_YD_UP_WO_YAXIS_GAP_MAX
        	    ,:V_YD_UP_WO_YAXIS_GAP_MIN
        	    ,:V_YD_UP_WO_LOC_ZAXIS
        	    ,:V_YD_UP_WO_ZAXIS_GAP_MAX
        	    ,:V_YD_UP_WO_ZAXIS_GAP_MIN
        	    ,:V_YD_DN_WO_LOC
        	    ,:V_YD_DN_WO_LAYER
        	    ,:V_YD_DN_WO_LOC_XAXIS
        	    ,:V_YD_DN_WO_XAXIS_GAP_MAX
        	    ,:V_YD_DN_WO_XAXIS_GAP_MIN
        	    ,:V_YD_DN_WO_LOC_YAXIS
        	    ,:V_YD_DN_WO_LOC_YAXIS1
        	    ,:V_YD_DN_WO_LOC_YAXIS2
        	    ,:V_YD_DN_WO_YAXIS_GAP_MAX
        	    ,:V_YD_DN_WO_YAXIS_GAP_MIN
        	    ,:V_YD_DN_WO_LOC_ZAXIS
        	    ,:V_YD_DN_WO_ZAXIS_GAP_MAX
        	    ,:V_YD_DN_WO_ZAXIS_GAP_MIN
        	    ,:V_YD_UP_WR_LOC
        	    ,:V_YD_UP_WR_LAYER
        	    ,:V_YD_UP_WRK_ACT_GP
        	    ,:V_YD_UP_WR_XAXIS
        	    ,:V_YD_UP_WR_YAXIS
        	    ,:V_YD_UP_WR_YAXIS1
        	    ,:V_YD_UP_WR_YAXIS2
        	    ,:V_YD_UP_WR_ZAXIS
        	    ,:V_YD_DN_WR_LOC
        	    ,:V_YD_DN_WR_LAYER
        	    ,:V_YD_DN_WRK_ACT_GP
        	    ,:V_YD_DN_WR_XAXIS
        	    ,:V_YD_DN_WR_YAXIS
        	    ,:V_YD_DN_WR_YAXIS1
        	    ,:V_YD_DN_WR_YAXIS2
        	    ,:V_YD_DN_WR_ZAXIS
        	)
        	*/
        	intRtnVal = ydCrnSchDao.insYdCrnsch(recSch);
        	if (intRtnVal <= 0) {
				szRtnMsg = "기존 크레인작업지시 등록 오류 ::" + Integer.toString(intRtnVal);
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
        	}

        	// 크레인스케쥴의 권하위치로 SET
        	szYdDnWoLoc = ydDaoUtils.paraRecChkNull(recSch, "YD_DN_WO_LOC");

			szMsg = "["+szOperationName+"] >>>> 크레인작업재료 등록 시작 >>>> " + szYdDnWoLoc;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	iWrkCnt = 0;
			for(int ii=0; ii<5; ii++) {
				for(int jj=0; jj<3; jj++) {
					if (!"".equals(arrStlNo[ii][jj])) {
						//------------------------------------------------------------------------------------------------
			    		// 2.4. 크레인 작업재료 조회
						//------------------------------------------------------------------------------------------------
			        	recPara.setField("YD_CRN_SCH_ID", 	arrSchId[ii][jj]);
			        	recPara.setField("STL_NO", 			arrStlNo[ii][jj]);
			        	recPara.setField("YD_WBOOK_ID", 	arrWbookId[ii][jj]);

			        	rsResult  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			        	
			        	/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getYdCrnWrkMtl 

			        	SELECT 
			        	       YD_CRN_SCH_ID        -- 야드크레인스케쥴ID
			        	     , STL_NO               -- 재료번호
			        	     , REGISTER             -- 등록자
			        	     , REG_DDTT             -- 등록일시
			        	     , MOD_DDTT             -- 수정일시
			        	     , MODIFIER             -- 수정자
			        	     , DEL_YN               -- 삭제유무
			        	     , YD_AID_WRK_YN        -- 야드보조작업여부
			        	     , YD_STK_LYR_NO        -- 야드적치단번호
			        	     , YD_STK_LOT_TP        -- 야드산적LotType
			        	     , YD_STK_LOT_CD        -- 야드산적Lot코드
			        	     , HCR_GP               -- HCR구분
			        	     , STL_PROG_CD          -- 재료진도코드
			        	     , YD_MTL_ITEM          -- 야드재료품목
			        	     , YD_ROUTE_GP          -- 야드행선구분
			        	     , YD_TO_LOC_DCSN_MTD   --야드To위치결정방법
			        	  FROM TB_YD_CRNWRKMTL
			        	 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			        	   AND STL_NO = :V_STL_NO
			        	   */
			        	intRtnVal = ydCrnWrkMtlDao.getYdCrnWrkMtl(recPara, rsResult);
			        	if (intRtnVal <= 0) {
							szRtnMsg = "기존 크레인작업재료 조회 .. 재료번호 ::" + arrStlNo[ii][jj] + " >> " + Integer.toString(intRtnVal);
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							return szRtnMsg;
			        	}
			        	rsResult.first();
			        	recMtl = rsResult.getRecord();

						//------------------------------------------------------------------------------------------------
			    		// 2.5. 크레인작업재료 등록
						//------------------------------------------------------------------------------------------------
			        	recMtl.setField("YD_CRN_SCH_ID",	szNewYdCrnSchId);							// 야드크레인스케쥴ID
	        			recMtl.setField("STL_NO",			arrStlNo[ii][jj]);							// 재료번호
						recMtl.setField("MODIFIER",	        szModifier);								// 수정자
						recMtl.setField("YD_STK_LYR_NO",	arrLayer[ii][jj]);      					// 야드적치단번호
						recMtl.setField("YD_STK_LOT_TP",	ydUtils.substr(arrYdLoc[ii][jj], 6, 2));	// 권상위치 BED 번호
						recMtl.setField("YD_STK_LOT_CD", 	arrYdLoc[ii][jj]);							// 권상위치
						/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.insYdCrnWrkMtl 

						INSERT INTO TB_YD_CRNWRKMTL
						(
						       YD_CRN_SCH_ID
						      ,STL_NO
						      ,REGISTER
						      ,REG_DDTT
						      ,MODIFIER
						      ,MOD_DDTT
						      ,DEL_YN
						      ,YD_AID_WRK_YN
						      ,YD_STK_LYR_NO
						      ,YD_STK_LOT_TP
						      ,YD_STK_LOT_CD
						      ,HCR_GP
						      ,STL_PROG_CD
						      ,YD_MTL_ITEM
						      ,YD_ROUTE_GP
						      ,YD_TO_LOC_DCSN_MTD
						)
						VALUES 
						(
						       :V_YD_CRN_SCH_ID
						      ,:V_STL_NO
						      ,:V_REGISTER
						      ,SYSDATE
						      ,:V_REGISTER
						      ,SYSDATE
						      ,'N'
						      ,:V_YD_AID_WRK_YN
						      ,LPAD(:V_YD_STK_LYR_NO, 3, '0')
						      ,:V_YD_STK_LOT_TP
						      ,:V_YD_STK_LOT_CD
						      ,:V_HCR_GP
						      ,:V_STL_PROG_CD
						      ,:V_YD_MTL_ITEM
						      ,:V_YD_ROUTE_GP
						      ,:V_YD_TO_LOC_DCSN_MTD
						)
						*/
						intRtnVal = ydCrnWrkMtlDao.insYdCrnWrkMtl(recMtl);
			        	if (intRtnVal <= 0) {
							szRtnMsg = "기존 크레인작업재료 등록 오류 ::" + arrStlNo[ii][jj] + " >> " + Integer.toString(intRtnVal);
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							return szRtnMsg;
			        	}

						if ("N".equals(szYdAidWrkYn)) {		// 주작업일때만 작업예약 신규로 생성
							//------------------------------------------------------------------------------------------------
				        	// 2.6. 작업예약재료 조회
							//------------------------------------------------------------------------------------------------
				        	rsResult  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				        	   
				        	/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getYdCrnWrkMtl 

				        	SELECT 
				        	       YD_CRN_SCH_ID        -- 야드크레인스케쥴ID
				        	     , STL_NO               -- 재료번호
				        	     , REGISTER             -- 등록자
				        	     , REG_DDTT             -- 등록일시
				        	     , MOD_DDTT             -- 수정일시
				        	     , MODIFIER             -- 수정자
				        	     , DEL_YN               -- 삭제유무
				        	     , YD_AID_WRK_YN        -- 야드보조작업여부
				        	     , YD_STK_LYR_NO        -- 야드적치단번호
				        	     , YD_STK_LOT_TP        -- 야드산적LotType
				        	     , YD_STK_LOT_CD        -- 야드산적Lot코드
				        	     , HCR_GP               -- HCR구분
				        	     , STL_PROG_CD          -- 재료진도코드
				        	     , YD_MTL_ITEM          -- 야드재료품목
				        	     , YD_ROUTE_GP          -- 야드행선구분
				        	     , YD_TO_LOC_DCSN_MTD   --야드To위치결정방법
				        	  FROM TB_YD_CRNWRKMTL
				        	 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				        	   AND STL_NO = :V_STL_NO
				        	*/   
				        	intRtnVal = ydCrnWrkMtlDao.getYdCrnWrkMtl(recPara, rsResult);
				        	if (intRtnVal <= 0) {
								szRtnMsg = "기존 작업예약재료 조회 .. 재료번호 ::" + arrStlNo[ii][jj] + " >> " + Integer.toString(intRtnVal);
								szMsg    = "["+szOperationName+"] " + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
								return szRtnMsg;
				        	}
				        	rsResult.first();
				        	recMtl = rsResult.getRecord();

							//------------------------------------------------------------------------------------------------
				        	// 2.7. 작업예약재료 등록
							//------------------------------------------------------------------------------------------------
				        	recMtl.setField("YD_WBOOK_ID",		szNewYdWbookId);	// 작업예약ID
		        			recMtl.setField("STL_NO",			arrStlNo[ii][jj]);	// 재료번호
							recMtl.setField("MODIFIER",	        szModifier);		// 수정자

							intRtnVal = ydWrkbookMtlDao.insYdWrkbookMtl(recMtl);
				        	if (intRtnVal <= 0) {
								szRtnMsg = "기존 작업예약재료 등록 오류 ::" + arrStlNo[ii][jj] + " >> " + Integer.toString(intRtnVal);
								szMsg    = "["+szOperationName+"] " + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
								return szRtnMsg;
				        	}

							//------------------------------------------------------------------------------------------------
				        	// 2.8. 기존작업예약재료 삭제
							//------------------------------------------------------------------------------------------------
				        	recPara.setField("YD_WBOOK_ID", 	arrWbookId[ii][jj]);
				        	recPara.setField("STL_NO", 			arrStlNo[ii][jj]);

							intRtnVal = ydWrkbookMtlDao.deldWrkbookMtl(recPara);
				        	if (intRtnVal <= 0) {
								szRtnMsg = "기존 작업예약재료 삭제 오류 ::" + arrStlNo[ii][jj] + " >> " + Integer.toString(intRtnVal);
								szMsg    = "["+szOperationName+"] " + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							//	return szRtnMsg;
				        	}

							//------------------------------------------------------------------------------------------------
				        	// 2.9. 기존작업예약 삭제
							//------------------------------------------------------------------------------------------------
							intRtnVal = ydWrkbookDao.delYdWrkbook(recPara);
				        	if (intRtnVal <= 0) {
								szRtnMsg = "기존 작업예약 삭제 오류 ::" + arrStlNo[ii][jj] + " >> " + Integer.toString(intRtnVal);
								szMsg    = "["+szOperationName+"] " + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							//	return szRtnMsg;
				        	}

				        	//------------------------------------------------------------------------------------------------
				        	// 2.10. 저장품에 작업예약ID 수정
							//------------------------------------------------------------------------------------------------
				        	recPara.setField("YD_WBOOK_ID",		szNewYdWbookId);	// 작업예약ID
				        	recPara.setField("STL_NO",			arrStlNo[ii][jj]);	// 재료번호
				        	recPara.setField("MODIFIER",	    szModifier);		// 수정자
				        	recPara.setField("YD_SCH_CD",	    szYdSchCd);			// 스케쥴코드
							intRtnVal = ydStockDao.updYdStockWbook(recPara);
				        	if (intRtnVal <= 0) {
								szRtnMsg = "저장품에 작업예약ID 수정 오류 .. 재료번호::" + arrStlNo[ii][jj];
								szMsg    = "["+szOperationName+"] " + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
								return szRtnMsg;
				        	}
						}

			        	iWrkCnt ++;
			        }
				}	// end of Loop jj
			}	// end of Loop ii

			szMsg = "["+szOperationName+"] >>>> 크레인작업재료 등록 정상종료 :: " + iWrkCnt + "건";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			
			
			JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
			
			String sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A036"); //1후판정정야드 차량작업진행관리 신규모듈 적용 여부 
			
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 차량작업진행관리 신규모듈 적용 여부   : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG, logId);
			if("Y".equals(sNEW_MODULE_EFF_YN) && szYdDnWoLoc.length() >5){
				
				//25.11.10 니켈강 출하 시 횡작업 보완 -- 김정헌 책임 
		        if ( (szYdDnWoLoc.substring(2, 4).equals("PT") ||
		        		  szYdDnWoLoc.substring(2, 4).equals("TR")
		        		  )
		        		  &&
		        		  "PFPT01UM".equals(szYdSchCd)
	
		        		){	
		        	//권하지시위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
		        	JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
	                recInTemp.setField("YD_STK_COL_GP", szYdDnWoLoc.substring(0, 6));
	                JDTORecordSet rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
	                
	                YdStkColDao ydStkColDao2 = new YdStkColDao();
	                YdCarSchDao ydCarSchDao = new YdCarSchDao();
	                
	                intRtnVal = ydStkColDao2.getYdStkcol(recInTemp, rsResult2, 0);
	                if( intRtnVal <= 0 ) {
	                    szMsg = "[파일링실적처리] 차량정지위치[" + szYdDnWoLoc.substring(0, 6) + "] 정보가 존재하지 않습니다." ;
	                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	                    
	                    szMsg = "[" + szOperationName + "] 권상 완료 실적 처리 >>>> END ";
	            		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	            	    
	            	    return JPlateYdConst.RETN_CD_SUCCESS;
	                }
	                
	                //조회된 차량정지위치에서 운송장비코드를 가져온다.
	                rsResult.first();
	                recInTemp = rsResult.getRecord();
	                //운송장비코드
	                String  szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(recInTemp, "TRN_EQP_CD");
	                String szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");
	                String szCAR_NO = ydDaoUtils.paraRecChkNull(recInTemp, "CAR_NO");
	
	                szMsg = "[권상실적처리 - 상차개시]차량정지위치[" + szYdDnWoLoc.substring(0, 6) + "] 정보가 존재하므로 차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"] 로 차량스케줄 조회" ;
	                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	                
	                //운송장비코드로 차량스케줄조회
	                recInTemp = JDTORecordFactory.getInstance().create();
	                recInTemp.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
	                recInTemp.setField("TRN_EQP_CD", szTRN_EQP_CD);
	                recInTemp.setField("CAR_NO", szCAR_NO);
	                //recInTemp.setField("CARD_NO", szCARD_NO);
	                rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	
	                intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 27);
	                if( intRtnVal <= 0 ) {
	                    szMsg = "[권상실적처리 - 상차개시]차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"]에 해당하는 차량스케줄이 존재하지 않습니다." ;
	                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	                    
	                    szMsg = "[" + szOperationName + "] 권상 완료 실적 처리 >>>> END ";
	            		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	            	    
	            	    return JPlateYdConst.RETN_CD_SUCCESS;
	                }
	                
	                
	                //차량진행상태를 파악하여 상차검수이거나 상차도착일 때만 상차개시 전문을 송신한다.
	                rsResult.absolute(1);
	                recInTemp = rsResult.getRecord();
	                //차량스케줄ID
	                String szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_SCH_ID");
	                //야드차량진행상태
	                String szYD_CAR_PROG_STAT = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_PROG_STAT");
	                //차량사용구분
	                szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");
	
	                szMsg = "[권상실적처리 - 상차개시]차량스케줄 조회 후 차량스케줄ID[" + szYD_CAR_SCH_ID + "], 야드차량진행상태[" + szYD_CAR_PROG_STAT + "], 차량사용구분[" + szYD_CAR_USE_GP + "]" ;
	                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	
	                String sCAR_KIND = ydDaoUtils.paraRecChkNull(recInTemp, "CAR_KIND");
	                //상차검수이거나 상차도착일 때 상차개시 전문 송신
	                if( szYD_CAR_PROG_STAT.equals("3") || szYD_CAR_PROG_STAT.equals("2") ) {
	                   
	
	                    //차량스케줄 업데이트 - 상차개시
	                    recInTemp = JDTORecordFactory.getInstance().create();
	                    recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);                               //차량스케줄ID
	                    recInTemp.setField("YD_CAR_PROG_STAT", "4");                                        //차량진행상태
	                    recInTemp.setField("YD_EQP_WRK_STAT", "U");                                         //작업상태
	                    recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szNewYdWbookId);                         //작업예약ID
	                    recInTemp.setField("YD_CARLD_ST_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));         //상차개시일시
	                    recInTemp.setField("MODIFIER",  szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName); //수정자
	
	                    intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
	
	                    if(intRtnVal <= 0) {
	                        szMsg="[권상실적처리]차량스케줄에 상차개시일시 등록시 Error!! Code : " + intRtnVal;
	                        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	                        m_ctx.setRollbackOnly();
	                        throw new DAOException(szMsg);
	                    }
	                    String szCARLD_PNT_CD = "";
	
	                    recInTemp = JDTORecordFactory.getInstance().create();
	                    
	                    //25.07.23 구내운송은 고려대상아님. 하지만 나중에 추가될수있으므로 코드는 남긴다. 
	                    if( szYD_CAR_USE_GP.equals("L") ) {                 //구내운송
	
	//                        //상차작업개시 송신 YDTSJ007 (구내운송 상차개시)
	//                        recInTemp.setField("MSG_ID",        "YDTSJ007");
	//                        //착지개소코드
	//                        recInTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD);
	
	                        szMsg="[권상실적처리]상차작업개시 송신 YDTSJ007 (구내운송 상차개시) 송신 시작";
	                        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	
	                    }else if( szYD_CAR_USE_GP.equals("G") ){            //출하차량
	
	                        //상차작업개시 송신 YDDMR008 (후판출하상차개시)
	                        //PIDEV
	                        recInTemp.setField("MQ_TC_CD",      "M10YDLMJ1072");
	                        szMsg="[권상실적처리]상차작업개시 송신 M10YDLMJ1072 (후판출하상차개시) 송신 시작";
	
	                        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	
	                        //해송출하 이후 파라메터로 받은 권하실적위치를 가지고 상차포인트를 편집하여 사용한다.
	                        if(!"".equals(szYdDnWoLoc)) {
	                            if(szYdDnWoLoc.length()>=6) {
	                                szCARLD_PNT_CD = szYdDnWoLoc.substring(0,1) + szYdDnWoLoc.substring(4,5) + szYdDnWoLoc.substring(1,2) + szYdDnWoLoc.substring(5,6);
	                            }
	                        }
	                    }
	                    
	                    YdDelegate mqDelegate = new YdDelegate();
	                    
	                    String szYD_GP          = szYdSchCd.substring(0,1);
	
	                    recInTemp.setField("YD_SCH_CD",     szYdSchCd);
	                    recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
	                    recInTemp.setField("YD_GP",         szYD_GP);
	                    recInTemp.setField("CARLD_PNT_CD",  szCARLD_PNT_CD);
	
	                    mqDelegate.sendMsg(recInTemp);
	
	                    szMsg="[권상실적처리]상차작업개시 송신 완료";
	                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	                }
	                
	               
		        }
			}
			

			//------------------------------------------------------------------------------------------------
			// 설비상태 UPATE --> 크레인 상태를 권하지시로 SET
			//------------------------------------------------------------------------------------------------
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_EQP_ID", 		szYdEqpId);
			recPara.setField("YD_EQP_STAT", 	"3");
			recPara.setField("MODIFIER", 		szModifier);
			/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.updYdEqpStat 

			UPDATE TB_YD_EQP
			   SET MODIFIER    = :V_MODIFIER
			      ,MOD_DDTT    = SYSDATE
			      ,YD_EQP_STAT = :V_YD_EQP_STAT
			 WHERE YD_EQP_ID   = :V_YD_EQP_ID
			   AND DEL_YN      = 'N'
			*/	   
			intRtnVal = ydEqpDao.updYdEqpStat(recPara);
			if (intRtnVal <= 0) {
				szRtnMsg = "설비상태 변경시 오류 발생 : " + Integer.toString(intRtnVal);
				szMsg = "["+szOperationName+"] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			//------------------------------------------------------------------------------------------------
			// 2. 저장위치 변경 크레인으로
			//------------------------------------------------------------------------------------------------
			// 2.1. 권상 지시위치의 적치 상태 정보 CLEAR
			// 2.2. 현재료의 적지위치를 크레인으로 변경
			// 2.3. 권하 지시위치의 적치 상태 정보 SET
			//------------------------------------------------------------------------------------------------
        	iWrkCnt 		= 0;
        	szYdStkBedNo	= ydUtils.substr(szYdDnWoLoc,6,2);
        	if (szYdStkBedNo == null || "".equals(szYdStkBedNo)) {
        		szYdStkBedNo = "01";
        	}

			for(int ii=0; ii<5; ii++) {
				for(int jj=0; jj<3; jj++) {
					if (!"".equals(arrStlNo[ii][jj])) {
						// 2.1. 권상위치 CLEAR
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("MODIFIER",			szModifier);
						recPara.setField("YD_STK_LYR_MTL_STAT",	"U");
						recPara.setField("STL_NO",				arrStlNo[ii][jj]);
						recPara.setField("YD_GP",				szYdGp);

						intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
						if (intRtnVal <= 0) {
							szRtnMsg = "파일링 재료 권상위치 CLEAR 오류발생 : " + Integer.toString(intRtnVal);
							szMsg = "["+szOperationName+"] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						//  RT에서 북아웃시 기존위치가 다른 재료로 변경됨으로 계속 진행하도록 보완
						//	return szRtnMsg;
						} else {
							iUpWrkCnt ++;
						}

						// 2.2. 크레인으로 저장위치 변경
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_STK_COL_GP",       szYdEqpId);
		    			recPara.setField("YD_STK_BED_NO",       "01");
		    			recPara.setField("YD_STK_LYR_NO",       arrLayer[ii][jj]);
						recPara.setField("YD_STK_LYR_MTL_STAT",	"C");
						recPara.setField("STL_NO",				arrStlNo[ii][jj]);
						recPara.setField("MODIFIER",			szModifier);
		                intRtnVal = ydStkLyrDao.updYdStklyrStat(recPara);  	//크레인 적치단의 재료정보 UPDATE

		                // 2.3. 권하위치 CLEAR
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("MODIFIER",			szModifier);
						recPara.setField("YD_STK_LYR_MTL_STAT",	"D");
						recPara.setField("STL_NO",				arrStlNo[ii][jj]);
						recPara.setField("YD_GP",				szYdGp);

						intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
						if (intRtnVal <= 0) {
							szRtnMsg = "파일링 재료 권하위치 CLEAR 오류발생 : " + Integer.toString(intRtnVal);
							szMsg = "["+szOperationName+"] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						//	return szRtnMsg;
						} else {
							iDnWrkCnt ++;
						}
					}
				}	// end of loop jj
			}	// end of loop ii

			szMsg = "["+szOperationName+"] >>>> 파일링 재료 권상위치 CLEAR 정상종료 :: " + iUpWrkCnt + "건";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			szMsg = "["+szOperationName+"] >>>> 파일링 재료 권하위치 CLEAR 정상종료 :: " + iDnWrkCnt + "건";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//------------------------------------------------------------------------------------------------
			// 2.4. 파일링후 권하위치 SET
			//------------------------------------------------------------------------------------------------
			String szTopLyr = JPlateYdCommonUtils.getTopLyrNoByColGp(ydUtils.substr(szYdDnWoLoc,0,6), "01", "", szYdPilingGp, "");

			szMsg = "["+szOperationName+"] >>>> 파일링 재료 권하위치 단 :: " + szTopLyr;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			for(int ii=0; ii<5; ii++) {
				szYdStkLyrNo = ydDaoUtils.stringPlusInt(szTopLyr, ii);			// 적치단을    1증가
				szYdStkBedNo = "00";

				for(int jj=0; jj<3; jj++) {
					szYdStkBedNo = ydDaoUtils.stringPlusInt2(szYdStkBedNo, 1);	// 적치베드를 1증가, 단은 그대로 놔둠
					if (!"".equals(arrStlNo[ii][jj])) {
						// 2.4.1 저장위치 공베드 조회 (횡작업일때 베드 증가 , 파일링일때 적치단 증가)
						rsResult = JDTORecordFactory.getInstance().createRecordSet("retTmp");
						recPara  = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_STK_COL_GP",		ydUtils.substr(szYdDnWoLoc,0,6));
						recPara.setField("YD_STK_BED_NO",		szYdStkBedNo);
						recPara.setField("YD_PILING_GP",		szYdPilingGp);		// 파일링/횡작업 구분
						recPara.setField("YD_STK_LYR_NO",		szYdStkLyrNo);

						intRtnVal = ydStkLyrDao.getPilingToLoc(recPara, rsResult);
						if (intRtnVal > 0) {

							rsResult.first();
							recPara = JDTORecordFactory.getInstance().create();
							recPara = rsResult.getRecord();

			    			szYdStkBedNo = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
			    			szYdStkLyrNo = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");

							szMsg = "["+szOperationName+"] >>>> 파일링 재료  권하위치  >>>> " + szYdStkBedNo + "-" + szYdStkLyrNo;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			                // 2.4.2. 권하위치 SET
							recPara = JDTORecordFactory.getInstance().create();
							recPara = rsResult.getRecord();
							recPara.setField("YD_STK_COL_GP",       ydUtils.substr(szYdDnWoLoc,0,6));
			    			recPara.setField("YD_STK_BED_NO",       szYdStkBedNo);
			    			recPara.setField("YD_STK_LYR_NO",       szYdStkLyrNo);
							recPara.setField("YD_STK_LYR_MTL_STAT",	"D");
							recPara.setField("STL_NO",				arrStlNo[ii][jj]);
							recPara.setField("MODIFIER",			szModifier);
			                intRtnVal = ydStkLyrDao.updYdStklyrStat(recPara);  	//크레인 적치단의 재료정보 UPDATE

						} else {

							szMsg = "["+szOperationName+"] >>>> 파일링 재료 권하위치 미존재로 SKIP >>>> ";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						}
					}
				}	// end of loop jj
			}	// end of loop ii

			//------------------------------------------------------------------------------------------------
			// 3. 크레인 작업지시  취소 [파일링전 작업지시]
			//------------------------------------------------------------------------------------------------
        	iWrkCnt = 0;
			for(int ii=0; ii<5; ii++) {
				for(int jj=0; jj<3; jj++) {
					if (!"".equals(arrStlNo[ii][jj])) {
						recPara.setField("YD_CRN_SCH_ID",	arrSchId[ii][jj]);		// 야드크레인스케쥴ID
						recPara.setField("MODIFIER",	    szModifier);		// 수정자
						recPara.setField("DEL_YN",	    	"Y");				// 삭제여부

			        	intRtnVal = ydCrnSchDao.delYdCrnSch(recPara);
			        	if (intRtnVal <= 0) {
							szRtnMsg = "기존 크레인작업재료 삭제 오류 .. 야드크레인스케쥴ID ::" + szOldYdCrnSchId + " >> " + Integer.toString(intRtnVal);
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							return szRtnMsg;
			        	}
					}

					if (!"".equals(arrStlNo[ii][jj])) {
			    		// 3.1. 크레인 작업재료 조회
			        	recPara.setField("YD_CRN_SCH_ID", 	arrSchId[ii][jj]);
			        	recPara.setField("STL_NO", 			arrStlNo[ii][jj]);
						recPara.setField("MODIFIER",	    szModifier);		// 수정자
						recPara.setField("DEL_YN",	    	"Y");				// 삭제여부

			        	intRtnVal =	ydCrnWrkMtlDao.delYdCrnWrkMtl(recPara);
			        	if (intRtnVal <= 0) {
							szRtnMsg = "기존 크레인작업재료 삭제 오류 ::" + arrStlNo[ii][jj] + " >> " + Integer.toString(intRtnVal);
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							return szRtnMsg;
			        	}
			        	iWrkCnt ++;
					}
				}
			}
			szMsg = "["+szOperationName+"] >>>> 크레인작업재료 삭제 정상종료 :: " + iWrkCnt + "건";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//------------------------------------------------------------------------------------------------
			// 4.크레인 작업지시 취소 전송 [기존 파일링 전 작업지시]
			//------------------------------------------------------------------------------------------------
			szMsg = "["+szOperationName+"] >>>> 작업지시 취소 전문 전송 .. 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	iWrkCnt = 0;
			for(int ii=0; ii<5; ii++) {
				for(int jj=0; jj<3; jj++) {
					if (!"".equals(arrStlNo[ii][jj])) {
				        recL2Msg = JDTORecordFactory.getInstance().create();
				        recL2Msg.setField("MSG_ID", 					"YDY2L004");						// 크레인 작업지시
				        recL2Msg.setField("YD_CRN_SCH_ID",    			arrSchId[ii][jj]);
				        recL2Msg.setField("YD_WRK_PROG_STAT", 			JPlateYdConst.YD_EQP_STAT_UP_WO);	// 크레인 작업상태
				        recL2Msg.setField("MSG_GP",           			"D");

						szMsg = "["+szOperationName+"] >>>> 작업지시 취소 전문 전송 .. 데이타 >>>> " + recL2Msg.toString();
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				        szSendMsg = ydDelegate.sendMsg(recL2Msg);
				        iWrkCnt ++;

						szMsg = "["+szOperationName+"] >>>> 작업지시 취소 전문 전송 .. 결과 >>>> " + szSendMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					}
				}
			}
			szMsg = "["+szOperationName+"] >>>> 작업지시 취소 전문 전송 .. 종료 :: " + iWrkCnt + "건 전송완료";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//------------------------------------------------------------------------------------------------
			// 5.크레인 작업지시 전송 [신규 파일링 작업지시] - 권하작업지시로 Set
			//------------------------------------------------------------------------------------------------
			szMsg = "["+szOperationName+"] >>>> 신규 작업지시 전문 전송 .. 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        recL2Msg = JDTORecordFactory.getInstance().create();
	        //recL2Msg.setField("MSG_ID",						"YDY2L004");						// 크레인 작업지시
	        recL2Msg.setField("MSG_ID",						"YDY2L004V2");						// 크레인 작업지시
	        recL2Msg.setField("YD_CRN_SCH_ID",    			szNewYdCrnSchId);
	        recL2Msg.setField("YD_WRK_PROG_STAT", 			JPlateYdConst.YD_EQP_STAT_DN_WO);	// 권하지시
	        recL2Msg.setField("MSG_GP",           			"I");

	        szSendMsg = ydDelegate.sendMsg(recL2Msg);

			szMsg = "["+szOperationName+"] >>>> 신규 작업지시 전문 전송 .. 종료 :: " + szSendMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			szMsg = "["+szOperationName+"] 메소드 끝 ";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }

		return JPlateYdConst.RETN_CD_SUCCESS;
	}
	
	/**
	 * 오퍼레이션명 :  1후판정정야드  Book-In/Book-Out 실적-기존
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procY2BookInOutRslt(JDTORecord msgRecord)throws DAOException  {
		
		String szMsg		  	= "";
		String szMethodName	  	= "procY2BookInOutRslt";
		/*
		OPERATION_TYPE	OPERATION_TYPE	CHAR	1	Y	1:Book In, 2:Book Out		
		PL_L2_TRK_NO	후판L2제품번호		CHAR	16				
		PL_MTL_NO		후판재료번호		CHAR	10				
		YD_STR_LOC		야드저장위치		CHAR	8	Y			
		YD_STK_LYR_NO	야드적치단번호		CHAR	3	Y			
		*/
		int intRtnVal 			= 0;		
		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
		JDTORecord recTmp 		= JDTORecordFactory.getInstance().create();
		String 	szRcvTcCode    	= ydUtils.getTcCode(msgRecord);
		
		YdStkLyrDao  ydStkLyrDao  = new YdStkLyrDao();
		JPlateYdStockDAO JPlateYdDao 	= new JPlateYdStockDAO();
		JPlateYdStkLyrDAO   JPlateYdStkLyrDao		= new JPlateYdStkLyrDAO();
		String 	szModifier		= "";
		try{
			
			String szOPERATION_TYPE	= ydDaoUtils.paraRecChkNull(msgRecord,"OPERATION_TYPE");
//			String szPL_L2_TRK_NO 	= ydDaoUtils.paraRecChkNull(msgRecord,"PL_L2_TRK_NO");
			String szSTL_NO			= ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO");	
			String szYD_STR_LOC		= ydDaoUtils.paraRecChkNull(msgRecord,"YD_STR_LOC");
			String szYD_STK_LYR_NO	= ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_LYR_NO");
			String szCURR_YD_STR_LOC    = "";
			
			if ("".equals(szModifier)) {
	        	szModifier = szRcvTcCode;
	        }

			/**********************************************************
			* 재료번호 기준으로 저장위치 검색   MAP 삭제
			**********************************************************/			

			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO" 				, szSTL_NO);
			recPara.setField("YD_STK_LYR_MTL_STAT"	, "C");
			
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 3);
			
			//기존 야드 정보 삭제
			recPara   = JDTORecordFactory.getInstance().create();
			if(intRtnVal >= 1) {
				outRecSet.absolute(1);
				recTmp = outRecSet.getRecord();
				
				szCURR_YD_STR_LOC = ydDaoUtils.paraRecChkNull(recTmp, "YD_STK_COL_GP");
				
				recPara.setField("STL_NO" 				, "");
				recPara.setField("YD_STK_COL_GP" 		, szCURR_YD_STR_LOC);
				recPara.setField("YD_STK_BED_NO" 		, ydDaoUtils.paraRecChkNull(recTmp, "YD_STK_BED_NO"));
				recPara.setField("YD_STK_LYR_NO" 		, ydDaoUtils.paraRecChkNull(recTmp, "YD_STK_LYR_NO"));
				recPara.setField("YD_STK_LYR_MTL_STAT"	, "E");
				recPara.setField("MODIFIER"				, szModifier);
				
				if(szCURR_YD_STR_LOC.startsWith("P")){// 1후판 정정야드에 있는 경우에만 삭제
					//적치단 정보 UPDATE
					intRtnVal = JPlateYdStkLyrDao.updYdStklyrStat(recPara);
				}
			}			
			
			if("2".equals(szOPERATION_TYPE)){ //BOOK OUT

				/**********************************************************
				*  북아웃시 수신받는 단정보를 활용하지 않음. 자체적으로 최상단 검색
				**********************************************************/			
				
				recPara 		= JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP", szYD_STR_LOC.substring(0,6));
				recPara.setField("YD_STK_BED_NO", szYD_STR_LOC.substring(6,8));
		        
		        intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 98);
		        
		        if(intRtnVal >= 1) {
		        	outRecSet.first();
			        recTmp = outRecSet.getRecord();
			        
		        	szYD_STK_LYR_NO	= ydDaoUtils.paraRecChkNull(recTmp,"REAL_TOP_LYR");
		        }
			
				recPara 		= JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO" 				, szSTL_NO);
				recPara.setField("YD_GP"  				, JPlateYdConst.YD_GP_P_PLATE_YARD);
				recPara.setField("YD_STK_COL_GP" 		, szYD_STR_LOC.substring(0,6));
				recPara.setField("YD_STK_BED_NO" 		, szYD_STR_LOC.substring(6,8));
				recPara.setField("YD_STK_LYR_NO" 		, szYD_STK_LYR_NO);
				recPara.setField("YD_STK_LYR_MTL_STAT"	, "C");
				recPara.setField("MODIFIER"				, szModifier);
				
				intRtnVal = JPlateYdStkLyrDao.updYdStklyrStat(recPara);
				if (intRtnVal != 1) {
					szMsg = "REPAIR BED 적치단 등록 오류 .. 재료번호 : " + szSTL_NO;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, YdConstant.ERROR);
				    //skip
				} else {
					
					// 재료 정보 존재여부 체크
					intRtnVal = JPlateYdDao.getYdStockWithLoc(recPara, outRecSet);
					
					// 1후판 정정야드 재료정보 생성모듈 호출
					if (intRtnVal < 1) {
						intRtnVal = JPlateYdDao.insYdStockBookOutYdP(recPara);
					}					
				}	
			}else if("1".equals(szOPERATION_TYPE)){ //BOOK IN
				/**********************************************************
				*  북인시 RT 등록
				**********************************************************/			
				
				//RT 등록
				recPara 		= JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO" 				, szSTL_NO);
				recPara.setField("YD_GP"  				, JPlateYdConst.YD_GP_P_PLATE_YARD);
				recPara.setField("YD_STK_COL_GP" 		, szYD_STR_LOC.substring(0,6));
				recPara.setField("YD_STK_BED_NO" 		, szYD_STR_LOC.substring(6,8));
				recPara.setField("YD_STK_LYR_NO" 		, szYD_STK_LYR_NO);
				recPara.setField("YD_STK_LYR_MTL_STAT"	, "C");
				recPara.setField("MODIFIER"				, szModifier);
				
				intRtnVal = JPlateYdStkLyrDao.updYdStklyrStat(recPara);
				if (intRtnVal != 1) {
					szMsg = "RT BED 적치단 등록 오류 .. 재료번호 : " + szSTL_NO;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, YdConstant.ERROR);
				}				
			}
			
		}catch(Exception e){
			
			szMsg = "[1후판정정  Book-In/Book-Out 실적-기존] Exception Error:" +e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, YdConstant.ERROR);
		}

		szMsg = "1후판정정  Book-In/Book-Out 실적-기존] ("+szMethodName+") 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // procY2BookInOutRslt
	
	
    /**
     * 오퍼레이션명 : 1후판전단/열처리 L2시스템으로부터 Book-In/Book-Out요구 수신 [P2YDL501/P3YDL501]
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● Stirng pYdCrnSchId, String pYdWrkProgStat
     * @return ● nRtnVal
     * @throws ● DAOException
     */
	public String  procP2P3BookInOutReq(JDTORecord msgRecord) throws DAOException {
		// 레코드선언
		JDTORecordSet 	rsResult  	= null;
		JDTORecord 		inRec 		= null;
		JDTORecord 		tempRec 	= null;
		JDTORecord 		recPara   	= null;
		JDTORecord 		recSchPara  = null;
		JDTORecord 		recL2Para   = null;

		// 변수 선언
        String 	szMethodName  		= "procP2P3BookInOutReq";
        String	szRtnMsg			= JPlateYdConst.RETN_CD_SUCCESS;
    	String 	szMsg         		= "";
    	String 	szOperationName 	= "1후판 전단/열처리 Book-In/Book-Out요구 수신";

		String 	szYdGp          	= ""; 			// 야드구분
		String 	szYdBayGp       	= ""; 			// 야드동구분

		String 	szYdSchCd       	= "";
		String 	szYdSchProhExn  	= "";  			// 야드스케쥴금지유무
		String 	szYdWrkCrnPrior 	= "1"; 			// 야드작업크레인우선순위
		String 	szYdStkLyrNo	 	= "001";		// 야드적치단 , RT는 무조건 1단
		String 	szYdWrkCrn      	= "";			// 야드설비ID (NOT NULL이기 때문에 RULE에 등록된 설비ID SET)
		String	szYdWbookId 		= "";			// 작업예약ID 생성
		String	szYdStkColGp		= "";
		String	szYdToLocGuide		= "";
        String 	szRegister			= "";			// 등록자, MSG_ID

        String	szFromLoc			= "";
        String	szToLoc				= "";
        
    	int 	intRtnVal 			= 0;
		int		iWBookInsCnt		= 0;			// 작업예약등록 건수
		int		iCrnSchCnt			= 0;			// 크레인스케쥴 호출 건수
		int		iSchOkCnt			= 0;			// 스케줄호출 OK 건수

		StringBuffer sbARR_WBOOK_ID = new StringBuffer();

    	//DAO
    	JPlateYdStockDAO    ydStockDao   	= new JPlateYdStockDAO();
    	JPlateYdWrkbookDAO	ydWrkbookDao 	= new JPlateYdWrkbookDAO();
    	JPlateYdCrnSchDAO 	ydCrnSchDao		= new JPlateYdCrnSchDAO();
    	JPlateYdStkLyrDAO   ydStkLyrDao		= new JPlateYdStkLyrDAO();
    	JPlateYdSchRuleDAO  ydSchRuleDao	= new JPlateYdSchRuleDAO();

        try {
        	szMsg = "["+szOperationName+"] ---- 메소드 시작  >>>> " + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			/*
			7	OPERATION_TYPE			OPERATION_TYPE		CHAR	1
			8	PL_L2_TRK_NO			후판L2제품번호			CHAR	16
			9	PL_MTL_NO				후판재료번호			CHAR	32
			10	PL_MEA_GDS_L			후판제촌제품길이		CHAR	5
			11	PL_MEA_GDS_W			후판제촌제품폭			CHAR	6
			12	PL_MEA_GDS_T			후판제촌제품두께		CHAR	7
			13	PL_TRCK_ZONE_ASGN		후판트래킹존지정		CHAR	5
			14	PL_BOOK_OUT_MOD			후판북아웃모드			CHAR	1
			15	CRANE_NO				Crane_No			CHAR	2
			16	YARD_NO					Yard_No				CHAR	6
			17	BED_NO					BED_NO				CHAR	2
			18	REASON_CODE				REASON_CODE			CHAR	3
			18	NEXT_PROCESS			NEXT_PROCESS		CHAR	1
		*/
			String szRcvTcCode    	= ydUtils.getTcCode(msgRecord);
			String szPlateId 		= ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO");				// PLATE_NO
			String szOperationType 	= ydDaoUtils.paraRecChkNull(msgRecord, "OPERATION_TYPE");			// 1:Book In, 2:Book Out
//			String szTrkZoneNo		= ydDaoUtils.paraRecChkNull(msgRecord, "PL_TRCK_ZONE_ASGN");		// PL_TRCK_ZONE_ASGN
			String szTrkZoneNo		= ydDaoUtils.paraRecChkNull(msgRecord, "PL_TRCK_ZONE_ASG");		    // PL_TRCK_ZONE_ASGN
			
			if(szTrkZoneNo.equals("")|| szTrkZoneNo == null) {
				szTrkZoneNo		= ydDaoUtils.paraRecChkNull(msgRecord, "PL_TRCK_ZONE_ASGN");		// PL_TRCK_ZONE_ASGN
			}
			
			String szYdNo			= ydDaoUtils.paraRecChkNull(msgRecord, "YARD_NO");                  // YARD_NO
			String szReasonCode		= ydDaoUtils.paraRecChkNull(msgRecord, "REASON_CODE");				// Book-Out원인 (777: 북아웃 요청 888:TEST , 999:북아웃취소)
			String szResonCode		= ydDaoUtils.paraRecChkNull(msgRecord, "RESON_CODE");				// REASON_CODE가 아닌 RESON_CODE로 올 경우 대비.
			String szCraneNo		= ydDaoUtils.paraRecChkNull(msgRecord, "CRANE_NO");					// 북인대상재 구분 'A1' : 북인대상재
			String szOperationMode	= ydDaoUtils.paraRecChkNull(msgRecord, "PL_BOOK_OUT_MOD");			// 1:one time 2:Start 3:End
			String szOperationBed	= ydDaoUtils.paraRecChkNull(msgRecord, "BED_NO");					// Book in/Book-Out시 야드적치Bed번호
			String szOperationDate 	= JPlateYdUtils.getCurDate("yyyyMMdd");
			szRegister 		= ydDaoUtils.paraRecModifier(msgRecord);							// 등록자, 수정자
			if ("".equals(szRegister)) {
				szRegister = szRcvTcCode;
			}
			
			szMsg    = "["+szOperationName+"] szRegister:" + szRegister;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			
			if (!"1".equals(szOperationMode) && !"2".equals(szOperationMode) && !"3".equals(szOperationMode)) {
				szRtnMsg = "수신전문중 OPERATION_MODE ERROR";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			// 북아웃 원인코드 체크 :: 888 - TEST , 999 - 취소처리
			if ("888".equals(szReasonCode)) {
				szRtnMsg = "북아웃 원인코드가 TEST재로 SKIP 처리함 :: " + szReasonCode;
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}
			// 북아웃원인 코드가 취소일때  (999)
			if ("999".equals(szReasonCode)) {

				szMsg    = "["+szOperationName+"] 북아웃 취소 처리 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				szRtnMsg = this.procP2P3BookInOutCancel(msgRecord);

				szMsg    = "["+szOperationName+"] 북아웃 취소 처리 END >>>> 결과 :: " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return szRtnMsg;
			}
			

			//------------------------------------------------------------------------------------------------
			// 1. 파라미터 체크
			// 1.1 북인   시 trkZoneNo -> to위치       ,   yardNo -> from 위치
			// 1.1 북아웃시 trkZoneNo -> from 위치 ,   yardNo -> to위치
			//------------------------------------------------------------------------------------------------
			if ("1".equals(szOperationType)) { // Book In

				szFromLoc 	= szYdNo;
				szToLoc 	= szTrkZoneNo;

				// From 위치
				if (szYdNo == null || szYdNo.length() < 6) {

					// 저장위치 없이 판번호만 들어 왔을때 처리
					if (!"".equals(szPlateId)) {

						szMsg    = "["+szOperationName+"] 저장위치 없이 판번호만 들어 왔을때 .... SKIP";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					} else {
						szRtnMsg = "수신전문중 FROM위치 ERROR >>>> " + szYdNo;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}

				if (szYdNo.length() > 1) {
					if (!"P".equals(ydUtils.substr(szYdNo, 0, 1))) {
						szRtnMsg = "수신전문중 FROM위치 ERROR >>>>>>> " + szYdNo;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}
				
			// To 위치
				
				// TRK ZONE NO를 야드저장위치로 변경
				if (!"".equals(szTrkZoneNo) && szTrkZoneNo.length() == 5) {
					// ZONE NO를 야드저장위치로 변경 :: 2300 >>>> FART01
					szToLoc = JPlateYdCommonUtils.getY2RtZoneToLoc(szTrkZoneNo);
				}
				
				if (szToLoc == null || szToLoc.equals("")) {
					szRtnMsg = "수신전문중 TO위치 ERROR >>>>>>> " + szTrkZoneNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				} 
				if (!"P".equals(ydUtils.substr(szToLoc, 0, 1))) {
					szRtnMsg = "수신전문중 TO위치 ERROR >>>>>>> " + szTrkZoneNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}


			} else if ("2".equals(szOperationType)) {		// Book Out
				
				szFromLoc 	= szTrkZoneNo;
				szToLoc 	= szYdNo;

				// PLATE_ID
				// BOOK-OUT일때는 2,3의 경우는 미존재함 즉 'PL999999'는 미존재
				if ("".equals(szPlateId) || "PL999999".equals(szPlateId)) {
					szRtnMsg = "수신전문중 PLATE_ID ERROR";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// from 위치				
				
				// TRK ZONE NO를 야드저장위치로 변경
				if (!"".equals(szTrkZoneNo) && szTrkZoneNo.length() == 5) {
					// ZONE NO를 야드저장위치로 변경 :: 2300 >>>> FART01
					szFromLoc = JPlateYdCommonUtils.getY2RtZoneToLoc(szTrkZoneNo);
				}
				
				szMsg    = "["+szOperationName+"]  >>>> szFromLoc1 :: " + szFromLoc;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				
				if (szFromLoc == null || szFromLoc.equals("")) {
					szRtnMsg = "수신전문중 FROM위치 ERROR >>>>>>> " + szTrkZoneNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				} 
				
				if (!"P".equals(ydUtils.substr(szFromLoc, 0, 1))) {
					szRtnMsg = "수신전문중 FROM위치 ERROR >>>>>>> " + szTrkZoneNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// TO 위치

				if (!"P".equals(ydUtils.substr(szYdNo, 0, 1)) || szYdNo.length() < 6) {
					if ("P2YDL501".equals(szRegister)||"P3YDL501".equals(szRegister)) {
						szRtnMsg = "수신전문중 TO위치 ERROR >>>>> " + szYdNo;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}
				
				// TO위치의 동과 FROM위치의 동이 상이할 경우 TO위치의 동을 스케줄 기준으로 변경 : 2014.07.08 보완
				String sToBay = ydUtils.substr(szToLoc,   1, 1);
				String sFrBay = ydUtils.substr(szFromLoc, 1, 1);
				if (!sToBay.equals(sFrBay)) {
					szMsg   = "["+szOperationName+"] To위치의 동과 From위치의 동이 상이하여 스케쥴 기준 적용 >>>>" + szToLoc + "," + szFromLoc;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					szToLoc = ydUtils.substr(szFromLoc, 0, 2);
				}

				// Book-Out원인
//				if ("".equals(szReasonCode)) {
//					szRtnMsg = "수신전문중 Book-Out원인 ERROR";
//					szMsg    = "["+szOperationName+"] " + szRtnMsg;
//					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
//					return szMsg;
//				}

				// BOOK-OUT시 BED는 '01' 임 (FROM위치가 RT임으로)
				szOperationBed = "01";
			}

			// Book In 시 저장위치 조회
			if ("1".equals(szOperationType)) {

				if ("".equals(szPlateId)) {			// 재료번호 미존재시

	    			szYdStkColGp = szFromLoc;

					szMsg    = "["+szOperationName+"] 북인작업중 .... 재료번호 미존재 >>>> 저장위치 :: " + szYdStkColGp;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				} else {
					// 베드정보 조회
					rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
					inRec    = JDTORecordFactory.getInstance().create();
					inRec.setField("STL_NO", 	szPlateId);             	// 재료번호
					inRec.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);

	    			intRtnVal = ydStockDao.getYdStockWithLoc(inRec, rsResult);
					if (intRtnVal < 1) {
						szRtnMsg = "야드재료가 미존재 합니다 .... 재료번호:" + szPlateId;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

	    			rsResult.first();
					tempRec = JDTORecordFactory.getInstance().create();
	    			tempRec = rsResult.getRecord();

	    			szYdStkColGp 	= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_COL_GP");
	    			szOperationBed 	= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_BED_NO");
					szYdStkLyrNo	= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_LYR_NO");
				}

				String sToBay = ydUtils.substr(szToLoc, 0, 2);
				if (!ydUtils.substr(szYdStkColGp,0,2).equals(sToBay)) {
					szRtnMsg = "해당동의 재료만 Book-IN 가능! .... 재료위치:: " + szYdStkColGp + ", Book-IN위치:: " + szToLoc;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
			}

			//------------------------------------------------------------------------------------------------
			// 1.2. 해당 재료 작업예약/스케쥴 존재여부 확인
			//------------------------------------------------------------------------------------------------
			if (!"".equals(szPlateId)) {
				// ------------------------------------------------------------------------
				// 1.2.1. 작업예약 존재여부 확인
				// ------------------------------------------------------------------------
				rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 		szPlateId);             	// 재료번호
				recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);

				intRtnVal = ydWrkbookDao.getExistByStlNo(recPara, rsResult);
				if (intRtnVal > 0) {
					szRtnMsg = "해당 재료["+szPlateId+"]로 작업예약이 존재!";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// ------------------------------------------------------------------------
				// 1.2.2. 크레인 작업지시 존재여부 확인
				// ------------------------------------------------------------------------
				rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 		szPlateId);             	// 재료번호
				recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);

				intRtnVal = ydCrnSchDao.getExistByStlNo(recPara, rsResult);
				if (intRtnVal > 0) {
					szRtnMsg = "해당 재료"+szPlateId+"로 크레인 작업지시 존재!";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				//------------------------------------------------------------------
				// 1.2.3. 현재 저장위치가 1후판 정정야드가 아닐경우 오류로 처리
				//------------------------------------------------------------------
				szRtnMsg = JPlateYdCommonUtils.checkUpdYdLocYdP(szPlateId, JPlateYdConst.YD_GP_P_PLATE_YARD, "N");
				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					szMsg = "[ " +szOperationName + "] 북아웃시 저장위치 확인 오류! >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
			}

			//------------------------------------------------------------------------------------------------
			// 2. 야드재료 등록 (BOOK-OUT시)
			//------------------------------------------------------------------------------------------------
			if ("2".equals(szOperationType)) {		// Book Out

				//------------------------------------------------------------------------------------------------
				// 2.1. 야드재료 조회
				//------------------------------------------------------------------------------------------------
				szMsg    = "["+szOperationName+"] ----------- 야드재료 조회 START ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				// 레코드 편성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
				inRec = JDTORecordFactory.getInstance().create();
				inRec.setField("STL_NO", 		szPlateId);             	// 재료번호
				inRec.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);
    			intRtnVal = ydStockDao.getYdStockWithLoc(inRec, rsResult);
				if (intRtnVal > 0) {

					tempRec = JDTORecordFactory.getInstance().create();

	    			rsResult.first();
	    			tempRec = rsResult.getRecord();
	    			szYdStkColGp = ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_COL_GP");

	    			if ("".equals(szYdStkColGp) || "RT".equals(ydUtils.substr(szYdStkColGp, 2, 2))) {

						szMsg    = "["+szOperationName+"] 해당 재료의 저장위치 :: " + szYdStkColGp + " 계속 진행 ";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    			} else {
						szRtnMsg = "야드재료가 이미 존재합니다 .... 현위치::" + szYdStkColGp;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
	    			}
				}

				//------------------------------------------------------------------------------------------------
				// 2.3. 야드재료 등록
				//------------------------------------------------------------------------------------------------
				szMsg    = "["+szOperationName+"] ----------- 야드재료 등록 START ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				// 레코드 편성
				inRec = JDTORecordFactory.getInstance().create();
				inRec.setField("REGISTER", 			szRegister);							// 등록자
				inRec.setField("STL_NO", 			szPlateId);             				// 재료번호
				inRec.setField("BOOK_OUT_RESN",		szReasonCode);							// Book-Out원인
				inRec.setField("BOOK_OUT_DATE",     JPlateYdUtils.getCurDate("yyyyMMdd"));	// Book-Out일자
				inRec.setField("BOOK_OUT_PROG",  	"");           							// Book-Out공정
				inRec.setField("FRTOMOVE_PLANT_GP",	szCraneNo);           					// 북인대상재 구분 (이송공장구분 항목사용)

    			intRtnVal = ydStockDao.insYdStockBookOut(inRec);
				if (intRtnVal <= 0) {
//					szRtnMsg = "야드재료 등록 ERROR .. " + Integer.toString(intRtnVal);
					szRtnMsg = "재료정보 미존재로 오류 발생 .. " + szPlateId + ", 오류코드::" + Integer.toString(intRtnVal);
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
				
				//------------------------------------------------------------------------------------------------
				// 2.4. 해당 재료 적치위치 비우기
				//------------------------------------------------------------------------------------------------
				szMsg    = "["+szOperationName+"] ----------- 해당 재료 적치위치 비우기 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
 
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 				szPlateId);             	// 재료번호
				recPara.setField("YD_STK_LYR_MTL_STAT", "C");             		// 야드적치단재료상태
				recPara.setField("YD_GP",				JPlateYdConst.YD_GP_P_PLATE_YARD);
				recPara.setField("MODIFIER", 			szRegister);

				intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
				if (intRtnVal < 0) {
					szRtnMsg = "저장위치 삭제 처리시 오류 발생!1 .... 오류코드 :" + Integer.toString(intRtnVal);
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				}
				
				
				//------------------------------------------------------------------------------------------------
				// 2.4. 적치단 수정
				//------------------------------------------------------------------------------------------------
				szMsg    = "["+szOperationName+"] ----------- 적치단 야드적치단재료상태 수정 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				//적치단 야드적치단재료상태 수정
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP", 		szFromLoc);                 // 야드적치열구분
				recPara.setField("YD_STK_BED_NO", 		"01");    					// 야드적치BED번호
				recPara.setField("YD_STK_LYR_NO", 		"001");    					// 야드적치단
				recPara.setField("STL_NO", 				szPlateId);             	// 재료번호
				recPara.setField("YD_STK_LYR_MTL_STAT", "C"); 						// 적치완료
				recPara.setField("MODIFIER", 			szRegister);				// 등록자

				intRtnVal = ydStkLyrDao.updYdStklyrStat(recPara);
				if (intRtnVal <= 0) {
					szRtnMsg = "야드적치단재료상태 수정 ERROR .. " + Integer.toString(intRtnVal);
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				//------------------------------------------------------------------------------------------------
				// 2.5. 야드재료 등록후 L2에 저장품제원 정보 송신 .. 작업지시 송신 이전에 전송해야함
				//      야드L2 전문송신 (저장품제원 :: YDY2L002 전송)
				//------------------------------------------------------------------------------------------------
				szMsg = "[ " +szOperationName + "] 야드L2 저장품제원 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				recL2Para = JDTORecordFactory.getInstance().create();
				recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
				recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
				recL2Para.setField("YD_STK_COL_GP", 	szFromLoc);                          	// 야드적치열구분
				recL2Para.setField("YD_STK_BED_NO", 	"01");    								// 야드적치BED번호
				recL2Para.setField("YD_INFO_SYNC_CD", 	"4");									// 야드정보동기화코드  [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
				recL2Para.setField("STL_NO", 			szPlateId);	        					// 재료번호
				szRtnMsg = ydDelegate.sendMsg(recL2Para);

				szMsg = "[ " +szOperationName + "] 야드L2 저장품제원 전문송신 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				
				/*
				 * 2018.08.31 윤재광 - 유창훈 대리 요구사항 반영
				 * 60012존 북아웃시 재료에 따른 To위치 결정
				 * 2019.04.05 윤재광 - 강기석 주임 요청으로 막음
				 */
				/*
				String sHmiStat 		= "N";
				String sQuery1			= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
				JDTORecord wbJr 		= (new com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO()).getData(sQuery1, new Object[]{ "2XRT04" });
				if (wbJr != null){ 
					sHmiStat	= StringHelper.evl(wbJr.getFieldString("HMI_STAT"), "");
				}
				if("Y".equals(sHmiStat)){
					if("60012".equals(szTrkZoneNo)){
						
						JDTORecordSet jsRcds = JDTORecordFactory.getInstance().createRecordSet("yd");
						JDTORecord jrRcd = JDTORecordFactory.getInstance().create();
						jrRcd.setField("PLATE_NO", szPlateId);
						
						com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao dao = new com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao();
						
						intRtnVal = dao.getYdStock(jrRcd, jsRcds, 4);
						
						String sCurrProgCd 	= "";
						int iPlMeaGdsL  	= 0;
						
						if(intRtnVal > 0 ){

							jsRcds.first();
							jrRcd = JDTORecordFactory.getInstance().create();
							jrRcd = jsRcds.getRecord();
							
							sCurrProgCd 	= ydDaoUtils.paraRecChkNull(jrRcd,"CURR_PROG_CD");
							iPlMeaGdsL  	= ydDaoUtils.paraRecChkNullInt(jrRcd,"PL_MEA_GDS_L");
							
							if("C".equals(sCurrProgCd)){
								
								szToLoc = "PF0104";
								
							}else{
								
								if(iPlMeaGdsL >  14000){
									szToLoc = "PF0103";
								}else{
									szToLoc = "PF0102";
								}
							}
						}
						
						szMsg = "[ " +szOperationName + "] 60012존 북아웃요청시 처리 >>>> " + szToLoc +"/"+ sCurrProgCd +"/"+ iPlMeaGdsL;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					}
				}
				*/
			}
 			
			/**********************************************************
			* 3. 스케줄기준 정보 Check
			**********************************************************/
			if ("".equals(szFromLoc)) {
				szYdGp    = ydUtils.substr(szToLoc, 0, 1); 		// 야드구분
				szYdBayGp = ydUtils.substr(szToLoc, 1, 1); 		// 야드동구분
			} else {
				szYdGp    = ydUtils.substr(szFromLoc, 0, 1); 	// 야드구분
				szYdBayGp = ydUtils.substr(szFromLoc, 1, 1); 	// 야드동구분
			}

			//야드스케쥴코드 : 야드+동+RT+마그네틱크레인번호+L(BOOK-OUT)+M(분할없음) : FART0?LM, FBRT0?LM, FCRT0?LM
			//야드스케쥴코드 : 야드+동+RT+마그네틱크레인번호+U(BOOK-IN)+M(분할없음)  : FART0?UM, FBRT0?UM, FCRT0?UM

			if ("1".equals(szOperationType)) {				// Book IN
			//	szYdSchCd   = szYdGp + szYdBayGp + "RT0?UM";
				szYdSchCd	= ydUtils.getRtSchCdYdP(szToLoc, "UM");
			} else if ("2".equals(szOperationType)) {		// Book Out
			//	szYdSchCd   = szYdGp + szYdBayGp + "RT0?LM";
				szYdSchCd	= ydUtils.getRtSchCdYdP(szFromLoc, "LM");
			}

			szMsg    = "[" + szOperationName + "] >>>> FROM위치 >>>> [" + szFromLoc + "]";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg    = "[" + szOperationName + "] >>>> TO위치 >>>> [" + szToLoc + "]";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg    = "[" + szOperationName + "] >>>> S1YDL013 스케쥴코드 >>>> " + szYdSchCd;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szYdSchProhExn  = "";  			// 야드스케쥴금지유무
			szYdWrkCrnPrior = "1"; 			// 야드작업크레인우선순위
			szYdWrkCrn      = "";			// 야드설비ID (NOT NULL이기 때문에 RULE에 등록된 설비ID SET)

			recPara  = JDTORecordFactory.getInstance().create();
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
			recPara.setField("YD_SCH_CD", szYdSchCd); //야드스케쥴코드

			//야드스케쥴금지유무 조회
			ydSchRuleDao.getYdSchrule(recPara, rsResult);

			if (rsResult != null && rsResult.size() > 0) {
				szYdSchProhExn  = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_SCH_PROH_EXN");
				//야드작업크레인우선순위 : 야드스케쥴코드에 해당하는 작업크레인의 우선순위이므로
				//실제 요청한 크레인의 우선순위와 다를 수 있음
				szYdWrkCrnPrior = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_CRN_PRIOR");
				szYdWrkCrn		= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_CRN");
			}

			if ("".equals(szYdSchProhExn)) {
				//스케줄기준 Table 정보 Check
				szRtnMsg = "스케쥴코드[" + szYdSchCd + "] 정보 없음";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			} else if ("Y".equals(szYdSchProhExn)) {
				//스케줄 금지여부 Check
				szRtnMsg = "스케쥴코드[" + szYdSchCd + "] 기동금지";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			// BOOK-IN & 재료번호 미존재시 작업예약재료 등록
			if ("1".equals(szOperationType) && "".equals(szPlateId)) {

				// FROM위치로 재료정보를 조회하여 작업예약 등록
				rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
				inRec    = JDTORecordFactory.getInstance().create();
				inRec.setField("YD_STK_COL_GP", 	ydUtils.substr(szFromLoc, 0, 6));   // 야드적치열
			//	inRec.setField("YD_STK_BED_NO", 	ydUtils.substr(szFromLoc, 6, 2));	// 야드적치베드
				inRec.setField("YD_STK_BED_NO", 	"");								// 연속북인시 베드정보 무시하도록 보완
				if ("2".equals(szOperationMode)) {										// 1:one time 2:Start 3:End
					inRec.setField("ROW_CNT", 		"999");		// FROM위치의 모든 재료
				} else {
					inRec.setField("ROW_CNT", 		"1");		// 1매
				}

				// BOOK-IN 대상재 조회 (적치단 역순으로)
		    	intRtnVal = ydStkLyrDao.getRTBookInMtl(inRec, rsResult);
		    	if (intRtnVal < 1) {
					szRtnMsg = "BOOK-IN 대상재 미존재 .... 저장위치 :: " + szFromLoc;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
		    	}

		    	rsResult.first();
		    	for(int ii=0; ii<rsResult.size(); ii++) {

		    		tempRec = rsResult.getRecord(ii);

					//------------------------------------------------------------------------------------------------
					// 4. 작업예약 등록 [연속 북인시]
					//------------------------------------------------------------------------------------------------
					szYdWbookId = ydWrkbookDao.getSeqId();						//작업예약ID 생성

					szMsg    = "["+szOperationName+"] ----------- 작업예약ID 생성 :: " + ii + " >>>> " + szYdWbookId;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					if ("".equals(szYdWbookId)) {
						szRtnMsg = "작업예약ID 생성 ERROR";
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					szMsg    = "["+szOperationName+"] ----------- 작업예약 등록";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					szYdToLocGuide = szToLoc;

					//작업예약 등록
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID", 		szYdWbookId); 		//야드작업예약ID
					recPara.setField("YD_GP", 				szYdGp); 			//야드구분
					recPara.setField("YD_BAY_GP", 			szYdBayGp); 		//야드동구분
					recPara.setField("YD_SCH_CD", 			szYdSchCd); 		//야드스케쥴코드
					recPara.setField("YD_SCH_PRIOR", 		szYdWrkCrnPrior); 	//야드스케쥴우선순위
					recPara.setField("YD_SCH_PROG_STAT", 	"W"); 				//야드스케쥴진행상태(스케줄수행대기)
					recPara.setField("YD_SCH_ST_GP", 		"M"); 				//야드스케쥴기동구분
					recPara.setField("YD_SCH_REQ_GP", 		"X"); 				//야드스케쥴요청구분
					recPara.setField("YD_AIM_YD_GP", 		szYdGp); 			//야드목표야드구분
					recPara.setField("YD_AIM_BAY_GP", 		szYdBayGp); 		//야드목표동구분
					recPara.setField("YD_TO_LOC_DCSN_MTD",	"A"); 				//야드TO위치결정방법(스케줄기준적용)
					recPara.setField("YD_TO_LOC_GUIDE",		szYdToLocGuide);	//야드To위치Guide
					recPara.setField("REGISTER", 			szRegister);
					recPara.setField("MODIFIER", 			szRegister);

					intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
					if (intRtnVal <= 0) {
						szRtnMsg = "작업예약 등록 ERROR .. " + Integer.toString(intRtnVal);
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					//작업예약재료 등록
					recPara.setField("YD_WBOOK_ID", 	szYdWbookId); 											//야드작업예약ID
					recPara.setField("STL_NO", 			ydDaoUtils.paraRecChkNull(tempRec, "STL_NO")); 			//재료번호
					recPara.setField("YD_STK_COL_GP", 	ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_COL_GP"));	//야드적치열구분
					recPara.setField("YD_STK_BED_NO", 	ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_BED_NO"));	//야드적치베드번호
					recPara.setField("YD_STK_LYR_NO", 	ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_LYR_NO")); 	//야드적치단번호
					recPara.setField("YD_TAKE_OUT_DT", 	szOperationDate);										//BOOK-OUT일시
					recPara.setField("YD_TAKE_OUT_CD", 	szReasonCode);											//BOOK-OUT원인코드

					szRtnMsg = this.insWrkbookMtl(recPara);

			        if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
						szMsg    = "["+szOperationName+"] 작업예약재료 등록 ERROR .. " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
			        }

					if (iWBookInsCnt > 0) {
						sbARR_WBOOK_ID.append(";");
					}
					sbARR_WBOOK_ID.append(szYdWbookId);
					iWBookInsCnt ++;
		    	}

			} else {

				//------------------------------------------------------------------------------------------------
				// 4. 작업예약 등록 [북아웃 또는 1매 북인]
				//------------------------------------------------------------------------------------------------
				szYdWbookId = ydWrkbookDao.getSeqId();						//작업예약ID 생성

				szMsg    = "["+szOperationName+"] ----------- 작업예약ID 생성 :: " + szYdWbookId;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				if ("".equals(szYdWbookId)) {
					szRtnMsg = "작업예약ID 생성 ERROR";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				szMsg    = "["+szOperationName+"] ----------- 작업예약 등록";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				szYdToLocGuide = szToLoc;

				//작업예약 등록
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID", 		szYdWbookId); 		//야드작업예약ID
				recPara.setField("YD_GP", 				szYdGp); 			//야드구분
				recPara.setField("YD_BAY_GP", 			szYdBayGp); 		//야드동구분
				recPara.setField("YD_SCH_CD", 			szYdSchCd); 		//야드스케쥴코드
				recPara.setField("YD_SCH_PRIOR", 		szYdWrkCrnPrior); 	//야드스케쥴우선순위
				recPara.setField("YD_SCH_PROG_STAT", 	"W"); 				//야드스케쥴진행상태(스케줄수행대기)
				recPara.setField("YD_SCH_ST_GP", 		"M"); 				//야드스케쥴기동구분
				recPara.setField("YD_SCH_REQ_GP", 		"X"); 				//야드스케쥴요청구분
				recPara.setField("YD_AIM_YD_GP", 		szYdGp); 			//야드목표야드구분
				recPara.setField("YD_AIM_BAY_GP", 		szYdBayGp); 		//야드목표동구분
				recPara.setField("YD_TO_LOC_DCSN_MTD",	"A"); 				//야드TO위치결정방법(스케줄기준적용)
				recPara.setField("YD_TO_LOC_GUIDE",		szYdToLocGuide);	//야드To위치Guide
				recPara.setField("REGISTER", 			szRegister);
				recPara.setField("MODIFIER", 			szRegister);

				intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
				if (intRtnVal <= 0) {
					szRtnMsg = "작업예약 등록 ERROR .. " + Integer.toString(intRtnVal);
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				//작업예약재료 등록
				recPara.setField("YD_WBOOK_ID", 	szYdWbookId); 		//야드작업예약ID
				recPara.setField("STL_NO", 			szPlateId); 		//재료번호
				recPara.setField("YD_STK_COL_GP", 	szFromLoc);			//야드적치열구분
				recPara.setField("YD_STK_BED_NO", 	szOperationBed);	//야드적치베드번호
				recPara.setField("YD_STK_LYR_NO", 	szYdStkLyrNo); 		//야드적치단번호
				recPara.setField("YD_TAKE_OUT_DT", 	szOperationDate);	//BOOK-OUT일시
				recPara.setField("YD_TAKE_OUT_CD", 	szReasonCode);		//BOOK-OUT원인코드

				szRtnMsg = this.insWrkbookMtl(recPara);

		        if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					szMsg    = "["+szOperationName+"] 작업예약재료 등록 ERROR .. " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
		        }

				if (iWBookInsCnt > 0) {
					sbARR_WBOOK_ID.append(";");
				}
				sbARR_WBOOK_ID.append(szYdWbookId);
				iWBookInsCnt ++;

			}

			//------------------------------------------------------------------------------------------------
			// 연속 BOOK-IN/OUT일때 가이던스 메시지 전송
			//------------------------------------------------------------------------------------------------
			if ("2".equals(szOperationMode) || "3".equals(szOperationMode)) {					// 1:one time 2:Start 3:End

				szMsg = "[" +szOperationName + "] 야드L2 가이던스메시지 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				
				recL2Para = JDTORecordFactory.getInstance().create();
				recL2Para.setField("JMS_TC_CD", 			"YDY2L006");                            // TC-CODE
				recL2Para.setField("YD_GP", 				JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
				recL2Para.setField("OPERATION_TYPE", 		szOperationType);    					// 1:Book In, 2:Book Out
				if ("2".equals(szOperationMode)) {
					recL2Para.setField("OPERATION_MODE", 	"1");									// 1:Start
				} else {
					recL2Para.setField("OPERATION_MODE", 	"2");									// 2:End
				}
				
				if ("1".equals(szOperationType)) {			// 1:Book-in 일때
					recL2Para.setField("YD_BAY_GP", 		ydUtils.substr(szToLoc, 1, 1));         // 야드동구분
					recL2Para.setField("OPERATION_SOURCE", 	szToLoc);	        					// Book-In일때  TO위치
				} else {
					recL2Para.setField("YD_BAY_GP", 		ydUtils.substr(szFromLoc, 1, 1));       // 야드동구분
					recL2Para.setField("OPERATION_SOURCE", 	szFromLoc);	        					// Book-Out일때 From위치
				}

				szRtnMsg = ydDelegate.sendMsg(recL2Para);

				szMsg = "[ " +szOperationName + "] 야드L2 가이던스메시지 전문송신 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

			iCrnSchCnt  = 0;
			iSchOkCnt	= 0;
			if (iWBookInsCnt > 0) {

				String[] arrWBookId = sbARR_WBOOK_ID.toString().split(";");

				szMsg = "["+szOperationName+"] ----------- 3.1. BOOK-IN/OUT 스케줄기동 START .... 예약건수 :: " + iWBookInsCnt + " 작업예약ID :: " + sbARR_WBOOK_ID.toString();
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				for(int ii=0; ii<iWBookInsCnt; ii++) {

	    			//-----------------------------------------------------
	        		// 스케쥴 코드로 크레인작업지시를 조회하여 1건도 없을경우 스케쥴 기동
	    			//-----------------------------------------------------
	        		rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
	        		recPara  = JDTORecordFactory.getInstance().create();
	        		recPara.setField("YD_SCH_CD", 		szYdSchCd);

	        		iCrnSchCnt = ydCrnSchDao.getByYdSchCd(recPara, rsResult);
	    			if (iCrnSchCnt >= 1) {
	        			szMsg = "["+szOperationName+"] ----------- BOOK-IN/OUT 스케줄 기동 SKIP :: " + ii + " .. 스케쥴 건수 .. " + iCrnSchCnt;
	        			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	    				break;
	    			}
 
					//------------------------------------------------------------------------------------------------
					// 스케쥴 기동 (BOOK-OUT)
					//------------------------------------------------------------------------------------------------
					recSchPara 	= JDTORecordFactory.getInstance().create();
					recSchPara.setField("MSG_ID", 			"YDYDJ");			//TC코드
					recSchPara.setField("YD_EQP_ID", 		szYdWrkCrn);		//크레인설비ID
					recSchPara.setField("YD_SCH_CD",		szYdSchCd);			//크레인스케줄코드
					recSchPara.setField("YD_WBOOK_ID",		arrWBookId[ii]);	//작업예약ID
					recSchPara.setField("REGISTER", 		szRegister);
					recSchPara.setField("MODIFIER", 		szRegister);
					recSchPara.setField("YD_TO_LOC_GUIDE",	szYdToLocGuide);	//야드To위치Guide
					recSchPara.setField("CHK_FROM_LOC",		"N");				//RT작업일경우 권상예약 체크 안하도록 보완

					szMsg    = "["+szOperationName+"] ----------- 3.2.BOOK-IN/OUT 스케줄기동 START :: " + ii + " >>>> " + arrWBookId[ii];
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			        EJBConnector ejbConn = new EJBConnector("default", "JPlateYdCrnSchYdPSeEJB", this);
			        szRtnMsg = (String)ejbConn.trx("procCrnSchMainYdP", new Class[] { JDTORecord.class }, new Object[] { recSchPara });

					szMsg    = "["+szOperationName+"] ----------- 3.3.BOOK-IN/OUT 스케줄기동 END :: " + + ii + " >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			        if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
			        	// 크레인 작업지시 전송후에는 오류발생 데이타 SKIP
			        	if (iSchOkCnt > 0) {
							szMsg    = "["+szOperationName+"] BOOK-IN/OUT 스케줄기동시 오류 발생 .... SKIP >>>> " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			        	} else {
				        	szRtnMsg = "BOOK_IN/OUT 스케줄기동 오류 .. <br>" + szRtnMsg;
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
			        	}
			        } else {
			        	iSchOkCnt ++;
			        }
				}
			}

			szMsg = "["+szOperationName+"] 메소드 끝 >>>> 스케줄 호출 건수 :: " + iSchOkCnt;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }

		return JPlateYdConst.RETN_CD_SUCCESS;
	}

    /**
     * 오퍼레이션명 : Book-In/Book-Out 취소처리
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● Stirng pYdCrnSchId, String pYdWrkProgStat
     * @return ● nRtnVal
     * @throws ● DAOException
     */
	public String  procP2P3BookInOutCancel(JDTORecord msgRecord) throws DAOException {
		// 레코드선언
		JDTORecordSet 	rsResult  	= null;
		JDTORecord 		inRec 		= null;
		JDTORecord 		outRecord	= JDTORecordFactory.getInstance().create();
		JDTORecord 		tempRec 	= null;
		JDTORecord 		recPara   	= null;

		// 변수 선언
        String 	szMethodName  		= "procP2P3BookInOutCancel";
        String	szRtnMsg			= JPlateYdConst.RETN_CD_SUCCESS;
    	String 	szMsg         		= "";
    	String 	szOperationName 	= "Book-In/Book-Out 취소처리";


		String	szYdStkColGp		= "";
		String	szYdStkBedNo		= "";
        String 	szModifier			= "";			// 등록자, MSG_ID
        String	szYdWrkProgStat		= "";			// 크레인작업진행상태
        String	szYdCrnSchId		= "";			// 크레인작업지시ID
  //      String 	szRegister			= "";			// 등록자, MSG_ID
        String	szFromLoc			= "";
        String	szToLoc				= "";
        
    	int 	intRtnVal 			= 0;

    	//DAO
    	JPlateYdStockDAO    ydStockDao   	= new JPlateYdStockDAO();
    	JPlateYdWrkbookDAO	ydWrkbookDao 	= new JPlateYdWrkbookDAO();
    	JPlateYdCrnSchDAO 	ydCrnSchDao		= new JPlateYdCrnSchDAO();
    	JPlateYdStkLyrDAO   ydStkLyrDao		= new JPlateYdStkLyrDAO();

		EJBConnector 		ejbConn 	= null;

        try {
        	szMsg = "["+szOperationName+"] ---- 메소드 시작  >>>> " + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			String szPlateId 		= ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO");				// PLATE_NO
			String szOperationType 	= ydDaoUtils.paraRecChkNull(msgRecord, "OPERATION_TYPE");			// 1:Book In, 2:Book Out
			String szTrkZoneNo		= ydDaoUtils.paraRecChkNull(msgRecord, "PL_TRCK_ZONE_ASGN");		// PL_TRCK_ZONE_ASGN
			
			String szYdNo			= ydDaoUtils.paraRecChkNull(msgRecord, "YARD_NO");                  // YARD_NO
			String szReasonCode		= ydDaoUtils.paraRecChkNull(msgRecord, "REASON_CODE");				// Book-Out원인 (888:TEST , 999:북아웃취소)
			String szOperationMode	= ydDaoUtils.paraRecChkNull(msgRecord, "PL_BOOK_OUT_MOD");			// 1:one time 2:Start 3:End
			
			if (!"1".equals(szOperationMode) && !"2".equals(szOperationMode)) {
				szRtnMsg = "수신전문중 OPERATION_MODE ERROR";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			//------------------------------------------------------------------------------------------------
			//------------------------------------------------------------------------------------------------
			// 1. 파라미터 체크
			// 1.1 북인   시 trkZoneNo -> to위치       ,   yardNo -> from 위치
			// 1.1 북아웃시 trkZoneNo -> from 위치 ,   yardNo -> to위치
			//------------------------------------------------------------------------------------------------
			if ("1".equals(szOperationType)) { // Book In

				szFromLoc 	= szYdNo;
				szToLoc 	= szTrkZoneNo;

				// From 위치
				if (szYdNo == null || szYdNo.length() < 6) {

					// 저장위치 없이 판번호만 들어 왔을때 처리
					if (!"".equals(szPlateId)) {

						szMsg    = "["+szOperationName+"] 저장위치 없이 판번호만 들어 왔을때 .... SKIP";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					} else {
						szRtnMsg = "수신전문중 FROM위치 ERROR >>>> " + szYdNo;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}

				if (szYdNo.length() > 1) {
					if (!"P".equals(ydUtils.substr(szYdNo, 0, 1))) {
						szRtnMsg = "수신전문중 FROM위치 ERROR >>>>>>> " + szYdNo;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}
				
			// To 위치
				
				// TRK ZONE NO를 야드저장위치로 변경
				if (!"".equals(szTrkZoneNo) && szTrkZoneNo.length() == 5) {
					// ZONE NO를 야드저장위치로 변경 :: 2300 >>>> FART01
					szToLoc = JPlateYdCommonUtils.getY2RtZoneToLoc(szTrkZoneNo);
				}
				
				if (szToLoc == null || szToLoc.equals("")) {
					szRtnMsg = "수신전문중 TO위치 ERROR >>>>>>> " + szTrkZoneNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				} 
				if (!"P".equals(ydUtils.substr(szToLoc, 0, 1))) {
					szRtnMsg = "수신전문중 TO위치 ERROR >>>>>>> " + szTrkZoneNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

			} else if ("2".equals(szOperationType)) {		// Book Out

				szFromLoc 	= szTrkZoneNo;
				szToLoc 	= szYdNo;

				// PLATE_ID
				// BOOK-OUT일때는 2,3의 경우는 미존재함 즉 'PL999999'는 미존재
				if ("".equals(szPlateId) || "PL999999".equals(szPlateId)) {
					szRtnMsg = "수신전문중 PLATE_ID ERROR";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// from 위치				
				
				// TRK ZONE NO를 야드저장위치로 변경
				if (!"".equals(szTrkZoneNo) && szTrkZoneNo.length() == 5) {
					// ZONE NO를 야드저장위치로 변경 :: 2300 >>>> FART01
					szFromLoc = JPlateYdCommonUtils.getY2RtZoneToLoc(szTrkZoneNo);
				}
				
				if (szFromLoc == null || szFromLoc.equals("")) {
					szRtnMsg = "수신전문중 FROM위치 ERROR >>>>>>> " + szTrkZoneNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				} 
				if (!"P".equals(ydUtils.substr(szFromLoc, 0, 1))) {
					szRtnMsg = "수신전문중 FROM위치 ERROR >>>>>>> " + szTrkZoneNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				
				// TO 위치
				if (!"P".equals(ydUtils.substr(szYdNo, 0, 1)) || szYdNo.length() < 6) {
					szRtnMsg = "수신전문중 TO위치 ERROR >>>>> " + szYdNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// TO위치의 동과 FROM위치의 동이 상이할 경우 TO위치의 동을 스케줄 기준으로 변경 : 
				String sToBay = ydUtils.substr(szToLoc,   1, 1);
				String sFrBay = ydUtils.substr(szFromLoc, 1, 1);
				if (!sToBay.equals(sFrBay)) {
					szMsg   = "["+szOperationName+"] To위치의 동과 From위치의 동이 상이하여 스케쥴 기준 적용 >>>>" + szToLoc + "," + szFromLoc;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					szToLoc = ydUtils.substr(szFromLoc, 0, 2);
				}

				// Book-Out원인
				if ("".equals(szReasonCode)) {
					szRtnMsg = "수신전문중 Book-Out원인 ERROR";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szMsg;
				}
			}

			// Book In 시 저장위치 조회
			if ("1".equals(szOperationType)) {

				if ("".equals(szPlateId)) {			// 재료번호 미존재시

	    			szYdStkColGp = szFromLoc;

					szMsg    = "["+szOperationName+"] 북인 취소 작업중 .... 재료번호 미존재 >>>> 저장위치 :: " + szYdStkColGp;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				} else {
					// 베드정보 조회
					rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
					inRec    = JDTORecordFactory.getInstance().create();
					inRec.setField("STL_NO", 		szPlateId);             	// 재료번호
					inRec.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);
	    			intRtnVal = ydStockDao.getYdStockWithLoc(inRec, rsResult);
					if (intRtnVal < 1) {
						szRtnMsg = "야드재료가 미존재 합니다 .... 재료번호:" + szPlateId;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

	    			rsResult.first();
					tempRec = JDTORecordFactory.getInstance().create();
	    			tempRec = rsResult.getRecord();

	    			szYdStkColGp 	= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_COL_GP");
				}
			}

			//------------------------------------------------------------------------------------------------
			// 2. 해당 재료 작업예약/스케쥴 존재여부 확인
			//------------------------------------------------------------------------------------------------
			if (!"".equals(szPlateId)) {

				// ------------------------------------------------------------------------
				// 2.1. 크레인 작업지시 존재여부 확인
				// ------------------------------------------------------------------------
				rsResult = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
				recPara  = JDTORecordFactory.getInstance().create();
				tempRec  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 		szPlateId);             	// 재료번호
				recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);

				intRtnVal = ydCrnSchDao.getExistByStlNo(recPara, rsResult);
				if (intRtnVal <= 0) {
					szMsg    = "["+szOperationName+"] 해당 재료"+szPlateId+"로 크레인 작업지시 미존재! .... SKIP";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				} else {
					rsResult.first();
					tempRec = rsResult.getRecord();
					szYdWrkProgStat = ydDaoUtils.paraRecChkNull(tempRec, "YD_WRK_PROG_STAT");
					szYdCrnSchId 	= ydDaoUtils.paraRecChkNull(tempRec, "YD_CRN_SCH_ID");

					if (!"W".equals(szYdWrkProgStat) && !"1".equals(szYdWrkProgStat)) {

						szRtnMsg = "재료번호[" + szPlateId + "]의 크레인작업지시[" + szYdCrnSchId + "]를 취소할수 없는 상태! " + szYdWrkProgStat;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;

					}

					// 작업상태가 2,3이 아닌 경우 크레인 작업지시 취소 처리
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(tempRec, "YD_CRN_SCH_ID"));
					recPara.setField("YD_SCH_CD",     ydDaoUtils.paraRecChkNull(tempRec, "YD_SCH_CD"));
					recPara.setField("DEL_YN",        "N");
					recPara.setField("MODIFIER",      szModifier);

					// 크레인 스케줄취소 처리
					szMsg = "["+szOperationName+"] 작업지시 취소 시작";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					outRecord	= JDTORecordFactory.getInstance().create();
					ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
					outRecord 	= (JDTORecord)ejbConn.trx("cancelJPlateYdCrnSch", new Class[] { JDTORecord.class }, new Object[] { recPara });

					szMsg = "["+szOperationName+"] ---- 작업지시 취소 종료!! >>>> " + outRecord.toString();
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				}

				// ------------------------------------------------------------------------
				// 2.2. 작업예약 존재여부 확인
				// ------------------------------------------------------------------------
				rsResult = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
				recPara  = JDTORecordFactory.getInstance().create();
				tempRec  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 		szPlateId);             	// 재료번호
				recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);

				intRtnVal = ydWrkbookDao.getExistByStlNo(recPara, rsResult);
				if (intRtnVal <= 0) {
					szMsg    = "["+szOperationName+"] 해당 재료"+szPlateId+"로 작업예약 미존재! .... SKIP";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				} else {
					rsResult.first();
					tempRec = rsResult.getRecord();

					// 작업 예약 삭제 처리
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID",	ydDaoUtils.paraRecChkNull(tempRec, "YD_WBOOK_ID"));
					recPara.setField("MODIFIER",    szModifier);

					szMsg = "["+szOperationName+"] ---- 작업예약 취소 시작";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					outRecord	= JDTORecordFactory.getInstance().create();
					ejbConn 	= new EJBConnector("default", "JPlateYdJspSeEJB", this);
					outRecord 	= (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { recPara });

					szMsg = "["+szOperationName+"] ---- 작업예약 취소 종료!! >>>> " + outRecord.toString();
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				}
			}

			//------------------------------------------------------------------------------------------------
			// 3. 야드재료 삭제 (BOOK-OUT시)
			//------------------------------------------------------------------------------------------------
			if ("2".equals(szOperationType)) {		// Book Out

				// ------------------------------------------------------------------------
				// 3.1. 저장위치 정보 조회
				// ------------------------------------------------------------------------
				szMsg = "[ " +szOperationName + "] 3.1.저장위치 정보 조회 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				rsResult = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
				recPara  = JDTORecordFactory.getInstance().create();
				tempRec  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 		szPlateId);             	// 재료번호
				recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);

				intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsResult);
				if (intRtnVal <= 0) {
					szRtnMsg = "야드재료가 미존재 합니다 .... 재료번호:" + szPlateId;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

		        szYdStkColGp	= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_COL_GP");
		        szYdStkBedNo	= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_BED_NO");

				szMsg = "[ " +szOperationName + "] 3.1.저장위치 정보 조회 END >>>> " + szYdStkColGp + szYdStkBedNo;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				// ------------------------------------------------------------------------
				// 3.2. 현재위치가 RT인지 체크
				// ------------------------------------------------------------------------
				if (!"RT".equals(ydUtils.substr(szYdStkColGp,2,2))) {
					szRtnMsg = "현재위치가 RT가 아닙니다 .... 저장위치:" + szYdStkColGp + szYdStkBedNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				//	return szRtnMsg;
				}

				// ------------------------------------------------------------------------
				// 3.3. 야드 L2 저장품재원 삭제전문 전송
				// ------------------------------------------------------------------------
				szMsg = "[ " +szOperationName + "] 3.2.야드L2 저장품제원 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("JMS_TC_CD", 			"YDY2L002");                            // TC-CODE
				recPara.setField("YD_GP", 				JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
				recPara.setField("YD_STK_COL_GP", 		szYdStkColGp);            				// 야드적치열구분
				recPara.setField("YD_STK_BED_NO", 		szYdStkBedNo);    						// 야드적치BED번호
				recPara.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
				recPara.setField("STL_NO", 				szPlateId);	        					// 재료번호
				recPara.setField("MSG_GP", 				"D");	        						// 전문구분
				szRtnMsg = ydDelegate.sendMsg(recPara);

				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				if (!"".equals(szYdStkColGp)) {
				// ------------------------------------------------------------------------
				// 3.4. 조업 L3 저장위치변경정보 전송
				// ------------------------------------------------------------------------
	            szMsg    = "["+szOperationName+"] 3.3.1후판조업 저장위치변경정보 전송 ---- START";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	            recPara = JDTORecordFactory.getInstance().create();
		        recPara.setField("MSG_ID", 				"YDPRJ011");
		        recPara.setField("YD_STK_COL_FR", 		szYdStkColGp);							// From적치열
		        recPara.setField("YD_STK_BED_FR", 		szYdStkBedNo);							// From적치BED
		        recPara.setField("YD_STK_COL_TO", 		"");									// TO적치열
		        recPara.setField("YD_STK_BED_TO", 		"");									// TO적치BED
		        recPara.setField("YD_EQP_WRK_SH", 		"1");									// 야드설비작업매수
		        recPara.setField("ARR_STL_NO", 			szPlateId);

		        szRtnMsg = ydDelegate.sendMsg(recPara);

				szMsg = "["+szOperationName+"] 1후판조업 저장위치변경정보 전송 완료>>>>" + szRtnMsg;
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				}

				// ------------------------------------------------------------------------
				// 3.5. 재료정보 삭제처리
				// ------------------------------------------------------------------------
				szMsg = "[ " +szOperationName + "] 3.4. 재료정보 삭제처리";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO",				szPlateId);				// 재료번호
				recPara.setField("YD_GP",				JPlateYdConst.YD_GP_P_PLATE_YARD);
				recPara.setField("MODIFIER", 			szModifier);

				intRtnVal = ydStockDao.delYdStock(recPara);
				if (intRtnVal < 0) {
					szRtnMsg = "재료정보 삭제처리시 오류 발생! .... 오류코드 :" + Integer.toString(intRtnVal);
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				//	return szRtnMsg;
				}

				// ------------------------------------------------------------------------
				// 3.6. 저장위치 CLEAR
				// ------------------------------------------------------------------------
				szMsg = "[ " +szOperationName + "] 3.5. 저장위치 CLEAR";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 				szPlateId);             // 재료번호
				recPara.setField("YD_STK_LYR_MTL_STAT", "C");             		// 야드적치단재료상태
				recPara.setField("YD_GP",				JPlateYdConst.YD_GP_P_PLATE_YARD);
				recPara.setField("MODIFIER", 			szModifier);

				intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
				if (intRtnVal < 0) {
					szRtnMsg = "저장위치 삭제 처리시 오류 발생! .... 오류코드 :" + Integer.toString(intRtnVal);
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				//	return szRtnMsg;
				}
	        }

			szMsg = "["+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }
		return JPlateYdConst.RETN_CD_SUCCESS;
	}
    /**
     * 오퍼레이션명 : 크레인 작업지시요구 에러시 응답메시지 전송
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
     */
    public String sendErrCrnWrkOrdReqYdP(JDTORecord msgRecord, String pYdL3Msg)throws DAOException  {

    	JPlateYdDelegate   	ydDelegate   = new JPlateYdDelegate();

    	JDTORecord recInPara 		= null;

        String 	szMsg              	= "";
        String	szRtnMsg			= "";
        String 	szMethodName       	= "sendErrCrnWrkOrdReqYdP";
        String 	szOperationName		= "크레인작업지시 요구 응답메시지 전송";

        String 	szEqpId             = "";
        String 	szYdWrkProgStat		= "";
        String 	szYdSchCd			= "";
        String	szYdCrnSchId		= "";
        String	szYdL3Msg			= "";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.12 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
                
        try {

    		szMsg = "[" + szOperationName + "] 응답메시지 전송 .... START";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        szEqpId         = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	        szYdWrkProgStat	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");
	        szYdSchCd		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
	        szYdCrnSchId	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");

	        if (pYdL3Msg != null && !"".equals(pYdL3Msg)) {
	        	szYdL3Msg = pYdL3Msg;
	        }

            /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 1후판정정 야드L2 크레인작업실적응답 전송  - YDY2L005
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        	recInPara = JDTORecordFactory.getInstance().create();
       		recInPara.setField("MSG_ID", 				"YDY2L005");
        	recInPara.setField("YD_EQP_ID", 			szEqpId);									//야드설비ID
        	recInPara.setField("YD_WRK_PROG_STAT", 		szYdWrkProgStat);							//야드작업진행상태
        	recInPara.setField("YD_SCH_CD", 			szYdSchCd);									//야드스케줄코드
        	recInPara.setField("YD_CRN_SCH_ID", 		szYdCrnSchId);								//야드크레인스케줄ID
        	recInPara.setField("YD_L2_WR_GP", 			JPlateYdConst.CRN_WRK_RE_WO_DMD);			//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
        	if ("".equals(szYdL3Msg)) {
            	recInPara.setField("YD_L3_HD_RS_CD",	JPlateYdConst.CRN_WRK_RE_CD_NO_WRK);		//야드L3처리결과코드 - 9999 : 크레인작업지시가 없을 경우
        	} else {
            	recInPara.setField("YD_L3_HD_RS_CD",	JPlateYdConst.CRN_WRK_RE_CD_ERROR);			//야드L3처리결과코드 - 8888 : 오류
        		recInPara.setField("YD_L3_MSG", 		szYdL3Msg);
        	}
        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.12 recInPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
        	recInPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
			        	
        	szRtnMsg = ydDelegate.sendMsg(recInPara);

    		szMsg = "[" + szOperationName + "] 응답메시지 전송 .... END >>>> " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		} catch(Exception e) {
			return e.getMessage();
		}

		szMsg = "크레인 작업지시(" + szMethodName + ") 완료";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;

	} // end of sendErrCrnWrkOrdReqYdP()	
    
    /**
     * 오퍼레이션명 : 해당 크레인 설비 스케줄 작업 상태를  변경 [명령선택]
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● Stirng pYdCrnSchId, String pYdWrkProgStat
     * @return ● nRtnVal
     * @throws ● DAOException
     */
	public String  updCrnWrkProgStatYdP(String pYdCrnSchId, String pYdEqpId, String pYdWrkProgStat, String pModifier, String logId) throws DAOException {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.12.06 updCrnWrkProgStatYdP argument 에 logId 항목 추가 개선
//	public String  updCrnWrkProgStatYdP(String pYdCrnSchId, String pYdEqpId, String pYdWrkProgStat, String pModifier) throws DAOException {
////////////////////////////////////////////////////////////////////////////////////////
		// 레코드선언
		JDTORecord setRecord 	= null;
		JDTORecord recRecord 	= null;
		JDTORecord recPara 		= null;
		JDTORecord recL2Msg		= null;

		// 변수 선언
        String 	szMethodName  	= "updCrnWrkProgStatYdP";
        String	szRtnMsg		= "";
    	String 	szMsg         	= "";
    	String 	szOperationName = "설비 스케줄 작업 상태  변경(" + pYdWrkProgStat + ")";
    	String 	szYdWrkProgStat	= "";
    	String	szYdEqpId		= "";
    	String	szYdUpWoLoc		= "";
    	String	szYdCrnSchId	= "";		// 기존작업지시 크레인스케줄 ID
		
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 logId 개선 
    	if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                    // log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

    	//JDTORecordSet
    	JDTORecordSet rsResult  = null;

    	//DAO
    	JPlateYdCrnSchDAO 	ydCrnSchDao = new JPlateYdCrnSchDAO();
    	JPlateYdEqpDAO		ydEqpDAO	= new JPlateYdEqpDAO();

    	int intRtnVal = 0;

        try {
        	szMsg = "[" + szOperationName + "] 메소드 시작 ";
        	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//------------------------------------------------------------------------------------------------
			//  크레인 작업지시  스케줄 조회
			//------------------------------------------------------------------------------------------------
			rsResult  	= JDTORecordFactory.getInstance().createRecordSet("ydTemp");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", 	pYdCrnSchId);
        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
			recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
        	
			intRtnVal = ydCrnSchDao.getYdCrnSchYdP(recPara, rsResult);

			if (intRtnVal < 0) {
				szRtnMsg = "스케줄 조회 ERROR";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szRtnMsg;
			} else if (intRtnVal == 0) {
				szMsg = "[" + szOperationName + "] 변경할 데이터가 없습니다.";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
				return JPlateYdConst.RETN_CD_SUCCESS;
			}
			szMsg = "[" + szOperationName + "] 크레인 스케줄 조회 성공!";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//------------------------------------------------------------------------------------------------
			// 명령선택 가능여부 체크
			//------------------------------------------------------------------------------------------------
			recRecord = JDTORecordFactory.getInstance().create();
			rsResult.first();
			recRecord = rsResult.getRecord();

			szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recRecord, "YD_WRK_PROG_STAT");
			szYdEqpId		= ydDaoUtils.paraRecChkNull(recRecord, "YD_EQP_ID");

			if (JPlateYdConst.YD_EQP_STAT_UP_CMPL.equals(szYdWrkProgStat) || 			// 권상완료
				JPlateYdConst.YD_EQP_STAT_DN_WO.equals(szYdWrkProgStat)) {				// 권하지시

				szRtnMsg = "권상처리후 크레인명령선택 불가.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
				return szRtnMsg;
        	}

			// 권상위치와 설비ID 체크 (C동 크레인이 다른동의 작업 선택 못하도록 보완)
			szYdUpWoLoc 	= ydDaoUtils.paraRecChkNull(recRecord, "YD_UP_WO_LOC");
			szYdUpWoLoc		= ydUtils.substr(szYdUpWoLoc, 0, 2);
			if (!szYdUpWoLoc.equals(ydUtils.substr(pYdEqpId, 0, 2))) {
				szRtnMsg = "해당 크레인이 크레인명령선택 불가한 FROM위치.";
				szMsg	 = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
				return szRtnMsg;
			}

			// 현재 명령선택중인 작업지시 정보를 체크
			rsResult  	= JDTORecordFactory.getInstance().createRecordSet("ydTemp");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("YD_EQP_ID", 	pYdEqpId);
        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
			recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
        	
			intRtnVal = ydCrnSchDao.getWrkProgStatYdP(recPara, rsResult);
			if (intRtnVal > 0) {

				// 기존의 명령선택 정보 CLEAR
				setRecord = JDTORecordFactory.getInstance().create();
				setRecord.setField("YD_WRK_PROG_STAT", 	JPlateYdConst.YD_EQP_STAT_IDLE);
				setRecord.setField("MODIFIER", 			pModifier);
				setRecord.setField("YD_EQP_ID", 		szYdEqpId);
				intRtnVal = ydCrnSchDao.updProgStatByEqpId(setRecord);

				rsResult.first();

				for (int ii=0; ii<rsResult.size(); ii++) {

					recRecord = JDTORecordFactory.getInstance().create();
					recRecord = rsResult.getRecord();

					szYdCrnSchId    = ydDaoUtils.paraRecChkNull(recRecord, "YD_CRN_SCH_ID");
					szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recRecord, "YD_WRK_PROG_STAT");

					szMsg = "[" + szOperationName + "] >>>> 기존지시 " + szYdCrnSchId + ", 작업 상태  >>>> " + szYdWrkProgStat;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					// 기존의 지시가 명령선택중이면 지시취소정보 전송
					if (JPlateYdConst.YD_EQP_STAT_UP_WO.equals(szYdWrkProgStat)) {						// '1' : 권상지시

				        recL2Msg = JDTORecordFactory.getInstance().create();
	            		//recL2Msg.setField("MSG_ID", 				"YDY2L004");						// 크레인 작업지시
	            		recL2Msg.setField("MSG_ID", 				"YDY2L004V2"						);		// 크레인 작업지시
				        recL2Msg.setField("YD_CRN_SCH_ID",    		szYdCrnSchId						);		// 크레인 스케줄 ID
				        recL2Msg.setField("YD_WRK_PROG_STAT",		JPlateYdConst.YD_EQP_STAT_IDLE	);		// 'W' : 명령선택 대기
				        recL2Msg.setField("MSG_GP",           		"U"									);

						szMsg = "[" + szOperationName + "] >>>> 명령선택 취소 전문 전송 .. 데이타 >>>> " + recL2Msg.toString();
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recL2Msg에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
						recL2Msg.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
						        	
						szRtnMsg = ydDelegate.sendMsg(recL2Msg);

						szMsg = "[" + szOperationName + "] >>>> 명령선택 취소 전문 전송 .. 결과 >>>> " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					} else if (JPlateYdConst.YD_EQP_STAT_UP_CMPL.equals(szYdWrkProgStat) || 	// 2 , 권상완료
							   JPlateYdConst.YD_EQP_STAT_DN_WO.equals(szYdWrkProgStat)) { 		// 3 , 권하지시

						szRtnMsg = "권상처리후 크레인명령선택 불가.";
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						
						return szRtnMsg;

					}
				}

			} else {
				szMsg = "[" + szOperationName + "] 현재 명령 선택중인 기존 작업지시는 없습니다.";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			}

			// 야드작업진행상태 변경
			setRecord = JDTORecordFactory.getInstance().create();
			setRecord.setField("YD_WRK_PROG_STAT", 	pYdWrkProgStat);
			setRecord.setField("MODIFIER", 			pModifier);
			setRecord.setField("YD_CRN_SCH_ID", 	pYdCrnSchId);
			setRecord.setField("YD_EQP_ID", 		pYdEqpId);
			setRecord.setField("YD_WORD_DT", 		JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));	// 명령선택일시

			intRtnVal = ydCrnSchDao.updYdCrnWrkProgStat(setRecord);

			if (intRtnVal < 0) {
				szRtnMsg = "UPDATE ERROR";
				szMsg = "[" + szOperationName + "] UPDATE ERROR";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szRtnMsg;

			} else if (intRtnVal == 0) {
				szRtnMsg = "UPDATE 할 스케줄이 없습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
				return JPlateYdConst.RETN_CD_SUCCESS;
			}

			szMsg = "[" + szOperationName + "] 스케줄 ID : " + pYdCrnSchId + "정보를 변경하였습니다.";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//------------------------------------------------------------------------------------------------
			// 야드설비상태 변경 [YD_EQP_STAT]
			//------------------------------------------------------------------------------------------------
			// 야드작업진행상태 변경
			setRecord = JDTORecordFactory.getInstance().create();
			setRecord.setField("MODIFIER", 			pModifier);
			setRecord.setField("YD_EQP_STAT", 		pYdWrkProgStat);
			setRecord.setField("YD_EQP_ID", 		pYdEqpId);

			intRtnVal = ydEqpDAO.updYdEqpStat(setRecord);
			if (intRtnVal < 0) {
				szRtnMsg = "야드작업진행상태 변경시 오류";
				szMsg = "[" + szOperationName + "] UPDATE ERROR";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szRtnMsg;
			}

			szMsg = "[" + szOperationName + "] 메소드 끝 ";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }

		return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of updCrnWrkProgStatYdP()
	
	/**
	 *  작업예약등록(GAS장추출)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String insCncOutWBook(JDTORecord inDto) throws DAOException {

		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();

		String 	szRtnMsg 		= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMethodName	= "insCncOutWBook";
		String 	szOperationName = "작업예약등록(GAS장추출)";
		String 	szLogMsg 		= "";
		String	szStlNo			= "";

		try {

			szStlNo = ydDaoUtils.paraRecChkNull(inDto, "ARR_STL_NO");

			String[] arrStlNo = szStlNo.split(";");

			szLogMsg = "JSP-SESSION ["+szOperationName+"] 시작 :: " + szStlNo;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			recPara.setField("PL_MPL_NO", 			ydDaoUtils.paraRecChkNull(inDto, 	"PL_MPL_NO"));
			recPara.setField("PL_WR_GDS_TOT_SH", 	ydDaoUtils.paraRecChkNull(inDto,	"PL_WR_GDS_TOT_SH"));
			recPara.setField("MODIFIER", 			ydDaoUtils.paraRecChkNull(inDto, 	"MODIFIER"));
			recPara.setField("ARR_STL_NO", 			ydDaoUtils.paraRecChkNull(inDto, 	"ARR_STL_NO"));
			recPara.setField("ARR_BS_END", 			ydDaoUtils.paraRecChkNull(inDto, 	"ARR_BS_END"));
			recPara.setField("YD_SCH_CALL",			"Y");		// 가스장추출 스케줄 기동여부

			for (int ii=0; ii<arrStlNo.length; ii++) {
				recPara.setField("STL_NO"+(ii+1),		arrStlNo[ii]);
			}

			// GAS장 절단 재료 등록 및 추출스케줄 기동
			EJBConnector ejbConn = new EJBConnector("default", this);
			szRtnMsg = (String)ejbConn.trx("JPlateYdL3RcvSeEJB", "procPRGasCutResult", recPara);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szLogMsg = "JSP-SESSION ["+szOperationName+"] 호출결과 :: " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION ["+szOperationName+"] 끝  .. 결과 :: " + szRtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of insCncOutWBook 
	
	//---------------------------------------------------------------------------	
	
	/**
	 * 오퍼레이션명 : 1후판정정야드L2 후판L2제품번호요구 수신 (Y2YDL015)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public void procY2YDL015(JDTORecord msgRecord) throws DAOException {

		// 델리게이트 호출을 위한 편집 레코드 데이터
		JDTORecord setMsgRecord 	= JDTORecordFactory.getInstance().create();

		JPlateYdDelegate ydDelegate	= new JPlateYdDelegate();

		String szMethodName        	= "procY2YDL015";
		String szMsg               	= "";
		String szOperationName     	= "1후판정정야드L2 후판L2제품번호요구 수신";
		String szRcvTcCode         	= ydUtils.getTcCode(msgRecord);

		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "\n=========Y2YDL015========\n", JPlateYdConst.DEBUG);
		szMsg = "[" + szOperationName + "] 수신데이타 :: " + msgRecord.toString();
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		if (szRcvTcCode == null) {
			szMsg = SZ_SESSION_NAME + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return;
		}

		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		}

		try {
			//=============================================================
			// Log 테이블 등록
			//=============================================================
			szMsg = "[1후판정정] 후판L2제품번호요구 수신 >>>> ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

            String szPlMtlNo = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO");
            
            // 파라미터 Check
            if("".equals(szPlMtlNo)){
    			szMsg = "파라미터 Check중 PL_MTL_NO(후판재료번호) Error : " + szPlMtlNo;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			return;
            }
            	
        	int intRtnVal = 0;
        	JDTORecord 		recPara     = null;
    		JDTORecordSet 	rsResult    = null;
    		
    		JPlateYdStockDAO ydStockDao	= new JPlateYdStockDAO();
    		
        	rsResult = JDTORecordFactory.getInstance().createRecordSet("rsTemp");
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO"	,szPlMtlNo);	// 재료번호
			recPara.setField("YD_GP"	,"P");			// 야드구분
			intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsResult);

			// 재료 정보 존재여부 체크
			if (intRtnVal < 1) {
				// 기존 TB_YD_SHRSTOCK에 미존재시 등록처리함
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("REGISTER"	,"Y2YDL015");	// 등록자
				recPara.setField("MODIFIER"	,"Y2YDL015");	// 수정자
				recPara.setField("STL_NO"	,szPlMtlNo);	// 재료번호

    			intRtnVal = ydStockDao.insYdStockBookOut(recPara);
				if (intRtnVal <= 0) {
					szMsg = "[" +szOperationName + "] 야드재료 등록 ERROR .. " + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				}
			}
         	
			// 델리게이트 호출을 위한 레코드 편집
			setMsgRecord.setField("JMS_TC_CD"	, "YDY2L008"); // TC-CODE
			setMsgRecord.setField("PL_MTL_NO"	, szPlMtlNo);  // 후판재료번호

			// 델리게이트 호출
			ydDelegate.sendMsg(setMsgRecord);
			
		}catch(DAOException e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        } 
	} // end of procY2YDL015
	
	/**
	 * 오퍼레이션명 : 1후판정정야드L2 저장위치제원정보 수신 (Y2YDL016)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public void procY2YDL016(JDTORecord msgRecord) throws DAOException {

    	JPlateYdCommDAO commDao					= new JPlateYdCommDAO();
    	JPlateYdStockDAO JPlateYdDao 			= new JPlateYdStockDAO();
    	JPlateYdStkLyrDAO   JPlateYdStkLyrDao	= new JPlateYdStkLyrDAO();
    	
    	int intRtnVal 			= 0;
    	
    	String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
    	
		//Query 실행시 파라메터 전달용 JDTORecord
		JDTORecord jrParam	 = JDTORecordFactory.getInstance().create();  
		
		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
		
		// 델리게이트 호출을 위한 편집 레코드 데이터
		JDTORecord setMsgRecord 	= JDTORecordFactory.getInstance().create();
        JDTORecordSet rsResult      = null;

		JPlateYdDelegate ydDelegate	= new JPlateYdDelegate();

		String szMethodName        	= "procY2YDL016";
		String szMsg               	= "";
		String szOperationName     	= "1후판정정야드L2 저장위치제원정보 수신";
		String szRcvTcCode         	= ydUtils.getTcCode(msgRecord);

		String szCURR_YD_STR_LOC    = "";
		String szCURR_YD_STK_BED_NO = "";
		String szCURR_YD_STK_LYR_NO = "";
		
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "\n=========Y2YDL016========\n", JPlateYdConst.DEBUG);
		szMsg = "[" + szOperationName + "] 수신데이타 :: " + msgRecord.toString();
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		if (szRcvTcCode == null) {
			szMsg = SZ_SESSION_NAME + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return;
		}

		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		}

		try {
			//=============================================================
			// Log 테이블 등록
			//=============================================================
			szMsg = "[1후판정정] 저장위치제원정보 수신 >>>> ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			String szPL_L2_TRK_NO 	= ydDaoUtils.paraRecChkNull(msgRecord,"PL_L2_TRK_NO");
			String szPL_MTL_NO 		= ydDaoUtils.paraRecChkNull(msgRecord,"PL_MTL_NO");	
			String szYD_STR_LOC		= ydDaoUtils.paraRecChkNull(msgRecord,"YD_STR_LOC");
			String szYD_STK_LYR_NO	= ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_LYR_NO");
            
            // 파라미터 Check
            //if("".equals(szPL_L2_TRK_NO)){
    		//	szMsg = "파라미터 Check중 PL_L2_TRK_NO(후판L2제품번호) Error : 빈 값 " + szPL_L2_TRK_NO;
    		//	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    		//	return;
            //}

            if("".equals(szYD_STR_LOC)){
    			szMsg = "파라미터 Check중 YD_STR_LOC(야드저장위치) Error : 빈 값 " + szYD_STR_LOC;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			return;
            }

            if(szYD_STR_LOC.length() < 8){
    			szMsg = "파라미터 Check중 YD_STR_LOC(야드저장위치) Error : 길이가 8이 아닙니다. " + szYD_STR_LOC;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			return;
            }
            
            if(!"P".equals(szYD_STR_LOC.substring(0,1))){
    			szMsg = "파라미터 Check중 YD_STR_LOC(야드저장위치) Error : 야드구분이 P가 아닙니다. " + szYD_STR_LOC;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			return;
            }
            
            if("".equals(szYD_STK_LYR_NO)){
    			szMsg = "파라미터 Check중 YD_STK_LYR_NO(야드적치단번호) Error : 빈 값 " + szYD_STK_LYR_NO;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			return;
            }
            

            String sYD_STR_LOC = szYD_STR_LOC.substring(0,6);
            
			/*--------------------------------------------------------
			 - YDY2L009 는 L2가 관리하는 갠트리 크레인 영역만 요구 할 수 있다.
			 - L3가 관리하는 영역을 요구하면 Error 처리 해야 한다.
			 - 갠트리 크레인 영역
			 - 50호기 PECB01~PECB04,PE0111
			 - 45호기 PD0162,PE0151,PE0154~PE0156
			 - 21호기 PB0361~PB0366
			 ---------------------------------------------------------*/
			boolean bFlag = false;
			
			if("PECB01".equals(sYD_STR_LOC) || "PECB02".equals(sYD_STR_LOC) || "PECB03".equals(sYD_STR_LOC) || "PECB04".equals(sYD_STR_LOC) || "PE0111".equals(sYD_STR_LOC)) {
				//50호기
				bFlag = true;
			} else if("PD0162".equals(sYD_STR_LOC) || "PE0151".equals(sYD_STR_LOC) || "PE0154".equals(sYD_STR_LOC) || "PE0155".equals(sYD_STR_LOC) || "PE0156".equals(sYD_STR_LOC)) {
				//45호기
				bFlag = true;
			} else if("PB0361".equals(sYD_STR_LOC) || "PB0362".equals(sYD_STR_LOC) || "PB0363".equals(sYD_STR_LOC) || "PB0364".equals(sYD_STR_LOC) || "PB0365".equals(sYD_STR_LOC) || "PB0366".equals(sYD_STR_LOC) ) {
				//21호기
				bFlag = true;
			}            
            
			if(!bFlag) {
				szMsg = "Y2(1후판정정야드L2) 후판L2 저장위치제원정보  데이터 수신 중 예외발생! 예외메세지: " + sYD_STR_LOC + " 위치는 L2 영역이 아닙니다!";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return;
			}            
            
            //1) PL_MTL_NO 가 없으면  PL_L2_TRK_NO 로 PL_MTL_NO 를 구한다. (구 Y9YDL002 참고) 
            //2) PL_MTL_NO로 SHRSTOCK에 존재하는지 체크하여 없으면 SHRSTOCK를 생성한다.
            //3) PL_MTL_NO가 존재하는 위치를 Clear 한다.
            //4) YD_STR_LOC와 YD_STK_LYR_NO 로 신규 위치에 적치한다.
            //5) 조업으로 위치정보를 전송한다.
            //6) 이력 정보 생성
            
            //----------------------------------------------------------------------------
            //1) PL_MTL_NO 가 없으면  PL_L2_TRK_NO 로 PL_MTL_NO 를 구한다. (구 Y9YDL002 참고)
			//   --> L2에 확인해 보니 PL_MTL_NO 값이 없는 경우는 없다고 함 - 2018.12.28 
            //if(!"".equals(szPL_L2_TRK_NO)&&"".equals(szPL_MTL_NO)) {
            //	
            //	jrParam.setField("STL_NO"	, szPL_L2_TRK_NO);
            //	
            //	rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getL2No", logId, szMethodName, "L3번호 -> L2번호 Convert");
            //	
            //	if(rsResult.size() > 0) {
            //		szPL_MTL_NO = rsResult.getRecord(0).getFieldString("STL_NO");
            //	}
            //}
            
            if("".equals(szPL_MTL_NO)){
    			szMsg = "파라미터 Check중 PL_MTL_NO(후판재료번호) Error : 빈 값 " + szPL_MTL_NO;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			return;
            }
            
            //----------------------------------------------------------------------------
            //2) PL_MTL_NO로 SHRSTOCK에 존재하는지 체크하여 없으면 SHRSTOCK를 생성한다.
			// 재료 정보 존재여부 체크
        	jrParam.setField("STL_NO"	, szPL_MTL_NO);
        	jrParam.setField("YD_GP"	, JPlateYdConst.YD_GP_P_PLATE_YARD);
        	
        	jrParam.setField("REGISTER"	, szRcvTcCode);
        	jrParam.setField("MODIFIER"	, szRcvTcCode);
        			
            rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdStockWithLoc2", logId, szMethodName, "1후판정정야드재료 정보 조회");
			
			// 1후판 정정야드 재료정보 생성모듈 호출
            if(rsResult.size() == 0) {
				JPlateYdDao.insYdStockBookOutYdP(jrParam);
			}					
            
            //----------------------------------------------------------------------------
            //3) PL_MTL_NO가 존재하는 위치를 Clear 한다.

			/**********************************************************
			* 재료번호 기준으로 저장위치 검색   MAP 삭제
			**********************************************************/			

			recPara.setField("STL_NO" 				, szPL_MTL_NO);
			recPara.setField("YD_STK_LYR_MTL_STAT"	, "C");
			
            rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNO", logId, szMethodName, "재료번호 기준으로 저장위치 검색");
			
			//기존 야드 정보 삭제
			if(rsResult.size() > 0) {
				
				szCURR_YD_STR_LOC    = StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"),"");
				szCURR_YD_STK_BED_NO = StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_STK_BED_NO"),"");
				szCURR_YD_STK_LYR_NO = StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_STK_LYR_NO"),""); 
				
				recPara.setField("STL_NO" 				, "");
				recPara.setField("YD_STK_COL_GP" 		, szCURR_YD_STR_LOC);
				recPara.setField("YD_STK_BED_NO" 		, szCURR_YD_STK_BED_NO);
				recPara.setField("YD_STK_LYR_NO" 		, szCURR_YD_STK_LYR_NO);
				recPara.setField("YD_STK_LYR_MTL_STAT"	, "E");
				recPara.setField("MODIFIER"				, szRcvTcCode);
				
				if(szCURR_YD_STR_LOC.startsWith("P")){// 1후판 정정야드에 있는 경우에만 삭제
					//적치단 정보 UPDATE
					intRtnVal = JPlateYdStkLyrDao.updYdStklyrStat(recPara);
				}
			}			
            
			
            //----------------------------------------------------------------------------
            //4) YD_STR_LOC와 YD_STK_LYR_NO 로 신규 위치에 적치한다.
			recPara.setField("STL_NO" 				, szPL_MTL_NO);
			recPara.setField("YD_GP"  				, JPlateYdConst.YD_GP_P_PLATE_YARD);
			recPara.setField("YD_STK_COL_GP" 		, szYD_STR_LOC.substring(0,6));
			recPara.setField("YD_STK_BED_NO" 		, szYD_STR_LOC.substring(6,8));
			recPara.setField("YD_STK_LYR_NO" 		, szYD_STK_LYR_NO);
			recPara.setField("YD_STK_LYR_MTL_STAT"	, "C");
			recPara.setField("MODIFIER"				, szRcvTcCode);
			
			intRtnVal = JPlateYdStkLyrDao.updYdStklyrStat(recPara);
			
            //----------------------------------------------------------------------------
            //5) 조업으로 위치정보를 전송한다.
			
	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 1후판조업 저장위치변경정보 전송  - YDPRJ011
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            szMsg    = "["+szMethodName+"] 1후판조업 저장위치변경정보 전송 ---- START";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

            JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("MSG_ID", 			"YDPRJ011");
	        recInTemp.setField("YD_STK_COL_FR", 	"");		// From적치열
	        recInTemp.setField("YD_STK_BED_FR", 	"");		// From적치BED
	        recInTemp.setField("YD_STK_COL_TO", 	ydUtils.substr(szYD_STR_LOC,0,6));		// TO적치열
	        recInTemp.setField("YD_STK_BED_TO", 	ydUtils.substr(szYD_STR_LOC,6,2));		// TO적치BED
	        recInTemp.setField("YD_EQP_WRK_SH", 	"");									// 야드설비작업매수
	        recInTemp.setField("ARR_STL_NO", 		szPL_MTL_NO + ";");

	        String szSendMsg = JPlateYdCommonUtils.sendL3YDPRJ011(recInTemp);

			szMsg = "["+szMethodName+"] 후판조업 저장위치변경정보 전송 완료>>>>" + szSendMsg;
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

            //----------------------------------------------------------------------------
            //6) 이력 정보 생성

			JDTORecord recStockInfo 	= JDTORecordFactory.getInstance().create();
			JPlateYdWrkHistDAO 	ydWrkHistDao	= new JPlateYdWrkHistDAO();

			String szYdSchCd;
			// FXYD01MM : 저장위치 수정 , FXYD02MM : 저장위치 삭제 , FXCN02MM : 스크랩처리
			szYdSchCd = "PXYD01MM";
				
			recStockInfo.setField("YD_SCH_CD", 		szYdSchCd);

			//PL_L2_TRK_NO 항목을 L3에서 사용하지 않음으로 PL_L2_TRK_NO에 겐트리 크래인 번호 6자리를 넣기로 함 - 2018.12.28
			String szYD_EQP_ID = "";
			if(!"".equals(szPL_L2_TRK_NO)) { 
				if(szPL_L2_TRK_NO.length()>=6) {
					if("CR".equals(szPL_L2_TRK_NO.substring(2,4))) {
						szYD_EQP_ID = szPL_L2_TRK_NO.substring(0,6);
					} 
				}
			}
			recStockInfo.setField("YD_EQP_ID", 		szYD_EQP_ID);
			recStockInfo.setField("YD_GNT_GP", 		JPlateYdConst.YD_GNT_GP_MVSTK);         // M
			recStockInfo.setField("YD_SCH_ST_GP", 	"B");									// 야드스케줄 기동 구분 B:BACKUP
	        recStockInfo.setField("YD_WRK_HDS_DD",  JPlateYdUtils.getDefaultHdsDate());		// 계상일자
	        recStockInfo.setField("YD_WRK_DUTY",	JPlateYdUtils.getDefaultDuty());		// 작업근

			// 야드보조작업여부 - YD_AID_WRK_YN
			recStockInfo.setField("YD_AID_WRK_YN" , "N");
			recStockInfo.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);
			recStockInfo.setField("STL_NO", 		szPL_MTL_NO);
	        
			recStockInfo.setField("REGISTER", 		szRcvTcCode);
			recStockInfo.setField("MODIFIER", 		szRcvTcCode);
			recStockInfo.setField("DEL_YN", 		"N");
			
			// 수정 작업이력 - 권상정보 관련 입력
			recStockInfo.setField("YD_UP_WR_LOC", 	szCURR_YD_STR_LOC + szCURR_YD_STK_BED_NO);
			recStockInfo.setField("YD_UP_WR_LAYER", szCURR_YD_STK_LYR_NO);
			recStockInfo.setField("YD_UP_CMPL_DT",	JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));
			
			// 수정 작업이력 - 권하정보 입력
			recStockInfo.setField("YD_DN_WR_LOC",   szYD_STR_LOC);
			recStockInfo.setField("YD_DN_WR_LAYER", szYD_STK_LYR_NO);
			recStockInfo.setField("YD_DN_CMPL_DT", 	JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));
				

			// 이력정보 남기기
			intRtnVal = ydWrkHistDao.insYdWrkHist(recStockInfo);			
            
            
			
		}catch(DAOException e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        } 
	} // end of procY2YDL016	
	
	/**
	 * 오퍼레이션명 :  1후판정정야드  Book-In/Book-Out 실적 (Y2YDL014) - 신규
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procY2BookInOutRslt2(JDTORecord msgRecord)throws DAOException  {
		
		String szMsg		  	= "";
		String szMethodName	  	= "procY2BookInOutRslt2";
		/*
		OPERATION_TYPE	OPERATION_TYPE	CHAR	1	Y	1:Book In, 2:Book Out		
		PL_L2_TRK_NO	후판L2제품번호		CHAR	16				
		PL_MTL_NO		후판재료번호		CHAR	10				
		YD_STR_LOC		야드저장위치		CHAR	8	Y			
		YD_STK_LYR_NO	야드적치단번호		CHAR	3	Y			
		*/
		
        szMsg    = "["+szMethodName+"] 1후판정정야드  Book-In/Book-Out 실적 ---- START";
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		
		int intRtnVal 			= 0;		
		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet outRecSet2 = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
		JDTORecord recTmp 		= JDTORecordFactory.getInstance().create();
		String 	szRcvTcCode    	= ydUtils.getTcCode(msgRecord);
		
		YdStkLyrDao  ydStkLyrDao  = new YdStkLyrDao();
		JPlateYdStockDAO JPlateYdDao 	= new JPlateYdStockDAO();
		JPlateYdStkLyrDAO   JPlateYdStkLyrDao		= new JPlateYdStkLyrDAO();
		
		String 	szModifier		= "";
		try{
			
			String szMSG_GP			= ydDaoUtils.paraRecChkNull(msgRecord,"MSG_GP"); 
			String szOPERATION_TYPE	= ydDaoUtils.paraRecChkNull(msgRecord,"OPERATION_TYPE");
			String szPL_L2_TRK_NO 	= ydDaoUtils.paraRecChkNull(msgRecord,"PL_L2_TRK_NO");
			String szSTL_NO			= ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO");	
			String szYD_STR_LOC		= ydDaoUtils.paraRecChkNull(msgRecord,"YD_STR_LOC");
			String szYD_STK_LYR_NO	= ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_LYR_NO");
			String szCURR_YD_STR_LOC    = "";
			String szCURR_YD_STK_BED_NO = "";
			String szCURR_YD_STK_LYR_NO = "";
			
			if ("".equals(szModifier)) {
	        	szModifier = szRcvTcCode;
	        }

            // 파라미터 Check
            if("".equals(szYD_STR_LOC)){
    			szMsg = "파라미터 Check중 YD_STR_LOC(야드저장위치) Error : 빈 값 " + szYD_STR_LOC;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			return;
            }

            //if(szYD_STR_LOC.length() < 8){
    		//	szMsg = "파라미터 Check중 YD_STR_LOC(야드저장위치) Error : 길이가 8이 아닙니다. " + szYD_STR_LOC;
    		//	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    		//	return;
            //}
            
            if(!"P".equals(szYD_STR_LOC.substring(0,1))){
    			szMsg = "파라미터 Check중 YD_STR_LOC(야드저장위치) Error : 야드구분이 P가 아닙니다. " + szYD_STR_LOC;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			return;
            }
            
            //if("".equals(szYD_STK_LYR_NO)){
    		//	szMsg = "파라미터 Check중 YD_STK_LYR_NO(야드적치단번호) Error : 빈 값 " + szYD_STK_LYR_NO;
    		//	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    		//	return;
            //}
			/*
            String sYD_STR_LOC = szYD_STR_LOC.substring(0,6);
            
			--------------------------------------------------------
			 - YDY2L009 는 L2가 관리하는 갠트리 크레인 영역만 요구 할 수 있다.
			 - L3가 관리하는 영역을 요구하면 Error 처리 해야 한다.
			 - 갠트리 크레인 영역
			 - 50호기 PECB01~PECB04,PE0111
			 - 45호기 PD0162,PE0151,PE0154~PE0156
			 - 21호기 PB0361~PB0366
			 ---------------------------------------------------------
			boolean bFlag = false;
			
			if("PECB01".equals(sYD_STR_LOC) || "PECB02".equals(sYD_STR_LOC) || "PECB03".equals(sYD_STR_LOC) || "PECB04".equals(sYD_STR_LOC) || "PE0111".equals(sYD_STR_LOC)) {
				//50호기
				bFlag = true;
			} else if("PD0162".equals(sYD_STR_LOC) || "PE0151".equals(sYD_STR_LOC) || "PE0154".equals(sYD_STR_LOC) || "PE0155".equals(sYD_STR_LOC) || "PE0156".equals(sYD_STR_LOC)) {
				//45호기
				bFlag = true;
			} else if("PB0361".equals(sYD_STR_LOC) || "PB0362".equals(sYD_STR_LOC) || "PB0363".equals(sYD_STR_LOC) || "PB0364".equals(sYD_STR_LOC) || "PB0365".equals(sYD_STR_LOC) || "PB0366".equals(sYD_STR_LOC) ) {
				//21호기
				bFlag = true;
			}            
            
			if(!bFlag) {
				szMsg = "Y2(1후판정정야드L2) 후판L2 저장위치제원정보  데이터 수신 중 예외발생! 예외메세지: " + sYD_STR_LOC + " 위치는 L2 영역이 아닙니다!";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return;
			}           			
            */
			/**********************************************************
			* 재료번호 기준으로 저장위치 검색   MAP 삭제
			**********************************************************/			

			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO" 				, szSTL_NO);
			recPara.setField("YD_STK_LYR_MTL_STAT"	, "C");
			recPara.setField("REGISTER"				, szModifier);
			recPara.setField("MODIFIER"				, szModifier);
			
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 3);
			
			//기존 야드 정보 삭제
			if(intRtnVal >= 1) {
				outRecSet.absolute(1);
				recTmp = outRecSet.getRecord();
				
				szCURR_YD_STR_LOC = ydDaoUtils.paraRecChkNull(recTmp, "YD_STK_COL_GP");
				szCURR_YD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recTmp, "YD_STK_BED_NO");
				szCURR_YD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recTmp, "YD_STK_LYR_NO");
				
				recPara.setField("STL_NO" 				, "");
				recPara.setField("YD_STK_COL_GP" 		, szCURR_YD_STR_LOC);
				recPara.setField("YD_STK_BED_NO" 		, ydDaoUtils.paraRecChkNull(recTmp, "YD_STK_BED_NO"));
				recPara.setField("YD_STK_LYR_NO" 		, ydDaoUtils.paraRecChkNull(recTmp, "YD_STK_LYR_NO"));
				recPara.setField("YD_STK_LYR_MTL_STAT"	, "E");
				recPara.setField("MODIFIER"				, szModifier);
				
				if(szCURR_YD_STR_LOC.startsWith("P")){// 1후판 정정야드에 있는 경우에만 삭제
					//적치단 정보 UPDATE
					intRtnVal = JPlateYdStkLyrDao.updYdStklyrStat(recPara);
				}
			}			
			
			if(!"D".equals(szMSG_GP)) { //삭제가 아닌 경우만 if문 실행
				
				if("2".equals(szOPERATION_TYPE)){ //BOOK OUT
	
					/**********************************************************
					*  북아웃시 수신받는 단정보를 활용하지 않음. 자체적으로 최상단 검색
					**********************************************************/			
					/* 2019.01.30 : L2에서 올라온 단 정보를 그대로 사용하기로 함
					recPara.setField("YD_STK_COL_GP", szYD_STR_LOC.substring(0,6));
					recPara.setField("YD_STK_BED_NO", szYD_STR_LOC.substring(6,8));
			        
			        intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet2, 98);
			        
			        if(intRtnVal > 0) {
			        	//outRecSet.first();
			        	outRecSet2.absolute(1);
				        recTmp = outRecSet2.getRecord();
				        
			        	szYD_STK_LYR_NO	= ydDaoUtils.paraRecChkNull(recTmp,"REAL_TOP_LYR");
			        	
						szMsg = ">>>>>>> REAL_TOP_LYR : " + szYD_STK_LYR_NO;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, YdConstant.ERROR);
			        	
			        } */
				
					recPara.setField("STL_NO" 				, szSTL_NO);
					recPara.setField("YD_GP"  				, JPlateYdConst.YD_GP_P_PLATE_YARD);
					recPara.setField("YD_STK_COL_GP" 		, szYD_STR_LOC.substring(0,6));
					recPara.setField("YD_STK_BED_NO" 		, szYD_STR_LOC.substring(6,8));
					recPara.setField("YD_STK_LYR_NO" 		, szYD_STK_LYR_NO);
					recPara.setField("YD_STK_LYR_MTL_STAT"	, "C");
					recPara.setField("MODIFIER"				, szModifier);
					
					intRtnVal = JPlateYdStkLyrDao.updYdStklyrStat(recPara);
					if (intRtnVal != 1) {
						szMsg = "REPAIR BED 적치단 등록 오류 .. 재료번호 : " + szSTL_NO;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, YdConstant.ERROR);
					    //skip
					} else {
						
						// 재료 정보 존재여부 체크
						intRtnVal = JPlateYdDao.getYdStockWithLoc(recPara, outRecSet);
						
						// 1후판 정정야드 재료정보 생성모듈 호출
						if (intRtnVal < 1) {
							intRtnVal = JPlateYdDao.insYdStockBookOutYdP(recPara);
						}					
					}	
				}else if("1".equals(szOPERATION_TYPE)){ //BOOK IN
					/**********************************************************
					*  북인시 RT 등록
					**********************************************************/			
					
					//RT 등록
					recPara.setField("STL_NO" 				, szSTL_NO);
					recPara.setField("YD_GP"  				, JPlateYdConst.YD_GP_P_PLATE_YARD);
					recPara.setField("YD_STK_COL_GP" 		, szYD_STR_LOC.substring(0,6));
					recPara.setField("YD_STK_BED_NO" 		, szYD_STR_LOC.substring(6,8));
					recPara.setField("YD_STK_LYR_NO" 		, szYD_STK_LYR_NO);
					recPara.setField("YD_STK_LYR_MTL_STAT"	, "C");
					recPara.setField("MODIFIER"				, szModifier);
					
					intRtnVal = JPlateYdStkLyrDao.updYdStklyrStat(recPara);
					if (intRtnVal != 1) {
						szMsg = "RT BED 적치단 등록 오류 .. 재료번호 : " + szSTL_NO;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, YdConstant.ERROR);
					}				
				}else if("3".equals(szOPERATION_TYPE)){ //이적  REQ202306468906
					/**********************************************************
					*  북인시 RT 등록
					**********************************************************/			
					
					//RT 등록
					recPara.setField("STL_NO" 				, szSTL_NO);
					recPara.setField("YD_GP"  				, JPlateYdConst.YD_GP_P_PLATE_YARD);
					recPara.setField("YD_STK_COL_GP" 		, szYD_STR_LOC.substring(0,6));
					recPara.setField("YD_STK_BED_NO" 		, szYD_STR_LOC.substring(6,8));
					recPara.setField("YD_STK_LYR_NO" 		, szYD_STK_LYR_NO);
					recPara.setField("YD_STK_LYR_MTL_STAT"	, "C");
					recPara.setField("MODIFIER"				, szModifier);
					
					intRtnVal = JPlateYdStkLyrDao.updYdStklyrStat(recPara);
					if (intRtnVal != 1) {
						szMsg = "이적 BED 적치단 등록 오류 .. 재료번호 : " + szSTL_NO;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, YdConstant.ERROR);
					}				
				}
				
		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 1후판조업 저장위치변경정보 전송  - YDPRJ011
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	            szMsg    = "["+szMethodName+"] 1후판조업 저장위치변경정보 전송 ---- START";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	            JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
		        recInTemp.setField("MSG_ID", 			"YDPRJ011");
		        recInTemp.setField("YD_STK_COL_FR", 	"");		// From적치열
		        recInTemp.setField("YD_STK_BED_FR", 	"");		// From적치BED
		        recInTemp.setField("YD_STK_COL_TO", 	ydUtils.substr(szYD_STR_LOC,0,6));		// TO적치열
		        recInTemp.setField("YD_STK_BED_TO", 	ydUtils.substr(szYD_STR_LOC,6,2));		// TO적치BED
		        recInTemp.setField("YD_EQP_WRK_SH", 	"");									// 야드설비작업매수
		        recInTemp.setField("ARR_STL_NO", 		szSTL_NO + ";");
		        
		        if("3".equals(szOPERATION_TYPE)){  //이적실적일 경우, 이전위치값 재셋팅
		        	recInTemp.setField("YD_STK_COL_FR", 	szCURR_YD_STR_LOC);		// From적치열
			        recInTemp.setField("YD_STK_BED_FR", 	szCURR_YD_STK_BED_NO);	// From적치BED
			        
			        szMsg    = "["+szMethodName+"] 이적실적발생. 이전위치값 셋팅. YD_STK_COL_FR:"+szCURR_YD_STR_LOC+" YD_STK_BED_FR:"+szCURR_YD_STK_BED_NO;
			        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		        }
		        
		        
		        if("2".equals(szOPERATION_TYPE)){  //BOOK-OUT일때만
		        	String szTmp_STK_COL_FR="";
		        	String szTmp_STK_BED_FR="";
		        	szMsg    = "["+szMethodName+"] 1후판조업 신규추가기능(원인코드,크레인등) 저장 ---- START";
		        	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		        	String szYD_TRK_ZONE_NO = "";
		        	String szYD_BOOKOUT_RSN="";
		        	String szYD_EQP_ID = "";
					if(!"".equals(szPL_L2_TRK_NO)) { 
						if(szPL_L2_TRK_NO.length()>=6) {
							if("CR".equals(szPL_L2_TRK_NO.substring(2,4))) {
								szYD_EQP_ID = szPL_L2_TRK_NO.substring(0,6);
								if(szPL_L2_TRK_NO.length()>=13){
									szYD_BOOKOUT_RSN=szPL_L2_TRK_NO.substring(10,13);//북아웃원인코드를 설비입력자리 뒤에 사용 14~16자리
								}
							} 
						}
					}
					
					//TRK_ZONE_NO 셋팅
					if(szYD_EQP_ID.equals("PECRE1")){  //서냉피트 크레인
						szYD_TRK_ZONE_NO="38030";
						szTmp_STK_COL_FR="PERT25";
						szTmp_STK_BED_FR="01";
						if(szYD_BOOKOUT_RSN.trim().equals("")||szYD_BOOKOUT_RSN.trim().equals("000")){  //북아웃 원인코드 빈값으로 오면
							szYD_BOOKOUT_RSN="772";
						}
					}
					if(szYD_EQP_ID.equals("PECRE4")){  //전단라인 크레인
						szYD_TRK_ZONE_NO="48000";
						szTmp_STK_COL_FR="PDRT50";
						szTmp_STK_BED_FR="01";
						if(szYD_BOOKOUT_RSN.trim().equals("")||szYD_BOOKOUT_RSN.trim().equals("000")){  //북아웃 원인코드 빈값으로 오면
						szYD_BOOKOUT_RSN="773"; //북아웃 원인 stock update
						}
					}
					
					recInTemp.setField("REGISTER", szModifier );  //등록자
					recInTemp.setField("STL_NO", szSTL_NO ); //재료번호
					recInTemp.setField("BOOK_OUT_RESN", szYD_BOOKOUT_RSN ); //Book-Out원인
					recInTemp.setField("BOOK_OUT_DATE", JPlateYdUtils.getCurDate("yyyyMMdd") ); //Book-Out 일자
					recInTemp.setField("BOOK_OUT_PROG", "" ); //Book-Out공정
					recInTemp.setField("FRTOMOVE_PLANT_GP", "" ); //BOOK-IN 대상재 구분(Y2YDL014로는 수신받는 내용 없음. 공란으로 두자)
					recInTemp.setField("ARR_WLOC_CD", szYD_TRK_ZONE_NO ); //후판트래킹존
					recInTemp.setField("YD_FRTOMOVE_YD_GP", szOPERATION_TYPE ); //북아웃모드
					recInTemp.setField("YD_STK_COL_FR", 	szTmp_STK_COL_FR);		// From적치열
					recInTemp.setField("YD_STK_BED_FR", 	szTmp_STK_BED_FR);		// From적치베드
					
					intRtnVal=JPlateYdDao.insYdStockBookOut(recInTemp);  //SHRSTOCK에 UPDATE함.이후 MAKETC(YDPRJ011)에서 SHRSTOCK읽어와서 전문 전송)
					
					//BOOKIN/OUT MOD 셋팅(1:BOOK-IN, 2:BOOK-OUT)
					recInTemp.setField("PL_BOOK_OUT_MOD", 		szOPERATION_TYPE); //bookout_mod stock update
						
					recInTemp.setField("PL_BOOK_OUT_CRANE", 		szYD_EQP_ID); //대상설비 담아서 전송.
					
					//intRtnVal =JPlateYdDao.updYdStockTX(recInTemp);//<-여기서 우선 SHRSTOCK에 UPDATE함. 이후 MAKETC(YDPRJ011)에서 SHRSTOCK읽어와서 전문 전송)
		        }

		        String szSendMsg = JPlateYdCommonUtils.sendL3YDPRJ011(recInTemp);

				szMsg = "["+szMethodName+"] 후판조업 저장위치변경정보 전송 완료>>>>" + szSendMsg;
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				
			}
			
			//-------------------------------------------------------------------------------------
			//이력 정보 생성

			JDTORecord recStockInfo 	= JDTORecordFactory.getInstance().create();
			JPlateYdWrkHistDAO 	ydWrkHistDao	= new JPlateYdWrkHistDAO();

			String szYdSchCd;
			// FXYD01MM : 저장위치 수정 , FXYD02MM : 저장위치 삭제 , FXCN02MM : 스크랩처리
			if ("D".equals(szMSG_GP)) {
				szYdSchCd = "PXYD02MM";
			} else {
				szYdSchCd = "PXYD01MM";
			}
			recStockInfo.setField("YD_SCH_CD", 		szYdSchCd);

			//PL_L2_TRK_NO 항목을 L3에서 사용하지 않음으로 PL_L2_TRK_NO에 겐트리 크래인 번호 6자리를 넣기로 함 - 2018.12.28
			String szYD_EQP_ID = "";
			if(!"".equals(szPL_L2_TRK_NO)) { 
				if(szPL_L2_TRK_NO.length()>=6) {
					if("CR".equals(szPL_L2_TRK_NO.substring(2,4))) {
						szYD_EQP_ID = szPL_L2_TRK_NO.substring(0,6);
					} 
				}
			}
			recStockInfo.setField("YD_EQP_ID", 		szYD_EQP_ID);
			recStockInfo.setField("YD_GNT_GP", 		JPlateYdConst.YD_GNT_GP_MVSTK);         // M
			recStockInfo.setField("YD_SCH_ST_GP", 	"B");									// 야드스케줄 기동 구분 B:BACKUP
	        recStockInfo.setField("YD_WRK_HDS_DD",  JPlateYdUtils.getDefaultHdsDate());		// 계상일자
	        recStockInfo.setField("YD_WRK_DUTY",	JPlateYdUtils.getDefaultDuty());		// 작업근

			// 야드보조작업여부 - YD_AID_WRK_YN
			recStockInfo.setField("YD_AID_WRK_YN" , "N");
			recStockInfo.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);
			recStockInfo.setField("STL_NO", 		szSTL_NO);
	        
			recStockInfo.setField("REGISTER", 		szModifier);
			recStockInfo.setField("MODIFIER", 		szModifier);
			recStockInfo.setField("DEL_YN", 		"N");
			
			if ("D".equals(szMSG_GP)) {
				
				// 삭제 작업이력 - 권상정보 관련 입력
				recStockInfo.setField("YD_UP_WR_LOC", 	szYD_STR_LOC);
				recStockInfo.setField("YD_UP_WR_LAYER", szYD_STK_LYR_NO);
				recStockInfo.setField("YD_UP_CMPL_DT",	JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));
				
				// 삭제 작업이력 - 권하정보 입력
				recStockInfo.setField("YD_DN_WR_LOC",   "");
				recStockInfo.setField("YD_DN_WR_LAYER", "");
				recStockInfo.setField("YD_DN_CMPL_DT", 	JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));
				
			} else {
				
				// 수정 작업이력 - 권상정보 관련 입력
				recStockInfo.setField("YD_UP_WR_LOC", 	szCURR_YD_STR_LOC + szCURR_YD_STK_BED_NO);
				recStockInfo.setField("YD_UP_WR_LAYER", szCURR_YD_STK_LYR_NO);
				recStockInfo.setField("YD_UP_CMPL_DT",	JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));
				
				// 수정 작업이력 - 권하정보 입력
				recStockInfo.setField("YD_DN_WR_LOC",   szYD_STR_LOC);
				recStockInfo.setField("YD_DN_WR_LAYER", szYD_STK_LYR_NO);
				recStockInfo.setField("YD_DN_CMPL_DT", 	JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));
				
			}

			// 이력정보 남기기
			intRtnVal = ydWrkHistDao.insYdWrkHist(recStockInfo);			
			
		}catch(Exception e){
			
			szMsg = "[1후판정정  Book-In/Book-Out 실적 (Y2YDL014) - 신규] Exception Error:" +e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, YdConstant.ERROR);
		}

		szMsg = "1후판정정  Book-In/Book-Out 실적 (Y2YDL014) - 신규] ("+szMethodName+") 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // procY2BookInOutRslt2
	
    /**
     * 오퍼레이션명 : 1후판전단/열처리 L2시스템으로부터 Book-In/Book-Out요구 수신 [P2YDL501/P3YDL501] 신규모듈
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● Stirng pYdCrnSchId, String pYdWrkProgStat
     * @return ● nRtnVal
     * @throws ● DAOException
     */
	public String  procP2P3BookInOutReq2(JDTORecord msgRecord) throws DAOException {
		// 레코드선언
		JDTORecordSet 	rsResult  	= null;
		
		JDTORecord 		inRec 		= null;
		JDTORecord 		tempRec 	= null;
		JDTORecord 		recPara   	= JDTORecordFactory.getInstance().create();
		JDTORecord 		recSchPara  = null;
		JDTORecord 		recL2Para   = null;

		// 변수 선언
        String 	szMethodName  		= "procP2P3BookInOutReq2";
        String	szRtnMsg			= JPlateYdConst.RETN_CD_SUCCESS;
    	String 	szMsg         		= "";
    	String 	szOperationName 	= "1후판 전단/열처리 Book-In/Book-Out요구 수신 - 신규";

		String 	szYdGp          	= ""; 			// 야드구분
		String 	szYdBayGp       	= ""; 			// 야드동구분

		String 	szYdSchCd       	= ""; //스케줄 코드
		String 	szYdSchProhExn  	= "";  			// 야드스케쥴금지유무
		String 	szYdWrkCrnPrior 	= "1"; 			// 야드작업크레인우선순위
		String 	szYdStkLyrNo[]	 	= {"001","002","003","004","005","006","007","008","009"};		// 야드적치단 , RT는 무조건 1단
		String 	szYdWrkCrn      	= "";			// 야드설비ID (NOT NULL이기 때문에 RULE에 등록된 설비ID SET)
		String	szYdWbookId 		= "";			// 작업예약ID 생성
		String	szYdStkColGp		= "";
		String	szYdToLocGuide		= "";
        String 	szRegister			= "";			// 등록자, MSG_ID
        String szPillingDelStlNo[] = null  ;	 //파일링 삭제 재료 보관용
        String szPillingDelStlNoStkLyrNo[] = null  ;	 //파일링 삭제 재료의 적치단 순서 보관용

        String	szFromLoc			= "";
        String  szFromBed           = "01";
        String	szToLoc				= "";
        
        String  szSPARE				= "";
        
        int iDelStlNo=0; //파일링 시, 삭제한 재료 건수
    	int 	intRtnVal 			= 0;
		int		iWBookInsCnt		= 0;			// 작업예약등록 건수
		int		iCrnSchCnt			= 0;			// 크레인스케쥴 호출 건수
		int		iSchOkCnt			= 0;			// 스케줄호출 OK 건수
		
		boolean isAsRollMtl			= false;		// AS Roll 재 구분

		StringBuffer sbARR_WBOOK_ID = new StringBuffer();
		
    	//DAO
    	JPlateYdStockDAO    ydStockDao   	= new JPlateYdStockDAO();
    	JPlateYdWrkbookDAO	ydWrkbookDao 	= new JPlateYdWrkbookDAO();
    	JPlateYdCrnSchDAO 	ydCrnSchDao		= new JPlateYdCrnSchDAO();
    	JPlateYdStkLyrDAO   ydStkLyrDao		= new JPlateYdStkLyrDAO();
    	JPlateYdSchRuleDAO  ydSchRuleDao	= new JPlateYdSchRuleDAO();

    	JPlateYdCommDAO 	commDao 		= new JPlateYdCommDAO();
    	JPlateYdEqpDAO ydEqpDao = new JPlateYdEqpDAO();
    	

    	String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
    	
        try {
        	szMsg = "["+szOperationName+"] ---- 메소드 시작  >>>> " + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			/*
			7	OPERATION_TYPE			OPERATION_TYPE		CHAR	1
			8	PL_L2_TRK_NO			후판L2제품번호			CHAR	16
			9	PL_MTL_NO				후판재료번호			CHAR	32
			10	PL_MEA_GDS_L			후판제촌제품길이		CHAR	5
			11	PL_MEA_GDS_W			후판제촌제품폭			CHAR	6
			12	PL_MEA_GDS_T			후판제촌제품두께		CHAR	7
			13	PL_TRCK_ZONE_ASGN		후판트래킹존지정		CHAR	5
			14	PL_BOOK_OUT_MOD			후판북아웃모드			CHAR	1
			15	CRANE_NO				Crane_No			CHAR	2
			16	YARD_NO					Yard_No				CHAR	6
			17	BED_NO					BED_NO				CHAR	2
			18	REASON_CODE				REASON_CODE			CHAR	3
			19	NEXT_PROCESS			NEXT_PROCESS		CHAR	1
			20  PILNG_WRK_GP			파일링작업구분			CHAR	1
			21	PL_MTL_NO2				2단재료번호			CHAR	10
			22	PL_MTL_NO3				3단재료번호			CHAR	10
		*/
			String szRcvTcCode    	= ydUtils.getTcCode(msgRecord);
			String szPlateId[]		= {"","","","","","","","",""};
			int iPlateCnt 			= 0;
			szPlateId[0]		= ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO");				// PLATE_NO 1단재료번호
			String szOperationType 	= ydDaoUtils.paraRecChkNull(msgRecord, "OPERATION_TYPE");			// 1:Book In, 2:Book Out
			String szTrkZoneNo		= ydDaoUtils.paraRecChkNull(msgRecord, "PL_TRCK_ZONE_ASG");		    // PL_TRCK_ZONE_ASGN

			if(szTrkZoneNo.equals("")|| szTrkZoneNo == null) {
				szTrkZoneNo		= ydDaoUtils.paraRecChkNull(msgRecord, "PL_TRCK_ZONE_ASGN");		// PL_TRCK_ZONE_ASGN
			}
			
			String szYdNo			= ydDaoUtils.paraRecChkNull(msgRecord, "YARD_NO");                  // YARD_NO
			String szReasonCode		= ydDaoUtils.paraRecChkNull(msgRecord, "REASON_CODE");				// Book-Out원인 (888:TEST , 999:북아웃취소)
			String szResonCode		= ydDaoUtils.paraRecChkNull(msgRecord, "RESON_CODE");				// REASON_CODE가 아닌, REASON_CODE로 올경우 대비
			if(szReasonCode.trim().equals("")){
				szReasonCode=szResonCode;
			}
			int test=10;
			String szCraneNo		= ydDaoUtils.paraRecChkNull(msgRecord, "CRANE_NO");					// 북인대상재 구분 'A1' : 북인대상재
			String szOperationMode	= ydDaoUtils.paraRecChkNull(msgRecord, "PL_BOOK_OUT_MOD");			// 1:one time 2:Start 3:End
			String szOperationBed	= ydDaoUtils.paraRecChkNull(msgRecord, "BED_NO");					// 파일링작업구붑
			
			String szPL_L2_TRK_NO=ydDaoUtils.paraRecChkNull(msgRecord, "PL_L2_TRK_NO");					// 후판L2제품번호
			
			String szPILNG_WRK_GP	= ydDaoUtils.paraRecChkNull(msgRecord, "PILNG_WRK_GP");				// 파일링작업구분 (Y:파일링작업,N:1매작업)
			szPlateId[1]		= ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO2");				// 2단재료번호
			szPlateId[2]		= ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO3");				// 3단재료번호
			
			
			szMsg="szOperationType:"+szOperationType+" szOperationMode:"+szOperationMode+" szReasonCode:"+szReasonCode;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			
  			boolean isPillingModBookOut=false;  //파일링 북아웃모드 여부 체크
			if("2".equals(szOperationType) &&"5".equals(szOperationMode) && "777".equals(szReasonCode)){
				isPillingModBookOut=true;
				
				szMsg="파일링 북아웃모드 설정";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}
			
			boolean isPillingModBookIn=false;   //파일링 북인모드 여부 체크, 1,5,재료번호
			//if("1".equals(szOperationType) &&"6".equals(szOperationMode))
			if("1".equals(szOperationType) &&"5".equals(szOperationMode) && !"".equals(szPlateId[0])){
				isPillingModBookIn=true;
				
				szMsg="파일링 북인모드 설정";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}
			
			String szOperationDate 	= JPlateYdUtils.getCurDate("yyyyMMdd");
			szRegister 		= ydDaoUtils.paraRecModifier(msgRecord);							// 등록자, 수정자
			if ("".equals(szRegister)) {
				szRegister = szRcvTcCode;        
			}
			szMsg    = "["+szOperationName+"] szRegister:" + szRegister;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			
			//조업L3로 내부 인터페이스 전달(극후물대 북아웃만 전송)
			if("33010".equals(ydDaoUtils.paraRecChkNull(msgRecord, "PL_TRCK_ZONE_ASG"))){
				JDTORecord recJmsPara = JDTORecordFactory.getInstance().create();
				recJmsPara.setField("JMS_TC_CD"         , 	"YDPRJ010");
				recJmsPara.setField("JMS_TC_CREATE_DDTT", 	YdUtils.getCurDate("yyyyMMddHHmmss"));			
				recJmsPara.setField("PL_MPL_NO", 			ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO"));			// 후판날판번호
				recJmsPara.setField("PL_L2_TRK_NO", 		ydDaoUtils.paraRecChkNull(msgRecord, "PL_L2_TRK_NO"));		// 후판L2제품번호
				recJmsPara.setField("PL_TRCK_ZONE_NO", 		ydDaoUtils.paraRecChkNull(msgRecord, "PL_TRCK_ZONE_ASG"));	// 후판트래킹존번호
				recJmsPara.setField("PL_BOOK_OUT_MOD", 		ydDaoUtils.paraRecChkNull(msgRecord, "PL_BOOK_OUT_MOD"));	// 후판북아웃모드
				recJmsPara.setField("PL_MEA_GDS_T", 		ydDaoUtils.paraRecChkNull(msgRecord, "PL_MEA_GDS_T"));		// 후판제촌제품두께
				recJmsPara.setField("PL_MEA_GDS_W", 		ydDaoUtils.paraRecChkNull(msgRecord, "PL_MEA_GDS_W")); 		// 후판제촌제품폭
				recJmsPara.setField("PL_MEA_GDS_L", 		ydDaoUtils.paraRecChkNull(msgRecord, "PL_MEA_GDS_L")); 		// 후판제촌제품길이

		        szRtnMsg = ydDelegate.sendMsgNoMakeTc(recJmsPara);

				szMsg = "["+szOperationName+"] 후판조업 극후물대 북아웃정보 전송 완료>>>>" + szRtnMsg;
			}
			
			if("P3YDL501".equals(szRcvTcCode)) {
				szSPARE = ydDaoUtils.paraRecChkNull(msgRecord, "SPARE");
				//열처리L2 00010 존 BOOK-OUT 시 SPARE 맨 앞자리를 작업지시 생성여부 FLAG로 사용
				//FLAG 가 'Y' 이면 B동 00010 존 BOOK-OUT 작업지시를 만들어라.
				//FLAG 가 'N' 이면 기존 처럼 C동 00012 존 BOOK-OUT 작업지시 만들어라
			}
			szMsg    = "["+szOperationName+"] SPARE:" + szSPARE;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			
			
			//if (!"1".equals(szOperationMode) && !"2".equals(szOperationMode) && !"3".equals(szOperationMode)) {
			//파일링 모드(5,6) 추가
			if (!"1".equals(szOperationMode) && !"2".equals(szOperationMode) && !"3".equals(szOperationMode)&& !"5".equals(szOperationMode)&& !"6".equals(szOperationMode)) {
				szRtnMsg = "수신전문중 OPERATION_MODE ERROR";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			// 북아웃 원인코드 체크 :: 888 - TEST , 999 - 취소처리
			if ("888".equals(szReasonCode)) {
				szRtnMsg = "북아웃 원인코드가 TEST재로 SKIP 처리함 :: " + szReasonCode;
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}
			// 북아웃원인 코드가 취소일때  (999)
			if ("999".equals(szReasonCode)) {

				szMsg    = "["+szOperationName+"] 북아웃 취소 처리 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				szRtnMsg = this.procP2P3BookInOutCancel2(msgRecord);

				szMsg    = "["+szOperationName+"] 북아웃 취소 처리 END >>>> 결과 :: " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return szRtnMsg;
			}
			
			//파일링 코드일 경우, 대상 크레인 체크처리
			if(isPillingModBookIn || isPillingModBookOut)
			{
				//1.대상 스케줄 코드, 지정 설비 존재 여부 확인.
				
				szYdSchProhExn  = "";  			// 야드스케쥴금지유무
				//szYdWrkCrnPrior = "1"; 			// 야드작업크레인우선순위
				szYdWrkCrn      = "";			// 야드설비ID (NOT NULL이기 때문에 RULE에 등록된 설비ID SET)

				recPara  = JDTORecordFactory.getInstance().create();
				rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
				
				if(isPillingModBookOut){
					recPara.setField("YD_SCH_CD", "PFRTAPLM");
				}
				else{
					recPara.setField("YD_SCH_CD", "PFRTAPUM");
				}

				//야드스케쥴금지유무, 대상 지정 설비 조회
				//ydSchRuleDao.getYdSchrule(recPara, rsResult);
				ydSchRuleDao.getYdSchruleWithEqp(recPara, rsResult);

				if (rsResult != null && rsResult.size() > 0) {
					//szYdSchProhExn  = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_SCH_PROH_EXN");
					//야드작업크레인우선순위 : 야드스케쥴코드에 해당하는 작업크레인의 우선순위이므로
					
					////SCHRULE이 아닌 추후 크레인 설비 지정 데이터에서 SELECT 변경필요
					szYdWrkCrn		= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_CRN");
					String szYdUseYn		= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_USE_YN");
					
					if("".equals(szYdWrkCrn) || "N".equals(szYdUseYn)){  //설비 미할당시 error return
						szRtnMsg = "입력받은 스케줄코드에 할당받은 설비 미존재 ";
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
					
					rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
					tempRec   = JDTORecordFactory.getInstance().create();
					tempRec.setField("YD_EQP_ID", szYdWrkCrn);
					
					intRtnVal = ydEqpDao.getYdEqp(tempRec, rsResult);

					if (intRtnVal <= 0) {
	    				szRtnMsg = "해당 설비 조회시 오류발생 .. 설비ID :: " + szYdWrkCrn;
	    				szMsg    = "["+szOperationName+"] " + szRtnMsg;
	    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	    				return szRtnMsg;
					}
					else{
						String szYD_EQP_STAT=ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_EQP_STAT");
						
						//설비 상태가 권상지시, 권하지시, 고장 상태일 경우 error return
						  
						if(JPlateYdConst.YD_EQP_STAT_BREAK.equals(szYD_EQP_STAT) ){
			    				szRtnMsg = "해당 설비 상태 조회시 사용 불가.. 설비ID :: " + szYdWrkCrn+ "설비 상태코드 ::"+szYD_EQP_STAT;
			    				szMsg    = "["+szOperationName+"] " + szRtnMsg;
			    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			    				return szRtnMsg;
							}
					}
				}
				else{  //기준 스케줄코드가 없을 경우 error return
					szRtnMsg = "입력받은 스케줄코드가 기준정보 내 미존재. ";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
			}
			
			if("Y".equals(szPILNG_WRK_GP)) {
				for(int ii=0; ii < szPlateId.length; ii++) {
					if(!"".equals(szPlateId[ii])) {
						iPlateCnt++;
					}
				}
			} else {
				iPlateCnt	 = 1;
			}
			

			//------------------------------------------------------------------------------------------------
			// 1. 파라미터 체크
			// 1.1 북인   시 trkZoneNo -> to위치       ,   yardNo -> from 위치
			// 1.1 북아웃시 trkZoneNo -> from 위치 ,   yardNo -> to위치
			//------------------------------------------------------------------------------------------------
			if ("1".equals(szOperationType)) { // Book In

				szFromLoc 	= szYdNo;
				szToLoc 	= szTrkZoneNo;

				// From 위치
				if (szYdNo == null || szYdNo.length() < 6) {

					for(int ii=0; ii < iPlateCnt; ii++) {
						// 저장위치 없이 판번호만 들어 왔을때 처리
						if (!"".equals(szPlateId[ii])) {
	
							szMsg    = "["+szOperationName+"] 저장위치 없이 판번호만 들어 왔을때 .... SKIP";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	
						} else {
							szRtnMsg = "수신전문중 FROM위치 ERROR >>>> " + szYdNo;
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}
					}
				}

				if (szYdNo.length() > 1) {
					if (!"P".equals(ydUtils.substr(szYdNo, 0, 1))) {
						szRtnMsg = "수신전문중 FROM위치 ERROR >>>>>>> " + szYdNo;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}
				
			// To 위치
				
				// TRK ZONE NO를 야드저장위치로 변경
				if (!"".equals(szTrkZoneNo) && szTrkZoneNo.length() == 5) {
					// ZONE NO를 야드저장위치로 변경 :: 2300 >>>> FART01
					szToLoc = JPlateYdCommonUtils.getY2RtZoneToLoc(szTrkZoneNo);
				}
				
				if (szToLoc == null || szToLoc.equals("")) {
					szRtnMsg = "수신전문중 TO위치 ERROR >>>>>>> " + szTrkZoneNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				} 
				if (!"P".equals(ydUtils.substr(szToLoc, 0, 1))) {
					szRtnMsg = "수신전문중 TO위치 ERROR >>>>>>> " + szTrkZoneNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}


			} else if ("2".equals(szOperationType)) {		// Book Out
				
				szFromLoc 	= szTrkZoneNo;
				szToLoc 	= szYdNo;

				for(int ii=0; ii < iPlateCnt; ii++) {
				
					// PLATE_ID
					// BOOK-OUT일때는 2,3의 경우는 미존재함 즉 'PL999999'는 미존재
					if ("".equals(szPlateId[ii]) || "PL999999".equals(szPlateId[ii])) {
						szRtnMsg = "수신전문중 PLATE_ID ERROR";
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}

				// from 위치				
				
				// TRK ZONE NO를 야드저장위치로 변경
				if (!"".equals(szTrkZoneNo) && szTrkZoneNo.length() == 5) {
					// ZONE NO를 야드저장위치로 변경 :: 2300 >>>> FART01
					szFromLoc = JPlateYdCommonUtils.getY2RtZoneToLoc(szTrkZoneNo);
				}
				
				szMsg    = "["+szOperationName+"]  >>>> szFromLoc1 :: " + szFromLoc;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				
				if (szFromLoc == null || szFromLoc.equals("")) {
					szRtnMsg = "수신전문중 FROM위치 ERROR >>>>>>> " + szTrkZoneNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				} 
				
				if (!"P".equals(ydUtils.substr(szFromLoc, 0, 1))) {
					szRtnMsg = "수신전문중 FROM위치 ERROR >>>>>>> " + szTrkZoneNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
				
				if("33010".equals(szTrkZoneNo)) { //33010:PCRT01 Book-Out 
					recPara.setField("STL_NO", 		szPlateId[0]);
					rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getTmAsGubun", logId, szMethodName, "극후물대 RT Book-Out시 TM재, AS롤재 구분 알아내기");
					/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getTmAsGubun 
					SELECT SUBSTR(PL_MFSD_NO,8,1) AS GUBUN -- A:TM재, B:AS롤재
					FROM   TB_PR_ROLL_MAT
					WHERE  PL_MPL_NO = :V_STL_NO
 
					UNION ALL

					SELECT SUBSTR(PL_MFSD_NO,8,1) AS GUBUN 
					FROM   TB_PR_PLATE_MAT
					WHERE  PL_PLATE_NO = :V_STL_NO
					*/
					if (rsResult.size() > 0) {
						if("B".equals(rsResult.getRecord(0).getFieldString("GUBUN"))) {
							//AS롤재
							isAsRollMtl = true;
						}
					}
				}

				// TO 위치 가 6자리가 아닐 때..
				if (!"P".equals(ydUtils.substr(szYdNo, 0, 1)) || szYdNo.length() < 6) {
					
					if("33010".equals(szTrkZoneNo) || "20000".equals(szTrkZoneNo)) { //33010:PCRT01 Book-Out 

						// TO 위치 6자리 체크 안함..
						
					} else {
						// TO 위치가 6자리가 아니면 에러처리..
						//if ("P2YDL501".equals(szRegister)||"P3YDL501".equals(szRegister)) {
						//	szRtnMsg = "수신전문중 TO위치 ERROR >>>>> " + szYdNo;
						//	szMsg    = "["+szOperationName+"] " + szRtnMsg;
						//	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						//	return szRtnMsg;
						//}
					}
				}

				String sToBay = ydUtils.substr(szToLoc,   1, 1);
				String sFrBay = ydUtils.substr(szFromLoc, 1, 1);
				/*
				 * 바로 C동으로 빼달라는 구분자로 셋팅하고
				 * To위치는 B동으로 수신된경우 - B동에 스케쥴이 만들어지기 때문에 
				 * 강제로 To위치동을 B동으로 바꿈
				 */
				if(szSPARE.length() > 0) {
					if("Y".equals(szSPARE.substring(0,1))) {
						
					}else{
						if("B".equals(sToBay)) { 
							sToBay = "C";
						}
					}
				}
				
				// Book-Out 이면서 From 위치가 00010 존이고 To위치가 C동이면 To위치를 00011 존으로 변경한다.
				if("0010N".equals(szTrkZoneNo)||"0010A".equals(szTrkZoneNo)||"0010B".equals(szTrkZoneNo)) { //0001N:PBRT1N, 0001A:PBRT1A, 0001B:PBRT1B Book-Out
					if("C".equals(sToBay)) { //To위치가 C동일 경우
						if("Y".equals(szPILNG_WRK_GP)) {
							//파일링 대상재이면...
							szToLoc = "PBRTWB"; //00011 존 
						} else {
							
							if(szSPARE.length() > 0) {
								if("Y".equals(szSPARE.substring(0,1))) {
									//00010 존에서 1매 권상해서 00011존에 권하하는 작업지시 생성
									szToLoc = "PBRTWB"; //00011 존 
									
								} else {
									
									//낱장으로 C동으로 가는 경우 (00011존 -->00012존) C동 00012 존에서 BOOK-OUT 지시 생성
									szToLoc = "PCXXXX"; //szYdNo;
									szFromLoc = "PCRT40"; //00012존
								}
								
							} else {
								//낱장으로 C동으로 가는 경우 (00011존 -->00012존) C동 00012 존에서 BOOK-OUT 지시 생성
								szToLoc = "PCXXXX"; //szYdNo;
								szFromLoc = "PCRT40"; //00012존
							}
						}
					}
				} else {
				
					// TO위치의 동과 FROM위치의 동이 상이할 경우 TO위치의 동을 스케줄 기준으로 변경 : 2014.07.08 보완
					if (!sToBay.equals(sFrBay)) {
						szMsg   = "["+szOperationName+"] To위치의 동과 From위치의 동이 상이하여 스케쥴 기준 적용 >>>>" + szToLoc + "," + szFromLoc;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						szToLoc = ydUtils.substr(szFromLoc, 0, 2);
					}
				}

				// Book-Out원인
//				if ("".equals(szReasonCode)) {
//					szRtnMsg = "수신전문중 Book-Out원인 ERROR";
//					szMsg    = "["+szOperationName+"] " + szRtnMsg;
//					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
//					return szMsg;
//				}

				// BOOK-OUT시 BED는 '01' 임 (FROM위치가 RT임으로)
				szOperationBed = "01";
			}

			// Book In 시 저장위치 조회 
			if ("1".equals(szOperationType) && !isPillingModBookIn) {  //파일링모드의 북인일경우는 해당 로직 제외(신규 파일링 재료가 적치단에 없으므로)

				for(int ii=0; ii < iPlateCnt; ii++) {
					if ("".equals(szPlateId[ii])) {			// 재료번호 미존재시
	
		    			szYdStkColGp = szFromLoc;
	
						szMsg    = "["+szOperationName+"] 북인작업중 .... 재료번호 미존재 >>>> 저장위치 :: " + szYdStkColGp;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	
					} else {
						// 베드정보 조회
						recPara.setField("STL_NO", 	szPlateId[ii]);             	// 재료번호
						recPara.setField("YD_GP",	JPlateYdConst.YD_GP_P_PLATE_YARD);
						rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStockWithLoc", logId, szMethodName, "베드정보 조회-야드재료가 존재하는지 확인");
						if(rsResult.size() < 1) {
							szRtnMsg = "야드재료가 미존재 합니다 .... 재료번호:" + szPlateId[ii];
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}
	
		    			rsResult.first();
						tempRec = JDTORecordFactory.getInstance().create();
		    			tempRec = rsResult.getRecord();
	
		    			szYdStkColGp	= StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"),"");
		    			szOperationBed 	= StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_STK_BED_NO"),"");
						szYdStkLyrNo[ii]= StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_STK_LYR_NO"),"");
					}
				}

				String sToBay = ydUtils.substr(szToLoc, 0, 2);
				if (!ydUtils.substr(szYdStkColGp,0,2).equals(sToBay)) {
					szRtnMsg = "해당동의 재료만 Book-IN 가능! .... 재료위치:: " + szYdStkColGp + ", Book-IN위치:: " + szToLoc;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
			}

			
			//if ("1".equals(szOperationType)) {
			//(신규개발)파일링 신규코드:파일링시 이전 파일링 작업/스케줄 정보 클린
			//if("PILLING".equals(szDEV_INPUT1)){  //(파일링추가) 파일링 작업시, 작업예약 존재시 해당 작업예약 삭제
			if(isPillingModBookIn || isPillingModBookOut){	 //(파일링추가) 파일링 작업시, 작업예약 존재시 해당 작업예약 삭제(북인/북아웃 공통)
				EJBConnector 		ejbConn 	= null;
				JDTORecord recCheck = null;
				JDTORecord outRecord1 = null;
				
				//작업 예약 내 파일링 작업 존재 여부 체크
				//recPara.setField("YD_DEL_YN", 		"N");       // 남아있는 작업예약 대상      	
				recPara.setField("YD_SCH_CD", 		"PFRTAPLM");        	//임시 스케줄코드번호, 확정 후 변경 필요.

				rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getByYdSchCd2", logId, szMethodName, "재료별 크레인 스케줄 존재여부 확인"); //생성 쿼리
				szPillingDelStlNo=new String[rsResult.size()];
				szPillingDelStlNoStkLyrNo=new String[rsResult.size()];
				
				
				if(rsResult.size()>0){
					for(int i=0; i<rsResult.size();i++){ //스케줄 아이디별 loop-> 삭제
						tempRec=rsResult.getRecord(i);
						String temp_szYdCrnSchId =ydDaoUtils.paraRecChkNull(tempRec, "YD_CRN_SCH_ID"); 
						String temp_szYdSchCd=ydDaoUtils.paraRecChkNull(tempRec, "YD_SCH_CD");
						String temp_szStlno=ydDaoUtils.paraRecChkNull(tempRec, "STL_NO");
						String temp_szYD_STK_LYR_NO=ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_LYR_NO");
						String temp_szYD_WBOOK_ID=ydDaoUtils.paraRecChkNull(tempRec, "YD_WBOOK_ID");
						String temp_szModifier=ydDaoUtils.paraRecModifier(tempRec);
						String temp_szYD_WRK_PROG_STAT=ydDaoUtils.paraRecChkNull(tempRec, "YD_WRK_PROG_STAT");
						
						String temp_szYD_DN_WO_LOC=ydDaoUtils.paraRecChkNull(tempRec, "YD_DN_WO_LOC");
						String temp_szYD_DN_WO_LAYER=ydDaoUtils.paraRecChkNull(tempRec, "YD_DN_WO_LAYER");

						
						if(i==0){  //스케줄ID, 작업예약 ID 기반 삭제는 재료개수만큼 삭제할 필요 없음.
							//1.1 작업지시(스케줄) 삭제
							recPara.setField("YD_CRN_SCH_ID", 		temp_szYdCrnSchId);        	
							recPara.setField("MODIFIER", 		temp_szModifier);
							recPara.setField("DEL_YN", 		"Y");
								
							intRtnVal=commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.delYdCrnSch", logId, szMethodName, "하단 파일링 할 기존 크레인작업지시 삭제");
								
				                if (intRtnVal <= 0) {
				    				szMsg = "["+szOperationName+"] 작업지시 CLEAR시 오류 발생 .. " + Integer.toString(intRtnVal);
				    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				                }
				                
				             //1.11 작업지시(스케줄) 삭제 전문 L2 전송
							recPara   = JDTORecordFactory.getInstance().create();
							recPara.setField("YD_CRN_SCH_ID",    	temp_szYdCrnSchId);
							recPara.setField("YD_WRK_PROG_STAT", 	temp_szYD_WRK_PROG_STAT);   	
							recPara.setField("MSG_GP",           	"D");
								
							recPara.setField("MSG_ID",    			"YDY2L004");
							recPara.setField("YD_GP",            	JPlateYdConst.YD_GP_P_PLATE_YARD);
							szRtnMsg = ydDelegate.sendMsg(recPara);
								
							szMsg = "[JSP Session : "+szOperationName+"] 크레인 작업지시 취소전문 L2 전송 --- END >>>> " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
							
							//1.2 작업지시 재료(스제줄 재료) 삭제
	
							recPara.setField("YD_CRN_SCH_ID", 		temp_szYdCrnSchId);        	
							//recPara.setField("STL_NO", 			temp_szStlno);
							recPara.setField("MODIFIER", 		temp_szModifier);
							recPara.setField("DEL_YN", 		"Y");
							
							intRtnVal=commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.delYdCrnWrkMtl", logId, szMethodName, "하단 파일링 할 기존 크레인작업재료 삭제");
							
			                if (intRtnVal <= 0) {
			    				szMsg = "["+szOperationName+"] 작업지시 재료 CLEAR시 오류 발생 .. " + Integer.toString(intRtnVal);
			    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			                }
							
							//1.3 작업예약 삭제
							
							recPara.setField("YD_WBOOK_ID",        temp_szYD_WBOOK_ID);
							recPara.setField("DEL_YN",             "Y");
							recPara.setField("MODIFIER", 		temp_szModifier);
	
			                intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.delYdWrkbook", logId, szMethodName, "작업예약정보를 삭제");
	
			                if (intRtnVal <= 0) {
			    				szMsg = "["+szOperationName+"] 작업예약 CLEAR시 오류 발생 .. " + Integer.toString(intRtnVal);
			    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			                }		
						}
							//1.4 작업예약 재료 삭제
							recPara.setField("YD_WBOOK_ID",        temp_szYD_WBOOK_ID);
							recPara.setField("DEL_YN",          "Y");
							recPara.setField("STL_NO",          temp_szStlno);
							recPara.setField("MODIFIER", 		temp_szModifier);
	
			                intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.deldWrkbookMtl", logId, szMethodName, "작업예약정보를 삭제");
			                
			                if (intRtnVal <= 0) {
			    				szMsg = "["+szOperationName+"] 작업예약 재료 CLEAR시 오류 발생 .. " + Integer.toString(intRtnVal);
			    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			                }
							//작업재료 삭제시, 삭제 재료 데이터(재료명, 적치단, 삭제 재료 수) 보관
							szPillingDelStlNo[i]=temp_szStlno; //삭제한 재료데이터 보관
							szPillingDelStlNoStkLyrNo[i]=temp_szYD_STK_LYR_NO; //삭제 재료의 적치단 보관
							iDelStlNo++; //삭제한 재료데이터의 총수 보관
						
						
						//1.5 야드맵 초기화(적치단 클리어)		
		                if ("".equals(temp_szStlno)) {
	                		recPara.setField("YD_STK_COL_GP", temp_szYD_DN_WO_LOC.substring(0,6));
	                		recPara.setField("YD_STK_BED_NO", temp_szYD_DN_WO_LOC.substring(6,8));
	                		recPara.setField("YD_STK_LYR_NO", temp_szYD_DN_WO_LAYER);
	                		recPara.setField("YD_STK_LYR_MTL_STAT", 		"E");
	                		recPara.setField("STL_NO", "");
	                		recPara.setField("MODIFIER", 		temp_szModifier);
	                		
	                		intRtnVal = ydStkLyrDao.updYdStklyrStat(recPara);  	//적치단의 재료정보 Clear
		                }
		                else{
	                		recPara.setField("YD_STK_COL_GP", temp_szYD_DN_WO_LOC.substring(0,6));
	                		recPara.setField("YD_STK_BED_NO", temp_szYD_DN_WO_LOC.substring(6,8));
	                		recPara.setField("STL_NO", temp_szStlno);
	                		recPara.setField("MODIFIER", 		temp_szModifier);
	                		recPara.setField("YD_GP", 		JPlateYdConst.YD_GP_P_PLATE_YARD);
	                		
	                		intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);  //적치단의 재료정보 Clear
		                }
		                
		                if (intRtnVal <= 0) {
		    				szMsg = "["+szOperationName+"] 저장위치 적치단 CLEAR시 오류 발생 .. " + Integer.toString(intRtnVal);
		    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
		                }
					}
				}  
			}		
			
			
			//------------------------------------------------------------------------------------------------
			// 1.2. 해당 재료 작업예약/스케쥴 존재여부 확인
			//------------------------------------------------------------------------------------------------
			for(int ii=0; ii < iPlateCnt; ii++) {
				if (!"".equals(szPlateId[ii])) {
					// ------------------------------------------------------------------------
					// 1.2.1. 작업예약 존재여부 확인
					// ------------------------------------------------------------------------
					recPara.setField("STL_NO", 		szPlateId[ii]);             	// 재료번호
					recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);
	
					rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getExistByStlNo", logId, szMethodName, "작업예약 존재여부 확인");
					
					if (rsResult.size() > 0) {
						szRtnMsg = "해당 재료["+szPlateId[ii]+"]로 작업예약이 존재!";
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
	
					// ------------------------------------------------------------------------
					// 1.2.2. 크레인 작업지시 존재여부 확인
					// ------------------------------------------------------------------------
					recPara.setField("STL_NO", 		szPlateId[ii]);             	// 재료번호
					recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);
	
					rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getExistByStlNo", logId, szMethodName, "크레인 작업지시 존재여부 확인");

					if (rsResult.size() > 0) {
						szRtnMsg = "해당 재료"+szPlateId[ii]+"로 크레인 작업지시 존재!";
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
	
					//------------------------------------------------------------------
					// 1.2.3. 현재 저장위치가 1후판 정정야드가 아닐경우 오류로 처리
					//------------------------------------------------------------------
					szRtnMsg = JPlateYdCommonUtils.checkUpdYdLocYdP(szPlateId[ii], JPlateYdConst.YD_GP_P_PLATE_YARD, "N");
					if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
						szMsg = "[ " +szOperationName + "] 북아웃시 저장위치 확인 오류! >>>> " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}
			}
			
			//------------------------------------------------------------------------------------------------
			// 2. 야드재료 등록 (BOOK-OUT시)
			//------------------------------------------------------------------------------------------------
			if ("2".equals(szOperationType) || isPillingModBookIn) {		// Book Out  //파일링 BOOK-IN 모드시에도 야드재료 등록될 수 있도록 추가

				//------------------------------------------------------------------------------------------------
				// 2.1. 야드재료 조회
				//------------------------------------------------------------------------------------------------
				szMsg    = "["+szOperationName+"] ----------- 야드재료 조회 START ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				// 레코드 편성
				for(int ii=0; ii < iPlateCnt; ii++) {

					recPara.setField("STL_NO", 	szPlateId[ii]);             	// 재료번호
					recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);
					rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStockWithLoc", logId, szMethodName, "야드재료 조회");
					if (rsResult.size() > 0) {
	
		    			szYdStkColGp = StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"),"");
	
		    			if ("".equals(szYdStkColGp) || "RT".equals(ydUtils.substr(szYdStkColGp, 2, 2))) {
	
							szMsg    = "["+szOperationName+"] 해당 재료의 저장위치 :: " + szYdStkColGp + " 계속 진행 ";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	
		    			} else {
							szRtnMsg = "야드재료가 이미 존재합니다 .... 현위치::" + szYdStkColGp;
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							//return szRtnMsg;
		    			}
					}
				}
				
				//------------------------------------------------------------------------------------------------
				// 2.3. 야드재료 등록
				//------------------------------------------------------------------------------------------------
				szMsg    = "["+szOperationName+"] ----------- 야드재료 등록 START ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				String tmpTrkZoneNo=szTrkZoneNo;  //szTrkZoneNo 여기에 59020이런식으로 안들어오고 PFRT60 이런식으로 들어올 경우, 다시 59020으로 치환해서 저장필요.20230602
				if(tmpTrkZoneNo.length()==6){//트랙킹존 방식으로 치환
					tmpTrkZoneNo=JPlateYdCommonUtils.getLocToY2RtZone(szTrkZoneNo);  //PFRT60->59020
					
					szMsg    = "L2 트랙킹존 형식으로 치환 결과:"+szTrkZoneNo+"=>"+tmpTrkZoneNo;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				}
				
				
				for(int ii=0; ii < iPlateCnt; ii++) {
				
					recPara.setField("REGISTER"				,szRegister);							// 등록자
					recPara.setField("STL_NO"				,szPlateId[ii]);             			// 재료번호
					recPara.setField("BOOK_OUT_RESN"		,szReasonCode);							// Book-Out원인
					recPara.setField("BOOK_OUT_DATE"		,JPlateYdUtils.getCurDate("yyyyMMdd"));	// Book-Out일자
					recPara.setField("BOOK_OUT_PROG"		,"");           						// Book-Out공정
					recPara.setField("FRTOMOVE_PLANT_GP"	,szCraneNo);           					// 북인대상재 구분 (이송공장구분 항목사용)
					recPara.setField("ARR_WLOC_CD"			,szTrkZoneNo);  						// 후판트래킹존지정 chito20230202  //tmpTrkZoneNo 이걸로 대체
					recPara.setField("YD_FRTOMOVE_YD_GP"	,szOperationMode);  					// 북아웃모드	 chito20230202
					
	    			intRtnVal = ydStockDao.insYdStockBookOut(recPara);
					if (intRtnVal <= 0) {
						szRtnMsg = "재료정보 미존재로 오류 발생 .. " + szPlateId[ii] + ", 오류코드::" + Integer.toString(intRtnVal);
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}
				
				//------------------------------------------------------------------------------------------------
				// 2.4. 해당 재료 적치위치 비우기
				//------------------------------------------------------------------------------------------------
				szMsg    = "["+szOperationName+"] ----------- 해당 재료 적치위치 비우기 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
 
				for(int ii=0; ii < iPlateCnt; ii++) {
					recPara.setField("STL_NO", 				szPlateId[ii]);             	// 재료번호
					recPara.setField("YD_STK_LYR_MTL_STAT", "C");             		// 야드적치단재료상태
					recPara.setField("YD_GP",				JPlateYdConst.YD_GP_P_PLATE_YARD);
					recPara.setField("MODIFIER", 			szRegister);
	
					intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrClearByStlNo", logId, szMethodName, "해당 재료 적치위치 비우기");
					
					if (intRtnVal < 0) {
						szRtnMsg = "저장위치 삭제 처리시 오류 발생!1 .... 오류코드 :" + Integer.toString(intRtnVal);
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					}
				}
				
				
				//------------------------------------------------------------------------------------------------
				// 2.4. 적치단 수정
				//------------------------------------------------------------------------------------------------
				szMsg    = "["+szOperationName+"] ----------- 적치단 야드적치단재료상태 수정 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				if(isAsRollMtl) {
					//33010에서 Book-Out 되고 As롤 재이면 냉각대의 빈 곳을 From 위치로 한다..
					
					recPara.setField("YD_STK_COL_GP", 	"PCCB");
					rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getCBEmptyLoc", logId, szMethodName, "냉각대 빈 Bed 찾기");
					if(rsResult.size() > 0) {
						szFromLoc = rsResult.getRecord(0).getFieldString("YD_STK_COL_GP");
						szFromBed = rsResult.getRecord(0).getFieldString("YD_STK_BED_NO");
					}
					
				} else {
					szFromBed = "01";
				}
				
				int MaxYdStkLyrNo=0;				//파일링모드용 신규 추가: 재료 적치단수의 max 값 파악용도
				//적치단 야드적치단재료상태 수정
				recPara = JDTORecordFactory.getInstance().create();
				for(int ii=0; ii < iPlateCnt; ii++) {
					recPara.setField("YD_STK_COL_GP", 		szFromLoc);                 // 야드적치열구분
					recPara.setField("YD_STK_BED_NO", 		szFromBed);    				// 야드적치BED번호
					recPara.setField("YD_STK_LYR_NO", 		szYdStkLyrNo[ii]);			// 야드적치단
					recPara.setField("STL_NO", 				szPlateId[ii]);            	// 재료번호
					recPara.setField("YD_STK_LYR_MTL_STAT", "C"); 						// 적치완료
					recPara.setField("MODIFIER", 			szRegister);				// 등록자
	
					intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat", logId, szMethodName, "적치단 야드적치단재료상태 수정");
					
					if (intRtnVal <= 0) {
						szRtnMsg = "야드적치단재료상태 수정 ERROR .. " + Integer.toString(intRtnVal);
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
					if(MaxYdStkLyrNo<Integer.parseInt(szYdStkLyrNo[ii])){
						MaxYdStkLyrNo=Integer.parseInt(szYdStkLyrNo[ii]);    //(파일링 모드용)쌓인 적치단 번호 중 최대값 저장
					}
				}
				
				//if("PILLING".equals(szDEV_INPUT1)){  //삭제한 이전 야드맵 재료 추가
				if(isPillingModBookIn || isPillingModBookOut){	//(파일링 모드용)삭제한 이전 야드맵 재료 추가
					for(int ii=0; ii<iDelStlNo; ii++){
						recPara.setField("YD_STK_COL_GP", 		szFromLoc);                 // 야드적치열구분
						recPara.setField("YD_STK_BED_NO", 		szFromBed);    				// 야드적치BED번호
						recPara.setField("YD_STK_LYR_NO", 		szYdStkLyrNo[ii+MaxYdStkLyrNo]);			// 야드적치단
						recPara.setField("STL_NO", 				szPillingDelStlNo[ii]);            	// 재료번호
						recPara.setField("YD_STK_LYR_MTL_STAT", "C"); 						// 적치완료
						recPara.setField("MODIFIER", 			szRegister);				// 등록자
						
						intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat", logId, szMethodName, "적치단 야드적치단재료상태 수정");
						
						if (intRtnVal <= 0) {
							szRtnMsg = "(파일링)야드적치단재료상태 수정 ERROR .. " + Integer.toString(intRtnVal);
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}
					}
				}

				//------------------------------------------------------------------------------------------------
				// 2.5. 야드재료 등록후 L2에 저장품제원 정보 송신 .. 작업지시 송신 이전에 전송해야함
				//      야드L2 전문송신 (저장품제원 :: YDY2L002 전송)
				//------------------------------------------------------------------------------------------------
				szMsg = "[ " +szOperationName + "] 야드L2 저장품제원 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				recL2Para = JDTORecordFactory.getInstance().create();
				for(int ii=0; ii < iPlateCnt; ii++) {
					recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
					recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
					recL2Para.setField("YD_STK_COL_GP", 	szFromLoc);                          	// 야드적치열구분
					recL2Para.setField("YD_STK_BED_NO", 	szFromBed);    							// 야드적치BED번호
					recL2Para.setField("YD_INFO_SYNC_CD", 	"4");									// 야드정보동기화코드  [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
					recL2Para.setField("STL_NO", 			szPlateId[ii]);	        					// 재료번호
					if(isPillingModBookIn)
						recL2Para.setField("isPillingModBookIn", 			"Y");	        					// 파일링 북인모드 여부 조회(북인모드시 마지막장만 전송)
					else
						recL2Para.setField("isPillingModBookIn", 			"N");
					szRtnMsg = ydDelegate.sendMsg(recL2Para);
				}
				szMsg = "[ " +szOperationName + "] 야드L2 저장품제원 전문송신 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				
				
				//(파일링) 이전 재료번호 야드재료 등록후 L2에 저장품제원 정보 송신 
				szMsg = "[ " +szOperationName + "] (이전 파일링 재료) 야드L2 저장품제원 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				
				//if("PILLING".equals(szDEV_INPUT1)){  //삭제한 이전 야드맵 재료 추가
				//if(isPillingModBookIn || isPillingModBookOut){  //(파일링 모드 전용)삭제한 이전 야드맵 재료 추가(북아웃/북인 공통)
				if(isPillingModBookOut){  //(파일링 모드 전용)삭제한 이전 야드맵 재료 추가(북아웃/북인 공통)
					for(int ii=0; ii<iDelStlNo; ii++){
						recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
						recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
						recL2Para.setField("YD_STK_COL_GP", 	szFromLoc);                          	// 야드적치열구분
						recL2Para.setField("YD_STK_BED_NO", 	szFromBed);    							// 야드적치BED번호
						recL2Para.setField("YD_INFO_SYNC_CD", 	"4");									// 야드정보동기화코드  [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
						recL2Para.setField("STL_NO", 			szPillingDelStlNo[ii]);	        					// 재료번호
						szRtnMsg = ydDelegate.sendMsg(recL2Para);
					}
					
					szMsg = "[ " +szOperationName + "] (이전 파일링 재료) 야드L2 저장품제원 전문송신 END >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				}
				
				/*
				 * 2018.08.31 윤재광 - 유창훈 대리 요구사항 반영
				 * 60012존 북아웃시 재료에 따른 To위치 결정
				 * 2019.04.05 윤재광 - 강기석 주임 요청으로 막음
				 */
				/*
				String sHmiStat 		= "N";
				String sQuery1			= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
				JDTORecord wbJr 		= (new com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO()).getData(sQuery1, new Object[]{ "2XRT04" });
				if (wbJr != null){ 
					sHmiStat	= StringHelper.evl(wbJr.getFieldString("HMI_STAT"), "");
				}
				if("Y".equals(sHmiStat)){
					if("60012".equals(szTrkZoneNo)){
						
						JDTORecordSet jsRcds = JDTORecordFactory.getInstance().createRecordSet("yd");
						JDTORecord jrRcd = JDTORecordFactory.getInstance().create();
						jrRcd.setField("PLATE_NO", szPlateId[0]);
						
						com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao dao = new com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao();
						
						intRtnVal = dao.getYdStock(jrRcd, jsRcds, 4);
						
						String sCurrProgCd 	= "";
						int iPlMeaGdsL  	= 0;
						
						if(intRtnVal > 0 ){

							jsRcds.first();
							jrRcd = JDTORecordFactory.getInstance().create();
							jrRcd = jsRcds.getRecord();
							
							sCurrProgCd 	= ydDaoUtils.paraRecChkNull(jrRcd,"CURR_PROG_CD");
							iPlMeaGdsL  	= ydDaoUtils.paraRecChkNullInt(jrRcd,"PL_MEA_GDS_L");
							
							if("C".equals(sCurrProgCd)){
								
								szToLoc = "PF0104";
								
							}else{
								
								if(iPlMeaGdsL >  14000){
									szToLoc = "PF0103";
								}else{
									szToLoc = "PF0102";
								}
							}
						}
						
						szMsg = "[ " +szOperationName + "] 60012존 북아웃요청시 처리 >>>> " + szToLoc +"/"+ sCurrProgCd +"/"+ iPlMeaGdsL;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					}
				}
				*/
			}
 			
			/**********************************************************
			* 3. 스케줄기준 정보 Check
			**********************************************************/
			if ("".equals(szFromLoc)) {
				szYdGp    = ydUtils.substr(szToLoc, 0, 1); 		// 야드구분
				szYdBayGp = ydUtils.substr(szToLoc, 1, 1); 		// 야드동구분
			} else {
				szYdGp    = ydUtils.substr(szFromLoc, 0, 1); 	// 야드구분
				szYdBayGp = ydUtils.substr(szFromLoc, 1, 1); 	// 야드동구분
			}

			//야드스케쥴코드 : 야드+동+RT+마그네틱크레인번호+L(BOOK-OUT)+M(분할없음) : FART0?LM, FBRT0?LM, FCRT0?LM
			//야드스케쥴코드 : 야드+동+RT+마그네틱크레인번호+U(BOOK-IN)+M(분할없음)  : FART0?UM, FBRT0?UM, FCRT0?UM

			if ("1".equals(szOperationType)) {				// Book IN
			//	szYdSchCd   = szYdGp + szYdBayGp + "RT0?UM";
				szYdSchCd	= ydUtils.getRtSchCdYdP(szToLoc, "UM");
				if(isPillingModBookIn){
					szYdSchCd="PFRTAPUM";  //파일링 북인모드 전용 스케줄코드
				}
			} else if ("2".equals(szOperationType)) {		// Book Out
			//	szYdSchCd   = szYdGp + szYdBayGp + "RT0?LM";
				if(isAsRollMtl) {
					//33010 존에서 Book-Out이고 As롤재 일 때..
					szYdSchCd	= "PCCB01MM";  
				} else { //(신규개발)
					if(isPillingModBookOut){
						szYdSchCd="PFRTAPLM"; //파일링 북아웃모드 전용 스케줄코드 
					}
					else{
					szYdSchCd	= ydUtils.getRtSchCdYdP(szFromLoc, "LM");
					}
				}
			}

			szMsg    = "[" + szOperationName + "] >>>> FROM위치 >>>> [" + szFromLoc + "]";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg    = "[" + szOperationName + "] >>>> TO위치 >>>> [" + szToLoc + "]";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg    = "[" + szOperationName + "] >>>> S1YDL013 스케쥴코드 >>>> " + szYdSchCd;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szYdSchProhExn  = "";  			// 야드스케쥴금지유무
			szYdWrkCrnPrior = "1"; 			// 야드작업크레인우선순위
			
			if(!isPillingModBookIn && !isPillingModBookOut){  //56020존 파일링 명령시, 야드기준(YD_RULE)에 할당한 설비 활용.
				szYdWrkCrn      = "";			// 야드설비ID (NOT NULL이기 때문에 RULE에 등록된 설비ID SET)
			}
			recPara  = JDTORecordFactory.getInstance().create();
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
			recPara.setField("YD_SCH_CD", szYdSchCd); //야드스케쥴코드

			//야드스케쥴금지유무 조회
			ydSchRuleDao.getYdSchrule(recPara, rsResult);

			if (rsResult != null && rsResult.size() > 0) {
				szYdSchProhExn  = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_SCH_PROH_EXN");
				//야드작업크레인우선순위 : 야드스케쥴코드에 해당하는 작업크레인의 우선순위이므로
				//실제 요청한 크레인의 우선순위와 다를 수 있음
				if(!isPillingModBookIn && !isPillingModBookOut){  //56020존 파일링 명령시, 야드기준(YD_RULE)에 할당한 설비 활용.
					szYdWrkCrnPrior = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_CRN_PRIOR");
					szYdWrkCrn		= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_CRN");
				}
			}

			if ("".equals(szYdSchProhExn)) {
				//스케줄기준 Table 정보 Check
				szRtnMsg = "스케쥴코드[" + szYdSchCd + "] 정보 없음";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			} else if ("Y".equals(szYdSchProhExn)) {
				//스케줄 금지여부 Check
				szRtnMsg = "스케쥴코드[" + szYdSchCd + "] 기동금지";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			
			if ("1".equals(szOperationType) && "3".equals(szOperationMode)) {
				// BOOK-IN & 후판북아웃모드가 3:End 이면 
				
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////

				String sNEW_MODULE_EFF_YN = "N";
				
				JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
				
				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A017"); //1후판정정야드 북인요구END 크레인스케줄,작업예약 삭제처리 여부 
				
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 북인요구END 크레인스케줄,작업예약 삭제처리 여부   : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
				
				if(sNEW_MODULE_EFF_YN.equals("Y")) {
					
					EJBConnector 		ejbConn 	= null;
					
					String szYdCrnSchId;
					String szStlNo;
					String szC_YD_WRK_PROG_STAT;
					String szYD_WBOOK_ID;
					String szYD_SCH_CD;
					String szYD_EQP_ID;
					
					JDTORecord recCheck = null;
					JDTORecord outRecord1 = null;
					JDTORecord setRecord 	= JDTORecordFactory.getInstance().create();
					
					String 	sRTN_CD					= "";
					String 	sRTN_MSG				= "";
					
					
					//해당 열에 잡힌 크레인 작업지시를 조회하여 작업취소 
					JDTORecord recPara2		= JDTORecordFactory.getInstance().create();
					
					recPara2.setField("YD_STK_COL_GP"	, szFromLoc);    
					
					JDTORecordSet getRecSet	= commDao.select(recPara2, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdCrnSchIdByLoc", logId, szMethodName, "해당 열에 잡힌 크레인 작업지시 조회");
					
					if (getRecSet.size() > 0) {
						
						//크레인 작업지시 취소 처리
						for (int ii=0; ii<getRecSet.size(); ii++) {
							
							szYdCrnSchId  	= getRecSet.getRecord(ii).getFieldString("YD_CRN_SCH_ID");
							szYD_SCH_CD 		= getRecSet.getRecord(ii).getFieldString("YD_SCH_CD"); 
							szStlNo			= getRecSet.getRecord(ii).getFieldString("STL_NO"); 
							
							if ("".equals(szYdCrnSchId)) {
								//szMsg = "["+szOperationName+"] 스케줄 취소 처리("+szMethodName+") 실패, YD_CRN_SCH_ID값이 없음";
								//ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
								continue;
							}		
							
							recPara2.setField("YD_CRN_SCH_ID", szYdCrnSchId);
							recPara2.setField("YD_SCH_CD",     szYD_SCH_CD);
							recPara2.setField("DEL_YN",        "N");
							recPara2.setField("MODIFIER",      szRegister);
							
							/*
							 * 크레인 스케줄에서 선택된 스케줄 ID로 작업예약을 조회한 후
							 * 삭제되지 않는 작업예약에 포함된 크레인 스케줄중 첫번째 값이
							 * 2,3 인 경우 후처리를 하지않기 위해  Check Logic 반영
							 */
							rsResult = JDTORecordFactory.getInstance().createRecordSet("temRs");

							intRtnVal = ydCrnSchDao.getCheckYdCrnSchId(recPara2, rsResult);		// intGp == 36

							if (intRtnVal < 1) {
								szMsg = "["+szOperationName+"] 취소 작업을 완료 하였습니다.";
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
								continue;
							}

							rsResult.first();
							recCheck = rsResult.getRecord();

							szC_YD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recCheck, "YD_WRK_PROG_STAT");

							//2,3, 인 경우 와 혹시모를 4이면서도 스케줄 삭제가 되지 않은 경우
							if ("2".equals(szC_YD_WRK_PROG_STAT) || "3".equals(szC_YD_WRK_PROG_STAT) || "4".equals(szC_YD_WRK_PROG_STAT)) {
								szMsg = "["+szOperationName+"] 크레인 작업이 완료되지 않았습니다!!";
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
								continue;
							}

							/*
							 * 2,3이 아닌 경우 스케줄 취소기능에  첫번째 크레인 스케줄 ID 정보를 전송
							 */
							recPara = JDTORecordFactory.getInstance().create();
							recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recCheck, "YD_CRN_SCH_ID"));
							recPara.setField("YD_SCH_CD",     ydDaoUtils.paraRecChkNull(recCheck, "YD_SCH_CD"));
							recPara.setField("DEL_YN",        ydDaoUtils.paraRecChkNull(recCheck, "DEL_YN"));
							recPara.setField("MODIFIER",      szRegister);

							//크레인스케줄 ID보다 이상인 ID 삭제  업데이트 실행(적치단정보까지 CLEAR)
							//스케줄취소
							szMsg = "["+szOperationName+"] 작업지시 취소 시작";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

							ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
							outRecord1 	= (JDTORecord)ejbConn.trx("cancelJPlateYdCrnSch", new Class[] { JDTORecord.class }, new Object[] { recPara });

							sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
							sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

							szMsg = "["+szOperationName+"] ---- 작업지시 취소 종료!! >>>> " + sRTN_CD + " , " + sRTN_MSG;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
							
							if ("0".equals(sRTN_CD)) {
								m_ctx.setRollbackOnly();
								szRtnMsg = "작업지시 취소 ERROR .. " + sRTN_CD;
								szMsg    = "["+szOperationName+"] " + sRTN_MSG;
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
								return szRtnMsg;
							}

							szMsg = "["+szOperationName+"] ---- 작업예약 취소 시작";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

							ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
							outRecord1 	= (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { outRecord1 });

							sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
							sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
							
							szMsg = "["+szOperationName+"] ---- 작업예약 취소 종료!! >>>> " + outRecord1.toString();
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

							if ("0".equals(sRTN_CD)) {
								m_ctx.setRollbackOnly();
								szRtnMsg = "작업예약 취소 ERROR .. " + sRTN_CD;
								szMsg    = "["+szOperationName+"] " + sRTN_MSG;
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
								return szRtnMsg;
							}

							// F?RT??LM :: RT BOOK-OUT일때 재료정보 , 적치위치 Clear
							if ("RT".equals(ydUtils.substr(szYD_SCH_CD,2,2)) && "LM".equals(ydUtils.substr(szYD_SCH_CD,6,2))) {

								recPara = JDTORecordFactory.getInstance().create();
								recPara.setField("YD_CRN_SCH_ID", szYdCrnSchId);
								recPara.setField("YD_SCH_CD",     szYD_SCH_CD);
								recPara.setField("STL_NO",        szStlNo);
								recPara.setField("MODIFIER",      szRegister);

								szMsg = "["+szOperationName+"] ---- RT BOOK-OUT 재료정보 CLEAR 시작 >>>> " + recPara.toString();
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

								ejbConn 		= new EJBConnector("default", this);
								String rtnMsg	= (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "delStockLocOnRt", recPara);

								szMsg = "["+szOperationName+"] ---- RT BOOK-OUT 재료정보 CLEAR 종료 >>>> " + rtnMsg;
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
							}
						}
						szMsg = "["+szOperationName+"] ---- 정상 취소 처리 완료>>>> " ;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					}
					
					//해당 열에 잡힌 크레인 작업예약을 조회하여 작업삭제 
					recPara2.setField("YD_STK_COL_GP"	, szFromLoc);    
					
					getRecSet	= commDao.select(recPara2, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdWrkBookIdByLoc", logId, szMethodName, "해당 열에 잡힌 크레인 작업예약 조회");
					
					if (getRecSet.size() > 0) {
						
						//크레인 작업예약 삭제 처리
						for (int ii=0; ii<getRecSet.size(); ii++) {
							szYD_WBOOK_ID	= getRecSet.getRecord(ii).getFieldString("YD_WBOOK_ID");
							szYD_SCH_CD 	= getRecSet.getRecord(ii).getFieldString("YD_SCH_CD");
							szYD_EQP_ID 	= getRecSet.getRecord(ii).getFieldString("YD_EQP_ID");
		
							recPara = JDTORecordFactory.getInstance().create();
							recPara.setField("MODIFIER",      	szRegister);
							recPara.setField("YD_WBOOK_ID",		szYD_WBOOK_ID);
							recPara.setField("YD_EQP_ID",       szYD_EQP_ID);
							recPara.setField("YD_SCH_CD",     	szYD_SCH_CD);
		
							
							szMsg = "["+szOperationName+"] ---- 작업예약 취소 시작";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		
							ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
							outRecord1 	= (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { recPara });
		
							sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
							sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
							
							szMsg = "["+szOperationName+"] ---- 작업예약 취소 종료!! >>>> " + outRecord1.toString();
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		
							if ("0".equals(sRTN_CD)) {
								m_ctx.setRollbackOnly();
								szRtnMsg = "작업예약 취소 ERROR .. " + sRTN_CD;
								szMsg    = "["+szOperationName+"] " + sRTN_MSG;
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
								return szRtnMsg;
							}
						}
						
					}
				}
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				
			} else if ("1".equals(szOperationType) && "".equals(szPlateId[0])) {
				// BOOK-IN & 재료번호 미존재시 작업예약재료 등록

				// FROM위치로 재료정보를 조회하여 작업예약 등록
				rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
				inRec    = JDTORecordFactory.getInstance().create();
				inRec.setField("YD_STK_COL_GP", 	ydUtils.substr(szFromLoc, 0, 6));   // 야드적치열
			//	inRec.setField("YD_STK_BED_NO", 	ydUtils.substr(szFromLoc, 6, 2));	// 야드적치베드
				inRec.setField("YD_STK_BED_NO", 	"");								// 연속북인시 베드정보 무시하도록 보완
				if ("2".equals(szOperationMode)) {										// 1:one time 2:Start 3:End
					inRec.setField("ROW_CNT", 		"999");		// FROM위치의 모든 재료
				} else {
					inRec.setField("ROW_CNT", 		"1");		// 1매
				}

				// BOOK-IN 대상재 조회 (적치단 역순으로)
		    	intRtnVal = ydStkLyrDao.getRTBookInMtl(inRec, rsResult);
		    	if (intRtnVal < 1) {
					szRtnMsg = "BOOK-IN 대상재 미존재 .... 저장위치 :: " + szFromLoc;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
		    	}

		    	rsResult.first();
		    	for(int ii=0; ii<rsResult.size(); ii++) {

		    		tempRec = rsResult.getRecord(ii);

					//------------------------------------------------------------------------------------------------
					// 4. 작업예약 등록 [연속 북인시]
					//------------------------------------------------------------------------------------------------
					szYdWbookId = ydWrkbookDao.getSeqId();						//작업예약ID 생성

					szMsg    = "["+szOperationName+"] ----------- 작업예약ID 생성 :: " + ii + " >>>> " + szYdWbookId;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					if ("".equals(szYdWbookId)) {
						szRtnMsg = "작업예약ID 생성 ERROR";
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					szMsg    = "["+szOperationName+"] ----------- 작업예약 등록";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					szYdToLocGuide = szToLoc;

					//작업예약 등록
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID", 		szYdWbookId); 		//야드작업예약ID
					recPara.setField("YD_GP", 				szYdGp); 			//야드구분
					recPara.setField("YD_BAY_GP", 			szYdBayGp); 		//야드동구분
					recPara.setField("YD_SCH_CD", 			szYdSchCd); 		//야드스케쥴코드
					recPara.setField("YD_SCH_PRIOR", 		szYdWrkCrnPrior); 	//야드스케쥴우선순위
					recPara.setField("YD_SCH_PROG_STAT", 	"W"); 				//야드스케쥴진행상태(스케줄수행대기)
					recPara.setField("YD_SCH_ST_GP", 		"M"); 				//야드스케쥴기동구분
					recPara.setField("YD_SCH_REQ_GP", 		"X"); 				//야드스케쥴요청구분
					recPara.setField("YD_AIM_YD_GP", 		szYdGp); 			//야드목표야드구분
					recPara.setField("YD_AIM_BAY_GP", 		szYdBayGp); 		//야드목표동구분
					recPara.setField("YD_TO_LOC_DCSN_MTD",	"A"); 				//야드TO위치결정방법(스케줄기준적용)
					recPara.setField("YD_TO_LOC_GUIDE",		szYdToLocGuide);	//야드To위치Guide
					recPara.setField("REGISTER", 			szRegister);
					recPara.setField("MODIFIER", 			szRegister);

					intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
					if (intRtnVal <= 0) {
						szRtnMsg = "작업예약 등록 ERROR .. " + Integer.toString(intRtnVal);
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					//작업예약재료 등록
					recPara.setField("YD_WBOOK_ID", 	szYdWbookId); 											//야드작업예약ID
					recPara.setField("STL_NO", 			ydDaoUtils.paraRecChkNull(tempRec, "STL_NO")); 			//재료번호
					recPara.setField("YD_STK_COL_GP", 	ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_COL_GP"));	//야드적치열구분
					recPara.setField("YD_STK_BED_NO", 	ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_BED_NO"));	//야드적치베드번호
					recPara.setField("YD_STK_LYR_NO", 	ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_LYR_NO")); 	//야드적치단번호
					recPara.setField("YD_TAKE_OUT_DT", 	szOperationDate);										//BOOK-OUT일시
					recPara.setField("YD_TAKE_OUT_CD", 	szReasonCode);											//BOOK-OUT원인코드

					szRtnMsg = this.insWrkbookMtl(recPara);

			        if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
						szMsg    = "["+szOperationName+"] 작업예약재료 등록 ERROR .. " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
			        }

					if (iWBookInsCnt > 0) {
						sbARR_WBOOK_ID.append(";");
					}
					sbARR_WBOOK_ID.append(szYdWbookId);
					iWBookInsCnt ++;
		    	}

			} else {

				//------------------------------------------------------------------------------------------------
				// 4. 작업예약 등록 [북아웃 또는 1매 북인]
				//------------------------------------------------------------------------------------------------
				szYdWbookId = ydWrkbookDao.getSeqId();						//작업예약ID 생성

				szMsg    = "["+szOperationName+"] ----------- 작업예약ID 생성 :: " + szYdWbookId;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				if ("".equals(szYdWbookId)) {
					szRtnMsg = "작업예약ID 생성 ERROR";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				szMsg    = "["+szOperationName+"] ----------- 작업예약 등록";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				szYdToLocGuide = szToLoc;

				//작업예약 등록
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID", 		szYdWbookId); 		//야드작업예약ID
				recPara.setField("YD_GP", 				szYdGp); 			//야드구분
				recPara.setField("YD_BAY_GP", 			szYdBayGp); 		//야드동구분
				recPara.setField("YD_SCH_CD", 			szYdSchCd); 		//야드스케쥴코드
				recPara.setField("YD_SCH_PRIOR", 		szYdWrkCrnPrior); 	//야드스케쥴우선순위
				recPara.setField("YD_SCH_PROG_STAT", 	"W"); 				//야드스케쥴진행상태(스케줄수행대기)
				recPara.setField("YD_SCH_ST_GP", 		"M"); 				//야드스케쥴기동구분
				recPara.setField("YD_SCH_REQ_GP", 		"X"); 				//야드스케쥴요청구분
				recPara.setField("YD_AIM_YD_GP", 		szYdGp); 			//야드목표야드구분
				recPara.setField("YD_AIM_BAY_GP", 		szYdBayGp); 		//야드목표동구분
				recPara.setField("YD_TO_LOC_DCSN_MTD",	"A"); 				//야드TO위치결정방법(스케줄기준적용)
				recPara.setField("YD_TO_LOC_GUIDE",		szYdToLocGuide);	//야드To위치Guide
				recPara.setField("REGISTER", 			szRegister);
				recPara.setField("MODIFIER", 			szRegister);
				if("2".equals(szOperationType)) {
					recPara.setField("YD_WRK_PLAN_TCAR", 		szYdNo); //TO위치
					
					if(iPlateCnt == 1 && "Y".equals(szPILNG_WRK_GP)) {
						recPara.setField("TRN_EQP_CD",	"P"); //00010 존 BOOK-OUT 시 파일링 대상으로 지상국에 표시
					}
				}

				if("PCCB01MM".equals(szYdSchCd) || "PCRT10LM".equals(szYdSchCd)) {
					//C동 극후물대 AS롤재 BOOK-OUT(33010) - PCCB01MM
					//C동 극후물대 TM재 BOOK-OUT(33010) - PCRT10LM
					//33010 에서 BOOK-OUT 되는 경우 TO위치를 XX010101 로 설정
					recPara.setField("YD_TO_LOC_GUIDE"	,"PC");	//야드To위치Guide
					recPara.setField("YD_WRK_PLAN_TCAR"	,"");   //TO위치 
					
					szMsg    = "["+szOperationName+"] ----------- 야드To위치Guide : PC 로 설정";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);					
				}
				
				intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
				if (intRtnVal <= 0) {
					szRtnMsg = "작업예약 등록 ERROR .. " + Integer.toString(intRtnVal);
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}


				int MaxYdStkLyrNo=0;				//(신규개발)
				for(int ii=0; ii < iPlateCnt; ii++) {
					//작업예약재료 등록
					recPara.setField("YD_WBOOK_ID", 	szYdWbookId); 		//야드작업예약ID
					recPara.setField("STL_NO", 			szPlateId[ii]); 		//재료번호
					recPara.setField("YD_STK_COL_GP", 	szFromLoc);			//야드적치열구분
					recPara.setField("YD_STK_BED_NO", 	szOperationBed);	//야드적치베드번호
					recPara.setField("YD_STK_LYR_NO", 	szYdStkLyrNo[ii]); 		//야드적치단번호
					recPara.setField("YD_TAKE_OUT_DT", 	szOperationDate);	//BOOK-OUT일시
					recPara.setField("YD_TAKE_OUT_CD", 	szReasonCode);		//BOOK-OUT원인코드
	
					szRtnMsg = this.insWrkbookMtl(recPara);

					if(MaxYdStkLyrNo<Integer.parseInt(szYdStkLyrNo[ii])){
						MaxYdStkLyrNo=Integer.parseInt(szYdStkLyrNo[ii]);    //쌓인 적치단 번호 중 최대값 저장
					}
					
			        if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
						szMsg    = "["+szOperationName+"] 작업예약재료 등록 ERROR .. " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
			        }
				}

				//(신규개발)
				//if("PILLING".equals(szDEV_INPUT1)){  
				if(isPillingModBookIn || isPillingModBookOut){	//(파일링 모드 전용)삭제한 이전 작업재료 복구(북아웃/북인 공통)
					for(int ii=0; ii < iDelStlNo; ii++) {  //파일링 시, 삭제한 재료를 다시 작업예약재료등록						
						recPara.setField("YD_WBOOK_ID", 	szYdWbookId); 		//야드작업예약ID
						recPara.setField("STL_NO", 			szPillingDelStlNo[ii]); 		// 클린한 재료번호
						recPara.setField("YD_STK_COL_GP", 	szFromLoc);			//야드적치열구분
						recPara.setField("YD_STK_BED_NO", 	szOperationBed);	//야드적치베드번호
						recPara.setField("YD_STK_LYR_NO", 	szYdStkLyrNo[MaxYdStkLyrNo+ii]); 		//야드적치단번호: 신규 재료 적치단 max+기존 적치단 번호
						recPara.setField("YD_TAKE_OUT_DT", 	szOperationDate);	//BOOK-OUT일시
						recPara.setField("YD_TAKE_OUT_CD", 	szReasonCode);		//BOOK-OUT원인코드				
						
						szRtnMsg = this.insWrkbookMtl(recPara);
						
				        if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
							szMsg    = "["+szOperationName+"] 작업예약재료 등록 ERROR .. " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
				        }
					}		
				}
				
				if (iWBookInsCnt > 0) {
					sbARR_WBOOK_ID.append(";");
				}
				sbARR_WBOOK_ID.append(szYdWbookId);
				iWBookInsCnt ++;

			}

			//------------------------------------------------------------------------------------------------
			// 연속 BOOK-IN/OUT일때 가이던스 메시지 전송
			//------------------------------------------------------------------------------------------------
			if ("2".equals(szOperationMode) || "3".equals(szOperationMode)) {					// 1:one time 2:Start 3:End

				szMsg = "[" +szOperationName + "] 야드L2 가이던스메시지 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				
				recL2Para = JDTORecordFactory.getInstance().create();
				recL2Para.setField("JMS_TC_CD", 			"YDY2L006");                            // TC-CODE
				recL2Para.setField("YD_GP", 				JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
				recL2Para.setField("OPERATION_TYPE", 		szOperationType);    					// 1:Book In, 2:Book Out
				if ("2".equals(szOperationMode)) {
					recL2Para.setField("OPERATION_MODE", 	"1");									// 1:Start
				} else {
					recL2Para.setField("OPERATION_MODE", 	"2");									// 2:End
				}
				
				if ("1".equals(szOperationType)) {			// 1:Book-in 일때
					recL2Para.setField("YD_BAY_GP", 		ydUtils.substr(szToLoc, 1, 1));         // 야드동구분
					recL2Para.setField("OPERATION_SOURCE", 	szToLoc);	        					// Book-In일때  TO위치
				} else {
					recL2Para.setField("YD_BAY_GP", 		ydUtils.substr(szFromLoc, 1, 1));       // 야드동구분
					recL2Para.setField("OPERATION_SOURCE", 	szFromLoc);	        					// Book-Out일때 From위치
				}

				szRtnMsg = ydDelegate.sendMsg(recL2Para);

				szMsg = "[ " +szOperationName + "] 야드L2 가이던스메시지 전문송신 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

			iCrnSchCnt  = 0;
			iSchOkCnt	= 0;
			if (iWBookInsCnt > 0) {

				String[] arrWBookId = sbARR_WBOOK_ID.toString().split(";");

				szMsg = "["+szOperationName+"] ----------- 3.1. BOOK-IN/OUT 스케줄기동 START .... 예약건수 :: " + iWBookInsCnt + " 작업예약ID :: " + sbARR_WBOOK_ID.toString();
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				for(int ii=0; ii<iWBookInsCnt; ii++) {

					if(!"PBRT40LM".equals(szYdSchCd) && !"PBRT10LM".equals(szYdSchCd)
							&& !"PCRT30LM".equals(szYdSchCd)
							&& !"PCRT40LM".equals(szYdSchCd)
							&& !"PCCB01MM".equals(szYdSchCd) && !"PCRT10LM".equals(szYdSchCd)) {
						//B동 SB 입측 BOOK-OUT(00004) - PBRT40LM
						//B동 열처리 BOOK-OUT(00010) - PBRT10LM
						//C동 #1PRESS 출측 BOOK-OUT(20000) - PCRT30LM
						//C동C동 WB 출측 BOOK-OUT(00012) - PCRT40LM
						//C동 극후물대 AS롤재 BOOK-OUT(33010) - PCCB01MM
						//C동 극후물대 TM재 BOOK-OUT(33010) - PCRT10LM
						//위 스케줄은 요청하는 대로 크레인 스케줄이 생성되고
						//그 외의 스케줄은 6건의 크레인스케줄만 존재하도록 한다.
						
		    			//-----------------------------------------------------
		        		// 스케쥴 코드로 크레인작업지시를 조회하여 6건 이하시 스케쥴 기동
		    			//-----------------------------------------------------
		        		rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
		        		recPara  = JDTORecordFactory.getInstance().create();
		        		recPara.setField("YD_SCH_CD", 		szYdSchCd);
	
		        		iCrnSchCnt = ydCrnSchDao.getByYdSchCd(recPara, rsResult);
		    			if (iCrnSchCnt >= JPlateYdConst.MAX_CRN_SCH_CNT) {
		        			szMsg = "["+szOperationName+"] ----------- BOOK-IN/OUT 스케줄 기동 SKIP :: " + ii + " .. 스케쥴 건수 .. " + iCrnSchCnt;
		        			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		    				break;
		    			}
					}
 
					//------------------------------------------------------------------------------------------------
					// 스케쥴 기동 (BOOK-OUT)
					//------------------------------------------------------------------------------------------------
					recSchPara 	= JDTORecordFactory.getInstance().create();
					recSchPara.setField("MSG_ID", 			"YDYDJ");			//TC코드
					recSchPara.setField("YD_EQP_ID", 		szYdWrkCrn);		//크레인설비ID
					recSchPara.setField("YD_SCH_CD",		szYdSchCd);			//크레인스케줄코드
					recSchPara.setField("YD_WBOOK_ID",		arrWBookId[ii]);	//작업예약ID
					recSchPara.setField("REGISTER", 		szRegister);
					recSchPara.setField("MODIFIER", 		szRegister);
					recSchPara.setField("YD_TO_LOC_GUIDE",	szYdToLocGuide);	//야드To위치Guide
					recSchPara.setField("CHK_FROM_LOC",		"N");				//RT작업일경우 권상예약 체크 안하도록 보완
					
					
					if("PCCB01MM".equals(szYdSchCd) || "PCRT10LM".equals(szYdSchCd)) {
						//C동 극후물대 AS롤재 BOOK-OUT(33010) - PCCB01MM
						//C동 극후물대 TM재 BOOK-OUT(33010) - PCRT10LM
						//33010 에서 BOOK-OUT 되는 경우 TO위치를 XX010101 로 설정
						recSchPara.setField("YD_TO_LOC_GUIDE",	"XX010101");	//야드To위치Guide
						
						szMsg    = "["+szOperationName+"] ----------- 야드To위치Guide : XX010101 로 설정";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);					
						
					}
                   //이 전에 야드 재료 두께(TB_YD_SHRSTOCK: YD_MTL_T 값 원복됨....Tracking 필요...
					szMsg    = "["+szOperationName+"] ----------- 3.2.BOOK-IN/OUT 스케줄기동 START :: " + ii + " >>>> " + arrWBookId[ii];
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			        EJBConnector ejbConn = new EJBConnector("default", "JPlateYdCrnSchYdPSeEJB", this);
			        szRtnMsg = (String)ejbConn.trx("procCrnSchMainYdP", new Class[] { JDTORecord.class }, new Object[] { recSchPara });

					szMsg    = "["+szOperationName+"] ----------- 3.3.BOOK-IN/OUT 스케줄기동 END :: " + + ii + " >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			        if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
			        	// 크레인 작업지시 전송후에는 오류발생 데이타 SKIP
			        	if (iSchOkCnt > 0) {
							szMsg    = "["+szOperationName+"] BOOK-IN/OUT 스케줄기동시 오류 발생 .... SKIP >>>> " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			        	} else {
				        	szRtnMsg = "BOOK_IN/OUT 스케줄기동 오류 .. <br>" + szRtnMsg;
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
			        	}
			        } else {
			        	iSchOkCnt ++;
			        }
				}
			}
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }

		return JPlateYdConst.RETN_CD_SUCCESS;
	}
	
    /**
     * 오퍼레이션명 : RT상에서 파일링실적 수신 (Y2YDL013) --S/B 입측 파일링 ==> RT상에서 권상 후 다음재료와 파일링 권상 
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● Stirng pYdCrnSchId, String pYdWrkProgStat
     * @return ● nRtnVal
     * @throws ● DAOException
     */
	public String procY2PilingRsltR(JDTORecord msgRecord) throws DAOException {
	
    	JPlateYdCommDAO 		commDao 		= new JPlateYdCommDAO();
    	
// 2024.11.21 신규 logId 사용
//    	String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
    	
		// 변수 선언
        String 	szMethodName  	= "procY2PilingRsltR";
        String	szRtnMsg		= "";
    	String 	szMsg         	= "";
    	String 	szOperationName = "RT상에서 파일링실적 수신";
		
		String 	szRcvTcCode		= ydUtils.getTcCode(msgRecord);
    	
    	String	szYdEqpId		= "";
    	String	szYdPilingSh 	= "";					// 파일링매수
		String	szYdPilingGp	= "";					// 파일링구분 (P:파일링, H:횡행작업, M:멀티작업)
		String	szModifier		= "";
		String	szYdEqpStat		= "";

		String	szYdSchCd		= "";
		String 	szYdUpWoLayer	= "";					// 야드권상지시단
		String  szYdCrnSchId	= "";
		String  szStlNo			= "";
		String  szYdUpWoLoc		= "";
		String  szYdStkColGp	= "";
		String  szYdStkSpanGp	= "";
		String  szYdUpWoBed		= "";
	
    	String 	szOldYdCrnSchId	= "";					// 스케쥴ID Old
    	String 	szSendMsg		= "";
    	
    	String 	arrSchId[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 스케쥴코드
    	String 	arrWbookId[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 기존작업예약ID
    	String 	arrStlNo[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 재료번호
    	String 	arrYdLoc[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 재료적치열+재료적치베드
    	String 	arrLayer[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 재료적치단
		
    	double	dArrWrkT[]		= {0,0,0,0,0};			// 야드설비작업총두께	[적치단]
		
    	int		iYdPilingSh		= 0;
    	boolean	bLocCheck		= true;
    	
    	int		iBedIdx 		= 0;		// 적치베드 	Index (0~2)
    	int		iLyrIdx 		= 0;		// 적치단 	Index (0~4)
    	
    	int		iSumWrkWt		= 0;		// 중량합
    	double  dMtlT			= 0;		// 두께 저장용 변수
    	double	dSumWrkT		= 0;		// 두께합
    	
		JDTORecordSet rsResult  = null;
		JDTORecord recPara      = JDTORecordFactory.getInstance().create();
		JDTORecord recSch 		= JDTORecordFactory.getInstance().create();
		JDTORecord recMtl 		= JDTORecordFactory.getInstance().create();
		JDTORecord recL2Msg		= JDTORecordFactory.getInstance().create();
        JDTORecord recInTemp    = JDTORecordFactory.getInstance().create();
		
        String	 szStlNoList	= "";      
        String[] szArrStlNo		= null;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 로그 개선 
              
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 		// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
            		
//-------------------------------------------------------------------------------------------------------------------------
        
        try {
        	szMsg = "[" + szOperationName + "] 파일링 실적 .. 메소드 시작 >>>>" + msgRecord.toString();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		
			if ("".equals(szRcvTcCode)) {
				szRcvTcCode = "Y2YDL013";
			}
			
			szYdEqpId		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"			);
			szYdPilingSh 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_PILING_SH"		);
			szYdPilingGp	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_PILING_GP"		);	// 파일링구분 (P:파일링, H:횡행작업, M:멀티작업)
			szModifier		= ydDaoUtils.paraRecModifier(msgRecord						);
			iYdPilingSh		= ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_PILING_SH"	);
			
			if(!"R".equals(szYdPilingGp)) {
				szRtnMsg = "파일링구분(YD_PILING_GP)이 'R'이 아닙니다!";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				return szRtnMsg;			
			}
			
			//------------------------------------------------------------------------------------------------
			// 설비상태  체크
			//------------------------------------------------------------------------------------------------
        	recPara.setField("YD_EQP_ID", 	szYdEqpId);
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.getYdEqp", logId, szMethodName, "설비상태  체크");

			if(rsResult.size() > 0) {
				szYdEqpStat =  StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_EQP_STAT"),"");
			}
			
			if (!JPlateYdConst.YD_EQP_STAT_UP_CMPL.equals(szYdEqpStat) && 			// 권상완료(2)
				!JPlateYdConst.YD_EQP_STAT_DN_WO.equals(szYdEqpStat)) {				// 권하지시(3)

				szRtnMsg = "권상후 RT상의 파일링 가능.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				return szRtnMsg;
	        }
			
			//------------------------------------------------------------------------------------------------
			// 파라미터 체크
			//------------------------------------------------------------------------------------------------
			if (iYdPilingSh < 1 || iYdPilingSh > 5) {
				szRtnMsg = "파일링 매수 오류 :: " + szYdPilingSh;
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}
			
        	String szFstLyrNo 	= "";       // 최하단 
        	String szLastLyrNo 	= "";

        	// 파일링 실적중 최하단 구하기
			for(int ii=0; ii<iYdPilingSh; ii++) {
				szYdUpWoLayer 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WO_LAYER" + Integer.toString(ii+1));	// 권상지시단
				if (ii==0) {
					szFstLyrNo	= szYdUpWoLayer;
					szLastLyrNo = szYdUpWoLayer;
				}
				if (!"".equals(szYdUpWoLayer) && !"".equals(szFstLyrNo)) {
					if (Integer.parseInt(szYdUpWoLayer) < Integer.parseInt(szFstLyrNo)) {
						szFstLyrNo	= szYdUpWoLayer;
					}
				}
				if (!"".equals(szYdUpWoLayer) && !"".equals(szLastLyrNo)) {
					if (Integer.parseInt(szYdUpWoLayer) > Integer.parseInt(szLastLyrNo)) {
						szLastLyrNo	= szYdUpWoLayer;
					}
				}
			}
			
			if ("R".equals(szYdPilingGp)) {		// RT상의 파일링작업
				if (szFstLyrNo.equals(szLastLyrNo)){
					szRtnMsg = "RT상의  파일링 작업인데 권상지시 단이 동일 함 :: " + szLastLyrNo;
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
			}
			
			// 야드크레인스케쥴ID, 재료번호, 권상지시위치, 권상지시단 체크
			for(int ii=0; ii<iYdPilingSh; ii++) {
				szYdCrnSchId  	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID" + Integer.toString(ii+1)		);	// 야드크레인스케쥴ID
				szStlNo		  	= ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO" + Integer.toString(ii+1)			);	// 재료번호
				szYdUpWoLoc 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WO_LOC" + Integer.toString(ii+1)		);	// 권상지시위치
				szYdUpWoLayer 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WO_LAYER" + Integer.toString(ii+1)	);	// 권상지시단
        		szYdStkColGp  	= ydUtils.substr(szYdUpWoLoc, 0, 6													);	// 권상위치구분
				szYdStkSpanGp	= ydUtils.substr(szYdUpWoLoc, 2, 2													);	// 권상위치 스판구분
				szYdUpWoBed		= ydUtils.substr(szYdUpWoLoc, 6, 2													);	// 권상위치 베드 번호
				
				// 스케쥴 ID 체크
				if ("".equals(szYdCrnSchId)) {
					szRtnMsg = "스케쥴 ID 오류 :: " + szYdCrnSchId;
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}

				// 재료번호 체크
				if ("".equals(szStlNo)) {
					szRtnMsg = "재료번호 오류 :: " + szStlNo;
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
				
				//파일링구분이 'R'인 경우 권상위치는 RT 이어야 한다.
				if (!"RT".equals(szYdStkSpanGp)) {
					szRtnMsg = "설비구분 오류 :: " + szYdStkSpanGp;
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
				
				// 권상지시위치 체크
				bLocCheck = true;
				if ("".equals(szYdUpWoLoc)) {
					bLocCheck = false;
				} else {
		        	recPara.setField("YD_STK_COL_GP", 		szYdStkColGp);
		        	recPara.setField("YD_STK_BED_NO", 		szYdUpWoBed);
					rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYdStkbed", logId, szMethodName, "권상지시위치 체크");
		        	
		        	if (rsResult.size() <= 0) {
		        		bLocCheck = false;
		        	}
				}

				if (!bLocCheck) {
					szRtnMsg = "권상위치 오류 :: " + szYdUpWoLoc;
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
				
				// 권상지시단 체크
				if ("".equals(szYdUpWoLayer)) {
					szRtnMsg = "권상위치단 오류 :: " + szYdUpWoLayer;
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
			
				
				if ("".equals(szOldYdCrnSchId)) {
					szOldYdCrnSchId = szYdCrnSchId; //첫번째 권상한 재료의 크레인스케줄ID
				}

				// array 변수 셋팅을 위한 index 설정
				// RT일경우 열로 Bed Index 가져온다
                if ("RT".equals(ydUtils.substr(szYdUpWoLoc, 2, 2))) {
		        	//iBedIdx = this.getBedIdx(arrYdLoc, szYdUpWoLoc);
		        	//iLyrIdx = Integer.parseInt(szYdUpWoLayer) - 1;										// 적치단 	Index (0~5)
                	iBedIdx = 0;
                	iLyrIdx = ii;
                } else {
		        	iBedIdx = Integer.parseInt(szYdUpWoBed)   - 1;										// 적치베드 	Index (0~2)
		        	iLyrIdx = Integer.parseInt(szYdUpWoLayer) - 1;										// 적치단 	Index (0~5)
                }
				
				if ((iBedIdx >= 0 && iBedIdx < 3) && (iLyrIdx >= 0 && iLyrIdx < 5)) {

			    	if ("".equals(arrLayer[iLyrIdx][iBedIdx])) {
					    arrSchId[iLyrIdx][iBedIdx] = szYdCrnSchId;		// 스케쥴코드
				    	arrStlNo[iLyrIdx][iBedIdx] = szStlNo;			// 재료번호
				    	arrYdLoc[iLyrIdx][iBedIdx] = szYdUpWoLoc;		// 재료적치열
				    	arrLayer[iLyrIdx][iBedIdx] = szYdUpWoLayer;		// 재료적치단
			    	} else {
						szRtnMsg = "array 변수 셋팅시 충돌 error :: " + (szYdUpWoLoc + "-" + szYdUpWoLayer + " iBedIdx: " + iBedIdx + " ,iLyrIdx: " + iLyrIdx);
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
			    	}

				} else {
					szRtnMsg = "array 변수 셋팅시 Index ERROR :: " + (szYdUpWoLoc + "-" + szYdUpWoLayer + " iBedIdx: " + iBedIdx + " ,iLyrIdx: " + iLyrIdx);
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
				
				//------------------------------------------------------------------------------------------------
	    		// 기존 크레인작업지시 조회 --> 작업예약 ID SET , 최상단 크레인사업지시 저장
				//------------------------------------------------------------------------------------------------
				recPara.setField("YD_CRN_SCH_ID", 		szYdCrnSchId);
				rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnSch", logId, szMethodName, "기존 크레인작업지시 조회 --> 작업예약 ID SET");
	        	
	        	if (rsResult.size() <= 0) {
					szRtnMsg = "기존 크레인작업지시 조회 오류 :: YD_CRN_SCH_ID : " + szYdCrnSchId;
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
	        	}
	        	
	        	// 스케쥴 코드 체크
	        	szYdSchCd 	= StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_SCH_CD"),"");
	    		
	        	arrWbookId[iLyrIdx][iBedIdx] = StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"),"");
	        	if(ii == 0) {
	        		recSch.setRecord(rsResult.getRecord(0)); //최상단 크레인 작업지시 저장
	        	}
				
	        	//------------------------------------------------------------------------------------------------
				// 재료정보 조회
				//------------------------------------------------------------------------------------------------
				recPara.setField("STL_NO", szStlNo);
	        	
				rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStock", logId, szMethodName, "재료정보 조회");
	        	
	        	if (rsResult.size() <= 0) {
					szRtnMsg = "재료정보 조회 오류 .. 재료번호::" + szStlNo;
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
	        	}
				
	        	//중량합계
	        	iSumWrkWt = iSumWrkWt + Integer.parseInt(StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_MTL_WT"),"0"));
	        	//두께 array변수에 저장
	        	dMtlT	  = Double.parseDouble(StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_MTL_T"),"0"));
	        	if(dMtlT > dArrWrkT[iLyrIdx]) {
	        		dArrWrkT[iLyrIdx] = dMtlT;
	        	}
	        	
			}
		
			for(int kk=0; kk<5; kk++) {
				dSumWrkT = dSumWrkT + dArrWrkT[kk];
			}
				
			szMsg = "[" + szOperationName + "] >>>> RT상의 파일링 재료 정보 >>>> 두께 : " + dSumWrkT + ", 중량 : " + iSumWrkWt;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			// 재료두께 50T 넘는 재료는 파일링 불가 [파일링,멀티작업 일때만 체크] + 파일링 50t 기준 해제 허용 경우 추가
			if (!"H".equals(szYdPilingGp)){
				
				/*	1. 파일링 50t 기준 해제 허용 크레인권상위치--- PBRT4N01(쇼트입측), PART1301(체인트렌스퍼출측)
				 *	2. 파일링 50t 기준 해제 허용 크레인번호------ 열처리 크레인 PM44,PM20  강력교정 크레인  PM55,PM54
				 *	3. 파일링 50t 기준 해제 허용 스케줄코드------ PBRT40LM , PART13LM
				 *	4. 변경 파일링 기준 ( 50 -> 170 ) ------ 강력교정 최대두께 80t 2매 + α) ,  상기위치외 파일링기준 현재와 동일
				 *	5. 파일링제한 해제 대상 : 파일링작업시  
				 */
				if (	("PBRT4N01".equals(szYdUpWoLoc) || "PART1301".equals(szYdUpWoLoc))
					&&  ("PACRA1".equals(szYdEqpId)     || "PACRA2".equals(szYdEqpId) 		|| "PBCRB1".equals(szYdEqpId) || "PBCRB2".equals(szYdEqpId))
					&&  ("PBRT40LM".equals(szYdSchCd)   || "PART13LM".equals(szYdSchCd))
					){
					if(dSumWrkT > 170){
						szRtnMsg =		"권상위치 : " + szYdUpWoLoc
									+ ", 크레인 : "  + szYdEqpId
									+ ", 스케줄코드 : "  + szYdSchCd
									+ ", 재료두께 합이 170t 넘어 파일링작업 불가! [" + dSumWrkT + "]";
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;	
					}
				} else if(dSumWrkT > 50) {
					szRtnMsg = "재료두께 합이 50t 넘어 파일링작업 불가! [" + dSumWrkT + "]";
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
				
			}
			
			if("PECRE2".equals(szYdEqpId)) {
				// 35톤 넘는 재료는 불가
				if (iSumWrkWt > 35000) {
					szRtnMsg = "재료중량 합이 35톤 넘어 작업 불가! [" + iSumWrkWt + "]";
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
			} 
			else if("PFCRF2".equals(szYdEqpId)){  //21호기(PFCRF2) 제한 중량 기준 변경 25T->30T 2021.12.06 박종호. 허남웅 책임 요청사항.
				// 30톤 넘는 재료는 불가
				if (iSumWrkWt > 30000) {
					szRtnMsg = "재료중량 합이 30톤 넘어 작업 불가! [" + iSumWrkWt + "]";
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
			}
			else {
				// 25톤 넘는 재료는 불가
				if (iSumWrkWt > 25000) {
					szRtnMsg = "재료중량 합이 25톤 넘어 작업 불가! [" + iSumWrkWt + "]";
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
			}
			
			//----------------------------------------------------------------
			// 1)최상단 작업예약재료 조회
			// 2)권상 중인 작업예약재료 단 수정
			// 3)하단 파일링 할 작업예작재료 추가
			// 4)하단 파일링 할 기존 작업예약 재료 삭제
			// 5)하단 파일링 할 기존 작업예약 삭제
			// 6)저장품에 작업예약ID를 최상단 작업예약ID로 변경
			//----------------------------------------------------------------

			// 1)최상단 작업예약재료 조회
			recPara.setField("YD_WBOOK_ID"	, arrWbookId[0][0]);
			recPara.setField("STL_NO"		, arrStlNo[0][0]);
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getWrkBookMtlByIdStlNo", logId, szMethodName, "최상단 작업예약재료 조회");
			
        	if (rsResult.size() <= 0) {
				szRtnMsg = "기존 작업예약재료 조회 .. 재료번호 ::" + arrStlNo[0][0] + " >> 작업예약ID ::" + arrWbookId[0][0];
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
        	}
			
        	recMtl.setRecord(rsResult.getRecord(0));
        	
        	for(int ii=0 ; ii<5 ; ii++) {
        		if("".equals(arrStlNo[ii][0])) {
        			break;
        		}
        		if(arrWbookId[ii][0] == arrWbookId[0][0]) {
        			//권상 중인 작업재료
        			
        			// 2)권상 중인 작업예약재료 단 수정
        			recPara.setField("YD_WBOOK_ID"	 , arrWbookId[0][0]	);
        			recPara.setField("STL_NO"		 , arrStlNo[ii][0]	);
        			recPara.setField("YD_STK_LYR_NO" , arrLayer[ii][0]	);
        			recPara.setField("MODIFIER"		 , szModifier		);
        			commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.updYdStkLyrNo", logId, szMethodName, "권상 중인 작업예약재료 단 수정");
        			
        			
        		} else {
        			//하단 파일링할 작업재료
        			
        			// 3)하단 파일링 할 작업예작재료 추가
        			recMtl.setField("YD_WBOOK_ID"	    , arrWbookId[0][0]	);
        			recMtl.setField("STL_NO"		    , arrStlNo[ii][0]	);
        			recMtl.setField("YD_STK_LYR_NO"     , arrLayer[ii][0]	);
        			recMtl.setField("REGISTER"		    , szModifier		);
        			commDao.insert(recMtl, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.insYdWrkbookMtl", logId, szMethodName, "하단 파일링 할 작업예작재료 추가");
        			
        			// 4)하단 파일링 할 기존 작업예약 재료 삭제
        			recPara.setField("YD_WBOOK_ID"	    , arrWbookId[ii][0]	);
        			recPara.setField("MODIFIER"		    , szModifier		);
        			commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.deldWrkbookMtl", logId, szMethodName, "하단 파일링 할 기존 작업예약 재료 삭제");
        			
        			// 5)하단 파일링 할 기존 작업예약 삭제
        			recPara.setField("YD_WBOOK_ID"	    , arrWbookId[ii][0]	);
        			recPara.setField("YD_SCH_PROG_STAT" , ""				);
        			recPara.setField("MODIFIER"		    , szModifier		);
        			commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.delYdWrkbook", logId, szMethodName, "하단 파일링 할 기존 작업예약 삭제");
        			
        			// 6)저장품에 작업예약ID를 최상단 작업예약ID로 변경
        			recPara.setField("YD_WBOOK_ID"	    , arrWbookId[0][0]						);
        			recPara.setField("MODIFIER"		    , szModifier							);
        			recPara.setField("STL_NO"		    , arrStlNo[ii][0]						);
        			recPara.setField("YD_SCH_CD"	    , recSch.getFieldString("YD_SCH_CD")	);
        			commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdStockWbook", logId, szMethodName, "저장품에 작업예약ID를 최상단 작업예약ID로 변경");
        			
        		}
        	}

			//----------------------------------------------------------------
			// 1)최상단 크레인작업지시 작업수량 수정
        	// 2)최상단 크레인작업재료 조회
			// 3)권상 중인 크레인작업재료 단 수정
			// 4)하단 파일링 할 기준 크레인작업재료 추가
			// 5)하단 파일링 할 기존 크레인작업재료 삭제
			// 6)하단 파일링 할 기존 크레인작업지시 삭제
        	// 7)하단 파일링 할 기존 크레인작업지시 취소전문 전송
			//----------------------------------------------------------------
        	
			// 1)최상단 크레인작업지시 작업수량 수정
			recPara.setField("MODIFIER"		 		, szModifier							);
			recPara.setField("YD_CRN_SCH_ID"	 	, arrSchId[0][0]						);
			recPara.setField("YD_MAIN_WRK_MTL_SH"	, szYdPilingSh							);
			recPara.setField("YD_EQP_WRK_SH"		, szYdPilingSh							);
			recPara.setField("YD_EQP_WRK_WT"		, Integer.toString(iSumWrkWt)			);
			recPara.setField("YD_EQP_WRK_T"			, Double.toString(dSumWrkT)				);
			recPara.setField("YD_WRK_PROG_STAT"		, JPlateYdConst.YD_EQP_STAT_DN_WO		); // 3:권하지시
			commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updYdCrnSchWrkSh", logId, szMethodName, "최상단 크레인작업지시 작업수량 수정");

        	// 2)최상단 크레인작업재료 조회
			recPara.setField("YD_CRN_SCH_ID"	, arrSchId[0][0]							);
			recPara.setField("STL_NO"			, arrStlNo[0][0]							);
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getYdCrnWrkMtl", logId, szMethodName, "최상단 크레인작업재료 조회");
			
        	if (rsResult.size() <= 0) {
				szRtnMsg = "기존 크레인작업재료 조회 .. 재료번호 ::" + arrStlNo[0][0] + " >> 크레인스케줄ID ::" + arrSchId[0][0];
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
        	}

        	recMtl.setRecord(rsResult.getRecord(0));
        	
        	for(int ii=0 ; ii<5 ; ii++) {
        		if("".equals(arrStlNo[ii][0])) {
        			break;
        		}
        		if(arrSchId[ii][0] == arrSchId[0][0]) {
        			//권상 중인 작업재료

        			// 3)권상 중인 크레인작업재료 단 수정
        			recPara.setField("YD_CRN_SCH_ID" , arrSchId[0][0]	);
        			recPara.setField("STL_NO"		 , arrStlNo[ii][0]	);
        			recPara.setField("YD_STK_LYR_NO" , arrLayer[ii][0]	);
        			recPara.setField("MODIFIER"		 , szModifier		);
        			commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updCrnWrkMtlStkLyrNo", logId, szMethodName, "권상 중인 크레인작업재료 단 수정");
        			
        		} else {
        			//하단 파일링할 작업재료
        			
        			// 4)하단 파일링 할 기준 크레인작업재료 추가
		        	recMtl.setField("YD_CRN_SCH_ID",	arrSchId[0][0]	);			// 야드크레인스케쥴ID
        			recMtl.setField("STL_NO",			arrStlNo[ii][0]	);			// 재료번호
					recMtl.setField("MODIFIER",	        szModifier		);			// 수정자
					recMtl.setField("YD_STK_LYR_NO",	arrLayer[ii][0]	);      	// 야드적치단번호
					recMtl.setField("YD_STK_LOT_TP",	"01"			);			// 권상위치 BED 번호
					recMtl.setField("YD_STK_LOT_CD", 	arrYdLoc[ii][0]	);			// 권상위치
        			commDao.insert(recMtl, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.insYdCrnWrkMtl", logId, szMethodName, "하단 파일링 할 기준 크레인작업재료 추가");
        			
        			// 5)하단 파일링 할 기존 크레인작업재료 삭제
		        	recPara.setField("YD_CRN_SCH_ID", 	arrSchId[ii][0]	);
		        	recPara.setField("STL_NO", 			arrStlNo[ii][0]	);
					recPara.setField("MODIFIER",	    szModifier		);			// 수정자
					recPara.setField("DEL_YN",	    	"Y"				);			// 삭제여부
        			commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.delYdCrnWrkMtl", logId, szMethodName, "하단 파일링 할 기존 크레인작업재료 삭제");
        			
        			// 6)하단 파일링 할 기존 크레인작업지시 삭제
					recPara.setField("YD_CRN_SCH_ID",	arrSchId[ii][0]	);			// 야드크레인스케쥴ID
					recPara.setField("MODIFIER",	    szModifier		);			// 수정자
					recPara.setField("DEL_YN",	    	"Y"				);			// 삭제여부
        			commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.delYdCrnSch", logId, szMethodName, "하단 파일링 할 기존 크레인작업지시 삭제");
        			
        			// 7)하단 파일링 할 기존 크레인작업지시 취소전문 전송
			        recL2Msg.setField("MSG_ID", 					"YDY2L004"							);		// 크레인 작업지시
			        recL2Msg.setField("YD_CRN_SCH_ID",    			arrSchId[ii][0]						);
			        recL2Msg.setField("YD_WRK_PROG_STAT", 			JPlateYdConst.YD_EQP_STAT_UP_WO	);		// 크레인 작업상태
			        recL2Msg.setField("MSG_GP",           			"D"									);
			        
			        szSendMsg = ydDelegate.sendMsg(recL2Msg);
			        
			        // Book-Out Complete 전문 전송할 대상을 변수에 저장한다.
	    			if ("".equals(szStlNoList)) {
	    				szStlNoList = arrStlNo[ii][0];
	    			} else {
	    				szStlNoList = szStlNoList + ";" + arrStlNo[ii][0];
	    			}
        			
        		}
        	}
        		
			//------------------------------------------------------------------------------------------------
			// 설비상태 UPATE --> 크레인 상태를 권하지시로 SET
			//------------------------------------------------------------------------------------------------
			if (!JPlateYdConst.YD_EQP_STAT_DN_WO.equals(szYdEqpStat)) {				// 권하지시(3)
				recPara.setField("YD_EQP_ID", 		szYdEqpId	);
				recPara.setField("YD_EQP_STAT", 	"3"			);
				recPara.setField("MODIFIER", 		szModifier	);
				commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.updYdEqpStat", logId, szMethodName, "설비상태 UPATE --> 크레인 상태를 권하지시로 SET");
		    }
        	
			//------------------------------------------------------------------------------------------------
			// 권상예약,권하예약 위치 Clear, 현재료의 적치위치를 크레인으로 변경, 권하예약 위치 새로 지정
			//------------------------------------------------------------------------------------------------
        	for(int ii=0 ; ii<5 ; ii++) {
        		if("".equals(arrStlNo[ii][0])) {
        			break;
        		}
        		
        		// 1. 권상위치 CLEAR
				recPara.setField("MODIFIER",			szModifier								);
				recPara.setField("YD_STK_LYR_MTL_STAT",	"U"										);
				recPara.setField("STL_NO",				arrStlNo[ii][0]							);
				recPara.setField("YD_GP",				JPlateYdConst.YD_GP_P_PLATE_YARD		);
				commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrClearByStlNo", logId, szMethodName, "권상위치 CLEAR");
        		
				// 2. 크레인으로 저장위치 변경
				recPara.setField("YD_STK_COL_GP",       szYdEqpId								);
    			recPara.setField("YD_STK_BED_NO",       "01"									);
    			recPara.setField("YD_STK_LYR_NO",       arrLayer[ii][0]							);
				recPara.setField("YD_STK_LYR_MTL_STAT",	"C"										);
				recPara.setField("STL_NO",				arrStlNo[ii][0]							);
				recPara.setField("MODIFIER",			szModifier								);
				commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat", logId, szMethodName, "크레인으로 저장위치 변경");
				
                // 3. 권하위치 CLEAR
				recPara.setField("MODIFIER",			szModifier								);
				recPara.setField("YD_STK_LYR_MTL_STAT",	"D"										);
				recPara.setField("STL_NO",				arrStlNo[ii][0]							);
				recPara.setField("YD_GP",				JPlateYdConst.YD_GP_P_PLATE_YARD		);
				commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrClearByStlNo", logId, szMethodName, "권하위치 CLEAR");
				
				// 4. 권하위치 SET 
				//recPara.setField("YD_STK_COL_GP",       ydUtils.substr(recSch.getFieldString("YD_DN_WO_LOC"),0,6));
    			//recPara.setField("YD_STK_BED_NO",       "01");
    			//recPara.setField("YD_STK_LYR_NO",       arrLayer[ii][0]);
				//recPara.setField("YD_STK_LYR_MTL_STAT",	"D");
				//recPara.setField("STL_NO",				arrStlNo[ii][0]);
				//recPara.setField("MODIFIER",			szModifier);
				//commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat", logId, szMethodName, "권하위치 SET");
        	}
        	
        	
        	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        	String  szYdDnWoLoc = recSch.getFieldString("YD_DN_WO_LOC");
        	String	szYdStkBedNo 	= "";
        	String	szYdStkLyrNo	= "";
        	JPlateYdStkLyrDAO 		ydStkLyrDao		= new JPlateYdStkLyrDAO();
        	int iLyr = 0;

			//------------------------------------------------------------------------------------------------
			// 2.4. 파일링후 권하위치 SET
			//------------------------------------------------------------------------------------------------
			String szTopLyr = JPlateYdCommonUtils.getTopLyrNoByColGp(ydUtils.substr(szYdDnWoLoc,0,6), "01", "", szYdPilingGp, "");

			szMsg = "[" + szOperationName + "] >>>> 파일링 재료 권하위치 단 :: " + szTopLyr;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			
			//for(int ii=0; ii<5; ii++) {
			for(int ii=4; ii>-1; ii--) {
				//iLyr = Integer.parseInt(arrLayer[ii][0]) - 1;
				//szYdStkLyrNo = ydDaoUtils.stringPlusInt(szTopLyr, iLyr);			// 적치단을    1증가
				szYdStkBedNo = "00";

				for(int jj=0; jj<3; jj++) {
					szYdStkBedNo = ydDaoUtils.stringPlusInt2(szYdStkBedNo, 1);	// 적치베드를 1증가, 단은 그대로 놔둠
					if (!"".equals(arrStlNo[ii][jj])) {
						
						iLyr = Integer.parseInt(arrLayer[ii][0]) - 1;
						szYdStkLyrNo = ydDaoUtils.stringPlusInt(szTopLyr, iLyr);			// 적치단을    1증가
						
						
						// 2.4.1 저장위치 공베드 조회 (횡작업일때 베드 증가 , 파일링일때 적치단 증가)
						recPara.setField("YD_STK_COL_GP"	,ydUtils.substr(szYdDnWoLoc,0,6)	);
						recPara.setField("YD_STK_BED_NO"	,szYdStkBedNo						);
						//recPara.setField("YD_PILING_GP"		,szYdPilingGp);		// 파일링/횡작업 구분
						recPara.setField("YD_PILING_GP"		,"P"								);		// "R" --> "P"
						recPara.setField("YD_STK_LYR_NO"	,szYdStkLyrNo						);
						
						rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getPilingToLoc", logId, szMethodName, "저장위치 공베드 조회");

						if (rsResult.size() > 0) {

			    			szYdStkBedNo = StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_STK_BED_NO"),"");
			    			szYdStkLyrNo = StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_STK_LYR_NO"),"");

							szMsg = "[" + szOperationName + "] >>>> 파일링 재료  권하위치  >>>> " + szYdStkBedNo + "-" + szYdStkLyrNo;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			                // 2.4.2. 권하위치 SET
							recPara.setField("YD_STK_COL_GP",       ydUtils.substr(szYdDnWoLoc,0,6)	);
			    			recPara.setField("YD_STK_BED_NO",       szYdStkBedNo					);
			    			recPara.setField("YD_STK_LYR_NO",       szYdStkLyrNo					);
							recPara.setField("YD_STK_LYR_MTL_STAT",	"D"								);
							recPara.setField("STL_NO",				arrStlNo[ii][jj]				);
							recPara.setField("MODIFIER",			szModifier						);
			                //intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat", logId, szMethodName, "권하위치 SET"); 	//크레인 적치단의 재료정보 UPDATE
							ydStkLyrDao.updYdStklyrStat(recPara);  	//크레인 적치단의 재료정보 UPDATE

						} else {

							szMsg = "[" + szOperationName + "] >>>> 파일링 재료 권하위치 미존재로 SKIP >>>> ";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						}
					}
				}	// end of loop jj
			}	// end of loop ii


        	
        	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        	
        	
			//------------------------------------------------------------------------------------------------
			// Book-Out Complete 전문 전송
			//------------------------------------------------------------------------------------------------
			if ("R".equals(szYdPilingGp)) {		// RT상의 파일링작업
        	
				String szYdUpWrLoc = arrYdLoc[0][0];

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 #2 열처리 YDP8L501(Bookin/out complete) 송신 추가 
//			  야드스케쥴코드(YD_SCH_CD) BOOK-OUT - PART31LM, PART32LM, PART34LM, PART35LM
//-------------------------------------------------------------------------------------------------------------------------
	        	szMsg = "[" + szOperationName + "] 야드스케쥴코드 [" + szYdSchCd + "]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			
    	        if (  ydUtils.is2ndHeatBookOutSchdule(szYdSchCd, szYdUpWrLoc) ) {  
       	        	
        	        recInTemp.setField("MSG_ID", 			"YDP8L501"	);      // 1 후판 #2 열처리 L2
        	        recInTemp.setField("STL_NO",			""			);		// 재료번호
        	        recInTemp.setField("STL_NO_LIST",		szStlNoList	);		// 재료번호 LIST
        	        recInTemp.setField("OPERATION_TYPE",	"2"			);		// 1:Book In, 2:Book Out
        	        recInTemp.setField("YD_STK_COL_GP",		szYdUpWrLoc	);		// FROM위치
        	        recInTemp.setField("YD_EQP_ID", 		szYdEqpId	);		//야드설비ID
        	        
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recInTemp에 logId 추가 
        	        recInTemp.setField("LOG_ID", 			logId 									);      // logId
//-------------------------------------------------------------------------------------------------------------------------
        	        
       	        	szSendMsg = ydDelegate.sendMsg(recInTemp);
				    	        
    	        } else if(szYdUpWrLoc.startsWith("PBRT")||szYdUpWrLoc.startsWith("PART13")||szYdUpWrLoc.startsWith("PART9")||szYdUpWrLoc.startsWith("PCRT40") ) {
    	        	//열처리 L2
    	        	
    				String sNEW_MODULE_EFF_YN = "N";

    				JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
    				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A008"); //1후판정정야드 열처리L2 Book-In/Book-Out실적 신규모듈적용여부 
    				
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(YDP3L501)---[[[ 1후판정정야드신규적용 : " + sNEW_MODULE_EFF_YN + " ]]]---", JPlateYdConst.DEBUG, logId);
    	        	
    				if(sNEW_MODULE_EFF_YN.equals("Y")) {
    					//신규 메소드 호출
            	        recInTemp.setField("MSG_ID", 			"YDP3L501V2");      // 1 후판열처리L2
            	        recInTemp.setField("STL_NO",			""			);		// 재료번호
            	        recInTemp.setField("STL_NO_LIST",		szStlNoList	);		// 재료번호 LIST
            	        recInTemp.setField("OPERATION_TYPE",	"2"			);		// 1:Book In, 2:Book Out
            	        recInTemp.setField("YD_STK_COL_GP",		szYdUpWrLoc	);		// FROM위치
            	        recInTemp.setField("YD_EQP_ID", 		szYdEqpId	);		//야드설비ID
            	        
           	        	szSendMsg = ydDelegate.sendMsg(recInTemp);
    				} else {
    					//기존 메소드 호출
    					
    					szArrStlNo	= szStlNoList.split(";");
    					for (int ii=0; ii < szArrStlNo.length ; ii++) {
                	        recInTemp.setField("MSG_ID", 			"YDP3L501"		);      // 1 후판열처리L2
                	        recInTemp.setField("STL_NO",			szArrStlNo[ii]	);		// 재료번호
                	        recInTemp.setField("OPERATION_TYPE",	"2"				);		// 1:Book In, 2:Book Out
                	        recInTemp.setField("YD_STK_COL_GP",		szYdUpWrLoc		);		// FROM위치
                	        recInTemp.setField("YD_EQP_ID", 		szYdEqpId		);		//야드설비ID
               	        	szSendMsg = ydDelegate.sendMsg(recInTemp);
    					}
    				}

    		        szMsg = "[" +  szOperationName  + "] RT BOOK-OUT 실적 전송 .. 완료>>>>" + szSendMsg;
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

    	        } else {
    	        	//전단 L2
  
    				String sNEW_MODULE_EFF_YN = "N";

    				JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
    				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A007"); //1후판정정야드 전단L2 Book-In/Book-Out실적 신규모듈적용여부 
    				
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(YDP2L501)---[[[ 1후판정정야드신규적용 : " + sNEW_MODULE_EFF_YN + " ]]]---", JPlateYdConst.DEBUG, logId);
    	        	
    				if(sNEW_MODULE_EFF_YN.equals("Y")) {
    					//신규 메소드 호출
            	        //szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("2", szStlNoList, szYdUpWrLoc,szYdEqpId);
    					
						//2019.01.11 전단정정L2는 무조건 1매 단위로 완료실적을 보내달라는 요청
						szArrStlNo	= szStlNoList.split(";");
						for (int ii=0; ii < szArrStlNo.length ; ii++) {
							if (!"".equals(szArrStlNo[ii])) {

	                	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("2", szArrStlNo[ii], szYdUpWrLoc,szYdEqpId);
							}
						}
    					
    				} else {
    					//기존 메소드 호출
    					szArrStlNo	= szStlNoList.split(";");
    					for (int ii=0; ii < szArrStlNo.length ; ii++) {
	    					szStlNo = szArrStlNo[ii];
	            	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSend("2", szStlNo, szYdUpWrLoc,szYdEqpId);
    					}
    				}
    				

    		        szMsg = "[" +  szOperationName  + "] 1 후판전단L2 RT BOOK-OUT 실적 전송 .. 완료>>>>" + szSendMsg;
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
    	        }
        	
			}
        	
			//------------------------------------------------------------------------------------------------
			// 크레인 작업지시 전송 [신규 파일링 작업지시] - 권하작업지시로 Set
			//------------------------------------------------------------------------------------------------
			szMsg = "[" + szOperationName + "] >>>> 신규 작업지시 전문 전송 .. 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			
	        //recL2Msg.setField("MSG_ID",						"YDY2L004");						// 크레인 작업지시
	        recL2Msg.setField("MSG_ID",						"YDY2L004V2"						);	// 크레인 작업지시
	        recL2Msg.setField("YD_CRN_SCH_ID",    			arrSchId[0][0]						);
	        recL2Msg.setField("YD_WRK_PROG_STAT", 			JPlateYdConst.YD_EQP_STAT_DN_WO	);	// 권하지시
	        recL2Msg.setField("MSG_GP",           			"U"									);
        	
	        szSendMsg = ydDelegate.sendMsg(recL2Msg);
			
			szMsg = "[" + szOperationName + "] >>>> 신규 작업지시 전문 전송 .. 종료 :: " + szSendMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			szMsg = "[" + szOperationName + "] 메소드 끝 ";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }
			
		return JPlateYdConst.RETN_CD_SUCCESS;
	}  // end of procY2PilingRsltR
	
    /**
     * 오퍼레이션명 : 해당 크레인 파일링실적 수신 (Y2YDL013) - 신규 (RT상에서 파일링 + Book-Out Complete 전문)
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● Stirng pYdCrnSchId, String pYdWrkProgStat
     * @return ● nRtnVal
     * @throws ● DAOException
     */
	public String procY2PilingRslt2(JDTORecord msgRecord) throws DAOException {
	
    	JPlateYdCommDAO 		commDao 		= new JPlateYdCommDAO();
    	JPlateYdStkLyrDAO 		ydStkLyrDao		= new JPlateYdStkLyrDAO();
  
// 2024.11.21 신규 logId 사용    	
//    	String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
		
		// 변수 선언
        String 	szMethodName  	= "procY2PilingRslt2";
        String	szRtnMsg		= "";
    	String 	szMsg         	= "";
    	String 	szOperationName = "파일링실적(Y2YDL013) 수신 - 신규 ";
		
		String 	szRcvTcCode		= ydUtils.getTcCode(msgRecord);
    	
    	String	szYdEqpId		= "";
    	String	szYdPilingSh 	= "";					// 파일링매수
		String	szYdPilingGp	= "";					// 파일링구분 (P:파일링, H:횡행작업, M:멀티작업)
		String	szModifier		= "";
		String	szYdEqpStat		= "";
		String  szYdGp			= "";
		
		String 	szYdUpWoLayer	= "";					// 야드권상지시단
		String  szYdCrnSchId	= "";
		String  szStlNo			= "";
		String  szYdUpWoLoc		= "";
		String  szYdStkColGp	= "";
		String  szYdStkSpanGp	= "";
		String  szYdUpWoBed		= "";
		
    	String 	szOldYdCrnSchId	= "";					// 스케쥴ID Old
    	String 	szSendMsg		= "";
		
    	String	szYdAidWrkYn	= "";					// 보조작업여부
    	String	szSaveAidWrkYn	= "";					// 주작업+보조작업 으로 파일링 못하도록 체크하기위한 필드
    	
		String	szYdSchCd		= "";
    	String	szYdDnWoLoc 	= "";
		String	szYdWrkStat		= "";
    	String	szOldYdWbookId	= "";					// 작업예약ID Old
    	String	szNewYdWbookId	= "";					// 작업예약ID New
    	String 	szNewYdCrnSchId	= "";					// 스케쥴ID New

    	String	szYdStkBedNo 	= "";
    	String	szYdStkLyrNo	= "";
    	
    	
    	String 	arrSchId[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 스케쥴코드
    	String 	arrWbookId[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 기존작업예약ID
    	String 	arrStlNo[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 재료번호
    	String 	arrYdLoc[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 재료적치열+재료적치베드
    	String 	arrLayer[][]	= {{"","",""}, {"","",""}, {"","",""}, {"","",""}, {"","",""}};		// 재료적치단
		
    	double	dArrWrkT[]		= {0,0,0,0,0};			// 야드설비작업총두께	[적치단]
    	int		iArrWrkLen[]	= {0,0,0,0,0};			// 야드설비작업총길이	[적치베드]
    	
    	int		iYdPilingSh		= 0;
    	boolean	bLocCheck		= true;
		
    	int		iBedIdx 		= 0;		// 적치베드 	Index (0~2)
    	int		iLyrIdx 		= 0;		// 적치단 	Index (0~4)

    	int		iYdStkColL		= 0;
    	int		iSumWrkWt		= 0;		// 중량합
    	double  dMtlT			= 0;		// 두께 저장용 변수
    	double	dSumWrkT		= 0;		// 두께합
    	int		iMtlL			= 0;					// 재료의길이
    	int		iSumWrkLen		= 0;					// 야드설비작업총길이
    	
    	int 	intRtnVal 		= 0;
    	int		iWrkCnt			= 0;
    	int		iUpWrkCnt		= 0;
    	int		iDnWrkCnt		= 0;
    	
		JDTORecordSet rsResult  = null;
		JDTORecord recPara      = JDTORecordFactory.getInstance().create();
		JDTORecord recWbook		= JDTORecordFactory.getInstance().create();
		JDTORecord recSch 		= JDTORecordFactory.getInstance().create();
		JDTORecord recMtl 		= JDTORecordFactory.getInstance().create();
		JDTORecord recL2Msg		= JDTORecordFactory.getInstance().create();
        JDTORecord recInTemp    = JDTORecordFactory.getInstance().create();

        String	 szStlNoList	= "";      
        String[] szArrStlNo		= null;


//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 로그 개선 
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 		// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
        		
//-------------------------------------------------------------------------------------------------------------------------
        
        try {
        	szMsg = "[" + szOperationName + "] 파일링 실적 .. 메소드 시작 >>>>" + msgRecord.toString();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			if ("".equals(szRcvTcCode)) {
				szRcvTcCode = "Y2YDL013";
			}
		
			szYdEqpId		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"			);
			szYdPilingSh 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_PILING_SH"		);
			szYdPilingGp	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_PILING_GP"		);			// 파일링구분 (P:파일링, H:횡행작업, M:멀티작업)
			szModifier		= ydDaoUtils.paraRecModifier(msgRecord						);
			iYdPilingSh		= ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_PILING_SH"	);
			szYdGp = ydUtils.substr(szYdEqpId, 0, 1);

			//------------------------------------------------------------------------------------------------
			// 설비상태  체크
			//------------------------------------------------------------------------------------------------
        	recPara.setField("YD_EQP_ID", 	szYdEqpId);
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.getYdEqp", logId, szMethodName, "설비상태  체크");

			if(rsResult.size() > 0) {
				szYdEqpStat =  StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_EQP_STAT"),"");
			}
			
			if (JPlateYdConst.YD_EQP_STAT_UP_CMPL.equals(szYdEqpStat) || 			// 권상완료(2)
				JPlateYdConst.YD_EQP_STAT_DN_WO.equals(szYdEqpStat)) {			// 권하지시(3)

				szRtnMsg = "권상처리후 파일링/횡작업 불가.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				return szRtnMsg;
        	}
			
			// 파라미터 체크
			if (iYdPilingSh < 1 || iYdPilingSh > 15) {
				szRtnMsg = "파일링 매수 오류 :: " + szYdPilingSh;
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			if (!"H".equals(szYdPilingGp) && !"P".equals(szYdPilingGp) && !"M".equals(szYdPilingGp)) {
				szRtnMsg = "파일링 구분 오류 :: " + szYdPilingGp;
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			if ("H".equals(szYdPilingGp) && iYdPilingSh > 3) {
				szRtnMsg = "횡작업시 3매 초과!!";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}
			if ("P".equals(szYdPilingGp) && iYdPilingSh > 5) {
				szRtnMsg = "파일링시 5매 초과!!";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}
			if ("M".equals(szYdPilingGp) && iYdPilingSh > 15) {
				szRtnMsg = "멀티작업시 15매 초과!!";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}
			
        	String szFstLyrNo 	= "";       // 최하단 
        	String szLastLyrNo 	= "";
			
        	// 파일링 실적중 최하단 구하기
			for(int ii=0; ii<iYdPilingSh; ii++) {
				szYdUpWoLayer 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WO_LAYER" + Integer.toString(ii+1));	// 권상지시단
				if (ii==0) {
					szFstLyrNo	= szYdUpWoLayer;
					szLastLyrNo = szYdUpWoLayer;
				}
				if (!"".equals(szYdUpWoLayer) && !"".equals(szFstLyrNo)) {
					if (Integer.parseInt(szYdUpWoLayer) < Integer.parseInt(szFstLyrNo)) {
						szFstLyrNo	= szYdUpWoLayer;
					}
				}
				if (!"".equals(szYdUpWoLayer) && !"".equals(szLastLyrNo)) {
					if (Integer.parseInt(szYdUpWoLayer) > Integer.parseInt(szLastLyrNo)) {
						szLastLyrNo	= szYdUpWoLayer;
					}
				}
			}
			
			if ("M".equals(szYdPilingGp)||"P".equals(szYdPilingGp)) {		// 파일링작업,멀티작업 일때
				if (szFstLyrNo.equals(szLastLyrNo)){
					//szRtnMsg = "멀티작업 및 파일링 작업인데 권상지시 단이 동일 함 :: " + szLastLyrNo;;
					szRtnMsg = "멀티및파일링작업인데권상단동일" + szLastLyrNo;
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
			}

			if ("H".equals(szYdPilingGp)) {		// 횡작업 일때
				if (!szFstLyrNo.equals(szLastLyrNo)){
					//szRtnMsg = "횡작업 작업인데 권상지시 단이 틀림 :: " + szLastLyrNo;;
					szRtnMsg = "횡작업인데권상단틀림" + szLastLyrNo;
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
			}

			szMsg    = "[" + szOperationName + "] 파일링/횡작업 실적 수신데이타중 최하단 정보 >>>> " + szFstLyrNo;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			
			// 야드크레인스케쥴ID, 재료번호, 권상지시위치, 권상지시단 체크
			for(int ii=0; ii<iYdPilingSh; ii++) {
				szYdCrnSchId  	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID" + Integer.toString(ii+1)		);	// 야드크레인스케쥴ID
				szStlNo		  	= ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO" + Integer.toString(ii+1)			);	// 재료번호
				szYdUpWoLoc 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WO_LOC" + Integer.toString(ii+1)		);	// 권상지시위치
				szYdUpWoLayer 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WO_LAYER" + Integer.toString(ii+1)	);	// 권상지시단
        		szYdStkColGp  	= ydUtils.substr(szYdUpWoLoc, 0, 6													);	// 권상위치구분
				szYdStkSpanGp	= ydUtils.substr(szYdUpWoLoc, 2, 2													);	// 권상위치 스판구분
				szYdUpWoBed		= ydUtils.substr(szYdUpWoLoc, 6, 2													);	// 권상위치 베드 번호
				
		    	szRtnMsg = "데이타 출력 :: " + (szYdCrnSchId + "-" + szStlNo + " szYdUpWoLoc: " + szYdUpWoLoc + " ,szYdUpWoLayer: " + szYdUpWoLayer + " , szYdStkColGp: " + szYdStkColGp  + " , szYdStkSpanGp: " + szYdStkSpanGp + " , szYdUpWoBed: " + szYdUpWoBed + " , ii:" + ii);
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				// 스케쥴 ID 체크
				if ("".equals(szYdCrnSchId)) {
					szRtnMsg = "스케쥴 ID 오류 :: " + szYdCrnSchId;
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}

				// 재료번호 체크
				if ("".equals(szStlNo)) {
					szRtnMsg = "재료번호 오류 :: " + szStlNo;
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
				
				// 권상지시위치 체크
				bLocCheck = true;
				if (!"RT".equals(ydUtils.substr(szYdUpWoLoc, 2, 2))) {
					if ("".equals(szYdUpWoLoc)) {
						bLocCheck = false;
					} else {
			        	recPara.setField("YD_STK_COL_GP", 		szYdStkColGp	);
			        	recPara.setField("YD_STK_BED_NO", 		szYdUpWoBed		);
						rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYdStkbed", logId, szMethodName, "권상지시위치 체크");
			        	
			        	if (rsResult.size() <= 0) {
			        		bLocCheck = false;
			        	}
					}
	
					if (!bLocCheck) {
						szRtnMsg = "권상위치 오류 :: " + szYdUpWoLoc;
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
					}
					
					// 권상지시단 체크
					if ("".equals(szYdUpWoLayer)) {
						szRtnMsg = "권상위치단 오류 :: " + szYdUpWoLayer;
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
					}
				}
				
				if (!"".equals(szYdCrnSchId)) {
					//if ("".equals(szOldYdCrnSchId)) {
					//	szOldYdCrnSchId = szYdCrnSchId;
					//}

					// RT일경우 열로 Bed Index 가져온다
                    if ("RT".equals(ydUtils.substr(szYdUpWoLoc, 2, 2))) {
    		        	iBedIdx = this.getBedIdx(arrYdLoc, szYdUpWoLoc);
    		        	iLyrIdx = Integer.parseInt(szYdUpWoLayer) - 1;										// 적치단 	Index (0~5)
                    } else if("BS".equals(ydUtils.substr(szYdUpWoLoc, 2, 2))) {
    		        	//iBedIdx = this.getBedIdx(arrYdLoc, szYdUpWoLoc);
    		        	iLyrIdx = Integer.parseInt(szYdUpWoLayer) - 1;										// 적치단 	Index (0~5)
    		        	iBedIdx = this.getBedIdxCn(arrYdLoc, szYdUpWoLoc, iLyrIdx);
                    } else if("CN".equals(ydUtils.substr(szYdUpWoLoc, 2, 2))) {
    		        	//iBedIdx = this.getBedIdx(arrYdLoc, szYdUpWoLoc);
    		        	iLyrIdx = Integer.parseInt(szYdUpWoLayer) - 1;										// 적치단 	Index (0~5)
    		        	iBedIdx = this.getBedIdxCn(arrYdLoc, szYdUpWoLoc, iLyrIdx);
                    } else {
    		        	iBedIdx = Integer.parseInt(szYdUpWoBed)   - 1;										// 적치베드 	Index (0~2)
    		        	iLyrIdx = Integer.parseInt(szYdUpWoLayer) - 1;										// 적치단 	Index (0~5)
                    }
                    
					if ((iBedIdx >= 0 && iBedIdx < 3) && (iLyrIdx >= 0 && iLyrIdx < 5)) {

				    	if ("".equals(arrLayer[iLyrIdx][iBedIdx])) {
						    arrSchId[iLyrIdx][iBedIdx] = szYdCrnSchId;		// 스케쥴코드
					    	arrStlNo[iLyrIdx][iBedIdx] = szStlNo;			// 재료번호
					    	arrYdLoc[iLyrIdx][iBedIdx] = szYdUpWoLoc;		// 재료적치열
					    	arrLayer[iLyrIdx][iBedIdx] = szYdUpWoLayer;		// 재료적치단
					    	
					    	szRtnMsg = "array 변수 셋팅 :: " + (szYdUpWoLoc + "-" + szYdUpWoLayer + " iBedIdx: " + iBedIdx + " ,iLyrIdx: " + iLyrIdx + " , szStlNo: " + szStlNo + " , ii:" + ii);
							szMsg    = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					    	
				    	} else {
							szRtnMsg = "array 변수 셋팅시 충돌 error :: " + (szYdUpWoLoc + "-" + szYdUpWoLayer + " iBedIdx: " + iBedIdx + " ,iLyrIdx: " + iLyrIdx+ " , szStlNo: " + szStlNo + " , ii:" + ii);
							szMsg    = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							return szRtnMsg;
				    	}

					} else {
						szRtnMsg = "array 변수 셋팅시 Index ERROR :: " + (szYdUpWoLoc + "-" + szYdUpWoLayer + " iBedIdx: " + iBedIdx + " ,iLyrIdx: " + iLyrIdx + " , szStlNo: " + szStlNo + " , ii:" + ii);
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
					}

					//------------------------------------------------------------------------------------------------
		    		// 기존 크레인작업지시 조회 --> 작업예약 ID SET
					//------------------------------------------------------------------------------------------------
					recPara.setField("YD_CRN_SCH_ID", 		szYdCrnSchId);
					rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnSch", logId, szMethodName, "기존 크레인작업지시 조회 --> 작업예약 ID SET");
		        	
		        	if (rsResult.size() <= 0) {
						szRtnMsg = "기존 크레인작업지시 조회 오류 :: YD_CRN_SCH_ID : " + szYdCrnSchId;
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
		        	}
		        	
		        	arrWbookId[iLyrIdx][iBedIdx] = StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"),"");
		        	szYdAidWrkYn = StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_AID_WRK_YN"),"N");

					if ("".equals(szOldYdCrnSchId)) {
						szOldYdCrnSchId = szYdCrnSchId;
						recSch.setRecord(rsResult.getRecord(0)); //크레인 작업지시 저장
					}
		        	
		        	if ("".equals(szSaveAidWrkYn)) {
		        		szSaveAidWrkYn = szYdAidWrkYn;
		        	}
		        	if (!szSaveAidWrkYn.equals(szYdAidWrkYn)) {
						szRtnMsg = "(보조작업,주작업)을 파일링/횡작업 불가!!";
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
		        	}

		        	// 스케쥴 코드 체크
		        	szYdSchCd 	= StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_SCH_CD"),"");
		        	szYdUpWoLoc = StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_UP_WO_LOC"),"");
		        	szYdDnWoLoc = StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_DN_WO_LOC"),"");
		    		szYdWrkStat = StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_WRK_PROG_STAT"),"");

					szMsg = "[" + szOperationName + "] 파일링/횡작업 확인 >>>> 스케쥴코드::" + szYdSchCd + ", 권상위치::" + szYdUpWoLoc + ", 권하위치::" + szYdDnWoLoc + ", 보조작업::" + szYdAidWrkYn + ", 작업상태::" + szYdWrkStat;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		        	//작업진행상태 체크(2, 3) 2,3이 아니면 에러 메시지 출력후 리턴
			        if ("2".equals(szYdWrkStat) || "3".equals(szYdWrkStat) || "4".equals(szYdWrkStat)) {
						szRtnMsg = "대기/명령선택 상태에서만 작업가능!!" + szYdWrkStat;
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
			        }

					
		        	if ("".equals(szOldYdWbookId)) {
			        	//recWbook = rsResult.getRecord();		// 작업예약 등록을 위해 Record Save
			        	recWbook.setRecord(rsResult.getRecord(0)); // 작업예약 등록을 위해 Record Save
		        		szOldYdWbookId = ydDaoUtils.paraRecChkNull(recWbook, "YD_WBOOK_ID");
		        	}

		        	// TO위치에 따라 열길이 체크 (횡작업, 멀티작업 일때만 체크)
		        	if (!"XX010101".equals(szYdDnWoLoc) && !"XXYY0101".equals(szYdDnWoLoc) && !"P".equals(szYdPilingGp)) {

		        		szYdStkColGp  = ydUtils.substr(szYdDnWoLoc, 0, 6);		// 적치열구분
		        		szYdStkSpanGp = ydUtils.substr(szYdDnWoLoc, 2, 2);		// 스판구분

						// 권하지시위치 체크
			        	recPara.setField("YD_STK_COL_GP", 		szYdStkColGp);
			        	if("H".equals(szYdPilingGp)||"M".equals(szYdPilingGp)) {
			        		if(ii>2) {
			        			recPara.setField("YD_STK_BED_NO", 		"01");
			        		} else {
			        			recPara.setField("YD_STK_BED_NO", 		"0" + (ii+1));
			        		}
			        	} else {
			        		recPara.setField("YD_STK_BED_NO", 		szYdUpWoBed);
			        	}
			        	
						rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYdStkbed", logId, szMethodName, "권하지시위치 체크");
			        	
			        	if (rsResult.size() <= 0) {
							szRtnMsg = "TO BED정보 조회 오류! [" + szYdUpWoBed + "]";
							szMsg    = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							return szRtnMsg;
			        	}

		        		if ("BC".equals(szYdStkSpanGp) ||"BS".equals(szYdStkSpanGp) || "CN".equals(szYdStkSpanGp) || "CB".equals(szYdStkSpanGp) || "RT".equals(szYdStkSpanGp)) {

							szMsg = "[" + szOperationName + "] 설비일때 횡작업일때 열길이 체크 SKIP :: " + szYdStkSpanGp;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		        		} else {
				        	//------------------------------------------------------------------------------------------------
							// 열정보 조회 (횡작업시 길이 체크)
							//------------------------------------------------------------------------------------------------
							recPara.setField("YD_STK_COL_GP", szYdStkColGp);
							rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.getYdStkcol", logId, szMethodName, "권하지시위치 체크");
							if (rsResult.size() <= 0) {
								szRtnMsg = "TO 위치 길이 체크 오류! [" + szYdStkColGp + "]";
								szMsg    = "[" + szOperationName + "] " + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
								return szRtnMsg;
							}

							if (Integer.parseInt(StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_STK_COL_L"),"0")) > iYdStkColL) {
								iYdStkColL = Integer.parseInt(StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_STK_COL_L"),"0"));
							}

							szMsg = "[" + szOperationName + "] 횡작업일때 열길이 조회 >>>> TO위치 :: " + szYdDnWoLoc + ", 결과 ::" + iYdStkColL;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		        		}
		        	}

		        	//------------------------------------------------------------------------------------------------
					// 재료정보 조회
					//------------------------------------------------------------------------------------------------
					recPara.setField("STL_NO", szStlNo);
					
					rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStock", logId, szMethodName, "재료정보 조회");
					
		        	if (rsResult.size() <= 0) {
						szRtnMsg = "재료정보 조회 오류 .. 재료번호::" + szStlNo;
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
		        	}
		        	
		        	//중량합계
		        	iSumWrkWt = iSumWrkWt + Integer.parseInt(StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_MTL_WT"),"0"));
		        	
		        	//두께 array변수에 저장
		        	dMtlT	  = Double.parseDouble(StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_MTL_T"),"0"));
		        	if(dMtlT > dArrWrkT[iLyrIdx]) {
		        		dArrWrkT[iLyrIdx] = dMtlT;
		        	}

		        	iMtlL = Integer.parseInt(StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_MTL_L"),"0"));
		        	if (iMtlL > iYdStkColL) {
		        		iMtlL = iYdStkColL;
		        	}

					if (!"P".equals(szYdPilingGp)) {		// 횡작업,멀티작업 일때
						iArrWrkLen[iLyrIdx] = iArrWrkLen[iLyrIdx]  + iMtlL;
					} else {
						if (iArrWrkLen[iLyrIdx] < iMtlL) {
							iArrWrkLen[iLyrIdx] = iMtlL;
						}
					}
				}
			}
			
			if (iYdStkColL == 0) {
				iYdStkColL = 25000;
			}

        	dSumWrkT	= 0;
        	iSumWrkLen	= 0;

			for(int kk=0; kk<5; kk++) {
				dSumWrkT = dSumWrkT + dArrWrkT[kk];
				if (iArrWrkLen[kk] > iSumWrkLen) {
					iSumWrkLen = iArrWrkLen[kk];
				}
			}
			
			szMsg = "[" + szOperationName + "] >>>> 파일링/횡작업 재료 계산 >>>> 길이 : " + iSumWrkLen + ", 두께 : " + dSumWrkT + ", 중량 : " + iSumWrkWt;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			// 재료길이 25M 넘는 재료는 횡작업 불가 [횡작업,멀티작업 일때만 체크]
			if (!"P".equals(szYdPilingGp) && iSumWrkLen > iYdStkColL) {
				szRtnMsg = "재료길이 합이 " + (iYdStkColL/1000) + "M 넘어 횡작업 불가! [" + iSumWrkLen + "]";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}
			
			// 재료두께 50T 넘는 재료는 파일링 불가 [파일링,멀티작업 일때만 체크] + 파일링 50t 기준 해제 허용 경우 추가
			if (!"H".equals(szYdPilingGp)){
				
				/*	1. 파일링 50t 기준 해제 허용 크레인권상위치--- PBRT4N01(쇼트입측), PART1301(체인트렌스퍼출측)
				 *	2. 파일링 50t 기준 해제 허용 크레인번호------ 열처리 크레인 PM44,PM20  강력교정 크레인  PM55,PM54
				 *	3. 파일링 50t 기준 해제 허용 스케줄코드------ PBRT40LM , PART13LM
				 *	4. 변경 파일링 기준 ( 50 -> 170 ) ------ 강력교정 최대두께 80t 2매 + α) ,  상기위치외 파일링기준 현재와 동일
				 *	5. 파일링제한 해제 대상 : 파일링작업시  
				 */
				if (	("PBRT4N01".equals(szYdUpWoLoc) || "PART1301".equals(szYdUpWoLoc))
					&&  ("PACRA1".equals(szYdEqpId) 	|| "PACRA2".equals(szYdEqpId) 		|| "PBCRB1".equals(szYdEqpId) || "PBCRB2".equals(szYdEqpId))
					&&  ("PBRT40LM".equals(szYdSchCd) 	|| "PART13LM".equals(szYdSchCd))
					){
					if(dSumWrkT > 170){
						szRtnMsg =		"권상위치 : " + szYdUpWoLoc
									+ ", 크레인 : "  + szYdEqpId
									+ ", 스케줄코드 : "  + szYdSchCd
									+ ", 재료두께 합이 170t 넘어 파일링작업 불가! [" + dSumWrkT + "]";
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
					}
				} else if(dSumWrkT > 50) {
						szRtnMsg = "재료두께 합이 50t 넘어 파일링작업 불가! [" + dSumWrkT + "]";
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
				}
				
			}

			if("PECRE2".equals(szYdEqpId)) {
				// 35톤 넘는 재료는 불가
				if (iSumWrkWt > 35000) {
					szRtnMsg = "재료중량 합이 35톤 넘어 작업 불가! [" + iSumWrkWt + "]";
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
			} 
			else if("PFCRF2".equals(szYdEqpId)){  //21호기(PFCRF2) 제한 중량 기준 변경 25T->30T 2021.12.06 박종호. 허남웅 책임 요청사항.
				// 30톤 넘는 재료는 불가
				if (iSumWrkWt > 30000) {
					szRtnMsg = "재료중량 합이 30톤 넘어 작업 불가! [" + iSumWrkWt + "]";
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
			}
			else {
				// 25톤 넘는 재료는 불가
				if (iSumWrkWt > 25000) {
					szRtnMsg = "재료중량 합이 25톤 넘어 작업 불가! [" + iSumWrkWt + "]";
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
			}
			
			szMsg = "[" + szOperationName + "] >>>> 크레인스케쥴 등록 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//------------------------------------------------------------------------------------------------
			// 1. 크레인 작업예약 생성 [신규 파일링 작업예약]
			//------------------------------------------------------------------------------------------------
			// 1.1. 작업예약ID를 할당받는다 (보조작업일때는 작업예약 생성 안함)
			if ("Y".equals(szYdAidWrkYn)) {
				szNewYdWbookId = szOldYdWbookId;
			} else {
				//신규 작업예약ID 생성
				rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getSeqId", logId, szMethodName, "신규 작업예약ID 생성");
				if (rsResult.size() <= 0) {
					szRtnMsg = "작업예약 Id를 생성하지 못했습니다.";
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
	    		}
				szNewYdWbookId = rsResult.getRecord(0).getFieldString("YD_WBOOK_ID");		

				// 1.2. 신규작업예약생성
	    		recWbook.setField("YD_WBOOK_ID", 	szNewYdWbookId	);
	    		recWbook.setField("REGISTER",	    szModifier		);			// 등록자
	    		recWbook.setField("MODIFIER",	    szModifier		);			// 수정자
	    		
	    		intRtnVal = commDao.insert(recWbook, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.insYdWrkbook", logId, szMethodName, "신규작업예약생성");
	    		
	        	if (intRtnVal <= 0) {
					szRtnMsg = "신규 작업예약 생성 오류 ::" + Integer.toString(intRtnVal);
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
	        	}
			}

			//------------------------------------------------------------------------------------------------
			// 2. 크레인 작업지시  생성 [신규 파일링 작업지시]
			//------------------------------------------------------------------------------------------------
			// 2.1. 신규 크레인스케줄ID를 할당받는다
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getSeqId", logId, szMethodName, "신규 크레인스케줄ID를 할당받는다");
			if (rsResult.size() <= 0) {
				szRtnMsg = "크레인스케줄 Id를 생성하지 못했습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
    		}
			szNewYdCrnSchId = rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID");			
			
			//------------------------------------------------------------------------------------------------
    		// 2.2. 기존 크레인작업지시 조회
			//------------------------------------------------------------------------------------------------
			// 상단에서 szOldYdCrnSchId 설정시 recSch 에 저장 함 
			
        	szYdSchCd = ydDaoUtils.paraRecChkNull(recSch, "YD_SCH_CD");

			szMsg = "[" + szOperationName + "] DATA 확인 >>>>" + rsResult.size() + ">>>>" + recSch.toString();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	recSch.setField("YD_CRN_SCH_ID", 		szNewYdCrnSchId								);	// 신규 야드크레인스케쥴ID
        	recSch.setField("YD_WBOOK_ID", 			szNewYdWbookId								);	// 신규 작업예약ID
        	recSch.setField("REGISTER",	     		szModifier									);  // 등록자
        	recSch.setField("MODIFIER",	        	szModifier									);	// 수정자
        	recSch.setField("YD_EQP_ID",			szYdEqpId									);	// 야드설비ID
        	recSch.setField("YD_MAIN_WRK_MTL_SH",	szYdPilingSh								);	// 야드주작업재료매수
        	recSch.setField("YD_EQP_WRK_SH",	    szYdPilingSh								);	// 야드설비작업매수
        	recSch.setField("YD_EQP_WRK_WT",	    Integer.toString(iSumWrkWt)					);	// 야드설비작업중량
        	recSch.setField("YD_EQP_WRK_T",	        Double.toString(dSumWrkT)					);	// 야드설비작업총두께
        	recSch.setField("YD_EQP_WRK_L",	        Double.toString(iSumWrkLen)					);	// 야드설비작업총길이
        	recSch.setField("YD_WRK_PROG_STAT", 	JPlateYdConst.YD_EQP_STAT_DN_WO			);	// 권하지시
        	recSch.setField("YD_UP_WR_LOC",			recSch.getField("YD_UP_WO_LOC")				);	// 야드권상실적위치
        	recSch.setField("YD_UP_WR_LAYER",		recSch.getField("YD_UP_WO_LAYER")			);	// 야드권상실적단
        //	recSch.setField("YD_UP_WRK_ACT_GP",		"M");											// 야드권상작업수행구분
        	recSch.setField("YD_UP_WRK_ACT_GP",	    szYdPilingGp								);	// 파일링작업구분 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)
        	recSch.setField("YD_UP_WR_XAXIS",		recSch.getField("YD_UP_WO_XAXIS")			);	// 야드권상실적X축
        	recSch.setField("YD_UP_WR_YAXIS",		recSch.getField("YD_UP_WO_YAXIS")			);	// 야드권상실적Y축
        	recSch.setField("YD_UP_WR_YAXIS1",		recSch.getField("YD_UP_WO_YAXIS1")			);	// 야드권상실적Y축1
        	recSch.setField("YD_UP_WR_YAXIS2",		recSch.getField("YD_UP_WO_YAXIS2")			);	// 야드권상실적Y축2
        	recSch.setField("YD_UP_WR_ZAXIS",		recSch.getField("YD_UP_WO_ZAXIS")			);	// 야드권상실적Z축
        	recSch.setField("YD_WORD_DT", 			JPlateYdUtils.getCurDate("yyyyMMddHHmmss")	);	// 명령선택일시
        	recSch.setField("YD_UP_CMPL_DT",		JPlateYdUtils.getCurDate("yyyyMMddHHmmss")	);	// 권상완료일시

			//------------------------------------------------------------------------------------------------
    		// 2.3. 크레인작업지시 등록
			//------------------------------------------------------------------------------------------------
        	intRtnVal = commDao.insert(recSch, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.insYdCrnsch", logId, szMethodName, "크레인작업지시 등록");
        	if (intRtnVal <= 0) {
				szRtnMsg = "기존 크레인작업지시 등록 오류 ::" + Integer.toString(intRtnVal);
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
        	}

        	// 크레인스케쥴의 권하위치로 SET
        	szYdDnWoLoc = ydDaoUtils.paraRecChkNull(recSch, "YD_DN_WO_LOC");
			
			szMsg = "[" + szOperationName + "] >>>> 크레인작업재료 등록 시작 >>>> " + szYdDnWoLoc;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	iWrkCnt = 0;
			for(int ii=0; ii<5; ii++) {
				for(int jj=0; jj<3; jj++) {
					if (!"".equals(arrStlNo[ii][jj])) {
						//------------------------------------------------------------------------------------------------
			    		// 2.4. 크레인 작업재료 조회
						//------------------------------------------------------------------------------------------------
			        	recPara.setField("YD_CRN_SCH_ID", 	arrSchId[ii][jj]	);
			        	recPara.setField("STL_NO", 			arrStlNo[ii][jj]	);
			        	recPara.setField("YD_WBOOK_ID", 	arrWbookId[ii][jj]	);

						rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getYdCrnWrkMtl", logId, szMethodName, "크레인 작업재료 조회");
						if (rsResult.size() <= 0) {
							szRtnMsg = "기존 크레인작업재료 조회 .. 재료번호 ::" + arrStlNo[ii][jj] + " >> 크레인스케줄ID ::" + arrSchId[ii][jj];
							szMsg    = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							return szRtnMsg;
			        	}
						recMtl.setRecord(rsResult.getRecord(0));
						
						//------------------------------------------------------------------------------------------------
			    		// 2.5. 크레인작업재료 등록
						//------------------------------------------------------------------------------------------------
			        	recMtl.setField("YD_CRN_SCH_ID",	szNewYdCrnSchId							);		// 야드크레인스케쥴ID
	        			recMtl.setField("STL_NO",			arrStlNo[ii][jj]						);		// 재료번호
						recMtl.setField("MODIFIER",	        szModifier								);		// 수정자
						recMtl.setField("YD_STK_LYR_NO",	arrLayer[ii][jj]						);      // 야드적치단번호
						recMtl.setField("YD_STK_LOT_TP",	ydUtils.substr(arrYdLoc[ii][jj], 6, 2)	);		// 권상위치 BED 번호
						recMtl.setField("YD_STK_LOT_CD", 	arrYdLoc[ii][jj]						);		// 권상위치
						
	        			commDao.insert(recMtl, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.insYdCrnWrkMtl", logId, szMethodName, "크레인작업재료 등록");
	        			
						if (rsResult.size() <= 0) {
							szRtnMsg = "기존 크레인작업재료 등록 오류 ::" + arrStlNo[ii][jj] + " >> 크레인스케줄ID ::" + arrSchId[ii][jj];
							szMsg    = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							return szRtnMsg;
			        	}

						if ("N".equals(szYdAidWrkYn)) {		// 주작업일때만 작업예약 신규로 생성
							//------------------------------------------------------------------------------------------------
				        	// 2.6. 작업예약재료 조회
							//------------------------------------------------------------------------------------------------
							// 상단에서 이미 조회 하여  recMtl 에 저장함


							//------------------------------------------------------------------------------------------------
				        	// 2.7. 작업예약재료 등록
							//------------------------------------------------------------------------------------------------
				        	recMtl.setField("YD_WBOOK_ID",		szNewYdWbookId		);	// 작업예약ID
		        			recMtl.setField("STL_NO",			arrStlNo[ii][jj]	);	// 재료번호
		        			recMtl.setField("YD_STK_LYR_NO", 	arrLayer[ii][jj]	);
							recMtl.setField("MODIFIER",	        szModifier			);	// 수정자
		        			recMtl.setField("REGISTER", 		szModifier			);

		        			intRtnVal = commDao.insert(recMtl, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.insYdWrkbookMtl", logId, szMethodName, "작업예약재료 등록");
		        			
				        	if (intRtnVal <= 0) {
								szRtnMsg = "기존 작업예약재료 등록 오류 ::" + arrStlNo[ii][jj] + " >> " + Integer.toString(intRtnVal);
								szMsg    = "[" + szOperationName + "] " + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
								return szRtnMsg;
				        	}

							//------------------------------------------------------------------------------------------------
				        	// 2.8. 기존작업예약재료 삭제
							//------------------------------------------------------------------------------------------------
				        	recPara.setField("YD_WBOOK_ID", 	arrWbookId[ii][jj]	);
				        	recPara.setField("STL_NO", 			arrStlNo[ii][jj]	);
		        			recPara.setField("MODIFIER", 		szModifier			);

							intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.deldWrkbookMtl", logId, szMethodName, "기존작업예약재료 삭제");;
							
				        	if (intRtnVal <= 0) {
								szRtnMsg = "기존 작업예약재료 삭제 오류 ::" + arrStlNo[ii][jj] + " >> " + Integer.toString(intRtnVal);
								szMsg    = "[" + szOperationName + "] " + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
								//return szRtnMsg;
				        	}

							//------------------------------------------------------------------------------------------------
				        	// 2.9. 기존작업예약 삭제
							//------------------------------------------------------------------------------------------------
							intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.delYdWrkbook", logId, szMethodName, "기존작업예약 삭제");
				        	if (intRtnVal <= 0) {
								szRtnMsg = "기존 작업예약 삭제 오류 ::" + arrStlNo[ii][jj] + " >> " + Integer.toString(intRtnVal);
								szMsg    = "[" + szOperationName + "] " + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
								//return szRtnMsg;
				        	}

				        	//------------------------------------------------------------------------------------------------
				        	// 2.10. 저장품에 작업예약ID 수정
							//------------------------------------------------------------------------------------------------
				        	recPara.setField("YD_WBOOK_ID",		szNewYdWbookId		);	// 작업예약ID
				        	recPara.setField("STL_NO",			arrStlNo[ii][jj]	);	// 재료번호
				        	recPara.setField("MODIFIER",	    szModifier			);	// 수정자
				        	recPara.setField("YD_SCH_CD",	    szYdSchCd			);	// 스케쥴코드
							intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdStockWbook", logId, szMethodName, "저장품에 작업예약ID 수정");
				        	if (intRtnVal <= 0) {
								szRtnMsg = "저장품에 작업예약ID 수정 오류 .. 재료번호::" + arrStlNo[ii][jj];
								szMsg    = "[" + szOperationName + "] " + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
								return szRtnMsg;
				        	}
						}

			        	iWrkCnt ++;
			        }
				}	// end of Loop jj
			}	// end of Loop ii

			szMsg = "[" + szOperationName + "] >>>> 크레인작업재료 등록 정상종료 :: " + iWrkCnt + "건";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			
			//------------------------------------------------------------------------------------------------
			// 설비상태 UPATE --> 크레인 상태를 권하지시로 SET
			//------------------------------------------------------------------------------------------------
			recPara.setField("YD_EQP_ID", 		szYdEqpId	);
			recPara.setField("YD_EQP_STAT", 	"3"			);
			recPara.setField("MODIFIER", 		szModifier	);
			intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.updYdEqpStat", logId, szMethodName, "설비상태 UPATE --> 크레인 상태를 권하지시로 SET");
			if (intRtnVal <= 0) {
				szRtnMsg = "설비상태 변경시 오류 발생 : " + Integer.toString(intRtnVal);
				szMsg = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}
			
			//------------------------------------------------------------------------------------------------
			// 2. 저장위치 변경 크레인으로
			//------------------------------------------------------------------------------------------------
			// 2.1. 권상 지시위치의 적치 상태 정보 CLEAR
			// 2.2. 현재료의 적지위치를 크레인으로 변경
			// 2.3. 권하 지시위치의 적치 상태 정보 SET
			//------------------------------------------------------------------------------------------------
        	iWrkCnt 		= 0;
        	szYdStkBedNo	= ydUtils.substr(szYdDnWoLoc,6,2);
        	if (szYdStkBedNo == null || "".equals(szYdStkBedNo)) {
        		szYdStkBedNo = "01";
        	}

			for(int ii=0; ii<5; ii++) {
				for(int jj=0; jj<3; jj++) {
					if (!"".equals(arrStlNo[ii][jj])) {
						// 2.1. 권상위치 CLEAR
						recPara.setField("MODIFIER",			szModifier			);
						recPara.setField("YD_STK_LYR_MTL_STAT",	"U"					);
						recPara.setField("STL_NO",				arrStlNo[ii][jj]	);
						recPara.setField("YD_GP",				szYdGp				);


//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recPara에 logId 추가 
						recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
											
						//intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrClearByStlNo", logId, szMethodName, "권상위치 CLEAR");
						intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
						if (intRtnVal <= 0) {
							szRtnMsg = "파일링 재료 권상위치 CLEAR 오류발생 : " + Integer.toString(intRtnVal);
							szMsg = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						    //RT에서 북아웃시 기존위치가 다른 재료로 변경됨으로 계속 진행하도록 보완
						    //return szRtnMsg;
						} else {
							iUpWrkCnt ++;
						}

						// 2.2. 크레인으로 저장위치 변경
						recPara.setField("YD_STK_COL_GP",       szYdEqpId			);
		    			recPara.setField("YD_STK_BED_NO",       "01"				);
		    			recPara.setField("YD_STK_LYR_NO",       arrLayer[ii][jj]	);
						recPara.setField("YD_STK_LYR_MTL_STAT",	"C"					);
						recPara.setField("STL_NO",				arrStlNo[ii][jj]	);
						recPara.setField("MODIFIER",			szModifier			);
		                //intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat", logId, szMethodName, "크레인으로 저장위치 변경");
						intRtnVal = ydStkLyrDao.updYdStklyrStat(recPara);  	//크레인 적치단의 재료정보 UPDATE

		                // 2.3. 권하위치 CLEAR
						recPara.setField("MODIFIER",			szModifier			);
						recPara.setField("YD_STK_LYR_MTL_STAT",	"D"					);
						recPara.setField("STL_NO",				arrStlNo[ii][jj]	);
						recPara.setField("YD_GP",				szYdGp				);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recPara에 logId 추가 
						recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
						
						//intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrClearByStlNo", logId, szMethodName, "권하위치 CLEAR");
						intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
						if (intRtnVal <= 0) {
							szRtnMsg = "파일링 재료 권하위치 CLEAR 오류발생 : " + Integer.toString(intRtnVal);
							szMsg = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							//return szRtnMsg;
						} else {
							iDnWrkCnt ++;
						}
					}
				}	// end of loop jj
			}	// end of loop ii
        	
			szMsg = "[" + szOperationName + "] >>>> 파일링 재료 권상위치 CLEAR 정상종료 :: " + iUpWrkCnt + "건";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			szMsg = "[" + szOperationName + "] >>>> 파일링 재료 권하위치 CLEAR 정상종료 :: " + iDnWrkCnt + "건";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//------------------------------------------------------------------------------------------------
			// 2.4. 파일링후 권하위치 SET
			//------------------------------------------------------------------------------------------------
			String szTopLyr = JPlateYdCommonUtils.getTopLyrNoByColGp(ydUtils.substr(szYdDnWoLoc,0,6), "01", "", szYdPilingGp, "");

			szMsg = "[" + szOperationName + "] >>>> 파일링 재료 권하위치 단 :: " + szTopLyr;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			
			for(int ii=0; ii<5; ii++) {
				szYdStkLyrNo = ydDaoUtils.stringPlusInt(szTopLyr, ii);			// 적치단을    1증가
				szYdStkBedNo = "00";

				for(int jj=0; jj<3; jj++) {
					szYdStkBedNo = ydDaoUtils.stringPlusInt2(szYdStkBedNo, 1);	// 적치베드를 1증가, 단은 그대로 놔둠
					if (!"".equals(arrStlNo[ii][jj])) {
						// 2.4.1 저장위치 공베드 조회 (횡작업일때 베드 증가 , 파일링일때 적치단 증가)
						recPara.setField("YD_STK_COL_GP"	,ydUtils.substr(szYdDnWoLoc,0,6)	);
						recPara.setField("YD_STK_BED_NO"	,szYdStkBedNo						);
						recPara.setField("YD_PILING_GP"		,szYdPilingGp						);		// 파일링/횡작업 구분
						recPara.setField("YD_STK_LYR_NO"	,szYdStkLyrNo						);
						
						rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getPilingToLoc", logId, szMethodName, "저장위치 공베드 조회");

						if (rsResult.size() > 0) {

			    			szYdStkBedNo = StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_STK_BED_NO"),"");
			    			szYdStkLyrNo = StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_STK_LYR_NO"),"");

							szMsg = "[" + szOperationName + "] >>>> 파일링 재료  권하위치  >>>> " + szYdStkBedNo + "-" + szYdStkLyrNo;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			                // 2.4.2. 권하위치 SET
							recPara.setField("YD_STK_COL_GP",       ydUtils.substr(szYdDnWoLoc,0,6)	);
			    			recPara.setField("YD_STK_BED_NO",       szYdStkBedNo					);
			    			recPara.setField("YD_STK_LYR_NO",       szYdStkLyrNo					);
							recPara.setField("YD_STK_LYR_MTL_STAT",	"D"								);
							recPara.setField("STL_NO",				arrStlNo[ii][jj]				);
							recPara.setField("MODIFIER",			szModifier						);
			                //intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat", logId, szMethodName, "권하위치 SET"); 	//크레인 적치단의 재료정보 UPDATE
							intRtnVal = ydStkLyrDao.updYdStklyrStat(recPara);  	//크레인 적치단의 재료정보 UPDATE

						} else {

							szMsg = "[" + szOperationName + "] >>>> 파일링 재료 권하위치 미존재로 SKIP >>>> ";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						}
					}
				}	// end of loop jj
			}	// end of loop ii
			
			
			//------------------------------------------------------------------------------------------------
			// 3. 크레인 작업지시  취소 [파일링전 작업지시]
			//------------------------------------------------------------------------------------------------
        	iWrkCnt = 0;
			for(int ii=0; ii<5; ii++) {
				for(int jj=0; jj<3; jj++) {
					if (!"".equals(arrStlNo[ii][jj])) {
						recPara.setField("YD_CRN_SCH_ID",	arrSchId[ii][jj]	);		// 야드크레인스케쥴ID
						recPara.setField("MODIFIER",	    szModifier			);		// 수정자
						recPara.setField("DEL_YN",	    	"Y"					);		// 삭제여부

			        	intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.delYdCrnSch", logId, szMethodName, "기존 크레인작업지시 삭제");
			        	if (intRtnVal <= 0) {
							szRtnMsg = "기존 크레인작업재료 삭제 오류 .. 야드크레인스케쥴ID ::" + szOldYdCrnSchId + " >> " + Integer.toString(intRtnVal);
							szMsg    = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							return szRtnMsg;
			        	}
					}

					if (!"".equals(arrStlNo[ii][jj])) {
			    		// 3.1. 크레인 작업재료 조회
			        	recPara.setField("YD_CRN_SCH_ID", 	arrSchId[ii][jj]	);
			        	recPara.setField("STL_NO", 			arrStlNo[ii][jj]	);
						recPara.setField("MODIFIER",	    szModifier			);		// 수정자
						recPara.setField("DEL_YN",	    	"Y"					);		// 삭제여부

			        	intRtnVal =	commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.delYdCrnWrkMtl", logId, szMethodName, "기존 크레인작업재료 삭제");
			        	if (intRtnVal <= 0) {
							szRtnMsg = "기존 크레인작업재료 삭제 오류 ::" + arrStlNo[ii][jj] + " >> " + Integer.toString(intRtnVal);
							szMsg    = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							return szRtnMsg;
			        	}
			        	iWrkCnt ++;
					}
				}
			}
			szMsg = "[" + szOperationName + "] >>>> 크레인작업재료 삭제 정상종료 :: " + iWrkCnt + "건";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			
			//------------------------------------------------------------------------------------------------
			// 4.크레인 작업지시 취소 전송 [기존 파일링 전 작업지시]
			//------------------------------------------------------------------------------------------------
			szMsg = "[" + szOperationName + "] >>>> 작업지시 취소 전문 전송 .. 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	iWrkCnt = 0;
			for(int ii=0; ii<5; ii++) {
				for(int jj=0; jj<3; jj++) {
					if (!"".equals(arrStlNo[ii][jj])) {
				        
				        recL2Msg.setField("MSG_ID", 					"YDY2L004"							);	// 크레인 작업지시
				        recL2Msg.setField("YD_CRN_SCH_ID",    			arrSchId[ii][jj]					);
				        recL2Msg.setField("YD_WRK_PROG_STAT", 			JPlateYdConst.YD_EQP_STAT_UP_WO	);	// 크레인 작업상태
				        recL2Msg.setField("MSG_GP",           			"D"									);

						szMsg = "[" + szOperationName + "] >>>> 작업지시 취소 전문 전송 .. 데이타 >>>> " + recL2Msg.toString();
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				        szSendMsg = ydDelegate.sendMsg(recL2Msg);
				        iWrkCnt ++;

						szMsg = "[" + szOperationName + "] >>>> 작업지시 취소 전문 전송 .. 결과 >>>> " + szSendMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						
				        //Book-Out Complete 전문 전송할 대상을 변수에 저장한다.
		    			if ("".equals(szStlNoList)) {
		    				szStlNoList = arrStlNo[ii][jj];
		    			} else {
		    				szStlNoList = szStlNoList + ";" + arrStlNo[ii][jj];
		    			}
						
					}
				}
			}
			szMsg = "[" + szOperationName + "] >>>> 작업지시 취소 전문 전송 .. 종료 :: " + iWrkCnt + "건 전송완료";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			
			
			//------------------------------------------------------------------------------------------------
			// Book-Out Complete 전문 전송
			//------------------------------------------------------------------------------------------------
			String szYdUpWrLoc = arrYdLoc[0][0];
			szYdStkSpanGp = ydUtils.substr(szYdUpWrLoc, 2, 2);		// 스판구분
			
			if ("RT".equals(szYdStkSpanGp)) {		// RT상의 파일링작업
        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 #2 열처리 YDP8L501(Bookin/out complete) 송신 추가 
//			  야드스케쥴코드(YD_SCH_CD) BOOK-OUT - PART31LM, PART32LM, PART34LM, PART35LM
//-------------------------------------------------------------------------------------------------------------------------
	        	szMsg = "[" + szOperationName + "] 야드스케쥴코드 [" + szYdSchCd + "]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
    	        if ( ydUtils.is2ndHeatBookOutSchdule(szYdSchCd, szYdUpWrLoc) ) {
				    	        	
					if("H".equals(szYdPilingGp)||"M".equals(szYdPilingGp)) {
						szArrStlNo	= szStlNoList.split(";");
						for (int ii=0; ii < szArrStlNo.length ; ii++) {
							if (!"".equals(szArrStlNo[ii])) {
								recInTemp.setField("MSG_ID", 		        "YDP8L501"		);
								recInTemp.setField("STL_NO",				""				);		// 재료번호
								recInTemp.setField("STL_NO_LIST",			szArrStlNo[ii]	);		// 재료번호 1건
								recInTemp.setField("OPERATION_TYPE",		"2"				);		// 1:Book In, 2:Book Out
								recInTemp.setField("YD_STK_COL_GP",			szYdUpWrLoc		);		// TO위치
								recInTemp.setField("YD_EQP_ID", 		    szYdEqpId		);		
			
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recInTemp에 logId 추가 
								recInTemp.setField("LOG_ID", 			logId 				);      // logId
//-------------------------------------------------------------------------------------------------------------------------
								        	        
								szSendMsg = ydDelegate.sendMsg(recInTemp);
							}
						}
						
					} else {
						
            	        recInTemp.setField("MSG_ID", 			"YDP8L501"		);      // 1 후판열처리L2
            	        recInTemp.setField("STL_NO",			""				);		// 재료번호
            	        recInTemp.setField("STL_NO_LIST",		szStlNoList		);		// 재료번호 LIST
            	        recInTemp.setField("OPERATION_TYPE",	"2"				);		// 1:Book In, 2:Book Out
            	        recInTemp.setField("YD_STK_COL_GP",		szYdUpWrLoc		);		// FROM위치
            	        recInTemp.setField("YD_EQP_ID", 		szYdEqpId		);		//야드설비ID
            			
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recInTemp에 logId 추가 
            	        recInTemp.setField("LOG_ID", 			logId 			);      // logId
//-------------------------------------------------------------------------------------------------------------------------
            	        
           	        	szSendMsg = ydDelegate.sendMsg(recInTemp);
					}
				        	        
				    	        
	        	} else if(szYdUpWrLoc.startsWith("PBRT")||szYdUpWrLoc.startsWith("PART13")||szYdUpWrLoc.startsWith("PART9")||szYdUpWrLoc.startsWith("PCRT40") ) {
    	        	//열처리 L2
    	        	
    				String sNEW_MODULE_EFF_YN = "N";

    				JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
    				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A008"); //1후판정정야드 열처리L2 Book-In/Book-Out실적 신규모듈적용여부 
    				
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(YDP3L501)---[[[ 1후판정정야드신규적용 : " + sNEW_MODULE_EFF_YN + " ]]]---", JPlateYdConst.DEBUG, logId);
    	        	
    				if(sNEW_MODULE_EFF_YN.equals("Y")) {
    					//신규 메소드 호출
    					
    					if("H".equals(szYdPilingGp)||"M".equals(szYdPilingGp)) {
    						szArrStlNo	= szStlNoList.split(";");
    						for (int ii=0; ii < szArrStlNo.length ; ii++) {
    							if (!"".equals(szArrStlNo[ii])) {
    								recInTemp.setField("MSG_ID", 		        "YDP3L501V2"	);
    								recInTemp.setField("STL_NO",				""				);		// 재료번호
    								recInTemp.setField("STL_NO_LIST",			szArrStlNo[ii]	);		// 재료번호 1건
    								recInTemp.setField("OPERATION_TYPE",		"2"				);		// 1:Book In, 2:Book Out
    								recInTemp.setField("YD_STK_COL_GP",			szYdUpWrLoc		);		// TO위치
    								recInTemp.setField("YD_EQP_ID", 		    szYdEqpId		);		
				
									szSendMsg = ydDelegate.sendMsg(recInTemp);
    							}
    						}
    						
    					} else {
    						
                	        recInTemp.setField("MSG_ID", 			"YDP3L501V2"	);      // 1 후판열처리L2
                	        recInTemp.setField("STL_NO",			""				);		// 재료번호
                	        recInTemp.setField("STL_NO_LIST",		szStlNoList		);		// 재료번호 LIST
                	        recInTemp.setField("OPERATION_TYPE",	"2"				);		// 1:Book In, 2:Book Out
                	        recInTemp.setField("YD_STK_COL_GP",		szYdUpWrLoc		);		// FROM위치
                	        recInTemp.setField("YD_EQP_ID", 		szYdEqpId		);		//야드설비ID
                	        
               	        	szSendMsg = ydDelegate.sendMsg(recInTemp);
    					}
           	        	
    				} else {
    					//기존 메소드 호출
    					
    					szArrStlNo	= szStlNoList.split(";");
    					for (int ii=0; ii < szArrStlNo.length ; ii++) {
                	        recInTemp.setField("MSG_ID", 			"YDP3L501"		);		// 1 후판열처리L2
                	        recInTemp.setField("STL_NO",			szArrStlNo[ii]	);		// 재료번호
                	        recInTemp.setField("OPERATION_TYPE",	"2"				);		// 1:Book In, 2:Book Out
                	        recInTemp.setField("YD_STK_COL_GP",		szYdUpWrLoc		);		// FROM위치
                	        recInTemp.setField("YD_EQP_ID", 		szYdEqpId		);		//야드설비ID
               	        	szSendMsg = ydDelegate.sendMsg(recInTemp);
    					}
    				}

    		        szMsg = "[" +  szOperationName  + "] RT BOOK-OUT 실적 전송 .. 완료>>>>" + szSendMsg;
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

    	        } else {
    	        	//전단 L2
  
    				String sNEW_MODULE_EFF_YN = "N";

    				JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
    				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A007"); //1후판정정야드 전단L2 Book-In/Book-Out실적 신규모듈적용여부 
    				
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(YDP2L501)---[[[ 1후판정정야드신규적용 : " + sNEW_MODULE_EFF_YN + " ]]]---", JPlateYdConst.DEBUG, logId);
    	        	
    				if(sNEW_MODULE_EFF_YN.equals("Y")) {
    					//신규 메소드 호출
    					
    					if("H".equals(szYdPilingGp)||"M".equals(szYdPilingGp)) {
    						szArrStlNo	= szStlNoList.split(";");
    						for (int ii=0; ii < szArrStlNo.length ; ii++) {
    							if (!"".equals(szArrStlNo[ii])) {

    	                	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("2", szArrStlNo[ii], szYdUpWrLoc,szYdEqpId);
    							}
    						}
    					} else {
    						
                	        //szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("2", szStlNoList, szYdUpWrLoc,szYdEqpId);
                	        
    						//2019.01.11 전단정정L2는 무조건 1매 단위로 완료실적을 보내달라는 요청
    						szArrStlNo	= szStlNoList.split(";");
    						for (int ii=0; ii < szArrStlNo.length ; ii++) {
    							if (!"".equals(szArrStlNo[ii])) {

    	                	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("2", szArrStlNo[ii], szYdUpWrLoc,szYdEqpId);
    							}
    						}
    					}
    					
    				} else {
    					//기존 메소드 호출
    					szArrStlNo	= szStlNoList.split(";");
    					for (int ii=0; ii < szArrStlNo.length ; ii++) {
	    					szStlNo = szArrStlNo[ii];
	            	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSend("2", szStlNo, szYdUpWrLoc,szYdEqpId);
    					}
    				}
    				

    		        szMsg = "[" +  szOperationName  + "] 1 후판전단L2 RT BOOK-OUT 실적 전송 .. 완료>>>>" + szSendMsg;
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
    	        }
        	
			}
			
			//------------------------------------------------------------------------------------------------
			// 5.크레인 작업지시 전송 [신규 파일링 작업지시] - 권하작업지시로 Set
			//------------------------------------------------------------------------------------------------
			szMsg = "[" + szOperationName + "] >>>> 신규 작업지시 전문 전송 .. 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        //recL2Msg.setField("MSG_ID",						"YDY2L004");						// 크레인 작업지시
	        recL2Msg.setField("MSG_ID",						"YDY2L004V2"						);	// 크레인 작업지시
	        recL2Msg.setField("YD_CRN_SCH_ID",    			szNewYdCrnSchId						);
	        recL2Msg.setField("YD_WRK_PROG_STAT", 			JPlateYdConst.YD_EQP_STAT_DN_WO	);	// 권하지시
	        recL2Msg.setField("MSG_GP",           			"I"									);

	        szSendMsg = ydDelegate.sendMsg(recL2Msg);

			szMsg = "[" + szOperationName + "] >>>> 신규 작업지시 전문 전송 .. 종료 :: " + szSendMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			szMsg = "[" + szOperationName + "] 메소드 끝 ";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			
			
			szMsg = "[" + szOperationName + "] 메소드 끝 ";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }
			
		return JPlateYdConst.RETN_CD_SUCCESS;
	}
		
	
    /**
     * 오퍼레이션명 : Book-In/Book-Out 취소처리2
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● Stirng pYdCrnSchId, String pYdWrkProgStat
     * @return ● nRtnVal
     * @throws ● DAOException
     */
	public String  procP2P3BookInOutCancel2(JDTORecord msgRecord) throws DAOException {
		// 레코드선언
		JDTORecordSet 	rsResult  	= null;
		JDTORecord 		inRec 		= null;
		JDTORecord 		outRecord	= JDTORecordFactory.getInstance().create();
		JDTORecord 		tempRec 	= null;
		JDTORecord 		recPara   	= null;

		// 변수 선언
        String 	szMethodName  		= "procP2P3BookInOutCancel2";
        String	szRtnMsg			= JPlateYdConst.RETN_CD_SUCCESS;
    	String 	szMsg         		= "";
    	String 	szOperationName 	= "Book-In/Book-Out 취소처리2";


		String	szYdStkColGp		= "";
		String	szYdStkBedNo		= "";
        String 	szModifier			= "";			// 등록자, MSG_ID
        String	szYdWrkProgStat		= "";			// 크레인작업진행상태
        String	szYdCrnSchId		= "";			// 크레인작업지시ID
  //      String 	szRegister			= "";			// 등록자, MSG_ID
        String	szFromLoc			= "";
        String	szToLoc				= "";
        
    	int 	intRtnVal 			= 0;

    	//DAO
    	JPlateYdStockDAO    ydStockDao   	= new JPlateYdStockDAO();
    	JPlateYdWrkbookDAO	ydWrkbookDao 	= new JPlateYdWrkbookDAO();
    	JPlateYdCrnSchDAO 	ydCrnSchDao		= new JPlateYdCrnSchDAO();
    	JPlateYdStkLyrDAO   ydStkLyrDao		= new JPlateYdStkLyrDAO();

		EJBConnector 		ejbConn 	= null;
		
		String szPlateIds[]		= {"","","","","","","","",""};
		int iPlateCnt 			= 0;
		

        try {
        	szMsg = "["+szOperationName+"] ---- 메소드 시작  >>>> " + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			String szPlateId 		= ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO");				// PLATE_NO
			String szOperationType 	= ydDaoUtils.paraRecChkNull(msgRecord, "OPERATION_TYPE");			// 1:Book In, 2:Book Out
			String szTrkZoneNo		= ydDaoUtils.paraRecChkNull(msgRecord, "PL_TRCK_ZONE_ASGN");		// PL_TRCK_ZONE_ASGN
			
			String szYdNo			= ydDaoUtils.paraRecChkNull(msgRecord, "YARD_NO");                  // YARD_NO
			String szReasonCode		= ydDaoUtils.paraRecChkNull(msgRecord, "REASON_CODE");				// Book-Out원인 (888:TEST , 999:북아웃취소)
			String szOperationMode	= ydDaoUtils.paraRecChkNull(msgRecord, "PL_BOOK_OUT_MOD");			// 1:one time 2:Start 3:End
			
			String szPILNG_WRK_GP	= ydDaoUtils.paraRecChkNull(msgRecord, "PILNG_WRK_GP");				// 파일링작업구분 (Y:파일링작업,N:1매작업)
			
			szPlateIds[0]		= ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO");				// PLATE_NO 1단재료번호
			szPlateIds[1]		= ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO2");				// 2단재료번호
			szPlateIds[2]		= ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO3");				// 3단재료번호
			
			if (!"1".equals(szOperationMode) && !"2".equals(szOperationMode)) {
				szRtnMsg = "수신전문중 OPERATION_MODE ERROR";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			if("Y".equals(szPILNG_WRK_GP)) {
				for(int ii=0; ii < szPlateIds.length; ii++) {
					if(!"".equals(szPlateIds[ii])) {
						iPlateCnt++;
					}
				}
			} else {
				iPlateCnt	 = 1;
			}
			
			
			//------------------------------------------------------------------------------------------------
			//------------------------------------------------------------------------------------------------
			// 1. 파라미터 체크
			// 1.1 북인   시 trkZoneNo -> to위치       ,   yardNo -> from 위치
			// 1.1 북아웃시 trkZoneNo -> from 위치 ,   yardNo -> to위치
			//------------------------------------------------------------------------------------------------
			if ("1".equals(szOperationType)) { // Book In

				szFromLoc 	= szYdNo;
				szToLoc 	= szTrkZoneNo;

				// From 위치
				if (szYdNo == null || szYdNo.length() < 6) {

					// 저장위치 없이 판번호만 들어 왔을때 처리
					if (!"".equals(szPlateId)) {

						szMsg    = "["+szOperationName+"] 저장위치 없이 판번호만 들어 왔을때 .... SKIP";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					} else {
						szRtnMsg = "수신전문중 FROM위치 ERROR >>>> " + szYdNo;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}

				if (szYdNo.length() > 1) {
					if (!"P".equals(ydUtils.substr(szYdNo, 0, 1))) {
						szRtnMsg = "수신전문중 FROM위치 ERROR >>>>>>> " + szYdNo;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}
				
			// To 위치
				
				// TRK ZONE NO를 야드저장위치로 변경
				if (!"".equals(szTrkZoneNo) && szTrkZoneNo.length() == 5) {
					// ZONE NO를 야드저장위치로 변경 :: 2300 >>>> FART01
					szToLoc = JPlateYdCommonUtils.getY2RtZoneToLoc(szTrkZoneNo);
				}
				
				if (szToLoc == null || szToLoc.equals("")) {
					szRtnMsg = "수신전문중 TO위치 ERROR >>>>>>> " + szTrkZoneNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				} 
				if (!"P".equals(ydUtils.substr(szToLoc, 0, 1))) {
					szRtnMsg = "수신전문중 TO위치 ERROR >>>>>>> " + szTrkZoneNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

			} else if ("2".equals(szOperationType)) {		// Book Out

				szFromLoc 	= szTrkZoneNo;
				szToLoc 	= szYdNo;

				// PLATE_ID
				// BOOK-OUT일때는 2,3의 경우는 미존재함 즉 'PL999999'는 미존재
				if ("".equals(szPlateId) || "PL999999".equals(szPlateId)) {
					szRtnMsg = "수신전문중 PLATE_ID ERROR";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// from 위치				
				
				// TRK ZONE NO를 야드저장위치로 변경
				if (!"".equals(szTrkZoneNo) && szTrkZoneNo.length() == 5) {
					// ZONE NO를 야드저장위치로 변경 :: 2300 >>>> FART01
					szFromLoc = JPlateYdCommonUtils.getY2RtZoneToLoc(szTrkZoneNo);
				}
				
				if (szFromLoc == null || szFromLoc.equals("")) {
					szRtnMsg = "수신전문중 FROM위치 ERROR >>>>>>> " + szTrkZoneNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				} 
				if (!"P".equals(ydUtils.substr(szFromLoc, 0, 1))) {
					szRtnMsg = "수신전문중 FROM위치 ERROR >>>>>>> " + szTrkZoneNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				
				// TO 위치
				//if (!"P".equals(ydUtils.substr(szYdNo, 0, 1)) || szYdNo.length() < 6) {
				//	szRtnMsg = "수신전문중 TO위치 ERROR >>>>> " + szYdNo;
				//	szMsg    = "["+szOperationName+"] " + szRtnMsg;
				//	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				//	return szRtnMsg;
				//}

				// TO위치의 동과 FROM위치의 동이 상이할 경우 TO위치의 동을 스케줄 기준으로 변경 : 
				String sToBay = ydUtils.substr(szToLoc,   1, 1);
				String sFrBay = ydUtils.substr(szFromLoc, 1, 1);
				if (!sToBay.equals(sFrBay)) {
					szMsg   = "["+szOperationName+"] To위치의 동과 From위치의 동이 상이하여 스케쥴 기준 적용 >>>>" + szToLoc + "," + szFromLoc;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					szToLoc = ydUtils.substr(szFromLoc, 0, 2);
				}

				// Book-Out원인
				if ("".equals(szReasonCode)) {
					szRtnMsg = "수신전문중 Book-Out원인 ERROR";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szMsg;
				}
			}

			// Book In 시 저장위치 조회
			if ("1".equals(szOperationType)) {

				if ("".equals(szPlateId)) {			// 재료번호 미존재시

	    			szYdStkColGp = szFromLoc;

					szMsg    = "["+szOperationName+"] 북인 취소 작업중 .... 재료번호 미존재 >>>> 저장위치 :: " + szYdStkColGp;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				} else {
					// 베드정보 조회
					rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
					inRec    = JDTORecordFactory.getInstance().create();
					inRec.setField("STL_NO", 		szPlateId);             	// 재료번호
					inRec.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);
	    			intRtnVal = ydStockDao.getYdStockWithLoc(inRec, rsResult);
					if (intRtnVal < 1) {
						szRtnMsg = "야드재료가 미존재 합니다 .... 재료번호:" + szPlateId;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

	    			rsResult.first();
					tempRec = JDTORecordFactory.getInstance().create();
	    			tempRec = rsResult.getRecord();

	    			szYdStkColGp 	= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_COL_GP");
				}
			}

			//------------------------------------------------------------------------------------------------
			// 2. 해당 재료 작업예약/스케쥴 존재여부 확인
			//------------------------------------------------------------------------------------------------
			if (!"".equals(szPlateId)) {

				// ------------------------------------------------------------------------
				// 2.1. 크레인 작업지시 존재여부 확인
				// ------------------------------------------------------------------------
				rsResult = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
				recPara  = JDTORecordFactory.getInstance().create();
				tempRec  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 		szPlateId);             	// 재료번호
				recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);

				intRtnVal = ydCrnSchDao.getExistByStlNo(recPara, rsResult);
				if (intRtnVal <= 0) {
					szMsg    = "["+szOperationName+"] 해당 재료"+szPlateId+"로 크레인 작업지시 미존재! .... SKIP";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				} else {
					rsResult.first();
					tempRec = rsResult.getRecord();
					szYdWrkProgStat = ydDaoUtils.paraRecChkNull(tempRec, "YD_WRK_PROG_STAT");
					szYdCrnSchId 	= ydDaoUtils.paraRecChkNull(tempRec, "YD_CRN_SCH_ID");

					if (!"W".equals(szYdWrkProgStat) && !"1".equals(szYdWrkProgStat)) {

						szRtnMsg = "재료번호[" + szPlateId + "]의 크레인작업지시[" + szYdCrnSchId + "]를 취소할수 없는 상태! " + szYdWrkProgStat;
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;

					}

					// 작업상태가 2,3이 아닌 경우 크레인 작업지시 취소 처리
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(tempRec, "YD_CRN_SCH_ID"));
					recPara.setField("YD_SCH_CD",     ydDaoUtils.paraRecChkNull(tempRec, "YD_SCH_CD"));
					recPara.setField("DEL_YN",        "N");
					recPara.setField("MODIFIER",      szModifier);

					// 크레인 스케줄취소 처리
					szMsg = "["+szOperationName+"] 작업지시 취소 시작";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					outRecord	= JDTORecordFactory.getInstance().create();
					ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
					outRecord 	= (JDTORecord)ejbConn.trx("cancelJPlateYdCrnSch", new Class[] { JDTORecord.class }, new Object[] { recPara });

					szMsg = "["+szOperationName+"] ---- 작업지시 취소 종료!! >>>> " + outRecord.toString();
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				}

				// ------------------------------------------------------------------------
				// 2.2. 작업예약 존재여부 확인
				// ------------------------------------------------------------------------
				rsResult = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
				recPara  = JDTORecordFactory.getInstance().create();
				tempRec  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 		szPlateId);             	// 재료번호
				recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);

				intRtnVal = ydWrkbookDao.getExistByStlNo(recPara, rsResult);
				if (intRtnVal <= 0) {
					szMsg    = "["+szOperationName+"] 해당 재료"+szPlateId+"로 작업예약 미존재! .... SKIP";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				} else {
					rsResult.first();
					tempRec = rsResult.getRecord();

					// 작업 예약 삭제 처리
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID",	ydDaoUtils.paraRecChkNull(tempRec, "YD_WBOOK_ID"));
					recPara.setField("MODIFIER",    szModifier);

					szMsg = "["+szOperationName+"] ---- 작업예약 취소 시작";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					outRecord	= JDTORecordFactory.getInstance().create();
					ejbConn 	= new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
					outRecord 	= (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { recPara });

					szMsg = "["+szOperationName+"] ---- 작업예약 취소 종료!! >>>> " + outRecord.toString();
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				}
			}

			//------------------------------------------------------------------------------------------------
			// 3. 야드재료 삭제 (BOOK-OUT시)
			//------------------------------------------------------------------------------------------------
			if ("2".equals(szOperationType)) {		// Book Out

	           	for(int ii=0; ii < iPlateCnt; ii++) {
					if (!"".equals(szPlateIds[ii])) {
				
						// ------------------------------------------------------------------------
						// 3.1. 저장위치 정보 조회
						// ------------------------------------------------------------------------
						szMsg = "[ " +szOperationName + "] 3.1.저장위치 정보 조회 START";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		
						rsResult = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
						recPara  = JDTORecordFactory.getInstance().create();
						tempRec  = JDTORecordFactory.getInstance().create();
						recPara.setField("STL_NO", 		szPlateIds[ii]);             	// 재료번호
						recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);
		
						intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsResult);
						if (intRtnVal <= 0) {
							szRtnMsg = "야드재료가 미존재 합니다 .... 재료번호:" + szPlateId;
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}
		
				        szYdStkColGp	= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_COL_GP");
				        szYdStkBedNo	= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_BED_NO");
		
						szMsg = "[ " +szOperationName + "] 3.1.저장위치 정보 조회 END >>>> " + szYdStkColGp + szYdStkBedNo;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		
						// ------------------------------------------------------------------------
						// 3.2. 현재위치가 RT인지 체크
						// ------------------------------------------------------------------------
						if (!"RT".equals(ydUtils.substr(szYdStkColGp,2,2))) {
							szRtnMsg = "현재위치가 RT가 아닙니다 .... 저장위치:" + szYdStkColGp + szYdStkBedNo;
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						//	return szRtnMsg;
						}
		
						// ------------------------------------------------------------------------
						// 3.3. 야드 L2 저장품재원 삭제전문 전송
						// ------------------------------------------------------------------------
						szMsg = "[ " +szOperationName + "] 3.2.야드L2 저장품제원 전문송신 START";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("JMS_TC_CD", 			"YDY2L002");                            // TC-CODE
						recPara.setField("YD_GP", 				JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
						recPara.setField("YD_STK_COL_GP", 		szYdStkColGp);            				// 야드적치열구분
						recPara.setField("YD_STK_BED_NO", 		szYdStkBedNo);    						// 야드적치BED번호
						recPara.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
						recPara.setField("STL_NO", 				szPlateIds[ii]);	        					// 재료번호
						recPara.setField("MSG_GP", 				"D");	        						// 전문구분
						szRtnMsg = ydDelegate.sendMsg(recPara);
		
						szMsg = "[ " +szOperationName + "] 야드L2 전문송신 END >>>> " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		
						if (!"".equals(szYdStkColGp)) {
						// ------------------------------------------------------------------------
						// 3.4. 조업 L3 저장위치변경정보 전송
						// ------------------------------------------------------------------------
			            szMsg    = "["+szOperationName+"] 3.3.1후판조업 저장위치변경정보 전송 ---- START";
			            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		
			            recPara = JDTORecordFactory.getInstance().create();
				        recPara.setField("MSG_ID", 				"YDPRJ011");
				        recPara.setField("YD_STK_COL_FR", 		szYdStkColGp);							// From적치열
				        recPara.setField("YD_STK_BED_FR", 		szYdStkBedNo);							// From적치BED
				        recPara.setField("YD_STK_COL_TO", 		"");									// TO적치열
				        recPara.setField("YD_STK_BED_TO", 		"");									// TO적치BED
				        recPara.setField("YD_EQP_WRK_SH", 		"1");									// 야드설비작업매수
				        recPara.setField("ARR_STL_NO", 			szPlateIds[ii]);
		
				        szRtnMsg = ydDelegate.sendMsg(recPara);
		
						szMsg = "["+szOperationName+"] 1후판조업 저장위치변경정보 전송 완료>>>>" + szRtnMsg;
			            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						}
		
						// ------------------------------------------------------------------------
						// 3.5. 재료정보 삭제처리
						// ------------------------------------------------------------------------
						szMsg = "[ " +szOperationName + "] 3.4. 재료정보 삭제처리";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("STL_NO",				szPlateIds[ii]);				// 재료번호
						recPara.setField("YD_GP",				JPlateYdConst.YD_GP_P_PLATE_YARD);
						recPara.setField("MODIFIER", 			szModifier);
		
						intRtnVal = ydStockDao.delYdStock(recPara);
						if (intRtnVal < 0) {
							szRtnMsg = "재료정보 삭제처리시 오류 발생! .... 오류코드 :" + Integer.toString(intRtnVal);
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						//	return szRtnMsg;
						}
		
						// ------------------------------------------------------------------------
						// 3.6. 저장위치 CLEAR
						// ------------------------------------------------------------------------
						szMsg = "[ " +szOperationName + "] 3.5. 저장위치 CLEAR";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		
						recPara  = JDTORecordFactory.getInstance().create();
						recPara.setField("STL_NO", 				szPlateIds[ii]);             // 재료번호
						recPara.setField("YD_STK_LYR_MTL_STAT", "C");             		// 야드적치단재료상태
						recPara.setField("YD_GP",				JPlateYdConst.YD_GP_P_PLATE_YARD);
						recPara.setField("MODIFIER", 			szModifier);
		
						intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
						if (intRtnVal < 0) {
							szRtnMsg = "저장위치 삭제 처리시 오류 발생! .... 오류코드 :" + Integer.toString(intRtnVal);
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						//	return szRtnMsg;
						}
					}
	           	}
						
						
	        }

			szMsg = "["+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }

		return JPlateYdConst.RETN_CD_SUCCESS;
	}
	
    /**
     * 오퍼레이션명 : 1후판정정 크레인 작업지시요구(2) (Y2YDL007)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
     */
    public String procY2CrnWrkOrdReq2(JDTORecord msgRecord)throws DAOException  {

    	JPlateYdDelegate   	ydDelegate   = new JPlateYdDelegate();

    	JDTORecord recCrnSch 		= JDTORecordFactory.getInstance().create();
    	JDTORecord recInPara 		= null;

        JDTORecordSet rsCrnSch 		= JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecordSet rsResult 		= JDTORecordFactory.getInstance().createRecordSet("temp");
		JDTORecordSet rsCrnInfo 	= null;

        int 	intRtnVal 			= 0;

        String 	szMsg              	= "";
        String 	szMethodName       	= "procY2CrnWrkOrdReq2";
        String 	szOperationName		= "1후판정정 크레인작업지시 요구(2)";

        String 	szEqpId             = "";
        String 	szYdWrkProgStat		= "";
        String	szModifier			= "";

        //스케쥴코드
        String 	szYdSchCd			= "";

        boolean blnRtnVal			= true;

        String 	szRtnMsg			= "";
        String 	szRcvTcCode			= ydUtils.getTcCode(msgRecord);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.12 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
          
        if (szRcvTcCode==null || "".equals(szRcvTcCode)) {
        	szMsg = "[ERROR] " + SZ_SESSION_NAME + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
        	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
        	return szMsg;
        }

        try {
        	//=============================================================
        	// Log 테이블 등록
        	//=============================================================
        	szMsg = "[1후판정정] 크레인작업지시 요구 수신";
       // 	ydUtils.putLogMsg("A", "", szMsg, "", "", "", "I", "A", "I", szRcvTcCode, SZ_SESSION_NAME, szMethodName);
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	szEqpId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
        	szModifier 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");
        	if ("".equals(szModifier)) {
            	szModifier 	= ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER", szRcvTcCode);
        	}

        	//------------------------------------------------------------------------------------------------------
        	// 야드설비상태 Check		수신받은  야드설비Id로 설비Table를 조회하여 야드설비상태를 Check하고 고장이면 return
        	//------------------------------------------------------------------------------------------------------
        	szMsg = "[" + szOperationName + "] 크레인설비[" + szEqpId + "] 상태 체크 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			rsCrnInfo 	= JDTORecordFactory.getInstance().createRecordSet("");
//---------------------------------------------------------------------------------------------
// 2024.12.12 Argument에 logId 추가 eqpStatCheck 신규 작성
//---------------------------------------------------------------------------------------------
//        	szRtnMsg 	= this.eqpStatCheck(szEqpId, rsCrnInfo);
        	szRtnMsg 	= this.eqpStatCheck(szEqpId, rsCrnInfo, logId);
//---------------------------------------------------------------------------------------------

        	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
        		szRtnMsg = "설비 상태 체크 시 오류발생 - 메세지 : " + szRtnMsg;
        		szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

        		this.sendErrCrnWrkOrdReqYdP(msgRecord, szRtnMsg);
        		return szRtnMsg;
        	}

        	szMsg = "[" + szOperationName + "] 크레인설비[" + szEqpId + "] 상태 체크 완료 - 메세지 : " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	//------------------------------------------------------------------------------------------------------
			// 스케줄 기준 체크 -- 현재 1후판정정야드는 스케줄코드를 SET하지 않고 작업지시를 요청한다.
        	//------------------------------------------------------------------------------------------------------
			szYdSchCd 	 = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if (!"".equals(szYdSchCd)) {
				blnRtnVal = JPlateYdCommonUtils.chkGetSchRule(szYdSchCd, rsResult);
				if (!blnRtnVal) {
					szRtnMsg = "스케줄 기준 체크 조회시 오류 발생!";
	        		szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

	        		this.sendErrCrnWrkOrdReqYdP(msgRecord, szRtnMsg);
					return szRtnMsg;
				}
				// 레코드 추출
				rsResult.first();
				JDTORecord recPara = rsResult.getRecord();
				// 스케줄 금지 유무
				String szYdSchProhExn = ydDaoUtils.paraRecChkNull(recPara,"YD_SCH_PROH_EXN");

				// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
				if ("Y".equals(szYdSchProhExn)) {
					szRtnMsg = "스케쥴코드(" + szYdSchCd + ")가 기동 금지 입니다";
	        		szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

	        		this.sendErrCrnWrkOrdReqYdP(msgRecord, szRtnMsg);
					return szRtnMsg;
				}
			}

        	//야드 작업 진행상태를 check한다.
			rsCrnSch  = JDTORecordFactory.getInstance().createRecordSet("temp");

//-------------------------------------------------------------------------------------------------------------------------
// 2024.21.12 chkWrkProgStat Method 1후판 정정, 2후판 정정 공동 사용으로 1후판 정정용으로  chkWrkProgStatYdP 신규 작성 
//    		intRtnVal = this.chkWrkProgStat(msgRecord, rsCrnSch);
    		intRtnVal = this.chkWrkProgStatYdP(msgRecord, rsCrnSch);
//-------------------------------------------------------------------------------------------------------------------------

    		if (intRtnVal == 0) {

        		szRtnMsg = "명령선택된 작업지시가 없음";
        		szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

        		this.sendErrCrnWrkOrdReqYdP(msgRecord, "");
				return szRtnMsg;

    		} else if (intRtnVal == -1) {

				szRtnMsg = "스케줄 기준 체크 조회시 오류 .. 오류코드 : " + Integer.toString(intRtnVal);
        		szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

        		this.sendErrCrnWrkOrdReqYdP(msgRecord, szRtnMsg);
				return szRtnMsg;
    		}

        	recCrnSch = JDTORecordFactory.getInstance().create();
        	recCrnSch.setRecord(rsCrnSch.getRecord(0));

        	szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WRK_PROG_STAT");

        	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 * 업무기준 : 1후판정정야드와 통합야드가 같은 로직을 사용하므로 1후판정정야드와 관련된 L2로만 전송 필요
			 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        	szMsg = "[크레인 작업지시] 작업지시 전문 전송 START >>>>";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	recInPara = JDTORecordFactory.getInstance().create();
    		// 작업지시 전문 전송 data setup
			recInPara.setField("MSG_ID", 			"YDY2L004V2"											);
        	recInPara.setField("YD_CRN_SCH_ID", 	ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID")	);
       		recInPara.setField("YD_WRK_PROG_STAT", 	szYdWrkProgStat											);
        	recInPara.setField("YD_WORD_DT",    	JPlateYdUtils.getCurDate("yyyyMMddHHmmss")				);
        	recInPara.setField("MODIFIER", 			"YDSYSTEM"												);
        	recInPara.setField("MSG_GP", 			"U"														);
        	recInPara.setField("YD_SCH_CD", 		ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD")		);
        	recInPara.setField("YD_GP", 			ydDaoUtils.paraRecChkNull(recCrnSch, "YD_GP")			);
        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.12 recInPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
        	recInPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
			
			szRtnMsg = ydDelegate.sendMsg(recInPara);

			szMsg = "[크레인 작업지시]크레인 작업지시 메세지 전송 완료 >>>> " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		} catch(Exception e) {
			return e.getMessage();
		}

		szMsg = "크레인 작업지시(" + szMethodName + ") 완료";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;

	} // end of procY2CrnWrkOrdReq2()
	
	

    /**
     * 오퍼레이션명 : 1후판전단 #2 열처리 L2시스템으로부터 Book-In/Book-Out요구 수신 [P8YDL501] 처리
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● JDTORecord msgRecord
     * @return ● nRtnVal
     * @throws ● DAOException
     */
	public String  procP8BookInOutReq(JDTORecord msgRecord) throws DAOException {

        // 레코드선언
        JDTORecordSet   rsResult    			= null;

        JDTORecord      inRec       			= null;
        JDTORecord      tempRec     			= null;
        JDTORecord      recPara     			= JDTORecordFactory.getInstance().create();
        JDTORecord      recSchPara  			= null;
        JDTORecord      recL2Para   			= null;

        // 변수 선언
        String  szMethodName                    = "procP8BookInOutReq";
        String  szRtnMsg                        = JPlateYdConst.RETN_CD_SUCCESS;
        String  szMsg                           = "";
        String  szOperationName                 = "1후판 전단 #2 열처리 Book-In/Book-Out 요구 수신";

        String  szYdGp                          = "";           // 야드구분
        String  szYdBayGp                       = "";           // 야드동구분

        String  szYdSchCd                       = "";           // 스케줄 코드
        String  szYdSchProhExn                  = "";           // 야드스케쥴금지유무
        String  szYdWrkCrnPrior                 = "1";          // 야드작업크레인우선순위
        String  szYdStkLyrNo[]                  = {"001","002","003","004","005","006","007","008","009"};      // 야드적치단 , RT는 무조건 1단
        String  szYdWrkCrn                      = "";           // 야드설비ID (NOT NULL이기 때문에 RULE에 등록된 설비ID SET)
        String  szYdWbookId                     = "";           // 작업예약ID 생성
        String  szYdStkColGp                    = "";
        String  szYdToLocGuide                  = "";
        String  szRegister                      = "";           // 등록자, MSG_ID
        String  szPillingDelStlNo[]             = null;         // 파일링 삭제 재료 보관용
        String  szPillingDelStlNoStkLyrNo[]     = null;         // 파일링 삭제 재료의 적치단 순서 보관용

        String  szFromLoc                       = "";
        String  szFromBed                       = "01";
        String  szToLoc                         = "";

        String  szSPARE                         = "";

        int     iDelStlNo                       = 0;            // 파일링 시, 삭제한 재료 건수
        int     intRtnVal                       = 0;
        int     iWBookInsCnt                    = 0;            // 작업예약등록 건수
        int     iCrnSchCnt                      = 0;            // 크레인스케쥴 호출 건수
        int     iSchOkCnt                       = 0;            // 스케줄호출 OK 건수

        boolean bAsRollMtl                     = false;       // AS Roll 재 구분

        StringBuffer sbARR_WBOOK_ID             = new StringBuffer();

        //DAO
        JPlateYdStockDAO    ydStockDao          = new JPlateYdStockDAO();
        JPlateYdWrkbookDAO  ydWrkbookDao        = new JPlateYdWrkbookDAO();
        JPlateYdCrnSchDAO   ydCrnSchDao         = new JPlateYdCrnSchDAO();
        JPlateYdStkLyrDAO   ydStkLyrDao         = new JPlateYdStkLyrDAO();
        JPlateYdSchRuleDAO  ydSchRuleDao        = new JPlateYdSchRuleDAO();

        JPlateYdCommDAO     commDao             = new JPlateYdCommDAO();
        JPlateYdEqpDAO      ydEqpDao            = new JPlateYdEqpDAO();


        String logId                            = ydLogUtils.getJDTOLogId(msgRecord, "P");  // JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId     = ydLogUtils.getLogIdNew("P");              // log id 가 비어있는경우 새로 1후판 정정 log id 새로 발번

        try {
            szMsg = "[" + szOperationName + "] ---- 메소드 시작  >>>> " + msgRecord.toString();
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

            /*
            7   OPERATION_MODE          OPERATION_TYPE              CHAR    1
            8   PIECE_ID                후판L2제품번호             	CHAR    16
            9   PPS_PIECE_ID            후판재료번호               	CHAR    32
            10  PIECE_LENGTH            후판제촌제품길이            CHAR    5
            11  PIECE_WIDTH             후판제촌제품폭              CHAR    6
            12  PIECE_THICKNESS         후판제촌제품두께            CHAR    7
            13  TRACK_ZONE_DESIG        후판트래킹존지정            CHAR    5
            14  BOOKIN_CONTI_MODE       후판북아웃모드              CHAR    1
            15  CRANE_NO                Crane_No                    CHAR    2
            16  YARD_NO                 Yard_No                     CHAR    6
            17  BED_NO                  BED_NO                      CHAR    2
            18  REASON_CODE             REASON_CODE                 CHAR    3
            19  NEXT_PROCESS            NEXT_PROCESS                CHAR    1
            20  PILNG_WRK_GP            파일링작업구분              CHAR    1
            21  PL_MTL_NO2              2단재료번호                 CHAR    10
            22  PL_MTL_NO3              3단재료번호                 CHAR    10
            23  CRANE_SEND_FLAG         Crane Send Flag (Zone 10)   CHAR    1
            24  SPARE_ARRAY             SPARE                       CHAR    58
            */

            String szRcvTcCode          = ydUtils.getTcCode(msgRecord);
            String szPlateId[]          = {"","","","","","","","",""};
            int iPlateCnt               = 0;

            String szOperation_Mode     = ydDaoUtils.paraRecChkNull(msgRecord, "OPERATION_MODE"		);      // 1:Book In, 2:Book Out
            String szPiece_Id           = ydDaoUtils.paraRecChkNull(msgRecord, "PIECE_ID"			);      // 후판L2제품번호

            szPlateId[0]                = ydDaoUtils.paraRecChkNull(msgRecord, "PPS_PIECE_ID"		);      // PLATE_NO 1단재료번호

            String szTrack_Zone_Desig   = ydDaoUtils.paraRecChkNull(msgRecord, "TRACK_ZONE_DESIG"	);     	// TRACK_ZONE_DESIG
            String szBookin_Conti_Mode  = ydDaoUtils.paraRecChkNull(msgRecord, "BOOKIN_CONTI_MODE"	);    	// 1:one time 2:Start 3:End
            String szCraneNo            = ydDaoUtils.paraRecChkNull(msgRecord, "CRANE_NO"			);      // 북인대상재 구분 'A1' : 북인대상재
            String szYard_No            = ydDaoUtils.paraRecChkNull(msgRecord, "YARD_NO"			);      // YARD_NO
            String szBed_No             = ydDaoUtils.paraRecChkNull(msgRecord, "BED_NO"				);      // 파일링작업구붑
            String szReason_Code        = ydDaoUtils.paraRecChkNull(msgRecord, "REASON_CODE"		);      // Book-Out원인 (888:TEST , 999:북아웃취소)
            String szNext_Process       = ydDaoUtils.paraRecChkNull(msgRecord, "NEXT_PROCESS"		);
            String szPilng_Wrk_Gp       = ydDaoUtils.paraRecChkNull(msgRecord, "PILNG_WRK_GP"		);      // 파일링작업구분 (Y:파일링작업,N:1매작업)

            szPlateId[1]                = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO2"			);      // 2단재료번호
            szPlateId[2]                = ydDaoUtils.paraRecChkNull(msgRecord, "PL_MTL_NO3")		;       // 3단재료번호

            String szCrane_Send_Flag    = ydDaoUtils.paraRecChkNull(msgRecord, "CRANE_SEND_FLAG"	);      // Crane Send Flag (Zone 10)

            
            szMsg = "\n\t OPERATION_MODE    : " 	+ szOperation_Mode 
            	  + "\n\t PIECE_ID          : " 	+ szPiece_Id 
            	  + "\n\t PPS_PIECE_ID      : " 	+ szPlateId[0] 
            	  + "\n\t TRACK_ZONE_DESIG  : " 	+ szTrack_Zone_Desig 
            	  + "\n\t BOOKIN_CONTI_MODE : " 	+ szBookin_Conti_Mode 
            	  + "\n\t CRANE_NO          : " 	+ szCraneNo 
            	  + "\n\t YARD_NO           : " 	+ szYard_No 
            	  + "\n\t BED_NO            : " 	+ szBed_No 
            	  + "\n\t REASON_CODE       : " 	+ szReason_Code 
            	  + "\n\t NEXT_PROCESS      : " 	+ szNext_Process 
            	  + "\n\t PILNG_WRK_GP      : " 	+ szPilng_Wrk_Gp 
            	  + "\n\t PL_MTL_NO2        : " 	+ szPlateId[1] 
            	  + "\n\t PL_MTL_NO3        : " 	+ szPlateId[2] 
            	  + "\n\t CRANE_SEND_FLAG   : " 	+ szCrane_Send_Flag;
            
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

            boolean bPillingModBookOut = false;  // 파일링 북아웃모드 여부 체크
            if("2".equals(szOperation_Mode) && "Y".equals(szPilng_Wrk_Gp)) {
                bPillingModBookOut 	= true;

                szMsg="파일링 북아웃 모드 설정";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
            }

            boolean bPillingModBookIn = false;   // 파일링 북인모드 여부 체크
            if("1".equals(szOperation_Mode) && "Y".equals(szPilng_Wrk_Gp)) {
                bPillingModBookIn		= true;

                szMsg="파일링 북인 모드 설정";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
            }

            String szOperationDate  = JPlateYdUtils.getCurDate("yyyyMMdd");
            szRegister      = ydDaoUtils.paraRecModifier(msgRecord);                            // 등록자, 수정자
            if ("".equals(szRegister)) {
                szRegister = szRcvTcCode;
            }
            
            szMsg    = "[" + szOperationName + "] szRegister:" + szRegister;
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

            // BOOKIN_CONTI_MODE 항목 체크
            if ( !"1".equals(szBookin_Conti_Mode) && !"2".equals(szBookin_Conti_Mode) && !"3".equals(szBookin_Conti_Mode) ) {
                szRtnMsg = "수신전문중 BOOKIN_CONTI_MODE ERROR";
                szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                return szRtnMsg;
            }


            
			// 파일링 코드일 경우, 대상 크레인 체크처리
			if(bPillingModBookIn || bPillingModBookOut)
			{
				//1.대상 스케줄 코드, 지정 설비 존재 여부 확인.
				
				szYdSchProhExn  = "";  			// 야드스케쥴금지유무
				//szYdWrkCrnPrior = "1"; 		// 야드작업크레인우선순위
				szYdWrkCrn      = "";			// 야드설비ID (NOT NULL이기 때문에 RULE에 등록된 설비ID SET)

				recPara  = JDTORecordFactory.getInstance().create();
				rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
				
				if(bPillingModBookOut){
					recPara.setField("YD_SCH_CD", "PFRTAPLM");
				}
				else{
					recPara.setField("YD_SCH_CD", "PFRTAPUM");
				}

				// 야드스케쥴금지유무, 대상 지정 설비 조회
				//ydSchRuleDao.getYdSchrule(recPara, rsResult);
				ydSchRuleDao.getYdSchruleWithEqp(recPara, rsResult);

				if (rsResult != null && rsResult.size() > 0) {
					//szYdSchProhExn  = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_SCH_PROH_EXN");
					// 야드작업크레인우선순위 : 야드스케쥴코드에 해당하는 작업크레인의 우선순위이므로
					
					//// SCHRULE이 아닌 추후 크레인 설비 지정 데이터에서 SELECT 변경필요
					szYdWrkCrn			= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_CRN");
					String szYdUseYn	= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_USE_YN");
					
					if("".equals(szYdWrkCrn) || "N".equals(szYdUseYn)){  // 설비 미할당시 error return
						szRtnMsg = "입력받은 스케줄코드에 할당받은 설비 미존재 ";
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
					}
					
					rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
					tempRec   = JDTORecordFactory.getInstance().create();
					tempRec.setField("YD_EQP_ID", szYdWrkCrn);
					
					intRtnVal = ydEqpDao.getYdEqp(tempRec, rsResult);

					if (intRtnVal <= 0) {
	    				szRtnMsg = "해당 설비 조회시 오류발생 .. 설비ID :: " + szYdWrkCrn;
	    				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
	    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    				return szRtnMsg;
					}
					else{
						String szYD_EQP_STAT=ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_EQP_STAT");
						
						//설비 상태가 권상지시, 권하지시, 고장 상태일 경우 error return
						  
						if(JPlateYdConst.YD_EQP_STAT_BREAK.equals(szYD_EQP_STAT) ){
			    				szRtnMsg = "해당 설비 상태 조회시 사용 불가.. 설비ID :: " + szYdWrkCrn + "설비 상태코드 ::" + szYD_EQP_STAT;
			    				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			    				return szRtnMsg;
							}
					}
				}
				else{  //기준 스케줄코드가 없을 경우 error return
					szRtnMsg = "입력받은 스케줄코드가 기준정보 내 미존재. ";
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
			}

            
            
            // 파일링작업구분(PILNG_WRK_GP) = "Y" 이면 매수 Count 
            if("Y".equals(szPilng_Wrk_Gp)) {
                for(int ii=0; ii < szPlateId.length; ii++) {
                    if(!"".equals(szPlateId[ii])) {
                        iPlateCnt++;
                    }
                }
            } else {
                iPlateCnt    = 1;
            }

            
            //------------------------------------------------------------------------------------------------
            // 1. 파라미터 체크
            // 1.1 북인   시 trkZoneNo -> to위치    ,   yardNo -> from 위치
            // 1.1 북아웃 시 trkZoneNo -> from 위치 ,   yardNo -> to위치
            //------------------------------------------------------------------------------------------------
            if ("1".equals(szOperation_Mode)) { 		// Book In

                szFromLoc   = szYard_No;
                szToLoc     = szTrack_Zone_Desig;

                // From 위치
                if (szYard_No == null || szYard_No.length() < 6) {

                    for(int ii=0; ii < iPlateCnt; ii++) {
                        // 저장위치 없이 판번호만 들어 왔을때 처리
                        if (!"".equals(szPlateId[ii])) {

                            szMsg    = "[" + szOperationName + "] 저장위치 없이 판번호만 들어 왔을때 .... SKIP";
                            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                        } else {
                            szRtnMsg = "수신전문중 FROM위치 ERROR >>>> " + szYard_No;
                            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                            return szRtnMsg;
                        }
                    }
                }

                // From 위치가 1후판 정정 공장(P) 아니면 오류
                if (szYard_No.length() > 1) {
                    if (!"P".equals(ydUtils.substr(szYard_No, 0, 1))) {
                        szRtnMsg = "수신전문중 FROM위치 ERROR >>>>>>> " + szYard_No;
                        szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                        return szRtnMsg;
                    }
                }

            
                // To 위치
                // TRK ZONE NO를 야드저장위치로 변경
                if (!"".equals(szTrack_Zone_Desig) && szTrack_Zone_Desig.length() == 5) {
                    // ZONE NO를 야드저장위치로 변경 :: 2300 >>>> FART01
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.15 기존 Hashtable 사용 부분을 TB_YD_RULE 사용으로 변경                 	
//-------------------------------------------------------------------------------------------------------------------------
//                    szToLoc = JPlateYdCommonUtils.getY2RtZoneToLoc(szTrack_Zone_Desig);
                    szToLoc = JPlateYdCommonUtils.selY2RtZoneToLoc(szTrack_Zone_Desig);
                }
                
                if (szToLoc == null || szToLoc.equals("")) {
                    szRtnMsg = "수신전문중 TO위치 ERROR >>>>>>> " + szTrack_Zone_Desig;
                    szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                    return szRtnMsg;
                }
                
                // To 위치가 1후판 정정 공장(P) 아니면 오류
                if (!"P".equals(ydUtils.substr(szToLoc, 0, 1))) {
                    szRtnMsg = "수신전문중 TO위치 ERROR >>>>>>> " + szTrack_Zone_Desig;
                    szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                    return szRtnMsg;
                }


            } else if ("2".equals(szOperation_Mode)) {       // Book Out

                szFromLoc   = szTrack_Zone_Desig;
                szToLoc     = szYard_No;
                
                for(int ii=0; ii < iPlateCnt; ii++) {

                    // PLATE_ID
                    // BOOK-OUT일때는 2,3의 경우는 미존재함
                    if ("".equals(szPlateId[ii])) {
                        szRtnMsg = "수신전문중 PLATE_ID ERROR";
                        szMsg    = "[" + szOperationName+"] " + szRtnMsg;
                        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                        return szRtnMsg;
                    }
                }

                // from 위치

                // TRK ZONE NO를 야드저장위치로 변경
                if (!"".equals(szTrack_Zone_Desig) && szTrack_Zone_Desig.length() == 5) {
                    // ZONE NO를 야드저장위치로 변경 :: 2300 >>>> FART01
                    szFromLoc = JPlateYdCommonUtils.selY2RtZoneToLoc(szTrack_Zone_Desig);
                }

                szMsg    = "[" + szOperationName + "]  >>>> szFromLoc :: " + szFromLoc;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                
                if (szFromLoc == null || szFromLoc.equals("")) {
                    szRtnMsg = "수신전문중 FROM위치 ERROR >>>>>>> " + szTrack_Zone_Desig;
                    szMsg    = "[" + szOperationName+"] " + szRtnMsg;
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                    return szRtnMsg;
                }

                // From 위치가 1후판 정정 공장(P) 아니면 오류
                if (!"P".equals(ydUtils.substr(szFromLoc, 0, 1))) {
                    szRtnMsg = "수신전문중 FROM위치 ERROR >>>>>>> " + szTrack_Zone_Desig;
                    szMsg    = "[" + szOperationName+"] " + szRtnMsg;
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                    return szRtnMsg;
                }


                String sToBay = ydUtils.substr(szToLoc,   1, 1);
                String sFrBay = ydUtils.substr(szFromLoc, 1, 1);

                // BOOK-OUT시 BED는 '01' 임 (FROM위치가 RT임으로)
                szBed_No = "01";
            }
            
            // Book In 시 저장위치 조회
            if ("1".equals(szOperation_Mode) && !bPillingModBookIn) {  // 파일링모드의 북인일경우는 해당 로직 제외(신규 파일링 재료가 적치단에 없으므로)

                for(int ii=0; ii < iPlateCnt; ii++) {
                    if ("".equals(szPlateId[ii])) {         // 재료번호 미존재시

                        szYdStkColGp = szFromLoc;

                        szMsg    = "[" + szOperationName + "] 북인작업중 .... 재료번호 미존재 >>>> 저장위치 :: " + szYdStkColGp;
                        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                    } else {
                        // 베드정보 조회
                        recPara.setField("STL_NO",  szPlateId[ii]						);                 // 재료번호
                        recPara.setField("YD_GP",   JPlateYdConst.YD_GP_P_PLATE_YARD	);
                        rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStockWithLoc", logId, szMethodName, "베드정보 조회-야드재료가 존재하는지 확인");
                        if(rsResult.size() < 1) {
                            szRtnMsg = "야드재료가 미존재 합니다 .... 재료번호:" + szPlateId[ii];
                            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                            return szRtnMsg;
                        }

                        rsResult.first();
                        tempRec = JDTORecordFactory.getInstance().create();
                        tempRec = rsResult.getRecord();
                        
                        // 야드적치열구분, 야드적치Bed번호, 야드적치단번호
                        szYdStkColGp    = StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"),"");
                        szBed_No  		= StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_STK_BED_NO"),"");
                        szYdStkLyrNo[ii]= StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_STK_LYR_NO"),"");
                    }
                }

                // 재료 저장위치와 Book-In TO 저장위치 공장구분,동  같은지 체크
                String sToBay = ydUtils.substr(szToLoc, 0, 2);
                if (!ydUtils.substr(szYdStkColGp,0,2).equals(sToBay)) {
                    szRtnMsg = "해당동의 재료만 Book-IN 가능! .... 재료위치:: " + szYdStkColGp + ", Book-IN위치:: " + szToLoc;
                    szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                    return szRtnMsg;
                }
            }

                                                      

            //------------------------------------------------------------------------------------------------
            // 1.2. 해당 재료 작업예약/스케쥴 존재여부 확인
            //------------------------------------------------------------------------------------------------
            for(int ii=0; ii < iPlateCnt; ii++) {
            	
                if (!"".equals(szPlateId[ii])) {
                	
                    // ------------------------------------------------------------------------
                    // 1.2.1. 작업예약 존재여부 확인 (SELECT TB_YD_WRKBOOK, TB_YD_WRKBOOKMTL)
                    // ------------------------------------------------------------------------
                    recPara  = JDTORecordFactory.getInstance().create();

                    recPara.setField("STL_NO",      szPlateId[ii]						);                 // 재료번호
                    recPara.setField("YD_GP",       JPlateYdConst.YD_GP_P_PLATE_YARD	);

                    rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getExistByStlNo", logId, szMethodName, "작업예약 존재여부 확인");

                    if (rsResult.size() > 0) {
                        szRtnMsg = "해당 재료[" + szPlateId[ii] + "]로 작업예약이 존재!";
                        szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                        
                        return szRtnMsg;
                    }


                    // ------------------------------------------------------------------------
                    // 1.2.2. 크레인 작업지시 존재여부 확인 (SELECT TB_YD_CRNSCH, TB_YD_CRNWRKMTL)
                    // ------------------------------------------------------------------------
                    recPara  = JDTORecordFactory.getInstance().create();

                    recPara.setField("STL_NO",      szPlateId[ii]						);                 // 재료번호
                    recPara.setField("YD_GP",       JPlateYdConst.YD_GP_P_PLATE_YARD	);

                    rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getExistByStlNo", logId, szMethodName, "크레인 작업지시 존재여부 확인");

                    if (rsResult.size() > 0) {
                        szRtnMsg = "해당 재료"+szPlateId[ii]+"로 크레인 작업지시 존재!";
                        szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
  
                        return szRtnMsg;
                    }

                    //------------------------------------------------------------------
                    // 1.2.3. 현재 저장위치가 1후판 정정야드가 아닐경우 오류로 처리
                    //------------------------------------------------------------------
                    
//---------------------------------------------------------------------------------------------
// 2024.11.20 Argument에 logId 추가 checkUpdYdLocYdP 신규 작성
//                  szRtnMsg = JPlateYdCommonUtils.checkUpdYdLocYdP(szPlateId[ii], JPlateYdConst.YD_GP_P_PLATE_YARD, "N");
                    szRtnMsg = JPlateYdCommonUtils.checkUpdYdLocYdP(szPlateId[ii], JPlateYdConst.YD_GP_P_PLATE_YARD, "N", logId);
//---------------------------------------------------------------------------------------------
                    
                    if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
                        szMsg = "[ " + szOperationName + "] 북아웃시 저장위치 확인 오류! >>>> " + szRtnMsg;
                        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                        return szRtnMsg;
                    }
                }
            }

            
            //------------------------------------------------------------------------------------------------
            // 2. 야드재료 등록 (BOOK-OUT시)
            //------------------------------------------------------------------------------------------------
            if ("2".equals(szOperation_Mode) || bPillingModBookIn) {		// Book Out  //파일링 BOOK-IN 모드시에도 야드재료 등록될 수 있도록 추가

                //------------------------------------------------------------------------------------------------
                // 2.1. 야드재료 조회 (SELECT TB_YD_SHRSTOCK, TB_YD_STKLYR, VW_YD_SHRSTOCK)
                //------------------------------------------------------------------------------------------------
                szMsg    = "[" + szOperationName + "] ----------- 야드재료 조회 START ";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                // 레코드 편성
                for(int ii=0; ii < iPlateCnt; ii++) {

                    recPara.setField("STL_NO",  	szPlateId[ii]						);                 // 재료번호
                    recPara.setField("YD_GP",       JPlateYdConst.YD_GP_P_PLATE_YARD	);
                    rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStockWithLoc", logId, szMethodName, "야드재료 조회");
                    if (rsResult.size() > 0) {

                        szYdStkColGp = StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"),"");

                        if ("".equals(szYdStkColGp) || "RT".equals(ydUtils.substr(szYdStkColGp, 2, 2))) {

                            szMsg    = "[" + szOperationName + "] 해당 재료의 저장위치 :: " + szYdStkColGp + " 계속 진행 ";
                            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                        } else {
                            szRtnMsg = "야드재료가 이미 존재합니다 .... 현위치::" + szYdStkColGp;
                            szMsg    = "[" + szOperationName+"] " + szRtnMsg;
                            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                            //return szRtnMsg;
                        }
                    }
                }
                
                //------------------------------------------------------------------------------------------------
                // 2.3. 야드재료 등록 (INSERT OR UPDATE TB_YD_SHRSTOCK)
                //------------------------------------------------------------------------------------------------
                szMsg    = "[" + szOperationName + "] ----------- 야드재료 등록 START ";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.15 TRACK_ZONE_DESIG 자리수가 5 인데 6자리 저장위치가 들어올수 있는지 검토 필요
//-------------------------------------------------------------------------------------------------------------------------
                String tmpTrkZoneNo = szTrack_Zone_Desig;  // szTrack_Zone_Desig 여기에 59020이런식으로 안들어오고 PFRT60 이런식으로 들어올 경우, 다시 59020으로 치환해서 저장필요.20230602
                if(tmpTrkZoneNo.length()==6){	// 트랙킹존 방식으로 치환
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.15 기존 Hashtable 사용 부분을 TB_YD_RULE 사용으로 변경
//-------------------------------------------------------------------------------------------------------------------------
//                	tmpTrkZoneNo=JPlateYdCommonUtils.getLocToY2RtZone(szTrack_Zone_Desig);  //PFRT60->59020
                	tmpTrkZoneNo = JPlateYdCommonUtils.selLocToY2RtZone(szTrack_Zone_Desig);  //PFRT60->59020

                    szMsg    = "L2 트랙킹존 형식으로 치환 결과:" + szTrack_Zone_Desig + "=>" + tmpTrkZoneNo;
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
                }


                for(int ii=0; ii < iPlateCnt; ii++) {

                    recPara.setField("REGISTER"             ,szRegister								);      // 등록자
                    recPara.setField("STL_NO"               ,szPlateId[ii]							);      // 재료번호
                    recPara.setField("BOOK_OUT_RESN"        ,szReason_Code							);  	// Book-Out원인
                    recPara.setField("BOOK_OUT_DATE"        ,JPlateYdUtils.getCurDate("yyyyMMdd")	); 		// Book-Out일자
                    recPara.setField("BOOK_OUT_PROG"        ,""										);      // Book-Out공정
                    recPara.setField("FRTOMOVE_PLANT_GP"    ,szCraneNo								);      // 북인대상재 구분 (이송공장구분 항목사용)
                    recPara.setField("ARR_WLOC_CD"          ,szTrack_Zone_Desig						);      // 후판트래킹존지정 chito20230202  //tmpTrkZoneNo 이걸로 대체
                    recPara.setField("YD_FRTOMOVE_YD_GP"    ,szBookin_Conti_Mode					);      // 북아웃모드    chito20230202

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 recPara에 logId 추가 
                    recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
                    
                    intRtnVal = ydStockDao.insYdStockBookOut(recPara);
                    if (intRtnVal <= 0) {
                        szRtnMsg = "재료정보 미존재로 오류 발생 .. " + szPlateId[ii] + ", 오류코드::" + Integer.toString(intRtnVal);
                        szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                        return szRtnMsg;
                    }
                }
                
                
                //------------------------------------------------------------------------------------------------
                // 2.4. 해당 재료 적치위치 비우기 (UPDATE TB_YD_STKLYR)
                //------------------------------------------------------------------------------------------------
                szMsg    = "[" + szOperationName + "] ----------- 해당 재료 적치위치 비우기 START";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                for(int ii=0; ii < iPlateCnt; ii++) {
                    recPara.setField("STL_NO",              szPlateId[ii]						);      // 재료번호
                    recPara.setField("YD_STK_LYR_MTL_STAT", "C"									);		// 야드적치단재료상태
                    recPara.setField("YD_GP",               JPlateYdConst.YD_GP_P_PLATE_YARD	);
                    recPara.setField("MODIFIER",            szRegister							);

                    intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrClearByStlNo", logId, szMethodName, "해당 재료 적치위치 비우기");

                    if (intRtnVal < 0) {
                        szRtnMsg = "저장위치 삭제 처리시 오류 발생!1 .... 오류코드 :" + Integer.toString(intRtnVal);
                        szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                    }
                }


                //------------------------------------------------------------------------------------------------
                // 2.4. 적치단 수정 (SELECT  TB_YD_STKLYR)
                //------------------------------------------------------------------------------------------------
                szMsg    = "[" + szOperationName + "] ----------- 적치단 야드적치단재료상태 수정 START";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);


                
                szFromBed = "01";

                int MaxYdStkLyrNo=0;                //파일링모드용 신규 추가: 재료 적치단수의 max 값 파악용도
                
                // 적치단 야드적치단재료상태 수정 (UPDATE TB_YD_STKLYR)
                recPara = JDTORecordFactory.getInstance().create();
                for(int ii=0; ii < iPlateCnt; ii++) {
                	
                    recPara.setField("YD_STK_COL_GP",       szFromLoc			);      // 야드적치열구분
                    recPara.setField("YD_STK_BED_NO",       szFromBed			);      // 야드적치BED번호
                    recPara.setField("YD_STK_LYR_NO",       szYdStkLyrNo[ii]	);		// 야드적치단
                    recPara.setField("STL_NO",              szPlateId[ii]		);      // 재료번호
                    recPara.setField("YD_STK_LYR_MTL_STAT", "C"					);      // 적치완료
                    recPara.setField("MODIFIER",            szRegister			);      // 등록자

                    intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat", logId, szMethodName, "적치단 야드적치단재료상태 수정");

                    if (intRtnVal <= 0) {
                        szRtnMsg = "야드적치단재료상태 수정 ERROR .. " + Integer.toString(intRtnVal);
                        szMsg    = "[" + szOperationName+"] " + szRtnMsg;
                        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                        return szRtnMsg;
                    }
                    if(MaxYdStkLyrNo<Integer.parseInt(szYdStkLyrNo[ii])){
                        MaxYdStkLyrNo=Integer.parseInt(szYdStkLyrNo[ii]);    //(파일링 모드용)쌓인 적치단 번호 중 최대값 저장
                    }
                }

                
				// (파일링 모드용)삭제한 이전 야드맵 재료 추가 (UPDATE TB_YD_STKLYR)
                //if("PILLING".equals(szDEV_INPUT1)){  //삭제한 이전 야드맵 재료 추가
                if(bPillingModBookIn || bPillingModBookOut){  // (파일링 모드용)삭제한 이전 야드맵 재료 추가 (UPDATE TB_YD_STKLYR)
                    for(int ii=0; ii<iDelStlNo; ii++){
                        recPara.setField("YD_STK_COL_GP",       szFromLoc						);      // 야드적치열구분
                        recPara.setField("YD_STK_BED_NO",       szFromBed						);      // 야드적치BED번호
                        recPara.setField("YD_STK_LYR_NO",       szYdStkLyrNo[ii+MaxYdStkLyrNo]	);		// 야드적치단
                        recPara.setField("STL_NO",              szPillingDelStlNo[ii]			);      // 재료번호
                        recPara.setField("YD_STK_LYR_MTL_STAT", "C"								);      // 적치완료
                        recPara.setField("MODIFIER",            szRegister						);      // 등록자

                        intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat", logId, szMethodName, "적치단 야드적치단재료상태 수정");

                        if (intRtnVal <= 0) {
                            szRtnMsg = "(파일링)야드적치단재료상태 수정 ERROR .. " + Integer.toString(intRtnVal);
                            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                            return szRtnMsg;
                        }
                    }
                }

                
                //------------------------------------------------------------------------------------------------
                // 2.5. 야드재료 등록후 L2에 저장품제원 정보 송신 .. 작업지시 송신 이전에 전송해야함
                //      야드L2 전문송신 (저장품제원 :: YDY2L002 전송)
                //------------------------------------------------------------------------------------------------
                szMsg = "[ " + szOperationName + "] 야드L2 저장품제원 전문송신 START";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                recL2Para = JDTORecordFactory.getInstance().create();
                for(int ii=0; ii < iPlateCnt; ii++) {
                    recL2Para.setField("JMS_TC_CD",         "YDY2L002"							);      // TC-CODE
                    recL2Para.setField("YD_GP",             JPlateYdConst.YD_GP_P_PLATE_YARD	);      // 야드구분
                    recL2Para.setField("YD_STK_COL_GP",     szFromLoc							);      // 야드적치열구분
                    recL2Para.setField("YD_STK_BED_NO",     szFromBed							);      // 야드적치BED번호
                    recL2Para.setField("YD_INFO_SYNC_CD",   "4"									);      // 야드정보동기화코드  [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
                    recL2Para.setField("STL_NO",            szPlateId[ii]						);      // 재료번호

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.15 "isPillingModBookIn" 는 JPlateYdMakeTcY2.java makeYDY2L002 Method에서 사용하는 항목
//-------------------------------------------------------------------------------------------------------------------------
                    if(bPillingModBookIn) {
                        recL2Para.setField("isPillingModBookIn",            "Y");                       // 파일링 북인모드 여부 조회(북인모드시 마지막장만 전송)
                    }    
                    else {
                        recL2Para.setField("isPillingModBookIn",            "N");
                    }

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 recL2Para에 logId 추가 
                    recL2Para.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
                    
                    szRtnMsg = ydDelegate.sendMsg(recL2Para);
                }
                
                szMsg = "[ " + szOperationName + "] 야드L2 저장품제원 전문송신 END >>>> " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                
				//(파일링) 이전 재료번호 야드재료 등록후 L2에 저장품제원 정보 송신 
				szMsg = "[ " + szOperationName + "] (이전 파일링 재료) 야드L2 저장품제원 전문송신 START";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                //if("PILLING".equals(szDEV_INPUT1)){  //삭제한 이전 야드맵 재료 추가
                //if(bPillingModBookIn || bPillingModBookOut){  //(파일링 모드 전용)삭제한 이전 야드맵 재료 추가(북아웃/북인 공통)
                if(bPillingModBookOut){  //(파일링 모드 전용)삭제한 이전 야드맵 재료 추가(북아웃/북인 공통)

                    // (파일링) 이전 재료번호 야드재료 등록후 L2에 저장품제원 정보 송신
                    szMsg = "[ " + szOperationName + "] (이전 파일링 재료) 야드L2 저장품제원 전문송신 START";
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
                	
                	for(int ii=0; ii<iDelStlNo; ii++){
                        recL2Para.setField("JMS_TC_CD",         "YDY2L002"							);      // TC-CODE
                        recL2Para.setField("YD_GP",             JPlateYdConst.YD_GP_P_PLATE_YARD	);      // 야드구분
                        recL2Para.setField("YD_STK_COL_GP",     szFromLoc							);      // 야드적치열구분
                        recL2Para.setField("YD_STK_BED_NO",     szFromBed							);      // 야드적치BED번호
                        recL2Para.setField("YD_INFO_SYNC_CD",   "4"									);      // 야드정보동기화코드  [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
                        recL2Para.setField("STL_NO",            szPillingDelStlNo[ii]				);      // 재료번호
                        
                        szRtnMsg = ydDelegate.sendMsg(recL2Para);
                    }

                    szMsg = "[ " + szOperationName + "] (이전 파일링 재료) 야드L2 저장품제원 전문송신 END >>>> " + szRtnMsg;
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
                }
                
            }

            
            
            /**********************************************************
            * 3. 스케줄기준 정보 Check
            **********************************************************/
            if ("".equals(szFromLoc)) {
                szYdGp    = ydUtils.substr(szToLoc, 0, 1	);      // 야드구분
                szYdBayGp = ydUtils.substr(szToLoc, 1, 1	);      // 야드동구분
            } else {
                szYdGp    = ydUtils.substr(szFromLoc, 0, 1	);    	// 야드구분
                szYdBayGp = ydUtils.substr(szFromLoc, 1, 1	);    	// 야드동구분
            }

//-------------------------------------------------------------------------------------------------------------------------
// #2 열처리 야드스케쥴코드
//    PART31LM	:	A동 #2 열처리 입측 BOOK-OUT(0031N)
//    PART32LM	:	A동 #2 열처리 입측 BOOK-OUT(0032N)
//    PART34LM	:	A동 #2 열처리 출측 BOOK-OUT(0034N)
//    PART35LM	:	A동 #2 열처리 출측 BOOK-OUT(0035N)
//  
//    PART31UM	:	A동 #2 열처리 입측 BOOK-IN(0031N)
//    PART32UM	:	A동 #2 열처리 입측 BOOK-IN(0032N)
//    PART34UM	:	A동 #2 열처리 출측 BOOK-IN(0034N)
//    PART35UM	:	A동 #2 열처리 출측 BOOK-IN(0035N)
//-------------------------------------------------------------------------------------------------------------------------

            if ("1".equals(szOperation_Mode)) {              // Book IN
//            //  szYdSchCd   = szYdGp + szYdBayGp + "RT0?UM";
//                szYdSchCd   = ydUtils.getRtSchCdYdP(szToLoc, "UM");
//                if(bPillingModBookIn){
//                    szYdSchCd	= "PFRTAPUM";  // 파일링 북인모드 전용 스케줄코드
//                }
                
                szYdSchCd   = ydUtils.substr(szToLoc, 0, 6	) + "UM";
                
            } else if ("2".equals(szOperation_Mode)) {       // Book Out
//            // szYdSchCd   = szYdGp + szYdBayGp + "RT0?LM";
//                if(bAsRollMtl) {
//                    //33010 존에서 Book-Out이고 As롤재 일 때..
//                    szYdSchCd   = "PCCB01MM";
//                } else { //(신규개발)
//                    if(bPillingModBookOut){
//                        szYdSchCd	= "PFRTAPLM"; //파일링 북아웃모드 전용 스케줄코드
//                    }
//                    else{
//                    	szYdSchCd   = ydUtils.getRtSchCdYdP(szFromLoc, "LM");
//                    }
//                }
                szYdSchCd   = ydUtils.substr(szFromLoc, 0, 6	) + "LM";
            }
            

            szMsg    = "[" + szOperationName + "] >>>> FROM위치   >>>> [" + szFromLoc + "]";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

            szMsg    = "[" + szOperationName + "] >>>> TO위치     >>>> [" + szToLoc + "]";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

            szMsg    = "[" + szOperationName + "] >>>> 스케쥴코드 >>>> " + szYdSchCd;
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

            szYdSchProhExn  = "";           // 야드스케쥴금지유무
            szYdWrkCrnPrior = "1";          // 야드작업크레인우선순위

            if(!bPillingModBookIn && !bPillingModBookOut){  //56020존 파일링 명령시, 야드기준(YD_RULE)에 할당한 설비 활용.
                szYdWrkCrn	= "";           // 야드설비ID (NOT NULL이기 때문에 RULE에 등록된 설비ID SET)
            }
            
            recPara  = JDTORecordFactory.getInstance().create();
            rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
            recPara.setField("YD_SCH_CD", szYdSchCd); 							//야드스케쥴코드


			// 스케쥴 코드 조회 (SELECT TB_YD_SCHRULE)
            // 야드스케쥴금지유무 조회
            ydSchRuleDao.getYdSchrule(recPara, rsResult);

            if (rsResult != null && rsResult.size() > 0) {
                szYdSchProhExn  = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_SCH_PROH_EXN");
                // 야드작업크레인우선순위 : 야드스케쥴코드에 해당하는 작업크레인의 우선순위이므로
                // 실제 요청한 크레인의 우선순위와 다를 수 있음
                if(!bPillingModBookIn && !bPillingModBookOut){  //56020존 파일링 명령시, 야드기준(YD_RULE)에 할당한 설비 활용.
                    szYdWrkCrnPrior = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_CRN_PRIOR"	);
                    szYdWrkCrn      = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_CRN"			);
                }
            }

            if ("".equals(szYdSchProhExn)) {
                //스케줄기준 Table 정보 Check
                szRtnMsg = "스케쥴코드[" + szYdSchCd + "] 정보 없음";
                szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                return szRtnMsg;
            } else if ("Y".equals(szYdSchProhExn)) {
                //스케줄 금지여부 Check
                szRtnMsg = "스케쥴코드[" + szYdSchCd + "] 기동금지";
                szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                return szRtnMsg;
            }

            

            if ("1".equals(szOperation_Mode) && "3".equals(szBookin_Conti_Mode)) {
                // BOOK-IN & 후판북아웃모드가 3(End) 이면

                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                String sNEW_MODULE_EFF_YN = "N";

                JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();

                sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A017"); // 1후판정정야드 북인요구END 크레인스케줄,작업예약 삭제처리 여부

                szMsg    = "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 북인요구END 크레인스케줄,작업예약 삭제처리 여부 : " + sNEW_MODULE_EFF_YN + " ]]]---";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                if(sNEW_MODULE_EFF_YN.equals("Y")) {

                    EJBConnector        ejbConn     = null;

                    String szYdCrnSchId;
                    String szStlNo;
                    String szC_YD_WRK_PROG_STAT;
                    String szYD_WBOOK_ID;
                    String szYD_SCH_CD;
                    String szYD_EQP_ID;

                    JDTORecord recCheck 	= null;
                    JDTORecord outRecord1 	= null;
                    JDTORecord setRecord    = JDTORecordFactory.getInstance().create();

                    String  sRTN_CD         = "";
                    String  sRTN_MSG        = "";


                    // 해당 열에 잡힌 크레인 작업지시를 조회하여 작업취소 (SELECT TB_YD_CRNSCH, TB_YD_CRNWRKMTL, TB_YD_STKLYR)
                    JDTORecord recPara2     = JDTORecordFactory.getInstance().create();

                    recPara2.setField("YD_STK_COL_GP"   , szFromLoc);

                    JDTORecordSet getRecSet = commDao.select(recPara2, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdCrnSchIdByLoc", logId, szMethodName, "해당 열에 잡힌 크레인 작업지시 조회");

                    if (getRecSet.size() > 0) {

                        //크레인 작업지시 취소 처리
                        for (int ii=0; ii<getRecSet.size(); ii++) {

                            szYdCrnSchId    = getRecSet.getRecord(ii).getFieldString("YD_CRN_SCH_ID"	);
                            szYD_SCH_CD     = getRecSet.getRecord(ii).getFieldString("YD_SCH_CD"		);
                            szStlNo         = getRecSet.getRecord(ii).getFieldString("STL_NO"			);

                            if ("".equals(szYdCrnSchId)) {
                                //szMsg = "["+szOperationName+"] 스케줄 취소 처리("+szMethodName+") 실패, YD_CRN_SCH_ID값이 없음";
                                //ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
                                continue;
                            }

                            recPara2.setField("YD_CRN_SCH_ID", 	szYdCrnSchId	);
                            recPara2.setField("YD_SCH_CD",     	szYD_SCH_CD		);
                            recPara2.setField("DEL_YN",        	"N"				);
                            recPara2.setField("MODIFIER",      	szRegister		);

                            /*
                             * 크레인 스케줄에서 선택된 스케줄 ID로 작업예약을 조회한 후
                             * 삭제되지 않는 작업예약에 포함된 크레인 스케줄중 첫번째 값이
                             * 2,3 인 경우 후처리를 하지않기 위해  Check Logic 반영
                             */
                            rsResult 	= JDTORecordFactory.getInstance().createRecordSet("temRs");

                            intRtnVal 	= ydCrnSchDao.getCheckYdCrnSchId(recPara2, rsResult);     // intGp == 36

                            if (intRtnVal < 1) {
                                szMsg = "[" + szOperationName + "] 취소 작업을 완료 하였습니다.";
                                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
                                continue;
                            }

                            rsResult.first();
                            recCheck = rsResult.getRecord();

                            szC_YD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recCheck, "YD_WRK_PROG_STAT");

                            //2,3, 인 경우 와 혹시모를 4이면서도 스케줄 삭제가 되지 않은 경우
                            if ("2".equals(szC_YD_WRK_PROG_STAT) || "3".equals(szC_YD_WRK_PROG_STAT) || "4".equals(szC_YD_WRK_PROG_STAT)) {
                                szMsg = "[" + szOperationName + "] 크레인 작업이 완료되지 않았습니다!!";
                                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
                                continue;
                            }

                            /*
                             * 2,3이 아닌 경우 스케줄 취소기능에  첫번째 크레인 스케줄 ID 정보를 전송
                             */
                            recPara = JDTORecordFactory.getInstance().create();
                            recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recCheck, "YD_CRN_SCH_ID")	);
                            recPara.setField("YD_SCH_CD",     ydDaoUtils.paraRecChkNull(recCheck, "YD_SCH_CD")		);
                            recPara.setField("DEL_YN",        ydDaoUtils.paraRecChkNull(recCheck, "DEL_YN")			);
                            recPara.setField("MODIFIER",      szRegister											);

                            //크레인스케줄 ID보다 이상인 ID 삭제  업데이트 실행(적치단정보까지 CLEAR)
                            //스케줄취소
                            szMsg = "[" + szOperationName + "] 작업지시 취소 시작";
                            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.03 recPara에 logId 추가 
                            recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

                            ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
                            outRecord1  = (JDTORecord)ejbConn.trx("cancelJPlateYdCrnSch", new Class[] { JDTORecord.class }, new Object[] { recPara });

                            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
                            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

                            szMsg = "[" + szOperationName + "] ---- 작업지시 취소 종료!! >>>> " + sRTN_CD + " , " + sRTN_MSG;
                            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                            if ("0".equals(sRTN_CD)) {
                                m_ctx.setRollbackOnly();
                                szRtnMsg = "작업지시 취소 ERROR .. " + sRTN_CD;
                                szMsg    = "[" + szOperationName + "] " + sRTN_MSG;
                                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                                return szRtnMsg;
                            }

                            szMsg = "[" + szOperationName + "] ---- 작업예약 취소 시작";
                            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                            ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
                            outRecord1  = (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { outRecord1 });

                            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
                            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

                            szMsg = "[" + szOperationName + "] ---- 작업예약 취소 종료!! >>>> " + outRecord1.toString();
                            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                            if ("0".equals(sRTN_CD)) {
                                m_ctx.setRollbackOnly();
                                szRtnMsg = "작업예약 취소 ERROR .. " + sRTN_CD;
                                szMsg    = "[" + szOperationName + "] " + sRTN_MSG;
                                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                                return szRtnMsg;
                            }

                            // F?RT??LM :: RT BOOK-OUT일때 재료정보 , 적치위치 Clear
                            if ("RT".equals(ydUtils.substr(szYD_SCH_CD,2,2)) && "LM".equals(ydUtils.substr(szYD_SCH_CD,6,2))) {

                                recPara = JDTORecordFactory.getInstance().create();
                                recPara.setField("YD_CRN_SCH_ID", szYdCrnSchId	);
                                recPara.setField("YD_SCH_CD",     szYD_SCH_CD	);
                                recPara.setField("STL_NO",        szStlNo		);
                                recPara.setField("MODIFIER",      szRegister	);

                                szMsg = "[" + szOperationName + "] ---- RT BOOK-OUT 재료정보 CLEAR 시작 >>>> " + recPara.toString();
                                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.10 recPara에 logId 추가  
//-------------------------------------------------------------------------------------------------------------------------
                                recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
                                
                                ejbConn         = new EJBConnector("default", this);
                                String rtnMsg   = (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "delStockLocOnRt", recPara);

                                szMsg = "[" + szOperationName + "] ---- RT BOOK-OUT 재료정보 CLEAR 종료 >>>> " + rtnMsg;
                                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
                            }
                        }
                        szMsg = "[" + szOperationName + "] ---- 정상 취소 처리 완료>>>> " ;
                        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
                    }

                    
                    // 해당 열에 잡힌 크레인 작업예약을 조회하여 작업삭제  (SELECT TB_YD_CRNSCH, TB_YD_CRNWRKMTL, TB_YD_STKLYR)
                    recPara2.setField("YD_STK_COL_GP"   , szFromLoc);

                    getRecSet   = commDao.select(recPara2, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdWrkBookIdByLoc", logId, szMethodName, "해당 열에 잡힌 크레인 작업예약 조회");

                    if (getRecSet.size() > 0) {

                        //크레인 작업예약 삭제 처리
                        for (int ii=0; ii<getRecSet.size(); ii++) {
                            szYD_WBOOK_ID   = getRecSet.getRecord(ii).getFieldString("YD_WBOOK_ID"	);
                            szYD_SCH_CD     = getRecSet.getRecord(ii).getFieldString("YD_SCH_CD"	);
                            szYD_EQP_ID     = getRecSet.getRecord(ii).getFieldString("YD_EQP_ID"	);

                            recPara = JDTORecordFactory.getInstance().create();
                            recPara.setField("MODIFIER",        szRegister		);
                            recPara.setField("YD_WBOOK_ID",     szYD_WBOOK_ID	);
                            recPara.setField("YD_EQP_ID",       szYD_EQP_ID		);
                            recPara.setField("YD_SCH_CD",       szYD_SCH_CD		);


                            szMsg = "[" + szOperationName + "] ---- 작업예약 취소 시작";
                            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
            				
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.10 recPara에 logId 추가  
//-------------------------------------------------------------------------------------------------------------------------
                            recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
                          				
                            ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
                            outRecord1  = (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { recPara });

                            sRTN_CD     = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
                            sRTN_MSG    = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

                            szMsg = "[" + szOperationName + "] ---- 작업예약 취소 종료!! >>>> " + outRecord1.toString();
                            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                            if ("0".equals(sRTN_CD)) {
                                m_ctx.setRollbackOnly();
                                szRtnMsg = "작업예약 취소 ERROR .. " + sRTN_CD;
                                szMsg    = "[" + szOperationName + "] " + sRTN_MSG;
                                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                                return szRtnMsg;
                            }
                        }

                    }
                }
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            } else if ("1".equals(szOperation_Mode) && "".equals(szPlateId[0])) {
                // BOOK-IN & 재료번호 미존재시 작업예약재료 등록

                // FROM 위치로 재료정보를 조회하여 작업예약 등록
                rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
                inRec    = JDTORecordFactory.getInstance().create();
                
                inRec.setField("YD_STK_COL_GP",     ydUtils.substr(szFromLoc, 0, 6)	);   	// 야드적치열
            //  inRec.setField("YD_STK_BED_NO",     ydUtils.substr(szFromLoc, 6, 2)	);   	// 야드적치베드
                inRec.setField("YD_STK_BED_NO",     ""								);   	// 연속북인시 베드정보 무시하도록 보완
                
                if ("2".equals(szBookin_Conti_Mode)) {                                      // 1 : one time, 2 : Start, 3 : End
                    inRec.setField("ROW_CNT",       "999");     							// FROM위치의 모든 재료
                } else {
                    inRec.setField("ROW_CNT",       "1");       							// 1매
                }


                // BOOK-IN 대상재 조회 (적치단 역순으로) (SELECT TB_YD_STKLYR, TB_YD_SHRSTOCK)
                intRtnVal = ydStkLyrDao.getRTBookInMtl(inRec, rsResult);
                if (intRtnVal < 1) {
                    szRtnMsg = "BOOK-IN 대상재 미존재 .... 저장위치 :: " + szFromLoc;
                    szMsg    = "[" + szOperationName  +"] " + szRtnMsg;
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                    return szRtnMsg;
                }

                rsResult.first();
                for(int ii=0; ii<rsResult.size(); ii++) {

                    tempRec = rsResult.getRecord(ii);

                    //------------------------------------------------------------------------------------------------
                    // 4. 작업예약 등록 [연속 북인시]
                    //------------------------------------------------------------------------------------------------
                    szYdWbookId = ydWrkbookDao.getSeqId();                      //작업예약ID 생성

                    szMsg    = "[" + szOperationName + "] ----------- 작업예약ID 생성 :: " + ii + " >>>> " + szYdWbookId;
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                    if ("".equals(szYdWbookId)) {
                        szRtnMsg = "작업예약ID 생성 ERROR";
                        szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                        return szRtnMsg;
                    }

                    szMsg    = "[" + szOperationName + "] ----------- 작업예약 등록";
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                    szYdToLocGuide = szToLoc;

                    // 작업예약 등록 (INSERT TB_YD_WRKBOOK)
                    recPara = JDTORecordFactory.getInstance().create();
                    recPara.setField("YD_WBOOK_ID",         szYdWbookId		);      // 야드작업예약ID
                    recPara.setField("YD_GP",               szYdGp			);      // 야드구분
                    recPara.setField("YD_BAY_GP",           szYdBayGp		);      // 야드동구분
                    recPara.setField("YD_SCH_CD",           szYdSchCd		);      // 야드스케쥴코드
                    recPara.setField("YD_SCH_PRIOR",        szYdWrkCrnPrior	);   	// 야드스케쥴우선순위
                    recPara.setField("YD_SCH_PROG_STAT",    "W"				);      // 야드스케쥴진행상태(스케줄수행대기)
                    recPara.setField("YD_SCH_ST_GP",        "M"				);      // 야드스케쥴기동구분
                    recPara.setField("YD_SCH_REQ_GP",       "X"				);      // 야드스케쥴요청구분
                    recPara.setField("YD_AIM_YD_GP",        szYdGp			);      // 야드목표야드구분
                    recPara.setField("YD_AIM_BAY_GP",       szYdBayGp		);      // 야드목표동구분
                    recPara.setField("YD_TO_LOC_DCSN_MTD",  "A"				);      // 야드TO위치결정방법(스케줄기준적용)
                    recPara.setField("YD_TO_LOC_GUIDE",     szYdToLocGuide	);    	// 야드To위치Guide
                    recPara.setField("REGISTER",            szRegister		);
                    recPara.setField("MODIFIER",            szRegister		);

                    intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
                    if (intRtnVal <= 0) {
                        szRtnMsg = "작업예약 등록 ERROR .. " + Integer.toString(intRtnVal);
                        szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                        return szRtnMsg;
                    }

                    // 작업예약재료 등록 (INSERT TB_YD_WRKBOOKMTL)
                    recPara.setField("YD_WBOOK_ID",     szYdWbookId											);      // 야드작업예약ID
                    recPara.setField("STL_NO",          ydDaoUtils.paraRecChkNull(tempRec, "STL_NO")		);      // 재료번호
                    recPara.setField("YD_STK_COL_GP",   ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_COL_GP")	);   	// 야드적치열구분
                    recPara.setField("YD_STK_BED_NO",   ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_BED_NO")	);   	// 야드적치베드번호
                    recPara.setField("YD_STK_LYR_NO",   ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_LYR_NO")	);   	// 야드적치단번호
                    recPara.setField("YD_TAKE_OUT_DT",  szOperationDate										);      // BOOK-OUT일시
                    recPara.setField("YD_TAKE_OUT_CD",  szReason_Code										);      // BOOK-OUT원인코드
    				
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.10 recPara에 logId 추가  
//-------------------------------------------------------------------------------------------------------------------------
                    recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
			
                    szRtnMsg = this.insWrkbookMtl(recPara);

                    if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
                        szMsg    = "[" + szOperationName + "] 작업예약재료 등록 ERROR .. " + szRtnMsg;
                        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                        return szRtnMsg;
                    }

                    if (iWBookInsCnt > 0) {
                        sbARR_WBOOK_ID.append(";");
                    }
                    sbARR_WBOOK_ID.append(szYdWbookId);
                    iWBookInsCnt ++;
                }

            } else {

                //------------------------------------------------------------------------------------------------
                // 4. 작업예약 등록 [북아웃 또는 1매 북인] (CREATE YD_WBOOK_ID)
                //------------------------------------------------------------------------------------------------
                szYdWbookId = ydWrkbookDao.getSeqId();                      // 작업예약ID 생성

                szMsg    = "[" + szOperationName + "] ----------- 작업예약ID 생성 :: " + szYdWbookId;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                if ("".equals(szYdWbookId)) {
                    szRtnMsg = "작업예약ID 생성 ERROR";
                    szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                    return szRtnMsg;
                }

                
                szMsg    = "[" + szOperationName + "] ----------- 작업예약 등록";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                szYdToLocGuide = szToLoc;

                //작업예약 등록
                recPara = JDTORecordFactory.getInstance().create();
                
                recPara.setField("YD_WBOOK_ID",         szYdWbookId		);      // 야드작업예약ID
                recPara.setField("YD_GP",               szYdGp			);      // 야드구분
                recPara.setField("YD_BAY_GP",           szYdBayGp		);      // 야드동구분
                recPara.setField("YD_SCH_CD",           szYdSchCd		);      // 야드스케쥴코드
                recPara.setField("YD_SCH_PRIOR",        szYdWrkCrnPrior	);   	// 야드스케쥴우선순위
                recPara.setField("YD_SCH_PROG_STAT",    "W"				);      // 야드스케쥴진행상태(스케줄수행대기)
                recPara.setField("YD_SCH_ST_GP",        "M"				);      // 야드스케쥴기동구분
                recPara.setField("YD_SCH_REQ_GP",       "X"				);      // 야드스케쥴요청구분
                recPara.setField("YD_AIM_YD_GP",        szYdGp			);      // 야드목표야드구분
                recPara.setField("YD_AIM_BAY_GP",       szYdBayGp		);      // 야드목표동구분
                recPara.setField("YD_TO_LOC_DCSN_MTD",  "A"				);      // 야드TO위치결정방법(스케줄기준적용)
                recPara.setField("YD_TO_LOC_GUIDE",     szYdToLocGuide	);    	// 야드To위치Guide
                recPara.setField("REGISTER",            szRegister		);
                recPara.setField("MODIFIER",            szRegister		);
                
                if("2".equals(szOperation_Mode)) {
                    recPara.setField("YD_WRK_PLAN_TCAR",        szYard_No); //TO위치

                    if(iPlateCnt == 1 && "Y".equals(szPilng_Wrk_Gp)) {
                        recPara.setField("TRN_EQP_CD",  "P"); // 00010 존 BOOK-OUT 시 파일링 대상으로 지상국에 표시
                    }
                }

			
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.15 아래 로직 필요한지 검토 필요
//-------------------------------------------------------------------------------------------------------------------------
/*                 
                if("PCCB01MM".equals(szYdSchCd) || "PCRT10LM".equals(szYdSchCd)) {
                    // C동 극후물대 AS롤재 BOOK-OUT(33010) - PCCB01MM
                    // C동 극후물대 TM재 BOOK-OUT(33010) - PCRT10LM
                    // 33010 에서 BOOK-OUT 되는 경우 TO위치를 XX010101 로 설정
                    recPara.setField("YD_TO_LOC_GUIDE"  ,"PC"); //야드To위치Guide
                    recPara.setField("YD_WRK_PLAN_TCAR" ,"");   //TO위치

                    szMsg    = "[" + szOperationName + "] ----------- 야드To위치Guide : PC 로 설정";
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
                }
*/

				// 작업 예약 등록 (INSERT TB_YD_WRKBOOK)
                intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
                if (intRtnVal <= 0) {
                    szRtnMsg = "작업예약 등록 ERROR .. " + Integer.toString(intRtnVal);
                    szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                    return szRtnMsg;
                }


                int MaxYdStkLyrNo=0;                // (신규개발)
                
                for(int ii=0; ii < iPlateCnt; ii++) {
                    // 재료 만틈 작업 예약재료 등록 (INSERT TB_YD_WRKBOOKMTL)
                    recPara.setField("YD_WBOOK_ID",     szYdWbookId			);      // 야드작업예약ID
                    recPara.setField("STL_NO",          szPlateId[ii]		);      // 재료번호
                    recPara.setField("YD_STK_COL_GP",   szFromLoc			);      // 야드적치열구분
                    recPara.setField("YD_STK_BED_NO",   szBed_No			);    	// 야드적치베드번호
                    recPara.setField("YD_STK_LYR_NO",   szYdStkLyrNo[ii]	);      // 야드적치단번호
                    recPara.setField("YD_TAKE_OUT_DT",  szOperationDate		);   	// BOOK-OUT일시
                    recPara.setField("YD_TAKE_OUT_CD",  szReason_Code		);      // BOOK-OUT원인코드
    				
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.10 recPara에 logId 추가  
//-------------------------------------------------------------------------------------------------------------------------
                    recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
                  				
                    szRtnMsg = this.insWrkbookMtl(recPara);

                    if(MaxYdStkLyrNo<Integer.parseInt(szYdStkLyrNo[ii])){
                        MaxYdStkLyrNo=Integer.parseInt(szYdStkLyrNo[ii]);    //쌓인 적치단 번호 중 최대값 저장
                    }

                    if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
                        szMsg    = "[" + szOperationName + "] 작업예약재료 등록 ERROR .. " + szRtnMsg;
                        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                        return szRtnMsg;
                    }
                }

                // (신규개발)
                //if("PILLING".equals(szDEV_INPUT1)){
                if(bPillingModBookIn || bPillingModBookOut){  // (파일링 모드 전용)삭제한 이전 작업재료 복구(북아웃/북인 공통)
                    for(int ii=0; ii < iDelStlNo; ii++) {  // 파일링 시, 삭제한 재료를 다시 작업예약재료등록
                    	
                        recPara.setField("YD_WBOOK_ID",     szYdWbookId						);      // 야드작업예약ID
                        recPara.setField("STL_NO",          szPillingDelStlNo[ii]			);      // 클린한 재료번호
                        recPara.setField("YD_STK_COL_GP",   szFromLoc						);      // 야드적치열구분
                        recPara.setField("YD_STK_BED_NO",   szBed_No						);    	// 야드적치베드번호
                        recPara.setField("YD_STK_LYR_NO",   szYdStkLyrNo[MaxYdStkLyrNo+ii]	);      // 야드적치단번호: 신규 재료 적치단 max+기존 적치단 번호
                        recPara.setField("YD_TAKE_OUT_DT",  szOperationDate					);   	// BOOK-OUT일시
                        recPara.setField("YD_TAKE_OUT_CD",  szReason_Code					);      // BOOK-OUT원인코드
        				
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.10 recrecParaPara에 logId 추가  
//-------------------------------------------------------------------------------------------------------------------------
                        recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
                                        				
                        szRtnMsg = this.insWrkbookMtl(recPara);

                        if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
                            szMsg    = "[" + szOperationName + "] 작업예약재료 등록 ERROR .. " + szRtnMsg;
                            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                            return szRtnMsg;
                        }
                    }
                }

                if (iWBookInsCnt > 0) {
                    sbARR_WBOOK_ID.append(";");
                }
                sbARR_WBOOK_ID.append(szYdWbookId);
                iWBookInsCnt ++;

            }

            
            //------------------------------------------------------------------------------------------------
            // 연속 BOOK-IN/OUT일때 가이던스 메시지 전송
            //------------------------------------------------------------------------------------------------
            if ("2".equals(szBookin_Conti_Mode) || "3".equals(szBookin_Conti_Mode)) {                   // 1:one time 2:Start 3:End

                szMsg = "[" + szOperationName + "] 야드L2 가이던스메시지 전문송신 START";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                recL2Para = JDTORecordFactory.getInstance().create();
                
                recL2Para.setField("JMS_TC_CD",             "YDY2L006"							);      // TC-CODE
                recL2Para.setField("YD_GP",                 JPlateYdConst.YD_GP_P_PLATE_YARD	);   	// 야드구분
                recL2Para.setField("OPERATION_TYPE",        szOperation_Mode					);      // 1:Book In, 2:Book Out
                if ("2".equals(szBookin_Conti_Mode)) {
                    recL2Para.setField("OPERATION_MODE",    "1"									);      // 1:Start
                } else {
                    recL2Para.setField("OPERATION_MODE",    "2"									);      // 2:End
                }

                if ("1".equals(szOperation_Mode)) {          											// 1:Book-in 일때
                    recL2Para.setField("YD_BAY_GP",         ydUtils.substr(szToLoc, 1, 1)		);      // 야드동구분
                    recL2Para.setField("OPERATION_SOURCE",  szToLoc								);      // Book-In일때  TO위치
                } else {
                    recL2Para.setField("YD_BAY_GP",         ydUtils.substr(szFromLoc, 1, 1)		);      // 야드동구분
                    recL2Para.setField("OPERATION_SOURCE",  szFromLoc							);      // Book-Out일때 From위치
                }
				
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.10 recL2Para에 logId 추가  
//-------------------------------------------------------------------------------------------------------------------------
                recL2Para.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
				
                szRtnMsg = ydDelegate.sendMsg(recL2Para);

                szMsg = "[ " + szOperationName + "] 야드L2 가이던스메시지 전문송신 END >>>> " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
            }

                
            
            iCrnSchCnt  = 0;
            iSchOkCnt   = 0;
            if (iWBookInsCnt > 0) {

                String[] arrWBookId = sbARR_WBOOK_ID.toString().split(";");

                szMsg = "[" + szOperationName + "] ----------- 3.1. BOOK-IN/OUT 스케줄기동 START .... 예약건수 :: " + iWBookInsCnt + " 작업예약ID :: " + sbARR_WBOOK_ID.toString();
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                for(int ii=0; ii<iWBookInsCnt; ii++) {

                    if(    !"PART31LM".equals(szYdSchCd) 
                    	&& !"PART32LM".equals(szYdSchCd)
                        && !"PART31UM".equals(szYdSchCd)
                        && !"PART32UM".equals(szYdSchCd)
                        && !"PART34LM".equals(szYdSchCd) 
                        && !"PART35LM".equals(szYdSchCd)
                        && !"PART34UM".equals(szYdSchCd)
                        && !"PART35UM".equals(szYdSchCd)
                        && !"PFRT21LM".equals(szYdSchCd)
                        && !"PFRT21UM".equals(szYdSchCd)
                        && !"PART23LM".equals(szYdSchCd)
                        && !"PART23UM".equals(szYdSchCd)
                        
                        ) {
                    	
						// PART31LM - A동 2열처리 입측 BOOK-OUT(0031N)
						// PART32LM - A동 2열처리 입측 BOOK-OUT(0032N)
						
						// PART31UM - A동 2열처리 입측 BOOK-IN(0031N)
						// PART32UM - A동 2열처리 입측 BOOK-IN(0032N)
						
						// PART34LM - A동 2열처리 출측 BOOK-OUT(0034N)
						// PART35LM - A동 2열처리 출측 BOOK-OUT(0034N)
						
						// PART34UM - A동 2열처리 출측 BOOK-IN(0034N)
						// PART35UM - A동 2열처리 출측 BOOK-IN(0035N)
						
						// PFRT21LM - F동 2SB 입측 BOOK-OUT(0021N)
						// PFRT21UM - F동 2SB 입측 BOOK-IN(0021N)
						
						// PART23LM - A동 2SB 출측 BOOK-OUT(0023N)
						// PART23UM - A동 2SB 출측 BOOK-IN(0023N)
                    	
                        // 위 스케줄은 요청하는 대로 크레인 스케줄이 생성되고
                        // 그 외의 스케줄은 6건의 크레인스케줄만 존재하도록 한다.

                        //------------------------------------------------------------------------------------------
                        // 스케쥴 코드로 크레인작업지시를 조회하여 6건 이하시 스케쥴 기동 (SELECT TB_YD_CRNSCH)
                        //------------------------------------------------------------------------------------------
                        rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
                        recPara  = JDTORecordFactory.getInstance().create();
                        recPara.setField("YD_SCH_CD",       szYdSchCd);

                        iCrnSchCnt = ydCrnSchDao.getByYdSchCd(recPara, rsResult);
                        if (iCrnSchCnt >= JPlateYdConst.MAX_CRN_SCH_CNT) {
                            szMsg = "["  +szOperationName + "] ----------- BOOK-IN/OUT 스케줄 기동 SKIP :: " + ii + " .. 스케쥴 건수 .. " + iCrnSchCnt;
                            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
                            break;
                        }
                    }

                    
                    //------------------------------------------------------------------------------------------------
                    // 스케쥴 기동 (BOOK-OUT)
                    //------------------------------------------------------------------------------------------------
                    recSchPara  = JDTORecordFactory.getInstance().create();
                    
                    recSchPara.setField("MSG_ID",           "YDYDJ"			);      // TC코드
                    recSchPara.setField("YD_EQP_ID",        szYdWrkCrn		);      // 크레인설비ID
                    recSchPara.setField("YD_SCH_CD",        szYdSchCd		);      // 크레인스케줄코드
                    recSchPara.setField("YD_WBOOK_ID",      arrWBookId[ii]	);    	// 작업예약ID
                    recSchPara.setField("REGISTER",         szRegister		);
                    recSchPara.setField("MODIFIER",         szRegister		);
                    recSchPara.setField("YD_TO_LOC_GUIDE",  szYdToLocGuide	);    	// 야드To위치Guide
                    recSchPara.setField("CHK_FROM_LOC",     "N"				);      // RT작업일경우 권상예약 체크 안하도록 보완


//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.15 recSchPara에 logId 추가 
					recSchPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

                    // 이 전에 야드 재료 두께(TB_YD_SHRSTOCK: YD_MTL_T 값 원복됨....Tracking 필요...
                    szMsg    = "[" + szOperationName + "] ----------- 3.2.BOOK-IN/OUT 스케줄기동 START :: " + ii + " >>>> " + arrWBookId[ii];
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                    EJBConnector ejbConn = new EJBConnector("default", "JPlateYdCrnSchYdPSeEJB", this);
                    szRtnMsg = (String)ejbConn.trx("procCrnSchMainYdP", new Class[] { JDTORecord.class }, new Object[] { recSchPara });

                    szMsg    = "[" + szOperationName + "] ----------- 3.3.BOOK-IN/OUT 스케줄기동 END :: " + ii + " >>>> " + szRtnMsg;
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                    if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
                        // 크레인 작업지시 전송후에는 오류발생 데이타 SKIP
                        if (iSchOkCnt > 0) {
                            szMsg    = "[" + szOperationName + "] BOOK-IN/OUT 스케줄기동시 오류 발생 .... SKIP >>>> " + szRtnMsg;
                            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                        } else {
                            szRtnMsg = "BOOK_IN/OUT 스케줄기동 오류 .. <br>" + szRtnMsg;
                            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                            return szRtnMsg;
                        }
                    } else {
                        iSchOkCnt ++;
                    }
                }
            }
        } catch(Exception e) {
            throw new DAOException(getClass().getName() + e.getMessage(),e);
        }

        return JPlateYdConst.RETN_CD_SUCCESS;
		
	} //end of procP8BookInOutReq()


	/**
	 * 오퍼레이션명 : 설비상태 체크
	 *
	 * @param   String szEqpId 설비ID
	 * @return boolean true(설비사용가능), false(설비사용불가)
	 * @throws DAOException
	 */
	public String eqpStatCheck(String szEqpId, JDTORecordSet rsResult, String logId)throws DAOException  {
		//메세지
		String szMsg           	= null;
		//메소드명
		String szMethodName    	= "eqpStatCheck";
		String szOperationName  = "설비조회";

		String szYdEqpStat   	= null;				// 설비상태
		String szYdEqpWrkMode	= null;				// 야드설비작업Mode
		String szYdEqpId		= null;

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew(ydUtils.substr(szEqpId, 0, 1)); // log id 가 비어있는경우 새로 설비ID 공장구분으로 log id 새로 발본

		// 레코드 선언
		JDTORecord recPara     	= null;

		int intRtnVal			= -100;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 설비ID를 작업크레인으로 설정
			recPara.setField("YD_EQP_ID", szEqpId);

			// 설비 체크 및 데이터 조회
//			blnRtnVal = this.chkGetEqp(szEqpId, rsResult);
//			if (!blnRtnVal) { return blnRtnVal; }

			JPlateYdEqpDAO ydEqpDao = new JPlateYdEqpDAO();

			intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult);

			if (intRtnVal == 0) {
				szMsg = "[" + szOperationName + "] 존재하지 않습니다.";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return JPlateYdConst.RETN_CD_NOTEXIST;
			} else if (intRtnVal == -2) {
				szMsg = "[" + szOperationName + "] 파라미터가 존재하지 않습니다.";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return JPlateYdConst.RETN_CD_NO_PARAM;
			} else if (intRtnVal < 0) {
				szMsg = "[" + szOperationName + "] 오류발생";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szMsg;
			} else {
				szMsg = "[" + szOperationName + "] 존재합니다. - 대상재[" + intRtnVal + "]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			}

			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 설비상태
			szYdEqpStat 	= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
			// 야드설비작업Mode
			szYdEqpWrkMode 	= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_MODE");
			// 야드설비작업Mode
			szYdEqpId 		= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_ID");

			szMsg = "설비ID(" + szYdEqpId + ")의 (" + szYdEqpStat + ") 입니다.";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

			// 크레인의 상태가 'T'이면 false 리턴.
			if (JPlateYdConst.YD_EQP_STAT_BREAK.equals(szYdEqpStat)) {

				szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYdEqpStat + ") 입니다.";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				//blnRtnVal = false;

				return JPlateYdConst.YD_EQP_STAT_BREAK;
			} else if (JPlateYdConst.YD_EQP_WRK_MODE_OFF_LINE.equals(szYdEqpWrkMode)) {

				szMsg = "설비ID(" + szEqpId + ")의 상태가  OFF LINE(" + szYdEqpWrkMode + ")상태 입니다.";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				//blnRtnVal = false;

				return JPlateYdConst.YD_EQP_WRK_MODE_OFF_LINE;
			} else {

				//blnRtnVal = true;
				return JPlateYdConst.RETN_CD_SUCCESS;
			}
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		//return blnRtnVal;

	} //end of eqpStatCheck


    /**
     * 오퍼레이션명 : 크레인 작업지시(작업지시를 재요구 하는 경우 사용한다.)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws DAOException
     */
    public int chkWrkProgStatYdP(JDTORecord msgRecord, JDTORecordSet rsCrnSch)throws DAOException  {

    	JPlateYdCrnSchDAO ydCrnSchDao 	= new JPlateYdCrnSchDAO();
    	JPlateYdEqpDAO    ydEqpDao 		= new JPlateYdEqpDAO();

        JDTORecordSet 	rsResult    = null;
        JDTORecord 		recOutTemp 	= null;

    	int intRtnVal 				= 0;

        String szMsg              	= "";
        String szMethodName       	= "chkWrkProgStatYdP";
        String szYdEqpStat          = "";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.12 로그 개선 
  		String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

  		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

        try {

        	//설비Table를 조회하여 설비상태가 '1 : 권상지시'또는 '3 : 권하지시'이라면 크레인 스케줄 Table에서 야드작업진행 상태가 1,2,3인 것만 찾는다. 1건만 나와야 정상이고 1건이 아니라면 Error처리
        	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
        	intRtnVal = ydEqpDao.getYdEqp(msgRecord, rsResult);
			if (intRtnVal <= 0) {
				if (intRtnVal == 0) {
					szMsg = "chkWrkProgStat getYdEqp : data not found";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING, logId);
				} else if (intRtnVal == -2) {
					szMsg = "chkWrkProgStat getYdEqp : parameter error";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				}
				return intRtnVal = -1;
			}

			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult.getRecord(0));
			szYdEqpStat = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_STAT");

			//설비상태가 1또는 3인경우
			if ("1".equals(szYdEqpStat) || "2".equals(szYdEqpStat) || "3".equals(szYdEqpStat)) {
				//설비id로 크레인 스케줄을 조회한다. 현재 작업진행상태가 1또는 3인 경우를 조회...
	        	rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    	intRtnVal = ydCrnSchDao.getWrkProgStatYdP(msgRecord, rsResult);
				if (intRtnVal <= 0) {
					//에러처리
					szMsg = "현재 작업진행상태가 1또는 3인 크레인 스케줄을 조회 중 Error";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					//return intRtnVal = -1;
					return intRtnVal;
				}
			} else {
				intRtnVal = 0;
			}

        	rsCrnSch.addAll(rsResult);

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "chkWrkProgStat(" + szMethodName + ") 처리 완료 " + Integer.toString(intRtnVal);
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		return intRtnVal;
	} // end of chkWrkProgStatYdP()
	
}
