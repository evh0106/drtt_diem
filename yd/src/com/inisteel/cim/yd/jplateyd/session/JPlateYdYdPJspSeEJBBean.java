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
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.DateHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
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

import com.inisteel.cim.yd.jplateyd.delegate.JPlateYdDelegate;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdCommonUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdCrnSchUtil;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdStkLocVO;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdToLocUtil;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.yd.jsp.common.CmnUtil;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;

import java.util.HashMap;
import java.util.List;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.14 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;


/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="JPlateYdYdPJspSeEJB" jndi-name="JPlateYdYdPJspSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class JPlateYdYdPJspSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YdSlabUtils commUtils = new YdSlabUtils();

	private static final String SZ_SESSION_NAME = JPlateYdYdPJspSeEJBBean.class.getName();

	private JPlateYdUtils    	ydUtils 	= new JPlateYdUtils();
    private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();
	private JPlateYdDelegate	ydDelegate 	= new JPlateYdDelegate();

	private JPlateYdCommDAO 	commDao 	= new JPlateYdCommDAO();
	
	private YdPICommDAO 		ydPICommDAO = new YdPICommDAO();

    
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
	 * [1후판정정야드] 야드Map관리 열 수정
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

				recPara.setField("MSG_ID", 			"YDY2L001");
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
	 * [1후판정정야드] 야드Map관리 베드 수정
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

				recPara.setField("MSG_ID", 		"YDY2L001");
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
	 *  [1후판정정야드] 오퍼레이션명 : 스케줄 삭제
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

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.03 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			szMsg = "[Jsp Session : " + szOperationName + "] 메소드 시작   ";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//========크레인스케줄 삭제==========//
			//파라미터 null 체크
			szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
			szYD_SCH_CD     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			szDEL_YN        = ydDaoUtils.paraRecChkNull(msgRecord, "DEL_YN");
			szYD_GP			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP", JPlateYdConst.YD_GP_P_PLATE_YARD);
			szMODIFIER      = ydDaoUtils.paraRecModifier(msgRecord);

			//파라미터 레코드 편집
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
			recPara.setField("YD_SCH_CD",     szYD_SCH_CD);
			recPara.setField("DEL_YN",        szDEL_YN);
			recPara.setField("MODIFIER",      szMODIFIER);

			//스케줄코드가 동일하고 크레인스케줄 ID이상인 크레인스케줄ID를 SELECT (추가 : 스케줄 ID에 포함된 같은 작업예약정보에서만 추출)
			rsGetCrnSch = JDTORecordFactory.getInstance().createRecordSet("YD");

			szMsg = "[Jsp Session : " + szOperationName + "] 스케줄코드가 동일하고 크레인스케줄 ID이상인 크레인스케줄ID를 SELECT";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			intRtnVal = ydCrnSchDao.getByYdCrnSchIdOver(recPara, rsGetCrnSch);		// intGp == 5

			//더 이상 삭제 작업이 없는경우
			if (intRtnVal < 1) {
				szMsg = "[Jsp Session : " + szOperationName + "] 삭제 작업이 완료되었습니다";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				outRecord.setField("RTN_CD" , "1");
				outRecord.setField("RTN_MSG", szMsg);
				return outRecord;
			}

			//레코드셋을 역순으로
			szMsg = "[Jsp Session : " + szOperationName + "] 레코드셋을 역순으로 정렬 - reverseOrder";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			rsGetCrnSch.reverseOrder();
			//레코드셋의 커서를 처음으로

			szMsg = "[Jsp Session : " + szOperationName + "] 레코드셋처음으로 이동";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			rsGetCrnSch.first();

			szMsg = "[Jsp Session : " + szOperationName + "] 선택된 건수 :" + rsGetCrnSch.size();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);


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
				szMsg = "[Jsp Session : " + szOperationName + "] 해당 크레인스케줄ID로 크레인작업재료를 SELECT ";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				rsGetCrnMtl = JDTORecordFactory.getInstance().createRecordSet("temp");
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID",	szYdSchId);

				intRtnVal = ydCrnSchDao.getYdCrnWrkMtl(recGetCrnSch, rsGetCrnMtl);		// intGp == 3

				//에러리턴
				if (intRtnVal <= 0) {

					szMsg = "[Jsp Session : " + szOperationName + "] 실패! 해당 작업재료 조회 ERROR :" + intRtnVal;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
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
					szMsg = "[JSP Session : " + szOperationName + "] 크레인 작업지시 취소전문 L2 전송 --- START";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					recPara   = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CRN_SCH_ID",    	szYdSchId);
					recPara.setField("YD_WRK_PROG_STAT", 	szYdWrkProgStat);   	// 이모듈을 탈려면 항상 '1'의값이 들어옴
					recPara.setField("MSG_GP",           	"D");
					
					recPara.setField("MSG_ID",    			"YDY2L004");
					recPara.setField("YD_GP",            	JPlateYdConst.YD_GP_P_PLATE_YARD);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.03 recPara에 logId 추가 
					recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

					szRtnMsg = ydDelegate.sendMsg(recPara);

					szMsg = "[JSP Session : " + szOperationName + "] 크레인 작업지시 취소전문 L2 전송 --- END >>>> " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				}

				//------------------------------------------------------------------------------------------------
				// 권상/ 권하 위치 Log
				//------------------------------------------------------------------------------------------------
				szMsg  = "권상지시위치 : " + szYD_UP_WO_LOC + ", 권상 지시단  : " + szYD_UP_WO_LAYER + " >>>> ";
				szMsg += "권하지시위치 : " + szYD_DN_WO_LOC + ", 권하 지시단  : " + szYD_DN_WO_LAYER;

				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				//------------------------------------------------------------------------------------------------
				// 권하위치 원복
				//------------------------------------------------------------------------------------------------
				if ("".equals(szYD_DN_WO_LOC) || "".equals(szYD_DN_WO_LAYER) || "XX".equals(ydUtils.substr(szYD_DN_WO_LOC,0,2))) {
					//권하위치가 올바르게 잡혀있지 않을때 에러처리를 원한다면 RollBack 을 시킬수 있다.
					szMsg = "[Jsp Session : " + szOperationName + "] : 권하위치가 올바른형식이 아니라 원복시킬수 없습니다. >>>> " + szYD_DN_WO_LOC;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING, logId);
				} else {

					//레코드의 커서를 처음으로
					szMsg = "[Jsp Session : " + szOperationName + "] 권하지시위치 " + szYD_DN_WO_LOC + "-" + szYD_DN_WO_LAYER;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					szMsg = "[Jsp Session : " + szOperationName + "] 크레인 작업재료 매수 :: " + rsGetCrnMtl.size();
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

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

						szMsg = "[Jsp Session : " + szOperationName + "] 기존 지시위치 에 쌓여 있는 정보 CLEAR ";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.03 recPara에 logId 추가 
						recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

						intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);

						//에러리턴
						if (intRtnVal < 1) {
							szMsg = "[Jsp Session : " + szOperationName + "] : 권하 지시 위치 CLEAR 실패";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING, logId);
						} else {

							szMsg = "[JSP Session] " + szOperationName + "기존 지시위치 에 쌓여 있는 정보 CLEAR 성공 [ " + Integer.toString(intRtnVal) + " ] ";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
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

						szMsg = "[Jsp Session : " + szOperationName + "] >>>> 권상지시 정보  : " + szYD_UP_WO_LOC + "-" + szYD_UP_WO_LAYER;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING, logId);

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
							szMsg = "[Jsp Session : " + szOperationName + "] >>>> 적치단 테이블에 권상위치 적치상태 원복  실패";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						} else {
							szMsg = "[Jsp Session : " + szOperationName + "] >>>> 적치단 테이블에 권상위치 적치상태 원복  성공";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						}

						//다음 스케줄 작업 재료 레코드로가기 전 작업한 스케줄 재료를 삭제 처리 해준다.
						szMsg = "[Jsp Session : " + szOperationName + "] : 크레인스케줄 작업 재료 삭제처리";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_CRN_SCH_ID", 	szYdSchId);
						recPara.setField("DEL_YN", 			"Y");
						recPara.setField("MODIFIER", 		szMODIFIER);
						recPara.setField("STL_NO", 			recGetCrnMtl.getField("STL_NO"));

						intRtnVal = ydCrnWrkMtlDao.delYdCrnWrkMtl(recPara);

						if (intRtnVal < 1) {
							szMsg = "[Jsp Session : " + szOperationName + "] : 크레인스케줄 작업 재료 삭제처리시 ERROR";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						} else {
							szMsg = "[Jsp Session : " + szOperationName + "] : 크레인스케줄 작업 재료 삭제처리 성공";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						}

						//------------------------------------------------------------------------------------------------
						// 스케줄  작업 재료 삭제시 저장품에 등록된 작업예약 ID, 스케줄 CD Clear
						//------------------------------------------------------------------------------------------------
						szMsg = "[Jsp Session : " + szOperationName + "] : 저장품  작업예약 ID, 스케줄코드 CLEAR";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("STL_NO", 		recGetCrnMtl.getField("STL_NO"));
						recPara.setField("MODIFIER", 	szMODIFIER);
						recPara.setField("YD_WBOOK_ID", "");
						recPara.setField("YD_SCH_CD", 	"");

						intRtnVal = ydStockDao.updYdStockWbook(recPara);

						if (intRtnVal < 0) {
							szMsg = "[Jsp Session : " + szOperationName + "] : 저장품  작업예약 ID, 스케줄코드 CLEAR ERROR";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						} else if (intRtnVal == 0) {
							szMsg = "[Jsp Session : " + szOperationName + "] : 저장품  작업예약 ID, 스케줄코드 CLEAR 대상 없음";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING, logId);
						} else {
							szMsg = "[Jsp Session : " + szOperationName + "] : 저장품  작업예약 ID, 스케줄코드 CLEAR 성공";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						}
					}

					//------------------------------------------------------------------------------------------------
					//	크레인스케줄 삭제처리
					//------------------------------------------------------------------------------------------------
					szMsg = "[Jsp Session : " + szOperationName + "] : 크레인스케줄  삭제처리";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CRN_SCH_ID", 	szYdSchId);
					recPara.setField("DEL_YN", 			"Y");
					recPara.setField("MODIFIER", 		szMODIFIER);

					intRtnVal = ydCrnSchDao.delYdCrnSch(recPara);

					if (intRtnVal > 0) {
						szMsg = "[Jsp Session : " + szOperationName + "] : 크레인스케줄  삭제처리 완료";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					} else {
						szMsg = "[Jsp Session : " + szOperationName + "] : 크레인스케줄  삭제처리 실패";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

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

						szMsg = "[Jsp Session : " + szOperationName + "] : 해당 작업예약 정보에  남은 스케줄 조회시 ERROR";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

						bUpdEqpFlag = false;

					} else if (intRtnVal == 0) {

						szMsg = "[Jsp Session : " + szOperationName + "] : 해당 작업예약 정보에  남은 스케줄 정보가 없으므로 작업대기 상태로 UPDATE 합니다.";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						//해당 스케줄 코드로 같은 편성된 스케줄이 없는지 확인한다.(다른작업예약 ID가 편성되었을경우)
						rsCrnSchInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
						recPara      = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_SCH_CD", szYD_SCH_CD);

						intRtnVal = ydCrnSchDao.getByYdSchCd(recPara, rsCrnSchInfo);		// intGp == 6

						if (intRtnVal < 0) {
							szMsg = "[Jsp Session : " + szOperationName + "] :남은 스케줄코드로 스케줄 조회시 ERROR";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							bUpdEqpFlag  = false;

						}  else if (intRtnVal == 0) {
							szMsg = "[Jsp Session : " + szOperationName + "] :남은 스케줄 정보가 없으므로 작업대기 상태로 UPDATE 합니다.";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
							szUpdEqpstat = JPlateYdConst.YD_EQP_STAT_IDLE;
							bUpdEqpFlag  = true;

						} else {
							szMsg = "[Jsp Session : " + szOperationName + "] :해당 스케줄 코드에 스케줄 정보가 남아 있어 설비정보는 남은스케줄 첫번째 진행상태 정보로 UPDATE 합니다.";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
							recGetEqp    = JDTORecordFactory.getInstance().create();
							rsCrnSchInfo.first();
							recGetEqp    = rsCrnSchInfo.getRecord();
							szUpdEqpstat = ydDaoUtils.paraRecChkNull(recGetEqp, "YD_WRK_PROG_STAT");
							bUpdEqpFlag  = true;
						}

					} else {

						szMsg = "[Jsp Session : " + szOperationName + "] 해당 작업예약 정보에 스케줄 정보가 남아 있어 설비정보는 UPDATE 하지 않습니다.";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
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
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

							} else {

								recPara = JDTORecordFactory.getInstance().create();
								recPara.setField("YD_EQP_ID", 		szEqpId);
								recPara.setField("YD_EQP_STAT", 	szUpdEqpstat);
								recPara.setField("MODIFIER",		szMODIFIER);

								szMsg = " + +++++++++ 해당 스케줄 크레인(" + szEqpId + ") 설비상태 [" + szUpdEqpstat + "]로 변경 +++++++++++++++++ + ";
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

								intRtnVal = ydEqpDao.updYdEqpStat(recPara);			// intGp == 0

								if (intRtnVal < 0) {
									szMsg = szEqpId  + "설비정보를 변경 실패 하였습니다.";
									ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg,JPlateYdConst.ERROR, logId);

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
	 *  [1후판정정야드] 오퍼레이션명 : 작업예약 삭제
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

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.10 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		szMsg = "작업예약 삭제 처리 기능 시작 >>>> " + msgRecord.toString();
	    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		try {
			szMODIFIER  	= ydDaoUtils.paraRecModifier(msgRecord);
			szYD_WBOOK_ID 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
			szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");

			// 크레인스케줄ID 미존재시는 작업예약만 삭제함
			if (!"".equals(szYD_CRN_SCH_ID)) {

				if ("".equals(ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID"))) {
					//szMsg = "스케줄 취소 처리(" + szMethodName + ") 실패, YD_CRN_SCH_ID값이 없음";
					szMsg = "스케줄 ID 정보가 없어서 작업예약 삭제처리를 하지 못하였습니다";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					outRecord.setField("RTN_CD" , "0");
					outRecord.setField("RTN_MSG", szMsg);
					return outRecord;
				}

				//파라미터 레코드 setting
				recPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
				
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.10 recPara에 logId 추가  
//-------------------------------------------------------------------------------------------------------------------------
				recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
				
				rsRtnVal  = JDTORecordFactory.getInstance().createRecordSet("temRs");
				intRtnVal = ydCrnSchDao.getYdCrnSchYdP(recPara, rsRtnVal);				// intGp == 0

				if (intRtnVal < 1) {
					szMsg = "해당크레인 스케줄이 존재하지않습니다";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					outRecord.setField("RTN_CD" , 	"0");
					outRecord.setField("RTN_MSG", 	szMsg);
					return outRecord;
				}

				rsRtnVal.first();
				recCheck = rsRtnVal.getRecord();
				szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(recCheck, "YD_WBOOK_ID");

				if ("".equals(szYD_WBOOK_ID)) {
					szMsg = "해당크레인 스케줄에 작업예약 정보가 존재하지않습니다.";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
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
					szMsg = "[Jsp Session : " + szOperationName + "] 작업예약[" + szYD_WBOOK_ID + "] 대차/차량 스케줄 Clear성공 ";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				} else if (JPlateYdConst.RETN_CD_FAILURE.equals(szRtnMsg)) {
					szMsg = "[Jsp Session : " + szOperationName + "] 작업예약[" + szYD_WBOOK_ID + "] 대차/차량 스케줄 Clear 실패 ";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

				} else {
					szMsg = "[Jsp Session : " + szOperationName + "] 작업예약[" + szYD_WBOOK_ID + "]  " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				}
			}

			//------------------------------------------------------------------------------------------------
			//	작업예약 과 작업예약재료 삭제
			//------------------------------------------------------------------------------------------------
			szRtnMsg = JPlateYdCommonUtils.delYdWrkbookNMtl(szYD_WBOOK_ID, szMODIFIER);

			if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szMsg = "[Jsp Session : " + szOperationName + "] 작업예약[" + szYD_WBOOK_ID + "]삭제 성공";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			} else {
				szMsg = "[Jsp Session : " + szOperationName + "] 작업예약[" + szYD_WBOOK_ID + "]삭제 실패 - 메세지 : " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			}

			szEqpId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			szYD_SCH_CD	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");

			//설비 ID 정보와 스케줄 코드가 들어왔을때만 실행한다.
			if ("".equals(szEqpId) || "".equals(szYD_SCH_CD)) {

				szMsg  = "[JSP Session : " + szOperationName + "] 설비 ID 정보" + szEqpId + "스케줄 코드 [" + szYD_SCH_CD + "]";
				szMsg += "중 누락된 정보가 발생하여 해당 크레인 작업지시를 호출하지 않고 마칩니다";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				outRecord.setField("RTN_CD" , 	"1");
				outRecord.setField("RTN_SND", 	"N");
				outRecord.setField("RTN_MSG", 	"성공");
				return outRecord;
			}

			szMsg = "[JSP Session : " + szOperationName + "] 설비 ID 정보" + szEqpId + "스케줄 코드 [" + szYD_SCH_CD + "]";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			szYdGp = ydUtils.substr(szEqpId, 0,1);

			szLogMsg = "[JSP Session] - 작업예약 삭제 - 크레인 작업지시 : 야드구분[" + szYdGp + "]";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			outRecord = JDTORecordFactory.getInstance().create();
			outRecord.setField("MSG_ID", 			"YDYDJX55");				// YDYDJ755 :: 크레인 작업지시요구
			outRecord.setField("YD_EQP_ID", 		szEqpId);
			outRecord.setField("YD_WRK_PROG_STAT", 	JPlateYdConst.YD_EQP_STAT_DN_CMPL);
			outRecord.setField("YD_SCH_CD", 		szYD_SCH_CD);
			outRecord.setField("RTN_CD" , 			"1");
			outRecord.setField("RTN_SND", 			"Y");
			outRecord.setField("RTN_MSG", 			"성공");
			return outRecord;

		} catch (Exception e) {
			szMsg = "[JSP Session : " + szOperationName + "] 크레인 작업지시 정보 호출가 발생하였습니다";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
		}

		outRecord.setField("RTN_CD" , "1");
		outRecord.setField("RTN_MSG", "성공");
		return outRecord;
	} // end of delJPlateWBook
	
	/**
	 * [1후판정정야드]  (작업)크레인 변경
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
					JPlateYdUtils.updYdCrnschBedDataYdP(recTemp2);

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
				 	// 1후판 정정명령선택 취소처리 수행
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("JMS_TC_CD", 			"Y2YDL012");
					recPara.setField("YD_EQP_ID", 			szEqpId);				// 야드설비ID
					recPara.setField("YD_CMD_PKUP_GP", 		"C");					// 야드명령선택구분 - S:명령선택, C:취소
					recPara.setField("YD_CRN_SCH_ID", 		szYdCrnSchId);			// 야드크레인스케쥴ID
					recPara.setField("MODIFIER", 			szModifier);

					ejbConn  = new EJBConnector("default", this);
					szRtnMsg = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY2CrnOrderSel", recPara);
					szLogMsg = "명령선택 취소 EJB 호출 .. END >>>> " + szRtnMsg;
	        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				}

			 	// 명령선택 처리 수행
				szLogMsg = "명령선택 EJB 호출 .. START";
        		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
	
			 	// 1후판 명령선택 처리 수행
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("JMS_TC_CD", 			"Y2YDL012");
				recPara.setField("YD_EQP_ID", 			szChgCrn);				// 야드설비ID
				recPara.setField("YD_CMD_PKUP_GP", 		"S");					// 야드명령선택구분 - S:명령선택, C:취소
				recPara.setField("YD_CRN_SCH_ID", 		szYdCrnSchId);			// 야드크레인스케쥴ID
				recPara.setField("MODIFIER", 			szModifier);

				ejbConn  = new EJBConnector("default", this);
				szRtnMsg = (String)ejbConn.trx("JPlateYdL2RcvSeEJB", "procY2CrnOrderSel", recPara);

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
	 * [1후판정정야드] 권하위치 변경 (크레인작업관리 화면)
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
				szYdGp		= ydDaoUtils.paraRecChkNull(inDto[ii], "YD_GP", JPlateYdConst.YD_GP_P_PLATE_YARD);

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
					if ("BC".equals(szYdStkSpanGp) ||"BS".equals(szYdStkSpanGp) || "RT".equals(szYdStkSpanGp) || "CN".equals(szYdStkSpanGp) ||
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
       			szRtnMsg = JPlateYdCrnSchUtil.uptCrnSchXYCordYdP(recPara);
        		
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
        		lb_updYdCrnBed = JPlateYdUtils.updYdCrnschBedDataYdP(recPara);

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
					//recInTemp.setField("MSG_ID",           	"YDY2L004");
					recInTemp.setField("MSG_ID",           	"YDY2L004V2");
	        			
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
	 * [1후판정정야드] 저장위치수정
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
			szYdGp			= ydDaoUtils.paraRecChkNull(inRec, "YD_GP", JPlateYdConst.YD_GP_P_PLATE_YARD);			// 야드구분

			inRec.setField("MODIFIER",		szModifier);			// 수정자
			inRec.setField("YD_SCH_ST_GP",	szYdSchStGp);			// 야드스케줄 기동구분 (A:AUTO, M:MANUAL, B:BACK-UP)

			//------------------------------------------------------------------
			// 2013.07.30 현재 저장위치가 2후판 정정야드가 아닐경우 오류로 처리
			//------------------------------------------------------------------
			szRtnMsg = JPlateYdCommonUtils.checkUpdYdLocYdP(szStlNo, szYdGp, "N");	
			
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
					szRtnMsg = "끼워넣기로 저장위치 수정시 TO위치에 권상/권하예약 정보가 존재하여 수정이 불가합니다. 재료번호["+szStlNo+"]";
					szMsg    = "[ " +szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
			}

			// 재료관련 정보로 이력정보를 추가적으로 넣을부분이 존재하면 재료정보를 조회한 후 이부분에 추가해줌
			rsResult = JDTORecordFactory.getInstance().createRecordSet("rsTemp");
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO",		szStlNo);			// 재료번호
			recPara.setField("YD_GP",		szYdGp);			// 야드구분
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
			// --> 저장위치제원은 안바뀌고 저장품제원이 변경됨으로 YDY2L002 변경
			// 후판조업 저장위치변경정보 전송 (YDPRJ011),
			// 1 후판전단L2 RT BOOK-IN 실적 전송 (YDP2L501)
			// 1 후판열처리L2 RT BOOK-IN 실적 전송 (YDP3L501)
			// --> updJPlateYdStkPosFix 메서드 에서 실시 함으로 SKIP
			//------------------------------------------------------------
			if ("Y".equals(szSendFlag)) {
				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	recL2Para = JDTORecordFactory.getInstance().create();
	        	recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
	        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
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
	 * [1후판정정야드] 저장위치삭제
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
			szRtnMsg = JPlateYdCommonUtils.checkUpdYdLoc(szStlNo, JPlateYdConst.YD_GP_P_PLATE_YARD, "Y");
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
			recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);		
			
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
			// --> 저장위치제원은 안바뀌고 저장품제원이 변경됨으로 YDY2L002 변경
			// 후판조업 저장위치변경정보 전송 (YDPRJ011),
			// 1 후판전단L2 RT BOOK-IN 실적 전송 (YDP2L501)
			// 1 후판열처리L2 RT BOOK-IN 실적 전송 (YDP3L501)
			// --> updJPlateYdStkPosFix 메서드 에서 실시 함으로 SKIP
			//------------------------------------------------------------
			if ("Y".equals(szSendFlag)) {
				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	recL2Para = JDTORecordFactory.getInstance().create();
	        	recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE : 저장품제원정보
	        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
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
	 * [1후판정정야드] 저장위치수정 체크
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
				recPara.setField("YD_GP", JPlateYdConst.YD_GP_P_PLATE_YARD);
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
				szRtnStr = "저장품에 데이터가 없습니다. 재료번호["+szStlNo+"]";
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
				szRtnStr = "해당 저장위치 하단에 권상/권하예약 정보가 존재합니다. 재료번호["+szStlNo+"]" ;
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
	 * [1후판정정야드] 후판정정야드 저장위치 수정
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

							szMsg = "미 변경된 재료 정보는 UPDATE 하지 않습니다.  재료번호["+szStlNo+"]";
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

				recDelPara.setField("STL_NO"				, szStlNo);
				recDelPara.setField("YD_STK_LYR_MTL_STAT"	, "");
				recDelPara.setField("YD_GP"					, szYdGp);

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
				szRtnStr = "야드재료 저장위치 정보 UPDATE 실패 하였습니다  재료번호["+szStlNo+"]" + Integer.toString(intRtnVal);
				szMsg      = "JSP-SESSION "+szOperationName+"] " + szRtnStr;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnStr;
			}

			szMsg = "JSP-SESSION [저장위치수정] 끝";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			// 2013.08.27 : 저장위치 수정후 적치재료의 길이의 합이 베드(열)의 길이를 초과 하는지 체크
			int iRemainMtlL = ydStkLyrDao.getRemainMtlL(recPara);
			if (iRemainMtlL < 0) {
				szRtnStr 	= "재료길이 합이 적치열길이 초과!!  재료번호["+szStlNo+"] :: " + Integer.toString(iRemainMtlL*(-1));
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
					// 조업L3 전문송신 (후판조업 저장위치변경정보 전송  - YDPRJ011)
					//---------------------------------------------------
					szMsg = "[ " +szOperationName + "] 조업L3 전문송신 START";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		        	recL3Para = JDTORecordFactory.getInstance().create();

		        	recL3Para.setField("MSG_ID", 			"YDPRJ011");
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
						// 1 후판전단L2 RT BOOK-IN 실적 전송 (YDP2L501)
						// 1 후판열처리L2 RT BOOK-IN 실적 전송 (YDP3L501)
						//------------------------------------------------------------
						if ("RT".equals(ydUtils.substr(szYdUpWrLoc, 2, 2))) {

							szMsg = "["+ szOperationName +"] RT BOOK-OUT 실적 전송 .. 시작";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

							recL2Para = JDTORecordFactory.getInstance().create();
							if("PB".equals(ydUtils.substr(szYdUpWrLoc, 0, 2))||szYdUpWrLoc.startsWith("PART13")) {
								recL2Para.setField("MSG_ID", 		   "YDP3L501");
								recL2Para.setField("STL_NO",			szStlNo);								// 재료번호
								recL2Para.setField("OPERATION_TYPE",	"2");									// 1:Book In, 2:Book Out
								recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szYdUpWrLoc, 0, 6));		// TO위치
								recL2Para.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdUpWrLoc, 6, 2));    	// 야드적치BED번호
								recL2Para.setField("YD_EQP_ID", 	    "P"+ydUtils.substr(szYdUpWrLoc, 1, 2)+"CR"+ydUtils.substr(szYdUpWrLoc, 1, 2)+"1");   
								szRtnMsg = ydDelegate.sendMsg(recL2Para);

								szMsg = "["+ szOperationName +"] RT BOOK-OUT 실적 전송 .. 완료>>>>"+szRtnMsg;
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
							} else {
//								recL2Para.setField("MSG_ID", 		"YDP2L501");
//								recL2Para.setField("STL_NO",			szStlNo);								// 재료번호
//								recL2Para.setField("OPERATION_TYPE",	"2");									// 1:Book In, 2:Book Out
//								recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szYdUpWrLoc, 0, 6));		// TO위치
//								recL2Para.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdUpWrLoc, 6, 2));    	// 야드적치BED번호
//
//								szRtnMsg = ydDelegate.sendMsg(recL2Para);
								
								szRtnMsg = JPlateYdCommonUtils.procJPlateSmsSend("2", szStlNo, szYdUpWrLoc,"P"+ydUtils.substr(szYdUpWrLoc, 1, 2)+"CR"+ydUtils.substr(szYdUpWrLoc, 1, 2)+"1");

			    		        szMsg = "["+ szOperationName +"] 1 후판전단L2 RT BOOK-OUT 실적 전송 .. 완료>>>>"+szRtnMsg;
			                    ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
							}
						}

						//------------------------------------------------------------
						// 1 후판전단L2 RT BOOK-IN 실적 전송 (YDP2L501)
						// 1 후판열처리L2 RT BOOK-IN 실적 전송 (YDP3L501)
						//------------------------------------------------------------
						if ("RT".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {

							szMsg = "["+ szOperationName +"] RT BOOK-IN 실적 전송 .. 시작";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

							recL2Para = JDTORecordFactory.getInstance().create();
							if("PB".equals(ydUtils.substr(szYdUpWrLoc, 0, 2))||szYdUpWrLoc.startsWith("PART13")) {
								recL2Para.setField("MSG_ID", 		"YDP3L501");
								recL2Para.setField("STL_NO",			szStlNo);								// 재료번호
								recL2Para.setField("OPERATION_TYPE",	"1");									// 1:Book In, 2:Book Out
								recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szYdDnWrLoc, 0, 6));		// TO위치
								recL2Para.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdDnWrLoc, 6, 2));    	// 야드적치BED번호
								recL2Para.setField("YD_EQP_ID", 	    "P"+ydUtils.substr(szYdUpWrLoc, 1, 2)+"CR"+ydUtils.substr(szYdUpWrLoc, 1, 2)+"1");  
								szRtnMsg = ydDelegate.sendMsg(recL2Para);

								szMsg = "["+ szOperationName +"] RT BOOK-IN 실적 전송 .. 완료>>>>"+szRtnMsg;
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
							} else {
//								recL2Para.setField("MSG_ID", 		"YDP2L501");
//								recL2Para.setField("STL_NO",			szStlNo);								// 재료번호
//								recL2Para.setField("OPERATION_TYPE",	"1");									// 1:Book In, 2:Book Out
//								recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szYdDnWrLoc, 0, 6));		// TO위치
//								recL2Para.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdDnWrLoc, 6, 2));    	// 야드적치BED번호
//
//								szRtnMsg = ydDelegate.sendMsg(recL2Para);
//
//								szMsg = "["+ szOperationName +"] RT BOOK-IN 실적 전송 .. 완료>>>>"+szRtnMsg;
//								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
								
								szRtnMsg = JPlateYdCommonUtils.procJPlateSmsSend("1", szStlNo, szYdDnWrLoc,"P"+ydUtils.substr(szYdDnWrLoc, 1, 2)+"CR"+ydUtils.substr(szYdDnWrLoc, 1, 2)+"1");

			    		        szMsg = "["+ szOperationName +"] 1 후판전단L2 RT BOOK-OUT 실적 전송 .. 완료>>>>"+szRtnMsg;
			                    ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);								
							}
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
	 * [1후판정정야드] 저장위치수정 List
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
				szYdGp		 = ydDaoUtils.paraRecChkNull(inDto[0], "YD_GP", JPlateYdConst.YD_GP_P_PLATE_YARD);
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
						recPara.setField("YD_SCH_CD",			"PXYD03MM");	// 스케쥴코드 : 저장위치 목록 수정
						recPara.setField("OLD_YD_STK_COL_GP",	szOldYdStkColGp);						// OLD위치 적치열구분
						recPara.setField("OLD_YD_STK_BED_NO",	szOldYdStkBedNo);						// OLD위치 적치베드 구분
						recPara.setField("OLD_YD_STK_LYR_NO",	szOldYdStkLyrNo);						// OLD위치 적치단 구분
						recPara.setField("YD_GP",				szYdGp);	// 야드구분

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
								recPara.setField("YD_SCH_CD",		"PXYD03MM");
								recPara.setField("MODIFIER", 		szModifier);

								szRtnMsg = this.insYdWrkHistPosFix(recPara);
								if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
									szMsg = "["+szOperationName+"] 저장위치변경이력 등록 오류 >>>> " + szRtnMsg+"  재료번호:"+szStlNo;
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
									szRtnMsg = "재료정보 삭제처리시 오류 발생! .... 오류코드 :" + Integer.toString(iRtnVal)+" 이전재료번호:"+szOldStlNo;
									szMsg    = "["+szOperationName+"] " + szRtnMsg;
									ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
									return szRtnMsg;
								}

								// ------------------------------------------------------------------------
								// 4.1.4. 야드L2 저장품제원 정보 송신 [YDY2L002]
								// ------------------------------------------------------------------------
					            szMsg = "["+szOperationName+"] 야드L2 저장위치삭제 정보 전송 ---- START";
					            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					        	recL2Para = JDTORecordFactory.getInstance().create();
					        	recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
					        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
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
								// 4.1.5. 후판조업 저장품제원 정보 송신 [YDPRJ011]
								// ------------------------------------------------------------------------
					            szMsg = "["+szOperationName+"] 1후판조업 저장위치 삭제 정보 전송 ---- START";
					            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					            recL3Para = JDTORecordFactory.getInstance().create();
					            recL3Para.setField("MSG_ID", 			"YDPRJ011");
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
				recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
				recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
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
	            recL3Para.setField("MSG_ID", 			"YDPRJ011");
	            recL3Para.setField("YD_STK_COL_FR", 	"");									// From적치열
	            recL3Para.setField("YD_STK_BED_FR", 	"");									// From적치BED
	            recL3Para.setField("YD_STK_COL_TO", 	szYdStkColGp);							// TO적치열
	            recL3Para.setField("YD_STK_BED_TO", 	"01");									// TO적치BED
	            recL3Para.setField("YD_EQP_WRK_SH", 	"");									// 야드설비작업매수
	            recL3Para.setField("ARR_STL_NO", 		szArrStlNo);

	        //	szRtnMsg = ydDelegate.sendMsg(recL3Para);
	            szRtnMsg = JPlateYdCommonUtils.sendL3YDPRJ011(recL3Para);

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
	 *  [1후판정정야드]  저장위치변경이력 등록 [저장위치수정, 스크랩처리]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String insYdWrkHistPosFix(JDTORecord inDto) throws DAOException {

		JPlateYdStockDAO	ydStockDao		= new JPlateYdStockDAO();
    	JPlateYdWrkHistDAO 	ydWrkHistDao	= new JPlateYdWrkHistDAO();
    	YdStockDao  ydStockDao2      = new YdStockDao();

		JDTORecordSet	rsStockInfo	= null;

		JDTORecord recPara 			= JDTORecordFactory.getInstance().create();

		JDTORecord recStockInfo 	= JDTORecordFactory.getInstance().create();

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


//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 로그 개선 
        String logId                     	= ydLogUtils.getJDTOLogId(inDto, "P");  			// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 		// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {

			szYdGp			= ydDaoUtils.paraRecChkNull(inDto, "YD_GP" ,	JPlateYdConst.YD_GP_P_PLATE_YARD 	);
			szStlNo			= ydDaoUtils.paraRecChkNull(inDto, "STL_NO"											);
			szYdUpWrLoc 	= ydDaoUtils.paraRecChkNull(inDto, "YD_UP_WR_LOC"									);		// 권상실적위치
			szYdUpWrLayer 	= ydDaoUtils.paraRecChkNull(inDto, "YD_UP_WR_LAYER"									);		// 권상실적단
			szYdDnWrLoc		= ydDaoUtils.paraRecChkNull(inDto, "YD_DN_WR_LOC"									);		// 권하실적위치
			szYdDnWrLayer	= ydDaoUtils.paraRecChkNull(inDto, "YD_DN_WR_LAYER"									);		// 권하실적단
			szYdSchCd		= ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_CD"										);
			szYdSchStGp		= ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_ST_GP", 	"B"								);
			szModifier 		= ydDaoUtils.paraRecModifier(inDto);

			//---------------------------------------
			// 이적작업  이력정보에 추가
			//---------------------------------------
			rsStockInfo = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
			recPara 	= JDTORecordFactory.getInstance().create();
			
			recPara.setField("STL_NO", szStlNo	);
			recPara.setField("YD_GP", szYdGp	);

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
			} else {
				// 재료미존재시는 Error 처리 .. 이함수 호출전에 미리 Insert함
				szRtnStr = "야드재료 정보가 존재하지 않습니다. >>>>" + szStlNo;
				szLogMsg = "JSP-SESSION " + szOperationName + "]" + szRtnStr;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			//	return szRtnStr;
			}

			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, ">>>>변경전 저장위치 ::" + szYdUpWrLoc + "-" + szYdUpWrLayer, JPlateYdConst.DEBUG, logId);
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, ">>>>변경후 저장위치 ::" + szYdDnWrLoc + "-" + szYdDnWrLayer, JPlateYdConst.DEBUG, logId);

			// 작업이력 - 권상정보 관련 입력
			if (!"".equals(szYdUpWrLoc)) {
				recStockInfo.setField("YD_UP_WR_LOC", 	szYdUpWrLoc									);
				recStockInfo.setField("YD_UP_WR_LAYER", szYdUpWrLayer								);
				recStockInfo.setField("YD_UP_CMPL_DT",	JPlateYdUtils.getCurDate("yyyyMMddHHmmss")	);
			} else {
				recStockInfo.setField("YD_UP_WR_LOC", 	""											);
				recStockInfo.setField("YD_UP_WR_LAYER", ""											);
				recStockInfo.setField("YD_UP_CMPL_DT",	""											);
			}

			recStockInfo.setField("YD_GP", 			szYdGp											);
			recStockInfo.setField("STL_NO", 		szStlNo											);

			// 작업이력 - 권하정보 입력
			recStockInfo.setField("YD_DN_WR_LOC",   szYdDnWrLoc										);
			recStockInfo.setField("YD_DN_WR_LAYER", szYdDnWrLayer									);
			recStockInfo.setField("YD_DN_CMPL_DT", 	JPlateYdUtils.getCurDate("yyyyMMddHHmmss")		);

			// 야드보조작업여부 - YD_AID_WRK_YN
			recStockInfo.setField("YD_AID_WRK_YN" , "N"												);

			if ("".equals(szYdSchCd)) {
			//	szYdSchCd = szYdGp + "XYDYDMM";		// FXYD01MM : 저장위치 수정 , FXYD02MM : 저장위치 삭제 , FXCN02MM : 스크랩처리
				if ("".equals(szYdDnWrLoc)) {
					szYdSchCd = "PXYD02MM";
				} else {
					szYdSchCd = "PXYD01MM";
				}
			}

			// 야드 스케줄 코드 - YD_SCH_CD
			recStockInfo.setField("YD_SCH_CD", 		szYdSchCd							);

			//스케줄 기준의 작업 크레인 정보를 넣어준다.
			recStockInfo.setField("YD_EQP_ID", 		szYdEqpId							);
			recStockInfo.setField("YD_GNT_GP", 		JPlateYdConst.YD_GNT_GP_MVSTK		);		// M
			recStockInfo.setField("YD_SCH_ST_GP", 	szYdSchStGp							);		// 야드스케줄 기동 구분
	        recStockInfo.setField("YD_WRK_HDS_DD",  JPlateYdUtils.getDefaultHdsDate()	);		// 계상일자
	        recStockInfo.setField("YD_WRK_DUTY",	JPlateYdUtils.getDefaultDuty()		);		// 작업근

			recStockInfo.setField("REGISTER", 		szModifier							);
			recStockInfo.setField("MODIFIER", 		szModifier							);

			// 이력정보 남기기
			intRtnVal = ydWrkHistDao.insYdWrkHist(recStockInfo);
			
			String ydStkColGp = szYdDnWrLoc.length()>6?szYdDnWrLoc.substring(0,6) : "";
			//니켈강베드 이적일시 공통테이블 동기화 추가 
			if("PF0101".equals(ydStkColGp) || "PF0102".equals(ydStkColGp)|| "PF0103".equals(ydStkColGp)|| "PF0199".equals(ydStkColGp) ){
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "니켈강베드 저장위치 수정시 공통테이블 동기화", JPlateYdConst.DEBUG, logId);
				
				//공통저장위치 수정
				JDTORecord commRecord = JDTORecordFactory.getInstance().create();
				
				String ydGp    = szYdDnWrLoc.substring(0,1);
				String ydBayGp = szYdDnWrLoc.substring(1,2);
				String ydEqpGp = szYdDnWrLoc.substring(2,4);
				String ydStkColNo = szYdDnWrLoc.substring(4,6);
				String szYdStkBedNo = szYdDnWrLoc.substring(6,8);
				String ydStrLoc   = ydGp + ydBayGp + ydEqpGp +ydStkColNo +szYdStkBedNo.substring(1,2) + szYdDnWrLayer;
				
				/*
                 *  PLATE공통 저장위치 UPDATE
                 */
                //-- NEW Version ---------------------------------------------------------
				commRecord.setField("YD_GP"				, ydGp);
				commRecord.setField("YD_BAY_GP"			, ydBayGp);
				commRecord.setField("YD_EQP_GP"			, ydEqpGp);
				commRecord.setField("YD_STK_COL_NO"		, ydStkColNo);
				commRecord.setField("YD_STK_BED_NO"		, szYdStkBedNo);
				commRecord.setField("YD_STK_LYR_NO"		, szYdDnWrLayer);
				commRecord.setField("YD_STR_LOC"		, ydStrLoc);
				commRecord.setField("FNL_REG_PGM"		, "insYdWrk");
				commRecord.setField("MODIFIER"			, "insYdWrk");
				commRecord.setField("PLATE_NO"			, szStlNo);

                /* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtPlatecommLOC */
                intRtnVal = ydStockDao2.updPtComm_LOC(commRecord, 1);
			}

			if (intRtnVal > 0) {
				szLogMsg = "JSP-SESSION " + szOperationName + "] 이력정보를 로깅하였습니다." ;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			} else {
				szRtnStr = "이력정보를 로깅 실패 하였습니다" + Integer.toString(intRtnVal);
				szLogMsg = "JSP-SESSION " + szOperationName + "] " + szRtnStr;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
				return szRtnStr;
			}

			//------------------------------------------------------------------
			// FROM 위치가 대차일 경우 YD_WRK_PLAN_TCAR(야드작업계획대차) 항목  CLEAR 처리
			//------------------------------------------------------------------
			if ("TC".equals(ydUtils.substr(szYdUpWrLoc, 2, 2)) && !"TC".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO",         		szStlNo		);
				recPara.setField("YD_WRK_PLAN_TCAR", 	""			);
				recPara.setField("REGISTER",       		szModifier	);
				recPara.setField("MODIFIER",       		szModifier	);

	    		intRtnVal = ydStockDao.updYdWrkPlanTcar(recPara);
	    		if (intRtnVal < 1) {
	    			szLogMsg = "야드작업계획대차 항목 CLEAR 실패 >>>> " + Integer.toString(intRtnVal);
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
	    		}
			}

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION [" + szOperationName + "] 끝  .. 결과 :: " + szRtnStr;
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}	
	
	/**
	 * [1후판정정야드] RT BOOK-OUT 작업지시 취소시 처리 (재료정보삭제, 적치위치CLEAR)
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

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.10 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(inDto, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------


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
			recPara.setField("YD_GP",				JPlateYdConst.YD_GP_P_PLATE_YARD);
			recPara.setField("MODIFIER", 			szModifier);

			intRtnVal = ydStockDao.delYdStock(recPara);
			if (intRtnVal < 0) {
				szRtnMsg = "재료정보 삭제처리시 오류 발생! .... 오류코드 :" + Integer.toString(intRtnVal);
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			//	return szRtnMsg;
			}

			// ------------------------------------------------------------------------
			// 2. 저장위치 CLEAR
			// ------------------------------------------------------------------------
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", 				szStlNo);             	// 재료번호
			recPara.setField("YD_STK_LYR_MTL_STAT", "C");             		// 야드적치단재료상태
			recPara.setField("YD_GP",				JPlateYdConst.YD_GP_P_PLATE_YARD);
			recPara.setField("MODIFIER", 			szModifier);
			
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.10 recPara에 logId 추가  
//-------------------------------------------------------------------------------------------------------------------------
			recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
			
			intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
			if (intRtnVal < 0) {
				szRtnMsg = "저장위치 삭제 처리시 오류 발생! .... 오류코드 :" + Integer.toString(intRtnVal);
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			//	return szRtnMsg;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			// throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION [" + szOperationName + "] 끝";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return szRtnMsg;
	} // end of delStockLocOnRt

	
	
	
	
	
	
   /**
	 *  [1후판정정야드] 작업예약등록(이적)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String insMvWBookId(JDTORecord[] inDto, String logId) throws DAOException {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.11.14 insMvWBookId argument 에 logId 항목 추가 개선
//	public String insMvWBookId(JDTORecord[] inDto) throws DAOException {
////////////////////////////////////////////////////////////////////////////////////////

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


		//---------------------------------------------------------------------------------------------
		// 2024.11.14 argument에 logId 없으면 새로 발본
		//---------------------------------------------------------------------------------------------
		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P"); // log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
		
		
		int		intRtnVal			= 0;

		try {

			szLogMsg = "JSP-SESSION [" + szOperationName + "] 시작 ";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

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
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

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
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
						return szRtnMsg;
					}
				}
			}

			
			for (int ii=0; ii<inDto.length; ii++) {
				szYdMainWrkGp = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_MAIN_WRK_GP");		// 1:이적, 2:북인, 3:보수장이적[저장위치수정]

				recPara = JDTORecordFactory.getInstance().create();
				// 해당 스케줄 기준정보에 야드 스케줄 금지유무를 "Y" Setting 한다.
				recPara.setField("YD_GP", 	          	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_GP")				);
				recPara.setField("YD_BAY_GP", 	      	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_BAY_GP")			);
				recPara.setField("YD_SPAN_GP", 	      	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_SPAN_GP")			);
				recPara.setField("YD_AIM_RT_GP", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_RT_GP")		);
			//  주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차, M:보수장이적
				recPara.setField("YD_MAIN_WRK_GP", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_MAIN_WRK_GP")		);
//				recPara.setField("YD_BS_MV_GP",			ydDaoUtils.paraRecChkNull(inDto[ii], "YD_BS_MV_GP"));			// BS:보수장, 1:#1보수대기, 2:#2보수대기, 3:충당대기
				recPara.setField("YD_TO_LOC_GUIDE_GP",	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_TO_LOC_GUIDE_GP")	);
				recPara.setField("YD_AIM_YD_GP", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_YD_GP")		);
				recPara.setField("YD_AIM_BAY_GP", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_BAY_GP")		);
				recPara.setField("YD_AIM_SPAN_GP", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_SPAN_GP")		);
				recPara.setField("YD_AIM_COL_GP", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_COL_GP")		);
			//	recPara.setField("YD_AIM_BED_NO", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_BED_NO"));

				szYdSpanGp = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_SPAN_GP");
				if ("BC".equals(szYdSpanGp) || "BS".equals(szYdSpanGp) || "CN".equals(szYdSpanGp) || "RT".equals(szYdSpanGp)) {
					recPara.setField("YD_AIM_BED_NO", 	"");
				} else {
					recPara.setField("YD_AIM_BED_NO", 	"01");														// 무조건 01베드로 TO위치 결정되도록 보완
				}

				recPara.setField("YD_STK_COL_GP", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GPS")		);
				recPara.setField("YD_STK_BED_NO", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_NOS")		);
				recPara.setField("YD_TO_LOC_GUIDE", 	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_TO_LOC_GUIDE")		);
				recPara.setField("YD_TC_GP", 	  		ydDaoUtils.paraRecChkNull(inDto[ii], "YD_TC_GP")			);

				//---------------------------------------------------------------------------------------------
				// 후판정정의 준비스케줄 등록 시 지정한 작업매수의 값을 전달하여 해당하는 매수만큼 작업예약에 등록
				//---------------------------------------------------------------------------------------------
				recPara.setField("YD_EQP_WRK_SH", 	  	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_EQP_WRK_SH")		);

				//---------------------------------------------------------------------------------------------
				// 이송대기인 경우 착지개소코드도 파라미터로 전달됨
				//---------------------------------------------------------------------------------------------
				recPara.setField("ARR_WLOC_CD", 	    ydDaoUtils.paraRecChkNull(inDto[ii], "ARR_WLOC_CD")			);

				//---------------------------------------------------------------------------------------------
				// 작업재료리스트와 JMS_TC_CD
				//---------------------------------------------------------------------------------------------
				recPara.setField("STL_LIST", 	        ydDaoUtils.paraRecChkNull(inDto[ii], "STL_LIST")			);
				recPara.setField("JMS_TC_CD", 	        ydDaoUtils.paraRecChkNull(inDto[ii], "JMS_TC_CD")			);

				recPara.setField("YD_USER_ID", 	        ydDaoUtils.paraRecModifier(inDto[ii])						);

				//---------------------------------------------------------------------------------------------
				// 크레인 스케줄 호출 SKIP FLAG (상단지시 존재시 SKIP여부 결정)
				//---------------------------------------------------------------------------------------------
				recPara.setField("CRNSCH_SKIP_FLAG",	szCrnschSkipFlag											);		// 작업예약만 등록하고 크레인 작업지시 생성 SKIP

				//---------------------------------------------------------------------------------------------
				// L3화면을 통해 만들어진 작업예약, 작업지시 설정
				//---------------------------------------------------------------------------------------------
				recPara.setField("CARD_NO",	"L3"																	);		
				
				
				//---------------------------------------------------------------------------------------------
				// 2024.11.14 recPara에 logId 추가 
				//---------------------------------------------------------------------------------------------
				recPara.setField("LOG_ID", logId);                                         

				
				if ("M".equals(szYdMainWrkGp)) {								// 보수장이적
					szRtnMsg = this.procBsLocChg(recPara);						// 보수장 저장위치 변경
				} else {
					szRtnMsg = (String)ejbConn.trx("JPlateYdMvStkWrkSeEJB", "procPrepLotCompByCapaYdP", recPara);
				}

				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					return szRtnMsg;
				}
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION ["+szOperationName+"] 끝  .. 결과:" + szRtnMsg;
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		return szRtnMsg;
	} // end of insMvWBookId

	/**
	 *  [1후판정정야드] 보수장 이적 [저장위치변경]--사용여부 확인
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

			szAimYdGp 	= ydDaoUtils.paraRecChkNull(inRec, "YD_AIM_YD_GP", 		JPlateYdConst.YD_GP_P_PLATE_YARD);
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
				recPara.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);
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
				
				szLogMsg = "[" + szOperationName + "] 현위치szCurrYdStkCol : " + szCurrYdStkCol;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				
				// 현재위치가 보수대기/충당대기인지 체크함
				if ("PF0104".equals(szCurrYdStkCol) || "PF0105".equals(szCurrYdStkCol) ||
				    "PF0106".equals(szCurrYdStkCol) || "PF0107".equals(szCurrYdStkCol) ) {

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
				recPara.setField("YD_SCH_CD", 	   	"PXBS01MM");		// 스케쥴코드 : 보수장내 이적FXBS01MM
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
	 * [1후판정정야드] 저장위치별 정보 조회화면 : 조업L3 저장위치 정보 재전송
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
		            recL3Para.setField("MSG_ID", 			"YDPRJ011");
		            recL3Para.setField("YD_STK_COL_FR", 	"");									// From적치열
		            recL3Para.setField("YD_STK_BED_FR", 	"");									// From적치BED
		            recL3Para.setField("YD_STK_COL_TO", 	szYdStkColGp);							// TO적치열
		            recL3Para.setField("YD_STK_BED_TO", 	szYdStkBedNo);							// TO적치BED
		            recL3Para.setField("YD_EQP_WRK_SH", 	"");									// 야드설비작업매수
		            recL3Para.setField("ARR_STL_NO", 		szArrStlNo);

		        //	szRtnMsg = ydDelegate.sendMsg(recL3Para);
		            szRtnMsg = JPlateYdCommonUtils.sendL3YDPRJ011(recL3Para);

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
	            recL3Para.setField("MSG_ID", 			"YDPRJ011");
	            recL3Para.setField("YD_STK_COL_FR", 	"");									// From적치열
	            recL3Para.setField("YD_STK_BED_FR", 	"");									// From적치BED
	            recL3Para.setField("YD_STK_COL_TO", 	szYdStkColGp);							// TO적치열
	            recL3Para.setField("YD_STK_BED_TO", 	szYdStkBedNo);							// TO적치BED
	            recL3Para.setField("YD_EQP_WRK_SH", 	"");									// 야드설비작업매수
	            recL3Para.setField("ARR_STL_NO", 		szArrStlNo);

	        //	szRtnMsg = ydDelegate.sendMsg(recL3Para);
	            szRtnMsg = JPlateYdCommonUtils.sendL3YDPRJ011(recL3Para);

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
	 * [1후판정정야드] 야드긴급재 등록/취소
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
			szYdGp		= ydDaoUtils.paraRecChkNull(inRec, "YD_GP", JPlateYdConst.YD_GP_P_PLATE_YARD);	// 야드구분
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
			// 3. 야드L2 전문송신 (저장품제원정보 전송) YDY2L002
			//------------------------------------------------------------
			for(int ii=0; ii<arrStlNo.length; ii++) {
				szStlNo = arrStlNo[ii];

				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	recL2Para = JDTORecordFactory.getInstance().create();
	        	recL2Para.setField("JMS_TC_CD", 		"YDY2L002");        // TC-CODE
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
	 *  임가공절단장 모니터링 : 스크랩처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String procBcScrap(JDTORecord[] inDto) throws DAOException {

		JDTORecordSet 	rsResult  	= null;
		JDTORecord 		recPara   	= null;
		JDTORecord 		tempRec		= null;
		JDTORecord 		recL2Para   = null;

		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMethodName		= "procBcScrap";
		String 	szOperationName 	= "임가공절단장 스크랩처리";
		String	szMsg				= "";
		String 	szLogMsg 			= "";
		String	szStlNo				= "";
		String	szModifier			= "";

		String	szYdStkColGp		= "";
		String	szMtlStatCd     	= "";
		String	szYdStkLyrMtlStat	= "";
		String	szYdGp				= "";

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
				recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);		
				
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

				if (!"BC".equals(ydUtils.substr(szYdStkColGp, 2, 2))) {
					szRtnMsg = "재료의 현위치가 임가공절단장이 아닙니다.... 저장위치:" + szYdStkColGp;
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
				szYdGp = ydUtils.substr(szYdStkColGp, 0, 1);
				// ------------------------------------------------------------------------
				// 2.1. 작업예약 존재여부 확인
				// ------------------------------------------------------------------------
				rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", 		szStlNo);             	// 재료번호
				recPara.setField("YD_GP",		szYdGp);
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
				recPara.setField("YD_GP",		szYdGp);

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
				recPara.setField("YD_SCH_CD",		JPlateYdConst.SCH_CD_JPLATE_SCRAP); //FXCN01MM
				recPara.setField("YD_SCH_CD",	    "PXBC01MM"); //FXCN01MM
				
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
				recPara.setField("YD_GP",				szYdGp);
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
			// 6. 야드L2 저장품제원 정보 송신 [YDY2L002]
			// ------------------------------------------------------------------------
			for (int ii=0; ii<inDto.length; ii++) {

				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				szStlNo = ydDaoUtils.paraRecChkNull(inDto[ii], "STL_NO");

	        	recL2Para = JDTORecordFactory.getInstance().create();
	        	recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
	        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
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
	} // end of procBcScrap

/**************************************************************************************************************************************************	
    2후판 기준으로 변경처리 해야 하는 로직
**************************************************************************************************************************************************/	


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
							recPara.setField("YD_GP",			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 2후판정정야드
							recPara.setField("STL_NO", 			szStlNo);             					// 재료번호
							recPara.setField("YD_UP_WR_LOC", 	szYdUpWrLoc);             				// 권상실적위치
							recPara.setField("YD_UP_WR_LAYER", 	szYdUpWrLayer);             			// 권상실적단
							recPara.setField("YD_DN_WR_LOC", 	"");             						// 권하실적위치
							recPara.setField("YD_DN_WR_LAYER", 	"");             						// 권하실적단
							recPara.setField("YD_SCH_ST_GP", 	"B");									// 야드 스케줄기동 구분 (A:AUTO, M:MANUAL, B:BACK-UP)
							recPara.setField("YD_SCH_CD",		JPlateYdConst.SCH_CD_JPLATE_LOC_INIT);	// 대차 초기화
							if(szYdGp.equals("P")) {
								recPara.setField("YD_SCH_CD",	"PXYD04MM");	// 대차 초기화
							}
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

				//---------------------------------------------------------------------------------------------
				//	L3화면을 통해 만들어진 작업예약, 작업지시 설정
				//---------------------------------------------------------------------------------------------
				recPara.setField("CARD_NO",	"L3");		
				
				//내부 Process 연결
				EJBConnector ejbConn = new EJBConnector("default", this);
				szRtnMsg = (String)ejbConn.trx("JPlateYdMvStkWrkSeEJB", "procPrepLotCompByCapaYdP", recPara);

			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION ["+szOperationName+"] 끝  .. 결과 :: " + szRtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of insBsInWBook
	
	/**
	 *  1후판정정 작업예약등록(보수장추출)
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
		String 	szOperationName 	= "1후판정정  작업예약등록(보수장추출)";
		String 	szLogMsg 			= "";
		String	szArrStlNo			= "";
		String	szModifier			= "";
		int		intRtnVal			= 0;
		
		String  sBOOK_OUT_SPAN		= ""; //화면에서 입력한 TO위치 가이드 Span
		String  sBOOK_OUT_COL		= ""; //화면에서 입력한 TO위치 가이드 열 
		
		String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";

		try {

			String szYdGp  = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			szArrStlNo  = ydDaoUtils.paraRecChkNull(inDto, "ARR_STL_NO");
			szModifier 	= ydDaoUtils.paraRecModifier(inDto);
			
			sBOOK_OUT_SPAN = ydDaoUtils.paraRecChkNull(inDto, "BOOK_OUT_SPAN");
			sBOOK_OUT_COL  = ydDaoUtils.paraRecChkNull(inDto, "BOOK_OUT_COL");

			String[] arrStlNo = szArrStlNo.split(";");

			szLogMsg = "JSP-SESSION ["+szOperationName+"] 시작 :: " + szArrStlNo;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			
			//------------------------------------------------------------------
			//재료별로 작업예약이 존재하는지 체크한다!
			JDTORecordSet       rsResult    = null;
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			for (int ii=0; ii<arrStlNo.length; ii++) {
				jrParam.setField("YD_GP", szYdGp);
				jrParam.setField("STL_NO", arrStlNo[ii]);
				
				rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getExistByStlNo", logId, szMethodName, "재료번호로 작업예약 정보 조회 [존재여부 체크]");
				
				if(rsResult.size() > 0) {
					szRtnMsg = "해당 재료[" + arrStlNo[ii] + "]에 작업예약이 존재! " + rsResult.getRecord(0).getFieldString("YD_WBOOK_ID") + " 작업예약을 삭제 하십시요!";
					szLogMsg = "[" + szOperationName + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
			}
			//------------------------------------------------------------------

			// 후판조업 차행선결정정보 수신 (PPYDJ015)
			// 보수완료실적(검사실적) 등록 및 추출스케줄 기동
			EJBConnector ejbConn = new EJBConnector("default", this);

			for (int ii=0; ii<arrStlNo.length; ii++) {

				rsYdStock 	= JDTORecordFactory.getInstance().createRecordSet("ydTemp");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO",		arrStlNo[ii]);
				recPara.setField("YD_GP",		szYdGp);

				intRtnVal 	= ydStockDao.getYdStockWithLocYdP(recPara, rsYdStock);

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
					recPara.setField("SCPL_WRK_MTL",		ydDaoUtils.paraRecChkNull(recMtl, "SCPL_WRK_MTL"));			// 강력교정재**
					recPara.setField("HTTRT_HPL_MTL",		ydDaoUtils.paraRecChkNull(recMtl, "HTTRT_HPL_MTL"));		// 열처리교정재
					recPara.setField("GAS_WRK_MTL",			ydDaoUtils.paraRecChkNull(recMtl, "GAS_WRK_MTL"));			// GAS작업재
					recPara.setField("SHOT_BLST_WRK_MTL",	ydDaoUtils.paraRecChkNull(recMtl, "SHOT_BLST_WRK_MTL"));	// ShortBlast작업재
					recPara.setField("PRESS_WRK_MTL",		ydDaoUtils.paraRecChkNull(recMtl, "PRESS_WRK_MTL"));		// 프레스교정재
					recPara.setField("PL_WR_PRSNT_PROC_CD",	ydDaoUtils.paraRecChkNull(recMtl, "PL_WR_PRSNT_PROC_CD"));	// 후판실적현공정코드
					recPara.setField("GDS_MAIN_GRD",		ydDaoUtils.paraRecChkNull(recMtl, "GDS_MAIN_GRD"));			// 제품주등급
					recPara.setField("MODIFIER", 			szModifier);
					
					recPara.setField("BOOK_OUT_SPAN", sBOOK_OUT_SPAN);
					recPara.setField("BOOK_OUT_COL", sBOOK_OUT_COL);

	                //---------------------------------------------------------------------------------------------
					//	L3화면을 통해 만들어진 작업예약, 작업지시 설정
					//---------------------------------------------------------------------------------------------
					recPara.setField("CARD_NO",	"L3");		
					
					szRtnMsg = (String)ejbConn.trx("JPlateYdL3RcvSeEJB", "procPRNextDeciInfo", recPara);

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
	 *  작업예약등록(임가공절단장 보급)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String insBcInWBook(JDTORecord[] inDto) throws DAOException {

		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();

		String szRtnMsg 		= JPlateYdConst.RETN_CD_SUCCESS;
		String szMethodName		= "insBcInWBook";
		String szOperationName 	= "작업예약등록(임가공절단장 보급)";
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
			//  주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차, E:임가공보급 ,F: 임가공추출
				recPara.setField("YD_MAIN_WRK_GP", 	  	ydDaoUtils.setDataDefault(inDto[ii].getField("YD_MAIN_WRK_GP"),""));
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

				//---------------------------------------------------------------------------------------------
				//	L3화면을 통해 만들어진 작업예약, 작업지시 설정
				//---------------------------------------------------------------------------------------------
				recPara.setField("CARD_NO",	"L3");		
				
				//내부 Process 연결
				EJBConnector ejbConn = new EJBConnector("default", this);
				szRtnMsg = (String)ejbConn.trx("JPlateYdMvStkWrkSeEJB", "procPrepLotCompByCapaYdP", recPara);

			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION ["+szOperationName+"] 끝  .. 결과 :: " + szRtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of insBcInWBook
	/**
	 *  작업예약등록(임가공절단장 추출)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String insBcOutWBook(JDTORecord inDto) throws DAOException {

		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();

		String 	szRtnMsg 		= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMethodName	= "insBcOutWBook";
		String 	szOperationName = "작업예약등록(임가공절단장 추출)";
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

			// 임가공절단장 절단 재료 등록 및 추출스케줄 기동
			EJBConnector ejbConn = new EJBConnector("default", this);
			szRtnMsg = (String)ejbConn.trx("JPlateYdL3RcvSeEJB", "procPRRentCutResult", recPara);

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
	} // end of insBcOutWBook
	
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

				//---------------------------------------------------------------------------------------------
				//	L3화면을 통해 만들어진 작업예약, 작업지시 설정
				//---------------------------------------------------------------------------------------------
				recPara.setField("CARD_NO",	"L3");		
				
				//내부 Process 연결
				EJBConnector ejbConn = new EJBConnector("default", this);
				szRtnMsg = (String)ejbConn.trx("JPlateYdMvStkWrkSeEJB", "procPrepLotCompByCapaYdP", recPara);

			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION ["+szOperationName+"] 끝  .. 결과 :: " + szRtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of insCncInWBook
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
				recPara.setField("YD_GP", 		JPlateYdConst.YD_GP_P_PLATE_YARD);             	// 야드구분

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
				recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);

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
				recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);

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
				recPara.setField("YD_GP",				JPlateYdConst.YD_GP_P_PLATE_YARD);
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
	        	recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
	        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
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
	} // end of procCncScrap 

	/**
	 *  작업예약등록(GAS절단장 추출)
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
		String 	szOperationName = "작업예약등록(GAS절단장 추출)";
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

			recPara.setField("BOOK_OUT_SPAN", 		ydDaoUtils.paraRecChkNull(inDto, 	"BOOK_OUT_SPAN"));
			recPara.setField("BOOK_OUT_COL", 		ydDaoUtils.paraRecChkNull(inDto, 	"BOOK_OUT_COL"));
			
			for (int ii=0; ii<arrStlNo.length; ii++) {
				recPara.setField("STL_NO"+(ii+1),		arrStlNo[ii]);
			}

            //---------------------------------------------------------------------------------------------
			//	L3화면을 통해 만들어진 작업예약, 작업지시 설정
			//---------------------------------------------------------------------------------------------
			recPara.setField("CARD_NO",	"L3");		
			
			// 임가공절단장(GAS절단장) 절단 재료 등록 및 추출스케줄 기동
			EJBConnector ejbConn = new EJBConnector("default", this);
			szRtnMsg = (String)ejbConn.trx("JPlateYdL3RcvSeEJB", "procPRRentCutResult", recPara);

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
	 * GridData - 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getSelectData(GridData gdReq) throws DAOException {
		String methodNm = "조회[JPlateYdYdPJspSeEJB.getSelectData] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Grid date 를 JDTORecord data 로 변환
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			commDao.jspSelect(inRecord, outRecSet, inRecord.getFieldString("QUERY_ID"), logId, methodNm);	
			
			
			//UI로 반환 할 Grid data 를 생성 
			//GridData gdRet = CmUtil.genGridData(gdReq, outRecSet); -- old version
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			GridData gdRet = commUtils.jdtoRecordToGridData(gdRtn, outRecSet.toList(), gdReq);
			
			commUtils.printLog(logId, methodNm, "S-", gdReq);
			
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of getSelectData		
	
	/**
	 *      [A] 오퍼레이션명 : Grid에서 값 추출하기
	 *
	 *      @param JDTORecord jrAdd
	 *      @return JDTORecord
	*/
	public String getValue(GridData gdReq, String headerNm, int ii) {
		try {
			String rtnValue;
			if (gdReq.getHeader(headerNm).getDataType().equals(OperateGridData.t_combo)) {
				rtnValue = commUtils.nvl(commUtils.trim(gdReq.getHeader(headerNm).getComboHiddenValues()[gdReq.getHeader(headerNm).getSelectedIndex(ii)]),"");
			} else if (gdReq.getHeader(headerNm).getDataType().equals(OperateGridData.t_number)) {	
				rtnValue = commUtils.nvl(commUtils.trim(gdReq.getHeader(headerNm).getValue(ii)),"0");
			} else {
				rtnValue = commUtils.nvl(commUtils.trim(gdReq.getHeader(headerNm).getValue(ii)),"");
			}
			return rtnValue; 
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 광폭 지정 가능 여부 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updWdwAbleYn(GridData gdReq) throws DAOException {
		String methodNm = "광폭 지정 가능 여부 변경[JPlateYDYDPJspSeEJB.updWdwAbleYn] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {

				jrParam.setField("MODIFIER"			, gdReq.getParam("userid"));
				jrParam.setField("YD_STK_COL_GP"	, this.getValue(gdReq, "YD_STK_COL_GP", ii) );
				jrParam.setField("YD_STKBED_USG_CD"	, this.getValue(gdReq, "YD_STKBED_USG_CD", ii) );
				commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updWdwAbleYn", logId, methodNm, "광폭 지정 가능 여부 변경");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updWdwAbleYn

	/**
	 * 광폭 지정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updWdwSet(GridData gdReq) throws DAOException {
		String methodNm = "광폭 지정[JPlateYDYDPJspSeEJB.updWdwSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {

				//01.광폭지정열 : 폭 구분을 광폭(L)으로 황성상태를 사용가능(L)으로 변경
				jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
				jrParam.setField("YD_STK_COL_GP"		, this.getValue(gdReq, "YD_STK_COL_GP", ii) );
				jrParam.setField("YD_STK_COL_W_GP"		, "L" );
				jrParam.setField("YD_STK_COL_ACT_STAT"	, "L" );
				commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updWdwSet", logId, methodNm, "광폭 지정 열 UPDATE ");
				
				
				//02광폭지정열 다음열 : 폭구분을 광폭(L)으로 활성상태를 비활성(C)으로 변경
				jrParam.setField("YD_STK_COL_GP"		, this.getValue(gdReq, "YD_STK_COL_GP_NEXT", ii) );
				jrParam.setField("YD_STK_COL_W_GP"		, "L" );
				jrParam.setField("YD_STK_COL_ACT_STAT"	, "C" );
				commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updWdwSet", logId, methodNm, "광폭 지정 다음 열 UPDATE ");
				
				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updWdwSet

	/**
	 * 광폭 해제 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updWdwRel(GridData gdReq) throws DAOException {
		String methodNm = "광폭 해제[JPlateYDYDPJspSeEJB.updWdwRel] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {

				//01.광폭해제열 : 폭 구분을 소폭(S)으로 황성상태를 사용가능(L)으로 변경
				jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
				jrParam.setField("YD_STK_COL_GP"		, this.getValue(gdReq, "YD_STK_COL_GP", ii) );
				jrParam.setField("YD_STK_COL_W_GP"		, "S" );
				jrParam.setField("YD_STK_COL_ACT_STAT"	, "L" );
				commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updWdwSet", logId, methodNm, "광폭 해제 열 UPDATE ");
				
				
				//02광폭해제열 다음열 : 폭구분을 소폭(S)으로 활성상태를 사용가능(L)으로 변경
				jrParam.setField("YD_STK_COL_GP"		, this.getValue(gdReq, "YD_STK_COL_GP_NEXT", ii) );
				jrParam.setField("YD_STK_COL_W_GP"		, "S" );
				jrParam.setField("YD_STK_COL_ACT_STAT"	, "L" );
				commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updWdwSet", logId, methodNm, "광폭 해제 다음 열 UPDATE ");
				
				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updWdwRel
	
	/**
	 * [1후판정정야드] 저장위치별 정보 조회화면 : 조업L3 저장위치 정보 재전송2
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String sndYdLocInfoList2(JDTORecord [] inDto) throws DAOException {

		String	szRtnMsg		= "";
		String 	szMsg        	= "";
		String 	szMethodName	= "sndYdLocInfoList2";
		String 	szOperationName	= "조업L3 저장위치 정보 재전송2";

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

			/* BED 정보가 틀리게 전송되어 주석처리
			for (int ii=0; ii<inDto.length; ii++) {

				szStlNo		 = ydDaoUtils.paraRecChkNull(inDto[ii], "STL_NO");
				szYdStkColGp = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GP");
				szYdStkBedNo = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_NO");
				
	            szMsg    = "["+szOperationName+"] STL_NO : " + szStlNo;
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	            szMsg    = "["+szOperationName+"] YD_STK_COL_GP : " + szYdStkColGp;
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	            szMsg    = "["+szOperationName+"] szOldYdStkColGp : " + szOldYdStkColGp;
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	            szMsg    = "["+szOperationName+"] szArrStlNo : " + szArrStlNo;
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				
				if ("".equals(szOldYdStkColGp)) {
					szOldYdStkColGp = szYdStkColGp;
				}

				if (!szYdStkColGp.equals(szOldYdStkColGp)) {
		            szMsg    = "["+szOperationName+"] 후판조업 저장위치변경정보 전송 ---- START";
		            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		            recL3Para = JDTORecordFactory.getInstance().create();
		            recL3Para.setField("MSG_ID", 			"YDPRJ011");
		            recL3Para.setField("YD_STK_COL_FR", 	"");									// From적치열
		            recL3Para.setField("YD_STK_BED_FR", 	"");									// From적치BED
		            recL3Para.setField("YD_STK_COL_TO", 	szOldYdStkColGp);						// TO적치열 **  szYdStkColGp --> szOldYdStkColGp 로 변경
		            recL3Para.setField("YD_STK_BED_TO", 	szYdStkBedNo);							// TO적치BED
		            recL3Para.setField("YD_EQP_WRK_SH", 	"");									// 야드설비작업매수
		            recL3Para.setField("ARR_STL_NO", 		szArrStlNo);

		        //	szRtnMsg = ydDelegate.sendMsg(recL3Para);
		            szRtnMsg = JPlateYdCommonUtils.sendL3YDPRJ011(recL3Para);

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
	            recL3Para.setField("MSG_ID", 			"YDPRJ011");
	            recL3Para.setField("YD_STK_COL_FR", 	"");									// From적치열
	            recL3Para.setField("YD_STK_BED_FR", 	"");									// From적치BED
	            recL3Para.setField("YD_STK_COL_TO", 	szYdStkColGp);							// TO적치열
	            recL3Para.setField("YD_STK_BED_TO", 	szYdStkBedNo);							// TO적치BED
	            recL3Para.setField("YD_EQP_WRK_SH", 	"");									// 야드설비작업매수
	            recL3Para.setField("ARR_STL_NO", 		szArrStlNo);

	        //	szRtnMsg = ydDelegate.sendMsg(recL3Para);
	            szRtnMsg = JPlateYdCommonUtils.sendL3YDPRJ011(recL3Para);

				szMsg = "["+szOperationName+"] 후판조업 저장위치변경정보 전송 완료 LAST >>>>" + szRtnMsg;
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}
			*/
			
            szMsg    = "["+szOperationName+"] 조업L3 저장위치 정보 재전송2 ---- START";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			
            recL3Para = JDTORecordFactory.getInstance().create();
			
			for (int ii=0; ii<inDto.length; ii++) {
				
				szStlNo		 = ydDaoUtils.paraRecChkNull(inDto[ii], "STL_NO");
				szYdStkColGp = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GP");
				szYdStkBedNo = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_NO");
				
				
	            recL3Para.setField("MSG_ID", 			"YDPRJ011");
	            recL3Para.setField("YD_STK_COL_FR", 	"");									// From적치열
	            recL3Para.setField("YD_STK_BED_FR", 	"");									// From적치BED
	            recL3Para.setField("YD_STK_COL_TO", 	szYdStkColGp);							// TO적치열
	            recL3Para.setField("YD_STK_BED_TO", 	szYdStkBedNo);							// TO적치BED
	            recL3Para.setField("YD_EQP_WRK_SH", 	"");									// 야드설비작업매수
	            recL3Para.setField("ARR_STL_NO", 		szStlNo);
	            
	            szRtnMsg = JPlateYdCommonUtils.sendL3YDPRJ011(recL3Para);
	            
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
	 * [1후판정정야드] 저장위치수정2 (BOOK-OUT 실적 BACKUP)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String updYdLocInfo2(JDTORecord inDto) throws DAOException {

		JPlateYdStockDAO 	ydStockDao		= new JPlateYdStockDAO();
		JPlateYdStkBedDAO	ydStkBedDao		= new JPlateYdStkBedDAO();
		JPlateYdStkLyrDAO	ydStkLyrDao		= new JPlateYdStkLyrDAO();

		JPlateYdDelegate 	ydDelegate 		= new JPlateYdDelegate();

		String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
		
		String	szRtnMsg		= "";
		String 	szMsg        	= "";
		String 	szMethodName	= "updYdLocInfo2";
		String 	szOperationName	= "저장위치수정 (BOOK-OUT 실적 BACKUP)";

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
			szYdGp			= ydDaoUtils.paraRecChkNull(inRec, "YD_GP", JPlateYdConst.YD_GP_P_PLATE_YARD);			// 야드구분

			inRec.setField("MODIFIER",		szModifier);			// 수정자
			inRec.setField("YD_SCH_ST_GP",	szYdSchStGp);			// 야드스케줄 기동구분 (A:AUTO, M:MANUAL, B:BACK-UP)

			//------------------------------------------------------------------
			// 2013.07.30 현재 저장위치가 2후판 정정야드가 아닐경우 오류로 처리
			//------------------------------------------------------------------
			szRtnMsg = JPlateYdCommonUtils.checkUpdYdLocYdP(szStlNo, szYdGp, "N");	
			
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
			recPara.setField("YD_GP",		szYdGp);			// 야드구분
			//intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsResult);
            rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdStockWithLoc2", logId, szMethodName, "1후판정정야드재료 정보 조회");

			// 재료 정보 존재여부 체크
			if (rsResult.size() < 1) {
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
					szMsg = "[ " +szOperationName + "] 재료번호("+ szStlNo  +")등록 ERROR .. " + Integer.toString(intRtnVal); //2021.12.01 박종호 재료번호 추가
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

						//String sAPPPI5 =ydPICommDAO.ApplyYnPI("",szOperationName,"APPPI5","T","*"); 
						// 3.2. 후판정정야드 저장위치 수정 처리
						//if(sAPPPI5.equals("Y")){
						//	szMsg = this.updJPlateYdStkPosFix2TX(recBed, "N");				// 단 변경시에는 후판조업에 송신 안함
						//}
						//else{
							szMsg = this.updJPlateYdStkPosFix2(recBed, "N");				// 단 변경시에는 후판조업에 송신 안함
						//}
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
			//String sAPPPI5 =ydPICommDAO.ApplyYnPI("",szOperationName,"APPPI5","T","*");
			// 3.2. 후판정정야드 저장위치 수정 처리
			//if(sAPPPI5.equals("Y")){
			//	szMsg = this.updJPlateYdStkPosFix2TX(inRec, szSendFlag);
			//}
			//else{
				szMsg = this.updJPlateYdStkPosFix2(inRec, szSendFlag);	
			//}
			
			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szMsg)) {
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szMsg;
			}
			
			//------------------------------------------------------------
			// 5. 야드L2 전문송신 (저장위치변경이력 일괄 전송)
			// --> 저장위치제원은 안바뀌고 저장품제원이 변경됨으로 YDY2L002 변경
			// 후판조업 저장위치변경정보 전송 (YDPRJ011),
			// 1 후판전단L2 RT BOOK-IN 실적 전송 (YDP2L501)
			// 1 후판열처리L2 RT BOOK-IN 실적 전송 (YDP3L501)
			// --> updJPlateYdStkPosFix 메서드 에서 실시 함으로 SKIP
			//------------------------------------------------------------
			if ("Y".equals(szSendFlag)) {
				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	recL2Para = JDTORecordFactory.getInstance().create();
	        	recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
	        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
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
	 * [1후판정정야드] 저장위치삭제2 (BOOK-IN 실적 BACKUP)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String delYdLocInfo2(JDTORecord inDto) throws DAOException {

		JPlateYdStockDAO 	ydStockDao		= new JPlateYdStockDAO();

		JPlateYdDelegate 	ydDelegate 		= new JPlateYdDelegate();
		
		String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";

		String	szRtnMsg		= "";
		String 	szMsg        	= "";
		String 	szMethodName	= "delYdLocInfo2";
		String 	szOperationName	= "저장위치삭제2  (BOOK-IN 실적 BACKUP)";

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
			szRtnMsg = JPlateYdCommonUtils.checkUpdYdLoc(szStlNo, JPlateYdConst.YD_GP_P_PLATE_YARD, "Y");
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
			recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);		
			
			//intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsResult);
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdStockWithLoc2", logId, szMethodName, "1후판정정야드재료 정보 조회");
			
			if (rsResult.size() <= 0) {
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

				szMsg = this.updJPlateYdStkPosFix2(inRec, szSendFlag);
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
			// --> 저장위치제원은 안바뀌고 저장품제원이 변경됨으로 YDY2L002 변경
			// 후판조업 저장위치변경정보 전송 (YDPRJ011),
			// 1 후판전단L2 RT BOOK-IN 실적 전송 (YDP2L501)
			// 1 후판열처리L2 RT BOOK-IN 실적 전송 (YDP3L501)
			// --> updJPlateYdStkPosFix 메서드 에서 실시 함으로 SKIP
			//------------------------------------------------------------
			if ("Y".equals(szSendFlag)) {
				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	recL2Para = JDTORecordFactory.getInstance().create();
	        	recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE : 저장품제원정보
	        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
	        	recL2Para.setField("YD_STK_COL_GP", 	szYdStkColGp);                          // 야드적치열구분
	        	recL2Para.setField("YD_STK_BED_NO", 	szYdStkBedNo);    						// 야드적치BED번호
	        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
	        	recL2Para.setField("STL_NO", 			szStlNo);	        					// 재료번호
	        	recL2Para.setField("MSG_GP", 			"D");	        						// 전문구분
	        	recL2Para.setField("DEL_YN_CHECK",		"N");									// 삭제된 데이타도 조회하도록 처리

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
	}// end of delYdLocInfo2
	
	/**
	 * [1후판정정야드] 저장위치삭제3 (BOOK-IN 실적 BACKUP), 클리어 트랜잭션 분리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String delYdLocInfo3(JDTORecord inDto) throws DAOException {

		JPlateYdStockDAO 	ydStockDao		= new JPlateYdStockDAO();

		JPlateYdDelegate 	ydDelegate 		= new JPlateYdDelegate();
		
		String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";

		String	szRtnMsg		= "";
		String 	szMsg        	= "";
		String 	szMethodName	= "delYdLocInfo3";
		String 	szOperationName	= "저장위치삭제3 (BOOK-IN 실적 BACKUP) 트랜잭션 분리";

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
			szRtnMsg = JPlateYdCommonUtils.checkUpdYdLoc(szStlNo, JPlateYdConst.YD_GP_P_PLATE_YARD, "Y");
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
			recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);		
			
			//intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsResult);
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdStockWithLoc2", logId, szMethodName, "1후판정정야드재료 정보 조회");
			
			if (rsResult.size() <= 0) {
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

				szMsg = this.updJPlateYdStkPosFix3(inRec, szSendFlag);				
				
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
			// --> 저장위치제원은 안바뀌고 저장품제원이 변경됨으로 YDY2L002 변경
			// 후판조업 저장위치변경정보 전송 (YDPRJ011),
			// 1 후판전단L2 RT BOOK-IN 실적 전송 (YDP2L501)
			// 1 후판열처리L2 RT BOOK-IN 실적 전송 (YDP3L501)
			// --> updJPlateYdStkPosFix 메서드 에서 실시 함으로 SKIP
			//------------------------------------------------------------
			if ("Y".equals(szSendFlag)) {
				szMsg = "[ " +szOperationName + "] 야드L2 전문송신 START";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	recL2Para = JDTORecordFactory.getInstance().create();
	        	recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE : 저장품제원정보
	        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
	        	recL2Para.setField("YD_STK_COL_GP", 	szYdStkColGp);                          // 야드적치열구분
	        	recL2Para.setField("YD_STK_BED_NO", 	szYdStkBedNo);    						// 야드적치BED번호
	        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
	        	recL2Para.setField("STL_NO", 			szStlNo);	        					// 재료번호
	        	recL2Para.setField("MSG_GP", 			"D");	        						// 전문구분
	        	recL2Para.setField("DEL_YN_CHECK",		"N");									// 삭제된 데이타도 조회하도록 처리

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
	}// end of delYdLocInfo3	
	
	/**
	 * [1후판정정야드] 후판정정야드 저장위치 수정2
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public String updJPlateYdStkPosFix2(JDTORecord inDto, String pSendFlag) {

		int 	intRtnVal 			= 0;
		String	szRtnMsg			= "";
		String 	szMsg  				= "";
		String 	szMethodName		= "updJPlateYdStkPosFix2";
		String 	szOperationName		= "저장위치 수정2";
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
		String  szRtLoc				= null;
		String  szBookInOutBackupGp = null;
		
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

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 로그 개선 
        String logId                     	= ydLogUtils.getJDTOLogId(inDto, "P");  			// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 		// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
      		
//-------------------------------------------------------------------------------------------------------------------------

		try {
			szMsg   = "[Jsp Session : " + szOperationName + "] 메소드 시작 >>>> " + inDto.toString();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setRecord(inDto);

			rsTemp  = JDTORecordFactory.getInstance().createRecordSet("JPlateYdTemp");

			szMsg   = "[Jsp Session : " + szOperationName + "] 적치단정보 조회";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsTemp);		// intGp == 0

			// 단정보가 존재 하지않거나  단데이터 생성이 되지않는경우
			if (intRtnVal  < 1) {
				szRtnStr = "적치단 정보가 존재하지 않습니다";
				szMsg    = "JSP-SESSION " + szOperationName + "] " + szRtnStr;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnStr;
			}

			// 1. 적치 단 정보 UPDATE
			szStlNo 	  		= ydDaoUtils.paraRecChkNull(inDto, "STL_NO"					);
			szYdStkColGp  		= ydDaoUtils.paraRecChkNull(inDto, "YD_STK_COL_GP"			);
			szYdStkBedNo  		= ydDaoUtils.paraRecChkNull(inDto, "YD_STK_BED_NO"			);
			szYdStkLyrNo  		= ydDaoUtils.paraRecChkNull(inDto, "YD_STK_LYR_NO"			);
			szYdSchStGp			= ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_ST_GP", "B"		);		// 야드스케줄 기동 구분 (A:AUTO, M:MANUAL, B:BACK-UP)
			szYdSchCd			= ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_CD"				);		// 야드스케줄 코드
			szDelFlag	 		= ydDaoUtils.paraRecChkNull(inDto, "DEL_FLAG", "N"			);		// 재료삭제 구분
			szModifier 			= ydDaoUtils.paraRecModifier(inDto							);
			szYdUpWrLoc			= ydDaoUtils.paraRecChkNull(inDto, "OLD_YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(inDto, "OLD_YD_STK_BED_NO");
			szYdUpWrLayer		= ydDaoUtils.paraRecChkNull(inDto, "OLD_YD_STK_LYR_NO"		);
			szYdStkLyrMtlStat	= ydDaoUtils.paraRecChkNull(inDto, "YD_STK_LYR_MTL_STAT"	);
			if ("N".equals(szDelFlag)) {
				szYdDnWrLoc   	= szYdStkColGp + szYdStkBedNo;
				szYdDnWrLayer 	= szYdStkLyrNo;
			}
			szRtLoc				= ydDaoUtils.paraRecChkNull(inDto, "RT_LOC");
			szBookInOutBackupGp = ydDaoUtils.paraRecChkNull(inDto, "BOOK_INOUT_BACKUP_GP");

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", 			szStlNo			);		// 재료번호
			recPara.setField("YD_STK_COL_GP", 	szYdStkColGp	);		// 적치열구분
			recPara.setField("YD_STK_BED_NO", 	szYdStkBedNo	);		// 적치베드
			recPara.setField("YD_STK_LYR_NO", 	szYdStkLyrNo	);		// 적치단
			recPara.setField("YD_SCH_CD", 		""				);
			recPara.setField("YD_WBOOK_ID", 	""				);
			recPara.setField("MODIFIER", 		szModifier		);

			szMsg = "재료번호 : [" + szStlNo + "] 권상위치 : [" + szYdUpWrLoc + "-" + szYdUpWrLayer + "], 권하위치 : [" + szYdDnWrLoc + "-" + szYdDnWrLayer + "] 적치상태 : [" + szYdStkLyrMtlStat + "] DEL_FLAG : " + szDelFlag + " " + szRtLoc + " " + szBookInOutBackupGp;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

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

							szMsg = "미 변경된 재료 정보(" + szStlNo + ")는 UPDATE 하지 않습니다.";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

							bHistFlag = false;
						}
					}
				}
			} else {
				bHistFlag = true;
				recPara.setField("YD_STK_LYR_MTL_STAT", "E");
			}

			szMsg = "이력정보 생성 유무 Flag :: " + String.valueOf(bHistFlag);
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//---------------------------------------
			// 이적작업  이력정보에 추가
			//---------------------------------------
			// bHistFlag - 이력정보 생성 유무 Flag
			if (bHistFlag) {

				recHist = JDTORecordFactory.getInstance().create();
				recHist.setField("YD_GP", 			szYdGp			);
				recHist.setField("STL_NO", 			szStlNo			);
				recHist.setField("YD_DN_WR_LOC",	szYdDnWrLoc		);
				recHist.setField("YD_DN_WR_LAYER",	szYdDnWrLayer	);
				recHist.setField("YD_SCH_ST_GP", 	szYdSchStGp		);
				recHist.setField("MODIFIER", 		szModifier		);
				recHist.setField("YD_SCH_CD",		szYdSchCd		);		// 야드스케줄 코드
				recHist.setField("YD_UP_WR_LOC",	szYdUpWrLoc		);		// 권상위치 (적치열+베드)
				recHist.setField("YD_UP_WR_LAYER",	szYdUpWrLayer	);		// 권상위치 (적치단)

				// 이력정보 남기기
				szRtnStr = this.insYdWrkHistPosFix(recHist);
				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnStr)) {
					szMsg = "JSP-SESSION " + szOperationName + "] " + szRtnStr;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnStr;
				}
			}
			
			
			//String sAPPPI9 = ydPICommDAO.ApplyYnPI("", szMethodName, "APPPI9", "T", "*");
			
			//if(sAPPPI9.equals("Y")){
				//20240419 데드락 방지위해 처리 순서 맞추기.(SHRSTOCK UDATE부터-> 이후 야드맵 수정) 야드맵 수정전으로 올림.
				// 야드재료 저장위치 정보 UPDATE
				intRtnVal = ydStockDao.updYdStkColInfo(recPara);
				if (intRtnVal <= 0) {
					szRtnStr = "야드재료 저장위치 정보 UPDATE 실패 하였습니다" + Integer.toString(intRtnVal);
					szMsg      = "JSP-SESSION " + szOperationName + "] " + szRtnStr;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnStr;
				}
			//}
			

			// 해당 재료번호가 적치중일 경우 해당 적치 정보 삭제 기능 추가 ==============================================
			JDTORecord 	recDelPara 	= JDTORecordFactory.getInstance().create();
						rsDelInfo	= JDTORecordFactory.getInstance().createRecordSet("rsdelInfo");

			if (!"".equals(szStlNo.trim())) {

				recDelPara.setField("STL_NO"				, szStlNo	);
				recDelPara.setField("YD_STK_LYR_MTL_STAT"	, ""		);
				recDelPara.setField("YD_GP"					, szYdGp	);

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

						szMsg = "기존 재료 위치 정보 : 열[" + szStkColGpFrom + "], 베드[" + szStkBedNoFrom + "], 단[" + szStkLyrNoFrom + "]";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						recDelPara.setField("STL_NO", 				"");
						recDelPara.setField("YD_STK_LYR_MTL_STAT", 	"E");

						intRtnVal = ydStkLyrDao.updYdStklyrStat(recDelPara);		// intGp == 0
						if (intRtnVal <= 0) {
							szRtnStr = "적치단 정보 CLEAR 실패 하였습니다" + Integer.toString(intRtnVal);
							szMsg      = "JSP-SESSION " + szOperationName + "] " + szRtnStr;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
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
					szMsg      = "JSP-SESSION " + szOperationName + "] " + szRtnStr;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnStr;
				}
			} else {
				recPara.setField("YD_SCH_CD", 		"");
				recPara.setField("YD_WBOOK_ID",		"");
				recPara.setField("YD_STK_COL_GP", 	"");
				recPara.setField("YD_STK_BED_NO", 	"");
			}

			//20240419 데드락 방지위해 처리 순서 맞추기.(SHRSTOCK UDATE부터-> 이후 야드맵 수정)  이거 야드맵 수정전으로 올리기->위로올림.
			// 야드재료 저장위치 정보 UPDATE
			
			//if(!sAPPPI9.equals("Y")){  //배포이전 순서
			//	intRtnVal = ydStockDao.updYdStkColInfo(recPara);
			//	if (intRtnVal <= 0) {
			//	szRtnStr = "야드재료 저장위치 정보 UPDATE 실패 하였습니다" + Integer.toString(intRtnVal);
			//	szMsg      = "JSP-SESSION " + szOperationName + "] " + szRtnStr;
			//	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			//	return szRtnStr;
			//	}
			//}
			szMsg = "JSP-SESSION [저장위치수정] 끝";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			// 2013.08.27 : 저장위치 수정후 적치재료의 길이의 합이 베드(열)의 길이를 초과 하는지 체크
			int iRemainMtlL = ydStkLyrDao.getRemainMtlL(recPara);
			if (iRemainMtlL < 0) {
				szRtnStr 	= "재료길이 합이 적치열길이 초과!! " + Integer.toString(iRemainMtlL*(-1));
				szMsg		= "JSP-SESSION " + szOperationName + "] " + szRtnStr;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnStr;
			}

			if ("Y".equals(pSendFlag)) {
				//---------------------------------------------------
				// FROM 과 TO 저장위치를 비교하여 변경이 없을경우는 송신하지 않음
				//---------------------------------------------------
				if (szYdUpWrLoc.equals(szYdDnWrLoc)) {

					szMsg = "[ " +szOperationName + "] 단 정보만 바뀌었을때 .. 조업L3 전문송신 SKIP";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				} else {

					String sNEW_MODULE_EFF_YN = "N";
					
					JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
					
					sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A032"); //1후판정정야드 PRYDJ018 수신시 YDPRJ011 송신 안함(N=송신함)
					
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 PRYDJ018 수신시 YDPRJ011 송신 안함(N=송신함)  : " + sNEW_MODULE_EFF_YN + " ]]]---", JPlateYdConst.DEBUG, logId);
					
					if(sNEW_MODULE_EFF_YN.equals("Y") && "PRYDJ018".equals(szModifier)) {

						//PRYDJ018 수신으로 보수장에 저장위치 생성된 재료는 YDPRJ011 을 송신 하지 않는다.
						szMsg = "[ " +szOperationName + "] 조업L3 전문송신 안함 <-PRYDJ018 수신으로 보수장에 저장위치 생성된 재료는 YDPRJ011 을 송신 하지 않는다!";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					} else if("TY".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {

						//TY 임시BED 로 권하위치 수정된 경우는 YDPRJ011 을 송신 하지 않는다.
						szMsg = "[ " +szOperationName + "] 조업L3 전문송신 안함 <-TY 임시BED 로 권하위치 수정된 경우는 YDPRJ011 을 송신 하지 않는다!";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						
					} else {
					
						//---------------------------------------------------
						// 조업L3 전문송신 (후판조업 저장위치변경정보 전송  - YDPRJ011)
						//---------------------------------------------------
						szMsg = "[ " +szOperationName + "] 조업L3 전문송신 START";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	
			        	recL3Para = JDTORecordFactory.getInstance().create();
	
			        	recL3Para.setField("MSG_ID", 			"YDPRJ011");
			        	recL3Para.setField("YD_STK_COL_FR", 	ydUtils.substr(szYdUpWrLoc, 0, 6));			// From적치열
			        	recL3Para.setField("YD_STK_BED_FR", 	ydUtils.substr(szYdUpWrLoc, 6, 2));			// From적치BED
			        	recL3Para.setField("YD_STK_COL_TO", 	ydUtils.substr(szYdDnWrLoc, 0, 6));			// TO적치열
				        recL3Para.setField("YD_STK_BED_TO", 	ydUtils.substr(szYdDnWrLoc, 6, 2));			// TO적치BED
				        recL3Para.setField("YD_EQP_WRK_SH", 	"1");										// 야드설비작업매수
			            recL3Para.setField("ARR_STL_NO", 		szStlNo);
	
			        	szRtnMsg = ydDelegate.sendMsg(recL3Para);
	
						szMsg = "[ " +szOperationName + "] 조업L3 전문송신 END >>>> " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						
					}

					if ("RT".equals(ydUtils.substr(szYdUpWrLoc, 2, 2)) && "RT".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {

						szMsg = "[" + szOperationName  + "] FROM, TO위치가 RT일때 BOOK-IN/OUT 실적 전송 .. SKIP";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					} else {
						//------------------------------------------------------------
						// 1 후판전단L2 RT BOOK-IN 실적 전송 (YDP2L501)
						// 1 후판열처리L2 RT BOOK-IN 실적 전송 (YDP3L501)
						//------------------------------------------------------------
						if ("RT".equals(ydUtils.substr(szYdUpWrLoc, 2, 2))) {

							szMsg = "[" + szOperationName  + "] RT BOOK-OUT 실적 전송 .. 시작";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

							recL2Para = JDTORecordFactory.getInstance().create();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 #2 열처리 YDP8L501(Bookin/out complete) 송신 추가 
//			  야드스케쥴코드(YD_SCH_CD) BOOK-OUT - PART31LM, PART32LM, PART34LM, PART35LM
//-------------------------------------------------------------------------------------------------------------------------
				        	szMsg = "[" + szOperationName + "] 야드스케쥴코드 [" + szYdSchCd + "]";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			    	        if (  ydUtils.is2ndHeatBookOutSchdule(szYdSchCd, szYdUpWrLoc) ) {
			    	        	
								recL2Para.setField("MSG_ID", 		   "YDP8L501"							);
								recL2Para.setField("STL_NO",			szStlNo								);		// 재료번호
								recL2Para.setField("OPERATION_TYPE",	"2"									);		// 1:Book In, 2:Book Out
								recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szYdUpWrLoc, 0, 6)	);		// FROM위치
								recL2Para.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdUpWrLoc, 6, 2)	);    	// 야드적치BED번호
								recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "CR" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "1");
								
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recL2Para에 logId 추가 
								recL2Para.setField("LOG_ID", 			logId 									);      // logId
//-------------------------------------------------------------------------------------------------------------------------
    
								szRtnMsg = ydDelegate.sendMsg(recL2Para);

								szMsg = "[" + szOperationName  + "] RT BOOK-OUT 실적 전송 .. 완료>>>>" + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
							        	        
			       	        	
				        	} else if("PB".equals(ydUtils.substr(szYdUpWrLoc, 0, 2))||szYdUpWrLoc.startsWith("PART13")||szYdUpWrLoc.startsWith("PART9")||szYdUpWrLoc.startsWith("PCRT40")) {
								recL2Para.setField("MSG_ID", 		   "YDP3L501V2"							);
								recL2Para.setField("STL_NO",			szStlNo								);		// 재료번호
								recL2Para.setField("OPERATION_TYPE",	"2"									);		// 1:Book In, 2:Book Out
								recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szYdUpWrLoc, 0, 6)	);		// FROM위치
								recL2Para.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdUpWrLoc, 6, 2)	);    	// 야드적치BED번호
								recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "CR" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "1");   
								szRtnMsg = ydDelegate.sendMsg(recL2Para);

								szMsg = "[" + szOperationName  + "] RT BOOK-OUT 실적 전송 .. 완료>>>>" + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
							} else {
//								recL2Para.setField("MSG_ID", 		"YDP2L501");
//								recL2Para.setField("STL_NO",			szStlNo);								// 재료번호
//								recL2Para.setField("OPERATION_TYPE",	"2");									// 1:Book In, 2:Book Out
//								recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szYdUpWrLoc, 0, 6));		// TO위치
//								recL2Para.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdUpWrLoc, 6, 2));    	// 야드적치BED번호
//
//								szRtnMsg = ydDelegate.sendMsg(recL2Para);
								
								szRtnMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("2", szStlNo, szYdUpWrLoc,"P" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "CR" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "1");

			    		        szMsg = "[" + szOperationName  + "] 1 후판전단L2 RT BOOK-OUT 실적 전송 .. 완료>>>>" + szRtnMsg;
			                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
							}
						}

						//------------------------------------------------------------
						// 1 후판전단L2 RT BOOK-IN 실적 전송 (YDP2L501)
						// 1 후판열처리L2 RT BOOK-IN 실적 전송 (YDP3L501)
						//------------------------------------------------------------
						if ("RT".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {

							szMsg = "[" + szOperationName  + "] RT BOOK-IN 실적 전송 .. 시작";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

							recL2Para = JDTORecordFactory.getInstance().create();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 #2 열처리 YDP8L501(Bookin/out complete) 송신 추가 
//			  야드스케쥴코드(YD_SCH_CD) BOOK-IN - PART31UM, PART32UM, PART34UM, PART35UM
//-------------------------------------------------------------------------------------------------------------------------
				        	szMsg = "[" + szOperationName + "] 야드스케쥴코드 [" + szYdSchCd + "]";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			    	        if ( ydUtils.is2ndHeatBookInSchdule( szYdSchCd,  szYdDnWrLoc) ) {  
			    	        	
								recL2Para.setField("MSG_ID", 			"YDP8L501"							);
								recL2Para.setField("STL_NO",			szStlNo								);		// 재료번호
								recL2Para.setField("OPERATION_TYPE",	"1"									);		// 1:Book In, 2:Book Out
								recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szYdDnWrLoc, 0, 6)	);		// TO위치
								recL2Para.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdDnWrLoc, 6, 2)	);    	// 야드적치BED번호
								recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "CR" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "1");  
								
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recL2Para에 logId 추가 
								recL2Para.setField("LOG_ID", 			logId 									);      // logId
//-------------------------------------------------------------------------------------------------------------------------
    
								szRtnMsg = ydDelegate.sendMsg(recL2Para);

								szMsg = "[" + szOperationName  + "] RT BOOK-IN 실적 전송 .. 완료>>>>" + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			       	        	
				        	} else if("PB".equals(ydUtils.substr(szYdDnWrLoc, 0, 2))||szYdDnWrLoc.startsWith("PART13")||szYdDnWrLoc.startsWith("PART9")||szYdDnWrLoc.startsWith("PCRT40")) {
								recL2Para.setField("MSG_ID", 			"YDP3L501V2"						);
								recL2Para.setField("STL_NO",			szStlNo								);		// 재료번호
								recL2Para.setField("OPERATION_TYPE",	"1"									);		// 1:Book In, 2:Book Out
								recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szYdDnWrLoc, 0, 6)	);		// TO위치
								recL2Para.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdDnWrLoc, 6, 2)	);    	// 야드적치BED번호
								recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "CR" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "1");  
								szRtnMsg = ydDelegate.sendMsg(recL2Para);

								szMsg = "[" + szOperationName  + "] RT BOOK-IN 실적 전송 .. 완료>>>>" + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
							} else {
//								recL2Para.setField("MSG_ID", 		"YDP2L501");
//								recL2Para.setField("STL_NO",			szStlNo);								// 재료번호
//								recL2Para.setField("OPERATION_TYPE",	"1");									// 1:Book In, 2:Book Out
//								recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szYdDnWrLoc, 0, 6));		// TO위치
//								recL2Para.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdDnWrLoc, 6, 2));    	// 야드적치BED번호
//
//								szRtnMsg = ydDelegate.sendMsg(recL2Para);
//
//								szMsg = "[" + szOperationName  + "] RT BOOK-IN 실적 전송 .. 완료>>>>" + szRtnMsg;
//								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
								
								szRtnMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", szStlNo, szYdDnWrLoc,"P" + ydUtils.substr(szYdDnWrLoc, 1, 2) + "CR" + ydUtils.substr(szYdDnWrLoc, 1, 2) + "1");

			    		        szMsg = "[" + szOperationName  + "] 1 후판전단L2 RT BOOK-OUT 실적 전송 .. 완료>>>>" + szRtnMsg;
			                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);								
							}
						}
					}
				}
			}
			
			if("BOOK-OUT".equals(szBookInOutBackupGp)) {
				//BOOK-OUT 실적 BACKUP
				szMsg = "[" + szOperationName  + "] RT BOOK-OUT 실적 BACKUP 전송 .. 시작 ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
				recL2Para = JDTORecordFactory.getInstance().create();
				

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 #2 열처리 YDP8L501(Bookin/out complete) 송신 추가 
//			  야드스케쥴코드(YD_SCH_CD) BOOK-OUT - PART31LM, PART32LM, PART34LM, PART35LM
//-------------------------------------------------------------------------------------------------------------------------
	        	szMsg = "[" + szOperationName + "] 야드스케쥴코드 [" + szYdSchCd + "]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
    	        if ( ydUtils.is2ndHeatBookOutSchdule(szYdSchCd, szYdUpWrLoc) ) {
    	        	
					recL2Para.setField("MSG_ID", 		    "YDP8L501"						);
					recL2Para.setField("STL_NO",			szStlNo							);		// 재료번호
					recL2Para.setField("OPERATION_TYPE",	"2"								);		// 1:Book In, 2:Book Out
					recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szRtLoc, 0, 6)	);		// FROM위치
					recL2Para.setField("YD_STK_BED_NO", 	"01"							);    	// 야드적치BED번호
					recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szRtLoc, 1, 1) + "CR" + ydUtils.substr(szRtLoc, 1, 1) + "1");   
				        	        
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recL2Para에 logId 추가 
					recL2Para.setField("LOG_ID", 			logId 							);      // logId
//-------------------------------------------------------------------------------------------------------------------------
				    	        
					szRtnMsg = ydDelegate.sendMsg(recL2Para);
				    	        	
	        	} else if("PB".equals(ydUtils.substr(szRtLoc, 0, 2))||szRtLoc.startsWith("PART13")||szRtLoc.startsWith("PART9")||szRtLoc.startsWith("PCRT40")) {
					recL2Para.setField("MSG_ID", 		    "YDP3L501V2");
					recL2Para.setField("STL_NO",			szStlNo);								// 재료번호
					recL2Para.setField("OPERATION_TYPE",	"2");									// 1:Book In, 2:Book Out
					recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szRtLoc, 0, 6));		// FROM위치
					recL2Para.setField("YD_STK_BED_NO", 	"01");    	// 야드적치BED번호
					recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szRtLoc, 1, 1) + "CR" + ydUtils.substr(szRtLoc, 1, 1) + "1");   
					szRtnMsg = ydDelegate.sendMsg(recL2Para);

				} else {
					
					szRtnMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("2", szStlNo, szRtLoc,"P" + ydUtils.substr(szRtLoc, 1, 1) + "CR" + ydUtils.substr(szRtLoc, 1, 1) + "1");

				}
				
				
				szMsg = "[" + szOperationName  + "] RT BOOK-OUT 실적 BACKUP 전송 .. 완료 ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
			} else if("BOOK-IN".equals(szBookInOutBackupGp)) {
				//BOOK-IN 실적 BACKUP
				szMsg = "[" + szOperationName  + "] RT BOOK-IN 실적 BACKUP 전송 .. 시작 ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
				recL2Para = JDTORecordFactory.getInstance().create();


//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 #2 열처리 YDP8L501(Bookin/out complete) 송신 추가 
//			  야드스케쥴코드(YD_SCH_CD) BOOK-IN - PART31UM, PART32UM, PART34UM, PART35UM
//-------------------------------------------------------------------------------------------------------------------------
	        	szMsg = "[" + szOperationName + "] 야드스케쥴코드 [" + szYdSchCd + "]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
								
    	        if ( ydUtils.is2ndHeatBookInSchdule( szYdSchCd,  szRtLoc) ) {
        	        	
					recL2Para.setField("MSG_ID", 			"YDP8L501"						);
					recL2Para.setField("STL_NO",			szStlNo							);		// 재료번호
					recL2Para.setField("OPERATION_TYPE",	"1"								);		// 1:Book In, 2:Book Out
					recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szRtLoc, 0, 6)	);		// TO위치
					recL2Para.setField("YD_STK_BED_NO", 	"01"							);    	// 야드적치BED번호
					recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szRtLoc, 1, 1) + "CR" + ydUtils.substr(szRtLoc, 1, 1) + "1");  
    				        	        
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recL2Para에 logId 추가 
					recL2Para.setField("LOG_ID", 			logId 							);      // logId
//-------------------------------------------------------------------------------------------------------------------------
    				    	        
					szRtnMsg = ydDelegate.sendMsg(recL2Para);
    				    	        	
	        	} else if("PB".equals(ydUtils.substr(szRtLoc, 0, 2))||szRtLoc.startsWith("PART13")||szRtLoc.startsWith("PART9")||szRtLoc.startsWith("PCRT40")) {
					recL2Para.setField("MSG_ID", 			"YDP3L501V2"					);
					recL2Para.setField("STL_NO",			szStlNo							);		// 재료번호
					recL2Para.setField("OPERATION_TYPE",	"1"								);		// 1:Book In, 2:Book Out
					recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szRtLoc, 0, 6)	);		// TO위치
					recL2Para.setField("YD_STK_BED_NO", 	"01"							);    	// 야드적치BED번호
					recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szRtLoc, 1, 1) + "CR" + ydUtils.substr(szRtLoc, 1, 1) + "1");  
					szRtnMsg = ydDelegate.sendMsg(recL2Para);

				} else {
					
					szRtnMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", szStlNo, szRtLoc,"P" + ydUtils.substr(szRtLoc, 1, 1) + "CR" + ydUtils.substr(szRtLoc, 1, 1) + "1");

				}
				
				
				szMsg = "[" + szOperationName  + "] RT BOOK-IN 실적 BACKUP 전송 .. 완료 ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
			}

			return JPlateYdConst.RETN_CD_SUCCESS;

		} catch (Exception e) {

			szMsg = "[ " +szOperationName + "] 조업L3 전문송신 Exception >>>> " + e.getMessage();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}	// end of updJPlateYdStkPosFix2	
	
	/**
	 * [1후판정정야드] 후판정정야드 저장위치 수정3 (트랜잭션 분리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public String updJPlateYdStkPosFix3(JDTORecord inDto, String pSendFlag) {

		int 	intRtnVal 			= 0;
		String	szRtnMsg			= "";
		String 	szMsg  				= "";
		String 	szMethodName		= "updJPlateYdStkPosFix3";
		String 	szOperationName		= "저장위치 수정3";
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
		String  szRtLoc				= null;
		String  szBookInOutBackupGp = null;
		
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

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 로그 개선 
        String logId                     	= ydLogUtils.getJDTOLogId(inDto, "P");  			// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 		// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
      		
//-------------------------------------------------------------------------------------------------------------------------

		try {
			szMsg   = "[Jsp Session : " + szOperationName + "] 메소드 시작 >>>> " + inDto.toString();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setRecord(inDto);

			rsTemp  = JDTORecordFactory.getInstance().createRecordSet("JPlateYdTemp");

			szMsg   = "[Jsp Session : " + szOperationName + "] 적치단정보 조회";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsTemp);		// intGp == 0

			// 단정보가 존재 하지않거나  단데이터 생성이 되지않는경우
			if (intRtnVal  < 1) {
				szRtnStr = "적치단 정보가 존재하지 않습니다";
				szMsg    = "JSP-SESSION " + szOperationName + "] " + szRtnStr;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnStr;
			}

			// 1. 적치 단 정보 UPDATE
			szStlNo 	  		= ydDaoUtils.paraRecChkNull(inDto, "STL_NO"					);
			szYdStkColGp  		= ydDaoUtils.paraRecChkNull(inDto, "YD_STK_COL_GP"			);
			szYdStkBedNo  		= ydDaoUtils.paraRecChkNull(inDto, "YD_STK_BED_NO"			);
			szYdStkLyrNo  		= ydDaoUtils.paraRecChkNull(inDto, "YD_STK_LYR_NO"			);
			szYdSchStGp			= ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_ST_GP", "B"		);		// 야드스케줄 기동 구분 (A:AUTO, M:MANUAL, B:BACK-UP)
			szYdSchCd			= ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_CD"				);		// 야드스케줄 코드
			szDelFlag	 		= ydDaoUtils.paraRecChkNull(inDto, "DEL_FLAG", "N"			);		// 재료삭제 구분
			szModifier 			= ydDaoUtils.paraRecModifier(inDto							);
			szYdUpWrLoc			= ydDaoUtils.paraRecChkNull(inDto, "OLD_YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(inDto, "OLD_YD_STK_BED_NO");
			szYdUpWrLayer		= ydDaoUtils.paraRecChkNull(inDto, "OLD_YD_STK_LYR_NO"		);
			szYdStkLyrMtlStat	= ydDaoUtils.paraRecChkNull(inDto, "YD_STK_LYR_MTL_STAT"	);
			if ("N".equals(szDelFlag)) {
				szYdDnWrLoc   	= szYdStkColGp + szYdStkBedNo;
				szYdDnWrLayer 	= szYdStkLyrNo;
			}
			szRtLoc				= ydDaoUtils.paraRecChkNull(inDto, "RT_LOC"					);
			szBookInOutBackupGp = ydDaoUtils.paraRecChkNull(inDto, "BOOK_INOUT_BACKUP_GP"	);

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", 			szStlNo			);		// 재료번호
			recPara.setField("YD_STK_COL_GP", 	szYdStkColGp	);		// 적치열구분
			recPara.setField("YD_STK_BED_NO", 	szYdStkBedNo	);		// 적치베드
			recPara.setField("YD_STK_LYR_NO", 	szYdStkLyrNo	);		// 적치단
			recPara.setField("YD_SCH_CD", 		""				);
			recPara.setField("YD_WBOOK_ID", 	""				);
			recPara.setField("MODIFIER", 		szModifier);

			szMsg = "재료번호 : [" + szStlNo + "] 권상위치 : [" + szYdUpWrLoc + "-" + szYdUpWrLayer + "], 권하위치 : [" + szYdDnWrLoc + "-" + szYdDnWrLayer + "] 적치상태 : [" + szYdStkLyrMtlStat + "] DEL_FLAG : " + szDelFlag + " " + szRtLoc + " " + szBookInOutBackupGp;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

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

							szMsg = "미 변경된 재료 정보(" + szStlNo + ")는 UPDATE 하지 않습니다.";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

							bHistFlag = false;
						}
					}
				}
			} else {
				bHistFlag = true;
				recPara.setField("YD_STK_LYR_MTL_STAT", "E");
			}

			szMsg = "이력정보 생성 유무 Flag :: " + String.valueOf(bHistFlag);
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//---------------------------------------
			// 이적작업  이력정보에 추가
			//---------------------------------------
			// bHistFlag - 이력정보 생성 유무 Flag
			if (bHistFlag) {

				recHist = JDTORecordFactory.getInstance().create();
				recHist.setField("YD_GP", 			szYdGp			);
				recHist.setField("STL_NO", 			szStlNo			);
				recHist.setField("YD_DN_WR_LOC",	szYdDnWrLoc		);
				recHist.setField("YD_DN_WR_LAYER",	szYdDnWrLayer	);
				recHist.setField("YD_SCH_ST_GP", 	szYdSchStGp		);
				recHist.setField("MODIFIER", 		szModifier		);
				recHist.setField("YD_SCH_CD",		szYdSchCd		);		// 야드스케줄 코드
				recHist.setField("YD_UP_WR_LOC",	szYdUpWrLoc		);		// 권상위치 (적치열+베드)
				recHist.setField("YD_UP_WR_LAYER",	szYdUpWrLayer	);		// 권상위치 (적치단)

				// 이력정보 남기기
				szRtnStr = this.insYdWrkHistPosFix(recHist);
				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnStr)) {
					szMsg = "JSP-SESSION " + szOperationName + "] " + szRtnStr;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnStr;
				}
			}

			// 해당 재료번호가 적치중일 경우 해당 적치 정보 삭제 기능 추가 ==============================================
			JDTORecord 	recDelPara 	= JDTORecordFactory.getInstance().create();
						rsDelInfo	= JDTORecordFactory.getInstance().createRecordSet("rsdelInfo");

			if (!"".equals(szStlNo.trim())) {

				recDelPara.setField("STL_NO"				, szStlNo	);
				recDelPara.setField("YD_STK_LYR_MTL_STAT"	, ""		);
				recDelPara.setField("YD_GP"					, szYdGp	);

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

						szMsg = "기존 재료 위치 정보 : 열[" + szStkColGpFrom + "], 베드[" + szStkBedNoFrom + "], 단[" + szStkLyrNoFrom + "]";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						recDelPara.setField("STL_NO", 				"");
						recDelPara.setField("YD_STK_LYR_MTL_STAT", 	"E");

						//intRtnVal = ydStkLyrDao.updYdStklyrStat(recDelPara);		// intGp == 0
						intRtnVal = ydStkLyrDao.updYdStklyrStat2(recDelPara);		// 트랜잭션 분리 작업
						
						if (intRtnVal <= 0) {
							szRtnStr = "적치단 정보 CLEAR 실패 하였습니다" + Integer.toString(intRtnVal);
							szMsg      = "JSP-SESSION " + szOperationName + "] " + szRtnStr;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
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
					szMsg      = "JSP-SESSION " + szOperationName + "] " + szRtnStr;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
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
				szMsg      = "JSP-SESSION " + szOperationName + "] " + szRtnStr;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnStr;
			}

			szMsg = "JSP-SESSION [저장위치수정] 끝";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			// 2013.08.27 : 저장위치 수정후 적치재료의 길이의 합이 베드(열)의 길이를 초과 하는지 체크
			int iRemainMtlL = ydStkLyrDao.getRemainMtlL(recPara);
			if (iRemainMtlL < 0) {
				szRtnStr 	= "재료길이 합이 적치열길이 초과!! " + Integer.toString(iRemainMtlL*(-1));
				szMsg		= "JSP-SESSION " + szOperationName + "] " + szRtnStr;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnStr;
			}

			if ("Y".equals(pSendFlag)) {
				//---------------------------------------------------
				// FROM 과 TO 저장위치를 비교하여 변경이 없을경우는 송신하지 않음
				//---------------------------------------------------
				if (szYdUpWrLoc.equals(szYdDnWrLoc)) {

					szMsg = "[ " +szOperationName + "] 단 정보만 바뀌었을때 .. 조업L3 전문송신 SKIP";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				} else {

					String sNEW_MODULE_EFF_YN = "N";
					
					JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
					
					sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A032"); //1후판정정야드 PRYDJ018 수신시 YDPRJ011 송신 안함(N=송신함)
					
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 PRYDJ018 수신시 YDPRJ011 송신 안함(N=송신함)  : " + sNEW_MODULE_EFF_YN + " ]]]---", JPlateYdConst.DEBUG, logId);
					
					if(sNEW_MODULE_EFF_YN.equals("Y") && "PRYDJ018".equals(szModifier)) {

						//PRYDJ018 수신으로 보수장에 저장위치 생성된 재료는 YDPRJ011 을 송신 하지 않는다.
						szMsg = "[ " +szOperationName + "] 조업L3 전문송신 안함 <-PRYDJ018 수신으로 보수장에 저장위치 생성된 재료는 YDPRJ011 을 송신 하지 않는다!";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					} else if("TY".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {

						//TY 임시BED 로 권하위치 수정된 경우는 YDPRJ011 을 송신 하지 않는다.
						szMsg = "[ " +szOperationName + "] 조업L3 전문송신 안함 <-TY 임시BED 로 권하위치 수정된 경우는 YDPRJ011 을 송신 하지 않는다!";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						
					} else {
					
						//---------------------------------------------------
						// 조업L3 전문송신 (후판조업 저장위치변경정보 전송  - YDPRJ011)
						//---------------------------------------------------
						szMsg = "[ " +szOperationName + "] 조업L3 전문송신 START";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	
			        	recL3Para = JDTORecordFactory.getInstance().create();
	
			        	recL3Para.setField("MSG_ID", 			"YDPRJ011"							);
			        	recL3Para.setField("YD_STK_COL_FR", 	ydUtils.substr(szYdUpWrLoc, 0, 6)	);			// From적치열
			        	recL3Para.setField("YD_STK_BED_FR", 	ydUtils.substr(szYdUpWrLoc, 6, 2)	);			// From적치BED
			        	recL3Para.setField("YD_STK_COL_TO", 	ydUtils.substr(szYdDnWrLoc, 0, 6)	);			// TO적치열
				        recL3Para.setField("YD_STK_BED_TO", 	ydUtils.substr(szYdDnWrLoc, 6, 2)	);			// TO적치BED
				        recL3Para.setField("YD_EQP_WRK_SH", 	"1"									);			// 야드설비작업매수
			            recL3Para.setField("ARR_STL_NO", 		szStlNo								);
	
			        	szRtnMsg = ydDelegate.sendMsg(recL3Para);
	
						szMsg = "[ " +szOperationName + "] 조업L3 전문송신 END >>>> " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						
					}

					if ("RT".equals(ydUtils.substr(szYdUpWrLoc, 2, 2)) && "RT".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {

						szMsg = "[" +  szOperationName  + "] FROM, TO위치가 RT일때 BOOK-IN/OUT 실적 전송 .. SKIP";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					} else {
						//------------------------------------------------------------
						// 1 후판전단L2 RT BOOK-IN 실적 전송 (YDP2L501)
						// 1 후판열처리L2 RT BOOK-IN 실적 전송 (YDP3L501)
						//------------------------------------------------------------
						if ("RT".equals(ydUtils.substr(szYdUpWrLoc, 2, 2))) {

							szMsg = "[" +  szOperationName  + "] RT BOOK-OUT 실적 전송 .. 시작";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

							recL2Para = JDTORecordFactory.getInstance().create();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 #2 열처리 YDP8L501(Bookin/out complete) 송신 추가 
//			  야드스케쥴코드(YD_SCH_CD) BOOK-OUT - PART31LM, PART32LM, PART34LM, PART35LM
//-------------------------------------------------------------------------------------------------------------------------
				        	szMsg = "[" + szOperationName + "] 야드스케쥴코드 [" + szYdSchCd + "]";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
							
			    	        if ( ydUtils.is2ndHeatBookOutSchdule(szYdSchCd, szYdUpWrLoc) ) {
			    	        	
								recL2Para.setField("MSG_ID", 		   "YDP8L501"							);
								recL2Para.setField("STL_NO",			szStlNo								);		// 재료번호
								recL2Para.setField("OPERATION_TYPE",	"2"									);		// 1:Book In, 2:Book Out
								recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szYdUpWrLoc, 0, 6)	);		// FROM위치
								recL2Para.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdUpWrLoc, 6, 2)	);    	// 야드적치BED번호
								recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "CR" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "1");   
			        	        
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recL2Para에 logId 추가 
								recL2Para.setField("LOG_ID", 			logId 									);      // logId
//-------------------------------------------------------------------------------------------------------------------------
			    	        
								szRtnMsg = ydDelegate.sendMsg(recL2Para);
								
								szMsg = "[" + szOperationName  + "] RT BOOK-OUT 실적 전송 .. 완료>>>>" + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
								
			    	        } else if("PB".equals(ydUtils.substr(szYdUpWrLoc, 0, 2))||szYdUpWrLoc.startsWith("PART13")||szYdUpWrLoc.startsWith("PART9")||szYdUpWrLoc.startsWith("PCRT40")) {
								recL2Para.setField("MSG_ID", 		   "YDP3L501V2"							);
								recL2Para.setField("STL_NO",			szStlNo								);		// 재료번호
								recL2Para.setField("OPERATION_TYPE",	"2"									);		// 1:Book In, 2:Book Out
								recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szYdUpWrLoc, 0, 6)	);		// FROM위치
								recL2Para.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdUpWrLoc, 6, 2)	);    	// 야드적치BED번호
								recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "CR" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "1");   
								szRtnMsg = ydDelegate.sendMsg(recL2Para);

								szMsg = "[" + szOperationName  + "] RT BOOK-OUT 실적 전송 .. 완료>>>>" + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
							} else {
//								recL2Para.setField("MSG_ID", 		"YDP2L501");
//								recL2Para.setField("STL_NO",			szStlNo);								// 재료번호
//								recL2Para.setField("OPERATION_TYPE",	"2");									// 1:Book In, 2:Book Out
//								recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szYdUpWrLoc, 0, 6));		// TO위치
//								recL2Para.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdUpWrLoc, 6, 2));    	// 야드적치BED번호
//
//								szRtnMsg = ydDelegate.sendMsg(recL2Para);
								
								szRtnMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("2", szStlNo, szYdUpWrLoc,"P" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "CR" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "1");

			    		        szMsg = "[" +  szOperationName  + "] 1 후판전단L2 RT BOOK-OUT 실적 전송 .. 완료>>>>" + szRtnMsg;
			                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
							}
						}

						//------------------------------------------------------------
						// 1 후판전단L2 RT BOOK-IN 실적 전송 (YDP2L501)
						// 1 후판열처리L2 RT BOOK-IN 실적 전송 (YDP3L501)
						//------------------------------------------------------------
						if ("RT".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {

							szMsg = "[" +  szOperationName  + "] RT BOOK-IN 실적 전송 .. 시작";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

							recL2Para = JDTORecordFactory.getInstance().create();
							

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 #2 열처리 YDP8L501(Bookin/out complete) 송신 추가 
//			  야드스케쥴코드(YD_SCH_CD) BOOK-IN - PART31UM, PART32UM, PART34UM, PART35UM
//-------------------------------------------------------------------------------------------------------------------------
				        	szMsg = "[" + szOperationName + "] 야드스케쥴코드 [" + szYdSchCd + "]";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
							
			    	        if ( ydUtils.is2ndHeatBookInSchdule( szYdSchCd,  szYdDnWrLoc) ) {
			    	        	
								recL2Para.setField("MSG_ID", 			"YDP8L501"							);
								recL2Para.setField("STL_NO",			szStlNo								);		// 재료번호
								recL2Para.setField("OPERATION_TYPE",	"1"									);		// 1:Book In, 2:Book Out
								recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szYdDnWrLoc, 0, 6)	);		// TO위치
								recL2Para.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdDnWrLoc, 6, 2)	);    	// 야드적치BED번호
								recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "CR" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "1");  
			        	        
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recL2Para에 logId 추가 
								recL2Para.setField("LOG_ID", 			logId 									);      // logId
//-------------------------------------------------------------------------------------------------------------------------
			    	        
								szRtnMsg = ydDelegate.sendMsg(recL2Para);

								szMsg = "[" +  szOperationName  + "] RT BOOK-IN 실적 전송 .. 완료>>>>" + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			    	        	
				        	} else if("PB".equals(ydUtils.substr(szYdDnWrLoc, 0, 2))||szYdDnWrLoc.startsWith("PART13")||szYdDnWrLoc.startsWith("PART9")||szYdDnWrLoc.startsWith("PCRT40")) {
								recL2Para.setField("MSG_ID", 			"YDP3L501V2"						);
								recL2Para.setField("STL_NO",			szStlNo								);		// 재료번호
								recL2Para.setField("OPERATION_TYPE",	"1"									);		// 1:Book In, 2:Book Out
								recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szYdDnWrLoc, 0, 6)	);		// TO위치
								recL2Para.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdDnWrLoc, 6, 2)	);    	// 야드적치BED번호
								recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "CR" + ydUtils.substr(szYdUpWrLoc, 1, 2) + "1");  
								szRtnMsg = ydDelegate.sendMsg(recL2Para);

								szMsg = "[" +  szOperationName  + "] RT BOOK-IN 실적 전송 .. 완료>>>>" + szRtnMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
							} else {
//								recL2Para.setField("MSG_ID", 		"YDP2L501");
//								recL2Para.setField("STL_NO",			szStlNo);								// 재료번호
//								recL2Para.setField("OPERATION_TYPE",	"1");									// 1:Book In, 2:Book Out
//								recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szYdDnWrLoc, 0, 6));		// TO위치
//								recL2Para.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdDnWrLoc, 6, 2));    	// 야드적치BED번호
//
//								szRtnMsg = ydDelegate.sendMsg(recL2Para);
//
//								szMsg = "[" +  szOperationName  + "] RT BOOK-IN 실적 전송 .. 완료>>>>" + szRtnMsg;
//								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
								
								szRtnMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", szStlNo, szYdDnWrLoc,"P" + ydUtils.substr(szYdDnWrLoc, 1, 2) + "CR" + ydUtils.substr(szYdDnWrLoc, 1, 2) + "1");

			    		        szMsg = "[" +  szOperationName  + "] 1 후판전단L2 RT BOOK-OUT 실적 전송 .. 완료>>>>" + szRtnMsg;
			                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);								
							}
						}
					}
				}
			}
			
			if("BOOK-OUT".equals(szBookInOutBackupGp)) {
				//BOOK-OUT 실적 BACKUP
				szMsg = "[" +  szOperationName  + "] RT BOOK-OUT 실적 BACKUP 전송 .. 시작 ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
				recL2Para = JDTORecordFactory.getInstance().create();
				

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 #2 열처리 YDP8L501(Bookin/out complete) 송신 추가 
//			  야드스케쥴코드(YD_SCH_CD) BOOK-OUT - PART31LM, PART32LM, PART34LM, PART35LM
//-------------------------------------------------------------------------------------------------------------------------
	        	szMsg = "[" + szOperationName + "] 야드스케쥴코드 [" + szYdSchCd + "]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
    	        if ( ydUtils.is2ndHeatBookOutSchdule(szYdSchCd, szRtLoc) ) {
    	        	
					recL2Para.setField("MSG_ID", 		    "YDP8L501"						);
					recL2Para.setField("STL_NO",			szStlNo							);		// 재료번호
					recL2Para.setField("OPERATION_TYPE",	"2"								);		// 1:Book In, 2:Book Out
					recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szRtLoc, 0, 6)	);		// FROM위치
					recL2Para.setField("YD_STK_BED_NO", 	"01"							);    	// 야드적치BED번호
					recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szRtLoc, 1, 1) + "CR" + ydUtils.substr(szRtLoc, 1, 1) + "1");   
        	        
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recL2Para에 logId 추가 
					recL2Para.setField("LOG_ID", 			logId 							);      // logId
//-------------------------------------------------------------------------------------------------------------------------
    	        
					szRtnMsg = ydDelegate.sendMsg(recL2Para);
    	        	
	        	} else if("PB".equals(ydUtils.substr(szRtLoc, 0, 2))||szRtLoc.startsWith("PART13")||szRtLoc.startsWith("PART9")||szRtLoc.startsWith("PCRT40")) {
					recL2Para.setField("MSG_ID", 		    "YDP3L501V2"					);
					recL2Para.setField("STL_NO",			szStlNo							);		// 재료번호
					recL2Para.setField("OPERATION_TYPE",	"2"								);		// 1:Book In, 2:Book Out
					recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szRtLoc, 0, 6)	);		// FROM위치
					recL2Para.setField("YD_STK_BED_NO", 	"01"							);    	// 야드적치BED번호
					recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szRtLoc, 1, 1) + "CR" + ydUtils.substr(szRtLoc, 1, 1) + "1");   
					szRtnMsg = ydDelegate.sendMsg(recL2Para);

				} else {
					
					szRtnMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("2", szStlNo, szRtLoc,"P" + ydUtils.substr(szRtLoc, 1, 1) + "CR" + ydUtils.substr(szRtLoc, 1, 1) + "1");

				}
				
				
				szMsg = "[" +  szOperationName  + "] RT BOOK-OUT 실적 BACKUP 전송 .. 완료 ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
			} else if("BOOK-IN".equals(szBookInOutBackupGp)) {
				//BOOK-IN 실적 BACKUP
				szMsg = "[" +  szOperationName  + "] RT BOOK-IN 실적 BACKUP 전송 .. 시작 ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
				recL2Para = JDTORecordFactory.getInstance().create();


//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 #2 열처리 YDP8L501(Bookin/out complete) 송신 추가 
//			  야드스케쥴코드(YD_SCH_CD) BOOK-IN - PART31UM, PART32UM, PART34UM, PART35UM
//-------------------------------------------------------------------------------------------------------------------------
	        	szMsg = "[" + szOperationName + "] 야드스케쥴코드 [" + szYdSchCd + "]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
    	        if ( ydUtils.is2ndHeatBookInSchdule( szYdSchCd,  szRtLoc) ) {
    	        	
					recL2Para.setField("MSG_ID", 			"YDP8L501"						);
					recL2Para.setField("STL_NO",			szStlNo							);		// 재료번호
					recL2Para.setField("OPERATION_TYPE",	"1"								);		// 1:Book In, 2:Book Out
					recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szRtLoc, 0, 6)	);		// TO위치
					recL2Para.setField("YD_STK_BED_NO", 	"01"							);    	// 야드적치BED번호
					recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szRtLoc, 1, 1) + "CR" + ydUtils.substr(szRtLoc, 1, 1) + "1");  
        	        
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recL2Para에 logId 추가 
					recL2Para.setField("LOG_ID", 			logId 									);      // logId
//-------------------------------------------------------------------------------------------------------------------------
    	        
					szRtnMsg = ydDelegate.sendMsg(recL2Para);
    	        	
	        	} else if("PB".equals(ydUtils.substr(szRtLoc, 0, 2))||szRtLoc.startsWith("PART13")||szRtLoc.startsWith("PART9")||szRtLoc.startsWith("PCRT40")) {
					recL2Para.setField("MSG_ID", 			"YDP3L501V2"					);
					recL2Para.setField("STL_NO",			szStlNo							);		// 재료번호
					recL2Para.setField("OPERATION_TYPE",	"1"								);		// 1:Book In, 2:Book Out
					recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szRtLoc, 0, 6)	);		// TO위치
					recL2Para.setField("YD_STK_BED_NO", 	"01"							);    	// 야드적치BED번호
					recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szRtLoc, 1, 1) + "CR" + ydUtils.substr(szRtLoc, 1, 1) + "1");  
					szRtnMsg = ydDelegate.sendMsg(recL2Para);

				} else {
					
					szRtnMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", szStlNo, szRtLoc,"P" + ydUtils.substr(szRtLoc, 1, 1) + "CR" + ydUtils.substr(szRtLoc, 1, 1) + "1");

				}
				
				
				szMsg = "[" +  szOperationName  + "] RT BOOK-IN 실적 BACKUP 전송 .. 완료 ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
			}

			return JPlateYdConst.RETN_CD_SUCCESS;

		} catch (Exception e) {

			szMsg = "[ " +szOperationName + "] 조업L3 전문송신 Exception >>>> " + e.getMessage();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}	// end of updJPlateYdStkPosFix3	
	
	/**
	 * 차량 지정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord doCarNoSet(GridData gdReq) throws DAOException {
		String methodNm = "차량 지정[JPlateYDYDPJspSeEJB.doCarNoSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String szRtnMsg = JPlateYdConst.RETN_CD_SUCCESS;
			String szMsg = null;
			
			String szYD_GP="";
			
			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
			jrParam.setField("YD_STK_COL_GP"		, gdReq.getParam("YD_TR_GP") );
			jrParam.setField("CAR_NO"				, gdReq.getParam("CAR_NO") );
			commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updCarNoSet", logId, methodNm, "차량번호 UPDATE ");
				
			jrParam.setField("MODIFIER", 			gdReq.getParam("userid"));
			jrParam.setField("YD_START_LOC",		gdReq.getParam("YD_TR_GP"));		// 선택 위치
			jrParam.setField("YD_STOP_LOC",			gdReq.getParam("YD_TR_GP"));		// 선택 위치
			
			szYD_GP=gdReq.getParam("YD_TR_GP").substring(0,1);//창고 구분 P:1후판정정 F:2후판 정정
			
	    	//-------------------------------------------------------------
	    	// 차량 도착 저장위치(베드) 활성화 처리
			/* -> 2019.02.14 차량포인트 모두 활성화 시킴
			EJBConnector ejbConn = new EJBConnector("default", "JPlateYdTcarSchSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("enableFromBed2", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
	    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				
				szRtnMsg = "차량 도착 저장위치(베드) 활성화 처리시 오류 발생 : " + szRtnMsg;
		    	szMsg 	 = "[" + methodNm + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
				
				jrRtn.setField("RTN_MSG", szRtnMsg);
	    		return jrRtn;
	    	}
			*/
			
	    	//-------------------------------------------------------------
	    	// 야드  L2 Interface 처리
	    	//-------------------------------------------------------------
			JDTORecord recL2Para = JDTORecordFactory.getInstance().create();;
	    	
	    	// - 저장위치 제원정보 야드L2전송 (활성화)
			//2021.11.08 박종호. 기존: YDY2L001 변경:YDY2L001/YDY7L001로 1/2후판 정정 분기처리.(2후판정정 차량작업관리 추가)
	    	// - 저장위치 제원정보 야드L2전송 (FROM:비활성화)
			if(szYD_GP.equals("P")) //1후판정정
			{
				recL2Para.setField("MSG_ID", 			"YDY2L001");  //적치대제원
			}
			else //F 2후판정정 
			{
				recL2Para.setField("MSG_ID", 			"YDY7L001"); //적치대제원
			}
			recL2Para.setField("YD_INFO_SYNC_CD", "3");  // 1:동, 2:SPAN, 3:열, 4:BED
			
			if(szYD_GP.equals("P")) //1후판정정
			{
				recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);
			}
			else //2후판정정
			{
				recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);
			}
			recL2Para.setField("YD_STK_COL_GP", 	gdReq.getParam("YD_TR_GP"));
			recL2Para.setField("YD_STK_BED_NO", 	"");
			szRtnMsg = ydDelegate.sendMsg(recL2Para);
	    	
				
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of doCarNoSet
	
	/**
	 * 차량 해제 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord doCarNoClear(GridData gdReq) throws DAOException {
		String methodNm = "차량 해제[JPlateYDYDPJspSeEJB.doCarNoClear] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String szRtnMsg = JPlateYdConst.RETN_CD_SUCCESS;
			String szMsg = null;
			
			String szYD_GP="";
			
			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
			jrParam.setField("YD_STK_COL_GP"		, gdReq.getParam("YD_TR_GP") );
			jrParam.setField("CAR_NO"				, gdReq.getParam("CAR_NO") );
			commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updCarNoClear", logId, methodNm, "차량번호 Clear ");
				
			jrParam.setField("MODIFIER", 			gdReq.getParam("userid"));
			jrParam.setField("YD_START_LOC",		gdReq.getParam("YD_TR_GP"));		// 선택 위치
			
			szYD_GP=gdReq.getParam("YD_TR_GP").substring(0,1);//창고 구분 P:1후판정정 F:2후판 정정
			
	    	//-------------------------------------------------------------
	    	// 차량 출발 저장위치(베드) 비활성화 처리
			/* -> 2019.02.14 차량포인트 모두 활성화 시킴
			EJBConnector ejbConn = new EJBConnector("default", "JPlateYdTcarSchSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("disableToBed", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
	    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				
				szRtnMsg = "차량 출발 저장위치(베드) 비활성화 처리시 오류 발생 : " + szRtnMsg;
		    	szMsg 	 = "[" + methodNm + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
				
				jrRtn.setField("RTN_MSG", szRtnMsg);
	    		return jrRtn;
	    	}			
			*/
			
	    	//-------------------------------------------------------------
	    	// 야드  L2 Interface 처리
	    	//-------------------------------------------------------------
			JDTORecord recL2Para = JDTORecordFactory.getInstance().create();;
	    	
			//2021.11.08 박종호. 기존: YDY2L001 변경:YDY2L001/YDY7L001로 1/2후판 정정 분기처리.(2후판정정 차량작업관리 추가)
	    	// - 저장위치 제원정보 야드L2전송 (FROM:비활성화)
			if(szYD_GP.equals("P")){ //1후판정정
				recL2Para.setField("MSG_ID", 			"YDY2L001");  //적치대제원
			}
			else{ //F 2후판정정 
				recL2Para.setField("MSG_ID", 			"YDY7L001"); //적치대제원
			}
			recL2Para.setField("YD_INFO_SYNC_CD", "3");  // 1:동, 2:SPAN, 3:열, 4:BED
			
			if(szYD_GP.equals("P")) //1후판정정
			{
				recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);
			}
			else //F 2후판정정
			{
				recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);
			}
			recL2Para.setField("YD_STK_COL_GP", 	gdReq.getParam("YD_TR_GP"));
			recL2Para.setField("YD_STK_BED_NO", 	"");
			szRtnMsg = ydDelegate.sendMsg(recL2Para);
	    	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of doCarNoClear	
	
	/**
	 * 차량이송백업처리 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord doTrSendBackup(GridData gdReq) throws DAOException {
		String methodNm = "차량이송백업처리[JPlateYDYDPJspSeEJB.doTrSendBackup] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String szSTL_NO = null;
		String szYD_STK_LYR_NO		= null;
		JDTORecordSet 	rsResult		= null;
		JDTORecord recInTemp        = null;

		JPlateYdCrnSchDAO	ydCrnSchDao	= new JPlateYdCrnSchDAO();
		
		int 	intRtnVal 				= 0;
		
	    String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
	    String	szArrStlNo			= "";
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			
			String szYD_GP  = gdReq.getParam("YD_GP"); //야드구분
			String sFromLoc = commUtils.trim(gdReq.getParam("YD_TR_GP"));
			String sToLoc	= commUtils.trim(gdReq.getParam("TO_TR_GP"));
			
			//차량작업 크레인 스케줄이 생성되어 있으면 대차이동처리 백업 불가
	    	String szYD_SCH_CD = szYD_GP  + ydUtils.substr(sFromLoc, 1, 5) + "_M";

	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
	    	jrParam.setField("YD_SCH_CD", szYD_SCH_CD);
	    	intRtnVal = ydCrnSchDao.getByYdSchCd(jrParam, rsResult);
	    	if (intRtnVal > 0) {
		    	szRtnMsg = "크레인 작업지시가 존재하여 차량이송백업처리 불가합니다.!";
		    	szMsg 	 = "[" + methodNm + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
				
				jrRtn.setField("RTN_MSG", szRtnMsg);
	    		return jrRtn;
	    	}
	    	
	    	szYD_SCH_CD = szYD_GP  + ydUtils.substr(sToLoc, 1, 5) + "_M";

	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
	    	jrParam.setField("YD_SCH_CD", szYD_SCH_CD);
	    	intRtnVal = ydCrnSchDao.getByYdSchCd(jrParam, rsResult);
	    	if (intRtnVal > 0) {
		    	szRtnMsg = "크레인 작업지시가 존재하여 차량이송백업처리 불가합니다.!";
		    	szMsg 	 = "[" + methodNm + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
				
				jrRtn.setField("RTN_MSG", szRtnMsg);
	    		return jrRtn;
	    	}
			
			jrParam.setField("YD_STK_COL_GP"	,sToLoc);
			JDTORecordSet getRecSet = commDao.select(jrParam,"com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getStlNoCnt", logId, methodNm, "TO위치가 비워있는지 체크");

			if (getRecSet.size() < 0) {
				szMsg="["+methodNm+"] TO위치 차량포인트가 조회 실패! ";
				commUtils.printLog(logId, methodNm,szMsg);
				throw new Exception(szMsg);
			}
			
			if(!"0".equals(getRecSet.getRecord(0).getFieldString("CNT"))) {
				szMsg="["+methodNm+"] TO위치 차량포인트가 비워 있지 않습니다! 작업 확인 후 다시 실행해 주세요! ";
				commUtils.printLog(logId, methodNm,szMsg);
				throw new Exception(szMsg);
			}
			
			jrParam.setField("MODIFIER", 			gdReq.getParam("userid"));
			jrParam.setField("YD_START_LOC",		sFromLoc);		// 대차 출발 위치
			jrParam.setField("YD_STOP_LOC",			sToLoc);		// 대차 도착 위치
			  
			
			  
			
	    	//-------------------------------------------------------------
	    	// 차량 도착 저장위치(베드) 활성화 처리
			/* -> 2019.02.14 차량포인트 모두 활성화 시킴
			EJBConnector ejbConn = new EJBConnector("default", "JPlateYdTcarSchSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("enableFromBed", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
	    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				
				szRtnMsg = "차량 도착 저장위치(베드) 활성화 처리시 오류 발생 : " + szRtnMsg;
		    	szMsg 	 = "[" + methodNm + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
				
				jrRtn.setField("RTN_MSG", szRtnMsg);
	    		return jrRtn;
	    	}
	    	*/
			
			JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();
			
			jrParam.setField("YD_STK_COL_GP_FR",	sFromLoc);
			jrParam.setField("YD_STK_COL_GP_TO",	sToLoc);
			jrParam.setField("YD_STK_LYR_ACT_STAT",	"E");					// C:Close(비활성화), E:적치 가능, F:적치 완료, N:사용 불가
			jrParam.setField("MODIFIER", 			gdReq.getParam("userid"));
			jrParam.setField("OCPY_CHK_FLAG",		"N");					// 점유베드 체크 안함
			
	    	// 차량 도착 저장위치(적치단) 활성화 처리 및 재료적치정보 복사
	    	intRtnVal = ydStkLyrDao.copyTcarFromBed(jrParam);
	    	if (intRtnVal <= 0) {
	    		szRtnMsg = "차량도착 적치단 활성화 처리시 오류 :: " + Integer.toString(intRtnVal);
	    		szMsg = "[" + methodNm + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
	    		
	    		jrRtn.setField("RTN_MSG", szRtnMsg);
	    		return jrRtn;
	    	}			
			

	    	//-------------------------------------------------------------
	    	// 차량 출발 저장위치(베드) 비활성화 처리
			/* -> 2019.02.14 차량포인트 모두 활성화 시킴
			szRtnMsg = (String)ejbConn.trx("disableToBed", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
	    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				
				szRtnMsg = "차량 출발 저장위치(베드) 비활성화 처리시 오류 발생 : " + szRtnMsg;
		    	szMsg 	 = "[" + methodNm + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
				
				jrRtn.setField("RTN_MSG", szRtnMsg);
	    		return jrRtn;
	    	}	
	    	*/	
	    	
	    	jrParam.setField("STL_NO",				"");
	    	jrParam.setField("MODIFIER",			gdReq.getParam("userid"));
	    	if(szYD_GP.equals("F")) // 2후판 정정 내 차량 FROM 위치 적치 가능 처리.
	    	{
	    		jrParam.setField("YD_STK_LYR_ACT_STAT",	"E");					// C:Close(비활성화), E:적치 가능, F:적치 완료, N:사용 불가
	    	}
	    	else //1후판정정 (1 후판 정정 차량 포인트는 SQL내에서 예외로 E처리
	    	{
	    		jrParam.setField("YD_STK_LYR_ACT_STAT",	"C");					// C:Close(비활성화), E:적치 가능, F:적치 완료, N:사용 불가
	    	}
	    	
	    	jrParam.setField("YD_STK_LYR_MTL_STAT",	"E");
	    	jrParam.setField("YD_STK_COL_GP",		sFromLoc);
	    	jrParam.setField("YD_STK_BED_NO",		"");
	    	jrParam.setField("YD_OCPY_BED_GP",		"");
	    	jrParam.setField("YD_OCPY_STK_BED_NO",	"");
	    	jrParam.setField("YD_OCPY_STK_LYR_NO",	"");
	    	jrParam.setField("OCPY_CHK_FLAG",		"N");					// 점유베드 체크 안함

	    	// 차량 도착 저장위치(적치단) 비활성화 처리
	    	intRtnVal = ydStkLyrDao.updYdStkLyrActStat(jrParam);
	    	if (intRtnVal <= 0) {
	    		szRtnMsg = "차량 출발위치의 베드 비활성화 처리시 오류 :: " + sFromLoc;
	    		szMsg = "[" + methodNm + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
	    		
	    		jrRtn.setField("RTN_MSG", szRtnMsg);
	    		return jrRtn;
	    	}            	    	

			//이전위치 차량 Clear
			jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
			jrParam.setField("YD_STK_COL_GP"		, sFromLoc );
			jrParam.setField("CAR_NO"				, gdReq.getParam("CAR_NO") );
			commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updCarNoClear", logId, methodNm, "차량번호 Clear ");
			
			//TO위치 차량 지정
			jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
			jrParam.setField("YD_STK_COL_GP"		, sToLoc );
			jrParam.setField("CAR_NO"				, gdReq.getParam("CAR_NO") );
			commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updCarNoSet", logId, methodNm, "차량번호 UPDATE ");
	    	
	    	//-------------------------------------------------------------
	    	// 야드  L2 Interface 처리
	    	//-------------------------------------------------------------
			JDTORecord recL2Para = JDTORecordFactory.getInstance().create();;
	    	
	    	// - 저장위치 제원정보 야드L2전송 (FROM:비활성화)
			if(szYD_GP.equals("P"))  //1후판정정
			{
				recL2Para.setField("MSG_ID", 			"YDY2L001");  //1후판정정 적치대제원
			}
			else //2후판정정(F)
			{
				recL2Para.setField("MSG_ID", 			"YDY7L001"); //2후판정정 적치대제원
			}
			recL2Para.setField("YD_INFO_SYNC_CD", "3");  // 1:동, 2:SPAN, 3:열, 4:BED
			
			if(szYD_GP.equals("P"))  //1후판정정
			{
				recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);
			}
			else //2후판정정(F)
			{
				recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);
			}
			recL2Para.setField("YD_STK_COL_GP", 	sFromLoc);
			recL2Para.setField("YD_STK_BED_NO", 	"");
			szRtnMsg = ydDelegate.sendMsg(recL2Para);
			
	    	// - 저장위치 제원정보 야드L2전송 (TO:활성화)
			if(szYD_GP.equals("P"))  //1후판정정
			{
				recL2Para.setField("MSG_ID", 			"YDY2L001");
			}
			else //2후판정정(F)
			{
				recL2Para.setField("MSG_ID", 			"YDY7L001");
			}
			recL2Para.setField("YD_INFO_SYNC_CD", "3");  // 1:동, 2:SPAN, 3:열, 4:BED
			
			if(szYD_GP.equals("P"))  //1후판정정
			{
				recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);
			}
			else //2후판정정(F)
			{
				recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);
			}
			
			recL2Para.setField("YD_STK_COL_GP", 	sToLoc);
			recL2Para.setField("YD_STK_BED_NO", 	"");
			szRtnMsg = ydDelegate.sendMsg(recL2Para);
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			if(rowCnt > 0) {
				
				// - 저장품 제원정보 야드L2전송
				if(szYD_GP.equals("P"))  //1후판정정
				{
					recL2Para.setField("MSG_ID", 		"YDY2L002");                        // TC-CODE
					recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);	// 야드구분
				}
				else{ //2후판정정(F)
					recL2Para.setField("MSG_ID", 		"YDY7L002");                        // TC-CODE
					recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);	// 야드구분
				}
				recL2Para.setField("YD_STK_COL_GP", 	sToLoc);                         	// 야드적치열구분
				recL2Para.setField("YD_STK_BED_NO", 	"");    							// 야드적치BED번호
				recL2Para.setField("YD_INFO_SYNC_CD",   "3");								// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
				recL2Para.setField("STL_NO", 			"");	        					// 재료번호
				szRtnMsg = ydDelegate.sendMsg(recL2Para);
				
				for (int ii = 0; ii < rowCnt; ii++) {
					szSTL_NO = commUtils.trim(gdReq.getHeader("STL_NO").getValue(ii));
			        if (ii > 0) {
			        	szArrStlNo = szArrStlNo + ";";
			        }
		        	szArrStlNo = szArrStlNo + szSTL_NO;
				}
				
				//F야드용 전문 필요..현재는 P야드만 존재(YDPRJ011) 2021.11.08 박종호
				
				//해당 대기장에 있는 포인트가 다른 후판공장에서(1->2후판, 2->1후판)넘어온 재료인지 체크 2024.04.02
				jrParam.setField("YD_STK_COL_GP"	,sFromLoc);
				getRecSet = commDao.select(jrParam,"com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdStkColList", logId, methodNm, "FROM위치 FLAG값 체크(공장간이송여부)");
				String szIS_PF_TO_PF="";
				
				if(getRecSet.size() > 0){
					szIS_PF_TO_PF=getRecSet.getRecord(0).getFieldString("YD_LOC_GP");  //YD_LOC_GP값을 안쓰는것같아서, 해당 필드로 사용(공장간 이송시, 대기장 셋팅시점에 함께 셋팅)
				}
				
				szRtnMsg = "공장간 이송여부 항목 값 : " + szIS_PF_TO_PF;
	    		szMsg = "[" + methodNm + "] " + szRtnMsg;
	    		ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.DEBUG);
				
				
				
		        recInTemp = JDTORecordFactory.getInstance().create();
		        
		        
		        if(szYD_GP.equals("P")){  //1후판정정
			        recInTemp.setField("JMS_TC_CD", 			"YDPRJ011");  //후판제품저장위치변경(P 야드)
			        recInTemp.setField("YD_STK_COL_FR", 	sFromLoc);	// From적치열
			        recInTemp.setField("YD_STK_BED_FR", 	"");		// From적치BED
			        recInTemp.setField("YD_STK_COL_TO", 	sToLoc);	// TO적치열
			        recInTemp.setField("YD_STK_BED_TO", 	"");		// TO적치BED
			        recInTemp.setField("YD_EQP_WRK_SH", 	"");		// 야드설비작업매수
			        recInTemp.setField("ARR_STL_NO", 		szArrStlNo);
			        
			        recInTemp.setField("IS_PF_TO_PF", 		szIS_PF_TO_PF);//후판 공장간 이송여부 Y:공장간 이송, "":공장간 이송 X
			        
			        szRtnMsg = JPlateYdCommonUtils.sendL3YDPRJ011(recInTemp);
		        }
		        else{  //2후판정정(F)
			        recInTemp.setField("MSG_ID", 			"YDPPJ011");  //후판제품저장위치변경(F 야드)
			        recInTemp.setField("YD_STK_COL_FR", 	sFromLoc);	// From적치열
			        recInTemp.setField("YD_STK_BED_FR", 	"");		// From적치BED
			        recInTemp.setField("YD_STK_COL_TO", 	sToLoc);	// TO적치열
			        recInTemp.setField("YD_STK_BED_TO", 	"");		// TO적치BED
			        recInTemp.setField("YD_EQP_WRK_SH", 	"");		// 야드설비작업매수
			        recInTemp.setField("ARR_STL_NO", 		szArrStlNo);
			        
			        recInTemp.setField("IS_PF_TO_PF", 		szIS_PF_TO_PF);//후판 공장간 이송여부 Y:공장간 이송, "":공장간 이송 X

			        szRtnMsg = JPlateYdCommonUtils.sendL3YDPPJ011(recInTemp);		        	
		        }
			}
			
			jrRtn.setField("RTN_MSG", JPlateYdConst.RETN_CD_SUCCESS);
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of 차량이송백업처리

	/**
	 * 차량포인트 초기화 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord doTrPointInit(GridData gdReq) throws DAOException {
		String methodNm = "차량포인트 초기화[JPlateYDYDPJspSeEJB.doTrPointInit] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String szSTL_NO = null;
		String szYD_STK_BED_NO      = null;
		String szYD_STK_LYR_NO		= null;
		JDTORecordSet 	rsResult		= null;
		JDTORecord recInTemp        = null;
		JDTORecord recHist = null;

		JPlateYdCrnSchDAO	ydCrnSchDao	= new JPlateYdCrnSchDAO();
		JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();
		
		int 	intRtnVal 				= 0;
		
	    String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
	    String	szArrStlNo			= "";
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			
			String szYD_GP  = gdReq.getParam("YD_GP"); //야드구분
			String sFromLoc = commUtils.trim(gdReq.getParam("YD_TR_GP"));
			
			//차량작업 크레인 스케줄이 생성되어 있으면 대차이동처리 백업 불가
	    	String szYD_SCH_CD = szYD_GP  + ydUtils.substr(sFromLoc, 1, 5) + "_M";

	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
	    	jrParam.setField("YD_SCH_CD", szYD_SCH_CD);
	    	intRtnVal = ydCrnSchDao.getByYdSchCd(jrParam, rsResult);
	    	if (intRtnVal > 0) {
		    	szRtnMsg = "크레인 작업지시가 존재하여 초기화 처리 불가합니다.!";
		    	szMsg 	 = "[" + methodNm + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
				
				jrRtn.setField("RTN_MSG", szRtnMsg);
	    		return jrRtn;
	    	}
			
	    	recHist = JDTORecordFactory.getInstance().create();

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
	    	
			for (int ii = 0; ii < rowCnt; ii++) {
				
				szSTL_NO = commUtils.trim(gdReq.getHeader("STL_NO").getValue(ii));
				szYD_STK_LYR_NO = commUtils.trim(gdReq.getHeader("YD_STK_LYR_NO").getValue(ii));
				szYD_STK_BED_NO = commUtils.trim(gdReq.getHeader("YD_STK_BED_NO").getValue(ii));
				
				//--------------------------------------------------------------------------------------------------------
				//	저장위치 변경이력 등록
				//--------------------------------------------------------------------------------------------------------
				
				recHist.setField("YD_GP", 			szYD_GP);
				recHist.setField("STL_NO", 			szSTL_NO);
				recHist.setField("YD_DN_WR_LOC",	"");
				recHist.setField("YD_DN_WR_LAYER",	"");
				recHist.setField("YD_SCH_ST_GP", 	"B");				// 야드 스케줄기동 구분 (A:AUTO, M:MANUAL, B:BACK-UP)
				recHist.setField("MODIFIER", 		gdReq.getParam("userid"));
				recHist.setField("YD_SCH_CD",		"PXYD02MM");			// 야드스케줄 코드 - 저장위치삭제
				recHist.setField("YD_UP_WR_LOC",	sFromLoc + szYD_STK_BED_NO );		// 권상위치 (적치열+베드)
				recHist.setField("YD_UP_WR_LAYER",	szYD_STK_LYR_NO);		// 권상위치 (적치단)

				// 이력정보 남기기
				szRtnMsg = this.insYdWrkHistPosFix(recHist);
				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
			    	szMsg 	 = "[" + methodNm + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
					
					jrRtn.setField("RTN_MSG", szRtnMsg);
		    		return jrRtn;
				}
				
			}	    
			
			JDTORecord recL2Para = JDTORecordFactory.getInstance().create();
			
			if(rowCnt > 0) {
				
				// - 저장품 제원정보 야드L2전송
				recL2Para.setField("MSG_ID", 			"YDY2L002");                        // TC-CODE
				recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);	// 야드구분
				recL2Para.setField("YD_STK_COL_GP", 	sFromLoc);                         	// 야드적치열구분
				recL2Para.setField("YD_STK_BED_NO", 	"");    							// 야드적치BED번호
				recL2Para.setField("YD_INFO_SYNC_CD",   "3");								// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
				recL2Para.setField("STL_NO", 			"");	        					// 재료번호
	        	recL2Para.setField("MSG_GP", 			"D");	        					// 전문구분 - D:삭제
	        	recL2Para.setField("DEL_YN_CHECK",		"N");								// 삭제된 데이타도 조회하도록 처리
				szRtnMsg = ydDelegate.sendMsg(recL2Para);
				
				for (int ii = 0; ii < rowCnt; ii++) {
					szSTL_NO = commUtils.trim(gdReq.getHeader("STL_NO").getValue(ii));
			        if (ii > 0) {
			        	szArrStlNo = szArrStlNo + ";";
			        }
		        	szArrStlNo = szArrStlNo + szSTL_NO;
				}
				
		        recInTemp = JDTORecordFactory.getInstance().create();
		        recInTemp.setField("JMS_TC_CD", 		"YDPRJ011");
		        recInTemp.setField("YD_STK_COL_FR", 	sFromLoc);	// From적치열
		        recInTemp.setField("YD_STK_BED_FR", 	"");		// From적치BED
		        recInTemp.setField("YD_STK_COL_TO", 	"");	// TO적치열
		        recInTemp.setField("YD_STK_BED_TO", 	"");		// TO적치BED
		        recInTemp.setField("YD_EQP_WRK_SH", 	"");		// 야드설비작업매수
		        recInTemp.setField("ARR_STL_NO", 		szArrStlNo);

		        szRtnMsg = JPlateYdCommonUtils.sendL3YDPRJ011(recInTemp);
			}
			

			jrParam.setField("MODIFIER", 			gdReq.getParam("userid"));
			jrParam.setField("YD_START_LOC",		sFromLoc);		// 대차 출발 위치
			
	    	//-------------------------------------------------------------
	    	// 차량 출발 저장위치(베드) 비활성화 처리
			EJBConnector ejbConn = new EJBConnector("default", "JPlateYdTcarSchSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("disableToBed", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
	    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				
				szRtnMsg = "차량 출발 저장위치(베드) 비활성화 처리시 오류 발생 : " + szRtnMsg;
		    	szMsg 	 = "[" + methodNm + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
				
				jrRtn.setField("RTN_MSG", szRtnMsg);
	    		return jrRtn;
	    	}			
			
			//이전위치 차량 Clear
			jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
			jrParam.setField("YD_STK_COL_GP"		, sFromLoc );
			jrParam.setField("CAR_NO"				, gdReq.getParam("CAR_NO") );
			commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updCarNoClear", logId, methodNm, "차량번호 Clear ");
	    	
			
	    	// - 저장위치 제원정보 야드L2전송 (FROM:비활성화)
			recL2Para.setField("MSG_ID", 			"YDY2L001");
			recL2Para.setField("YD_INFO_SYNC_CD", 	"3");  // 1:동, 2:SPAN, 3:열, 4:BED
			recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);
			recL2Para.setField("YD_STK_COL_GP", 	sFromLoc);
			recL2Para.setField("YD_STK_BED_NO", 	"");
			szRtnMsg = ydDelegate.sendMsg(recL2Para);
			
			//--------------------------------------------------------------------------------------------------------
			//	차량의 저장위치 적치정보 초기화
			//--------------------------------------------------------------------------------------------------------
			jrParam.setField("MODIFIER", 		gdReq.getParam("userid"));
			jrParam.setField("YD_STK_COL_GP", 	sFromLoc);
			jrParam.setField("YD_STK_BED_NO", 	"");
			jrParam.setField("YD_STK_LYR_NO", 	"");
			jrParam.setField("OCPY_CHK_FLAG",	"N");
			intRtnVal = ydStkLyrDao.updYdStklyrClear(jrParam);
			
			jrRtn.setField("RTN_MSG", JPlateYdConst.RETN_CD_SUCCESS);			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of 차량포인트 초기화
	
	
	/**
	 * 사외이송백업처리 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord doTrSendOutSideBackup(GridData gdReq) throws DAOException {
		String methodNm = "사외이송백업처리[JPlateYDYDPJspSeEJB.doTrSendOutSideBackup] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String szSTL_NO = null;
		String szYD_STK_LYR_NO		= null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			
			String sFromLoc = commUtils.trim(gdReq.getParam("YD_TR_GP"));
			//String sToLoc	= commUtils.trim(gdReq.getParam("TO_TR_GP"));
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			//JDTORecord recL2Para = JDTORecordFactory.getInstance().create();;
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				szSTL_NO = commUtils.trim(gdReq.getHeader("STL_NO").getValue(ii));
				szYD_STK_LYR_NO = commUtils.trim(gdReq.getHeader("YD_STK_LYR_NO").getValue(ii));

				/*
				//01.이전위치 정보 Clear
				jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
				jrParam.setField("YD_STK_COL_GP"		, sFromLoc );
				jrParam.setField("YD_STK_BED_NO"		, "01" );
				jrParam.setField("STL_NO"				, szSTL_NO );
				commDao.update(jrParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updFromLocClear", logId, methodNm, "전위치 정보 Clear ");
				
	        	recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
	        	recL2Para.setField("YD_GP", 			"P");									// 야드구분
	        	recL2Para.setField("YD_STK_COL_GP", 	sFromLoc);                          	// 야드적치열구분
	        	recL2Para.setField("YD_STK_BED_NO", 	"01");    					// 야드적치BED번호
	        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드  [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
	        	recL2Para.setField("STL_NO", 			szSTL_NO);	        					// 재료번호
	        	recL2Para.setField("MSG_GP", 			"D");	        						// 전문구분 - D:삭제
	        	recL2Para.setField("DEL_YN_CHECK",		"N");									// 삭제된 데이타도 조회하도록 처리

	        	ydDelegate.sendMsg(recL2Para);
	        	*/
				
				jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
				jrParam.setField("STL_NO"				, szSTL_NO );
				
				this.delYdLocInfo2(jrParam);

			}

			//이전위치 차량 Clear
			jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
			jrParam.setField("YD_STK_COL_GP"		, sFromLoc );
			jrParam.setField("CAR_NO"				, gdReq.getParam("CAR_NO") );
			commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updCarNoClear", logId, methodNm, "차량번호 Clear ");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of 사외이송백업처리
	
	/**
	 *  작업예약등록(차량포인트 상차)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String insPtInWBook(JDTORecord[] inDto) throws DAOException {

		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();

		String szRtnMsg 		= JPlateYdConst.RETN_CD_SUCCESS;
		String szMethodName		= "insBsInWBook";
		String szOperationName 	= "작업예약등록(차량포인트 상차)";
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
				recPara.setField("YD_MAIN_WRK_GP", 	  	JPlateYdConst.YD_MAIN_WRK_GP_PT_IN);
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

				//---------------------------------------------------------------------------------------------
				//	L3화면을 통해 만들어진 작업예약, 작업지시 설정
				//---------------------------------------------------------------------------------------------
				recPara.setField("CARD_NO",	"L3");		
				
				//내부 Process 연결
				EJBConnector ejbConn = new EJBConnector("default", this);
				szRtnMsg = (String)ejbConn.trx("JPlateYdMvStkWrkSeEJB", "procPrepLotCompByCapaYdP", recPara);

			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION ["+szOperationName+"] 끝  .. 결과 :: " + szRtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of insPtInWBook

	/**
	 *  작업예약등록(차량포인트 하차)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String insPtOutWBook(JDTORecord[] inDto) throws DAOException {

		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();

		String szRtnMsg 		= JPlateYdConst.RETN_CD_SUCCESS;
		String szMethodName		= "insBsInWBook";
		String szOperationName 	= "작업예약등록(차량포인트 하차)";
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
				recPara.setField("YD_MAIN_WRK_GP", 	  	JPlateYdConst.YD_MAIN_WRK_GP_PT_OUT);
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

				//---------------------------------------------------------------------------------------------
				//	L3화면을 통해 만들어진 작업예약, 작업지시 설정
				//---------------------------------------------------------------------------------------------
				recPara.setField("CARD_NO",	"L3");		
				
				//내부 Process 연결
				EJBConnector ejbConn = new EJBConnector("default", this);
				szRtnMsg = (String)ejbConn.trx("JPlateYdMvStkWrkSeEJB", "procPrepLotCompByCapaYdP", recPara);

			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION ["+szOperationName+"] 끝  .. 결과 :: " + szRtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of insBsInWBook
	
	/**
	 *  상차완료처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String procPlateYdPtInCmplt(GridData gdReq) throws DAOException {

		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();

		String szRtnMsg 		= JPlateYdConst.RETN_CD_SUCCESS;
		String szMethodName		= "procPlateYdPtInCmplt";
		String szOperationName 	= "상차완료처리";
		String szLogMsg 		= "";
		JDTORecordSet rsResult = null;
		
		String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";

		
		
		try {
			szLogMsg = "JSP-SESSION ["+szOperationName+"] 시작 :: " + gdReq;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			
			JDTORecord jrParam = commUtils.getParam(logId, szMethodName, commUtils.trim(gdReq.getParam("userid")));
			
			String YD_GP=gdReq.getParam("YD_GP");//F
			String TO_DEST=gdReq.getParam("TO_DEST");//P,4,...
			
			szLogMsg = "JSP-SESSION ["+szOperationName+"] YD_GP: "+YD_GP+" TO_DEST:"+TO_DEST;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			
			if( (YD_GP.equals("P") && TO_DEST.equals("F"))  || (YD_GP.equals("F") && TO_DEST.equals("P"))){  //1후판->2후판 OR 2후판->1후판
				//상대야드 대기장에 차량정보 생성시켜준다.
				if(YD_GP.equals("F")){  //2후판->1후판
					//대차작업 작업예약이 남아 있으면 대차이동처리 백업 불가
					jrParam.setField("YD_GP", "P");  //1후판 내 빈 대기장 조회
					//1.빈 대기장 조회  
					/*
					SELECT YD_STK_COL_GP ,
					       (
					       	SELECT COUNT(*)
					       	FROM TB_YD_STKLYR tys 
					       	WHERE YD_STK_COL_GP =A.YD_STK_COL_GP 
					       	AND STL_NO IS NOT NULL       	
					       ) AS STL_CNT
					FROM TB_YD_STKCOL A
					WHERE 1=1
					AND YD_GP=:V_YD_GP
					AND YD_STK_COL_GP LIKE :V_YD_GP||'XTR%'
					AND CAR_NO IS NULL
					ORDER BY YD_STK_COL_GP 
				    */				
					
					rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
					//신규쿼리 개발					
		            rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getEmptyWaitingPoint", logId, szMethodName, "빈 대기장 조회");

					if (rsResult.size() <= 0) {
						szLogMsg = "조회된 빈대기장 목록이 없습니다.";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
						szRtnMsg="빈 대기장 검색 실패";
						
			    		return szRtnMsg;
					}
					String TO_YD_STK_COL_GP=rsResult.getRecord(0).getFieldString("YD_STK_COL_GP");
					szLogMsg = "조회된 빈대기장:"+TO_YD_STK_COL_GP;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
					
					
					jrParam.setField("YD_STK_COL_GP", gdReq.getParam("YD_TR_GP"));  //상차정보(상차재료, 상차재료위치) 조회
					//신규쿼리 개발
					rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getStlNoAndPos", logId, szMethodName, "상차정보 조회");						
					//2.재료번호가 있는 적치단 조회  입력 필요변수:gdReq.getParam("YD_TR_GP")
					/*
				    -- com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getStlNoAndPos
					SELECT YD_STK_COL_GP , YD_STK_BED_NO ,YD_STK_LYR_NO , STL_NO 
					FROM TB_YD_STKLYR tys 
					WHERE 1=1
					AND STL_NO IS NOT NULL
					AND YD_STK_LYR_MTL_STAT ='C'
					AND YD_STK_COL_GP =:V_YD_STK_COL_GP--'FCPT01'
					*/
					
					if (rsResult.size() <= 0) {
						szLogMsg = "조회된 상차대상 목록이 없습니다.";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
						
						szRtnMsg="상차대상 목록 검색 실패";
						
			    		return szRtnMsg;
					}
					
					jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
					jrParam.setField("CAR_NO"				, gdReq.getParam("CAR_NO") );
					jrParam.setField("YD_LOC_GP"			, "Y" );  //1/2후판간 이송인지 여부 라벨링
					jrParam.setField("YD_STK_COL_GP"		, TO_YD_STK_COL_GP );
					
					//기존쿼리 사용
					//commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updCarNoSet", logId, szMethodName, "하차지 차량번호 셋팅");					
					//신규쿼리 사용
					commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updCarNoSet2", logId, szMethodName, "하차지 차량번호,공장간이송여부 셋팅");
					
					//3.1 하차지 포인트에 차량번호 설정.
					/*
					-- com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updCarNoSet 
					UPDATE TB_YD_STKCOL
					   SET MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					      ,CAR_NO = :V_CAR_NO
					 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
					   AND YD_GP IN ('P','F')
					   AND DEL_YN = 'N'
					 */
					
					
					for(int i=0; i<rsResult.size(); i++){//상차대상 리스트 조회 후 하차지로 COPY
						rsResult.absolute(i+1);
						recPara=rsResult.getRecord();
						
		    			jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
		    			jrParam.setField("STL_NO"				, ydDaoUtils.paraRecChkNull(recPara, "STL_NO") );
		    			jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C" );
		    			jrParam.setField("YD_STK_COL_GP"		, TO_YD_STK_COL_GP );
		    			jrParam.setField("YD_STK_BED_NO"		, ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO") );
		    			jrParam.setField("YD_STK_LYR_NO"		, ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO") );
		    			commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat", logId, szMethodName, "적치열 UPDATE ");
					}
					
					//1.하차지 L2야드 위치 정보 전송(YDY2L002)  
					JDTORecord recL2Para = JDTORecordFactory.getInstance().create();
					
		        	for(int i=0; i<rsResult.size(); i++){//상차대상 리스트 조회 후 하차지로 COPY
		        		
					recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
		        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
		        	recL2Para.setField("YD_STK_COL_GP", 	TO_YD_STK_COL_GP);                       // 야드적치열구분
		        	recL2Para.setField("YD_STK_BED_NO", 	ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO"));    					// 야드적치BED번호
		        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
		        	recL2Para.setField("STL_NO", 			ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));	        					// 재료번호		        	
		        	szRtnMsg = ydDelegate.sendMsg(recL2Para);
		        	
		        	}
		        	
		        	
			    	
					
					
					
					//gdReq에서 대상열만 입력된 상차지에서 하차지포인트로 변경후 야드map update 수행
					//this.updTrStk(gdReq);
					
					
					//3.2에서 조회한 재료번호와 위치를 1에서 조회한 빈 대기장에 해당하는 적치단으로 copy해준다.
					/*
					2번에서 조회된 재료수만큼 LOOP돌면서
					적치열만 1에서 조회한 값으로 셋팅후 베드/단/재료번호/적치상태값 UPDATE(TB_YD_STKLYR)
					 */
				}
				else {  //1후판->2후판
					//대차작업 작업예약이 남아 있으면 대차이동처리 백업 불가
					jrParam.setField("YD_GP", "F");  //1후판 내 빈 대기장 조회
					//1.빈 대기장 조회  
					/*
					SELECT YD_STK_COL_GP ,
					       (
					       	SELECT COUNT(*)
					       	FROM TB_YD_STKLYR tys 
					       	WHERE YD_STK_COL_GP =A.YD_STK_COL_GP 
					       	AND STL_NO IS NOT NULL       	
					       ) AS STL_CNT
					FROM TB_YD_STKCOL A
					WHERE 1=1
					AND YD_GP=:V_YD_GP
					AND YD_STK_COL_GP LIKE :V_YD_GP||'XTR%'
					AND CAR_NO IS NULL
					ORDER BY YD_STK_COL_GP 
				    */				
					
					rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
					//신규쿼리 개발					
		            rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getEmptyWaitingPoint", logId, szMethodName, "빈 대기장 조회");

					if (rsResult.size() <= 0) {
						szLogMsg = "조회된 빈대기장 목록이 없습니다.";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
						szRtnMsg="빈 대기장 검색 실패";
						
			    		return szRtnMsg;
					}
					String TO_YD_STK_COL_GP=rsResult.getRecord(0).getFieldString("YD_STK_COL_GP");
					szLogMsg = "조회된 빈대기장:"+TO_YD_STK_COL_GP;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
					
					
					jrParam.setField("YD_STK_COL_GP", gdReq.getParam("YD_TR_GP"));  //상차정보(상차재료, 상차재료위치) 조회
					//신규쿼리 개발
					rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getStlNoAndPos", logId, szMethodName, "상차정보 조회");						
					//2.재료번호가 있는 적치단 조회  입력 필요변수:gdReq.getParam("YD_TR_GP")
					/*
				    -- com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getStlNoAndPos
					SELECT YD_STK_COL_GP , YD_STK_BED_NO ,YD_STK_LYR_NO , STL_NO 
					FROM TB_YD_STKLYR tys 
					WHERE 1=1
					AND STL_NO IS NOT NULL
					AND YD_STK_LYR_MTL_STAT ='C'
					AND YD_STK_COL_GP =:V_YD_STK_COL_GP--'FCPT01'
					*/
					
					if (rsResult.size() <= 0) {
						szLogMsg = "조회된 상차대상 목록이 없습니다.";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
						
						szRtnMsg="상차대상 목록 검색 실패";
						
			    		return szRtnMsg;
					}
					
					jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
					jrParam.setField("CAR_NO"				, gdReq.getParam("CAR_NO") );
					jrParam.setField("YD_LOC_GP"			, "Y" );  //1/2후판간 이송인지 여부 라벨링
					jrParam.setField("YD_STK_COL_GP"		, TO_YD_STK_COL_GP );
					
					//기존쿼리 사용
					//commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updCarNoSet", logId, szMethodName, "하차지 차량번호 셋팅");					
					//신규쿼리 사용
					commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updCarNoSet2", logId, szMethodName, "하차지 차량번호,공장간이송여부 셋팅");
					
					//3.1 하차지 포인트에 차량번호 설정.
					/*
					-- com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updCarNoSet 
					UPDATE TB_YD_STKCOL
					   SET MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					      ,CAR_NO = :V_CAR_NO
					 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
					   AND YD_GP IN ('P','F')
					   AND DEL_YN = 'N'
					 */
					
					
					for(int i=0; i<rsResult.size(); i++){//상차대상 리스트 조회 후 하차지로 COPY
						rsResult.absolute(i+1);
						recPara=rsResult.getRecord();
						
		    			jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
		    			jrParam.setField("STL_NO"				, ydDaoUtils.paraRecChkNull(recPara, "STL_NO") );
		    			jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C" );
		    			jrParam.setField("YD_STK_COL_GP"		, TO_YD_STK_COL_GP );
		    			jrParam.setField("YD_STK_BED_NO"		, ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO") );
		    			jrParam.setField("YD_STK_LYR_NO"		, ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO") );
		    			commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat", logId, szMethodName, "적치열 UPDATE ");
					}
					
					//1.하차지 L2야드 위치 정보 전송(YDY7L002)  
					JDTORecord recL2Para = JDTORecordFactory.getInstance().create();
					
		        	for(int i=0; i<rsResult.size(); i++){//상차대상 리스트 조회 후 하차지로 COPY
		        		
					recL2Para.setField("JMS_TC_CD", 		"YDY7L002");                            // TC-CODE
		        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);		// 야드구분
		        	recL2Para.setField("YD_STK_COL_GP", 	TO_YD_STK_COL_GP);                       // 야드적치열구분
		        	recL2Para.setField("YD_STK_BED_NO", 	ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO"));    					// 야드적치BED번호
		        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
		        	recL2Para.setField("STL_NO", 			ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));	        					// 재료번호		        	
		        	szRtnMsg = ydDelegate.sendMsg(recL2Para);
		        	
		        	}
				}
			}
			
			
			szLogMsg = "JSP-SESSION ["+szOperationName+"] 차량정보 클리어 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			
			jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
			jrParam.setField("YD_STK_COL_GP"		, gdReq.getParam("YD_TR_GP") );
			jrParam.setField("CAR_NO"				, gdReq.getParam("CAR_NO") );
			
			//기존쿼리 사용
			commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updCarNoClear", logId, szMethodName, "차량번호 Clear ");

			szLogMsg = "JSP-SESSION ["+szOperationName+"] 차량정보 클리어 완료";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			
			szLogMsg = "JSP-SESSION ["+szOperationName+"] 상차포인트 클리어 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			
			jrParam = commUtils.getParam(logId, szMethodName, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("YD_STK_LYR_ACT_STAT"		, "E" );
			jrParam.setField("STL_NO"		, "" );
			jrParam.setField("YD_STK_LYR_MTL_STAT"		, "E" );
			jrParam.setField("YD_STK_COL_GP"		, gdReq.getParam("YD_TR_GP") );
			
			//기존쿼리 사용
			commDao.update(jrParam, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrYdStkColGp", logId, szMethodName, "상차포인트 적치단 클리어");
			
			szLogMsg = "JSP-SESSION ["+szOperationName+"] 상차포인트 클리어 완료";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);			
			
			
			commUtils.printLog(logId, szMethodName, "S-");
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION ["+szOperationName+"] 끝  .. 결과 :: " + szRtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of procPlateYdPtInCmplt	
	
	/**
	 *  작업예약등록(대차포인트 상차)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String insTcInWBook(JDTORecord[] inDto) throws DAOException {

		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();

		String szRtnMsg 		= JPlateYdConst.RETN_CD_SUCCESS;
		String szMethodName		= "insBsInWBook";
		String szOperationName 	= "작업예약등록(대차포인트 상차)";
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
				recPara.setField("YD_MAIN_WRK_GP", 	  	JPlateYdConst.YD_MAIN_WRK_GP_MV);
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

				//---------------------------------------------------------------------------------------------
				//	L3화면을 통해 만들어진 작업예약, 작업지시 설정
				//---------------------------------------------------------------------------------------------
				recPara.setField("CARD_NO",	"L3");		
				
				//내부 Process 연결
				EJBConnector ejbConn = new EJBConnector("default", this);
				szRtnMsg = (String)ejbConn.trx("JPlateYdMvStkWrkSeEJB", "procPrepLotCompByCapaYdP", recPara);

			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION ["+szOperationName+"] 끝  .. 결과 :: " + szRtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of insTcInWBook	
	
	/**
	 *  작업예약등록(대차포인트 하차)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String insTcOutWBook(JDTORecord[] inDto) throws DAOException {

		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();

		String szRtnMsg 		= JPlateYdConst.RETN_CD_SUCCESS;
		String szMethodName		= "insTcInWBook";
		String szOperationName 	= "작업예약등록(대차포인트 하차)";
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
				recPara.setField("YD_MAIN_WRK_GP", 	  	JPlateYdConst.YD_MAIN_WRK_GP_MV);
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

				//---------------------------------------------------------------------------------------------
				//	L3화면을 통해 만들어진 작업예약, 작업지시 설정
				//---------------------------------------------------------------------------------------------
				recPara.setField("CARD_NO",	"L3");		
				
				//내부 Process 연결
				EJBConnector ejbConn = new EJBConnector("default", this);
				szRtnMsg = (String)ejbConn.trx("JPlateYdMvStkWrkSeEJB", "procPrepLotCompByCapaYdP", recPara);

			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION ["+szOperationName+"] 끝  .. 결과 :: " + szRtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of insTcInWBook
	
	/**
	 * 대차이동처리백업
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord doTcarSendBackup(GridData gdReq) throws DAOException {
		String methodNm = "대차이동처리백업처리[JPlateYDYDPJspSeEJB.doTcarSendBackup] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String szSTL_NO = null;
		String szYD_STK_BED_NO      = null;
		String szYD_STK_LYR_NO		= null;
		JDTORecordSet 	rsResult		= null;
		JDTORecord recInTemp        = null;
		
		JPlateYdCrnSchDAO	ydCrnSchDao	= new JPlateYdCrnSchDAO();
		
	    int 	intRtnVal 				= 0;
		
	    String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
	    String	szArrStlNo			= "";
	    
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String szYD_GP  = gdReq.getParam("YD_GP"); //야드구분
			String sFromLoc = commUtils.trim(gdReq.getParam("YD_TCLOC_GP")); //From 위치(동)
			String sToLoc	= commUtils.trim(gdReq.getParam("TO_TCLOC_GP")); //To 위치(동)
			String szCAR_NO = commUtils.trim(gdReq.getParam("CAR_NO")); //대차번호
			
			//대차작업 크레인 스케줄이 생성되어 있으면 대차이동처리 백업 불가
	    	String szYD_SCH_CD = szYD_GP + "_" + ydUtils.substr(sFromLoc, 2, 4) + "_M";

	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
	    	jrParam.setField("YD_SCH_CD", szYD_SCH_CD);
	    	intRtnVal = ydCrnSchDao.getByYdSchCd(jrParam, rsResult);
	    	if (intRtnVal > 0) {
		    	szRtnMsg = "크레인 작업지시가 존재하여 대차 이동처리 불가합니다! 작업을 완료하거나 남은 작업을 취소 한 뒤에 가능합니다.";
		    	szMsg 	 = "[" + methodNm + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
				
				jrRtn.setField("RTN_MSG", szRtnMsg);
	    		return jrRtn;
	    	}
	    	
	    	//대차작업 작업예약이 남아 있으면 대차이동처리 백업 불가
            rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdWrkbookByYdSchCd", logId, methodNm, "작업예약 정보 조회");

			if (rsResult.size() > 0) {
		    	szRtnMsg = "대차관련 작업예약 정보가 남아 있어 대차 이동처리 불가합니다! 작업을 완료하거나 남은 작업예약을 삭제 한 뒤에 가능합니다.";
		    	szMsg 	 = "[" + methodNm + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
				
				jrRtn.setField("RTN_MSG", szRtnMsg);
	    		return jrRtn;
			}
	    	
			
			jrParam.setField("YD_STK_COL_GP"	,sToLoc);
			JDTORecordSet getRecSet = commDao.select(jrParam,"com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getStlNoCnt", logId, methodNm, "TO위치가 비워있는지 체크");

			if (getRecSet.size() < 0) {
				szMsg="["+methodNm+"] TO위치 대차포인트가 조회 실패! ";
				commUtils.printLog(logId, methodNm,szMsg);
				throw new Exception(szMsg);
			}
			
			if(!"0".equals(getRecSet.getRecord(0).getFieldString("CNT"))) {
				szMsg="["+methodNm+"] TO위치 대차포인트가 비워 있지 않습니다! 작업 확인 후 다시 실행해 주세요! ";
				commUtils.printLog(logId, methodNm,szMsg);
				throw new Exception(szMsg);
			}
			
			//TO위치 대차 지정 (TB_YD_EQP 현재동변경);
			jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
			jrParam.setField("YD_CURR_BAY_GP"		, sToLoc.substring(1,2) );
			jrParam.setField("YD_EQP_ID"			, szCAR_NO );
			jrParam.setField("YD_GP"				, "P" );
			commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updTcarCurrBayGp", logId, methodNm, "대차 현재동 지정 ");
			
			
			jrParam.setField("YD_EQP_ID", 			szCAR_NO);
			jrParam.setField("YD_CURR_BAY_GP",		sToLoc.substring(1,2));
			jrParam.setField("MODIFIER", 			gdReq.getParam("userid"));
			jrParam.setField("YD_START_LOC",		sFromLoc);		// 대차 출발 위치
			jrParam.setField("YD_STOP_LOC",			sToLoc);		// 대차 도착 위치
			
	    	//-------------------------------------------------------------
	    	// 대차 도착 저장위치(베드) 활성화 처리
			EJBConnector ejbConn = new EJBConnector("default", "JPlateYdTcarSchSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("enableFromBed", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
	    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				
				szRtnMsg = "대차 도착 저장위치(베드) 활성화 처리시 오류 발생 : " + szRtnMsg;
		    	szMsg 	 = "[" + methodNm + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
				
				jrRtn.setField("RTN_MSG", szRtnMsg);
	    		return jrRtn;
	    	}

	    	//-------------------------------------------------------------
	    	// 대차 출발 저장위치(베드) 비활성화 처리
			szRtnMsg = (String)ejbConn.trx("disableToBed", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
	    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				
				szRtnMsg = "대차 출발 저장위치(베드) 비활성화 처리시 오류 발생 : " + szRtnMsg;
		    	szMsg 	 = "[" + methodNm + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
				
				jrRtn.setField("RTN_MSG", szRtnMsg);
	    		return jrRtn;
	    	}			

	    	//-------------------------------------------------------------
	    	// 야드  L2 Interface 처리
	    	//-------------------------------------------------------------
			JDTORecord recL2Para = JDTORecordFactory.getInstance().create();;
	    	
	    	// - 저장위치 제원정보 야드L2전송 (FROM:비활성화)
			recL2Para.setField("MSG_ID", 			"YDY2L001");
			recL2Para.setField("YD_INFO_SYNC_CD", "3");  // 1:동, 2:SPAN, 3:열, 4:BED
			recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);
			recL2Para.setField("YD_STK_COL_GP", 	sFromLoc);
			recL2Para.setField("YD_STK_BED_NO", 	"");
			szRtnMsg = ydDelegate.sendMsg(recL2Para);
			
	    	// - 저장위치 제원정보 야드L2전송 (TO:활성화)
			recL2Para.setField("MSG_ID", 			"YDY2L001");
			recL2Para.setField("YD_INFO_SYNC_CD", "3");  // 1:동, 2:SPAN, 3:열, 4:BED
			recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);
			recL2Para.setField("YD_STK_COL_GP", 	sToLoc);
			recL2Para.setField("YD_STK_BED_NO", 	"");
			szRtnMsg = ydDelegate.sendMsg(recL2Para);
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			if(rowCnt > 0) {
				
				// - 저장품 제원정보 야드L2전송
				recL2Para.setField("MSG_ID", 		"YDY2L002");                        // TC-CODE
				recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);	// 야드구분
				recL2Para.setField("YD_STK_COL_GP", 	sToLoc);                         	// 야드적치열구분
				recL2Para.setField("YD_STK_BED_NO", 	"");    							// 야드적치BED번호
				recL2Para.setField("YD_INFO_SYNC_CD",   "3");								// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
				recL2Para.setField("STL_NO", 			"");	        					// 재료번호
				szRtnMsg = ydDelegate.sendMsg(recL2Para);
				
				for (int ii = 0; ii < rowCnt; ii++) {
					szSTL_NO = commUtils.trim(gdReq.getHeader("STL_NO").getValue(ii));
			        if (ii > 0) {
			        	szArrStlNo = szArrStlNo + ";";
			        }
		        	szArrStlNo = szArrStlNo + szSTL_NO;
				}
				
		        //recInTemp = JDTORecordFactory.getInstance().create();
		        //recInTemp.setField("JMS_TC_CD", 			"YDPRJ011");
		        //recInTemp.setField("YD_STK_COL_FR", 	sFromLoc);	// From적치열
		        //recInTemp.setField("YD_STK_BED_FR", 	"");		// From적치BED
		        //recInTemp.setField("YD_STK_COL_TO", 	sToLoc);	// TO적치열
		        //recInTemp.setField("YD_STK_BED_TO", 	"");		// TO적치BED
		        //recInTemp.setField("YD_EQP_WRK_SH", 	"");		// 야드설비작업매수
		        //recInTemp.setField("ARR_STL_NO", 		szArrStlNo);

		        //szRtnMsg = JPlateYdCommonUtils.sendL3YDPRJ011(recInTemp);
			}
			
			
			jrRtn.setField("RTN_MSG", JPlateYdConst.RETN_CD_SUCCESS);
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of 대차도착처리백업	
	
	/**
	 * [1후판정정야드] 야드Map관리 열 수정(2)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String procUpdColInfo(JDTORecord [] inDto, String sBedUpdYN) throws DAOException {

		int 	intRtnVal 			= 0;
		String 	szMsg 				= "";
		String 	szMethodName		= "procUpdColInfo";
		String	szYdStkColGp		= "";
		String 	szYdGp 				= "";
		String 	szRtnValue 			= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szOperationName 	= "저장위치 좌표설정화면 열 수정(2)";
		String	szModifier			= "";
		String  szYD_STK_COL_GP;

		JPlateYdDelegate ydDelegate = new JPlateYdDelegate();

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JPlateYdStkColDAO ydStkcolDao = new JPlateYdStkColDAO();

    	String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
		
		try {

			szMsg = "[JSP Session : "+szOperationName+"] 야드Map관리 열 수정 >>>> 메소드 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//메뉴 페이지 목록을 수정한다.
			for (int ii=0; ii<inDto.length; ii++) {

				//수정할 항목 SETTING
				szModifier = ydDaoUtils.paraRecModifier(inDto[ii]);
				recPara.setField("MODIFIER", 				szModifier);

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
				
				//용도구분
				recPara.setField("PL_SHEAR_YD_GRP_GP", 		ydDaoUtils.paraRecChkNull(inDto[ii], "PL_SHEAR_YD_GRP_GP", ""));
				
				//길이Type
				recPara.setField("YD_STK_COL_BED_L_TP", 	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_BED_L_TP", ""));

				//적치열 구분 * 필수
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GP");
				recPara.setField("YD_STK_COL_GP", 			szYD_STK_COL_GP.substring(0,6));

				//intRtnVal = ydStkcolDao.updYdStkcol(recPara);
				intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdStkCol", logId, szOperationName, "야드Map관리 - 적치열 수정");

				if (intRtnVal < 0) {
					szMsg = "routine error!!!, ErrorCode:" + Integer.toString(intRtnVal);
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				} else if  (intRtnVal == 0) {
					szMsg = "[JSP Session : "+szOperationName+"] 데이터 없음 ";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				} // end of if


				if("Y".equals(sBedUpdYN)) {
					//화면에서 "열정보 수정시 BED좌표 동시 수정 설정" 체크박스에 체크했으면..
					//BED X,Y 도 UPDATE 한다.
					
					//길이Type
					recPara.setField("YD_STK_BED_TP", 	    ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_BED_L_TP", ""));
					
					//기준 X축
					recPara.setField("YD_STK_BED_XAXIS",	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_RULE_XAXIS", "0"));
					
					//기준Y축
					recPara.setField("YD_STK_BED_YAXIS",	ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_RULE_YAXIS", "0"));
					
					intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdStkBed", logId, szOperationName, "야드Map관리 - 적치열 수정 후 BED정보 수정");
				}
				
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

				recPara.setField("MSG_ID", 			"YDY2L001");
				recPara.setField("YD_INFO_SYNC_CD", "3");  //3 은 열정보 4는 베드 정보
				recPara.setField("YD_GP", 			szYdGp);
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GP");
				recPara.setField("YD_STK_COL_GP", 	szYD_STK_COL_GP.substring(0,6));
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
	 * 차상위 재료등록처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updTrStk(GridData gdReq) throws DAOException {
		String methodNm = "차상위 재료등록처리[JPlateYDYDPJspSeEJB.updTrStk] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String szSTL_NO;
			String szYD_STK_COL_GP;
			String szYD_STK_BED_NO;
			String szYD_STK_LYR_NO;
			String szMsg;
			
			int intRtnVal;
			
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("rsTemp");
			
			JPlateYdStockDAO 	ydStockDao		= new JPlateYdStockDAO();

			JDTORecord recL2Para = JDTORecordFactory.getInstance().create();;
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
            
          	for (int ii = 0; ii < rowCnt; ii++) {
          		
          		szSTL_NO 		= commUtils.trim(gdReq.getHeader("STL_NO").getValue(ii));
          		szYD_STK_COL_GP = commUtils.trim(gdReq.getHeader("YD_STK_COL_GP").getValue(ii));
          		szYD_STK_BED_NO = commUtils.trim(gdReq.getHeader("YD_STK_BED_NO").getValue(ii));
          		szYD_STK_LYR_NO = commUtils.trim(gdReq.getHeader("YD_STK_LYR_NO").getValue(ii));
          		
          		jrParam.setField("STL_NO",		szSTL_NO);			// 재료번호
          		jrParam.setField("YD_GP",		gdReq.getParam("YD_GP"));			// 야드구분
    			//intRtnVal = ydStockDao.getYdStockWithLoc(recPara, rsResult);
                rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdStockWithLoc2", logId, methodNm, "1후판정정야드재료 정보 조회");
          		
                if (rsResult.size() < 1) {
                	
    				// 기존 TB_YD_SHRSTOCK에 미존재시 등록처리함
    				jrParam.setField("REGISTER"	,	gdReq.getParam("userid"));			// 등록자
    				jrParam.setField("MODIFIER"	,	gdReq.getParam("userid"));			// 수정자
    				jrParam.setField("STL_NO"	, 	szSTL_NO);             	// 재료번호

        			intRtnVal = ydStockDao.insYdStockBookOut(jrParam);
    				if (intRtnVal <= 0) {
    					szMsg="["+methodNm+"] 야드저장품 INSERT 실패! ";
    					commUtils.printLog(logId, methodNm,szMsg);
    					throw new Exception(szMsg);
    				}                	
                }
                
    			jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
    			jrParam.setField("STL_NO"				, szSTL_NO );
    			jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C" );
    			jrParam.setField("YD_STK_COL_GP"		, szYD_STK_COL_GP );
    			jrParam.setField("YD_STK_BED_NO"		, szYD_STK_BED_NO );
    			jrParam.setField("YD_STK_LYR_NO"		, szYD_STK_LYR_NO );
    			commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat", logId, methodNm, "적치열 UPDATE ");
                
    			
	        	recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
	        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
	        	recL2Para.setField("YD_STK_COL_GP", 	szYD_STK_COL_GP);                       // 야드적치열구분
	        	recL2Para.setField("YD_STK_BED_NO", 	szYD_STK_BED_NO);    					// 야드적치BED번호
	        	recL2Para.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
	        	recL2Para.setField("STL_NO", 			szSTL_NO);	        					// 재료번호
    			
	        	ydDelegate.sendMsg(recL2Para);
          	}
			
				
				
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updTrStk
	
	/**
	 * 대차초기화
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord doTcarInit(GridData gdReq) throws DAOException {
		String methodNm = "대차초기화[JPlateYDYDPJspSeEJB.doTcarInit] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String szSTL_NO = null;
		String szYD_STK_BED_NO      = null;
		String szYD_STK_LYR_NO		= null;
		JDTORecordSet 	rsResult		= null;
		JDTORecord recInTemp        = null;
		JDTORecord recHist = null;
		
		JPlateYdCrnSchDAO	ydCrnSchDao	= new JPlateYdCrnSchDAO();
		JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();
		
	    int 	intRtnVal 				= 0;
		
	    String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;
	    String	szArrStlNo			= "";
	    
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String szYD_GP  = gdReq.getParam("YD_GP"); //야드구분
			String sFromLoc = commUtils.trim(gdReq.getParam("YD_TCLOC_GP")); //From 위치(동)
			String sToLoc	= commUtils.trim(gdReq.getParam("TO_TCLOC_GP")); //To 위치(동)
			String szCAR_NO = commUtils.trim(gdReq.getParam("CAR_NO")); //대차번호
			
			//대차작업 크레인 스케줄이 생성되어 있으면 대차이동처리 백업 불가
	    	String szYD_SCH_CD = szYD_GP + "_" + ydUtils.substr(sFromLoc, 2, 4) + "_M";

	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
	    	jrParam.setField("YD_SCH_CD", szYD_SCH_CD);
	    	intRtnVal = ydCrnSchDao.getByYdSchCd(jrParam, rsResult);
	    	if (intRtnVal > 0) {
		    	szRtnMsg = "크레인 작업지시가 존재하여 초기화 처리 불가합니다.!";
		    	szMsg 	 = "[" + methodNm + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
				
				jrRtn.setField("RTN_MSG", szRtnMsg);
	    		return jrRtn;
	    	}
			
	    	recHist = JDTORecordFactory.getInstance().create();
	    	
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
	    	
			for (int ii = 0; ii < rowCnt; ii++) {
				
				szSTL_NO = commUtils.trim(gdReq.getHeader("STL_NO").getValue(ii));
				szYD_STK_LYR_NO = commUtils.trim(gdReq.getHeader("YD_STK_LYR_NO").getValue(ii));
				szYD_STK_BED_NO = commUtils.trim(gdReq.getHeader("YD_STK_BED_NO").getValue(ii));
				
				//--------------------------------------------------------------------------------------------------------
				//	저장위치 변경이력 등록
				//--------------------------------------------------------------------------------------------------------
				
				recHist.setField("YD_GP", 			szYD_GP);
				recHist.setField("STL_NO", 			szSTL_NO);
				recHist.setField("YD_DN_WR_LOC",	"");
				recHist.setField("YD_DN_WR_LAYER",	"");
				recHist.setField("YD_SCH_ST_GP", 	"B");				// 야드 스케줄기동 구분 (A:AUTO, M:MANUAL, B:BACK-UP)
				recHist.setField("MODIFIER", 		gdReq.getParam("userid"));
				recHist.setField("YD_SCH_CD",		"PXYD04MM");			// 야드스케줄 코드 - 대차초기화
				recHist.setField("YD_UP_WR_LOC",	sFromLoc + szYD_STK_BED_NO );		// 권상위치 (적치열+베드)
				recHist.setField("YD_UP_WR_LAYER",	szYD_STK_LYR_NO);		// 권상위치 (적치단)

				// 이력정보 남기기
				szRtnMsg = this.insYdWrkHistPosFix(recHist);
				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
			    	szMsg 	 = "[" + methodNm + "] " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
					
					jrRtn.setField("RTN_MSG", szRtnMsg);
		    		return jrRtn;
				}
				
			}

	    	//-------------------------------------------------------------
	    	// 야드  L2 Interface 처리
	    	//-------------------------------------------------------------
			JDTORecord recL2Para = JDTORecordFactory.getInstance().create();;
	    	
			if(rowCnt > 0) {
				
				// - 저장품 제원정보 야드L2전송
				recL2Para.setField("MSG_ID", 			"YDY2L002");                        // TC-CODE
				recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);	// 야드구분
				recL2Para.setField("YD_STK_COL_GP", 	sFromLoc);                         	// 야드적치열구분
				recL2Para.setField("YD_STK_BED_NO", 	"");    							// 야드적치BED번호
				recL2Para.setField("YD_INFO_SYNC_CD",   "3");								// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
				recL2Para.setField("STL_NO", 			"");	        					// 재료번호
	        	recL2Para.setField("MSG_GP", 			"D");	        					// 전문구분 - D:삭제
	        	recL2Para.setField("DEL_YN_CHECK",		"N");								// 삭제된 데이타도 조회하도록 처리
				
				szRtnMsg = ydDelegate.sendMsg(recL2Para);
				
				for (int ii = 0; ii < rowCnt; ii++) {
					szSTL_NO = commUtils.trim(gdReq.getHeader("STL_NO").getValue(ii));
			        if (ii > 0) {
			        	szArrStlNo = szArrStlNo + ";";
			        }
		        	szArrStlNo = szArrStlNo + szSTL_NO;
				}
				
		        recInTemp = JDTORecordFactory.getInstance().create();
		        recInTemp.setField("JMS_TC_CD", 			"YDPRJ011");
		        recInTemp.setField("YD_STK_COL_FR", 	sFromLoc);	// From적치열
		        recInTemp.setField("YD_STK_BED_FR", 	"");		// From적치BED
		        recInTemp.setField("YD_STK_COL_TO", 	"");		// TO적치열
		        recInTemp.setField("YD_STK_BED_TO", 	"");		// TO적치BED
		        recInTemp.setField("YD_EQP_WRK_SH", 	"");		// 야드설비작업매수
		        recInTemp.setField("ARR_STL_NO", 		szArrStlNo);

		        szRtnMsg = JPlateYdCommonUtils.sendL3YDPRJ011(recInTemp);
			}
			
			
			//(TB_YD_EQP 현재동변경);
			jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
			jrParam.setField("YD_CURR_BAY_GP"		, sFromLoc.substring(1,2) );
			jrParam.setField("YD_EQP_ID"			, szCAR_NO );
			jrParam.setField("YD_GP"				, szYD_GP );
			commDao.update(jrParam, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updTcarCurrBayGp", logId, methodNm, "대차 현재동 지정 ");
			
			
			jrParam.setField("YD_EQP_ID", 			szCAR_NO);
			jrParam.setField("YD_CURR_BAY_GP",		sFromLoc.substring(1,2));
			jrParam.setField("MODIFIER", 			gdReq.getParam("userid"));
			jrParam.setField("YD_START_LOC",		sFromLoc);		// 대차 출발 위치
			jrParam.setField("YD_STOP_LOC",			sFromLoc);		// 대차 도착 위치
			
	    	//-------------------------------------------------------------
	    	// 대차 도착 저장위치(베드) 활성화 처리
			EJBConnector ejbConn = new EJBConnector("default", "JPlateYdTcarSchSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("enableFromBed2", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
	    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				
				szRtnMsg = "대차 도착 저장위치(베드) 활성화 처리시 오류 발생 : " + szRtnMsg;
		    	szMsg 	 = "[" + methodNm + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
				
				jrRtn.setField("RTN_MSG", szRtnMsg);
	    		return jrRtn;
	    	}

			jrParam.setField("YD_START_LOC",		sToLoc);		// 대차 출발 위치
			jrParam.setField("YD_STOP_LOC",			sFromLoc);		// 대차 도착 위치
	    	
	    	//-------------------------------------------------------------
	    	// 대차 출발 저장위치(베드) 비활성화 처리
			szRtnMsg = (String)ejbConn.trx("disableToBed", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
	    	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				
				szRtnMsg = "대차 출발 저장위치(베드) 비활성화 처리시 오류 발생 : " + szRtnMsg;
		    	szMsg 	 = "[" + methodNm + "] " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, methodNm, szMsg, JPlateYdConst.ERROR);
				
				jrRtn.setField("RTN_MSG", szRtnMsg);
	    		return jrRtn;
	    	}			

	    	
	    	// - 저장위치 제원정보 야드L2전송 (비활성화)
			recL2Para.setField("MSG_ID", 			"YDY2L001");
			recL2Para.setField("YD_INFO_SYNC_CD", "3");  // 1:동, 2:SPAN, 3:열, 4:BED
			recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);
			recL2Para.setField("YD_STK_COL_GP", 	sToLoc);
			recL2Para.setField("YD_STK_BED_NO", 	"");
			szRtnMsg = ydDelegate.sendMsg(recL2Para);
			
	    	// - 저장위치 제원정보 야드L2전송 (활성화)
			recL2Para.setField("MSG_ID", 			"YDY2L001");
			recL2Para.setField("YD_INFO_SYNC_CD", "3");  // 1:동, 2:SPAN, 3:열, 4:BED
			recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);
			recL2Para.setField("YD_STK_COL_GP", 	sFromLoc);
			recL2Para.setField("YD_STK_BED_NO", 	"");
			szRtnMsg = ydDelegate.sendMsg(recL2Para);
			

			
			
			//--------------------------------------------------------------------------------------------------------
			//	대차의 저장위치 적치정보 초기화
			//--------------------------------------------------------------------------------------------------------
			jrParam.setField("MODIFIER", 		gdReq.getParam("userid"));
			jrParam.setField("YD_STK_COL_GP", 	sFromLoc);
			jrParam.setField("YD_STK_BED_NO", 	"");
			jrParam.setField("YD_STK_LYR_NO", 	"");
			jrParam.setField("OCPY_CHK_FLAG",	"N");
			intRtnVal = ydStkLyrDao.updYdStklyrClear(jrParam);
	    	
			
			jrRtn.setField("RTN_MSG", JPlateYdConst.RETN_CD_SUCCESS);
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of 대차 초기화	
		
	
	/**
	 * [1후판정정야드] 저장위치별 정보 조회화면 : Book-Out 실적 백업
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String sendP2P3BookOut(JDTORecord [] inDto, String logId) throws DAOException {
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.22 argument에 logId 추가		
//		      #2 열처리 YDP8L501(Bookin/out complete) 송신 관련 추가
//		public String sendP2P3BookOut(JDTORecord [] inDto) throws DAOException {
//-------------------------------------------------------------------------------------------------------------------------

		String	szRtnMsg		= "";
		String 	szMsg        	= "";
		String 	szMethodName	= "sendP2P3BookOut";
		String 	szOperationName	= "Book-Out 실적 백업";

		String	szStlNo			= "";

		String	szYdStkColGp	= "";
		String	szOldYdStkColGp	= "";
		String	szYdStkBedNo	= "";
		String	szArrStlNo		= "";			// L3 전송용 재료번호 보관

		int		iSendCnt		= 0;

		JDTORecord recL2Para	= null;
		String  szRtLoc			= null;


//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.22 로그 개선 
		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                    // log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {

			szMsg = "JSP-SESSION [" + szOperationName + "] 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			
            recL2Para = JDTORecordFactory.getInstance().create();
			
            szMsg    = "[" + szOperationName + "] 저장위치별 정보 조회화면 : Book-Out 실적 백업 ---- START ";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
            
			for (int ii=0; ii<inDto.length; ii++) {
				
				szStlNo		= ydDaoUtils.paraRecChkNull(inDto[ii], "STL_NO");
				szRtLoc		= ydDaoUtils.paraRecChkNull(inDto[ii], "RT_LOC");
				

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.22 #2 열처리 YDP8L501(Bookin/out complete) 송신 추가 
//			  저장위치 BOOK-OUT - PART31, PART32, PART34, PART35
//-------------------------------------------------------------------------------------------------------------------------
	        	szMsg = "[" + szOperationName + "] 저장위치 [" + szRtLoc + "]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

    	        if (   "PART31".equals(ydUtils.substr(szRtLoc, 0, 6))			// A동 #2 열처리 입측 저장위치(0031N)
        	        || "PART32".equals(ydUtils.substr(szRtLoc, 0, 6))			// A동 #2 열처리 입측 저장위치(0032N)	
        	        || "PART34".equals(ydUtils.substr(szRtLoc, 0, 6))			// A동 #2 열처리 출측 저장위치(0034N)
        	        || "PART35".equals(ydUtils.substr(szRtLoc, 0, 6))  			// A동 #2 열처리 출측 저장위치(0035N)
        	        || "PFRT21".equals(ydUtils.substr(szRtLoc, 0, 6))			// A동 #2 쇼트 입측 저장위치(0021N)
        	        || "PART23".equals(ydUtils.substr(szRtLoc, 0, 6))  			// A동 #2 쇼트 출측 저장위치(0023N)
    	           ) {
    	        	
					recL2Para.setField("MSG_ID", 		    "YDP8L501"						);
					recL2Para.setField("STL_NO",			szStlNo							);		// 재료번호
					recL2Para.setField("OPERATION_TYPE",	"2"								);		// 1:Book In, 2:Book Out
					recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szRtLoc, 0, 6)	);		// FROM위치
					recL2Para.setField("YD_STK_BED_NO", 	"01"							);    	// 야드적치BED번호
					recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szRtLoc, 1, 1) + "CR" + ydUtils.substr(szRtLoc, 1, 1) + "1");   
        	        
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.22 recL2Para에 logId 추가 
					recL2Para.setField("LOG_ID", 			logId 									);      // logId
//-------------------------------------------------------------------------------------------------------------------------
    	        
					szRtnMsg = ydDelegate.sendMsg(recL2Para);
       	        	
	        	} else if("PB".equals(ydUtils.substr(szRtLoc, 0, 2))||szRtLoc.startsWith("PART13")||szRtLoc.startsWith("PART9")||szRtLoc.startsWith("PCRT40")) {
					recL2Para.setField("MSG_ID", 		    "YDP3L501V2"					);
					recL2Para.setField("STL_NO",			szStlNo							);		// 재료번호
					recL2Para.setField("OPERATION_TYPE",	"2"								);		// 1:Book In, 2:Book Out
					recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szRtLoc, 0, 6)	);		// FROM위치
					recL2Para.setField("YD_STK_BED_NO", 	"01"							);    	// 야드적치BED번호
					recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szRtLoc, 1, 1) + "CR" + ydUtils.substr(szRtLoc, 1, 1) + "1");   
					szRtnMsg = ydDelegate.sendMsg(recL2Para);

				} else {
					
					szRtnMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("2", szStlNo, szRtLoc,"P" + ydUtils.substr(szRtLoc, 1, 1) + "CR" + ydUtils.substr(szRtLoc, 1, 1) + "1");

				}
			}
			

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION [" + szOperationName + "] 끝";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of sendP2P3BookOut
	
	/**
	 * [1후판정정야드] 저장위치별 정보 조회화면 : Book-In 실적 백업
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String sendP2P3BookIn(JDTORecord [] inDto, String logId) throws DAOException {
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.22 argument에 logId 추가		
//		 	  #2 열처리 YDP8L501(Bookin/out complete) 송신 관련 추가
//	public String sendP2P3BookIn(JDTORecord [] inDto) throws DAOException {
//-------------------------------------------------------------------------------------------------------------------------

		String	szRtnMsg		= "";
		String 	szMsg        	= "";
		String 	szMethodName	= "sendP2P3BookIn";
		String 	szOperationName	= "Book-In 실적 백업";

		String	szStlNo			= "";

		String	szYdStkColGp	= "";
		String	szYdStkBedNo	= "";
		String  szYdStkLyrNo	= "";
		String  szModifier;
		
		String	szOldYdStkColGp	= "";
		String	szArrStlNo		= "";			// L3 전송용 재료번호 보관

		int		iSendCnt		= 0;
		int     intRtnVal      	= 0;

		JDTORecord recL2Para	= null;
		JDTORecord recL2Para2	= null;
		JDTORecord recL3Para	= null;
		JDTORecord recHist		= null;
		JDTORecord recPara		= null;
		String	szRtLoc			= null;
				
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.22 logId 개선 
		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                    // log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		JPlateYdStkLyrDAO 	ydStkLyrDao 	= new JPlateYdStkLyrDAO();
		JPlateYdStockDAO 	ydStockDao 		= new JPlateYdStockDAO();
		
		try {

			szMsg = "JSP-SESSION [" + szOperationName + "] 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			
            recL3Para 	= JDTORecordFactory.getInstance().create();
            recL2Para 	= JDTORecordFactory.getInstance().create();
            recL2Para2 	= JDTORecordFactory.getInstance().create();
			recHist 	= JDTORecordFactory.getInstance().create();
			recPara   	= JDTORecordFactory.getInstance().create();
			
            szMsg    = "[" + szOperationName + "] 저장위치별 정보 조회화면 : Book-In 실적 백업 ---- START ";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
            
			for (int ii=0; ii<inDto.length; ii++) {
				
				szStlNo			= ydDaoUtils.paraRecChkNull(inDto[ii], "STL_NO"			);
				szRtLoc			= ydDaoUtils.paraRecChkNull(inDto[ii], "RT_LOC"			);
				szYdStkColGp 	= ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GP"	);
				szYdStkBedNo	= ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_NO"	);
				szYdStkLyrNo	= ydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_LYR_NO"	);
				szModifier		= ydDaoUtils.paraRecChkNull(inDto[ii], "YD_USER_ID"		);
				
				
				//-----------------------------------------------------------------------------------------
				//이력정보 남기기
				recHist.setField("YD_GP", 			szYdStkColGp.substring(0,1)	);
				recHist.setField("STL_NO", 			szStlNo						);
				recHist.setField("YD_DN_WR_LOC",	""							);
				recHist.setField("YD_DN_WR_LAYER",	""							);
				recHist.setField("YD_SCH_ST_GP", 	"B"							);	// B : Backup
				recHist.setField("MODIFIER", 		szModifier					);
				recHist.setField("YD_SCH_CD",		""							);	// 야드스케줄 코드
				recHist.setField("YD_UP_WR_LOC",	szYdStkColGp + szYdStkBedNo	);	// 권상위치 (적치열+베드)
				recHist.setField("YD_UP_WR_LAYER",	szYdStkLyrNo				);	// 권상위치 (적치단)


//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.22 recHist에 logId 추가 
				recHist.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
				
				szRtnMsg = this.insYdWrkHistPosFix(recHist);
				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					szMsg = "JSP-SESSION " + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}

				
				//-----------------------------------------------------------------------------------------
				// 적치단 정보 CLEAR
				recPara.setField("MODIFIER", 			szModifier		);
				recPara.setField("STL_NO", 				""				);
				recPara.setField("YD_STK_LYR_MTL_STAT", "E"				);
				recPara.setField("YD_STK_COL_GP", 		szYdStkColGp	);
				recPara.setField("YD_STK_BED_NO", 		szYdStkBedNo	);
				recPara.setField("YD_STK_LYR_NO", 		szYdStkLyrNo	);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.22 recPara에 logId 추가 
				recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
								
				intRtnVal = ydStkLyrDao.updYdStklyrStat(recPara);		// intGp == 0
				if (intRtnVal <= 0) {
					szRtnMsg = "적치단 정보 CLEAR 실패 하였습니다" + Integer.toString(intRtnVal);
					szMsg      = "JSP-SESSION " + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
				

				//-----------------------------------------------------------------------------------------
				//정정야드저장품 수정
				recPara.setField("MODIFIER", 		szModifier	);
				recPara.setField("YD_SCH_CD", 		""			);
				recPara.setField("YD_WBOOK_ID",		""			);
				recPara.setField("YD_STK_COL_GP", 	""			);
				recPara.setField("YD_STK_BED_NO", 	""			);
				recPara.setField("STL_NO", 			szStlNo		);

                // 야드재료 저장위치 정보 UPDATE
                intRtnVal = ydStockDao.updYdStkColInfo(recPara);
                if (intRtnVal <= 0) {
                	szRtnMsg = "야드재료 저장위치 정보 UPDATE 실패 하였습니다" + Integer.toString(intRtnVal);
                    szMsg      = "JSP-SESSION " + szOperationName + "] " + szRtnMsg;
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                    return szRtnMsg;
                }     
                
                
                //---------------------------------------------------
                // 조업L3 전문송신 (후판조업 저장위치변경정보 전송  - YDPRJ011)
                //---------------------------------------------------
                szMsg = "[ " + szOperationName + "] 조업L3 전문송신 START";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                recL3Para.setField("MSG_ID", 			"YDPRJ011"		);
                recL3Para.setField("YD_STK_COL_FR", 	szYdStkColGp	);		// From적치열
                recL3Para.setField("YD_STK_BED_FR", 	szYdStkBedNo	);		// From적치BED
                recL3Para.setField("YD_STK_COL_TO", 	""				);		// TO적치열
                recL3Para.setField("YD_STK_BED_TO", 	""				);		// TO적치BED
                recL3Para.setField("YD_EQP_WRK_SH", 	"1"				);		// 야드설비작업매수
                recL3Para.setField("ARR_STL_NO", 		szStlNo			);

                szRtnMsg = ydDelegate.sendMsg(recL3Para);

                szMsg = "[ " + szOperationName + "] 조업L3 전문송신 END >>>> " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId); 
                
                //---------------------------------------------------
                // L2 Book-In 실적 전문송신
                //---------------------------------------------------
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.22 #2 열처리 YDP8L501(Bookin/out complete) 송신 추가 
//            #2 열처리 저장위치 BOOK-IN - PART31, PART32, PART34, PART35
//-------------------------------------------------------------------------------------------------------------------------
 	        	szMsg = "[" + szOperationName + "] BOOK-IN 위치 [" + szRtLoc + "]";
 				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

     	        if (   "PART31".equals(ydUtils.substr(szRtLoc, 0, 6))			// A동 #2 열처리 입측 존(0031N)
         	        || "PART32".equals(ydUtils.substr(szRtLoc, 0, 6))			// A동 #2 열처리 입측 존(0032N)	
         	        || "PART34".equals(ydUtils.substr(szRtLoc, 0, 6))			// A동 #2 열처리 출측 존(0034N)
         	        || "PART35".equals(ydUtils.substr(szRtLoc, 0, 6))  			// A동 #2 열처리 출측 존(0035N)
         	        || "PFRT21".equals(ydUtils.substr(szRtLoc, 0, 6))			// A동 #2 쇼트 입측 저장위치(0021N)
       	            || "PART23".equals(ydUtils.substr(szRtLoc, 0, 6))  			// A동 #2 쇼트 출측 저장위치(0023N)
     	           ) {
     	        	
					recL2Para.setField("MSG_ID", 		    "YDP8L501"						);
					recL2Para.setField("STL_NO",			szStlNo							);		// 재료번호
					recL2Para.setField("OPERATION_TYPE",	"1"								);		// 1:Book In, 2:Book Out
					recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szRtLoc, 0, 6)	);		// FROM위치
					recL2Para.setField("YD_STK_BED_NO", 	"01"							);    	// 야드적치BED번호
					recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szRtLoc, 1, 1) + "CR" + ydUtils.substr(szRtLoc, 1, 1) + "1");   
         	        
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.22 recL2Para에 logId 추가 
					recL2Para.setField("LOG_ID", 			logId 									);      // logId
//-------------------------------------------------------------------------------------------------------------------------
     	        
					szRtnMsg = ydDelegate.sendMsg(recL2Para);
        	        	
 	        	} else if("PB".equals(ydUtils.substr(szRtLoc, 0, 2))||szRtLoc.startsWith("PART13")||szRtLoc.startsWith("PART9")||szRtLoc.startsWith("PCRT40")) {
					recL2Para.setField("MSG_ID", 		    "YDP3L501V2"					);
					recL2Para.setField("STL_NO",			szStlNo							);		// 재료번호
					recL2Para.setField("OPERATION_TYPE",	"1"								);		// 1:Book In, 2:Book Out
					recL2Para.setField("YD_STK_COL_GP",		ydUtils.substr(szRtLoc, 0, 6)	);		// FROM위치
					recL2Para.setField("YD_STK_BED_NO", 	"01"							);    	// 야드적치BED번호
					recL2Para.setField("YD_EQP_ID", 	    "P" + ydUtils.substr(szRtLoc, 1, 1) + "CR" + ydUtils.substr(szRtLoc, 1, 1) + "1");   
         	        
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.22 recL2Para에 logId 추가 
					recL2Para.setField("LOG_ID", 			logId 									);      // logId
//-------------------------------------------------------------------------------------------------------------------------
     	        
					szRtnMsg = ydDelegate.sendMsg(recL2Para);

				} else {
					
					szRtnMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("2", szStlNo, szRtLoc,"P" + ydUtils.substr(szRtLoc, 1, 1) + "CR" + ydUtils.substr(szRtLoc, 1, 1) + "1");

				}
				
                //---------------------------------------------------
                // L2 YDY2L002 전문송신
                //---------------------------------------------------
	        	recL2Para2.setField("JMS_TC_CD", 		"YDY2L002"							);		// TC-CODE
	        	recL2Para2.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD	);		// 야드구분
	        	recL2Para2.setField("YD_STK_COL_GP", 	szYdStkColGp						);		// 야드적치열구분
	        	recL2Para2.setField("YD_STK_BED_NO", 	szYdStkBedNo						);		// 야드적치BED번호
	        	recL2Para2.setField("YD_INFO_SYNC_CD", 	"5"									);		// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
	        	recL2Para2.setField("STL_NO", 			szStlNo								);		// 재료번호
	        	// BOOK-IN(TO위치가RT) 일때 저장품제원 삭제
	        	recL2Para2.setField("MSG_GP", 		"D"										);		// 전문구분
     	        
//-------------------------------------------------------------------------------------------------------------------------
//2024.11.22 recL2Para에 logId 추가 
				recL2Para.setField("LOG_ID", 			logId 									);      // logId
//-------------------------------------------------------------------------------------------------------------------------
 	        
	        	szRtnMsg = ydDelegate.sendMsg(recL2Para2);

			}
			

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szMsg = "JSP-SESSION [" + szOperationName + "] 끝";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of sendP2P3BookIn	
	
	/**
	 * [1후판정정야드] 저장위치수정 List (보수장,가스장,C동 냉각대)
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
		String 	szOperationName	= "저장위치수정 LIST (보수장,가스장,C동 냉각대)";

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
				szYdGp		 = ydDaoUtils.paraRecChkNull(inDto[0], "YD_GP", JPlateYdConst.YD_GP_P_PLATE_YARD);
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
						recPara.setField("YD_SCH_CD",			"PXYD03MM");	// 스케쥴코드 : 저장위치 목록 수정
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
								recPara.setField("YD_SCH_CD",		"PXYD03MM");
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
								// 4.1.4. 야드L2 저장품제원 정보 송신 [YDY2L002]
								// ------------------------------------------------------------------------
					            szMsg = "["+szOperationName+"] 야드L2 저장위치삭제 정보 전송 ---- START";
					            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					        	recL2Para = JDTORecordFactory.getInstance().create();
					        	recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
					        	recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
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
								// 4.1.5. 후판조업 저장품제원 정보 송신 [YDPRJ011]
								// ------------------------------------------------------------------------
					            szMsg = "["+szOperationName+"] 1후판조업 저장위치 삭제 정보 전송 ---- START";
					            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					            recL3Para = JDTORecordFactory.getInstance().create();
					            recL3Para.setField("MSG_ID", 			"YDPRJ011");
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
				recL2Para.setField("JMS_TC_CD", 		"YDY2L002");                            // TC-CODE
				recL2Para.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
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
	            recL3Para.setField("MSG_ID", 			"YDPRJ011");
	            recL3Para.setField("YD_STK_COL_FR", 	"");									// From적치열
	            recL3Para.setField("YD_STK_BED_FR", 	"");									// From적치BED
	            recL3Para.setField("YD_STK_COL_TO", 	szYdStkColGp);							// TO적치열
	            recL3Para.setField("YD_STK_BED_TO", 	"01");									// TO적치BED
	            recL3Para.setField("YD_EQP_WRK_SH", 	"");									// 야드설비작업매수
	            recL3Para.setField("ARR_STL_NO", 		szArrStlNo);

	        //	szRtnMsg = ydDelegate.sendMsg(recL3Para);
	            szRtnMsg = JPlateYdCommonUtils.sendL3YDPRJ011(recL3Para);

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
}