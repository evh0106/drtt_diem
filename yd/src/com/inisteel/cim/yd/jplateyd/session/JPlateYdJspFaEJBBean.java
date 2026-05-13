/*
 * @(#) 2후판정정야드 JSP에서 호출되는 Facade Session EJB클래스
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
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;

import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO;
import com.inisteel.cim.yd.jplateyd.delegate.JPlateYdDelegate;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.yd.jsp.common.YDComUtil;

/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Facade Session EJB클래스입니다.
 *
 * @ejb.bean name="JPlateYdJspFaEJB" jndi-name="JPlateYdJspFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class JPlateYdJspFaEJBBean extends BaseSessionBean {


	private static final long serialVersionUID = 1L;

	// Session Name
	private static final String SZ_SESSION_NAME = JPlateYdJspFaEJBBean.class.getName();

	private JPlateYdUtils    	ydUtils   	= new JPlateYdUtils();
	private YDComUtil  			ydComUtil 	= new YDComUtil();
    private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();
    
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * [2후판정정야드] - GridData - 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inGridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getGridData(GridData inGridData) throws DAOException {

		String 	szMethodName 	= "getGridData";
		String	szOperationName	= "JspFaEJB - FLEX모니터링";
		String	szLogMsg		= "";

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try {

			//Grid date 를 JDTORecord data 로 변환
			JDTORecord inRecord = CmUtil.genJDTORecord(inGridData);

	    	//SE EJB 호출
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet)ejbConn.trx("JPlateYdJspSeEJB", "getGridData", inRecord);

			//UI로 반환 할 Grid data 를 생성
			gdRes = CmUtil.genGridData(inGridData, recordSet);

			gdRes.setStatus("true");
			gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);

			return gdRes;

		} catch(Exception e) {
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

	}	// end of getGridData

	/**
	 * [2후판정정야드] FLEX모니터링 : 모니터링 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public List getFlexData(HashMap param) throws DAOException {

		//LOG
		String 	szMethodName	= "getFlexData";
		String	szOperationName	= "JspFaEJB - FLEX모니터링";
		String 	szLogMsg 		= "";

		List     	 rsltList 	= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			ejbConn  = new EJBConnector("default", "JPlateYdJspSeEJB", this);
			rsltList = (List)ejbConn.trx("getFlexData", new Class[] { HashMap.class }, new Object[] { param });

		} catch(Exception e) {

			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝 >>>> " + rsltList.size();
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return rsltList;
	}

	/**
	 * [2후판정정야드] 야드Map관리 열조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws DAOException
	 */
	public GridData getJPlateYdStkPosSet(GridData inDto) throws DAOException {

		String 	szMethodName 		= "getJPlateYdStkPosSet";
		String 	szOperationName 	= "JspFaEJB - 저장위치 좌표설정화면 조회";
		String 	szLogMsg 			= "";

		GridData 		gdRes 		= null;
		EJBConnector 	ejbConn 	= null;
		JDTORecordSet 	recordSet 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet)ejbConn.trx("JPlateYdJspSeEJB", "getJPlateYdStkPosSet", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);

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
	 * [2후판정정야드] 야드Map관리 베드조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws DAOException
	 */
	public GridData getJPlateYdStkPosSetBed(GridData inDto) throws DAOException {

		String 	szMethodName		= "getJPlateYdStkPosSetBed";
		String 	szOperationName 	= "JspFaEJB - 저장위치 좌표설정화면 베드조회";
		String 	szLogMsg 			= "";

		GridData 		gdRes 		= null;
		EJBConnector 	ejbConn 	= null;
		JDTORecordSet 	recordSet 	= null;
		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet)ejbConn.trx("JPlateYdJspSeEJB", "getJPlateYdStkPosSetBed", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);

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
	 * [2후판정정야드] 야드Map관리 열 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws DAOException
	 */
	public GridData insJPlateYdStkPosSet(GridData inDto) throws DAOException {

		String 	szMethodName	= "insJPlateYdStkPosSet";
		String 	szOperationName	= "JspFaEJB - 저장위치 좌표설정화면 열 등록";
		String 	szLogMsg 		= "";

		GridData 		gdRes 	= null;
		EJBConnector 	ejbConn = null;

		try {
			szLogMsg = szOperationName + " (" + szMethodName + ") 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
			ejbConn.trx("insJPlateYdStkPosSet", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
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
	 * [2후판정정야드] 야드Map관리 베드등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws DAOException
	 */
	public GridData insJPlateYdStkPosSetBed(GridData inDto) throws DAOException {

		String 	szMethodName	= "insJPlateYdStkPosSetBed";
		String 	szOperationName	= "JspFaEJB - 저장위치 좌표설정화면 BED  등록";
		String 	szLogMsg 		= "";

		GridData 		gdRes 	= null;
		EJBConnector 	ejbConn	= null;

		try {
			szLogMsg = szOperationName + " (" + szMethodName + ") 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
			ejbConn.trx("insJPlateYdStkPosSetBed", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
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
	 * [2후판정정야드] 야드Map관리 열 수정
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
			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);

			ejbConn.trx("updJPlateYdStkPosSet",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
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
	 * [2후판정정야드] 야드Map관리 베드수정
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
			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);

			ejbConn.trx("updJPlateYdStkPosSetBed",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
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
	 * [2후판정정야드] 저장위치 야드Map관리 열 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData delJPlateYdStkPosSet(GridData inDto) throws DAOException {

		String 	szMethodName	= "delJPlateYdStkPosSet";
		String 	szOperationName	= "JspFaEJB - 저장위치 좌표설정화면 열 삭제";
		String 	szLogMsg 		= "";

		GridData 		gdRes 	= null;
		EJBConnector 	ejbConn = null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
			ejbConn.trx("insJPlateYdStkPosSet",new Class[] { JDTORecord[].class }, new Object[] { inRecord });
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
	} // end of delJPlateYdStkPosSet

	/**
	 * [2후판정정야드] 크레인작업관리화면 : 설비 목록 조회 [크레인호기조회]
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData getEqpList(GridData inDto) throws DAOException {

		String 	szMethodName 	= "getEqpList";
		String	szOperationName	= "JspFaEJB - 설비 목록 조회";
		String 	szLogMsg 		= null;

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet)ejbConn.trx("JPlateYdJspSeEJB", "getEqpList", inRecord);

			gdRes = CmUtil.genGridData(inDto , recordSet);

		} catch(Exception e) {
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}

	/**
	 * [2후판정정야드] 크레인작업관리화면 : 스케줄 목록 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData getSchRuleList(GridData inDto) throws DAOException {

		String 	szMethodName 	= "getSchRuleList";
		String	szOperationName	= "JspFaEJB - 스케줄 목록 조회";
		String 	szLogMsg 		= null;

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet)ejbConn.trx("JPlateYdJspSeEJB", "getSchRuleList", inRecord);

			gdRes = CmUtil.genGridData(inDto, recordSet);

		} catch(Exception e) {
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}

	/**
	 * [2후판정정야드] 크레인작업관리화면 : 크레인 작업재료 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData getCrnWrkMtlRef(GridData inDto) throws DAOException {

		String 	szMethodName 	= "getCrnWrkMtlRef";
		String	szOperationName	= "JspFaEJB - 크레인 작업재료 조회";
		String 	szLogMsg 		= null;

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet)ejbConn.trx("JPlateYdJspSeEJB", "getCrnWrkMtlRef", inRecord);

			gdRes = CmUtil.genGridData(inDto, recordSet);

		} catch(Exception e) {
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}

	/**
	 * [2후판정정야드] 크레인작업관리화면 : 크레인 작업관리 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData getYdCrnWorkMgt(GridData inDto) throws DAOException {

		String 	szMethodName 	= "getYdCrnWorkMgt";
		String	szOperationName	= "JspFaEJB - 크레인 작업관리 조회";
		String 	szLogMsg 		= null;

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet)ejbConn.trx("JPlateYdJspSeEJB", "getYdCrnWorkMgt", inRecord);

			gdRes = CmUtil.genGridData(inDto, recordSet);

		} catch(Exception e) {
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}

	/**
	 * [2후판정정야드] 크레인작업관리화면 : 작업취소 - 작업예약도 취소
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

				ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
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

				ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
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
					String rtnMsg	= (String)ejbConn.trx("JPlateYdJspSeEJB", "delStockLocOnRt", recPara);

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
	 * [2후판정정야드] 크레인작업관리화면 : 스케줄 취소 - 작업예약은 남겨놈
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

				ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
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
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

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
	 * [2후판정정야드] 크레인작업관리화면 : 명령선택
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

			// 야드L2 전문수신 EJB기동 : 명령선택처리 (Y7YDL012)
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("JMS_TC_CD", 			"Y7YDL012");
			recPara.setField("YD_EQP_ID", 			sYdEqpId);				// 야드설비ID
		//	recPara.setField("YD_CMD_PKUP_GP", 		"S");					// 야드명령선택구분 - S:명령선택, C:취소
			recPara.setField("YD_CMD_PKUP_GP", 		sydCmdPkupGp);			// 야드명령선택구분 - S:명령선택, C:취소
			recPara.setField("YD_CRN_SCH_ID", 		sYdCrnSchId);			// 야드크레인스케쥴ID
			recPara.setField("MODIFIER", 			sModifier);

			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY7CrnOrderSel", recPara);
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
	 * [2후판정정야드] 크레인작업관리화면 : 크레인 변경
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
			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
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
	 * [2후판정정야드] 크레인작업관리화면 : 우선순위 변경
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
			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
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
	 * [2후판정정야드] 크레인작업관리화면 : 파일링 처리
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

			// 야드L2 전문수신 EJB기동 : 파일링실적 [Y7YDL013]
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

			recPara.setField("JMS_TC_CD", 			"Y7YDL013");
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

			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY7PilingRslt", recPara);

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
	 * 2후판정정 스케줄 기동관리 (조회)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getJPlateYdSchStartMgt(GridData inDto) throws DAOException {

		String	szMethodName		= "getJPlateYdSchStartMgt";
		String	szOperationName		= "JspFaEJB - 스케줄 기동관리[조회]";
		String 	szLogMsg        	= "";

		GridData 		gdRes 		= null;
		EJBConnector	ejbConn 	= null;
		JDTORecordSet 	recordSet	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet)ejbConn.trx("JPlateYdJspSeEJB", "getJPlateYdSchStartMgt", inRecord);

			gdRes = CmUtil.genGridData(inDto , recordSet);

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

	} //end of getJPlateYdSchStartMgt

	/**
	 * 2후판정정 스케줄 기동관리 (수정)
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

			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);

			ejbConn.trx("updJPlateYdSchStartMgt",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });

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
	 * [2후판정정야드] 크레인상태설정팝업 : 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData getYdCrnStsSetById(GridData inDto) throws DAOException {

		String 	szMethodName 	= "getYdCrnStsSetById";
		String	szOperationName = "JspFaEJB - 크레인 상태설정 조회";
		String 	szLogMsg 		= null;

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet)ejbConn.trx("JPlateYdJspSeEJB", "getYdCrnStsSetById", inRecord);

			gdRes = CmUtil.genGridData(inDto, recordSet);

		} catch(Exception e) {
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}	// end of getYdCrnStsSetById

	/**
	 * [2후판정정야드] 크레인상태설정팝업 : BED정보 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData getYdStkbed(GridData inDto) throws DAOException {

		String 	szMethodName 	= "getYdStkbed";
		String 	szOperationName	= "JspFaEJB - 크레인상태설정팝업[BED정보 조회]";
		String 	szLogMsg 		= null;

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet)ejbConn.trx("JPlateYdJspSeEJB", "getYdStkbed", inRecord);

			gdRes = CmUtil.genGridData(inDto, recordSet);
			gdRes.setMessage(JPlateYdConst.RETN_CD_SUCCESS);

		} catch(Exception e) {
			gdRes.setMessage(JPlateYdConst.RETN_CD_FAILURE);
			szLogMsg = szOperationName + " (" + szMethodName + ") Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = szOperationName + " (" + szMethodName + ") 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}	// end of getYdStkbed

	/**
	 * [2후판정정야드] 크레인상태설정팝업 : 설비 고장/정상 설정
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

			// 야드L2 전문수신 EJB기동 : Y7 설비고장복구실적 [Y7YDL004]
			inRecord.setField("JMS_TC_CD"			, "Y7YDL004");
			inRecord.setField("YD_EQP_STAT"			, sCrnStat);			// 설비상태
			inRecord.setField("YD_EQP_PAUSE_CODE"	, "0000");				// 휴지코드
			inRecord.setField("YD_EQP_TRBL_RCVR_DT"	, sYmdHms);				// 야드설비고장복구일시

			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY7EqpTrblRcvrWr", inRecord);
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
	 * [2후판정정야드] 크레인상태설정팝업 : 설비 On-Line/Off-Line 설정
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

			// 야드L2 전문수신 EJB기동 : Y7 설비운전모드전환 [Y7YDL003]
			inRecord.setField("JMS_TC_CD"			, "Y7YDL003");
			inRecord.setField("YD_EQP_WRK_MODE"		, sCrnMode);			// 운전모드

			ejbConn	= new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY7EqpDrvModeChg", inRecord);
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
	 * [2후판정정야드] 크레인상태설정팝업 : 권상처리
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

			// 야드L2 전문수신 EJB기동 : Y7 ON-LINE권상실적 [Y7YDL008]
			inRecord.setField("JMS_TC_CD", "Y7YDL008");

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

			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY7CrnUpWr", inRecord);
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
	 * [2후판정정야드] 크레인상태설정팝업 : 권하처리
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

			// 야드L2 전문수신 EJB기동 : Y7 ON-LINE권하실적 [Y7YDL009]
			inRecord.setField("JMS_TC_CD", "Y7YDL009");

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
			// STL_NO1			재료번호1
			// YD_DN_WR_LOC1	야드권하실적위치1
			// STL_NO2			재료번호2
			// YD_DN_WR_LOC2	야드권하실적위치2
			// STL_NO3			재료번호3
			// YD_DN_WR_LOC3	야드권하실적위치3
			// STL_NO4			재료번호4
			// YD_DN_WR_LOC4	야드권하실적위치4
			// STL_NO5			재료번호5
			// YD_DN_WR_LOC5	야드권하실적위치5

			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY7CrnDownWr", inRecord);
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
	 * [2후판정정야드] 크레인상태설정팝업 : 강제권상
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

			// 야드L2 전문수신 EJB기동 : Y7 강제권상요구 [Y7YDL010]
			inRecord.setField("JMS_TC_CD", "Y7YDL010");

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

			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY7OffCrnUpWr", inRecord);

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

			// 야드L2 전문수신 EJB기동 : Y7 강제권하요구 [Y7YDL011]
			inRecord.setField("JMS_TC_CD", "Y7YDL011");

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

			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY7OffCrnDnWr", inRecord);

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
	 * [2후판정정야드] 크레인상태설정팝업 : 권하위치 변경
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

			// YD_CRN_SCH_ID	야드크레인스케쥴ID
			// YD_DN_WO_LOC		야드권하지시위치
			// YD_DN_WO_LAYER	야드권하지시단

			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdCrnReSchSeEJB", "updCrnDnPrsFix", inRecord);

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = szOperationName + " (" + szMethodName + ") JPlateYdCrnReSchSeEJB.updCrnDnPrsFix 호출오류발생 >>>> " + rtnMsg;
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
	 *  재료 상세정보 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getYdStrlocIdInfojl(GridData inDto) throws DAOException {

		String 	szMethodName 	= "getYdStrlocIdInfojl";
		String 	szOperationName	= "JspFaEJB - 재료 상세정보 조회";
		String 	szLogMsg 		= "";

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try {
			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("JPlateYdJspSeEJB", "getYdStrlocIdInfojl", inRecord);
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
	} //end of getYdStrlocIdInfojl

	/**
	 * 재료이력정보 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData getYdWrkHistByStlNo(GridData inDto) throws DAOException {

		String 	szMethodName	= "getYdWrkHistByStlNo";
		String 	szOperationName	= "JspFaEJB - 재료상세정보_재료이력 조회";
		String 	szLogMsg 		= "";

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet) ejbConn.trx("JPlateYdJspSeEJB", "getYdWrkHistByStlNo", inRecord);

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
	 * 작업예약관리화면 : 작업예약 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData getYdWrkBookListjm(GridData inDto) throws DAOException {

		String 	szMethodName	= "getYdWrkBookListjm";
		String 	szOperationName	= "JspFaEJB - 작업예약관리_작업예약 조회";
		String 	szLogMsg 		= "";

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet) ejbConn.trx("JPlateYdJspSeEJB", "getYdWrkBookListjm", inRecord);

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
	 * 작업예약관리화면 : 작업재료 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData getYdWrkBookListjmDtl(GridData inDto) throws DAOException {

		String 	szMethodName	= "getYdWrkBookListjmDtl";
		String 	szOperationName	= "JspFaEJB - 작업예약관리_작업재료 조회";
		String 	szLogMsg 		= "";

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet) ejbConn.trx("JPlateYdJspSeEJB", "getYdWrkBookListjmDtl", inRecord);

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
	 * 작업예약관리화면 : 작업예약 삭제
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

				ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
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
	 * 작업예약관리화면 : 스케쥴기동
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

				szLogMsg = szOperationName + " (" + szMethodName + ") 스케쥴기동 시작!! >>>> 스케쥴ID :: " + szYD_WBOOK_ID;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				szLogMsg = szOperationName + " (" + szMethodName + ") 스케줄기동 START :: " + szYD_WBOOK_ID;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

    	        ejbConn = new EJBConnector("default", "JPlateYdCrnSchSeEJB", this);
    	        szRtnMsg = (String)ejbConn.trx("procCrnSchMain", new Class[] { JDTORecord.class }, new Object[] { recSchPara });

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
	 * 작업예약관리화면 : TO위치변경
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
	 * 작업예약관리화면 : 스케쥴점검
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
	 * RT모니터링_BOOK-OUT대상재료 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData getYdStockBookOut(GridData inDto) throws DAOException {

		String 	szMethodName	= "getYdStockBookOut";
		String 	szOperationName	= "JspFaEJB - RT모니터링_BOOK-OUT대상재료 조회";
		String 	szLogMsg 		= "";

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet) ejbConn.trx("JPlateYdJspSeEJB", "getYdStockBookOut", inRecord);

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
	 * [2후판정정야드] RT모니터링 : Book-Out 요구
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
			rtnMsg  = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procS1BookInOutReq", inRecord);
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
			rtnMsg  = (String)ejbConn.trx("JPlateYdJspSeEJB", "updYdLocInfo", inRecord);

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
			rtnMsg  = (String)ejbConn.trx("JPlateYdJspSeEJB", "delYdLocInfo", inRecord);

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
	 * [2후판정정야드] 저장위치LIST 수정
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

			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
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
	 * [2후판정정야드] 이적대상재 등록
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

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);

			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("insMvWBookId", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

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
	 * [2후판정정야드] 대차작업취소처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData tCarCancelWBook(GridData inDto) throws DAOException {

		String 	szMethodName	= "tCarCancelWBook";
		String 	szOperationName	= "JspFaEJB - 대차작업취소 처리";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		//EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			//JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);

			//ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
			//rtnMsg  = (String)ejbConn.trx("tCarCancelWBook", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

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
	 * [2후판정정야드] 대차작업초기화 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData tCarClearSch(GridData inDto) throws DAOException {

		String 	szMethodName	= "tCarClearSch";
		String 	szOperationName	= "JspFaEJB - 대차작업초기화 처리";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);

			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("delTcarSch", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

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
	 * [2후판정정야드] 대차출발 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData tCarMoveStart(GridData inDto) throws DAOException {

		String 	szMethodName	= "tCarMoveStart";
		String 	szOperationName	= "JspFaEJB - 대차출발 처리";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);

			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("tCarMoveStart", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

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
	 * [2후판정정야드] 대차도착처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData tCarMoveStop(GridData inDto) throws DAOException {

		String 	szMethodName	= "tCarMoveStop";
		String 	szOperationName	= "JspFaEJB - 대차도착처리";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);

			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("tCarMoveStop", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

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

			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
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

			rtnMsg = (String)ejbConn.trx("JPlateYdJspSeEJB", "insCncOutWBook", inRecord);

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

			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
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
	 * [2후판정정야드] 보수장모니터링 : 보급 요구
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

			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
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
	 * [2후판정정야드] 보수장모니터링 : 추출 요구
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

			rtnMsg = (String)ejbConn.trx("JPlateYdJspSeEJB", "insBsOutWBook", inRecord);

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
	 * [2후판정정야드] 냉각대모니터링 : 이적 요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procPlateYdCbMoveReq(GridData inDto) throws DAOException {

		String 	szMethodName	= "procPlateYdCbMoveReq";
		String 	szOperationName	= "JspFaEJB - 극후물 냉각대 이적요구";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);

			rtnMsg  = (String)ejbConn.trx("JPlateYdJspSeEJB", "insCbMoveReq", inRecord);

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
	 * [2후판정정야드] 보수장모니터링 : 가변베드 활성화 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procBsBedAct(GridData inDto) throws DAOException {

		String 	szMethodName	= "procBsBedAct";
		String 	szOperationName	= "JspFaEJB - 보수장 가변베드 활성화 처리";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);

			rtnMsg  = (String)ejbConn.trx("JPlateYdJspSeEJB", "updBsBedAct", inRecord);

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
	 * [2후판정정야드] 보수장모니터링 : 보수장 이적 처리 [저장위치 수정]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData procBsMvReq(GridData inDto) throws DAOException {

		String 	szMethodName	= "procBsMvReq";
		String 	szOperationName	= "JspFaEJB - 보수장 이적 처리 [저장위치 수정]";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();

	   	String	szYdAimYdGp		= "";
	   	String	szAimBayGp		= "";
	   	String	szAimSpanGp		= "";
	   	String	szAimColGp		= "";
	   	String	szStlList		= "";
	   	String	szModifier		= "";
	   	StringBuffer	sbStlNo	= new StringBuffer();

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);

			if (inRecord.length > 0) {
				szYdAimYdGp = ydDaoUtils.paraRecChkNull(inRecord[0], "YD_AIM_YD_GP");
				szAimBayGp 	= ydDaoUtils.paraRecChkNull(inRecord[0], "YD_AIM_BAY_GP");
				szAimSpanGp	= ydDaoUtils.paraRecChkNull(inRecord[0], "YD_AIM_SPAN_GP");
				szAimColGp	= ydDaoUtils.paraRecChkNull(inRecord[0], "YD_AIM_COL_GP");
				szModifier	= ydDaoUtils.paraRecModifier(inRecord[0]);
			}

			for(int ii=0; ii<inRecord.length; ii++) {
				szStlList = ydDaoUtils.paraRecChkNull(inRecord[ii],	"STL_LIST");
				if (ii!=0) {
					sbStlNo.append(";");
				}
				sbStlNo.append(szStlList);
			}

			szLogMsg = szOperationName + " (" + szMethodName + ") 보수장 이적 처리 .. 재료 LIST >>>> " + sbStlNo.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_AIM_YD_GP", 	szYdAimYdGp);
			recPara.setField("YD_AIM_BAY_GP", 	szAimBayGp);
			recPara.setField("YD_AIM_SPAN_GP", 	szAimSpanGp);
			recPara.setField("YD_AIM_COL_GP", 	szAimColGp);
			recPara.setField("MODIFIER", 		szModifier);
			recPara.setField("STL_LIST", 		sbStlNo.toString());

			ejbConn = new EJBConnector("default", this);
			rtnMsg  = (String)ejbConn.trx("JPlateYdJspSeEJB", "procBsLocChg", recPara);

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
	 * 2후판정정야드 저장위치별 정보 조회화면 : 조업L3 저장위치 정보 재전송
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

			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
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
	 * [2후판정정야드] 야드긴급재 등록/취소
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
			rtnMsg  = (String)ejbConn.trx("JPlateYdJspSeEJB", "updYdUgntGp", inRecord);

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
	 * [2후판정정야드] 2후판정정 작업메시지 전송 [YDY7L007 : 크레인작업메시지]
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
			recSndPara.setField("MSG_ID", 			"YDY7L007");			// YDY7L007 : 크레인작업메세지
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
	 * [2후판정정야드] 적치열검수 SET
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
	 * [2후판정정야드] 시편채취 등록
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
	 * [2후판정정야드] 차량작업관리 - 상차위치변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData updCarLdLoc(GridData inDto) throws DAOException {

		String 	szMethodName	= "updCarLdLoc";
		String 	szOperationName	= "JspFaEJB - 상차위치변경";
		String 	szLogMsg 		= "";
		String 	rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;

		try {

			szLogMsg = szOperationName + " (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			// JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			// 수정부는 ydComUtil 로 바꾸어야한다.왜냐하면 콤보박스의 정보가 없을경우
			// 공통에서 Null Point Exception 발생(개발계 발생, 운영계는 발생X)

			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);

			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);

			rtnMsg  = (String)ejbConn.trx("updCarLdLoc", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

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
	 * 차량작업관리 화면 상차LOT편성 후판사용
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData insCarLdLot(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="insCarLdLot";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		String szYD_CAR_LOT_TYPE = "";
		JDTORecord [] inRecord =  null;
		try{

			szMsg = "[JSP Facade] 차량작업관리 화면 상차LOT편성 시작  ==>";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName , szMsg , YdConstant.DEBUG);
			
			szYD_CAR_LOT_TYPE = inDto.getParam("YD_CAR_LOT_TYPE");
			if( szYD_CAR_LOT_TYPE.equals("M") ) {
				inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
				szMsg = "[JSP Facade] 차량작업관리 화면 상차LOT편성 - 작업자 지정";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName , szMsg , YdConstant.DEBUG);
			}else{
				inRecord = new JDTORecord[1];
				inRecord[0] = ydComUtil.genParamToJDTORecord(inDto);
				szMsg = "[JSP Facade] 차량작업관리 화면 상차LOT편성 시작   - 기준 적용";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName , szMsg , YdConstant.DEBUG);
			}
			
			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("insCarLdLot", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 상차LOT편성 끝";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage(szRtnMsg);
		}catch(Exception e){
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			
		}
		return gdRes;
	}
	
	
	/**
	 * 차량작업관리 화면 상차LOT편성 취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData cancelCarLdLot(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="cancelCarLdLot";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 상차LOT편성  취소 시작  ==>";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName , szMsg , YdConstant.DEBUG);
			
			
			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("cancelCarLdLot", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 상차LOT편성  취소 끝";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage(szRtnMsg);
		}catch(Exception e){
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of cancelCarLdLot
	
	
	/**
	 * 차량작업관리화면 상차완료처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData complCarLdLot(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="complCarLdLot";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 상차완료처리 시작  ==>";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName , szMsg , YdConstant.DEBUG);
			
			
			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("complCarLdLot", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 상차완료처리 끝";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage(szRtnMsg);
		}catch(Exception e){
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of complCarLdLot
	
	/**
	 * [2후판정정야드] 저장위치LIST 수정 (보수장)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData updYdLocListBs(GridData inDto) throws DAOException {

		String 	szMethodName	= "updYdLocListBs";
		String 	szOperationName	= "JspFaEJB - 저장위치LIST 수정 처리(보수장)";
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

			ejbConn = new EJBConnector("default", "JPlateYdJspSeEJB", this);
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
}
