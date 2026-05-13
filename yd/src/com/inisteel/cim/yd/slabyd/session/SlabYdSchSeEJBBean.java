/**
 * @(#)SlabYdSchSeEJBBean
 *
 * @version          V1.00
 * @author           허철호
 * @date             2012/11/22
 *
 * @description      Slab야드 Schedule 처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2012/11/22   허철호      허철호      최초 등록
 * V1.02  2015/12/14   이준영      이준영      항만야드 설비추가
 */
package com.inisteel.cim.yd.slabyd.session;

import java.util.Vector;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.pSlabYd.dao.PSlabYdCommDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO;

import jspeed.base.log.LogLevel;  //LeeJY
import jspeed.base.log.Logger;   //LeeJY
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

/**
 *      [A] 클래스명 : Slab야드 Schedule 처리
 *
 * @ejb.bean name="SlabYdSchSeEJB" jndi-name="SlabYdSchSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class SlabYdSchSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YdSlabUtils slabUtils = new YdSlabUtils();
	private SlabYdComm   slabComm = new SlabYdComm();
	private SlabYdCommDAO commDao = new SlabYdCommDAO();
	private SlabYdSchDAO   schDao = new SlabYdSchDAO();

	private Logger logger = new Logger("yd");   //LeeJY

	
	private PSlabYdCommDAO  PcommDao = new PSlabYdCommDAO();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/***************************************************************************
	 * Crane Schedule
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄(YDYDJ400)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYDYDJ400(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인스케줄[SlabYdSchSeEJB.rcvYDYDJ400] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "rcvYDYDJ400 => 크레인스케줄", "APPPI0", "S", "*");				
			
			//수신 항목 값
			String msgId      = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydWbookId  = slabUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"  )); //야드작업예약ID
			String ydSchCd    = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"    )); //야드스케쥴코드
			String ydEqpId    = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    )); //야드설비ID
			String ydSchStGp  = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_ST_GP" )); //야드스케쥴기동구분
			String ydSchReqGp = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_REQ_GP")); //야드스케쥴요청구분
			String modifier   = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"   )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			if ("".equals(ydSchStGp)) { ydSchStGp = "A"; }		//값이 없으면 Auto작업
			if ("".equals(ydSchReqGp)) { ydSchReqGp = "0"; }	//값이 없으면 모름

			slabUtils.printLog(logId, "스케쥴코드[" + ydSchCd + "], 설비ID[" + ydEqpId + "], 작업예약ID[" + ydWbookId + "], 수정자[" + modifier + "]", "SL");

			JDTORecord jrRtn = null;	//전문 Return
			String trtGp   = "";		//처리구분
			String trtMsg  = ""; 		//처리메세지
			String ydL3Msg = ""; 		//야드L3MESSAGE
			String tmpStr  = "";		//임시변수

			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_YD_WBOOK_ID", ydWbookId); //야드작업예약ID
			jrParam.setField("V_YD_SCH_CD"  , ydSchCd  ); //야드스케쥴코드
			jrParam.setField("V_YD_EQP_ID"  , ydEqpId  ); //야드설비ID
			jrParam.setField("V_MODIFIER"   , modifier ); //수정자

			/**********************************************************
			* 1. 스케줄 정합성 Check
			* 1.1 작업예약ID조회
			**********************************************************/
			//스케줄코드 Check
			if ("".equals(ydWbookId) && !"".equals(ydSchCd)) {
				JDTORecord jrChk = slabComm.chkSchCd(jrParam);

				ydL3Msg = slabUtils.trim(jrChk.getFieldString("YD_L3_MSG"));

				if (!"".equals(ydL3Msg)) {
					ydSchCd = "";
				}
			}

			//설비ID Check
			if ("".equals(ydWbookId) && "".equals(ydSchCd) && !"".equals(ydEqpId)) {
				JDTORecord jrChk = slabComm.chkEqpStat(jrParam);

				ydL3Msg = slabUtils.trim(jrChk.getFieldString("YD_L3_MSG"));

				if (!"".equals(ydL3Msg)) {
					ydEqpId = "";
				}
			}
			
			if (!"".equals(ydWbookId)) {
				//작업예약ID가 있으면
				trtGp = "WbId";
				trtMsg = "작업예약ID조회 [작업예약ID : " + ydWbookId + "]";
			} else if (!"".equals(ydSchCd)) {
				//스케줄코드가 있으면
				trtGp = "WbSchCd";
				trtMsg = "작업예약ID조회 [스케쥴코드 : " + ydSchCd + "]";
			} else if (!"".equals(ydEqpId)) {
				//설비ID가 있으면
				trtGp = "WbEqpId";
				trtMsg = "작업예약ID조회 [야드설비ID : " + ydEqpId + "]";
			} else {
				throw new Exception("오류:작업예약ID조회 항목 없음");
			}

			//작업예약ID 조회
			JDTORecordSet jsChk = schDao.getYDYDJ400(trtGp, jrParam);
			
			if (jsChk.size() > 0) {
				ydWbookId = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_WBOOK_ID"));
			} else {
				 //throw new Exception("오류:" + trtMsg + " >> 작업예약정보 없음");
				slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약정보 없음", "SL");
				return jrRtn;
			}
			slabUtils.printLog(logId, trtMsg + " >> 작업예약ID조회 [" + ydWbookId + "]", "SL");

			jrParam.setField("V_YD_WBOOK_ID", ydWbookId); //야드작업예약ID

			
			//2024.03.29 HJW 재료 중복적치 여부 확인하여 exception 추가 (ora- 에러 방지용).
			//작업예약 재료의 저장위치 등록된 걸로 추후 시스템에서 사용하기 때문에,
			//중복적치현상 발생으로 작업예약 재료의 위치를 정확히 가져오지 못하면, 추후 실적처리도 이상해질 수 있음.
			if(chkDupStkByWBookId(ydWbookId,logId)){
				slabUtils.printLog(logId, "작업예약재료 중복적치현상 발생", "SL");
				throw new Exception("오류 : 작업예약재료 중복적치현상 발생");
			}
			
			
			/**********************************************************
			* 1. 스케줄 적합성 Check
			* 1.2 권상모음순서 및 현재 적치단 저장위치 Update (별도 Transaction 으로 처리)
			**********************************************************/
			EJBConnector tranConn = new EJBConnector("default", "SlabYdSchSeEJB", this);
			tranConn.trx("updCrnSchWB", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			/**********************************************************
			* 1. 스케줄 적합성 Check
			* 1.3 상태정보 Check
			**********************************************************/
			//조회된 작업예약ID로 상태정보 Check
			String ydGp           = ""; 	//야드구분
			String ydBayGp        = ""; 	//야드동구분
			String ydSchPrior     = ""; 	//야드스케쥴우선순위
			String ydToLocDcsnMtd = ""; 	//야드To위치결정방법
			String ydToLocGuide   = ""; 	//야드To위치Guide
			String toLocChkGp     = ""; 	//To위치 점검을 위한 구분(G:To위치Guide, C:차량상차, T:대차상차, E:설비불출, Z:기타)
			String ydAimRtGp      = ""; 	//야드목표행선구분
			String trnEqpCd       = ""; 	//운송장비코드
			String ydWbookDt      = ""; 	//야드작업예약일시
			//HandlingLot분리구분(N:분리안함, R:목표행선분리, X:권하분리)
			String hdLotSprGpAw   = "N";	//보조작업
			String hdLotSprGpMw   = "N";	//주작업
			String maxLyrOvSh     = "0";	//Max단초과매수
			String crnSchLogYn    = "N";	//크레인스케줄Log여부
			String ydEqpStat      = "";		//야드설비상태
			//TakeInBed1매선보급요구여부(장입보급  공Bed가 없더라도 스케줄 생성 및 권하지시위치 XX010101로 Set)
			String tiPreSupYn     = "N";
			
			String interlockWrkYn = "N";
			String upperWrkYn = "N"; //상단재료 작업 여부
			String narrowSlabYn = "N"; //협폭슬라브 2후판 4,5픽업 보급 여부
			
			String planCrnWrkAbleYn = "N";
			String wrkCrnWrkAbleYn = "N";
			String artCrnWrkAbleYn = "N";
			String isIgStmpGpNull  = "N";
			
			trtMsg = "상태정보Check [작업예약ID : " + ydWbookId + "]";

			jsChk = schDao.getYDYDJ400("Stat", jrParam);

			if (jsChk.size() <= 0) {
				throw new Exception("오류:" + trtMsg + " >> 상태정보 없음");
			} else {
				JDTORecord jrChk = jsChk.getRecord(0);

			    ydGp                   = slabUtils.trim(jrChk.getFieldString("YD_GP"              ));	//야드구분
				ydBayGp                = slabUtils.trim(jrChk.getFieldString("YD_BAY_GP"          ));	//야드동구분
				ydSchCd                = slabUtils.trim(jrChk.getFieldString("YD_SCH_CD"          ));	//야드스케쥴코드
				ydSchPrior             = slabUtils.trim(jrChk.getFieldString("YD_SCH_PRIOR"       ));	//야드스케쥴우선순위
				ydToLocDcsnMtd         = slabUtils.trim(jrChk.getFieldString("YD_TO_LOC_DCSN_MTD" ));	//야드To위치결정방법
				ydToLocGuide           = slabUtils.trim(jrChk.getFieldString("YD_TO_LOC_GUIDE"    ));	//야드To위치Guide
				ydWbookDt              = slabUtils.trim(jrChk.getFieldString("YD_WBOOK_DT"        ));	//야드작업예약일시
				toLocChkGp             = slabUtils.trim(jrChk.getFieldString("TO_LOC_CHK_GP"      ));	//To위치점검구분
				hdLotSprGpAw           = slabUtils.nvl(jrChk.getFieldString("HD_LOT_SPR_GP_AW"),"N");	//HandlingLot분리구분(보조작업)
				hdLotSprGpMw           = slabUtils.nvl(jrChk.getFieldString("HD_LOT_SPR_GP_MW"),"N");	//HandlingLot분리구분(주작업)
				maxLyrOvSh             = slabUtils.nvl(jrChk.getFieldString("MAX_LYR_OV_SH"   ),"0");	//Max단초과매수
				crnSchLogYn            = slabUtils.nvl(jrChk.getFieldString("CRN_SCH_LOG_YN"  ),"N");	//크레인스케줄Log여부
				String ydSchProhExn    = slabUtils.trim(jrChk.getFieldString("YD_SCH_PROH_EXN"    ));	//야드스케쥴금지유무
				String ydWrkPlanCrn    = slabUtils.trim(jrChk.getFieldString("YD_WRK_PLAN_CRN"    ));	//야드작업계획크레인
				String ydEqpStatPln    = slabUtils.trim(jrChk.getFieldString("YD_EQP_STAT_PLN"    ));	//야드설비상태(작업계획크레인)
				String ydEqpWrkModePln = slabUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_PLN"));	//야드설비작업Mode(작업계획크레인)
				String ydWrkCrn        = slabUtils.trim(jrChk.getFieldString("YD_WRK_CRN"         ));	//야드작업크레인
				String ydEqpStatWrk    = slabUtils.trim(jrChk.getFieldString("YD_EQP_STAT_WRK"    ));	//야드설비상태(작업크레인)
				String ydEqpWrkModeWrk = slabUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_WRK"));	//야드설비작업Mode(작업크레인)
				String ydAltCrn        = slabUtils.trim(jrChk.getFieldString("YD_ALT_CRN"         ));	//야드대체크레인
				String ydEqpStatAlt    = slabUtils.trim(jrChk.getFieldString("YD_EQP_STAT_ALT"    ));	//야드설비상태(대체크레인)
				String ydEqpWrkModeAlt = slabUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_ALT"));	//야드설비작업Mode(대체크레인)
				String cmDupYn         = slabUtils.trim(jrChk.getFieldString("CM_DUP_YN"          ));	//크레인스케줄 재료중복여부
				String clDupGp         = slabUtils.trim(jrChk.getFieldString("CL_DUP_GP"          ));	//크레인스케줄 저장위치중복여부
				String carUdYn		   = slabUtils.trim(jrChk.getFieldString("CARUD_YN"           ));   //이송하차 여부
				//--REQ202308481172 성분미판정대상 2차절단 자동보급 방지
				isIgStmpGpNull  = slabUtils.trim(jrChk.getFieldString("IS_IG_STMP_GP_CNT_NULL"));   //성분미판정재료 포함여부
				
				int ttMtlSh = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("TT_MTL_SH"),"0"));	//전체 재료매수
				int wmMtlSh = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("WM_MTL_SH"),"0"));	//작업예약 재료매수
				int stMtlSh = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("ST_MTL_SH"),"0"));	//저장품 재료매수
				int slMtlSh = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("SL_MTL_SH"),"0"));	//적치단 재료매수
				int statCSh = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("STAT_C_SH"),"0"));	//적치중인 재료매수(야드적치단재료상태가 적치 중[C] 인 재료수)
				int abLocSh = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("AB_LOC_SH"),"0"));	//저장위치이상 재료매수
				
				interlockWrkYn         = slabUtils.nvl(jrChk.getFieldString("INTERLOCK_WRK_YN"),"N");	//인터락 구간 작업 여부
				upperWrkYn             = slabUtils.nvl(jrChk.getFieldString("UPPER_WRK_YN"),"N");//상단재료 작업예약 여부
				narrowSlabYn           = slabUtils.nvl(jrChk.getFieldString("NARROW_SLAB_YN"),"N");//협폭슬라브 2후판 4,5픽업 보급 여부
				
				planCrnWrkAbleYn	    = slabUtils.nvl(jrChk.getFieldString("PLAN_CRN_WRK_ABLE_YN"),"N");//작업계획크레인 작업예약재료 작업 가능 여부
				wrkCrnWrkAbleYn	   		= slabUtils.nvl(jrChk.getFieldString("WRK_CRN_WRK_ABLE_YN"),"N");//작업크레인 작업예약재료 작업 가능 여부
				artCrnWrkAbleYn	   		= slabUtils.nvl(jrChk.getFieldString("ALT_CRN_WRK_ABLE_YN"),"N");//대체크레인 작업예약재료 작업 가능 여부
				//야드스케쥴우선순위는 작업예약 우선순위로 변경
				//String ydWrkCrnPrior   = slabUtils.trim(jrChk.getFieldString("YD_WRK_CRN_PRIOR"   ));	//야드작업크레인우선순위
				//String ydAltCrnPrior   = slabUtils.trim(jrChk.getFieldString("YD_ALT_CRN_PRIOR"   ));	//야드대체크레인우선순위

				slabUtils.printLog(logId, trtMsg + "인터락구역작업여부[" + interlockWrkYn + "], 상단재료 작업예약 여부 [" + upperWrkYn + "]", "SL");
				
				if ("".equals(ydSchProhExn)) {
					//throw new Exception("오류:" + trtMsg + " >> 스케쥴코드[" + ydSchCd + "] 정보 없음");
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 스케쥴코드[" + ydSchCd + "] 정보 없음", "SL");
					return jrRtn;
				} else if (!("M".equals(ydSchStGp) && "Y".equals(carUdYn)) && "Y".equals(ydSchProhExn)) { 
					//2023.02.16 연주 김충만계장 요청. 사용자가 스케줄기동금지 이송하차 기동시킬시 기동되게끔.
					//throw new Exception("오류:" + trtMsg + " >> 스케쥴코드[" + ydSchCd + "] 기동금지");
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 스케쥴코드[" + ydSchCd + "] 기동금지", "SL");
					return jrRtn;
				} else if (wmMtlSh == 0) {
					//throw new Exception("오류:" + trtMsg + " >> 작업예약재료 정보 없음");
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약재료 정보 없음", "SL");
					return jrRtn;
				} else if (wmMtlSh != ttMtlSh) {
					//throw new Exception("오류:" + trtMsg + " >> 작업예약재료 적치단 중복 등록 [작업예약: " + wmMtlSh + ", 적치단: " + ttMtlSh + "]");
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약재료 적치단 중복 등록 [작업예약: " + wmMtlSh + ", 적치단: " + ttMtlSh + "]", "SL");
					return jrRtn;
				} else if (wmMtlSh != slMtlSh) {
					//throw new Exception("오류:" + trtMsg + " >> 작업예약재료 적치단 정보 이상 [" + (wmMtlSh - slMtlSh) + "매]");
					slabUtils.printLog(logId, trtMsg +"오류:" + trtMsg + " >> 작업예약재료 적치단 정보 이상 [" + (wmMtlSh - slMtlSh) + "매]", "SL");
					return jrRtn;
				} else if (wmMtlSh != statCSh) {
					//throw new Exception("오류:" + trtMsg + " >> 작업예약재료 적치중[C]이 아님 [" + (wmMtlSh - statCSh) + "매]");
					slabUtils.printLog(logId, trtMsg +"오류:" + trtMsg + " >> 작업예약재료 적치중[C]이 아님 [" + (wmMtlSh - statCSh) + "매]", "SL");
					return jrRtn;
				} else if (wmMtlSh != stMtlSh) {
					//throw new Exception("오류:" + trtMsg + " >> 작업예약재료 저장품 정보 이상 [" + (wmMtlSh - stMtlSh) + "매]");
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약재료 저장품 정보 이상 [" + (wmMtlSh - stMtlSh) + "매]", "SL");
					return jrRtn;
				} else if (abLocSh > 0) {
					//throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치 이상 [" + abLocSh + "매]");
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약재료의 현재위치 이상 [" + abLocSh + "매]", "SL");
					return jrRtn;
				} else if ("Y".equals(cmDupYn)) {
					//throw new Exception("오류:" + trtMsg + " >> 작업예약재료가 기 등록된 크레인작업재료와 중복");
					
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약재료가 기 등록된 크레인작업재료와 중복", "SL");
					return jrRtn;
				} else if ("1".equals(clDupGp)) {
					//throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권하위치와 중복");
					
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권하위치와 중복", "SL");
					return jrRtn;
				} else if ("2".equals(clDupGp)) {
					//throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권상위치와 중복");
					
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권상위치와 중복", "SL");
					return jrRtn;
				} else if ("Y".equals(interlockWrkYn)) {
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약재료의 권상위치 혹은 To위치가 크레인 Interlock 구간", "SL");
					return jrRtn;
				} else if("Y".equals(upperWrkYn)){
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약재료 상단에 다른 작업예약 걸려있는 재료 존재", "SL");
					return jrRtn;
				} else if("Y".equals(narrowSlabYn)){
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 협폭 슬라브 2후판 4,5번 픽업 Bed 사용 불가", "SL");
					return jrRtn;
				}
				
				//2021.11.24 연주1부 최광석 계장님 요청-- 크레인 SPEC 에 따라 스케줄 기동할 수 있도록 수정
				
				//크레인 결정
				if (!"".equals(ydWrkPlanCrn) && !"B".equals(ydEqpStatPln) && "1".equals(ydEqpWrkModePln) && "Y".equals(planCrnWrkAbleYn)) {
					//작업예약 지정크레인 : 최우선 지정
					ydEqpId   = ydWrkPlanCrn;	//야드설비ID
					ydEqpStat = ydEqpStatPln;	//야드설비상태
					slabUtils.printLog(logId, trtMsg + " >> 작업예약 지정크레인[" + ydWrkPlanCrn + "]으로 설정", "SL");
				} else if (!"".equals(ydWrkCrn) && !"B".equals(ydEqpStatWrk) && "1".equals(ydEqpWrkModeWrk) && "Y".equals(wrkCrnWrkAbleYn)) {
					//주작업크레인
					ydEqpId   = ydWrkCrn;		//야드설비ID
					ydEqpStat = ydEqpStatWrk;	//야드설비상태
					slabUtils.printLog(logId, trtMsg + " >> 주작업크레인[" + ydWrkCrn + "]으로 설정", "SL");
				} else {
					//보조작업크레인 : 주작업크레인이 고장이거나 Off-Line 이면
					String tmpMsg = "주작업 크레인[" + ydWrkCrn + "] ";
					
					if ("B".equals(ydEqpStatWrk)) {
						tmpMsg = tmpMsg + "고장";
					} else if (!"1".equals(ydEqpWrkModeWrk)) {
						tmpMsg = tmpMsg + "Off-Line";
					} else if ("N".equals(wrkCrnWrkAbleYn)){
						tmpMsg = tmpMsg + "작업예약 재료 작업 불가";
					}
					
					if ("".equals(ydAltCrn)) {
						throw new Exception("오류:" + trtMsg + " >> " + tmpMsg + ", 보조작업 크레인 정보 없음");
					} else if ("B".equals(ydEqpStatAlt)) {
						throw new Exception("오류:" + trtMsg + " >> " + tmpMsg + ", 보조작업 크레인[" + ydAltCrn + "] 고장");
					} else if (!"1".equals(ydEqpWrkModeAlt)) {
						throw new Exception("오류:" + trtMsg + " >> " + tmpMsg + ", 보조작업 크레인[" + ydAltCrn + "] Off-Line");
					} else if ("N".equals(artCrnWrkAbleYn)){
						throw new Exception("오류:" + trtMsg + " >> " + tmpMsg + ", 보조작업 크레인[" + ydAltCrn + "] 작업예약 재료 작업 불가");
					}
					
					ydEqpId   = ydAltCrn;		//야드설비ID
					ydEqpStat = ydEqpStatAlt;	//야드설비상태
					
					slabUtils.printLog(logId, trtMsg + " >> " + tmpMsg + "이므로 보조작업 크레인[" + ydAltCrn + "]으로 대체", "SL");
				}
				
			

				//To위치 사전 점검
				if ("C".equals(toLocChkGp)) {
					//차량상차작업
					String ydCarUseGp = slabUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP"));	//야드차량사용구분
				           trnEqpCd   = slabUtils.trim(jrChk.getFieldString("TRN_EQP_CD"   ));	//운송장비코드
					String carNo      = slabUtils.trim(jrChk.getFieldString("CAR_NO"       ));	//차량번호
					String cardNo     = slabUtils.trim(jrChk.getFieldString("CARD_NO"      ));	//카드번호

					if ("".equals(ydCarUseGp)) {
						throw new Exception("오류:" + trtMsg + " >> 차량상차작업 야드차량사용구분 없음");
					} else if ("L".equals(ydCarUseGp) && "".equals(trnEqpCd)) {
						throw new Exception("오류:" + trtMsg + " >> 구내운송 상차작업 운송장비코드 없음");
					} else if ("G".equals(ydCarUseGp)) {
						
						// PIDEV
//						if("Y".equals(sApplyYnPI)) {
							
							if ("".equals(carNo)) {							
								throw new Exception("오류:" + trtMsg + " >> 출하차량 상차작업 차량번호  없음");
							} else {
								trnEqpCd = carNo + "-" + cardNo;
							}							
							
//						} else {
//
//							if ("".equals(carNo) || "".equals(cardNo)) {
//								throw new Exception("오류:" + trtMsg + " >> 출하차량 상차작업 차량번호 또는 카드번호 없음");
//							} else {
//								trnEqpCd = carNo + "-" + cardNo;
//							}							
//							
//						}

					}
				} else if ("T".equals(toLocChkGp)) {
					//대차상차작업 : 상차출발시 상차스케줄 기동하면 현재동이 없어 오류가 발생하므로 처리 안 함
					//String ydCurrBayGp = slabUtils.trim(jrChk.getFieldString("YD_CURR_BAY_GP"));	//야드현재동구분
					
					//if (!ydCurrBayGp.equals(ydSchCd.substring(1, 2))) {
					//	throw new Exception("오류:" + trtMsg + " >> 대차상차작업 현재동 이상 [" + ydSchCd.substring(0, 1) + "XTC" + ydSchCd.substring(4, 6) + " : " + ydCurrBayGp + "]");
					//}
				} else if ("E".equals(toLocChkGp) || "Z".equals(toLocChkGp)) {
					//설비보급 또는 기타
					ydAimRtGp = slabUtils.trim(jrChk.getFieldString("YD_AIM_RT_GP"));	//야드목표행선구분

					if ("".equals(ydAimRtGp)) {
						throw new Exception("오류:" + trtMsg + " >> 설비보급 또는 기타작업 야드목표행선구분 정보 없음");
					}
				}
				
				//야드To위치Guide 값이 4자리 이상이고 To 야드동이 같을 경우가 아니면
				if (!"G".equals(toLocChkGp)) {
					ydToLocDcsnMtd = ""; //야드To위치결정방법
					ydToLocGuide   = ""; //야드To위치Guide
				}
			}

			//주작업 권하분리 안함
			if ("G".equals(toLocChkGp)) {
				//To위치 지정
				if (ydToLocGuide.length() == 8) {
					//Bed까지 지정
					hdLotSprGpMw = "N";
					//to위치가 핸드장일때 to위치 재지정
					if (ydToLocGuide.startsWith("MBHD01")) {
						JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
						JDTORecord jrChk = null;
						JDTORecordSet jsChk2;
						SlabYdL2RcvDAO rcv2Dao = new SlabYdL2RcvDAO();
						
						if(ydToLocGuide.length() == 6) {
							jrParam2.setField("V_YD_STK_COL_GP", ydToLocGuide.substring(0,6));
						}
						else {
							jrParam2.setField("V_YD_STK_COL_GP", ydToLocGuide.substring(0,6));
							jrParam2.setField("V_YD_STK_BED_NO", String.valueOf(Integer.parseInt(ydToLocGuide.substring(6,8))+1));
						}

						jsChk2 = rcv2Dao.getY1YDL009("EmpBed", jrParam2);
						
						if(jsChk2.size() > 0) {
							jrChk = jsChk2.getRecord(0);
							ydToLocGuide = slabUtils.trim(jrChk.getFieldString("YD_STK_COL_GP"))+slabUtils.trim(jrChk.getFieldString("YD_STK_BED_NO"));
						}
					}
				} else if (!"0".equals(ydToLocGuide.substring(2, 3))) {
					//설비Bed 이적
					hdLotSprGpMw = "N";
				}
			} else if (!"Z".equals(toLocChkGp)) {
				//대차상차, 차량상차, 설비보급
				hdLotSprGpMw = "N";
			}
			
			jrParam.setField("V_YD_EQP_ID"       , ydEqpId       ); //야드설비ID
			jrParam.setField("V_YD_SCH_CD"       , ydSchCd       ); //야드스케쥴코드
			jrParam.setField("YD_TO_LOC_DCSN_MTD", ydToLocDcsnMtd); //야드To위치결정방법(작업예약)
			jrParam.setField("YD_TO_LOC_GUIDE"   , ydToLocGuide  ); //야드To위치Guide
			jrParam.setField("HD_LOT_SPR_GP_AW"  , hdLotSprGpAw  ); //HandlingLot분리구분(보조작업)
			jrParam.setField("HD_LOT_SPR_GP_MW"  , hdLotSprGpMw  ); //HandlingLot분리구분(주작업)
			jrParam.setField("MAX_LYR_OV_SH"     , maxLyrOvSh    ); //Max단초과매수
			jrParam.setField("CRN_SCH_LOG_YN"    , crnSchLogYn   ); //크레인스케줄Log여부

			slabUtils.printLog(logId, trtMsg + " 완료 >> 스케쥴코드[" + ydSchCd + "], 크레인[" + ydEqpId + "]", "SL");

			/**********************************************************
			* 1. 스케줄 적합성 Check
			* 1.4 To위치 점검 (대차상차 시 점검 안 함)
			**********************************************************/
			trtGp = "";
			if ("G".equals(toLocChkGp)) {
				//야드To위치Guide 값이 있고 작업 야드동이 같을 경우 야드To위치Guide로 
				//PU, DP, PI 불출위치에 재료가 있거나, 단수, 중량 초과이면 불가
				trtGp  = "ToLocGuide";
				trtMsg = "To위치점검[To위치지정 : " + ydToLocGuide + "]";
			} else if ("C".equals(toLocChkGp)) {
				//차량상차(__PT__U_)일 경우 적치가능 차량이 없으면 불가
				trtGp  = "ToLocCar";
				trtMsg = "To위치점검[차량상차 : " + ydSchCd + ", " + trnEqpCd + "]";
			} else if ("E".equals(toLocChkGp)) {
				//스케줄코드 및 행선구분으로 To위치 점검
				//불출(__PU__U_, __DP__U_)이고 위치검색범위에 적치매수 0인 Bed가 없으면 불가
				trtGp  = "ToLocExt";
				trtMsg = "To위치점검[설비보급 : " + ydSchCd + ", " + ydAimRtGp + "]";
			} else if ("Z".equals(toLocChkGp)) {
				//스케줄코드 및 행선구분으로 To위치 점검
				trtGp  = "ToLocEtc";
				trtMsg = "To위치점검[기타 : " + ydSchCd + ", " + ydAimRtGp + "]";
			}
			
			jrParam.setField("V_IS_IG_STMP_GP_NULL", isIgStmpGpNull);
			if (!"".equals(trtGp)) {
				String toLocChkRst = ""; //To위치점검결과

				jsChk = schDao.getYDYDJ400(trtGp, jrParam);
				
				if (jsChk.size() > 0) {
					toLocChkRst = slabUtils.trim(jsChk.getRecord(0).getFieldString("TO_LOC_CHK_RST"));

					//To위치지정일 경우
					if ("G".equals(toLocChkGp)) {
						//대체할 Bed가 있어도 OK
						if ("OK".equals(toLocChkRst)) {
							String ydToLocGuideNew = "";
							
							if (!"0".equals(ydToLocGuide.substring(2, 3))) {
								if("HD".equals(ydToLocGuide.substring(2,4))) {
									JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
									JDTORecord jrChk = null;
									JDTORecordSet jsChk2;
									SlabYdL2RcvDAO rcv2Dao = new SlabYdL2RcvDAO();
									
									jrParam2.setField("V_YD_STK_COL_GP", ydToLocGuide.substring(0,6));
									
									jsChk2 = rcv2Dao.getY1YDL009("EmpBed", jrParam2);
									if(jsChk2.size() > 0) {
										jrChk = jsChk2.getRecord(0);
										ydToLocGuideNew = slabUtils.trim(jrChk.getFieldString("YD_STK_COL_GP"))+slabUtils.trim(jrChk.getFieldString("YD_STK_BED_NO"));
									}
								} else {
									//To위치지정 점검에서 설비 Bed 적치불가이면 무조건 Error에서 대체 Bed로 변경
									ydToLocGuideNew = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_TO_LOC_GUIDE_NEW"));
								}
							} else if (ydToLocGuide.length() < 8) {
								//To위치 값이 8자리가 아니면 범위내에서 Bed를 선택(권하위치 기준 적용)
								JDTORecordSet jsNew = schDao.getYDYDJ400("ToLocGuideNew", jrParam);
	
								if (jsNew.size() > 0) {
									ydToLocGuideNew = slabUtils.trim(jsNew.getRecord(0).getFieldString("YD_TO_LOC_GUIDE_NEW"));
								} else {
									toLocChkRst = "G1";
								}
							}
	
							if (!"".equals(ydToLocGuideNew) && !ydToLocGuide.equals(ydToLocGuideNew)) {
								//작업예약의 야드To위치Guide 값은 변경하지 않음
								trtMsg = "To위치점검[To위치 변경 : " + ydToLocGuide + " → " + ydToLocGuideNew + "]";
								ydToLocGuide = ydToLocGuideNew;
								jrParam.setField("YD_TO_LOC_GUIDE", ydToLocGuide); //야드To위치Guide
							}
						} else if ("G1".equals(toLocChkRst)) {
							//TakeInBed1매선보급요구여부가 'Y'이면 장입보급  공Bed가 없더라도 스케줄 생성
							tiPreSupYn = slabUtils.trim(jsChk.getRecord(0).getFieldString("TI_PRE_SUP_YN"));
							if ("Y".equals(tiPreSupYn)) {
								toLocChkRst = "OK";
							}
						}
					}
				} else {
					toLocChkRst = toLocChkGp + "1";
				}
	
				if ("G1".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> To위치 적치가능 Bed 없음");
					slabUtils.printLog(logId, "오류:" + trtMsg + " >> To위치 적치가능 Bed 없음", "SL");					
					return jrRtn;
				} else if ("G2".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> To위치 Max단 초과");
					slabUtils.printLog(logId, "오류:" + trtMsg + " >> To위치 Max단 초과", "SL");					
					return jrRtn;
				} else if ("G3".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> To위치 Max중량 초과");
					slabUtils.printLog(logId, "오류:" + trtMsg + " >> To위치 Max중량 초과", "SL");					
					return jrRtn;
				} else if ("G4".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> To위치 Max높이 초과");
					slabUtils.printLog(logId, "오류:" + trtMsg + " >> To위치 Max높이 초과", "SL");					
					return jrRtn;
				} else if ("G5".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> To위치에 적치된 재료 있음");
					slabUtils.printLog(logId, "오류:" + trtMsg + " >> To위치에 적치된 재료 있음", "SL");					
					return jrRtn;
				} else if ("G6".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> To위치에 적치된 재료 있음");
					slabUtils.printLog(logId, "오류:" + trtMsg + " >> 2차절단 #1,#3 협폭재 장입 불가", "SL");					
					return jrRtn;
				} else if ("G7".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> To위치에 적치된 재료 있음");
					slabUtils.printLog(logId, "오류:" + trtMsg + " >> To위치 길이 기준 만족 못함", "SL");					
					return jrRtn;
				}  
				 else if ("C1".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> 상차가능 차량 없음");
					slabUtils.printLog(logId, "오류:" + trtMsg + " >> 상차가능 차량 없음", "SL");					
					return jrRtn;
				} else if ("E1".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> 적치가능 설비 공Bed 없음");
					slabUtils.printLog(logId,"오류:" + trtMsg + " >> 적치가능 설비 공Bed 없음", "SL");					
					return jrRtn;
				} else if ("Z1".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> 적치가능 위치검색Bed 없음");
					slabUtils.printLog(logId,"오류:" + trtMsg + " >> 적치가능 위치검색Bed 없음", "SL");					
					return jrRtn;
				}
	
				slabUtils.printLog(logId, trtMsg + " 완료", "SL");
			}
			
			/**********************************************************
			* 2. Handling Lot 편성
			**********************************************************/
			Vector vcLot = this.setSlabYdHdLot(jrParam);
			
			/**********************************************************
			* 3. 크레인스케줄 등록
			* 3.1 크레인스케줄 등록전 사전 작업
			*   - 크레인스케줄ID 조회
			*   - 적치단 상태(작업예약재료 권상대기) 수정
			* 3.2 권하지시위치 결정
			*   - 권상지시위치 조회
			* 3.3 크레인스케줄 저장
			*   - 크레인스케줄 등록
			*   - 크레인작업재료 등록
			*   - 적치단 상태(권하대기) 수정
			**********************************************************/
			JDTORecordSet jsLot = null;		//Lot
			JDTORecord    jrRow = null;		//현재 Row

			int    schCnt = vcLot.size();	//스케줄수
			int    totCnt          = 0;		//스케줄총재료수
			int    seqNo           = 0;		//재료순서
			int    iYdMainWrkMtlSh = 0;		//야드주작업재료매수
			int    iYdAidWrkMtlSh  = 0;		//야드보조작업재료매수
			int    iYdEqpWrkSh     = 0;		//야드설비작업매수
			int    iYdEqpWrkWt     = 0;		//야드설비작업중량
			float  fYdEqpWrkT      = 0;		//야드설비작업총두께
			float  fYdEqpWrkMaxW   = 0;		//야드설비작업최대폭
			float  fYdEqpWrkMinW   = 0;		//야드설비작업최소폭
			int    iYdEqpWrkMaxL   = 0;		//야드설비작업최대길이
			int    iYdEqpWrkMinL   = 0;		//야드설비작업최대길이
			float  fYdMtlW         = 0;		//야드재료폭
			int    iYdMtlL         = 0;		//야드재료길이
			String stlNo           = "";	//재료번호
			String ydCrnSchId      = "";	//야드크레인스케쥴ID
			String ydStkLotTp      = "";	//야드산적LotType
			String ydStkLotCd      = "";	//야드산적Lot코드
			String toLocDcsnMtd    = "";	//To위치결정방법
			String ydUpWoLoc       = "";	//야드권상지시위치
			String ydUpWoLayer     = "";	//야드권상지시단
			String ydUpWoLocXaxis  = "";	//야드권상지시X축
			String ydUpWoXaxisGap  = "";	//야드권상지시X축오차
			String ydUpWoLocYaxis  = "";	//야드권상지시Y축
			String ydUpWoYaxisGap  = "";	//야드권상지시Y축오차
			String ydUpWoLocZaxis  = "";	//야드권상지시Z축
			String ydUpWoZaxisGap  = "";	//야드권상지시Z축오차
			String ydDnWoLoc       = "";	//야드권하지시위치
			String ydDnWoLayer     = "";	//야드권하지시단
			String ydDnWoLocXaxis  = "";	//야드권하지시X축
			String ydDnWoXaxisGap  = "";	//야드권하지시X축오차
			String ydDnWoLocYaxis  = "";	//야드권하지시Y축
			String ydDnWoYaxisGap  = "";	//야드권하지시Y축오차
			String ydDnWoLocZaxis  = "";	//야드권하지시Z축
			String ydDnWoZaxisGap  = "";	//야드권하지시Z축오차
			String befDnWoLoc      = "";	//권하지시위치 결정 전 권하지시위치
			String dnWoLocZaxis    = "";	//야드권하지시Z축(다음 권하위치 Setting 용)
			
			
			//스케줄총재료수
			for (int ii = 0; ii < schCnt; ii++) {
				totCnt += ((JDTORecordSet)vcLot.get(ii)).size();
			}

			//크레인스케줄 등록용
			String[][] paramCS = new String[schCnt][46];	//크레인스케줄 Param
			String[][] paramCM = new String[totCnt][14];	//크레인작업재료 Param
			String[][] lastMtl = new String[schCnt][2];		//스케줄마지막재료 정보
			
			float [][] ydMtlWArr = new float[schCnt][2];    //야드재료폭 배열 0 : 최대두께  1: 최소두께
			int [][] ydMtlLArr = new int[schCnt][2];    //야드재료길이 배열 0 : 최대길이  1: 최소길이

			//권상지시위치(현재위치) 정보 조회
			jsChk = schDao.getYDYDJ400("CurrLoc", jrParam);
			
			//크레인스케쥴ID 조회 (Lot수 만큼)
			jrParam.setField("V_CRN_SCH_CNT", String.valueOf(schCnt)); //크레인스케쥴수
			JDTORecordSet jsCrnSchId = schDao.getYDYDJ400("CrnSchId", jrParam);

			for (int ii = 0; ii < schCnt; ii++) {
				//야드크레인스케쥴ID
				ydCrnSchId = jsCrnSchId.getRecord(ii).getFieldString("YD_CRN_SCH_ID");

				jsLot  = (JDTORecordSet)vcLot.get(ii);
				iYdEqpWrkSh = jsLot.size(); //야드설비작업매수

				//크레인작업재료 등록용 Data Set
				stlNo         = "";
				ydStkLotTp    = "";
				ydStkLotCd    = "";
				ydAimRtGp     = "";
				toLocDcsnMtd  = "";
				iYdEqpWrkWt   = 0;
				fYdEqpWrkT    = 0;
				fYdEqpWrkMaxW = 0;
				fYdEqpWrkMinW = 9999;
				iYdEqpWrkMaxL = 0;
				iYdEqpWrkMinL = 9999;
				fYdMtlW       = 0;
				iYdMtlL       = 0;

				for (int jj = 0; jj < iYdEqpWrkSh; jj++) {
					jrRow = jsLot.getRecord(jj);

					stlNo        = jrRow.getFieldString("STL_NO"            );
					ydStkLotTp   = jrRow.getFieldString("YD_STK_LOT_TP"     );
					ydStkLotCd   = jrRow.getFieldString("YD_STK_LOT_CD"     );
					ydAimRtGp    = jrRow.getFieldString("YD_AIM_RT_GP"      );
					toLocDcsnMtd = jrRow.getFieldString("YD_TO_LOC_DCSN_MTD");
					iYdEqpWrkWt  += Integer.parseInt(slabUtils.nvl(jrRow.getFieldString("YD_MTL_WT"),"0"));
					fYdEqpWrkT   += Float.parseFloat(slabUtils.nvl(jrRow.getFieldString("YD_MTL_T" ),"0"));
					fYdMtlW       = Float.parseFloat(slabUtils.nvl(jrRow.getFieldString("YD_MTL_W" ),"0"));
					iYdMtlL       = Integer.parseInt(slabUtils.nvl(jrRow.getFieldString("YD_MTL_L" ),"0"));
					if (fYdMtlW > fYdEqpWrkMaxW) { fYdEqpWrkMaxW = fYdMtlW; }
					if (iYdMtlL > iYdEqpWrkMaxL) { iYdEqpWrkMaxL = iYdMtlL; }
					if (iYdMtlL < iYdEqpWrkMinL) { iYdEqpWrkMinL = iYdMtlL; }
					if (fYdMtlW < fYdEqpWrkMinW) { fYdEqpWrkMinW = fYdMtlW; }
					
					paramCM[seqNo][0]  = ydCrnSchId;							//야드크레인스케쥴ID
					paramCM[seqNo][1]  = stlNo;									//재료번호
					paramCM[seqNo][2]  = modifier;								//등록자
					paramCM[seqNo][3]  = modifier;								//수정자
					paramCM[seqNo][4]  = "N";									//삭제유무
					paramCM[seqNo][5]  = jrRow.getFieldString("YD_AID_WRK_YN");	//야드보조작업여부
					paramCM[seqNo][6]  = jrRow.getFieldString("YD_STK_LYR_NO");	//야드적치단번호
					paramCM[seqNo][7]  = ydStkLotTp;							//야드산적LotType
					paramCM[seqNo][8]  = ydStkLotCd;							//야드산적Lot코드
					paramCM[seqNo][9]  = jrRow.getFieldString("HCR_GP"       );	//HCR구분
					paramCM[seqNo][10] = jrRow.getFieldString("STL_PROG_CD"  );	//재료진도코드
					paramCM[seqNo][11] = jrRow.getFieldString("YD_MTL_ITEM"  );	//야드재료품목
					paramCM[seqNo][12] = ydAimRtGp;								//야드행선구분
					paramCM[seqNo][13] = toLocDcsnMtd;							//야드To위치결정방법
					seqNo++;
				}

				//크레인스케줄 등록용 Data Set
				ydUpWoLoc       = jrRow.getFieldString("YD_UP_WO_LOC"  );	//야드권상지시위치
				ydUpWoLayer     = jrRow.getFieldString("YD_UP_WO_LAYER");	//야드권상지시단
				ydUpWoLocXaxis  = "";										//야드권상지시X축
				ydUpWoXaxisGap  = "";										//야드권상지시X축오차
				ydUpWoLocYaxis  = "";										//야드권상지시Y축
				ydUpWoYaxisGap  = "";										//야드권상지시Y축오차
				ydUpWoLocZaxis  = "0";										//야드권상지시Z축
				ydUpWoZaxisGap  = "";										//야드권상지시Z축오차
				ydDnWoLoc       = jrRow.getFieldString("YD_DN_WO_LOC"  );	//야드권하지시위치
				ydDnWoLayer     = jrRow.getFieldString("YD_DN_WO_LAYER");	//야드권하지시단
				ydDnWoLocXaxis  = "";										//야드권하지시X축
				ydDnWoXaxisGap  = "";										//야드권하지시X축오차
				ydDnWoLocYaxis  = "";										//야드권하지시Y축
				ydDnWoYaxisGap  = "";										//야드권하지시Y축오차
				ydDnWoLocZaxis  = "0";										//야드권하지시Z축
				ydDnWoZaxisGap  = "";										//야드권하지시Z축오차
				
				if ("W".equals(toLocDcsnMtd) || "X".equals(toLocDcsnMtd)) {
					iYdAidWrkMtlSh  = iYdEqpWrkSh;
					iYdMainWrkMtlSh = 0;
				} else {
					iYdAidWrkMtlSh  = 0;
					iYdMainWrkMtlSh = iYdEqpWrkSh;
				}

				//권상지시위치 조회
				if (ydUpWoLoc.startsWith("X")) {
					//권상지시위치가 없는 경우(S, R, X, Y)
					if (ii > 0 && ("X".equals(toLocDcsnMtd) || "Y".equals(toLocDcsnMtd))) {
						//권하분리이면 이전 스케줄에서 같은 재료를 찾아 하단 재료의 두께를 보관(권하위치 결정 시 재 계산)
						//이전 스케줄 총두께 - 현 스케줄 총두께
						ydUpWoLocZaxis = String.valueOf(Math.round(Float.parseFloat(paramCS[ii-1][21]) - fYdEqpWrkT));
					}
				} else {
					//권상지시위치가 있는 경우 (S, T, B, M, W)
					for (int jj = 0; jj < jsChk.size(); jj++) {
						jrRow = jsChk.getRecord(jj);
						if (stlNo.equals(jrRow.getFieldString("STL_NO"))) {
							ydUpWoLocXaxis = jrRow.getFieldString("YD_UP_WO_LOC_XAXIS");	//야드권상지시X축
							ydUpWoXaxisGap = jrRow.getFieldString("YD_UP_WO_XAXIS_GAP");	//야드권상지시X축오차
							ydUpWoLocYaxis = jrRow.getFieldString("YD_UP_WO_LOC_YAXIS");	//야드권상지시Y축
							ydUpWoYaxisGap = jrRow.getFieldString("YD_UP_WO_YAXIS_GAP");	//야드권상지시Y축오차
							ydUpWoLocZaxis = jrRow.getFieldString("YD_UP_WO_LOC_ZAXIS");	//야드권상지시Z축
							ydUpWoZaxisGap = jrRow.getFieldString("YD_UP_WO_ZAXIS_GAP");	//야드권상지시Z축오차
							break;
						}
					}

					//권상지시위치가 결정되었으면 상위 스케줄에서 현재 위치로 이적하는 스케줄의 권하위치를 설정한다.
					if (!"".equals(ydUpWoLocXaxis)) {
						String ydDnWoLocZaxisNext = ydUpWoLocZaxis;
						for (int jj = 0; jj < ii; jj++) {
							if (ydUpWoLoc.equals(paramCS[jj][34])) {
								if ("".equals(paramCS[jj][36])) {
									paramCS[jj][36] = ydUpWoLocXaxis;		//야드권하지시X축
									paramCS[jj][37] = ydUpWoXaxisGap;		//야드권하지시X축오차
									paramCS[jj][38] = ydUpWoXaxisGap;		//야드권하지시X축오차
									paramCS[jj][39] = ydUpWoLocYaxis;		//야드권하지시Y축
									paramCS[jj][40] = ydUpWoYaxisGap;		//야드권하지시Y축오차
									paramCS[jj][41] = ydUpWoYaxisGap;		//야드권하지시Y축오차
									paramCS[jj][42] = ydDnWoLocZaxisNext;	//야드권하지시Z축
									paramCS[jj][43] = ydUpWoZaxisGap;		//야드권하지시Z축오차
									paramCS[jj][44] = ydUpWoZaxisGap;		//야드권하지시Z축오차
								}
								ydDnWoLocZaxisNext = String.valueOf(Integer.parseInt(ydDnWoLocZaxisNext) + Math.round(Float.parseFloat(paramCS[jj][20])));	//다음 야드권하지시Z축
							}
						}
					}
				}

				//권하지시위치 조회
				if (!ydDnWoLoc.startsWith("X")) {
					//현재 적치Bed로 권상모음 할 경우
					//먼저 권하위치가 동일한 스케줄을 검색 - 권상모음을 2곳 이상에서 할 경우 먼저 권상모음한 재료가 있는지를 Check
					for (int jj = ii - 1; jj >= 0; jj--) {
						//동일 권하위치에 권하지시단이 크거나 같은 스케줄이 있으면 Handling Lot 편성 시 권하위치 설정 부분을 재 검토 할 것 
						if (ydDnWoLoc.equals(paramCS[jj][34])) {
							ydDnWoLocXaxis = paramCS[jj][36];	//야드권하지시X축
							ydDnWoXaxisGap = paramCS[jj][37];	//야드권하지시X축오차
							ydDnWoLocYaxis = paramCS[jj][39];	//야드권하지시Y축
							ydDnWoYaxisGap = paramCS[jj][40];	//야드권하지시Y축오차
							ydDnWoLocZaxis = String.valueOf(Integer.parseInt(paramCS[jj][42]) + Math.round(Float.parseFloat(paramCS[jj][20])));	//야드권하지시Z축
							ydDnWoZaxisGap = paramCS[jj][43];	//야드권하지시Z축오차
							break;
						}
					}

					//권하위치에 동일한 스케줄이 없으면 첫번째 권상모음이므로 권상위치에서 동일한 위치 및 단을 찾음 - 없으면 뭔가 문제가 있음
					if ("".equals(ydDnWoLocXaxis)) {
						for (int jj = ii - 1; jj >= 0; jj--) {
							if (ydDnWoLoc.equals(paramCS[jj][23]) && ydDnWoLayer.equals(paramCS[jj][24])) {
								ydDnWoLocXaxis = paramCS[jj][25];	//야드권하지시X축
								ydDnWoXaxisGap = paramCS[jj][26];	//야드권하지시X축오차
								ydDnWoLocYaxis = paramCS[jj][28];	//야드권하지시Y축
								ydDnWoYaxisGap = paramCS[jj][29];	//야드권하지시Y축오차
								ydDnWoLocZaxis = paramCS[jj][31];	//야드권하지시Z축
								ydDnWoZaxisGap = paramCS[jj][32];	//야드권하지시Z축오차
								break;
							}
						}
					}
				}
				
				paramCS[ii][0]  = ydCrnSchId;						//야드크레인스케쥴ID
				paramCS[ii][1]  = modifier;							//등록자
				paramCS[ii][2]  = modifier;							//수정자
				paramCS[ii][3]  = "N";								//삭제유무(To위치가 대차이면 권하분리를 하지 않기 위함)
				paramCS[ii][4]  = ydWbookId;						//야드작업예약ID
				paramCS[ii][5]  = ydEqpId;							//야드설비ID
				paramCS[ii][6]  = ydGp;								//야드구분
				paramCS[ii][7]  = ydBayGp;							//야드동구분
				paramCS[ii][8]  = ydSchCd;							//야드스케쥴코드
				paramCS[ii][9]  = ydSchStGp;						//야드스케쥴기동구분
				paramCS[ii][10] = ydSchReqGp;						//야드스케쥴요청구분
				paramCS[ii][11] = ydSchPrior;						//야드스케쥴우선순위
				paramCS[ii][12] = "W";								//야드작업진행상태(명령선택대기)
				paramCS[ii][13] = ydWbookDt;						//야드작업예약일시
				paramCS[ii][14] = String.valueOf(iYdMainWrkMtlSh);	//야드주작업재료매수
				paramCS[ii][15] = String.valueOf(iYdAidWrkMtlSh );	//야드보조작업재료매수
				paramCS[ii][16] = toLocDcsnMtd;						//야드To위치결정방법(마지막재료)
				if ("S".equals(toLocDcsnMtd)) {
					paramCS[ii][17] = ydToLocGuide;					//야드To위치Guide(주작업 최종)
				} else {
					paramCS[ii][17] = "";							//야드To위치Guide
					
				}
				paramCS[ii][18] = String.valueOf(iYdEqpWrkSh  );	//야드설비작업매수
				paramCS[ii][19] = String.valueOf(iYdEqpWrkWt  );	//야드설비작업중량
				paramCS[ii][20] = String.valueOf(fYdEqpWrkT   );	//야드설비작업총두께
				paramCS[ii][21] = String.valueOf(fYdEqpWrkMaxW);	//야드설비작업최대폭
				paramCS[ii][22] = String.valueOf(iYdEqpWrkMaxL);	//야드설비작업최대길이
				paramCS[ii][23] = ydUpWoLoc;						//야드권상지시위치
				paramCS[ii][24] = ydUpWoLayer;						//야드권상지시단
				paramCS[ii][25] = ydUpWoLocXaxis;					//야드권상지시X축
				paramCS[ii][26] = ydUpWoXaxisGap;					//야드권상지시X축오차최대
				paramCS[ii][27] = ydUpWoXaxisGap;					//야드권상지시X축오차최소
				paramCS[ii][28] = ydUpWoLocYaxis;					//야드권상지시Y축
				paramCS[ii][29] = ydUpWoYaxisGap;					//야드권상지시Y축오차최대
				paramCS[ii][30] = ydUpWoYaxisGap;					//야드권상지시Y축오차최소
				paramCS[ii][31] = ydUpWoLocZaxis;					//야드권상지시Z축
				paramCS[ii][32] = ydUpWoZaxisGap;					//야드권상지시Z축오차최대
				paramCS[ii][33] = ydUpWoZaxisGap;					//야드권상지시Z축오차최소
				paramCS[ii][34] = ydDnWoLoc;						//야드권하지시위치
				paramCS[ii][35] = ydDnWoLayer;						//야드권하지시단
				paramCS[ii][36] = ydDnWoLocXaxis;					//야드권하지시X축        
				paramCS[ii][37] = ydDnWoXaxisGap;					//야드권하지시X축오차최대
				paramCS[ii][38] = ydDnWoXaxisGap;					//야드권하지시X축오차최소
				paramCS[ii][39] = ydDnWoLocYaxis;					//야드권하지시Y축        
				paramCS[ii][40] = ydDnWoYaxisGap;					//야드권하지시Y축오차최대
				paramCS[ii][41] = ydDnWoYaxisGap;					//야드권하지시Y축오차최소
				paramCS[ii][42] = ydDnWoLocZaxis;					//야드권하지시Z축        
				paramCS[ii][43] = ydDnWoZaxisGap;					//야드권하지시Z축오차최대
				paramCS[ii][44] = ydDnWoZaxisGap;					//야드권하지시Z축오차최소
				paramCS[ii][45] = ydAimRtGp;						//야드크레인Grab사용RuleID(야드목표행선구분)

				//To위치 결정을 위한 스케줄 마지막 재료 정보
				lastMtl[ii][0] = String.valueOf(seqNo-1);	//스케줄 마지막재료 순서
				lastMtl[ii][1] = stlNo;						//재료번호
				
				ydMtlWArr[ii][0] = fYdEqpWrkMaxW;               //야드설비작업최대폭
				ydMtlWArr[ii][1] = fYdEqpWrkMinW;               //야드설비작업최소폭
				ydMtlLArr[ii][0] = iYdEqpWrkMaxL;               //야드설비작업최대길이
				ydMtlLArr[ii][1] = iYdEqpWrkMinL;               //야드설비작업최소길이
			}

			if ("Y".equals(crnSchLogYn)) {
				slabUtils.printLog(logId, "크레인스케줄 등록용 Param : 크레인스케줄수[" + schCnt + "], 크레인작업재료수[" + totCnt + "]", "SL");
				slabUtils.printParam(logId + " 크레인스케줄 To위치결정전", paramCS);
			}
		
			
			//트랜잭션 분리모듈 적용 여부
			jsChk = schDao.getYDYDJ400("newModuleYN", jrParam);
			String newModuleYN = "N";
			if(jsChk.size() >0){
				jrRow = jsChk.getRecord(0);
				newModuleYN = jrRow.getFieldString("NEW_MODULE_YN");
				
				if("Y".equals(newModuleYN)){
					//////////////////////////////////////////////////////////////////////////////////////////////////////////
					//2020.07.09 크레인 스케줄 등록 작업 트랜잭션 분리 (중복작업지시 방지)
					tranConn = new EJBConnector("default", "SlabYdSchSeEJB", this);
					tranConn.trx("insCrnSch", new Class[] { String.class,String[][].class,String.class,String.class }, new Object[] { "CrnSch",paramCS,logId,methodNm });
					
					tranConn = new EJBConnector("default", "SlabYdSchSeEJB", this);
					tranConn.trx("insCrnSch", new Class[] { String.class,String[][].class,String.class,String.class }, new Object[] { "CrnMtl",paramCM,logId,methodNm });
					
					//////////////////////////////////////////////////////////////////////////////////////////////////////////
					
					
				}
			}
			
			
			
			
			//적치단 야드적치단재료상태 수정 (크레인작업대상 전체 권상대기)
			schDao.updYDYDJ400("StkLyrU", jrParam);

			String ydEqpWrkSh = "";	//야드설비작업매수
			String ydEqpWrkWt = "";	//야드설비작업중량
			String ydEqpWrkT  = "";	//야드설비작업총두께
			
			//권하위치결정 - 다음 스케줄의 동일한 권하위치 정보를 같이 Setting 하기 위해 하단부에서 처리
			for (int ii = 0; ii < schCnt; ii++) {
				if ("Y".equals(paramCS[ii][3])) {
					//삭제유무가 "Y"이면 대차 권하분리로 삭제된 스케줄이므로 관련 크레인 작업재료를 삭제로 Set
					seqNo = Integer.parseInt(lastMtl[ii][0]);	//스케줄 마지막 재료 순서
					while (seqNo >= 0 && paramCS[ii][0].equals(paramCM[seqNo][0])) {
						paramCM[seqNo][4] = "Y";	//삭제유무
						seqNo--;
					}

					continue;
				}
				
				//크레인스케줄 등록용 Data Set
				toLocDcsnMtd    = paramCS[ii][16];	//마지막재료 To위치결정방법
				ydEqpWrkSh      = paramCS[ii][18];	//야드설비작업매수
				ydEqpWrkWt      = paramCS[ii][19];	//야드설비작업중량
				ydEqpWrkT       = paramCS[ii][20];	//야드설비작업총두께
				ydUpWoLoc       = paramCS[ii][23];	//야드권상지시위치
				ydUpWoLocXaxis  = paramCS[ii][25];	//야드권상지시X축
				ydUpWoLocYaxis  = paramCS[ii][28];	//야드권상지시Y축
				ydDnWoLoc       = paramCS[ii][34];	//야드권하지시위치
				ydDnWoLayer     = paramCS[ii][35];	//야드권하지시단
				ydDnWoLocXaxis  = paramCS[ii][36];	//야드권하지시X축
				ydDnWoXaxisGap  = paramCS[ii][37];	//야드권하지시X축오차
				ydDnWoLocYaxis  = paramCS[ii][39];	//야드권하지시Y축
				ydDnWoYaxisGap  = paramCS[ii][40];	//야드권하지시Y축오차
				ydDnWoLocZaxis  = paramCS[ii][42];	//야드권하지시Z축
				ydDnWoZaxisGap  = paramCS[ii][43];	//야드권하지시Z축오차
				befDnWoLoc      = ydDnWoLoc;		//(결정전)권하지시위치
				//스케줄 마지막 재료 정보
				stlNo           = lastMtl[ii][1];	//재료번호

				//권하위치결정 - 권하위치가 결정되지 않은 스케줄의 권하위치를 검색
				trtGp = "";
				if (ydDnWoLoc.startsWith("X")) {
					//주작업(모음) "B", "R" 인 경우에는 이미 권상, 권하 위치가 결정되어 있음, "A"는 최하단에 있을 수 없음
					jrParam.setField("V_YD_UP_WO_LOC"      , ydUpWoLoc     ); //야드권상지시위치
					jrParam.setField("V_YD_UP_WO_LOC_XAXIS", ydUpWoLocXaxis); //야드권상지시X축
					jrParam.setField("V_YD_UP_WO_LOC_YAXIS", ydUpWoLocYaxis); //야드권상지시Y축
					jrParam.setField("V_STL_NO"            , stlNo         ); //재료번호
					//다음 작업이 권하분리 작업이면 Bed사양 Check 시 최종 남아 있는 재료만 해당
					if (ii < schCnt - 1 && ("X".equals(paramCS[ii + 1][16]) || "Y".equals(paramCS[ii + 1][16]))) {
						iYdEqpWrkSh = Integer.parseInt(ydEqpWrkSh) - Integer.parseInt(paramCS[ii][18]);
						iYdEqpWrkWt = Integer.parseInt(ydEqpWrkWt) - Integer.parseInt(paramCS[ii][19]);
						fYdEqpWrkT  = Float.parseFloat(ydEqpWrkT ) - Float.parseFloat(paramCS[ii][20]);
						if (iYdEqpWrkSh > 0 && iYdEqpWrkWt > 0 && fYdEqpWrkT > 0) {
							jrParam.setField("V_YD_EQP_WRK_SH", String.valueOf(iYdEqpWrkSh)); //야드설비작업매수
							jrParam.setField("V_YD_EQP_WRK_WT", String.valueOf(iYdEqpWrkWt)); //야드설비작업중량
							jrParam.setField("V_YD_EQP_WRK_T" , String.valueOf(fYdEqpWrkT )); //야드설비작업총두께
							jrParam.setField("V_YD_EQP_WRK_MIN_W" , ydMtlWArr[ii][1]); //야드설비작업최소폭
							jrParam.setField("V_YD_EQP_WRK_MIN_L" , ydMtlLArr[ii][1]); //야드설비작업최소폭
						}
					} else {
						jrParam.setField("V_YD_EQP_WRK_SH", ydEqpWrkSh); //야드설비작업매수
						jrParam.setField("V_YD_EQP_WRK_WT", ydEqpWrkWt); //야드설비작업중량
						jrParam.setField("V_YD_EQP_WRK_T" , ydEqpWrkT ); //야드설비작업총두께
						jrParam.setField("V_YD_EQP_WRK_MIN_W" , ydMtlWArr[ii][1]); //야드설비작업최소폭
						jrParam.setField("V_YD_EQP_WRK_MIN_L" , ydMtlLArr[ii][1]); //야드설비작업최소폭
					}
					//jrParam.setField("V_YD_SCH_CD"         , ydSchCd       ); //야드스케쥴코드(위에서 Set)
					//jrParam.setField("V_YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID(위에서 Set)
					//jrParam.setField("V_YD_CRN_TONG_W_TOL" , ydCrnTongWTol ); //야드크레인집게폭허용오차(HandlingLot 편성 시 Set)
					if ("W".equals(toLocDcsnMtd) || "X".equals(toLocDcsnMtd) || "Y".equals(toLocDcsnMtd)) {
						//보조작업(주작업권하분리) - To위치평점, 권상-권하위치 거리 순
						trtGp = "DnLocWX";
					} else if ("M".equals(toLocDcsnMtd) || "T".equals(toLocDcsnMtd)) {
						//주작업([Base]이적) - 권상-권하위치 거리 순 (설비 Bed 일 경우에만 사양 Check)
						trtGp = "DnLocMT";
					} else if ("S".equals(toLocDcsnMtd)) {
						//주작업(최종)
						if ("U".equals(ydSchCd.substring(6, 7)) && ("DP".equals(ydSchCd.substring(2, 4)) || "TC".equals(ydSchCd.substring(2, 4)))) {
							//대차상차, Depiler불출
							trtGp = "DnLocTD";
						} else if ("U".equals(ydSchCd.substring(6, 7)) && "PT".equals(ydSchCd.substring(2, 4))) {
							//차량상차
							trtGp = "DnLocPT";
						} else {
							//기타 주작업
							trtGp = "DnLocSY";
						}
					}
				} else if (ydDnWoLoc.equals(ydToLocGuide)) {
					//To위치Guide일 경우
					if ("Y".equals(tiPreSupYn)) {
						//TakeInBed1매선보급요구여부가 'Y'이면 권하지시위치 XX010101로 Set
						ydDnWoLoc       = "XX010101";		//야드권하지시위치
						ydDnWoLayer     = "001";			//야드권하지시단
						paramCS[ii][34] = "XX010101";		//야드권하지시위치
						paramCS[ii][35] = "001";			//야드권하지시단
						paramCS[ii][36] = "0";				//야드권하지시X축
						paramCS[ii][37] = paramCS[ii][26];	//야드권하지시X축오차최대
						paramCS[ii][38] = paramCS[ii][26];	//야드권하지시X축오차최소
						paramCS[ii][39] = "0";				//야드권하지시Y축
						paramCS[ii][40] = paramCS[ii][29];	//야드권하지시Y축오차최대
						paramCS[ii][41] = paramCS[ii][29];	//야드권하지시Y축오차최소
						paramCS[ii][42] = "0";				//야드권하지시Z축
						paramCS[ii][43] = paramCS[ii][32];	//야드권하지시Z축오차최대
						paramCS[ii][44] = paramCS[ii][32];	//야드권하지시Z축오차최소
					} else if ("".equals(paramCS[ii][36])) {
						//작업예약 To위치Guide와 이미 등록되어 있는 권하위치가 같고 최초(야드권하지시X축 값이 없음) 등록일 경우
						trtGp = "DnLocTG";
						jrParam.setField("V_YD_STK_COL_GP", ydDnWoLoc.substring(0, 6)); //야드적치열구분
						jrParam.setField("V_YD_STK_BED_NO", ydDnWoLoc.substring(6, 8)); //야드적치Bed번호
					}
				}
				
				//차량 상차 스케쥴
				if ("M".equals(ydGp)&&"U".equals(ydSchCd.substring(6, 7)) && "PT".equals(ydSchCd.substring(2, 4))) {
					//차량상차
					trtGp = "DnLocPT";
				}
				

				//권하지시위치를 조회하여 Set
				if (!"".equals(trtGp)) {
					jsLot = schDao.getYDYDJ400(trtGp, jrParam);
	
					if (jsLot != null && jsLot.size() > 0) {
						jrRow = jsLot.getRecord(0);
						ydDnWoLoc      = jrRow.getFieldString("YD_DN_WO_LOC"      );	//야드권하지시위치
						ydDnWoLayer    = jrRow.getFieldString("YD_DN_WO_LAYER"    );	//야드권하지시단
						ydDnWoLocXaxis = jrRow.getFieldString("YD_DN_WO_LOC_XAXIS");	//야드권하지시X축
						ydDnWoXaxisGap = jrRow.getFieldString("YD_DN_WO_XAXIS_GAP");	//야드권하지시X축오차
						ydDnWoLocYaxis = jrRow.getFieldString("YD_DN_WO_LOC_YAXIS");	//야드권하지시Y축
						ydDnWoYaxisGap = jrRow.getFieldString("YD_DN_WO_YAXIS_GAP");	//야드권하지시Y축오차
						ydDnWoLocZaxis = jrRow.getFieldString("YD_DN_WO_LOC_ZAXIS");	//야드권하지시Z축
						ydDnWoZaxisGap = jrRow.getFieldString("YD_DN_WO_ZAXIS_GAP");	//야드권하지시Z축오차

						if ("Y".equals(crnSchLogYn)) {
							slabUtils.printLog(logId, "▩ 권하지시위치결정 - " + slabUtils.format(ii, 3) + " : " + paramCS[ii][34] + "-" + paramCS[ii][35] + " >> " + ydDnWoLoc + "-" + ydDnWoLayer + "]", "SL");
						}

						//최초 권하위치가 결정되었으면 이후 스케줄에서 같은 위치인 것을 찾아 변경
						//이전 스케줄에서 결정된 권하지시위치가 있으면 Skip
						paramCS[ii][34] = ydDnWoLoc;		//야드권하지시위치
						paramCS[ii][35] = ydDnWoLayer;		//야드권하지시단
						paramCS[ii][36] = ydDnWoLocXaxis;	//야드권하지시X축
						paramCS[ii][37] = ydDnWoXaxisGap;	//야드권하지시X축오차최대
						paramCS[ii][38] = ydDnWoXaxisGap;	//야드권하지시X축오차최소
						paramCS[ii][39] = ydDnWoLocYaxis;	//야드권하지시Y축
						paramCS[ii][40] = ydDnWoYaxisGap;	//야드권하지시Y축오차최대
						paramCS[ii][41] = ydDnWoYaxisGap;	//야드권하지시Y축오차최소
						paramCS[ii][42] = ydDnWoLocZaxis;	//야드권하지시Z축
						paramCS[ii][43] = ydDnWoZaxisGap;	//야드권하지시Z축오차최대
						paramCS[ii][44] = ydDnWoZaxisGap;	//야드권하지시Z축오차최소
						dnWoLocZaxis  = String.valueOf(Integer.parseInt(ydDnWoLocZaxis) + Math.round(Float.parseFloat(paramCS[ii][20])));	//다음 권하지시Z축
						
						//(결정전)권하지시위치와 같은 스케줄을 찾아 권상, 권하위치 Set
						for (int jj = ii + 1; jj < schCnt; jj++) {
							//같은 권상위치 Set
							if (befDnWoLoc.equals(paramCS[jj][23])) {
								tmpStr = slabUtils.format(Integer.parseInt(ydDnWoLayer) + Integer.parseInt(paramCS[jj][24]) - 1, 3);	//야드권상지시단
								if ("Y".equals(crnSchLogYn)) {
									slabUtils.printLog(logId, "▩ 권상지시위치변경 - " + slabUtils.format(jj, 3) + " : " + paramCS[jj][23] + "-" + paramCS[jj][24] + " >> " + ydDnWoLoc + "-" + tmpStr + "]", "SL");
								}
								paramCS[jj][23] = ydDnWoLoc;		//야드권상지시위치
								paramCS[jj][24] = tmpStr;			//야드권상지시단
								paramCS[jj][25] = ydDnWoLocXaxis;	//야드권상지시X축
								paramCS[jj][26] = ydDnWoXaxisGap;	//야드권상지시X축오차최대
								paramCS[jj][27] = ydDnWoXaxisGap;	//야드권상지시X축오차최소
								paramCS[jj][28] = ydDnWoLocYaxis;	//야드권상지시Y축
								paramCS[jj][29] = ydDnWoYaxisGap;	//야드권상지시Y축오차최대
								paramCS[jj][30] = ydDnWoYaxisGap;	//야드권상지시Y축오차최소
								paramCS[jj][31] = String.valueOf(Integer.parseInt(ydDnWoLocZaxis) + Integer.parseInt(paramCS[jj][31]));	//야드권상지시Z축
								paramCS[jj][32] = ydDnWoZaxisGap;	//야드권상지시Z축오차최대
								paramCS[jj][33] = ydDnWoZaxisGap;	//야드권상지시Z축오차최소
							}
							//같은 권하위치 Set
							if (befDnWoLoc.equals(paramCS[jj][34])) {
								tmpStr = slabUtils.format(Integer.parseInt(ydDnWoLayer) + Integer.parseInt(paramCS[jj][35]) - 1, 3);	//야드권하지시단
								if ("Y".equals(crnSchLogYn)) {
									slabUtils.printLog(logId, "▩ 권하지시위치변경 - " + slabUtils.format(jj, 3) + " : " + paramCS[jj][34] + "-" + paramCS[jj][35] + " >> " + ydDnWoLoc + "-" + tmpStr + "]", "SL");
								}
								paramCS[jj][34] = ydDnWoLoc;		//야드권하지시위치
								paramCS[jj][35] = tmpStr;			//야드권하지시단
								paramCS[jj][36] = ydDnWoLocXaxis;	//야드권하지시X축
								paramCS[jj][37] = ydDnWoXaxisGap;	//야드권하지시X축오차최대
								paramCS[jj][38] = ydDnWoXaxisGap;	//야드권하지시X축오차최소
								paramCS[jj][39] = ydDnWoLocYaxis;	//야드권하지시Y축
								paramCS[jj][40] = ydDnWoYaxisGap;	//야드권하지시Y축오차최대
								paramCS[jj][41] = ydDnWoYaxisGap;	//야드권하지시Y축오차최소
								paramCS[jj][42] = dnWoLocZaxis;		//야드권하지시Z축
								paramCS[jj][43] = ydDnWoZaxisGap;	//야드권하지시Z축오차최대
								paramCS[jj][44] = ydDnWoZaxisGap;	//야드권하지시Z축오차최소
								dnWoLocZaxis  = String.valueOf(Integer.parseInt(ydDnWoLocZaxis) + Math.round(Float.parseFloat(paramCS[jj][20])));	//다음 권하지시Z축
							}
						}

						//권하지시위치가 대차이고 다음 스케줄의 야드To위치결정방법이 권하분리("X","Y") 이면 권하분리 스케줄을 삭제
						if ("TC".equals(ydDnWoLoc.substring(2, 4))) {
							for (int jj = ii + 1; jj < schCnt; jj++) {
								if ("X".equals(paramCS[jj][16]) || "Y".equals(paramCS[jj][16])) {
									paramCS[jj][3] = "Y";	//삭제유무
								} else {
									break;
								}
							}
						}

						//적치Bed 야드적치Bed입출고상태 완산Bed Set
						//"M" : 이적 후 다시 모음작업을 하여야 하므로 해당Bed에 더 이상 적치를 못하게 막음
						//      해당 재료의 권하위치에서 권상모음작업("R") 권상실적 발생 시 해제
						//"T" : To위치 Bed로 모음작업("R")을 할 경우 현재 Logic에서 별도의 권하위치검색을 하지 않음
						//      해당 재료의 권하위치에서 최종작업("S") 권상실적 발생 시 해제
						if ("M".equals(toLocDcsnMtd) || "T".equals(toLocDcsnMtd)) {
							jrParam.setField("V_YD_STK_COL_GP", ydDnWoLoc.substring(0, 6)); //야드적치열구분
							jrParam.setField("V_YD_STK_BED_NO", ydDnWoLoc.substring(6, 8)); //야드적치Bed번호
							schDao.updYDYDJ400("StkBedF", jrParam);
						}
					} else {
						slabUtils.printLog(logId, "▩ Warning ▩ 권하위치 미결정  [" + ii + "] >> To위치결정방법 : " + toLocDcsnMtd + ", 스케줄ID : " + paramCS[ii][0] + ", 스케쥴코드 : " + paramCS[ii][8] + ", 목표행선 : " + paramCS[ii][45], "SL");
						slabUtils.printParam(logId + " 권하위치결정[" + toLocDcsnMtd + "] 오류", paramCS);
						continue;
						//권하위치 미결정되더라도 조업자가 처리할 수 있도록 Exception 발생 안 함
						//throw new Exception("오류:권하위치결정[" + toLocDcsnMtd + "] >> 권상 또는 권하지시위치 미결정 [" + ii + " : " + paramCS[ii][0] + "]");
					}
				}
				
				//권상지시위치가 결정되지 않았으면 Logging
				if (ydUpWoLoc.startsWith("X")) {
					slabUtils.printLog(logId, "▩ Warning ▩ 권상위치 미결정  [" + ii + "] >> To위치결정방법 : " + toLocDcsnMtd + ", 스케줄ID : " + paramCS[ii][0] + ", 스케쥴코드 : " + paramCS[ii][8] + ", 목표행선 : " + paramCS[ii][45], "SL");
					slabUtils.printParam(logId + " 권상위치결정[" + toLocDcsnMtd + "] 오류", paramCS);
				}

				//적치단 야드적치단재료상태 권하대기 Set : 현재 적치된 재료가 없는 것은 전부 Set
				iYdEqpWrkSh = Integer.parseInt(paramCS[ii][18]); //야드설비작업매수

				String[][] paramSL = new String[iYdEqpWrkSh][5];

				seqNo = Integer.parseInt(lastMtl[ii][0]);	//스케줄 마지막 재료 순서
				for (int jj = 0; jj < iYdEqpWrkSh; jj++) {
					paramSL[jj][0] = modifier;					//수정자
					paramSL[jj][1] = paramCM[seqNo][1];			//재료번호
					paramSL[jj][2] = ydDnWoLoc.substring(0, 6);	//야드적치열구분
					paramSL[jj][3] = ydDnWoLoc.substring(6, 8);	//야드적치Bed번호
					paramSL[jj][4] = slabUtils.format(Integer.parseInt(ydDnWoLayer) + Integer.parseInt(paramCM[seqNo][6]) - 1, 3); //야드적치단번호
					seqNo--;
					if (seqNo < 0) { break; }
				}

				//적치단 야드적치단재료상태 권하대기로 수정
				if (!ydDnWoLoc.startsWith("X")) {
					schDao.insYDYDJ400("StkLyrD", paramSL, logId, methodNm);
				}
			}

			/**********************************************************
			* 연주2부 절단장 Y축값 변경 : 2013.11.11 추가
			* - AC0625 ~ AC0627
			* - 재료길이 6500mm 이하
			**********************************************************/
			if (ydSchCd.startsWith("AC")) {
				for (int ii = 0; ii < schCnt; ii++) {
					//야드설비작업최대길이가 6500이하이면
					if (Integer.parseInt(paramCS[ii][22]) <= 6500) {
						//야드권상지시위치가 절단장이면 야드권상지시Y축값 변경
						if ("AC062501".equals(paramCS[ii][23])) {
							paramCS[ii][28] = "10400";
						} else if ("AC062502".equals(paramCS[ii][23])) {
							paramCS[ii][28] = "24000";
						} else if ("AC062601".equals(paramCS[ii][23])) {
							paramCS[ii][28] = "10400";
						} else if ("AC062602".equals(paramCS[ii][23])) {
							paramCS[ii][28] = "24000";
						} else if ("AC062701".equals(paramCS[ii][23])) {
							paramCS[ii][28] = "10400";
						} else if ("AC062702".equals(paramCS[ii][23])) {
							paramCS[ii][28] = "24000";
						}
	
						//야드권하지시위치가 절단장이면 야드권하지시Y축값 변경
						if ("AC062501".equals(paramCS[ii][34])) {
							paramCS[ii][39] = "10400";
						} else if ("AC062502".equals(paramCS[ii][34])) {
							paramCS[ii][39] = "24000";
						} else if ("AC062601".equals(paramCS[ii][34])) {
							paramCS[ii][39] = "10400";
						} else if ("AC062602".equals(paramCS[ii][34])) {
							paramCS[ii][39] = "24000";
						} else if ("AC062701".equals(paramCS[ii][34])) {
							paramCS[ii][39] = "10400";
						} else if ("AC062702".equals(paramCS[ii][34])) {
							paramCS[ii][39] = "24000";
						}
					}
				}
			}
			
			slabUtils.printLog(logId, "크레인스케줄 등록 : 크레인스케줄수[" + schCnt + "], 크레인작업재료수[" + totCnt + "]", "SL");
			slabUtils.printParam(logId + " 크레인스케줄(TB_YD_CRNSCH) 등록"     , paramCS);
			slabUtils.printParam(logId + " 크레인작업재료(TB_YD_CRNWRKMTL) 등록", paramCM);
	
			//크레인스케줄 등록
			schDao.insYDYDJ400("CrnSch", paramCS, logId, methodNm);

			//크레인작업재료 등록
			schDao.insYDYDJ400("CrnMtl", paramCM, logId, methodNm);

			//야드설비상태가 대기이면 크레인작업지시요구(Y1YDL007) 전송
			if ("W".equals(ydEqpStat)) {
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name

				jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ440"               ); //JMSTC코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태
				
				jrRtn = slabUtils.addSndData(jrYdMsg);
			}
			//후판 슬라브야드 작업예약의 마지막 크레인스케줄 권상시 다음 작업예약 기동 및 l2로 작업지시 전달
			//후판슬라브야드에 크레인상태 권상시			
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////
	
	     if("Y".equals(APPLY_YN34)){          

		      if ( ("D".equals(ydGp) && "2".equals(ydEqpStat))){
					jrParam.setField("V_EQP_ID" , ydEqpId );//야드설비ID
					//해당 장비로 기동된 작업예약 없는지 검사.
					jsChk = schDao.getYDYDJ400("EndCrnSchYN", jrParam);
					
					if (jsChk.size() > 0) {
						logger.println(LogLevel.INFO, ydEqpId+" 장비의 현 작업예약의 다음 작업할 크레인 스케줄 존재");
					}
					else{
						logger.println(LogLevel.INFO, ydEqpId+" 장비의 다음 작업예약의 크레인스케줄 작업지시요구 전송");
						JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	
						jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ440"               ); //JMSTC코드
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
						jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
						jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태
						
						jrRtn = slabUtils.addSndData(jrYdMsg);
					
					}
					
					
				}
	      }
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/***************************************************************************
	 * Crane Schedule - Handling Lot 편성
	 **************************************************************************/
	
	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 Handling Lot 편성
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrParam
	 *      @return Vector
	 *      @throws DAOException
	*/
	public Vector setSlabYdHdLot(JDTORecord jrParam) throws DAOException {
		String methodNm = "HandlingLot편성[SlabYdSchSeEJB.setSlabYdHdLot] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			Vector vcRstLot = new Vector();	//최종편성결과
			Vector vcLstLot = new Vector();	//Handling Lot 마지막에 추가할 Lot
			Vector vcDivLot = new Vector();	//권상모음 크레인사양Lot분리결과

			String stlNo           = "";	//재료번호
			String bedSpecOvGp     = "";	//Bed사양초과구분
			String ydToLocDcsnMtd  = "";	//야드To위치결정방법
			String carPointCd 	   = "";
			
			JDTORecordSet jsDivLot = null;	//분할Lot
			JDTORecordSet jsInsLot = null;	//이적작업으로 인하여 추가할 Lot
			JDTORecord jrDivMtl    = null;	//분할Lot재료
			JDTORecord jrInsMtl    = null;	//이적작업으로 인하여 추가할 Lot재료
			JDTORecord jrCrnMtl    = null;	//크레인작업재료

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			String crnSchLogYn = jrParam.getFieldString("CRN_SCH_LOG_YN");	//크레인스케줄Log여부

			/**********************************************************
			* 1. Handling Lot 편성을 위한 사양 정보 조회
			*  - 크레인사양 조회
			*  - Bed사양 조회 : 현재  주작업재료가 적치된 Bed들의 사양
			**********************************************************/
			//크레인사양 조회
			JDTORecordSet jsCrnSpec = schDao.getYDYDJ400("CrnSpec", jrParam);

			if (jsCrnSpec.size() <= 0) {
				throw new Exception("오류:크레인사양 조회 >> 조회 Data 없음");
			}

			JDTORecord jrCrnSpec = JDTORecordFactory.getInstance().create();
			jrCrnSpec.setResultCode(logId);		//Log ID
			jrCrnSpec.setResultMsg(methodNm);	//Log Method Name
			jrCrnSpec.setRecord(jsCrnSpec.getRecord(0));

			//야드크레인집게폭허용오차 : To위치결정 시 사용하기 위해 공통 Param에 Set
			jrParam.setField("V_YD_CRN_TONG_W_TOL", slabUtils.nvl(jrCrnSpec.getFieldString("YD_CRN_TONG_W_TOL"),"0"));
			
			//Bed사양 조회
			JDTORecordSet jsBedSpec = schDao.getYDYDJ400("BedSpec", jrParam);

			if (jsBedSpec.size() <= 0) {
				throw new Exception("오류:Bed사양 조회 >> 조회 Data 없음");
			}

			JDTORecord jrBedSpec = JDTORecordFactory.getInstance().create();
			jrBedSpec.setResultCode(logId);		//Log ID
			jrBedSpec.setResultMsg(methodNm);	//Log Method Name
			jrBedSpec.setField("MAX_LYR_OV_SH", jrParam.getFieldString("MAX_LYR_OV_SH"));	//Max단초과매수
			jrBedSpec.setField("BED_SPEC"     , jsBedSpec);
			
			/**********************************************************
			* 2. 크레인작업재료 조회
			*  - 주작업재료 상단의 모든 재료를 조회
			**********************************************************/
			JDTORecordSet jsCrnMtl = schDao.getYDYDJ400("CrnMtl", jrParam);

			if (jsCrnMtl.size() <= 0) {
				throw new Exception("오류:크레인작업재료조회 >> 조회 Data 없음");
			}

			int cntCrnMtl = jsCrnMtl.size();	//크레인작업재료수
			int maxYdUpCollSeq = 0;				//Max권상모음순서
			
			/**********************************************************
			* 3. 작업예약재료 추출
			*  - 크레인작업재료에서 주작업재료만 추출
			*  - 권상모음Lot를 추가하기 위한 작업
			**********************************************************/
			JDTORecordSet jsWbMtl = JDTORecordFactory.getInstance().createRecordSet("");
			//권상모음순서 최대값 조회 및 Lot편성을 위한 항목 추가
			for (int ii = 0; ii < cntCrnMtl; ii++) {
				jrCrnMtl = jsCrnMtl.getRecord(ii);
				ydToLocDcsnMtd = jrCrnMtl.getFieldString("YD_TO_LOC_DCSN_MTD");
				carPointCd = jrCrnMtl.getFieldString("CAR_POINT_CD");
				//권상모음순서 최대값
				if (maxYdUpCollSeq == 0 && ("S".equals(ydToLocDcsnMtd) || "T".equals(ydToLocDcsnMtd))) {
					maxYdUpCollSeq = jrCrnMtl.getFieldInt("YD_UP_COLL_SEQ");
				}
				//Lot편성을 위한 항목 추가
				jrCrnMtl.setField("LOT_NO"                , "");	//Lot번호
				jrCrnMtl.setField("LOT_IN_SEQ"            , "");	//Lot내순서
				jrCrnMtl.setField("AIM_RT_CHG_GP"         , "");	//목표행선변경구분(참고항목[R:목표행선변경])
				jrCrnMtl.setField("CRN_SPEC_OV_GP"        , "");	//크레인사양초과구분(참고항목[SH:매수초과, WT:중량초과, W:폭초과])
				jrCrnMtl.setField("YD_DN_WO_LOC"          , "");	//야드권하지시위치
				jrCrnMtl.setField("YD_DN_WO_LAYER"        , "");	//야드권하지시단
				jrCrnMtl.setField("YD_STK_LYR_NO"         , "");	//야드적치단번호(크레인작업재료)
				jrCrnMtl.setField("BEF_YD_TO_LOC_DCSN_MTD", ydToLocDcsnMtd);	//전 야드To위치결정방법
			}

			//권상모음순서 1~최대 까지의 작업예약재료 추출
			for (int ii = 1; ii <= maxYdUpCollSeq; ii++) {
				for (int jj = 0; jj < cntCrnMtl; jj++) {
					if (ii == jsCrnMtl.getRecord(jj).getFieldInt("YD_UP_COLL_SEQ")) {
						jrInsMtl = JDTORecordFactory.getInstance().create();
						jrInsMtl.setRecord(jsCrnMtl.getRecord(jj));
						jsWbMtl.addRecord(jrInsMtl);
						break;
					}
				}
			}

			int cntWbMtl = jsWbMtl.size();	//작업예약재료수

			//printParam에서 출력할 항목
			String logItm = "BED_SEQ;HD_SEQ;LOT_NO;LOT_IN_SEQ;YD_UP_COLL_SEQ;AIM_RT_CHG_GP;CRN_SPEC_OV_GP;BEF_YD_TO_LOC_DCSN_MTD;YD_TO_LOC_DCSN_MTD;YD_UP_WO_LOC;YD_UP_WO_LAYER;YD_DN_WO_LOC;YD_DN_WO_LAYER;YD_STK_LYR_NO;STL_NO;YD_AIM_RT_GP";
			if ("Y".equals(crnSchLogYn)) {
				slabUtils.printLog(logId, "3.작업예약재료추출 완료 : 크레인작업재료수[" + cntCrnMtl + "], 작업예약재료수[" + cntWbMtl + "]", "SL");
				slabUtils.printParam(logId + " 3.Handling Lot 편성 대상 (크레인작업재료)", jsCrnMtl, logItm);
			}

			/**********************************************************
			* 4. 권상모음재료의 크레인사양Lot분할
			*  - 권상모음을 완료 했을 경우의 크레인사양을 Check하여 초과시 분할
			*  - 분할Lot별 현 야드To위치결정방법에 따라
			*  . 크레인작업재료 삭제
			*  . 이적후 모음작업대상 Lot 추가 ([A, B, M] -> R -> A)
			*  . 권상모음Lot 추가
			*  - 장입스케줄은 작업예약 등록 시 크레인사양 Check하여 1 Lot가 되도록 등록
			*  . 그러므로 Lot가 분리되더라도 권상모음순서 순으로 Lot를 편성한다.
			*  . 만약 위 조건을 만족하지 않으면 권상모음 역순으로 Lot편성을 하여야만
			*    최종적으로 권상모음순서로 적치된다.
			*    (장입스케줄이 아닐 경우 권상모음순서를 꼭 지킬 필요 없음)
			**********************************************************/
			JDTORecord jrHdLot = JDTORecordFactory.getInstance().create();
			jrHdLot.setResultCode(logId);	//Log ID
			jrHdLot.setResultMsg(methodNm);	//Log Method Name
			//HandlingLot분리구분(N:분리안함, R:목표행선분리, X:권하분리)
			jrHdLot.setField("HD_LOT_SPR_GP_AW"  , jrParam.getFieldString("HD_LOT_SPR_GP_AW"  ));	//HandlingLot분리구분(보조작업)
			jrHdLot.setField("HD_LOT_SPR_GP_MW"  , jrParam.getFieldString("HD_LOT_SPR_GP_MW"  ));	//HandlingLot분리구분(주작업)
			jrHdLot.setField("YD_TO_LOC_DCSN_MTD", jrParam.getFieldString("YD_TO_LOC_DCSN_MTD"));	//야드To위치결정방법(작업예약)
			jrHdLot.setField("CRN_SCH_LOG_YN"    , crnSchLogYn       );	//크레인스케줄Log여부
			jrHdLot.setField("CRN_SPEC"          , jrCrnSpec         );	//크레인사양
			jrHdLot.setField("UP_LOT_NO"         , String.valueOf(90));	//상위 Lot번호
			jrHdLot.setField("UP_LOT"            , jsWbMtl           );	//상위 Lot

			//권상모음재료의 Handling Lot 분할
			this.setSlabYdHdLotSpr(jrHdLot, vcDivLot);

			if ("Y".equals(crnSchLogYn)) {
				slabUtils.printParam(logId + " 4.권상모음재료 Handling Lot 분할", vcDivLot, logItm);
			}

			int cntDivLot = vcDivLot.size();	//분할Lot수
			int cntDivMtl = 0;					//분할Lot재료수
			int cntInsMtl = 0;					//추가Lot재료수
			int lotGrpNo  = 0;					//Lot번호(그룹)
			int lotNo     = 0;					//Lot번호
			
			//크레인작업재료 삭제 및 이적후 모음작업대상 Lot 추가
			for (int ii = 0; ii < cntDivLot; ii++) {
				lotGrpNo  = (9001 + cntDivLot - ii) * 10;
				lotNo     = 0;
				jsInsLot  = JDTORecordFactory.getInstance().createRecordSet("");
				jsDivLot  = (JDTORecordSet)vcDivLot.get(ii);
				cntDivMtl = jsDivLot.size();

				//마지막 Row의 야드To위치결정방법이 'X'이면 Lot추가 Skip
				if (!"X".equals(jsDivLot.getRecord(cntDivMtl - 1).getFieldString("YD_TO_LOC_DCSN_MTD"))) {
					/****************************************************************
					* 권상모음  : A -> B, AB -> C
					****************************************************************/
					for (int jj = 0; jj < cntDivMtl; jj++) {
						jrDivMtl = jsDivLot.getRecord(jj);
						ydToLocDcsnMtd = jrDivMtl.getFieldString("YD_TO_LOC_DCSN_MTD");
	
						if (jj == (cntDivMtl - 1)) {
							//Lot내 마지막 Row이면
							if ("S".equals(ydToLocDcsnMtd) || "B".equals(ydToLocDcsnMtd) || "A".equals(ydToLocDcsnMtd)) {
								//야드To위치결정방법이 'S', 'B', 'A'(입고시 연속된 재료 등) 이면
								//권상모음Bed사양 Check
								jrBedSpec.setField("LOT_MTL", jsDivLot);
								bedSpecOvGp = this.chkCollBedSpec(jrBedSpec);
								if (!"".equals(bedSpecOvGp)) {
									slabUtils.printLog(logId, "권상모음Lot[" + slabUtils.format(lotGrpNo + lotNo + 1, "00000") + "] Bed사양 초과 : " + bedSpecOvGp, "SL");
								}
							} else {
								//야드To위치결정방법이 'M', 'T'
								bedSpecOvGp = "MT";
							}

							if ("".equals(bedSpecOvGp)) {
								//Bed사양을 초과하지 않은 'S', 'B'
								//크레인작업재료에서 해당 Row부터 권상모음순서가 연속된 Record 삭제
								for (int kk = jj; kk >= 0; kk--) {
									if ((kk == jj) || "A".equals(jsDivLot.getRecord(kk).getFieldString("YD_TO_LOC_DCSN_MTD"))) {
										//크레인작업재료에서 같은 재료번호를 삭제
										stlNo = jsDivLot.getRecord(kk).getFieldString("STL_NO");
										cntCrnMtl = jsCrnMtl.size();
										for (int mm = 0; mm < cntCrnMtl; mm++) {
											if (stlNo.equals(jsCrnMtl.getRecord(mm).getFieldString("STL_NO"))) {
												jsCrnMtl.removeRecord(mm);
												break;
											}
										}
									} else {
										break;
									}
								}
							} else {
								//Bed사양을 초과한  'S', 'B' 또는 기타 야드To위치결정방법 -> 'T'
								stlNo = jrDivMtl.getFieldString("STL_NO");
								//크레인작업재료에서 같은 재료번호를 검색
								cntCrnMtl = jsCrnMtl.size();
								for (int kk = 0; kk < cntCrnMtl; kk++) {
									if (stlNo.equals(jsCrnMtl.getRecord(kk).getFieldString("STL_NO"))) {
										jsCrnMtl.getRecord(kk).setField("YD_TO_LOC_DCSN_MTD", "T"); //야드To위치결정방법(이적후'S')
										//To위치결정을 위하여 이전 야드To위치결정방법이 T -> S로 변경된 것으로 설정
										jrDivMtl.setField("BEF_YD_TO_LOC_DCSN_MTD", "T");
										break;
									}
								}
							}
						} else if ("B".equals(ydToLocDcsnMtd) || "M".equals(ydToLocDcsnMtd)) {
							//Lot마지막 재료가 아니고 ('S' 없음, 'A' Skip)
							//야드To위치결정방법이 'B'이면 현재위치에서 다음 권상모음순서위에 적치
							//야드To위치결정방법이 'M'이면 다른 곳에 이적했다가 다음 권상모음순서위에 적치
							//야드To위치결정방법이 'M'이면서 다른 곳에 이적할 필요 없이 바로 다음 권상모음순서위에 적치
							jsInsLot = JDTORecordFactory.getInstance().createRecordSet("");

							for (int kk = 0; kk <= jj; kk++) {
								jrInsMtl = JDTORecordFactory.getInstance().create();
								jrInsMtl.setRecord(jsDivLot.getRecord(kk));
								if (kk < jj) {
									jrInsMtl.setField("YD_TO_LOC_DCSN_MTD", "A"); //야드To위치결정방법(연속)
								} else if ("M".equals(ydToLocDcsnMtd)) {
									jrInsMtl.setField("YD_TO_LOC_DCSN_MTD", "R"); //야드To위치결정방법(다음 권상모음순서위에 적치)
								}
								jsInsLot.addRecord(jrInsMtl);

								//크레인작업재료에서 같은 재료번호를 삭제
								if ("B".equals(ydToLocDcsnMtd)) {
									stlNo = jsDivLot.getRecord(kk).getFieldString("STL_NO");
									cntCrnMtl = jsCrnMtl.size();
									for (int mm = 0; mm < cntCrnMtl; mm++) {
										if (stlNo.equals(jsCrnMtl.getRecord(mm).getFieldString("STL_NO"))) {
											jsCrnMtl.removeRecord(mm);
											break;
										}
									}
								}
							}
							
							//다음 권상모음순서위에 적치하여야할 Lot 추가
							cntInsMtl = jsInsLot.size();
							if (cntInsMtl > 0) {
								lotNo++;
								for (int mm = 0; mm < cntInsMtl; mm++) {
									jrInsMtl = jsInsLot.getRecord(mm);
									jrInsMtl.setField("LOT_NO"    , slabUtils.format(lotGrpNo + lotNo, "00000")); //Lot번호
									jrInsMtl.setField("LOT_IN_SEQ", String.valueOf(mm + 1)); //Lot내순서
								}
								//Lot추가
								vcLstLot.add(jsInsLot);
							}
						}	//if (jj == (cntDivMtl - 1))
					}	//for (int jj = 0; jj < cntDivMtl; jj++) {
				}	//if (!"X".equals(jsDivLot.getRecord(cntDivMtl - 1).getFieldString("YD_TO_LOC_DCSN_MTD")))

				//권상모음Lot 추가 후 최종 Lot 추가
				lotNo++;

				for (int mm = 0; mm < cntDivMtl; mm++) {
					jrInsMtl = jsDivLot.getRecord(mm);
					ydToLocDcsnMtd = jrInsMtl.getFieldString("YD_TO_LOC_DCSN_MTD");
					if (mm == (cntDivMtl - 1)) {
						if ("X".equals(ydToLocDcsnMtd)) {
							jrInsMtl.setField("YD_TO_LOC_DCSN_MTD", "Y"); //야드To위치결정방법(주작업 권하분리)
						} else {
							jrInsMtl.setField("YD_TO_LOC_DCSN_MTD", "S"); //야드To위치결정방법(권상모음Base재료)
						}
					} else if (!"A".equals(ydToLocDcsnMtd)) {
						jrInsMtl.setField("YD_TO_LOC_DCSN_MTD", "A"); //야드To위치결정방법(권상모음연속재료)
					}
					jrInsMtl.setField("LOT_NO"    , slabUtils.format(lotGrpNo + lotNo, "00000")); //Lot번호
					jrInsMtl.setField("LOT_IN_SEQ", String.valueOf(mm + 1)); //Lot내순서
				}

				vcLstLot.add(jsDivLot);
			}	//for (int ii = 0; ii < cntDivLot; ii++)
			
			if ("Y".equals(crnSchLogYn)) {
				slabUtils.printLog(logId, "4.권상모음재료Lot 및 크레인작업재료 정리 : 권상모음재료Lot수[" + vcLstLot.size() + "], 크레인작업재료수[" + jsCrnMtl.size() + "]", "SL");
				slabUtils.printParam(logId + " 4.권상모음Lot 편성 완료", vcLstLot, logItm);
			}

			/**********************************************************
			* 5. 크레인작업재료 Handling Lot 편성
			* 5.1 To위치결정방법 Check
			*  - 이전 Bed와 다름
			*  - 현재 주작업     , 이전 보조작업 : 현재 권상모음순서가 0보다 크고 이전 권상모음순서가 0인경우
			*  - 현재 보조작업, 이전 주작업      : 현재 권상모음순서가 0이고 이전 권상모음순서가 0보다 큰 경우
			*  - 현재 주작업     , 이전 주작업      : 현재 권상모음순서가 0보다 크고 이전권상모음순서도 0보다 클때 
			*    . 이전 및 현재 To위치결정방법이 모두 'A'가 아니면
			* 5.2  목표행선 Lot분리
			*  - 업무기준(YDB030)의 HandlingLot분리구분에 따라 분리
			*  - N:분리안함, R:목표행선분리, D:권하분리
			* 5.3 크레인사양 Lot분리
			*  - 차량상차 스케줄이면 최종 야드권상지시위치와 현재 야드권상지시위치와 달라야 함
			*  - Max단, Max중량, Max높이, 폭 허용공차 초과시 분리
			**********************************************************/
			//To위치결정방법 Check
			lotNo              = 0; 		//Lot번호
			int befBedSeq      = 0; 		//전 Bed순서
			int curBedSeq      = 0; 		//현 Bed순서
			int befYdUpCollSeq = 0; 		//전 야드권상모음순서
			int curYdUpCollSeq = 0; 		//현 야드권상모음순서
			String befYdToLocDcsnMtd = "";	//전 야드To위치결정방법
			String curYdToLocDcsnMtd = "";	//현 야드To위치결정방법
			String szScheduleCode = "";	    //작업스케쥴코드

			cntCrnMtl = jsCrnMtl.size();	//크레인작업재료수(권상모음제외)
			jsDivLot = null;				//분할Lot
			boolean newLotYn = false;		//신규Lot여부

			for (int ii = 0; ii < cntCrnMtl; ii++) {
				jrCrnMtl = jsCrnMtl.getRecord(ii);
				
				//To위치결정방법 Check
				curBedSeq         = jrCrnMtl.getFieldInt("BED_SEQ");
				curYdUpCollSeq    = jrCrnMtl.getFieldInt("YD_UP_COLL_SEQ");
				curYdToLocDcsnMtd = jrCrnMtl.getFieldString("YD_TO_LOC_DCSN_MTD");
				szScheduleCode    = jrCrnMtl.getFieldString("YD_SCH_CD");

				newLotYn = false;
				
				if ((curBedSeq != befBedSeq) ||
				    (curYdUpCollSeq > 0 && befYdUpCollSeq == 0) ||
				    (curYdUpCollSeq == 0 && befYdUpCollSeq > 0)) {
					newLotYn = true;
				} else if (curYdUpCollSeq > 0 && befYdUpCollSeq > 0 && !"A".equals(befYdToLocDcsnMtd)) {
					newLotYn = true;
				}
				
				//신규Lot이면
				if (newLotYn) {
					//이전 Lot 목표행선(크레인사양) Lot분리
					if (jsDivLot != null && jsDivLot.size() > 0) {
						jrHdLot.setField("UP_LOT_NO", String.valueOf(lotNo)); //상위 Lot번호
						jrHdLot.setField("UP_LOT"   , jsDivLot             ); //상위 Lot
						this.setSlabYdHdLotSpr(jrHdLot, vcRstLot);
					}
					
					lotNo++;
					jsDivLot = JDTORecordFactory.getInstance().createRecordSet("");
				}
				
				jsDivLot.addRecord(jrCrnMtl);

				befBedSeq         = curBedSeq;
				befYdUpCollSeq    = curYdUpCollSeq;
				befYdToLocDcsnMtd = curYdToLocDcsnMtd;
			}
			
			//마지막Lot 목표행선(크레인사양) Lot분리
			if (jsDivLot != null && jsDivLot.size() > 0) {
				jrHdLot.setField("UP_LOT_NO", String.valueOf(lotNo)); //상위 Lot번호
				jrHdLot.setField("UP_LOT"   , jsDivLot             ); //상위 Lot
				this.setSlabYdHdLotSpr(jrHdLot, vcRstLot);
			}

			if ("Y".equals(crnSchLogYn)) {
				slabUtils.printParam(logId + " 5.크레인작업재료Lot", vcRstLot, logItm);
			}

			/**********************************************************
			* 6. Handling Lot 통합
			*  - 크레인작업재료 Lot + 권상모음 Lot
			*  - M -> R Lot Check (동일 재료 연속 스케줄 삭제)
			**********************************************************/
			if (vcLstLot.size() > 0) {
				vcRstLot.addAll(vcLstLot);
			}

			//동일 재료 연속 스케줄 삭제
			int cntNextMtl          = 0;
			String nextStlNo        = "";
			boolean chkLotDel       = true;
			JDTORecordSet jsNextLot = null;

			cntDivLot = vcRstLot.size() - 2;

			for (int ii = 0; ii < cntDivLot; ii++) {
				jsDivLot   = (JDTORecordSet)vcRstLot.get(ii);
				jsNextLot  = (JDTORecordSet)vcRstLot.get(ii + 1);
				cntDivMtl  = jsDivLot.size();
				cntNextMtl = jsNextLot.size();
				chkLotDel  = true;

				//M -> R로 변경하는 Lot가 M 이 필요 없는지를 Check
				if (cntDivMtl == cntNextMtl) {
					for (int kk = 0; kk < cntDivMtl; kk++) {
						stlNo     = jsDivLot.getRecord(kk).getFieldString("STL_NO");
						nextStlNo = jsNextLot.getRecord(kk).getFieldString("STL_NO");
						
						if (!stlNo.equals(nextStlNo)) {
							//동일한 재료이면 유지
							chkLotDel = false;
							break;
						}
					}
					
					//동일한 Lot이면 삭제
					if (chkLotDel) {
						vcRstLot.remove(ii);
						cntDivLot--;
					}
				}
			}

			if ("Y".equals(crnSchLogYn)) {
				slabUtils.printParam(logId + " 6. 통합Lot(크레인작업재료 + 권상모음)", vcRstLot, logItm);
			}

			cntDivLot = vcRstLot.size();

			/**********************************************************
			* 7. 권상, 권하지시위치 설정 (가상)
			*  - Handling Lot 편성 결과 검증을 위함
			**********************************************************/
			int bedNo    = 0;			//가상으로 지정할 BedNo
			int cntAbLot = 0;			//이상Lot수(권상, 권하지시위치 없음)
			int cntMvMtl = 0;			//이적재료수
			String ydUpWoLoc    = "";	//야드권상지시위치
			String ydUpWoLayer  = "";	//야드권상지시단
			String ydDnWoLoc    = "";	//야드권하지시위치
			String ydDnWoLayer  = "";	//야드권하지시단
			String ydToLocGuide = jrParam.getFieldString("YD_TO_LOC_GUIDE"); //야드To위치Guide

			for (int ii = cntDivLot - 1; ii >= 0; ii--) {
				jsDivLot  = (JDTORecordSet)vcRstLot.get(ii);
				cntDivMtl = jsDivLot.size();
				cntMvMtl += cntDivMtl;
				
				//Lot 마지막 Record
				jrInsMtl = jsDivLot.getRecord(cntDivMtl - 1);
				ydToLocDcsnMtd = jrInsMtl.getFieldString("YD_TO_LOC_DCSN_MTD");	//야드To위치결정방법(대표)
				stlNo          = jrInsMtl.getFieldString("STL_NO"            );	//재료번호
				ydUpWoLoc      = jrInsMtl.getFieldString("YD_UP_WO_LOC"      );	//야드권상지시위치
				ydUpWoLayer    = jrInsMtl.getFieldString("YD_UP_WO_LAYER"    );	//야드권상지시단
				ydUpWoLoc      = jrInsMtl.getFieldString("YD_UP_WO_LOC"      );	//야드권상지시위치
				szScheduleCode = jrInsMtl.getFieldString("YD_SCH_CD");
				ydDnWoLoc      = "";
				ydDnWoLayer    = "";

				if ("W".equals(ydToLocDcsnMtd)) {
					//보조작업 : 현재적치위치 -> To위치결정위치
					//권하지시위치 : 다음Lot 권하분리여부에 따라 결정
					if (ii < cntDivLot - 1) {
						//다음Lot 권하분리
						jsCrnMtl = (JDTORecordSet)vcRstLot.get(ii + 1);
						if ("X".equals(jsCrnMtl.getRecord(jsCrnMtl.size() - 1).getFieldString("YD_TO_LOC_DCSN_MTD"))) {
							ydDnWoLoc = jsCrnMtl.getRecord(0).getFieldString("YD_UP_WO_LOC");
						}
					}
					//권하지시위치 : To위치결정위치
					if ("".equals(ydDnWoLoc)) {
						ydDnWoLoc = "XX0101" + slabUtils.format(++bedNo, "00");
						
						//----  항만야드 상차 권하위치 : MBPT??01 --> 항만슬라브야드 기능추가 - 2016.01.07 LeeJY
						slabUtils.printLog(logId, ">>> 항만야드 권하지시위치 설정1 : 작업스케쥴코드 [" + szScheduleCode + "]", "SL");
						if (!szScheduleCode.equals("") && szScheduleCode.substring(0,4).equals("MBPT") && szScheduleCode.substring(6,8).equals("UM") ) {
							//ydDnWoLoc = szScheduleCode.substring(0,6) + "01";
							if(!"".equals(carPointCd)){
								ydDnWoLoc = carPointCd;
							}
						}
					}
					ydDnWoLayer = "001";
				} else if ("S".equals(ydToLocDcsnMtd)) {
					//주작업(최종) : 현재적치위치('T' To위치결정위치) -> 최종위치
					befYdToLocDcsnMtd = jrInsMtl.getFieldString("BEF_YD_TO_LOC_DCSN_MTD");	//원래 야드To위치결정방법
					//원 야드To위치결정방법이 'S','B','A'가 아니면('M','T') 이적후 Base가 된 형태이므로 권상지시위치는 이전 Lot('T')의 권하지시위치가 됨
					if ("M".equals(befYdToLocDcsnMtd) || "T".equals(befYdToLocDcsnMtd)) {
						ydUpWoLoc = "XX0101" + slabUtils.format(++bedNo, "00");
						ydUpWoLayer = "001";
						
						//----  항만야드 상차 권하위치 : MBPT??01 --> 항만슬라브야드 기능추가 - 2016.01.07 LeeJY
						slabUtils.printLog(logId, ">>> 항만야드 권하지시위치 설정2 : 작업스케쥴코드 [" + szScheduleCode + "]", "SL");
						if (!szScheduleCode.equals("") && szScheduleCode.substring(0,4).equals("MBPT") && szScheduleCode.substring(6,8).equals("UM")) {
							//ydDnWoLoc = szScheduleCode.substring(0,6) + "01";
							if(!"".equals(carPointCd)){
								ydDnWoLoc = carPointCd;
							}
						}
					}

					//권하지시위치 : 최종 목적지
					if ("".equals(ydToLocGuide)) {
						//야드To위치Guide 값이 없으면 다음Lot 권하분리여부에 따라 결정
						if (ii < cntDivLot - 1) {
							//다음Lot 권하분리
							jsCrnMtl = (JDTORecordSet)vcRstLot.get(ii + 1);
							if ("Y".equals(jsCrnMtl.getRecord(jsCrnMtl.size() - 1).getFieldString("YD_TO_LOC_DCSN_MTD"))) {
								ydDnWoLoc = jsCrnMtl.getRecord(0).getFieldString("YD_UP_WO_LOC");
							}
						}
						//권하지시위치 : To위치결정위치
						if ("".equals(ydDnWoLoc)) {
							ydDnWoLoc = "XX0101" + slabUtils.format(++bedNo, "00");
							
							//----  항만야드 상차 권하위치 : MBPT??01 --> 항만슬라브야드 기능추가 - 2016.01.07 LeeJY
							slabUtils.printLog(logId, ">>> 항만야드 권하지시위치 설정3 : 작업스케쥴코드 [" + szScheduleCode + "]", "SL");
							if (!szScheduleCode.equals("") && szScheduleCode.substring(0,4).equals("MBPT") && szScheduleCode.substring(6,8).equals("UM")) {
								//ydDnWoLoc = szScheduleCode.substring(0,6) + "01";
								if(!"".equals(carPointCd)){
									ydDnWoLoc = carPointCd;
								}
							}
						}
						ydDnWoLayer = "001";
						
					} else {
						//야드To위치Guide 값이 있으면 지정된 위치로
						ydDnWoLoc = ydToLocGuide;
						ydDnWoLayer = slabUtils.format(jrInsMtl.getFieldInt("YD_UP_COLL_SEQ") - cntDivMtl + 1, "000");
					}
				} else if ("X".equals(ydToLocDcsnMtd) || "Y".equals(ydToLocDcsnMtd)) {
					//권하분리작업 : 상위Lot의 권하위치 -> To위치결정위치
					//권상지시위치 : 현재Lot 권하분리여부에 따라 결정
					ydUpWoLoc = "XX0101" + slabUtils.format(++bedNo, "00");
					//권상지시단 : 이전Lot 재료수 - 현재Lot 재료수 + 1
					jsCrnMtl = (JDTORecordSet)vcRstLot.get(ii - 1);
					ydUpWoLayer = slabUtils.format(jsCrnMtl.size() - cntDivMtl + 1, "000");

					//권하지시위치 : 다음Lot 권하분리여부에 따라 결정
					if (ii < cntDivLot - 1) {
						//다음Lot 권하분리
						jsCrnMtl = (JDTORecordSet)vcRstLot.get(ii + 1);
						befYdToLocDcsnMtd = jsCrnMtl.getRecord(jsCrnMtl.size() - 1).getFieldString("YD_TO_LOC_DCSN_MTD");
						if ("X".equals(befYdToLocDcsnMtd) || "Y".equals(befYdToLocDcsnMtd)) {
							ydDnWoLoc = jsCrnMtl.getRecord(0).getFieldString("YD_UP_WO_LOC");
						}
					}
					//권하지시위치 : To위치결정위치
					if ("".equals(ydDnWoLoc)) {
						ydDnWoLoc = "XX0101" + slabUtils.format(++bedNo, "00");
						
						//----  항만야드 상차 권하위치 : MBPT??01 --> 항만슬라브야드 기능추가 - 2016.01.07 LeeJY
						slabUtils.printLog(logId, ">>> 항만야드 권하지시위치 설정4 : 작업스케쥴코드 [" + szScheduleCode + "]", "SL");
						if (!szScheduleCode.equals("") && szScheduleCode.substring(0,4).equals("MBPT") && szScheduleCode.substring(6,8).equals("UM")) {
							//ydDnWoLoc = szScheduleCode.substring(0,6) + "01";
							if(!"".equals(carPointCd)){
								ydDnWoLoc = carPointCd;
							}
						}
					}
					ydDnWoLayer = "001";
				} else {
					//주작업(모음)
					//T : 현재적치위치 -> To위치결정위치 -> 최종위치
					//B : 현재적치위치 -> 다음 권상모음순서 상단
					//M : 현재적치위치 -> To위치결정위치 -> 다음 권상모음순서 상단
					//R : 'M' To위치결정위치 -> 다음 권상모음순서 상단 or 현재적치위치 -> 다음 권상모음순서 상단
					if ("R".equals(ydToLocDcsnMtd)) {
						if (ii > 0) {
							for (int jj = ii - 1; jj >= 0; jj--) {
								jsCrnMtl = (JDTORecordSet)vcRstLot.get(jj);
								cntCrnMtl = jsCrnMtl.size();
								for (int kk = 0; kk < cntCrnMtl; kk++) {
									jrCrnMtl = jsCrnMtl.getRecord(kk);
									if (stlNo.equals(jrCrnMtl.getFieldString("STL_NO"))) {
										ydUpWoLoc   = "XX0101" + slabUtils.format(++bedNo, "00");
										ydUpWoLayer = "001";
										break;
									}
								}
								if (ydUpWoLoc.startsWith("X")) { break; }
							}
						}
					}
					
					//권하지시위치 : 다음 To위치결정위치(권상모음순서 상단)
					for (int jj = ii + 1; jj < cntDivLot; jj++) {
						jsCrnMtl = (JDTORecordSet)vcRstLot.get(jj);
						cntCrnMtl = jsCrnMtl.size();
						for (int kk = 0; kk < cntCrnMtl; kk++) {
							jrCrnMtl = jsCrnMtl.getRecord(kk);
							if (stlNo.equals(jrCrnMtl.getFieldString("STL_NO"))) {
								ydDnWoLoc = jrCrnMtl.getFieldString("YD_UP_WO_LOC");
								ydDnWoLayer = slabUtils.format(jrCrnMtl.getFieldInt("YD_UP_WO_LAYER") + cntCrnMtl - kk - 1, "000");
								break;
							}
						}
						if (!"".equals(ydDnWoLoc)) { break; }
					}
				}
				
				//결정된 권상, 권하지시위치 Set
				for (int jj = 0; jj < cntDivMtl; jj++) {
					jrInsMtl = jsDivLot.getRecord(jj);
					jrInsMtl.setField("YD_UP_WO_LOC"  , ydUpWoLoc  );	//야드권상지시위치
					jrInsMtl.setField("YD_UP_WO_LAYER", ydUpWoLayer);	//야드권상지시단
					jrInsMtl.setField("YD_DN_WO_LOC"  , ydDnWoLoc  );	//야드권하지시위치
					jrInsMtl.setField("YD_DN_WO_LAYER", ydDnWoLayer);	//야드권하지시단
					jrInsMtl.setField("YD_STK_LYR_NO" , slabUtils.format(cntDivMtl - jj, "000"));	//야드적치단번호(크레인작업재료)
				}
				
				if ("".equals(ydUpWoLoc) || "".equals(ydDnWoLoc)) {
					cntAbLot++;
				}
			}

			//권상, 권하지시위치 값이 없는 재료가 있으면 Lot편성에 문제가 있음
			if (cntAbLot > 0) {
				slabUtils.printParam(logId + " Handling Lot 편성 오류", vcRstLot, logItm);
				throw new Exception("오류:Handling Lot 편성 >> 권상, 권하지시위치 값이 없는 Lot 발생 [" + cntAbLot + "]");
			}

			/**********************************************************
			* 8. 권하위치 역전 Lot 찾기
			**********************************************************/
			cntAbLot = 0;
			
			for (int ii = 0; ii < cntDivLot; ii++) {
				jsDivLot  = (JDTORecordSet)vcRstLot.get(ii);
				cntDivMtl = jsDivLot.size();
				ydDnWoLoc = jsDivLot.getRecord(cntDivMtl - 1).getFieldString("YD_DN_WO_LOC");
				befBedSeq = jsDivLot.getRecord(cntDivMtl - 1).getFieldInt("YD_DN_WO_LAYER");

				for (int jj = ii; jj < cntDivLot; jj++) {
					jsInsLot  = (JDTORecordSet)vcRstLot.get(jj);
					cntDivMtl = jsInsLot.size();
					
					if (ydDnWoLoc.equals(jsInsLot.getRecord(cntDivMtl - 1).getFieldString("YD_DN_WO_LOC")) &&
						befBedSeq >	jsInsLot.getRecord(cntDivMtl - 1).getFieldInt("YD_DN_WO_LAYER")) {
						slabUtils.printParam(logId + " 8.권하지시위치가 같고 단이 역전된 Lot", jsDivLot, logItm);
						slabUtils.printParam(logId + " 8.권하지시위치가 같고 단이 역전된 Lot", jsInsLot, logItm);
						cntAbLot++;
					}
				}
			}

			//권하지시위치가 같고 단이 역전된 재료가 있으면 Lot편성 순서에 문제가 있음
			if (cntAbLot > 0) {
				slabUtils.printParam(logId + " Handling Lot 편성 오류", vcRstLot, logItm);
				throw new Exception("오류:Handling Lot 편성 >> 권하지시위치가 같고 단이 역전된 Lot 발생 [" + cntAbLot + "]");
			}

			if ("Y".equals(crnSchLogYn)) {
				slabUtils.printParam(logId + " 7.Handling Lot 편성 결과", vcRstLot, logItm);
			}
			slabUtils.printLog(logId, "Handling Lot 편성 완료 : 총Lot수[" + cntDivLot + "], 이적재료수[" + cntMvMtl + "], 소요Bed수[" + bedNo + "]", "SL");
			slabUtils.printLog(logId, methodNm, "S-");

			return vcRstLot;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 Handling Lot 편성 - HandlingLot분리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrUpLot
	 *      @param Vector vcLot
	 *      @return void
	 *      @throws DAOException
	*/
	public void setSlabYdHdLotSpr(JDTORecord jrUpLot, Vector vcLot) throws DAOException {
		String methodNm = "HandlingLot분리[SlabYdSchSeEJB.setSlabYdHdLotSpr] < " + jrUpLot.getResultMsg();
		String logId = jrUpLot.getResultCode();

		try {
			JDTORecord    jrRow = null;	//현재 Row
			JDTORecordSet jsLot = JDTORecordFactory.getInstance().createRecordSet("");	//Lot

			//Parameter
			int lotNo   = 1; 										//Lot번호
			int upLotNo = jrUpLot.getFieldInt("UP_LOT_NO") * 100;	//상위 Lot번호
			String crnSchLogYn = jrUpLot.getFieldString("CRN_SCH_LOG_YN");	//크레인스케줄Log여부

			JDTORecordSet jsUpLot   = (JDTORecordSet)jrUpLot.getField("UP_LOT");	//상위 Lot
			JDTORecord    jrCrnSpec = (JDTORecord)jrUpLot.getField("CRN_SPEC");		//크레인사양
			jrCrnSpec.setResultCode(logId);		//Log ID
			jrCrnSpec.setResultMsg(methodNm);	//Log Method Name
			
			//추가 Lot분리 편성용
			JDTORecord jrHdLot = JDTORecordFactory.getInstance().create();
			jrHdLot.setResultCode(logId);	//Log ID
			jrHdLot.setResultMsg(methodNm);	//Log Method Name
			jrHdLot.setField("CRN_SCH_LOG_YN", crnSchLogYn);	//크레인스케줄Log여부
			jrHdLot.setField("CRN_SPEC"      , jrCrnSpec  );	//크레인사양
			
			int rowCnt = jsUpLot.size();

			/**********************************************************
			* 1. HandlingLot분리구분 Check
			*  - 최종 야드To위치결정방법이 보조작업(W) 이면 
			*   . HandlingLot분리구분(보조작업)
			*  - 최종 야드To위치결정방법이 주작업(S,T) 이고
			*    작업예약 야드To위치결정방법이 작업자지정(F)이 아니면
			*   . HandlingLot분리구분(주작업)
			*  - 기타 Lot분리 안함
			**********************************************************/
			//야드To위치결정방법(작업예약)
			String ydToLocDcsnMtd = jrUpLot.getFieldString("YD_TO_LOC_DCSN_MTD");

			//Lot 마지막 Row 야드To위치결정방법
			jrRow = jsUpLot.getRecord(rowCnt - 1);
			String lstYdToLocDcsnMtd = slabUtils.trim(jrRow.getFieldString("YD_TO_LOC_DCSN_MTD"));

			//HandlingLot분리구분(N:분리안함, R:목표행선분리, X:권하분리)
			//주작업 권하분리 조건 확인 필요?????
			String hdLotSprGp = "N";

			if ("W".equals(lstYdToLocDcsnMtd)) {
				hdLotSprGp = jrUpLot.getFieldString("HD_LOT_SPR_GP_AW");	//보조작업
			} else if (("S".equals(lstYdToLocDcsnMtd) || "T".equals(lstYdToLocDcsnMtd)) &&
					   !"F".equals(ydToLocDcsnMtd)) {
				hdLotSprGp = jrUpLot.getFieldString("HD_LOT_SPR_GP_MW");	//주작업
			}

			if ("Y".equals(crnSchLogYn)) {
				slabUtils.printLog(logId, "1-Lot분할-HandlingLot   >> 상위Lot[" + upLotNo + "0-" + rowCnt + "건], Lot분리구분[" + hdLotSprGp + "]", "SL");
			}

			/**********************************************************
			* 2. 목표행선 및 크레인사양 Lot분리
			*  - HandlingLot분리구분이 분리안함(N)이면
			*   . 크레인사양Lot분리
			*  - HandlingLot분리구분이 목표행선분리(R)이면
			*   . 목표행선Lot분리 후 크레인사양Lot분리
			*  - HandlingLot분리구분이 권하분리(D)이면
			*   . 크레인사양Lot분리 후 목표행선Lot분리
			**********************************************************/
			if ("R".equals(hdLotSprGp)) {
				//목표행선Lot분리 후 크레인사양Lot분리
				String aimRtChgGp = "";	//목표행선변경구분(참고항목)

				for (int ii = 0; ii < rowCnt; ii++) {
					jrRow = jsUpLot.getRecord(ii);

					if (ii > 0) {
						aimRtChgGp = this.chkYdAimRtGp(logId, methodNm, jsUpLot.getRecord(ii - 1), jrRow);
					}
					
					if (!"".equals(aimRtChgGp)) {
						//전 행선과 다르거나 장입행선이고 순서가 맞지 않을 경우 이전 Lot 크레인사양Lot분리
						if ("Y".equals(crnSchLogYn)) {
							slabUtils.printLog(logId, "2-Lot분할-목표행선[R]   >> 상위Lot[" + (upLotNo + lotNo) + "0-" + jsLot.size() + "건], 분할구분[RT-" + aimRtChgGp + "]", "SL");
						}

						jrHdLot.setField("UP_LOT_NO", String.valueOf(upLotNo + lotNo)); //상위 Lot번호
						jrHdLot.setField("UP_LOT"   , jsLot                          ); //상위 Lot
						this.setSlabYdHdLotSprCs(jrHdLot, vcLot);
						
						jsLot = JDTORecordFactory.getInstance().createRecordSet("");
						lotNo++;
					}
					
					jrRow.setField("AIM_RT_CHG_GP", aimRtChgGp); //목표행선변경구분(참고항목)
					jsLot.addRecord(jrRow);
				}
				
				if ("Y".equals(crnSchLogYn)) {
					slabUtils.printLog(logId, "2-Lot분할-목표행선[R]   >> 상위Lot[" + (upLotNo + lotNo) + "0-" + jsLot.size() + "건]", "SL");
				}

				//마지막 Lot 크레인사양Lot분리
				jrHdLot.setField("UP_LOT_NO", String.valueOf(upLotNo + lotNo)); //상위 Lot번호
				jrHdLot.setField("UP_LOT"   , jsLot                          ); //상위 Lot
				this.setSlabYdHdLotSprCs(jrHdLot, vcLot);
			} else if ("X".equals(hdLotSprGp)) {
				//크레인사양Lot분리 후 권하분리
				String crnSpecOvGp = "";	//크레인사양초과구분(참고항목)

				int   mtlWt    = 0;		//재료중량
				float mtlT     = 0;		//재료두께
				float mtlW     = 0;		//재료폭
				int   mtlShSum = 0;		//재료매수합
				int   mtlWtSum = 0;		//재료중량합
				float mtlTSum  = 0;		//재료두께합
				float mtlWMax  = 0;		//재료폭최대

				for (int ii = 0; ii < rowCnt; ii++) {
					jrRow = jsUpLot.getRecord(ii);
					
					mtlWt = Integer.parseInt(slabUtils.nvl(jrRow.getFieldString("YD_MTL_WT"),"0"));
					mtlT  = Float.parseFloat(slabUtils.nvl(jrRow.getFieldString("YD_MTL_T" ),"0"));
					mtlW  = Float.parseFloat(slabUtils.nvl(jrRow.getFieldString("YD_MTL_W" ),"0"));
					
					if (ii > 0) {
						mtlShSum++;
						mtlWtSum += mtlWt;
						mtlTSum  += mtlT;
						
						jrCrnSpec.setField("MTL_SH_SUM", String.valueOf(mtlShSum));	//재료매수합
						jrCrnSpec.setField("MTL_WT_SUM", String.valueOf(mtlWtSum));	//재료중량합
						jrCrnSpec.setField("MTL_T_SUM" , String.valueOf(mtlTSum ));	//재료두께합
						jrCrnSpec.setField("MTL_W"     , String.valueOf(mtlW    ));	//재료폭
						jrCrnSpec.setField("MTL_W_MAX" , String.valueOf(mtlWMax ));	//재료폭최대

						//크레인사양 초과 Check
						crnSpecOvGp = slabComm.chkCrnSpec(jrCrnSpec);

						if (!"".equals(crnSpecOvGp)) {
							//크레인사양 초과 시 이전 Lot 권하분리
							if ("Y".equals(crnSchLogYn)) {
								slabUtils.printLog(logId, "2-Lot분할-권하분리[X]   >> 상위Lot[" + (upLotNo + lotNo) + "0-" + jsLot.size() + "건], 분할구분[CS-" + crnSpecOvGp + "]", "SL");
							}

							jrHdLot.setField("UP_LOT_NO", String.valueOf(upLotNo + lotNo)); //상위 Lot번호
							jrHdLot.setField("UP_LOT"   , jsLot                          ); //상위 Lot
							this.setSlabYdHdLotSprDn(jrHdLot, vcLot);
							
							jsLot = JDTORecordFactory.getInstance().createRecordSet("");
							lotNo++;
							mtlShSum = 1;
							mtlWtSum = mtlWt;
							mtlTSum  = mtlT;
							mtlWMax  = mtlW;
						}
					} else {
						mtlShSum = 1;
						mtlWtSum = mtlWt;
						mtlTSum  = mtlT;
						mtlWMax  = mtlW;
					}

					if (mtlW > mtlWMax) { mtlWMax = mtlW; }

					jrRow.setField("CRN_SPEC_OV_GP", crnSpecOvGp); //크레인사양Lot분리구분
					jsLot.addRecord(jrRow);
				}
				
				if ("Y".equals(crnSchLogYn)) {
					slabUtils.printLog(logId, "2-Lot분할-권하분리[X]   >> 상위Lot[" + (upLotNo + lotNo) + "0-" + jsLot.size() + "건]", "SL");
				}

				//마지막 Lot 권하분리
				jrHdLot.setField("UP_LOT_NO", String.valueOf(upLotNo + lotNo)); //상위 Lot번호
				jrHdLot.setField("UP_LOT"   , jsLot                          ); //상위 Lot
				this.setSlabYdHdLotSprDn(jrHdLot, vcLot);
			} else {
				//목표행선Lot분리 안함
				for (int ii = 0; ii < rowCnt; ii++) {
					jrRow = jsUpLot.getRecord(ii);
					jrRow.setField("AIM_RT_CHG_GP", ""); //목표행선변경구분(참고항목)
					jsLot.addRecord(jrRow);
				}

				if ("Y".equals(crnSchLogYn)) {
					slabUtils.printLog(logId, "2-Lot분할-분리안함[N]   >> 상위Lot[" + (upLotNo + lotNo) + "0-" + jsLot.size() + "건]", "SL");
				}
				
				//크레인사양Lot분리
				jrHdLot.setField("UP_LOT_NO", String.valueOf(upLotNo + 1)); //상위 Lot번호
				jrHdLot.setField("UP_LOT"   , jsLot                      ); //상위 Lot
				this.setSlabYdHdLotSprCs(jrHdLot, vcLot);
			}
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 Handling Lot 편성 - 권하분리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrUpLot
	 *      @param Vector vcLot
	 *      @return void
	 *      @throws DAOException
	*/
	public void setSlabYdHdLotSprDn(JDTORecord jrUpLot, Vector vcLot) throws DAOException {
		String methodNm = "권하분리[SlabYdSchSeEJB.setSlabYdHdLotSprDn] < " + jrUpLot.getResultMsg();
		String logId = jrUpLot.getResultCode();

		try {
			JDTORecord    jrRow = null;	//현재 Row
			JDTORecordSet jsLot = null;	//Lot

			//Parameter
			int lotNo   = 0; 										//Lot번호
			int upLotNo = jrUpLot.getFieldInt("UP_LOT_NO") * 10;	//상위 Lot번호
			String crnSchLogYn = jrUpLot.getFieldString("CRN_SCH_LOG_YN");	//크레인스케줄Log여부

			JDTORecordSet jsUpLot = (JDTORecordSet)jrUpLot.getField("UP_LOT");	//상위 Lot
			
			int rowCnt = jsUpLot.size() - 1;

			/**********************************************************
			* 1. 크레인사양Lot분리 후 목표행선Lot분리
			*  - HandlingLot분리구분이 권하분리(D)인 경우
			*  - 전 행선과 다르거나 장입행선이고 순서가 맞지 않을 경우 신규Lot
			*  - 권하분리 Lot 추가
			**********************************************************/
			String aimRtChgGp = "";	//목표행선변경구분(참고항목)

			for (int ii = rowCnt; ii >= 0; ii--) {
				//전 행선과 다르거나 장입행선이고 순서가 맞지 않을 경우 신규Lot
				if (ii < rowCnt) {
					aimRtChgGp = this.chkYdAimRtGp(logId, methodNm, jsUpLot.getRecord(ii), jsUpLot.getRecord(ii + 1));
				}

				//처음Lot는 전체, 2번째 이후는 권하분리일 경우
				if (lotNo == 0 || !"".equals(aimRtChgGp)) {
					lotNo++;
					jsLot = JDTORecordFactory.getInstance().createRecordSet("");

					for (int jj = 0; jj <= ii; jj++) {
						jrRow = JDTORecordFactory.getInstance().create();
						jrRow.setRecord(jsUpLot.getRecord(jj));

						//권하분리일 경우 마지막 Row 야드To위치결정방법을 'X'로 변경
						if (!"".equals(aimRtChgGp)) {
							if (jj == 0) {
								jrRow.setField("AIM_RT_CHG_GP", aimRtChgGp); //목표행선변경구분(참고항목)
							} 
							if (jj == ii) {
								jrRow.setField("YD_TO_LOC_DCSN_MTD", "X"); //야드To위치결정방법
							}
						}
						jrRow.setField("LOT_NO"    , slabUtils.format(upLotNo + lotNo, "00000")); //Lot번호
						jrRow.setField("LOT_IN_SEQ", String.valueOf(jj + 1)                    ); //Lot내순서

						jsLot.addRecord(jrRow);
					}

					vcLot.add(jsLot);

					if ("Y".equals(crnSchLogYn)) {
						if ("".equals(aimRtChgGp)) {
							slabUtils.printLog(logId, "3-Lot분할-권하분리      >> 분할Lot[" + (upLotNo + lotNo) + "-" + jsLot.size() + "건]", "SL");
						} else {
							slabUtils.printLog(logId, "3-Lot분할-권하분리      >> 분할Lot[" + (upLotNo + lotNo) + "-" + jsLot.size() + "건], 분할구분[RT-" + aimRtChgGp + "]", "SL");
						}
					}
				}
			}
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 크레인사양Lot분리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrUpLot
	 *      @param Vector vcLot
	 *      @return void
	 *      @throws DAOException
	*/
	public void setSlabYdHdLotSprCs(JDTORecord jrUpLot, Vector vcLot) throws DAOException {
		String methodNm = "크레인사양Lot분리[SlabYdSchSeEJB.setSlabYdHdLotSprCs] < " + jrUpLot.getResultMsg();
		String logId = jrUpLot.getResultCode();

		try {
			JDTORecord    jrRow = null;	//현재 Row
			JDTORecordSet jsLot = JDTORecordFactory.getInstance().createRecordSet("");	//Lot
			String crnSpecOvGp  = "";	//크레인사양초과구분(참고항목)

			//크레인사양Lot분리
			int   mtlWt    = 0;		//재료중량
			float mtlT     = 0;		//재료두께
			float mtlW     = 0;		//재료폭
			int   mtlWtSum = 0;		//재료중량합
			float mtlTSum  = 0;		//재료두께합
			float mtlWMax  = 0;		//재료폭최대

			//Parameter
			int lotNo   = 1; 										//현재 Lot번호
			int lotInNo = 1; 										//Lot내순서
			int upLotNo = jrUpLot.getFieldInt("UP_LOT_NO") * 10;	//상위 Lot번호
			String crnSchLogYn = jrUpLot.getFieldString("CRN_SCH_LOG_YN");	//크레인스케줄Log여부

			JDTORecordSet jsUpLot   = (JDTORecordSet)jrUpLot.getField("UP_LOT");	//상위 Lot
			JDTORecord    jrCrnSpec = (JDTORecord)jrUpLot.getField("CRN_SPEC");		//크레인사양
			jrCrnSpec.setResultCode(logId);		//Log ID
			jrCrnSpec.setResultMsg(methodNm);	//Log Method Name

			int rowCnt = jsUpLot.size();
			String Is_plate_StoA_Scarfing = "N"; 
			/**********************************************************
			* 1. 크레인사양 Lot 편성
			*  - Max단, Max중량, Max높이, 폭 허용공차 초과시 Lot분리
			**********************************************************/
			for (int ii = 0; ii < rowCnt; ii++) {
				jrRow = jsUpLot.getRecord(ii);
				
				mtlWt = Integer.parseInt(slabUtils.nvl(jrRow.getFieldString("YD_MTL_WT"),"0"));
				mtlT  = Float.parseFloat(slabUtils.nvl(jrRow.getFieldString("YD_MTL_T" ),"0"));
				mtlW  = Float.parseFloat(slabUtils.nvl(jrRow.getFieldString("YD_MTL_W" ),"0"));
				
				Is_plate_StoA_Scarfing = slabUtils.nvl(jrRow.getFieldString("PLATE_SIDE_SCARFING_YN" ),"N");
				if (ii > 0) {
					lotInNo++;
					mtlWtSum += mtlWt;
					mtlTSum  += mtlT;
					
					jrCrnSpec.setField("MTL_SH_SUM", String.valueOf(lotInNo ));	//재료매수합
					jrCrnSpec.setField("MTL_WT_SUM", String.valueOf(mtlWtSum));	//재료중량합
					jrCrnSpec.setField("MTL_T_SUM" , String.valueOf(mtlTSum ));	//재료두께합
					jrCrnSpec.setField("MTL_W"     , String.valueOf(mtlW    ));	//재료폭
					jrCrnSpec.setField("MTL_W_MAX" , String.valueOf(mtlWMax ));	//재료폭최대
					
					jrCrnSpec.setField("PLATE_SIDE_SCARFING_YN" , Is_plate_StoA_Scarfing);	//통합적치이력 있는 후판재 사이드스카핑 여부
					//크레인사양 초과 Check
					crnSpecOvGp = slabComm.chkCrnSpec(jrCrnSpec);

					if (!"".equals(crnSpecOvGp)) {
						//이전 Lot 추가
						vcLot.add(jsLot);

						if ("Y".equals(crnSchLogYn)) {
							slabUtils.printLog(logId, "3-Lot분할-크레인사양    >> 분할Lot[" + (upLotNo + lotNo) + "-" + jsLot.size() + "건], 분할구분[CS-" + crnSpecOvGp + "]", "SL");
						}
						
						jsLot = JDTORecordFactory.getInstance().createRecordSet("");
						lotNo++;
						lotInNo  = 1;
						mtlWtSum = mtlWt;
						mtlTSum  = mtlT;
						mtlWMax  = mtlW;
					}
				} else {
					mtlWtSum = mtlWt;
					mtlTSum  = mtlT;
					mtlWMax  = mtlW;
				}
				if (mtlW > mtlWMax) { mtlWMax = mtlW; }

				jrRow.setField("CRN_SPEC_OV_GP", crnSpecOvGp                               ); //크레인사양Lot분리구분
				jrRow.setField("LOT_NO"        , slabUtils.format(upLotNo + lotNo, "00000")); //Lot번호
				jrRow.setField("LOT_IN_SEQ"    , String.valueOf(lotInNo)                   ); //Lot내순서
				
				jsLot.addRecord(jrRow);
			}
			
			//마지막 Lot 추가
			vcLot.add(jsLot);

			if ("Y".equals(crnSchLogYn)) {
				slabUtils.printLog(logId, "3-Lot분할-크레인사양    >> 분할Lot[" + (upLotNo + lotNo) + "-" + jsLot.size() + "건]", "SL");
			}
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 목표행선 Check
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param String logId
	 *      @param String mthdNm
	 *      @param JDTORecord jrBefRow
	 *      @param JDTORecord jrCurRow
	 *      @return String
	 *      @throws DAOException
	*/
	public String chkYdAimRtGp(String logId, String mthdNm, JDTORecord jrBefRow, JDTORecord jrCurRow) throws DAOException {
		String methodNm = "목표행선Check[SlabYdSchSeEJB.chkYdAimRtGp] < " + mthdNm;
    	
		try {
	    	String befYdAimRtGp  = slabUtils.trim(jrBefRow.getFieldString("YD_AIM_RT_GP" ));	//전 야드목표행선구분
	    	String befYdStkLotCd = slabUtils.trim(jrBefRow.getFieldString("YD_STK_LOT_CD"));	//전 야드산적Lot코드
	    	String curYdAimRtGp  = slabUtils.trim(jrCurRow.getFieldString("YD_AIM_RT_GP" ));	//현 야드목표행선구분
	    	String curYdStkLotCd = slabUtils.trim(jrCurRow.getFieldString("YD_STK_LOT_CD"));	//현 야드산적Lot코드


	    	//전 행선과 다르거나
			if (!curYdAimRtGp.equals(befYdAimRtGp)) {
				return "R";
			}
			
			//장입행선이고 순서가 맞지 않을 경우 True
			String warnMsg = ""; //경고 Message
			
			if (curYdAimRtGp.startsWith("C") && curYdStkLotCd.length() > 2 && befYdStkLotCd.length() > 2) {
				if (befYdStkLotCd.length() < 6) {
					warnMsg = "전 장입행선 야드산적Lot코드 이상[야드목표행선구분:" + befYdAimRtGp + ", 야드산적Lot코드:" + befYdStkLotCd + "]";
				} else if (curYdStkLotCd.length() < 6) {
					warnMsg = "현 장입행선 야드산적Lot코드 이상[야드목표행선구분:" + curYdAimRtGp + ", 야드산적Lot코드:" + curYdStkLotCd + "]";
				}

				if ("".equals(warnMsg)) {
					befYdStkLotCd = befYdStkLotCd.substring(2);
					curYdStkLotCd = curYdStkLotCd.substring(2);
		
					if (!slabUtils.isNumber(befYdStkLotCd)) {
						warnMsg = "전 장입행선 야드산적Lot코드 이상[야드목표행선구분:" + befYdAimRtGp + ", 야드산적Lot코드:" + befYdStkLotCd + "]";
					} else if (!slabUtils.isNumber(curYdStkLotCd)) {
						warnMsg = "현 장입행선 야드산적Lot코드 이상[야드목표행선구분:" + curYdAimRtGp + ", 야드산적Lot코드:" + curYdStkLotCd + "]";
					}
				}

				//경고 Message가 있으면 Return
				if (!"".equals(warnMsg)) {
					slabUtils.printWarnLog(logId, methodNm, warnMsg);
					return "";
				}

				long befYdChgNo = Long.parseLong(befYdStkLotCd); //전 야드장입순위
				long curYdChgNo = Long.parseLong(curYdStkLotCd); //현 야드장입순위

				if (befYdChgNo > curYdChgNo) {
					return "C";
				}
			}

			return "";
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
    }


	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 권상모음Bed사양Check
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrBedSpec
	 *      @return String
	 *      @throws DAOException
	*/
	public String chkCollBedSpec(JDTORecord jrBedSpec) throws DAOException {
		String methodNm = "권상모음Bed사양Check[SlabYdSchSeEJB.chkCollBedSpec] < " + jrBedSpec.getResultMsg();
		String logId = jrBedSpec.getResultCode();

		try {
			//점검결과
			String bedSpecOvGp = "";	//Bed사양초과구분

			//(설비Bed제외)권상모음 시 'T'가 발생되는 것을 막기 위한 Max단 초과 허용 매수
			int maxLyrOvSh = Integer.parseInt(slabUtils.nvl(jrBedSpec.getFieldString("MAX_LYR_OV_SH"),"0"));	//Max단초과매수
			//Bed사양 - Bed별 사양 및 최하단 적치단 아래의 재료사양 합계
			JDTORecordSet jsBedSpec = (JDTORecordSet)jrBedSpec.getField("BED_SPEC");
			//Lot재료
			JDTORecordSet jsLotMtl = (JDTORecordSet)jrBedSpec.getField("LOT_MTL");

			JDTORecord jrBsRow = null;	//BedBase Row
			JDTORecord jrLmRow = null;	//Lot재료 Row

			//Bed사양
			int   bbCnt = jsBedSpec.size();	//BedBase수
			int   ydStkBedLyrMax = 0;		//야드적치Bed단Max
			float ydStkBedHMax   = 0;		//야드적치Bed높이Max
			int   ydMtlShBase    = 0;		//야드재료매수
			float ydMtlTBase     = 0;		//야드재료두께

			//Lot재료
			int   mtlShSum = jsLotMtl.size();	//재료매수합
			float mtlTSum  = 0;					//재료두께합

			//최하단의 야드권상지시위치
			String ydUpWoLoc = jsLotMtl.getRecord(mtlShSum - 1).getFieldString("YD_UP_WO_LOC");
			
			for (int ii = 0; ii < bbCnt; ii++) {
				//야드권상지시위치(적치열구분+Bed번호)가 같은 Bed정보를 찾으면
				if (ydUpWoLoc.equals(jsBedSpec.getRecord(ii).getFieldString("YD_UP_WO_LOC"))) {
					jrBsRow = jsBedSpec.getRecord(ii);
					ydStkBedLyrMax = Integer.parseInt(slabUtils.nvl(jrBsRow.getFieldString("YD_STK_BED_LYR_MAX"),"0"));
					ydStkBedHMax   = Float.parseFloat(slabUtils.nvl(jrBsRow.getFieldString("YD_STK_BED_H_MAX"  ),"0"));
					ydMtlShBase    = Integer.parseInt(slabUtils.nvl(jrBsRow.getFieldString("YD_MTL_SH_BASE"    ),"0"));
					ydMtlTBase     = Float.parseFloat(slabUtils.nvl(jrBsRow.getFieldString("YD_MTL_T_BASE"     ),"0"));
					
					//Lot재료 합계
					for (int jj = 0; jj < mtlShSum; jj++) {
						jrLmRow = jsLotMtl.getRecord(jj);
						mtlTSum  += Float.parseFloat(slabUtils.nvl(jrLmRow.getFieldString("YD_MTL_T" ),"0"));
					}
					
					//설비Bed가 아니면 Max단 + 초과매수를 적용한다.
					if ("0".equals(ydUpWoLoc.substring(2, 3))) {
						ydStkBedLyrMax += maxLyrOvSh;
					}

					//Bed사양Check
					if ((ydMtlShBase + mtlShSum) > ydStkBedLyrMax) {
						bedSpecOvGp = "SH";
					} else if ((ydMtlTBase + mtlTSum) > ydStkBedHMax) {
						bedSpecOvGp = "T";
					}
					
					return bedSpecOvGp;
				}
			}
			
			return bedSpecOvGp;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 insert 트랜잭션 분리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void insCrnSch(String trtGp, String[][] param, String logId,String methodNm) throws DAOException {
		//String methodNm = "크레인스케줄 insert 트랜잭션 분리[SlabYdSchSeEJB.rcvYDYDJ400] < ";

		try {
			if ("CrnSch".equals(trtGp)) {
				schDao.insYDYDJ400(trtGp, param, logId, methodNm);
			} else if ("CrnMtl".equals(trtGp)) {
				schDao.insYDYDJ400(trtGp, param, logId, methodNm);
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 작업예약재료 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void updCrnSchWB(JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인스케줄 작업예약재료 수정[SlabYdSchSeEJB.rcvYDYDJ410] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			schDao.updYDYDJ400("WmStrLoc", jrParam);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/***************************************************************************
	 * 야드관리(YD) 내부
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 설비인출요구(YDYDJ410 <- YDYDJ201, YDYDJ202)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYDYDJ410(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "설비인출요구[SlabYdSchSeEJB.rcvYDYDJ410] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId      = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId    = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    )); //야드설비ID
			String ydStkBedNo = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO")); //야드적치Bed번호
			String ydSchStGp  = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_ST_GP" )); //야드스케쥴기동구분
			String modifier   = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"   )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			String ydWrkPlanCrn = slabUtils.trim(rcvMsg.getFieldString("YD_WRK_PLAN_CRN")); //작업크레인
			String stlNos	  = slabUtils.trim(rcvMsg.getFieldString("STL_NOS")); //재료번호

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 : [" + ydEqpId + "]");
			} else if ("".equals(ydStkBedNo)) {
				throw new Exception("적치Bed번호(YD_STK_BED_NO) 없음");
			}
			
			//반환 결과 등
			JDTORecord jrRtn = null;
			JDTORecord jrChk = null;
			JDTORecordSet jsChk = null;
			
			String ydAimYdGp   = ""; //야드목표야드구분
			String ydAimBayGp  = ""; //야드목표동구분
			String ydHomeBayGp = ""; //야드Home동구분
			//대차 Pickup Bed일 경우 대차 설비ID조회
			String tcEqpId = slabComm.getPickupBedTcar(ydEqpId, ydStkBedNo, logId, methodNm);

			//조회 및 등록 용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_YD_EQP_ID"    , tcEqpId   ); //대차설비ID
			jrParam.setField("V_YD_STK_COL_GP", ydEqpId   ); //야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO", ydStkBedNo); //야드적치Bed번호
			jrParam.setField("V_YD_SCH_ST_GP" , ydSchStGp ); //야드스케쥴기동구분
			jrParam.setField("V_MODIFIER"     , modifier  ); //수정자

			//작업예약 기 등록여부 등 Check
			jsChk = schDao.getYDYDJ410("WB", jrParam);

			if (jsChk != null && jsChk.size() > 0) {
				jrChk = jsChk.getRecord(0);
				
				if ("Y".equals(slabUtils.trim(jrChk.getFieldString("WB_STL_YN")))) {
					//throw new Exception("이미 작업예약에 등록된 재료가 존재합니다.");
					slabUtils.printLog(logId, "이미 작업예약에 등록된 재료가 존재합니다.", "SL");
					return jrRtn;
				}

				ydAimYdGp   = slabUtils.trim(jrChk.getFieldString("YD_AIM_YD_GP"  )); //야드목표야드구분
				ydAimBayGp  = slabUtils.trim(jrChk.getFieldString("YD_AIM_BAY_GP" )); //야드목표동구분
				ydHomeBayGp = slabUtils.trim(jrChk.getFieldString("YD_HOME_BAY_GP")); //야드Home동구분
				
				if ("".equals(ydAimBayGp)) {
					//throw new Exception("재료번호[" + slabUtils.trim(jrChk.getFieldString("STL_NO")) + "]의 야드목표동구분이 없습니다.");
					slabUtils.printLog(logId, "재료번호[" + slabUtils.trim(jrChk.getFieldString("STL_NO")) + "]의 야드목표동구분이 없습니다.", "SL");
					return jrRtn;
				}
			} else {
				//throw new Exception("적치Bed[" + ydEqpId + "-" + ydStkBedNo + "] 재료정보가 없습니다.");
				slabUtils.printLog(logId, "적치Bed[" + ydEqpId + "-" + ydStkBedNo + "] 재료정보가 없습니다.", "SL");
				return jrRtn;
			}
			
			//대차 Pickup Bed Carry-Out요구 일지라도 목표동이 홈동과 같으면 일반 Pickup Bed와 같이 처리
			if (ydAimBayGp.equals(ydHomeBayGp)) {
				tcEqpId = "";
			}
			
			if ("".equals(tcEqpId)) {
				/**********************************************************
				* 2. 대차 Pickup Bed 인출요구인 아닌 경우
				*  - 작업예약등록, 크레인스케줄 전문 전송
				**********************************************************/
				//스케쥴코드 조회 및 스케줄금지여부 등 Check
				jrParam.setField("V_YD_SCH_WHIO_GP", "L"); //야드스케쥴입출고구분(입고)

				JDTORecord jrSchCd = slabComm.getSchCd(jrParam);
				
				String ydSchCd    = jrSchCd.getFieldString("YD_SCH_CD"   ); //야드스케쥴코드
				String ydSchPrior = jrSchCd.getFieldString("YD_SCH_PRIOR"); //야드스케쥴우선순위
				
				//--2023.09.05 수정 스케줄금지처리는 exception 제외.
				String ydSchProhExn = jrSchCd.getFieldString("YD_SCH_PROH_EXN");  //야드스케쥴금지유무
				
				if("Y".equals(ydSchProhExn)){
					slabUtils.printLog(logId, "스케쥴코드[" + ydSchCd + "] 기동금지", "SL");
					return jrRtn;
				}
				
				/**********************************************************
				* 2.1 작업예약 등록
				**********************************************************/
				//작업예약ID 생성
				String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
	
				if ("".equals(ydWbookId)) {
					throw new Exception("작업예약ID 생성 실패");
					
				}
	
				//작업예약 등록
				jrParam.setField("V_YD_WBOOK_ID"       , ydWbookId             ); //야드작업예약ID
				jrParam.setField("V_YD_GP"             , ydEqpId.substring(0,1)); //야드구분
				jrParam.setField("V_YD_BAY_GP"         , ydEqpId.substring(1,2)); //야드동구분
				jrParam.setField("V_YD_SCH_CD"         , ydSchCd               ); //야드스케쥴코드
				jrParam.setField("V_YD_SCH_PRIOR"      , ydSchPrior            ); //야드스케쥴우선순위
				jrParam.setField("V_YD_SCH_PROG_STAT"  , "W"                   ); //야드스케쥴진행상태(스케줄수행대기)
				jrParam.setField("V_YD_SCH_REQ_GP"     , "L"                   ); //야드스케쥴요청구분(인출)
				jrParam.setField("V_YD_AIM_YD_GP"      , ydAimYdGp             ); //야드목표야드구분
				jrParam.setField("V_YD_AIM_BAY_GP"     , ydAimBayGp            ); //야드목표동구분
				jrParam.setField("V_YD_TO_LOC_DCSN_MTD", "S"                   ); //야드TO위치결정방법(스케줄기준적용)
				jrParam.setField("V_YD_WRK_PLAN_CRN"   , ydWrkPlanCrn          ); //작업크레인
				
				commDao.insSlabYd("WrkBook", jrParam);
	
				//작업예약재료 등록
				if("|".equals(stlNos) || "".equals(stlNos)) {
					schDao.insYDYDJ410("WbMtl", jrParam);
				} else {
					//선택한 슬라브가 존재하는 경우
					jrParam.setField("V_STL_NOS"       , stlNos             ); //야드작업예약재료
					
					schDao.insYDYDJ410("WbMtl2", jrParam);
				}
	
				/**********************************************************
				* 2.2 크레인스케줄(YDYDJ400) 전문 조회
				**********************************************************/
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	
				jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
				jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
				jrYdMsg.setField("YD_SCH_ST_GP" , ydSchStGp); //야드스케쥴기동구분
				jrYdMsg.setField("YD_SCH_REQ_GP", "L"      ); //야드스케쥴요청구분(인출)
				jrYdMsg.setField("V_MODIFIER"   , modifier ); //수정자
	
				jrRtn = slabUtils.addSndData(slabComm.getCrnSchMsg(jrYdMsg));
			} else {
				/**********************************************************
				* 3. 대차 Pickup Bed 인출요구인 경우 (ACPUP7, ADPUP8)
				*  - 대차하차 작업예약 생성
				*  - 대차스케줄 수정
				*  - 대차작업실적, 대차출발지시 전문 전송
				**********************************************************/
				//대차스케쥴ID 조회(기존 대차스케쥴이 없으면 신규로 생성)
				jsChk = schDao.getYDYDJ410("TcarSch", jrParam);
				
				if (jsChk == null || jsChk.size() <= 0) {
					throw new Exception("대차[" + tcEqpId + "] 스케쥴 정보 조회 실패");
				}

				String ydTcarSchId = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_TCAR_SCH_ID")); //야드대차스케쥴ID
				
				/**********************************************************
				* 3.1 대차하차 작업예약 생성
				**********************************************************/
				//대차하차 작업예약ID 생성
				String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");

				if ("".equals(ydWbookId)) {
					throw new Exception("대차하차 작업예약ID 생성 실패");
				}

				String ydCarldStopLoc = "A" + ydEqpId.substring(1,2) + tcEqpId.substring(2, 6);	//야드상차정지위치
				String ydCarudStopLoc = "A" + ydAimBayGp             + tcEqpId.substring(2, 6);	//야드하차정지위치
				
				jrParam.setField("V_YD_WBOOK_ID"      , ydWbookId            ); //야드대차스케쥴ID
				jrParam.setField("V_YD_SCH_CD"        , ydCarudStopLoc + "LM"); //야드스케쥴코드(하차)
				jrParam.setField("V_YD_BAY_GP"        , ydAimBayGp           ); //야드동구분
				jrParam.setField("V_YD_AIM_BAY_GP"    , ydAimBayGp           ); //야드목표동구분
				jrParam.setField("V_YD_TCAR_SCH_ID"   , ydTcarSchId          ); //야드대차스케쥴ID
				jrParam.setField("V_YD_CARLD_STOP_LOC", ydCarldStopLoc       ); //야드상차정지위치
				jrParam.setField("V_YD_CARUD_STOP_LOC", ydCarudStopLoc       ); //야드하차정지위치
				
				//작업예약 등록
				schDao.insYDYDJ410("WbTcar", jrParam);

				//작업예약재료 등록
				schDao.insYDYDJ410("WbMtlTcar", jrParam);
				
				/**********************************************************
				* 3.2 대차스케줄 등록
				**********************************************************/
				//대차이송재료 등록
				schDao.insYDYDJ410("TcarMtl", jrParam);

				//대차스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등록
				schDao.insYDYDJ410("TcarSch", jrParam);

				/**********************************************************
				* 3.3 C연주정정L2 Carry-Out완료 전송
				**********************************************************/
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDC7L003Tcar", jrParam));

				/**********************************************************
				* 3.4 Bed용도구분설정
				*   - Pickup Bed 재료를 대차 Bed로 이동
				*   - Pickup Bed Close(C), 대차Bed 적치가능(L)
				*   - 수불구변경응답 전송
				**********************************************************/
				//Bed용도구분설정
				jrParam.setField("V_YD_STK_BED_USG_GP", "C"); //야드적치Bed용도구분(Close)
				slabComm.setBedUsgGp(jrParam);

				//수불구변경응답 전송
				jrParam.setField("V_JMS_TC_CD", "YDC7L001"); //JMSTC코드
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDC7L001", jrParam));
				
				/**********************************************************
				* 3.5 대차작업실적 및 영대차출발지시 전송
				**********************************************************/
				//대차작업실적 전송
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDC7L007", jrParam));

				//영대차출발지시 전송
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDC7L006", jrParam));
			}
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 설비보급요구(YDYDJ420 <- YDYDJ231, YDYDJ232, YDYDJ233, YDYDJ237, YDYDJ241, YDYDJ242, YDYDJ243, YDYDJ245)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYDYDJ420(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "설비보급요구[SlabYdSchSeEJB.rcvYDYDJ420] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId      = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId    = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    )); //야드설비ID
			String ydStkBedNo = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO")); //야드적치Bed번호
			String ydSchStGp  = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_ST_GP" )); //야드스케쥴기동구분
			String prStrList  = slabUtils.trim(rcvMsg.getFieldString("STR_LIST" ));     //후판선택대상리스트
			String prLocList  = slabUtils.trim(rcvMsg.getFieldString("LOC_LIST" ));     //후판선택위치리스트
			String modifier   = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"   )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			String ydWrkPlanCrn = slabUtils.trim(rcvMsg.getFieldString("YD_WRK_PLAN_CRN")); //작업크레인

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() != 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 : [" + ydEqpId + "]");
			} else if ("".equals(ydStkBedNo)) {
				throw new Exception("적치Bed번호(YD_STK_BED_NO) 없음");
			}

			String ydToLocGuide   = ydEqpId + ydStkBedNo;	//야드To위치Guide
			String ydSchCd        = "";	//야드스케쥴코드
			String ydAimRtGp      = "";	//야드목표행선구분
			String ydSchPrior     = "";	//야드스케쥴우선순위
			String ydEqpName      = ""; //야드설비명(보급Bed명)
			String ydStkBedLyrMax = "";	//야드적치Bed단Max
			String ydStkBedWtMax  = "";	//야드적치Bed중량Max
			String ydStkBedHMax   = "";	//야드적치Bed높이Max
			String ciAutoGpYn     = "";	//보급요구대상재자동편성여부
			String stkMtlSh       = "";	//적치재료매수
			String crnEqpId       = "";	//야드설비ID(작업크레인)

			JDTORecord jrRow = null;
			JDTORecord jrRtn = null;

			//내부처리를 위한 Message 생성 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, modifier);
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, modifier);
			jrParam.setField("V_YD_STK_COL_GP"  , ydEqpId     ); //야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO"  , ydStkBedNo  ); //야드적치Bed번호
			jrParam.setField("V_YD_SCH_ST_GP"   , ydSchStGp   ); //야드스케쥴기동구분
			jrParam.setField("V_YD_TO_LOC_GUIDE", ydToLocGuide); //야드To위치Guide

			/**********************************************************
			* 2. 기준정보 Check
			**********************************************************/
			JDTORecordSet jsChk = schDao.getYDYDJ420("Rule", jrParam);

			if (jsChk != null && jsChk.size() > 0) {
				JDTORecord jrChk = jsChk.getRecord(0);

				String ydStkBedUsgGp = slabUtils.trim(jrChk.getFieldString("YD_STK_BED_USG_GP")); //야드적치Bed용도구분
				String ydCoilOutdiaGrpGp = slabUtils.trim(jrChk.getFieldString("YD_COIL_OUTDIA_GRP_GP")); //픽업크레인작업유무

				if (!"B".equals(ydStkBedUsgGp)) {
					throw new Exception("보급Bed[" + ydToLocGuide + "] 용도구분이 불출구[B:" + ydStkBedUsgGp + "]가 아닙니다.");
				}
				
				if("Y".equals(ydCoilOutdiaGrpGp)) {
					throw new Exception("보급Bed[" + ydToLocGuide + "] 픽업 크레인이 현재 작업중입니다.");
				}

				ydSchCd        = slabUtils.trim(jrChk.getFieldString("YD_SCH_CD"         ));	//야드스케쥴코드
				ydEqpName      = slabUtils.trim(jrChk.getFieldString("YD_EQP_NAME"       ));	//야드설비명(보급Bed명)
				ydAimRtGp      = slabUtils.trim(jrChk.getFieldString("YD_AIM_RT_GP"      ));	//야드목표행선구분
				ydStkBedLyrMax = slabUtils.trim(jrChk.getFieldString("YD_STK_BED_LYR_MAX"));	//야드적치Bed단Max
				ydStkBedWtMax  = slabUtils.trim(jrChk.getFieldString("YD_STK_BED_WT_MAX" ));	//야드적치Bed중량Max
				ydStkBedHMax   = slabUtils.trim(jrChk.getFieldString("YD_STK_BED_H_MAX"  ));	//야드적치Bed높이Max
				ciAutoGpYn     = slabUtils.trim(jrChk.getFieldString("CI_AUTO_GP_YN"     ));	//보급요구대상재자동편성여부
				stkMtlSh       = slabUtils.trim(jrChk.getFieldString("STK_MTL_SH"        ));	//적치재료매수
			} else {
				throw new Exception("적치Bed[" + ydToLocGuide + "] 보급설비기준정보가 없습니다.");
			}
			
			/**********************************************************
			* 3. 스케쥴코드 조회 및 Check
			**********************************************************/
			//스케줄금지여부 등 Check
			jrParam.setField("V_YD_SCH_CD"     , ydSchCd); //야드스케쥴코드
			jrParam.setField("V_YD_SCH_WHIO_GP", "U"    ); //야드스케쥴입출고구분(출고)

			JDTORecord jrSchCd = slabComm.getSchCd(jrParam);

			ydSchCd    = slabUtils.trim(jrSchCd.getFieldString("YD_SCH_CD"   )); //야드스케쥴코드
			ydSchPrior = slabUtils.trim(jrSchCd.getFieldString("YD_SCH_PRIOR")); //야드스케쥴우선순위
			crnEqpId   = slabUtils.trim(jrSchCd.getFieldString("YD_EQP_ID"   )); //야드설비ID(작업크레인)

			jrParam.setField("V_YD_SCH_CD"   , ydSchCd   ); //야드스케쥴코드
			jrParam.setField("V_YD_SCH_PRIOR", ydSchPrior); //야드스케쥴우선순위
			jrParam.setField("V_YD_EQP_ID"   , crnEqpId  ); //야드설비ID(작업크레인)
			
			/**********************************************************
			* 4. C열연 장입보급(혹은 항만 스카핑보급)Bed가 공Bed 이면 크레인스케줄 존재여부 Check
			*  - 선보급요구로 생성된 크레인스케줄 : 스케쥴코드가 같고 권하지시위치가 없음
			*  - 크레인스케줄 권하위치 변경, 적치단 권하대기 등록
			*  - 작업지시 재전송
			**********************************************************/
			if (("AAPUP4".equals(ydEqpId) || "ABPUP6".equals(ydEqpId) || "MADP01".equals(ydEqpId))    // 항만야드 기능적용 보완 : 2015.12.15 by LeeJY
					&& "0".equals(stkMtlSh)) {
				jsChk = schDao.getYDYDJ420("CrnSch", jrParam);
	
				if (jsChk != null && jsChk.size() > 0) {
					//크레인작업관리화면 권하위치변경 호출
					String ydCrnSchId = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_CRN_SCH_ID"));	//크레인스케줄ID
					
					slabUtils.printLog(logId, ydEqpName + " 권하위치변경 호출 : C열연 장입보급 크레인스케줄ID[" + ydCrnSchId + "]", "SL");

					jrYdMsg.setField("YD_EQP_ID"       , slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"       ))); //야드설비ID(크레인)
					jrYdMsg.setField("YD_SCH_CD"       , ydSchCd                                                              ); //야드스케쥴코드
					jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId                                                           ); //야드크레인스케쥴ID
					jrYdMsg.setField("YD_WBOOK_ID"     , slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_WBOOK_ID"     ))); //야드작업예약ID
					jrYdMsg.setField("YD_DN_WO_LOC"    , ydToLocGuide                                                         ); //야드권하지시위치(신규)
					jrYdMsg.setField("YD_WRK_PROG_STAT", slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_WRK_PROG_STAT"))); //야드작업진행상태
					
					EJBConnector sndConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
					jrRtn = (JDTORecord)sndConn.trx("updCrnSchDnWoLoc", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
					
					slabUtils.printLog(logId, methodNm, "S-");
	
					return jrRtn;
				}
			}

			/**********************************************************
			* 5. 같은 스케쥴코드의 작업예약 존재여부 Check
			*  - 크레인스케줄 미편성 작업예약이 존재하면 최우선 작업예약을 선택
			*  - 선보급요구가 'Y'이면 선보급요구 실행
			**********************************************************/
			jsChk = schDao.getYDYDJ420("WrkBook", jrParam);

			if (jsChk != null && jsChk.size() > 0) {
				String ydWbookId  = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_WBOOK_ID"    ));	//야드작업예약ID
				String toLocGuide = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_TO_LOC_GUIDE"));	//야드To위치Guide(우선 작업예약)

				if (!ydToLocGuide.equals(toLocGuide)) {
					//작업예약 To위치Guide를 보급요구 To위치Guide로 변경
					jrParam.setField("V_YD_WBOOK_ID"    , ydWbookId   );	//야드스케쥴코드
					jrParam.setField("V_YD_TO_LOC_GUIDE", ydToLocGuide);	//야드To위치Guide

					schDao.updYDYDJ420("WbToLoc", jrParam);
					
					slabUtils.printLog(logId, "스케쥴코드[" + ydSchCd + "]로 기 등록된 작업예약ID[" + ydWbookId + "]의 To위치를 [" + toLocGuide + " → " + ydToLocGuide + "]로 변경한 후 크레인스케줄 호출", "SL");
				} else {
					slabUtils.printLog(logId, "스케쥴코드[" + ydSchCd + "]로 기 등록된 작업예약ID[" + ydWbookId + "]로 크레인스케줄 호출", "SL");
				}

				//크레인스케줄(YDYDJ400) 전문 조회
				jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId);	//야드작업예약ID
				jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  );	//야드스케쥴코드
				jrYdMsg.setField("YD_EQP_ID"    , crnEqpId );	//야드설비ID(크레인)
				jrYdMsg.setField("YD_SCH_ST_GP" , ydSchStGp);	//야드스케쥴기동구분
				jrYdMsg.setField("YD_SCH_REQ_GP", "U"      );	//야드스케쥴요청구분(보급)

				jrRtn = slabUtils.addSndData(slabComm.getCrnSchMsg(jrYdMsg));
				
				slabUtils.printLog(logId, methodNm, "S-");

				return jrRtn;
			}

			/**********************************************************
			* 6. 보급 대상재 추출하여 신규 작업예약 등록  (후판 장입제외)
			*  - 준비스케줄 보급Lot가 있으면 보급Lot재료를 작업예약으로 등록하고 준비스케줄 삭제
			*  - 준비스케줄 보급Lot가 없으면
			*    . 스카핑/2차절단 : 종료
			*    . 장입 : 장입지시 재료를 조회하여 작업예약 등록
			**********************************************************/
			logger.println(LogLevel.INFO, "%%%%%%%%%%> 보급 대상재 추출하여 신규 작업예약 등록 - 야드구분( "+ydEqpId.substring(0,1)+" )");
			slabUtils.printLog(logId,"%%%%%%%%%%> 보급 대상재 추출하여 신규 작업예약 등록222 - 야드구분( "+ydEqpId.substring(0,1)+" )", "SL");
			if (ydEqpId.startsWith("A") || ydEqpId.startsWith("M")) {  //항만야드 기능적용 보완 : 2015.12.15 by LeeJY
				String trtGp = "";	//처리구분

				logger.println(LogLevel.INFO, "------> 보급 대상재 추출하여 신규 작업예약 등록 - 목표행선( "+ydAimRtGp+" )");
				if ("C2".equals(ydAimRtGp)) {
					trtGp = "PrepSchHC";	//C열연 가열로 보급
				} else {
					trtGp = "PrepSchSS";	//C연주 #1,2 Scarfer, #1,2,3 2차절단 보급
					logger.println(LogLevel.INFO, " ----> Scarfer보급 대상재 추출");
				}

				//준비스케줄 보급 대상재 조회
				JDTORecordSet jsWbMtl = schDao.getYDYDJ420(trtGp, jrParam);

				if (jsWbMtl != null && jsWbMtl.size() > 0) {
					//준비스케줄에 보급Lot가 등록되어 있으면
					String ydPrepSchId = slabUtils.trim(jsWbMtl.getRecord(0).getFieldString("YD_PREP_SCH_ID")); //야드준비스케쥴ID

					if ("Y".equals(jsWbMtl.getRecord(0).getFieldString("WB_STL_YN"))) {
						throw new Exception("준비스케쥴ID[" + ydPrepSchId + "]의 재료가 이미 작업예약재료에 등록되어 있습니다.");
					}

					slabUtils.printLog(logId, ydEqpName + " 작업예약 등록 : 보급Lot 준비스케쥴ID[" + ydPrepSchId + "]", "SL");
					
					ydWrkPlanCrn = slabUtils.trim(jsWbMtl.getRecord(0).getFieldString("YD_WRK_PLAN_CRN"));
					//작업예약 등록
					jrParam.setField("V_WB_MTL_SH", String.valueOf(jsWbMtl.size())); //작업예약재료매수
					jrParam.setField("V_YD_WRK_PLAN_CRN", ydWrkPlanCrn);			 //작업크레인
					
					jrRtn = this.insWrkBook(jrParam, jsWbMtl, ydPrepSchId);
					
					slabUtils.printLog(logId, methodNm, "S-");

					return jrRtn;
				} else if ("CS".equals(trtGp)) {
					//보급Lot Only 일 경우 (C연주 #1,2 Scarfer, #1,2,3 2차절단 보급 등)
					slabUtils.printLog(logId, ydEqpName + " 작업예약 등록 : 보급Lot 준비스케줄 없음", "SL");
					slabUtils.printLog(logId, methodNm, "S-");
					return null;
				} 
			}
			
			/**********************************************************
			* 6.5  후판장입대상재중 선택된 재료정보가 있을 경우에 체크
			* 2015.02.09 윤재광 추가(후판슬라브 요청사항)
			**********************************************************/
			if(!"".equals(prStrList)){
				
				JDTORecordSet jsWbMtl 	= JDTORecordFactory.getInstance().createRecordSet("");
    			JDTORecord recInTemp	= null;
				
    			String[] prSList = prStrList.split("-");
    			String[] prLList = prLocList.split("-");
    			
    			for(int inx = 0; inx < prSList.length; inx++){
    				
    				if(!"".equals(prSList[inx])){
    					recInTemp	= JDTORecordFactory.getInstance().create();
    	    			recInTemp.setField("STL_NO", prSList[inx]);//재료번호
    	    			recInTemp.setField("YD_STK_COL_GP", prLList[inx].substring(0, 6));//야드적치열구분
    	    			recInTemp.setField("YD_STK_BED_NO", prLList[inx].substring(6, 8));//야드적치Bed번호
    	    			recInTemp.setField("YD_STK_LYR_NO", prLList[inx].substring(8));   //야드적치단번호
    	    			recInTemp.setField("YD_UP_COLL_SEQ", (inx+1)+"");				  //야드권상모음순서
    	    			jsWbMtl.addRecord(recInTemp);
    				}
    			}
    			
    			if (jsWbMtl != null && jsWbMtl.size() > 0) {
					
					//작업예약 등록
					jrParam.setField("V_WB_MTL_SH", String.valueOf(jsWbMtl.size())); //작업예약재료매수
					jrParam.setField("V_YD_WRK_PLAN_CRN", ydWrkPlanCrn);			 //작업크레인
					
					jrRtn = this.insWrkBook(jrParam, jsWbMtl, "");
					
					slabUtils.printLog(logId, ydEqpName + " 작업예약 등록 : 선택된 후판장입대상재 등록", "SL");
					slabUtils.printLog(logId, methodNm, "S-");

					return jrRtn;
				}
			}
			
			/**********************************************************
			* 7. 장입보급 자동편성여부가 'Y'이면 대상재 추출하여 신규 작업예약 등록
			*  - C열연장입은 대상재 추출 업무기준 사용
			*  - 후판장입은 대상재 추출 업무기준 없음
			**********************************************************/
			if ("Y".equals(ciAutoGpYn)) {
				//장입 보급 대상재 조회
				JDTORecordSet jsWbMtl = null;
				
				if ("C2".equals(ydAimRtGp)) {
					//C열연 가열로 보급 대상재 조회
					jsWbMtl = schDao.getYDYDJ420("HR", jrParam);

					if (jsWbMtl != null && jsWbMtl.size() > 0) {
						//스케줄코드 좌우측 방향과 대상재료의 스케줄코드가 다르면
						String ydSchCdNew = slabUtils.trim(jsWbMtl.getRecord(0).getFieldString("YD_SCH_CD_SEQ"));
		
						if ("2".equals(ydSchCdNew)) {
							if ("L".equals(ydSchCd.substring(7, 8))) {
								ydSchCdNew = ydSchCd.substring(0, 7) + "R";
							} else {
								ydSchCdNew = ydSchCd.substring(0, 7) + "L";
							}

							slabUtils.printLog(logId, ydEqpName + " 작업예약 등록 : 자동편성 스케쥴코드 변경 [" + ydSchCd + " → " + ydSchCdNew + "]", "SL");

							ydSchCd = ydSchCdNew;
		
							//스케쥴코드가 변경되었으므로 다시 Check
							jrParam.setField("V_YD_SCH_CD", ydSchCd); //야드스케쥴코드
							jrSchCd = slabComm.getSchCd(jrParam);
		
							ydSchPrior = slabUtils.trim(jrSchCd.getFieldString("YD_SCH_PRIOR")); //야드스케쥴우선순위
							crnEqpId   = slabUtils.trim(jrSchCd.getFieldString("YD_EQP_ID"   )); //야드설비ID(작업크레인)
		
							jrParam.setField("V_YD_SCH_PRIOR", ydSchPrior); //야드스케쥴우선순위
							jrParam.setField("V_YD_EQP_ID"   , crnEqpId  ); //야드설비ID(작업크레인)
						}
					}
				} else {
					//1,2후판 가열로 보급 대상재 조회
					jsWbMtl = schDao.getYDYDJ420("PR", jrParam);
				}
				
				if (jsWbMtl == null || jsWbMtl.size() <= 0) {
					slabUtils.printLog(logId, ydEqpName + " 작업예약 등록 : 자동편성 대상재 없음", "SL");
					slabUtils.printLog(logId, methodNm, "S-");
					return null;
				} 

				slabUtils.printLog(logId, ydEqpName + " 작업예약 등록 : 자동편성 대상재 추출 " + jsWbMtl.size() + "매", "SL");
	
				//보급재료매수 조회 : 크레인스케줄 편성 시 크레인이 한 번에 들어서 Bed에 적치할 수 있도록 작업예약을 편성
				JDTORecord jrSupMtl = slabUtils.getParam(logId, methodNm, modifier);
				jrSupMtl.setField("SUP_MTL"           , jsWbMtl       ); //보급재료(대상재)
				jrSupMtl.setField("YD_STK_COL_GP"     , ydEqpId       ); //야드적치열구분
				jrSupMtl.setField("YD_EQP_ID"         , crnEqpId      ); //야드설비ID(작업크레인)
				jrSupMtl.setField("YD_STK_BED_LYR_MAX", ydStkBedLyrMax); //야드적치Bed단Max
				jrSupMtl.setField("YD_STK_BED_WT_MAX" , ydStkBedWtMax ); //야드적치Bed단Max
				jrSupMtl.setField("YD_STK_BED_H_MAX"  , ydStkBedHMax  ); //야드적치Bed높이Max
				
				int wbMtlSh = this.getSupMtlSh(jrSupMtl); //작업예약재료매수
    //////////////////////////////////////////
				 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
	//////////////////////////////////////////
		
		     if("Y".equals(APPLY_YN34)){
	
				if("DBPU05".equals(jrSupMtl.getField("YD_STK_COL_GP"))) {
					
					int totalWeight = 0;
					
					for(int i = 0; i<wbMtlSh; i++) {
						totalWeight += jsWbMtl.getRecord(i).getFieldInt("YD_MTL_WT");
					}
					
					if(totalWeight >= 61000) {
						slabUtils.printLog(logId, ydEqpName + " 작업예약 등록 실패: 중량 초과", "SL");
						slabUtils.printLog(logId, methodNm, "S-");
						//return null;
						throw new Exception("Carry-in 작업예약 등록 실패: 중량 초과하였습니다.");
					}
				}
		     
				//1후판 장입보급스케줄일 경우 장입준비작업으로 크레인스케줄이 등록된 재료가 있으면
				//기 등록되어 있는 크레인스케줄의 권하지시위치를 변경
				if ("DAPU01UM".equals(ydSchCd) || "DAPU03UM".equals(ydSchCd)) {
					String ydCrnSchId = "";	//크레인스케줄ID
	
					for (int ii = 0; ii < wbMtlSh; ii++) {
						jrRow = jsWbMtl.getRecord(ii);
						ydCrnSchId = slabUtils.trim(jrRow.getFieldString("YD_CRN_SCH_ID"));	//크레인스케줄ID
	
						if (!"".equals(ydCrnSchId)) {
							//크레인작업관리화면 권하위치변경 호출
							slabUtils.printLog(logId, ydEqpName + " 권하위치변경 호출 : 1후판 장입보급 스케줄ID[" + ydCrnSchId + "]", "SL");

							jrYdMsg.setField("YD_EQP_ID"       , slabUtils.trim(jrRow.getFieldString("YD_EQP_ID"       ))); //야드설비ID(크레인)
							jrYdMsg.setField("YD_SCH_CD"       , slabUtils.trim(jrRow.getFieldString("YD_SCH_CD"       ))); //야드스케쥴코드
							jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId                                              ); //야드크레인스케쥴ID
							jrYdMsg.setField("YD_WBOOK_ID"     , slabUtils.trim(jrRow.getFieldString("YD_WBOOK_ID"     ))); //야드작업예약ID
							jrYdMsg.setField("YD_DN_WO_LOC"    , ydToLocGuide                                            ); //야드권하지시위치(신규)
							jrYdMsg.setField("YD_WRK_PROG_STAT", slabUtils.trim(jrRow.getFieldString("YD_WRK_PROG_STAT"))); //야드작업진행상태
		
							EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
							jrRtn = (JDTORecord)ejbConn.trx("updCrnSchDnWoLoc", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			                
							slabUtils.printLog(logId, methodNm, "S-");
	
							return jrRtn;
						}
					}
				}
		     }
				slabUtils.printLog(logId, ydEqpName + " 작업예약 등록 : 크레인사양 Check후 자동편성 대상재 " + wbMtlSh + "매", "SL");

				//작업예약 등록
				jrParam.setField("V_WB_MTL_SH", String.valueOf(wbMtlSh)); //작업예약재료매수
				jrParam.setField("V_YD_WRK_PLAN_CRN", ydWrkPlanCrn);			 //작업크레인
				jrRtn = this.insWrkBook(jrParam, jsWbMtl, "");
			} else {
				slabUtils.printLog(logId, ydEqpName + " 작업예약 등록 : 대상재 자동편성 안함", "SL");
			}
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 작업예약등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @param JDTORecordSet jsWbMtl
	 *      @param String ydPrepSchId
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord insWrkBook(JDTORecord rcvMsg, JDTORecordSet jsWbMtl, String ydPrepSchId) throws DAOException {
		String methodNm = "작업예약등록[SlabYdSchSeEJB.작업예약등록] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String ydSchCd      = slabUtils.trim(rcvMsg.getFieldString("V_YD_SCH_CD"      )); //야드스케쥴코드
			String ydSchPrior   = slabUtils.trim(rcvMsg.getFieldString("V_YD_SCH_PRIOR"   )); //야드적치Bed번호
			String ydSchStGp    = slabUtils.trim(rcvMsg.getFieldString("V_YD_SCH_ST_GP"   )); //야드스케쥴기동구분
			String ydToLocGuide = slabUtils.trim(rcvMsg.getFieldString("V_YD_TO_LOC_GUIDE")); //야드To위치Guide
			String ydEqpId      = slabUtils.trim(rcvMsg.getFieldString("V_YD_EQP_ID"      )); //야드설비ID(크레인)
			String modifier     = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"       )); //수정자
			int wbMtlSh         = Integer.parseInt(slabUtils.nvl(rcvMsg.getFieldString("V_WB_MTL_SH"),"0")); //작업예약재료매수
			String ydWrkPlanCrn = slabUtils.trim(rcvMsg.getFieldString("V_YD_WRK_PLAN_CRN")); //작업크레인
			
			String ydGp         = ydSchCd.substring(0, 1);	//야드구분
			String ydBayGp      = ydSchCd.substring(1, 2);	//야드동구분

			//내부처리를 위한 Message 생성 - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, modifier);
			JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, modifier);

			//작업예약ID 생성
			String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");

			if ("".equals(ydWbookId)) {
				throw new Exception("작업예약ID 생성 실패");
			}

			//작업예약 등록
			jrParam.setField("V_YD_WBOOK_ID"       , ydWbookId   ); //야드작업예약ID
			jrParam.setField("V_YD_GP"             , ydGp        ); //야드구분
			jrParam.setField("V_YD_BAY_GP"         , ydBayGp     ); //야드동구분
			jrParam.setField("V_YD_SCH_CD"         , ydSchCd     ); //야드스케쥴코드
			jrParam.setField("V_YD_SCH_PRIOR"      , ydSchPrior  ); //야드스케쥴우선순위
			jrParam.setField("V_YD_SCH_PROG_STAT"  , "W"         ); //야드스케쥴진행상태(스케줄수행대기)
			jrParam.setField("V_YD_SCH_ST_GP"      , ydSchStGp   ); //야드스케쥴기동구분
			jrParam.setField("V_YD_SCH_REQ_GP"     , "U"         ); //야드스케쥴요청구분(보급)
			jrParam.setField("V_YD_AIM_YD_GP"      , ydGp        ); //야드목표야드구분
			jrParam.setField("V_YD_AIM_BAY_GP"     , ydBayGp     ); //야드목표동구분
			jrParam.setField("V_YD_TO_LOC_DCSN_MTD", "F"         ); //야드TO위치결정방법(작업자지정)
			jrParam.setField("V_YD_TO_LOC_GUIDE"   , ydToLocGuide); //야드To위치Guide
			jrParam.setField("V_YD_WRK_PLAN_CRN"   , ydWrkPlanCrn); //작업크레인
			
			commDao.insSlabYd("WrkBook", jrParam);

			//작업예약재료 등록
			if (wbMtlSh < 1) {
				wbMtlSh = jsWbMtl.size(); //작업예약재료매수
			}
			
			String[][] wmParam = new String[wbMtlSh][8];
			JDTORecord jrRow = null;
			
			for (int ii = 0; ii < wbMtlSh; ii++) {
				jrRow = jsWbMtl.getRecord(ii);
				
				wmParam[ii][0] = ydWbookId;													//야드작업예약ID
				wmParam[ii][1] = slabUtils.trim(jrRow.getFieldString("STL_NO"        ));	//재료번호
				wmParam[ii][2] = modifier;													//등록자
				wmParam[ii][3] = modifier;													//수정자
				wmParam[ii][4] = slabUtils.trim(jrRow.getFieldString("YD_STK_COL_GP" ));	//야드적치열구분
				wmParam[ii][5] = slabUtils.trim(jrRow.getFieldString("YD_STK_BED_NO" ));	//야드적치Bed번호
				wmParam[ii][6] = slabUtils.trim(jrRow.getFieldString("YD_STK_LYR_NO" ));	//야드적치단번호
				wmParam[ii][7] = slabUtils.trim(jrRow.getFieldString("YD_UP_COLL_SEQ"));	//야드권상모음순서
			}
			
			commDao.upsBatch("WrkBookMtl", wmParam, logId, methodNm);
			
			//준비스케줄 삭제
			if (!"".equals(ydPrepSchId)) {
				jrParam.setField("V_YD_PREP_SCH_ID", ydPrepSchId); //야드준비스케쥴ID
				//준비재료 삭제
				schDao.updYDYDJ420("PM", jrParam);
				//준비스케줄 삭제
				schDao.updYDYDJ420("PP", jrParam);
			}
			
			//크레인스케줄(YDYDJ400) 전문 조회
			jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
			jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
			jrYdMsg.setField("YD_EQP_ID"    , ydEqpId  ); //야드설비ID(크레인)
			jrYdMsg.setField("YD_SCH_ST_GP" , ydSchStGp); //야드스케쥴기동구분
			jrYdMsg.setField("YD_SCH_REQ_GP", "U"      ); //야드스케쥴요청구분(보급)

			JDTORecord jrRtn = slabUtils.addSndData(slabComm.getCrnSchMsg(jrYdMsg));

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 보급재료매수 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public int getSupMtlSh(JDTORecord jrSupMtl) throws DAOException {
		String methodNm = "보급재료매수조회[SlabYdSchSeEJB.getSupMtlSh] < " + jrSupMtl.getResultMsg();
		String logId = jrSupMtl.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			/**********************************************************
			* 1. Bed사양 조회
			**********************************************************/
			int   ydStkBedLyrMax = Integer.parseInt(slabUtils.nvl(jrSupMtl.getFieldString("YD_STK_BED_LYR_MAX"),"0"));	//야드적치Bed단Max
			int   ydStkBedWtMax  = Integer.parseInt(slabUtils.nvl(jrSupMtl.getFieldString("YD_STK_BED_WT_MAX" ),"0"));	//야드적치Bed중량Max
			float ydStkBedHMax   = Float.parseFloat(slabUtils.nvl(jrSupMtl.getFieldString("YD_STK_BED_H_MAX"  ),"0"));	//야드적치Bed높이Max
			
			/**********************************************************
			* 2. 크레인사양 조회
			**********************************************************/
			String ydEqpId = slabUtils.trim(jrSupMtl.getFieldString("YD_EQP_ID")); //야드설비ID

			if (ydEqpId.length() < 6) {
				throw new Exception("크레인 설비ID(YD_EQP_ID) 이상 : [" + ydEqpId + "]");
			}

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("V_YD_EQP_ID", ydEqpId); //야드설비ID(작업크레인)

			JDTORecordSet jsCrnSpec = schDao.getYDYDJ400("CrnSpec", jrParam);

			if (jsCrnSpec.size() <= 0) {
				throw new Exception("크레인[" + ydEqpId + "] 사양정보가 없습니다.");
			}

			JDTORecord jrCrnSpec = jsCrnSpec.getRecord(0);
			
			//크레인사양
			int   ydWrkAbleSh   = Integer.parseInt(slabUtils.nvl(jrCrnSpec.getFieldString("YD_WRK_ABLE_SH"   ),"0"));	//야드작업가능매수
			float ydWrkAbleWt   = Integer.parseInt(slabUtils.nvl(jrCrnSpec.getFieldString("YD_WRK_ABLE_WT"   ),"0"));	//야드작업가능중량
			float ydCrnTongH    = Float.parseFloat(slabUtils.nvl(jrCrnSpec.getFieldString("YD_CRN_TONG_H"    ),"0"));	//야드크레인집게높이
			float ydCrnTongWTol = Float.parseFloat(slabUtils.nvl(jrCrnSpec.getFieldString("YD_CRN_TONG_W_TOL"),"0"));	//야드크레인집게폭허용오차

			//1후판 가열로 장입보급일 경우 크레인사양  야드크레인집게높이를 제한(300mm 이상 Slab 3매는 작업 곤란) -> 추후 크레인사양 값을 변경하자
			String ydStkColGp = slabUtils.trim(jrSupMtl.getFieldString("YD_STK_COL_GP"));	//야드적치열구분
			if("DAPU01".equals(ydStkColGp) || "DAPU03".equals(ydStkColGp)){
				ydStkBedHMax = 850;
			}

			/**********************************************************
			* 3. Bed 및 크레인 사양 Check
			**********************************************************/
			int   supMtlSh  = 0;	//보급재료매수
			int   mtlWt     = 0;	//재료중량
			float mtlT      = 0;	//재료두께
			float mtlW      = 0;	//재료폭
			int   mtlShSum  = 0;	//재료매수합
			int   mtlWtSum  = 0;	//재료중량합
			float mtlTSum   = 0;	//재료두께합
			float mtlWMax   = 0;	//재료폭최대
			String spanSeq  = "";	//Span순서(C열연장입 스케줄코드 왼쪽, 오른쪽 구분을 위한)
			String specOvGp = "";	//사양초과구분(Bed 및 크레인)
			JDTORecord jrRow = null;

			JDTORecordSet jsSupMtl = (JDTORecordSet)jrSupMtl.getField("SUP_MTL"); //보급재료(대상재)
			int rowCnt = jsSupMtl.size();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrRow = jsSupMtl.getRecord(ii);

				mtlWt = Integer.parseInt(slabUtils.nvl(jrRow.getFieldString("YD_MTL_WT"),"0"));
				mtlT  = Float.parseFloat(slabUtils.nvl(jrRow.getFieldString("YD_MTL_T" ),"0"));
				mtlW  = Float.parseFloat(slabUtils.nvl(jrRow.getFieldString("YD_MTL_W" ),"0"));
				
				if (ii == 0) {
					spanSeq = slabUtils.trim(jrRow.getFieldString("SPAN_SEQ")); //Span구분

					mtlShSum = 1;
					mtlWtSum = mtlWt;
					mtlTSum  = mtlT;
					mtlWMax  = mtlW;
				} else {
					if (!"".equals(spanSeq) && !spanSeq.equals(jrRow.getFieldString("SPAN_SEQ"))) {
						break;
					}

					mtlShSum++;
					mtlWtSum += mtlWt;
					mtlTSum  += mtlT;
					
					//Bed 및 크레인 사양 Check
					if (mtlShSum > ydWrkAbleSh || mtlShSum > ydStkBedLyrMax) {
						specOvGp = "SH";	//매수 초과
					} else if (mtlWtSum > ydWrkAbleWt || mtlWtSum > ydStkBedWtMax) {
						specOvGp = "WT";	//중량 초과
					} else if (mtlTSum > ydCrnTongH || mtlTSum > ydStkBedHMax) {
						specOvGp = "T";		//두께 초과
					} else if ((mtlWMax - mtlW) > ydCrnTongWTol) {
						specOvGp = "W";		//폭 허용오차 초과
					}

					//사양 초과
					if (!"".equals(specOvGp)) {
						break;
					}
				}

				if (mtlW > mtlWMax) { mtlWMax = mtlW; }
				supMtlSh++; //작업재료매수
			}
			
			slabUtils.printLog(logId, methodNm, "S-");

			return supMtlSh;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 장입준비작업요구(YDYDJ430 <- YDYDJ294) : 사용안함
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYDYDJ430(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "장입준비작업요구[SlabYdSchSeEJB.rcvYDYDJ430] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId     = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId   = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"   )); //야드설비ID
			String ydSchStGp = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_ST_GP")); //야드스케쥴기동구분
			String modifier  = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"  )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 : [" + ydEqpId + "]");
			}

			/**********************************************************
			* 2. 작업예약 대상재료 조회
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_YD_EQP_ID", ydEqpId); //야드설비ID

			//보급 대상재 조회
			JDTORecordSet jsWbMtl = schDao.getYDYDJ430("WM", jrParam);
			
			if (jsWbMtl.size() <= 0) {
				slabUtils.printLog(logId, "크레인[" + ydEqpId + "] 장입준비 대상재가 없습니다.", "SL");
				slabUtils.printLog(logId, methodNm, "S-");
				return null;
			}

			//작업예약재료매수
			int wbMtlSh = jsWbMtl.size();
			String ydGp       = ydEqpId.substring(0, 1);	//야드구분
			String ydBayGp    = ydEqpId.substring(1, 2);	//야드동구분
			String ydSchCd    = slabUtils.trim(jsWbMtl.getRecord(0).getFieldString("YD_SCH_CD"   ));	//야드스케쥴코드
			String ydSchPrior = slabUtils.trim(jsWbMtl.getRecord(0).getFieldString("YD_SCH_PRIOR"));	//야드스케쥴우선순위

			/**********************************************************
			* 3. 작업예약 등록
			**********************************************************/
			//작업예약ID 생성
			String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");

			if ("".equals(ydWbookId)) {
				throw new Exception("작업예약ID 생성 실패");
			}

			//작업예약 등록
			jrParam.setField("V_YD_WBOOK_ID"       , ydWbookId   ); //야드작업예약ID
			jrParam.setField("V_MODIFIER"          , modifier    ); //수정자
			jrParam.setField("V_YD_GP"             , ydGp        ); //야드구분
			jrParam.setField("V_YD_BAY_GP"         , ydBayGp     ); //야드동구분
			jrParam.setField("V_YD_SCH_CD"         , ydSchCd     ); //야드스케쥴코드
			jrParam.setField("V_YD_SCH_PRIOR"      , ydSchPrior  ); //야드스케쥴우선순위
			jrParam.setField("V_YD_SCH_PROG_STAT"  , "W"         ); //야드스케쥴진행상태(스케줄수행대기)
			jrParam.setField("V_YD_SCH_ST_GP"      , ydSchStGp   ); //야드스케쥴기동구분
			jrParam.setField("V_YD_SCH_REQ_GP"     , "M"         ); //야드스케쥴요청구분(장입준비)
			jrParam.setField("V_YD_AIM_YD_GP"      , ydGp        ); //야드목표야드구분
			jrParam.setField("V_YD_AIM_BAY_GP"     , ydBayGp     ); //야드목표동구분
			jrParam.setField("V_YD_TO_LOC_DCSN_MTD", "S"         ); //야드TO위치결정방법(스케줄지정)

			commDao.insSlabYd("WrkBook", jrParam);

			//작업예약재료 등록
			String[][] wmParam = new String[wbMtlSh][8];
			JDTORecord jrRow = null;
			
			for (int ii = 0; ii < wbMtlSh; ii++) {
				jrRow = jsWbMtl.getRecord(ii);
				
				wmParam[ii][0] = ydWbookId;													//야드작업예약ID
				wmParam[ii][1] = slabUtils.trim(jrRow.getFieldString("STL_NO"        ));	//재료번호
				wmParam[ii][2] = modifier;													//등록자
				wmParam[ii][3] = modifier;													//수정자
				wmParam[ii][4] = slabUtils.trim(jrRow.getFieldString("YD_STK_COL_GP" ));	//야드적치열구분
				wmParam[ii][5] = slabUtils.trim(jrRow.getFieldString("YD_STK_BED_NO" ));	//야드적치Bed번호
				wmParam[ii][6] = slabUtils.trim(jrRow.getFieldString("YD_STK_LYR_NO" ));	//야드적치단번호
				wmParam[ii][7] = slabUtils.trim(jrRow.getFieldString("YD_UP_COLL_SEQ"));	//야드권상모음순서
			}
			
			commDao.upsBatch("WrkBookMtl", wmParam, logId, methodNm);

			/**********************************************************
			* 4. 크레인스케줄(YDYDJ400) 전문 조회
			**********************************************************/
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
			jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
			jrYdMsg.setField("YD_EQP_ID"    , ydEqpId  ); //야드설비ID
			jrYdMsg.setField("YD_SCH_ST_GP" , ydSchStGp); //야드스케쥴기동구분
			jrYdMsg.setField("YD_SCH_REQ_GP", "U"      ); //야드스케쥴요청구분(보급)
			jrYdMsg.setField("V_MODIFIER"   , modifier ); //수정자

			JDTORecord jrRtn = slabUtils.addSndData(slabComm.getCrnSchMsg(jrYdMsg));
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 작업예약 아이디로 중복적치 여부 조회 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public boolean chkDupStkByWBookId(String ydWbookId, String logId) throws DAOException {
		String methodNm = "작업예약 아이디로 중복적치 여부 조회 [SlabYdSchSeEJB.chkDupStkByWBookId] < " + logId;
		
		try {
			slabUtils.printLog(logId, methodNm, "S+");
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			jrParam.setField("YD_WBOOK_ID",ydWbookId);
			
			JDTORecordSet recSetRes = commDao.select(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.chkDupStkByWBookId", logId, methodNm, "중복적치 조회"); 
			
			
			if(recSetRes == null || recSetRes.size() <=0 ) {
				slabUtils.printLog(logId,"작업예약 ["+ydWbookId+"] 재료에 대해 중복적치 발생 안함", "SL");
				slabUtils.printLog(logId, methodNm, "S-");
				return false;
			} 
			
			for(int ii=0; ii<recSetRes.size() ; ii++){
				int stkCnt = Integer.parseInt(slabUtils.trim(recSetRes.getRecord(ii).getFieldString("STK_CNT")));
				String stlNo = slabUtils.trim(recSetRes.getRecord(ii).getFieldString("STL_NO"));
				//중복적치된 재료 발견 
				if(stkCnt > 1) {
					slabUtils.printLog(logId,"재료번호 ["+stlNo+"] 중복적치 발생", "SL");
					return true;
				}
			}
			
			
			slabUtils.printLog(logId, methodNm, "S-");

			return false;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

}
