/*
 * @(#) 1후판정정야드 JSP에서 호출되는 Facade Session EJB클래스
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/14
 *
 * @description		2후판정정야드 JSP에서 호출되는 Facade Session EJB클래스
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/14   김현우      김현우       최초작성 
 */

package com.inisteel.cim.yd.jplateyd.session;

import java.util.HashMap;
import java.util.List;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.DateHelper;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;

import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO;
import com.inisteel.cim.yd.jplateyd.delegate.JPlateYdDelegate;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.yd.jsp.common.YDComUtil;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.14 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;


/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Facade Session EJB클래스입니다.
 *
 * @ejb.bean name="JPlateYdYdPJspFaEJB" jndi-name="JPlateYdYdPJspFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class JPlateYdYdPJspFaEJBBean extends BaseSessionBean { 


	private static final long serialVersionUID = 1L;
	private YdSlabUtils commUtils = new YdSlabUtils();
	
	// Session Name
	private static final String SZ_SESSION_NAME = JPlateYdYdPJspFaEJBBean.class.getName();

	private JPlateYdUtils    	ydUtils   	= new JPlateYdUtils();
	private YDComUtil  			ydComUtil 	= new YDComUtil();
    private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();
    private YdPICommDAO	   		ydPICommDAO = new YdPICommDAO();

    private EJBConnector ydEjbCon = new EJBConnector("default", this);
    
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.14 1후판 정정 로그 관련 야드공통 UTIL 
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
	 * 오퍼레이션명 : 1후판정정야드 I/F 수신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvY2NewInterface(JDTORecord inRecord) throws JDTOException {
		//
		// 1후판정정야드 신규I/F 수신
		//
		String szMsg="";
		String szMethodName="rcvY2NewInterface";
		
		try {
			String szTcCode = ydDaoUtils.paraRecChkNull(inRecord, "MSG_ID");
			
			if("".equals(szTcCode)){
				szTcCode = ydDaoUtils.paraRecChkNull(inRecord, "Telegram_Id");
			}
			
			if      ("Y2YDL001".equals(szTcCode)){
				ydEjbCon.trx("JPlateYdL2RcvFaEJB", "rcvY2StrLocSpecReq", 	inRecord); //1후판정정 저장위치제원요구
			}else if("Y2YDL002".equals(szTcCode)){
				ydEjbCon.trx("JPlateYdL2RcvFaEJB", "rcvY2StockSpecReq",		inRecord); //1후판정정 저장품제원요구
			}else if("Y2YDL003".equals(szTcCode)){
				ydEjbCon.trx("JPlateYdL2RcvFaEJB", "rcvY2EqpDrvModeChg", 	inRecord); //1후판정정 설비운전모드전환
			}else if("Y2YDL004".equals(szTcCode)){
				ydEjbCon.trx("JPlateYdL2RcvFaEJB", "rcvY2EqpTrblRcvrWr", 	inRecord); //1후판정정 설비고장복구실적 
			}else if("Y2YDL007".equals(szTcCode)){
				ydEjbCon.trx("JPlateYdL2RcvFaEJB", "rcvY2CrnWrkOrdReq", 	inRecord); //1후판정정 크레인작업지시요구
			}else if("Y2YDL008".equals(szTcCode)){
				ydEjbCon.trx("JPlateYdL2RcvFaEJB", "rcvY2CrnUpWr", 			inRecord); //1후판정정 권상실적
			}else if("Y2YDL009".equals(szTcCode)){
				ydEjbCon.trx("JPlateYdL2RcvFaEJB", "rcvY2CrnDownWr", 		inRecord); //1후판정정 권하실적	 
			}else if("Y2YDL010".equals(szTcCode)){
				ydEjbCon.trx("JPlateYdL2RcvFaEJB", "rcvY2OffCrnUpWr", 		inRecord); //1후판정정 강제권상요구	 
			}else if("Y2YDL011".equals(szTcCode)){ 
				ydEjbCon.trx("JPlateYdL2RcvFaEJB", "rcvY2OffCrnDownWr", 	inRecord); //1후판정정 강제권하요구	
			}else if("Y2YDL012".equals(szTcCode)){
				ydEjbCon.trx("JPlateYdL2RcvFaEJB", "rcvY2CrnOrderSel", 		inRecord); //1후판정정 크레인명령선택	
			}else if("Y2YDL013".equals(szTcCode)){
				ydEjbCon.trx("JPlateYdL2RcvFaEJB", "rcvY2PilingRslt", 		inRecord); //1후판정정 파일링실적	
			}else if("Y2YDL014".equals(szTcCode)){
				ydEjbCon.trx("JPlateYdL2RcvFaEJB", "rcvY2BookInOutRslt", 	inRecord); //1후판정정  Book-In/Book-Out 실적
			}else if("Y2YDL015".equals(szTcCode)){
				ydEjbCon.trx("JPlateYdL2RcvFaEJB", "rcvY2YDL015", 			inRecord); //1후판정정  후판L2제품번호요구
			}else if("Y2YDL016".equals(szTcCode)){
				ydEjbCon.trx("JPlateYdL2RcvFaEJB", "rcvY2YDL016", 			inRecord); //1후판정정  저장위치제원정보
				
			}else if("P2YDL501".equals(szTcCode)||"24752".equals(szTcCode)){ //TY3ABR
				ydEjbCon.trx("JPlateYdL2RcvFaEJB", "rcvP2BookInOutReq", 	inRecord); //1후판전단 Book-In/Book-Out요구	
			}else if("P2YDL601".equals(szTcCode)||"29782".equals(szTcCode)){
				ydEjbCon.trx("JPlateYdL2RcvFaEJB", "rcvP2YDL601", 			inRecord); //1후판압연 Book-In/Book-Out요구	
			}else if("P3YDL501".equals(szTcCode)){
				ydEjbCon.trx("JPlateYdL2RcvFaEJB", "rcvP3BookInOutReq", 	inRecord); //1후판열처리 Book-In/Book-Out요구	
				
			}else if("PRYDJ014".equals(szTcCode)){
				ydEjbCon.trx("JPlateYdL3RcvFaEJB", "rcvPRRentCutResult", 	inRecord); //1후판정정 임가공절단장 절단실적	
			}else if("PRYDJ015".equals(szTcCode)){
				ydEjbCon.trx("JPlateYdL3RcvFaEJB", "rcvPRNextDeciInfo", 	inRecord); //1후판정정 차행선결정정보	
			}else if("PRYDJ016".equals(szTcCode)){
				ydEjbCon.trx("JPlateYdL3RcvFaEJB", "rcvPRYDJ016", 			inRecord); //1후판정정 설비완료실적	
			}else if("P8YDL501".equals(szTcCode)){
				
//-----------------------------------------------------------------------------------------------------------------
// 2024.12.05 1후판 정정 #2 열처리 Book-In/Book-Out 요구 L2 수신 전문 추가			
//-----------------------------------------------------------------------------------------------------------------
				ydEjbCon.trx("JPlateYdL2RcvFaEJB", "rcvP8BookInOutReq", 	inRecord); //1후판열처리 Book-In/Book-Out요구	
				              
			} 

			 
			
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);
		} // end of try catch
		
		szMsg="1후판정정야드 I/F 수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvY2NewInterface()
	
	/**
	 * [1후판정정야드] 크레인상태설정팝업 : 권하처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData updCrnDnPrsBackUp(GridData inDto) throws DAOException {

		String 	szMethodName	= "updCrnDnPrsBackUp";
		String 	szOperationName	= "JspFaEJB - 권하 실적 처리";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szLogMsg 		= "";

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			inRecord.setField("JMS_TC_CD", "Y2YDL009");
			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY2CrnDownWr", inRecord);
			gdRes 	= OperateGridData.cloneResponseGridData(inDto);

			if (JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				gdRes.setStatus("true");
				gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);
				szLogMsg = szOperationName + " (" + szMethodName + ") 권하실적 처리 성공";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			} else {
				gdRes.setStatus("true");
				gdRes.setMessage(rtnMsg);
				szLogMsg = szOperationName + " (" + szMethodName + ") 권하실적처리 실패 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			}

		} catch(Exception e) {
			gdRes.setMessage(e.getMessage());
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	
	/**
	 * [1후판정정야드] 크레인상태설정팝업 : 권하위치 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData updCrnDnPrsFix(GridData inDto) throws DAOException {

		String 	szMethodName	= "updCrnDnPrsFix";
		String 	szOperationName	= "JspFaEJB - 권하위치 변경 처리";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdCrnReSchSeEJB", "updCrnDnPrsFixYdP", inRecord);

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") JPlateYdCrnReSchSeEJB.updCrnDnPrsFixYdP 호출오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	
	/**
	 * [1후판정정야드] 크레인상태설정팝업 : 강제권상
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData updCrnUpPrsOffLine(GridData inDto) throws DAOException {

		String 	szMethodName	= "updCrnUpPrsOffLine";
		String 	szOperationName	= "JspFaEJB - 강제권상 요구 처리";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작 >>>> " + inRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			String 	szYdEqpId  = ydDaoUtils.paraRecChkNull(inRecord, "YD_EQP_ID");
			if ("".equals(ydDaoUtils.paraRecChkNull(inRecord, "YD_EQP_WRK_SH"))) {
				inRecord.setField("YD_EQP_WRK_SH",	ydDaoUtils.paraRecChkNull(inRecord, "YD_WO_CNT"));
			}

			// YD_EQP_ID		야드설비ID
			// YD_UP_WR_LOC		야드권상실적위치
			// YD_UP_WR_LAYER	야드권상실적단
			// YD_CRN_XAXIS		야드크레인X축
			// YD_CRN_YAXIS		야드크레인Y축
			// YD_CRN_ZAXIS		야드크레인Z축
			// YD_EQP_WRK_SH	야드크레인작업매수

			
				// 야드L2 전문수신 EJB기동 : Y2 강제권상요구 [Y2YDL010]
			inRecord.setField("JMS_TC_CD", "Y2YDL010");

			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY2OffCrnUpWr", inRecord);
			szLogMsg = szOperationName + " (" + szMethodName + ") 호출 결과 >>>> " + rtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
			//	throw new DAOException(rtnMsg);
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			gdRes.setMessage(e.getMessage());
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}

	/**
	 * [1후판정정야드] 크레인작업관리화면 : 명령선택
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData wrkCrnSel(GridData gdReq) throws DAOException {

		String 	szMethodName	= "wrkCrnSel";
		String 	szOperationName	= "JspFaEJB - 크레인작업관리_명령선택";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     	gdRes 	= null;
		EJBConnector 	ejbConn = null;
		JDTORecord 		recPara = null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			JDTORecord [] inRecSet = ydComUtil.genJDTORecordSet(gdReq);

			String 	sYdEqpId 		= ydDaoUtils.paraRecChkNull(inRecSet[0], "YD_EQP_ID");
			String 	sYdCrnSchId 	= ydDaoUtils.paraRecChkNull(inRecSet[0], "YD_CRN_SCH_ID");
			String 	sModifier 		= ydDaoUtils.paraRecModifier(inRecSet[0]);
			String	sydCmdPkupGp	= ydDaoUtils.paraRecChkNull(inRecSet[0], "YD_CMD_PKUP_GP", "S");

			// 야드L2 전문수신 EJB기동 : 명령선택처리 (Y2YDL012)
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_EQP_ID", 			sYdEqpId);				// 야드설비ID
		//	recPara.setField("YD_CMD_PKUP_GP", 		"S");					// 야드명령선택구분 - S:명령선택, C:취소
			recPara.setField("YD_CMD_PKUP_GP", 		sydCmdPkupGp);			// 야드명령선택구분 - S:명령선택, C:취소
			recPara.setField("YD_CRN_SCH_ID", 		sYdCrnSchId);			// 야드크레인스케쥴ID
			recPara.setField("MODIFIER", 			sModifier);
			recPara.setField("JMS_TC_CD", 			"Y2YDL012");
			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY2CrnOrderSel", recPara);
			
			gdRes   = OperateGridData.cloneResponseGridData(gdReq);
			gdRes.setMessage(rtnMsg);

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JspFaEJB - "+ szOperationName  +"] 설비고장/정상 설정 에러발생 : " + e.getMessage(), JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>>" + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	
	/**
	 * [2후판정정야드] 크레인상태설정팝업 : 강제권하
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData updCrnDnPrsOffLine(GridData inDto) throws DAOException {

		String 	szMethodName	= "updCrnDnPrsOffLine";
		String 	szOperationName	= "JspFaEJB - 강제권하 요구 처리";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			// 야드L2 전문수신 EJB기동 : Y2 강제권하요구 [Y2YDL011]

			// YD_EQP_ID		야드설비ID
			// YD_EQP_WRK_MODE	야드설비작업Mode
			// YD_WRK_PROG_STAT	야드작업진행상태
			// YD_SCH_CD		야드스케쥴코드
			// YD_CRN_SCH_ID	야드크레인스케쥴ID
			// YD_DN_WR_LOC		야드권하실적위치
			// YD_DN_WR_LAYER	야드권하실적단
			// YD_CRN_XAXIS		야드크레인X축
			// YD_CRN_YAXIS		야드크레인Y축
			// YD_CRN_ZAXIS		야드크레인Z축

			inRecord.setField("JMS_TC_CD", "Y2YDL011");
			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY2OffCrnDnWr", inRecord);
			
			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			gdRes.setMessage(e.getMessage());
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}

	/**
	 * [1후판정정야드] 크레인상태설정팝업 : 설비 고장/정상 설정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData updCrnStsSetCrnStat(GridData inDto) throws DAOException {

		String 	szMethodName	= "updCrnStsSetCrnStat";
		String 	szOperationName	= "JspFaEJB - 설비 고장/정상 설정";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			String 	sCrnStat = ydDaoUtils.paraRecChkNull(inRecord, "CRN_STAT");
			String 	sYmdHms  = JPlateYdUtils.getCurDate("yyyyMMddHHmmss");

			// 야드L2 전문수신 EJB기동 : Y2 설비고장복구실적 [Y2YDL004]
			
			inRecord.setField("JMS_TC_CD"			, "Y2YDL004");
			inRecord.setField("YD_EQP_STAT"			, sCrnStat);			// 설비상태
			inRecord.setField("YD_EQP_PAUSE_CODE"	, "0000");				// 휴지코드
			inRecord.setField("YD_EQP_TRBL_RCVR_DT"	, sYmdHms);				// 야드설비고장복구일시
			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY2EqpTrblRcvrWr", inRecord);

			
			gdRes   = OperateGridData.cloneResponseGridData(inDto);
			gdRes.setMessage(rtnMsg);

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>>" + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}

	/**
	 * [1후판정정야드] 크레인상태설정팝업 : 설비 On-Line/Off-Line 설정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData updCrnStsSetCrnMode(GridData inDto) throws DAOException {

		String 	szMethodName	= "updCrnStsSetCrnMode";
		String 	szOperationName	= "JspFaEJB - 설비 On-Line/Off-Line 설정";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			String 	sCrnMode = ydDaoUtils.paraRecChkNull(inRecord, "CRN_MODE");
			String 	szYdEqpId  = ydDaoUtils.paraRecChkNull(inRecord, "YD_EQP_ID");
			// 야드L2 전문수신 EJB기동 : Y2 설비운전모드전환 [Y2YDL003]
			inRecord.setField("JMS_TC_CD"			, "Y2YDL003");
			inRecord.setField("YD_EQP_WRK_MODE"		, sCrnMode);			// 운전모드

			ejbConn	= new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY2EqpDrvModeChg", inRecord);
			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes.setMessage(rtnMsg);

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>>" + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	
	/**
	 * [1후판정정야드] 크레인상태설정팝업 : 권상처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData updCrnUpPrsBackUp(GridData inDto) throws DAOException {

		String 	szMethodName	= "updCrnUpPrsBackUp";
		String 	szOperationName	= "JspFaEJB - 권상 실적 처리";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			// 야드L2 전문수신 EJB기동 : Y2 ON-LINE권상실적 [Y2YDL008]

			// YD_EQP_ID	        야드설비ID
			// YD_EQP_WRK_MODE	야드설비작업Mode
			// YD_WRK_PROG_STAT	야드작업진행상태
			// YD_SCH_CD	        야드스케쥴코드
			// YD_CRN_SCH_ID	야드크레인스케쥴ID
			// YD_UP_WR_LOC		야드권상실적위치
			// YD_UP_WR_LAYER	야드권상실적단
			// YD_CRN_XAXIS		야드크레인X축
			// YD_CRN_YAXIS		야드크레인Y축
			// YD_CRN_ZAXIS		야드크레인Z축

			inRecord.setField("JMS_TC_CD", "Y2YDL008");
			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY2CrnUpWr", inRecord);
			
			gdRes 	= OperateGridData.cloneResponseGridData(inDto);

			if (JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				gdRes.setStatus("true");
				gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);
				szLogMsg = szOperationName + " (" + szMethodName + ") 권상실적 처리 성공";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			} else {
				gdRes.setStatus("true");
				gdRes.setMessage(rtnMsg);
				szLogMsg = szOperationName + " (" + szMethodName + ") 권상실적처리 실패 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			}

		} catch(Exception e) {
			gdRes.setMessage(e.getMessage());
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	/**
	 * [1후판정정야드] 크레인작업관리화면 : 작업취소 - 작업예약도 취소
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData delWorkYdCrnWorkMgt(GridData gdReq) throws DAOException {

		String 	szMethodName     		= "delWorkYdCrnWorkMgt";
		String 	szOperationName			= "JspFaEJB - 작업관리 (작업취소)";
		String 	szLogMsg         		= "";
		String 	szR_msg          		= "";

		//파라미터 스크링 변수

		String 	szYdCrnSchId  			= "";
		String 	szYdSchCd 				= "";
		String 	szModifier 				= "";
		String	szStlNo					= "";

		JDTORecordSet rsResult 			= JDTORecordFactory.getInstance().createRecordSet("temRs");
		JDTORecord recPara 				= JDTORecordFactory.getInstance().create();
		JDTORecord recCheck 			= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1 			= JDTORecordFactory.getInstance().create();

		JPlateYdCrnSchDAO 	ydCrnSchDao	= new JPlateYdCrnSchDAO();
		GridData 			gdRes 		= null;
		EJBConnector 		ejbConn 	= null;

		String 	sRTN_CD					= "";
		String 	sRTN_MSG				= "";
		int 	intRtnVal 				= 0;
		String 	szMsg 					= "";
		String 	szC_YD_WRK_PROG_STAT 	= "";

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

			for (int ii=0; ii<inRecord.length; ii++) {

				szYdCrnSchId  	= ydDaoUtils.paraRecChkNull(inRecord[ii], "YD_CRN_SCH_ID");
				szYdSchCd 		= ydDaoUtils.paraRecChkNull(inRecord[ii], "YD_SCH_CD");
				szStlNo			= ydDaoUtils.paraRecChkNull(inRecord[ii], "STL_NO");
				szModifier		= ydDaoUtils.paraRecModifier(inRecord[ii]);

				if ("".equals(szYdCrnSchId)) {
					szMsg = "["+szOperationName+"] 스케줄 취소 처리("+szMethodName+") 실패, YD_CRN_SCH_ID값이 없음";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					continue;
				}

				//파라미터 레코드 setting
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", szYdCrnSchId);
				recPara.setField("YD_SCH_CD",     szYdSchCd);
				recPara.setField("DEL_YN",        "N");
				recPara.setField("MODIFIER",      szModifier);

				/*
				 * 크레인 스케줄에서 선택된 스케줄 ID로 작업예약을 조회한 후
				 * 삭제되지 않는 작업예약에 포함된 크레인 스케줄중 첫번째 값이
				 * 2,3 인 경우 후처리를 하지않기 위해  Check Logic 반영
				 */
				rsResult = JDTORecordFactory.getInstance().createRecordSet("temRs");

				intRtnVal = ydCrnSchDao.getCheckYdCrnSchId(recPara, rsResult);		// intGp == 36

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
				recPara.setField("MODIFIER",      szModifier);

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
					gdRes.setMessage(sRTN_MSG);
					m_ctx.setRollbackOnly();
					return gdRes;
				}

				szMsg = "["+szOperationName+"] ---- 작업예약 취소 시작";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
				outRecord1 	= (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { outRecord1 });

				szMsg = "["+szOperationName+"] ---- 작업예약 취소 종료!! >>>> " + outRecord1.toString();
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				if ("0".equals(sRTN_CD)) {
					gdRes.setMessage(sRTN_MSG);
					m_ctx.setRollbackOnly();
					return gdRes;
				}

				// F?RT??LM :: RT BOOK-OUT일때 재료정보 , 적치위치 Clear
				if ("RT".equals(ydUtils.substr(szYdSchCd,2,2)) && "LM".equals(ydUtils.substr(szYdSchCd,6,2))) {

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CRN_SCH_ID", szYdCrnSchId);
					recPara.setField("YD_SCH_CD",     szYdSchCd);
					recPara.setField("STL_NO",        szStlNo);
					recPara.setField("MODIFIER",      szModifier);

					szMsg = "["+szOperationName+"] ---- RT BOOK-OUT 재료정보 CLEAR 시작 >>>> " + recPara.toString();
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					ejbConn 		= new EJBConnector("default", this);
					String rtnMsg	= (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "delStockLocOnRt", recPara);

					szMsg = "["+szOperationName+"] ---- RT BOOK-OUT 재료정보 CLEAR 종료 >>>> " + rtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				}
			}

			szMsg = "[JSP Session : "+szOperationName+"] ----- 끝";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			gdRes.setMessage("정상적으로 취소 처리되었습니다.");

			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szR_msg, JPlateYdConst.DEBUG);

		} catch(DAOException de) {

			gdRes.setMessage("Failure");
			szLogMsg = szOperationName + " (" + szMethodName + ") DAO Exception 발생 >>>> " + de.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);

		} catch(Exception e) {
			gdRes.setMessage("Failure");
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	/**
	 * [1후판정정야드] 크레인작업관리화면 : 스케줄 취소 - 작업예약은 남겨놈
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 *
	 */
	public GridData cancelSchYdCrnWorkMgt(GridData gdReq) throws DAOException {

		String 	szMethodName			= "cancelSchYdCrnWorkMgt";
		String 	szOperationName			= "JspFaEJB - 작업관리 (스케줄 취소)";
		String 	szR_msg 				= "";
		String 	szLogMsg 				= "";

		//파라미터 스크링 변수
		String 	sYD_CRN_SCH_ID  		= "";
		String 	sYD_SCH_CD 				= "";
		String 	sYD_USER_ID 			= "";
		JDTORecordSet rsRtnVal 			= JDTORecordFactory.getInstance().createRecordSet("temRs");
		JDTORecord recPara 				= JDTORecordFactory.getInstance().create();
		JDTORecord recCheck 			= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1 			= JDTORecordFactory.getInstance().create();
//		JDTORecord recEqpPara 			= JDTORecordFactory.getInstance().create();

		String 	sRTN_CD					= "";
		String 	sRTN_MSG				= "";

//		String 	szRtnMsg				= "";
//		String 	szYD_EQP_ID				= "";
		JPlateYdCrnSchDAO ydCrnSchDao 	= new JPlateYdCrnSchDAO();
		GridData gdRes 					= null;
		EJBConnector ejbConn 			= null;
		int 	intRtnVal 				= 0;
		String 	szMsg 					= "";
		String 	szC_YD_WRK_PROG_STAT 	= "";

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

			for (int ii=0; ii<inRecord.length; ii++) {

				sYD_CRN_SCH_ID  = ydDaoUtils.paraRecChkNull(inRecord[ii], "YD_CRN_SCH_ID");
				sYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(inRecord[ii], "YD_SCH_CD");
				sYD_USER_ID 	= ydDaoUtils.paraRecModifier(inRecord[ii]);

				if ("".equals(sYD_CRN_SCH_ID)) {

					szMsg = "스케줄 취소 처리("+szMethodName+") 실패, YD_CRN_SCH_ID값이 없음";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					continue;
				}

				//파라미터 레코드 setting
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID);
				recPara.setField("YD_SCH_CD",     sYD_SCH_CD);
				recPara.setField("DEL_YN",        "N");
				recPara.setField("MODIFIER",      sYD_USER_ID);

				/*
				 * 크레인 스케줄에서 선택된 스케줄 ID로 작업예약을 조회한 후
				 * 삭제되지 않는 작업예약에 포함된 크레인 스케줄중 첫번째 값이
				 * 2,3 인 경우 후처리를 하지않기 위해  Check Logic 반영
				 */
				rsRtnVal  = JDTORecordFactory.getInstance().createRecordSet("temRs");
				intRtnVal = ydCrnSchDao.getCheckYdCrnSchId(recPara, rsRtnVal);			// intGp == 36

				if (intRtnVal < 1) {
					szMsg = "취소 작업을 완료 하였습니다.";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					continue;
				}

				rsRtnVal.first();
				recCheck = rsRtnVal.getRecord();

				szC_YD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recCheck, "YD_WRK_PROG_STAT");

				//2,3, 인 경우 와 혹시모를 4이면서도 스케줄 삭제가 되지 않은 경우
				if ("2".equals(szC_YD_WRK_PROG_STAT) || "3".equals(szC_YD_WRK_PROG_STAT) || "4".equals(szC_YD_WRK_PROG_STAT)) {
					szMsg = "크레인 작업이 완료되지 않았습니다!!";
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
				recPara.setField("MODIFIER",      sYD_USER_ID);

				//크레인스케줄 ID보다 이상인 ID 삭제  업데이트 실행(적치단정보까지 CLEAR)
				//스케줄취소

				szMsg = "스케쥴 취소 시작!!";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
				outRecord1 	 = (JDTORecord)ejbConn.trx("cancelJPlateYdCrnSch", new Class[] { JDTORecord.class }, new Object[] { recPara });

				sRTN_CD		 = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				sRTN_MSG	 = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
//				szYD_EQP_ID	 = StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");

				if ("0".equals(sRTN_CD)) {
					gdRes.setMessage(sRTN_MSG);
					m_ctx.setRollbackOnly();
					return gdRes;
				}

				/*
				//--------------------------------------------------------------------------------
				// 설비가 고장 또는 OFF 라인 상태가 아닐경우
				// 선택된 설비가 취소 되었으므로 해당설비의 설비 테이블정보에
				// 작업대기 상태로 UPDATE 해준다.
				//--------------------------------------------------------------------------------
				recPara  = JDTORecordFactory.getInstance().create();
				szRtnMsg = JPlateYdCommonUtils.checkCrnStat(szYD_EQP_ID, recPara);

				if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					recEqpPara   = JDTORecordFactory.getInstance().create();
					recEqpPara.setField("YD_EQP_ID"		, szYD_EQP_ID);
					recEqpPara.setField("YD_EQP_STAT"	, JPlateYdConst.YD_EQP_STAT_IDLE);
					recEqpPara.setField("MODIFIER"		, sYD_USER_ID);

					szMsg = "[Jsp-Session " + szOperationName+ " ] 크레인("+ szYD_EQP_ID +") 설비상태 [" + JPlateYdConst.YD_EQP_STAT_IDLE +"]로 변경 ------------------";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			        intRtnVal = ydEqpDao.updYdEqpStat(recEqpPara);
				}
				*/
			}// end for

			szLogMsg = szOperationName + " (" + szMethodName + ") 끝";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			gdRes.setMessage("정상적으로 스케줄 취소 처리되었습니다.");

			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szR_msg, JPlateYdConst.DEBUG);

		} catch(Exception e) {
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		return gdRes;
	}
	
	/**
	 * [1후판정정야드] 크레인작업관리화면 : 크레인 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData wrkCrnChange(GridData gdReq) throws DAOException {

		String 	szMethodName    = "wrkCrnChange";
		String 	szOperationName = "JspFaEJB - 크레인상태관리_크레인 변경";
		String 	szLogMsg        = "";
		String 	szRtnValue 		=  JPlateYdConst.RETN_CD_SUCCESS;

		GridData gdRes          = null;
		EJBConnector ejbConn    = null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			szRtnValue = (String)ejbConn.trx("wrkCrnChange", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			gdRes.setMessage(szRtnValue);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnValue)) {
				m_ctx.setRollbackOnly();
			}

		} catch(DAOException de) {

			szLogMsg = szOperationName + " (" + szMethodName + ") DAO Exception 발생 >>>> " + de.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);

			gdRes.setMessage(de.getMessage());
			m_ctx.setRollbackOnly();
			return gdRes;

		} catch(Exception e) {
			//Log
			gdRes.setMessage(szRtnValue);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 ";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	} // end of wrkCrnChange()
	
	/**
	 * [1후판정정야드] 크레인작업관리화면 : 우선순위 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData crnChgSchPrior(GridData gdReq) throws DAOException {

		String 	szMethodName	= "crnChgSchPrior";
		String	szOperationName	= "JspFaEJB - 크레인상태관리[우선순위 변경]";
		String 	szLogMsg		= "";
		boolean bool			= false;
		Boolean boolWrapper		= null;

		GridData 		gdRes   = null;
		EJBConnector 	ejbConn = null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			//2후판거 사용
			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
			boolWrapper = (Boolean)ejbConn.trx("crnChgSchPrior", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			bool = boolWrapper.booleanValue();

			if (bool) {
				gdRes.setStatus("true");
				gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);

				szLogMsg = "우선순위 변경 성공";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			} else {
				gdRes.setStatus("true");
				szLogMsg = "우선순위  변경 실패";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.WARNING);

				gdRes.setMessage(szLogMsg);
				m_ctx.setRollbackOnly();
				return gdRes;
			}

		} catch(DAOException de) {

			szLogMsg = szOperationName + " (" + szMethodName + ") DAO Exception 발생 >>>> " + de.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);

			gdRes.setMessage(de.getMessage());
			m_ctx.setRollbackOnly();
			return gdRes;

		} catch(Exception e) {

			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);

			gdRes.setMessage(e.getMessage());
			m_ctx.setRollbackOnly();
			return gdRes;

		//	throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 ";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);


		return gdRes;
	} // end of crnChgSchPrior()	
	
	/**
	 * [2후판정정야드] 크레인작업관리화면 : 권하위치 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updToPosFix(GridData gdReq) throws DAOException {

		String 	szMethodName	= "updToPosFix";
		String	szOperationName	= "JspFaEJB - 크레인작업관리 화면 [권하위치 변경]";
		String 	szLogMsg        = "";
		boolean bool            = false;
		Boolean boolWrapper     = null;

		GridData 		gdRes	= null;
		EJBConnector 	ejbConn	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			boolWrapper = (Boolean)ejbConn.trx("updToPosFix", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			bool = boolWrapper.booleanValue();

			if (bool) {
				gdRes.setStatus("true");
				szLogMsg = "권하위치 변경 성공";
				gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			} else {
				gdRes.setStatus("true");
				szLogMsg = "권하위치 변경 실패";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);

				gdRes.setMessage(szLogMsg);
				m_ctx.setRollbackOnly();
				return gdRes;
			}

		} catch(DAOException de) {

			szLogMsg = szOperationName + " (" + szMethodName + ") DAO Exception 발생 >>>> " + de.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);

			gdRes.setMessage(de.getMessage());
			m_ctx.setRollbackOnly();
			return gdRes;

		} catch(Exception e) {

			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);

			gdRes.setMessage(e.getMessage());
			m_ctx.setRollbackOnly();
			return gdRes;

		//	throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 ";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}  //end of updToPosFix

	/**
	 * [1후판정정야드] 크레인작업관리화면 : 파일링 처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procPilingRslt(GridData gdReq) throws DAOException {

        String 	szMethodName    = "procPilingRslt";
        String 	szOperationName = "JspFaEJB - 2후판정정 파일링처리";
		String 	szLogMsg        = "";
		String	rtnMsg			= "";
		String	szYdEqpId		= "";
		String	szStlNo 		= "";
		String	szYdCrnSchId 	= "";
		String	szYdPilingGp	= "";
		String	szModifier 		= "";
		String	szYdUpWoLoc	 	= "";
		String	szYdUpWoLayer 	= "";

		GridData 		gdRes	= null;
		EJBConnector 	ejbConn = null;
		JDTORecord		recPara = null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);

			if (inRecord != null && inRecord.length > 0) {
				szYdEqpId    = ydDaoUtils.paraRecChkNull(inRecord[0], "YD_EQP_ID");
				szModifier   = ydDaoUtils.paraRecModifier(inRecord[0]);
				szYdPilingGp = ydDaoUtils.paraRecChkNull(inRecord[0], "YD_PILING_GP");
			}

			// 야드L2 전문수신 EJB기동 : 파일링실적 [Y2YDL013]
			/*
			 * 	YD_EQP_ID			야드설비ID			CHAR	6
			 * 	YD_PILING_SH		파일링매수			NUMBER	2
			 * 	YD_PILING_GP		파일링구분			CHAR	1		(P:파일링, H:횡행작업, M:멀티작업, N:일반작업, F:강제권상)
			 * 	YD_CRN_SCH_ID1		야드크레인스케쥴ID1	CHAR	18		크레인스케줄ID1
			 * 	STL_NO1				재료번호1				CHAR	11		재료번호1
			 * 	YD_UP_WO_LOC1		야드권상지시단1		CHAR	8		권상위치1
			 * 	YD_UP_WO_LAYER1		야드권상지시단1		CHAR	3		파일링단1
			 *			:
			 * 	YD_CRN_SCH_ID15		야드크레인스케쥴ID15	CHAR	18		크레인스케줄ID15
			 * 	STL_NO15			재료번호15			CHAR	11		재료번호15
			 * 	YD_UP_WO_LOC15		야드권상지시단15		CHAR	8		권상위치15
			 * 	YD_UP_WO_LAYER15	야드권상지시단15		CHAR	3		파일링단15
			 */

			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_EQP_ID", 			szYdEqpId);								// 야드설비ID
			recPara.setField("YD_PILING_SH",		Integer.toString(inRecord.length));		// 파일링매수
			recPara.setField("YD_PILING_GP",		szYdPilingGp);							// 파일링구분
			recPara.setField("MODIFIER",			szModifier);

			for(int ii=0; ii<inRecord.length; ii++) {
				szStlNo 		= ydDaoUtils.paraRecChkNull(inRecord[ii], "STL_NO");
				szYdCrnSchId  	= ydDaoUtils.paraRecChkNull(inRecord[ii], "YD_CRN_SCH_ID");
				szYdUpWoLoc	 	= ydDaoUtils.paraRecChkNull(inRecord[ii], "YD_UP_WO_LOC");
				szYdUpWoLayer	= ydDaoUtils.paraRecChkNull(inRecord[ii], "YD_UP_WO_LAYER");

				recPara.setField("YD_CRN_SCH_ID"+(ii+1),	szYdCrnSchId);
				recPara.setField("STL_NO"+(ii+1),			szStlNo);
				recPara.setField("YD_UP_WO_LOC"+(ii+1),		szYdUpWoLoc);
				recPara.setField("YD_UP_WO_LAYER"+(ii+1),	szYdUpWoLayer);
			}

			recPara.setField("JMS_TC_CD", 			"Y2YDL013");
			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY2PilingRslt", recPara);
			
			if (JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				gdRes.setStatus("true");
				gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);
				szLogMsg = "파일링 처리 성공";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			} else {

				m_ctx.setRollbackOnly();

				gdRes.setStatus("true");
				gdRes.setMessage(rtnMsg);
				szLogMsg = "파일링 처리 실패";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.WARNING);
			}

		} catch(DAOException de) {

			szLogMsg = szOperationName + " (" + szMethodName + ") DAO Exception 발생 >>>> " + de.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);

			gdRes.setMessage(de.getMessage());
			m_ctx.setRollbackOnly();
			return gdRes;

		} catch(Exception e) {
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);

			gdRes.setMessage(e.getMessage());
			m_ctx.setRollbackOnly();
			return gdRes;

		//	throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 ";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}  //end of procPilingRslt
	
	/**
	 * [1후판정정야드] 이적대상재 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData insMvWBookId(GridData inDto) throws DAOException {

		String 	szMethodName	= "insMvWBookId";
		String 	szOperationName	= "JspFaEJB - 이적대상재 등록 처리";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		//---------------------------------------------------------------------------------------------
		// 2024.11.14 logId 새로 발본
		//---------------------------------------------------------------------------------------------
		String logId            = ydLogUtils.getLogIdNew("P");
		
		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);

			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			
//---------------------------------------------------------------------------------------------
// 2024.11.14 Argument에 logId 추가 
//			rtnMsg  = (String)ejbConn.trx("insMvWBookId", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			rtnMsg  = (String)ejbConn.trx("insMvWBookId", new Class[] { JDTORecord[].class, String.class }, new Object[] { inRecord, logId });
//---------------------------------------------------------------------------------------------

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		return gdRes;
    } // end of insMvWBookId()
	
	
	/**
	 * 1후판정정 스케줄 기동관리 (수정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updJPlateYdSchStartMgt(GridData gdReq) throws DAOException {

		String	szMethodName	= "updJPlateYdSchStartMgt";
		String	szOperationName	= "JspFaEJB - 스케줄 기동관리[수정]";
		String 	szLogMsg		= "";

		GridData		gdRes	= null;
		EJBConnector	ejbConn = null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			//2후판거 사용
			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
			ejbConn.trx("updJPlateYdSchStartMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			szLogMsg = szOperationName + " (" + szMethodName + ") 끝";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			gdRes.setStatus("true");
			gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);
			return gdRes;

		} catch(Exception e) {
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}  //end of updJPlateYdSchStartMgt
	
	/**
	 * 1후판정정야드 저장위치별 정보 조회화면 : 조업L3 저장위치 정보 재전송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData sndYdLocInfoList(GridData inDto) throws DAOException {

		String 	szMethodName	= "sndYdLocInfoList";
		String 	szOperationName	= "JspFaEJB - 조업L3 저장위치 정보 재전송";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szOperationName  + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			szLogMsg = szOperationName + " (" + szMethodName + ") ydComUtil.genJDTORecordSet 호출 START >>>>";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

			szLogMsg = szOperationName + " (" + szMethodName + ") ydComUtil.genJDTORecordSet 호출 END >>>>";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//
			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("sndYdLocInfoList", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szOperationName  + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	/**
	 * [1후판정정야드] 야드긴급재 등록/취소
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData updYdUgntGp(GridData inDto) throws DAOException {

		String 	szMethodName	= "updYdUgntGp";
		String 	szOperationName	= "JspFaEJB - 야드긴급재 등록/취소";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "updYdUgntGp", inRecord);

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			//	throw new DAOException(rtnMsg);

				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	/**
	 * [1후판정정야드] 시편채취 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData updRgntPkGp(GridData inDto) throws DAOException {

		String 	szMethodName	= "updRgntPkGp";
		String 	szOperationName	= "JspFaEJB - 시편채취 등록/취소";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			//2후판거 사용
			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdJspSeEJB", "updRgntPkGp", inRecord);

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			//	throw new DAOException(rtnMsg);

				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	/**
	 * [1후판정정야드] 적치열검수 SET
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData updCheckUp(GridData inDto) throws DAOException {

		String 	szMethodName	= "updCheckUp";
		String 	szOperationName	= "JspFaEJB - 적치열검수 등록/취소";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			//2후판거 사용
			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdJspSeEJB", "updCheckUp", inRecord);

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			//	throw new DAOException(rtnMsg);

				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	/**
	 * [1후판정정야드] 야드Map관리 열 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws DAOException
	 */
	public GridData updJPlateYdStkPosSet(GridData inDto) throws DAOException {

		String 	szMethodName	= "updJPlateYdStkPosSet";
		String 	szOperationName	= "JspFaEJB - 저장위치 좌표설정화면 열 수정 ";
		String 	szLogMsg 		= "";

		GridData 		gdRes 	= null;
		EJBConnector 	ejbConn	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);

			ejbConn.trx("updJPlateYdStkPosSet",	new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);

		} catch(Exception e) {
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 ";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	
	/**
	 * [1후판정정야드] 야드Map관리 베드수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws DAOException
	 */
	public GridData updJPlateYdStkPosSetBed(GridData inDto) throws DAOException {

		String 	szMethodName	= "updJPlateYdStkPosSetBed";
		String 	szOperationName	= "JspFaEJB - 저장위치 좌표설정화면 BED 수정";
		String 	szLogMsg 		= "";

		GridData 		gdRes	= null;
		EJBConnector 	ejbConn = null;
		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			// JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			// 수정부는 ydComUtil 로 바꾸어야한다.왜냐하면 콤보박스의 정보가 없을경우
			// 공통에서 Null Point Exception 발생(개발계 발생, 운영계는 발생X)

			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);

			ejbConn.trx("updJPlateYdStkPosSetBed", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);

		} catch(Exception e) {
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 ";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}


	/**
	 * [1후판정정야드] 작업예약관리화면 : 작업예약 삭제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 */
	public GridData delYdWrkBookListjm(GridData gdReq) throws DAOException {

		String 	szMethodName     		= "delYdWrkBookListjm";
		String 	szOperationName			= "JspFaEJB - 작업예약관리_작업예약 삭제";
		String 	szLogMsg         		= "";

		JDTORecord recPara 				= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1 			= JDTORecordFactory.getInstance().create();
		JDTORecord recDelPara 			= JDTORecordFactory.getInstance().create();

		GridData 			gdRes 		= null;
		EJBConnector 		ejbConn 	= null;

		//파라미터 스크링 변수
		String 	szYD_WBOOK_ID  			= "";
		String 	szYD_SCH_CD 			= "";
		String 	szYD_USER_ID 			= "";
		String 	szRTN_CD				= "";
		String 	szRTN_MSG				= "";
		String 	szJMS_TC_CD				= "";
		String 	szYD_EQP_ID				= "";
		String 	szYD_WRK_PROG_STAT 		= "";
		String 	szRTN_SND    			= "N";
		String 	sCANCEL_SEND 			= "N";

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

			for (int ii=0; ii<inRecord.length; ii++) {

				szYD_WBOOK_ID	= ydDaoUtils.setDataDefault(inRecord[ii].getField("YD_WBOOK_ID"), "");
				szYD_SCH_CD 	= ydDaoUtils.setDataDefault(inRecord[ii].getField("YD_SCH_CD"), "");
				szYD_EQP_ID 	= ydDaoUtils.setDataDefault(inRecord[ii].getField("YD_EQP_ID"), "");
				szYD_USER_ID 	= ydDaoUtils.setDataDefault(inRecord[ii].getField("YD_USER_ID"), "");

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("MODIFIER",      	szYD_USER_ID);
				recPara.setField("YD_WBOOK_ID",		szYD_WBOOK_ID);
				recPara.setField("YD_EQP_ID",       szYD_EQP_ID);
				recPara.setField("YD_SCH_CD",     	szYD_SCH_CD);

				szLogMsg = szOperationName + " (" + szMethodName + ") 작업예약 삭제 시작!!";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
				outRecord1 	= (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { recPara });

				szLogMsg = szOperationName + " (" + szMethodName + ") 작업예약 삭제 종료!!";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				szRTN_CD			= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				szJMS_TC_CD			= StringHelper.evl(outRecord1.getFieldString("MSG_ID"), "");
				szYD_EQP_ID			= StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");
				szYD_WRK_PROG_STAT	= StringHelper.evl(outRecord1.getFieldString("YD_WRK_PROG_STAT"), "");
				szYD_SCH_CD			= StringHelper.evl(outRecord1.getFieldString("YD_SCH_CD"), "");
				szRTN_SND			= StringHelper.evl(outRecord1.getFieldString("RTN_SND"), "N");

				if ("0".equals(szRTN_CD)) {
					gdRes.setMessage(szRTN_MSG);
					m_ctx.setRollbackOnly();
					return gdRes;
				}

				if ("Y".equals(szRTN_SND) && "Y".equals(sCANCEL_SEND)) {

					JPlateYdDelegate ydDelegate = new JPlateYdDelegate();

					szLogMsg = szOperationName + " (" + szMethodName + ") 크레인 작업지시 정보를 내부QUEUE로 송신 합니다";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

					recDelPara = JDTORecordFactory.getInstance().create();
					recDelPara.setField("MSG_ID", 			szJMS_TC_CD);
					recDelPara.setField("YD_EQP_ID", 		szYD_EQP_ID);
					recDelPara.setField("YD_WRK_PROG_STAT",	szYD_WRK_PROG_STAT);
					recDelPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
					ydDelegate.sendMsg(recDelPara);
				}
			}

		//	gdRes.setMessage("정상적으로 취소 처리되었습니다.");
			gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);

		} catch(DAOException de) {

			gdRes.setMessage("Failure");
			szLogMsg = szOperationName + " (" + szMethodName + ") DAO Exception 발생 >>>> " + de.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);

		} catch(Exception e) {
			gdRes.setMessage("Failure");
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}	
	
	/**
	 * [1후판정정야드]작업예약관리화면 : 스케쥴기동
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 */
	public GridData trxRunSchedule(GridData gdReq) throws DAOException {

		String 	szMethodName    = "trxRunSchedule";
		String 	szOperationName	= "JspFaEJB - 작업예약관리_스케쥴기동";
		String 	szRtnMsg        = "";
		String 	szLogMsg        = "";

		JDTORecord recSchPara	= JDTORecordFactory.getInstance().create();

		GridData 		gdRes 	= null;
		EJBConnector	ejbConn	= null;

		//파라미터 스크링 변수
		String 	szYD_WBOOK_ID  	= "";
		String 	szYD_SCH_CD 	= "";
		String 	szYD_USER_ID 	= "";
		String 	szYD_EQP_ID		= "";
		int		iCrnSchCnt		= 0;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			
			//String sNEW_MODULE_EFF_YN_A011 = "N";
			//String sNEW_MODULE_EFF_YN_A015 = "N";

			//JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
			//sNEW_MODULE_EFF_YN_A011 = effYnDao.getNewModuleEffYn("A011"); //1후판정정야드 작업예약 스케줄기동시 RT인 경우 FROM위치 체크안함 여부 
			//ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(WKBOOK)---[[[ 1후판정정야드 작업예약 스케줄기동시 RT인 경우 FROM위치 체크안함 여부 : "+sNEW_MODULE_EFF_YN_A011+" ]]]---", JPlateYdConst.DEBUG);
			
			//sNEW_MODULE_EFF_YN_A015 = effYnDao.getNewModuleEffYn("A015"); //1후판정정야드 작업예약 스케줄기동시 이적인 경우 FROM위치 체크안함 여부
			//ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(WKBOOK)---[[[ 1후판정정야드 작업예약 스케줄기동시 이적인 경우 FROM위치 체크안함 여부 : "+sNEW_MODULE_EFF_YN_A015+" ]]]---", JPlateYdConst.DEBUG);
			

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

			iCrnSchCnt = 0;
			for (int ii=0; ii<inRecord.length; ii++) {

				szYD_WBOOK_ID	= ydDaoUtils.paraRecChkNull(inRecord[ii], "YD_WBOOK_ID");
				szYD_SCH_CD 	= ydDaoUtils.paraRecChkNull(inRecord[ii], "YD_SCH_CD");
				szYD_EQP_ID 	= ydDaoUtils.paraRecChkNull(inRecord[ii], "YD_EQP_ID");
				szYD_USER_ID 	= ydDaoUtils.paraRecChkNull(inRecord[ii], "YD_USER_ID");

				//----------------------------------
				// 스케줄기동
				//----------------------------------
				recSchPara = JDTORecordFactory.getInstance().create();
				recSchPara.setField("MSG_ID", 		"YDYDJ");				//TC코드
				recSchPara.setField("YD_EQP_ID", 	szYD_EQP_ID);			//크레인설비ID
				recSchPara.setField("YD_SCH_CD",	szYD_SCH_CD);			//크레인스케줄코드
				recSchPara.setField("YD_WBOOK_ID",	szYD_WBOOK_ID);			//작업예약ID
				recSchPara.setField("REGISTER", 	szYD_USER_ID);
				recSchPara.setField("MODIFIER", 	szYD_USER_ID);
				
				recSchPara.setField("CHK_FROM_LOC",		"N");		//권상예약 체크 안하도록 보완

				//if(sNEW_MODULE_EFF_YN_A011.equals("Y")) {
				//	if("RT".equals(szYD_SCH_CD.substring(2,4))) {
				//		recSchPara.setField("CHK_FROM_LOC",		"N");		//RT작업일경우 권상예약 체크 안하도록 보완
				//	}
				//}
				
				//if(sNEW_MODULE_EFF_YN_A015.equals("Y")) {
				//	if("YD".equals(szYD_SCH_CD.substring(2,4))) {
				//		recSchPara.setField("CHK_FROM_LOC",		"N");		//이적작업일경우 권상예약 체크 안하도록 보완
				//	}
				//}
				
				szLogMsg = szOperationName + " (" + szMethodName + ") 스케쥴기동 시작!! >>>> 스케쥴ID :: " + szYD_WBOOK_ID;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				szLogMsg = szOperationName + " (" + szMethodName + ") 스케줄기동 START :: " + szYD_WBOOK_ID;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				
    	        ejbConn = new EJBConnector("default", "JPlateYdCrnSchYdPSeEJB", this);
    	        szRtnMsg = (String)ejbConn.trx("procCrnSchMainYdP", new Class[] { JDTORecord.class }, new Object[] { recSchPara });
    	        
				szLogMsg = szOperationName + " (" + szMethodName + ") 스케줄기동 END :: " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

    	        if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {

    	        	// 2013.07.24 야드 L2 전송이후에는 RollBack 안되도록 보완
    	        	// 크레인작업지시 1건이상 생성후에는 작업예약 남겨놓고 권하시 크레인 지시 다시 생성
    	        	if (iCrnSchCnt > 0) {
	    	        	szRtnMsg = "이적 스케줄기동 오류 .. 계속진행 .. <br>" + szRtnMsg;
	    	        	szLogMsg = szOperationName + " (" + szMethodName + ") " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
    	        	} else {
	    	        	szRtnMsg = "이적 스케줄기동 오류 .. <br>" + szRtnMsg;
	    	        	szLogMsg = szOperationName + " (" + szMethodName + ") " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
						gdRes.setMessage(szRtnMsg);
						m_ctx.setRollbackOnly();
						return gdRes;
    	        	}
    	        }

    	        iCrnSchCnt ++;

			}	// end for

		//	gdRes.setMessage("정상적으로 스케쥴 기동 처리되었습니다.");
			gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);

		} catch(DAOException de) {

			gdRes.setMessage("Failure");
			szLogMsg = szOperationName + " (" + szMethodName + ") DAO Exception 발생 >>>> " + de.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);

		} catch(Exception e) {
			gdRes.setMessage("Failure");
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	
	/**
	 * [1후판정정야드]작업예약관리화면 : 스케쥴점검
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 */
	public GridData chkCrnSchRunnable(GridData gdReq) throws JDTOException {

		String 	szMethodName 	= "chkCrnSchRunnable";
		String 	szOperationName	= "JspFaEJB - 2후판정정 스케줄점검";
		String 	szLogMsg 		= "";
		String 	szRtnMsg		= null;

		GridData 		gdRes 	= null;
		EJBConnector 	ejbConn = null;
		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			gdRes = OperateGridData.cloneResponseGridData(gdReq);

			JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(gdReq);

			szLogMsg = szOperationName + " (" + szMethodName + ") 스케쥴검점 호출 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//2후판거 사용
			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("chkCrnSchRunnable", new Class[] { JDTORecord[].class },	new Object[] { inRecord });

			szLogMsg = szOperationName + " (" + szMethodName + ") 스케쥴검점 호출 완료 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		} catch (Exception e) {
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}

		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;

	} // end of chkCrnSchRunnable
	/**
	 * [1후판정정야드]작업예약관리화면 : TO위치변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 */
	public GridData trxUpdToLoc(GridData gdReq) throws DAOException {

		String 	szMethodName     	= "trxUpdToLoc";
		String 	szOperationName		= "JspFaEJB - 작업예약관리_TO위치변경";
		String 	szLogMsg         	= "";
		String 	szRtnMsg			= "";

		JDTORecord 		recSchPara	= JDTORecordFactory.getInstance().create();

		GridData 		gdRes 		= null;
		EJBConnector	ejbConn 	= null;

		//파라미터 스크링 변수
		String 	szYdWbookId  		= "";
		String 	szYdToLocGuide 		= "";
		String 	szYdSchPrior		= "";
		String 	szModifier 			= "";

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

			for (int ii=0; ii<inRecord.length; ii++) {

				szYdWbookId		= ydDaoUtils.paraRecChkNull(inRecord[ii], "YD_WBOOK_ID");
				szYdToLocGuide 	= ydDaoUtils.paraRecChkNull(inRecord[ii], "YD_TO_LOC_GUIDE");
				szYdSchPrior 	= ydDaoUtils.paraRecChkNull(inRecord[ii], "YD_SCH_PRIOR");
				szModifier 		= ydDaoUtils.paraRecModifier(inRecord[ii]);

				//----------------------------------
				// 작업예약 TO위치변경
				//----------------------------------
				recSchPara = JDTORecordFactory.getInstance().create();
				recSchPara.setField("MSG_ID", 			"YDYDJ");				// TC코드
				recSchPara.setField("YD_WBOOK_ID",		szYdWbookId);			// 작업예약ID
				recSchPara.setField("YD_TO_LOC_GUIDE",	szYdToLocGuide);		// TO위치
				recSchPara.setField("YD_SCH_PRIOR",		szYdSchPrior);			// 우선순위
				recSchPara.setField("REGISTER", 		szModifier);
				recSchPara.setField("MODIFIER", 		szModifier);

				szLogMsg = szOperationName + " (" + szMethodName + ") 호출시작!! >>>> 스케쥴ID :: " + szYdWbookId;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				//2후판거 사용
    	        ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
    	        szRtnMsg = (String)ejbConn.trx("updWBookToLoc", new Class[] { JDTORecord.class }, new Object[] { recSchPara });

    	        szLogMsg = szOperationName + " (" + szMethodName + ") 호출완료 >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

    	        if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
    	        	szRtnMsg = "작업예약 TO위치변경 오류 .. <br>" + szRtnMsg;
    	        	szLogMsg = szOperationName + " (" + szMethodName + ") " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
					gdRes.setMessage(szRtnMsg);
					m_ctx.setRollbackOnly();
					return gdRes;
    	        }
			}	// end for

		//	gdRes.setMessage("정상적으로 스케쥴 기동 처리되었습니다.");
			gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);

		} catch(DAOException de) {

			gdRes.setMessage("Failure");
			szLogMsg = szOperationName + " (" + szMethodName + ") DAO Exception 발생 >>>> " + de.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);

		} catch(Exception e) {
			gdRes.setMessage("Failure");
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}

	/**
	 * [1후판정정야드] 1후판정정 작업메시지 전송 [YDY2L007 : 크레인작업메시지]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData sendYdMsg(GridData inDto) throws DAOException {

		String 	szMethodName	= "sendYdMsg";
		String 	szOperationName	= "JspFaEJB - 작업메시지 전송";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		String	szYdMsgGp		= "";
		String	szYdBayGp		= "";
		String	szYdEqpId		= "";
		String	szYdWrkMsg		= "";

		JDTORecord recSndPara 	= JDTORecordFactory.getInstance().create();

		JPlateYdDelegate ydDelegate = new JPlateYdDelegate();

		GridData     gdRes 		= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			szYdBayGp	= ydDaoUtils.paraRecChkNull(inRecord, "YD_BAY_GP");
			szYdEqpId	= ydDaoUtils.paraRecChkNull(inRecord, "YD_EQP_ID");
			szYdWrkMsg	= ydDaoUtils.paraRecChkNull(inRecord, "YD_SEND_MSG");

			// 1:전체, 2:해당동, 3:해당설비
			if ("".equals(szYdBayGp)) {
				szYdMsgGp = "1";
			} else {
				if ("".equals(szYdEqpId)) {
					szYdMsgGp = "2";
				} else {
					szYdMsgGp = "3";
				}
			}

			recSndPara = JDTORecordFactory.getInstance().create();
			recSndPara.setField("MSG_ID", 			"YDY2L007");			// YDY2L007 : 크레인작업메세지
			recSndPara.setField("YD_MSG_GP", 		szYdMsgGp);
			recSndPara.setField("YD_BAY_GP", 		szYdBayGp);
			recSndPara.setField("YD_EQP_ID", 		szYdEqpId);
			recSndPara.setField("YD_WRK_MSG",		szYdWrkMsg);
			rtnMsg  = ydDelegate.sendMsg(recSndPara);

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			//	throw new DAOException(rtnMsg);

				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	/**
	 * [2후판정정야드] 저장위치수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData updYdLocInfo(GridData inDto) throws DAOException {

		String 	szMethodName	= "updYdLocInfo";
		String 	szOperationName	= "JspFaEJB - 저장위치 수정 처리";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "updYdLocInfo", inRecord);

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 호출오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			//	throw new DAOException(rtnMsg);

				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}

	/**
	 * [2후판정정야드] 저장위치삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData delYdLocInfo(GridData inDto) throws DAOException {

		String 	szMethodName	= "delYdLocInfo";
		String 	szOperationName	= "JspFaEJB - 저장위치 삭제 처리";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "delYdLocInfo", inRecord);

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 호출오류 발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);

				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}

	/**
	 * [1후판정정야드] 저장위치LIST 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData updYdLocList(GridData inDto) throws DAOException {

		String 	szMethodName	= "updYdLocList";
		String 	szOperationName	= "JspFaEJB - 저장위치LIST 수정 처리";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			szLogMsg = szOperationName + " (" + szMethodName + ") ydComUtil.genJDTORecordSet 호출 START";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		//	운영에서  NullPointerException 발생하여 CmUtil ---->  ydComUtil 로 변경
		//	JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

			szLogMsg = szOperationName + " (" + szMethodName + ") ydComUtil.genJDTORecordSet 호출 END";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("updYdLocList", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 호출오류 발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);

				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	/**
	 * [1후판정정야드] RT모니터링 : Book-Out 요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procPlateYdBookOutReq(GridData inDto) throws DAOException {

		String 	szMethodName	= "procPlateYdBookOutReq";
		String 	szOperationName	= "JspFaEJB - BOOK-OUT 요구";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			// 야드L2 전문수신 EJB기동 : S1 2후판전단L2 Book-In/Book-Out요구 수신 [S1YDL013]
			/*
			inRecord.setField("JMS_TC_CD", 				"S1YDL013");
			inRecord.setField("PLATE_ID",				ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"));		// PLATE_NO
			inRecord.setField("OPERATION_TYPE",			"2");												// 1:Book In, 2:Book Out
			inRecord.setField("OPERATION_SOURCE",		"");												// From
			inRecord.setField("OPERATION_DESTINATION",	"");												// To
			inRecord.setField("REASON_CODE",			"");												// Book-Out원인
			inRecord.setField("OPERATION_MODE",			"1");												// 1:one time 2:Start 3:End
			inRecord.setField("OPERATION_BED",			"");												// Book in/Book-Out시 야드적치Bed번호
			inRecord.setField("OPERATION_DATE",			"");												// Book out 일시
			*/

			ejbConn	= new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procP2P3BookInOutReq", inRecord);
			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
			//	throw new DAOException(rtnMsg);
			//	DAOException 발생시 메시지가 출력안되서 일단 Rollback 처리시킴 ..

				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생으로 Rollback 처리함 ..";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

	            m_ctx.setRollbackOnly();
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>>" + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}	
	/**
	 * [1후판정정야드] 보수장모니터링 : 추출 요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procPlateYdBsOutReq(GridData inDto) throws DAOException {

		String 	szMethodName	= "procPlateYdBsOutReq";
		String 	szOperationName	= "JspFaEJB - 추출 요구(보수장)";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);

			rtnMsg = (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "insBsOutWBook", inRecord);

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}	
	/**
	 * [1후판정정야드] 임가공절단장 모니터링 : 보급 요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procPlateYdBcInReq(GridData inDto) throws DAOException {

		String 	szMethodName	= "procPlateYdBcInReq";
		String 	szOperationName	= "JspFaEJB - 보급 요구( 임가공절단장  )";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);

			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("insBcInWBook", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}

	/**
	 * [1후판정정야드] 임가공절단장 모니터링 : 추출 요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procPlateYdBcOutReq(GridData inDto) throws DAOException {

		String 	szMethodName	= "procPlateYdBcOutReq";
		String 	szOperationName	= "JspFaEJB - 추출 요구(임가공절단장)";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szLogMsg 		= "";

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);

			rtnMsg = (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "insBcOutWBook", inRecord);

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}

	/**
	 * [1후판정정야드] 임가공절단장모니터링 : 스크랩 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procPlateYdBcScrap(GridData inDto) throws DAOException {

		String 	szMethodName	= "procPlateYdBcScrap";
		String 	szOperationName	= "JspFaEJB - 임가공절단장 스크랩 처리";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);

			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("procBcScrap", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	/**
	 * [2후판정정야드] CNC모니터링 : 보급 요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procPlateYdCncInReq(GridData inDto) throws DAOException {

		String 	szMethodName	= "procPlateYdCncInReq";
		String 	szOperationName	= "JspFaEJB - 보급 요구(가스장)";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);

			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("insCncInWBook", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}

	/**
	 * [2후판정정야드] CNC모니터링 : 추출 요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procPlateYdCncOutReq(GridData inDto) throws DAOException {

		String 	szMethodName	= "procPlateYdCncOutReq";
		String 	szOperationName	= "JspFaEJB - 추출 요구(가스장)";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szLogMsg 		= "";

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);

			rtnMsg = (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "insCncOutWBook", inRecord);

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}

	/**
	 * CNC모니터링_정정야드재료 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData getYdStockWithLoc(GridData inDto) throws DAOException {

		String 	szMethodName	= "getYdStockWithLoc";
		String 	szOperationName	= "JspFaEJB - 정정야드 재료정보 조회";
		String 	szLogMsg 		= "";

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet) ejbConn.trx("JPlateYdJspSeEJB", "getYdStockWithLoc", inRecord);

			gdRes = CmUtil.genGridData(inDto , recordSet);
		} catch(Exception e) {
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}

	/**
	 * [2후판정정야드] CNC모니터링 : 스크랩 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procPlateYdCncScrap(GridData inDto) throws DAOException {

		String 	szMethodName	= "procPlateYdCncScrap";
		String 	szOperationName	= "JspFaEJB - CNC 스크랩 처리";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);

			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("procCncScrap", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	
	/**
	 * GridData - 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getSelectData(GridData gdReq) throws DAOException {
		String methodNm =  "조회[JPlateYDYDPJspFaEJB.getSelectData]";
		String logId = commUtils.getLogId();
		
		try {

//PIDEV
//			String toQuery_ID = ydPICommDAO.getYdRulePI("", methodNm, "YD0001", commUtils.trim(gdReq.getParam("query_id")), "APPPI0", "*", "*" );
//			gdReq.addParam("query_id", commUtils.trim(toQuery_ID));				
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
			                    + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of getSelectData	
	
	/**
	 * 광폭 지정 가능 여부 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updWdwAbleYn(GridData gdReq) throws DAOException {
		String methodNm =  "광폭 지정 가능 여부 변경[JPlateYDYDPJspFaEJB.updWdwAbleYn]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			ejbConn.trx("updWdwAbleYn", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updWdwAbleYn
	
	/**
	 * 광폭 지정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updWdwSet(GridData gdReq) throws DAOException {
		String methodNm =  "광폭 지정[JPlateYDYDPJspFaEJB.updWdwSet]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			ejbConn.trx("updWdwSet", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updWdwSet
	
	/**
	 * 광폭 해제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updWdwRel(GridData gdReq) throws DAOException {
		String methodNm =  "광폭 해제[JPlateYDYDPJspFaEJB.updWdwRel]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			ejbConn.trx("updWdwRel", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updWdwRel

	/**
	 * [1후판정정야드] 보수장모니터링 : 보급 요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procPlateYdBsInReq(GridData inDto) throws DAOException {

		String 	szMethodName	= "procPlateYdBsInReq";
		String 	szOperationName	= "JspFaEJB - 보급 요구(보수장)";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);

			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("insBsInWBook", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	
	/**
	 * 1후판정정야드 저장위치별 정보 조회화면 : 조업L3 저장위치 정보 재전송2
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData sndYdLocInfoList2(GridData inDto) throws DAOException {

		String 	szMethodName	= "sndYdLocInfoList2";
		String 	szOperationName	= "JspFaEJB - 조업L3 저장위치 정보 재전송2";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szOperationName  + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			szLogMsg = szOperationName + " (" + szMethodName + ") ydComUtil.genJDTORecordSet 호출 START >>>>";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

			szLogMsg = szOperationName + " (" + szMethodName + ") ydComUtil.genJDTORecordSet 호출 END >>>>";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//
			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("sndYdLocInfoList2", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szOperationName  + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	
	/**
	 * [1후판정정야드] 저장위치수정2 (BOOK-OUT 실적 BACKUP)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData updYdLocInfo2(GridData inDto) throws DAOException {

		String 	szMethodName	= "updYdLocInfo2";
		String 	szOperationName	= "JspFaEJB - 저장위치 수정 처리 (BOOK-OUT 실적 BACKUP)";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "updYdLocInfo2", inRecord);

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 호출오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			//	throw new DAOException(rtnMsg);

				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	
	/**
	 * [1후판정정야드] 저장위치삭제2 (BOOK-IN 실적 BACKUP) 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData delYdLocInfo2(GridData inDto) throws DAOException {

		String 	szMethodName	= "delYdLocInfo2";
		String 	szOperationName	= "JspFaEJB - 저장위치 삭제 처리2 (BOOK-IN 실적 BACKUP)";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "delYdLocInfo2", inRecord);

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 호출오류 발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);

				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	
	/**
	 * [1후판정정야드] RT모니터링 : Book-Out 요구(2)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procPlateYdBookOutReq2(GridData inDto) throws DAOException {

		String 	szMethodName	= "procPlateYdBookOutReq2";
		String 	szOperationName	= "JspFaEJB - BOOK-OUT 요구(2)";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			// 야드L2 전문수신 EJB기동 : S1 2후판전단L2 Book-In/Book-Out요구 수신 [S1YDL013]
			/*
			inRecord.setField("JMS_TC_CD", 				"S1YDL013");
			inRecord.setField("PLATE_ID",				ydDaoUtils.paraRecChkNull(inRecord, "STL_NO"));		// PLATE_NO
			inRecord.setField("OPERATION_TYPE",			"2");												// 1:Book In, 2:Book Out
			inRecord.setField("OPERATION_SOURCE",		"");												// From
			inRecord.setField("OPERATION_DESTINATION",	"");												// To
			inRecord.setField("REASON_CODE",			"");												// Book-Out원인
			inRecord.setField("OPERATION_MODE",			"1");												// 1:one time 2:Start 3:End
			inRecord.setField("OPERATION_BED",			"");												// Book in/Book-Out시 야드적치Bed번호
			inRecord.setField("OPERATION_DATE",			"");												// Book out 일시
			*/

			ejbConn	= new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procP2P3BookInOutReq2", inRecord);
			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
			//	throw new DAOException(rtnMsg);
			//	DAOException 발생시 메시지가 출력안되서 일단 Rollback 처리시킴 ..

				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생으로 Rollback 처리함 ..";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

	            m_ctx.setRollbackOnly();
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>>" + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}	
	
	/**
	 * 차량 지정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData doCarNoSet(GridData gdReq) throws DAOException {
		String methodNm =  "차량 지정[JPlateYDYDPJspFaEJB.doCarNoSet]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			ejbConn.trx("doCarNoSet", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of doCarNoSet

	/**
	 * 차량 해제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData doCarNoClear(GridData gdReq) throws DAOException {
		String methodNm =  "차량 해제[JPlateYDYDPJspFaEJB.doCarNoClear]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			ejbConn.trx("doCarNoClear", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of doCarNoClear
	
	
	/**
	 * [1후판정정야드] 차량작업관리 : 차량이송백업처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData doTrSendBackup(GridData gdReq) throws DAOException {
		String methodNm =  "차량이송백업처리[JPlateYDYDPJspFaEJB.doTrSendBackup]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("doTrSendBackup", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			gdRet.setMessage(jrRtn.getFieldString("RTN_MSG"));
			if(!JPlateYdConst.RETN_CD_SUCCESS.equals(jrRtn.getFieldString("RTN_MSG"))) {
				gdRet.setStatus("false");
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}	
	
	/**
	 * [1후판정정야드] 차량작업관리 : 차량포인트 초기화
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData doTrPointInit(GridData gdReq) throws DAOException {
		String methodNm =  "차량포인트 초기화[JPlateYDYDPJspFaEJB.doTrPointInit]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("doTrPointInit", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			gdRet.setMessage(jrRtn.getFieldString("RTN_MSG"));
			if(!JPlateYdConst.RETN_CD_SUCCESS.equals(jrRtn.getFieldString("RTN_MSG"))) {
				gdRet.setStatus("false");
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}		

	/**
	 * [1후판정정야드] 차량작업관리 : 사외이송백업처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData doTrSendOutSideBackup(GridData gdReq) throws DAOException {
		String methodNm =  "사외이송백업처리[JPlateYDYDPJspFaEJB.doTrSendOutSideBackup]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			ejbConn.trx("doTrSendOutSideBackup", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}	
	
	
	/**
	 * [1후판정정야드] 차량작업관리 : 상차 요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procPlateYdPtInReq(GridData inDto) throws DAOException {

		String 	szMethodName	= "procPlateYdPtInReq";
		String 	szOperationName	= "JspFaEJB - 상차 요구(차량)";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);

			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("insPtInWBook", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	
	/**
	 * [1후판정정야드] 차량작업관리 : 하차 요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procPlateYdPtOutReq(GridData inDto) throws DAOException {

		String 	szMethodName	= "procPlateYdPtOutReq";
		String 	szOperationName	= "JspFaEJB - 하차 요구(차량)";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);

			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("insPtOutWBook", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	
	/**
	 * [1후판정정야드] 차량작업관리 : 상차완료
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procPlateYdPtInCmplt(GridData geReq) throws DAOException {

		String 	szMethodName	= "procPlateYdPtInCmplt";
		String 	szOperationName	= "JspFaEJB - 상차 완료";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			//JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);

			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("procPlateYdPtInCmplt", new Class[] { GridData.class }, new Object[] { geReq });

			gdRes 	= OperateGridData.cloneResponseGridData(geReq);
			
			

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}	
	
	/**
	 * [1후판정정야드] 대차작업관리 : 상차 요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procPlateYdTcInReq(GridData inDto) throws DAOException {

		String 	szMethodName	= "procPlateYdTcInReq";
		String 	szOperationName	= "JspFaEJB - 상차 요구(대차)";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);

			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("insTcInWBook", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}	
	
	/**
	 * [1후판정정야드] 대차작업관리 : 하차 요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procPlateYdTcOutReq(GridData inDto) throws DAOException {

		String 	szMethodName	= "procPlateYdTcOutReq";
		String 	szOperationName	= "JspFaEJB - 하차 요구(대차)";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);

			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("insTcOutWBook", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}	
	
	/**
	 * [1후판정정야드] 대차작업관리 : 대차도착처리백업
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData doTcarSendBackup(GridData gdReq) throws DAOException {
		String methodNm =  "대차도착처리백업[JPlateYDYDPJspFaEJB.doTcarSendBackup]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("doTcarSendBackup", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			gdRet.setMessage(jrRtn.getFieldString("RTN_MSG"));
			if(!JPlateYdConst.RETN_CD_SUCCESS.equals(jrRtn.getFieldString("RTN_MSG"))) {
				gdRet.setStatus("false");
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}	
	
	/**
	 * [1후판정정야드] 1후판정정 L2제원정보요구 전송 [YDY2L009]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData sendYDY2L009(GridData inDto) throws DAOException {

		String 	szMethodName	= "sendYDY2L009";
		String 	szOperationName	= "JspFaEJB - L2제원정보요구 전송";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		String	szYdMsgGp		= "";
		//String	szYdBayGp		= "";
		//String	szYdEqpGp		= "";
		//String	szYdStkColNo	= "";
		String  szYdStkCol      = "";
		String  szPwd			= "";

		JDTORecord recSndPara 	= JDTORecordFactory.getInstance().create();
		JDTORecord recPara		= JDTORecordFactory.getInstance().create();

		JPlateYdDelegate ydDelegate = new JPlateYdDelegate();
		JPlateYdCommDAO  commDao 	= new JPlateYdCommDAO();

		GridData     gdRes 		= null;
		
		String szYYMM = DateHelper.format(new java.util.Date(System.currentTimeMillis()),"yyMM");
		String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
		
		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			//szYdBayGp		= ydDaoUtils.paraRecChkNull(inRecord, "YD_BAY_GP");
			//szYdEqpGp		= ydDaoUtils.paraRecChkNull(inRecord, "YD_SPAN_GP");
			//szYdStkColNo	= ydDaoUtils.paraRecChkNull(inRecord, "YD_COL_GP");
			szYdStkCol      = ydDaoUtils.paraRecChkNull(inRecord, "COL_ADDR");
			szPwd			= ydDaoUtils.paraRecChkNull(inRecord, "YD_PW1");

			if(szYYMM.equals(szPwd)) {

	            if(szYdStkCol.length() != 6){
	            	rtnMsg = "파라미터 Check중 YD_STR_LOC(야드저장위치) Error : 길이가 6이 아닙니다. " + szYdStkCol;
	    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, rtnMsg, JPlateYdConst.ERROR);
	            } else {				
				
					/*--------------------------------------------------------
					 - YDY2L009 는 L2가 관리하는 갠트리 크레인 영역만 요구 할 수 있다.
					 - L3가 관리하는 영역을 요구하면 Error 처리 해야 한다.
					 - 갠트리 크레인 영역
					 - 50호기 PECB01~PECB04,PE0111
					 - 45호기 PD0162,PE0151,PE0154~PE0156
					 - 21호기 PB0361~PB0366
					 ---------------------------------------------------------*/				
					boolean bFlag = false;
					
					if("PECB01".equals(szYdStkCol) || "PECB02".equals(szYdStkCol) || "PECB03".equals(szYdStkCol) || "PECB04".equals(szYdStkCol) || "PE0111".equals(szYdStkCol)) {
						//50호기
						bFlag = true;
					} else if("PD0162".equals(szYdStkCol) || "PE0151".equals(szYdStkCol) || "PE0154".equals(szYdStkCol) || "PE0155".equals(szYdStkCol) || "PE0156".equals(szYdStkCol)) {
						//45호기
						bFlag = true;
					} else if("PB0361".equals(szYdStkCol) || "PB0362".equals(szYdStkCol) || "PB0363".equals(szYdStkCol) || "PB0364".equals(szYdStkCol) || "PB0365".equals(szYdStkCol) || "PB0366".equals(szYdStkCol) ) {
						//21호기
						bFlag = true;
					}
					
					
					if(!bFlag) {
						rtnMsg = "Y2(1후판정정야드L2) 송신  후판L2제원정보요구   예외발생! 예외메세지: " + szYdStkCol + " 위치는 L2 영역이 아닙니다!";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, rtnMsg, JPlateYdConst.ERROR);
						
					} else {
					
						//요청한 L2관리영역 모든 단정보 Clear 하기
						recPara.setField("MODIFIER"			, inDto.getParam("YD_USER_ID"));
						recPara.setField("YD_STK_COL_GP"	, szYdStkCol);             		
						recPara.setField("YD_STK_BED_NO"	, "");
		
						commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updL2AreaClear", logId, szMethodName, "요청한 L2관리영역 모든 단정보 Clear 하기");
	
						
						recSndPara = JDTORecordFactory.getInstance().create();
						recSndPara.setField("MSG_ID", 			"YDY2L009");			// YDY2L009 : L2제원정보요구
						recSndPara.setField("YD_MSG_GP", 		"I");
						//recSndPara.setField("YD_STR_LOC", 		"P"+szYdBayGp + szYdEqpGp + szYdStkColNo);
						recSndPara.setField("YD_STR_LOC", 		szYdStkCol);
						
						rtnMsg  = ydDelegate.sendMsg(recSndPara);
					}
	            }
				
			} else {
				rtnMsg = "PASSWORD가 틀립니다!!";
			}

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			//	throw new DAOException(rtnMsg);

				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}	
	
	/**
	 * [1후판정정야드] 야드Map관리 열 수정(2)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws DAOException
	 */
	public GridData procUpdColInfo(GridData inDto) throws DAOException {

		String 	szMethodName	= "procUpdColInfo";
		String 	szOperationName	= "JspFaEJB - 저장위치 좌표설정화면 열 수정(2) ";
		String 	szLogMsg 		= "";

		GridData 		gdRes 	= null;
		EJBConnector 	ejbConn	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			String szBedUpdate = inDto.getParam("BED_UPDATE");
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);

			ejbConn.trx("procUpdColInfo",	new Class[] { JDTORecord[].class , String.class }, new Object[] { inRecord , szBedUpdate });
			gdRes = OperateGridData.cloneResponseGridData(inDto);

		} catch(Exception e) {
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 ";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}
	
	/**
	 * [1후판정정야드] 차량작업관리 : 차상위 재료등록처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData updTrStk(GridData gdReq) throws DAOException {
		String methodNm =  "차상위 재료등록처리[JPlateYDYDPJspFaEJB.updTrStk]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			ejbConn.trx("updTrStk", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}		
	
	/**
	 * [1후판정정야드] 대차작업관리 : 대차초기화
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData doTcarInit(GridData gdReq) throws DAOException {
		String methodNm =  "대차초기화[JPlateYDYDPJspFaEJB.doTcarInit]";
		String logId = commUtils.getLogId();
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("doTcarInit", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			gdRet.setMessage(jrRtn.getFieldString("RTN_MSG"));
			if(!JPlateYdConst.RETN_CD_SUCCESS.equals(jrRtn.getFieldString("RTN_MSG"))) {
				gdRet.setStatus("false");
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}		
	
	
	/**
	 * 1후판정정야드 저장위치별 정보 조회화면 : Book-Out 실적 백업
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData sendP2P3BookOut(GridData inDto) throws DAOException {

		String 	szMethodName	= "sendP2P3BookOut";
		String 	szOperationName	= "JspFaEJB - Book-Out 실적 백업";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.22 로그 개선 
        String logId            = ydLogUtils.getLogIdNew("P");                 		// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szOperationName  + ") 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			szLogMsg = szOperationName + " (" + szMethodName + ") ydComUtil.genJDTORecordSet 호출 START >>>>";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

			szLogMsg = szOperationName + " (" + szMethodName + ") ydComUtil.genJDTORecordSet 호출 END >>>>";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			//
			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			
//---------------------------------------------------------------------------------------------
// 2024.11.22 sendP2P3BookOut call argument에 logId 추가  
//			rtnMsg  = (String)ejbConn.trx("sendP2P3BookOut", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			rtnMsg  = (String)ejbConn.trx("sendP2P3BookOut", new Class[] { JDTORecord[].class, String.class }, new Object[] { inRecord, logId });
//---------------------------------------------------------------------------------------------

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szOperationName  + ") 끝 >>>> " + rtnMsg;
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		return gdRes;
	}

	/**
	 * 1후판정정야드 저장위치별 정보 조회화면 : Book-In 실적 백업
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData sendP2P3BookIn(GridData inDto) throws DAOException {

		String 	szMethodName	= "sendP2P3BookIn";
		String 	szOperationName	= "JspFaEJB - Book-In 실적 백업";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.22 로그 개선 
        String logId            = ydLogUtils.getLogIdNew("P");                 		// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szOperationName  + ") 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			szLogMsg = szOperationName + " (" + szMethodName + ") ydComUtil.genJDTORecordSet 호출 START >>>>";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

			szLogMsg = szOperationName + " (" + szMethodName + ") ydComUtil.genJDTORecordSet 호출 END >>>>";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			//
			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			
//---------------------------------------------------------------------------------------------
// 2024.11.22 sendP2P3BookIn call argument에 logId 추가  
//			rtnMsg  = (String)ejbConn.trx("sendP2P3BookIn", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			rtnMsg  = (String)ejbConn.trx("sendP2P3BookIn", new Class[] { JDTORecord[].class, String.class }, new Object[] { inRecord, logId });
//---------------------------------------------------------------------------------------------

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생 >>>> " + rtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			//	throw new DAOException(rtnMsg);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szOperationName  + ") 끝 >>>> " + rtnMsg;
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		return gdRes;
	}
	
	/**
	 * [1후판정정야드] 저장위치LIST 수정 (보수장,가스장,C동 냉각대)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData updYdLocListBs(GridData inDto) throws DAOException {

		String 	szMethodName	= "updYdLocListBs";
		String 	szOperationName	= "JspFaEJB - 저장위치LIST 수정 처리(보수장,가스장,C동 냉각대)";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			szLogMsg = szOperationName + " (" + szMethodName + ") ydComUtil.genJDTORecordSet 호출 START";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		//	운영에서  NullPointerException 발생하여 CmUtil ---->  ydComUtil 로 변경
		//	JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

			szLogMsg = szOperationName + " (" + szMethodName + ") ydComUtil.genJDTORecordSet 호출 END";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("updYdLocListBs", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") 호출오류 발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			//	throw new DAOException(rtnMsg);

				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}

	/**
	 * [1후판정정야드] RT모니터링 : #2 열처리 Book-Out 요구 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procPlateYdBookOutReq3(GridData inDto) throws DAOException {

		String 	szMethodName	= "procPlateYdBookOutReq3";
		String 	szOperationName	= "JspFaEJB - #2 열처리 Book-Out 요구";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		String logId            = ydLogUtils.getLogIdNew("P");
		
		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			inRecord.setField("LOG_ID", logId);                                         
			
			ejbConn	= new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procP8BookInOutReq", inRecord);
			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
			//	throw new DAOException(rtnMsg);
			//	DAOException 발생시 메시지가 출력안되서 일단 Rollback 처리시킴 ..

				szLogMsg = szOperationName + " (" + szMethodName + ") 오류발생으로 Rollback 처리함 ..";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

	            m_ctx.setRollbackOnly();
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>>" + rtnMsg;
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		return gdRes;
	} // end of procPlateYdBookOutReq3()	
	
	
	
}
