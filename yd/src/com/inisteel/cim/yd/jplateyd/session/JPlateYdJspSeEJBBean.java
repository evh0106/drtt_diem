/*
 * @(#) 2후판정정야드 JSP에서 호출 Session EJB클래스
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/14
 *
 * @description		2후판정정야드 JSP에서 호출 Session EJB클래스
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/14   김현우      김현우       최초작성
 */

package com.inisteel.cim.yd.jplateyd.session;

//UTIL IMPORT
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;

import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCarSpecDao.YdCarSpecDao;
import com.inisteel.cim.yd.common.dao.ydPrepMtlDao.YdPrepMtlDao;
import com.inisteel.cim.yd.common.dao.ydPrepSchDao.YdPrepSchDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCarSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarFtmvMtlDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO;

import com.inisteel.cim.yd.jplateyd.delegate.JPlateYdDelegate;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdCommonUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdCrnSchUtil;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdStkLocVO;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdToLocUtil;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.yd.jsp.common.CmnUtil;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="JPlateYdJspSeEJB" jndi-name="JPlateYdJspSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class JPlateYdJspSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;

	private static final String SZ_SESSION_NAME = JPlateYdJspSeEJBBean.class.getName();
	
	private String szSessionName = getClass().getName();
	
	private JPlateYdUtils    	ydUtils 	= new JPlateYdUtils();
    private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();
	private JPlateYdDelegate	ydDelegate 	= new JPlateYdDelegate();
	private YDDataUtil  yddatautil = new YDDataUtil();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * [2후판정정야드] GridData - 단순 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getGridData(JDTORecord inDto) throws DAOException {
		int intRtnVal 			= 0;
		String szMsg 			= "";
		String szMethodName 	= "getGridData";
		String szOperationName 	= "GridData 단순 조회";

		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();

		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			String sQueryId = ydDaoUtils.paraRecChkNull(inDto, "QUERY_ID").trim();

			/*
			 * com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDao.getCrnWorkRsltjl		-- 작업실적일품조회 		[jPlateYdCrnWorkRsltjl.jsp]
			 * com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDao.getYdStkLocInfoListjl_PIDEV	-- 저장위치별 정보 조회		[jPlateYdStkLocInfoListjl.jsp]
			 */

			intRtnVal = ydCommDao.select(inDto, outRecSet, sQueryId);

			if (intRtnVal <= 0) {
				szMsg = "[JSP Session : "+szOperationName+"] 조회 ERROR";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			} // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return outRecSet;
	}	// end of getGridData

	/**
	 * [2후판정정야드] FlexData - 단순 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public List getFlexData(HashMap param) throws DAOException {
		int intRtnVal 				= 0;
		String szMsg 				= "";
		String szMethodName 		= "getFlexData";
		String szOperationName 		= "Flex 단순 조회";
		List	rtnList				= null;
		JDTORecord      recPara  	= null;
		JDTORecordSet 	outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JPlateYdCommDAO ydCommDao 	= new JPlateYdCommDAO();

		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//Hash Map Data => Grid Data
			recPara = JDTORecordFactory.getInstance().create();
			recPara = CmnUtil.hashMapTojdtoRecord(param);

			String sQueryId = ydDaoUtils.paraRecChkNull(recPara, "QUERY_ID").trim();

			intRtnVal = ydCommDao.select(recPara, outRecSet, sQueryId);

			if (intRtnVal <= 0) {
				szMsg = "[JSP Session : "+szOperationName+"] 조회 ERROR";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			} // end of if

			rtnList = CmnUtil.listJdtoRecordTohashMap(outRecSet.toList());

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return rtnList;
	}	// end of getFlexData

	/**
	 * [2후판정정야드] 야드Map관리 열 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getJPlateYdStkPosSet(JDTORecord inDto) throws DAOException {
		int 	intRtnVal 		= 0;
		String 	szMsg 			= "";
		String 	szMethodName 	= "getJPlateYdStkPosSet";
		String 	szOperationName = "좌표설정화면 조회";

		JDTORecord recPara      = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JPlateYdStkColDAO ydStkcolDao = new JPlateYdStkColDAO();

		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if ("ALL".equals(ydDaoUtils.paraRecChkNull(inDto, "YD_STK_COL_NO").trim())) {
				recPara.setField("YD_GP", 	      inDto.getField("YD_GP"));
				recPara.setField("YD_BAY_GP",     inDto.getField("YD_BAY_GP"));
				recPara.setField("YD_EQP_GP",     inDto.getField("YD_EQP_GP"));

				intRtnVal = ydStkcolDao.getYdStkcolEqp(recPara, outRecSet);

			} else {
				recPara.setField("YD_GP", 		  inDto.getField("YD_GP"));
				recPara.setField("YD_BAY_GP", 	  inDto.getField("YD_BAY_GP"));
				recPara.setField("YD_EQP_GP", 	  inDto.getField("YD_EQP_GP"));
				recPara.setField("YD_STK_COL_NO", inDto.getField("YD_STK_COL_NO"));

				intRtnVal = ydStkcolDao.getYdStkcolCol(recPara, outRecSet);

			}

			if (intRtnVal <= 0) {
				szMsg = "[JSP Session : "+szOperationName+"] 조회 ERROR";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			} // end of if


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return outRecSet;
	}	// end of getJPlateYdStkPosSet

	/**
	 * [2후판정정야드] 야드Map관리 베드 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getJPlateYdStkPosSetBed(JDTORecord inDto) throws DAOException {

		//for Log
		String 	szMsg 			= "";
		String 	szMethodName	= "getJPlateYdStkPosSetBed";
		int 	intRtnVal 		= 0;
		String 	szOperationName = "저장위치 좌표설정화면 베드 조회";

		String 	szEditStkPos 	= null;
		String 	szEditStkCol 	= null;
		String 	szEditStkBed 	= null;

		JDTORecord 		recPara 	= JDTORecordFactory.getInstance().create();
		JDTORecord 		recEdit 	= JDTORecordFactory.getInstance().create();
		JDTORecordSet 	retRecSet 	= JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet 	outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("retTmp");

		JPlateYdStkBedDAO ydStkbedDao = new JPlateYdStkBedDAO();

		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//적치열구분을 Parameter 로 Set
			recPara.setField("YD_STK_COL_GP", 	ydDaoUtils.paraRecChkNull(inDto, "YD_STK_COL_GP"));

			intRtnVal = ydStkbedDao.getYdStkbedYdStkColGpBed(recPara, outRecSet);

			if (intRtnVal < 0) {
				szMsg = "[JSP Session : "+szOperationName+"] 조회 ERROR";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return outRecSet;
			} else if  (intRtnVal == 0) {
				szMsg = "[JSP Session : "+szOperationName+"] 데이터 없음 ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return outRecSet;
			}

			//JDTORecordSet 에 첫 위치로 이동시킨다.
			outRecSet.first();

			do {

				//스케줄 코드를  설비구분을 분리하여 YD_EQP_GP 컬럼에 넣어준다.
				recEdit      = outRecSet.getRecord();
				szEditStkCol = ydDaoUtils.setDataDefault(recEdit.getField("YD_STK_COL_GP"), "");
				szEditStkBed = ydDaoUtils.setDataDefault(recEdit.getField("YD_STK_BED_NO"), "");
				szEditStkPos = szEditStkCol + "-"+ szEditStkBed;

				recEdit.setField("YD_STK_POS", szEditStkPos);

				retRecSet.addRecord(recEdit);

			} while(outRecSet.next());


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return retRecSet;
	}	// end of getJPlateYdStkPosSetBed

	/**
	 * [2후판정정야드] 야드Map관리 열 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void insJPlateYdStkPosSet(JDTORecord [] inDto) throws DAOException {

		int 	intRtnVal       = 0;
		String 	szMsg        	= "";
		String 	szMethodName 	= "insJPlateYdStkPosSet";
		String 	szOperationName = "좌표설정화면 열 등록";
		String	szModifier		= "";

		String szYdStkColGp 	= null;
		JDTORecord recPara  	= JDTORecordFactory.getInstance().create();
		JPlateYdStkColDAO ydStkcolDao = new JPlateYdStkColDAO();

		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 " + Integer.toString(inDto.length);
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//메뉴 페이지 목록을 수정한다.
			for (int ii=0; ii<inDto.length; ii++) {

				//등록  항목 SETTING
				szModifier = ydDaoUtils.paraRecModifier(inDto[ii]);
				recPara.setField("REGISTER", 				szModifier);
				recPara.setField("MODIFIER", 				szModifier);

				//적치열번호 필수
				recPara.setField("YD_STK_COL_NO", 			ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_NO"));

				//야드구분
				recPara.setField("YD_GP", 					ydDaoUtils.paraRecChkNull(inDto[ii], "YD_GP"));

				//야드동구분
				recPara.setField("YD_BAY_GP", 				ydDaoUtils.paraRecChkNull(inDto[ii], "YD_BAY_GP"));

				//야드설비구분
				recPara.setField("YD_EQP_GP", 				ydDaoUtils.paraRecChkNull(inDto[ii], "YD_EQP_GP"));

				//활성상태
				recPara.setField("YD_STK_COL_ACT_STAT", 	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_ACT_STAT"));

				//기준 X축
				recPara.setField("YD_STK_COL_RULE_XAXIS", 	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_RULE_XAXIS"));

				//기준Y축
				recPara.setField("YD_STK_COL_RULE_YAXIS", 	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_RULE_YAXIS"));

				//폭
				recPara.setField("YD_STK_COL_W", 			ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_W"));

				//길이
				recPara.setField("YD_STK_COL_L", 			ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_L"));

				//적치열 구분 * 필수
				szYdStkColGp = inDto[ii].getFieldString("YD_GP")
							+  inDto[ii].getFieldString("YD_BAY_GP")
							+  inDto[ii].getFieldString("YD_EQP_GP")
							+  inDto[ii].getFieldString("YD_STK_COL_NO");
				recPara.setField("YD_STK_COL_GP", szYdStkColGp.trim());

				intRtnVal = ydStkcolDao.insYdStkcol(recPara);

				if (intRtnVal < 0) {
					szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				} else if  (intRtnVal == 0) {
					szMsg = "[JSP Session : "+szOperationName+"] 데이터 없음 ";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				} // end of if

			}

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);


		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}// end of insJPlateYdStkPosSet

	/**
	 * [2후판정정야드] 야드Map관리 베드 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void insJPlateYdStkPosSetBed(JDTORecord [] inDto) throws DAOException {

		int 	intRtnVal 		= 0;
		String 	szMsg 			= "";
		String 	szMethodName	= "insJPlateYdStkPosSetBed";
		String 	szOperationName = "저장위치 좌표설정화면 BED 등록";
		String	szModifier		= "";

		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
		JPlateYdStkBedDAO ydStkbedDao = new JPlateYdStkBedDAO();
		try {
			//저장위치 좌표설정화면 열 등록.

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			for (int ii=0; ii<inDto.length; ii++) {

				//등록  항목 SETTING
				szModifier = ydDaoUtils.paraRecModifier(inDto[ii]);
				recPara.setField("REGISTER", 			szModifier);
				recPara.setField("MODIFIER", 			szModifier);

				//적치열구분 필수
				recPara.setField("YD_STK_COL_GP", 		ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GP").trim());

				//적치열번호 필수
				recPara.setField("YD_STK_BED_NO", 		ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_NO"));

				//야드저장집합코드 NOT NULL
				recPara.setField("YD_STR_GTR_CD",		"TESTYD");

				recPara.setField("YD_STK_BED_TP",	     ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_TP"));
				recPara.setField("YD_STK_BED_L_GP",      ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_L_GP"));
				recPara.setField("YD_STK_BED_W_GP",      ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_W_GP"));
				recPara.setField("YD_STK_BED_DIR_GP",    ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_DIR_GP"));
				recPara.setField("YD_STK_BED_ACT_STAT",  ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_ACT_STAT"));
				recPara.setField("YD_STK_BED_WHIO_STAT", ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_WHIO_STAT"));
				recPara.setField("YD_STK_BED_XAXIS",     ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_XAXIS"));
				recPara.setField("YD_STK_BED_YAXIS",     ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_YAXIS"));
				recPara.setField("YD_STK_BED_ZAXIS",     ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_ZAXIS"));
				recPara.setField("YD_STK_BED_LYR_MAX",   ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_LYR_MAX"));
				recPara.setField("YD_STK_BED_WT_MAX",    ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_WT_MAX"));
				recPara.setField("YD_STK_BED_H_MAX",     ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_H_MAX"));
				recPara.setField("YD_STK_BED_L_MAX",     ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_L_MAX"));
				recPara.setField("YD_STK_BED_W_MAX",     ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_W_MAX"));
				recPara.setField("YD_STR_GTR_CD",        ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STR_GTR_CD"));

				intRtnVal = ydStkbedDao.insYdStkbed(recPara);

				if (intRtnVal < 0) {
					szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				} else if  (intRtnVal == 0) {
					szMsg = "[JSP Session : "+szOperationName+"] 데이터 없음 ";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				} // end of if
			}

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}// end of insJPlateYdStkPosSetBed

	/**
	 * [2후판정정야드] 야드Map관리 열 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String updJPlateYdStkPosSet(JDTORecord [] inDto) throws DAOException {

		int 	intRtnVal 			= 0;
		String 	szMsg 				= "";
		String 	szMethodName		= "updJPlateYdStkPosSet";
		String	szYdStkColGp		= "";
		String 	szYdGp 				= "";
		String 	szRtnValue 			= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szOperationName 	= "저장위치 좌표설정화면 열 수정";
		String	szModifier			= "";

		JPlateYdDelegate ydDelegate = new JPlateYdDelegate();

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JPlateYdStkColDAO ydStkcolDao = new JPlateYdStkColDAO();

		try {

			szMsg = "[JSP Session : "+szOperationName+"] 야드Map관리 열 수정 >>>> 메소드 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//메뉴 페이지 목록을 수정한다.
			for (int ii=0; ii<inDto.length; ii++) {

				//수정할 항목 SETTING
				szModifier = ydDaoUtils.paraRecModifier(inDto[ii]);
				recPara.setField("MODIFIER", 				szModifier);

				//적치열번호
				recPara.setField("YD_STK_COL_NO",			ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_NO"));

				//활성상태
				recPara.setField("YD_STK_COL_ACT_STAT", 	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_ACT_STAT"));

				//기준 X축
				recPara.setField("YD_STK_COL_RULE_XAXIS",	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_RULE_XAXIS", "0"));

				//기준Y축
				recPara.setField("YD_STK_COL_RULE_YAXIS",	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_RULE_YAXIS", "0"));

				//폭
				recPara.setField("YD_STK_COL_W", 			ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_W", "0"));

				//길이
				recPara.setField("YD_STK_COL_L", 			ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_L", "0"));

				//적치열 구분 * 필수
				recPara.setField("YD_STK_COL_GP", 			ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GP"));

				intRtnVal = ydStkcolDao.updYdStkcol(recPara);

				if (intRtnVal < 0) {
					szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				} else if  (intRtnVal == 0) {
					szMsg = "[JSP Session : "+szOperationName+"] 데이터 없음 ";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				} // end of if


				// C연주 슬라브야드, A후판 슬라브야드 , 코일야드,  코일제품야드, 후판제품야드
				// L2 송신 정보 생성
				// 적치열 정보 수정후 야드별 L2 정보로 송신기능

				szYdStkColGp = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GP");
				szYdGp       = ydUtils.substr(szYdStkColGp, 0, 1);

				if ("".equals(szYdGp)) {
					// 적치열 정보가 맞지 않는경우는
					szMsg = "JSP-SESSION [저장위치 좌표설정화면 열 수정] 적치열 정보가 맞지 않습니다.";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					szRtnValue = JPlateYdConst.RETN_CD_FAILURE;
					//return szRtnValue;
					continue;
				}

				recPara = JDTORecordFactory.getInstance().create();

				recPara.setField("MSG_ID", 			"YDY7L001");
				recPara.setField("YD_INFO_SYNC_CD", "3");  //3 은 열정보 4는 베드 정보
				recPara.setField("YD_GP", 			szYdGp);
				recPara.setField("YD_STK_COL_GP", 	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GP"));
				recPara.setField("YD_STK_BED_NO", 	"");

				 szMsg = "[Jsp-Session "+szOperationName+" ] L2 로 적치열 수정된 정보 송신 시작";
				 ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				 ydDelegate.sendMsg(recPara);

				 szMsg = "[Jsp-Session "+szOperationName+" ] L2 로 적치열 수정된 정보 송신 완료";
				 ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}

			szMsg = "[JSP Session : "+szOperationName+"] 야드Map관리 열 수정 >>>> 메소드 끝 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);


			return szRtnValue;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}	// end of updJPlateYdStkPosSet

	/**
	 * [2후판정정야드] 야드Map관리 베드 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public String updJPlateYdStkPosSetBed(JDTORecord [] inDto)  throws DAOException  {

		int 	intRtnVal 		= 0;
		String 	szMsg 			= "";
		String 	szMethodName	= "updJPlateYdStkPosSetBed";
		String 	szYdgp 			= "";
		String 	szRtnValue 		= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szOperationName = "좌표설정화면 BED 수정";
		String	szModifier		= "";
		JPlateYdDelegate ydDelegate = new JPlateYdDelegate();

		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		JPlateYdStkBedDAO ydStkbedDao = new JPlateYdStkBedDAO();
		try {
			//저장위치 좌표설정화면 BED 수정

			szMsg = "[JSP Session : "+szOperationName+"] 야드Map관리 베드 수정 >>>> 메소드 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			for(int ii=0; ii<inDto.length; ii++) {

				szModifier = ydDaoUtils.paraRecModifier(inDto[ii]);
				recPara.setField("MODIFIER" 			, szModifier);

				//적치열구분 필수
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP"		, ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GP"));
				recPara.setField("YD_STK_BED_NO"		, ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_NO"));
				recPara.setField("YD_STK_COL_GP"        , ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GP"));
				recPara.setField("YD_STK_BED_NO"        , ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_NO"));
				recPara.setField("YD_STR_GTR_CD"        , ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STR_GTR_CD"));
				recPara.setField("YD_STK_BED_TP"        , ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_TP"));
				recPara.setField("YD_STK_BED_L_GP"      , ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_L_GP"));
				recPara.setField("YD_STK_BED_W_GP"      , ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_W_GP"));
				recPara.setField("YD_STK_BED_DIR_GP"    , ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_DIR_GP"));
				recPara.setField("YD_STK_BED_ACT_STAT"  , ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_ACT_STAT"));
				recPara.setField("YD_STK_BED_WHIO_STAT" , ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_WHIO_STAT"));
				recPara.setField("YD_STK_BED_XAXIS"     , ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_XAXIS"));
				recPara.setField("YD_STK_BED_YAXIS"     , ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_YAXIS"));
				recPara.setField("YD_STK_BED_ZAXIS"     , ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_ZAXIS"));
				recPara.setField("YD_STK_BED_LYR_MAX"   , ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_LYR_MAX"));
				recPara.setField("YD_STK_BED_WT_MAX"    , ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_WT_MAX"));
				recPara.setField("YD_STK_BED_H_MAX"     , ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_H_MAX"));
				recPara.setField("YD_STK_BED_L_MAX"     , ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_L_MAX"));
				recPara.setField("YD_STK_BED_W_MAX"     , ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_W_MAX"));

				intRtnVal = ydStkbedDao.updYdStkbed(recPara);

				if (intRtnVal < 0) {
					szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				} else if  (intRtnVal == 0) {
					szMsg = "[JSP Session : "+szOperationName+"] 데이터 없음 ";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				} // end of if


				// L2 송신 정보 생성
				// 적치베드 정보 수정후 야드별 L2 정보로 송신기능
				szYdgp = ydUtils.substr(ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GP"), 0, 1);

				if ("".equals(szYdgp)) {
					// 적치열 정보가 맞지 않는경우는
					szMsg = "JSP-SESSION [저장위치 좌표설정화면 열 수정] 적치열 정보가 맞지 않습니다.";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					//다음 베드 처리를 계속 진행한다.
					continue;
				}

				recPara = JDTORecordFactory.getInstance().create();

				recPara.setField("MSG_ID", 			"YDY7L001");
				recPara.setField("YD_INFO_SYNC_CD", "4");  //3 은 열정보 4는 베드 정보
				recPara.setField("YD_GP", 			szYdgp);
				recPara.setField("YD_STK_COL_GP", 	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GP"));
				recPara.setField("YD_STK_BED_NO", 	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_NO"));

				 szMsg = "[Jsp-Session "+szOperationName+" ] L2 로 적치베드 수정된 정보 송신 시작";
				 ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				 ydDelegate.sendMsg(recPara);

				 szMsg = "[Jsp-Session "+szOperationName+" ] L2 로 적치베드 수정된 정보 송신 완료";
				 ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}

			szMsg = "[JSP Session : "+szOperationName+"] 야드Map관리 베드 수정 >>>> 메소드 끝 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch (Exception e) {
			//ydUtils.putLog(SZ_SESSION_NAME, szMethodName, e.getMessage(), JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		return szRtnValue;

	}	// end of updJPlateYdStkPosSetBed

	/**
	 * [2후판정정야드] 야드Map관리 열 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void delJPlateYdStkPosSet(JDTORecord [] inDto) throws DAOException {

		int 	intRtnVal 			= 0;
		String 	szMsg 				= "";
		String 	szMethodName		= "delJPlateYdStkPosSet";
		String 	szOperationName 	= "좌표설정화면 열 삭제";
		String	szModifier			= "";

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		JPlateYdStkColDAO ydStkcolDao = new JPlateYdStkColDAO();

		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//메뉴 페이지 목록을 수정한다.
			for (int ii=0; ii<inDto.length; ii++) {

				//삭제 KEY SETTING
				szModifier = ydDaoUtils.paraRecModifier(inDto[ii]);
				recPara.setField("MODIFIER", 		szModifier);

				//적치열번호
				recPara.setField("YD_STK_COL_NO", 	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_NO"));

				//야드 구분
				recPara.setField("YD_GP", 			ydDaoUtils.paraRecChkNull(inDto[ii], "YD_GP"));

				//동구분
				recPara.setField("YD_BAY_GP", 		ydDaoUtils.paraRecChkNull(inDto[ii], "YD_BAY_GP"));

				//설비구분
				recPara.setField("YD_EQP_GP", 		ydDaoUtils.paraRecChkNull(inDto[ii], "YD_EQP_GP"));


				//적치 BED 정보가 있는지 확인

				intRtnVal = ydStkcolDao.getYdStkcolCol(recPara, outRecSet);

				if (intRtnVal < 0) {
					szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				} else if  (intRtnVal == 0) {
					szMsg = "[JSP Session : "+szOperationName+"] 데이터 없음 ";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				} // end of if


				//적치 BED 정보가 없을경우 삭제
				if (intRtnVal == 0) {
					//삭제유무
					recPara.setField("DEL_YN", 	"Y");
					szMsg = "[JSP Session : "+szOperationName+"]적치열 정보 삭제 ";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					intRtnVal = ydStkcolDao.updYdStkcol(recPara);
				}
			}

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);


		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}	// end of delJPlateYdStkPosSet

	/**
	 * [2후판정정야드] 크레인작업관리화면 : 설비 목록 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getEqpList(JDTORecord inDto) throws DAOException {

		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		String szMsg        = "";
		String szMethodName = "getEqpList";

		JPlateYdEqpDAO  ydEqpDao  = new JPlateYdEqpDAO();
		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION [설비목록 조회]시작>>";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			recPara.setField("YD_GP",      ydDaoUtils.setDataDefault(inDto.getField("YD_GP"), JPlateYdConst.YD_GP_F_PLATE_YARD));
			recPara.setField("YD_BAY_GP",  ydDaoUtils.setDataDefault(inDto.getField("YD_BAY_GP"), ""));
			recPara.setField("YD_EQP_GP",  ydDaoUtils.setDataDefault(inDto.getField("YD_EQP_GP"), ""));

			intRtnVal = ydEqpDao.getJspYdEqpList(recPara, outRecSet);		// intGp == 2

			if (intRtnVal < 0) {
				szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			} else if  (intRtnVal == 0) {
				szMsg = "[JSP Session : "+szMethodName+"] 데이터 없음 ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION [설비 목록 조회] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return outRecSet;
	}//end of getEqpList

	/**
	 * [2후판정정야드] 크레인작업관리화면 : 스케줄 목록 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSchRuleList(JDTORecord inDto) throws DAOException {

		JDTORecord      recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet	outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		String szMsg        = "";
		String szMethodName = "getSchRuleList";

		int intRtnVal = 0;

		try {

			szMsg = "JSP-SESSION [스케줄 목록 조회]시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			recPara.setField("YD_GP",      ydDaoUtils.setDataDefault(inDto.getField("YD_GP"), JPlateYdConst.YD_GP_F_PLATE_YARD));
			recPara.setField("YD_BAY_GP",  ydDaoUtils.setDataDefault(inDto.getField("YD_BAY_GP"), ""));

			JPlateYdSchRuleDAO ydSchRuleDao = new JPlateYdSchRuleDAO();

			intRtnVal = ydSchRuleDao.getSchRuleList(recPara, outRecSet);		// intGp == 4

			if (intRtnVal < 0) {
				szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			} else if  (intRtnVal == 0) {
				szMsg = "[JSP Session : "+szMethodName+"] 데이터 없음 ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION [스케줄 목록 조회] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return outRecSet;
	}//end of getSchRuleList

	/**
	 * [2후판정정야드] 크레인작업관리화면 : 크레인 작업재료 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCrnWrkMtlRef(JDTORecord inDto) throws DAOException {

		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		String szMsg        = "";
		String szMethodName = "getCrnWrkMtlRef";

		int intRtnVal = 0;

		JPlateYdCrnWrkMtlDAO ydCrnWrkMtlDao = new JPlateYdCrnWrkMtlDAO();

		try {
			szMsg = "JSP-SESSION [크레인 작업재료 조회]시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			intRtnVal = ydCrnWrkMtlDao.getByYdCrnSchId(inDto, outRecSet);			// intGp == 1 , 14 , 17

			if (intRtnVal < 0) {
				szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			} else if  (intRtnVal == 0) {
				szMsg = "[JSP Session : "+szMethodName+"] 데이터 없음 ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION [크레인 작업재료 조회] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return outRecSet;
	}//end of getCrnWrkMtlRef

	/**
	 * 크레인 관리 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getYdCrnWorkMgt(JDTORecord inDto) throws DAOException {
		int intRtnVal           = 0;
		String szMsg            = "";
		String szMethodName     = "getYdCrnWorkMgt";

		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord paraRec      = JDTORecordFactory.getInstance().create();

		JPlateYdCrnSchDAO ydCrnschDao = new JPlateYdCrnSchDAO();

		try {

			paraRec.setField("YD_GP", 		inDto.getField("YD_GP"));
			paraRec.setField("YD_BAY_GP", 	inDto.getField("YD_BAY_GP"));
			paraRec.setField("YD_SCH_CD", 	inDto.getField("YD_SCH_CD"));
			paraRec.setField("YD_EQP_ID", 	inDto.getField("YD_EQP_ID"));
			paraRec.setField("PAGE_CNT1", 	inDto.getField("PAGE_NO"));
			paraRec.setField("PAGE_CNT2", 	inDto.getField("PAGE_NO"));
			paraRec.setField("ROW_CNT1",  	inDto.getField("ROWCOUNT"));
			paraRec.setField("ROW_CNT2",  	inDto.getField("ROWCOUNT"));

			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, paraRec.toString(), JPlateYdConst.ERROR);

			intRtnVal = ydCrnschDao.getYdCrnWorkMgt(paraRec, outRecSet);		// intGp == 20

			if (intRtnVal < 0) {
				szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			} else if  (intRtnVal == 0) {
				szMsg = "[JSP Session : "+szMethodName+"] 데이터 없음 ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		return outRecSet;
	}	// end of getYdCrnWorkMgt

	/**
	 * 오퍼레이션명 : 스케줄 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord cancelJPlateYdCrnSch(JDTORecord msgRecord)throws JDTOException  {

		JPlateYdEqpDAO        	ydEqpDao 		= new JPlateYdEqpDAO();
		JPlateYdCrnSchDAO     	ydCrnSchDao  	= new JPlateYdCrnSchDAO();
		JPlateYdStkLyrDAO     	ydStkLyrDao 	= new JPlateYdStkLyrDAO();
		JPlateYdStockDAO      	ydStockDao 		= new JPlateYdStockDAO();
		JPlateYdCrnWrkMtlDAO  	ydCrnWrkMtlDao 	= new JPlateYdCrnWrkMtlDAO();

		JPlateYdDelegate 		ydDelegate 		= new JPlateYdDelegate();

		JDTORecordSet rsGetCrnSch 	= null;
		JDTORecordSet rsGetCrnMtl 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet rsCrnSchInfo 	= null;

		JDTORecord recPara   		= JDTORecordFactory.getInstance().create();
		JDTORecord recGetCrnSch   	= null;
		JDTORecord recGetCrnMtl   	= null;
		JDTORecord recGetEqp		= null;
		JDTORecord outRecord 		= JDTORecordFactory.getInstance().create();

		int intRtnVal 				= 0;
		int intRsGetCrnMtlSize 		= 0;
//		String 	szStkLyrPlus 		= null;

		//파라미터 string
		String 	szYD_CRN_SCH_ID  	= null;
		String 	szYD_SCH_CD      	= null;
		String 	szDEL_YN         	= null;
		String 	szMODIFIER       	= null;
		String 	szYD_UP_WO_LOC   	= null;
		String 	szYD_UP_WO_LAYER 	= null;
		String 	szYD_DN_WO_LOC   	= null;
		String 	szYD_DN_WO_LAYER 	= null;
		String	szYD_GP				= null;
		String 	szRtnMsg			= "";
		String 	szMsg				= "";
		String 	szMethodName		= "cancelJPlateYdCrnSch";

		String 	szYdSchId 			= "";
		String 	szYdWrkProgStat 	= "";
		String 	szEqpId 			= "";
		String 	szOperationName 	= "스케줄 삭제";

		String 	szUpdEqpstat 		= "";
		String 	szWbookId 			= "";
		String	szYdEqpStat			= "";

		try {
			szMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작   ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//========크레인스케줄 삭제==========//
			//파라미터 null 체크
			szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
			szYD_SCH_CD     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			szDEL_YN        = ydDaoUtils.paraRecChkNull(msgRecord, "DEL_YN");
			szYD_GP			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP", JPlateYdConst.YD_GP_F_PLATE_YARD);
			szMODIFIER      = ydDaoUtils.paraRecModifier(msgRecord);

			//파라미터 레코드 편집
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
			recPara.setField("YD_SCH_CD",     szYD_SCH_CD);
			recPara.setField("DEL_YN",        szDEL_YN);
			recPara.setField("MODIFIER",      szMODIFIER);

			//스케줄코드가 동일하고 크레인스케줄 ID이상인 크레인스케줄ID를 SELECT (추가 : 스케줄 ID에 포함된 같은 작업예약정보에서만 추출)
			rsGetCrnSch = JDTORecordFactory.getInstance().createRecordSet("YD");

			szMsg = "[Jsp Session : "+szOperationName+"] 스케줄코드가 동일하고 크레인스케줄 ID이상인 크레인스케줄ID를 SELECT";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			intRtnVal = ydCrnSchDao.getByYdCrnSchIdOver(recPara, rsGetCrnSch);		// intGp == 5

			//더 이상 삭제 작업이 없는경우
			if (intRtnVal < 1) {
				szMsg = "[Jsp Session : "+szOperationName+"] 삭제 작업이 완료되었습니다";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				outRecord.setField("RTN_CD" , "1");
				outRecord.setField("RTN_MSG", szMsg);
				return outRecord;
			}

			//레코드셋을 역순으로
			szMsg = "[Jsp Session : "+szOperationName+"] 레코드셋을 역순으로 정렬 - reverseOrder";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			rsGetCrnSch.reverseOrder();
			//레코드셋의 커서를 처음으로

			szMsg = "[Jsp Session : "+szOperationName+"] 레코드셋처음으로 이동";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			rsGetCrnSch.first();

			szMsg = "[Jsp Session : "+szOperationName+"] 선택된 건수 :" + rsGetCrnSch.size();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);


			//크레인스케줄 데이터 만큼 루프를 돌아서 크레인스케줄ID에 편성된 재료를 찾아 적치단을 CLEAR한다.
			for (int ii=0; ii<rsGetCrnSch.size(); ii++) {

				//크레인스케줄 데이터의 레코드를 추출(작업상태 체크를 위해 미리 추출)
				recGetCrnSch = JDTORecordFactory.getInstance().create();
				recGetCrnSch = rsGetCrnSch.getRecord(ii);

				szYdSchId 			= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_CRN_SCH_ID");
				szWbookId 			= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_WBOOK_ID");
				szYdWrkProgStat 	= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_WRK_PROG_STAT");
				szEqpId 			= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_EQP_ID");
				szYD_UP_WO_LOC   	= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_UP_WO_LOC");		//권상 지시위치
				szYD_UP_WO_LAYER 	= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_UP_WO_LAYER");	//권상 지시단
				szYD_DN_WO_LOC   	= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_DN_WO_LOC");		//권하 지시위치
				szYD_DN_WO_LAYER 	= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_DN_WO_LAYER");	//권하 지시단

				//해당 크레인스케줄ID로 크레인작업재료를 SELECT
				szMsg = "[Jsp Session : "+szOperationName+"] 해당 크레인스케줄ID로 크레인작업재료를 SELECT ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				rsGetCrnMtl = JDTORecordFactory.getInstance().createRecordSet("temp");
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID",	szYdSchId);

				intRtnVal = ydCrnSchDao.getYdCrnWrkMtl(recGetCrnSch, rsGetCrnMtl);		// intGp == 3

				//에러리턴
				if (intRtnVal <= 0) {

					szMsg = "[Jsp Session : "+szOperationName+"] 실패! 해당 작업재료 조회 ERROR :" +  intRtnVal;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					outRecord.setField("RTN_CD" , "0");
					outRecord.setField("RTN_MSG", szMsg);
					return outRecord;
				}

				//------------------------------------------------------------------------------------------------
				// 권상지시 상태(작업지시가 내려간경우) - 취소 처리 :JPlateYdConst.YD_EQP_STAT_UP_WO ==1
				//------------------------------------------------------------------------------------------------
				if (JPlateYdConst.YD_EQP_STAT_IDLE.equals(szYdWrkProgStat) ||
					JPlateYdConst.YD_EQP_STAT_OW.equals(szYdWrkProgStat) ||
					JPlateYdConst.YD_EQP_STAT_UP_WO.equals(szYdWrkProgStat)) {

					//------------------------------------------------------------------------------------------------
					//  작업지시 취소 전문 : YD_CRN_SCH_ID,YD_WRK_PROG_STAT, MSG_GP = 'D'
					//------------------------------------------------------------------------------------------------
					szMsg = "[JSP Session : "+szOperationName+"] 크레인 작업지시 취소전문 L2 전송 --- START";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					recPara   = JDTORecordFactory.getInstance().create();
					recPara.setField("MSG_ID",    			"YDY7L004");
					recPara.setField("YD_CRN_SCH_ID",    	szYdSchId);
					recPara.setField("YD_WRK_PROG_STAT", 	szYdWrkProgStat);   	// 이모듈을 탈려면 항상 '1'의값이 들어옴
					recPara.setField("YD_GP",            	JPlateYdConst.YD_GP_F_PLATE_YARD);
					recPara.setField("MSG_GP",           	"D");

					szRtnMsg = ydDelegate.sendMsg(recPara);

					szMsg = "[JSP Session : "+szOperationName+"] 크레인 작업지시 취소전문 L2 전송 --- END >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				}

				//------------------------------------------------------------------------------------------------
				// 권상/ 권하 위치 Log
				//------------------------------------------------------------------------------------------------
				szMsg  = "권상지시위치 : " + szYD_UP_WO_LOC + ", 권상 지시단  : " + szYD_UP_WO_LAYER + " >>>> ";
				szMsg += "권하지시위치 : " + szYD_DN_WO_LOC + ", 권하 지시단  : " + szYD_DN_WO_LAYER;

				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				//------------------------------------------------------------------------------------------------
				// 권하위치 원복
				//------------------------------------------------------------------------------------------------
				if ("".equals(szYD_DN_WO_LOC) || "".equals(szYD_DN_WO_LAYER) || "XX".equals(ydUtils.substr(szYD_DN_WO_LOC,0,2))) {
					//권하위치가 올바르게 잡혀있지 않을때 에러처리를 원한다면 RollBack 을 시킬수 있다.
					szMsg = "[Jsp Session : "+szOperationName+"] : 권하위치가 올바른형식이 아니라 원복시킬수 없습니다. >>>> " + szYD_DN_WO_LOC;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING);
				} else {

					//레코드의 커서를 처음으로
					szMsg = "[Jsp Session : "+szOperationName+"] 권하지시위치 " + szYD_DN_WO_LOC + "-" + szYD_DN_WO_LAYER;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					szMsg = "[Jsp Session : "+szOperationName+"] 크레인 작업재료 매수 :: " + rsGetCrnMtl.size();
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					rsGetCrnMtl.first();

					//레코드 갯수를 구한다.
					intRsGetCrnMtlSize = rsGetCrnMtl.size();

					//크레인스케줄의 작업 재료 만큼 루프를 돌아 권상권하 대기 정보를 초기화한다.
					for (int jj=0; jj<intRsGetCrnMtlSize; jj++) {

						//크레인작업재료 데이터의 레코드를 추출
						recGetCrnMtl = JDTORecordFactory.getInstance().create();
						recGetCrnMtl = rsGetCrnMtl.getRecord(jj);

						// 기존 지시위치 에 쌓여 있는 정보 CLEAR
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_STK_COL_GP",      	ydUtils.substr(szYD_DN_WO_LOC, 0, 6));
						recPara.setField("YD_STK_BED_NO",      	"");
						recPara.setField("STL_NO",				ydDaoUtils.paraRecChkNull(recGetCrnMtl, "STL_NO"));
						recPara.setField("MODIFIER", 			szMODIFIER);
						recPara.setField("YD_STK_LYR_MTL_STAT",	"D");				// 권하예약 정보 Clear 조건
						recPara.setField("YD_STK_LYR_NO", 		"");
						recPara.setField("YD_GP", 				szYD_GP);

						szMsg = "[Jsp Session : "+szOperationName+"] 기존 지시위치 에 쌓여 있는 정보 CLEAR ";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);

						//에러리턴
						if (intRtnVal < 1) {
							szMsg = "[Jsp Session : "+szOperationName+"] : 권하 지시 위치 CLEAR 실패";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING);
						} else {

							szMsg = "[JSP Session] " + szOperationName + "기존 지시위치 에 쌓여 있는 정보 CLEAR 성공 [ " + Integer.toString(intRtnVal) + " ] ";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						}

					} // end for jj
				}

				//------------------------------------------------------------------------------------------------
				//	권상위치 원복
				//------------------------------------------------------------------------------------------------
				if (!("".equals(szYD_UP_WO_LOC) || "".equals(szYD_UP_WO_LAYER))) {

					//레코드의 커서를 처음으로
					rsGetCrnMtl.first();

					//레코드 갯수를 구한다.
					intRsGetCrnMtlSize = rsGetCrnMtl.size();

					//------------------------------------------------------------------------------------------------
					//	크레인스케줄의 작업 재료 만큼 루프를 돌아 권상대기 정보를 초기화한다.
					//------------------------------------------------------------------------------------------------
					for (int jj = 0; jj < intRsGetCrnMtlSize; jj++) {

						recGetCrnMtl = JDTORecordFactory.getInstance().create();
						recGetCrnMtl = rsGetCrnMtl.getRecord(jj);

						szYD_UP_WO_LOC 	 = ydDaoUtils.paraRecChkNull(recGetCrnMtl, "YD_UP_WO_LOC");
						szYD_UP_WO_LAYER = ydDaoUtils.paraRecChkNull(recGetCrnMtl, "YD_UP_WO_LAYER");

						szMsg = "[Jsp Session : "+szOperationName+"] >>>> 권상지시 정보  : " + szYD_UP_WO_LOC + "-" + szYD_UP_WO_LAYER;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING);

						//권상지시 적치열구분 (권상지시위치 = 적치열(6) + 적치BED(2))
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_STK_COL_GP",       	ydUtils.substr(szYD_UP_WO_LOC, 0, 6));
						recPara.setField("YD_STK_BED_NO",       	ydUtils.substr(szYD_UP_WO_LOC, 6, 2));	//권상지시 적치BED번호
						recPara.setField("YD_STK_LYR_NO", 			szYD_UP_WO_LAYER);
						recPara.setField("STL_NO", 					ydDaoUtils.paraRecChkNull(recGetCrnMtl, "STL_NO"));
						recPara.setField("YD_STK_LYR_MTL_STAT", 	"C");
						recPara.setField("MODIFIER",				szMODIFIER);
						recPara.setField("YD_GP", 					szYD_GP);

						//적치단 테이블에 권상지시 CLEAR 업데이트 ('U' -> 'C')
						intRtnVal = ydStkLyrDao.updMtlStatByStlNo(recPara);
						if (intRtnVal < 1) {
							szMsg = "[Jsp Session : "+szOperationName+"] >>>> 적치단 테이블에 권상위치 적치상태 원복  실패";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						} else {
							szMsg = "[Jsp Session : "+szOperationName+"] >>>> 적치단 테이블에 권상위치 적치상태 원복  성공";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						}

						//다음 스케줄 작업 재료 레코드로가기 전 작업한 스케줄 재료를 삭제 처리 해준다.
						szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄 작업 재료 삭제처리";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_CRN_SCH_ID", 	szYdSchId);
						recPara.setField("DEL_YN", 			"Y");
						recPara.setField("MODIFIER", 		szMODIFIER);
						recPara.setField("STL_NO", 			recGetCrnMtl.getField("STL_NO"));

						intRtnVal = ydCrnWrkMtlDao.delYdCrnWrkMtl(recPara);

						if (intRtnVal < 1) {
							szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄 작업 재료 삭제처리시 ERROR";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						} else {
							szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄 작업 재료 삭제처리 성공";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						}

						//------------------------------------------------------------------------------------------------
						// 스케줄  작업 재료 삭제시 저장품에 등록된 작업예약 ID, 스케줄 CD Clear
						//------------------------------------------------------------------------------------------------
						szMsg = "[Jsp Session : "+szOperationName+"] : 저장품  작업예약 ID, 스케줄코드 CLEAR";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("STL_NO", 		recGetCrnMtl.getField("STL_NO"));
						recPara.setField("MODIFIER", 	szMODIFIER);
						recPara.setField("YD_WBOOK_ID", "");
						recPara.setField("YD_SCH_CD", 	"");

						intRtnVal = ydStockDao.updYdStockWbook(recPara);

						if (intRtnVal < 0) {
							szMsg = "[Jsp Session : "+szOperationName+"] : 저장품  작업예약 ID, 스케줄코드 CLEAR ERROR";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						} else if (intRtnVal == 0) {
							szMsg = "[Jsp Session : "+szOperationName+"] : 저장품  작업예약 ID, 스케줄코드 CLEAR 대상 없음";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING);
						} else {
							szMsg = "[Jsp Session : "+szOperationName+"] : 저장품  작업예약 ID, 스케줄코드 CLEAR 성공";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						}
					}

					//------------------------------------------------------------------------------------------------
					//	크레인스케줄 삭제처리
					//------------------------------------------------------------------------------------------------
					szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄  삭제처리";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CRN_SCH_ID", 	szYdSchId);
					recPara.setField("DEL_YN", 			"Y");
					recPara.setField("MODIFIER", 		szMODIFIER);

					intRtnVal = ydCrnSchDao.delYdCrnSch(recPara);

					if (intRtnVal > 0) {
						szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄  삭제처리 완료";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					} else {
						szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄  삭제처리 실패";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

					}

					//------------------------------------------------------------------------------------------------
					// 설비 상태를 진행상태에 맞도록 변경 시킨다.
					// 해당 작업 예약 ID으로 스케줄 정보 조회시에 하나도 존재 하지 않을경우에
					// - 해당 스케줄 코드로 전체 스케줄 조회시 남은스케줄 첫번째 진행상태 정보로 UPDATE
					// - 해당 스케줄 코드로 전체 스케줄 조회시 남아있는것이 없을경우는 대기상태로 UPDAT 해준다.
					//------------------------------------------------------------------------------------------------
					rsCrnSchInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
					recPara      = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID", szWbookId);

					intRtnVal = ydCrnSchDao.getByWrkId(recPara, rsCrnSchInfo);			// intGp == 28

					//설비 상태 UPDATE 유무 체크 FLAG
					boolean bUpdEqpFlag  = false;

					if (intRtnVal < 0) {

						szMsg = "[Jsp Session : "+szOperationName+"] : 해당 작업예약 정보에  남은 스케줄 조회시 ERROR";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

						bUpdEqpFlag = false;

					} else if (intRtnVal == 0) {

						szMsg = "[Jsp Session : "+szOperationName+"] : 해당 작업예약 정보에  남은 스케줄 정보가 없으므로 작업대기 상태로 UPDATE 합니다.";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						//해당 스케줄 코드로 같은 편성된 스케줄이 없는지 확인한다.(다른작업예약 ID가 편성되었을경우)
						rsCrnSchInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
						recPara      = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_SCH_CD", szYD_SCH_CD);

						intRtnVal = ydCrnSchDao.getByYdSchCd(recPara, rsCrnSchInfo);		// intGp == 6

						if (intRtnVal < 0) {
							szMsg = "[Jsp Session : "+szOperationName+"] :남은 스케줄코드로 스케줄 조회시 ERROR";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							bUpdEqpFlag  = false;

						}  else if (intRtnVal == 0) {
							szMsg = "[Jsp Session : "+szOperationName+"] :남은 스케줄 정보가 없으므로 작업대기 상태로 UPDATE 합니다.";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
							szUpdEqpstat = JPlateYdConst.YD_EQP_STAT_IDLE;
							bUpdEqpFlag  = true;

						} else {
							szMsg = "[Jsp Session : "+szOperationName+"] :해당 스케줄 코드에 스케줄 정보가 남아 있어 설비정보는 남은스케줄 첫번째 진행상태 정보로 UPDATE 합니다.";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
							recGetEqp    = JDTORecordFactory.getInstance().create();
							rsCrnSchInfo.first();
							recGetEqp    = rsCrnSchInfo.getRecord();
							szUpdEqpstat = ydDaoUtils.paraRecChkNull(recGetEqp, "YD_WRK_PROG_STAT");
							bUpdEqpFlag  = true;
						}

					} else {

						szMsg = "[Jsp Session : "+szOperationName+"] 해당 작업예약 정보에 스케줄 정보가 남아 있어 설비정보는 UPDATE 하지 않습니다.";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						bUpdEqpFlag  = false;
					}

                    if (bUpdEqpFlag) {

						//설비정보 업데이트 하기전에 설비상태 체크해준다.
						JDTORecord recInfo = JDTORecordFactory.getInstance().create();
						szRtnMsg = JPlateYdCommonUtils.checkCrnStat(szEqpId, recInfo);

						if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {

							szYdEqpStat = ydDaoUtils.paraRecChkNull(recInfo, "YD_EQP_STAT");

							if ("2".equals(szYdEqpStat) || "3".equals(szYdEqpStat) || "4".equals(szYdEqpStat)) {

								szMsg = "설비상태가 대기 상태가 아닌 작업상태 [" + szYdEqpStat + "]이기 때문에 값을 변경 할수 없습니다.";
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

							} else {

								recPara = JDTORecordFactory.getInstance().create();
								recPara.setField("YD_EQP_ID", 		szEqpId);
								recPara.setField("YD_EQP_STAT", 	szUpdEqpstat);
								recPara.setField("MODIFIER",		szMODIFIER);

								szMsg = "++++++++++ 해당 스케줄 크레인("+ szEqpId +") 설비상태 [" + szUpdEqpstat +"]로 변경 ++++++++++++++++++";
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

								intRtnVal = ydEqpDao.updYdEqpStat(recPara);			// intGp == 0

								if (intRtnVal < 0) {
									szMsg = szEqpId +"설비정보를 변경 실패 하였습니다.";
									ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg,JPlateYdConst.ERROR);

								}
							}
						}
					}
				}
			}

			// 작업 예약 /재료 삭제
			// 크레인 작업 재지시를 위하여  설비 아이디 , 스케줄 코드를 넘겨준다.
			// --> 2후판 정정야드는 무의미함 << 명령선택을 L2에서 함으로 지시생성시마다 전송함 ..
			outRecord.setField("YD_EQP_ID"		, szEqpId);
			outRecord.setField("YD_SCH_CD"		, szYD_SCH_CD);
			outRecord.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);
			outRecord.setField("MODIFIER"		, szMODIFIER);
			outRecord.setField("DEL_YN"			, szDEL_YN);
			outRecord.setField("RTN_CD" 		, "1");
			outRecord.setField("RTN_MSG"		, szMsg);

			return outRecord;

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}// end of cancelJPlateYdCrnSch

	/**
	 * 오퍼레이션명 : 작업예약 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord (YD_CRN_SCH_ID)
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord delJPlateWBook(JDTORecord msgRecord) throws JDTOException  {

		JPlateYdCrnSchDAO ydCrnSchDao	= new JPlateYdCrnSchDAO();

		JDTORecord outRecord = JDTORecordFactory.getInstance().create();

		//리턴레코드셋
		JDTORecordSet rsRtnVal 		= JDTORecordFactory.getInstance().createRecordSet("temRs");
		JDTORecord    recPara  		= JDTORecordFactory.getInstance().create();
		JDTORecord    recCheck  	= JDTORecordFactory.getInstance().create();
		JDTORecord    inRec  		= JDTORecordFactory.getInstance().create();

		//파라미터 스크링 변수
		String szYD_CRN_SCH_ID		= null;
		String szYD_WBOOK_ID     	= null;
		String szOperationName		= "작업예약 삭제";

		//리턴값
		int intRtnVal 				= 0;

		//체크 값
		String szMsg				= "";
		String szMethodName			= "delJPlateWBook";

		// 크레인 작업 지시 EJB Call 시 필요한 변수
		String szLogMsg 			= "";
		String szYdGp 				= "";
		String szEqpId 				= "";
		String szYD_SCH_CD 			= "";
		String szMODIFIER			= "";
		String szRtnMsg 			= "";

		szMsg = "작업예약 삭제 처리 기능 시작 >>>> " + msgRecord.toString();
	    ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		try {
			szMODIFIER  	= ydDaoUtils.paraRecModifier(msgRecord);
			szYD_WBOOK_ID 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
			szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");

			// 크레인스케줄ID 미존재시는 작업예약만 삭제함
			if (!"".equals(szYD_CRN_SCH_ID)) {

				if ("".equals(ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID"))) {
					//szMsg = "스케줄 취소 처리("+szMethodName+") 실패, YD_CRN_SCH_ID값이 없음";
					szMsg = "스케줄 ID 정보가 없어서 작업예약 삭제처리를 하지 못하였습니다";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					outRecord.setField("RTN_CD" , "0");
					outRecord.setField("RTN_MSG", szMsg);
					return outRecord;
				}

				//파라미터 레코드 setting
				recPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);

				rsRtnVal  = JDTORecordFactory.getInstance().createRecordSet("temRs");
				intRtnVal = ydCrnSchDao.getYdCrnSch(recPara, rsRtnVal);				// intGp == 0

				if (intRtnVal < 1) {
					szMsg = "해당크레인 스케줄이 존재하지않습니다";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					outRecord.setField("RTN_CD" , 	"0");
					outRecord.setField("RTN_MSG", 	szMsg);
					return outRecord;
				}

				rsRtnVal.first();
				recCheck = rsRtnVal.getRecord();
				szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(recCheck, "YD_WBOOK_ID");

				if ("".equals(szYD_WBOOK_ID)) {
					szMsg = "해당크레인 스케줄에 작업예약 정보가 존재하지않습니다.";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					outRecord.setField("RTN_CD" , "0");
					outRecord.setField("RTN_MSG", szMsg);
					return outRecord;
				}

				rsRtnVal  = JDTORecordFactory.getInstance().createRecordSet("temRs");
				intRtnVal = ydCrnSchDao.getCheckYdCrnSchId(recPara, rsRtnVal);				// intGp == 36

				if (intRtnVal < 0) {
					szMsg = JPlateYdConst.RETN_CD_FAILURE;
					outRecord.setField("RTN_CD" , 	"0");
					outRecord.setField("RTN_MSG", 	szMsg);
					return outRecord;
				} else if (intRtnVal > 0) {
					szMsg = "스케줄 정보가 남아 있습니다.";
					outRecord.setField("RTN_CD" , 	"0");
					outRecord.setField("RTN_MSG", 	szMsg);
					return outRecord;
				}

				//------------------------------------------------------------------------------------------------
				//	차량 / 대차 작업과 관계있는 작업 Clear
				//------------------------------------------------------------------------------------------------
				// 차량 또는 대차 스케줄에 있는 작업예약 ID를 Clear
				inRec = JDTORecordFactory.getInstance().create();
				inRec.setField("MODIFIER", 		szMODIFIER);
				inRec.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID);

				szRtnMsg = ydDaoUtils.delWBookBefoCarOrTCar(inRec);

				if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"] 대차/차량 스케줄 Clear성공 ";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				} else if (JPlateYdConst.RETN_CD_FAILURE.equals(szRtnMsg)) {
					szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"] 대차/차량 스케줄 Clear 실패 ";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

				} else {
					szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"]  " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				}
			}

			//------------------------------------------------------------------------------------------------
			//	작업예약 과 작업예약재료 삭제
			//------------------------------------------------------------------------------------------------
			szRtnMsg = JPlateYdCommonUtils.delYdWrkbookNMtl(szYD_WBOOK_ID, szMODIFIER);

			if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"]삭제 성공";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} else {
				szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"]삭제 실패 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

			szEqpId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			szYD_SCH_CD	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");

			//설비 ID 정보와 스케줄 코드가 들어왔을때만 실행한다.
			if ("".equals(szEqpId) || "".equals(szYD_SCH_CD)) {

				szMsg  = "[JSP Session : "+szOperationName+"] 설비 ID 정보" + szEqpId + "스케줄 코드 [" + szYD_SCH_CD + "]";
				szMsg += "중 누락된 정보가 발생하여 해당 크레인 작업지시를 호출하지 않고 마칩니다";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				outRecord.setField("RTN_CD" , 	"1");
				outRecord.setField("RTN_SND", 	"N");
				outRecord.setField("RTN_MSG", 	"성공");
				return outRecord;
			}

			szMsg = "[JSP Session : "+szOperationName+"] 설비 ID 정보" + szEqpId + "스케줄 코드 [" + szYD_SCH_CD + "]";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szYdGp = ydUtils.substr(szEqpId, 0,1);

			szLogMsg = "[JSP Session] - 작업예약 삭제 - 크레인 작업지시 : 야드구분[" + szYdGp + "]";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			outRecord = JDTORecordFactory.getInstance().create();
			outRecord.setField("MSG_ID", 			JPlateYdConst.JMS_TC_WRK_REQ);				// YDYDJ755 :: 크레인 작업지시요구
			outRecord.setField("YD_EQP_ID", 		szEqpId);
			outRecord.setField("YD_WRK_PROG_STAT", 	JPlateYdConst.YD_EQP_STAT_DN_CMPL);
			outRecord.setField("YD_SCH_CD", 		szYD_SCH_CD);
			outRecord.setField("RTN_CD" , 			"1");
			outRecord.setField("RTN_SND", 			"Y");
			outRecord.setField("RTN_MSG", 			"성공");
			return outRecord;

		} catch (Exception e) {
			szMsg = "[JSP Session : "+szOperationName+"] 크레인 작업지시 정보 호출가 발생하였습니다";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
		}

		outRecord.setField("RTN_CD" , "1");
		outRecord.setField("RTN_MSG", "성공");
		return outRecord;
	}// end of delJPlateWBook

	/**
	 * 오퍼레이션명 :
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public boolean updYdEqpRequires(JDTORecord recEqpPara) {

		boolean isSuccess 		= false;

		// 실제로 호출되는 곳 없음 .. UPDATE 항목별로 메서드 세분화하여 .. 주석처리함
		/*
		int intRtnVal 			= 0;

		JPlateYdEqpDAO ydEqpDao = new JPlateYdEqpDAO();

		try {

			intRtnVal = ydEqpDao.updYdEqp(recEqpPara);		// intGp == 0

			if (intRtnVal > 0) {
				isSuccess = true;
			}

	    } catch(DAOException daoe) {
	        throw daoe;
	    } catch(Exception e) {
	        throw new EJBServiceException(e);
	    }
	    */

	    return isSuccess;
	}

	/**
	 * (작업)크레인 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return Boolean
	 * @throws DAOException
	 */
	public String wrkCrnChange(JDTORecord[] recMsg) throws DAOException {

		JDTORecord recPara 			= null;
		JDTORecord recTemp 			= null;
		JDTORecord recEqpInfo 		= null;
		JDTORecord recSchInfo 		= null;

	 	JDTORecordSet rsrstDataSch 	= null;
	 	JDTORecordSet rsEqpInfo 	= null;
	 	JDTORecordSet rsSchInfo 	= null;

    	JPlateYdCrnSchDAO  ydCrnSchDao 	= new JPlateYdCrnSchDAO ();
    	JPlateYdWrkbookDAO ydWrkbookDao	= new JPlateYdWrkbookDAO();
    	JPlateYdSchRuleDAO ydSchRuleDao = new JPlateYdSchRuleDAO();
    	JPlateYdEqpDAO     ydEqpDao 	= new JPlateYdEqpDAO();

		String	szYdCrnSchId 		= "";
    	String 	szWbookId			= "";
    	String 	szWrkCrn 			= "";
    	String 	szAltCrn			= "";
    	String 	szChgCrn 			= "";
    	String 	szEqpId				= "";
    	String	szYdAltCrnYn		= "";
    	String	szYdEqpStat			= "";

    	int 	intGp 				= 0;
    	int 	intWrkCrnPrior		= 0;
    	int 	intAltCrnPrior 		= 0;
    	int 	intCrnPrior			= 0;

    	String 	szMethodName		= "wrkCrnChange";
    	String 	szOperationName 	= "(작업)크레인 변경";
    	String 	szLogMsg 			= "";
    	String	szRtnMsg			= "";

    	String 	szRtnStr 			= JPlateYdConst.RETN_CD_SUCCESS;

    	String 	szWrkProgStat 		= "";
    	String	szModifier			= "";

    	boolean sbSendFlag;

    	EJBConnector ejbConn 		= null;

		try {

			szLogMsg = "JSP-SESSION [" + szOperationName + " ]시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			for(int ii=0; ii<recMsg.length; ii++) {

				sbSendFlag 		= false;

				szYdCrnSchId 	= ydDaoUtils.paraRecChkNull(recMsg[ii], "YD_CRN_SCH_ID");
				szEqpId 		= ydDaoUtils.paraRecChkNull(recMsg[ii], "YD_EQP_ID");
				szModifier		= ydDaoUtils.paraRecModifier(recMsg[ii]);

				/*
				 * 1. 체크 선택 된 크레인 스케줄 정보를 가지고 온다.
				 */
				rsrstDataSch =  JDTORecordFactory.getInstance().createRecordSet("YD");
				recTemp = JDTORecordFactory.getInstance().create();
				recPara = JDTORecordFactory.getInstance().create();

				recPara.setField("YD_CRN_SCH_ID", szYdCrnSchId);

				intGp = ydCrnSchDao.getYdCrnSch(recPara, rsrstDataSch);		// intGp == 0

				if (intGp < 1) {
					// 스케줄 정보가 존재 하지 않을 경우
					szRtnStr = "스케줄 [ " +szYdCrnSchId + "정보가 존재 하지 않습니다.";
					return szRtnStr;
				}

				rsrstDataSch.first();
				recTemp = rsrstDataSch.getRecord();

				//설비 상태
				szWrkProgStat	= ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT");
				//작업예약 ID
				szWbookId 		= ydDaoUtils.paraRecChkNull(recTemp, "YD_WBOOK_ID");

				if (JPlateYdConst.YD_EQP_STAT_UP_CMPL.equals(szWrkProgStat)) {
					szRtnStr = "스케줄 ["+  szYdCrnSchId +"]가 권상완료 상태에서는 상태를 변경 할 수 없습니다.";
					szLogMsg = "JSP-SESSION [" + szOperationName + " ] " + szRtnStr;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
					return szRtnStr;
				} else if (JPlateYdConst.YD_EQP_STAT_DN_WO.equals(szWrkProgStat)) {
					szRtnStr = "스케줄 ["+  szYdCrnSchId +"]가 권하지시 상태에서는 상태를 변경 할 수 없습니다.";
					szLogMsg = "JSP-SESSION [" + szOperationName + " ] " + szRtnStr;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
					return szRtnStr;
				} else if (JPlateYdConst.YD_EQP_STAT_DN_CMPL.equals(szWrkProgStat)) {
					szRtnStr = "스케줄 ["+  szYdCrnSchId +"]가 권하완료 상태에서는 상태를 변경 할 수 없습니다.";
					szLogMsg = "JSP-SESSION [" + szOperationName + " ] " + szRtnStr;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
					return szRtnStr;
				}

				/*
				 *  2. 선택된 크레인 스케줄 ID로 편성된 스케줄 기준에서 대체 크레인 설비 ID 와 우선순위를 가지고 온다.
				 */
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_SCH_CD", recMsg[ii].getField("YD_SCH_CD"));

				rsrstDataSch =  JDTORecordFactory.getInstance().createRecordSet("YD");

				intGp = ydSchRuleDao.getYdSchrule(recPara, rsrstDataSch);		// intGp == 0

				if (intGp < 1) {
					//선택된 스케줄 기준데이터 정보에 해당 스케줄 정보가 없을경우
					szRtnStr = "선택된 스케줄 기준데이터 정보에 해당 스케줄 정보가 없음";
					szLogMsg = "JSP-SESSION [" + szOperationName + " ] " + szRtnStr;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
					return szRtnStr;
				}

				// 데이터가 실제로 1건 존재함
				recTemp = JDTORecordFactory.getInstance().create();
				rsrstDataSch.first();

				do{
					recTemp = rsrstDataSch.getRecord();

				} while(rsrstDataSch.next());

				szWrkCrn	   	= ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_CRN");
				szAltCrn	   	= ydDaoUtils.paraRecChkNull(recTemp, "YD_ALT_CRN");
				szYdAltCrnYn	= ydDaoUtils.paraRecChkNull(recTemp, "YD_ALT_CRN_YN", "N");
				intWrkCrnPrior 	= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_WRK_CRN_PRIOR");
				intAltCrnPrior 	= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_ALT_CRN_PRIOR");

				if  ("N".equals(szYdAltCrnYn)) {
					// 대체크레인 미존재 경우
					szRtnStr = "해당 설비["+ szEqpId+"]가 대체크레인이 미존재하여 변경 할 수 없습니다.";
					szLogMsg = "JSP-SESSION [" + szOperationName + " ] " + szRtnStr;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
					return szRtnStr;
				}

				/*
				 * 2-1 크레인 ID를 비교하여 주작업 크레인인 경우는 대체작업 크레인과 순위를
				 *    그렇지 않은 경우는 주작업 크레인과 순위를 변경 크레인 정보와 순위를 SETTING 한다.
				 */
				if (szEqpId.equals(szWrkCrn)) {

					szChgCrn 	= szAltCrn;
					intCrnPrior = intAltCrnPrior;

				} else {

					szChgCrn 	= szWrkCrn;
					intCrnPrior = intWrkCrnPrior;
				}

				/*
				 * 2-2 변경 할 크레인이 선택되어 있으면 설비 정보를 조회한 후 고장 또는 OFF-LINE 일 경우 변경 할 수 없다고 판단하고 RETURN 한다.
				 */
				recEqpInfo = JDTORecordFactory.getInstance().create();
				rsEqpInfo  = JDTORecordFactory.getInstance().createRecordSet("YD");
				recEqpInfo.setField("YD_EQP_ID", szChgCrn);

				//해당 설비  szChgCrn 로 설비 정보 조회
				intGp = ydEqpDao.getYdEqp(recEqpInfo, rsEqpInfo);		// intGp == 0

				if (intGp > 0) {
					rsEqpInfo.first();
					recEqpInfo  = rsEqpInfo.getRecord();

					szYdEqpStat = ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_STAT");

					if (JPlateYdConst.YD_EQP_STAT_BREAK.equals(szYdEqpStat)) {
						// 설비 상태가 고장일 경우
						szRtnStr = "변경 설비["+ szChgCrn+"]가 고장 상태여서 상태를 변경 할 수 없습니다.";
						szLogMsg = "JSP-SESSION [" + szOperationName + " ] " + szRtnStr;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
						return szRtnStr;
					}

					if (JPlateYdConst.YD_EQP_WRK_MODE_OFF_LINE.equals(ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_WRK_MODE"))) {
						// 설비 상태가 OFF_LINE 일 경우
						szRtnStr = "변경 설비["+ szChgCrn+"]가 OFF_LINE 이기때문에 상태를 변경 할 수 없습니다.";
						szLogMsg = "JSP-SESSION [" + szOperationName + " ] " + szRtnStr;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
						return szRtnStr;
					}

					if (JPlateYdConst.YD_EQP_STAT_UP_WO.equals(szYdEqpStat) ||
						JPlateYdConst.YD_EQP_STAT_UP_CMPL.equals(szYdEqpStat) ||
						JPlateYdConst.YD_EQP_STAT_DN_WO.equals(szYdEqpStat)) {
					//	JPlateYdConst.YD_EQP_STAT_DN_CMPL.equals(szYdEqpStat)) {

						szRtnStr = "변경 설비["+ szChgCrn+"]가 작업지시가("+szYdEqpStat+") 내려가 있기 때문에 상태를 변경 할 수 없습니다.";
						szLogMsg = "JSP-SESSION [" + szOperationName + "] " + szRtnStr;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
						return szRtnStr;
					}

					// 2009.12.07 [작업지시 취소 전문은 발생하지 않는다 - 작업재지시 전문만 발생시켜준다]
					// 선택된 정보가 선택된 정보일 경우 작업지시 취소 정보를 송신한다.
					recSchInfo = JDTORecordFactory.getInstance().create();
					rsSchInfo  = JDTORecordFactory.getInstance().createRecordSet("YD");

					recSchInfo.setField("YD_CRN_SCH_ID", recMsg[ii].getField("YD_CRN_SCH_ID"));

					intGp = ydCrnSchDao.getYdCrnSch(recSchInfo, rsSchInfo);		// intGp == 0

					if (intGp > 0) {

						rsSchInfo.first();
						recSchInfo = rsSchInfo.getRecord();

						// 현 작업 상태가 선택인 경우는 작업재지시 전문을 전송하기위채 체크해놓는다.
						if (JPlateYdConst.YD_EQP_STAT_UP_WO.equals(ydDaoUtils.paraRecChkNull(recSchInfo, "YD_WRK_PROG_STAT"))) {
							//작업재지시 전문을 전송하기 위하여 Flag Setting
							sbSendFlag = true;
						}
					}
				} else {
					//해당 설비가 존재 하지 않습니다.
					szRtnStr = "해당 설비["+ szChgCrn+"]가 존재 하지 않습니다";
					szLogMsg = "JSP-SESSION [" + szOperationName + " ] " + szRtnStr;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
					return szRtnStr;
				}

				/*
				 * 3. 선택된 크레인 스케줄 작업예약 ID에  대체 설비 ID 와 우선순위 를 UPDATE 한다.
				 */
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID",		szWbookId);
				recPara.setField("YD_SCH_PRIOR", 	Integer.toString(intCrnPrior));
				recPara.setField("MODIFIER",		recMsg[ii].getField("MODIFIER"));
				intGp = ydWrkbookDao.updYdWrkbook(recPara);		// intGp == 0
				if (intGp <1) {
					szRtnStr = "선택된 크레인 스케줄 작업예약 ID : "+szWbookId+"에   우선순위 ("+intCrnPrior+") 를 UPDATE 중 ERROR";
					szLogMsg = "JSP-SESSION [" + szOperationName + " ] " + szRtnStr;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
					return szRtnStr;
				}

				/*
				 * 4. 작업예약 ID 로 현재 편성된 스케줄 ID [] 를 구한다.
				 */
				recPara = JDTORecordFactory.getInstance().create();
				rsrstDataSch = JDTORecordFactory.getInstance().createRecordSet("YD");
				recPara.setField("YD_WBOOK_ID",			szWbookId);
				recPara.setField("YD_WRK_PROG_STAT",	"W");

				// 기존쿼리는 W 이상태만 체크하였으나 지금은 1,W 상태를 조회한다.
				intGp = ydCrnSchDao.getByYdWBookIdStat(recPara, rsrstDataSch);		// intGp == 23
				if (intGp <1) {
					// 해당 작업 ID 에 편성된 스케줄 정보가 없을경우
					szRtnStr = "해당작업 ID 에 편성된 스케줄 정보가 존재하지 않음";
					szLogMsg = "JSP-SESSION [" + szOperationName + " ] " + szRtnStr;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
					return szRtnStr;
				}

				//크레인 스케줄 정보 변경
				rsrstDataSch.first();

				do {
					/*
					 * 5. 편성된 스케줄 ID [] 에  대체 크레인 설비 ID와 우선순위를 편성
					 */
					recTemp = JDTORecordFactory.getInstance().create();
					recPara = JDTORecordFactory.getInstance().create();

					recTemp = rsrstDataSch.getRecord();

					recPara.setField("YD_CRN_SCH_ID", 	recTemp.getField("YD_CRN_SCH_ID"));
					recPara.setField("YD_EQP_ID",		szChgCrn);
					recPara.setField("YD_SCH_PRIOR",  	Integer.toString(intCrnPrior));
					recPara.setField("MODIFIER",		recMsg[ii].getField("MODIFIER"));

					// 5. 스케줄 테이블에 UPDATE : 스케쥴 우선순위변경
					intGp = ydCrnSchDao.updSchPrior(recPara);
					if (intGp <1) {
						szRtnStr = "스케줄 테이블에 UPDATE 중 ERROR";
						szLogMsg = "JSP-SESSION [" + szOperationName + " ] " + szRtnStr;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
						return szRtnStr;
					}

					/* 2후판정정야드는 필요 없음 .. 주석처리
					//-------------------------------------------------------------------------------------------------------------
					//	크레인 허용 오차 및 크레인 X, Y좌표 계산 -
					//-------------------------------------------------------------------------------------------------------------
					szLogMsg ="크레인 변경 후 제원 위치정보 세팅 호출";
	        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

	        		recTemp2.setField("YD_CRN_SCH_ID", recTemp.getField("YD_CRN_SCH_ID"));
					JPlateYdUtils.updYdCrnschBedData(recTemp2);

					szLogMsg ="크레인 스케줄 변경 후 제원 위치정보 세팅 완료";
	        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
					*/

				} while(rsrstDataSch.next());

				/*
				 * 6. 작업 재지시 정보를 호출하여준다.
				 */

				//sbSendFlag 가 True 라는것은 변경전 스케줄 상태가 작업선택상태임을 나타낸다.
				if (sbSendFlag) {

					szLogMsg = "명령선택 취소 EJB 호출 .. START";
	        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				 	// 명령선택 취소처리 수행
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("JMS_TC_CD", 			"Y7YDL012");
					recPara.setField("YD_EQP_ID", 			szEqpId);				// 야드설비ID
					recPara.setField("YD_CMD_PKUP_GP", 		"C");					// 야드명령선택구분 - S:명령선택, C:취소
					recPara.setField("YD_CRN_SCH_ID", 		szYdCrnSchId);			// 야드크레인스케쥴ID
					recPara.setField("MODIFIER", 			szModifier);

					ejbConn  = new EJBConnector("default", this);
					szRtnMsg = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY7CrnOrderSel", recPara);

					szLogMsg = "명령선택 취소 EJB 호출 .. END >>>> " + szRtnMsg;
	        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				}

			 	// 명령선택 처리 수행
				szLogMsg = "명령선택 EJB 호출 .. START";
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			 	// 명령선택 처리 수행
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("JMS_TC_CD", 			"Y7YDL012");
				recPara.setField("YD_EQP_ID", 			szChgCrn);				// 야드설비ID
				recPara.setField("YD_CMD_PKUP_GP", 		"S");					// 야드명령선택구분 - S:명령선택, C:취소
				recPara.setField("YD_CRN_SCH_ID", 		szYdCrnSchId);			// 야드크레인스케쥴ID
				recPara.setField("MODIFIER", 			szModifier);

				ejbConn  = new EJBConnector("default", this);
				szRtnMsg = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY7CrnOrderSel", recPara);

				szLogMsg = "명령선택 EJB 호출 .. END >>>> " + szRtnMsg;
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			}
		} catch(DAOException e) {

			throw new DAOException(getClass().getName() + e.getMessage(),e);

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);

		}

		szLogMsg = "JSP-SESSION [(작업)크레인 변경 ]끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}

	/**
	 * 크레인 우선 순위 변경
	 * Input  : YD_CRN_SCH_ID : 스케줄 ID
	 *          CRN_PRIOR : 크레인 우선순위
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public Boolean crnChgSchPrior(JDTORecord [] recMsg) throws DAOException {
		JDTORecord recPara = null;
		JDTORecord recTemp = null;
		int intSchPrior = 0;
		boolean bool = false;

		String szMethodName="crnChgSchPrior";
		String szLogMsg = "";



    	JDTORecordSet rsrstDataSch =  JDTORecordFactory.getInstance().createRecordSet("YD");

    	int intGp = 0;

    	JPlateYdCrnSchDAO  ydCrnSchDao  = new JPlateYdCrnSchDAO();
    	JPlateYdWrkbookDAO ydWrkbookDao = new JPlateYdWrkbookDAO();

		try {
			szLogMsg = "JSP-SESSION [크레인 우선 순위 변경]시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);


			for(int ii=0; ii<recMsg.length; ii++) {
				recPara = JDTORecordFactory.getInstance().create();

				// 1.  작업예약 ID ,크레인 ID , 입력받은 스케줄 우선순위
				recPara.setField("YD_WBOOK_ID" 	, recMsg[ii].getField("YD_WBOOK_ID"));
				recPara.setField("YD_SCH_PRIOR" , recMsg[ii].getField("YD_SCH_PRIOR"));
				intSchPrior = recMsg[ii].getFieldInt("YD_SCH_PRIOR");

				// 2. 작업 예약 정보 변경
				ydWrkbookDao.updYdWrkbook(recPara);		// intGp == 0

				// 3. 작업예약에 편성된 스케줄정보 조회

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID"		, recMsg[ii].getField("YD_WBOOK_ID"));
				recPara.setField("YD_WRK_PROG_STAT" , "W");

				intGp = ydCrnSchDao.getByYdWBookIdStat(recPara, rsrstDataSch);		// intGp == 23

				if (intGp <1) {
					//해당 정보가 없을경우는 미처리
					throw new DAOException();
				}

				//크레인 스케줄 정보 변경
				rsrstDataSch.first();

				do {
					// 3. 스케쿨 ID 에  입력받은 크레인 우선순위를 편성시킨다.

					recTemp = JDTORecordFactory.getInstance().create();
					recPara = JDTORecordFactory.getInstance().create();

					recTemp = rsrstDataSch.getRecord();

					recPara.setField("YD_CRN_SCH_ID", 	recTemp.getField("YD_CRN_SCH_ID"));
					recPara.setField("YD_SCH_PRIOR", 	Integer.toString(intSchPrior));

					// 4. 스케줄 테이블에 UPDATE : 우선순위 변경
					ydCrnSchDao.updSchPrior(recPara);

				} while(rsrstDataSch.next());
			}

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		bool = true;

		szLogMsg = "JSP-SESSION [크레인 우선 순위 변경] 끝 ";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return new Boolean(bool);

	}

	/**
	 * 권하위치 변경 (크레인작업관리 화면)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public boolean updToPosFix(JDTORecord[] inDto) throws DAOException {

		int 	intRtnVal          	= 0;
		String 	szLogMsg         	= null;
		String 	szMethodName    	= "updToPosFix";
		String 	szOperationName 	= "권하위치 변경 (크레인작업관리 화면)";

		String szStkPos        		= null;
		String szStkColGp      		= null;
		String szStkBedNo      		= null;
		String szStkLyrNo      		= null;

		JDTORecordSet   outRecSet  	= null;

		JDTORecord    recPara  		= null;
		JDTORecord    recInPara 	= null;
		JDTORecord    recTemp  		= null;
		JDTORecord    recCrnSch		= null;
		JDTORecord    recInTemp		= null;

		String 	szOldStkPos     	= null;
		String 	szOldStkColGp   	= null;
		String 	szOldStkBedNo   	= null;
		String 	szOldStkLyrNo   	= null;

		boolean rtnBool 			= false;

		JPlateYdStkLyrDAO    ydStkLyrDao	= new JPlateYdStkLyrDAO();
		JPlateYdCrnSchDAO    ydCrnSchDao    = new JPlateYdCrnSchDAO();
		JPlateYdCrnWrkMtlDAO ydCrnWrkMtlDao = new JPlateYdCrnWrkMtlDAO();

		String 	szSendYdWrkProgStat = "";
		String 	szYdGp  			= "";
		String 	szYdSchCd 			= "";

	    String 	szYD_EQP_ID 		= "";
	    String 	szRtnMsg 			= "";
	    String 	szYdSchId 			= "";
	    String	szStlNo				= "";

	    String 	szYdGpTemp 			= "";
	    String 	szEqpGp 			= ""; 		// 변경 설비구분
	    String 	szEqpGpBefo 		= ""; 		// 기존 설비구분
	    String 	szYdWbookId 		= ""; 		// 작업예약 ID
	    String 	szRtnMsg1			= null;
	    String	szModifier			= "";		// 변경자
		String	szStkColGpTo 		= "";
		String	szStkBedNoTo 		= "";
		String	szStkLyrNoTo 		= "";
		String	szYdStkSpanGp		= "";
		String	szEmptyBsLoc		= "";
		String	szTempLyrNo			= "";
		String	szYdUpWrkActGp		= "";
		String	szTopLyrNo			= "";

		int		iPlusLyrNo 			= 0;
		int		iCrMtlCnt			= 0;
		int		iBedUseCnt			= 0;
		int		iYdStkBedCnt		= 0;		// 권하위치의 베드 갯수

		try {
			szLogMsg = "JSP-SESSION [권하위치 변경 (크레인작업관리 화면)] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			for (int ii=0; ii<inDto.length; ii++) {

				// 1. 기존 To 위치 정보 와 변경 To 위치 정보
				recPara   	= JDTORecordFactory.getInstance().create();
				outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("YD");

				szModifier 	= ydDaoUtils.paraRecModifier(inDto[ii]);
				szYdSchId 	= ydDaoUtils.paraRecChkNull(inDto[ii], "YD_CRN_SCH_ID");
				szYdGp		= ydDaoUtils.paraRecChkNull(inDto[ii], "YD_GP", JPlateYdConst.YD_GP_F_PLATE_YARD);

				recPara.setField("YD_CRN_SCH_ID", szYdSchId);

				szLogMsg = "[JSP Session] " + szOperationName + " 크레인스케줄 ID :["+szYdSchId+"]로 스케줄 조회";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				intRtnVal = ydCrnSchDao.getYdCrnSch(recPara, outRecSet);		// intGp == 0

				if (intRtnVal < 0) {
					szRtnMsg = "해당 크레인스케줄 ID  정보: ["+ szYdSchId +" ]조회시 ERROR발생";
					szLogMsg = "[JSP Session] " + szOperationName + szRtnMsg;
            		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            		throw new DAOException(szRtnMsg);
				} else if (intRtnVal == 0) {
					szRtnMsg = "해당 스케줄크레인스케줄 ID  정보: ["+ szYdSchId + "] 가 존재하지않습니다";
					szLogMsg = "[JSP Session] " + szOperationName + szRtnMsg;
            		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            		throw new DAOException(szRtnMsg);
				}

				szLogMsg = "[JSP Session] " + szOperationName + " 크레인스케줄 ID :["+szYdSchId+"]로 스케줄 조회 성공";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				outRecSet.first();

				recCrnSch = JDTORecordFactory.getInstance().create();
				recCrnSch.setRecord(outRecSet.getRecord());

				szOldStkPos   	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC");
				szOldStkLyrNo 	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LAYER");
				szYdWbookId   	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WBOOK_ID");
				szStlNo			= ydDaoUtils.paraRecChkNull(recCrnSch, "LAST_STL_NO");
		        szYdUpWrkActGp 	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WRK_ACT_GP");		//야드권상작업수행구분(YD_UP_WRK_ACT_GP) 항목사용 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)
				szOldStkColGp 	= ydUtils.substr(szOldStkPos, 0, 6);
				szOldStkBedNo 	= ydUtils.substr(szOldStkPos, 6, 2);

				//현 스케줄 작업 진행상태(DB)
				szSendYdWrkProgStat =  ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WRK_PROG_STAT");

				szLogMsg = "[JSP Session] " + szOperationName + "현 스케줄 작업 진행상태(DB)[" + szSendYdWrkProgStat +"]";
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				szLogMsg = "[JSP Session] " + szOperationName + "DB 권하지시위치 [" + szOldStkPos + ">>>>" + szOldStkColGp + szOldStkBedNo + "-" + szOldStkLyrNo +"]";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				szYdGpTemp = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_GP");
				if ("".equals(szYdGpTemp)) {
					szYdGpTemp = ydUtils.substr(szOldStkPos, 0, 1);
				}

				szLogMsg = "[JSP Session] " + szOperationName + "야드구분 [" + szYdGpTemp +"]";
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				szStkPos = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_DN_WO_LOC");

				szLogMsg = "[JSP Session] " + szOperationName + "입력받은 권하지시위치 [" + szStkPos + "]";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				if ("".equals(szStkPos)) {
					szRtnMsg = "변경 권하지시위치 정보가 없습니다.";
					szLogMsg = "[JSP Session] " + szOperationName + szRtnMsg;
            		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            		throw new DAOException(szRtnMsg);
				}

				if (szOldStkPos.equals(szStkPos)) {
					szRtnMsg = "입력된 권하위치가 기존 권하위치와 같습니다.";
					szLogMsg = "[JSP Session] " + szOperationName + szRtnMsg;
            		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            		throw new DAOException(szRtnMsg);
				}

				szStkLyrNo = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_DN_WO_LAYER");

				if ("".equals(szStkLyrNo)) {
					szLogMsg = "[JSP Session] " + szOperationName +  "변경 권하지시단 정보가 없으면 재계산하여줍니다.";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				}

				if (szStkPos.length() >= 8) {
					szStkColGp 	= ydUtils.substr(szStkPos,   0, 6);
					szStkBedNo 	= ydUtils.substr(szStkPos,   6, 2);
					szEqpGp 	= ydUtils.substr(szStkColGp, 2, 2);
				} else {
					szRtnMsg = "변경 권하지시위치 정보가 맞지 않습니다";
					szLogMsg = "[JSP Session] " + szOperationName + szRtnMsg;
            		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            		throw new DAOException(szRtnMsg);
				}

				//-----------------------------------------------------------------------
				//	권하지시위치 변경 시 베드의 TO위치 정합성 판단.
				//-----------------------------------------------------------------------
				JPlateYdStkLocVO ydStkLocVO	= new JPlateYdStkLocVO();
				recInPara = JDTORecordFactory.getInstance().create();

				/* 파라미터정의:	1) YD_STK_COL_GP	- 적치열
				 * 				2) YD_STK_BED_NO	- 적치베드
				 * 				3) YD_EQP_WRK_SH	- 작업총매수
				 * 				4) YD_EQP_WRK_WT	- 작업총중량
				 * 				5) YD_EQP_WRK_T		- 작업총두께
				 * 				6) YD_SCH_CD		- 스케줄코드
				 */
				recInPara.setField("YD_STK_COL_GP", szStkColGp);
				recInPara.setField("YD_STK_BED_NO", szStkBedNo);
				recInPara.setField("YD_EQP_WRK_SH", ydDaoUtils.paraRecChkNull(recCrnSch, "YD_EQP_WRK_SH"));
				recInPara.setField("YD_EQP_WRK_WT", ydDaoUtils.paraRecChkNull(recCrnSch, "YD_EQP_WRK_WT"));
				recInPara.setField("YD_EQP_WRK_T" , ydDaoUtils.paraRecChkNull(recCrnSch, "YD_EQP_WRK_T"));
				recInPara.setField("YD_EQP_WRK_L" , ydDaoUtils.paraRecChkNull(recCrnSch, "YD_EQP_WRK_L"));
				recInPara.setField("YD_SCH_CD"	  , ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD"));

				szLogMsg = "[JSP Session- " + szOperationName + "] 권하위치 체크 시작";
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				szRtnMsg1 = JPlateYdToLocUtil.procBedStackable(recInPara, ydStkLocVO, szMethodName);

				szLogMsg = "[JSP Session- " + szOperationName + "] 권하위치 체크 결과 :: "+szRtnMsg1;
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        		int intERR_CD = 0;
        		StringBuffer szSTATUS = new StringBuffer();

				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg1)) {
					if (JPlateYdConst.RETN_CD_NOTEXIST.equals(szRtnMsg1)) {

						intERR_CD = ydStkLocVO.getYdBedErrCd();

						if (intERR_CD >= JPlateYdConst.YD_BED_ERR_CD_H_OVER) {
							//해당하는 적치베드에 적치가능높이 OVER
							intERR_CD -= JPlateYdConst.YD_BED_ERR_CD_H_OVER;

							szSTATUS.append("적치가능높이 OVER");
						}

						if (intERR_CD >= JPlateYdConst.YD_BED_ERR_CD_WT_OVER) {
							//해당하는 적치베드에 적치가능중량 OVER
							intERR_CD -= JPlateYdConst.YD_BED_ERR_CD_WT_OVER;

							if (szSTATUS.length() > 0) szSTATUS.append(", ");

							szSTATUS.append("적치가능중량 OVER");
						}

						if (intERR_CD == JPlateYdConst.YD_BED_ERR_CD_SH_OVER) {
							//해당하는 적치베드에 적치가능매수 OVER

							if (szSTATUS.length() > 0) szSTATUS.append(", ");

							szSTATUS.append("적치가능매수 OVER");
						}

						szRtnMsg = "해당크레인스케줄["+szYdSchId+"]의 권하지시적치열["+szStkColGp+"], 권하지시베드["+szStkBedNo+"]에 적치불가능합니다 - " + szSTATUS.toString();
					} else {
						szRtnMsg = "권하위치 체크 오류 >>>> " + szRtnMsg1;
					}
					throw new DAOException(szRtnMsg);
				}

				if (ydStkLocVO.getYdBedErrCd() != JPlateYdConst.YD_BED_STACKABLE) {
					szRtnMsg = "적치불가능합니다. >>>> 오류코드 : " + ydStkLocVO.getYdBedErrCd();
					throw new DAOException(szRtnMsg);
				}

				//-----------------------------------------------------------------------
				//	권하위치 변경할시에 작업지시 취소전문 발생 (데이터 변경전정보)
				//-----------------------------------------------------------------------
				szYdSchCd = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD");

				// 신규 위치 적치단 정보
				szLogMsg = "[JSP Session] " + szOperationName + "변경 권하지시단 정보 계산";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				recPara   = JDTORecordFactory.getInstance().create();
				recTemp   = outRecSet.getRecord();
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

				recPara.setField("YD_STK_COL_GP", 	szStkColGp);
				recPara.setField("YD_STK_BED_NO", 	szStkBedNo);
				recPara.setField("STL_NO", 			szStlNo);

				szLogMsg = "[JSP Session] " + szOperationName + "적치단 정보조회";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				intRtnVal = ydStkLyrDao.getYdStklyrByColGpBedNo(recPara, outRecSet);		// intGp == 29

				if (intRtnVal == 0) {
					szStkLyrNo = "001";
				} else if (intRtnVal > 0) {
					outRecSet.first();
					recTemp 	 = outRecSet.getRecord();
					szStkLyrNo 	 = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");
					iYdStkBedCnt = ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_CNT");
				}

				szLogMsg = "[JSP Session] " + szOperationName +  "신규위치정보 :"+ szStkColGp + "-"+ szStkBedNo +"-" +szStkLyrNo + ", 베드갯수 : " + iYdStkBedCnt;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(inDto[ii], "YD_CRN_SCH_ID"));
				recPara.setField("YD_STK_COL_GP", szStkColGp);

				outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

				szLogMsg = "[JSP Session] " + szOperationName +  "크레인 작업재료 조회";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				intRtnVal = ydCrnWrkMtlDao.getBySchIdStlNo(recPara, outRecSet);

				szLogMsg = "[JSP Session] " + szOperationName +  "크레인 작업재료 조회 리턴값 :[ "+  intRtnVal +" ]";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				if (intRtnVal == 0) {
					szRtnMsg = "해당 스케줄에 해당되는 크레인 작업 재료가 없습니다.";
					szLogMsg = "[JSP Session] " + szOperationName + szRtnMsg;
            		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            		throw new DAOException(szRtnMsg);
				} else if (intRtnVal < 0) {
					szRtnMsg = "해당 스케줄에 해당되는 재료 조회시 ERROR >>>> " + Integer.toString(intRtnVal);
					szLogMsg = "[JSP Session] " + szOperationName + szRtnMsg;
            		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            		throw new DAOException(szRtnMsg);
				}

				// 기존 권하작업 위치 Clear
				szLogMsg = "[JSP Session] " + szOperationName +  "기존위치정보 :"+ szOldStkColGp + "-"+ szOldStkBedNo +":" +szOldStkLyrNo;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				//실제로는 크레인작업재료의 개수만 필요함
				outRecSet.first();
				for (int jj=0; jj<outRecSet.size(); jj++) {

					recTemp = JDTORecordFactory.getInstance().create();
					recTemp.setRecord(outRecSet.getRecord(jj));

					// 기존 지시위치 에 쌓여 있는 정보 Clear
					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("STL_NO",              	ydDaoUtils.paraRecChkNull(recTemp, "STL_NO"));
					recInPara.setField("YD_STK_LYR_MTL_STAT",	"D");		// 권하예약 정보 Clear 조건
					recInPara.setField("MODIFIER", 				szModifier);
					recInPara.setField("YD_GP", 				szYdGp);

	                szLogMsg = "[JSP Session] " + szOperationName + "기존 지시위치 에 쌓여 있는 정보 Clear >>>> " + recInPara.toString();
            		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

	                intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recInPara);

	            	szLogMsg = "[JSP Session] " + szOperationName + "기존 지시위치 에 쌓여 있는 정보 Clear 성공 [ " + Integer.toString(intRtnVal) + " ] ";
	            	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
	            }

				outRecSet.first();

				iCrMtlCnt = outRecSet.size();

				szLogMsg = "["+szOperationName+"] <<<< DEBUG >>>> iCrMtlCnt :: " + iCrMtlCnt + ", szYdUpWrkActGp :: " + szYdUpWrkActGp + ", szStkLyrNo :: " + szStkLyrNo;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				for (int kk=0; kk<iCrMtlCnt; kk++) {

					// 신규위치에 정보를 Setting
					recInPara = JDTORecordFactory.getInstance().create();
					recTemp   = JDTORecordFactory.getInstance().create();
					recTemp.setRecord(outRecSet.getRecord(kk));

					// 보수장, 가스장, 냉각대, TOD, RT 일때 적치가능한 공베드 재검색
					szYdStkSpanGp = ydUtils.substr(szStkColGp, 2, 2);
					if ("BS".equals(szYdStkSpanGp) || "RT".equals(szYdStkSpanGp) || "CN".equals(szYdStkSpanGp) ||
						"CB".equals(szYdStkSpanGp) || "TD".equals(szYdStkSpanGp)) {

						szEmptyBsLoc = ydStkLyrDao.getEmptyBsLoc(szStkColGp, szStkBedNo, "");
						szStkColGpTo = ydUtils.substr(szEmptyBsLoc, 0, 6);
						szStkBedNoTo = ydUtils.substr(szEmptyBsLoc, 6, 2);
						szStkLyrNoTo = "001";

					} else {
						szStkColGpTo = szStkColGp;

						// 파일링 작업업 아니고 적치매수가 1이상일때
						if (iCrMtlCnt > 1 && !"P".equals(szYdUpWrkActGp)) {

							// 권하위치변경시 기존 작업지시의 베드 정보 참조하여 SET - 횡작업시
							szStkBedNoTo 	 = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LOT_TP", szStkBedNo);
							szStlNo			 = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");

							if (kk == 0) {
								szTopLyrNo	 = ydDaoUtils.paraRecChkNull(recTemp, "TOP_LYR_NO", "000");
								szTopLyrNo   = ydDaoUtils.stringPlusInt(szTopLyrNo,  1);		// 적치단을    1증가

								szStkLyrNoTo = szTopLyrNo;

							} else {

								// 권상위치의 적치단이 변경시
								if (!szTempLyrNo.equals(ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO"))) {

									iBedUseCnt  = ydDaoUtils.paraRecChkNullInt(recTemp, "BED_USE_CNT");		// 동일베드 적치 건수 - 횡작업여부 체크

									if (iCrMtlCnt > 3 || iBedUseCnt > 1) {
										iPlusLyrNo ++;
										szTopLyrNo = ydDaoUtils.stringPlusInt(szTopLyrNo,  1);				// 적치단을    1증가

									}
									szLogMsg = "["+szOperationName+"] szTopLyrNo >>>> " + szTopLyrNo;
				        			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

								}
								szStkLyrNoTo = ydDaoUtils.stringPlusInt(szStkLyrNoTo, iPlusLyrNo);			// 적치단을 증가

								// 강제권상시 권상위치 정보로 적치베드,적치단을 SET
					            if ("F".equals(szYdUpWrkActGp)) {
									// 권하베드가 2베드이고 권상베드가 3베드일때
									if (!"".equals(szStkBedNoTo) && Integer.parseInt(szStkBedNoTo) > iYdStkBedCnt) {
										szStkBedNoTo = ydUtils.addLeftStr(Integer.toString(iYdStkBedCnt), 2, '0');
									}
					            	szStkLyrNoTo = JPlateYdCommonUtils.getTopLyrNoByColGp(szStkColGpTo, szStkBedNoTo, szStlNo, szYdUpWrkActGp, szTopLyrNo);
					            }
							}
							szTempLyrNo  = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");

						} else {
							szStkBedNoTo = szStkBedNo;
							szStkLyrNoTo = ydDaoUtils.stringPlusInt(szStkLyrNo, kk);
						}
					}

					recInPara.setField("YD_STK_COL_GP",       szStkColGpTo);
					recInPara.setField("YD_STK_BED_NO",       szStkBedNoTo);
					recInPara.setField("YD_STK_LYR_NO",       szStkLyrNoTo);
					recInPara.setField("YD_STK_LYR_MTL_STAT", "D");
					recInPara.setField("STL_NO",              ydDaoUtils.paraRecChkNull(recTemp, "STL_NO"));
					recInPara.setField("MODIFIER",            szModifier);

	            	szLogMsg = "[JSP Session] " + szOperationName +   "신규위치에 정보를 UPDATE >>>> " + szStkColGpTo + szStkBedNoTo + "-" + szStkLyrNoTo;
	            	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

	                intRtnVal = ydStkLyrDao.updYdStklyrDownStat(recInPara);

	            	if (intRtnVal < 1) {
						//신규위치에 정보를 Setting 실패
	            		szRtnMsg = "신규위치에 정보를 Setting 실패 [ " + Integer.toString(intRtnVal) + " ]";
	            		szLogMsg = "[JSP Session] " + szOperationName + szRtnMsg;
	            		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
	            		throw new DAOException(szRtnMsg);
					}

	        		//신규위치에 정보를 Setting 실패
            		szLogMsg = "[JSP Session] " + szOperationName + "신규위치에 정보를 Setting 성공 [ " + Integer.toString(intRtnVal) + " ]";
            		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				}

				// 권하위치 정보 스케줄 정보에서 변경
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setRecord(recCrnSch);
				recPara.setField("MODIFIER", 				szModifier);
				recPara.setField("YD_CRN_SCH_ID", 			ydDaoUtils.paraRecChkNull(inDto[ii], "YD_CRN_SCH_ID"));
				recPara.setField("YD_DN_WO_LOC", 			szStkColGp+szStkBedNo);
				recPara.setField("YD_DN_WO_LAYER", 			szStkLyrNo);
				recPara.setField("YD_DN_WO_FLAG",			"Y");

				szLogMsg = "[JSP Session] " + szOperationName + " 권하위치 정보 스케줄 정보에서 변경(UPDATE)";
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        		szRtnMsg = JPlateYdCrnSchUtil.uptCrnSchXYCord(recPara);

				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					szRtnMsg = "권하위치 스케줄 정보 변경 실패  >>>> " + szRtnMsg;
					szLogMsg = "[JSP Session] " + szOperationName + szRtnMsg;
            		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            		throw new DAOException(szRtnMsg);
				}

				szLogMsg = "[JSP Session] " + szOperationName + " 권하위치 정보 스케줄 정보 UPDATE 성공 ";
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				// 스케줄 변경 후 제원 위치정보를 맞춰준다.
				szLogMsg = "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 호출";
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

        		boolean lb_updYdCrnBed = false;
        		lb_updYdCrnBed = JPlateYdUtils.updYdCrnschBedData(recPara);

        		if (!lb_updYdCrnBed) {
        			szLogMsg = "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 실패";
            		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
        		}

				szLogMsg = "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 완료";
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				// 권하위치 변경후 작업지시정보를  재전송 (Manual 작업지시보내는 방법으로  선택 인경우만 전송한다)
        		// 2013.07.17 2후판 정정야드는 무조건 재전송 하도록 보완
				//if (JPlateYdConst.YD_EQP_STAT_UP_WO.equals(szSendYdWrkProgStat) ||
				//    JPlateYdConst.YD_EQP_STAT_UP_CMPL.equals(szSendYdWrkProgStat)) {

					szLogMsg = "[JSP Session] " + szOperationName +  "권하위치 변경후 작업지시정보를  재전송";
	        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

					if (szYdSchCd.length() > 0) {

						szYdGp = ydUtils.substr(szYdSchCd, 0, 1);
					} else {
						szLogMsg = "[JSP Session] " + szOperationName + " 스케줄코드의 야드구분이 올바르지 않습니다";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
					}

					szLogMsg = "[JSP Session] " + szOperationName + "   - 야드구분[" + szYdGp + "]";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

					szLogMsg = "[JSP Session] " + szOperationName + "   - 스케줄 진행상태 [" + szSendYdWrkProgStat +" ]";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

					szYD_EQP_ID = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_EQP_ID");

					szLogMsg = "[JSP Session] " + szOperationName +  "- 야드구분[" + szYdGp + "], 크레인 설비[" + szYD_EQP_ID + "]";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

					// 크레인 작업지시 전송
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID",           	"YDY7L004");
					recInTemp.setField("YD_EQP_ID",        	szYD_EQP_ID);
					recInTemp.setField("YD_EQP_WRK_MODE",  	"1");				// 설비작업모드 : 1-Online Mode , 0-Offline Mode
					recInTemp.setField("YD_WRK_PROG_STAT", 	"W");
					recInTemp.setField("YD_SCH_CD",        	szYdSchCd);
					recInTemp.setField("YD_CRN_SCH_ID",    	szYdSchId);
					recInTemp.setField("YD_CRN_XAXIS",     	"");
					recInTemp.setField("YD_CRN_YAXIS",     	"");

					//크레인작업지시 송신
					szRtnMsg = ydDelegate.sendMsg(recInTemp);

					szLogMsg = "[JSP Session] " + szOperationName + " 작업재지시  - 리턴메세지 : [" + szRtnMsg +"] ";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				//}

        		//------------------------------------------------------------------------
        		// 본래 권하위치가 차량 또는 대차에서 일반야드로 권하위치를 변경하는경우
        		//------------------------------------------------------------------------
        		// szOldStkPos 기존 권하위치
        		if (szOldStkPos.length() >= 6) {
        			szEqpGpBefo = ydUtils.substr(szOldStkPos, 2, 2);
        		}

        		// 기존 설비구분이 차량/대차이고 , 신규 설비 구분이 그 차량/대차 작업이 아닌경우
        		// 작업예약 ID를 Clear  한다.
        		if (JPlateYdConst.YD_EQP_GP_TCAR.equals(szEqpGpBefo)) {

        			if (!szEqpGpBefo.equals(szEqpGp)) {

        				//szYdWbookId - 현 스케줄의 작업예약 ID
        				//delWBookBefoCarOrTCar
        				recPara   = JDTORecordFactory.getInstance().create();
        				recPara.setField("YD_WBOOK_ID", szYdWbookId);
        				recPara.setField("YD_EQP_GP", 	szEqpGpBefo);
        				ydDaoUtils.delWBookBefoCarOrTCar(recPara);
        			}
        		}
			}

		} catch(Exception e) {

			szRtnMsg = "권하위치 변경 처리 오류발생<br>" + e.getMessage();

			szLogMsg = "JSP-SESSION [권하위치 변경 (크레인작업관리 화면)] Exception 발생 >>>>" + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			throw new DAOException(szRtnMsg);
		}

		rtnBool = true;

		szLogMsg = "JSP-SESSION [권하위치 변경 (크레인작업관리 화면)] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

	//	return new Boolean(rtnBool);
		return rtnBool;
	}	// end of updToPosFix


	/**
	 *  2후판정정 스케줄 기동관리 (조회)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getJPlateYdSchStartMgt(JDTORecord inDto) throws DAOException {

		//Log Message 용
		String szMsg			= "";
		String szEdit 			= null;
		String szTemp 			= null;
		String szMethodName		= "getJPlateYdSchStartMgt";

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecord    recEdit   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet retRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		JPlateYdSchRuleDAO ydSchRuleDao = new JPlateYdSchRuleDAO();

		int intRtnVal = 0;

		try {

			//적치열 구분과 적치베드 번호로 분리
			recPara.setField("YD_GP",     	inDto.getField("YD_GP"));
			recPara.setField("YD_SCH_CD", 	inDto.getField("YD_SCH_CD"));
			recPara.setField("YD_BAY_GP", 	inDto.getField("YD_BAY_GP"));

			recPara.setField("PAGE_NO", 	inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_SIZE",  	inDto.getField("ROWCOUNT"));

			intRtnVal = ydSchRuleDao.getJPlateYdSchStartMgt(recPara, outRecSet);		// intGp == 1

			if (intRtnVal <= 0) {
				szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return outRecSet;
			} 	// end of if

			outRecSet.first();

			do {
				//스케줄 코드를  설비구분을 분리하여 YD_EQP_GP 컬럼에 넣어준다.
				//스케줄 코드가  NULL일경우 에러가 발생하므로  SIZE체크및 NULL 체크 필요
				recEdit = outRecSet.getRecord();
				szTemp  = recEdit.getFieldString("YD_SCH_CD");

				if (szTemp == null) {
					continue;
				} else if (szTemp.length() <6) {
					continue;
				}

				szEdit = ydUtils.substr(recEdit.getFieldString("YD_SCH_CD").trim(), 4, 2);
				recEdit.setField("YD_EQP_GP", szEdit);

				retRecSet.addRecord(recEdit);

			} while(outRecSet.next());

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		return retRecSet;
	}	//end of getJPlateYdSchStartMgt

	/**
	 * 2후판정정 스케줄 기동관리 (수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updJPlateYdSchStartMgt(JDTORecord[] inDto) throws DAOException {

		int    intRtnVal 		= 0;
		String szMsg			= "";
		String szMethodName 	= "updJPlateYdSchStartMgt";
		JDTORecord	recPara		= JDTORecordFactory.getInstance().create();

		JPlateYdSchRuleDAO ydSchRuleDao = new JPlateYdSchRuleDAO();

		try {

			// 스케줄 기동관리 화면 스케줄기준 수정
			for (int ii=0; ii<inDto.length; ii++) {

				//스케줄 코드
				recPara.setField("YD_SCH_CD",           ydDaoUtils.paraRecChkNull(inDto[ii], "YD_SCH_CD"));

				//작업크레인
				recPara.setField("YD_WRK_CRN",          ydDaoUtils.paraRecChkNull(inDto[ii], "YD_WRK_CRN"));

				//작업크레인 우선순위
				recPara.setField("YD_WRK_CRN_PRIOR",    ydDaoUtils.paraRecChkNull(inDto[ii], "YD_WRK_CRN_PRIOR"));

				//대체 크레인
				recPara.setField("YD_ALT_CRN",          ydDaoUtils.paraRecChkNull(inDto[ii], "YD_ALT_CRN"));

				//대체 크레인 우선순위
				recPara.setField("YD_ALT_CRN_PRIOR",    ydDaoUtils.paraRecChkNull(inDto[ii], "YD_ALT_CRN_PRIOR"));

				//기준활성상태
			//  recPara.setField("YD_SCH_RULE_ACT_STAT",ydDaoUtils.paraRecChkNull(inDto[ii], "YD_SCH_RULE_ACT_STAT"));

				//금지/해제
				recPara.setField("YD_SCH_PROH_EXN",		ydDaoUtils.paraRecChkNull(inDto[ii], "YD_SCH_PROH_EXN", "N"));

				//수정
				recPara.setField("MODIFIER",			ydDaoUtils.paraRecChkNull(inDto[ii], "YD_USER_ID"));

				intRtnVal = ydSchRuleDao.updYdSchrule(recPara);		// intGp == 0

				if (intRtnVal < 0) {
					szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				} else if  (intRtnVal == 0) {
					szMsg = "[JSP Session : "+szMethodName+"] 데이터 없음 ";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				} 	// end of if
			}

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}	// end of updJPlateYdSchStartMgt


	/**
	 *  크레인별 배차기준조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCarAsgnStdByCrn(JDTORecord inDto) throws DAOException {
		/*
		 * 업무기준 : 1. 야드구분으로 설비테이블로부터 배차기준정보를 조회하여 반환
		 */
		JDTORecord     recPara    	= null;
		JDTORecordSet  outRecSet	= null;
		JPlateYdEqpDAO ydEqpDao		= new JPlateYdEqpDAO();

		String szOperationName	= "크레인별 배차기준조회";
		String szMsg      		= "";
		String szMethodName 	= "getCarAsgnStdByCrn";
		String szYdGp			= null;
		int    intRtnVal 		= 0;

		try {

			szMsg = "JSP-SESSION ["+szOperationName+"] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szYdGp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");

			szMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작  - 야드구분 : " + szYdGp;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
		    recPara.setField("YD_GP",     szYdGp);
			intRtnVal = ydEqpDao.getCarAsgnStdByCrn(recPara, outRecSet);		// intGp == 9

			if (intRtnVal < 0) {
				szMsg = "["+szOperationName+"] 조회 시 오류발생[1] - 반환값 : " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			} else if  (intRtnVal == 0) {
				szMsg = "[JSP Session : "+szMethodName+"] 데이터 없음 ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} // end of if

			szMsg = "[Jsp Session : "+szOperationName+"] 메소드 끝 - 조회 건수 : " + outRecSet.size();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			szMsg = "["+szOperationName+"] 예외발생 - 메세지 : " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return outRecSet;
	}//end of getCarAsgnStdByCrn

	/**
	 * [2후판정정야드] 크레인상태설정팝업 : 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdCrnStsSetById(JDTORecord inDto) throws DAOException {

		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		String szMsg        	= "";
		String szMethodName 	= "getYdCrnStsSetById";
		String szOperationName	= "크레인 상태설정 팝업 조회";
		int intRtnVal = 0;

		JPlateYdEqpDAO ydEqpDao = new JPlateYdEqpDAO();

		try {
			szMsg = "JSP-SESSION ["+szOperationName+"]시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			intRtnVal = ydEqpDao.getYdCrnStsSetById(inDto, outRecSet);

			if (intRtnVal < 0) {
				szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			} else if  (intRtnVal == 0) {
				szMsg = "[JSP Session : "+szMethodName+"] 데이터 없음 ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return outRecSet;
	}//end of getYdCrnStsSetById

	/**
	 * [2후판정정야드] 크레인상태설정팝업 : BED정보조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdStkbed(JDTORecord inDto) throws DAOException {

		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		String 	szMsg        	= "";
		String 	szMethodName 	= "getYdStkbed";
		String 	szOperationName	= "BED정보 조회";
		int intRtnVal 			= 0;

		String	szOffSch		= "";				// 1:강제권상 , 2:강제권하

		JPlateYdStkBedDAO ydStkBedDao = new JPlateYdStkBedDAO();
		JPlateYdStkLyrDAO ydStkLyrDao = new JPlateYdStkLyrDAO();

		try {
			szMsg = "JSP-SESSION ["+szOperationName+"]시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szOffSch  = ydDaoUtils.paraRecChkNull(inDto, "OFF_SCH");

			if ("1".equals(szOffSch)) {			// 강제권상
				intRtnVal = ydStkLyrDao.getOffCrnUpWr(inDto, outRecSet);
			} else {							// 강제권하
				intRtnVal = ydStkBedDao.getYdStkbed(inDto, outRecSet);
			}

			if (intRtnVal < 0) {
				szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			} else if  (intRtnVal == 0) {
				szMsg = "[JSP Session : "+szMethodName+"] 데이터 없음 ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return outRecSet;
	}//end of getYdStkbed

	/**
	 * 재료상세정보 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdStrlocIdInfojl(JDTORecord inDto) throws DAOException {

		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet	= JDTORecordFactory.getInstance().createRecordSet("YD");
	    JPlateYdStockDAO ydStockDao = new JPlateYdStockDAO();

		String szMsg 			= "";
		String szMethodName 	= "getYdStrlocIdInfojl";
		String szOperationName 	= "재료 상세정보 조회";
		int intRtnVal 			= 0;

		try {

			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "JSP-SESSION ["+ szOperationName +"] 재료번호 [:" + ydDaoUtils.paraRecChkNull(inDto, "V_STL_NO") +"]";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		    recPara.setField("STL_NO",	ydDaoUtils.paraRecChkNull(inDto, "V_STL_NO"));

		    // 정정야드재료 + 조업공통 정보 조회
		    intRtnVal = ydStockDao.getYdStockWithPRInfo(recPara, outRecSet);		// intGp == 132 , 162

		    szMsg = "JSP-SESSION ["+ szOperationName +"] 조회건수  [:" + Integer.toString(intRtnVal) ;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				}
				return outRecSet;
			} // end of if

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return outRecSet;
	}//end of getYdStrlocIdInfojl

	/**
	 * 재료상세정보_재료이력 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdWrkHistByStlNo(JDTORecord inDto) throws DAOException {

	    JDTORecord      	recPara     	= JDTORecordFactory.getInstance().create();
		JDTORecordSet		outRecSet		= JDTORecordFactory.getInstance().createRecordSet("retTmp");
    	JPlateYdWrkHistDAO 	ydWrkHistDao	= new JPlateYdWrkHistDAO();

		String szMsg        	= "";
    	String szMethodName		= "getYdWrkHistByStlNo";
		String szOperationName	= "재료상세정보_재료이력 조회";
		int intRtnVal 			= 0;

		try {

			szMsg = "JSP-SESSION ["+szOperationName+"] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			recPara.setField("STL_NO",	inDto.getField("V_STL_NO"));

			intRtnVal = ydWrkHistDao.getYdWrkHistByStlNo(recPara, outRecSet);		// intGp == 9

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				}
				return outRecSet;
			} // end of if
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return outRecSet;
	}// end of getYdWrkHistByStlNo


	/**
	 * 작업예약관리_작업예약 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdWrkBookListjm(JDTORecord inDto) throws DAOException {

		JDTORecordSet	outRecSet	= JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord 		recPara		= JDTORecordFactory.getInstance().create();

		String szMsg        	= "";
		String szMethodName		= "getYdWrkBookListjm";
		String szOperationName	= "작업예약관리_작업예약 조회";
		int intRtnVal 			= 0;

		JPlateYdWrkbookDAO 	ydWrkbookDao	= new JPlateYdWrkbookDAO();

		try {

			szMsg = "JSP-SESSION ["+szOperationName+"] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		    recPara.setField("YD_GP",        	ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
		    recPara.setField("YD_BAY_GP",		ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP"));
		    recPara.setField("YD_SCH_CD",    	ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_CD"));
		    recPara.setField("STL_NO",    		ydDaoUtils.paraRecChkNull(inDto, "STL_NO"));
			recPara.setField("PAGE_CNT",        inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT",         inDto.getField("ROWCOUNT"));

			intRtnVal = ydWrkbookDao.getYdWrkBookListjm(recPara, outRecSet);			// intGp == 26

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				}
				return outRecSet;
			} // end of if
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return outRecSet;
	}// end of getYdWrkBookListjm

	/**
	 * 작업예약관리_작업재료 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdWrkBookListjmDtl(JDTORecord inDto) throws DAOException {

		JDTORecordSet			outRecSet		= JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JPlateYdWrkbookMtlDAO 	ydWrkbookmtlDao	= new JPlateYdWrkbookMtlDAO();

		String szMsg        	= "";
		String szMethodName		= "getYdWrkBookListjmDtl";
		String szOperationName	= "작업예약관리_작업재료 조회";
		int intRtnVal 			= 0;

		try {

			szMsg = "JSP-SESSION ["+szOperationName+"] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			intRtnVal = ydWrkbookmtlDao.getYdWrkBookListjmDtl(inDto, outRecSet);		// intGp == 29

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				}
				return outRecSet;
			} // end of if
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return outRecSet;
	}// end of getYdWrkBookListjmDtl

	/**
	 * 후판제품 스케줄점검
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String chkCrnSchRunnable(JDTORecord[] inDto) throws DAOException {

		String 	szRtnMsg 			= "";
		String	szMsg        		= "";
		String	szMethodName 		= "chkCrnSchRunnable";
		String 	szOperationName		= "2후판정정 스케줄점검";

		try {
			szMsg = "[Jsp Session : "+szOperationName+"] ------------------- 메소드 시작 -------------------";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[Jsp Session : "+szOperationName+"] 스케줄점검 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			ydUtils.displayRecord(szOperationName, inDto[0]);

			szRtnMsg = JPlateYdCrnSchUtil.procCheckIfCrnSchRunnableForPlateGds(inDto[0]);

			szMsg = "[Jsp Session : "+szOperationName+"] 스케줄점검 완료 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "[Jsp Session : "+szOperationName+"] ------------------- 메소드 끝 -------------------";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		return szRtnMsg;
	}	// end of chkCrnSchRunnable

	/**
	 * RT모니터링_BOOK-OUT대상재료 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdStockBookOut(JDTORecord inDto) throws DAOException {

		JDTORecordSet		outRecSet	= JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JPlateYdStockDAO 	ydStockDAO	= new JPlateYdStockDAO();

		String szMsg        	= "";
		String szMethodName		= "getYdStockBookOut";
		String szOperationName	= "RT모니터링_BOOK-OUT대상재료 조회";
		int intRtnVal 			= 0;

		try {

			szMsg = "JSP-SESSION ["+szOperationName+"] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			intRtnVal = ydStockDAO.getYdStockBookOut(inDto, outRecSet);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				}
				return outRecSet;
			} // end of if
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return outRecSet;
	}// end of getYdStockBookOut

	/**
	 * [2후판정정야드] 저장위치수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String updYdLocInfo(JDTORecord inDto) throws DAOException {

		JPlateYdStockDAO 	ydStockDao		= new JPlateYdStockDAO();
		JPlateYdStkBedDAO	ydStkBedDao		= new JPlateYdStkBedDAO();
		JPlateYdStkLyrDAO	ydStkLyrDao		= new JPlateYdStkLyrDAO();

		JPlateYdDelegate 	ydDelegate 		= new JPlateYdDelegate();

		String	szRtnMsg		= "";
		String 	szMsg        	= "";
		String 	szMethodName	= "updYdLocInfo";
		String 	szOperationName	= "저장위치수정";

		String	szMtlIns		= "";		// 재료번호 Insert 구분
		String	szLocIns		= "";		// 끼워넣기 구분
		String 	szStlNo			= "";		// 재료번호
		String	szModifier		= "";		// 등록자
		String	szYdStkColGp 	= "";		// 열구분
		String	szYdStkBedNo 	= "";		// 베드번호
		String	szYdStkLyrNo	= "";		// 적치단
		String	szSendFlag		= "";
		String	szDelFlag		= "";
		String	szYdSchStGp		= "";
		String	szYdSchCd		= "";
		String	szYdGp			= "";

		int 	intRtnVal 		= 0;

		JDTORecordSet rsResult  = JDTORecordFactory.getInstance().createRecordSet("tempYd");
		JDTORecord inRec		= JDTORecordFactory.getInstance().create();
		JDTORecord recPara		= null;
		JDTORecord recBed 		= null;
		JDTORecord recStock 	= null;
		JDTORecord recL2Para   	= null;

		try {

			szMsg = "JSP-SESSION ["+szOperationName+"] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			inRec.setRecord(inDto);

			szMtlIns 	 	= ydDaoUtils.paraRecChkNull(inRec, "MTL_INS");				// 재료추가 FLAG
			szLocIns 	 	= ydDaoUtils.paraRecChkNull(inRec, "LOC_INS");				// 저장위치 끼워넣기 FLAG
			szStlNo	 	 	= ydDaoUtils.paraRecChkNull(inRec, "STL_NO");				// 재료번호
			szModifier	 	= ydDaoUtils.paraRecModifier(inRec);						// 등록자, 수정자
			szYdStkColGp 	= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP");		// TO위치 적치열구분
			szYdStkBedNo 	= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");		// TO위치 적치베드 구분
			szYdStkLyrNo	= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_LYR_NO");		// TO위치 적치단
			szDelFlag	 	= ydDaoUtils.paraRecChkNull(inRec, "DEL_FLAG", "N");		// 재료삭제 구분
			szSendFlag	 	= ydDaoUtils.paraRecChkNull(inRec, "SEND_FLAG", "Y");
			szYdSchStGp 	= ydDaoUtils.paraRecChkNull(inRec, "YD_SCH_ST_GP", "B");	// 야드스케줄 기동구분 (A:AUTO, M:MANUAL, B:BACK-UP)
			szYdSchCd 		= ydDaoUtils.paraRecChkNull(inRec, "YD_SCH_CD");			// 스케쥴코드
			szYdGp			= ydDaoUtils.paraRecChkNull(inRec, "YD_GP", JPlateYdConst.YD_GP_F_PLATE_YARD);			// 야드구분

			inRec.setField("MODIFIER",		szModifier);			// 수정자
			inRec.setField("YD_SCH_ST_GP",	szYdSchStGp);			// 야드스케줄 기동구분 (A:AUTO, M:MANUAL, B:BACK-UP)

			//------------------------------------------------------------------
			// 2013.07.30 현재 저장위치가 2후판 정정야드가 아닐경우 오류로 처리
			//------------------------------------------------------------------
			szRtnMsg = JPlateYdCommonUtils.checkUpdYdLoc(szStlNo, szYdGp, "N");
			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szMsg = "[ " +szOperationName + "] 저장위치 수정시 재공 확인 오류! >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			//---------------------------------------
			//  끼워넣기 일경우 기존 권상/권하예약 정보 존재 체크
			//---------------------------------------
			if ("Y".equals(szLocIns)) {
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("tempYd");
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP",		szYdStkColGp);
				recPara.setField("YD_STK_LYR_NO",		"999");

				intRtnVal = ydStkLyrDao.getUpStatByLyrNo(recPara, rsResult);
				if (intRtnVal > 0) {
					szRtnMsg = "끼워넣기로 저장위치 수정시 TO위치에 권상/권하예약 정보가 존재하여 수정이 불가합니다.";
					szMsg    = "[ " +szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
			}

			// 재료관련 정보로 이력정보를 추가적으로 넣을부분이 존재하면 재료정보를 조회한 후 이부분에 추가해줌
			rsResult = JDTORecordFactory.getInstance().createRecordSet("rsTemp");
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO",		szStlNo);			// 재료번호

			intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsResult);

			// 재료 정보 존재여부 체크
			if (intRtnVal < 1) {
				szMtlIns = "Y";
			}

			if ("".equals(szYdStkColGp)) {
				szDelFlag = "Y";

				recBed = JDTORecordFactory.getInstance().create();
				rsResult.first();
				recBed = rsResult.getRecord();
				inRec.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recBed, "YD_STK_COL_GP"));
				inRec.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(recBed, "YD_STK_BED_NO"));
				inRec.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(recBed, "YD_STK_LYR_NO"));
			}
			inRec.setField("DEL_FLAG", szDelFlag);		// 재료삭제 구분

			//----------------------------------------------------------
			// 1. TB_YD_SHRSTOCK에 INSERT 처리 [YD에 미존재하는 재료번호만 대상]
			//----------------------------------------------------------
			if ("Y".equals(szMtlIns)) {
				// 기존 TB_YD_SHRSTOCK에 미존재시 등록처리함
				recStock = JDTORecordFactory.getInstance().create();
				recStock.setField("REGISTER",	szModifier);			// 등록자
				recStock.setField("MODIFIER",	szModifier);			// 수정자
				recStock.setField("STL_NO", 	szStlNo);             	// 재료번호

    			intRtnVal = ydStockDao.insYdStockBookOut(recStock);
				if (intRtnVal <= 0) {
					szMsg = "[ " +szOperationName + "] 야드재료 등록 ERROR .. " + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szMsg;
				}
			}

			//-------------------------------------
			// 2. 저장위치 수정 가능여부 체크 [TO저장위치]
			//-------------------------------------
			szMsg = "[ " +szOperationName + "] 저장위치 수정 가능여부 체크 .. 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = this.chkYdLocMod(inRec);
			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szMsg)) {
				return szMsg;
			}

			szMsg = "[ " +szOperationName + "] 저장위치 수정 가능여부 체크 .. 종료 [" + szMsg + "]";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//---------------------------------------
			// 3. 끼워넣기 일경우 기존 저장위치 1단씩 이동처리
			//---------------------------------------
			if ("Y".equals(szLocIns)) {
				szMsg = "[ " +szOperationName + "] 저장위치 수정 끼워넣기 .. 시작";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				// 3.1. 끼워넣기 대상재 조회
				rsResult = JDTORecordFactory.getInstance().createRecordSet("rsTemp");
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP", 	szYdStkColGp);
				recPara.setField("YD_STK_LYR_NO",	szYdStkLyrNo);
				recPara.setField("STL_NO",			szStlNo);

				intRtnVal = ydStkBedDao.getStkLocModWithIns(recPara, rsResult);
				if (intRtnVal > 0) {

					szMsg = "[ " +szOperationName + "] 저장위치 수정 끼워넣기 .. 대상 건수 [" + rsResult.size() + "]";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					String 	sYdStkColGp = "";
					String	sYdStkLyrNo = "";

					for (int ii=1; ii<=rsResult.size(); ii++) {
						recBed = JDTORecordFactory.getInstance().create();
						rsResult.absolute(ii);
						recBed = rsResult.getRecord();

						// 적치단이 틀려졌을때만 TO위치 CLEAR (단별로 혼적 베드때문에 지우고 다시 저장위치 SET함)
						if (!sYdStkLyrNo.equals(ydDaoUtils.paraRecChkNull(recBed, "YD_STK_LYR_NO"))) {

							sYdStkColGp = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_COL_GP");
							sYdStkLyrNo = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_LYR_NO");

							szMsg = "[" +szOperationName + "] TO 저장위치 초기화 시작 .... 저장위치 :: " + sYdStkColGp + "-" + sYdStkLyrNo;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

							recPara = JDTORecordFactory.getInstance().create();
							recPara.setField("YD_STK_COL_GP", 	ydDaoUtils.paraRecChkNull(recBed, "YD_STK_COL_GP"));
							recPara.setField("MODIFIER", 		szModifier);
							recPara.setField("YD_STK_LYR_NO",	ydDaoUtils.paraRecChkNull(recBed, "YD_STK_LYR_NO"));
							recPara.setField("OCPY_CHK_FLAG",	"N");
							intRtnVal = ydStkLyrDao.updYdStklyrClear(recPara);

							szMsg = "[ " +szOperationName + "] TO 저장위치 초기화 결과 .... 건수  :: " + Integer.toString(intRtnVal);
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						}

						recBed.setField("MODIFIER",				szModifier);		// 수정자
						recBed.setField("YD_SCH_ST_GP",			szYdSchStGp);		// 야드스케쥴 기동 구분 (A:AUTO, M:MANUAL, B:BACK-UP)
						recBed.setField("YD_SCH_CD",			szYdSchCd);			// 스케줄코드

						// 3.2. 후판정정야드 저장위치 수정 처리
						szMsg = this.updJPlateYdStkPosFix(recBed, "N");				// 단 변경시에는 후판조업에 송신 안함
						if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szMsg)) {
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szMsg;
						}
					}
				}

				szMsg = "[ " +szOperationName + "] 저장위치 수정 끼워넣기 .. 종료 .. 건수 [" + Integer.toString(intRtnVal) + "]";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

			//-----------------------------
			// 4. 2후판정정야드 저장위치 수정 처리
			//-----------------------------
			szMsg = this.updJPlateYdStkPosFix(inRec, szSendFlag);
			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szMsg)) {
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szMsg;
			}

			//------------------------------------------------------------
			// 5. 야드L2 전문송신 (저장위치변경이력 일괄 전송)
			// --> 저장위치제원은 안바뀌고 저장품제원이 변경됨으로 YDY7L002로 변경
			// 후판조업 저장위치변경정보 전송 (YDPPJ011),
			// 2 후판전단L2 RT BOOK-IN 실적 전송 (YDS1L005)
			// --> updJPlateYdStkPosFix 메서드 에서 실시 함으로 SKIP
			//------------------------------------------------------------
			if ("Y".equals(szSendFlag)) {
				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	recL2Para = JDTORecordFactory.getInstance().create();
	        	recL2Para.setField("JMS_TC_CD", 		"YDY7L002");                            // TC-CODE
	        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);		// 야드구분
	        	recL2Para.setField("YD_STK_COL_GP", 	szYdStkColGp);                          // 야드적치열구분
	        	recL2Para.setField("YD_STK_BED_NO", 	szYdStkBedNo);    						// 야드적치BED번호
	        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
	        	recL2Para.setField("STL_NO", 			szStlNo);	        					// 재료번호
	        	// BOOK-IN(TO위치가RT) 일때 저장품제원 삭제
	        	if ("".equals(szYdStkColGp) || "RT".equals(ydUtils.substr(szYdStkColGp, 2, 2))) {
	        		recL2Para.setField("MSG_GP", 		"D");	        						// 전문구분
	        	}

	        	szRtnMsg = ydDelegate.sendMsg(recL2Para);

				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			} else {
				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 SKIP ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}// end of updYdLocInfo

	/**
	 * [2후판정정야드] 저장위치삭제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String delYdLocInfo(JDTORecord inDto) throws DAOException {

		JPlateYdStockDAO 	ydStockDao		= new JPlateYdStockDAO();

		JPlateYdDelegate 	ydDelegate 		= new JPlateYdDelegate();

		String	szRtnMsg		= "";
		String 	szMsg        	= "";
		String 	szMethodName	= "delYdLocInfo";
		String 	szOperationName	= "저장위치삭제";

		String 	szStlNo			= "";		// 재료번호
		String	szYdStkColGp 	= "";
		String	szYdStkBedNo 	= "";
		String	szYdStkLyrNo 	= "";
		String	szModifier		= "";		// 등록자
		String	szSendFlag		= "Y";
		String	szDelFlag		= "";

		int 	intRtnVal 		= 0;

		JDTORecordSet rsResult  = JDTORecordFactory.getInstance().createRecordSet("tempYd");
		JDTORecord inRec		= JDTORecordFactory.getInstance().create();
		JDTORecord recPara		= null;
		JDTORecord recBed 		= null;
		JDTORecord recL2Para   	= null;

		try {

			szMsg = "JSP-SESSION ["+szOperationName+"] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			inRec.setRecord(inDto);

			szStlNo	 	 = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");				// 재료번호
			szModifier	 = ydDaoUtils.paraRecModifier(inRec);						// 등록자, 수정자
			szDelFlag	 = "Y";														// 재료삭제 구분
			inRec.setField("MODIFIER",	szModifier);								// 수정자

			//------------------------------------------------------------------
			// 2013.07.30 현재 저장위치가 2후판 정정야드가 아닐경우 오류로 처리
			//------------------------------------------------------------------
			szRtnMsg = JPlateYdCommonUtils.checkUpdYdLoc(szStlNo, JPlateYdConst.YD_GP_F_PLATE_YARD, "Y");
			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szMsg = "[ " +szOperationName + "] 저장위치 수정시 재공 확인 오류! >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			//-----------------------------
			// 1. 2후판정정야드 재료정보 조회
			//-----------------------------
			// 재료관련 정보로 이력정보를 추가적으로 넣을부분이 존재하면 재료정보를 조회한 후 이부분에 추가해줌
			rsResult = JDTORecordFactory.getInstance().createRecordSet("rsTemp");
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO",		szStlNo);			// 재료번호

			intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsResult);
			if (intRtnVal <= 0) {
				szRtnMsg = "재료번호 [" + szStlNo + "] 미존재하여 저장위치 Clear오류 발생";
				szMsg = "[ " +szOperationName + "] 재료정보 조회 오류! >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}
			recBed = JDTORecordFactory.getInstance().create();
			rsResult.first();
			recBed = rsResult.getRecord();
			szYdStkColGp = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_COL_GP");
			szYdStkBedNo = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_BED_NO");
			szYdStkLyrNo = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_LYR_NO");

			//-----------------------------
			// 3. 2후판정정야드 저장위치 삭제 처리
			//-----------------------------
			if ("".equals(szYdStkColGp)) {
				szMsg = "[ " +szOperationName + "] 저장위치가 없을 경우에는 저장위치 삭제처리 .... SKIP";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			} else {
				recBed = JDTORecordFactory.getInstance().create();
				rsResult.first();
				recBed = rsResult.getRecord();
				inRec.setField("YD_STK_COL_GP", szYdStkColGp);
				inRec.setField("YD_STK_BED_NO", szYdStkBedNo);
				inRec.setField("YD_STK_LYR_NO", szYdStkLyrNo);
				inRec.setField("DEL_FLAG", 		szDelFlag);

				szMsg = this.updJPlateYdStkPosFix(inRec, szSendFlag);
				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szMsg)) {
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szMsg;
				}
			}

			//-----------------------------
			// 4. 2후판정정야드 재료정보 삭제 처리
			//-----------------------------
			intRtnVal = ydStockDao.delYdStock(inRec);
			if (intRtnVal <= 0) {
				szRtnMsg = "재료번호 [" + szStlNo + "] 삭제시 오류 발생 >>>> " + Integer.toString(intRtnVal);
				szMsg = "[ " +szOperationName + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			//------------------------------------------------------------
			// 5. 야드L2 전문송신 (저장위치변경이력 일괄 전송)
			// --> 저장위치제원은 안바뀌고 저장품제원이 변경됨으로 YDY7L002로 변경
			// 후판조업 저장위치변경정보 전송 (YDPPJ011),
			// 2 후판전단L2 RT BOOK-IN 실적 전송 (YDS1L005)
			// --> updJPlateYdStkPosFix 메서드 에서 실시 함으로 SKIP
			//------------------------------------------------------------
			if ("Y".equals(szSendFlag)) {
				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	recL2Para = JDTORecordFactory.getInstance().create();
	        	recL2Para.setField("JMS_TC_CD", 		"YDY7L002");                            // TC-CODE : 저장품제원정보
	        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);		// 야드구분
	        	recL2Para.setField("YD_STK_COL_GP", 	szYdStkColGp);                          // 야드적치열구분
	        	recL2Para.setField("YD_STK_BED_NO", 	szYdStkBedNo);    						// 야드적치BED번호
	        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
	        	recL2Para.setField("STL_NO", 			szStlNo);	        					// 재료번호
	        	recL2Para.setField("MSG_GP", 			"D");	        						// 전문구분

	        	szRtnMsg = ydDelegate.sendMsg(recL2Para);

				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			} else {
				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 SKIP ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}// end of delYdLocInfo

	/**
	 * [2후판정정야드] 저장위치수정 체크
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String chkYdLocMod(JDTORecord inDto) throws DAOException {

		JPlateYdStkBedDAO 	ydStkBedDao	= new JPlateYdStkBedDAO();
		JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();

		String 	szMsg        	= "";
		String	szRtnStr		= "";
		String 	szMethodName	= "chkYdLocMod";
		String 	szOperationName	= "저장위치수정 체크";

		String	szLocIns		= "";		// 끼워넣기 구분
		String 	szStlNo			= "";		// 재료번호
		String	szYdStkColGp	= "";
		String	szDelFlag		= "";		// 재료삭제 구분

		int intRtnVal 			= 0;

		JDTORecordSet rsResult  = JDTORecordFactory.getInstance().createRecordSet("tempYd");
		JDTORecord recPara		= null;
		JDTORecord recBed 		= null;

		try {

			szMsg = "JSP-SESSION ["+szOperationName+"] ----- 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szLocIns 	 = ydDaoUtils.paraRecChkNull(inDto, "LOC_INS");
			szStlNo	 	 = ydDaoUtils.paraRecChkNull(inDto, "STL_NO");
			szYdStkColGp = ydDaoUtils.paraRecChkNull(inDto, "YD_STK_COL_GP");
			szDelFlag	 = ydDaoUtils.paraRecChkNull(inDto, "DEL_FLAG", "N");		// 재료삭제 구분

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setRecord(inDto);

			//---------------------------------------
			// 0. [TO저장위치가 Empty일때:삭제일때]
			//	  - 기존 저장위치를 조회하여 체크
			//---------------------------------------
			if ("".equals(szYdStkColGp)) {

				rsResult = JDTORecordFactory.getInstance().createRecordSet("tempYd");
				recBed   = JDTORecordFactory.getInstance().create();

				recPara.setField("YD_STK_LYR_MTL_STAT", "C");
				intRtnVal = ydStkLyrDao.getYdStklyrByStlNoStat(recPara, rsResult);

				if (intRtnVal <= 0) {
					szRtnStr = "TO위치정보(OLD) 조회 ERROR >>>> " + intRtnVal;
					szMsg    = "[ " +szOperationName + "] " + szRtnStr;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnStr;
				}
				rsResult.first();
				recBed = rsResult.getRecord();

				recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recBed, "YD_STK_COL_GP"));
				recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(recBed, "YD_STK_BED_NO"));
				recPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(recBed, "YD_STK_LYR_NO"));
			}

			//---------------------------------------
			// 1. 저장위치 수정 가능여부 체크 [TO저장위치]
			//---------------------------------------
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("tempYd");
			recBed    = JDTORecordFactory.getInstance().create();
			intRtnVal = ydStkBedDao.getStkLocModChk(recPara, rsResult);

			if (intRtnVal <= 0) {
				szRtnStr = "TO위치정보 조회 ERROR >>>> " + intRtnVal;
				szMsg    = "[ " +szOperationName + "] " + szRtnStr;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnStr;
			}

			rsResult.first();
			recBed = rsResult.getRecord();

			// 1.1. BED활성상태 체크 - L:적치 가능
			if (!"L".equals(ydDaoUtils.paraRecChkNull(recBed, "YD_STK_BED_ACT_STAT"))) {
				szRtnStr = "BED활성상태가 적치가능이 아닙니다. >>>> " + ydDaoUtils.paraRecChkNull(recBed, "YD_STK_BED_ACT_STAT");
				szMsg    = "[ " +szOperationName + "] " + szRtnStr;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnStr;
			}

			String sYdStkLyrActStat = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_LYR_ACT_STAT");
			String sYdStkLyrMtlStat = ydDaoUtils.paraRecChkNull(recBed, "YD_STK_LYR_MTL_STAT");
			String sOldStlNo		= ydDaoUtils.paraRecChkNull(recBed, "STL_NO");

			if ("Y".equals(szLocIns)) {		// 끼워넣기일때

				// 1.2. 적치단활성상태 - E:적치 가능 , F:적치 완료
				if (!"E".equals(sYdStkLyrActStat) && !"F".equals(sYdStkLyrActStat)) {
					szRtnStr = "적치단 활성상태가 적치가능이 아닙니다. (끼워넣기)" + sYdStkLyrActStat;
					szMsg    = "[ " +szOperationName + "] " + szRtnStr;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnStr;
				}

				// 1.3. 야드적치단재료상태 - C:적치 중,D:권하대기,E:적치가능,	U:권상대기,X:적치불가
				if (!"E".equals(sYdStkLyrMtlStat) && !"C".equals(sYdStkLyrMtlStat)) {
					szRtnStr = "야드적치단재료 상태가 적치가능이 아닙니다. (끼워넣기)" + sYdStkLyrMtlStat;
					szMsg    = "[ " +szOperationName + "] " + szRtnStr;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnStr;
				}

			} else {

				// 1.4. 적치단활성상태 - E:적치 가능 , F:적치 완료
				if (!"E".equals(sYdStkLyrActStat)) {
					szRtnStr = "적치단 활성상태가 적치가능이 아닙니다." + sYdStkLyrActStat;
					szMsg    = "[ " +szOperationName + "] " + szRtnStr;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnStr;
				}

				// 1.5. 야드적치단재료상태 - C:적치 중,D:권하대기,E:적치가능,	U:권상대기,X:적치불가
				if ("Y".equals(szDelFlag)) {
					if (!"C".equals(sYdStkLyrMtlStat) || !sOldStlNo.equals(szStlNo)) {
						szRtnStr = "야드적치단재료 상태가 적치중이 아닙니다. 재료번호::" + szStlNo + ", 상태::" + sYdStkLyrMtlStat + ", 적치재료::" + sOldStlNo;
						szMsg    = "[ " +szOperationName + "] " + szRtnStr;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnStr;
					}
				} else {
					if (!"E".equals(sYdStkLyrMtlStat)) {
						szRtnStr = "야드적치단재료 상태가 적치가능이 아닙니다." + sYdStkLyrMtlStat;
						szMsg    = "[ " +szOperationName + "] " + szRtnStr;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnStr;
					}
				}
			}

			// 1.6. 최대적치 단을 초과 체크
			int iMaxYdStkLyrNo	=	ydDaoUtils.paraRecChkNullInt(recBed, "MAX_YD_STK_LYR_NO");		// 적치가능 적치단
			int iYdStkBedLyrMax	=	ydDaoUtils.paraRecChkNullInt(recBed, "YD_STK_BED_LYR_MAX");	// 야드적체베드단 Max
			if (iMaxYdStkLyrNo > iYdStkBedLyrMax) {
				szRtnStr = "최대적치 단을 초과 하였습니다.";
				szMsg    = "[ " +szOperationName + "] " + szRtnStr;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnStr;
			}

			//---------------------------------------
			// 2. 저장품에 있는 재료인지 CHECK
			//---------------------------------------
			if ("Y".equals(ydDaoUtils.paraRecChkNull(recBed, "ERR_STOCK"))) {
				szRtnStr = "저장품에 데이터가 없습니다.";
				szMsg    = "[ " +szOperationName + "] " + szRtnStr;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnStr;
			}

			/* 2013-10-21 : 작업예약 재료도 저장위치 수정할수 있도록 주석처리
			//---------------------------------------
			// 3. 작업예약 재료확인
			//---------------------------------------
			if ("Y".equals(ydDaoUtils.paraRecChkNull(recBed, "ERR_WRKMTL"))) {
				szRtnStr = "해당재료 ["+szStlNo+"] 는 작업예약재료 입니다.";
				szMsg    = "[ " +szOperationName + "] " + szRtnStr;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnStr;
			}
			*/

			//---------------------------------------
			// 4. 스케쥴 재료확인
			//---------------------------------------
			if ("Y".equals(ydDaoUtils.paraRecChkNull(recBed, "ERR_CRNSCH"))) {
				szRtnStr = "해당재료 ["+szStlNo+"] 는 크레인작업재료 입니다.";
				szMsg    = "[ " +szOperationName + "] " + szRtnStr;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnStr;
			}

			//---------------------------------------
			// 5. TO위치에 권상예약 정보 존재여부 체크 (하단에 권상재료 존재 체크)
			//---------------------------------------
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("tempYd");
			intRtnVal = ydStkLyrDao.getUpStatByLyrNo(recPara, rsResult);
			if (intRtnVal > 0) {
				szRtnStr = "해당 저장위치 하단에 권상/권하예약 정보가 존재합니다.";
				szMsg    = "[ " +szOperationName + "] " + szRtnStr;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnStr;
			}

			szMsg = "[ " +szOperationName + "] 저장위치 수정 가능여부  성공!";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch (Exception e) {

			szMsg = "[ " +szOperationName + "] 저장위치 수정 Exception 발생 >>>>" + e.getLocalizedMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}// end of chkYdLocMod

	/**
	 * 후판정정야드 저장위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public String updJPlateYdStkPosFix(JDTORecord inDto, String pSendFlag) {

		int 	intRtnVal 			= 0;
		String	szRtnMsg			= "";
		String 	szMsg  				= "";
		String 	szMethodName		= "updJPlateYdStkPosFix";
		String 	szOperationName		= "저장위치 수정";
		String 	szStlNo 			= null;
		String 	szStkColGpFrom 		= null;
		String 	szStkBedNoFrom 		= null;
		String 	szStkLyrNoFrom 		= null;
		String 	szYdStkColGp 		= null;
		String 	szYdStkBedNo 		= null;
		String 	szYdStkLyrNo 		= null;
		String 	szModifier 			= null;

		String 	szYdUpWrLoc			= "";
		String	szYdUpWrLayer		= "";
		String 	szYdDnWrLoc			= "";
		String	szYdDnWrLayer		= "";
		String	szDelFlag			= "";
		String	szYdSchStGp			= "";
		String	szYdSchCd			= "";
		String	szYdStkLyrMtlStat	= "";
		String 	szRtnStr 			= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szYdGp 				= null;
		boolean bHistFlag 			= false;
		int 	iRtnVal				= 0;

		JDTORecordSet   	rsTemp  		= null;
		JDTORecordSet   	rsBefoLyrInfo  	= null;
		JDTORecordSet 		rsDelInfo 		= null;

		JDTORecord      	recPara			= null;
		JDTORecord 			recL2Para		= null;
		JDTORecord 			recL3Para		= null;
		JDTORecord 			recBefoLyrInfo  = null;
		JDTORecord			recHist			= null;

		JPlateYdDelegate 	ydDelegate 		= new JPlateYdDelegate();

		JPlateYdStkLyrDAO 	ydStkLyrDao 	= new JPlateYdStkLyrDAO();
		JPlateYdStockDAO 	ydStockDao 		= new JPlateYdStockDAO();

		try {
			szMsg   = "[Jsp Session : "+szOperationName+"] 메소드 시작 >>>> " + inDto.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setRecord(inDto);

			rsTemp  = JDTORecordFactory.getInstance().createRecordSet("JPlateYdTemp");

			szMsg   = "[Jsp Session : "+szOperationName+"] 적치단정보 조회";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsTemp);		// intGp == 0

			// 단정보가 존재 하지않거나  단데이터 생성이 되지않는경우
			if (intRtnVal  < 1) {
				szRtnStr = "적치단 정보가 존재하지 않습니다";
				szMsg    = "JSP-SESSION "+szOperationName+"] " + szRtnStr;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnStr;
			}

			// 1. 적치 단 정보 UPDATE
			szStlNo 	  		= ydDaoUtils.paraRecChkNull(inDto, "STL_NO");
			szYdStkColGp  		= ydDaoUtils.paraRecChkNull(inDto, "YD_STK_COL_GP");
			szYdStkBedNo  		= ydDaoUtils.paraRecChkNull(inDto, "YD_STK_BED_NO");
			szYdStkLyrNo  		= ydDaoUtils.paraRecChkNull(inDto, "YD_STK_LYR_NO");
			szYdSchStGp			= ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_ST_GP", "B");	// 야드스케줄 기동 구분 (A:AUTO, M:MANUAL, B:BACK-UP)
			szYdSchCd			= ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_CD");			// 야드스케줄 코드
			szDelFlag	 		= ydDaoUtils.paraRecChkNull(inDto, "DEL_FLAG", "N");		// 재료삭제 구분
			szModifier 			= ydDaoUtils.paraRecModifier(inDto);
			szYdUpWrLoc			= ydDaoUtils.paraRecChkNull(inDto, "OLD_YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(inDto, "OLD_YD_STK_BED_NO");
			szYdUpWrLayer		= ydDaoUtils.paraRecChkNull(inDto, "OLD_YD_STK_LYR_NO");
			szYdStkLyrMtlStat	= ydDaoUtils.paraRecChkNull(inDto, "YD_STK_LYR_MTL_STAT");
			if ("N".equals(szDelFlag)) {
				szYdDnWrLoc   	= szYdStkColGp + szYdStkBedNo;
				szYdDnWrLayer 	= szYdStkLyrNo;
			}

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", 			szStlNo);			// 재료번호
			recPara.setField("YD_STK_COL_GP", 	szYdStkColGp);		// 적치열구분
			recPara.setField("YD_STK_BED_NO", 	szYdStkBedNo);		// 적치베드
			recPara.setField("YD_STK_LYR_NO", 	szYdStkLyrNo);		// 적치단
			recPara.setField("YD_SCH_CD", 		"");
			recPara.setField("YD_WBOOK_ID", 	"");
			recPara.setField("MODIFIER", 		szModifier);

			szMsg = "재료번호 : ["+szStlNo+"] 권상위치 : ["+szYdUpWrLoc + "-" + szYdUpWrLayer + "], 권하위치 : ["+szYdDnWrLoc + "-" + szYdDnWrLayer + "] 적치상태 : [" + szYdStkLyrMtlStat + "]";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szYdGp = ydUtils.substr(szYdStkColGp, 0, 1);
			recPara.setField("YD_GP", 			szYdGp);			//야드구분

			if ("N".equals(szDelFlag)) {

				// 적치 상태 [재료번호가 존재 : "C" , 미존재 : "E"]
				if ("".equals(szStlNo)) {
					bHistFlag = false;
					recPara.setField("YD_STK_LYR_MTL_STAT", "E");
				} else {
					if ("".equals(szYdStkLyrMtlStat) || "C".equals(szYdStkLyrMtlStat)) {
						bHistFlag = true;
						recPara.setField("YD_STK_LYR_MTL_STAT", "C");
					} else {
						bHistFlag = false;
						recPara.setField("YD_STK_LYR_MTL_STAT", szYdStkLyrMtlStat);
					}
				}

				if (szYdUpWrLoc.equals(szYdDnWrLoc) && szYdUpWrLayer.equals(szYdDnWrLayer)) {
					bHistFlag = false;
				} else {

					// 해당 베드의 위치정보가 기존정보의 재료정보랑 같은 경우는 PASS한다.
					rsBefoLyrInfo = JDTORecordFactory.getInstance().createRecordSet("rsBefoLyrInfo");
					intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsBefoLyrInfo);		// ingGp == 0

					if (intRtnVal > 0) {

						rsBefoLyrInfo.first();
						recBefoLyrInfo = rsBefoLyrInfo.getRecord();

						if (ydDaoUtils.paraRecChkNull(recBefoLyrInfo, "STL_NO").equals(szStlNo)) {
							//기존재료 정보와 해당 정보는 같은 정보 이므로 UPDATE 하지 않는다.

							szMsg = "미 변경된 재료 정보는 UPDATE 하지 않습니다.";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

							bHistFlag = false;
						}
					}
				}
			} else {
				bHistFlag = true;
				recPara.setField("YD_STK_LYR_MTL_STAT", "E");
			}

			szMsg = "이력정보 생성 유무 Flag :: " + String.valueOf(bHistFlag);
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//---------------------------------------
			// 이적작업  이력정보에 추가
			//---------------------------------------
			// bHistFlag - 이력정보 생성 유무 Flag
			if (bHistFlag) {

				recHist = JDTORecordFactory.getInstance().create();
				recHist.setField("YD_GP", 			szYdGp);
				recHist.setField("STL_NO", 			szStlNo);
				recHist.setField("YD_DN_WR_LOC",	szYdDnWrLoc);
				recHist.setField("YD_DN_WR_LAYER",	szYdDnWrLayer);
				recHist.setField("YD_SCH_ST_GP", 	szYdSchStGp);
				recHist.setField("MODIFIER", 		szModifier);
				recHist.setField("YD_SCH_CD",		szYdSchCd);			// 야드스케줄 코드
				recHist.setField("YD_UP_WR_LOC",	szYdUpWrLoc);		// 권상위치 (적치열+베드)
				recHist.setField("YD_UP_WR_LAYER",	szYdUpWrLayer);		// 권상위치 (적치단)

				// 이력정보 남기기
				szRtnStr = this.insYdWrkHistPosFix(recHist);
				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnStr)) {
					szMsg = "JSP-SESSION "+szOperationName+"] " + szRtnStr;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnStr;
				}
			}

			// 해당 재료번호가 적치중일 경우 해당 적치 정보 삭제 기능 추가 ==============================================
			JDTORecord 	recDelPara 	= JDTORecordFactory.getInstance().create();
						rsDelInfo	= JDTORecordFactory.getInstance().createRecordSet("rsdelInfo");

			if (!"".equals(szStlNo.trim())) {

				recDelPara.setField("STL_NO", 				szStlNo);
				recDelPara.setField("YD_STK_LYR_MTL_STAT", 	"");

				iRtnVal  = ydStkLyrDao.getYdStklyrByStlNoStat(recDelPara, rsDelInfo);		// intGp == 3

				if (iRtnVal > 0) {

					//정보 존재시 해당 Map Clear
					rsDelInfo.first();

					do {
						recDelPara   = 	JDTORecordFactory.getInstance().create();
						recDelPara   =  rsDelInfo.getRecord();

						szStkColGpFrom = ydDaoUtils.paraRecChkNull(recDelPara, "YD_STK_COL_GP");
						szStkBedNoFrom = ydDaoUtils.paraRecChkNull(recDelPara, "YD_STK_BED_NO");
						szStkLyrNoFrom = ydDaoUtils.paraRecChkNull(recDelPara, "YD_STK_LYR_NO");

						szMsg = "기존 재료 위치 정보 : 열["+szStkColGpFrom+"], 베드["+szStkBedNoFrom+"], 단[" + szStkLyrNoFrom + "]";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						recDelPara.setField("STL_NO", 				"");
						recDelPara.setField("YD_STK_LYR_MTL_STAT", 	"E");

						intRtnVal = ydStkLyrDao.updYdStklyrStat(recDelPara);		// intGp == 0
						if (intRtnVal <= 0) {
							szRtnStr = "적치단 정보 CLEAR 실패 하였습니다" + Integer.toString(intRtnVal);
							szMsg      = "JSP-SESSION "+szOperationName+"] " + szRtnStr;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnStr;
						}

					} while(rsDelInfo.next());
				}
			}

			if ("N".equals(szDelFlag)) {
				// 적치단 정보 UPDATE
				intRtnVal = ydStkLyrDao.updYdStklyrStat(recPara);		// intGp == 0
				if (intRtnVal <= 0) {
					szRtnStr = "적치단 정보 UPDATE 실패 하였습니다" + Integer.toString(intRtnVal);
					szMsg      = "JSP-SESSION "+szOperationName+"] " + szRtnStr;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnStr;
				}
			} else {
				recPara.setField("YD_SCH_CD", 		"");
				recPara.setField("YD_WBOOK_ID",		"");
				recPara.setField("YD_STK_COL_GP", 	"");
				recPara.setField("YD_STK_BED_NO", 	"");
			}

			// 야드재료 저장위치 정보 UPDATE
			intRtnVal = ydStockDao.updYdStkColInfo(recPara);
			if (intRtnVal <= 0) {
				szRtnStr = "야드재료 저장위치 정보 UPDATE 실패 하였습니다" + Integer.toString(intRtnVal);
				szMsg      = "JSP-SESSION "+szOperationName+"] " + szRtnStr;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnStr;
			}

			szMsg = "JSP-SESSION [저장위치수정] 끝";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			// 2013.08.27 : 저장위치 수정후 적치재료의 길이의 합이 베드(열)의 길이를 초과 하는지 체크
			int iRemainMtlL = ydStkLyrDao.getRemainMtlL(recPara);
			if (iRemainMtlL < 0) {
				szRtnStr 	= "재료길이 합이 적치열길이 초과!! " + Integer.toString(iRemainMtlL*(-1));
				szMsg		= "JSP-SESSION "+szOperationName+"] " + szRtnStr;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnStr;
			}

			if ("Y".equals(pSendFlag)) {
				//---------------------------------------------------
				// FROM 과 TO 저장위치를 비교하여 변경이 없을경우는 송신하지 않음
				//---------------------------------------------------
				if (szYdUpWrLoc.equals(szYdDnWrLoc)) {

					szMsg = "[ " +szOperationName + "] 단 정보만 바뀌었을때 .. 조업L3 전문송신 SKIP";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				} else {

					//---------------------------------------------------
					// 조업L3 전문송신 (후판조업 저장위치변경정보 전송  - YDPPJ011)
					//---------------------------------------------------
					szMsg = "[ " +szOperationName + "] 조업L3 전문송신 START";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		        	recL3Para = JDTORecordFactory.getInstance().create();

		        	recL3Para.setField("MSG_ID", 			"YDPPJ011");
		        	recL3Para.setField("YD_STK_COL_FR", 	ydUtils.substr(szYdUpWrLoc, 0, 6));			// From적치열
		        	recL3Para.setField("YD_STK_BED_FR", 	ydUtils.substr(szYdUpWrLoc, 6, 2));			// From적치BED
		        	recL3Para.setField("YD_STK_COL_TO", 	ydUtils.substr(szYdDnWrLoc, 0, 6));			// TO적치열
			        recL3Para.setField("YD_STK_BED_TO", 	ydUtils.substr(szYdDnWrLoc, 6, 2));			// TO적치BED
			        recL3Para.setField("YD_EQP_WRK_SH", 	"1");										// 야드설비작업매수
		            recL3Para.setField("ARR_STL_NO", 		szStlNo);

		        	szRtnMsg = ydDelegate.sendMsg(recL3Para);

					szMsg = "[ " +szOperationName + "] 조업L3 전문송신 END >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					if ("RT".equals(ydUtils.substr(szYdUpWrLoc, 2, 2)) && "RT".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {

						szMsg = "["+ szOperationName +"] FROM, TO위치가 RT일때 BOOK-IN/OUT 실적 전송 .. SKIP";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					} else {
						//------------------------------------------------------------
				        // 2후판전단L2 RT BOOK-OUT 실적 전송 - YDS1L005
						//------------------------------------------------------------
						if ("RT".equals(ydUtils.substr(szYdUpWrLoc, 2, 2))) {

							szMsg = "["+ szOperationName +"] RT BOOK-OUT 실적 전송 .. 시작";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

							recL2Para = JDTORecordFactory.getInstance().create();
							recL2Para.setField("MSG_ID", 			"YDS1L005");
							recL2Para.setField("STL_NO",			szStlNo);								// 재료번호
							recL2Para.setField("OPERATION_TYPE",	"2");									// 1:Book In, 2:Book Out
							recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szYdUpWrLoc, 0, 6));		// TO위치
							recL2Para.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdUpWrLoc, 6, 2));    	// 야드적치BED번호

							szRtnMsg = ydDelegate.sendMsg(recL2Para);

							szMsg = "["+ szOperationName +"] RT BOOK-OUT 실적 전송 .. 완료>>>>"+szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						}

						//------------------------------------------------------------
				        // 2후판전단L2 RT BOOK-IN 실적 전송 - YDS1L005
						//------------------------------------------------------------
						if ("RT".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {

							szMsg = "["+ szOperationName +"] RT BOOK-IN 실적 전송 .. 시작";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

							recL2Para = JDTORecordFactory.getInstance().create();
							recL2Para.setField("MSG_ID", 			"YDS1L005");
							recL2Para.setField("STL_NO",			szStlNo);								// 재료번호
							recL2Para.setField("OPERATION_TYPE",	"1");									// 1:Book In, 2:Book Out
							recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szYdDnWrLoc, 0, 6));		// TO위치
							recL2Para.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdDnWrLoc, 6, 2));    	// 야드적치BED번호

							szRtnMsg = ydDelegate.sendMsg(recL2Para);

							szMsg = "["+ szOperationName +"] RT BOOK-IN 실적 전송 .. 완료>>>>"+szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						}
					}
				}
			}

			return JPlateYdConst.RETN_CD_SUCCESS;

		} catch (Exception e) {

			szMsg = "[ " +szOperationName + "] 조업L3 전문송신 Exception >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}	// end of updJPlateYdStkPosFix


	/**
	 * [2후판정정야드] 저장위치수정 List
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String updYdLocList(JDTORecord [] inDto) throws DAOException {

		JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();
		JPlateYdStockDAO	ydStockDao	= new JPlateYdStockDAO();

		String	szRtnMsg		= "";
		String 	szMsg        	= "";
		String 	szMethodName	= "updYdLocList";
		String 	szOperationName	= "저장위치수정 LIST";

		String	szStlNo			= "";
		String	szOldStlNo		= "";

		String	szYdStkColGp	= "";
		String	szYdStkBedNo	= "";
		String	szYdStkLyrNo	= "";
		String	szMtlStat		= "";
		String	szModifier		= "";
		String	szArrStlNo		= "";			// L3 전송용 재료번호 보관
		String	szOldYdStkColGp = "";			// OLD위치 적치열구분
		String	szOldYdStkBedNo = "";			// OLD위치 적치베드 구분
		String	szOldYdStkLyrNo = "";			// OLD위치 적치단 구분
		String	szYdGp			= "";
		String	szYdUpColGp   	= "";
		String	szYdUpBedNo   	= "";
		String	szYdUpWrLoc   	= "";
		String	szYdUpWrLayer 	= "";
		String	szTempColGp		= "";

		int 	iRtnVal			= 0;
		int		iYdStkBedCnt	= 0;
		int		iUpdOkCnt		= 0;

		JDTORecordSet rsResult  = null;
		JDTORecord recTemp      = null;
		JDTORecord recPara      = null;
		JDTORecord recL2Para	= null;
		JDTORecord recL3Para	= null;

		try {

			szMsg = "JSP-SESSION ["+szOperationName+"] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if (inDto.length > 0) {
				szYdStkColGp = ydDaoUtils.paraRecChkNull(inDto[0], "YD_STK_COL_GP");
				szModifier	 = ydDaoUtils.paraRecModifier(inDto[0]);
				szYdGp		 = ydDaoUtils.paraRecChkNull(inDto[0], "YD_GP", JPlateYdConst.YD_GP_F_PLATE_YARD);
			} else {
				szRtnMsg = "저장위치 변경 대상이 없습니다.";
				szMsg = "[" +szOperationName + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			//------------------------------
			// 1.1. TO 저장위치 예약정보 조회
			//------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("tempYd");
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", 		 szYdStkColGp);
			recPara.setField("YD_STK_LYR_MTL_STAT1", "U");
			recPara.setField("YD_STK_LYR_MTL_STAT2", "D");
			iRtnVal = ydStkLyrDao.getByLocMtlStat(recPara, rsResult);

			if (iRtnVal > 0) {
				szRtnMsg = "TO위치에 작업 예약 정보가 존재합니다.";
				szMsg = "[" +szOperationName + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			//---------------------------------------------------------------------------------------------
			// 2.1. TO 저장위치 Clear :: 조건 해당 저장위치, 적치단의 베드 정보 Clear
			//---------------------------------------------------------------------------------------------
			for (int ii=0; ii<inDto.length; ii++) {

				szYdStkColGp = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GP");
				szYdStkLyrNo = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_LYR_NO");

				szMsg = "[" +szOperationName + "] TO 저장위치 초기화 시작 .... 저장위치 :: " + szYdStkColGp + "-" + szYdStkLyrNo;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP", 	szYdStkColGp);
				recPara.setField("MODIFIER", 		szModifier);
				recPara.setField("YD_STK_LYR_NO",	szYdStkLyrNo);
				recPara.setField("OCPY_CHK_FLAG",	"N");
				iRtnVal = ydStkLyrDao.updYdStklyrClear(recPara);

				szMsg = "[ " +szOperationName + "] TO 저장위치 초기화 결과 .... 건수  :: " + Integer.toString(iRtnVal);
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

			//---------------------------------------------------------------------------------------------
			// 3.1. TO 저장위치 SET
			//---------------------------------------------------------------------------------------------
			for (int ii=0; ii<inDto.length; ii++) {

				szYdStkColGp = ydDaoUtils.paraRecChkNull(inDto[ii], 	"YD_STK_COL_GP");
				szYdStkLyrNo = ydDaoUtils.paraRecChkNull(inDto[ii], 	"YD_STK_LYR_NO");
				iYdStkBedCnt = ydDaoUtils.paraRecChkNullInt(inDto[ii], 	"YD_STK_BED_CNT");

				for(int jj=1; jj<=iYdStkBedCnt; jj++) {
					szStlNo		= ydDaoUtils.paraRecChkNull(inDto[ii], "STL_NO"+jj);
					szOldStlNo 	= ydDaoUtils.paraRecChkNull(inDto[ii], "OLD_STL_NO"+jj);
					szMtlStat	= ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_LYR_MTL_STAT"+jj);

					if ("V".equals(szMtlStat)) {
						szMsg = "[ " +szOperationName + "] ("+ii+","+jj+") 해당위치가 점유베드 임으로 SKIP .... " + szMtlStat;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						continue;
					}

					szOldYdStkColGp = ydDaoUtils.paraRecChkNull(inDto[ii], 	"OLD_YD_STK_COL_GP"+jj);	// OLD위치 적치열구분
					szOldYdStkBedNo = ydDaoUtils.paraRecChkNull(inDto[ii], 	"OLD_YD_STK_BED_NO"+jj);	// OLD위치 적치베드 구분
					szOldYdStkLyrNo = ydDaoUtils.paraRecChkNull(inDto[ii], 	"OLD_YD_STK_LYR_NO"+jj);	// OLD위치 적치단 구분

					if (!"".equals(szStlNo)) {
						szYdStkBedNo = "0" + Integer.toString(jj);

						// 저장위치 변경 없을시는 기존 저장위치 SET :: 저장위치 변경 이력 안남기도록 하기 위해서
						//if ("".equals(szOldYdStkColGp) && szStlNo.equals(szOldStlNo)) {
						//	szOldYdStkColGp = szYdStkColGp;
						//	szOldYdStkBedNo = szYdStkBedNo;
						//	szOldYdStkLyrNo = szYdStkLyrNo;
						//}

						recPara  = JDTORecordFactory.getInstance().create();
						recPara.setField("LOC_INS",				"N");									// 저장위치 끼워넣기 FLAG
						recPara.setField("STL_NO",				szStlNo);								// 재료번호
						recPara.setField("YD_USER_ID",			szModifier);							// 등록자, 수정자
						recPara.setField("YD_STK_COL_GP",		szYdStkColGp);							// TO위치 적치열구분
						recPara.setField("YD_STK_BED_NO",		szYdStkBedNo);							// TO위치 적치베드 구분
						recPara.setField("YD_STK_LYR_NO",		szYdStkLyrNo);							// TO위치 적치단 구분
						recPara.setField("SEND_FLAG",			"N");									// 일괄송신을 위해 저장위치변경정보 송신 FLAG 'N'로 SET
						recPara.setField("DEL_FLAG",			"N");									// 저장위치삭제 FLAG
						recPara.setField("YD_SCH_CD",			JPlateYdConst.SCH_CD_JPLATE_LOC_LIST);	// 스케쥴코드 : 저장위치 목록 수정
						recPara.setField("OLD_YD_STK_COL_GP",	szOldYdStkColGp);						// OLD위치 적치열구분
						recPara.setField("OLD_YD_STK_BED_NO",	szOldYdStkBedNo);						// OLD위치 적치베드 구분
						recPara.setField("OLD_YD_STK_LYR_NO",	szOldYdStkLyrNo);						// OLD위치 적치단 구분

						szMsg = "[ " +szOperationName + "] ("+ii+","+jj+") 저장위치수정 호출 >>>> " + recPara.toString();
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						szRtnMsg = this.updYdLocInfo(recPara);

						szMsg = "[ " +szOperationName + "] ("+ii+","+jj+") 저장위치수정 호출 결과 >>>> " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
							return szRtnMsg;
						}
						iUpdOkCnt ++;

						// 조업 L3 일괄 전송용 데이타 편집
						if (!szYdStkColGp.equals(szOldYdStkColGp)) {
							if ("".equals(szArrStlNo)) {
								szArrStlNo = szStlNo;
							} else {
								szArrStlNo = szArrStlNo + ";" + szStlNo;
							}
						}
					}
				}
			}

			//---------------------------------------------------------------------------------------------
			// 4.1. 저장위치 삭제 처리 : 조건 재료번호 삭제시 (화면에서 재료번호 변경,CLEAR & 저장위치 미존재)
			//---------------------------------------------------------------------------------------------
			int 	iDelCnt = 0;
            szMsg = "["+szOperationName+"] 저장위치(야드재료) 삭제 처리 ---- START";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			for (int ii=0; ii<inDto.length; ii++) {

				szYdUpColGp   = ydDaoUtils.paraRecChkNull(inDto[ii], 	"YD_STK_COL_GP");
				szYdUpWrLayer = ydDaoUtils.paraRecChkNull(inDto[ii], 	"YD_STK_LYR_NO");
				iYdStkBedCnt  = ydDaoUtils.paraRecChkNullInt(inDto[ii], "YD_STK_BED_CNT");

				for(int jj=1; jj<=iYdStkBedCnt; jj++) {

					szYdUpBedNo = "0" + Integer.toString(jj);
					szYdUpWrLoc = szYdUpColGp + szYdUpBedNo;

					szStlNo		= ydDaoUtils.paraRecChkNull(inDto[ii], "STL_NO"+jj);
					szOldStlNo 	= ydDaoUtils.paraRecChkNull(inDto[ii], "OLD_STL_NO"+jj);

					if (!szStlNo.equals(szOldStlNo) && !"".equals(szOldStlNo)) {

			            szMsg = "["+szOperationName+"] ("+ii+","+jj+") 저장위치(야드재료) 삭제 처리 ---- 재료번호 :: " + szOldStlNo;
			            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						//---------------------------------------------------------------------------------------------
						// 4.1.1. 해당 OLD 재료 번호로 저장위치를 조회
						//---------------------------------------------------------------------------------------------
						rsResult = JDTORecordFactory.getInstance().createRecordSet("tempYd");
						recTemp  = JDTORecordFactory.getInstance().create();
						recPara  = JDTORecordFactory.getInstance().create();
						recPara.setField("STL_NO", 	szOldStlNo);
						recPara.setField("YD_GP", 	szYdGp);

						iRtnVal  = ydStockDao.getYdStockWithLoc(recPara, rsResult);
						if (iRtnVal > 0) {
							rsResult.first();
							recTemp = rsResult.getRecord();
							szTempColGp = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");

							if ("".equals(szTempColGp)) {

								// ------------------------------------------------------------------------
								// 4.1.2. 저장위치 변경이력 등록
								// ------------------------------------------------------------------------
					            szMsg = "["+szOperationName+"] 저장위치 변경이력 등록 ---- START";
					            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

								recPara.setField("YD_UP_WR_LOC",	szYdUpWrLoc);			// 권상실적위치
								recPara.setField("YD_UP_WR_LAYER",	szYdUpWrLayer);			// 권상실적단
								recPara.setField("YD_SCH_ST_GP", 	"B");					// 야드 스케줄기동 구분 (A:AUTO, M:MANUAL, B:BACK-UP)
								recPara.setField("YD_SCH_CD",		JPlateYdConst.SCH_CD_JPLATE_LOC_LIST);
								recPara.setField("MODIFIER", 		szModifier);

								szRtnMsg = this.insYdWrkHistPosFix(recPara);
								if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
									szMsg = "["+szOperationName+"] 저장위치변경이력 등록 오류 >>>> " + szRtnMsg;
									ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
									return szRtnMsg;
								}

								// ------------------------------------------------------------------------
								// 4.1.3. 재료정보 삭제
								// ------------------------------------------------------------------------
					            szMsg = "["+szOperationName+"] 재료정보 삭제 ---- START";
					            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

								recPara  = JDTORecordFactory.getInstance().create();
								recPara.setField("STL_NO", 			szOldStlNo);             					// 재료번호
								recPara.setField("MODIFIER", 		szModifier);

								iRtnVal = ydStockDao.delYdStock(recPara);
								if (iRtnVal < 0) {
									szRtnMsg = "재료정보 삭제처리시 오류 발생! .... 오류코드 :" + Integer.toString(iRtnVal);
									szMsg    = "["+szOperationName+"] " + szRtnMsg;
									ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
									return szRtnMsg;
								}

								// ------------------------------------------------------------------------
								// 4.1.4. 야드L2 저장품제원 정보 송신 [YDY7L002]
								// ------------------------------------------------------------------------
					            szMsg = "["+szOperationName+"] 야드L2 저장위치삭제 정보 전송 ---- START";
					            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					        	recL2Para = JDTORecordFactory.getInstance().create();
					        	recL2Para.setField("JMS_TC_CD", 		"YDY7L002");                            // TC-CODE
					        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);		// 야드구분
					        	recL2Para.setField("YD_STK_COL_GP", 	"");                          			// 야드적치열구분
					        	recL2Para.setField("YD_STK_BED_NO", 	"");    								// 야드적치BED번호
					        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
					        	recL2Para.setField("STL_NO", 			szOldStlNo);	        				// 재료번호
					        	recL2Para.setField("MSG_GP", 			"D");	        						// 전문구분
					        	recL2Para.setField("DEL_YN_CHECK",		"N");									// 삭제된 데이타도 조회하도록 처리

					        	szRtnMsg = ydDelegate.sendMsg(recL2Para);

								szMsg = "[ " +szOperationName + "] 야드L2 전문송신 END >>>> " + szRtnMsg;
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

								// ------------------------------------------------------------------------
								// 4.1.5. 후판조업 저장품제원 정보 송신 [YDPPJ011]
								// ------------------------------------------------------------------------
					            szMsg = "["+szOperationName+"] 후판조업 저장위치 삭제 정보 전송 ---- START";
					            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					            recL3Para = JDTORecordFactory.getInstance().create();
					            recL3Para.setField("MSG_ID", 			"YDPPJ011");
					            recL3Para.setField("YD_STK_COL_FR", 	szYdUpColGp);							// From적치열
					            recL3Para.setField("YD_STK_BED_FR", 	szYdUpBedNo);							// From적치BED
					            recL3Para.setField("YD_STK_COL_TO", 	"");									// TO적치열
					            recL3Para.setField("YD_STK_BED_TO", 	"");									// TO적치BED
					            recL3Para.setField("YD_EQP_WRK_SH", 	"");									// 야드설비작업매수
					            recL3Para.setField("ARR_STL_NO", 		szOldStlNo);

					            szRtnMsg = ydDelegate.sendMsg(recL3Para);

								szMsg = "["+szOperationName+"] 후판조업 저장위치 삭제 정보 전송 완료>>>>" + szRtnMsg;
					            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					            iDelCnt ++;

							} else {
					            szMsg = "["+szOperationName+"] 저장위치가 존재하여 삭제 SKIP >>>> " + szTempColGp;
					            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
							}
						}
					}
				}
			}
            szMsg = "["+szOperationName+"] 저장위치(야드재료) 삭제 처리 ---- END >>>> 건수 :: " + iDelCnt;
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//---------------------------------------------------------------------------------------------
			// 5.1. 저장위치 변경정보 일괄 전송
			//---------------------------------------------------------------------------------------------
			if (iUpdOkCnt > 0) {

				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				recL2Para = JDTORecordFactory.getInstance().create();
				recL2Para.setField("JMS_TC_CD", 		"YDY7L002");                            // TC-CODE
				recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);		// 야드구분
				recL2Para.setField("YD_STK_COL_GP", 	szYdStkColGp);                          // 야드적치열구분
				recL2Para.setField("YD_STK_BED_NO", 	"");    								// 야드적치BED번호
				recL2Para.setField("YD_INFO_SYNC_CD", 	"3");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
				recL2Para.setField("STL_NO", 			"");	        						// 재료번호
				recL2Para.setField("ARR_STL_NO", 		szArrStlNo);

				szRtnMsg = ydDelegate.sendMsg(recL2Para);

				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);


	            szMsg    = "["+szOperationName+"] 후판조업 저장위치변경정보 전송 ---- START";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	            recL3Para = JDTORecordFactory.getInstance().create();
	            recL3Para.setField("MSG_ID", 			"YDPPJ011");
	            recL3Para.setField("YD_STK_COL_FR", 	"");									// From적치열
	            recL3Para.setField("YD_STK_BED_FR", 	"");									// From적치BED
	            recL3Para.setField("YD_STK_COL_TO", 	szYdStkColGp);							// TO적치열
	            recL3Para.setField("YD_STK_BED_TO", 	"01");									// TO적치BED
	            recL3Para.setField("YD_EQP_WRK_SH", 	"");									// 야드설비작업매수
	            recL3Para.setField("ARR_STL_NO", 		szArrStlNo);

	        //	szRtnMsg = ydDelegate.sendMsg(recL3Para);
	            szRtnMsg = JPlateYdCommonUtils.sendL3YDPPJ011(recL3Para);

				szMsg = "["+szOperationName+"] 후판조업 저장위치변경정보 전송 완료>>>>" + szRtnMsg;
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}// end of updYdLocList

   /**
	 *  작업예약등록(이적)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String insMvWBookId(JDTORecord[] inDto) throws DAOException {

    	JPlateYdStkLyrDAO   ydStkLyrDao 	= new JPlateYdStkLyrDAO();
    	JPlateYdWrkbookDAO	ydWrkbookDao	= new JPlateYdWrkbookDAO();

		JDTORecordSet 	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("ydTemp");
		JDTORecord 		recPara 	= JDTORecordFactory.getInstance().create();

		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMethodName		= "insMvWBookId";
		String 	szOperationName 	= "작업예약등록(이적)";
		String 	szLogMsg 			= "";
		String	szYdMainWrkGp		= "";
		String	szYdStkColGp 		= "";
		String	szYdTcGp			= "";
		String	szYdGp				= "";
		String	szYdBayGp			= "";
		String	szYdSpanGp			= "";
		String	szCrnschSkipFlag	= "N";

		int		intRtnVal			= 0;

		try {

			szLogMsg = "JSP-SESSION ["+szOperationName+"] 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			// 권상위치 상단에 크레인 작업지시가 존재시 작업예약만 등록
			szYdStkColGp = ydDaoUtils.paraRecChkNull(inDto[0], "YD_STK_COL_GPS");
			rsResult 	 = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
			recPara 	 = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP",	szYdStkColGp);

			intRtnVal = ydWrkbookDao.getExistByYdStkColGp(recPara, rsResult);
			if (intRtnVal > 0) {
				szCrnschSkipFlag = "Y";		// 권상위치에 작업예약 존재하여 크레인 스케줄 호출 SKIP
			} else {
				szCrnschSkipFlag = "N";
			}
			szLogMsg = "[" + szOperationName + "] 권상위치에 작업예약 존재여부 체크하여 크레인 스케줄 SKIP여부 >>>> " + szCrnschSkipFlag;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			//내부 Process 연결
			EJBConnector ejbConn = new EJBConnector("default", this);

			for (int ii=0; ii<inDto.length; ii++) {

				//---------------------------------------------------------------------------------------------
				// 이적 대상재 등록시 TO위치나 대차에 권상예약 정보 존재시 오류 처리
				// --> 대차를 선택하거나 TO위치 지정했을때 (동+스판+열까지 입력)
				//---------------------------------------------------------------------------------------------
				szYdGp		= ydDaoUtils.paraRecChkNull(inDto[ii], "YD_GP");
				szYdBayGp	= ydDaoUtils.paraRecChkNull(inDto[ii], "YD_BAY_GP");
				szYdStkColGp= ydDaoUtils.paraRecChkNull(inDto[ii], "YD_TO_LOC_GUIDE_GP");
				szYdTcGp	= ydDaoUtils.paraRecChkNull(inDto[ii], "YD_TC_GP");

				if (!"".equals(szYdTcGp) || szYdStkColGp.length() >= 6) {

					if (!"".equals(szYdTcGp)) {
						szYdStkColGp = szYdGp + szYdBayGp + ydUtils.substr(szYdTcGp, 2, 4);
					}
					rsResult 	= JDTORecordFactory.getInstance().createRecordSet("ydTemp");
					recPara 	= JDTORecordFactory.getInstance().create();
					recPara.setField("YD_STK_COL_GP", 			szYdStkColGp);
					recPara.setField("YD_STK_LYR_MTL_STAT1", 	"U");
					recPara.setField("YD_STK_LYR_MTL_STAT2", 	"U");
					recPara.setField("YD_STK_LYR_MTL_STAT3", 	"U");

					intRtnVal 	= ydStkLyrDao.getByLocMtlStat(recPara, rsResult);
					if (intRtnVal > 0) {
						szRtnMsg = "TO위치[" + szYdStkColGp + "]에 권상예약 정보가 존재하여 작업불가!";
						szLogMsg = "[" + szOperationName + "]" + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}
			}

			for (int ii=0; ii<inDto.length; ii++) {
				szYdMainWrkGp = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_MAIN_WRK_GP");		// 1:이적, 2:북인, 3:보수장이적[저장위치수정]

				recPara = JDTORecordFactory.getInstance().create();
				// 해당 스케줄 기준정보에 야드 스케줄 금지유무를 "Y" Setting 한다.
				recPara.setField("YD_GP", 	          	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_GP"));
				recPara.setField("YD_BAY_GP", 	      	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_BAY_GP"));
				recPara.setField("YD_SPAN_GP", 	      	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_SPAN_GP"));
				recPara.setField("YD_AIM_RT_GP", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_RT_GP"));
			//  주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차, M:보수장이적
				recPara.setField("YD_MAIN_WRK_GP", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_MAIN_WRK_GP"));
				recPara.setField("YD_BS_MV_GP",			ydDaoUtils.paraRecChkNull(inDto[ii], "YD_BS_MV_GP"));			// BS:보수장, 1:#1보수대기, 2:#2보수대기, 3:충당대기
				recPara.setField("YD_TO_LOC_GUIDE_GP",	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_TO_LOC_GUIDE_GP"));
				recPara.setField("YD_AIM_YD_GP", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_YD_GP"));
				recPara.setField("YD_AIM_BAY_GP", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_BAY_GP"));
				recPara.setField("YD_AIM_SPAN_GP", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_SPAN_GP"));
				recPara.setField("YD_AIM_COL_GP", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_COL_GP"));
			//	recPara.setField("YD_AIM_BED_NO", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_BED_NO"));

				szYdSpanGp = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_SPAN_GP");
				if ("BS".equals(szYdSpanGp) || "CN".equals(szYdSpanGp) || "RT".equals(szYdSpanGp)) {
					recPara.setField("YD_AIM_BED_NO", 	"");
				} else {
					recPara.setField("YD_AIM_BED_NO", 	"01");														// 무조건 01베드로 TO위치 결정되도록 보완
				}

				recPara.setField("YD_STK_COL_GP", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GPS"));
				recPara.setField("YD_STK_BED_NO", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_NOS"));
				recPara.setField("YD_TO_LOC_GUIDE", 	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_TO_LOC_GUIDE"));
				recPara.setField("YD_TC_GP", 	  		ydDaoUtils.paraRecChkNull(inDto[ii], "YD_TC_GP"));

				//---------------------------------------------------------------------------------------------
				//	후판정정의 준비스케줄 등록 시 지정한 작업매수의 값을 전달하여 해당하는 매수만큼 작업예약에 등록
				//---------------------------------------------------------------------------------------------
				recPara.setField("YD_EQP_WRK_SH", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_EQP_WRK_SH"));

				//---------------------------------------------------------------------------------------------
				//	이송대기인 경우 착지개소코드도 파라미터로 전달됨
				//---------------------------------------------------------------------------------------------
				recPara.setField("ARR_WLOC_CD", 	    ydDaoUtils.paraRecChkNull(inDto[ii], "ARR_WLOC_CD"));

				//---------------------------------------------------------------------------------------------
				//	작업재료리스트와 JMS_TC_CD
				//---------------------------------------------------------------------------------------------
				recPara.setField("STL_LIST", 	        ydDaoUtils.paraRecChkNull(inDto[ii], "STL_LIST"));
				recPara.setField("JMS_TC_CD", 	        ydDaoUtils.paraRecChkNull(inDto[ii], "JMS_TC_CD"));

				recPara.setField("YD_USER_ID", 	        ydDaoUtils.paraRecModifier(inDto[ii]));

				//---------------------------------------------------------------------------------------------
				//	크레인 스케줄 호출 SKIP FLAG (상단지시 존재시 SKIP여부 결정)
				//---------------------------------------------------------------------------------------------
				recPara.setField("CRNSCH_SKIP_FLAG",	szCrnschSkipFlag);		// 작업예약만 등록하고 크레인 작업지시 생성 SKIP

				if ("M".equals(szYdMainWrkGp)) {								// 보수장이적
					szRtnMsg = this.procBsLocChg(recPara);						// 보수장 저장위치 변경
				} else {
					szRtnMsg = (String)ejbConn.trx("JPlateYdMvStkWrkSeEJB", "procPrepLotCompByCapa", recPara);
				}

				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					return szRtnMsg;
				}
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION ["+szOperationName+"] 끝  .. 결과:" + szRtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of insMvWBookId
	
	/**
	 * [2후판정정야드] 저장위치수정 List (보수장)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String updYdLocListBs(JDTORecord [] inDto) throws DAOException {

		JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();
		JPlateYdStockDAO	ydStockDao	= new JPlateYdStockDAO();

		String	szRtnMsg		= "";
		String 	szMsg        	= "";
		String 	szMethodName	= "updYdLocListBs";
		String 	szOperationName	= "저장위치수정 LIST (보수장)";

		String	szStlNo			= "";
		String	szOldStlNo		= "";

		String	szYdStkColGp	= "";
		String	szYdStkBedNo	= "";
		String	szYdStkLyrNo	= "";
		String	szMtlStat		= "";
		String	szModifier		= "";
		String	szArrStlNo		= "";			// L3 전송용 재료번호 보관
		String	szOldYdStkColGp = "";			// OLD위치 적치열구분
		String	szOldYdStkBedNo = "";			// OLD위치 적치베드 구분
		String	szOldYdStkLyrNo = "";			// OLD위치 적치단 구분
		String	szYdGp			= "";
		String	szYdUpColGp   	= "";
		String	szYdUpBedNo   	= "";
		String	szYdUpWrLoc   	= "";
		String	szYdUpWrBedNo 	= "";
		String	szYdUpWrLayer 	= "";
		String	szTempColGp		= "";

		int 	iRtnVal			= 0;
		int		iYdStkBedCnt	= 0;
		int		iUpdOkCnt		= 0;

		JDTORecordSet rsResult  = null;
		JDTORecord recTemp      = null;
		JDTORecord recPara      = null;
		JDTORecord recL2Para	= null;
		JDTORecord recL3Para	= null;

		try {

			szMsg = "JSP-SESSION ["+szOperationName+"] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if (inDto.length > 0) {
				szYdStkColGp = ydDaoUtils.paraRecChkNull(inDto[0], "YD_STK_COL_GP");
				szModifier	 = ydDaoUtils.paraRecModifier(inDto[0]);
				szYdGp		 = ydDaoUtils.paraRecChkNull(inDto[0], "YD_GP", JPlateYdConst.YD_GP_F_PLATE_YARD);
			} else {
				szRtnMsg = "저장위치 변경 대상이 없습니다.";
				szMsg = "[" +szOperationName + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			//------------------------------
			// 1.1. TO 저장위치 예약정보 조회
			//------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("tempYd");
			recPara  = JDTORecordFactory.getInstance().create();
			
			//recPara.setField("YD_STK_COL_GP", 		 szYdStkColGp);
			//recPara.setField("YD_STK_LYR_MTL_STAT1", "U");
			//recPara.setField("YD_STK_LYR_MTL_STAT2", "D");
			//iRtnVal = ydStkLyrDao.getByLocMtlStat(recPara, rsResult);

			//if (iRtnVal > 0) {
			//	szRtnMsg = "TO위치에 작업 예약 정보가 존재합니다.";
			//	szMsg = "[" +szOperationName + "] " + szRtnMsg;
			//	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			//	return szRtnMsg;
			//}

			//---------------------------------------------------------------------------------------------
			// 2.1. TO 저장위치 Clear :: 조건 해당 저장위치, 적치단의 베드 정보 Clear
			//---------------------------------------------------------------------------------------------
			for (int ii=0; ii<inDto.length; ii++) {

				szYdStkColGp = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GP");
				szYdStkBedNo = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_NO");
				szYdStkLyrNo = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_LYR_NO1");

				szMsg = "[" +szOperationName + "] TO 저장위치 초기화 시작 .... 저장위치 :: " + szYdStkColGp + "-" + szYdStkBedNo;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP", 	szYdStkColGp);
				recPara.setField("MODIFIER", 		szModifier);
				recPara.setField("YD_STK_BED_NO",	szYdStkBedNo);
				recPara.setField("YD_STK_LYR_NO",	szYdStkLyrNo);
				recPara.setField("OCPY_CHK_FLAG",	"N");
				iRtnVal = ydStkLyrDao.updYdStklyrClear(recPara);

				szMsg = "[ " +szOperationName + "] TO 저장위치 초기화 결과 .... 건수  :: " + Integer.toString(iRtnVal);
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

			//---------------------------------------------------------------------------------------------
			// 3.1. TO 저장위치 SET
			//---------------------------------------------------------------------------------------------
			for (int ii=0; ii<inDto.length; ii++) {

				szYdStkColGp = ydDaoUtils.paraRecChkNull(inDto[ii], 	"YD_STK_COL_GP");
				szYdStkBedNo = ydDaoUtils.paraRecChkNull(inDto[ii], 	"YD_STK_BED_NO");
				szYdStkLyrNo = ydDaoUtils.paraRecChkNull(inDto[ii], 	"YD_STK_LYR_NO1");
				//iYdStkBedCnt = ydDaoUtils.paraRecChkNullInt(inDto[ii], 	"YD_STK_BED_CNT");

				//for(int jj=1; jj<=iYdStkBedCnt; jj++) {
					szStlNo		= ydDaoUtils.paraRecChkNull(inDto[ii], "STL_NO1");
					szOldStlNo 	= ydDaoUtils.paraRecChkNull(inDto[ii], "OLD_STL_NO1");
					szMtlStat	= ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_LYR_MTL_STAT1");

					if ("V".equals(szMtlStat)) {
						szMsg = "[ " +szOperationName + "] ("+ii+") 해당위치가 점유베드 임으로 SKIP .... " + szMtlStat;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						continue;
					}

					szOldYdStkColGp = ydDaoUtils.paraRecChkNull(inDto[ii], 	"OLD_YD_STK_COL_GP1");	// OLD위치 적치열구분
					szOldYdStkBedNo = ydDaoUtils.paraRecChkNull(inDto[ii], 	"OLD_YD_STK_BED_NO1");	// OLD위치 적치베드 구분
					szOldYdStkLyrNo = ydDaoUtils.paraRecChkNull(inDto[ii], 	"OLD_YD_STK_LYR_NO1");	// OLD위치 적치단 구분

					if (!"".equals(szStlNo)) {

						recPara  = JDTORecordFactory.getInstance().create();
						recPara.setField("LOC_INS",				"N");									// 저장위치 끼워넣기 FLAG
						recPara.setField("STL_NO",				szStlNo);								// 재료번호
						recPara.setField("YD_USER_ID",			szModifier);							// 등록자, 수정자
						recPara.setField("YD_STK_COL_GP",		szYdStkColGp);							// TO위치 적치열구분
						recPara.setField("YD_STK_BED_NO",		szYdStkBedNo);							// TO위치 적치베드 구분
						recPara.setField("YD_STK_LYR_NO",		szYdStkLyrNo);							// TO위치 적치단 구분
						recPara.setField("SEND_FLAG",			"N");									// 일괄송신을 위해 저장위치변경정보 송신 FLAG 'N'로 SET
						recPara.setField("DEL_FLAG",			"N");									// 저장위치삭제 FLAG
						recPara.setField("YD_SCH_CD",			JPlateYdConst.SCH_CD_JPLATE_LOC_LIST);	// 스케쥴코드 : 저장위치 목록 수정
						recPara.setField("OLD_YD_STK_COL_GP",	szOldYdStkColGp);						// OLD위치 적치열구분
						recPara.setField("OLD_YD_STK_BED_NO",	szOldYdStkBedNo);						// OLD위치 적치베드 구분
						recPara.setField("OLD_YD_STK_LYR_NO",	szOldYdStkLyrNo);						// OLD위치 적치단 구분
						recPara.setField("YD_GP",				szYdGp);	// 야드구분

						szMsg = "[ " +szOperationName + "] ("+ii+") 저장위치수정 호출 >>>> " + recPara.toString();
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						szRtnMsg = this.updYdLocInfo(recPara);

						szMsg = "[ " +szOperationName + "] ("+ii+") 저장위치수정 호출 결과 >>>> " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
							return szRtnMsg;
						}
						iUpdOkCnt ++;

						// 조업 L3 일괄 전송용 데이타 편집
						if (!szYdStkColGp.equals(szOldYdStkColGp)) {
							if ("".equals(szArrStlNo)) {
								szArrStlNo = szStlNo;
							} else {
								szArrStlNo = szArrStlNo + ";" + szStlNo;
							}
						}
					}
				//}
			}

			//---------------------------------------------------------------------------------------------
			// 4.1. 저장위치 삭제 처리 : 조건 재료번호 삭제시 (화면에서 재료번호 변경,CLEAR & 저장위치 미존재)
			//---------------------------------------------------------------------------------------------
			int 	iDelCnt = 0;
            szMsg = "["+szOperationName+"] 저장위치(야드재료) 삭제 처리 ---- START";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			for (int ii=0; ii<inDto.length; ii++) {

				szYdUpColGp   = ydDaoUtils.paraRecChkNull(inDto[ii], 	"YD_STK_COL_GP");
				szYdUpBedNo = ydDaoUtils.paraRecChkNull(inDto[ii], 	"YD_STK_BED_NO");
				szYdUpWrLayer = ydDaoUtils.paraRecChkNull(inDto[ii], 	"YD_STK_LYR_NO1");
				//iYdStkBedCnt  = ydDaoUtils.paraRecChkNullInt(inDto[ii], "YD_STK_BED_CNT");

				//for(int jj=1; jj<=iYdStkBedCnt; jj++) {

					//szYdUpBedNo = "0" + Integer.toString(jj);
					szYdUpWrLoc = szYdUpColGp + szYdUpBedNo;

					szStlNo		= ydDaoUtils.paraRecChkNull(inDto[ii], "STL_NO1");
					szOldStlNo 	= ydDaoUtils.paraRecChkNull(inDto[ii], "OLD_STL_NO1");

					if (!szStlNo.equals(szOldStlNo) && !"".equals(szOldStlNo)) {

			            szMsg = "["+szOperationName+"] ("+ii+") 저장위치(야드재료) 삭제 처리 ---- 재료번호 :: " + szOldStlNo;
			            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						//---------------------------------------------------------------------------------------------
						// 4.1.1. 해당 OLD 재료 번호로 저장위치를 조회
						//---------------------------------------------------------------------------------------------
						rsResult = JDTORecordFactory.getInstance().createRecordSet("tempYd");
						recTemp  = JDTORecordFactory.getInstance().create();
						recPara  = JDTORecordFactory.getInstance().create();
						recPara.setField("STL_NO", 	szOldStlNo);
						recPara.setField("YD_GP", 	szYdGp);

						iRtnVal  = ydStockDao.getYdStockWithLoc(recPara, rsResult);
						if (iRtnVal > 0) {
							rsResult.first();
							recTemp = rsResult.getRecord();
							szTempColGp = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");

							if ("".equals(szTempColGp)) {

								// ------------------------------------------------------------------------
								// 4.1.2. 저장위치 변경이력 등록
								// ------------------------------------------------------------------------
					            szMsg = "["+szOperationName+"] 저장위치 변경이력 등록 ---- START";
					            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

								recPara.setField("YD_UP_WR_LOC",	szYdUpWrLoc);			// 권상실적위치
								recPara.setField("YD_UP_WR_LAYER",	szYdUpWrLayer);			// 권상실적단
								recPara.setField("YD_SCH_ST_GP", 	"B");					// 야드 스케줄기동 구분 (A:AUTO, M:MANUAL, B:BACK-UP)
								recPara.setField("YD_SCH_CD",		JPlateYdConst.SCH_CD_JPLATE_LOC_LIST);
								recPara.setField("MODIFIER", 		szModifier);

								szRtnMsg = this.insYdWrkHistPosFix(recPara);
								if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
									szMsg = "["+szOperationName+"] 저장위치변경이력 등록 오류 >>>> " + szRtnMsg;
									ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
									return szRtnMsg;
								}

								// ------------------------------------------------------------------------
								// 4.1.3. 재료정보 삭제
								// ------------------------------------------------------------------------
					            szMsg = "["+szOperationName+"] 재료정보 삭제 ---- START";
					            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

								recPara  = JDTORecordFactory.getInstance().create();
								recPara.setField("STL_NO", 			szOldStlNo);             					// 재료번호
								recPara.setField("MODIFIER", 		szModifier);

								iRtnVal = ydStockDao.delYdStock(recPara);
								if (iRtnVal < 0) {
									szRtnMsg = "재료정보 삭제처리시 오류 발생! .... 이전재료번호["+szOldStlNo+"] 오류코드 : " + Integer.toString(iRtnVal);
									szMsg    = "["+szOperationName+"] " + szRtnMsg;
									ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
									return szRtnMsg;
								}

								// ------------------------------------------------------------------------
								// 4.1.4. 야드L2 저장품제원 정보 송신 [YDY7L002]
								// ------------------------------------------------------------------------
					            szMsg = "["+szOperationName+"] 야드L2 저장위치삭제 정보 전송 ---- START";
					            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					        	recL2Para = JDTORecordFactory.getInstance().create();
					        	recL2Para.setField("JMS_TC_CD", 		"YDY7L002");                            // TC-CODE
					        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);		// 야드구분
					        	recL2Para.setField("YD_STK_COL_GP", 	"");                          			// 야드적치열구분
					        	recL2Para.setField("YD_STK_BED_NO", 	"");    								// 야드적치BED번호
					        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
					        	recL2Para.setField("STL_NO", 			szOldStlNo);	        				// 재료번호
					        	recL2Para.setField("MSG_GP", 			"D");	        						// 전문구분
					        	recL2Para.setField("DEL_YN_CHECK",		"N");									// 삭제된 데이타도 조회하도록 처리

					        	szRtnMsg = ydDelegate.sendMsg(recL2Para);

								szMsg = "[ " +szOperationName + "] 야드L2 전문송신 END >>>> " + szRtnMsg;
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

								// ------------------------------------------------------------------------
								// 4.1.5. 후판조업 저장품제원 정보 송신 [YDPPJ011]
								// ------------------------------------------------------------------------
					            szMsg = "["+szOperationName+"] 1후판조업 저장위치 삭제 정보 전송 ---- START";
					            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					            recL3Para = JDTORecordFactory.getInstance().create();
					            recL3Para.setField("MSG_ID", 			"YDPPJ011");
					            recL3Para.setField("YD_STK_COL_FR", 	szYdUpColGp);							// From적치열
					            recL3Para.setField("YD_STK_BED_FR", 	szYdUpBedNo);							// From적치BED
					            recL3Para.setField("YD_STK_COL_TO", 	"");									// TO적치열
					            recL3Para.setField("YD_STK_BED_TO", 	"");									// TO적치BED
					            recL3Para.setField("YD_EQP_WRK_SH", 	"");									// 야드설비작업매수
					            recL3Para.setField("ARR_STL_NO", 		szOldStlNo);

					            szRtnMsg = ydDelegate.sendMsg(recL3Para);

								szMsg = "["+szOperationName+"] 후판조업 저장위치 삭제 정보 전송 완료>>>>" + szRtnMsg;
					            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					            iDelCnt ++;

							} else {
					            szMsg = "["+szOperationName+"] 저장위치가 존재하여 삭제 SKIP >>>> " + szTempColGp;
					            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
							}
						}
					}
				//}
			}
            szMsg = "["+szOperationName+"] 저장위치(야드재료) 삭제 처리 ---- END >>>> 건수 :: " + iDelCnt;
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//---------------------------------------------------------------------------------------------
			// 5.1. 저장위치 변경정보 일괄 전송
			//---------------------------------------------------------------------------------------------
			if (iUpdOkCnt > 0) {

				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				recL2Para = JDTORecordFactory.getInstance().create();
				recL2Para.setField("JMS_TC_CD", 		"YDY7L002");                            // TC-CODE
				recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);		// 야드구분
				recL2Para.setField("YD_STK_COL_GP", 	szYdStkColGp);                          // 야드적치열구분
				recL2Para.setField("YD_STK_BED_NO", 	"");    								// 야드적치BED번호
				recL2Para.setField("YD_INFO_SYNC_CD", 	"3");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
				recL2Para.setField("STL_NO", 			"");	        						// 재료번호
				recL2Para.setField("ARR_STL_NO", 		szArrStlNo);

				szRtnMsg = ydDelegate.sendMsg(recL2Para);

				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);


	            szMsg    = "["+szOperationName+"] 후판조업 저장위치변경정보 전송 ---- START";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	            recL3Para = JDTORecordFactory.getInstance().create();
	            recL3Para.setField("MSG_ID", 			"YDPPJ011");
	            recL3Para.setField("YD_STK_COL_FR", 	"");									// From적치열
	            recL3Para.setField("YD_STK_BED_FR", 	"");									// From적치BED
	            recL3Para.setField("YD_STK_COL_TO", 	szYdStkColGp);							// TO적치열
	            recL3Para.setField("YD_STK_BED_TO", 	"01");									// TO적치BED
	            recL3Para.setField("YD_EQP_WRK_SH", 	"");									// 야드설비작업매수
	            recL3Para.setField("ARR_STL_NO", 		szArrStlNo);

	        //	szRtnMsg = ydDelegate.sendMsg(recL3Para);
	            szRtnMsg = JPlateYdCommonUtils.sendL3YDPPJ011(recL3Para);

				szMsg = "["+szOperationName+"] 후판조업 저장위치변경정보 전송 완료>>>>" + szRtnMsg;
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}// end of updYdLocListBs

	/**
	 *  보수장 이적 [저장위치변경]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String procBsLocChg(JDTORecord inRec) throws DAOException {

    	JPlateYdStkLyrDAO   ydStkLyrDao = new JPlateYdStkLyrDAO();
    	JPlateYdStockDAO	ydStockDao	= new JPlateYdStockDAO();

		JDTORecordSet rsResult 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
		JDTORecord recStock		= JDTORecordFactory.getInstance().create();
		JDTORecord recBed 		= JDTORecordFactory.getInstance().create();

		String 	szRtnMsg 		= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMethodName	= "procBsLocChg";
		String 	szOperationName = "보수장이적[저장위치변경]";
		String 	szLogMsg 		= "";
		String	szStlNo			= "";
		String	szYdStkColGp	= "";
		String	szYdStkBedNo	= "";
		String	szYdStkLyrNo	= "";
		String	szModifier		= "";
		String	szStlList		= "";
		String	szAimYdGp 		= "";
		String	szAimBayGp		= "";
		String	szAimSpanGp		= "";
	   	String	szAimColGp		= "";
	   	String	szCurrYdStkCol	= "";

		int		intRtnVal		= 0;

		try {

			szLogMsg = "JSP-SESSION ["+szOperationName+"] 시작 :: " + inRec.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			szAimYdGp 	= ydDaoUtils.paraRecChkNull(inRec, "YD_AIM_YD_GP", 		JPlateYdConst.YD_GP_F_PLATE_YARD);
			szAimBayGp	= ydDaoUtils.paraRecChkNull(inRec, "YD_AIM_BAY_GP", 	"A");
			szAimSpanGp	= ydDaoUtils.paraRecChkNull(inRec, "YD_AIM_SPAN_GP",	"BS");
		   	szAimColGp	= ydDaoUtils.paraRecChkNull(inRec, "YD_AIM_COL_GP",		"");
		   	szModifier	= ydDaoUtils.paraRecModifier(inRec);
		   	szStlList	= ydDaoUtils.paraRecChkNull(inRec, "STL_LIST");

			String strArrStlNo[] = szStlList.split(";");

			for (int ii=0; ii<strArrStlNo.length; ii++) {

				szStlNo = strArrStlNo[ii];
				szYdStkColGp = (szAimYdGp+szAimBayGp+szAimSpanGp+szAimColGp);

				// 재료의 현재위치 조회
				rsResult = JDTORecordFactory.getInstance().createRecordSet("YD");
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 			szStlNo);
				intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsResult);
				if (intRtnVal <= 0) {
					szRtnMsg = "재료번호 [" + szStlNo + "]의 현재위치 미존재!";
					szLogMsg = "[" + szOperationName + "]" + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
				rsResult.first();
				recStock = rsResult.getRecord();
				szCurrYdStkCol = ydDaoUtils.paraRecChkNull(recStock, "YD_STK_COL_GP");		// 재료의 현재위치

				// 현재위치가 보수대기/충당대기인지 체크함
				if ("FA0101".equals(szCurrYdStkCol) || "FA0102".equals(szCurrYdStkCol) ||
				    "FC0301".equals(szCurrYdStkCol) || "FC0302".equals(szCurrYdStkCol) || "FCTD01".equals(szCurrYdStkCol)) {

					szLogMsg = "[" + szOperationName + "] 현위치가 보수대기/충당대기/TOD 로 작업 가능합니다. " + szCurrYdStkCol;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				} else {
					szRtnMsg = "재료번호 [" + szStlNo + "]의 현위치가 보수대기/충당대기/TOD가 아님으로 작업 불가!" + szCurrYdStkCol;
					szLogMsg = "[" + szOperationName + "]" + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// 적치가능 위치 조회
				rsResult = JDTORecordFactory.getInstance().createRecordSet("YD");
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 			szStlNo);
				recPara.setField("YD_STK_COL_GP", 	szYdStkColGp);

				intRtnVal = ydStkLyrDao.getEmptyToLoc(recPara, rsResult);
				if (intRtnVal <= 0) {
					szRtnMsg = "재료번호 [" + szStlNo + "] 적치가능한 저장위치가 없습니다! [" + szYdStkColGp + "]";
					szLogMsg = "[" + szOperationName + "]" + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
				rsResult.first();
				recBed = rsResult.getRecord();
				szYdStkColGp 	= ydDaoUtils.paraRecChkNull(recBed, "YD_STK_COL_GP");
				szYdStkBedNo	= ydDaoUtils.paraRecChkNull(recBed, "YD_STK_BED_NO");
				szYdStkLyrNo	= ydDaoUtils.paraRecChkNull(recBed, "YD_STK_LYR_NO");

				szLogMsg = "[" + szOperationName + "] (보수장이적) 저장위치수정 호출 >>>> 재료번호 :: " + szStlNo + ", 저장위치 :: " + szYdStkColGp + szYdStkBedNo + "-" + szYdStkLyrNo;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				// 저장위치 수정 호출
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 			szStlNo);
				recPara.setField("YD_STK_COL_GP", 	szYdStkColGp);
				recPara.setField("YD_STK_BED_NO", 	szYdStkBedNo);
				recPara.setField("YD_STK_LYR_NO", 	szYdStkLyrNo);
				recPara.setField("MODIFIER", 		szModifier);
				recPara.setField("YD_SCH_ST_GP", 	"M");									// 야드스케줄 기동구분 (A:AUTO, M:MANUAL, B:BACK-UP)
				recPara.setField("YD_SCH_CD", 	   	JPlateYdConst.SCH_CD_JPLATE_BS_MV);		// 스케쥴코드 : 보수장내 이적
				szRtnMsg = this.updYdLocInfo(recPara);

				szLogMsg = "JSP-SESSION ["+szOperationName+"] 끝  .. 결과 :: " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					return szRtnMsg;
				}
			}

		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		return JPlateYdConst.RETN_CD_SUCCESS;
	}


	/**
	 *  작업예약등록(GAS장보급)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String insCncInWBook(JDTORecord[] inDto) throws DAOException {

		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();

		String szRtnMsg 		= JPlateYdConst.RETN_CD_SUCCESS;
		String szMethodName		= "insCncInWBook";
		String szOperationName 	= "작업예약등록(GAS장보급)";
		String szLogMsg 		= "";

		try {

			szLogMsg = "JSP-SESSION ["+szOperationName+"] 시작 :: " + inDto.length;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			for (int ii=0; ii<inDto.length; ii++) {

				// 해당 스케줄 기준정보에 야드 스케줄 금지유무를 "Y" Setting 한다.
				recPara.setField("YD_GP", 	          	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_GP"),""));
				recPara.setField("YD_BAY_GP", 	      	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_BAY_GP"),""));
				recPara.setField("YD_SPAN_GP", 	      	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_SPAN_GP"),""));
				recPara.setField("YD_AIM_RT_GP", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_AIM_RT_GP"),""));
			//	recPara.setField("YD_MAIN_WRK_GP", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_MAIN_WRK_GP"),""));
			//  주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차
				recPara.setField("YD_MAIN_WRK_GP", 	  	JPlateYdConst.YD_MAIN_WRK_GP_GAS_IN);
				recPara.setField("YD_TO_LOC_GUIDE_GP",	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_TO_LOC_GUIDE_GP"),""));
				recPara.setField("YD_AIM_YD_GP", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_AIM_YD_GP"),""));
				recPara.setField("YD_AIM_BAY_GP", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_AIM_BAY_GP"),""));
				recPara.setField("YD_AIM_SPAN_GP", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_AIM_SPAN_GP"),""));
				recPara.setField("YD_AIM_COL_GP", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_AIM_COL_GP"),""));
				recPara.setField("YD_AIM_BED_NO", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_AIM_BED_NO"),""));
				recPara.setField("YD_STK_COL_GP", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_STK_COL_GPS"),""));
				recPara.setField("YD_STK_BED_NO", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_STK_BED_NOS"),""));
				recPara.setField("YD_TC_GP", 	  		ydDaoUtils.setDataDefault(inDto[ii].getField("YD_TC_GP"),""));

				//---------------------------------------------------------------------------------------------
				//	후판정정의 준비스케줄 등록 시 지정한 작업매수의 값을 전달하여 해당하는 매수만큼 작업예약에 등록
				//---------------------------------------------------------------------------------------------
				recPara.setField("YD_EQP_WRK_SH", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_EQP_WRK_SH"),""));

				//---------------------------------------------------------------------------------------------
				//	이송대기인 경우 착지개소코드도 파라미터로 전달됨
				//---------------------------------------------------------------------------------------------
				recPara.setField("ARR_WLOC_CD", 	    ydDaoUtils.setDataDefault(inDto[ii].getField("ARR_WLOC_CD"),""));

				//---------------------------------------------------------------------------------------------
				//	작업재료리스트와 JMS_TC_CD
				//---------------------------------------------------------------------------------------------
				recPara.setField("STL_LIST", 	        ydDaoUtils.setDataDefault(inDto[ii].getField("STL_LIST"),""));
				recPara.setField("JMS_TC_CD", 	        ydDaoUtils.setDataDefault(inDto[ii].getField("JMS_TC_CD"),""));

				recPara.setField("YD_USER_ID", 	        ydDaoUtils.setDataDefault(inDto[ii].getField("YD_USER_ID"),""));

				//내부 Process 연결
				EJBConnector ejbConn = new EJBConnector("default", this);
				szRtnMsg = (String)ejbConn.trx("JPlateYdMvStkWrkSeEJB", "procPrepLotCompByCapa", recPara);

			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION ["+szOperationName+"] 끝  .. 결과 :: " + szRtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of insCncInWBook

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
			szRtnMsg = (String)ejbConn.trx("JPlateYdL3RcvSeEJB", "procPPGasCutResult", recPara);

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

	/**
	 * CNC모니터링_정정야드 재료 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdStockWithLoc(JDTORecord inDto) throws DAOException {

		JDTORecordSet		outRecSet	= JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JPlateYdStockDAO 	ydStockDAO	= new JPlateYdStockDAO();

		String szMsg        	= "";
		String szMethodName		= "getYdStockWithLoc";
		String szOperationName	= "정정야드 재료 조회";
		int intRtnVal 			= 0;

		try {

			szMsg = "JSP-SESSION ["+szOperationName+"] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			intRtnVal = ydStockDAO.getYdStockWithLoc(inDto, outRecSet);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				}
				return outRecSet;
			} // end of if
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return outRecSet;
	}// end of getYdStockWithLoc


	/**
	 *  CNC모니터링 : 스크랩처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String procCncScrap(JDTORecord[] inDto) throws DAOException {

		JDTORecordSet 	rsResult  	= null;
		JDTORecord 		recPara   	= null;
		JDTORecord 		tempRec		= null;
		JDTORecord 		recL2Para   = null;

		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMethodName		= "procCncScrap";
		String 	szOperationName 	= "CNC 스크랩처리";
		String	szMsg				= "";
		String 	szLogMsg 			= "";
		String	szStlNo				= "";
		String	szModifier			= "";

		String	szYdStkColGp		= "";
		String	szMtlStatCd     	= "";
		String	szYdStkLyrMtlStat	= "";

    	int 	intRtnVal 			= 0;

    	//DAO
    	JPlateYdStockDAO    ydStockDao   	= new JPlateYdStockDAO();
    	JPlateYdStkLyrDAO   ydStkLyrDao		= new JPlateYdStkLyrDAO();
    	JPlateYdWrkbookDAO	ydWrkbookDao	= new JPlateYdWrkbookDAO();
    	JPlateYdCrnSchDAO	ydCrnSchDao		= new JPlateYdCrnSchDAO();

		try {

			szLogMsg = "JSP-SESSION ["+szOperationName+"] 시작 :: " + inDto.length;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			for (int ii=0; ii<inDto.length; ii++) {

				szStlNo 	= ydDaoUtils.paraRecChkNull(inDto[ii], "STL_NO");
				szModifier 	= ydDaoUtils.paraRecModifier(inDto[ii]);

				szLogMsg = "JSP-SESSION ["+szOperationName+"] 시작 :: " + szStlNo;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				// ------------------------------------------------------------------------
				// 1. 재료정보 조회 : 재료진행상태가 '3'인 데이타만 스크랩 가능 , 현위치가 가스장일때만 가능
				// ------------------------------------------------------------------------
				rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
				tempRec  = JDTORecordFactory.getInstance().create();
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 		szStlNo);             	// 재료번호

				intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsResult);
				if (intRtnVal < 1) {
					szRtnMsg = "야드재료가 미존재 합니다 .... 재료번호:" + szStlNo;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				rsResult.first();
				tempRec = rsResult.getRecord();

				szYdStkColGp		= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_COL_GP");			// 야드적치열구분
				szMtlStatCd     	= ydDaoUtils.paraRecChkNull(tempRec, "MTL_STAT_CD");			// 재료상태코드
				szYdStkLyrMtlStat	= ydDaoUtils.paraRecChkNull(tempRec, "YD_STK_LYR_MTL_STAT");	// 재료적치상태

				if (!"3".equals(szMtlStatCd)) {
					szRtnMsg = "재료상태코드가 종료상태가 아닙니다.... 재료상태코드:" + szMtlStatCd;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				if (!"CN".equals(ydUtils.substr(szYdStkColGp, 2, 2))) {
					szRtnMsg = "재료의 현위치가 가스장이 아닙니다.... 저장위치:" + szYdStkColGp;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				if (!"C".equals(szYdStkLyrMtlStat)) {
					szRtnMsg = "현위치의 적치상태가 적치중이 아닙니다.... 재료적치상태:" + szYdStkLyrMtlStat;
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// ------------------------------------------------------------------------
				// 2.1. 작업예약 존재여부 확인
				// ------------------------------------------------------------------------
				rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 		szStlNo);             	// 재료번호
				recPara.setField("YD_GP",		JPlateYdConst.YD_GP_F_PLATE_YARD);

				intRtnVal = ydWrkbookDao.getExistByStlNo(recPara, rsResult);
				if (intRtnVal > 0) {
					szRtnMsg = "해당 재료로 작업예약이 존재!";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// ------------------------------------------------------------------------
				// 2.2. 크레인 작업지시 존재여부 확인
				// ------------------------------------------------------------------------
				rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 		szStlNo);             	// 재료번호
				recPara.setField("YD_GP",		JPlateYdConst.YD_GP_F_PLATE_YARD);

				intRtnVal = ydCrnSchDao.getExistByStlNo(recPara, rsResult);
				if (intRtnVal > 0) {
					szRtnMsg = "해당 재료로 크레인 작업지시 존재!";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// ------------------------------------------------------------------------
				// 2.3. 저장위치 변경이력 등록
				// ------------------------------------------------------------------------
				recPara.setField("YD_SCH_ST_GP", 	"M");		// 야드 스케줄기동 구분 (A:AUTO, M:MANUAL, B:BACK-UP)
				recPara.setField("YD_SCH_CD",		JPlateYdConst.SCH_CD_JPLATE_SCRAP);
				recPara.setField("MODIFIER", 		szModifier);

				szRtnMsg = this.insYdWrkHistPosFix(recPara);
				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					szMsg    = "["+szOperationName+"] 저장위치변경이력 등록 오류 >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// ------------------------------------------------------------------------
				// 3. 재료정보 삭제
				// ------------------------------------------------------------------------
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 		szStlNo);             	// 재료번호
				recPara.setField("MODIFIER", 	szModifier);

				intRtnVal = ydStockDao.delYdStock(recPara);
				if (intRtnVal < 0) {
					szRtnMsg = "재료정보 삭제처리시 오류 발생! .... 오류코드 :" + Integer.toString(intRtnVal);
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// ------------------------------------------------------------------------
				// 4. 저장위치 CLEAR
				// ------------------------------------------------------------------------
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 				szStlNo);             	// 재료번호
				recPara.setField("YD_STK_LYR_MTL_STAT", "C");             		// 야드적치단재료상태
				recPara.setField("YD_GP",				JPlateYdConst.YD_GP_F_PLATE_YARD);
				recPara.setField("MODIFIER", 			szModifier);

				intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
				if (intRtnVal < 0) {
					szRtnMsg = "저장위치 삭제 처리시 오류 발생! .... 오류코드 :" + Integer.toString(intRtnVal);
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
			}

			// ------------------------------------------------------------------------
			// 6. 야드L2 저장품제원 정보 송신 [YDY7L002]
			// ------------------------------------------------------------------------
			for (int ii=0; ii<inDto.length; ii++) {

				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				szStlNo = ydDaoUtils.paraRecChkNull(inDto[ii], "STL_NO");

	        	recL2Para = JDTORecordFactory.getInstance().create();
	        	recL2Para.setField("JMS_TC_CD", 		"YDY7L002");                            // TC-CODE
	        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);		// 야드구분
	        	recL2Para.setField("YD_STK_COL_GP", 	"");                          			// 야드적치열구분
	        	recL2Para.setField("YD_STK_BED_NO", 	"");    								// 야드적치BED번호
	        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
	        	recL2Para.setField("STL_NO", 			szStlNo);	        					// 재료번호
	        	recL2Para.setField("MSG_GP", 			"D");	        						// 전문구분
	        	recL2Para.setField("DEL_YN_CHECK",		"N");									// 삭제된 데이타도 조회하도록 처리

	        	szRtnMsg = ydDelegate.sendMsg(recL2Para);

				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION ["+szOperationName+"] 끝  .. 결과 :: " + szRtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of insCncOutWBook


	/**
	 *  저장위치변경이력 등록 [저장위치수정, 스크랩처리]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String insYdWrkHistPosFix(JDTORecord inDto) throws DAOException {

		JPlateYdStockDAO	ydStockDao		= new JPlateYdStockDAO();
//		JPlateYdSchRuleDAO 	ydSchRuleDao 	= new JPlateYdSchRuleDAO();
    	JPlateYdWrkHistDAO 	ydWrkHistDao	= new JPlateYdWrkHistDAO();

		JDTORecordSet	rsStockInfo	= null;

		JDTORecord recPara 			= JDTORecordFactory.getInstance().create();

		JDTORecord recStockInfo 	= JDTORecordFactory.getInstance().create();
//		JDTORecord recWrkHistPara 	= JDTORecordFactory.getInstance().create();
//		JDTORecord recWrkHistInfo 	= JDTORecordFactory.getInstance().create();
//		JDTORecordSet outRecSet 	= null;

		String 	szRtnStr 			= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMethodName		= "insYdWrkHistPosFix";
		String 	szOperationName 	= "저장위치변경이력 등록";
		String 	szLogMsg 			= "";
		int		intRtnVal			= 0;

		String	szStlNo				= "";
		String	szYdUpWrLoc   		= "";
		String	szYdUpWrLayer 		= "";
		String	szYdDnWrLoc			= "";
		String	szYdDnWrLayer		= "";
		String	szYdGp				= "";
		String	szModifier			= "";
		String	szYdSchStGp			= "";
		String	szYdSchCd			= "";
		String	szYdEqpId			= "";

		try {

			szYdGp			= ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			szStlNo			= ydDaoUtils.paraRecChkNull(inDto, "STL_NO");
			szYdUpWrLoc 	= ydDaoUtils.paraRecChkNull(inDto, "YD_UP_WR_LOC");			// 권상실적위치
			szYdUpWrLayer 	= ydDaoUtils.paraRecChkNull(inDto, "YD_UP_WR_LAYER");		// 권상실적단
			szYdDnWrLoc		= ydDaoUtils.paraRecChkNull(inDto, "YD_DN_WR_LOC");			// 권하실적위치
			szYdDnWrLayer	= ydDaoUtils.paraRecChkNull(inDto, "YD_DN_WR_LAYER");		// 권하실적단
			szYdSchCd		= ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_CD");
			szYdSchStGp		= ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_ST_GP", 	"B");
			szModifier 		= ydDaoUtils.paraRecModifier(inDto);

			//---------------------------------------
			// 이적작업  이력정보에 추가
			//---------------------------------------
			rsStockInfo = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", szStlNo);

			intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsStockInfo);		// intGp == 0

			// 재료 정보 이력정보에 추가
			if (intRtnVal > 0) {
				//STOCK 정보가 존재할 경우
				rsStockInfo.first();
				recStockInfo  = rsStockInfo.getRecord();

				if ("".equals(szYdUpWrLoc)) {
					szYdUpWrLoc   = ydDaoUtils.paraRecChkNull(recStockInfo, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recStockInfo, "YD_STK_BED_NO");
					szYdUpWrLayer = ydDaoUtils.paraRecChkNull(recStockInfo, "YD_STK_LYR_NO");
				}

				if ("".equals(szYdGp)) {
					szYdGp = ydUtils.substr(ydDaoUtils.paraRecChkNull(recStockInfo, "YD_STK_COL_GP"), 0, 1);
				}

			} else {
				// 재료미존재시는 Error 처리 .. 이함수 호출전에 미리 Insert함
				szRtnStr = "야드재료 정보가 존재하지 않습니다. >>>>" + szStlNo;
				szLogMsg = "JSP-SESSION "+szOperationName+"]" + szRtnStr;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
			//	return szRtnStr;
			}

			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, ">>>>변경전 저장위치 ::"+szYdUpWrLoc+"-"+szYdUpWrLayer, JPlateYdConst.DEBUG);
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, ">>>>변경후 저장위치 ::"+szYdDnWrLoc+"-"+szYdDnWrLayer, JPlateYdConst.DEBUG);

			// 작업이력 - 권상정보 관련 입력
			if (!"".equals(szYdUpWrLoc)) {
				recStockInfo.setField("YD_UP_WR_LOC", 	szYdUpWrLoc);
				recStockInfo.setField("YD_UP_WR_LAYER", szYdUpWrLayer);
				recStockInfo.setField("YD_UP_CMPL_DT",	JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));
			} else {
				recStockInfo.setField("YD_UP_WR_LOC", 	"");
				recStockInfo.setField("YD_UP_WR_LAYER", "");
				recStockInfo.setField("YD_UP_CMPL_DT",	"");
			}

			if ("".equals(szYdGp)) {
				szYdGp = JPlateYdConst.YD_GP_F_PLATE_YARD;
			}
			recStockInfo.setField("YD_GP", 			szYdGp);
			recStockInfo.setField("STL_NO", 		szStlNo);

			// 작업이력 - 권하정보 입력
			recStockInfo.setField("YD_DN_WR_LOC",   szYdDnWrLoc);
			recStockInfo.setField("YD_DN_WR_LAYER", szYdDnWrLayer);
			recStockInfo.setField("YD_DN_CMPL_DT", 	JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));

			// 야드보조작업여부 - YD_AID_WRK_YN
			recStockInfo.setField("YD_AID_WRK_YN" , "N");
/*
			if (!"".equals(szYdSchCd)) {
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("outRecSet");
				//해당 스케줄 정보로 주작업 크레인 정보를 가지고 온다.
				recWrkHistPara.setField("YD_SCH_CD", 	szYdSchCd);
				intRtnVal = ydSchRuleDao.getYdSchrule(recWrkHistPara, outRecSet);		// intGp == 0

				if (intRtnVal > 0) {
					outRecSet.first();
					recWrkHistInfo = outRecSet.getRecord();
				} else if (intRtnVal == 0) {
					szLogMsg = "JSP-SESSION "+szOperationName+"] 해당 스케줄에 대한 기준정보가 존재 하지 않습니다.["+ szYdSchCd +"]" ;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
				}

				szYdEqpId = ydDaoUtils.paraRecChkNull(recWrkHistInfo, "YD_WRK_CRN");
			}
*/
			if ("".equals(szYdSchCd)) {
			//	szYdSchCd = szYdGp + "XYDYDMM";		// FXYD01MM : 저장위치 수정 , FXYD02MM : 저장위치 삭제 , FXCN02MM : 스크랩처리
				if ("".equals(szYdDnWrLoc)) {
					szYdSchCd = JPlateYdConst.SCH_CD_JPLATE_LOC_DEL;
				} else {
					szYdSchCd = JPlateYdConst.SCH_CD_JPLATE_LOC_MOD;
				}
			}
			// 야드 스케줄 코드 - YD_SCH_CD
			recStockInfo.setField("YD_SCH_CD", 		szYdSchCd);

			//스케줄 기준의 작업 크레인 정보를 넣어준다.
			recStockInfo.setField("YD_EQP_ID", 		szYdEqpId);
			recStockInfo.setField("YD_GNT_GP", 		JPlateYdConst.YD_GNT_GP_MVSTK);
			recStockInfo.setField("YD_SCH_ST_GP", 	szYdSchStGp);							// 야드스케줄 기동 구분
	        recStockInfo.setField("YD_WRK_HDS_DD",  JPlateYdUtils.getDefaultHdsDate());		// 계상일자
	        recStockInfo.setField("YD_WRK_DUTY",	JPlateYdUtils.getDefaultDuty());		// 작업근

			recStockInfo.setField("REGISTER", 		szModifier);
			recStockInfo.setField("MODIFIER", 		szModifier);

			// 이력정보 남기기
			intRtnVal = ydWrkHistDao.insYdWrkHist(recStockInfo);

			if (intRtnVal > 0) {
				szLogMsg = "JSP-SESSION "+szOperationName+"] 이력정보를 로깅하였습니다." ;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			} else {
				szRtnStr = "이력정보를 로깅 실패 하였습니다" + Integer.toString(intRtnVal);
				szLogMsg = "JSP-SESSION "+szOperationName+"] " + szRtnStr;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
				return szRtnStr;
			}

			//------------------------------------------------------------------
			// FROM 위치가 대차일 경우 YD_WRK_PLAN_TCAR(야드작업계획대차) 항목  CLEAR 처리
			//------------------------------------------------------------------
			if ("TC".equals(ydUtils.substr(szYdUpWrLoc, 2, 2)) && !"TC".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO",         		szStlNo);
				recPara.setField("YD_WRK_PLAN_TCAR", 	"");
				recPara.setField("REGISTER",       		szModifier);
				recPara.setField("MODIFIER",       		szModifier);

	    		intRtnVal = ydStockDao.updYdWrkPlanTcar(recPara);
	    		if (intRtnVal < 1) {
	    			szLogMsg = "야드작업계획대차 항목 CLEAR 실패 >>>> " + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
	    		}
			}

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION ["+szOperationName+"] 끝  .. 결과 :: " + szRtnStr;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}

	/**
	 *  작업예약등록(보수장보급)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String insBsInWBook(JDTORecord[] inDto) throws DAOException {

		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();

		String szRtnMsg 		= JPlateYdConst.RETN_CD_SUCCESS;
		String szMethodName		= "insBsInWBook";
		String szOperationName 	= "작업예약등록(보수장보급)";
		String szLogMsg 		= "";

		try {

			szLogMsg = "JSP-SESSION ["+szOperationName+"] 시작 :: " + inDto.length;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			for (int ii=0; ii<inDto.length; ii++) {

				// 해당 스케줄 기준정보에 야드 스케줄 금지유무를 "Y" Setting 한다.
				recPara.setField("YD_GP", 	          	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_GP"),""));
				recPara.setField("YD_BAY_GP", 	      	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_BAY_GP"),""));
				recPara.setField("YD_SPAN_GP", 	      	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_SPAN_GP"),""));
				recPara.setField("YD_AIM_RT_GP", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_AIM_RT_GP"),""));
			//	recPara.setField("YD_MAIN_WRK_GP", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_MAIN_WRK_GP"),""));
			//  주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차
				recPara.setField("YD_MAIN_WRK_GP", 	  	JPlateYdConst.YD_MAIN_WRK_GP_BS_IN);
				recPara.setField("YD_TO_LOC_GUIDE_GP",	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_TO_LOC_GUIDE_GP"),""));
				recPara.setField("YD_AIM_YD_GP", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_AIM_YD_GP"),""));
				recPara.setField("YD_AIM_BAY_GP", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_AIM_BAY_GP"),""));
				recPara.setField("YD_AIM_SPAN_GP", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_AIM_SPAN_GP"),""));
				recPara.setField("YD_AIM_COL_GP", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_AIM_COL_GP"),""));
				recPara.setField("YD_AIM_BED_NO", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_AIM_BED_NO"),""));
				recPara.setField("YD_STK_COL_GP", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_STK_COL_GPS"),""));
				recPara.setField("YD_STK_BED_NO", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_STK_BED_NOS"),""));
				recPara.setField("YD_TC_GP", 	  		ydDaoUtils.setDataDefault(inDto[ii].getField("YD_TC_GP"),""));

				//---------------------------------------------------------------------------------------------
				//	후판정정의 준비스케줄 등록 시 지정한 작업매수의 값을 전달하여 해당하는 매수만큼 작업예약에 등록
				//---------------------------------------------------------------------------------------------
				recPara.setField("YD_EQP_WRK_SH", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_EQP_WRK_SH"),""));

				//---------------------------------------------------------------------------------------------
				//	이송대기인 경우 착지개소코드도 파라미터로 전달됨
				//---------------------------------------------------------------------------------------------
				recPara.setField("ARR_WLOC_CD", 	    ydDaoUtils.setDataDefault(inDto[ii].getField("ARR_WLOC_CD"),""));

				//---------------------------------------------------------------------------------------------
				//	작업재료리스트와 JMS_TC_CD
				//---------------------------------------------------------------------------------------------
				recPara.setField("STL_LIST", 	        ydDaoUtils.setDataDefault(inDto[ii].getField("STL_LIST"),""));
				recPara.setField("JMS_TC_CD", 	        ydDaoUtils.setDataDefault(inDto[ii].getField("JMS_TC_CD"),""));

				recPara.setField("YD_USER_ID", 	        ydDaoUtils.setDataDefault(inDto[ii].getField("YD_USER_ID"),""));

				//내부 Process 연결
				EJBConnector ejbConn = new EJBConnector("default", this);
				szRtnMsg = (String)ejbConn.trx("JPlateYdMvStkWrkSeEJB", "procPrepLotCompByCapa", recPara);

			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION ["+szOperationName+"] 끝  .. 결과 :: " + szRtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of insBsInWBook

	/**
	 *  작업예약등록(보수장추출)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String insBsOutWBook(JDTORecord inDto) throws DAOException {

		// DAO 객체 생성
		JPlateYdStockDAO 	ydStockDao	= new JPlateYdStockDAO();

		JDTORecordSet 		rsYdStock   = null;
		JDTORecord 			recPara 	= null;
		JDTORecord			recMtl		= null;

		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMethodName		= "insBsOutWBook";
		String 	szOperationName 	= "작업예약등록(보수장추출)";
		String 	szLogMsg 			= "";
		String	szArrStlNo			= "";
		String	szModifier			= "";
		int		intRtnVal			= 0;

		try {

			szArrStlNo  = ydDaoUtils.paraRecChkNull(inDto, "ARR_STL_NO");
			szModifier 	= ydDaoUtils.paraRecModifier(inDto);

			String[] arrStlNo = szArrStlNo.split(";");

			szLogMsg = "JSP-SESSION ["+szOperationName+"] 시작 :: " + szArrStlNo;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			// 후판조업 차행선결정정보 수신 (PPYDJ015)
			// 보수완료실적(검사실적) 등록 및 추출스케줄 기동
			EJBConnector ejbConn = new EJBConnector("default", this);

			for (int ii=0; ii<arrStlNo.length; ii++) {

				rsYdStock 	= JDTORecordFactory.getInstance().createRecordSet("ydTemp");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO",		arrStlNo[ii]);

				intRtnVal 	= ydStockDao.getYdStockWithLoc(recPara, rsYdStock);

				if (intRtnVal != 1) {
					szRtnMsg = "재료정보 미존재 :: " + arrStlNo[ii];
					szLogMsg = "[" + szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				} else {

					recMtl = JDTORecordFactory.getInstance().create();
					recMtl = rsYdStock.getRecord(0);

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO",				arrStlNo[ii]);
					recPara.setField("US_MAINTMATL",		ydDaoUtils.paraRecChkNull(recMtl, "US_MAINTMATL"));			// 상면보수재
					recPara.setField("LS_MAINTMATL",		ydDaoUtils.paraRecChkNull(recMtl, "LS_MAINTMATL"));			// 하면보수재
					recPara.setField("CPL_WRK_MTL",			ydDaoUtils.paraRecChkNull(recMtl, "CPL_WRK_MTL"));			// 냉간교정재
					recPara.setField("HTTRT_HPL_MTL",		ydDaoUtils.paraRecChkNull(recMtl, "HTTRT_HPL_MTL"));		// 열처리교정재
					recPara.setField("GAS_WRK_MTL",			ydDaoUtils.paraRecChkNull(recMtl, "GAS_WRK_MTL"));			// GAS작업재
					recPara.setField("SHOT_BLST_WRK_MTL",	ydDaoUtils.paraRecChkNull(recMtl, "SHOT_BLST_WRK_MTL"));	// ShortBlast작업재
					recPara.setField("PRESS_WRK_MTL",		ydDaoUtils.paraRecChkNull(recMtl, "PRESS_WRK_MTL"));		// 프레스교정재
					recPara.setField("PL_WR_PRSNT_PROC_CD",	ydDaoUtils.paraRecChkNull(recMtl, "PL_WR_PRSNT_PROC_CD"));	// 후판실적현공정코드
					recPara.setField("GDS_MAIN_GRD",		ydDaoUtils.paraRecChkNull(recMtl, "GDS_MAIN_GRD"));			// 제품주등급
					recPara.setField("MODIFIER", 			szModifier);

					szRtnMsg = (String)ejbConn.trx("JPlateYdL3RcvSeEJB", "procPPNextDeciInfo", recPara);

					if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
						szLogMsg = "JSP-SESSION ["+szOperationName+"] 호출결과 :: " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
						return szRtnMsg;
					}
				}
			}

			/* procPPNextDeciInfo :: 차행선결정정보 수신 (PPYDJ015) 에서 처리하도록 보완하여 ... 주석처리함
			//---------------------------------
			// 보수장 추출 스케줄 기동
			//---------------------------------
			szRtnMsg = (String)ejbConn.trx("JPlateYdL3RcvSeEJB", "procBsOut", inDto);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szLogMsg = "JSP-SESSION ["+szOperationName+"] 보수장 추출 스케줄 기동 결과 :: " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				return szRtnMsg;
			}
			*/

		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION ["+szOperationName+"] 끝  .. 결과 :: " + szRtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of insBsOutWBook

	/**
	 * 대차스케줄삭제 - 대차초기화
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String delTcarSch(JDTORecord[] inDto) throws DAOException {

		String 	szMethodName 			= "delTcarSch";
		String 	szOperationName 		= "대차스케줄삭제:대차초기화";
		String 	szMsg 					= "";
		String 	szRtnMsg 				= JPlateYdConst.RETN_CD_SUCCESS;
		int		intRtnVal				= -100;

		JPlateYdTcarSchDAO		ydTcarSchDao		= new JPlateYdTcarSchDAO();
		JPlateYdEqpDAO			ydEqpDao			= new JPlateYdEqpDAO();
		JPlateYdTcarFtmvMtlDAO	ydTcarFtmvMtlDao	= new JPlateYdTcarFtmvMtlDAO();
		JPlateYdCrnSchDAO		ydCrnSchDao			= new JPlateYdCrnSchDAO();
		JPlateYdStkLyrDAO		ydStkLyrDao			= new JPlateYdStkLyrDAO();

		String 	szYdEqpId			= "";
		String 	szModifier			= "";
		String 	szYdTcarSchId		= "";
		String	szYdGp				= "";
		String	szYdSchCd			= "";
		String 	szYdHomeBayGp  		= "";			// 야드Home동구분
    	String	szYdCurrBayGp		= "";			// 현재동
    	String	szLdBay				= "";			// 상차동
    	String	szUdBay				= "";			// 하차동
		String 	szYdCarldStopLoc	= "";			// 야드상차정지위치
		String	szYdStkColGp 		= "";
		String	szYdEqpBayGp 		= "";
		String 	szStlNo 			= "";
		String	szYdUpWrLoc			= "";
		String	szYdUpWrLayer		= "";

		int		iHistInsCnt			= 0;			// 저장위치 변경이력 등록 건수

		JDTORecord		recPara		= null;
		JDTORecord		recTemp		= null;
		JDTORecordSet	rsResult	= null;

    	EJBConnector 	ejbConn 	= null;

		try {

			szMsg = "[Jsp-Session : "+szOperationName + "] ------------------ 메소드 시작 ------------------";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			recPara = JDTORecordFactory.getInstance().create();

			for(int ii=0; ii<inDto.length; ii++){
		    	//-------------------------------------------------------------
		    	// 0. 현재 위치로 크레인 작업지시 생성시에는 대차 초기화 처리 못하도록 체크
		    	//-------------------------------------------------------------
				szYdEqpId = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_EQP_ID");
		    	szYdSchCd = szYdGp + "_" + ydUtils.substr(szYdEqpId, 2, 4) + "_M";

		    	rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
		    	recPara  = JDTORecordFactory.getInstance().create();
		    	recPara.setField("YD_SCH_CD", szYdSchCd);

		    	intRtnVal = ydCrnSchDao.getByYdSchCd(recPara, rsResult);
		    	if (intRtnVal > 0) {
			    	szRtnMsg = "크레인 작업지시가 존재하여 초기화처리 불가합니다.!";
			    	szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
		    		return szRtnMsg;
		    	}
			}

			for(int ii=0; ii<inDto.length; ii++){

				szYdEqpId		= ydDaoUtils.paraRecChkNull(inDto[ii], "YD_EQP_ID");
		    	szYdCurrBayGp	= ydDaoUtils.paraRecChkNull(inDto[ii], "YD_CURR_BAY_GP");		// 현재동
		    	szLdBay			= ydDaoUtils.paraRecChkNull(inDto[ii], "LD_BAY");				// 상차동
		    	szUdBay			= ydDaoUtils.paraRecChkNull(inDto[ii], "UD_BAY");				// 하차동
				szModifier		= ydDaoUtils.paraRecModifier(inDto[ii]);
		    	szYdGp 			= ydUtils.substr(szYdEqpId, 0, 1);

		    	szMsg = "[" + szOperationName + "] 대차ID::" + szYdEqpId + " , 현재동 :: " + szYdCurrBayGp + " , 상차동 :: " + szLdBay + " , 하차동 :: " + szUdBay;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				//--------------------------------------------------------------------------------------------------------
				//	대차설비로 대차스케줄 조회
				//--------------------------------------------------------------------------------------------------------
				szMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYdEqpId+"]에 해당하는 대차스케줄 조회 시작";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				rsResult = JDTORecordFactory.getInstance().createRecordSet("");

				recPara.setField("YD_EQP_ID", szYdEqpId);

				intRtnVal = ydTcarSchDao.getByYdEqpId(recPara, rsResult);		// intGp == 4
				if (intRtnVal <= 0) {
					szMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYdEqpId+"]에 해당하는 대차스케줄 조회 시 오류발생 - 루프반복 >>>> " + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				//	continue;
				} else {

					szMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYdEqpId+"]에 해당하는 대차스케줄 조회 완료 - 대상재건수["+rsResult.size()+"]";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					rsResult.first();
					recTemp = rsResult.getRecord();

					szYdTcarSchId = ydDaoUtils.paraRecChkNull(recTemp, "YD_TCAR_SCH_ID");
				}

				//--------------------------------------------------------------------------------------------------------
				//	대차설비정보조회
				//--------------------------------------------------------------------------------------------------------
				szMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYdEqpId+"]로 설비TABLE 조회 시작";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult);				// intGp == 0

				if (intRtnVal <= 0) {
					szRtnMsg = "대차설비ID["+szYdEqpId+"]로 설비TABLE 조회 시 오류발생 >>>> " + Integer.toString(intRtnVal);
					szMsg = "[Jsp-Session : "+szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					throw new DAOException(szRtnMsg);
				}

				szMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYdEqpId+"]로 설비TABLE 조회 완료 - 대상재건수["+rsResult.size()+"]";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				rsResult.first();
				recTemp = rsResult.getRecord();

				// 2013.05.17 - 대차의 현재동을 Home 동으로 변경
				szYdEqpBayGp  = ydDaoUtils.paraRecChkNull(recTemp, "YD_CURR_BAY_GP");		// 야드현재동구분
				szYdHomeBayGp = ydDaoUtils.paraRecChkNull(recTemp, "YD_HOME_BAY_GP");		// 야드Home동구분
				szYdCurrBayGp = szYdHomeBayGp;

				//--------------------------------------------------------------------------------------------------------
				//	대차스케줄이 존재하면 대차이송재료 삭제
				//--------------------------------------------------------------------------------------------------------
				szMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYdEqpId+"]에 해당하는 대차스케줄["+szYdTcarSchId+"]의 대차이송재료 조회 시작";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recTemp  = JDTORecordFactory.getInstance().create();
				recTemp.setField("YD_TCAR_SCH_ID", 			szYdTcarSchId);

				intRtnVal = ydTcarFtmvMtlDao.getByYdTcarSchId(recTemp, rsResult);		// intGp == 1

				if (intRtnVal > 0) {

					szMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYdEqpId+"]에 해당하는 대차스케줄["+szYdTcarSchId+"]의 대차이송재료 조회 완료 - 대상재건수["+rsResult.size()+"]";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					for(int jj=1; jj<=rsResult.size(); jj++) {

						rsResult.absolute(jj);
						recTemp = rsResult.getRecord();

						szMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYdEqpId+"]에 해당하는 대차스케줄["+szYdTcarSchId+"]의 대차이송재료["+ydDaoUtils.paraRecChkNull(recTemp, "STL_NO")+"] 삭제 시작";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						recTemp.setField("DEL_YN", 		"Y");
						recTemp.setField("MODIFIER", 	szModifier);

						intRtnVal = ydTcarFtmvMtlDao.delYdTcarFtmvMtl(recTemp);

						if (intRtnVal <= 0) {
							szRtnMsg = "대차설비ID["+szYdEqpId+"]에 해당하는 대차스케줄["+szYdTcarSchId+"]의 대차이송재료["+ydDaoUtils.paraRecChkNull(recTemp, "STL_NO")+"] 삭제 시 오류발생";
							szMsg    = "[Jsp-Session : "+szOperationName + "] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							throw new DAOException(szRtnMsg);
						}

						szMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYdEqpId+"]에 해당하는 대차스케줄["+szYdTcarSchId+"]의 대차이송재료["+ydDaoUtils.paraRecChkNull(recTemp, "STL_NO")+"] 삭제 성공";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					}
				}

				szMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYdEqpId+"]에 해당하는 대차스케줄["+szYdTcarSchId+"]의 대차이송재료 조회 완료";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				//--------------------------------------------------------------------------------------------------------
				//	대차스케줄 삭제
				//--------------------------------------------------------------------------------------------------------
				szMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYdEqpId+"]와 관련된 대차스케줄 삭제 시작";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				recPara.setField("YD_EQP_ID",	szYdEqpId);
				recPara.setField("MODIFIER", 	szModifier);

				intRtnVal = ydTcarSchDao.delYdTcarsch(recPara);			// intGp == 2

				szMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYdEqpId+"]와 관련된 대차스케줄 삭제 완료 - 메세지 : " + intRtnVal;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				//--------------------------------------------------------------------------------------------------------
				//	저장위치 변경이력 등록
				//--------------------------------------------------------------------------------------------------------
				iHistInsCnt = 0;
				szYdStkColGp = szYdGp + szYdEqpBayGp + ydUtils.substr(szYdEqpId, 2, 4);

				szMsg = "[Jsp-Session : "+szOperationName + "] 대차 저장위치 변경이력 등록 시작 - 조건 : " + szYdStkColGp;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		    	rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP", 			szYdStkColGp);
				recPara.setField("YD_STK_LYR_MTL_STAT1", 	"C");
				recPara.setField("YD_STK_LYR_MTL_STAT2", 	"U");

				intRtnVal = ydStkLyrDao.getByLocMtlStat(recPara, rsResult);
				if (intRtnVal > 0) {
					rsResult.first();
					for(int jj=1; jj<=rsResult.size(); jj++) {

						recTemp = JDTORecordFactory.getInstance().create();

						rsResult.absolute(jj);
						recTemp = rsResult.getRecord();

						szStlNo = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
						if (!"".equals(szStlNo)) {
							szYdUpWrLoc		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_NO");
							szYdUpWrLayer	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");

							recPara  = JDTORecordFactory.getInstance().create();
							recPara.setField("YD_GP",			JPlateYdConst.YD_GP_F_PLATE_YARD);		// 2후판정정야드
							recPara.setField("STL_NO", 			szStlNo);             					// 재료번호
							recPara.setField("YD_UP_WR_LOC", 	szYdUpWrLoc);             				// 권상실적위치
							recPara.setField("YD_UP_WR_LAYER", 	szYdUpWrLayer);             			// 권상실적단
							recPara.setField("YD_DN_WR_LOC", 	"");             						// 권하실적위치
							recPara.setField("YD_DN_WR_LAYER", 	"");             						// 권하실적단
							recPara.setField("YD_SCH_ST_GP", 	"B");									// 야드 스케줄기동 구분 (A:AUTO, M:MANUAL, B:BACK-UP)
							recPara.setField("YD_SCH_CD",		JPlateYdConst.SCH_CD_JPLATE_LOC_INIT);	// 대차 초기화
							recPara.setField("MODIFIER", 		szModifier);

							szRtnMsg = this.insYdWrkHistPosFix(recPara);
							if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
								szMsg    = "["+szOperationName+"] 저장위치변경이력 등록 오류 >>>> " + szRtnMsg;
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
								return szRtnMsg;
							}
							iHistInsCnt ++;
						}
					}
				}

				szMsg = "[Jsp-Session : "+szOperationName + "] 대차 저장위치 변경이력 등록 완료 - 건수 : " + Integer.toString(iHistInsCnt);
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				//--------------------------------------------------------------------------------------------------------
				//	대차의 저장위치 적치정보 초기화
				//--------------------------------------------------------------------------------------------------------
				szMsg = "[Jsp-Session : "+szOperationName + "] 대차 저장위치 적치 정보 CLEAR 시작 - 조건 : " + szYdStkColGp;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("MODIFIER", 		szModifier);
				recPara.setField("YD_STK_COL_GP", 	szYdStkColGp);
				recPara.setField("YD_STK_BED_NO", 	"");
				recPara.setField("YD_STK_LYR_NO", 	"");
				recPara.setField("OCPY_CHK_FLAG",	"N");
				intRtnVal = ydStkLyrDao.updYdStklyrClear(recPara);

				szMsg = "[Jsp-Session : "+szOperationName + "] 대차 저장위치 적치 정보 CLEAR 완료 - 메세지 : " + intRtnVal;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);


				//--------------------------------------------------------------------------------------------------------
				//	사용자가 지정한 현재동정보를 사용해서 대차스케줄 초기값으로 생성
				//--------------------------------------------------------------------------------------------------------
				szMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYdEqpId+"]의 사용자지정 현재동 정보[" + szYdCurrBayGp + "]로 대차스케줄 초기생성 시작";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				szYdCarldStopLoc = szYdEqpId.substring(0, 1) + szYdCurrBayGp + ydUtils.substr(szYdEqpId, 2, 4);
				szYdTcarSchId	 = ydTcarSchDao.getSeqId();

				recTemp	= JDTORecordFactory.getInstance().create();
				recTemp.setField("YD_TCAR_SCH_ID", 			szYdTcarSchId);					//대차스케줄ID
				recTemp.setField("REGISTER", 				szModifier);					//등록자
				recTemp.setField("YD_CAR_PROG_STAT", 		"0");							//차량진행상태
				recTemp.setField("YD_CARLD_SCH_REQ_GP", 	"6");							//상차스케줄요청구분
				recTemp.setField("YD_CARUD_SCH_REQ_GP", 	"3");							//하차스케줄요청구분
				recTemp.setField("YD_EQP_WRK_STAT", 		"U");							//설비작업상태 - 공차
				recTemp.setField("YD_EQP_ID", 				szYdEqpId);						//야드설비ID
				recTemp.setField("YD_CARLD_STOP_LOC",		szYdCarldStopLoc);				//야드상차정지위치

				intRtnVal = ydTcarSchDao.insYdTcarsch(recTemp);

				if (intRtnVal <= 0 ) {
					szMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYdEqpId+"]의 사용자지정 현재동 정보[" + szYdCurrBayGp + "]로 대차스케줄["+szYdTcarSchId+"] 초기생성 시 오류발생";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					throw new DAOException(szMsg);
				}

				szMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYdEqpId+"]의 사용자지정 현재동 정보[" + szYdCurrBayGp + "]로 대차스케줄["+szYdTcarSchId+"] 초기생성 완료";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				//--------------------------------------------------------------------------------------------------------
				//	해당설비의 HOME 동으로 대차 도착처리 수행
				//--------------------------------------------------------------------------------------------------------
				szMsg = "[Jsp-Session : "+szOperationName + "] 대차도착처리 호출 .. START >>>> ";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_EQP_ID",		szYdEqpId);
				recPara.setField("YD_CURR_BAY_GP",	szYdCurrBayGp);		// 현재동
				recPara.setField("LD_BAY",			"");				// 상차동
				recPara.setField("UD_BAY",			"");				// 하차동
				recPara.setField("MODIFIER",		szModifier);

				ejbConn  = new EJBConnector("default", this);
				szRtnMsg = (String)ejbConn.trx("JPlateYdTcarSchSeEJB", "procY7TcarStop", recPara);

				szMsg = "[Jsp-Session : "+szOperationName + "] 대차도착처리 호출 .. END >>>> " + szRtnMsg;
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			} // end of For Loop

		} catch(DAOException e) {
			szMsg = "[Jsp-Session : "+szOperationName + "] 예외발생[1] : " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw e;

		} catch(Exception e) {
			szMsg = "[Jsp-Session : "+szOperationName + "] 예외발생[2] : " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw new DAOException(szMsg);
		}

		// 4. 작업완료 유무를 RETURN 한다.
		szMsg = "[Jsp-Session : "+szOperationName + "] ------------------ 메소드 끝 ------------------";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of delTcarSch

	/**
	 *  대차 작업 취소
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String tCarCancleWBook(JDTORecord[] inDto) throws DAOException {
		/*
		 * 1. 해당 대차작업의  작업예약 정보가 크레인 스케줄에 편성되어 있는지 판단한다.
		 * 2. 크레인 스케줄 정보에 편성되어 있으면 작업취소할수 없다고 판단하고 Exception (작업취소 불가) 라고 RETURN 한다.
		 * 3. 크레인 스케줄 정보에 편성되지 않았을 경우에는 작업예약 및 작업 예약재료를 삭제 (DEL_YN = 'Y' SETTING) 한다.
		 * 4. 작업완료 유무를 RETURN 한다.
		 */

		int 	nRtnVal 			= 0;
		String 	szMethodName 		= "tCarCancleWBook";
		String 	szOperationName 	= " 대차 작업 취소";
		String 	szMsg 				= "";
		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szWBookId 			= null;
		String 	szYdSchCd 			= null;
		String	szMODIFIER 			= null;

		JDTORecord 		recGetPara 	= null;
		JDTORecord 		recPara 	= null;
		JDTORecord 		recTcarInfo = null;
		JDTORecordSet 	outRecSet 	= null;

		JPlateYdWrkbookDAO 		ydWrkbookDao 	= new JPlateYdWrkbookDAO();
		JPlateYdWrkbookMtlDAO 	ydWrkbookMtlDao = new JPlateYdWrkbookMtlDAO();
		JPlateYdCrnSchDAO 		ydCrnSchDao 	= new JPlateYdCrnSchDAO();
		JPlateYdTcarSchDAO 		ydTcarSchDao  	= new JPlateYdTcarSchDAO();

		try {

			szMsg = "[Jsp-Session] "+szOperationName + ">>>> 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			for(int ii=0; ii<inDto.length; ii++){

				//  1. 해당 대차작업의  작업예약 정보가 크레인 스케줄에 편성되어 있는지 판단한다.
				recGetPara = JDTORecordFactory.getInstance().create();
				recPara.setRecord(inDto[ii]);
				szWBookId  = ydDaoUtils.paraRecChkNull(recGetPara, "YD_WBOOK_ID");
				szMODIFIER = ydDaoUtils.paraRecModifier(recGetPara);

				if ("".equals(szWBookId.trim())) {
					//대차 작업 의 예약 정보가 올바르지 않을경우
					szRtnMsg = "작업예약정보가 맞지않습니다.";
					szMsg    = "["+szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

					//DAO Exception 발생해야할지 판단해야함
					return szRtnMsg;
				}

				// 2010.01.08
				// A. 해당 작업 스케줄 코드가 상차작업인지 하차작업인지 판단한다.
				szYdSchCd = ydDaoUtils.paraRecChkNull(recGetPara, "YD_SCH_CD");

				szMsg = "[Jsp-Session] "+szOperationName + ": 스케줄 코드. [" + szYdSchCd +"]";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				if ("".equals(szYdSchCd) || szYdSchCd.length() != 8){
					szRtnMsg = "스케줄 코드가 올바르지 않습니다. [" + szYdSchCd +"]";
					szMsg    = "["+szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				outRecSet   = JDTORecordFactory.getInstance().createRecordSet("");
				recPara     = JDTORecordFactory.getInstance().create();
				recTcarInfo = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(recGetPara, "YD_WRK_PLAN_TCAR"));

				nRtnVal = ydTcarSchDao.getByYdEqpId(recPara, outRecSet);		// intGp == 4

				if (nRtnVal < 0) {
					szRtnMsg = "해당 대차 스케줄 조회시 ERROR.";
					szMsg    = "["+szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					//DAO Exception 발생해야할지 판단해야함
					return szRtnMsg;
				} else if (nRtnVal == 0) {
					szRtnMsg = "대차 스케줄 조회된 데이터가 없습니다.";
					szMsg    = "["+szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING);
				} else {
					szMsg = "[Jsp-Session] "+szOperationName + "대차 스케줄 조회 성공.";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					outRecSet.first();
					recTcarInfo = outRecSet.getRecord();
				}

				// A-1  상차 작업인경우 대차스케줄에 상차 작업예약이 같은것이 편성되어있는지 확인한다.
				if ("".equals(ydUtils.substr(szYdSchCd, 6, 1))) {
					if (szWBookId.equals(ydDaoUtils.paraRecChkNull(recTcarInfo, "YD_CARLD_WRK_BOOK_ID"))) {
						szRtnMsg = "상차작업예약 되어 있어 삭제 할수 없습니다.";
						szMsg    = "["+szOperationName + "] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}

				// A-2  하차 작업인경우 대차스케줄에 하차 작업예약이 같은것이 편성되어 있는지 확인한다.
				if ("".equals(ydUtils.substr(szYdSchCd, 6, 1))) {
					if (szWBookId.equals(ydDaoUtils.paraRecChkNull(recTcarInfo, "YD_CARUD_WRK_BOOK_ID"))) {
						szRtnMsg = "하차작업예약 되어 있어 삭제 할수 없습니다.";
						szMsg    = "["+szOperationName + "] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}

				// 2. 크레인 스케줄 정보에 편성되어 있으면 작업취소할수 없다고 판단하고 Exception (작업취소 불가) 라고 RETURN 한다.
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("Yd");
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID", szWBookId);
				nRtnVal = ydCrnSchDao.getByWrkId(recPara, outRecSet);		// intGp == 28

				if (nRtnVal > 0) {
					szRtnMsg = "크레인 스케줄이 생성되어 삭제 할수 없습니다.!!";
					szMsg    = "["+szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					//DAO Exception 발생해야할지 판단해야함
					return szRtnMsg;

				} else if (nRtnVal < 0 ) {
					szRtnMsg = "크레인 스케줄 작업예약 ID로 조회시 ERROR!!";
					szMsg    = "["+szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					//DAO Exception 발생해야할지 판단해야함
					return szRtnMsg;
				}

				// 3. 크레인 스케줄 정보에 편성되지 않았을 경우에는 작업예약 및 작업 예약재료를 삭제 (DEL_YN = 'Y' SETTING) 한다.
				//작업예약 재료 삭제
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID", szWBookId);
				recPara.setField("MODIFIER", 	szMODIFIER);
				recPara.setField("DEL_YN",		"Y");

				nRtnVal = ydWrkbookMtlDao.deldWrkbookMtl(recPara);

				if (nRtnVal < 0) {
					szRtnMsg = "작업예약재료  삭제시 오류 발생 ";
					szMsg    = "["+szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				//작업예약 삭제
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID", szWBookId);
				recPara.setField("MODIFIER", 	szMODIFIER);
				recPara.setField("DEL_YN",		"Y");
				nRtnVal = ydWrkbookDao.delYdWrkbook(recPara);

				if (nRtnVal < 0) {
					szRtnMsg = "작업예약  삭제시 오류 발생 ";
					szMsg    = "["+szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg , JPlateYdConst.ERROR);
					return szRtnMsg;
				}
			}

		} catch(DAOException e) {
			szMsg = "[Jsp-Session] "+szOperationName + "UPDATE 실패!!";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw e;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		// 4. 작업완료 유무를 RETURN 한다.

		szMsg = "[Jsp-Session] "+szOperationName + ">>>> 종료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of tCarCancleWBook


	/**
	 *  대차 작업 출발처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String tCarMoveStart(JDTORecord[] inDto) throws DAOException {

		String 	szMethodName 		= "tCarMoveStart";
		String 	szOperationName 	= " 대차 출발 처리";
		String 	szMsg 				= "";
		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		JDTORecord 		recGetPara 	= null;

		try {

			szMsg = "[Jsp-Session] "+szOperationName + ">>>> 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			recGetPara = JDTORecordFactory.getInstance().create();
			recGetPara.setRecord(inDto[0]);

		} catch(DAOException e) {
			szMsg = "[Jsp-Session] "+szOperationName + "UPDATE 실패!!";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw e;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		// 4. 작업완료 유무를 RETURN 한다.

		szMsg = "[Jsp-Session] "+szOperationName + ">>>> 종료";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of tCarMoveStart


	/**
	 *  대차 도착처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String tCarMoveStop(JDTORecord[] inDto) throws DAOException {

		String 	szMethodName 		= "tCarMoveStop";
		String 	szOperationName 	= "대차 도착 처리";
		String 	szMsg 				= "";
		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		JDTORecord 		recGetPara 	= null;
    	EJBConnector ejbConn 		= null;

		try {

			szMsg = "[Jsp-Session] "+szOperationName + ">>>> 대차도착처리 START >>>>";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			for (int ii=0; ii<inDto.length; ii++) {

				recGetPara = JDTORecordFactory.getInstance().create();
				recGetPara.setRecord(inDto[ii]);

				ejbConn  = new EJBConnector("default", this);
				szRtnMsg = (String)ejbConn.trx("JPlateYdTcarSchSeEJB", "procY7TcarStop", recGetPara);

				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					return szRtnMsg;
				}
			}

		} catch(DAOException e) {
			szRtnMsg = "대차도착처리시 EXCEPTION 발생!!" + e.getMessage();
			szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szRtnMsg;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "[Jsp-Session] "+szOperationName + ">>>> 대차도착처리 END >>>>";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of tCarMoveStop


	/**
	 *  냉각대모니터링 : 이적 요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String insCbMoveReq(JDTORecord inDto) throws DAOException {

		JPlateYdEqpDAO  	ydEqpDao  	= new JPlateYdEqpDAO();
		JPlateYdStkLyrDAO	ydStkLyrDao = new JPlateYdStkLyrDAO();

		String 	szMethodName 		= "insCbMoveReq";
		String 	szOperationName 	= "냉각대 대차이적 요구";
		String 	szMsg 				= "";
		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
    	String	szYdEqpId			= "FXTC03";
    	String	szYdEqpLoc			= "FDTC03";			// 대차 야드적치열구분
    	String	szYdCurrBayGp		= "";				// 대차 현재 정지위치
        String	szToYdStkColGp      = "";     			// 대차 야드적치열구분
        String	szToYdStkBedNo      = "";    			// 대차 야드적치BED번호
        String	szToYdStkLyrNo      = "";    			// 대차 적치단
    	String	szStlNo				= "";				// 야드재료번호
    	String	szLyrNo				= "";
    	String	szModifier			= "";
    	String	szTemp				= "";

    	int		intRtnVal			= 0;

		JDTORecordSet   outSet  	= null;
		JDTORecord      recOut    	= null;
		JDTORecord      recPara    	= null;

		JPlateYdDelegate ydDelegate = new JPlateYdDelegate();

		try {

			szMsg = "[Jsp-Session] "+szOperationName + ">>>> 냉각대모니터링 : 이적 요구 START >>>>" + inDto.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szStlNo  	= ydDaoUtils.paraRecChkNull(inDto, "ARR_STL_NO");
			szLyrNo  	= ydDaoUtils.paraRecChkNull(inDto, "ARR_LYR_NO");
			szModifier	= ydDaoUtils.paraRecModifier(inDto);

			String[] arrStlNo = szStlNo.split(";");
			String[] arrLyrNo = szLyrNo.split(";");

			// 대차의 현재동 체크 :: YD_CURR_BAY_GP
			outSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_EQP_ID",  szYdEqpId);

			intRtnVal = ydEqpDao.getYdEqp(recPara, outSet);
			if (intRtnVal <= 0) {
				szRtnMsg = "대차 설비 조회시 오류 .. 설비ID :: " + szYdEqpId;
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}
			outSet.first();
			recOut = outSet.getRecord();
			szYdCurrBayGp = ydDaoUtils.paraRecChkNull(recOut, "YD_CURR_BAY_GP");
			if (!"D".equals(szYdCurrBayGp)) {
				szRtnMsg = "해당 대차의 현 위치가 " + szYdCurrBayGp + "동 입니다!";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			// 적치 순서로 SORT
			for(int ii = 0; ii < arrStlNo.length; ii++) {

				for(int jj = ii + 1; jj < arrStlNo.length; jj++) {

					if (Integer.parseInt(arrLyrNo[ii]) > Integer.parseInt(arrLyrNo[jj])) {

						szTemp = arrStlNo[ii];
						arrStlNo[ii] = arrStlNo[jj];
						arrStlNo[jj] = szTemp;

					}
				}
			}

			for (int ii=0; ii<arrStlNo.length; ii++) {

		        szStlNo = arrStlNo[ii];

				// 적치가능 베드 조회
				outSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP",  	szYdEqpLoc);
				recPara.setField("STL_NO",  		szStlNo);

				intRtnVal = ydStkLyrDao.getEmptyToLoc(recPara, outSet);
				if (intRtnVal <= 0) {
					szRtnMsg = "해당 대차의 적치가능한 저장위치가 없습니다!";
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
				outSet.first();
				recOut = outSet.getRecord();
		        szToYdStkColGp  = ydDaoUtils.paraRecChkNull(recOut, "YD_STK_COL_GP");     			// 대차 야드적치열구분
		        szToYdStkBedNo  = ydDaoUtils.paraRecChkNull(recOut, "YD_STK_BED_NO");    			// 대차 야드적치BED번호
		        szToYdStkLyrNo  = ydDaoUtils.paraRecChkNull(recOut, "YD_STK_LYR_NO");    			// 대차 적치단

		        /* ---- updJPlateYdStkPosFix 모듈에서 기존 저장위치 Clear 함으로 주석처리

				// 냉각대 저장위치 수정 (Old)
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("MODIFIER",	szModifier);
				recPara.setField("STL_NO",  	szStlNo);
				intRtnVal = ydStkLyrDao.updYdStklyrWithStock(recPara);

				if (intRtnVal <= 0) {
					szRtnMsg = "냉각대 저장위치 Clear시 오류 .... 재료번호 :: " + szStlNo;
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
				*/

				// 대차 저장위치상태 수정 (New)
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("MODIFIER",			szModifier);
				recPara.setField("STL_NO",				szStlNo);
				recPara.setField("YD_STK_LYR_MTL_STAT",	"C");
				recPara.setField("YD_STK_COL_GP",		szToYdStkColGp);
				recPara.setField("YD_STK_BED_NO",		szToYdStkBedNo);
				recPara.setField("YD_STK_LYR_NO",		szToYdStkLyrNo);

				// 후판정정야드 저장위치 수정 모듈 호출
				// 조업L3 전문송신 (후판조업 저장위치변경정보 전송  - YDPPJ011)
				szRtnMsg = this.updJPlateYdStkPosFix(recPara, "Y");			// 저장위치 수정후 , 전문전송

				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					szRtnMsg = "대차 저장위치 Set시 오류 .... 저장위치 :: " + szToYdStkColGp + szToYdStkBedNo + "-" + szToYdStkLyrNo;
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
			}

			//------------------------------------------------------------
			// 야드L2 전문송신 (저장위치변경이력 일괄 전송)
			// 후판조업 저장위치변경정보 전송 (YDPPJ011),
			// --> updJPlateYdStkPosFix 메서드 에서 실시 함으로 SKIP
			//------------------------------------------------------------
			szMsg = "[ " +szOperationName + "] 야드L2 전문송신 START";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("JMS_TC_CD", 			"YDY7L002");                            // TC-CODE
			recPara.setField("YD_GP", 				JPlateYdConst.YD_GP_F_PLATE_YARD);		// 야드구분
			recPara.setField("YD_STK_COL_GP", 		szYdEqpLoc);                          	// 야드적치열구분
			recPara.setField("YD_STK_BED_NO", 		"");    								// 야드적치BED번호
			recPara.setField("YD_INFO_SYNC_CD", 	"3");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
			recPara.setField("STL_NO", 				"");	        						// 재료번호

        	szRtnMsg = ydDelegate.sendMsg(recPara);

			szMsg = "[ " +szOperationName + "] 야드L2 전문송신 END >>>> " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch(DAOException e) {
			szRtnMsg = "냉각대 대차이적 요구 처리시 EXCEPTION 발생!!" + e.getMessage();
			szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szRtnMsg;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "[Jsp-Session] "+szOperationName + ">>>> 냉각대 대차이적 요구 처리 END >>>>";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of insCbMoveReq

	/**
	 *  보수장모니터링 : 보수장 가변베드 활성화 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String updBsBedAct(JDTORecord inDto) throws DAOException {

		JPlateYdStkColDAO	ydStkColDao = new JPlateYdStkColDAO();
		JPlateYdStkBedDAO	ydStkBedDao	= new JPlateYdStkBedDAO();
		JPlateYdStkLyrDAO	ydStkLyrDao = new JPlateYdStkLyrDAO();

		String 	szMethodName 		= "updBsBedAct";
		String 	szOperationName 	= "보수장 가변베드 활성화 처리";
		String 	szMsg 				= "";
		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;

		String	szYdBsGp  			= "";
		String	szBedActStat		= "";
    	String	szModifier			= "";

    	int		intRtnVal			= 0;

		JDTORecordSet   outSet  	= null;
		JDTORecord      recPara    	= null;

		JPlateYdDelegate ydDelegate = new JPlateYdDelegate();

    	String[]	arrYdStkColGp	= {"", ""};			// 활성화 대상 적치열

		try {

			szMsg = "[Jsp-Session] "+szOperationName + ">>>> 보수장 : 가변베드 활성화  START >>>>" + inDto.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szYdBsGp  		= ydDaoUtils.paraRecChkNull(inDto, "YD_BS_GP");
			szBedActStat	= ydDaoUtils.paraRecChkNull(inDto, "BED_ACT_STAT");
			szModifier		= ydDaoUtils.paraRecModifier(inDto);

			if ("FABS01".equals(szYdBsGp)) {
				arrYdStkColGp[0] 	= "FA0104";
			} else if ("FCBS01".equals(szYdBsGp)) {
				arrYdStkColGp[0]	= "FC0303";
				arrYdStkColGp[1]	= "FC0304";
			}

			for(int ii=0; ii<arrYdStkColGp.length; ii++) {

				if ("".equals(arrYdStkColGp[ii])) {
					break;
				}

				// -----------------------------------
				// 1. 야드 적치단에 해당 재료 존재여부 체크
				// -----------------------------------
				outSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP",		 arrYdStkColGp[ii]);
				recPara.setField("YD_STK_LYR_MTL_STAT1", "C");
				recPara.setField("YD_STK_LYR_MTL_STAT2", "U");
				recPara.setField("YD_STK_LYR_MTL_STAT3", "D");

				intRtnVal = ydStkLyrDao.getByLocMtlStat(recPara, outSet);
				if (intRtnVal > 0) {
					szRtnMsg = "해당위치 저장위치 :: " + arrYdStkColGp[ii] + "에 작업예약/재료가 존재합니다.";
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// -----------------------------------
				// 2. 야드 적치열에 활성 상태 변경
				// -----------------------------------
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP",		 	arrYdStkColGp[ii]);
				recPara.setField("YD_STK_COL_ACT_STAT", 	szBedActStat);
				recPara.setField("MODIFIER",				szModifier);

				intRtnVal = ydStkColDao.updYdStkColActStat(recPara);
				if (intRtnVal <= 0) {
					szRtnMsg = "해당 적치열  [" + arrYdStkColGp[ii] + "] 활성화 상태 변경시 오류! .... " + Integer.toString(intRtnVal);
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// -----------------------------------
				// 3. 야드 적치베드에 활성 상태 변경
				// -----------------------------------
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP",		 	arrYdStkColGp[ii]);
				recPara.setField("YD_STK_BED_NO",			"");
				recPara.setField("YD_STK_BED_ACT_STAT", 	szBedActStat);
				recPara.setField("MODIFIER",				szModifier);

				intRtnVal = ydStkBedDao.updYdStkBedActStat(recPara);
				if (intRtnVal <= 0) {
					szRtnMsg = "해당 적치베드  [" + arrYdStkColGp[ii] + "] 활성화 상태 변경시 오류! .... " + Integer.toString(intRtnVal);
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

			}

			for(int ii=0; ii<arrYdStkColGp.length; ii++) {

				if ("".equals(arrYdStkColGp[ii])) {
					break;
				}

				// -----------------------------------
				// 4. 야드L2에 저장위치제원 송신 [YDY7L001]
				// -----------------------------------
		    	recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("MSG_ID", 			"YDY7L001");
				recPara.setField("YD_INFO_SYNC_CD", "3");  // 1:동, 2:SPAN, 3:열, 4:BED
				recPara.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);
				recPara.setField("YD_STK_COL_GP", 	arrYdStkColGp[ii]);
				recPara.setField("YD_STK_BED_NO", 	"");
				szRtnMsg = ydDelegate.sendMsg(recPara);

				szMsg = "[Jsp-Session "+szOperationName+" ] 보수장 가변베드 활성화 정보 송신 완료 >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

		} catch(DAOException e) {
			szRtnMsg = "보수장 가변베드 활성하 처리시 EXCEPTION 발생!!" + e.getMessage();
			szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szRtnMsg;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "[Jsp-Session] "+szOperationName + ">>>> 보수장 가변베드 활성화 처리 END >>>>";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of updBsBedAct

	/**
	 * 2후판정정야드 저장위치별 정보 조회화면 : 조업L3 저장위치 정보 재전송
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String sndYdLocInfoList(JDTORecord [] inDto) throws DAOException {

		String	szRtnMsg		= "";
		String 	szMsg        	= "";
		String 	szMethodName	= "sndYdLocInfoList";
		String 	szOperationName	= "조업L3 저장위치 정보 재전송";

		String	szStlNo			= "";

		String	szYdStkColGp	= "";
		String	szOldYdStkColGp	= "";
		String	szYdStkBedNo	= "";
		String	szArrStlNo		= "";			// L3 전송용 재료번호 보관

		int		iSendCnt		= 0;

		JDTORecord recL3Para	= null;

		try {

			szMsg = "JSP-SESSION ["+szOperationName+"] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			for (int ii=0; ii<inDto.length; ii++) {

				szStlNo		 = ydDaoUtils.paraRecChkNull(inDto[ii], "STL_NO");
				szYdStkColGp = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GP");
				szYdStkBedNo = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_NO");
				if ("".equals(szOldYdStkColGp)) {
					szOldYdStkColGp = szYdStkColGp;
				}

				if (!szYdStkColGp.equals(szOldYdStkColGp)) {
		            szMsg    = "["+szOperationName+"] 후판조업 저장위치변경정보 전송 ---- START";
		            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		            recL3Para = JDTORecordFactory.getInstance().create();
		            recL3Para.setField("MSG_ID", 			"YDPPJ011");
		            recL3Para.setField("YD_STK_COL_FR", 	"");									// From적치열
		            recL3Para.setField("YD_STK_BED_FR", 	"");									// From적치BED
		            recL3Para.setField("YD_STK_COL_TO", 	szYdStkColGp);							// TO적치열
		            recL3Para.setField("YD_STK_BED_TO", 	szYdStkBedNo);							// TO적치BED
		            recL3Para.setField("YD_EQP_WRK_SH", 	"");									// 야드설비작업매수
		            recL3Para.setField("ARR_STL_NO", 		szArrStlNo);

		        //	szRtnMsg = ydDelegate.sendMsg(recL3Para);
		            szRtnMsg = JPlateYdCommonUtils.sendL3YDPPJ011(recL3Para);

					szMsg = "["+szOperationName+"] 후판조업 저장위치변경정보 전송 완료>>>>" + szRtnMsg;
		            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		            iSendCnt 		= 0;
		            szArrStlNo 		= "";
					szOldYdStkColGp = szYdStkColGp;
				}

				// 조업 L3 일괄 전송용 데이타 편집
				if ("".equals(szArrStlNo)) {
					szArrStlNo = szStlNo;
				} else {
					szArrStlNo = szArrStlNo + ";" + szStlNo;
				}
				iSendCnt ++;
			}

			// 저장위치 변경정보 일괄 전송
			if (iSendCnt > 0) {

	            szMsg    = "["+szOperationName+"] 후판조업 저장위치변경정보 전송 LAST ---- START";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	            recL3Para = JDTORecordFactory.getInstance().create();
	            recL3Para.setField("MSG_ID", 			"YDPPJ011");
	            recL3Para.setField("YD_STK_COL_FR", 	"");									// From적치열
	            recL3Para.setField("YD_STK_BED_FR", 	"");									// From적치BED
	            recL3Para.setField("YD_STK_COL_TO", 	szYdStkColGp);							// TO적치열
	            recL3Para.setField("YD_STK_BED_TO", 	szYdStkBedNo);							// TO적치BED
	            recL3Para.setField("YD_EQP_WRK_SH", 	"");									// 야드설비작업매수
	            recL3Para.setField("ARR_STL_NO", 		szArrStlNo);

	        //	szRtnMsg = ydDelegate.sendMsg(recL3Para);
	            szRtnMsg = JPlateYdCommonUtils.sendL3YDPPJ011(recL3Para);

				szMsg = "["+szOperationName+"] 후판조업 저장위치변경정보 전송 완료 LAST >>>>" + szRtnMsg;
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}// end of sndYdLocInfoList

	/**
	 * RT BOOK-OUT 작업지시 취소시 처리 (재료정보삭제, 적치위치CLEAR)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String delStockLocOnRt(JDTORecord inDto) throws DAOException {

		JDTORecord 			recPara		= null;

		JPlateYdStockDAO 	ydStockDao	= new JPlateYdStockDAO();
		JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();

		String 	szRtnMsg        = JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMsg        	= "";
		String 	szMethodName	= "delStockLocOnRt";
		String 	szOperationName	= "북아웃지시 취소후 재료정보CLEAR";

		String	szStlNo			= "";
		String	szModifier		= "";

		int 	intRtnVal 		= 0;

		try {

			szStlNo		= ydDaoUtils.paraRecChkNull(inDto, "STL_NO");
			szModifier	= ydDaoUtils.paraRecModifier(inDto);

			//--------------------------------------------------------------------------------------------
			// 북아웃지시 취소시 - 대상재료, 저장위치 삭제 처리
			//--------------------------------------------------------------------------------------------
			// 1. 재료정보 삭제처리
			// ------------------------------------------------------------------------
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO",				szStlNo);								// 재료번호
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
			// 2. 저장위치 CLEAR
			// ------------------------------------------------------------------------
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", 				szStlNo);             	// 재료번호
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

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			// throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	}// end of delStockLocOnRt

	/**
	 * 작업예약관리 :: TO위치 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String updWBookToLoc(JDTORecord inDto) throws DAOException {

		JDTORecordSet	rsResult  	= JDTORecordFactory.getInstance().createRecordSet("tempYd");
		JDTORecord		recPara		= null;

		JPlateYdCrnSchDAO 	ydCrnSchDao		= new JPlateYdCrnSchDAO();
		JPlateYdWrkbookDAO 	ydWrkbookDao	= new JPlateYdWrkbookDAO();

		String 	szRtnMsg        = JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMsg        	= "";
		String 	szMethodName	= "updWBookToLoc";
		String 	szOperationName	= "작업예약관리_TO위치 변경";

		String	szYdWbookId		= "";
		String	szYdToLocGuide	= "";
		String	szYdSchPrior	= "";
		String	szModifier		= "";

		int 	intRtnVal 		= 0;

		try {

			szYdWbookId		= ydDaoUtils.paraRecChkNull(inDto, "YD_WBOOK_ID");
			szYdToLocGuide	= ydDaoUtils.paraRecChkNull(inDto, "YD_TO_LOC_GUIDE");
			szYdSchPrior	= ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_PRIOR");
			szModifier		= ydDaoUtils.paraRecModifier(inDto);

			//--------------------------------------------------------------------------------------------
			// 1. 작업예약ID로 크레인 작업지시 존재여부 체크
			// ------------------------------------------------------------------------
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO",	szYdWbookId);

			intRtnVal = ydCrnSchDao.getByYdWBookId(recPara, rsResult);
			if (intRtnVal > 0) {
				szRtnMsg = "크레인작업지시 생성되어 .. TO위치 변경 불가!";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			// ------------------------------------------------------------------------
			// 2. 작업예약 UPDATE CLEAR
			// ------------------------------------------------------------------------
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_WBOOK_ID", 		szYdWbookId);
			recPara.setField("YD_TO_LOC_GUIDE", 	szYdToLocGuide);
			recPara.setField("YD_SCH_PRIOR",		szYdSchPrior);
			recPara.setField("MODIFIER", 			szModifier);

			intRtnVal = ydWrkbookDao.updToLocGuide(recPara);
			if (intRtnVal < 0) {
				szRtnMsg = "TO위치 변경 처리시 오류 발생! .... 오류코드 :" + Integer.toString(intRtnVal);
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			// throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	}// end of updWBookToLoc

	/**
	 * [2후판정정야드] 야드긴급재 등록/취소
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String updYdUgntGp(JDTORecord inDto) throws DAOException {

		JPlateYdStockDAO 	ydStockDao		= new JPlateYdStockDAO();
		JPlateYdDelegate 	ydDelegate 		= new JPlateYdDelegate();

		String	szRtnMsg		= "";
		String 	szMsg        	= "";
		String 	szMethodName	= "updYdUgntGp";
		String 	szOperationName	= "야드 긴급재 등록/취소";

		String 	szStlNo			= "";		// 재료번호
		String	szModifier		= "";		// 등록자
		String	szYdGp			= "";
		String	szYdUgntGp 		= "";
		String	szStlList		= "";

		int 	intRtnVal 		= 0;

		JDTORecord inRec		= JDTORecordFactory.getInstance().create();
		JDTORecord recPara		= null;
		JDTORecord recL2Para   	= null;

		try {

			szMsg = "JSP-SESSION ["+szOperationName+"] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			inRec.setRecord(inDto);

			szYdUgntGp 	= ydDaoUtils.paraRecChkNull(inRec, "YD_UGNT_GP", "Y");
			szStlList	= ydDaoUtils.paraRecChkNull(inRec, "YD_UGNT_STL_NO");
			szYdGp		= ydDaoUtils.paraRecChkNull(inRec, "YD_GP", JPlateYdConst.YD_GP_F_PLATE_YARD);	// 야드구분
			szModifier	= ydDaoUtils.paraRecModifier(inRec);											// 등록자, 수정자
			inRec.setField("MODIFIER",		szModifier);

			String arrStlNo[] = szStlList.split(";");

			for(int ii=0; ii<arrStlNo.length; ii++) {
				szStlNo = arrStlNo[ii];
				//------------------------------------------------------------------
				// 1.현재 저장위치가 2후판 정정야드가 아닐경우 오류로 처리
				//------------------------------------------------------------------
				szRtnMsg = JPlateYdCommonUtils.checkUpdYdLoc(szStlNo, szYdGp, "N");
				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					szMsg = "[" + szOperationName + "] 긴급재 등록/취소시 재공 확인 오류! >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				//------------------------------------------------------------------
				// 2.정정야드재료 테이블에 긴급재구분 변경
				//------------------------------------------------------------------
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 				szStlNo);			// 재료번호
				recPara.setField("YD_UGNT_GP",      	szYdUgntGp);		// 긴급재구분 (Y,N)
				recPara.setField("YD_UGNT_REGISTER",	szModifier); 		// 긴급재등록자
				recPara.setField("MODIFIER",			szModifier);		// 수정자

				intRtnVal = ydStockDao.updYdUgntGp(recPara);
				if (intRtnVal < 1) {
					szRtnMsg = "야드재료정보(긴급재) 변경시 오류 발생! 오류코드 :: " + Integer.toString(intRtnVal);
					szMsg 	 = "[" + szOperationName + "] >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
			}

			//------------------------------------------------------------
			// 3. 야드L2 전문송신 (저장품제원정보 전송) YDY7L002
			//------------------------------------------------------------
			for(int ii=0; ii<arrStlNo.length; ii++) {
				szStlNo = arrStlNo[ii];

				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	recL2Para = JDTORecordFactory.getInstance().create();
	        	recL2Para.setField("JMS_TC_CD", 		"YDY7L002");        // TC-CODE
	        	recL2Para.setField("YD_GP", 			szYdGp);			// 야드구분
	        	recL2Para.setField("YD_STK_COL_GP", 	"");          		// 야드적치열구분
	        	recL2Para.setField("YD_STK_BED_NO", 	"");				// 야드적치BED번호
	        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5");				// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
	        	recL2Para.setField("STL_NO", 			szStlNo);	        // 재료번호
	        	recL2Para.setField("MSG_GP", 			"U");	        	// 전문구분 : I(신규), U(수정), D(취소,삭제), R(재 전송)

	        	szRtnMsg = ydDelegate.sendMsg(recL2Para);

				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 END >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}// end of updYdUgntGp

	/**
	 * [2후판정정야드] 적치열검수 등록/취소
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String updCheckUp(JDTORecord inDto) throws DAOException {

		JPlateYdStkColDAO 	ydStkColDao		= new JPlateYdStkColDAO();

		String	szRtnMsg		= "";
		String 	szMsg        	= "";
		String 	szMethodName	= "updCheckUp";
		String 	szOperationName	= "적치열검수 등록/취소";

		String	szModifier		= "";		// 등록자
		String	szYdChkColGp 	= "";
		String	szYdChkLyrNo	= "";
		String	szYdChkSetGp	= "";

		int 	intRtnVal 		= 0;

		JDTORecord inRec		= JDTORecordFactory.getInstance().create();
		JDTORecord recPara		= null;

		try {

			szMsg = "JSP-SESSION ["+szOperationName+"] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			inRec.setRecord(inDto);

			szYdChkColGp 	= ydDaoUtils.paraRecChkNull(inRec, "YD_CHK_COL_GP");
			szYdChkLyrNo	= ydDaoUtils.paraRecChkNull(inRec, "YD_CHK_LYR_NO");
			szYdChkSetGp	= ydDaoUtils.paraRecChkNull(inRec, "YD_CHK_SET_GP");
			szModifier		= ydDaoUtils.paraRecModifier(inRec);					// 등록자, 수정자
			inRec.setField("MODIFIER",		szModifier);

			if (!"Y".equals(szYdChkSetGp)) {
				szYdChkLyrNo = "";
			}

			//------------------------------------------------------------------
			// 1.정정야드 적치열 테이블에 적치열검수 변경 (카드번호 항목 사용)
			//------------------------------------------------------------------
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", 		szYdChkColGp);		// 적치열구분
			recPara.setField("CARD_NO",      		szYdChkLyrNo);		// 적치열 검수단
			recPara.setField("MODIFIER",			szModifier);		// 수정자

			intRtnVal = ydStkColDao.updCardNo(recPara);
			if (intRtnVal < 1) {
				szRtnMsg = "적치열검수 처리시 오류 발생! 오류코드 :: " + Integer.toString(intRtnVal);
				szMsg 	 = "[" + szOperationName + "] >>>> " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}// end of updCheckUp

	/**
	 * [2후판정정야드] 시편채취 등록/취소
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String updRgntPkGp(JDTORecord inDto) throws DAOException {

		JPlateYdStockDAO 	ydStockDao		= new JPlateYdStockDAO();

		String	szRtnMsg		= "";
		String 	szMsg        	= "";
		String 	szMethodName	= "updYdUgntGp";
		String 	szOperationName	= "시편채취 등록/취소";

		String 	szStlNo			= "";		// 재료번호
		String	szModifier		= "";		// 등록자
		String	szRgntPkGp 		= "";
		String	szRgntPkCnts	= "";
		String	szStlList		= "";

		int 	intRtnVal 		= 0;

		JDTORecord inRec		= JDTORecordFactory.getInstance().create();
		JDTORecord recPara		= null;

		try {

			szMsg = "JSP-SESSION ["+szOperationName+"] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			inRec.setRecord(inDto);

			szRgntPkGp 		= ydDaoUtils.paraRecChkNull(inRec, "RGNT_PK_GP");
			szRgntPkCnts	= ydDaoUtils.paraRecChkNull(inRec, "RGNT_PK_CNTS");
			szStlList		= ydDaoUtils.paraRecChkNull(inRec, "RGNT_PK_STL_NO");
			szModifier		= ydDaoUtils.paraRecModifier(inRec);					// 등록자, 수정자
			inRec.setField("MODIFIER",		szModifier);

			String arrStlNo[] = szStlList.split(";");

			for(int ii=0; ii<arrStlNo.length; ii++) {
				szStlNo = arrStlNo[ii];

				//------------------------------------------------------------------
				// 정정야드재료 테이블에 시편채취구분 변경
				//------------------------------------------------------------------
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 				szStlNo);			// 재료번호
				recPara.setField("RGNT_PK_GP",      	szRgntPkGp);		// 시편채취구분 (1 : 시편채취 , 2 : 미인도시편채취 , 3 : 대체시편채취)
				recPara.setField("RGNT_PK_CNTS",      	szRgntPkCnts);		// 시편채취내용
				recPara.setField("MODIFIER",			szModifier);		// 수정자

				intRtnVal = ydStockDao.updRgntPkGp(recPara);
				if (intRtnVal < 1) {
					szRtnMsg = "시편채취 구분 변경시 오류 발생! 오류코드 :: " + Integer.toString(intRtnVal);
					szMsg 	 = "[" + szOperationName + "] >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}// end of updRgntPkGp
	
	
	/**
	 * [2후판정정야드] 상차위치변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String updCarLdLoc(JDTORecord[] inDto) throws DAOException {

		JPlateYdCarSchDAO 		ydCarSchDao			= new JPlateYdCarSchDAO();
		JPlateYdTcarFtmvMtlDAO	ydTcarFtmvMtlDao	= new JPlateYdTcarFtmvMtlDAO();
		JPlateYdWrkbookMtlDAO	ydWrkbookMtlDao		= new JPlateYdWrkbookMtlDAO();

		String	szRtnMsg		= "";
		String 	szMsg        	= "";
		String 	szMethodName	= "updYdUgntGp";
		String 	szOperationName	= "상차위치 변경";

		String	szYdCarSchId	= "";		// 차량스케줄ID
		String	szYdWbookId		= "";		// 작업예약ID
		String 	szStlNo			= "";		// 재료번호
		String	szYdStkBedNo	= "";		// 차상위치 Bed
		String	szYdStkLyrNo	= "";		// 차상위치 단
		String	szYdCarProgStat	= "";		// 차량진행상태
		String	szModifier		= "";		// 등록자

		int 	intRtnVal 		= 0;

		JDTORecord recPara		= JDTORecordFactory.getInstance().create();
		JDTORecord recOut		= JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		try {

			szYdCarSchId 	= ydDaoUtils.paraRecChkNull(inDto[0], "YD_CAR_SCH_ID");					// 차량스케줄ID
			szModifier 		= ydDaoUtils.paraRecModifier(inDto[0]);									// 수정자

			// 차량스케줄 조회
			outRecSet 		= JDTORecordFactory.getInstance().createRecordSet("retTmp");
			recPara 		= JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID", szYdCarSchId);
			intRtnVal = ydCarSchDao.getYdCarSch(recPara, outRecSet);
			if (intRtnVal > 0) {
				outRecSet.first();
				recOut = outRecSet.getRecord();
				szYdCarProgStat = ydDaoUtils.paraRecChkNull(recOut, "YD_CAR_PROG_STAT");
			}

			szMsg = "JSP-SESSION ["+szOperationName+"] 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			for (int ii=0; ii<inDto.length; ii++) {

				if ("1".equals(ydDaoUtils.paraRecChkNull(inDto[ii], "CHECK"))) {

					szStlNo			= ydDaoUtils.paraRecChkNull(inDto[ii], "STL_NO");				// 재료번호
					szYdStkBedNo	= ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_NO");		// 차상위치 Bed
					szYdStkLyrNo	= ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_LYR_NO");		// 차상위치 단
					szYdWbookId		= ydDaoUtils.paraRecChkNull(inDto[ii], "YD_WBOOK_ID");			// 작업예약ID
					
					ydUtils.putLog(SZ_SESSION_NAME, "szStlNo      : " + szStlNo			, szMsg, JPlateYdConst.DEBUG);
					ydUtils.putLog(SZ_SESSION_NAME, "szYdStkBedNo : " + szYdStkBedNo	, szMsg, JPlateYdConst.DEBUG);
					ydUtils.putLog(SZ_SESSION_NAME, "szYdStkLyrNo : " + szYdStkLyrNo	, szMsg, JPlateYdConst.DEBUG);
					ydUtils.putLog(SZ_SESSION_NAME, "szYdWbookId  : " + szYdWbookId		, szMsg, JPlateYdConst.DEBUG);
					
					// 상차완료일때 이송재료의 상차위치 변경
					if ("5".equals(szYdCarProgStat)) {
						recPara 	= JDTORecordFactory.getInstance().create();
						recPara.setField("YD_CAR_SCH_ID", 	szYdCarSchId);
						recPara.setField("STL_NO", 			szStlNo);
						recPara.setField("MODIFIER", 		szModifier);
						recPara.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdStkBedNo, 0, 2));
						recPara.setField("YD_STK_LYR_NO", 	ydUtils.substr(szYdStkLyrNo, 0, 3));
						intRtnVal = ydTcarFtmvMtlDao.updYdStkLyrNo(recPara);

						if (intRtnVal < 1) {
							szRtnMsg = "이송재료 상차위치 변경시 오류 발생! 오류코드 :: " + Integer.toString(intRtnVal);
							szMsg 	 = "[" + szOperationName + "] >>>> " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}
					}
					
					// 작업예약 재료의 상차위치 변경
					recPara.setField("YD_WBOOK_ID", 	szYdWbookId);
					recPara.setField("STL_NO", 			szStlNo);
					recPara.setField("MODIFIER", 		szModifier);
					recPara.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdStkBedNo, 0, 2));
					recPara.setField("YD_STK_LYR_NO", 	ydUtils.substr(szYdStkLyrNo, 0, 3));
					intRtnVal = ydWrkbookMtlDao.updYdStkBedLyrNo(recPara);

					if (intRtnVal < 1) {
						szRtnMsg = "작업예약 재료 상차위치 변경시 오류 발생! 오류코드 :: " + Integer.toString(intRtnVal);
						szMsg 	 = "[" + szOperationName + "] >>>> " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION ["+szOperationName+"] 끝";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}// end of updCarLdLoc
	
	
	/**
	 * [1, 2후판 정정야드]차량작업관리 상차LOT편성
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	public String insCarLdLot(JDTORecord [] inDto) throws DAOException {
		/*
		 * 업무기준 : 1. 차량스케줄ID로 차량스케줄이 존재하는 조회
		 * 				1-1. 존재하지 않으면 오류처리
		 * 				1-2. 존재하면 기준적용/작업자지정인 지를 판단
		 * 					1-2-1. 작업자지정이면
		 * 						1-2-1-1. 스케줄기준 체크
		 * 						1-2-1-2. 기존작업예약이 존재하지 않으면 작업예약 등록
		 * 						1-2-1-3. 작업예약 재료 등록
		 * 						1-2-1-4. 통합야드인 경우의 준비스케줄을 이용하여 작업예약 등록인 경우에는 
		 * 								해당 준비스케줄의 작업예약ID와 DEL_YN 항목에 Y를 설정
		 * 					1-2-2. 기준적용이면
		 * 						1-2-2-1. 구내운송은 차량스펙을 조회하여 야드작업허용중량을 사용
		 * 						1-2-2-2. 이송대상재를 조회
		 * 						1-2-2-3. 스케줄기준 체크
		 * 						1-2-2-4. 작업예약 등록
		 * 						1-2-2-5. 작업예약재료 등록
		 * 					1-2-3. 상차출발인 경우 예약된 차량정지point를 삭제하고 차량정지point지시요구 모듈을 호출
		 * 수정자 : 임춘수
		 * 수정일 : 2009.10.12
		 */
		int       intRtnVal    = 0;
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		String szMsg        = "";		
		String szMethodName = "insCarLdLot";
		String szOperationName		= "상차LOT편성";
		String szSTL_NO = null;
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szYD_CAR_SCH_ID = null;
		String szYD_CAR_USE_GP = null;
		String szTRN_EQP_CD 	= null;
		String szCAR_NO = null;
		String szCARD_NO = null;
		String szYD_STR_LOC = null;
		String szYD_GP = "";
		String szYD_BAY_GP = "";
		String szYD_SCH_CD = "";
		String szYD_SCH_PRIOR = "";
		String szYD_AIM_YD_GP = null;
		String szYD_AIM_BAY_GP = null;
		String szCRUD = null;
		String szUser = null;
		String szWLOC_CD = null;
		String szYD_STK_COL_GP = "";
		String szYD_STK_BED_NO = "";
		String szYD_STK_LYR_NO = "";
		String szYD_AIM_RT_GP = null;
		String szYD_PNT_CD1 = "";
		String szPREP_TYPE		= null;
		String szYD_PREP_SCH_ID	= null;
		String szYD_CAR_PROG_STAT	= null;
		String szCurDate		= YdUtils.getCurDate("yyyyMMddHHmmss");
		
		boolean bPointSendable = false;
		
		String szYD_WBOOK_ID = null;
		String szIS_EJB_CALL	= "";
		
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		YdStkColDao ydStkColDao		= null;
		
		
		JDTORecordSet rsWrkMtl = JDTORecordFactory.getInstance().createRecordSet("stock");
		JDTORecordSet rsSort = JDTORecordFactory.getInstance().createRecordSet("stock");
		JDTORecord recSort = null;
		
		try {
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//발지개소코드
			szWLOC_CD = ydDaoUtils.paraRecChkNull(inDto[0], "WLOC_CD3");
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 - 발지개소코드["+szWLOC_CD+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//+++++++++++++++ 1. 차량스케줄을 먼저 조회해서 존재하는 지를 확인 ++++++++++++++++++++++
			szYD_CAR_SCH_ID = yddatautil.setDataDefault(inDto[0].getField("YD_CAR_SCH_ID"), "");
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 조회 전";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
//getYdCarsch0			
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 0);
			if( intRtnVal == 0 ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]이 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if( intRtnVal < 0 ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 조회시 오류발생 : 반환값  - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
			outRecSet.first();
			recPara = outRecSet.getRecord();
			
			szYD_CAR_PROG_STAT = yddatautil.setDataDefault(recPara.getField("YD_CAR_PROG_STAT"), "");		//차량진행상태
			szYD_CAR_USE_GP = yddatautil.setDataDefault(recPara.getField("YD_CAR_USE_GP"), "");
			szTRN_EQP_CD = yddatautil.setDataDefault(recPara.getField("TRN_EQP_CD"), "");
			szCAR_NO = yddatautil.setDataDefault(recPara.getField("CAR_NO"), "");
			szCARD_NO = yddatautil.setDataDefault(recPara.getField("CARD_NO"), "");
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 차량스케줄이 존재합니다 - 운송장비코드["+szTRN_EQP_CD+"], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"], 차량사용구분["+szYD_CAR_USE_GP+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 - 작업자지정";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			
			//+++++++++++++++++++++++ 2. 스케줄기준 확인 +++++++++++++++++++++++
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 스케줄기준 확인 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szYD_STR_LOC = yddatautil.setDataDefault(inDto[0].getField("YD_STR_LOC"), "");
			if( !szYD_STR_LOC.equals("")) {
				szYD_GP = szYD_STR_LOC.substring(0, 1);
				szYD_BAY_GP = szYD_STR_LOC.substring(1, 2);
				
				recPara = JDTORecordFactory.getInstance().create();
				intRtnVal = YdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recPara);
				if( intRtnVal == -1 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 스케줄기준["+szYD_SCH_CD+"]이 금지상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CRN_SCH_PROH;
				}else if( intRtnVal < 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 스케줄기준["+szYD_SCH_CD+"]조회 시 오류발생 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
				
				szYD_SCH_PRIOR = yddatautil.setDataDefault(recPara.getField("YD_SCH_PRIOR"), "");
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 스케줄기준 확인 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//+++++++++++++++++++++++ 3. 작업예약 등록 +++++++++++++++++++++++
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 작업예약 등록 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szYD_PREP_SCH_ID = yddatautil.setDataDefault(inDto[0].getField("YD_PREP_SCH_ID"), "");
			szPREP_TYPE = yddatautil.setDataDefault(inDto[0].getField("PREP_TYPE"), "");
			szYD_WBOOK_ID = yddatautil.setDataDefault(inDto[0].getField("YD_WBOOK_ID"), "");
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 기존의 준비스케줄을 작업예약에 등록할 지 유무 판단["+szPREP_TYPE+"] - 준비스케줄ID["+szYD_PREP_SCH_ID+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if( szYD_WBOOK_ID.equals("") ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 기존에 등록된 작업예약ID가 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 기존에 등록된 작업예약ID["+szYD_WBOOK_ID+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			szYD_AIM_YD_GP = yddatautil.setDataDefault(inDto[0].getField("YD_AIM_YD_GP"), "");
			szYD_AIM_BAY_GP = yddatautil.setDataDefault(inDto[0].getField("YD_AIM_BAY_GP"), "");
			szUser = yddatautil.setDataDefault(inDto[0].getField("YD_USER_ID"), "");
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 첫번째 재료의 목표야드["+szYD_AIM_YD_GP+"], 목표동["+szYD_AIM_BAY_GP+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if( szYD_WBOOK_ID.equals("")) {
				//신규작업예약 등록
				szYD_WBOOK_ID = ydWrkbookDao.getYdWrkbookId();
				
				ydUtils.putLog(szSessionName, szMethodName, "szWLOC_CD : " + szWLOC_CD, YdConstant.DEBUG);
				if(YdConstant.WLOC_CD_B_PLATE_PLANT.equals(szWLOC_CD) ){ 	// 2021.06.28. 추가  => 2후판사이징재 작업예약에 야드구분, 스케줄코드 추가 
					ydUtils.putLog(szSessionName, szMethodName, "2후판사이징재 작업예약에 야드구분  F 추가", YdConstant.DEBUG);
					szYD_GP			= "F"; 			// [F]2후판 정정야드
					szYD_SCH_CD		= "FCPT01UM";	// 2후판정정 C동 차량이송 상차 스케쥴 코드
					szYD_STK_COL_GP	= "FCPT01";
				}else if(YdConstant.WLOC_CD_A_PLATE_PLANT.equals(szWLOC_CD)){
					ydUtils.putLog(szSessionName, szMethodName, "1후판사이징재 작업예약에 야드구분  F 추가", YdConstant.DEBUG);
					szYD_GP			= "P"; 			// [P]1후판 정정야드
					szYD_SCH_CD		= "PAPT01UM";	// 1후판정정 A동 차량 상차작업(PATR01) 스케쥴코드
					szYD_STK_COL_GP	= "PAPT01";
				}
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
				recPara.setField("YD_GP", 		  szYD_GP);
				recPara.setField("YD_BAY_GP", 	  szYD_BAY_GP);
				recPara.setField("YD_SCH_PRIOR",  szYD_SCH_PRIOR);
				recPara.setField("YD_SCH_CD", 	  szYD_SCH_CD);
				recPara.setField("REGISTER", 	  szUser);
				recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
				recPara.setField("TRN_EQP_CD", 	  szTRN_EQP_CD);
				recPara.setField("YD_AIM_YD_GP",  szYD_AIM_YD_GP);
				recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
				
				//작업예약 INSERT
				intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
				if (intRtnVal < 1) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 작업예약 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 신규작업예약 등록["+szYD_WBOOK_ID+"] 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 기존에 등록된 작업예약ID["+szYD_WBOOK_ID+"]로 작업예약 재료 등록 시 사용합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//+++++++++++++++++++++++ 4. 작업예약재료 등록 +++++++++++++++++++++++
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 작업예약재료 등록[작업예약ID : "+szYD_WBOOK_ID+"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//여러건의 로우를 처리 - 등록 및 수정
			for(int i = 0; i < inDto.length; i++ ) {
				
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
				recPara.setField("REGISTER", 	  szUser);
				
				szCRUD = yddatautil.setDataDefault(inDto[i].getField("CRUD"), "");
				
				if( !szPREP_TYPE.equals("Y")) { /*^^ 준비스케줄 관리 안하니 CRUD만 체크하면 될 듯*/
					if( !szCRUD.equals("C") ) continue;
				}
				//항목 편집
				szSTL_NO			= yddatautil.setDataDefault(inDto[i].getField("STL_NO"), "");
				szYD_STK_BED_NO		= yddatautil.setDataDefault(inDto[i].getField("YD_STK_BED_NO"), "");	// 화면에서 입력한 bed
				szYD_STK_LYR_NO		= yddatautil.setDataDefault(inDto[i].getField("YD_STK_LYR_NO"), "");	// 화면에서 입력한 단
				
				//재료번호
				recPara.setField("STL_NO", 		   szSTL_NO);
				//적치열구분
				recPara.setField("YD_STK_COL_GP",  szYD_STK_COL_GP);
				//적치BED번호
				recPara.setField("YD_STK_BED_NO",  szYD_STK_BED_NO);
				//적치단번호
				recPara.setField("YD_STK_LYR_NO",  szYD_STK_LYR_NO);
				//권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", "" + (i + 1));
				
				rsWrkMtl.addRecord(recPara);
			}
			
			// 2021.08.24 주석처리 화면에서 입력한 위치로 작업예약 생성
//			String szRtnVal = sortUpColSeq(rsWrkMtl, szYD_AIM_RT_GP, rsSort); 
			
//			if (szRtnVal == YdConstant.RETN_CD_SUCCESS) {
//				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 작업예약재료 정렬 성공 ";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				
//				for(int Loop_i = 1; Loop_i <= rsSort.size(); Loop_i++) {
//					
//					rsSort.absolute(Loop_i);
//					recSort         = JDTORecordFactory.getInstance().create();
//					recSort = rsSort.getRecord();
//					
//					recPara         = JDTORecordFactory.getInstance().create();
//					recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
//					recPara.setField("REGISTER", 	  szUser);
//					//재료번호
//					recPara.setField("STL_NO", 		   ydDaoUtils.paraRecChkNull(recSort, "STL_NO"));
//					//적치열구분
//					recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_COL_GP"));
//					//적치BED번호
//					recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_BED_NO"));
//					//적치단번호
//					recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_LYR_NO"));
//					//권상모음순서
//					recPara.setField("YD_UP_COLL_SEQ", "" + Loop_i);
//					
//					// 작업예약재료 테이블에 등록한다.
//					intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
//					
//					if (intRtnVal < 1) {
//						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 작업예약재료 데이터 등록 중 에러";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//						throw new DAOException(YdConstant.RETN_CD_FAILURE);
//					}
//					
//					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - "+Loop_i+"번째 재료번호["+ydDaoUtils.paraRecChkNull(recSort, "STL_NO")+"]를 작업예약재료[작업예약ID : "+szYD_WBOOK_ID+"]로 등록 성공 ";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				}
//				
//			} else {
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 작업예약재료 정렬 실패 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsWrkMtl.first();
				
				for(int Loop_i = 0; Loop_i < rsWrkMtl.size(); Loop_i++) {
					recSort         = JDTORecordFactory.getInstance().create();
					recSort = rsWrkMtl.getRecord(Loop_i);
					
					recPara         = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
					recPara.setField("REGISTER", 	  szUser);
					//재료번호
					recPara.setField("STL_NO", 		   ydDaoUtils.paraRecChkNull(recSort, "STL_NO"));
					//적치열구분
					recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_COL_GP"));
					//적치BED번호
					recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_BED_NO"));
					//적치단번호
					recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_LYR_NO"));
					//권상모음순서
					recPara.setField("YD_UP_COLL_SEQ", ydDaoUtils.paraRecChkNull(recSort, "YD_UP_COLL_SEQ"));
					
					// 작업예약재료 테이블에 등록한다.
					intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
					
					if (intRtnVal < 1) {
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 작업예약재료 데이터 등록 중 에러";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(YdConstant.RETN_CD_FAILURE);
					}
					
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - "+Loop_i+"번째 재료번호["+ydDaoUtils.paraRecChkNull(recSort, "STL_NO")+"]를 작업예약재료[작업예약ID : "+szYD_WBOOK_ID+"]로 등록 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				
//			}
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 작업예약재료[작업예약ID : "+szYD_WBOOK_ID+"] 등록 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			/*++++++++++++++++++++++++++ 차량정지Point 전송 시작 +++++++++++++++++++++++++++*/
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량정지Point 전송 전 개소코드["+szWLOC_CD+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if ( szYD_CAR_PROG_STAT.equals(YdConstant.YD_CARLD_LEV) ) {
				
				//------------------------------------------------------------------------------------------------------
				//	후판sizing개소코드거나 재열재개소코드이고 상차출발이면 소재차량정지point요구 모듈 호출
				//------------------------------------------------------------------------------------------------------
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 야드에서관리되지 않는 개소코드["+szWLOC_CD+"]이므로 차량스케줄["+szYD_CAR_SCH_ID+"] 상차point코드 등록 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szYD_PNT_CD1 = YdConstant.WLOC_CD_A_PLATE_PLANT_PNT_CD;
				
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				recPara.setField("YD_CARLD_PNT_WO_DT", szCurDate);
				recPara.setField("YD_PNT_CD1", szYD_PNT_CD1);
				recPara.setField("MODIFIER", szUser);
				recPara.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
				
				szIS_EJB_CALL = "Y";
				bPointSendable = true;
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량정지Point 전송 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//------------------------------------------------------------------------------------------------------
			}else{
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				recPara.setField("MODIFIER", szUser);
				recPara.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량도착 시 상차LOT편성이므로 차량정지POINT전송하지 않음";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			
			
			//------------------------------------------------------------------------------------------------------
			//	차량스케줄 수정 - 상차작업예약ID, 차량정지POINT 등
			//------------------------------------------------------------------------------------------------------
			
			intRtnVal = ydCarSchDao.updYdCarsch(recPara, 0);
			
			if (intRtnVal < 1) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄["+szYD_CAR_SCH_ID+"]의 수정 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}else if (intRtnVal == 0 ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄["+szYD_CAR_SCH_ID+"]의 수정 시 차량스케줄이 존재하지 않음 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(YdConstant.RETN_CD_NOTEXIST);
			}
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄["+szYD_CAR_SCH_ID+"]의 수정 성공";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			if( bPointSendable ) {
				
				//------------------------------------------------------------------------------------------------------
				/*
				 * 예약된 차량정지POINT 삭제처리
				 */
				//------------------------------------------------------------------------------------------------------
				ydStkColDao = new YdStkColDao();
				recPara = JDTORecordFactory.getInstance().create();
				//운송장비코드
				recPara.setField("TRN_EQP_CD",              szTRN_EQP_CD);
				intRtnVal = ydStkColDao.updYdStkcol1(recPara, 1);
				
				if( intRtnVal <= 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 운송장비코드["+szTRN_EQP_CD+"] - 예약된 차량정지point 삭제 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if( intRtnVal == 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 운송장비코드["+szTRN_EQP_CD+"] - 예약된 차량정지point 삭제 시 예약된 차량정지point가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 운송장비코드["+szTRN_EQP_CD+"] - 예약된 차량정지point 삭제 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//------------------------------------------------------------------------------------------------------
				
				
				//------------------------------------------------------------------------------------------------------
				//	차량정지 Point 전송
				//------------------------------------------------------------------------------------------------------
				
				YdCommonUtils.callMatlCarArrPntReq(szTRN_EQP_CD, szWLOC_CD, szIS_EJB_CALL, this);
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 야드에서관리되는 개소코드["+szWLOC_CD+"] - 차량정지Point 전송";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//------------------------------------------------------------------------------------------------------
				
			}
			/*++++++++++++++++++++++++++ 차량정지Point 전송 끝 +++++++++++++++++++++++++++*/
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of insCarLdLot
	
	
	/**
	 * 후판 사용함
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public String sortUpColSeq(JDTORecordSet rsWrkBookMtl, String szYD_AIM_RT_GP, JDTORecordSet rsSort) throws JDTOException {
		
		JDTORecord recInRsSet = null;
		JDTORecordSet rsBedCnt = null;
		
		String szRtnVal = YdConstant.RETN_CD_SUCCESS;
		String szMethodName = "sortUpColSeq";
		String szOperationName = "권상모음순서 정리";
		String szLogMsg = null;
		String[] szArrYdEqpGp = null;
		String[] szArrStlNo = null;
		String[] szArrYdStkPos = null;
		
		String[] szSortStkPos = null;
		String[] szSortStlCnt = null;
		String   szTmpStkPos = null;
		String   szTmpStlCnt = null;
		
		Vector vBedCnt = null;
		String szBeforeYdStkPos = "";
		int    intStlCnt = 0;
		
				
		String szTmpYdEqpGp = "";
		String szTmpStlNo = null;
		
		boolean blnSaveEqpId = true;
		String  szYdEqpGp   = "";
		boolean blnBaseBed = false;
		
		int intArrSize = 0;
		
		try {
			
			szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--------------------- 처리 시작 --------------------------";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLog(szSessionName, szMethodName, "rsWrkBookMtl.size()" + rsWrkBookMtl.size(), YdConstant.DEBUG);
			
			szArrYdEqpGp 	= new String[rsWrkBookMtl.size()];
			szArrStlNo      = new String[rsWrkBookMtl.size()];
			szArrYdStkPos   = new String[rsWrkBookMtl.size()];
			
						
			for(int Loop_i = 1; Loop_i <= rsWrkBookMtl.size(); Loop_i++) {
				rsWrkBookMtl.absolute(Loop_i);
				recInRsSet = JDTORecordFactory.getInstance().create();
				recInRsSet.setRecord(rsWrkBookMtl.getRecord());
				
				szArrYdEqpGp[Loop_i - 1] 	= ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_COL_GP").substring(2,4);
				szArrStlNo[Loop_i - 1] 		= ydDaoUtils.paraRecChkNull(recInRsSet, "STL_NO");
				szArrYdStkPos[Loop_i - 1] 	= ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_BED_NO");
				ydUtils.putLog(szSessionName, szMethodName, "rsWrkBookMtl.size()1111 "+szArrYdStkPos.length, YdConstant.DEBUG);
								
			} //end of for
			
			// 동일스판의 재료들인지를 확인한다.
			for(int Loop_i = 0; Loop_i<szArrYdEqpGp.length; Loop_i++) {
				if (Loop_i == 0) {
					szYdEqpGp = szArrYdEqpGp[Loop_i];
				} else {
					if (!szArrYdEqpGp[Loop_i].equals(szYdEqpGp)){
						blnSaveEqpId = false;
						break;
					}
				}
			}
			
			//동일스판이 아닐경우
			if (blnSaveEqpId == false) {
				// 정렬 시작
				intArrSize = rsWrkBookMtl.size();
				
				szLogMsg = "----권상모음순서 Sorting 작업전 목표행선[" + szYD_AIM_RT_GP + "]---";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				for(int x = 0; x<intArrSize; x++ ) {
					szLogMsg = "스판번호[" + szArrYdEqpGp[x] + "] - 재료번호[" + szArrStlNo[x] + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
				
				
				for(int x = 0; x<intArrSize-1; x++ ) {
					for(int y = 0; y<intArrSize-x-1; y++) {
						if (szYD_AIM_RT_GP.startsWith("A") ||szYD_AIM_RT_GP.startsWith("Y")) { // 4스판쪽으로 정렬
							if(Integer.parseInt(szArrYdEqpGp[y]) < Integer.parseInt(szArrYdEqpGp[y+1])) {
								szTmpYdEqpGp = szArrYdEqpGp[y];
								szTmpStlNo = szArrStlNo[y];
								szArrYdEqpGp[y] = szArrYdEqpGp[y+1];
								szArrStlNo[y] = szArrStlNo[y+1];   
								szArrYdEqpGp[y+1] = szTmpYdEqpGp;
								szArrStlNo[y+1] = szTmpStlNo;
							}
							
						} else if (szYD_AIM_RT_GP.startsWith("E") ||szYD_AIM_RT_GP.startsWith("G")) { //1스판쪽으로 정렬
							if(Integer.parseInt(szArrYdEqpGp[y]) > Integer.parseInt(szArrYdEqpGp[y+1])) {
								szTmpYdEqpGp = szArrYdEqpGp[y];
								szTmpStlNo = szArrStlNo[y];
								szArrYdEqpGp[y] = szArrYdEqpGp[y+1];
								szArrStlNo[y] = szArrStlNo[y+1];   
								szArrYdEqpGp[y+1] = szTmpYdEqpGp;
								szArrStlNo[y+1] = szTmpStlNo;
							}
							
						} else if (szYD_AIM_RT_GP.startsWith("B") || szYD_AIM_RT_GP.startsWith("C")) { //2스판쪽으로 정렬
							if( "02".equals(szArrYdEqpGp[y+1]) ||
								Integer.parseInt(szArrYdEqpGp[y]) > Integer.parseInt(szArrYdEqpGp[y+1])) {
								szTmpYdEqpGp = szArrYdEqpGp[y];
								szTmpStlNo = szArrStlNo[y];
								szArrYdEqpGp[y] = szArrYdEqpGp[y+1];
								szArrStlNo[y] = szArrStlNo[y+1];   
								szArrYdEqpGp[y+1] = szTmpYdEqpGp;
								szArrStlNo[y+1] = szTmpStlNo;
							}
						}
					}
				}
				
				szLogMsg = "----권상모음순서 Sorting 작업후 목표행선[" + szYD_AIM_RT_GP + "]---";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				for(int x = 0; x<intArrSize; x++ ) {
					szLogMsg = "스판번호[" + szArrYdEqpGp[x] + "] - 재료번호[" + szArrStlNo[x] + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
				
				
				for(int x = 0; x<intArrSize; x++ ) {
				
					for( int Loop_i = 1; Loop_i <= rsWrkBookMtl.size(); Loop_i++) {
						rsWrkBookMtl.absolute(Loop_i);
						recInRsSet = JDTORecordFactory.getInstance().create();
						recInRsSet.setRecord(rsWrkBookMtl.getRecord());
						
						
						if (szArrStlNo[x].equals(ydDaoUtils.paraRecChkNull(recInRsSet, "STL_NO"))){
							rsSort.addRecord(recInRsSet);
						}
						
					} //end of for
				}
				
				if (rsSort.size() != rsWrkBookMtl.size()) {
					szRtnVal = YdConstant.RETN_CD_FAILURE;
					return szRtnVal;
				}
				
				szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--------------------- 처리 종료 --------------------------";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				return szRtnVal;
				
			} else { // 동일스판일 경우 예약재료가 많은 BED를 권상모음순서 순위조정
				rsBedCnt = JDTORecordFactory.getInstance().createRecordSet("Temp");
				
				if (szArrYdStkPos.length > 1) {
					szBeforeYdStkPos = szArrYdStkPos[0];
					intStlCnt = 1;
					for(int Loop_i = 1; Loop_i<szArrYdStkPos.length; Loop_i++) {
						if (szArrYdStkPos[Loop_i].equals(szBeforeYdStkPos)){
							intStlCnt++;
						} else {
							
							recInRsSet = JDTORecordFactory.getInstance().create();
							recInRsSet.setField("YD_STK_POS", szBeforeYdStkPos);
							recInRsSet.setField("YD_STK_CNT", ""+intStlCnt);
							rsBedCnt.addRecord(recInRsSet);
							
							szBeforeYdStkPos = szArrYdStkPos[Loop_i];
							intStlCnt = 1;
							
							
						}
							
					} //end for
					
					recInRsSet = JDTORecordFactory.getInstance().create();
					recInRsSet.setField("YD_STK_POS", szBeforeYdStkPos);
					recInRsSet.setField("YD_STK_CNT", ""+intStlCnt);
					rsBedCnt.addRecord(recInRsSet);
					
					if(rsBedCnt.size() > 1) {
						szSortStkPos = new String[rsBedCnt.size()];
						szSortStlCnt = new String[rsBedCnt.size()];
						
						intArrSize = rsBedCnt.size();
						
						for(int Loop_i = 1; Loop_i <= rsBedCnt.size(); Loop_i++){
							rsBedCnt.absolute(Loop_i);
							recInRsSet = rsBedCnt.getRecord();
							
							szSortStkPos[Loop_i - 1] = ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_POS");
							szSortStlCnt[Loop_i - 1] = ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_CNT");
							
						} // end for
					
						
						for(int x = 0; x<intArrSize-1; x++ ) {
							for(int y = 0; y<intArrSize-x-1; y++) {
								if(Integer.parseInt(szSortStlCnt[y]) > Integer.parseInt(szSortStlCnt[y+1])) {
									szTmpStkPos = szSortStkPos[y];
									szTmpStlCnt = szSortStlCnt[y];
									szSortStkPos[y] = szSortStkPos[y+1];
									szSortStlCnt[y] = szSortStlCnt[y+1];   
									szSortStkPos[y+1] = szTmpStkPos;
									szSortStlCnt[y+1] = szTmpStlCnt;
								}
							}
						}
						
						szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--BED_CNT[" + intArrSize + "]개 입니다.";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						for(int x = 0; x<intArrSize; x++ ) {
							szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--정렬자료 POS[" + szSortStkPos[x] + "]와 재료수[" + szSortStlCnt[x] + "] 입니다.";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						}
						
						szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--정렬된 BED의 작업능력을 체크하여 가능여부를 판단";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						for(int x = 0; x<intArrSize; x++ ) {
							blnBaseBed = this.CheckBedSpec(szSortStkPos[intArrSize-1], rsWrkBookMtl);
							if(blnBaseBed == false){
								this.RotateSortStkPosCnt(szSortStkPos, szSortStlCnt);
							} else {
								break;
							}
						}
						
						
						for(int x = 0; x<intArrSize; x++ ) {
							
							for( int Loop_i = 1; Loop_i <= rsWrkBookMtl.size(); Loop_i++) {
								rsWrkBookMtl.absolute(Loop_i);
								recInRsSet = JDTORecordFactory.getInstance().create();
								recInRsSet.setRecord(rsWrkBookMtl.getRecord());
								
								
								if (szSortStkPos[x].equals(ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_BED_NO"))){
									rsSort.addRecord(recInRsSet);
								}
								
							} //end of for
						}
						
						if (rsSort.size() != rsWrkBookMtl.size()) {
							szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--작업예약재료[" + rsWrkBookMtl.size() + "]와 정렬재료[" + rsSort.size() + "]의 수가 맞지 않습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							szRtnVal = YdConstant.RETN_CD_FAILURE;
							return szRtnVal;
						}
						
						szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--------------------- 처리 종료 --------------------------";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					} else {
						szLogMsg = "[" + szOperationName + "] - sortUpColSeq]----대상BED가 1개입니다.";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						szRtnVal = YdConstant.RETN_CD_FAILURE;
						return szRtnVal;
					}
				}
				
				
				
				return szRtnVal;
			}
		} catch (Exception e) {
			szLogMsg = szOperationName + " ++++++++++++ 권상모음순서 정리 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			szRtnVal = YdConstant.RETN_CD_FAILURE;
			return szRtnVal;
		}
		
	} //end of sortUpColSeq
	
	/**
	 * Sorting된 위치를 회전변경한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param szSortStkPos, szSortStlCnt
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public String RotateSortStkPosCnt(String [] szSortStkPos, String [] szSortStlCnt) throws DAOException {
		
		String szMsg        		= "";		
		String szMethodName 		= "RotateSortStkPosCnt";
		String szOperationName 		= "Sorting된 위치를 회전변경";
		String szRtnMsg 			= YdConstant.RETN_CD_SUCCESS;
		
		String tmpStkPos = "";
		String tmpStlCnt = "";
		
		try {
			
			szMsg = "[" + szOperationName + "] - RotateSortStkPosCnt]--------------------- 처리 시작 --------------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			for(int x = 0; x<szSortStkPos.length; x++ ) {
				szMsg = "[" + szOperationName + "] - RotateSortStkPosCnt]--정렬전자료 POS[" + szSortStkPos[x] + "]와 재료수[" + szSortStlCnt[x] + "] 입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			tmpStkPos = szSortStkPos[szSortStkPos.length-1];
			tmpStlCnt = szSortStlCnt[szSortStkPos.length-1];
			
			for(int i = 0; i < szSortStkPos.length-1; i++){
				szSortStkPos[i+1] = szSortStkPos[i];
				szSortStlCnt[i+1] = szSortStlCnt[i];
			}
			
			szSortStkPos[0] = tmpStkPos;
			szSortStlCnt[0] = tmpStlCnt;
			
			szMsg = "[" + szOperationName + "] - RotateSortStkPosCnt]--------------------- 처리 종료 --------------------------";
			for(int x = 0; x<szSortStkPos.length; x++ ) {
				szMsg = "[" + szOperationName + "] - RotateSortStkPosCnt]--정렬후자료 POS[" + szSortStkPos[x] + "]와 재료수[" + szSortStlCnt[x] + "] 입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	} // end of RotateSortStkPosCnt
	
	
	/**
	 * BED 능력체크.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param szSortStkPos, szSortStlCnt
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public boolean CheckBedSpec(String  szSortStkPos, JDTORecordSet rsWrkBookMtl) throws DAOException {
		String szMsg        		= "";		
		String szMethodName 		= "CheckBedSpec";
		String szOperationName 		= "BED 능력체크";
		
		String szYD_STK_COL_GP    = "";
		String szYD_STK_BED_NO    = "";
		String szYD_STK_LYR_NO    = "";
		int    intYD_STK_LYR_NO   = 1000;
		
		
		JDTORecord     recWrkBookMtl = null;
		JDTORecordSet  rsTemp        = null;
		JDTORecord     recTemp       = null;
			
		
		boolean blnRtn = true;
		
		try {
			
			szYD_STK_COL_GP				= szSortStkPos.substring(0, 6);
			szYD_STK_BED_NO				= szSortStkPos.substring(6, 8);
			
			rsWrkBookMtl.first();
			for(int i=0; i<rsWrkBookMtl.size(); i++) {
				recWrkBookMtl = rsWrkBookMtl.getRecord(i);
				if(szYD_STK_COL_GP.equals(ydDaoUtils.paraRecChkNull(recWrkBookMtl,"YD_STK_COL_GP")) && szYD_STK_BED_NO.equals(ydDaoUtils.paraRecChkNull(recWrkBookMtl,"YD_STK_BED_NO"))) {
					if(Integer.parseInt(ydDaoUtils.paraRecChkNull(recWrkBookMtl,"YD_STK_LYR_NO")) < intYD_STK_LYR_NO ) {
						szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recWrkBookMtl,"YD_STK_LYR_NO");
						intYD_STK_LYR_NO = Integer.parseInt(ydDaoUtils.paraRecChkNull(recWrkBookMtl,"YD_STK_LYR_NO"));
					}
				}
			}
					
			
			szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 BASE위치[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+", 단:"+szYD_STK_LYR_NO+"]부터 아래단으로 적치중인 재료들 정보 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			rsTemp			= JDTORecordFactory.getInstance().createRecordSet("");
			recTemp 		= JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_STK_COL_GP", 		szYD_STK_COL_GP);
			recTemp.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
			recTemp.setField("YD_STK_LYR_NO", 		szYD_STK_LYR_NO);
			
			String szRtnMsg				= DaoManager.getYdStklyr(recTemp, rsTemp, 93);
			
			int intStkLyrMax			= 0;
			long lngStkLyrWtMax			= 0;
			double dblStkLyrHMax		= 0;
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				rsTemp.first();
				
				recTemp				= rsTemp.getRecord();
				
				//적치단에적치중인 총매수 중량 높이
	    		intStkLyrMax     = ydDaoUtils.paraRecChkNullInt (recTemp,"SH_CNT");
	   			lngStkLyrWtMax   = ydDaoUtils.paraRecChkNullLong(recTemp,"SUM_MTL_WT");
	   			dblStkLyrHMax    = ydDaoUtils.paraRecChkNullDouble(recTemp,"SUM_MTL_T");
	   			
	   			szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 BASE위치[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+", 단:"+szYD_STK_LYR_NO+"]부터 아래단으로 적치중인 재료의 총매수["+intStkLyrMax+"], 총중량["+lngStkLyrWtMax+"], 총높이["+dblStkLyrHMax+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}else if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
				szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 BASE위치[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+", 단:"+szYD_STK_LYR_NO+"]부터 아래단으로 적치중인 재료가 존재하지 않으므로 총매수["+intStkLyrMax+"], 총중량["+lngStkLyrWtMax+"], 총높이["+dblStkLyrHMax+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 BASE위치[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+", 단:"+szYD_STK_LYR_NO+"]부터 아래단으로 적치중인 재료들 정보 조회 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
			
			//---------------------------------------------------------------------------------------------------------
			//	현재의 권상모음순서보다 큰 재료들의 매수, 중량합, 두께합을 조회
			//---------------------------------------------------------------------------------------------------------
			
			szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 해당 작업예약재료들의 정보 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
			
			int intMTL_SH			= 0;
			long lngSUM_MTL_WT		= 0;
			double dblSUM_MTL_T		= 0;
			
			rsWrkBookMtl.first();
			for(int i=0; i<rsWrkBookMtl.size(); i++) {
				recWrkBookMtl = rsWrkBookMtl.getRecord(i);
				intMTL_SH = intMTL_SH + 1;
				lngSUM_MTL_WT = lngSUM_MTL_WT + ydDaoUtils.paraRecChkNullLong(recTemp,"YD_MTL_WT");
				dblSUM_MTL_T = dblSUM_MTL_T + ydDaoUtils.paraRecChkNullDouble(recTemp,"YD_MTL_T");
			}
				
	   		szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 작업예약재료들의 정보 조회 완료 - 총매수["+intMTL_SH+"], 총중량["+lngSUM_MTL_WT+"], 총높이["+dblSUM_MTL_T+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//---------------------------------------------------------------------------------------------------------
			
			
			//---------------------------------------------------------------------------------------------------------
			//	베드정보 조회 - MAX단, MAX중량, 높이 조회
			//---------------------------------------------------------------------------------------------------------
			
			szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"] 정보 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			int intYD_STK_BED_LYR_MAX			= 0;
			long lngYD_STK_BED_WT_MAX			= 0;
			double dblYD_STK_BED_H_MAX			= 0;
			
			rsTemp			= JDTORecordFactory.getInstance().createRecordSet("");
			recTemp 		= JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_STK_COL_GP", 		szYD_STK_COL_GP);
			recTemp.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
			
			szRtnMsg				= DaoManager.getYdStkbed(recTemp, rsTemp, 0);
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				rsTemp.first();
				
				recTemp				= rsTemp.getRecord();
	
				intYD_STK_BED_LYR_MAX 			= ydDaoUtils.paraRecChkNullInt(recTemp,"YD_STK_BED_LYR_MAX");
				lngYD_STK_BED_WT_MAX   			= ydDaoUtils.paraRecChkNullLong(recTemp,"YD_STK_BED_WT_MAX");
				dblYD_STK_BED_H_MAX				= ydDaoUtils.paraRecChkNullDouble(recTemp,"YD_STK_BED_H_MAX");
				
				szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"] 정보 조회 완료 - 총매수["+intYD_STK_BED_LYR_MAX+"], 총중량["+lngYD_STK_BED_WT_MAX+"], 총높이["+dblYD_STK_BED_H_MAX+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"] 정보 조회 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
			
			boolean isShOver			= false;
			boolean isWtOver			= false;
			boolean isHOver				= false;
			
			//매수 비교
			if( intYD_STK_BED_LYR_MAX < intMTL_SH + intStkLyrMax ) {
				isShOver				= true;
				
				szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 BASE위치[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+", 단:"+szYD_STK_LYR_NO+"]가 매수초과이므로 BASE이적 처리";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			//중량 비교
			if( lngYD_STK_BED_WT_MAX < lngSUM_MTL_WT + lngStkLyrWtMax ) {
				isWtOver				= true;
				
				szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 BASE위치[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+", 단:"+szYD_STK_LYR_NO+"]가중량초과이므로 BASE이적 처리";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			//높이 비교
			if( dblYD_STK_BED_H_MAX < dblSUM_MTL_T + dblStkLyrHMax ) {
				isHOver					= true;
				szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 BASE위치[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+", 단:"+szYD_STK_LYR_NO+"]가 높이초과이므로 BASE이적 처리";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			if( isShOver || isWtOver || isHOver ) {
				blnRtn = false;
			}
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return blnRtn;
	} // end of CheckBedSpec
	
	
	/**
	 * 차량작업관리 상차LOT편성 취소 : 후판
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public String cancelCarLdLot(JDTORecord [] inDto) throws DAOException {
		/*
		 * 업무기준 : 1. 그리드에 선택된 작업예약재료를 삭제처리
		 * 			 2. 작업예약재료가 모두 삭제되었으면
		 * 				2-1. 작업예약도 같이 삭제처리
		 * 				2-2. 차량스케줄의 상차작업예약ID를 NULL로 수정
		 * 			  	2-3. 상차작업예약ID와 연관된 준비스케줄과 준비재료의 DEL_YN항목을 N으로 설정
		 * 				2-4. 예약된 차량정지POINT를 삭제처리
		 * 수정자 : 임춘수
		 * 수정일 : 2009.10.12
		 */
		//JDTO 변수정의
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = null;
		//DAO 변수정의
		YdCarSchDao ydCarSchDao		= new YdCarSchDao();
		//기본 변수 정의
		String szMsg        		= "";		
		String szMethodName 		= "cancelCarLdLot";
		String szOperationName 		= "차량작업관리 - 상차LOT편성취소";
		int       intRtnVal			= 0;
		String szRtnMsg 			= YdConstant.RETN_CD_SUCCESS;
		//로컬변수 정의
		String szSTL_NO 			= null;
		String szCRUD 				= null;
		String szYD_WBOOK_ID 		= null;
		String szYD_CAR_SCH_ID		= null;
		String szUser 				= null;
		
		int intRUDCnt 				= 0;
		
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//YdCarSpecDao ydCarSpecDao = new YdCarSpecDao();
		
		try {
			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(inDto[0], "YD_CAR_SCH_ID");
			
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 - 차량스케줄확인[" + szYD_CAR_SCH_ID + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 1. 그리드에 선택된 작업예약재료를 삭제처리
			 */
			//여러건의 로우를 처리 - 등록 및 수정
			for(int i = 0; i < inDto.length; i++ ) {
				szCRUD = ydDaoUtils.paraRecChkNull(inDto[i], "CRUD");
				szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(inDto[i], "YD_WBOOK_ID");
				szSTL_NO = ydDaoUtils.paraRecChkNull(inDto[i], "STL_NO");
				szUser = ydDaoUtils.paraRecChkNull(inDto[i], "YD_USER_ID");
				szMsg = "[JSP Session : "+szOperationName+"] ["+( i + 1 )+"]재료번호 - CRUD["+szCRUD+"], YD_WBOOK_ID["+szYD_WBOOK_ID+"], STL_NO["+szSTL_NO+"] 삭제 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				if( !szCRUD.equals("C") ) {
					recPara.setField("STL_NO", szSTL_NO);
					recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
					recPara.setField("DEL_YN", "Y");
					recPara.setField("MODIFIER", szUser);
					intRtnVal = ydWrkbookMtlDao.updYdWrkbookmtl(recPara, 0);
					if( intRtnVal < 0 ) {
						szMsg = "[JSP Session : "+szOperationName+"] 작업예약재료["+szSTL_NO+"] 삭제 시 오류발생 : 반환값 - " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(YdConstant.RETN_CD_FAILURE);
					}else if( intRtnVal == 0 ) {
						szMsg = "[JSP Session : "+szOperationName+"] 작업예약재료["+szSTL_NO+"] 삭제 시 해당 재료가 존재하지 않음 - " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}else{
						szMsg = "[JSP Session : "+szOperationName+"] 작업예약재료["+szSTL_NO+"] 삭제 성공 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					intRUDCnt++;
				}
			}
			if( intRUDCnt > 0 ) {
				/*
				 * 2. 작업예약재료가 모두 삭제되었으면
				 */
				szMsg = "[JSP Session : "+szOperationName+"] 작업예약[" + szYD_WBOOK_ID + "] 삭제 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	//getYdWrkbookmtl1				
				intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 1);
				
				if( intRtnVal == 0 ) {
					/*
					 * 2-1. 작업예약도 같이 삭제처리
					 */
					recPara.setField("DEL_YN", "Y");
					recPara.setField("MODIFIER", szUser);
					intRtnVal = ydWrkbookDao.updYdWrkbook(recPara, 0);
					if( intRtnVal < 0  ) {
						szMsg = "[JSP Session : "+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"] 삭제 시 오류발생 : 반환값 - " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(YdConstant.RETN_CD_FAILURE);
					}else if( intRtnVal == 0  ) {
						szMsg = "[JSP Session : "+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"] 삭제 시 작업예약이 존재하지 않음 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(YdConstant.RETN_CD_FAILURE);
					}
					szMsg = "[JSP Session : "+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"] 삭제 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					/*
					 * 2-2. 차량스케줄의 상차작업예약ID를 NULL로 수정
					 */
					recPara         = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					recPara.setField("YD_CARLD_WRK_BOOK_ID", "");
					recPara.setField("MODIFIER", szUser);
					
					intRtnVal = ydCarSchDao.updYdCarsch(recPara, 0);
					
					if( intRtnVal < 0  ) {
						szMsg = "[JSP Session : "+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 상차작업예약ID를 NULL로 수정 시 오류발생 : 반환값 - " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(YdConstant.RETN_CD_FAILURE);
					}else if( intRtnVal == 0  ) {
						szMsg = "[JSP Session : "+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 상차작업예약ID를 NULL로 수정 시 존재하지 않음 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(YdConstant.RETN_CD_FAILURE);
					}
					
					/*
					 * 2.3. 상차작업예약ID와 연관된 준비스케줄과 준비재료의 DEL_YN항목을 N으로 설정
					 */
					szMsg = "[JSP Session : "+szOperationName+"] 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 원복 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					szRtnMsg = YdCommonUtils.restorePrepSch(szYD_WBOOK_ID, szMethodName);
					
					szMsg = "[JSP Session : "+szOperationName+"] 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 원복 성공 : " + szRtnMsg;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
	//				outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
	//				recPara         = JDTORecordFactory.getInstance().create();
	//				recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	//				
	//				YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();
	//				
	//				intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 8);
	//				
	//				if( intRtnVal < 0  ) {
	//					szMsg = "[JSP Session : "+szOperationName+"] 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 조회 시 오류발생 : 반환값 - " + intRtnVal;
	//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	//				}else if( intRtnVal == 0  ) {
	//					szMsg = "[JSP Session : "+szOperationName+"] 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 조회 시 존재하지 않음 ";
	//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	//				}else{
	//					
	//					outRecSet.first();
	//					recPara = outRecSet.getRecord();
	//					
	//					szYD_PREP_SCH_ID = ydDaoUtils.paraRecChkNull(recPara, "YD_PREP_SCH_ID");
	//					
	//					szMsg = "[JSP Session : "+szOperationName+"] 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄[" + szYD_PREP_SCH_ID + "]이 존재하므로 삭제 시작";
	//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//					
	//					YdPrepMtlDao ydPrepMtlDao = new YdPrepMtlDao();
	//					
	//					recPara         = JDTORecordFactory.getInstance().create();
	//					recPara.setField("YD_PREP_SCH_ID",   	szYD_PREP_SCH_ID);
	//					recPara.setField("DEL_YN",   			"N");
	//					recPara.setField("MODIFIER",   			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
	//					//준비재료 삭제처리
	//					intRtnVal = ydPrepMtlDao.uptDelYdPrepmtlByPrepSchId(recPara);
	//					
	//					//준비스케줄 삭제처리
	//					recPara.setField("YD_WBOOK_ID",   		"");
	//					intRtnVal =  ydPrepSchDao.updDelYdPrepsch(recPara);
	//					
	//					szMsg = "[JSP Session : "+szOperationName+"] 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄[" + szYD_PREP_SCH_ID + "]과 준비재료 삭제 성공";
	//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//				}
					
					/*
					 * 2-4. 예약된 차량정지POINT를 삭제처리 --> 상차LOT편성 시 삭제 됨
					 */
	//				szYD_CAR_STOP_LOC = ydDaoUtils.paraRecChkNull(inDto[0], "CAR_POINT3");
	//				szMsg = "[JSP Session : "+szOperationName+"] 예약된 차량정지POINT["+szYD_CAR_STOP_LOC+"]";
	//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//				if( !szYD_CAR_STOP_LOC.equals("")) {
	//					szMsg = "[JSP Session : "+szOperationName+"] 예약된 차량정지POINT["+szYD_CAR_STOP_LOC+"]가 존재하므로 삭제처리 시작";
	//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//					recPara         = JDTORecordFactory.getInstance().create();
	//					recPara.setField("YD_STK_COL_GP",   	szYD_CAR_STOP_LOC);
	//					recPara.setField("YD_CAR_USE_GP",   	"");					//차량사용구분
	//					recPara.setField("TRN_EQP_CD",   		"");					//운송장비코드
	//					YdStkColDao ydStkColDao = new YdStkColDao();
	//					intRtnVal = ydStkColDao.updYdStkcol(recPara, 0);
	//					
	//					if( intRtnVal < 0 ) {
	//						szMsg = "[JSP Session : "+szOperationName+"] 예약된 차량정지POINT["+szYD_CAR_STOP_LOC+"]가 존재하므로 삭제처리 시 오류발생[1] - 반환값 : " +intRtnVal;
	//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	//					}else if( intRtnVal == 0 ) {
	//						szMsg = "[JSP Session : "+szOperationName+"] 예약된 차량정지POINT["+szYD_CAR_STOP_LOC+"]가 존재하므로 삭제처리 시 적치열이 존재하지 않습니다. - 반환값 : " +intRtnVal;
	//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	//					}
	//					
	//					szMsg = "[JSP Session : "+szOperationName+"] 예약된 차량정지POINT["+szYD_CAR_STOP_LOC+"]가 존재하므로 삭제처리 완료";
	//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//				}
					
				}else if( intRtnVal > 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 취소  - 작업예약재료가 존재하므로 작업예약["+szYD_WBOOK_ID+"] 삭제 불가";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}else{
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 취소  - 작업예약ID["+szYD_WBOOK_ID+"]로 작업예약재료 조회시 오류발생 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_FAILURE);
				}
			}
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 취소 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of cancelCarLdLot
	
	
	/**
	 * 차량작업관리화면 상차완료처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public String complCarLdLot(JDTORecord [] inDto) throws DAOException {
		int       intRtnVal    = 0;
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecord       recTemp         = null;
		JDTORecord       outRec         = JDTORecordFactory.getInstance().create();
		JDTORecord       recLdStart      = null;
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg        = "";		
		String szMethodName = "complCarLdLot";
		String szSTL_NO = null;
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szYD_STK_BED_NO = null;	// 2021.06.14 Bed 항목 추가 -> 발지가 1,2후판 정정야드일 때 사용
		String szYD_STK_LYR_NO = null;
		String szYD_WBOOK_ID = null;
		String szYD_CAR_SCH_ID = null;
		String szUser = null;
		String szCurrDate = YdUtils.getCurDate("yyyyMMddHHmmss");
		String szARR_WLOC_CD = "";
		String szYD_AIM_YD_GP = null;
		String szTRN_EQP_CD = null;
		String szWLOC_CD = null;
		String szTcCode = null;
		String szYD_MTL_WT = null;
		String szYD_CAR_PROG_STAT = null;
		String szSPOS_YD_PNT_CD = "";
		String szHCR_GP = null;
		String szYD_MTL_ITEM = null;
		String szOperationName = "상차완료처리";
		//int intRUDCnt = 0;
		
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		
		
		try {
			//------------------------------------------------------------------------------------------------------
			// 전문확인
			//------------------------------------------------------------------------------------------------------
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			szYD_CAR_SCH_ID = yddatautil.setDataDefault(inDto[0].getField("YD_CAR_SCH_ID"), "");
			szYD_WBOOK_ID 	= ydDaoUtils.paraRecChkNull(inDto[0], "YD_WBOOK_ID"	);
			szUser 			= ydDaoUtils.paraRecChkNull(inDto[0], "YD_USER_ID"	);
			szYD_AIM_YD_GP 	= ydDaoUtils.paraRecChkNull(inDto[0], "YD_AIM_YD_GP");
			szWLOC_CD 		= ydDaoUtils.paraRecChkNull(inDto[0], "WLOC_CD3"	);
			szTRN_EQP_CD 	= ydDaoUtils.paraRecChkNull(inDto[0], "TRN_CAR_NO"	);
			szYD_MTL_ITEM 	= ydDaoUtils.paraRecChkNull(inDto[0], "YD_MTL_ITEM"	);
			szHCR_GP 		= ydDaoUtils.paraRecChkNull(inDto[0], "HCR_GP"		);
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 차량스케줄ID["+szYD_CAR_SCH_ID+"], 작업예약ID["+szYD_WBOOK_ID+"], 야드목표야드구분["+szYD_AIM_YD_GP+"], 운송장비코드["+szTRN_EQP_CD+"], 발지개소코드["+szWLOC_CD+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//------------------------------------------------------------------------------------------------------
			
			
			//------------------------------------------------------------------------------------------------------
			// 차량스케줄 조회 후 차량진행상태 확인 시작
			//------------------------------------------------------------------------------------------------------
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 조회 전";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//1. 차량스케줄을 먼저 조회해서 존재하는 지를 확인
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);

//getYdCarsch1			
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 0);
			if( intRtnVal == 0 ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]이 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if( intRtnVal < 0 ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 조회시 오류발생 : 반환값  - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
			outRecSet.first();
			recPara = outRecSet.getRecord();
			
			szYD_CAR_PROG_STAT = yddatautil.setDataDefault(recPara.getField("YD_CAR_PROG_STAT"), "");
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 야드차량진행상태["+szYD_CAR_PROG_STAT+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			if( szYD_CAR_PROG_STAT.equals("5")) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 야드차량진행상태["+szYD_CAR_PROG_STAT+"]가 이미 상차완료이므로 상차완료처리를 할 수 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return YdConstant.RETN_CD_EQ_STATUS;
			}
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 야드차량진행상태["+szYD_CAR_PROG_STAT+"]가 상차완료가 아니므로 상차완료처리를 합니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//------------------------------------------------------------------------------------------------------
			
			
			//------------------------------------------------------------------------------------------------------
			// 차량스케줄과 작업예약을 연결시킨다
			//------------------------------------------------------------------------------------------------------
			//야드구분을 개소코드로 변환
			szARR_WLOC_CD = YdCommonUtils.getWlocCd(szYD_AIM_YD_GP);
			
			if( szWLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT)){			// 1후판 정정
				
				if("".equals(ydDaoUtils.paraRecChkNull(inDto[0], "STL_NO"))){
					szARR_WLOC_CD = YdConstant.WLOC_CD_A_PLATE_SLAB_YARD;
				}else{
					szARR_WLOC_CD = YdCommonUtils.getWlocCd3(ydDaoUtils.paraRecChkNull(inDto[0], "STL_NO"));
				}
				
			}else if( szWLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT)) {		// 2후판정정
				
				if("".equals(ydDaoUtils.paraRecChkNull(inDto[0], "STL_NO"))){
					szARR_WLOC_CD = YdConstant.WLOC_CD_2_PLATE_SLAB_YARD;
				}else{
					szARR_WLOC_CD = YdCommonUtils.getWlocCd3(ydDaoUtils.paraRecChkNull(inDto[0], "STL_NO"));
				}
			}
			//1. 차량스케줄과 작업예약을 연결시킨다 - 상차작업예약ID, 상차개시일시, 상차완료일시, 착지개소코드, 차량진행상태를 상차완료로 설정한다.
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID"		, szYD_CAR_SCH_ID	);	//차량스케줄ID
			recPara.setField("YD_CARLD_WRK_BOOK_ID"	, szYD_WBOOK_ID		);	//상차작업예약ID
			recPara.setField("YD_EQP_WRK_STAT"		, "L"				);	//야드설비작업상태
			recPara.setField("YD_CARLD_ST_DT"		, szCurrDate		);	//상차개시일시
			recPara.setField("YD_CARLD_CMPL_DT"		, szCurrDate		);	//상차완료일시
			recPara.setField("ARR_WLOC_CD"			, szARR_WLOC_CD		);	//착지개소코드
			recPara.setField("YD_CAR_PROG_STAT"		, "5"				);	//차량진행상태 : 상차완료[5]
			recPara.setField("MODIFIER", szUser);							//수정자
			
			intRtnVal = ydCarSchDao.updYdCarsch(recPara, 0);
			
			if( intRtnVal == 0 ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 차량스케줄에 상차개시일시, 상차완료일시, 차량진행상태[상차완료-5]를 업데이트시 차량스케줄이 존재하지 않습니다. : 반환값 - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}else if( intRtnVal < 0 ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 차량스케줄에 상차개시일시, 상차완료일시, 차량진행상태[상차완료-5]를 업데이트시 오류발생 : 반환값 - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}
			//------------------------------------------------------------------------------------------------------
			
			
			//------------------------------------------------------------------------------------------------------
			// 상차개시를 구내운송으로 전송
			//------------------------------------------------------------------------------------------------------
			//1,2후판 모두 같은 Point코드사용
			szSPOS_YD_PNT_CD = YdConstant.WLOC_CD_A_PLATE_PLANT_PNT_CD;
			
			recLdStart         = JDTORecordFactory.getInstance().create();
			szTcCode = "YDTSJ007";
			//2. 상차개시를 구내운송으로 전송
			recLdStart.setField("JMS_TC_CD", 			szTcCode);
			recLdStart.setField("JMS_TC_CREATE_DDTT",   YdUtils.getCurDate("yyyyMMddHHmmss"));
			// 운송장비코드 [운송장비코드]
			recLdStart.setField("TRN_EQP_CD", szTRN_EQP_CD);	
			// 발지개소코드 [발지개소코드]
			recLdStart.setField("SPOS_WLOC_CD", szWLOC_CD);
			// 발지 야드포인트코드 [상차정지위치 = 적치열구분] - 아직 미 결정
			recLdStart.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			// 착지개소코드 [착지개소코드]
			recLdStart.setField("ARR_WLOC_CD", szARR_WLOC_CD);
			// 운송작업시작일시 [상차개시일시]
			recLdStart.setField("TRN_WRK_ST_DT", szCurrDate);
			
//			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  시 - 상차개시전문을 구내운송으로 전송 시작";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			ydUtils.displayRecord(szOperationName, recLdStart);
			
			//------------------------------------------------------------------------------------------------------
			
			
			//------------------------------------------------------------------------------------------------------
			// 상차완료를 구내운송으로 전송, 차량이송재료 등록, 작업예약재료/작업예약 삭제 처리
			//------------------------------------------------------------------------------------------------------
			szTcCode = "YDTSJ008";
			outRec.setField("JMS_TC_CD", 			szTcCode);
			outRec.setField("JMS_TC_CREATE_DDTT",   YdUtils.getCurDate("yyyyMMddHHmmss"));
			// 운송장비코드 [운송장비코드]
			outRec.setField("TRN_EQP_CD", szTRN_EQP_CD);	
			// 발지개소코드 [발지개소코드]
			outRec.setField("SPOS_WLOC_CD", szWLOC_CD);
			// 발지 야드포인트코드 [상차정지위치 = 적치열구분] - 아직 미 결정
			outRec.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			// 착지개소코드 [착지개소코드]
			outRec.setField("ARR_WLOC_CD", szARR_WLOC_CD);
			//3. 작업예약에 등록된 재료를 차량이송재료로 등록 후 작업예약재료를 삭제처리한다.
			recTemp         = JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recTemp.setField("DEL_YN", "Y");
			recTemp.setField("MODIFIER", szUser);
			
			recPara         = JDTORecordFactory.getInstance().create();
			for(int i = inDto.length - 1; i >= 0; i-- ) {
				
				//------------------------------------------------------------------------------------------------------
				// 차량이송재료 등록
				//------------------------------------------------------------------------------------------------------
				szYD_CAR_SCH_ID	= yddatautil.setDataDefault(inDto[i].getField("YD_CAR_SCH_ID"), ""	);
				szSTL_NO		= ydDaoUtils.paraRecChkNull(inDto[i], "STL_NO"						);
				szYD_MTL_WT		= ydDaoUtils.paraRecChkNull(inDto[i], "YD_MTL_WT"					);
				szUser			= ydDaoUtils.paraRecChkNull(inDto[i], "YD_USER_ID"					);
				szYD_STK_BED_NO	= ydDaoUtils.paraRecChkNull(inDto[i], "YD_STK_BED_NO"				);	// 화면에서 입력한 Bed
				szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(inDto[i], "YD_STK_LYR_NO"				);	// 화면에서 입력한 단
				
				szMsg			= "[JSP Session] 차량작업관리 화면 상차완료처리 - 1,2후판 정정야드 차량이송재료 등록 : ["+( i+ 1)+"]재료번호 - 차량스케줄ID["+szYD_CAR_SCH_ID+"], STL_NO["+szSTL_NO+"] ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recPara.setField("YD_CAR_SCH_ID"	, szYD_CAR_SCH_ID	);
				recPara.setField("STL_NO"			, szSTL_NO			);
				recPara.setField("REGISTER"			, szUser			);
				recPara.setField("YD_STK_BED_NO"	, szYD_STK_BED_NO	);
				recPara.setField("YD_STK_LYR_NO"	, szYD_STK_LYR_NO	);
				
				// 작업예약재료에 등록할 Bed정보와 단정보도 Set한다.
				recTemp.setField("YD_STK_BED_NO"	, szYD_STK_BED_NO	);
				recTemp.setField("YD_STK_LYR_NO"	, szYD_STK_LYR_NO	);
				
				
				intRtnVal = ydCarFtmvMtlDao.insYdCarftmvmtl(recPara);
				
				if( intRtnVal == 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 차량이송재료["+szSTL_NO+"] 등록 시 오류발생 1 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_FAILURE);
				}else if( intRtnVal < 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리 - 차량이송재료["+szSTL_NO+"] 등록 시 오류발생 2 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_FAILURE);
				}
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 차량이송재료["+szSTL_NO+"] 등록 성공 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//------------------------------------------------------------------------------------------------------
				
				
				//------------------------------------------------------------------------------------------------------
				// 작업예약재료 삭제 처리
				//------------------------------------------------------------------------------------------------------
				outRec.setField("STL_NO" + (i + 1), szSTL_NO);
				outRec.setField("STL_WT" + (i + 1), szYD_MTL_WT);
				
				//작업예약재료 삭제처리
				
				recTemp.setField("STL_NO", szSTL_NO);
				intRtnVal = ydWrkbookMtlDao.updYdWrkbookmtl(recTemp, 0);
				if( intRtnVal < 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 작업예약재료 삭제 시 오류발생 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					//throw new DAOException(YdConstant.RETN_CD_FAILURE);
				}else if( intRtnVal == 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 작업예약재료 삭제 시 해당 재료가 존재하지 않음 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}else{
					szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 작업예약재료 삭제 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				//------------------------------------------------------------------------------------------------------
			}
			
			//------------------------------------------------------------------------------------------------------
			// 작업예약 삭제 처리
			//------------------------------------------------------------------------------------------------------
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 작업예약 삭제 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			recTemp         = JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recTemp.setField("DEL_YN", "Y");
			intRtnVal = ydWrkbookDao.updYdWrkbook(recTemp, 0);
			if( intRtnVal < 0  ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 작업예약["+szYD_WBOOK_ID+"] 삭제 시 오류발생 : 반환값 - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}else if( intRtnVal == 0  ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 작업예약["+szYD_WBOOK_ID+"] 삭제 시 작업예약이 존재하지 않음 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 작업예약["+szYD_WBOOK_ID+"] 삭제 성공 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//------------------------------------------------------------------------------------------------------
			
			
			//------------------------------------------------------------------------------------------------------
			// 공통테이블 업데이트 처리
			//------------------------------------------------------------------------------------------------------
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 공통테이블 업데이트 처리 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szRtnMsg = YdCommonUtils.procCarLoadCmplForPlateGds(szYD_CAR_SCH_ID, YdConstant.YD_STR_LOC_CURR, szMethodName);
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				szMsg="[JSP Session] 차량작업관리 화면 상차완료시 공통테이블 업데이트 처리 성공 - 현위치 개소코드["+szWLOC_CD+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				szMsg="[JSP Session] 차량작업관리 화면 상차완료시 공통테이블 업데이트 처리 실패 - 현위치 개소코드["+szWLOC_CD+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 공통테이블 업데이트 처리 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//------------------------------------------------------------------------------------------------------
			
			
			//------------------------------------------------------------------------------------------------------
			//4. 상차완료를 구내운송으로 전송
			//------------------------------------------------------------------------------------------------------
			outRec.setField("TRN_WRK_MTL_GP", (szYD_MTL_ITEM.equals("") ? "" : szYD_MTL_ITEM.substring(0, 1)));
			outRec.setField("MTL_UGNT_GP", "");
			outRec.setField("HCR_GP", szHCR_GP);
			outRec.setField("CARLD_SH", "" + inDto.length);
			outRec.setField("CARLD_CMPL_DT", szCurrDate);
			
			//ydUtils.displayRecord(szOperationName, outRec);
			//deleComm.jmsQSnder(szQueueName, outRec);
			//YdCommonUtils.sndLdStartNComplTc(recLdStart, outRec);
			
			YdDelegate ydDelegate = new YdDelegate();
			
			//------------------------------------------------------------------------------------------------------
			// 이송상차완료실적[YDYDJ770] 2후판정정야드 송신 
			//------------------------------------------------------------------------------------------------------	
			if(YdConstant.WLOC_CD_B_PLATE_PLANT.equals(szWLOC_CD)) {
				//2후판SIZING 개소코드(DWY23) 일 경우만 전문 송신
				int cnt = 0;
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  시 - 이송상차완료실적[YDYDJ770]을 정정야드로 전송 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				JDTORecord recLdCmpl1  = JDTORecordFactory.getInstance().create();
				szTcCode  = "YDYDJ770";
				recLdCmpl1.setField("JMS_TC_CD", 				szTcCode);
				recLdCmpl1.setField("YD_GP", 					"F"); //F:2후판 정정야드	
				
				for(int i = 0; i < inDto.length ;  i++ ) {
					szSTL_NO = ydDaoUtils.paraRecChkNull(inDto[i], "STL_NO");
					if(!"".equals(szSTL_NO)) {
						cnt++;
						if(cnt>10) {
							break;
						}						
					}					
					recLdCmpl1.setField("STL_NO" + (i + 1), szSTL_NO);
				}
				recLdCmpl1.setField("PL_WR_GDS_TOT_SH", Integer.toString(cnt));				
				
				ydDelegate.sendMsg(recLdCmpl1);
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  시 - 이송상차완료실적[YDYDJ770]을 정정야드로 전송 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
			}
			//------------------------------------------------------------------------------------------------------
			
			//------------------------------------------------------------------------------------------------------
			// 상차개시전문 송신
			//------------------------------------------------------------------------------------------------------
			
			//열연재열재 개소코드 
			if(!YdConstant.WLOC_CD_C_HR_PLANT.equals(szWLOC_CD)) { 
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  시 - 상차개시전문을 구내운송으로 전송 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recLdStart         = JDTORecordFactory.getInstance().create();
				szTcCode = "YDTSJ007";
				//2. 상차개시를 구내운송으로 전송
				recLdStart.setField("JMS_TC_CD", 			szTcCode);
				recLdStart.setField("YD_CAR_SCH_ID", 		szYD_CAR_SCH_ID);
				
				ydDelegate.sendMsg(recLdStart);
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  시 - 상차개시전문을 구내운송으로 전송 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			}
			//------------------------------------------------------------------------------------------------------
			
			
			//------------------------------------------------------------------------------------------------------
			// 상차완료전문 송신
			//------------------------------------------------------------------------------------------------------
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  시 - 상차완료전문을 구내운송으로 전송 시작(전문버퍼사용)";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			JDTORecord recLdCmpl         = JDTORecordFactory.getInstance().create();
			szTcCode = "YDTSJ008";
			recLdCmpl.setField("JMS_TC_CD", 				YdConstant.YDYDJ701);
			recLdCmpl.setField(YdConstant.BUFFER_TC_CD, 	szTcCode);
			recLdCmpl.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
			
			//recPara         = JDTORecordFactory.getInstance().create();
			//recPara.setField("JMS_TC_CD", 					"YDYDJ701");
			//recPara.setField(YdConstant.TC_BODY, 			recLdCmpl);
			
			ydDelegate.sendMsg(recLdCmpl);
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  시 - 상차완료전문을 구내운송으로 전송 완료(전문버퍼사용)";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//------------------------------------------------------------------------------------------------------
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return YdConstant.RETN_CD_SUCCESS;
	}//end of complCarLdLot
	
	
}