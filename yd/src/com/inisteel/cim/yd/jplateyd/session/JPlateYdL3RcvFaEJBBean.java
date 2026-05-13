/*
 * @(#) 2후판정정야드 L3수신 처리 Facade Session EJB클래스
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/12/03
 *
 * @description		2후판정정야드 L3수신  처리 Facade Session EJB클래스
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/12/03   김현우      김현우       최초작성 
 */

package com.inisteel.cim.yd.jplateyd.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.DateHelper;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;

//-------------------------------------------------------------------------------------------------------------------------
//2024.12.17 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 * 2후판정정야드 L3수신 처리 Facade Session EJB
 *
 * @ejb.bean name="JPlateYdL3RcvFaEJB" jndi-name="JPlateYdL3RcvFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class JPlateYdL3RcvFaEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;

	// Session Name
	private static final String SZ_SESSION_NAME = JPlateYdL3RcvFaEJBBean.class.getName();

	private JPlateYdUtils	ydUtils     = new JPlateYdUtils();
	private EJBConnector 	ydEjbCon 	= new EJBConnector("default", this);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
    private YdUtils 			ydLogUtils  = new YdUtils();
	    
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * 오퍼레이션명 : PP 후판조업 GAS장 절단실적수신 [PPYDJ014]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws DAOException
	 */
	public void rcvPPGasCutResult(JDTORecord inRecord) throws DAOException  {
		//
		// TC : PPYDJ014
		// 후판조업 GAS장 절단실적수신
		//
		String	szRtnMsg		= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMsg        	= "";
		String 	szMethodName 	= "rcvPPGasCutResult";
		String 	szOperationName = "후판조업 GAS장 절단실적수신";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.18 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(inRecord, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		                                 
		szMsg = szOperationName + "[" + szMethodName + "] PPYDJ014 수신처리 .. 시작";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		try {

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.19 inRecord에 logId 추가 
			inRecord.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
			
			szRtnMsg = (String)ydEjbCon.trx("JPlateYdL3RcvSeEJB", "procPPGasCutResult", inRecord);
			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szMsg = szOperationName + "[" + szMethodName + "] PPYDJ014 수신처리 .. 실행 실패 >>>> " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

				m_ctx.setRollbackOnly();

				throw new DAOException(szRtnMsg);
			}
		} catch (Exception e) {
			szMsg = szOperationName + "[" + szMethodName + "] " + e.getMessage();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			throw new DAOException(szMsg);
		} // end of try catch

		szMsg = szOperationName + "[" + szMethodName + "] PPYDJ014 수신처리 .. 완료";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	} // end of rcvPPGasCutResult()

	/**
	 * 오퍼레이션명 : PP 후판조업 차행선결정정보 수신 [PPYDJ015]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws DAOException
	 */
	public void rcvPPNextDeciInfo(JDTORecord inRecord) throws DAOException  {
		//
		// TC : PPYDJ015
		// 후판조업 차행선결정정보 수신
		//
		String 	szMsg        	= "";
		String	szRtnMsg		= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMethodName 	= "rcvPPNextDeciInfo";
		String 	szOperationName = "후판조업 차행선결정정보 수신";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.19 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(inRecord, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
				                                 
		szMsg = szOperationName + "[" + szMethodName + "] PPYDJ015 수신처리  .. 시작";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		try {

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.19 inRecord에 logId 추가 
			inRecord.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
			
			szRtnMsg = (String)ydEjbCon.trx("JPlateYdL3RcvSeEJB", "procPPNextDeciInfo", inRecord);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szMsg = szOperationName + "[" + szMethodName + "] PPYDJ015 수신처리 .. 실행 실패 >>>> " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

				m_ctx.setRollbackOnly();

			// throw new DAOException(szRtnMsg);
			}
		} catch (Exception e) {
			szMsg = szOperationName + "[" + szMethodName + "] " + e.getMessage();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			throw new DAOException(szMsg);
		} // end of try catch

		szMsg = szOperationName + "[" + szMethodName + "] PPYDJ015 수신처리 .. 완료";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	} // end of rcvPPNextDeciInfo()

	/**
	 * 오퍼레이션명 : PP 후판조업 극후물대 북아웃실적 수신 [PPYDJ016]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws DAOException
	 */
	public void rcvPPCBRTBookOut(JDTORecord inRecord) throws DAOException  {
		//
		// TC : PPYDJ016
		// 후판조업 극후물대 북아웃실적 수신
		//
		String 	szMsg        	= "";
		String	szRtnMsg		= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMethodName 	= "rcvPPCBRTBookOut";
		String 	szOperationName = "후판조업 극후물대 북아웃실적 수신";

		szMsg = szOperationName + "[" + szMethodName + "] PPYDJ016 수신처리  .. 시작";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		try {
			szRtnMsg = (String)ydEjbCon.trx("JPlateYdL3RcvSeEJB", "procPPCBRTBookOut", inRecord);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szMsg = szOperationName + "[" + szMethodName + "] PPYDJ016 수신처리 .. 실행 실패 >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

				m_ctx.setRollbackOnly();

				throw new DAOException(szRtnMsg);
			}
		} catch (Exception e) {
			szMsg = szOperationName + "[" + szMethodName + "] " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch

		szMsg = szOperationName + "[" + szMethodName + "] PPYDJ016 수신처리 .. 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	} // end of procPPNextDeciInfo()

	/**
	 * 오퍼레이션명 : 이송상차완료실적 수신 [YDYDJ770]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws DAOException
	 */
	public void rcvYdPcarWrkEnd(JDTORecord inRecord) throws DAOException  {
		//
		// TC : YDYDJ770
		// 이송상차완료실적 실적수신
		//
		String 	szMsg        	= "";
		String	szRtnMsg		= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMethodName 	= "rcvYdPcarWrkEnd";
		String 	szOperationName = "이송상차완료실적 수신";

		szMsg = szOperationName + "[" + szMethodName + "] YDYDJ770 수신처리  .. 시작";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		try {
			szRtnMsg = (String)ydEjbCon.trx("JPlateYdL3RcvSeEJB", "procYdPcarWrkEnd", inRecord);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szMsg = szOperationName + "[" + szMethodName + "] YDYDJ770 수신처리 .. 실행 실패 >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

				m_ctx.setRollbackOnly();

				//throw new DAOException(szRtnMsg);
			}
		} catch (Exception e) {
			szMsg = szOperationName + "[" + szMethodName + "] " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch

		szMsg = szOperationName + "[" + szMethodName + "] YDYDJ770 수신처리 .. 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	} // end of rcvYdPcarWrkEnd()

	//---------------------------------------------------------------------------
	
	/**********************************************************
	* 1후판정정추가 SJH16 
	**********************************************************/
	
	/**
	 * 오퍼레이션명 : PR 후판조업 임가공절단장 절단실적수신 [PRYDJ014]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws DAOException
	 */
	public void rcvPRRentCutResult(JDTORecord inRecord) throws DAOException  {
		//
		// TC : PRYDJ014
		// 후판조업  임가공절단장 절단실적수신
		//
		String	szRtnMsg		= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMsg        	= "";
		String 	szMethodName 	= "rcvPRRentCutResult";
		String 	szOperationName = "1후판조업 임가공절단장 절단실적수신";

		szMsg = szOperationName + "[" + szMethodName + "] PRYDJ014 수신처리 .. 시작";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		try {
			szRtnMsg = (String)ydEjbCon.trx("JPlateYdL3RcvSeEJB", "procPRRentCutResult", inRecord);
			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szMsg = szOperationName + "[" + szMethodName + "] PRYDJ014 수신처리 .. 실행 실패 >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

				m_ctx.setRollbackOnly();

				throw new DAOException(szRtnMsg);
			}
		} catch (Exception e) {
			szMsg = szOperationName + "[" + szMethodName + "] " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch

		szMsg = szOperationName + "[" + szMethodName + "] PRYDJ014 수신처리 .. 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	} // end of rcvPRRentCutResult()
	
	/**
	 * 오퍼레이션명 : PR 1후판조업 차행선결정정보 수신 [PRYDJ015]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws DAOException
	 */
	public void rcvPRNextDeciInfo(JDTORecord inRecord) throws DAOException  {
		//
		// TC : PPYDJ015
		// 후판조업 차행선결정정보 수신
		//
		String 	szMsg        	= "";
		String	szRtnMsg		= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMethodName 	= "rcvPRNextDeciInfo";
		String 	szOperationName = "1후판조업 차행선결정정보 수신";

		szMsg = szOperationName + "[" + szMethodName + "] PRYDJ015 수신처리  .. 시작";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		try {
			String sNEW_MODULE_EFF_YN = "N";
			
			JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
			
			szRtnMsg = (String)ydEjbCon.trx("JPlateYdL3RcvSeEJB", "procPRNextDeciInfo", inRecord);

			sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A014"); //1후판정정야드 조업L3 차행선결정정보 수신(PRYDJ015) DAOException 미발생 적용여부
			
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(PRYDJ017 DAOException)---[[[ 1후판정정야드 조업L3 차행선결정정보 수신(PRYDJ015) DAOException 미발생 적용 : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
			
			if(sNEW_MODULE_EFF_YN.equals("Y")) {
				//메세지만 남기고 DAOException 은 발생시키지 않는다..
				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					szMsg = szOperationName + "[" + szMethodName + "] PRYDJ015 수신처리 .. 실행 안함 >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					szRtnMsg = JPlateYdConst.RETN_CD_SUCCESS;
					m_ctx.setRollbackOnly();
				}
			}
			
			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szMsg = szOperationName + "[" + szMethodName + "] PRYDJ015 수신처리 .. 실행 실패 >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

				m_ctx.setRollbackOnly();

				throw new DAOException(szRtnMsg);
			}
		} catch (Exception e) {
			szMsg = szOperationName + "[" + szMethodName + "] " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch

		szMsg = szOperationName + "[" + szMethodName + "] PRYDJ015 수신처리 .. 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	} // end of rcvPRNextDeciInfo()

	/**
	 * 오퍼레이션명 : PR 1후판조업 설비완료실적 수신 [PRYDJ016]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws DAOException
	 */
	public void rcvPRYDJ016(JDTORecord inRecord) throws DAOException  {
		//
		// TC : PPYDJ015
		// 후판조업 차행선결정정보 수신
		//
		String 	szMsg        	= "";
		String	szRtnMsg		= JPlateYdConst.RETN_CD_FAILURE;
		String 	szMethodName 	= "rcvPRYDJ016";
		String 	szOperationName = "1후판조업 설비완료실적 수신";

		szMsg = szOperationName + "[" + szMethodName + "] PRYDJ016 수신처리  .. 시작";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		try {
			
			String szEQP_GP = StringHelper.evl(inRecord.getFieldString("EQP_GP"), "00");
			String szSTL_NO = StringHelper.evl(inRecord.getFieldString("STL_NO"), "");
			String modifier = StringHelper.evl(inRecord.getFieldString("MODIFIER"), "PRYDJ016");
			
			if("01".equals(szEQP_GP)) { //#1PRESS 완료실적
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
				
				inRecord.setField("JMS_TC_CD"			, "P2YDL501"); 
				inRecord.setField("OPERATION_TYPE"		, "2"); //2:Book-Out
				inRecord.setField("PL_MTL_NO"			, szSTL_NO); 
				inRecord.setField("PL_TRCK_ZONE_ASGN"	, "20000"); //BOOK-OUT Zone 번호 (from위치)
				inRecord.setField("YARD_NO"				, ""); //to 위치
				inRecord.setField("PL_BOOK_OUT_MOD"		, "1"); //1:one time 2:Start 3:End
				inRecord.setField("PILNG_WRK_GP"		, "N"); // 파일링 작업 구분
				inRecord.setField("PL_MTL_NO2"			, ""); 
				inRecord.setField("PL_MTL_NO3"			, ""); 
				
				szRtnMsg = (String)ydEjbCon.trx("JPlateYdL2RcvSeEJB", "procP2P3BookInOutReq2", inRecord);
				
			} else if("02".equals(szEQP_GP)) { //DSS 완료실적
				
				String sNEW_MODULE_EFF_YN = "N";
				
				JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
				
				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A018"); //1후판정정야드 DSS설비완료실적 수신시 크레인스케줄 삭제처리 여부
				
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 DSS설비완료실적 수신시 크레인스케줄 삭제처리 여부  : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
				
				if(sNEW_MODULE_EFF_YN.equals("Y")) {
					
					EJBConnector 		ejbConn 	= null;
					
					JDTORecord    recPara   = null;
					JDTORecordSet rsResult  = null;
					int 	intRtnVal 		= 0 ;
					
					String szYdCrnSchId;
					String szYdSchCd;
					String szStlNo;
					String szC_YD_WRK_PROG_STAT;
					String szYD_WBOOK_ID;
					String szYD_SCH_CD;
					String szYD_EQP_ID;
					
					JDTORecord recCheck = null;
					JDTORecord outRecord1 = null;
					
					String 	sRTN_CD					= "";
					String 	sRTN_MSG				= "";
					
					
					//재료번호로 크레인 작업지시를 조회하여 작업취소 
					JDTORecord recPara2		= JDTORecordFactory.getInstance().create();
					String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
					JPlateYdCommDAO  commDao 	= new JPlateYdCommDAO();
					JPlateYdCrnSchDAO   ydCrnSchDao = new JPlateYdCrnSchDAO();		// 크레인스케쥴DAO
					
					recPara2.setField("STL_NO"	, szSTL_NO);    
					
					JDTORecordSet getRecSet	= commDao.select(recPara2, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdCrnSchIdByStlNo", logId, szMethodName, "재료번호로 크레인 작업지시 조회");
					
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

							szC_YD_WRK_PROG_STAT = StringHelper.evl(recCheck.getFieldString("YD_WRK_PROG_STAT"),"");

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
							recPara.setField("YD_CRN_SCH_ID", StringHelper.evl(recCheck.getFieldString("YD_CRN_SCH_ID"),""));
							recPara.setField("YD_SCH_CD",     StringHelper.evl(recCheck.getFieldString("YD_SCH_CD"),""));
							recPara.setField("DEL_YN",        StringHelper.evl(recCheck.getFieldString("DEL_YN"),""));
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
								return;
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
								return;
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
					
					//재료번호로 크레인 작업예약을 조회하여 작업삭제 
					/*
					recPara2.setField("STL_NO"	, szSTL_NO);    
					
					getRecSet	= commDao.select(recPara2, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdWrkBookIdByStlNo", logId, szMethodName, "재료번호로 크레인 작업예약 조회");
					
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
								return;
							}
						}
						
					}
					*/
					
					szRtnMsg = JPlateYdConst.RETN_CD_SUCCESS;
				}
				
			}
			
			
			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szMsg = szOperationName + "[" + szMethodName + "] PRYDJ016 수신처리 .. 실행 실패 >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

				m_ctx.setRollbackOnly();

				throw new DAOException(szRtnMsg);
			}
		} catch (Exception e) {
			szMsg = szOperationName + "[" + szMethodName + "] " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch

		szMsg = szOperationName + "[" + szMethodName + "] PRYDJ016 수신처리 .. 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	} // end of rcvPRYDJ016()

	/**
	 * 오퍼레이션명 : PR 1후판조업 야드재료삭제처리 수신 [PRYDJ017]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws DAOException
	 */
	public void rcvPRYDJ017(JDTORecord inRecord) throws DAOException  {
		//
		// TC : PPYDJ017
		// 1후판조업 야드재료삭제처리 수신
		//
		String 	szMsg        	= "";
		String	szRtnMsg		= JPlateYdConst.RETN_CD_FAILURE;
		String 	szMethodName 	= "rcvPRYDJ017";
		String 	szOperationName = "1후판조업 야드재료삭제처리 수신";

		szMsg = szOperationName + "[" + szMethodName + "] PRYDJ017 수신처리  .. 시작";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		try {
			
			String sNEW_MODULE_EFF_YN = "N";
			
			JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
			sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A012"); //1후판정정야드 조업L3 야드재료삭제처리(PRYDJ017) 수신 적용여부
			
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(PRYDJ017)---[[[ 1후판정정야드 조업L3 야드재료삭제처리(PRYDJ017) 수신 적용 : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
			
			if(sNEW_MODULE_EFF_YN.equals("Y")) {
				
				String szSTL_NO = StringHelper.evl(inRecord.getFieldString("STL_NO"), "");
				
				inRecord.setField("STL_NO"			, szSTL_NO); 
				inRecord.setField("EQP_GP"			, "02"); 
				inRecord.setField("MODIFIER"		, StringHelper.evl(ydUtils.getTcCode(inRecord),"PRYDJ017")); 
				
				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A024"); //1후판정정야드 조업L3 야드재료삭제처리(PRYDJ017) 수신시 크레인스케줄 삭제여부
				
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(PRYDJ017)---[[[ 1후판정정야드 조업L3 야드재료삭제처리(PRYDJ017) 수신시 크레인스케줄 삭제여부 : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
				
				if(sNEW_MODULE_EFF_YN.equals("Y")) {
				
					//크레인 스케줄 취소 처리 추가
					this.rcvPRYDJ016(inRecord);
				}
				
				
				//적치단 Clear 
				szRtnMsg = (String)ydEjbCon.trx("JPlateYdYdPJspSeEJB", "delYdLocInfo2", inRecord);
				
				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A013"); //1후판정정야드 조업L3 야드재료삭제처리(PRYDJ017) DAOException 미발생 적용여부
				
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(PRYDJ017 DAOException)---[[[ 1후판정정야드 조업L3 야드재료삭제처리(PRYDJ017) DAOException 미발생 적용 : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
				
				if(sNEW_MODULE_EFF_YN.equals("Y")) {
					//메세지만 남기고 DAOException 은 발생시키지 않는다..
					if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
						szMsg = szOperationName + "[" + szMethodName + "] PRYDJ017 수신처리 .. DAOException 실행 안함 >>>> " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						szRtnMsg = JPlateYdConst.RETN_CD_SUCCESS;
						m_ctx.setRollbackOnly();
					}
				}
				
			} else {
				
				szRtnMsg = "1후판정정야드 조업L3 야드재료삭제처리(PRYDJ017) 수신 적용여부 가 'N' 입니다.";
				szMsg = szOperationName + "[" + szMethodName + "] PRYDJ017 수신처리 .. 실행 안함 >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				szRtnMsg = JPlateYdConst.RETN_CD_SUCCESS;
			}
			
			
			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szMsg = szOperationName + "[" + szMethodName + "] PRYDJ017 수신처리 .. 실행 실패 >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

				m_ctx.setRollbackOnly();

				throw new DAOException(szRtnMsg);
			}
			
		} catch (Exception e) {
			szMsg = szOperationName + "[" + szMethodName + "] " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch

		szMsg = szOperationName + "[" + szMethodName + "] PRYDJ017 수신처리 .. 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	} // end of rcvPRYDJ017()

	/**
	 * 오퍼레이션명 : PR 1후판조업 1후판정정 실적처리대상재료저장위치생성 수신 [PRYDJ018]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return:
	 * @throws DAOException
	 */
	public void rcvPRYDJ018(JDTORecord inRecord) throws DAOException  {
		//
		// TC : PPYDJ018
		// 1후판조업 실적처리대상재료저장위치생성 수신
		//
		String 	szMsg        	= "";
		String	szRtnMsg		= JPlateYdConst.RETN_CD_FAILURE;
		String 	szMethodName 	= "rcvPRYDJ018";
		String 	szOperationName = "1후판조업 실적처리대상재료저장위치생성 수신";

		szMsg = szOperationName + "[" + szMethodName + "] PRYDJ018 수신처리  .. 시작";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		try {
			
			String sNEW_MODULE_EFF_YN = "N";
			
			JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
			sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A027"); //1후판정정야드 조업L3 실적처리대상재료저장위치생성(PRYDJ018) 수신 적용여부
			
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(PRYDJ018)---[[[ 1후판정정야드 조업L3 실적처리대상재료저장위치생성(PRYDJ018) 수신 적용여부 : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
			
			if(sNEW_MODULE_EFF_YN.equals("Y")) {
				
				String szSTL_NO = StringHelper.evl(inRecord.getFieldString("STL_NO"), "");
				String szSPAN_GP = StringHelper.evl(inRecord.getFieldString("SPAN_GP"), "");
				String szYD_GP = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");
				
				if("P".equals(szYD_GP) && "BS".equals(szSPAN_GP) && !"".equals(szSTL_NO)) {
					
					inRecord.setField("STL_NO"			, szSTL_NO); 
					inRecord.setField("EQP_GP"			, "02"); 
					inRecord.setField("MODIFIER"		, "PRYDJ018"); 
					
					sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A029"); //1후판정정야드 조업L3 실적처리대상재료저장위치생성(PRYDJ018) 수신시 크레인스케줄 삭제여부
					
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(PRYDJ018)---[[[ 1후판정정야드 조업L3 실적처리대상재료저장위치생성(PRYDJ018) 수신시 크레인스케줄 삭제여부 : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
					
					if(sNEW_MODULE_EFF_YN.equals("Y")) {
					
						//크레인 스케줄 취소 처리 추가
						this.rcvPRYDJ016(inRecord);
					}
					
					JPlateYdCommDAO 		commDao 		= new JPlateYdCommDAO();
					
					String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
					
					JDTORecord setRecord = JDTORecordFactory.getInstance().create();
			        setRecord.setField("STL_NO"				,	szSTL_NO);
			        setRecord.setField("YD_TO_STK_COL_GP"	,	"PCBS");
			        setRecord.setField("YD_GP"				,	JPlateYdConst.YD_GP_P_PLATE_YARD);
			        setRecord.setField("YD_BAY_GP"			,	"C");
	
			        JDTORecordSet  rsResult = commDao.select(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getSameLWGpBookInYdP", logId, szMethodName, "보수장일때 적치위치를 다시 조회 (보수장은 권하위치 틀림)");
			        if(rsResult.size() > 0){
			        	
			        	inRecord.setField("YD_STK_COL_GP", rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
			        	inRecord.setField("YD_STK_BED_NO", rsResult.getRecord(0).getFieldString("YD_STK_BED_NO"));
			        	inRecord.setField("YD_STK_LYR_NO", rsResult.getRecord(0).getFieldString("YD_STK_LYR_NO"));
			        	
						//저장위치 수정 호출
						szRtnMsg = (String)ydEjbCon.trx("JPlateYdYdPJspSeEJB", "updYdLocInfo2", inRecord);
			        }
					
					sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A028"); //1후판정정야드 조업L3 실적처리대상재료저장위치생성(PRYDJ018) DAOException 미발생 적용여부
					
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(PRYDJ018 DAOException)---[[[ 1후판정정야드 조업L3 실적처리대상재료저장위치생성(PRYDJ018) DAOException 미발생 적용여부 : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
					
					if(sNEW_MODULE_EFF_YN.equals("Y")) {
						//메세지만 남기고 DAOException 은 발생시키지 않는다..
						if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
							szMsg = szOperationName + "[" + szMethodName + "] PRYDJ018 수신처리 .. DAOException 실행 안함 >>>> " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
							szRtnMsg = JPlateYdConst.RETN_CD_SUCCESS;
							m_ctx.setRollbackOnly();
						}
					}
				}
				
			} else {
				
				szRtnMsg = "1후판정정야드 조업L3 실적처리대상재료저장위치생성(PRYDJ018) 수신 적용여부 가 'N' 입니다.";
				szMsg = szOperationName + "[" + szMethodName + "] PRYDJ018 수신처리 .. 실행 안함 >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				szRtnMsg = JPlateYdConst.RETN_CD_SUCCESS;
			}
			
			
			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szMsg = szOperationName + "[" + szMethodName + "] PRYDJ018 수신처리 .. 실행 실패 >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

				m_ctx.setRollbackOnly();

				throw new DAOException(szRtnMsg);
			}
			
		} catch (Exception e) {
			szMsg = szOperationName + "[" + szMethodName + "] " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch

		szMsg = szOperationName + "[" + szMethodName + "] PRYDJ018 수신처리 .. 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	} // end of rcvPRYDJ018()
	
}
