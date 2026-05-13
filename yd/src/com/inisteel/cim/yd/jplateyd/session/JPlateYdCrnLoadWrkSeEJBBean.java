/*
 * @(#) 2후판정정야드 권상실적처리 Session EJB
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/26
 *
 * @description		권하실적처리 Session EJB
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/26   김현우      김현우       최초작성 
 */

package com.inisteel.cim.yd.jplateyd.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.DateHelper;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.yd.jplateyd.delegate.JPlateYdDelegate;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarFtmvMtlDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdCrnSchUtil;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdCommonUtils;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 * 권상실적처리 Session EJB
 *
 * @ejb.bean name="JPlateYdCrnLoadWrkSeEJB" jndi-name="JPlateYdCrnLoadWrkSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class JPlateYdCrnLoadWrkSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;

	// Session Name
	private final String SZ_SESSION_NAME = getClass().getName();

	private JPlateYdUtils 		ydUtils     = new JPlateYdUtils();
	private JPlateYdDaoUtils	ydDaoUtils  = new JPlateYdDaoUtils();
	private JPlateYdDelegate 	ydDelegate  = new JPlateYdDelegate();

	private EJBConnector ydEjbCon = new EJBConnector("default", this);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 1후판 정정 로그 관련 야드공통 UTIL 
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
     * 오퍼레이션명 : 2후판정정 크레인권상실적등록 (Y7YDL008)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
     */
    public String procY7CrnUpWr(JDTORecord msgRecord)throws DAOException  {

    	JPlateYdCrnSchDAO 	ydCrnSchDao = new JPlateYdCrnSchDAO();
    	JPlateYdEqpDAO   	ydEqpDao    = new JPlateYdEqpDAO();
    	JPlateYdCrnSchDAO	ydCrnschDao	= new JPlateYdCrnSchDAO();

    	//업데이트 할 크레인 스케줄 Data 항목 set
        JDTORecord setCrnschRec	= null;

        //파라미터 null check 후 받아온 Data
        JDTORecord getParamRec	= JDTORecordFactory.getInstance().create();

        //스케줄Table의 컬럼을 저장하기위해 생성
        JDTORecordSet getRecSet = null;
        //레코드 셋의 레코드값을 받음
        JDTORecord getRecord 	= JDTORecordFactory.getInstance().create();

        JDTORecord recSendMsg 	= null;
        JDTORecord recInTemp    = null;

        int intRtnVal 			= 0;

        String 	szWbookId		= "";
		String 	szMsg			= "";
		String 	szRtnMsg		= JPlateYdConst.RETN_CD_SUCCESS;
        String 	szSendMsg       = "";
		String 	szMethodName	= "procY7CrnUpWr";
		String 	szOperationName	= "2후판정정야드 L2 크레인권상실적등록";

		String 	szTcarEqpId 	= "";
		String	szYdUpWrLoc		= "";			//권상위치
		String 	szYdDnWoLoc 	= null;			//권하지시위치
		String 	szModifier		= null;			//수정자
		String	szYdSchCd		= "";			//스케쥴코드
		String	szStlNo			= "";			//재료번호
		String	szYdWrkProgStat	= "";
		String	szYdUpWrkActGp	= "";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 로그 개선 
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 		// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		    	
		String 	szRcvTcCode 	= ydUtils.getTcCode(msgRecord);
		if (szRcvTcCode == null) {
			szMsg = SZ_SESSION_NAME + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			return JPlateYdConst.RETN_CD_TC_ERROR;
		}

        try {

        	szMsg = "[" + szOperationName + "] ---------------------- START :: " + msgRecord.toString();
    		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	//=============================================================
        	// Log 테이블 등록
        	//=============================================================
        	szMsg = "[2후판정정] 크레인 권상실적등록 수신";
        	ydUtils.putLogMsg("A", "", szMsg, "", "", "", "I", "A", "I", szRcvTcCode, SZ_SESSION_NAME, szMethodName);

	        //파라미터 check
	        intRtnVal = this.paramY7YDL008Check(msgRecord, getParamRec);

	        if (intRtnVal == -1) {
	        	szRtnMsg = "파라미터 Check중 Error : " + Integer.toString(intRtnVal);
                szMsg    = "[ " + szOperationName + "] " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                
                throw new DAOException(szMsg);
	        }

	        szYdUpWrLoc = ydDaoUtils.paraRecChkNull(getParamRec, "YD_UP_WR_LOC"	);		// 권상위치
    		szYdSchCd   = ydDaoUtils.paraRecChkNull(getParamRec, "YD_SCH_CD"	);		// 스케쥴코드
	        szModifier  = ydDaoUtils.paraRecModifier(getParamRec				);		// 수정자

	        //대상 데이터 SELECT
	        getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
	        intRtnVal = ydCrnSchDao.getYdCrnWrkMtl(getParamRec, getRecSet);

	        if (intRtnVal <= 0) {
				szRtnMsg = "스케쥴 Data가 존재하지 않습니다. :: " +  Integer.toString(intRtnVal);
                szMsg    = "[ " + szOperationName + "] " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                
				return szRtnMsg;
	        }

	        //레코드셋의 사이즈값으로 ErrorCheck
	        if (getRecSet.size() == 0) {
				szRtnMsg = "no data found!!!, ErrorCode ::" + Integer.toString(intRtnVal);
                szMsg    = "[ " + szOperationName + "] " + szRtnMsg;
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	            
				return szRtnMsg;
	        }

	        getRecSet.first();
	        getRecord = getRecSet.getRecord();

	        szStlNo			= ydDaoUtils.paraRecChkNull(getRecord, "STL_NO"					);		// 재료번호
	        szWbookId		= ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID"			);		// 작업예약ID
	      //szYdToLocDcsnMtd= ydDaoUtils.paraRecChkNull(getRecord, "YD_TO_LOC_DCSN_MTD");		//야드To위치결정방법
	        szYdDnWoLoc		= ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC"			);		// 권하지시위치
	      //szYdAimYdGp 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_AIM_YD_GP");				//야드목표야드구분
	        szYdWrkProgStat	= ydDaoUtils.paraRecChkNull(getRecord, "YD_WRK_PROG_STAT"		);
	        szYdUpWrkActGp	= ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WRK_ACT_GP"		);		// 야드권상작업수행구분(YD_UP_WRK_ACT_GP) 항목사용 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)

	        //조회한 크레인 스케줄의 작업활성상태 체크 한다.
	        // if ("0".equals(szYdWrkProgStat) || "1".equals(szYdWrkProgStat) || "W".equals(szYdWrkProgStat)) {
	        // 명령선택 상태일때만 권상실적 처리 가능하도록 변경
	        if ("1".equals(szYdWrkProgStat)) {

	        	// 적치단 정보 Clear (1개의 크레인스케줄에 잡혀있는 크레인작업재료의 정보를 모두 Check!)

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 getParamRec에 logId 추가 
	        	getParamRec.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
	        	
	        	intRtnVal = this.clearYdStklyrY7(getRecSet, getParamRec);

	            //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
		        setCrnschRec = JDTORecordFactory.getInstance().create();
		        setCrnschRec.setField("YD_CRN_SCH_ID",      getParamRec.getFieldString("YD_CRN_SCH_ID")		);
		        setCrnschRec.setField("YD_WRK_PROG_STAT",   JPlateYdConst.YD_EQP_STAT_DN_WO				);		// 3:권하지시
		        setCrnschRec.setField("YD_EQP_ID",     	   	getParamRec.getFieldString("YD_EQP_ID")			);
		        setCrnschRec.setField("YD_UP_WR_LOC",       getParamRec.getFieldString("YD_UP_WR_LOC")		);
		        setCrnschRec.setField("YD_UP_WR_LAYER",     getParamRec.getFieldString("YD_UP_WR_LAYER")	);
		        setCrnschRec.setField("YD_UP_WRK_ACT_GP",   szYdUpWrkActGp									);		// 야드권상작업수행구분(YD_UP_WRK_ACT_GP) 항목사용 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)
		        setCrnschRec.setField("YD_UP_WR_XAXIS",     getParamRec.getFieldString("YD_CRN_XAXIS")		);
		        setCrnschRec.setField("YD_UP_WR_YAXIS",     getParamRec.getFieldString("YD_CRN_YAXIS")		);
		        setCrnschRec.setField("YD_UP_WR_ZAXIS",     getParamRec.getFieldString("YD_CRN_ZAXIS")		);
		        setCrnschRec.setField("YD_UP_CMPL_DT",      JPlateYdUtils.getCurDate("yyyyMMddHHmmss")		);		// 권상완료일시
		        setCrnschRec.setField("MODIFIER",			szModifier);

				intRtnVal = ydCrnschDao.updCrnUpWr(setCrnschRec);
		        if (intRtnVal <= 0) {
		        	szRtnMsg = "크레인 스케줄의 업데이트 UPDATE 처리시 오류 발생." + Integer.toString(intRtnVal);
	                szMsg    = "[ " +szOperationName + "] " + szRtnMsg;
				 	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				 	
					return szRtnMsg;
		        }

		        //설비Table의 상태 변경 (권상완료상태로 변경)
		        setCrnschRec = JDTORecordFactory.getInstance().create();
		        setCrnschRec.setField("YD_EQP_ID",			getParamRec.getFieldString("YD_EQP_ID")		);
			    setCrnschRec.setField("YD_EQP_STAT",        JPlateYdConst.YD_EQP_STAT_UP_CMPL			);		// 2 : 권상완료
		        setCrnschRec.setField("MODIFIER",			szModifier									);

		        intRtnVal = ydEqpDao.updYdEqpStat(setCrnschRec);
				if (intRtnVal <= 0) {
		        	szRtnMsg = "설비상태 UPDATE 처리시 오류 발생." + Integer.toString(intRtnVal);
	                szMsg    = "[ " + szOperationName + "] " + szRtnMsg;
				 	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				 	
				 	return szRtnMsg;
	    		}

		        //------------------------------------------------------------------
			    //대차 및 차량 스케줄 이송재료 Handling
	            if ("TC".equals(szYdUpWrLoc.substring(2, 4))) {

	            	// 2후판정정야드는 대차이송재료 테이블 사용안함으로 주석 처리
	            	/*
	            	//대차스케쥴 이송재료 정보 셋팅.
	            	this.setYdTcarY7(getRecSet);

	            	szMsg = "권상시 대차이송재료 삭제 완료";
		            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					*/

	            	szTcarEqpId = szYdUpWrLoc.substring(0,1) + "X" + szYdUpWrLoc.substring(2,6);

	            	//대차스케줄 호출
	            	recSendMsg = JDTORecordFactory.getInstance().create();
	            	recSendMsg.setField("MSG_ID", 			"YDYDJ"			);		// YDYDJ520
	            	recSendMsg.setField("YD_LD_UD_GP", 		"U"				);
	            	recSendMsg.setField("YD_EQP_ID", 		szTcarEqpId		);
	            	recSendMsg.setField("YD_WBOOK_ID", 		szWbookId		);
	            	
	            	ydEjbCon.trx("JPlateYdTcarSchSeEJB", 	"procY7TcarSch", recSendMsg);

	            }

	            szMsg = "[권상실적처리] 권상지시위치[" + szYdUpWrLoc + "], " + "설비구분[" + szYdDnWoLoc.substring(2, 4) + "]";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 2후판정정 야드L2 크레인작업실적응답 전송  - YDY7L005
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    	        recInTemp = JDTORecordFactory.getInstance().create();
    	        recInTemp.setField("MSG_ID", 			"YDY7L005"										);
    	        recInTemp.setField("YD_EQP_ID", 		getParamRec.getFieldString("YD_EQP_ID")			);		// 야드설비ID
    	        recInTemp.setField("YD_WRK_PROG_STAT", 	JPlateYdConst.YD_EQP_STAT_UP_CMPL				);		// 야드작업진행상태 : 권상완료
    	        recInTemp.setField("YD_SCH_CD", 		getParamRec.getFieldString("YD_SCH_CD")			);		// 야드스케줄코드
    	        recInTemp.setField("YD_CRN_SCH_ID", 	getParamRec.getFieldString("YD_CRN_SCH_ID")		);		// 야드크레인스케줄ID
   	        	recInTemp.setField("YD_L2_WR_GP", 		JPlateYdConst.CRN_WRK_RE_LD_WR				);		// 야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
    	        recInTemp.setField("YD_L3_HD_RS_CD", 	JPlateYdConst.CRN_WRK_RE_CD_NORMAL_HD			);		// 야드L3처리결과코드

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recInTemp에 logId 추가 
    	        recInTemp.setField("LOG_ID", 			logId 											);      // logId
//-------------------------------------------------------------------------------------------------------------------------

   	        	szSendMsg = ydDelegate.sendMsg(recInTemp);

    			szMsg = "[" + szOperationName + "] 2후판정정야드L2 크레인작업실적응답[YDY7L005] 전송 완료>>>>" + szSendMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        } else {
	            szMsg = "야드작업진행상태 체크 오류 .. YD_WRK_PROG_STAT :: " + szYdWrkProgStat;
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

	            szRtnMsg = "명령선택후 권상처리 가능!. 현재상태:" + szYdWrkProgStat;
	            
	            return szMsg;
            //  m_ctx.setRollbackOnly();
			//	throw new DAOException(szMsg);
	        }

	        szMsg = "[" + szOperationName + "] 권상 완료 실적 처리 완료";
	        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        // RT BOOK-OUT시 전문전송 (FART0?LM)
	        if (szYdSchCd.length() == 8 && "RT".equals(szYdSchCd.substring(2,4)) && ydUtils.isBookOutSchCd(szYdSchCd)) {

		        szMsg = "[" + szOperationName + "] RT BOOK-OUT 실적 전송 .. 시작";
		        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

    	        recInTemp = JDTORecordFactory.getInstance().create();
    	        recInTemp.setField("MSG_ID", 			"YDS1L005"		);
    	        recInTemp.setField("STL_NO",			szStlNo			);		// 재료번호
    	        recInTemp.setField("OPERATION_TYPE",	"2"				);		// 1:Book In, 2:Book Out
    	        recInTemp.setField("YD_STK_COL_GP",		szYdUpWrLoc		);		// FROM위치

   	        	szSendMsg = ydDelegate.sendMsg(recInTemp);

		        szMsg = "[" + szOperationName + "] RT BOOK-OUT 실적 전송 .. 완료>>>>" + szSendMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        }


	    } catch(Exception e) {
	    //  m_ctx.setRollbackOnly();
			throw new DAOException(getClass().getName() + e.getMessage(),e);
	    }

    	szMsg = "[" + szOperationName + "] 권상 완료 실적 처리 >>>> END ";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	    return JPlateYdConst.RETN_CD_SUCCESS;
    } // end of procY7CrnUpWr()

    /**
     * 오퍼레이션명 : L2전문송신
     *
     * @param  ● msgRecord
     * @return ● szRtnStr
     * @throws
     */
    public String sendMsg(JDTORecord sendRecord) throws DAOException {

		String szMsg			= "";
        String szRtnStr        	= JPlateYdConst.RETN_CD_SUCCESS;
		String szMethodName		= "sendMsg";
		String szOperationName  = "L2전문송신";

        try {
        	szRtnStr = ydDelegate.sendMsg(sendRecord);
        } catch(Exception e) {
			szMsg = "[" + szOperationName + "] L2전문 송신처리 .. Exception발생";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
            return JPlateYdConst.RETN_CD_FAILURE;
        }

        if (JPlateYdConst.RETN_CD_FAILURE.equals(szRtnStr)) {
			szMsg = "[" + szOperationName + "] L2전문 송신처리 .. Exception발생222222";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
            return JPlateYdConst.RETN_CD_FAILURE;
        }

		szMsg = "[" + szOperationName + "] L2전문 송신처리 완료>>>>" + szRtnStr;
        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    return szRtnStr;
    }

    /**
     * 오퍼레이션명 : 권상 파라미터 체크
     *
     * @param  ● msgRecord, outRecord
     * @return ● intRtnVal
     * @throws ● DAOException
     */
    public int paramY7YDL008Check(JDTORecord msgRecord, JDTORecord outRecord) throws DAOException {

    	JDTORecord setRecord 	= JDTORecordFactory.getInstance().create();
//      String 	szMsg        	= "";
//      String 	szMethodName	= "paramCheckY8";
        int 	intRtnVal		= 0;

    	try {
/*
			//======================================================================================================
			// LOG 출력 - 그냥 테스트용으로 출력 레코드값체크하는 코드는 제외하고 나중에 삭제
			szMsg = "[1] 야드설비ID : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			szMsg = "[2] 야드설비작업Mode : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_MODE");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			szMsg = "[3] 야드작업진행상태 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			szMsg = "[4] 야드스케쥴코드 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			szMsg = "[5] 야드크레인스케쥴ID : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			szMsg = "[6] 야드권상실적위치 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LOC");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			szMsg = "[7] 야드권상실적단 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LAYER");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			szMsg = "[8] 야드크레인X축 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			szMsg = "[9] 야드크레인Y축 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			szMsg = "[10] 야드크레인Z축 : " + ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_ZAXIS");
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			//======================================================================================================
*/
			setRecord.setField("YD_CRN_SCH_ID"		, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID"));
			setRecord.setField("YD_SCH_CD"        	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD"));
			setRecord.setField("MSG_ID"          	, ydDaoUtils.paraRecChkNull(msgRecord,"MSG_ID"));
			setRecord.setField("DATE"          		, ydDaoUtils.paraRecChkNull(msgRecord,"DATE"));
			setRecord.setField("TIME"          		, ydDaoUtils.paraRecChkNull(msgRecord,"TIME"));
			setRecord.setField("MSG_GP"          	, ydDaoUtils.paraRecChkNull(msgRecord,"MSG_GP"));
			setRecord.setField("YD_EQP_ID"          , ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID"));
			setRecord.setField("YD_EQP_WRK_MODE"    , ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE"));
			setRecord.setField("YD_WRK_PROG_STAT"   , ydDaoUtils.paraRecChkNull(msgRecord,"YD_WRK_PROG_STAT"));
			setRecord.setField("YD_CRN_XAXIS"       , ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_XAXIS"));
			setRecord.setField("YD_CRN_YAXIS"       , ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_YAXIS"));
			setRecord.setField("YD_CRN_ZAXIS"       , ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_ZAXIS"));
			setRecord.setField("YD_UP_WR_LOC"   	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LOC"));
			setRecord.setField("YD_UP_WR_LAYER"		, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LAYER"));
			setRecord.setField("MODIFIER"			, ydDaoUtils.paraRecModifier(msgRecord));

			setRecord.setField("YD_UP_WRK_ACT_GP"	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WRK_ACT_GP"));

			/*
	        //전문 송신지 위치 Check				:AUTO MANUAL BACKUP구분
	        //if ("1".equals(setRecord.getFieldString("YD_EQP_WRK_MODE"))) {
	        //	setRecord.setField("YD_UP_WRK_ACT_GP", "A");
	        //} else if ("9".equals(setRecord.getFieldString("YD_EQP_WRK_MODE"))) {
	        //	setRecord.setField("YD_UP_WRK_ACT_GP", "B");
	        //} else if ("0".equals(setRecord.getFieldString("YD_EQP_WRK_MODE"))) {
	        //	setRecord.setField("YD_UP_WRK_ACT_GP", "M");
	        //}
			*/

    		outRecord.addRecord(setRecord);

        } catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }//end of try~catch

        intRtnVal = 1;
        return intRtnVal;

    }//end of paramY7YDL008Check()

    /**
     * 오퍼레이션명 : 적치단 Clear
     *
     * @param  ● getRecSet, intGp
     * @return ● intRtnVal
     * @throws ● DAOException
     */
    public int clearYdStklyrY7(JDTORecordSet getRecSet, JDTORecord pInRecord) throws DAOException {

    	JPlateYdStkLyrDAO ydStklyrDao = new JPlateYdStkLyrDAO();

    	JDTORecord getRecord 	= JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord 	= null;
    	JDTORecord crnRecord 	= null;

    	int intRtnVal 			= 0;
    	String 	szMsg 			= "";
    	String 	szMethodName 	= "clearYdStklyrY7";
    	String	szOperationName	= "적치단 Clear";
    	String 	szYdStkColGp 	= "";
    	String	szYdGp			= "";
    	String 	szYdStkBedNo 	= "";
    	String 	szYdUpWrLayer 	= "";
    	String	szYdUpWrLoc		= "";

    	String 	szCrnId			= "";
    	String 	szStlNo			= "";
    	String	szModifier		= "";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.04 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(pInRecord, "F");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

    	try {
    		int rowsize = getRecSet.size();
            getRecSet.first();
            getRecord = getRecSet.getRecord();

            szModifier		= ydDaoUtils.paraRecModifier(pInRecord);
            szYdUpWrLoc		= ydDaoUtils.paraRecChkNull(pInRecord, "YD_UP_WR_LOC");
            szYdUpWrLayer 	= ydDaoUtils.paraRecChkNull(pInRecord, "YD_UP_WR_LAYER");
            szYdGp		  	= ydUtils.substr(szYdUpWrLoc, 0, 1);
        	szYdStkColGp 	= ydUtils.substr(szYdUpWrLoc, 0, 6);
        	szYdStkBedNo 	= ydUtils.substr(szYdUpWrLoc, 6, 2);

            szMsg = "[ " + szOperationName + " ] 적치단 Clear Start :: " + szYdStkColGp + szYdStkBedNo + "-" + szYdUpWrLayer;
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

    		for(int ii = 0; ii < rowsize; ii++) {

    			getRecSet.absolute(ii+1);
            	getRecord = getRecSet.getRecord();
            	szCrnId   = ydDaoUtils.paraRecChkNull(getRecord,"YD_EQP_ID");
            	szStlNo   = ydDaoUtils.paraRecChkNull(getRecord,"STL_NO");

    			//크레인에 UPDATE
    			crnRecord = JDTORecordFactory.getInstance().create();
    			crnRecord.setField("YD_STK_COL_GP",       	szCrnId);
    			crnRecord.setField("YD_STK_BED_NO",       	"01");
    			crnRecord.setField("YD_STK_LYR_NO",       	ydUtils.addLeftStr(Integer.toString(ii+1), 3, '0'));
                crnRecord.setField("YD_STK_LYR_MTL_STAT", 	"C");
                crnRecord.setField("STL_NO",              	szStlNo);
                crnRecord.setField("MODIFIER",            	szModifier);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.04 crnRecord에 logId 추가 
                crnRecord.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

                intRtnVal = ydStklyrDao.updYdStklyrStat(crnRecord);  		//크레인 적치단의 재료정보 UPDATE

    			//권상 지시위치 Clear --> 재료번호로 CLEAR
                setRecord = JDTORecordFactory.getInstance().create();
                setRecord.setField("STL_NO",              	szStlNo);		//재료번호
                setRecord.setField("YD_STK_LYR_MTL_STAT", 	"U");			//권상지시상태
                setRecord.setField("YD_GP",					szYdGp);		//야드구분
                setRecord.setField("MODIFIER", 				szModifier);	//수정자


//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.04 setRecord에 logId 추가 
                setRecord.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

                intRtnVal = ydStklyrDao.updYdStklyrClearByStlNo(setRecord);	//권상위치 재료의 적치단 정보 Clear

            } //end of for

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
	    }//end of try~catch

		intRtnVal = 1;
		return  intRtnVal;
    } // end of clearYdStklyrY7()

    /**
     * 오퍼레이션명 : 대차 Setting
     *
     * @param  ● inRecordSet
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public void setYdTcarY7(JDTORecordSet inRecordSet) throws JDTOException{

    	JPlateYdTcarFtmvMtlDAO ydTcarFtmvMtlDao = new JPlateYdTcarFtmvMtlDAO();

    	//Data Setting
    	JDTORecord 	  setRecord 	= JDTORecordFactory.getInstance().create();
    	JDTORecord 	  getRecord 	= JDTORecordFactory.getInstance().create();
    	JDTORecord 	  getTcarRecord	= JDTORecordFactory.getInstance().create();
    	//대차 스케줄의 레코드셋
    	JDTORecordSet outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("temp");

    	int 	intRtnVal 			= 0;

		String 	szOperationName		= "대차 Setting";
    	String 	szMethodName		= "setYdTcarY7";
    	String 	szMsg 				= "";

    	//대차 스케줄 ID
    	String szYdTcarSchId		= "";

    	try {
	    	// 크레인스케줄 Data
	    	inRecordSet.first();
	    	getRecord = inRecordSet.getRecord();

			setRecord.setField("YD_CARUD_WRK_BOOK_ID", getRecord.getFieldString("YD_WBOOK_ID"));
	    	setRecord.setField("YD_CARLD_WRK_BOOK_ID", "");

	    	// 상하차 작업예약 ID로 대차스케줄 조회
	    	intRtnVal = this.getYdTcarSchY7(setRecord, outRecSet);
	    	if (intRtnVal <= 0){
	    		szMsg = "[ " +szOperationName + "] 대차에서 권상작업 처리시 대차스케쥴 정보 오류발생.";
	            throw new  DAOException(szMsg);
	    	}

	    	// 대차스케줄 Data
	    	outRecSet.first();
	    	getTcarRecord = outRecSet.getRecord();
	    	// 대차스케줄 ID를 추출한다
	    	szYdTcarSchId = ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_TCAR_SCH_ID");

	    	//setRecord 초기화
	    	setRecord 		= JDTORecordFactory.getInstance().create();
	    	int szRowSize 	= inRecordSet.size();

	    	// 권상한 재료만큼 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)
	    	for(int ii=0; ii<szRowSize; ii++){

	    		// 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)
	    		setRecord.setField("YD_TCAR_SCH_ID", szYdTcarSchId);
	    		setRecord.setField("STL_NO",         ydDaoUtils.paraRecChkNull(getRecord, "STL_NO"));
	    		setRecord.setField("DEL_YN",         "Y");

	    		intRtnVal = ydTcarFtmvMtlDao.delYdTcarFtmvMtl(setRecord);
				if (intRtnVal <= 0) {
		            szMsg = "[ " +szOperationName + "] 대차이송재료 삭제 오류 >>>> " + intRtnVal;
		            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				}

	    		inRecordSet.next();
		    	getRecord = inRecordSet.getRecord();
		    }

		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}//end of try~catch

    }//end of setYdTcarY7()

    /**
     * 오퍼레이션명 : 대차 스케줄 Select
     *
     * @param  ● msgRecord, outRecset
     * @return ● intRtnVal
     * @throws ●
     */
    public int getYdTcarSchY7(JDTORecord msgRecord, JDTORecordSet outRecset)throws JDTOException{

    	JPlateYdTcarSchDAO ydTcarschDao = new JPlateYdTcarSchDAO();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");

    	int intRtnVal 		= 0;
    	String szMethodName	= "getYdTcarSchY7";
    	String szMsg 		= "";

        try {

	        intRtnVal = ydTcarschDao.getByWrkBookId(msgRecord, getRecSet);
			if (intRtnVal <= 0) {
				if (intRtnVal == 0) {
					szMsg = "data not found";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING);
				} else if (intRtnVal == -2) {
					szMsg = "parameter error";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				}
				return intRtnVal = -1;
			}

	        outRecset.addAll(getRecSet);

        } catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }
        return intRtnVal;
    }//end of getYdTcarSchY7

	/**
     *  오퍼레이션명 : 2후판정정야드 강제권상요구 (Y7YDL010)
     *
     *  @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     *  @param msgRecord
     *  @return JDTORecord
     *  @throws DAOException
	*/
	public JDTORecord procY7OffCrnUpWr(JDTORecord msgRecord) throws DAOException {

		JDTORecord setRecord 		= JDTORecordFactory.getInstance().create();
		String	szRtnMsg			= "";
        String 	szMsg         		= "";
        String 	szMethodName  		= "procY7OffCrnUpWr";
        String	szOperationName		= "2후판정정야드 강제권상요구";

        int 	intRtnVal 			= 0;

		String 	szYdEqpId    		= ""; 		// 야드설비ID
		String 	szYdUpWrLoc  		= ""; 		// 야드권상실적위치
		String 	szYdCrnXaxis 		= ""; 		// 야드크레인X축
		String 	szYdCrnYaxis 		= ""; 		// 야드크레인Y축
		String	szYdEqpWrkSh		= ""; 		// 야드크레인작업매수
		String 	szModifier   		= ""; 		// 수정자(Backup Only)
		int		iYdEqpWrkSh			= 0;
		int		oUpCnt				= 0;		// 권상예약건수
		int		nExistCnt			= 0;		// 상단 보조작업 존재 건수

		String 	szStlNo         	= ""; 		// 권상가능 재료번호
		String	szYdStkBedNo		= "";		// 야드적치베드번호
		String 	szYdStkLyrNo    	= ""; 		// 야드적치단번호
		String 	szXaxisYn       	= ""; 		// X좌표 정합성여부
		String 	szYaxisYn       	= ""; 		// Y좌표 정합성여부
		String 	szYdStkBedXaxis 	= ""; 		// 야드적치BedX축
		String 	szYdStkBedYaxis 	= ""; 		// 야드적치BedY축
		String 	szYdMtlW        	= ""; 		// 야드재료폭
		String 	szYdMtlL        	= ""; 		// 야드재료길이
		String 	szYdMtlWt       	= ""; 		// 야드재료중량
		String 	szWmCnt         	= ""; 		// 작업예약재료 건수
		String	arrStlNo[]			= {"","","","","", "","","","","", "","","","",""};
		String	szStlNoList			= "";

        String	szYD_UP_WO_LOC		= "";
        String	szYD_DN_WO_LOC		= "";
		String	szYD_UP_STK_COL_GP 	= "";
		String	szYD_UP_STK_BED_NO	= "";
		String	szYD_DN_STK_COL_GP	= "";
		String	szYD_DN_STK_BED_NO	= "";

//		int		iSumMtlL 			= 0;
		double	dSumMtlT 			= 0;
		int		iSumMtlWt 			= 0;

        // 레코드 선언
		JDTORecord    recPara   = null;
    	JDTORecord    recInPara	= null;
    	JDTORecord    recCrnSch	= null;
    	JDTORecord    recTemp	= null;
		JDTORecordSet rsOffUp  	= null;
		JDTORecordSet rsResult	= null;

		JPlateYdCrnSchDAO     ydCrnSchDao  	  = new JPlateYdCrnSchDAO();		// 크레인스케쥴DAO
		JPlateYdEqpDAO        ydEqpDao		  = new JPlateYdEqpDAO();			// 야드설비(크레인)DAO
		JPlateYdWrkbookDAO    ydWrkbookDao 	  = new JPlateYdWrkbookDAO();		// 야드작업예약 DAO
		JPlateYdWrkbookMtlDAO ydWrkbookMtlDao = new JPlateYdWrkbookMtlDAO();	// 야드작업예약재료 DAO
		JPlateYdStockDAO	  ydStockDao      = new JPlateYdStockDAO();			// 야드저장품 DAO
		JPlateYdStkLyrDAO	  ydStkLyrDao	  = new JPlateYdStkLyrDAO();		// 야드적치단 DAO
		JPlateYdSchRuleDAO    ydSchRuleDao	  = new JPlateYdSchRuleDAO();		// 야드스케줄기준 DAO
		JPlateYdCrnWrkMtlDAO  ydCrnWrkMtlDao  = new JPlateYdCrnWrkMtlDAO();		// 야드크레인작업재료 DAO

		try {
			szMsg = "["+szOperationName+"] 강제권상 요구 .. Start >>>> " + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			// YD_EQP_ID		야드설비ID
			// YD_UP_WR_LOC		야드권상실적위치		(6자리까지만 사용)
			// YD_UP_WR_LAYER	야드권상실적단			(무의미)
			// YD_CRN_XAXIS		야드크레인X축
			// YD_CRN_YAXIS		야드크레인Y축
			// YD_CRN_ZAXIS		야드크레인Z축
			// YD_EQP_WRK_SH	야드크레인작업매수
			// STL_NO1~15		재료번호1 ~ 15

			//수신 항목 값
			szYdEqpId    	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"); 			//야드설비ID
			szYdUpWrLoc  	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LOC"); 		//야드권상실적위치
			szYdCrnXaxis 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS"); 		//야드크레인X축
			szYdCrnYaxis 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS"); 		//야드크레인Y축
			szYdEqpWrkSh	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_SH", "1"); 	//야드크레인작업매수
			for(int ii=0; ii<15; ii++) {
				arrStlNo[ii] = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"+(ii+1));		//강제권상 재료번호 1~15
			}
			szModifier   	= ydDaoUtils.paraRecModifier(msgRecord); 						//수정자(Backup Only)

			//크레인작업실적응답 전문 생성용
			String 	ydL3HdRsCd 	= ""; //야드L3처리결과코드
			String 	ydL3Msg    	= ""; //야드L3MESSAGE
//			String	ydSpanNo	= "";

			setRecord.setField("YD_EQP_ID", 		szYdEqpId); 						//야드설비ID
			setRecord.setField("YD_L2_WR_GP", 		"J"    ); 						 	//야드L2실적구분(지시요구)
			setRecord.setField("YD_L3_HD_RS_CD", 	"FU99" ); 						 	//야드L3처리결과코드(Error)
			setRecord.setField("YD_L3_MSG", 		"오류:강제권상요구 예상치 못한 오류"); 		//야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(szYdEqpId)) {
				ydL3HdRsCd = "FU01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (szYdEqpId.length() < 6) {
				ydL3HdRsCd = "FU02";
				ydL3Msg    = "오류:설비ID[" + szYdEqpId + "] 이상";
			} else if (szYdUpWrLoc.length() < 8) {
				ydL3HdRsCd = "FU04";
				ydL3Msg    = "오류:권상위치 이상";
			} else if (!szYdEqpId.substring(0, 2).equals(szYdUpWrLoc.substring(0, 2))) {
				ydL3HdRsCd = "FU05";
				ydL3Msg    = "오류:설비-권상위치[" + szYdEqpId.substring(0, 2) + "-" + szYdUpWrLoc.substring(0, 2) + "] 부적합";
			} else if ("".equals(szYdCrnXaxis)) {
				ydL3HdRsCd = "FU06";
				ydL3Msg    = "오류:크레인X축 없음";
			} else if ("".equals(szYdCrnYaxis)) {
				ydL3HdRsCd = "FU07";
				ydL3Msg    = "오류:크레인Y축 없음";
			}

			/*
			// 설비에서는 권상 못하도록 보완 .. 2013.05.02 김현우
			ydSpanNo = ydUtils.substr(szYdUpWrLoc, 2, 2);
			if (!ydSpanNo.matches("\\d\\d")) {
				ydL3HdRsCd = "FU08";
				ydL3Msg    = "오류:권상위치 이상[설비위치]";
			}
			*/

			// 작업매수 MAX 5건 체크 --> 15건으로 변경
			iYdEqpWrkSh = Integer.parseInt(szYdEqpWrkSh);
			if (iYdEqpWrkSh < 1 || iYdEqpWrkSh > 15) {
				ydL3HdRsCd = "FU09";
				ydL3Msg    = "오류:작업매수 [" + szYdEqpWrkSh + "]";
			}
			for(int ii=1; ii<=iYdEqpWrkSh; ii++) {
				szStlNo = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"+ii); 	//재료번호
				if ("".equals(szStlNo)) {
					ydL3HdRsCd = "FU09";
					ydL3Msg    = "오류:작업매수(재료번호) [" + szYdEqpWrkSh + "]";
					break;
				}
			}

			if (!"".equals(ydL3Msg)) {
				setRecord.setField("YD_L3_HD_RS_CD", 	ydL3HdRsCd); 			//야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		ydL3Msg   ); 			//야드L3MESSAGE
				setRecord.setField("RTN_CD",			JPlateYdConst.RETN_CD_FAILURE);
				return setRecord;
			}

			/**********************************************************
			* 2. 권상위치, 좌표값, 이적 재료 등을 Check
				 YD_EQP_ID		//야드설비ID
				 YD_UP_WR_LOC	//야드권상실적위치
				 YD_UP_WR_LAYER	//야드권상실적단
				 YD_CRN_XAXIS	//야드크레인X축
				 YD_CRN_YAXIS	//야드크레인Y축
				 YD_EQP_WRK_SH	//야드크레인작업매수
				 STL_NO1~15		//재료번호1 ~ 15

			**********************************************************/
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_EQP_ID", 		szYdEqpId); 							//야드설비ID
			recPara.setField("YD_STK_COL_GP", 	ydUtils.substr(szYdUpWrLoc, 0, 6)); 	//야드적치열구분
			recPara.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdUpWrLoc, 6, 2)); 	//야드적치Bed번호
			recPara.setField("YD_CRN_XAXIS", 	szYdCrnXaxis); 							//야드크레인X축
			recPara.setField("YD_CRN_YAXIS", 	szYdCrnYaxis); 							//야드크레인Y축
			recPara.setField("YD_EQP_WRK_SH", 	szYdEqpWrkSh);							//야드크레인작업매수
			for(int ii=0; ii<15; ii++) {
				recPara.setField("STL_NO"+(ii+1), arrStlNo[ii]); 						// 강제권상 재료번호 1~15
				if ("".equals(szStlNoList)) {
					szStlNoList = arrStlNo[ii];
				} else {
					szStlNoList = szStlNoList + ";" + arrStlNo[ii];
				}
			}
			recPara.setField("ARR_STL_NO",		szStlNoList); 							// 강제권상 재료번호 LIST

			//좌표값 에 해당하는 재료정보 조회
			rsOffUp   = JDTORecordFactory.getInstance().createRecordSet("yd");
			intRtnVal = ydCrnSchDao.getOffCrnUpBed(recPara, rsOffUp);

			if (intRtnVal <= 0) {
				//FROM위치에 대상재료 미존재
				ydL3HdRsCd = "FU11";
				ydL3Msg    = "오류:FROM위치 대상 재료 미존재";
			} else if (intRtnVal < Integer.parseInt(szYdEqpWrkSh)) {
				//FROM위치에 대상재료 부족
				ydL3HdRsCd = "FU12";
				ydL3Msg    = "오류:FROM위치 대상 재료 부족";
			} else {

				if (rsOffUp != null && rsOffUp.size() > 0) {
					szStlNo         = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "STL_NO");
					szYdStkLyrNo    = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "YD_STK_LYR_NO");
					szXaxisYn       = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "XAXIS_YN");
					szYaxisYn       = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "YAXIS_YN");
					szYdStkBedXaxis = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "YD_STK_BED_XAXIS");
					szYdStkBedYaxis = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "YD_STK_BED_YAXIS");
					szYdMtlW        = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "YD_MTL_W");
					szYdMtlL        = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "YD_MTL_L");
					szYdMtlWt       = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "YD_MTL_WT");
					szWmCnt         = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "WM_CNT");
					oUpCnt 			= ydDaoUtils.paraRecChkNullInt(rsOffUp.getRecord(0), "O_UP_CNT");
					nExistCnt 		= ydDaoUtils.paraRecChkNullInt(rsOffUp.getRecord(0), "N_EXIST_CNT");		// 상단에 보조작업 (작업 비대상 재료) 존재 건수
				}

				if (!"Y".equals(szXaxisYn)) {
					//권상위치의 BedX축 허용오차 값내에 크레인X축 값 존재여부를 Check
					ydL3HdRsCd = "FU13";
					ydL3Msg    = "오류:크레인X축[" + szYdStkBedXaxis + ":" + szYdCrnXaxis + "] 이상";
				} else if (!"Y".equals(szYaxisYn)) {
					//권상위치의 BedY축 허용오차 값내에 크레인Y축 값 존재여부를 Check
					ydL3HdRsCd = "FU14";
					ydL3Msg    = "오류:크레인Y축[" + szYdStkBedYaxis + ":" + szYdCrnYaxis + "] 이상";
				} else if ("".equals(szStlNo)) {
					//권상위치에 적치된 이적 가능 재료 존재여부를 Check
					ydL3HdRsCd = "FU15";
					ydL3Msg    = "오류:권상가능 재료 없음[" + szYdUpWrLoc + "]";
				} else if ("".equals(szYdMtlW) || "".equals(szYdMtlL) || "".equals(szYdMtlWt)) {
					//이적 재료 저장품 Table 존재여부 및  Size 정상여부를 Check
					ydL3HdRsCd = "FU16";
					ydL3Msg    = "오류:[" + szStlNo + "] 저장품정보 이상";
				} else if (!"".equals(szWmCnt) && Integer.parseInt(szWmCnt) > 0) {
					//이적 재료 작업예약재료 Table 존재여부를 Check
					ydL3HdRsCd = "FU17";
					ydL3Msg    = "오류:[" + szStlNo + "] 작업예약 기등록";
				} else if (oUpCnt > 0) {
					//권상위치의 권상예약정보 존재여부 체크
					ydL3HdRsCd = "FU18";
					ydL3Msg    = "오류:권상예약 정보 존재로 오류발생!";
				} else if (nExistCnt > 0) {
					//FROM위치 상단에 보조작업 존재
					ydL3HdRsCd = "FU19";
					ydL3Msg    = "오류:FROM위치 상단에 보조작업 존재";
				}

				if (rsOffUp.size() > 0) {
					for(int ii=0; ii<rsOffUp.size(); ii++) {
						rsOffUp.absolute(ii+1);
						recTemp	= rsOffUp.getRecord();

//						iYdStkColL 	= ydDaoUtils.paraRecChkNullInt   (recTemp, 	"YD_STK_COL_L");
//						iSumMtlL 	= ydDaoUtils.paraRecChkNullInt   (recTemp, 	"SUM_MTL_L");
						dSumMtlT 	= ydDaoUtils.paraRecChkNullDouble(recTemp, 	"SUM_MTL_T");
						iSumMtlWt 	= ydDaoUtils.paraRecChkNullInt   (recTemp, 	"SUM_MTL_WT");

				/* -- 25M 넘는 제품
						// 재료길이 25M 넘는 재료는 강제권상 불가
						if (iSumMtlL > 25000) {
							ydL3HdRsCd = "FU31";
							ydL3Msg    = "재료길이 합이 25M 넘어 작업불가! [" + iSumMtlL + "]";
							break;
						}
				*/
						// 재료두께 50T 넘는 재료는 강제권상 불가 (1건이상시)
						if (dSumMtlT > 50 && rsOffUp.size() > 1) {
							ydL3HdRsCd = "FU32";
							ydL3Msg    = "재료두께 합 50t 넘어 작업불가! [" + dSumMtlT + "]";
							break;
						}

						// 25톤 넘는 재료는 강제권상 불가
						if (iSumMtlWt > 25000) {
							ydL3HdRsCd = "FU33";
							ydL3Msg    = "재료중량 합 25톤 넘어 작업불가! [" + iSumMtlWt + "]";
							break;
						}
					}
				}
			}

			if (!"".equals(ydL3Msg)) {
				setRecord.setField("YD_L3_HD_RS_CD", 	ydL3HdRsCd); 			//야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		ydL3Msg); 				//야드L3MESSAGE
				setRecord.setField("RTN_CD",			JPlateYdConst.RETN_CD_FAILURE);
				return setRecord;
			}

			/**********************************************************
			* 3. 설비상태, 설비사양, 크레인스케줄 존재여부 등을 Check
			**********************************************************/
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
			ydCrnSchDao.getOffCrnUpStat(recPara, rsResult);

			String ydEqpStat     = ""; //야드설비상태
			String ydEqpWrkMode  = ""; //야드설비작업Mode
			String ydCrnSchId    = ""; //야드크레인스케쥴ID(현재)
//			String ydWrkProgStat = ""; //야드작업진행상태
			String ydWrkAbleW    = ""; //야드작업가능폭
			String ydWrkAbleL    = ""; //야드작업가능길이
			String ydWrkAbleWt   = ""; //야드작업가능중량

			if (rsResult != null && rsResult.size() > 0) {
				ydEqpStat     = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_EQP_STAT"     );
				ydEqpWrkMode  = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_EQP_WRK_MODE" );
				ydCrnSchId    = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_CRN_SCH_ID"   );
//				ydWrkProgStat = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_PROG_STAT");
				ydWrkAbleW    = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_ABLE_W"   );
				ydWrkAbleL    = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_ABLE_L"   );
				ydWrkAbleWt   = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_ABLE_WT"  );
			}

			szMsg = "["+szMethodName+"] 대상재료 >>>> " + rsOffUp.getRecord(0).toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "["+szMethodName+"] 설비사양 >>>> " + rsResult.getRecord(0).toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if ("".equals(ydEqpStat)) {
				//설비 Table 정보 Check
				ydL3HdRsCd = "FU41";
				ydL3Msg    = "오류:크레인[" + szYdEqpId + "] 정보 없음";
			} else if ("B".equals(ydEqpStat)) {
				//설비 Table 설비상태 Check
				ydL3HdRsCd = "FU42";
				ydL3Msg    = "오류:크레인[" + szYdEqpId + "] 고장";
			} else if (!"1".equals(ydEqpWrkMode)) {
				//설비 Table 설비작업Mode Check
				ydL3HdRsCd = "FU43";
				ydL3Msg    = "오류:크레인[" + szYdEqpId + "] Off-Line";

		//  2013-04-25 L2테스트 하면서 주석처리함
		//	} else if (!"1".equals(ydEqpStat) && !"W".equals(ydEqpStat)) {
		//		//설비 Table 설비상태 Check : 권상작업지시, 대기 외이면 불가
		//		ydL3HdRsCd = "FU44";
		//		ydL3Msg    = "오류:크레인[" + szYdEqpId + "] 상태[" + ydEqpStat + "] 부적합";
		//	} else if ("1".equals(ydEqpStat) && "".equals(ydCrnSchId)) {
		//		//설비 Table 설비상태가 권상작업지시이면 크레인스케줄의 존재여부를 Check
		//		//크레인스케줄  Table의 야드작업진행상태가 권상지시[1], 권상완료[2], 권하지시[3]인 스케줄이 있어야 함
		//		ydL3HdRsCd = "FU45";
		//		ydL3Msg    = "오류:크레인[" + szYdEqpId + "] 상태[" + ydWrkProgStat + "] 스케줄 없음";

		//  2013-07-16 명령선택 이후에 강제권상 안되도록 보완
			} else if (!"W".equals(ydEqpStat) && !"4".equals(ydEqpStat)) {
				//설비 Table 설비상태 Check : 권하완료, 대기 외이면 불가
				ydL3HdRsCd = "FU46";
				ydL3Msg    = "오류:크레인[" + szYdEqpId + "] 상태[" + ydEqpStat + "] 부적합";

			} else if ("".equals(ydWrkAbleW) || "".equals(ydWrkAbleL) || "".equals(ydWrkAbleWt)) {
				//크레인사양 Table 정보의 이상여부를 Check
				ydL3HdRsCd = "FU47";
				ydL3Msg    = "오류:크레인[" + szYdEqpId + "] 사양 이상";
			} else if (Double.parseDouble(szYdMtlW) > Double.parseDouble(ydWrkAbleW)) {
				//이적 대상 재료 폭의 크레인사양폭 초과여부를 Check
				ydL3HdRsCd = "FU48";
				ydL3Msg    = "오류:크레인[" + szYdEqpId + "] 사양폭 초과";
			} else if (Double.parseDouble(szYdMtlL) > Double.parseDouble(ydWrkAbleL)) {
				//이적 대상 재료 길이의 크레인사양길이 초과여부를 Check
				ydL3HdRsCd = "FU49";
				ydL3Msg    = "오류:크레인[" + szYdEqpId + "] 사양길이 초과";
			} else if (Double.parseDouble(szYdMtlWt) > Double.parseDouble(ydWrkAbleWt)) {
				//이적 대상 재료 중량의 크레인사양중량 초과여부를 Check
				ydL3HdRsCd = "FU50";
				ydL3Msg    = "오류:크레인[" + szYdEqpId + "] 사양중량 초과";
			}

			if (!"".equals(ydL3Msg)) {
				setRecord.setField("YD_L3_HD_RS_CD", 	ydL3HdRsCd); 		//야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		ydL3Msg); 			//야드L3MESSAGE
				setRecord.setField("RTN_CD",			JPlateYdConst.RETN_CD_FAILURE);
				return setRecord;
			}

			/**********************************************************
			* 4. 스케줄기준 정보 Check
			**********************************************************/
			String ydGp          = ydUtils.substr(szYdUpWrLoc, 0, 1);		//야드구분
			String ydBayGp       = ydUtils.substr(szYdUpWrLoc, 1, 1); 		//야드동구분
		//	String ydSchCd       = ydGp + ydBayGp + "YD" + szYdUpWrLoc.substring(2, 4) + "MM";
			//야드스케쥴코드 : 야드+동+FU(강제권상)+설비호기+M(이적)+M(분할없음)
			String ydSchCd       = ydGp + ydBayGp + "FU0" + ydUtils.substr(szYdEqpId,5,1) + "MM";
			String ydSchProhExn  = "";  //야드스케쥴금지유무
			String ydWrkCrnPrior = "1"; //야드작업크레인우선순위

			recPara.setField("YD_SCH_CD", ydSchCd); //야드스케쥴코드

			//야드스케쥴금지유무 조회
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
			ydSchRuleDao.getYdSchrule(recPara, rsResult);

			if (rsResult != null && rsResult.size() > 0) {
				ydSchProhExn  = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_SCH_PROH_EXN");
				//야드작업크레인우선순위 : 야드스케쥴코드에 해당하는 작업크레인의 우선순위이므로
				//실제 요청한 크레인의 우선순위와 다를 수 있음
				ydWrkCrnPrior = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_CRN_PRIOR");
			}

			if ("".equals(ydSchProhExn)) {
				//스케줄기준 Table 정보 Check
				ydL3HdRsCd = "FU71";
				ydL3Msg    = "오류:스케쥴코드[" + ydSchCd + "] 정보 없음";
			} else if ("Y".equals(ydSchProhExn)) {
				//스케줄 금지여부 Check
				ydL3HdRsCd = "FU72";
				ydL3Msg    = "오류:스케쥴코드[" + ydSchCd + "] 기동금지";
			}

			if (!"".equals(ydL3Msg)) {
				setRecord.setField("YD_L3_HD_RS_CD", 	ydL3HdRsCd); //야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		ydL3Msg); //야드L3MESSAGE
				setRecord.setField("RTN_CD",			JPlateYdConst.RETN_CD_FAILURE);
				return setRecord;
			}

			/**********************************************************
			* 5. 현재 크레인스케줄의 야드작업진행상태 Update
			*    설비상태가 권상작업지시[1]이고  야드작업진행상태가 권상지시,
			*    권상완료, 권하지시[1,2,3] 이면 야드작업진행상태를
			*    명령선택대기[W]로 수정
			**********************************************************/
			if ("1".equals(ydEqpStat) && !"".equals(ydCrnSchId)) {
				recPara.setField("YD_CRN_SCH_ID", 		ydCrnSchId); 									// 야드크레인스케쥴ID(현재)
				recPara.setField("YD_WRK_PROG_STAT", 	"1"       ); 									// 권상지시
				recPara.setField("YD_WORD_DT", 			JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));	// 야드작업지시일시
				recPara.setField("MODIFIER", 			szModifier);

				//현재 크레인스케줄 야드작업진행상태 수정
				ydCrnSchDao.updYdCrnWrkProgStat(recPara);
			}

			/**********************************************************
			* 6. 작업예약 등록 및 저장품, 적치단 정보 수정
			**********************************************************/
			//작업예약ID 생성
			String ydWbookId = ydWrkbookDao.getSeqId();

			if ("".equals(ydWbookId)) {
				ydL3Msg = "오류:작업예약ID 생성 실패";
				setRecord.setField("YD_L3_HD_RS_CD", 	"FU41" ); 		//야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		ydL3Msg); 		//야드L3MESSAGE
				setRecord.setField("RTN_CD",			JPlateYdConst.RETN_CD_FAILURE);
				return setRecord;
			}

			//작업예약 등록
			recPara.setField("YD_WBOOK_ID", 		ydWbookId); 		//야드작업예약ID
			recPara.setField("YD_GP", 				ydGp); 				//야드구분
			recPara.setField("YD_BAY_GP", 			ydBayGp); 			//야드동구분
			recPara.setField("YD_SCH_CD", 			ydSchCd); 			//야드스케쥴코드
			recPara.setField("YD_SCH_PRIOR", 		ydWrkCrnPrior); 	//야드스케쥴우선순위
			recPara.setField("YD_SCH_PROG_STAT", 	"W"); 				//야드스케쥴진행상태(스케줄수행대기)
			recPara.setField("YD_SCH_ST_GP", 		"M"); 				//야드스케쥴기동구분(Manual 작업)
			recPara.setField("YD_SCH_REQ_GP", 		"X"); 				//야드스케쥴요청구분(강제권상요구)
			recPara.setField("YD_AIM_YD_GP", 		ydGp); 				//야드목표야드구분
			recPara.setField("YD_AIM_BAY_GP", 		ydBayGp); 			//야드목표동구분
			recPara.setField("YD_TO_LOC_DCSN_MTD", 	"S"); 				//야드TO위치결정방법(스케줄기준적용)
			recPara.setField("REGISTER",			szModifier);
			recPara.setField("MODIFIER",			szModifier);

			ydWrkbookDao.insYdWrkbook(recPara);

			rsOffUp.first();

			for (int ii=0; ii<rsOffUp.size(); ii++) {

				rsOffUp.absolute(ii+1);
				recTemp 	 = rsOffUp.getRecord();
				szStlNo 	 = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
				szYdStkBedNo = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_NO");
				szYdStkLyrNo = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");

				//작업예약재료 등록
				recPara.setField("STL_NO", 			szStlNo); 			//재료번호
				recPara.setField("YD_STK_BED_NO",	szYdStkBedNo);		//야드적치베드
				recPara.setField("YD_STK_LYR_NO", 	szYdStkLyrNo); 		//야드적치단

				ydWrkbookMtlDao.insYdWrkbookMtl(recPara);

				//저장품 작업예약정보 수정 (TB_YD_STOCK)
				ydStockDao.updYdStockWbook(recPara);

				//적치단 야드적치단재료상태 수정
				recPara.setField("YD_STK_LYR_MTL_STAT", "U"); 				//권상대기

				ydStkLyrDao.updYdStklyrStat(recPara);

			}

			/**********************************************************
			* 7. 크레인스케줄 등록
			**********************************************************/
			//크레인스케줄ID 생성
			ydCrnSchId = ydCrnSchDao.getSeqId();

        	szMsg = "["+ szOperationName +"] 크레인스케줄 등록 .. 크레인스케줄ID 생성 >>>> " + ydCrnSchId;
    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if ("".equals(ydCrnSchId)) {
				ydL3Msg = "오류:크레인스케줄ID 생성 실패";
				setRecord.setField("YD_L3_HD_RS_CD", 	"FU51" ); 			//야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		ydL3Msg); 			//야드L3MESSAGE
				setRecord.setField("RTN_CD",			JPlateYdConst.RETN_CD_FAILURE);
				return setRecord;
			}

			recPara.setField("YD_CRN_SCH_ID", 			ydCrnSchId); 					//야드크레인스케쥴ID

			recPara.setField("YD_UP_WRK_ACT_GP",		JPlateYdConst.YD_PILING_GP_F);	//야드권상작업수행구분(YD_UP_WRK_ACT_GP) 항목에 강제권상('F') SET

			//크레인스케줄 등록
			ydCrnSchDao.mergeYdCrnsch(recPara);

			//크레인작업재료 등록
			ydCrnWrkMtlDao.mergeYdCrnWrkMtl(recPara);

			//-----------------------------------------------------------
			// 8. 크레인작업지시의 크레인XY좌표수정
			//-----------------------------------------------------------
			// 크레인 작업지시 조회
			//좌표값 에 해당하는 재료정보 조회
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("yd");
			intRtnVal = ydCrnSchDao.getYdCrnSch(recPara, rsResult);

			if (intRtnVal < 1) {
				ydL3Msg = "오류:크레인작업지시 조회 오류";
				setRecord.setField("YD_L3_HD_RS_CD", 	"FU61" ); 			//야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		ydL3Msg); 			//야드L3MESSAGE
				setRecord.setField("RTN_CD",			JPlateYdConst.RETN_CD_FAILURE);
				return setRecord;
			}
			rsResult.first();
			recCrnSch = rsResult.getRecord();

			szYD_UP_WO_LOC = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");
			szYD_DN_WO_LOC = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC");

			szYD_UP_STK_COL_GP = ydUtils.substr(szYD_UP_WO_LOC, 0, 6);
			szYD_UP_STK_BED_NO = ydUtils.substr(szYD_UP_WO_LOC, 6, 2);
			szYD_DN_STK_COL_GP = ydUtils.substr(szYD_DN_WO_LOC, 0, 6);
			szYD_DN_STK_BED_NO = ydUtils.substr(szYD_DN_WO_LOC, 6, 2);

			recCrnSch.setField("YD_UP_STK_COL_GP", 	szYD_UP_STK_COL_GP);
			recCrnSch.setField("YD_UP_STK_BED_NO",	szYD_UP_STK_BED_NO);
			recCrnSch.setField("YD_DN_STK_COL_GP",	szYD_DN_STK_COL_GP);
			recCrnSch.setField("YD_DN_STK_BED_NO",	szYD_DN_STK_BED_NO);

        	szMsg = "["+ szOperationName +"] 강제권상 크레인 X,Y 좌표 수정 START";
    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    		szRtnMsg = JPlateYdCrnSchUtil.uptCrnSchXYCord(recCrnSch);

        	szMsg = "["+ szOperationName +"] 강제권상 크레인 X,Y 좌표 수정 END >>>>" + szRtnMsg;
    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//-----------------------------------------------------------
			// 9. 크레인작업지시(YDY7L004) 전문 송신
			//-----------------------------------------------------------
			szMsg = "["+szOperationName+"] 강제권상 요구 .. 크레인작업지시(YDY7L004) 전문 송신 START";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//설비 야드설비상태 수정
			recPara.setField("YD_EQP_STAT", 		"1"); //권상작업지시
			ydEqpDao.updYdEqpStat(recPara);

        	recInPara = JDTORecordFactory.getInstance().create();
    		//작업지시 전문 전송 data setup
			recInPara.setField("MSG_ID", 			"YDY7L004");
        	recInPara.setField("YD_CRN_SCH_ID",    	ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_SCH_ID"));
        	recInPara.setField("YD_WRK_PROG_STAT", 	ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_PROG_STAT"));
        	recInPara.setField("YD_SCH_CD",        	ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD"));
        	recInPara.setField("YD_GP",            	ydDaoUtils.paraRecChkNull(recPara, "YD_GP"));
        	recInPara.setField("MODIFIER", 			szModifier);

        	//작업진행중인 작업을 재전송하는 경우는  MSG_GP 값을 'U' UPDATE로 설정해서 보낸다.
        	recInPara.setField("MSG_GP", 			"U");
        	szRtnMsg = ydDelegate.sendMsg(recInPara);

			szMsg = "["+szOperationName+"] 강제권상 요구 .. 크레인작업지시(YDY7L004) 전문 송신 END >>>> " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

        	recInPara.setField("RTN_CD", 		JPlateYdConst.RETN_CD_SUCCESS);
        	recInPara.setField("YD_L3_MSG", 	JPlateYdConst.RETN_CD_SUCCESS);
			return recInPara;

		} catch (Exception e) {

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}

	/**********************************************************
	* 1후판정정추가 SJH16 
	**********************************************************/
    /**
     * 오퍼레이션명 : 1후판정정 크레인권상실적등록 (Y2YDL008)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
     */
    public String procY2CrnUpWr(JDTORecord msgRecord)throws DAOException  {

    	JPlateYdCrnSchDAO 	ydCrnSchDao = new JPlateYdCrnSchDAO();
    	JPlateYdEqpDAO   	ydEqpDao    = new JPlateYdEqpDAO();
    	JPlateYdCrnSchDAO	ydCrnschDao	= new JPlateYdCrnSchDAO();

    	//업데이트 할 크레인 스케줄 Data 항목 set
        JDTORecord setCrnschRec	= null;

        //파라미터 null check 후 받아온 Data
        JDTORecord getParamRec	= JDTORecordFactory.getInstance().create();

        //스케줄Table의 컬럼을 저장하기위해 생성
        JDTORecordSet getRecSet = null;
        //레코드 셋의 레코드값을 받음
        JDTORecord getRecord 	= JDTORecordFactory.getInstance().create();

//        JDTORecord recSendMsg 	= null;
        JDTORecord recInTemp    = null;

        int intRtnVal 			= 0;

//        String 	szWbookId		= "";
		String 	szMsg			= "";
		String 	szRtnMsg		= JPlateYdConst.RETN_CD_SUCCESS;
        String 	szSendMsg       = "";
		String 	szMethodName	= "procY2CrnUpWr";
		String 	szOperationName	= "1후판정정야드 L2 크레인권상실적등록";

//		String 	szTcarEqpId 	= "";
		String	szYdUpWrLoc		= "";			//권상위치
		String 	szYdDnWoLoc 	= null;			//권하지시위치
		String 	szModifier		= null;			//수정자
		String	szYdSchCd		= "";			//스케쥴코드
		String	szStlNo			= "";			//재료번호
		String	szYdWrkProgStat	= "";
		String	szYdUpWrkActGp	= "";

		String 	szRcvTcCode 	= ydUtils.getTcCode(msgRecord);
		if (szRcvTcCode == null) {
			szMsg = SZ_SESSION_NAME+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return JPlateYdConst.RETN_CD_TC_ERROR;
		}

        try {

        	szMsg = "["+ szOperationName +"] ---------------------- START :: " + msgRecord.toString();
    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

        	//=============================================================
        	// Log 테이블 등록
        	//=============================================================
        	szMsg = "[1후판정정] 크레인 권상실적등록 수신";
        	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        //파라미터 check
        	getParamRec.setField("YD_CRN_SCH_ID"		, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID"));
        	getParamRec.setField("YD_SCH_CD"        	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD"));
        	getParamRec.setField("MSG_ID"          		, ydDaoUtils.paraRecChkNull(msgRecord,"MSG_ID"));
        	getParamRec.setField("DATE"          		, ydDaoUtils.paraRecChkNull(msgRecord,"DATE"));
        	getParamRec.setField("TIME"          		, ydDaoUtils.paraRecChkNull(msgRecord,"TIME"));
        	getParamRec.setField("MSG_GP"          		, ydDaoUtils.paraRecChkNull(msgRecord,"MSG_GP"));
        	getParamRec.setField("YD_EQP_ID"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID"));
			getParamRec.setField("YD_EQP_WRK_MODE"    	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE"));
			getParamRec.setField("YD_WRK_PROG_STAT"   	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_WRK_PROG_STAT"));
			getParamRec.setField("YD_CRN_XAXIS"       	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_XAXIS"));
			getParamRec.setField("YD_CRN_YAXIS"       	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_YAXIS"));
			getParamRec.setField("YD_CRN_ZAXIS"       	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_ZAXIS"));
			getParamRec.setField("YD_UP_WR_LOC"   		, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LOC"));
			getParamRec.setField("YD_UP_WR_LAYER"		, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LAYER"));
			getParamRec.setField("MODIFIER"			    , ydDaoUtils.paraRecModifier(msgRecord));
			getParamRec.setField("YD_UP_WRK_ACT_GP"		, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WRK_ACT_GP"));


	        if (intRtnVal == -1) {
	        	szRtnMsg = "파라미터 Check중 Error : " + Integer.toString(intRtnVal);
                szMsg    = "[ " +szOperationName + "] " + szRtnMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
                throw new DAOException(szMsg);
	        }

	        szYdUpWrLoc = ydDaoUtils.paraRecChkNull(getParamRec, "YD_UP_WR_LOC");		//권상위치
    		szYdSchCd   = ydDaoUtils.paraRecChkNull(getParamRec, "YD_SCH_CD");			//스케쥴코드
	        szModifier  = ydDaoUtils.paraRecModifier(getParamRec);						//수정자

	        //대상 데이터 SELECT
	        getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
	        /* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnWrkMtl 
	        -- 크레인작업재료 조회 (야드L2작업지시 송신용)

	        SELECT Z.*
	          FROM
	        (
	            SELECT A.YD_CRN_SCH_ID                                  AS YD_CRN_SCH_ID
	                 , A.REGISTER                                       AS REGISTER
	                 , TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS')          AS REG_DDTT
	                 , A.MODIFIER                                       AS MODIFIER
	                 , TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS')          AS MOD_DDTT
	                 , A.DEL_YN                                         AS DEL_YN
	                 , A.YD_WBOOK_ID                                    AS YD_WBOOK_ID
	                 , A.YD_EQP_ID                                      AS YD_EQP_ID
	                 , A.YD_GP                                          AS YD_GP
	                 , A.YD_BAY_GP                                      AS YD_BAY_GP
	                 , A.YD_SCH_CD                                      AS YD_SCH_CD
	                 , A.YD_SCH_ST_GP                                   AS YD_SCH_ST_GP
	                 , A.YD_SCH_REQ_GP                                  AS YD_SCH_REQ_GP
	                 , A.YD_SCH_PRIOR                                   AS YD_SCH_PRIOR
	                 , A.YD_EQP_WRK_STAT                                AS YD_EQP_WRK_STAT
	                 , A.YD_WRK_PROG_STAT                               AS YD_WRK_PROG_STAT
	                 , A.YD_WBOOK_DT                                    AS YD_WBOOK_DT
	                 , TO_CHAR(A.YD_SCH_DT,     'YYYYMMDDHH24MISS')     AS YD_SCH_DT
	                 , TO_CHAR(A.YD_WORD_DT,    'YYYYMMDDHH24MISS')     AS YD_WORD_DT
	                 , TO_CHAR(A.YD_UP_CMPL_DT, 'YYYYMMDDHH24MISS')     AS YD_UP_CMPL_DT
	                 , TO_CHAR(A.YD_DN_CMPL_DT, 'YYYYMMDDHH24MISS')     AS YD_DN_CMPL_DT
	                 , A.YD_WRK_HDS_DD                                  AS YD_WRK_HDS_DD
	                 , A.YD_WRK_DUTY                                    AS YD_WRK_DUTY
	                 , A.YD_WRK_PARTY                                   AS YD_WRK_PARTY
	                 , A.YD_MAIN_WRK_MTL_SH                             AS YD_MAIN_WRK_MTL_SH
	                 , A.YD_AID_WRK_MTL_SH                              AS YD_AID_WRK_MTL_SH
	                 , A.YD_AID_WRK_UPDN_GP                             AS YD_AID_WRK_UPDN_GP
	                 , A.YD_TO_LOC_DCSN_MTD                             AS YD_TO_LOC_DCSN_MTD
	                 , A.YD_TO_LOC_GUIDE                                AS YD_TO_LOC_GUIDE
	                 , A.YD_EQP_WRK_SH                                  AS YD_EQP_WRK_SH
	                 , A.YD_EQP_WRK_WT                                  AS YD_EQP_WRK_WT
	                 , A.YD_EQP_WRK_T                                   AS YD_EQP_WRK_T
	                 , A.YD_EQP_WRK_MAX_W                               AS YD_EQP_WRK_MAX_W
	                 , A.YD_EQP_WRK_MAX_L                               AS YD_EQP_WRK_MAX_L
	                 , A.YD_CRN_SB_CTL_H                                AS YD_CRN_SB_CTL_H
	                 , A.YD_CRN_GRAB_USE_RULE_ID                        AS YD_CRN_GRAB_USE_RULE_ID
	                 , A.YD_UP_WO_LOC                                   AS YD_UP_WO_LOC
	                 , A.YD_UP_WO_LAYER                                 AS YD_UP_WO_LAYER
	                 , A.YD_UP_WO_LOC_XAXIS                             AS YD_UP_WO_LOC_XAXIS
	                 , A.YD_UP_WO_XAXIS_GAP_MAX                         AS YD_UP_WO_XAXIS_GAP_MAX
	                 , A.YD_UP_WO_XAXIS_GAP_MIN                         AS YD_UP_WO_XAXIS_GAP_MIN
	                 , A.YD_UP_WO_LOC_YAXIS                             AS YD_UP_WO_LOC_YAXIS
	                 , A.YD_UP_WO_LOC_YAXIS1                            AS YD_UP_WO_LOC_YAXIS1
	                 , A.YD_UP_WO_LOC_YAXIS2                            AS YD_UP_WO_LOC_YAXIS2
	                 , A.YD_UP_WO_YAXIS_GAP_MAX                         AS YD_UP_WO_YAXIS_GAP_MAX
	                 , A.YD_UP_WO_YAXIS_GAP_MIN                         AS YD_UP_WO_YAXIS_GAP_MIN
	                 , A.YD_UP_WO_LOC_ZAXIS                             AS YD_UP_WO_LOC_ZAXIS
	                 , A.YD_UP_WO_ZAXIS_GAP_MAX                         AS YD_UP_WO_ZAXIS_GAP_MAX
	                 , A.YD_UP_WO_ZAXIS_GAP_MIN                         AS YD_UP_WO_ZAXIS_GAP_MIN
	            --   , A.YD_DN_WO_LOC                                   AS YD_DN_WO_LOC
	            --   , A.YD_DN_WO_LAYER                                 AS YD_DN_WO_LAYER

	                 -- 권하지시위치
	                 , CASE WHEN A.YD_EQP_WRK_SH > 1 AND B.YD_STK_LOT_TP IS NOT NULL
	                                                 AND SUBSTR(A.YD_DN_WO_LOC,1,2) <> 'XX'
	                       THEN NVL((SELECT MAX(S.YD_STK_COL_GP || S.YD_STK_BED_NO)
	                                   FROM TB_YD_STKLYR S
	                                  WHERE S.STL_NO = B.STL_NO
	                                    AND S.YD_STK_LYR_MTL_STAT = 'D'
	                                    AND S.DEL_YN = 'N'
	                                ), (SUBSTR(A.YD_DN_WO_LOC,1,6) || B.YD_STK_LOT_TP))
	                       ELSE A.YD_DN_WO_LOC
	                   END                                              AS YD_DN_WO_LOC

	                 -- 권하지시단
	                 , CASE WHEN A.YD_EQP_WRK_SH > 1 AND B.YD_STK_LOT_TP IS NOT NULL
	                                                 AND SUBSTR(A.YD_DN_WO_LOC,1,2) <> 'XX'
	                       THEN NVL((SELECT MAX(S.YD_STK_LYR_NO)
	                                   FROM TB_YD_STKLYR S
	                                  WHERE S.STL_NO = B.STL_NO
	                                    AND S.YD_STK_LYR_MTL_STAT = 'D'
	                                    AND S.DEL_YN = 'N'
	                                ), B.YD_STK_LYR_NO)
	                       ELSE A.YD_DN_WO_LAYER
	                   END                                              AS YD_DN_WO_LAYER

	                 , A.YD_DN_WO_LOC_XAXIS                             AS YD_DN_WO_LOC_XAXIS
	                 , A.YD_DN_WO_XAXIS_GAP_MAX                         AS YD_DN_WO_XAXIS_GAP_MAX
	                 , A.YD_DN_WO_XAXIS_GAP_MIN                         AS YD_DN_WO_XAXIS_GAP_MIN
	                 , A.YD_DN_WO_LOC_YAXIS                             AS YD_DN_WO_LOC_YAXIS
	                 , A.YD_DN_WO_LOC_YAXIS1                            AS YD_DN_WO_LOC_YAXIS1
	                 , A.YD_DN_WO_LOC_YAXIS2                            AS YD_DN_WO_LOC_YAXIS2
	                 , A.YD_DN_WO_YAXIS_GAP_MAX                         AS YD_DN_WO_YAXIS_GAP_MAX
	                 , A.YD_DN_WO_YAXIS_GAP_MIN                         AS YD_DN_WO_YAXIS_GAP_MIN
	                 , A.YD_DN_WO_LOC_ZAXIS                             AS YD_DN_WO_LOC_ZAXIS
	                 , A.YD_DN_WO_ZAXIS_GAP_MAX                         AS YD_DN_WO_ZAXIS_GAP_MAX
	                 , A.YD_DN_WO_ZAXIS_GAP_MIN                         AS YD_DN_WO_ZAXIS_GAP_MIN
	                 , A.YD_UP_WR_LOC                                   AS YD_UP_WR_LOC
	                 , A.YD_UP_WR_LAYER                                 AS YD_UP_WR_LAYER
	                 , A.YD_UP_WRK_ACT_GP                               AS YD_UP_WRK_ACT_GP
	                 , A.YD_UP_WR_XAXIS                                 AS YD_UP_WR_XAXIS
	                 , A.YD_UP_WR_YAXIS                                 AS YD_UP_WR_YAXIS
	                 , A.YD_UP_WR_YAXIS1                                AS YD_UP_WR_YAXIS1
	                 , A.YD_UP_WR_YAXIS2                                AS YD_UP_WR_YAXIS2
	                 , A.YD_UP_WR_ZAXIS                                 AS YD_UP_WR_ZAXIS
	                 , A.YD_DN_WR_LOC                                   AS YD_DN_WR_LOC
	                 , A.YD_DN_WR_LAYER                                 AS YD_DN_WR_LAYER
	                 , A.YD_DN_WRK_ACT_GP                               AS YD_DN_WRK_ACT_GP
	                 , A.YD_DN_WR_XAXIS                                 AS YD_DN_WR_XAXIS
	                 , A.YD_DN_WR_YAXIS                                 AS YD_DN_WR_YAXIS
	                 , A.YD_DN_WR_YAXIS1                                AS YD_DN_WR_YAXIS1
	                 , A.YD_DN_WR_YAXIS2                                AS YD_DN_WR_YAXIS2
	                 , A.YD_DN_WR_ZAXIS                                 AS YD_DN_WR_ZAXIS
	                 , B.STL_NO                                         AS STL_NO
	                 , B.YD_AID_WRK_YN                                  AS YD_AID_WRK_YN
	            --   , B.YD_STK_LYR_NO                                  AS YD_STK_LYR_NO
	                 , B.YD_STK_LOT_TP                                  AS YD_STK_LOT_TP
	                 , B.YD_STK_LOT_CD                                  AS YD_STK_LOT_CD
	                 , B.HCR_GP                                         AS HCR_GP
	                 , B.STL_PROG_CD                                    AS STL_PROG_CD
	                 , B.YD_MTL_ITEM                                    AS YD_MTL_ITEM
	                 , B.YD_ROUTE_GP                                    AS YD_ROUTE_GP
	                 , B.YD_MTL_WT                                      AS YD_MTL_WT
	                 , B.YD_MTL_T                                       AS YD_MTL_T
	                 , B.YD_MTL_W                                       AS YD_MTL_W
	                 , B.YD_MTL_L                                       AS YD_MTL_L
	                 -- 권상지시의 적치단
	                 ,(SELECT MAX(S.YD_STK_LYR_NO)
	                     FROM TB_YD_STKLYR S
	                    WHERE S.STL_NO = B.STL_NO
	                      AND S.YD_STK_LYR_MTL_STAT IN ('C','U')
	                      AND S.YD_STK_COL_GP LIKE A.YD_GP || '%'
	                  )                                                 AS YD_STK_LYR_NO
	                 -- 파일링코드 RTBOOK-IN 일때만 SET : FCRT01UM
	                 , CASE WHEN (SUBSTR(A.YD_SCH_CD,3,2) || SUBSTR(A.YD_SCH_CD,7,2)) = 'RTUM' THEN
	                            NVL((SELECT MAX(S.YD_PILING_CD)
	                                   FROM TB_YD_STOCK S
	                                  WHERE S.STL_NO = B.STL_NO
	                                    AND S.DEL_YN = 'N'), 'NOPILING')
	                       ELSE ''
	                   END                                              AS YD_PILING_CD
	              FROM TB_YD_CRNSCH A
	                 ,(SELECT Y.YD_CRN_SCH_ID
	                        , Y.STL_NO
	                        , Y.YD_AID_WRK_YN
	                        , Y.YD_STK_LYR_NO
	                        , Y.YD_STK_LOT_TP
	                        , Y.YD_STK_LOT_CD
	                        , Y.HCR_GP
	                        , (SELECT CURR_PROG_CD FROM TB_PT_PLATECOMM WHERE PLATE_NO=X.STL_NO) AS STL_PROG_CD
	                        , Y.YD_MTL_ITEM
	                        , Y.YD_ROUTE_GP
	                        , X.YD_MTL_T
	                        , X.YD_MTL_W
	                        , X.YD_MTL_L
	                        , X.YD_MTL_WT
	                     FROM TB_YD_SHRSTOCK  X
	                        , TB_YD_CRNWRKMTL Y
	                     WHERE X.STL_NO = Y.STL_NO
	                  ) B
	             WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
	               AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
	        ) Z
	        ORDER BY Z.YD_DN_WO_LAYER, Z.YD_DN_WO_LOC, Z.STL_NO
	        */
	        
	        intRtnVal = ydCrnSchDao.getYdCrnWrkMtl(getParamRec, getRecSet);

	        if (intRtnVal <= 0) {
				szRtnMsg = "스케쥴 Data가 존재하지 않습니다. :: "+ Integer.toString(intRtnVal);
                szMsg    = "[ " +szOperationName + "] " + szRtnMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
	        }

	        //레코드셋의 사이즈값으로 ErrorCheck
	        if (getRecSet.size() == 0) {
				szRtnMsg = "no data found!!!, ErrorCode ::" + Integer.toString(intRtnVal);
                szMsg    = "[ " +szOperationName + "] " + szRtnMsg;
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
	        }

	        getRecSet.first();
	        getRecord = getRecSet.getRecord();

	        szStlNo			= ydDaoUtils.paraRecChkNull(getRecord, "STL_NO");					//재료번호
//	        szWbookId		= ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID");				//작업예약ID
	        szYdDnWoLoc		= ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC");				//권하지시위치
	        szYdWrkProgStat	= ydDaoUtils.paraRecChkNull(getRecord, "YD_WRK_PROG_STAT");
	        szYdUpWrkActGp	= ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WRK_ACT_GP");			//야드권상작업수행구분(YD_UP_WRK_ACT_GP) 항목사용 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)

	        //조회한 크레인 스케줄의 작업활성상태 체크 한다.
	        // 명령선택 상태일때만 권상실적 처리 가능하도록 변경
	        if ("1".equals(szYdWrkProgStat)) {

	        	// 적치단 정보 Clear (1개의 크레인스케줄에 잡혀있는 크레인작업재료의 정보를 모두 Check!)
	        	// 2후판 공통사용
	        	intRtnVal = this.clearYdStklyrY7(getRecSet, getParamRec);

	            //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
		        setCrnschRec = JDTORecordFactory.getInstance().create();
		        setCrnschRec.setField("YD_CRN_SCH_ID",      getParamRec.getFieldString("YD_CRN_SCH_ID"));
		        setCrnschRec.setField("YD_WRK_PROG_STAT",   "3");					// 3:권하지시
		        setCrnschRec.setField("YD_EQP_ID",     	   	getParamRec.getFieldString("YD_EQP_ID"));
		        setCrnschRec.setField("YD_UP_WR_LOC",       getParamRec.getFieldString("YD_UP_WR_LOC"));
		        setCrnschRec.setField("YD_UP_WR_LAYER",     getParamRec.getFieldString("YD_UP_WR_LAYER"));
		        setCrnschRec.setField("YD_UP_WRK_ACT_GP",   szYdUpWrkActGp);									// 야드권상작업수행구분(YD_UP_WRK_ACT_GP) 항목사용 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)
		        setCrnschRec.setField("YD_UP_WR_XAXIS",     getParamRec.getFieldString("YD_CRN_XAXIS"));
		        setCrnschRec.setField("YD_UP_WR_YAXIS",     getParamRec.getFieldString("YD_CRN_YAXIS"));
		        setCrnschRec.setField("YD_UP_WR_ZAXIS",     getParamRec.getFieldString("YD_CRN_ZAXIS"));
		        setCrnschRec.setField("YD_UP_CMPL_DT",      JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));		// 권상완료일시
		        setCrnschRec.setField("MODIFIER",			szModifier);
		        /* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updCrnUpWr 

		        UPDATE TB_YD_CRNSCH
		           SET
		        	   MODIFIER         = :V_MODIFIER
		             , MOD_DDTT         = SYSDATE
		             , YD_WRK_PROG_STAT = NVL(:V_YD_WRK_PROG_STAT, YD_WRK_PROG_STAT)    -- NULL일때 전 상태값 유지
		             , YD_EQP_ID     	= :V_YD_EQP_ID
		             , YD_UP_WR_LOC     = :V_YD_UP_WR_LOC
		             , YD_UP_WR_LAYER   = :V_YD_UP_WR_LAYER
		             , YD_UP_WRK_ACT_GP = :V_YD_UP_WRK_ACT_GP
		             , YD_UP_WR_XAXIS   = :V_YD_UP_WR_XAXIS
		             , YD_UP_WR_YAXIS   = :V_YD_UP_WR_YAXIS
		             , YD_UP_WR_ZAXIS   = :V_YD_UP_WR_ZAXIS
		             , YD_UP_CMPL_DT    = TO_DATE(:V_YD_UP_CMPL_DT, 'YYYYMMDDHH24MISS')
		         WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
		        */ 
				intRtnVal = ydCrnschDao.updCrnUpWr(setCrnschRec);
		        if (intRtnVal <= 0) {
		        	szRtnMsg = "크레인 스케줄의 업데이트 UPDATE 처리시 오류 발생." + Integer.toString(intRtnVal);
	                szMsg    = "[ " +szOperationName + "] " + szRtnMsg;
				 	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					return szRtnMsg;
		        }

		        //설비Table의 상태 변경 (권상완료상태로 변경)
		        setCrnschRec = JDTORecordFactory.getInstance().create();
		        setCrnschRec.setField("YD_EQP_ID",			getParamRec.getFieldString("YD_EQP_ID"));
			    setCrnschRec.setField("YD_EQP_STAT",        JPlateYdConst.YD_EQP_STAT_UP_CMPL);					// 2 : 권상완료
		        setCrnschRec.setField("MODIFIER",			szModifier);

		        /* com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.updYdEqpStat 

		        UPDATE TB_YD_EQP
		           SET MODIFIER    = :V_MODIFIER
		              ,MOD_DDTT    = SYSDATE
		              ,YD_EQP_STAT = :V_YD_EQP_STAT
		         WHERE YD_EQP_ID   = :V_YD_EQP_ID
		           AND DEL_YN      = 'N'
		        */	   
		        intRtnVal = ydEqpDao.updYdEqpStat(setCrnschRec);
				if (intRtnVal <= 0) {
		        	szRtnMsg = "설비상태 UPDATE 처리시 오류 발생." + Integer.toString(intRtnVal);
	                szMsg    = "[ " +szOperationName + "] " + szRtnMsg;
				 	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				 	return szRtnMsg;
	    		}

		        //------------------------------------------------------------------
			    //대차 및 차량 스케줄 이송재료 Handling
				//강력교정기 프로젝트 대차 없음
//	            if ("TC".equals(szYdUpWrLoc.substring(2, 4))) {
//
//	            	// 1후판정정야드는 대차이송재료 테이블 사용안함
//	            	if ("P".equals(szYdUpWrLoc.substring(0, 1))) {
//	            	
//		            	//대차스케쥴 이송재료 정보 셋팅.
//		            	this.setYdTcarY7(getRecSet);
//	
//		            	szMsg = "권상시 대차이송재료 삭제 완료";
//			            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
//					
//	            	}	
//	            	szTcarEqpId = szYdUpWrLoc.substring(0,1) + "X" + szYdUpWrLoc.substring(2,6);
//
//	            	//대차스케줄 호출
//	            	recSendMsg = JDTORecordFactory.getInstance().create();
//	            	recSendMsg.setField("MSG_ID", 			"YDYDJ");				// YDYDJ520
//	            	recSendMsg.setField("YD_LD_UD_GP", 		"U");
//	            	recSendMsg.setField("YD_EQP_ID", 		szTcarEqpId);
//	            	recSendMsg.setField("YD_WBOOK_ID", 		szWbookId);
//	            	
//	            	ydEjbCon.trx("JPlateYdTcarSchSeEJB", 	"procY7TcarSch", recSendMsg);
//
//	            }

	            szMsg = "[권상실적처리] 권상지시위치[" + szYdUpWrLoc + "], " + "설비구분[" + szYdDnWoLoc.substring(2, 4) + "]";
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 1후판정정 야드L2 크레인작업실적응답 전송  - YDY2L005
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    	        recInTemp = JDTORecordFactory.getInstance().create();
    	        recInTemp.setField("MSG_ID", 			"YDY2L005");
    	        recInTemp.setField("YD_EQP_ID", 		getParamRec.getFieldString("YD_EQP_ID"));		//야드설비ID
    	        recInTemp.setField("YD_WRK_PROG_STAT", 	"2");				//야드작업진행상태 : 권상완료
    	        recInTemp.setField("YD_SCH_CD", 		getParamRec.getFieldString("YD_SCH_CD"));		//야드스케줄코드
    	        recInTemp.setField("YD_CRN_SCH_ID", 	getParamRec.getFieldString("YD_CRN_SCH_ID"));	//야드크레인스케줄ID
   	        	recInTemp.setField("YD_L2_WR_GP", 		"U");				//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
    	        recInTemp.setField("YD_L3_HD_RS_CD", 	"0000");			//야드L3처리결과코드

   	        	szSendMsg = ydDelegate.sendMsg(recInTemp);

    			szMsg = "[" + szOperationName + "] 1후판정정야드L2 크레인작업실적응답[YDY2L005] 전송 완료>>>>"+szSendMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        } else {
	            szMsg = "야드작업진행상태 체크 오류 .. YD_WRK_PROG_STAT :: " + szYdWrkProgStat;
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

	            szRtnMsg = "명령선택후 권상처리 가능!. 현재상태:" + szYdWrkProgStat;
	            return szMsg;
	        }

	        szMsg = "["+ szOperationName +"] 권상 완료 실적 처리 완료";
	        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

//SJH16       // RT BOOK-OUT시 전문전송 (__RT__L_)
	        if (szYdSchCd.length() == 8 && "RT".equals(szYdSchCd.substring(2,4)) && ("L".equals(szYdSchCd.substring(6,7)))) {

		        szMsg = "["+ szOperationName +"] RT BOOK-OUT 실적 전송 .. 시작";
		        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    	        recInTemp = JDTORecordFactory.getInstance().create();
    	        
    	        /*
    	         * 2016.05.11 윤재광
    	         * I/F 타겟 분기처리
    	         * B동 전체와  PART13(T/F), PART9A,N,B(대차)인 경우는 열처리 L2, 그 외는 전단L2
    	         */
    	        if(szYdSchCd.substring(0,2).equals("PB")||szYdUpWrLoc.startsWith("PART13")||szYdUpWrLoc.startsWith("PART9") ) {
        	        recInTemp.setField("MSG_ID", 			"YDP3L501");          // 1 후판열처리L2
        	        recInTemp.setField("STL_NO",			szStlNo);			// 재료번호
        	        recInTemp.setField("OPERATION_TYPE",	"2");				// 1:Book In, 2:Book Out
        	        recInTemp.setField("YD_STK_COL_GP",		szYdUpWrLoc);		// FROM위치
        	        recInTemp.setField("YD_EQP_ID", 		getParamRec.getFieldString("YD_EQP_ID"));		//야드설비ID
       	        	szSendMsg = ydDelegate.sendMsg(recInTemp);

    		        szMsg = "["+ szOperationName +"] RT BOOK-OUT 실적 전송 .. 완료>>>>"+szSendMsg;
                    ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    	        } else {
//        	        recInTemp.setField("MSG_ID", 			"YDP2L501");          // 1 후판전단L2
//        	        recInTemp.setField("STL_NO",			szStlNo);			// 재료번호
//        	        recInTemp.setField("OPERATION_TYPE",	"2");				// 1:Book In, 2:Book Out
//        	        recInTemp.setField("YD_STK_COL_GP",		szYdUpWrLoc);		// FROM위치
  
        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSend("2", szStlNo, szYdUpWrLoc,getParamRec.getFieldString("YD_EQP_ID"));

    		        szMsg = "["+ szOperationName +"] 1 후판전단L2 RT BOOK-OUT 실적 전송 .. 완료>>>>"+szSendMsg;
                    ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
    	        }
    	        
	        }


	    } catch(Exception e) {
	    //  m_ctx.setRollbackOnly();
			throw new DAOException(getClass().getName() + e.getMessage(),e);
	    }

    	szMsg = "["+ szOperationName +"] 권상 완료 실적 처리 >>>> END ";
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    return JPlateYdConst.RETN_CD_SUCCESS;
    }// end of procY2CrnUpWr()
    
    
	/**
     *  오퍼레이션명 : 1후판정정야드 강제권상요구 (Y2YDL010)
     *
     *  @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     *  @param msgRecord
     *  @return JDTORecord
     *  @throws DAOException
	*/
	public JDTORecord procY2OffCrnUpWr(JDTORecord msgRecord) throws DAOException {

		JDTORecord setRecord 		= JDTORecordFactory.getInstance().create();
		String	szRtnMsg			= "";
        String 	szMsg         		= "";
        String 	szMethodName  		= "procY2OffCrnUpWr";
        String	szOperationName		= "1후판정정야드 강제권상요구";

        int 	intRtnVal 			= 0;

		String 	szYdEqpId    		= ""; 		// 야드설비ID
		String 	szYdUpWrLoc  		= ""; 		// 야드권상실적위치
		String 	szYdCrnXaxis 		= ""; 		// 야드크레인X축
		String 	szYdCrnYaxis 		= ""; 		// 야드크레인Y축
		String	szYdEqpWrkSh		= ""; 		// 야드크레인작업매수
		String 	szModifier   		= ""; 		// 수정자(Backup Only)
		int		iYdEqpWrkSh			= 0;
		int		oUpCnt				= 0;		// 권상예약건수
		int		nExistCnt			= 0;		// 상단 보조작업 존재 건수

		String 	szStlNo         	= ""; 		// 권상가능 재료번호
		String	szYdStkBedNo		= "";		// 야드적치베드번호
		String 	szYdStkLyrNo    	= ""; 		// 야드적치단번호
		String 	szXaxisYn       	= ""; 		// X좌표 정합성여부
		String 	szYaxisYn       	= ""; 		// Y좌표 정합성여부
		String 	szYdStkBedXaxis 	= ""; 		// 야드적치BedX축
		String 	szYdStkBedYaxis 	= ""; 		// 야드적치BedY축
		String 	szYdMtlW        	= ""; 		// 야드재료폭
		String 	szYdMtlL        	= ""; 		// 야드재료길이
		String 	szYdMtlWt       	= ""; 		// 야드재료중량
		String 	szWmCnt         	= ""; 		// 작업예약재료 건수
		String	arrStlNo[]			= {"","","","","", "","","","","", "","","","",""};
		String	szStlNoList			= "";

        String	szYD_UP_WO_LOC		= "";
        String	szYD_DN_WO_LOC		= "";
		String	szYD_UP_STK_COL_GP 	= "";
		String	szYD_UP_STK_BED_NO	= "";
		String	szYD_DN_STK_COL_GP	= "";
		String	szYD_DN_STK_BED_NO	= "";

//		int		iSumMtlL 			= 0;
		double	dSumMtlT 			= 0;
		int		iSumMtlWt 			= 0;

        // 레코드 선언
		JDTORecord    recPara   = null;
    	JDTORecord    recInPara	= null;
    	JDTORecord    recCrnSch	= null;
    	JDTORecord    recTemp	= null;
		JDTORecordSet rsOffUp  	= null;
		JDTORecordSet rsResult	= null;

		JPlateYdCrnSchDAO     ydCrnSchDao  	  = new JPlateYdCrnSchDAO();		// 크레인스케쥴DAO
		JPlateYdEqpDAO        ydEqpDao		  = new JPlateYdEqpDAO();			// 야드설비(크레인)DAO
		JPlateYdWrkbookDAO    ydWrkbookDao 	  = new JPlateYdWrkbookDAO();		// 야드작업예약 DAO
		JPlateYdWrkbookMtlDAO ydWrkbookMtlDao = new JPlateYdWrkbookMtlDAO();	// 야드작업예약재료 DAO
		JPlateYdStockDAO	  ydStockDao      = new JPlateYdStockDAO();			// 야드저장품 DAO
		JPlateYdStkLyrDAO	  ydStkLyrDao	  = new JPlateYdStkLyrDAO();		// 야드적치단 DAO
		JPlateYdSchRuleDAO    ydSchRuleDao	  = new JPlateYdSchRuleDAO();		// 야드스케줄기준 DAO
		JPlateYdCrnWrkMtlDAO  ydCrnWrkMtlDao  = new JPlateYdCrnWrkMtlDAO();		// 야드크레인작업재료 DAO

		try {
			szMsg = "["+szOperationName+"] 강제권상 요구 .. Start >>>> " + msgRecord.toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			// YD_EQP_ID		야드설비ID
			// YD_UP_WR_LOC		야드권상실적위치		(6자리까지만 사용)
			// YD_UP_WR_LAYER	야드권상실적단			(무의미)
			// YD_CRN_XAXIS		야드크레인X축
			// YD_CRN_YAXIS		야드크레인Y축
			// YD_CRN_ZAXIS		야드크레인Z축
			// YD_EQP_WRK_SH	야드크레인작업매수
			// STL_NO1~15		재료번호1 ~ 15

			//수신 항목 값
			szYdEqpId    	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"); 			//야드설비ID
			szYdUpWrLoc  	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LOC"); 		//야드권상실적위치
			szYdCrnXaxis 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS"); 		//야드크레인X축
			szYdCrnYaxis 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS"); 		//야드크레인Y축
			szYdEqpWrkSh	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_SH", "1"); 	//야드크레인작업매수
			
			for(int ii=0; ii<15; ii++) {
				arrStlNo[ii] = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"+(ii+1));		//강제권상 재료번호 1~15
			}
			szModifier   	= ydDaoUtils.paraRecModifier(msgRecord); 						//수정자(Backup Only)

			//크레인작업실적응답 전문 생성용
			String 	ydL3HdRsCd 	= ""; //야드L3처리결과코드
			String 	ydL3Msg    	= ""; //야드L3MESSAGE

			setRecord.setField("YD_EQP_ID", 		szYdEqpId); 						//야드설비ID
			setRecord.setField("YD_L2_WR_GP", 		"J"    ); 						 	//야드L2실적구분(지시요구)
			setRecord.setField("YD_L3_HD_RS_CD", 	"FU99" ); 						 	//야드L3처리결과코드(Error)
			setRecord.setField("YD_L3_MSG", 		"오류:강제권상요구 예상치 못한 오류"); 		//야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(szYdEqpId)) {
				ydL3HdRsCd = "FU01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (szYdEqpId.length() < 6) {
				ydL3HdRsCd = "FU02";
				ydL3Msg    = "오류:설비ID[" + szYdEqpId + "] 이상";
			} else if (szYdUpWrLoc.length() < 8) {
				ydL3HdRsCd = "FU04";
				ydL3Msg    = "오류:권상위치 이상";
			} else if (!szYdEqpId.substring(0, 2).equals(szYdUpWrLoc.substring(0, 2))) {
				ydL3HdRsCd = "FU05";
				ydL3Msg    = "오류:설비-권상위치[" + szYdEqpId.substring(0, 2) + "-" + szYdUpWrLoc.substring(0, 2) + "] 부적합";
			} else if ("".equals(szYdCrnXaxis)) {
				ydL3HdRsCd = "FU06";
				ydL3Msg    = "오류:크레인X축 없음";
			} else if ("".equals(szYdCrnYaxis)) {
				ydL3HdRsCd = "FU07";
				ydL3Msg    = "오류:크레인Y축 없음";
			}

			// 작업매수 MAX 15건 체크 
			iYdEqpWrkSh = Integer.parseInt(szYdEqpWrkSh);
			if (iYdEqpWrkSh < 1 || iYdEqpWrkSh > 15) {
				ydL3HdRsCd = "FU09";
				ydL3Msg    = "오류:작업매수 [" + szYdEqpWrkSh + "]";
			}
			for(int ii=1; ii<=iYdEqpWrkSh; ii++) {
				szStlNo = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"+ii); 	//재료번호
				if ("".equals(szStlNo)) {
					ydL3HdRsCd = "FU09";
					ydL3Msg    = "오류:작업매수(재료번호) [" + szYdEqpWrkSh + "]";
					break;
				}
			}

			if (!"".equals(ydL3Msg)) {
				setRecord.setField("YD_L3_HD_RS_CD", 	ydL3HdRsCd); 			//야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		ydL3Msg   ); 			//야드L3MESSAGE
				setRecord.setField("RTN_CD",			JPlateYdConst.RETN_CD_FAILURE);
				return setRecord;
			}

			/**********************************************************
			* 2. 권상위치, 좌표값, 이적 재료 등을 Check
				 YD_EQP_ID		//야드설비ID
				 YD_UP_WR_LOC	//야드권상실적위치
				 YD_UP_WR_LAYER	//야드권상실적단
				 YD_CRN_XAXIS	//야드크레인X축
				 YD_CRN_YAXIS	//야드크레인Y축
				 YD_EQP_WRK_SH	//야드크레인작업매수
				 STL_NO1~15		//재료번호1 ~ 15

			**********************************************************/
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_EQP_ID", 		szYdEqpId); 							//야드설비ID
			recPara.setField("YD_STK_COL_GP", 	ydUtils.substr(szYdUpWrLoc, 0, 6)); 	//야드적치열구분
			recPara.setField("YD_STK_BED_NO", 	ydUtils.substr(szYdUpWrLoc, 6, 2)); 	//야드적치Bed번호
			recPara.setField("YD_CRN_XAXIS", 	szYdCrnXaxis); 							//야드크레인X축
			recPara.setField("YD_CRN_YAXIS", 	szYdCrnYaxis); 							//야드크레인Y축
			recPara.setField("YD_EQP_WRK_SH", 	szYdEqpWrkSh);							//야드크레인작업매수
			
			for(int ii=0; ii<15; ii++) {
				recPara.setField("STL_NO"+(ii+1), arrStlNo[ii]); 						// 강제권상 재료번호 1~15
				if ("".equals(szStlNoList)) {
					szStlNoList = arrStlNo[ii];
				} else {
					szStlNoList = szStlNoList + ";" + arrStlNo[ii];
				}
			}
			recPara.setField("ARR_STL_NO",		szStlNoList); 							// 강제권상 재료번호 LIST

			//좌표값 에 해당하는 재료정보 조회
			rsOffUp   = JDTORecordFactory.getInstance().createRecordSet("yd");
			/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getOffCrnUpBed
			-- 강제권상요구 Bed정보 조회

			WITH TEMP_TABLE AS (

			    SELECT ZZ.*
			         , SUM(CASE WHEN ZZ.EXIST_YN = 'Y' THEN ZZ.YD_MTL_L  ELSE 0 END) OVER(PARTITION BY ZZ.YD_STK_LYR_NO) AS SUM_MTL_L
			         , SUM(CASE WHEN ZZ.EXIST_YN = 'Y' THEN ZZ.YD_MTL_T  ELSE 0 END) OVER(PARTITION BY ZZ.YD_STK_BED_NO) AS SUM_MTL_T
			         , SUM(CASE WHEN ZZ.EXIST_YN = 'Y' THEN ZZ.YD_MTL_WT ELSE 0 END) OVER()                              AS SUM_MTL_WT
			      FROM
			    (
			        SELECT Z.*
			             , CASE WHEN INSTR(:V_ARR_STL_NO, Z.OCPY_STL_NO) > 0 THEN 'Y' ELSE 'N' END EXIST_YN
			          FROM
			        (
			            SELECT B.YD_STK_COL_GP
			                 , B.YD_STK_BED_NO
			                 , C.YD_STK_LYR_NO
			                 , B.YD_STK_BED_XAXIS
			                 , B.YD_STK_BED_YAXIS
			                 , B.YD_STK_BED_XAXIS_TOL
			                 , B.YD_STK_BED_YAXIS_TOL
			                 , C.STL_NO
			                 , C.YD_STK_LYR_ACT_STAT
			                 , C.YD_STK_LYR_MTL_STAT
			                 -- 점유베드 재료정보
			                 , CASE WHEN C.STL_NO IS NULL AND C.YD_STK_LYR_MTL_STAT <> 'E' AND C.YD_OCPY_BED_GP = 'V' THEN
			                           ( SELECT MAX(X.STL_NO)
			                               FROM TB_YD_STKLYR X
			                              WHERE X.YD_STK_COL_GP = C.YD_STK_COL_GP
			                                AND X.YD_STK_BED_NO = C.YD_OCPY_STK_BED_NO
			                                AND X.YD_STK_LYR_NO = C.YD_OCPY_STK_LYR_NO
			                           )
			                        ELSE C.STL_NO
			                   END  AS OCPY_STL_NO
			                 , A.YD_STK_COL_L
			                 , NVL(D.YD_MTL_L, 6)           AS YD_MTL_L     -- 정보이상재일때 디폴트값 SET
			                 , NVL(D.YD_MTL_T, 3000)        AS YD_MTL_T
			                 , D.YD_MTL_WT
			              FROM TB_YD_STKCOL     A
			                 , TB_YD_STKBED     B
			                 , TB_YD_STKLYR     C
			                 , TB_YD_SHRSTOCK   D
			             WHERE A.YD_STK_COL_GP  = :V_YD_STK_COL_GP
			               AND A.YD_STK_COL_GP  = B.YD_STK_COL_GP
			               AND B.YD_STK_COL_GP  = C.YD_STK_COL_GP
			               AND B.YD_STK_BED_NO  = C.YD_STK_BED_NO
			               AND C.YD_STK_LYR_MTL_STAT <> 'E'
			               AND C.STL_NO         = D.STL_NO
			               AND A.DEL_YN         = 'N'
			               AND B.DEL_YN         = 'N'
			               AND C.DEL_YN         = 'N'
			               AND D.DEL_YN         = 'N'
			             ORDER BY C.YD_STK_LYR_NO DESC, C.YD_STK_BED_NO
			        ) Z
			    ) ZZ
			)
			SELECT SZ.STL_NO                                -- 권상가능 재료번호
			     , SZ.YD_STK_COL_GP
			     , SZ.YD_STK_BED_NO
			     , SZ.YD_STK_LYR_NO
			     , ST.YD_MTL_W
			     , ST.YD_MTL_L
			     , ST.YD_MTL_WT
			     , (SELECT COUNT(*)
			          FROM TB_YD_WRKBOOK    WB
			             , TB_YD_WRKBOOKMTL WM
			         WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
			           AND WM.STL_NO      = ST.STL_NO
			           AND WM.DEL_YN      = 'N'
			           AND WB.DEL_YN      = 'N') AS WM_CNT
			     ,  CASE WHEN ABS(SZ.YD_STK_BED_XAXIS - TO_NUMBER(:V_YD_CRN_XAXIS)) <= SZ.YD_STK_BED_XAXIS_TOL
			           THEN 'Y' ELSE 'N' END            AS XAXIS_YN     -- X좌표 정합성여부
			     , CASE WHEN ABS(SZ.YD_STK_BED_YAXIS - TO_NUMBER(:V_YD_CRN_YAXIS)) <= SZ.YD_STK_BED_YAXIS_TOL
			            THEN 'Y' ELSE 'N' END           AS YAXIS_YN     -- Y좌표 정합성여부
			     , SZ.YD_STK_BED_XAXIS
			     , SZ.YD_STK_BED_YAXIS
			     , (SELECT COUNT(1)
			          FROM TB_YD_STKLYR S
			         WHERE S.YD_STK_COL_GP = SZ.YD_STK_COL_GP
			           AND S.YD_STK_LYR_MTL_STAT = 'U'
			           AND S.STL_NO IS NOT NULL
			       )                                    AS O_UP_CNT     -- 권상 예약 건수
			     , MAX(SZ.N_EXIST_CNT) OVER()           AS N_EXIST_CNT  -- 상단에 작업 비대상 재료존재 건수
			     , SZ.YD_STK_COL_L
			     , SZ.SUM_MTL_L
			     , SZ.SUM_MTL_T
			     , SZ.SUM_MTL_WT
			  FROM
			       (SELECT Z.YD_STK_COL_GP
			             , Z.YD_STK_BED_NO
			             , Z.YD_STK_BED_XAXIS
			             , Z.YD_STK_BED_YAXIS
			             , Z.YD_STK_BED_XAXIS_TOL
			             , Z.YD_STK_BED_YAXIS_TOL
			             , Z.YD_STK_LYR_NO
			             , Z.STL_NO
			             , Z.YD_STK_COL_L
			             , Z.SUM_MTL_L
			             , Z.SUM_MTL_T
			             , Z.SUM_MTL_WT
			             , (SELECT COUNT(1)
			                  FROM TEMP_TABLE S
			                 WHERE S.YD_STK_COL_GP = Z.YD_STK_COL_GP
			                   AND S.YD_STK_BED_NO = Z.YD_STK_BED_NO
			                   AND S.YD_STK_LYR_NO > Z.YD_STK_LYR_NO
			                   AND S.EXIST_YN      = 'N'
			               )                            AS N_EXIST_CNT  -- 상단에 작업 비대상 재료존재 건수
			          FROM TEMP_TABLE Z
			         WHERE Z.YD_STK_LYR_ACT_STAT = 'E'                  -- 활성상태
			           AND Z.YD_STK_LYR_MTL_STAT IN ('C', 'U')          -- 적치중, 권상예약
			           AND Z.STL_NO IS NOT NULL
			           AND Z.EXIST_YN = 'Y'
			       ) SZ
			     , TB_YD_SHRSTOCK  ST
			 WHERE SZ.STL_NO = ST.STL_NO(+)

			 */
			intRtnVal = ydCrnSchDao.getOffCrnUpBed(recPara, rsOffUp);

			if (intRtnVal <= 0) {
				//FROM위치에 대상재료 미존재
				ydL3HdRsCd = "FU11";
				ydL3Msg    = "오류:FROM위치 대상 재료 미존재";
			} else if (intRtnVal < Integer.parseInt(szYdEqpWrkSh)) {
				//FROM위치에 대상재료 부족
				ydL3HdRsCd = "FU12";
				ydL3Msg    = "오류:FROM위치 대상 재료 부족";
			} else {

				if (rsOffUp != null && rsOffUp.size() > 0) {
					szStlNo         = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "STL_NO");
					szYdStkLyrNo    = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "YD_STK_LYR_NO");
					szXaxisYn       = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "XAXIS_YN");
					szYaxisYn       = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "YAXIS_YN");
					szYdStkBedXaxis = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "YD_STK_BED_XAXIS");
					szYdStkBedYaxis = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "YD_STK_BED_YAXIS");
					szYdMtlW        = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "YD_MTL_W");
					szYdMtlL        = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "YD_MTL_L");
					szYdMtlWt       = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "YD_MTL_WT");
					szWmCnt         = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "WM_CNT");
					oUpCnt 			= ydDaoUtils.paraRecChkNullInt(rsOffUp.getRecord(0), "O_UP_CNT");
					nExistCnt 		= ydDaoUtils.paraRecChkNullInt(rsOffUp.getRecord(0), "N_EXIST_CNT");		// 상단에 보조작업 (작업 비대상 재료) 존재 건수
				}

				if (!"Y".equals(szXaxisYn)) {
					//권상위치의 BedX축 허용오차 값내에 크레인X축 값 존재여부를 Check
					ydL3HdRsCd = "FU13";
					ydL3Msg    = "오류:크레인X축[" + szYdStkBedXaxis + ":" + szYdCrnXaxis + "] 이상";
				} else if (!"Y".equals(szYaxisYn)) {
					//권상위치의 BedY축 허용오차 값내에 크레인Y축 값 존재여부를 Check
					ydL3HdRsCd = "FU14";
					ydL3Msg    = "오류:크레인Y축[" + szYdStkBedYaxis + ":" + szYdCrnYaxis + "] 이상";
				} else if ("".equals(szStlNo)) {
					//권상위치에 적치된 이적 가능 재료 존재여부를 Check
					ydL3HdRsCd = "FU15";
					ydL3Msg    = "오류:권상가능 재료 없음[" + szYdUpWrLoc + "]";
				} else if ("".equals(szYdMtlW) || "".equals(szYdMtlL) || "".equals(szYdMtlWt)) {
					//이적 재료 저장품 Table 존재여부 및  Size 정상여부를 Check
					ydL3HdRsCd = "FU16";
					ydL3Msg    = "오류:[" + szStlNo + "] 저장품정보 이상";
				} else if (!"".equals(szWmCnt) && Integer.parseInt(szWmCnt) > 0) {
					//이적 재료 작업예약재료 Table 존재여부를 Check
					ydL3HdRsCd = "FU17";
					ydL3Msg    = "오류:[" + szStlNo + "] 작업예약 기등록";
				} else if (oUpCnt > 0) {
					//권상위치의 권상예약정보 존재여부 체크
					ydL3HdRsCd = "FU18";
					ydL3Msg    = "오류:권상예약 정보 존재로 오류발생!";
				} else if (nExistCnt > 0) {
					//FROM위치 상단에 보조작업 존재
					ydL3HdRsCd = "FU19";
					ydL3Msg    = "오류:FROM위치 상단에 보조작업 존재";
				}
				
				String sNEW_MODULE_EFF_YN = "N";
				
				JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
				
				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A020"); //1후판정정야드 강제권상 두께체크 신규모듈적용 여부  
				
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 강제권상 두께체크 신규모듈적용 여부  : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
				
				if(sNEW_MODULE_EFF_YN.equals("Y")) {
					
				    //신규 방식 -> 단이 틀린경우만 두께 체크를 한다.
					if (rsOffUp.size() > 0) {
						
						String szYdStkLyrNoBase = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(0), "YD_STK_LYR_NO");
						String szYdStkBedNoBase = "";
						int    iBedCnt = 0;
						boolean isSameLayer = true; 
						
						for(int ii=0; ii<rsOffUp.size(); ii++) {

							if(!szYdStkLyrNoBase.equals(ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(ii), "YD_STK_LYR_NO"))){
								
								szYdStkBedNoBase = ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(ii), "YD_STK_BED_NO");
								iBedCnt = 0;
								
								
								for(int jj=0; jj<rsOffUp.size(); jj++) {
									
									if(szYdStkBedNoBase.equals(ydDaoUtils.paraRecChkNull(rsOffUp.getRecord(jj), "YD_STK_BED_NO"))) {
										iBedCnt++;
									}
								}
								
								szMsg = "["+szMethodName+"] 두께체크 >>>> BED:" + szYdStkBedNoBase + ","+iBedCnt;
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
								
								if(iBedCnt>1) {
									isSameLayer = false; //단이 틀리다. 동일 BED
									break;
								}
							}
						}
						
						for(int ii=0; ii<rsOffUp.size(); ii++) {
							rsOffUp.absolute(ii+1);
							recTemp	= rsOffUp.getRecord();

							dSumMtlT 	= ydDaoUtils.paraRecChkNullDouble(recTemp, 	"SUM_MTL_T");
							iSumMtlWt 	= ydDaoUtils.paraRecChkNullInt   (recTemp, 	"SUM_MTL_WT");

							// 재료두께 50T 넘는 재료는 강제권상 불가 (단이 틀린 경우->파일링, 멀티작업) + 파일링 50t 기준 해제 허용 경우 추가
							/*	1. 파일링 50t 기준 해제 허용 크레인권상위치--- PBRT4N01(쇼트입측), PART1301(체인트렌스퍼출측)
							 *	2. 파일링 50t 기준 해제 허용 크레인번호------ 열처리 크레인 PM44,PM20  강력교정 크레인  PM55,PM54
							 *	3. 파일링 50t 기준 해제 허용 스케줄코드------ 강제권상시에는 스케줄코드 없음
							 *	4. 변경 파일링 기준 ( 50 -> 170 ) ------ 강력교정 최대두께 80t 2매 + α) ,  상기위치외 파일링기준 현재와 동일
							 *	5. 파일링제한 해제 대상 :	강제권상시 
							 */
							if (	("PBRT4N01".equals(szYdUpWrLoc) || "PART1301".equals(szYdUpWrLoc))
								&&  ("PACRA1".equals(szYdEqpId) || "PACRA2".equals(szYdEqpId) || "PBCRB1".equals(szYdEqpId) || "PBCRB2".equals(szYdEqpId))
//								&&  ("PBRT40LM".equals(szYdSchCd) || "PART13LM".equals(szYdSchCd))
								){
								if(dSumMtlT > 170 && !isSameLayer){
									ydL3HdRsCd = "FU32";
									ydL3Msg =		"권상위치 : " + szYdUpWrLoc
												+ ", 크레인 : "  + szYdEqpId
												+ ", 재료두께 합이 170t 넘어 파일링작업 불가! [" + dSumMtlT + "]";
									break;	
								}
							} else if(dSumMtlT > 50 && !isSameLayer) {
									ydL3HdRsCd = "FU32";
									ydL3Msg    = "재료두께 합 50t 넘어 작업불가! [" + dSumMtlT + "]";
									break;
							}
							
							if("PECRE2".equals(szYdEqpId)) {
								// 35톤 넘는 재료는 강제권상 불가 -- 2019.02.22 E2 크레엔 35t까지 가능
								if (iSumMtlWt > 35000) {
									ydL3HdRsCd = "FU33";
									ydL3Msg    = "재료중량 합 35톤 넘어 작업불가! [" + iSumMtlWt + "]";
									break;
								}
							} else {
								// 25톤 넘는 재료는 강제권상 불가
								if (iSumMtlWt > 25000) {
									ydL3HdRsCd = "FU33";
									ydL3Msg    = "재료중량 합 25톤 넘어 작업불가! [" + iSumMtlWt + "]";
									break;
								}
							}
						}
					}				
					
				} else {
					
					//이전 방식
					if (rsOffUp.size() > 0) {
						for(int ii=0; ii<rsOffUp.size(); ii++) {
							rsOffUp.absolute(ii+1);
							recTemp	= rsOffUp.getRecord();

							dSumMtlT 	= ydDaoUtils.paraRecChkNullDouble(recTemp, 	"SUM_MTL_T");
							iSumMtlWt 	= ydDaoUtils.paraRecChkNullInt   (recTemp, 	"SUM_MTL_WT");

							// 재료두께 50T 넘는 재료는 강제권상 불가 (1건이상시)
							if (dSumMtlT > 50 && rsOffUp.size() > 1) {
								ydL3HdRsCd = "FU32";
								ydL3Msg    = "재료두께 합 50t 넘어 작업불가! [" + dSumMtlT + "]";
								break;
							}

							if("PECRE2".equals(szYdEqpId)) {
								// 35톤 넘는 재료는 강제권상 불가 -- 2019.02.22 E2 크레엔 35t까지 가능
								if (iSumMtlWt > 35000) {
									ydL3HdRsCd = "FU33";
									ydL3Msg    = "재료중량 합 35톤 넘어 작업불가! [" + iSumMtlWt + "]";
									break;
								}
							} else {
							
								// 25톤 넘는 재료는 강제권상 불가
								if (iSumMtlWt > 25000) {
									ydL3HdRsCd = "FU33";
									ydL3Msg    = "재료중량 합 25톤 넘어 작업불가! [" + iSumMtlWt + "]";
									break;
								}
							}
						}
					}
					
				}
				
			}

			if (!"".equals(ydL3Msg)) {
				setRecord.setField("YD_L3_HD_RS_CD", 	ydL3HdRsCd); 			//야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		ydL3Msg); 				//야드L3MESSAGE
				setRecord.setField("RTN_CD",			JPlateYdConst.RETN_CD_FAILURE);
				return setRecord;
			}

			/**********************************************************
			* 3. 설비상태, 설비사양, 크레인스케줄 존재여부 등을 Check
			**********************************************************/
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
			/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getOffCrnUpStat 

			SELECT EQ.YD_EQP_STAT
			      ,EQ.YD_EQP_WRK_MODE
			      ,SP.YD_WRK_ABLE_L
			      ,SP.YD_WRK_ABLE_W
			      ,SP.YD_WRK_ABLE_WT
			      ,CS.YD_CRN_SCH_ID
			      ,CS.YD_WRK_PROG_STAT
			  FROM TB_YD_EQP     EQ
			      ,TB_YD_CRNSPEC SP
			      ,(SELECT YD_EQP_ID
			              ,YD_CRN_SCH_ID
			              ,YD_WRK_PROG_STAT
			          FROM TB_YD_CRNSCH
			         WHERE YD_EQP_ID = :V_YD_EQP_ID
			           AND YD_WRK_PROG_STAT IN ('1','2','3') --권상지시,권상완료,권하지시
			           AND DEL_YN = 'N') CS
			 WHERE EQ.YD_EQP_ID = SP.YD_EQP_ID(+)
			   AND EQ.YD_EQP_ID = CS.YD_EQP_ID(+)
			   AND EQ.YD_EQP_ID = :V_YD_EQP_ID
			   AND EQ.DEL_YN    = 'N'
			*/	   
			ydCrnSchDao.getOffCrnUpStat(recPara, rsResult);

			String ydEqpStat     = ""; //야드설비상태
			String ydEqpWrkMode  = ""; //야드설비작업Mode
			String ydCrnSchId    = ""; //야드크레인스케쥴ID(현재)
			String ydWrkAbleW    = ""; //야드작업가능폭
			String ydWrkAbleL    = ""; //야드작업가능길이
			String ydWrkAbleWt   = ""; //야드작업가능중량

			if (rsResult != null && rsResult.size() > 0) {
				ydEqpStat     = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_EQP_STAT"     );
				ydEqpWrkMode  = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_EQP_WRK_MODE" );
				ydCrnSchId    = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_CRN_SCH_ID"   );
				ydWrkAbleW    = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_ABLE_W"   );
				ydWrkAbleL    = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_ABLE_L"   );
				ydWrkAbleWt   = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_ABLE_WT"  );
			}

			szMsg = "["+szMethodName+"] 대상재료 >>>> " + rsOffUp.getRecord(0).toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szMsg = "["+szMethodName+"] 설비사양 >>>> " + rsResult.getRecord(0).toString();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if ("".equals(ydEqpStat)) {
				//설비 Table 정보 Check
				ydL3HdRsCd = "FU41";
				ydL3Msg    = "오류:크레인[" + szYdEqpId + "] 정보 없음";
			} else if ("B".equals(ydEqpStat)) {
				//설비 Table 설비상태 Check
				ydL3HdRsCd = "FU42";
				ydL3Msg    = "오류:크레인[" + szYdEqpId + "] 고장";
			} else if (!"1".equals(ydEqpWrkMode)) {
				//설비 Table 설비작업Mode Check
				ydL3HdRsCd = "FU43";
				ydL3Msg    = "오류:크레인[" + szYdEqpId + "] Off-Line";

			//   명령선택 이후에 강제권상 안됨
			} else if (!"W".equals(ydEqpStat) && !"4".equals(ydEqpStat)) {
				//설비 Table 설비상태 Check : 권하완료, 대기 외이면 불가
				ydL3HdRsCd = "FU46";
				ydL3Msg    = "오류:크레인[" + szYdEqpId + "] 상태[" + ydEqpStat + "] 부적합";

			} else if ("".equals(ydWrkAbleW) || "".equals(ydWrkAbleL) || "".equals(ydWrkAbleWt)) {
				//크레인사양 Table 정보의 이상여부를 Check
				ydL3HdRsCd = "FU47";
				ydL3Msg    = "오류:크레인[" + szYdEqpId + "] 사양 이상";
			} else if (Double.parseDouble(szYdMtlW) > Double.parseDouble(ydWrkAbleW)) {
				//이적 대상 재료 폭의 크레인사양폭 초과여부를 Check
				ydL3HdRsCd = "FU48";
				ydL3Msg    = "오류:크레인[" + szYdEqpId + "] 사양폭 초과";
			} else if (Double.parseDouble(szYdMtlL) > Double.parseDouble(ydWrkAbleL)) {
				//이적 대상 재료 길이의 크레인사양길이 초과여부를 Check
				ydL3HdRsCd = "FU49";
				ydL3Msg    = "오류:크레인[" + szYdEqpId + "] 사양길이 초과";
			} else if (Double.parseDouble(szYdMtlWt) > Double.parseDouble(ydWrkAbleWt)) {
				//이적 대상 재료 중량의 크레인사양중량 초과여부를 Check
				ydL3HdRsCd = "FU50";
				ydL3Msg    = "오류:크레인[" + szYdEqpId + "] 사양중량 초과";
			}

			if (!"".equals(ydL3Msg)) {
				setRecord.setField("YD_L3_HD_RS_CD", 	ydL3HdRsCd); 		//야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		ydL3Msg); 			//야드L3MESSAGE
				setRecord.setField("RTN_CD",			JPlateYdConst.RETN_CD_FAILURE);
				return setRecord;
			}

			/**********************************************************
			* 4. 스케줄기준 정보 Check
			**********************************************************/
			String ydGp          = ydUtils.substr(szYdUpWrLoc, 0, 1);		//야드구분
			String ydBayGp       = ydUtils.substr(szYdUpWrLoc, 1, 1); 		//야드동구분
			//야드스케쥴코드 : 야드+동+FU(강제권상)+설비호기+M(이적)+M(분할없음)
			String ydSchCd       = ydGp + ydBayGp + "FU0" + ydUtils.substr(szYdEqpId,5,1) + "MM";
			String ydSchProhExn  = "";  //야드스케쥴금지유무
			String ydWrkCrnPrior = "1"; //야드작업크레인우선순위

			recPara.setField("YD_SCH_CD", ydSchCd); //야드스케쥴코드

			//야드스케쥴금지유무 조회
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
			/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO.getYdSchrule 

			SELECT YD_SCH_CD  AS YD_SCH_CD
			      ,REGISTER  AS REGISTER
			      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
			      ,MODIFIER  AS MODIFIER
			      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
			      ,DEL_YN  AS DEL_YN
			      ,YD_GP  AS YD_GP
			      ,YD_BAY_GP  AS YD_BAY_GP
			      ,YD_SCH_RNG_CD  AS YD_SCH_RNG_CD
			      ,YD_SCH_WHIO_GP  AS YD_SCH_WHIO_GP
			      ,YD_SCH_DIV_GP  AS YD_SCH_DIV_GP
			      ,YD_SCH_RULE_ACT_STAT  AS YD_SCH_RULE_ACT_STAT
			      ,YD_WRK_CRN  AS YD_WRK_CRN
			      ,YD_WRK_CRN_PRIOR  AS YD_WRK_CRN_PRIOR
			      ,YD_ALT_CRN_YN  AS YD_ALT_CRN_YN
			      ,YD_ALT_CRN  AS YD_ALT_CRN
			      ,YD_ALT_CRN_PRIOR  AS YD_ALT_CRN_PRIOR
			      ,CD_CONTENTS  AS CD_CONTENTS
			      ,YD_SCH_PROH_EXN AS YD_SCH_PROH_EXN
			   FROM TB_YD_SCHRULE
			 WHERE YD_SCH_CD = :V_YD_SCH_CD
			*/ 
			ydSchRuleDao.getYdSchrule(recPara, rsResult);

			if (rsResult != null && rsResult.size() > 0) {
				ydSchProhExn  = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_SCH_PROH_EXN");
				//야드작업크레인우선순위 : 야드스케쥴코드에 해당하는 작업크레인의 우선순위이므로
				//실제 요청한 크레인의 우선순위와 다를 수 있음
				ydWrkCrnPrior = ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_WRK_CRN_PRIOR");
			}

			if ("".equals(ydSchProhExn)) {
				//스케줄기준 Table 정보 Check
				ydL3HdRsCd = "FU71";
				ydL3Msg    = "오류:스케쥴코드[" + ydSchCd + "] 정보 없음";
			} else if ("Y".equals(ydSchProhExn)) {
				//스케줄 금지여부 Check
				ydL3HdRsCd = "FU72";
				ydL3Msg    = "오류:스케쥴코드[" + ydSchCd + "] 기동금지";
			}

			if (!"".equals(ydL3Msg)) {
				setRecord.setField("YD_L3_HD_RS_CD", 	ydL3HdRsCd); //야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		ydL3Msg); //야드L3MESSAGE
				setRecord.setField("RTN_CD",			JPlateYdConst.RETN_CD_FAILURE);
				return setRecord;
			}

			/**********************************************************
			* 5. 현재 크레인스케줄의 야드작업진행상태 Update
			*    설비상태가 권상작업지시[1]이고  야드작업진행상태가 권상지시,
			*    권상완료, 권하지시[1,2,3] 이면 야드작업진행상태를
			*    명령선택대기[W]로 수정
			**********************************************************/
			if ("1".equals(ydEqpStat) && !"".equals(ydCrnSchId)) {
				recPara.setField("YD_CRN_SCH_ID", 		ydCrnSchId); 									// 야드크레인스케쥴ID(현재)
				recPara.setField("YD_WRK_PROG_STAT", 	"1"       ); 									// 권상지시
				recPara.setField("YD_WORD_DT", 			JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));	// 야드작업지시일시
				recPara.setField("MODIFIER", 			szModifier);

				//현재 크레인스케줄 야드작업진행상태 수정
				/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updYdCrnWrkProgStat 

				UPDATE TB_YD_CRNSCH       
				   SET YD_WRK_PROG_STAT = :V_YD_WRK_PROG_STAT 
				     , MODIFIER         = :V_MODIFIER 
				     , YD_EQP_ID        = NVL(:V_YD_EQP_ID, YD_EQP_ID)
				     , MOD_DDTT         = SYSDATE 
				     , YD_WORD_DT       = DECODE(:V_YD_WORD_DT, null, YD_WORD_DT, TO_DATE(:V_YD_WORD_DT,'YYYYMMDDHH24MISS'))
				 WHERE YD_CRN_SCH_ID     = :V_YD_CRN_SCH_ID 
				*/ 
				ydCrnSchDao.updYdCrnWrkProgStat(recPara);
			}

			/**********************************************************
			* 6. 작업예약 등록 및 저장품, 적치단 정보 수정
			**********************************************************/
			//작업예약ID 생성
			String ydWbookId = ydWrkbookDao.getSeqId();

			if ("".equals(ydWbookId)) {
				ydL3Msg = "오류:작업예약ID 생성 실패";
				setRecord.setField("YD_L3_HD_RS_CD", 	"FU41" ); 		//야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		ydL3Msg); 		//야드L3MESSAGE
				setRecord.setField("RTN_CD",			JPlateYdConst.RETN_CD_FAILURE);
				return setRecord;
			}

			//작업예약 등록
			recPara.setField("YD_WBOOK_ID", 		ydWbookId); 		//야드작업예약ID
			recPara.setField("YD_GP", 				ydGp); 				//야드구분
			recPara.setField("YD_BAY_GP", 			ydBayGp); 			//야드동구분
			recPara.setField("YD_SCH_CD", 			ydSchCd); 			//야드스케쥴코드
			recPara.setField("YD_SCH_PRIOR", 		ydWrkCrnPrior); 	//야드스케쥴우선순위
			recPara.setField("YD_SCH_PROG_STAT", 	"W"); 				//야드스케쥴진행상태(스케줄수행대기)
			recPara.setField("YD_SCH_ST_GP", 		"M"); 				//야드스케쥴기동구분(Manual 작업)
			recPara.setField("YD_SCH_REQ_GP", 		"X"); 				//야드스케쥴요청구분(강제권상요구)
			recPara.setField("YD_AIM_YD_GP", 		ydGp); 				//야드목표야드구분
			recPara.setField("YD_AIM_BAY_GP", 		ydBayGp); 			//야드목표동구분
			recPara.setField("YD_TO_LOC_DCSN_MTD", 	"S"); 				//야드TO위치결정방법(스케줄기준적용)
			recPara.setField("REGISTER",			szModifier);
			recPara.setField("MODIFIER",			szModifier);
			/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.insYdWrkbook 
			-- 작업예약 INSERT

			INSERT INTO TB_YD_WRKBOOK
			(
			       YD_WBOOK_ID
			     , REGISTER
			     , REG_DDTT
			     , MODIFIER
			     , MOD_DDTT
			     , YD_GP
			     , YD_BAY_GP
			     , YD_SCH_CD
			     , YD_SCH_PRIOR
			     , YD_SCH_PROG_STAT
			     , YD_SCH_ST_GP
			     , YD_SCH_REQ_GP
			     , YD_AIM_YD_GP
			     , YD_AIM_BAY_GP
			     , YD_CTS_RELAY_YN
			     , YD_CTS_RELAY_BAY_GP
			     , YD_TO_LOC_DCSN_MTD
			     , YD_TO_LOC_GUIDE
			     , YD_WRK_PLAN_TCAR
			     , YD_CAR_USE_GP
			     , TRN_EQP_CD
			     , CAR_NO
			     , CARD_NO
			)
			VALUES
			(
			       :V_YD_WBOOK_ID
			     , :V_REGISTER
			     , SYSDATE
			     , :V_MODIFIER
			     , SYSDATE
			     , NVL(:V_YD_GP, 'F')
			     , :V_YD_BAY_GP
			     , :V_YD_SCH_CD
			     , :V_YD_SCH_PRIOR
			     , :V_YD_SCH_PROG_STAT
			     , :V_YD_SCH_ST_GP
			     , :V_YD_SCH_REQ_GP
			     , :V_YD_AIM_YD_GP
			     , :V_YD_AIM_BAY_GP
			     , :V_YD_CTS_RELAY_YN
			     , :V_YD_CTS_RELAY_BAY_GP
			     , :V_YD_TO_LOC_DCSN_MTD
			     , :V_YD_TO_LOC_GUIDE
			     , :V_YD_WRK_PLAN_TCAR
			     , :V_YD_CAR_USE_GP
			     , :V_TRN_EQP_CD
			     , :V_CAR_NO
			     , :V_CARD_NO
			)
			*/
			ydWrkbookDao.insYdWrkbook(recPara);

			rsOffUp.first();

			for (int ii=0; ii<rsOffUp.size(); ii++) {

				rsOffUp.absolute(ii+1);
				recTemp 	 = rsOffUp.getRecord();
				szStlNo 	 = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
				szYdStkBedNo = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_NO");
				szYdStkLyrNo = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");

				//작업예약재료 등록
				recPara.setField("STL_NO", 			szStlNo); 			//재료번호
				recPara.setField("YD_STK_BED_NO",	szYdStkBedNo);		//야드적치베드
				recPara.setField("YD_STK_LYR_NO", 	szYdStkLyrNo); 		//야드적치단
				
				/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.insYdWrkbookMtl 

				INSERT INTO TB_YD_WRKBOOKMTL
				        (
				         YD_WBOOK_ID
				        ,STL_NO
				        ,REGISTER
				        ,REG_DDTT
				        ,YD_STK_COL_GP
				        ,YD_STK_BED_NO
				        ,YD_STK_LYR_NO
				        ,YD_UP_COLL_SEQ
				        ,YD_ISPTOR
				        ,YD_TAKE_OUT_DT
				        ,YD_TAKE_OUT_CD
				        )
				VALUES (
				         :V_YD_WBOOK_ID
				        ,:V_STL_NO     
				        ,:V_REGISTER   
				        ,SYSDATE
				        ,:V_YD_STK_COL_GP 
				        ,:V_YD_STK_BED_NO 
				        ,:V_YD_STK_LYR_NO 
				        ,:V_YD_UP_COLL_SEQ
				        ,:V_YD_ISPTOR     
				        ,:V_YD_TAKE_OUT_DT
				        ,:V_YD_TAKE_OUT_CD
				        )
				*/        
				ydWrkbookMtlDao.insYdWrkbookMtl(recPara);

				//저장품 작업예약정보 수정 (TB_YD_STOCK)
				ydStockDao.updYdStockWbook(recPara);

				//적치단 야드적치단재료상태 수정
				recPara.setField("YD_STK_LYR_MTL_STAT", "U"); 				//권상대기

				ydStkLyrDao.updYdStklyrStat(recPara);

			}

			/**********************************************************
			* 7. 크레인스케줄 등록
			**********************************************************/
			//크레인스케줄ID 생성
			ydCrnSchId = ydCrnSchDao.getSeqId();

        	szMsg = "["+ szOperationName +"] 크레인스케줄 등록 .. 크레인스케줄ID 생성 >>>> " + ydCrnSchId;
    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if ("".equals(ydCrnSchId)) {
				ydL3Msg = "오류:크레인스케줄ID 생성 실패";
				setRecord.setField("YD_L3_HD_RS_CD", 	"FU51" ); 			//야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		ydL3Msg); 			//야드L3MESSAGE
				setRecord.setField("RTN_CD",			JPlateYdConst.RETN_CD_FAILURE);
				return setRecord;
			}

			recPara.setField("YD_CRN_SCH_ID", 			ydCrnSchId); 					//야드크레인스케쥴ID
			recPara.setField("YD_UP_WRK_ACT_GP",		"F");	//야드권상작업수행구분(YD_UP_WRK_ACT_GP) 항목에 강제권상('F') SET

			//크레인스케줄 등록
			/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.mergeYdCrnsch 
			-- 강제권상시 크레인 작업지시 등록

			MERGE INTO TB_YD_CRNSCH CS USING (
			SELECT *
			  FROM
			(
			    SELECT :V_YD_CRN_SCH_ID                         AS YD_CRN_SCH_ID      --야드크레인스케쥴ID
			          ,:V_MODIFIER                              AS MODIFIER           --수정자
			          ,SYSDATE                                  AS MOD_DDTT           --수정일시
			          ,'N'                                      AS DEL_YN             --삭제유무
			          ,WB.YD_WBOOK_ID                                                 --야드작업예약ID
			          ,:V_YD_EQP_ID                             AS YD_EQP_ID          --야드설비ID
			          ,WB.YD_GP                                                       --야드구분
			          ,WB.YD_BAY_GP                                                   --야드동구분
			          ,WB.YD_SCH_CD                                                   --야드스케쥴코드
			          ,WB.YD_SCH_ST_GP                                                --야드스케쥴기동구분
			          ,WB.YD_SCH_REQ_GP                                               --야드스케쥴요청구분
			          ,WB.YD_SCH_PRIOR                                                --야드스케쥴우선순위
			          ,'1'                                      AS YD_WRK_PROG_STAT   --야드작업진행상태(권상지시)
			          ,WB.REG_DDTT                              AS YD_WBOOK_DT        --야드작업예약일시
			          ,SYSDATE                                  AS YD_SCH_DT          --야드스케쥴일시
			          ,SYSDATE                                  AS YD_WORD_DT         --야드작업지시일시
			          ,SF_YD_WRK_HDS_DD(SYSDATE)                AS YD_WRK_HDS_DD      --야드작업계상일자
			          ,SF_YD_WRK_DUTY(SYSDATE)                  AS YD_WRK_DUTY        --야드작업근
			          ,SF_YD_WRK_PARTY(SYSDATE)                 AS YD_WRK_PARTY       --야드작업조
			          ,COUNT(1) OVER()                          AS YD_MAIN_WRK_MTL_SH --야드주작업재료매수
			          ,WB.YD_TO_LOC_DCSN_MTD                                          --야드To위치결정방법
			          ,COUNT(1) OVER()                          AS YD_EQP_WRK_SH      --야드설비작업매수
			          ,SUM(ST.YD_MTL_WT) OVER()                 AS YD_EQP_WRK_WT      --야드설비작업중량
			          ,SUM(ST.YD_MTL_T) OVER()                  AS YD_EQP_WRK_T       --야드설비작업총두께
			          ,MAX(ST.YD_MTL_W) OVER()                  AS YD_EQP_WRK_MAX_W   --야드설비작업최대폭
			          ,MAX(ST.YD_MTL_L) OVER()                  AS YD_EQP_WRK_MAX_L   --야드설비작업최대길이
			          ,WM.YD_STK_COL_GP || WM.YD_STK_BED_NO     AS YD_UP_WO_LOC       --야드권상지시위치
			          ,WM.YD_STK_LYR_NO                         AS YD_UP_WO_LAYER     --야드권상지시단
			          ,SB.YD_STK_BED_XAXIS                      AS YD_UP_WO_LOC_XAXIS --야드권상지시X축
			          ,SF_SLAB_YD_CRN_GAP(WM.YD_STK_COL_GP,'X') AS YD_UP_WO_XAXIS_GAP --야드권상지시X축오차
			          ,SB.YD_STK_BED_YAXIS                      AS YD_UP_WO_LOC_YAXIS --야드권상지시Y축
			          ,SF_SLAB_YD_CRN_GAP(WM.YD_STK_COL_GP,'Y') AS YD_UP_WO_YAXIS_GAP --야드권상지시Y축오차
			          ,SB.YD_STK_BED_ZAXIS                      AS YD_UP_WO_LOC_ZAXIS --야드권상지시Z축
			          ,SF_SLAB_YD_CRN_GAP(WM.YD_STK_COL_GP,'Z') AS YD_UP_WO_ZAXIS_GAP --야드권상지시Z축오차
			          ,'XX010101'                               AS YD_DN_WO_LOC       --야드권하지시위치(NULL)
			          ,'F'                                      AS YD_UP_WRK_ACT_GP   --야드권상작업수행구분 (강제권상)
			      FROM TB_YD_WRKBOOK    WB
			          ,TB_YD_WRKBOOKMTL WM
			          ,TB_YD_STKBED     SB
			          ,TB_YD_SHRSTOCK   ST
			     WHERE WB.YD_WBOOK_ID   = WM.YD_WBOOK_ID
			       AND WM.YD_STK_COL_GP = SB.YD_STK_COL_GP
			       AND WM.YD_STK_BED_NO = SB.YD_STK_BED_NO
			       AND WM.STL_NO        = ST.STL_NO
			       AND WB.YD_WBOOK_ID   = :V_YD_WBOOK_ID
			       AND WB.DEL_YN        = 'N'
			       AND WM.DEL_YN        = 'N'
			     ORDER BY WM.YD_STK_LYR_NO DESC
			)
			 WHERE ROWNUM = 1
			) DD ON (CS.YD_CRN_SCH_ID = DD.YD_CRN_SCH_ID)
			WHEN NOT MATCHED THEN
			INSERT (CS.YD_CRN_SCH_ID         , CS.REGISTER              , CS.REG_DDTT              , CS.MODIFIER              , CS.MOD_DDTT              ,
			        CS.YD_WBOOK_ID           , CS.YD_EQP_ID             , CS.YD_GP                 , CS.YD_BAY_GP             , CS.YD_SCH_CD             ,
			        CS.YD_SCH_ST_GP          , CS.YD_SCH_REQ_GP         , CS.YD_SCH_PRIOR          , CS.YD_WRK_PROG_STAT      , CS.YD_WBOOK_DT           ,
			        CS.YD_SCH_DT             , CS.YD_WORD_DT            , CS.YD_WRK_HDS_DD         , CS.YD_WRK_DUTY           , CS.YD_WRK_PARTY          ,
			        CS.YD_MAIN_WRK_MTL_SH    , CS.YD_TO_LOC_DCSN_MTD    , CS.YD_EQP_WRK_SH         , CS.YD_EQP_WRK_WT         , CS.YD_EQP_WRK_T          ,
			        CS.YD_EQP_WRK_MAX_W      , CS.YD_EQP_WRK_MAX_L      , CS.YD_UP_WO_LOC          , CS.YD_UP_WO_LAYER        , CS.YD_UP_WO_LOC_XAXIS    ,
			        CS.YD_UP_WO_XAXIS_GAP_MAX, CS.YD_UP_WO_XAXIS_GAP_MIN, CS.YD_UP_WO_LOC_YAXIS    , CS.YD_UP_WO_YAXIS_GAP_MAX, CS.YD_UP_WO_YAXIS_GAP_MIN,
			        CS.YD_UP_WO_LOC_ZAXIS    , CS.YD_UP_WO_ZAXIS_GAP_MAX, CS.YD_UP_WO_ZAXIS_GAP_MIN, CS.YD_DN_WO_LOC          , CS.YD_UP_WRK_ACT_GP)
			VALUES (DD.YD_CRN_SCH_ID         , DD.MODIFIER              , DD.MOD_DDTT              , DD.MODIFIER              , DD.MOD_DDTT              ,
			        DD.YD_WBOOK_ID           , DD.YD_EQP_ID             , DD.YD_GP                 , DD.YD_BAY_GP             , DD.YD_SCH_CD             ,
			        DD.YD_SCH_ST_GP          , DD.YD_SCH_REQ_GP         , DD.YD_SCH_PRIOR          , DD.YD_WRK_PROG_STAT      , DD.YD_WBOOK_DT           ,
			        DD.YD_SCH_DT             , DD.YD_WORD_DT            , DD.YD_WRK_HDS_DD         , DD.YD_WRK_DUTY           , DD.YD_WRK_PARTY          ,
			        DD.YD_MAIN_WRK_MTL_SH    , DD.YD_TO_LOC_DCSN_MTD    , DD.YD_EQP_WRK_SH         , DD.YD_EQP_WRK_WT         , DD.YD_EQP_WRK_T          ,
			        DD.YD_EQP_WRK_MAX_W      , DD.YD_EQP_WRK_MAX_L      , DD.YD_UP_WO_LOC          , DD.YD_UP_WO_LAYER        , DD.YD_UP_WO_LOC_XAXIS    ,
			        DD.YD_UP_WO_XAXIS_GAP    , DD.YD_UP_WO_XAXIS_GAP    , DD.YD_UP_WO_LOC_YAXIS    , DD.YD_UP_WO_YAXIS_GAP    , DD.YD_UP_WO_YAXIS_GAP    ,
			        DD.YD_UP_WO_LOC_ZAXIS    , DD.YD_UP_WO_ZAXIS_GAP    , DD.YD_UP_WO_ZAXIS_GAP    , DD.YD_DN_WO_LOC          , DD.YD_UP_WRK_ACT_GP)

			*/        
			ydCrnSchDao.mergeYdCrnsch(recPara);

			//크레인작업재료 등록
			/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.mergeYdCrnWrkMtl 
			-- 강제권상시 크레인 작업재료 등록

			MERGE INTO TB_YD_CRNWRKMTL CM USING (
			SELECT :V_YD_CRN_SCH_ID AS YD_CRN_SCH_ID      --야드크레인스케쥴ID
			      ,WM.STL_NO                              --재료번호
			      ,:V_MODIFIER      AS MODIFIER           --수정자
			      ,SYSDATE          AS MOD_DDTT           --수정일시
			      ,'N'              AS DEL_YN             --삭제유무
			      ,'N'              AS YD_AID_WRK_YN      --야드보조작업여부(주작업)
			      ,WM.YD_STK_LYR_NO                       --야드적치단번호
			      ,WM.YD_STK_BED_NO AS YD_STK_LOT_TP      --야드산적LotType (정정야드에서 해당 항목에 베드번호 SET)
			      ,NULL             AS YD_STK_LOT_CD      --야드산적Lot코드
			      ,NULL             AS HCR_GP             --HCR구분
			      ,ST.STL_PROG_CD                         --재료진도코드
			      ,ST.YD_MTL_ITEM                         --야드재료품목
			      ,NULL             AS YD_ROUTE_GP        --야드행선구분
			      ,'S'              AS YD_TO_LOC_DCSN_MTD --야드To위치결정방법(스케줄기준적용)
			  FROM TB_YD_WRKBOOKMTL WM
			      ,TB_YD_SHRSTOCK   ST
			 WHERE WM.STL_NO      = ST.STL_NO
			   AND WM.YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND WM.DEL_YN      = 'N'
			   AND ST.DEL_YN      = 'N'
			) DD ON (CM.YD_CRN_SCH_ID = DD.YD_CRN_SCH_ID AND CM.STL_NO = DD.STL_NO)
			WHEN NOT MATCHED THEN
			INSERT (CM.YD_CRN_SCH_ID     , CM.STL_NO, CM.REGISTER     , CM.REG_DDTT     , CM.MOD_DDTT     ,
			        CM.MODIFIER          , CM.DEL_YN, CM.YD_AID_WRK_YN, CM.YD_STK_LYR_NO, CM.YD_STK_LOT_TP,
			        CM.YD_STK_LOT_CD     , CM.HCR_GP, CM.STL_PROG_CD  , CM.YD_MTL_ITEM  , CM.YD_ROUTE_GP  ,
			        CM.YD_TO_LOC_DCSN_MTD)
			VALUES (DD.YD_CRN_SCH_ID     , DD.STL_NO, DD.MODIFIER     , DD.MOD_DDTT     , DD.MOD_DDTT     ,
			        DD.MODIFIER          , DD.DEL_YN, DD.YD_AID_WRK_YN, DD.YD_STK_LYR_NO, DD.YD_STK_LOT_TP,
			        DD.YD_STK_LOT_CD     , DD.HCR_GP, DD.STL_PROG_CD  , DD.YD_MTL_ITEM  , DD.YD_ROUTE_GP  ,
			        DD.YD_TO_LOC_DCSN_MTD)

	        */		        
			ydCrnWrkMtlDao.mergeYdCrnWrkMtl(recPara);

			//-----------------------------------------------------------
			// 8. 크레인작업지시의 크레인XY좌표수정
			//-----------------------------------------------------------
			// 크레인 작업지시 조회
			//좌표값 에 해당하는 재료정보 조회
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("yd");
			intRtnVal = ydCrnSchDao.getYdCrnSch(recPara, rsResult);

			if (intRtnVal < 1) {
				ydL3Msg = "오류:크레인작업지시 조회 오류";
				setRecord.setField("YD_L3_HD_RS_CD", 	"FU61" ); 			//야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		ydL3Msg); 			//야드L3MESSAGE
				setRecord.setField("RTN_CD",			JPlateYdConst.RETN_CD_FAILURE);
				return setRecord;
			}
			rsResult.first();
			recCrnSch = rsResult.getRecord();
 
			szYD_UP_WO_LOC = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_UP_WO_LOC");
			szYD_DN_WO_LOC = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_DN_WO_LOC");

			szYD_UP_STK_COL_GP = ydUtils.substr(szYD_UP_WO_LOC, 0, 6);
			szYD_UP_STK_BED_NO = ydUtils.substr(szYD_UP_WO_LOC, 6, 2);
			szYD_DN_STK_COL_GP = ydUtils.substr(szYD_DN_WO_LOC, 0, 6);
			szYD_DN_STK_BED_NO = ydUtils.substr(szYD_DN_WO_LOC, 6, 2);

			recCrnSch.setField("YD_UP_STK_COL_GP", 	szYD_UP_STK_COL_GP);
			recCrnSch.setField("YD_UP_STK_BED_NO",	szYD_UP_STK_BED_NO);
			recCrnSch.setField("YD_DN_STK_COL_GP",	szYD_DN_STK_COL_GP);
			recCrnSch.setField("YD_DN_STK_BED_NO",	szYD_DN_STK_BED_NO);

        	szMsg = "["+ szOperationName +"] 강제권상 크레인 X,Y 좌표 수정 START";
    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    		// 1 후판 정정 기준으로 변경처리
    		szRtnMsg = JPlateYdCrnSchUtil.uptCrnSchXYCordYdP(recCrnSch);

        	szMsg = "["+ szOperationName +"] 강제권상 크레인 X,Y 좌표 수정 END >>>>" + szRtnMsg;
    		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//-----------------------------------------------------------
			// 9. 크레인작업지시(YDY2L004) 전문 송신
			//-----------------------------------------------------------
			szMsg = "["+szOperationName+"] 강제권상 요구 .. 크레인작업지시(YDY2L004) 전문 송신 START";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//설비 야드설비상태 수정
			recPara.setField("YD_EQP_STAT", 		"1"); //권상작업지시
			ydEqpDao.updYdEqpStat(recPara);

        	recInPara = JDTORecordFactory.getInstance().create();
        	//작업지시 전문 전송 data setup
			//recInPara.setField("MSG_ID", 			"YDY2L004");
        	recInPara.setField("MSG_ID", 			"YDY2L004V2");
        	recInPara.setField("YD_CRN_SCH_ID",    	ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_SCH_ID"));
        	recInPara.setField("YD_WRK_PROG_STAT", 	ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_PROG_STAT"));
        	recInPara.setField("YD_SCH_CD",        	ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD"));
        	recInPara.setField("YD_GP",            	ydDaoUtils.paraRecChkNull(recPara, "YD_GP"));
        	recInPara.setField("MODIFIER", 			szModifier);

        	//작업진행중인 작업을 재전송하는 경우는  MSG_GP 값을 'U' UPDATE로 설정해서 보낸다.
        	recInPara.setField("MSG_GP", 			"U");
        	szRtnMsg = ydDelegate.sendMsg(recInPara);

			szMsg = "["+szOperationName+"] 강제권상 요구 .. 크레인작업지시(YDY2L004) 전문 송신 END >>>> " + szRtnMsg;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

        	recInPara.setField("RTN_CD", 		JPlateYdConst.RETN_CD_SUCCESS);
        	recInPara.setField("YD_L3_MSG", 	JPlateYdConst.RETN_CD_SUCCESS);
			return recInPara;

		} catch (Exception e) {

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}	
	
	/**********************************************************
	* 1후판정정야드자동화 신규메소드 추가
	**********************************************************/	
    /**
     * 오퍼레이션명 : 1후판정정 크레인권상실적등록 (Y2YDL008) - 신규
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
     */
    public String procY2CrnUpWr2(JDTORecord msgRecord)throws DAOException  {

    	JPlateYdCommDAO 		commDao 		= new JPlateYdCommDAO();
    	YdStkColDao ydStkColDao = new YdStkColDao();
    	YdCarSchDao ydCarSchDao = new YdCarSchDao();
//    	String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
    	
        int intRtnVal 			= 0;
    	
        String 	szMsg            	= "";
        String 	szRtnMsg            = JPlateYdConst.RETN_CD_SUCCESS;
        String 	szMethodName     	= "procY2CrnUpWr2";
        String 	szOperationName  	= "1후판정정 권상실적처리(신규)";
        
		String	szYdUpWrLoc		= "";			//권상위치
		String	szYdSchCd		= "";			//스케쥴코드
		String 	szModifier		= null;			//수정자

		String	szStlNo			= "";			//재료번호
		String 	szYdDnWoLoc 	= null;			//권하지시위치
		String	szYdWrkProgStat	= "";
		String	szYdUpWrkActGp	= "";
		
        String 	szSendMsg       = "";
        String  szWbookId		= "";
        
		String szCARD_NO = null; 
		
        JDTORecord getParamRec	= JDTORecordFactory.getInstance().create();
        JDTORecord setCrnschRec	= null;
        
        JDTORecord recInTemp    = null;
        
        JDTORecordSet getRecSet 	= null;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 로그 개선 
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 		// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
    	
		String 	szRcvTcCode 	= ydUtils.getTcCode(msgRecord);
		if (szRcvTcCode == null) {
			szMsg = SZ_SESSION_NAME + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			return JPlateYdConst.RETN_CD_TC_ERROR;
		}
        
        try {
    	
        	szMsg = "[" + szOperationName + "] ---------------------- START :: " + msgRecord.toString();
    		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
        	
        	szMsg = "[1후판정정] 크레인 권상실적등록 수신";
        	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	getParamRec.setField("YD_CRN_SCH_ID"		, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID")		);
        	getParamRec.setField("YD_SCH_CD"        	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD")			);
        	getParamRec.setField("MSG_ID"          		, ydDaoUtils.paraRecChkNull(msgRecord,"MSG_ID")				);
        	getParamRec.setField("DATE"          		, ydDaoUtils.paraRecChkNull(msgRecord,"DATE")				);
        	getParamRec.setField("TIME"          		, ydDaoUtils.paraRecChkNull(msgRecord,"TIME")				);
        	getParamRec.setField("MSG_GP"          		, ydDaoUtils.paraRecChkNull(msgRecord,"MSG_GP")				);
        	getParamRec.setField("YD_EQP_ID"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID")			);
			getParamRec.setField("YD_EQP_WRK_MODE"    	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE")	);
			getParamRec.setField("YD_WRK_PROG_STAT"   	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_WRK_PROG_STAT")	);
			getParamRec.setField("YD_CRN_XAXIS"       	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_XAXIS")		);
			getParamRec.setField("YD_CRN_YAXIS"       	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_YAXIS")		);
			getParamRec.setField("YD_CRN_ZAXIS"       	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_ZAXIS")		);
			getParamRec.setField("YD_UP_WR_LOC"   		, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LOC")		);
			getParamRec.setField("YD_UP_WR_LAYER"		, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LAYER")		);
			getParamRec.setField("MODIFIER"			    , ydDaoUtils.paraRecModifier(msgRecord)						);
			getParamRec.setField("YD_UP_WRK_ACT_GP"		, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WRK_ACT_GP")	);
        	
	        szYdUpWrLoc = ydDaoUtils.paraRecChkNull(getParamRec, "YD_UP_WR_LOC"	);		// 권상위치
    		szYdSchCd   = ydDaoUtils.paraRecChkNull(getParamRec, "YD_SCH_CD"	);		// 스케쥴코드
	        szModifier  = ydDaoUtils.paraRecModifier(getParamRec				);		// 수정자
			
	        // 크레인작업재료 조회
        	getRecSet = commDao.select(getParamRec, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnWrkMtl2", logId, szMethodName, "크레인작업재료 조회");
			
	        // 레코드셋의 사이즈값으로 ErrorCheck
	        if (getRecSet.size() == 0) {
				szRtnMsg = "no data found!!!, " + getRecSet.size();
                szMsg    = "[ " + szOperationName + " ] " + szRtnMsg;
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
	        }
	        
	        szStlNo			= StringHelper.evl(getRecSet.getRecord(0).getFieldString("STL_NO"),""				);	// 재료번호
	        szYdDnWoLoc		= StringHelper.evl(getRecSet.getRecord(0).getFieldString("YD_DN_WO_LOC"),""			);	// 권하지시위치
	        szYdWrkProgStat	= StringHelper.evl(getRecSet.getRecord(0).getFieldString("YD_WRK_PROG_STAT"),""		);
	        szYdUpWrkActGp	= StringHelper.evl(getRecSet.getRecord(0).getFieldString("YD_UP_WRK_ACT_GP"),""		);	// 야드권상작업수행구분(YD_UP_WRK_ACT_GP) 항목사용 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)
	        szCARD_NO		= StringHelper.evl(getRecSet.getRecord(0).getFieldString("CARD_NO"),""				);	// L3 화면에서 만들어진 지시여부 
	        szWbookId       = StringHelper.evl(getRecSet.getRecord(0).getFieldString("YD_WBOOK_ID"),""				);	// L3 화면에서 만들어진 지시여부 
	        // 조회한 크레인 스케줄의 작업활성상태 체크 한다.
	        // 명령선택 상태일때만 권상실적 처리 가능하도록 변경
	        if ("1".equals(szYdWrkProgStat)) {

	        	// 적치단 정보 Clear (1개의 크레인스케줄에 잡혀있는 크레인작업재료의 정보를 모두 Check!)
	        	// 2후판 공통사용

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.04 getParamRec에 logId 추가 
	        	getParamRec.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
	        	
// 2024.12.16 clearYdStklyrY7 -> clearYdStklyrY2 변경 
//	        	intRtnVal = this.clearYdStklyrY7(getRecSet, getParamRec);
	        	intRtnVal = this.clearYdStklyrY2(getRecSet, getParamRec);
	        	
	            // 크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
		        setCrnschRec = JDTORecordFactory.getInstance().create();
		        setCrnschRec.setField("YD_CRN_SCH_ID",      getParamRec.getFieldString("YD_CRN_SCH_ID")		);
		        setCrnschRec.setField("YD_WRK_PROG_STAT",   "3"												);	// 3:권하지시
		        setCrnschRec.setField("YD_EQP_ID",     	   	getParamRec.getFieldString("YD_EQP_ID")			);
		        setCrnschRec.setField("YD_UP_WR_LOC",       getParamRec.getFieldString("YD_UP_WR_LOC")		);
		        setCrnschRec.setField("YD_UP_WR_LAYER",     getParamRec.getFieldString("YD_UP_WR_LAYER")	);
		        setCrnschRec.setField("YD_UP_WRK_ACT_GP",   szYdUpWrkActGp									);	// 야드권상작업수행구분(YD_UP_WRK_ACT_GP) 항목사용 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)
		        setCrnschRec.setField("YD_UP_WR_XAXIS",     getParamRec.getFieldString("YD_CRN_XAXIS")		);
		        setCrnschRec.setField("YD_UP_WR_YAXIS",     getParamRec.getFieldString("YD_CRN_YAXIS")		);
		        setCrnschRec.setField("YD_UP_WR_ZAXIS",     getParamRec.getFieldString("YD_CRN_ZAXIS")		);
		        setCrnschRec.setField("YD_UP_CMPL_DT",      JPlateYdUtils.getCurDate("yyyyMMddHHmmss")		);	// 권상완료일시
		        setCrnschRec.setField("MODIFIER",			szModifier);
		        
				intRtnVal = commDao.update(setCrnschRec, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updCrnUpWr", logId, szMethodName, "야드크레인스케줄 UPDATE");
		        
		        if (intRtnVal <= 0) {
		        	szRtnMsg = "크레인 스케줄의 업데이트 UPDATE 처리시 오류 발생." + Integer.toString(intRtnVal);
	                szMsg    = "[ " +szOperationName + "] " + szRtnMsg;
				 	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					return szRtnMsg;
		        }

		        //설비Table의 상태 변경 (권상완료상태로 변경)
		        setCrnschRec.setField("YD_EQP_ID",			getParamRec.getFieldString("YD_EQP_ID")			);
			    setCrnschRec.setField("YD_EQP_STAT",        JPlateYdConst.YD_EQP_STAT_UP_CMPL				);	// 2 : 권상완료
		        setCrnschRec.setField("MODIFIER",			szModifier										);

				intRtnVal = commDao.update(setCrnschRec, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.updYdEqpStat", logId, szMethodName, "설비Table의 상태 변경 (권상완료상태로 변경)");
		        
				if (intRtnVal <= 0) {
		        	szRtnMsg = "설비상태 UPDATE 처리시 오류 발생." + Integer.toString(intRtnVal);
	                szMsg    = "[ " +szOperationName + "] " + szRtnMsg;
				 	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				 	return szRtnMsg;
	    		}
				
	            szMsg = "[권상실적처리] 권상지시위치[" + szYdUpWrLoc + "], " + "설비구분[" + szYdDnWoLoc.substring(2, 4) + "]";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 1후판정정 야드L2 크레인작업실적응답 전송  - YDY2L005
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    	        recInTemp = JDTORecordFactory.getInstance().create();
    	        recInTemp.setField("MSG_ID", 			"YDY2L005"										);
    	        recInTemp.setField("YD_EQP_ID", 		getParamRec.getFieldString("YD_EQP_ID")			);	// 야드설비ID
    	        recInTemp.setField("YD_WRK_PROG_STAT", 	"2"												);	// 야드작업진행상태 : 권상완료
    	        recInTemp.setField("YD_SCH_CD", 		getParamRec.getFieldString("YD_SCH_CD")			);	// 야드스케줄코드
    	        recInTemp.setField("YD_CRN_SCH_ID", 	getParamRec.getFieldString("YD_CRN_SCH_ID")		);	// 야드크레인스케줄ID
   	        	recInTemp.setField("YD_L2_WR_GP", 		"U"												);	// 야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
    	        recInTemp.setField("YD_L3_HD_RS_CD", 	"0000"											);	// 야드L3처리결과코드

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recInTemp에 logId 추가 
    	        recInTemp.setField("LOG_ID", 			logId 											);      // logId
//-------------------------------------------------------------------------------------------------------------------------

   	        	szSendMsg = ydDelegate.sendMsg(recInTemp);

    			szMsg = "[" + szOperationName + "] 1후판정정야드L2 크레인작업실적응답[YDY2L005] 전송 완료>>>>" + szSendMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        	
	        } else {
	            szMsg = "야드작업진행상태 체크 오류 .. YD_WRK_PROG_STAT :: " + szYdWrkProgStat;
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

	            szRtnMsg = "명령선택후 권상처리 가능!. 현재상태:" + szYdWrkProgStat;
	            return szMsg;
	        }
	        
	        // RT BOOK-OUT시 전문전송 (__RT__L_)
	        if (szYdSchCd.length() == 8 && "RT".equals(szYdSchCd.substring(2,4)) && ("L".equals(szYdSchCd.substring(6,7)))) {

		        szMsg = "[" + szOperationName + "] RT BOOK-OUT 실적 전송 .. 시작";
		        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		        
		        String	szStlNoList		= "";
		        String[] szArrStlNo		= null;
		        
		        for (int ii=0; ii < getRecSet.size() ; ii++) {
	    			if ("".equals(szStlNoList)) {
	    				szStlNoList = StringHelper.evl(getRecSet.getRecord(ii).getFieldString("STL_NO"),"");
	    			} else {
	    				szStlNoList = szStlNoList + ";" + StringHelper.evl(getRecSet.getRecord(ii).getFieldString("STL_NO"),"");
	    			}
		        }
		        

    	        recInTemp = JDTORecordFactory.getInstance().create();
    	        
    	        /*
    	         * 2016.05.11 윤재광
    	         * I/F 타겟 분기처리
    	         * B동 전체와  PART13(T/F), PART9A,N,B(대차)인 경우는 열처리 L2, 그 외는 전단L2
    	         */

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 #2 열처리 YDP8L501(Bookin/out complete) 송신 추가 
//    	      야드스케쥴코드(YD_SCH_CD) BOOK-OUT - PART31LM, PART32LM, PART34LM, PART35LM
//-------------------------------------------------------------------------------------------------------------------------
	        	szMsg = "[" + szOperationName + "] 야드스케쥴코드 [" + szYdSchCd + "]";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
    	        if ( ydUtils.is2ndHeatBookOutSchdule(szYdSchCd, szYdUpWrLoc) ) {
    	        	
        	        recInTemp.setField("MSG_ID", 			"YDP8L501"								);      // 1 후판 #2 열처리L2
        	        recInTemp.setField("STL_NO",			""										);		// 재료번호
        	        recInTemp.setField("STL_NO_LIST",		szStlNoList								);		// 재료번호 LIST
        	        recInTemp.setField("OPERATION_TYPE",	"2"										);		// 1:Book In, 2:Book Out
        	        recInTemp.setField("YD_STK_COL_GP",		szYdUpWrLoc								);		// FROM위치
        	        recInTemp.setField("YD_EQP_ID", 		getParamRec.getFieldString("YD_EQP_ID")	);		// 야드설비ID
        	        recInTemp.setField("CARD_NO", 			szCARD_NO								); 		// L3 화면에서 만들어진 지시여부 
        	        
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recInTemp에 logId 추가 
        	        recInTemp.setField("LOG_ID", 			logId 									);      // logId
//-------------------------------------------------------------------------------------------------------------------------
    	        
       	        	szSendMsg = ydDelegate.sendMsg(recInTemp);
    	        	
	        	} else if(szYdSchCd.substring(0,2).equals("PB")||szYdUpWrLoc.startsWith("PART13")||szYdUpWrLoc.startsWith("PART9")||szYdUpWrLoc.startsWith("PCRT40")) {
    	        	//열처리 L2
    	        	
    				String sNEW_MODULE_EFF_YN = "N";

    				JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
    				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A008"); //1후판정정야드 열처리L2 Book-In/Book-Out실적 신규모듈적용여부 
    				
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(YDP3L501)---[[[ 1후판정정야드신규적용 : " + sNEW_MODULE_EFF_YN + " ]]]---", JPlateYdConst.DEBUG, logId);
    	        	
    				if(sNEW_MODULE_EFF_YN.equals("Y")) {
    					//신규 메소드 호출
            	        recInTemp.setField("MSG_ID", 			"YDP3L501V2"								);	// 1 후판열처리L2
            	        recInTemp.setField("STL_NO",			""											);	// 재료번호
            	        recInTemp.setField("STL_NO_LIST",		szStlNoList									);	// 재료번호 LIST
            	        recInTemp.setField("OPERATION_TYPE",	"2"											);	// 1:Book In, 2:Book Out
            	        recInTemp.setField("YD_STK_COL_GP",		szYdUpWrLoc									);	// FROM위치
            	        recInTemp.setField("YD_EQP_ID", 		getParamRec.getFieldString("YD_EQP_ID")		);	// 야드설비ID
            	        recInTemp.setField("CARD_NO", 			szCARD_NO									);	// L3 화면에서 만들어진 지시여부 
            	        
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recInTemp에 logId 추가 
            	        recInTemp.setField("LOG_ID", 			logId 									);      // logId
//-------------------------------------------------------------------------------------------------------------------------
            	          	        
           	        	szSendMsg = ydDelegate.sendMsg(recInTemp);
    				} else {
    					//기존 메소드 호출
    					for (int ii=0; ii < getRecSet.size() ; ii++) {
                	        recInTemp.setField("MSG_ID", 			"YDP3L501"																);	// 1 후판열처리L2
                	        recInTemp.setField("STL_NO",			StringHelper.evl(getRecSet.getRecord(ii).getFieldString("STL_NO"),"")	);	// 재료번호
                	        recInTemp.setField("OPERATION_TYPE",	"2"																		);	// 1:Book In, 2:Book Out
                	        recInTemp.setField("YD_STK_COL_GP",		szYdUpWrLoc																);	// FROM위치
                	        recInTemp.setField("YD_EQP_ID", 		getParamRec.getFieldString("YD_EQP_ID")									);	// 야드설비ID
                	        
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
            	        //szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("2", szStlNoList, szYdUpWrLoc,getParamRec.getFieldString("YD_EQP_ID"),szCARD_NO);
    					
						//2019.01.11 전단정정L2는 무조건 1매 단위로 완료실적을 보내달라는 요청
						szArrStlNo	= szStlNoList.split(";");
						for (int ii=0; ii < szArrStlNo.length ; ii++) {
							if (!"".equals(szArrStlNo[ii])) {
								if("PFRTAPLM".equals(szYdSchCd)){  //56020존 크레인 파일링 권상완료지시시 별도 처리 op-type:2, bookout_mode:5
									//szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV4("2", szArrStlNo[ii], szYdUpWrLoc,getParamRec.getFieldString("YD_EQP_ID"),szCARD_NO);
									if(ii==0){  //가장 마지막에 온 재료 기준으로 1번만 전송한다.(L2전단 협의) 2022.06.22 박종호
									szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV4("2", szArrStlNo[ii], szYdUpWrLoc,getParamRec.getFieldString("YD_EQP_ID"),szCARD_NO,szYdSchCd);
									}
								}
								else{
	                	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("2", szArrStlNo[ii], szYdUpWrLoc,getParamRec.getFieldString("YD_EQP_ID"),szCARD_NO);
								}
							}
						}
    					
    				} else {
    					//기존 메소드 호출
    					for (int ii=0; ii < getRecSet.size() ; ii++) {
	    					szStlNo = StringHelper.evl(getRecSet.getRecord(ii).getFieldString("STL_NO"),"");
	            	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSend("2", szStlNo, szYdUpWrLoc,getParamRec.getFieldString("YD_EQP_ID"));
    					}
    				}
    				

    		        szMsg = "[" + szOperationName + "] 1 후판전단L2 RT BOOK-OUT 실적 전송 .. 완료>>>>" + szSendMsg;
                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
    	        }
    	        
	        }
	        
	        //25.07.23 니켈강 출하 추가 - 허동수 책임 
	        else if ( (szYdDnWoLoc.substring(2, 4).equals("PT") ||
	        		  szYdDnWoLoc.substring(2, 4).equals("TR")
	        		  )
	        		  &&
	        		  "PFPT01UM".equals(szYdSchCd)

	        		){	
	        	//권하지시위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
	        	recInTemp = JDTORecordFactory.getInstance().create();
                recInTemp.setField("YD_STK_COL_GP", szYdDnWoLoc.substring(0, 6));
                JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
                intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsResult, 0);
                if( intRtnVal <= 0 ) {
                    szMsg = "[권상실적처리 - 상차개시]차량정지위치[" + szYdDnWoLoc.substring(0, 6) + "] 정보가 존재하지 않습니다." ;
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

                szMsg = "[권상실적처리 - 상차개시]차량정지위치[" + szYdDnWoLoc.substring(0, 6) + "] 정보가 존재하므로 차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]로 차량스케줄 조회" ;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
                
                //운송장비코드로 차량스케줄조회
                recInTemp = JDTORecordFactory.getInstance().create();
                recInTemp.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
                recInTemp.setField("TRN_EQP_CD", szTRN_EQP_CD);
                recInTemp.setField("CAR_NO", szCAR_NO);
                recInTemp.setField("CARD_NO", szCARD_NO);
                rsResult = JDTORecordFactory.getInstance().createRecordSet("");

                intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 27);
                if( intRtnVal <= 0 ) {
                    szMsg = "[권상실적처리 - 상차개시]차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]에 해당하는 차량스케줄이 존재하지 않습니다." ;
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
                    recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szWbookId);                              //작업예약ID
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
	        
	        
	        
	    } catch(Exception e) {
				throw new DAOException(getClass().getName() + e.getMessage(),e);
	    }
    	
    	szMsg = "[" + szOperationName + "] 권상 완료 실적 처리 >>>> END ";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    
	    return JPlateYdConst.RETN_CD_SUCCESS;
	    
    }// end of procY2CrnUpWr2()
	

    /**
     * 오퍼레이션명 : 적치단 Clear
     *
     * @param  ● getRecSet, intGp
     * @return ● intRtnVal
     * @throws ● DAOException
     */
    public int clearYdStklyrY2(JDTORecordSet getRecSet, JDTORecord pInRecord) throws DAOException {

    	JPlateYdStkLyrDAO ydStklyrDao = new JPlateYdStkLyrDAO();

    	JDTORecord getRecord 	= JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord 	= null;
    	JDTORecord crnRecord 	= null;

    	int intRtnVal 			= 0;
    	String 	szMsg 			= "";
    	String 	szMethodName 	= "clearYdStklyrY2";
    	String	szOperationName	= "적치단 Clear";
    	String 	szYdStkColGp 	= "";
    	String	szYdGp			= "";
    	String 	szYdStkBedNo 	= "";
    	String 	szYdUpWrLayer 	= "";
    	String	szYdUpWrLoc		= "";

    	String 	szCrnId			= "";
    	String 	szStlNo			= "";
    	String	szModifier		= "";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(pInRecord, "P");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

    	try {
    		int rowsize = getRecSet.size();
            getRecSet.first();
            getRecord = getRecSet.getRecord();

            szModifier		= ydDaoUtils.paraRecModifier(pInRecord);
            szYdUpWrLoc		= ydDaoUtils.paraRecChkNull(pInRecord, "YD_UP_WR_LOC");
            szYdUpWrLayer 	= ydDaoUtils.paraRecChkNull(pInRecord, "YD_UP_WR_LAYER");
            szYdGp		  	= ydUtils.substr(szYdUpWrLoc, 0, 1);
        	szYdStkColGp 	= ydUtils.substr(szYdUpWrLoc, 0, 6);
        	szYdStkBedNo 	= ydUtils.substr(szYdUpWrLoc, 6, 2);

            szMsg = "[ " + szOperationName + " ] 적치단 Clear Start :: " + szYdStkColGp + szYdStkBedNo + "-" + szYdUpWrLayer;
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

    		for(int ii = 0; ii < rowsize; ii++) {

    			getRecSet.absolute(ii+1);
            	getRecord = getRecSet.getRecord();
            	szCrnId   = ydDaoUtils.paraRecChkNull(getRecord,"YD_EQP_ID");
            	szStlNo   = ydDaoUtils.paraRecChkNull(getRecord,"STL_NO");

    			//크레인에 UPDATE
    			crnRecord = JDTORecordFactory.getInstance().create();
    			crnRecord.setField("YD_STK_COL_GP",       	szCrnId);
    			crnRecord.setField("YD_STK_BED_NO",       	"01");
    			crnRecord.setField("YD_STK_LYR_NO",       	ydUtils.addLeftStr(Integer.toString(ii+1), 3, '0'));
                crnRecord.setField("YD_STK_LYR_MTL_STAT", 	"C");
                crnRecord.setField("STL_NO",              	szStlNo);
                crnRecord.setField("MODIFIER",            	szModifier);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 crnRecord에 logId 추가 
                crnRecord.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

                intRtnVal = ydStklyrDao.updYdStklyrStat(crnRecord);  		//크레인 적치단의 재료정보 UPDATE

    			//권상 지시위치 Clear --> 재료번호로 CLEAR
                setRecord = JDTORecordFactory.getInstance().create();
                setRecord.setField("STL_NO",              	szStlNo);		//재료번호
                setRecord.setField("YD_STK_LYR_MTL_STAT", 	"U");			//권상지시상태
                setRecord.setField("YD_GP",					szYdGp);		//야드구분
                setRecord.setField("MODIFIER", 				szModifier);	//수정자


//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 setRecord에 logId 추가 
                setRecord.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

                intRtnVal = ydStklyrDao.updYdStklyrClearByStlNo(setRecord);	//권상위치 재료의 적치단 정보 Clear

            } //end of for

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
	    }//end of try~catch

		intRtnVal = 1;
		return  intRtnVal;
    } // end of clearYdStklyrY2()

	
	
} // end of class JPlateYdCrnLoadWrkSeEJBBean
