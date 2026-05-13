/**
 * @(#)PSlabSchSeEJBBean
 *
 * @version          V1.00
 * @author           염용선
 * @date             2020/05/06
 *
 * @description      Slab야드 Schedule 처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2020/05/06   염용선      염용선      최초 등록
 */
package com.inisteel.cim.yd.pSlabYd.session;

import java.util.Vector;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.pSlabCommon.util.PSlabYdUtils;
import com.inisteel.cim.yd.pSlabYd.dao.PSlabYdCommDAO;

/**
 *      [A] 클래스명 : Slab야드 Schedule 처리
 *
 * @ejb.bean name="PSlabYdSchSeEJB" jndi-name="PSlabYdSchSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class PSlabYdSchSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private PSlabYdUtils slabUtils = new PSlabYdUtils();
	private PSlabYdComm   slabComm = new PSlabYdComm();
	private PSlabYdCommDAO commDao = new PSlabYdCommDAO();
	
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
	 *      [A] 오퍼레이션명 : 크레인스케줄(YDYDJ400 >> YDYDJ401로 코드 변경)
	 *      염용선 2020 06 11
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYDYDJ401(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인스케줄[PSlabYdSchSeEJB.rcvYDYDJ401] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printParam(logId, rcvMsg, "SL");

			//수신 항목 값
			String msgId      = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydWbookId  = slabUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"  )); //야드작업예약ID
			String ydSchCd    = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"    )); //야드스케쥴코드
			String ydEqpId    = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    )); //야드설비ID
			String ydSchStGp  = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_ST_GP" )); //야드스케쥴기동구분
			String ydSchReqGp = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_REQ_GP")); //야드스케쥴요청구분
			String modifier   = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"   )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			if ("".equals(ydSchStGp)) { ydSchStGp = "A"; }		//값이 없으면 Auto작업
			if ("".equals(ydSchReqGp)) { ydSchReqGp = "0"; }	//값이 없으면 모름

			slabUtils.printLog(logId, "후판슬라브-스케쥴코드[" + ydSchCd + "], 설비ID[" + ydEqpId + "], 작업예약ID[" + ydWbookId + "], 수정자[" + modifier + "]", "SL");

			JDTORecord jrRtn = slabUtils.getParam(logId, methodNm,modifier );	//전문 Return
			String trtGp   = "";		//처리구분
			String trtMsg  = ""; 		//처리메세지
			String ydL3Msg = ""; 		//야드L3MESSAGE
			String tmpStr  = "";		//임시변수
			String rtnCd   = "";
			String rtnMsg  = "";
			String query_id = "";
			
			//조회 및 등록용
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, modifier);
			jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
			jrParam.setField("YD_SCH_CD"  , ydSchCd  ); //야드스케쥴코드
			jrParam.setField("YD_EQP_ID"  , ydEqpId  ); //야드설비ID
			
			/**********************************************************
			* 1. 스케줄 정합성 Check
			* 1.1 작업예약ID조회
			**********************************************************/
			//스케줄코드 Check
			if ("".equals(ydWbookId) && !"".equals(ydSchCd)) {
				JDTORecord jrChk = slabComm.chkSchCd(jrParam);
				 rtnCd	 = slabUtils.nvl(jrChk.getFieldString("RTN_CD"), "0");				
				 rtnMsg	 = slabUtils.nvl(jrChk.getFieldString("RTN_MSG"), "");
				ydL3Msg  = slabUtils.trim(jrChk.getFieldString("YD_L3_MSG"));

				if (!"".equals(ydL3Msg)) {
					ydSchCd = "";
				}
			}

			//설비ID Check
			if ("".equals(ydWbookId) && "".equals(ydSchCd) && !"".equals(ydEqpId)) {
				JDTORecord jrChk = slabComm.chkEqpStat(jrParam);
				rtnCd	 = slabUtils.nvl(jrChk.getFieldString("RTN_CD"), "0");
				rtnMsg	 = slabUtils.nvl(jrChk.getFieldString("RTN_MSG"), "");
				ydL3Msg  = slabUtils.trim(jrChk.getFieldString("YD_L3_MSG"));

				if (!"".equals(ydL3Msg)) {
					ydEqpId = "";
				}
			}
			
			if (!"".equals(ydWbookId)) {
				//작업예약ID가 있으면
				query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchDAO.getYDYDJ401WbId";
				trtMsg = "작업예약ID조회 [작업예약ID : " + ydWbookId + "]";
			} else if (!"".equals(ydSchCd)) {
				//스케줄코드가 있으면
				query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchDAO.getYDYDJ401WbSchCd";
				trtMsg = "작업예약ID조회 [스케쥴코드 : " + ydSchCd + "]";
			} else if (!"".equals(ydEqpId)) {
				//설비ID가 있으면
				query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchDAO.getYDYDJ401WbEqpId";
				trtMsg = "작업예약ID조회 [야드설비ID : " + ydEqpId + "]";
			} else {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "오류:작업예약ID조회 항목 없음");
				return jrRtn;
			}
			
			slabUtils.printLog(logId, "["+trtMsg+"] query_id : "+query_id, "SL");
			
			//작업예약ID 조회
			/*크레인스케줄 작업예약-설비ID 조회 - com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchDAO.getYDYDJ400WbEqpId
			SELECT YD_WBOOK_ID
			  FROM (SELECT WB.YD_WBOOK_ID
			          FROM TB_YD_SCHRULE SR
			              ,TB_YD_WRKBOOK WB
			         WHERE SR.YD_SCH_CD       = WB.YD_SCH_CD
			           AND SR.YD_WRK_CRN      = :V_YD_EQP_ID
			           AND SR.YD_SCH_PROH_EXN = 'N'
			           AND SR.DEL_YN          = 'N'
			           AND WB.DEL_YN          = 'N'
			           AND WB.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
			                                        FROM TB_YD_CRNSCH
			                                       WHERE DEL_YN = 'N')
			         ORDER BY WB.YD_SCH_PRIOR, WB.YD_WBOOK_ID)
			 WHERE ROWNUM = 1
			 
			 크레인스케줄 작업예약-스케줄코드 조회 - com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchDAO.getYDYDJ400WbSchCd
				SELECT YD_WBOOK_ID
				  FROM (SELECT YD_WBOOK_ID
				          FROM TB_YD_WRKBOOK
				         WHERE YD_SCH_CD = :V_YD_SCH_CD
				           AND DEL_YN    = 'N'
				           AND YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
				                                     FROM TB_YD_CRNSCH
				                                    WHERE DEL_YN = 'N')
				         ORDER BY YD_SCH_PRIOR, YD_WBOOK_ID)
				 WHERE ROWNUM = 1

			 */
			JDTORecordSet jsChk = commDao.select(jrParam, query_id, logId, methodNm, trtMsg);
			
			if (jsChk.size() > 0) {
				ydWbookId = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_WBOOK_ID"));
			} else {
				slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약정보 없음", "SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", trtMsg + "오류:" + trtMsg + " >> 작업예약정보 없음");
				return jrRtn;
			}
			slabUtils.printLog(logId, trtMsg + " >> 작업예약ID조회 [" + ydWbookId + "]", "SL");

			jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID

			/**********************************************************
			* 1. 스케줄 적합성 Check
			* 1.2 권상모음순서 및 현재 적치단 저장위치 Update (별도 Transaction 으로 처리)
			**********************************************************/
			//EJBConnector tranConn = new EJBConnector("default", "PSlabSchSeEJB", this);
			//Object getRt = tranConn.trx("updCrnSchWB", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			/*
			 * SELECT WM.YD_STK_COL_GP
			
                    FROM TB_YD_WRKBOOKMTL WM
                    ,TB_YD_WRKBOOK WB
                   WHERE 1=1 
                     AND WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
                     AND WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
                     AND YD_UP_COLL_SEQ = '1'
             */
            JDTORecordSet jsStkColChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getStkColGp", logId, methodNm, "from 위치 조회");
            
            if (jsStkColChk.size() <= 0) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "오류: 현재  from위치 확인[예약ID : "+ydWbookId+"]");
				return jrRtn;
			} 
            JDTORecord jrStkColChk = jsStkColChk.getRecord(0);

			String ydStkColGp = slabUtils.trim(jrStkColChk.getFieldString("YD_STK_COL_GP")); //야드차량사용구분
			String queryId = "";
			/*
			--크레인스케줄 작업예약재료 저장위치 수정 --com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.updYDYDJ401WmStrLoc  
				MERGE INTO TB_YD_WRKBOOKMTL WM USING (
				SELECT YD_WBOOK_ID
				      ,STL_NO
				      ,:V_MODIFIER AS MODIFIER
				      ,SYSDATE     AS MOD_DDTT
				      ,YD_STK_COL_GP
				      ,YD_STK_BED_NO
				      ,YD_STK_LYR_NO
				      ,ROWNUM AS YD_UP_COLL_SEQ
				  FROM (SELECT WM.*
				               --이적 스케줄이고 장입번호가 2종류 이상일 경우 변경
				              ,CASE WHEN SUBSTR(WM.YD_SCH_CD,3,2) = 'YD' AND COUNT(DISTINCT WM.YD_CHG_NO) OVER () > 1 THEN
				                         NVL(WM.YD_CHG_NO
				                        ,NVL(CASE WHEN WM.YD_STK_COL_BED = LAG(WM.YD_STK_COL_BED,1) OVER (ORDER BY WM.YD_STR_LOC DESC)
				                                  THEN LAG(WM.YD_CHG_NO,1) OVER (ORDER BY WM.YD_STR_LOC DESC) END
				                        ,NVL(CASE WHEN WM.YD_STK_COL_BED = LAG(WM.YD_STK_COL_BED,2) OVER (ORDER BY WM.YD_STR_LOC DESC)
				                                  THEN LAG(WM.YD_CHG_NO,2) OVER (ORDER BY WM.YD_STR_LOC DESC) END
				                        ,NVL(CASE WHEN WM.YD_STK_COL_BED = LAG(WM.YD_STK_COL_BED,3) OVER (ORDER BY WM.YD_STR_LOC DESC)
				                                  THEN LAG(WM.YD_CHG_NO,3) OVER (ORDER BY WM.YD_STR_LOC DESC) END
				                        ,NVL(CASE WHEN WM.YD_STK_COL_BED = LEAD(WM.YD_STK_COL_BED,1) OVER (ORDER BY WM.YD_STR_LOC DESC)
				                                  THEN LEAD(WM.YD_CHG_NO,1) OVER (ORDER BY WM.YD_STR_LOC DESC) END
				                        ,NVL(CASE WHEN WM.YD_STK_COL_BED = LEAD(WM.YD_STK_COL_BED,2) OVER (ORDER BY WM.YD_STR_LOC DESC)
				                                  THEN LEAD(WM.YD_CHG_NO,2) OVER (ORDER BY WM.YD_STR_LOC DESC) END
				                        ,NVL(CASE WHEN WM.YD_STK_COL_BED = LEAD(WM.YD_STK_COL_BED,3) OVER (ORDER BY WM.YD_STR_LOC DESC)
				                                  THEN LEAD(WM.YD_CHG_NO,3) OVER (ORDER BY WM.YD_STR_LOC DESC) END
				                        ,999999)))))))
				                   ELSE WM.YD_UP_COLL_SEQ END AS YD_CHG_NO_SEQ
				          FROM (SELECT WB.YD_WBOOK_ID
				                      ,WB.YD_SCH_CD
				                      ,WM.STL_NO
				                      ,WM.YD_UP_COLL_SEQ
				                      ,SL.YD_STK_COL_GP
				                      ,SL.YD_STK_BED_NO
				                      ,SL.YD_STK_LYR_NO
				                      ,SL.YD_STK_COL_GP||SL.YD_STK_BED_NO||SL.YD_STK_LYR_NO AS YD_STR_LOC
				                      ,SL.YD_STK_COL_GP||SL.YD_STK_BED_NO AS YD_STK_COL_BED
				                      ,CASE WHEN ST.YD_STK_LOT_CD LIKE 'C%' THEN TO_NUMBER(SUBSTR(ST.YD_STK_LOT_CD,3)) END AS YD_CHG_NO
				                  FROM TB_YD_WRKBOOK WB
				                      ,TB_YD_WRKBOOKMTL WM
				                      ,TB_YD_STKLYR     SL
				                      ,TB_YD_STOCK      ST
				                 WHERE WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
				                   AND WM.STL_NO      = SL.STL_NO
				                   AND WM.STL_NO      = ST.STL_NO
				                   AND WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
				                   AND WB.DEL_YN      = 'N'
				                   AND WM.DEL_YN      = 'N'
				                   AND NVL(SL.DEL_YN,'*')  <> 'Y'  --추가 : 2016.01.20 
				                   AND SL.YD_STK_LYR_MTL_STAT = 'C'
				                   AND WB.YD_GP=SUBSTR(SL.YD_STK_COL_GP,1,1)
				                   --AND  SL.YD_STK_COL_GP = :V_YD_STK_COL_GP  --대차일때만 비교
				                 ORDER BY YD_STR_LOC DESC) WM
				         ORDER BY YD_CHG_NO_SEQ, YD_STR_LOC DESC)
				) DD ON (WM.YD_WBOOK_ID = DD.YD_WBOOK_ID AND WM.STL_NO = DD.STL_NO)
				WHEN MATCHED THEN UPDATE SET
				     WM.MODIFIER       = DD.MODIFIER
				    ,WM.MOD_DDTT       = DD.MOD_DDTT
				    ,WM.YD_STK_COL_GP  = DD.YD_STK_COL_GP
				    ,WM.YD_STK_BED_NO  = DD.YD_STK_BED_NO
				    ,WM.YD_STK_LYR_NO  = DD.YD_STK_LYR_NO
				    --,WM.YD_UP_COLL_SEQ = DD.YD_UP_COLL_SEQ
			 */
			queryId        = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.updYDYDJ401WmStrLoc";
			
			jrParam.setField("YD_STK_COL_GP", ydStkColGp); // from 위치
			
			slabUtils.printLog(logId, "YD_STK_COL_GP:" + ydStkColGp + " >> from위치확인", "SL");
			
			int intRtnVal = commDao.updateTx(jrParam, queryId, logId, methodNm, "작업예약재료(TB_YD_WRKBOOKMTL) 저장위치 수정");
			if (intRtnVal <= 0) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "오류: 현재 적치단 저장위치 확인[예약ID : "+ydWbookId+"]");
				return jrRtn;
			} 
			
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
			String dummyYn        = "";		//더미 이적 여부
			//HandlingLot분리구분(N:분리안함, R:목표행선분리, X:권하분리)
			String hdLotSprGpAw   = "N";	//보조작업
			String hdLotSprGpMw   = "N";	//주작업
			String maxLyrOvSh     = "0";	//Max단초과매수
			String crnSchLogYn    = "N";	//크레인스케줄Log여부
			String ydEqpStat      = "";		//야드설비상태
			//TakeInBed1매선보급요구여부(장입보급  공Bed가 없더라도 스케줄 생성 및 권하지시위치 XX010101로 Set)
			String tiPreSupYn     = "N";
			
			String interlockYn    = "N";
			String interlockWrkYn = "N";
			String upperWrkYn     = "N";	//상단재료 작업 여부
			String narrowSlabYn   = "N";	//협폭슬라브 2후판 4,5픽업 보급 여부
			
			trtMsg = "상태정보Check [작업예약ID : " + ydWbookId + "]";

			/*
			 * --크레인스케줄 상태정보 조회
				WITH TEMP_PARAM AS (
				    SELECT :V_YD_WBOOK_ID AS V_YD_WBOOK_ID FROM DUAL
				)
				SELECT WB.YD_GP                                   --야드구분
				      ,WB.YD_BAY_GP                               --야드동구분
				      ,WB.YD_SCH_CD                               --야드스케쥴코드
				      ,WB.YD_SCH_PRIOR                            --야드스케쥴우선순위
				      ,WB.YD_TO_LOC_DCSN_MTD                      --야드To위치결정방법
				      ,WB.YD_TO_LOC_GUIDE                         --야드To위치Guide
				      ,TO_CHAR(WB.REG_DDTT,'YYYYMMDDHH24MISS') AS YD_WBOOK_DT --야드작업예약일시
				      ,CASE WHEN WB.YD_SCH_CD LIKE '__PT__U_' THEN 'C' --차량상차
				            WHEN WB.YD_SCH_CD LIKE '__TC__U_' THEN 'T' --대차상차
				            WHEN LENGTH(WB.YD_TO_LOC_GUIDE) >= 4       --Span구분 이상이면
				             AND WB.YD_TO_LOC_GUIDE LIKE WB.YD_GP||WB.YD_BAY_GP||'%' THEN 'G' --To위치Guide
				            WHEN WB.YD_SCH_CD LIKE '__PU__U_'
				              OR WB.YD_SCH_CD LIKE '__DP__U_' THEN 'E' --불출
				      	    ELSE 'Z'                                   --기타
				      	END AS TO_LOC_CHK_GP                      --To위치점검구분
				      ,BR.HD_LOT_SPR_GP_AW                        --HandlingLot분리구분(보조작업)
				      ,BR.HD_LOT_SPR_GP_MW                        --HandlingLot분리구분(주작업)
				      ,BR.MAX_LYR_OV_SH                           --Max단초과매수
				      ,BR.CRN_SCH_LOG_YN                          --크레인스케줄Log여부
				      ,SR.YD_SCH_PROH_EXN                         --야드스케쥴금지유무
				      ,WB.YD_WRK_PLAN_CRN                         --야드작업계획크레인
				      ,E0.YD_EQP_STAT      AS YD_EQP_STAT_PLN     --작업계획크레인 야드설비상태
				      ,E0.YD_EQP_WRK_MODE  AS YD_EQP_WRK_MODE_PLN --작업계획크레인 야드설비작업Mode
				      ,SR.YD_WRK_CRN                              --야드작업크레인
				      ,SR.YD_WRK_CRN_PRIOR                        --야드작업크레인우선순위
				      ,E1.YD_EQP_STAT      AS YD_EQP_STAT_WRK     --작업크레인 야드설비상태
				      ,E1.YD_EQP_WRK_MODE  AS YD_EQP_WRK_MODE_WRK --작업크레인 야드설비작업Mode
				      ,SR.YD_ALT_CRN                              --야드대체크레인
				      ,SR.YD_ALT_CRN_PRIOR                        --야드대체크레인우선순위
				      ,E2.YD_EQP_STAT      AS YD_EQP_STAT_ALT     --대체크레인 야드설비상태
				      ,E2.YD_EQP_WRK_MODE  AS YD_EQP_WRK_MODE_ALT --대체크레인 야드설비작업Mode
				      ,NVL(WM.TT_MTL_SH,0) AS TT_MTL_SH           --전체 재료매수
				      ,NVL(WM.WM_MTL_SH,0) AS WM_MTL_SH           --작업예약 재료매수
				      ,NVL(WM.ST_MTL_SH,0) AS ST_MTL_SH           --저장품 재료매수
				      ,NVL(WM.SL_MTL_SH,0) AS SL_MTL_SH           --적치단 재료매수
				      ,NVL(WM.STAT_C_SH,0) AS STAT_C_SH           --적치중인 재료매수
				      ,(SELECT COUNT(*)
				          FROM TB_YD_WRKBOOKMTL WM
				              ,TB_YD_STKLYR     SL
				         WHERE WM.STL_NO      = SL.STL_NO
				           AND WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
				           AND SL.YD_STK_COL_GP NOT LIKE SUBSTR(WB.YD_SCH_CD,1,2)||'%'
				           AND SL.YD_STK_LYR_MTL_STAT = 'C'
				           AND WM.DEL_YN      = 'N'
				           AND SL.DEL_YN      = 'N') AS AB_LOC_SH --저장위치이상 재료매수
				      ,(SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END
				          FROM TB_YD_WRKBOOKMTL WM
				              ,TB_YD_CRNSCH     CS
				              ,TB_YD_CRNWRKMTL  CM
				         WHERE WM.STL_NO        = CM.STL_NO
				           AND CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
				           AND WM.YD_WBOOK_ID   = WB.YD_WBOOK_ID
				           AND WM.DEL_YN        = 'N'
				           AND CM.DEL_YN        = 'N'
				           AND CS.DEL_YN        = 'N') AS CM_DUP_YN --크레인스케줄 재료중복여부
				      ,(SELECT MIN(CASE WHEN SL.YD_STK_COL_GP||SL.YD_STK_BED_NO = CS.YD_DN_WO_LOC THEN '1'
				                        WHEN SL.YD_STK_COL_GP||SL.YD_STK_BED_NO = CS.YD_UP_WO_LOC
				                         AND CS.YD_WRK_PROG_STAT != '2' THEN '2' END)
				          FROM TB_YD_WRKBOOKMTL WM
				              ,TB_YD_STKLYR     SL
				              ,TB_YD_CRNSCH     CS
				         WHERE WM.STL_NO      = SL.STL_NO
				           AND SL.YD_STK_COL_GP||SL.YD_STK_BED_NO IN (CS.YD_UP_WO_LOC, CS.YD_DN_WO_LOC)
				           AND WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
				           AND WM.DEL_YN      = 'N'
				           
				           AND SL.DEL_YN      = 'N'
				           AND CS.DEL_YN      = 'N') AS CL_DUP_GP --크레인스케줄 저장위치중복여부
				      ,WB.YD_CAR_USE_GP                           --야드차량사용구분
				      ,WB.TRN_EQP_CD                              --운송장비코드
				      ,WB.CAR_NO                                  --차량번호
				      ,WB.CARD_NO                                 --카드번호
				      ,(SELECT MIN(ST.YD_AIM_RT_GP)
				          FROM TB_YD_WRKBOOKMTL BM
				              ,TB_YD_STOCK      ST
				         WHERE BM.STL_NO         = ST.STL_NO
				           AND BM.YD_WBOOK_ID    = WM.YD_WBOOK_ID
				           AND BM.YD_UP_COLL_SEQ = WM.YD_UP_COLL_SEQ) AS YD_AIM_RT_GP --야드목표행선구분
				      ,(SELECT YD_CURR_BAY_GP
				          FROM TB_YD_EQP EQ
				         WHERE EQ.YD_EQP_ID = SUBSTR(WB.YD_SCH_CD,1,1)||'XTC'||SUBSTR(WB.YD_SCH_CD,5,2)) AS YD_CURR_BAY_GP
				      
				      ,NVL((SELECT C2CR_INTERLOCK_YN FROM TB_YD_EQP WHERE YD_EQP_ID = 'AACRA1'),'N') AS INTERLOCK_YN
				      ,CASE WHEN USRYDA.SF_YD_C2CR_INTERLOCK_WRK(WB.YD_TO_LOC_GUIDE) = 'Y' OR INTERLOCK_SH >0 THEN 'Y'
				            ELSE 'N' END AS INTERLOCK_WRK_YN
				      
				      
				      ,( SELECT CASE WHEN SUM(UPPER_WRK_YN) >0 THEN 'Y' ELSE 'N' END 
				          FROM (
				            SELECT  --+ index(a OI_YD_TB_YD_WRKBOOKMTL_ACTIVE)
				                     (
				                      SELECT COUNT(*) 
				                      FROM TB_YD_WRKBOOKMTL WB 
				                     WHERE 1=1
				                       AND WB.DEL_YN = 'N'
				                       AND WB.YD_STK_COL_GP||'' = WM.YD_STK_COL_GP 
				                       AND WB.YD_STK_BED_NO = WM.YD_STK_BED_NO 
				                       AND WB.YD_STK_LYR_NO>WM.YD_STK_LYR_NO
				                       AND WB.YD_WBOOK_ID <> (SELECT V_YD_WBOOK_ID FROM TEMP_PARAM)
				                       ) AS UPPER_WRK_YN
				              FROM TB_YD_WRKBOOKMTL WM
				             WHERE 1=1
				               AND WM.YD_WBOOK_ID =(SELECT V_YD_WBOOK_ID FROM TEMP_PARAM)
				                )
				   
				      ) AS UPPER_WRK_YN1 --현재 작업예약재료 상단에 작업예약걸린 재료가 있는지
				      ,'N' AS UPPER_WRK_YN
				      ,CASE WHEN NARROW_SLAB_SH >0 AND (YD_TO_LOC_GUIDE LIKE 'DBPU04%' OR YD_TO_LOC_GUIDE LIKE 'DBPU05%') THEN 'Y'
				            ELSE 'N' END AS NARROW_SLAB_YN
				  FROM TB_YD_WRKBOOK WB
				      ,TB_YD_SCHRULE SR
				      ,TB_YD_EQP     E0
				      ,TB_YD_EQP     E1
				      ,TB_YD_EQP     E2
				      ,(SELECT WM.YD_WBOOK_ID
				              ,COUNT(*)                  AS TT_MTL_SH
				              ,COUNT(DISTINCT WM.STL_NO) AS WM_MTL_SH
				              ,COUNT(DISTINCT ST.STL_NO) AS ST_MTL_SH
				              ,COUNT(DISTINCT SL.STL_NO) AS SL_MTL_SH
				              ,SUM(DECODE(SL.YD_STK_LYR_MTL_STAT,'C',1)) AS STAT_C_SH --적치중인 재료매수
				              ,MAX(WM.YD_UP_COLL_SEQ)    AS YD_UP_COLL_SEQ
				              ,SUM(DECODE(USRYDA.SF_YD_C2CR_INTERLOCK_WRK(WM.YD_STK_COL_GP),'Y',1,0)) AS INTERLOCK_SH--인터락 구간 권상위치 재료매수
				              ,SUM (CASE WHEN (SELECT REAL_MEASURE_SLAB_W FROM VW_YD_SLABCOMM SC WHERE SC.SLAB_NO = ST.STL_NO) <1475 THEN 1
				                    ELSE 0 END ) AS NARROW_SLAB_SH
				          FROM TB_YD_WRKBOOKMTL WM
				              ,TB_YD_STOCK      ST
				              ,TB_YD_STKLYR     SL
				         WHERE WM.STL_NO      = ST.STL_NO(+)
				           AND WM.STL_NO      = SL.STL_NO(+)
				           AND WM.YD_WBOOK_ID = :V_YD_WBOOK_ID
				           AND WM.DEL_YN      = 'N'
				           AND ST.DEL_YN(+)   = 'N'
				           AND SL.DEL_YN(+)   = 'N'
				         GROUP BY WM.YD_WBOOK_ID) WM
				      ,(SELECT YR.CD_GP AS  YD_GP
				           ,MIN(DECODE(YR.DTL_ITEM1,'HD_LOT_SPR_GP_AW',YR.ITEM_VALUE2)) AS HD_LOT_SPR_GP_AW --HandlingLot분리구분(보조작업)
				           ,MIN(DECODE(YR.DTL_ITEM1,'HD_LOT_SPR_GP_MW',YR.ITEM_VALUE2)) AS HD_LOT_SPR_GP_MW --HandlingLot분리구분(주작업)
				           ,MIN(DECODE(YR.DTL_ITEM1,'CRN_SCH_LOG_YN'  ,YR.ITEM_VALUE2)) AS CRN_SCH_LOG_YN   --크레인스케줄Log여부
				           ,MIN(DECODE(YR.DTL_ITEM1,'MAX_LYR_OV_SH'   ,YR.ITEM_VALUE2)) AS MAX_LYR_OV_SH    --Max단초과매수
				        FROM USRYDA.TB_YD_RULE YR
				          WHERE 1=1
				             AND YR.CD_GP = 'D'
				             AND YR.DEL_YN = 'N'
				             AND YR.REPR_CD_GP = 'DYD100'
				         GROUP BY YR.CD_GP) BR -- VW_YD_YDB030 테이블을 TB_YD_RULE  테이블로 변경
				 WHERE WB.YD_SCH_CD       = SR.YD_SCH_CD(+)
				   AND SR.YD_WRK_CRN      = E1.YD_EQP_ID(+)
				   AND SR.YD_ALT_CRN      = E2.YD_EQP_ID(+)
				   AND WB.YD_WRK_PLAN_CRN = E0.YD_EQP_ID(+)
				   AND WB.YD_WBOOK_ID     = WM.YD_WBOOK_ID(+)
				   AND WB.YD_GP           = BR.YD_GP
				   AND WB.YD_WBOOK_ID     = (SELECT V_YD_WBOOK_ID FROM TEMP_PARAM)
				   AND WB.DEL_YN          = 'N'
				   AND SR.DEL_YN(+)       = 'N'
				   AND E1.DEL_YN(+)       = 'N'
				   AND E2.DEL_YN(+)       = 'N'
				   AND E0.DEL_YN(+)       = 'N'


			 */
			jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYDYDJ401Stat", logId, methodNm, "크레인스케줄 상태정보 조회");
    	
			if (jsChk.size() <= 0) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> 상태정보 없음");
				return jrRtn;
			} else {
				JDTORecord jrChk = jsChk.getRecord(0);
				
				String logItem = "YD_GP;YD_BAY_GP;YD_SCH_CD;YD_SCH_PRIOR;YD_TO_LOC_DCSN_MTD;YD_TO_LOC_GUIDE;YD_WBOOK_DT;TO_LOC_CHK_GP;HD_LOT_SPR_GP_AW;"
					           + "HD_LOT_SPR_GP_MW;MAX_LYR_OV_SH;CRN_SCH_LOG_YN;YD_SCH_PROH_EXN;YD_WRK_PLAN_CRN;YD_EQP_STAT_PLN;YD_EQP_WRK_MODE_PLN;"
					           + "YD_WRK_CRN;YD_WRK_CRN_PRIOR;YD_EQP_STAT_WRK;YD_EQP_WRK_MODE_WRK;YD_ALT_CRN;YD_ALT_CRN_PRIOR;YD_EQP_STAT_ALT;YD_EQP_WRK_MODE_ALT;"
					           + "TT_MTL_SH;WM_MTL_SH;ST_MTL_SH;SL_MTL_SH;STAT_C_SH;AB_LOC_SH;CM_DUP_YN;CL_DUP_GP;YD_CAR_USE_GP;TRN_EQP_CD;CAR_NO;CARD_NO;YD_AIM_RT_GP;YD_CURR_BAY_GP";
				slabUtils.printParam(logId + " 크레인스케줄 상태정보 조회", jrChk, logItem);
				
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
				
				int ttMtlSh = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("TT_MTL_SH"),"0"));	//전체 재료매수
				int wmMtlSh = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("WM_MTL_SH"),"0"));	//작업예약 재료매수
				int stMtlSh = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("ST_MTL_SH"),"0"));	//저장품 재료매수
				int slMtlSh = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("SL_MTL_SH"),"0"));	//적치단 재료매수
				int statCSh = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("STAT_C_SH"),"0"));	//적치중인 재료매수(야드적치단재료상태가 적치 중[C] 인 재료수)
				int abLocSh = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("AB_LOC_SH"),"0"));	//저장위치이상 재료매수
				
				interlockYn            = slabUtils.nvl(jrChk.getFieldString("INTERLOCK_YN"),"N"); 		//인터락 구간 여부
				interlockWrkYn         = slabUtils.nvl(jrChk.getFieldString("INTERLOCK_WRK_YN"),"N");	//인터락 구간 작업 여부
				upperWrkYn             = slabUtils.nvl(jrChk.getFieldString("UPPER_WRK_YN"),"N");		//상단재료 작업예약 여부
				dummyYn         	   = slabUtils.trim(jrChk.getFieldString("YD_SCH_REQ_GP"      ));	//더미 이적 여부 ("Y":더미이적)
				slabUtils.printLog(logId, " 더미 이적 여부('D':더미이적) : " + dummyYn, "SL");
				
				 /* 협폭재 작업가능하게 
				  *  com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYDYDJ401Stat
				  *  위 쿼리에서 상태값을 Y 로 변경 : 2021-06-09 YYS				 
				 */
				narrowSlabYn           = slabUtils.nvl(jrChk.getFieldString("NARROW_SLAB_YN"),"N");//협폭슬라브 2후판 4,5픽업 보급 여부
				
				//야드스케쥴우선순위는 작업예약 우선순위로 변경
				//String ydWrkCrnPrior   = slabUtils.trim(jrChk.getFieldString("YD_WRK_CRN_PRIOR"   ));	//야드작업크레인우선순위
				//String ydAltCrnPrior   = slabUtils.trim(jrChk.getFieldString("YD_ALT_CRN_PRIOR"   ));	//야드대체크레인우선순위
				
				slabUtils.printLog(logId, ydWbookId + " 후판슬라브스케쥴점검결과", "SL");
				
				if ("".equals(ydSchProhExn)) {
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 스케쥴코드[" + ydSchCd + "] 정보 없음", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", trtMsg + "오류:" + trtMsg + " >> 스케쥴코드[" + ydSchCd + "] 정보 없음");
					return jrRtn;
				} else if ("Y".equals(ydSchProhExn)) {
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 스케쥴코드[" + ydSchCd + "] 기동금지", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", trtMsg + "오류:" + trtMsg + " >> 스케쥴코드[" + ydSchCd + "] 정보 없음");
					return jrRtn;
				} else if (wmMtlSh == 0) {
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약재료 정보 없음", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", trtMsg + "오류:" + trtMsg + " >> 작업예약재료 정보 없음");
					return jrRtn;
				} else if (wmMtlSh != ttMtlSh) {
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약재료 적치단 중복 등록 [작업예약: " + wmMtlSh + ", 적치단: " + ttMtlSh + "]", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", trtMsg + "오류:" + trtMsg + " >> 작업예약재료 적치단 중복 등록 [작업예약: " + wmMtlSh + ", 적치단: " + ttMtlSh + "]");
					return jrRtn;
				} else if (wmMtlSh != slMtlSh) {
					slabUtils.printLog(logId, trtMsg +"오류:" + trtMsg + " >> 작업예약재료 적치단 정보 이상 [" + (wmMtlSh - slMtlSh) + "매]", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", trtMsg + "오류:" + trtMsg + " >> 작업예약재료 적치단 정보 이상 [" + (wmMtlSh - slMtlSh) + "매]");
					return jrRtn;
				} else if (wmMtlSh != statCSh) {
					slabUtils.printLog(logId, trtMsg +"오류:" + trtMsg + " >> 작업예약재료 적치중[C]이 아님 [" + (wmMtlSh - statCSh) + "매]", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", trtMsg + "오류:" + trtMsg + " >> 작업예약재료 적치중[C]이 아님 [" + (wmMtlSh - statCSh) + "매]");
					return jrRtn;
				} else if (wmMtlSh != stMtlSh) {
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약재료 저장품 정보 이상 [" + (wmMtlSh - stMtlSh) + "매]", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", trtMsg + "오류:" + trtMsg + " >> 작업예약재료 저장품 정보 이상 [" + (wmMtlSh - stMtlSh) + "매]");
					return jrRtn;
				} else if (abLocSh > 0) {
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약재료의 현재위치 이상 [" + abLocSh + "매]", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", trtMsg + "오류:" + trtMsg + " >> 작업예약재료의 현재위치 이상 [" + abLocSh + "매]");
					return jrRtn;
				} else if ("Y".equals(cmDupYn)) {
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약재료가 기 등록된 크레인작업재료와 중복", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", trtMsg + "오류:" + trtMsg + " >> 작업예약재료가 기 등록된 크레인작업재료와 중복");
					return jrRtn;
				} else if ("1".equals(clDupGp)) {
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권하위치와 중복", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", trtMsg + "오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권하위치와 중복");
					return jrRtn;
				} else if ("2".equals(clDupGp)) {
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권상위치와 중복", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", trtMsg + "오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권상위치와 중복");
					return jrRtn;
				} else if ("Y".equals(interlockYn) &&"Y".equals(interlockWrkYn)) {
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약재료의 권상위치 혹은 To위치가 A1크레인 Interlock 구간", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", trtMsg + "오류:" + trtMsg + " >> 작업예약재료의 권상위치 혹은 To위치가 A1크레인 Interlock 구간");
					return jrRtn;
				} else if("Y".equals(upperWrkYn)){
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 작업예약재료 상단에 다른 작업예약 걸려있는 재료 존재", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", trtMsg + "오류:" + trtMsg + " >> 작업예약재료 상단에 다른 작업예약 걸려있는 재료 존재");
					return jrRtn;
				} else if("Y".equals(narrowSlabYn)){
					slabUtils.printLog(logId, trtMsg + "오류:" + trtMsg + " >> 협폭 슬라브 2후판 4,5번 픽업 Bed 사용 불가", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", trtMsg + "오류:" + trtMsg + " >> 협폭 슬라브 2후판 4,5번 픽업 Bed 사용 불가");
					return jrRtn;
				}

				
				//크레인 결정
				//"야드작업계(지정)크레인" 상태 체크
				if ( (!"".equals(ydWrkPlanCrn) && !"B".equals(ydEqpStatPln) && ("1".equals(ydEqpWrkModePln) || "4".equals(ydEqpWrkModePln) || "5".equals(ydEqpWrkModePln)))  ) {
					//작업예약 지정크레인 : 최우선 지정
					ydEqpId   = ydWrkPlanCrn;	//야드설비ID
					ydEqpStat = ydEqpStatPln;	//야드설비상태
					slabUtils.printLog(logId, trtMsg + " >> 작업예약 지정크레인[" + ydWrkPlanCrn + "]으로 설정", "SL");
					
				//"야드작업(주)크레인" 상태 체크
				} else if (!"".equals(ydWrkCrn) && !"B".equals(ydEqpStatWrk) && ("1".equals(ydEqpWrkModeWrk) || "4".equals(ydEqpWrkModeWrk) || "5".equals(ydEqpWrkModeWrk))) {
					//주작업크레인
					ydEqpId   = ydWrkCrn;		//야드설비ID
					ydEqpStat = ydEqpStatWrk;	//야드설비상태
					slabUtils.printLog(logId, trtMsg + " >> 주작업크레인[" + ydWrkCrn + "]으로 설정", "SL");
					
				//"야드대체크레인" 상태 체크
				} else {
					//보조작업(대체)크레인 : 주작업크레인이 고장이거나 Off-Line 이면
					String tmpMsg = "주작업 크레인[" + ydWrkCrn + "] ";
					
					if ("B".equals(ydEqpStatWrk)) {
						tmpMsg = tmpMsg + "고장";
					} else if ("0".equals(ydEqpWrkModeWrk)) {
						tmpMsg = tmpMsg + "Off-Line";
					} else if ("4".equals(ydEqpWrkModeWrk)) {
						tmpMsg = tmpMsg + "일시정지";
					} else if ("5".equals(ydEqpWrkModeWrk)) {
						tmpMsg = tmpMsg + "비상정지";
					}
					
					if ("".equals(ydAltCrn)) {
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> " + tmpMsg + ", 보조작업(대체) 크레인 정보 없음");
						return jrRtn;
					} else if ("B".equals(ydEqpStatAlt)) {
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> " + tmpMsg + ", 보조작업(대체) 크레인[" + ydAltCrn + "] 고장");
						return jrRtn;
					} else if ("0".equals(ydEqpWrkModeAlt)) {
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> " + tmpMsg + ", 보조작업(대체) 크레인[" + ydAltCrn + "] Off-Line");
						return jrRtn;
					}
					
					slabUtils.printLog(logId, trtMsg + " >> " + tmpMsg + "이므로 보조작업 크레인[" + ydAltCrn + "]으로 대체", "SL");
					slabUtils.printLog(logId, "크레인[" + ydEqpId + "]으로 대체", "SL");
				}
								

				//To위치 사전 점검
				if ("C".equals(toLocChkGp)) {
					//차량상차작업
					String ydCarUseGp = slabUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP"));	//야드차량사용구분
				           trnEqpCd   = slabUtils.trim(jrChk.getFieldString("TRN_EQP_CD"   ));	//운송장비코드
					String carNo      = slabUtils.trim(jrChk.getFieldString("CAR_NO"       ));	//차량번호
					String cardNo     = slabUtils.trim(jrChk.getFieldString("CARD_NO"      ));	//카드번호

					if ("".equals(ydCarUseGp)) {
						//throw new Exception("오류:" + trtMsg + " >> 차량상차작업 야드차량사용구분 없음");
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> 차량상차작업 야드차량사용구분 없음");
						return jrRtn;
					} else if ("L".equals(ydCarUseGp) && "".equals(trnEqpCd)) { //"L" : 구내운송
						//throw new Exception("오류:" + trtMsg + " >> 구내운송 상차작업 운송장비코드 없음");
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> 구내운송 상차작업 운송장비코드 없음");
						return jrRtn;
					} else if ("G".equals(ydCarUseGp)) { //출하 (후판슬라브에 없음)
						if ("".equals(carNo) || "".equals(cardNo)) {
							//throw new Exception("오류:" + trtMsg + " >> 출하차량 상차작업 차량번호 또는 카드번호 없음");
							jrRtn.setField("RTN_CD"	, "0");
							jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> 출하차량 상차작업 차량번호 또는 카드번호 없음");
							return jrRtn;
						} else {
							trnEqpCd = carNo + "-" + cardNo;
						}
					}

				} else if ("E".equals(toLocChkGp) || "Z".equals(toLocChkGp)) {
					//설비보급 또는 기타
					ydAimRtGp = slabUtils.trim(jrChk.getFieldString("YD_AIM_RT_GP"));	//야드목표행선구분

					if ("".equals(ydAimRtGp)) {
						//throw new Exception("오류:" + trtMsg + " >> 설비보급 또는 기타작업 야드목표행선구분 정보 없음");
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> 설비보급 또는 기타작업 야드목표행선구분 정보 없음");
						return jrRtn;
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
					 
				} else if (!"0".equals(ydToLocGuide.substring(2, 3))) {
					//설비Bed 이적
					hdLotSprGpMw = "N";
				}
			} else if (!"Z".equals(toLocChkGp)) {
				//대차상차, 차량상차, 설비보급
				hdLotSprGpMw = "N";
			}
			
			jrParam.setField("YD_EQP_ID"         , ydEqpId       ); //야드설비ID
			jrParam.setField("YD_SCH_CD"         , ydSchCd       ); //야드스케쥴코드
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
			slabUtils.printLog(logId, trtMsg + " To위치점검구분(toLocChkGp) : " + toLocChkGp, "SL");
			if ("G".equals(toLocChkGp)) {
				//야드To위치Guide 값이 있고 작업 야드동이 같을 경우 야드To위치Guide로 
				//PU, DP, PI 불출위치에 재료가 있거나, 단수, 중량 초과이면 불가
				trtGp  = "ToLocGuide";
				trtMsg = "To위치점검[To위치지정 : " + ydToLocGuide + "]";
				query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYDYDJ401ToLocGuide";
			} else if ("C".equals(toLocChkGp)) {
				//차량상차(__PT__U_)일 경우 적치가능 차량이 없으면 불가
				trtGp  = "ToLocCar";
				trtMsg = "To위치점검[차량상차 : " + ydSchCd + ", " + trnEqpCd + "]";
				query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYDYDJ401ToLocCar";
			} else if ("E".equals(toLocChkGp)) {
				//스케줄코드 및 행선구분으로 To위치 점검
				//불출(__PU__U_, __DP__U_)이고 위치검색범위에 적치매수 0인 Bed가 없으면 불가
				trtGp  = "ToLocExt";
				trtMsg = "To위치점검[설비보급 : " + ydSchCd + ", " + ydAimRtGp + "]";
				query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYDYDJ401ToLocExt";
			} else if ("Z".equals(toLocChkGp)) {
				//스케줄코드 및 행선구분으로 To위치 점검
				trtGp  = "ToLocEtc";
				trtMsg = "To위치점검[기타 : " + ydSchCd + ", " + ydAimRtGp + "]";
				query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYDYDJ401ToLocEtc";
			} //2022.04.27 L3 추가 PUNCHLIST 20번.
			  else if ("T".equals(toLocChkGp)) {
				//스케줄코드 및 행선구분으로 To위치 점검
				trtGp  = "ToLocTCar";
				trtMsg = "To위치점검[대차상차 : " + ydSchCd + ", " + ydAimRtGp + "]";
				query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getYDYDJ401ToLocTCar";
			}
			
			if (!"".equals(trtGp)) {
				String toLocChkRst = ""; //To위치점검결과

				/*크레인스케줄 To위치점검-Guide 조회 - com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYDYDJ401ToLocGuide 
				SELECT CASE WHEN YD_SCH_CD IN ('DAYD01M2','DAYD03M2','DAYD04M2','DBYD03M2','DBYD04M2') AND TO_LOC_CHK_RST = 'G2' THEN 'OK'
				            ELSE TO_LOC_CHK_RST END AS TO_LOC_CHK_RST
				      ,YD_STK_COL_GP||YD_STK_BED_NO AS YD_TO_LOC_GUIDE_NEW
				      ,NVL((SELECT NVL(SR.ITEM1,'N')
				              FROM TB_YD_RULE SR
				             WHERE SR.REPR_CD_GP = WB.YD_STK_COL_GP
				               AND SR.CD_GP = '*'
				               AND SR.ITEM  = '2'),'N') AS TI_PRE_SUP_YN --TakeInBed1매선보급요구여부
				      ,CNT_STL          
				  FROM (SELECT CASE WHEN MIN(WB.EQP_BED_YN) = 'Y' AND COUNT(ST.STL_NO) > 0                         THEN 'G1' --공Bed 아님
				                    WHEN MIN(WB.EQP_BED_YN) = 'Y' AND MIN(WB.SAME_COL_YN) = 'Y'                    THEN 'G5' --재료위치, 권하위치 동일
				                    WHEN MIN(SB.YD_STK_BED_LYR_MAX) < MIN(WB.YD_MTL_SH) + COUNT(ST.STL_NO)         THEN 'G2' --매수 초과
				                    WHEN MIN(SB.YD_STK_BED_WT_MAX ) < MIN(WB.YD_MTL_WT) + NVL(SUM(ST.YD_MTL_WT),0) THEN 'G3' --중량 초과
				                    WHEN MIN(SB.YD_STK_BED_H_MAX  ) < MIN(WB.YD_MTL_T ) + NVL(SUM(ST.YD_MTL_T ),0) THEN 'G4' --높이 초과
				                    ELSE 'OK'
				                END AS TO_LOC_CHK_RST --설비 공Bed 및 Bed사양
				              ,SB.YD_STK_COL_GP
				              ,SB.YD_STK_BED_NO
				              ,COUNT(ST.STL_NO) AS CNT_STL
				              ,WB.YD_SCH_CD
				          FROM TB_YD_STKBED SB
				              ,TB_YD_STKLYR SL
				              ,TB_YD_STOCK ST
				              ,(SELECT SUBSTR(WB.YD_TO_LOC_GUIDE,1,6) AS YD_STK_COL_GP
				                      ,SUBSTR(WB.YD_TO_LOC_GUIDE,7,2) AS YD_STK_BED_NO
				                      ,COUNT(*)                       AS YD_MTL_SH
				                      ,SUM(ST.YD_MTL_WT)              AS YD_MTL_WT
				                      ,SUM(ST.YD_MTL_T )              AS YD_MTL_T
				                      --,CASE WHEN SUBSTR(WB.YD_TO_LOC_GUIDE,3,2) IN ('PU','DP','PI') THEN 'Y' ELSE 'N' END AS EQP_BED_YN  --//PU베드 진행반에서 장입순서책임 관리
				                      ,CASE WHEN WB.YD_GP = 'M' THEN 'N'
				                            WHEN SUBSTR(WB.YD_TO_LOC_GUIDE,3,2) IN ('DP','PI') THEN 'Y' ELSE 'N' END AS EQP_BED_YN
				                      ,MIN((SELECT   DECODE(SL.YD_STK_COL_GP,SUBSTR(WB.YD_TO_LOC_GUIDE,1,6),'Y')
				                              FROM TB_YD_STKLYR SL
				                             WHERE SL.STL_NO = WM.STL_NO
				                               AND SL.DEL_YN = 'N'
				                               AND SL.YD_STK_LYR_MTL_STAT = 'C')) AS SAME_COL_YN --현 재료위치가 권하위치와 동일열
				                      ,WB.YD_SCH_CD --스케줄코드
				                  FROM TB_YD_WRKBOOK   WB
				                      ,TB_YD_WRKBOOKMTL WM
				                      ,TB_YD_STOCK   ST
				                 WHERE WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
				                   AND WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                   AND WM.STL_NO      = ST.STL_NO
				                 GROUP BY WB.YD_TO_LOC_GUIDE, WB.YD_SCH_CD, WB.YD_GP) WB
				         WHERE SB.YD_STK_COL_GP LIKE WB.YD_STK_COL_GP||'%'
				           AND SB.YD_STK_BED_NO LIKE DECODE(WB.EQP_BED_YN,'N',WB.YD_STK_BED_NO,SB.YD_STK_BED_NO)||'%'
				           AND SB.YD_STK_COL_GP = SL.YD_STK_COL_GP
				           AND SB.YD_STK_BED_NO = SL.YD_STK_BED_NO
				           AND SL.STL_NO        = ST.STL_NO(+)
				         GROUP BY SB.YD_STK_COL_GP, SB.YD_STK_BED_NO, WB.YD_SCH_CD
				         ORDER BY DECODE(TO_LOC_CHK_RST,'OK','G0',TO_LOC_CHK_RST) --적치가능, 공Bed 순
				                , DECODE(SUBSTR(YD_STK_COL_GP,3,2),'PU',CNT_STL,1) --공BED 최우선
				                , DECODE(SB.YD_STK_BED_NO,MIN(WB.YD_STK_BED_NO),'00',SB.YD_STK_BED_NO)) WB
				 WHERE ROWNUM = 1 
				 */				
				JDTORecordSet jsChkToLoc = commDao.select(jrParam, query_id, logId, methodNm, "To위치점검결과 조회");

				if (jsChkToLoc.size() > 0) {

					slabUtils.printParam(logId + " To위치점검결과 조회", jsChkToLoc, "TO_LOC_CHK_RST;YD_TO_LOC_GUIDE_NEW;TI_PRE_SUP_YN;CNT_STL");
					
					toLocChkRst = slabUtils.trim(jsChkToLoc.getRecord(0).getFieldString("TO_LOC_CHK_RST"));

					slabUtils.printLog(logId, " To위치점검결과(toLocChkRst) 변경전 : "+toLocChkRst, "SL");
					
					//To위치지정일 경우
					if ("G".equals(toLocChkGp)) {
						//대체할 Bed가 있어도 OK
						if ("OK".equals(toLocChkRst)) {
							String ydToLocGuideNew = "";
							
							if (!"0".equals(ydToLocGuide.substring(2, 3))) {
//								if("HD".equals(ydToLocGuide.substring(2,4))) {
//									JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
//									JDTORecord jrChk    = JDTORecordFactory.getInstance().create();
//									
//									jrParam2.setField("YD_STK_COL_GP", ydToLocGuide.substring(0,6));
//									
//									/*
//									 * com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getY3YDL009EmpBed 
//										SELECT *
//										FROM (
//										    SELECT COUNT(*) AS CNT
//										          ,YD_STK_COL_GP
//										          ,YD_STK_BED_NO
//										    FROM TB_YD_STKLYR
//										    WHERE DEL_YN = 'N'
//										    AND YD_STK_COL_GP = :V_YD_STK_COL_GP
//										    AND YD_STK_LYR_MTL_STAT = 'E'
//										    AND STL_NO IS NULL
//										    GROUP BY YD_STK_COL_GP, YD_STK_BED_NO
//										    ORDER BY CNT DESC, YD_STK_COL_GP, ABS(NVL(:V_YD_STK_BED_NO,'00')-YD_STK_BED_NO)
//										)
//										WHERE ROWNUM <= 1
//									 */
//									JDTORecordSet jsChk2 = commDao.select(jrParam2, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getY3YDL009EmpBed", logId, methodNm, "비어있는 베드 조회");
//							    	
//									if(jsChk2.size() > 0) {
//										jrChk = jsChk2.getRecord(0);
//										ydToLocGuideNew = slabUtils.trim(jrChk.getFieldString("YD_STK_COL_GP"))+slabUtils.trim(jrChk.getFieldString("YD_STK_BED_NO"));
//									}
//								} else {
									//To위치지정 점검에서 설비 Bed 적치불가이면 무조건 Error에서 대체 Bed로 변경
									ydToLocGuideNew = slabUtils.trim(jsChkToLoc.getRecord(0).getFieldString("YD_TO_LOC_GUIDE_NEW"));
//								}
							} else if (ydToLocGuide.length() < 8) {
								//To위치 값이 8자리가 아니면 범위내에서 Bed를 선택(권하위치 기준 적용)
								//JDTORecordSet jsNew = schDao.getYDYDJ400("ToLocGuideNew", jrParam);
								/*
								 *크레인스케줄 신규To위치Guide 조회 - com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401ToLocGuideNew
									SELECT YD_DN_WO_LOC AS YD_TO_LOC_GUIDE_NEW --야드권하지시위치
									  FROM (SELECT YD_DN_WO_LOC
									              ,TO_LOC_LMT_SH
									              ,CASE WHEN TO_LOC_EVP_SLC  < LMT_SLC  THEN 0 ELSE TO_LOC_EVP_SLC  * SEQ_SLC  END +
									               CASE WHEN TO_LOC_EVP_MW   < LMT_MW   THEN 0 ELSE TO_LOC_EVP_MW   * SEQ_MW   END +
									               CASE WHEN TO_LOC_EVP_SLC2 < LMT_SLC2 THEN 0 ELSE TO_LOC_EVP_SLC2 * SEQ_SLC2 END +
									               CASE WHEN TO_LOC_EVP_RT   < LMT_RT   THEN 0 ELSE TO_LOC_EVP_RT   * SEQ_RT   END +
									               TO_LOC_EVP_EMP * SEQ_EMP AS TO_LOC_EVP
									              ,SF_SLAB_YD_BED_GAP(YD_UP_WO_LOC_XAXIS, YD_UP_WO_LOC_YAXIS, YD_DN_WO_LOC_XAXIS, YD_DN_WO_LOC_YAXIS) AS BED_GAP --BedGap
									          FROM (SELECT SB.YD_STK_COL_GP||SB.YD_STK_BED_NO AS YD_DN_WO_LOC
									                      ,MIN(SB.YD_STK_BED_XAXIS)           AS YD_DN_WO_LOC_XAXIS
									                      ,MIN(SB.YD_STK_BED_YAXIS)           AS YD_DN_WO_LOC_YAXIS
									                      ,MIN(DD.YD_UP_WO_LOC_XAXIS)         AS YD_UP_WO_LOC_XAXIS
									                      ,MIN(DD.YD_UP_WO_LOC_YAXIS)         AS YD_UP_WO_LOC_YAXIS
									                      --적치불가능Bed
									                      ,CASE WHEN SUBSTR(SB.YD_STK_COL_GP,3,2) BETWEEN '01' AND '99' THEN 'G'
									                      	    ELSE CASE WHEN COUNT(ST.STL_NO) > 0 THEN 'N' ELSE 'E' END
									                       END AS EQP_BED_GP --설비Bed구분(G:일반,E:공설비Bed,N:적치불가능Bed)
									                      ,SUM(CASE WHEN SL.YD_STK_LYR_MTL_STAT IN ('U','X') THEN 1 ELSE 0 END) AS STK_IMP_LYR_CNT --적치불가능단수
									                      ,CASE WHEN MIN(SB.YD_STK_BED_LYR_MAX) < MIN(DD.YD_EQP_WRK_SH) + COUNT(ST.STL_NO)         THEN 'SH'
									                            WHEN MIN(SB.YD_STK_BED_WT_MAX ) < MIN(DD.YD_EQP_WRK_WT) + NVL(SUM(ST.YD_MTL_WT),0) THEN 'WT'
									                            WHEN MIN(SB.YD_STK_BED_H_MAX  ) < MIN(DD.YD_EQP_WRK_T ) + NVL(SUM(ST.YD_MTL_T ),0) THEN 'T'
									                       ELSE 'N' END     AS BED_SPEC_OV_GP --Bed사양초과구분
									                      ,SUM(CASE WHEN CS.YD_TO_LOC_DCSN_MTD NOT IN ('S','W') THEN 0
									                      	        WHEN WM.STL_NO IS NOT NULL THEN 1 ELSE 0 END) AS WB_MTL_SH --작업예약재료매수(다른 스케줄 최종 제외)
									                      --To위치평점
									                      ,NVL(SUM(CASE WHEN DD.CRN_YD_STK_LOT_TP IN ('SL','SP') AND DD.CRN_YD_STK_LOT_TP = ST.YD_STK_LOT_TP THEN
									                                    CASE WHEN TO_NUMBER(SUBSTR(ST.YD_STK_LOT_CD,3)) < TO_NUMBER(SUBSTR(DD.CRN_YD_STK_LOT_CD,3)) THEN 1 END
									                               ELSE CASE WHEN ST.YD_AIM_RT_GP LIKE 'C%' THEN 1 END END),0) AS TO_LOC_LMT_SH --적치제한재료수
									                      ,NVL(DECODE(COUNT(ST.STL_NO),0,0,
									                           ROUND(SUM(DECODE(DD.CRN_YD_STK_LOT_CD,ST.YD_STK_LOT_CD,100))
									                                / COUNT(ST.STL_NO))),0) AS TO_LOC_EVP_SLC  --산적Lot코드평점
									                      ,NVL(DECODE(COUNT(ST.STL_NO),0,0,
									                           ROUND(SUM(CASE WHEN ST.YD_MTL_W BETWEEN DD.CRN_YD_MTL_W_MIN AND DD.CRN_YD_MTL_W_MAX THEN 100 END)
									                           	    / COUNT(ST.STL_NO))),0) AS TO_LOC_EVP_MW   --재료폭평점
									                      ,NVL(DECODE(COUNT(ST.STL_NO),0,0,
									                           ROUND(SUM(SF_SLAB_YD_TO_LOC_EVP_SLC(DD.CRN_YD_STK_LOT_TP, DD.CRN_YD_STK_LOT_CD, DD.CRN_SLAB_WO_RT_CD,
									                                                               ST.YD_STK_LOT_TP    , ST.YD_STK_LOT_CD    , ST.SLAB_WO_RT_CD    ))
									                                / COUNT(ST.STL_NO))),0) AS TO_LOC_EVP_SLC2 --산적Lot코드부분평점
									                      ,NVL(DECODE(COUNT(ST.STL_NO),0,0,
									                           ROUND(SUM(DECODE(DD.CRN_YD_AIM_RT_GP,ST.YD_AIM_RT_GP,100))
									                                / COUNT(ST.STL_NO))),0) AS TO_LOC_EVP_RT   --목표행선평점
									                      ,DECODE(COUNT(ST.STL_NO),0,100,0) AS TO_LOC_EVP_EMP  --공Bed평점
									                      ,MIN(DD.SEQ_SLC ) AS SEQ_SLC
									                      ,MIN(DD.SEQ_MW  ) AS SEQ_MW
									                      ,MIN(DD.SEQ_SLC2) AS SEQ_SLC2
									                      ,MIN(DD.SEQ_RT  ) AS SEQ_RT
									                      ,MIN(DD.SEQ_EMP ) AS SEQ_EMP
									                      ,MIN(DD.LMT_SLC ) AS LMT_SLC
									                      ,MIN(DD.LMT_MW  ) AS LMT_MW
									                      ,MIN(DD.LMT_SLC2) AS LMT_SLC2
									                      ,MIN(DD.LMT_RT  ) AS LMT_RT
									                  FROM TB_YD_STKBED     SB
									                      ,TB_YD_STKLYR     SL
									                      ,TB_YD_STOCK      ST
									                      ,TB_YD_WRKBOOK    WB
									                      ,TB_YD_WRKBOOKMTL WM
									                      ,TB_YD_CRNSCH     CS
									                      ,TB_YD_CRNWRKMTL  CM
									                      ,(SELECT DD.YD_SCH_CD
									                              ,DD.YD_STK_COL_GP
									                              ,DD.YD_STK_BED_NO
									                              ,DD.YD_EQP_WRK_SH                                            --야드설비작업매수
									                              ,DD.YD_EQP_WRK_WT                                            --야드설비작업중량
									                              ,DD.YD_EQP_WRK_T                                             --야드설비작업총두께
									                              ,DD.YD_STK_LOT_TP                       AS CRN_YD_STK_LOT_TP --야드산적LotType
									                              ,DD.YD_STK_LOT_CD                       AS CRN_YD_STK_LOT_CD --야드산적Lot코드
									                              ,DD.YD_AIM_RT_GP                        AS CRN_YD_AIM_RT_GP  --야드목표행선구분
									                              ,DD.SLAB_WO_RT_CD                       AS CRN_SLAB_WO_RT_CD --Slab지시행선코드
									                              ,DD.YD_MTL_L                            AS CRN_YD_MTL_L      --야드재료길이
									                              ,DD.YD_MTL_W - CS.YD_CRN_TONG_W_TOL / 2 AS CRN_YD_MTL_W_MIN  --야드재료폭Min
									                              ,DD.YD_MTL_W + CS.YD_CRN_TONG_W_TOL / 2 AS CRN_YD_MTL_W_MAX  --야드재료폭Max
									                              ,CASE WHEN BR.YD_WFACT1 IN ('1','2','3','4','5') THEN POWER(10,(5-TO_NUMBER(BR.YD_WFACT1))*3) ELSE 0 END AS SEQ_SLC  --야드산적Lot코드순서
									                              ,CASE WHEN BR.YD_WFACT2 IN ('1','2','3','4','5') THEN POWER(10,(5-TO_NUMBER(BR.YD_WFACT2))*3) ELSE 0 END AS SEQ_MW   --야드재료폭순서
									                              ,CASE WHEN BR.YD_WFACT3 IN ('1','2','3','4','5') THEN POWER(10,(5-TO_NUMBER(BR.YD_WFACT3))*3) ELSE 0 END AS SEQ_SLC2 --야드산적Lot코드2순서
									                              ,CASE WHEN BR.YD_WFACT4 IN ('1','2','3','4','5') THEN POWER(10,(5-TO_NUMBER(BR.YD_WFACT4))*3) ELSE 0 END AS SEQ_RT   --야드목표행선구분순서
									                              ,CASE WHEN BR.YD_WFACT5 IN ('1','2','3','4','5') THEN POWER(10,(5-TO_NUMBER(BR.YD_WFACT5))*3) ELSE 0 END AS SEQ_EMP  --공Bed순서
									                              ,NVL(TO_NUMBER(BR.SEGMENT1),0) AS LMT_SLC  --야드산적Lot코드제한비율
									                              ,NVL(TO_NUMBER(BR.SEGMENT2),0) AS LMT_MW   --야드재료폭제한비율
									                              ,NVL(TO_NUMBER(BR.SEGMENT3),0) AS LMT_SLC2 --야드산적Lot코드2제한비율
									                              ,NVL(TO_NUMBER(BR.SEGMENT4),0) AS LMT_RT   --야드목표행선구분제한비율
									                              ,YD_UP_WO_LOC_XAXIS
									                              ,YD_UP_WO_LOC_YAXIS
									                          FROM VW_YD_YDB031 BR --가중치
									                              ,(SELECT WB.YD_SCH_CD
									                                      ,SUBSTR(WB.YD_TO_LOC_GUIDE,1,6) AS YD_STK_COL_GP
									                                      ,SUBSTR(WB.YD_TO_LOC_GUIDE,7,2) AS YD_STK_BED_NO
									                                      ,COUNT(*)          OVER ()      AS YD_EQP_WRK_SH
									                                      ,SUM(ST.YD_MTL_WT) OVER ()      AS YD_EQP_WRK_WT
									                                      ,SUM(ST.YD_MTL_T ) OVER ()      AS YD_EQP_WRK_T
									                                      ,ST.YD_STK_LOT_TP
									                                      ,ST.YD_STK_LOT_CD
									                                      ,ST.YD_AIM_RT_GP
									                                      ,ST.SLAB_WO_RT_CD
									                                      ,ST.YD_MTL_L
									                                      ,ST.YD_MTL_W
									                                      ,SB.YD_STK_BED_XAXIS AS YD_UP_WO_LOC_XAXIS
									                                      ,SB.YD_STK_BED_YAXIS AS YD_UP_WO_LOC_YAXIS
									                                      ,ROW_NUMBER() OVER (ORDER BY WM.YD_UP_COLL_SEQ DESC) AS RN
									                                  FROM TB_YD_WRKBOOK    WB
									                                      ,TB_YD_WRKBOOKMTL WM
									                                      ,TB_YD_STOCK      ST
									                                      ,TB_YD_STKBED     SB
									                                      ,TB_YD_STKLYR     SL
									                                 WHERE WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
									                                   AND WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
									                                   AND WM.STL_NO      = ST.STL_NO
									                                   AND ST.STL_NO      = SL.STL_NO
									                                   AND SL.YD_STK_LYR_MTL_STAT IN ( 'C','D')
									                                   AND SL.YD_STK_COL_GP = SB.YD_STK_COL_GP
									                                   AND SL.YD_STK_BED_NO = SB.YD_STK_BED_NO) DD
									                              ,(SELECT CS.YD_CRN_TONG_W_TOL --야드크레인집게폭허용오차
									                                  FROM TB_YD_CRNSPEC CS
									                                 WHERE CS.YD_EQP_ID = :V_YD_EQP_ID
									                                   AND CS.DEL_YN    = 'N') CS
									                         WHERE DD.RN            = 1
									                           AND BR.YD_GP         = SUBSTR(DD.YD_SCH_CD,1,1)
									                           AND BR.YD_STK_LOT_TP = DD.YD_STK_LOT_TP) DD
									                 WHERE SB.YD_STK_COL_GP LIKE DD.YD_STK_COL_GP||'%'
									                   AND SB.YD_STK_BED_NO LIKE DD.YD_STK_BED_NO||'%'
									                   AND SB.YD_STK_COL_GP = SL.YD_STK_COL_GP
									                   AND SB.YD_STK_BED_NO = SL.YD_STK_BED_NO
									                   AND SL.STL_NO        = ST.STL_NO(+)
									                   AND SL.STL_NO        = WM.STL_NO(+)
									                   AND 'N'              = WM.DEL_YN(+)
									                   AND WM.YD_WBOOK_ID   = WB.YD_WBOOK_ID(+)
									                   AND 'N'              = WB.DEL_YN(+)
									                   AND SL.STL_NO        = CM.STL_NO(+)
									                   AND 'N'              = CM.DEL_YN(+)
									                   AND CM.YD_CRN_SCH_ID = CS.YD_CRN_SCH_ID(+)
									                   AND 'N'              = CS.DEL_YN(+)
									                   AND SB.DEL_YN        = 'N'
									                   AND SB.YD_STK_BED_ACT_STAT  = 'L' --적치가능
									                   AND SB.YD_STK_BED_WHIO_STAT = 'E' --입출고가능(완산Bed제외)
									                   AND SB.YD_STK_BED_USG_GP   != 'S' --수입구제외
									                 GROUP BY SB.YD_STK_COL_GP, SB.YD_STK_BED_NO)
									         WHERE STK_IMP_LYR_CNT = 0   --적치불가능단 없음
									           AND BED_SPEC_OV_GP  = 'N' --Bed사양 초과 안함
									           AND WB_MTL_SH       = 0   --작업예약재료 없음
									           AND EQP_BED_GP     != 'N' --재료가 있는 설비Bed 제외
									         ORDER BY TO_LOC_LMT_SH, TO_LOC_EVP DESC, BED_GAP, YD_DN_WO_LOC)
									 WHERE ROWNUM = 1
								 */
								JDTORecordSet jsNew = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401ToLocGuideNew", logId, methodNm, "신규To위치Guide 조회");
								
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
							tiPreSupYn = slabUtils.trim(jsChkToLoc.getRecord(0).getFieldString("TI_PRE_SUP_YN"));
							if ("Y".equals(tiPreSupYn)) {
								toLocChkRst = "OK";
							}
						}
					}
				} else {
					toLocChkRst = toLocChkGp + "1";
				}
				
				slabUtils.printLog(logId, " To위치점검결과(toLocChkRst) 최종 : "+toLocChkRst, "SL");
				
				if ("G1".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> To위치 적치가능 Bed 없음");
					slabUtils.printLog(logId, "오류:" + trtMsg + " >> To위치 적치가능 Bed 없음", "SL");	
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> To위치 적치가능 Bed 없음");
					return jrRtn;
				} else if ("G2".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> To위치 Max단 초과");
					slabUtils.printLog(logId, "오류:" + trtMsg + " >> To위치 Max단 초과", "SL");	
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> To위치 Max단 초과");
					return jrRtn;
				} else if ("G3".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> To위치 Max중량 초과");
					slabUtils.printLog(logId, "오류:" + trtMsg + " >> To위치 Max중량 초과", "SL");	
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> To위치 Max중량 초과");
					return jrRtn;
				} else if ("G4".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> To위치 Max높이 초과");
					slabUtils.printLog(logId, "오류:" + trtMsg + " >> To위치 Max높이 초과", "SL");	
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> To위치 Max높이 초과");
					return jrRtn;
				} else if ("G5".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> To위치에 적치된 재료 있음");
					slabUtils.printLog(logId, "오류:" + trtMsg + " >> To위치에 적치된 재료 있음", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> To위치에 적치된 재료 있음");
					return jrRtn;
				} else if ("G6".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> To위치에 적치된 재료 있음");
					slabUtils.printLog(logId, "오류:" + trtMsg + " >> To위치에 길이 기준 만족 못함", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> To위치에 길이 기준 만족 못함");
					return jrRtn;
				} else if ("G7".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> To위치에 적치된 재료 있음");
					slabUtils.printLog(logId, "오류:" + trtMsg + " >> To위치 두께 기준 만족 못함", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> To위치 두께 기준 만족 못함");
					return jrRtn;
				} else if ("G8".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> To위치에 적치된 재료 있음");
					slabUtils.printLog(logId, "오류:" + trtMsg + " >> To위치 폭 기준 만족 못함", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> To위치 폭 기준 만족 못함");
					return jrRtn;
				}else if ("C1".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> 상차가능 차량 없음");
					slabUtils.printLog(logId, "오류:" + trtMsg + " >> 상차가능 차량 없음", "SL");	
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> 상차가능 차량 없음");
					return jrRtn;
				} else if ("E1".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> 적치가능 설비 공Bed 없음");
					slabUtils.printLog(logId,"오류:" + trtMsg + " >> 적치가능 설비 공Bed 없음", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> 적치가능 설비 공Bed 없음");
					return jrRtn;
				} else if ("Z1".equals(toLocChkRst)) {
					//throw new Exception("오류:" + trtMsg + " >> 적치가능 위치검색Bed 없음");
					slabUtils.printLog(logId,"오류:" + trtMsg + " >> 적치가능 위치검색Bed 없음", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "오류:" + trtMsg + " >> 적치가능 위치검색Bed 없음");				
					return jrRtn;
				}
	
				slabUtils.printLog(logId, trtMsg + " 완료", "SL");
			}
			
			/**********************************************************
			* 2. Handling Lot 편성 전 데이타 체크
			**********************************************************/
			// 1.크레인사양 check
			/*
			 * SELECT CS.YD_WRK_ABLE_SH    --야드작업가능매수			
				      ,CS.YD_WRK_ABLE_WT    --야드작업가능중량
				      ,CS.YD_CRN_TONG_H     --야드크레인집게높이
				      ,CS.YD_CRN_TONG_W_TOL --야드크레인집게폭허용오차
				  FROM TB_YD_CRNSPEC CS
				 WHERE CS.YD_EQP_ID = :V_YD_EQP_ID
				   AND CS.DEL_YN    = 'N'
		    */
			JDTORecordSet jsCrnSpec = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401CrnSpec", logId, methodNm, "크레인사양 조회");

			if (jsCrnSpec.size() <= 0) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "오류:크레인사양 조회 >> 조회 Data 없음");				
				return jrRtn;
			}
			
			slabUtils.printParam(logId + " 크레인사양 조회", jsCrnSpec, "YD_WRK_ABLE_SH;YD_WRK_ABLE_WT;YD_CRN_TONG_H;YD_CRN_TONG_W_TOL");
			
			JDTORecord jrCrnSpec = JDTORecordFactory.getInstance().create();
			jrCrnSpec.setResultCode(logId);		//Log ID
			jrCrnSpec.setResultMsg(methodNm);	//Log Method Name
			jrCrnSpec.setRecord(jsCrnSpec.getRecord(0));

			//야드크레인집게폭허용오차 : To위치결정 시 사용하기 위해 공통 Param에 Set
			jrParam.setField("YD_CRN_TONG_W_TOL", slabUtils.nvl(jrCrnSpec.getFieldString("YD_CRN_TONG_W_TOL"),"0"));
			
			//2.크레인스케줄 Bed사양  체크
			/* 크레인스케줄 Bed사양 조회 -  com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401BedSpec  
				SELECT WB.YD_STK_COL_GP||WB.YD_STK_BED_NO AS YD_UP_WO_LOC
				      ,MIN(SB.YD_STK_BED_LYR_MAX) AS YD_STK_BED_LYR_MAX
				      ,MIN(SB.YD_STK_BED_WT_MAX ) AS YD_STK_BED_WT_MAX
				      ,MIN(SB.YD_STK_BED_H_MAX  ) AS YD_STK_BED_H_MAX
				      ,COUNT(ST.STL_NO)           AS YD_MTL_SH_BASE
				      ,NVL(SUM(ST.YD_MTL_WT),0)   AS YD_MTL_WT_BASE
				      ,NVL(SUM(ST.YD_MTL_T ),0)   AS YD_MTL_T_BASE
				  FROM (SELECT YD_STK_COL_GP
				              ,YD_STK_BED_NO
				              ,MIN(YD_STK_LYR_NO) AS YD_STK_LYR_NO
				          FROM TB_YD_WRKBOOKMTL
				         WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				           AND DEL_YN      = 'N'
				         GROUP BY YD_STK_COL_GP, YD_STK_BED_NO) WB
				      ,TB_YD_STKLYR SL
				      --,TB_YD_STOCK  ST
				      ,(
				         SELECT ST.STL_NO
				        , DECODE(SC.REAL_MEASURE_SLAB_LEN,0,SC.SLAB_LEN, SC.REAL_MEASURE_SLAB_LEN) YD_MTL_LEN   -- (실측)길이
				        , DECODE(SC.REAL_MEASURE_SLAB_T,  0,SC.SLAB_T  , SC.REAL_MEASURE_SLAB_T  ) YD_MTL_T     -- (실측)두께
				        , DECODE(SC.REAL_MEASURE_SLAB_W,  0,SC.SLAB_W  , SC.REAL_MEASURE_SLAB_W  ) YD_MTL_W  -- (실측)폭
				        , DECODE(SC.CAL_SLAB_WT,          0,SC.SLAB_WT , SC.CAL_SLAB_WT          ) YD_MTL_WT -- (계산)중량
				           FROM USRYDA.TB_YD_STOCK    ST -- YD_저장품
				              , USRPTA.TB_PT_SLABCOMM SC -- SLAB공통
				          WHERE ST.STL_NO = SC.SLAB_NO
				      ) ST
	
				      ,TB_YD_STKBED SB
				 WHERE WB.YD_STK_COL_GP = SB.YD_STK_COL_GP
				   AND WB.YD_STK_BED_NO = SB.YD_STK_BED_NO
				   AND WB.YD_STK_COL_GP = SL.YD_STK_COL_GP(+)
				   AND WB.YD_STK_BED_NO = SL.YD_STK_BED_NO(+)
				   AND WB.YD_STK_LYR_NO > SL.YD_STK_LYR_NO(+)
				   AND SL.STL_NO        = ST.STL_NO(+)
				 GROUP BY WB.YD_STK_COL_GP, WB.YD_STK_BED_NO
			*/
            JDTORecordSet jsBedSpec = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401BedSpec", logId, methodNm, "크레인스케줄 Bed사양 조회");
			
			if (jsBedSpec.size() <= 0) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "오류:Bed사양 조회 >> 조회 Data 없음");				
				return jrRtn;
			}

			slabUtils.printParam(logId + " 크레인스케줄 Bed사양 조회", jsBedSpec, "YD_UP_WO_LOC;YD_STK_BED_LYR_MAX;YD_STK_BED_WT_MAX;YD_STK_BED_H_MAX;YD_MTL_SH_BASE;YD_MTL_WT_BASE;YD_MTL_T_BASE");
			
			//크레인작업재료 존재여부 체크 
            JDTORecordSet jsCrnMtl = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401CrnMtl", logId, methodNm, "크레인작업재료 조회");
			
			if (jsCrnMtl.size() <= 0) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "오류:크레인작업재료조회 >> 조회 Data 없음");				
				return jrRtn;
			}
			
			//크레인스케줄 Handling Lot 편성
			Vector vcLot = this.setSlabYdHdLot(jrParam, jrCrnSpec, jsBedSpec);
			
			
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
			float  fYdEqpWrkMinW   = 9999;		//야드설비작업최대폭
			int    iYdEqpWrkMaxL   = 0;		//야드설비작업최대길이
			int    iYdEqpWrkMinL   = 9999;		//야드설비작업최소길이
			float  fYdEqpWrkMaxT   = 0;		//야드설비작업최대두께
			float  fYdEqpWrkMinT   = 999;		//야드설비작업최소두께
			float  fYdMtlT         = 0;     //야드재료두께
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
			String[][] lastMtl = new String[schCnt][3];		//스케줄마지막재료 정보
			float [][] ydMtlWArr = new float[schCnt][2];    //야드재료폭 배열 0 : 최대두께  1: 최소두께
			float [][] ydMtlTArr = new float[schCnt][2];    //야드재료두께 배열 0 : 최대두께  1: 최소두께
			int [][] ydMtlLArr = new int[schCnt][2];    //야드재료길이 배열 0 : 최대길이  1: 최소길이
			//권상지시위치(현재위치) 정보 조회
			//jsChk = schDao.getYDYDJ400("CurrLoc", jrParam);
			/*
			 * SELECT A.*
				      ,DECODE(YD_UP_WO_LOC,'AC020101',3000,'AC020801',3000,YD_UP_WO_XAXIS_GAP2) AS YD_UP_WO_XAXIS_GAP
				      ,DECODE(YD_UP_WO_LOC,'AC020101',3000,'AC020801',3000,YD_UP_WO_YAXIS_GAP2) AS YD_UP_WO_YAXIS_GAP
				      ,DECODE(YD_UP_WO_LOC,'AC020101',3000,'AC020801',3000,YD_UP_WO_ZAXIS_GAP2) AS YD_UP_WO_ZAXIS_GAP
				FROM
				(
				SELECT SL.YD_STK_COL_GP||SL.YD_STK_BED_NO       AS YD_UP_WO_LOC       --야드권상지시위치
				      ,SL.YD_STK_LYR_NO                         AS YD_UP_WO_LAYER     --야드권상지시단
				      ,SL.STL_NO                                                      --재료번호
				      ,SB.YD_STK_BED_XAXIS                      AS YD_UP_WO_LOC_XAXIS --야드권상지시X축
				      ,SF_SLAB_YD_CRN_GAP(SB.YD_STK_COL_GP,'X') AS YD_UP_WO_XAXIS_GAP2 --야드권상지시X축오차
				      ,SB.YD_STK_BED_YAXIS                      AS YD_UP_WO_LOC_YAXIS --야드권상지시Y축
				      ,SF_SLAB_YD_CRN_GAP(SB.YD_STK_COL_GP,'Y') AS YD_UP_WO_YAXIS_GAP2 --야드권상지시Y축오차
				      ,ROUND(SUM(ST.YD_MTL_T) OVER (PARTITION BY SL.YD_STK_COL_GP, SL.YD_STK_BED_NO ORDER BY SL.YD_STK_LYR_NO) - ST.YD_MTL_T) AS YD_UP_WO_LOC_ZAXIS --야드권상지시Z축
				      ,SF_SLAB_YD_CRN_GAP(SB.YD_STK_COL_GP,'Z') AS YD_UP_WO_ZAXIS_GAP2 --야드권상지시Z축오차
				  FROM TB_YD_STKLYR SL
				      ,TB_YD_STKBED SB
				      ,TB_YD_STOCK  ST
				      ,(SELECT YD_STK_COL_GP
				              ,YD_STK_BED_NO
				          FROM TB_YD_WRKBOOKMTL
				         WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				           AND DEL_YN      = 'N'
				         GROUP BY YD_STK_COL_GP, YD_STK_BED_NO) WM
				 WHERE SL.YD_STK_COL_GP = WM.YD_STK_COL_GP
				   AND SL.YD_STK_BED_NO = WM.YD_STK_BED_NO
				   AND SL.YD_STK_COL_GP = SB.YD_STK_COL_GP
				   AND SL.YD_STK_BED_NO = SB.YD_STK_BED_NO
				   AND SL.STL_NO        = ST.STL_NO
				) A

			 */
			jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401CurrLoc", logId, methodNm, "현재위치 조회");
	    	
			//크레인스케쥴ID 조회 (Lot수 만큼)
			jrParam.setField("CRN_SCH_CNT", String.valueOf(schCnt)); //크레인스케쥴수
			//ydCrnSchId = commDao.getSeqId(logId, methodNm, "CrnSch");
			/* 확인 후 교체-염용선
			 * SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI')||TO_CHAR(YD_CRNSCH_SEQ.NEXTVAL,'FM000000') AS YD_CRN_SCH_ID
                  FROM DUAL CONNECT BY LEVEL <= TO_NUMBER(:V_CRN_SCH_CNT)

			 */
			JDTORecordSet jsCrnSchId = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401CrnSchId", logId, methodNm, "크레인스케쥴ID 조회");
	    	
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
				fYdEqpWrkMaxT = 0;
				fYdEqpWrkMinT = 999;
				fYdMtlW       = 0;
				iYdMtlL       = 0;

				for (int jj = 0; jj < iYdEqpWrkSh; jj++) {
					jrRow = jsLot.getRecord(jj);

					stlNo        = jrRow.getFieldString("STL_NO"            );
					ydStkLotTp   = jrRow.getFieldString("YD_STK_LOT_TP"     );
					ydStkLotCd   = jrRow.getFieldString("YD_STK_LOT_CD"     );
					ydAimRtGp    = jrRow.getFieldString("YD_AIM_RT_GP"      );
					toLocDcsnMtd = jrRow.getFieldString("YD_TO_LOC_DCSN_MTD");
					fYdMtlT      = Float.parseFloat(slabUtils.nvl(jrRow.getFieldString("YD_MTL_T" ),"0"));
					fYdMtlW       = Float.parseFloat(slabUtils.nvl(jrRow.getFieldString("YD_MTL_W" ),"0"));
					iYdMtlL       = Integer.parseInt(slabUtils.nvl(jrRow.getFieldString("YD_MTL_L" ),"0"));
					iYdEqpWrkWt  += Integer.parseInt(slabUtils.nvl(jrRow.getFieldString("YD_MTL_WT"),"0"));
					fYdEqpWrkT   += fYdMtlT;
					
					if (fYdMtlW > fYdEqpWrkMaxW) { fYdEqpWrkMaxW = fYdMtlW; }
					if (fYdMtlW < fYdEqpWrkMinW) { fYdEqpWrkMinW = fYdMtlW; }
					if (iYdMtlL > iYdEqpWrkMaxL) { iYdEqpWrkMaxL = iYdMtlL; }
					if (iYdMtlL < iYdEqpWrkMinL) { iYdEqpWrkMinL = iYdMtlL; }
					if (fYdMtlT > fYdEqpWrkMaxT) { fYdEqpWrkMaxT = fYdMtlT; }
					if (fYdMtlT < fYdEqpWrkMinT) { fYdEqpWrkMinT = fYdMtlT; }
					
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
			
				slabUtils.printLog(logId, ydDnWoLoc + "======================= 크레인스케줄 Handling Lot 편성 결과", "SL");
				
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
				lastMtl[ii][0] = String.valueOf(seqNo-1);		//스케줄 마지막재료 순서
				lastMtl[ii][1] = stlNo;							//재료번호
				
				ydMtlTArr[ii][0] = fYdEqpWrkMaxT;               //야드설비작업최대두께
				ydMtlTArr[ii][1] = fYdEqpWrkMinT;               //야드설비작업최소두께
				
				ydMtlLArr[ii][0] = iYdEqpWrkMaxL;               //야드설비작업최대길이
				ydMtlLArr[ii][1] = iYdEqpWrkMinL;               //야드설비작업최소길이
				
				ydMtlWArr[ii][0] = fYdEqpWrkMaxW;               //야드설비작업최대두께
				ydMtlWArr[ii][1] = fYdEqpWrkMinW;               //야드설비작업최소두께
			}

			if ("Y".equals(crnSchLogYn)) {
				slabUtils.printLog(logId, " 크레인스케줄 등록용 Param : 크레인스케줄수[" + schCnt + "], 크레인작업재료수[" + totCnt + "]", "SL");
				slabUtils.printParam(logId + " 크레인스케줄 To위치결정전", paramCS);
			}
			
			//적치단 야드적치단재료상태 수정 (크레인작업대상 전체 권상대기)
			//schDao.updYDYDJ400("StkLyrU", jrParam);
			/*
			 *크레인스케줄 적치단 권상대기 수정
				UPDATE TB_YD_STKLYR
				   SET MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,YD_STK_LYR_MTL_STAT = 'U' --권상대기
				 WHERE (YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO)
				    IN (SELECT SL.YD_STK_COL_GP
				              ,SL.YD_STK_BED_NO
				              ,SL.YD_STK_LYR_NO
				          FROM TB_YD_STKLYR SL
				              ,(SELECT YD_STK_COL_GP
				                      ,YD_STK_BED_NO
				                      ,MIN(YD_STK_LYR_NO) AS YD_STK_LYR_NO_MIN
				                  FROM TB_YD_WRKBOOKMTL
				                 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				                   AND DEL_YN      = 'N'
				                 GROUP BY YD_STK_COL_GP, YD_STK_BED_NO) WB
				         WHERE SL.YD_STK_COL_GP  = WB.YD_STK_COL_GP
				           AND SL.YD_STK_BED_NO  = WB.YD_STK_BED_NO
				           AND SL.YD_STK_LYR_NO >= WB.YD_STK_LYR_NO_MIN
				           AND SL.STL_NO IS NOT NULL)

			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchDAO.updYDYDJ401StkLyrU", logId, methodNm, "적치단(TB_YD_STKLYR) 권상대기 수정");
			String ydEqpWrkSh = "";	//야드설비작업매수
			String ydEqpWrkWt = "";	//야드설비작업중량
			String ydEqpWrkT  = "";	//야드설비작업총두께
			String ydEqpWrkMaxL  = ""; //야드작업 최대 길이
			String ydEqpWrkMinL  = ""; //야드작업 최소 길이
			String ydEqpWrkMaxT  = ""; //야드작업 최대 두께
			String ydEqpWrkMinT  = ""; //야드작업 최소 두께
			String ydEqpWrkMaxW  = ""; //야드작업 최대 폭
			String ydEqpWrkMinW  = ""; ////야드작업 최소 폭
			
            //더미 이적 시 '1차 To위치 검색' 적용여부
			String APPLY_YN42   = commDao.PSlabApplyYn("APPLY_YN42");
			slabUtils.printLog(logId, "더미 이적 여부 >> APPLY_YN42 : " + APPLY_YN42, "SL");
			
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
				ydEqpWrkMaxL    = paramCS[ii][22];	//야드설비작업최대길이
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
				
				ydEqpWrkMaxT    = String.valueOf(ydMtlTArr[ii][0]); //야드설비작업최대두께
				ydEqpWrkMinT    = String.valueOf(ydMtlTArr[ii][1]); //야드설비작업최소두께
				
				ydEqpWrkMinL    = String.valueOf(ydMtlLArr[ii][1]); //야드설비작업최대길이
				
				ydEqpWrkMaxW    = String.valueOf(ydMtlWArr[ii][0]); //야드설비작업최대폭
				ydEqpWrkMinW    = String.valueOf(ydMtlWArr[ii][1]); //야드설비작업최소폭
				
				//권하위치결정 - 권하위치가 결정되지 않은 스케줄의 권하위치를 검색
				trtGp = "";
				
				slabUtils.printLog(logId, " ydDnWoLoc [" + ydDnWoLoc + "], ydToLocGuide [" + ydToLocGuide + "]", "SL");
				
				//더미 이적일 경우 1차 To위치(권상위치에서 가까운위치 20개 BED대상 검색)
				JDTORecordSet jsDnWoLoc1 = null;
				
				if (ydDnWoLoc.startsWith("X")) {
					//주작업(모음) "B", "R" 인 경우에는 이미 권상, 권하 위치가 결정되어 있음, "A"는 최하단에 있을 수 없음
					jrParam.setField("YD_UP_WO_LOC"      , ydUpWoLoc     ); //야드권상지시위치
					jrParam.setField("YD_UP_WO_LOC_XAXIS", ydUpWoLocXaxis); //야드권상지시X축
					jrParam.setField("YD_UP_WO_LOC_YAXIS", ydUpWoLocYaxis); //야드권상지시Y축
					jrParam.setField("STL_NO"            , stlNo         ); //재료번호
					jrParam.setField("YD_EQP_WRK_W_MAX"  , paramCS[ii][21]); //야드설비작업최대폭 (권상재료들 중 최대폭)
					
					//다음 작업이 권하분리 작업이면 Bed사양 Check 시 최종 남아 있는 재료만 해당
					if (ii < schCnt - 1 && ("X".equals(paramCS[ii + 1][16]) || "Y".equals(paramCS[ii + 1][16]))) {
						iYdEqpWrkSh = Integer.parseInt(ydEqpWrkSh) - Integer.parseInt(paramCS[ii][18]);
						iYdEqpWrkWt = Integer.parseInt(ydEqpWrkWt) - Integer.parseInt(paramCS[ii][19]);
						fYdEqpWrkT  = Float.parseFloat(ydEqpWrkT ) - Float.parseFloat(paramCS[ii][20]);
						
						iYdEqpWrkMaxL  = ydMtlLArr[ii][0];
						iYdEqpWrkMinL  = ydMtlLArr[ii][1];
						fYdEqpWrkMaxT  = ydMtlTArr[ii][0];
						fYdEqpWrkMinT  = ydMtlTArr[ii][1];
						fYdEqpWrkMaxW  = ydMtlTArr[ii][0];
						fYdEqpWrkMinW  = ydMtlTArr[ii][1];
						
						if (iYdEqpWrkSh > 0 && iYdEqpWrkWt > 0 && fYdEqpWrkT > 0) {
							jrParam.setField("YD_EQP_WRK_SH", String.valueOf(iYdEqpWrkSh)); //야드설비작업매수
							jrParam.setField("YD_EQP_WRK_WT", String.valueOf(iYdEqpWrkWt)); //야드설비작업중량
							jrParam.setField("YD_EQP_WRK_T" , String.valueOf(fYdEqpWrkT )); //야드설비작업총두께
							jrParam.setField("YD_EQP_WRK_MAX_L" , String.valueOf(iYdEqpWrkMaxL )); //야드설비작업최대길이
							jrParam.setField("YD_EQP_WRK_MIN_L" , String.valueOf(iYdEqpWrkMinL )); //야드설비작업최소길이
							jrParam.setField("YD_EQP_WRK_MAX_T" , String.valueOf(fYdEqpWrkMaxT )); //야드설비작업최대두께
							jrParam.setField("YD_EQP_WRK_MIN_T" , String.valueOf(fYdEqpWrkMinT )); //야드설비작업최소두께
							jrParam.setField("YD_EQP_WRK_MAX_W" , String.valueOf(fYdEqpWrkMaxW )); //야드설비작업최대폭
							jrParam.setField("YD_EQP_WRK_MIN_W" , String.valueOf(fYdEqpWrkMinW )); //야드설비작업최소폭
						}
					} else {
						jrParam.setField("YD_EQP_WRK_SH", ydEqpWrkSh); //야드설비작업매수
						jrParam.setField("YD_EQP_WRK_WT", ydEqpWrkWt); //야드설비작업중량
						jrParam.setField("YD_EQP_WRK_T" , ydEqpWrkT ); //야드설비작업총두께
						jrParam.setField("YD_EQP_WRK_MAX_L" , ydEqpWrkMaxL ); //야드설비작업최대길이
						jrParam.setField("YD_EQP_WRK_MIN_L" , ydEqpWrkMinL ); //야드설비작업최소길이
						jrParam.setField("YD_EQP_WRK_MAX_T" , ydEqpWrkMaxT); //야드설비작업최대두께
						jrParam.setField("YD_EQP_WRK_MIN_T" , ydEqpWrkMinT); //야드설비작업최소두께
						jrParam.setField("YD_EQP_WRK_MAX_W" , ydEqpWrkMaxW); //야드설비작업최대폭
						jrParam.setField("YD_EQP_WRK_MIN_W" , ydEqpWrkMinW); //야드설비작업최소폭
					}
					jrParam.setField("DUMMY_YN" , dummyYn); //더미여부
					
					if ("W".equals(toLocDcsnMtd) || "X".equals(toLocDcsnMtd) || "Y".equals(toLocDcsnMtd)) {
						//보조작업(주작업권하분리) - To위치평점, 권상-권하위치 거리 순
						//  제외구간(크레인주행금지구간/스카핑장/DB02 SPAN) 적용
						trtGp = "DnLocWX_Ex";
						query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401DnLocWX_Ex";
					} else if ("M".equals(toLocDcsnMtd) || "T".equals(toLocDcsnMtd)) {
						//주작업([Base]이적) - 권상-권하위치 거리 순 (설비 Bed 일 경우에만 사양 Check)
						//  제외구간(크레인주행금지구간/스카핑장/DB02 SPAN) 적용
						trtGp = "DnLocMT_Ex";
						query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401DnLocMT_Ex";
					} else if ("S".equals(toLocDcsnMtd)) {
						//주작업(최종)
						if ("U".equals(ydSchCd.substring(6, 7)) && ("PU".equals(ydSchCd.substring(2, 4)) || "TC".equals(ydSchCd.substring(2, 4)))) {
							//대차상차, Depiler불출 DP
							//2021.04.27 기준 "U"일 경우에만 처리 되고, "L"일 경우에는  "DnLocSY_Ex" 쿼리문이 실행되어 권하위치를 찾지 못하고 "XX010101"로 표기됨. "B"동일 경우 2SPAN을 임의로 사용하기 위해서 인가??? 
							trtGp = "DnLocTD";
							query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401DnLocTD";
						} else if ("U".equals(ydSchCd.substring(6, 7)) && "PT".equals(ydSchCd.substring(2, 4))) {
							//차량상차
							//  차량상차 2BED 적용
							trtGp = "DnLocPT2";
							query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401DnLocPT2";
						} else {
							//기타 주작업
							
							//지시재->지시재 자동이적 시 권상재료 최하단의 Lot번호와 권하위치 최상단의 Lot번호 같은 베드 먼저 탐색.
							if("M1".equals(ydSchCd.substring(6, 8))){
								trtGp = "DnLocSY3_Ex_SameLot";
								query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401DnLocSY3_Ex_SameLot";
								jsDnWoLoc1 = commDao.select(jrParam, query_id, logId, methodNm, "지시재자동이적 동일 Lot번호 권하위치 검색");
								
								if (jsDnWoLoc1 == null || jsDnWoLoc1.size() < 1) {
	  								//스케쥴코드별 위치검색Bed(From~To) + 설정된 우선순위 적용여부 + 제외구간(크레인주행금지구간/스카핑장/DB02 SPAN) 적용
	    							trtGp = "DnLocSY3_Ex";
			    					query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401DnLocSY3_Ex";
	  							}
							}
							
							//더미 이적 시 '1차 To위치 검색' 적용여부
							else if("Y".equals(APPLY_YN42)){
								
								//더미 이적이 맞으면...
								if ("Y".equals(dummyYn)) { //("Y" == 더미이적)
									//1차 To위치 검색 : 권상위치에서 가까운위치 20개 BED 대상으로 To위치 검색
									trtGp = "DnLocSY3_Ex_Step1";
									query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401DnLocSY3_Ex_Step1";
									jsDnWoLoc1 = commDao.select(jrParam, query_id, logId, methodNm, "1차 To위치 검색 : 권상위치에서 가까운위치 20개 BED대상");
									
									if ("Y".equals(crnSchLogYn)) {
										String logItem = "YD_DN_WO_LOC;YD_DN_WO_LAYER;YD_DN_WO_LOC_XAXIS;YD_DN_WO_XAXIS_GAP;YD_DN_WO_LOC_YAXIS;YD_DN_WO_YAXIS_GAP;YD_DN_WO_LOC_ZAXIS;"
												       + "YD_DN_WO_ZAXIS_GAP;TO_LOC_EVP_MW_VAL;TO_LOC_EVP_RT_VAL;TO_LOC_EVP_SLC_VAL;TO_LOC_EVP_EMP_VAL;BED_GAP;TO_LOC_EVP;DN_REAL_MEASURE_SLAB_W";
										slabUtils.printParam(logId + " 더미 이적 여부 >> 1차 To위치 검색 : ", jsDnWoLoc1, logItem);
									}
									
		  							if (jsDnWoLoc1 == null || jsDnWoLoc1.size() < 1) {
		  								//스케쥴코드별 위치검색Bed(From~To) + 설정된 우선순위 적용여부 + 제외구간(크레인주행금지구간/스카핑장/DB02 SPAN) 적용
		    							trtGp = "DnLocSY3_Ex";
				    					query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401DnLocSY3_Ex";
		  							}
		  							
		  						//더미 이적이 아니면...
								} else {
		  							//스케쥴코드별 위치검색Bed(From~To) + 설정된 우선순위 적용여부 + 제외구간(크레인주행금지구간/스카핑장/DB02 SPAN) 적용
		  							trtGp = "DnLocSY3_Ex";
		  							query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401DnLocSY3_Ex";
								}
								
							} else {
								//스케쥴코드별 위치검색Bed(From~To) + 설정된 우선순위 적용여부 + 제외구간(크레인주행금지구간/스카핑장/DB02 SPAN) 적용
								trtGp = "DnLocSY3_Ex";
								query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401DnLocSY3_Ex";
							}
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
						query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401DnLocTG";
						
						jrParam.setField("YD_STK_COL_GP", ydDnWoLoc.substring(0, 6)); //야드적치열구분
						jrParam.setField("YD_STK_BED_NO", ydDnWoLoc.substring(6, 8)); //야드적치Bed번호
					}
				}
				
				slabUtils.printLog(logId, "권하지시위치를 조회 (trtGp) : "+ trtGp, "SL");
				
				//권하지시위치를 조회하여 Set
				if (!"".equals(trtGp)) {
					/*
					 * 
					 *	크레인스케줄 권하위치(보조작업) 조회 - com.inisteel.cim.yd.pslabyd.dao.PSlabSchSeEJB.getYDYDJ400DnLocWX
						SELECT YD_DN_WO_LOC                                               --야드권하지시위치
						      ,TO_CHAR(YD_MTL_SH + 1,'FM000')       AS YD_DN_WO_LAYER     --야드권하지시단
						      ,YD_DN_WO_LOC_XAXIS                                         --야드권하지시X축
						      ,SF_SLAB_YD_CRN_GAP(YD_DN_WO_LOC,'X') AS YD_DN_WO_XAXIS_GAP --야드권하지시X축오차
						      ,YD_DN_WO_LOC_YAXIS                                         --야드권하지시Y축
						      ,SF_SLAB_YD_CRN_GAP(YD_DN_WO_LOC,'Y') AS YD_DN_WO_YAXIS_GAP --야드권하지시Y축오차
						      ,YD_DN_WO_LOC_ZAXIS                                         --야드권하지시Z축
						      ,SF_SLAB_YD_CRN_GAP(YD_DN_WO_LOC,'Z') AS YD_DN_WO_ZAXIS_GAP --야드권하지시Z축오차
						      ,TO_LOC_EVP                                                 --Bed재료평점
						  FROM (SELECT YD_DN_WO_LOC
						              ,YD_MTL_SH
						              ,YD_DN_WO_LOC_XAXIS
						              ,YD_DN_WO_LOC_YAXIS
						              ,YD_DN_WO_LOC_ZAXIS
						              ,TO_LOC_LMT_SH
						              ,CASE WHEN TO_LOC_EVP_SLC  < LMT_SLC  THEN 0 ELSE TO_LOC_EVP_SLC  * SEQ_SLC  END +
						               CASE WHEN TO_LOC_EVP_MW   < LMT_MW   THEN 0 ELSE TO_LOC_EVP_MW   * SEQ_MW   END +
						               CASE WHEN TO_LOC_EVP_SLC2 < LMT_SLC2 THEN 0 ELSE TO_LOC_EVP_SLC2 * SEQ_SLC2 END +
						               CASE WHEN TO_LOC_EVP_RT   < LMT_RT   THEN 0 ELSE TO_LOC_EVP_RT   * SEQ_RT   END +
						               TO_LOC_EVP_EMP * SEQ_EMP AS TO_LOC_EVP
						              ,SF_SLAB_YD_BED_GAP(YD_UP_WO_LOC_XAXIS, YD_UP_WO_LOC_YAXIS, YD_DN_WO_LOC_XAXIS, YD_DN_WO_LOC_YAXIS) AS BED_GAP --BedGap
						          FROM (SELECT SB.YD_STK_COL_GP||SB.YD_STK_BED_NO AS YD_DN_WO_LOC
						                      ,COUNT(ST.STL_NO)                   AS YD_MTL_SH
						                      ,MIN(SB.YD_STK_BED_XAXIS)           AS YD_DN_WO_LOC_XAXIS
						                      ,MIN(SB.YD_STK_BED_YAXIS)           AS YD_DN_WO_LOC_YAXIS
						                      ,NVL(ROUND(SUM(ST.YD_MTL_T)),0)     AS YD_DN_WO_LOC_ZAXIS
						                      ,DD.YD_UP_WO_LOC
						                      ,DD.YD_UP_WO_LOC_XAXIS
						                      ,DD.YD_UP_WO_LOC_YAXIS
						                      --적치불가능Bed
						                      ,SUM(CASE WHEN SL.YD_STK_LYR_MTL_STAT IN ('U','X') THEN 1 ELSE 0 END) AS STK_IMP_LYR_CNT --적치불가능단수
						                      ,CASE WHEN MIN(SB.YD_STK_BED_LYR_MAX) < MIN(DD.YD_EQP_WRK_SH) + COUNT(ST.STL_NO)         THEN 'SH'
						                            WHEN MIN(SB.YD_STK_BED_WT_MAX ) < MIN(DD.YD_EQP_WRK_WT) + NVL(SUM(ST.YD_MTL_WT),0) THEN 'WT'
						                            WHEN MIN(SB.YD_STK_BED_H_MAX  ) < MIN(DD.YD_EQP_WRK_T ) + NVL(SUM(ST.YD_MTL_T ),0) THEN 'T'
						                       ELSE 'N' END     AS BED_SPEC_OV_GP --Bed사양초과구분
						                      ,SUM(CASE WHEN CS.YD_TO_LOC_DCSN_MTD NOT IN ('S','W') THEN 0
						                      	        WHEN WM.STL_NO IS NOT NULL THEN 1 ELSE 0 END) AS WB_MTL_SH --작업예약재료매수(다른 스케줄 최종 제외)
						                      --To위치평점
						                      ,NVL(SUM(CASE WHEN DD.CRN_YD_STK_LOT_TP IN ('SL','SP') AND DD.CRN_YD_STK_LOT_TP = ST.YD_STK_LOT_TP THEN
						                                    CASE WHEN TO_NUMBER(SUBSTR(ST.YD_STK_LOT_CD,3)) < TO_NUMBER(SUBSTR(DD.CRN_YD_STK_LOT_CD,3)) THEN 1 END
						                               ELSE CASE WHEN ST.YD_AIM_RT_GP LIKE 'C%' THEN 1 END END),0) AS TO_LOC_LMT_SH --적치제한재료수
						                      ,NVL(DECODE(COUNT(ST.STL_NO),0,0,
						                           ROUND(SUM(DECODE(DD.CRN_YD_STK_LOT_CD,ST.YD_STK_LOT_CD,100))
						                                / COUNT(ST.STL_NO))),0) AS TO_LOC_EVP_SLC  --산적Lot코드평점
						                      ,NVL(DECODE(COUNT(ST.STL_NO),0,0,
						                           ROUND(SUM(CASE WHEN ST.YD_MTL_W BETWEEN DD.CRN_YD_MTL_W_MIN AND DD.CRN_YD_MTL_W_MAX THEN 100 END)
						                           	    / COUNT(ST.STL_NO))),0) AS TO_LOC_EVP_MW   --재료폭평점
						                      ,NVL(DECODE(COUNT(ST.STL_NO),0,0,
						                           ROUND(SUM(SF_SLAB_YD_TO_LOC_EVP_SLC(DD.CRN_YD_STK_LOT_TP, DD.CRN_YD_STK_LOT_CD, DD.CRN_SLAB_WO_RT_CD,
						                                                               ST.YD_STK_LOT_TP    , ST.YD_STK_LOT_CD    , ST.SLAB_WO_RT_CD    ))
						                                / COUNT(ST.STL_NO))),0) AS TO_LOC_EVP_SLC2 --산적Lot코드부분평점
						                      ,NVL(DECODE(COUNT(ST.STL_NO),0,0,
						                           ROUND(SUM(DECODE(DD.CRN_YD_AIM_RT_GP,ST.YD_AIM_RT_GP,100))
						                                / COUNT(ST.STL_NO))),0) AS TO_LOC_EVP_RT   --목표행선평점
						                      ,DECODE(COUNT(ST.STL_NO),0,100,0) AS TO_LOC_EVP_EMP  --공Bed평점
						                      ,MIN(DD.SEQ_SLC ) AS SEQ_SLC
						                      ,MIN(DD.SEQ_MW  ) AS SEQ_MW
						                      ,MIN(DD.SEQ_SLC2) AS SEQ_SLC2
						                      ,MIN(DD.SEQ_RT  ) AS SEQ_RT
						                      ,MIN(DD.SEQ_EMP ) AS SEQ_EMP
						                      ,MIN(DD.LMT_SLC ) AS LMT_SLC
						                      ,MIN(DD.LMT_MW  ) AS LMT_MW
						                      ,MIN(DD.LMT_SLC2) AS LMT_SLC2
						                      ,MIN(DD.LMT_RT  ) AS LMT_RT
						                  FROM TB_YD_STKBED     SB
						                      ,TB_YD_STKLYR     SL
						                      ,TB_YD_STOCK      ST
						                      ,TB_YD_WRKBOOK    WB
						                      ,TB_YD_WRKBOOKMTL WM
						                      ,TB_YD_CRNSCH     CS
						                      ,TB_YD_CRNWRKMTL  CM
						                      ,(SELECT DD.YD_UP_WO_LOC                                             --권상지시위치
						                              ,DD.YD_UP_WO_LOC_XAXIS                                       --야드권상지시X축
						                              ,DD.YD_UP_WO_LOC_YAXIS                                       --야드권상지시Y축
						                              ,DD.YD_EQP_WRK_SH                                            --야드설비작업매수
						                              ,DD.YD_EQP_WRK_WT                                            --야드설비작업중량
						                              ,DD.YD_EQP_WRK_T                                             --야드설비작업총두께
						                              ,ST.YD_STK_LOT_TP                       AS CRN_YD_STK_LOT_TP --야드산적LotType
						                              ,ST.YD_STK_LOT_CD                       AS CRN_YD_STK_LOT_CD --야드산적Lot코드
						                              ,ST.YD_AIM_RT_GP                        AS CRN_YD_AIM_RT_GP  --야드목표행선구분
						                              ,ST.SLAB_WO_RT_CD                       AS CRN_SLAB_WO_RT_CD --Slab지시행선코드
						                              ,ST.YD_MTL_L                            AS CRN_YD_MTL_L      --야드재료길이
						                              ,ST.YD_MTL_W - DD.YD_CRN_TONG_W_TOL / 2 AS CRN_YD_MTL_W_MIN  --야드재료폭Min
						                              ,ST.YD_MTL_W + DD.YD_CRN_TONG_W_TOL / 2 AS CRN_YD_MTL_W_MAX  --야드재료폭Max
						                              ,CASE WHEN BR.YD_WFACT1 IN ('1','2','3','4','5') THEN POWER(10,(5-TO_NUMBER(BR.YD_WFACT1))*3) ELSE 0 END AS SEQ_SLC  --야드산적Lot코드순서
						                              ,CASE WHEN BR.YD_WFACT2 IN ('1','2','3','4','5') THEN POWER(10,(5-TO_NUMBER(BR.YD_WFACT2))*3) ELSE 0 END AS SEQ_MW   --야드재료폭순서
						                              ,CASE WHEN BR.YD_WFACT3 IN ('1','2','3','4','5') THEN POWER(10,(5-TO_NUMBER(BR.YD_WFACT3))*3) ELSE 0 END AS SEQ_SLC2 --야드산적Lot코드2순서
						                              ,CASE WHEN BR.YD_WFACT4 IN ('1','2','3','4','5') THEN POWER(10,(5-TO_NUMBER(BR.YD_WFACT4))*3) ELSE 0 END AS SEQ_RT   --야드목표행선구분순서
						                              ,CASE WHEN BR.YD_WFACT5 IN ('1','2','3','4','5') THEN POWER(10,(5-TO_NUMBER(BR.YD_WFACT5))*3) ELSE 0 END AS SEQ_EMP  --공Bed순서
						                              ,NVL(TO_NUMBER(BR.SEGMENT1),0) AS LMT_SLC  --야드산적Lot코드제한비율
						                              ,NVL(TO_NUMBER(BR.SEGMENT2),0) AS LMT_MW   --야드재료폭제한비율
						                              ,NVL(TO_NUMBER(BR.SEGMENT3),0) AS LMT_SLC2 --야드산적Lot코드2제한비율
						                              ,NVL(TO_NUMBER(BR.SEGMENT4),0) AS LMT_RT   --야드목표행선구분제한비율
						                              ,DD.YD_WRK_ABLE_XAXIS_FR
						                              ,DD.YD_WRK_ABLE_XAXIS_TO
						                              ,DD.YD_WRK_ABLE_YAXIS_FR
						                              ,DD.YD_WRK_ABLE_YAXIS_TO
						                          FROM TB_YD_STOCK  ST
						                              ,VW_YD_YDB031 BR --To위치기준
						                              ,(SELECT :V_YD_UP_WO_LOC                   AS YD_UP_WO_LOC
						                                      ,TO_NUMBER(:V_YD_UP_WO_LOC_XAXIS ) AS YD_UP_WO_LOC_XAXIS
						                                      ,TO_NUMBER(:V_YD_UP_WO_LOC_YAXIS ) AS YD_UP_WO_LOC_YAXIS
						                                      ,TO_NUMBER(:V_YD_EQP_WRK_SH      ) AS YD_EQP_WRK_SH
						                                      ,TO_NUMBER(:V_YD_EQP_WRK_WT      ) AS YD_EQP_WRK_WT
						                                      ,TO_NUMBER(:V_YD_EQP_WRK_T       ) AS YD_EQP_WRK_T
						                                      ,:V_STL_NO                         AS STL_NO
						                                      ,YD_CRN_TONG_W_TOL
						                                      ,NVL(YD_WRK_ABLE_XAXIS_FR,      0) AS YD_WRK_ABLE_XAXIS_FR
						                                      ,NVL(YD_WRK_ABLE_XAXIS_TO,9999999) AS YD_WRK_ABLE_XAXIS_TO
						                                      ,NVL(YD_WRK_ABLE_YAXIS_FR,      0) AS YD_WRK_ABLE_YAXIS_FR
						                                      ,NVL(YD_WRK_ABLE_YAXIS_TO,  99999) AS YD_WRK_ABLE_YAXIS_TO
						                                  FROM TB_YD_CRNSPEC
						                                 WHERE YD_EQP_ID = :V_YD_EQP_ID) DD
						                         WHERE ST.STL_NO = DD.STL_NO
						                           AND BR.YD_GP  = SUBSTR(DD.YD_UP_WO_LOC,1,1)
						                           AND BR.YD_STK_LOT_TP = ST.YD_STK_LOT_TP) DD
						                 WHERE SB.YD_STK_COL_GP LIKE SUBSTR(DD.YD_UP_WO_LOC,1,2)||'0%'
						                   AND SB.YD_STK_BED_XAXIS BETWEEN DD.YD_WRK_ABLE_XAXIS_FR AND DD.YD_WRK_ABLE_XAXIS_TO
						                   AND SB.YD_STK_BED_YAXIS BETWEEN DD.YD_WRK_ABLE_YAXIS_FR AND DD.YD_WRK_ABLE_YAXIS_TO
						                   AND SB.YD_STK_COL_GP = SL.YD_STK_COL_GP
						                   AND SB.YD_STK_BED_NO = SL.YD_STK_BED_NO
						                   AND SL.STL_NO        = ST.STL_NO(+)
						                   AND SL.STL_NO        = WM.STL_NO(+)
						                   AND 'N'              = WM.DEL_YN(+)
						                   AND WM.YD_WBOOK_ID   = WB.YD_WBOOK_ID(+)
						                   AND 'N'              = WB.DEL_YN(+)
						                   AND SL.STL_NO        = CM.STL_NO(+)
						                   AND 'N'              = CM.DEL_YN(+)
						                   AND CM.YD_CRN_SCH_ID = CS.YD_CRN_SCH_ID(+)
						                   AND 'N'              = CS.DEL_YN(+)
						                   AND SB.DEL_YN        = 'N'
						                   AND SB.YD_STK_BED_ACT_STAT  = 'L' --적치가능
						                   AND SB.YD_STK_BED_WHIO_STAT = 'E' --입출고가능(완산Bed제외)
						                   AND SB.YD_STK_BED_USG_GP   != 'S' --수입구제외
						                 GROUP BY SB.YD_STK_COL_GP, SB.YD_STK_BED_NO) SB
						         WHERE YD_DN_WO_LOC   != YD_UP_WO_LOC --권상지시Bed제외
						           AND STK_IMP_LYR_CNT = 0   --적치불가능단 없음
						           AND BED_SPEC_OV_GP  = 'N' --Bed사양 초과 안함
						           AND WB_MTL_SH       = 0   --작업예약재료 없음
						         ORDER BY TO_LOC_LMT_SH, TO_LOC_EVP DESC, BED_GAP)
						 WHERE ROWNUM = 1
						
						
						*크레인스케줄 권하위치(주작업이적) 조회 - com.inisteel.cim.yd.pslabyd.dao.PSlabSchSeEJB.getYDYDJ400DnLocMT*
						SELECT YD_DN_WO_LOC                                               --야드권하지시위치
						      ,TO_CHAR(YD_MTL_SH + 1,'FM000')       AS YD_DN_WO_LAYER     --야드권하지시단
						      ,YD_DN_WO_LOC_XAXIS                                         --야드권하지시X축
						      ,SF_SLAB_YD_CRN_GAP(YD_DN_WO_LOC,'X') AS YD_DN_WO_XAXIS_GAP --야드권하지시X축오차
						      ,YD_DN_WO_LOC_YAXIS                                         --야드권하지시Y축
						      ,SF_SLAB_YD_CRN_GAP(YD_DN_WO_LOC,'Y') AS YD_DN_WO_YAXIS_GAP --야드권하지시Y축오차
						      ,YD_DN_WO_LOC_ZAXIS                                         --야드권하지시Z축
						      ,SF_SLAB_YD_CRN_GAP(YD_DN_WO_LOC,'Z') AS YD_DN_WO_ZAXIS_GAP --야드권하지시Z축오차
						      ,0                                    AS BED_MTL_EVP        --Bed재료평점
						  FROM (SELECT YD_DN_WO_LOC
						              ,YD_MTL_SH
						              ,YD_DN_WO_LOC_XAXIS
						              ,YD_DN_WO_LOC_YAXIS
						              ,YD_DN_WO_LOC_ZAXIS
						              ,SF_SLAB_YD_BED_GAP(YD_UP_WO_LOC_XAXIS, YD_UP_WO_LOC_YAXIS, YD_DN_WO_LOC_XAXIS, YD_DN_WO_LOC_YAXIS) AS BED_GAP --BedGap
						          FROM (SELECT SB.YD_STK_COL_GP||SB.YD_STK_BED_NO AS YD_DN_WO_LOC
						                      ,COUNT(ST.STL_NO)                   AS YD_MTL_SH
						                      ,MIN(SB.YD_STK_BED_XAXIS)           AS YD_DN_WO_LOC_XAXIS
						                      ,MIN(SB.YD_STK_BED_YAXIS)           AS YD_DN_WO_LOC_YAXIS
						                      ,NVL(ROUND(SUM(ST.YD_MTL_T)),0)     AS YD_DN_WO_LOC_ZAXIS
						                      ,DD.YD_UP_WO_LOC
						                      ,DD.YD_UP_WO_LOC_XAXIS
						                      ,DD.YD_UP_WO_LOC_YAXIS
						                      --적치불가능Bed
						                      ,SUM(CASE WHEN SL.YD_STK_LYR_MTL_STAT IN ('U','X') THEN 1 ELSE 0 END) AS STK_IMP_LYR_CNT --적치불가능단수
						                      ,CASE WHEN MIN(SB.YD_STK_BED_LYR_MAX) < MIN(DD.YD_EQP_WRK_SH) + COUNT(ST.STL_NO)         THEN 'SH'
						                            WHEN MIN(SB.YD_STK_BED_WT_MAX ) < MIN(DD.YD_EQP_WRK_WT) + NVL(SUM(ST.YD_MTL_WT),0) THEN 'WT'
						                            WHEN MIN(SB.YD_STK_BED_H_MAX  ) < MIN(DD.YD_EQP_WRK_T ) + NVL(SUM(ST.YD_MTL_T ),0) THEN 'T'
						                       ELSE 'N' END     AS BED_SPEC_OV_GP --Bed사양초과구분
						                      ,COUNT(WM.STL_NO) AS WB_MTL_SH      --작업예약재료매수
						                  FROM TB_YD_STKBED     SB
						                      ,TB_YD_STKLYR     SL
						                      ,TB_YD_STOCK      ST
						                      ,TB_YD_WRKBOOK    WB
						                      ,TB_YD_WRKBOOKMTL WM
						                      ,(SELECT :V_YD_UP_WO_LOC                  AS YD_UP_WO_LOC
						                              ,TO_NUMBER(:V_YD_UP_WO_LOC_XAXIS) AS YD_UP_WO_LOC_XAXIS
						                              ,TO_NUMBER(:V_YD_UP_WO_LOC_YAXIS) AS YD_UP_WO_LOC_YAXIS
						                              ,TO_NUMBER(:V_YD_EQP_WRK_SH     ) AS YD_EQP_WRK_SH
						                              ,TO_NUMBER(:V_YD_EQP_WRK_WT     ) AS YD_EQP_WRK_WT
						                              ,TO_NUMBER(:V_YD_EQP_WRK_T      ) AS YD_EQP_WRK_T
						                          FROM DUAL) DD
						                 WHERE SB.YD_STK_COL_GP LIKE SUBSTR(DD.YD_UP_WO_LOC,1,2)||'0%'
						                   AND SB.YD_STK_COL_GP = SL.YD_STK_COL_GP
						                   AND SB.YD_STK_BED_NO = SL.YD_STK_BED_NO
						                   AND SL.STL_NO        = ST.STL_NO(+)
						                   AND SL.STL_NO        = WM.STL_NO(+)
						                   AND 'N'              = WM.DEL_YN(+)
						                   AND WM.YD_WBOOK_ID   = WB.YD_WBOOK_ID(+)
						                   AND 'N'              = WB.DEL_YN(+)
						                   AND SB.DEL_YN        = 'N'
						                   AND SB.YD_STK_BED_ACT_STAT  = 'L' --적치가능
						                   AND SB.YD_STK_BED_WHIO_STAT = 'E' --입출고가능(완산Bed제외)
						                   AND SB.YD_STK_BED_USG_GP   != 'S' --수입구제외
						                 GROUP BY SB.YD_STK_COL_GP, SB.YD_STK_BED_NO) SB
						         WHERE YD_DN_WO_LOC   != YD_UP_WO_LOC --권상지시Bed제외
						           AND STK_IMP_LYR_CNT = 0   --적치불가능단 없음
						           AND BED_SPEC_OV_GP  = 'N' --Bed사양 초과 안함
						           AND WB_MTL_SH       = 0   --작업예약재료 없음
						         ORDER BY BED_GAP)
						 WHERE ROWNUM = 1


					 */
					//
					if ("DnLocSY3_Ex_SameLot".equals(trtGp)) {
						jsLot = jsDnWoLoc1;
					}
					//더미 이적 시 '1차 To위치 검색' 적용여부
					else if("Y".equals(APPLY_YN42)){
						//더미 이적일 경우 : 1차 To위치 검색결과가 존재하면...
						if ("DnLocSY3_Ex_Step1".equals(trtGp)) {
							jsLot = jsDnWoLoc1;
						
						} else {
							jsLot = commDao.select(jrParam, query_id, logId, methodNm, "권하지시위치를 조회");
						}
						
					} else {
						jsLot = commDao.select(jrParam, query_id, logId, methodNm, "권하지시위치를 조회");
					}
					
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
							slabUtils.printLog(logId, "▩ 권하지시위치결정1 - " + slabUtils.format(ii, 3) + " : " + paramCS[ii][34] + "-" + paramCS[ii][35] + " >> " + ydDnWoLoc + "-" + ydDnWoLayer + "]", "SL");
							slabUtils.printLog(logId, "▩ 권하지시위치결정2 - 1.재료폭 : " + jrRow.getFieldString("TO_LOC_EVP_MW_VAL") + " >> 2.날판폭(여재 포함) : " + jrRow.getFieldString("TO_LOC_EVP_RT_VAL") + " >> 3.산적Lot코드 : " + jrRow.getFieldString("TO_LOC_EVP_SLC_VAL") + " >> 4.공BED : " + jrRow.getFieldString("TO_LOC_EVP_EMP_VAL") + " >> 5.가까운거리(BedGap) : " + jrRow.getFieldString("BED_GAP") + " >> Bed재료평점 : " + jrRow.getFieldString("TO_LOC_EVP") + "]", "SL");
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
								slabUtils.printLog(logId, "▩ 권상지시위치변경 - " +  paramCS[jj][24] + " >> " + ydDnWoLoc + "-" + tmpStr + "]", "SL");
								
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
							jrParam.setField("YD_STK_COL_GP", ydDnWoLoc.substring(0, 6)); //야드적치열구분
							jrParam.setField("YD_STK_BED_NO", ydDnWoLoc.substring(6, 8)); //야드적치Bed번호
							//schDao.updYDYDJ400("StkBedF", jrParam);
							commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchDAO.updYDYDJ401StkBedF", logId, methodNm, "완산베드 수정");
							
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
					//기존 배치 프로세스 사용
					commDao.insYDYDJ401("StkLyrD", paramSL, logId, methodNm);
					//commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.updYDYDJ401StkLyrD", logId, methodNm, "적치단(TB_YD_STKLYR) 권하대기 수정");
				}
			}
			

			slabUtils.printLog(logId, "크레인스케줄 등록 : 크레인스케줄수[" + schCnt + "], 크레인작업재료수[" + totCnt + "]", "SL");
			slabUtils.printParam(logId + " 크레인스케줄(TB_YD_CRNSCH) 등록"     , paramCS);
			slabUtils.printParam(logId + " 크레인작업재료(TB_YD_CRNWRKMTL) 등록", paramCM);

			//크레인스케줄 등록
			commDao.insYDYDJ401("CrnSch", paramCS, logId, methodNm);

			//크레인작업재료 등록
			commDao.insYDYDJ401("CrnMtl", paramCM, logId, methodNm);
			slabUtils.printLog(logId ," 크레인작업재료(TB_YD_CRNWRKMTL) 등록---설비상태:"+ydEqpStat, "SL");
			//야드설비상태가 대기이면 크레인작업지시요구(Y3YDL007) 전송
			if ("W".equals(ydEqpStat)) {
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
                /* 후판슬라브 내부전문 코드 변경 YDYDJ440 >> YDYDJ441
				 * 야드 공통기준에 등록 
				 * 염용선 2020-11-10
				 */
				jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ441"               ); //JMSTC코드 --후판슬라브 크레인작업지시요구
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태
				
				jrRtn = slabUtils.addSndData(jrRtn,jrYdMsg);
			}
			//후판 슬라브야드 작업예약의 마지막 크레인스케줄 권상시 다음 작업예약 기동 및 L2로 작업지시 전달
			//후판슬라브야드에 크레인상태 권상시			
			else if ( ("D".equals(ydGp) && "2".equals(ydEqpStat))){
				jrParam.setField("EQP_ID" , ydEqpId );//야드설비ID
				//해당 장비로 기동된 작업예약 없는지 검사.
				//jsChk = schDao.getYDYDJ400("EndCrnSchYN", jrParam);
				jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getY3YDL008EndCrnSchYNByEQPID", logId, methodNm, "권하지시위치를 조회");
				if (jsChk.size() > 0) {
					slabUtils.printLog(logId, ydEqpId+" 장비의 현 작업예약의 다음 작업할 크레인 스케줄 존재", "SL");
				}
				else{
					slabUtils.printLog(logId, ydEqpId+" 장비의 다음 작업예약의 크레인스케줄 작업지시요구 전송", "SL");
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

					jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ441"               ); //JMSTC코드
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태
					
					jrRtn = slabUtils.addSndData(jrRtn,jrYdMsg);
				}
				
				
			}
			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "크레인스케줄등록 완료.");
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
	 *      염용선 2020 06 12
	 *      
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrParam
	 *      @return Vector
	 *      @throws DAOException
	*/
	public Vector setSlabYdHdLot(JDTORecord jrParam,JDTORecord jrCrnSpec,JDTORecordSet jsBedSpec) throws DAOException {
		String methodNm = "HandlingLot편성[PSlabYdSchSeEJB.setSlabYdHdLot] < " + jrParam.getResultMsg();
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
			
			JDTORecordSet jsDivLot = JDTORecordFactory.getInstance().createRecordSet("Tmp");	//분할Lot
			JDTORecordSet jsInsLot = JDTORecordFactory.getInstance().createRecordSet("Tmp");	//이적작업으로 인하여 추가할 Lot
			JDTORecord jrDivMtl    = JDTORecordFactory.getInstance().create();	//분할Lot재료
			JDTORecord jrInsMtl    = JDTORecordFactory.getInstance().create();	//이적작업으로 인하여 추가할 Lot재료
			JDTORecord jrCrnMtl    = JDTORecordFactory.getInstance().create();	//크레인작업재료

			JDTORecord jrInsMtlTop    = JDTORecordFactory.getInstance().create();// 지시단 상단의 레이어 가져오기 위함
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			String crnSchLogYn = jrParam.getFieldString("CRN_SCH_LOG_YN");	//크레인스케줄Log여부

			/**********************************************************
			* 1. Handling Lot 편성을 위한 사양 정보 조회
			*  - 크레인사양 조회
			*  - Bed사양 조회 : 현재  주작업재료가 적치된 Bed들의 사양
			**********************************************************/
			JDTORecord jrBedSpec = JDTORecordFactory.getInstance().create();
			jrBedSpec.setResultCode(logId);		//Log ID
			jrBedSpec.setResultMsg(methodNm);	//Log Method Name
			jrBedSpec.setField("MAX_LYR_OV_SH", jrParam.getFieldString("MAX_LYR_OV_SH"));	//Max단초과매수
			jrBedSpec.setField("BED_SPEC"     , jsBedSpec);
			
			/**********************************************************
			* 2. 크레인작업재료 조회
			*  - 주작업재료 상단의 모든 재료를 조회
			**********************************************************/
			/*
			 * 크레인스케줄 크레인작업재료 조회 
				SELECT BED_SEQ
				      ,ROW_NUMBER() OVER (ORDER BY BED_SEQ, YD_STK_LYR_NO DESC) AS HD_SEQ
				      ,NVL(YD_UP_COLL_SEQ,0) AS YD_UP_COLL_SEQ
				      ,CASE WHEN YD_AID_WRK_YN = 'Y' THEN 'W'                              --보조작업
				       ELSE CASE WHEN YD_UP_COLL_SEQ = YD_UP_COLL_SEQ_MAX THEN
				       	              DECODE(YD_STK_LYR_NO_MIN_YN,'Y','S','T')             --주작업(최종,최종이적)
				            ELSE CASE WHEN YD_STK_LYR_NO_MIN_YN = 'Y' THEN 'B'             --주작업(Base)
				            	 ELSE DECODE(YD_UP_COLL_SEQ+1,YD_UP_COLL_SEQ_NEXT,'A','M') --주작업(연속,이적)
				                  END END END AS YD_TO_LOC_DCSN_MTD
				      ,YD_STK_COL_GP||YD_STK_BED_NO AS YD_UP_WO_LOC
				      ,YD_STK_LYR_NO AS YD_UP_WO_LAYER
				      ,STL_NO
				      ,(SELECT CHK2 FROM VW_YD_FROM_S_PLATE_IS_SCARFING TEMP WHERE TEMP.STL_NO = WM.STL_NO) AS PLATE_SIDE_SCARFING_YN
				      ,YD_AID_WRK_YN
				      ,YD_STK_LOT_TP
				      ,YD_STK_LOT_CD
				      ,HCR_GP
				      ,STL_PROG_CD
				      ,YD_MTL_ITEM
				      ,YD_AIM_RT_GP
				      ,YD_MTL_T
				      ,YD_MTL_W
				      ,YD_MTL_L
				      ,YD_MTL_WT
				      ,NVL(YD_SCH_CD,'XX010101') as YD_SCH_CD
				      , CAR_POINT_CD||'01' AS CAR_POINT_CD
				  FROM (SELECT WB.BED_SEQ
				              ,WM.YD_UP_COLL_SEQ
				              ,WB.YD_UP_COLL_SEQ_MAX
				              ,LEAD(WM.YD_UP_COLL_SEQ, 1) OVER (ORDER BY WB.BED_SEQ, SL.YD_STK_LYR_NO DESC) AS YD_UP_COLL_SEQ_NEXT
				              ,DECODE(SL.YD_STK_LYR_NO,WB.YD_STK_LYR_NO_MIN,'Y','N') AS YD_STK_LYR_NO_MIN_YN
				              ,SL.YD_STK_COL_GP
				              ,SL.YD_STK_BED_NO
				              ,SL.YD_STK_LYR_NO
				              ,SL.STL_NO
				              ,DECODE(SL.STL_NO,WM.STL_NO,'N','Y') AS YD_AID_WRK_YN
				              ,ST.YD_STK_LOT_TP
				              ,ST.YD_STK_LOT_CD
				              ,ST.HCR_GP
				              ,ST.STL_PROG_CD
				              ,ST.YD_MTL_ITEM
				              ,ST.YD_AIM_RT_GP
				              ,ST.YD_MTL_T
				              ,ST.YD_MTL_W
				              ,ST.YD_MTL_L
				              ,ST.YD_MTL_WT
				              ,(select YD_SCH_CD from tb_yd_wrkbook where YD_WBOOK_ID = wm.YD_WBOOK_ID) as YD_SCH_CD
				              , (select B.YD_STK_COL_GP
				                     from tb_yd_wrkbook A 
				                      , USRYDA.TB_YD_CARPOINT B
				                     where A.YD_WBOOK_ID =  wm.YD_WBOOK_ID
				                       AND A.TRN_EQP_CD = B.TRN_EQP_CD) AS CAR_POINT_CD
				          FROM TB_YD_STKLYR     SL
				              --,TB_YD_STOCK      ST
				              ,(
				                  SELECT 
				                        ST.YD_STK_LOT_TP
				                       ,ST.YD_STK_LOT_CD
				                       ,ST.HCR_GP
				                       ,ST.STL_PROG_CD
				                       ,ST.YD_MTL_ITEM
				                       ,ST.YD_AIM_RT_GP
				                       ,st.STL_NO
				                       , DECODE(SC.REAL_MEASURE_SLAB_LEN,0,SC.SLAB_LEN, SC.REAL_MEASURE_SLAB_LEN) YD_MTL_L   -- (실측)길이
				                       , DECODE(SC.REAL_MEASURE_SLAB_T,  0,SC.SLAB_T  , SC.REAL_MEASURE_SLAB_T  ) YD_MTL_T   -- (실측)두께
				                       , DECODE(SC.REAL_MEASURE_SLAB_W,  0,SC.SLAB_W  , SC.REAL_MEASURE_SLAB_W  ) YD_MTL_W  -- (실측)폭
				                       , DECODE(SC.CAL_SLAB_WT,          0,SC.SLAB_WT , SC.CAL_SLAB_WT          ) YD_MTL_WT -- (계산)중량
				
				                    FROM USRYDA.TB_YD_STOCK    ST -- YD_저장품
				                       , USRPTA.TB_PT_SLABCOMM SC -- SLAB공통
				                   WHERE ST.STL_NO = SC.SLAB_NO
				               ) ST
				                      
				              ,TB_YD_WRKBOOKMTL WM
				              ,(SELECT YD_STK_COL_GP
				                      ,YD_STK_BED_NO
				                      ,MIN(YD_STK_LYR_NO) AS YD_STK_LYR_NO_MIN
				                              ,MAX(MAX(YD_UP_COLL_SEQ)) OVER () AS YD_UP_COLL_SEQ_MAX
				                      ,ROW_NUMBER() OVER (ORDER BY MAX(YD_UP_COLL_SEQ) DESC) AS BED_SEQ
				                  FROM TB_YD_WRKBOOKMTL
				                 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				                   AND DEL_YN      = 'N'
				                 GROUP BY YD_STK_COL_GP, YD_STK_BED_NO) WB
				         WHERE SL.YD_STK_COL_GP  = WB.YD_STK_COL_GP
				           AND SL.YD_STK_BED_NO  = WB.YD_STK_BED_NO
				           AND SL.YD_STK_LYR_NO >= WB.YD_STK_LYR_NO_MIN
				           AND SL.STL_NO         = ST.STL_NO
				           AND SL.STL_NO         = WM.STL_NO(+)
				           AND :V_YD_WBOOK_ID    = WM.YD_WBOOK_ID(+)
				           AND 'N'               = WM.DEL_YN(+)
				           ) WM
				 ORDER BY PLATE_SIDE_SCARFING_YN DESC,HD_SEQ


			 */
			JDTORecordSet jsCrnMtl = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401CrnMtl", logId, methodNm, "크레인작업재료 조회");
			
			if (jsCrnMtl.size() <= 0) {
				throw new Exception("오류:크레인작업재료조회 >> 조회 Data 없음");
			}
			
			if ("Y".equals(crnSchLogYn)) {
				String logItem = "BED_SEQ;HD_SEQ;YD_UP_COLL_SEQ;YD_TO_LOC_DCSN_MTD;YD_UP_WO_LOC;YD_UP_WO_LAYER;STL_NO;PLATE_SIDE_SCARFING_YN;YD_AID_WRK_YN;YD_STK_LOT_TP;"
						       + "YD_STK_LOT_CD;HCR_GP;STL_PROG_CD;YD_MTL_ITEM;YD_AIM_RT_GP;YD_MTL_T;YD_MTL_W;YD_MTL_L;YD_MTL_WT";
				slabUtils.printParam(logId + " 크레인작업재료 조회", jsCrnMtl, logItem);
			}
			
			int cntCrnMtl = jsCrnMtl.size();	//크레인작업재료수
			int cntCrnMtlEver = jsCrnMtl.size();//크레인작업재료수
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
			
			slabUtils.printLog(logId, " 3. 작업예약재료 추출 >>> Max권상모음순서(maxYdUpCollSeq) : "+maxYdUpCollSeq, "SL");
			
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
				slabUtils.printParam(logId + " 주작업예약재료", jsWbMtl);
				slabUtils.printLog(logId, " 3.작업예약재료추출 완료 : 크레인작업재료수[" + cntCrnMtl + "], 작업예약재료수[" + cntWbMtl + "]", "SL");
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
			jrHdLot.setField("UP_LOT"            , jsWbMtl           );	//상위 Lot(주작업예약재료)
			
			//권상모음재료(주작업예약재료)의 Handling Lot 분할
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
			slabUtils.printLog(logId, "▼FOR+▼ 4.1 크레인작업재료 삭제 및 이적후 모음작업대상 Lot 추가 ", "SL");
			for (int ii = 0; ii < cntDivLot; ii++) {
				lotGrpNo  = (9001 + cntDivLot - ii) * 10;
				lotNo     = 0;
				jsInsLot  = JDTORecordFactory.getInstance().createRecordSet("");
				jsDivLot  = (JDTORecordSet)vcDivLot.get(ii);
				cntDivMtl = jsDivLot.size();
				
				//마지막 Row의 야드To위치결정방법
				String ydToLocDcsnMtd_last = jsDivLot.getRecord(cntDivMtl - 1).getFieldString("YD_TO_LOC_DCSN_MTD");
				slabUtils.printLog(logId, "     ●SL1● ii<cntDivLot ["+ii+"<"+cntDivLot+"], lotGrpNo ["+lotGrpNo+"], 마지막 Row의 야드To위치결정방법 ["+ydToLocDcsnMtd_last+"]", "SL");
				//마지막 Row의 야드To위치결정방법이 'X'이면 Lot추가 Skip
				if (!"X".equals(ydToLocDcsnMtd_last)) {
					/****************************************************************
					* 권상모음  : A -> B, AB -> C
					****************************************************************/
					for (int jj = 0; jj < cntDivMtl; jj++) {
						jrDivMtl = jsDivLot.getRecord(jj);
						ydToLocDcsnMtd = jrDivMtl.getFieldString("YD_TO_LOC_DCSN_MTD");
						slabUtils.printLog(logId, "          ●SL2● jj<cntDivMtl ["+jj+"<"+cntDivMtl+"], ydToLocDcsnMtd ["+ydToLocDcsnMtd+"]", "SL");
						if (jj == (cntDivMtl - 1)) {
							//Lot내 마지막 Row이면
							if ("S".equals(ydToLocDcsnMtd) || "B".equals(ydToLocDcsnMtd) || "A".equals(ydToLocDcsnMtd)) {
								//야드To위치결정방법이 'S', 'B', 'A'(입고시 연속된 재료 등) 이면
								//권상모음Bed사양 Check
								jrBedSpec.setField("LOT_MTL", jsDivLot);
								bedSpecOvGp = this.chkCollBedSpec(jrBedSpec);
								if (!"".equals(bedSpecOvGp)) {
									slabUtils.printLog(logId, "               ●SL3● 권상모음Lot[" + slabUtils.format(lotGrpNo + lotNo + 1, "00000") + "] Bed사양 초과 : " + bedSpecOvGp, "SL");
								}
							} else {
								//야드To위치결정방법이 'M', 'T'
								bedSpecOvGp = "MT";
							}

							slabUtils.printLog(logId, "               ●SL3● Bed사양 초과(bedSpecOvGp) : " + bedSpecOvGp, "SL");

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
							slabUtils.printLog(logId, "               ●SL4● 추가 Lot 재료수(cntInsMtl) : " + cntInsMtl, "SL");
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
					slabUtils.printLog(logId, "               ●SL5● ydToLocDcsnMtd ["+ydToLocDcsnMtd+"]", "SL");
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

			slabUtils.printLog(logId, "▲FOR-▲ 4.1 크레인작업재료 삭제 및 이적후 모음작업대상 Lot 추가 ", "SL");
			
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
			
			slabUtils.printLog(logId, "▼FOR+▼ 5. 크레인작업재료 Handling Lot 편성", "SL");
			for (int ii = 0; ii < cntCrnMtl; ii++) {
				jrCrnMtl = jsCrnMtl.getRecord(ii);
				
				//To위치결정방법 Check
				curBedSeq         = jrCrnMtl.getFieldInt("BED_SEQ");
				curYdUpCollSeq    = jrCrnMtl.getFieldInt("YD_UP_COLL_SEQ");
				curYdToLocDcsnMtd = jrCrnMtl.getFieldString("YD_TO_LOC_DCSN_MTD");
				szScheduleCode    = jrCrnMtl.getFieldString("YD_SCH_CD");
				
				newLotYn = false;

				slabUtils.printLog(logId, "     ●SL1● ii<cntCrnMtl ["+ii+"<"+cntCrnMtl+"], curBedSeq ["+curBedSeq+"], curYdUpCollSeq ["+curYdUpCollSeq+"], curYdToLocDcsnMtd ["+curYdToLocDcsnMtd+"]", "SL");
				slabUtils.printLog(logId, "     ●SL1● ii<cntCrnMtl ["+ii+"<"+cntCrnMtl+"], befBedSeq ["+befBedSeq+"], befYdUpCollSeq ["+befYdUpCollSeq+"], befYdToLocDcsnMtd ["+befYdToLocDcsnMtd+"]", "SL");
				
				if ((curBedSeq != befBedSeq) ||						//동일 Bed가 아니고
				    (curYdUpCollSeq > 0 && befYdUpCollSeq == 0) ||	//이전/이후 권상모음순서가 서로 다르면
				    (curYdUpCollSeq == 0 && befYdUpCollSeq > 0)) {
					newLotYn = true;
				} else if (curYdUpCollSeq > 0 && befYdUpCollSeq > 0 && !"A".equals(befYdToLocDcsnMtd)) {
					newLotYn = true;
				}
				
				slabUtils.printLog(logId, "     ●SL1● 신규 Lot 확인(newLotYn) ["+newLotYn+"]", "SL");
				
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
			
			slabUtils.printLog(logId, "▲FOR-▲ 5. 크레인작업재료 Handling Lot 편성", "SL");
			
			if ("Y".equals(crnSchLogYn)) {
				slabUtils.printLog(logId, " 5.크레인작업재료Lot : 크레인작업재료Lot수[" + vcRstLot.size() + "]", "SL");
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
			
			slabUtils.printLog(logId, "▼FOR+▼ 7. 권상, 권하지시위치 설정 (가상) : cntDivLot == " + cntDivLot, "SL");
			for (int ii = cntDivLot - 1; ii >= 0; ii--) {
				jsDivLot  = (JDTORecordSet)vcRstLot.get(ii);
				cntDivMtl = jsDivLot.size();
				cntMvMtl += cntDivMtl;
				jrInsMtlTop =jsDivLot.getRecord(0);//최상단 지시 재료 레이어 번호 가져오기
				//Lot 마지막 Record
				jrInsMtl = jsDivLot.getRecord(cntDivMtl - 1);
				ydToLocDcsnMtd = jrInsMtl.getFieldString("YD_TO_LOC_DCSN_MTD");	//야드To위치결정방법(대표)
				stlNo          = jrInsMtl.getFieldString("STL_NO"            );	//재료번호
				ydUpWoLoc      = jrInsMtl.getFieldString("YD_UP_WO_LOC"      );	//야드권상지시위치 현재는 
				ydUpWoLayer    = jrInsMtl.getFieldString("YD_UP_WO_LAYER"    );	//야드권상지시단 
				szScheduleCode = jrInsMtl.getFieldString("YD_SCH_CD");
				ydDnWoLoc      = "";
				ydDnWoLayer    = "";
				
				slabUtils.printLog(logId, "     ●SL1● ii>=0 ["+ii+">=0], cntDivLot ["+cntDivLot+"]", "SL");
				slabUtils.printLog(logId, "     ●SL1● Lot 마지막 Record : ydToLocDcsnMtd ["+ydToLocDcsnMtd+"], stlNo ["+stlNo+"], ydUpWoLoc ["+ydUpWoLoc+"], ydUpWoLayer ["+ydUpWoLayer+"], szScheduleCode ["+szScheduleCode+"]", "SL");
				
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
					slabUtils.printLog(logId, "          ●SL2● ydDnWoLoc ["+ydDnWoLoc+"]", "SL");
					
					//권하지시위치 : To위치결정위치
					if ("".equals(ydDnWoLoc)) {
						ydDnWoLoc = "XX0101" + slabUtils.format(++bedNo, "00");
					}
					ydDnWoLayer = "001";
					slabUtils.printLog(logId, "          ●SL2● ydDnWoLoc ["+ydDnWoLoc+"], ydDnWoLayer ["+ydDnWoLayer+"]", "SL");
				} else if ("S".equals(ydToLocDcsnMtd)) {
					//주작업(최종) : 현재적치위치('T' To위치결정위치) -> 최종위치
					befYdToLocDcsnMtd = jrInsMtl.getFieldString("BEF_YD_TO_LOC_DCSN_MTD");	//원래 야드To위치결정방법
					slabUtils.printLog(logId, "          ●SL3● befYdToLocDcsnMtd ["+befYdToLocDcsnMtd+"]", "SL");
					//원 야드To위치결정방법이 'S','B','A'가 아니면('M','T') 이적후 Base가 된 형태이므로 권상지시위치는 이전 Lot('T')의 권하지시위치가 됨
					if ("M".equals(befYdToLocDcsnMtd) || "T".equals(befYdToLocDcsnMtd)) {
						ydUpWoLoc = "XX0101" + slabUtils.format(++bedNo, "00");
						ydUpWoLayer = "001";
					}
					slabUtils.printLog(logId, "          ●SL3● ydUpWoLoc ["+ydUpWoLoc+"], ydUpWoLayer ["+ydUpWoLayer+"], ydToLocGuide ["+ydToLocGuide+"], ydDnWoLoc ["+ydDnWoLoc+"]", "SL");
					
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
						slabUtils.printLog(logId, "          ●SL3● ydDnWoLoc ["+ydDnWoLoc+"]", "SL");
						//권하지시위치 : To위치결정위치
						if ("".equals(ydDnWoLoc)) {
							ydDnWoLoc = "XX0101" + slabUtils.format(++bedNo, "00");
						}
						ydDnWoLayer = "001";
						
					} else {
						//야드To위치Guide 값이 있으면 지정된 위치로
						ydDnWoLoc = ydToLocGuide;
						ydDnWoLayer = slabUtils.format(jrInsMtl.getFieldInt("YD_UP_COLL_SEQ") - cntDivMtl + 1, "000");
					}
					slabUtils.printLog(logId, "          ●SL3● ydDnWoLoc ["+ydDnWoLoc+"], ydDnWoLayer ["+ydDnWoLayer+"]", "SL");
					
				} else if ("X".equals(ydToLocDcsnMtd) || "Y".equals(ydToLocDcsnMtd)) {
					//권하분리작업 : 상위Lot의 권하위치 -> To위치결정위치
					//권상지시위치 : 현재Lot 권하분리여부에 따라 결정
					ydUpWoLoc = "XX0101" + slabUtils.format(++bedNo, "00");
					//권상지시단 : 이전Lot 재료수 - 현재Lot 재료수 + 1
					jsCrnMtl = (JDTORecordSet)vcRstLot.get(ii - 1);
					ydUpWoLayer = slabUtils.format(jsCrnMtl.size() - cntDivMtl + 1, "000");

					slabUtils.printLog(logId, "          ●SL4● ydUpWoLoc ["+ydUpWoLoc+"], ydUpWoLayer ["+ydUpWoLayer+"]", "SL");
					
					//권하지시위치 : 다음Lot 권하분리여부에 따라 결정
					if (ii < cntDivLot - 1) {
						//다음Lot 권하분리
						jsCrnMtl = (JDTORecordSet)vcRstLot.get(ii + 1);
						befYdToLocDcsnMtd = jsCrnMtl.getRecord(jsCrnMtl.size() - 1).getFieldString("YD_TO_LOC_DCSN_MTD");
						if ("X".equals(befYdToLocDcsnMtd) || "Y".equals(befYdToLocDcsnMtd)) {
							ydDnWoLoc = jsCrnMtl.getRecord(0).getFieldString("YD_UP_WO_LOC");
						}
					}
					slabUtils.printLog(logId, "          ●SL4● ydDnWoLoc ["+ydDnWoLoc+"]", "SL");
					
					//권하지시위치 : To위치결정위치
					if ("".equals(ydDnWoLoc)) {
						ydDnWoLoc = "XX0101" + slabUtils.format(++bedNo, "00");
						
					}
					ydDnWoLayer = "001";
					slabUtils.printLog(logId, "          ●SL4● ydDnWoLoc ["+ydDnWoLoc+"], ydDnWoLayer ["+ydDnWoLayer+"]", "SL");
					
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

					slabUtils.printLog(logId, "          ●SL5● ydUpWoLoc ["+ydUpWoLoc+"], ydUpWoLayer ["+ydUpWoLayer+"]", "SL");
					
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
					slabUtils.printLog(logId, "          ●SL5● ydDnWoLoc ["+ydDnWoLoc+"], ydDnWoLayer ["+ydDnWoLayer+"]", "SL");
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
			slabUtils.printLog(logId, "▲FOR-▲ 7. 권상, 권하지시위치 설정 (가상)", "SL");
			
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
	 *      염용선 2020 06 12
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrUpLot
	 *      @param Vector vcLot
	 *      @return void
	 *      @throws DAOException
	*/
	public void setSlabYdHdLotSpr(JDTORecord jrUpLot, Vector vcLot) throws DAOException {
		String methodNm = "HandlingLot분리[PSlabYdSchSeEJB.setSlabYdHdLotSpr] < " + jrUpLot.getResultMsg();
		String logId = jrUpLot.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			
			JDTORecord    jrRow = null;	//현재 Row
			JDTORecordSet jsLot = JDTORecordFactory.getInstance().createRecordSet("");	//Lot
			
			//Parameter
			int lotNo   = 1; 										//Lot번호
			int upLotNo = jrUpLot.getFieldInt("UP_LOT_NO") * 100;	//상위 Lot번호
			String crnSchLogYn = jrUpLot.getFieldString("CRN_SCH_LOG_YN");	//크레인스케줄Log여부

			JDTORecordSet jsUpLot   = (JDTORecordSet)jrUpLot.getField("UP_LOT");	//상위 Lot(주작업예약재료)
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
				slabUtils.printLog(logId, "  HandlingLot분리  >> Lot 마지막 Row 야드To위치결정방법 [" + lstYdToLocDcsnMtd + "], HandlingLot분리구분 ["+hdLotSprGp+"]", "SL");
				slabUtils.printLog(logId, "  HandlingLot분리  >> 상위Lot[" + upLotNo + "0-" + rowCnt + "건], Lot분리구분[" + hdLotSprGp + "]", "SL");
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
//SJH: BRE 확인 사용안함 2016.08 이후 					
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
							slabUtils.printLog(logId, "    2-Lot분할-목표행선[R]   >> 상위Lot[" + (upLotNo + lotNo) + "0-" + jsLot.size() + "건], 분할구분[RT-" + aimRtChgGp + "]", "SL");
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
					slabUtils.printLog(logId, "    2-Lot분할-목표행선[R]   >> 상위Lot[" + (upLotNo + lotNo) + "0-" + jsLot.size() + "건]", "SL");
				}

				//마지막 Lot 크레인사양Lot분리
				jrHdLot.setField("UP_LOT_NO", String.valueOf(upLotNo + lotNo)); //상위 Lot번호
				jrHdLot.setField("UP_LOT"   , jsLot                          ); //상위 Lot
				this.setSlabYdHdLotSprCs(jrHdLot, vcLot);
			} else if ("X".equals(hdLotSprGp)) {
//SJH:BRE 확인 사용안함 2016.08 이후 				
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
								slabUtils.printLog(logId, "    2-Lot분할-권하분리[X]   >> 상위Lot[" + (upLotNo + lotNo) + "0-" + jsLot.size() + "건], 분할구분[CS-" + crnSpecOvGp + "]", "SL");
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
					slabUtils.printLog(logId, "    2-Lot분할-권하분리[X]   >> 상위Lot[" + (upLotNo + lotNo) + "0-" + jsLot.size() + "건]", "SL");
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
					slabUtils.printLog(logId, "    2-Lot분할-분리안함[N]   >> 상위Lot[" + (upLotNo + lotNo) + "0-" + jsLot.size() + "건]", "SL");
				}
				
				//크레인사양Lot분리
				jrHdLot.setField("UP_LOT_NO", String.valueOf(upLotNo + 1)); //상위 Lot번호
				jrHdLot.setField("UP_LOT"   , jsLot                      ); //상위 Lot(주작업예약재료)
				this.setSlabYdHdLotSprCs(jrHdLot, vcLot);
			}

			slabUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 Handling Lot 편성 - 권하분리(SJH:BRE 확인 사용안함 2016.08 이후) 		
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrUpLot
	 *      @param Vector vcLot
	 *      @return void
	 *      @throws DAOException
	*/
	public void setSlabYdHdLotSprDn(JDTORecord jrUpLot, Vector vcLot) throws DAOException {
		String methodNm = "권하분리[PSlabYdSchSeEJB.setSlabYdHdLotSprDn] < " + jrUpLot.getResultMsg();
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
	 *      염용선 2020-10-28
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrUpLot
	 *      @param Vector vcLot
	 *      @return void
	 *      @throws DAOException
	*/
	public void setSlabYdHdLotSprCs(JDTORecord jrUpLot, Vector vcLot) throws DAOException {
		String methodNm = "크레인사양Lot분리[PSlabYdSchSeEJB.setSlabYdHdLotSprCs] < " + jrUpLot.getResultMsg();
		String logId = jrUpLot.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			
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

			if ("Y".equals(crnSchLogYn)) {
				slabUtils.printParam(logId + " 크레인사양Lot분리 Param : jrUpLot", jrUpLot);
				slabUtils.printParam(logId + " 크레인사양Lot분리 Param : 상위 Lot(주작업예약재료)", jsUpLot);
				slabUtils.printParam(logId + " 크레인사양Lot분리 Param : 크레인사양", jrCrnSpec);
			}
			
			int rowCnt = jsUpLot.size();
			String Is_plate_StoA_Scarfing = "N"; 
			
			/**********************************************************
			* 1. 크레인사양 Lot 편성
			*  - Max단, Max중량, Max높이, 폭 허용공차 초과시 Lot분리
			**********************************************************/
			slabUtils.printLog(logId, "▼FOR+▼ 1. 크레인사양 Lot 편성", "SL");
			for (int ii = 0; ii < rowCnt; ii++) {
				jrRow = jsUpLot.getRecord(ii);
				
				mtlWt = Integer.parseInt(slabUtils.nvl(jrRow.getFieldString("YD_MTL_WT"),"0"));
				mtlT  = Float.parseFloat(slabUtils.nvl(jrRow.getFieldString("YD_MTL_T" ),"0"));
				mtlW  = Float.parseFloat(slabUtils.nvl(jrRow.getFieldString("YD_MTL_W" ),"0"));
				Is_plate_StoA_Scarfing = slabUtils.nvl(jrRow.getFieldString("PLATE_SIDE_SCARFING_YN" ),"N");
				
				slabUtils.printLog(logId, "     ●SL1● ii<rowCnt ["+ii+"<"+rowCnt+"], 크레인사양Lot 편성 : 재료 중량[" + mtlWt + "], " + "재료두께[" + mtlT + "], " + "재료폭[" + mtlW + "], 스카핑["+Is_plate_StoA_Scarfing+"]", "SL");
				
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
					
					slabUtils.printLog(logId, "          ●SL2● 크레인사양 초과 Check : 크레인사양초과구분(crnSpecOvGp) [" + crnSpecOvGp + "]", "SL");
					
					if (!"".equals(crnSpecOvGp)) {
						//이전 Lot 추가
						vcLot.add(jsLot);

						if ("Y".equals(crnSchLogYn)) {
							slabUtils.printLog(logId, "          ●SL3● 이전 Lot 추가  >> 분할Lot[" + (upLotNo + lotNo) + "-" + jsLot.size() + "건], 분할구분[CS-" + crnSpecOvGp + "]", "SL");
							slabUtils.printParam(logId + " 크레인사양Lot 편성  >> 이전 Lot 추가  >> 분할Lot[" + (upLotNo + lotNo) + "-" + jsLot.size() + "건]", jsLot);
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
			slabUtils.printLog(logId, "▲FOR-▲ 1. 크레인사양 Lot 편성", "SL");
			
			//마지막 Lot 추가
			vcLot.add(jsLot);
			
			if ("Y".equals(crnSchLogYn)) {
				slabUtils.printLog(logId, " 크레인사양Lot분리  >> 분할Lot[" + (upLotNo + lotNo) + "-" + jsLot.size() + "건]", "SL");
				slabUtils.printParam(logId + " 크레인사양Lot분리  >> 분할Lot[" + (upLotNo + lotNo) + "-" + jsLot.size() + "건]", jsLot);
			}
			
			slabUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 목표행선 Check (SJH: BRE 확인 사용안함 2016.08 이후) 
	 *      염용선  2020-10-28
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param String logId
	 *      @param String mthdNm
	 *      @param JDTORecord jrBefRow
	 *      @param JDTORecord jrCurRow
	 *      @return String 
	 *      @throws DAOException
	*/
	public String chkYdAimRtGp(String logId, String mthdNm, JDTORecord jrBefRow, JDTORecord jrCurRow) throws DAOException {
		String methodNm = "목표행선Check[PSlabYdSchSeEJB.chkYdAimRtGp] < " + mthdNm;
    	
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
				slabUtils.printLog(logId, warnMsg, "SL");
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
		String methodNm = "권상모음Bed사양Check[PSlabYdSchSeEJB.chkCollBedSpec] < " + jrBedSpec.getResultMsg();
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
					//Max단초과매수(단수) 체크 : com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401BedSpec -> USRYDA.TB_YD_RULE
					if ((ydMtlShBase + mtlShSum) > ydStkBedLyrMax) {
						bedSpecOvGp = "SH";
					}
					
					return bedSpecOvGp;
				}
			}
			
			return bedSpecOvGp;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

//	/**
//	 *      [A] 오퍼레이션명 : 크레인스케줄 작업예약재료 수정(
//	 *      염용선 2020-10-28
//	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 *      @param JDTORecord rcvMsg
//	 *      @return void
//	 *      @throws DAOException
//	 *      @ejb.transaction type="RequiresNew"
//	*/
//	public int updCrnSchWB(JDTORecord jrParam) throws DAOException {
//		String methodNm = "크레인스케줄 작업예약재료 수정[PSlabYdSchSeEJB.rcvYDYDJ411(updCrnSchWB)] < " + jrParam.getResultMsg();
//		String logId = jrParam.getResultCode();
//		String trtNm = "작업예약재료(TB_YD_WRKBOOKMTL) 저장위치 수정";
//		try {
//			//schDao.updYDYDJ400("WmStrLoc", jrParam);
//			
//			 /*크레인스케줄 작업예약재료 저장위치 수정 
//				MERGE INTO TB_YD_WRKBOOKMTL WM USING (
//				SELECT YD_WBOOK_ID
//				      ,STL_NO
//				      ,:V_MODIFIER AS MODIFIER
//				      ,SYSDATE     AS MOD_DDTT
//				      ,YD_STK_COL_GP
//				      ,YD_STK_BED_NO
//				      ,YD_STK_LYR_NO
//				      ,ROWNUM AS YD_UP_COLL_SEQ
//				  FROM (SELECT WM.*
//				               --이적 스케줄이고 장입번호가 2종류 이상일 경우 변경
//				              ,CASE WHEN SUBSTR(WM.YD_SCH_CD,3,2) = 'YD' AND COUNT(DISTINCT WM.YD_CHG_NO) OVER () > 1 THEN
//				                         NVL(WM.YD_CHG_NO
//				                        ,NVL(CASE WHEN WM.YD_STK_COL_BED = LAG(WM.YD_STK_COL_BED,1) OVER (ORDER BY WM.YD_STR_LOC DESC)
//				                                  THEN LAG(WM.YD_CHG_NO,1) OVER (ORDER BY WM.YD_STR_LOC DESC) END
//				                        ,NVL(CASE WHEN WM.YD_STK_COL_BED = LAG(WM.YD_STK_COL_BED,2) OVER (ORDER BY WM.YD_STR_LOC DESC)
//				                                  THEN LAG(WM.YD_CHG_NO,2) OVER (ORDER BY WM.YD_STR_LOC DESC) END
//				                        ,NVL(CASE WHEN WM.YD_STK_COL_BED = LAG(WM.YD_STK_COL_BED,3) OVER (ORDER BY WM.YD_STR_LOC DESC)
//				                                  THEN LAG(WM.YD_CHG_NO,3) OVER (ORDER BY WM.YD_STR_LOC DESC) END
//				                        ,NVL(CASE WHEN WM.YD_STK_COL_BED = LEAD(WM.YD_STK_COL_BED,1) OVER (ORDER BY WM.YD_STR_LOC DESC)
//				                                  THEN LEAD(WM.YD_CHG_NO,1) OVER (ORDER BY WM.YD_STR_LOC DESC) END
//				                        ,NVL(CASE WHEN WM.YD_STK_COL_BED = LEAD(WM.YD_STK_COL_BED,2) OVER (ORDER BY WM.YD_STR_LOC DESC)
//				                                  THEN LEAD(WM.YD_CHG_NO,2) OVER (ORDER BY WM.YD_STR_LOC DESC) END
//				                        ,NVL(CASE WHEN WM.YD_STK_COL_BED = LEAD(WM.YD_STK_COL_BED,3) OVER (ORDER BY WM.YD_STR_LOC DESC)
//				                                  THEN LEAD(WM.YD_CHG_NO,3) OVER (ORDER BY WM.YD_STR_LOC DESC) END
//				                        ,999999)))))))
//				                   ELSE WM.YD_UP_COLL_SEQ END AS YD_CHG_NO_SEQ
//				          FROM (SELECT WB.YD_WBOOK_ID
//				                      ,WB.YD_SCH_CD
//				                      ,WM.STL_NO
//				                      ,WM.YD_UP_COLL_SEQ
//				                      ,SL.YD_STK_COL_GP
//				                      ,SL.YD_STK_BED_NO
//				                      ,SL.YD_STK_LYR_NO
//				                      ,SL.YD_STK_COL_GP||SL.YD_STK_BED_NO||SL.YD_STK_LYR_NO AS YD_STR_LOC
//				                      ,SL.YD_STK_COL_GP||SL.YD_STK_BED_NO AS YD_STK_COL_BED
//				                      ,CASE WHEN ST.YD_STK_LOT_CD LIKE 'C%' THEN TO_NUMBER(SUBSTR(ST.YD_STK_LOT_CD,3)) END AS YD_CHG_NO
//				                  FROM TB_YD_WRKBOOK WB
//				                      ,TB_YD_WRKBOOKMTL WM
//				                      ,TB_YD_STKLYR     SL
//				                      ,TB_YD_STOCK      ST
//				                 WHERE WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
//				                   AND WM.STL_NO      = SL.STL_NO
//				                   AND WM.STL_NO      = ST.STL_NO
//				                   AND WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
//				                   AND WB.DEL_YN      = 'N'
//				                   AND WM.DEL_YN      = 'N'
//				                   AND NVL(SL.DEL_YN,'*')  <> 'Y'  --추가 : 2016.01.20 
//				                   AND SL.YD_STK_LYR_MTL_STAT = 'C'
//				                   AND WB.YD_GP=SUBSTR(SL.YD_STK_COL_GP,1,1)
//				                 ORDER BY YD_STR_LOC DESC) WM
//				         ORDER BY YD_CHG_NO_SEQ, YD_STR_LOC DESC)
//				) DD ON (WM.YD_WBOOK_ID = DD.YD_WBOOK_ID AND WM.STL_NO = DD.STL_NO)
//				WHEN MATCHED THEN UPDATE SET
//				     WM.MODIFIER       = DD.MODIFIER
//				    ,WM.MOD_DDTT       = DD.MOD_DDTT
//				    ,WM.YD_STK_COL_GP  = DD.YD_STK_COL_GP
//				    ,WM.YD_STK_BED_NO  = DD.YD_STK_BED_NO
//				    ,WM.YD_STK_LYR_NO  = DD.YD_STK_LYR_NO
//				    --,WM.YD_UP_COLL_SEQ = DD.YD_UP_COLL_SEQ
//			 */
//			int intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchDAO.updYDYDJ401WmStrLoc", logId, methodNm, trtNm);
//			return intRtnVal;
//		} catch (DAOException e) {
//			throw e;
//		} catch (Exception e) {
//			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
//		}
//	}


	/***************************************************************************
	 * 야드관리(YD) 내부
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 설비인출요구(YDYDJ410 > 변경 YDYDJ411 )
	 *      염용선 2020-09-08
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYDYDJ411(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "설비인출요구[PSlabYdSchSeEJB.rcvYDYDJ411] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId      = slabUtils.getMsgId(rcvMsg);									//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId    = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     ));	//야드설비ID
			String ydStkBedNo = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO" ));	//야드적치Bed번호
			String ydSchStGp  = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_ST_GP"  ));	//야드스케쥴기동구분
			String sModifier  = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"      ));	//수정자(Backup Only)
			String sToLocSpan = slabUtils.trim(rcvMsg.getFieldString("YD_TO_LOC_SPAN"));	//입고  SPAN 번호 (01/02/03/04)
			String sToLocTo12 = slabUtils.trim(rcvMsg.getFieldString("YD_TO_LOC_12"));	//L:장척재 S:협폭재 Y:일반
			String ydSlabT    = "";//slabUtils.trim(rcvMsg.getFieldString("YD_SLAB_T"));	//L:장척재 S:협폭재 Y:일반 
			JDTORecord jrRtn  = slabUtils.getParam(logId, methodNm, sModifier);
			
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			String ydWrkPlanCrn = slabUtils.trim(rcvMsg.getFieldString("YD_WRK_PLAN_CRN")); //작업크레인
			String stlNos	    = slabUtils.trim(rcvMsg.getFieldString("STL_NOS")); //재료번호

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "설비ID(YD_EQP_ID) 이상 : [" + ydEqpId + "]");	
				return jrRtn;
			} else if ("".equals(ydStkBedNo)) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "적치Bed번호(YD_STK_BED_NO) 없음");	
				return jrRtn;
			}
			
			//반환 결과 등
			
			
			String ydAimYdGp   = ""; //야드목표야드구분
			String ydAimBayGp  = ""; //야드목표동구분
			String ydHomeBayGp = ""; //야드Home동구분
			//대차 Pickup Bed일 경우 대차 설비ID조회
			String tcEqpId = "";
			
			slabUtils.printLog(logId, "YD_EQP_ID : ["+ydEqpId+"] YD_STK_BED_NO :["+ydStkBedNo+"] YD_SCH_ST_GP : ["+ydSchStGp+"] YD_TO_LOC_SPAN : ["+sToLocSpan+"]", "SL>>>>>>>>>>>>>");
			
			//조회 및 등록 용
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);
			jrParam.setField("YD_EQP_ID"    , tcEqpId   ); //대차설비ID
			jrParam.setField("YD_STK_COL_GP", ydEqpId   ); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO", ydStkBedNo); //야드적치Bed번호
			jrParam.setField("YD_SCH_ST_GP" , ydSchStGp ); //야드스케쥴기동구분
			
			//작업예약 기 등록여부 등 Check
			/*
			 * --설비인출요구 작업예약 조회 
				SELECT SL.STL_NO
				      ,ST.YD_AIM_YD_GP
				      ,ST.YD_AIM_BAY_GP
				      ,DECODE((SELECT COUNT(*)
				                 FROM TB_YD_STKLYR     SS
				                     ,TB_YD_WRKBOOKMTL WM
				                     ,TB_YD_WRKBOOK    WB
				                WHERE SS.STL_NO        = WM.STL_NO
				                  AND WM.YD_WBOOK_ID   = WB.YD_WBOOK_ID
				                  AND SS.YD_STK_COL_GP = SL.YD_STK_COL_GP
				                  AND SS.YD_STK_BED_NO = SL.YD_STK_BED_NO
				                  AND WM.DEL_YN        = 'N'
				                  AND WB.DEL_YN        = 'N'),0,'N','Y') AS WB_STL_YN --작업예약재료여부
				      ,(SELECT YD_HOME_BAY_GP
				          FROM TB_YD_EQP EQ
				         WHERE YD_EQP_ID = :V_YD_EQP_ID
				           AND DEL_YN    = 'N') AS YD_HOME_BAY_GP
				  FROM TB_YD_STKLYR SL
				      ,TB_YD_STOCK  ST
				 WHERE SL.STL_NO        = ST.STL_NO
				   AND SL.YD_STK_COL_GP = :V_YD_STK_COL_GP
				   AND SL.YD_STK_BED_NO = :V_YD_STK_BED_NO
				   AND SL.YD_STK_LYR_NO = '001'

			 */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ411WB", logId, methodNm, "작업예약 조회");
	    	
			if (jsChk != null && jsChk.size() > 0) {
				JDTORecord jrChk = jsChk.getRecord(0);
				
				if ("Y".equals(slabUtils.trim(jrChk.getFieldString("WB_STL_YN")))) {
					//throw new Exception("이미 작업예약에 등록된 재료가 존재합니다.");
					slabUtils.printLog(logId, "이미 작업예약에 등록된 재료가 존재합니다.", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "이미 작업예약에 등록된 재료가 존재합니다.");	
					return jrRtn;
				}

				ydAimYdGp   = slabUtils.trim(jrChk.getFieldString("YD_AIM_YD_GP"  )); //야드목표야드구분
				ydAimBayGp  = slabUtils.trim(jrChk.getFieldString("YD_AIM_BAY_GP" )); //야드목표동구분
				ydHomeBayGp = slabUtils.trim(jrChk.getFieldString("YD_HOME_BAY_GP")); //야드Home동구분
				
				if ("".equals(ydAimBayGp)) {
					//throw new Exception("재료번호[" + slabUtils.trim(jrChk.getFieldString("STL_NO")) + "]의 야드목표동구분이 없습니다.");
					slabUtils.printLog(logId, "재료번호[" + slabUtils.trim(jrChk.getFieldString("STL_NO")) + "]의 야드목표동구분이 없습니다.", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "재료번호[" + slabUtils.trim(jrChk.getFieldString("STL_NO")) + "]의 야드목표동구분이 없습니다.");	
					return jrRtn;
				}
			} else {
				//throw new Exception("적치Bed[" + ydEqpId + "-" + ydStkBedNo + "] 재료정보가 없습니다.");
				slabUtils.printLog(logId, "적치Bed[" + ydEqpId + "-" + ydStkBedNo + "] 재료정보가 없습니다.", "SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "적치Bed[" + ydEqpId + "-" + ydStkBedNo + "] 재료정보가 없습니다.");	
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
				jrParam.setField("YD_SCH_WHIO_GP", "L"); //야드스케쥴입출고구분(입고)

				JDTORecord jrSchCd = slabComm.getSchCd(jrParam);
				String rtnCd	 = slabUtils.nvl(jrSchCd.getFieldString("RTN_CD"), "0");
				String rtnMsg	 = slabUtils.nvl(jrSchCd.getFieldString("RTN_MSG"), "");
				if (!"1".equals(rtnCd)) {
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", rtnMsg);	
					return jrRtn;
				}
				String ydSchCd    = jrSchCd.getFieldString("YD_SCH_CD"   ); //야드스케쥴코드
				String ydSchPrior = jrSchCd.getFieldString("YD_SCH_PRIOR"); //야드스케쥴우선순위
				
				/**********************************************************
				* 2.1 작업예약 등록
				**********************************************************/
				//작업예약ID 생성
				String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
				
				if ("".equals(ydWbookId)) {
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "작업예약ID 생성 실패");	
					return jrRtn;
				}

				slabUtils.printLog(logId, "작업예약 등록 > YD_TO_LOC_GUIDE : ydAimYdGp["+ydAimYdGp+"] ydAimBayGp["+ydAimBayGp+"] sToLocSpan["+sToLocSpan+"]  sToLocTo12["+sToLocTo12+"]" , "SL");

				String sToLocGuide = ""; 
				if (!"".equals(sToLocSpan)) {
					sToLocGuide = ydAimYdGp + ydAimBayGp + sToLocSpan;
				}else{
					if("L".equals(sToLocTo12)){//장척재 (4560 이상)일때
						/*
						 * SELECT LOC_GUIDE
							  FROM (
							            SELECT A.YD_STK_COL_GP || A.YD_STK_BED_NO AS LOC_GUIDE
							              FROM (
							                        SELECT YD_STK_COL_GP, YD_STK_BED_NO, COUNT(STL_NO) AS STLCNT
							                          FROM TB_YD_STKLYR
							                         WHERE 1=1 
							                           AND YD_STK_COL_GP LIKE 'DA02%'
							                           AND YD_STK_BED_NO = '05'
							                           AND DEL_YN  = 'N'
							                         GROUP BY YD_STK_COL_GP, YD_STK_BED_NO
							                   ) A   --권하위치 Bed List
							                   , TB_YD_STKBED B
							             WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP
							               AND A.YD_STK_BED_NO = B.YD_STK_BED_NO
							               AND B.YD_STK_BED_LYR_MAX > STLCNT
							             ORDER BY STLCNT
							       )
							 WHERE ROWNUM =1
						 */
//						JDTORecordSet jsToLocChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getLocGuide", logId, methodNm, "장척재 특정 권하위치 조회");
//				    	String toLocChk = "";
//						if (jsToLocChk != null && jsToLocChk.size() > 0) {
//							JDTORecord jrToLocChk = jsToLocChk.getRecord(0);
//							sToLocGuide = slabUtils.trim(jrToLocChk.getFieldString("LOC_GUIDE"));
//						}
						
						ydSchCd = "DAL199LM";
					}
				}

				
				if("DAPU02".equals(ydEqpId) || "DAPU04".equals(ydEqpId) || "DBPU06".equals(ydEqpId)){
					/*		SELECT SL.STL_NO
		                           ,NVL(SC.REAL_MEASURE_SLAB_T , SC.SLAB_T)  SLAB_T    -- 실측두께
						  FROM TB_YD_STKLYR SL
						      ,TB_YD_STOCK  ST
		                      ,USRPTA.TB_PT_SLABCOMM SC
						 WHERE SL.STL_NO        = ST.STL_NO
		                   AND SL.STL_NO        = SC.SLAB_NO
						   AND SL.YD_STK_COL_GP = :V_YD_STK_COL_GP
						   AND SL.YD_STK_BED_NO = :V_YD_STK_BED_NO
					*/
					//jrRtn.setField("YD_STK_COL_GP"    ,	ydEqpId   );		//야드설비ID
					//jrRtn.setField("YD_STK_BED_NO"    ,	ydStkBedNo);		//야드적치Bed번호
					JDTORecordSet jsSlabTChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getSlabT", logId, methodNm, "슬라브 두께 조회");
			    	
					for (int i = 0; i < jsSlabTChk.size(); i++) {
						JDTORecord jrChk = jsSlabTChk.getRecord(i);
						int slabT = Integer.parseInt(slabUtils.trim(jrChk.getFieldString("SLAB_T")));
						if(slabT > 250){
							ydSlabT = "300T";
						}
					}
					
					// 이적 슬라브 두께가 250 이상이면 특정 스케줄코드 셋팅
					if("300T".equals(ydSlabT)){
						if("DAPU02".equals(ydEqpId)){
							ydSchCd = "DAT103MM";
						}else if("DAPU04".equals(ydEqpId)){
							ydSchCd = "DAT104MM";
						}else if("DBPU06".equals(ydEqpId)){
							ydSchCd = "DBT103MM";
						}
					}else{
                       
						  if("DBPU06".equals(ydEqpId)){
							if("M".equals(ydSchStGp)){//메뉴얼 작업
								if("DBCRB2".equals(ydWrkPlanCrn)){
									ydSchCd = "DBPU04LM";
								}else{
									ydSchCd = "DBPU06LM";
								}
							}else{//지동처리시 야드별 적치슬라브 갯수로 판단
								  //적게 적치된 스판으로 스케줄 코드 생성
								  // DBPU06LM : 116 BED
								  // DBPU04LM : 58  BED
								/*
								 * SELECT * FROM								
									(
									SELECT TO_CHAR(COUNT(ST.STL_NO)               ,'FM999,990') AS MTL_SH -- 총매수
									        ,SB.YD_SCH_CD
									  FROM TB_YD_STKLYR SL
									      ,TB_YD_STOCK ST
									      ,VW_YD_SLABCOMM SC
									      ,(
									            SELECT LR.YD_SCH_CD, LB.YD_STK_COL_GP, LB.YD_STK_BED_NO
									              FROM TB_YD_LOCSRCHRNG LR
									                  ,TB_YD_LOCSRCHBED LB
									             WHERE LR.YD_LOC_SRCH_RNG_REG_SNO = LB.YD_LOC_SRCH_RNG_REG_SNO
									               AND LR.YD_SCH_CD   LIKE 'DBPU__LM%'
									               AND LR.YD_ROUTE_GP = 'DD'
									               AND LR.DEL_YN      = 'N'
									               AND LB.DEL_YN      = 'N'
									        ) SB
									 WHERE SL.STL_NO = ST.STL_NO
									   AND SL.STL_NO = SC.SLAB_NO
									   AND SB.YD_STK_COL_GP = SL.YD_STK_COL_GP
									   AND SB.YD_STK_BED_NO = SL.YD_STK_BED_NO
									   AND SL.YD_STK_LYR_MTL_STAT IN ('C','U')
									GROUP BY SB.YD_SCH_CD
									)
									WHERE 1=1
									  ORDER BY MTL_SH ASC
							  */
								
								JDTORecordSet jsSlabSum = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getSlabYdSumSchCd", logId, methodNm, "코드별 슬라브 갯수 조회");
								if (jsSlabSum != null && jsSlabSum.size() > 0) {
									JDTORecord jrChk = jsSlabSum.getRecord(0);
									ydSchCd = slabUtils.trim(jrChk.getFieldString("YD_SCH_CD"));
								}
								
								
							}
						
					  }
					}
					
				}
				slabUtils.printLog(logId, "야드To위치Guide : sToLocGuide["+sToLocGuide+"] ydSlabT["+ydSlabT+"]", "SL");
				
				
				//작업예약 등록
				jrParam.setField("YD_WBOOK_ID"       , ydWbookId             ); //야드작업예약ID
				jrParam.setField("YD_GP"             , ydEqpId.substring(0,1)); //야드구분
				jrParam.setField("YD_BAY_GP"         , ydEqpId.substring(1,2)); //야드동구분
				jrParam.setField("YD_SCH_CD"         , ydSchCd               ); //야드스케쥴코드
				jrParam.setField("YD_SCH_PRIOR"      , ydSchPrior            ); //야드스케쥴우선순위
				jrParam.setField("YD_SCH_PROG_STAT"  , "W"                   ); //야드스케쥴진행상태(스케줄수행대기)
				jrParam.setField("YD_SCH_REQ_GP"     , "L"                   ); //야드스케쥴요청구분(인출)
				jrParam.setField("YD_AIM_YD_GP"      , ydAimYdGp             ); //야드목표야드구분
				jrParam.setField("YD_AIM_BAY_GP"     , ydAimBayGp            ); //야드목표동구분
				jrParam.setField("YD_TO_LOC_DCSN_MTD", "S"                   ); //야드TO위치결정방법(스케줄기준적용)
				jrParam.setField("YD_TO_LOC_GUIDE"   , sToLocGuide           ); //야드To위치Guide
				jrParam.setField("YD_WRK_PLAN_CRN"   , ydWrkPlanCrn          ); //작업크레인
				
				jrParam.setField("YD_WRK_PLAN_TCAR"  , ""          ); //야드차량사용구분
				jrParam.setField("YD_CAR_USE_GP"     , ""          ); //야드차량사용구분
				jrParam.setField("TRN_EQP_CD"        , ""          ); //운송장비코드
				jrParam.setField("CAR_NO"            , ""          ); //차량번호
				jrParam.setField("CARD_NO"           , ""          ); //카드번호
				
				/*
				 * 작업예약 등록
					INSERT INTO TB_YD_WRKBOOK (
					  YD_WBOOK_ID        --야드작업예약ID
					 ,REGISTER           --등록자
					 ,REG_DDTT           --등록일시
					 ,MODIFIER           --수정자
					 ,MOD_DDTT           --수정일시
					 ,DEL_YN             --삭제유무
					 ,YD_GP              --야드구분
					 ,YD_BAY_GP          --야드동구분
					 ,YD_SCH_CD          --야드스케쥴코드
					 ,YD_SCH_PRIOR       --야드스케쥴우선순위
					 ,YD_SCH_PROG_STAT   --야드스케쥴진행상태
					 ,YD_SCH_ST_GP       --야드스케쥴기동구분
					 ,YD_SCH_REQ_GP      --야드스케쥴요청구분
					 ,YD_AIM_YD_GP       --야드목표야드구분
					 ,YD_AIM_BAY_GP      --야드목표동구분
					 ,YD_TO_LOC_DCSN_MTD --야드To위치결정방법
					 ,YD_TO_LOC_GUIDE    --야드To위치Guide
					 ,YD_WRK_PLAN_TCAR   --야드작업계획대차
					 ,YD_CAR_USE_GP      --야드차량사용구분
					 ,TRN_EQP_CD         --운송장비코드
					 ,CAR_NO             --차량번호
					 ,CARD_NO            --카드번호
					 ,YD_WRK_PLAN_CRN
					) VALUES (
					  :V_YD_WBOOK_ID
					 ,:V_MODIFIER
					 ,SYSDATE
					 ,:V_MODIFIER
					 ,SYSDATE
					 ,'N'
					 ,:V_YD_GP
					 ,:V_YD_BAY_GP
					 ,:V_YD_SCH_CD
					 ,TO_NUMBER(:V_YD_SCH_PRIOR)
					 ,:V_YD_SCH_PROG_STAT
					 ,:V_YD_SCH_ST_GP
					 ,:V_YD_SCH_REQ_GP
					 ,:V_YD_AIM_YD_GP
					 ,:V_YD_AIM_BAY_GP
					 ,:V_YD_TO_LOC_DCSN_MTD
					 ,:V_YD_TO_LOC_GUIDE
					 ,:V_YD_WRK_PLAN_TCAR
					 ,:V_YD_CAR_USE_GP
					 ,:V_TRN_EQP_CD
					 ,:V_CAR_NO
					 ,:V_CARD_NO
					 ,:V_YD_WRK_PLAN_CRN
					)
				 */
				int iRtnVal = commDao.insert(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.insSlabYdWrkBook2", logId, methodNm, "작업예약(TB_YD_WRKBOOK) 등록");
				if(iRtnVal < 1) {
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG"  , "작업예약(TB_YD_WRKBOOK) 등록중 예외 발생");
					return jrRtn;
				}
				//작업예약재료 등록
				if("|".equals(stlNos) || "".equals(stlNos)) {
					/*
					 * --설비인출요구 작업예약재료 등록 
						MERGE INTO TB_YD_WRKBOOKMTL WM USING (
						SELECT :V_YD_WBOOK_ID   AS YD_WBOOK_ID --야드작업예약ID
						      ,SL.STL_NO                       --재료번호
						      ,:V_MODIFIER      AS MODIFIER    --수정자
						      ,SYSDATE          AS MOD_DDTT    --수정일시
						      ,'N'              AS DEL_YN      --삭제유무
						      ,SL.YD_STK_COL_GP                --야드적치열구분
						      ,SL.YD_STK_BED_NO                --야드적치Bed번호
						      ,SL.YD_STK_LYR_NO                --야드적치단번호
						      ,ROW_NUMBER() OVER (ORDER BY SL.YD_STK_LYR_NO DESC) AS YD_UP_COLL_SEQ --야드권상모음순서
						  FROM TB_YD_STKLYR SL
						 WHERE SL.YD_STK_COL_GP = :V_YD_STK_COL_GP
						   AND SL.YD_STK_BED_NO = :V_YD_STK_BED_NO
						   AND SL.STL_NO IS NOT NULL
						) DD ON (WM.YD_WBOOK_ID = DD.YD_WBOOK_ID AND WM.STL_NO = DD.STL_NO)
						WHEN NOT MATCHED THEN
						INSERT (WM.YD_WBOOK_ID  , WM.STL_NO       , WM.REGISTER      , WM.REG_DDTT     ,
						        WM.MODIFIER     , WM.MOD_DDTT     , WM.DEL_YN        , WM.YD_STK_COL_GP,
						        WM.YD_STK_BED_NO, WM.YD_STK_LYR_NO, WM.YD_UP_COLL_SEQ)
						VALUES (DD.YD_WBOOK_ID  , DD.STL_NO       , DD.MODIFIER      , DD.MOD_DDTT     ,
						        DD.MODIFIER     , DD.MOD_DDTT     , DD.DEL_YN        , DD.YD_STK_COL_GP,
						        DD.YD_STK_BED_NO, DD.YD_STK_LYR_NO, DD.YD_UP_COLL_SEQ)

					 */
					
					commDao.insert(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.insYDYDJ411WbMtl", logId, methodNm, "작업예약재료(TB_YD_WRKBOOKMTL) 등록");
				} else {
					//선택한 슬라브가 존재하는 경우
					jrParam.setField("STL_NOS"       , stlNos             ); //야드작업예약재료
					
					/*
					 * --설비인출요구 작업예약재료 등록2
						MERGE INTO TB_YD_WRKBOOKMTL WM USING (
						SELECT :V_YD_WBOOK_ID   AS YD_WBOOK_ID --야드작업예약ID
						      ,SL.STL_NO                       --재료번호
						      ,:V_MODIFIER      AS MODIFIER    --수정자
						      ,SYSDATE          AS MOD_DDTT    --수정일시
						      ,'N'              AS DEL_YN      --삭제유무
						      ,SL.YD_STK_COL_GP                --야드적치열구분
						      ,SL.YD_STK_BED_NO                --야드적치Bed번호
						      ,SL.YD_STK_LYR_NO                --야드적치단번호
						      ,ROW_NUMBER() OVER (ORDER BY SL.YD_STK_LYR_NO DESC) AS YD_UP_COLL_SEQ --야드권상모음순서
						  FROM TB_YD_STKLYR SL
						 WHERE SL.YD_STK_COL_GP = :V_YD_STK_COL_GP
						   AND SL.YD_STK_BED_NO = :V_YD_STK_BED_NO
						   AND SL.STL_NO IN (
						            SELECT SUBSTR(IN_STR, INSTR (IN_STR, '|', 1, LEVEL) + 1,INSTR (IN_STR, '|', 1, LEVEL + 1) - INSTR (IN_STR, '|', 1, LEVEL) - 1) 
						            FROM ( 
						                SELECT :V_STL_NOS AS IN_STR FROM DUAL
						            ) 
						            CONNECT BY LEVEL <= LENGTH(IN_STR) - LENGTH (REPLACE (IN_STR, '|')) - 1 
						        )
						) DD ON (WM.YD_WBOOK_ID = DD.YD_WBOOK_ID AND WM.STL_NO = DD.STL_NO)
						WHEN NOT MATCHED THEN
						INSERT (WM.YD_WBOOK_ID  , WM.STL_NO       , WM.REGISTER      , WM.REG_DDTT     ,
						        WM.MODIFIER     , WM.MOD_DDTT     , WM.DEL_YN        , WM.YD_STK_COL_GP,
						        WM.YD_STK_BED_NO, WM.YD_STK_LYR_NO, WM.YD_UP_COLL_SEQ)
						VALUES (DD.YD_WBOOK_ID  , DD.STL_NO       , DD.MODIFIER      , DD.MOD_DDTT     ,
						        DD.MODIFIER     , DD.MOD_DDTT     , DD.DEL_YN        , DD.YD_STK_COL_GP,
						        DD.YD_STK_BED_NO, DD.YD_STK_LYR_NO, DD.YD_UP_COLL_SEQ)

					 */
					commDao.insert(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.insYDYDJ411WbMtl2", logId, methodNm, "작업예약재료(TB_YD_WRKBOOKMTL) 등록");
				}
	
				/**********************************************************
				* 2.2 크레인스케줄(YDYDJ400 >> YDYDJ401 로 내부전문 변경) 전문 조회
				**********************************************************/
				JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, sModifier);
				
				jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
				jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
				jrYdMsg.setField("YD_SCH_ST_GP" , ydSchStGp); //야드스케쥴기동구분
				jrYdMsg.setField("YD_SCH_REQ_GP", "L"      ); //야드스케쥴요청구분(인출)
				JDTORecord jrGetCrn = slabComm.getCrnSchMsg(jrYdMsg);
				 rtnCd	 = slabUtils.nvl(jrGetCrn.getFieldString("RTN_CD"), "0");
				 rtnMsg	 = slabUtils.nvl(jrGetCrn.getFieldString("RTN_MSG"), "");
				 if (!"1".equals(rtnCd)) {
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", rtnMsg);	
						return jrRtn;
					}
				jrRtn = slabUtils.addSndData(jrGetCrn);
			}
			
			slabUtils.printLog(logId, methodNm, "S-");

			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "설비인출요구.");	
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 설비보급요구(YDYDJ420 <- YDYDJ231, YDYDJ232, YDYDJ233, YDYDJ237, YDYDJ241, YDYDJ242, YDYDJ243, YDYDJ245)
	 *      YDYDJ420 >> YDYDJ421 변경으로 메소드 명 변경
	 *      rcvYDYDJ420 >> rcvYDYDJ421
	 *      염용선 2020-09-04
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYDYDJ421(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "설비보급요구[PSlabYdSchSeEJB.rcvYDYDJ421] < " + rcvMsg.getResultMsg();
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
			String sModifier  = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"   )); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			JDTORecord jrRtn  = slabUtils.getParam(logId, methodNm, sModifier);
			
			
			String ydWrkPlanCrn = slabUtils.trim(rcvMsg.getFieldString("YD_WRK_PLAN_CRN")); //작업크레인

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() != 6) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "설비ID(YD_EQP_ID) 이상 : [" + ydEqpId + "]");	
				return jrRtn;
			} else if ("".equals(ydStkBedNo)) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "적치Bed번호(YD_STK_BED_NO) 없음");	
				return jrRtn;
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
			
			//내부처리를 위한 Message 생성 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, sModifier);
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);
			jrParam.setField("YD_STK_COL_GP"  , ydEqpId     ); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO"  , ydStkBedNo  ); //야드적치Bed번호
			jrParam.setField("YD_SCH_ST_GP"   , ydSchStGp   ); //야드스케쥴기동구분
			jrParam.setField("YD_TO_LOC_GUIDE", ydToLocGuide); //야드To위치Guide

			/**********************************************************
			* 2. 기준정보 Check
			**********************************************************/
			/*
			 * --설비보급요구 기준정보 조회
				SELECT SB.YD_STK_BED_USG_GP  --야드적치Bed용도구분(B)
				      ,SB.YD_STK_BED_LYR_MAX --야드적치Bed단Max
				      ,SB.YD_STK_BED_WT_MAX  --야드적치Bed중량Max
				      ,SB.YD_STK_BED_H_MAX   --야드적치Bed높이Max
				      ,BR.YD_AIM_RT_GP       --야드목표행선구분
				      ,BR.YD_SCH_CD          --야드스케쥴코드
				      ,BR.YD_EQP_NAME        --야드설비명
				      ,NVL((SELECT ITEM1
				              FROM TB_YD_RULE
				             WHERE REPR_CD_GP = SB.YD_STK_COL_GP
				               AND CD_GP = '*'
				               AND ITEM  = '3'),'N') AS CI_AUTO_GP_YN  --보급요구대상재자동편성여부
				      ,(SELECT COUNT(*)
				          FROM TB_YD_STKLYR
				         WHERE YD_STK_COL_GP = SB.YD_STK_COL_GP
				           AND YD_STK_BED_NO = SB.YD_STK_BED_NO
				           AND STL_NO IS NOT NULL) AS STK_MTL_SH --적치재료매수
				      ,NVL(SB.YD_COIL_OUTDIA_GRP_GP,'N') AS YD_COIL_OUTDIA_GRP_GP --픽업크레인 작업유무
				  FROM TB_YD_STKBED SB
				      ,VW_YD_YDB033 BR       --Slab보급설비기준
				 WHERE SB.YD_STK_COL_GP = BR.YD_STK_COL_GP
				   AND SB.YD_STK_COL_GP = :V_YD_STK_COL_GP
				   AND SB.YD_STK_BED_NO = :V_YD_STK_BED_NO 

			 */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdL2RcvSeEJB.getYDYDJ421Rule", logId, methodNm, "기준정보 조회");
	    	
			if (jsChk != null && jsChk.size() > 0) {
				JDTORecord jrChk = jsChk.getRecord(0);

				String ydStkBedUsgGp = slabUtils.trim(jrChk.getFieldString("YD_STK_BED_USG_GP")); //야드적치Bed용도구분
				String ydCoilOutdiaGrpGp = slabUtils.trim(jrChk.getFieldString("YD_COIL_OUTDIA_GRP_GP")); //픽업크레인작업유무

				if (!"B".equals(ydStkBedUsgGp)) {
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "보급Bed[" + ydToLocGuide + "] 용도구분이 불출구[B:" + ydStkBedUsgGp + "]가 아닙니다.");	
					return jrRtn;
				}
				
				if("Y".equals(ydCoilOutdiaGrpGp)) {
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "보급Bed[" + ydToLocGuide + "] 픽업 크레인이 현재 작업중입니다.");	
					return jrRtn;
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
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "적치Bed[" + ydToLocGuide + "] 보급설비기준정보가 없습니다.");	
				return jrRtn;
			}
			
			/**********************************************************
			* 3. 스케쥴코드 조회 및 Check
			**********************************************************/
			//스케줄금지여부 등 Check
			jrParam.setField("YD_SCH_CD"     , ydSchCd); //야드스케쥴코드
			jrParam.setField("YD_SCH_WHIO_GP", "U"    ); //야드스케쥴입출고구분(출고)

			JDTORecord jrSchCd = slabComm.getSchCd(jrParam);
			String rtnCd	 = slabUtils.nvl(jrSchCd.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrSchCd.getFieldString("RTN_MSG"), "");
			if (!"1".equals(rtnCd)) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", rtnMsg);	
				return jrRtn;
			}
			ydSchCd    = slabUtils.trim(jrSchCd.getFieldString("YD_SCH_CD"   )); //야드스케쥴코드
			ydSchPrior = slabUtils.trim(jrSchCd.getFieldString("YD_SCH_PRIOR")); //야드스케쥴우선순위
			crnEqpId   = slabUtils.trim(jrSchCd.getFieldString("YD_EQP_ID"   )); //야드설비ID(작업크레인)

			jrParam.setField("YD_SCH_CD"   , ydSchCd   ); //야드스케쥴코드
			jrParam.setField("YD_SCH_PRIOR", ydSchPrior); //야드스케쥴우선순위
			jrParam.setField("YD_EQP_ID"   , crnEqpId  ); //야드설비ID(작업크레인)
			
			/**********************************************************
			* 4. C열연 장입보급(혹은 항만 스카핑보급)Bed가 공Bed 이면 크레인스케줄 존재여부 Check
			*  - 선보급요구로 생성된 크레인스케줄 : 스케쥴코드가 같고 권하지시위치가 없음
			*  - 크레인스케줄 권하위치 변경, 적치단 권하대기 등록
			*  - 작업지시 재전송
			**********************************************************/
			
			/**********************************************************
			* 5. 같은 스케쥴코드의 작업예약 존재여부 Check
			*  - 크레인스케줄 미편성 작업예약이 존재하면 최우선 작업예약을 선택
			*  - 선보급요구가 'Y'이면 선보급요구 실행
			**********************************************************/
			/*
			 * 
			SELECT WB.YD_WBOOK_ID
			      ,WB.YD_TO_LOC_GUIDE
			  FROM TB_YD_WRKBOOK WB
			 WHERE WB.YD_SCH_CD LIKE SUBSTR(:V_YD_SCH_CD,1,7)||'%'
			   AND WB.DEL_YN    = 'N'
			   AND WB.YD_WBOOK_ID NOT IN
			       (SELECT CS.YD_WBOOK_ID
			          FROM TB_YD_CRNSCH CS
			         WHERE CS.YD_SCH_CD = WB.YD_SCH_CD
			           AND CS.DEL_YN    = 'N')
			 ORDER BY WB.YD_WBOOK_ID

			 */
			jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ421WrkBook", logId, methodNm, "작업예약 조회");
	    	
			if (jsChk != null && jsChk.size() > 0) {
				String ydWbookId  = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_WBOOK_ID"    ));	//야드작업예약ID
				String toLocGuide = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_TO_LOC_GUIDE"));	//야드To위치Guide(우선 작업예약)

				if (!ydToLocGuide.equals(toLocGuide)) {
					//작업예약 To위치Guide를 보급요구 To위치Guide로 변경
					jrParam.setField("YD_WBOOK_ID"    , ydWbookId   );	//야드스케쥴코드
					jrParam.setField("YD_TO_LOC_GUIDE", ydToLocGuide);	//야드To위치Guide

					/*
					 * 설비보급요구 작업예약 To위치 수정
						UPDATE TB_YD_WRKBOOK
						   SET MODIFIER        = :V_MODIFIER
						      ,MOD_DDTT        = SYSDATE
						      ,YD_TO_LOC_GUIDE = :V_YD_TO_LOC_GUIDE
						 WHERE YD_WBOOK_ID     = :V_YD_WBOOK_ID
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYDYDJ421WbToLoc", logId, methodNm, "작업예약(TB_YD_WRKBOOK) To위치 Guide 수정");
					
					slabUtils.printLog(logId, "스케쥴코드[" + ydSchCd + "]로 기 등록된 작업예약ID[" + ydWbookId + "]의 To위치를 [" + toLocGuide + " → " + ydToLocGuide + "]로 변경한 후 크레인스케줄 호출", "SL");
				} else {
					slabUtils.printLog(logId, "스케쥴코드[" + ydSchCd + "]로 기 등록된 작업예약ID[" + ydWbookId + "]로 크레인스케줄 호출", "SL");
				}

				//크레인스케줄(YDYDJ401) 전문 조회
				jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId);	//야드작업예약ID
				jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  );	//야드스케쥴코드
				jrYdMsg.setField("YD_EQP_ID"    , crnEqpId );	//야드설비ID(크레인)
				jrYdMsg.setField("YD_SCH_ST_GP" , ydSchStGp);	//야드스케쥴기동구분
				jrYdMsg.setField("YD_SCH_REQ_GP", "U"      );	//야드스케쥴요청구분(보급)
				
				JDTORecord jrGetCrn = slabComm.getCrnSchMsg(jrYdMsg);
				 rtnCd	 = slabUtils.nvl(jrGetCrn.getFieldString("RTN_CD"), "0");
				 rtnMsg	 = slabUtils.nvl(jrGetCrn.getFieldString("RTN_MSG"), "");
				 if (!"1".equals(rtnCd)) {
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", rtnMsg);	
						return jrRtn;
					}
				jrRtn = slabUtils.addSndData(jrRtn,jrGetCrn);
				
				slabUtils.printLog(logId, methodNm, "S-");
				jrRtn.setField("RTN_CD"	, "1");
				jrRtn.setField("RTN_MSG", "");	
				return jrRtn;
			}

			
			/**********************************************************
			* 6.5  후판장입대상재중 선택된 재료정보가 있을 경우에 체크
			**********************************************************/
			if(!"".equals(prStrList)){
				
				JDTORecordSet jsWbMtl 	= JDTORecordFactory.getInstance().createRecordSet("");
    			JDTORecord recInTemp	= null;
				
    			String[] prSList = prStrList.split("-");
    			String[] prLList = prLocList.split("-");
    			
    			for(int inx = 0; inx < prSList.length; inx++){
    				
    				if(!"".equals(prSList[inx])){
    					recInTemp	= slabUtils.getParam(logId, methodNm, sModifier);
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
					jrParam.setField("WB_MTL_SH"      , String.valueOf(jsWbMtl.size())); //작업예약재료매수
					jrParam.setField("YD_WRK_PLAN_CRN", ydWrkPlanCrn);			 //작업크레인
					
					jrRtn = this.insWrkBook(jrParam, jsWbMtl, "");
					 rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
					 rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
					 if (!"1".equals(rtnCd)) {
							jrRtn.setField("RTN_CD"	, "0");
							jrRtn.setField("RTN_MSG", rtnMsg);	
							return jrRtn;
						}
					 
					slabUtils.printLog(logId, ydEqpName + " 작업예약 등록 : 선택된 후판장입대상재 등록", "SL");
					slabUtils.printLog(logId, methodNm, "S-");
					
					jrRtn.setField("RTN_CD"	, "1");
					jrRtn.setField("RTN_MSG", ydEqpName + " 작업예약 등록 : 선택된 후판장입대상재 등록");	
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
				
					//1,2후판 가열로 보급 대상재 조회
					//jsWbMtl = schDao.getYDYDJ420("PR", jrParam);
					/*
					 * --설비보급요구 후판가열로 조회 
						SELECT WM.*
						      ,ROWNUM AS YD_UP_COLL_SEQ
						  FROM (SELECT MO.STL_NO
						              ,SL.YD_STK_COL_GP
						              ,SL.YD_STK_BED_NO
						              ,SL.YD_STK_LYR_NO
						              ,ST.REAL_MEASURE_SLAB_T   AS YD_MTL_T
						              ,ST.REAL_MEASURE_SLAB_W   AS YD_MTL_W
						              ,ST.REAL_MEASURE_SLAB_LEN AS YD_MTL_L
						              ,ST.CAL_SLAB_WT           AS YD_MTL_WT
						              ,CS.YD_CRN_SCH_ID --크레인스케줄ID(장입준비작업)
						              ,CS.YD_WBOOK_ID
						              ,CS.YD_SCH_CD
						              ,CS.YD_EQP_ID
						              ,CS.YD_WRK_PROG_STAT
						              ,NVL((SELECT TRANS_ORD_SEQNO FROM TB_YD_STOCK ST WHERE ST.STL_NO = MO.STL_NO),'99') AS SEQNO1
						   
						          FROM TB_CT_J_MILLWOIDX MI
						              ,TB_CT_N_PLMPLWO   MO
						              ,VW_YD_SLABCOMM    ST
						              ,TB_YD_STKLYR      SL
						              ,(SELECT WM.STL_NO
						                      ,CS.YD_CRN_SCH_ID
						                      ,CS.YD_WBOOK_ID
						                      ,CS.YD_SCH_CD
						                      ,CS.YD_EQP_ID
						                      ,CS.YD_WRK_PROG_STAT
						                  FROM TB_YD_CRNSCH     CS
						                      ,TB_YD_WRKBOOKMTL WM
						                 WHERE WM.YD_WBOOK_ID = CS.YD_WBOOK_ID
						                   AND WM.DEL_YN = 'N'
						                   AND CS.YD_CRN_SCH_ID =
						                       (SELECT MAX(YD_CRN_SCH_ID) AS YD_CRN_SCH_ID
						                          FROM TB_YD_CRNSCH
						                         WHERE YD_EQP_ID = :V_YD_EQP_ID
						                           AND YD_SCH_CD = 'DAYD99MR'
						                           AND DEL_YN    = 'N')) CS
						         WHERE (MI.PTOP_PLNT_GP, MI.CT_RCV_SEQ) =
						               (SELECT -- + INDEX_DESC(MX OK_CT_J_MILLWOIDX)
						                       MX.PTOP_PLNT_GP, MX.CT_RCV_SEQ
						                  FROM USRCTA.TB_CT_J_MILLWOIDX MX
						                 WHERE MX.PTOP_PLNT_GP = 'P'||SUBSTR(:V_YD_SCH_CD,2,1)
						                   AND ROWNUM = 1)
						           AND MO.PTOP_PLNT_GP = MI.PTOP_PLNT_GP
						           AND MO.REFUR_CHG_PLN_SERNO BETWEEN MI.CHG_WO_FR_PNT AND MI.CHG_WO_TO_PNT
						           AND MO.CT_MILL_SPEC_WRK_STAT_GP >= '3' --스케줄확정
						           AND MO.STL_NO = ST.SLAB_NO
						           AND MO.STL_NO = SL.STL_NO
						           AND MO.STL_NO = CS.STL_NO(+)
						           AND SL.YD_STK_COL_GP LIKE SUBSTR(:V_YD_SCH_CD,1,2)||'0%' --일반Bed
						           AND SL.YD_STK_LYR_MTL_STAT IN ('C','U')
						           AND NOT EXISTS (SELECT WM.STL_NO
						                             FROM TB_YD_WRKBOOKMTL WM
						                            WHERE WM.STL_NO = MO.STL_NO
						                              AND WM.DEL_YN = 'N')
						           AND
						              (  
						                (SUBSTR(YD_STK_COL_GP,0,2) = 'DA' AND SUBSTR(:V_YD_SCH_CD,0,4) = 'DART' AND ST.REAL_MEASURE_SLAB_LEN >  4560)
						                OR
						                (SUBSTR(YD_STK_COL_GP,0,2) = 'DA' AND SUBSTR(:V_YD_SCH_CD,0,4) = 'DAPU' AND ST.REAL_MEASURE_SLAB_LEN <= 4560)
						                 OR  
						                (SUBSTR(YD_STK_COL_GP,0,2) = 'DB')  
						
						              )  
						           
						         ORDER BY MO.YD_CHG_NO, SEQNO1, SL.YD_STK_COL_GP DESC, SL.YD_STK_BED_NO, SL.YD_STK_LYR_NO DESC) WM
						 WHERE ROWNUM < DECODE(SUBSTR(WM.YD_STK_COL_GP,0,2), 'DA', DECODE(SUBSTR(:V_YD_SCH_CD,0,4),'DART', 2, 4), 'DB', 7, 4) -- 1후판:3매, 2후판:6매
					 */
					jsWbMtl = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ421PR", logId, methodNm, "후판장입재 조회");
			    	
				
				
				if (jsWbMtl == null || jsWbMtl.size() <= 0) {
					slabUtils.printLog(logId, ydEqpName + " 작업예약 등록 : 자동편성 대상재 없음", "SL");
					slabUtils.printLog(logId, methodNm, "S-");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", ydEqpName + " 작업예약 등록 : 자동편성 대상재 없음");	
					return jrRtn;
				}

				
				slabUtils.printLog(logId, ydEqpName + " 작업예약 등록 : 자동편성 대상재 추출(삭제로직처리후) " + jsWbMtl.size() + "매", "SL");
	
				//보급재료매수 조회 : 크레인스케줄 편성 시 크레인이 한 번에 들어서 Bed에 적치할 수 있도록 작업예약을 편성
				JDTORecord jrSupMtl = slabUtils.getParam(logId, methodNm, sModifier);
				jrSupMtl.setField("SUP_MTL"           , jsWbMtl       ); //보급재료(대상재)
				jrSupMtl.setField("YD_STK_COL_GP"     , ydEqpId       ); //야드적치열구분
				jrSupMtl.setField("YD_EQP_ID"         , crnEqpId      ); //야드설비ID(작업크레인)
				jrSupMtl.setField("YD_STK_BED_LYR_MAX", ydStkBedLyrMax); //야드적치Bed단Max
				jrSupMtl.setField("YD_STK_BED_WT_MAX" , ydStkBedWtMax ); //야드적치Bed단Max
				jrSupMtl.setField("YD_STK_BED_H_MAX"  , ydStkBedHMax  ); //야드적치Bed높이Max
				
				int wbMtlSh = this.getSupMtlSh(jrSupMtl); //작업예약재료매수
				
				if("DBPU05".equals(jrSupMtl.getField("YD_STK_COL_GP"))) {
					
					int totalWeight = 0;
					
					for(int i = 0; i<wbMtlSh; i++) {
						totalWeight += jsWbMtl.getRecord(i).getFieldInt("YD_MTL_WT");
					}
					
					if(totalWeight >= 61000) {
						slabUtils.printLog(logId, ydEqpName + " 작업예약 등록 실패: 중량 초과", "SL");
						slabUtils.printLog(logId, methodNm, "S-");
						//return null;
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", "Carry-in 작업예약 등록 실패: 중량 초과하였습니다.");	
						return jrRtn;
					}
				}
				
				//1후판 장입보급스케줄일 경우 장입준비작업으로 크레인스케줄이 등록된 재료가 있으면
				//기 등록되어 있는 크레인스케줄의 권하지시위치를 변경
				if ("DAPU01UM".equals(ydSchCd) || "DAPU03UM".equals(ydSchCd)) {
					String ydCrnSchId = "";	//크레인스케줄ID
	
					for (int ii = 0; ii < wbMtlSh; ii++) {
						JDTORecord jrRow = jsWbMtl.getRecord(ii);
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
		
							EJBConnector ejbConn = new EJBConnector("default", "PSlabJspSeEJB", this);
							jrRtn = (JDTORecord)ejbConn.trx("updCrnSchDnWoLoc", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
							 rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
							 rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
							if(!"1".equals(rtnCd)){
								jrRtn.setField("RTN_CD"	, "0");
								jrRtn.setField("RTN_MSG", rtnMsg);
								return jrRtn;
							}
							slabUtils.printLog(logId, methodNm, "S-");
							jrRtn.setField("RTN_CD"	, "1");
							jrRtn.setField("RTN_MSG", "");	
							return jrRtn;
						}
					}
				}

				slabUtils.printLog(logId, ydEqpName + " 작업예약 등록 : 크레인사양 Check후 자동편성 대상재 " + wbMtlSh + "매", "SL");

				//작업예약 등록
				jrParam.setField("WB_MTL_SH", String.valueOf(wbMtlSh)); //작업예약재료매수
				jrParam.setField("YD_WRK_PLAN_CRN", ydWrkPlanCrn);			 //작업크레인
				jrRtn = this.insWrkBook(jrParam, jsWbMtl, "");
				 rtnCd	 = slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
				 rtnMsg	 = slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
				 if (!"1".equals(rtnCd)) {
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", rtnMsg);	
						return jrRtn;
					}
			} else {
				slabUtils.printLog(logId, ydEqpName + " 작업예약 등록 : 대상재 자동편성 안함", "SL");
			}
			
			slabUtils.printLog(logId, methodNm, "S-");

			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "");	
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 작업예약등록
	 *      염용선   2020-09-04
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @param JDTORecordSet jsWbMtl
	 *      @param String ydPrepSchId
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord insWrkBook(JDTORecord rcvMsg, JDTORecordSet jsWbMtl, String ydPrepSchId) throws DAOException {
		String methodNm = "작업예약등록[PSlabYdSchSeEJB.작업예약등록] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String ydSchCd      = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"      )); //야드스케쥴코드
			String ydSchPrior   = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_PRIOR"   )); //야드적치Bed번호
			String ydSchStGp    = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_ST_GP"   )); //야드스케쥴기동구분
			String ydToLocGuide = slabUtils.trim(rcvMsg.getFieldString("YD_TO_LOC_GUIDE")); //야드To위치Guide
			String ydEqpId      = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"      )); //야드설비ID(크레인)
			String sModifier     = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"       )); //수정자
			
			JDTORecord jrRtn   = slabUtils.getParam(logId, methodNm, sModifier);
			
			int wbMtlSh         = Integer.parseInt(slabUtils.nvl(rcvMsg.getFieldString("WB_MTL_SH"),"0")); //작업예약재료매수
			String ydWrkPlanCrn = slabUtils.trim(rcvMsg.getFieldString("YD_WRK_PLAN_CRN")); //작업크레인
			
			String ydGp         = ydSchCd.substring(0, 1);	//야드구분
			String ydBayGp      = ydSchCd.substring(1, 2);	//야드동구분

			//내부처리를 위한 Message 생성 - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);
			JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, sModifier);

			//작업예약ID 생성
			String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
					
			
			if ("".equals(ydWbookId)) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "작업예약ID 생성 실패");	
				return jrRtn;
			}

			//작업예약 등록
			jrParam.setField("YD_WBOOK_ID"       , ydWbookId   ); //야드작업예약ID
			jrParam.setField("YD_GP"             , ydGp        ); //야드구분
			jrParam.setField("YD_BAY_GP"         , ydBayGp     ); //야드동구분
			jrParam.setField("YD_SCH_CD"         , ydSchCd     ); //야드스케쥴코드
			jrParam.setField("YD_SCH_PRIOR"      , ydSchPrior  ); //야드스케쥴우선순위
			jrParam.setField("YD_SCH_PROG_STAT"  , "W"         ); //야드스케쥴진행상태(스케줄수행대기)
			jrParam.setField("YD_SCH_ST_GP"      , ydSchStGp   ); //야드스케쥴기동구분
			jrParam.setField("YD_SCH_REQ_GP"     , "U"         ); //야드스케쥴요청구분(보급)
			jrParam.setField("YD_AIM_YD_GP"      , ydGp        ); //야드목표야드구분
			jrParam.setField("YD_AIM_BAY_GP"     , ydBayGp     ); //야드목표동구분
			jrParam.setField("YD_TO_LOC_DCSN_MTD", "F"         ); //야드TO위치결정방법(작업자지정)
			jrParam.setField("YD_TO_LOC_GUIDE"   , ydToLocGuide); //야드To위치Guide
			jrParam.setField("YD_WRK_PLAN_TCAR"  , ""          ); //야드작업계획대차
			jrParam.setField("YD_CAR_USE_GP"     , ""          ); //야드차량사용구분
			jrParam.setField("TRN_EQP_CD"        , ""          ); //운송장비코드
			jrParam.setField("CAR_NO"            , ""          ); //차량번호
			jrParam.setField("CARD_NO"           , ""          ); //카드번호
			jrParam.setField("YD_WRK_PLAN_CRN"   , ydWrkPlanCrn); //야드작업계획크레인
			
			//commDao.insSlabYd("WrkBook", jrParam);
			/*
			 * 작업예약 등록
				INSERT INTO TB_YD_WRKBOOK (
				  YD_WBOOK_ID        --야드작업예약ID
				 ,REGISTER           --등록자
				 ,REG_DDTT           --등록일시
				 ,MODIFIER           --수정자
				 ,MOD_DDTT           --수정일시
				 ,DEL_YN             --삭제유무
				 ,YD_GP              --야드구분
				 ,YD_BAY_GP          --야드동구분
				 ,YD_SCH_CD          --야드스케쥴코드
				 ,YD_SCH_PRIOR       --야드스케쥴우선순위
				 ,YD_SCH_PROG_STAT   --야드스케쥴진행상태
				 ,YD_SCH_ST_GP       --야드스케쥴기동구분
				 ,YD_SCH_REQ_GP      --야드스케쥴요청구분
				 ,YD_AIM_YD_GP       --야드목표야드구분
				 ,YD_AIM_BAY_GP      --야드목표동구분
				 ,YD_TO_LOC_DCSN_MTD --야드To위치결정방법
				 ,YD_TO_LOC_GUIDE    --야드To위치Guide
				 ,YD_WRK_PLAN_TCAR   --야드작업계획대차
				 ,YD_CAR_USE_GP      --야드차량사용구분
				 ,TRN_EQP_CD         --운송장비코드
				 ,CAR_NO             --차량번호
				 ,CARD_NO            --카드번호
				 ,YD_WRK_PLAN_CRN
				) VALUES (
				  :V_YD_WBOOK_ID
				 ,:V_MODIFIER
				 ,SYSDATE
				 ,:V_MODIFIER
				 ,SYSDATE
				 ,'N'
				 ,:V_YD_GP
				 ,:V_YD_BAY_GP
				 ,:V_YD_SCH_CD
				 ,TO_NUMBER(:V_YD_SCH_PRIOR)
				 ,:V_YD_SCH_PROG_STAT
				 ,:V_YD_SCH_ST_GP
				 ,:V_YD_SCH_REQ_GP
				 ,:V_YD_AIM_YD_GP
				 ,:V_YD_AIM_BAY_GP
				 ,:V_YD_TO_LOC_DCSN_MTD
				 ,:V_YD_TO_LOC_GUIDE
				 ,:V_YD_WRK_PLAN_TCAR
				 ,:V_YD_CAR_USE_GP
				 ,:V_TRN_EQP_CD
				 ,:V_CAR_NO
				 ,:V_CARD_NO
				 ,:V_YD_WRK_PLAN_CRN
				)
			 */
			int iRtnVal = commDao.insert(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.insSlabYdWrkBook2", logId, methodNm, "작업예약(TB_YD_WRKBOOK) 등록");
			if(iRtnVal < 1) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG"  , "작업예약(TB_YD_WRKBOOK) 등록중 예외 발생");
				return jrRtn;
			}
			
			//작업예약재료 등록
			if (wbMtlSh < 1) {
				wbMtlSh = jsWbMtl.size(); //작업예약재료매수
			}
			
			
			JDTORecord jrRow = null;
			JDTORecord jrWrkBookMtl = slabUtils.getParam(logId, methodNm, sModifier);
			for (int ii = 0; ii < wbMtlSh; ii++) {
				jrRow = jsWbMtl.getRecord(ii);
				
				
				jrWrkBookMtl.setField("YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID
				jrWrkBookMtl.setField("STL_NO"            , slabUtils.trim(jrRow.getFieldString("STL_NO"       ))); //재료번호
				jrWrkBookMtl.setField("YD_STK_COL_GP"     , slabUtils.trim(jrRow.getFieldString("YD_STK_COL_GP")));	//야드적치열구분
				jrWrkBookMtl.setField("YD_STK_BED_NO"     , slabUtils.trim(jrRow.getFieldString("YD_STK_BED_NO")));	//야드적치Bed번호
				jrWrkBookMtl.setField("YD_STK_LYR_NO"     , slabUtils.trim(jrRow.getFieldString("YD_STK_LYR_NO")));	//야드적치단번호
				jrWrkBookMtl.setField("YD_UP_COLL_SEQ"    , String.valueOf(ii + 1)           ); //야드권상모음순서
				
				/*
				 * 작업예약재료 등록 
					INSERT INTO TB_YD_WRKBOOKMTL (
					  YD_WBOOK_ID    --야드작업예약ID
					 ,STL_NO         --재료번호
					 ,REGISTER       --등록자
					 ,REG_DDTT       --등록일시
					 ,MODIFIER       --수정자
					 ,MOD_DDTT       --수정일시
					 ,DEL_YN         --삭제유무
					 ,YD_STK_COL_GP  --야드적치열구분
					 ,YD_STK_BED_NO  --야드적치Bed번호
					 ,YD_STK_LYR_NO  --야드적치단번호
					 ,YD_UP_COLL_SEQ --야드권상모음순서
					) VALUES (
					  :V_YD_WBOOK_ID
					 ,:V_STL_NO
					 ,:V_MODIFIER
					 ,SYSDATE
					 ,:V_MODIFIER
					 ,SYSDATE
					 ,'N'
					 ,:V_YD_STK_COL_GP
					 ,:V_YD_STK_BED_NO
					 ,:V_YD_STK_LYR_NO
					 ,TO_NUMBER(:V_YD_UP_COLL_SEQ)
					)
				 */
				commDao.insert(jrWrkBookMtl, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.insSlabYdWrkBookMtl", logId, methodNm, "작업예약재료(TB_YD_WRKBOOKMTL) 등록");
				
			}
			
			//commDao.upsBatch("WrkBookMtl", wmParam, logId, methodNm);
			
			//준비스케줄 삭제
			if (!"".equals(ydPrepSchId)) {
				jrParam.setField("YD_PREP_SCH_ID", ydPrepSchId); //야드준비스케쥴ID
				//준비재료 삭제
				//schDao.updYDYDJ420("PM", jrParam);
				/*
				 * --설비보급요구 준비재료 삭제
					UPDATE TB_YD_PREPMTL
					   SET MODIFIER       = :V_MODIFIER
					      ,MOD_DDTT       = SYSDATE
					      ,DEL_YN         = 'Y'
					 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID

				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.updYDYDJ421PM", logId, methodNm, "준비재료(TB_YD_PREPMTL) 삭제");
				
				//준비스케줄 삭제
				//schDao.updYDYDJ420("PP", jrParam);
				/*
				 * --설비보급요구 준비스케줄 삭제
					UPDATE TB_YD_PREPSCH
					   SET MODIFIER       = :V_MODIFIER
					      ,MOD_DDTT       = SYSDATE
					      ,DEL_YN         = 'Y'
					      ,YD_WBOOK_ID    = :V_YD_WBOOK_ID
					 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID

				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.updYDYDJ421PP", logId, methodNm, "준비스케줄(TB_YD_PREPSCH) 삭제");
				
			}
			
			//크레인스케줄(YDYDJ401/YDYDJ400) 전문 조회
			jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
			jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
			jrYdMsg.setField("YD_EQP_ID"    , ydEqpId  ); //야드설비ID(크레인)
			jrYdMsg.setField("YD_SCH_ST_GP" , ydSchStGp); //야드스케쥴기동구분
			jrYdMsg.setField("YD_SCH_REQ_GP", "U"      ); //야드스케쥴요청구분(보급)
			
			JDTORecord jrGetCrn = slabComm.getCrnSchMsg(jrYdMsg);
			
			String rtnCd	 = slabUtils.nvl(jrGetCrn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrGetCrn.getFieldString("RTN_MSG"), "");
			 if (!"1".equals(rtnCd)) {
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", rtnMsg);	
					return jrRtn;
				}
			jrRtn = slabUtils.addSndData(jrGetCrn);
			
			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "");	
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 보급재료매수 조회
	 *      염용선 2020-09-04
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public int getSupMtlSh(JDTORecord jrSupMtl) throws DAOException {
		String methodNm = "보급재료매수조회[PSlabYdSchSeEJB.getSupMtlSh] < " + jrSupMtl.getResultMsg();
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

			jrParam.setField("YD_EQP_ID", ydEqpId); //야드설비ID(작업크레인)

			JDTORecordSet jsCrnSpec = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.getYDYDJ401CrnSpec", logId, methodNm, "크레인사양 조회");
			/*
			 * SELECT CS.YD_WRK_ABLE_SH    --야드작업가능매수			
				      ,CS.YD_WRK_ABLE_WT    --야드작업가능중량
				      ,CS.YD_CRN_TONG_H     --야드크레인집게높이
				      ,CS.YD_CRN_TONG_W_TOL --야드크레인집게폭허용오차
				  FROM TB_YD_CRNSPEC CS
				 WHERE CS.YD_EQP_ID = :V_YD_EQP_ID
				   AND CS.DEL_YN    = 'N'
		    */
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
//	public JDTORecord rcvYDYDJ430(JDTORecord rcvMsg) throws DAOException {
//		String methodNm = "장입준비작업요구[SlabYdSchSeEJB.rcvYDYDJ430] < " + rcvMsg.getResultMsg();
//		String logId = rcvMsg.getResultCode();
//
//		try {
//			slabUtils.printLog(logId, methodNm, "S+");
//
//			//수신 항목 값
//			String msgId     = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			String ydEqpId   = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"   )); //야드설비ID
//			String ydSchStGp = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_ST_GP")); //야드스케쥴기동구분
//			String modifier  = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"  )); //수정자(Backup Only)
//			if ("".equals(modifier)) { modifier = msgId; }
//
//			/**********************************************************
//			* 1. 수신 항목 값 Check
//			**********************************************************/
//			if (ydEqpId.length() < 6) {
//				throw new Exception("설비ID(YD_EQP_ID) 이상 : [" + ydEqpId + "]");
//			}
//
//			/**********************************************************
//			* 2. 작업예약 대상재료 조회
//			**********************************************************/
//			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
//			jrParam.setResultCode(logId);	//Log ID
//			jrParam.setResultMsg(methodNm);	//Log Method Name
//			jrParam.setField("YD_EQP_ID", ydEqpId); //야드설비ID
//
//			//보급 대상재 조회
//			//JDTORecordSet jsWbMtl = schDao.getYDYDJ430("WM", jrParam);
//			/*
//			 * --장입준비작업요구 작업예약재료 조회 
//				SELECT STL_NO
//				      ,YD_STK_COL_GP
//				      ,YD_STK_BED_NO
//				      ,YD_STK_LYR_NO
//				      ,ROWNUM AS YD_UP_COLL_SEQ
//				      ,YD_SCH_CD
//				      ,YD_SCH_PRIOR
//				      ,YD_STK_LOT_CD
//				  FROM (SELECT *
//				          FROM (SELECT SL.STL_NO
//				                      ,SL.YD_STK_COL_GP
//				                      ,SL.YD_STK_BED_NO
//				                      ,SL.YD_STK_LYR_NO
//				                      ,ST.YD_STK_LOT_CD
//				                      ,BR.YD_SCH_CD
//				                      ,BR.YD_SCH_PRIOR
//				                      ,NVL(TO_NUMBER(BR.YD_EQP_WRK_SH),0) AS YD_EQP_WRK_SH
//				                      ,CASE WHEN ST.YD_STK_LOT_TP = BR.YD_STK_LOT_TP
//				                             AND ST.YD_AIM_RT_GP  = BR.YD_AIM_RT_GP  THEN 'N' ELSE 'Y' END AS EXC_STL_YN --제외재료여부
//				                      ,DECODE((SELECT COUNT(*)
//				                                 FROM TB_YD_WRKBOOKMTL WM
//				                                     ,TB_YD_WRKBOOK    WB
//				                                WHERE WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
//				                                  AND WB.YD_SCH_CD LIKE SUBSTR(SL.YD_STK_COL_GP,1,2)||'%'
//				                                  AND WM.STL_NO = SL.STL_NO
//				                                  AND WB.DEL_YN = 'N'
//				                                  AND WM.DEL_YN = 'N'),0,'N','Y') AS WB_STL_YN --작업예약재료여부
//				                  FROM TB_YD_STKLYR SL
//				                      ,TB_YD_STOCK  ST
//				                      ,(SELECT EQ.YD_EQP_ID
//				                              ,SR.YD_SCH_CD
//				                              ,CASE WHEN EQ.YD_EQP_ID = SR.YD_WRK_CRN THEN SR.YD_WRK_CRN_PRIOR
//				                              	    WHEN EQ.YD_EQP_ID = SR.YD_ALT_CRN THEN SR.YD_ALT_CRN_PRIOR
//				                               END AS YD_SCH_PRIOR
//				                              ,BR.YD_STK_COL_GP
//				                              ,BR.YD_STK_LOT_TP
//				                              ,BR.YD_AIM_RT_GP
//				                              ,BR.YD_EQP_WRK_SH --설비작업매수(작업예약매수)
//				                              ,DECODE((SELECT COUNT(*)
//				                                         FROM TB_YD_WRKBOOK WB
//				                                        WHERE WB.YD_SCH_CD = SR.YD_SCH_CD
//				                                          AND WB.DEL_YN    = 'N'),0,'N','Y') AS WB_EXT_YN --작업예약존재여부
//				                          FROM TB_YD_SCHRULE SR
//				                              ,TB_YD_EQP     EQ
//				                              ,VW_YD_YDB035  BR --Slab장입준비작업기준
//				                         WHERE BR.YD_EQP_ID = :V_YD_EQP_ID
//				                           AND BR.YD_SCH_CD = SR.YD_SCH_CD
//				                           AND BR.YD_EQP_ID = EQ.YD_EQP_ID
//				                           AND EQ.YD_EQP_ID IN (SR.YD_WRK_CRN, SR.YD_ALT_CRN)
//				                           AND SR.YD_SCH_PROH_EXN = 'N' --스케쥴기동가능
//				                           AND EQ.YD_EQP_STAT    != 'B' --크레인고장아님
//				                           AND EQ.YD_EQP_WRK_MODE = '1' --크레인On-Line
//				                           AND SR.DEL_YN          = 'N') BR
//				                 WHERE BR.WB_EXT_YN = 'N'
//				                   AND SL.YD_STK_COL_GP LIKE BR.YD_STK_COL_GP||'%'
//				                   AND SL.YD_STK_LYR_MTL_STAT IN ('C','U')
//				                   AND SL.STL_NO        = ST.STL_NO
//				                   AND ST.YD_STK_LOT_TP IN ('SL','SP')
//				                   AND ST.YD_AIM_RT_GP  LIKE 'C%'
//				                   AND ST.DEL_YN        = 'N'
//				                   AND (SL.YD_STK_COL_GP, SL.YD_STK_BED_NO) NOT IN
//				                       (SELECT LB.YD_STK_COL_GP, LB.YD_STK_BED_NO
//				                          FROM TB_YD_LOCSRCHRNG LR
//				                              ,TB_YD_LOCSRCHBED LB
//				                         WHERE LR.YD_LOC_SRCH_RNG_REG_SNO = LB.YD_LOC_SRCH_RNG_REG_SNO
//				                           AND LR.YD_SCH_CD   = BR.YD_SCH_CD
//				                           AND LR.YD_ROUTE_GP = BR.YD_AIM_RT_GP
//				                           AND LR.DEL_YN      = 'N'
//				                           AND LB.DEL_YN      = 'N'))
//				         WHERE EXC_STL_YN = 'N'
//				           AND WB_STL_YN  = 'N'
//				         ORDER BY YD_STK_LOT_CD, YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO DESC)
//				 WHERE ROWNUM <= YD_EQP_WRK_SH
//
//			 */
//			JDTORecordSet jsWbMtl = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabSchSeEJB.getYDYDJ430WM", logId, methodNm, "야드작업예약ID");
//			
//			if (jsWbMtl.size() <= 0) {
//				slabUtils.printLog(logId, "크레인[" + ydEqpId + "] 장입준비 대상재가 없습니다.", "SL");
//				slabUtils.printLog(logId, methodNm, "S-");
//				return null;
//			}
//
//			//작업예약재료매수
//			int wbMtlSh = jsWbMtl.size();
//			String ydGp       = ydEqpId.substring(0, 1);	//야드구분
//			String ydBayGp    = ydEqpId.substring(1, 2);	//야드동구분
//			String ydSchCd    = slabUtils.trim(jsWbMtl.getRecord(0).getFieldString("YD_SCH_CD"   ));	//야드스케쥴코드
//			String ydSchPrior = slabUtils.trim(jsWbMtl.getRecord(0).getFieldString("YD_SCH_PRIOR"));	//야드스케쥴우선순위/
//
//			/**********************************************************
//			* 3. 작업예약 등록
//			**********************************************************/
//			//작업예약ID 생성
//			String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
//			
//			
//			if ("".equals(ydWbookId)) {
//				throw new Exception("작업예약ID 생성 실패");
//			}
//
//			//작업예약 등록
//			jrParam.setField("V_YD_WBOOK_ID"       , ydWbookId   ); //야드작업예약ID
//			jrParam.setField("V_MODIFIER"          , modifier    ); //수정자
//			jrParam.setField("V_YD_GP"             , ydGp        ); //야드구분
//			jrParam.setField("V_YD_BAY_GP"         , ydBayGp     ); //야드동구분
//			jrParam.setField("V_YD_SCH_CD"         , ydSchCd     ); //야드스케쥴코드
//			jrParam.setField("V_YD_SCH_PRIOR"      , ydSchPrior  ); //야드스케쥴우선순위
//			jrParam.setField("V_YD_SCH_PROG_STAT"  , "W"         ); //야드스케쥴진행상태(스케줄수행대기)
//			jrParam.setField("V_YD_SCH_ST_GP"      , ydSchStGp   ); //야드스케쥴기동구분
//			jrParam.setField("V_YD_SCH_REQ_GP"     , "M"         ); //야드스케쥴요청구분(장입준비)
//			jrParam.setField("V_YD_AIM_YD_GP"      , ydGp        ); //야드목표야드구분
//			jrParam.setField("V_YD_AIM_BAY_GP"     , ydBayGp     ); //야드목표동구분
//			jrParam.setField("V_YD_TO_LOC_DCSN_MTD", "S"         ); //야드TO위치결정방법(스케줄지정)
//
//			commDao.insSlabYd("WrkBook", jrParam);
//
//			//작업예약재료 등록
//			String[][] wmParam = new String[wbMtlSh][8];
//			JDTORecord jrRow = null;
//			
//			for (int ii = 0; ii < wbMtlSh; ii++) {
//				jrRow = jsWbMtl.getRecord(ii);
//				
//				wmParam[ii][0] = ydWbookId;													//야드작업예약ID
//				wmParam[ii][1] = slabUtils.trim(jrRow.getFieldString("STL_NO"        ));	//재료번호
//				wmParam[ii][2] = modifier;													//등록자
//				wmParam[ii][3] = modifier;													//수정자
//				wmParam[ii][4] = slabUtils.trim(jrRow.getFieldString("YD_STK_COL_GP" ));	//야드적치열구분
//				wmParam[ii][5] = slabUtils.trim(jrRow.getFieldString("YD_STK_BED_NO" ));	//야드적치Bed번호
//				wmParam[ii][6] = slabUtils.trim(jrRow.getFieldString("YD_STK_LYR_NO" ));	//야드적치단번호
//				wmParam[ii][7] = slabUtils.trim(jrRow.getFieldString("YD_UP_COLL_SEQ"));	//야드권상모음순서
//			}
//			
//			commDao.upsBatch("WrkBookMtl", wmParam, logId, methodNm);
//
//			/**********************************************************
//			* 4. 크레인스케줄(YDYDJ401/YDYDJ400) 전문 조회
//			**********************************************************/
//			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//			jrYdMsg.setResultCode(logId);	//Log ID
//			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
//
//			jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
//			jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
//			jrYdMsg.setField("YD_EQP_ID"    , ydEqpId  ); //야드설비ID
//			jrYdMsg.setField("YD_SCH_ST_GP" , ydSchStGp); //야드스케쥴기동구분
//			jrYdMsg.setField("YD_SCH_REQ_GP", "U"      ); //야드스케쥴요청구분(보급)
//			jrYdMsg.setField("MODIFIER"   , modifier ); //수정자
//
//			JDTORecord jrRtn = slabUtils.addSndData(slabComm.getCrnSchMsg(jrYdMsg));
//			
//			slabUtils.printLog(logId, methodNm, "S-");
//
//			return jrRtn;
//		} catch (DAOException e) {
//			throw e;
//		} catch (Exception e) {
//			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
//		}
//	}

	
	
	
	/**
	 * 오퍼레이션명 : 후판 대차 스케줄   TransEqpSchSeEJB
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public JDTORecord procY3TcarSch(JDTORecord msgRecord)throws DAOException  {
		String methodNm = "후판 대차 스케줄 [SlabYdSchSeEJB.procY3TcarSch] < " + msgRecord.getResultMsg();
		String logId = msgRecord.getResultCode();
		String sModifier  = slabUtils.trim(msgRecord.getFieldString("MODIFIER"        )); //수정자
		
	    int intRtnVal 					= 0 ;
	    
	    String szMsg              		= "";
	    String szOperationName			= "후판대차스케줄";
	    
	    //상하차 구분
	    String szLdUdGp					= "";
	    
        String szRcvTcCode=slabUtils.getTcCode(msgRecord);
        
      
          
	    try{
	    	JDTORecord jrRtn   = slabUtils.getParam(logId, methodNm, sModifier);
	    	
	    	if(szRcvTcCode==null || "".equals(szRcvTcCode)){
	        	szMsg="["+szOperationName+"] TC Code Error ("+szRcvTcCode+")";
	        	slabUtils.printLog(logId, szMsg, "SL:");
	        	jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szMsg);	
				return jrRtn;
	        }
	    	
	    	szMsg="["+szOperationName+"] ------------------- 메소드 시작 -------------------";
	    	slabUtils.printLog(logId, szMsg, "SL:");
            
            szLdUdGp = slabUtils.paraRecChkNull(msgRecord, "YD_LD_UD_GP");
	    	
	    	//------------------------------------------------------------------------------------------
    		//권상 실적 처리중 호출한 경우 - 하차 시
	    	//------------------------------------------------------------------------------------------
    		if( szLdUdGp.equals("U") ) {
    			
    			szMsg="["+szOperationName+"] 권상실적 처리 시 호출 - 대차하차스케줄 모듈 시작";
    			slabUtils.printLog(logId, szMsg, "SL:");
    			
    			//대차 하차스케줄 호출
    			JDTORecord jrY3LdTcarSch  = this.Y3LdTcarSch(msgRecord);
    			String rtnCd	 = slabUtils.nvl(jrY3LdTcarSch.getFieldString("RTN_CD"), "0");
    			String rtnMsg	 = slabUtils.nvl(jrY3LdTcarSch.getFieldString("RTN_MSG"), "");
    			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "SH");
    			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "SH");
    			
    			if(!"1".equals(rtnCd)){
    	        	slabUtils.printLog(logId, szMsg, "SL:");
    	        	jrRtn.setField("RTN_CD"	, "0");
    				jrRtn.setField("RTN_MSG", rtnMsg);	
    				return jrRtn;
    	        }
    			szMsg="["+szOperationName+"] 권상실적 처리 시 호출 - 대차하차스케줄 모듈 완료 - 반환값 : " + intRtnVal;
    			slabUtils.printLog(logId, szMsg, "SL:");
    			
    		//------------------------------------------------------------------------------------------
    		//권하 실적 처리중 호출한 경우 - 상차 시
    		//------------------------------------------------------------------------------------------
    		}else if( szLdUdGp.equals("L") ){
    			
    			szMsg="["+szOperationName+"] 권하실적 처리 시 호출 - 대차상차스케줄 모듈 시작";
    			slabUtils.printLog(logId, szMsg, "SL:");
    			//대차 상차 스케줄 호출
    			//해햐할일   intRtnVal = this.Y3UdTcarSch(msgRecord);
    			
    			szMsg="["+szOperationName+"] 권하실적 처리 시 호출 - 대차상차스케줄 모듈 완료 - 반환값 : " + intRtnVal;
    			slabUtils.printLog(logId, szMsg, "SL:");
    		}else{
    			//------------------------------------------------------------------------------------------
    			//화면에서 송신한 경우 - 공대차출발지시
    			//------------------------------------------------------------------------------------------
    			szMsg="["+szOperationName+"] 화면에서 호출 - 공대차출발스케줄 모듈 시작";
    			slabUtils.printLog(logId, szMsg, "SL:");
    			
    			JDTORecord rtnY3L2TcarSch = this.Y3L2TcarSch(msgRecord);
	    		String rtnCd	 = slabUtils.nvl(rtnY3L2TcarSch.getFieldString("RTN_CD"), "0");
    			String rtnMsg	 = slabUtils.nvl(rtnY3L2TcarSch.getFieldString("RTN_MSG"), "");
    			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "SH");
    			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "SH");
    			
    			if(!"1".equals(rtnCd)){
    	        	slabUtils.printLog(logId, szMsg, "SL:");
    	        	jrRtn.setField("RTN_CD"	, "0");
    				jrRtn.setField("RTN_MSG", rtnMsg);	
    				return jrRtn;
    	        }
    			jrRtn = slabUtils.addSndData(jrRtn, rtnY3L2TcarSch);
    			
	    		szMsg="["+szOperationName+"] 화면에서 호출 - 공대차출발스케줄 모듈 완료 - 반환값 : " + intRtnVal;
	    		slabUtils.printLog(logId, szMsg, "SL:");
			}
    		
    		jrRtn.setField("RTN_CD"	, "1");
    		jrRtn.setField("RTN_MSG", "후판 대차 스케줄 정상 처리.");	
    		return jrRtn;
    		
		}catch(DAOException e) {
			throw e;
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
	} 
	
	/**
	 * 오퍼레이션명 : 대차 하차 스케줄(공대차 출발 지시)  TransEqpSchSeEJB
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return JDTORecord
	 * @throws JDTOException
	 */
	public JDTORecord Y3LdTcarSch(JDTORecord msgRecord)throws DAOException  {
		String methodNm = "대차 하차 스케줄(공대차 출발 지시)[SlabYdSchSeEJB.Y3LdTcarSch] < " + msgRecord.getResultMsg();
		String logId = msgRecord.getResultCode();
		String sModifier  = slabUtils.trim(msgRecord.getFieldString("MODIFIER"        )); //수정자
		
	    String szMsg              		= "";
	    String szMethodName       		= "Y3LdTcarSch";
	    
	    try{
	    	JDTORecord jrRtn   = slabUtils.getParam(logId, methodNm, sModifier);
	    	
			//하차 완료 Check를 한다.
	    	//작업예약ID로 대차하차작업예약ID로 대차스케줄을 조회해서 대차스케줄ID를 조회한다.
	    	String szWbookId = slabUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	
	    	//대차스케줄 조회용
	    	JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
	    	jrTemp.setField("YD_CARUD_WRK_BOOK_ID", szWbookId);
	    	jrTemp.setField("YD_CARLD_WRK_BOOK_ID", "");
	    	
	    	//intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsTcarSch, 1);
	    	/*
	    	 * SELECT YD_TCAR_SCH_ID AS YD_TCAR_SCH_ID
				      ,REGISTER AS REGISTER
				      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
				      ,MODIFIER AS MODIFIER
				      ,TO_CHAR(MOD_DDTT , 'YYYYMMDDHH24MISS') AS MOD_DDTT
				      ,DEL_YN AS DEL_YN
				      ,YD_EQP_ID AS YD_EQP_ID
				      ,YD_EQP_WRK_STAT AS YD_EQP_WRK_STAT
				      ,YD_WRK_PROG_STAT AS YD_WRK_PROG_STAT
				      ,YD_EQP_WRK_SH AS YD_EQP_WRK_SH
				      ,YD_EQP_WRK_WT AS YD_EQP_WRK_WT
				      ,YD_STK_BED_TP AS YD_STK_BED_TP
				      ,YD_CARLD_LEV_LOC AS YD_CARLD_LEV_LOC
				      ,TO_CHAR(YD_CARLD_LEV_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_LEV_DT
				      ,TO_CHAR(YD_CARLD_ARR_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_ARR_DT
				      ,YD_CARLD_WRK_BOOK_ID AS YD_CARLD_WRK_BOOK_ID
				      ,YD_CARLD_SCH_REQ_GP AS YD_CARLD_SCH_REQ_GP
				      ,YD_CARLD_STOP_LOC AS YD_CARLD_STOP_LOC
				      ,TO_CHAR(YD_CARLD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT
				      ,TO_CHAR(YD_CARLD_CMPL_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_CMPL_DT
				      ,YD_CARLD_WRK_ACT_GP AS YD_CARLD_WRK_ACT_GP
				      ,YD_CARLD_WRK_CRN AS YD_CARLD_WRK_CRN
				      ,YD_CARUD_WRK_ACT_GP AS YD_CARUD_WRK_ACT_GP
				      ,TO_CHAR(YD_CARUD_LEV_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_LEV_DT
				      ,TO_CHAR(YD_CARUD_ARR_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_ARR_DT
				      ,YD_CARUD_WRK_BOOK_ID AS YD_CARUD_WRK_BOOK_ID
				      ,YD_CARUD_SCH_REQ_GP AS YD_CARUD_SCH_REQ_GP
				      ,YD_CARUD_STOP_LOC AS YD_CARUD_STOP_LOC
				      ,TO_CHAR(YD_CARUD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_ST_DT
				      ,TO_CHAR(YD_CARUD_CMPL_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_CMPL_DT
				      ,YD_CARUD_WRK_CRN AS YD_CARUD_WRK_CRN
				      ,YD_CAR_PROG_STAT AS YD_CAR_PROG_STAT
				  FROM TB_YD_TCARSCH
				 WHERE (YD_CARUD_WRK_BOOK_ID = :V_YD_CARUD_WRK_BOOK_ID       
				         OR YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID)
				  AND DEL_YN='N'
	    	 */
	    	 JDTORecordSet rsTcarSch = commDao.select(jrTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabSchSeEJB.getYdTcarschUDLDWRKBOOKID", logId, methodNm, "대차스케줄 조회용");

	    	 if(rsTcarSch.size() != 1){
	    		 jrRtn.setField("RTN_CD"	, "0");
	     		 jrRtn.setField("RTN_MSG", "대차스케줄 조회 이상.");	
	     		 return jrRtn;
	    	 }
	    	//대차이송재료조회
	    	rsTcarSch.absolute(1);
	    	JDTORecord recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsTcarSch.getRecord());
	    	
	    	String ydTcarSchId  = slabUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
	    	String ydEqpId  	= slabUtils.paraRecChkNull(recOutTemp, "YD_EQP_ID");
	    	
	    	//대차이송재료가 전부 삭제상태인지 확인한다.
	    	//intRtnVal = ydTcarFtmvMtlDao.getYdTcarftmvmtl(recOutTemp, rsTcarSch, 1);
	    	/*
	    	 *SELECT YD_TCAR_SCH_ID  AS YD_TCAR_SCH_ID
				      ,STL_NO  AS STL_NO
				      ,REGISTER  AS REGISTER
				      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
				      ,MODIFIER  AS MODIFIER
				      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
				      ,DEL_YN  AS DEL_YN
				      ,YD_STK_BED_NO  AS YD_STK_BED_NO
				      ,YD_STK_LYR_NO  AS YD_STK_LYR_NO
				      ,HCR_GP  AS HCR_GP
				      ,STL_PROG_CD  AS STL_PROG_CD
				      ,YD_MTL_ITEM  AS YD_MTL_ITEM
				      ,YD_ROUTE_GP  AS YD_ROUTE_GP
				  FROM TB_YD_TCARFTMVMTL
				 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
				   AND DEL_YN='N'
	    	 */
	    	JDTORecordSet rsResult = commDao.select(recOutTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabSchSeEJB.getYdTcarftmvmtlId", logId, methodNm, "대차이송재료가 전부 삭제상태인지 확인 조회용");

	    	//하차가 완료된 경우 
	    	if(rsResult.size() == 0) {
	    		
	    		//대차스케줄 삭제처리
	    		jrTemp = slabUtils.getParam(logId, methodNm, sModifier);
	    		jrTemp.setField("YD_EQP_WRK_STAT" , "U");
	    		jrTemp.setField("DEL_YN"          , "Y");
	    		jrTemp.setField("YD_TCAR_SCH_ID"  , ydTcarSchId);
	    		jrTemp.setField("YD_CAR_PROG_STAT", "E");

				//intRtnVal = ydTcarSchDao.updYdTcarsch(jrTemp, 0);
				/*
				 *
				UPDATE TB_YD_TCARSCH
				      SET MODIFIER = :V_MODIFIER
				         ,MOD_DDTT = SYSDATE
				         ,DEL_YN   = :V_DEL_YN
				         ,YD_EQP_WRK_STAT  = :V_YD_EQP_WRK_STAT
				         ,YD_CAR_PROG_STAT = :V_YD_CAR_PROG_STAT
				  WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
				 */
	    		 commDao.update(jrTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabSchSeEJB.updYdTcarsch", logId, methodNm, "대차스케줄 삭제처리");
				
				/*
				 * 대차스케줄생성 메소드 호출 
				 * msgRecord에 값을 비워서 보내도록 변경
				 * 이전작업예약이 남아있어 대차상차작업예약ID에 대차 하차작업예약을 등록함.
				 * 작업예약이 없는 상태에서 다음 작업예약을 찾도록 조회!!
				 */
				msgRecord = slabUtils.getParam(logId, methodNm, sModifier);
				msgRecord.setField("YD_EQP_ID", ydEqpId);

				JDTORecord rtnY3L2TcarSch = this.Y3L2TcarSch(msgRecord);
				String rtnCd	 = slabUtils.nvl(rtnY3L2TcarSch.getFieldString("RTN_CD"), "0");
    			String rtnMsg	 = slabUtils.nvl(rtnY3L2TcarSch.getFieldString("RTN_MSG"), "");
    			slabUtils.printLog(logId, methodNm + " =======rtnCd:"+ rtnCd, "SH");
    			slabUtils.printLog(logId, methodNm + " =======rtnMsg:"+ rtnMsg, "SH");
    			
    			if(!"1".equals(rtnCd)){
    	        	slabUtils.printLog(logId, szMsg, "SL:");
    	        	jrRtn.setField("RTN_CD"	, "0");
    				jrRtn.setField("RTN_MSG", rtnMsg);	
    				return jrRtn;
    	        }
    			jrRtn = slabUtils.addSndData(jrRtn, rtnY3L2TcarSch);
    			
	    	}
	    	szMsg="대차 하차 스케줄("+szMethodName+") 완료";
			slabUtils.printLog(logId, szMsg, "SL:");
			
	    	 jrRtn.setField("RTN_CD"	, "1");
			 jrRtn.setField("RTN_MSG"   , "대차 하차 스케줄 처리 완료.");	
			 return jrRtn;
		}catch(DAOException e) {
			throw e;
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		
		
	} 

	
	
	/**
	 * 오퍼레이션명 : 대차 하차 스케줄(공대차 출발지시) TransEqpSchSeEJB
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public JDTORecord 	Y3L2TcarSch(JDTORecord msgRecord)throws DAOException  {
		String methodNm = "대차 하차 스케줄(공대차 출발지시)[SlabYdSchSeEJB.Y3L2TcarSch] < " + msgRecord.getResultMsg();
		String logId = msgRecord.getResultCode();
		String sModifier  = slabUtils.trim(msgRecord.getFieldString("MODIFIER"        )); //수정자
		
		//동간이적요구시 화면에서  송신하는 경우
		
		
	    
	    try{
	    	
	    	int intRtnVal 					= 0 ;
		    
		    String szMsg              		= "";
		    String szMethodName       		= "Y3L2TcarSch";
		    String szOperationName			= "공대차스케줄";
		    
		    String ydGp					= "";
		    String ydBayGp				= "";
		    String ydAimBayGp				= "";
		    String ydWrkPlanTcar			= "";
		    String ydSchCd                  = "";
		    String ydTcarCurrBayGp          = "";
		    String ydTcarSchId              = "";
		    String ydToBay                  = "";
		    String ydCarldWrkBookId         = "";
		    String ydCarldStopLoc           = "";					//상차정지위치
		    String ydCarudStopLoc    		= "";					//하차정지위치
		    String ydWbookId                = "";
		    String ydHomeBayGp              = "";
		    String sUsageYn  				= "";
		    String sWorkGp   				= "";
		    String ydCarldBayGp         	= "";
		    
		    JDTORecordSet rsResult          = null;
			JDTORecordSet rsResult1         = null;
			JDTORecord    recOutTemp        = slabUtils.getParam(logId, methodNm, sModifier);
			JDTORecord    recTcar           = slabUtils.getParam(logId, methodNm, sModifier);
			JDTORecord    recWbook          = slabUtils.getParam(logId, methodNm, sModifier);	    
	    	JDTORecord jrRtn   = slabUtils.getParam(logId, methodNm, sModifier);
	    	
	    	szMsg="["+szOperationName+"] ------------------- 메소드 시작 -------------------";
            slabUtils.printLog(logId, szMsg, "SL:");
            
            String sTcarEqpId 		= slabUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");	//대차설비ID
	    	ydToBay 		= slabUtils.paraRecChkNull(msgRecord, "YD_TO_BAY");	//공대차출발 시 사용자가 지정한 목표동(값 없을수 있슴)
	    	ydWbookId 		= slabUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");	//작업예약ID(값 없을수 있슴)
	    	
	    	//-------------------------------------------------------------------------------------
	    	//	대차설비ID로 설비테이블을 조회한다.
	    	//-------------------------------------------------------------------------------------
	    	szMsg="["+szOperationName+"] 대차설비ID(" + sTcarEqpId + ")로 설비테이블 조회 시작";
			slabUtils.printLog(logId, szMsg, "SL:");
			
    		rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
    		
    		JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);
    		jrParam.setField("YD_EQP_ID", 			sTcarEqpId);
    		jrParam.setField("YD_TCAR_SCH_ID", 		ydTcarSchId);

    		//intRtnVal = ydEqpDao.getYdEqp(jrParam, rsResult1, 0);
    		/*
			 * 
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
				    ,YD_EQP_WRK_MODE2 AS YD_EQP_WRK_MODE2
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
				    ,YD_CRN_USE_SEQ AS YD_CRN_USE_SEQ
				    ,YD_CRN_CONT_CARASGN_CNT AS YD_CRN_CONT_CARASGN_CNT
				    ,YD_CRN_CONT_CARASGN_WR AS YD_CRN_CONT_CARASGN_WR
				    ,YD_EQP_AUTO_CRN_MODE AS YD_EQP_AUTO_CRN_MODE
				FROM TB_YD_EQP
				WHERE YD_EQP_ID = :V_YD_EQP_ID 
				    AND DEL_YN='N'
			 */
    		rsResult1 = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdEqp", logId, methodNm, "설비 테이블 조회");

    		szMsg="["+szOperationName+"] 대차설비ID(" + sTcarEqpId + ")로 설비테이블 조회 완료 - 반환값 : " + rsResult1.size();
			slabUtils.printLog(logId, szMsg, "SL:");
			
			if(rsResult1.size() <= 0) {
    			szMsg="["+szOperationName+"] 대차설비ID(" + sTcarEqpId + ")로 설비테이블 조회 시 오류발생 - 반환값 : " + rsResult1.size();
    			slabUtils.printLog(logId, szMsg, "SL:");
    			jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szMsg);	
				return jrRtn;
			}
			
			rsResult1.absolute(1);
    		recOutTemp = JDTORecordFactory.getInstance().create();
    		recOutTemp.setRecord(rsResult1.getRecord());
    		
    		ydTcarCurrBayGp 	= slabUtils.paraRecChkNull(recOutTemp, "YD_CURR_BAY_GP");	//현재동
	    	ydHomeBayGp 		= slabUtils.paraRecChkNull(recOutTemp, "YD_HOME_BAY_GP");	//홈동
    		//-------------------------------------------------------------------------------------
	    	
	    	if(!"".equals(ydWbookId)) {
	    		
	    		//-------------------------------------------------------------------------------------
	    		//	파라미터로 전달된 작업예약ID가 존재하면 작업예약을 조회한다.
	    		//-------------------------------------------------------------------------------------
	    		szMsg="["+szOperationName+"] 파라미터로 전달된 작업예약[" + ydWbookId + "]을 조회 시작";
				slabUtils.printLog(logId, szMsg, "SL:");
	    		
				rsResult  			= JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecord recInTemp 			= JDTORecordFactory.getInstance().create();
	    		
	    		recInTemp.setField("YD_WBOOK_ID", 			ydWbookId);
	    		
	    		//intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, rsResult, 0);
	    		/*
	    		 *  SELECT YD_WBOOK_ID  AS YD_WBOOK_ID
					      ,REGISTER  AS REGISTER
					      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
					      ,MODIFIER  AS MODIFIER
					      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
					      ,DEL_YN  AS DEL_YN
					      ,YD_GP  AS YD_GP
					      ,YD_BAY_GP  AS YD_BAY_GP
					      ,YD_SCH_CD  AS YD_SCH_CD
					      ,YD_SCH_PRIOR  AS YD_SCH_PRIOR
					      ,YD_SCH_PROG_STAT  AS YD_SCH_PROG_STAT
					      ,YD_SCH_ST_GP  AS YD_SCH_ST_GP
					      ,YD_SCH_REQ_GP  AS YD_SCH_REQ_GP
					      ,YD_AIM_YD_GP  AS YD_AIM_YD_GP
					      ,YD_AIM_BAY_GP  AS YD_AIM_BAY_GP
					      ,YD_CTS_RELAY_YN  AS YD_CTS_RELAY_YN
					      ,YD_CTS_RELAY_BAY_GP  AS YD_CTS_RELAY_BAY_GP
					      ,YD_TO_LOC_DCSN_MTD AS YD_TO_LOC_DCSN_MTD
					      ,YD_TO_LOC_GUIDE  AS YD_TO_LOC_GUIDE
					      ,YD_WRK_PLAN_TCAR AS YD_WRK_PLAN_TCAR
					      ,(CASE WHEN YD_SCH_CD LIKE 'J_PT0_LM' THEN '' 
					             WHEN YD_SCH_CD LIKE 'J_PT5_LM' THEN '' 
					             WHEN YD_SCH_CD LIKE 'J_TR0_MM' THEN 'G' 
					             WHEN YD_SCH_CD LIKE 'J_TR5_MM' THEN 'G' 
					        ELSE YD_CAR_USE_GP END) AS YD_CAR_USE_GP
					      ,TRN_EQP_CD AS TRN_EQP_CD
					      ,CAR_NO AS CAR_NO
					      ,CARD_NO AS CARD_NO
					   FROM TB_YD_WRKBOOK
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
	    		 */
	    		rsResult = commDao.select(recInTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdWrkbook", logId, methodNm, "설비 테이블 조회");

	    		
	    		if(rsResult.size() <= 0 ){
	    			szMsg= "["+szOperationName+"] 파라미터로 전달된 작업예약[" + ydWbookId + "]을 조회 시 오류발생 - 반환값 : " + rsResult.size();
	    			slabUtils.printLog(logId, szMsg, "SL:");
	    			jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", szMsg);	
					return jrRtn;
	    		}
	    		
	    		szMsg="["+szOperationName+"] 파라미터로 전달된 작업예약[" + ydWbookId + "]을 조회 완료 - 반환값 : " + rsResult.size();
				slabUtils.printLog(logId, szMsg, "SL:");
	    		
	    		rsResult.absolute(1);
	    		recWbook = JDTORecordFactory.getInstance().create();
	    		recWbook.setRecord(rsResult.getRecord());
	    		
	    		ydBayGp       = slabUtils.paraRecChkNull(recWbook, "YD_BAY_GP");						//동구분
	    		
	    		ydGp            = slabUtils.paraRecChkNull(recWbook, "YD_GP");							//야드구분
		    	ydAimBayGp    = slabUtils.paraRecChkNull(recWbook, "YD_AIM_BAY_GP");					//목표동구분
		    	ydWrkPlanTcar = slabUtils.paraRecChkNull(recWbook, "YD_WRK_PLAN_TCAR");				//작업계획대차
		    	ydSchCd       = slabUtils.paraRecChkNull(recWbook, "YD_SCH_CD");						//스케줄코드
		    	//-------------------------------------------------------------------------------------
		    	
	    	}else{
	    		
	    		if( !ydToBay.equals("") ) {
	    			
	    			ydBayGp	= ydToBay;
	    			
	    			szMsg="["+szOperationName+"] 해당 대차["+sTcarEqpId+"]를 사용자가 지정한 동["+ydToBay+"]으로 공대차출발 처리";
					slabUtils.printLog(logId, szMsg, "SL:");
	    		}else{
	    	    	szMsg="["+szOperationName+"] 해당 대차["+sTcarEqpId+"]에 대해서 일반적인 규칙인 우선순위가 빠르고, 작업예약순서가 빠른 대차상차작업예약을 조회한다.";
					slabUtils.printLog(logId, szMsg, "SL:");
	    	    	
		    		//-------------------------------------------------------------------------------------
			    	//	파라미터로 전달된 작업예약ID가 존재하지 않으면 
		    		//	작업계획대차로 우선순위가 빠르고, 작업예약순서가 빠른 대차상차작업예약만 조회
		    		//-------------------------------------------------------------------------------------
		    		szMsg="["+szOperationName+"] 해당 대차["+sTcarEqpId+"]로 등록된 대차상차작업예약을 조회 시작";
					slabUtils.printLog(logId, szMsg, "SL:");
		    		
			    	msgRecord.setField("YD_WRK_PLAN_TCAR", 				sTcarEqpId);
			    	
			    	rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
		    		//intRtnVal = ydWrkbookDao.getYdWrkbook(msgRecord, rsResult, 27);
		    		
		    		/*
		    		 * SELECT YD_WBOOK_ID  AS YD_WBOOK_ID
						      ,REGISTER  AS REGISTER
						      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
						      ,MODIFIER  AS MODIFIER
						      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
						      ,DEL_YN  AS DEL_YN
						      ,YD_GP  AS YD_GP
						      ,YD_BAY_GP  AS YD_BAY_GP
						      ,YD_SCH_CD  AS YD_SCH_CD
						      ,YD_SCH_PRIOR  AS YD_SCH_PRIOR
						      ,YD_SCH_PROG_STAT  AS YD_SCH_PROG_STAT
						      ,YD_SCH_ST_GP  AS YD_SCH_ST_GP
						      ,YD_SCH_REQ_GP  AS YD_SCH_REQ_GP
						      ,YD_AIM_YD_GP  AS YD_AIM_YD_GP
						      ,YD_AIM_BAY_GP  AS YD_AIM_BAY_GP
						      ,YD_CTS_RELAY_YN  AS YD_CTS_RELAY_YN
						      ,YD_CTS_RELAY_BAY_GP  AS YD_CTS_RELAY_BAY_GP
						      ,YD_TO_LOC_DCSN_MTD  AS YD_TO_LOC_DCSN_MTD
						      ,YD_TO_LOC_GUIDE  AS YD_TO_LOC_GUIDE
						      ,YD_WRK_PLAN_TCAR AS YD_WRK_PLAN_TCAR
						      ,YD_CAR_USE_GP AS YD_CAR_USE_GP
						      ,TRN_EQP_CD AS TRN_EQP_CD
						      ,CAR_NO AS CAR_NO
						      ,CARD_NO AS CARD_NO
						  FROM TB_YD_WRKBOOK   
						 WHERE YD_WRK_PLAN_TCAR = :V_YD_WRK_PLAN_TCAR
						   AND YD_WBOOK_ID NOT IN (
						                           SELECT 
						                              YD_CARLD_WRK_BOOK_ID AS YD_WBOOK_ID
						                            FROM TB_YD_TCARSCH
						                            WHERE YD_CARLD_WRK_BOOK_ID IS NOT NULL
						                            AND DEL_YN = 'N'
						                            UNION ALL
						                            SELECT 
						                               YD_CARUD_WRK_BOOK_ID AS YD_WBOOK_ID
						                            FROM TB_YD_TCARSCH
						                            WHERE YD_CARUD_WRK_BOOK_ID IS NOT NULL
						                            AND DEL_YN = 'N'
						        )
						   AND YD_SCH_CD LIKE '__TC__U%'
						   AND DEL_YN = 'N'
						 ORDER BY YD_SCH_PRIOR ASC, YD_WBOOK_ID ASC
		    		 */
		    		rsResult = commDao.select(msgRecord, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdWrkbookForTcarLoad", logId, methodNm, "설비 테이블 조회");

		    		szMsg="["+szOperationName+"] 해당 대차["+sTcarEqpId+"]로 등록된 대차상차작업예약을 조회 완료 - 반환값 : " + rsResult.size();
					slabUtils.printLog(logId, szMsg, "SL:");
		    		
		    		if(rsResult.size() > 0) {
				    	rsResult.first();
				    	recOutTemp = JDTORecordFactory.getInstance().create();
				    	recOutTemp.setRecord(rsResult.getRecord());
				    	
				    	ydWbookId   = slabUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID");				//작업예약ID
				    	ydGp            = slabUtils.paraRecChkNull(recOutTemp, "YD_GP");					//야드구분
				    	ydBayGp       = slabUtils.paraRecChkNull(recOutTemp, "YD_BAY_GP");				//동구분
				    	ydAimBayGp    = slabUtils.paraRecChkNull(recOutTemp, "YD_AIM_BAY_GP");			//목표동구분
				    	ydWrkPlanTcar = slabUtils.paraRecChkNull(recOutTemp, "YD_WRK_PLAN_TCAR");		//작업계획대차
				    	ydSchCd       = slabUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD");				//스케줄코드
		    		}else{
		    			
		    			ydBayGp		= ydHomeBayGp;
		    			
		    			szMsg="["+szOperationName+"] 대차와 관련된 작업예약이 없는 경우에는 홈동["+ydHomeBayGp+"]으로 공대차 출발 지시를 처리.";
		    			slabUtils.printLog(logId, szMsg, "SL:");
		    		}
	    		}
	    	}
	    	
	    	//-------------------------------------------------------------------------------------
	    	//	대차설비ID로 대차스케줄을 조회한다.
	    	//-------------------------------------------------------------------------------------
	    	
			szMsg="["+szOperationName+"] 대차설비ID(" + sTcarEqpId + ")로 대차스케줄 조회 시작";
			slabUtils.printLog(logId, szMsg, "SL:");
			
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			JDTORecord recInTemp = slabUtils.getParam(logId, methodNm, sModifier);
	    	recInTemp.setField("YD_EQP_ID", 		sTcarEqpId);
	    	
	    	//intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 4);
	    	/*
	    	 * SELECT YD_TCAR_SCH_ID  AS YD_TCAR_SCH_ID
				      ,REGISTER  AS REGISTER
				      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
				      ,MODIFIER  AS MODIFIER
				      ,TO_CHAR(MOD_DDTT , 'YYYYMMDDHH24MISS') AS MOD_DDTT
				      ,DEL_YN  AS DEL_YN
				      ,YD_EQP_ID  AS YD_EQP_ID
				      ,YD_EQP_WRK_STAT  AS YD_EQP_WRK_STAT
				      ,YD_WRK_PROG_STAT  AS YD_WRK_PROG_STAT
				      ,YD_EQP_WRK_SH  AS YD_EQP_WRK_SH
				      ,YD_EQP_WRK_WT  AS YD_EQP_WRK_WT
				      ,YD_STK_BED_TP  AS YD_STK_BED_TP
				      ,YD_CARLD_LEV_LOC  AS YD_CARLD_LEV_LOC
				      ,TO_CHAR(YD_CARLD_LEV_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_LEV_DT
				      ,TO_CHAR(YD_CARLD_ARR_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_ARR_DT
				      ,YD_CARLD_WRK_BOOK_ID  AS YD_CARLD_WRK_BOOK_ID
				      ,YD_CARLD_SCH_REQ_GP  AS YD_CARLD_SCH_REQ_GP
				      ,YD_CARLD_STOP_LOC  AS YD_CARLD_STOP_LOC
				      ,TO_CHAR(YD_CARLD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT
				      ,TO_CHAR(YD_CARLD_CMPL_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_CMPL_DT
				      ,YD_CARLD_WRK_ACT_GP  AS YD_CARLD_WRK_ACT_GP
				      ,YD_CARLD_WRK_CRN  AS YD_CARLD_WRK_CRN
				      ,YD_CARUD_WRK_ACT_GP  AS YD_CARUD_WRK_ACT_GP
				      ,TO_CHAR(YD_CARUD_LEV_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_LEV_DT
				      ,TO_CHAR(YD_CARUD_ARR_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_ARR_DT
				      ,YD_CARUD_WRK_BOOK_ID  AS YD_CARUD_WRK_BOOK_ID
				      ,YD_CARUD_SCH_REQ_GP  AS YD_CARUD_SCH_REQ_GP
				      ,YD_CARUD_STOP_LOC  AS YD_CARUD_STOP_LOC
				      ,TO_CHAR(YD_CARUD_ST_DT, 'YYYYMMDDHH24MISS')  AS YD_CARUD_ST_DT
				      ,TO_CHAR(YD_CARUD_CMPL_DT, 'YYYYMMDDHH24MISS')  AS YD_CARUD_CMPL_DT
				      ,YD_CARUD_WRK_CRN  AS YD_CARUD_WRK_CRN
				      ,YD_CAR_PROG_STAT AS YD_CAR_PROG_STAT                               
				  FROM TB_YD_TCARSCH                    
				 WHERE YD_EQP_ID = :V_YD_EQP_ID
				   AND DEL_YN='N'
	    	 */
	    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdTcarschByYdEqpId", logId, methodNm, "설비 테이블 조회");

	    	szMsg="["+szOperationName+"] 대차설비ID(" + sTcarEqpId + ")로 대차스케줄 조회 완료 - 반환값 : " + rsResult.size();
			slabUtils.printLog(logId, szMsg, "SL:");
	    	//-------------------------------------------------------------------------------------
	    	
	    	//-------------------------------------------------------------------------------------
	    	//	만약 대차스케줄이 존재하면 대차 스케줄을 기동 하지 않는다.(공대차인경우는 제외)
	    	//-------------------------------------------------------------------------------------
	    	
	    	if(rsResult.size() > 0) {
	    		
	    		//-------------------------------------------------------------------------------------
		    	//	대차스케줄이 존재
	    		//-------------------------------------------------------------------------------------
	    		rsResult.absolute(1);
	    		recTcar = slabUtils.getParam(logId, methodNm, sModifier);
	    		recTcar.setRecord(rsResult.getRecord());
	    		
	    		//상차작업예약 id가 있는지 없는지 Check!!!
	    		ydCarldWrkBookId = slabUtils.paraRecChkNull(recTcar, "YD_CARLD_WRK_BOOK_ID");
	    		ydCarldStopLoc    = slabUtils.paraRecChkNull(recTcar, "YD_CARLD_STOP_LOC");
	    		ydTcarSchId       = slabUtils.paraRecChkNull(recTcar, "YD_TCAR_SCH_ID");
	    		
	    		szMsg="["+szOperationName+"] 대차설비ID(" + sTcarEqpId + ")로 대차스케줄["+ydTcarSchId+"]이 존재하는 경우 - 이미 등록된 상차작업예약["+ydCarldWrkBookId+"], 상차정지위치["+ydCarldStopLoc+"]";
				slabUtils.printLog(logId, szMsg, "SL:");
	    		
	    		if(!"".equals(ydCarldWrkBookId)) {
	    			
	    			//-------------------------------------------------------------------------------------
		    		//	대차스케줄의 상차작업예약이 존재하는 경우
		    		//-------------------------------------------------------------------------------------
					szMsg="["+szOperationName+"] 대차[" + sTcarEqpId + "]스케줄["+ydTcarSchId+"]이 존재하고 이미 등록된 상차작업예약["+ydCarldWrkBookId+"]이 존재하므로 대차스케줄["+ydTcarSchId+"]을 기동할 수 없습니다.";
					slabUtils.printLog(logId, szMsg, "SL:");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", szMsg);	
					return jrRtn;
					//-------------------------------------------------------------------------------------
					
	    		}else if ("".equals(ydCarldWrkBookId) && "".equals(ydToBay)){
	    			
		    		//-------------------------------------------------------------------------------------
		    		//	대차스케줄 업데이트항목
		    		//-------------------------------------------------------------------------------------
		    		ydCarldStopLoc			= "D" + ydBayGp 	+ sTcarEqpId.substring(2,6);
		    		ydCarudStopLoc			= "D" + ydAimBayGp+ sTcarEqpId.substring(2,6);
		    		
		    		recInTemp = slabUtils.getParam(logId, methodNm, sModifier);
		    		recInTemp.setField("YD_TCAR_SCH_ID", 				ydTcarSchId);
			    	recInTemp.setField("YD_CARLD_SCH_REQ_GP", 			"6");
					recInTemp.setField("YD_CARUD_SCH_REQ_GP", 			"3");
					recInTemp.setField("YD_CARLD_WRK_BOOK_ID", 			ydWbookId);				//상차작업예약
					recInTemp.setField("YD_CARLD_STOP_LOC", 			ydCarldStopLoc);		//상차정지위치
					if( !ydAimBayGp.equals("") ) {
						recInTemp.setField("YD_CARUD_STOP_LOC", 		ydCarudStopLoc);		//하차정지위치
					}
					
		    		if(ydBayGp.equals(ydTcarCurrBayGp)) {
		    			
		    			//-------------------------------------------------------------------------------------
						//	상차정지위치와 대차설비의 현재동이 같은경우 상차도착상태로 변경, 크레인스케줄 호출
						//-------------------------------------------------------------------------------------
		    			szMsg="["+szOperationName+"] 상차정지위치동["+ydBayGp+"]와 대차설비의 현재동["+ydTcarCurrBayGp+"]이 같은 경우는 대차스케줄["+ydTcarSchId+"]에 상차작업예약["+ydWbookId+"], 상차도착 상태[2]로 등록 후 크레인스케줄 호출";
		    			slabUtils.printLog(logId, szMsg, "SL:");
		    			
		    			recInTemp.setField("YD_CAR_PROG_STAT", 			"2");	//상차도착
		    			
		    			/*
		    			 *  SELECT YD_TCAR_SCH_ID  AS YD_TCAR_SCH_ID
							      ,REGISTER  AS REGISTER
							      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
							      ,MODIFIER  AS MODIFIER
							      ,TO_CHAR(MOD_DDTT , 'YYYYMMDDHH24MISS') AS MOD_DDTT
							      ,DEL_YN  AS DEL_YN
							      ,YD_EQP_ID  AS YD_EQP_ID
							      ,YD_EQP_WRK_STAT  AS YD_EQP_WRK_STAT
							      ,YD_WRK_PROG_STAT  AS YD_WRK_PROG_STAT
							      ,YD_EQP_WRK_SH  AS YD_EQP_WRK_SH
							      ,YD_EQP_WRK_WT  AS YD_EQP_WRK_WT
							      ,YD_STK_BED_TP  AS YD_STK_BED_TP
							      ,YD_CARLD_LEV_LOC  AS YD_CARLD_LEV_LOC
							      ,TO_CHAR(YD_CARLD_LEV_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_LEV_DT
							      ,TO_CHAR(YD_CARLD_ARR_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_ARR_DT
							      ,YD_CARLD_WRK_BOOK_ID  AS YD_CARLD_WRK_BOOK_ID
							      ,YD_CARLD_SCH_REQ_GP  AS YD_CARLD_SCH_REQ_GP
							      ,YD_CARLD_STOP_LOC  AS YD_CARLD_STOP_LOC
							      ,TO_CHAR(YD_CARLD_ST_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT
							      ,TO_CHAR(YD_CARLD_CMPL_DT, 'YYYYMMDDHH24MISS') AS YD_CARLD_CMPL_DT
							      ,YD_CARLD_WRK_ACT_GP  AS YD_CARLD_WRK_ACT_GP
							      ,YD_CARLD_WRK_CRN  AS YD_CARLD_WRK_CRN
							      ,YD_CARUD_WRK_ACT_GP  AS YD_CARUD_WRK_ACT_GP
							      ,TO_CHAR(YD_CARUD_LEV_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_LEV_DT
							      ,TO_CHAR(YD_CARUD_ARR_DT, 'YYYYMMDDHH24MISS') AS YD_CARUD_ARR_DT
							      ,YD_CARUD_WRK_BOOK_ID  AS YD_CARUD_WRK_BOOK_ID
							      ,YD_CARUD_SCH_REQ_GP  AS YD_CARUD_SCH_REQ_GP
							      ,YD_CARUD_STOP_LOC  AS YD_CARUD_STOP_LOC
							      ,TO_CHAR(YD_CARUD_ST_DT, 'YYYYMMDDHH24MISS')  AS YD_CARUD_ST_DT
							      ,TO_CHAR(YD_CARUD_CMPL_DT, 'YYYYMMDDHH24MISS')  AS YD_CARUD_CMPL_DT
							      ,YD_CARUD_WRK_CRN  AS YD_CARUD_WRK_CRN
							      ,YD_CAR_PROG_STAT AS YD_CAR_PROG_STAT
							      ,YD_TCAR_WRK_SEQ 
							   FROM TB_YD_TCARSCH
							 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
		    			 */
		    			
		    			rsResult = commDao.select(recInTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabSchSeEJB.getYdTcarsch", logId, methodNm, "크레인스케줄 호출조회");
		    			
		    			if(rsResult.size() != 1){
		    				jrRtn.setField("RTN_CD"	, "0");
							jrRtn.setField("RTN_MSG", "대차 스케줄 정보 이상.");	
							return jrRtn;
		    			}
		    			
		    			//rsResult.first();
		    			//JDTORecord outRec = rsResult.getRecord();
		    			/*
		    			 * 
							UPDATE TB_YD_TCARSCH
							      SET MODIFIER = :V_MODIFIER
							         ,MOD_DDTT = SYSDATE
							         ,YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
							         ,YD_CARLD_SCH_REQ_GP  = :V_YD_CARLD_SCH_REQ_GP
							         ,YD_CARLD_STOP_LOC    = :V_YD_CARLD_STOP_LOC
							         ,YD_CARUD_SCH_REQ_GP  = :V_YD_CARUD_SCH_REQ_GP
							         ,YD_CARUD_STOP_LOC    = :V_YD_CARUD_STOP_LOC
							         ,YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT
							  WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
		    			 */
		    			//intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
		    			intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabSchSeEJB.updYdTcarsch01", logId, methodNm, "대차스케줄 상차작업예약 INSERT");

		    			if(intRtnVal <= 0) {
			    			szMsg="["+szOperationName+"] 대차스케줄["+ydTcarSchId+"]에 상차작업예약["+ydWbookId+"], 상차도착 상태[2], 상차정지위치["+ydCarldStopLoc+"], 하차정지위치["+ydCarudStopLoc+"] 등록 시 오류발생 - 반환값 : " + intRtnVal;
			    			slabUtils.printLog(logId, szMsg, "SL:");
			    			jrRtn.setField("RTN_CD"	, "0");
							jrRtn.setField("RTN_MSG", szMsg);	
							return jrRtn;
		    			}
		    			
		    			if( !"".equals(ydWbookId) ){
		    				
			    			recInTemp = slabUtils.getParam(logId, methodNm, sModifier);
			    			recInTemp.setField("MSG_ID",    	"YDYDJ401");//YDYDJ503 >> YDYDJ401 로 대체
			    			recInTemp.setField("YD_SCH_CD", 	ydSchCd);
			    			
			    			//-------------------------------------------------------------------------------------
							//	크레인설비ID를 구하기 위해서 스케줄기준 조회
							//-------------------------------------------------------------------------------------
			    			szMsg="["+szOperationName+"] 크레인설비ID를 구하기 위해서 스케줄코드["+ydSchCd+"]로 스케줄기준 조회 시작";
			    			slabUtils.printLog(logId, szMsg, "SL:");
			    			
			    			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			    			//intRtnVal = ydSchRuleDao.getYdSchrule(recInTemp, rsResult, 0);
			    			/*
			    			 * 
								SELECT YD_SCH_CD                              AS YD_SCH_CD
								      ,REGISTER                               AS REGISTER
								      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
								      ,MODIFIER                               AS MODIFIER
								      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
								      ,DEL_YN                                 AS DEL_YN
								      ,YD_GP                                  AS YD_GP
								      ,YD_BAY_GP                              AS YD_BAY_GP
								      ,YD_SCH_RNG_CD                          AS YD_SCH_RNG_CD
								      ,YD_SCH_WHIO_GP                         AS YD_SCH_WHIO_GP
								      ,YD_SCH_DIV_GP                          AS YD_SCH_DIV_GP
								      ,YD_SCH_RULE_ACT_STAT                   AS YD_SCH_RULE_ACT_STAT
								      ,YD_WRK_CRN                             AS YD_WRK_CRN
								      ,YD_WRK_CRN_PRIOR                       AS YD_WRK_CRN_PRIOR
								      ,YD_ALT_CRN_YN                          AS YD_ALT_CRN_YN
								      ,YD_ALT_CRN                             AS YD_ALT_CRN
								      ,YD_ALT_CRN_PRIOR                       AS YD_ALT_CRN_PRIOR
								      ,CD_CONTENTS                            AS CD_CONTENTS
								      ,YD_SCH_PROH_EXN                        AS YD_SCH_PROH_EXN
								   FROM TB_YD_SCHRULE
								 WHERE YD_SCH_CD = replace( :V_YD_SCH_CD ,'HEKD05LM','HEDD05LM')
			    			 */
			    			rsResult = commDao.select(recInTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdSchrule"  , logId, szMethodName, "스케쥴기준관리_수정");
			    	    	
			    			if(rsResult.size() == 0) {
			        			szMsg="["+szOperationName+"] 크레인설비ID를 구하기 위해서 스케줄코드["+ydSchCd+"]로 스케줄기준 조회 시 오류발생 - 반환값 : " + rsResult.size();
			        			slabUtils.printLog(logId, szMsg, "SL:");
			        			jrRtn.setField("RTN_CD"	, "0");
								jrRtn.setField("RTN_MSG", szMsg);	
								return jrRtn;
			    			}
			    			
			    			szMsg="["+szOperationName+"] 크레인설비ID를 구하기 위해서 스케줄코드["+ydSchCd+"]로 스케줄기준 조회 완료";
			    			slabUtils.printLog(logId, szMsg, "SL:");
			    			
			    			rsResult.absolute(1);
			    			recOutTemp = JDTORecordFactory.getInstance().create();
			        		recOutTemp.setRecord(rsResult.getRecord());
			    			//-------------------------------------------------------------------------------------
			        		
			        		//-------------------------------------------------------------------------------------
			    			//	대차가 이미 도착한 상태이므로 크레인 스케줄 호출
			        		//-------------------------------------------------------------------------------------
			        		szMsg="["+szOperationName+"] 상차정지위치동["+ydBayGp+"]와 대차설비의 현재동["+ydTcarCurrBayGp+"]이 같은 경우는 대차가 이미 도착한 상태이므로 크레인 스케줄 호출 시작";
			    			slabUtils.printLog(logId, szMsg, "SL:");
			    			
			    			recInTemp.setField("YD_EQP_ID", slabUtils.paraRecChkNull(recOutTemp, "YD_WRK_CRN"));
			    			
			    			//ydDelegate.sendMsg(recInTemp);
			    			jrRtn = slabUtils.addSndData(jrRtn, recInTemp); 
			    			
			    			szMsg="["+szOperationName+"] 상차정지위치동["+ydBayGp+"]와 대차설비의 현재동["+ydTcarCurrBayGp+"]이 같은 경우는 대차가 이미 도착한 상태이므로 크레인 스케줄 호출 완료 - 공대차출발스케줄 종료";
			    			slabUtils.printLog(logId, szMsg, "SL:");
			    			
			    			//-------------------------------------------------------------------------------------
		    			}else{
		    				szMsg="["+szOperationName+"] 상차작업예약["+ydWbookId+"]이 존재하지 않으므로 크레인 스케줄 호출하지 않음";
			    			slabUtils.printLog(logId, szMsg, "SL:");
		    			}
		    			
		    		}else{
		    			
		    			//공대차이지만 다른동에 있을경우에는 대차스케줄 상차동을 현재동으로 등록한 후 공대차출발지시 등록 
		    			//-------------------------------------------------------------------------------------
		    			//	대차설비의 현재동과 대차상차작업예약의 상차정지위치가 다른 경우 공대차출발지시를 L2로 전송
		    			//-------------------------------------------------------------------------------------
		    			szMsg="["+szOperationName+"] 대차설비[" + sTcarEqpId + "]의 현재동["+ydTcarCurrBayGp+"]과 대차상차작업예약의 상차정지위치동["+ydBayGp+"]이 다른 경우 공대차출발지시를 L2로 전송";
		    			slabUtils.printLog(logId, szMsg, "SL:");
		    			
		    			szMsg="["+szOperationName+"] 상차정지위치동["+ydBayGp+"]와 대차설비[" + sTcarEqpId + "]의 현재동["+ydTcarCurrBayGp+"]이 다른 경우는 대차스케줄["+ydTcarSchId+"]에 상차작업예약["+ydWbookId+"], 공차대기 상태[0]로 등록 시작";
		    			slabUtils.printLog(logId, szMsg, "SL:");
		    			
		    			recInTemp.setField("YD_CAR_PROG_STAT", "0");	//공차대기 - 공차출발실적에서 상차출발로 변경함.

		    			//intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
		    			/*
		    			 * 
							UPDATE TB_YD_TCARSCH
							      SET MODIFIER = :V_MODIFIER
							         ,MOD_DDTT = SYSDATE
							         ,YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
							         ,YD_CARLD_SCH_REQ_GP  = :V_YD_CARLD_SCH_REQ_GP
							         ,YD_CARLD_STOP_LOC    = :V_YD_CARLD_STOP_LOC
							         ,YD_CARUD_SCH_REQ_GP  = :V_YD_CARUD_SCH_REQ_GP
							         ,YD_CARUD_STOP_LOC    = :V_YD_CARUD_STOP_LOC
							         ,YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT
							  WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
		    			 */
		    			//intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
		    			intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabSchSeEJB.updYdTcarsch01", logId, methodNm, "대차스케줄 상차작업예약 INSERT");

		    			if(intRtnVal <= 0) {
		    				szMsg="["+szOperationName+"] 상차정지위치동["+ydBayGp+"]와 대차설비의 현재동["+ydTcarCurrBayGp+"]이 다른 경우는 대차스케줄["+ydTcarSchId+"]에 상차작업예약["+ydWbookId+"], 공차대기 상태[0]로 등록 시 오류발생 - 반환값 : " + intRtnVal;
			    			slabUtils.printLog(logId, szMsg, "SL:");
			    			jrRtn.setField("RTN_CD"	, "0");
							jrRtn.setField("RTN_MSG", szMsg);	
							return jrRtn;
		    			}
		    			
		    			szMsg="["+szOperationName+"] 상차정지위치동["+ydBayGp+"]와 대차설비의 현재동["+ydTcarCurrBayGp+"]이 다른 경우는 대차스케줄["+ydTcarSchId+"]에 상차작업예약["+ydWbookId+"], 공차대기 상태[0]로 등록 완료 - 반환값 : " + intRtnVal;
		    			slabUtils.printLog(logId, szMsg, "SL:");
		    			//-------------------------------------------------------------------------------------
		    			
		    			//-------------------------------------------------------------------------------------
						//	공대차출발지시를 한다.야드구분, 야드스케줄코드, 대차스케줄ID
		    			//-------------------------------------------------------------------------------------
		    			recInTemp = slabUtils.getParam(logId, methodNm, sModifier);
			    		recInTemp.setField("MSG_ID", 				"YDY3L006");
			    		recInTemp.setField("YD_GP", 				"D");
			    		recInTemp.setField("YD_SCH_CD", 			"");
			    		recInTemp.setField("YD_TCAR_SCH_ID", 		ydTcarSchId);
			    		
			    		jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY3L006", recInTemp));
						szMsg="["+szOperationName+"] 상차정지위치동["+ydBayGp+"]으로 공대차 출발지시[상차출발] 전송 완료";
						slabUtils.printLog(logId, szMsg, "SL:");
						//-------------------------------------------------------------------------------------
		    		}

	    		}else if ("".equals(ydCarldWrkBookId) && !"".equals(ydToBay.trim())){
	    			
	    			//-------------------------------------------------------------------------------------
	    			//	대차스케줄의 상차작업예약이 등록되어 있지 않고 사용자가 지정한 목표동의 값이 존재하는 경우
	    			//-------------------------------------------------------------------------------------
	    			szMsg="["+szOperationName+"] 대차스케줄["+ydTcarSchId+"]의 상차작업예약이 등록되어 있지 않고 사용자가 지정한 목표동["+ydToBay+"]이 존재하는 경우는 대차스케줄["+ydTcarSchId+"]에 공차대기 상태[0]로 등록 후 공대차출발 지시 전송";
	    			slabUtils.printLog(logId, szMsg, "SL:");
	    			
	    			recTcar.setField("YD_CAR_PROG_STAT", 			"0");					//공차대기
	    			recTcar.setField("YD_CARLD_STOP_LOC", 			"D" + ydToBay + sTcarEqpId.substring(2,6)); //사용자지정위치
	    			recTcar.setField("YD_CARLD_WRK_BOOK_ID", 		ydCarldWrkBookId);	
	    			recTcar.setField("YD_TCAR_SCH_ID", 			    ydTcarSchId);	
	    			
		    		//intRtnVal = ydTcarSchDao.updYdTcarsch(recTcar, 0);
	    			/*
	    			 * UPDATE TB_YD_TCARSCH
					      SET 
					            MODIFIER = :V_MODIFIER
					           ,MOD_DDTT = SYSDATE
					           ,YD_CARLD_STOP_LOC = :V_YD_CARLD_STOP_LOC
					           ,YD_CARUD_WRK_BOOK_ID = :V_YD_CARUD_WRK_BOOK_ID
					  WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
	    			 */
	    			intRtnVal = commDao.update(recTcar, "com.inisteel.cim.yd.pslabyd.dao.PSlabSchSeEJB.updYdTcarschAll", logId, methodNm, "대차스케줄 상차작업예약 수정");

	    			if(intRtnVal <= 0) {
	    				szMsg="["+szOperationName+"] 대차스케줄["+ydTcarSchId+"]의 상차작업예약이 등록되어 있지 않고 사용자가 지정한 목표동["+ydToBay+"]이 존재하는 경우는 대차스케줄["+ydTcarSchId+"]에 공차대기 상태[0]로 등록 시 오류발생 - 반환값 : " + intRtnVal;
		    			slabUtils.printLog(logId, szMsg, "SL:");
		    			jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", szMsg);	
						return jrRtn;
	    			}
	    			//-------------------------------------------------------------------------------------
	    			
	    			//-------------------------------------------------------------------------------------
					//공대차출발지시를 한다.야드구분, 야드스케줄코드, 대차스케줄ID
	    			//-------------------------------------------------------------------------------------
	    			recInTemp = slabUtils.getParam(logId, methodNm, sModifier);
		    		recInTemp.setField("MSG_ID", 			"YDY3L006");
		    		recInTemp.setField("YD_GP", 			"D");
		    		recInTemp.setField("YD_SCH_CD", 		"");
		    		recInTemp.setField("YD_TCAR_SCH_ID", 	ydTcarSchId);
		    		
		    		jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY3L006", recInTemp));
		    		szMsg="["+szOperationName+"] 사용자 지정동["+ydToBay+"]으로 공대차 출발지시[공차대기] 전송 완료";
					slabUtils.printLog(logId, szMsg, "SL:");
					//-------------------------------------------------------------------------------------
	    		}

	    	}else{
	    		
	    		//-------------------------------------------------------------------------------------
	    		//	대차스케줄이 없는 경우!!
	    		//-------------------------------------------------------------------------------------
	    		String szYD_LD_UD_GP			= slabUtils.paraRecChkNull(msgRecord, "YD_LD_UD_GP");
	    		
	    		szMsg="["+szOperationName+"] 대차스케줄이 존재하지 않는 경우 - YD_LD_UD_GP["+szYD_LD_UD_GP+"], YD_WBOOK_ID["+ydWbookId+"]";
				slabUtils.printLog(logId, szMsg, "SL:");
	    		
	    		if( szYD_LD_UD_GP.equals("U") ) {
	    			szMsg="["+szOperationName+"] 파라미터로 전달된 권상/권하실적처리 호출구분["+szYD_LD_UD_GP+"]이 권상실적처리인 경우 - 하차작업이 끝나고 다음 작업예약을 찾는경우";
					slabUtils.printLog(logId, szMsg, "SL:");
	    		}else{
	    			szMsg="["+szOperationName+"] 파라미터로 전달된 권상/권하실적처리 호출구분["+szYD_LD_UD_GP+"]이 없는 경우 - 권상실적처리 시";
					slabUtils.printLog(logId, szMsg, "SL:");
	    		}
	    		
	    		//작업계획대차설정
		    	msgRecord.setField("YD_WRK_PLAN_TCAR", sTcarEqpId);
		    	
		    	intRtnVal		= 0;

		    	if( "".equals(ydWbookId) ) {
		    		
					szMsg="intRtnVal = " + rsResult.size();
					slabUtils.printLog(logId, szMsg, "SL:");
					
					szMsg="["+szOperationName+"] 해당 대차설비[" + sTcarEqpId + "]의 작업예약이 등록되어 있지 않으므로 대차스케줄 생성 후 현재동과 홈동이 다르면 공대차출발지시를 전송한다.";
	    			slabUtils.printLog(logId, szMsg, "SL:");
					
					/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					 * 대차 하차 후 다음 대차작업을 찾을때 다음 대차 작업이 없는 경우 현재위치에 대차 스케줄을 생성한다.
					 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		    		
		    		//대차스케줄을 생성한다.
		    		//대차스케줄1개를 생성하고 해당 작업예약id를 상차작업예약id에 등록한다.
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YD_TCAR_SCH_ID", "1");
			    	rsResult1 = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    	
			    	//intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult1, 5);
			    	/*
			    	 * SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') ||  LPAD(YD_TCARSCH_SEQ.nextval,6,'0') AS YD_TCAR_SCH_ID
						  FROM DUAL                                                                                      
						 WHERE '1' = :V_YD_TCAR_SCH_ID
			    	 */
			    	rsResult1 = commDao.select(recInTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabSchSeEJB.getYdTcarschId"  , logId, szMethodName, "YD_TCARSCH_SEQ.nextval 조회");
	    	    	
			    	rsResult1.absolute(1);
			    	recOutTemp = JDTORecordFactory.getInstance().create();
			    	recOutTemp.setRecord(rsResult1.getRecord());
			    	ydTcarSchId = slabUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
			    	
			    	recInTemp = slabUtils.getParam(logId, methodNm, sModifier);
			    	//대차스케줄생성
			    	recInTemp.setField("YD_TCAR_SCH_ID", 				ydTcarSchId);
			    	recInTemp.setField("REGISTER", 						sModifier);
		    		
			    	//현재위치가 상차정지위치로등록한다.
		    		
			    	boolean bSEND_START		= false;
			    	
			    	if(ydToBay.equals("")){
			    		
			    		//대차상차정지위치-- 상차작업예약이 없고 to동이 지정되어 오지 않은경우에는 홈동으로 보내기위해...
			    		szMsg="["+szOperationName+"] 지정목표동이 존재하지 않으므로 해당 대차설비[" + sTcarEqpId + "]의 대차스케줄["+ydTcarSchId+"]의 상차정지위치를 홈동["+ydHomeBayGp+"], 공차대기[0]으로 지정한다.";
		    			slabUtils.printLog(logId, szMsg, "SL:");
				    					    	
				    	if( sUsageYn.equals("Y")) {				//사용
			    	    	if( sWorkGp.equals("D") ) {			//직상차
			    	    		szMsg="["+szOperationName+"] 지정목표동이 존재하지 않으므로 해당 대차설비[" + sTcarEqpId + "]의 대차스케줄["+ydTcarSchId+"]의 상차정지위치를 홈동["+ydHomeBayGp+"]이 아닌 Rule의 직상차 상차동["+ydCarldBayGp+"]으로 설정한다.";
				    			slabUtils.printLog(logId, szMsg, "SL:");
			    	    		ydHomeBayGp			= ydCarldBayGp;
			    	    	}
			    		}
			    		
				    	recInTemp.setField("YD_CARLD_STOP_LOC", 			sTcarEqpId.substring(0,1) + ydHomeBayGp + sTcarEqpId.substring(2));
				    	
				    	if( !ydAimBayGp.equals("") ) {
				    		recInTemp.setField("YD_CARUD_STOP_LOC", 			sTcarEqpId.substring(0,1) + ydAimBayGp + sTcarEqpId.substring(2));		//하차정지위치
				    	}
				    	
				    	//------------------------------------------------
				    	recInTemp.setField("YD_CAR_PROG_STAT", 			"0");
				    	//------------------------------------------------
				    	
				    	if( !ydHomeBayGp.equals(ydTcarCurrBayGp) ) {
				    		
				    		bSEND_START			= true;
				    		
				    		szMsg="["+szOperationName+"] 해당 대차설비[" + sTcarEqpId + "]의 대차스케줄["+ydTcarSchId+"]의 상차정지위치를 홈동["+ydHomeBayGp+"]과 현재동["+ydTcarCurrBayGp+"]이 다르므로 공대차출발지시 전송한다.";
			    			slabUtils.printLog(logId, szMsg, "SL:");
				    	}
			    	}else{
				    	//대차상차정지위치-- to동이 지정되어 왔을 경우에는 To동에 상차위치로 잡고 대차출발지시를 내린다.
			    		szMsg="["+szOperationName+"] 지정목표동["+ydToBay+"]이 존재하므로 해당 대차설비[" + sTcarEqpId + "]의 대차스케줄["+ydTcarSchId+"]의 상차정지위치를 지정목표동["+ydToBay+"], 공차대기[0]으로 지정한다.";
		    			slabUtils.printLog(logId, szMsg, "SL:");
				    	recInTemp.setField("YD_CARLD_STOP_LOC", sTcarEqpId.substring(0,1) + ydToBay + sTcarEqpId.substring(2));
				    	
				    	//------------------------------------------------
				    	recInTemp.setField("YD_CAR_PROG_STAT", "0");	
				    	//------------------------------------------------
				    	
				    	if( !ydToBay.equals(ydTcarCurrBayGp) ) {
				    		bSEND_START			= true;
				    		
				    		szMsg="["+szOperationName+"] 해당 대차설비[" + sTcarEqpId + "]의 대차스케줄["+ydTcarSchId+"]의 상차정지위치를 지정목표동["+ydToBay+"]과 현재동["+ydTcarCurrBayGp+"]이 다르므로 공대차출발지시 전송한다.";
			    			slabUtils.printLog(logId, szMsg, "SL:");
				    	}
				    	
			    	}
			    	
			    	recInTemp.setField("YD_CARLD_SCH_REQ_GP", "6");
					recInTemp.setField("YD_CARUD_SCH_REQ_GP", "3");
					
			    	//설비작업상태값( 공차로 등록 )
			    	recInTemp.setField("YD_EQP_WRK_STAT", "U");
		    		
			    	//야드설비ID
			    	recInTemp.setField("YD_EQP_ID", sTcarEqpId);
			    	//intRtnVal = ydTcarSchDao.insYdTcarsch(recInTemp);
			    	/*
			    	 * INSERT INTO TB_YD_TCARSCH
						            (
						             YD_TCAR_SCH_ID
						            ,REGISTER
						            ,REG_DDTT
						            ,YD_EQP_ID
						            ,YD_EQP_WRK_STAT
						            ,YD_WRK_PROG_STAT
						            ,YD_EQP_WRK_SH
						            ,YD_EQP_WRK_WT
						            ,YD_STK_BED_TP
						            ,YD_CARLD_LEV_LOC
						            ,YD_CARLD_LEV_DT
						            ,YD_CARLD_ARR_DT
						            ,YD_CARLD_WRK_BOOK_ID
						            ,YD_CARLD_SCH_REQ_GP
						            ,YD_CARLD_STOP_LOC
						            ,YD_CARLD_ST_DT
						            ,YD_CARLD_CMPL_DT
						            ,YD_CARLD_WRK_ACT_GP
						            ,YD_CARLD_WRK_CRN
						            ,YD_CARUD_WRK_ACT_GP
						            ,YD_CARUD_LEV_DT
						            ,YD_CARUD_ARR_DT
						            ,YD_CARUD_WRK_BOOK_ID
						            ,YD_CARUD_SCH_REQ_GP
						            ,YD_CARUD_STOP_LOC
						            ,YD_CARUD_ST_DT
						            ,YD_CARUD_CMPL_DT
						            ,YD_CARUD_WRK_CRN
						            ,YD_CAR_PROG_STAT
						            ,YD_TCAR_WRK_SEQ
						            )
						VALUES (
						            :V_YD_TCAR_SCH_ID
						            ,:V_REGISTER
						            ,SYSDATE
						            ,:V_YD_EQP_ID
						            ,:V_YD_EQP_WRK_STAT
						            ,:V_YD_WRK_PROG_STAT
						            ,:V_YD_EQP_WRK_SH
						            ,:V_YD_EQP_WRK_WT
						            ,:V_YD_STK_BED_TP
						            ,:V_YD_CARLD_LEV_LOC
						            ,:V_YD_CARLD_LEV_DT
						            ,:V_YD_CARLD_ARR_DT
						            ,:V_YD_CARLD_WRK_BOOK_ID
						            ,:V_YD_CARLD_SCH_REQ_GP
						            ,:V_YD_CARLD_STOP_LOC
						            ,:V_YD_CARLD_ST_DT
						            ,:V_YD_CARLD_CMPL_DT
						            ,:V_YD_CARLD_WRK_ACT_GP
						            ,:V_YD_CARLD_WRK_CRN
						            ,:V_YD_CARUD_WRK_ACT_GP
						            ,:V_YD_CARUD_LEV_DT
						            ,:V_YD_CARUD_ARR_DT
						            ,:V_YD_CARUD_WRK_BOOK_ID
						            ,:V_YD_CARUD_SCH_REQ_GP
						            ,:V_YD_CARUD_STOP_LOC
						            ,:V_YD_CARUD_ST_DT
						            ,:V_YD_CARUD_CMPL_DT
						            ,:V_YD_CARUD_WRK_CRN
						            ,:V_YD_CAR_PROG_STAT
						            ,:V_YD_TCAR_WRK_SEQ
						            )

			    	 */
			    	intRtnVal = commDao.insert(recInTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabSchSeEJB.insYdTcarsch", logId, methodNm, "대차스케줄 상차작업예약 수정");

		    		if(intRtnVal < 1) {
						szMsg="["+szOperationName+"] parameter error[1]";
						slabUtils.printLog(logId, szMsg, "SL:");
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", szMsg);	
						return jrRtn;
		    		}
		    		
					szMsg="["+szOperationName+"] 대차하차 후 새로운 대차 작업이 없어 대차스케줄만 생성!!";
					slabUtils.printLog(logId, szMsg, "SL:");
					
					//홈동과 현재동이 틀릴경우 공대차 출발지시를 내린다.
					if( bSEND_START ) {
						//공대차출발지시를 한다.야드구분, 야드스케줄코드, 대차스케줄ID
			    		recInTemp = JDTORecordFactory.getInstance().create();
			    		recInTemp.setField("MSG_ID", 			"YDY3L006");
			    		recInTemp.setField("YD_GP", 			"D");
			    		recInTemp.setField("YD_SCH_CD", 		"");
			    		recInTemp.setField("YD_TCAR_SCH_ID", 	ydTcarSchId);
			    		
			    		jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY3L006", recInTemp));
						szMsg="["+szOperationName+"] 공대차 출발지시 전송 완료[1]";
						slabUtils.printLog(logId, szMsg, "SL:");
					}
					
		    	}else{
			    	//----------------------------------------------------------------------------------------
			    	//	대차 작업예약이 존재하는 경우
			    	//----------------------------------------------------------------------------------------
			    	szMsg="["+szOperationName+"] 해당 대차설비[" + sTcarEqpId + "]의 작업예약["+ydWbookId+"]이 등록되어 있으므로 대차스케줄 생성 후 현재동과 목표동이 다르면 공대차출발지시를 전송한다.";
	    			slabUtils.printLog(logId, szMsg, "SL:");
			    	
			    	//대차스케줄1개를 생성하고 해당 작업예약id를 상차작업예약id에 등록한다.
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YD_TCAR_SCH_ID", "1");
			    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    	//intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 5);
			    	/*
			    	 * SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') ||  LPAD(YD_TCARSCH_SEQ.nextval,6,'0') AS YD_TCAR_SCH_ID
						  FROM DUAL                                                                                      
						 WHERE '1' = :V_YD_TCAR_SCH_ID
			    	 */
			    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabSchSeEJB.getYdTcarschId"  , logId, szMethodName, "YD_TCARSCH_SEQ.nextval 조회");
	    	    	
			    	
			    	rsResult.absolute(1);
			    	recOutTemp = JDTORecordFactory.getInstance().create();
			    	recOutTemp.setRecord(rsResult.getRecord());
			    	ydTcarSchId = slabUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
			    	
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	//대차스케줄생성
			    	recInTemp.setField("YD_TCAR_SCH_ID", 				ydTcarSchId);
			    	recInTemp.setField("YD_CARLD_WRK_BOOK_ID", 			ydWbookId);
			    	recInTemp.setField("REGISTER", 						sModifier);
			    	
			    	//대차상차정지위치, 하차정지위치
			    	recInTemp.setField("YD_CARLD_STOP_LOC", 			"D" + ydBayGp + ydWrkPlanTcar.substring(2,6));
			    	if( !ydAimBayGp.equals("") ) {
			    		recInTemp.setField("YD_CARUD_STOP_LOC", 		"D" + ydAimBayGp + ydWrkPlanTcar.substring(2,6));
			    	}
			    	recInTemp.setField("YD_CARLD_SCH_REQ_GP", 			"6");
					recInTemp.setField("YD_CARUD_SCH_REQ_GP", 			"3");
					
					if(ydTcarCurrBayGp.equals(ydBayGp)) {
						//현재동과 상차동이 같으므로 상차도착 상태로 등록
						recInTemp.setField("YD_CAR_PROG_STAT", 				"2");
					}else{
						//현재동과 상차동이 다르므로 공차대기 상태로 등록
						recInTemp.setField("YD_CAR_PROG_STAT", 				"0");
					}
					//설비작업상태값( 공차로 등록 )
			    	recInTemp.setField("YD_EQP_WRK_STAT", 				"U");
			    	
			    	//야드설비ID
			    	recInTemp.setField("YD_EQP_ID", ydWrkPlanTcar);
			    	//intRtnVal = ydTcarSchDao.insYdTcarsch(recInTemp);
			    	/*
			    	 * INSERT INTO TB_YD_TCARSCH
						            (
						             YD_TCAR_SCH_ID
						            ,REGISTER
						            ,REG_DDTT
						            ,YD_EQP_ID
						            ,YD_EQP_WRK_STAT
						            ,YD_WRK_PROG_STAT
						            ,YD_EQP_WRK_SH
						            ,YD_EQP_WRK_WT
						            ,YD_STK_BED_TP
						            ,YD_CARLD_LEV_LOC
						            ,YD_CARLD_LEV_DT
						            ,YD_CARLD_ARR_DT
						            ,YD_CARLD_WRK_BOOK_ID
						            ,YD_CARLD_SCH_REQ_GP
						            ,YD_CARLD_STOP_LOC
						            ,YD_CARLD_ST_DT
						            ,YD_CARLD_CMPL_DT
						            ,YD_CARLD_WRK_ACT_GP
						            ,YD_CARLD_WRK_CRN
						            ,YD_CARUD_WRK_ACT_GP
						            ,YD_CARUD_LEV_DT
						            ,YD_CARUD_ARR_DT
						            ,YD_CARUD_WRK_BOOK_ID
						            ,YD_CARUD_SCH_REQ_GP
						            ,YD_CARUD_STOP_LOC
						            ,YD_CARUD_ST_DT
						            ,YD_CARUD_CMPL_DT
						            ,YD_CARUD_WRK_CRN
						            ,YD_CAR_PROG_STAT
						            ,YD_TCAR_WRK_SEQ
						            )
						VALUES (
						            :V_YD_TCAR_SCH_ID
						            ,:V_REGISTER
						            ,SYSDATE
						            ,:V_YD_EQP_ID
						            ,:V_YD_EQP_WRK_STAT
						            ,:V_YD_WRK_PROG_STAT
						            ,:V_YD_EQP_WRK_SH
						            ,:V_YD_EQP_WRK_WT
						            ,:V_YD_STK_BED_TP
						            ,:V_YD_CARLD_LEV_LOC
						            ,:V_YD_CARLD_LEV_DT
						            ,:V_YD_CARLD_ARR_DT
						            ,:V_YD_CARLD_WRK_BOOK_ID
						            ,:V_YD_CARLD_SCH_REQ_GP
						            ,:V_YD_CARLD_STOP_LOC
						            ,:V_YD_CARLD_ST_DT
						            ,:V_YD_CARLD_CMPL_DT
						            ,:V_YD_CARLD_WRK_ACT_GP
						            ,:V_YD_CARLD_WRK_CRN
						            ,:V_YD_CARUD_WRK_ACT_GP
						            ,:V_YD_CARUD_LEV_DT
						            ,:V_YD_CARUD_ARR_DT
						            ,:V_YD_CARUD_WRK_BOOK_ID
						            ,:V_YD_CARUD_SCH_REQ_GP
						            ,:V_YD_CARUD_STOP_LOC
						            ,:V_YD_CARUD_ST_DT
						            ,:V_YD_CARUD_CMPL_DT
						            ,:V_YD_CARUD_WRK_CRN
						            ,:V_YD_CAR_PROG_STAT
						            ,:V_YD_TCAR_WRK_SEQ
						            )

			    	 */
			    	intRtnVal = commDao.insert(recInTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabSchSeEJB.insYdTcarsch", logId, methodNm, "대차스케줄 상차작업예약 수정");

		    		if(intRtnVal < 1) {
						szMsg="["+szOperationName+"] parameter error[2]";
						slabUtils.printLog(logId, szMsg, "SL:");
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", szMsg);	
						return jrRtn;
		    		}
		    		
					szMsg="["+szOperationName+"] ydTcarCurrBayGp : " + ydTcarCurrBayGp + " ,  ydBayGp : " + ydBayGp;
					slabUtils.printLog(logId, szMsg, "SL:");
		    		
		    		//현재 대차동과 작업예약의 동과 일치하면 스케줄 호출 비일치시 대차출발지시를 내린다.
		    		if(ydTcarCurrBayGp.equals(ydBayGp)) {
		    			
		    			recInTemp = JDTORecordFactory.getInstance().create();
		    			recInTemp.setField("MSG_ID",    "YDYDJ503");
		    			recInTemp.setField("YD_SCH_CD", ydSchCd);
		    			
		    			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		    			//intRtnVal = ydSchRuleDao.getYdSchrule(recInTemp, rsResult, 0);
		    			/*
		    			 * 
							SELECT YD_SCH_CD                              AS YD_SCH_CD
							      ,REGISTER                               AS REGISTER
							      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
							      ,MODIFIER                               AS MODIFIER
							      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
							      ,DEL_YN                                 AS DEL_YN
							      ,YD_GP                                  AS YD_GP
							      ,YD_BAY_GP                              AS YD_BAY_GP
							      ,YD_SCH_RNG_CD                          AS YD_SCH_RNG_CD
							      ,YD_SCH_WHIO_GP                         AS YD_SCH_WHIO_GP
							      ,YD_SCH_DIV_GP                          AS YD_SCH_DIV_GP
							      ,YD_SCH_RULE_ACT_STAT                   AS YD_SCH_RULE_ACT_STAT
							      ,YD_WRK_CRN                             AS YD_WRK_CRN
							      ,YD_WRK_CRN_PRIOR                       AS YD_WRK_CRN_PRIOR
							      ,YD_ALT_CRN_YN                          AS YD_ALT_CRN_YN
							      ,YD_ALT_CRN                             AS YD_ALT_CRN
							      ,YD_ALT_CRN_PRIOR                       AS YD_ALT_CRN_PRIOR
							      ,CD_CONTENTS                            AS CD_CONTENTS
							      ,YD_SCH_PROH_EXN                        AS YD_SCH_PROH_EXN
							   FROM TB_YD_SCHRULE
							 WHERE YD_SCH_CD = replace( :V_YD_SCH_CD ,'HEKD05LM','HEDD05LM')
		    			 */
		    			rsResult = commDao.select(recInTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdSchrule"  , logId, szMethodName, "스케쥴기준관리_수정");
		    	    	
		    			if(intRtnVal <= 0) {
		        			szMsg="["+szOperationName+"] 스케줄 기준 조회중 값이없거나 Error Code : " + intRtnVal;
		        			slabUtils.printLog(logId, szMsg, "SL:");
		    			}
		    			
		    			rsResult.absolute(1);
		    			recOutTemp = JDTORecordFactory.getInstance().create();
		        		recOutTemp.setRecord(rsResult.getRecord());
		    			
		        		//------------------------------------------------
		        		//	대차의 차량진행상태를 상차도착으로 변경 필요.
		        		//------------------------------------------------
		        		
		    			//크레인 스케줄 호출
		    			szMsg="["+szOperationName+"] 대차가 이미 도착해있으므로 크레인 스케줄 호출";
		    			slabUtils.printLog(logId, szMsg, "SL:");
		    			recInTemp.setField("YD_EQP_ID", slabUtils.paraRecChkNull(recOutTemp, "YD_WRK_CRN"));
		    			
		    			//ydDelegate.sendMsg(recInTemp);
		    			jrRtn = slabUtils.addSndData(jrRtn, recInTemp); 
		    			
		    			szMsg="["+szOperationName+"] 대차 상차 스케줄("+szMethodName+") 완료";
		    			slabUtils.printLog(logId, szMsg, "SL:");
		    			
		    		}else{
			    	
				    	//공대차출발지시를 한다.야드구분, 야드스케줄코드, 대차스케줄ID
			    		recInTemp = JDTORecordFactory.getInstance().create();
			    		recInTemp.setField("MSG_ID", "YDY3L006");
			    		recInTemp.setField("YD_GP", ydGp);
			    		recInTemp.setField("YD_SCH_CD", ydSchCd);
			    		recInTemp.setField("YD_TCAR_SCH_ID", ydTcarSchId);
			    		
			    		
			    		jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY3L006", recInTemp));
						szMsg="["+szOperationName+"] 공대차 출발지시 전송 완료[2]";
						slabUtils.printLog(logId, szMsg, "SL:");
		    		}
		    	}
	    	}
	    	
	    	jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "");	
			return jrRtn;
			
		}catch(DAOException e) {
			throw e;
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}

}
