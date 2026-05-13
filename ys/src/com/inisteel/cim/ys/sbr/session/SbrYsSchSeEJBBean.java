/**
 * @(#)SbrYsSchSeEJBBean
 *
 * @version          V1.00
 * @author           김현규
 * @date             2026/01/07
 *
 * @description      특수강소형야드 Schedule 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2026/01/07                         김현규      최초 등록
 */
package com.inisteel.cim.ys.sbr.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.session.YsComm;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsConstant;
import com.inisteel.cim.ys.common.util.YsQueryIFSbr;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;



/**
 *      [A] 클래스명 : BILLET 야드 Schedule 처리
 *
 * @ejb.bean name="SbrYsSchSeEJB" jndi-name="SbrYsSchSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class SbrYsSchSeEJBBean extends BaseSessionBean implements YsQueryIFSbr {
	
	private static final long serialVersionUID = 1L;
	private YsCommUtils commUtils = new YsCommUtils();
	private YsConstant  constant  = new YsConstant();
	private YsCommDAO   commDao   = new YsCommDAO();
	private YsComm      ysComm    = new YsComm();
	private SbrYsComm   SbrYsComm = new SbrYsComm();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	/**	
	 *      [A] 오퍼레이션명 : 크레인스케줄
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYSYSJ702(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인스케줄[SbrYsSchSeEJB.rcvYSYSJ702] < " + rcvMsg.getResultMsg();
		String logId     = rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");	//
			
			//전문 Return
			JDTORecord    jrRtn   = null;
			
			//파라메타 (수신 항목값)
			String msgId     = commUtils.getMsgId(rcvMsg);								//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydWbookId = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"));	//야드작업예약ID
			String ydSchCd   = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"  ));	//야드스케줄코드
			String ydEqpId   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"  ));	//야드설비ID
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"   ));	//수정자(Backup Only)
			if( "".equals(modifier) ) { modifier = msgId; }
			
			StringBuffer sbImpPros  = new StringBuffer();								//주요진행내용로그

			commUtils.printLog(logId, "스케줄코드[" + ydSchCd + "], 설비ID[" + ydEqpId + "], 작업예약ID[" + ydWbookId + "], 수정자[" + modifier + "]", "SL");
			sbImpPros.append("[크레인스케줄 > 파라메타정보]::시작 \r\n");
			sbImpPros.append(" > 파라메타정보::스케줄코드[" + ydSchCd + "], 설비ID[" + ydEqpId + "], 작업예약ID[" + ydWbookId + "], 수정자[" + modifier + "] \r\n");
			sbImpPros.append("[크레인스케줄 > 파라메타정보]::종료 \r\n \r\n");
			
			//공용변수
			int    intRtnVal = 0;
			String trtMsg    = "";	//처리메세지
			String ydL3Msg   = "";	//야드L3MESSAGE
			
			//조회 및 등록용 변수
			JDTORecordSet rsWbook = JDTORecordFactory.getInstance().createRecordSet("Temp");
			JDTORecord    jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);				//Log ID
			jrParam.setResultMsg(methodNm);				//Log Method Name
			jrParam.setField("YD_WBOOK_ID", ydWbookId);	//야드작업예약ID
			jrParam.setField("YD_SCH_CD"  , ydSchCd  );	//야드스케줄코드
			jrParam.setField("YD_EQP_ID"  , ydEqpId  );	//야드설비ID
			jrParam.setField("MODIFIER"   , modifier );	//수정자
			
			
			/**********************************************************
			* 0. 대형옥내야드-추출대(GEPC11/GEPC21) 모든 스위치 OFF 여부 체크
			**********************************************************/
        	//추출대 전체 OFF 이면 작업지시[추출대입고(#1/#2)), 정렬대이적(#11/#12/#21/#22)] 작성 중지
        	String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "002");
        	if( "Y".equals(sRuleItem) && (constant.SCH_CD_GDPC11LM.equals(ydSchCd) || constant.SCH_CD_GDPC21LM.equals(ydSchCd)) ) {
        		
        		JDTORecordSet rsParam = commDao.select(jrParam, getSwitchOffYn, logId, methodNm, "추출대(GEPC11/GEPC21) 모든 스위치 OFF 여부 체크");

    			if( rsParam.size() > 0 ) {
    				
    				rsParam.first();
    				JDTORecord recInPara = rsParam.getRecord();
    				
    				String sOFF_YN = commUtils.trim(recInPara.getFieldString("OFF_YN"));	//추출대(GEPC11/GEPC21) 모든 스위치 OFF 여부 : [N:스위치 ON 존재],[Y:스위치 ON 미존재]
    				
            		if( "Y".equals(sOFF_YN) ) {
            			throw new Exception("오류:추출대(GEPC11/GEPC21) 모든 스위치 OFF~!!!");
            		}
    			}
        	}
        	
        	
			/**********************************************************
			* 1. 파라메타 정보 체크 및 "작업예약ID" 조회
			**********************************************************/
			sbImpPros.append("[크레인스케줄 > 파라메타정보체크및'작업예약ID'조회]::시작 \r\n");
			
			//1.1. 스케줄코드 체크
			if( "".equals(ydWbookId) && !"".equals(ydSchCd) ) {
				
				JDTORecord jrChk = SbrYsComm.chkSchCd(jrParam);
				
				ydL3Msg = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"));
				if( !"".equals(ydL3Msg) ) {
					ydSchCd = "";
				}
			}
			
			sbImpPros.append(" > 스케줄코드체크::완료 \r\n");
			
			//1.2. 설비상태 체크
			if( "".equals(ydWbookId) && "".equals(ydSchCd) && !"".equals(ydEqpId) ) {
				
				JDTORecord jrChk = SbrYsComm.chkEqpStat(jrParam);

				ydL3Msg = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"));
				if( !"".equals(ydL3Msg) ) {
					ydEqpId = "";
				}
			}
			
			sbImpPros.append(" > 설비상태체크  ::완료 \r\n");
			
			//1.3. 작업예약ID 조회 (작업예약ID 존재여부 확인)
			//     - [작업예약ID]가 있으면 (해당작업예약ID로 직접 조회를 해서 작업진행 - 차량도착인 경우)
			if( !"".equals(ydWbookId) ) {
				rsWbook = commDao.select(jrParam, getCrnSchWbook,      logId, methodNm, "(작업예약ID) 작업예약ID 조회");
			//     - [스케줄코드]가 있으면
			} else if( !"".equals(ydSchCd) ) {
				rsWbook = commDao.select(jrParam, getCrnSchWbookSchcd, logId, methodNm, "(스케줄코드) 작업예약ID 조회");
			//     - [설비ID]가 있으면
			} else if( !"".equals(ydEqpId) ) {
				rsWbook = commDao.select(jrParam, getCrnSchWbookEqp,   logId, methodNm, "  (설비코드) 작업예약ID 조회");
			} else {
				throw new Exception("오류:작업예약ID조회 항목 없음");
			}

			if( rsWbook != null && rsWbook.size() > 0 ) {
				ydWbookId = commUtils.trim(rsWbook.getRecord(0).getFieldString("YD_WBOOK_ID"));
			} else {
				throw new Exception("오류:" + trtMsg + " >> 작업예약정보 없음");
			}
			
			commUtils.printLog(logId, trtMsg + " >> 작업예약ID조회 [" + ydWbookId + "]", "SL");
			sbImpPros.append(" > 작업예약ID조회::ydWbookId("+ydWbookId+") \r\n");
			
			jrParam.setField("YD_WBOOK_ID", ydWbookId);	//야드작업예약ID

			sbImpPros.append("[크레인스케줄 > 파라메타정보체크및'작업예약ID'조회]::종료 \r\n \r\n");
			
			
			/**********************************************************
			* 2. "크레인작업재료"의 저장위치를 현재 적치단 저장위치로 Update (별도 Transaction 으로 처리)
			**********************************************************/
			sbImpPros.append("[크레인스케줄 > '크레인작업재료'의저장위치를현재적치단저장위치로Update]::시작 \r\n");
			
			EJBConnector tranConn = new EJBConnector("default", "SbrYsSchSeEJB", this);
			tranConn.trx("updCrnSchWB", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			sbImpPros.append("[크레인스케줄 > '크레인작업재료'의저장위치를현재적치단저장위치로Update]::종료 \r\n \r\n");
			
			
			/**********************************************************
			* 3. 스케줄 수행가능여부 판단 및 크레인 결정, TO위치 사전 점검
			**********************************************************/
			sbImpPros.append("[크레인스케줄 > 스케줄수행가능여부판단 및 크레인결정, TO위치사전점검]::시작 \r\n");
			
			//조회된 작업예약ID로 상태정보 Check
			String ydToLocDcsnMtd = "";		//야드To위치결정방법
			String ydToLocGuide   = ""; 	//야드To위치Guide
//			String toLocChkGp     = ""; 	//To위치점검을 위한 구분(G:To위치Guide, C:차량상차)
			String trnEqpCd       = ""; 	//운송장비코드
			String ydEqpStat      = "";		//야드설비상태
			String ydSchPrior     = "";
			String sYD_WBOOK_DT   = "";
			
			trtMsg = "스케줄 수행가능여부 판단을 위한 상태정보 조회 [작업예약ID : " + ydWbookId + "]";
			
			JDTORecordSet jsChk = commDao.select(jrParam, getCrnSchStat, logId, methodNm, "스케줄 수행가능여부 판단을 위한 상태정보 조회");
			
			if( jsChk.size() <= 0 ) {
				throw new Exception("오류:" + trtMsg + " >> 상태정보 없음");
			} else {
				JDTORecord jrChk = jsChk.getRecord(0);
				
				//3.1. 스케줄 수행가능여부 판단
				ydSchCd                = commUtils.trim(jrChk.getFieldString("YD_SCH_CD"          ));	//야드스케줄코드
				ydToLocDcsnMtd         = commUtils.trim(jrChk.getFieldString("YD_TO_LOC_DCSN_MTD" ));	//야드To위치결정방법
				ydToLocGuide           = commUtils.trim(jrChk.getFieldString("YD_TO_LOC_GUIDE"    ));	//야드To위치Guide
//				toLocChkGp             = commUtils.trim(jrChk.getFieldString("TO_LOC_CHK_GP"      ));	//To위치점검구분
				ydSchPrior             = commUtils.trim(jrChk.getFieldString("YD_WRK_CRN_PRIOR"   ));	//야드스케줄우선순위
				String ydWrkPlanCrn    = commUtils.trim(jrChk.getFieldString("YD_WRK_PLAN_CRN"    ));	//야드작업계획크레인             : 크레인작업예약 생성시 "스케줄코드별로 설정된 주/부 크레인" 중 작업 가능한 크레인
				String ydEqpStatPln    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_PLN"    ));	//야드작업계획크레인 설비상태    : 크레인작업예약 생성시 "스케줄코드별로 설정된 주/부 크레인" 중 작업 가능한 크레인의 설비상태
				String ydEqpWrkModePln = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_PLN"));	//야드작업계획크레인 설비작업Mode: [0:Offline Mode],[1:Online Mode]
				String ydWrkCrn        = commUtils.trim(jrChk.getFieldString("YD_WRK_CRN"         ));	//야드작업크레인                 : 현시점에 해당 스케줄코드에 "설정된 주/부 크레인" 중 작업 가능한 크레인
				String ydEqpStatWrk    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_WRK"    ));	//야드작업크레인 설비상태        : 현시점에 해당 스케줄코드에 "설정된 주/부 크레인" 중 작업 가능한 크레인의 설비상태
				String ydEqpWrkModeWrk = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_WRK"));	//야드작업크레인 설비작업Mode    : [0:Offline Mode],[1:Online Mode]
				String toLocColStat    = commUtils.trim(jrChk.getFieldString("TO_LOC_COL_STAT"    ));	//TO위치-야드적치열활성상태      : [L:적치가능][C:비활성화] <- 차량 포인트
				
				String ydEqpStatTcar   = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_TCAR"   ));	//대차 야드설비상태
				String ydEqpWrkModeTcar= commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_TCAR"));	//대차 설비작업MODE [0:Offline Mode],[1:Online Mode]
//				String ydEnabledTcar   = commUtils.trim(jrChk.getFieldString("ENABLED_TCAR"       ));	//사용가능한 다른 대차
//				String ydEnabledTcarCnt= commUtils.trim(jrChk.getFieldString("ENABLED_TCAR_CNT"   ));	//사용가능한 다른 대차 개수
				
				String ydCurrBayGp     = commUtils.trim(jrChk.getFieldString("YD_CURR_BAY_GP"     ));	//대차 현재동
				String cmDupYn         = commUtils.trim(jrChk.getFieldString("CM_DUP_YN"          ));	//크레인스케줄 재료중복여부
				String clDupGp         = commUtils.trim(jrChk.getFieldString("CL_DUP_GP"          ));	//크레인스케줄 저장위치중복여부
				String ysStkColGp      = commUtils.trim(jrChk.getFieldString("YS_STK_COL_GP"      ));	//크레인작업재료 적치열
				String tcumWrkbookChk  = commUtils.trim(jrChk.getFieldString("TCUM_WRKBOOK_CHK"   ));	//선택된 작업예약이 "대차상차" 작업일 경우 선행 작업예약 존재여부 확인
				
				int ydCurrBayShSh      = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("YD_CURR_BAY_SH"),"0"));	//대차         재료매수(대차 상하차시)
				int ttMtlSh            = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("TT_MTL_SH"),     "0"));	//전체         재료매수
				int wmMtlSh            = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("WM_MTL_SH"),     "0"));	//작업예약     재료매수
				int stMtlSh            = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("ST_MTL_SH"),     "0"));	//저장품       재료매수
				int slMtlSh            = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("SL_MTL_SH"),     "0"));	//적치단       재료매수
				int statCSh            = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("STAT_C_SH"),     "0"));	//적치중인     재료매수(야드적치단재료상태가 적치 중[C] 인 재료수)
				int abLocSh            = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("AB_LOC_SH"),     "0"));	//저장위치이상 재료매수
				sYD_WBOOK_DT           = commUtils.trim(jrChk.getFieldString("YD_WBOOK_DT"        ));
				
				if( wmMtlSh == 0 ) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료 정보 없음");
				} else if( wmMtlSh != ttMtlSh ) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료 적치단 중복 등록 [작업예약: " + wmMtlSh + ", 적치단: " + ttMtlSh + "]");
				} else if( wmMtlSh != slMtlSh ) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료 적치단 정보 이상 [" + (wmMtlSh - slMtlSh) + "매]");
				} else if( wmMtlSh != statCSh ) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료 적치중[C]이 아님 [" + (wmMtlSh - statCSh) + "매]");
				} else if( wmMtlSh != stMtlSh ) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료 저장품 정보 이상 [" + (wmMtlSh - stMtlSh) + "매]");
				} else if( abLocSh > 0 ) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치 이상 [" + abLocSh + "매]");
				} else if( "Y".equals(cmDupYn) ) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료가 기 등록된 크레인작업재료와 중복");
				} else if( "1".equals(clDupGp) ) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케줄 권하위치와 중복");
				//차량하차 시 동일 적치 BED/단에 있는 소재가 n개일 경우 작업예약이 n개로 나누어 생성될 수 있어 권상위치 중복 발생 가능함
//				} else if( "2".equals(clDupGp) ) {
//					throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케줄 권상위치와 중복");
				} else if( !"".equals(ydToLocGuide) && "TR".equals(ydToLocGuide.substring(2, 4)) && !"L".equals(toLocColStat)  ) {
					throw new Exception("오류:" + trtMsg + " >> 차량 적치열 활성상태가 적치가능[L]이 아님 ");		//"차량"이 도착하여야만 "차량 포인트(열)"의 활성화상태가 '적치가능[L]'이 됨
				} else if( !"".equals(ydToLocGuide) && "TC".equals(ydToLocGuide.substring(2, 4)) && "B".equals(ydEqpStatTcar) ) {
					throw new Exception("오류:" + trtMsg + " >> 대차 상태가 'B(고장)' 임 ");						//대차 상차시 대차 상태가 'B(고장)'이면 크레인스케줄 생성 불가함
				} else if( !"".equals(ydToLocGuide) && "TC".equals(ydToLocGuide.substring(2, 4)) && !"1".equals(ydEqpWrkModeTcar) ) {
					throw new Exception("오류:" + trtMsg + " >> 대차 작업모드가 '0:Offline Mode' 임 ");				//대차 상차시 대차 작업모드가 '0:Offline Mode'이면 크레인스케줄 생성 불가함
				} else if( !"".equals(ydToLocGuide) && "TC".equals(ydToLocGuide.substring(2, 4)) && !"A".equals(ydCurrBayGp) ) {
					throw new Exception("오류:" + trtMsg + " >> 대차 현재동이 'A'가 아님 ");						//"대차"가 "A"동에 도착하여야만 대차 크레인스케줄 생성이 가능함
				} else if( !"".equals(ydToLocGuide) && "TC".equals(ydToLocGuide.substring(2, 4)) && "A".equals(ydCurrBayGp) && ydCurrBayShSh > 0 ) {
					throw new Exception("오류:" + trtMsg + " >> 대차에 소재가 존재함 ");							//대차 상차시 대차에 소재가 없어야만 대차 크레인스케줄 생성이 가능함
				} else if( "1".equals(tcumWrkbookChk) ) {
					throw new Exception("오류:" + trtMsg + " >> 선행되어야 할 대차상차 작업예약이 존재함 ");		//선행되어야 할 "대차상차" 작업예약이 존재함
				}
				 
				commUtils.printLog(logId, "야드작업계획 크레인[" + ydWrkPlanCrn + "], 야드작업계획크레인 설비상태[" + ydEqpStatPln + "], 야드작업계획크레인 설비작업Mode[" + ydEqpWrkModePln + "]", "SL");
				commUtils.printLog(logId, "야드작업     크레인[" + ydWrkCrn + "],     야드작업크레인     설비상태[" + ydEqpStatWrk + "], 야드작업크레인     설비작업Mode[" + ydEqpWrkModeWrk + "]", "SL");
				
				
				//3.2. 크레인 설정(결정)
				sbImpPros.append("	[크레인스케줄 > 스케줄수행가능여부판단 및 크레인결정, TO위치사전점검 > 추출대 입고 크레인 지정여부 확인]::시작 \r\n");
				
	        	//3.2.1. "추출대 입고"이고, "지정크레인" 존재시 지정된 크레인으로 변경
				jrParam.setField("YS_STK_COL_GP", ysStkColGp);	//크레인작업재료 적치열
	    		JDTORecordSet rsParam = commDao.select(jrParam, getPCFixedEqpId, logId, methodNm, "추출대(GDPC11/GDPC21) 입고용 지정 크레인 조회");
	    		
				if( rsParam.size() > 0 ) {
					
					rsParam.first();
					JDTORecord recInPara = rsParam.getRecord();
					
					String sPCFixed_ydWrkCrn        = commUtils.trim(recInPara.getFieldString("YD_EQP_ID"      ));
					String sPCFixed_ydEqpStatWrk    = commUtils.trim(recInPara.getFieldString("YD_EQP_STAT"    ));
					String sPCFixed_ydEqpWrkModeWrk = commUtils.trim(recInPara.getFieldString("YD_EQP_WRK_MODE"));
					
					commUtils.printLog(logId, "	 > 추출대입고 지정크레인::sPCFixed_ydWrkCrn ("+sPCFixed_ydWrkCrn+"), sPCFixed_ydEqpStatWrk ("+sPCFixed_ydEqpStatWrk+"), sPCFixed_ydEqpWrkModeWrk ("+sPCFixed_ydEqpWrkModeWrk+")", "SL");
        			sbImpPros.append("	 > 추출대입고 지정크레인::sPCFixed_ydWrkCrn ("+sPCFixed_ydWrkCrn+"), sPCFixed_ydEqpStatWrk ("+sPCFixed_ydEqpStatWrk+"), sPCFixed_ydEqpWrkModeWrk ("+sPCFixed_ydEqpWrkModeWrk+") \r\n");
        			
        			//"지정크레인" 존재시 지정된 크레인으로 변경
	        		if( !"".equals(sPCFixed_ydWrkCrn) && !"ALL".equals(sPCFixed_ydWrkCrn) ) {
	        			ydWrkPlanCrn    = sPCFixed_ydWrkCrn;
	        			ydEqpStatPln    = sPCFixed_ydEqpStatWrk;
	        			ydEqpWrkModePln = sPCFixed_ydEqpWrkModeWrk;
	        		}
				}

				sbImpPros.append("	[크레인스케줄 > 스케줄수행가능여부판단 및 크레인결정, TO위치사전점검 > 추출대 입고 크레인 지정여부 확인]::종료 \r\n");
	        	
	        	
				//3.2.2. 크레인작업예약 생성시 지정된 크레인을 최우선 지정: 지정된 크레인은 작업예약-'야드작업계획크레인'으로 등록됨
				//       1) 이적 팝업에서 강제로 지정된 크레인
				//       2) "추출대 입고"이고, "지정크레인"이 존재할 경우
				if( !"".equals(ydWrkPlanCrn) && !"B".equals(ydEqpStatPln) && "1".equals(ydEqpWrkModePln) ) {	//B:고장, 1:Online Mode
					ydEqpId   = ydWrkPlanCrn;	//야드설비ID
					ydEqpStat = ydEqpStatPln;	//야드설비상태
				} else {
					ydEqpId   = ydWrkCrn;		//야드설비ID
					ydEqpStat = ydEqpStatWrk;	//야드설비상태
				}
				
				commUtils.printLog(logId, trtMsg + " >> 작업예약 지정크레인 [" + ydWrkPlanCrn + "]", "SL");
				commUtils.printLog(logId, trtMsg + " >> 스케줄   지정크레인 [" + ydEqpId + "], 야드설비상태 [" + ydEqpStat + "]", "SL");
				sbImpPros.append(" > 크레인&상태::야드작업계획크레인 (" + ydWrkPlanCrn + "), 야드설비상태(작업계획크레인) (" + ydEqpStatPln + "), 야드설비작업Mode(작업계획크레인) (" + ydEqpWrkModePln + "), 야드작업크레인 (" + ydWrkCrn + "), 야드설비상태(작업크레인) (" + ydEqpStatWrk + ")  \r\n");
				sbImpPros.append(" > 크레인&상태::크레인 설정(결정)-야드설비ID (" + ydEqpId + "), 크레인 설정(결정)-야드설비상태 (" + ydEqpStat + ") \r\n");
			}
			sbImpPros.append("[크레인스케줄 > 스케줄수행가능여부판단 및 크레인결정, TO위치사전점검]::종료 \r\n \r\n");
			
			
			/**********************************************************
			* 4. 그룹핑 파라미터 셋팅 (작업예약에 대한 크레인스케줄 생성정보 묶음)
  			**********************************************************/
			commUtils.printLog(logId, "그룹핑 파라미터 셋팅 시작", "SL");
			sbImpPros.append("[크레인스케줄 > 그룹핑파라미터셋팅]::시작 \r\n");
			
			JDTORecord jrParamSet = JDTORecordFactory.getInstance().create();
			jrParamSet.setResultCode(logId);									//Log ID
			jrParamSet.setResultMsg(methodNm);									//Log Method Name
			jrParamSet.setField("YD_WBOOK_ID"			, ydWbookId      );		//야드작업예약ID
			jrParamSet.setField("YD_SCH_CD"  			, ydSchCd        );		//야드스케줄코드
			jrParamSet.setField("YD_EQP_ID"  			, ydEqpId        );		//야드설비ID
			jrParamSet.setField("YD_SCH_PRIOR"  		, ydSchPrior     );		//야드스케줄우선순위
			jrParamSet.setField("YD_TO_LOC_DCSN_MTD"  	, ydToLocDcsnMtd );		//야드To위치결정방법
			jrParamSet.setField("YD_TO_LOC_GUIDE"  		, ydToLocGuide   );		//야드To위치Guide
			jrParamSet.setField("MODIFIER"   			, modifier       );		//수정자
			jrParamSet.setField("YD_WBOOK_DT"  		    , sYD_WBOOK_DT   );
			
			//스케줄코드가 "차량하차[TR:차량, L:입고]"일 경우
			JDTORecordSet outRecset	= JDTORecordFactory.getInstance().createRecordSet("Temp");
			if( ydSchCd.substring(2,4).equals("TR") && ydSchCd.substring(6,7).equals("L") ) {
				sbImpPros.append("	[크레인스케줄 > 그룹핑파라미터셋팅 > 차량하차]::시작 \r\n");
				intRtnVal = this.CrnSchGrpTr(logId, methodNm, jrParamSet, outRecset, sbImpPros);
				sbImpPros.append("	[크레인스케줄 > 그룹핑파라미터셋팅 > 차량하차]::종료 \r\n");
			} else {
				sbImpPros.append("	[크레인스케줄 > 그룹핑파라미터셋팅 > 차량하차 외]::시작 \r\n");
				intRtnVal = this.CrnSchGrp(logId, methodNm, jrParamSet, outRecset, sbImpPros);
				sbImpPros.append("	[크레인스케줄 > 그룹핑파라미터셋팅 > 차량하차 외]::종료 \r\n");
			}
			
			if( intRtnVal == -1 ) {
				m_ctx.setRollbackOnly();
				throw new Exception("오류:그룹핑 파라미터 셋팅 없음");
			}
			sbImpPros.append("[크레인스케줄 > 그룹핑파라미터셋팅]::종료 \r\n \r\n");
			
			
			/**********************************************************
			* 5. 크레인스케줄, 크레인작업재료 등록
			*    - 권상위치 U로 변경
  			**********************************************************/
			commUtils.printLog(logId, "크레인스케줄, 크레인작업재료 등록 시작 ", "SL");
			sbImpPros.append("[크레인스케줄 > 크레인스케줄,크레인작업재료 등록]::시작 \r\n");	
			
			intRtnVal = this.CrnSchIns(logId, methodNm, outRecset, jrParamSet, sbImpPros);
			
			if( intRtnVal == -1 ) {
				m_ctx.setRollbackOnly();
				throw new DAOException("크레인스케줄 및 작업재료 등록 오류");
			}

			sbImpPros.append("[크레인스케줄 > 크레인스케줄,크레인작업재료 등록]::종료 \r\n \r\n");
			
			
			/**********************************************************
			* 6. TO위치 결정
  			**********************************************************/
			commUtils.printLog(logId, "TO위치 결정 시작 ", "SL");
			sbImpPros.append("[크레인스케줄 > TO위치결정]::시작 \r\n");	
			
			intRtnVal = this.LocSrcRngDataSet(logId, methodNm, jrParamSet, sbImpPros);
			
			if( intRtnVal == -1 ) {
				m_ctx.setRollbackOnly();
				throw new DAOException("TO위치 결정 오류");
			}
			
			
			/**********************************************************
			* 7. 크레인작업지시 호출 [내부 크레인 작업지시 요구 (rcvYSYSJ001)]
  			**********************************************************/
			//크레인스케줄의 설비호기 재호출
			JDTORecordSet rsCrnsch = commDao.select(jrParamSet, getYsCrnSch, logId, methodNm, "크레인스케줄 조회"); 	    	
			
			commUtils.printLog(logId, "작업예약ID로 크레인스케줄 조회::크레인스케줄 개수 (" + rsCrnsch.size() + ")", "SL");	
			String szYD_EQP_ID	= "";
			
			if( rsCrnsch.size() > 0 ) {
				
				rsCrnsch.first();
				JDTORecord recInPara = rsCrnsch.getRecord();
				
				szYD_EQP_ID	= commUtils.trim(recInPara.getFieldString("YD_EQP_ID"));	//야드설비ID
				ydEqpStat	= commUtils.trim(recInPara.getFieldString("YD_EQP_STAT"));	//야드설비상태: [W:대기(Wait)]
				
			} else {
				szYD_EQP_ID = ydEqpId; 
			}
			
			//야드설비상태가 대기이면 내부크레인작업지시요구 전송
			commUtils.printLog(logId, "야드설비상태 [ydEqpStat] :: " + ydEqpStat, "");
			if( "W".equals(ydEqpStat) ) {
				
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name

				jrYdMsg.setField("JMS_TC_CD"         , "YSYSJ001"               ); //JMSTC코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("YD_EQP_ID"         , szYD_EQP_ID              ); //야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태: [W:명령선택대기]
				
				sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "001");
				if( sRuleItem.equals("Y") ) {
					commUtils.printLog(logId, "크레인작업지시요구 [jrYdMsg] :: " + jrYdMsg.toString(), "");			
					jrRtn = commUtils.addSndData(jrYdMsg);
				}
			}
			
			commUtils.printLog(logId, "[스케줄메인종료]", "SL");
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;

		} catch (DAOException e ) {
			throw e;
		} catch (Exception e ) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}  // End of rcvYSYSJ702(...)

	/**
     * 오퍼레이션명 : TO위치 결정
     * @param  inRecord, recGetCrnWrkMtl, rsResultCrnwrkmtl
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int LocSrcRngDataSet (String logId, String methodNms, JDTORecord inRecord, StringBuffer sbImpPros)throws JDTOException{
    	String methodNm = "TO위치 결정[SbrYsSchSeEJB.LocSrcRngDataSet] < " + methodNms;
    	
    	JDTORecordSet rsWbook        = null;
    	JDTORecord    recWbook       = null;
    	JDTORecordSet rsCrnsch       = null;
    	JDTORecord    recCrnSch      = null;
    	JDTORecordSet rsCrnwrkmtl    = null;
    	JDTORecordSet rsRSS          = null;
    	
    	String szCrnSchId 	         = "";
    	String szSchCd    	         = "";
    	String szToLocDcsnMtd        = "";
		String szWbookId  	         = "";
		String szEqpId    	         = "";
    	String szRtnMsg              = "";
		String szCRN_YD_TO_LOC_GUIDE = "";
		
    	int    intRtnVal             = 0 ;
		
        try{
        	commUtils.printLog(logId, methodNm, "S+");
        	
			//파라미터
        	szWbookId = commUtils.trim(inRecord.getFieldString("YD_WBOOK_ID" ));	
        	szEqpId   = commUtils.trim(inRecord.getFieldString("YD_EQP_ID"   ));	
        	

    		/**********************************************************
			* 1. 작업예약 조회
			**********************************************************/
			sbImpPros.append("	[크레인스케줄 > TO위치결정 > 작업예약조회]::시작 \r\n");
			sbImpPros.append("	 > 작업예약조회::szWbookId ("+szWbookId+") \r\n");
			
        	rsWbook = JDTORecordFactory.getInstance().createRecordSet("");
			
        	rsWbook = commDao.select(inRecord, getYdWrkbook, logId, methodNm, "작업예약 조회"); 
	    	
	    	if( rsWbook == null || rsWbook.size() <= 0 ) {
				commUtils.printLog(logId, "["+methodNm+"] 작업예약 조회(getYdWrkbook) data not found", "SL");
			}
			
	    	rsWbook.absolute(1);
			recWbook = JDTORecordFactory.getInstance().create();
			recWbook.setRecord(rsWbook.getRecord());

			sbImpPros.append("	[크레인스케줄 > TO위치결정 > 작업예약조회]::종료 \r\n \r\n");
			
			
    		/**********************************************************
			* 2. 크레인스케줄 조회
			**********************************************************/
			sbImpPros.append("	[크레인스케줄 > TO위치결정 > 크레인스케줄조회]::시작 \r\n");
			sbImpPros.append("	 > 크레인스케줄조회::szWbookId ("+szWbookId+"), szEqpId ("+szEqpId+") \r\n");
			
			rsCrnsch = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recInPara = JDTORecordFactory.getInstance().create();
			recInPara.setField("YD_WBOOK_ID", szWbookId);
			recInPara.setField("YD_EQP_ID"	, szEqpId  );
			
			rsCrnsch = commDao.select(inRecord, getYdCrnschByEqpIdandWBookId, logId, methodNm, "크레인스케줄 조회");
	    	
			commUtils.printLog(logId, "크레인스케줄 조회 > 스케줄 횟수 : " + rsCrnsch.size(), "SL");
			sbImpPros.append("	[크레인스케줄 > TO위치결정 > 크레인스케줄조회]::종료 \r\n");
			

    		/**********************************************************
			* 3. 크레인스케줄별 권하지시위치 결정
			**********************************************************/
    		StringBuffer sbImpPros_ToLoc = null;
		    for( int Loop_i = 1; Loop_i <= rsCrnsch.size(); Loop_i++ ) {

        		rsCrnsch.absolute(Loop_i);
        		recCrnSch = rsCrnsch.getRecord();
        		
        		//조회된 크레인스케줄 정보 
        		szCrnSchId     		  = commUtils.trim(recCrnSch.getFieldString("YD_CRN_SCH_ID"     ));
        		szSchCd        		  = commUtils.trim(recCrnSch.getFieldString("YD_SCH_CD"         ));
        		szToLocDcsnMtd 		  = commUtils.trim(recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD"));
        		szCRN_YD_TO_LOC_GUIDE = commUtils.trim(recCrnSch.getFieldString("YD_TO_LOC_GUIDE"   ));
        		
        		commUtils.printLog(logId, "[" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "]에 대한 권하지시위치 결정 ", "SL");
        		sbImpPros_ToLoc = new StringBuffer();
        		sbImpPros_ToLoc.append("	[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정][순번:"+Loop_i+"]::시작 \r\n");
        		sbImpPros_ToLoc.append("	 > 조회된 크레인스케줄 정보::szCrnSchId  ("+szCrnSchId+"), szSchCd  ("+szSchCd+"), szToLocDcsnMtd  ("+szToLocDcsnMtd+"), szCRN_YD_TO_LOC_GUIDE  ("+szCRN_YD_TO_LOC_GUIDE+") \r\n");
				
        		
        		//3.1. 크레인스케줄 작업재료 조회
				rsCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
        		JDTORecord recInData = JDTORecordFactory.getInstance().create();
        		recInData.setField("YD_CRN_SCH_ID", szCrnSchId);
        		
        		rsCrnwrkmtl = commDao.select(recInData, getYdCrnwrkmtlBySchId, logId, methodNm, "크레인스케줄 작업재료 조회");
        		
        		if( rsCrnwrkmtl.size() <= 0 ) {
    				commUtils.printLog(logId, "LocSrcRngDataSet : 위치검색범위 조회 Data Setting 실패!!", "SL");
        		}
        		
        		
        		//3.2. TO위치 결정
        		//3.2.1. "보조작업"인 경우 (야드로...)
            	if( szToLocDcsnMtd.equals("W") ) {
            		
        			//보조작업 TO위치결정
            		commUtils.printLog(logId, "["+Loop_i+"]번째 크레인스케줄["+szCrnSchId+"]은 보조작업 스케줄의  To위치 결정 시작", "SL");
        			sbImpPros_ToLoc.append("		[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 보조작업 TO위치결정]::시작 \r\n");
        			szRtnMsg = this.procDummyToLoc(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook, sbImpPros_ToLoc);
    				
    				if( szRtnMsg.equals(YsConstant.RETN_CD_SUCCESS) ) {
            			commUtils.printLog(logId, "["+Loop_i+"]번째 크레인스케줄["+szCrnSchId+"]/스케줄코드["+szSchCd+"]: 보조작업 스케줄의  To위치 결정 성공", "SL");
    				} else {
    					commUtils.printLog(logId, "["+Loop_i+"]번째 크레인스케줄["+szCrnSchId+"]/스케줄코드["+szSchCd+"]: 보조작업 스케줄의  To위치 결정 실패", "SL");
    				}
					commUtils.printLog(logId, "["+Loop_i+"]번째 크레인스케줄["+szCrnSchId+"]/스케줄코드["+szSchCd+"]: 보조작업 스케줄의  To위치 결정 완료", "SL");
					sbImpPros_ToLoc.append("		[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 보조작업 TO위치결정]::종료 \r\n");
					
            	//3.2.2. "주작업"인 경우
            	} else {
        			
    				//주작업 TO위치결정
        			commUtils.printLog(logId, "["+Loop_i+"]번째 크레인스케줄["+szCrnSchId+"]은 주작업 스케줄의  To위치 결정 시작", "SL");
        			sbImpPros_ToLoc.append("		[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 주작업 TO위치결정]::시작 \r\n");
    				szRtnMsg = this.procMainWrkLoc(logId, methodNm, rsCrnwrkmtl, recCrnSch, recWbook, sbImpPros_ToLoc);
    				
    				if( szRtnMsg.equals(YsConstant.RETN_CD_SUCCESS) ) {
    					commUtils.printLog(logId, "["+Loop_i+"]번째 크레인스케줄["+szCrnSchId+"]/스케줄코드["+szSchCd+"]: 주작업 스케줄의  To위치 결정 성공", "SL");
    				} else {
    					commUtils.printLog(logId, "["+Loop_i+"]번째 크레인스케줄["+szCrnSchId+"]/스케줄코드["+szSchCd+"]: 주작업 스케줄의  To위치 결정 실패", "SL");
    				}
					commUtils.printLog(logId, "["+Loop_i+"]번째 크레인스케줄["+szCrnSchId+"]/스케줄코드["+szSchCd+"]: 주작업 스케줄의  To위치 결정 완료"+szRtnMsg, "SL");
					sbImpPros_ToLoc.append("		[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 주작업 TO위치결정]::종료 \r\n");
            	}

            	sbImpPros_ToLoc.append("	[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정][순번:"+Loop_i+"]::종료 \r\n");
    			
    			
				//주요진행내용로그: 단계별항목정보 ([logId],[재료번호],[적치열],[적치BED],[적치단],[적치SEQ],[야드To위치Guide],[야드스케줄코드],[야드작업계획크레인])
				String sSCH_CONTENTS = logId+","+StringHelper.replaceStr(commUtils.getParamJs(rsCrnwrkmtl,new String[]{"SSTL_NO"}), " , ", " ")+","+commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LOC"))+",,"+commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LAYER"))+",,"+szCRN_YD_TO_LOC_GUIDE+","+szSchCd;	
				String sParamVal = "CS"+"#"+"G"+"#"+"D"+"#"+szWbookId+"#"+szCrnSchId+"#"+"1"+"#"+sbImpPros.toString()+sbImpPros_ToLoc.toString()+"\r\n"+"#"+sSCH_CONTENTS;
				JDTORecord   jrSchlog = JDTORecordFactory.getInstance().create();
						     jrSchlog.setField("PARAM_VALUE", sParamVal);
				EJBConnector SchLogConn = new EJBConnector("default", "SbrYsSchSeEJB", this);
							 SchLogConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrSchlog });
        	} 


    		/**********************************************************
			* 4. To위치 결정 실패시 default값으로 GFXXXXXX을 설정
			**********************************************************/
        	rsCrnsch  = JDTORecordFactory.getInstance().createRecordSet("");
    		recInPara = JDTORecordFactory.getInstance().create();
    		recInPara.setField("YD_WBOOK_ID", szWbookId);
    		recInPara.setField("YD_EQP_ID",   szEqpId);
    		
    		rsCrnsch = commDao.select(recInPara, getYdCrnschByEqpIdandWBookId, logId, methodNm, "크레인스케줄 조회");   		
    		
    		for( int Loop_i = 1; Loop_i <= rsCrnsch.size(); Loop_i++ ) {
    			
				rsCrnsch.absolute(Loop_i);
				
				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setRecord(rsCrnsch.getRecord());

				String sYS_UP_STK_COL_GP = commUtils.trim(recInPara.getFieldString("YS_UP_WO_LOC")).substring(0, 6);
				String sYS_DN_WO_LOC     = commUtils.trim(recInPara.getFieldString("YS_DN_WO_LOC"));
				String sYS_DN_STK_SPAN   = "";
				if( !"".equals(sYS_DN_WO_LOC) ) {
					sYS_DN_STK_SPAN   = commUtils.trim(recInPara.getFieldString("YS_DN_WO_LOC")).substring(0, 4);
				}

				commUtils.printLog(logId, "sYS_DN_STK_SPAN ["+sYS_DN_STK_SPAN+"], sYS_UP_STK_COL_GP ["+sYS_UP_STK_COL_GP+"]", "SL");
				
				//"권하지시위치"가 없을 경우
				if( "".equals(sYS_DN_WO_LOC) ) {

					recInPara.setField("YS_DN_WO_LOC", "GDXXXXXX");
					
					intRtnVal = commDao.update(recInPara, updCrnWrkMgtDnLoc, logId, methodNm, "크레인스케줄 갱신");
					if( intRtnVal <= 0 ) {
	    				commUtils.printLog(logId, "크레인스케줄 To위치 Default값 등록 실패!!", "SL");
					}
				}
			}
			
        	commUtils.printLog(logId, methodNm, "S-");
        	
			return intRtnVal = 1;
			
		} catch (DAOException e ) {
			throw e;
		} catch (Exception e ) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
    }//end of LocSrcRngDataSet()
    
    
	/**
     * 오퍼레이션명 : 크레인스케줄, 크레인작업재료 등록
     *  
     * @param  ● vResult, msgRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int CrnSchIns (String logId, String methodNms ,JDTORecordSet outRecset, JDTORecord jrParamSet, StringBuffer sbImpPros) throws JDTOException {
   		String methodNm = "크레인스케줄, 크레인작업재료 등록[SbrYsSchSeEJB.CrnSchIns] < " + methodNms;
   		
		JDTORecord    recInCrn  = null;
		JDTORecordSet rsBedLyr  = null;
		JDTORecord    recBedLyr = null;
		
		int    intRtnVal = 0;
		String szName    = "SYSTEM";
		
		try{
			commUtils.printLog(logId, methodNm, "S+");
			
			String szEqpId        = commUtils.trim(jrParamSet.getFieldString("YD_EQP_ID"   ));
			String szSchCd        = commUtils.trim(jrParamSet.getFieldString("YD_SCH_CD"   ));
			String szYD_SCH_PRIOR = commUtils.trim(jrParamSet.getFieldString("YD_SCH_PRIOR"));
			String szYD_WBOOK_DT  = commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_DT" ));
			
			commUtils.printParam(logId, outRecset);
			
			//그룹핑 파라미터 셋팅 (작업예약에 대한 크레인스케줄 생성정보 묶음) 개수 만큼 for()
			for( int i = 1; i <= outRecset.size(); i++ ) {

				outRecset.absolute(i);
				recInCrn = JDTORecordFactory.getInstance().create();
				recInCrn = outRecset.getRecord();
				

				sbImpPros.append("	[크레인스케줄 > 크레인스케줄,크레인작업재료등록 > 크레인스케줄생성] (순번:"+i+")::시작 \r\n");
				
				/**********************************************************
				* 1. 크레인스케줄 생성
				**********************************************************/
				//1.1. 크레인스케줄ID를 할당받는다
				String ydCrnSchId = commDao.getSeqId(logId, methodNm, "CrnSch");

				recInCrn.setField("YD_CRN_SCH_ID",    ydCrnSchId);
				recInCrn.setField("YD_EQP_ID",        szEqpId);
				recInCrn.setField("YD_GP",            recInCrn.getFieldString("YS_STK_COL_GP").substring(0,1));
				recInCrn.setField("YD_BAY_GP",        recInCrn.getFieldString("YS_STK_COL_GP").substring(1,2));
				recInCrn.setField("YD_SCH_CD",        szSchCd);	
				recInCrn.setField("REGISTER",         recInCrn.getFieldString("HANDLING_CNT"));
				recInCrn.setField("YD_SCH_PRIOR",     szYD_SCH_PRIOR);
				recInCrn.setField("YD_WBOOK_DT",      szYD_WBOOK_DT);
				recInCrn.setField("YD_SCH_ST_GP",     "A");		//야드스케줄기동구분: [A:Auto작업] [B:작업자 Backup] [M:Manual 작업]
				recInCrn.setField("YS_UP_WO_LOC",     recInCrn.getFieldString("YS_STK_COL_GP") + recInCrn.getFieldString("YS_STK_BED_NO"));
				recInCrn.setField("YS_UP_WO_LAYER",   recInCrn.getFieldString("YS_STK_LYR_NO"));
				recInCrn.setField("YD_WRK_PROG_STAT", "W");		//야드작업진행상태: [W:명령선택대기]
				
				if( commUtils.trim(recInCrn.getFieldString("YS_UP_WO_LOC")).equals("") ) {
					throw new JDTOException("오류:권상지시위치가 없습니다");
				}
				
				//1.2. 권상위치 재료정보 조회
				rsBedLyr = JDTORecordFactory.getInstance().createRecordSet("");
				rsBedLyr = commDao.select(recInCrn, getYdStklyrSstl, logId, methodNm, "크레인스케줄(권상위치) 재료정보 조회");
				if( rsBedLyr.size() <= 0 ) {
					throw new Exception("오류:크레인스케줄(권상위치) 재료정보 조회 >> 조회 Data 없음");
				}			
				
				//이전 "그룹핑 파라미터 셋팅" 단계에서 "YD_TO_LOC_DCSN_MTD (TO위치결정방법: [M:이적] [W:보조])" 항목값이 결정됨
				commUtils.printLog(logId, "YD_TO_LOC_DCSN_MTD : " + recInCrn.getFieldString("YD_TO_LOC_DCSN_MTD"), "SL");
				
				recInCrn.setField("YD_EQP_WRK_SH", "" + rsBedLyr.size());	//작업재료매수
				
				//1.3. 크레인스케줄 생성
				intRtnVal = commDao.insert(recInCrn, insYdCrnsch, logId, methodNm, "크레인스케줄(TB_YS_CRNSCH) 등록");
				
				if( intRtnVal < 1 ) {
					commUtils.printLog(logId, "크레인 스케줄 등록중  Error!! ErrorCode: ", "SL");
				}
				
				sbImpPros.append("	 > 크레인스케줄 등록::YD_CRN_SCH_ID ("+ydCrnSchId+"), YD_EQP_ID ("+szEqpId+"), YD_SCH_CD ("+szSchCd+"), YD_SCH_PRIOR ("+szYD_SCH_PRIOR+"), YD_WBOOK_ID ("+szYD_WBOOK_DT+"), YS_UP_WO_LOC ("+recInCrn.getFieldString("YS_STK_COL_GP")+recInCrn.getFieldString("YS_STK_BED_NO")+"), YS_UP_WO_LAYER ("+recInCrn.getFieldString("YS_STK_LYR_NO")+") \r\n");
				
				
				/**********************************************************
				* 2. 권상위치 작업재료들의 재료상태를 "권상대기"로 변경: 적치단(TB_YS_STKLYR)
				**********************************************************/
				sbImpPros.append("	 > 권상위치작업재료들의 재료상태를 '권상대기'로 적치단(TB_YS_STKLYR) 변경::시작 \r\n");
				
				//조회된 각각의 작업재료정보
				recBedLyr = JDTORecordFactory.getInstance().create();
				//권상위치 작업재료들의 재료상태를 "권상대기"로 변경
				for( int Loop_k = 1; Loop_k <= rsBedLyr.size(); Loop_k++ ) {
					
					rsBedLyr.absolute(Loop_k);
					recBedLyr.setRecord( rsBedLyr.getRecord() );
					
					recBedLyr.setField("YD_STK_LYR_MTL_STAT", "U");	//야드적치단재료상태: [U:권상대기]
					
					intRtnVal = commDao.update(recBedLyr, updYdStkLyrMtlStat, logId, methodNm, "TB_YS_STKLYR 재료상태 변경");
					
					if( intRtnVal <= 0 ) {
						commUtils.printLog(logId, "[" + methodNm + "] 적치단[" + recInCrn.getFieldString("YS_STK_COL_GP") + "]활성화중 ERROR 발생", "SL");
						throw new Exception("오류:TB_YS_STKLYR 재료상태 변경시 오류 발생");
					}
				}

				sbImpPros.append("	 > 권상위치작업재료들의 재료상태를 '권상대기'로 적치단(TB_YS_STKLYR) 변경::종료 \r\n");
				
				
				/**********************************************************
				* 3. 크레인스케줄 작업재료 생성
				**********************************************************/
				JDTORecord recInCrnMtl = JDTORecordFactory.getInstance().create();
				
				if( recInCrn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("W") ) {	//TO위치결정방법: [M:이적] [W:보조]
					recInCrnMtl.setField("YD_AID_WRK_YN", "Y");	//보조작업여부  : [Y:보조작업] [N:주작업]
				} else {
					recInCrnMtl.setField("YD_AID_WRK_YN", "N");
				}
				recInCrnMtl.setField("YD_CRN_SCH_ID", ydCrnSchId);
				recInCrnMtl.setField("REGISTER"     , szName    );
				recInCrnMtl.setField("MOD_DDTT"     , ""        );
				
				//조회된 각각의 작업재료정보
				recBedLyr = JDTORecordFactory.getInstance().create();
				//크레인스케줄 작업재료 생성
				for( int Loop_k = 1; Loop_k <= rsBedLyr.size(); Loop_k++ ) {
					
					rsBedLyr.absolute(Loop_k);
					recBedLyr = rsBedLyr.getRecord();
					
					recInCrnMtl.setField("SSTL_NO"			 , commUtils.trim(recBedLyr.getFieldString("SSTL_NO"          )));
					recInCrnMtl.setField("YS_STK_LYR_NO"	 , recBedLyr.getFieldString("YS_STK_LYR_NO")                    );	//적치단번호        : 현 권상위치단 제공
					recInCrnMtl.setField("YS_STK_SEQ_NO"	 , ""+Loop_k                                                    );	//적치SEQ번호
					recInCrnMtl.setField("HCR_GP"			 , commUtils.trim(recBedLyr.getFieldString("HCR_GP"           )));	//HCR_GP            : TB_YS_STOCK에서 조회된 값 제공
					recInCrnMtl.setField("STL_PROG_CD"		 , commUtils.trim(recBedLyr.getFieldString("STL_PROG_CD"      )));	//재료진도코드      : TB_YS_STOCK에서 조회된 값 제공
					recInCrnMtl.setField("YS_MTL_ITEM"		 , commUtils.trim(recBedLyr.getFieldString("YS_MTL_ITEM"      )));	//특수강야드재료품목: TB_YS_STOCK에서 조회된 값 제공
					recInCrnMtl.setField("YD_TO_LOC_DCSN_MTD", commUtils.trim(recInCrn.getFieldString("YD_TO_LOC_DCSN_MTD")));	//TO위치결정방법    : [M:이적] [W:보조]
					
					intRtnVal = commDao.insert(recInCrnMtl, insYdCrnwrkmtl, logId, methodNm, "크레인스케줄 작업재료 등록");
					
					if( intRtnVal <= 0 ) {
						commUtils.printLog(logId, "크레인스케줄 작업재료 등록중 실패: " + intRtnVal, "SL");
						throw new Exception("오류:크레인스케줄 작업재료 등록중 오류 발생");
					}

					sbImpPros.append("	 > 크레인스케줄작업재료 등록 (순번:"+Loop_k+")::YD_CRN_SCH_ID ("+ydCrnSchId+"), SSTL_NO ("+commUtils.trim(recBedLyr.getFieldString("SSTL_NO"))+"), YS_STK_LYR_NO ("+recBedLyr.getFieldString("YS_STK_LYR_NO")+"), YS_STK_SEQ_NO ("+Loop_k+"), HCR_GP ("+commUtils.trim(recBedLyr.getFieldString("HCR_GP"))+"), STL_PROG_CD ("+commUtils.trim(recBedLyr.getFieldString("STL_PROG_CD"))+"), YS_MTL_ITEM ("+recInCrn.getFieldString("YS_MTL_ITEM")+"), YD_TO_LOC_DCSN_MTD ("+recInCrn.getFieldString("YD_TO_LOC_DCSN_MTD")+") \r\n");
					
				}	// End  크레인스케줄 작업재료 생성 for()

				sbImpPros.append("	[크레인스케줄 > 크레인스케줄,크레인작업재료등록 > 크레인스케줄생성] (순번:"+i+")::종료 \r\n");
				
			}	// End  그룹핑 파라미터 셋팅 (작업예약에 대한 크레인스케줄 생성정보 묶음) 개수 만큼 for()
			
			
			commUtils.printLog(logId, methodNm, "S-");
			
			return intRtnVal = 1;
			
		} catch (DAOException e ) {
			throw e;
		} catch (Exception e ) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
        
    }//end of CrnSchIns()
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 작업예약재료 저장위치 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void updCrnSchWB(JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인스케줄 작업예약재료 저장위치 수정[SbrYsSchSeEJB.updCrnSchWB] < " + jrParam.getResultMsg();
		String logId    = jrParam.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			commDao.update(jrParam, updWmStrLoc, logId, methodNm, "작업예약재료 저장위치 수정");

			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e ) {
			throw e;
		} catch (Exception e ) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
    
	/**
	 * 오퍼레이션명 : 크레인 스케줄 GROUPING PARAMETER DATA SETTING
	 *  
	 * @param  String     szEqpId, szSchCd, recMainPara, rsReturn
	 * @return boolean    intRtnVal 1: 성공, -1:실패
	 * @throws JDTOException
	 */
	public int CrnSchGrp(String logId, String methodNms ,JDTORecord recMainPara, JDTORecordSet rsReturn, StringBuffer sbImpPros)throws JDTOException  {
		String methodNm = "크레인 스케줄 GROUPING PARAMETER DATA SETTING[SbrYsSchSeEJB.CrnSchGrp] < " + methodNms;
		
		JDTORecord    recInPara    = null;
		JDTORecord    recPara      = null;
		JDTORecord    recStkLyr    = null;
		JDTORecordSet rsWrkbookCol = JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet rsResult     = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		int intHandlingCnt = 1;

		try {
			commUtils.printLog(logId, methodNm, "S+");

    		/**********************************************************
			* 1. 작업예약재료들의 적치열 내 최하단 위치정보 조회
			*    - #형 적치대일 경우: "열" 내 "BED"와는 상관없이 이적할 소재의 "단"을 기준으로
			*                         "열" 내 모든 "BED"의 상위 "단"들을 (보조작업)이적해야 함
			*                         따라서 경우에 따라 조회시 BED가 조건중에 빠짐
			*
			*     - 크레인작업 기준 : 1. 이적대상재 선택은 동일 "열" 내 모든 "BED/단"에 있는 소재들은 선택 가능함
			*                         2. 이적대상재 선택시 동일 "열/BED/단"에 있는 소재들은 1회 이적시 모두 이적됨
			*                            (# 화면에서 특정 소재를 선택하면 같은 단에 있는 소재들은 모두 선택되게 함)
			**********************************************************/
			sbImpPros.append("		[크레인스케줄 > 그룹핑파라미터셋팅 > 차량하차 외 > 작업예약재료들의 적치열 내 최하단 위치정보 조회]::시작 \r\n");
			
			rsWrkbookCol = commDao.select(recMainPara, getWorkBookColGpGroup, logId, methodNm, "작업예약재료들의 적치열 내 최하단 위치정보 조회");
			
			if( rsWrkbookCol.size() <= 0 ) {
				throw new Exception("오류:작업예약재료들의 적치열 내 최하단 위치정보 조회 >> 조회 Data 없음");
			}
						
			commUtils.printParam(logId, rsWrkbookCol);
			sbImpPros.append("		[크레인스케줄 > 그룹핑파라미터셋팅 > 차량하차 외 > 작업예약재료들의 적치열 내 최하단 위치정보 조회]::종료 \r\n");
			

			/**********************************************************
			* 2. 크레인작업단위('열/BED/단')별 "주작업/보조작업", "TO위치결정방법" 파라미터 설정
			*    - 크레인작업단위 == 크레인스케줄
			**********************************************************/
			sbImpPros.append("		[크레인스케줄 > 그룹핑파라미터셋팅 > 차량하차 외 > 크레인작업단위('열/BED/단')별'주작업/보조작업','TO위치결정방법'파라미터설정]::시작 \r\n");
			
			//이전 작업예약 등록시 "크레인사양 분리" 기준이 "열/BED/단"이기 때문에 조회되는 "열"은 하나만 존재함
			rsWrkbookCol.absolute(1);
			recPara = rsWrkbookCol.getRecord();
			
			//2.1. 크레인작업단위('열/BED/단')별 위치 및 정보 조회
			recInPara = JDTORecordFactory.getInstance().create();
			recInPara.setField("YD_WBOOK_ID",   recPara.getFieldString("YD_WBOOK_ID"  ));
			recInPara.setField("YS_STK_COL_GP", recPara.getFieldString("YS_STK_COL_GP"));
			recInPara.setField("YS_STK_LYR_NO", recPara.getFieldString("YS_STK_LYR_NO"));
			
			rsResult = commDao.select(recInPara, getStkLyrbyWBookIdGroupV1, logId, methodNm, "크레인작업단위('열/BED/단')별 위치 및 정보 조회");
			
			if( rsResult.size() <= 0 ) {
				throw new Exception("오류:크레인작업단위('열/BED/단')별 위치 및 정보 조회 >> 조회 Data 없음");
			}

			//2.2. 크레인작업단위('열/BED/단')별 스케줄생성용 파라미터 설정
			JDTORecord recInPara9 = JDTORecordFactory.getInstance().create();
    		
			for( int Loop_j = 1; Loop_j <= rsResult.size(); Loop_j++ ) {
				
    			rsResult.absolute(Loop_j);
    			recStkLyr = rsResult.getRecord();
    			
    			recStkLyr.setField("YD_WBOOK_ID", recPara.getFieldString("YD_WBOOK_ID"));
    			
    			String szYS_STK_COL_GP 	= commUtils.trim(recStkLyr.getFieldString("YS_STK_COL_GP"));	//From-적치열
    			String szYS_STK_BED_NO 	= commUtils.trim(recStkLyr.getFieldString("YS_STK_BED_NO"));	//From-적치BED
    			String szYS_STK_LYR_NO 	= commUtils.trim(recStkLyr.getFieldString("YS_STK_LYR_NO"));	//From-적치단
    			String szWRKBOOKMTL_CNT = commUtils.trim(recStkLyr.getFieldString("WRKBOOKMTL_CNT"));	//From-해당단에 작업예약매수
    			String szYD_WBOOK_ID 	= recPara.getFieldString("YD_WBOOK_ID");
    			
    			//2.2.1. "주작업" 이면...
    			if( commUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ") > 0 ) {
    				
					recInPara9 = JDTORecordFactory.getInstance().create();
					recInPara9.setField("HANDLING_CNT"		, ""+intHandlingCnt);
					intHandlingCnt++;
					recInPara9.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
					recInPara9.setField("YS_STK_BED_NO"		, szYS_STK_BED_NO);
					recInPara9.setField("YS_STK_LYR_NO"		, szYS_STK_LYR_NO);
					recInPara9.setField("WRKBOOKMTL_CNT"	, szWRKBOOKMTL_CNT);
					recInPara9.setField("WRK_SPR"			, "TARGET");			//[ALL:적치단의 모든 소재들을 이적],[TARGET:"작업예약"의 "작업재료"만 이적]
					recInPara9.setField("YD_WBOOK_ID"		, szYD_WBOOK_ID);
					recInPara9.setField("MAIN_WRK_YN"		, "Y");
					recInPara9.setField("YD_TO_LOC_DCSN_MTD", "M");					//야드To위치결정방법: [M:이적],[W:보조],[C:분리],[S:모음]
					recInPara9.setField("YD_UP_COLL_SEQ"	, ""+commUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ"));	//권상모음순서
					recInPara9.setField("TARGET_LOC"		, "YD" );
					
					rsReturn.addRecord(recInPara9);

					sbImpPros.append("		 > (차량하차 외)작업예약에 대한 크레인스케줄 생성정보 묶음 (주작업)   ["+Loop_j+"]::YS_STK_COL_GP ("+szYS_STK_COL_GP+"), YS_STK_BED_NO ("+szYS_STK_BED_NO+"), YS_STK_LYR_NO ("+szYS_STK_LYR_NO+"), WRKBOOKMTL_CNT ("+szWRKBOOKMTL_CNT+"), WRK_SPR (TARGET), YD_WBOOK_ID ("+szYD_WBOOK_ID+"), YD_TO_LOC_DCSN_MTD (M) \r\n");
				
    			//2.2.2. "보조작업" 이면...
    			} else {
    				
 					recInPara9 = JDTORecordFactory.getInstance().create();
					recInPara9.setField("HANDLING_CNT"		, ""+intHandlingCnt);
					intHandlingCnt++;
					recInPara9.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
					recInPara9.setField("YS_STK_BED_NO"		, szYS_STK_BED_NO);
					recInPara9.setField("YS_STK_LYR_NO"		, szYS_STK_LYR_NO);
					recInPara9.setField("WRKBOOKMTL_CNT"	, szWRKBOOKMTL_CNT);
					recInPara9.setField("WRK_SPR"			, "ALL");				//[ALL:적치단의 모든 소재들을 이적],[TARGET:"작업예약"의 "작업재료"만 이적]
					recInPara9.setField("YD_WBOOK_ID"		, szYD_WBOOK_ID);
					recInPara9.setField("MAIN_WRK_YN"       , "N");
    				recInPara9.setField("YD_TO_LOC_DCSN_MTD", "W");					//야드To위치결정방법: [M:이적],[W:보조],[C:분리],[S:모음]
    				recInPara9.setField("YD_UP_COLL_SEQ", ""+commUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ"));
					recInPara9.setField("TARGET_LOC"		, "YD" );
    				
    				rsReturn.addRecord(recInPara9);
    				
					sbImpPros.append("		 > (차량하차 외)작업예약에 대한 크레인스케줄 생성정보 묶음 (보조작업) ["+Loop_j+"]::YS_STK_COL_GP ("+szYS_STK_COL_GP+"), YS_STK_BED_NO ("+szYS_STK_BED_NO+"), YS_STK_LYR_NO ("+szYS_STK_LYR_NO+"), WRKBOOKMTL_CNT ("+szWRKBOOKMTL_CNT+"), WRK_SPR (ALL),    YD_WBOOK_ID ("+szYD_WBOOK_ID+"), YD_TO_LOC_DCSN_MTD (W) \r\n");
    			}
    			
    		}	//end of for

			sbImpPros.append("		[크레인스케줄 > 그룹핑파라미터셋팅 > 차량하차 외 > 크레인작업단위('열/BED/단')별'주작업/보조작업','TO위치결정방법'파라미터설정]::종료 \r\n");
			commUtils.printLog(logId, methodNm, "S-");			
			
		} catch (DAOException e ) {
			throw e;
		} catch (Exception e ) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return YsConstant.RETN_INT_SUCCESS;
	}
	
	/**
	 * 오퍼레이션명 : (차량하차) 크레인 스케줄 GROUPING PARAMETER DATA SETTING
	 *  
	 * @param  String     szEqpId, szSchCd, recMainPara, rsReturn
	 * @return boolean    intRtnVal 1: 성공, -1:실패
	 * @throws JDTOException
	 */
	public int CrnSchGrpTr(String logId, String methodNms ,JDTORecord recMainPara, JDTORecordSet rsReturn, StringBuffer sbImpPros)throws JDTOException  {
		String methodNm = "(차량하차) 크레인 스케줄 GROUPING PARAMETER DATA SETTING[SbrYsSchSeEJB.CrnSchGrpTr] < " + methodNms;
		
		JDTORecordSet rsSelBed     = JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecord    recInPara    = null;
		JDTORecord    recPara      = null;
		JDTORecord    recStkLyr    = null;
		JDTORecordSet rsResult     = JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet rsWrkbookCol = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		int intHandlingCnt = 1;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			/**********************************************************
			* 1. 작업예약재료들의 적치단('열/BED/단')정보 조회
			**********************************************************/
			sbImpPros.append("		[크레인스케줄 > 그룹핑파라미터셋팅 > 차량하차 > 작업예약재료들의적치단('열/BED/단')정보조회]::시작 \r\n");
			
			rsWrkbookCol = commDao.select(recMainPara, getWorkBookColGpBedGroupTr, logId, methodNm, "작업예약재료들의 적치단('열/BED/단')정보 조회");
			if( rsWrkbookCol.size() <= 0 ) {
				throw new Exception("오류:작업예약재료들의 적치단('열/BED/단')정보 조회 >> 조회 Data 없음");
			}
			
			for( int Loop_j = 1; Loop_j <= rsWrkbookCol.size(); Loop_j++ ) {
				rsWrkbookCol.absolute(Loop_j);
				rsSelBed.addRecord(rsWrkbookCol.getRecord());
			}
			
			commUtils.printParam(logId, rsSelBed);
			sbImpPros.append("		[크레인스케줄 > 그룹핑파라미터셋팅 > 차량하차 > 작업예약재료들의적치단('열/BED/단')정보조회]::종료 \r\n \r\n");
			
			
			/**********************************************************
			* 2. 조회된 작업예약재료들의 '열/BED/단' 개수 만큼 for( ) 실행하며
			*    각 재료의 레코드에 "주작업/보조작업", "TO위치결정방법" 파라미터 설정
			**********************************************************/
			sbImpPros.append("		[크레인스케줄 > 그룹핑파라미터셋팅 > 차량하차 > '주작업/보조작업','TO위치결정방법'파라미터설정]::시작 \r\n");
			
			for( int Loop_i = 1; Loop_i <= rsSelBed.size(); Loop_i++ ) {
				
				rsSelBed.absolute(Loop_i);  //적치Bed를 조회한다.
				recPara = rsSelBed.getRecord();
							
				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setField("YD_WBOOK_ID",   recPara.getFieldString("YD_WBOOK_ID"));
				recInPara.setField("YS_STK_COL_GP", recPara.getFieldString("YS_STK_COL_GP"));
				recInPara.setField("YS_STK_BED_NO", recPara.getFieldString("YS_STK_BED_NO"));
				recInPara.setField("YS_STK_LYR_NO", recPara.getFieldString("YS_STK_LYR_NO"));  
				
				rsResult = commDao.select(recInPara, getStkLyrbyWBookIdGroupTr, logId, methodNm, "조회된 적치단('열/BED/단') 상세정보 조회");
				
				if( rsResult.size() <= 0 ) {
					throw new Exception("오류:조회된 적치단('열/BED/단') 상세정보 조회 >> 조회 Data 없음");
				}					
				
				
				rsResult.absolute(1);
    			recStkLyr = rsResult.getRecord();
    			
    			recStkLyr.setField("YD_WBOOK_ID", recPara.getFieldString("YD_WBOOK_ID"));
    			
    			String szYS_STK_COL_GP 	= commUtils.trim(recStkLyr.getFieldString("YS_STK_COL_GP" ));	//적치열
    			String szYS_STK_BED_NO 	= commUtils.trim(recStkLyr.getFieldString("YS_STK_BED_NO" ));	//적치BED
    			String szYS_STK_LYR_NO 	= commUtils.trim(recStkLyr.getFieldString("YS_STK_LYR_NO" ));	//적치단
    			String szWRKBOOKMTL_CNT = commUtils.trim(recStkLyr.getFieldString("WRKBOOKMTL_CNT"));	//해당단에 작업예약매수
    			String szYD_WBOOK_ID 	= recPara.getFieldString("YD_WBOOK_ID");
	    		
    			//(차량하차)작업예약에 대한 크레인스케줄 생성정보 (JDTORecord): 차량->야드 이적
				JDTORecord recInPara9 = JDTORecordFactory.getInstance().create();
				recInPara9.setField("HANDLING_CNT"		, ""+intHandlingCnt);
				intHandlingCnt++;
				recInPara9.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP  );
				recInPara9.setField("YS_STK_BED_NO"		, szYS_STK_BED_NO  );
				recInPara9.setField("YS_STK_LYR_NO"		, szYS_STK_LYR_NO  );
				recInPara9.setField("WRKBOOKMTL_CNT"	, szWRKBOOKMTL_CNT );
				recInPara9.setField("WRK_SPR"			, "TARGET"         );											//[ALL:적치단의 모든 소재들을 이적],[TARGET:"작업예약"의 "작업재료"만 이적]
				recInPara9.setField("YD_WBOOK_ID"		, szYD_WBOOK_ID    );
				recInPara9.setField("MAIN_WRK_YN"		, "Y"              );
				recInPara9.setField("YD_TO_LOC_DCSN_MTD", "M"              );											//야드To위치결정방법: [M:이적],[W:보조],[C:분리],[S:모음]
				recInPara9.setField("YD_UP_COLL_SEQ"	, ""+commUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ"));	//권상모음순서
				recInPara9.setField("TARGET_LOC"		, "YD"             );
				
				//(차량하차)작업예약에 대한 크레인스케줄 생성정보 묶음 (JDTORecordSet)
				rsReturn.addRecord(recInPara9);
				
				sbImpPros.append("		 > (차량하차)작업예약에 대한 크레인스케줄 생성정보 묶음 ["+Loop_i+"]::YS_STK_COL_GP ("+szYS_STK_COL_GP+"), YS_STK_BED_NO ("+szYS_STK_BED_NO+"), YS_STK_LYR_NO ("+szYS_STK_LYR_NO+"), WRKBOOKMTL_CNT ("+szWRKBOOKMTL_CNT+"), WRK_SPR (TARGET), YD_WBOOK_ID ("+szYD_WBOOK_ID+"), YD_TO_LOC_DCSN_MTD (M) \r\n");
				
			}//end of for
			
			sbImpPros.append("		[크레인스케줄 > 그룹핑파라미터셋팅 > 차량하차 > '주작업/보조작업','TO위치결정방법'파라미터설정]::종료 \r\n");
			commUtils.printLog(logId, methodNm, "S-");			
			
		} catch (DAOException e ) {
			throw e;
		} catch (Exception e ) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return YsConstant.RETN_INT_SUCCESS;
	} 
	


	/**
	 * 보조작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procDummyToLoc(String logId, String methodNms, JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch, JDTORecord recWbook, StringBuffer sbImpPros) throws JDTOException {
    	String methodNm      = "보조작업TO위치결정[SbrYsSchSeEJB.procDummyToLoc] < " + methodNms;
    	String LocalmethodNm = "보조작업TO위치결정[SbrYsSchSeEJB.procDummyToLoc] "; 

		JDTORecordSet	rsResult		   = null;
		JDTORecordSet	rsStock			   = null;
		JDTORecord		recStock		   = null;
		JDTORecord		recPara			   = null;
		JDTORecord		recTemp			   = null;
		JDTORecord		recCrnwrkmtl	   = null;

		JDTORecordSet	outRsResult 	   = null;
		JDTORecord		outRecResult 	   = null;
		
		String			szYS_UP_WO_LOC     = "";
		String			szYS_UP_WO_LAYER   = null;
		String			szYS_DN_WO_LOC     = null;
		String			szYS_DN_WO_LAYER   = null;
		String			szYD_UP_STK_COL_GP = null;
		String			szYD_UP_STK_BED_NO = null;
		
		
		commUtils.printLog(logId, methodNm, "S+");
		
		
		/**********************************************************
		* 1. 크레인 작업재료정보
		**********************************************************/
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 보조작업 TO위치결정 > 크레인작업재료정보]::시작 \r\n");
		
		rsCrnwrkmtl.first();
		recCrnwrkmtl = rsCrnwrkmtl.getRecord();
		
		//크레인스케줄 정보
		String szYD_EQP_ID  	    = commUtils.trim(recCrnSch.getFieldString("YD_EQP_ID"                     ));	//크레인설비ID
		String szYD_SCH_PRIOR	    = commUtils.trim(recCrnSch.getFieldString("YD_SCH_PRIOR"                  ));	//분리작업시 필요
		String szYD_WBOOK_ID	    = commUtils.trim(recCrnSch.getFieldString("YD_WBOOK_ID"                   ));	//분리작업시 필요
		
		//크레인스케줄 작업재료 정보
		String szYD_CRN_SCH_ID 	    = commUtils.trim(recCrnwrkmtl.getFieldString("YD_CRN_SCH_ID"              ));	//크레인스케줄ID
		String szSSTL_NO 		    = commUtils.trim(recCrnwrkmtl.getFieldString("SSTL_NO"                    ));	//크레인작업재료
		String szYD_TO_LOC_DCSN_MTD	= commUtils.trim(recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD"            ));	//TO위치결정방법: [M:이적][W:보조]
		int intMTL_CNT 			    = Integer.parseInt(commUtils.nvl(recCrnwrkmtl.getFieldString("SH_CNT"),"0"));
		
		//작업예약 정보
		String szYD_SCH_CD 	        = commUtils.trim(recWbook.getFieldString("YD_SCH_CD"                      ));	//크레인스케줄코드
		
		commUtils.printParam(logId, recWbook); 
		
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 보조작업 TO위치결정 > 크레인작업재료정보]::종료 \r\n \r\n");
		

		/**********************************************************
		* 2. 야드 저장품정보 조회
		**********************************************************/
		commUtils.printLog(logId, "["+ LocalmethodNm +"] 크레인작업재료의 '단' 내 첫번째 재료정보["+szSSTL_NO+"]를 저장품(TB_YS_STOCK)에서 조회 시작 ", "SL");
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 보조작업 TO위치결정 > 야드저장품정보조회]::시작 \r\n");
		
		rsStock = commDao.select(recCrnwrkmtl, getYdStock, logId, methodNm, "저장품 조회");
		
		if( rsStock.size() <= 0 ) {
			commUtils.printLog(logId, "["+ LocalmethodNm +"]  재료정보["+szSSTL_NO+"]를 저장품에서 조회 실패 ", "SL");
			sbImpPros.append("			 > 저장품정보 조회::저장품에서 조회 실패: getYdStock \r\n");
			
			return YsConstant.RETN_CD_FAILURE ;  //"0";
		}
		
		rsStock.first();
		recStock = rsStock.getRecord();

		String szHEAT_NO   		= commUtils.trim(recStock.getFieldString("HEAT_NO"      ));		//HEAT_NO
		String szYD_MTL_L 	    = commUtils.trim(recStock.getFieldString("YD_MTL_L"     ));		//크레인작업 첫번째 재료의 길이
		String szYD_MTL_W 	    = commUtils.trim(recStock.getFieldString("YD_MTL_W"     ));		//크레인작업 첫번째 재료의 폭
		String szYD_MTL_T 	    = commUtils.trim(recStock.getFieldString("YD_MTL_T"     ));		//크레인작업 첫번째 재료의 두께
		String szSPEC_ABBSYM    = commUtils.trim(recStock.getFieldString("SPEC_ABBSYM"  ));		//규격약호 (강종)
		
		commUtils.printLog(logId, "["+ LocalmethodNm +"] 크레인작업재료의 '단' 내 첫번째 재료정보["+szSSTL_NO+"]를 저장품에서 조회 완료 - 두께["+szYD_MTL_T+"], 폭["+szYD_MTL_W+"], 길이["+szYD_MTL_L+"], HEAT_NO["+szHEAT_NO+"], SPEC_ABBSYM["+szSPEC_ABBSYM+"]", "SL");
		sbImpPros.append("			 > 저장품정보 조회::두께 ("+szYD_MTL_T+"), 폭 ("+szYD_MTL_W+"), 길이 ("+szYD_MTL_L+"), HEAT_NO ("+szHEAT_NO+"), SPEC_ABBSYM ("+szSPEC_ABBSYM+") \r\n");
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 보조작업 TO위치결정 > 야드저장품정보조회]::종료 \r\n \r\n");
		
		
		/**********************************************************
		* 3. "권상중 or 권하중"인 재료를 적치단으로부터 조회
		**********************************************************/
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 보조작업 TO위치결정 > '권상중or권하중'인 재료를 적치단으로부터 조회]::시작 \r\n");
		
		szYS_UP_WO_LOC   = commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LOC"  ));		
		szYS_UP_WO_LAYER = commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LAYER"));		
		
		//크레인스케줄 정보에 "권상지시위치"가 없을 경우
		if( szYS_UP_WO_LOC.equals("") ) {
			
			commUtils.printLog(logId, "["+ methodNm +"] 크레인작업재료의 최하단 재료정보["+szSSTL_NO+"]에 대한 권하 또는 권상위치 조회 시작 ", "SL");
			
			//"권하대기"중인 첫번째 작업재료의 적재위치정보 조회
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("SSTL_NO"            , szSSTL_NO);
			recPara.setField("YD_STK_LYR_MTL_STAT", "D");		//[D:권하대기]
			
			rsResult = commDao.select(recPara, getYdStklyrSTLNO, logId, methodNm, "적재위치정보 조회");
			
			if( rsResult.size() <= 0 ) {

				recPara.setField("YD_STK_LYR_MTL_STAT", "U");	//[U:권상대기]
				
				//"권상대기"중인 첫번째 작업재료의 적재위치정보 조회
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				rsResult = commDao.select(recPara, getYdStklyrSTLNO, logId, methodNm, "적재위치정보 조회");
				
				if( rsResult.size() <= 0 ) {
					commUtils.printLog(logId, "["+ LocalmethodNm +"] 적재위치정보 조회 실패 ", "SL");
					sbImpPros.append("			 > '권상중or권하중'인 재료를 적치단으로부터 조회::적재위치정보 조회 실패: getYdStklyrSTLNO \r\n");

					return YsConstant.RETN_CD_FAILURE;  //"0";
				}	
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			commUtils.printLog(logId, "["+ LocalmethodNm +"] 크레인작업재료의  단 최대SEQ 재료정보["+szSSTL_NO+"]에 대한 권하 또는 권상위치 조회 완료  ", "SL");
			
			//조회된 권상지시 "열", "BED", "위치", "단"
			szYD_UP_STK_COL_GP 		= recTemp.getFieldString("YS_STK_COL_GP");
			szYD_UP_STK_BED_NO 		= recTemp.getFieldString("YS_STK_BED_NO");
			szYS_UP_WO_LOC 			= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYS_UP_WO_LAYER 		= recTemp.getFieldString("YS_STK_LYR_NO");
			
			commUtils.printLog(logId, "["+ LocalmethodNm +"] 조회된 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단["+szYS_UP_WO_LAYER+"]", "SL");
			
		//크레인스케줄 정보에 "권상지시위치"가 있을 경우
		} else {
			
			//크레인스케줄 정보 -> 권상지시 "열", "BED"
			szYD_UP_STK_COL_GP = szYS_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYS_UP_WO_LOC.substring(6);
			
			commUtils.printLog(logId, "["+ LocalmethodNm +"] 크레인스케줄에 등록된 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단["+szYS_UP_WO_LAYER+"]", "SL");
		}

		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 보조작업 TO위치결정 > '권상중or권하중'인 재료를 적치단으로부터 조회]::종료 \r\n \r\n");
		
		
		/**********************************************************
		* 4. 크레인스케줄(좌표 등) 권상위치 정보 갱신
		**********************************************************/
		String szYD_EQP_WRK_MAX_W 	= commUtils.trim(recCrnwrkmtl.getFieldString("MAX_MTL_W"      ));	//크레인작업재료 중 최대 폭
		String szYD_EQP_WRK_MAX_L 	= commUtils.trim(recCrnwrkmtl.getFieldString("MAX_MTL_L"      ));	//크레인작업재료 중 최대 길이
		int intYD_EQP_WRK_SH    	= commUtils.paraRecChkNullInt(recCrnwrkmtl,"SH_CNT"            );	//크레인작업재료 총매수
		int intYD_EQP_WRK_WT    	= commUtils.paraRecChkNullInt(recCrnwrkmtl,"SUM_MTL_WT"        );	//크레인작업재료 총중량
		double dblYD_EQP_WRK_T     	= commUtils.paraRecChkNullDouble(recCrnwrkmtl,"SUM_MTL_T"      );	//크레인작업재료 총높이
		
		String szMODIFIER 			= commUtils.trim(recCrnwrkmtl.getFieldString("MODIFIER"       ));	//MODIFIER
		
		
		//4.1. 크레인스케줄(좌표 등) 권상위치 정보 생성 
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 보조작업 TO위치결정 > 크레인스케줄(좌표 등) 권상위치정보 갱신]::시작 \r\n");
		
		JDTORecord recUpCrnSch = JDTORecordFactory.getInstance().create();
		recUpCrnSch.setField("YD_CRN_SCH_ID"   , szYD_CRN_SCH_ID               );	//크레인스케줄ID
		recUpCrnSch.setField("YD_EQP_ID"       , szYD_EQP_ID                   );	//크레인설비ID
		
		//4.2. 권상BED 좌표 조회
		sbImpPros.append("			 > 권상BED좌표조회::시작 \r\n");
		
		JDTORecord recInBed= JDTORecordFactory.getInstance().create();
		recInBed.setField("YS_STK_COL_GP", szYS_UP_WO_LOC.substring(0, 6)); 		//권상지시위치-적치열
		recInBed.setField("YS_STK_BED_NO", szYS_UP_WO_LOC.substring(6)   );			//권상지시위치-적치BED
		recInBed.setField("YS_STK_LYR_NO", szYS_UP_WO_LAYER              );			//권상지시위치-적치단
		
		JDTORecordSet RsBedUpXy = commDao.select(recInBed, getStkBedXY, logId, methodNm, "권상BED 좌표 조회");
		
		if( RsBedUpXy.size() <= 0 ) {
			commUtils.printLog(logId, "["+ LocalmethodNm +"] 권상BED 좌표 조회 검색 실패", "SL");
			sbImpPros.append("			 > 권상BED 좌표 조회::권상BED 좌표 조회 검색 실패 \r\n");
		}
		
		RsBedUpXy.first();
		JDTORecord RecUpBedXy = RsBedUpXy.getRecord();

		sbImpPros.append("			 > 권상BED좌표조회::종료 \r\n");
		
		//4.3. 권상정보 생성
		recUpCrnSch.setField("YS_UP_WO_LOC"    , szYS_UP_WO_LOC                );	//권상지시위치
		recUpCrnSch.setField("YS_UP_WO_LAYER"  , szYS_UP_WO_LAYER              );	//권상지시단
		recUpCrnSch.setField("YD_UP_STK_COL_GP", szYS_UP_WO_LOC.substring(0, 6));	//권상지시위치-적치열
		recUpCrnSch.setField("YD_UP_STK_BED_NO", szYS_UP_WO_LOC.substring(6)   );	//권상지시위치-적치BED
		
		//짝수단
		if( commUtils.trim(RecUpBedXy.getFieldString("DAN_GP")).equals("0") ) {
			recUpCrnSch.setField("YD_UP_WO_LOC_XAXIS", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS1"   )));
			recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS1"   )));
			recUpCrnSch.setField("YD_UP_WO_LOC_ZAXIS", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_ZAXIS1"   )));
		//홀수단
		} else {
			recUpCrnSch.setField("YD_UP_WO_LOC_XAXIS", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS"    )));
			recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS"    )));
			recUpCrnSch.setField("YD_UP_WO_LOC_ZAXIS", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_ZAXIS"    )));
		}
		recUpCrnSch.setField("YD_UP_WO_XAXIS_GAP_MAX", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS_TOL")));
		recUpCrnSch.setField("YD_UP_WO_XAXIS_GAP_MIN", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS_TOL")));
		recUpCrnSch.setField("YD_UP_WO_YAXIS_GAP_MAX", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS_TOL")));
		recUpCrnSch.setField("YD_UP_WO_YAXIS_GAP_MIN", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS_TOL")));
		recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS1"   , "");							//야드권상지시Y축1
		recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS2"   , "");							//야드권상지시Y축2
		recUpCrnSch.setField("YD_UP_WO_ZAXIS_GAP_MAX", "");							//야드권상지시Z축오차최대
		recUpCrnSch.setField("YD_UP_WO_ZAXIS_GAP_MIN", "");							//야드권상지시Z축오차최소
		
		//4.4 기타
		recUpCrnSch.setField("YD_EQP_WRK_SH"   , String.valueOf(intYD_EQP_WRK_SH));	//크레인작업재료 총매수
		recUpCrnSch.setField("YD_EQP_WRK_WT"   , String.valueOf(intYD_EQP_WRK_WT));	//크레인작업재료 총중량
		recUpCrnSch.setField("YD_EQP_WRK_T"    , String.valueOf(dblYD_EQP_WRK_T ));	//크레인작업재료 총높이
		recUpCrnSch.setField("YD_EQP_WRK_MAX_W", szYD_EQP_WRK_MAX_W              );	//크레인작업재료 중 최대 폭
		recUpCrnSch.setField("YD_EQP_WRK_MAX_L", szYD_EQP_WRK_MAX_L              );	//크레인작업재료 중 최대 길이
		recUpCrnSch.setField("MODIFIER"        , szMODIFIER                      );
		
		//4.5 크레인스케줄(좌표 등) 권상위치 정보 갱신
		int intRtnVal = commDao.update(recUpCrnSch, updCrnschXY, logId, methodNm, "크레인스케줄(좌표 등) 권상위치 정보 갱신");
		
		if( intRtnVal <= 0 ) {
			commUtils.printLog(logId, "["+ LocalmethodNm +"] 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단[" +szYS_UP_WO_LAYER +" ] : 크레인스케줄(좌표 등) 권상위치정보 갱신 중 ERROR 발생", "SL");
			sbImpPros.append("			 > 크레인스케줄(좌표 등) 권상위치정보 갱신::크레인스케줄(좌표 등) 권상위치정보 갱신 중 ERROR 발생 szYS_DN_WO_LOC [" + szYS_UP_WO_LOC + "], szYS_DN_WO_LAYER [" + szYS_UP_WO_LAYER + "] \r\n");
			
			return YsConstant.RETN_CD_FAILURE;
		}
		
		commUtils.printLog(logId, "["+ LocalmethodNm +"] 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단["+szYS_UP_WO_LAYER+"] : 크레인스케줄(좌표 등) 권상위치정보 갱신 완료", "SL");
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 보조작업 TO위치결정 > 크레인스케줄(좌표 등) 권상위치정보 갱신]::종료 \r\n \r\n");
		
		
		/**********************************************************
		* 4. 동일한 사양의 적치가능한 "열"(+"Max-BED/Max-단") 조회 
		*    - 대형옥내-검색기준: "길이 > 두께(각) > Heat No > 강종" + 스케줄코드에 맞는 특정 적치대 구간에서만 검색
		*    - 빌렛소재-검색기준: 재료와 같은 "동일 HEAT", "동일 강종"의 적치가능한 베드 해당 동의 모든 위치를 조회
		**********************************************************/
		commUtils.printLog(logId, "["+ LocalmethodNm +"] TOSQL:["+szYD_CRN_SCH_ID+ "] 동일한사양  szHEAT_NO["+szHEAT_NO+"], szYD_MTL_T["+szYD_MTL_T+"], szYD_MTL_L["+szYD_MTL_L+"], szSPEC_ABBSYM["+szSPEC_ABBSYM+"], szYD_SCH_CD["+szYD_SCH_CD+"] 의 적치가능한 '열'(+'Max-BED/Max-단') 조회 시작", "SL");
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 보조작업 TO위치결정 > 동일한 사양의 적치가능한 '열' 조회]::시작 \r\n");
		sbImpPros.append("			 > 크레인작업재료&저장품 정보::szYD_TO_LOC_DCSN_MTD ("+szYD_TO_LOC_DCSN_MTD+"), szYD_SCH_CD ("+szYD_SCH_CD+"), szHEAT_NO ("+szHEAT_NO+"), szYD_MTL_T ("+szYD_MTL_T+"), szYD_MTL_L ("+szYD_MTL_L+"), SPEC_ABBSYM ("+szSPEC_ABBSYM+"), szYD_UP_STK_COL_GP ("+szYD_UP_STK_COL_GP+"), 크레인작업재료 매수 ("+intMTL_CNT+")  \r\n");
		
    	recTemp = JDTORecordFactory.getInstance().create();
    	
    	recTemp.setField("YD_TO_LOC_DCSN_MTD", szYD_TO_LOC_DCSN_MTD);					//TO위치결정방법: [M:이적][W:보조]
    	recTemp.setField("YD_SCH_CD", 	       szYD_SCH_CD         );					//크레인 스케줄코드
    	recTemp.setField("HEAT_NO", 	       szHEAT_NO           );					//크레인작업 최하단재료의 HEAT_NO
    	recTemp.setField("YD_MTL_T",           szYD_MTL_T          );					//크레인작업 최하단재료의 두께
    	recTemp.setField("YD_MTL_L",           szYD_MTL_L          );					//크레인작업 최하단재료의 길이
    	recTemp.setField("SPEC_ABBSYM",        szSPEC_ABBSYM       );					//규격약호 (강종)
    	recTemp.setField("YS_STK_COL_GP",      szYD_UP_STK_COL_GP);						//권상지시위치 - 적치열
    	
    	//4.1. 동일한 사양의 적치가능한 '열'(+'Max-BED/Max-단') 조회
		outRsResult = commDao.select(recTemp, getToLocCol, logId, methodNm, "동일한 사양의 적치가능한 '열'(+'Max-BED/Max-단') 조회");
		
		if( outRsResult.size() <= 0 ) {
			commUtils.printLog(logId, "["+ LocalmethodNm +"] 동일한 사양의 적치가능한 '열'(+'Max-BED/Max-단') 조회 실패 ", "SL");
			sbImpPros.append("			 > 동일한 사양의 적치가능한 '열' 조회::동일한 사양의 적치가능한 '열'(+'Max-BED/Max-단') 조회 실패: getToLocCol \r\n");
			
			return YsConstant.RETN_CD_FAILURE;
		}
		
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 보조작업 TO위치결정 > 동일한 사양의 적치가능한 '열' 조회]::종료 \r\n \r\n");
		
		
		/**********************************************************
		* 5. 동일한 사양의 적치가능한 "열" 단위로 "적치가능 여부 CEHCK"
		**********************************************************/
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 보조작업 TO위치결정 > 적치가능 여부 CEHCK]::시작 \r\n");
		
		JDTORecord  recResult   = null;
		String szLocYS_STK_COL_GP = "";
		String szLocYS_STK_BED_NO = "";
		
	    //5.1. 조회된 "동일한 사양의 적치가능한 열" 단위로 "적치가능 여부 CEHCK"
		for( int i = 1; i <= outRsResult.size(); i++ ) {

			outRsResult.absolute(i);
			outRecResult = outRsResult.getRecord();

			szLocYS_STK_COL_GP = commUtils.trim(outRecResult.getFieldString("YS_STK_COL_GP"  ));		
			szLocYS_STK_BED_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_BED_NO"  ));		

			commUtils.printLog(logId, "["+ LocalmethodNm +"] (순번:"+i+") TO위치["+ szLocYS_STK_COL_GP + szLocYS_STK_BED_NO + commUtils.trim(outRecResult.getFieldString("YS_STK_LYR_NO")) + commUtils.trim(outRecResult.getFieldString("YS_STK_SEQ_NO")) +"], TO위치를 검색한 쿼리[" + commUtils.trim(outRecResult.getFieldString("TO_LOC_POINT")) +"]", "SL");
			sbImpPros.append("			 > 동일한 사양의 적치가능한 '열'(+'Max-BED/Max-단') 정보::TO위치 ("+szLocYS_STK_COL_GP + szLocYS_STK_BED_NO + commUtils.trim(outRecResult.getFieldString("YS_STK_LYR_NO")) + commUtils.trim(outRecResult.getFieldString("YS_STK_SEQ_NO"))+") \r\n");
			
			JDTORecord recTemp1 = JDTORecordFactory.getInstance().create();
			recTemp1.setField("YD_UP_STK_COL_GP"  ,	szYD_UP_STK_COL_GP  );	
			recTemp1.setField("LOC_YS_STK_COL_GP" ,	szLocYS_STK_COL_GP  );	
			recTemp1.setField("LOC_YS_STK_BED_NO" ,	szLocYS_STK_BED_NO  );	
			recTemp1.setField("YD_TO_LOC_DCSN_MTD", szYD_TO_LOC_DCSN_MTD);	
			recTemp1.setField("MTL_CNT"           ,	""+intMTL_CNT       );	//크레인작업재료 매수
			recTemp1.setField("YD_SCH_CD"         , szYD_SCH_CD         );	//스케줄코드
			recTemp1.setField("YD_TO_LOC_GUIDE"   ,	""                  );	//TO위치가이드(사용자지정 TO위치)
			
			//적재가능 여부 CHECK
			sbImpPros.append("			 > 적재가능 여부 CHECK (순번:"+i+")::시작 \r\n");
			recResult = this.procLocAbleCheckCnt(logId, methodNms, recTemp1, sbImpPros);
			sbImpPros.append("			 > 적재가능 여부 CHECK (순번:"+i+")::종료 \r\n");
			
			if( commUtils.trim(recResult.getFieldString("ABLE_YN")).equals("Y") ) {
				szYS_DN_WO_LOC 	 = commUtils.trim(recResult.getFieldString("YS_DN_WO_LOC"  ));	//TO위치-권하지시위치		
				szYS_DN_WO_LAYER = commUtils.trim(recResult.getFieldString("YS_DN_WO_LAYER"));	//TO위치-권하지시단

				commUtils.printLog(logId, "["+ LocalmethodNm +"] 적재가능 여부 CHECK (순번:"+i+") 결과::TO위치 ("+szYS_DN_WO_LOC+szYS_DN_WO_LAYER+")", "SL");
				sbImpPros.append("			 > 적재가능 여부 CHECK (순번:"+i+") 결과::TO위치 ("+szYS_DN_WO_LOC+szYS_DN_WO_LAYER+") \r\n");
				break;
				
			} else {
				commUtils.printLog(logId, "["+ LocalmethodNm +"] 적재가능 여부 CHECK (순번:"+i+") 결과::continue~!!!", "SL");
				sbImpPros.append("			 > 적재가능 여부 CHECK (순번:"+i+") 결과::continue~!!! \r\n");
			}
		}
		
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 보조작업 TO위치결정 > 적치가능 여부 CEHCK]::종료 \r\n \r\n");

		
		/**********************************************************
		* 6. TO위치 UPDATE
		**********************************************************/
		JDTORecord RecSetLoc = JDTORecordFactory.getInstance().create();
		RecSetLoc.setField("YD_CRN_SCH_ID", 	 szYD_CRN_SCH_ID); 
		RecSetLoc.setField("YD_EQP_ID", 		 szYD_EQP_ID);	
		RecSetLoc.setField("YD_SCH_CD", 		 szYD_SCH_CD);	 
		RecSetLoc.setField("YD_WBOOK_ID", 		 szYD_WBOOK_ID);	 
		RecSetLoc.setField("YD_SCH_PRIOR", 		 szYD_SCH_PRIOR);	 
		RecSetLoc.setField("YS_UP_WO_LOC", 		 szYS_UP_WO_LOC); 
		RecSetLoc.setField("YS_UP_WO_LAYER",	 szYS_UP_WO_LAYER);	 
		RecSetLoc.setField("YS_DN_WO_LOC", 		 szYS_DN_WO_LOC);        
		RecSetLoc.setField("YS_DN_WO_LAYER", 	 szYS_DN_WO_LAYER);
		RecSetLoc.setField("YD_TO_LOC_DCSN_MTD", szYD_TO_LOC_DCSN_MTD);
		
		//TO위치 UPDATE
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 보조작업 TO위치결정 > TO위치UPDATE]::시작 \r\n");
		this.procUpdateLoc(logId, methodNms, recCrnwrkmtl, RecSetLoc, sbImpPros);
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 보조작업 TO위치결정 > TO위치UPDATE]::종료 \r\n");
		
		commUtils.printLog(logId, methodNm, "S-");
		
    	// ERROR 발생시 ?
		return YsConstant.RETN_CD_SUCCESS;
	}
	
    
	/**
	 * 설비-적재가능 여부 CHECK
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procLocAbleCheckCnt_Eqp(String logId, String methodNms,JDTORecord jrParamSet, StringBuffer sbImpPros) throws JDTOException {
		String methodNm      = "설비-적재가능 여부 CHECK[SbrYsSchSeEJB.procLocAbleCheckCnt_Eqp] < " + methodNms;
		String LocalmethodNm = "설비-적재가능 여부 CHECK[SbrYsSchSeEJB.procLocAbleCheckCnt_Eqp]";
		
		JDTORecordSet RsResultBed   = null;
		JDTORecord    RecResultBed  = null;
		JDTORecord	  recInBed      = null;
		
		String szYS_DN_WO_LOC       = "";
		String szYS_DN_WO_LAYER		= "";
		String szTEMP_YS_STK_COL_GP = "";
		String szTEMP_YS_STK_BED_NO = "";
		String szTEMP_YS_STK_LYR_NO = "";
		String szTEMP_STL_CNT 	    = "";
		
		String szYD_SCH_CD 		    = commUtils.trim(jrParamSet.getFieldString("YD_SCH_CD" ));
		String sYdLoc 				= commUtils.trim(jrParamSet.getFieldString("LOC_YS_STK_COL_GP" ));

		int    iCRNWRKMTL_CNT       = Integer.parseInt(commUtils.nvl(jrParamSet.getFieldString("MTL_CNT"),"0"));	//크레인작업재료 매수
		int    iTEMP_STL_CNT        = 0;																			//TO위치-적치된 재료매수: 야드적치단재료상태 == [C:적치중]or[D:권하대기]
		
		commUtils.printParam(logId, jrParamSet);
		
		
		/**********************************************************
		* 1. TO위치(시설 "열/'01'BED/'01'단")에 존재하는 재료 매수 조회한 후
		*    "적치가능여부 CEHCK" & "TO위치 결정"
		**********************************************************/
		commUtils.printLog(logId, methodNm, "S+");
		
		JDTORecord RecRtn = JDTORecordFactory.getInstance().create();
		RecRtn.setField("ABLE_DCSN_MTD",   "Y");		//기본
		
		recInBed= JDTORecordFactory.getInstance().create();
		recInBed.setField("YS_STK_COL_GP", sYdLoc);		//TO위치-열
		
    	RsResultBed = commDao.select(recInBed, getYdStkBedInfo, logId, methodNm, "TO위치(시설 '열/'01'BED/01단') 재료 매수 조회");
    	
		if( RsResultBed.size() <= 0 ) {
			commUtils.printLog(logId, "["+ LocalmethodNm +"] TO위치(시설 '열/'01'BED/01단')에 존재하는 재료 매수 조회 실패 ", "SL");
			sbImpPros.append("					 > 설비-적재가능 여부 CHECK::TO위치(시설 '열/'01'BED/01단') 재료 매수 조회::(불가) 조회 실패: getYdStkBedInfo \r\n");
			
			RecRtn.setField("ABLE_YN", "N");			//불가
			return RecRtn;
		}
		
		//TO위치(열)에 대해  조회된 "BED/Max-단"의 BED별 재료 매수로 BED별 "적치가능여부 CEHCK" & "TO위치 결정"
		for( int j = 1; j <= RsResultBed.size(); j++ ) {
			
			RsResultBed.absolute(j);
			RecResultBed = RsResultBed.getRecord();
			
			szTEMP_YS_STK_COL_GP = commUtils.trim(RecResultBed.getFieldString("YS_STK_COL_GP"  ));	//TO위치-적치열
			szTEMP_YS_STK_BED_NO = commUtils.trim(RecResultBed.getFieldString("YS_STK_BED_NO"  ));	//TO위치-적치BED
			szTEMP_YS_STK_LYR_NO = commUtils.trim(RecResultBed.getFieldString("YS_STK_LYR_NO"  ));	//TO위치-적치단
			szTEMP_STL_CNT       = commUtils.trim(RecResultBed.getFieldString("STL_CNT"        ));	//TO위치-적치된 재료매수: 야드적치단재료상태 == [C:적치중]or[D:권하대기]
			iTEMP_STL_CNT        = Integer.parseInt(commUtils.nvl(szTEMP_STL_CNT,"0"));				//TO위치-적치된 재료매수: 야드적치단재료상태 == [C:적치중]or[D:권하대기]
			
			//"BED/Max-단"의 해당 BED가 [(GDTF11 12 21 22 (정렬대)) && (크레인작업재료 매수 + TO위치-적치된 재료매수 <= 5)] 일 경우 TO위치로 결정
			if( (constant.SCH_CD_GDTF11MM.equals(szYD_SCH_CD) || constant.SCH_CD_GDTF12MM.equals(szYD_SCH_CD) || constant.SCH_CD_GDTF21MM.equals(szYD_SCH_CD) || constant.SCH_CD_GDTF22MM.equals(szYD_SCH_CD)) 
				&& (iCRNWRKMTL_CNT + iTEMP_STL_CNT <= 5) ) {
				szYS_DN_WO_LOC 	 = szTEMP_YS_STK_COL_GP + szTEMP_YS_STK_BED_NO;
				szYS_DN_WO_LAYER = szTEMP_YS_STK_LYR_NO;

				commUtils.printLog(logId, "["+ LocalmethodNm +"] TO 위치 가이드 처리 #위치 결정1::szYS_DN_WO_LOC:"+szYS_DN_WO_LOC+"szYS_DN_WO_LAYER:"+szYS_DN_WO_LAYER, "SL");
				sbImpPros.append("					 > 설비-적재가능 여부 CHECK::'TO위치 결정1::[GDTF11 12 21 22 (정렬대)]::(가능)['TO위치-적치된 재료매수'+'크레인작업재료 매수' <= 5] 조건 충족됨. szYS_DN_WO_LOC:"+szYS_DN_WO_LOC+"szYS_DN_WO_LAYER:"+szYS_DN_WO_LAYER+"\r\n");
				break;
				
			//"BED/Max-단"의 해당 BED가 [(GDTC04 05 06 (대차포인트)) && (크레인작업재료 매수 + TO위치-적치된 재료매수 <= 3)] 일 경우 TO위치로 결정
			} else if( (constant.SCH_CD_GDTC04UM.equals(szYD_SCH_CD) || constant.SCH_CD_GDTC05UM.equals(szYD_SCH_CD) || constant.SCH_CD_GDTC06UM.equals(szYD_SCH_CD)) 
				&& (iCRNWRKMTL_CNT + iTEMP_STL_CNT <= 3) ) {
				szYS_DN_WO_LOC 	 = szTEMP_YS_STK_COL_GP + szTEMP_YS_STK_BED_NO;
				szYS_DN_WO_LAYER = szTEMP_YS_STK_LYR_NO;

				commUtils.printLog(logId, "["+ LocalmethodNm +"] TO 위치 가이드 처리 #위치 결정1::szYS_DN_WO_LOC:"+szYS_DN_WO_LOC+"szYS_DN_WO_LAYER:"+szYS_DN_WO_LAYER, "SL");
				sbImpPros.append("					 > 설비-적재가능 여부 CHECK::'TO위치 결정2::[GDTC04 05 06 (대차포인트)]::(가능)['TO위치-적치된 재료매수'+'크레인작업재료 매수' <= 3] 조건 충족됨. szYS_DN_WO_LOC:"+szYS_DN_WO_LOC+"szYS_DN_WO_LAYER:"+szYS_DN_WO_LAYER+"\r\n");
				break;
				
			//"BED/Max-단"의 해당 BED의 적치된 재료가 없을 경우 TO위치로 결정
	        } else if( "0".equals(szTEMP_STL_CNT) ) {
				szYS_DN_WO_LOC 	 = szTEMP_YS_STK_COL_GP + szTEMP_YS_STK_BED_NO;
				szYS_DN_WO_LAYER = szTEMP_YS_STK_LYR_NO;

	        	commUtils.printLog(logId, "["+ LocalmethodNm +"] TO 위치 가이드 처리 #위치 결정2::szYS_DN_WO_LOC:"+szYS_DN_WO_LOC+"szYS_DN_WO_LAYER:"+szYS_DN_WO_LAYER, "SL");
				sbImpPros.append("					 > 설비-적재가능 여부 CHECK::'TO위치 결정3::[적치된 재료가 없을 경우]::(가능)['TO위치-적치된 재료매수' == 0] 조건 충족됨. szYS_DN_WO_LOC:"+szYS_DN_WO_LOC+"szYS_DN_WO_LAYER:"+szYS_DN_WO_LAYER+"\r\n");
				break;
	        }
		}
		
        //적치 가능여부 결정
        if( !"".equals(szYS_DN_WO_LOC) ) {
        	RecRtn.setField("ABLE_YN", "Y");	//적치가능
        } else {
        	RecRtn.setField("ABLE_YN", "N");	//적치불가
        }
        
		RecRtn.setField("YS_DN_WO_LOC",   szYS_DN_WO_LOC);		//TO위치-권하지시위치
		RecRtn.setField("YS_DN_WO_LAYER", szYS_DN_WO_LAYER);	//TO위치-권하지시단
		
		commUtils.printParam(logId, RecRtn);
		commUtils.printLog(logId, methodNm, "S-");
		
		return RecRtn;
	}
	
	
	/**
	 * 차량-적재가능 여부 CHECK
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procLocAbleCheckCnt_Car(String logId, String methodNms,JDTORecord jrParamSet, StringBuffer sbImpPros) throws JDTOException {
		String methodNm      = "차량-적재가능 여부 CHECK[SbrYsSchSeEJB.procLocAbleCheckCnt] < " + methodNms;
		String LocalmethodNm = "차량-적재가능 여부 CHECK[SbrYsSchSeEJB.procLocAbleCheckCnt]";
		
		JDTORecordSet RsResultBed   = null;
		JDTORecord    RecResultBed  = null;
		JDTORecord	  recInBed      = null;
		
		String szYS_DN_WO_LOC       = "";
		String szYS_DN_WO_LAYER		= "";
		
		String sYdLoc 				= commUtils.trim(jrParamSet.getFieldString("LOC_YS_STK_COL_GP"));
		String szYD_WBOOK_ID        = commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_ID"      ));
		String szYD_CRN_SCH_ID      = commUtils.trim(jrParamSet.getFieldString("YD_CRN_SCH_ID"    ));
		String szMODIFIER           = commUtils.trim(jrParamSet.getFieldString("MODIFIER"         ));
		
		commUtils.printParam(logId, jrParamSet);
		
		
		/**********************************************************
		* 1. TO위치(차량 "BED/'01'단")에 존재하는 재료 매수 조회한 후
		*    "적치가능여부 CEHCK" & "TO위치 결정"
		**********************************************************/
		commUtils.printLog(logId, methodNm, "S+");
		
		JDTORecord RecRtn = JDTORecordFactory.getInstance().create();
		RecRtn.setField("ABLE_DCSN_MTD",   "Y");		//기본
		
		recInBed= JDTORecordFactory.getInstance().create();
		recInBed.setField("YS_STK_COL_GP", sYdLoc);		//TO위치-열
		
    	RsResultBed = commDao.select(recInBed, getYdStkBedInfo, logId, methodNm, "TO위치(차량 'BED/01단') 재료 매수 조회");
    	
		if( RsResultBed.size() <= 0 ) {
			commUtils.printLog(logId, "["+ LocalmethodNm +"] TO위치(차량 '열/'01'BED/01단')에 존재하는 재료 매수 조회 실패 ", "SL");
			sbImpPros.append("					 > 차량-적재가능 여부 CHECK::TO위치(차량 'BED/01단') 재료 매수 조회::(불가) 조회 실패: getYdStkBedInfo \r\n");
			
			RecRtn.setField("ABLE_YN", "N");			//불가
			return RecRtn;
		}
		
		int iCRNWRKMTL_CNT = Integer.parseInt(commUtils.nvl(jrParamSet.getFieldString("MTL_CNT"),"0"));		//크레인작업재료 매수
		
		//TO위치(차량) 2BED에 재료 존재유무 확인
		RsResultBed.absolute(2);
		RecResultBed = RsResultBed.getRecord();
		int iTEMP_STR_CNT_2BED = Integer.parseInt(commUtils.nvl(RecResultBed.getFieldString("STL_CNT"),"0"));

		commUtils.printLog(logId, "procLocAbleCheckCnt_Car :: TO위치-[2BED]-적치된 재료매수 ["+ iTEMP_STR_CNT_2BED +"]  ", "SL");
		sbImpPros.append("					 > 차량-적재가능 여부 CHECK::TO위치(차량) 2BED에 재료 존재유무 확인:: TO위치-[2BED]-적치된 재료매수 ("+ iTEMP_STR_CNT_2BED +") \r\n");
		
		//TO위치(열)에 대해  조회된 "BED/Max-단"의 BED별 재료 매수로 BED별 "적치가능여부 CEHCK" & "TO위치 결정"
		for( int j = 1; j <= RsResultBed.size(); j++ ) {
			
			RsResultBed.absolute(j);
			RecResultBed = RsResultBed.getRecord();
			
			//TO위치(차량) 1BED에 상차 가능여부 확인전에, 2BED에 재료가 존재하면 1BED 상차 안함 
			if( j == 1 && iTEMP_STR_CNT_2BED > 0) {
				continue;
			}
			
			int iTEMP_STL_CNT = Integer.parseInt(commUtils.nvl(RecResultBed.getFieldString("STL_CNT"),"0"));	//TO위치-적치된 재료매수: 야드적치단재료상태 == [C:적치중]or[D:권하대기]
			
			commUtils.printLog(logId, "procLocAbleCheckCnt_Car :: TO위치-["+j+"BED]-적치된 재료매수 ["+ iTEMP_STL_CNT +"]  ", "SL");
			sbImpPros.append("					 > 차량-적재가능 여부 CHECK::TO위치-("+j+"BED)-적치된 재료매수::"+ iTEMP_STL_CNT +" \r\n");
			
			if( (iTEMP_STL_CNT + iCRNWRKMTL_CNT) <= 6 ) {	//6: 차량 1BED 1단 최대 매수
				szYS_DN_WO_LOC 	 = sYdLoc + "0" + j;
				szYS_DN_WO_LAYER = "01";

				commUtils.printLog(logId, "["+ LocalmethodNm +"] TO위치(차량 '열')에 'TO위치-적치된 재료매수'+'크레인작업재료 매수' <= 6 조건 충족됨", "SL");
				sbImpPros.append("					 > 차량-적재가능 여부 CHECK::'TO위치 결정1::(가능)TO위치(차량 '열')에 ['TO위치-적치된 재료매수'+'크레인작업재료 매수' <= 6] 조건 충족됨. szYS_DN_WO_LOC ("+szYS_DN_WO_LOC+"), szYS_DN_WO_LAYER ("+szYS_DN_WO_LAYER+") \r\n");
				
				break;
			}
		}
		
		commUtils.printLog(logId, "procLocAbleCheckCnt_Car :: szYS_DN_WO_LOC ["+ szYS_DN_WO_LOC +"]  ", "SL");
		sbImpPros.append("					 > 차량-적재가능 여부 CHECK::적치 가능여부 결정 최종정보::szYS_DN_WO_LOC ("+ szYS_DN_WO_LOC +") \r\n");
		
        //적치 가능여부 결정
        if( !"".equals(szYS_DN_WO_LOC) ) {
        	RecRtn.setField("ABLE_YN", "Y");	//적치가능
        } else {
        	RecRtn.setField("ABLE_YN", "N");	//적치불가
        	sbImpPros.append("					 > 차량-적재가능 여부 CHECK::적치 가능여부 결정::(불가)예외상황 확인 필요함~!!! \r\n");
        	
        	//차량상차 불가시 작업예약(유지), 크레인스케줄(삭제) 적용여부
        	String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "003");
        	if( "Y".equals(sRuleItem) ) {
	        	sbImpPros.append("					 > 차량-적재가능 여부 CHECK::차량상차 불가시 작업예약(유지), 크레인스케줄(삭제)::시작 \r\n");
	        	
	        	//1. 크레인스케줄 작업재료 조회
	    		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
	    		jrParam.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);	//크레인스케줄ID
	
				sbImpPros.append("					 > 차량-적재가능 여부 CHECK::차량상차 불가시 작업예약(유지), 크레인스케줄(삭제)::szYD_CRN_SCH_ID ("+szYD_CRN_SCH_ID+") \r\n");
				
	    		JDTORecordSet rsCrnwrkmtl = commDao.select(jrParam, getYdCrnwrkmtlBySchId, logId, methodNm, "크레인스케줄 작업재료 조회"); 
	    		
	    		if( rsCrnwrkmtl.size() <= 0 ) {
					commUtils.printLog(logId, "procLocAbleCheckCnt_Car :: 차량상차 불가시 작업예약(유지), 크레인스케줄(삭제) >> 크레인스케줄 작업재료 조회 실패!!", "SL");
					sbImpPros.append("					 > 차량-적재가능 여부 CHECK::차량상차 불가시 작업예약(유지), 크레인스케줄(삭제)::크레인스케줄 작업재료 조회 실패!! \r\n");
	    		}
	    		
	    		//2. 권상위치 작업재료들의 재료상태를 "적치중"로 변경: 적치단(TB_YS_STKLYR)
				try {
					JDTORecord recCrnwrkmtl = JDTORecordFactory.getInstance().create();
					for( int Loop_k = 1; Loop_k <= rsCrnwrkmtl.size(); Loop_k++ ) {
						
						rsCrnwrkmtl.absolute(Loop_k);
						recCrnwrkmtl = rsCrnwrkmtl.getRecord();
						
						String sYS_STK_COL_GP = commUtils.trim(recCrnwrkmtl.getFieldString("YS_UP_WO_LOC")).substring(0, 6);
						String sYS_STK_BED_NO = commUtils.trim(recCrnwrkmtl.getFieldString("YS_UP_WO_LOC")).substring(6);
						String sYS_STK_LYR_NO = commUtils.trim(recCrnwrkmtl.getFieldString("YS_UP_WO_LAYER"));
						String sSSTL_NO       = commUtils.trim(recCrnwrkmtl.getFieldString("SSTL_NO"));
						
						commUtils.printLog(logId, "procLocAbleCheckCnt_Car :: 차량상차 불가시 작업예약(유지), 크레인스케줄(삭제) >> procLocAbleCheckCnt_Car :: szMODIFIER["+szMODIFIER+"], sYS_STK_COL_GP["+sYS_STK_COL_GP+"], sYS_STK_COL_GP["+sYS_STK_BED_NO+"], sYS_STK_COL_GP["+sYS_STK_LYR_NO+"], sYS_STK_COL_GP["+sSSTL_NO+"]", "SL");
						sbImpPros.append("					 > 차량-적재가능 여부 CHECK::차량상차 불가시 작업예약(유지), 크레인스케줄(삭제)::szMODIFIER ("+szMODIFIER+"), sYS_STK_COL_GP ("+sYS_STK_COL_GP+"), sYS_STK_COL_GP ("+sYS_STK_BED_NO+"), sYS_STK_COL_GP ("+sYS_STK_LYR_NO+"), sYS_STK_COL_GP ("+sSSTL_NO+") \r\n");
						
						recCrnwrkmtl.setField("MODIFIER",            szMODIFIER    );	//
						recCrnwrkmtl.setField("YD_STK_LYR_MTL_STAT", "C"           );	//야드적치단재료상태: [U:권상대기 -> C:적치중]
						recCrnwrkmtl.setField("YS_STK_COL_GP",       sYS_STK_COL_GP);	//야드적치열구분(야드/동/스판/열)
						recCrnwrkmtl.setField("YS_STK_BED_NO",       sYS_STK_BED_NO);	//야드적치BED
						recCrnwrkmtl.setField("YS_STK_LYR_NO",       sYS_STK_LYR_NO);	//야드적치단
						recCrnwrkmtl.setField("SSTL_NO",             sSSTL_NO      );	//야드적치재료
						
						int intRtnVal = commDao.update(recCrnwrkmtl, updYdStkLyrMtlStat, logId, methodNm, "TB_YS_STKLYR 재료상태 변경");
						
						if( intRtnVal <= 0 ) {
							commUtils.printLog(logId, "[" + methodNm + "] 차량상차 불가시 작업예약(유지), 크레인스케줄(삭제) >> TB_YS_STKLYR 재료상태 변경중 ERROR 발생", "SL");
							sbImpPros.append("					 > 차량-적재가능 여부 CHECK::차량상차 불가시 작업예약(유지), 크레인스케줄(삭제)::TB_YS_STKLYR 재료상태 변경중 ERROR 발생!! \r\n");
							throw new Exception("procLocAbleCheckCnt_Car :: 차량상차 불가시 작업예약(유지), 크레인스케줄(삭제) >> TB_YS_STKLYR 재료상태 변경중 ERROR 발생!!");
						}
					}
				} catch (DAOException e ) {
					throw e;
				} catch (Exception e ) {
					throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
				}
	
				jrParam.setField("YD_WBOOK_ID", szYD_WBOOK_ID);	//크레인작업예약ID
				jrParam.setField("MODIFIER",    szMODIFIER   );	//
				
				commUtils.printLog(logId, "procLocAbleCheckCnt_Car :: 차량상차 불가시 작업예약(유지), 크레인스케줄(삭제) >> procLocAbleCheckCnt_Car :: szYD_CRN_SCH_ID["+szYD_CRN_SCH_ID+"], szYD_WBOOK_ID["+szYD_WBOOK_ID+"], szMODIFIER["+szMODIFIER+"]", "SL");
				sbImpPros.append("					 > 차량-적재가능 여부 CHECK::차량상차 불가시 작업예약(유지), 크레인스케줄(삭제)::szYD_CRN_SCH_ID ("+szYD_CRN_SCH_ID+"), szYD_WBOOK_ID ("+szYD_WBOOK_ID+"), szMODIFIER ("+szMODIFIER+") \r\n");
				
				//3. 크레인스케줄 작업재료 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtSCCrnMtl", logId, methodNm, "크레인스케줄 작업재료 삭제");
				
				commUtils.printLog(logId, "procLocAbleCheckCnt_Car :: 차량상차 불가시 작업예약(유지), 크레인스케줄(삭제) >> procLocAbleCheckCnt_Car :: 크레인스케줄 작업재료 삭제 완료", "SL");
				sbImpPros.append("					 > 차량-적재가능 여부 CHECK::차량상차 불가시 작업예약(유지), 크레인스케줄(삭제)::크레인스케줄 작업재료 삭제 완료 \r\n");
				
				//4. 크레인스케줄 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtSCCrnSch", logId, methodNm, "크레인스케줄 삭제");
				
				commUtils.printLog(logId, "procLocAbleCheckCnt_Car :: 차량상차 불가시 작업예약(유지), 크레인스케줄(삭제) >> procLocAbleCheckCnt_Car :: 크레인스케줄 삭제 완료", "SL");
				sbImpPros.append("					 > 차량-적재가능 여부 CHECK::차량상차 불가시 작업예약(유지), 크레인스케줄(삭제)::크레인스케줄 삭제 완료 \r\n");
				sbImpPros.append("					 > 차량-적재가능 여부 CHECK::차량상차 불가시 작업예약(유지), 크레인스케줄(삭제)::종료 \r\n");
        	}
        }
        
		RecRtn.setField("YS_DN_WO_LOC",   szYS_DN_WO_LOC);		//TO위치-권하지시위치
		RecRtn.setField("YS_DN_WO_LAYER", szYS_DN_WO_LAYER);	//TO위치-권하지시단
		
		commUtils.printParam(logId, RecRtn);
		commUtils.printLog(logId, methodNm, "S-");
		
		return RecRtn;
	}
	
	
	/**
	 * 적재가능 여부 CHECK
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procLocAbleCheckCnt(String logId, String methodNms,JDTORecord jrParamSet, StringBuffer sbImpPros) throws JDTOException {
		String methodNm      = "적재가능 여부 CHECK[SbrYsSchSeEJB.procLocAbleCheckCnt] < " + methodNms;
		String LocalmethodNm = "적재가능 여부 CHECK[SbrYsSchSeEJB.procLocAbleCheckCnt]";
		
		JDTORecordSet RsResultBed   = null;
		JDTORecord    RecResultBed  = null;
		JDTORecord	  recInBed      = null;
		
		String szYS_DN_WO_LOC       = "";
		String szYS_DN_WO_LAYER		= "";
		String szTEMP_YS_STK_COL_GP = "";
		String szTEMP_YS_STK_BED_NO = "";
		String szTEMP_YS_STK_LYR_NO = "";
		String szTEMP_STL_CNT 	    = "";
		
		String sYdLoc 				= commUtils.trim(jrParamSet.getFieldString("LOC_YS_STK_COL_GP" ));	//TO위치-열
		String sYD_TO_LOC_GUIDE     = commUtils.trim(jrParamSet.getFieldString("YD_TO_LOC_GUIDE"   ));	//TO위치가이드(사용자지정 TO위치)

		int    iCRNWRKMTL_CNT       = Integer.parseInt(commUtils.nvl(jrParamSet.getFieldString("MTL_CNT"),"0"));	//크레인작업재료 매수
		int    iTEMP_STL_CNT        = 0;																			//TO위치-적치된 재료매수
		int    iYD_STK_BED_LYR_MAX	= 0;		

		commUtils.printParam(logId, jrParamSet);
		
		
		/**********************************************************
		* 1. TO위치(열)의 모든 BED들을 대상으로 "Max단"에 존재하는 재료 매수 조회한 후
		*    "적치가능여부 CEHCK" & "TO위치 결정"
		**********************************************************/
		commUtils.printLog(logId, methodNm, "S+");
		
		JDTORecord RecRtn = JDTORecordFactory.getInstance().create();
		RecRtn.setField("ABLE_DCSN_MTD",   "Y");		//기본
		
		recInBed= JDTORecordFactory.getInstance().create();
		recInBed.setField("YS_STK_COL_GP", sYdLoc);		//TO위치-열
		
    	RsResultBed = commDao.select(recInBed, getYdStkBedInfo, logId, methodNm, "TO위치(열)의 모든 BED들을 대상으로 'Max단'에 존재하는 BED별 재료 매수 조회");
    	
		if( RsResultBed.size() <= 0 ) {
			commUtils.printLog(logId, "["+ LocalmethodNm +"] TO위치(열)의 모든 BED들을 대상으로 'Max단'에 존재하는 BED별 재료 매수 조회 실패 ", "SL");
			sbImpPros.append("					 > 적재가능 여부 CHECK::TO위치(열)의 모든 BED들을 대상으로 'Max단'에 존재하는 BED별 재료 매수 조회::(불가) 조회 실패: getYdStkBedInfo \r\n");
			
			RecRtn.setField("ABLE_YN", "N");			//불가
			return RecRtn;
		}
		
		commUtils.printLog(logId, "적치가능여부 CEHCK TO위치::sYdLoc [" +  sYdLoc + "], sYD_TO_LOC_GUIDE [" + sYD_TO_LOC_GUIDE + "]", "SL");
		sbImpPros.append("					 > 적재가능 여부 CHECK::TO위치::sYdLoc ("+sYdLoc+"), sYD_TO_LOC_GUIDE ("+sYD_TO_LOC_GUIDE+") \r\n");
		
		//TO위치(열)에 대해  조회된 "BED/Max-단"의 BED별 재료 매수로 BED별 "적치가능여부 CEHCK" & "TO위치 결정"
		for( int j = 1; j <= RsResultBed.size(); j++ ) {
			
			RsResultBed.absolute(j);
			RecResultBed = RsResultBed.getRecord();
			
			szTEMP_YS_STK_COL_GP = commUtils.trim(RecResultBed.getFieldString("YS_STK_COL_GP"  ));	//TO위치-적치열
			szTEMP_YS_STK_BED_NO = commUtils.trim(RecResultBed.getFieldString("YS_STK_BED_NO"  ));	//TO위치-적치BED
			szTEMP_YS_STK_LYR_NO = commUtils.trim(RecResultBed.getFieldString("YS_STK_LYR_NO"  ));	//TO위치-적치단
			szTEMP_STL_CNT       = commUtils.trim(RecResultBed.getFieldString("STL_CNT"        ));	//TO위치-적치된 재료매수: 야드적치단재료상태 == [C:적치중]or[D:권하대기]
			
			iTEMP_STL_CNT        = Integer.parseInt(commUtils.nvl(szTEMP_STL_CNT, "0"          ));  //TO위치-적치된 재료매수
			iYD_STK_BED_LYR_MAX	 = commUtils.paraRecChkNullInt(RecResultBed,"YD_STK_BED_LYR_MAX");	//야드적치Bed-단Max
			
			//"열/BED" 지정 시 : 특정 번들을 특정 위치로 이적할 경우 있다고 함
			if( sYD_TO_LOC_GUIDE.length() > 6 ) {
				//지정된 BED일 경우
				if( sYD_TO_LOC_GUIDE.substring(6,8).equals(szTEMP_YS_STK_BED_NO) ) {
					//"BED/Max-단"의 해당 BED의 "TO위치-적치된 재료매수"+"크레인작업재료 매수" <= 3 경우 TO위치로 결정
					if( (iTEMP_STL_CNT +  iCRNWRKMTL_CNT) <= 3 ) {
						szYS_DN_WO_LOC 	 = szTEMP_YS_STK_COL_GP + szTEMP_YS_STK_BED_NO;
						szYS_DN_WO_LAYER = szTEMP_YS_STK_LYR_NO;

						commUtils.printLog(logId, "["+ LocalmethodNm +"] TO 위치 가이드 처리 #위치 결정1: szYS_DN_WO_LOC ["+szYS_DN_WO_LOC+"], szYS_DN_WO_LAYER ["+szYS_DN_WO_LAYER+"]", "SL");
						sbImpPros.append("					 > 적재가능 여부 CHECK::'TO위치 결정1::(가능)열/BED' 지정 시 ['TO위치-적치된 재료매수'+'크레인작업재료 매수' <= 3] 조건 충족됨. szYS_DN_WO_LOC ["+szYS_DN_WO_LOC+"], szYS_DN_WO_LAYER ["+szYS_DN_WO_LAYER+"] \r\n");
						break;
					}
				}
				
				//위 매수 조건을 만족하지 못하면 "GDXXXXXX" 제공
				continue;
				
			//"미지정" or "스판","열" 지정 시
			} else {
			
				//"BED/Max-단"의 해당 BED의 "TO위치-적치된 재료매수"+"크레인작업재료 매수" <= 3 경우 TO위치로 결정
				if( (iTEMP_STL_CNT +  iCRNWRKMTL_CNT) <= 3 ) {
					szYS_DN_WO_LOC 	 = szTEMP_YS_STK_COL_GP + szTEMP_YS_STK_BED_NO;
					szYS_DN_WO_LAYER = szTEMP_YS_STK_LYR_NO;

					commUtils.printLog(logId, "["+ LocalmethodNm +"] TO 위치 가이드 처리 #위치 결정2: szYS_DN_WO_LOC ["+szYS_DN_WO_LOC+"], szYS_DN_WO_LAYER ["+szYS_DN_WO_LAYER+"]", "SL");
					sbImpPros.append("					 > 적재가능 여부 CHECK::TO위치 결정2::(가능)'미지정' or '스판','열' 지정 시 ['TO위치-적치된 재료매수'+'크레인작업재료 매수' <= 3] 조건 충족됨. szYS_DN_WO_LOC ["+szYS_DN_WO_LOC+"], szYS_DN_WO_LAYER ["+szYS_DN_WO_LAYER+"] \r\n");
					break;
				}
			}
		}
		
		//위에서 ["미지정" or "스판"/"열" 지정일 경우] 해당 "적치열" 내에 있는 "최상단"의 "공BED"가 없으면, 상위 "적치단"을 TO위치로 지정함
        if( sYD_TO_LOC_GUIDE.length() <= 6 && "".equals(szYS_DN_WO_LOC) ) {
        	szYS_DN_WO_LOC 	 = szTEMP_YS_STK_COL_GP + "01";
			szYS_DN_WO_LAYER = commUtils.stringPlusInt(""+szTEMP_YS_STK_LYR_NO, 1);

			commUtils.printLog(logId, "["+ LocalmethodNm +"] TO 위치 가이드 처리 #위치 결정3: szYS_DN_WO_LOC ["+szYS_DN_WO_LOC+"], szYS_DN_WO_LAYER ["+szYS_DN_WO_LAYER+"]", "SL");
			sbImpPros.append("					 > 적재가능 여부 CHECK::TO위치 결정3::(가능)위에서 ['미지정' or '스판'/'열' 지정일 경우] 해당 '적치열' 내에 있는 '최상단'의 '공BED'가 없으면, 상위 '적치단'을 TO위치로 지정함: szYS_DN_WO_LOC ["+szYS_DN_WO_LOC+"], szYS_DN_WO_LAYER ["+szYS_DN_WO_LAYER+"] \r\n");
			
        }
        
		//["TO위치-적치단" > "TO위치-최대 적재 가능단"]일 경우 "불가"로 Return
		if( Integer.parseInt(commUtils.nvl(szYS_DN_WO_LAYER, "0")) > iYD_STK_BED_LYR_MAX ) {
			szYS_DN_WO_LOC 	 = "";
			szYS_DN_WO_LAYER = "";

			commUtils.printLog(logId, "["+ LocalmethodNm +"] 최대 적재 가능단 초과", "SL");
			sbImpPros.append("					 > 적재가능 여부 CHECK::TO위치 결정4::(불가)['TO위치-적치단' > 'TO위치-최대 적재 가능단']일 경우 '불가' \r\n");
        }
		
		commUtils.printLog(logId, "["+ LocalmethodNm +"]  szYS_DN_WO_LOC: "+ szYS_DN_WO_LOC , "SL");
		sbImpPros.append("					 > 적재가능 여부 CHECK::적치 가능여부 결정 최종정보::szYS_DN_WO_LOC ("+ szYS_DN_WO_LOC+") \r\n");
		
        //적치 가능여부 결정
        if( !"".equals(szYS_DN_WO_LOC) ) {
        	RecRtn.setField("ABLE_YN", "Y");	//적치가능
        } else {
        	RecRtn.setField("ABLE_YN", "N");	//적치불가
        }
        
		RecRtn.setField("YS_DN_WO_LOC",   szYS_DN_WO_LOC);		//TO위치-권하지시위치
		RecRtn.setField("YS_DN_WO_LAYER", szYS_DN_WO_LAYER);	//TO위치-권하지시단
		
		commUtils.printParam(logId, RecRtn);
		commUtils.printLog(logId, methodNm, "S-");
		
		return RecRtn;
	}
	
	
	/**
	 * 주작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public String procMainWrkLoc(String logId, String methodNms, JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch, JDTORecord recWbook, StringBuffer sbImpPros) throws JDTOException {
		String methodNm      = "주작업TO위치결정[SbrYsSchSeEJB.procMainWrkLoc] < " + methodNms;
		String LocalmethodNm = "주작업TO위치결정[SbrYsSchSeEJB.procMainWrkLoc] " ;

		JDTORecordSet	rsStock			= null;
		JDTORecordSet	rsResult		= null;
		JDTORecordSet   RsResultBed 	= null;
		JDTORecordSet   outRsResult     = null;

		JDTORecord		recCrnwrkmtl	= null;
		JDTORecord		recPara			= null;
		JDTORecord		recStock		= null;
		JDTORecord		recTemp			= null;
		JDTORecord      RecResultBed 	= null;
		JDTORecord      recResult       = null;
		JDTORecord      outRecResult 	= null;
		
		String szYS_UP_WO_LOC			= null;
		String szYS_UP_WO_LAYER			= null;
		String szYS_DN_WO_LOC			= "";
		String szYS_DN_WO_LAYER			= null;
		String szYD_UP_STK_COL_GP		= null;
		String szYD_UP_STK_BED_NO		= null;
		
		String szLocYS_STK_COL_GP       = "";
		String szLocYS_STK_BED_NO       = "";
		
		commUtils.printLog(logId, methodNm, "S+");
		
		
		/**********************************************************************
		* 1. 크레인 작업재료정보
		***********************************************************************/
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 주작업 TO위치결정 > 크레인작업재료정보]::시작 \r\n");
		
		rsCrnwrkmtl.first();
		recCrnwrkmtl = rsCrnwrkmtl.getRecord();
		
		commUtils.printLog(logId, "["+ LocalmethodNm +"] -------------------- 크레인작업재료의 '단' 최대SEQ 재료정보 확인 -------------------", "SL");
		commUtils.printParam(logId, recWbook);
		commUtils.printLog(logId, "["+ LocalmethodNm +"] -------------------- 작업예약정보 확인 ---------------------------------------------", "SL");
		
		String szYD_CRN_SCH_ID      = commUtils.trim(recCrnwrkmtl.getFieldString("YD_CRN_SCH_ID"              ));	//크레인스케줄ID
		String szSSTL_NO 		    = commUtils.trim(recCrnwrkmtl.getFieldString("SSTL_NO"                    ));	//크레인작업재료 중 최대 길이
		String szYD_EQP_ID  	    = commUtils.trim(recCrnSch.getFieldString("YD_EQP_ID"                     ));	//크레인설비ID
		String szYD_SCH_PRIOR	    = commUtils.trim(recCrnSch.getFieldString("YD_SCH_PRIOR"                  ));	//분리작업시 필요 ???
		String szYD_WBOOK_ID	    = commUtils.trim(recCrnSch.getFieldString("YD_WBOOK_ID"                   ));	//크레인작업예약ID
		String szYD_TO_LOC_DCSN_MTD	= commUtils.trim(recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD"            ));	//TO위치결정방법: [M:이적][W:보조]
		String szYD_TO_LOC_GUIDE    = commUtils.trim(recWbook.getFieldString("YD_TO_LOC_GUIDE"                ));	//TO위치가이드(사용자지정 TO위치)
		String szYD_SCH_CD 		    = commUtils.trim(recWbook.getFieldString("YD_SCH_CD"                      ));	//크레인스케줄코드
		int    intMTL_CNT           = Integer.parseInt(commUtils.nvl(recCrnwrkmtl.getFieldString("SH_CNT"),"0"));	//작업재료 매수

		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 주작업 TO위치결정 > 크레인작업재료정보]::종료 \r\n \r\n");
		
		
		/**********************************************************************
		* 2. 야드 저장품정보 조회
		***********************************************************************/
		commUtils.printLog(logId, "["+ LocalmethodNm +"] 크레인작업재료의 '단' 내 첫번째 재료정보["+szSSTL_NO+"]를 저장품(TB_YS_STOCK)에서 조회 시작 ", "SL");
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 주작업 TO위치결정 > 야드저장품정보조회]::시작 \r\n");
		
		rsStock = commDao.select(recCrnwrkmtl, getYdStock, logId, methodNm, "저장품 조회");
		
		if( rsStock.size() <= 0 ) {
			commUtils.printLog(logId, "["+ methodNm +"]  재료정보["+szSSTL_NO+"]를 저장품에서 조회 실패 ", "SL");
			sbImpPros.append("			 > 저장품정보 조회::저장품에서 조회 실패: getYdStock \r\n");
			
			return YsConstant.RETN_CD_FAILURE;
		}
		
		rsStock.first();
		recStock = rsStock.getRecord();

		String szHEAT_NO   		= commUtils.trim(recStock.getFieldString("HEAT_NO"      ));		//HEAT_NO
		String szYD_MTL_L 	    = commUtils.trim(recStock.getFieldString("YD_MTL_L"     ));		//크레인작업 첫번째 재료의 길이
		String szYD_MTL_W 	    = commUtils.trim(recStock.getFieldString("YD_MTL_W"     ));		//크레인작업 첫번째 재료의 폭
		String szYD_MTL_T 	    = commUtils.trim(recStock.getFieldString("YD_MTL_T"     ));		//크레인작업 첫번째 재료의 두께
		String szSPEC_ABBSYM    = commUtils.trim(recStock.getFieldString("SPEC_ABBSYM"  ));		//규격약호 (강종)
		
		commUtils.printLog(logId, "["+ LocalmethodNm +"] 크레인작업재료의 '단' 내 첫번째 재료정보["+szSSTL_NO+"]를 저장품에서 조회 완료 - 두께["+szYD_MTL_T+"], 폭["+szYD_MTL_W+"], 길이["+szYD_MTL_L+"], HEAT_NO["+szHEAT_NO+"], SPEC_ABBSYM["+szSPEC_ABBSYM+"]", "SL");
		sbImpPros.append("			 > 저장품정보 조회::두께 ("+szYD_MTL_T+"), 폭 ("+szYD_MTL_W+"), 길이 ("+szYD_MTL_L+"), HEAT_NO ("+szHEAT_NO+"), SPEC_ABBSYM ("+szSPEC_ABBSYM+") \r\n");
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 주작업 TO위치결정 > 야드저장품정보조회]::종료 \r\n \r\n");
		
		
		/**********************************************************************
		* 3. "권상중 or 권하중"인 재료를 적치단으로부터 조회
		***********************************************************************/
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 주작업 TO위치결정 > '권상중or권하중'인 재료를 적치단으로부터 조회]::시작 \r\n");
		
		szYS_UP_WO_LOC 	 = commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LOC"  ));		
		szYS_UP_WO_LAYER = commUtils.trim(recCrnSch.getFieldString("YS_UP_WO_LAYER"));		
		
		//크레인스케줄 정보에 "권상지시위치"가 없을 경우
		if( szYS_UP_WO_LOC.equals("") ) {
			
			commUtils.printLog(logId, "["+ LocalmethodNm +"] 크레인작업재료의 최하단 재료정보["+szSSTL_NO+"]에 대한 권하 또는 권상위치 조회 시작 ", "SL");
			
			//"권하대기"중인 첫번째 작업재료의 적재위치정보 조회
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("SSTL_NO"            , szSSTL_NO);
			recPara.setField("YD_STK_LYR_MTL_STAT", "D"      );		//[D:권하대기]
			
			rsResult = commDao.select(recPara, getYdStklyrSTLNO, logId, methodNm, "적재위치정보 조회");
			
			if( rsResult.size() <= 0 ) {

				recPara.setField("YD_STK_LYR_MTL_STAT", "U");	//[U:권상대기]

				//"권상대기"중인 첫번째 작업재료의 적재위치정보 조회
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				rsResult = commDao.select(recPara, getYdStklyrSTLNO, logId, methodNm, "적재위치정보 조회");
				
				if( rsResult.size() <= 0 ) {
					commUtils.printLog(logId, "["+ methodNm +"]  재료정보["+szSSTL_NO+"]를 저장품에서 조회 실패 ", "SL");
					sbImpPros.append("			 > 적재위치정보 조회::적재위치정보 조회 실패: getYdStklyrSTLNO \r\n");
					
					return YsConstant.RETN_CD_FAILURE;  //"0";
				}	
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			commUtils.printLog(logId, "["+ LocalmethodNm +"] 크레인작업재료의  단 최대SEQ 재료정보["+szSSTL_NO+"]에 대한 권하 또는 권상위치 조회 완료  ", "SL");
			
			szYD_UP_STK_COL_GP 		= recTemp.getFieldString("YS_STK_COL_GP");
			szYD_UP_STK_BED_NO 		= recTemp.getFieldString("YS_STK_BED_NO");
			szYS_UP_WO_LOC 			= szYD_UP_STK_COL_GP + szYD_UP_STK_BED_NO;
			szYS_UP_WO_LAYER 		= recTemp.getFieldString("YS_STK_LYR_NO");
			
			commUtils.printLog(logId, "["+ LocalmethodNm +"] 조회된 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단["+szYS_UP_WO_LAYER+"]", "SL");
		
		//크레인스케줄 정보에 "권상지시위치"가 있을 경우
		} else {
			
			//크레인스케줄 정보 -> 권상지시 "열", "BED"
			szYD_UP_STK_COL_GP = szYS_UP_WO_LOC.substring(0, 6);
			szYD_UP_STK_BED_NO = szYS_UP_WO_LOC.substring(6);
			
			commUtils.printLog(logId, "["+ LocalmethodNm +"] 크레인스케줄에 등록된 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단["+szYS_UP_WO_LAYER+"]", "SL");
		}
		
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 주작업 TO위치결정 > '권상중or권하중'인 재료를 적치단으로부터 조회]::종료 \r\n \r\n");

		
		/**********************************************************
		* 4. 크레인스케줄(좌표 등) 권상위치 정보 갱신
		**********************************************************/
		String szYD_EQP_WRK_MAX_W 	= commUtils.trim(recCrnwrkmtl.getFieldString("MAX_MTL_W"      ));	//크레인작업재료 중 최대 폭
		String szYD_EQP_WRK_MAX_L 	= commUtils.trim(recCrnwrkmtl.getFieldString("MAX_MTL_L"      ));	//크레인작업재료 중 최대 길이
		int intYD_EQP_WRK_SH    	= commUtils.paraRecChkNullInt(recCrnwrkmtl,"SH_CNT"            );	//크레인작업재료 총매수
		int intYD_EQP_WRK_WT    	= commUtils.paraRecChkNullInt(recCrnwrkmtl,"SUM_MTL_WT"        );	//크레인작업재료 총중량
		double dblYD_EQP_WRK_T     	= commUtils.paraRecChkNullDouble(recCrnwrkmtl,"SUM_MTL_T"      );	//크레인작업재료 총높이
		
		String szMODIFIER 			= commUtils.trim(recCrnwrkmtl.getFieldString("MODIFIER"       ));	//MODIFIER
		
		
		//4.1. 크레인스케줄(좌표 등) 권상위치 정보 생성 
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 보조작업 TO위치결정 > 크레인스케줄(좌표 등) 권상위치정보 갱신]::시작 \r\n");
		
		JDTORecord recUpCrnSch = JDTORecordFactory.getInstance().create();
		recUpCrnSch.setField("YD_CRN_SCH_ID"   , szYD_CRN_SCH_ID               );	//크레인스케줄ID
		recUpCrnSch.setField("YD_EQP_ID"       , szYD_EQP_ID                   );	//크레인설비ID
		
		//4.2. 권상BED 좌표 조회
		sbImpPros.append("			 > 권상BED좌표조회::시작 \r\n");
		
		JDTORecord recInBed= JDTORecordFactory.getInstance().create();
		recInBed.setField("YS_STK_COL_GP", szYS_UP_WO_LOC.substring(0, 6)); //권상지시위치-적치열
		recInBed.setField("YS_STK_BED_NO", szYS_UP_WO_LOC.substring(6)   );	//권상지시위치-적치BED
		recInBed.setField("YS_STK_LYR_NO", szYS_UP_WO_LAYER              );	//권상지시위치-적치단
		
		JDTORecordSet RsBedUpXy = commDao.select(recInBed, getStkBedXY, logId, methodNm, "권상BED 좌표 조회");
		
		if( RsBedUpXy.size() <= 0 ) {
			commUtils.printLog(logId, "["+ LocalmethodNm +"] 권상BED 좌표 조회 검색 실패", "SL");
		}
		
		RsBedUpXy.first();
		JDTORecord RecUpBedXy = RsBedUpXy.getRecord();

		sbImpPros.append("			 > 권상BED좌표조회::종료 \r\n");
		
		//4.3. 권상정보 생성
		recUpCrnSch.setField("YS_UP_WO_LOC"    , szYS_UP_WO_LOC                );	//권상지시위치
		recUpCrnSch.setField("YS_UP_WO_LAYER"  , szYS_UP_WO_LAYER              );	//권상지시단
		recUpCrnSch.setField("YD_UP_STK_COL_GP", szYS_UP_WO_LOC.substring(0, 6));	//권상지시위치-적치열
		recUpCrnSch.setField("YD_UP_STK_BED_NO", szYS_UP_WO_LOC.substring(6)   );	//권상지시위치-적치BED
		
		//짝수단
		if( commUtils.trim(RecUpBedXy.getFieldString("DAN_GP")).equals("0") ) {
			recUpCrnSch.setField("YD_UP_WO_LOC_XAXIS", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS1"   )));
			recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS1"   )));
			recUpCrnSch.setField("YD_UP_WO_LOC_ZAXIS", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_ZAXIS1"   )));
		//홀수단
		} else {
			recUpCrnSch.setField("YD_UP_WO_LOC_XAXIS", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS"    )));
			recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS"    )));
			recUpCrnSch.setField("YD_UP_WO_LOC_ZAXIS", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_ZAXIS"    )));
		}
		recUpCrnSch.setField("YD_UP_WO_XAXIS_GAP_MAX", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS_TOL")));
		recUpCrnSch.setField("YD_UP_WO_XAXIS_GAP_MIN", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS_TOL")));
		recUpCrnSch.setField("YD_UP_WO_YAXIS_GAP_MAX", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS_TOL")));
		recUpCrnSch.setField("YD_UP_WO_YAXIS_GAP_MIN", commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS_TOL")));
		recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS1"   , "");		//야드권상지시Y축1
		recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS2"   , "");		//야드권상지시Y축2
		recUpCrnSch.setField("YD_UP_WO_ZAXIS_GAP_MAX", "");		//야드권상지시Z축오차최대
		recUpCrnSch.setField("YD_UP_WO_ZAXIS_GAP_MIN", "");		//야드권상지시Z축오차최소
		
		//4.4 기타
		recUpCrnSch.setField("YD_EQP_WRK_SH"   , String.valueOf(intYD_EQP_WRK_SH));	//크레인작업재료 총매수
		recUpCrnSch.setField("YD_EQP_WRK_WT"   , String.valueOf(intYD_EQP_WRK_WT));	//크레인작업재료 총중량
		recUpCrnSch.setField("YD_EQP_WRK_T"    , String.valueOf(dblYD_EQP_WRK_T ));	//크레인작업재료 총높이
		recUpCrnSch.setField("YD_EQP_WRK_MAX_W", szYD_EQP_WRK_MAX_W              );	//크레인작업재료 중 최대 폭
		recUpCrnSch.setField("YD_EQP_WRK_MAX_L", szYD_EQP_WRK_MAX_L              );	//크레인작업재료 중 최대 길이
		recUpCrnSch.setField("MODIFIER"        , szMODIFIER                      );
		
		//4.5 크레인스케줄(좌표 등) 권상위치 정보 갱신
		int intRtnVal = commDao.update(recUpCrnSch, updCrnschXY, logId, methodNm, "크레인스케줄(좌표 등) 권상위치 정보 갱신");
		
		if( intRtnVal <= 0 ) {
			commUtils.printLog(logId, "["+ LocalmethodNm +"] 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단[" +szYS_UP_WO_LAYER +" ] : 크레인스케줄(좌표 등) 권상위치정보 갱신 중 ERROR 발생", "SL");
			sbImpPros.append("			 > 크레인스케줄(좌표 등) 권상위치정보 갱신::크레인스케줄(좌표 등) 권상위치정보 갱신 중 ERROR 발생 szYS_DN_WO_LOC [" + szYS_UP_WO_LOC + "], szYS_DN_WO_LAYER [" + szYS_UP_WO_LAYER + "]\r\n");
			
			return YsConstant.RETN_CD_FAILURE;
		}
		
		commUtils.printLog(logId, "["+ LocalmethodNm +"] 권상지시위치["+szYS_UP_WO_LOC+"], 권상지시단["+szYS_UP_WO_LAYER+"] : 크레인스케줄(좌표 등) 권상위치정보 갱신 완료", "SL");
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 보조작업 TO위치결정 > 크레인스케줄(좌표 등) 권상위치정보 갱신]::종료 \r\n \r\n");
		
		
		/**********************************************************
		* 5. "TO위치가이드" 존재할 경우
		*    동일한 사양의 적치가능한 "열"(+"Max-BED/Max-단") 조회 
		*    - 대형옥내-검색기준: "길이 > 두께(각) > Heat No > 강종" + 스케줄코드에 맞는 특정 적치대 구간에서만 검색
		*    - 빌렛소재-검색기준: 재료와 같은 "동일 HEAT", "동일 강종"의 적치가능한 베드 해당 동의 모든 위치를 조회
		**********************************************************/
		commUtils.printLog(logId, "["+ LocalmethodNm +"] TOSQL:["+szYD_CRN_SCH_ID+ "] 동일한사양  szHEAT_NO["+szHEAT_NO+"], szYD_MTL_T["+szYD_MTL_T+"], szYD_MTL_L["+szYD_MTL_L+"], szSPEC_ABBSYM["+szSPEC_ABBSYM+"], szYD_SCH_CD["+szYD_SCH_CD+"] 의 적치가능한 '열'(+'Max-BED/Max-단') 조회 시작", "SL");
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 주작업 TO위치결정 > 동일한 사양의 적치가능한 '열' 조회]::시작 \r\n");
		sbImpPros.append("			 > 크레인작업재료&저장품 정보::szYD_TO_LOC_DCSN_MTD ("+szYD_TO_LOC_DCSN_MTD+"), szYD_SCH_CD ("+szYD_SCH_CD+"), szHEAT_NO ("+szHEAT_NO+"), szYD_MTL_T ("+szYD_MTL_T+"), szYD_MTL_L ("+szYD_MTL_L+"), SPEC_ABBSYM ("+szSPEC_ABBSYM+"), szYD_UP_STK_COL_GP ("+szYD_UP_STK_COL_GP+"), 크레인작업재료 매수 ("+intMTL_CNT+")  \r\n");
		
		
    	recTemp = JDTORecordFactory.getInstance().create();

    	recTemp.setField("YD_TO_LOC_DCSN_MTD", szYD_TO_LOC_DCSN_MTD);					//TO위치결정방법: [M:이적][W:보조]
		recTemp.setField("YD_SCH_CD"         , szYD_SCH_CD         );					//스케줄코드 
		recTemp.setField("HEAT_NO"           , szHEAT_NO           );					//크레인작업 HEAT_NO
    	recTemp.setField("YD_MTL_T",           szYD_MTL_T          );					//크레인작업 첫번째 재료의 두께
    	recTemp.setField("YD_MTL_L",           szYD_MTL_L          );					//크레인작업 첫번째 재료의 길이
    	recTemp.setField("SPEC_ABBSYM",        szSPEC_ABBSYM       );					//규격약호 (강종)
		recTemp.setField("YS_STK_COL_GP"     , szYD_UP_STK_COL_GP  );					//권상지시위치-적치열
		
		commUtils.printLog(logId, "["+ LocalmethodNm +"] 동일한 사양의 적치가능한 '열'(+'Max-BED/Max-단')의 적치가능한 베드 조회 시작", "SL");
		
		//5.1. "TO위치가이드" 존재하면...
		//5.1.1. "TO위치가이드" 값이 ("열" || "열/BED") 지정인 경우 <- 화면
		if( (szYD_TO_LOC_GUIDE.length() == 6) || (szYD_TO_LOC_GUIDE.length() == 8) ) {
			
			commUtils.printLog(logId, "["+ LocalmethodNm +"] TOSQL:["+szYD_CRN_SCH_ID+ "]  적치대TO위치결정 적재위치 가이드  YD_TO_LOC_GUIDE ["+szYD_TO_LOC_GUIDE+"]의 베드 조회 시작", "SL");
			sbImpPros.append("			 > 'TO위치가이드'값('열'||'열/BED')이 지정인 경우::시작 \r\n");
			sbImpPros.append("			 > 'TO위치가이드'값('열'||'열/BED')이 지정인 경우::szYD_TO_LOC_GUIDE ("+szYD_TO_LOC_GUIDE+") \r\n");
			
			JDTORecord recTemp1 = JDTORecordFactory.getInstance().create();
			recTemp1.setField("YD_UP_STK_COL_GP", 	szYD_UP_STK_COL_GP      );
			recTemp1.setField("YD_TO_LOC_DCSN_MTD", szYD_TO_LOC_DCSN_MTD    );
			recTemp1.setField("MTL_CNT", 			""+intMTL_CNT           );	//크레인작업재료 매수
			recTemp1.setField("YD_SCH_CD", 			szYD_SCH_CD             );	//스케줄코드
			recTemp1.setField("YD_WBOOK_ID",		szYD_WBOOK_ID           );	//크레인작업예약ID
			recTemp1.setField("YD_CRN_SCH_ID",		szYD_CRN_SCH_ID         );	//크레인스케줄ID
			recTemp1.setField("MODIFIER",           szMODIFIER              );
			
			//5.1.1.1. TO위치가 "설비"일 경우 결정
			if( constant.SCH_CD_GDTF11MM.equals(szYD_SCH_CD) || constant.SCH_CD_GDTF12MM.equals(szYD_SCH_CD) ||														//GDTF11 (정렬대) || GDTF12 (정렬대)
				constant.SCH_CD_GDTF21MM.equals(szYD_SCH_CD) || constant.SCH_CD_GDTF22MM.equals(szYD_SCH_CD) ||														//GDTF21 (정렬대) || GDTF22 (정렬대)
				constant.SCH_CD_GDTC04UM.equals(szYD_SCH_CD) || constant.SCH_CD_GDTC05UM.equals(szYD_SCH_CD) || constant.SCH_CD_GDTC06UM.equals(szYD_SCH_CD) ) {	//GDTC04 (대차포인트) || GDTC05 (대차포인트) || GDTC06 (대차포인트)

				commUtils.printLog(logId, "["+ LocalmethodNm +"] 주작업TO위치결정 >> TO위치가 '설비'일 경우... 시작", "SL");
				
				szLocYS_STK_COL_GP = szYD_TO_LOC_GUIDE.substring(0,6);	//적치열
				szLocYS_STK_BED_NO = "01";
				
				recTemp1.setField("LOC_YS_STK_COL_GP", 	szLocYS_STK_COL_GP);	//TO위치-적치열
				recTemp1.setField("LOC_YS_STK_BED_NO", 	szLocYS_STK_BED_NO);	//TO위치-적치BED
				
				//설비-적재가능 여부 CHECK
				//각 설비별 적재가능여부 판별 기준은 ???
				// - GDTF11 12 21 22 (정렬대)     : TO위치-적치매수 + 작업재료매수 <= 5
				// - GDTC04 05 06    (대차포인트) : TO위치-적치매수 + 작업재료매수 <= 3
				sbImpPros.append("				['TO위치가이드'값('열'||'열/BED')이 지정인 경우::TO위치가 '설비'일 경우::적재가능 여부 CHECK]::시작 \r\n");
				sbImpPros.append("				 > 'TO위치가이드'값('열'||'열/BED')이 지정인 경우::TO위치 ("+szLocYS_STK_COL_GP + szLocYS_STK_BED_NO +") \r\n");
				recResult = this.procLocAbleCheckCnt_Eqp(logId, methodNms, recTemp1, sbImpPros);
				sbImpPros.append("				['TO위치가이드'값('열'||'열/BED')이 지정인 경우::TO위치가 '설비'일 경우::적재가능 여부 CHECK]::종료 \r\n");
				
				if( "Y".equals(commUtils.trim(recResult.getFieldString("ABLE_YN"))) ) {
					szYS_DN_WO_LOC 	 = commUtils.trim(recResult.getFieldString("YS_DN_WO_LOC"  ));	//"열/BED"	
					szYS_DN_WO_LAYER = commUtils.trim(recResult.getFieldString("YS_DN_WO_LAYER"));	//"단"
				}
				
				sbImpPros.append("			 > 'TO위치가이드'값('열'||'열/BED')이 지정인 경우::TO위치가 '설비'일 경우::적재가능 여부 CHECK 결과::TO위치 ("+szYS_DN_WO_LOC+szYS_DN_WO_LAYER+") \r\n");
				
			//5.1.1.2. TO위치가 "차량"일 경우 결정
			} else if( constant.SCH_CD_GDTR11UM.equals(szYD_SCH_CD) || constant.SCH_CD_GDTR21UM.equals(szYD_SCH_CD) ) {	// GDTR11 (차량포인트) || GDTR21 (차량포인트)
				
				//TO위치가 차량일 경우 차량의 중심-X/Y 좌표값으로 n개의 스케줄을 생성하고,
				//유인일 경우 작업자가 차량내 적절한 위치로 이적함
				// - 자동화시점    : 차량 내 "BED" & "X/Y 좌표"를 생성한 후 "강종별", "길이별", "상차매수", "차량 단수"를 파악하여 차량 적치단을 조회하는 쿼리를 만들어야 함
				// - 차량 적치단수 : 1단
				
				commUtils.printLog(logId, "["+ LocalmethodNm +"] 주작업TO위치결정 >> TO위치가 '차량'일 경우... 시작", "SL");
				
				szLocYS_STK_COL_GP = szYD_TO_LOC_GUIDE.substring(0,6);	//적치열
				szLocYS_STK_BED_NO = "01";

				recTemp1.setField("LOC_YS_STK_COL_GP", 	szLocYS_STK_COL_GP);	//TO위치-적치열
				
				//차량-적재가능 여부 CHECK
				sbImpPros.append("				['TO위치가이드'값('열'||'열/BED')이 지정인 경우::TO위치가 '차량'일 경우::적재가능 여부 CHECK]::시작 \r\n");
				sbImpPros.append("				 > 'TO위치가이드'값('열'||'열/BED')이 지정인 경우::TO위치 ("+szLocYS_STK_COL_GP + szLocYS_STK_BED_NO +") \r\n");
				recResult = this.procLocAbleCheckCnt_Car(logId, methodNms, recTemp1, sbImpPros);
				sbImpPros.append("				['TO위치가이드'값('열'||'열/BED')이 지정인 경우::TO위치가 '차량'일 경우::적재가능 여부 CHECK]::종료 \r\n");
				
				if( "Y".equals(commUtils.trim(recResult.getFieldString("ABLE_YN"))) ) {
					szYS_DN_WO_LOC 	 = commUtils.trim(recResult.getFieldString("YS_DN_WO_LOC"  ));	//"열/BED"	
					szYS_DN_WO_LAYER = commUtils.trim(recResult.getFieldString("YS_DN_WO_LAYER"));	//"단"
				} else {
		        	//차량상차 불가시 작업예약(유지), 크레인스케줄(삭제) 적용여부
		        	String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "003");
		        	if( "Y".equals(sRuleItem) ) {
		        		commUtils.printLog(logId, "차량-적재가능 여부 CHECK 오류 :: [szYS_DN_WO_LOC||szYS_DN_WO_LAYER] == [NULL]", "SL");
						sbImpPros.append("			 > 'TO위치가이드'값('열'||'열/BED')이 지정인 경우::TO위치가 '차량'일 경우: [szYS_DN_WO_LOC||szYS_DN_WO_LAYER] == [NULL], YsConstant.RETN_CD_FAILURE \r\n");
						
						throw new DAOException("차량-적재가능 여부 CHECK 오류 :: [szYS_DN_WO_LOC||szYS_DN_WO_LAYER] == [NULL]");
		        	}
				}
				
				sbImpPros.append("			 > 'TO위치가이드'값('열'||'열/BED')이 지정인 경우::TO위치가 '차량'일 경우::적재가능 여부 CHECK 결과::TO위치 ("+szYS_DN_WO_LOC+szYS_DN_WO_LAYER+") \r\n");
				
			//5.1.1.3. TO위치가 "야드"일 경우 결정
			} else {
				
				commUtils.printLog(logId, "["+ LocalmethodNm +"] 주작업TO위치결정 >> TO위치가 '야드'일 경우... 시작", "SL");

				recTemp.setField("YD_TO_LOC_GUIDE", szYD_TO_LOC_GUIDE);
				
				RsResultBed = commDao.select(recTemp, getToLocColByLocGuide, logId, methodNm, "'TO위치가이드'의 적치가능한 '열'(+'Max-BED/Max-단') 조회");
				
				if( RsResultBed.size() <= 0 ) {
					commUtils.printLog(logId, "'TO위치가이드'의 적치가능한 '열'(+'Max-BED/Max-단') 조회 실패 ", "SL");
					szYS_DN_WO_LOC = "";
					
				} else {
					
					RsResultBed.first();
					RecResultBed = RsResultBed.getRecord();
					
					szLocYS_STK_COL_GP = commUtils.trim(RecResultBed.getFieldString("YS_STK_COL_GP"  ));
					szLocYS_STK_BED_NO = commUtils.trim(RecResultBed.getFieldString("YS_STK_BED_NO"  ));

					recTemp1.setField("LOC_YS_STK_COL_GP", 	szLocYS_STK_COL_GP);	//TO위치-적치열
					recTemp1.setField("LOC_YS_STK_BED_NO", 	szLocYS_STK_BED_NO);	//TO위치-적치BED
					recTemp1.setField("YD_TO_LOC_GUIDE", 	szYD_TO_LOC_GUIDE );	//TO위치가이드(사용자지정 TO위치)

					commUtils.printLog(logId, "["+ LocalmethodNm +"] TO위치["+ szLocYS_STK_COL_GP + szLocYS_STK_BED_NO + commUtils.trim(RecResultBed.getFieldString("YS_STK_LYR_NO")) + commUtils.trim(RecResultBed.getFieldString("YS_STK_SEQ_NO")) +"], TO위치를 검색한 쿼리[" + commUtils.trim(RecResultBed.getFieldString("TO_LOC_POINT")) +"]", "SL");

					//야드-적재가능 여부 CHECK > 'TO위치가이드'값('열'||'열/BED')이 지정인 경우
					sbImpPros.append("				['TO위치가이드'값('열'||'열/BED')이 지정인 경우::TO위치가 '야드'일 경우::적재가능 여부 CHECK]::시작 \r\n");
					sbImpPros.append("				 > 'TO위치가이드'값('열'||'열/BED')이 지정인 경우::TO위치 ("+szLocYS_STK_COL_GP + szLocYS_STK_BED_NO + commUtils.trim(RecResultBed.getFieldString("YS_STK_LYR_NO")) + commUtils.trim(RecResultBed.getFieldString("YS_STK_SEQ_NO"))+") \r\n");
					recResult = this.procLocAbleCheckCnt(logId, methodNms, recTemp1, sbImpPros);
					sbImpPros.append("				['TO위치가이드'값('열'||'열/BED')이 지정인 경우::TO위치가 '야드'일 경우::적재가능 여부 CHECK]::종료 \r\n");
					
					if( "Y".equals(commUtils.trim(recResult.getFieldString("ABLE_YN"))) ) {
						szYS_DN_WO_LOC 	 = commUtils.trim(recResult.getFieldString("YS_DN_WO_LOC"  ));	//"열/BED"	
						szYS_DN_WO_LAYER = commUtils.trim(recResult.getFieldString("YS_DN_WO_LAYER"));	//"단"
					}
				}
				
				sbImpPros.append("			 > 'TO위치가이드'값('열'||'열/BED')이 지정인 경우::TO위치가 '야드'일 경우::적재가능 여부 CHECK 결과::TO위치 ("+szYS_DN_WO_LOC+szYS_DN_WO_LAYER+") \r\n");
			}

			sbImpPros.append("			 > 'TO위치가이드'값('열'||'열/BED')이 지정인 경우::종료 \r\n");
			
		//5.1.2. "TO위치가이드" 값이 "열/BED/단" 지정인 경우 
		} else if( szYD_TO_LOC_GUIDE.length() == 10 ) {

			commUtils.printLog(logId, "["+ LocalmethodNm +"] TOSQL:["+szYD_CRN_SCH_ID+ "] 동일한사양의 적치가능한 '열'(+'Max-BED/Max-단') 조회 시작", "SL");

			szYS_DN_WO_LOC 	 = szYD_TO_LOC_GUIDE.substring(0,8);
			szYS_DN_WO_LAYER = szYD_TO_LOC_GUIDE.substring(8,10);				
			
			sbImpPros.append("			 > 'TO위치가이드' 값이 '열/BED/단' 지정인 경우::결과::TO위치 ("+szYS_DN_WO_LOC+szYS_DN_WO_LAYER+") \r\n");
			
			
		//5.1.3. "TO위치가이드"가 "야드/동/스판 (동내이적)"까지 지정하였거나 존재하지 않을 경우
		//       - 현업 의견 확인 필요함: "TO위치가이드"가 제공되었지만 적재가 불가능 할 경우
		//                                1. "GEXXXXXX"를 뛰워준다 ?
		//                                2. 야드를 대상으로 적치 가능한 BED를 재조회한다 ?
		} else if( szYS_DN_WO_LOC.equals("") ) {

			/**********************************************************
			* 5. "TO위치가이드"가 존재하지 않을 경우 
			*    동일한 사양의 적치가능한 "열"(+"Max-BED/Max-단") 조회 
			*    - 대형옥내-검색기준: "길이 > 두께(각) > Heat No > 강종" + 스케줄코드에 맞는 특정 적치대 구간에서만 검색
			*    - 빌렛소재-검색기준: 재료와 같은 "동일 HEAT", "동일 강종"의 적치가능한 베드 해당 동의 모든 위치를 조회
			**********************************************************/
			
			commUtils.printLog(logId, "["+ LocalmethodNm +"] TOSQL:["+szYD_CRN_SCH_ID+ "] 동일한사양  HEAT_NO: ["+szHEAT_NO+"]"	+ "의 적치가능한 베드 조회 시작", "SL");
			sbImpPros.append("			 > 'TO위치가이드'가 존재하지 않을 경우::시작 \r\n");
						
			outRsResult = commDao.select(recTemp, getToLocCol, logId, methodNm, "동일한 사양의 적치가능한 '열'(+'Max-BED/Max-단') 조회");
			
			if( outRsResult.size() <= 0 ) {
				commUtils.printLog(logId, "["+ LocalmethodNm +"] 동일한 사양의 적치가능한 '열'(+'Max-BED/Max-단') 조회 실패 ", "SL");
				sbImpPros.append("			 > 'TO위치가이드'가 존재하지 않을 경우::동일한 사양의 적치가능한 '열'(+'Max-BED/Max-단') 조회::동일한  사양의 적치가능한 '열'(+'Max-BED/Max-단') 조회 실패: getToLocCol \r\n");
				
				return YsConstant.RETN_CD_FAILURE;
			}
			

			// 5.1. 동일한 사양의 적치가능한 "열" 단위로 "적치가능 여부 CEHCK"
			for( int i = 1; i <= outRsResult.size(); i++ ) {

				outRsResult.absolute(i);
				outRecResult  = outRsResult.getRecord();
				
				szLocYS_STK_COL_GP  = commUtils.trim(outRecResult.getFieldString("YS_STK_COL_GP"  ));
				szLocYS_STK_BED_NO  = commUtils.trim(outRecResult.getFieldString("YS_STK_BED_NO"  ));
				
				commUtils.printLog(logId, "["+ LocalmethodNm +"] TO위치["+ szLocYS_STK_COL_GP + szLocYS_STK_BED_NO + commUtils.trim(outRecResult.getFieldString("YS_STK_LYR_NO")) + commUtils.trim(outRecResult.getFieldString("YS_STK_SEQ_NO")) +"], TO위치를 검색한 쿼리[" + commUtils.trim(outRecResult.getFieldString("TO_LOC_POINT")) +"]", "SL");
				
				JDTORecord recTemp1 = JDTORecordFactory.getInstance().create();
				recTemp1.setField("YD_UP_STK_COL_GP"  , szYD_UP_STK_COL_GP  );		
				recTemp1.setField("LOC_YS_STK_COL_GP" , szLocYS_STK_COL_GP  );		
				recTemp1.setField("LOC_YS_STK_BED_NO" , szLocYS_STK_BED_NO  );
				recTemp1.setField("YD_TO_LOC_DCSN_MTD", szYD_TO_LOC_DCSN_MTD);	
				recTemp1.setField("MTL_CNT"           , ""+intMTL_CNT       );	
				recTemp1.setField("YD_SCH_CD"         , szYD_SCH_CD         );
				recTemp1.setField("YD_TO_LOC_GUIDE"   ,	szYD_TO_LOC_GUIDE   );	//TO위치가이드(사용자지정 TO위치)
				
				//적재가능 여부 CHECK
				sbImpPros.append("				['TO위치가이드'가 존재하지 않을 경우::적재가능 여부 CHECK]::시작 \r\n");
				sbImpPros.append("				 > 'TO위치가이드'가 존재하지 않을 경우::TO위치 ("+szLocYS_STK_COL_GP + szLocYS_STK_BED_NO + commUtils.trim(outRecResult.getFieldString("YS_STK_LYR_NO")) + commUtils.trim(outRecResult.getFieldString("YS_STK_SEQ_NO"))+") \r\n");
				recResult = this.procLocAbleCheckCnt(logId, methodNms, recTemp1, sbImpPros);
				sbImpPros.append("				['TO위치가이드'가 존재하지 않을 경우::적재가능 여부 CHECK]::종료 \r\n");
				
				if( commUtils.trim(recResult.getFieldString("ABLE_YN")).equals("Y") ) {
					szYS_DN_WO_LOC 	= commUtils.trim(recResult.getFieldString("YS_DN_WO_LOC"  ));	//TO위치-권하지시위치
					szYS_DN_WO_LAYER= commUtils.trim(recResult.getFieldString("YS_DN_WO_LAYER"));	//TO위치-권하지시단

					commUtils.printLog(logId, "["+ LocalmethodNm +"] 적재가능 여부 CHECK (순번:"+i+") 결과::TO위치 ("+szYS_DN_WO_LOC+szYS_DN_WO_LAYER+")", "SL");
					sbImpPros.append("			 > 'TO위치가이드'가 존재하지 않을 경우::적재가능 여부 CHECK (순번:"+i+") 결과::TO위치 ("+szYS_DN_WO_LOC+szYS_DN_WO_LAYER+") \r\n");
					break;
					
				} else {
					commUtils.printLog(logId, "["+ LocalmethodNm +"]  continue~!!!", "SL");
					sbImpPros.append("			 > 'TO위치가이드'가 존재하지 않을 경우::적재가능 여부 CHECK (순번:"+i+") 결과::continue~!!! \r\n");
				}
			}
			sbImpPros.append("			 > 'TO위치가이드'가 존재하지 않을 경우::종료 \r\n");
		}
		
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 주작업 TO위치결정 > 동일한 사양의 적치가능한 '열' 조회]::종료 \r\n \r\n");
				
		
		/**********************************************************
		* 6. TO위치 UPDATE
		**********************************************************/
		JDTORecord RecSetLoc = JDTORecordFactory.getInstance().create();
		RecSetLoc.setField("YD_CRN_SCH_ID", 	 szYD_CRN_SCH_ID); 
		RecSetLoc.setField("YD_EQP_ID", 		 szYD_EQP_ID);	
		RecSetLoc.setField("YD_SCH_CD", 		 szYD_SCH_CD);	 
		RecSetLoc.setField("YD_WBOOK_ID", 		 szYD_WBOOK_ID);
		RecSetLoc.setField("YD_SCH_PRIOR", 		 szYD_SCH_PRIOR);	 
		RecSetLoc.setField("YS_UP_WO_LOC", 		 szYS_UP_WO_LOC); 
		RecSetLoc.setField("YS_UP_WO_LAYER",	 szYS_UP_WO_LAYER);	 
		RecSetLoc.setField("YS_DN_WO_LOC", 		 szYS_DN_WO_LOC);        
		RecSetLoc.setField("YS_DN_WO_LAYER", 	 szYS_DN_WO_LAYER);
		RecSetLoc.setField("YD_TO_LOC_DCSN_MTD", szYD_TO_LOC_DCSN_MTD);
		RecSetLoc.setField("YD_AID_WRK_UPDN_GP", "1");
		
		//TO위치 UPDATE
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 주작업 TO위치결정 > TO위치UPDATE]::시작 \r\n");
		this.procUpdateLoc(logId, methodNms, recCrnwrkmtl, RecSetLoc, sbImpPros);
		sbImpPros.append("			[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 주작업 TO위치결정 > TO위치UPDATE]::종료 \r\n");
		
		return YsConstant.RETN_CD_SUCCESS;
	}

	/**
	 * TO위치 UPDATE
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */	
	public String procUpdateLoc(String logId, String methodNms, JDTORecord recCrnwrkmtl, JDTORecord RecSetLoc, StringBuffer sbImpPros) throws JDTOException {
		String methodNm      = "TO위치 UPDATE[SbrYsSchSeEJB.procUpdateLoc] < " + methodNms;
		String LocalmethodNm = "TO위치 UPDATE[SbrYsSchSeEJB.procUpdateLoc]";
		
		JDTORecordSet RsBedUpXy  = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet RsDnBedXy  = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord    RecUpBedXy = JDTORecordFactory.getInstance().create();
		JDTORecord    RecDnBedXy = JDTORecordFactory.getInstance().create();
		JDTORecord    recInBed   = null;
		
		int           intRtnVal  = 0;
		
		commUtils.printLog(logId, methodNm, "S+");
		
		/**********************************************************
		* 파라미터
		**********************************************************/
		String szYD_CRN_SCH_ID  	= commUtils.trim(RecSetLoc.getFieldString("YD_CRN_SCH_ID"     ));	//크레인스케줄ID
		String szYD_EQP_ID  		= commUtils.trim(RecSetLoc.getFieldString("YD_EQP_ID"         ));	
		String szYS_UP_WO_LOC		= commUtils.trim(RecSetLoc.getFieldString("YS_UP_WO_LOC"      ));			
		String szYS_UP_WO_LAYER		= commUtils.trim(RecSetLoc.getFieldString("YS_UP_WO_LAYER"    ));			
		String szYS_DN_WO_LOC		= commUtils.trim(RecSetLoc.getFieldString("YS_DN_WO_LOC"      ));			
		String szYS_DN_WO_LAYER		= commUtils.trim(RecSetLoc.getFieldString("YS_DN_WO_LAYER"    ));			
		
		String szMODIFIER 			= commUtils.trim(recCrnwrkmtl.getFieldString("MODIFIER"       ));	//MODIFIER
		String szYD_SCH_CD 		    = commUtils.trim(RecSetLoc.getFieldString("YD_SCH_CD"         ));	//크레인스케줄코드
		
		commUtils.printParam(logId, RecSetLoc);
		commUtils.printLog(logId, "["+ LocalmethodNm +"] 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단["+szYS_DN_WO_LAYER+"]을 크레인스케줄에 수정 시작", "SL");
		
		
		//"권하지시위치(TO위치)"가 있으면...
		if( !szYS_DN_WO_LOC.equals("") ) {
			
			/**********************************************************
			* 1. 크레인스케줄(좌표 등) 권하위치정보 갱신
			**********************************************************/
			sbImpPros.append("				[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 주/보조작업TO위치결정 > TO위치UPDATE > 크레인스케줄(좌표 등) 권하위치정보 갱신]::시작 \r\n");
			
			JDTORecord recUpCrnSch = JDTORecordFactory.getInstance().create();
			recUpCrnSch.setField("YD_CRN_SCH_ID"   , szYD_CRN_SCH_ID               );	//크레인스케줄ID
			recUpCrnSch.setField("YD_EQP_ID"       , szYD_EQP_ID                   );	//크레인설비ID
			
			
			/**********************************************************
			* 1.1. 권하BED 좌표 조회
			**********************************************************/
			sbImpPros.append("				 > 권하BED좌표조회::시작 \r\n");

			recInBed= JDTORecordFactory.getInstance().create();
			recInBed.setField("YS_STK_COL_GP", szYS_DN_WO_LOC.substring(0, 6));	//권하지시위치-적치열
			recInBed.setField("YS_STK_BED_NO", szYS_DN_WO_LOC.substring(6)   );	//권하지시위치-적치BED
			recInBed.setField("YS_STK_LYR_NO", szYS_DN_WO_LAYER              );	//권상지시위치-적치단
			
			RsDnBedXy = commDao.select(recInBed, getStkBedXY, logId, methodNm, "권하BED 좌표 조회");
			
			if( RsDnBedXy.size() <= 0 ) {
				commUtils.printLog(logId, "["+ LocalmethodNm +"] 권하BED 좌표 검색 실패", "SL");
				
			}
			
			RsDnBedXy.first();
			RecDnBedXy = RsDnBedXy.getRecord();
	
			sbImpPros.append("				 > 권하BED좌표조회::종료 \r\n");
			
	
			//1.1.1. 권하정보 생성
			recUpCrnSch.setField("YS_DN_WO_LOC"    , szYS_DN_WO_LOC                );	//권하지시위치
			recUpCrnSch.setField("YS_DN_WO_LAYER"  , szYS_DN_WO_LAYER              );	//권하지시단
			recUpCrnSch.setField("YD_DN_STK_COL_GP", szYS_DN_WO_LOC.substring(0, 6));	//권하지시위치-적치열
			recUpCrnSch.setField("YD_DN_STK_BED_NO", szYS_DN_WO_LOC.substring(6)   );	//권하지시위치-적치BED
			
			//짝수단
			if( commUtils.trim(RecDnBedXy.getFieldString("DAN_GP")).equals("0") ) {
				recUpCrnSch.setField("YD_DN_WO_LOC_XAXIS", commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_XAXIS1"   )));
				recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS", commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_YAXIS1"   )));
				recUpCrnSch.setField("YD_DN_WO_LOC_ZAXIS", commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_ZAXIS1"   )));
			//홀수단
			} else {
				recUpCrnSch.setField("YD_DN_WO_LOC_XAXIS", commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_XAXIS"    )));
				recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS", commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_YAXIS"    )));
				recUpCrnSch.setField("YD_DN_WO_LOC_ZAXIS", commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_ZAXIS"    )));
			}
			recUpCrnSch.setField("YD_DN_WO_XAXIS_GAP_MAX", commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_XAXIS_TOL")));
			recUpCrnSch.setField("YD_DN_WO_XAXIS_GAP_MIN", commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_XAXIS_TOL")));
			recUpCrnSch.setField("YD_DN_WO_YAXIS_GAP_MAX", commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_YAXIS_TOL")));
			recUpCrnSch.setField("YD_DN_WO_YAXIS_GAP_MIN", commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_YAXIS_TOL")));
			recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS1"   , "" );
			recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS2"   , "" );
			recUpCrnSch.setField("YD_DN_WO_ZAXIS_GAP_MAX", "" );
			recUpCrnSch.setField("YD_DN_WO_ZAXIS_GAP_MIN", "" );
			
			
			/**********************************************************
			* 1.2. 크레인스케줄(좌표 등) 권하위치정보 갱신
			**********************************************************/
			intRtnVal = commDao.update(recUpCrnSch, updCrnschXY, logId, methodNm, "크레인스케줄(좌표 등) 권하위치정보 갱신");
			
			if( intRtnVal <= 0 ) {
				commUtils.printLog(logId, "["+ LocalmethodNm +"] 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단[" +szYS_DN_WO_LAYER +" ] : 크레인스케줄(좌표 등) 권하위치정보 갱신 중 ERROR 발생", "SL");
				sbImpPros.append("				 > 크레인스케줄(좌표 등) 권하위치정보 갱신]::크레인스케줄(좌표 등) 권하위치정보 갱신 중 ERROR 발생 szYS_DN_WO_LOC [" + szYS_DN_WO_LOC + "], szYS_DN_WO_LAYER [" + szYS_DN_WO_LAYER + "]\r\n");
				
				return YsConstant.RETN_CD_FAILURE;
			}
			
			commUtils.printLog(logId, "["+ LocalmethodNm +"] 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단["+szYS_DN_WO_LAYER+"] : 크레인스케줄(좌표 등) 권하위치정보 갱신 완료", "SL");
			sbImpPros.append("				[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 주/보조작업TO위치결정 > TO위치UPDATE > 크레인스케줄(좌표 등) 권하위치정보 갱신]::종료 \r\n");
			
		//"권하지시위치"가 없으면...
		} else {
			
			commUtils.printLog(logId, "["+ LocalmethodNm +"] 크레인스케줄(좌표 등) 권하위치정보 갱신::권하지시위치 없음: szYS_DN_WO_LOC [" + szYS_DN_WO_LOC + "]", "SL");
			sbImpPros.append("				 > 크레인스케줄(좌표 등) 권하위치정보 갱신::권하지시위치 없음: szYS_DN_WO_LOC [" + szYS_DN_WO_LOC + "] \r\n");
			
			return YsConstant.RETN_CD_FAILURE;
		}
		
		
		/**********************************************************
		* 2. 권상위치 작업재료번호들을 조회하여  
		*    적치단(TB_YS_STKLYR)의 권하지시위치에 '작업재료', '재료상태' 변경
		**********************************************************/
		commUtils.printLog(logId, "["+ LocalmethodNm +"] 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단["+szYS_DN_WO_LAYER+"]에 크레인작업재료 등록 시작", "SL");
		sbImpPros.append("				[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 주/보조작업TO위치결정 > TO위치UPDATE > 권하지시위치,권하지시단에 크레인작업재료 등록]::시작 \r\n");

		JDTORecordSet rsOutBed 	= JDTORecordFactory.getInstance().createRecordSet("");		
		JDTORecord    resOutBed = JDTORecordFactory.getInstance().create();
		
		//4.1. 권상위치 "작업재료"들을 조회
		recInBed= JDTORecordFactory.getInstance().create();
		recInBed.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID                            );
		recInBed.setField("YS_STK_COL_GP", szYS_UP_WO_LOC.substring(0, 6)             );
		recInBed.setField("YS_STK_BED_NO", szYS_UP_WO_LOC.substring(6)                );
		recInBed.setField("YS_STK_LYR_NO", commUtils.stringPlusInt(szYS_UP_WO_LAYER,0));
		
		rsOutBed = commDao.select(recInBed, getStklyrInfoByCrnsch, logId, methodNm, "권상위치 작업재료 조회");

		if( rsOutBed.size() <= 0 ) {
				commUtils.printLog(logId, "["+ LocalmethodNm +"] 권상위치 작업재료 조회 실패", "SL");
		}
		
		
		JDTORecordSet rsMaxSeq 	 = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord    recPara	 = JDTORecordFactory.getInstance().create();
		JDTORecord    jrParam	 = JDTORecordFactory.getInstance().create();
		
		//2.2. 권상위치 작업재료 각각에 대해
		//     적치단(TB_YS_STKLYR)에서 "최대SEQ+1" 위치의 권하지시위치를 찾아서
		//     '작업재료[권상지시위치-작업재료번호]','야드적치단재료상태[D:권하대기]'로 변경
		for( int i = 1; i <= rsOutBed.size(); i++ ) {
			
			rsOutBed.absolute(i);
			resOutBed = rsOutBed.getRecord();
			
			//2.2.1. 현재 TO위치(권하위치)에 쌓여있는 재료의 "최대SEQ" 조회
			//       : 크레인스케줄(권상위치) 작업재료들은 이적 후 TO위치에서의 SEQ가 변경되어야 함 
			//         [TO위치(권하위치)에 적치된 재료의 존재 유무에 따라 이적할 재료들의 SEQ가 영향을 받아 변경되어야 함]
			//         따라서 현재 TO위치(권하위치)에 쌓여있는 재료의 "최대SEQ"를 찾아서 +1 하여 Update 하여야 함
			jrParam.setField("YS_STK_COL_GP", szYS_DN_WO_LOC.substring(0, 6)              );
			jrParam.setField("YS_STK_BED_NO", szYS_DN_WO_LOC.substring(6)                 );
			jrParam.setField("YS_STK_LYR_NO", commUtils.stringPlusInt(szYS_DN_WO_LAYER, 0));
			
			rsMaxSeq = commDao.select(jrParam, getStklyrMaxSeqNo, logId, methodNm, "TO위치(권하위치)에 쌓여있는 재료의 '최대SEQ' 조회");
			
			rsMaxSeq.first();
			recPara = rsMaxSeq.getRecord();
			
			commUtils.printLog(logId, LocalmethodNm + " :: YS_STK_SEQ_NO_NEXT[" + commUtils.trim(recPara.getFieldString("YS_STK_SEQ_NO_NEXT")) + "] >> 권상위치 작업재료매수["+rsOutBed.size()+"]", "SL");
			
			jrParam.setField("MODIFIER"           , commUtils.trim(szMODIFIER                                  ));
			jrParam.setField("SSTL_NO"            , commUtils.trim(resOutBed.getFieldString("SSTL_NO"         )));
			jrParam.setField("YD_STK_LYR_MTL_STAT", "D"                                                         );	//[D:권하대기]
			jrParam.setField("YS_STK_SEQ_NO"      , commUtils.trim(recPara.getFieldString("YS_STK_SEQ_NO_NEXT")));
			
			intRtnVal = commDao.update(jrParam, updStkLyrToStlNoStat, logId, methodNm, "TB_YS_STKLYR 갱신");
			
			if( intRtnVal <= 0 ) {
				commUtils.printLog(logId, "[" + LocalmethodNm + "] 적치단[" + jrParam.getFieldString("YS_STK_COL_GP") + "]활성화중 ERROR 발생", "SL");
				sbImpPros.append("				 > 권하지시위치,권하지시단에 크레인작업재료 등록::적치단 활성화 중 ERROR 발생 YS_STK_COL_GP [" + jrParam.getFieldString("YS_STK_COL_GP") + "]\r\n");
				
				return YsConstant.RETN_CD_FAILURE;
			}
		}
			
		commUtils.printLog(logId, "["+ LocalmethodNm +"] 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단["+szYS_DN_WO_LAYER+"]에 크레인작업재료 등록 완료 - 메세지 : " + YsConstant.RETN_CD_SUCCESS, "SL");
		sbImpPros.append("				[크레인스케줄 > TO위치결정 > 크레인스케줄별 권하지시위치결정 > 주/보조작업TO위치결정 > TO위치UPDATE > 권하지시위치,권하지시단에크레인작업재료등록]::종료 \r\n");
		
		commUtils.printLog(logId, methodNm, "S-");
		
		return YsConstant.RETN_CD_SUCCESS;
	}

	/**
	 *      [A] 오퍼레이션명 : 작업예약/스케줄 생성 로그
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void insSchLog(JDTORecord jrParam) throws DAOException {
		String methodNm = "작업예약/스케줄 생성 로그 [SbrYsSchSeEJBBean.insSchLog] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "004");
			
			commUtils.printLog(logId,  "SCHLOG기록 적용여부: [" + sRuleItem + "] 시작.", "SL");
			
			if( "Y".equals(sRuleItem) ) {
				
				String aParamVal[] = jrParam.getFieldString("PARAM_VALUE").split("#");
				
				commUtils.printLog(logId,  "insSchLog >> aParamVal : " + java.util.Arrays.toString(aParamVal), "SL");
				
				JDTORecord    jrSchlog  = JDTORecordFactory.getInstance().create();
				jrSchlog.setField("YD_WRK_GP"    , aParamVal[0]);	//작업구분: 작업예약등록[WB], 스케줄생성[CS]
				jrSchlog.setField("YD_GP"        , aParamVal[1]);	//야드구분
				jrSchlog.setField("YD_BAY_GP"    , aParamVal[2]);	//야드동구분ID
				jrSchlog.setField("YD_WBOOK_ID"  , aParamVal[3]);	//야드작업예약ID
				jrSchlog.setField("YD_CRN_SCH_ID", aParamVal[4]);	//야드크레인스케쥴ID
				jrSchlog.setField("SORT_SEQ"     , aParamVal[5]);	//로그순번
				jrSchlog.setField("SCH_PROG_CNTS", aParamVal[6]);	//단계별진행내용
				jrSchlog.setField("SCH_CONTENTS" , aParamVal[7]);	//단계별항목정보: [logId],[재료번호],[적치열],[적치BED],[적치단],[적치SEQ],[야드To위치Guide],[야드스케줄코드],[야드작업계획크레인]
				
				commDao.update(jrSchlog, insYSSchlog, logId, methodNm, "작업예약/스케줄 생성 로그");
			}
				
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
}